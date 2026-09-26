import argparse
import hashlib
import json
import re
import xml.etree.ElementTree as ET
from pathlib import Path

ap = argparse.ArgumentParser()
ap.add_argument('--root', default='.')
ap.add_argument('--skip-version', action='store_true')
args = ap.parse_args()
root = Path(args.root).resolve()
errors = []

def need(condition, message):
    if not condition:
        errors.append(message)

def text(relative):
    return (root / relative).read_text(encoding='utf-8')

def sha(relative):
    return hashlib.sha256((root / relative).read_bytes()).hexdigest()

required = [
    'app/build.gradle.kts',
    'app/src/main/AndroidManifest.xml',
    'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt',
    'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt',
    'app/src/main/cpp/CMakeLists.txt',
    'app/src/main/cpp/registry_query_catalog.h',
    'app/src/main/cpp/runtime_extension_pnext_generated.inc',
    'app/src/main/cpp/extension_field_coverage_generated.inc',
    'app/src/main/cpp/video_registry_generated.h',
    'app/src/main/cpp/vulkanscope.cpp',
    'registry/registry_lock.json',
    'registry/video_registry_lock.json',
    'registry/upstream/vk.xml',
    'registry/upstream/video.xml',
    'registry/generated/registry_query_manifest.json',
    'registry/generated/coverage_snapshot.json',
    'registry/generated/extension_reference.json',
    'registry/generated/resource_budget.json',
    'rules/PROJECT_RULES.md'
]
for relative in required:
    need((root / relative).is_file(), f'missing {relative}')
if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)

build = text('app/build.gradle.kts')
main = text('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
manifest = text('app/src/main/AndroidManifest.xml')
cmake = text('app/src/main/cpp/CMakeLists.txt')
catalog = text('app/src/main/cpp/registry_query_catalog.h')
pnext = text('app/src/main/cpp/runtime_extension_pnext_generated.inc')
fields = text('app/src/main/cpp/extension_field_coverage_generated.inc')
video_header = text('app/src/main/cpp/video_registry_generated.h')
cpp = text('app/src/main/cpp/vulkanscope.cpp')
coverage = text('app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt')
rules = text('rules/PROJECT_RULES.md')
lock = json.loads(text('registry/registry_lock.json'))
video_lock = json.loads(text('registry/video_registry_lock.json'))
manifest_json = json.loads(text('registry/generated/registry_query_manifest.json'))
snapshot = json.loads(text('registry/generated/coverage_snapshot.json'))
extref = json.loads(text('registry/generated/extension_reference.json'))
budget = json.loads(text('registry/generated/resource_budget.json'))

if not args.skip_version:
    need('versionCode = 1405' in build, 'versionCode is not 1405')
    need('versionName = "1.4.5"' in build, 'versionName is not 1.4.5')
    need('## Release 1.4.5 Googlebook OS evidence, Vulkan 1.4.364 registry and full correctness audit' in rules, '1.4.5 rules section missing')

expected_sha = '4cfe3c137f3a95c15275b1cfda0aee407dbf131de2005a3552aa41c49f895c89'
expected_commit = 'b0c3dd6851e22621f194306d511b9253e4c1577f'
need(lock.get('apiBaseline') == 'Vulkan 1.4.364', 'registry baseline is not Vulkan 1.4.364')
need(lock.get('registryRef') == '1.4.364', 'registry ref is not 1.4.364')
need(lock.get('publishedDate') == '2026-09-25', 'registry published date lock drifted')
need(lock.get('registrySha256') == expected_sha == sha('registry/upstream/vk.xml'), 'vk.xml content lock mismatch')
need(lock.get('headerCommit') == expected_commit, 'Vulkan-Headers commit lock mismatch')
need(lock.get('headerTag') == 'v1.4.364', 'Vulkan-Headers tag lock mismatch')
need(lock.get('headerVersion') == 364, 'Vulkan-Headers version lock mismatch')
need(f'GIT_TAG {expected_commit}' in cmake, 'CMake exact Vulkan-Headers commit pin missing')
need(expected_sha in cmake, 'CMake bundled registry hash gate missing')
need('#define VK_HEADER_VERSION[ \\t]+364' in cmake, 'CMake header 364 semantic gate missing')
need('VK_INTEL_device_info' in cmake, 'CMake 1.4.364 sentinel extension gate missing')
need('kBaseline = "Vulkan 1.4.364"' in catalog, 'native query baseline is not Vulkan 1.4.364')
need('Vulkan 1.4.364 compile headers; validated query catalog Vulkan 1.4.364' in catalog, 'native header/query provenance drifted')
need(cpp.count('vulkanRegistryVersion\\\":\\\"1.4.364') >= 3, 'native checkpoint provenance does not consistently report 1.4.364')
need('locked Vulkan 1.4.364 vk.xml' in cpp, 'Vulkan Video recipe does not identify current locked registry')

vk_root = ET.parse(root / 'registry/upstream/vk.xml').getroot()
header_versions = []
for node in vk_root.findall('./types/type'):
    if node.findtext('name') == 'VK_HEADER_VERSION' and 'vulkan' in {x.strip() for x in (node.get('api') or '').split(',')}:
        match = re.search(r'VK_HEADER_VERSION\s+(\d+)', ''.join(node.itertext()))
        if match:
            header_versions.append(int(match.group(1)))
need(364 in header_versions, f'vk.xml Vulkan header version mismatch: {header_versions}')

extensions = {}
for extension in vk_root.findall('./extensions/extension'):
    supported = {x.strip() for x in (extension.get('supported') or '').split(',') if x.strip()}
    if {'vulkan', 'vulkanbase'} & supported:
        name = extension.get('name') or ''
        if name:
            extensions[name] = extension
need(len(extensions) == 477, f'Vulkan extension census is {len(extensions)}, expected 477')
intel = extensions.get('VK_INTEL_device_info')
need(intel is not None, 'VK_INTEL_device_info missing from locked registry')
if intel is not None:
    need(intel.get('type') == 'device', 'VK_INTEL_device_info is not a device extension')
    need(intel.get('number') == '709', 'VK_INTEL_device_info extension number drifted')
    need(intel.get('depends') == 'VK_KHR_get_physical_device_properties2,VK_VERSION_1_1', 'VK_INTEL_device_info dependency drifted')

types = {}
for node in vk_root.findall('./types/type'):
    name = node.get('name') or node.findtext('name')
    if name:
        types[name] = node
intel_type = types.get('VkPhysicalDeviceInfoPropertiesINTEL')
need(intel_type is not None, 'VkPhysicalDeviceInfoPropertiesINTEL missing')
if intel_type is not None:
    need('VkPhysicalDeviceProperties2' in {x.strip() for x in (intel_type.get('structextends') or '').split(',')}, 'INTEL device-info struct is not a properties2 provider')
    value_members = [m.findtext('name') for m in intel_type.findall('member') if m.findtext('name') not in {'sType', 'pNext'}]
    need(value_members == ['deviceIpVersionArch', 'deviceIpVersionRelease', 'deviceIpVersionRevision'], f'INTEL device-info member census drifted: {value_members}')

need('"VK_INTEL_device_info"' in coverage, 'VK_INTEL_device_info missing from validated runtime coverage')
need('std::strcmp(selectedExtension, "VK_INTEL_device_info") == 0' in pnext, 'INTEL extension pNext scheduler missing')
need('storage.add<VkPhysicalDeviceInfoPropertiesINTEL>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INFO_PROPERTIES_INTEL, false, true);' in pnext, 'INTEL properties struct is not scheduled correctly')
for member in ['deviceIpVersionArch', 'deviceIpVersionRelease', 'deviceIpVersionRevision']:
    need(f'generatedEmitNumeric(dst, section, "{member}", value.{member});' in fields, f'INTEL device-info field not serialized: {member}')
need('kGeneratedRuntimePNextTypeCount = 104' in pnext, 'generated pNext struct census is not 104')
need('kGeneratedPhysicalDeviceStructSerializerCount = 117' in fields, 'generated field serializer census is not 117')
need(manifest_json.get('baseline') == 'Vulkan 1.4.364', 'registry query manifest baseline mismatch')
need(manifest_json.get('validatedPhysicalDeviceQueryExtensionCount') == 305, 'validated physical-device extension census is not 305')
need(manifest_json.get('validatedStablePhysicalDeviceQueryExtensionCount') == 300, 'stable physical-device extension census is not 300')
need(manifest_json.get('validatedProvisionalPhysicalDeviceQueryExtensionCount') == 5, 'provisional physical-device extension census is not 5')
need(snapshot.get('validatedPhysicalDeviceQueryExtensionCount') == 305, 'coverage snapshot provider census mismatch')
need(len(extref.get('entries', [])) == 477 and extref.get('baseline') == 'Vulkan 1.4.364', 'extension reference census/baseline mismatch')
need(budget.get('registeredVulkanExtensionCensus') == 477, 'resource budget extension census mismatch')

video_sha = 'd018b914014c06605e367a3b929670511e6f6de2f225c405a8b5e2d912408b76'
need(sha('registry/upstream/video.xml') == video_sha, 'video.xml changed unexpectedly')
need(video_lock.get('sha256') == video_sha, 'video registry content lock mismatch')
need(video_lock.get('vulkanBaseline') == 'Vulkan 1.4.364', 'video registry Vulkan baseline mismatch')
need(video_lock.get('vulkanRegistrySha256') == expected_sha, 'video registry Vulkan cross-lock mismatch')
need(video_sha in video_header and expected_sha in video_header, 'generated video registry provenance is stale')

need('<uses-feature android:name="android.hardware.type.pc" android:required="false" />' in manifest, 'optional Android PC uses-feature declaration missing or restrictive')
need('context.packageManager.hasSystemFeature(PackageManager.FEATURE_PC)' in main, 'Android PC form-factor evidence query missing')
need('context.packageManager.hasSystemFeature(PackageManager.FEATURE_FREEFORM_WINDOW_MANAGEMENT)' in main, 'freeform window-management evidence query missing')
need('private const val CHROMEOS_ARC_FEATURE = "org.chromium.arc"' in main, 'ChromeOS ARC feature evidence regressed')
need('ChromeOS ARC detected; Googlebook identity is not inferred' in main, 'ChromeOS/Googlebook non-inference disclosure missing')
need('Android PC form factor with freeform window management detected; Googlebook-compatible environment evidence only' in main, 'Googlebook PC/freeform evidence-only disclosure missing')
need('Googlebook identity is not exposed by a documented public Android API' in main, 'Googlebook identity unavailable state missing')
need('Googlebook OS version' in main and 'Unavailable through documented public Android APIs' in main, 'Googlebook OS version unavailable state missing')
for forbidden in ['Build.MODEL.contains("Googlebook"', 'Build.PRODUCT.contains("Googlebook"', 'Build.BRAND.contains("Googlebook"', 'Build.MANUFACTURER.contains("Googlebook"', 'Build.FINGERPRINT.contains("Googlebook"']:
    need(forbidden not in main, f'forbidden Googlebook identity inference present: {forbidden}')
for token in [
    'CapabilityKeyValue("Android PC form factor", if (isAndroidPcFormFactor(context)) "Detected" else "Not detected")',
    'CapabilityKeyValue("Freeform window management", if (hasFreeformWindowManagement(context)) "Detected" else "Not detected")',
    'CapabilityKeyValue("Googlebook environment", googlebookEnvironmentEvidence(context))',
    'CapabilityKeyValue("Googlebook OS version", googlebookOsVersionEvidence())',
    'put("androidPcFormFactor", isAndroidPcFormFactor(context))',
    'put("freeformWindowManagement", hasFreeformWindowManagement(context))',
    'put("googlebookEnvironmentEvidence", googlebookEnvironmentEvidence(context))',
    'put("googlebookOsVersion", googlebookOsVersionEvidence())',
    'appendLine("Googlebook environment: ${googlebookEnvironmentEvidence(context)}")',
    'appendLine("Googlebook OS version: ${googlebookOsVersionEvidence()}")',
    '"Googlebook environment" to htmlEscape(googlebookEnvironmentEvidence(context))',
    '"Googlebook OS version" to htmlEscape(googlebookOsVersionEvidence())'
]:
    need(token in main, f'Googlebook/desktop report surface missing: {token}')

need('CapabilityKeyValue("Current published specification", "Vulkan® 1.4.364 · 2026-09-25")' in main, 'current specification label is stale')
need('CapabilityKeyValue("Collection baseline", registryCoverage.baseline)' in main, 'collection baseline remains conflated with published spec')
need('Runtime absence is not an Unsupported claim' in main, 'runtime absence certainty disclosure missing')

need('NavigationRail(' in main and 'NavigationRailItem(' in main and 'ShortNavigationBar(' in main and 'ShortNavigationBarItem(' in main, 'adaptive Material 3 navigation contract missing')
need('val isExpandedWindow = configuration.screenWidthDp >= 600' in main, 'Googlebook/freeform expanded-window navigation breakpoint missing')
need('val useRail = isLandscape || isExpandedWindow || isTelevision' in main, 'navigation rail does not adapt to landscape, expanded windows and TV')
need('Modifier.width(if (expandedTextLayout) 112.dp else 88.dp).fillMaxHeight()' in main, 'large-text rail width adaptation missing')
need('.fillMaxHeight().verticalScroll(rememberScrollState()).focusGroup()' in main, 'navigation rail is not scrollable/focus grouped')
need('.heightIn(min = 64.dp)' in main, 'navigation rail target minimum height regressed')
need('maxLines = if (expandedTextLayout) 2 else 1' in main, 'large-text rail labels do not permit two lines')
need('contentDescription = "Open ${trademarkVulkanDisplayText(title)}"' in main, 'destination action TalkBack label missing')
need('"VulkanScope · ${trademarkVulkanDisplayText(page.title)}"' in main, 'large-text app header adaptation missing')
need('CapabilitySectionCard("Export complete report")' in main and 'Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp))' in main, 'report export actions are not stacked for accessibility')
need(main.count('SharedStoragePermissionActionButton(') >= 2 and main.count('Modifier.fillMaxWidth(),\n                        completeReportReady && !exportPreparing') >= 2, 'report export actions are not full-width independently reachable controls')
need('liveRegion = LiveRegionMode.Polite' in main, 'live-region accessibility semantics missing')
need('semantics { heading() }' in main or 'heading()' in main, 'heading semantics missing')

for permission in ['android.permission.INTERNET', 'android.permission.ACCESS_NETWORK_STATE']:
    need(permission in manifest, f'expected existing permission missing: {permission}')
for forbidden_permission in ['android.permission.READ_PHONE_STATE', 'android.permission.READ_CONTACTS', 'android.permission.GET_ACCOUNTS', 'android.permission.ACCESS_FINE_LOCATION', 'android.permission.ACCESS_COARSE_LOCATION']:
    need(forbidden_permission not in manifest, f'forbidden sensitive permission present: {forbidden_permission}')
need(manifest.count('android:exported="true"') == 1, 'unexpected exported Android component')
need('android:exported="false"' in manifest and 'android:name=".VulkanProbeService"' in manifest, 'probe service export hardening regressed')
need('isMinifyEnabled = true' in build and 'isShrinkResources = true' in build, 'release shrink/resource optimization is disabled')
for abi in ['"arm64-v8a"', '"armeabi-v7a"', '"x86_64"']:
    need(abi in build, f'required ABI missing: {abi}')
need('"x86"' not in re.sub(r'"x86_64"', '', build), 'x86 ABI is unexpectedly enabled')
for hardening in ['-fstack-protector-strong', '-Wl,-z,relro', '-Wl,-z,now']:
    need(hardening in cmake, f'native hardening flag missing: {hardening}')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('release_1405 verifier: PASS Vulkan=1.4.364 extensions=477 physicalDeviceProviders=305 stable=300 provisional=5 GooglebookIdentity=inference-forbidden')
