#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_3008.py'
main_rel = 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
mutations = [
    (
        'reintroduce television-only lazy-list gate',
        'private fun Modifier.tvRemoteLazyListNavigation(state: LazyListState): Modifier {\n    val focusManager = LocalFocusManager.current',
        'private fun Modifier.tvRemoteLazyListNavigation(state: LazyListState): Modifier {\n    val configuration = LocalConfiguration.current\n    val isTelevision = configuration.uiMode and Configuration.UI_MODE_TYPE_MASK == Configuration.UI_MODE_TYPE_TELEVISION\n    if (!isTelevision) return this\n    val focusManager = LocalFocusManager.current'
    ),
    (
        'restore generic graph section icon',
        'title.equals("Dependency graph explorer", true) || title.equals("Graph legend", true) || title.equals("Capability dependency graph", true) || title.equals("Visual registry-reference graph", true) || title.equals("Graph overview", true) || title.equals("Interactive dependency map", true) -> R.drawable.ic_graph',
        'title.equals("Dependency graph explorer", true) || title.equals("Graph legend", true) || title.equals("Capability dependency graph", true) || title.equals("Visual registry-reference graph", true) || title.equals("Graph overview", true) || title.equals("Interactive dependency map", true) -> R.drawable.ic_info'
    ),
    (
        'restore predecessor Database vendor artwork path',
        'SystemDriverVendorBadge(reportRow.vendorId)',
        'VendorLogo(vendorId = vendorIdFromDisplay(reportRow.vendorId), modifier = Modifier.size(46.dp))'
    ),
    (
        'remove common metric accent container',
        'color = VulkanAccentContainer,\n        contentColor = VulkanTextPrimary,',
        'color = VulkanSurfaceRaised,\n        contentColor = VulkanTextPrimary,'
    ),
    (
        'remove requirement state summary',
        '"CHECKED" to model.requirementEvaluations.size.toString()',
        '"CHECKED" to "hidden"'
    ),
    (
        'remove Turnip directory transition',
        'targetState = state.directoryPath,',
        'targetState = "static",'
    ),
    (
        'remove Encyclopedia evidence boundary',
        'Text("Evidence boundary", color = VulkanTextPrimary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)',
        'Text("Reference", color = VulkanTextPrimary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)'
    ),
]

for name, old, new in mutations:
    with tempfile.TemporaryDirectory(prefix='vulkanscope-3008-neg-') as temp:
        root = Path(temp) / 'tree'
        shutil.copytree(source, root)
        target = root / main_rel
        text = target.read_text(encoding='utf-8')
        if old not in text:
            raise SystemExit(f'negative mutation source token missing: {name}')
        target.write_text(text.replace(old, new, 1), encoding='utf-8')
        files = sorted(path.relative_to(root).as_posix() for path in root.rglob('*') if path.is_file())
        (root / 'files.txt').write_text('\n'.join(files) + '\n', encoding='utf-8')
        result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        if result.returncode == 0:
            raise SystemExit(f'negative mutation unexpectedly passed: {name}')

with tempfile.TemporaryDirectory(prefix='vulkanscope-3008-protected-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    protected = root / 'app/src/main/cpp/vulkanscope.cpp'
    protected.write_bytes(protected.read_bytes() + b'\n')
    files = sorted(path.relative_to(root).as_posix() for path in root.rglob('*') if path.is_file())
    (root / 'files.txt').write_text('\n'.join(files) + '\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode == 0:
        raise SystemExit('protected native mutation unexpectedly passed')

with tempfile.TemporaryDirectory(prefix='vulkanscope-3008-control-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    audit = root / 'rules/3.0.8_DPAD_ANALYSIS_METRICS_FILE_MANAGER_AUDIT.md'
    audit.write_text(audit.read_text(encoding='utf-8') + '\nAdditional bounded documentation evidence.\n', encoding='utf-8')
    files = sorted(path.relative_to(root).as_posix() for path in root.rglob('*') if path.is_file())
    (root / 'files.txt').write_text('\n'.join(files) + '\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode != 0:
        print(result.stdout)
        raise SystemExit('documentation-only false-positive control failed')

print('PASS VulkanScope 3.0.8 negative mutations and documentation false-positive control')
