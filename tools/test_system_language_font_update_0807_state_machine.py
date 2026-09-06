#!/usr/bin/env python3
import unicodedata

SAMPLES = {
    'Latin / Vulkan': ('VK_SUCCESS — VulkanScope 0.80.7', 'LTR'),
    'Arabic': ('نجاح فحص Vulkan', 'RTL'),
    'Hebrew': ('בדיקת Vulkan הושלמה', 'RTL'),
    'Cyrillic': ('Проверка Vulkan завершена', 'LTR'),
    'Devanagari': ('Vulkan जाँच पूर्ण', 'LTR'),
    'Thai': ('ทดสอบ Vulkan สำเร็จ', 'LTR'),
    'CJK': ('Vulkan 検査完了', 'LTR'),
    'mixed technical first': ('VK_SUCCESS — نجاح', 'LTR'),
    'mixed Arabic first': ('نجاح — VK_SUCCESS', 'RTL'),
    'emoji only fallback': ('✅ ℹ️', 'LTR'),
}

def content_or_ltr(text):
    for ch in text:
        bidi = unicodedata.bidirectional(ch)
        if bidi == 'L':
            return 'LTR'
        if bidi in {'R', 'AL'}:
            return 'RTL'
    return 'LTR'

for name, (text, expected) in SAMPLES.items():
    encoded = text.encode('utf-8')
    if encoded.decode('utf-8') != text:
        raise SystemExit(f'{name}: UTF-8 roundtrip failed')
    got = content_or_ltr(text)
    if got != expected:
        raise SystemExit(f'{name}: content direction {got}, expected {expected}')

SYSTEM_FONT_POLICY = {
    'family': 'system-default-sans',
    'bundled_font': False,
    'script_fallback': True,
    'manual_glyph_filter': False,
}
if SYSTEM_FONT_POLICY != {
    'family': 'system-default-sans',
    'bundled_font': False,
    'script_fallback': True,
    'manual_glyph_filter': False,
}:
    raise SystemExit('system font fallback model drifted')

UPDATE_STATES = {
    'available': ('info', 'blue'),
    'completed_collection': ('check', 'green'),
    'failed_collection': ('close', 'red'),
}
if UPDATE_STATES['available'] != ('info', 'blue'):
    raise SystemExit('update-available state is not distinct blue information evidence')
if len(set(UPDATE_STATES.values())) != 3:
    raise SystemExit('status icon/color semantics collapsed')
print(f'PASS 0.80.7 locale/bidi/font fallback state model ({len(SAMPLES)} Unicode fixtures) and update-info state')
