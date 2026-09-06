#!/usr/bin/env python3


def arrows(first_visible, last_visible, count):
    return first_visible > 0, count > 0 and last_visible < count - 1


def move_focus(focusable, focus_pos, direction, first_visible, capacity):
    target = focus_pos + direction
    target = max(0, min(target, len(focusable) - 1))
    item = focusable[target]
    last_visible = first_visible + capacity - 1
    if item < first_visible:
        first_visible = item
    elif item > last_visible:
        first_visible = item - capacity + 1
    first_visible = max(0, first_visible)
    return target, first_visible


lines = ['# Title', '', 'Intro', '- A', '- B', '', '## Section', 'Text', '- C', '> Note']
focusable = [index for index, line in enumerate(lines) if line.strip()]
capacity = 4
focus_pos = 0
first_visible = 0
for _ in range(len(focusable) - 1):
    focus_pos, first_visible = move_focus(focusable, focus_pos, 1, first_visible, capacity)
if focusable[focus_pos] != focusable[-1]:
    raise SystemExit('D-pad down did not reach the final release-note row')
if first_visible == 0:
    raise SystemExit('focus-driven navigation did not scroll the release-note viewport')
show_up, show_down = arrows(first_visible, min(len(lines) - 1, first_visible + capacity - 1), len(lines))
if not show_up or show_down:
    raise SystemExit('release-note boundary indicators are wrong at the bottom')
for _ in range(len(focusable) - 1):
    focus_pos, first_visible = move_focus(focusable, focus_pos, -1, first_visible, capacity)
if focusable[focus_pos] != focusable[0] or first_visible != 0:
    raise SystemExit('D-pad up did not restore the first release-note row and viewport')
show_up, show_down = arrows(0, min(len(lines) - 1, capacity - 1), len(lines))
if show_up or not show_down:
    raise SystemExit('release-note boundary indicators are wrong at the top')
if arrows(0, 0, 1) != (False, False):
    raise SystemExit('non-scrollable release notes must hide both boundary indicators')
print('PASS 0.80.5 release-notes D-pad focus/bring-into-view and boundary state machine')
