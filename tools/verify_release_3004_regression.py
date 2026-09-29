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
if len(pred_all) != 811:
    errors.append(f'predecessor census mismatch: {len(pred_all)} != 811')

pred_prod = files_under(pred / 'app/src/main')
cur_prod = files_under(root / 'app/src/main')
if set(cur_prod) != set(pred_prod):
    errors.append('app/src/main file set changed')
allowed = {
    'java/com/efishell/vulkanscope/MainActivity.kt',
    'java/com/efishell/vulkanscope/VulkanDependencyGraph.kt',
}
for rel in sorted(set(pred_prod) & set(cur_prod)):
    if rel in allowed:
        continue
    if sha(pred_prod[rel]) != sha(cur_prod[rel]):
        errors.append(f'unexpected production byte drift: app/src/main/{rel}')

pred_main = (pred / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
cur_main = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
normalized_main = cur_main.replace('event.key.nativeKeyCode', 'event.nativeKeyCode', 6)
if normalized_main != pred_main:
    errors.append('MainActivity.kt changed beyond the six documented keycode receiver repairs')

pred_graph = (pred / 'app/src/main/java/com/efishell/vulkanscope/VulkanDependencyGraph.kt').read_text(encoding='utf-8')
cur_graph = (root / 'app/src/main/java/com/efishell/vulkanscope/VulkanDependencyGraph.kt').read_text(encoding='utf-8')
normalized_graph = cur_graph.replace('import androidx.compose.foundation.layout.width\n', 'import androidx.compose.foundation.layout.width\nimport androidx.compose.foundation.layout.weight\n', 1)
if normalized_graph != pred_graph:
    errors.append('VulkanDependencyGraph.kt changed beyond removal of the invalid weight import')

pred_gradle = (pred / 'app/build.gradle.kts').read_text(encoding='utf-8').replace('versionCode = 3003', 'versionCode = 3004').replace('versionName = "3.0.3"', 'versionName = "3.0.4"')
cur_gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
if pred_gradle != cur_gradle:
    errors.append('app/build.gradle.kts changed beyond release identity')

for rel in [
    'app/src/main/AndroidManifest.xml',
    'app/src/main/cpp/vulkanscope.cpp',
    'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt',
    'registry/registry_lock.json',
    'registry/generated/registry_query_manifest.json',
]:
    if not (pred / rel).is_file() or not (root / rel).is_file() or sha(pred / rel) != sha(root / rel):
        errors.append(f'protected predecessor-equivalent file drift: {rel}')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 3.0.4 predecessor regression boundary')
