#!/usr/bin/env python3

def visual(header, height, padding, item_offset, visible=True, first_after=False):
    distance = max(1.0, height + padding)
    if visible:
        progress = max(0.0, min(1.0, (header + distance - item_offset) / distance))
        natural = item_offset
    elif first_after:
        progress = 1.0
        natural = header
    else:
        progress = 0.0
        natural = header
    overlay = max(natural, header)
    glass = (height + padding) * progress
    return progress, overlay, glass

for header, height in [(102.0, 78.0), (72.0, 68.0)]:
    padding = 12.0
    distance = height + padding
    offsets = [header + distance + 8.0, header + distance, header + distance * 0.75, header + distance * 0.5, header + distance * 0.25, header, header - 20.0]
    states = [visual(header, height, padding, y) for y in offsets]
    progresses = [x[0] for x in states]
    if progresses != sorted(progresses):
        raise SystemExit('handoff progress is not monotonic while scrolling toward the header')
    if states[1][0] != 0.0 or abs(states[-2][0] - 1.0) > 1e-6:
        raise SystemExit('handoff endpoints are wrong')
    for progress, overlay, glass in states:
        if not 0.0 <= progress <= 1.0:
            raise SystemExit('handoff progress escaped bounds')
        if overlay < header:
            raise SystemExit('overlay crossed above live header boundary')
        if not 0.0 <= glass <= height + padding + 1e-6:
            raise SystemExit('glass extension exceeded pager lane bound')
    reverse = [visual(header, height, padding, y)[0] for y in reversed(offsets)]
    if reverse != sorted(reverse, reverse=True):
        raise SystemExit('reverse handoff is not continuous')
    pinned = visual(header, height, padding, header - 200.0, visible=False, first_after=True)
    if pinned[0] != 1.0 or pinned[1] != header:
        raise SystemExit('post-visibility pinned fallback failed')
print('PASS VulkanScope 2.1.10 portrait/landscape progressive handoff state machine')
