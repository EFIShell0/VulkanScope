#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_release_2002.py'

def mutate_and_expect_failure(rel, old, new):
    with tempfile.TemporaryDirectory(prefix='vs2002-mutation-') as temp_name:
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
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'private const val BACKGROUND_PROBE_LANES = 4', 'private const val BACKGROUND_PROBE_LANES = 8'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'memoryClassMb <= 256 -> 2', 'memoryClassMb <= 256 -> 4'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'runBackgroundIsolatedProbe(group, index % activeBackgroundProbeLanes, modeSnapshot)', 'runIsolatedProbe(group, modeSnapshot)'),
    ('app/src/main/AndroidManifest.xml', 'android:name=".VulkanProbeServiceBg2" android:exported="false"', 'android:name=".VulkanProbeServiceBg2" android:exported="true"'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'top = 8.dp + topOverlayInset', 'top = 8.dp'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'if (!suppressDesktopQuickMenu) showQuickMenu = true', 'showQuickMenu = true'),
    ('app/src/main/cpp/vulkanscope.cpp', 'VK_API_VERSION_1_0', 'VK_API_VERSION_1_1'),
]
for mutation in mutations:
    mutate_and_expect_failure(*mutation)
print('PASS VulkanScope 2.0.2 targeted negative mutations')
