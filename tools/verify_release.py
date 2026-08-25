import json
import re
import sys
from pathlib import Path

root = Path(__file__).resolve().parents[1]
errors = []
gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
version = re.search(r'versionName\s*=\s*"([^"]+)"', gradle)
code = re.search(r'versionCode\s*=\s*(\d+)', gradle)
if not version or version.group(1) != '0.41.6': errors.append('versionName mismatch')
if not code or code.group(1) != '416': errors.append('versionCode mismatch')
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
for source_path in list((root / 'app/src/main/java').rglob('*.kt')) + list((root / 'app/src/main/cpp').glob('*')) + list((root / 'tools').glob('*.py')):
    if not source_path.is_file():
        continue
    source_text = source_path.read_text(encoding='utf-8', errors='ignore')
    if source_path.suffix == '.py':
        if re.search(r'^(?!#!)\s*#', source_text, re.MULTILINE): errors.append(f'source-code comment remains: {source_path.relative_to(root)}')
    elif source_path.suffix in {'.kt', '.cpp', '.c', '.h', '.hpp'}:
        if re.search(r'^\s*//|^\s*/\*', source_text, re.MULTILINE): errors.append(f'source-code comment remains: {source_path.relative_to(root)}')
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



for needle in [
    'ActivityResultContracts.OpenDocument()',
    'driverPickerLauncher.launch(arrayOf("application/zip", "application/octet-stream", "*/*"))'
]:
    if needle not in kt: errors.append(f'missing restored Turnip/SAF picker behavior: {needle}')
for forbidden in [
    'Driver import was deferred because Vulkan collection is still active.',
    'Wait for the current Vulkan collection pass to finish before changing drivers.',
    'enabled = completeReportReady, onClick = { if (completeReportReady) onInstallDriverBundle() }',
    'val path = findTurnipIcd() ?: return null'
]:
    if forbidden in kt: errors.append(f'post-0.32.4 Turnip behavior remains: {forbidden}')

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

if '\"Sparse Image Format Properties2\",\"name\":\"' in cpp:
    errors.append('sparse image safety fallback must emit valid JSON without an extra name quote')
for needle in [
    'count > values.size()',
    'count > devices.size()',
    'count > layers.size()',
    'queueCount > queueCapacity',
    'qcount > queueCapacity',
    'toolCount > toolCapacity',
    'formatCount > formats.size()',
    'gc <= groupCapacity'
]:
    if needle not in cpp: errors.append(f'missing second-stage enumeration capacity defense: {needle}')
for needle in [
    'Vulkan Query Safety',
    'Queue-family enumeration safety rejected',
    'Memory-heap count safety rejected',
    'Memory-type count safety rejected',
    'Surface queue-family enumeration safety rejected'
]:
    if needle not in kt: errors.append(f'missing query-safety report/UI parity evidence: {needle}')
if 'groupName.rfind("ext::", 0) == 0' not in cpp: errors.append('generic exhaustive extension query dispatch is missing')
if '.map { "ext::$it" }' not in kt: errors.append('runtime-enumerated exhaustive extension scheduling is missing')
if 'for (group in newGroups)' not in kt: errors.append('isolated complete-report queries must execute sequentially')
if 'payload.size > 2 * 1024 * 1024' not in kt: errors.append('complete-report submission bound must match current Database transport')

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
    'val uniquePropertyNames = filtered.asSequence().filterNot { it.section == "Vulkan Query Safety" }.map { it.name }.distinct().count()',
    'val uniqueSafetyNames = filtered.asSequence().filter { it.section == "Vulkan Query Safety" }.map { it.name }.distinct().count()',
    'val limitResultCount = visibleLimits.size',
    '"Limits" -> "$limitResultCount limits"'
]:
    if needle not in kt: errors.append(f'missing Properties & Limits summary-semantics requirement: {needle}')
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
    'videoCodecOperations=${if (it.videoCodecQueryStatus == "available") videoCodecOperationFlags(it.videoCodecOperations) else "Unknown"}',
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
if 'count > kMaxPresentModeEntries' not in cpp:
    errors.append('present-mode enumeration must use the dedicated safety bound')
if 'Os.rename(temp.path, file.path)' not in probe_service:
    errors.append('probe result publication must use app-private atomic rename')
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


for needle in [
    'implementation("androidx.compose.ui:ui:1.12.0")',
    'implementation("androidx.compose.foundation:foundation:1.12.0")',
    'implementation("androidx.compose.animation:animation:1.12.0")',
    'implementation("androidx.compose.material3:material3:1.5.0-alpha26")',
]:
    if needle not in gradle:
        errors.append(f'missing 0.34.9 expressive dependency baseline: {needle}')

for needle in [
    'MaterialExpressiveTheme(',
    'motionScheme = MotionScheme.expressive()',
    'ShortNavigationBar(',
    'ShortNavigationBarItem(',
    'LoadingIndicator(',
    'LinearWavyProgressIndicator(',
    'IconButtonDefaults.shapes(',
    'FilterChipDefaults.shapes(',
    'ButtonDefaults.shapes(',
    'private fun ExpressiveSearchField(',
    'private fun ExpressiveSwitch(',
    'private fun ExpressiveRadioButton(',
]:
    if needle not in kt_current:
        errors.append(f'missing 0.34.9 Material 3 Expressive UI requirement: {needle}')

if re.search(r'(?<!Short)\bNavigationBar\(', kt_current):
    errors.append('legacy portrait NavigationBar remains after 0.34.9 expressive navigation migration')
if re.search(r'(?<!Short)\bNavigationBarItem\(', kt_current):
    errors.append('legacy portrait NavigationBarItem remains after 0.34.9 expressive navigation migration')
if re.search(r'\bCircularProgressIndicator\(', kt_current):
    errors.append('legacy CircularProgressIndicator remains after 0.34.9 expressive loading migration')
if re.search(r'(?<!Wavy)\bLinearProgressIndicator\(', kt_current):
    errors.append('legacy LinearProgressIndicator remains after 0.34.9 expressive progress migration')

if '## Release 0.34.9 full Material 3 Expressive surface pass' not in (root / 'rules/PROJECT_RULES.md').read_text(encoding='utf-8'):
    errors.append('PROJECT_RULES is missing the 0.34.9 full Material 3 Expressive release contract')
if '## Release 0.35.1 full application security and correctness audit' not in (root / 'rules/PROJECT_RULES.md').read_text(encoding='utf-8'):
    errors.append('PROJECT_RULES is missing the 0.35.1 full application security and correctness contract')
for needle in ['selectedTextColorTopIconPosition = VulkanTextPrimary', 'selectedTextColorStartIconPosition = VulkanTextPrimary']:
    if needle not in kt_current:
        errors.append(f'missing 0.35.0 ShortNavigationBar selected-label color argument: {needle}')
if re.search(r'ShortNavigationBarItemDefaults\.colors\([^)]*selectedTextColor\s*=', kt_current, re.S):
    errors.append('obsolete ShortNavigationBar selectedTextColor argument remains')
if not (root / 'rules/0.34.9_MATERIAL3_EXPRESSIVE_FULL_UI_AUDIT.md').is_file():
    errors.append('0.34.9 Material 3 Expressive audit is missing')

functional_icons = [
    'ic_back.xml', 'ic_home.xml', 'ic_display.xml', 'ic_surface.xml', 'ic_extensions.xml',
    'ic_features.xml', 'ic_memory.xml', 'ic_queues.xml', 'ic_formats.xml', 'ic_properties.xml',
    'ic_action_database.xml', 'ic_action_html.xml', 'ic_action_import.xml', 'ic_action_text.xml',
    'ic_action_update.xml', 'ic_search.xml', 'ic_check.xml', 'ic_chevron_right.xml',
    'ic_info.xml', 'ic_settings.xml',
]
for icon_name in functional_icons:
    icon_path = root / 'app/src/main/res/drawable' / icon_name
    if not icon_path.is_file():
        errors.append(f'missing 0.34.9 functional icon: {icon_name}')
        continue
    icon_text = icon_path.read_text(encoding='utf-8')
    if 'strokeLineCap="round"' not in icon_text:
        errors.append(f'functional icon is not rounded-line expressive geometry: {icon_name}')

if 'ic_action_github.xml' not in {p.name for p in (root / 'app/src/main/res/drawable').glob('*.xml')}:
    errors.append('GitHub brand icon asset is missing')

for needle in ['activeUpdateCheckCall', 'activeUpdateDownloadCall', 'updateDownloadJob', 'archiveHistory.containsAll(installedCurrent)', 'runCatching { target.delete() }', 'if (!directUpdatesEnabled) {']:
    if needle not in kt:
        errors.append(f'missing 0.35.1 updater hardening: {needle}')
for needle in ['Analysis("Analysis")', 'Page.Analysis -> AnalysisPage(report, device, display, driverMode)', 'VulkanScopeAnalysisSnapshot1', 'ActivityResultContracts.OpenDocument()', 'ActivityResultContracts.CreateDocument("application/json")', 'vulkanSnapshotDiff', 'vulkanProfileRequirements()', 'getSharedPreferences("analysis_tools"', 'Minimal SPIR-V shader module', 'Minimal compute pipeline creation']:
    if needle not in kt + cpp:
        errors.append(f'missing 0.40.0 analysis/test requirement: {needle}')
if 'readBoundedAnalysisBytes(input, ANALYSIS_MAX_SNAPSHOT_BYTES)' not in kt or 'ANALYSIS_MAX_SNAPSHOT_BYTES = 8 * 1024 * 1024' not in kt:
    errors.append('0.40.0 snapshot import bound is missing')
if 'collectVulkanSelfTest' not in cpp or 'queueCount > 4096' not in cpp or 'groupName.rfind("selftest:", 0) == 0' not in cpp:
    errors.append('0.40.0 isolated Vulkan self-test contract is incomplete')
if '## Release 0.40.0 local analysis and optional tests' not in (root / 'rules/PROJECT_RULES.md').read_text(encoding='utf-8'):
    errors.append('PROJECT_RULES is missing the 0.40.0 analysis/test contract')
if not (root / 'rules/0.40.0_ANALYSIS_COMPARE_TESTS_AUDIT.md').is_file():
    errors.append('0.40.0 analysis/test audit is missing')
if not (root / 'fastlane/metadata/android/en-US/changelogs/400.txt').is_file():
    errors.append('0.40.0 fastlane changelog is missing')
if 'BuildConfig.VERSION_NAME' in kt:
    errors.append('0.40.1 Analysis must not depend on BuildConfig.VERSION_NAME')
if '@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun AnalysisPage' not in kt:
    errors.append('0.40.1 Analysis Material 3 Expressive opt-in is missing or too broad')
if 'Minimal pipeline layout' not in cpp or 'layoutResult == VK_SUCCESS' not in cpp:
    errors.append('0.40.1 pipeline-layout self-test evidence is not consumed')
if '## Release 0.40.1 analysis build-fix requirements' not in (root / 'rules/PROJECT_RULES.md').read_text(encoding='utf-8'):
    errors.append('PROJECT_RULES is missing the 0.40.1 build-fix contract')
if not (root / 'rules/0.40.1_ANALYSIS_BUILD_FIX_AUDIT.md').is_file():
    errors.append('0.40.1 build-fix audit is missing')
if not (root / 'fastlane/metadata/android/en-US/changelogs/401.txt').is_file():
    errors.append('0.40.1 fastlane changelog is missing')

for needle in ['validateAnalysisSnapshot', 'ANALYSIS_MAX_ENTRIES = 32768', 'ANALYSIS_MAX_WATCHED = 256', 'readFileTextLimited(resultFile, maxProbeResultBytes.toInt())', 'items(diffRows, key = { it.key })', 'Image Format Properties2', 'Safe shader create/destroy path unavailable', 'completed_with_unavailable', 'stopVulkanProbeProcess()']:
    if needle not in kt + cpp:
        errors.append(f'missing 0.40.2 full-audit hardening: {needle}')
if 'rows.take(2500)' in kt:
    errors.append('0.40.2 Compare must not silently truncate diff rows')
if '## Release 0.40.2 full application audit requirements' not in (root / 'rules/PROJECT_RULES.md').read_text(encoding='utf-8'):
    errors.append('PROJECT_RULES is missing the 0.40.2 full-audit contract')
if not (root / 'rules/0.40.2_FULL_APPLICATION_AUDIT.md').is_file():
    errors.append('0.40.2 full-audit record is missing')
if not (root / 'fastlane/metadata/android/en-US/changelogs/402.txt').is_file():
    errors.append('0.40.2 fastlane changelog is missing')

ext_ref = json.loads((root / 'registry/generated/extension_reference.json').read_text(encoding='utf-8'))
if ext_ref.get('baseline') != 'Vulkan 1.4.360': errors.append('0.41.0 extension-reference baseline mismatch')
known_block = kt[kt.index('private val KNOWN_VULKAN_EXTENSIONS'):kt.index('private fun List<String>.distinctScopes')]
known_extensions = set(re.findall(r'"(VK_[^"]+)"', known_block))
reference_names = {x.get('name') for x in ext_ref.get('entries', []) if isinstance(x, dict)}
missing_reference = sorted(known_extensions - reference_names)
if missing_reference: errors.append('0.41.0 known extensions missing registry reference: ' + ', '.join(missing_reference))
isolated_block_match = re.search(r'private val ISOLATED_EXTENSION_GROUPS = linkedMapOf\((.*?)\n\)', kt, re.S)
if not isolated_block_match:
    errors.append('0.41.0 isolated extension query map missing')
else:
    isolated_pairs = re.findall(r'"([^"]+)"\s+to\s+"([^"]+)"', isolated_block_match.group(1))
    isolated_by_name = {name: group for group, name in isolated_pairs}
    missing_known_handlers = sorted(set(isolated_by_name) - known_extensions)
    if missing_known_handlers:
        errors.append('0.41.0 query-handled extensions missing known catalog: ' + ', '.join(missing_known_handlers))
    reference_by_name = {x.get('name'): x for x in ext_ref.get('entries', []) if isinstance(x, dict)}
    missing_handler_refs = sorted(set(isolated_by_name) - set(reference_by_name))
    if missing_handler_refs:
        errors.append('0.41.0 query-handled extensions missing reference entry: ' + ', '.join(missing_handler_refs))
    mismatched_handler_refs = sorted(name for name, group in isolated_by_name.items() if name in reference_by_name and str(reference_by_name[name].get('queryGroup','')) != group)
    if mismatched_handler_refs:
        errors.append('0.41.0 query-handler/reference mapping mismatch: ' + ', '.join(mismatched_handler_refs))
if 'vulkanExtensionQueryGroup(name) != null || ref.queryGroup.isNotBlank()' not in kt:
    errors.append('0.41.0 handler:true filter must include the authoritative app query map')
for entry in ext_ref.get('entries', []):
    if not re.fullmatch(r'VK_[A-Z0-9]+_[A-Za-z0-9_]+', str(entry.get('name',''))): errors.append('0.41.0 extension reference contains non-extension token'); break
    if not str(entry.get('specUrl','')).startswith('https://registry.khronos.org/vulkan/specs/latest/man/html/VK_'): errors.append('0.41.0 extension reference has non-authoritative URL'); break
for needle in ['dependencyGraphEntries', 'maxDepth: Int = 4', 'maxNodes: Int = 64', 'Heuristic diagnostic evidence score', 'not a Vulkan conformance result', 'depends:VK_KHR', '"command" -> ref.commands.any', '"enum" -> ref.enums.any', 'VulkanQrCode(sharedReportUrl', 'databaseReportUrl(lastSharedReportId)', 'database_share', 'watchedEvidence']:
    if needle not in kt: errors.append(f'missing 0.41.0 advanced-analysis requirement: {needle}')

graph_kt_path = root / 'app/src/main/java/com/efishell/vulkanscope/VulkanDependencyGraph.kt'
qr_kt_path = root / 'app/src/main/java/com/efishell/vulkanscope/VulkanQrCode.kt'
if not graph_kt_path.is_file():
    errors.append('0.41.0 visual dependency graph source missing')
else:
    graph_kt = graph_kt_path.read_text(encoding='utf-8')
    for needle in ['Canvas(', 'drawLine(', 'shown = nodes.take(24)', 'horizontalScroll(rememberScrollState())', 'VulkanGraphNode']:
        if needle not in graph_kt: errors.append(f'missing 0.41.0 visual graph requirement: {needle}')
if not qr_kt_path.is_file():
    errors.append('0.41.0 local QR renderer source missing')
else:
    qr_kt = qr_kt_path.read_text(encoding='utf-8')
    for needle in ['QRCodeWriter().encode(', 'BarcodeFormat.QR_CODE', 'ErrorCorrectionLevel.M', 'Canvas(', 'Color.Black']:
        if needle not in qr_kt: errors.append(f'missing 0.41.0 local QR requirement: {needle}')
    if 'https://' in qr_kt or 'http://' in qr_kt: errors.append('0.41.0 QR renderer must not contain a remote QR/network endpoint')
if 'VulkanDependencyGraph(visualGraphNodes' not in kt:
    errors.append('0.41.0 visual graph is not integrated into Analysis')
if 'driverHealth.score?.let { "$it / 100" } ?: "Unavailable"' not in kt:
    errors.append('0.41.0 diagnostic score must not display an unavailable report as 0/100')

if 'com.google.zxing:core:3.5.4' not in gradle: errors.append('0.41.2 local QR dependency pin missing')
if not (root / 'tools/generate_extension_reference.py').is_file(): errors.append('0.41.0 vk.xml extension-reference generator missing')
if '## Release 0.41.0 advanced analysis, registry reference and sharing requirements' not in (root / 'rules/PROJECT_RULES.md').read_text(encoding='utf-8'): errors.append('PROJECT_RULES is missing 0.41.0 contract')
if not (root / 'rules/0.41.0_ADVANCED_ANALYSIS_DATABASE_AUDIT.md').is_file(): errors.append('0.41.0 audit record missing')
if not (root / 'fastlane/metadata/android/en-US/changelogs/410.txt').is_file(): errors.append('0.41.0 fastlane changelog missing')

if '## Release 0.41.1 Analysis Compose build-fix requirements' not in (root / 'rules/PROJECT_RULES.md').read_text(encoding='utf-8'):
    errors.append('PROJECT_RULES is missing 0.41.1 contract')
if not (root / 'rules/0.41.1_ANALYSIS_COMPOSE_BUILD_FIX_AUDIT.md').is_file():
    errors.append('0.41.1 audit record missing')
if not (root / 'fastlane/metadata/android/en-US/changelogs/411.txt').is_file():
    errors.append('0.41.1 fastlane changelog missing')
for forbidden in [
    'val rows = remember(baseline, current, includeUnchanged, diffQuery)',
    'val rootRef = remember(rootToken)',
    'val graphEntries = remember(rootToken)',
    'val visualNodes = remember(graphEntries, report, device)',
    'val health = remember(report, device)',
    'val entries = remember(current)',
    'val watchEvidence = remember(entries, watched)',
    'val sharePrefs = remember { context.getSharedPreferences("database_share", Context.MODE_PRIVATE) }'
]:
    analysis_start = kt.find('private fun AnalysisPage(')
    lazy_start = kt.find('LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues()', analysis_start)
    analysis_end = kt.find('\n@Composable', lazy_start + 1)
    analysis_lazy_body = kt[lazy_start:analysis_end if analysis_end >= 0 else len(kt)] if lazy_start >= 0 else ''
    if forbidden in analysis_lazy_body:
        errors.append('0.41.1 forbidden composable state calculation remains inside LazyListScope: ' + forbidden)
if 'preview += key to value' not in kt or 'ArrayList<Pair<String, String>>(10)' not in kt:
    errors.append('watched evidence must retain a bounded explicit Pair preview')
if 'currentEntries.filter' in kt and '.toList()' in kt[kt.find('val watchedEvidence'):kt.find('val visibleWatched')]:
    errors.append('watched evidence must not materialize an unbounded match list')
if 'val diffRows = remember(tab, baseline, current, includeUnchanged, diffQuery, diffStateFilter, diffKindFilter)' not in kt:
    errors.append('Analysis diff calculation is not hoisted and filter-aware at composable scope')
if 'val visualGraphNodes = remember(graphEntries, report, device)' not in kt:
    errors.append('0.41.1 graph calculation is not hoisted to composable scope')


rules_text = (root / 'rules/PROJECT_RULES.md').read_text(encoding='utf-8')
if '## Release 0.41.2 full correctness, compatibility and reporting audit requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.2 full-audit contract')
if not (root / 'rules/0.41.2_FULL_APPLICATION_AUDIT.md').is_file():
    errors.append('0.41.2 full-audit record is missing')
if not (root / 'fastlane/metadata/android/en-US/changelogs/412.txt').is_file():
    errors.append('0.41.2 fastlane changelog is missing')
for needle in [
    'val evidenceResultCount = filtered.size',
    'val safetyEvidenceCount = filtered.count { it.section == "Vulkan Query Safety" }',
    'val propertyResultCount = evidenceResultCount - safetyEvidenceCount',
    '$evidenceResultCount evidence rows · $propertyResultCount property/query rows · $safetyEvidenceCount safety diagnostics',
    '$detailedPropertyCount property/query rows; $detailedSafetyCount safety diagnostics',
    '$htmlDetailedPropertyCount property/query rows; $htmlDetailedSafetyCount safety diagnostics'
]:
    if needle not in kt:
        errors.append(f'missing 0.41.2 query/evidence count separation: {needle}')
for needle in [
    'SurfaceFormatEnumeration',
    'SurfacePresentModeEnumeration',
    'for (uint32_t attempt = 0; attempt < 4; ++attempt)',
    'returnedCount > capacity',
    'formatEnumerationComplete',
    'presentModeEnumerationComplete',
    'Partial: VK_INCOMPLETE; returned entries are positive evidence only.',
    'data query count exceeded the bounded allocation.'
]:
    if needle not in cpp:
        errors.append(f'missing 0.41.2 bounded enumeration hardening: {needle}')
if '"vulkanRegistryVersion":"1.4.357"' in cpp:
    errors.append('obsolete Vulkan 1.4.357 provenance remains in active native source')
if cpp.count('vulkanRegistryVersion\\":\\"1.4.360') < 3:
    errors.append('all active native checkpoint provenance paths must report Vulkan 1.4.360')
for needle in [
    'private fun resolveInstalledTurnipLibrary(filesDir: File): File?',
    'metadataFiles.size != 1',
    'metadata.optInt("schemaVersion", -1) != 1',
    'declared.contains(\'/\')',
    'declared.contains(\'\\\\\')',
    '!declared.endsWith(".so", true)',
    '!declared.contains("vulkan", true)',
    'libraries.size != 1',
    'canonicalLibrary.path.startsWith(rootPrefix)',
    'readFileTextLimited(metadataFiles.single(), 1024 * 1024)',
    'entry.name.length > 1024',
    'Build.VERSION.SDK_INT < 28 || !Build.SUPPORTED_ABIS.contains("arm64-v8a")'
]:
    if needle not in kt:
        errors.append(f'missing 0.41.2 strict Turnip requirement: {needle}')
if 'return root.walkTopDown().firstOrNull { it.isFile && it.extension.equals("so", true)' in kt:
    errors.append('legacy arbitrary first-.so Turnip fallback must not return after 0.41.2')
for needle in [
    'class ProbeBoundedOutputStream',
    '64L * 1024L * 1024L',
    'stream.fd.sync()',
    'Os.rename(temp.path, file.path)',
    'surface?.release()',
    'worker.shutdownNow()'
]:
    if needle not in probe_service:
        errors.append(f'missing 0.41.2 probe publication/lifecycle hardening: {needle}')
if 'java.nio.file' in probe_service or 'Files.move' in probe_service or 'StandardCopyOption' in probe_service:
    errors.append('minSdk 24 probe service must not use API-26-only java.nio.file publication APIs')
for needle in [
    'androidx.core:core-ktx:1.19.0',
    'androidx.lifecycle:lifecycle-runtime-compose:2.11.0',
    'androidx.compose.ui:ui:1.12.0',
    'androidx.compose.foundation:foundation:1.12.0',
    'androidx.compose.animation:animation:1.12.0',
    'androidx.compose.material3:material3:1.5.0-alpha26',
    'com.squareup.okhttp3:okhttp:5.2.0',
    'com.google.zxing:core:3.5.4'
]:
    if needle not in gradle:
        errors.append(f'missing 0.41.2 dependency baseline: {needle}')
field_generator = (root / 'tools/generate_extension_field_coverage.py').read_text(encoding='utf-8')
if "int(version_match.group(1)) != 360" not in field_generator or "r'#define\\s+VK_HEADER_VERSION\\s+(\\d+)\\b'" not in field_generator:
    errors.append('0.41.2 extension field generator must validate VK_HEADER_VERSION 360 with a valid word boundary')
for stale in ['vulkanscope.cpp.pre0412audit', 'MainActivity.kt.pre0412audit']:
    if list(root.rglob(stale)):
        errors.append(f'release package contains stale source backup: {stale}')
if list(root.rglob('__pycache__')) or list(root.rglob('*.pyc')):
    errors.append('release package contains Python cache artifacts')

if '## Release 0.41.3 queue and Vulkan Video evidence semantics' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.3 queue/video semantics contract')
if not (root / 'rules/0.41.3_QUEUE_VIDEO_SEMANTICS_AUDIT.md').is_file():
    errors.append('0.41.3 queue/video audit record is missing')
if not (root / 'fastlane/metadata/android/en-US/changelogs/413.txt').is_file():
    errors.append('0.41.3 fastlane changelog is missing')
for needle in [
    'val videoCodecQueryStatus: String = "unknown"',
    'val videoCodecQueryReason: String = ""',
    '0 · no VkQueueFlagBits reported',
    'VK_VIDEO_CODEC_OPERATION_NONE_KHR',
    'videoCodecQueryStatus',
    'videoCodecQueryReason',
    'queueVideoCodecQueryState(queue)',
    'hasVideoQueueExtension',
    'VK_KHR_video_queue was not enumerated for this device.',
    'val knownQueueFlags = 0x57FL'
]:
    if needle not in kt:
        errors.append(f'missing 0.41.3 queue/video semantics: {needle}')
if 'if (queue.videoCodecOperations != 0L) CapabilityKeyValue("Video codec operations"' in kt:
    errors.append('0.41.3 must not hide a successfully queried zero video codec mask')
if 'private fun queueCapabilityFlags(bits: Long): String = canonicalFlagNames' in kt:
    errors.append('0.41.3 queue flags require explicit zero-mask semantics')

if '~0x577u' in cpp or '0x577L' in kt:
    errors.append('obsolete queue known-bit mask 0x577 remains; Vulkan 1.4.360 mask is 0x57F')
if '~0x57Fu' not in cpp:
    errors.append('native queue unknown-bit mask must include sparse binding bit in 0x57F known mask')


if '## Release 0.41.4 full application hardening and reporting audit' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.4 full-audit contract')
if not (root / 'rules/0.41.4_FULL_APPLICATION_AUDIT.md').is_file():
    errors.append('0.41.4 full application audit record is missing')
if not (root / 'fastlane/metadata/android/en-US/changelogs/414.txt').is_file():
    errors.append('0.41.4 fastlane changelog is missing')
for needle in [
    'private val databaseHttpClient = ipv6PreferredHttpClient.newBuilder()',
    '.followRedirects(false)',
    '.followSslRedirects(false)',
    'databaseHttpClient.newCall(request).execute()',
    'put("videoCodecOperations", if (q.videoCodecQueryStatus == "available") q.videoCodecOperations else JSONObject.NULL)',
    'put("videoCodecOperationsU64", if (q.videoCodecQueryStatus == "available") q.videoCodecOperations.toULong().toString() else JSONObject.NULL)',
    'Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(7.dp)',
    'unique diagnostics'
]:
    if needle not in kt:
        errors.append(f'missing 0.41.4 reporting/security/usability hardening: {needle}')
if 'returned entries are retained as partial positive evidence only' not in cpp:
    errors.append('0.41.4 device-extension VK_INCOMPLETE partial evidence retention missing')
for needle in ['if (copySrcWithinLimit || copyDstWithinLimit) api.queryProperties2(device, &base);', 'const bool copySrcCollected = copySrcWithinLimit && p14.copySrcLayoutCount <= copySrcCapacity;', 'if (srcWithinLimit || dstWithinLimit) api.queryProperties2(devices[i], &hostQuery);', 'const bool srcCollected = srcWithinLimit && hostProperties.copySrcLayoutCount <= srcCapacity;']:
    if needle not in cpp:
        errors.append(f'missing 0.41.4 Host Image Copy independent bound/returned-count hardening: {needle}')
gradle_properties = (root / 'gradle.properties').read_text(encoding='utf-8')
for needle in ['org.gradle.caching=false', 'kotlin.caching.enabled=false']:
    if needle not in gradle_properties:
        errors.append(f'missing build-cache hardening: {needle}')
if 'put("videoCodecOperations", q.videoCodecOperations)' in kt or 'put("videoCodecOperationsU64", q.videoCodecOperations.toULong().toString())' in kt:
    errors.append('technicalReport must not serialize a zero video codec mask when the query is unavailable/not applicable')

if '## Release 0.41.5 Analysis, Profiles and Database compatibility requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.5 Analysis/Profile/Database contract')
if not (root / 'rules/0.41.5_ANALYSIS_PROFILE_DATABASE_AUDIT.md').is_file():
    errors.append('0.41.5 Analysis/Profile/Database audit record is missing')
if not (root / 'fastlane/metadata/android/en-US/changelogs/415.txt').is_file():
    errors.append('0.41.5 fastlane changelog is missing')
if '## Release 0.41.6 Kotlin compile-gate requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.6 Kotlin compile-gate contract')
if not (root / 'rules/0.41.6_KOTLIN_COMPILE_FIX_AUDIT.md').is_file():
    errors.append('0.41.6 Kotlin compile-fix audit record is missing')
if not (root / 'fastlane/metadata/android/en-US/changelogs/416.txt').is_file():
    errors.append('0.41.6 fastlane changelog is missing')
if 'mutableIntStateOf(' in kt and 'import androidx.compose.runtime.mutableIntStateOf' not in kt:
    errors.append('mutableIntStateOf is used without the required Compose runtime import')
if 'mutableLongStateOf(' in kt and 'import androidx.compose.runtime.mutableLongStateOf' not in kt:
    errors.append('mutableLongStateOf is used without the required Compose runtime import')
if 'mutableFloatStateOf(' in kt and 'import androidx.compose.runtime.mutableFloatStateOf' not in kt:
    errors.append('mutableFloatStateOf is used without the required Compose runtime import')
if 'mutableDoubleStateOf(' in kt and 'import androidx.compose.runtime.mutableDoubleStateOf' not in kt:
    errors.append('mutableDoubleStateOf is used without the required Compose runtime import')
for needle in [
    'apiVersionAtLeast',
    'VP_ANDROID_17_requirements',
    '"1.4.335"',
    'VP_ANDROID_vulkan_profile_2025',
    '"1.1.128"',
    'VP_KHR_roadmap_2026',
    '"1.4.328"',
    'completeCoverage',
    'hasUnknown || !requirements.completeCoverage',
    'maximumLimits',
    'extensionPromotedToSatisfied',
    'snapshotDeviceExtensionsComplete',
    'snapshotSurfaceFormatsComplete',
    'diffStateFilter',
    'diffKindFilter',
    'graphDepth',
    'watchStateFilter',
    'Heuristic diagnostic evidence score',
    'if (tab == 3) heuristicDiagnosticEvidenceScore',
    'databaseReportUrl(lastSharedReportId)',
    '/#reports/',
    'runVulkanSelfTests(target.vendorIdRaw, target.deviceIdRaw)',
    'collectVulkanSelfTest(driverMode, driverIcdPath, driverBundlePath, hookLibDir, targetVendorId, targetDeviceId)',
    'The selected Vulkan physical device was not found in the isolated self-test process.'
]:
    if needle not in kt + cpp:
        errors.append(f'missing 0.41.5 Analysis/Profile requirement: {needle}')
for needle in ['memory/heap/', 'memory/type/', 'videoQueryStatus', 'query/deviceExtensionStatus', 'safety/surfaceFormatRejected', 'display/hdrCapabilityStatus', 'registry/reportSchema', 'profile/']:
    if needle not in kt:
        errors.append(f'0.41.5 snapshot evidence coverage missing: {needle}')

for needle in [
    'fun putCompatible(base: String, value: String, index: Int)',
    'putCompatible("extension/device/${item.name}"',
    'putCompatible("feature/${item.name}"',
    'putCompatible("limit/${item.first}"',
    'putCompatible("format/${item.name}"',
    'if (tab == 0 || tab == 4) vulkanAnalysisSnapshot',
    'entries["surface/formatQuerySecondAttempted"] == "true"',
    'entries["surface/formatQueryResultSecond"] == "0"',
]:
    if needle not in kt:
        errors.append(f'0.41.5 Analysis compatibility/optimization invariant missing: {needle}')
if 'groupName.rfind("selftest:", 0) == 0' not in cpp or 'properties.vendorID == targetVendorId && properties.deviceID == targetDeviceId' not in cpp:
    errors.append('0.41.5 self-test selected-device targeting is incomplete')
if 'std::stoul' in cpp or 'catch (...)' in cpp[cpp.find('groupName.rfind("selftest:", 0)'):cpp.find('groupName == "metadata"')]:
    errors.append('0.41.5 self-test target parsing must not require C++ exception handling')
if errors:
    for error in errors: print(f'FAIL: {error}')
    raise SystemExit(1)
print('VulkanScope release verification: PASS')

print(f'version={version.group(1)} code={code.group(1)} baseline=Vulkan 1.4.360 schema=6 compileHeaders=0b7f383797fa7be53ae28213e001ae60668ee511')
