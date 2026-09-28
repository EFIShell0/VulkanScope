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
if len(pred_all) != 751:
    errors.append(f'predecessor census mismatch: {len(pred_all)} != 751')
main_rel = 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
if main_rel not in pred_all or sha(pred_all[main_rel]) != '0ba308122100e1bb40d1dc9594e6fe382756dbb33a832dc5ff5b1b3e18c1e756':
    errors.append('predecessor MainActivity hash mismatch')

pred_prod = files_under(pred / 'app/src/main')
cur_prod = files_under(root / 'app/src/main')
if set(cur_prod) != set(pred_prod):
    errors.append('app/src/main file set changed in pager-only release')
for rel in sorted(set(pred_prod) & set(cur_prod)):
    if rel == 'java/com/efishell/vulkanscope/MainActivity.kt':
        continue
    if sha(pred_prod[rel]) != sha(cur_prod[rel]):
        errors.append(f'unexpected production byte drift: app/src/main/{rel}')

pred_gradle = (pred / 'app/build.gradle.kts').read_text(encoding='utf-8').replace('versionCode = 2108', 'versionCode = 2109').replace('versionName = "2.1.8"', 'versionName = "2.1.9"')
cur_gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
if pred_gradle != cur_gradle:
    errors.append('app/build.gradle.kts changed beyond release identity')

for rel in [
    'app/src/main/AndroidManifest.xml',
    'app/src/main/cpp/vulkanscope.cpp',
    'registry/registry_lock.json',
    'registry/generated/registry_query_manifest.json',
]:
    if not (pred / rel).is_file() or not (root / rel).is_file() or sha(pred / rel) != sha(root / rel):
        errors.append(f'protected predecessor-equivalent file drift: {rel}')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 2.1.9 predecessor regression boundary')
