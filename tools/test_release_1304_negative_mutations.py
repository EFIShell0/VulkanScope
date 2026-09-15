#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = 'tools/verify_release_1304.py'
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
manifest_rel = Path('app/src/main/AndroidManifest.xml')

def replace_once(path, old, new):
    text = path.read_text(encoding='utf-8')
    if old not in text:
        raise RuntimeError(f'mutation anchor missing: {old}')
    path.write_text(text.replace(old, new, 1), encoding='utf-8')

def run_mutation(name, mutate, expect_fail=True):
    with tempfile.TemporaryDirectory() as tmp:
        dst = Path(tmp) / 'tree'
        shutil.copytree(root, dst)
        mutate(dst)
        result = subprocess.run([sys.executable, str(dst / verifier), '--root', str(dst)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        failed = result.returncode != 0
        if failed != expect_fail:
            print(result.stdout)
            raise SystemExit(f'{name}: expected fail={expect_fail}, got fail={failed}')

run_mutation('remove all-files permission', lambda d: replace_once(
    d / manifest_rel,
    '    <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />\n',
    ''
))
run_mutation('weaken traversal rejection', lambda d: replace_once(
    d / main_rel,
    'segments.any { it == "." || it == ".." }',
    'segments.any { it == "." }'
))
run_mutation('weaken schema validation', lambda d: replace_once(
    d / main_rel,
    'if (schemaVersion != 1) error("Unsupported schema")',
    'if (schemaVersion < 0) error("Unsupported schema")'
))
run_mutation('remove selection cap', lambda d: replace_once(
    d / main_rel,
    'if (selected.size >= state.maxSelectable) {',
    'if (false) {'
))
run_mutation('move folder scan to UI dispatcher', lambda d: replace_once(
    d / main_rel,
    'withContext(Dispatchers.IO) { scanTurnipFileManagerDirectory(root, target) }',
    'scanTurnipFileManagerDirectory(root, target)'
))
run_mutation('reintroduce SAF', lambda d: replace_once(
    d / main_rel,
    'private fun requestDriverBundleImport() {',
    'private val legacySafProbe = ActivityResultContracts.OpenDocument()\n\n    private fun requestDriverBundleImport() {'
))
run_mutation('unrelated wording false-positive control', lambda d: replace_once(
    d / main_rel,
    'No Vulkan® report',
    'No Vulkan® report available'
), expect_fail=False)

print('PASS VulkanScope 1.3.4 negative mutations and false-positive control')
