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
audit = read('rules/2.1.10_PROGRESSIVE_GLASS_HANDOFF_AUDIT.md')
changelog = read('changelog.md')

if not args.skip_version:
    need('versionCode = 2110' in gradle and 'versionName = "2.1.10"' in gradle, '2.1.10 release identity missing')

lazy_page = block(main, '@Composable\nprivate fun VulkanLazyPage(', '@Composable\nprivate fun SoftScrollIntersectionShadows')
sticky = block(main, 'private fun LazyListScope.stickyCollectionPager(', '@Composable\nprivate fun ScrollBoundaryIndicators')

for token in [
    'private data class CollectionPagerVisualState(',
    'val progress: Float,',
    'val overlayOffsetPx: Float,',
    'val laneExtentPx: Int',
    'val activePagerVisual by remember(listState, pagerRegistrations, headerBoundaryPx, glassPaddingPx)',
    'val transitionDistance = (registration.heightPx + glassPaddingPx).coerceAtLeast(1).toFloat()',
    '((headerBoundaryPx + transitionDistance - info.offset.toFloat()) / transitionDistance).coerceIn(0f, 1f)',
    'val overlayOffset = naturalOffset.coerceAtLeast(headerBoundaryPx.toFloat())',
    'val glassExtensionPx = if (pagerVisual == null) 0f else (pagerVisual.registration.heightPx + glassPaddingPx) * pagerVisual.progress',
    'val sourceLayer = rememberGraphicsLayer()',
    'val blurredLayer = rememberGraphicsLayer()',
    'AndroidRenderEffect.createBlurEffect(blurRadiusPx, blurRadiusPx, Shader.TileMode.CLAMP).asComposeRenderEffect()',
    'sourceLayer.record { this@drawWithContent.drawContent() }',
    'blurredLayer.record { drawLayer(sourceLayer) }',
    '.offset { IntOffset(0, pagerVisual.overlayOffsetPx.toInt()) }',
    '.alpha(pagerVisual.progress)',
]:
    need(token in lazy_page or token in main, f'progressive glass handoff contract missing: {token}')


need(main.count('((headerBoundaryPx + transitionDistance - info.offset.toFloat()) / transitionDistance).coerceIn(0f, 1f)') >= 2, 'live handoff progress formula must drive both page overlay and normal anchor')
need('AnimatedVisibility(' not in lazy_page, 'independent whole-pager AnimatedVisibility remains in lazy-page handoff')
need('stickyHeader(key = pagerKey)' not in sticky, 'pager anchor remains a stickyHeader')
need('item(key = pagerKey)' in sticky, 'normal-flow pager item anchor missing')
need('Modifier.zIndex(6f).alpha(1f - handoffProgress)' in sticky, 'normal-flow pager crossfade missing')
need('val handoffProgress by remember(' in sticky, 'normal-flow pager handoff progress missing')
need('pagerHeightPx + glassPaddingPx' in sticky, 'measured pager-height transition distance missing')
need('reportStickyPagerState(reporterKey, true, activeLaneExtent)' in lazy_page, 'moving pager lane extent is not reported')
need('PixelCopy' not in main and 'toImageBitmap(' not in lazy_page, 'forbidden framebuffer/bitmap glass path present')
need('drawRect(ComposeColor(0xFF23171D).copy(alpha = 0.72f))' in lazy_page, 'dense plum glass tint missing')
need('Brush.radialGradient(' in lazy_page and 'Brush.verticalGradient(' in lazy_page, 'smooth glass tint gradients missing')

for token in [
    'focusManager.clearFocus(force = true)',
    'label = "pageNumberTransition"',
    'SPIR-V™',
    'fileManagerSortModeIcon(candidate)',
]:
    need(token in main, f'retained UI contract missing: {token}')

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

need('Release 2.1.10 progressive pager-to-glass handoff and bounded backdrop requirements' in rules, 'PROJECT_RULES missing 2.1.10 contract')
for token in ['5.06-second', '0..1', 'GraphicsLayer', 'portrait and landscape', 'API 31']:
    need(token in audit or token in rules, f'2.1.10 evidence missing: {token}')
need(changelog.startswith('## 2.1.10\n'), '2.1.10 changelog entry missing or not current')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 2.1.10 progressive glass handoff contract')
