#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_2106.py'
mutations = [
    ('remove mosaic modifier', '.mosaicHeaderBackdrop()', '.alpha(1f)'),
    ('make header opaque', 'VulkanSurfaceRaised.copy(alpha = 0.76f)', 'VulkanSurfaceRaised.copy(alpha = 1.0f)'),
    ('restore popup heading', 'contentDescription = "Close view and sort menu"', 'contentDescription = "View & sort"'),
    ('remove pager index memory', 'var pagerIndex by remember(pagerKey) { mutableIntStateOf(-1) }', 'var pagerIndex by remember(pagerKey) { mutableIntStateOf(0) }'),
    ('remove omitted-item fallback', 'state.firstVisibleItemIndex > pagerIndex', 'false'),
    ('remove pager z-order', 'Modifier.zIndex(3f)', 'Modifier'),
    ('restore whole-pager transition marker', 'CollectionPager(totalItems = totalItems, currentPage = currentPage, onPageChange = onPageChange, pageSize = pageSize)', 'AnimatedContent(targetState = currentPage, label = "collectionPagerTransition") { CollectionPager(totalItems = totalItems, currentPage = it, onPageChange = onPageChange, pageSize = pageSize) }'),
    ('remove page-number transition', 'label = "pageNumberTransition"', 'label = "numberTransitionRemoved"'),
    ('remove coordinated top inset', 'label = "coordinatedTopOverlayInset"', 'label = "topInsetRemoved"'),
]

for name, old, new in mutations:
    with tempfile.TemporaryDirectory(prefix='vulkanscope-2106-neg-') as temp:
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

with tempfile.TemporaryDirectory(prefix='vulkanscope-2106-control-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    audit = root / 'rules/2.1.6_FROSTED_HEADER_OVERLAY_COORDINATION_AUDIT.md'
    audit.write_text(audit.read_text(encoding='utf-8') + '\nControl evidence note.\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode != 0:
        print(result.stdout)
        raise SystemExit('documentation-only false-positive control failed')
print('PASS VulkanScope 2.1.6 negative mutations and false-positive control')
