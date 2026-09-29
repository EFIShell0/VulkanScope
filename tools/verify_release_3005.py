#!/usr/bin/env python3
import argparse
import hashlib
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', type=Path, default=Path(__file__).resolve().parents[1])
args = parser.parse_args()
root = args.root.resolve()
errors = []

def need(condition, message):
    if not condition:
        errors.append(message)

def read(rel):
    path = root / rel
    need(path.is_file(), f'missing file: {rel}')
    return path.read_text(encoding='utf-8') if path.is_file() else ''

def block(text, start, end):
    a = text.find(start)
    b = text.find(end, a + len(start)) if a >= 0 else -1
    need(a >= 0 and b > a, f'missing block: {start}')
    return text[a:b] if a >= 0 and b > a else ''

main = read('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
advanced = read('app/src/main/java/com/efishell/vulkanscope/AdvancedAnalysis.kt')
graph = read('app/src/main/java/com/efishell/vulkanscope/VulkanDependencyGraph.kt')
gradle = read('app/build.gradle.kts')
rules = read('rules/PROJECT_RULES.md')
audit = read('rules/3.0.5_FULLSCREEN_FILE_MANAGER_DIAGNOSTICS_DATABASE_AUDIT.md')
changelog = read('changelog.md')

declared_files = [line.strip() for line in read('files.txt').splitlines() if line.strip()]
actual_files = sorted(path.relative_to(root).as_posix() for path in root.rglob('*') if path.is_file())
need(declared_files == actual_files, 'files.txt strict census mismatch')

need('versionCode = 3005' in gradle and 'versionName = "3.0.5"' in gradle, '3.0.5 release identity missing')
need(changelog.startswith('## 3.0.5\n'), '3.0.5 changelog entry missing or not first')
need('## Release 3.0.5 full-screen file-manager, diagnostic timing and Database lookup requirements' in rules, '3.0.5 project rules missing')
need('# VulkanScope 3.0.5 Full-Screen File Manager, Diagnostics and Database Audit' in audit, '3.0.5 audit heading missing')

turnip = block(main, 'private fun TurnipFileManagerDialog(', '@Composable\nprivate fun TurnipFileManagerSelectionSummary')
need('Box(Modifier.fillMaxSize().zIndex(40f))' in turnip, 'Turnip manager is not full-screen')
need('shape = RoundedCornerShape(0.dp)' in turnip and 'color = VulkanBlack' in turnip, 'Turnip manager is not rectangular AMOLED black')
need('shadowElevation = 0.dp' in turnip and 'tonalElevation = 0.dp' in turnip, 'Turnip outer surface still has window elevation')
need('.padding(start = navigationStartInset, end = navigationEndInset, bottom = navigationBottomInset)' in turnip, 'Turnip manager system-navigation insets regressed')
need('FileManagerOptionsChooser(state.viewMode, state.sortMode' in turnip, 'Turnip unified View & sort control missing')
need('contentDescription = "Search files and folders"' in turnip, 'Turnip search action missing')
need('modifier = Modifier.weight(1f).focusRequester(searchFocusRequester)' in turnip, 'Turnip expanded search does not consume remaining toolbar width')
need(turnip.find('FileManagerOptionsChooser(state.viewMode') < turnip.find('contentDescription = "Search files and folders"'), 'Turnip search is not immediately after View & sort')

need('AnimatedVisibility(\n                visible = turnipFileManagerState.visible' in main, 'Turnip manager open/close animation missing')
need('fadeIn(tween(180)) + slideInVertically' in main and 'fadeOut(tween(160)) + slideOutVertically' in main, 'file-manager fade/slide motion missing')
close_turnip = block(main, '    private fun closeTurnipFileManager() {', '    private fun importSelectedTurnipFiles()')
need('turnipFileManagerState.copy(visible = false' in close_turnip and 'delay(220L)' in close_turnip, 'Turnip close does not retain state through exit animation')

shared = block(main, 'private fun SharedStorageBrowserDialog(', '@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun SharedStorageBrowserListing(')
need('Box(Modifier.fillMaxSize().zIndex(50f))' in shared, 'shared-storage manager is not full-screen')
need('shape = RoundedCornerShape(0.dp)' in shared and 'color = VulkanBlack' in shared, 'shared-storage manager is not rectangular AMOLED black')
need('AnimatedVisibility(' in shared and 'screenVisible' in shared and 'delay(220L)' in shared, 'shared-storage close/open animation contract missing')
need('FileManagerOptionsChooser(' in shared and 'contentDescription = "Search files and folders"' in shared, 'shared-storage View & sort/search toolbar missing')
need(shared.find('FileManagerOptionsChooser(') < shared.find('contentDescription = "Search files and folders"'), 'shared-storage search is not to the right of View & sort')
need('modifier = Modifier.weight(1f).focusRequester(searchFocusRequester)' in shared, 'shared-storage expanded search does not consume remaining toolbar width')
need('FileManagerBreadcrumbBar(' in shared, 'shared-storage clickable breadcrumb missing')
need('.padding(start = navigationStartInset, end = navigationEndInset, bottom = navigationBottomInset)' in shared, 'shared-storage system-navigation insets regressed')
need(main.count('SharedStorageBrowserDialog(') == 2, 'shared-storage browser is rendered outside the single full-screen overlay host')
need('LocalSharedStorageBrowserLauncher provides { request -> sharedStorageBrowserOverlay = request }' in main, 'shared-storage full-screen overlay host missing')
need('LaunchedEffect(storageAction)' in main and 'SharedStorageBrowserOverlayRequest(' in main, 'Analysis import/export is not routed through shared browser host')
need('LaunchedEffect(pendingStorageSnapshot?.path)' in main, 'Reports TXT/HTML export is not routed through shared browser host')

for fn in ['TurnipFileManagerFolderRow', 'TurnipFileManagerCandidateRow', 'TurnipFileManagerFolderGridCard', 'TurnipFileManagerCandidateGridCard', 'SharedStorageFolderRow', 'SharedStorageFileRow', 'SharedStorageFolderGridCard', 'SharedStorageFileGridCard']:
    start = f'private fun {fn}('
    a = main.find(start)
    need(a >= 0, f'{fn} missing')
    if a >= 0:
        b = main.find('\n@Composable', a + len(start))
        section = main[a:(b if b > a else min(len(main), a + 8000))]
        need('VulkanAccentSoft.copy(alpha =' in section, f'{fn} lacks Vulkan-red card outline')

need('internal fun formatAnalysisElapsedTime(elapsedMs: Long): String' in advanced, 'central diagnostic duration formatter missing')
need('"%.3f s (%d ms)"' in advanced, 'diagnostic timing is not seconds with milliseconds in parentheses')
need('formatAnalysisElapsedTime(elapsed)' in advanced, 'timeline rows do not use duration formatter')
need('CapabilitySectionCard("Diagnostic collection")' in main, 'Diagnostic collection redesign missing')
need('Text("Complete collection"' in main and 'Probe and scheduler timing' in main, 'diagnostic timing hierarchy missing')
need(main.count('formatAnalysisElapsedTime(') >= 5, 'diagnostic timing UI does not consistently use seconds + milliseconds')

need('var databaseLookupMode by mutableIntStateOf(0)' in main, 'Database lookup mode state missing')
need('listOf("Database list", "Report ID")' in main, 'Database list/Report ID mode selector missing')
need('private suspend fun fetchDatabaseReportPage(' in main, 'bounded Database list fetch missing')
db = block(main, 'private suspend fun fetchDatabaseReportPage(', 'private suspend fun fetchDatabaseTechnicalReport(')
need('addPathSegments("v1/reports")' in db, 'Database list path is not /v1/reports')
need('addQueryParameter("limit", "50")' in db, 'Database list request limit is not 50')
need('beforeSubmittedAt' in db and 'beforeId' in db, 'Database list cursor parameters missing')
need('readResponseTextLimited(response.body, ANALYSIS_DATABASE_COMPARE_MAX_BYTES)' in db, 'Database list response is not bounded')
need('Regex("[a-f0-9]{64}")' in db, 'Database list report IDs are not validated')
need('take(200)' in main, 'Database in-app report list is not bounded to 200 unique rows')
need('fetchDatabaseReport: (String) -> Unit' in main and 'fetchDatabaseReportList: (Boolean) -> Unit' in main, 'Database mode actions missing from Analysis model')
need('64-character report id' in main and 'Fetch by report ID' in main, 'exact Report ID workflow missing')

options = block(main, 'private fun FileManagerOptionsChooser(', 'private fun fileManagerViewModeIcon(')
need('onDismissRequest = { }' in options, 'View & sort can dismiss without X')
need('contentDescription = "Close view and sort menu"' in options and 'onClick = { expanded = false }' in options, 'View & sort X-only close action missing')

need('event.nativeKeyCode' not in main, '3.0.4 TV nativeKeyCode receiver regression returned')
need(main.count('event.key.nativeKeyCode') == 7, '3.0.4 TV key receiver count changed unexpectedly')
need('import androidx.compose.foundation.layout.weight' not in graph and graph.count('Modifier.weight(1f)') == 3, '3.0.4 graph weight repair regressed')
for token in [
    'MinimumProfileConfirmation(MinimumProfileConfirmationKind.SAVE',
    'MinimumProfileConfirmation(MinimumProfileConfirmationKind.LOAD',
    'MinimumProfileConfirmation(MinimumProfileConfirmationKind.DELETE',
    'private fun Modifier.tvRemoteLazyListNavigation(state: LazyListState): Modifier',
    'private fun Modifier.tvRemoteLazyGridNavigation(state: LazyGridState): Modifier',
    'delay(3000L)',
]:
    need(token in main, f'predecessor behavior anchor missing: {token}')
need('.verticalScroll(verticalState)' in graph and '.horizontalScroll(horizontalState)' in graph, 'dependency graph two-axis scrolling regressed')

need('// ' not in main and '/*' not in main, 'source-code comment rule violated in MainActivity.kt')
need('// ' not in advanced and '/*' not in advanced, 'source-code comment rule violated in AdvancedAnalysis.kt')

protected = {
    'app/src/main/AndroidManifest.xml': 'f89dab96d5c123cecb227d6a44f4deef5b8b92d2688e46fb55df9bd1c54f18a8',
    'app/src/main/cpp/vulkanscope.cpp': 'fa2d5aea65b493138aa7c0cdbfa25bd715fa6e2265daa4c5c6b70b777e62f3ff',
    'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt': '6da56de2847870fbe2ad1777c951f405fafb3859b2552e3d60857686bc477544',
    'app/src/main/java/com/efishell/vulkanscope/VulkanDependencyGraph.kt': '6ca8bab0c89a28b322ccb449f8e2314f13597488d69fec6c1a263340c030d71d',
    'registry/registry_lock.json': '79c46ebb498997b31c56b3171296ff672d229c8434a31ac77c8a17f86e4f9d0a',
    'registry/generated/registry_query_manifest.json': '60136afc325fef0dfd1ea6bfaf57da6ae61096b96a241dd126ad0fdecc3f6d00',
}
for rel, expected in protected.items():
    path = root / rel
    need(path.is_file(), f'missing protected file: {rel}')
    if path.is_file():
        need(hashlib.sha256(path.read_bytes()).hexdigest() == expected, f'protected file drift: {rel}')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 3.0.5 full-screen file-manager/diagnostics/Database verifier')
