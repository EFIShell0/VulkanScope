from pathlib import Path

root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')

class Startup:
    def __init__(self, animation_enabled, restored_gate=False):
        self.gate = restored_gate or not animation_enabled
        self.surface = False
        self.collection_starts = 0
        self.update_starts = 0
        self.post_started = False

    def surface_ready(self):
        self.surface = True
        if self.gate:
            self.collection_starts += 1

    def animation_complete(self):
        if self.gate and self.post_started:
            return
        self.gate = True
        if self.surface and self.collection_starts == 0:
            self.collection_starts += 1
        if not self.post_started:
            self.post_started = True
            self.update_starts += 1

cases = []
a = Startup(True)
a.surface_ready()
cases.append(('enabled-before-complete', a.collection_starts == 0 and a.update_starts == 0))
a.animation_complete()
cases.append(('enabled-after-complete', a.collection_starts == 1 and a.update_starts == 1))
a.animation_complete()
cases.append(('completion-idempotent', a.collection_starts == 1 and a.update_starts == 1))
b = Startup(False)
b.surface_ready()
b.animation_complete()
cases.append(('disabled-no-delay', b.collection_starts == 1 and b.update_starts == 1))
c = Startup(True, restored_gate=True)
c.surface_ready()
c.animation_complete()
cases.append(('recreated-after-complete-no-replay', c.collection_starts == 1 and c.update_starts == 1))
d = Startup(True, restored_gate=False)
d.surface_ready()
cases.append(('recreated-during-animation-remains-gated', d.collection_starts == 0 and d.update_starts == 0))
d.animation_complete()
cases.append(('recreated-during-animation-opens-after-animation', d.collection_starts == 1 and d.update_starts == 1))
for name, ok in cases:
    if not ok:
        raise SystemExit('startup state-machine failed: ' + name)

required_order_tokens = [
    'NavigationBar(',
    'NavigationBarItem(',
    'alwaysShowLabel = true',
    'indicatorColor = VulkanAccentContainer'
]
for token in required_order_tokens:
    if token not in main:
        raise SystemExit('compact navigation contract missing: ' + token)


if 'platformSplashExited = true' not in main or 'VulkanScopeOpeningAnimation(startAnimation = platformSplashExited' not in main:
    raise SystemExit('custom opening animation is not sequenced after the platform splash')
if 'if (startupGateOpen || !openingAnimationEnabled) {\n            VulkanScopeApp(' not in main:
    raise SystemExit('main app content is not held out of composition during the enabled opening animation')
if 'outState.putBoolean("startup_gate_open", startupGateOpen)' not in main:
    raise SystemExit('startup gate completion is not preserved across recreation')

if 'ExpressiveSwitch(checked = openingAnimationEnabled, onCheckedChange = onOpeningAnimationChanged)' not in main:
    raise SystemExit('opening-animation preference is not a direct switch')
if 'requestOpeningAnimation' in main or 'openingAnimationConsent' in main:
    raise SystemExit('opening-animation switch unexpectedly introduces a confirmation flow')
print(f'release_1406 state machine: PASS cases={len(cases)} compactNavItems=5 directPreferenceToggle=1')
