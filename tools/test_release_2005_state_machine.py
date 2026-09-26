#!/usr/bin/env python3

HOLD_MS = 550

def key_event(state, event_type, activation, elapsed_ms=0):
    job_active, consumed, dialog = state
    if not activation:
        return state, False
    if event_type == 'down':
        if not job_active:
            return (True, False, False), False
        if elapsed_ms >= HOLD_MS:
            return (True, True, True), True
        return state, consumed
    if event_type == 'timer':
        if job_active and elapsed_ms >= HOLD_MS:
            return (True, True, True), True
        return state, False
    if event_type == 'up':
        was_consumed = consumed
        return (False, False, dialog), was_consumed
    return state, False

state = (False, False, False)
state, consumed = key_event(state, 'down', True)
assert state == (True, False, False) and not consumed
state, consumed = key_event(state, 'up', True, 200)
assert state == (False, False, False) and not consumed

state = (False, False, False)
state, consumed = key_event(state, 'down', True)
assert not consumed
state, consumed = key_event(state, 'timer', True, 550)
assert state == (True, True, True) and consumed
state, consumed = key_event(state, 'up', True, 700)
assert consumed and state == (False, False, True)

state = (False, False, False)
state, consumed = key_event(state, 'down', False)
assert not consumed and state == (False, False, False)

print('PASS VulkanScope 2.0.5 TV key hold state machine')
