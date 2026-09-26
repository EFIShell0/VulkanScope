#!/usr/bin/env python3
import importlib.util
import shutil
import sys
import tempfile
import zipfile
from pathlib import Path

sys.dont_write_bytecode = True
root = Path(__file__).resolve().parents[1]
predecessor_zip = Path('/mnt/data/VulkanScope-1.4.13.zip')
spec = importlib.util.spec_from_file_location('verify_release_1414', root / 'tools/verify_release_1414.py')
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)
if not predecessor_zip.is_file():
    raise SystemExit('immutable predecessor ZIP unavailable')
with tempfile.TemporaryDirectory(prefix='vulkanscope-1414-predecessor-') as temp:
    with zipfile.ZipFile(predecessor_zip) as archive:
        archive.extractall(temp)
    dirs = [p for p in Path(temp).iterdir() if p.is_dir()]
    if len(dirs) != 1:
        raise SystemExit('immutable predecessor ZIP layout is invalid')
    errors = module.verify(dirs[0], skip_version=True)
    if not errors:
        raise SystemExit('immutable predecessor unexpectedly satisfies the 1.4.14 contract')
    required = ['visible destinations', 'overlay scrolling page content']
    if not any(any(token in error for token in required) for error in errors):
        raise SystemExit('predecessor failed for an unexpected reason instead of the reported floating-navigation regression')
print('VulkanScope 1.4.14 predecessor regression oracle: PASS')
