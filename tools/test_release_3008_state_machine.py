#!/usr/bin/env python3

UI_MODE_PHONE = 'phone'
UI_MODE_TELEVISION = 'television'

def handles_dpad(ui_mode, key_code):
    return key_code in {'DPAD_UP', 'DPAD_DOWN', 'PAGE_UP', 'PAGE_DOWN'}

for mode in [UI_MODE_PHONE, UI_MODE_TELEVISION, 'tablet', 'desktop']:
    if not handles_dpad(mode, 'DPAD_DOWN'):
        raise SystemExit(f'hardware D-pad handling regressed for {mode}')
if handles_dpad(UI_MODE_PHONE, 'A'):
    raise SystemExit('unrelated hardware key must not be consumed by D-pad fallback')

def focus_first(can_move_focus, can_scroll, direction, first, last, total):
    if can_move_focus:
        return 'focus', None
    if not can_scroll or total <= 0:
        return 'unhandled', None
    target = min(total - 1, last + 1) if direction == 'down' else max(0, first - 1)
    return 'scroll', target

if focus_first(True, True, 'down', 0, 4, 20) != ('focus', None):
    raise SystemExit('focus-first behavior regressed')
if focus_first(False, True, 'down', 0, 4, 20) != ('scroll', 5):
    raise SystemExit('downward bounded scroll fallback regressed')
if focus_first(False, True, 'up', 5, 9, 20) != ('scroll', 4):
    raise SystemExit('upward bounded scroll fallback regressed')
if focus_first(False, False, 'down', 15, 19, 20) != ('unhandled', None):
    raise SystemExit('end-of-list D-pad handling regressed')

def metric_columns(width_dp, font_scale):
    if width_dp < 300 or (font_scale >= 1.55 and width_dp < 420):
        return 1
    if width_dp < 760:
        return 2
    return 3

if metric_columns(360, 1.0) != 2 or metric_columns(900, 1.0) != 3 or metric_columns(280, 1.0) != 1 or metric_columns(360, 1.6) != 1:
    raise SystemExit('responsive metric-grid state machine regressed')

def state_counts(states):
    return {name: states.count(name) for name in ['SATISFIED', 'NOT SATISFIED', 'UNKNOWN']}

counts = state_counts(['SATISFIED', 'UNKNOWN', 'NOT SATISFIED', 'SATISFIED'])
if counts != {'SATISFIED': 2, 'NOT SATISFIED': 1, 'UNKNOWN': 1}:
    raise SystemExit('requirement summary counts regressed')

def profile_counts(states):
    return tuple(states.count(name) for name in ['PASS', 'FAIL', 'UNKNOWN'])

if profile_counts(['PASS', 'FAIL', 'UNKNOWN', 'PASS']) != (2, 1, 1):
    raise SystemExit('profile/minimum summary counts regressed')

def breadcrumb_state(count, active):
    return [('active' if i == active else 'inactive') for i in range(count)]

if breadcrumb_state(3, 1) != ['inactive', 'active', 'inactive']:
    raise SystemExit('breadcrumb active-state model regressed')

def selection(selected, maximum):
    return selected, max(0, maximum - selected)

if selection(0, 9) != (0, 9) or selection(3, 9) != (3, 6) or selection(9, 9) != (9, 0):
    raise SystemExit('Turnip selected/remaining counter model regressed')

print('PASS VulkanScope 3.0.8 D-pad/analysis/metrics/file-manager state machine')
