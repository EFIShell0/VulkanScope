#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_3001.py'
mutations = [
    ('restore opening shrink', '            phase >= 2 -> 1f\n', '            phase >= 4 -> 0.58f\n            phase >= 3 -> 0.88f\n            phase >= 2 -> 1f\n'),
    ('remove page separation', 'private val PageChromeSeparation = 12.dp', 'private val PageChromeSeparation = 0.dp'),
    ('restore page underlap', 'val pageTopContentInset = headerContentInset + PageChromeSeparation', 'val pageTopContentInset = (headerContentInset - PageChromeSeparation).coerceAtLeast(0.dp)'),
    ('disable landscape split', 'val landscapeLayout = maxWidth > maxHeight && maxWidth >= 700.dp', 'val landscapeLayout = false'),
    ('starve landscape browser height', 'modifier = Modifier.weight(0.58f).fillMaxHeight()', 'modifier = Modifier.weight(0.58f).height(120.dp)'),
    ('move secondary interception later', 'awaitPointerEvent(PointerEventPass.Initial)', 'awaitPointerEvent(PointerEventPass.Main)'),
    ('drop app secondary guard', 'Box(Modifier.fillMaxSize().consumeDesktopSecondaryMouseInput(suppressDesktopSecondaryInput))', 'Box(Modifier.fillMaxSize())'),
    ('remove animated hold border', 'val pressBorderAlpha by animateFloatAsState(if (pressed) 0.46f else 0f, tween(140, easing = FastOutSlowInEasing), label = "evidenceHoldBorder")', 'val pressBorderAlpha = 0f'),
]

for name, old, new in mutations:
    with tempfile.TemporaryDirectory(prefix='vulkanscope-3001-neg-') as temp:
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

with tempfile.TemporaryDirectory(prefix='vulkanscope-3001-control-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    audit = root / 'rules/3.0.1_UI_INPUT_LAYOUT_AUDIT.md'
    audit.write_text(audit.read_text(encoding='utf-8') + '\nAdditional bounded evidence note.\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode != 0:
        print(result.stdout)
        raise SystemExit('documentation-only false-positive control failed')

print('PASS VulkanScope 3.0.1 negative mutations and documentation false-positive control')
