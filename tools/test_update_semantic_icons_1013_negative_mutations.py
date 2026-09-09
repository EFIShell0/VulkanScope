#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
import re
from pathlib import Path

root = Path(__file__).resolve().parents[1]
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
verifier = Path('tools/verify_update_semantic_icons_1013.py')
gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
version_match = re.search(r'versionName\s*=\s*"1\.0\.(\d+)"', gradle)
release_minor = int(version_match.group(1)) if version_match else 0
compare_anchor = 'title.equals("Compare with VulkanScope Database", true) -> R.drawable.ic_action_database' if release_minor >= 17 else 'title.equals("Compare with VulkanScope Database", true) || title.equals("Database comparison summary", true) -> R.drawable.ic_compare'
compare_mutation = 'title.equals("Compare with VulkanScope Database", true) -> R.drawable.ic_compare' if release_minor >= 17 else 'title.equals("Compare with VulkanScope Database", true) || title.equals("Database comparison summary", true) -> R.drawable.ic_action_database'

def replace_once(path, old, new):
    text = path.read_text(encoding='utf-8')
    if old not in text: raise AssertionError('mutation anchor missing: ' + old[:180])
    path.write_text(text.replace(old, new, 1), encoding='utf-8')

def run_case(name, mutate, should_fail):
    with tempfile.TemporaryDirectory(prefix='vs1013-mut-') as tmp:
        dest = Path(tmp) / 'project'
        shutil.copytree(root, dest)
        mutate(dest)
        result = subprocess.run([sys.executable, str(dest / verifier), '--root', str(dest)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        failed = result.returncode != 0
        if failed != should_fail:
            print(result.stdout)
            raise AssertionError(f'{name}: expected fail={should_fail}, got fail={failed}')

mutations = [
    ('restore green Android palette', lambda d: replace_once(d / 'app/src/main/res/drawable/ic_android.xml', '#E2676A', '#34A853')),
    ('damage Android supplied geometry', lambda d: replace_once(d / 'app/src/main/res/drawable/ic_android.xml', 'M151.025,85.224', 'M150.025,85.224')),
    ('restore generic available icon', lambda d: replace_once(d / main_rel, 'painter = painterResource(R.drawable.ic_update_available)', 'painter = painterResource(R.drawable.ic_info)')),
    ('make Review plain text action', lambda d: replace_once(d / main_rel, 'ChevronAffordance("Review", "Review update") { onInstallUpdate(status.update) }', 'ExpressiveTextButton("Review") { onInstallUpdate(status.update) }')),
    ('remove Review chevron', lambda d: replace_once(d / main_rel, 'Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = contentDescription', 'Icon(painterResource(R.drawable.ic_info), contentDescription = contentDescription')),
    ('restore question icon in update dialog', lambda d: replace_once(d / main_rel, 'SemanticDialogTitle("Download VulkanScope ${update.version}?", R.drawable.ic_update_available)', 'QuestionDialogTitle("Download VulkanScope ${update.version}?")')),
    ('remove positive download icon', lambda d: replace_once(d / main_rel, 'ExpressivePrimaryIconTextButton("Download update", R.drawable.ic_download_update, enabled = networkAvailable, onClick = onConfirm)', 'ExpressivePrimaryButton("Download update", enabled = networkAvailable, onClick = onConfirm)')),
    ('remove update network gate', lambda d: replace_once(d / main_rel, 'R.drawable.ic_download_update, enabled = networkAvailable, onClick = onConfirm', 'R.drawable.ic_download_update, enabled = true, onClick = onConfirm')),
    ('remove Cancel X', lambda d: replace_once(d / main_rel, 'Icon(painterResource(R.drawable.ic_close), contentDescription = null, modifier = Modifier.size(17.dp))', 'Icon(painterResource(R.drawable.ic_info), contentDescription = null, modifier = Modifier.size(17.dp))')),
    ('conflate Database fetch icon', lambda d: replace_once(d / main_rel, 'R.drawable.ic_database_fetch', 'R.drawable.ic_action_database')),
    ('conflate Database submit icon', lambda d: replace_once(d / main_rel, 'R.drawable.ic_database_submit', 'R.drawable.ic_action_database')),
    ('conflate Database browse icon', lambda d: replace_once(d / main_rel, 'R.drawable.ic_database_browse', 'R.drawable.ic_action_database')),
    ('conflate Database compare section', lambda d: replace_once(d / main_rel, compare_anchor, compare_mutation)),
    ('conflate Database permalink section', lambda d: replace_once(d / main_rel, 'title.equals("Database permalink & QR", true) -> R.drawable.ic_qr', 'title.equals("Database permalink & QR", true) -> R.drawable.ic_action_database')),
    ('stale release identity', lambda d: replace_once(d / 'app/build.gradle.kts', 'versionCode = 1018' if 'versionCode = 1018' in (d / 'app/build.gradle.kts').read_text(encoding='utf-8') else ('versionCode = 1016' if 'versionCode = 1016' in (d / 'app/build.gradle.kts').read_text(encoding='utf-8') else ('versionCode = 1014' if 'versionCode = 1014' in (d / 'app/build.gradle.kts').read_text(encoding='utf-8') else 'versionCode = 1013')), 'versionCode = 1012')),
]
for name, mutate in mutations: run_case(name, mutate, True)
run_case('unrelated changelog wording', lambda d: (d / 'changelog.md').write_text((d / 'changelog.md').read_text(encoding='utf-8') + '\nUnrelated wording control.\n', encoding='utf-8'), False)
print(f'PASS VulkanScope 1.0.13 negative mutations: {len(mutations)} defects rejected + false-positive control')
