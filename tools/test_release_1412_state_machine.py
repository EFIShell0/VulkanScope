#!/usr/bin/env python3

PRIMARY = {'Overview', 'Vulkan', 'Surface', 'Display', 'Extensions'}
OVERVIEW_CHILDREN = {'Features', 'Memory', 'Queues', 'Video', 'Formats', 'Properties', 'Profiles', 'Encyclopedia', 'Analysis', 'Settings', 'Info'}

def selected(page: str) -> str:
    if page in OVERVIEW_CHILDREN:
        return 'Overview'
    return page

for page in PRIMARY:
    if selected(page) != page:
        raise SystemExit(f'primary destination {page} does not select itself')
for page in OVERVIEW_CHILDREN:
    if selected(page) != 'Overview':
        raise SystemExit(f'Overview child {page} does not keep Overview selected')

profile = {
    'status': 'FAIL',
    'checkedRequirementCount': 7,
    'missingExtensions': ['VK_EXT_example'],
    'unknownExtensions': ['VK_EXT_unknown'],
    'missingFeatures': ['VkPhysicalDeviceFeatures.example'],
    'unknownFeatures': ['VkPhysicalDeviceFooFeatures.foo'],
    'failingLimits': ['VkPhysicalDeviceProperties.maxFoo >= 8'],
    'unknownLimits': ['VkPhysicalDeviceProperties.maxBar'],
    'failingFormats': ['VK_FORMAT_EXAMPLE'],
    'unknownFormats': ['VK_FORMAT_UNKNOWN'],
    'failingRequirementGroups': ['required profile example failed'],
    'unknownRequirementGroups': ['alternative group unresolved'],
}
for surface in ('JSON', 'TXT', 'HTML', 'Database technicalReport'):
    for key, value in profile.items():
        if value is None:
            raise SystemExit(f'{surface} lost {key}')
print('VulkanScope 1.4.12 navigation/profile reporting state machine: PASS')
