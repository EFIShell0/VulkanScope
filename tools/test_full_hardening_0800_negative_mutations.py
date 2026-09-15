#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_full_hardening_0800.py'
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')

def verify(tree):
    result = subprocess.run([sys.executable, str(verifier), '--root', str(tree)], text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    return result.returncode, result.stdout

def mutate(name, transform, should_fail=True):
    with tempfile.TemporaryDirectory(prefix=f'vs-0800-{name}-') as temp:
        tree = Path(temp) / 'tree'
        shutil.copytree(root, tree, ignore=shutil.ignore_patterns('.gradle', 'build', '__pycache__'))
        path = tree / main_rel
        text = path.read_text(encoding='utf-8')
        updated = transform(text)
        if updated == text:
            raise SystemExit(f'FAIL mutation did not change source: {name}')
        path.write_text(updated, encoding='utf-8')
        code, output = verify(tree)
        if should_fail and code == 0:
            raise SystemExit(f'FAIL verifier accepted targeted mutation: {name}\n{output}')
        if not should_fail and code != 0:
            raise SystemExit(f'FAIL false-positive control rejected: {name}\n{output}')

mutate('unbounded-archive', lambda s: s.replace('ZipInputStream(BoundedDriverArchiveInputStream(input, TURNIP_ARCHIVE_INPUT_MAX_BYTES)).use { zip ->', 'ZipInputStream(input).use { zip ->', 1))
mutate('archive-limit-drift', lambda s: s.replace('private const val TURNIP_ARCHIVE_INPUT_MAX_BYTES = 96L * 1024L * 1024L', 'private const val TURNIP_ARCHIVE_INPUT_MAX_BYTES = Long.MAX_VALUE', 1))
mutate('terminal-validation-bypass', lambda s: s.replace('return strictProbeTerminalCandidate(candidate, group == "base")', 'return true', 1))
mutate('checkpoint-full-read', lambda s: s.replace('if (crashDetected()) continue', 'readFileTextLimited(resultFile, maxProbeResultBytes.toInt())\n                                if (crashDetected()) continue', 1))
mutate('unrelated-text', lambda s: s.replace('Driver manager is busy.', 'Driver manager is currently busy.', 1), should_fail=False)
print('PASS 0.80.0 full hardening negative mutations')
