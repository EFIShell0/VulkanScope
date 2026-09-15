#!/usr/bin/env python3
import argparse
import hashlib
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--predecessor', required=True)
parser.add_argument('--root')
args = parser.parse_args()
root = Path(args.root).resolve() if args.root else Path(__file__).resolve().parents[1]
pred = Path(args.predecessor).resolve()
errors = []

def need(condition, message):
    if not condition:
        errors.append(message)

def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()

def block(text, start, end):
    a = text.find(start)
    b = text.find(end, a + len(start)) if a >= 0 else -1
    if a < 0 or b <= a:
        errors.append(f'protected block missing: {start}')
        return ''
    return text[a:b]

pred_main_path = pred / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
new_main_path = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
need(pred_main_path.is_file(), 'predecessor MainActivity missing')
need(new_main_path.is_file(), 'new MainActivity missing')
if pred_main_path.is_file() and new_main_path.is_file():
    old = pred_main_path.read_text(encoding='utf-8')
    new = new_main_path.read_text(encoding='utf-8')
    start = '    private suspend fun installDriverBundleIo(sourceInfo: TurnipSourceInfo, openInput: () -> InputStream?): Int {'
    end = '    private fun activateManagedTurnipDriver(slot: Int) {'
    need(block(old, start, end) == block(new, start, end), 'existing Turnip install/validation core changed')
    for start, end, label in [
        ('    private fun startUpdateDownload(update: AppUpdate) {', '    private fun pauseUpdateDownload()', 'updater transfer core'),
        ('private suspend fun submitDatabaseReport(', 'private fun safeFilePart(', 'Database submission core'),
    ]:
        old_block = block(old, start, end)
        new_block = block(new, start, end)
        if old_block and new_block:
            need(old_block == new_block, f'{label} changed outside 1.3.4 scope')
    for text in [
        'Import unavailable · SAF and fallback paths were removed in 1.3.3',
        'Export unavailable · SAF and fallback paths were removed in 1.3.3',
        'Unavailable in 1.3.3 · SAF and Downloads fallback removed',
    ]:
        need(text in old and text in new, f'non-Turnip storage state drifted: {text}')

pred_manifest = (pred / 'app/src/main/AndroidManifest.xml').read_text(encoding='utf-8')
new_manifest = (root / 'app/src/main/AndroidManifest.xml').read_text(encoding='utf-8')
normalized_manifest = new_manifest.replace('    <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />\n', '')
need(normalized_manifest == pred_manifest, 'manifest changed beyond all-files special-access declaration')

pred_gradle = (pred / 'app/build.gradle.kts').read_text(encoding='utf-8')
new_gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
normalized_gradle = new_gradle.replace('versionCode = 1304', 'versionCode = 1303').replace('versionName = "1.3.4"', 'versionName = "1.3.3"')
need(normalized_gradle == pred_gradle, 'build.gradle changed beyond release identity')

allowed = {
    Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'),
    Path('app/src/main/AndroidManifest.xml'),
    Path('app/src/main/res/drawable/ic_folder.xml'),
}
for old_file in sorted((pred / 'app/src/main').rglob('*')):
    if not old_file.is_file():
        continue
    rel = old_file.relative_to(pred)
    new_file = root / rel
    need(new_file.is_file(), f'predecessor production file removed: {rel}')
    if new_file.is_file() and rel not in allowed:
        need(digest(old_file) == digest(new_file), f'locked production file changed: {rel}')
for new_file in sorted((root / 'app/src/main').rglob('*')):
    if not new_file.is_file():
        continue
    rel = new_file.relative_to(root)
    if not (pred / rel).is_file():
        need(rel == Path('app/src/main/res/drawable/ic_folder.xml'), f'unexpected new production file: {rel}')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 1.3.3 -> 1.3.4 file-manager regression boundary')
