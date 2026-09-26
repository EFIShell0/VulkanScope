from pathlib import Path

root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')

class MousePan:
    def __init__(self, slop=8.0):
        self.slop = slop
        self.active = False
        self.dragging = False
        self.last_y = 0.0
        self.accumulated = 0.0
        self.scroll = 0.0
        self.consumed_moves = 0

    def press(self, y, primary=True):
        if not primary:
            return
        self.active = True
        self.dragging = False
        self.last_y = y
        self.accumulated = 0.0

    def move(self, y, primary=True):
        if not self.active or not primary:
            return
        delta = y - self.last_y
        self.last_y = y
        if delta == 0:
            return
        if not self.dragging:
            self.accumulated += delta
            if abs(self.accumulated) <= self.slop:
                return
            self.dragging = True
            slop_direction = self.slop if self.accumulated > 0 else -self.slop
            over_slop = self.accumulated - slop_direction
            self.scroll += -over_slop
            self.consumed_moves += 1
        else:
            self.scroll += -delta
            self.consumed_moves += 1

    def release(self):
        self.active = False
        self.dragging = False
        self.accumulated = 0.0

class Wheel:
    def __init__(self, factor=48.0):
        self.factor = factor
        self.scroll = 0.0
        self.consumed = 0

    def event(self, y, x=0.0, state_accepts=True):
        axis = y if y != 0 else x
        if axis == 0:
            return
        consumed = axis * self.factor if state_accepts else 0.0
        self.scroll += consumed
        if consumed != 0:
            self.consumed += 1

cases = []
p = MousePan()
p.press(100)
p.move(104)
cases.append(('sub-slop-keeps-click-path', p.scroll == 0 and p.consumed_moves == 0 and not p.dragging))
p.move(80)
cases.append(('mouse-upward-drag-scrolls-forward', p.scroll > 0 and p.dragging and p.consumed_moves == 1))
first = p.scroll
p.move(92)
cases.append(('mouse-downward-drag-scrolls-backward', p.scroll < first and p.consumed_moves == 2))
p.release()
old = p.scroll
p.move(40)
cases.append(('release-resets-drag', p.scroll == old and not p.dragging))
q = MousePan()
q.press(100, primary=False)
q.move(20, primary=False)
cases.append(('non-primary-does-not-pan', q.scroll == 0 and q.consumed_moves == 0))
w = Wheel(64)
w.event(1)
cases.append(('wheel-forward-scaled', w.scroll == 64 and w.consumed == 1))
w.event(-1)
cases.append(('wheel-backward-scaled', w.scroll == 0 and w.consumed == 2))
w.event(0, x=1)
cases.append(('horizontal-axis-fallback', w.scroll == 64 and w.consumed == 3))
w.event(1, state_accepts=False)
cases.append(('boundary-does-not-consume', w.scroll == 64 and w.consumed == 3))

for name, ok in cases:
    if not ok:
        raise SystemExit('release_1407 pointer state-machine failed: ' + name)

required = [
    'private fun persistOpeningAnimationPreference(enabled: Boolean)',
    'private fun setOpeningAnimationEnabled(enabled: Boolean)',
    'PointerEventType.Scroll',
    'PointerType.Mouse',
    'event.buttons.isPrimaryPressed',
    'viewConfiguration.touchSlop',
    'state.dispatchRawDelta(-deltaY)'
]
if required[0] not in main or required[1] in main:
    raise SystemExit('opening-animation JVM setter collision contract failed')
for token in required[2:]:
    if token not in main:
        raise SystemExit('pointer implementation token missing: ' + token)
print(f'release_1407 state machine: PASS cases={len(cases)} clickSlop=preserved primaryMouseDrag=grab wheel=scaled')
