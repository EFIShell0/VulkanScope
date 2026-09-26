#!/usr/bin/env python3
import argparse
import hashlib
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
rules = read('rules/PROJECT_RULES.md')
audit = read('rules/2.0.3_ADAPTIVE_PARALLEL_SCHEDULER_AUDIT.md')
wrapper = read('gradle/wrapper/gradle-wrapper.properties')

need('versionCode = 2003' in app_gradle and 'versionName = "2.0.3"' in app_gradle, '2.0.3 release identity missing')
need('com.android.application") version "9.4.1"' in root_gradle, 'AGP 9.4.1 drifted')
need('gradle-9.7.1-bin.zip' in wrapper, 'Gradle wrapper drifted from 9.7.1')
need('compileSdk = 37' in app_gradle and 'targetSdk = 37' in app_gradle, 'compile/target SDK drifted')
need('android:usesCleartextTraffic="false"' in manifest, 'cleartext transport protection missing')
need('// ' not in main and '/*' not in main and '// ' not in service and '/*' not in service, 'source-code comment rule violated in changed Kotlin files')

for token in [
    'private const val BACKGROUND_PROBE_LANES = 4',
    'val memoryInfo = ActivityManager.MemoryInfo()',
    'activityManager.getMemoryInfo(memoryInfo)',
    'activityManager.isLowRamDevice || memoryInfo.lowMemory -> 2',
    'totalMiB < 3072L || availableMiB < 768L || availableRatio < 0.12 -> 2',
    'totalMiB < 6144L || availableMiB < 1536L || availableRatio < 0.20 || processors < 6 -> 3',
    'else -> BACKGROUND_PROBE_LANES',
    'val nextGroup = java.util.concurrent.atomic.AtomicInteger(0)',
    '(0 until activeBackgroundProbeLanes).map { lane ->',
    'val index = nextGroup.getAndIncrement()',
    'runBackgroundIsolatedProbe(group, lane, modeSnapshot)',
    '}.awaitAll().flatten().sortedBy { it.first }.map { it.second }',
    'val hostElapsedMs = timingMap["service/$group/host_total"] ?: scheduledElapsedMs',
    'timingMap["probe_total/$group"] = hostElapsedMs',
    'timingMap["probe_queue_wait/$group"] = (scheduledElapsedMs - hostElapsedMs).coerceAtLeast(0L)',
    'ExpressiveMetric("Longest scheduler wait"',
    'VALIDATED_PHYSICAL_DEVICE_QUERY_EXTENSIONS',
    '.map { "ext::$it" }',
    'BACKGROUND_COLLECTION_BUDGET_MS = 60_000L',
]:
    need(token in main, f'2.0.3 scheduler contract missing: {token}')

need('memoryClassMb' not in main, 'legacy Java heap memoryClass scheduler gate remains')
need('index % activeBackgroundProbeLanes' not in main, 'static modulo lane assignment remains')
need('if (initialPids.isEmpty() && !stopRequested) return true' in main, 'quiescence no-process fast path missing')
need('if (stopRequested) delay(150L)' not in main, 'unconditional 150 ms teardown sleep remains')
for token in ['kotlinx.coroutines.delay(10L)', 'kotlinx.coroutines.delay(15L)', 'kotlinx.coroutines.delay(12L)']:
    need(token in main, f'bounded positive publication polling delay missing: {token}')

need('EXTRA_SESSION' not in service and 'SESSION_MAX_GROUPS' not in service, 'multi-query session code returned')
need('Process.killProcess(Process.myPid())' in service, 'one-shot process teardown missing')
need('Executors.newSingleThreadExecutor' in service, 'dedicated service worker bound missing')
for lane in range(4):
    need(f'class VulkanProbeServiceBg{lane} : VulkanProbeService()' in service, f'background service class {lane} missing')
    need(f'android:name=".VulkanProbeServiceBg{lane}" android:exported="false" android:process=":vulkan_probe_bg{lane}"' in manifest, f'non-exported lane {lane} manifest entry missing')

immutable = {
    'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt': 'bbb3f3a36d667561ddfb6c8b83e1945fe7d59930fa6794ec2e737dbd5cdd0313',
    'app/src/main/cpp/vulkanscope.cpp': 'fa2d5aea65b493138aa7c0cdbfa25bd715fa6e2265daa4c5c6b70b777e62f3ff',
    'app/src/main/cpp/registry_query_catalog.h': 'bef2bbfa855eafdd56934409c4dd541dd4273ecf78ee3db5aa98646b16c5d299',
    'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt': 'dec891e7fe1251deb3911d4b9ec394d8b7b8468bacd8a19953733aabd3a94629',
    'app/src/main/java/com/efishell/vulkanscope/AdvancedAnalysis.kt': '6ba807d6e5ab99f780c49fa87ae6e075e8d3cb6833cf060890e0ab9a80277fcb',
    'registry/generated/registry_query_manifest.json': '60136afc325fef0dfd1ea6bfaf57da6ae61096b96a241dd126ad0fdecc3f6d00',
    'registry/registry_lock.json': '79c46ebb498997b31c56b3171296ff672d229c8434a31ac77c8a17f86e4f9d0a',
}
for rel, expected in immutable.items():
    path = root / rel
    need(path.is_file(), f'correctness-baseline file missing: {rel}')
    if path.is_file():
        need(hashlib.sha256(path.read_bytes()).hexdigest() == expected, f'correctness-baseline byte drift: {rel}')

need('one-query-per-dedicated-process contract' in rules, 'PROJECT_RULES does not preserve one-query process isolation')
need('physical total/available memory' in rules, 'PROJECT_RULES does not require physical-memory scheduler signals')
need('477 registered extensions' in audit and '305 validated Android-queryable physical-device providers' in audit, 'locked 477/305 extension census missing from 2.0.3 audit')

if errors:
    for error in errors:
        if error:
            print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 2.0.3 adaptive parallel scheduler contract')
