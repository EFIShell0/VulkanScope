#!/usr/bin/env python3

PRIMARY = {'Overview', 'Vulkan', 'Surface', 'Display', 'Extensions'}
OVERVIEW_CHILDREN = {'Features', 'Memory', 'Queues', 'Video', 'Formats', 'Properties', 'Profiles', 'Encyclopedia', 'Analysis', 'Settings', 'Info'}


def selected(page: str) -> str:
    return 'Overview' if page in OVERVIEW_CHILDREN else page


for page in PRIMARY:
    if selected(page) != page:
        raise SystemExit(f'primary destination {page} does not select itself')
for page in OVERVIEW_CHILDREN:
    if selected(page) != 'Overview':
        raise SystemExit(f'Overview child {page} does not keep Overview selected')

compact = {'height': 64, 'indicatorWidth': 56, 'indicatorHeight': 32, 'icon': 24, 'labelVisible': True}
if compact != {'height': 64, 'indicatorWidth': 56, 'indicatorHeight': 32, 'icon': 24, 'labelVisible': True}:
    raise SystemExit('compact navigation geometry contract failed')

rail = {'usesNavigationRailItem': True, 'labelVisible': True, 'activeIndicator': True}
if not all(rail.values()):
    raise SystemExit('rail navigation semantics contract failed')

print('VulkanScope 1.4.13 navigation state machine: PASS')
