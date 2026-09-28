#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_2109.py'
mutations = [
    ('remove page overlay host', 'visible = activePinnedPager != null', 'visible = false'),
    ('remove visible offset threshold', 'info != null -> info.offset <= headerBoundaryPx', 'info != null -> false'),
    ('remove post-visibility fallback', 'firstIndex > registration.itemIndex -> true', 'firstIndex > registration.itemIndex -> false'),
    ('remove same-index offset fallback', 'firstIndex == registration.itemIndex -> firstOffset > 0', 'firstIndex == registration.itemIndex -> false'),
    ('remove stale-layout generation guard', '.filter { it.itemIndex >= 0 && it.heightPx > 0 && it.layoutItemCount == listState.layoutInfo.totalItemsCount }', '.filter { it.itemIndex >= 0 && it.heightPx > 0 }'),
    ('restore clipped sticky child offset', '.onSizeChanged { pagerHeightPx = it.height }', '.offset(y = headerContentInset).onSizeChanged { pagerHeightPx = it.height }'),
    ('remove system side insets', '.padding(start = 18.dp + horizontalNavigationStartInset, end = 18.dp + horizontalNavigationEndInset)', '.padding(start = 18.dp, end = 18.dp)'),
    ('show duplicate in-list pager', 'modifier = Modifier.zIndex(6f).alpha(if (pinned) 0f else 1f)', 'modifier = Modifier.zIndex(6f)'),
    ('remove overlay lane report', 'reportStickyPagerState(reporterKey, true, registration.heightPx)', 'reportStickyPagerState(reporterKey, false, 0)'),
]

for name, old, new in mutations:
    with tempfile.TemporaryDirectory(prefix='vulkanscope-2109-neg-') as temp:
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

with tempfile.TemporaryDirectory(prefix='vulkanscope-2109-control-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    audit = root / 'rules/2.1.9_PINNED_PAGER_OVERLAY_AUDIT.md'
    audit.write_text(audit.read_text(encoding='utf-8') + '\nAdditional evidence note.\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode != 0:
        print(result.stdout)
        raise SystemExit('documentation-only false-positive control failed')

print('PASS VulkanScope 2.1.9 negative mutations and false-positive control')
