#!/usr/bin/env python3

def pinned_state(can_scroll_backward, info_offset, pager_index, first_index, first_offset):
    if not can_scroll_backward:
        return False
    if info_offset is not None:
        return info_offset <= 0
    return pager_index >= 0 and (first_index > pager_index or (first_index == pager_index and first_offset > 0))

assert not pinned_state(False, 0, 3, 0, 0)
assert not pinned_state(True, 120, 3, 2, 40)
assert pinned_state(True, 0, 3, 3, 0)
assert pinned_state(True, None, 3, 4, 0)
assert pinned_state(True, None, 3, 3, 1)
assert not pinned_state(True, None, -1, 8, 20)
assert not pinned_state(True, None, 3, 2, 200)

def lane_positions(header, transient, arrow_top_gap=10, arrow_extent=40, pager_gap=8):
    status_top = header
    arrow_top = header + transient + arrow_top_gap
    arrow_bottom = arrow_top + arrow_extent
    pager_top = header + transient + arrow_top_gap + arrow_extent + pager_gap
    return status_top, arrow_top, arrow_bottom, pager_top

for header, transient in [(92, 0), (92, 46), (68, 112)]:
    status_top, arrow_top, arrow_bottom, pager_top = lane_positions(header, transient)
    assert arrow_top >= status_top + transient
    assert pager_top > arrow_bottom
    assert pager_top == header + transient + 58

class PagerVisualState:
    def __init__(self, page):
        self.page = page
        self.surface_generation = 1
        self.number_generation = 1
    def change_page(self, page):
        if page != self.page:
            self.page = page
            self.number_generation += 1

state = PagerVisualState(0)
state.change_page(1)
assert state.page == 1
assert state.number_generation == 2
assert state.surface_generation == 1
state.change_page(1)
assert state.number_generation == 2
assert state.surface_generation == 1

print('PASS VulkanScope 2.1.6 UI state-machine contract')
