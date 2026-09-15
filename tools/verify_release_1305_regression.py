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

allowed = {
    Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'),
    Path('app/src/main/java/com/efishell/vulkanscope/VulkanQrCode.kt'),
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
    if new_file.is_file():
        rel = new_file.relative_to(root)
        need((pred / rel).is_file(), f'unexpected production file added: {rel}')

old_main = (pred / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
new_main = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
for start, end, label in [
    ('    private suspend fun installDriverBundleIo(', '    private fun activateManagedTurnipDriver(', 'Turnip installation/transaction core'),
    ('    private fun startUpdateDownload(update: AppUpdate) {', '    private fun pauseUpdateDownload()', 'updater transfer/security core'),
    ('private suspend fun submitDatabaseReport(', 'private fun safeFilePart(', 'Database submission core'),
    ('    private suspend fun collectReport(modeSnapshot: DriverMode): VulkanReport', '    private suspend fun runIsolatedProbe(', 'Vulkan collection/report boundary'),
]:
    ob = block(old_main, start, end)
    nb = block(new_main, start, end)
    if ob and nb:
        need(ob == nb, f'{label} changed outside demonstrated 1.3.5 fixes')

pred_manifest = (pred / 'app/src/main/AndroidManifest.xml').read_bytes()
new_manifest = (root / 'app/src/main/AndroidManifest.xml').read_bytes()
need(pred_manifest == new_manifest, 'AndroidManifest changed in 1.3.5')
pred_gradle = (pred / 'app/build.gradle.kts').read_text(encoding='utf-8')
new_gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
normalized = new_gradle.replace('versionCode = 1305', 'versionCode = 1304').replace('versionName = "1.3.5"', 'versionName = "1.3.4"')
need(normalized == pred_gradle, 'build.gradle changed beyond release identity')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 1.3.4 -> 1.3.5 full-audit regression boundary')
