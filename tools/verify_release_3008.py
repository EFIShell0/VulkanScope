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

def sha(rel):
    path = root / rel
    return hashlib.sha256(path.read_bytes()).hexdigest() if path.is_file() else ''

main = read('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
gradle = read('app/build.gradle.kts')
rules = read('rules/PROJECT_RULES.md')
audit = read('rules/3.0.8_DPAD_ANALYSIS_METRICS_FILE_MANAGER_AUDIT.md')
changelog = read('changelog.md')

declared_files = [line.strip() for line in read('files.txt').splitlines() if line.strip()]
actual_files = sorted(path.relative_to(root).as_posix() for path in root.rglob('*') if path.is_file())
need(declared_files == actual_files, 'files.txt strict census mismatch')

need('versionCode = 3008' in gradle and 'versionName = "3.0.8"' in gradle, '3.0.8 release identity missing')
need(changelog.startswith('## 3.0.8\n'), '3.0.8 changelog entry missing or not first')
need('## Release 3.0.8 hardware D-pad navigation, analysis clarity, metric presentation and file-manager motion requirements' in rules, '3.0.8 project rules missing')
need('# VulkanScope 3.0.8 D-pad, Analysis, Metrics and File Manager Audit' in audit, '3.0.8 audit heading missing')

list_nav = block(main, 'private fun Modifier.tvRemoteLazyListNavigation(', '@Composable\nprivate fun Modifier.tvRemoteLazyGridNavigation(')
grid_nav = block(main, 'private fun Modifier.tvRemoteLazyGridNavigation(', '@Composable\nprivate fun Modifier.dpadScrollableNavigation(')
scroll_nav = block(main, 'private fun Modifier.dpadScrollableNavigation(', '@Composable\nprivate fun VulkanLazyPage(')
lazy_page = block(main, 'private fun VulkanLazyPage(', '@Composable\nprivate fun OverviewPage(')
tv_browse = block(main, 'private fun tvBrowseModifier(', '@Composable\nprivate fun ExpressiveIconButton(')
for label, section in [('lazy-list', list_nav), ('lazy-grid', grid_nav), ('primary page', lazy_page), ('focus-card', tv_browse)]:
    need('UI_MODE_TYPE_TELEVISION' not in section and 'isTelevision' not in section, f'{label} D-pad path is still gated to television uiMode')
need('event.key.nativeKeyCode' in list_nav and 'KEYCODE_DPAD_DOWN' in list_nav and 'KEYCODE_DPAD_UP' in list_nav, 'lazy-list hardware D-pad handler missing')
need('event.key.nativeKeyCode' in grid_nav and 'KEYCODE_DPAD_DOWN' in grid_nav and 'KEYCODE_DPAD_UP' in grid_nav, 'lazy-grid hardware D-pad handler missing')
need('focusManager.moveFocus(direction)' in list_nav and 'animateScrollToItem(target)' in list_nav, 'lazy-list focus-first/scroll-fallback contract missing')
need('focusManager.moveFocus(direction)' in grid_nav and 'animateScrollToItem(target)' in grid_nav, 'lazy-grid focus-first/scroll-fallback contract missing')
need('KEYCODE_PAGE_DOWN' in list_nav and 'KEYCODE_PAGE_UP' in list_nav and 'KEYCODE_PAGE_DOWN' in scroll_nav and 'KEYCODE_PAGE_UP' in scroll_nav, 'page-key navigation coverage missing')
need('pageFocusRequester.requestFocus()' in lazy_page and '.focusRequester(pageFocusRequester)' in lazy_page and '.focusable()' in lazy_page, 'primary lazy page does not acquire a hardware-navigation focus root')
need('.dpadScrollableNavigation(scrollState)' in main and main.count('.tvRemoteLazyListNavigation(') >= 6 and main.count('.tvRemoteLazyGridNavigation(') >= 2, 'D-pad navigation is not applied broadly to scroll/lazy surfaces')

icon_map = block(main, 'private fun capabilitySectionIcon(', '@Composable\nprivate fun preferExpandedTextLayout(')
for token, icon in [
    ('Collection integrity score', 'R.drawable.ic_shield'),
    ('Graph overview', 'R.drawable.ic_graph'),
    ('Interactive dependency map', 'R.drawable.ic_graph'),
    ('Vulkan active self-tests', 'R.drawable.ic_self_test'),
    ('Test result summary', 'R.drawable.ic_self_test'),
    ('Capability requirement resolver', 'R.drawable.ic_registry'),
    ('Vulkan Profiles and custom minimums', 'R.drawable.ic_profile'),
    ('Encyclopedia', 'R.drawable.ic_book'),
]:
    need(token in icon_map and icon in icon_map, f'semantic section icon mapping missing: {token}')

metric = block(main, 'private fun ExpressiveMetric(', '@Composable\nprivate fun ExpressiveMetricGrid(')
metric_grid = block(main, 'private fun ExpressiveMetricGrid(', '@Composable\nprivate fun ExpressiveStatus(')
metric_card = block(main, 'private fun MetricCard(', 'private fun formatBytes(')
for label, section in [('common metric', metric), ('overview metric', metric_card)]:
    need('VulkanAccentContainer' in section and 'VulkanAccentSoft.copy(alpha = 0.34f)' in section, f'{label} does not use the common Vulkan-accent summary language')
need('maxWidth < 300.dp' in metric_grid and 'maxWidth < 760.dp -> 2' in metric_grid and 'fontScale >= 1.55f' in metric_grid, 'responsive compact metric-grid policy missing')

workspace = block(main, 'private fun LazyListScope.analysisWorkspaceItems(', '@Composable\nprivate fun ChevronAffordance(')
requirements = block(workspace, '        4 -> {', '        5 -> {')
minimums = block(workspace, '        5 -> {', '        6 -> {')
graph = block(workspace, '        6 -> {', '        7 -> {')
database = block(workspace, '        9 -> {', '        10 -> {')
quality = block(workspace, '        12 -> {', '        13 -> {')
tests_start = workspace.rfind('        else -> {')
need(tests_start >= 0, 'missing self-test branch')
tests = workspace[tests_start:] if tests_start >= 0 else ''

for token in ['"SATISFIED" to satisfiedCount.toString()', '"NOT SATISFIED" to notSatisfiedCount.toString()', '"UNKNOWN" to unknownCount.toString()', '"CHECKED" to model.requirementEvaluations.size.toString()', 'Registry relationship', 'Runtime evidence', 'never collapsed']:
    need(token in requirements, f'requirement redesign anchor missing: {token}')
for token in ['"PASS" to profilePassCount.toString()', '"FAIL" to profileFailCount.toString()', '"UNKNOWN" to profileUnknownCount.toString()', '"MAPPED" to result.checkedRequirementCount.toString()', 'Custom minimum builder', 'Profile identity', 'Rule contract', 'Saved minimum profiles', 'Current custom evaluation']:
    need(token in minimums, f'minimum/profile redesign anchor missing: {token}')
need('MinimumProfileConfirmationKind.SAVE' in minimums and 'MinimumProfileConfirmationKind.LOAD' in minimums and 'MinimumProfileConfirmationKind.DELETE' in minimums, 'minimum Save/Load/Delete confirmation semantics regressed')

need('SystemDriverVendorBadge(reportRow.vendorId)' in database, 'Database report-list does not reuse the Turnip/System red vendor badge')
need('vendorIdFromDisplay(reportRow.vendorId)' not in database, 'Database report-list still uses the predecessor free badge path')
need('listOf("Database list", "Report ID")' in database, '3.0.5 Database two-mode lookup regressed')

encyclopedia = block(main, 'private fun encyclopediaEntryIcon(', '@Composable\nprivate fun AnalysisPage(')
for token in ['COMMANDS', 'VK_* TOKENS', 'Vk* TYPES', 'EXTENSIONS', 'Evidence boundary', 'Reference search', 'How to read encyclopedia entries', 'MATCHES', 'PAGE', 'encyclopediaEntryIcon(entry.category)']:
    need(token in encyclopedia, f'Encyclopedia redesign anchor missing: {token}')
need('browsing this page never performs a network request' in encyclopedia, 'Encyclopedia offline boundary missing')

turnip_manager = block(main, 'private fun TurnipFileManagerDialog(', '@Composable\nprivate fun TurnipFileManagerSelectionSummary')
selection_summary = block(main, 'private fun TurnipFileManagerSelectionSummary(', '@Composable\nprivate fun FileManagerOptionsChooser(')
shared_listing = block(main, 'private fun SharedStorageBrowserListing(', '@Composable\nprivate fun SharedStorageFolderRow(')
breadcrumb = block(main, 'private fun FileManagerBreadcrumbBar(', '@Composable\nprivate fun ExpandableFileManagerSearch(')
need('targetState = state.directoryPath' in turnip_manager and 'turnipDirectoryTransition' in turnip_manager, 'Turnip directory enter/exit animation missing')
need('targetState = navigationKey' in shared_listing and 'sharedStorageDirectoryTransition' in shared_listing, 'shared-storage directory enter/exit animation missing')
need('animateColorAsState' in breadcrumb and 'animateContentSize' in breadcrumb and 'onNavigate(crumb.path)' in breadcrumb, 'breadcrumb state/navigation animation missing')
need('targetState = selectedCount' in selection_summary and 'targetState = remainingCount' in selection_summary and 'turnipSelectedCount' in selection_summary and 'turnipRemainingCount' in selection_summary, 'Turnip selected/remaining count animation missing')

for token in ['Collection integrity score', 'Starting score', 'Scoring method', 'Observed evidence', 'What this score does not mean']:
    need(token in quality, f'3.0.7 Quality transparency regressed: {token}')
for token in ['Vulkan active self-tests', 'Test result summary', '"PASS" to passCount.toString()', '"FAIL" to failCount.toString()', '"UNAVAILABLE" to unavailableCount.toString()', 'Result semantics']:
    need(token in tests, f'3.0.7 self-test detail regressed: {token}')
need('LocalValidatedNetwork.current' not in workspace, '3.0.6 non-composable CompositionLocal regression reintroduced')
need('event.nativeKeyCode' not in main, '3.0.4 nativeKeyCode receiver regression reintroduced')
need('color = VulkanBlack' in turnip_manager, 'full-screen AMOLED Turnip file manager regressed')
need('onDismissRequest = { }' in main, 'X-only View & sort dismissal regressed')
need('// ' not in main and '/*' not in main, 'source-code comment rule violated in MainActivity.kt')

protected = {
    'app/src/main/AndroidManifest.xml': 'f89dab96d5c123cecb227d6a44f4deef5b8b92d2688e46fb55df9bd1c54f18a8',
    'app/src/main/cpp/vulkanscope.cpp': 'fa2d5aea65b493138aa7c0cdbfa25bd715fa6e2265daa4c5c6b70b777e62f3ff',
    'app/src/main/java/com/efishell/vulkanscope/AdvancedAnalysis.kt': '4bb7d65a1206873f0629792d3012139dcf3247157622e8200e40595104e62904',
    'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt': '6da56de2847870fbe2ad1777c951f405fafb3859b2552e3d60857686bc477544',
    'app/src/main/java/com/efishell/vulkanscope/VulkanDependencyGraph.kt': '6ca8bab0c89a28b322ccb449f8e2314f13597488d69fec6c1a263340c030d71d',
    'registry/registry_lock.json': '79c46ebb498997b31c56b3171296ff672d229c8434a31ac77c8a17f86e4f9d0a',
    'registry/generated/registry_query_manifest.json': '60136afc325fef0dfd1ea6bfaf57da6ae61096b96a241dd126ad0fdecc3f6d00',
}
for rel, expected in protected.items():
    need(sha(rel) == expected, f'protected predecessor-equivalent file drift: {rel}')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 3.0.8 D-pad/analysis/metrics/file-manager verifier')
