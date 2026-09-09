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

def need(cond,msg):
    if not cond: errors.append(msg)

def block(start,end):
    a=main.find(start)
    b=main.find(end,a+1) if a>=0 else -1
    need(a>=0 and b>a,f'block missing: {start}')
    return main[a:b] if a>=0 and b>a else ''

if not args.skip_version:
    need('versionCode = 1007' in gradle and 'versionName = "1.0.7"' in gradle,'1.0.7 release identity missing')
need('kBaseline = "Vulkan 1.4.362"' in (root/'app/src/main/cpp/registry_query_catalog.h').read_text(encoding='utf-8'),'Vulkan baseline drifted')

settings=block('private fun SettingsPage(','@Composable\nprivate fun TurnipDriverManagerTable')
need(settings.count('DriverOption(')==1,'Settings must expose only the System Vulkan driver option above the Turnip manager')
need('DriverMode.SYSTEM,' in settings,'System Vulkan driver option missing')
need('DriverMode.TURNIP,' not in settings,'legacy Turnip source selector remains')
need('turnipManagerExpanded' not in settings,'Turnip manager still depends on expansion/selector state')
need('CapabilitySectionCard("Turnip driver manager")' in settings,'Turnip driver manager is not always present')
need('TurnipSupport.UNSUPPORTED ->' in settings and 'TurnipSupport.UNKNOWN ->' in settings and 'TurnipSupport.SUPPORTED ->' in settings,'Turnip eligibility branches missing')
need(settings.count('Text("UNAVAILABLE", color = ComposeColor(0xFFFFC857)')>=2,'unsupported/unknown Turnip manager does not use amber unavailable state')
unsupported=settings[settings.find('TurnipSupport.UNSUPPORTED ->'):settings.find('TurnipSupport.UNKNOWN ->')]
need('ExpressiveMetricGrid' not in unsupported and 'Import driver ZIP' not in unsupported,'unsupported Turnip manager exposes installed/import controls')
need('Driver changes are temporarily locked while VulkanScope is collecting a report.' in settings,'collection driver-lock explanation missing')

source_model=block('private data class ManagedTurnipDriver(','private data class FallbackTurnipCandidate')
need('val sourceAvailable: Boolean' in source_model,'managed Turnip source availability state missing')
source_check=block('private fun turnipSourceAvailable(','private fun readManagedTurnipDrivers')
for token in ['location.startsWith("content://"','openFileDescriptor(Uri.parse(location), "r")','location.startsWith(File.separator)','it.isFile && it.canRead() && it.length() > 0L']:
    need(token in source_check,f'Turnip source availability check missing: {token}')
need('takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)' in main,'SAF source read permission is not retained best-effort')
state_helpers=block('private fun turnipDriverStateLabel(','@Composable\nprivate fun TurnipStatePill')
for token in ['driver.selected -> "ACTIVE"','driver.info.installed && driver.sourceAvailable -> "AVAILABLE"','else -> "UNAVAILABLE"','ComposeColor(0xFF73C991)','ComposeColor(0xFF9CCBFF)','ComposeColor(0xFFFFC857)']:
    need(token in state_helpers,f'Turnip state/color contract missing: {token}')

table=block('private fun TurnipDriverManagerTable(','private fun turnipDriverStateLabel')
need(table.count('DetailAffordance { onDetails(driver) }')==2,'Turnip Details action no longer matches Extensions affordance')
need(table.count('ExpressiveContainedIconTextButton("Remove", R.drawable.ic_delete')==2,'Turnip Remove trash-icon action missing in responsive layouts')
need((root/'app/src/main/res/drawable/ic_delete.xml').is_file(),'trash drawable missing')
remove_dialog=settings[settings.find('title = { Text("Remove Turnip driver?") }'):]
need('ExpressiveContainedIconTextButton("Remove", R.drawable.ic_delete' in remove_dialog,'Turnip removal confirmation lacks trash icon')

host=block('private fun ConnectivityStatusHost(','@Composable\nprivate fun OfflineFeatureAvailabilityBanner')
need('if (collectionStatus == CollectionStatus.COLLECTING) return' not in host,'connectivity UI is still suppressed during collection')
need('OfflineFeatureAvailabilityBanner(collectionStatus == CollectionStatus.COLLECTING)' in host,'offline banner does not model collection overlap')
offline=block('private fun OfflineFeatureAvailabilityBanner(','@Composable\nprivate fun NetworkStatusBanner')
need('collectionInProgress: Boolean' in offline,'offline banner lacks collection-aware text state')
need('Vulkan collection continues offline.' in offline,'offline+collecting explanation missing')
network_state=block('    private fun applyValidatedNetworkState(validated: Boolean) {','    override fun onResume()')
need('if (collectionStatus == CollectionStatus.COLLECTING)' not in network_state,'network transition timer still pauses during collection')
need('remainingVisibleMillis = 4_500L' in network_state,'network transition duration drifted')

database=block('CapabilitySectionCard("VulkanScope Database")','            }\n        }\n    }\n}')
for token in ['!completeReportReady && !networkAvailable -> "Waiting for complete Vulkan collection · internet unavailable"','Database submission is locked for two independent reasons: Vulkan collection is incomplete and Android does not report a validated internet connection.','When internet returns during collection, the network lock clears immediately; submission still waits for complete report evidence.']:
    need(token in database,f'combined Database collection/network state missing: {token}')

carousel=block('private fun ExpressiveFilterCarousel(','@Composable\nprivate fun ExpressiveFilterBar')
for token in ['contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 60.dp)','width(82.dp)','val leftVisualAlpha by animateFloatAsState(if (canMoveLeft) 1f else 0.42f, tween(220), label = "filterLeftAlpha")','val rightVisualAlpha by animateFloatAsState(if (canMoveRight) 1f else 0.42f, tween(220), label = "filterRightAlpha")','val leftContinuationAlpha by animateFloatAsState(if (canMoveLeft) 1f else 0f, tween(220), label = "filterLeftContinuation")','val rightContinuationAlpha by animateFloatAsState(if (canMoveRight) 1f else 0f, tween(220), label = "filterRightContinuation")','graphicsLayer(scaleX = leftScale, scaleY = leftScale)','graphicsLayer(scaleX = rightScale, scaleY = rightScale)']:
    need(token in carousel,f'carousel continuation/arrow animation contract missing: {token}')
need('drawWithContent' not in carousel and 'ComposeColor.Black.copy' not in carousel,'legacy hard black edge-shadow implementation remains')
need(carousel.count('Brush.horizontalGradient')==2,'carousel needs two surface-colored continuation masks')

dialog=block('private fun ExpressiveDetailDialog(','private val FORMAT_USAGE_FILTERS')
need('painterResource(R.drawable.ic_info)' in dialog and 'color = VulkanAccentContainer' in dialog,'detail-dialog info header icon missing')
need('ExpressiveContainedIconTextButton("Close", R.drawable.ic_close)' in dialog,'detail Close action lacks contained X icon')
icon_map=block('private fun capabilitySectionIcon(','@Composable\nprivate fun preferExpandedTextLayout')
need('title.equals("Extension explorer", true) -> R.drawable.ic_info' in icon_map,'Extension explorer does not use shared info glyph')

need('## Release 1.0.7 Turnip manager, combined connectivity and continuation UI requirements' in rules,'PROJECT_RULES 1.0.7 section missing')
need((root/'rules/1.0.7_TURNIP_CONNECTIVITY_FILTER_DIALOG_AUDIT.md').is_file(),'1.0.7 audit record missing')
need((root/'tests/golden/1.0.6_regression_contract.json').is_file(),'1.0.6 immutable predecessor contract missing')
need('FontFamily(' not in main and not (root/'app/src/main/res/font').exists(),'system-font substitution contract regressed')

if errors:
    for error in errors: print('FAIL:',error)
    raise SystemExit(1)
print('PASS VulkanScope 1.0.7 Turnip/connectivity/filter/dialog contract')
