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

def block(text, start, end):
    a = text.find(start)
    b = text.find(end, a + len(start)) if a >= 0 else -1
    need(a >= 0 and b > a, f'missing block: {start}')
    return text[a:b] if a >= 0 and b > a else ''

def sha(rel):
    path = root / rel
    return hashlib.sha256(path.read_bytes()).hexdigest() if path.is_file() else ''

main = read('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
gradle = read('app/build.gradle.kts')
rules = read('rules/PROJECT_RULES.md')
audit = read('rules/3.0.10_KOTLIN_COMPILE_FILE_MANAGER_LANDSCAPE_AUDIT.md')
changelog = read('changelog.md')

need('versionCode = 3010' in gradle and 'versionName = "3.0.10"' in gradle, '3.0.10 release identity missing')
need(changelog.startswith('## 3.0.10\n'), '3.0.10 changelog entry missing or not first')
need('## Release 3.0.10 Kotlin compile and File Manager landscape requirements' in rules, '3.0.10 project rules missing')
need('# VulkanScope 3.0.10 Kotlin Compile and File Manager Landscape Audit' in audit, '3.0.10 audit heading missing')
need('// ' not in main and '/*' not in main, 'source-code comment rule violated in MainActivity.kt')

submitted = block(main, '@Composable\nprivate fun DatabaseSubmittedAt(raw: String)', '\n@Composable\nprivate fun ExpressiveInfoPill(')
need('val context = androidx.compose.ui.platform.LocalContext.current' in submitted, 'Database submitted-time LocalContext compile repair missing')
need('val context = LocalContext.current' not in submitted, 'unqualified LocalContext compile regression reintroduced')
need('databaseSubmittedDisplay(context, raw)' in submitted, 'Database submitted-time formatter path regressed')

chooser = block(main, '@Composable\nprivate fun FileManagerOptionsChooser(', '\nprivate fun fileManagerViewModeIcon(')
for token in [
    'Dialog(',
    'dismissOnBackPress = false',
    'dismissOnClickOutside = false',
    'usePlatformDefaultWidth = false',
    '.statusBarsPadding()',
    '.navigationBarsPadding()',
    'desktopVerticalPointerScroll(menuScrollState)',
    'dpadScrollableNavigation(menuScrollState)',
    'verticalScroll(menuScrollState)',
    'icon = R.drawable.ic_close',
    'contentDescription = "Close view and sort menu"',
    'onClick = { expanded = false }',
    'FileManagerViewMode.entries.chunked(3)',
    'FileManagerSortMode.entries.forEach',
]:
    need(token in chooser, f'landscape-safe View & sort contract missing: {token}')
need('DropdownMenu(' not in chooser, 'View & sort remains dependent on anchored DropdownMenu')
need('onDismissRequest = { }' in chooser, 'View & sort chooser no longer preserves explicit X-only dismissal path')
need(main.count('FileManagerOptionsChooser(') >= 3, 'Turnip/shared File Manager chooser reuse regressed')

need('SystemDriverVendorBadge(reportRow.vendorId)' in main, '3.0.8 Database GPU vendor badge regressed')
need('DatabaseSubmittedAt(reportRow.submittedAt)' in main, '3.0.9 Database submitted-time presentation regressed')
need('color = if (isTurnip) VulkanAccentSoft else VulkanTextPrimary' in main, '3.0.9 driver identity styling regressed')
need('event.nativeKeyCode' not in main, '3.0.4 nativeKeyCode receiver regression reintroduced')

protected = {
    'app/src/main/AndroidManifest.xml': 'f89dab96d5c123cecb227d6a44f4deef5b8b92d2688e46fb55df9bd1c54f18a8',
    'app/src/main/cpp/vulkanscope.cpp': 'fa2d5aea65b493138aa7c0cdbfa25bd715fa6e2265daa4c5c6b70b777e62f3ff',
    'app/src/main/java/com/efishell/vulkanscope/AdvancedAnalysis.kt': '4bb7d65a1206873f0629792d3012139dcf3247157622e8200e40595104e62904',
    'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt': '6da56de2847870fbe2ad1777c951f405fafb3859b2552e3d60857686bc477544',
    'app/src/main/java/com/efishell/vulkanscope/VulkanDependencyGraph.kt': '6ca8bab0c89a28b322ccb449f8e2314f13597488d69fec6c1a263340c030d71d',
    'registry/registry_lock.json': '79c46ebb498997b31c56b3171296ff672d229c8434a31ac77c8a17f86e4f9d0a',
    'registry/generated/registry_query_manifest.json': '60136afc325fef0dfd1ea6bfaf57da6ae61096b96a241dd126ad0fdecc3f6d00',
}
for rel, expected in protected.items():
    need(sha(rel) == expected, f'protected predecessor-equivalent file drift: {rel}')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 3.0.10 Kotlin compile/File Manager landscape verifier')
