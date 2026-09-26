import argparse
import hashlib
import json
from pathlib import Path

ap = argparse.ArgumentParser()
ap.add_argument('--root', default='.')
ap.add_argument('--predecessor', required=True)
args = ap.parse_args()
root = Path(args.root).resolve()
pred = Path(args.predecessor).resolve()
contract = json.loads((root / 'tests/golden/1.4.4_googlebook_vulkan_1.4.364_contract.json').read_text(encoding='utf-8'))

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
pred_manifest = (pred / 'app/src/main/AndroidManifest.xml').read_text(encoding='utf-8')
current_manifest = (root / 'app/src/main/AndroidManifest.xml').read_text(encoding='utf-8')
pc_feature_line = '    <uses-feature android:name="android.hardware.type.pc" android:required="false" />\n'
if current_manifest.count(pc_feature_line) != 1 or current_manifest.replace(pc_feature_line, '', 1) != pred_manifest:
    raise SystemExit('AndroidManifest change exceeds the single optional android.hardware.type.pc declaration')

pred_build = (pred / 'app/build.gradle.kts').read_text(encoding='utf-8')
cur_build = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
normalized = cur_build.replace('versionCode = 1405', 'versionCode = 1404').replace('versionName = "1.4.5"', 'versionName = "1.4.4"')
if normalized != pred_build:
    raise SystemExit('build.gradle changed beyond release identity')

for relative in contract['unchanged_security_sensitive_paths']:
    if (pred / relative).read_bytes() != (root / relative).read_bytes():
        raise SystemExit('security-sensitive predecessor path changed: ' + relative)
print(f'release_1405 regression boundary: PASS appChanges={len(changed_app)} predecessorFiles={contract["predecessor_file_count"]}')
