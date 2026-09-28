#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_3000.py'
mutations = [
    ('remove edge-to-edge call', '        enableEdgeToEdge()\n', ''),
    ('restore opaque navigation bar', 'window.navigationBarColor = Color.TRANSPARENT', 'window.navigationBarColor = android.graphics.Color.rgb(17, 17, 17)'),
    ('remove page underlap', 'private val PageChromeUnderlap = 10.dp', 'private val PageChromeUnderlap = 0.dp'),
    ('shorten opening rule', '.width(lineWidth + 20.dp)', '.width(lineWidth)'),
    ('remove preemptive overlay lead', 'private val OverlayCoordinationLead = 96.dp', 'private val OverlayCoordinationLead = 0.dp'),
    ('delay pager lane through coroutine', '    SideEffect {\n        val reporterKey = activeReporterKey', '    LaunchedEffect(activeReporterKey, activeLaneExtent) {\n        val reporterKey = activeReporterKey'),
    ('drop transient overlay z-order', '                            .zIndex(11f)\n', ''),
    ('drop system navigation glass host', '                    SystemNavigationBackdrop(\n', '                    DisabledNavigationGlass(\n'),
]

for name, old, new in mutations:
    with tempfile.TemporaryDirectory(prefix='vulkanscope-3000-neg-') as temp:
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

with tempfile.TemporaryDirectory(prefix='vulkanscope-3000-control-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    audit = root / 'rules/3.0.0_EDGE_TO_EDGE_CHROME_OVERLAY_AUDIT.md'
    audit.write_text(audit.read_text(encoding='utf-8') + '\nAdditional bounded evidence note.\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode != 0:
        print(result.stdout)
        raise SystemExit('documentation-only false-positive control failed')

print('PASS VulkanScope 3.0.0 negative mutations and documentation false-positive control')
