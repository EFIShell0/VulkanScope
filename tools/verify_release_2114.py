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
audit = read('rules/2.1.14_PAGER_LANDING_UNIFIED_GLASS_AUDIT.md')
changelog = read('changelog.md')

if not args.skip_version:
    need('versionCode = 2114' in gradle and 'versionName = "2.1.14"' in gradle, '2.1.14 release identity missing')

lazy_page = block(main, '@Composable\nprivate fun VulkanLazyPage(', '@Composable\nprivate fun SoftScrollIntersectionShadows')
sticky = block(main, 'private fun LazyListScope.stickyCollectionPager(', '@Composable\nprivate fun ScrollBoundaryIndicators')
pager = block(main, '@Composable\nprivate fun CollectionPager(', 'private fun LazyListScope.stickyCollectionPager(')
chrome = block(main, 'private fun Modifier.frostedChromeBackdrop(', '@OptIn(ExperimentalMaterial3Api::class)')
app = block(main, '@Composable\nprivate fun VulkanScopeApp(', '@Composable\nprivate fun SurfaceProbe')

need('LocalCollectionPagerMeasuredHeightLookup' in main and 'LocalCollectionPagerMeasuredHeightReporter' in main, 'pager measured-height owner state missing')
need('val pagerMeasuredHeights = remember(listState) { mutableStateMapOf<String, Int>() }' in lazy_page, 'pager height is not retained outside lazy-item lifetime')
need('val storedPagerHeightPx = measuredHeightLookup(pagerKey)' in sticky, 'lazy pager does not reuse retained measured height')
need('val pagerHeightPx = if (storedPagerHeightPx > 0) storedPagerHeightPx else initialPagerHeightPx' in sticky, 'pager placeholder lacks stable measured-height fallback')
need('reportMeasuredHeight(pagerKey, measuredHeight)' in sticky, 'in-flow pager measurement is not persisted to page owner')
need('onMeasuredHeight = updateMeasuredHeight' in sticky, 'overlay pager measurement feedback missing')

need('val previous = visibleItems.firstOrNull { it.index == registration.itemIndex - 1 }' in lazy_page, 'predecessor-adjacent pager position bridge missing')
need('val next = visibleItems.firstOrNull { it.index == registration.itemIndex + 1 }' in lazy_page, 'successor-adjacent pager position bridge missing')
need('previous.offset + previous.size + verticalSpacingPx' in lazy_page, 'previous-item natural pager offset reconstruction missing')
need('next.offset - registration.heightPx - verticalSpacingPx' in lazy_page, 'next-item natural pager offset reconstruction missing')
need('if (progress <= 0f) {' in lazy_page and '\n                            null\n' in lazy_page, 'overlay is not removed outside bounded join region')
need('val activeOverlayKey = pagerVisual?.registration?.key' in lazy_page, 'single-visual pager ownership key missing')
need('LocalCollectionPagerOverlayActiveKey provides activeOverlayKey' in lazy_page, 'single-visual pager ownership is not provided to lazy item')
need('if (activeOverlayKey == pagerKey) {' in sticky, 'lazy item is not replaced by same-height spacer during overlay ownership')
need('} else {\n            Box(' in sticky and 'CollectionPager(' in sticky, 'normal-flow pager is not restored into the lazy item')
need(lazy_page.count('CollectionPager(') == 1, 'page overlay must render exactly one pager during join/pin')
need(sticky.count('CollectionPager(') == 2, 'lazy item must render exactly one pager outside join/pin')
need('CollectionPagerVisualState(registration, progress, overlayOffset, laneExtent, glassHeightPx)' in lazy_page, 'pager overlay visual state missing')
need('.offset { IntOffset(0, pagerVisual.overlayOffsetPx.toInt()) }' in lazy_page, 'overlay pager no longer follows reconstructed live Y geometry')
need('pagerOverlayOffset' not in lazy_page and 'animatedOverlayOffsetPx' not in lazy_page, 'independent pager position tween returned')

need('val chromeBlurRadiusPx = with(density) { 24.dp.toPx() }' in app, 'app chrome blur radius changed')
need('val blurRadiusPx = with(density) { 24.dp.toPx() }' in lazy_page, 'pager join blur radius does not match app chrome blur radius')
need('drawRect(\n                                        color = VulkanGlassTint' in lazy_page, 'pager join does not use shared chrome glass tint')
need('drawRect(VulkanGlassTint)' in chrome, 'header/navigation do not use shared glass tint')
need('VulkanGlassTint.copy(' not in block(lazy_page, 'if (glassHeight > 0.dp) {', 'if (pagerVisual != null) {'), 'pager join glass tint alpha diverged from shared chrome tint')
need('Brush.radialGradient' not in block(lazy_page, 'if (glassHeight > 0.dp) {', 'if (pagerVisual != null) {'), 'pager join reintroduced radial tone variation')
need('Brush.verticalGradient' not in block(lazy_page, 'if (glassHeight > 0.dp) {', 'if (pagerVisual != null) {'), 'pager join reintroduced vertical tone variation')
need('val glassHeightPx = pagerBottom.toInt().coerceAtLeast(headerBoundaryPx)' in lazy_page, 'pager glass no longer encloses the full moving pager lane')
need('val glassTop = headerBoundaryPx.toFloat().coerceIn(0f, size.height)' in lazy_page, 'pager glass no longer starts exactly at live header boundary')
need('clipRect(top = glassTop)' in lazy_page, 'pager glass is not clipped below header boundary')
need('VulkanPagerSurface.copy(' not in pager and 'color = VulkanPagerSurface,' in pager, 'pager card tone changes across join state')
need('drawLayer(blurredLayer)' not in block(lazy_page, 'LazyColumn(', 'if (glassHeight > 0.dp) {'), 'blurred layer is painted over ordinary page content')
need('drawLayer(sourceLayer)' in lazy_page, 'ordinary lazy page is not replayed crisp')
need('drawLayer(chromeBlurredLayer)' not in app, 'blurred chrome layer is painted over full ordinary page')

need('targetValue = headerContentInset + pinnedPagerContentInset + transientOverlayContentInset + 10.dp' in lazy_page, 'scroll indicator stack target changed')
need('animationSpec = tween(220, easing = FastOutSlowInEasing)' in lazy_page, 'scroll indicator stack lost bounded smooth motion')
need('PixelCopy' not in main and 'toImageBitmap(' not in main, 'forbidden framebuffer or bitmap blur path present')
need('// ' not in main and '/*' not in main, 'source-code comment rule violated in MainActivity.kt')

need('## Release 2.1.14 pager landing and unified glass requirements' in rules, '2.1.14 project rules missing')
need('# VulkanScope 2.1.14 Pager Landing / Unified Glass Audit' in audit, '2.1.14 audit heading missing')
need(changelog.startswith('## 2.1.14\n'), '2.1.14 changelog entry missing or not first')

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
print('PASS VulkanScope 2.1.14 pager landing, single-visual handoff and unified blur/tone verifier')
