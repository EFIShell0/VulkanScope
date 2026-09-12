#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_release_1205.py'
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
gradle_rel = Path('app/build.gradle.kts')

def make_tree(temp):
    for rel in [main_rel, gradle_rel]:
        dst = temp / rel
        dst.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(root / rel, dst)

def mutate_text(old, new, rel=main_rel, expect_pass=False):
    with tempfile.TemporaryDirectory(prefix='vs1205-neg-') as name:
        temp = Path(name)
        make_tree(temp)
        target = temp / rel
        text = target.read_text(encoding='utf-8')
        if old not in text:
            raise SystemExit(f'mutation source missing: {old}')
        target.write_text(text.replace(old, new, 1), encoding='utf-8')
        result = subprocess.run([sys.executable, str(verifier), '--root', str(temp)], text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        if (result.returncode == 0) != expect_pass:
            print(result.stdout)
            raise SystemExit(f'unexpected verifier result for mutation: {old}')

mutate_text('versionName = "1.2.5"', 'versionName = "1.2.4"', gradle_rel)
mutate_text('targetState = selectedSection', 'targetState = null')
mutate_text('initialState == null && targetState != null', 'initialState == null && targetState == null')
mutate_text('slideInHorizontally(tween(durationMillis = 280)) { it / 8 }', 'slideInHorizontally(tween(durationMillis = 0)) { 0 }')
mutate_text('fadeIn(tween(durationMillis = 220))', 'fadeIn(tween(durationMillis = 0))')
mutate_text('slideOutHorizontally(tween(durationMillis = 180)) { -it / 12 }', 'slideOutHorizontally(tween(durationMillis = 0)) { 0 }')
mutate_text('label = "settingsSectionTransition"', 'label = ""')
mutate_text('SettingsSection.INFO -> InfoPage(', 'SettingsSection.INFO -> EmptyState(')
mutate_text('SettingsSection.REPORTS -> InfoPage(', 'SettingsSection.REPORTS -> EmptyState(')
mutate_text('SettingsSection.DRIVER_UPDATES -> DriverUpdatePreferencesPage(', 'SettingsSection.DRIVER_UPDATES -> EmptyState(')
mutate_text('Vulkan capability and device inspection utility', 'Vulkan capability/device inspection utility', expect_pass=True)
print('PASS VulkanScope 1.2.5 negative mutations and unrelated false-positive control')
