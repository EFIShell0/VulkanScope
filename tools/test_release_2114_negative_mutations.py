#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_2114.py'
mutations = [
    ('lose measured height across lazy disposal', 'val storedPagerHeightPx = measuredHeightLookup(pagerKey)', 'val storedPagerHeightPx = 0'),
    ('drop previous-item landing bridge', 'val previous = visibleItems.firstOrNull { it.index == registration.itemIndex - 1 }', 'val previous = null'),
    ('drop next-item landing bridge', 'val next = visibleItems.firstOrNull { it.index == registration.itemIndex + 1 }', 'val next = null'),
    ('keep overlay alive outside join region', 'if (progress <= 0f) {\n                            null', 'if (progress < 0f) {\n                            null'),
    ('show in-flow pager while overlay owns it', 'if (activeOverlayKey == pagerKey) {', 'if (false) {'),
    ('mismatch pager blur radius', 'val blurRadiusPx = with(density) { 24.dp.toPx() }', 'val blurRadiusPx = with(density) { 30.dp.toPx() }'),
    ('reintroduce varying pager tint', 'color = VulkanGlassTint,', 'color = VulkanGlassTint.copy(alpha = 0.55f),'),
    ('shrink glass before pager bottom', 'val glassHeightPx = pagerBottom.toInt().coerceAtLeast(headerBoundaryPx)', 'val glassHeightPx = (headerBoundaryPx + (pagerBottom - headerBoundaryPx) * progress).toInt()'),
]

for name, old, new in mutations:
    with tempfile.TemporaryDirectory(prefix='vulkanscope-2114-neg-') as temp:
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

with tempfile.TemporaryDirectory(prefix='vulkanscope-2114-control-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    audit = root / 'rules/2.1.14_PAGER_LANDING_UNIFIED_GLASS_AUDIT.md'
    audit.write_text(audit.read_text(encoding='utf-8') + '\nAdditional bounded evidence note.\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode != 0:
        print(result.stdout)
        raise SystemExit('documentation-only false-positive control failed')

print('PASS VulkanScope 2.1.14 negative mutations and documentation false-positive control')
