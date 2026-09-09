#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
verifier_rel = Path('tools/verify_semantic_icon_corrections_1017.py')

def replace_once(path, old, new):
    text = path.read_text(encoding='utf-8')
    if old not in text:
        raise AssertionError('mutation anchor missing: ' + old[:180])
    path.write_text(text.replace(old, new, 1), encoding='utf-8')

def run_case(name, mutate, should_fail=True):
    with tempfile.TemporaryDirectory(prefix='vs1017-mut-') as td:
        dst = Path(td) / 'root'
        shutil.copytree(root, dst, ignore=shutil.ignore_patterns('.gradle', 'build', '.cxx'))
        mutate(dst)
        result = subprocess.run([sys.executable, str(dst / verifier_rel), '--root', str(dst), '--skip-version'], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        failed = result.returncode != 0
        if failed != should_fail:
            print(result.stdout)
            raise AssertionError(f'{name}: expected fail={should_fail}, got fail={failed}')

mutations = [
    ('Semih identity returns code glyph', lambda d: replace_once(d/main_rel, 'ExpressiveIdentityBlock("Semih Boran", "EFI Shell · VulkanScope developer", R.drawable.ic_person)', 'ExpressiveIdentityBlock("Semih Boran", "EFI Shell · VulkanScope developer", R.drawable.ic_code)')),
    ('Developer header returns person glyph', lambda d: replace_once(d/main_rel, 'title.equals("Developer", true) -> R.drawable.ic_code', 'title.equals("Developer", true) -> R.drawable.ic_person')),
    ('Check update leaves shared ZIP glyph', lambda d: replace_once(d/main_rel, 'R.drawable.ic_zip_download, enabled = directUpdatesEnabled && networkAvailable, onClick = onCheckForUpdates', 'R.drawable.ic_check_updates, enabled = directUpdatesEnabled && networkAvailable, onClick = onCheckForUpdates')),
    ('Direct update status returns GitHub glyph', lambda d: replace_once(d/main_rel, 'painter = painterResource(R.drawable.ic_zip_download)', 'painter = painterResource(R.drawable.ic_action_github)')),
    ('Turnip import returns old import glyph', lambda d: replace_once(d/main_rel, 'icon = R.drawable.ic_zip_download', 'icon = R.drawable.ic_action_import')),
    ('Core arrows return gray', lambda d: replace_once(d/main_rel, 'ExpressiveFilterBar(sources, sources.indexOf(sourceFilter).coerceAtLeast(0), arrowTint = VulkanAccentSoft)', 'ExpressiveFilterBar(sources, sources.indexOf(sourceFilter).coerceAtLeast(0))')),
    ('HDR quick access returns plain display', lambda d: replace_once(d/main_rel, 'if (title == "HDR & Color")', 'if (false && title == "HDR & Color")')),
    ('Global search returns generic info', lambda d: replace_once(d/main_rel, 'title.equals("Global Vulkan report search", true) -> R.drawable.ic_search', 'title.equals("Global Vulkan report search", true) -> R.drawable.ic_info')),
    ('Presentation composite loses surface overlay', lambda d: replace_once(d/main_rel, 'title.equals("Surface + Display presentation evidence", true)', 'title.equals("Surface + Display presentation evidence disabled", true)')),
    ('Raw title rejoins technicalReport', lambda d: replace_once(d/main_rel, 'CapabilitySectionCard("Raw structured technical Report")', 'CapabilitySectionCard("Raw structured technicalReport")')),
    ('Raw JSON badge removed', lambda d: replace_once(d/main_rel, '"JSON",', '"JXON",')),
    ('Database compare composite loses database primary', lambda d: replace_once(d/main_rel, 'title.equals("Compare with VulkanScope Database", true) -> R.drawable.ic_action_database', 'title.equals("Compare with VulkanScope Database", true) -> R.drawable.ic_compare')),
    ('Database lookup loses compact mode', lambda d: replace_once(d/main_rel, '.addPathSegments("v1/reports/$id").addQueryParameter("compact", "1")', '.addPathSegments("v1/reports/$id")')),
    ('Database submission changes schema', lambda d: replace_once(d/main_rel, 'put("schemaVersion", 2)', 'put("schemaVersion", 3)')),
    ('Database canonical key changes', lambda d: replace_once(d/main_rel, 'put("technicalReport", technicalReportJson(context, report, display, mode))', 'put("technical_report", technicalReportJson(context, report, display, mode))')),
    ('Database transport ceiling removed', lambda d: replace_once(d/main_rel, 'if (payload.size > 2 * 1024 * 1024)', 'if (false)')),
    ('ZIP folder geometry removed', lambda d: replace_once(d/'app/src/main/res/drawable/ic_zip_download.xml', 'M3,7H7.4L9.2,9H14.2V19H3Z', 'M3,7H14V19H3Z')),
    ('launcher foreground black byte regression', lambda d: (d/'app/src/main/res/drawable-nodpi/vulkanscope_logo_foreground.png').write_bytes((root/'app/src/main/res/drawable-nodpi/vulkanscope_logo_foreground.png').read_bytes() + b'X')),
]

for name, mutate in mutations:
    run_case(name, mutate, True)
run_case('unrelated changelog wording', lambda d: (d/'changelog.md').write_text((d/'changelog.md').read_text(encoding='utf-8') + '\nUnrelated wording.\n', encoding='utf-8'), False)
print(f'PASS VulkanScope 1.0.17 negative mutations: {len(mutations)} defects rejected + false-positive control')
