#!/usr/bin/env python3
import argparse
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
graph = read('app/src/main/java/com/efishell/vulkanscope/VulkanDependencyGraph.kt')
gradle = read('app/build.gradle.kts')
rules = read('rules/PROJECT_RULES.md')
audit = read('rules/3.0.4_KOTLIN_COMPILE_RESTORATION_AUDIT.md')
changelog = read('changelog.md')

declared_files = [line.strip() for line in read('files.txt').splitlines() if line.strip()]
actual_files = sorted(path.relative_to(root).as_posix() for path in root.rglob('*') if path.is_file())
need(declared_files == actual_files, 'files.txt strict census mismatch')

need('versionCode = 3004' in gradle and 'versionName = "3.0.4"' in gradle, '3.0.4 release identity missing')
need(changelog.startswith('## 3.0.4\n'), '3.0.4 changelog entry missing or not first')
need('## Release 3.0.4 Kotlin compile restoration requirements' in rules, '3.0.4 project rules missing')
need('# VulkanScope 3.0.4 Kotlin Compile Restoration Audit' in audit, '3.0.4 audit heading missing')

need('event.nativeKeyCode' not in main, 'KeyEvent receiver is still used for nativeKeyCode')
need(main.count('event.key.nativeKeyCode') == 7, 'expected six repaired and one retained event.key.nativeKeyCode reads')
list_nav = block(main, 'private fun Modifier.tvRemoteLazyListNavigation(state: LazyListState): Modifier', 'private fun Modifier.tvRemoteLazyGridNavigation(state: LazyGridState): Modifier')
grid_nav = block(main, 'private fun Modifier.tvRemoteLazyGridNavigation(state: LazyGridState): Modifier', 'private fun VulkanLazyPage(')
page_nav = block(main, 'private fun VulkanLazyPage(', '@Composable\nprivate fun')
need(list_nav.count('event.key.nativeKeyCode') == 2, 'lazy-list TV keycode reads are not both repaired')
need(grid_nav.count('event.key.nativeKeyCode') == 2, 'lazy-grid TV keycode reads are not both repaired')
need(page_nav.count('event.key.nativeKeyCode') >= 2, 'page-level TV keycode reads are not repaired')
for token in ['AndroidKeyEvent.KEYCODE_DPAD_DOWN', 'AndroidKeyEvent.KEYCODE_DPAD_UP', 'AndroidKeyEvent.KEYCODE_PAGE_DOWN', 'AndroidKeyEvent.KEYCODE_PAGE_UP']:
    need(token in list_nav and token in grid_nav and token in page_nav, f'TV navigation mapping regressed: {token}')

need('import androidx.compose.foundation.layout.weight' not in graph, 'invalid explicit Foundation weight import remains')
need(graph.count('Modifier.weight(1f)') == 3, 'dependency graph equal-width summary weights changed')
need('Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {' in graph, 'dependency graph summary Row changed')

for token in [
    'MinimumProfileConfirmation(MinimumProfileConfirmationKind.SAVE',
    'MinimumProfileConfirmation(MinimumProfileConfirmationKind.LOAD',
    'MinimumProfileConfirmation(MinimumProfileConfirmationKind.DELETE',
    'private fun Modifier.tvRemoteLazyListNavigation(state: LazyListState): Modifier',
    'private fun Modifier.tvRemoteLazyGridNavigation(state: LazyGridState): Modifier',
    'delay(3000L)',
]:
    need(token in main, f'3.0.3 behavior anchor missing: {token}')
need('.verticalScroll(verticalState)' in graph and '.horizontalScroll(horizontalState)' in graph, '3.0.3 graph two-axis scrolling regressed')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 3.0.4 Kotlin compile restoration verifier')
