#!/usr/bin/env python3
import argparse
import hashlib
import json
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', type=Path, default=Path(__file__).resolve().parents[1])
args = parser.parse_args()
root = args.root.resolve()
errors = []

def need(condition, message):
    if not condition:
        errors.append(message)

def read(rel):
    path = root / rel
    need(path.is_file(), f'missing file: {rel}')
    return path.read_text(encoding='utf-8') if path.is_file() else ''

app_gradle = read('app/build.gradle.kts')
root_gradle = read('build.gradle.kts')
main = read('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
service = read('app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt')
manifest = read('app/src/main/AndroidManifest.xml')
wrapper = read('gradle/wrapper/gradle-wrapper.properties')
rules = read('rules/PROJECT_RULES.md')
audit = read('rules/2.0.1_SESSION_BASED_COLLECTION_AUDIT.md')

need('versionCode = 2001' in app_gradle and 'versionName = "2.0.1"' in app_gradle, '2.0.1 release identity missing')
need('com.android.application") version "9.4.1"' in root_gradle, 'AGP 9.4.1 drifted')
need('gradle-9.7.1-bin.zip' in wrapper, 'Gradle wrapper drifted from 9.7.1')
need('compileSdk = 37' in app_gradle and 'targetSdk = 37' in app_gradle, 'compile/target SDK drifted')
need('android:usesCleartextTraffic="false"' in manifest, 'cleartext transport protection missing')
need('android:process=":vulkan_probe"' in manifest and 'android:exported="false"' in manifest, 'isolated probe service manifest contract drifted')
need('// ' not in main and '/*' not in main and '// ' not in service and '/*' not in service, 'source-code comment rule violated in changed Kotlin files')

main_tokens = [
    'private suspend fun runIsolatedProbeSession(',
    'ProbeSessionBatchResult',
    'runIsolatedProbeSession(newGroups, modeSnapshot, BACKGROUND_COLLECTION_BUDGET_MS)',
    '.putStringArrayListExtra(VulkanProbeService.EXTRA_QUERY_GROUPS, ArrayList(segmentGroups))',
    '.putExtra(VulkanProbeService.EXTRA_QUERY_TIMEOUTS_MS, timeouts)',
    '.putExtra(VulkanProbeService.EXTRA_SESSION_DIR, sessionDir.absolutePath)',
    'if (segmentGroups[index] in ISOLATED_ADVANCED_GROUPS.keys) 30_000L else 12_000L',
    'val sessionDir = File(cacheDir, "vulkan_session_${java.util.UUID.randomUUID()}")',
    'private const val BACKGROUND_SESSION_GROUP_LIMIT = 16',
    'val segmentGroups = groups.drop(offset).take(BACKGROUND_SESSION_GROUP_LIMIT)',
    'val maxProbeResultBytes = 64L * 1024L * 1024L',
    'strictProbeTerminalCandidate(it, false)',
    'File(resultFile.path + ".crash")',
    'File(resultFile.path + ".timeout")',
    'offset += index + 1',
    'sessionDir.deleteRecursively()',
    'stopSessionProcess()',
    'session/background_process_launches',
    'session/background_host_total',
    'ExpressiveMetric("Background process launches", it.toString())',
]
for token in main_tokens:
    need(token in main, f'MainActivity session collector contract missing: {token}')

service_tokens = [
    'const val EXTRA_QUERY_GROUPS = "query_groups"',
    'const val EXTRA_QUERY_TIMEOUTS_MS = "query_timeouts_ms"',
    'const val EXTRA_SESSION_DIR = "session_dir"',
    'groups.size !in 1..16',
    'timeouts.size != groups.size',
    "it.isBlank() || it.length > 256 || '\\u0000' in it",
    'timeouts.any { it !in 1_000L..60_000L }',
    '!sessionDir.path.startsWith(cacheRoot.path + File.separator)',
    'File(sessionDir, "%03d.json".format(java.util.Locale.US, index))',
    'writeTelemetry(startedFile.path, group)',
    'writeTelemetry(timeoutFile.path, "timeout")',
    'mainHandler.postDelayed(hardTimeoutWatchdog, timeouts[index] + 100L)',
    'collectVulkanQueryData(group, mode, icd, bundle, hook, resultFile.path)',
    'validateTerminalResult(resultFile.path, group)',
    'writeResult(terminalFile.path, "done")',
    'writeResult(sessionTerminal.path, "done")',
    'System.loadLibrary("vulkanscope")',
]
for token in service_tokens:
    need(token in service, f'VulkanProbeService session contract missing: {token}')

session_block = service.split('private fun startQuerySession(', 1)[1].split('private fun elapsedProbeMillis', 1)[0] if 'private fun startQuerySession(' in service else ''
need(session_block.count('System.loadLibrary("vulkanscope")') == 1, 'session must load libvulkanscope once per bounded service session')
need(session_block.count('collectVulkanQueryData(') == 1, 'session query dispatch must use the single existing JNI query entry point')
need('collectVulkanData(' not in session_block and 'collectVulkanSurfaceData(' not in session_block, 'session path must not absorb base or Surface collection')
need('runIsolatedProbe(group, modeSnapshot)' in main, 'one-shot ad-hoc probe path was removed')
session_main_block = main.split('private suspend fun runIsolatedProbeSession(', 1)[1].split('private fun requestQueryGroup', 1)[0] if 'private suspend fun runIsolatedProbeSession(' in main else ''
need('withContext(Dispatchers.IO) {' in session_main_block, 'session file/process orchestration must stay off the UI thread')
need('ExpressiveMetric("Query groups measured", probeTotals.size.toString())' in main and 'ExpressiveMetric("Slowest query group"' in main, 'session timing UI still mislabels query groups as dedicated processes')
need('runServiceProbe("surface"' in main and 'runServiceProbe("base"' in main, 'base/Surface one-shot isolation path drifted')
need('The 2.0.0 measured background bottleneck must be addressed by session-level isolated collection' in rules, 'PROJECT_RULES 2.0.1 performance boundary missing')
need('Completed groups are consumed and retained as validated evidence while the session is running' in audit, 'session resource-bounding audit requirement missing')

contract_path = root / 'tests/golden/2.0.1_session_immutable_contract.json'
need(contract_path.is_file(), '2.0.1 immutable regression contract missing')
if contract_path.is_file():
    contract = json.loads(contract_path.read_text(encoding='utf-8'))
    for rel, expected in contract.get('immutableSha256', {}).items():
        path = root / rel
        if not path.is_file():
            errors.append(f'immutable predecessor file missing: {rel}')
            continue
        actual = hashlib.sha256(path.read_bytes()).hexdigest()
        if actual != expected:
            errors.append(f'immutable predecessor byte drift: {rel}')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 2.0.1 session-based isolated collection contract')
