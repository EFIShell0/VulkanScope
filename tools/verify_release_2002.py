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
audit = read('rules/2.0.2_PARALLEL_ONESHOT_CORRECTNESS_UI_AUDIT.md')
wrapper = read('gradle/wrapper/gradle-wrapper.properties')

need('versionCode = 2002' in app_gradle and 'versionName = "2.0.2"' in app_gradle, '2.0.2 release identity missing')
need('com.android.application") version "9.4.1"' in root_gradle, 'AGP 9.4.1 drifted')
need('gradle-9.7.1-bin.zip' in wrapper, 'Gradle wrapper drifted from 9.7.1')
need('compileSdk = 37' in app_gradle and 'targetSdk = 37' in app_gradle, 'compile/target SDK drifted')
need('android:usesCleartextTraffic="false"' in manifest, 'cleartext transport protection missing')
need('// ' not in main and '/*' not in main and '// ' not in service and '/*' not in service, 'source-code comment rule violated in changed Kotlin files')

for token in [
    'private const val BACKGROUND_PROBE_LANES = 4',
    'memoryClassMb <= 256 -> 2',
    'memoryClassMb <= 384 -> 3',
    'else -> BACKGROUND_PROBE_LANES',
    'private val backgroundProbeMutexes = List(BACKGROUND_PROBE_LANES) { Mutex() }',
    'VulkanProbeServiceBg0::class.java',
    'VulkanProbeServiceBg1::class.java',
    'VulkanProbeServiceBg2::class.java',
    'VulkanProbeServiceBg3::class.java',
    'async(Dispatchers.IO)',
    'runBackgroundIsolatedProbe(group, index % activeBackgroundProbeLanes, modeSnapshot)',
    '}.awaitAll()',
    'for ((group, raw, elapsedMs) in backgroundResults)',
    'timingMap["collection/background_process_lanes"] = activeBackgroundProbeLanes.toLong()',
    'ExpressiveMetric("Parallel isolated lanes", it.toString())',
    'VALIDATED_PHYSICAL_DEVICE_QUERY_EXTENSIONS',
    '.map { "ext::$it" }',
    'BACKGROUND_COLLECTION_BUDGET_MS = 60_000L',
]:
    need(token in main, f'parallel one-shot contract missing: {token}')

need('EXTRA_SESSION' not in service and 'SESSION_MAX_GROUPS' not in service, '2.0.1 multi-query session code remains in service')
need('open class VulkanProbeService : Service()' in service, 'probe service is not reusable by isolated lane components')
for lane in range(4):
    need(f'class VulkanProbeServiceBg{lane} : VulkanProbeService()' in service, f'background service class {lane} missing')
    need(f'android:name=".VulkanProbeServiceBg{lane}" android:exported="false" android:process=":vulkan_probe_bg{lane}"' in manifest, f'non-exported background service manifest entry {lane} missing')
need('Process.killProcess(Process.myPid())' in service, 'one-shot service process teardown missing')
need('Executors.newSingleThreadExecutor' in service, 'dedicated service worker bound missing')
need('64L * 1024L * 1024L' in service, 'probe result bound missing')
need('16 * 1024' in service, 'timing telemetry bound missing')

for token in [
    'val topOverlayInset = LocalTransientOverlayContentInset.current',
    'val bottomNavigationInset = LocalBottomNavigationContentInset.current',
    'start = 18.dp + startInset',
    'top = 8.dp + topOverlayInset',
    'end = 18.dp + endInset',
    'bottom = bottomNavigationInset',
]:
    need(token in main, f'Surface landing overlay-safe inset missing: {token}')

for token in [
    'val suppressDesktopQuickMenu = remember(context)',
    'isChromeOsRuntime(context) || isAndroidPcFormFactor(context) || hasFreeformWindowManagement(context)',
    'if (!suppressDesktopQuickMenu) showQuickMenu = true',
    'if (!suppressDesktopQuickMenu) {',
    'CustomAccessibilityAction("Evidence actions")',
    'onLongPress = { showEvidenceActions = true }',
]:
    need(token in main, f'desktop secondary-click/accessibility contract missing: {token}')

immutable = {
    'app/src/main/cpp/vulkanscope.cpp': 'fa2d5aea65b493138aa7c0cdbfa25bd715fa6e2265daa4c5c6b70b777e62f3ff',
    'app/src/main/cpp/registry_query_catalog.h': 'bef2bbfa855eafdd56934409c4dd541dd4273ecf78ee3db5aa98646b16c5d299',
    'app/src/main/cpp/extension_field_coverage_generated.inc': '6b0d5d6b5efed5573ed8b53b73d2765557afa38d4ce76bbd0c61df00e83c8d00',
    'app/src/main/cpp/extension_field_coverage_parity.inc': '9fb748f489140c078d18149bd2233ca21a1db88bdbbc79f71e5935a15bcf6a76',
    'app/src/main/cpp/runtime_extension_pnext_generated.inc': '4cf00f7f1cda7a1d807e186c75ac38235743ccf426bff8c2ea3ed49c2f9906e4',
    'app/src/main/cpp/runtime_extension_pnext_parity.inc': 'b809e7204ed8ae54f34f115e29331bbf9ca6e2222c12adf67f30a66f3f8129d9',
    'app/src/main/cpp/video_registry_generated.h': '4f681bffc63c012a53becc38db3d2c5f8f80ef7f2e33b9524c5dce1c3a121cd3',
    'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt': 'dec891e7fe1251deb3911d4b9ec394d8b7b8468bacd8a19953733aabd3a94629',
    'app/src/main/java/com/efishell/vulkanscope/AdvancedAnalysis.kt': '6ba807d6e5ab99f780c49fa87ae6e075e8d3cb6833cf060890e0ab9a80277fcb',
    'registry/generated/registry_query_manifest.json': '60136afc325fef0dfd1ea6bfaf57da6ae61096b96a241dd126ad0fdecc3f6d00',
    'registry/registry_lock.json': '79c46ebb498997b31c56b3171296ff672d229c8434a31ac77c8a17f86e4f9d0a',
    'registry/video_registry_lock.json': '6491b557ebd7bd2f3eeac81f2661643bd8533867d62d0418666f628b6832c555',
}
for rel, expected in immutable.items():
    path = root / rel
    need(path.is_file(), f'2.0.0 parity file missing: {rel}')
    if path.is_file():
        need(hashlib.sha256(path.read_bytes()).hexdigest() == expected, f'2.0.0 correctness-baseline byte drift: {rel}')

need('477 registered extensions' in audit and '305 validated Android-queryable physical-device providers' in audit, 'locked extension census missing from 2.0.2 audit')
need('one-query-per-process' in rules and 'Multi-query process reuse is forbidden' in rules, 'PROJECT_RULES does not supersede 2.0.1 session reuse')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 2.0.2 parallel one-shot correctness/UI contract')
