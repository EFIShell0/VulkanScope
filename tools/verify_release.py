import argparse
import json
import re
import subprocess
import sys
from pathlib import Path

root = Path(__file__).resolve().parents[1]
parser = argparse.ArgumentParser()
parser.add_argument('--skip-regression-contracts', action='store_true')
parser.add_argument('--skip-nested-verifiers', action='store_true')
args = parser.parse_args()
errors = []
gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
root_gradle = (root / 'build.gradle.kts').read_text(encoding='utf-8')
if 'id("com.android.application") version "9.4.0" apply false' not in root_gradle: errors.append('AGP 9.4.0 pin mismatch')
if 'id("org.jetbrains.kotlin.plugin.compose") version "2.4.10" apply false' not in root_gradle: errors.append('Kotlin Compose compiler plugin 2.4.10 pin mismatch')
wrapper_properties = (root / 'gradle/wrapper/gradle-wrapper.properties').read_text(encoding='utf-8')
if 'gradle-9.7.1-bin.zip' not in wrapper_properties: errors.append('Gradle wrapper 9.7.1 pin mismatch')
version = re.search(r'versionName\s*=\s*"([^"]+)"', gradle)
code = re.search(r'versionCode\s*=\s*(\d+)', gradle)
if not version or version.group(1) != '0.80.15': errors.append('versionName mismatch')
if not code or code.group(1) != '815': errors.append('versionCode mismatch')
abi_line = re.search(r'abiFilters \+= listOf\(([^\n]+)\)', gradle)
if not abi_line or any(x not in abi_line.group(1) for x in ['arm64-v8a', 'armeabi-v7a', 'x86_64']): errors.append('required ABI set is incomplete')
if '"x86"' in gradle: errors.append('x86 ABI must remain excluded')
manifest = (root / 'app/src/main/AndroidManifest.xml').read_text(encoding='utf-8')
if manifest.count('android.permission.INTERNET') != 1: errors.append('exactly one INTERNET permission is required for approved HTTPS runtime paths')
catalog = (root / 'app/src/main/cpp/registry_query_catalog.h').read_text(encoding='utf-8')
for needle in ['kCatalogSchemaVersion = 6', 'kBaseline = "Vulkan 1.4.362"', 'findQueryDescriptor']:
    if needle not in catalog: errors.append(f'missing catalog requirement: {needle}')
manifest_json = json.loads((root / 'registry/generated/registry_query_manifest.json').read_text(encoding='utf-8'))
snapshot = json.loads((root / 'registry/generated/coverage_snapshot.json').read_text(encoding='utf-8'))
if manifest_json.get('baseline') != 'Vulkan 1.4.362': errors.append('generated manifest baseline mismatch')
if snapshot.get('baseline') != 'Vulkan 1.4.362': errors.append('coverage snapshot baseline mismatch')

lock_path = root / 'registry/registry_lock.json'
contract_path = root / 'tests/golden/0.80.14_regression_contract.json'
regression_verifier_path = root / 'tools/verify_regression_contracts.py'
quality_gate_path = root / 'tools/quality_gate.py'
upstream_verifier_path = root / 'tools/verify_upstream_registry.py'
spec_regression_path = root / 'tools/verify_spec_regressions.py'
spec_361_regression_path = root / 'tools/verify_1_4_361_regressions.py'
registry_snapshot_path = root / 'tools/verify_registry_snapshot.py'
concurrency_resource_path = root / 'tools/verify_concurrency_resource_contracts.py'
cmake_registry_lock_path = root / 'tools/verify_cmake_registry_lock.py'
compile_regression_path = root / 'tools/verify_compile_regressions.py'
probe_lifecycle_regression_path = root / 'tools/verify_probe_lifecycle_regressions.py'
probe_publication_state_machine_path = root / 'tools/test_probe_publication_state_machine.py'
probe_publication_handshake_path = root / 'tools/verify_probe_publication_handshake.py'
package_reproducibility_path = root / 'tools/verify_package_reproducibility.py'
resource_budget_path = root / 'registry/generated/resource_budget.json'
bundled_registry_path = root / 'registry/upstream/vk.xml'
audit_361_path = root / 'rules/0.41.32_VULKAN_1.4.361_REGISTRY_CONCURRENCY_RESOURCE_AUDIT.md'
audit_4133_path = root / 'rules/0.41.33_VULKAN_1.4.361_CMAKE_REGISTRY_LOCK_BUILD_FIX_AUDIT.md'
audit_4134_path = root / 'rules/0.41.34_RELEASE_COMPILE_REGRESSION_AUDIT.md'
audit_4135_path = root / 'rules/0.41.35_TURNIP_POST_CHECKPOINT_LIFECYCLE_AUDIT.md'
audit_4136_path = root / 'rules/0.41.36_TERMINAL_PROBE_PUBLICATION_HANDSHAKE_AUDIT.md'
audit_4137_path = root / 'rules/0.41.37_PROBE_TIMEOUT_FULL_REAUDIT.md'
audit_4138_path = root / 'rules/0.41.38_TERMINAL_OWNERSHIP_RACE_AUDIT.md'
audit_4139_path = root / 'rules/0.41.39_BASE_TERMINAL_JSON_STRUCTURE_AUDIT.md'
audit_4140_path = root / 'rules/0.41.40_REPORT_SEMANTICS_DATABASE_HTML_AUDIT.md'
audit_4141_path = root / 'rules/0.41.41_SURFACE_BINARY_PROPERTY_PROVENANCE_AUDIT.md'
audit_4142_path = root / 'rules/0.41.42_HTML_STATE_PRESENTATION_REGISTRY_LABEL_AUDIT.md'
audit_4143_path = root / 'rules/0.41.43_DRIVER_SURFACE_REBIND_AUDIT.md'
driver_surface_state_machine_path = root / 'tools/test_driver_surface_rebind_state_machine.py'
driver_surface_rebind_path = root / 'tools/verify_driver_surface_rebind_04143.py'
driver_surface_negative_path = root / 'tools/test_driver_surface_rebind_negative_mutations.py'
audit_4144_path = root / 'rules/0.41.44_PROFILE_REQUIREMENT_EVALUATION_AUDIT.md'
profile_lock_path = root / 'registry/profiles/profile_requirements_lock.json'
profile_state_machine_path = root / 'tools/test_profile_evaluator_state_machine.py'
profile_requirements_path = root / 'tools/verify_profile_requirements_04144.py'
profile_negative_path = root / 'tools/test_profile_requirements_negative_mutations.py'
audit_4145_path = root / 'rules/0.41.45_VULKAN_VIDEO_REGISTRY_CENSUS_AUDIT.md'
video_registry_lock_path = root / 'registry/video_registry_lock.json'
video_registry_source_path = root / 'registry/upstream/video.xml'
video_registry_generated_path = root / 'app/src/main/cpp/video_registry_generated.h'
video_registry_generator_path = root / 'tools/generate_video_registry.py'
video_registry_verifier_path = root / 'tools/verify_video_registry_04145.py'
video_registry_state_machine_path = root / 'tools/test_video_profile_census_state_machine.py'
video_registry_negative_path = root / 'tools/test_video_registry_negative_mutations.py'
audit_4146_path = root / 'rules/0.41.46_UI_INFORMATION_ARCHITECTURE_AUDIT.md'
audit_0800_path = root / 'rules/0.80.0_FULL_SECURITY_MEMORY_SPEC_DESIGN_AUDIT.md'
hardening_0800_verifier_path = root / 'tools/verify_full_hardening_0800.py'
hardening_0800_state_machine_path = root / 'tools/test_full_hardening_0800_state_machine.py'
hardening_0800_negative_path = root / 'tools/test_full_hardening_0800_negative_mutations.py'
audit_0801_path = root / 'rules/0.80.1_OVERVIEW_ENCYCLOPEDIA_AUDIT.md'
audit_0802_path = root / 'rules/0.80.2_DETAILED_VULKAN_ENCYCLOPEDIA_AUDIT.md'
encyclopedia_0802_verifier_path = root / 'tools/verify_encyclopedia_0802.py'
encyclopedia_0802_negative_path = root / 'tools/test_encyclopedia_0802_negative_mutations.py'
encyclopedia_symbol_generator_path = root / 'tools/generate_encyclopedia_symbols.py'
encyclopedia_symbol_index_path = root / 'app/src/main/java/com/efishell/vulkanscope/VulkanSymbolIndex.kt'
audit_0803_path = root / 'rules/0.80.3_OVERVIEW_TOOLS_SEARCH_CRASH_AUDIT.md'
overview_tools_0803_verifier_path = root / 'tools/verify_overview_tools_search_0803.py'
encyclopedia_search_0803_state_machine_path = root / 'tools/test_encyclopedia_search_0803_state_machine.py'
overview_tools_0803_negative_path = root / 'tools/test_overview_tools_search_0803_negative_mutations.py'
audit_0804_path = root / 'rules/0.80.4_DETAIL_SCROLL_TV_COLLECTION_AUDIT.md'
detail_tv_0804_verifier_path = root / 'tools/verify_detail_tv_collection_0804.py'
detail_tv_0804_state_machine_path = root / 'tools/test_detail_tv_collection_0804_state_machine.py'
detail_tv_0804_negative_path = root / 'tools/test_detail_tv_collection_0804_negative_mutations.py'
audit_0805_path = root / 'rules/0.80.5_UPDATE_RELEASE_NOTES_TV_DESIGN_AUDIT.md'
update_release_0805_verifier_path = root / 'tools/verify_update_release_notes_tv_0805.py'
update_release_0805_state_machine_path = root / 'tools/test_update_release_notes_tv_0805_state_machine.py'
update_release_0805_negative_path = root / 'tools/test_update_release_notes_tv_0805_negative_mutations.py'
audit_0806_path = root / 'rules/0.80.6_TALKBACK_LARGE_TEXT_ACCESSIBILITY_AUDIT.md'
accessibility_0806_verifier_path = root / 'tools/verify_accessibility_large_text_0806.py'
accessibility_0806_state_machine_path = root / 'tools/test_accessibility_large_text_0806_state_machine.py'
accessibility_0806_negative_path = root / 'tools/test_accessibility_large_text_0806_negative_mutations.py'
audit_0807_path = root / 'rules/0.80.7_SYSTEM_LANGUAGE_FONT_UPDATE_INFO_AUDIT.md'
system_language_0807_verifier_path = root / 'tools/verify_system_language_font_update_0807.py'
system_language_0807_state_machine_path = root / 'tools/test_system_language_font_update_0807_state_machine.py'
system_language_0807_negative_path = root / 'tools/test_system_language_font_update_0807_negative_mutations.py'
audit_0808_path = root / 'rules/0.80.8_FINAL_AGP_COLLECTION_DATABASE_AUDIT.md'
final_0808_verifier_path = root / 'tools/verify_final_release_0808.py'
final_0808_state_machine_path = root / 'tools/test_final_release_0808_state_machine.py'
final_0808_negative_path = root / 'tools/test_final_release_0808_negative_mutations.py'
audit_0809_path = root / 'rules/0.80.9_DETAIL_BUTTON_ONLY_DATABASE_0.39.23_AUDIT.md'
detail_0809_verifier_path = root / 'tools/verify_detail_button_only_0809.py'
detail_0809_state_machine_path = root / 'tools/test_detail_button_only_0809_state_machine.py'
detail_0809_negative_path = root / 'tools/test_detail_button_only_0809_negative_mutations.py'
audit_0813_path = root / 'rules/0.80.13_MATERIAL3_EXPRESSIVE_FULL_UI_AUDIT.md'
audit_0814_path = root / 'rules/0.80.14_PADDINGVALUES_COMPILE_AUDIT.md'
audit_0815_path = root / 'rules/0.80.15_RESPONSIVE_OVERLAY_DIALOG_UI_AUDIT.md'
responsive_0815_verifier_path = root / 'tools/verify_responsive_overlay_ui_0815.py'
responsive_0815_state_machine_path = root / 'tools/test_responsive_overlay_ui_0815_state_machine.py'
responsive_0815_negative_path = root / 'tools/test_responsive_overlay_ui_0815_negative_mutations.py'
paddingvalues_0814_verifier_path = root / 'tools/verify_paddingvalues_compile_0814.py'
paddingvalues_0814_negative_path = root / 'tools/test_paddingvalues_compile_0814_negative_mutations.py'
material3_0813_verifier_path = root / 'tools/verify_material3_expressive_ui_0813.py'
material3_0813_state_machine_path = root / 'tools/test_material3_expressive_ui_0813_state_machine.py'
material3_0813_negative_path = root / 'tools/test_material3_expressive_ui_0813_negative_mutations.py'
ui_architecture_verifier_path = root / 'tools/verify_ui_information_architecture_04146.py'
scroll_indicator_state_machine_path = root / 'tools/test_scroll_boundary_indicators_state_machine.py'
ui_architecture_negative_path = root / 'tools/test_ui_information_architecture_negative_mutations.py'
html_presentation_path = root / 'tools/verify_html_presentation_04142.py'
html_presentation_negative_path = root / 'tools/test_html_presentation_negative_mutations.py'
report_surface_integrity_path = root / 'tools/verify_report_surface_integrity_04141.py'
report_surface_integrity_state_machine_path = root / 'tools/test_report_surface_integrity_state_machine.py'
report_surface_integrity_negative_path = root / 'tools/test_report_surface_integrity_negative_mutations.py'
report_semantics_state_machine_path = root / 'tools/test_report_semantics_state_machine.py'
report_semantics_path = root / 'tools/verify_report_semantics_04140.py'
base_terminal_json_state_machine_path = root / 'tools/test_base_terminal_json_state_machine.py'
base_terminal_json_path = root / 'tools/verify_base_terminal_json.py'
probe_terminal_ownership_state_machine_path = root / 'tools/test_probe_terminal_ownership_state_machine.py'
probe_terminal_ownership_path = root / 'tools/verify_probe_terminal_ownership.py'
probe_timeout_state_machine_path = root / 'tools/test_probe_timeout_state_machine.py'
probe_timeout_recovery_path = root / 'tools/verify_probe_timeout_recovery.py'
probe_cancellation_state_machine_path = root / 'tools/test_probe_cancellation_state_machine.py'
probe_cancellation_recovery_path = root / 'tools/verify_probe_cancellation_recovery.py'
for required_path in [lock_path, contract_path, regression_verifier_path, quality_gate_path, upstream_verifier_path, spec_regression_path, spec_361_regression_path, registry_snapshot_path, concurrency_resource_path, cmake_registry_lock_path, compile_regression_path, probe_lifecycle_regression_path, probe_publication_state_machine_path, probe_publication_handshake_path, package_reproducibility_path, resource_budget_path, bundled_registry_path, audit_361_path, audit_4133_path, audit_4134_path, audit_4135_path, audit_4136_path, audit_4137_path, audit_4138_path, audit_4139_path, audit_4140_path, audit_4141_path, audit_4142_path, audit_4143_path, audit_4144_path, profile_lock_path, profile_state_machine_path, profile_requirements_path, profile_negative_path, audit_4145_path, video_registry_lock_path, video_registry_source_path, video_registry_generated_path, video_registry_generator_path, video_registry_verifier_path, video_registry_state_machine_path, video_registry_negative_path, audit_4146_path, audit_0800_path, hardening_0800_verifier_path, hardening_0800_state_machine_path, hardening_0800_negative_path, audit_0801_path, audit_0802_path, encyclopedia_0802_verifier_path, encyclopedia_0802_negative_path, encyclopedia_symbol_generator_path, encyclopedia_symbol_index_path, audit_0803_path, overview_tools_0803_verifier_path, encyclopedia_search_0803_state_machine_path, overview_tools_0803_negative_path, audit_0804_path, detail_tv_0804_verifier_path, detail_tv_0804_state_machine_path, detail_tv_0804_negative_path, audit_0805_path, update_release_0805_verifier_path, update_release_0805_state_machine_path, update_release_0805_negative_path, audit_0806_path, accessibility_0806_verifier_path, accessibility_0806_state_machine_path, accessibility_0806_negative_path, audit_0807_path, system_language_0807_verifier_path, system_language_0807_state_machine_path, system_language_0807_negative_path, audit_0808_path, final_0808_verifier_path, final_0808_state_machine_path, final_0808_negative_path, audit_0809_path, detail_0809_verifier_path, detail_0809_state_machine_path, detail_0809_negative_path, audit_0813_path, audit_0814_path, audit_0815_path, responsive_0815_verifier_path, responsive_0815_state_machine_path, responsive_0815_negative_path, paddingvalues_0814_verifier_path, paddingvalues_0814_negative_path, material3_0813_verifier_path, material3_0813_state_machine_path, material3_0813_negative_path, ui_architecture_verifier_path, scroll_indicator_state_machine_path, ui_architecture_negative_path, driver_surface_state_machine_path, driver_surface_rebind_path, driver_surface_negative_path, html_presentation_path, html_presentation_negative_path, report_surface_integrity_path, report_surface_integrity_state_machine_path, report_surface_integrity_negative_path, report_semantics_state_machine_path, report_semantics_path, base_terminal_json_state_machine_path, base_terminal_json_path, probe_timeout_state_machine_path, probe_timeout_recovery_path, probe_cancellation_state_machine_path, probe_cancellation_recovery_path, probe_terminal_ownership_state_machine_path, probe_terminal_ownership_path]:
    if not required_path.is_file(): errors.append(f'0.41.39 quality-gate artifact missing: {required_path.relative_to(root)}')
if lock_path.is_file():
    registry_lock = json.loads(lock_path.read_text(encoding='utf-8'))
    for key, expected in {
        'apiBaseline': 'Vulkan 1.4.362',
        'publishedDate': '2026-09-04',
        'registryRef': '1.4.362',
        'registrySha256': 'cf31c965cf6e788697139601da0c7e02a75a9b6c7ac764e7641f5521ffd9da06',
        'headerCommit': 'ee2ec5fd83dafce291024683b50dc89219333076',
        'headerVersion': 362
    }.items():
        if registry_lock.get(key) != expected: errors.append(f'0.41.32 registry lock mismatch: {key}')
for key in ['registryRef', 'registrySha256', 'publishedDate', 'headerCommit', 'headerVersion', 'validatedPhysicalDeviceQueryExtensionCount']:
    if key not in manifest_json: errors.append(f'0.41.32 reproducible registry manifest field missing: {key}')
if manifest_json.get('validatedPhysicalDeviceQueryExtensionCount') != 304:
    errors.append('current validated physical-device extension coverage count mismatch')
if snapshot.get('validatedPhysicalDeviceQueryExtensionCount') != 304:
    errors.append('current coverage snapshot extension count mismatch')
if manifest_json.get('validatedStablePhysicalDeviceQueryExtensionCount') != 299 or manifest_json.get('validatedProvisionalPhysicalDeviceQueryExtensionCount') != 5:
    errors.append('current stable/provisional physical-device extension coverage split mismatch')
if manifest_json.get('validatedProvisionalPhysicalDeviceQueryExtensions') != ['VK_AMDX_dense_geometry_format', 'VK_AMDX_shader_enqueue', 'VK_KHR_portability_subset', 'VK_NV_cuda_kernel_launch', 'VK_NV_displacement_micromap']:
    errors.append('0.41.32 provisional extension coverage identity mismatch')
if snapshot.get('validatedStablePhysicalDeviceQueryExtensionCount') != 299 or snapshot.get('validatedProvisionalPhysicalDeviceQueryExtensionCount') != 5:
    errors.append('current coverage snapshot stable/provisional split mismatch')
if "'schemaVersion': 4" in (root / 'tools/generate_vk_registry.py').read_text(encoding='utf-8'):
    errors.append('stale schema-4 registry generator remains')

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
database_setup = (root / 'DATABASE_SETUP.md').read_text(encoding='utf-8')
if 'VulkanScope 0.80.15 uses the fixed official VulkanScope Database Worker root:' not in database_setup: errors.append('DATABASE_SETUP current application version mismatch')
if 'VulkanScope Database 0.39.27 is the companion Database for VulkanScope 0.80.15' not in database_setup: errors.append('DATABASE_SETUP current companion mismatch')
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
for needle in ['GIT_TAG ee2ec5fd83dafce291024683b50dc89219333076', '#define VK_HEADER_VERSION[ \\t]+362', '-Wl,-z,relro', '-Wl,-z,now']:
    if needle not in cmake: errors.append(f'missing current native build/security baseline: {needle}')
for needle in ['technicalReport', 'schemaVersion", 3', 'ExpressiveActionButton', 'meta.json', 'canonicalEntry.path.startsWith(rootPrefix)']:
    if needle not in kt: errors.append(f'missing 0.32.x report/UI/runtime hardening: {needle}')
if 'Vulkan 1.4.362 compile headers; validated query catalog Vulkan 1.4.362' not in catalog:
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

coverage_kt = (root / 'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt').read_text(encoding='utf-8')
coverage_extensions = set(re.findall(r'\"(VK_[A-Za-z0-9_]+)\"', coverage_kt))
if len(coverage_extensions) != 304: errors.append(f'current physical-device extension coverage mismatch: {len(coverage_extensions)}')

if '\"Sparse Image Format Properties2\",\"name\":\"' in cpp:
    errors.append('sparse image safety fallback must emit valid JSON without an extra name quote')
for needle in [
    'const size_t capacity = values.size()',
    'returnedCount > capacity',
    'const size_t capacity = values.size()',
    'queueCount > queueCapacity',
    'qcount > queueCapacity',
    'toolCount > toolCapacity',
    'formatCount > formatCapacity',
    'groupCount > groupCapacity'
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
if not (root / 'tools/audit_extension_reference.py').exists(): errors.append('legacy reference/canonical parity audit tool missing')
if not (root / 'tools/compare_reference_4_12.py').exists(): errors.append('field-level legacy reference 4.12 source audit tool missing')
if not (root / 'rules/REFERENCE_PARITY.md').exists(): errors.append('legacy reference parity methodology missing')
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
if kt.count('metric("GPU", reportGpuSummary(report))') != 1:
    errors.append('HTML report GPU hero metric must appear exactly once and use multi-device-safe summary')
if 'private fun reportGpuSummary(report: VulkanReport)' not in kt or 'else -> "${report.devices.size} physical devices"' not in kt:
    errors.append('TXT/HTML report summary must not imply that the first physical device represents a multi-device report')
if 'metric("HDR", hdrTypesText(display))' not in kt:
    errors.append('HTML HDR hero must preserve the same available-empty/unknown/unavailable semantics as TXT/UI')

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
    'videoCodecOperations=${if (queueVideoCodecEvidenceRetained(it)) videoCodecOperationFlags(it.videoCodecOperations) else "Unknown"}',
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
    'lastObservedInode',
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
if 'ee2ec5fd83dafce291024683b50dc89219333076' not in cmake: errors.append('canonical Vulkan-Headers 1.4.362 commit is not pinned')
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
    'implementation("androidx.compose.material3:material3:1.5.0-alpha27")',
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
for needle in ['AnalysisWorkspaceState', 'analysisWorkspaceItems(analysisModel, report, device)', 'VulkanScopeAnalysisSnapshot1', 'ActivityResultContracts.OpenDocument()', 'ActivityResultContracts.CreateDocument("application/json")', 'vulkanSnapshotDiff', 'vulkanProfileRequirements()', 'getSharedPreferences("analysis_tools"', 'Minimal SPIR-V shader module', 'Minimal compute pipeline creation']:
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
if 'BuildConfig.VERSION_NAME' in kt:
    errors.append('0.40.1 Analysis must not depend on BuildConfig.VERSION_NAME')
if '@OptIn(ExperimentalMaterial3ExpressiveApi::class)\nprivate fun LazyListScope.analysisWorkspaceItems' not in kt:
    errors.append('0.40.1/0.41.46 Analysis workspace Material 3 Expressive opt-in is missing or too broad')
if 'Minimal pipeline layout' not in cpp or 'layoutResult == VK_SUCCESS' not in cpp:
    errors.append('0.40.1 pipeline-layout self-test evidence is not consumed')
if '## Release 0.40.1 analysis build-fix requirements' not in (root / 'rules/PROJECT_RULES.md').read_text(encoding='utf-8'):
    errors.append('PROJECT_RULES is missing the 0.40.1 build-fix contract')
if not (root / 'rules/0.40.1_ANALYSIS_BUILD_FIX_AUDIT.md').is_file():
    errors.append('0.40.1 build-fix audit is missing')

for needle in ['validateAnalysisSnapshot', 'ANALYSIS_MAX_ENTRIES = 32768', 'ANALYSIS_MAX_WATCHED = 256', 'readFileTextLimited(resultFile, maxProbeResultBytes.toInt())', 'items(model.diffRows, key = { it.key })', 'Image Format Properties2', 'Safe shader create/destroy path unavailable', 'completed_with_unavailable', 'stopVulkanProbeProcess()']:
    if needle not in kt + cpp:
        errors.append(f'missing 0.40.2 full-audit hardening: {needle}')
if 'rows.take(2500)' in kt:
    errors.append('0.40.2 Compare must not silently truncate diff rows')
if '## Release 0.40.2 full application audit requirements' not in (root / 'rules/PROJECT_RULES.md').read_text(encoding='utf-8'):
    errors.append('PROJECT_RULES is missing the 0.40.2 full-audit contract')
if not (root / 'rules/0.40.2_FULL_APPLICATION_AUDIT.md').is_file():
    errors.append('0.40.2 full-audit record is missing')

ext_ref = json.loads((root / 'registry/generated/extension_reference.json').read_text(encoding='utf-8'))
if ext_ref.get('baseline') != 'Vulkan 1.4.362': errors.append('0.41.0 extension-reference baseline mismatch')
reference_names = {x.get('name') for x in ext_ref.get('entries', []) if isinstance(x, dict)}
if 'private val EMBEDDED_EXTENSION_REFERENCE_NAMES = VULKAN_EXTENSION_REFERENCE.keys' not in kt:
    errors.append('0.41.20 embedded extension-reference subset must derive from the checked-in reference map')
known_extensions = reference_names
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
for needle in ['dependencyGraphEntries', 'maxDepth: Int = 4', 'maxNodes: Int = 64', 'Heuristic diagnostic evidence score', 'not a Vulkan conformance result', 'depends:VK_KHR', '"command" -> ref.commands.any', '"enum" -> ref.enums.any', 'VulkanQrCode(model.sharedReportUrl', 'databaseReportUrl(lastSharedReportId)', 'database_share', 'watchedEvidence']:
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
if 'VulkanDependencyGraph(model.visualGraphNodes' not in kt:
    errors.append('0.41.0 visual graph is not integrated into Analysis workspace')
if 'driverHealth.score?.let { "$it / 100" } ?: "Unavailable"' not in kt:
    errors.append('0.41.0 diagnostic score must not display an unavailable report as 0/100')

if 'com.google.zxing:core:3.5.4' not in gradle: errors.append('0.41.2 local QR dependency pin missing')
if not (root / 'tools/generate_extension_reference.py').is_file(): errors.append('0.41.0 vk.xml extension-reference generator missing')
if '## Release 0.41.0 advanced analysis, registry reference and sharing requirements' not in (root / 'rules/PROJECT_RULES.md').read_text(encoding='utf-8'): errors.append('PROJECT_RULES is missing 0.41.0 contract')
if not (root / 'rules/0.41.0_ADVANCED_ANALYSIS_DATABASE_AUDIT.md').is_file(): errors.append('0.41.0 audit record missing')

if '## Release 0.41.1 Analysis Compose build-fix requirements' not in (root / 'rules/PROJECT_RULES.md').read_text(encoding='utf-8'):
    errors.append('PROJECT_RULES is missing 0.41.1 contract')
if not (root / 'rules/0.41.1_ANALYSIS_COMPOSE_BUILD_FIX_AUDIT.md').is_file():
    errors.append('0.41.1 audit record missing')
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
if 'val diffRows = remember(state.tab, state.baseline, current, state.includeUnchanged, state.diffQuery, state.diffStateFilter, state.diffKindFilter)' not in kt:
    errors.append('Analysis diff calculation is not hoisted and filter-aware at composable scope')
if 'val visualGraphNodes = remember(graphEntries, report, device)' not in kt:
    errors.append('0.41.1 graph calculation is not hoisted to composable scope')


rules_text = (root / 'rules/PROJECT_RULES.md').read_text(encoding='utf-8')

for candidate in root.rglob('*'):
    relative = candidate.relative_to(root).as_posix()
    lowered_parts = [part.lower() for part in candidate.relative_to(root).parts]
    if candidate.is_file() and candidate.name.lower() in {'readme.md', 'release.md'}:
        errors.append(f'forbidden release document remains: {relative}')
    if candidate.is_dir() and 'fastlane' in lowered_parts:
        errors.append(f'forbidden fastlane directory remains: {relative}')
if '## Release 0.41.11 reporting-state and canonical-mask requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.11 reporting-state/canonical-mask contract')
if not (root / 'rules/0.41.11_REPORT_STATE_CANONICAL_MASK_AUDIT.md').is_file():
    errors.append('0.41.11 reporting-state/canonical-mask audit record is missing')
if '## Release 0.41.12 Database submission reliability requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.12 Database submission reliability contract')
if not (root / 'rules/0.41.12_DATABASE_SUBMISSION_RELIABILITY_AUDIT.md').is_file():
    errors.append('0.41.12 Database submission reliability audit record is missing')
if '## Release 0.41.13 Database query-group state requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.13 Database query-group state contract')
if not (root / 'rules/0.41.13_DATABASE_QUERY_GROUP_STATE_AUDIT.md').is_file():
    errors.append('0.41.13 Database query-group state audit record is missing')
for needle in [
    'imageFormatQueryResults = if (group == "imageFormat2") { if ((status == "available" || status == "incomplete") && imageFormatQueryResultMatch != null) imageFormatQueryResultMatch else emptyList() } else device.imageFormatQueryResults',
    'private fun imageFormatQueryGroupState(device: DeviceReport): Pair<String, String>',
    'put("imageFormatQueryStatus", imageFormatGroupState.first)',
    'put("imageFormatQueryReason", imageFormatGroupState.second)'
]:
    if needle not in kt:
        errors.append(f'missing 0.41.13 Database query-group invariant: {needle}')
for needle in [
    'private fun isCompleteReportReady(report: VulkanReport, collectionStatus: CollectionStatus): Boolean =',
    'report.devices.isNotEmpty() && report.error == null && collectionStatus != CollectionStatus.COLLECTING',
    'val completeReportReady = isCompleteReportReady(report, collectionStatus)',
    'if (report.devices.isEmpty()) return@withContext "Submission blocked: the complete report contains no Vulkan physical device."',
    'if (report.error != null) return@withContext "Submission blocked: the Vulkan collection is incomplete. Re-run collection before submitting."',
    'val payload = runCatching { databaseSubmissionJson(context, report, display, mode).toByteArray(Charsets.UTF_8) }',
    'Submission failed: the complete report could not be serialized locally.',
    'Submission failed (HTTP ${response.code}): $message',
    'finally {\n                                submissionInFlight = false'
]:
    if needle not in kt:
        errors.append(f'missing 0.41.12 Database submission reliability invariant: {needle}')
if kt.count('val completeReportReady = isCompleteReportReady(report, collectionStatus)') != 2:
    errors.append('0.41.12 complete-report gate must be shared by Info and Settings')
if 'val payload = databaseSubmissionJson(context, report, display, mode).toByteArray(Charsets.UTF_8)' in kt:
    errors.append('Database payload serialization remains outside the guarded 0.41.12 submission path')
if 'if (bits == 0L) return "0"' not in kt or 'if (bits == 0L) return "NONE"' in kt:
    errors.append('generic zero Vulkan masks must render as numeric 0, not a synthetic NONE token')
for needle in ['VK_TOOL_PURPOSE_VALIDATION_BIT', 'VK_TOOL_PURPOSE_PROFILING_BIT', 'VK_TOOL_PURPOSE_TRACING_BIT', 'VK_TOOL_PURPOSE_ADDITIONAL_FEATURES_BIT', 'VK_TOOL_PURPOSE_MODIFYING_FEATURES_BIT', 'VK_TOOL_PURPOSE_DEBUG_REPORTING_BIT_EXT', 'VK_TOOL_PURPOSE_DEBUG_MARKERS_BIT_EXT', 'UNKNOWN_BITS=0x']:
    if needle not in cpp + kt: errors.append(f'missing canonical flag/tool-purpose evidence: {needle}')
if '{0x1u, "WARNING"}' in cpp:
    errors.append('fabricated/shifted VkToolPurposeFlags decoder remains')
if 'vulkanProfileEvaluations(report, d)' not in kt or 'Authoritative requirements are not mapped in this release' not in kt:
    errors.append('catalog-only profile entries are still silently omitted instead of explicit UNKNOWN evidence')
if '## Release 0.41.2 full correctness, compatibility and reporting audit requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.2 full-audit contract')
if not (root / 'rules/0.41.2_FULL_APPLICATION_AUDIT.md').is_file():
    errors.append('0.41.2 full-audit record is missing')
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
if cpp.count('vulkanRegistryVersion\\":\\"1.4.362') < 3:
    errors.append('all active native checkpoint provenance paths must report Vulkan 1.4.362')
for needle in [
    'private fun resolveInstalledTurnipLibrary(filesDir: File): File?',
    'metadataFiles.size != 1',
    'metadata.optInt("schemaVersion", -1) != 1',
    'declared.contains(\'/\')',
    'declared.contains(\'\\\\\')',
    '!declared.endsWith(".so", true)',
    '!declared.contains("vulkan", true)',
    'libraries.size != 1',
    'canonicalEntry.path.startsWith(rootPrefix)',
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
    'androidx.compose.material3:material3:1.5.0-alpha27',
    'com.squareup.okhttp3:okhttp:5.5.0',
    'com.google.zxing:core:3.5.4'
]:
    if needle not in gradle:
        errors.append(f'missing 0.41.2 dependency baseline: {needle}')
field_generator = (root / 'tools/generate_extension_field_coverage.py').read_text(encoding='utf-8')
if "int(version_match.group(1)) != 362" not in field_generator or "r'#define\\s+VK_HEADER_VERSION\\s+(\\d+)\\b'" not in field_generator:
    errors.append('current extension field generator must validate VK_HEADER_VERSION 362 with a valid word boundary')
for stale in ['vulkanscope.cpp.pre0412audit', 'MainActivity.kt.pre0412audit']:
    if list(root.rglob(stale)):
        errors.append(f'release package contains stale source backup: {stale}')
if list(root.rglob('__pycache__')) or list(root.rglob('*.pyc')):
    errors.append('release package contains Python cache artifacts')

if '## Release 0.41.3 queue and Vulkan Video evidence semantics' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.3 queue/video semantics contract')
if not (root / 'rules/0.41.3_QUEUE_VIDEO_SEMANTICS_AUDIT.md').is_file():
    errors.append('0.41.3 queue/video audit record is missing')
for needle in [
    'val videoCodecQueryStatus: String = "unknown"',
    'val videoCodecQueryReason: String = ""',
    '0 · no VkQueueFlagBits reported',
    'VK_VIDEO_CODEC_OPERATION_NONE_KHR',
    'videoCodecQueryStatus',
    'videoCodecQueryReason',
    'queueVideoCodecQueryState(queue)',
    'hasVideoQueueExtension',
    'Complete device-extension enumeration did not report VK_KHR_video_queue for this device.',
    'Device-extension enumeration is incomplete or unavailable, so absence of VK_KHR_video_queue cannot be established.',
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
for needle in [
    'private val databaseHttpClient = ipv6PreferredHttpClient.newBuilder()',
    '.followRedirects(false)',
    '.followSslRedirects(false)',
    'databaseHttpClient.newCall(request).execute()',
    'put("videoCodecOperations", if (queueVideoCodecEvidenceRetained(q)) q.videoCodecOperations else JSONObject.NULL)',
    'put("videoCodecOperationsU64", if (queueVideoCodecEvidenceRetained(q)) q.videoCodecOperations.toULong().toString() else JSONObject.NULL)',
    'verticalScroll(scrollState)',
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
if '## Release 0.41.6 Kotlin compile-gate requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.6 Kotlin compile-gate contract')
if not (root / 'rules/0.41.6_KOTLIN_COMPILE_FIX_AUDIT.md').is_file():
    errors.append('0.41.6 Kotlin compile-fix audit record is missing')

if '## Release 0.41.7 Image Format Properties2 correctness requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.7 Image Format Properties2 contract')
if not (root / 'rules/0.41.7_IMAGE_FORMAT_PROPERTIES2_AUDIT.md').is_file():
    errors.append('0.41.7 Image Format Properties2 audit record is missing')
image_format_start = cpp.find('} else if (group && std::strcmp(group, "imageFormat2") == 0) {')
image_format_end = cpp.find('} else if (group && std::strcmp(group, "external") == 0) {', image_format_start)
image_format_block = cpp[image_format_start:image_format_end] if image_format_start >= 0 and image_format_end > image_format_start else ''
if not image_format_block:
    errors.append('0.41.7 Image Format Properties2 query group is missing')
else:
    if 'if (baseResult == VK_SUCCESS) {\n                        for (uint32_t handleIndex = 0; handleIndex < 2; ++handleIndex)' in image_format_block:
        errors.append('external image-format queries are still gated by handle-less base success')
    for needle in ['externalAttempts[2]', 'ANDROID_HARDWARE_BUFFER external image-format queries', 'OPAQUE_FD external image-format queries', 'formatNotSupported=', 'firstOtherVkResult=']:
        if needle not in image_format_block:
            errors.append(f'missing 0.41.7 Image Format Properties2 diagnostic: {needle}')
    if image_format_block.find('for (uint32_t handleIndex = 0; handleIndex < 2; ++handleIndex)') < image_format_block.find('const VkResult baseResult'):
        errors.append('external image-format query ordering is invalid')

if '## Release 0.41.8 Image Format Properties2 tuple-result and build baseline requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing historical 0.41.8 Image Format Properties2 contract')
if not (root / 'rules/0.41.8_IMAGE_FORMAT_PROPERTIES2_TUPLE_RESULTS_AGP_9_3_2_AUDIT.md').is_file():
    errors.append('0.41.8 Image Format Properties2 tuple-result audit record is missing')

if '## Release 0.41.9 Image Format Properties2 query-outcome separation requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing historical 0.41.9 Image Format Properties2 outcome-separation contract')
if not (root / 'rules/0.41.9_IMAGE_FORMAT_QUERY_OUTCOME_SEPARATION_AUDIT.md').is_file():
    errors.append('historical 0.41.9 Image Format Properties2 outcome-separation audit record is missing')

if '## Release 0.41.10 Image Format Properties2 tuple-state completeness requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.10 Image Format Properties2 tuple-state completeness contract')
if not (root / 'rules/0.41.10_IMAGE_FORMAT_QUERY_STATE_COMPLETENESS_AUDIT.md').is_file():
    errors.append('0.41.10 Image Format Properties2 tuple-state completeness audit record is missing')
for needle in ['queryResults', '"imageFormatQueryResults"', 'ImageFormatQueryResultEntry', 'parseImageFormatQueryResults', 'exact tuple states; excluded from property/query totals', 'imageFormatQuery/${item.name}', '"not_applicable"', 'VK_KHR_external_memory_fd was not enumerated for this device.', 'VK_ANDROID_external_memory_android_hardware_buffer was not enumerated for this device.']:
    if needle not in cpp + kt:
        errors.append(f'missing 0.41.10 Image Format Properties2 tuple-state completeness evidence: {needle}')
if 'nonSuccessQueryResults' in image_format_block:
    errors.append('0.41.10 must use the complete tuple-state ledger rather than the historical non-success-only collection')
if 'Unsupported: VK_ERROR_FORMAT_NOT_SUPPORTED' in image_format_block or 'Unavailable: VkResult=' in image_format_block:
    errors.append('0.41.10 native Image Format Properties2 query outcomes must remain outside detailedProperties')
for needle in ['queryResults.push_back({baseName, "available", 0, true, ""})', 'queryResults.push_back({externalName, "available", 0, true, ""})', 'queryResults.push_back({externalName, "not_applicable", 0, false, missingReasons[handleIndex]})', 'if (result.hasVkResult) out << result.vkResult; else out << "null"']:
    if needle not in image_format_block:
        errors.append(f'missing 0.41.10 exact tuple ledger native invariant: {needle}')
if 'put("vkResult", result.vkResult ?: JSONObject.NULL)' not in kt or 'put("reason", result.reason)' not in kt:
    errors.append('0.41.10 technicalReport must preserve nullable VkResult and tuple applicability reason')
if '"available" -> vkResult == 0 && reason.isBlank()' not in kt or '"unsupported" -> vkResult == -11 && reason.isBlank()' not in kt or '"not_applicable" -> vkResult == null && reason.isNotBlank()' not in kt:
    errors.append('0.41.10 Kotlin tuple-state parser is not fail-closed')
if 'device.imageFormatQueryResults.forEach' not in kt or '|Reason=${item.reason}' not in kt:
    errors.append('0.41.10 Analysis snapshot must preserve complete Image Format Properties2 tuple state and reason')

fmt_match = re.search(r'static const int32_t values\[\] = \{([^}]*)\};', cpp)
if not fmt_match:
    errors.append('known VkFormat catalog is missing for Image Format Properties2 outcome bound verification')
else:
    fmt_values = [x.strip() for x in fmt_match.group(1).split(',') if x.strip()]
    if len(fmt_values) * 6 > 4096:
        errors.append(f'Image Format Properties2 tuple-outcome structural maximum exceeds Database bound: {len(fmt_values) * 6} > 4096')
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
    'ProfilePropertyComparison.MAXIMUM',
    'extensionPromotedToSatisfied',
    'snapshotDeviceExtensionsComplete',
    'snapshotSurfaceFormatsComplete',
    'diffStateFilter',
    'diffKindFilter',
    'graphDepth',
    'watchStateFilter',
    'Heuristic diagnostic evidence score',
    'if (state.tab == 3) heuristicDiagnosticEvidenceScore',
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
    'if (state.tab == 0 || state.tab == 4) vulkanAnalysisSnapshot',
    'entries["surface/formatQuerySecondAttempted"] == "true"',
    'entries["surface/formatQueryResultSecond"] == "0"',
]:
    if needle not in kt:
        errors.append(f'0.41.5 Analysis compatibility/optimization invariant missing: {needle}')
if 'groupName.rfind("selftest:", 0) == 0' not in cpp or 'properties.vendorID == targetVendorId && properties.deviceID == targetDeviceId' not in cpp:
    errors.append('0.41.5 self-test selected-device targeting is incomplete')
if 'std::stoul' in cpp or 'catch (...)' in cpp[cpp.find('groupName.rfind("selftest:", 0)'):cpp.find('groupName == "metadata"')]:
    errors.append('0.41.5 self-test target parsing must not require C++ exception handling')
if 'Query parameters' not in cpp or 'VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_SAMPLED_BIT' not in cpp:
    errors.append('0.41.8 Image Format Properties2 fixed query recipe provenance is missing')

if '## Release 0.41.14 Kotlin compile-correctness requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.14 Kotlin compile-correctness contract')
if not (root / 'rules/0.41.14_KOTLIN_COMPILE_CORRECTNESS_AUDIT.md').is_file():
    errors.append('0.41.14 Kotlin compile-correctness audit record is missing')
main_source = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8', errors='ignore')
if 'private fun imageFormatQueryGroupState(device: DeviceReport): Pair<String, String>' not in main_source:
    errors.append('imageFormatQueryGroupState must use the actual DeviceReport model type')
if 'imageFormatQueryGroupState(device: GpuInfo)' in main_source:
    errors.append('stale nonexistent GpuInfo type remains in imageFormatQueryGroupState')


if '## Release 0.41.15 canonical enumeration and Android HDR provenance requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.15 canonical enumeration/HDR provenance contract')
if not (root / 'rules/0.41.15_CANONICAL_ENUMERATION_AND_HDR_PROVENANCE_AUDIT.md').is_file():
    errors.append('0.41.15 canonical enumeration/HDR provenance audit record is missing')
for needle in [
    'VK_PRESENT_MODE_SHARED_DEMAND_REFRESH_KHR',
    'VK_PRESENT_MODE_SHARED_CONTINUOUS_REFRESH_KHR',
    'VK_PRESENT_MODE_FIFO_LATEST_READY_KHR',
    'struct InstanceExtensionEnumeration',
    'struct InstanceLayerEnumeration',
    'instanceExtensionStatus',
    'instanceLayerStatus',
    'instanceExtensionsComplete',
    'instanceLayersComplete',
    'extensionEnumerationUncertain'
]:
    if needle not in cpp + kt:
        errors.append(f'missing 0.41.15 enumeration/canonical requirement: {needle}')
for forbidden in [
    'return "VK_PRESENT_MODE_" + std::to_string',
    'return "VK_COLOR_SPACE_" + std::to_string',
    'return "VK_FORMAT_" + std::to_string'
]:
    if forbidden in cpp:
        errors.append(f'0.41.15 synthetic Vulkan enum fallback remains: {forbidden}')
for needle in [
    'Build.VERSION.SDK_INT >= 34 -> "available"',
    'hdr != null -> "available"',
    'display.hdrCapabilityStatus == "available" -> "None reported"',
    'private fun hdrTypesText(display: DisplayReport)',
    'put("query/instanceExtensionStatus", report.instanceExtensionStatus)',
    'put("query/instanceLayerStatus", report.instanceLayerStatus)',
    'HDR capability status: ${display.hdrCapabilityStatus}',
    'Instance extension enumeration',
    'Instance layer enumeration'
]:
    if needle not in kt:
        errors.append(f'missing 0.41.15 report/HDR provenance requirement: {needle}')
if 'vkEnumeratePhysicalDevices failed. VkResult=' not in cpp:
    errors.append('0.41.15 physical-device enumeration failures must retain VkResult evidence')

if '## Release 0.41.18 full correctness, provenance and report-integrity requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.18 full correctness/report-integrity contract')
if not (root / 'rules/0.41.18_FULL_CORRECTNESS_REPORT_INTEGRITY_AUDIT.md').is_file():
    errors.append('0.41.18 full correctness/report-integrity audit record is missing')
for needle in [
    'baseReportComplete: Boolean = false',
    'report.baseReportComplete &&',
    'crashMarkerFile.isFile && crashMarkerFile.length() > 0L',
    'deviceLayerStatus: String = "unknown"',
    'surfaceQueryStatus: String = "unknown"',
    'extensionStatus: String = "unknown"',
    'markSurfaceProbeState',
    'markMetadataProbeUnavailable',
    'if (!report.baseReportComplete) return@withContext',
    'put("status", if (report.baseReportComplete && report.error == null) "available" else "incomplete")'
]:
    if needle not in kt: errors.append(f'missing 0.41.18 report-integrity/provenance gate: {needle}')
for needle in [
    'enumerateInstanceLayerExtensions',
    'enumerateDeviceLayers',
    'enumerateDeviceLayerExtensions',
    'Instance-layer extension enumeration remained VK_INCOMPLETE',
    'Device-layer extension enumeration remained VK_INCOMPLETE',
    'surfaceInstanceExtensionEnumeration.complete ? "not_applicable" : "unavailable"',
    'UNKNOWN(raw-index='
]:
    if needle not in cpp: errors.append(f'missing 0.41.18 native correctness gate: {needle}')
if 'UNKNOWN_VK_' in cpp or 'UNKNOWN_VK_' in kt:
    errors.append('synthetic official-looking UNKNOWN_VK_* token remains')
for dead_helper in ['private fun SectionCard(', 'private fun KeyValue(', 'private fun DataRow(']:
    if dead_helper in kt:
        errors.append(f'unused legacy Compose helper remains: {dead_helper}')
for needle in [
    'put("$layerBase/extensionQueryStatus", layer.extensionStatus)',
    'put("$layerBase/extensionQueryReason", layer.extensionReason)',
    'put("$layerBase/extensionsComplete", layer.extensionsComplete.toString())',
]:
    if kt.count(needle) < 2:
        errors.append(f'Analysis snapshot is missing per-layer extension provenance: {needle}')
if 'the published specification is Vulkan 1.4.358' in rules_text:
    errors.append('PROJECT_RULES contains stale current-spec wording for Vulkan 1.4.358')
if 'published specification is Vulkan 1.4.362 dated 2026-09-04' not in rules_text:
    errors.append('PROJECT_RULES does not state the current published Vulkan 1.4.362 baseline')
if 'baseReportReady' in kt:
    errors.append('legacy heuristic baseReportReady acceptance remains')
if 'partialCandidate ?:' in kt or 'value = partialCandidate' in kt:
    errors.append('partial base checkpoint can still escape as a completed report')
if '## Release 0.41.17 native warning-clean compile requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.17 native warning-clean compile contract')
if not (root / 'rules/0.41.17_NATIVE_WARNING_CLEAN_COMPILE_AUDIT.md').is_file():
    errors.append('0.41.17 native warning-clean compile audit record is missing')
if 'std::vector<VkLayerProperties> instanceLayers(VulkanApi& api)' in cpp:
    errors.append('obsolete unused instanceLayers(VulkanApi&) wrapper remains and breaks -Werror release compilation')

if '## Release 0.41.19 collection serialization, report preservation and package hygiene requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.19 collection/report/package contract')
if not (root / 'rules/0.41.19_COLLECTION_SERIALIZATION_REPORT_PRESERVATION_AUDIT.md').is_file():
    errors.append('0.41.19 collection serialization/report-preservation audit record is missing')
for needle in [
    'private val probeMutex = Mutex()',
    'private suspend fun runServiceProbe(group: String, surface: Surface?, timeoutMs: Long, modeSnapshot: DriverMode): String = probeMutex.withLock {',
    'return@withLock if (group == "base")',
    'private fun sanitizeReportLabel(value: String): String = value.removePrefix("Validated extension coverage · ")',
    'if (collectionInFlight || pendingCollectionTasks.isNotEmpty()) {',
    'mergeSurfaceProbeReport(latestReport ?: base, rawSurface)',
    'markSurfaceProbeState(latestReport ?: base, "unavailable"',
    'generation == collectionGeneration && modeSnapshot == driverMode && !collectionInFlight',
    'if (collectionPending && !isFinishing && !isDestroyed) {',
]:
    if needle not in kt:
        errors.append(f'missing 0.41.19 collection/report-preservation gate: {needle}')
forbidden_comparison_name = ('Caps' + 'Viewer').lower()
text_suffixes = {'.kt', '.kts', '.java', '.cpp', '.cc', '.c', '.h', '.hpp', '.py', '.md', '.txt', '.xml', '.json', '.toml', '.yml', '.yaml', '.gradle', '.pro'}
for candidate in root.rglob('*'):
    relative = candidate.relative_to(root).as_posix()
    if forbidden_comparison_name in relative.lower():
        errors.append(f'forbidden third-party comparison-product name remains in packaged path: {relative}')
    if candidate.is_file() and (candidate.suffix.lower() in text_suffixes or candidate.name == 'files.txt'):
        try:
            candidate_text = candidate.read_text(encoding='utf-8')
        except (UnicodeDecodeError, OSError):
            continue
        if forbidden_comparison_name in candidate_text.lower():
            errors.append(f'forbidden third-party comparison-product name remains in packaged text: {relative}')



if '## Release 0.41.20 full reporting, registry-reference and stale-state correctness requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.20 full correctness contract')
if not (root / 'rules/0.41.20_FULL_REPORT_REGISTRY_STALE_STATE_AUDIT.md').is_file():
    errors.append('0.41.20 full report/registry/stale-state audit record is missing')
for needle in [
    'instanceApiVersion: String = "Unknown"',
    'CapabilityKeyValue("Base probe instance API", report.instanceApiVersion)',
    'put("instanceApiVersion", report.instanceApiVersion)',
    'instanceGroupProperties',
    'root.optJSONArray("groupProperties")',
    '"Surface probe instance API" to root.optString("selectedApiVersion", "Unknown")',
    '"Surface format query API"',
    'if (collectionInFlight || pendingCollectionTasks.isNotEmpty()) {\n            synchronized(surfaceLock) {',
    'private fun requestQueryGroup(group: String) {\n        if (driverImportInFlight) {\n            collectionPending = true\n            return\n        }\n        if (collectionInFlight) return',
    'prefs.edit().putString("driver_mode", mode.name).apply()',
    'private val EMBEDDED_EXTENSION_REFERENCE_NAMES = VULKAN_EXTENSION_REFERENCE.keys',
    'checked-in Vulkan 1.4.362 registry census',
    'runtime enumeration and registry registration remain separate evidence',
    'VK_EXT_swapchain_colorspace',
    'val complete = if (extension in PROFILE_INSTANCE_EXTENSIONS) report?.instanceExtensionStatus == "available" else device.deviceExtensionStatus == "available"',
    'VK_PHYSICAL_DEVICE_TYPE_INTEGRATED_GPU',
    'UNKNOWN(raw=$type)',
    'if (name == "capabilityResult") return value.toIntOrNull()?.let(::vkResultText) ?: value',
    '"VP_ANDROID_16_requirements", "r.7", "1.3.276"',
    '"shaderSubgroupUniformControlFlow"',
    'ProfilePropertyComparison.EQUAL_BOOL, "standardSampleLocations" to "true", "timestampComputeAndGraphics" to "true"'
]:
    if needle not in kt:
        errors.append(f'missing 0.41.20 correctness gate: {needle}')
for forbidden in ['"Integrated GPU"', '"Discrete GPU"', '"Virtual GPU"', '"Platform independent or unavailable in checked-in reference asset"', '"No embedded promotion record"', 'if (raw.isBlank()) return@launch']:
    if forbidden in kt:
        errors.append(f'0.41.20 misleading or lossy reporting pattern remains: {forbidden}')
if 'VK_EXT_cooperative_matrix_maintenance1' not in reference_names or 'VK_EXT_image_tiling_control' not in reference_names:
    errors.append('0.41.20 embedded extension-reference subset misses the 1.4.358/1.4.359 additions')
manifest_runtime = manifest_json.get('runtimeRegistryTokenReferenceCount')
if manifest_json.get('implementedPhysicalDeviceStructCount') != snapshot.get('implementedPhysicalDeviceStructCount'):
    errors.append('0.41.20 registry manifest/snapshot implemented-struct count mismatch')
if manifest_json.get('validatedRuntimeQueryGroupCount') != snapshot.get('validatedRuntimeQueryGroupCount'):
    errors.append('0.41.20 registry manifest/snapshot query-group count mismatch')
if manifest_runtime != snapshot.get('runtimeRegistryTokenReferenceCount'):
    errors.append('0.41.20 registry manifest/snapshot runtime-token count mismatch')
if manifest_json.get('validatedRuntimeQueryGroupCount') != 104 or manifest_runtime != 268:
    errors.append('registry audit counts do not match the current native catalog')
if 'runtimeExtensionTokens' in manifest_json:
    errors.append('0.41.20 stale/misnamed runtimeExtensionTokens manifest field remains')

if 'runtimeExtensionTokenCount' in cpp:
    errors.append('0.41.20 misleading native runtimeExtensionTokenCount key remains')
if 'bool apiVersionAtLeast(uint32_t value, uint32_t major, uint32_t minor)' not in cpp:
    errors.append('0.41.20 major/minor Vulkan API comparison helper is missing')
for stale_api_gate in [
    'VK_API_VERSION_MINOR(apiVersion) >= 1',
    'VK_API_VERSION_MINOR(apiVersion) < 1',
    'VK_API_VERSION_MINOR(apiVersion) >= 4',
    'VK_API_VERSION_MINOR(apiVersion) < 4',
    'VK_API_VERSION_MINOR(apiVersion) < targetMinor'
]:
    if stale_api_gate in cpp:
        errors.append(f'0.41.20 minor-only Vulkan API gate remains: {stale_api_gate}')
cmake_text = (root / 'app/src/main/cpp/CMakeLists.txt').read_text(encoding='utf-8')
if 'GIT_TAG 8fae8ce254dfc1344527e05301e43f37dea2df80' not in cmake_text:
    errors.append('0.41.20 libadrenotools dependency is not pinned to the verified full commit')
if re.search(r'GIT_TAG\s+8fae8ce254dfc1344527e05301e43f37dea2df80\s+GIT_SHALLOW\s+TRUE', cmake_text):
    errors.append('0.41.20 commit-hash FetchContent pin incorrectly enables GIT_SHALLOW')
if 'GIT_TAG 8fae8ce254dfc1344527e05301e43f37dea2df80\n        GIT_SHALLOW FALSE' not in cmake_text:
    errors.append('0.41.20 libadrenotools full commit pin must use non-shallow fetch semantics')
if 'Group containing device' in cpp or 'Query performed for this Vulkan instance' in cpp:
    errors.append('0.41.20 synthetic physical-device-group placeholder remains')
for needle in [
    'val modeSnapshot = driverMode',
    'collectReport(modeSnapshot)',
    'enrichReport(base, modeSnapshot)',
    'startBackgroundInformationCollection(generation, modeSnapshot)',
    'generation != collectionGeneration || modeSnapshot != driverMode',
    '.putExtra(VulkanProbeService.EXTRA_DRIVER_MODE, modeSnapshot.name)',
    'latestReport = null\n        reportLoading = true',
    'JSONObject().put("status", "unavailable").put("group", group).put("reason", "The dedicated background query returned no data.")',
    'status == "incomplete"'
]:
    if needle not in kt:
        errors.append(f'missing 0.41.20 driver/report-race gate: {needle}')

for needle in [
    'runtimeRegistryTokenReferenceCount: Int = 0',
    'put("deviceIdRaw", d.deviceIdRaw)',
    'put("querySafety", JSONObject().apply',
    'put("queueFamilyEnumerationRejected", d.queueQuerySafetyRejected)',
    'put("formatQueryResultCanonical", if (d.surfaceFormatQueryAttempted) vkResultText(d.surfaceFormatQueryResult) else "Not attempted")',
    'put("formatQueryResultSecondCanonical", if (d.surfaceFormatQuerySecondAttempted) vkResultText(d.surfaceFormatQueryResultSecond) else "Not attempted")',
    'appendLine("Base report complete: ${report.baseReportComplete}")',
    'metric("Base report complete", report.baseReportComplete.toString())',
    'registry.optInt("runtimeRegistryTokenReferenceCount", registry.optInt("runtimeExtensionTokenCount"',
    'put("runtimeRegistryTokenReferenceCount", report.registryCoverage.runtimeRegistryTokenReferenceCount)',
    'instanceGroupStatus: String = "unknown"',
    'instanceGroupEnumerationResult: Int? = null',
    'if (status == "available" || status == "incomplete") instanceGroupProperties else emptyList()',
    'put("instanceGroupEnumerationComplete", report.instanceGroupEnumerationComplete)',
    'CapabilityKeyValue("Enumeration result", report.instanceGroupEnumerationResult?.let(::vkResultText) ?: "Unknown")'
]:
    if needle not in kt:
        errors.append(f'missing 0.41.20 group/registry semantic gate: {needle}')


if '## Release 0.41.21 Surface, external-capability and evidence-state correctness requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.21 Surface/external/evidence-state correctness contract')
if not (root / 'rules/0.41.21_SURFACE_EXTERNAL_EVIDENCE_AUDIT.md').is_file():
    errors.append('0.41.21 Surface/external/evidence audit record is missing')
for needle in [
    'surfaceColorSpaceExtensionStatus: String = "unknown"',
    'private data class PresentationQueueEvidence(',
    'presentationQueueEvidence: List<PresentationQueueEvidence> = emptyList()',
    'surfaceFormatQueryAttempted: Boolean = false',
    'surfaceDependentWsiQueryStatus: String = "unknown"',
    'surfaceColorSpaceExtensionEvidence(',
    '"Unknown: instance-extension enumeration is incomplete or unavailable"',
    'presentationQueueEvidenceText(entry: PresentationQueueEvidence)',
    'put("colorSpaceExtensionStatus", d.surfaceColorSpaceExtensionStatus)',
    'put("queryResultName", vkResultText(result))',
    '"Unavailable: requires Android API 29+"',
    '"Unknown: HDR capabilities unavailable"',
    'device.copy(surfaceColorSpaceExtensionAvailable = false, surfaceColorSpaceExtensionStatus = "unknown", surfaceColorSpaceExtensionEnabled = false)'
]:
    if needle not in kt:
        errors.append(f'missing 0.41.21 Kotlin evidence gate: {needle}')
for needle in [
    'surfaceColorSpaceExtensionStatus',
    'presentationQueryComplete',
    'dependentWsiQueryStatus',
    r'\"formatQueryAttempted\":false',
    r'\"presentModeQueryAttempted\":false',
    'std::vector<VkResult> presentationQueryResults',
    r'\"queryResult\":',
    r'\"queryStatus\":',
    'queryExternalBuffer("VK_KHR_external_memory_fd", VK_EXTERNAL_MEMORY_HANDLE_TYPE_OPAQUE_FD_BIT, "VK_EXTERNAL_MEMORY_HANDLE_TYPE_OPAQUE_FD_BIT")',
    'queryExternalBuffer("VK_EXT_external_memory_dma_buf", VK_EXTERNAL_MEMORY_HANDLE_TYPE_DMA_BUF_BIT_EXT, "VK_EXTERNAL_MEMORY_HANDLE_TYPE_DMA_BUF_BIT_EXT")',
    'queryExternalBuffer("VK_ANDROID_external_memory_android_hardware_buffer", VK_EXTERNAL_MEMORY_HANDLE_TYPE_ANDROID_HARDWARE_BUFFER_BIT_ANDROID, "VK_EXTERNAL_MEMORY_HANDLE_TYPE_ANDROID_HARDWARE_BUFFER_BIT_ANDROID")',
    'queryExternalFence(VK_EXTERNAL_FENCE_HANDLE_TYPE_OPAQUE_FD_BIT, "VK_EXTERNAL_FENCE_HANDLE_TYPE_OPAQUE_FD_BIT")',
    'queryExternalFence(VK_EXTERNAL_FENCE_HANDLE_TYPE_SYNC_FD_BIT, "VK_EXTERNAL_FENCE_HANDLE_TYPE_SYNC_FD_BIT")',
    'queryExternalSemaphore(VK_EXTERNAL_SEMAPHORE_HANDLE_TYPE_OPAQUE_FD_BIT, "VK_EXTERNAL_SEMAPHORE_HANDLE_TYPE_OPAQUE_FD_BIT")',
    'queryExternalSemaphore(VK_EXTERNAL_SEMAPHORE_HANDLE_TYPE_SYNC_FD_BIT, "VK_EXTERNAL_SEMAPHORE_HANDLE_TYPE_SYNC_FD_BIT")',
    'Unavailable: vkGetPhysicalDeviceExternalBufferProperties entry point is unavailable.',
    'Unavailable: vkGetPhysicalDeviceExternalFenceProperties entry point is unavailable.',
    'Unavailable: vkGetPhysicalDeviceExternalSemaphoreProperties entry point is unavailable.'
    ,'VK_KHR_device_group_creation'
    ,'VK_KHR_external_memory_capabilities'
    ,'VK_KHR_external_fence_capabilities'
    ,'VK_KHR_external_semaphore_capabilities'
    ,'vkEnumeratePhysicalDeviceGroupsKHR'
]:
    if needle not in cpp:
        errors.append(f'missing 0.41.21 native evidence gate: {needle}')
for forbidden in [
    'const bool hasLiveSurface = false',
    'VkSurfaceKHR liveSurface',
    'Opaque FD Fence',
    'Opaque FD Semaphore',
    'One or more external capability query entry points are unavailable',
    'getPhysicalDeviceFormatProperties2(devices[i], fmt, &p2), true'
]:
    if forbidden in cpp:
        errors.append(f'0.41.21 stale/misleading native pattern remains: {forbidden}')
if cpp.count('ANativeWindow_fromSurface') != 1:
    errors.append('0.41.21 real Android Surface ownership must exist only in the isolated Surface probe')
if 'device.surfaceQueryStatus == "available" -> "Not supported by this Vulkan device"' not in kt or 'device.surfaceQueryStatus == "incomplete" -> "Unknown: Surface query is incomplete"' not in kt:
    errors.append('0.41.22 incomplete Surface query must not be rendered as presentation unsupported')
if 'device?.surfaceColorSpaceExtensionStatus == "not_exposed" -> "Not exposed"' not in kt:
    errors.append('0.41.21 VK_EXT_swapchain_colorspace absence requires explicit complete-enumeration state')
if 'if (presentationSupported) {' not in cpp or 'formatEnumeration = enumerateSurfaceFormatsRobust' not in cpp or 'presentModeEnumeration = enumerateSurfacePresentModesRobust' not in cpp:
    errors.append('0.41.21 dependent WSI queries must be gated by proven Surface presentation support')
if 'const char* dependentWsiQueryStatus = presentationSupported ?' not in cpp:
    errors.append('0.41.21 dependent WSI prerequisite status is missing')
if 'put("formatQueryResult", if (d.surfaceFormatQueryAttempted) d.surfaceFormatQueryResult else JSONObject.NULL)' not in kt:
    errors.append('0.41.21 unattempted Surface-format query must serialize null rather than a fabricated VkResult sentinel')
if 'put("formatQueryResultCanonical", if (d.surfaceFormatQueryAttempted) vkResultText(d.surfaceFormatQueryResult) else "Not attempted")' not in kt:
    errors.append('0.41.21 unattempted Surface-format query must not canonicalize the -1 compatibility sentinel')
for needle in [
    'const bool deviceEnumerationComplete = baseDevicesResult.complete',
    'deviceEnumerationResult != VK_SUCCESS && deviceEnumerationResult != VK_INCOMPLETE',
    'Physical-device enumeration remained VK_INCOMPLETE after bounded retries; returned devices are retained as partial positive evidence and the base report is not complete.',
    'physicalDeviceEnumerationResult',
    'physicalDeviceEnumerationComplete',
    'jsonBool(deviceEnumerationComplete)',
    'matchingPhysicalDevices.size() > 1',
    'no stable cross-process identity is available'
]:
    if needle not in cpp:
        errors.append(f'missing 0.41.21 physical-device enumeration/identity gate: {needle}')
for needle in [
    'physicalDeviceEnumerationResult: Int? = null',
    'physicalDeviceEnumerationComplete: Boolean = false',
    'status == "incomplete" || status == "not_applicable"',
    'lastParsed = parsed',
    'nativeStatus == "incomplete"',
    'basePhysicalIdentityUnique(',
    'jsonPhysicalIdentityMatches(',
    'dedicated-process evidence cannot be attributed safely because vendorId/deviceId is ambiguous across physical devices',
    'Physical-device enumeration result',
    'put("physicalDeviceEnumerationResult", report.physicalDeviceEnumerationResult ?: JSONObject.NULL)',
    'put("physicalDeviceEnumerationComplete", report.physicalDeviceEnumerationComplete)'
]:
    if needle not in kt:
        errors.append(f'missing 0.41.21 Kotlin physical-device evidence gate: {needle}')
if kt.count('val status = root.optString("status", "unavailable")') != 3:
    errors.append('0.41.21 merge status declarations are missing or duplicated')
if 'surfaceCapabilities2Usable' not in cpp or 'classic VK_KHR_surface queries are used as a fallback' not in cpp:
    errors.append('0.41.21 advertised-but-unusable SurfaceCapabilities2 must fall back to classic WSI queries')
if 'status != "available" && status != "incomplete"' not in kt:
    errors.append('0.41.21 partial Surface positive evidence must survive an incomplete root probe status')

if '## Release 0.41.16 native/Kotlin compile-correctness requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.16 native/Kotlin compile-correctness contract')
if not (root / 'rules/0.41.16_NATIVE_KOTLIN_COMPILE_CORRECTNESS_AUDIT.md').is_file():
    errors.append('0.41.16 native/Kotlin compile-correctness audit record is missing')
if 'deviceExtensionEnumeration.status != "available"' in cpp:
    errors.append('DeviceExtensionEnumeration.status must not be compared to a string literal by pointer identity')
if 'std::strcmp(deviceExtensionEnumeration.status, "available") != 0' not in cpp:
    errors.append('DeviceExtensionEnumeration.status content comparison gate is missing')
if main_source.count('private fun hdrTypesText(display: DisplayReport): String') != 1:
    errors.append('hdrTypesText must exist exactly once')
main_class_start = main_source.find('class MainActivity : ComponentActivity() {')
main_class_end = main_source.find('\n}\n\nprivate fun hdrTypeName', main_class_start)
hdr_helper_pos = main_source.find('private fun hdrTypesText(display: DisplayReport): String')
if main_class_start < hdr_helper_pos < main_class_end:
    errors.append('hdrTypesText must be top-level so top-level report serializers can resolve it')
if hdr_helper_pos < 0 or hdr_helper_pos > main_source.find('private fun reportToText'):
    errors.append('top-level hdrTypesText helper must be visible before report serializers')

if 'Image Format Properties2 Query Diagnostics\\",\\"name\\":\\"Query parameters' not in cpp or 'VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_SAMPLED_BIT' not in cpp:
    errors.append('0.41.8 Image Format Properties2 fixed query recipe provenance is missing')


if '## Release 0.41.22 full re-audit, multi-device and query-state correctness requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.22 full re-audit/multi-device/query-state contract')
if not (root / 'rules/0.41.22_FULL_REAUDIT.md').is_file():
    errors.append('0.41.22 full re-audit record is missing')
if cpp.count('VK_API_VERSION_MINOR(') != 2:
    errors.append('0.41.22 direct minor-only Vulkan capability gate remains outside canonical version helpers')
for needle in [
    'hdrTypesResult.isFailure -> "unavailable"',
    'display.hdrCapabilityStatus == "available" -> "None reported"',
    'Available: zero matching video formats reported for this sampled profile and usage.',
    'Incomplete: VK_INCOMPLETE returned zero entries; absence is not proven.',
    'Available: zero active Vulkan tools were reported.',
    'Incomplete: VK_INCOMPLETE returned zero tool entries; absence is not proven.',
    'PhysicalDeviceSelector(current.devices, selectedDeviceIndex)',
    'report.devices.getOrNull(selectedDeviceIndex) ?: report.devices.firstOrNull()',
    'TurnipSupport.UNKNOWN -> "Unknown: waiting for complete platform/Vulkan evidence"',
    'Device-layer enumeration is incomplete; an empty list is not proof that no device layers exist.',
    'Driver bundle contains duplicate archive paths',
    'val safeResultPath = requestedResult.path'
]:
    if needle not in (kt + '\n' + cpp + '\n' + probe_service):
        errors.append(f'missing 0.41.22 correctness/security gate: {needle}')
if 'Page.Vulkan -> VulkanPage(report, device, turnipSupport == TurnipSupport.SUPPORTED)' in kt:
    errors.append('0.41.22 Vulkan page still collapses Turnip UNKNOWN and UNSUPPORTED to one boolean')
if 'const uint32_t minor = VK_API_VERSION_MINOR(apiVersion);' in cpp or 'const uint32_t apiMinor = VK_API_VERSION_MINOR(apiVersion);' in cpp:
    errors.append('0.41.22 minor-only Vulkan capability gating remains')
if 'Build.VERSION.SDK_INT >= 34 -> if (hdrNames.isNotEmpty()) "available" else "unavailable"' in kt:
    errors.append('0.41.22 successful empty HDR-type query is still misreported unavailable')
if '1000483000 -> "VK_PIPELINE_BINARY_MISSING_KHR (1000483000)"' not in kt or '-1000483000 -> "VK_ERROR_NOT_ENOUGH_SPACE_KHR (-1000483000)"' not in kt:
    errors.append('0.41.22 canonical VkResult coverage is missing current Vulkan 1.4.360 result values')
if 'put("vkResultName", result.vkResult?.let(::vkResultText) ?: JSONObject.NULL)' not in kt:
    errors.append('0.41.22 Image Format Properties2 structured results are missing canonical VkResult text')

if '## Release 0.41.23 Vulkan Video routing, partial-evidence and update-version integrity requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.23 Vulkan Video/update integrity contract')
if not (root / 'rules/0.41.23_VULKAN_VIDEO_UPDATE_INTEGRITY_AUDIT.md').is_file():
    errors.append('0.41.23 Vulkan Video/update integrity audit record is missing')
for needle in [
    'private fun queueVideoCodecEvidenceRetained(queue: QueueEntry): Boolean = queue.videoCodecQueryStatus == "available" || queue.videoCodecQueryStatus == "incomplete"',
    '"incomplete" -> "Incomplete${queue.videoCodecQueryReason',
    'if (queueVideoCodecEvidenceRetained(queue)) CapabilityKeyValue("Video codec operations"',
    'if (queueVideoCodecEvidenceRetained(queue)) put("queue/${queue.index}/videoCodecOperations"',
    'put("videoCodecOperations", if (queueVideoCodecEvidenceRetained(q)) q.videoCodecOperations else JSONObject.NULL)',
    'archiveVersion != update.version',
    'Downloaded package versionName does not match the selected VulkanScope release.'
]:
    if needle not in kt:
        errors.append(f'missing 0.41.23 Kotlin integrity gate: {needle}')
extension_start = cpp.find('std::string collectVulkanExtensionGroup')
advanced_start = cpp.find('std::string collectVulkanAdvancedGroup')
simple_start = cpp.find('std::string collectVulkanSimpleFeatureGroup')
extension_body = cpp[extension_start:advanced_start] if extension_start >= 0 and advanced_start > extension_start else ''
advanced_body = cpp[advanced_start:simple_start] if advanced_start >= 0 and simple_start > advanced_start else ''
if 'std::strcmp(group, "videoCapabilities")' in extension_body or 'std::strcmp(groupName, "videoCapabilities")' in extension_body:
    errors.append('0.41.23 videoCapabilities remains misrouted inside the device-extension collector')
for needle in [
    'group && std::strcmp(group, "videoCapabilities") == 0',
    'Unknown: device-extension enumeration is incomplete or unavailable, so absence of VK_KHR_video_queue cannot be established.',
    'Video format query',
    'vkGetPhysicalDeviceVideoFormatPropertiesKHR is unavailable in this Vulkan stack.',
    'VK_IMAGE_USAGE_VIDEO_DECODE_DST_BIT_KHR',
    'VK_IMAGE_USAGE_VIDEO_DECODE_DPB_BIT_KHR',
    'VK_IMAGE_USAGE_VIDEO_ENCODE_SRC_BIT_KHR',
    'VK_IMAGE_USAGE_VIDEO_ENCODE_DPB_BIT_KHR',
    'Unsupported for sampled profile/usage (VkResult=',
    'Video capability query',
    'VkVideoEncodeH264CapabilitiesKHR',
    'VkVideoEncodeH265CapabilitiesKHR',
    'VkVideoEncodeAV1CapabilitiesKHR',
    'encodeCaps.pNext = codecCaps'
]:
    if needle not in advanced_body:
        errors.append(f'missing 0.41.23 advanced Vulkan Video gate: {needle}')
if not ('Sampled capability profiles use 4:2:0 chroma with 8-bit luma/chroma' in advanced_body or 'Registry-driven codec-profile census.' in advanced_body): errors.append('missing 0.41.23+ Vulkan Video capability recipe disclosure')
if not ('Unsupported for sampled profile (VkResult=' in advanced_body or 'Unsupported for exact 4:2:0 8-bit profile (VkResult=' in advanced_body): errors.append('missing 0.41.23+ Vulkan Video exact unsupported state mapping')
if 'if (!extensionPresent && group && std::strcmp(group, "videoCapabilities") != 0) continue;' in cpp:
    errors.append('0.41.23 obsolete videoCapabilities extension-collector bypass remains')
if '} else if (!api.getPhysicalDeviceVideoCapabilitiesKHR) {' in advanced_body:
    errors.append('0.41.23 video-format evidence remains incorrectly gated by the video-capabilities entry point')
if 'VkVideoEncodeCapabilitiesKHR encodeCaps{};' not in advanced_body or 'encodeCaps.pNext = codecCaps;' not in advanced_body:
    errors.append('0.41.23 sampled encode capability chain is missing the codec-specific pNext requirement')
if 'VkPhysicalDeviceVideoFormatInfoKHR info{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_FORMAT_INFO_KHR, &list, VK_IMAGE_USAGE_SAMPLED_BIT}' in advanced_body:
    errors.append('0.41.23 Vulkan Video format query still uses generic SAMPLED image usage instead of video decode/encode usage categories')

if '## Release 0.41.24 Surface enumeration, bounded partial-evidence and native-lifecycle requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.24 Surface/enumeration/lifecycle contract')
if not (root / 'rules/0.41.24_SURFACE_ENUMERATION_EVIDENCE_LIFECYCLE_AUDIT.md').is_file():
    errors.append('0.41.24 Surface/enumeration/lifecycle audit record is missing')
for forbidden in [
    'result.result = VK_ERROR_OUT_OF_HOST_MEMORY',
    'surfaceFormatQuerySecondAttempted == true && device.surfaceFormatQueryResultSecond == 0',
    'surfaceFormatQueryResultSecond == 0 && !device.surfaceFormatQuerySafetyRejected'
]:
    if forbidden in (cpp + '\n' + kt):
        errors.append(f'0.41.24 stale/synthetic evidence pattern remains: {forbidden}')
for needle in [
    'bool safetyRejected = false;',
    'Physical-device count exceeded the local bounded-allocation safety limit.',
    'A later physical-device count retry failed; earlier bounded partial device evidence was retained.',
    'jsonBool(baseDevicesResult.safetyRejected)',
    'jsonString(baseDevicesResult.localReason)',
    'jsonBool(surfaceDevicesResult.safetyRejected)',
    'jsonString(surfaceDevicesResult.localReason)',
    'Surface-format enumeration remained VK_INCOMPLETE after four bounded attempts; returned entries are retained as partial positive evidence.',
    'Present-mode enumeration remained VK_INCOMPLETE after four bounded attempts; returned entries are retained as partial positive evidence.',
    'The Surface-format query completed with zero entries even though Vulkan requires at least one supported surface format for a supported surface.',
    'The completed present-mode enumeration omitted VK_PRESENT_MODE_FIFO_KHR, which Vulkan requires to be supported.',
    'Classic Surface-format fallback did not produce retained entries; earlier bounded partial vkGetPhysicalDeviceSurfaceFormats2KHR evidence was retained.',
    'formatQuerySpecAnomaly',
    'formatEnumerationComplete',
    'presentModeQuerySpecAnomaly',
    'presentModeEnumerationComplete',
    'g_previousProbeSignalActions',
    'sigaction(kProbeSignals[i], &g_previousProbeSignalActions[i], nullptr)'
]:
    if needle not in cpp:
        errors.append(f'missing 0.41.24 native evidence/lifecycle gate: {needle}')
for needle in [
    'A later instance-extension count retry failed; earlier bounded partial extension evidence was retained.',
    'A later instance-layer count retry failed; earlier bounded partial layer evidence was retained.',
    'A later instance-layer extension count retry failed; earlier bounded partial extension evidence was retained.',
    'A later device-layer count retry failed; earlier bounded partial layer evidence was retained.',
    'A later device-layer extension count retry failed; earlier bounded partial extension evidence was retained.',
    'A later device-extension count retry failed; earlier bounded partial extension evidence was retained.'
]:
    if needle not in cpp:
        errors.append(f'missing 0.41.24 bounded partial-enumeration retention gate: {needle}')
for needle in [
    'val surfaceFormatEnumerationComplete: Boolean = false',
    'val surfaceFormatQuerySpecAnomaly: Boolean = false',
    'val surfacePresentModeEnumerationComplete: Boolean = false',
    'val surfacePresentModeQuerySpecAnomaly: Boolean = false',
    'surfaceFormatEnumerationComplete = surface?.optBoolean("formatEnumerationComplete", false) ?: false',
    'surfacePresentModeEnumerationComplete = surface?.optBoolean("presentModeEnumerationComplete", false) ?: false',
    'val completeEnumeration = device?.surfaceFormatEnumerationComplete == true && !device.surfaceFormatQuerySpecAnomaly',
    'put("formatEnumerationComplete", d.surfaceFormatEnumerationComplete)',
    'put("presentModeEnumerationComplete", d.surfacePresentModeEnumerationComplete)',
    'put("surface/formatEnumerationComplete", device.surfaceFormatEnumerationComplete.toString())',
    'complete=${d.surfaceFormatEnumerationComplete}, specAnomaly=${d.surfaceFormatQuerySpecAnomaly}',
    '"Format enumeration complete" to htmlEscape(d.surfaceFormatEnumerationComplete.toString())',
    'put("summaryScope", if (report.devices.size > 1) "firstPhysicalDeviceCompatibilitySummary" else "singlePhysicalDevice")',
    'put("physicalDeviceCount", report.devices.size)',
    'put("deviceApiSummaryScope", if (report.devices.size > 1) "firstPhysicalDeviceCompatibilitySummary" else "singlePhysicalDevice")'
]:
    if needle not in kt:
        errors.append(f'missing 0.41.24 Kotlin/report-parity gate: {needle}')

if '## Release 0.41.25 Android 17 native dynamic-code hardening requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.25 native dynamic-code hardening contract')
if not (root / 'rules/0.41.25_ANDROID17_NATIVE_DCL_SECURITY_AUDIT.md').is_file():
    errors.append('0.41.25 Android 17 native DCL security audit record is missing')
for needle in [
    'private fun ensureTurnipNativeLibrariesReadOnly(scan: TurnipBundleScan): Boolean',
    'safe.name.endsWith(".so", true) && !safe.setReadOnly()',
    'Unable to secure a native driver library before extraction',
    'if (!ensureTurnipNativeLibrariesReadOnly(bundleScan))',
    'if (!ensureTurnipNativeLibrariesReadOnly(scan)) return null',
    'if (library.canWrite()) return null',
    'private suspend fun installDriverBundleIo(uri: Uri)',
    'withContext(Dispatchers.IO) { probeMutex.withLock { installDriverBundleIo(uri) } }',
    'importContext.ensureActive()',
    'if (driverImportInFlight) return',
    'driverPickerInFlight || driverImportInFlight',
    'output.fd.sync()'
]:
    if needle not in kt:
        errors.append(f'missing 0.41.25 Android 17 native-code hardening gate: {needle}')

if '## Release 0.41.26 export-snapshot, driver-mutation and UI-thread correctness requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.26 export/driver/UI-thread correctness contract')
if not (root / 'rules/0.41.26_EXPORT_SNAPSHOT_DRIVER_MUTATION_AUDIT.md').is_file():
    errors.append('0.41.26 export/driver mutation audit record is missing')
for needle in [
    'import androidx.compose.runtime.saveable.rememberSaveable',
    'private data class ExportSnapshot(val filename: String, val path: String, val mime: String)',
    'withContext(Dispatchers.IO) { createExportSnapshot(context, filename, mime, contentFactory) }',
    'var pendingExportPath by rememberSaveable { mutableStateOf("") }',
    'input.copyTo(output, 64 * 1024)',
    'MediaStore.Downloads.IS_PENDING, 1',
    'runCatching { context.contentResolver.delete(uri, null, null) }',
    'val driverPaths = withContext(Dispatchers.IO)',
    'bundleInstalled = withContext(Dispatchers.IO) { resolveInstalledTurnipLibrary(context.filesDir) != null }',
    'private fun completeReportMutationReady(): Boolean',
    'driverPickerInFlight = true',
    'driverPickerInFlight = false',
    'if (driverPickerInFlight || driverImportInFlight)',
    'if (driverImportInFlight) {\n            collectionPending = true\n            return\n        }',
    'activateImportedTurnipDriver()',
    'applyDriverModeChange(DriverMode.TURNIP, true)'
]:
    if needle not in kt:
        errors.append(f'missing 0.41.26 export/driver/UI-thread gate: {needle}')
for forbidden in [
    'private data class ExportPayload(',
    'private fun writeExportToDownloads(',
    'private fun writeExport(context: Context, uri: Uri, content: String',
    'remember(mode, turnipSupport, collectionStatus) { resolveInstalledTurnipLibrary(context.filesDir) != null }',
    '.putExtra(VulkanProbeService.EXTRA_DRIVER_ICD, findTurnipIcd(modeSnapshot))'
]:
    if forbidden in kt:
        errors.append(f'0.41.26 stale blocking/lossy pattern remains: {forbidden}')
proguard = (root / 'app/proguard-rules.pro').read_text(encoding='utf-8')
if '-keep class com.efishell.vulkanscope.MainActivity { *; }' in proguard:
    errors.append('0.41.26 broad MainActivity R8 keep rule remains')
if '-keepclasseswithmembernames,includedescriptorclasses class com.efishell.vulkanscope.VulkanProbeService {' not in proguard or 'native <methods>;' not in proguard:
    errors.append('0.41.26 VulkanProbeService static-JNI R8 protection is missing')


if '## Release 0.41.27 checkpoint, incomplete-enumeration and bounded-driver-scan correctness requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.27 checkpoint/enumeration/driver-scan correctness contract')
if not (root / 'rules/0.41.27_CHECKPOINT_ENUMERATION_DRIVER_SCAN_AUDIT.md').is_file():
    errors.append('0.41.27 checkpoint/enumeration/driver-scan audit record is missing')
for needle in [
    'var lastObservedInode = -1L',
    'android.system.Os.stat(resultFile.path).st_ino',
    'resultLength != lastObservedLength || resultModified != lastObservedModified || resultInode != lastObservedInode',
    'private fun scanTurnipBundle(root: File, maxEntries: Int): TurnipBundleScan?',
    'if (++visitedEntries > maxEntries) return null',
    'val children = runCatching { directory.listFiles() }.getOrNull() ?: return null',
    'canonicalEntry.path != entry.absoluteFile.path',
    'if (!seenCanonicalPaths.add(canonicalEntry.path)) return null',
    'val bundleScan = scanTurnipBundle(tempDir, maxEntries)',
    'val scan = scanTurnipBundle(File(filesDir, "turnip"), 2048)',
    '@Volatile private var driverImportInFlight = false',
    'withContext(Dispatchers.IO) { probeMutex.withLock { installDriverBundleIo(uri) } }',
    'if (driverImportInFlight) return@withLock unavailableProbe',
    'markAdvancedIncomplete("vkGetPhysicalDeviceToolProperties count query returned VK_INCOMPLETE',
    'markAdvancedIncomplete("vkGetPhysicalDeviceToolProperties data query returned VK_INCOMPLETE',
    'markAdvancedIncomplete("vkGetPhysicalDeviceVideoFormatPropertiesKHR count query returned VK_INCOMPLETE',
    'markAdvancedIncomplete("vkGetPhysicalDeviceVideoFormatPropertiesKHR data query returned VK_INCOMPLETE',
    'markExtensionIncomplete(deviceExtensionEnumeration.reason.empty()',
    'markExtensionIncomplete("vkGetPhysicalDeviceCooperativeMatrixProperties2EXT count query returned VK_INCOMPLETE',
    'replaceAdvancedToken(advancedStatusToken, advancedEnumerationIncomplete ? "incomplete" : "available")',
    'replaceExtensionToken(extensionStatusToken, extensionQueryIncomplete ? "incomplete" : "available")'
]:
    if needle not in kt + cpp:
        errors.append(f'missing 0.41.27 checkpoint/enumeration/driver-scan gate: {needle}')
for forbidden in [
    'periodicRefreshDue',
    'walkTopDown().filter { it.isFile && it.name.endsWith(".so", true) }.take(maxEntries + 1)',
    'root.walkTopDown().filter { it.isFile && it.name.equals("meta.json", true) }',
    'root.walkTopDown().filter { it.isFile && it.name == declared }',
    'tempDir.walkTopDown().filter { it.isFile && it.name.equals("meta.json", true) }',
    'tempDir.walkTopDown().filter { it.isFile && it.name == declared }'
]:
    if forbidden in kt:
        errors.append(f'0.41.27 stale unbounded/polling pattern remains: {forbidden}')

if '## Release 0.41.28 simple-feature completeness and isolated-crash fast-fail requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.28 simple-feature/crash correctness contract')
if not (root / 'rules/0.41.28_SIMPLE_FEATURE_CRASH_AUDIT.md').is_file():
    errors.append('0.41.28 simple-feature/crash audit record is missing')
simple_start = cpp.find('std::string collectVulkanSimpleFeatureGroup(')
simple_end = cpp.find('std::string collectVulkanSelfTest(', simple_start)
if simple_start < 0 or simple_end <= simple_start:
    errors.append('0.41.28 simple-feature collector scope could not be isolated')
    simple_cpp = ''
else:
    simple_cpp = cpp[simple_start:simple_end]
for needle in [
    'bool simpleFeatureIncomplete = !physicalDeviceEnumerationComplete;',
    'markSimpleFeatureIncomplete(extensionEnumeration.reason.empty()',
    'replaceSimpleFeatureToken(simpleFeatureStatusToken, simpleFeatureIncomplete ? "incomplete" : "available")',
    'replaceSimpleFeatureToken(simpleFeatureReasonToken, simpleFeatureReason)'
]:
    if needle not in simple_cpp:
        errors.append(f'missing 0.41.28 scoped simple-feature completeness gate: {needle}')
for forbidden in [
    'jsonString(physicalDeviceEnumerationComplete ? "available" : "incomplete")'
]:
    if forbidden in simple_cpp:
        errors.append(f'0.41.28 stale simple-feature root-status pattern remains: {forbidden}')
for needle in [
    'if (crashDetected()) {',
    'if (group == "base") {',
    'value = unavailable("The dedicated Vulkan $group probe terminated after a native signal before publishing a valid result.")'
]:
    if needle not in kt:
        errors.append(f'missing 0.41.28 isolated-crash fast-fail gate: {needle}')
if 'if (crashDetected() && group == "base")' in kt:
    errors.append('0.41.28 stale base-only crash-marker gate remains')


if '## Release 0.41.29 base-completeness, extension-state and Turnip traversal requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.29 base/extension/Turnip correctness contract')
if not (root / 'rules/0.41.29_BASE_COMPLETENESS_EXTENSION_TURNIP_AUDIT.md').is_file():
    errors.append('0.41.29 base/extension/Turnip audit record is missing')
base_start = cpp.find('std::string collect(jobject surfaceObject')
base_end = cpp.find('std::string collectVulkanMetadata(', base_start)
base_cpp = cpp[base_start:base_end] if base_start >= 0 and base_end > base_start else ''
for needle in [
    'bool allDeviceExtensionEnumerationsComplete = true;',
    'allDeviceExtensionEnumerationsComplete = false;',
    'const bool baseReportComplete = deviceEnumerationComplete && allDeviceExtensionEnumerationsComplete;',
    'const std::string deviceExtensionCompleteness = std::string(',
    'jsonBool(baseReportComplete)'
]:
    if needle not in base_cpp:
        errors.append(f'missing 0.41.29 native base-completeness gate: {needle}')
coverage_start = kt.find('private fun hasCompleteBaseCoverage(report: VulkanReport): Boolean =')
coverage_end = kt.find('private suspend fun enrichReport(', coverage_start)
coverage_kt = kt[coverage_start:coverage_end] if coverage_start >= 0 and coverage_end > coverage_start else ''
if 'device.deviceExtensionStatus == "available"' not in coverage_kt:
    errors.append('0.41.29 scoped Kotlin base coverage does not require complete device-extension enumeration')
ext_start = cpp.find('std::string collectVulkanExtensionGroup(')
ext_end = cpp.find('std::string collectVulkanAdvancedGroup(', ext_start)
ext_cpp = cpp[ext_start:ext_end] if ext_start >= 0 and ext_end > ext_start else ''
for needle in [
    'bool extensionEnumerationIncomplete = false;',
    'std::strcmp(deviceExtensionEnumeration.status, "incomplete") == 0',
    'if (!matchedAny && extensionEnumerationIncomplete)',
    'Device-extension enumeration remained VK_INCOMPLETE'
]:
    if needle not in ext_cpp:
        errors.append(f'missing 0.41.29 extension no-match VK_INCOMPLETE gate: {needle}')
for needle in [
    'bool extensionEnumerationIncomplete=false;',
    'std::strcmp(extensionEnumeration.status, "incomplete") == 0',
    'if (extensionEnumerationIncomplete) return std::string',
    'Device-extension enumeration remained VK_INCOMPLETE'
]:
    if needle not in simple_cpp:
        errors.append(f'missing 0.41.29 simple-feature no-match VK_INCOMPLETE gate: {needle}')
advanced_start = cpp.find('std::string collectVulkanAdvancedGroup(')
advanced_end = cpp.find('std::string collectVulkanSimpleFeatureGroup(', advanced_start)
advanced_cpp = cpp[advanced_start:advanced_end] if advanced_start >= 0 and advanced_end > advanced_start else ''
dependency_start = advanced_cpp.find('const bool advancedDependsOnDeviceExtensions = group && (')
dependency_end = advanced_cpp.find('const std::string advancedStatusToken', dependency_start)
dependency_cpp = advanced_cpp[dependency_start:dependency_end] if dependency_start >= 0 and dependency_end > dependency_start else ''
for needle in [
    'const bool advancedDependsOnDeviceExtensions = group && (',
    'std::strcmp(group, "queue2") == 0',
    'std::strcmp(group, "format2") == 0',
    'std::strcmp(group, "imageFormat2") == 0',
    'std::strcmp(group, "external") == 0',
    'std::strcmp(group, "sparse") == 0',
    'std::strcmp(group, "memory2") == 0',
    'std::strcmp(group, "videoCapabilities") == 0'
]:
    if needle not in dependency_cpp:
        errors.append(f'missing 0.41.29 scoped advanced extension-dependency predicate: {needle}')
for needle in [
    'if (advancedDependsOnDeviceExtensions && std::strcmp(deviceExtensionEnumeration.status, "available") != 0)',
    'markAdvancedIncomplete(deviceExtensionEnumeration.reason.empty()'
]:
    if needle not in advanced_cpp:
        errors.append(f'missing 0.41.29 advanced extension-dependency completeness gate: {needle}')
for needle in [
    'val directories = java.util.ArrayDeque<File>()',
    'val children = runCatching { directory.listFiles() }.getOrNull() ?: return null',
    'if (++visitedEntries > maxEntries) return null',
    'canonicalEntry.path != entry.absoluteFile.path',
    'entry.isDirectory -> directories.add(canonicalEntry)',
    'turnip_import_${java.util.UUID.randomUUID()}',
    'turnip_backup_${java.util.UUID.randomUUID()}',
    'if (!tempDir.mkdir()) throw IllegalStateException'
]:
    if needle not in kt:
        errors.append(f'missing 0.41.29 streaming/unique Turnip transaction gate: {needle}')
for forbidden in [
    'canonicalRoot.walkTopDown().drop(1).take(maxEntries + 1).toList()',
    'turnip_import_${System.currentTimeMillis()}',
    'turnip_backup_${System.currentTimeMillis()}'
]:
    if forbidden in kt + cpp:
        errors.append(f'0.41.29 stale completeness/Turnip pattern remains: {forbidden}')


if '## Release 0.41.30 spec-lock, regression-contract and reproducible-quality-gate requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.30 spec-lock/regression quality-gate contract')
if not (root / 'rules/0.41.30_SPEC_LOCK_REGRESSION_QUALITY_GATE_AUDIT.md').is_file():
    errors.append('0.41.30 spec-lock/regression quality-gate audit record is missing')
workflow_path = root / '.github/workflows/vulkanscope-quality.yml'
if not workflow_path.is_file():
    errors.append('0.41.30 CI quality-gate workflow is missing')
else:
    workflow = workflow_path.read_text(encoding='utf-8')
    for needle in ['tools/quality_gate.py --strict-upstream --fetch-locked-upstream', 'lintRelease testReleaseUnitTest assembleRelease', 'actions/checkout@v7', 'android-actions/setup-android@v4']:
        if needle not in workflow:
            errors.append(f'0.41.30 CI quality-gate requirement missing: {needle}')
quality_gate_text = quality_gate_path.read_text(encoding='utf-8') if quality_gate_path.is_file() else ''
for needle in ['tools/verify_canonical_vulkan_headers.py', 'tools/verify_registry_catalog.py', 'tools/verify_extension_field_coverage.py', 'tools/verify_upstream_registry.py', 'tools/verify_spec_regressions.py', '--require-complete-extension-coverage']:
    if needle not in quality_gate_text:
        errors.append(f'0.41.31 strict quality gate omits: {needle}')
if '## Release 0.41.31 spec-correctness, provisional-registry and allowlisted-regression requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.31 spec/provisional/regression contract')
if not (root / 'rules/0.41.31_FULL_SPEC_REPORT_SECURITY_MEMORY_AUDIT.md').is_file():
    errors.append('0.41.31 full audit record is missing')
for token in [
    'VK_KHR_maintenance5',
    'VK_IMAGE_LAYOUT_TENSOR_ALIASING_ARM',
    'presentation engine interprets components as XYZ',
    'Legacy Vulkan Dolby Vision color-space enum · does not signal Dolby Vision metadata'
]:
    if token not in cpp:
        errors.append(f'0.41.31 targeted Vulkan spec correction missing: {token}')
if '#define VK_ENABLE_BETA_EXTENSIONS 1' not in cpp:
    errors.append('0.41.31 provisional extension coverage requires explicit beta-header mode')
if '## Release 0.41.32 Vulkan 1.4.361, registry-census, concurrency and resource-budget requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.32 Vulkan 1.4.361/regression/resource contract')
if audit_361_path.is_file():
    audit_361 = audit_361_path.read_text(encoding='utf-8')
    for token in ['Vulkan 1.4.361', '474', '302', '64 MiB', 'expected FAIL']:
        if token not in audit_361:
            errors.append(f'0.41.32 audit evidence missing: {token}')
if '## Release 0.41.33 Vulkan 1.4.361 CMake registry-lock build-regression requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.33 CMake registry-lock build-regression contract')
if audit_4133_path.is_file():
    audit_4133 = audit_4133_path.read_text(encoding='utf-8')
    for token in ['Vulkan-Headers', 'Vulkan-Docs', 'byte-for-byte', '31386378257ac8653ce5b32c93baec385259ebbe', '3ff4984b841932e04eebeb4ce2a6613ebd37c00ffb2e96549785b2c5d7da9e1d']:
        if token not in audit_4133:
            errors.append(f'0.41.33 audit evidence missing: {token}')
if cmake_registry_lock_path.is_file() and not args.skip_nested_verifiers:
    result = subprocess.run([sys.executable, str(cmake_registry_lock_path)], cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    if result.returncode != 0:
        errors.append('0.41.33 CMake registry-lock verifier failed: ' + result.stdout.strip().replace('\n', ' | '))


if '## Release 0.41.34 real release-compiler regression requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.34 release compiler regression contract')
if audit_4134_path.is_file():
    audit_4134 = audit_4134_path.read_text(encoding='utf-8')
    for token in ['swapchainColorspaceAvailable', 'surfaceExtensionAvailable', 'androidSurfaceExtensionAvailable', 'instanceExts', 'unused hasExt', 'Unresolved reference', 'Device layer enumeration']:
        if token not in audit_4134:
            errors.append(f'0.41.34 audit evidence missing: {token}')
if compile_regression_path.is_file() and not args.skip_nested_verifiers:
    result = subprocess.run([sys.executable, str(compile_regression_path)], cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    if result.returncode != 0:
        errors.append('0.41.34 compile-regression verifier failed: ' + result.stdout.strip().replace('\n', ' | '))


if '## Release 0.41.35 Turnip post-checkpoint lifecycle and bounded-detail-collection requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.35 Turnip lifecycle/bounded-detail contract')
if audit_4135_path.is_file():
    audit_4135 = audit_4135_path.read_text(encoding='utf-8')
    for token in ['base report complete checkpoint published before optional metadata', '176 device extensions', 'one-shot', '60000 ms', 'timeout']:
        if token not in audit_4135:
            errors.append(f'0.41.35 audit evidence missing: {token}')
if probe_lifecycle_regression_path.is_file() and not args.skip_nested_verifiers:
    result = subprocess.run([sys.executable, str(probe_lifecycle_regression_path)], cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    if result.returncode != 0:
        errors.append('0.41.35 probe-lifecycle verifier failed: ' + result.stdout.strip().replace('\n', ' | '))

if '## Mandatory evidence workflow for every future change' not in rules_text:
    errors.append('PROJECT_RULES is missing the mandatory evidence workflow')
for token in ['immutable predecessor ZIP', 'Behavioral or state-machine evidence outranks source-pattern checks', 'negative mutation', 'false-positive control', 'NOT EXECUTED', 'Release packaging is itself a gate']:
    if token not in rules_text:
        errors.append(f'PROJECT_RULES mandatory evidence workflow missing: {token}')
if '## Release 0.41.36 terminal probe-publication handshake and evidence-workflow requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.36 terminal publication handshake contract')
if audit_4136_path.is_file():
    audit_4136 = audit_4136_path.read_text(encoding='utf-8')
    for token in ['c3fdfcf718fddf53cac0dd98f5886436df2f5392d8d9b75e171d03a082f3f28f', 'Process.killProcess(Process.myPid())', 'base report complete checkpoint published before optional metadata', 'terminal marker', 'failing-before-fix', 'false-positive', 'DEVICE RUNTIME', 'NOT EXECUTED']:
        if token not in audit_4136:
            errors.append(f'0.41.36 audit evidence missing: {token}')
for verifier_path, label in [(probe_publication_state_machine_path, 'state-machine'), (probe_publication_handshake_path, 'handshake'), (package_reproducibility_path, 'package-manifest')]:
    if verifier_path.is_file() and not args.skip_nested_verifiers:
        result = subprocess.run([sys.executable, str(verifier_path)], cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        if result.returncode != 0:
            errors.append(f'0.41.36 {label} verifier failed: ' + result.stdout.strip().replace('\n', ' | '))

if '## Release 0.41.37 pre-return terminal publication, one-shot process isolation and bounded base-timeout requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.37 pre-return publication/process-isolation/timeout contract')
if audit_4137_path.is_file():
    audit_4137 = audit_4137_path.read_text(encoding='utf-8')
    for token in ['2c3a9a74929349d8de70949b776777308ea4a5e89d8386a594e499851842b52d', 'pre-return terminal sidecar', 'process-owned', 'stale-process barrier', '20 seconds', 'failing-before-fix', 'false-positive', 'NonCancellable', 'CancellationException', 'Device runtime / original failure reproduction', 'NOT EXECUTED']:
        if token not in audit_4137:
            errors.append(f'0.41.37 audit evidence missing: {token}')
for verifier_path, label in [(probe_timeout_state_machine_path, 'timeout-state-machine'), (probe_timeout_recovery_path, 'timeout-recovery'), (probe_cancellation_state_machine_path, 'cancellation-state-machine'), (probe_cancellation_recovery_path, 'cancellation-recovery')]:
    if verifier_path.is_file() and not args.skip_nested_verifiers:
        result = subprocess.run([sys.executable, str(verifier_path)], cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        if result.returncode != 0:
            errors.append(f'0.41.37 {label} verifier failed: ' + result.stdout.strip().replace('\n', ' | '))


if '## Release 0.41.38 single terminal-owner and post-JNI handoff requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.38 single terminal-owner/post-JNI handoff contract')
if audit_4138_path.is_file():
    audit_4138 = audit_4138_path.read_text(encoding='utf-8')
    for token in ['99ca00817dee7d0f2a0c5ab6b99df02e56072fa2bd87876dcdb5af4586404052', 'malformed or non-terminal JSON', 'two separate SIGKILL', 'failing-before-fix', 'service-owned', 'post-JNI', 'Device runtime / original failure reproduction', 'NOT EXECUTED']:
        if token not in audit_4138:
            errors.append(f'0.41.38 audit evidence missing: {token}')
for verifier_path, label in [(probe_terminal_ownership_state_machine_path, 'terminal-ownership-state-machine'), (probe_terminal_ownership_path, 'terminal-ownership')]:
    if verifier_path.is_file() and not args.skip_nested_verifiers:
        result = subprocess.run([sys.executable, str(verifier_path)], cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        if result.returncode != 0:
            errors.append(f'0.41.38 {label} verifier failed: ' + result.stdout.strip().replace('\n', ' | '))


if '## Release 0.41.39 base terminal JSON structural-integrity requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.39 base-terminal JSON structural-integrity contract')
if audit_4139_path.is_file():
    audit_4139 = audit_4139_path.read_text(encoding='utf-8')
    for token in ['0d537fe2f4a3488503f1278eaf13e3c71eed1521db05ffac66efdba6b8a8e4f0', 'malformed or non-terminal JSON', 'Root cause', 'failing-before-fix', 'multi-device', 'process-owned', 'Device runtime / original failure reproduction', 'NOT EXECUTED']:
        if token not in audit_4139:
            errors.append(f'0.41.39 audit evidence missing: {token}')
for verifier_path, label in [(base_terminal_json_state_machine_path, 'base-terminal-json-state-machine'), (base_terminal_json_path, 'base-terminal-json')]:
    if verifier_path.is_file() and not args.skip_nested_verifiers:
        result = subprocess.run([sys.executable, str(verifier_path)], cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        if result.returncode != 0:
            errors.append(f'0.41.39 {label} verifier failed: ' + result.stdout.strip().replace('\n', ' | '))

if '## Release 0.41.40 report-field semantics, evidence-state and Database-contract requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.40 report semantics/Database contract')
if audit_4140_path.is_file():
    audit_4140 = audit_4140_path.read_text(encoding='utf-8')
    for token in ['11e94bad2605e87c1e3a484a2905843ac6a1495180d2855c6a0a7e62b8ff26d6', 'Properties emitted as Features', 'Available', '1482', 'Database HTTP 400', 'failing-before-fix']:
        if token not in audit_4140:
            errors.append(f'0.41.40 audit evidence missing: {token}')
for verifier_path, label in [(report_semantics_state_machine_path, 'report-semantics-state-machine'), (report_semantics_path, 'report-semantics')]:
    if verifier_path.is_file() and not args.skip_nested_verifiers:
        result = subprocess.run([sys.executable, str(verifier_path)], cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        if result.returncode != 0:
            errors.append(f'0.41.40 {label} verifier failed: ' + result.stdout.strip().replace('\n', ' | '))

if '## Release 0.41.41 Surface identity, binary-property and provenance requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.41 Surface/binary-property/provenance contract')
if audit_4141_path.is_file():
    audit_4141 = audit_4141_path.read_text(encoding='utf-8')
    for token in ['b85f3df2a78496be08dd8634ac94d0e97732e1e7f32524dd6e8d72ad9475deb2', 'VkResult=-1000000001', 'uint8_t', 'section-aware', 'failing-before-fix', 'Device runtime / original Surface failure reproduction after fix', 'NOT EXECUTED']:
        if token not in audit_4141:
            errors.append(f'0.41.41 audit evidence missing: {token}')
for verifier_path, label in [(report_surface_integrity_state_machine_path, 'report-surface-integrity-state-machine'), (report_surface_integrity_path, 'report-surface-integrity'), (report_surface_integrity_negative_path, 'report-surface-negative-mutations')]:
    if verifier_path.is_file() and not args.skip_nested_verifiers:
        result = subprocess.run([sys.executable, str(verifier_path)], cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        if result.returncode != 0:
            errors.append(f'0.41.41 {label} verifier failed: ' + result.stdout.strip().replace('\n', ' | '))



if '## Release 0.41.42 HTML state-presentation and registry-label requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.42 HTML presentation contract')
if audit_4142_path.is_file():
    audit_4142 = audit_4142_path.read_text(encoding='utf-8')
    for token in ['fb54101445963f65f289cadaa6acde75b575baa2958ac51732c02313707aee76', 'Available', 'Registry report schema', 'failing-before-fix', 'NOT EXECUTED']:
        if token not in audit_4142: errors.append(f'0.41.42 audit evidence missing: {token}')
for verifier_path, label in [(html_presentation_path, 'html-presentation'), (html_presentation_negative_path, 'html-presentation-negative-mutations')]:
    if verifier_path.is_file() and not args.skip_nested_verifiers:
        result = subprocess.run([sys.executable, str(verifier_path)], cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        if result.returncode != 0: errors.append(f'0.41.42 {label} verifier failed: ' + result.stdout.strip().replace('\n', ' | '))

if '## Release 0.41.43 driver-bound Surface rebind requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.43 driver/Surface rebind contract')
if audit_4143_path.is_file():
    audit_4143 = audit_4143_path.read_text(encoding='utf-8')
    for token in ['8ca0dc49c2e4eb015fe18c293c444a9bc826dc8a768f36b7058eb871ba50ee33', 'SURFACE_LIFECYCLE_FOLLOWS_ATTACHMENT', 'driver', 'Surface', 'failing-before-fix', 'NOT EXECUTED']:
        if token not in audit_4143: errors.append(f'0.41.43 audit evidence missing: {token}')
for verifier_path, label in [(driver_surface_state_machine_path, 'driver-surface-state-machine'), (driver_surface_rebind_path, 'driver-surface-rebind'), (driver_surface_negative_path, 'driver-surface-negative-mutations')]:
    if verifier_path.is_file() and not args.skip_nested_verifiers:
        result = subprocess.run([sys.executable, str(verifier_path)], cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        if result.returncode != 0: errors.append(f'0.41.43 {label} verifier failed: ' + result.stdout.strip().replace('\n', ' | '))

if '## Release 0.41.44 authoritative Vulkan Profiles evaluation requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.44 profile-evaluation contract')
if audit_4144_path.is_file():
    audit_4144 = audit_4144_path.read_text(encoding='utf-8')
    for token in ['48565d324d216f80f0278814047d19539cb32c05724411b36b55199430611ffa', '0x3F', 'Roadmap 2026', 'API-only', 'failing-before-fix', 'NOT EXECUTED']:
        if token not in audit_4144: errors.append(f'0.41.44 profile audit evidence missing: {token}')
for verifier_path, label in [(profile_state_machine_path, 'profile-state-machine'), (profile_requirements_path, 'profile-requirements'), (profile_negative_path, 'profile-negative-mutations')]:
    if verifier_path.is_file() and not args.skip_nested_verifiers:
        result = subprocess.run([sys.executable, str(verifier_path)], cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        if result.returncode != 0: errors.append(f'0.41.44 {label} verifier failed: ' + result.stdout.strip().replace('\n', ' | '))

if '## Release 0.41.45 locked Vulkan Video StdVideo registry census requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.45 Vulkan Video registry census contract')
if audit_4145_path.is_file():
    audit_4145 = audit_4145_path.read_text(encoding='utf-8')
    for token in ['01d6953384471b1191d81a6ef59d74257cc1cab133cff2e9aee47a1e766314f7', 'd018b914014c06605e367a3b929670511e6f6de2f225c405a8b5e2d912408b76', '47', 'sampled', 'failing-before-fix', 'NOT EXECUTED']:
        if token not in audit_4145: errors.append(f'0.41.45 video registry audit evidence missing: {token}')
for verifier_path, label in [(video_registry_verifier_path, 'video-registry'), (video_registry_state_machine_path, 'video-census-state-machine'), (video_registry_negative_path, 'video-registry-negative-mutations')]:
    if verifier_path.is_file() and not args.skip_nested_verifiers:
        result = subprocess.run([sys.executable, str(verifier_path)], cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        if result.returncode != 0: errors.append(f'0.41.45 {label} verifier failed: ' + result.stdout.strip().replace('\n', ' | '))

if '## Release 0.41.46 Material 3 Expressive information-architecture requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.41.46 UI information-architecture contract')
if audit_4146_path.is_file():
    audit_4146 = audit_4146_path.read_text(encoding='utf-8')
    for token in ['0c23606857404475db3b551c9d458d1c57a05ea59c2f2f6da8b0ba5c47bfc993', 'Vulkan Video', 'Analysis workspace', 'canScrollBackward', 'Libraries', 'failing-before-fix', 'NOT EXECUTED']:
        if token not in audit_4146: errors.append(f'0.41.46 UI audit evidence missing: {token}')
for verifier_path, label in [(scroll_indicator_state_machine_path, 'scroll-indicator-state-machine'), (ui_architecture_verifier_path, 'ui-information-architecture'), (ui_architecture_negative_path, 'ui-information-architecture-negative-mutations')]:
    if verifier_path.is_file() and not args.skip_nested_verifiers:
        command = [sys.executable, str(verifier_path)]
        if verifier_path == ui_architecture_verifier_path:
            command += ['--root', str(root)]
        result = subprocess.run(command, cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        if result.returncode != 0: errors.append(f'0.41.46 {label} verifier failed: ' + result.stdout.strip().replace('\n', ' | '))

if '## Release 0.80.0 full security, memory, specification and design audit requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.80.0 full hardening contract')
if audit_0800_path.is_file():
    audit_0800 = audit_0800_path.read_text(encoding='utf-8')
    for token in ['b04fd07c135182010cc2237f9a0decc753cdae42abc6aed4b10bf9149da6a235', '96 MiB', 'streaming', 'Failing-before-fix', 'Material 3 Expressive', 'NOT EXECUTED']:
        if token not in audit_0800: errors.append(f'0.80.0 full audit evidence missing: {token}')
for verifier_path, label in [(hardening_0800_verifier_path, 'full-hardening'), (hardening_0800_state_machine_path, 'full-hardening-state-machine'), (hardening_0800_negative_path, 'full-hardening-negative-mutations')]:
    if verifier_path.is_file() and not args.skip_nested_verifiers:
        command = [sys.executable, str(verifier_path)]
        if verifier_path == hardening_0800_verifier_path:
            command += ['--root', str(root)]
        result = subprocess.run(command, cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        if result.returncode != 0: errors.append(f'0.80.0 {label} verifier failed: ' + result.stdout.strip().replace('\n', ' | '))

if '## Release 0.80.1 Overview Encyclopedia requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.80.1 Overview Encyclopedia contract')
if audit_0801_path.is_file():
    audit_0801 = audit_0801_path.read_text(encoding='utf-8')
    for token in ['09ae1a26347f906203c1992c78ac61ab119d0ccbf591d99848f04b36f158ed51', 'Encyclopedia', 'Capability snapshot', 'Analysis workspace', 'failing-before-fix', 'NOT EXECUTED']:
        if token.lower() not in audit_0801.lower(): errors.append(f'0.80.1 Encyclopedia audit evidence missing: {token}')
if '## Release 0.80.2 local Vulkan encyclopedia expansion' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.80.2 detailed Encyclopedia contract')
if audit_0802_path.is_file():
    audit_0802 = audit_0802_path.read_text(encoding='utf-8')
    for token in ['dd077ac5935e6187838ac728f724a4d4fb4b8c6d7676261cc051ffcf3321b53d', '842', '6241', '2457', 'VkResult', 'failing-before-fix', 'NOT EXECUTED']:
        if token.lower() not in audit_0802.lower(): errors.append(f'0.80.2 Encyclopedia audit evidence missing: {token}')
for verifier_path, label in [(encyclopedia_0802_verifier_path, 'encyclopedia'), (encyclopedia_0802_negative_path, 'encyclopedia-negative-mutations')]:
    if verifier_path.is_file() and not args.skip_nested_verifiers:
        command = [sys.executable, str(verifier_path)]
        if verifier_path == encyclopedia_0802_verifier_path:
            command += ['--root', str(root)]
        result = subprocess.run(command, cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        if result.returncode != 0: errors.append(f'0.80.2 {label} verifier failed: ' + result.stdout.strip().replace('\n', ' | '))

if '## Release 0.80.3 separate Overview tools and crash-safe Encyclopedia search' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.80.3 Overview tools/search crash contract')
if audit_0803_path.is_file():
    audit_0803 = audit_0803_path.read_text(encoding='utf-8')
    for token in ['be11ceea517c1a36f3720ebed866624a754b8b2f8d2584bfe6db59ee477b04e5', 'StringIndexOutOfBoundsException', 'separate', 'lazy', 'Failing-before-fix', 'NOT EXECUTED']:
        if token.lower() not in audit_0803.lower(): errors.append(f'0.80.3 audit evidence missing: {token}')
for verifier_path, label in [(overview_tools_0803_verifier_path, 'overview-tools-search'), (encyclopedia_search_0803_state_machine_path, 'encyclopedia-search-state-machine'), (overview_tools_0803_negative_path, 'overview-tools-search-negative-mutations')]:
    if verifier_path.is_file() and not args.skip_nested_verifiers:
        command = [sys.executable, str(verifier_path)]
        if verifier_path == overview_tools_0803_verifier_path:
            command += ['--root', str(root)]
        result = subprocess.run(command, cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        if result.returncode != 0: errors.append(f'0.80.3 {label} verifier failed: ' + result.stdout.strip().replace('\n', ' | '))

if '## Release 0.80.4 detail affordance, scroll/TV and collection-outcome requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.80.4 detail/scroll/TV/collection contract')
if audit_0804_path.is_file():
    audit_0804 = audit_0804_path.read_text(encoding='utf-8')
    for token in ['04a1c45804d1d1ecc13f1b5cf70729d42e551313be091540f3f981cac5c73807', 'Details', 'Android TV', 'FAILED', 'ScrollState', 'NOT EXECUTED']:
        if token.lower() not in audit_0804.lower(): errors.append(f'0.80.4 audit evidence missing: {token}')
for verifier_path, label in [(detail_tv_0804_verifier_path, 'detail-tv-collection'), (detail_tv_0804_state_machine_path, 'detail-tv-collection-state-machine'), (detail_tv_0804_negative_path, 'detail-tv-collection-negative-mutations')]:
    if verifier_path.is_file() and not args.skip_nested_verifiers:
        command = [sys.executable, str(verifier_path)]
        if verifier_path == detail_tv_0804_verifier_path:
            command += ['--root', str(root)]
        result = subprocess.run(command, cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        if result.returncode != 0: errors.append(f'0.80.4 {label} verifier failed: ' + result.stdout.strip().replace('\n', ' | '))

if '## Release 0.80.5 update release-notes Android TV and design-integrity requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.80.5 update release-notes TV/design contract')
if audit_0805_path.is_file():
    audit_0805 = audit_0805_path.read_text(encoding='utf-8')
    for token in ['90dfed09e3026c923da3adb4ac888c3c1c7738fc001a8b2f6d03b8ba557802fd', 'LazyListState', 'Android TV', 'boundary', 'Material 3 Expressive', 'failing', 'NOT EXECUTED']:
        if token.lower() not in audit_0805.lower(): errors.append(f'0.80.5 audit evidence missing: {token}')
for verifier_path, label in [(update_release_0805_verifier_path, 'update-release-notes'), (update_release_0805_state_machine_path, 'update-release-notes-state-machine'), (update_release_0805_negative_path, 'update-release-notes-negative-mutations')]:
    if verifier_path.is_file() and not args.skip_nested_verifiers:
        command = [sys.executable, str(verifier_path)]
        if verifier_path == update_release_0805_verifier_path:
            command += ['--root', str(root)]
        result = subprocess.run(command, cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        if result.returncode != 0: errors.append(f'0.80.5 {label} verifier failed: ' + result.stdout.strip().replace('\n', ' | '))

if '## Release 0.80.6 TalkBack and large-text/display-size accessibility requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.80.6 accessibility/large-text contract')
if audit_0806_path.is_file():
    audit_0806 = audit_0806_path.read_text(encoding='utf-8')
    for token in ['92a2ea2e853c6b01a65e1a2076d737c8e9595b06b3db28b3ee5e86a0ae538a43', 'TalkBack', '200%', 'live-region', 'RadioButton', 'NOT EXECUTED']:
        if token.lower() not in audit_0806.lower(): errors.append(f'0.80.6 audit evidence missing: {token}')
for verifier_path, label in [(accessibility_0806_verifier_path, 'accessibility-large-text'), (accessibility_0806_state_machine_path, 'accessibility-large-text-state-machine'), (accessibility_0806_negative_path, 'accessibility-large-text-negative-mutations')]:
    if verifier_path.is_file() and not args.skip_nested_verifiers:
        command = [sys.executable, str(verifier_path)]
        if verifier_path == accessibility_0806_verifier_path:
            command += ['--root', str(root)]
        result = subprocess.run(command, cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        if result.returncode != 0: errors.append(f'0.80.6 {label} verifier failed: ' + result.stdout.strip().replace('\n', ' | '))

if '## Release 0.80.7 system-language, bidi, system-font and update-information requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.80.7 system-language/font/update-info contract')
if audit_0807_path.is_file():
    audit_0807 = audit_0807_path.read_text(encoding='utf-8')
    for token in ['8cb67397f76ba5a1a1ada0c5d8f1046895161d754a9a2f5654da4ce52b9f9b69', 'ContentOrLtr', 'system font', 'Arabic', 'blue', 'ic_info', 'NOT EXECUTED']:
        if token.lower() not in audit_0807.lower(): errors.append(f'0.80.7 audit evidence missing: {token}')
for verifier_path, label in [(system_language_0807_verifier_path, 'system-language-font-update-info'), (system_language_0807_state_machine_path, 'system-language-font-update-info-state-machine'), (system_language_0807_negative_path, 'system-language-font-update-info-negative-mutations')]:
    if verifier_path.is_file() and not args.skip_nested_verifiers:
        command = [sys.executable, str(verifier_path)]
        if verifier_path == system_language_0807_verifier_path:
            command += ['--root', str(root)]
        result = subprocess.run(command, cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        if result.returncode != 0: errors.append(f'0.80.7 {label} verifier failed: ' + result.stdout.strip().replace('\n', ' | '))

if '## Release 0.80.8 final collection-status, AGP and Database companion requirements' not in rules_text:
    errors.append('PROJECT_RULES is missing 0.80.8 final release contract')
if audit_0808_path.is_file():
    audit_0808 = audit_0808_path.read_text(encoding='utf-8')
    for token in ['7fdbca8843a581be4b7b6d64917716e01e4273bc9871e20fdaf8c0578c1ed8a0', 'AGP 9.4.0', 'persistent', '0.39.22', '0.80.1']:
        if token.lower() not in audit_0808.lower(): errors.append(f'0.80.8 audit evidence missing: {token}')
for verifier_path, label in [(final_0808_verifier_path, 'final-release'), (final_0808_state_machine_path, 'final-release-state-machine'), (final_0808_negative_path, 'final-release-negative-mutations')]:
    if verifier_path.is_file() and not args.skip_nested_verifiers:
        command = [sys.executable, str(verifier_path)]
        if verifier_path == final_0808_verifier_path:
            command += ['--root', str(root)]
        result = subprocess.run(command, cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        if result.returncode != 0: errors.append(f'0.80.8 {label} verifier failed: ' + result.stdout.strip().replace('\n', ' | '))

if not args.skip_regression_contracts and not args.skip_nested_verifiers and regression_verifier_path.is_file():
    result = subprocess.run([sys.executable, str(regression_verifier_path)], cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    if result.returncode != 0:
        errors.append('0.80.8 regression-contract verifier failed: ' + result.stdout.strip().replace('\n', ' | '))



if errors:
    for error in errors: print(f'FAIL: {error}')
    raise SystemExit(1)
print('VulkanScope release verification: PASS')

print(f'version={version.group(1)} code={code.group(1)} baseline=Vulkan 1.4.362 schema=6 compileHeaders=ee2ec5fd83dafce291024683b50dc89219333076')
