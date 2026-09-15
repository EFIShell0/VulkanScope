#!/usr/bin/env python3
import re

pattern = re.compile(r'\bVulkan(?!Scope|®)')

def display(text):
    return pattern.sub('Vulkan®', text)

cases = {
    'Vulkan': 'Vulkan®',
    'Vulkan Video': 'Vulkan® Video',
    'System Vulkan driver': 'System Vulkan® driver',
    'VulkanScope': 'VulkanScope',
    'VulkanScope Vulkan utility': 'VulkanScope Vulkan® utility',
    'Vulkan® Headers': 'Vulkan® Headers',
    'VkPhysicalDeviceVulkan12Features': 'VkPhysicalDeviceVulkan12Features',
    'VK_KHR_vulkan_memory_model': 'VK_KHR_vulkan_memory_model',
}
for source, expected in cases.items():
    actual = display(source)
    assert actual == expected, (source, actual, expected)

libraries = [
    ('AndroidX Core KTX', 'Apache License 2.0'),
    ('AndroidX Activity Compose', 'Apache License 2.0'),
    ('Compose UI', 'Apache License 2.0'),
    ('Compose Foundation', 'Apache License 2.0'),
    ('Compose Animation', 'Apache License 2.0'),
    ('Material 3', 'Apache License 2.0'),
    ('Lifecycle Runtime Compose', 'Apache License 2.0'),
    ('OkHttp', 'Apache License 2.0'),
    ('ZXing Core', 'Apache License 2.0'),
    ('Vulkan® Headers', 'Apache-2.0 OR MIT'),
    ('libadrenotools', 'BSD 2-Clause License'),
]
assert len(libraries) == 11
assert len({name for name, _ in libraries}) == len(libraries)
assert all(license_name for _, license_name in libraries)
print('PASS VulkanScope 1.3.0 presentation/license state model')
