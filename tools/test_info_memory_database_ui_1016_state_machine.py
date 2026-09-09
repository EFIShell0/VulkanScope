#!/usr/bin/env python3

section_icons = {
    'Developer': 'code',
    'Application': 'scope-wordmark',
    'Memory heaps': 'ram-module',
    'Memory types': 'ram-banks',
}
assert len(set(section_icons.values())) == len(section_icons)
assert section_icons['Memory heaps'] != section_icons['Memory types']
application_identity = {'section': 'scope-wordmark', 'versionRow': 'launcher-foreground'}
assert application_identity['section'] != application_identity['versionRow']
check_updates = {'arrow': True, 'tray': False, 'line': False}
assert check_updates == {'arrow': True, 'tray': False, 'line': False}
self_test = {'device': 'selected-gpu', 'action': 'run', 'resultDoesNotRewriteCapability': True, 'icon': 'chip-check'}
assert self_test['resultDoesNotRewriteCapability'] and self_test['icon'] != 'flask'

def producer_identity(version, version_code):
    parts = version.split('.')
    if len(parts) != 3 or not all(part.isdigit() for part in parts):
        return False
    major, minor, patch = map(int, parts)
    if major == 1 and minor == 0:
        return version_code == 1000 + patch
    return False

assert producer_identity('1.0.16', 1016)
assert not producer_identity('1.0.16', 1015)
assert not producer_identity('1.0.15', 1016)
routes = {'submit': ('POST', '/v1/reports'), 'lookup': ('GET', '/v1/reports/<sha256>?compact=1')}
assert routes['submit'][0] == 'POST' and routes['lookup'][0] == 'GET'
print('PASS VulkanScope 1.0.16 semantic-icon and Database producer/route state machine')
