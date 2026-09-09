#!/usr/bin/env python3
import argparse
import re
from pathlib import Path

parser=argparse.ArgumentParser()
parser.add_argument('--root',type=Path,default=Path(__file__).resolve().parents[1])
parser.add_argument('--skip-version',action='store_true')
args=parser.parse_args()
root=args.root.resolve()
errors=[]
main_path=root/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
main=main_path.read_text(encoding='utf-8')
gradle=(root/'app/build.gradle.kts').read_text(encoding='utf-8')
manifest=(root/'app/src/main/AndroidManifest.xml').read_text(encoding='utf-8')
rules=(root/'rules/PROJECT_RULES.md').read_text(encoding='utf-8')
current_1007='versionName = "1.0.7"' in gradle

def need(cond,msg):
    if not cond: errors.append(msg)

def block(start_token,end_token):
    a=main.find(start_token)
    b=main.find(end_token,a+1) if a>=0 else -1
    need(a>=0 and b>a,f'block missing: {start_token}')
    return main[a:b] if a>=0 and b>a else ''

if not args.skip_version:
    need(any(f'versionName = "{name}"' in gradle and f'versionCode = {code}' in gradle for name, code in [('1.0.4', 1004), ('1.0.5', 1005), ('1.0.6', 1006), ('1.0.7', 1007), ('1.0.8', 1008), ('1.0.9', 1009), ('1.0.10', 1010), ('1.0.11', 1011), ('1.0.12', 1012), ('1.0.13', 1013), ('1.0.14', 1014), ('1.0.15', 1015), ('1.0.16', 1016), ('1.0.17', 1017), ('1.0.18', 1018)]),'release identity is not a retained 1.0.4+ identity')
need('kBaseline = "Vulkan 1.4.362"' in (root/'app/src/main/cpp/registry_query_catalog.h').read_text(encoding='utf-8'),'Vulkan baseline drifted')
need('implementation("androidx.compose.foundation:foundation:1.12.0")' in gradle,'Compose Foundation pin drifted')
need('implementation("androidx.compose.material3:material3:1.5.0-alpha27")' in gradle,'Material 3 pin drifted')
need(manifest.count('android.permission.ACCESS_NETWORK_STATE')==1,'ACCESS_NETWORK_STATE contract drifted')
need('android.permission.MANAGE_EXTERNAL_STORAGE' not in manifest,'all-files storage permission is forbidden')

if '    private fun applyValidatedNetworkState(validated: Boolean) {' in main:
    network=block('    private fun applyValidatedNetworkState(validated: Boolean) {','    override fun onResume()')
    network_tokens = [
        'val previous = lastValidatedNetwork',
        'validatedNetworkAvailable = validated',
        'networkStateKnown = true',
        'if (previous == null)',
        'networkBannerState = NetworkBannerState.HIDDEN',
        'networkBannerState = if (validated) NetworkBannerState.CONNECTED else NetworkBannerState.DISCONNECTED',
        'var remainingVisibleMillis = 4_500L',
        'remainingVisibleMillis -= step',
        'if (networkBannerGeneration == generation) networkBannerState = NetworkBannerState.HIDDEN'
    ]
    if not current_1007: network_tokens.append('if (collectionStatus == CollectionStatus.COLLECTING)')
    for token in network_tokens: need(token in network,f'network transition/collection arbitration missing: {token}')
    if current_1007: need('if (collectionStatus == CollectionStatus.COLLECTING)' not in network,'1.0.7 transition lifetime must not pause during collection')
    callback=block('    private val networkCallback = object : ConnectivityManager.NetworkCallback() {','    override fun onStart()')
    need('applyValidatedNetworkState(false)' in callback,'successor default-network loss is not published explicitly')
    need('applyValidatedNetworkState(hasValidatedInternetCapabilities(networkCapabilities))' in callback,'successor callback capability evidence is not retained')
else:
    network=block('    private fun publishValidatedNetworkState() {','    override fun onResume()')
    network_tokens = [
        'val previous = lastValidatedNetwork',
        'validatedNetworkAvailable = validated',
        'networkStateKnown = true',
        'if (previous == null)',
        'networkBannerState = NetworkBannerState.HIDDEN',
        'networkBannerState = if (validated) NetworkBannerState.CONNECTED else NetworkBannerState.DISCONNECTED',
        'var remainingVisibleMillis = 4_500L',
        'remainingVisibleMillis -= step',
        'if (networkBannerGeneration == generation) networkBannerState = NetworkBannerState.HIDDEN'
    ]
    if not current_1007: network_tokens.append('if (collectionStatus == CollectionStatus.COLLECTING)')
    for token in network_tokens: need(token in network,f'network transition/collection arbitration missing: {token}')
    if current_1007: need('if (collectionStatus == CollectionStatus.COLLECTING)' not in network,'1.0.7 transition lifetime must not pause during collection')
for forbidden in ['OkHttp','Request.Builder','InetAddress','HttpURLConnection','Socket(']:
    need(forbidden not in network,f'network status must not probe connectivity: {forbidden}')

host=block('private fun ConnectivityStatusHost(','@Composable\nprivate fun OfflineFeatureAvailabilityBanner')
host_tokens=['networkStateKnown && !networkAvailable','transitionState == NetworkBannerState.HIDDEN','delay(380L)']
if current_1007:
    host_tokens += ['NetworkStatusBanner(transitionState)','OfflineFeatureAvailabilityBanner(collectionStatus == CollectionStatus.COLLECTING)']
else:
    host_tokens += ['collectionStatus == CollectionStatus.COLLECTING','OfflineFeatureAvailabilityBanner()']
for token in host_tokens: need(token in host,f'persistent offline arbitration missing: {token}')
offline_start='private fun OfflineFeatureAvailabilityBanner(collectionInProgress: Boolean)' if current_1007 else 'private fun OfflineFeatureAvailabilityBanner()'
offline=block(offline_start,'@Composable\nprivate fun NetworkStatusBanner')
offline_tokens=['R.drawable.ic_info','ComposeColor(0xFF16344F)','ComposeColor(0xFF5CA9FF)','Internet features are unavailable','liveRegion = LiveRegionMode.Polite']
offline_tokens.append('Vulkan collection continues offline' if current_1007 else 'Database submission/fetching, web links and update checks remain disabled')
for token in offline_tokens: need(token in offline,f'offline information banner missing: {token}')
transition=block('private fun NetworkStatusBanner(','@Composable\nprivate fun UpdateStatusBanner')
for token in ['var renderedState by remember','if (state != NetworkBannerState.HIDDEN) renderedState = state','val connected = renderedState == NetworkBannerState.CONNECTED','visible = state != NetworkBannerState.HIDDEN']:
    need(token in transition,f'exit-state network banner fix missing: {token}')
need('val connected = state == NetworkBannerState.CONNECTED' not in transition,'exit animation still derives presentation from HIDDEN state')

for token in [
    'val networkAvailable = LocalValidatedNetwork.current',
    'enabled = !submissionInFlight && completeReportReady && networkAvailable',
    'enabled = networkAvailable && !state.databaseLoading && state.databaseReportId.length == 64',
    'enabled = directUpdatesEnabled && networkAvailable',
    'ExpressivePrimaryButton("Download APK", enabled = networkAvailable',
    'ExpressiveTextButton("Open Khronos specification", enabled = networkAvailable',
    'if (!hasValidatedInternet(context)) return@withContext "Submission blocked: no validated internet connection is available."',
    'if (!hasValidatedInternet(context)) {\n                state.databaseRemoteLeaves = emptyList()',
    'if (!validatedDefaultNetwork()) {\n            if (showProgress) updateStatus = UpdateStatus.Failed',
    'if (!validatedDefaultNetwork()) {\n            updateStatus = UpdateStatus.Failed'
]: need(token in main,f'offline action gate missing: {token}')

for token in [
    'private const val TURNIP_MANAGER_MAX_DRIVERS = 10',
    'private const val TURNIP_MANAGER_ROOT_NAME = "turnip_drivers"',
    'private const val TURNIP_MANAGER_SOURCE_NAME = "source.json"',
    'private const val TURNIP_ACTIVE_SLOT_PREF = "turnip_active_slot"',
    'private fun turnipSlotRoot(',
    'private fun readManagedTurnipDrivers(',
    'private fun migrateLegacyTurnipBundleIfNeeded(',
    'private fun activateManagedTurnipDriver(slot: Int)',
    'private fun removeManagedTurnipDriver(slot: Int)',
    'CapabilitySectionCard("Turnip driver manager")',
    'TurnipDriverManagerTable(',
    'TurnipDriverDetailsDialog(',
    'The 10-driver limit has been reached',
    'Driver imported into slot %02d. Activate it from the Turnip driver manager.'
]: need(token in main,f'Turnip manager contract missing: {token}')
settings=block('private fun SettingsPage(','@Composable\nprivate fun TurnipDriverManagerTable')
if current_1007:
    need(settings.count('DriverOption(')==1 and 'DriverMode.TURNIP,' not in settings,'1.0.7 must retain only the System source selector')
    need('CapabilitySectionCard("Turnip driver manager")' in settings,'1.0.7 Turnip manager must remain persistent below System')
else:
    need('onInstallDriverBundle' not in settings[settings.find('DriverOption(\n                    DriverMode.TURNIP'):settings.find('if (turnipSupport == TurnipSupport.SUPPORTED)',settings.find('DriverOption(\n                    DriverMode.TURNIP'))] if 'DriverOption(\n                    DriverMode.TURNIP' in settings else False,'choosing Turnip directly launches import instead of manager')
    need('turnipManagerExpanded = true' in settings,'Turnip option does not open manager')
table_end='private fun turnipDriverStateLabel' if current_1007 else '@Composable\nprivate fun TurnipStatePill'
table=block('private fun TurnipDriverManagerTable(',table_end)
need('FlowRow(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalArrangement = Arrangement.spacedBy(2.dp))' in table,'wide Turnip manager actions do not wrap at large text/intermediate widths')

install=block('    private suspend fun installDriverBundleIo(','    private fun activateManagedTurnipDriver')
for token in [
    'firstOrNull { !turnipSlotRoot(filesDir, it).exists() }',
    'BoundedDriverArchiveInputStream(input, TURNIP_ARCHIVE_INPUT_MAX_BYTES)',
    'if (++entryCount > maxEntries)',
    'if (fileBytes > maxFileBytes || totalBytes > maxTotalBytes)',
    'safe.path.startsWith(canonicalTempDir.path + File.separator)',
    'metadataFiles.size != 1',
    'schemaVersion != 1',
    'libraries.size != 1',
    'library.length() == 0L',
    'ensureTurnipNativeLibrariesReadOnly(bundleScan)',
    'writeTurnipSourceInfo(File(tempSlotRoot, TURNIP_MANAGER_SOURCE_NAME), sourceInfo, importedAt)',
    'if (!tempSlotRoot.renameTo(finalSlotRoot))',
    'tempSlotRoot.deleteRecursively()'
]: need(token in install,f'Turnip atomic/security import contract missing: {token}')
need('applyDriverModeChange' not in install,'import must not auto-activate a Turnip driver')

source_write=block('private fun writeTurnipSourceInfo(','private fun legacyTurnipSourceInfo')
for token in ['source.name.take(512)','source.location.take(4096)','put("importedAtMillis", importedAtMillis)','payload.size > 64 * 1024','output.fd.sync()','temp.renameTo(sourceFile)']:
    need(token in source_write,f'private bounded source provenance persistence missing: {token}')
manager_read=block('private fun readManagedTurnipDrivers(','private fun migrateLegacyTurnipBundleIfNeeded')
need('(1..TURNIP_MANAGER_MAX_DRIVERS)' in manager_read,'manager does not bound slot enumeration')
need('readTurnipBundleInfo(turnipSlotBundleRoot(filesDir, slot)' in manager_read,'manager does not bind metadata to fixed slot bundle')

fallback=block('    private fun turnipSafPickerAvailable()','    private fun openDriverBundlePicker()')
for token in [
    'Intent(Intent.ACTION_OPEN_DOCUMENT)',
    '.setType("*/*")',
    'File(filesDir, "turnip_imports")',
    'getExternalFilesDirs(null)',
    'getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS)',
    'getExternalFilesDirs(Environment.DIRECTORY_DOCUMENTS)',
    '"turnip_%02d.zip"',
    'canonical.parentFile != root',
    'size > TURNIP_ARCHIVE_INPUT_MAX_BYTES',
    'FallbackTurnipDialogData',
    'TURNIP_MANAGER_MAX_DRIVERS - occupied'
]: need(token in fallback,f'SAF-unavailable fallback contract missing: {token}')
for forbidden in ['getExternalStoragePublicDirectory','/sdcard','MANAGE_EXTERNAL_STORAGE','Environment.getExternalStorageDirectory']:
    need(forbidden not in fallback,f'fallback performs broad shared-storage access: {forbidden}')
need('FallbackTurnipImportDialog(' in main and 'Import selected' in main and 'Role.Checkbox' in main,'fallback review/checkbox import UI missing')

serialization=block('private fun technicalReportJson','private fun reportToText')
for token in ['turnip_source_name','turnip_source_location','turnip_source_size','turnip_source_modified','turnip_imported_at','source.json','turnip_drivers']:
    need(token not in serialization,f'Turnip source provenance leaked into technicalReport: {token}')
report_text=block('private fun reportToText','private fun reportToHtml')
for token in ['turnip_source_name','turnip_source_location','source.json','turnip_drivers']:
    need(token not in report_text,f'Turnip source provenance leaked into TXT export: {token}')

carousel=block('private fun ExpressiveFilterCarousel(','@Composable\nprivate fun ExpressiveFilterBar')
if current_1007:
    for token in ['PaddingValues(horizontal = 60.dp)','width(82.dp)','animateFloatAsState','Brush.horizontalGradient','if (canMoveLeft)','if (canMoveRight)','LocalLayoutDirection.current','state.canScrollBackward','state.canScrollForward','ic_chevron_left','ic_chevron_right']:
        need(token in carousel,f'filter carousel edge/RTL contract missing: {token}')
    need('drawWithContent' not in carousel and 'ComposeColor.Black.copy' not in carousel,'1.0.7 must not restore hard black continuation drawing')
else:
    for token in ['drawWithContent','Brush.horizontalGradient','if (canMoveLeft','if (canMoveRight','LocalLayoutDirection.current','state.canScrollBackward','state.canScrollForward','ic_chevron_left','ic_chevron_right']:
        need(token in carousel,f'filter carousel edge/RTL contract missing: {token}')
    need('val fadeWidth = 22.dp.toPx()' in carousel or 'val fadeWidth = 30.dp.toPx()' in carousel,'filter carousel edge extent missing')
    need('ComposeColor(0xD9000000)' in carousel or 'edgeShadow = ComposeColor.Black.copy(alpha = 0.34f)' in carousel,'filter carousel edge shadow implementation missing')
need(carousel.count('Brush.horizontalGradient') == 2,'filter carousel must retain two horizontal edge gradients')
need('onKeyEvent' not in carousel and 'onPreviewKeyEvent' not in carousel,'carousel added custom TV key interception')

need('## Release 1.0.4 offline availability, Turnip driver manager and filter-edge continuity requirements' in rules,'PROJECT_RULES 1.0.4 section missing')
need((root/'rules/1.0.4_OFFLINE_TURNIP_MANAGER_FILTER_EDGE_AUDIT.md').is_file(),'1.0.4 audit record missing')
need('FontFamily(' not in main and not (root/'app/src/main/res/font').exists(),'system-font contract regressed')

if errors:
    for e in errors: print('FAIL:',e)
    raise SystemExit(1)
print('PASS VulkanScope 1.0.4 offline/Turnip-manager/filter-edge contract')
