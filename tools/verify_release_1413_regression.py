#!/usr/bin/env python3
import importlib.util
import sys
from pathlib import Path

sys.dont_write_bytecode = True
root = Path(__file__).resolve().parents[1]
predecessor = Path('/mnt/data/VulkanScope-1.4.12-work')
spec = importlib.util.spec_from_file_location('verify_release_1413', root / 'tools/verify_release_1413.py')
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)
if not predecessor.is_dir():
    raise SystemExit('immutable predecessor working tree unavailable')
errors = module.verify(predecessor)
if not errors:
    raise SystemExit('immutable predecessor unexpectedly satisfies the 1.4.13 contract')
print('VulkanScope 1.4.13 predecessor regression oracle: PASS')
