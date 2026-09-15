#!/usr/bin/env python3
import argparse, hashlib, json, sys
from pathlib import Path

def fail(m): raise AssertionError(m)
def need(t,x,m=None):
    if x not in t: fail(m or f'missing {x!r}')
def absent(t,x,m=None):
    if x in t: fail(m or f'forbidden {x!r}')
def sha(p): return hashlib.sha256(p.read_bytes()).hexdigest()

def main():
    ap=argparse.ArgumentParser(); ap.add_argument('--root',default='.'); ap.add_argument('--skip-version',action='store_true'); a=ap.parse_args()
    r=Path(a.root).resolve(); src=(r/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(); build=(r/'app/build.gradle.kts').read_text(); rules=(r/'rules/PROJECT_RULES.md').read_text()
    contract = r/'tests/golden/1.3.6_material3_optin_contract.json'
    if not contract.is_file(): contract = Path(__file__).resolve().parents[1]/'tests/golden/1.3.6_material3_optin_contract.json'
    g=json.loads(contract.read_text())
    if not a.skip_version:
        need(build,'versionCode = 1307'); need(build,'versionName = "1.3.7"')
    need(src,'import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi')
    absent(src,'@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)','broad file-level Expressive opt-in is not allowed')
    for fn in ['TurnipFileManagerDialog','SharedStorageBrowserDialog']:
        token='@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun '+fn+'('
        need(src,token,f'{fn} missing narrow ExperimentalMaterial3ExpressiveApi opt-in')
    # Each affected scope contains exactly the two LoadingIndicator calls reported by assembleRelease.
    t=src.split('private fun TurnipFileManagerDialog(',1)[1].split('private fun TurnipFileManagerSelectionSummary',1)[0]
    s=src.split('private fun SharedStorageBrowserDialog(',1)[1].split('private fun SharedStorageFolderRow',1)[0]
    if t.count('LoadingIndicator(')!=2: fail('TurnipFileManagerDialog LoadingIndicator call count drift')
    if s.count('LoadingIndicator(')!=2: fail('SharedStorageBrowserDialog LoadingIndicator call count drift')
    # Production delta from immutable 1.3.6 must be exactly two narrow annotations.
    normalized=src.replace('@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun TurnipFileManagerDialog(', '@Composable\nprivate fun TurnipFileManagerDialog(',1)
    normalized=normalized.replace('@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun SharedStorageBrowserDialog(', '@Composable\nprivate fun SharedStorageBrowserDialog(',1)
    if hashlib.sha256(normalized.encode()).hexdigest()!=g['predecessor_main_activity_sha256']: fail('MainActivity changed beyond the two required opt-ins')
    for rel,expected in g['immutable_app_src_main_sha256'].items():
        p=r/rel
        if not p.is_file() or sha(p)!=expected: fail(f'unrelated production drift: {rel}')
    need(rules,'## Release 1.3.7 Material3 Expressive compile opt-in fix')
    print('release_1307 verifier: PASS')
if __name__=='__main__':
    try: main()
    except Exception as e: print(f'release_1307 verifier: FAIL: {e}',file=sys.stderr); sys.exit(1)
