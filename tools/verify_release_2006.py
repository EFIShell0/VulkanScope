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
audit = read('rules/2.0.6_SURFACE_CHOOSER_SCROLL_AUDIT.md')
wrapper = read('gradle/wrapper/gradle-wrapper.properties')

surface_start = main.find('private fun SurfaceSectionCards(onSelected: (SurfaceSection) -> Unit)')
surface_end = main.find('@Composable\nprivate fun SurfacePresentationPage', surface_start)
surface_block = main[surface_start:surface_end] if surface_start >= 0 and surface_end > surface_start else ''
need(bool(surface_block), 'SurfaceSectionCards block not found')

need('versionCode = 2006' in app_gradle and 'versionName = "2.0.6"' in app_gradle, '2.0.6 release identity missing')
need('com.android.application") version "9.4.1"' in root_gradle, 'AGP 9.4.1 drifted')
need('gradle-9.7.1-bin.zip' in wrapper, 'Gradle wrapper drifted from 9.7.1')
need('compileSdk = 37' in app_gradle and 'targetSdk = 37' in app_gradle, 'compile/target SDK drifted')
need('android:usesCleartextTraffic="false"' in manifest, 'cleartext transport protection missing')
need('// ' not in main and '/*' not in main and '// ' not in service and '/*' not in service, 'source-code comment rule violated in changed Kotlin files')

for token in [
    'private fun SurfaceSectionCards(onSelected: (SurfaceSection) -> Unit)',
    'val scrollState = rememberScrollState()',
    '.desktopVerticalPointerScroll(scrollState)',
    '.verticalScroll(scrollState)',
    '.focusGroup()',
    'top = 8.dp + topOverlayInset',
    'bottom = bottomNavigationInset',
    'start = 18.dp + startInset',
    'end = 18.dp + endInset',
    'ExpressiveScrollHints(',
    'scrollState,',
    'bottom = bottomNavigationInset + 4.dp',
]:
    need(token in surface_block, f'2.0.6 Surface chooser scroll contract missing: {token}')

for label in ['Display & HDR', 'Surface & color spaces', 'Presentation']:
    need(label in main, f'Surface destination missing: {label}')

for token in [
    'private const val BACKGROUND_PROBE_LANES = 6',
    'Process.killProcess(Process.myPid())',
    'Executors.newSingleThreadExecutor',
    'delay(550L)',
    'showEvidenceActions = true',
    'event.key == Key.DirectionCenter',
    'event.key == Key.Enter',
]:
    source = main if token not in ['Process.killProcess(Process.myPid())', 'Executors.newSingleThreadExecutor'] else service
    need(token in source, f'preserved 2.0.5/2.0.4 contract missing: {token}')

need('EXTRA_SESSION' not in service and 'SESSION_MAX_GROUPS' not in service, 'multi-query session code returned')
need('nativeKeyEvent' not in main, 'unsupported nativeKeyEvent path returned')

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

need('Surface chooser scrollability requirements' in rules, 'PROJECT_RULES missing 2.0.6 chooser scroll contract')
need('vertically scrollable' in audit and 'ExpressiveScrollHints' in audit, '2.0.6 audit missing scroll/safe-area design')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 2.0.6 Surface chooser scrollability contract')
