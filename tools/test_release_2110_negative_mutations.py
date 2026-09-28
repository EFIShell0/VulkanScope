#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_2110.py'
mutations = [
    ('restore sticky anchor', 'item(key = pagerKey)', 'stickyHeader(key = pagerKey)'),
    ('remove live progress formula', '((headerBoundaryPx + transitionDistance - info.offset.toFloat()) / transitionDistance).coerceIn(0f, 1f)', '1f'),
    ('remove header clamp', 'val overlayOffset = naturalOffset.coerceAtLeast(headerBoundaryPx.toFloat())', 'val overlayOffset = naturalOffset'),
    ('remove glass progress coupling', '(pagerVisual.registration.heightPx + glassPaddingPx) * pagerVisual.progress', '(pagerVisual.registration.heightPx + glassPaddingPx).toFloat()'),
    ('remove normal crossfade', 'Modifier.zIndex(6f).alpha(1f - handoffProgress)', 'Modifier.zIndex(6f)'),
    ('remove overlay crossfade', '.alpha(pagerVisual.progress)', '.alpha(1f)'),
    ('remove retained blur layer', 'val blurredLayer = rememberGraphicsLayer()', 'val blurredLayer = sourceLayer'),
    ('remove blur render effect', 'AndroidRenderEffect.createBlurEffect(blurRadiusPx, blurRadiusPx, Shader.TileMode.CLAMP).asComposeRenderEffect()', 'null'),
]

for name, old, new in mutations:
    with tempfile.TemporaryDirectory(prefix='vulkanscope-2110-neg-') as temp:
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

with tempfile.TemporaryDirectory(prefix='vulkanscope-2110-control-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    audit = root / 'rules/2.1.10_PROGRESSIVE_GLASS_HANDOFF_AUDIT.md'
    audit.write_text(audit.read_text(encoding='utf-8') + '\nAdditional bounded evidence note.\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode != 0:
        print(result.stdout)
        raise SystemExit('documentation-only false-positive control failed')

print('PASS VulkanScope 2.1.10 negative mutations and false-positive control')
