#!/usr/bin/env python3
import argparse
import re
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', type=Path, default=Path(__file__).resolve().parents[1])
parser.add_argument('--skip-version', action='store_true')
parser.add_argument('--skip-release-records', action='store_true')
args = parser.parse_args()
root = args.root.resolve()
errors = []
main_path = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
gradle_path = root / 'app/build.gradle.kts'
manifest_path = root / 'app/src/main/AndroidManifest.xml'
quality_path = root / 'tools/quality_gate.py'
rules_path = root / 'rules/PROJECT_RULES.md'
audit_path = root / 'rules/1.0.15_SAF_LAUNCH_FIRST_FULL_AUDIT.md'
for path in [main_path, gradle_path, manifest_path, quality_path]:
    if not path.is_file(): errors.append(f'missing required file: {path.relative_to(root)}')
if errors:
    [print('FAIL:', x) for x in errors]
    raise SystemExit(1)
main = main_path.read_text(encoding='utf-8')
gradle = gradle_path.read_text(encoding='utf-8')
manifest = manifest_path.read_text(encoding='utf-8')
quality = quality_path.read_text(encoding='utf-8')

def need(cond, msg):
    if not cond: errors.append(msg)

def block(start, end):
    a = main.find(start); b = main.find(end, a + len(start)) if a >= 0 else -1
    need(a >= 0 and b > a, f'block missing: {start}')
    return main[a:b] if a >= 0 and b > a else ''

version_match = re.search(r'versionName\s*=\s*"(\d+)\.(\d+)\.(\d+)"', gradle)
code_match = re.search(r'versionCode\s*=\s*(\d+)', gradle)
current_version = tuple(map(int, version_match.groups())) if version_match else (0, 0, 0)
if not args.skip_version:
    expected = current_version[0] * 1000 + current_version[1] * 100 + current_version[2]
    need(version_match is not None and code_match is not None and current_version >= (1, 0, 15) and int(code_match.group(1)) == expected, 'retained 1.0.15+ semantic release identity missing')
need('kBaseline = "Vulkan 1.4.362"' in (root / 'app/src/main/cpp/registry_query_catalog.h').read_text(encoding='utf-8'), 'Vulkan 1.4.362 baseline drifted')
need('android.permission.MANAGE_EXTERNAL_STORAGE' not in manifest and 'android.permission.MANAGE_EXTERNAL_STORAGE' not in main, 'all-files access is forbidden')
for forbidden in ['private fun documentPickerAvailable(', 'private fun analysisDocumentProviderAvailable(', 'private fun turnipSafPickerAvailable()', 'PackageManager.MATCH_DEFAULT_ONLY', 'ResolveInfoFlags.of']:
    need(forbidden not in main, f'resolver-preflight contract survived: {forbidden}')
helper = block('private inline fun tryLaunchSystemDocumentPicker(', 'private enum class FallbackAnalysisImportKind')
for token in ['launch()', 'catch (error: ActivityNotFoundException)', 'catch (error: SecurityException)', 'return try']:
    need(token in helper, f'launch-first helper missing: {token}')
for forbidden in ['catch (error: Throwable)', 'catch (error: Exception)', 'runCatching']:
    need(forbidden not in helper, f'picker helper swallows broad failure: {forbidden}')
need('if (isTvDevice(context)) return false' not in main, 'TV is still guessed to have no system picker')
turnip = block('private fun openDriverBundlePicker()', 'private fun queryTurnipSourceInfo')
need('tryLaunchSystemDocumentPicker { driverPickerLauncher.launch(' in turnip, 'Turnip does not launch the system picker first')
need('openFallbackTurnipImportDialog()' in turnip, 'Turnip launch failure fallback missing')
model = block('private fun rememberAnalysisWorkspaceModel(', 'private fun historyLabel')
for token in [
    'tryLaunchSystemDocumentPicker { importLauncher.launch(',
    'tryLaunchSystemDocumentPicker { exportLauncher.launch(',
    'tryLaunchSystemDocumentPicker { minimumImportLauncher.launch(',
    'tryLaunchSystemDocumentPicker { minimumExportLauncher.launch(',
    'tryLaunchSystemDocumentPicker { rawExportLauncher.launch(',
    'openFallbackImport(FallbackAnalysisImportKind.SNAPSHOT)',
    'openFallbackImport(FallbackAnalysisImportKind.MINIMUM)',
    'exportSnapshotFallback()', 'exportMinimumFallback()', 'exportRawFallback()'
]: need(token in model, f'Analysis launch-first/fallback behavior missing: {token}')
report = block('private fun launchExportPickerOrFallback(', 'private fun reportGpuSummary')
need('tryLaunchSystemDocumentPicker { launcher.launch(snapshot.filename) }' in report and 'onDownloadsFallback(snapshot)' in report, 'complete report launch-first Downloads fallback missing')
for token in [
    'turnip_01.zip through turnip_10.zip',
    'The system document picker could not be opened.',
    'Document picker fallback · analysis snapshot saved to ${it.absolutePath}',
    'Document picker fallback · minimum profile saved to ${it.absolutePath}',
    'Document picker fallback · technicalReport JSON saved to ${it.absolutePath}'
]: need(token in main, f'truthful/bounded fallback requirement missing: {token}')
need('SAF unavailable' not in main and "Storage Access Framework is unavailable" not in main, 'UI still makes a false global SAF-unavailable claim')
helpers = block('private enum class FallbackAnalysisImportKind', 'private fun validateAnalysisSnapshot')
for token in ['.take(64)', 'getExternalFilesDirs(Environment.DIRECTORY_DOCUMENTS)', 'getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS)', 'File(context.filesDir, "analysis_exchange")', 'canonical.parentFile != root']:
    need(token in helpers, f'bounded Analysis fallback guard missing: {token}')
for forbidden in ['Environment.getExternalStorageDirectory()', 'File("/storage/emulated/0")']:
    need(forbidden not in main, f'broad storage traversal introduced: {forbidden}')
for name in ['verify_fallback_import_compile_intro_1014.py','test_fallback_import_compile_intro_1014_state_machine.py','test_fallback_import_compile_intro_1014_negative_mutations.py','verify_saf_launch_first_full_audit_1015.py','test_saf_launch_first_full_audit_1015_state_machine.py','test_saf_launch_first_full_audit_1015_negative_mutations.py']:
    need(name in quality, f'aggregate quality gate omits release-specific suite: {name}')
if not args.skip_release_records:
    need(rules_path.is_file() and '## Release 1.0.15 system-document-picker launch-first and full-audit requirements' in rules_path.read_text(encoding='utf-8'), 'PROJECT_RULES 1.0.15 contract missing')
    need(audit_path.is_file(), '1.0.15 audit record missing')
    need((root / 'tests/golden/1.0.14_regression_contract.json').is_file(), '1.0.14 immutable regression contract missing')
if errors:
    [print('FAIL:', x) for x in errors]
    raise SystemExit(1)
print('PASS VulkanScope 1.0.15 system-document-picker launch-first / full-audit contract')
