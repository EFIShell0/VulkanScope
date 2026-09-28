#!/usr/bin/env python3

class PinnedPagerCoordinator:
    def __init__(self):
        self.heights = {}
    def report(self, key, pinned, height):
        if pinned and height > 0:
            self.heights[key] = height
        else:
            self.heights.pop(key, None)
    @property
    def inset(self):
        return max(self.heights.values(), default=0)

coordinator = PinnedPagerCoordinator()
assert coordinator.inset == 0
coordinator.report('a', True, 84)
assert coordinator.inset == 84
coordinator.report('b', False, 0)
assert coordinator.inset == 84
coordinator.report('b', True, 92)
assert coordinator.inset == 92
coordinator.report('b', False, 0)
assert coordinator.inset == 84
coordinator.report('a', False, 0)
assert coordinator.inset == 0

def lanes(header, pager, transient, gap=10, arrow_extent=40):
    pager_top = header
    status_top = header + pager
    arrow_top = status_top + transient + gap
    arrow_bottom = arrow_top + arrow_extent
    return pager_top, status_top, arrow_top, arrow_bottom

for header, pager, transient in [(92, 0, 0), (92, 82, 0), (92, 82, 48), (68, 96, 112)]:
    pager_top, status_top, arrow_top, arrow_bottom = lanes(header, pager, transient)
    assert pager_top == header
    assert status_top >= pager_top + pager
    assert arrow_top >= status_top + transient
    assert arrow_bottom > arrow_top

class PagerEditState:
    def __init__(self, page, count):
        self.page = page
        self.count = count
        self.focused = False
        self.field = str(page + 1)
        self.number_generation = 1
        self.surface_generation = 1
    def edit(self, value):
        self.focused = True
        self.field = value
    def arrow(self, delta):
        target = min(max(self.page + delta, 0), self.count - 1)
        self.focused = False
        self.field = str(target + 1)
        if target != self.page:
            self.page = target
            self.number_generation += 1

state = PagerEditState(2, 8)
state.edit('7')
state.arrow(1)
assert state.page == 3
assert state.field == '4'
assert not state.focused
assert state.number_generation == 2
assert state.surface_generation == 1
state.edit('1')
state.arrow(-1)
assert state.page == 2
assert state.field == '3'
assert state.number_generation == 3
assert state.surface_generation == 1

print('PASS VulkanScope 2.1.7 UI state-machine contract')
