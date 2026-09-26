#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_2001.py'

def run(root):
    return subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True).returncode

def mutate(name, rel, old, new, should_fail=True):
    with tempfile.TemporaryDirectory(prefix=f'vs2001-{name}-') as tmp:
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
    raise SystemExit('baseline 2.0.1 verifier does not pass')
mutate('version-rollback', 'app/build.gradle.kts', 'versionCode = 2001', 'versionCode = 2000')
mutate('session-group-cap-relaxed', 'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt', 'groups.size !in 1..16', 'groups.size !in 1..512')
mutate('timeout-cardinality-removed', 'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt', 'timeouts.size != groups.size', 'false')
mutate('cache-confinement-removed', 'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt', '!sessionDir.path.startsWith(cacheRoot.path + File.separator)', 'false')
mutate('per-group-timeout-weakened', 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'if (segmentGroups[index] in ISOLATED_ADVANCED_GROUPS.keys) 30_000L else 12_000L', '30_000L')
mutate('session-bypass', 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'runIsolatedProbeSession(newGroups, modeSnapshot, BACKGROUND_COLLECTION_BUDGET_MS)', 'ProbeSessionBatchResult(emptyMap(), emptyMap(), 0)')
mutate('crash-resume-removed', 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'offset += index + 1', 'offset += segmentGroups.size')
mutate('native-drift', 'app/src/main/cpp/vulkanscope.cpp', '#include', '#define VULKANSCOPE_NEGATIVE_MUTATION 1\n#include')
mutate('unrelated-audit-text', 'rules/2.0.1_SESSION_BASED_COLLECTION_AUDIT.md', '## Release scope', '## Release scope\n', should_fail=False)
print('PASS VulkanScope 2.0.1 negative-mutation suite')
