#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_2108.py'

with tempfile.TemporaryDirectory(prefix='vulkanscope-2108-neg-') as temp:
    base = Path(temp) / 'base'
    shutil.copytree(source, base, ignore=shutil.ignore_patterns('.gradle', 'build', '.cxx', '*.zip', '*.apk', '__pycache__'))
    broken = Path(temp) / 'broken'
    shutil.copytree(base, broken)
    main = broken / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
    data = main.read_text(encoding='utf-8')
    old = '''                if (pinned && heightPx > 0) pinnedPagerHeights[key] = heightPx else pinnedPagerHeights.remove(key)\n                Unit\n            }'''
    new = '''                if (pinned && heightPx > 0) pinnedPagerHeights[key] = heightPx else pinnedPagerHeights.remove(key)\n            }'''
    if old not in data:
        raise SystemExit('compile-regression mutation anchor missing')
    main.write_text(data.replace(old, new, 1), encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(broken)], capture_output=True, text=True)
    if result.returncode == 0:
        raise SystemExit('non-Unit sticky reporter mutation escaped verifier')

    control = Path(temp) / 'control'
    shutil.copytree(base, control)
    build_audit = control / 'BUILD_AUDIT.md'
    build_audit.write_text(build_audit.read_text(encoding='utf-8') + '\n2.1.8 documentation-only false-positive control.\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(control)], capture_output=True, text=True)
    if result.returncode != 0:
        raise SystemExit('documentation-only mutation incorrectly rejected\n' + result.stdout + result.stderr)

print('PASS VulkanScope 2.1.8 negative mutation and false-positive control')
