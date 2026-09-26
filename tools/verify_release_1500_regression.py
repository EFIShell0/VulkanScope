#!/usr/bin/env python3
import importlib.util
import sys
import tempfile
import zipfile
from pathlib import Path

sys.dont_write_bytecode = True
root = Path(__file__).resolve().parents[1]
predecessor_zip = Path('/mnt/data/VulkanScope-1.4.16.zip')
spec = importlib.util.spec_from_file_location('verify_release_1500', root / 'tools/verify_release_1500.py')
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)
if not predecessor_zip.is_file():
    raise SystemExit('immutable predecessor ZIP unavailable')
with tempfile.TemporaryDirectory(prefix='vulkanscope-1500-predecessor-') as temp:
    with zipfile.ZipFile(predecessor_zip) as archive:
        archive.extractall(temp)
    dirs = [p for p in Path(temp).iterdir() if p.is_dir()]
    if len(dirs) != 1:
        raise SystemExit('immutable predecessor ZIP layout is invalid')
    errors = module.verify(dirs[0], skip_version=True)
    if not errors:
        raise SystemExit('immutable predecessor unexpectedly satisfies the 1.5.0 contract')
    expected_fragments = ['navigation metric missing', 'startup watchdog', 'compact transient status', 'double-applies']
    if not any(any(fragment in error for fragment in expected_fragments) for error in errors):
        raise SystemExit('predecessor failed for an unexpected reason')
print('VulkanScope 1.5.0 predecessor regression oracle: PASS')
