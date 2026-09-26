#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_release_2003.py'

def mutate_and_expect_failure(rel, old, new):
    with tempfile.TemporaryDirectory(prefix='vs2003-mutation-') as temp_name:
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
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'activityManager.isLowRamDevice || memoryInfo.lowMemory -> 2', 'false -> 2'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'val nextGroup = java.util.concurrent.atomic.AtomicInteger(0)', 'val nextGroup = java.util.concurrent.atomic.AtomicInteger(1)'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'runBackgroundIsolatedProbe(group, lane, modeSnapshot)', 'runIsolatedProbe(group, modeSnapshot)'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'if (initialPids.isEmpty() && !stopRequested) return true', 'if (initialPids.isEmpty() && !stopRequested) delay(150L)'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'timingMap["probe_total/$group"] = hostElapsedMs', 'timingMap["probe_total/$group"] = scheduledElapsedMs'),
    ('app/src/main/AndroidManifest.xml', 'android:name=".VulkanProbeServiceBg3" android:exported="false"', 'android:name=".VulkanProbeServiceBg3" android:exported="true"'),
    ('app/src/main/cpp/vulkanscope.cpp', 'VK_API_VERSION_1_0', 'VK_API_VERSION_1_1'),
]
for mutation in mutations:
    mutate_and_expect_failure(*mutation)
print('PASS VulkanScope 2.0.3 targeted negative mutations')
