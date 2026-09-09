#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_paddingvalues_compile_0814.py'
current_gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
exact_0815 = 'versionCode = 815' in current_gradle

def run(target):
    return subprocess.run([sys.executable, str(verifier), '--root', str(target)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)

def mutate(path, old, new, label):
    with tempfile.TemporaryDirectory() as td:
        dst = Path(td) / 'repo'
        shutil.copytree(root, dst)
        p = dst / path
        text = p.read_text(encoding='utf-8')
        if old not in text:
            raise SystemExit(f'fixture token missing: {label}')
        p.write_text(text.replace(old, new, 1), encoding='utf-8')
        if run(dst).returncode == 0:
            raise SystemExit(f'negative mutation was accepted: {label}')

mutate('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'import androidx.compose.foundation.layout.fillMaxSize\n', 'import androidx.compose.foundation.layout.calculateTopPadding\nimport androidx.compose.foundation.layout.fillMaxSize\n', 'restore invalid top import')
mutate('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'import androidx.compose.foundation.layout.fillMaxSize\n', 'import androidx.compose.foundation.layout.calculateBottomPadding\nimport androidx.compose.foundation.layout.fillMaxSize\n', 'restore invalid bottom import')
mutate('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'navigationPadding.calculateTopPadding()', '0.dp', 'remove top member call')
mutate('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'navigationPadding.calculateBottomPadding()', '0.dp', 'remove bottom member call')
if exact_0815:
    mutate('app/build.gradle.kts', 'versionCode = 815', 'versionCode = 813', 'stale versionCode')
with tempfile.TemporaryDirectory() as td:
    dst = Path(td) / 'repo'
    shutil.copytree(root, dst)
    p = dst / 'changelog.md'
    p.write_text(p.read_text(encoding='utf-8').replace('release-blocking', 'build-blocking', 1), encoding='utf-8')
    result = run(dst)
    if result.returncode != 0:
        raise SystemExit('false-positive control failed')
print('PASS 0.80.14 PaddingValues compile negative mutations and false-positive control')
