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
audit = read('rules/3.0.1_UI_INPUT_LAYOUT_AUDIT.md')
changelog = read('changelog.md')

if not args.skip_version:
    need('versionCode = 3001' in gradle and 'versionName = "3.0.1"' in gradle, '3.0.1 release identity missing')

opening = block(main, '@Composable\nprivate fun VulkanScopeOpeningAnimation(', '@Composable\nprivate fun VulkanScopeApp(')
lazy = block(main, '@Composable\nprivate fun VulkanLazyPage(', '@Composable\nprivate fun SoftScrollIntersectionShadows')
selector = block(main, 'private fun PhysicalDeviceSelector(', 'private fun Modifier.consumeDesktopSecondaryMouseInput')
surface_cards = block(main, 'private fun SurfaceSectionCards(', '@Composable\nprivate fun SurfacePresentationPage')
shared = block(main, 'private fun SharedStorageBrowserDialog(', '@Composable\nprivate fun SharedStorageFolderRow')
evidence = block(main, 'private fun CapabilityKeyValue(', 'private fun evidenceStateAccent(')
secondary = block(main, 'private fun Modifier.consumeDesktopSecondaryMouseInput(', '@Composable\nprivate fun rememberPointerWheelScalePx')

need('phase >= 2 -> 1f' in opening, 'opening rule does not remain full scale after completion')
need('phase >= 3 -> 0.88f' not in opening and 'phase >= 4 -> 0.58f' not in opening, 'opening rule still shrinks in a later phase')
need(opening.count('val lineWidth = if (landscape) 180.dp else 142.dp') == 1, 'opening lineWidth declaration must be unique')
need('.width(lineWidth + 20.dp)' in opening, 'opening rule no longer spans the former endpoint area')
need(opening.count('.size(4.dp)') == 0, 'opening endpoint dots returned')

need('private val PageChromeSeparation = 12.dp' in main, '12 dp page/header separation constant missing')
need('PageChromeUnderlap' not in main, 'legacy page/header underlap remains')
need('val pageTopContentInset = headerContentInset + PageChromeSeparation' in lazy, 'lazy-page positive header separation missing')
need('val pageTopInset = headerContentInset + PageChromeSeparation' in selector, 'physical-device selector positive header separation missing')
need('val pageTopInset = headerContentInset + PageChromeSeparation' in surface_cards, 'surface landing positive header separation missing')
need('val headerBoundaryPx = with(density) { headerContentInset.roundToPx() }' in lazy, 'sticky pager clamp no longer uses the actual header boundary')

need('val landscapeLayout = maxWidth > maxHeight && maxWidth >= 700.dp' in shared, 'responsive landscape file-manager switch missing')
need('.weight(0.42f)' in shared and '.weight(0.58f).fillMaxHeight()' in shared, 'landscape two-pane allocation missing')
need('.desktopVerticalPointerScroll(controlsScrollState)' in shared and '.verticalScroll(controlsScrollState)' in shared, 'landscape control pane is not bounded-scrollable')
need('private fun SharedStorageBrowserListing(' in shared, 'shared file-manager listing was not isolated for full-height reuse')
need('modifier = Modifier.fillMaxSize().padding(8.dp)' in shared, 'landscape folder/file viewport is not full-height')
need('modifier = Modifier.weight(1f).fillMaxWidth()' in shared, 'portrait folder/file viewport no longer receives remaining height')

need('private fun Modifier.consumeDesktopSecondaryMouseInput(enabled: Boolean): Modifier' in main, 'desktop secondary-input interception missing')
need('awaitPointerEvent(PointerEventPass.Initial)' in secondary, 'secondary input is not intercepted at the Initial pointer pass')
need('secondarySequence || event.buttons.isSecondaryPressed' in secondary, 'complete secondary-button sequence is not consumed')
need('event.changes.forEach { change -> if (!change.isConsumed) change.consume() }' in secondary, 'secondary input changes are not consumed')
need('val suppressDesktopSecondaryInput = isChromeOsRuntime(this) || isAndroidPcFormFactor(this)' in main, 'app-wide ChromeOS/Android-PC secondary suppression missing')
need(main.count('.consumeDesktopSecondaryMouseInput(suppressDesktopSecondaryInput)') >= 3, 'secondary suppression is not applied to app and file-manager roots')
need('model' not in secondary.lower() and 'fingerprint' not in secondary.lower(), 'secondary-input behavior must not infer desktop identity heuristically')

need('val pressBorderAlpha by animateFloatAsState(if (pressed) 0.46f else 0f' in evidence, 'animated hold border alpha missing')
need('.border(1.dp, VulkanAccentSoft.copy(alpha = pressBorderAlpha), RoundedCornerShape(14.dp))' in evidence, 'hold border does not use file-manager accent tone')
need('val pressScale by animateFloatAsState(if (pressed) 0.985f else 1f' in evidence, 'existing press-in animation regressed')

need('(direct.offset - layoutInfo.viewportStartOffset).toFloat()' in lazy, 'pager direct viewport coordinate repair regressed')
need('(previous.offset + previous.size + verticalSpacingPx - layoutInfo.viewportStartOffset).toFloat()' in lazy, 'pager previous bridge repair regressed')
need('(next.offset - registration.heightPx - verticalSpacingPx - layoutInfo.viewportStartOffset).toFloat()' in lazy, 'pager next bridge repair regressed')
need('PixelCopy' not in main and 'toImageBitmap(' not in main, 'forbidden framebuffer/bitmap capture path present')
need('// ' not in main and '/*' not in main, 'source-code comment rule violated in MainActivity.kt')

need('## Release 3.0.1 opening-rule stability, landscape file-manager, desktop secondary-input and page-separation requirements' in rules, '3.0.1 project rules missing')
need('# VulkanScope 3.0.1 UI, Input and Layout Audit' in audit, '3.0.1 audit heading missing')
need(changelog.startswith('## 3.0.1\n'), '3.0.1 changelog entry missing or not first')

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
print('PASS VulkanScope 3.0.1 UI/input/layout verifier')
