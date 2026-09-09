#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_kotlin_default_parameter_compile_1005.py'
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')

def run(target):
    return subprocess.run([sys.executable, str(verifier), '--root', str(target)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)

def mutate_occurrence(index, label):
    with tempfile.TemporaryDirectory(prefix='vs1005mut_') as td:
        dst = Path(td) / 'repo'
        shutil.copytree(root, dst)
        path = dst / main_rel
        text = path.read_text(encoding='utf-8')
        good = 'ExpressiveCancelButton(onClick = onDismiss)' if any(f'versionName = "{name}"' in (dst / 'app/build.gradle.kts').read_text(encoding='utf-8') for name in ['1.0.8', '1.0.9', '1.0.10', '1.0.11', '1.0.12', '1.0.13', '1.0.14', '1.0.15', '1.0.16', '1.0.17', '1.0.18']) else 'ExpressiveTextButton("Cancel", onClick = onDismiss)'
        positions = [i for i in range(len(text)) if text.startswith(good, i)]
        if len(positions) != 2:
            raise SystemExit('fixture does not contain exactly two compile-safe Cancel calls')
        pos = positions[index]
        bad = 'ExpressiveCancelButton(onDismiss)' if good.startswith('ExpressiveCancelButton') else 'ExpressiveTextButton("Cancel", onDismiss)'
        text = text[:pos] + bad + text[pos + len(good):]
        path.write_text(text, encoding='utf-8')
        result = run(dst)
        if result.returncode == 0:
            raise SystemExit(f'negative mutation was accepted: {label}')

mutate_occurrence(0, 'restore positional callback in direct-update consent dialog')
mutate_occurrence(1, 'restore positional callback in update-confirmation dialog')
with tempfile.TemporaryDirectory(prefix='vs1005mut_') as td:
    dst = Path(td) / 'repo'
    shutil.copytree(root, dst)
    path = dst / 'app/build.gradle.kts'
    text = path.read_text(encoding='utf-8')
    import re
    match = re.search(r'versionCode\s*=\s*(\d+)', text)
    if not match:
        raise SystemExit('versionCode fixture missing')
    path.write_text(text.replace(f'versionCode = {match.group(1)}', 'versionCode = 1005', 1), encoding='utf-8')
    if run(dst).returncode == 0:
        raise SystemExit('negative mutation was accepted: stale versionCode')
with tempfile.TemporaryDirectory(prefix='vs1005fp_') as td:
    dst = Path(td) / 'repo'
    shutil.copytree(root, dst)
    path = dst / 'rules/1.0.5_KOTLIN_DEFAULT_PARAMETER_COMPILE_FIX_AUDIT.md'
    text = path.read_text(encoding='utf-8')
    token = 'No component signature, UI state, update behavior or Vulkan/report logic changes.'
    if token not in text:
        raise SystemExit('false-positive fixture token missing')
    path.write_text(text.replace(token, 'No component signature or runtime Vulkan/report behavior changes.', 1), encoding='utf-8')
    result = run(dst)
    if result.returncode != 0:
        raise SystemExit('false-positive control failed')
print('PASS 1.0.5 Kotlin default-parameter compile negative mutations and false-positive control')
