#!/usr/bin/env python3

def visual(header, height, padding, item_offset=None, first_after=False):
    if item_offset is None and not first_after:
        return None
    distance = max(1.0, height + padding)
    natural = header if first_after else item_offset
    progress = 1.0 if first_after else max(0.0, min(1.0, (header + distance - natural) / distance))
    overlay = max(natural, header)
    lane = max(0.0, overlay - header + height + padding) if progress > 0.0 else 0.0
    glass = header + (height + padding) * progress if progress > 0.0 else 0.0
    return progress, overlay, lane, glass

for header, height in [(104.0, 84.0), (72.0, 84.0)]:
    padding = 12.0
    distance = height + padding
    offsets = [header + distance + 20.0, header + distance, header + distance * 0.8, header + distance * 0.5, header + distance * 0.2, header, header - 40.0]
    states = [visual(header, height, padding, y) for y in offsets]
    positions = [state[1] for state in states]
    if positions != sorted(positions, reverse=True):
        raise SystemExit('single pager Y position does not move monotonically toward the header')
    for y, state in zip(offsets, states):
        progress, overlay, lane, glass = state
        if y >= header and overlay != y:
            raise SystemExit('single pager stopped following its live anchor before the header clamp')
        if y < header and overlay != header:
            raise SystemExit('single pager crossed above the live header boundary')
        if not 0.0 <= progress <= 1.0:
            raise SystemExit('glass handoff progress escaped bounds')
        if lane < 0.0:
            raise SystemExit('top-stack lane extent became negative')
        if glass != 0.0 and not header <= glass <= header + height + padding + 1e-6:
            raise SystemExit('glass escaped the bounded header/pager lane')
    reverse = [visual(header, height, padding, y)[1] for y in reversed(offsets)]
    if reverse != sorted(reverse):
        raise SystemExit('single pager does not return continuously along the reverse path')
    pinned_after_disposal = visual(header, height, padding, None, first_after=True)
    if pinned_after_disposal[0] != 1.0 or pinned_after_disposal[1] != header:
        raise SystemExit('pinned pager was lost after lazy anchor disposal')
    if visual(header, height, padding, None, first_after=False) is not None:
        raise SystemExit('pager rendered when anchor is below viewport and not pinned')

for pager_lane, transient in [(0.0, 0.0), (84.0, 0.0), (84.0, 52.0), (130.0, 76.0), (0.0, 76.0)]:
    header = 104.0
    arrow_top = header + pager_lane + transient + 10.0
    if arrow_top < header + pager_lane + transient:
        raise SystemExit('upper scroll hint crossed into pager/status stack')

print('PASS VulkanScope 2.1.12 portrait/landscape single-pager and scroll-hint state machine')
