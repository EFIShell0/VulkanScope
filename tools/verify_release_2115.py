#!/usr/bin/env python3
import argparse
import hashlib
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', type=Path, default=Path(__file__).resolve().parents[1])
parser.add_argument('--skip-version', action='store_true')
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
audit = read('rules/2.1.15_SINGLE_PAGER_CONTINUOUS_GLASS_AUDIT.md')
changelog = read('changelog.md')

if not args.skip_version:
    need('versionCode = 2115' in gradle and 'versionName = "2.1.15"' in gradle, '2.1.15 release identity missing')

lazy_page = block(main, '@Composable\nprivate fun VulkanLazyPage(', '@Composable\nprivate fun SoftScrollIntersectionShadows')
sticky = block(main, 'private fun LazyListScope.stickyCollectionPager(', '@Composable\nprivate fun ScrollBoundaryIndicators')
chrome = block(main, 'private fun Modifier.frostedChromeBackdrop(', '@OptIn(ExperimentalMaterial3Api::class)')
app = block(main, '@Composable\nprivate fun VulkanScopeApp(', '@Composable\nprivate fun SurfaceProbe')
nav = block(main, 'private fun CompactBottomNavigationBar(', '@Composable\nprivate fun HeaderActionButton')

need('private data class ChromeBackdropSource(val layer: GraphicsLayer, val rootOffset: Offset)' in main, 'shared chrome backdrop source model missing')
need('LocalChromeBackdropReporter' in main, 'page chrome backdrop reporter missing')
need('var pageChromeBackdrop by remember { mutableStateOf<ChromeBackdropSource?>(null) }' in app, 'active page chrome source is not retained by app host')
need('pageChromeBackdrop = ChromeBackdropSource(layer, rootOffset)' in app, 'active page chrome source is not registered')
need('if (pageChromeBackdrop?.layer === layer) pageChromeBackdrop = null' in app, 'page chrome source disposal does not protect newer source')
need('val headerBackdrop = pageChromeBackdrop ?: ChromeBackdropSource(chromeBlurredLayer, Offset.Zero)' in app, 'header does not prefer active page blur source')
need('val navigationBackdrop = pageChromeBackdrop ?: ChromeBackdropSource(chromeBlurredLayer, Offset.Zero)' in app, 'bottom navigation does not prefer active page blur source')
need('backdropLayer = headerBackdrop.layer' in app and 'backdropRootOffset = headerBackdrop.rootOffset' in app, 'header backdrop root mapping missing')
need('backdropLayer = navigationBackdrop.layer' in app and 'backdropRootOffset = navigationBackdrop.rootOffset' in app, 'bottom navigation backdrop root mapping missing')
need('LocalChromeBackdropReporter provides chromeBackdropReporter' in app, 'page chrome reporter is not provided to page content')

need('val pagerMeasuredHeights = remember(listState) { mutableStateMapOf<String, Int>() }' in lazy_page, 'pager height owner state missing')
need('val storedPagerHeightPx = measuredHeightLookup(pagerKey)' in sticky, 'pager spacer does not reuse retained measured height')
need('val pagerHeightPx = if (storedPagerHeightPx > 0) storedPagerHeightPx else initialPagerHeightPx' in sticky, 'pager spacer bootstrap height missing')
need(sticky.count('CollectionPager(') == 1, 'lazy pager item still renders a second CollectionPager')
need('Spacer(' in sticky and '.height(with(density) { pagerHeightPx.toDp() })' in sticky, 'lazy pager item is not a permanent exact-height spacer')
need('LocalCollectionPagerOverlayActiveKey' not in main, 'legacy pager ownership handoff key remains')
need(lazy_page.count('CollectionPager(') == 1, 'VulkanLazyPage must render exactly one visual CollectionPager')
need('direct.offset.toFloat()' in lazy_page, 'live LazyListItemInfo offset is not authoritative when pager anchor is visible')
need('previous.offset + previous.size + verticalSpacingPx' in lazy_page, 'previous-adjacent pager bridge missing')
need('next.offset - registration.heightPx - verticalSpacingPx' in lazy_page, 'next-adjacent pager bridge missing')
need('val overlayOffset = resolvedNaturalOffset.coerceAtLeast(headerBoundaryPx.toFloat())' in lazy_page, 'single pager does not clamp only at live header boundary')
need('.offset { IntOffset(0, pagerVisual.overlayOffsetPx.toInt()) }' in lazy_page, 'single pager does not follow scroll-derived Y directly')
need('animateIntAsState' not in lazy_page and 'animatedOverlayOffsetPx' not in lazy_page and 'pagerOverlayOffset' not in lazy_page, 'whole-pager position animation returned')
need('if (progress <= 0f)' not in lazy_page, 'single pager is still discarded outside the join region')

need('val reportChromeBackdrop = LocalChromeBackdropReporter.current' in lazy_page, 'lazy page does not report its retained blur layer')
need('var pageRootOffset by remember { mutableStateOf(Offset.Zero) }' in lazy_page, 'lazy-page root coordinate state missing')
need('if (pageRootOffset.y <= 0.5f) reportChromeBackdrop(blurredLayer, pageRootOffset) else reportChromeBackdrop(blurredLayer, null)' in lazy_page, 'lazy-page blur source/root offset registration/fallback missing')
need('onDispose { reportChromeBackdrop(blurredLayer, null) }' in lazy_page, 'lazy-page blur source disposal missing')
need('val blurRadiusPx = with(density) { 24.dp.toPx() }' in lazy_page, 'pager/header shared page blur radius changed')
need('val chromeBlurRadiusPx = with(density) { 24.dp.toPx() }' in app, 'fallback app chrome blur radius changed')
need('drawRect(\n                                        color = VulkanGlassTint' in lazy_page, 'pager join does not use shared uniform glass tint')
need('drawRect(VulkanGlassTint)' in chrome, 'header/navigation do not use shared uniform glass tint')
need('translate(backdropRootOffset.x - targetRootOffset.x, backdropRootOffset.y - targetRootOffset.y)' in chrome, 'shared blur source is not mapped through live root coordinates')
need('backdropRootOffset: Offset' in nav, 'bottom navigation lacks shared source root offset')
need('backdropRootOffset: Offset' in block(main, 'private fun AppHeader(', '@Composable\nprivate fun SettingsPage'), 'app header lacks shared source root offset')

need('val glassHeightPx = if (progress > 0f) {' in lazy_page, 'scroll-coupled join glass growth missing')
need('headerBoundaryPx + transitionDistance * progress' in lazy_page, 'join glass height is not derived directly from scroll progress')
need('val glassTop = headerBoundaryPx.toFloat().coerceIn(0f, size.height)' in lazy_page, 'join glass no longer starts at live header boundary')
need('clipRect(top = glassTop)' in lazy_page, 'join glass is not clipped to the bounded top-stack region')
need('Brush.radialGradient' not in block(lazy_page, 'if (glassHeight > 0.dp) {', 'if (pagerVisual != null) {'), 'join glass reintroduced radial tint variation')
need('Brush.verticalGradient' not in block(lazy_page, 'if (glassHeight > 0.dp) {', 'if (pagerVisual != null) {'), 'join glass reintroduced vertical tint variation')
need('BorderStroke' not in block(lazy_page, 'if (glassHeight > 0.dp) {', 'if (pagerVisual != null) {'), 'join glass reintroduced a separator border')
need('drawLayer(blurredLayer)' not in block(lazy_page, 'LazyColumn(', 'if (glassHeight > 0.dp) {'), 'blurred layer is painted over ordinary page content')
need('drawLayer(sourceLayer)' in lazy_page, 'ordinary lazy page is not replayed crisp')
need('PixelCopy' not in main and 'toImageBitmap(' not in main, 'forbidden framebuffer/bitmap blur path present')

need('targetValue = headerContentInset + pinnedPagerContentInset + transientOverlayContentInset + 10.dp' in lazy_page, 'upper scroll-indicator stack target changed')
need('animationSpec = tween(220, easing = FastOutSlowInEasing)' in lazy_page, 'upper scroll-indicator stack lost bounded smooth motion')
need('// ' not in main and '/*' not in main, 'source-code comment rule violated in MainActivity.kt')

need('## Release 2.1.15 single-pager anchor tracking and continuous shared glass requirements' in rules, '2.1.15 project rules missing')
need('# VulkanScope 2.1.15 Single Pager / Continuous Shared Glass Audit' in audit, '2.1.15 audit heading missing')
need(changelog.startswith('## 2.1.15\n'), '2.1.15 changelog entry missing or not first')

protected = {
    'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt': '6da56de2847870fbe2ad1777c951f405fafb3859b2552e3d60857686bc477544',
    'app/src/main/AndroidManifest.xml': 'f89dab96d5c123cecb227d6a44f4deef5b8b92d2688e46fb55df9bd1c54f18a8',
    'app/src/main/cpp/vulkanscope.cpp': 'fa2d5aea65b493138aa7c0cdbfa25bd715fa6e2265daa4c5c6b70b777e62f3ff',
    'registry/registry_lock.json': '79c46ebb498997b31c56b3171296ff672d229c8434a31ac77c8a17f86e4f9d0a',
    'registry/generated/registry_query_manifest.json': '60136afc325fef0dfd1ea6bfaf57da6ae61096b96a241dd126ad0fdecc3f6d00',
}
for rel, expected in protected.items():
    path = root / rel
    need(path.is_file(), f'missing protected file: {rel}')
    if path.is_file():
        need(hashlib.sha256(path.read_bytes()).hexdigest() == expected, f'protected file drift: {rel}')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 2.1.15 single-pager anchor tracking and continuous shared-glass verifier')
