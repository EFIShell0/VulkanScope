#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_2111.py'
mutations = [
    (
        'restore wrong rememberGraphicsLayer package',
        'import androidx.compose.ui.graphics.rememberGraphicsLayer',
        'import androidx.compose.ui.graphics.layer.rememberGraphicsLayer',
    ),
    (
        'restore wrong drawLayer package',
        'import androidx.compose.ui.graphics.layer.drawLayer',
        'import androidx.compose.ui.graphics.drawscope.drawLayer',
    ),
    (
        'remove rememberGraphicsLayer import',
        'import androidx.compose.ui.graphics.rememberGraphicsLayer\n',
        '',
    ),
]

for name, old, new in mutations:
    with tempfile.TemporaryDirectory(prefix='vulkanscope-2111-neg-') as temp:
        root = Path(temp) / 'tree'
        shutil.copytree(source, root)
        main = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
        text = main.read_text(encoding='utf-8')
        if old not in text:
            raise SystemExit(f'negative mutation source token missing: {name}: {old}')
        main.write_text(text.replace(old, new, 1), encoding='utf-8')
        result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        if result.returncode == 0:
            raise SystemExit(f'negative mutation unexpectedly passed: {name}')

with tempfile.TemporaryDirectory(prefix='vulkanscope-2111-control-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    audit = root / 'rules/2.1.11_GRAPHICS_LAYER_IMPORT_COMPILE_FIX_AUDIT.md'
    audit.write_text(audit.read_text(encoding='utf-8') + '\nAdditional bounded compiler-evidence note.\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode != 0:
        print(result.stdout)
        raise SystemExit('documentation-only false-positive control failed')

print('PASS VulkanScope 2.1.11 negative import mutations and false-positive control')
