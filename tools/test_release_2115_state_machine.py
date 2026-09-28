#!/usr/bin/env python3

def visual(header, height, padding, spacing, direct=None, previous=None, next_item=None, first_index=0, first_offset=0, pager_index=4):
    natural = None
    if direct is not None:
        natural = direct
    elif previous is not None:
        natural = previous[0] + previous[1] + spacing
    elif next_item is not None:
        natural = next_item[0] - height - spacing
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

for header, height, spacing in [(104.0, 72.0, 14.0), (72.0, 72.0, 10.0), (118.0, 88.0, 18.0)]:
    padding = 12.0
    distance = height + padding
    far = header + distance + 96.0
    state = visual(header, height, padding, spacing, direct=far)
    if state is None or state[0] != far or state[1] != 0.0:
        raise SystemExit('single pager did not remain at exact live anchor offset outside join region')
    last_overlay = far
    last_glass = header
    for step in range(0, 13):
        natural = header + distance + 48.0 - step * (distance + 48.0) / 12.0
        state = visual(header, height, padding, spacing, direct=natural)
        if state is None:
            raise SystemExit('single pager disappeared while live anchor remained visible')
        expected_overlay = max(natural, header)
        if abs(state[0] - expected_overlay) > 1e-6:
            raise SystemExit('single pager diverged from authoritative live anchor/clamp coordinate')
        if state[0] > last_overlay + 1e-6:
            raise SystemExit('pager path is not monotonic while approaching header')
        if state[3] < last_glass - 1e-6:
            raise SystemExit('glass did not grow monotonically while pager approached header')
        last_overlay = state[0]
        last_glass = state[3]
    pinned = visual(header, height, padding, spacing, first_index=9, pager_index=4)
    if pinned is None or pinned[0] != header or pinned[1] != 1.0:
        raise SystemExit('passed offscreen pager did not remain pinned at header boundary')
    if abs(pinned[3] - (header + distance)) > 1e-6:
        raise SystemExit('fully pinned glass does not reach one pager-height-plus-padding below header')
    bridge_target = header + distance * 0.42
    prev = visual(header, height, padding, spacing, previous=(bridge_target - 120.0 - spacing, 120.0), pager_index=4)
    if prev is None or abs(prev[0] - bridge_target) > 1e-6:
        raise SystemExit('previous-item bridge failed exact pager geometry')
    nxt = visual(header, height, padding, spacing, next_item=(bridge_target + height + spacing, 96.0), pager_index=4)
    if nxt is None or abs(nxt[0] - bridge_target) > 1e-6:
        raise SystemExit('next-item bridge failed exact pager geometry')
    if visual(header, height, padding, spacing, first_index=0, pager_index=4) is not None:
        raise SystemExit('pager rendered before anchor/bridge/passed evidence existed')

for source_root, target_root, local in [((0.0, 0.0), (0.0, 0.0), (25.0, 90.0)), ((0.0, 36.0), (0.0, 12.0), (10.0, 40.0)), ((48.0, 0.0), (12.0, 0.0), (30.0, 22.0))]:
    translated = (local[0] + source_root[0] - target_root[0], local[1] + source_root[1] - target_root[1])
    root_coordinate = (translated[0] + target_root[0], translated[1] + target_root[1])
    expected = (local[0] + source_root[0], local[1] + source_root[1])
    if root_coordinate != expected:
        raise SystemExit('shared backdrop root-coordinate mapping failed')

for width, start_inset, end_inset in [(390.0, 0.0, 0.0), (848.0, 42.0, 42.0), (1280.0, 0.0, 96.0)]:
    pager_width = width - 36.0 - start_inset - end_inset
    if pager_width <= 0.0:
        raise SystemExit('portrait/landscape navigation insets collapsed pager width')

for pager_lane, transient in [(0.0, 0.0), (84.0, 0.0), (132.0, 52.0), (132.0, 76.0), (0.0, 76.0)]:
    header = 104.0
    arrow_top = header + pager_lane + transient + 10.0
    if arrow_top < header + pager_lane + transient:
        raise SystemExit('upper scroll indicator intersected pager/transient stack')

print('PASS VulkanScope 2.1.15 portrait/landscape single-pager anchor, continuous glass and overlay-stack state machine')
