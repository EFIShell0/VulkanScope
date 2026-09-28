#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_2102.py'
mutations = [
    ('header_statusbar', 'shape = RoundedCornerShape(28.dp)', 'shape = RoundedCornerShape(4.dp)'),
    ('pager_keystroke', 'candidate.isEmpty() -> pageField = value', 'candidate.isEmpty() -> { pageField = value; onPageChange(currentPage) }'),
    ('dense_grid', 'TurnipFileManagerViewMode.DENSE_GRID -> 92.dp', 'TurnipFileManagerViewMode.DENSE_GRID -> 156.dp'),
    ('chooser', 'TurnipFileManagerViewMode.entries.chunked(3)', 'TurnipFileManagerViewMode.entries.chunked(2)'),
    ('search_fade', '.width(34.dp)\n                        .height(38.dp)', '.width(1.dp)\n                        .height(38.dp)'),
]
with tempfile.TemporaryDirectory(prefix='vs2102-neg-') as td:
    base = Path(td) / 'base'
    shutil.copytree(source, base, ignore=shutil.ignore_patterns('.gradle', 'build', '.cxx', '*.zip', '*.apk', 'MainActivity.kt.pre_refine'))
    for name, old, new in mutations:
        tree = Path(td) / name
        shutil.copytree(base, tree)
        main = tree / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
        data = main.read_text(encoding='utf-8')
        if old not in data:
            raise SystemExit(f'mutation anchor missing: {name}')
        main.write_text(data.replace(old, new, 1), encoding='utf-8')
        result = subprocess.run([sys.executable, str(verifier), '--root', str(tree)], capture_output=True, text=True)
        if result.returncode == 0:
            raise SystemExit(f'negative mutation escaped verifier: {name}')
    doc = Path(td) / 'doc_only'
    shutil.copytree(base, doc)
    (doc / 'BUILD_AUDIT.md').write_text((doc / 'BUILD_AUDIT.md').read_text(encoding='utf-8') + '\n2.1.2 verifier false-positive guard.\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(doc)], capture_output=True, text=True)
    if result.returncode != 0:
        raise SystemExit('documentation-only mutation incorrectly rejected\n' + result.stdout + result.stderr)
print('PASS VulkanScope 2.1.2 negative mutations and false-positive guard')
