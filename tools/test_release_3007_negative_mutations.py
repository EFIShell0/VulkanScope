#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_3007.py'
main_rel = 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
mutations = [
    ('restore generic Database icon', 'VendorLogo(\n                                    vendorId = vendorIdFromDisplay(reportRow.vendorId),\n                                    modifier = Modifier.size(46.dp)\n                                )', 'Icon(painterResource(R.drawable.ic_action_database), contentDescription = null, modifier = Modifier.size(46.dp))'),
    ('remove Turnip horizontal search expansion', 'expandHorizontally(tween(240, easing = FastOutSlowInEasing), expandFrom = Alignment.End)', 'fadeIn(tween(240))'),
    ('make Quality score opaque', 'val score = (100 - deductedPoints).coerceAtLeast(0)', 'val score = 100'),
    ('change fixed queue deduction', '"Queue-family enumeration safety",\n            15,', '"Queue-family enumeration safety",\n            5,'),
    ('remove detailed self-test summary', 'CapabilitySectionCard("Test result summary")', 'CapabilitySectionCard("Self-test result")'),
]

for name, old, new in mutations:
    with tempfile.TemporaryDirectory(prefix='vulkanscope-3007-neg-') as temp:
        root = Path(temp) / 'tree'
        shutil.copytree(source, root)
        target = root / main_rel
        text = target.read_text(encoding='utf-8')
        if old not in text:
            raise SystemExit(f'negative mutation source token missing: {name}')
        target.write_text(text.replace(old, new, 1), encoding='utf-8')
        result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        if result.returncode == 0:
            raise SystemExit(f'negative mutation unexpectedly passed: {name}')

with tempfile.TemporaryDirectory(prefix='vulkanscope-3007-protected-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    protected = root / 'app/src/main/cpp/vulkanscope.cpp'
    protected.write_bytes(protected.read_bytes() + b'\n')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode == 0:
        raise SystemExit('protected native mutation unexpectedly passed')

with tempfile.TemporaryDirectory(prefix='vulkanscope-3007-control-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    audit = root / 'rules/3.0.7_DATABASE_FILE_MANAGER_QUALITY_TESTS_AUDIT.md'
    audit.write_text(audit.read_text(encoding='utf-8') + '\nAdditional bounded documentation evidence.\n', encoding='utf-8')
    files = sorted(path.relative_to(root).as_posix() for path in root.rglob('*') if path.is_file())
    (root / 'files.txt').write_text('\n'.join(files) + '\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode != 0:
        print(result.stdout)
        raise SystemExit('documentation-only false-positive control failed')

print('PASS VulkanScope 3.0.7 negative mutations and documentation false-positive control')
