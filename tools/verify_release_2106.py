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
    p = root / rel
    need(p.is_file(), f'missing file: {rel}')
    return p.read_text(encoding='utf-8') if p.is_file() else ''

def block(text, start, end):
    a = text.find(start)
    b = text.find(end, a + len(start)) if a >= 0 else -1
    need(a >= 0 and b > a, f'missing block: {start}')
    return text[a:b] if a >= 0 and b > a else ''

main = read('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
gradle = read('app/build.gradle.kts')
rules = read('rules/PROJECT_RULES.md')
audit = read('rules/2.1.6_FROSTED_HEADER_OVERLAY_COORDINATION_AUDIT.md')
changelog = read('changelog.md')

need('versionCode = 2106' in gradle and 'versionName = "2.1.6"' in gradle, '2.1.6 release identity missing')
need('// ' not in main and '/*' not in main, 'source-code comment rule violated in MainActivity.kt')

header = block(main, 'private fun Modifier.mosaicHeaderBackdrop()', '\n@Composable\nprivate fun DisplayPage')
for token in [
    'val cell = 12.dp.toPx()',
    'ComposeColor.White.copy(alpha = 0.020f)',
    'ComposeColor.Black.copy(alpha = 0.032f)',
    'VulkanSurfaceRaised.copy(alpha = 0.76f)',
    'VulkanSurfaceRaised.copy(alpha = 0.48f)',
    '.mosaicHeaderBackdrop()',
    'calculateLeftPadding(layoutDirection)',
    'calculateRightPadding(layoutDirection)',
]:
    need(token in header, f'frosted header contract missing: {token}')
need('VulkanSurfaceRaised.copy(alpha = 1.0f)' not in header, 'header became opaque')

menu = block(main, 'private fun FileManagerOptionsChooser(', '\nprivate fun fileManagerViewModeIcon')
for token in [
    'icon = R.drawable.ic_close',
    'contentDescription = "Close view and sort menu"',
    'onClick = { expanded = false }',
    'size = 36.dp',
]:
    need(token in menu, f'file-manager close contract missing: {token}')
need('Text("View & sort"' not in menu, 'old View & sort popup heading remains')
for token in ['FileManagerViewMode.entries.chunked(3)', 'FileManagerSortMode.entries.forEach']:
    need(token in menu, f'combined layout/sort choices missing: {token}')

pager = block(main, 'private fun CollectionPager(', '\n\nprivate fun LazyListScope.stickyCollectionPager')
for token in [
    'unfocusedTextColor = ComposeColor.Transparent',
    'targetState = (currentPage + 1).coerceIn(1, pageCount)',
    'label = "pageNumberTransition"',
]:
    need(token in pager, f'page-number-only transition contract missing: {token}')
need(pager.count('AnimatedContent(') == 1, 'CollectionPager must animate only the page number')

sticky = block(main, 'private fun LazyListScope.stickyCollectionPager(', '\n@Composable\nprivate fun ScrollBoundaryIndicators')
for token in [
    'var pagerIndex by remember(pagerKey) { mutableIntStateOf(-1) }',
    'pagerInfo?.let { pagerIndex = it.index }',
    'state.firstVisibleItemIndex > pagerIndex',
    'state.firstVisibleItemScrollOffset > 0',
    'headerContentInset + transientOverlayContentInset + 58.dp',
    'Modifier.zIndex(3f)',
    'CollectionPager(totalItems = totalItems, currentPage = currentPage',
]:
    need(token in sticky, f'sticky pager persistence/coordination missing: {token}')
need('label = "collectionPagerTransition"' not in sticky, 'whole-pager transition remains')
need('AnimatedContent(' not in sticky, 'whole sticky pager is still animated on page change')

lazy_page = block(main, 'private fun VulkanLazyPage(', '\n@Composable\nprivate fun SoftScrollIntersectionShadows')
for token in [
    'val coordinatedTransientOverlayInset by animateDpAsState(',
    'label = "coordinatedTopOverlayInset"',
    'top = headerContentInset + coordinatedTransientOverlayInset,',
    'top = headerContentInset + coordinatedTransientOverlayInset + 10.dp,',
]:
    need(token in lazy_page, f'lazy overlay coordination missing: {token}')

surface = block(main, 'private fun SurfaceSectionCards(', '\n@Composable\nprivate fun SurfacePresentationPage')
for token in ['val coordinatedTopOverlayInset by animateDpAsState(', 'label = "surfaceTopOverlayInset"', 'top = headerContentInset + 8.dp + coordinatedTopOverlayInset']:
    need(token in surface, f'Surface overlay coordination missing: {token}')

protected = {
    'app/src/main/AndroidManifest.xml': 'f89dab96d5c123cecb227d6a44f4deef5b8b92d2688e46fb55df9bd1c54f18a8',
    'app/src/main/cpp/vulkanscope.cpp': 'fa2d5aea65b493138aa7c0cdbfa25bd715fa6e2265daa4c5c6b70b777e62f3ff',
    'registry/registry_lock.json': '79c46ebb498997b31c56b3171296ff672d229c8434a31ac77c8a17f86e4f9d0a',
}
for rel, expected in protected.items():
    p = root / rel
    need(p.is_file(), f'protected file missing: {rel}')
    if p.is_file():
        need(hashlib.sha256(p.read_bytes()).hexdigest() == expected, f'protected file drift: {rel}')

need('Release 2.1.6 frosted header, file-menu close action and coordinated paging overlay requirements' in rules, 'PROJECT_RULES missing 2.1.6 contract')
for token in ['immutable predecessor', 'pager item index', 'vertical lanes', 'page number']:
    need(token.lower() in audit.lower(), f'2.1.6 audit missing: {token}')
need(changelog.startswith('## 2.1.6\n'), '2.1.6 changelog entry missing or not current')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 2.1.6 frosted-header/overlay-coordination contract')
