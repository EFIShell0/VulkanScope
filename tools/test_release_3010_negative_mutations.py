#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_3010.py'
main_rel = 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
mutations = [
    (
        'reintroduce unresolved LocalContext reference',
        '@Composable\nprivate fun DatabaseSubmittedAt(raw: String) {\n    val context = androidx.compose.ui.platform.LocalContext.current',
        '@Composable\nprivate fun DatabaseSubmittedAt(raw: String) {\n    val context = LocalContext.current'
    ),
    (
        'restore anchored landscape dropdown',
        '        Dialog(\n            onDismissRequest = { },',
        '        DropdownMenu(\n            expanded = true,\n            onDismissRequest = { },'
    ),
    (
        'allow outside-tap dismissal',
        'dismissOnClickOutside = false',
        'dismissOnClickOutside = true'
    ),
    (
        'remove D-pad scroll support from chooser',
        '.dpadScrollableNavigation(menuScrollState)',
        '.focusable()'
    ),
]

for name, old, new in mutations:
    with tempfile.TemporaryDirectory(prefix='vulkanscope-3010-neg-') as temp:
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

with tempfile.TemporaryDirectory(prefix='vulkanscope-3010-protected-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    protected = root / 'app/src/main/cpp/vulkanscope.cpp'
    protected.write_bytes(protected.read_bytes() + b'\n')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode == 0:
        raise SystemExit('protected native mutation unexpectedly passed')

with tempfile.TemporaryDirectory(prefix='vulkanscope-3010-control-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    audit = root / 'rules/3.0.10_KOTLIN_COMPILE_FILE_MANAGER_LANDSCAPE_AUDIT.md'
    audit.write_text(audit.read_text(encoding='utf-8') + '\nAdditional bounded documentation evidence.\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode != 0:
        print(result.stdout)
        raise SystemExit('documentation-only false-positive control failed')

print('PASS VulkanScope 3.0.10 negative mutations and documentation false-positive control')
