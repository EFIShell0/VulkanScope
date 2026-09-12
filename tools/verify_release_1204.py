#!/usr/bin/env python3
import argparse
import re
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root')
parser.add_argument('--skip-version', action='store_true')
args = parser.parse_args()
root = Path(args.root).resolve() if args.root else Path(__file__).resolve().parents[1]
main_path = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
gradle_path = root / 'app/build.gradle.kts'
main = main_path.read_text(encoding='utf-8')
gradle = gradle_path.read_text(encoding='utf-8')
version_match = re.search(r'versionName\s*=\s*"(\d+)\.(\d+)\.(\d+)"', gradle)
current_version = tuple(map(int, version_match.groups())) if version_match else (0, 0, 0)
code_match = re.search(r'versionCode\s*=\s*(\d+)', gradle)
errors = []

def need(condition, message):
    if not condition:
        errors.append(message)

def between(start, end):
    a = main.find(start)
    b = main.find(end, a + 1) if a >= 0 else -1
    return main[a:b] if a >= 0 and b > a else ''

if not args.skip_version:
    expected_code = current_version[0] * 1000 + current_version[1] * 100 + current_version[2]
    need(version_match is not None and code_match is not None and current_version >= (1, 2, 4) and int(code_match.group(1)) == expected_code, 'retained 1.2.4+ release identity missing')
need(re.search(r'\bminSdk\s*=\s*31\b', gradle) is not None, 'Android 12 minimum drifted')

state = between('private class AnalysisWorkspaceState(', 'private data class AnalysisWorkspaceModel(')
need('var pendingHistoryDeleteAll by mutableStateOf(false)' in state, 'history clear-all confirmation state missing')
model = between('private data class AnalysisWorkspaceModel(', '@Composable\nprivate fun rememberAnalysisWorkspaceModel')
need('val deleteAllHistory: () -> Unit' in model, 'history clear-all model action missing')
remember_model = between('private fun rememberAnalysisWorkspaceModel(', 'private fun historyLabel(')
need('deleteAllHistory = { scope.launch {' in remember_model, 'history clear-all action is not coroutine-owned')
need('val ids = state.history.map { it.id }' in remember_model, 'history clear-all does not snapshot the bounded retained ids')
need('ids.forEach { deleteAnalysisHistoryRecord(context, it) }' in remember_model, 'history clear-all does not use the existing validated per-record delete path')
need('state.history = withContext(Dispatchers.IO) { loadAnalysisHistoryRecords(context) }' in remember_model, 'history clear-all does not reload authoritative local history')

analysis_page = between('private fun AnalysisPage(', '@Composable\nprivate fun VulkanPage')
need('QuestionDialogTitle("Delete all analysis history?")' in analysis_page, 'history clear-all question dialog missing')
need('ExpressiveContainedIconTextButton("Delete all", R.drawable.ic_clear_all, fontWeight = FontWeight.Bold)' in analysis_page, 'history clear-all destructive action does not match watched clear-all hierarchy')
need('ExpressiveCloseButton { analysisModel.state.pendingHistoryDeleteAll = false }' in analysis_page, 'history clear-all Close X action missing')
need('analysisModel.deleteAllHistory()' in analysis_page, 'history clear-all dialog does not invoke the bounded deletion action')

history_ui = between('10 -> {', '11 -> {')
need('ExpressiveContainedIconTextButton("Clear all", R.drawable.ic_clear_all' in history_ui, 'History Clear all contained action missing')
need('enabled = state.history.isNotEmpty()' in history_ui, 'History Clear all is not disabled when history is empty')
need('state.pendingHistoryDeleteAll = true' in history_ui, 'History Clear all does not open confirmation')

settings_cards = between('private fun SettingsSectionCards(', '@Composable\nprivate fun DatabaseSubmissionFailureDialog')
need('var cardsVisible by remember { mutableStateOf(false) }' in settings_cards, 'Settings destination entrance state missing')
need('LaunchedEffect(Unit) { cardsVisible = true }' in settings_cards, 'Settings destinations do not start their bounded entrance transition')
need('SettingsSection.entries.forEachIndexed { index, section ->' in settings_cards, 'Settings destination order is no longer used for staggered animation')
need('AnimatedVisibility(' in settings_cards and 'visible = cardsVisible' in settings_cards, 'Settings destination cards are not animated into view')
need('fadeIn(tween(durationMillis = 260, delayMillis = index * 45))' in settings_cards, 'Settings destination fade is not bounded/staggered')
need('slideInHorizontally(tween(durationMillis = 260, delayMillis = index * 45)) { it / 10 }' in settings_cards, 'Settings destination slide is not bounded/staggered')
need('ExpressiveDestinationCard(section.label, section.description, section.icon)' in settings_cards, 'Settings animation stopped using the shared expressive destination card')

share_lines = re.findall(r'ExpressiveActionButton\("Share[^\n]+', main)
need(len(share_lines) == 2, f'unexpected Share action count: {len(share_lines)}')
for line in share_lines:
    need('R.drawable.ic_share' in line, 'Share leading artwork changed')
    need('trailingIcon = R.drawable.ic_open_external' in line, 'Share trailing action does not match Open Database external-open artwork')
need('trailingIcon = R.drawable.ic_link' not in main, 'legacy chain-link trailing artwork remains on a Share action')
external_row = between('private fun ExpressiveExternalLinkRow(', '@Composable\nprivate fun ExpressiveIdentityBlock')
need('R.drawable.ic_open_external' in external_row, 'Open Database external-open reference artwork drifted')

need('OFFICIAL_DATABASE_API_ENDPOINT = "https://vulkanscope-database-api.vulkanscope.workers.dev"' in main, 'Database endpoint drifted')
need('delay(3000)' in main, 'existing bounded transient action timing drifted')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 1.2.4 History/Settings-motion/Share-action contract')
