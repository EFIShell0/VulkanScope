#!/usr/bin/env python3
import argparse
import re
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', type=Path, default=Path(__file__).resolve().parents[1])
parser.add_argument('--skip-version', action='store_true')
args = parser.parse_args()
root = args.root.resolve()
errors = []
main = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
rules = (root / 'rules/PROJECT_RULES.md').read_text(encoding='utf-8')
manifest = (root / 'app/src/main/AndroidManifest.xml').read_text(encoding='utf-8')


def need(condition, message):
    if not condition:
        errors.append(message)


def block(start, end):
    a = main.find(start)
    b = main.find(end, a + 1) if a >= 0 else -1
    need(a >= 0 and b > a, f'block missing: {start}')
    return main[a:b] if a >= 0 and b > a else ''


vm = re.search(r'versionName\s*=\s*"(\d+)\.(\d+)\.(\d+)"', gradle)
cv = tuple(map(int, vm.groups())) if vm else (0, 0, 0)
if not args.skip_version:
    cm = re.search(r'versionCode\s*=\s*(\d+)', gradle)
    expected = cv[0] * 1000 + cv[1] * 100 + cv[2]
    need(vm is not None and cm is not None and cv >= (1, 0, 9) and int(cm.group(1)) == expected, 'release identity is not a retained 1.0.9+ semantic identity')
need('kBaseline = "Vulkan 1.4.362"' in (root / 'app/src/main/cpp/registry_query_catalog.h').read_text(encoding='utf-8'), 'Vulkan baseline drifted')
need('android.permission.MANAGE_EXTERNAL_STORAGE' not in manifest, 'all-files storage permission is forbidden')

close_dialog = block('private fun ExpressiveDetailDialog(', 'private val FORMAT_USAGE_FILTERS')
for token in ['val dialogMaxHeight =', '.weight(1f, fill = false)', 'ExpressiveContainedIconTextButton("Close", R.drawable.ic_close)']:
    need(token in close_dialog, f'detail-dialog clipping repair missing: {token}')
need(close_dialog.count('.heightIn(max = dialogMaxHeight)') >= 2, 'detail-dialog outer/content max-height constraints missing')
contained_icon = block('private fun ExpressiveContainedIconTextButton(', '@Composable\nprivate fun ExpressiveLinearProgressIndicator')
for token in ['modifier = modifier.heightIn(min = 48.dp)', 'verticalAlignment = Alignment.CenterVertically', 'Icon(painterResource(icon)', 'Text(label, fontWeight = fontWeight']:
    need(token in contained_icon, f'contained icon/text alignment contract missing: {token}')
need('Box(Modifier.size(20.dp)' not in contained_icon, 'legacy nested icon box remains in contained icon/text action')

action = block('private fun ExpressiveActionButton(', '@Composable\nprivate fun ExpressiveExternalLinkRow')
need('Surface(' in action and 'Card(' not in action, 'chevron action surface remains whole-card clickable')
need('IconButton(' in action and 'onClick = onClick' in action, 'chevron action lacks trailing icon-only activation')
need(('trailingIcon: Int = R.drawable.ic_chevron_right' in action and action.count('AnimatedContent(targetState = trailingIcon') >= 2) if cv >= (1, 2, 0) else action.count('R.drawable.ic_chevron_right') >= 2, 'responsive chevron action variants missing')
overview_destination = block('private fun OverviewDestinationCard(', '@Composable\nprivate fun QuickAccessCard')
if cv >= (1, 2, 2):
    shared_destination = block('private fun ExpressiveDestinationCard(', '@Composable\nprivate fun OverviewDestinationCard')
    need('ExpressiveDestinationCard(title, subtitle, pageIcon(destination)) { navigate(destination) }' in overview_destination, 'Overview destination does not delegate to the shared non-card action surface')
    need('Surface(' in shared_destination and '\n    Card(' not in shared_destination, 'shared destination remains whole-card clickable')
    need('IconButton(' in shared_destination and 'onClick = onClick' in shared_destination, 'shared destination trailing chevron is not the only activation target')
else:
    need('Surface(' in overview_destination and '\n    Card(' not in overview_destination, 'Overview destination remains whole-card clickable')
    need('IconButton(' in overview_destination and 'onClick = { navigate(destination) }' in overview_destination, 'Overview destination trailing chevron is not the only activation target')
detail_affordance = block('private fun DetailAffordance(', '@Composable\nprivate fun ScrollableDetailDialog')
if cv >= (1, 0, 13):
    chevron_affordance = block('private fun ChevronAffordance(', '@Composable\nprivate fun DetailAffordance')
    need('ChevronAffordance("Details", "Open details", onClick)' in detail_affordance, 'Details affordance does not delegate to the shared trailing-chevron target')
    need('Surface(' in chevron_affordance and 'IconButton(onClick = onClick' in chevron_affordance and 'R.drawable.ic_chevron_right' in chevron_affordance, 'shared chevron affordance is not trailing-icon targeted')
else:
    need('Surface(' in detail_affordance and 'IconButton(onClick = onClick' in detail_affordance, 'Details affordance is not trailing-chevron targeted')

system_model = block('private data class SystemDriverSummary(', 'private data class FallbackTurnipCandidate')
for token in ['val capturedAtMillis: Long', 'val deviceName: String', 'val driverVersion: String', 'val driverName: String?', 'val loaderVersion: String']:
    need(token in system_model, f'System driver summary field missing: {token}')
system_helpers = block('private fun systemDriverSummaryFromReport(', 'private fun scanTurnipBundle')
for token in ['report.devices.firstOrNull()', 'device.detailedProperties.firstOrNull', 'property("driverName")', 'private fun readSystemDriverSummary(', 'private fun persistSystemDriverSummary(', 'system_driver_summary_v1']:
    need(token in system_helpers, f'System driver summary evidence/persistence missing: {token}')
settings = block('private fun SettingsPage(', '@Composable\nprivate fun UnavailableTurnipKeyValue')
for token in ['if (mode == DriverMode.SYSTEM && completeReportReady) systemDriverSummaryFromReport(report)', 'persistSystemDriverSummary', 'SystemDriverManagerRow(', 'SystemDriverDetailsDialog(', 'detailsSystemDriver']:
    need(token in settings, f'System driver manager detail integration missing: {token}')
system_row = block('private fun SystemDriverManagerRow(', '@Composable\nprivate fun SystemDriverDetailsDialog')
for token in ['summary: SystemDriverSummary?', 'CapabilityKeyValue("GPU"', 'CapabilityKeyValue("Driver"', 'CapabilityKeyValue("Version"', 'DetailAffordance(onDetails)', 'TurnipStatePill("ACTIVE", true, true)']:
    need(token in system_row, f'System driver row parity missing: {token}')
system_dialog = block('private fun SystemDriverDetailsDialog(', '@Composable\nprivate fun UnavailableTurnipKeyValue')
for token in ['ScrollableDetailDialog(title = "System Vulkan driver"', 'CapabilityKeyValue("Evidence source"', 'CapabilityKeyValue("Driver version"', 'CapabilityKeyValue("Driver name"', 'CapabilityKeyValue("Vulkan API"', 'CapabilityKeyValue("Loader version"']:
    need(token in system_dialog, f'System driver detail field missing: {token}')
need('Turnip' not in system_dialog, 'System detail dialog misattributes Turnip evidence')

analysis = block('private fun LazyListScope.analysisWorkspaceItems(', '@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun DetailAffordance')
need('ExpressiveContainedIconTextButton("Evidence provenance", R.drawable.ic_evidence)' in analysis, 'Evidence provenance is not a contained icon action')
need('ExpressiveContainedIconTextButton("Add to watch list", R.drawable.ic_watch_add' in analysis, 'Add to watch list is not a contained icon action')
need(('TransientActionButton("Copy link", "Copy permalink to clipboard", R.drawable.ic_copy)' in analysis) if cv >= (1, 2, 0) else ('ExpressiveContainedIconTextButton("Copy link", R.drawable.ic_link)' in analysis), 'Copy link action does not match the retained/current semantic icon contract')
for drawable in (['ic_copy.xml', 'ic_watch_add.xml'] if cv >= (1, 2, 0) else ['ic_link.xml', 'ic_watch_add.xml']):
    need((root / 'app/src/main/res/drawable' / drawable).is_file(), f'required action drawable missing: {drawable}')

key_value = block('private fun CapabilityKeyValue(', 'private fun evidenceStateAccent')
for token in ['var pressed by remember(key, value)', 'onPress = {', 'tryAwaitRelease()', 'animateFloatAsState(', 'graphicsLayer(scaleX = pressScale, scaleY = pressScale)', 'background(VulkanAccentContainer.copy(alpha = pressHighlightAlpha)']:
    need(token in key_value, f'long-press visual feedback missing: {token}')
need('onLongPress = { showEvidenceActions = true }' in key_value, 'long-press Evidence Inspector behavior drifted')

search = block('private fun ExpressiveSearchField(', '@Composable\nprivate fun ExpressiveFilterChip')
need('enabled: Boolean = true' in search and 'enabled = enabled' in search, 'search field lacks disabled-state support')
database = analysis[analysis.find('CapabilitySectionCard("Compare with VulkanScope Database")'):]
need('enabled = networkAvailable,\n                    placeholderText = "64-character report id"' in database, 'Database report-id input is not offline-gated')
need('enabled = networkAvailable && !state.databaseLoading && state.databaseReportId.length == 64' in database, 'Database fetch gate drifted')
need('color = ComposeColor(0xFFFFC857)' in database and 'Public Database report-id lookup is locked until Android reports a validated internet connection.' in database, 'offline Database reason is not amber')

for text in [
    'VulkanScope-*-analysis.json',
    'VulkanScope-<GPU>-analysis.json',
    'VulkanScope-*-minimum.json',
    'VulkanScope-<profile>-minimum.json',
    'VulkanScope-<GPU>-technicalReport.json',
    'Android/data/com.efishell.vulkanscope/files/Documents',
    'Android/data/com.efishell.vulkanscope/files/Download',
    'files/analysis_exchange',
    'turnip_01.zip through turnip_10.zip',
    'Android/data/com.efishell.vulkanscope/files/Download',
    'files/turnip_imports',
    'VulkanScope-<GPU>-report.txt',
    'VulkanScope-<GPU>-report.html',
    'public Download'
]:
    need(text in main, f'user-visible no-SAF guidance missing: {text}')

helpers = block('private fun analysisExchangeRoots(', 'private fun validateAnalysisSnapshot')
for token in ['getExternalFilesDirs(Environment.DIRECTORY_DOCUMENTS)', 'getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS)', 'File(context.filesDir, "analysis_exchange")', '.take(64)', 'canonical.parentFile != root']:
    need(token in helpers, f'bounded Analysis fallback regressed: {token}')
turnip = block('    private fun fallbackTurnipSearchRoots()', '    private fun openFallbackTurnipImportDialog()')
for token in ['File(filesDir, "turnip_imports")', 'getExternalFilesDirs(null)', 'getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS)', 'getExternalFilesDirs(Environment.DIRECTORY_DOCUMENTS)', '"turnip_%02d.zip"']:
    need(token in turnip, f'bounded Turnip fallback regressed: {token}')
for forbidden in ['Environment.getExternalStorageDirectory()', 'android.permission.MANAGE_EXTERNAL_STORAGE']:
    need(forbidden not in main, f'broad-storage access introduced: {forbidden}')

need('## Release 1.0.9 detail-dialog, action-target, driver-evidence and fallback-guidance requirements' in rules, 'PROJECT_RULES 1.0.9 section missing')
need((root / 'rules/1.0.9_DETAIL_ACTION_DRIVER_FALLBACK_UI_AUDIT.md').is_file(), '1.0.9 audit record missing')
need('FontFamily(' not in main and not (root / 'app/src/main/res/font').exists(), 'system-font substitution contract regressed')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 1.0.9 detail/action/System-evidence/offline/fallback UI contract')
