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
    contract=r/'tests/golden/1.3.7_ui_refinement_contract.json'
    if not contract.is_file(): contract=Path(__file__).resolve().parents[1]/'tests/golden/1.3.7_ui_refinement_contract.json'
    g=json.loads(contract.read_text())
    if not a.skip_version:
        need(build,'versionCode = 1308'); need(build,'versionName = "1.3.8"')
    need(rules,'## Release 1.3.8 filter selector and Turnip file-manager Material 3 Expressive refinement')
    # Filter motion/search/layout/pagination contract.
    f=src.split('private fun ExpressiveSingleFilterSelector(',1)[1].split('private fun ExpressiveFilterBar(',1)[0]
    for token in ['scaleIn(tween(180), initialScale = 0.96f)','scaleOut(tween(130), targetScale = 0.98f)','filterSelectorArrow','ExpressiveSearchField(','ExpressiveScrollHints(listState','softWrap = true','val pagingEnabled = indexed.isNotEmpty() && pageCount > 1','candidate.any { !it.isDigit() }','requested !in 1..pageCount','KeyboardOptions(keyboardType = KeyboardType.Number)','enabled = pagingEnabled']:
        need(f,token,'filter selector contract drift: '+token)
    absent(f,'value.filter(Char::isDigit)','legacy permissive page-entry sanitizer returned')
    # File manager palette and expressive control contract.
    fm=src.split('private fun TurnipFileManagerDialog(',1)[1].split('private fun SharedStoragePermissionActionButton(',1)[0]
    for token in ['shape = MaterialTheme.shapes.extraLarge,\n            color = VulkanSurfaceRaised,\n            contentColor = VulkanTextPrimary','ExpressiveSearchField(','TurnipFileManagerViewModeButton(','role = Role.RadioButton','color = if (selected) VulkanAccentContainer else VulkanSurfaceTonal','checkedColor = VulkanAccent','containerColor = VulkanSurfaceRaised,\n        titleContentColor = VulkanTextPrimary,\n        textContentColor = VulkanTextSecondary','titleContentColor = VulkanTextPrimary','ExpressiveScrollHints(detailsScroll']:
        need(fm,token,'file-manager UI contract drift: '+token)
    absent(fm,'ComposeColor(0xFF163121)','legacy competing green selection surface returned')
    # Protected production bytes outside MainActivity.
    for rel,expected in g['immutable_app_src_main_sha256'].items():
        p=r/rel
        if not p.is_file() or sha(p)!=expected: fail(f'unrelated production drift: {rel}')
    print('release_1308 verifier: PASS')
if __name__=='__main__':
    try: main()
    except Exception as e: print(f'release_1308 verifier: FAIL: {e}',file=sys.stderr); sys.exit(1)
