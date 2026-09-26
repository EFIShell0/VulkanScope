#!/usr/bin/env python3
import importlib.util
from pathlib import Path

root = Path(__file__).resolve().parents[1]
predecessor = root.parent / 'VulkanScope-1.4.9-work'
spec = importlib.util.spec_from_file_location('verify_release_1410', root / 'tools/verify_release_1410.py')
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)
errors = module.verify(predecessor)
if not errors:
    raise SystemExit('immutable predecessor unexpectedly satisfies the 1.4.10 icon-placement contract')
if not any('header' in error.lower() or 'duplicate' in error.lower() or 'identity' in error.lower() for error in errors):
    raise SystemExit('predecessor failed for an unexpected reason')
print('VulkanScope 1.4.10 predecessor regression oracle: PASS')
