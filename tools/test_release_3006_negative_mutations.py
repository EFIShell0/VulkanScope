#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_3006.py'
main_rel = 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'

mutations = [
    (
        'restore CompositionLocal read inside non-composable LazyListScope builder',
        'private fun LazyListScope.analysisWorkspaceItems(model: AnalysisWorkspaceModel, report: VulkanReport, device: DeviceReport?, networkAvailable: Boolean) {\n    val state = model.state',
        'private fun LazyListScope.analysisWorkspaceItems(model: AnalysisWorkspaceModel, report: VulkanReport, device: DeviceReport?, networkAvailable: Boolean) {\n    val networkAvailable = LocalValidatedNetwork.current\n    val state = model.state',
    ),
    (
        'force Database network availability',
        'private fun LazyListScope.analysisWorkspaceItems(model: AnalysisWorkspaceModel, report: VulkanReport, device: DeviceReport?, networkAvailable: Boolean) {\n    val state = model.state',
        'private fun LazyListScope.analysisWorkspaceItems(model: AnalysisWorkspaceModel, report: VulkanReport, device: DeviceReport?, networkAvailable: Boolean) {\n    val networkAvailable = true\n    val state = model.state',
    ),
    (
        'drop validated-network argument from AnalysisPage call',
        'analysisWorkspaceItems(analysisModel, report, device, networkAvailable)',
        'analysisWorkspaceItems(analysisModel, report, device)',
    ),
]

for name, old, new in mutations:
    with tempfile.TemporaryDirectory(prefix='vulkanscope-3006-neg-') as temp:
        root = Path(temp) / 'tree'
        shutil.copytree(source, root)
        target = root / main_rel
        text = target.read_text(encoding='utf-8')
        if old not in text:
            raise SystemExit(f'negative mutation source token missing: {name}')
        target.write_text(text.replace(old, new, 1), encoding='utf-8')
        result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        if result.returncode == 0:
            raise SystemExit(f'negative mutation unexpectedly passed: {name}')

with tempfile.TemporaryDirectory(prefix='vulkanscope-3006-control-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    audit = root / 'rules/3.0.6_ANALYSIS_COMPOSITIONLOCAL_COMPILE_AUDIT.md'
    audit.write_text(audit.read_text(encoding='utf-8') + '\nAdditional compiler evidence may be appended without changing production behavior.\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode != 0:
        print(result.stdout)
        raise SystemExit('documentation-only false-positive control failed')

print('PASS VulkanScope 3.0.6 negative mutations and documentation false-positive control')
