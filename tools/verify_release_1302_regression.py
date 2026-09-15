#!/usr/bin/env python3
import argparse
import hashlib
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--predecessor', required=True)
parser.add_argument('--root')
args = parser.parse_args()
root = Path(args.root).resolve() if args.root else Path(__file__).resolve().parents[1]
pred = Path(args.predecessor).resolve()
errors = []

def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()

allowed_changed = {
    Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'),
    Path('app/build.gradle.kts'),
}
for old in sorted((pred / 'app/src/main').rglob('*')):
    if not old.is_file():
        continue
    rel = old.relative_to(pred)
    new = root / rel
    if not new.is_file():
        errors.append(f'predecessor runtime file removed: {rel}')
    elif rel not in allowed_changed and digest(old) != digest(new):
        errors.append(f'locked runtime file changed: {rel}')
for new in sorted((root / 'app/src/main').rglob('*')):
    if not new.is_file():
        continue
    rel = new.relative_to(root)
    if not (pred / rel).exists():
        errors.append(f'unexpected new runtime file: {rel}')
for rel in allowed_changed:
    if not (root / rel).is_file() or not (pred / rel).is_file():
        errors.append(f'allowed changed file missing: {rel}')
manifest_rel = Path('app/src/main/AndroidManifest.xml')
if digest(root / manifest_rel) != digest(pred / manifest_rel):
    errors.append('AndroidManifest changed')
if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
locked = sum(1 for p in (pred / 'app/src/main').rglob('*') if p.is_file()) - 1
print(f'PASS VulkanScope 1.3.1 -> 1.3.2 runtime regression boundary; lockedRuntimeFiles={locked}, newRuntimeFiles=0')
