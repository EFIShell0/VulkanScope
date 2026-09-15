#!/usr/bin/env python3
import argparse, hashlib, json, sys
from pathlib import Path
def sha(p): return hashlib.sha256(p.read_bytes()).hexdigest()
def main():
    ap=argparse.ArgumentParser(); ap.add_argument('--root',default='.'); ap.add_argument('--predecessor',required=True); a=ap.parse_args()
    r=Path(a.root).resolve(); p=Path(a.predecessor).resolve(); g=json.loads((r/'tests/golden/1.3.6_material3_optin_contract.json').read_text())
    if sum(1 for x in p.rglob('*') if x.is_file())!=g['predecessor_file_count']: raise AssertionError('predecessor file census mismatch')
    if sha(p/'app/build.gradle.kts')!=g['predecessor_build_gradle_sha256']: raise AssertionError('predecessor build identity mismatch')
    if sha(p/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')!=g['predecessor_main_activity_sha256']: raise AssertionError('predecessor MainActivity mismatch')
    for rel,expected in g['immutable_app_src_main_sha256'].items():
        if sha(p/rel)!=expected or sha(r/rel)!=expected: raise AssertionError(f'protected production drift: {rel}')
    b=(r/'app/build.gradle.kts').read_text(); pb=(p/'app/build.gradle.kts').read_text(); normalized=b.replace('versionCode = 1307','versionCode = 1306').replace('versionName = "1.3.7"','versionName = "1.3.6"')
    if normalized!=pb: raise AssertionError('build.gradle changed beyond release identity')
    old=(p/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(); new=(r/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text()
    normalized_new=new.replace('@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun TurnipFileManagerDialog(', '@Composable\nprivate fun TurnipFileManagerDialog(',1)
    normalized_new=normalized_new.replace('@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun SharedStorageBrowserDialog(', '@Composable\nprivate fun SharedStorageBrowserDialog(',1)
    if normalized_new!=old: raise AssertionError('MainActivity delta exceeds two narrow Expressive opt-ins')
    print('release_1307 regression boundary: PASS')
if __name__=='__main__':
    try: main()
    except Exception as e: print(f'release_1307 regression boundary: FAIL: {e}',file=sys.stderr); sys.exit(1)
