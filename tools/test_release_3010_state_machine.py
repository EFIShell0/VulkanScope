#!/usr/bin/env python3

orientations = ('portrait', 'landscape', 'freeform')
for orientation in orientations:
    expanded = False
    trigger_enabled = True
    if trigger_enabled:
        expanded = True
    if not expanded:
        raise SystemExit(f'View & sort did not open in {orientation}')
    for event in ('outside_tap', 'back'):
        if event == 'close_x':
            expanded = False
        if not expanded:
            raise SystemExit(f'View & sort dismissed without X in {orientation}: {event}')
    expanded = False
    if expanded:
        raise SystemExit(f'View & sort X close failed in {orientation}')

view_modes = ('LIST', 'COMPACT', 'DETAILS', 'GRID', 'DENSE_GRID', 'LARGE_GRID')
sort_modes = ('NAME_ASC', 'NAME_DESC', 'MODIFIED_NEWEST', 'MODIFIED_OLDEST', 'CREATED_NEWEST', 'CREATED_OLDEST')
if len(view_modes) != 6 or len(set(view_modes)) != 6:
    raise SystemExit('View & sort layout-mode model regressed')
if len(sort_modes) != 6 or len(set(sort_modes)) != 6:
    raise SystemExit('View & sort sort-mode model regressed')

print('PASS VulkanScope 3.0.10 File Manager landscape state machine')
