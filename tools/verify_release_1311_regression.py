#!/usr/bin/env python3
import argparse,hashlib,json,sys
from pathlib import Path
def sha(p): return hashlib.sha256(p.read_bytes()).hexdigest()
def main():
    ap=argparse.ArgumentParser(); ap.add_argument('--root',default='.'); ap.add_argument('--predecessor',required=True); a=ap.parse_args()
    root=Path(a.root).resolve(); pred=Path(a.predecessor).resolve(); c=json.loads((root/'tests/golden/1.3.10_compile_update_palette_contract.json').read_text())
    if sum(1 for p in pred.rglob('*') if p.is_file())!=c['predecessor_file_count']: raise AssertionError('predecessor census mismatch')
    if sha(pred/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')!=c['predecessor_main_activity_sha256']: raise AssertionError('predecessor MainActivity mismatch')
    for rel,h in c['immutable_app_src_main_sha256'].items():
        if sha(pred/rel)!=h or sha(root/rel)!=h: raise AssertionError('protected app/src/main drift: '+rel)
    pb=(pred/'app/build.gradle.kts').read_text(); cb=(root/'app/build.gradle.kts').read_text()
    norm=cb.replace('versionCode = 1311','versionCode = 1310').replace('versionName = "1.3.11"','versionName = "1.3.10"')
    if norm!=pb: raise AssertionError('build.gradle changed beyond release identity')
    print('release_1311 regression boundary: PASS')
if __name__=='__main__':
    try: main()
    except Exception as e: print('release_1311 regression boundary: FAIL:',e,file=sys.stderr); sys.exit(1)
