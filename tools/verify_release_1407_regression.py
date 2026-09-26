import argparse
import hashlib
import json
import subprocess
import sys
from pathlib import Path

ap = argparse.ArgumentParser()
ap.add_argument('--root', default='.')
ap.add_argument('--predecessor', required=True)
args = ap.parse_args()
root = Path(args.root).resolve()
pred = Path(args.predecessor).resolve()
contract = json.loads((root / 'tests/golden/1.4.6_compile_mouse_input_contract.json').read_text(encoding='utf-8'))

def sha(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()

if sum(1 for p in pred.rglob('*') if p.is_file()) != contract['predecessor_file_count']:
    raise SystemExit('predecessor file census mismatch')
for relative, expected in contract['predecessor_sha256'].items():
    if sha(pred / relative) != expected:
        raise SystemExit('predecessor hash mismatch: ' + relative)

pred_app = {p.relative_to(pred).as_posix(): p for p in (pred / 'app/src/main').rglob('*') if p.is_file()}
cur_app = {p.relative_to(root).as_posix(): p for p in (root / 'app/src/main').rglob('*') if p.is_file()}
if set(pred_app) != set(cur_app):
    raise SystemExit('app/src/main file set changed unexpectedly')
changed_app = sorted(relative for relative in pred_app if pred_app[relative].read_bytes() != cur_app[relative].read_bytes())
if changed_app != sorted(contract['allowed_changed_app_src_main']):
    raise SystemExit('unexpected app/src/main diff set: ' + repr(changed_app))

pred_build = (pred / 'app/build.gradle.kts').read_text(encoding='utf-8')
cur_build = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
normalized = cur_build.replace('versionCode = 1407', 'versionCode = 1406').replace('versionName = "1.4.7"', 'versionName = "1.4.6"')
if normalized != pred_build:
    raise SystemExit('build.gradle changed beyond release identity')
for relative in contract['unchanged_security_sensitive_paths']:
    if (pred / relative).read_bytes() != (root / relative).read_bytes():
        raise SystemExit('security-sensitive predecessor path changed: ' + relative)

pred_result = subprocess.run([sys.executable, str(root / 'tools/verify_release_1407.py'), '--root', str(pred), '--skip-version'], text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
if pred_result.returncode == 0:
    raise SystemExit('immutable predecessor unexpectedly satisfies 1.4.7 compile/mouse-input contract')
required_failures = [
    'JVM setter clash reintroduced: setOpeningAnimationEnabled(Boolean)',
    'non-conflicting opening-animation persistence helper missing',
    'mouse pointer scrolling contract missing: import androidx.compose.foundation.gestures.ScrollableState',
    'primary VulkanLazyPage does not expose mouse wheel/drag scrolling'
]
for failure in required_failures:
    if failure not in pred_result.stdout:
        raise SystemExit('predecessor failure signature missing: ' + failure + '\n' + pred_result.stdout)
print(f'release_1407 regression boundary: PASS appChanges={len(changed_app)} predecessorFiles={contract["predecessor_file_count"]} failingBeforeFix={len(required_failures)}')
