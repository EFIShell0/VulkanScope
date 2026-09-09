#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_compile_role_0812.py'
current_gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
successor = 'versionName = "0.80.12"' not in current_gradle

def run(candidate, *extra):
    return subprocess.run([sys.executable, str(verifier), '--root', str(candidate), *extra], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)

def mutate_and_expect_failure(relative, old, new, label, skip_version=False):
    with tempfile.TemporaryDirectory(prefix='vulkanscope-0812-negative-') as td:
        candidate = Path(td) / 'tree'
        shutil.copytree(root, candidate)
        path = candidate / relative
        source = path.read_text(encoding='utf-8')
        if old not in source:
            raise SystemExit(f'mutation source missing for {label}')
        path.write_text(source.replace(old, new, 1), encoding='utf-8')
        result = run(candidate, *(['--skip-version'] if skip_version else []))
        if result.returncode == 0:
            raise SystemExit(f'negative mutation unexpectedly passed: {label}')

current = run(root, *(['--skip-version'] if successor else []))
if current.returncode != 0:
    raise SystemExit(current.stdout)
mutate_and_expect_failure('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'import androidx.compose.ui.semantics.role\n', '', 'missing role extension import', True)
mutate_and_expect_failure('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'Modifier.semantics { role = Role.Button }', 'Modifier.semantics { }', 'lost explicit button role', True)
if not successor:
    mutate_and_expect_failure('app/build.gradle.kts', 'versionCode = 812', 'versionCode = 810', 'stale versionCode')
with tempfile.TemporaryDirectory(prefix='vulkanscope-0812-control-') as td:
    candidate = Path(td) / 'tree'
    shutil.copytree(root, candidate)
    path = candidate / 'changelog.md'
    path.write_text(path.read_text(encoding='utf-8') + '\nUnrelated documentation control.\n', encoding='utf-8')
    result = run(candidate, *(['--skip-version'] if successor else []))
    if result.returncode != 0:
        raise SystemExit('false-positive control failed:\n' + result.stdout)
print('PASS 0.80.12 Compose role negative mutations and false-positive control')
