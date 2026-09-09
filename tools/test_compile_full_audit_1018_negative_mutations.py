#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_compile_full_audit_1018.py'

mutations = [
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'selectedIndex, onSelected = onSelected)', 'selectedIndex, onSelected)', 'restore positional filter callback'),
    ('app/src/main/AndroidManifest.xml', 'android:usesCleartextTraffic="false"', 'android:usesCleartextTraffic="true"', 'enable cleartext traffic'),
    ('app/src/main/AndroidManifest.xml', '<service android:name=".VulkanProbeService" android:exported="false"', '<service android:name=".VulkanProbeService" android:exported="true"', 'export probe service'),
    ('app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt', 'worker.shutdownNow()', 'worker.shutdown()', 'weaken deterministic worker teardown'),
    ('app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt', 'surface?.release()', 'surface?.isValid', 'remove service-owned Surface release'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'https://vulkanscope-database-api.vulkanscope.workers.dev', 'http://vulkanscope-database-api.vulkanscope.workers.dev', 'downgrade Database origin'),
    ('registry/registry_lock.json', '"registryRef": "1.4.362"', '"registryRef": "1.4.361"', 'drift Vulkan registry ref'),
    ('app/build.gradle.kts', 'versionCode = 1018', 'versionCode = 1017', 'stale versionCode'),
    ('app/build.gradle.kts', '"x86_64"', '"x86"', 'enable forbidden x86 ABI'),
    ('app/build.gradle.kts', 'isMinifyEnabled = true', 'isMinifyEnabled = false', 'disable release shrinking'),
]

def run(target):
    return subprocess.run([sys.executable, str(verifier), '--root', str(target)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)

for relative, old, new, label in mutations:
    with tempfile.TemporaryDirectory(prefix='vs1018mut_') as td:
        dst = Path(td) / 'repo'
        shutil.copytree(root, dst)
        path = dst / relative
        text = path.read_text(encoding='utf-8')
        if old not in text:
            raise SystemExit(f'fixture token missing for {label}: {old}')
        path.write_text(text.replace(old, new, 1), encoding='utf-8')
        if run(dst).returncode == 0:
            raise SystemExit(f'negative mutation accepted: {label}')

with tempfile.TemporaryDirectory(prefix='vs1018fp_') as td:
    dst = Path(td) / 'repo'
    shutil.copytree(root, dst)
    path = dst / 'changelog.md'
    text = path.read_text(encoding='utf-8')
    token = 'Keeps all 1.0.17 semantic icon and design corrections unchanged.'
    if token not in text:
        raise SystemExit('false-positive fixture token missing')
    path.write_text(text.replace(token, 'Keeps the 1.0.17 semantic icon and design corrections unchanged.', 1), encoding='utf-8')
    result = run(dst)
    if result.returncode != 0:
        raise SystemExit('false-positive control failed')
print(f'PASS VulkanScope 1.0.18 negative mutations: {len(mutations)} defects rejected + false-positive control')
