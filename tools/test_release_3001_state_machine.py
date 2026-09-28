#!/usr/bin/env python3

def opening_rule_scale(phase):
    if phase >= 2:
        return 1.0
    if phase >= 1:
        return 0.34
    return 0.0

scales = [opening_rule_scale(phase) for phase in range(5)]
if scales != [0.0, 0.34, 1.0, 1.0, 1.0]:
    raise SystemExit(f'opening rule scale sequence regressed: {scales}')
if scales[2:] != [1.0, 1.0, 1.0]:
    raise SystemExit('opening rule shrinks after reaching full width')


def page_top(header_dp):
    return header_dp + 12.0

for header in (72.0, 96.0, 118.0, 156.0):
    top = page_top(header)
    if abs(top - header - 12.0) > 1e-6 or top <= header:
        raise SystemExit('page/header separation is not a positive 12 dp gap')


def file_manager_layout(width_dp, height_dp):
    if width_dp > height_dp and width_dp >= 700.0:
        return ('two-pane', width_dp * 0.42, width_dp * 0.58, height_dp)
    return ('stacked', width_dp, width_dp, None)

for width, height in ((1024.0, 550.0), (1280.0, 720.0), (900.0, 600.0)):
    mode, controls_width, browser_width, browser_height = file_manager_layout(width, height)
    if mode != 'two-pane' or browser_width <= controls_width or browser_height != height:
        raise SystemExit('landscape file manager does not preserve a full-height dominant browser pane')
for width, height in ((412.0, 915.0), (699.0, 500.0), (600.0, 600.0)):
    if file_manager_layout(width, height)[0] != 'stacked':
        raise SystemExit('portrait/compact file manager incorrectly enters two-pane mode')


def consume_secondary_sequence(desktop, events):
    active = False
    consumed = []
    for mouse, secondary_down in events:
        should_consume = desktop and mouse and (active or secondary_down)
        consumed.append(should_consume)
        if desktop and mouse and should_consume:
            active = secondary_down
    return consumed

secondary_click = [(True, True), (True, True), (True, False)]
if consume_secondary_sequence(True, secondary_click) != [True, True, True]:
    raise SystemExit('desktop secondary sequence is not consumed through release')
if any(consume_secondary_sequence(False, secondary_click)):
    raise SystemExit('secondary suppression leaked into non-desktop behavior')
if any(consume_secondary_sequence(True, [(True, False), (True, False)])):
    raise SystemExit('primary/non-secondary pointer path was incorrectly consumed')


def hold_border(pressed):
    return 0.46 if pressed else 0.0

if hold_border(False) != 0.0 or hold_border(True) != 0.46:
    raise SystemExit('hold border alpha contract regressed')

print('PASS VulkanScope 3.0.1 opening/layout/input state machine')
