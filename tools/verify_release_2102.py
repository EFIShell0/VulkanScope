#!/usr/bin/env python3
import argparse
import hashlib
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
    p = root / rel
    need(p.is_file(), f'missing file: {rel}')
    return p.read_text(encoding='utf-8') if p.is_file() else ''

main = read('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
app_gradle = read('app/build.gradle.kts')
rules = read('rules/PROJECT_RULES.md')
audit = read('rules/2.1.2_HEADER_LAYOUT_SCROLL_REFINEMENT_AUDIT.md')
changelog = read('changelog.md')

need('versionCode = 2102' in app_gradle and 'versionName = "2.1.2"' in app_gradle, '2.1.2 release identity missing')
need('// ' not in main and '/*' not in main, 'source-code comment rule violated in MainActivity.kt')

for token in [
    '.statusBarsPadding()\n            .padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 8.dp)',
    'shape = RoundedCornerShape(28.dp)',
    'val shape = RoundedCornerShape(17.dp)',
    '.size(50.dp)',
    'ComposeColor(0xFF35191D)',
    'Box(modifier.width(50.dp).height(50.dp)',
    'fromEnd: Boolean = false',
    'fromEnd = true',
    'private fun HeaderTitleBadge(',
    'color = ComposeColor(0x223A1B1F)',
    'VulkanAccentSoft.copy(alpha = 0.84f)',
]:
    need(token in main, f'header refinement missing: {token}')
need('CURRENT SECTION' not in main, 'legacy CURRENT SECTION header label returned')

pager_start = main.find('private fun CollectionPager(')
pager_end = main.find('\n@Composable\nprivate fun ScrollBoundaryIndicators', pager_start)
need(pager_start >= 0 and pager_end > pager_start, 'CollectionPager block missing')
if pager_start >= 0 and pager_end > pager_start:
    pager = main[pager_start:pager_end]
    on_value = re.search(r'onValueChange = \{ value ->(.*?)\n\s*\},\n\s*modifier =', pager, re.S)
    need(on_value is not None, 'CollectionPager onValueChange block missing')
    if on_value:
        need('onPageChange' not in on_value.group(1), 'page entry still navigates during each keystroke')
    need('keyboardActions = KeyboardActions(onDone = {' in pager, 'pager IME Done commit missing')
    need('commitPageSelection()' in pager, 'pager deferred commit missing')
    need('focusManager.clearFocus(force = true)' in pager, 'pager focus completion missing')

for token in [
    'private enum class TurnipFileManagerViewMode { LIST, COMPACT, DETAILS, GRID, DENSE_GRID, LARGE_GRID }',
    'modifier = Modifier.width(304.dp)',
    'TurnipFileManagerViewMode.entries.chunked(3)',
    'repeat(3 - rowModes.size)',
    'TurnipFileManagerViewMode.DENSE_GRID -> 92.dp',
    'else -> 156.dp',
    'if (large) {',
    'MesaOfficialLogoBadge(size = 44.dp',
    'Text(candidate.name',
]:
    need(token in main, f'file-manager layout refinement missing: {token}')

need(len(re.findall(r'AnimatedHeaderActionButton\(\s*visible = !atRoot', main)) >= 2, 'file-manager/shared-storage animated root back buttons missing')
need('contentDescription = "Parent folder"' in main, 'parent-folder back semantics missing')

for token in [
    'private fun ExpressiveSearchField(',
    '.width(22.dp)\n                        .height(38.dp)',
    '.width(34.dp)\n                        .height(38.dp)',
    'Brush.horizontalGradient(listOf(containerColor, containerColor.copy(alpha = 0.72f), ComposeColor.Transparent))',
    'Brush.horizontalGradient(listOf(ComposeColor.Transparent, containerColor.copy(alpha = 0.74f), containerColor))',
    'private fun SoftScrollIntersectionShadows(',
    '.height(46.dp)',
    '.height(50.dp)',
    'edgeColor: ComposeColor = VulkanBlack',
]:
    need(token in main, f'soft clipping/fade contract missing: {token}')
need(main.count('edgeColor = VulkanSurfaceRaised') >= 3, 'dialog/file-manager edge fades not applied to all required scrolling surfaces')

immutable = {
    'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt': '6da56de2847870fbe2ad1777c951f405fafb3859b2552e3d60857686bc477544',
    'app/src/main/AndroidManifest.xml': 'f89dab96d5c123cecb227d6a44f4deef5b8b92d2688e46fb55df9bd1c54f18a8',
    'app/src/main/cpp/vulkanscope.cpp': 'fa2d5aea65b493138aa7c0cdbfa25bd715fa6e2265daa4c5c6b70b777e62f3ff',
    'app/src/main/cpp/registry_query_catalog.h': 'bef2bbfa855eafdd56934409c4dd541dd4273ecf78ee3db5aa98646b16c5d299',
    'registry/registry_lock.json': '79c46ebb498997b31c56b3171296ff672d229c8434a31ac77c8a17f86e4f9d0a',
    'registry/generated/registry_query_manifest.json': '60136afc325fef0dfd1ea6bfaf57da6ae61096b96a241dd126ad0fdecc3f6d00',
    'app/src/main/java/com/efishell/vulkanscope/AdvancedAnalysis.kt': '6ba807d6e5ab99f780c49fa87ae6e075e8d3cb6833cf060890e0ab9a80277fcb',
    'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt': 'dec891e7fe1251deb3911d4b9ec394d8b7b8468bacd8a19953733aabd3a94629',
}
for rel, expected in immutable.items():
    p = root / rel
    need(p.is_file(), f'correctness baseline file missing: {rel}')
    if p.is_file():
        need(hashlib.sha256(p.read_bytes()).hexdigest() == expected, f'correctness baseline byte drift: {rel}')

need('Release 2.1.2 header, layout and scroll refinement requirements' in rules, 'PROJECT_RULES missing 2.1.2 contract')
for token in ['status bar', '3×2', 'IME Done', 'soft fade', '92 dp', 'large-tile']:
    need(token.lower() in audit.lower(), f'2.1.2 audit missing evidence: {token}')
need(changelog.startswith('## 2.1.2\n'), '2.1.2 changelog entry missing or not current')

if errors:
    for e in errors:
        print('FAIL:', e)
    raise SystemExit(1)
print('PASS VulkanScope 2.1.2 header/layout/scroll refinement contract')
