#!/usr/bin/env python3


def expanded(width_dp, font_scale):
    return font_scale >= 1.3 or width_dp < 360


def quick_columns(width_dp, font_scale):
    return 2 if expanded(width_dp, font_scale) else 4


def key_value_stacked(width_dp, font_scale, value):
    return expanded(width_dp, font_scale) or len(value) > 54 or '\n' in value


cases = [
    (411, 1.0, False, 4),
    (360, 1.0, False, 4),
    (359, 1.0, True, 2),
    (411, 1.3, True, 2),
    (411, 2.0, True, 2),
    (800, 2.0, True, 2),
    (800, 1.0, False, 4),
]
for width, font, expected_expanded, expected_columns in cases:
    if expanded(width, font) != expected_expanded:
        raise SystemExit(f'expanded layout mismatch width={width} font={font}')
    if quick_columns(width, font) != expected_columns:
        raise SystemExit(f'Quick Access column mismatch width={width} font={font}')
if key_value_stacked(411, 2.0, 'Short') is not True:
    raise SystemExit('200% font must stack short key/value content')
if key_value_stacked(411, 1.0, 'Short') is not False:
    raise SystemExit('normal font should retain compact short key/value layout')
if key_value_stacked(411, 1.0, 'x' * 55) is not True:
    raise SystemExit('long values must stack independent of font scale')
if key_value_stacked(411, 1.0, 'a\nb') is not True:
    raise SystemExit('multiline values must stack independent of font scale')
print('PASS 0.80.6 large-text/display-size layout state machine')
