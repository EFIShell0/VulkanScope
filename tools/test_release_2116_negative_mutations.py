#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_2116.py'
mutations = [
    ('drop direct viewport-start conversion', '(direct.offset - layoutInfo.viewportStartOffset).toFloat()', 'direct.offset.toFloat()'),
    ('drop previous bridge viewport-start conversion', '(previous.offset + previous.size + verticalSpacingPx - layoutInfo.viewportStartOffset).toFloat()', '(previous.offset + previous.size + verticalSpacingPx).toFloat()'),
    ('drop next bridge viewport-start conversion', '(next.offset - registration.heightPx - verticalSpacingPx - layoutInfo.viewportStartOffset).toFloat()', '(next.offset - registration.heightPx - verticalSpacingPx).toFloat()'),
    ('restore whole-pager offset animation marker', 'val overlayOffset = resolvedNaturalOffset.coerceAtLeast(headerBoundaryPx.toFloat())', 'val animatedOverlayOffsetPx = resolvedNaturalOffset\n                        val overlayOffset = resolvedNaturalOffset.coerceAtLeast(headerBoundaryPx.toFloat())'),
]

for name, old, new in mutations:
    with tempfile.TemporaryDirectory(prefix='vulkanscope-2116-neg-') as temp:
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

with tempfile.TemporaryDirectory(prefix='vulkanscope-2116-control-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    audit = root / 'rules/2.1.16_LAZY_VIEWPORT_PAGER_ALIGNMENT_AUDIT.md'
    audit.write_text(audit.read_text(encoding='utf-8') + '\nAdditional bounded evidence note.\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode != 0:
        print(result.stdout)
        raise SystemExit('documentation-only false-positive control failed')

print('PASS VulkanScope 2.1.16 negative mutations and documentation false-positive control')
