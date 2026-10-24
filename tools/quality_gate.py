#!/usr/bin/env python3
import subprocess
import sys
from pathlib import Path
ROOT = Path(__file__).resolve().parents[1]
for command in (
    [sys.executable, '-B', 'tools/verify_release_source.py'],
    [sys.executable, '-B', 'tools/verify_github_docs.py'],
    [sys.executable, '-B', 'tools/verify_cmake_registry_lock.py'],
    [sys.executable, '-B', 'tools/test_adaptive_contracts.py'],
    [sys.executable, '-B', 'tools/test_app_chrome_contracts.py'],
    [sys.executable, '-B', 'tools/test_kotlin_jvm_signatures.py'],
    [sys.executable, '-B', 'tools/test_kotlin_pruning_regressions.py'],
    [sys.executable, '-B', 'tools/test_ui_design_contracts.py'],
    [sys.executable, '-B', 'tools/test_action_contrast.py'],
    [sys.executable, '-B', 'tools/test_pager_landscape_contracts.py'],
    [sys.executable, '-B', 'tools/test_pager_policy.py'],
    [sys.executable, '-B', 'tools/test_pager_handoff_contracts.py'],
):
    subprocess.run(command, cwd=ROOT, check=True)
print('PASS VulkanScope 4.0.15 static source checks; Android APK and device execution NOT VERIFIED')
