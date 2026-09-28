#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_3002.py'
mutations = [
    (
        'remove expressive opt-in',
        '@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun SharedStorageBrowserListing(',
        '@Composable\nprivate fun SharedStorageBrowserListing(',
    ),
    (
        'substitute wrong material opt-in',
        '@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun SharedStorageBrowserListing(',
        '@OptIn(ExperimentalMaterial3Api::class)\n@Composable\nprivate fun SharedStorageBrowserListing(',
    ),
    (
        'remove expressive loading call',
        '                LoadingIndicator(color = VulkanAccentSoft)\n                Text("Reading shared storage…"',
        '                Text("Reading shared storage…"',
    ),
]

for name, old, new in mutations:
    with tempfile.TemporaryDirectory(prefix='vulkanscope-3002-neg-') as temp:
        root = Path(temp) / 'tree'
        shutil.copytree(source, root)
        main = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
        text = main.read_text(encoding='utf-8')
        if old not in text:
            raise SystemExit(f'negative mutation source token missing: {name}')
        main.write_text(text.replace(old, new, 1), encoding='utf-8')
        result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        if result.returncode == 0:
            raise SystemExit(f'negative mutation unexpectedly passed: {name}')

with tempfile.TemporaryDirectory(prefix='vulkanscope-3002-control-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    audit = root / 'rules/3.0.2_EXPRESSIVE_LOADING_COMPILE_AUDIT.md'
    audit.write_text(audit.read_text(encoding='utf-8') + '\nAdditional bounded evidence note.\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode != 0:
        print(result.stdout)
        raise SystemExit('documentation-only false-positive control failed')

print('PASS VulkanScope 3.0.2 negative mutations and documentation false-positive control')
