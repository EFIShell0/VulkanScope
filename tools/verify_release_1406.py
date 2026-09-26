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
need(base_failures in [[], ['FAIL: adaptive Material 3 navigation contract missing']], 'retained 1.4.5 correctness/spec/security gate failed outside the explicitly superseded compact-navigation shape')

build = text('app/build.gradle.kts')
main = text('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
rules = text('rules/PROJECT_RULES.md')

compact_nav_match = re.search(r'bottomBar = \{\s*if \(!useRail\) \{(.*?)\n\s*\}\n\s*\},', main, re.S)
compact_nav = compact_nav_match.group(1) if compact_nav_match else ''
opening_match = re.search(r'@Composable\s+private fun VulkanScopeOpeningAnimation\((.*?)\n\}\n\n@Composable\s+private fun VulkanScopeApp', main, re.S)
opening_block = opening_match.group(1) if opening_match else ''
preference_match = re.search(r'CapabilitySectionCard\("Opening animation"\) \{(.*?)\n\s*\}\n\s*\}\n\s*item \{\s*CapabilitySectionCard\("Driver manager"\)', main, re.S)
preference_block = preference_match.group(1) if preference_match else ''

if not args.skip_version:
    need('versionCode = 1406' in build, 'versionCode is not 1406')
    need('versionName = "1.4.6"' in build, 'versionName is not 1.4.6')
    need('## Release 1.4.6 launch sequencing and compact navigation requirements' in rules, '1.4.6 rules section missing')

need('import androidx.compose.material3.NavigationBar\n' in main, 'Material 3 NavigationBar import missing')
need('import androidx.compose.material3.NavigationBarItem\n' in main, 'Material 3 NavigationBarItem import missing')
need('import androidx.compose.material3.NavigationBarItemDefaults\n' in main, 'Material 3 NavigationBarItemDefaults import missing')
need('ShortNavigationBar' not in main, 'compact navigation still uses ShortNavigationBar instead of the requested top-icon/label layout')
need(compact_nav_match is not None, 'compact bottom-navigation block is missing')
need('NavigationBar(' in compact_nav, 'compact Material 3 NavigationBar is missing')
need('NavigationBarItem(' in compact_nav, 'compact Material 3 NavigationBarItem is missing')
need('alwaysShowLabel = true' in compact_nav, 'compact navigation labels are not guaranteed visible')
need('.heightIn(min = 80.dp)' in compact_nav, 'compact navigation does not preserve the requested bottom-bar touch/layout height')
need('indicatorColor = VulkanAccentContainer' in compact_nav, 'compact navigation selected pill indicator is missing')

need('private var openingAnimationEnabled by mutableStateOf(true)' in main, 'opening-animation preference state missing')
need('private var startupGateOpen by mutableStateOf(false)' in main, 'startup collection gate state missing')
need('prefs.getBoolean("opening_animation_enabled", true)' in main, 'opening animation is not default-enabled from persistent preferences')
need('putBoolean("opening_animation_enabled", enabled)' in main, 'opening-animation switch does not persist directly')
need('private fun setOpeningAnimationEnabled(enabled: Boolean)' in main, 'direct opening-animation preference setter missing')
need('private fun completeOpeningSequence()' in main, 'opening sequence completion gate missing')
need(re.search(r'private fun requestReportCollection\(\) \{\s*if \(!startupGateOpen \|\| !activityStarted\) return', main) is not None, 'report collection is not blocked before the opening sequence completes')
need(re.search(r'private fun requestQueryGroup\(group: String\) \{\s*if \(!startupGateOpen\) return', main) is not None, 'lazy Vulkan query path is not blocked before opening completion')
need('if (startupGateOpen) startRuntimeObservers()' in main, 'startup-bound display/network observers are not gated')
need('if (!startupGateOpen) return' in main, 'startup gate is not enforced by runtime entry paths')
need('startupPostAnimationWorkStarted' in main, 'post-animation startup work idempotence state missing')
need(opening_match is not None and 'VulkanScopeOpeningAnimation' in main, 'custom VulkanScope opening animation is missing')
need('R.drawable.vulkanscope_logo_horizontal' in opening_block, 'opening animation does not use the packaged VulkanScope logo')
need('LaunchedEffect(startAnimation)' in opening_block and 'if (!startAnimation) return@LaunchedEffect' in opening_block, 'custom opening motion can start before the platform splash has exited')
need('platformSplashExited = true' in main and 'VulkanScopeOpeningAnimation(startAnimation = platformSplashExited' in main, 'platform splash handoff is not wired to the custom opening animation')
need('if (startupGateOpen || !openingAnimationEnabled) {\n            VulkanScopeApp(' in main, 'main application UI is composed before the enabled opening sequence completes')
need('savedInstanceState?.getBoolean("startup_gate_open", false) == true' in main and 'outState.putBoolean("startup_gate_open", startupGateOpen)' in main, 'startup gate completion is not preserved safely across Activity recreation')
need(preference_match is not None and 'ExpressiveSwitch(checked = openingAnimationEnabled, onCheckedChange = onOpeningAnimationChanged)' in preference_block, 'Preferences opening-animation switch is missing or is not direct-toggle')
need('Applies on the next cold launch' in preference_block, 'opening-animation preference behavior is not disclosed')

surface_block = re.search(r'surfaceReady = \{ hostGeneration, surface ->(.*?)surfaceDestroyed =', main, re.S)
need(surface_block is not None and 'startupGateOpen' in surface_block.group(1), 'surface-ready startup collection can still start before animation completion')
complete_block = re.search(r'private fun completeOpeningSequence\(\) \{(.*?)\n    \}', main, re.S)
if complete_block is not None:
    body = complete_block.group(1)
    need('startupGateOpen = true' in body, 'opening completion does not open startup gate')
    need('startRuntimeObservers()' in body, 'opening completion does not start deferred runtime observers')
observer_block = re.search(r'private fun startRuntimeObservers\(\) \{(.*?)\n    \}', main, re.S)
if observer_block is not None:
    observer_body = observer_block.group(1)
    need('requestReportCollection()' in observer_body, 'deferred runtime startup does not start Vulkan collection when Surface is ready')
    need('checkForApplicationUpdate(showProgress = false)' in observer_body, 'startup update check is not deferred behind the opening gate')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('release_1406 verifier: PASS compactNavigation=NavigationBar openingAnimation=direct-toggle startupCollectionGate=deferred')
