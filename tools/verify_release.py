import json
import re
from pathlib import Path

root = Path(__file__).resolve().parents[1]
errors = []
gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
version = re.search(r'versionName\s*=\s*"([^"]+)"', gradle)
code = re.search(r'versionCode\s*=\s*(\d+)', gradle)
if not version or version.group(1) != '0.15.5': errors.append('versionName mismatch')
if not code or code.group(1) != '53': errors.append('versionCode mismatch')
abi_line = re.search(r'abiFilters \+= listOf\(([^\n]+)\)', gradle)
if not abi_line or any(x not in abi_line.group(1) for x in ['arm64-v8a', 'armeabi-v7a', 'x86_64']): errors.append('required ABI set is incomplete')
if '"x86"' in gradle: errors.append('x86 ABI must remain excluded')
manifest = (root / 'app/src/main/AndroidManifest.xml').read_text(encoding='utf-8')
if 'android.permission.INTERNET' in manifest: errors.append('INTERNET permission is forbidden')
catalog = (root / 'app/src/main/cpp/registry_query_catalog.h').read_text(encoding='utf-8')
for needle in ['kCatalogSchemaVersion = 6', 'kBaseline = "Vulkan 1.4.357"', 'findQueryDescriptor']:
    if needle not in catalog: errors.append(f'missing catalog requirement: {needle}')
manifest_json = json.loads((root / 'registry/generated/registry_query_manifest.json').read_text(encoding='utf-8'))
snapshot = json.loads((root / 'registry/generated/coverage_snapshot.json').read_text(encoding='utf-8'))
if manifest_json.get('baseline') != 'Vulkan 1.4.357': errors.append('generated manifest baseline mismatch')
if snapshot.get('baseline') != 'Vulkan 1.4.357': errors.append('coverage snapshot baseline mismatch')
cpp = (root / 'app/src/main/cpp/vulkanscope.cpp').read_text(encoding='utf-8')
kt = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
for needle in ['reportToText', 'reportToHtml', 'registryCoverage', 'instanceExtensions', 'deviceExtensions']:
    if needle not in kt + cpp: errors.append(f'missing report/export path: {needle}')
if re.search(r'\bTODO\b|\bFIXME\b', cpp + kt): errors.append('TODO/FIXME marker remains in production source')
cmake = (root / 'app/src/main/cpp/CMakeLists.txt').read_text(encoding='utf-8')
if 'GIT_TAG master' in cmake: errors.append('libadrenotools dependency must be pinned to an immutable commit')
if errors:
    for error in errors: print(f'FAIL: {error}')
    raise SystemExit(1)
print('VulkanScope release verification: PASS')
print(f'version={version.group(1)} code={code.group(1)} baseline=Vulkan 1.4.357 schema=6')
