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

main = read('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
gradle = read('app/build.gradle.kts')
rules = read('rules/PROJECT_RULES.md')
audit = read('rules/2.1.8_STICKY_REPORTER_COMPILE_FIX_AUDIT.md')
changelog = read('changelog.md')

need('versionCode = 2108' in gradle and 'versionName = "2.1.8"' in gradle, '2.1.8 release identity missing')
need('private typealias StickyPagerStateReporter = (String, Boolean, Int) -> Unit' in main, 'StickyPagerStateReporter Unit contract missing')
reporter = block(main, '        val stickyPagerStateReporter = remember(pinnedPagerHeights) {', '\n\n        CompositionLocalProvider(')
expected_tail = '''            { key: String, pinned: Boolean, heightPx: Int ->\n                if (pinned && heightPx > 0) pinnedPagerHeights[key] = heightPx else pinnedPagerHeights.remove(key)\n                Unit\n            }\n        }'''
need(expected_tail in reporter, 'sticky pager reporter does not terminate explicitly with Unit')
for forbidden in [
    'if (pinned && heightPx > 0) pinnedPagerHeights[key] = heightPx else pinnedPagerHeights.remove(key)\n            }\n        }',
    'LocalStickyPagerStateReporter provides { key: String, pinned: Boolean, heightPx: Int ->',
]:
    need(forbidden not in main, f'compile-breaking sticky reporter form remains: {forbidden}')
need('LocalStickyPagerStateReporter provides stickyPagerStateReporter' in main, 'sticky reporter composition-local binding missing')
need('// ' not in main and '/*' not in main, 'source-code comment rule violated in MainActivity.kt')

for token in [
    'private fun Modifier.frostedHeaderBackdrop()',
    '.frostedHeaderBackdrop()',
    'mutableStateMapOf<String, Int>()',
    'pinnedPagerHeights.values.maxOrNull() ?: 0',
    'label = "coordinatedPinnedPagerInset"',
    'label = "stickyPagerHeaderJoinOffset"',
    'label = "stickyPagerBackdropAlpha"',
    'fun requestPageChange(targetPage: Int)',
    'focusManager.clearFocus(force = true)',
    'label = "pageNumberTransition"',
    'fun requestFilterPageChange(targetPage: Int)',
    'label = "filterPageNumberTransition"',
    'SPIRV_TRADEMARK_DISPLAY_REGEX',
    'SPIR-V™',
]:
    need(token in main, f'2.1.7 retained contract missing: {token}')

sort_icon_files = [
    'app/src/main/res/drawable/ic_sort_name_asc.xml',
    'app/src/main/res/drawable/ic_sort_name_desc.xml',
    'app/src/main/res/drawable/ic_sort_modified_newest.xml',
    'app/src/main/res/drawable/ic_sort_modified_oldest.xml',
    'app/src/main/res/drawable/ic_sort_created_newest.xml',
    'app/src/main/res/drawable/ic_sort_created_oldest.xml',
]
icon_bytes = []
for rel in sort_icon_files:
    path = root / rel
    need(path.is_file(), f'missing retained semantic sort icon: {rel}')
    if path.is_file():
        data = path.read_bytes()
        icon_bytes.append(data)
        need(b'<vector' in data and b'<path' in data, f'invalid retained semantic sort icon: {rel}')
need(len(set(icon_bytes)) == 6, 'retained semantic sort icons are not six byte-distinct resources')

protected = {
    'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt': '0ba308122100e1bb40d1dc9594e6fe382756dbb33a832dc5ff5b1b3e18c1e756',
    'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt': '6da56de2847870fbe2ad1777c951f405fafb3859b2552e3d60857686bc477544',
    'app/src/main/AndroidManifest.xml': 'f89dab96d5c123cecb227d6a44f4deef5b8b92d2688e46fb55df9bd1c54f18a8',
    'app/src/main/cpp/vulkanscope.cpp': 'fa2d5aea65b493138aa7c0cdbfa25bd715fa6e2265daa4c5c6b70b777e62f3ff',
    'registry/registry_lock.json': '79c46ebb498997b31c56b3171296ff672d229c8434a31ac77c8a17f86e4f9d0a',
}
for rel, expected in protected.items():
    path = root / rel
    need(path.is_file(), f'protected file missing: {rel}')
    if path.is_file():
        need(hashlib.sha256(path.read_bytes()).hexdigest() == expected, f'protected file drift: {rel}')

need('Release 2.1.8 sticky-pager reporter Kotlin compile restoration requirements' in rules, 'PROJECT_RULES missing 2.1.8 contract')
for token in ['compileReleaseKotlin', '(String, Boolean, Int) -> Any?', '(String, Boolean, Int) -> Unit', 'explicit terminal `Unit`']:
    need(token in audit, f'2.1.8 audit missing: {token}')
need(changelog.startswith('## 2.1.8\n'), '2.1.8 changelog entry missing or not current')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 2.1.8 sticky-pager reporter compile-restoration contract')
