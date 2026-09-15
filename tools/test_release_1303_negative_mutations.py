#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = 'tools/verify_release_1303.py'
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
manifest_rel = Path('app/src/main/AndroidManifest.xml')

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

def replace_once(path, old, new):
    text = path.read_text(encoding='utf-8')
    if old not in text:
        raise RuntimeError(f'mutation anchor missing: {old}')
    path.write_text(text.replace(old, new, 1), encoding='utf-8')

run_mutation('reintroduce OpenDocument SAF token', lambda d: replace_once(
    d / main_rel,
    'private fun requestDriverBundleImport() {',
    'private val removedSafProbe = ActivityResultContracts.OpenDocument()\n\n    private fun requestDriverBundleImport() {'
))
run_mutation('reintroduce Analysis fallback type', lambda d: replace_once(
    d / main_rel,
    'private data class AnalysisWorkspaceModel(',
    'private class FallbackAnalysisDialogData\n\nprivate data class AnalysisWorkspaceModel('
))
run_mutation('reintroduce broad storage permission', lambda d: replace_once(
    d / manifest_rel,
    '<uses-permission android:name="android.permission.INTERNET" />',
    '<uses-permission android:name="android.permission.INTERNET" />\n    <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />'
))
run_mutation('reenable one TXT export action', lambda d: replace_once(
    d / main_rel,
    'R.drawable.ic_action_text, Modifier.fillMaxWidth(), false, true) { }',
    'R.drawable.ic_action_text, Modifier.fillMaxWidth(), true, true) { }'
))
run_mutation('unrelated permitted wording control', lambda d: replace_once(
    d / main_rel,
    'No Vulkan® report',
    'No Vulkan® report available'
), expect_fail=False)

print('PASS VulkanScope 1.3.3 negative mutations and false-positive control')
