import json
import re
import sys
from pathlib import Path

root = Path(__file__).resolve().parents[1]
errors = []
gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
version = re.search(r'versionName\s*=\s*"([^"]+)"', gradle)
code = re.search(r'versionCode\s*=\s*(\d+)', gradle)
if not version or version.group(1) != '0.21.6': errors.append('versionName mismatch')
if not code or code.group(1) != '133': errors.append('versionCode mismatch')
abi_line = re.search(r'abiFilters \+= listOf\(([^\n]+)\)', gradle)
if not abi_line or any(x not in abi_line.group(1) for x in ['arm64-v8a', 'armeabi-v7a', 'x86_64']): errors.append('required ABI set is incomplete')
if '"x86"' in gradle: errors.append('x86 ABI must remain excluded')
manifest = (root / 'app/src/main/AndroidManifest.xml').read_text(encoding='utf-8')
if 'android.permission.INTERNET' in manifest: errors.append('INTERNET permission is forbidden')
catalog = (root / 'app/src/main/cpp/registry_query_catalog.h').read_text(encoding='utf-8')
for needle in ['kCatalogSchemaVersion = 6', 'kBaseline = "Vulkan 1.4.357"', 'findQueryDescriptor']:
    if needle not in catalog: errors.append(f'missing catalog requirement: {needle}')
manifest_json = json.loads((root / 'registry/generated/registry_query_manifest.json').read_text(encoding='utf-8'))
snapshot = json.loads((root / 'registry/generated/coverage_snapshot.json').read_text(encoding='utf-8'))
if manifest_json.get('baseline') != 'Vulkan 1.4.357': errors.append('generated manifest baseline mismatch')
if snapshot.get('baseline') != 'Vulkan 1.4.357': errors.append('coverage snapshot baseline mismatch')
cpp = (root / 'app/src/main/cpp/vulkanscope.cpp').read_text(encoding='utf-8')
kt = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
for needle in ['reportToText', 'reportToHtml', 'registryCoverage', 'instanceExtensions', 'deviceExtensions']:
    if needle not in kt + cpp: errors.append(f'missing report/export path: {needle}')
if re.search(r'\bTODO\b|\bFIXME\b', cpp + kt): errors.append('TODO/FIXME marker remains in production source')
# Every validated device-extension descriptor must have an implemented native query branch.
descriptor_groups = set(re.findall(r'\{\"([^\"]+)\", \"device-extension\"', catalog))
implemented_groups = set(re.findall(r'std::strcmp\(groupName, \"([^\"]+)\"\)', cpp))
implemented_extension_branches = set(re.findall(r'std::strcmp\(extensionName, "([^"]+)"\)', cpp))
extension_to_group = {}
for match in re.finditer(r'\{"([^"]+)", "device-extension", "([^"]+)"', catalog): extension_to_group[match.group(2)] = match.group(1)
implemented_groups.update(extension_to_group[name] for name in implemented_extension_branches if name in extension_to_group)
missing_groups = sorted(descriptor_groups - implemented_groups)
if missing_groups: errors.append('validated device-extension query groups missing native implementation: ' + ', '.join(missing_groups))
if not (root / 'tools/verify_canonical_vulkan_headers.py').exists(): errors.append('canonical header verifier missing')
if not (root / 'app/src/main/cpp/runtime_extension_pnext_generated.inc').exists(): errors.append('checked-in runtime pNext generated source missing')
if not (root / 'tools/generate_extension_pnext_query.py').exists(): errors.append('runtime pNext generator missing')
if not (root / 'tools/audit_capsviewer_parity.py').exists(): errors.append('CapsViewer/canonical parity audit tool missing')
if not (root / 'tools/compare_capsviewer_4_12.py').exists(): errors.append('field-level CapsViewer 4.12 source audit tool missing')
if not (root / 'rules/CAPSVIEWER_PARITY.md').exists(): errors.append('CapsViewer parity methodology missing')
if 'runtime_extension_pnext_generated.inc' not in cpp: errors.append('generated runtime pNext include missing from native source')
if '#include <runtime_extension_pnext_generated.inc>' not in cpp: errors.append('generated runtime pNext query is not included by native collector')
field_parity = (root / 'app/src/main/cpp/extension_field_coverage_parity.inc').read_text(encoding='utf-8')
runtime_parity = (root / 'app/src/main/cpp/runtime_extension_pnext_parity.inc').read_text(encoding='utf-8')
for forbidden in ['VkPhysicalDeviceExternalMemoryScreenBufferFeaturesQNX', 'VkPhysicalDevicePresentationPropertiesOHOS', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_MEMORY_SCREEN_BUFFER_FEATURES_QNX', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENTATION_PROPERTIES_OHOS']:
    if forbidden in field_parity or forbidden in runtime_parity: errors.append(f'platform-specific parity type must not be compiled for Android baseline: {forbidden}')
for left, right in [
    ('VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COMPUTE_SHADER_DERIVATIVES_FEATURES_KHR', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COMPUTE_SHADER_DERIVATIVES_FEATURES_NV'),
    ('VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COPY_MEMORY_INDIRECT_PROPERTIES_KHR', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COPY_MEMORY_INDIRECT_PROPERTIES_NV'),
    ('VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_CLAMP_ZERO_ONE_FEATURES_EXT', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_CLAMP_ZERO_ONE_FEATURES_KHR'),
    ('VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_OFFSET_FEATURES_EXT', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_OFFSET_FEATURES_QCOM'),
    ('VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INDEX_TYPE_UINT8_FEATURES_EXT', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INDEX_TYPE_UINT8_FEATURES_KHR'),
    ('VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINE_RASTERIZATION_FEATURES_EXT', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINE_RASTERIZATION_FEATURES_KHR'),
    ('VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINE_RASTERIZATION_PROPERTIES_EXT', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINE_RASTERIZATION_PROPERTIES_KHR'),
    ('VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MEMORY_DECOMPRESSION_FEATURES_EXT', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MEMORY_DECOMPRESSION_FEATURES_NV'),
    ('VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ROBUSTNESS_2_FEATURES_EXT', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ROBUSTNESS_2_FEATURES_KHR'),
    ('VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SWAPCHAIN_MAINTENANCE_1_FEATURES_EXT', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SWAPCHAIN_MAINTENANCE_1_FEATURES_KHR'),
    ('VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VERTEX_ATTRIBUTE_DIVISOR_FEATURES_EXT', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VERTEX_ATTRIBUTE_DIVISOR_FEATURES_KHR'),
]:
    if left in field_parity and right in field_parity: errors.append(f'duplicate VkStructureType alias cases remain: {left} / {right}')
cmake = (root / 'app/src/main/cpp/CMakeLists.txt').read_text(encoding='utf-8')
if 'find_package(Python3' in cmake or 'Python3_EXECUTABLE' in cmake: errors.append('Android CMake build must not require Python at configure/build time')

field_gen = (root / 'tools/generate_extension_field_coverage.py').read_text(encoding='utf-8')
if '--registry' not in field_gen or 'generatedEnumName_' not in field_gen or 'generatedFlagsName_' not in field_gen:
    errors.append('generated field coverage must consume vk.xml and emit canonical enum/flag semantics')
if 'generatedEmitHexTyped' not in cpp:
    errors.append('typed raw-byte fallback missing from native generated-field pipeline')
for forbidden in ['generatedEmitUnsupported', 'generatedEmitEnum(', 'generatedEmitFlags(', 'generatedEmitEnumArray', 'generatedEmitFlagsArray', 'generatedEmitFloat(']:
    if forbidden in cpp:
        errors.append(f'unused generated helper remains: {forbidden}')
if 'std::is_signed_v' not in cpp:
    errors.append('numeric fields must preserve signed/unsigned semantics in generated display')
if 'VkPhysicalDeviceShaderOcpMicroscalingTypesFeaturesEXT' in cpp or 'VkPhysicalDeviceShaderOcpMicroscalingTypesFeaturesEXT' in (root / 'app/src/main/cpp/runtime_extension_pnext_generated.inc').read_text(encoding='utf-8'):
    errors.append('non-canonical OCP microscaling type spelling remains')
if 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TEXTURE_COMPRESSION_ASTC_3_DFEATURES_EXT' in (root / 'app/src/main/cpp/runtime_extension_pnext_generated.inc').read_text(encoding='utf-8'):
    errors.append('non-canonical ASTC 3D structure type spelling remains')
if 'features.values[' in cpp or 'VkPhysicalDeviceLimitsLayout' in cpp or 'VkPhysicalDeviceSparsePropertiesLayout' in cpp:
    errors.append('native collector still contains non-canonical Vulkan struct access')
if 'std::vector<int32_t> copySrc' in cpp or 'std::vector<int32_t> copyDst' in cpp:
    errors.append('Vulkan 1.4 copy-layout arrays must use VkImageLayout')
if 'getPhysicalDeviceProperties(devices[i], raw.data())' in cpp or 'getPhysicalDeviceProperties(device, properties.data())' in cpp:
    errors.append('physical-device properties must use canonical VkPhysicalDeviceProperties storage')

probe_service = (root / 'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt').read_text(encoding='utf-8')
if 'ATOMIC_MOVE' not in probe_service and 'renameTo(file)' not in probe_service:
    errors.append('probe result publication must be atomic')
if 'VK_MAX_MEMORY_HEAPS' not in cpp or 'VK_MAX_MEMORY_TYPES' not in cpp:
    errors.append('memory enumeration safety limits must use canonical Vulkan array bounds')
if 'kMaxSparseImageFormatEntries' not in cpp:
    errors.append('sparse image property enumeration safety limit missing')
if re.search(r'^\s*/[/*]', cpp, re.MULTILINE) or re.search(r'^\s*/[/*]', kt, re.MULTILINE):
    errors.append('source-code comments are forbidden by PROJECT_RULES')
if 'GIT_TAG master' in cmake: errors.append('libadrenotools dependency must be pinned to an immutable commit')
if 'e3b1eec08173d6b825cd3ac88c885a63b621504a' not in cmake: errors.append('canonical Vulkan-Headers commit is not pinned')
if '#include <vulkan/vulkan.h>' not in cpp: errors.append('canonical Vulkan header is not used')
if '#define VK_ENABLE_BETA_EXTENSIONS 1' not in cpp: errors.append('provisional Vulkan extensions must be explicitly enabled before vulkan.h')
if 'VK_USE_PLATFORM_ANDROID_KHR' not in cpp: errors.append('Android Vulkan platform macro missing')
if errors:
    for error in errors: print(f'FAIL: {error}')
    raise SystemExit(1)
print('VulkanScope release verification: PASS')
print(f'version={version.group(1)} code={code.group(1)} baseline=Vulkan 1.4.357 schema=6 canonicalHeaders=e3b1eec08173d6b825cd3ac88c885a63b621504a')
