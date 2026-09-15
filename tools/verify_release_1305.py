#!/usr/bin/env python3
import argparse
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root')
parser.add_argument('--skip-version', action='store_true')
args = parser.parse_args()
root = Path(args.root).resolve() if args.root else Path(__file__).resolve().parents[1]
main_path = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
qr_path = root / 'app/src/main/java/com/efishell/vulkanscope/VulkanQrCode.kt'
manifest_path = root / 'app/src/main/AndroidManifest.xml'
gradle_path = root / 'app/build.gradle.kts'
rules_path = root / 'rules/PROJECT_RULES.md'
main = main_path.read_text(encoding='utf-8')
qr = qr_path.read_text(encoding='utf-8')
manifest = manifest_path.read_text(encoding='utf-8')
gradle = gradle_path.read_text(encoding='utf-8')
rules = rules_path.read_text(encoding='utf-8')
errors = []

def need(condition, message):
    if not condition:
        errors.append(message)

def block(text, start, end):
    a = text.find(start)
    b = text.find(end, a + len(start)) if a >= 0 else -1
    return text[a:b] if a >= 0 and b > a else ''

if not args.skip_version:
    need('versionCode = 1305' in gradle and 'versionName = "1.3.5"' in gradle, '1.3.5 release identity missing')

need(main.count('val registryCoverage = report.registryCoverage') == 1, 'duplicate registryCoverage declaration remains')
need('private suspend fun inspectTurnipArchive(file: File): TurnipArchiveCandidate?' in main, 'Turnip ZIP inspection is not cancellable suspend work')
need('private suspend fun scanTurnipFileManagerDirectory(root: File, directory: File): TurnipDirectoryListing' in main, 'Turnip directory scan is not cancellable suspend work')
inspect = block(main, 'private suspend fun inspectTurnipArchive(file: File): TurnipArchiveCandidate?', 'private suspend fun scanTurnipFileManagerDirectory(root: File, directory: File)')
scan = block(main, 'private suspend fun scanTurnipFileManagerDirectory(root: File, directory: File): TurnipDirectoryListing', 'private data class SystemDriverSummary')
need(inspect.count('coroutineContext.ensureActive()') >= 2, 'Turnip archive inspection lacks cooperative cancellation checks')
need('catch (cancelled: CancellationException)' in inspect and 'throw cancelled' in inspect, 'Turnip archive inspection swallows cancellation')
need(scan.count('coroutineContext.ensureActive()') >= 3, 'Turnip directory scan lacks cooperative cancellation checks')
need('turnipFileManagerScanJob: Job?' in main and 'turnipFileManagerScanGeneration' in main, 'file-manager scan ownership/generation state missing')
need('turnipFileManagerScanJob?.cancel()' in main, 'file-manager scan cancellation missing')
load = block(main, '    private fun loadTurnipFileManagerDirectory(path: String)', '    private fun navigateTurnipFileManagerUp()')
need('current.loading' in load, 'directory navigation does not reject an in-flight scan')
need('generation != turnipFileManagerScanGeneration' in load, 'stale directory-scan publication guard missing')
need('folders = emptyList(), candidates = emptyList()' in load, 'stale directory contents remain visible while a new scan is loading/fails')
close = block(main, '    private fun closeTurnipFileManager()', '    private fun importSelectedTurnipFiles()')
need('turnipFileManagerScanJob?.cancel()' in close and 'turnipFileManagerScanGeneration += 1L' in close, 'closing the file manager does not invalidate/cancel scanning')
on_destroy = block(main, '    override fun onDestroy()', '    override fun onCreate(')
need('turnipFileManagerScanJob?.cancel()' in on_destroy and 'activityScope.cancel()' in on_destroy, 'activity destruction does not cancel owned file-manager/background work')

folder = block(main, 'private fun TurnipFileManagerFolderRow(', 'private fun TurnipFileManagerCandidateRow(')
need('.clickable(enabled = enabled, role = Role.Button, onClick = onOpen)' in folder, 'folder rows lack standard Material click semantics/effect')
need('.pointerInput(' not in folder, 'folder rows still use raw pointer handling')
selector = block(main, 'private fun ExpressiveSingleFilterSelector(', '@Composable\nprivate fun ExpressiveSingleFilterPopup')
if not selector:
    selector = block(main, 'private fun ExpressiveSingleFilterSelector(', '@Composable\nprivate fun ExpressiveFilterBar')
need('.clickable(enabled = enabled, role = Role.Button)' in selector, 'single-filter selector lacks standard Material click semantics/effect')
need('.pointerInput(enabled)' not in selector, 'single-filter selector still uses raw pointer handling')
dialog = block(main, 'private fun TurnipFileManagerDialog(', '@Composable\nprivate fun TurnipFileManagerSelectionSummary')
need('val expandedTextLayout = preferExpandedTextLayout()' in dialog and 'TurnipFileManagerSelectionSummary(state)' in dialog and 'TurnipFileManagerViewModeButtons(state, onViewMode)' in dialog, 'file-manager selection/mode header lacks responsive large-text layout')

need('produceState<BitMatrix?>' in qr, 'QR matrix is not produced asynchronously')
need('withContext(Dispatchers.Default)' in qr, 'QR encoding is not moved off the UI thread')
need('remember(text)' not in qr, 'synchronous Compose QR encoding remains')

for token in ['ActivityResultContracts.OpenDocument', 'ActivityResultContracts.CreateDocument', 'Intent.ACTION_OPEN_DOCUMENT', 'Intent.ACTION_CREATE_DOCUMENT', 'takePersistableUriPermission']:
    need(token not in main, f'SAF route reintroduced: {token}')
need(manifest.count('android.permission.MANAGE_EXTERNAL_STORAGE') == 1, 'all-files special-access permission drifted')
need('android.permission.READ_EXTERNAL_STORAGE' not in manifest and 'android.permission.WRITE_EXTERNAL_STORAGE' not in manifest, 'legacy storage permission reintroduced')
need('android:allowBackup="false"' in manifest, 'backup hardening missing')
need('android:usesCleartextTraffic="false"' in manifest, 'cleartext network hardening missing')
need('android:exported="false"' in manifest and 'android:grantUriPermissions="true"' in manifest, 'private provider/service export hardening drifted')
need('GlobalScope' not in main and 'GlobalScope' not in qr, 'unowned GlobalScope work introduced')

for token in [
    'if (++entryCount > 2048)',
    'fileBytes > 32L * 1024L * 1024L',
    'totalBytes > 64L * 1024L * 1024L',
    'compressedBytes > TURNIP_ARCHIVE_INPUT_MAX_BYTES',
    'if (metadataCount != 1)',
    'if (schemaVersion != 1)',
    'segments.any { it == "." || it == ".." }',
    'if (!seen.add(normalized))',
    'if (libraries.size != 1 || libraries.single().second <= 0L)',
]:
    need(token in main, f'Turnip archive safety bound drifted: {token}')
need('children.size >= 4096' in main and 'inspectedZipCount >= 256' in main, 'file-manager scan bounds drifted')
need('withContext(Dispatchers.IO) { scanTurnipFileManagerDirectory(root, target) }' in main, 'directory scanning moved back to UI dispatcher')
need('withContext(Dispatchers.IO) { inspectTurnipArchive(File(path)) }' in main, 'pre-import archive validation moved back to UI dispatcher')
import_flow = block(main, '    private fun importSelectedTurnipFiles()', '    private suspend fun installDriverBundleIo')
need('catch (cancelled: CancellationException)' in import_flow and 'throw cancelled' in import_flow, 'Turnip import converts lifecycle cancellation into an ordinary package failure')
need('runCatching {\n                    withContext(Dispatchers.IO) { installDriverBundleIo' not in import_flow, 'Turnip import still swallows installation cancellation through runCatching')
need('private suspend fun installDriverBundleIo' in main and 'Driver metadata must resolve to exactly one Vulkan library' in main, 'Turnip installation validation core missing')

for base in [root / 'app/src/main/java', root / 'app/src/main/cpp']:
    for path in base.rglob('*'):
        if path.is_file() and path.suffix in {'.kt', '.cpp', '.cc', '.c', '.h', '.hpp'}:
            for line_no, line in enumerate(path.read_text(encoding='utf-8', errors='ignore').splitlines(), 1):
                stripped = line.lstrip()
                if stripped.startswith('//') or '/*' in line or '*/' in line:
                    errors.append(f'source comment prohibited: {path.relative_to(root)}:{line_no}')
                    break

need('## Release 1.3.5 full correctness, security, performance and UX audit requirements' in rules, 'PROJECT_RULES 1.3.5 section missing')
need('Vulkan 1.4.362' in rules and '2026-09-04' in rules, 'current Vulkan published baseline audit record missing')
need((root / 'tests/golden/1.3.4_regression_contract.json').is_file(), '1.3.4 predecessor regression contract missing')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 1.3.5 full-audit source contract')
