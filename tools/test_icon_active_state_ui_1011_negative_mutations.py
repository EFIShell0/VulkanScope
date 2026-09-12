#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
import re
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_icon_active_state_ui_1011.py'
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
version_match = re.search(r'versionName\s*=\s*"(\d+)\.(\d+)\.(\d+)"', gradle)
current_version = tuple(map(int, version_match.groups())) if version_match else (0, 0, 0)
release_minor = current_version[2] if current_version[:2] == (1, 0) else (99 if current_version >= (1, 1, 0) else 0)
updates_mapping = 'title.equals("Updates", true) || title.equals("Update preferences", true) -> R.drawable.ic_download' if current_version >= (1, 2, 0) else f'title.equals("Updates", true) -> {"R.drawable.ic_zip_download" if release_minor >= 17 else "R.drawable.ic_download_update"}'
developer_icon = 'R.drawable.ic_person' if release_minor >= 17 else ('R.drawable.ic_code' if release_minor >= 16 else 'R.drawable.ic_person')
check_updates_anchor = ('R.drawable.ic_download, enabled = directUpdatesEnabled && networkAvailable && !updateCheckInFlight, trailingIcon = R.drawable.ic_receive, onClick = onCheckForUpdates' if current_version >= (1, 2, 2) else 'R.drawable.ic_receive, enabled = directUpdatesEnabled && networkAvailable && !updateCheckInFlight, trailingIcon = R.drawable.ic_receive, onClick = onCheckForUpdates') if current_version >= (1, 2, 0) else f'{"R.drawable.ic_zip_download" if release_minor >= 17 else ("R.drawable.ic_check_updates" if release_minor >= 16 else "R.drawable.ic_download_update")}, enabled = directUpdatesEnabled && networkAvailable, onClick = onCheckForUpdates'

def replace_once(path, old, new):
    text = path.read_text(encoding='utf-8')
    if old not in text:
        raise AssertionError('mutation anchor missing: ' + old[:160])
    path.write_text(text.replace(old, new, 1), encoding='utf-8')

def run_case(name, mutate, should_fail=True):
    with tempfile.TemporaryDirectory(prefix='vs1011mut_') as td:
        dst = Path(td) / 'root'
        shutil.copytree(root, dst, ignore=shutil.ignore_patterns('.gradle', 'build', '.cxx'))
        mutate(dst)
        result = subprocess.run([sys.executable, str(dst / 'tools/verify_icon_active_state_ui_1011.py'), '--root', str(dst)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        if should_fail and result.returncode == 0:
            raise AssertionError(name + ': verifier accepted defect\n' + result.stdout)
        if not should_fail and result.returncode != 0:
            raise AssertionError(name + ': false-positive control failed\n' + result.stdout)

mutations = [
    ('baseline loses semantic icon', lambda d: replace_once(d / main_rel, 'ExpressiveContainedIconTextButton("Use as baseline", R.drawable.ic_baseline, modifier = Modifier.weight(1f))', 'ExpressiveContainedTextButton("Use as baseline")')),
    ('baseline loses equal width', lambda d: replace_once(d / main_rel, 'R.drawable.ic_baseline, modifier = Modifier.weight(1f)', 'R.drawable.ic_baseline')),
    ('Turnip selected ignores active mode', lambda d: replace_once(d / main_rel, 'activeMode == DriverMode.TURNIP && slot == activeSlot', 'slot == activeSlot')),
    ('driver manager stops observing mode', lambda d: replace_once(d / main_rel, 'LaunchedEffect(turnipManagerRevision, turnipSupport, turnipManagerBusy, mode)', 'LaunchedEffect(turnipManagerRevision, turnipSupport, turnipManagerBusy)')),
    ('Turnip table keeps stale selected state', lambda d: replace_once(d / main_rel, 'val driver = if (activeMode == DriverMode.TURNIP) rawDriver else rawDriver.copy(selected = false)', 'val driver = rawDriver')),
    ('Overview arrow returns muted', lambda d: replace_once(d / main_rel, 'contentDescription = "Open $title", tint = VulkanAccentSoft, modifier = Modifier.size(20.dp)', 'contentDescription = "Open $title", tint = VulkanTextMuted, modifier = Modifier.size(20.dp)')),
    ('Encyclopedia page icon returns generic info', lambda d: replace_once(d / main_rel, 'Page.Encyclopedia -> R.drawable.ic_book', 'Page.Encyclopedia -> R.drawable.ic_info')),
    ('Android section icon returns generic properties', lambda d: replace_once(d / main_rel, 'title.equals("Android", true) || title.equals("Android runtime", true) || title.equals("Operating system", true) -> R.drawable.ic_android', 'title.equals("Android", true) || title.equals("Android runtime", true) || title.equals("Operating system", true) -> R.drawable.ic_properties')),
    ('Updates section loses update glyph', lambda d: replace_once(d / main_rel, updates_mapping, 'title.equals("Updates", true) || title.equals("Update preferences", true) -> R.drawable.ic_info' if current_version >= (1, 2, 0) else 'title.equals("Updates", true) -> R.drawable.ic_info')),
    ('developer identity returns info glyph', lambda d: replace_once(d / main_rel, f'ExpressiveIdentityBlock("Semih Boran", "EFI Shell · VulkanScope developer", {developer_icon})', 'ExpressiveIdentityBlock("Semih Boran", "EFI Shell · VulkanScope developer", R.drawable.ic_info)')),
    ('Check for updates returns refresh glyph', lambda d: replace_once(d / main_rel, check_updates_anchor, ('R.drawable.ic_action_update, enabled = directUpdatesEnabled && networkAvailable && !updateCheckInFlight, trailingIcon = R.drawable.ic_action_update, onClick = onCheckForUpdates' if current_version >= (1, 2, 0) else 'R.drawable.ic_action_update, enabled = directUpdatesEnabled && networkAvailable, onClick = onCheckForUpdates'))),
    ('Encyclopedia action uses database glyph', lambda d: replace_once(d / main_rel, 'ExpressiveActionButton("Open in Encyclopedia", "Open the closest Vulkan symbol/reference token", R.drawable.ic_book', 'ExpressiveActionButton("Open in Encyclopedia", "Open the closest Vulkan symbol/reference token", R.drawable.ic_action_database')),
    ('remove Android semantic drawable', lambda d: (d / 'app/src/main/res/drawable/ic_android.xml').unlink()),
    ('stale version code', lambda d: replace_once(d / 'app/build.gradle.kts', re.search(r'versionCode\s*=\s*(\d+)', (d / 'app/build.gradle.kts').read_text(encoding='utf-8')).group(0), 'versionCode = 1010')),
]

for name, mutate in mutations:
    run_case(name, mutate, True)
run_case('unrelated changelog wording', lambda d: (d / 'changelog.md').write_text((d / 'changelog.md').read_text(encoding='utf-8') + '\nUnrelated retained release-history wording.\n', encoding='utf-8'), False)
print(f'PASS VulkanScope 1.0.11 negative mutations: {len(mutations)} defects rejected + false-positive control')
