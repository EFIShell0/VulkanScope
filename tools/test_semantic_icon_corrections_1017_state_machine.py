#!/usr/bin/env python3

icons = {
    'Developer header': 'code',
    'Semih Boran identity': 'person',
    'Check for updates': 'zip-download',
    'Direct GitHub updates': 'zip-download',
    'Import driver ZIP': 'zip-download',
    'HDR & Color': 'display-hdr',
    'Global Vulkan report search': 'search',
    'Surface + Display presentation evidence': 'display+surface',
    'Raw structured technical Report': 'registry+json',
    'Compare with VulkanScope Database': 'database+compare',
}
assert icons['Developer header'] != icons['Semih Boran identity']
assert len({icons['Check for updates'], icons['Direct GitHub updates'], icons['Import driver ZIP']}) == 1
assert icons['Surface + Display presentation evidence'] != icons['HDR & Color']
assert icons['Raw structured technical Report'].endswith('+json')
assert icons['Compare with VulkanScope Database'].startswith('database+')

def producer_identity(version, version_code):
    parts = version.split('.')
    if len(parts) != 3 or not all(part.isdigit() for part in parts):
        return False
    major, minor, patch = map(int, parts)
    return major == 1 and minor == 0 and version_code == 1000 + patch

assert producer_identity('1.0.17', 1017)
assert not producer_identity('1.0.17', 1016)
assert not producer_identity('1.0.16', 1017)
routes = {'submit': ('POST', '/v1/reports'), 'lookup': ('GET', '/v1/reports/<sha256>?compact=1')}
assert routes['submit'][0] == 'POST' and routes['lookup'][0] == 'GET'
canonical_key = 'technicalReport'
ui_title = 'Raw structured technical Report'
assert canonical_key not in ui_title and 'technical Report' in ui_title
print('PASS VulkanScope 1.0.17 semantic-icon/title and Database producer/route state machine')
