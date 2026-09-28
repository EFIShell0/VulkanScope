#!/usr/bin/env python3
import argparse
import hashlib
from pathlib import Path
from PIL import Image

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
audit = read('rules/2.1.5_TRANSPARENT_HEADER_FILE_MANAGER_PAGER_AUDIT.md')
changelog = read('changelog.md')

need('versionCode = 2105' in gradle and 'versionName = "2.1.5"' in gradle, '2.1.5 release identity missing')
need('// ' not in main and '/*' not in main, 'source-code comment rule violated in MainActivity.kt')
need('private val LocalAppHeaderContentInset = staticCompositionLocalOf { 0.dp }' in main, 'header content inset local missing')
need('private val LocalPrimaryLazyListState = staticCompositionLocalOf<LazyListState?> { null }' in main, 'primary list state local missing')
need('val headerContentInset = padding.calculateTopPadding()' in main, 'Scaffold header inset capture missing')
need('Box(Modifier.fillMaxSize()) {' in main, 'page viewport is not allowed behind top bar')
need('Box(Modifier.fillMaxSize().padding(padding)) {' not in main, 'Scaffold top padding still globally displaces page content')

header = block(main, 'private fun AppHeader(', '\n@Composable\nprivate fun DisplayPage')
for token in [
    'VulkanSurfaceRaised.copy(alpha = 0.48f)',
    'ComposeColor.Transparent',
    'calculateLeftPadding(layoutDirection)',
    'calculateRightPadding(layoutDirection)',
    'buttonSize = 36.dp',
    'slotSize = 50.dp',
    'R.drawable.vulkanscope_logo_horizontal_aligned',
]:
    need(token in header, f'2.1.5 header contract missing: {token}')
need('buttonSize = 42.dp' not in header, 'primary app Back circle was not reduced')

balanced = root / 'app/src/main/res/drawable-nodpi/vulkanscope_logo_horizontal_balanced.png'
aligned = root / 'app/src/main/res/drawable-nodpi/vulkanscope_logo_horizontal_aligned.png'
need(balanced.is_file() and aligned.is_file(), 'horizontal logo evidence/assets missing')
if balanced.is_file():
    need(hashlib.sha256(balanced.read_bytes()).hexdigest() == 'a764480b2789f748de0ba8a772a7899b18edf33982347300d969d68f41ca9d82', 'predecessor balanced logo drift')
if balanced.is_file() and aligned.is_file():
    a = Image.open(balanced).convert('RGBA')
    b = Image.open(aligned).convert('RGBA')
    need(a.size == b.size == (546, 84), 'aligned logo dimensions changed')
    if a.size == b.size:
        pa = a.load(); pb = b.load(); width, height = a.size
        preserved = all(pa[x, y] == pb[x, y] for x in range(282) for y in range(height))
        moved = True
        for x in range(282, width):
            for y in range(height):
                expected = pa[x, y - 4] if y >= 4 else (0, 0, 0, 0)
                if pb[x, y] != expected:
                    moved = False
                    break
            if not moved:
                break
        need(preserved, 'Vulkan mark or separator pixels moved in aligned logo')
        need(moved, 'SCOPE glyph group is not an exact four-pixel vertical translation')
need(main.count('R.drawable.vulkanscope_logo_horizontal_aligned') >= 2, 'aligned logo is not used by header/opening surfaces')

lazy_page = block(main, 'private fun VulkanLazyPage(', '\n@Composable\nprivate fun SoftScrollIntersectionShadows')
for token in ['LocalAppHeaderContentInset.current', 'LocalPrimaryLazyListState provides listState', 'top = headerContentInset + transientOverlayContentInset', 'top = headerContentInset + transientOverlayContentInset + 10.dp']:
    need(token in lazy_page, f'overlay scroll geometry missing: {token}')

pager = block(main, 'private fun CollectionPager(', '\n\nprivate fun LazyListScope.stickyCollectionPager')
need('VulkanSurfaceLow.copy(alpha = 0.78f)' in pager, 'pager card is not translucent')
sticky = block(main, 'private fun LazyListScope.stickyCollectionPager(', '\n@Composable\nprivate fun ScrollBoundaryIndicators')
for token in ['val listState = LocalPrimaryLazyListState.current', 'animateDpAsState(', 'headerContentInset + transientOverlayContentInset + 56.dp', '.offset(y = pinnedOffset)', 'ComposeColor.Transparent']:
    need(token in sticky, f'sticky transparent pager behavior missing: {token}')
need('VulkanBlack.copy(alpha = 0.96f)' not in sticky, 'opaque full-width sticky pager backing returned')

need('private enum class FileManagerViewMode { LIST, COMPACT, DETAILS, GRID, DENSE_GRID, LARGE_GRID }' in main, 'generic six-mode file-manager enum missing')
need('private fun FileManagerOptionsChooser(' in main, 'unified View & sort chooser missing')
need('Text("View & sort"' in main, 'unified menu heading missing')
need('TurnipFileManagerViewModeChooser(' not in main, 'separate Turnip layout chooser remains')
need('FileManagerSortChooser(' not in main, 'separate sort chooser remains')
for token in ['shared_storage_file_manager_view_mode', 'shared_storage_file_manager_sort_mode', 'FileManagerViewMode.GRID', 'SharedStorageFolderGridCard(', 'SharedStorageFileGridCard(']:
    need(token in main, f'shared-storage six-layout contract missing: {token}')

search = block(main, 'private fun ExpressiveSearchField(', '\n@Composable\nprivate fun ExpressiveAssistChip')
for token in ['val showLeadingFade = focused && textOverflows', 'val showTrailingFade = textOverflows', 'Alignment.CenterStart', 'Alignment.CenterEnd', 'width(24.dp)', 'width(28.dp)']:
    need(token in search, f'search soft-overflow contract missing: {token}')
need('value.isNotBlank() && measuredTextPx > availableTextPx' in search, 'search fade is not overflow-gated')

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

need('Release 2.1.5 transparent header, aligned wordmark, unified file-manager options and sticky-pager requirements' in rules, 'PROJECT_RULES missing 2.1.5 contract')
for token in ['immutable predecessor', 'shared-storage', 'sticky pager', 'side-navigation']:
    need(token.lower() in audit.lower(), f'2.1.5 audit missing: {token}')
need(changelog.startswith('## 2.1.5\n'), '2.1.5 changelog entry missing or not current')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 2.1.5 transparent header/file-manager/pager contract')
