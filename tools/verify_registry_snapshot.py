#!/usr/bin/env python3
import hashlib
import json
import re
import xml.etree.ElementTree as ET
from pathlib import Path

root = Path(__file__).resolve().parents[1]
lock = json.loads((root / 'registry/registry_lock.json').read_text(encoding='utf-8'))
registry_path = root / lock['bundledRegistryPath']
errors = []
if not registry_path.is_file():
    raise SystemExit('locked bundled vk.xml is missing')
actual_sha = hashlib.sha256(registry_path.read_bytes()).hexdigest()
if actual_sha != lock['registrySha256']:
    errors.append('bundled vk.xml SHA-256 does not match registry lock')
registry = ET.parse(registry_path).getroot()
header_versions = []
for node in registry.findall('./types/type'):
    if node.findtext('name') == 'VK_HEADER_VERSION' and 'vulkan' in {x.strip() for x in (node.get('api') or '').split(',')}:
        match = re.search(r'VK_HEADER_VERSION\s+(\d+)', ''.join(node.itertext()))
        if match:
            header_versions.append(int(match.group(1)))
if lock['headerVersion'] not in header_versions:
    errors.append(f'vk.xml header version does not contain locked version {lock["headerVersion"]}')
types = {}
for node in registry.findall('./types/type'):
    name = node.get('name') or node.findtext('name')
    if name:
        types[name] = node

def canonical_type(name):
    seen = set()
    while name and name in types and name not in seen:
        seen.add(name)
        alias = types[name].get('alias')
        if not alias:
            return name
        name = alias
    return name

def struct_extends(name):
    seen = set()
    while name and name in types and name not in seen:
        seen.add(name)
        node = types[name]
        values = {x.strip() for x in (node.get('structextends') or '').split(',') if x.strip()}
        if values:
            return values
        name = node.get('alias')
    return set()

all_vulkan_extensions = set()
queryable_extensions = set()
provisional_extensions = set()
provider_structs = {}
for extension in registry.findall('./extensions/extension'):
    name = extension.get('name') or ''
    supported = {x.strip() for x in (extension.get('supported') or '').split(',') if x.strip()}
    if not name or not ({'vulkan', 'vulkanbase'} & supported):
        continue
    all_vulkan_extensions.add(name)
    if extension.get('type') != 'device' or 'vulkan' not in supported:
        continue
    platform = extension.get('platform') or ''
    if platform not in {'', 'android', 'provisional'}:
        continue
    matched_structs = []
    for require in extension.findall('require'):
        for type_node in require.findall('type'):
            type_name = type_node.get('name') or ''
            if not type_name.startswith('VkPhysicalDevice'):
                continue
            extends = struct_extends(type_name)
            if {'VkPhysicalDeviceFeatures2', 'VkPhysicalDeviceProperties2'} & extends:
                matched_structs.append(type_name)
    if matched_structs:
        queryable_extensions.add(name)
        provider_structs[name] = sorted(set(matched_structs))
        if extension.get('provisional', '').lower() == 'true':
            provisional_extensions.add(name)
coverage = (root / 'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt').read_text(encoding='utf-8')
match = re.search(r'VALIDATED_PHYSICAL_DEVICE_QUERY_EXTENSIONS\s*=\s*setOf\((.*?)\n\)', coverage, re.S)
validated = set(re.findall(r'"(VK_[A-Za-z0-9_]+)"', match.group(1))) if match else set()
if validated != queryable_extensions:
    errors.append(f'vk.xml/runtime extension coverage mismatch: missing={sorted(queryable_extensions - validated)} extra={sorted(validated - queryable_extensions)}')
pnext = '\n'.join((root / 'app/src/main/cpp' / name).read_text(encoding='utf-8') for name in ['runtime_extension_pnext_generated.inc', 'runtime_extension_pnext_parity.inc'])
pnext_extensions = set(re.findall(r'std::strcmp\(selectedExtension,\s*"(VK_[A-Za-z0-9_]+)"', pnext))
if pnext_extensions != validated:
    errors.append(f'pNext extension parity mismatch: missing={sorted(validated - pnext_extensions)} extra={sorted(pnext_extensions - validated)}')
queried_structs = set(re.findall(r'storage\.add<\s*(VkPhysicalDevice\w+)', pnext))
queried_canonical_structs = {canonical_type(name) for name in queried_structs}
for extension, structs in provider_structs.items():
    provider_canonical_structs = {canonical_type(name) for name in structs}
    if not (provider_canonical_structs & queried_canonical_structs):
        errors.append(f'no alias-equivalent pNext struct is scheduled for {extension}')
cpp = (root / 'app/src/main/cpp/vulkanscope.cpp').read_text(encoding='utf-8')
if provisional_extensions and '#define VK_ENABLE_BETA_EXTENSIONS 1' not in cpp:
    errors.append('provisional extension coverage requires VK_ENABLE_BETA_EXTENSIONS')
reference = json.loads((root / 'registry/generated/extension_reference.json').read_text(encoding='utf-8'))
reference_names = {entry.get('name') for entry in reference.get('entries', []) if isinstance(entry, dict)}
if reference.get('baseline') != lock['apiBaseline']:
    errors.append('extension-reference baseline does not match registry lock')
if reference_names != all_vulkan_extensions:
    errors.append(f'extension-reference census mismatch: missing={sorted(all_vulkan_extensions - reference_names)} extra={sorted(reference_names - all_vulkan_extensions)}')
manifest = json.loads((root / 'registry/generated/registry_query_manifest.json').read_text(encoding='utf-8'))
if manifest.get('validatedPhysicalDeviceQueryExtensionCount') != len(validated):
    errors.append('registry manifest validated extension count mismatch')
if manifest.get('validatedStablePhysicalDeviceQueryExtensionCount') != len(validated - provisional_extensions):
    errors.append('registry manifest stable extension count mismatch')
if manifest.get('validatedProvisionalPhysicalDeviceQueryExtensionCount') != len(provisional_extensions):
    errors.append('registry manifest provisional extension count mismatch')
if manifest.get('validatedProvisionalPhysicalDeviceQueryExtensions') != sorted(provisional_extensions):
    errors.append('registry manifest provisional extension identity mismatch')
max_extension_match = re.search(r'kMaxExtensionEntries\s*=\s*(\d+)', cpp)
if not max_extension_match or int(max_extension_match.group(1)) < len(all_vulkan_extensions):
    errors.append('native extension-enumeration safety bound is below the current Vulkan registry census')
if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print(f'PASS locked registry snapshot: header={lock["headerVersion"]} registeredExtensions={len(all_vulkan_extensions)} androidQueryableDeviceExtensions={len(queryable_extensions)} stable={len(queryable_extensions - provisional_extensions)} provisional={len(provisional_extensions)} pNextExtensions={len(pnext_extensions)}')
