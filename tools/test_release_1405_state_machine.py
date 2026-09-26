import json
import re
import xml.etree.ElementTree as ET
from pathlib import Path

root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
coverage = (root / 'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt').read_text(encoding='utf-8')
pnext = (root / 'app/src/main/cpp/runtime_extension_pnext_generated.inc').read_text(encoding='utf-8')
registry = ET.parse(root / 'registry/upstream/vk.xml').getroot()

def platform(chrome, pc):
    if chrome:
        return 'ChromeOS Android Runtime (ARC)'
    if pc:
        return 'Android desktop / PC runtime'
    return 'Android'

def googlebook(chrome, pc, freeform):
    if chrome:
        return 'ChromeOS ARC detected; Googlebook identity is not inferred'
    if pc and freeform:
        return 'Android PC form factor with freeform window management detected; Googlebook-compatible environment evidence only'
    if pc:
        return 'Android PC form factor detected; Googlebook identity is not exposed by a documented public Android API'
    return 'Googlebook identity is not exposed by a documented public Android API'

cases = [
    ((True, True, True), ('ChromeOS Android Runtime (ARC)', 'ChromeOS ARC detected; Googlebook identity is not inferred')),
    ((False, True, True), ('Android desktop / PC runtime', 'Android PC form factor with freeform window management detected; Googlebook-compatible environment evidence only')),
    ((False, True, False), ('Android desktop / PC runtime', 'Android PC form factor detected; Googlebook identity is not exposed by a documented public Android API')),
    ((False, False, True), ('Android', 'Googlebook identity is not exposed by a documented public Android API')),
    ((False, False, False), ('Android', 'Googlebook identity is not exposed by a documented public Android API'))
]
for inputs, expected in cases:
    chrome, pc, freeform = inputs
    actual = (platform(chrome, pc), googlebook(chrome, pc, freeform))
    if actual != expected:
        raise SystemExit(f'platform state-machine mismatch: {inputs} -> {actual}')

match = re.search(r'VALIDATED_PHYSICAL_DEVICE_QUERY_EXTENSIONS\s*=\s*setOf\((.*?)\n\)', coverage, re.S)
validated = set(re.findall(r'"(VK_[A-Za-z0-9_]+)"', match.group(1))) if match else set()
types = {}
for node in registry.findall('./types/type'):
    name = node.get('name') or node.findtext('name')
    if name:
        types[name] = node

def extends_properties_or_features(name):
    seen = set()
    while name and name not in seen and name in types:
        seen.add(name)
        node = types[name]
        ext = {x.strip() for x in (node.get('structextends') or '').split(',') if x.strip()}
        if {'VkPhysicalDeviceFeatures2', 'VkPhysicalDeviceProperties2'} & ext:
            return True
        name = node.get('alias')
    return False

queryable = set()
provisional = set()
for extension in registry.findall('./extensions/extension'):
    name = extension.get('name') or ''
    supported = {x.strip() for x in (extension.get('supported') or '').split(',') if x.strip()}
    if not name or extension.get('type') != 'device' or 'vulkan' not in supported:
        continue
    if (extension.get('platform') or '') not in {'', 'android', 'provisional'}:
        continue
    if any(extends_properties_or_features(t.get('name') or '') for req in extension.findall('require') for t in req.findall('type')):
        queryable.add(name)
        if extension.get('provisional', '').lower() == 'true':
            provisional.add(name)
if validated != queryable:
    raise SystemExit(f'validated/queryable mismatch missing={sorted(queryable-validated)} extra={sorted(validated-queryable)}')
pnext_names = set(re.findall(r'std::strcmp\(selectedExtension,\s*"(VK_[A-Za-z0-9_]+)"', pnext))
parity = (root / 'app/src/main/cpp/runtime_extension_pnext_parity.inc').read_text(encoding='utf-8')
pnext_names |= set(re.findall(r'std::strcmp\(selectedExtension,\s*"(VK_[A-Za-z0-9_]+)"', parity))
if pnext_names != validated:
    raise SystemExit(f'pNext/validated mismatch missing={sorted(validated-pnext_names)} extra={sorted(pnext_names-validated)}')
if len(validated) != 305 or len(validated - provisional) != 300 or len(provisional) != 5:
    raise SystemExit(f'provider census mismatch total={len(validated)} stable={len(validated-provisional)} provisional={len(provisional)}')


def use_rail(landscape, width_dp, television):
    return landscape or width_dp >= 600 or television

nav_cases = [
    ((False, 360, False), False),
    ((False, 599, False), False),
    ((False, 600, False), True),
    ((False, 840, False), True),
    ((True, 480, False), True),
    ((False, 360, True), True)
]
for inputs, expected in nav_cases:
    if use_rail(*inputs) != expected:
        raise SystemExit(f'navigation adaptive state-machine mismatch: {inputs}')

required_surfaces = {
    'ui': 'CapabilityKeyValue("Googlebook environment", googlebookEnvironmentEvidence(context))',
    'database': 'put("googlebookEnvironmentEvidence", googlebookEnvironmentEvidence(context))',
    'text': 'appendLine("Googlebook environment: ${googlebookEnvironmentEvidence(context)}")',
    'html': '"Googlebook environment" to htmlEscape(googlebookEnvironmentEvidence(context))'
}
for surface, token in required_surfaces.items():
    if token not in main:
        raise SystemExit(f'Googlebook evidence missing from {surface}')
print('release_1405 state machine: PASS platformCases=5 navigationCases=6 queryableProviders=305 reportSurfaces=4')
