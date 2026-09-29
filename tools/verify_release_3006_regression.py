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
if len(pred_all) != 821:
    errors.append(f'predecessor census mismatch: {len(pred_all)} != 821')

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

pred_gradle = (pred / 'app/build.gradle.kts').read_text(encoding='utf-8').replace('versionCode = 3005', 'versionCode = 3006').replace('versionName = "3.0.5"', 'versionName = "3.0.6"')
cur_gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
if pred_gradle != cur_gradle:
    errors.append('app/build.gradle.kts changed beyond release identity')

pred_main = (pred / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
cur_main = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
normalized = cur_main
normalized = normalized.replace('    val networkAvailable = LocalValidatedNetwork.current\n    VulkanLazyPage(verticalSpacing = 12.dp) {\n        analysisWorkspaceItems(analysisModel, report, device, networkAvailable)', '    VulkanLazyPage(verticalSpacing = 12.dp) {\n        analysisWorkspaceItems(analysisModel, report, device)', 1)
normalized = normalized.replace('private fun LazyListScope.analysisWorkspaceItems(model: AnalysisWorkspaceModel, report: VulkanReport, device: DeviceReport?, networkAvailable: Boolean) {', 'private fun LazyListScope.analysisWorkspaceItems(model: AnalysisWorkspaceModel, report: VulkanReport, device: DeviceReport?) {', 1)
normalized = normalized.replace('        9 -> {\n            item {', '        9 -> {\n            val networkAvailable = LocalValidatedNetwork.current\n            item {', 1)
if normalized != pred_main:
    errors.append('MainActivity.kt changed beyond documented CompositionLocal threading repair')
if sha(pred / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt') == sha(root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'):
    errors.append('MainActivity.kt did not change for requested 3.0.6 compile repair')

for rel in [
    'app/src/main/java/com/efishell/vulkanscope/AdvancedAnalysis.kt',
    'app/src/main/java/com/efishell/vulkanscope/VulkanDependencyGraph.kt',
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
print('PASS VulkanScope 3.0.6 predecessor regression boundary')
