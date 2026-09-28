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
audit = read('rules/2.1.16_LAZY_VIEWPORT_PAGER_ALIGNMENT_AUDIT.md')
changelog = read('changelog.md')

if not args.skip_version:
    need('versionCode = 2116' in gradle and 'versionName = "2.1.16"' in gradle, '2.1.16 release identity missing')

lazy_page = block(main, '@Composable\nprivate fun VulkanLazyPage(', '@Composable\nprivate fun SoftScrollIntersectionShadows')
sticky = block(main, 'private fun LazyListScope.stickyCollectionPager(', '@Composable\nprivate fun ScrollBoundaryIndicators')
chrome = block(main, 'private fun Modifier.frostedChromeBackdrop(', '@OptIn(ExperimentalMaterial3Api::class)')
app = block(main, '@Composable\nprivate fun VulkanScopeApp(', '@Composable\nprivate fun SurfaceProbe')
nav = block(main, 'private fun CompactBottomNavigationBar(', '@Composable\nprivate fun HeaderActionButton')

need('val pagerMeasuredHeights = remember(listState) { mutableStateMapOf<String, Int>() }' in lazy_page, 'page-owned pager height state missing')
need(sticky.count('CollectionPager(') == 1, 'lazy pager item rendered a second CollectionPager')
need('Spacer(' in sticky and '.height(with(density) { pagerHeightPx.toDp() })' in sticky, 'permanent exact-height pager spacer missing')
need(lazy_page.count('CollectionPager(') == 1, 'VulkanLazyPage must render exactly one visual CollectionPager')
need('(direct.offset - layoutInfo.viewportStartOffset).toFloat()' in lazy_page, 'direct pager anchor is not converted through viewportStartOffset')
need('(previous.offset + previous.size + verticalSpacingPx - layoutInfo.viewportStartOffset).toFloat()' in lazy_page, 'previous-item bridge is not converted through viewportStartOffset')
need('(next.offset - registration.heightPx - verticalSpacingPx - layoutInfo.viewportStartOffset).toFloat()' in lazy_page, 'next-item bridge is not converted through viewportStartOffset')
need('direct != null -> direct.offset.toFloat()' not in lazy_page, 'raw direct lazy-item offset is still used as overlay Y')
need('previous.offset + previous.size + verticalSpacingPx).toFloat()' not in lazy_page, 'raw previous bridge offset is still used as overlay Y')
need('next.offset - registration.heightPx - verticalSpacingPx).toFloat()' not in lazy_page, 'raw next bridge offset is still used as overlay Y')
need('val overlayOffset = resolvedNaturalOffset.coerceAtLeast(headerBoundaryPx.toFloat())' in lazy_page, 'pager no longer clamps only at live header boundary')
need('.offset { IntOffset(0, pagerVisual.overlayOffsetPx.toInt()) }' in lazy_page, 'pager does not follow corrected scroll-derived Y directly')
need('animateIntAsState' not in lazy_page and 'animatedOverlayOffsetPx' not in lazy_page and 'pagerOverlayOffset' not in lazy_page, 'whole-pager position animation returned')

need('val reportChromeBackdrop = LocalChromeBackdropReporter.current' in lazy_page, 'shared page blur reporting missing')
need('val blurRadiusPx = with(density) { 24.dp.toPx() }' in lazy_page, 'shared pager/header blur radius changed')
need('val chromeBlurRadiusPx = with(density) { 24.dp.toPx() }' in app, 'fallback chrome blur radius changed')
need('drawRect(\n                                        color = VulkanGlassTint' in lazy_page, 'pager join glass tint changed')
need('drawRect(VulkanGlassTint)' in chrome, 'header/navigation glass tint changed')
need('val headerBackdrop = pageChromeBackdrop ?: ChromeBackdropSource(chromeBlurredLayer, Offset.Zero)' in app, 'header shared backdrop selection changed')
need('val navigationBackdrop = pageChromeBackdrop ?: ChromeBackdropSource(chromeBlurredLayer, Offset.Zero)' in app, 'navigation shared backdrop selection changed')
need('translate(backdropRootOffset.x - targetRootOffset.x, backdropRootOffset.y - targetRootOffset.y)' in chrome, 'shared backdrop root-coordinate mapping changed')
need('backdropRootOffset: Offset' in nav, 'bottom navigation shared backdrop mapping missing')
need('val glassTop = headerBoundaryPx.toFloat().coerceIn(0f, size.height)' in lazy_page, 'join glass no longer begins at live header boundary')
need('clipRect(top = glassTop)' in lazy_page, 'join glass clipping changed')
need('Brush.radialGradient' not in block(lazy_page, 'if (glassHeight > 0.dp) {', 'if (pagerVisual != null) {'), 'join glass reintroduced radial tint variation')
need('Brush.verticalGradient' not in block(lazy_page, 'if (glassHeight > 0.dp) {', 'if (pagerVisual != null) {'), 'join glass reintroduced vertical tint variation')
need('drawLayer(blurredLayer)' not in block(lazy_page, 'LazyColumn(', 'if (glassHeight > 0.dp) {'), 'blurred layer is painted over ordinary page content')
need('drawLayer(sourceLayer)' in lazy_page, 'ordinary page crisp replay missing')
need('PixelCopy' not in main and 'toImageBitmap(' not in main, 'forbidden framebuffer/bitmap path present')
need('targetValue = headerContentInset + pinnedPagerContentInset + transientOverlayContentInset + 10.dp' in lazy_page, 'upper scroll-indicator stack target changed')
need('animationSpec = tween(220, easing = FastOutSlowInEasing)' in lazy_page, 'upper scroll-indicator bounded smooth motion changed')
need('// ' not in main and '/*' not in main, 'source-code comment rule violated in MainActivity.kt')

need('## Release 2.1.16 lazy-viewport pager alignment requirements' in rules, '2.1.16 project rules missing')
need('# VulkanScope 2.1.16 Lazy Viewport Pager Alignment Audit' in audit, '2.1.16 audit heading missing')
need(changelog.startswith('## 2.1.16\n'), '2.1.16 changelog entry missing or not first')

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
print('PASS VulkanScope 2.1.16 lazy viewport pager alignment verifier')
