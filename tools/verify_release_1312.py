#!/usr/bin/env python3
import argparse, hashlib, json, sys
from pathlib import Path

def fail(m): raise AssertionError(m)
def need(t,x,m=None):
    if x not in t: fail(m or f'missing {x!r}')
def absent(t,x,m=None):
    if x in t: fail(m or f'forbidden {x!r}')
def sha(p): return hashlib.sha256(p.read_bytes()).hexdigest()
def block(t,a,b):
    if a not in t: fail(f'cannot find block start {a!r}')
    tail=t.split(a,1)[1]
    if b not in tail: fail(f'cannot find block end {b!r}')
    return tail.split(b,1)[0]

def main():
    ap=argparse.ArgumentParser(); ap.add_argument('--root',default='.'); ap.add_argument('--skip-version',action='store_true'); a=ap.parse_args()
    root=Path(a.root).resolve(); toolroot=Path(__file__).resolve().parents[1]
    mainp=root/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'; buildp=root/'app/build.gradle.kts'; rulesp=root/'rules/PROJECT_RULES.md'
    cp=root/'tests/golden/1.3.11_filter_storage_driver_contract.json'
    if not cp.is_file(): cp=toolroot/'tests/golden/1.3.11_filter_storage_driver_contract.json'
    for p in (mainp,buildp,rulesp,cp):
        if not p.is_file(): fail(f'missing {p}')
    src=mainp.read_text(); build=buildp.read_text(); rules=rulesp.read_text(); c=json.loads(cp.read_text())
    if not a.skip_version:
        need(build,'versionCode = 1312'); need(build,'versionName = "1.3.12"')
        need(rules,'## Release 1.3.12 filter compactness, shared-storage actions and driver-manager Expressive refinement')
    # Retain the real 1.3.11 build fix.
    need(src,'import androidx.compose.foundation.layout.fillMaxHeight','1.3.11 fillMaxHeight compile fix regressed')
    filt=block(src,'private fun ExpressiveSingleFilterSelector(','private fun ExpressiveFilterBar(')
    for x in [
        'val showSearch = labels.size >= 5',
        'query = ""\n            val openingPage = selectedIndex?.takeIf { it in labels.indices }?.div(pageSize) ?: 0',
        'popupMounted = true\n            popupVisible = false\n            delay(20)\n            popupVisible = true',
        'enter = fadeIn(tween(220)) + scaleIn(tween(240), initialScale = 0.94f) + expandVertically',
        'modifier = Modifier.size(56.dp).clip(closeShape).clickable(role = Role.Button) { expanded = false }',
        'if (showSearch) {\n                                    ExpressiveSearchField(',
        'if (indexed.isNotEmpty() && pageCount > 1) {',
        'candidate.isEmpty() -> pageField = value',
        'requested != null && requested in 1..pageCount',
        'else if (pageField.text.isBlank()) {\n                                                pageField = TextFieldValue((page + 1).toString())',
        'val selectedOffset = if (targetQuery.isBlank()) targetPageItems.indexOfFirst { it.first == selectedIndex } else -1',
        'rememberLazyListState(initialFirstVisibleItemIndex = selectedOffset.coerceAtLeast(0))'
    ]: need(filt,x,'filter 1.3.12 contract drift: '+x)
    if filt.count('if (indexed.isNotEmpty() && pageCount > 1) {') != 2:
        fail('search header/page footer must both gate page chrome on pageCount > 1')
    absent(filt,'val pagingEnabled = indexed.isNotEmpty() && pageCount > 1','one-page disabled paging chrome was restored instead of being omitted')
    # Shared storage browser is common to JSON/TXT/HTML/profile/report import/export; cards are informational and arrow-only.
    folder=block(src,'private fun SharedStorageFolderRow(','private fun SharedStorageFileRow(')
    fileb=block(src,'private fun SharedStorageFileRow(','private fun SystemDriverManagerRow(')
    need(folder,'FileManagerNavigateArrow(enabled = enabled, description = "Open folder", onClick = onOpen)')
    absent(folder,'.clickable(','shared-storage folder body became clickable')
    need(fileb,'FileManagerNavigateArrow(enabled = enabled, description = "Select ${entry.name}", onClick = onSelect)')
    absent(fileb,'.clickable(','shared-storage file body became clickable')
    browser=block(src,'private fun SharedStorageBrowserDialog(','private fun SharedStorageFolderRow(')
    need(browser,'SharedStorageFolderRow(path = path, enabled = !busy) { directoryPath = path }')
    need(browser,'SharedStorageFileRow(entry = entry, enabled = !busy)')
    for invariant in ['validatedSharedStorageDestination','scanSharedStorageDirectory','pendingOverwrite','fixedName = "${filenameBase.trim()}.$extension"']:
        need(browser,invariant,'storage security/type-lock invariant drift: '+invariant)
    # Driver-manager visual refinement without changing activation/removal semantics.
    sysb=block(src,'private fun SystemDriverManagerRow(','private fun SystemDriverDetailsDialog(')
    for x in ['painterResource(R.drawable.ic_android)','ExpressiveInfoPill("GPU"','ExpressiveInfoPill("Driver"','ExpressiveInfoPill("Version"','TurnipStatePill("ACTIVE", true, true)','ExpressiveContainedTextButton("Activate", enabled = enabled, onClick = onActivate)']:
        need(sysb,x,'System driver Expressive card drift: '+x)
    turn=block(src,'private fun TurnipDriverManagerTable(','private fun turnipDriverStateLabel(')
    for x in ['MesaOfficialLogoBadge(size = 30.dp, muted = unavailable)','MesaOfficialLogoBadge(size = 34.dp, muted = unavailable)','ExpressiveInfoPill("Source ZIP"','ExpressiveInfoPill("Driver version"','ExpressiveInfoPill("Package"','TurnipStatePill(turnipDriverStateLabel(driver), driver.selected, driver.sourceAvailable && info.installed)','ExpressiveContainedTextButton("Activate", enabled = enabled && info.installed)','ExpressiveContainedIconTextButton("Remove", R.drawable.ic_delete, enabled = enabled)']:
        need(turn,x,'Turnip driver Expressive card drift: '+x)
    # All packaged app/src/main artifacts except MainActivity remain predecessor-identical.
    for rel,expected in c['immutable_app_src_main_sha256'].items():
        p=root/rel
        if not p.is_file() or sha(p)!=expected: fail('unrelated app/src/main drift: '+rel)
    print('release_1312 verifier: PASS')
if __name__=='__main__':
    try: main()
    except Exception as e: print('release_1312 verifier: FAIL:',e,file=sys.stderr); sys.exit(1)
