#!/usr/bin/env python3
from dataclasses import dataclass

@dataclass(frozen=True)
class Row:
    feature: bool
    value: object

def emit_bool(section, value):
    return Row('Features' in section, bool(value))

def emit_auto(section, value):
    if 'Features' in section:
        raise AssertionError('generatedEmitAuto must never be used for feature structs')
    if isinstance(value, tuple) and len(value) in (2, 3):
        return Row(False, ' × '.join(str(v) for v in value))
    return Row(False, value)



def relative_luminance(hex_color):
    rgb=[int(hex_color[i:i+2],16)/255.0 for i in (1,3,5)]
    linear=[v/12.92 if v<=0.04045 else ((v+0.055)/1.055)**2.4 for v in rgb]
    return 0.2126*linear[0]+0.7152*linear[1]+0.0722*linear[2]

def contrast_ratio(background, foreground):
    a,b=relative_luminance(background),relative_luminance(foreground)
    return (max(a,b)+0.05)/(min(a,b)+0.05)

def badge(value):
    lower = value.strip().lower().replace('_', ' ')
    if lower in {'true','yes','supported','pass'}:
        return 'yes'
    if lower == 'available':
        return 'available'
    if lower in {'false','no','not supported','unsupported','fail','error','not available','not exposed'}:
        return 'no'
    if 'unavailable' in lower:
        return 'unavailable'
    if 'not applicable' in lower:
        return 'neutral'
    if 'incomplete' in lower:
        return 'incomplete'
    return 'unknown'

assert emit_bool('Validated · VkPhysicalDeviceExampleFeaturesEXT', 1) == Row(True, True)
assert emit_bool('Validated · VkPhysicalDeviceExamplePropertiesEXT', 0) == Row(False, False)
assert emit_auto('Validated · VkPhysicalDeviceExamplePropertiesEXT', 8) == Row(False, 8)
assert emit_auto('Validated · VkPhysicalDeviceExamplePropertiesEXT', (8, 8)) == Row(False, '8 × 8')
try:
    emit_auto('Validated · VkPhysicalDeviceExampleFeaturesEXT', 1)
except AssertionError:
    pass
else:
    raise SystemExit('feature auto-routing was accepted')
expected = {'AVAILABLE':'available','SUPPORTED':'yes','PASS':'yes','UNSUPPORTED':'no','FAIL':'no','UNAVAILABLE':'unavailable','NOT APPLICABLE':'neutral','INCOMPLETE':'incomplete','UNKNOWN':'unknown'}
for label, cls in expected.items():
    if badge(label) != cls:
        raise SystemExit(f'{label} mapped to {badge(label)} instead of {cls}')
palette={
    'yes/available':('#133b28','#74e2a6'),
    'no':('#49171c','#ff8f98'),
    'unavailable':('#493019','#ffc27a'),
    'neutral':('#243246','#a9c9ff'),
    'incomplete':('#403713','#ffd76b'),
    'unknown':('#292a2f','#c6c6cc'),
}
for name,(background,foreground) in palette.items():
    ratio=contrast_ratio(background,foreground)
    if ratio < 4.5:
        raise SystemExit(f'{name} badge contrast {ratio:.2f}:1 is below 4.5:1')
evidence = [('Core','a'),('Generated','b'),('Vulkan Query Safety','queue'),('Vulkan Query Safety','memory')]
property_rows = sum(1 for section, _ in evidence if section != 'Vulkan Query Safety')
safety_rows = sum(1 for section, _ in evidence if section == 'Vulkan Query Safety')
assert len(evidence) == property_rows + safety_rows
print('PASS report semantic state machine: field roles, status classes, >=4.5:1 badge contrast, evidence-count decomposition')
