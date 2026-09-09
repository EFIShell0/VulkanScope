#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
verifier_rel = Path('tools/verify_info_memory_database_ui_1016.py')

def replace_once(path, old, new):
    text = path.read_text(encoding='utf-8')
    if old not in text:
        raise AssertionError('mutation anchor missing: ' + old[:180])
    path.write_text(text.replace(old, new, 1), encoding='utf-8')

def run_case(name, mutate, should_fail=True):
    with tempfile.TemporaryDirectory(prefix='vs1016-mut-') as td:
        dst = Path(td) / 'root'
        shutil.copytree(root, dst, ignore=shutil.ignore_patterns('.gradle', 'build', '.cxx'))
        mutate(dst)
        result = subprocess.run([sys.executable, str(dst / verifier_rel), '--root', str(dst)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        failed = result.returncode != 0
        if failed != should_fail:
            print(result.stdout)
            raise AssertionError(f'{name}: expected fail={should_fail}, got fail={failed}')

mutations = [
    ('Application section returns generic app glyph', lambda d: replace_once(d/main_rel, 'title.equals("Application", true) -> R.drawable.vulkanscope_scope_wordmark', 'title.equals("Application", true) -> R.drawable.ic_app')),
    ('Application identity returns generic app glyph', lambda d: replace_once(d/main_rel, 'Image(painter = painterResource(R.drawable.vulkanscope_logo_foreground)', 'Image(painter = painterResource(R.drawable.ic_app)')),
    ('Developer section returns person glyph', lambda d: replace_once(d/main_rel, 'title.equals("Developer", true) -> R.drawable.ic_code', 'title.equals("Developer", true) -> R.drawable.ic_person')),
    ('Developer identity returns person glyph', lambda d: replace_once(d/main_rel, 'ExpressiveIdentityBlock("Semih Boran", "EFI Shell · VulkanScope developer", R.drawable.ic_code)', 'ExpressiveIdentityBlock("Semih Boran", "EFI Shell · VulkanScope developer", R.drawable.ic_person)')),
    ('Check for updates returns tray glyph', lambda d: replace_once(d/main_rel, 'R.drawable.ic_check_updates, enabled = directUpdatesEnabled && networkAvailable, onClick = onCheckForUpdates', 'R.drawable.ic_download_update, enabled = directUpdatesEnabled && networkAvailable, onClick = onCheckForUpdates')),
    ('Check for updates line restored', lambda d: replace_once(d/'app/src/main/res/drawable/ic_check_updates.xml', 'M12,4v11M8.5,11.5L12,15l3.5,-3.5', 'M12,4v11M8.5,11.5L12,15l3.5,-3.5M5,17h14')),
    ('Memory heaps reuses generic memory', lambda d: replace_once(d/main_rel, 'title.equals("Memory heaps", true) -> R.drawable.ic_memory_heap', 'title.equals("Memory heaps", true) -> R.drawable.ic_memory')),
    ('Memory types reuses heap icon', lambda d: replace_once(d/main_rel, 'title.equals("Memory types", true) -> R.drawable.ic_memory_type', 'title.equals("Memory types", true) -> R.drawable.ic_memory_heap')),
    ('Self-tests returns flask icon', lambda d: replace_once(d/main_rel, 'R.drawable.ic_self_test, enabled = !state.testRunning', 'R.drawable.ic_test, enabled = !state.testRunning')),
    ('Database lookup loses compact mode', lambda d: replace_once(d/main_rel, '.addPathSegments("v1/reports/$id").addQueryParameter("compact", "1")', '.addPathSegments("v1/reports/$id")')),
    ('Database submission changes schema', lambda d: replace_once(d/main_rel, 'put("schemaVersion", 2)', 'put("schemaVersion", 3)')),
    ('Database POST route changes', lambda d: replace_once(d/main_rel, '.addPathSegments("v1/reports").build()', '.addPathSegments("v2/reports").build()')),
    ('Database transport ceiling removed', lambda d: replace_once(d/main_rel, 'if (payload.size > 2 * 1024 * 1024)', 'if (false)')),
    ('SCOPE artwork mutated', lambda d: (d/'app/src/main/res/drawable-nodpi/vulkanscope_scope_wordmark.png').write_bytes((d/'app/src/main/res/drawable-nodpi/vulkanscope_scope_wordmark.png').read_bytes() + b'X')),
    ('stale release identity', lambda d: replace_once(d/'app/build.gradle.kts', 'versionCode = 1016', 'versionCode = 1015')),
]

for name, mutate in mutations:
    run_case(name, mutate, True)
run_case('unrelated changelog wording', lambda d: (d/'changelog.md').write_text((d/'changelog.md').read_text(encoding='utf-8') + '\nUnrelated wording.\n', encoding='utf-8'), False)
print(f'PASS VulkanScope 1.0.16 negative mutations: {len(mutations)} defects rejected + false-positive control')
