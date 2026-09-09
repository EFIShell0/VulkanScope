#!/usr/bin/env python3
import argparse
from pathlib import Path

parser=argparse.ArgumentParser()
parser.add_argument('--root',type=Path,default=Path(__file__).resolve().parents[1])
parser.add_argument('--skip-version',action='store_true')
args=parser.parse_args()
root=args.root.resolve()
errors=[]
main=(root/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
gradle=(root/'app/build.gradle.kts').read_text(encoding='utf-8')
rules=(root/'rules/PROJECT_RULES.md').read_text(encoding='utf-8')
manifest=(root/'app/src/main/AndroidManifest.xml').read_text(encoding='utf-8')
current_1007='versionName = "1.0.7"' in gradle

def need(cond,msg):
    if not cond: errors.append(msg)

def block(start,end):
    a=main.find(start)
    b=main.find(end,a+1) if a>=0 else -1
    need(a>=0 and b>a,f'block missing: {start}')
    return main[a:b] if a>=0 and b>a else ''

if not args.skip_version:
    if current_1007:
        need('versionCode = 1007' in gradle,'1.0.7 successor identity missing')
    else:
        need('versionCode = 1006' in gradle and 'versionName = "1.0.6"' in gradle,'1.0.6 release identity missing')
need('kBaseline = "Vulkan 1.4.362"' in (root/'app/src/main/cpp/registry_query_catalog.h').read_text(encoding='utf-8'),'Vulkan baseline drifted')
need(manifest.count('android.permission.ACCESS_NETWORK_STATE')==1,'ACCESS_NETWORK_STATE contract drifted')
need('android.permission.MANAGE_EXTERNAL_STORAGE' not in manifest,'all-files storage permission is forbidden')
callback=block('    private val networkCallback = object : ConnectivityManager.NetworkCallback() {','    override fun onStart()')
for token in ['override fun onAvailable(network: Network)','trackedDefaultNetwork = network','if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)','delay(80L)','override fun onLost(network: Network)','if (trackedDefaultNetwork == network)','trackedDefaultNetwork = null','applyValidatedNetworkState(false)','override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities)','applyValidatedNetworkState(hasValidatedInternetCapabilities(networkCapabilities))']:
    need(token in callback,f'ordered default-network callback contract missing: {token}')
need('validatedDefaultNetwork()' not in callback,'network callback must consume callback evidence instead of synchronously re-querying default state')
state=block('    private fun applyValidatedNetworkState(validated: Boolean) {','    override fun onResume()')
for token in ['val previous = lastValidatedNetwork','if (previous == validated) return','networkBannerGeneration += 1L','NetworkBannerState.DISCONNECTED','var remainingVisibleMillis = 4_500L','networkBannerState = NetworkBannerState.HIDDEN']:
    need(token in state,f'network transition state machine missing: {token}')
if current_1007:
    need('if (collectionStatus == CollectionStatus.COLLECTING)' not in state,'1.0.7 must not pause transition lifetime during collection')
else:
    need('if (collectionStatus == CollectionStatus.COLLECTING)' in state,'1.0.6 collection arbitration missing')
request=block('    private fun requestDriverModeChange(mode: DriverMode) {','    private fun confirmDriverModeChange()')
need(request.count('pendingDriverModeConfirmation = mode')==2,'System and Turnip driver-source changes must both enter confirmation state')
need('applyDriverModeChange(mode, false)' not in request,'request path must not bypass driver-source confirmation')
confirm=block('    private fun confirmDriverModeChange() {','    private fun prepareDriverSurfaceRebind()')
need(confirm.count('applyDriverModeChange(mode, false)')==2,'confirmed System/Turnip source switch path drifted')
activation=block('    private fun activateManagedTurnipDriver(slot: Int) {','    private fun removeManagedTurnipDriver(slot: Int)')
for token in ['pendingTurnipActivationSlot = slot','private fun confirmManagedTurnipDriverActivation()','pendingTurnipActivationSlot = null','applyDriverModeChange(DriverMode.TURNIP, true)']:
    need(token in activation,f'Turnip activation confirmation contract missing: {token}')
settings=block('private fun SettingsPage(','@Composable\nprivate fun TurnipDriverManagerTable')
if current_1007:
    need(settings.count('DriverOption(')==1 and 'DriverMode.TURNIP,' not in settings,'1.0.7 System-selector/managed-slot successor contract missing')
    need('CapabilitySectionCard("Turnip driver manager")' in settings,'Turnip manager is not persistent')
else:
    need('DriverMode.TURNIP,' in settings and 'turnipManagerExpanded = true' in settings,'1.0.6 Turnip selector/manager contract missing')
need('Driver changes are temporarily locked while VulkanScope is collecting a report.' in settings,'collection-time driver-lock explanation missing')
need('Switching Vulkan drivers mid-collection would mix evidence from different driver sessions.' in settings,'driver-lock reason incomplete')
table=block('private fun TurnipDriverManagerTable(','private fun turnipDriverStateLabel' if current_1007 else '@Composable\nprivate fun TurnipStatePill')
need(table.count('DetailAffordance { onDetails(driver) }')==2,'Turnip Details affordance drifted')
if current_1007:
    need(table.count('ExpressiveContainedIconTextButton("Remove", R.drawable.ic_delete, enabled = enabled)')==2,'1.0.7 contained Remove actions missing')
else:
    need(table.count('ExpressiveContainedTextButton("Remove", enabled = enabled)')==2,'1.0.6 contained Remove actions missing')
dialog=block('private fun ExpressiveDetailDialog(','private val FORMAT_USAGE_FILTERS')
need(('ExpressiveContainedIconTextButton("Close", R.drawable.ic_close)' if current_1007 else 'ExpressiveContainedTextButton("Close")') in dialog,'detail Close accent action drifted')
remove_dialog=settings[settings.find('title = { Text("Remove Turnip driver?") }'):]
need(('ExpressiveContainedIconTextButton("Remove", R.drawable.ic_delete, enabled = !turnipManagerBusy)' if current_1007 else 'ExpressiveContainedTextButton("Remove", enabled = !turnipManagerBusy)') in remove_dialog,'removal confirmation accent action drifted')
need('This slot is active. Removing it will deactivate Turnip, switch VulkanScope to the System driver' in remove_dialog,'active removal fallback disclosure missing')
carousel=block('private fun ExpressiveFilterCarousel(','@Composable\nprivate fun ExpressiveFilterBar')
if current_1007:
    for token in ['PaddingValues(horizontal = 60.dp)','width(82.dp)','animateFloatAsState','Brush.horizontalGradient','LocalLayoutDirection.current']:
        need(token in carousel,f'1.0.7 continuation successor contract missing: {token}')
    need('ComposeColor.Black.copy' not in carousel and 'drawWithContent' not in carousel,'hard black edge shadow returned')
else:
    for token in ['val fadeWidth = 30.dp.toPx()','edgeShadow = ComposeColor.Black.copy(alpha = 0.34f)','midShadow = ComposeColor.Black.copy(alpha = 0.14f)','softShadow = ComposeColor.Black.copy(alpha = 0.04f)','Brush.horizontalGradient','LocalLayoutDirection.current']:
        need(token in carousel,f'1.0.6 edge-shadow contract missing: {token}')
need(carousel.count('Brush.horizontalGradient')==2,'two directional continuation treatments required')
need('onKeyEvent' not in carousel and 'onPreviewKeyEvent' not in carousel,'custom TV key interception added')
need('## Release 1.0.6 driver confirmation, network transition and UI-coherence requirements' in rules,'PROJECT_RULES 1.0.6 section missing')
need((root/'rules/1.0.6_DRIVER_CONFIRMATION_NETWORK_UI_AUDIT.md').is_file(),'1.0.6 audit record missing')
need('FontFamily(' not in main and not (root/'app/src/main/res/font').exists(),'system-font substitution contract regressed')
if errors:
    for e in errors: print('FAIL:',e)
    raise SystemExit(1)
print('PASS VulkanScope retained 1.0.6 driver-confirmation/network-transition/UI-coherence contract')
