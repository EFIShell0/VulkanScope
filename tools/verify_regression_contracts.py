#!/usr/bin/env python3
import hashlib
import json
import re
import sys
from pathlib import Path

root = Path(__file__).resolve().parents[1]
errors = []
gradle_probe = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
version_probe = re.search(r'versionName\s*=\s*"([^"]+)"', gradle_probe)
contracts = [json.loads(path.read_text(encoding='utf-8')) for path in sorted((root / 'tests/golden').glob('*_regression_contract.json'))]
contract = next((item for item in reversed(contracts) if version_probe and item.get('successorVersion') == version_probe.group(1)), None)
if contract is None:
    raise SystemExit('no regression contract matches the current successor version')
lock = json.loads((root / 'registry/registry_lock.json').read_text(encoding='utf-8'))
manifest = json.loads((root / 'registry/generated/registry_query_manifest.json').read_text(encoding='utf-8'))
snapshot = json.loads((root / 'registry/generated/coverage_snapshot.json').read_text(encoding='utf-8'))

def digest(path):
    return hashlib.sha256((root / path).read_bytes()).hexdigest()

allowed_runtime_changes = contract.get('allowedRuntimeChanges', {})
allowed_runtime_additions = contract.get('allowedRuntimeAdditions', {})
for path, addition in allowed_runtime_additions.items():
    if path in contract['runtimeFileSha256']:
        errors.append(f'allowed runtime addition already exists in predecessor runtime set: {path}')
        continue
    target = root / path
    if not target.is_file():
        errors.append(f'allowed runtime addition is missing: {path}')
        continue
    actual = hashlib.sha256(target.read_bytes()).hexdigest()
    if actual != addition.get('successorSha256'):
        errors.append(f'allowed runtime addition does not match its successor hash: {path}')
    if not addition.get('categories') or not addition.get('evidence') or not addition.get('regressionTest'):
        errors.append(f'allowed runtime addition lacks category/evidence/regression-test provenance: {path}')
unknown_allowed = sorted(set(allowed_runtime_changes) - set(contract['runtimeFileSha256']))
if unknown_allowed:
    errors.append('allowed runtime change is not part of the locked runtime file set: ' + ', '.join(unknown_allowed))
for path, expected in contract['runtimeFileSha256'].items():
    actual = digest(path)
    allowed = allowed_runtime_changes.get(path)
    if allowed is None:
        if actual != expected:
            errors.append(f'production baseline changed without an explicit contract update: {path}')
        continue
    if actual == expected:
        errors.append(f'allowed runtime change did not change the predecessor bytes: {path}')
    if actual != allowed.get('successorSha256'):
        errors.append(f'allowed runtime change does not match its successor hash: {path}')
    if not allowed.get('categories') or not allowed.get('evidence') or not allowed.get('regressionTest'):
        errors.append(f'allowed runtime change lacks category/evidence/regression-test provenance: {path}')

gradle_path = root / 'app/build.gradle.kts'
gradle = gradle_path.read_text(encoding='utf-8')
version = re.search(r'versionName\s*=\s*"([^"]+)"', gradle)
code = re.search(r'versionCode\s*=\s*(\d+)', gradle)
if not version or version.group(1) != contract['successorVersion']:
    errors.append('successor versionName does not match the regression contract')
if not code or int(code.group(1)) != contract['successorVersionCode']:
    errors.append('successor versionCode does not match the regression contract')
normalized = re.sub(r'versionCode\s*=\s*\d+', 'versionCode = <VERSION_CODE>', gradle)
normalized = re.sub(r'versionName\s*=\s*"[^"]+"', 'versionName = "<VERSION_NAME>"', normalized)
if hashlib.sha256(normalized.encode('utf-8')).hexdigest() != contract['normalizedBuildGradleSha256']:
    errors.append('build configuration changed outside version metadata without an explicit contract update')

catalog_path = root / 'app/src/main/cpp/registry_query_catalog.h'
catalog = catalog_path.read_text(encoding='utf-8')

def array_strings(symbol, prefix=None):
    match = re.search(re.escape(symbol) + r'\s*=\s*\{(.*?)\};', catalog, re.S)
    if not match:
        errors.append(f'catalog array missing: {symbol}')
        return []
    values = re.findall(r'"([A-Za-z0-9_:.-]+)"', match.group(1))
    if prefix is not None:
        values = [x for x in values if x.startswith(prefix)]
    return values

schema_match = re.search(r'kCatalogSchemaVersion\s*=\s*(\d+)', catalog)
if not schema_match or int(schema_match.group(1)) != contract['catalogSchemaVersion']:
    errors.append('catalog schema version drifted from the golden contract')
qgroups = array_strings('kValidatedRuntimeQueryGroups')
if qgroups != contract['validatedRuntimeQueryGroups']:
    errors.append('validated runtime query-group list drifted from the golden contract')
structs = array_strings('kImplementedPhysicalDeviceStructs', 'VkPhysicalDevice')
if structs != contract['implementedPhysicalDeviceStructs']:
    errors.append('implemented physical-device struct list drifted from the golden contract')
instance_candidates = array_strings('kInstanceDependencyCandidates', 'VK_')
if instance_candidates != contract['instanceDependencyCandidates']:
    errors.append('instance dependency candidate list drifted from the golden contract')

coverage_text = (root / 'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt').read_text(encoding='utf-8')
coverage_match = re.search(r'VALIDATED_PHYSICAL_DEVICE_QUERY_EXTENSIONS\s*=\s*setOf\((.*?)\n\)', coverage_text, re.S)
if not coverage_match:
    errors.append('validated physical-device extension coverage set is missing')
    coverage_extensions = []
else:
    coverage_extensions = sorted(set(re.findall(r'"(VK_[A-Za-z0-9_]+)"', coverage_match.group(1))))
if coverage_extensions != contract['validatedPhysicalDeviceQueryExtensions']:
    errors.append('validated physical-device extension coverage drifted from the golden contract')

pnext_extensions = set()
for name in ['runtime_extension_pnext_generated.inc', 'runtime_extension_pnext_parity.inc']:
    text = (root / 'app/src/main/cpp' / name).read_text(encoding='utf-8')
    pnext_extensions.update(re.findall(r'std::strcmp\(selectedExtension,\s*"(VK_[A-Za-z0-9_]+)"\)', text))
if sorted(pnext_extensions) != coverage_extensions:
    missing = sorted(set(coverage_extensions) - pnext_extensions)
    extra = sorted(pnext_extensions - set(coverage_extensions))
    errors.append(f'pNext scheduling parity mismatch: missing={missing[:12]} extra={extra[:12]}')

descriptor_match = re.search(r'kValidatedQueryDescriptors\s*=\s*\{\{(.*?)\}\};', catalog, re.S)
descriptors = []
if descriptor_match:
    descriptors = re.findall(r'\{"([^"]+)",\s*"([^"]+)",\s*"([^"]*)",\s*(\d+),\s*"([^"]+)"\}', descriptor_match.group(1))
if len(descriptors) != len(contract['validatedRuntimeQueryGroups']):
    errors.append('query descriptor count does not match validated query-group count')
descriptor_groups = [x[0] for x in descriptors]
if len(descriptor_groups) != len(set(descriptor_groups)) or set(descriptor_groups) != set(contract['validatedRuntimeQueryGroups']):
    errors.append('query descriptor content does not match validated query-group list')
descriptor_extensions = {x[2] for x in descriptors if x[1] == 'device-extension' and x[2]}
if not descriptor_extensions.issubset(set(coverage_extensions)):
    errors.append('catalog contains a device-extension query outside validated runtime extension coverage')

for key in ['registryRepository', 'registryRef', 'registryPath', 'registrySha256', 'bundledRegistryPath', 'publishedDate', 'headerRepository', 'headerCommit', 'headerTag', 'headerVersion']:
    if manifest.get(key) != lock.get(key):
        errors.append(f'registry manifest provenance mismatch: {key}')
if manifest.get('baseline') != lock.get('apiBaseline'):
    errors.append('registry manifest baseline does not match registry lock')
if manifest.get('implementedPhysicalDeviceStructs') != structs:
    errors.append('registry manifest struct list does not match native catalog')
if manifest.get('implementedPhysicalDeviceStructCount') != len(structs):
    errors.append('registry manifest struct count does not match native catalog')
if manifest.get('validatedRuntimeQueryGroups') != qgroups:
    errors.append('registry manifest query-group list does not match native catalog')
if manifest.get('validatedRuntimeQueryGroupCount') != len(qgroups):
    errors.append('registry manifest query-group count does not match native catalog')
if manifest.get('validatedPhysicalDeviceQueryExtensions') != coverage_extensions:
    errors.append('registry manifest extension coverage does not match Kotlin runtime coverage')
if manifest.get('validatedPhysicalDeviceQueryExtensionCount') != len(coverage_extensions):
    errors.append('registry manifest extension coverage count mismatch')
provisional_extensions = manifest.get('validatedProvisionalPhysicalDeviceQueryExtensions', [])
if manifest.get('validatedProvisionalPhysicalDeviceQueryExtensionCount') != len(provisional_extensions):
    errors.append('registry manifest provisional extension count mismatch')
if manifest.get('validatedStablePhysicalDeviceQueryExtensionCount') != len(coverage_extensions) - len(provisional_extensions):
    errors.append('registry manifest stable extension count mismatch')
if set(provisional_extensions) - set(coverage_extensions):
    errors.append('registry manifest provisional extensions exceed runtime coverage')
if manifest.get('instanceDependencyCandidates') != instance_candidates:
    errors.append('registry manifest instance candidate list does not match native catalog')
if manifest.get('instanceDependencyCandidateCount') != len(instance_candidates):
    errors.append('registry manifest instance candidate count mismatch')

snapshot_expect = {
    'baseline': lock['apiBaseline'],
    'registryRef': lock['registryRef'],
    'registrySha256': lock['registrySha256'],
    'publishedDate': lock['publishedDate'],
    'catalogSchemaVersion': contract['catalogSchemaVersion'],
    'headerCommit': lock['headerCommit'],
    'headerVersion': lock['headerVersion'],
    'implementedPhysicalDeviceStructCount': len(structs),
    'validatedRuntimeQueryGroupCount': len(qgroups),
    'validatedPhysicalDeviceQueryExtensionCount': len(coverage_extensions),
    'validatedStablePhysicalDeviceQueryExtensionCount': len(coverage_extensions) - len(provisional_extensions),
    'validatedProvisionalPhysicalDeviceQueryExtensionCount': len(provisional_extensions),
}

for key, expected in snapshot_expect.items():
    if snapshot.get(key) != expected:
        errors.append(f'coverage snapshot mismatch: {key}')

cmake = (root / 'app/src/main/cpp/CMakeLists.txt').read_text(encoding='utf-8')
if f'GIT_TAG {lock["headerCommit"]}' not in cmake:
    errors.append('CMake Vulkan-Headers pin does not match registry lock')
if f'VK_HEADER_VERSION[ \\t]+{lock["headerVersion"]}' not in cmake:
    errors.append('CMake Vulkan header-version gate does not match registry lock')

if errors:
    for error in errors:
        print(f'FAIL: {error}')
    raise SystemExit(1)
print(f'PASS regression contracts: baseline={contract["baselineVersion"]} successor={contract["successorVersion"]} runtimeFiles={len(contract["runtimeFileSha256"])} queryGroups={len(qgroups)} extensions={len(coverage_extensions)} structs={len(structs)}')
