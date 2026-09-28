#!/usr/bin/env python3

def overlay_state(header, height, padding, spacing, viewport_start, direct=None, previous=None, next_item=None, first_index=0, first_offset=0, pager_index=4):
    natural = None
    if direct is not None:
        natural = direct - viewport_start
    elif previous is not None:
        natural = previous[0] + previous[1] + spacing - viewport_start
    elif next_item is not None:
        natural = next_item[0] - height - spacing - viewport_start
    passed = natural is None and (first_index > pager_index or (first_index == pager_index and first_offset > 0))
    if natural is None and not passed:
        return None
    resolved = header if natural is None else natural
    distance = max(1.0, height + padding)
    progress = 1.0 if passed and natural is None else max(0.0, min(1.0, (header + distance - resolved) / distance))
    overlay = max(resolved, header)
    pager_bottom = overlay + height
    lane = max(0.0, pager_bottom + padding - header) if progress > 0.0 else 0.0
    glass_bottom = header + distance * progress if progress > 0.0 else header
    return overlay, progress, lane, glass_bottom

scenarios = [
    (104.0, 72.0, 12.0, 7.0, -104.0),
    (72.0, 72.0, 12.0, 7.0, -96.0),
    (118.0, 88.0, 12.0, 11.0, -170.0),
    (104.0, 72.0, 12.0, 7.0, -156.0),
]
for header, height, padding, spacing, viewport_start in scenarios:
    physical_target = header + height + padding + 240.0
    lazy_offset = physical_target + viewport_start
    state = overlay_state(header, height, padding, spacing, viewport_start, direct=lazy_offset)
    if state is None or abs(state[0] - physical_target) > 1e-6:
        raise SystemExit('direct anchor did not land at its physical spacer coordinate')
    raw_wrong = lazy_offset
    if abs(raw_wrong - physical_target) < 1e-6:
        raise SystemExit('test scenario did not model non-zero before-content padding')
    if abs((physical_target - raw_wrong) + viewport_start) > 1e-6:
        raise SystemExit('modeled predecessor displacement is not exactly the omitted viewport-start correction')

    bridge_target = header + height + padding + 96.0
    previous_local_top = bridge_target + viewport_start - 120.0 - spacing
    prev = overlay_state(header, height, padding, spacing, viewport_start, previous=(previous_local_top, 120.0))
    if prev is None or abs(prev[0] - bridge_target) > 1e-6:
        raise SystemExit('previous-item bridge did not land at exact viewport coordinate')
    next_local_top = bridge_target + viewport_start + height + spacing
    nxt = overlay_state(header, height, padding, spacing, viewport_start, next_item=(next_local_top, 96.0))
    if nxt is None or abs(nxt[0] - bridge_target) > 1e-6:
        raise SystemExit('next-item bridge did not land at exact viewport coordinate')

    join_distance = height + padding
    for step in range(13):
        physical = header + join_distance + 48.0 - step * (join_distance + 48.0) / 12.0
        local = physical + viewport_start
        state = overlay_state(header, height, padding, spacing, viewport_start, direct=local)
        if state is None:
            raise SystemExit('pager disappeared while corrected anchor remained observable')
        expected = max(physical, header)
        if abs(state[0] - expected) > 1e-6:
            raise SystemExit('pager did not follow corrected physical coordinate into header clamp')

    pinned = overlay_state(header, height, padding, spacing, viewport_start, first_index=9, pager_index=4)
    if pinned is None or pinned[0] != header or pinned[1] != 1.0:
        raise SystemExit('offscreen passed pager did not remain pinned at live header boundary')

for viewport_start in (-72.0, -104.0, -156.0, -220.0):
    local_anchor = 500.0 + viewport_start
    state = overlay_state(104.0, 72.0, 12.0, 7.0, viewport_start, direct=local_anchor)
    if state is None or abs(state[0] - 500.0) > 1e-6:
        raise SystemExit('dynamic top-content padding changed the physical pager landing coordinate')

for width, start_inset, end_inset in [(390.0, 0.0, 0.0), (848.0, 42.0, 42.0), (1280.0, 0.0, 96.0)]:
    pager_width = width - 36.0 - start_inset - end_inset
    if pager_width <= 0.0:
        raise SystemExit('portrait/landscape navigation insets collapsed pager width')

print('PASS VulkanScope 2.1.16 portrait/landscape lazy viewport pager alignment state machine')
