import argparse
import re
import xml.etree.ElementTree as ET
from pathlib import Path

ap = argparse.ArgumentParser()
ap.add_argument('--registry', required=True)
ap.add_argument('--header', required=True)
ap.add_argument('--out', required=True)
args = ap.parse_args()

root = ET.parse(args.registry).getroot()
header = Path(args.header).read_text(encoding='utf-8', errors='ignore')
out = Path(args.out)

types = {}
for node in root.findall('./types/type'):
    name = node.get('name')
    if not name:
        n = node.find('name')
        name = n.text if n is not None else None
    if name:
        types[name] = node

ext_providers = {}
ext_protect = {}
for ext in root.findall('./extensions/extension'):
    name = ext.get('name')
    if not name or ext.get('supported') in ('disabled', 'provisional'):
        continue
    protect = ext.get('protect', '')
    for req in ext.findall('./require'):
        for t in req.findall('type'):
            type_name = t.get('name')
            if type_name and type_name.startswith('VkPhysicalDevice'):
                ext_providers.setdefault(type_name, set()).add(name)
                if protect:
                    ext_protect.setdefault(type_name, set()).add(protect)

header_structs = set(re.findall(r'typedef\s+struct\s+(VkPhysicalDevice\w*)\s*\{', header))
header_structs = {n.replace('ShaderOcpMicroscalingTypes', 'ShaderOCPMicroscalingTypes') for n in header_structs}

def stype_for(name):
    exact = {
        'VkPhysicalDeviceTextureCompressionASTC3DFeaturesEXT': 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TEXTURE_COMPRESSION_ASTC_3D_FEATURES_EXT',
        'VkPhysicalDeviceShaderOCPMicroscalingTypesFeaturesEXT': 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_OCP_MICROSCALING_TYPES_FEATURES_EXT',
    }
    if name in exact:
        return exact[name]
    node = types.get(name)
    if node is not None:
        for member in node.findall('member'):
            if member.findtext('name') == 'sType':
                values = member.get('values')
                if values:
                    return values
    s = name[2:]
    s = s.replace('Vulkan11', 'Vulkan_1_1').replace('Vulkan12', 'Vulkan_1_2').replace('Vulkan13', 'Vulkan_1_3').replace('Vulkan14', 'Vulkan_1_4').replace('TextureCompressionASTC3D', 'TextureCompressionASTC_3D').replace('ShaderOCPMicroscalingTypes', 'Shader_OCP_Microscaling_Types')
    s = re.sub(r'([a-z0-9])([A-Z])', r'\1_\2', s)
    s = re.sub(r'([A-Za-z])([0-9])', r'\1_\2', s)
    s = re.sub(r'([0-9])([A-Za-z])', r'\1_\2', s)
    return 'VK_STRUCTURE_TYPE_' + s.upper()

entries = []
for name in sorted(header_structs):
    if name in {
        'VkPhysicalDeviceFeatures', 'VkPhysicalDeviceFeatures2',
        'VkPhysicalDeviceProperties', 'VkPhysicalDeviceProperties2',
        'VkPhysicalDeviceMemoryProperties', 'VkPhysicalDeviceMemoryProperties2',
        'VkPhysicalDeviceGroupProperties', 'VkPhysicalDeviceImageFormatInfo2',
        'VkPhysicalDeviceExternalBufferInfo', 'VkPhysicalDeviceExternalFenceInfo',
        'VkPhysicalDeviceExternalSemaphoreInfo', 'VkPhysicalDeviceExternalImageFormatInfo',
    }:
        continue
    node = types.get(name)
    if node is None:
        continue
    extends = {x.strip() for x in node.get('structextends', '').split(',') if x.strip()}
    feature = 'VkPhysicalDeviceFeatures2' in extends
    prop = 'VkPhysicalDeviceProperties2' in extends
    if not feature and not prop:
        continue
    providers = sorted(ext_providers.get(name, set()))
    if not providers:
        continue
    s_type = stype_for(name)
    if s_type not in header:
        continue
    entries.append((name, s_type, feature, prop, providers, sorted(ext_protect.get(name, set()))))

lines = []
lines.append('struct RuntimePNextStorage {')
lines.append('    struct Base { virtual ~Base() = default; virtual VkBaseOutStructure* base() = 0; };')
lines.append('    template <typename T> struct Item final : Base {')
lines.append('        T value{};')
lines.append('        explicit Item(VkStructureType sType) { value.sType = sType; value.pNext = nullptr; }')
lines.append('        VkBaseOutStructure* base() override { return reinterpret_cast<VkBaseOutStructure*>(&value); }')
lines.append('    };')
lines.append('    std::vector<std::unique_ptr<Base>> items;')
lines.append('    VkBaseOutStructure* featureHead = nullptr;')
lines.append('    VkBaseOutStructure* featureTail = nullptr;')
lines.append('    VkBaseOutStructure* propertyHead = nullptr;')
lines.append('    VkBaseOutStructure* propertyTail = nullptr;')
lines.append('    template <typename T> T* add(VkStructureType sType, bool feature, bool property) {')
lines.append('        auto item = std::make_unique<Item<T>>(sType);')
lines.append('        T* value = &item->value;')
lines.append('        VkBaseOutStructure* node = item->base();')
lines.append('        items.emplace_back(std::move(item));')
lines.append('        if (feature) { if (!featureHead) featureHead = node; else featureTail->pNext = node; featureTail = node; }')
lines.append('        if (property) { if (!propertyHead) propertyHead = node; else propertyTail->pNext = node; propertyTail = node; }')
lines.append('        return value;')
lines.append('    }')
lines.append('};')
lines.append('')
lines.append('static size_t appendAllGeneratedExtensionPNext(const char* selectedExtension, const std::vector<VkExtensionProperties>& devExts, RuntimePNextStorage& storage) {')
lines.append('    if (!selectedExtension || selectedExtension[0] == 0 || !hasExtension(devExts, selectedExtension)) return 0;')
lines.append('    size_t added = 0;')

for name, s_type, feature, prop, providers, protects in entries:
    provider_check = ' || '.join(f'std::strcmp(selectedExtension, "{p}") == 0' for p in providers)
    lines.append(f'    if ({provider_check}) {{')
    if protects:
        for protect in protects:
            lines.append(f'#if defined({protect})')
    if feature and prop:
        lines.append(f'        storage.add<{name}>({s_type}, true, false);')
        lines.append(f'        storage.add<{name}>({s_type}, false, true);')
        lines.append('        added += 2;')
    elif feature:
        lines.append(f'        storage.add<{name}>({s_type}, true, false);')
        lines.append('        ++added;')
    else:
        lines.append(f'        storage.add<{name}>({s_type}, false, true);')
        lines.append('        ++added;')
    if protects:
        for _ in protects:
            lines.append('#endif')
    lines.append('    }')

lines.append('    return added;')
lines.append('}')
lines.append(f'static constexpr uint32_t kGeneratedRuntimePNextTypeCount = {len(entries)};')

out.parent.mkdir(parents=True, exist_ok=True)
content='\n'.join(lines) + '\n'
assert 'static size_t appendAllGeneratedExtensionPNext' in content
out.write_text(content, encoding='utf-8')
print(f'PASS generated runtime pNext coverage for {len(entries)} extension property/feature structs')
