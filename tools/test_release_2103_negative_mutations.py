#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_2103.py'
mutations = [
    ('page_size', 'private const val COLLECTION_PAGE_SIZE = 25', 'private const val COLLECTION_PAGE_SIZE = 50'),
    ('invalid_page', '(candidate.toIntOrNull() ?: 0) in 1..pageCount', 'candidate.length <= pageCount.toString().length'),
    ('main_fade', 'userScrollEnabled = true,\n            content = content', 'userScrollEnabled = true,\n            content = content\n        )\n        SoftScrollIntersectionShadows(true, true, Modifier.fillMaxSize()'),
    ('nav_inset', '16.dp + startSystemInset', '16.dp'),
    ('sort_mode', 'CREATED_NEWEST, CREATED_OLDEST', 'CREATED_NEWEST'),
    ('dialog_border', 'modifier = Modifier.border(1.dp, VulkanAccentSoft.copy(alpha = 0.46f), MaterialTheme.shapes.extraLarge)', 'modifier = Modifier'),
    ('search_leading', 'if (textOverflows && !focused)', 'if (textOverflows && focused)'),
]
with tempfile.TemporaryDirectory(prefix='vs2103-neg-') as td:
    base = Path(td) / 'base'
    shutil.copytree(source, base, ignore=shutil.ignore_patterns('.gradle', 'build', '.cxx', '*.zip', '*.apk', '__pycache__'))
    for name, old, new in mutations:
        tree = Path(td) / name
        shutil.copytree(base, tree)
        main = tree / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
        data = main.read_text(encoding='utf-8')
        if old not in data:
            raise SystemExit(f'mutation anchor missing: {name}')
        main.write_text(data.replace(old, new) if name == 'dialog_border' else data.replace(old, new, 1), encoding='utf-8')
        result = subprocess.run([sys.executable, str(verifier), '--root', str(tree)], capture_output=True, text=True)
        if result.returncode == 0:
            raise SystemExit(f'negative mutation escaped verifier: {name}')
    doc = Path(td) / 'doc_only'
    shutil.copytree(base, doc)
    audit = doc / 'BUILD_AUDIT.md'
    audit.write_text(audit.read_text(encoding='utf-8') + '\n2.1.3 false-positive control.\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(doc)], capture_output=True, text=True)
    if result.returncode != 0:
        raise SystemExit('documentation-only mutation incorrectly rejected\n' + result.stdout + result.stderr)
print('PASS VulkanScope 2.1.3 negative mutations and false-positive guard')
