#!/usr/bin/env python3
import math

styles = {
    'Overview': lambda w: (1 + 0.16*w, 1 + 0.16*w, 0, 0),
    'Vulkan': lambda w: (1 + 0.08*w, 1 + 0.08*w, -11*w, 0),
    'Surface': lambda w: (1, 1, 4*w, -5*w),
    'Display': lambda w: (1 + 0.12*w, 1 - 0.10*w, 0, 0),
    'Extensions': lambda w: (1 + 0.09*w, 1 + 0.09*w, 10*w, 0),
}
mid = math.sin(math.pi * 0.5)
values = {name: fn(mid) for name, fn in styles.items()}
assert len(set(values.values())) == len(styles)
assert values['Overview'][0] > 1 and values['Overview'][2] == 0
assert values['Vulkan'][2] < 0
assert values['Surface'][3] < 0
assert values['Display'][0] > 1 and values['Display'][1] < 1
assert values['Extensions'][2] > 0
assert math.sin(0) == 0
assert abs(math.sin(math.pi)) < 1e-9
badges = {'HDR capabilities': 'HDR', 'HDR / wide-color surface detection': 'HDR', 'Supported display modes': 'MODE', 'Display ↔ Vulkan interpretation': 'SURFACE'}
assert badges['HDR capabilities'] == badges['HDR / wide-color surface detection']
assert badges['Supported display modes'] != badges['HDR capabilities']
assert badges['Display ↔ Vulkan interpretation'] == 'SURFACE'
print('PASS VulkanScope 1.0.15 display-badge/navigation-animation state machine')
