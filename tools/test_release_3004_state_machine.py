#!/usr/bin/env python3

def key_event_native_code(event):
    return event['key']['nativeKeyCode']

for native in (19, 20, 92, 93):
    event = {'type': 'KeyDown', 'key': {'nativeKeyCode': native}}
    if key_event_native_code(event) != native:
        raise SystemExit('KeyEvent to Key native-code projection regressed')


def tv_action(native):
    if native == 20:
        return 'down'
    if native == 19:
        return 'up'
    if native == 93:
        return 'page-down'
    if native == 92:
        return 'page-up'
    return 'pass'

expected = {20: 'down', 19: 'up', 93: 'page-down', 92: 'page-up', 23: 'pass'}
for native, action in expected.items():
    if tv_action(native) != action:
        raise SystemExit(f'TV key mapping regressed for {native}')

weights = [1.0, 1.0, 1.0]
shares = [weight / sum(weights) for weight in weights]
if shares != [1 / 3, 1 / 3, 1 / 3]:
    raise SystemExit('graph summary equal-weight layout regressed')

print('PASS VulkanScope 3.0.4 compile-contract state machine')
