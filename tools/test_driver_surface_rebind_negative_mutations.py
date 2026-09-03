#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_driver_surface_rebind_04143.py'
kt_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
state_rel = Path('tools/test_driver_surface_rebind_state_machine.py')


def build_temp():
    temp = tempfile.TemporaryDirectory(prefix='vulkanscope-04143-negative-')
    base = Path(temp.name)
    for rel in [kt_rel, state_rel]:
        target = base / rel
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(root / rel, target)
    return temp, base


def run(base):
    return subprocess.run([sys.executable, str(verifier), '--root', str(base)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)


def expect_fail(old, new, label):
    temp, base = build_temp()
    try:
        path = base / kt_rel
        text = path.read_text(encoding='utf-8')
        if old not in text:
            raise SystemExit(f'FAIL mutation source missing: {label}')
        path.write_text(text.replace(old, new, 1), encoding='utf-8')
        if run(base).returncode == 0:
            raise SystemExit(f'FAIL mutation was not rejected: {label}')
    finally:
        temp.cleanup()


expect_fail('surfaceHostGeneration += 1L', 'surfaceHostGeneration += 0L', 'surface-host-recreation')
expect_fail('if (hostGeneration != surfaceHostGeneration) {', 'if (false) {', 'stale-host-ready-rejection')
expect_fail('prepareDriverSurfaceRebind()\n        driverMode = mode', 'driverMode = mode', 'driver-change-rebind-preparation')
temp, base = build_temp()
try:
    path = base / kt_rel
    text = path.read_text(encoding='utf-8')
    path.write_text(text.replace('Log.e("VulkanScope", "Surface refresh failed", e)', 'Log.e("VulkanScope", "Surface refresh failed after retry", e)', 1), encoding='utf-8')
    if run(base).returncode != 0:
        raise SystemExit('FAIL unrelated Surface diagnostic false-positive control')
finally:
    temp.cleanup()
print('PASS VulkanScope 0.41.43 driver/Surface negative mutation gate')
