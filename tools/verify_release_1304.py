#!/usr/bin/env python3
import argparse
import hashlib
import json
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root')
parser.add_argument('--skip-version', action='store_true')
args = parser.parse_args()
root = Path(args.root).resolve() if args.root else Path(__file__).resolve().parents[1]
main_path = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
manifest_path = root / 'app/src/main/AndroidManifest.xml'
gradle_path = root / 'app/build.gradle.kts'
rules_path = root / 'rules/PROJECT_RULES.md'
contract_path = root / 'tests/golden/1.3.3_file_manager_contract.json'
main = main_path.read_text(encoding='utf-8')
manifest = manifest_path.read_text(encoding='utf-8')
gradle = gradle_path.read_text(encoding='utf-8')
rules = rules_path.read_text(encoding='utf-8')
contract = json.loads(contract_path.read_text(encoding='utf-8'))
errors = []

def need(condition, message):
    if not condition:
        errors.append(message)

def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()

if not args.skip_version:
    need('versionCode = 1304' in gradle and 'versionName = "1.3.4"' in gradle, '1.3.4 release identity missing')

def source_block(text, start, end):
    a = text.find(start)
    b = text.find(end, a + len(start)) if a >= 0 else -1
    return text[a:b] if a >= 0 and b > a else ''

inspect_block = source_block(main, 'private fun inspectTurnipArchive(file: File): TurnipArchiveCandidate?', 'private fun scanTurnipFileManagerDirectory(root: File, directory: File)')
need(bool(inspect_block), 'Turnip pre-validation block could not be isolated')

for token in ['ActivityResultContracts.OpenDocument', 'ActivityResultContracts.CreateDocument', 'Intent.ACTION_OPEN_DOCUMENT', 'Intent.ACTION_CREATE_DOCUMENT', 'takePersistableUriPermission']:
    need(token not in main, f'SAF route reintroduced: {token}')

need(manifest.count('android.permission.MANAGE_EXTERNAL_STORAGE') == 1, 'all-files special access manifest declaration missing or duplicated')
need('android.permission.READ_EXTERNAL_STORAGE' not in manifest, 'legacy READ_EXTERNAL_STORAGE must remain absent')
need('android.permission.WRITE_EXTERNAL_STORAGE' not in manifest, 'legacy WRITE_EXTERNAL_STORAGE must remain absent')
need('Environment.isExternalStorageManager()' in main, 'all-files access state check missing')
need('Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION' in main, 'app-specific all-files settings action missing')
need('Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION' in main, 'guarded system settings fallback missing')
need('Uri.parse("package:$packageName")' in main, 'app-specific settings package URI missing')
need('awaitingAllFilesAccessReturn' in main and 'showStoragePermissionDeniedFeedback()' in main, 'permission return/denial state missing')
need('delay(3_000L)' in main and 'storagePermissionDeniedFeedback' in main, 'three-second permission-denied feedback missing')
need('title = if (denied) "Permission denied" else "Import driver ZIP"' in main and 'R.drawable.ic_close' in main, 'Import action X/permission-denied presentation missing')

for token in [
    'private fun inspectTurnipArchive(file: File): TurnipArchiveCandidate?',
    'private fun scanTurnipFileManagerDirectory(root: File, directory: File)',
    'private fun TurnipFileManagerDialog(',
    'private fun TurnipFileManagerFolderRow(',
    'private fun TurnipFileManagerCandidateRow(',
    'private fun TurnipArchiveCandidateDetailsDialog(',
]:
    need(token in main, f'file-manager component missing: {token}')

for token in [
    'if (++entryCount > 2048)',
    'fileBytes > 32L * 1024L * 1024L',
    'totalBytes > 64L * 1024L * 1024L',
    'compressedBytes > TURNIP_ARCHIVE_INPUT_MAX_BYTES',
    'if (metadataCount != 1)',
    'if (schemaVersion != 1)',
    '!libraryName.endsWith(".so", true)',
    '!libraryName.contains("vulkan", true)',
    'if (libraries.size != 1 || libraries.single().second <= 0L)',
    'rawName.startsWith(\'/\')',
    'segments.any { it == "." || it == ".." }',
    'if (!seen.add(normalized))',
]:
    need(token in inspect_block, f'pre-validation security contract missing: {token}')

need('children.size >= 4096' in main and 'inspectedZipCount >= 256' in main and 'Files.newDirectoryStream' in main, 'bounded directory/candidate scan missing')
need('withContext(Dispatchers.IO) { scanTurnipFileManagerDirectory(root, target) }' in main, 'directory scan is not dispatched to IO')
need('withContext(Dispatchers.IO) { inspectTurnipArchive(File(path)) }' in main, 'pre-import revalidation is not dispatched to IO')
need('withContext(Dispatchers.IO) { installDriverBundleIo(sourceInfo) { FileInputStream(candidate.path) } }' in main, 'existing importer is not reused on IO')
need('val canonicalRoot = root.canonicalFile' in main and 'canonicalDirectory != canonicalRoot && !canonicalDirectory.path.startsWith(prefix)' in main, 'canonical root confinement missing')
need('file.absoluteFile.path != canonicalFile.path' in main and 'child.absoluteFile.path != canonical.path' in main, 'symlink/canonical alias rejection missing')

need('val remaining = (TURNIP_MANAGER_MAX_DRIVERS - occupied).coerceAtLeast(0)' in main, 'remaining-slot capacity derivation missing')
need('if (selected.size >= state.maxSelectable)' in main, 'selection limit enforcement missing')
need('selectedPaths.size <= state.maxSelectable' in main, 'Import selection bound missing')
need('Checkbox(' in main, 'validated ZIP checkbox missing')
folder_start = main.find('private fun TurnipFileManagerFolderRow(')
folder_end = main.find('private fun TurnipFileManagerCandidateRow(', folder_start)
need(folder_start >= 0 and folder_end > folder_start and 'Checkbox(' not in main[folder_start:folder_end], 'folders must not expose selection checkboxes')
need('painterResource(R.drawable.ic_folder)' in main and 'ComposeColor(0xFFFFC857)' in main, 'yellow folder presentation missing')
need(main.count('painterResource(R.drawable.mesa3d_logo)') >= 2, 'Mesa logo missing from package row/details')
need('ComposeColor(0xFF66E58A)' in main, 'validated Turnip green presentation missing')
need('TurnipFileManagerViewMode.LIST' in main and 'TurnipFileManagerViewMode.COMPACT' in main, 'List/Compact view modes missing')
need('Search folders and Turnip packages' in main, 'current-folder search missing')
need('formatTimestampOrUnavailable(candidate.modifiedAtMillis' in main, 'modified timestamp presentation missing')
need('Other files are hidden. ZIP validation is repeated during import.' in main, 'hidden ordinary-file/revalidation disclosure missing')
need((root / 'app/src/main/res/drawable/ic_folder.xml').is_file(), 'semantic folder vector missing')
need((root / 'app/src/main/res/drawable-nodpi/mesa3d_logo.webp').is_file(), 'existing Mesa asset missing')

need('private suspend fun installDriverBundleIo(sourceInfo: TurnipSourceInfo, openInput: () -> InputStream?): Int' in main, 'existing Turnip importer missing')
need('Driver bundle contains too many entries' in main and 'Driver metadata must resolve to exactly one Vulkan library' in main, 'existing importer security contract drifted')
for text in [
    'Import unavailable · SAF and fallback paths were removed in 1.3.3',
    'Export unavailable · SAF and fallback paths were removed in 1.3.3',
    'Unavailable in 1.3.3 · SAF and Downloads fallback removed',
]:
    need(text in main, f'non-Turnip storage surface changed outside 1.3.4 scope: {text}')

need('## Release 1.3.4 in-app Turnip file manager requirements' in rules, 'PROJECT_RULES 1.3.4 section missing')
need('Google Play distribution policy for `MANAGE_EXTERNAL_STORAGE` is an external publication constraint' in rules, 'restricted-permission distribution caveat missing')

for rel, expected in contract['immutable_production_files'].items():
    path = root / rel
    need(path.is_file(), f'locked production file missing: {rel}')
    if path.is_file():
        need(digest(path) == expected, f'locked production file changed outside 1.3.4 scope: {rel}')
allowed = {
    'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt',
    'app/src/main/AndroidManifest.xml',
    'app/src/main/res/drawable/ic_folder.xml',
}
for path in (root / 'app/src/main').rglob('*'):
    if path.is_file():
        rel = path.relative_to(root).as_posix()
        need(rel in contract['immutable_production_files'] or rel in allowed, f'unexpected new production file: {rel}')

normalized_gradle = gradle.replace('versionCode = 1304', 'versionCode = 1303').replace('versionName = "1.3.4"', 'versionName = "1.3.3"')
need(hashlib.sha256(normalized_gradle.encode()).hexdigest() == contract['predecessor_build_gradle_sha256'], 'build.gradle changed beyond release identity')
normalized_manifest = manifest.replace('    <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />\n', '')
need(hashlib.sha256(normalized_manifest.encode()).hexdigest() == contract['predecessor_manifest_sha256'], 'AndroidManifest changed beyond MANAGE_EXTERNAL_STORAGE declaration')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 1.3.4 in-app Turnip file-manager contract')
