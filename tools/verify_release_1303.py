#!/usr/bin/env python3
import argparse
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root')
parser.add_argument('--skip-version', action='store_true')
args = parser.parse_args()
root = Path(args.root).resolve() if args.root else Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
manifest = (root / 'app/src/main/AndroidManifest.xml').read_text(encoding='utf-8')
rules = (root / 'rules/PROJECT_RULES.md').read_text(encoding='utf-8')
errors = []

def need(condition, message):
    if not condition:
        errors.append(message)

if not args.skip_version:
    need('versionCode = 1303' in gradle and 'versionName = "1.3.3"' in gradle, '1.3.3 release identity missing')

for token in [
    'ActivityResultContracts.OpenDocument',
    'ActivityResultContracts.CreateDocument',
    'Intent.ACTION_OPEN_DOCUMENT',
    'Intent.ACTION_CREATE_DOCUMENT',
    'takePersistableUriPermission',
    'tryLaunchSystemDocumentPicker',
    'FallbackTurnipCandidate',
    'FallbackTurnipDialogData',
    'FallbackTurnipImportDialog',
    'FallbackAnalysisImportKind',
    'FallbackAnalysisCandidate',
    'FallbackAnalysisDialogData',
    'FallbackAnalysisImportDialog',
    'analysisExchangeRoots(',
    'scanAnalysisExchangeFiles(',
    'writeAnalysisExchangeFile(',
    'writeExportSnapshotToDownloads(',
    'launchExportPickerOrFallback(',
    'MediaStore.Downloads',
]:
    need(token not in main, f'legacy SAF/fallback route remains in production source: {token}')

for permission in ['android.permission.MANAGE_EXTERNAL_STORAGE', 'android.permission.READ_EXTERNAL_STORAGE', 'android.permission.WRITE_EXTERNAL_STORAGE']:
    need(permission not in manifest, f'unrequested storage permission introduced: {permission}')

need('private fun requestDriverBundleImport()' in main, 'Turnip import unavailable boundary missing')
need('Turnip ZIP import is unavailable in this release because SAF and its fallback paths were removed.' in main, 'Turnip storage-removal disclosure missing')
need('private suspend fun installDriverBundleIo(sourceInfo: TurnipSourceInfo, openInput: () -> InputStream?): Int' in main, 'retained private Turnip validation/install core missing')
need('Driver metadata must resolve to exactly one Vulkan library' in main and 'Driver bundle contains too many entries' in main, 'retained Turnip package validation contract drifted')

for text in [
    'Import unavailable · SAF and fallback paths were removed in 1.3.3',
    'Export unavailable · SAF and fallback paths were removed in 1.3.3',
    'TXT/HTML storage export is unavailable in 1.3.3 because SAF and the previous Downloads fallback were removed.',
    'this release intentionally adds no replacement storage path.',
]:
    need(text in main, f'explicit unavailable storage state missing: {text}')

need(main.count('"Unavailable in 1.3.3 · SAF and Downloads fallback removed"') == 4, 'TXT/HTML export actions are not all explicitly disabled/unavailable')
need(main.count('R.drawable.ic_action_text, Modifier.fillMaxWidth(), false, true) { }') == 1, 'expanded TXT action is not disabled')
need(main.count('R.drawable.ic_action_html, Modifier.fillMaxWidth(), false, true) { }') == 1, 'expanded HTML action is not disabled')
need(main.count('R.drawable.ic_action_text, Modifier.weight(1f), false, true) { }') == 1, 'compact TXT action is not disabled')
need(main.count('R.drawable.ic_action_html, Modifier.weight(1f), false, true) { }') == 1, 'compact HTML action is not disabled')

need('## Release 1.3.3 SAF and SAF-fallback removal-only requirements' in rules, 'PROJECT_RULES 1.3.3 storage-removal section missing')
need('No replacement file manager' in rules and '`MANAGE_EXTERNAL_STORAGE`' in rules, '1.3.3 no-replacement/broad-storage contract missing')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 1.3.3 SAF and SAF-fallback removal-only contract')
