#!/usr/bin/env python3
import argparse
import re
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', default=None)
args = parser.parse_args()
root = Path(args.root).resolve() if args.root else Path(__file__).resolve().parents[1]
errors = []
cpp_path = root / 'app/src/main/cpp/vulkanscope.cpp'
kt_path = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
if not cpp_path.is_file() or not kt_path.is_file():
    raise SystemExit('compile-regression source inputs are missing')
cpp = cpp_path.read_text(encoding='utf-8')
kt = kt_path.read_text(encoding='utf-8')
collect_match = re.search(r'std::string collect\(jobject surfaceObject, JNIEnv\* env,.*?\n\}\n\nstd::string collectVulkanMetadata', cpp, re.S)
if not collect_match:
    errors.append('base collect function could not be isolated')
else:
    collect = collect_match.group(0)
    declarations = [
        'const auto baseInstanceExtensionEnumeration = enumerateInstanceExtensions(api);',
        'const auto& instanceExts = baseInstanceExtensionEnumeration.values;',
        'const bool surfaceExtensionAvailable = hasExtension(instanceExts, "VK_KHR_surface");',
        'const bool androidSurfaceExtensionAvailable = hasExtension(instanceExts, "VK_KHR_android_surface");',
        'const bool swapchainColorspaceAvailable = hasExtension(instanceExts, "VK_EXT_swapchain_colorspace");',
    ]
    for declaration in declarations:
        if declaration not in collect:
            errors.append('base collect declaration missing: ' + declaration)
    for identifier in ['swapchainColorspaceAvailable', 'surfaceExtensionAvailable', 'androidSurfaceExtensionAvailable', 'instanceExts']:
        first_use = collect.find(identifier)
        if first_use < 0:
            errors.append(f'base collect expected identifier missing: {identifier}')
    if 'unknown because instance-extension enumeration is incomplete' not in collect:
        errors.append('incomplete instance-extension enumeration must not be reported as definite extension absence')

extension_group_match = re.search(r'std::string collectVulkanExtensionGroup\(.*?\n\}\n\nstd::string', cpp, re.S)
if extension_group_match and re.search(r'auto\s+hasExt\s*=\s*\[&\]', extension_group_match.group(0)):
    errors.append('dead hasExt lambda remains in extension-group collector and fails -Werror')

bad_kv = 'kv("Device layer enumeration"'
if bad_kv in kt:
    errors.append('unresolved Kotlin kv helper call remains in HTML export')
expected_table = 'table("Device layer enumeration", "<th>Property</th><th>Value</th>"'
if expected_table not in kt:
    errors.append('Device layer enumeration HTML export is not routed through the existing table helper')


if 'ExpressiveFilterBar(devices.mapIndexed { index, device -> "GPU ${index + 1} · ${device.name.ifBlank { "Unknown" }.take(48)}" }, selectedIndex, onSelected)' in kt:
    errors.append('compile-breaking positional PhysicalDeviceSelector callback remains after arrowTint parameter insertion')
if 'ExpressiveFilterBar(devices.mapIndexed { index, device -> "GPU ${index + 1} · ${device.name.ifBlank { "Unknown" }.take(48)}" }, selectedIndex, onSelected = onSelected)' not in kt:
    errors.append('PhysicalDeviceSelector must bind the callback through named onSelected to preserve default-parameter type safety')

if 'ExpressiveTextButton("Cancel", onDismiss)' in kt or 'ExpressiveCancelButton(onDismiss)' in kt:
    errors.append('compile-breaking positional default-parameter Cancel callback remains')
if 'private fun ExpressiveCancelButton(enabled: Boolean = true, onClick: () -> Unit)' in kt:
    if kt.count('ExpressiveCancelButton(onClick = onDismiss)') != 2:
        errors.append('both update-dialog Cancel callbacks must use the named onClick argument')
else:
    if kt.count('ExpressiveTextButton("Cancel", onClick = onDismiss)') != 2:
        errors.append('both update-dialog Cancel callbacks must use the named onClick argument')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS compile regressions: native undeclared/dead-symbol guards and Kotlin HTML helper resolution')
