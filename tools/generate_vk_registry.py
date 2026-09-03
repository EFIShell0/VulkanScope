#!/usr/bin/env python3
import argparse
import hashlib
import json
import re
import xml.etree.ElementTree as ET
from pathlib import Path


def text(path):
    return Path(path).read_text(encoding='utf-8', errors='ignore')


def parse_array(source, symbol, prefix=None):
    match = re.search(re.escape(symbol) + r'\s*=\s*\{(.*?)\};', source, re.S)
    if not match:
        raise SystemExit(f'missing catalog array: {symbol}')
    values = re.findall(r'"([A-Za-z0-9_:.-]+)"', match.group(1))
    return [value for value in values if prefix is None or value.startswith(prefix)]


def registry_model(path):
    registry = ET.parse(path).getroot()
    types = {}
    for node in registry.findall('./types/type'):
        name = node.get('name') or node.findtext('name')
        if name:
            types[name] = node
    def extends(name):
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
    queryable = set()
    provisional = set()
    for extension in registry.findall('./extensions/extension'):
        name = extension.get('name') or ''
        supported = {x.strip() for x in (extension.get('supported') or '').split(',') if x.strip()}
        if not name:
            continue
        extensions[name] = {
            'supported': sorted(supported),
            'provisional': extension.get('provisional', '').lower() == 'true',
            'platform': extension.get('platform') or '',
            'type': extension.get('type') or ''
        }
        if 'vulkan' not in supported or extension.get('type') != 'device' or (extension.get('platform') or '') not in {'', 'android', 'provisional'}:
            continue
        matched = False
        for require in extension.findall('require'):
            for type_node in require.findall('type'):
                name_type = type_node.get('name') or ''
                if name_type.startswith('VkPhysicalDevice') and {'VkPhysicalDeviceFeatures2', 'VkPhysicalDeviceProperties2'} & extends(name_type):
                    matched = True
        if matched:
            queryable.add(name)
            if extension.get('provisional', '').lower() == 'true':
                provisional.add(name)
    return types, extensions, queryable, provisional


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--registry', required=True)
    parser.add_argument('--header')
    parser.add_argument('--catalog', required=True)
    parser.add_argument('--coverage', required=True)
    parser.add_argument('--lock', required=True)
    parser.add_argument('--out', required=True)
    parser.add_argument('--require-complete-extension-coverage', action='store_true')
    args = parser.parse_args()
    lock = json.loads(text(args.lock))
    registry_bytes = Path(args.registry).read_bytes()
    if hashlib.sha256(registry_bytes).hexdigest() != lock['registrySha256']:
        raise SystemExit('registry SHA-256 mismatch')
    catalog = text(args.catalog)
    coverage = text(args.coverage)
    types, extensions, queryable_extensions, provisional_queryable = registry_model(args.registry)
    if args.header:
        header = text(args.header)
        header_version = re.search(r'#define\s+VK_HEADER_VERSION\s+(\d+)\b', header)
        if not header_version or int(header_version.group(1)) != int(lock['headerVersion']):
            raise SystemExit(f'header version mismatch: expected {lock["headerVersion"]}')
    schema_match = re.search(r'kCatalogSchemaVersion\s*=\s*(\d+)', catalog)
    baseline_match = re.search(r'kBaseline\s*=\s*"([^"]+)"', catalog)
    header_baseline_match = re.search(r'kHeaderBaseline\s*=\s*"([^"]+)"', catalog)
    report_schema_match = re.search(r'kReportSchema\s*=\s*"([^"]+)"', catalog)
    token_count_match = re.search(r'kRuntimeRegistryTokenReferenceCount\s*=\s*(\d+)', catalog)
    if not all([schema_match, baseline_match, header_baseline_match, report_schema_match, token_count_match]):
        raise SystemExit('catalog metadata is incomplete')
    if baseline_match.group(1) != lock['apiBaseline']:
        raise SystemExit('catalog baseline does not match registry lock')
    structs = parse_array(catalog, 'kImplementedPhysicalDeviceStructs', 'VkPhysicalDevice')
    groups = parse_array(catalog, 'kValidatedRuntimeQueryGroups')
    instance_candidates = parse_array(catalog, 'kInstanceDependencyCandidates', 'VK_')
    missing_registry_structs = sorted(name for name in structs if name not in types)
    if missing_registry_structs:
        raise SystemExit('catalog structs missing from locked registry: ' + ', '.join(missing_registry_structs))
    coverage_match = re.search(r'VALIDATED_PHYSICAL_DEVICE_QUERY_EXTENSIONS\s*=\s*setOf\((.*?)\n\)', coverage, re.S)
    if not coverage_match:
        raise SystemExit('validated extension coverage set is missing')
    validated_extensions = sorted(set(re.findall(r'"(VK_[A-Za-z0-9_]+)"', coverage_match.group(1))))
    provisional_validated = sorted(set(validated_extensions) & provisional_queryable)
    if args.require_complete_extension_coverage:
        missing = sorted(queryable_extensions - set(validated_extensions))
        extra = sorted(set(validated_extensions) - queryable_extensions)
        if missing or extra:
            raise SystemExit(f'locked registry extension coverage mismatch: missing={missing} extra={extra}')
    manifest = {
        'schemaVersion': int(schema_match.group(1)),
        'generator': 'VulkanScope reproducible registry-query audit manifest',
        'baseline': lock['apiBaseline'],
        'registryRepository': lock['registryRepository'],
        'registryRef': lock['registryRef'],
        'registryPath': lock['registryPath'],
        'registrySha256': lock['registrySha256'],
        'bundledRegistryPath': lock['bundledRegistryPath'],
        'publishedDate': lock['publishedDate'],
        'headerRepository': lock['headerRepository'],
        'headerCommit': lock['headerCommit'],
        'headerTag': lock['headerTag'],
        'headerVersion': int(lock['headerVersion']),
        'headerBaseline': header_baseline_match.group(1),
        'reportSchema': report_schema_match.group(1),
        'implementedPhysicalDeviceStructCount': len(structs),
        'implementedPhysicalDeviceStructs': structs,
        'validatedRuntimeQueryGroupCount': len(groups),
        'validatedRuntimeQueryGroups': groups,
        'validatedPhysicalDeviceQueryExtensionCount': len(validated_extensions),
        'validatedPhysicalDeviceQueryExtensions': validated_extensions,
        'validatedStablePhysicalDeviceQueryExtensionCount': len(validated_extensions) - len(provisional_validated),
        'validatedProvisionalPhysicalDeviceQueryExtensionCount': len(provisional_validated),
        'validatedProvisionalPhysicalDeviceQueryExtensions': provisional_validated,
        'runtimeRegistryTokenReferenceCount': int(token_count_match.group(1)),
        'instanceDependencyCandidateCount': len(instance_candidates),
        'instanceDependencyCandidates': instance_candidates,
        'notes': 'Counts and lists are regenerated from the SHA-256-locked Vulkan registry plus the checked-in native query catalog and runtime extension coverage. Android physical-device provider coverage resolves registry type aliases, includes device extensions available without a foreign platform header, and tracks provisional providers separately.'
    }
    out = Path(args.out)
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_text(json.dumps(manifest, indent=2) + '\n', encoding='utf-8')
    print(f'PASS registry manifest: structs={len(structs)} groups={len(groups)} validatedExtensions={len(validated_extensions)} stable={len(validated_extensions)-len(provisional_validated)} provisional={len(provisional_validated)} registryQueryableExtensions={len(queryable_extensions)}')


if __name__ == '__main__':
    main()
