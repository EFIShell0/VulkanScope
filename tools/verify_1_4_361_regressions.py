#!/usr/bin/env python3
import argparse
import hashlib
import json
import re
import xml.etree.ElementTree as ET
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--target-root')
parser.add_argument('--registry')
args = parser.parse_args()
root = Path(args.target_root).resolve() if args.target_root else Path(__file__).resolve().parents[1]
registry_path = Path(args.registry).resolve() if args.registry else root / 'registry/upstream/vk.xml'
errors = []
if not registry_path.is_file():
    raise SystemExit('canonical Vulkan 1.4.361 vk.xml input is missing')
registry = ET.parse(registry_path).getroot()
header_version = None
for node in registry.findall('./types/type'):
    if (node.findtext('name') or node.get('name')) == 'VK_HEADER_VERSION' and 'vulkan' in {x.strip() for x in (node.get('api') or '').split(',')}:
        match = re.search(r'VK_HEADER_VERSION\s+(\d+)', ''.join(node.itertext()))
        if match:
            header_version = int(match.group(1))
            break
if header_version != 361:
    errors.append(f'canonical registry header version is {header_version}, expected 361')
extension = next((node for node in registry.findall('./extensions/extension') if node.get('name') == 'VK_NV_private_data_base_handle'), None)
if extension is None:
    errors.append('VK_NV_private_data_base_handle is missing from canonical registry')
else:
    required_types = {node.get('name') for req in extension.findall('require') for node in req.findall('type') if node.get('name')}
    required_features = {(node.get('struct'), node.get('name')) for req in extension.findall('require') for node in req.findall('feature')}
    if 'VkPhysicalDevicePrivateDataBaseHandleFeaturesNV' not in required_types:
        errors.append('1.4.361 private-data-base-handle feature struct is missing from extension requirements')
    if ('VkPhysicalDevicePrivateDataBaseHandleFeaturesNV', 'privateDataBaseHandle') not in required_features:
        errors.append('1.4.361 privateDataBaseHandle feature member is missing from extension requirements')

lock_path = root / 'registry/registry_lock.json'
if not lock_path.is_file():
    errors.append('registry lock is missing')
else:
    lock = json.loads(lock_path.read_text(encoding='utf-8'))
    expected_sha = hashlib.sha256(registry_path.read_bytes()).hexdigest()
    if lock.get('apiBaseline') != 'Vulkan 1.4.361': errors.append('registry lock baseline is not Vulkan 1.4.361')
    if lock.get('headerVersion') != 361: errors.append('registry lock header version is not 361')
    if lock.get('registrySha256') != expected_sha: errors.append('registry lock SHA does not match canonical 1.4.361 input')

coverage_path = root / 'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt'
coverage = coverage_path.read_text(encoding='utf-8') if coverage_path.is_file() else ''
if '"VK_NV_private_data_base_handle"' not in coverage:
    errors.append('runtime validated extension coverage omits VK_NV_private_data_base_handle')
pnext_path = root / 'app/src/main/cpp/runtime_extension_pnext_generated.inc'
pnext = pnext_path.read_text(encoding='utf-8') if pnext_path.is_file() else ''
if 'VkPhysicalDevicePrivateDataBaseHandleFeaturesNV' not in pnext or 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRIVATE_DATA_BASE_HANDLE_FEATURES_NV' not in pnext:
    errors.append('runtime pNext scheduling omits VkPhysicalDevicePrivateDataBaseHandleFeaturesNV')
serializer_path = root / 'app/src/main/cpp/extension_field_coverage_generated.inc'
serializer = serializer_path.read_text(encoding='utf-8') if serializer_path.is_file() else ''
if 'privateDataBaseHandle' not in serializer or 'VkPhysicalDevicePrivateDataBaseHandleFeaturesNV' not in serializer:
    errors.append('report serializer omits privateDataBaseHandle')
cpp_path = root / 'app/src/main/cpp/vulkanscope.cpp'
cpp = cpp_path.read_text(encoding='utf-8') if cpp_path.is_file() else ''
if cpp.count('vulkanRegistryVersion\\\":\\\"1.4.361') < 3:
    errors.append('native complete-report provenance is not fully pinned to Vulkan 1.4.361')
if 'NewStringUTF(result.c_str())' in cpp:
    errors.append('large native probe result is still duplicated through JNI jstring')
if 'kMaxProbePublishedBytes = 64ULL * 1024ULL * 1024ULL' not in cpp:
    errors.append('native bounded result-publication ceiling is missing')
service_path = root / 'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt'
service = service_path.read_text(encoding='utf-8') if service_path.is_file() else ''
if service.count('): Boolean') < 3:
    errors.append('probe JNI methods do not use publication-status return values')
main_path = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
main = main_path.read_text(encoding='utf-8') if main_path.is_file() else ''
if 'val bytes = ByteArray(size.toInt())' not in main or 'File ended before the published probe result was complete.' not in main:
    errors.append('main-process exact-size checkpoint read contract is missing')
if 'isolated Vulkan probe process' in main or 'isolated probe process' in main:
    errors.append('production terminology overclaims Android isolatedProcess semantics')

reference_path = root / 'registry/generated/extension_reference.json'
if reference_path.is_file():
    reference = json.loads(reference_path.read_text(encoding='utf-8'))
    registry_names = {
        node.get('name') for node in registry.findall('./extensions/extension')
        if node.get('name') and ({'vulkan', 'vulkanbase'} & {x.strip() for x in (node.get('supported') or '').split(',') if x.strip()})
    }
    reference_names = {entry.get('name') for entry in reference.get('entries', []) if isinstance(entry, dict)}
    if reference_names != registry_names:
        errors.append(f'embedded extension registry census differs from vk.xml: registry={len(registry_names)} asset={len(reference_names)}')
else:
    errors.append('generated extension-reference census is missing')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS Vulkan 1.4.361 regressions: private-data-base-handle query/report coverage, canonical registry census, bounded direct publication, dedicated-process semantics')
