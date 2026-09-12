#!/usr/bin/env python3

def transition(initial, target):
    if initial is None and target is not None:
        return {'enter_slide_ms': 280, 'enter_fade_ms': 220, 'exit_slide_ms': 180, 'exit_fade_ms': 150, 'direction': 'forward'}
    if initial is not None and target is None:
        return {'enter_slide_ms': 240, 'enter_fade_ms': 200, 'exit_slide_ms': 200, 'exit_fade_ms': 150, 'direction': 'back'}
    return {'enter_fade_ms': 200, 'exit_fade_ms': 150, 'direction': 'peer'}

for target in ('INFO', 'REPORTS', 'DRIVER_UPDATES'):
    result = transition(None, target)
    assert result['direction'] == 'forward'
    assert result['enter_slide_ms'] == 280
    assert result['enter_fade_ms'] == 220
assert transition('INFO', None)['direction'] == 'back'
assert transition('REPORTS', None)['enter_slide_ms'] == 240
assert transition('DRIVER_UPDATES', None)['exit_slide_ms'] == 200
assert transition('INFO', 'REPORTS') == {'enter_fade_ms': 200, 'exit_fade_ms': 150, 'direction': 'peer'}
print('PASS VulkanScope 1.2.5 Settings destination transition state machine')
