#!/usr/bin/env python3
import argparse
import re
import subprocess
import sys
import tempfile
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', default=str(Path(__file__).resolve().parents[1]))
args = parser.parse_args()
root = Path(args.root).resolve()
kt_path = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
gradle_path = root / 'app/build.gradle.kts'
index_path = root / 'app/src/main/java/com/efishell/vulkanscope/VulkanSymbolIndex.kt'
generator_path = root / 'tools/generate_encyclopedia_symbols.py'
errors = []

def require(condition, message):
    if not condition:
        errors.append(message)

if not kt_path.is_file() or not gradle_path.is_file():
    raise SystemExit('required source files missing')
kt = kt_path.read_text(encoding='utf-8')
gradle = gradle_path.read_text(encoding='utf-8')

def function_body(name):
    match = re.search(r'private fun (?:[A-Za-z0-9_<>?.]+\.)?' + re.escape(name) + r'\s*\(', kt)
    if not match:
        return ''
    open_brace = kt.find('{', match.start())
    if open_brace < 0:
        return ''
    depth = 0
    in_string = False
    escaped = False
    for index in range(open_brace, len(kt)):
        char = kt[index]
        if in_string:
            if escaped:
                escaped = False
            elif char == '\\':
                escaped = True
            elif char == '"':
                in_string = False
            continue
        if char == '"':
            in_string = True
        elif char == '{':
            depth += 1
        elif char == '}':
            depth -= 1
            if depth == 0:
                return kt[match.start():index + 1]
    return ''

version_match = re.search(r'versionName\s*=\s*"([0-9.]+)"', gradle)
version_tuple = tuple(int(x) for x in version_match.group(1).split('.')) if version_match else (0,)
require(version_tuple >= (0, 80, 2), 'Encyclopedia contract requires version 0.80.2 or newer')
overview = function_body('OverviewPage')
require(overview, 'OverviewPage missing')
if version_tuple == (0, 80, 2):
    snapshot_pos = overview.find('CapabilitySectionCard("Capability snapshot")')
    encyclopedia_pos = overview.find('EncyclopediaOverviewCard(')
    analysis_pos = overview.find('analysisWorkspaceItems(')
    require(snapshot_pos >= 0 and encyclopedia_pos > snapshot_pos and analysis_pos > encyclopedia_pos, '0.80.2 Encyclopedia is not between Capability snapshot and Analysis workspace')
    encyclopedia = function_body('EncyclopediaOverviewCard')
else:
    encyclopedia = function_body('EncyclopediaPage')
    require('Page.Encyclopedia -> EncyclopediaPage()' in kt, 'successor Encyclopedia destination missing')
require(encyclopedia, 'Encyclopedia surface missing')
require('CapabilitySectionCard("Encyclopedia")' in encyclopedia, 'Encyclopedia design-system card missing')
require('ExpressiveSearchField(' in encyclopedia, 'Encyclopedia local symbol search missing')
for category in ['All', 'VkResult', 'Commands', 'VK_*', 'Types', 'Extensions']:
    require(f'"{category}"' in encyclopedia, f'Encyclopedia category missing: {category}')
for destination in ['Page.Extensions', 'Page.Profiles', 'Page.Video']:
    require(destination not in encyclopedia, f'Encyclopedia destination shortcut must be removed: {destination}')
require('ExpressiveAssistChip(' not in encyclopedia, 'Encyclopedia destination/action assist chips must be removed')
require('verticalScroll(' not in encyclopedia and 'LazyColumn(' not in encyclopedia, 'Encyclopedia introduces nested vertical scrolling')
require('Runtime evidence and registry/reference symbols are separate evidence classes.' in encyclopedia, 'registry/runtime evidence separation text missing')
require('does not prove hardware video decode/encode is absent' in encyclopedia, 'Vulkan Video Not applicable clarification missing')
require('VULKAN_COMMAND_SYMBOL_COUNT' in encyclopedia and 'VULKAN_TOKEN_SYMBOL_COUNT' in encyclopedia and 'VULKAN_TYPE_SYMBOL_COUNT' in encyclopedia, 'registry symbol census is not surfaced')
require(re.search(r'ENCYCLOPEDIA_VISIBLE_RESULT_LIMIT\s*=\s*24(?!\d)', kt) is not None, 'visible Encyclopedia result bound must remain 24')

required_concepts = [
    'Vulkan instance', 'Physical device', 'Logical device', 'Queue', 'Command buffer', 'Feature', 'Property', 'Limit',
    'Format', 'Layer', 'Extension', 'Surface / WSI', 'Swapchain', 'pNext', 'sType', 'Loader API version', 'Device API version', 'Driver version',
    'vk* command', 'vkCmd* command', 'vkQueue* command', 'Vk* type', 'VK_* token'
]
for concept in required_concepts:
    require(f'"{concept}"' in kt, f'core Encyclopedia concept missing: {concept}')
for state in ['Supported', 'Unsupported', 'Unavailable', 'Not applicable', 'Unknown']:
    require(f'"{state}"' in kt, f'evidence-state definition missing: {state}')

required_results = [
    'VK_SUCCESS','VK_NOT_READY','VK_TIMEOUT','VK_EVENT_SET','VK_EVENT_RESET','VK_INCOMPLETE',
    'VK_ERROR_OUT_OF_HOST_MEMORY','VK_ERROR_OUT_OF_DEVICE_MEMORY','VK_ERROR_INITIALIZATION_FAILED','VK_ERROR_DEVICE_LOST',
    'VK_ERROR_MEMORY_MAP_FAILED','VK_ERROR_LAYER_NOT_PRESENT','VK_ERROR_EXTENSION_NOT_PRESENT','VK_ERROR_FEATURE_NOT_PRESENT',
    'VK_ERROR_INCOMPATIBLE_DRIVER','VK_ERROR_TOO_MANY_OBJECTS','VK_ERROR_FORMAT_NOT_SUPPORTED','VK_ERROR_FRAGMENTED_POOL','VK_ERROR_UNKNOWN',
    'VK_ERROR_VALIDATION_FAILED','VK_ERROR_OUT_OF_POOL_MEMORY','VK_ERROR_INVALID_EXTERNAL_HANDLE','VK_ERROR_INVALID_OPAQUE_CAPTURE_ADDRESS',
    'VK_ERROR_FRAGMENTATION','VK_PIPELINE_COMPILE_REQUIRED','VK_ERROR_NOT_PERMITTED','VK_ERROR_SURFACE_LOST_KHR',
    'VK_ERROR_NATIVE_WINDOW_IN_USE_KHR','VK_SUBOPTIMAL_KHR','VK_ERROR_OUT_OF_DATE_KHR','VK_ERROR_INCOMPATIBLE_DISPLAY_KHR',
    'VK_ERROR_INVALID_SHADER_NV','VK_ERROR_IMAGE_USAGE_NOT_SUPPORTED_KHR','VK_ERROR_VIDEO_PICTURE_LAYOUT_NOT_SUPPORTED_KHR',
    'VK_ERROR_VIDEO_PROFILE_OPERATION_NOT_SUPPORTED_KHR','VK_ERROR_VIDEO_PROFILE_FORMAT_NOT_SUPPORTED_KHR',
    'VK_ERROR_VIDEO_PROFILE_CODEC_NOT_SUPPORTED_KHR','VK_ERROR_VIDEO_STD_VERSION_NOT_SUPPORTED_KHR',
    'VK_ERROR_INVALID_DRM_FORMAT_MODIFIER_PLANE_LAYOUT_EXT','VK_ERROR_PRESENT_TIMING_QUEUE_FULL_EXT',
    'VK_ERROR_FULL_SCREEN_EXCLUSIVE_MODE_LOST_EXT','VK_THREAD_IDLE_KHR','VK_THREAD_DONE_KHR','VK_OPERATION_DEFERRED_KHR',
    'VK_OPERATION_NOT_DEFERRED_KHR','VK_ERROR_INVALID_VIDEO_STD_PARAMETERS_KHR','VK_ERROR_COMPRESSION_EXHAUSTED_EXT',
    'VK_INCOMPATIBLE_SHADER_BINARY_EXT','VK_PIPELINE_BINARY_MISSING_KHR','VK_ERROR_NOT_ENOUGH_SPACE_KHR'
]
for name in required_results:
    require(f'"{name}"' in kt, f'authoritative VkResult glossary entry missing: {name}')
require('Command successfully completed' in kt, 'VK_SUCCESS meaning missing')
require('A return array was too small for the complete result' in kt, 'VK_INCOMPLETE meaning missing')
require('The logical or physical device was lost' in kt, 'VK_ERROR_DEVICE_LOST meaning missing')
require('The surface changed and the swapchain is no longer compatible' in kt, 'VK_ERROR_OUT_OF_DATE_KHR meaning missing')
require('not global capability evidence' in kt, 'VkResult interpretation guard missing')

require(index_path.is_file(), 'generated VulkanSymbolIndex.kt missing')
require(generator_path.is_file(), 'Encyclopedia symbol generator missing')
if index_path.is_file():
    index = index_path.read_text(encoding='utf-8')
    counts = {}
    for key in ['COMMAND','TOKEN','TYPE']:
        m = re.search(rf'VULKAN_{key}_SYMBOL_COUNT\s*=\s*(\d+)', index)
        counts[key] = int(m.group(1)) if m else 0
    require(counts['COMMAND'] >= 800, f'command symbol census unexpectedly small: {counts["COMMAND"]}')
    require(counts['TOKEN'] >= 6000, f'VK_* symbol census unexpectedly small: {counts["TOKEN"]}')
    require(counts['TYPE'] >= 2400, f'Vk* type census unexpectedly small: {counts["TYPE"]}')
    require('if (query.length < 2 || limit <= 0) return emptyList()' in index, 'large registry search lacks minimum query bound')
    require('if (out.size >= limit) return' in index, 'registry symbol search lacks visible result bound')
    for symbol in ['vkCreateInstance','vkGetPhysicalDeviceFeatures2','VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2','VK_KHR_SWAPCHAIN_EXTENSION_NAME','VkPhysicalDeviceFeatures2']:
        require(symbol in index, f'generated registry symbol missing: {symbol}')

if generator_path.is_file() and index_path.is_file():
    with tempfile.TemporaryDirectory(prefix='vulkanscope-encyclopedia-index-') as td:
        generated = Path(td) / 'VulkanSymbolIndex.kt'
        result = subprocess.run([
            sys.executable, str(generator_path), '--registry', str(root / 'registry/upstream/vk.xml'), '--output', str(generated)
        ], text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        require(result.returncode == 0, 'symbol index generator failed: ' + result.stdout.strip())
        if result.returncode == 0:
            require(generated.read_bytes() == index_path.read_bytes(), 'checked-in VulkanSymbolIndex.kt differs from locked-registry regeneration')

if errors:
    for error in errors:
        print('FAIL ' + error)
    raise SystemExit(1)
print('PASS 0.80.2 detailed local Vulkan Encyclopedia contract')
