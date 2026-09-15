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
    cp=root/'tests/golden/1.3.12_filter_driver_branding_contract.json'
    if not cp.is_file(): cp=toolroot/'tests/golden/1.3.12_filter_driver_branding_contract.json'
    for p in (mainp,buildp,rulesp,cp):
        if not p.is_file(): fail(f'missing {p}')
    src=mainp.read_text(); build=buildp.read_text(); rules=rulesp.read_text(); c=json.loads(cp.read_text())
    if not a.skip_version:
        need(build,'versionCode = 1313'); need(build,'versionName = "1.3.13"')
        need(rules,'## Release 1.3.13 filter anchoring, driver branding and Turnip metadata semantics')
    need(src,'import androidx.compose.foundation.layout.fillMaxHeight','retained compile fix regressed')
    filt=block(src,'private fun ExpressiveSingleFilterSelector(','private fun ExpressiveFilterBar(')
    for x in [
        'Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp))',
        '.clickable(enabled = enabled && !expanded, role = Role.Button) { expanded = true }',
        'BackHandler(enabled = dropdownMounted && expanded)',
        'if (imeVisible) focusManager.clearFocus(force = true)',
        'modifier = Modifier.size(56.dp).clip(closeShape).clickable(role = Role.Button) { expanded = false }',
        'modifier = Modifier.fillMaxWidth().clip(rowShape).clickable(role = Role.RadioButton) { onSelected(index) }',
        'enter = fadeIn(tween(220)) + scaleIn(tween(240), initialScale = 0.97f',
        'val selectedOffset = if (targetQuery.isBlank()) targetPageItems.indexOfFirst { it.first == selectedIndex } else -1',
        'candidate.isEmpty() -> pageField = value',
        'if (indexed.isNotEmpty() && pageCount > 1) {'
    ]: need(filt,x,'filter 1.3.13 contract drift: '+x)
    need(src,'import androidx.compose.ui.graphics.TransformOrigin','filter motion import missing')
    for x in ['Popup(','PopupPositionProvider','dismissOnClickOutside','onDismissRequest = { expanded = false }','onSelected(index); expanded = false','.clickable(enabled = enabled, role = Role.Button) { expanded = !expanded }','if (imeVisible) focusManager.clearFocus(force = true) else expanded = false']:
        absent(filt,x,'filter must remain in-layout and X-only dismiss: '+x)
    mesa=block(src,'private fun MesaOfficialLogoBadge(','private fun FileManagerNavigateArrow(')
    for x in ['color = VulkanAccentContainer','contentColor = VulkanAccentSoft','ColorFilter.tint(VulkanAccentSoft)','VulkanAccentSoft.copy(alpha = 0.34f)']:
        need(mesa,x,'Mesa badge accent-red contract drift: '+x)
    comp=block(src,'private fun CompositeMesaActionIcon(','private fun ExpressiveActionButton(')
    need(comp,'ColorFilter.tint(VulkanAccentSoft)','composite Mesa logo must use VulkanScope accent red')
    sysb=block(src,'private fun SystemDriverVendorBadge(','private fun SystemDriverDetailsDialog(')
    for x in ['val info = vendorInfo(vendorIdFromDisplay(vendorId) ?: -1L)','color = VulkanAccentContainer','modifier = Modifier.size(50.dp)','painter = painterResource(info.logo)','SystemDriverVendorBadge(summary?.vendorId)']:
        need(sysb,x,'System vendor badge contract drift: '+x)
    absent(sysb,'painterResource(R.drawable.ic_android)','System driver Android badge restored')
    turn=block(src,'private fun TurnipDriverManagerTable(','private fun turnipDriverStateLabel(')
    need(turn,'ExpressiveInfoPill("Driver name", info.driverName ?: "Not provided"','Turnip evidence must show driver name')
    absent(turn,'ExpressiveInfoPill("Driver version",','Turnip evidence pill must not mislabel Vulkan version as driver version')
    need(turn,'"Slot %02d · %s".format(java.util.Locale.ROOT, driver.slot, info.driverVersion ?: "version not provided")','slot/state Vulkan-version line changed')
    cand=block(src,'private fun TurnipArchiveCandidateDetailsDialog(','private fun SharedStorageBrowserDialog(')
    need(cand,'CapabilityKeyValue("Driver name", candidate.driverName ?: "Not exposed")')
    need(cand,'CapabilityKeyValue("Vulkan version", candidate.driverVersion ?: "Not exposed")')
    details=block(src,'private fun TurnipDriverDetailsDialog(','private fun DriverOption(')
    need(details,'CapabilityKeyValue("Driver name", info.driverName ?: "Not provided by package")')
    need(details,'CapabilityKeyValue("Vulkan version", info.driverVersion ?: "Not provided by package")')
    mesa_asset=root/'app/src/main/res/drawable-nodpi/mesa3d_logo.webp'
    if not mesa_asset.is_file() or sha(mesa_asset)!=c['mesa_asset_sha256']: fail('packaged Mesa geometry bytes changed')
    for rel,expected in c['immutable_app_src_main_sha256'].items():
        p=root/rel
        if not p.is_file() or sha(p)!=expected: fail('unrelated app/src/main drift: '+rel)
    print('release_1313 verifier: PASS')
if __name__=='__main__':
    try: main()
    except Exception as e: print('release_1313 verifier: FAIL:',e,file=sys.stderr); sys.exit(1)
