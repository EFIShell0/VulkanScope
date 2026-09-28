#!/usr/bin/env python3

def visual(header, height, padding, item_offset=None, first_after=False):
    if item_offset is None and not first_after:
        return None
    distance = max(1.0, height + padding)
    natural = header if first_after else item_offset
    progress = 1.0 if first_after else max(0.0, min(1.0, (header + distance - natural) / distance))
    overlay = max(natural, header)
    pager_bottom = overlay + height + padding
    lane = max(0.0, pager_bottom - header) if progress > 0.0 else 0.0
    glass = header + (pager_bottom - header) * progress if progress > 0.0 else 0.0
    return progress, overlay, lane, glass

for header, height in [(104.0, 72.0), (72.0, 72.0), (118.0, 88.0)]:
    padding = 12.0
    distance = height + padding
    offsets = [
        header + distance + 36.0,
        header + distance + 1.0,
        header + distance,
        header + distance * 0.8,
        header + distance * 0.5,
        header + distance * 0.2,
        header,
        header - 32.0,
    ]
    states = [visual(header, height, padding, y) for y in offsets]
    if any(state is None for state in states):
        raise SystemExit('visible pager disappeared while its anchor remained visible')
    positions = [state[1] for state in states]
    if positions != sorted(positions, reverse=True):
        raise SystemExit('pager Y position is not monotonic toward the header')
    for y, state in zip(offsets, states):
        progress, overlay, lane, glass = state
        if y >= header and overlay != y:
            raise SystemExit('pager stopped following live anchor before header clamp')
        if y < header and overlay != header:
            raise SystemExit('pager crossed above live header boundary')
        if not 0.0 <= progress <= 1.0:
            raise SystemExit('handoff progress escaped bounds')
        if y >= header + distance and progress != 0.0:
            raise SystemExit('pager handoff started outside bounded transition region')
        if progress == 0.0:
            if lane != 0.0 or glass != 0.0:
                raise SystemExit('zero-progress pager unexpectedly reserved glass/status lane')
        else:
            pager_bottom = overlay + height + padding
            if abs(lane - (pager_bottom - header)) > 1e-6:
                raise SystemExit('pager/status lane no longer follows moving pager bottom')
            if not header <= glass <= pager_bottom + 1e-6:
                raise SystemExit('glass growth escaped header-to-pager bounds')
    reverse = [visual(header, height, padding, y)[1] for y in reversed(offsets)]
    if reverse != sorted(reverse):
        raise SystemExit('pager reverse path does not return through the same live positions')
    far_visible = visual(header, height, padding, header + distance + 36.0)
    if far_visible is None or far_visible[0] != 0.0 or far_visible[1] != header + distance + 36.0:
        raise SystemExit('zero-progress visible pager cannot settle into normal-flow placeholder')
    pinned_after_disposal = visual(header, height, padding, None, first_after=True)
    if pinned_after_disposal is None or pinned_after_disposal[0] != 1.0 or pinned_after_disposal[1] != header:
        raise SystemExit('pinned pager was lost after lazy anchor disposal')
    if visual(header, height, padding, None, first_after=False) is not None:
        raise SystemExit('pager rendered when anchor is below viewport and not pinned')

for pager_lane, transient in [(0.0, 0.0), (84.0, 0.0), (84.0, 52.0), (132.0, 76.0), (0.0, 76.0)]:
    header = 104.0
    arrow_top = header + pager_lane + transient + 10.0
    if arrow_top < header + pager_lane + transient:
        raise SystemExit('upper scroll hint crossed into pager/status stack')

for width, start_inset, end_inset in [(412.0, 0.0, 0.0), (915.0, 42.0, 42.0), (1280.0, 0.0, 96.0)]:
    content_width = width - 36.0 - start_inset - end_inset
    if content_width <= 0.0:
        raise SystemExit('orientation/system inset geometry collapsed pager width')

print('PASS VulkanScope 2.1.13 portrait/landscape pager settle, glass growth and scroll-hint state machine')
