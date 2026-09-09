#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_semantic_ui_refinement_1019.py'
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')

def replace_once(path, old, new):
    text = path.read_text(encoding='utf-8')
    if old not in text:
        raise SystemExit(f'mutation fixture missing in {path}: {old[:90]}')
    path.write_text(text.replace(old, new, 1), encoding='utf-8')

def run(dst):
    return subprocess.run([sys.executable, str(verifier), '--root', str(dst)], cwd=dst, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)

mutations = [
    ('check update returns ZIP glyph', lambda d: replace_once(d/main_rel, 'R.drawable.ic_download, enabled = directUpdatesEnabled && networkAvailable, onClick = onCheckForUpdates', 'R.drawable.ic_zip_download, enabled = directUpdatesEnabled && networkAvailable, onClick = onCheckForUpdates')),
    ('Updates section returns ZIP glyph', lambda d: replace_once(d/main_rel, 'title.equals("Updates", true) -> R.drawable.ic_download', 'title.equals("Updates", true) -> R.drawable.ic_zip_download')),
    ('update status returns ZIP glyph', lambda d: replace_once(d/main_rel, 'painter = painterResource(R.drawable.ic_download)', 'painter = painterResource(R.drawable.ic_zip_download)')),
    ('horizontal container returns gray', lambda d: replace_once(d/main_rel, 'containerColor = VulkanAccentContainer,\n                contentColor = VulkanTextPrimary', 'containerColor = VulkanSurfaceTonal,\n                contentColor = VulkanTextPrimary')),
    ('horizontal chevron returns tinted', lambda d: replace_once(d/main_rel, 'contentColor = VulkanTextPrimary,\n                disabledContainerColor = VulkanAccentContainer.copy(alpha = 0.72f)', 'contentColor = arrowTint,\n                disabledContainerColor = VulkanAccentContainer.copy(alpha = 0.72f)')),
    ('Queue safety loses shield', lambda d: replace_once(d/main_rel, 'painter = painterResource(R.drawable.ic_shield)', 'painter = painterResource(R.drawable.ic_queues)')),
    ('Export complete loses Surface primary', lambda d: replace_once(d/main_rel, 'title.equals("Export complete report", true) -> R.drawable.ic_surface', 'title.equals("Export complete report", true) -> R.drawable.ic_export')),
    ('TXT badge renamed', lambda d: replace_once(d/main_rel, 'title.equals("Export TXT", true) -> "TXT"', 'title.equals("Export TXT", true) -> "TEXT"')),
    ('HTML badge renamed', lambda d: replace_once(d/main_rel, 'title.equals("Export HTML", true) -> "HTML"', 'title.equals("Export HTML", true) -> "WEB"')),
    ('Explore returns home', lambda d: replace_once(d/main_rel, 'title.equals("Explore", true) -> R.drawable.ic_compass', 'title.equals("Explore", true) -> R.drawable.ic_home')),
    ('Turnip import loses ZIP semantics', lambda d: replace_once(d/main_rel, 'icon = R.drawable.ic_zip_download', 'icon = R.drawable.ic_download')),
    ('stale versionCode', lambda d: replace_once(d/'app/build.gradle.kts', 'versionCode = 1019', 'versionCode = 1018')),
]

for label, mutation in mutations:
    with tempfile.TemporaryDirectory() as td:
        dst = Path(td) / 'tree'
        shutil.copytree(root, dst)
        mutation(dst)
        result = run(dst)
        if result.returncode == 0:
            raise SystemExit(f'negative mutation was not rejected: {label}')

with tempfile.TemporaryDirectory() as td:
    dst = Path(td) / 'tree'
    shutil.copytree(root, dst)
    path = dst / 'changelog.md'
    text = path.read_text(encoding='utf-8')
    path.write_text(text + '\n', encoding='utf-8')
    result = run(dst)
    if result.returncode != 0:
        raise SystemExit('false-positive control failed: ' + result.stdout)

print(f'PASS VulkanScope 1.0.19 negative mutations: {len(mutations)} defects rejected + false-positive control')
