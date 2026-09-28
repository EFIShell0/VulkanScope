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
if len(pred_all) != 736:
    errors.append(f'predecessor census mismatch: {len(pred_all)} != 736')
main_rel = 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
if main_rel not in pred_all or sha(pred_all[main_rel]) != '08972eea435a8f49769ec49cc1951c257b6f1fbcd5c758fae317407665a034d8':
    errors.append('predecessor MainActivity hash mismatch')

pred_prod = files_under(pred / 'app/src/main')
cur_prod = files_under(root / 'app/src/main')
allowed_added = {
    'res/drawable/ic_sort_name_asc.xml',
    'res/drawable/ic_sort_name_desc.xml',
    'res/drawable/ic_sort_modified_newest.xml',
    'res/drawable/ic_sort_modified_oldest.xml',
    'res/drawable/ic_sort_created_newest.xml',
    'res/drawable/ic_sort_created_oldest.xml',
}
added = set(cur_prod) - set(pred_prod)
removed = set(pred_prod) - set(cur_prod)
if added != allowed_added:
    errors.append(f'app/src/main additions mismatch: {sorted(added)}')
if removed:
    errors.append(f'unexpected app/src/main removals: {sorted(removed)}')
allowed_changed = {'java/com/efishell/vulkanscope/MainActivity.kt'}
for rel in sorted(set(pred_prod) & set(cur_prod)):
    if sha(pred_prod[rel]) != sha(cur_prod[rel]) and rel not in allowed_changed:
        errors.append(f'unexpected production byte drift: app/src/main/{rel}')

pred_gradle = (pred / 'app/build.gradle.kts').read_text(encoding='utf-8').replace('versionCode = 2106', 'versionCode = 2107').replace('versionName = "2.1.6"', 'versionName = "2.1.7"')
cur_gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
if pred_gradle != cur_gradle:
    errors.append('app/build.gradle.kts changed beyond release identity')

protected = [
    'app/src/main/AndroidManifest.xml',
    'app/src/main/cpp/vulkanscope.cpp',
    'registry/registry_lock.json',
    'registry/generated/registry_query_manifest.json',
]
for rel in protected:
    if not (pred / rel).is_file() or not (root / rel).is_file() or sha(pred / rel) != sha(root / rel):
        errors.append(f'protected predecessor-equivalent file drift: {rel}')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 2.1.7 predecessor regression boundary')
