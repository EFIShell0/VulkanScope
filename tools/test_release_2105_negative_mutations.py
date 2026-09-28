#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_2105.py'
mutations = [
    ('opaque header', 'VulkanSurfaceRaised.copy(alpha = 0.48f)', 'VulkanSurfaceRaised.copy(alpha = 0.92f)'),
    ('global scaffold padding', 'Box(Modifier.fillMaxSize()) {', 'Box(Modifier.fillMaxSize().padding(padding)) {'),
    ('large app back', 'buttonSize = 36.dp', 'buttonSize = 42.dp'),
    ('old logo active', 'R.drawable.vulkanscope_logo_horizontal_aligned', 'R.drawable.vulkanscope_logo_horizontal_balanced'),
    ('opaque sticky backing', 'Surface(color = ComposeColor.Transparent, contentColor = VulkanTextPrimary)', 'Surface(color = VulkanBlack.copy(alpha = 0.96f), contentColor = VulkanTextPrimary)'),
    ('remove pinned motion', 'headerContentInset + transientOverlayContentInset + 56.dp', '0.dp'),
    ('remove shared layout persistence', 'shared_storage_file_manager_view_mode', 'shared_storage_file_manager_layout_removed'),
    ('remove unified menu heading', 'Text("View & sort"', 'Text("Options"'),
    ('remove leading overflow gate', 'val showLeadingFade = focused && textOverflows', 'val showLeadingFade = focused'),
    ('restore opaque pager card', 'VulkanSurfaceLow.copy(alpha = 0.78f)', 'VulkanSurfaceLow'),
]

for name, old, new in mutations:
    with tempfile.TemporaryDirectory(prefix='vulkanscope-2105-neg-') as temp:
        root = Path(temp) / 'tree'
        shutil.copytree(source, root)
        main = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
        text = main.read_text(encoding='utf-8')
        if old not in text:
            raise SystemExit(f'negative mutation source token missing: {name}: {old}')
        main.write_text(text.replace(old, new), encoding='utf-8')
        result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        if result.returncode == 0:
            raise SystemExit(f'negative mutation unexpectedly passed: {name}')

with tempfile.TemporaryDirectory(prefix='vulkanscope-2105-control-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    audit = root / 'rules/2.1.5_TRANSPARENT_HEADER_FILE_MANAGER_PAGER_AUDIT.md'
    audit.write_text(audit.read_text(encoding='utf-8') + '\nControl evidence note.\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode != 0:
        print(result.stdout)
        raise SystemExit('documentation-only false-positive control failed')
print('PASS VulkanScope 2.1.5 negative mutations and false-positive control')
