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
    if a not in t or b not in t.split(a,1)[1]: fail(f'cannot isolate {a} -> {b}')
    return t.split(a,1)[1].split(b,1)[0]

def main():
    ap=argparse.ArgumentParser(); ap.add_argument('--root',default='.'); ap.add_argument('--skip-version',action='store_true'); args=ap.parse_args()
    root=Path(args.root).resolve(); mainp=root/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'; buildp=root/'app/build.gradle.kts'; rulesp=root/'rules/PROJECT_RULES.md'; cp=root/'tests/golden/1.3.9_filter_turnip_about_contract.json'
    if not cp.is_file(): cp=Path(__file__).resolve().parents[1]/'tests/golden/1.3.9_filter_turnip_about_contract.json'
    for p in (mainp,buildp,rulesp,cp):
        if not p.is_file(): fail(f'missing {p}')
    src=mainp.read_text(); build=buildp.read_text(); rules=rulesp.read_text(); c=json.loads(cp.read_text())
    if not args.skip_version:
        need(build,'versionCode = 1310'); need(build,'versionName = "1.3.10"'); need(rules,'## Release 1.3.10 filter-arrow interaction, Turnip duplicate-source presentation and About disclosure refinement')
    filt=block(src,'private fun ExpressiveSingleFilterSelector(','private fun ExpressiveFilterBar(')
    for x in ['val arrowShape = RoundedCornerShape(15.dp)','color = if (expanded) VulkanAccent else VulkanAccentContainer','.size(44.dp)\n                        .clip(arrowShape)\n                        .clickable(enabled = enabled, role = Role.Button) { expanded = !expanded }','enter = fadeIn(tween(220)) + scaleIn(tween(240), initialScale = 0.94f)','targetState = page to query','label = "filterPageTransition"','Box(Modifier.weight(1f).fillMaxWidth().heightIn(min = 56.dp))','val popupMaxHeight = (screenHeight - imeHeight - 42.dp).coerceIn(300.dp, 640.dp)','val pagingEnabled = indexed.isNotEmpty() && pageCount > 1','requested !in 1..pageCount']:
        need(filt,x,'filter 1.3.10 contract drift: '+x)
    absent(filt,'.clip(selectorShape)\n                .clickable(enabled = enabled, role = Role.Button)', 'whole filter selector became clickable again')
    fm=block(src,'private fun TurnipFileManagerDialog(','private fun SharedStoragePermissionActionButton(')
    for x in ['private fun FileManagerNavigateArrow(','color = if (enabled) VulkanAccentContainer else VulkanSurfaceLow','.size(44.dp).clip(shape).clickable(enabled = enabled, role = Role.Button','private fun MesaOfficialLogoBadge(','painter = painterResource(R.drawable.mesa3d_logo)','border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant)','val alreadyImported = turnipImportedSourceKey(candidate.path, candidate.name) in state.importedSourceKeys','Already imported · same file name and location','MesaOfficialLogoBadge(size = 38.dp)']:
        need(fm,x,'Turnip file-manager 1.3.10 drift: '+x)
    if fm.count('enabled = !state.importing && !alreadyImported') != 2:
        fail('both list/details and grid imported Turnip packages must be disabled')
    if fm.count('FileManagerNavigateArrow(enabled = enabled, description = "Open folder")') != 2:
        fail('both list/details and grid folders must use the dedicated arrow-only navigation action')
    folderrow=block(src,'private fun TurnipFileManagerFolderRow(','private fun TurnipFileManagerCandidateRow(')
    absent(folderrow,'.clickable(enabled = enabled', 'folder row itself became clickable')
    foldergrid=block(src,'private fun TurnipFileManagerFolderGridCard(','private fun TurnipFileManagerCandidateGridCard(')
    absent(foldergrid,'.clickable(enabled = enabled', 'folder grid card itself became clickable')
    for x in ['private fun turnipImportedSourceKey(path: String, name: String)','val importedSourceKeys: Set<String> = emptySet()','.filterNot { path -> turnipImportedSourceKey(path, File(path).name) in state.importedSourceKeys }']:
        need(src,x,'duplicate-source block missing: '+x)
    toggle = block(src, 'private fun toggleTurnipFileManagerSelection(', 'private fun closeTurnipFileManager(')
    need(toggle, 'if (turnipImportedSourceKey(candidate.path, candidate.name) in state.importedSourceKeys) return', 'selection guard for already-imported source missing')
    about=block(src,'CapabilitySectionCard("About")','CapabilitySectionCard("Export complete report")')
    need(about,'Text("VulkanScope is not an official Khronos Group project.", color = ComposeColor(0xFFFFC857), fontWeight = FontWeight.SemiBold)')
    need(about,'Spacer(Modifier.height(8.dp))')
    # Official Mesa asset and all other app/src/main resources stay predecessor-identical.
    for rel,expected in c['immutable_app_src_main_sha256'].items():
        p=root/rel
        if not p.is_file() or sha(p)!=expected: fail(f'unrelated production/resource drift: {rel}')
    print('release_1310 verifier: PASS')
if __name__=='__main__':
    try: main()
    except Exception as e: print('release_1310 verifier: FAIL:',e,file=sys.stderr); sys.exit(1)
