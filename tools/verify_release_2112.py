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
audit = read('rules/2.1.12_SINGLE_PAGER_CLIPPED_GLASS_AUDIT.md')
changelog = read('changelog.md')

if not args.skip_version:
    need('versionCode = 2112' in gradle and 'versionName = "2.1.12"' in gradle, '2.1.12 release identity missing')

lazy_page = block(main, '@Composable\nprivate fun VulkanLazyPage(', '@Composable\nprivate fun SoftScrollIntersectionShadows')
sticky = block(main, 'private fun LazyListScope.stickyCollectionPager(', '@Composable\nprivate fun ScrollBoundaryIndicators')
header = block(main, 'private fun Modifier.frostedHeaderBackdrop(): Modifier = drawWithContent {', '@OptIn(ExperimentalMaterial3Api::class)')

need('import androidx.compose.ui.draw.clipToBounds' in main, 'clipToBounds import missing')
need('.height(glassHeight)\n                        .clipToBounds()\n                        .drawWithContent {' in lazy_page, 'blur glass is not clipped to bounded glass container')
need('drawLayer(blurredLayer)' in lazy_page, 'retained blurred layer missing')
need('sourceLayer.record { this@drawWithContent.drawContent() }' in lazy_page, 'crisp retained source layer missing')
need('drawLayer(sourceLayer)' in lazy_page, 'crisp source layer replay missing')
need('PixelCopy' not in main and 'toImageBitmap(' not in lazy_page, 'forbidden framebuffer/bitmap glass path present')

need(lazy_page.count('CollectionPager(') == 1, 'VulkanLazyPage must own exactly one visible CollectionPager instance')
need('\n        CollectionPager(' not in sticky, 'lazy pager anchor still renders a second CollectionPager')
need('Spacer(' in sticky and '.height(with(density) { pagerHeightPx.toDp() })' in sticky, 'same-height pager placeholder missing')
need('onMeasuredHeight = updateMeasuredHeight' in sticky, 'pager measured-height feedback missing')
need('.onSizeChanged { size -> if (size.height > 0 && size.height != registration.heightPx) registration.onMeasuredHeight(size.height) }' in lazy_page, 'overlay pager measurement feedback missing')
need('.offset { IntOffset(0, pagerVisual.overlayOffsetPx.toInt()) }' in lazy_page, 'single pager live Y offset missing')
need('.alpha(pagerVisual.progress)' not in lazy_page, 'overlay pager crossfade remains')
need('alpha(1f - handoffProgress)' not in sticky and 'val handoffProgress by remember(' not in sticky, 'normal/overlay pager crossfade remains')
need('DisposableEffect(pagerKey)' not in sticky, 'lazy-item disposal still unregisters pinned pager')
need('item(key = "$pagerKey-cleanup")' in sticky and 'reportRegistration(pagerKey, null)' in sticky, 'explicit no-pager cleanup path missing')
need('it.layoutItemCount == listState.layoutInfo.totalItemsCount' in lazy_page, 'layout-generation guard missing')
need('val fallbackPinned = info == null && (' in lazy_page, 'post-anchor pinned fallback missing')
need('val overlayOffset = naturalOffset.coerceAtLeast(headerBoundaryPx.toFloat())' in lazy_page, 'live header clamp missing')

need('reportStickyPagerState(reporterKey, activeLaneExtent > 0, activeLaneExtent)' in lazy_page, 'pager lane reporter does not clear zero-lane state')
need('val scrollHintTopInset by animateDpAsState(' in lazy_page, 'animated combined scroll-hint top inset missing')
need('targetValue = headerContentInset + pinnedPagerContentInset + transientOverlayContentInset + 10.dp' in lazy_page, 'scroll-hint stack ordering target missing')
need('top = scrollHintTopInset' in lazy_page, 'upper scroll hint does not consume animated stack inset')
need('LocalPinnedPagerContentInset provides pinnedPagerContentInset' in main, 'raw pager lane is not provided for one-stage scroll-hint animation')

hairline = 'drawRect(ComposeColor.White.copy(alpha = 0.045f), topLeft = Offset(0f, size.height - 1f), size = Size(size.width, 1f))'
need(hairline not in header, 'header bottom seam hairline remains')
need(hairline not in lazy_page, 'pager glass bottom seam hairline remains')

need('import androidx.compose.ui.graphics.rememberGraphicsLayer' in main, 'correct rememberGraphicsLayer import lost')
need('import androidx.compose.ui.graphics.layer.drawLayer' in main, 'correct drawLayer import lost')
need('import androidx.compose.ui.graphics.layer.rememberGraphicsLayer' not in main, 'compile-breaking rememberGraphicsLayer import returned')
need('import androidx.compose.ui.graphics.drawscope.drawLayer' not in main, 'compile-breaking drawLayer import returned')
need('// ' not in main and '/*' not in main, 'source-code comment rule violated in MainActivity.kt')

protected = {
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

need('Release 2.1.12 single-pager clipped-glass and coordinated scroll-hint requirements' in rules, 'PROJECT_RULES missing 2.1.12 contract')
for token in ['8.04-second', 'one visible rendering instance', 'clip', 'portrait and landscape', 'post-fix device replay']:
    need(token in audit or token in rules, f'2.1.12 evidence missing: {token}')
need(changelog.startswith('## 2.1.12\n'), '2.1.12 changelog entry missing or not current')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 2.1.12 single-pager clipped-glass contract')
