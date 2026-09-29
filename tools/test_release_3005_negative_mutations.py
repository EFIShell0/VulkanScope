#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_3005.py'
mutations = [
    ('restore Turnip outer gray background', 'color = VulkanBlack,', 'color = VulkanSurfaceRaised,'),
    ('remove shared overlay host', 'LocalSharedStorageBrowserLauncher provides { request -> sharedStorageBrowserOverlay = request },', ''),
    ('make diagnostic milliseconds-only', '"%.3f s (%d ms)"', '"%d ms"'),
    ('remove Database list bound', '.distinctBy { it.id }.take(200)', '.distinctBy { it.id }'),
    ('restore outside View & sort dismissal', 'onDismissRequest = { },', 'onDismissRequest = { expanded = false },'),
]

targets = [
    'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt',
    'app/src/main/java/com/efishell/vulkanscope/AdvancedAnalysis.kt',
]
for name, old, new in mutations:
    with tempfile.TemporaryDirectory(prefix='vulkanscope-3005-neg-') as temp:
        root = Path(temp) / 'tree'
        shutil.copytree(source, root)
        changed = False
        for rel in targets:
            target = root / rel
            text = target.read_text(encoding='utf-8')
            if old in text:
                target.write_text(text.replace(old, new, 1), encoding='utf-8')
                changed = True
                break
        if not changed:
            raise SystemExit(f'negative mutation source token missing: {name}')
        result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        if result.returncode == 0:
            raise SystemExit(f'negative mutation unexpectedly passed: {name}')

with tempfile.TemporaryDirectory(prefix='vulkanscope-3005-protected-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    protected = root / 'app/src/main/AndroidManifest.xml'
    protected.write_bytes(protected.read_bytes() + b'\n')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode == 0:
        raise SystemExit('protected-file mutation unexpectedly passed')

with tempfile.TemporaryDirectory(prefix='vulkanscope-3005-control-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    audit = root / 'rules/3.0.5_FULLSCREEN_FILE_MANAGER_DIAGNOSTICS_DATABASE_AUDIT.md'
    audit.write_text(audit.read_text(encoding='utf-8') + '\nAdditional bounded documentation evidence.\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode != 0:
        print(result.stdout)
        raise SystemExit('documentation-only false-positive control failed')

print('PASS VulkanScope 3.0.5 negative mutations and documentation false-positive control')
