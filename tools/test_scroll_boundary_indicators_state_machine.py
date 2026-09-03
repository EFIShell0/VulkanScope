#!/usr/bin/env python3

def indicators(can_scroll_backward, can_scroll_forward):
    return {
        'up': bool(can_scroll_backward),
        'down': bool(can_scroll_forward),
    }

cases = [
    ('top', False, True, {'up': False, 'down': True}),
    ('middle', True, True, {'up': True, 'down': True}),
    ('bottom', True, False, {'up': True, 'down': False}),
    ('non-scrollable', False, False, {'up': False, 'down': False}),
]
for name, backward, forward, expected in cases:
    actual = indicators(backward, forward)
    if actual != expected:
        raise SystemExit(f'FAIL {name}: expected {expected}, got {actual}')
print('PASS scroll boundary indicator state machine')
