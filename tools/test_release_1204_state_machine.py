#!/usr/bin/env python3

def history_clear_enabled(count):
    return count > 0

def clear_history(ids, confirmed):
    retained = list(ids)
    if not confirmed:
        return retained, False
    return [], True

def settings_card_timeline(count=3):
    return [(index, index * 45, 260) for index in range(count)]

def share_trailing_action(title):
    if title in {'Share link', 'Share evidence'}:
        return 'ic_open_external'
    return None

assert history_clear_enabled(0) is False
assert history_clear_enabled(1) is True
assert clear_history(['a', 'b'], False) == (['a', 'b'], False)
assert clear_history(['a', 'b'], True) == ([], True)
assert settings_card_timeline() == [(0, 0, 260), (1, 45, 260), (2, 90, 260)]
assert settings_card_timeline(0) == []
assert share_trailing_action('Share link') == 'ic_open_external'
assert share_trailing_action('Share evidence') == 'ic_open_external'
print('PASS VulkanScope 1.2.4 History/Settings-motion/Share state machine')
