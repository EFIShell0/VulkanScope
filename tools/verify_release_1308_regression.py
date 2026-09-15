#!/usr/bin/env python3
import argparse, hashlib, json, sys
from pathlib import Path

def sha(p): return hashlib.sha256(p.read_bytes()).hexdigest()
def main():
    ap=argparse.ArgumentParser(); ap.add_argument('--root',default='.'); ap.add_argument('--predecessor',required=True); a=ap.parse_args()
    r=Path(a.root).resolve(); p=Path(a.predecessor).resolve(); g=json.loads((r/'tests/golden/1.3.7_ui_refinement_contract.json').read_text())
    if sum(1 for x in p.rglob('*') if x.is_file())!=g['predecessor_file_count']: raise AssertionError('predecessor file census mismatch')
    if sha(p/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')!=g['predecessor_main_activity_sha256']: raise AssertionError('predecessor MainActivity mismatch')
    for rel,expected in g['immutable_app_src_main_sha256'].items():
        if sha(p/rel)!=expected or sha(r/rel)!=expected: raise AssertionError(f'protected production drift: {rel}')
    pb=(p/'app/build.gradle.kts').read_text(); b=(r/'app/build.gradle.kts').read_text().replace('versionCode = 1308','versionCode = 1307').replace('versionName = "1.3.8"','versionName = "1.3.7"')
    if b!=pb: raise AssertionError('build.gradle changed beyond release identity')
    # No production file other than MainActivity/build identity may change; MainActivity is intentionally UI-only for the requested surfaces.
    print('release_1308 regression boundary: PASS')
if __name__=='__main__':
    try: main()
    except Exception as e: print(f'release_1308 regression boundary: FAIL: {e}',file=sys.stderr); sys.exit(1)
