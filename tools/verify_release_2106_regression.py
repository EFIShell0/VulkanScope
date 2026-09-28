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
if len(pred_all) != 731:
    errors.append(f'predecessor census mismatch: {len(pred_all)} != 731')
main_rel = 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
if main_rel not in pred_all or sha(pred_all[main_rel]) != 'dba3cb2b7491d506b93e5b961179797d52cd49bdc02eb0ab3d885da78b11973d':
    errors.append('predecessor MainActivity hash mismatch')

pred_prod = files_under(pred / 'app/src/main')
cur_prod = files_under(root / 'app/src/main')
added = set(cur_prod) - set(pred_prod)
removed = set(pred_prod) - set(cur_prod)
if added:
    errors.append(f'unexpected app/src/main additions: {sorted(added)}')
if removed:
    errors.append(f'unexpected app/src/main removals: {sorted(removed)}')
allowed_changed = {'java/com/efishell/vulkanscope/MainActivity.kt'}
for rel in sorted(set(pred_prod) & set(cur_prod)):
    if sha(pred_prod[rel]) != sha(cur_prod[rel]) and rel not in allowed_changed:
        errors.append(f'unexpected production byte drift: app/src/main/{rel}')

pred_gradle = (pred / 'app/build.gradle.kts').read_text(encoding='utf-8').replace('versionCode = 2105', 'versionCode = 2106').replace('versionName = "2.1.5"', 'versionName = "2.1.6"')
cur_gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
if pred_gradle != cur_gradle:
    errors.append('app/build.gradle.kts changed beyond release identity')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 2.1.6 predecessor regression boundary')
