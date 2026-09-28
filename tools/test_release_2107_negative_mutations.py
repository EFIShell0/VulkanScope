#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_2107.py'
mutations = [
    ('restore block mosaic marker', 'private fun Modifier.frostedHeaderBackdrop()', 'private fun Modifier.mosaicHeaderBackdrop()'),
    ('make header opaque', 'VulkanSurfaceRaised.copy(alpha = 0.90f)', 'VulkanSurfaceRaised.copy(alpha = 1.0f)'),
    ('generic sort trigger', 'fileManagerSortModeIcon(sortMode)', 'R.drawable.ic_sort'),
    ('generic sort rows', 'fileManagerSortModeIcon(candidate)', 'R.drawable.ic_sort'),
    ('remove keyed pager aggregation', 'mutableStateMapOf<String, Int>()', 'mutableMapOf<String, Int>()'),
    ('restore status-before-pager position', '.offset(y = headerContentInset + coordinatedPinnedPagerInset)', '.offset(y = headerContentInset)'),
    ('remove pager join offset', 'label = "stickyPagerHeaderJoinOffset"', 'label = "removedPagerJoinOffset"'),
    ('remove collection focus clear', 'requestPageChange(currentPage - 1)', 'onPageChange(currentPage - 1)'),
    ('remove filter number transition', 'label = "filterPageNumberTransition"', 'label = "removedFilterNumberTransition"'),
    ('remove trademark mark', 'SPIR-V™ shader-module', 'SPIR-V shader-module'),
]

for name, old, new in mutations:
    with tempfile.TemporaryDirectory(prefix='vulkanscope-2107-neg-') as temp:
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

with tempfile.TemporaryDirectory(prefix='vulkanscope-2107-icon-neg-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    a = root / 'app/src/main/res/drawable/ic_sort_name_asc.xml'
    b = root / 'app/src/main/res/drawable/ic_sort_name_desc.xml'
    b.write_bytes(a.read_bytes())
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode == 0:
        raise SystemExit('duplicate semantic sort icon unexpectedly passed')

with tempfile.TemporaryDirectory(prefix='vulkanscope-2107-control-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    audit = root / 'rules/2.1.7_GLASS_PAGER_FILE_ICONS_TRADEMARK_AUDIT.md'
    audit.write_text(audit.read_text(encoding='utf-8') + '\nControl evidence note.\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode != 0:
        print(result.stdout)
        raise SystemExit('documentation-only false-positive control failed')
print('PASS VulkanScope 2.1.7 negative mutations and false-positive control')
