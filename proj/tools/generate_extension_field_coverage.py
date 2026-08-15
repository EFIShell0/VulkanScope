import re
import argparse
import xml.etree.ElementTree as ET
from pathlib import Path

root = Path(__file__).resolve().parents[1]
ap = argparse.ArgumentParser()
ap.add_argument('--registry', required=True)
ap.add_argument('--header', required=True)
ap.add_argument('--beta-header', required=False)
ap.add_argument('--out', default=str(root / 'app/src/main/cpp/extension_field_coverage_generated.inc'))
args = ap.parse_args()
header = Path(args.header)
registry_path = Path(args.registry)
out = Path(args.out)
text = header.read_text(encoding='utf-8', errors='ignore')
version_match = re.search(r'#define\s+VK_HEADER_VERSION\s+(\d+)', text)
if not version_match or int(version_match.group(1)) != 357:
    raise SystemExit("Canonical Vulkan-Headers 1.4.357 required for generation")
registry = ET.parse(registry_path).getroot()

structs = re.findall(r'typedef\s+struct\s+(VkPhysicalDevice\w*)\s*\{(.*?)\}\s*\1\s*;', text, re.S)
macros = dict(re.findall(r'#define\s+(VK_STRUCTURE_TYPE_[A-Z0-9_]+)\s+([-0-9]+)', text))

# Preserve Khronos' canonical acronym casing for types which contain OCP.
structs = [(name.replace("ShaderOcpMicroscalingTypes", "ShaderOCPMicroscalingTypes"), body) for name, body in structs]

# Canonical enum values from vk.xml. Runtime values are always emitted alongside the canonical names.
enum_values = {}
for enums in registry.findall('./enums'):
    if enums.get('type') != 'enum':
        continue
    enum_name = enums.get('name')
    if not enum_name:
        continue
    values = []
    for enum in enums.findall('enum'):
        name = enum.get('name')
        if not name or enum.get('alias'):
            continue
        raw = enum.get('value')
        if raw is not None:
            try:
                value = int(raw, 0)
            except ValueError:
                continue
            values.append((name, value))
        elif enum.get('bitpos') is not None:
            try:
                bit = int(enum.get('bitpos'))
                values.append((name, 1 << bit))
            except ValueError:
                pass
    # Keep one canonical name per numeric value to prevent duplicate switch cases.
    dedup = {}
    for name, value in values:
        dedup.setdefault(value, name)
    if dedup:
        enum_values[enum_name] = dict(sorted(dedup.items(), key=lambda kv: kv[0]))

bitmask_types = set()
bitmask_enum_group = {}
for t in registry.findall('./types/type'):
    if t.get('category') == 'bitmask':
        n = t.findtext('name')
        if n:
            bitmask_types.add(n)
            req = t.get('requires')
            if req:
                bitmask_enum_group[n] = req


def to_macro(struct_name):
    s = struct_name[2:]
    s = s.replace('Vulkan11', 'Vulkan_1_1').replace('Vulkan12', 'Vulkan_1_2').replace('Vulkan13', 'Vulkan_1_3').replace('Vulkan14', 'Vulkan_1_4')
    s = re.sub(r'([a-z0-9])([A-Z])', r'\1_\2', s)
    s = re.sub(r'([A-Za-z])([0-9])', r'\1_\2', s)
    s = re.sub(r'([0-9])([A-Za-z])', r'\1_\2', s)
    candidate = 'VK_STRUCTURE_TYPE_' + s.upper()
    if candidate in macros:
        return candidate
    if struct_name == 'VkPhysicalDeviceTextureCompressionASTC3DFeaturesEXT':
        return 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TEXTURE_COMPRESSION_ASTC_3D_FEATURES_EXT'
    return candidate


def parse_fields(body):
    fields = []
    for decl in body.split(';'):
        decl = ' '.join(decl.strip().split())
        if not decl:
            continue
        m = re.match(r'(.+?)\s+(\w+)(\s*\[([^\]]+)\])?$', decl)
        if not m:
            continue
        typ, name, _, arr = m.groups()
        if name in ('sType', 'pNext'):
            continue
        fields.append((typ.strip(), name, arr))
    return fields


def esc(s):
    return s.replace('\\', '\\\\').replace('"', '\\"')


def sanitize(s):
    return re.sub(r'[^A-Za-z0-9_]', '_', s)


def emit_enum_helper(lines, type_name, values):
    fn = f'generatedEnumName_{sanitize(type_name)}'
    lines.append(f'static const char* {fn}(int64_t value) {{')
    lines.append('    switch (value) {')
    for value, name in values.items():
        lines.append(f'        case {value}: return "{esc(name)}";')
    lines.append('        default: return nullptr;')
    lines.append('    }')
    lines.append('}')


def emit_flags_helper(lines, type_name, values):
    fn = f'generatedFlagsName_{sanitize(type_name)}'
    lines.append(f'static std::string {fn}(uint64_t raw) {{')
    lines.append('    if (raw == 0) return "0";')
    lines.append('    std::string out;')
    lines.append('    uint64_t known = 0;')
    # Prefer named bits; multi-bit enum values are still exact and will be emitted if the raw value matches.
    for value, name in values.items():
        if value == 0:
            continue
        if value & (value - 1) == 0:
            lines.append(f'    if ((raw & UINT64_C({value})) != 0) {{ if (!out.empty()) out += " | "; out += "{esc(name)}"; known |= UINT64_C({value}); }}')
    lines.append('    if (known != raw) { if (!out.empty()) out += " | "; std::ostringstream ss; ss << "0x" << std::uppercase << std::hex << (raw & ~known); out += ss.str(); }')
    lines.append('    return out;')
    lines.append('}')


def enum_helper_for(typ):
    if typ in bitmask_types:
        enum_group = bitmask_enum_group.get(typ)
        if enum_group in enum_values:
            return f'generatedFlagsName_{sanitize(enum_group)}'
        if typ in enum_values:
            return f'generatedFlagsName_{sanitize(typ)}'
    if typ in enum_values:
        return f'generatedEnumName_{sanitize(typ)}'
    return None

lines = []
lines.append('')
# Forward declarations for generated enum/bitmask name helpers.
for typ in sorted(enum_values):
    if typ in bitmask_types:
        continue
    lines.append(f'static const char* generatedEnumName_{sanitize(typ)}(int64_t value);')
for typ in sorted(bitmask_types):
    enum_group = bitmask_enum_group.get(typ)
    if enum_group in enum_values:
        lines.append(f'static std::string generatedFlagsName_{sanitize(enum_group)}(uint64_t raw);')
    elif typ in enum_values:
        lines.append(f'static std::string generatedFlagsName_{sanitize(typ)}(uint64_t raw);')
lines.append('')
# Emit enum/bitmask helpers only for types actually referenced by physical-device structs.
referenced_types = set()
for _struct_name, body in structs:
    if not re.search(r'\b(?:const\s+)?void\s*\*\s*pNext\b', body):
        continue
    for typ, _name, _arr in parse_fields(body):
        if typ in enum_values or typ in bitmask_types:
            referenced_types.add(typ)
for typ in sorted(referenced_types):
    if typ in bitmask_types:
        enum_group = bitmask_enum_group.get(typ)
        target = enum_group if enum_group in enum_values else typ if typ in enum_values else None
        if target:
            emit_flags_helper(lines, target, enum_values[target])
    elif typ in enum_values:
        emit_enum_helper(lines, typ, enum_values[typ])
lines.append('')


def emit_field(lines, typ, name, arr):
    member = f'value.{name}'
    enum_helper = enum_helper_for(typ)
    if '*' in typ:
        lines.append(f'    generatedEmitString(dst, section, "{esc(name)}", std::string("unavailable; unsupported pointer field type=") + "{esc(typ)}");')
        return
    if arr:
        if enum_helper and typ in bitmask_types:
            lines.append(f'    generatedEmitString(dst, section, "{esc(name)}", ([&]() {{ std::string joined; for (size_t i = 0; i < sizeof({member}) / sizeof({member}[0]); ++i) {{ if (i) joined += ", "; joined += {enum_helper}((uint64_t){member}[i]); }} return joined; }})());')
        elif enum_helper:
            lines.append(f'    generatedEmitString(dst, section, "{esc(name)}", ([&]() {{ std::string joined; for (size_t i = 0; i < sizeof({member}) / sizeof({member}[0]); ++i) {{ if (i) joined += ", "; const char* n = {enum_helper}((int64_t){member}[i]); joined += n ? (std::string(n) + " = " + std::to_string((int64_t){member}[i])) : std::to_string((int64_t){member}[i]); }} return joined; }})());')
        elif typ in ('char', 'int8_t', 'uint8_t'):
            if typ == 'char':
                lines.append(f'    generatedEmitString(dst, section, "{esc(name)}", {member}, sizeof({member}));')
            else:
                lines.append(f'    generatedEmitHexTyped(dst, section, "{esc(name)}", "{esc(typ)}", reinterpret_cast<const uint8_t*>({member}), sizeof({member}));')
        elif typ in ('VkBool32', 'uint32_t', 'int32_t', 'uint64_t', 'int64_t', 'VkFlags', 'VkDeviceSize', 'VkDeviceAddress'):
            lines.append(f'    generatedEmitArray(dst, section, "{esc(name)}", {member}, sizeof({member}) / sizeof({member}[0]));')
        else:
            lines.append(f'    generatedEmitHexTyped(dst, section, "{esc(name)}", "{esc(typ)}", reinterpret_cast<const uint8_t*>({member}), sizeof({member}));')
        return
    if enum_helper and typ in bitmask_types:
        lines.append(f'    generatedEmitString(dst, section, "{esc(name)}", generatedFlagsName_{sanitize(bitmask_enum_group.get(typ) if bitmask_enum_group.get(typ) in enum_values else typ)}(static_cast<uint64_t>(value.{name})));')
    elif enum_helper:
        lines.append(f'    generatedEmitString(dst, section, "{esc(name)}", ([&]() {{ const char* n = {enum_helper}(static_cast<int64_t>(value.{name})); return n ? std::string(n) + " = " + std::to_string(static_cast<int64_t>(value.{name})) : std::to_string(static_cast<int64_t>(value.{name})); }})());')
    elif typ == 'VkBool32':
        lines.append(f'    generatedEmitBool(dst, section, "{esc(name)}", value.{name});')
    elif typ in ('uint32_t', 'int32_t', 'uint64_t', 'int64_t', 'VkFlags', 'VkDeviceSize', 'VkDeviceAddress', 'size_t'):
        lines.append(f'    generatedEmitNumeric(dst, section, "{esc(name)}", value.{name});')
    elif typ in ('float', 'double'):
        lines.append(f'    generatedEmitString(dst, section, "{esc(name)}", std::to_string(value.{name}));')
    elif typ == 'VkExtent2D':
        lines.append(f'    generatedEmitString(dst, section, "{esc(name)}", (std::to_string(value.{name}.width) + " × " + std::to_string(value.{name}.height)));')
    elif typ == 'VkExtent3D':
        lines.append(f'    generatedEmitString(dst, section, "{esc(name)}", (std::to_string(value.{name}.width) + " × " + std::to_string(value.{name}.height) + " × " + std::to_string(value.{name}.depth)));')
    elif typ == 'VkComponentMapping':
        lines.append(f'    generatedEmitString(dst, section, "{esc(name)}", (std::to_string(value.{name}.r) + "," + std::to_string(value.{name}.g) + "," + std::to_string(value.{name}.b) + "," + std::to_string(value.{name}.a)));')
    elif typ.startswith('Vk'):
        lines.append(f'    generatedEmitHexTyped(dst, section, "{esc(name)}", "{esc(typ)}", reinterpret_cast<const uint8_t*>(&value.{name}), sizeof(value.{name}));')
    else:
        lines.append(f'    generatedEmitHexTyped(dst, section, "{esc(name)}", "{esc(typ)}", reinterpret_cast<const uint8_t*>(&value.{name}), sizeof(value.{name}));')

count = 0
lines.append('static void appendGeneratedStructFields(std::vector<GeneratedField>& dst, uint32_t sType, void* ptr) {')
lines.append('    switch (sType) {')
for struct_name, body in structs:
    macro = to_macro(struct_name)
    if macro not in macros:
        continue
    if struct_name in ('VkPhysicalDeviceProperties2', 'VkPhysicalDeviceFeatures2', 'VkPhysicalDeviceMemoryProperties', 'VkPhysicalDeviceFeatures'):
        continue
    if not re.search(r'\b(?:const\s+)?void\s*\*\s*pNext\b', body):
        continue
    fields = parse_fields(body)
    if not fields:
        continue
    lines.append(f'        case {macro}: {{')
    lines.append(f'            const auto& value = *reinterpret_cast<const {struct_name}*>(ptr);')
    lines.append(f'            const char* section = "Generated · {esc(struct_name)}";')
    for typ, name, arr in fields:
        emit_field(lines, typ, name, arr)
    lines.append('            break;')
    lines.append('        }')
    count += 1
lines.append('        default: break;')
lines.append('    }')
lines.append('}')
lines.append(f'static constexpr uint32_t kGeneratedPhysicalDeviceStructSerializerCount = {count};')
out.parent.mkdir(parents=True, exist_ok=True)
out.write_text('\n'.join(lines) + '\n', encoding='utf-8')
print('generated', count, 'serializers at', out)
