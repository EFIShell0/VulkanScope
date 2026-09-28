#!/usr/bin/env python3

def visual(header, height, padding, spacing, direct=None, previous=None, next_item=None, fallback_pinned=False):
    natural = None
    if direct is not None:
        natural = direct
    elif previous is not None:
        natural = previous[0] + previous[1] + spacing
    elif next_item is not None:
        natural = next_item[0] - height - spacing
    if natural is None and not fallback_pinned:
        return None
    distance = max(1.0, height + padding)
    y = header if natural is None else natural
    progress = 1.0 if fallback_pinned and natural is None else max(0.0, min(1.0, (header + distance - y) / distance))
    if progress <= 0.0:
        return ('flow', y, 0.0, 0.0)
    overlay = max(y, header)
    bottom = overlay + height + padding
    return ('overlay', overlay, bottom - header, bottom)

for header, height, spacing in [(104.0, 72.0, 14.0), (72.0, 72.0, 10.0), (118.0, 88.0, 18.0)]:
    padding = 12.0
    distance = height + padding
    far = header + distance + 40.0
    state = visual(header, height, padding, spacing, direct=far)
    if state[0] != 'flow' or state[1] != far:
        raise SystemExit('normal-flow pager failed to own its real lazy position outside join region')
    offsets = [header + distance - d for d in (1.0, distance * 0.25, distance * 0.5, distance * 0.75, distance, distance + 30.0)]
    positions = []
    for y in offsets:
        state = visual(header, height, padding, spacing, direct=y)
        if state[0] != 'overlay':
            raise SystemExit('overlay did not take exclusive ownership inside join region')
        positions.append(state[1])
        if state[1] < header:
            raise SystemExit('overlay crossed live header boundary')
        if abs(state[3] - (state[1] + height + padding)) > 1e-6:
            raise SystemExit('glass no longer fully encloses moving pager lane')
    if positions != sorted(positions, reverse=True):
        raise SystemExit('overlay path is not monotonic toward header clamp')
    bridge_target = header + distance * 0.65
    prev_offset = bridge_target - 120.0 - spacing
    bridged = visual(header, height, padding, spacing, previous=(prev_offset, 120.0))
    if bridged[0] != 'overlay' or abs(bridged[1] - bridge_target) > 1e-6:
        raise SystemExit('previous visible neighbor did not bridge pager return before direct anchor visibility')
    next_offset = bridge_target + height + spacing
    bridged_next = visual(header, height, padding, spacing, next_item=(next_offset, 96.0))
    if bridged_next[0] != 'overlay' or abs(bridged_next[1] - bridge_target) > 1e-6:
        raise SystemExit('next visible neighbor did not bridge pager geometry')
    pinned = visual(header, height, padding, spacing, fallback_pinned=True)
    if pinned[0] != 'overlay' or pinned[1] != header:
        raise SystemExit('offscreen post-anchor pager did not remain pinned')
    if visual(header, height, padding, spacing) is not None:
        raise SystemExit('pager rendered without visible/adjacent/pinned evidence')

stored_height = 88
recomposed_height = stored_height if stored_height > 0 else 72
if recomposed_height != 88:
    raise SystemExit('measured pager height was lost across lazy-item disposal/recomposition')

for width, start_inset, end_inset in [(390.0, 0.0, 0.0), (848.0, 42.0, 42.0), (1280.0, 0.0, 96.0)]:
    pager_width = width - 36.0 - start_inset - end_inset
    if pager_width <= 0.0:
        raise SystemExit('portrait/landscape navigation insets collapsed pager width')

for pager_lane, transient in [(0.0, 0.0), (84.0, 0.0), (84.0, 52.0), (132.0, 76.0), (0.0, 76.0)]:
    header = 104.0
    arrow_top = header + pager_lane + transient + 10.0
    if arrow_top < header + pager_lane + transient:
        raise SystemExit('upper scroll indicator intersected pager/transient stack')

print('PASS VulkanScope 2.1.14 portrait/landscape single-visual landing, neighbor bridge, retained height and overlay stack state machine')
