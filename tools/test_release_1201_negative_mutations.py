#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_release_1201.py'
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
gradle_rel = Path('app/build.gradle.kts')

def run_case(old, new, rel, expect_pass):
    with tempfile.TemporaryDirectory(prefix='vs1201-neg-') as name:
        temp = Path(name)
        for item in [main_rel, gradle_rel]:
            target = temp / item
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(root / item, target)
        target = temp / rel
        text = target.read_text(encoding='utf-8')
        if old not in text:
            raise SystemExit(f'mutation source missing: {old}')
        target.write_text(text.replace(old, new, 1), encoding='utf-8')
        result = subprocess.run([sys.executable, str(verifier), '--root', str(temp)], text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        if (result.returncode == 0) != expect_pass:
            print(result.stdout)
            raise SystemExit(f'unexpected verifier result for mutation {old}')

run_case('versionName = "1.2.1"', 'versionName = "1.2.0"', gradle_rel, False)
run_case('DRIVER_UPDATES("Driver & Update Preferences"', 'DRIVER_UPDATES("Settings"', main_rel, False)
run_case('initialSection: SettingsSection = SettingsSection.DRIVER_UPDATES', 'initialSection: SettingsSection = SettingsSection.INFO', main_rel, False)
run_case('updateStatusHideJob = null\n        updateCheckInFlight = true\n        updateCheckJob = activityScope.launch', 'updateStatusHideJob = null\n        updateCheckJob = activityScope.launch', main_rel, False)
run_case('updateCheckInFlight = false\n            }', 'updateCheckInFlight = true\n            }', main_rel, False)
run_case('enabled = directUpdatesEnabled && networkAvailable && !updateCheckInFlight', 'enabled = directUpdatesEnabled && networkAvailable', main_rel, False)
run_case('Vulkan capability and device inspection utility', 'Vulkan device and capability inspection utility', main_rel, True)
print('PASS VulkanScope 1.2.1 negative mutations and unrelated false-positive control')
