#!/usr/bin/env python3
import argparse
import hashlib
import json
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--predecessor', required=True)
parser.add_argument('--root')
args = parser.parse_args()
root = Path(args.root).resolve() if args.root else Path(__file__).resolve().parents[1]
pred = Path(args.predecessor).resolve()
contract = json.loads((root / 'tests/golden/1.3.2_storage_removal_contract.json').read_text(encoding='utf-8'))
errors = []

def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()

def need(condition, message):
    if not condition:
        errors.append(message)

for rel, expected in contract['predecessor']['lockedHashes'].items():
    p = pred / rel
    need(p.is_file(), f'predecessor locked file missing: {rel}')
    if p.is_file():
        need(digest(p) == expected, f'predecessor locked hash mismatch: {rel}')

allowed_runtime = {Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')}
for old in sorted((pred / 'app/src/main').rglob('*')):
    if not old.is_file():
        continue
    rel = old.relative_to(pred)
    new = root / rel
    need(new.is_file(), f'predecessor runtime file removed: {rel}')
    if new.is_file() and rel not in allowed_runtime:
        need(digest(old) == digest(new), f'locked runtime file changed: {rel}')
for new in sorted((root / 'app/src/main').rglob('*')):
    if not new.is_file():
        continue
    rel = new.relative_to(root)
    need((pred / rel).is_file(), f'unexpected new runtime file: {rel}')

pred_main = (pred / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
new_main = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')

def block(text, start, end):
    a = text.find(start)
    b = text.find(end, a + len(start)) if a >= 0 else -1
    if a < 0 or b <= a:
        errors.append(f'protected block missing: {start}')
        return ''
    return text[a:b]

# Retained private Turnip validator must be byte-identical; only its old external source frontends were removed.
start = '    private suspend fun installDriverBundleIo(sourceInfo: TurnipSourceInfo, openInput: () -> InputStream?): Int {'
end = '    private fun activateManagedTurnipDriver(slot: Int) {'
need(block(pred_main, start, end) == block(new_main, start, end), 'retained Turnip install/validation core changed')
# Updater and Database transport are explicitly outside this release.
for start, end, label in [
    ('    private fun startUpdateDownload(update: AppUpdate) {', '    private fun pauseUpdateDownload()', 'updater transfer core'),
    ('private suspend fun submitDatabaseReport(', 'private fun safeFilePart(', 'Database submission core'),
]:
    old_block = block(pred_main, start, end)
    new_block = block(new_main, start, end)
    if old_block and new_block:
        need(old_block == new_block, f'{label} changed outside 1.3.3 scope')

need(digest(root / 'app/src/main/AndroidManifest.xml') == digest(pred / 'app/src/main/AndroidManifest.xml'), 'AndroidManifest changed')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 1.3.2 -> 1.3.3 storage-removal regression boundary')
