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
audit = read('rules/2.0.5_TV_KEY_COMPILE_FIX_AUDIT.md')
wrapper = read('gradle/wrapper/gradle-wrapper.properties')

need('versionCode = 2005' in app_gradle and 'versionName = "2.0.5"' in app_gradle, '2.0.5 release identity missing')
need('com.android.application") version "9.4.1"' in root_gradle, 'AGP 9.4.1 drifted')
need('gradle-9.7.1-bin.zip' in wrapper, 'Gradle wrapper drifted from 9.7.1')
need('compileSdk = 37' in app_gradle and 'targetSdk = 37' in app_gradle, 'compile/target SDK drifted')
need('android:usesCleartextTraffic="false"' in manifest, 'cleartext transport protection missing')
need('// ' not in main and '/*' not in main and '// ' not in service and '/*' not in service, 'source-code comment rule violated in changed Kotlin files')
need('nativeKeyEvent' not in main, 'unavailable Compose nativeKeyEvent path remains')
need('delay(550L)showEvidenceActions=truetvLongPressConsumed=true' in ''.join(main.split()), 'TV long-press timer no longer opens and consumes evidence action atomically')

for token in [
    'import androidx.compose.ui.input.key.Key',
    'import androidx.compose.ui.input.key.KeyEventType',
    'import androidx.compose.ui.input.key.key',
    'import androidx.compose.ui.input.key.nativeKeyCode',
    'import androidx.compose.ui.input.key.type',
    '.onPreviewKeyEvent { event ->',
    'event.key == Key.DirectionCenter',
    'event.key == Key.Enter',
    'event.key == Key.NumPadEnter',
    'nativeKeyCode == AndroidKeyEvent.KEYCODE_BUTTON_SELECT',
    'nativeKeyCode == AndroidKeyEvent.KEYCODE_BUTTON_A',
    'var tvLongPressJob by remember(key, value) { mutableStateOf<Job?>(null) }',
    'val tvLongPressScope = rememberCoroutineScope()',
    'tvLongPressJob = tvLongPressScope.launch {',
    'delay(550L)',
    'showEvidenceActions = true',
    'tvLongPressConsumed = true',
    'tvLongPressJob?.cancel()',
    'tvLongPressJob = null',
    'KeyEventType.KeyDown',
    'KeyEventType.KeyUp',
]:
    need(token in main, f'2.0.5 TV key compile-fix contract missing: {token}')

for token in [
    'private const val BACKGROUND_PROBE_LANES = 6',
    'val memoryInfo = ActivityManager.MemoryInfo()',
    'activityManager.isLowRamDevice || memoryInfo.lowMemory -> 2',
    'val nextGroup = java.util.concurrent.atomic.AtomicInteger(0)',
    '(0 until activeBackgroundProbeLanes).map { lane ->',
    'runBackgroundIsolatedProbe(group, lane, modeSnapshot, backgroundDriverPaths)',
    '}.awaitAll().flatten().sortedBy { it.first }.map { it.second }',
    'val backgroundDriverPaths = withContext(Dispatchers.IO)',
    'BACKGROUND_COLLECTION_BUDGET_MS = 60_000L',
    'ExpressiveMetric("Longest scheduler wait"',
]:
    need(token in main, f'2.0.4 scheduler contract drifted in 2.0.5: {token}')

need('memoryClassMb' not in main, 'legacy Java heap memoryClass scheduler gate remains')
need('EXTRA_SESSION' not in service and 'SESSION_MAX_GROUPS' not in service, 'multi-query session code returned')
need('Process.killProcess(Process.myPid())' in service, 'one-shot process teardown missing')
need('Executors.newSingleThreadExecutor' in service, 'dedicated service worker bound missing')
for lane in range(6):
    need(f'class VulkanProbeServiceBg{lane} : VulkanProbeService()' in service, f'background service class {lane} missing')
    need(f'android:name=".VulkanProbeServiceBg{lane}" android:exported="false" android:process=":vulkan_probe_bg{lane}"' in manifest, f'non-exported lane {lane} manifest entry missing')

immutable = {
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

need('unresolved `androidx.compose.ui.input.key.nativeKeyEvent` API path is forbidden' in rules, 'PROJECT_RULES missing nativeKeyEvent compile-fix prohibition')
need('550 ms' in audit and 'Key.nativeKeyCode' in audit, '2.0.5 audit missing supported key/timer design')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 2.0.5 Android TV key-input compile-fix contract')
