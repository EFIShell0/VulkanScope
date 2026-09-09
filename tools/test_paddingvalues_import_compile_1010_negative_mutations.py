#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
import re
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_paddingvalues_import_compile_1010.py'

def run(target):
    return subprocess.run([sys.executable, str(verifier), '--root', str(target)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)

def mutate(path, old, new, label):
    with tempfile.TemporaryDirectory() as td:
        dst = Path(td) / 'repo'
        shutil.copytree(root, dst)
        target = dst / path
        text = target.read_text(encoding='utf-8')
        if old not in text:
            raise SystemExit(f'fixture token missing: {label}')
        target.write_text(text.replace(old, new, 1), encoding='utf-8')
        result = run(dst)
        if result.returncode == 0:
            raise SystemExit(f'negative mutation was accepted: {label}')

mutate('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'import androidx.compose.foundation.layout.PaddingValues\n', '', 'remove PaddingValues import')
mutate('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'import androidx.compose.foundation.layout.PaddingValues', 'import androidx.compose.foundation.layout.Padding', 'replace PaddingValues import with wrong symbol')
mutate('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)', 'contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 8.dp)', 'drift contained button content padding')
current_gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
code_match = re.search(r'versionCode\s*=\s*(\d+)', current_gradle)
name_match = re.search(r'versionName\s*=\s*"([^"]+)"', current_gradle)
if not code_match or not name_match:
    raise SystemExit('retained version fixture missing')
mutate('app/build.gradle.kts', f'versionCode = {code_match.group(1)}', 'versionCode = 999', 'invalid retained versionCode')
mutate('app/build.gradle.kts', f'versionName = "{name_match.group(1)}"', 'versionName = "0.0.1"', 'invalid retained versionName')
with tempfile.TemporaryDirectory() as td:
    dst = Path(td) / 'repo'
    shutil.copytree(root, dst)
    changelog = dst / 'changelog.md'
    changelog.write_text(changelog.read_text(encoding='utf-8') + '\nUnrelated retained history wording.\n', encoding='utf-8')
    result = run(dst)
    if result.returncode != 0:
        raise SystemExit('false-positive control failed: ' + result.stdout.strip())
print('PASS VulkanScope 1.0.10 PaddingValues import negative mutations and false-positive control')
