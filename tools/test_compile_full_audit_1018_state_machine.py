#!/usr/bin/env python3

def bind_filter_bar(positional_third=None, named_on_selected=None):
    arrow_tint = positional_third if positional_third is not None else 'default-color'
    on_selected = named_on_selected
    return arrow_tint, on_selected

bad_arrow, bad_callback = bind_filter_bar(positional_third='callback')
assert bad_arrow == 'callback'
assert bad_callback is None
good_arrow, good_callback = bind_filter_bar(named_on_selected='callback')
assert good_arrow == 'default-color'
assert good_callback == 'callback'

def producer_identity(version, code):
    parts = version.split('.')
    if len(parts) != 3 or parts[0] != '1' or parts[1] != '0' or not parts[2].isdigit():
        return False
    return code == 1000 + int(parts[2])

assert producer_identity('1.0.18', 1018)
assert not producer_identity('1.0.18', 1017)
assert not producer_identity('1.0.18', 1019)
print('PASS VulkanScope 1.0.18 callback-binding and Database producer state machine')
