#!/usr/bin/env python3
import argparse, hashlib, json, sys
from pathlib import Path

def fail(message): raise AssertionError(message)
def need(text, token, message=None):
    if token not in text: fail(message or f"missing {token!r}")
def absent(text, token, message=None):
    if token in text: fail(message or f"forbidden {token!r}")
def sha(path): return hashlib.sha256(path.read_bytes()).hexdigest()
def block(text, start, end):
    if start not in text: fail(f"cannot find block start {start!r}")
    tail=text.split(start,1)[1]
    if end not in tail: fail(f"cannot find block end {end!r}")
    return tail.split(end,1)[0]

def main():
    ap=argparse.ArgumentParser(); ap.add_argument('--root',default='.'); ap.add_argument('--skip-version',action='store_true'); args=ap.parse_args()
    root=Path(args.root).resolve(); toolroot=Path(__file__).resolve().parents[1]
    mainp=root/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'; buildp=root/'app/build.gradle.kts'; rulesp=root/'rules/PROJECT_RULES.md'
    contractp=root/'tests/golden/1.4.0_filter_boundary_turnip_slot_metadata_contract.json'
    if not contractp.is_file(): contractp=toolroot/'tests/golden/1.4.0_filter_boundary_turnip_slot_metadata_contract.json'
    for p in (mainp,buildp,rulesp,contractp):
        if not p.is_file(): fail(f'missing {p}')
    src=mainp.read_text(); build=buildp.read_text(); rules=rulesp.read_text(); contract=json.loads(contractp.read_text())
    if not args.skip_version:
        need(build,'versionCode = 1401'); need(build,'versionName = "1.4.1"'); need(rules,'## Release 1.4.1 filter scroll containment and Turnip slot metadata')
    helper=block(src,'private fun rememberFilterScrollBoundaryConnection(): NestedScrollConnection = remember {','@Composable\nprivate fun ExpressiveSingleFilterSelector(')
    need(helper,'override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset')
    need(helper,'if (available.y != 0f) Offset(0f, available.y) else Offset.Zero','residual scroll Y must be consumed')
    need(helper,'override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity')
    need(helper,'if (available.y != 0f) Velocity(0f, available.y) else Velocity.Zero','residual fling Y must be consumed')
    absent(helper,'onPreScroll','filter containment must not pre-consume normal list scroll')
    absent(helper,'onPreFling','filter containment must not pre-consume normal list fling')
    single=block(src,'private fun ExpressiveSingleFilterSelector(','private fun ExpressiveFilterBar(')
    need(single,'val boundaryScrollConnection = rememberFilterScrollBoundaryConnection()')
    need(single,'Box(Modifier.fillMaxSize().nestedScroll(boundaryScrollConnection))','single-filter list boundary containment missing')
    multi=block(src,'private fun ExpressiveMultiFilterBar(','private fun ExpressiveToggleRow(')
    need(multi,'val boundaryScrollConnection = rememberFilterScrollBoundaryConnection()')
    need(multi,'.heightIn(min = 56.dp, max = 420.dp).nestedScroll(boundaryScrollConnection)','multi-filter list boundary containment missing')
    table=block(src,'private fun TurnipDriverManagerTable(','private fun turnipDriverStateLabel(')
    if table.count('ExpressiveInfoPill("Vulkan® library", info.libraryName ?: "Not available"') < 2: fail('Turnip Vulkan library must be visible in wide and compact slot layouts')
    if table.count('ExpressiveInfoPill("Description", info.description?.takeIf { it.isNotBlank() } ?: "Not provided"') < 2: fail('Turnip description must be visible in wide and compact slot layouts')
    need(src,'val description: String?'); need(src,'val libraryName: String?')
    for rel,expected in contract['immutable_app_src_main_sha256'].items():
        p=root/rel
        if not p.is_file() or sha(p)!=expected: fail('unrelated app/src/main drift: '+rel)
    print('release_1401 verifier: PASS')

if __name__=='__main__':
    try: main()
    except Exception as exc:
        print('release_1401 verifier: FAIL:',exc,file=sys.stderr); sys.exit(1)
