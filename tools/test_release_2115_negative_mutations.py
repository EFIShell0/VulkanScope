#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_2115.py'
mutations = [
    ('restore second in-flow pager', '        Spacer(\n            Modifier\n                .fillMaxWidth()\n                .height(with(density) { pagerHeightPx.toDp() })\n        )', '        CollectionPager(totalItems, currentPage, onPageChange, pageSize)'),
    ('drop authoritative direct offset', 'direct != null -> direct.offset.toFloat()', 'direct != null -> headerBoundaryPx.toFloat()'),
    ('restore join-region pager discard', 'val overlayOffset = resolvedNaturalOffset.coerceAtLeast(headerBoundaryPx.toFloat())', 'if (progress <= 0f) return@mapNotNull null\n                        val overlayOffset = resolvedNaturalOffset.coerceAtLeast(headerBoundaryPx.toFloat())'),
    ('drop page blur source reporting', 'if (pageRootOffset.y <= 0.5f) reportChromeBackdrop(blurredLayer, pageRootOffset) else reportChromeBackdrop(blurredLayer, null)', 'Unit'),
    ('make header ignore page blur', 'val headerBackdrop = pageChromeBackdrop ?: ChromeBackdropSource(chromeBlurredLayer, Offset.Zero)', 'val headerBackdrop = ChromeBackdropSource(chromeBlurredLayer, Offset.Zero)'),
    ('break backdrop root mapping', 'translate(backdropRootOffset.x - targetRootOffset.x, backdropRootOffset.y - targetRootOffset.y)', 'translate(-targetRootOffset.x, -targetRootOffset.y)'),
    ('mismatch join blur radius', 'val blurRadiusPx = with(density) { 24.dp.toPx() }', 'val blurRadiusPx = with(density) { 30.dp.toPx() }'),
    ('make glass height independent of scroll', '(headerBoundaryPx + transitionDistance * progress).toInt().coerceAtLeast(headerBoundaryPx)', '(headerBoundaryPx + transitionDistance).toInt().coerceAtLeast(headerBoundaryPx)'),
]

for name, old, new in mutations:
    with tempfile.TemporaryDirectory(prefix='vulkanscope-2115-neg-') as temp:
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

with tempfile.TemporaryDirectory(prefix='vulkanscope-2115-control-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    audit = root / 'rules/2.1.15_SINGLE_PAGER_CONTINUOUS_GLASS_AUDIT.md'
    audit.write_text(audit.read_text(encoding='utf-8') + '\nAdditional bounded evidence note.\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode != 0:
        print(result.stdout)
        raise SystemExit('documentation-only false-positive control failed')

print('PASS VulkanScope 2.1.15 negative mutations and documentation false-positive control')
