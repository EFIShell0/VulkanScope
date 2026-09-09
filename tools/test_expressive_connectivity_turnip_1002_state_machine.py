#!/usr/bin/env python3

def physical_direction(layout,left):
    return 'backward' if (layout == 'LTR') == left else 'forward'

def banner_transition(previous,current,generation):
    if previous == current: return previous,generation,None
    generation += 1
    return current,generation,'CONNECTED' if current else 'DISCONNECTED'

def banner_timeout(current_generation,scheduled_generation,state):
    return 'HIDDEN' if current_generation == scheduled_generation else state

def metric_columns(width,font_scale):
    if font_scale >= 1.3 or width < 360: return 1
    if width < 760: return 2
    return 3

def metadata_value(value):
    return value if value not in (None,'') else 'Not provided by package'

assert physical_direction('LTR',True) == 'backward'
assert physical_direction('LTR',False) == 'forward'
assert physical_direction('RTL',True) == 'forward'
assert physical_direction('RTL',False) == 'backward'
previous=None; generation=0
previous,generation,state=banner_transition(previous,False,generation)
assert state == 'DISCONNECTED' and generation == 1
previous,generation,state=banner_transition(previous,False,generation)
assert state is None and generation == 1
previous,generation,state=banner_transition(previous,True,generation)
assert state == 'CONNECTED' and generation == 2
assert banner_timeout(2,1,'CONNECTED') == 'CONNECTED'
assert banner_timeout(2,2,'CONNECTED') == 'HIDDEN'
assert metric_columns(320,1.0) == 1
assert metric_columns(411,1.5) == 1
assert metric_columns(411,1.0) == 2
assert metric_columns(900,1.0) == 3
assert metadata_value(None) == 'Not provided by package'
assert metadata_value('Vulkan 1.4.362') == 'Vulkan 1.4.362'
print('PASS 1.0.2 carousel/network/metric/metadata state model')
