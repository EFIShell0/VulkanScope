#!/usr/bin/env python3
import argparse, hashlib, json, sys
from pathlib import Path

def sha(path): return hashlib.sha256(path.read_bytes()).hexdigest()

def main():
    ap=argparse.ArgumentParser(); ap.add_argument('--root',default='.'); ap.add_argument('--predecessor',required=True); args=ap.parse_args()
    root=Path(args.root).resolve(); pred=Path(args.predecessor).resolve(); contract=json.loads((root/'tests/golden/1.4.1_filter_landscape_pagination_contract.json').read_text())
    if sum(1 for p in pred.rglob('*') if p.is_file())!=contract['predecessor_file_count']: raise AssertionError('predecessor census mismatch')
    if sha(pred/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')!=contract['predecessor_main_activity_sha256']: raise AssertionError('predecessor MainActivity mismatch')
    if sha(pred/'app/build.gradle.kts')!=contract['predecessor_build_gradle_sha256']: raise AssertionError('predecessor build.gradle mismatch')
    for rel,expected in contract['immutable_app_src_main_sha256'].items():
        if sha(pred/rel)!=expected or sha(root/rel)!=expected: raise AssertionError('protected app/src/main drift: '+rel)
    predecessor_build=(pred/'app/build.gradle.kts').read_text(); current_build=(root/'app/build.gradle.kts').read_text()
    normalized=current_build.replace('versionCode = 1402','versionCode = 1401').replace('versionName = "1.4.2"','versionName = "1.4.1"')
    if normalized!=predecessor_build: raise AssertionError('build.gradle changed beyond release identity')
    if (root/'screenshots').exists(): raise AssertionError('screenshots directory must be excluded from 1.4.2 source package')
    print('release_1402 regression boundary: PASS')

if __name__=='__main__':
    try: main()
    except Exception as exc:
        print('release_1402 regression boundary: FAIL:',exc,file=sys.stderr); sys.exit(1)
