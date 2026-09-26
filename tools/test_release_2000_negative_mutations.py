#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_2000.py'

def run(root):
    return subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True).returncode

def mutate(name, rel, old, new, should_fail=True):
    with tempfile.TemporaryDirectory(prefix=f'vs2000-{name}-') as tmp:
        root = Path(tmp) / 'tree'
        shutil.copytree(source, root)
        path = root / rel
        text = path.read_text(encoding='utf-8')
        if old not in text:
            raise SystemExit(f'mutation token not found for {name}')
        path.write_text(text.replace(old, new, 1), encoding='utf-8')
        failed = run(root) != 0
        if failed != should_fail:
            raise SystemExit(f'negative mutation expectation failed: {name}, verifier_failed={failed}, expected={should_fail}')

if run(source) != 0:
    raise SystemExit('baseline 2.0.0 verifier does not pass')
mutate('version-rollback', 'app/build.gradle.kts', 'versionCode = 2000', 'versionCode = 1505')
mutate('timing-path-relaxed', 'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt', 'requestedTiming.path != requestedResult.path + ".timing"', 'false')
mutate('timing-bound-relaxed', 'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt', '16 * 1024', '64 * 1024')
mutate('timing-cleanup-removed', 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'timingFile.delete()', 'Unit')
mutate('native-drift', 'app/src/main/cpp/vulkanscope.cpp', '#include', '#define VULKANSCOPE_NEGATIVE_MUTATION 1\n#include')
mutate('unrelated-audit-text', 'rules/2.0.0_COLLECTION_TIMING_INSTRUMENTATION_AUDIT.md', '## Release scope', '## Release scope\n', should_fail=False)
print('PASS VulkanScope 2.0.0 negative-mutation suite')
