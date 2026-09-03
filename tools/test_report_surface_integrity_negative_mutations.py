#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_report_surface_integrity_04141.py'
cpp_rel = Path('app/src/main/cpp/vulkanscope.cpp')
kt_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')


def build_temp():
    temp = tempfile.TemporaryDirectory(prefix='vulkanscope-04141-negative-')
    base = Path(temp.name)
    for rel in [cpp_rel, kt_rel]:
        target = base / rel
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(root / rel, target)
    return temp, base


def run(base):
    return subprocess.run([sys.executable, str(verifier), '--root', str(base)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)


def expect_fail(rel, old, new, label):
    temp, base = build_temp()
    try:
        path = base / rel
        text = path.read_text(encoding='utf-8')
        if old not in text:
            raise SystemExit(f'FAIL mutation source missing: {label}')
        path.write_text(text.replace(old, new, 1), encoding='utf-8')
        result = run(base)
        if result.returncode == 0:
            raise SystemExit(f'FAIL mutation was not rejected: {label}')
    finally:
        temp.cleanup()


expect_fail(cpp_rel, 'std::is_same_v<std::remove_cv_t<E>, uint8_t>', 'std::is_same_v<std::remove_cv_t<E>, uint16_t>', 'uuid-byte-array')
expect_fail(cpp_rel, 'existing.section == generated.section && existing.name == generated.name', 'existing.name == generated.name', 'property-section-identity')
temp, base = build_temp()
try:
    path = base / kt_rel
    text = path.read_text(encoding='utf-8')
    source = 'override fun surfaceDestroyed(holder: SurfaceHolder) { onDestroyed(hostGeneration, holder.surface) }' if 'onDestroyed(hostGeneration, holder.surface)' in text else 'override fun surfaceDestroyed(holder: SurfaceHolder) { onDestroyed(holder.surface) }'
    if source not in text:
        raise SystemExit('FAIL mutation source missing: surface-destroy-identity')
    path.write_text(text.replace(source, 'override fun surfaceDestroyed(holder: SurfaceHolder) { }', 1), encoding='utf-8')
    result = run(base)
    if result.returncode == 0:
        raise SystemExit('FAIL mutation was not rejected: surface-destroy-identity')
finally:
    temp.cleanup()
temp, base = build_temp()
try:
    path = base / kt_rel
    text = path.read_text(encoding='utf-8')
    path.write_text(text.replace('Log.e("VulkanScope", "Surface refresh failed", e)', 'Log.e("VulkanScope", "Surface refresh failed after collection", e)', 1), encoding='utf-8')
    result = run(base)
    if result.returncode != 0:
        raise SystemExit('FAIL unrelated diagnostic text false-positive control')
finally:
    temp.cleanup()
print('PASS VulkanScope 0.41.41 negative mutation gate')
