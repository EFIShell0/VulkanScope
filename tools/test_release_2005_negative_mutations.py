#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_release_2005.py'

def mutate_and_expect_failure(rel, old, new):
    with tempfile.TemporaryDirectory(prefix='vs2005-mutation-') as temp_name:
        temp = Path(temp_name) / 'root'
        shutil.copytree(root, temp)
        path = temp / rel
        text = path.read_text(encoding='utf-8')
        if old not in text:
            raise SystemExit(f'mutation source token missing: {rel}: {old}')
        path.write_text(text.replace(old, new, 1), encoding='utf-8')
        result = subprocess.run([sys.executable, str(verifier), '--root', str(temp)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        if result.returncode == 0:
            raise SystemExit(f'verifier accepted forbidden mutation: {rel}: {old} -> {new}')

mutations = [
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'import androidx.compose.ui.input.key.nativeKeyCode', 'import androidx.compose.ui.input.key.nativeKeyEvent'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'event.key == Key.DirectionCenter', 'event.key == Key.Back'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'delay(550L)', 'delay(0L)'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'tvLongPressJob?.cancel()', 'tvLongPressJob?.start()'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'tvLongPressConsumed = true', 'tvLongPressConsumed = false'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'private const val BACKGROUND_PROBE_LANES = 6', 'private const val BACKGROUND_PROBE_LANES = 8'),
    ('app/src/main/cpp/vulkanscope.cpp', 'VK_API_VERSION_1_0', 'VK_API_VERSION_1_1'),
]
for mutation in mutations:
    mutate_and_expect_failure(*mutation)
print('PASS VulkanScope 2.0.5 targeted negative mutations')
