#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_kotlin_regex_compile_1003.py'

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
        result = run(dst)
        if result.returncode == 0:
            raise SystemExit(f'negative mutation was accepted: {label}')

mutate('app/src/main/java/com/efishell/vulkanscope/AdvancedAnalysis.kt', 'Regex("""^api\\s*>=\\s*([0-9]+\\.[0-9]+)$""", RegexOption.IGNORE_CASE)', 'Regex("^api\\s*>=\\s*([0-9]+\\.[0-9]+)$", RegexOption.IGNORE_CASE)', 'restore invalid API regex ordinary string')
mutate('app/src/main/java/com/efishell/vulkanscope/AdvancedAnalysis.kt', 'Regex("""^(.+?)(>=|<=|==|>|<)(-?[0-9]+(?:\\.[0-9]+)?)$""")', 'Regex("^(.+?)(>=|<=|==|>|<)(-?[0-9]+(?:\\.[0-9]+)?)$")', 'restore invalid limit matcher ordinary string')
mutate('app/src/main/java/com/efishell/vulkanscope/AdvancedAnalysis.kt', 'Regex("""-?[0-9]+(?:\\.[0-9]+)?""")', 'Regex("-?[0-9]+(?:\\.[0-9]+)?")', 'restore invalid numeric matcher ordinary string')
mutate('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'Regex("""VK_VERSION_(\\d+)_(\\d+)""")', 'Regex("VK_VERSION_(\\d+)_(\\d+)")', 'restore invalid VK_VERSION matcher ordinary string')
current_gradle=(root/'app/build.gradle.kts').read_text(encoding='utf-8')
import re
match=re.search(r'versionCode\s*=\s*(\d+)',current_gradle)
if not match: raise SystemExit('fixture token missing: invalid successor versionCode')
mutate('app/build.gradle.kts', f'versionCode = {match.group(1)}', 'versionCode = 9999', 'invalid successor versionCode')
with tempfile.TemporaryDirectory() as td:
    dst = Path(td) / 'repo'
    shutil.copytree(root, dst)
    p = dst / 'changelog.md'
    text = p.read_text(encoding='utf-8')
    if 'release-blocking Kotlin compilation errors' not in text:
        raise SystemExit('false-positive fixture token missing')
    p.write_text(text.replace('release-blocking Kotlin compilation errors', 'Kotlin compilation errors', 1), encoding='utf-8')
    result = run(dst)
    if result.returncode != 0:
        raise SystemExit('false-positive control failed')
print('PASS 1.0.3 Kotlin regex compile negative mutations and false-positive control')
