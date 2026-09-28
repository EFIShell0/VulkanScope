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
audit = read('rules/2.1.13_UNIFIED_PAGER_FROSTED_BLUR_AUDIT.md')
changelog = read('changelog.md')

if not args.skip_version:
    need('versionCode = 2113' in gradle and 'versionName = "2.1.13"' in gradle, '2.1.13 release identity missing')

lazy_page = block(main, '@Composable\nprivate fun VulkanLazyPage(', '@Composable\nprivate fun SoftScrollIntersectionShadows')
sticky = block(main, 'private fun LazyListScope.stickyCollectionPager(', '@Composable\nprivate fun ScrollBoundaryIndicators')
chrome = block(main, 'private fun Modifier.frostedChromeBackdrop(', '@OptIn(ExperimentalMaterial3Api::class)')
nav = block(main, '@Composable\nprivate fun CompactBottomNavigationBar(', '@Composable\nprivate fun ExploreDestinationTile')
app = block(main, '@Composable\nprivate fun VulkanScopeApp(', '@Composable\nprivate fun SurfaceProbe')
pager = block(main, '@Composable\nprivate fun CollectionPager(', 'private fun LazyListScope.stickyCollectionPager(')

need('import androidx.compose.ui.graphics.rememberGraphicsLayer' in main, 'rememberGraphicsLayer import missing')
need('import androidx.compose.ui.graphics.layer.drawLayer' in main, 'drawLayer import missing')
need('import androidx.compose.ui.graphics.layer.GraphicsLayer' in main, 'GraphicsLayer type import missing')
need('import androidx.compose.ui.graphics.drawscope.clipRect' in main, 'clipRect import missing')
need('import androidx.compose.ui.layout.positionInRoot' in main, 'positionInRoot import missing')
need('import androidx.compose.ui.graphics.layer.rememberGraphicsLayer' not in main, 'compile-breaking rememberGraphicsLayer import returned')
need('import androidx.compose.ui.graphics.drawscope.drawLayer' not in main, 'compile-breaking drawLayer import returned')

need('val chromeSourceLayer = rememberGraphicsLayer()' in app, 'crisp app chrome source layer missing')
need('val chromeBlurredLayer = rememberGraphicsLayer()' in app, 'blurred app chrome layer missing')
need('chromeSourceLayer.record { this@drawWithContent.drawContent() }' in app, 'app chrome source recording missing')
need('chromeBlurredLayer.record { drawLayer(chromeSourceLayer) }' in app, 'app chrome blur recording missing')
need('drawLayer(chromeSourceLayer)' in app, 'ordinary page is not replayed from crisp source layer')
need('drawLayer(chromeBlurredLayer)' not in app, 'blurred app layer is drawn over the ordinary page')
need('chromeBlurredLayer.renderEffect = chromeRenderEffect' in app, 'bounded app chrome blur RenderEffect missing')
need('backdropLayer = chromeBlurredLayer' in app, 'header/navigation blur layer is not shared from app host')

need('drawLayer(backdropLayer)' in chrome, 'chrome backdrop does not sample retained blurred page layer')
need('translate(-rootOffset.x, -rootOffset.y)' in chrome, 'chrome backdrop sampling is not aligned to live root coordinates')
need('drawRect(VulkanGlassTint)' in chrome, 'shared chrome glass tint missing')
need('Brush.radialGradient' not in chrome and 'Brush.verticalGradient' not in chrome, 'chrome tint still contains varying gradient tones')
need('.onGloballyPositioned { headerRootOffset = it.positionInRoot() }' in main, 'header live root coordinate sampling missing')
need('.frostedChromeBackdrop(backdropLayer, headerRootOffset)' in main, 'header real frosted backdrop missing')
need('.onGloballyPositioned { navigationRootOffset = it.positionInRoot() }' in nav, 'bottom navigation live root coordinate sampling missing')
need('.frostedChromeBackdrop(backdropLayer, navigationRootOffset)' in nav, 'bottom navigation real frosted backdrop missing')
need('color = ComposeColor.Transparent' in nav, 'bottom navigation retains opaque base that hides backdrop blur')

need('val sourceLayer = rememberGraphicsLayer()' in lazy_page and 'val blurredLayer = rememberGraphicsLayer()' in lazy_page, 'pager join retained layers missing')
need('sourceLayer.record { this@drawWithContent.drawContent() }' in lazy_page, 'pager join crisp source recording missing')
need('blurredLayer.record { drawLayer(sourceLayer) }' in lazy_page, 'pager join blurred layer recording missing')
need('drawLayer(sourceLayer)' in lazy_page, 'pager list crisp replay missing')
need('.height(glassHeight)\n                        .clipToBounds()' in lazy_page, 'pager join glass is not clipped to bounded height')
need('val glassTop = headerBoundaryPx.toFloat().coerceIn(0f, size.height)' in lazy_page, 'pager join does not start at live header boundary')
need('clipRect(top = glassTop)' in lazy_page, 'pager join blur is not clipped below header boundary')
need('drawRect(\n                                        color = VulkanGlassTint' in lazy_page, 'pager join does not use shared uniform glass tint')
need('Brush.radialGradient' not in block(lazy_page, 'if (glassHeight > 0.dp) {', 'if (pagerVisual != null) {') and 'Brush.verticalGradient' not in block(lazy_page, 'if (glassHeight > 0.dp) {', 'if (pagerVisual != null) {'), 'pager join still contains varying gradient tones')

need('val progress = if (fallbackPinned) 1f else' in lazy_page, 'pager handoff progress missing')
need('CollectionPagerVisualState(registration, progress, overlayOffset, laneExtent, glassHeightPx)' in lazy_page, 'visible zero-progress pager state is not retained')
need('if (progress <= 0f)' not in lazy_page, 'pager disappears before settling when handoff progress reaches zero')
need('.offset { IntOffset(0, pagerVisual.overlayOffsetPx.toInt()) }' in lazy_page, 'single pager does not follow live anchor Y directly')
need('pagerOverlayOffset' not in lazy_page and 'animatedOverlayOffsetPx' not in lazy_page, 'independent pager-position tween remains')
need('pagerGlassHeight' not in lazy_page and 'glassHeightTarget' not in lazy_page, 'independent pager-glass-height tween remains')
need('.padding(top = 5.dp, bottom = 7.dp)' not in lazy_page, 'overlay-only vertical pager padding still prevents exact placeholder landing')
need(lazy_page.count('CollectionPager(') == 1, 'VulkanLazyPage must own exactly one visible CollectionPager instance')
need('\n        CollectionPager(' not in sticky, 'lazy pager anchor still renders a second CollectionPager')
need('Spacer(' in sticky and '.height(with(density) { pagerHeightPx.toDp() })' in sticky, 'same-height pager placeholder missing')
need('val initialPagerHeightPx = with(density) { 72.dp.roundToPx() }' in sticky, 'pager initial placeholder height is not aligned with current pager minimum geometry')
need('onMeasuredHeight = updateMeasuredHeight' in sticky, 'pager measured-height feedback missing')
need('DisposableEffect(pagerKey)' not in sticky, 'lazy-item disposal still unregisters active pager')
need('item(key = "$pagerKey-cleanup")' in sticky and 'reportRegistration(pagerKey, null)' in sticky, 'explicit no-pager cleanup path missing')
need('it.layoutItemCount == listState.layoutInfo.totalItemsCount' in lazy_page, 'layout-generation guard missing')
need('val fallbackPinned = info == null && (' in lazy_page, 'post-anchor pinned fallback missing')
need('val overlayOffset = naturalOffset.coerceAtLeast(headerBoundaryPx.toFloat())' in lazy_page, 'live header clamp missing')

need('private val VulkanPagerSurface = ComposeColor(0xFF1B1719)' in main, 'stable opaque pager surface color missing')
need('color = VulkanPagerSurface,' in pager, 'pager card does not use stable opaque surface')
need('VulkanPagerSurface.copy(' not in pager, 'pager card surface became translucent during join')
need('frostedPagerBackdrop' not in main, 'translucent pager tint layer can still double-compose during join')
need('private val VulkanGlassTint = ComposeColor(0xA823171D)' in main, 'shared glass tint changed or missing')

need('reportStickyPagerState(reporterKey, activeLaneExtent > 0, activeLaneExtent)' in lazy_page, 'pager lane reporter does not clear zero-lane state')
need('val scrollHintTopInset by animateDpAsState(' in lazy_page, 'smooth upper scroll-hint stack animation missing')
need('targetValue = headerContentInset + pinnedPagerContentInset + transientOverlayContentInset + 10.dp' in lazy_page, 'scroll-hint stack ordering target missing')
need('top = scrollHintTopInset' in lazy_page, 'upper scroll hint does not consume coordinated stack inset')

need('PixelCopy' not in main and 'toImageBitmap(' not in main, 'forbidden framebuffer/bitmap glass path present')
need('// ' not in main and '/*' not in main, 'source-code comment rule violated in MainActivity.kt')

protected = {
    'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt': '6da56de2847870fbe2ad1777c951f405fafb3859b2552e3d60857686bc477544',
    'app/src/main/AndroidManifest.xml': 'f89dab96d5c123cecb227d6a44f4deef5b8b92d2688e46fb55df9bd1c54f18a8',
    'app/src/main/cpp/vulkanscope.cpp': 'fa2d5aea65b493138aa7c0cdbfa25bd715fa6e2265daa4c5c6b70b777e62f3ff',
    'registry/registry_lock.json': '79c46ebb498997b31c56b3171296ff672d229c8434a31ac77c8a17f86e4f9d0a',
    'registry/generated/registry_query_manifest.json': '60136afc325fef0dfd1ea6bfaf57da6ae61096b96a241dd126ad0fdecc3f6d00',
}
for rel, expected in protected.items():
    path = root / rel
    need(path.is_file(), f'protected file missing: {rel}')
    if path.is_file():
        need(hashlib.sha256(path.read_bytes()).hexdigest() == expected, f'protected file drift: {rel}')

need('Release 2.1.13 unified pager settle and bounded frosted chrome requirements' in rules, 'PROJECT_RULES missing 2.1.13 contract')
for token in ['2.1.12', '9.66-second', 'zero handoff progress', 'compact bottom navigation', 'stable opaque surface', 'portrait and landscape']:
    need(token in audit or token in rules, f'2.1.13 evidence missing: {token}')
need(changelog.startswith('## 2.1.13\n'), '2.1.13 changelog entry missing or not current')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 2.1.13 unified pager settle and frosted chrome contract')
