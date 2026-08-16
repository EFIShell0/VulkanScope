from pathlib import Path
import re

root = Path(__file__).resolve().parents[1]
cpp = root / 'app/src/main/cpp'

unsupported = {
    'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_MEMORY_SCREEN_BUFFER_FEATURES_QNX',
    'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENTATION_PROPERTIES_OHOS',
}

alias_groups = [
    [
        'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COMPUTE_SHADER_DERIVATIVES_FEATURES_KHR',
        'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COMPUTE_SHADER_DERIVATIVES_FEATURES_NV',
    ],
    [
        'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COPY_MEMORY_INDIRECT_PROPERTIES_KHR',
        'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COPY_MEMORY_INDIRECT_PROPERTIES_NV',
    ],
    [
        'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_CLAMP_ZERO_ONE_FEATURES_EXT',
        'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_CLAMP_ZERO_ONE_FEATURES_KHR',
    ],
    [
        'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_OFFSET_FEATURES_EXT',
        'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_OFFSET_FEATURES_QCOM',
    ],
    [
        'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INDEX_TYPE_UINT8_FEATURES_EXT',
        'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INDEX_TYPE_UINT8_FEATURES_KHR',
    ],
    [
        'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINE_RASTERIZATION_FEATURES_EXT',
        'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINE_RASTERIZATION_FEATURES_KHR',
    ],
    [
        'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINE_RASTERIZATION_PROPERTIES_EXT',
        'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINE_RASTERIZATION_PROPERTIES_KHR',
    ],
    [
        'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MEMORY_DECOMPRESSION_FEATURES_EXT',
        'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MEMORY_DECOMPRESSION_FEATURES_NV',
    ],
    [
        'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ROBUSTNESS_2_FEATURES_EXT',
        'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ROBUSTNESS_2_FEATURES_KHR',
    ],
    [
        'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SWAPCHAIN_MAINTENANCE_1_FEATURES_EXT',
        'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SWAPCHAIN_MAINTENANCE_1_FEATURES_KHR',
    ],
    [
        'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VERTEX_ATTRIBUTE_DIVISOR_FEATURES_EXT',
        'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VERTEX_ATTRIBUTE_DIVISOR_FEATURES_KHR',
    ],
]

keep = {group[-1] for group in alias_groups}
remove = unsupported | {label for group in alias_groups for label in group[:-1]}

case_re = re.compile(r'(?ms)^        case ([A-Z0-9_]+): \{.*?^        \}\n')

def sanitize_case_file(path: Path) -> None:
    text = path.read_text(encoding='utf-8')
    kept = []
    removed = set()
    for match in case_re.finditer(text):
        label = match.group(1)
        if label in remove:
            removed.add(label)
            continue
        kept.append((match.start(), match.end()))
    if removed:
        chunks = []
        cursor = 0
        removed_any = False
        for match in case_re.finditer(text):
            label = match.group(1)
            if label in remove:
                chunks.append(text[cursor:match.start()])
                cursor = match.end()
                removed_any = True
        if removed_any:
            chunks.append(text[cursor:])
            text = ''.join(chunks)
    path.write_text(text, encoding='utf-8')

sanitize_case_file(cpp / 'extension_field_coverage_parity.inc')

runtime = cpp / 'runtime_extension_pnext_parity.inc'
s = runtime.read_text(encoding='utf-8')
for ext, type_name, struct_name in [
    ('VK_QNX_external_memory_screen_buffer', 'VkPhysicalDeviceExternalMemoryScreenBufferFeaturesQNX', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_MEMORY_SCREEN_BUFFER_FEATURES_QNX'),
    ('VK_OHOS_native_buffer', 'VkPhysicalDevicePresentationPropertiesOHOS', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENTATION_PROPERTIES_OHOS'),
]:
    pattern = re.compile(r'(?ms)^    if \(std::strcmp\(selectedExtension, "' + re.escape(ext) + r'"\) == 0\) \{\n        storage\.add<' + re.escape(type_name) + r'>\(' + re.escape(struct_name) + r', (?:true, false|false, true)\);\n        \+\+added;\n    \}\n')
    s, _ = pattern.subn('', s)
runtime.write_text(s, encoding='utf-8')

print('sanitized parity sources')
print('removed case/type aliases:', len(remove))
print('preserved canonical promoted alias variants:', ', '.join(sorted(keep)))
