#!/usr/bin/env python3
import argparse
import hashlib
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--predecessor', type=Path, required=True)
parser.add_argument('--root', type=Path, default=Path(__file__).resolve().parents[1])
args = parser.parse_args()
pred = args.predecessor.resolve()
root = args.root.resolve()
errors = []

def sha(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()

def files_under(base):
    return {p.relative_to(base).as_posix(): p for p in base.rglob('*') if p.is_file()}

if not pred.is_dir():
    raise SystemExit('predecessor directory missing')
pred_all = files_under(pred)
if len(pred_all) != 851:
    errors.append(f'predecessor census mismatch: {len(pred_all)} != 851')

pred_prod = files_under(pred / 'app/src/main')
cur_prod = files_under(root / 'app/src/main')
if set(cur_prod) != set(pred_prod):
    errors.append('app/src/main file set changed')
allowed = {'java/com/efishell/vulkanscope/MainActivity.kt'}
for rel in sorted(set(pred_prod) & set(cur_prod)):
    if rel in allowed:
        continue
    if sha(pred_prod[rel]) != sha(cur_prod[rel]):
        errors.append(f'unexpected production byte drift: app/src/main/{rel}')

pred_gradle = (pred / 'app/build.gradle.kts').read_text(encoding='utf-8').replace('versionCode = 3011', 'versionCode = 3012').replace('versionName = "3.0.11"', 'versionName = "3.0.12"')
cur_gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
if pred_gradle != cur_gradle:
    errors.append('app/build.gradle.kts changed beyond release identity')

pred_main = (pred / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
cur_main = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
start = 'private suspend fun scanTurnipFileManagerDirectory('
end = '\n\nprivate enum class SharedStorageBrowserMode'
pa, pb = pred_main.find(start), pred_main.find(end, pred_main.find(start))
ca, cb = cur_main.find(start), cur_main.find(end, cur_main.find(start))
if min(pa, pb, ca, cb) < 0:
    errors.append('scanTurnipFileManagerDirectory boundary missing')
else:
    if pred_main[:pa] != cur_main[:ca] or pred_main[pb:] != cur_main[cb:]:
        errors.append('MainActivity.kt changed outside requested Turnip folder-scan function')
    if pred_main[pa:pb] == cur_main[ca:cb]:
        errors.append('Turnip folder-scan function did not change')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 3.0.12 predecessor regression boundary')
