#!/usr/bin/env python3
import argparse, hashlib, json, sys, xml.etree.ElementTree as ET
from pathlib import Path

ANDROID_NS = '{http://schemas.android.com/apk/res/android}'

def fail(message): raise AssertionError(message)
def need(text, token, message=None):
    if token not in text: fail(message or f'missing {token!r}')
def absent(text, token, message=None):
    if token in text: fail(message or f'forbidden {token!r}')
def sha(path): return hashlib.sha256(path.read_bytes()).hexdigest()
def block(text, start, end):
    if start not in text: fail(f'cannot find block start {start!r}')
    tail=text.split(start,1)[1]
    if end not in tail: fail(f'cannot find block end {end!r}')
    return tail.split(end,1)[0]

def main():
    ap=argparse.ArgumentParser(); ap.add_argument('--root',default='.'); ap.add_argument('--skip-version',action='store_true'); args=ap.parse_args()
    root=Path(args.root).resolve(); toolroot=Path(__file__).resolve().parents[1]
    mainp=root/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'; manifestp=root/'app/src/main/AndroidManifest.xml'; buildp=root/'app/build.gradle.kts'; rulesp=root/'rules/PROJECT_RULES.md'
    contractp=root/'tests/golden/1.4.2_landscape_width_rotation_state_contract.json'
    if not contractp.is_file(): contractp=toolroot/'tests/golden/1.4.2_landscape_width_rotation_state_contract.json'
    for p in (mainp,manifestp,buildp,rulesp,contractp):
        if not p.is_file(): fail(f'missing {p}')
    src=mainp.read_text(); build=buildp.read_text(); rules=rulesp.read_text(); contract=json.loads(contractp.read_text())
    if not args.skip_version:
        need(build,'versionCode = 1403'); need(build,'versionName = "1.4.3"')
        need(rules,'## Release 1.4.3 landscape filter width and rotation state retention')
    single=block(src,'private fun ExpressiveSingleFilterSelector(','private fun ExpressiveFilterBar(')
    for token in [
        'val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE',
        'val dropdownWidth = if (landscape) maxWidth else maxWidth.coerceAtMost(560.dp)',
        'modifier = Modifier.width(dropdownWidth).height(dropdownDesiredHeight)',
        '((usableHeight.value * 0.62f).dp).coerceIn(220.dp, 440.dp)',
        'size.coerceIn(1, if (landscape) 4 else 7)',
        'modifier = Modifier.width(72.dp).onFocusChanged',
        'textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center)',
        'Box(Modifier.fillMaxSize().nestedScroll(boundaryScrollConnection))'
    ]: need(single,token,'1.4.3 filter contract drift: '+token)
    multi=block(src,'private fun ExpressiveMultiFilterBar(','private fun ExpressiveToggleRow(')
    need(multi,'.heightIn(min = 56.dp, max = 420.dp).nestedScroll(boundaryScrollConnection)','1.4.1 multi-filter scroll containment regressed')
    turn=block(src,'private fun TurnipDriverManagerTable(','private fun turnipDriverStateLabel(')
    if turn.count('ExpressiveInfoPill("Vulkan® library", info.libraryName ?: "Not available"') < 2: fail('1.4.1 Turnip library metadata regressed')
    if turn.count('ExpressiveInfoPill("Description", info.description?.takeIf { it.isNotBlank() } ?: "Not provided"') < 2: fail('1.4.1 Turnip description metadata regressed')
    app=block(src,'private fun VulkanScopeApp(','@Composable\nprivate fun SurfaceProbe(')
    need(app,'stateSaver = androidx.compose.runtime.saveable.Saver<Page, String>','active destination must use a String-backed saveable state')
    need(app,'restore = { saved -> Page.values().find { it.name == saved } ?: Page.Overview }','active destination restoration fallback missing')
    need(app,'var settingsSection by rememberSaveable { mutableStateOf<SettingsSection?>(null) }','settings subsection retention regressed')
    need(app,'var selectedDeviceIndex by rememberSaveable { mutableIntStateOf(0) }','selected device retention regressed')
    surface_ready=block(src,'surfaceReady = { hostGeneration, surface ->','surfaceDestroyed = { hostGeneration, surface ->')
    need(surface_ready,'latestReport == null && !collectionInFlight && pendingCollectionTasks.isEmpty()','fresh startup collection gate missing')
    need(surface_ready,'requestReportCollection()','fresh startup must still collect')
    need(surface_ready,'else if (latestReport != null) {\n                            requestSurfaceRefresh(surface)','ordinary Surface recreation must stay Surface-only when report exists')
    tree=ET.parse(manifestp); activity=None
    for node in tree.getroot().findall('./application/activity'):
        if node.attrib.get(ANDROID_NS+'name')=='.MainActivity': activity=node; break
    if activity is None: fail('MainActivity manifest entry missing')
    values=set((activity.attrib.get(ANDROID_NS+'configChanges') or '').split('|'))
    required={'orientation','screenSize','smallestScreenSize','screenLayout','keyboardHidden'}
    if not required.issubset(values): fail('rotation/window-size configChanges incomplete: '+','.join(sorted(required-values)))
    if (root/'screenshots').exists(): fail('top-level screenshots directory must remain excluded')
    files=root/'files.txt'
    if files.is_file() and any(line.strip().startswith('screenshots/') for line in files.read_text().splitlines()): fail('screenshots entries remain in files.txt')
    for rel,expected in contract['immutable_app_src_main_sha256'].items():
        p=root/rel
        if not p.is_file() or sha(p)!=expected: fail('unrelated app/src/main drift: '+rel)
    print('release_1403 verifier: PASS')

if __name__=='__main__':
    try: main()
    except Exception as exc:
        print('release_1403 verifier: FAIL:',exc,file=sys.stderr); sys.exit(1)
