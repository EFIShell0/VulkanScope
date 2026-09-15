#!/usr/bin/env python3
import argparse, hashlib, json, re, sys
from pathlib import Path

def sha(p): return hashlib.sha256(p.read_bytes()).hexdigest()
def main():
    ap=argparse.ArgumentParser(); ap.add_argument('--root',default='.'); ap.add_argument('--predecessor',required=True); a=ap.parse_args()
    r=Path(a.root).resolve(); p=Path(a.predecessor).resolve(); g=json.loads((r/'tests/golden/1.3.5_storage_exchange_contract.json').read_text())
    if sum(1 for x in p.rglob('*') if x.is_file()) != g['predecessor_file_count']: raise AssertionError('predecessor file census mismatch')
    if sha(p/'app/build.gradle.kts') != g['predecessor_build_gradle_sha256']: raise AssertionError('predecessor build identity mismatch')
    for rel,expected in g['immutable_app_src_main_sha256'].items():
        if sha(p/rel)!=expected: raise AssertionError(f'predecessor immutable hash mismatch: {rel}')
        if sha(r/rel)!=expected: raise AssertionError(f'unrelated production drift: {rel}')
    b=(r/'app/build.gradle.kts').read_text(); pb=(p/'app/build.gradle.kts').read_text()
    normalized=b.replace('versionCode = 1306','versionCode = 1305').replace('versionName = "1.3.6"','versionName = "1.3.5"')
    if normalized!=pb: raise AssertionError('build.gradle changed beyond release identity')
    # Manifest and key non-storage runtime surfaces must remain byte-identical.
    for rel in ['app/src/main/AndroidManifest.xml','app/src/main/java/com/efishell/vulkanscope/VulkanQrCode.kt']:
        if sha(r/rel)!=sha(p/rel): raise AssertionError(f'protected predecessor drift: {rel}')
    old=(p/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(); new=(r/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text()
    # Existing all-files and Turnip security contract remains present.
    for t in ['Environment.isExternalStorageManager()','installDriverBundleIo','inspectTurnipArchive','TURNIP_ARCHIVE_INPUT_MAX_BYTES','activeUpdateDownloadCall?.cancel()','technicalReportJson']:
        if t not in old or t not in new: raise AssertionError(f'protected behavior token lost: {t}')
    print('release_1306 regression boundary: PASS')
if __name__=='__main__':
    try: main()
    except Exception as e: print(f'release_1306 regression boundary: FAIL: {e}',file=sys.stderr); sys.exit(1)
