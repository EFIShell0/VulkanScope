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
if len(pred_all) != 826:
    errors.append(f'predecessor census mismatch: {len(pred_all)} != 826')

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

pred_gradle = (pred / 'app/build.gradle.kts').read_text(encoding='utf-8').replace('versionCode = 3006', 'versionCode = 3007').replace('versionName = "3.0.6"', 'versionName = "3.0.7"')
cur_gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
if pred_gradle != cur_gradle:
    errors.append('app/build.gradle.kts changed beyond release identity')

for rel in [
    'app/src/main/AndroidManifest.xml',
    'app/src/main/cpp/vulkanscope.cpp',
    'app/src/main/java/com/efishell/vulkanscope/AdvancedAnalysis.kt',
    'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt',
    'app/src/main/java/com/efishell/vulkanscope/VulkanDependencyGraph.kt',
    'registry/registry_lock.json',
    'registry/generated/registry_query_manifest.json',
]:
    if not (pred / rel).is_file() or not (root / rel).is_file() or sha(pred / rel) != sha(root / rel):
        errors.append(f'protected predecessor-equivalent file drift: {rel}')

if sha(pred / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt') == sha(root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'):
    errors.append('MainActivity.kt did not change for requested 3.0.7 UI/evidence scope')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 3.0.7 predecessor regression boundary')
