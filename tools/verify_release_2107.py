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
audit = read('rules/2.1.7_GLASS_PAGER_FILE_ICONS_TRADEMARK_AUDIT.md')
changelog = read('changelog.md')

need('versionCode = 2107' in gradle and 'versionName = "2.1.7"' in gradle, '2.1.7 release identity missing')
need('// ' not in main and '/*' not in main, 'source-code comment rule violated in MainActivity.kt')

header = block(main, 'private fun Modifier.frostedHeaderBackdrop()', '\n@OptIn(ExperimentalMaterial3Api::class)\n@Composable\nprivate fun AppHeader')
for token in [
    'VulkanSurfaceRaised.copy(alpha = 0.90f)',
    'Brush.verticalGradient(',
    'ComposeColor.White.copy(alpha = 0.045f)',
    'ComposeColor.Black.copy(alpha = 0.030f)',
    'drawContent()',
]:
    need(token in header, f'smooth glass header contract missing: {token}')
for forbidden in ['val cell = 12.dp.toPx()', 'var row = 0', 'var column = 0', 'mosaicHeaderBackdrop']:
    need(forbidden not in main, f'block mosaic implementation remains: {forbidden}')
app_header = block(main, 'private fun AppHeader(', '\n@Composable\nprivate fun DisplayPage')
need('.frostedHeaderBackdrop()' in app_header, 'app header does not use smooth glass backdrop')
need('calculateLeftPadding(layoutDirection)' in app_header and 'calculateRightPadding(layoutDirection)' in app_header, 'header lost side-system-navigation safe insets')

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
    need(path.is_file(), f'missing semantic sort icon: {rel}')
    if path.is_file():
        data = path.read_bytes()
        icon_bytes.append(data)
        need(b'<vector' in data and b'<path' in data, f'invalid semantic vector icon: {rel}')
need(len(set(icon_bytes)) == 6, 'semantic sort icons must be six byte-distinct resources')
menu = block(main, 'private fun FileManagerOptionsChooser(', '\nprivate fun fileManagerViewModeIcon')
for token in [
    'fileManagerViewModeIcon(viewMode)',
    'fileManagerSortModeIcon(sortMode)',
    'fileManagerSortModeIcon(candidate)',
    'contentDescription = fileManagerSortLabel(candidate)',
    'R.drawable.ic_check',
]:
    need(token in menu, f'file-manager semantic icon contract missing: {token}')
sort_helper = block(main, 'private fun fileManagerSortModeIcon(', '\n@Composable\nprivate fun MesaOfficialLogoBadge')
for token in [
    'NAME_ASC -> R.drawable.ic_sort_name_asc',
    'NAME_DESC -> R.drawable.ic_sort_name_desc',
    'MODIFIED_NEWEST -> R.drawable.ic_sort_modified_newest',
    'MODIFIED_OLDEST -> R.drawable.ic_sort_modified_oldest',
    'CREATED_NEWEST -> R.drawable.ic_sort_created_newest',
    'CREATED_OLDEST -> R.drawable.ic_sort_created_oldest',
]:
    need(token in sort_helper, f'sort icon mapping missing: {token}')

app = block(main, 'private fun VulkanScopeApp(', '\n@Composable\nprivate fun rememberPointerWheelScalePx')
for token in [
    'mutableStateMapOf<String, Int>()',
    'pinnedPagerHeights.values.maxOrNull() ?: 0',
    'label = "coordinatedPinnedPagerInset"',
    'LocalPinnedPagerContentInset provides coordinatedPinnedPagerInset',
    'LocalStickyPagerStateReporter provides stickyPagerStateReporter',
    '.offset(y = headerContentInset + coordinatedPinnedPagerInset)',
]:
    need(token in app, f'overlay coordinator contract missing: {token}')

lazy_page = block(main, 'private fun VulkanLazyPage(', '\n@Composable\nprivate fun SoftScrollIntersectionShadows')
need('val pinnedPagerContentInset = LocalPinnedPagerContentInset.current' in lazy_page, 'lazy page missing pinned-pager inset')
need('top = headerContentInset + pinnedPagerContentInset + coordinatedTransientOverlayInset + 10.dp' in lazy_page, 'top scroll hint is not below pager and transient status lanes')

pager = block(main, 'private fun CollectionPager(', '\n\nprivate fun LazyListScope.stickyCollectionPager')
for token in [
    'var suppressFocusCommit by remember { mutableStateOf(false) }',
    'fun requestPageChange(targetPage: Int)',
    'pageField = TextFieldValue((bounded + 1).toString())',
    'focusManager.clearFocus(force = true)',
    'requestPageChange(currentPage - 1)',
    'requestPageChange(currentPage + 1)',
    'label = "pageNumberTransition"',
    'unfocusedTextColor = ComposeColor.Transparent',
]:
    need(token in pager, f'collection pager edit/number contract missing: {token}')
need(pager.count('AnimatedContent(') == 1, 'collection pager must animate only its displayed page number')

sticky = block(main, 'private fun LazyListScope.stickyCollectionPager(', '\n@Composable\nprivate fun ScrollBoundaryIndicators')
for token in [
    'var pagerIndex by remember(pagerKey) { mutableIntStateOf(-1) }',
    'state.firstVisibleItemIndex > pagerIndex',
    'reportStickyPagerState(pagerKey, pinned, if (pinned) pagerHeightPx else 0)',
    'onDispose { reportStickyPagerState(pagerKey, false, 0) }',
    'targetValue = if (pinned) headerContentInset else 0.dp',
    'label = "stickyPagerHeaderJoinOffset"',
    'label = "stickyPagerBackdropAlpha"',
    'VulkanSurfaceRaised.copy(alpha = 0.90f * backdropAlpha)',
    'Modifier.zIndex(6f)',
]:
    need(token in sticky, f'sticky pager join/persistence contract missing: {token}')
need('transientOverlayContentInset' not in sticky, 'pinned pager is still positioned below transient status instead of above it')

filter_selector = block(main, 'private fun ExpressiveSingleFilterSelector(', '\n@Composable\nprivate fun ExpressiveFilterBar')
for token in [
    'var suppressFilterPageCommit by remember { mutableStateOf(false) }',
    'fun requestFilterPageChange(targetPage: Int)',
    'requestFilterPageChange(page - 1)',
    'requestFilterPageChange(page + 1)',
    'label = "filterPageNumberTransition"',
    'unfocusedTextColor = ComposeColor.Transparent',
]:
    need(token in filter_selector, f'filter pager edit/number contract missing: {token}')

for token in [
    'SPIRV_TRADEMARK_DISPLAY_REGEX',
    'Selected GPU only · minimal VkDevice, SPIR-V™ shader-module',
    'trademarkSpirvDisplayText(test.optString("name", "Test"))',
]:
    need(token in main, f'SPIR-V trademark presentation missing: {token}')

protected = {
    'app/src/main/AndroidManifest.xml': 'f89dab96d5c123cecb227d6a44f4deef5b8b92d2688e46fb55df9bd1c54f18a8',
    'app/src/main/cpp/vulkanscope.cpp': 'fa2d5aea65b493138aa7c0cdbfa25bd715fa6e2265daa4c5c6b70b777e62f3ff',
    'registry/registry_lock.json': '79c46ebb498997b31c56b3171296ff672d229c8434a31ac77c8a17f86e4f9d0a',
}
for rel, expected in protected.items():
    path = root / rel
    need(path.is_file(), f'protected file missing: {rel}')
    if path.is_file():
        need(hashlib.sha256(path.read_bytes()).hexdigest() == expected, f'protected file drift: {rel}')

need('Release 2.1.7 smooth glass, semantic file icons, pinned-pager lane ordering and SPIR-V trademark requirements' in rules, 'PROJECT_RULES missing 2.1.7 contract')
for token in ['immutable predecessor', 'smooth', 'sort', 'overlay', 'SPIR-V™']:
    need(token.lower() in audit.lower(), f'2.1.7 audit missing: {token}')
need(changelog.startswith('## 2.1.7\n'), '2.1.7 changelog entry missing or not current')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 2.1.7 smooth-glass/pager/file-icon/trademark contract')
