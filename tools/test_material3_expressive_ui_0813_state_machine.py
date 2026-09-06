#!/usr/bin/env python3

def key_value_stacked(width_dp, font_scale, key, value):
    return font_scale >= 1.3 or width_dp < 360 or len(key) > 26 or len(value) > 34 or '\n' in value

def update_metadata_stacked(width_dp, font_scale, key, value):
    return font_scale >= 1.3 or width_dp < 360 or len(key) > 24 or len(value) > 32 or '\n' in value

def dialog_body_height(screen_height_dp):
    return min(560, max(220, screen_height_dp - 240))

def scroll_state(position, maximum):
    return position > 0, position < maximum

cases = [
    (393, 1.0, 'Registry requires', 'Unavailable in checked-in reference asset', True),
    (393, 1.0, 'Scope', 'device', False),
    (320, 1.0, 'Scope', 'device', True),
    (700, 1.3, 'Scope', 'device', True),
    (700, 1.0, 'Related commands', 'vkCmdWriteBufferMarker2AMD, vkCmdWriteBufferMarkerAMD', True),
]
for width, scale, key, value, expected in cases:
    actual = key_value_stacked(width, scale, key, value)
    assert actual == expected, (width, scale, key, value, actual, expected)

assert update_metadata_stacked(393, 1.0, 'Downloaded versionCode', 'Verified from the APK before installation')
assert not update_metadata_stacked(500, 1.0, 'Installed ABI', 'arm64-v8a')
assert dialog_body_height(800) == 560
assert dialog_body_height(600) == 360
assert dialog_body_height(400) == 220
assert scroll_state(0, 100) == (False, True)
assert scroll_state(50, 100) == (True, True)
assert scroll_state(100, 100) == (True, False)
assert scroll_state(0, 0) == (False, False)
print('PASS 0.80.13 Material 3 Expressive responsive-layout state machine')
