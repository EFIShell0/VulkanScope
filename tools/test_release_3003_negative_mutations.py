#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_3003.py'
mutations = [
    ('remove minimum load confirmation', 'MinimumProfileConfirmation(MinimumProfileConfirmationKind.LOAD, entry.key)', 'MinimumProfileConfirmation(MinimumProfileConfirmationKind.SAVE, entry.key)'),
    ('restore outside menu dismissal', 'onDismissRequest = { },', 'onDismissRequest = { expanded = false },'),
    ('shorten copy success feedback', 'delay(3000L)', 'delay(500L)'),
    ('remove graph vertical scrolling', '.verticalScroll(verticalState)', ''),
    ('remove TV grid fallback declaration', 'private fun Modifier.tvRemoteLazyGridNavigation(state: LazyGridState): Modifier', 'private fun Modifier.removedTvGridNavigation(state: LazyGridState): Modifier'),
]

for name, old, new in mutations:
    with tempfile.TemporaryDirectory(prefix='vulkanscope-3003-neg-') as temp:
        root = Path(temp) / 'tree'
        shutil.copytree(source, root)
        targets = [
            root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt',
            root / 'app/src/main/java/com/efishell/vulkanscope/VulkanDependencyGraph.kt',
        ]
        changed = False
        for target in targets:
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

with tempfile.TemporaryDirectory(prefix='vulkanscope-3003-protected-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    protected = root / 'app/src/main/AndroidManifest.xml'
    protected.write_bytes(protected.read_bytes() + b'\n')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode == 0:
        raise SystemExit('protected-file mutation unexpectedly passed')

with tempfile.TemporaryDirectory(prefix='vulkanscope-3003-control-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    audit = root / 'rules/3.0.3_ANALYSIS_FILE_MANAGER_TV_AUDIT.md'
    audit.write_text(audit.read_text(encoding='utf-8') + '\nAdditional bounded documentation evidence.\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode != 0:
        print(result.stdout)
        raise SystemExit('documentation-only false-positive control failed')

print('PASS VulkanScope 3.0.3 negative mutations and documentation false-positive control')
