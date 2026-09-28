#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_2112.py'
mutations = [
    ('remove glass clip', '.height(glassHeight)\n                        .clipToBounds()', '.height(glassHeight)'),
    ('restore overlay crossfade', '.offset { IntOffset(0, pagerVisual.overlayOffsetPx.toInt()) }\n                        .zIndex(9f)', '.offset { IntOffset(0, pagerVisual.overlayOffsetPx.toInt()) }\n                        .alpha(pagerVisual.progress)\n                        .zIndex(9f)'),
    ('render second pager in lazy anchor', 'Spacer(\n            Modifier\n                .fillMaxWidth()', 'CollectionPager(totalItems, currentPage, onPageChange, pageSize)\n        Spacer(\n            Modifier\n                .fillMaxWidth()'),
    ('drop explicit no-pager cleanup', 'item(key = "$pagerKey-cleanup")', 'item(key = "$pagerKey-disabled")'),
    ('remove measured-height feedback', 'onMeasuredHeight = updateMeasuredHeight', 'onMeasuredHeight = { }'),
    ('stop clearing zero pager lane', 'reportStickyPagerState(reporterKey, activeLaneExtent > 0, activeLaneExtent)', 'if (activeLaneExtent > 0) reportStickyPagerState(reporterKey, true, activeLaneExtent)'),
    ('remove animated scroll-hint stack', 'val scrollHintTopInset by animateDpAsState(', 'val scrollHintTopInset = animateDpAsState('),
    ('reintroduce header hairline', '    drawContent()\n}\n\n@OptIn(ExperimentalMaterial3Api::class)', '    drawContent()\n    drawRect(ComposeColor.White.copy(alpha = 0.045f), topLeft = Offset(0f, size.height - 1f), size = Size(size.width, 1f))\n}\n\n@OptIn(ExperimentalMaterial3Api::class)'),
]

for name, old, new in mutations:
    with tempfile.TemporaryDirectory(prefix='vulkanscope-2112-neg-') as temp:
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

with tempfile.TemporaryDirectory(prefix='vulkanscope-2112-control-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    audit = root / 'rules/2.1.12_SINGLE_PAGER_CLIPPED_GLASS_AUDIT.md'
    audit.write_text(audit.read_text(encoding='utf-8') + '\nAdditional bounded evidence note.\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode != 0:
        print(result.stdout)
        raise SystemExit('documentation-only false-positive control failed')

print('PASS VulkanScope 2.1.12 negative mutations and false-positive control')
