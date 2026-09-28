#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_2113.py'
mutations = [
    (
        'drop zero-progress pager settle state',
        'CollectionPagerVisualState(registration, progress, overlayOffset, laneExtent, glassHeightPx)',
        'if (progress <= 0f) null else CollectionPagerVisualState(registration, progress, overlayOffset, laneExtent, glassHeightPx)',
    ),
    (
        'reintroduce independent pager position tween',
        '.offset { IntOffset(0, pagerVisual.overlayOffsetPx.toInt()) }',
        '.offset { IntOffset(0, animatedOverlayOffsetPx.toInt()) }',
    ),
    (
        'remove header real blur sampling',
        '.frostedChromeBackdrop(backdropLayer, headerRootOffset)',
        '.background(VulkanGlassTint)',
    ),
    (
        'remove bottom navigation real blur sampling',
        '.frostedChromeBackdrop(backdropLayer, navigationRootOffset)',
        '.background(VulkanGlassTint)',
    ),
    (
        'make pager translucent again',
        'color = VulkanPagerSurface,',
        'color = VulkanPagerSurface.copy(alpha = 0.68f),',
    ),
    (
        'allow pager join blur to repaint header',
        'clipRect(top = glassTop) {',
        'clipRect(top = 0f) {',
    ),
    (
        'remove crisp ordinary page replay',
        'drawLayer(chromeSourceLayer)',
        'drawLayer(chromeBlurredLayer)',
    ),
    (
        'restore unmatched overlay vertical padding',
        '.fillMaxWidth()\n                            .onSizeChanged',
        '.fillMaxWidth()\n                            .padding(top = 5.dp, bottom = 7.dp)\n                            .onSizeChanged',
    ),
]

for name, old, new in mutations:
    with tempfile.TemporaryDirectory(prefix='vulkanscope-2113-neg-') as temp:
        root = Path(temp) / 'tree'
        shutil.copytree(source, root)
        main = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
        text = main.read_text(encoding='utf-8')
        if old not in text:
            raise SystemExit(f'negative mutation source token missing: {name}: {old}')
        main.write_text(text.replace(old, new, 1), encoding='utf-8')
        result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        if result.returncode == 0:
            raise SystemExit(f'negative mutation unexpectedly passed: {name}')

with tempfile.TemporaryDirectory(prefix='vulkanscope-2113-control-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    audit = root / 'rules/2.1.13_UNIFIED_PAGER_FROSTED_BLUR_AUDIT.md'
    audit.write_text(audit.read_text(encoding='utf-8') + '\nAdditional bounded evidence note.\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode != 0:
        print(result.stdout)
        raise SystemExit('documentation-only false-positive control failed')

print('PASS VulkanScope 2.1.13 negative mutations and false-positive control')
