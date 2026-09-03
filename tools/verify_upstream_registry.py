#!/usr/bin/env python3
import argparse
import json
import re
import xml.etree.ElementTree as ET
from pathlib import Path

root = Path(__file__).resolve().parents[1]
parser = argparse.ArgumentParser()
parser.add_argument('--registry', required=True)
parser.add_argument('--header', required=True)
args = parser.parse_args()
errors = []

def resolved_stype_values(source):
    raw = {}
    enum_match = re.search(r'typedef\s+enum\s+VkStructureType\s*\{(.*?)\}\s*VkStructureType\s*;', source, re.S)
    if enum_match:
        for name, value in re.findall(r'^\s*(VK_STRUCTURE_TYPE_[A-Z0-9_]+)\s*=\s*([^,\n]+)', enum_match.group(1), re.M):
            raw[name] = value.strip()
    for name, value in re.findall(r'^\s*#define\s+(VK_STRUCTURE_TYPE_[A-Z0-9_]+)\s+(VK_STRUCTURE_TYPE_[A-Z0-9_]+|[-+]?0[xX][0-9A-Fa-f]+|[-+]?\d+)[uUlL]*\s*$', source, re.M):
        raw[name] = value.strip()
    cache = {}
    def resolve(name, trail=None):
        if name in cache:
            return cache[name]
        trail = set() if trail is None else trail
        if name in trail:
            return name
        trail = trail | {name}
        value = raw.get(name)
        if value is None:
            cache[name] = name
            return name
        cleaned = re.sub(r'[uUlL]+$', '', value.strip())
        try:
            resolved = str(int(cleaned, 0))
        except ValueError:
            token = re.fullmatch(r'VK_STRUCTURE_TYPE_[A-Z0-9_]+', cleaned)
            resolved = resolve(cleaned, trail) if token else cleaned
        cache[name] = resolved
        return resolved
    return {name: resolve(name) for name in raw}
lock = json.loads((root / 'registry/registry_lock.json').read_text(encoding='utf-8'))
header = Path(args.header).read_text(encoding='utf-8', errors='ignore')
registry_root = ET.parse(args.registry).getroot()

header_version = re.search(r'#define\s+VK_HEADER_VERSION\s+(\d+)\b', header)
if not header_version or int(header_version.group(1)) != int(lock['headerVersion']):
    errors.append(f'expected VK_HEADER_VERSION {lock["headerVersion"]}')

types = {}
for node in registry_root.findall('./types/type'):
    name = node.get('name') or node.findtext('name')
    if name:
        types[name] = node

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

extensions = {}
queryable_extensions = set()
provider_structs = {}
for extension in registry_root.findall('./extensions/extension'):
    name = extension.get('name')
    if not name:
        continue
    supported = {x.strip() for x in extension.get('supported', '').split(',') if x.strip()}
    provisional = extension.get('provisional', '').lower() == 'true'
    platform = extension.get('platform') or ''
    extensions[name] = (supported, provisional)
    if 'vulkan' not in supported or extension.get('type') != 'device' or platform not in {'', 'android', 'provisional'}:
        continue
    matched = []
    for require in extension.findall('./require'):
        for type_node in require.findall('type'):
            type_name = type_node.get('name') or ''
            if type_name.startswith('VkPhysicalDevice') and {'VkPhysicalDeviceFeatures2', 'VkPhysicalDeviceProperties2'} & struct_extends(type_name):
                matched.append(type_name)
    if matched:
        queryable_extensions.add(name)
        provider_structs[name] = set(matched)

coverage_text = (root / 'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt').read_text(encoding='utf-8')
coverage_match = re.search(r'VALIDATED_PHYSICAL_DEVICE_QUERY_EXTENSIONS\s*=\s*setOf\((.*?)\n\)', coverage_text, re.S)
if not coverage_match:
    errors.append('validated extension coverage set missing')
    validated_extensions = set()
else:
    validated_extensions = set(re.findall(r'"(VK_[A-Za-z0-9_]+)"', coverage_match.group(1)))

header_structs = set(re.findall(r'\b(VkPhysicalDevice\w+)\b', header))
queryable_structs = set().union(*provider_structs.values()) if provider_structs else set()

missing_extensions = sorted(queryable_extensions - validated_extensions)
extra_extensions = sorted(validated_extensions - queryable_extensions)
if missing_extensions or extra_extensions:
    errors.append(f'vk.xml/runtime extension coverage mismatch: missing={missing_extensions} extra={extra_extensions}')
provisional_queryable = {name for name in queryable_extensions if extensions.get(name, (set(), False))[1]}
provisional_validated = {name for name in validated_extensions if extensions.get(name, (set(), False))[1]}
if provisional_queryable != provisional_validated:
    errors.append(f'provisional extension coverage mismatch: registry={sorted(provisional_queryable)} runtime={sorted(provisional_validated)}')
source_cpp = (root / 'app/src/main/cpp/vulkanscope.cpp').read_text(encoding='utf-8')
if provisional_validated and '#define VK_ENABLE_BETA_EXTENSIONS 1' not in source_cpp:
    errors.append('provisional Vulkan extension queries require explicit VK_ENABLE_BETA_EXTENSIONS mode')

pnext_text = '\n'.join((root / 'app/src/main/cpp' / name).read_text(encoding='utf-8') for name in ['runtime_extension_pnext_generated.inc', 'runtime_extension_pnext_parity.inc'])
queried_pairs = re.findall(r'storage\.add<\s*(VkPhysicalDevice\w+)\s*>\(\s*(VK_STRUCTURE_TYPE_[A-Z0-9_]+)', pnext_text)
queried_structs = {name for name, _ in queried_pairs}
queried_stypes = {stype for _, stype in queried_pairs}
missing_queried_header = sorted(queried_structs - header_structs)
missing_queried_registry = sorted(name for name in queried_structs if name not in types)
if missing_queried_header:
    errors.append('queried pNext structs missing from locked header: ' + ', '.join(missing_queried_header))
if missing_queried_registry:
    errors.append('queried pNext structs missing from locked registry: ' + ', '.join(missing_queried_registry))

serializer_text = '\n'.join((root / 'app/src/main/cpp' / name).read_text(encoding='utf-8') for name in ['extension_field_coverage_generated.inc', 'extension_field_coverage_parity.inc'])
serialized_stypes = set(re.findall(r'case\s+(VK_STRUCTURE_TYPE_[A-Z0-9_]+)\s*:', serializer_text))
stype_values = resolved_stype_values(header)
queried_values = {stype_values.get(stype, stype) for stype in queried_stypes}
serialized_values = {stype_values.get(stype, stype) for stype in serialized_stypes}
missing_serializer_values = queried_values - serialized_values
missing_serializers = sorted(stype for stype in queried_stypes if stype_values.get(stype, stype) in missing_serializer_values)
if missing_serializers:
    errors.append('queried pNext structure types without alias-resolved report serializer coverage: ' + ', '.join(missing_serializers))
unknown_serializer_tokens = sorted(stype for stype in serialized_stypes if stype not in header)
if unknown_serializer_tokens:
    errors.append('serializer structure-type tokens missing from locked header: ' + ', '.join(unknown_serializer_tokens))

catalog = (root / 'app/src/main/cpp/registry_query_catalog.h').read_text(encoding='utf-8')
struct_array = re.search(r'kImplementedPhysicalDeviceStructs\s*=\s*\{(.*?)\};', catalog, re.S)
implemented_structs = set(re.findall(r'"(VkPhysicalDevice[A-Za-z0-9_]+)"', struct_array.group(1))) if struct_array else set()
missing_catalog_header = sorted(implemented_structs - header_structs - {'VkPhysicalDeviceFeatures', 'VkPhysicalDeviceLimits', 'VkPhysicalDeviceMemoryProperties'})
missing_catalog_registry = sorted(name for name in implemented_structs if name not in types)
if missing_catalog_header:
    errors.append('catalog physical-device structs missing from locked header: ' + ', '.join(missing_catalog_header))
if missing_catalog_registry:
    errors.append('catalog physical-device structs missing from locked registry: ' + ', '.join(missing_catalog_registry))

descriptor_match = re.search(r'kValidatedQueryDescriptors\s*=\s*\{\{(.*?)\}\};', catalog, re.S)
descriptor_extensions = set()
if descriptor_match:
    for _, scope, extension, _, _ in re.findall(r'\{"([^"]+)",\s*"([^"]+)",\s*"([^"]*)",\s*(\d+),\s*"([^"]+)"\}', descriptor_match.group(1)):
        if scope == 'device-extension' and extension:
            descriptor_extensions.add(extension)
missing_descriptor_registry = sorted(descriptor_extensions - set(extensions))
if missing_descriptor_registry:
    errors.append('catalog descriptor extensions missing from locked registry: ' + ', '.join(missing_descriptor_registry))
if not descriptor_extensions.issubset(validated_extensions):
    errors.append('catalog descriptor extensions exceed validated runtime coverage')

instance_match = re.search(r'kInstanceDependencyCandidates\s*=\s*\{(.*?)\};', catalog, re.S)
instance_candidates = set(re.findall(r'"(VK_[A-Za-z0-9_]+)"', instance_match.group(1))) if instance_match else set()
missing_instance_registry = sorted(instance_candidates - set(extensions))
if missing_instance_registry:
    errors.append('instance dependency candidates missing from locked registry: ' + ', '.join(missing_instance_registry))

if errors:
    for error in errors:
        print(f'FAIL: {error}')
    raise SystemExit(1)
print(f'PASS upstream registry coverage: header={lock["headerVersion"]} queryableExtensions={len(queryable_extensions)} validatedExtensions={len(validated_extensions)} stable={len(validated_extensions) - len(provisional_validated)} provisional={len(provisional_validated)} queryableStructs={len(queryable_structs)} queriedStructs={len(queried_structs)} serializedSTypes={len(serialized_stypes)}')
