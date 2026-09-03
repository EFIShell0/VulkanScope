#!/usr/bin/env python3
import argparse
import re
import xml.etree.ElementTree as ET
from pathlib import Path

root = Path(__file__).resolve().parents[1]
parser = argparse.ArgumentParser()
parser.add_argument('--header', required=True)
parser.add_argument('--registry', required=True)
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
header = Path(args.header).read_text(encoding='utf-8', errors='ignore')
registry = ET.parse(args.registry).getroot()
registry_types = set()
for node in registry.findall('./types/type'):
    name = node.get('name') or node.findtext('name')
    if name:
        registry_types.add(name)
pnext = '\n'.join((root / 'app/src/main/cpp' / name).read_text(encoding='utf-8') for name in ['runtime_extension_pnext_generated.inc', 'runtime_extension_pnext_parity.inc'])
serializer = '\n'.join((root / 'app/src/main/cpp' / name).read_text(encoding='utf-8') for name in ['extension_field_coverage_generated.inc', 'extension_field_coverage_parity.inc'])
queried_pairs = re.findall(r'storage\.add<\s*(VkPhysicalDevice\w+)\s*>\(\s*(VK_STRUCTURE_TYPE_[A-Z0-9_]+)', pnext)
queried_structs = {name for name, _ in queried_pairs}
queried_stypes = {stype for _, stype in queried_pairs}
serialized_stypes = set(re.findall(r'case\s+(VK_STRUCTURE_TYPE_[A-Z0-9_]+)\s*:', serializer))
stype_values = resolved_stype_values(header)
queried_values = {stype_values.get(stype, stype) for stype in queried_stypes}
serialized_values = {stype_values.get(stype, stype) for stype in serialized_stypes}
missing_serializer_values = queried_values - serialized_values
missing_serializer = sorted(stype for stype in queried_stypes if stype_values.get(stype, stype) in missing_serializer_values)
if missing_serializer:
    errors.append('queried structure types missing alias-resolved serializer coverage: ' + ', '.join(missing_serializer))
for struct in sorted(queried_structs):
    if not re.search(r'\btypedef\s+struct\s+' + re.escape(struct) + r'\b', header):
        errors.append(f'queried struct missing from canonical header: {struct}')
    if struct not in registry_types:
        errors.append(f'queried struct missing from locked registry: {struct}')
for stype in sorted(serialized_stypes):
    if stype not in header:
        errors.append(f'serializer structure-type token missing from canonical header: {stype}')
if errors:
    for error in errors:
        print(f'FAIL: {error}')
    raise SystemExit(1)
print(f'PASS extension field coverage: queriedStructs={len(queried_structs)} queriedSTypes={len(queried_stypes)} serializedSTypes={len(serialized_stypes)}')
