#!/usr/bin/env python3

class Registration:
    def __init__(self, key, item_index, height, layout_count):
        self.key = key
        self.item_index = item_index
        self.height = height
        self.layout_count = layout_count


def is_pinned(registration, header_boundary, can_scroll_backward, visible_offset, first_index, first_offset, current_layout_count):
    if not can_scroll_backward:
        return False
    if registration.item_index < 0 or registration.height <= 0:
        return False
    if registration.layout_count != current_layout_count:
        return False
    if visible_offset is not None:
        return visible_offset <= header_boundary
    if first_index > registration.item_index:
        return True
    if first_index == registration.item_index:
        return first_offset > 0
    return False


def choose_active(registrations, offsets, header_boundary, can_scroll_backward, first_index, first_offset, current_layout_count):
    eligible = [
        registration
        for registration in registrations
        if is_pinned(
            registration,
            header_boundary,
            can_scroll_backward,
            offsets.get(registration.key),
            first_index,
            first_offset,
            current_layout_count,
        )
    ]
    return max(eligible, key=lambda item: item.item_index, default=None)

for header_boundary in (116, 72):
    pager = Registration('collection-pager', 5, 88, 31)
    assert not is_pinned(pager, header_boundary, False, header_boundary + 180, 0, 0, 31)
    assert not is_pinned(pager, header_boundary, True, header_boundary + 1, 4, 20, 31)
    assert is_pinned(pager, header_boundary, True, header_boundary, 4, 20, 31)
    assert is_pinned(pager, header_boundary, True, header_boundary - 18, 5, 4, 31)
    assert is_pinned(pager, header_boundary, True, None, 6, 0, 31)
    assert is_pinned(pager, header_boundary, True, None, 5, 12, 31)
    assert not is_pinned(pager, header_boundary, True, header_boundary + 2, 4, 20, 31)
    assert not is_pinned(pager, header_boundary, True, None, 4, 200, 31)
    assert not is_pinned(pager, header_boundary, True, None, 6, 0, 30)

first = Registration('first', 5, 80, 44)
second = Registration('second', 19, 94, 44)
active = choose_active([first, second], {'first': None, 'second': None}, 96, True, 24, 0, 44)
assert active is second
active = choose_active([first, second], {'first': None, 'second': 130}, 96, True, 18, 8, 44)
assert active is first

class OverlayLane:
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

lane = OverlayLane()
lane.report('page-a@host-a', True, 88)
assert lane.inset == 88
lane.report('page-b@host-b', False, 0)
assert lane.inset == 88
lane.report('page-a@host-a', False, 0)
assert lane.inset == 0

class PagerEditState:
    def __init__(self, page, count):
        self.page = page
        self.count = count
        self.focused = False
        self.field = str(page + 1)
        self.surface_generation = 1
        self.number_generation = 1
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

edit = PagerEditState(2, 20)
edit.edit('17')
edit.arrow(1)
assert edit.page == 3
assert edit.field == '4'
assert not edit.focused
assert edit.surface_generation == 1
assert edit.number_generation == 2

print('PASS VulkanScope 2.1.9 clipping-independent pager state-machine contract')
