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
audit = read('rules/3.0.0_EDGE_TO_EDGE_CHROME_OVERLAY_AUDIT.md')
changelog = read('changelog.md')

if not args.skip_version:
    need('versionCode = 3000' in gradle and 'versionName = "3.0.0"' in gradle, '3.0.0 release identity missing')

opening = block(main, '@Composable\nprivate fun VulkanScopeOpeningAnimation(', '@Composable\nprivate fun VulkanScopeApp(')
app = block(main, '@Composable\nprivate fun VulkanScopeApp(', '@Composable\nprivate fun SurfaceProbe')
lazy = block(main, '@Composable\nprivate fun VulkanLazyPage(', '@Composable\nprivate fun SoftScrollIntersectionShadows')
indicators = block(main, 'private fun ScrollBoundaryIndicatorColumn(', '@Composable\nprivate fun ScrollBoundaryIndicatorBubble')
system_nav = block(main, 'private fun SystemNavigationBackdrop(', '@Composable\nprivate fun CompactBottomNavigationBar(')
surface_cards = block(main, 'private fun SurfaceSectionCards(', '@Composable\nprivate fun SurfacePresentationPage')
selector = block(main, 'private fun PhysicalDeviceSelector(', '@Composable\nprivate fun rememberPointerWheelScalePx')

need('import androidx.activity.enableEdgeToEdge' in main, 'enableEdgeToEdge import missing')
need('enableEdgeToEdge()' in main, 'edge-to-edge enablement missing')
need('window.navigationBarColor = Color.TRANSPARENT' in main, 'navigation bar is not transparent')
need('window.isNavigationBarContrastEnforced = false' in main, 'three-button navigation contrast scrim is not disabled')
need('SystemNavigationBackdrop(' in app, 'system navigation glass host missing')
need('WindowInsets.navigationBars.asPaddingValues()' in system_nav, 'system navigation glass does not use live navigationBars insets')
need('.height(bottomInset)' in system_nav, 'bottom navigation-bar glass region missing')
need('.width(leftInset)' in system_nav and '.width(rightInset)' in system_nav, 'side navigation-bar glass regions missing')
need(system_nav.count('.frostedChromeBackdrop(backdropLayer, backdropRootOffset, targetRootOffset)') == 3, 'system navigation glass does not reuse shared frosted chrome backdrop')

need('private val PageChromeUnderlap = 10.dp' in main, '10 dp page/header underlap contract missing')
need('val pageTopContentInset = (headerContentInset - PageChromeUnderlap).coerceAtLeast(0.dp)' in lazy, 'lazy-page header underlap missing')
need('top = pageTopContentInset + coordinatedTransientOverlayInset' in lazy, 'lazy-page content padding does not use underlap')
need('val pageTopInset = (headerContentInset - PageChromeUnderlap).coerceAtLeast(0.dp)' in selector, 'physical-device selector underlap missing')
need('val pageTopInset = (headerContentInset - PageChromeUnderlap).coerceAtLeast(0.dp)' in surface_cards, 'surface landing underlap missing')
need('top = pageTopInset + coordinatedTopOverlayInset' in surface_cards, 'surface landing content does not use underlap')
need('val headerBoundaryPx = with(density) { headerContentInset.roundToPx() }' in lazy, 'sticky header boundary was incorrectly moved with content underlap')

need('.width(lineWidth + 20.dp)' in opening, 'opening accent rule was not extended across former endpoint span')
need(opening.count('.size(4.dp)') == 0, 'opening endpoint dots remain')
line_block = block(opening, '.width(lineWidth + 20.dp)', '        }\n    }\n}')
need('ComposeColor.Transparent' not in line_block, 'opening accent rule still fades to transparent before its full span')

need('private val OverlayCoordinationLead = 96.dp' in main, 'preemptive overlay coordination lead missing')
need('private const val OverlayCoordinationMotionMillis = 120' in main, 'prompt overlay coordination timing missing')
need('coordinationProgress' in lazy and 'overlayCoordinationLeadPx' in lazy, 'preemptive pager-lane coordination missing')
need('joinStartLaneExtent' in lazy and 'coordinationProgress > 0f ->' in lazy, 'pre-join lane reservation missing')
need('SideEffect {' in lazy, 'pager lane state is still coroutine-delayed')
need('animationSpec = tween(OverlayCoordinationMotionMillis, easing = FastOutSlowInEasing)' in lazy, 'scroll/overlay motion does not use shared prompt timing')
need('.zIndex(11f)' in app, 'transient status surfaces are not explicitly above pager/content crossing')
need('Box(modifier.zIndex(10f))' in indicators, 'scroll indicators are not explicitly above scrolling content/pager crossing')
need('.zIndex(9f)' in lazy, 'pager z-order contract changed')
need('(direct.offset - layoutInfo.viewportStartOffset).toFloat()' in lazy, '2.1.16 direct viewport coordinate repair regressed')
need('(previous.offset + previous.size + verticalSpacingPx - layoutInfo.viewportStartOffset).toFloat()' in lazy, '2.1.16 previous bridge repair regressed')
need('(next.offset - registration.heightPx - verticalSpacingPx - layoutInfo.viewportStartOffset).toFloat()' in lazy, '2.1.16 next bridge repair regressed')
need('PixelCopy' not in main and 'toImageBitmap(' not in main, 'forbidden framebuffer/bitmap capture path present')
need('// ' not in main and '/*' not in main, 'source-code comment rule violated in MainActivity.kt')

need('## Release 3.0.0 edge-to-edge chrome, header underlap, opening rule and overlay coordination requirements' in rules, '3.0.0 project rules missing')
need('# VulkanScope 3.0.0 Edge-to-edge Chrome and Overlay Coordination Audit' in audit, '3.0.0 audit heading missing')
need(changelog.startswith('## 3.0.0\n'), '3.0.0 changelog entry missing or not first')

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
print('PASS VulkanScope 3.0.0 edge-to-edge chrome and overlay verifier')
