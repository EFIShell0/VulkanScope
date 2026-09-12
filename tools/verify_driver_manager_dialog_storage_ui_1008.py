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


if not args.skip_version:
    need('versionCode = 1008' in gradle and 'versionName = "1.0.8"' in gradle, '1.0.8 release identity missing')
need('kBaseline = "Vulkan 1.4.362"' in (root / 'app/src/main/cpp/registry_query_catalog.h').read_text(encoding='utf-8'), 'Vulkan baseline drifted')
need('android.permission.MANAGE_EXTERNAL_STORAGE' not in manifest, 'all-files storage permission is forbidden')

source_model = block('private data class ManagedTurnipDriver(', 'private data class FallbackTurnipCandidate')
need('val sourceAvailable: Boolean' in source_model, 'managed Turnip source availability state missing')
source_check = block('private fun turnipSourceAvailable(', 'private fun readManagedTurnipDrivers')
for token in ['location.startsWith("content://"', 'openFileDescriptor(Uri.parse(location), "r")', 'location.startsWith(File.separator)', 'it.isFile && it.canRead() && it.length() > 0L']:
    need(token in source_check, f'Turnip source availability check missing: {token}')
need('takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)' in main, 'SAF source read permission is not retained best-effort')

host = block('private fun ConnectivityStatusHost(', '@Composable\nprivate fun OfflineFeatureAvailabilityBanner')
need('if (collectionStatus == CollectionStatus.COLLECTING) return' not in host, 'connectivity UI is still suppressed during collection')
need('OfflineFeatureAvailabilityBanner(collectionStatus == CollectionStatus.COLLECTING)' in host, 'offline banner does not model collection overlap')
offline = block('private fun OfflineFeatureAvailabilityBanner(', '@Composable\nprivate fun NetworkStatusBanner')
need('collectionInProgress: Boolean' in offline, 'offline banner lacks collection-aware text state')
need('Vulkan collection continues offline.' in offline, 'offline+collecting explanation missing')
network_state = block('    private fun applyValidatedNetworkState(validated: Boolean) {', '    override fun onResume()')
need('if (collectionStatus == CollectionStatus.COLLECTING)' not in network_state, 'network transition timer still pauses during collection')
need('remainingVisibleMillis = 4_500L' in network_state, 'network transition duration drifted')

database = block('CapabilitySectionCard("VulkanScope Database")', '            }\n        }\n    }\n}')
for token in ['!completeReportReady && !networkAvailable -> "Waiting for complete Vulkan collection · internet unavailable"', 'Database submission is locked for two independent reasons: Vulkan collection is incomplete and Android does not report a validated internet connection.', 'When internet returns during collection, the network lock clears immediately; submission still waits for complete report evidence.']:
    need(token in database, f'combined Database collection/network state missing: {token}')

carousel = block('private fun ExpressiveFilterCarousel(', '@Composable\nprivate fun ExpressiveFilterBar')
for token in ['contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 60.dp)', 'width(82.dp)', 'val leftVisualAlpha by animateFloatAsState(if (canMoveLeft) 1f else 0.42f, tween(220), label = "filterLeftAlpha")', 'val rightVisualAlpha by animateFloatAsState(if (canMoveRight) 1f else 0.42f, tween(220), label = "filterRightAlpha")', 'val leftContinuationAlpha by animateFloatAsState(if (canMoveLeft) 1f else 0f, tween(220), label = "filterLeftContinuation")', 'val rightContinuationAlpha by animateFloatAsState(if (canMoveRight) 1f else 0f, tween(220), label = "filterRightContinuation")', 'graphicsLayer(scaleX = leftScale, scaleY = leftScale)', 'graphicsLayer(scaleX = rightScale, scaleY = rightScale)']:
    need(token in carousel, f'carousel continuation/arrow animation contract missing: {token}')
need('drawWithContent' not in carousel and 'ComposeColor.Black.copy' not in carousel, 'legacy hard black edge-shadow implementation remains')
need(carousel.count('Brush.horizontalGradient') == 2, 'carousel needs two surface-colored continuation masks')

hero = block('private fun HeroCard(', '@Composable\nprivate fun OverviewDestinationCard')
need('color = if (driverMode == DriverMode.TURNIP) VulkanAccentSoft else VulkanTextPrimary' in hero, 'Overview driver source colors are not Turnip accent/System primary')
need('fontWeight = FontWeight.Bold' in hero, 'Overview driver source label is not bold')

settings = block('private fun SettingsPage(', '@Composable\nprivate fun TurnipDriverManagerTable')
need('CapabilitySectionCard("Driver manager")' in settings, 'unified Driver manager card missing')
need('CapabilitySectionCard("Turnip driver manager")' not in settings, 'legacy Turnip driver manager card remains')
need('CapabilitySectionCard("Vulkan driver")' not in settings, 'legacy standalone Vulkan driver card remains')
need('SystemDriverManagerRow(' in settings, 'System Vulkan driver row is not inside Driver manager')
need('TurnipSupport.UNSUPPORTED ->' in settings and 'TurnipSupport.UNKNOWN ->' in settings and 'TurnipSupport.SUPPORTED ->' in settings, 'Turnip evidence branches missing')
need(settings.count('Text("UNAVAILABLE", color = ComposeColor(0xFFFFC857)') >= 2, 'Turnip ineligible/unknown eligibility does not retain amber Unavailable presentation')
need('Driver changes are temporarily locked while VulkanScope is collecting a report.' in settings, 'collection-time driver mutation lock explanation missing')

system_row = block('private fun SystemDriverManagerRow(', '@Composable\nprivate fun UnavailableTurnipKeyValue')
for token in ['Text("System Vulkan driver", color = VulkanTextPrimary, fontWeight = FontWeight.Bold', 'if (active) TurnipStatePill("ACTIVE", true, true)', 'ExpressiveContainedTextButton("Activate", enabled = enabled']:
    need(token in system_row, f'System driver manager state contract missing: {token}')

unavailable_row = block('private fun UnavailableTurnipKeyValue(', '@Composable\nprivate fun TurnipDriverManagerTable')
need('alpha(0.48f)' in unavailable_row and unavailable_row.count('TextDecoration.LineThrough') >= 2, 'unavailable Turnip metadata is not subdued and struck through')

table = block('private fun TurnipDriverManagerTable(', 'private fun turnipDriverStateLabel')
need('val unavailable = turnipDriverStateLabel(driver) == "UNAVAILABLE"' in table, 'unavailable Turnip row state missing')
need(table.count('if (!unavailable) DetailAffordance { onDetails(driver) }') == 2, 'unavailable Turnip rows still expose Details or responsive behavior drifted')
need(table.count('if (!unavailable && !driver.selected) ExpressiveContainedTextButton("Activate"') == 2, 'Turnip activation availability/selected-state contract drifted')
need(table.count('ExpressiveContainedIconTextButton("Remove", R.drawable.ic_delete, enabled = enabled)') == 2, 'Turnip Remove action must remain available in both layouts')
need('textDecoration = if (unavailable) TextDecoration.LineThrough else TextDecoration.None' in table, 'wide unavailable Turnip data is not struck through')

remove_dialog = settings[settings.find('title = { QuestionDialogTitle("Delete Turnip driver?") }'):]
need(remove_dialog != settings[-1:], 'Turnip removal confirmation missing')
need('This slot is active. Removing it will deactivate Turnip, switch VulkanScope to the System driver, and delete the private package.' in remove_dialog, 'active Turnip deletion does not disclose System fallback')
need('ExpressiveContainedIconTextButton("Delete", R.drawable.ic_delete, enabled = !turnipManagerBusy, fontWeight = FontWeight.Bold' in remove_dialog, 'Turnip delete confirmation is not bold/contained with trash icon')
need('dismissButton = { ExpressiveCancelButton' in remove_dialog, 'Turnip delete Cancel does not use X action')

question = block('private fun QuestionDialogTitle(', '@Composable\nprivate fun ExpressiveCancelButton')
need('R.drawable.ic_question' in question and 'color = VulkanAccentContainer' in question, 'shared question-title icon treatment missing')
cancel = block('private fun ExpressiveCancelButton(', '@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun ExpressivePrimaryButton')
need('R.drawable.ic_close' in cancel and 'Text("Cancel", fontWeight = FontWeight.Normal' in cancel, 'shared Cancel X/normal-weight treatment missing')
version_match = re.search(r'versionName\s*=\s*"(\d+)\.(\d+)\.(\d+)"', gradle)
current_version = tuple(map(int, version_match.groups())) if version_match else (0, 0, 0)
semantic_update_dialog = current_version >= (1, 0, 13)
watch_confirmation_dialogs = current_version >= (1, 2, 2)
database_failure_dialog = current_version >= (1, 2, 3)
history_clear_all_dialog = current_version >= (1, 2, 4)
expected_alerts = 10 if history_clear_all_dialog else (9 if database_failure_dialog else (8 if watch_confirmation_dialogs else 6))
expected_cancel = 6
expected_close = 3 if history_clear_all_dialog else (2 if watch_confirmation_dialogs else 0)
expected_question_titles = 8 if history_clear_all_dialog else (7 if watch_confirmation_dialogs else (5 if semantic_update_dialog else 6))
need(main.count('AlertDialog(') == expected_alerts, 'unexpected AlertDialog census; audit all questions before release')
need(main.count('dismissButton = { ExpressiveCancelButton') == expected_cancel, 'shared Cancel X dialog census drifted')
need(main.count('dismissButton = { ExpressiveCloseButton') == expected_close, 'shared Close X dialog census drifted')
if database_failure_dialog:
    need(main.count('dismissButton = { ExpressiveContainedIconTextButton("Close", R.drawable.ic_close') == 1, '1.2.3 Database failure dialog contained Close/X action census drifted')
need(len(re.findall(r'title\s*=\s*\{.{0,220}?QuestionDialogTitle\(', main, re.S)) == expected_question_titles, 'question-dialog title icon census drifted')
if semantic_update_dialog:
    need('SemanticDialogTitle("Download VulkanScope ${update.version}?", R.drawable.ic_update_available)' in main, '1.0.13 update dialog does not retain its superseding semantic update icon')
for drawable in ['ic_question.xml', 'ic_open_external.xml', 'ic_evidence.xml', 'ic_delete.xml', 'ic_close.xml']:
    need((root / 'app/src/main/res/drawable' / drawable).is_file(), f'required action drawable missing: {drawable}')

history = block('private fun LazyListScope.analysisWorkspaceItems(', '@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun DetailAffordance')
need('ExpressiveContainedIconTextButton("Use as baseline", R.drawable.ic_baseline, modifier = Modifier.weight(1f))' in history, 'Use as baseline is not an equal-width contained icon action')
need('ExpressiveContainedIconTextButton("Delete", R.drawable.ic_delete, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)' in history, 'history Delete is not equal-width/bold/contained with trash icon')
need('ExpressiveContainedIconTextButton("Evidence actions", R.drawable.ic_evidence)' in history, 'Evidence actions is not contained with evidence icon')
analysis_page = block('private fun AnalysisPage(', '@Composable\nprivate fun VulkanPage')
need('QuestionDialogTitle("Delete analysis history snapshot?")' in analysis_page, 'history delete confirmation question treatment missing')
need('ExpressiveContainedIconTextButton("Yes", R.drawable.ic_delete)' in analysis_page, 'history delete affirmative action is not contained')
need('ExpressiveCancelButton' in analysis_page, 'history delete Cancel X missing')

toggle = block('private fun ExpressiveToggleRow(', '@Composable\nprivate fun ExpressiveMetric')
need('.toggleable(' not in toggle and '.clickable(' not in toggle and 'ExpressiveSwitch(checked = checked, onCheckedChange = onCheckedChange)' in toggle, 'Analysis toggle row still makes text/row clickable')

status_accent = block('private fun evidenceStateAccent(', '@Composable\nprivate fun CapabilityStatusBadge')
for token in ['"SUPPORTED", "PASS" -> ComposeColor(0xFF73C991)', '"AVAILABLE" -> ComposeColor(0xFF7CC4FF)', '"UNSUPPORTED", "FAIL" -> ComposeColor(0xFFFF8A8A)', '"UNAVAILABLE" -> ComposeColor(0xFFFFC857)', '"INCOMPLETE" -> ComposeColor(0xFFD3A4FF)', '"NOT APPLICABLE" -> ComposeColor(0xFFC4C4C4)', '"UNKNOWN", "UNRESOLVED" -> ComposeColor(0xFFA8A8A8)']:
    need(token in status_accent, f'evidence color mapping missing: {token}')
status_badge = block('private fun CapabilityStatusBadge(', '@Composable\nprivate fun ExpressiveActionButton')
need('available -> ComposeColor(0xFF172B3A)' in status_badge and 'available -> ComposeColor(0xFF7CC4FF)' in status_badge, 'Available badge is not blue')
need('unknown -> ComposeColor(0xFF252525)' in status_badge and 'unknown -> ComposeColor(0xFFA8A8A8)' in status_badge, 'Unknown badge is not neutral gray')
need('unavailable -> ComposeColor(0xFF332A16)' in status_badge and 'unavailable -> ComposeColor(0xFFFFC857)' in status_badge, 'Unavailable badge is not amber')
need('notApplicable -> ComposeColor(0xFF272727)' in status_badge and 'notApplicable -> ComposeColor(0xFFC4C4C4)' in status_badge, 'Not applicable badge is not neutral gray')

external = block('private fun ExpressiveExternalLinkRow(', '@Composable\nprivate fun ExpressiveIdentityBlock')
need('Surface(' in external and 'Card(' not in external, 'external-link row must be informational, not whole-card clickable')
need('IconButton(onClick = onOpen, enabled = enabled' in external, 'external link trailing icon is not the browser activation target')
need('R.drawable.ic_open_external' in external, 'external-link row lacks open-external icon')
info = block('private fun InfoPage(', 'private fun detectInstalledAbi')
need(info.count('ExpressiveExternalLinkRow(') == 3, 'Info GitHub/Database external-link rows are incomplete')
need('ExpressiveActionButton("Open GitHub profile"' not in info and 'ExpressiveActionButton("Open GitHub repository"' not in info and 'ExpressiveActionButton("Open VulkanScope Database"' not in info, 'external Info destination remains whole-card clickable')
need(main.count('ExpressiveContainedIconTextButton("Open Khronos specification", R.drawable.ic_open_external') == 2, 'Khronos specification actions are not contained external-link actions')

close_button = block('private fun ExpressiveContainedIconTextButton(', '@Composable\nprivate fun ExpressiveLinearProgressIndicator')
need('Row(verticalAlignment = Alignment.CenterVertically' in close_button and (('Box(Modifier.size(20.dp), contentAlignment = Alignment.Center)' in close_button and 'Modifier.size(17.dp)' in close_button) or ('modifier = modifier.heightIn(min = 48.dp)' in close_button and 'Icon(painterResource(icon)' in close_button and 'Modifier.size(18.dp)' in close_button)), 'contained icon/text alignment contract missing')

analysis_helpers = block('private enum class FallbackAnalysisImportKind', 'private fun validateAnalysisSnapshot')
for token in ['getExternalFilesDirs(Environment.DIRECTORY_DOCUMENTS)', 'getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS)', 'File(context.filesDir, "analysis_exchange")', '.take(64)', 'canonical.parentFile != root', 'canonical.name.startsWith("VulkanScope-")', 'bytes.size > maxBytes', 'target.parentFile != root', 'output.fd.sync()', 'target.length() != bytes.size.toLong()']:
    need(token in analysis_helpers, f'bounded app-specific exchange contract missing: {token}')
for forbidden in ['Environment.getExternalStorageDirectory', 'MANAGE_EXTERNAL_STORAGE', '/sdcard']:
    need(forbidden not in analysis_helpers, f'Analysis fallback broadens storage access: {forbidden}')
model = block('private fun rememberAnalysisWorkspaceModel(', 'private fun historyLabel')
for token in ['tryLaunchSystemDocumentPicker { importLauncher.launch', 'tryLaunchSystemDocumentPicker { exportLauncher.launch', 'tryLaunchSystemDocumentPicker { minimumImportLauncher.launch', 'tryLaunchSystemDocumentPicker { minimumExportLauncher.launch', 'tryLaunchSystemDocumentPicker { rawExportLauncher.launch', 'openFallbackImport(FallbackAnalysisImportKind.SNAPSHOT)', 'openFallbackImport(FallbackAnalysisImportKind.MINIMUM)', 'exportSnapshotFallback()', 'exportMinimumFallback()', 'exportRawFallback()']:
    need(token in model, f'Analysis launch-first/fallback routing missing: {token}')
for text in ['system document picker first', 'fallback selection dialog for bounded VulkanScope-*-analysis.json', 'fallback selection dialog for bounded VulkanScope-*-minimum.json', 'files/analysis_exchange', 'shows the exact saved path in a toast']:
    need(text in main, f'fallback user-visible explanation missing: {text}')
need('If the picker cannot be opened, the report is saved to the public Downloads collection without requesting broad storage access and a toast reports the result.' in main, 'complete-report picker-failure Downloads behavior is not disclosed')

turnip_fallback = block('    private fun fallbackTurnipSearchRoots()', '    private fun openDriverBundlePicker()')
for token in ['File(filesDir, "turnip_imports")', 'getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS)', 'getExternalFilesDirs(Environment.DIRECTORY_DOCUMENTS)', '"turnip_%02d.zip"', 'canonical.parentFile != root']:
    need(token in turnip_fallback, f'retained bounded Turnip fallback missing: {token}')
for forbidden in ['Environment.getExternalStorageDirectory', 'getExternalStoragePublicDirectory', '/sdcard']:
    need(forbidden not in turnip_fallback, f'Turnip fallback broadens storage access: {forbidden}')
need('tryLaunchSystemDocumentPicker { driverPickerLauncher.launch(' in main, 'Turnip no longer attempts the system picker before bounded fallback')

need('## Release 1.0.8 driver-manager, confirmation-action, external-link and no-SAF exchange requirements' in rules, 'PROJECT_RULES 1.0.8 section missing')
need((root / 'rules/1.0.8_DRIVER_MANAGER_DIALOG_STORAGE_UI_AUDIT.md').is_file(), '1.0.8 audit record missing')
need('FontFamily(' not in main and not (root / 'app/src/main/res/font').exists(), 'system-font substitution contract regressed')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 1.0.8 driver-manager/dialog/external-link/no-SAF UI contract')
