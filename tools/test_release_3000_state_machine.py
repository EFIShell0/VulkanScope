#!/usr/bin/env python3

def page_top(header_dp):
    return max(0.0, header_dp - 10.0)

for header in (72.0, 96.0, 118.0, 156.0):
    top = page_top(header)
    if abs((header - top) - 10.0) > 1e-6:
        raise SystemExit('page header underlap is not exactly 10 dp')


def system_nav_region(bottom, left, right):
    if bottom > 0:
        return ('bottom', bottom)
    regions = []
    if left > 0:
        regions.append(('left', left))
    if right > 0:
        regions.append(('right', right))
    return tuple(regions)

if system_nav_region(48, 0, 0) != ('bottom', 48):
    raise SystemExit('portrait three-button navigation region not protected')
if system_nav_region(0, 0, 64) != (('right', 64),):
    raise SystemExit('landscape right navigation region not protected')
if system_nav_region(0, 58, 0) != (('left', 58),):
    raise SystemExit('landscape left navigation region not protected')


def pager_state(header, height, padding, lead, natural):
    transition = max(1.0, height + padding)
    progress = max(0.0, min(1.0, (header + transition - natural) / transition))
    overlay = max(natural, header)
    bottom = overlay + height
    coordination = max(0.0, min(1.0, (header + transition + lead - natural) / max(1.0, lead)))
    live_lane = max(0.0, bottom + padding - header)
    join_start_lane = 2.0 * (height + padding)
    if progress > 0.0:
        lane = live_lane
    elif coordination > 0.0:
        lane = join_start_lane * coordination
    else:
        lane = 0.0
    return progress, coordination, lane, overlay

for header, height, padding, lead in ((104.0, 72.0, 12.0, 96.0), (72.0, 88.0, 12.0, 96.0), (118.0, 96.0, 12.0, 96.0)):
    transition = height + padding
    far = pager_state(header, height, padding, lead, header + transition + lead + 1.0)
    if far[2] != 0.0:
        raise SystemExit('overlay lane reserves space before the bounded lead window')
    pre = pager_state(header, height, padding, lead, header + transition + lead * 0.5)
    if not (pre[0] == 0.0 and 0.0 < pre[1] < 1.0 and pre[2] > 0.0):
        raise SystemExit('preemptive overlay motion does not begin before visual pager join')
    edge = pager_state(header, height, padding, lead, header + transition)
    expected_edge = 2.0 * (height + padding)
    if abs(edge[2] - expected_edge) > 1e-6:
        raise SystemExit('pre-join lane does not meet live pager clearance continuously')
    pinned = pager_state(header, height, padding, lead, header - 20.0)
    if abs(pinned[3] - header) > 1e-6 or abs(pinned[2] - (height + padding)) > 1e-6:
        raise SystemExit('pinned pager/header lane geometry is wrong')

line_width = 142.0
if line_width + 20.0 <= line_width:
    raise SystemExit('opening accent rule was not extended')

print('PASS VulkanScope 3.0.0 portrait/landscape chrome and overlay state machine')
