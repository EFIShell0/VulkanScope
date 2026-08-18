import argparse
import json
import re
import sys
from pathlib import Path
import xml.etree.ElementTree as ET


def parse_registry(path):
    root = ET.parse(path).getroot()
    types = {}
    for t in root.findall('./types/type'):
        name = t.get('name')
        if not name:
            n = t.find('name')
            name = n.text if n is not None else None
        if name:
            types[name] = t
    extensions = {}
    for ext in root.findall('./extensions/extension'):
        name = ext.get('name')
        if not name:
            continue
        required_types = sorted({x.get('name') for x in ext.findall('./require/type') if x.get('name')})
        required_commands = sorted({x.get('name') for x in ext.findall('./require/command') if x.get('name')})
        extensions[name] = {
            'supported': ext.get('supported', ''),
            'type_names': required_types,
            'command_names': required_commands,
        }
    return types, extensions


def struct_fields(type_node):
    fields = []
    if type_node is None:
        return fields
    for m in type_node.findall('./member'):
        n = m.find('name')
        t = m.find('type')
        if n is None or t is None:
            continue
        fields.append({'name': n.text or '', 'type': t.text or ''})
    return fields


def implemented_structs(header_text):
    return set(re.findall(r'typedef struct (VkPhysicalDevice[A-Za-z0-9_]+)', header_text))


def s_type_values(header_text):
    result = {}
    for match in re.finditer(r'#define\s+(VK_STRUCTURE_TYPE_[A-Z0-9_]+)\s+(-?\d+)', header_text):
        result[match.group(1)] = int(match.group(2))
    return result


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--registry', required=True)
    ap.add_argument('--header', required=True)
    ap.add_argument('--out', required=True)
    ap.add_argument('--header-out', required=False)
    args = ap.parse_args()

    types, extensions = parse_registry(args.registry)
    header_text = Path(args.header).read_text(encoding='utf-8', errors='ignore')
    implemented = implemented_structs(header_text)
    stypes = s_type_values(header_text)

    relevant = {}
    for ext_name, ext in extensions.items():
        structs = []
        for type_name in ext['type_names']:
            if not type_name.startswith('VkPhysicalDevice'):
                continue
            node = types.get(type_name)
            structs.append({
                'name': type_name,
                'implemented_in_vulkan_min_h': type_name in implemented,
                'fields': struct_fields(node),
            })
        if structs:
            relevant[ext_name] = {
                'supported': ext['supported'],
                'structs': structs,
                'commands': ext['command_names'],
            }

    physical_structs = sorted({s['name'] for v in relevant.values() for s in v['structs']})
    implemented_structs = sorted({s['name'] for v in relevant.values() for s in v['structs'] if s['implemented_in_vulkan_min_h']})
    missing_structs = sorted(set(physical_structs) - set(implemented_structs))
    manifest = {
        'schemaVersion': 4,
        'generator': 'VulkanScope registry-driven query generator',
        'registrySource': 'Khronos Vulkan vk.xml',
        'registryPath': str(Path(args.registry).as_posix()),
        'physicalDeviceStructCount': len(physical_structs),
        'implementedStructCount': len(implemented_structs),
        'missingStructCount': len(missing_structs),
        'implementedPhysicalDeviceStructs': implemented_structs,
        'missingPhysicalDeviceStructs': missing_structs,
        'extensions': relevant,
        'knownStructureTypeDefines': stypes,
    }
    out = Path(args.out)
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_text(json.dumps(manifest, indent=2, ensure_ascii=False, sort_keys=True) + '\n', encoding='utf-8')

    if args.header_out:
        hp = Path(args.header_out)
        hp.parent.mkdir(parents=True, exist_ok=True)
        lines = [
            '#pragma once',
            '#include <array>',
            '#include <cstddef>',
            'namespace vulkanscope_registry {',
            'inline constexpr const char* kBaseline = "Vulkan 1.4.360";',
            'inline constexpr const char* kGeneratorMode = "offline registry-driven validated query catalog";',
            'inline constexpr const char* kHeaderBaseline = "Vulkan 1.4.360 / official header baseline";',
            'inline constexpr const char* kReportSchema = "4";',
            f'inline constexpr std::size_t kRegistryPhysicalDeviceStructCount = {len(physical_structs)};',
            f'inline constexpr std::size_t kRegistryImplementedStructCount = {len(implemented_structs)};',
            f'inline constexpr std::size_t kRegistryMissingStructCount = {len(missing_structs)};',
            'inline constexpr std::array<const char*, %d> kImplementedPhysicalDeviceStructs = {' % len(implemented_structs),
        ]
        lines += ['    \"%s\",' % x for x in implemented_structs]
        lines += ['};', '}', '']
        hp.write_text('\n'.join(lines), encoding='utf-8')

    missing = [
        (ext_name, s['name'])
        for ext_name, ext in relevant.items()
        for s in ext['structs']
        if not s['implemented_in_vulkan_min_h']
    ]
    print(json.dumps({'extensions': len(relevant), 'physicalDeviceStructs': manifest['physicalDeviceStructCount'], 'implemented': manifest['implementedStructCount'], 'missing': missing}, indent=2))


if __name__ == '__main__':
    main()
