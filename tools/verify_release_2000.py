#!/usr/bin/env python3
import argparse
import hashlib
import json
import re
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
advanced = read('app/src/main/java/com/efishell/vulkanscope/AdvancedAnalysis.kt')
rules = read('rules/PROJECT_RULES.md')
audit = read('rules/2.0.0_COLLECTION_TIMING_INSTRUMENTATION_AUDIT.md')
manifest = read('app/src/main/AndroidManifest.xml')
wrapper = read('gradle/wrapper/gradle-wrapper.properties')

need('versionCode = 2000' in app_gradle and 'versionName = "2.0.0"' in app_gradle, '2.0.0 release identity missing')
need('com.android.application") version "9.4.1"' in root_gradle, 'AGP 9.4.1 drifted')
need('gradle-9.7.1-bin.zip' in wrapper, 'Gradle wrapper drifted from 9.7.1')
need('compileSdk = 37' in app_gradle and 'targetSdk = 37' in app_gradle, 'compile/target SDK drifted')
need('android:usesCleartextTraffic="false"' in manifest, 'cleartext transport protection missing')
need('// ' not in main and '/*' not in main and '// ' not in service and '/*' not in service and '// ' not in advanced and '/*' not in advanced, 'source-code comment rule violated in changed Kotlin files')

main_tokens = [
    'private fun elapsedMillis(startNanos: Long)',
    'startupGateDelayMs',
    '"startup/gate_delay"',
    '"collection/base_total"',
    '"collection/enrichment_total"',
    '"collection/background_total"',
    '"collection/total"',
    '"base/attempt_${attemptNumber}/probe"',
    '"base/attempt_${attemptNumber}/decode"',
    '"enrichment/metadata"',
    '"enrichment/surface"',
    '"probe_total/$group"',
    'VulkanProbeService.EXTRA_TIMING_PATH',
    'File(resultFile.absolutePath + ".timing")',
    'timingFile.length() !in 1L..16_384L',
    '"service/$group/host_total"',
    '"service/$group/quiescence"',
    '"service/$group/dispatch_to_worker"',
    '"service/$group/library_load"',
    '"service/$group/native_call"',
    '"service/$group/terminal_validation"',
    '"service/$group/pre_terminal"',
    'val timingsMs: Map<String, Long>',
    'ExpressiveMetric("Complete collection", "$it ms")',
    'ExpressiveMetric("Base collection", "$it ms")',
    'ExpressiveMetric("Metadata + Surface", "$it ms")',
    'ExpressiveMetric("Background details", "$it ms")',
    '"Slowest dedicated probe"',
]
for token in main_tokens:
    need(token in main, f'MainActivity timing instrumentation missing: {token}')

service_tokens = [
    'const val EXTRA_TIMING_PATH = "timing_path"',
    'requestedTiming.path != requestedResult.path + ".timing"',
    '!requestedTiming.path.startsWith(cacheRoot.path + File.separator)',
    'private fun writeTelemetry(path: String, text: String): Boolean',
    'text.toByteArray(Charsets.UTF_8).size > 16 * 1024',
    '"dispatchToWorkerMs"',
    '"libraryLoadMs"',
    '"nativeCallMs"',
    '"terminalValidationMs"',
    '"servicePreTerminalMs"',
    'writeTelemetry(safeTimingPath, timingPayload)',
]
for token in service_tokens:
    need(token in service, f'VulkanProbeService timing telemetry missing: {token}')

need(main.count('timingFile.delete()') >= 3, 'timing sidecar lifecycle cleanup is incomplete')
need('Native-call timing is measured around the JNI collector inside the dedicated probe process' in main, 'UI does not distinguish JNI timing from individual Vulkan-command timing')
need('JNI collector wall-clock timing must not be labeled as timing for individual Vulkan API commands.' in rules, 'PROJECT_RULES timing semantics missing')
need('Timing telemetry is not part of the Vulkan capability report and is not sent to VulkanScope Database.' in audit, 'audit privacy boundary missing')
need('queryTimingMs' not in ''.join(re.findall(r'private fun technicalReportJson.*?\n\}', main, re.S)), 'timing telemetry leaked into technical report function')
need('"service/"' in advanced and 'Isolated probe-process phase measured' in advanced, 'diagnostics classification for service timing missing')

contract_path = root / 'tests/golden/2.0.0_timing_instrumentation_immutable_contract.json'
need(contract_path.is_file(), '2.0.0 immutable regression contract missing')
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
print('PASS VulkanScope 2.0.0 collection timing instrumentation contract')
