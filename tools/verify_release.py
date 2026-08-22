import json
import re
import sys
from pathlib import Path

root = Path(__file__).resolve().parents[1]
errors = []
gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
version = re.search(r'versionName\s*=\s*"([^"]+)"', gradle)
code = re.search(r'versionCode\s*=\s*(\d+)', gradle)
if not version or version.group(1) != '0.34.8': errors.append('versionName mismatch')
if not code or code.group(1) != '349': errors.append('versionCode mismatch')
abi_line = re.search(r'abiFilters \+= listOf\(([^\n]+)\)', gradle)
if not abi_line or any(x not in abi_line.group(1) for x in ['arm64-v8a', 'armeabi-v7a', 'x86_64']): errors.append('required ABI set is incomplete')
if '"x86"' in gradle: errors.append('x86 ABI must remain excluded')
manifest = (root / 'app/src/main/AndroidManifest.xml').read_text(encoding='utf-8')
if manifest.count('android.permission.INTERNET') != 1: errors.append('exactly one INTERNET permission is required for approved HTTPS runtime paths')
catalog = (root / 'app/src/main/cpp/registry_query_catalog.h').read_text(encoding='utf-8')
for needle in ['kCatalogSchemaVersion = 6', 'kBaseline = "Vulkan 1.4.360"', 'findQueryDescriptor']:
    if needle not in catalog: errors.append(f'missing catalog requirement: {needle}')
manifest_json = json.loads((root / 'registry/generated/registry_query_manifest.json').read_text(encoding='utf-8'))
snapshot = json.loads((root / 'registry/generated/coverage_snapshot.json').read_text(encoding='utf-8'))
if manifest_json.get('baseline') != 'Vulkan 1.4.360': errors.append('generated manifest baseline mismatch')
if snapshot.get('baseline') != 'Vulkan 1.4.360': errors.append('coverage snapshot baseline mismatch')
cpp = (root / 'app/src/main/cpp/vulkanscope.cpp').read_text(encoding='utf-8')
kt = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
if 'import androidx.compose.ui.text.style.TextAlign' not in kt: errors.append('TextAlign import missing for update dialog key/value alignment')
for needle in ['reportToText', 'reportToHtml', 'registryCoverage', 'instanceExtensions', 'deviceExtensions']:
    if needle not in kt + cpp: errors.append(f'missing report/export path: {needle}')
if re.search(r'\bTODO\b|\bFIXME\b', cpp + kt): errors.append('TODO/FIXME marker remains in production source')
if 'android:usesCleartextTraffic="false"' not in manifest: errors.append('cleartext traffic must be disabled')
for needle in ['packageSigningCertificatesMatch', 'archiveVersionCode <= installedVersionCode', 'toHttpUrlOrNull', 'baseUrl.username.isNotEmpty()', 'target.parentFile?.canonicalFile']:
    if needle not in kt: errors.append(f'missing update/network hardening: {needle}')
if 'private fun InfoPage(report: VulkanReport, display: DisplayReport, mode: DriverMode, collectionStatus: CollectionStatus, onCheckForUpdates: () -> Unit, directUpdatesEnabled: Boolean)' not in kt or 'Page.Info -> InfoPage(report, display, driverMode, collectionStatus, onCheckForUpdates, directUpdatesEnabled)' not in kt:
    errors.append('manual update callback is not exposed from the Info destination')
if 'OFFICIAL_DATABASE_API_ENDPOINT = "https://vulkanscope-database-api.vulkanscope.workers.dev"' not in kt:
    errors.append('official VulkanScope Database endpoint is missing')
if 'baseUrl.encodedPath != "/"' not in kt:
    errors.append('official database endpoint must be constrained to an HTTPS API root')
if 'Database API endpoint' in kt or 'databaseEndpoint' in kt or 'vulkanscope_database' in kt:
    errors.append('database endpoint must not be user-editable or persisted')
if 'submitDatabaseReport(context, report, display, mode)' not in kt:
    errors.append('database submission must use the fixed official endpoint')
if 'collectionStatus != CollectionStatus.COLLECTING' not in kt:
    errors.append('database submission must be disabled during active collection')

for needle in [
    'UpdateConfirmationDialog',
    'releaseNotes = json.optString("body")',
    'installedVersionCode()',
    'downloadAbi = if (exact != null) abi else "universal"',
    'OFFICIAL_DATABASE_WEB_URL = "https://efishell0.github.io/VulkanScope_database/"',
    'enabled = completeReportReady',
    'if (showProgress && result is UpdateCheckResult.Available)'
]:
    if needle not in kt: errors.append(f'missing 0.32.5 update/export UX requirement: {needle}')
if not re.search(r'ExpressiveActionButton\("Export TXT"[^\n]+completeReportReady', kt) or not re.search(r'ExpressiveActionButton\("Export HTML"[^\n]+completeReportReady', kt):
    errors.append('TXT and HTML must both use the complete-report collection gate')
if 'Download APK' not in kt or 'Downloaded versionCode' not in kt:
    errors.append('update confirmation must expose explicit download approval and APK version verification state')



# 0.33.0 intentionally restores the complete Turnip/SAF behavior from VulkanScope 0.32.4.
for needle in [
    'ActivityResultContracts.OpenDocument()',
    'driverPickerLauncher.launch(arrayOf("application/zip", "application/octet-stream", "*/*"))',
    'return root.walkTopDown().firstOrNull { it.isFile && it.extension.equals("so", true) && it.length() > 0L }'
]:
    if needle not in kt: errors.append(f'missing restored 0.32.4 Turnip/SAF behavior: {needle}')
for forbidden in [
    'Driver import was deferred because Vulkan collection is still active.',
    'Wait for the current Vulkan collection pass to finish before changing drivers.',
    'enabled = completeReportReady, onClick = { if (completeReportReady) onInstallDriverBundle() }',
    'val path = findTurnipIcd() ?: return null'
]:
    if forbidden in kt: errors.append(f'post-0.32.4 Turnip behavior remains: {forbidden}')

# 0.33.1 changes only Settings interactivity: the restored 0.32.4 Turnip/SAF internals remain intact.
for needle in [
    'completeReportReady && turnipSupport == TurnipSupport.SUPPORTED',
    'if (completeReportReady) "Uses Android\'s system Vulkan loader/driver." else "Waiting for complete Vulkan collection"',
    'ExpressiveActionButton("Import driver ZIP"',
    'if (completeReportReady) "Validate and install an AdrenoTools-compatible bundle" else "Waiting for complete Vulkan collection"'
]:
    if needle not in kt: errors.append(f'missing 0.33.1 collection driver gate: {needle}')

if 'SectionCard("Application updates")' in kt:
    errors.append('manual update control must no longer be hosted in Settings')
if kt.count('ExpressiveActionButton("Check for updates"') != 1:
    errors.append('manual update action must appear exactly once in Info')

cmake = (root / 'app/src/main/cpp/CMakeLists.txt').read_text(encoding='utf-8')
for needle in ['GIT_TAG 0b7f383797fa7be53ae28213e001ae60668ee511', '#define VK_HEADER_VERSION[ \\t]+360', '-Wl,-z,relro', '-Wl,-z,now']:
    if needle not in cmake: errors.append(f'missing current native build/security baseline: {needle}')
for needle in ['technicalReport', 'schemaVersion", 3', 'ExpressiveActionButton', 'meta.json', 'canonicalLibrary.path.startsWith(rootPrefix)']:
    if needle not in kt: errors.append(f'missing 0.32.x report/UI/runtime hardening: {needle}')
if 'Vulkan 1.4.360 compile headers; validated query catalog Vulkan 1.4.360' not in catalog:
    errors.append('compile-header/query-catalog baseline distinction is missing')

for needle in [
    'canonicalImageLayoutName',
    'imageLayoutListString',
    'kMaxHostImageCopyLayoutEntries = 65536',
    'pCopySrcLayouts',
    'pCopyDstLayouts'
]:
    if needle not in cpp: errors.append(f'missing 0.34.0 Host Image Copy parity requirement: {needle}')
parity_fields = (root / 'app/src/main/cpp/extension_field_coverage_parity.inc').read_text(encoding='utf-8')
if 'generatedEmitAuto(dst, section, "pCopySrcLayouts", value.pCopySrcLayouts)' in parity_fields or 'generatedEmitAuto(dst, section, "pCopyDstLayouts", value.pCopyDstLayouts)' in parity_fields:
    errors.append('0.34.0 must not serialize Host Image Copy pointer addresses as unavailable pointer fields')
for needle in [
    'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_CLAMP_ZERO_ONE_FEATURES_KHR',
    'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_OFFSET_FEATURES_QCOM',
    'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INDEX_TYPE_UINT8_FEATURES_KHR',
    'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINE_RASTERIZATION_FEATURES_KHR',
    'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINE_RASTERIZATION_PROPERTIES_KHR',
    'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MEMORY_DECOMPRESSION_FEATURES_NV',
    'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ROBUSTNESS_2_FEATURES_KHR',
    'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SWAPCHAIN_MAINTENANCE_1_FEATURES_KHR',
    'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COMPUTE_SHADER_DERIVATIVES_FEATURES_NV',
    'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COPY_MEMORY_INDIRECT_PROPERTIES_NV'
]:
    if needle not in parity_fields: errors.append(f'missing 0.34.0 canonical alias field consumer: {needle}')

coverage_kt = (root / 'app/src/main/java/com/efishell/vulkanscope/CapsViewer412ExtensionCoverage.kt').read_text(encoding='utf-8')
coverage_extensions = set(re.findall(r'\"(VK_[A-Za-z0-9_]+)\"', coverage_kt))
if len(coverage_extensions) != 301: errors.append(f'CapsViewer 4.12 physical-device extension coverage mismatch: {len(coverage_extensions)}')

if 'groupName.rfind("ext::", 0) == 0' not in cpp: errors.append('generic exhaustive extension query dispatch is missing')
if '.map { "ext::$it" }' not in kt: errors.append('runtime-enumerated exhaustive extension scheduling is missing')
if 'for (group in newGroups)' not in kt: errors.append('isolated complete-report queries must execute sequentially')
if 'payload.size > 2 * 1024 * 1024' not in kt: errors.append('complete-report submission bound must match current Database transport')

# Every validated device-extension descriptor must have an implemented native query branch.
descriptor_groups = set(re.findall(r'\{\"([^\"]+)\", \"device-extension\"', catalog))
implemented_groups = set(re.findall(r'std::strcmp\(groupName, \"([^\"]+)\"\)', cpp))
implemented_extension_branches = set(re.findall(r'std::strcmp\(extensionName, "([^"]+)"\)', cpp))
pnext_text = (root / 'app/src/main/cpp/runtime_extension_pnext_generated.inc').read_text(encoding='utf-8') + '\n' + (root / 'app/src/main/cpp/runtime_extension_pnext_parity.inc').read_text(encoding='utf-8')
for needle in ['VK_EXT_image_tiling_control', 'VK_EXT_cooperative_matrix_maintenance1']:
    if needle not in coverage_kt: errors.append(f'missing Vulkan 1.4.360 delta extension scheduling: {needle}')
for needle in ['VkPhysicalDeviceImageTilingControlFeaturesEXT', 'VkPhysicalDeviceCooperativeMatrixMaintenance1FeaturesEXT']:
    if needle not in pnext_text: errors.append(f'missing Vulkan 1.4.360 delta pNext query: {needle}')
for needle in ['vkGetPhysicalDeviceCooperativeMatrixProperties2EXT', 'cooperativeMatrixProperties2Query']:
    if needle not in cpp: errors.append(f'missing cooperative matrix maintenance1 physical-device query: {needle}')
for needle in ['linearU64', 'optimalU64', 'bufferU64', 'flagsU64', 'VK_FORMAT_FEATURE_2_VERTEX_BUFFER_BIT']:
    if needle not in kt: errors.append(f'missing exact/canonical structured Vulkan data: {needle}')

implemented_extension_branches.update(re.findall(r'std::strcmp\(selectedExtension, "([^"]+)"\)', pnext_text))
extension_to_group = {}
for match in re.finditer(r'\{"([^"]+)", "device-extension", "([^"]+)"', catalog): extension_to_group[match.group(2)] = match.group(1)
implemented_groups.update(extension_to_group[name] for name in implemented_extension_branches if name in extension_to_group)
missing_groups = sorted(descriptor_groups - implemented_groups)
if missing_groups: errors.append('validated device-extension query groups missing native implementation: ' + ', '.join(missing_groups))
if not (root / 'gradlew').exists() or not (root / 'gradlew.bat').exists(): errors.append('Gradle wrapper launch scripts are missing')
if not (root / 'tools/verify_canonical_vulkan_headers.py').exists(): errors.append('canonical header verifier missing')
if not (root / 'app/src/main/cpp/runtime_extension_pnext_generated.inc').exists(): errors.append('checked-in runtime pNext generated source missing')
if not (root / 'tools/generate_extension_pnext_query.py').exists(): errors.append('runtime pNext generator missing')
if not (root / 'tools/audit_capsviewer_parity.py').exists(): errors.append('CapsViewer/canonical parity audit tool missing')
if not (root / 'tools/compare_capsviewer_4_12.py').exists(): errors.append('field-level CapsViewer 4.12 source audit tool missing')
if not (root / 'rules/CAPSVIEWER_PARITY.md').exists(): errors.append('CapsViewer parity methodology missing')
if 'runtime_extension_pnext_generated.inc' not in cpp: errors.append('generated runtime pNext include missing from native source')
if '#include <runtime_extension_pnext_generated.inc>' not in cpp: errors.append('generated runtime pNext query is not included by native collector')
field_parity = (root / 'app/src/main/cpp/extension_field_coverage_parity.inc').read_text(encoding='utf-8')
runtime_parity = (root / 'app/src/main/cpp/runtime_extension_pnext_parity.inc').read_text(encoding='utf-8')
for forbidden in ['VkPhysicalDeviceExternalMemoryScreenBufferFeaturesQNX', 'VkPhysicalDevicePresentationPropertiesOHOS', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_MEMORY_SCREEN_BUFFER_FEATURES_QNX', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENTATION_PROPERTIES_OHOS']:
    if forbidden in field_parity or forbidden in runtime_parity: errors.append(f'platform-specific parity type must not be compiled for Android baseline: {forbidden}')
for left, right in [
    ('VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COMPUTE_SHADER_DERIVATIVES_FEATURES_KHR', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COMPUTE_SHADER_DERIVATIVES_FEATURES_NV'),
    ('VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COPY_MEMORY_INDIRECT_PROPERTIES_KHR', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COPY_MEMORY_INDIRECT_PROPERTIES_NV'),
    ('VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_CLAMP_ZERO_ONE_FEATURES_EXT', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_CLAMP_ZERO_ONE_FEATURES_KHR'),
    ('VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_OFFSET_FEATURES_EXT', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_OFFSET_FEATURES_QCOM'),
    ('VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INDEX_TYPE_UINT8_FEATURES_EXT', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INDEX_TYPE_UINT8_FEATURES_KHR'),
    ('VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINE_RASTERIZATION_FEATURES_EXT', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINE_RASTERIZATION_FEATURES_KHR'),
    ('VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINE_RASTERIZATION_PROPERTIES_EXT', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINE_RASTERIZATION_PROPERTIES_KHR'),
    ('VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MEMORY_DECOMPRESSION_FEATURES_EXT', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MEMORY_DECOMPRESSION_FEATURES_NV'),
    ('VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ROBUSTNESS_2_FEATURES_EXT', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ROBUSTNESS_2_FEATURES_KHR'),
    ('VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SWAPCHAIN_MAINTENANCE_1_FEATURES_EXT', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SWAPCHAIN_MAINTENANCE_1_FEATURES_KHR'),
    ('VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VERTEX_ATTRIBUTE_DIVISOR_FEATURES_EXT', 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VERTEX_ATTRIBUTE_DIVISOR_FEATURES_KHR'),
]:
    if left in field_parity and right in field_parity: errors.append(f'duplicate VkStructureType alias cases remain: {left} / {right}')

cmake = (root / 'app/src/main/cpp/CMakeLists.txt').read_text(encoding='utf-8')
if 'find_package(Python3' in cmake or 'Python3_EXECUTABLE' in cmake: errors.append('Android CMake build must not require Python at configure/build time')

field_gen = (root / 'tools/generate_extension_field_coverage.py').read_text(encoding='utf-8')
if '--registry' not in field_gen or 'generatedEnumName_' not in field_gen or 'generatedFlagsName_' not in field_gen:
    errors.append('generated field coverage must consume vk.xml and emit canonical enum/flag semantics')
if 'generatedEmitHexTyped' not in cpp:
    errors.append('typed raw-byte fallback missing from native generated-field pipeline')
for forbidden in ['generatedEmitUnsupported', 'generatedEmitEnum(', 'generatedEmitFlags(', 'generatedEmitEnumArray', 'generatedEmitFlagsArray', 'generatedEmitFloat(']:
    if forbidden in cpp:
        errors.append(f'unused generated helper remains: {forbidden}')
if 'std::is_signed_v' not in cpp:
    errors.append('numeric fields must preserve signed/unsigned semantics in generated display')
if 'VkPhysicalDeviceShaderOcpMicroscalingTypesFeaturesEXT' in cpp or 'VkPhysicalDeviceShaderOcpMicroscalingTypesFeaturesEXT' in (root / 'app/src/main/cpp/runtime_extension_pnext_generated.inc').read_text(encoding='utf-8'):
    errors.append('non-canonical OCP microscaling type spelling remains')
if 'VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TEXTURE_COMPRESSION_ASTC_3_DFEATURES_EXT' in (root / 'app/src/main/cpp/runtime_extension_pnext_generated.inc').read_text(encoding='utf-8'):
    errors.append('non-canonical ASTC 3D structure type spelling remains')
if 'features.values[' in cpp or 'VkPhysicalDeviceLimitsLayout' in cpp or 'VkPhysicalDeviceSparsePropertiesLayout' in cpp:
    errors.append('native collector still contains non-canonical Vulkan struct access')
if 'std::vector<int32_t> copySrc' in cpp or 'std::vector<int32_t> copyDst' in cpp:
    errors.append('Vulkan 1.4 copy-layout arrays must use VkImageLayout')
if 'getPhysicalDeviceProperties(devices[i], raw.data())' in cpp or 'getPhysicalDeviceProperties(device, properties.data())' in cpp:
    errors.append('physical-device properties must use canonical VkPhysicalDeviceProperties storage')


for needle in [
    'registerDisplayListener(displayListener, null)',
    'unregisterDisplayListener(displayListener)',
    'hdrTypes.filter { it != -1 }',
    'else -> "Android HDR type $type"',
    'Content-Security-Policy',
    'no-referrer',
    '.sortedWith { a, b -> -compareVersions(a.second, b.second) }',
    'compareVersions(candidate, current) > 0',
    'baseUrl.host != "vulkanscope-database-api.vulkanscope.workers.dev"',
    'if (filter == "Limits" || filter == "All")'
]:
    if needle not in kt: errors.append(f'missing 0.33.7 full-audit requirement: {needle}')
if kt.count('metric("GPU", report.devices.firstOrNull()?.name ?: "Unknown")') != 1:
    errors.append('HTML report GPU hero metric must appear exactly once')

for needle in [
    'val visibleLimits = remember(query, device?.limits)',
    'val propertyResultCount = filtered.size',
    'val uniquePropertyNames = filtered.asSequence().map { it.name }.distinct().count()',
    'val limitResultCount = visibleLimits.size',
    '"Limits" -> "$limitResultCount limits"',
    '"All" -> "$propertyResultCount query results · $uniquePropertyNames unique property names · $limitResultCount limits"',
    'else -> "$propertyResultCount query results · $uniquePropertyNames unique property names"'
]:
    if needle not in kt: errors.append(f'missing 0.33.10 Properties & Limits summary-semantics requirement: {needle}')
if 'filtered.size + visibleLimits.size' in kt:
    errors.append('All summary must not merge property query results with limit rows')
if '(filtered.asSequence().map { it.name } + visibleLimits.asSequence().map { it.first })' in kt:
    errors.append('unique property names must never include limit names')


for needle in [
    'CapabilityKeyValue("Security patch", Build.VERSION.SECURITY_PATCH.ifBlank { "Unavailable" })',
    'CapabilityKeyValue("Build fingerprint", Build.FINGERPRINT)',
    'appendLine("Implemented structs=${report.registryCoverage.implementedPhysicalDeviceStructs.joinToString(", ")}")',
    'layer.extensions.forEach { ext -> appendLine("  ${ext.name} | ${ext.scope} | spec ${ext.specVersion} | supported=${ext.supported}") }',
    'flags=${memoryHeapFlags(it.flags)} | raw=${it.flags.toULong()}',
    'flags=${memoryTypeFlags(it.flags)} | raw=${it.flags.toULong()}',
    'flags=${queueCapabilityFlags(it.flags)}, rawFlags=${it.flags.toULong()}',
    'videoCodecOperations=${videoCodecOperationFlags(it.videoCodecOperations)}',
    'linear=${formatFeatureFlags(it.linear)} [raw ${it.linear.toULong()}',
    '"Implemented structs" to htmlEscape(report.registryCoverage.implementedPhysicalDeviceStructs.joinToString(", "))',
    '"Security patch" to htmlEscape(Build.VERSION.SECURITY_PATCH.ifBlank { "Unavailable" })'
]:
    if needle not in kt: errors.append(f'missing 0.34.4 report/UI parity requirement: {needle}')
for needle in [
    'put("detailedProperties", JSONArray().apply { d.detailedProperties.forEach',
    'appendLine(); appendLine("DETAILED QUERY RESULTS',
    'table("Detailed query results (',
    'private fun PropertiesPage(device: DeviceReport?'
]:
    if needle not in kt: errors.append(f'missing generic detailed-property propagation path: {needle}')

probe_service = (root / 'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt').read_text(encoding='utf-8')

for needle in [
    'val hdrCapabilityStatus: String = "unavailable"',
    'val wideGamut: Boolean?',
    'Display.HdrCapabilities.INVALID_LUMINANCE',
    'periodicRefreshDue',
    'put("hdrCapabilityStatus", display.hdrCapabilityStatus)',
    'put("preferredWideGamutColorSpace", display.preferredWideGamut)',
    'resultLength != lastObservedLength || resultModified != lastObservedModified'
]:
    if needle not in kt: errors.append(f'missing 0.33.3 reporting/runtime audit fix: {needle}')
if 'surface?.release()' not in probe_service:
    errors.append('isolated probe Surface parcel must be released deterministically')
if 'pmc <= kMaxPresentModeEntries' not in cpp:
    errors.append('present-mode enumeration must use the dedicated safety bound')
if 'ATOMIC_MOVE' not in probe_service and 'renameTo(file)' not in probe_service:
    errors.append('probe result publication must be atomic')
if 'VK_MAX_MEMORY_HEAPS' not in cpp or 'VK_MAX_MEMORY_TYPES' not in cpp:
    errors.append('memory enumeration safety limits must use canonical Vulkan array bounds')
if 'kMaxSparseImageFormatEntries' not in cpp:
    errors.append('sparse image property enumeration safety limit missing')
if re.search(r'^\s*/[/*]', cpp, re.MULTILINE) or re.search(r'^\s*/[/*]', kt, re.MULTILINE):
    errors.append('source-code comments are forbidden by PROJECT_RULES')
if 'GIT_TAG master' in cmake: errors.append('libadrenotools dependency must be pinned to an immutable commit')
if '0b7f383797fa7be53ae28213e001ae60668ee511' not in cmake: errors.append('canonical Vulkan-Headers 1.4.360 commit is not pinned')
if '#include <vulkan/vulkan.h>' not in cpp: errors.append('canonical Vulkan header is not used')
if '#define VK_ENABLE_BETA_EXTENSIONS 1' not in cpp: errors.append('provisional Vulkan extensions must be explicitly enabled before vulkan.h')
if 'VK_USE_PLATFORM_ANDROID_KHR' not in cpp: errors.append('Android Vulkan platform macro missing')

cpp_text = (root / 'app/src/main/cpp/vulkanscope.cpp').read_text()
if 'appendProperty(out, first, "Core 1.4", "pCopySrcLayouts"' not in cpp_text: errors.append('missing canonical Core 1.4 pCopySrcLayouts report name')
if 'appendProperty(out, first, "Core 1.4", "pCopyDstLayouts"' not in cpp_text: errors.append('missing canonical Core 1.4 pCopyDstLayouts report name')
if 'appendProperty(out, first, "Core 1.4", "copySrcLayouts"' in cpp_text or 'appendProperty(out, first, "Core 1.4", "copyDstLayouts"' in cpp_text: errors.append('legacy noncanonical Core 1.4 Host Image Copy pointer member name')


kt_current = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
cpp_current = (root / 'app/src/main/cpp/vulkanscope.cpp').read_text(encoding='utf-8')
for needle in [
    'Direct GitHub updates are currently disabled. Obtainium can manage updates externally, or direct updates can be enabled in Settings.',
    'may duplicate update notifications.'
]:
    if needle not in kt_current: errors.append(f'missing 0.34.7 Obtainium requirement: {needle}')
if 'Add to Obtainium' in kt_current or 'obtainium://app/' in kt_current or 'openInObtainium' in kt_current:
    errors.append('runtime source still exposes removed Add to Obtainium action')
if 'IzzyOnDroid' in kt_current:
    errors.append('current VulkanScope runtime source must not retain IzzyOnDroid-specific messaging')
obtainium_config = root / 'obtainium-config.json'
if not obtainium_config.is_file():
    errors.append('missing obtainium-config.json')
else:
    import json
    obtainium_data = json.loads(obtainium_config.read_text(encoding='utf-8'))
    obtainium_settings = json.loads(obtainium_data['apps'][0]['additionalSettings'])
    if obtainium_settings.get('apkFilterRegEx') != r'(?i).*universal.*\.apk$' or obtainium_settings.get('autoApkFilterByArch') is not False:
        errors.append('obtainium-config.json must preserve universal APK selection')

for needle in [
    'featureFlags2Available=',
    'parseUnsignedHexLong',
    'featureFlags2Available && flags2Linear != null',
    'featureFlags2Available && flags2Optimal != null',
    'featureFlags2Available && flags2Buffer != null',
    'apiVersion >= VK_API_VERSION_1_3',
    'remaining.toULong().toString(16).uppercase()',
]:
    if needle not in kt_current and needle not in cpp_current:
        errors.append(f'missing 0.34.8 FormatFeatureFlags2 requirement: {needle}')
for icon_name in ['ic_settings.xml', 'ic_info.xml']:
    icon_text = (root / 'app' / 'src' / 'main' / 'res' / 'drawable' / icon_name).read_text()
    if 'strokeLineCap="round"' not in icon_text:
        errors.append(f'missing rounded expressive icon geometry: {icon_name}')

if errors:
    for error in errors: print(f'FAIL: {error}')
    raise SystemExit(1)
print('VulkanScope release verification: PASS')
print(f'version={version.group(1)} code={code.group(1)} baseline=Vulkan 1.4.360 schema=6 compileHeaders=0b7f383797fa7be53ae28213e001ae60668ee511')
