import argparse
import re
import subprocess
import sys
from pathlib import Path

ap = argparse.ArgumentParser()
ap.add_argument('--root', default='.')
ap.add_argument('--skip-version', action='store_true')
args = ap.parse_args()
root = Path(args.root).resolve()
errors = []

def need(condition, message):
    if not condition:
        errors.append(message)

def text(relative):
    return (root / relative).read_text(encoding='utf-8')

for relative in [
    'app/build.gradle.kts',
    'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt',
    'rules/PROJECT_RULES.md',
    'tools/verify_release_1405.py'
]:
    need((root / relative).is_file(), f'missing {relative}')
if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)

base = subprocess.run(
    [sys.executable, str(root / 'tools/verify_release_1405.py'), '--root', str(root), '--skip-version'],
    text=True,
    stdout=subprocess.PIPE,
    stderr=subprocess.STDOUT
)
base_failures = [line.strip() for line in base.stdout.splitlines() if line.startswith('FAIL:')]
allowed_base_failures = {
    'FAIL: adaptive Material 3 navigation contract missing',
    'FAIL: navigation rail is not scrollable/focus grouped'
}
need(set(base_failures).issubset(allowed_base_failures), 'retained 1.4.5 correctness/spec/security gate failed outside superseded navigation source-shape checks')

build = text('app/build.gradle.kts')
main = text('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
rules = text('rules/PROJECT_RULES.md')

if not args.skip_version:
    need('versionCode = 1407' in build, 'versionCode is not 1407')
    need('versionName = "1.4.7"' in build, 'versionName is not 1.4.7')
    need('## Release 1.4.7 compile repair and mouse pointer scrolling requirements' in rules, '1.4.7 rules section missing')

need('private var openingAnimationEnabled by mutableStateOf(true)' in main, 'opening-animation state missing')
need('private fun setOpeningAnimationEnabled(enabled: Boolean)' not in main, 'JVM setter clash reintroduced: setOpeningAnimationEnabled(Boolean)')
need('private fun persistOpeningAnimationPreference(enabled: Boolean)' in main, 'non-conflicting opening-animation persistence helper missing')
need('onOpeningAnimationChanged = { enabled -> persistOpeningAnimationPreference(enabled) }' in main, 'opening-animation switch is not wired to the non-conflicting persistence helper')
need('prefs.edit().putBoolean("opening_animation_enabled", enabled).apply()' in main, 'opening-animation preference persistence changed')
need('ExpressiveSwitch(checked = openingAnimationEnabled, onCheckedChange = onOpeningAnimationChanged)' in main, 'opening-animation direct switch behavior changed')

for token in [
    'import androidx.compose.foundation.gestures.ScrollableState',
    'import androidx.compose.ui.input.pointer.PointerEventPass',
    'import androidx.compose.ui.input.pointer.PointerEventType',
    'import androidx.compose.ui.input.pointer.PointerType',
    'import androidx.compose.ui.input.pointer.isPrimaryPressed',
    'private fun rememberPointerWheelScalePx(): Float',
    'android.view.ViewConfiguration.get(context).scaledVerticalScrollFactor.coerceAtLeast(1f)',
    'private fun Modifier.desktopVerticalPointerScroll(state: ScrollableState, wheelScalePx: Float): Modifier',
    'if (event.type != PointerEventType.Scroll) continue',
    'val axis = if (change.scrollDelta.y != 0f) change.scrollDelta.y else change.scrollDelta.x',
    'val consumed = state.dispatchRawDelta(axis * wheelScalePx)',
    'if (consumed != 0f) change.consume()',
    'change.type == PointerType.Mouse',
    'event.buttons.isPrimaryPressed',
    'val slop = viewConfiguration.touchSlop',
    'if (kotlin.math.abs(accumulatedY) <= slop) continue',
    'state.dispatchRawDelta(-overSlop)',
    'state.dispatchRawDelta(-deltaY)',
    'mouse.consume()'
]:
    need(token in main, 'mouse pointer scrolling contract missing: ' + token)

need('if (!event.buttons.isPrimaryPressed || !mouse.pressed)' in main, 'mouse drag is not restricted to a held primary button')
need(main.count('mouse.consume()') >= 2, 'mouse drag movement is not consistently consumed after slop')
need(main.count('desktopVerticalPointerScroll(') >= 12, 'mouse pointer scrolling is not applied broadly enough to vertical scroll surfaces')
need('Modifier.fillMaxSize().desktopVerticalPointerScroll(listState, pointerWheelScalePx).focusGroup()' in main, 'primary VulkanLazyPage does not expose mouse wheel/drag scrolling')
need('.desktopVerticalPointerScroll(railScrollState, pointerWheelScalePx).verticalScroll(railScrollState).focusGroup()' in main, 'navigation rail lost scroll/focus behavior while adding mouse input')
need('.desktopVerticalPointerScroll(scrollState).verticalScroll(scrollState).focusGroup()' in main, 'detail dialog pointer scrolling missing')
need('Modifier.fillMaxSize().desktopVerticalPointerScroll(gridState)' in main, 'lazy grid pointer scrolling missing')
need('Modifier.fillMaxWidth().desktopVerticalPointerScroll(listState).focusGroup()' in main, 'secondary lazy-list pointer scrolling missing')

for retained in [
    'private var startupGateOpen by mutableStateOf(false)',
    'prefs.getBoolean("opening_animation_enabled", true)',
    'R.drawable.vulkanscope_logo_horizontal',
    'if (startupGateOpen || !openingAnimationEnabled) {\n            VulkanScopeApp(',
    'private fun requestReportCollection() {\n        if (!startupGateOpen || !activityStarted) return',
    'private fun requestQueryGroup(group: String) {\n        if (!startupGateOpen) return'
]:
    need(retained in main, 'retained 1.4.6 startup/animation contract changed: ' + retained)

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print(f'release_1407 verifier: PASS setterClash=removed pointerScrollSites={main.count("desktopVerticalPointerScroll(")} wheelScale=android mouseDrag=primary+slop')
