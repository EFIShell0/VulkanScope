#!/usr/bin/env python3
import json
import re
import sys
from pathlib import Path

root = Path(__file__).resolve().parents[1]
if len(sys.argv) != 4:
    raise SystemExit('usage: verify_registry_catalog.py <manifest.json> <header.h> <catalog.h>')
manifest = json.loads(Path(sys.argv[1]).read_text(encoding='utf-8'))
header = Path(sys.argv[2]).read_text(encoding='utf-8', errors='ignore')
catalog = Path(sys.argv[3]).read_text(encoding='utf-8', errors='ignore')
lock = json.loads((root / 'registry/registry_lock.json').read_text(encoding='utf-8'))
errors = []

def array_strings(symbol, prefix=None):
    match = re.search(re.escape(symbol) + r'\s*=\s*\{(.*?)\};', catalog, re.S)
    if not match:
        errors.append(f'missing catalog array: {symbol}')
        return []
    values = re.findall(r'"([A-Za-z0-9_:.-]+)"', match.group(1))
    if prefix is not None:
        values = [value for value in values if value.startswith(prefix)]
    return values

header_version = re.search(r'#define\s+VK_HEADER_VERSION\s+(\d+)\b', header)
if not header_version or int(header_version.group(1)) != int(lock['headerVersion']):
    errors.append('canonical header version does not match registry lock')
structs = array_strings('kImplementedPhysicalDeviceStructs', 'VkPhysicalDevice')
groups = array_strings('kValidatedRuntimeQueryGroups')
instance_candidates = array_strings('kInstanceDependencyCandidates', 'VK_')
for struct in structs:
    if not re.search(r'\b(?:typedef\s+)?struct\s+' + re.escape(struct) + r'\b', header):
        errors.append(f'catalog struct missing from canonical header: {struct}')
if manifest.get('implementedPhysicalDeviceStructs') != structs or manifest.get('implementedPhysicalDeviceStructCount') != len(structs):
    errors.append('manifest implemented-struct catalog mismatch')
if manifest.get('validatedRuntimeQueryGroups') != groups or manifest.get('validatedRuntimeQueryGroupCount') != len(groups):
    errors.append('manifest validated-query-group catalog mismatch')
if manifest.get('instanceDependencyCandidates') != instance_candidates or manifest.get('instanceDependencyCandidateCount') != len(instance_candidates):
    errors.append('manifest instance-dependency catalog mismatch')
for key in ['registryRepository', 'registryRef', 'registryPath', 'registrySha256', 'bundledRegistryPath', 'publishedDate', 'headerRepository', 'headerCommit', 'headerTag', 'headerVersion']:
    if manifest.get(key) != lock.get(key):
        errors.append(f'manifest provenance mismatch: {key}')
descriptor_match = re.search(r'kValidatedQueryDescriptors\s*=\s*\{\{(.*?)\}\};', catalog, re.S)
descriptor_groups = []
if descriptor_match:
    descriptor_groups = [row[0] for row in re.findall(r'\{"([^"]+)",\s*"([^"]+)",\s*"([^"]*)",\s*(\d+),\s*"([^"]+)"\}', descriptor_match.group(1))]
if len(descriptor_groups) != len(set(descriptor_groups)) or set(descriptor_groups) != set(groups):
    errors.append('native query descriptors do not match validated query-group catalog')
if errors:
    for error in errors:
        print(f'FAIL: {error}')
    raise SystemExit(1)
print(f'PASS registry catalog: structs={len(structs)} groups={len(groups)} instanceCandidates={len(instance_candidates)} header={lock["headerVersion"]}')
