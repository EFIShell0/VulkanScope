#!/usr/bin/env python3
import argparse
import re
import xml.etree.ElementTree as ET
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', type=Path, default=Path(__file__).resolve().parents[1])
parser.add_argument('--skip-version', action='store_true')
parser.add_argument('--skip-release-records', action='store_true')
args = parser.parse_args()
root = args.root.resolve()
errors = []
main_path = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
gradle_path = root / 'app/build.gradle.kts'
rules_path = root / 'rules/PROJECT_RULES.md'
audit_path = root / 'rules/1.0.11_ICON_ACTIVE_STATE_UI_AUDIT.md'
manifest_path = root / 'app/src/main/AndroidManifest.xml'

for path in [main_path, gradle_path, manifest_path]:
    if not path.is_file():
        errors.append(f'missing required file: {path.relative_to(root)}')
if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)

main = main_path.read_text(encoding='utf-8')
gradle = gradle_path.read_text(encoding='utf-8')
manifest = manifest_path.read_text(encoding='utf-8')

def need(condition, message):
    if not condition:
        errors.append(message)

def block(start, end):
    a = main.find(start)
    b = main.find(end, a + len(start)) if a >= 0 else -1
    need(a >= 0 and b > a, f'block missing: {start}')
    return main[a:b] if a >= 0 and b > a else ''

version_match = re.search(r'versionName\s*=\s*"(\d+)\.(\d+)\.(\d+)"', gradle)
current_version = tuple(map(int, version_match.groups())) if version_match else (0, 0, 0)
release_minor = current_version[2] if current_version[:2] == (1, 0) else (99 if current_version >= (1, 1, 0) else 0)
if not args.skip_version:
    code_match = re.search(r'versionCode\s*=\s*(\d+)', gradle)
    expected = current_version[0] * 1000 + current_version[1] * 100 + current_version[2]
    need(version_match is not None and code_match is not None and current_version >= (1, 0, 11) and int(code_match.group(1)) == expected, 'retained 1.0.11+ semantic release identity missing')
need('kBaseline = "Vulkan 1.4.362"' in (root / 'app/src/main/cpp/registry_query_catalog.h').read_text(encoding='utf-8'), 'Vulkan baseline drifted')
need('android.permission.MANAGE_EXTERNAL_STORAGE' not in manifest, 'all-files storage permission is forbidden')

analysis = block('private fun LazyListScope.analysisWorkspaceItems(', '@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun DetailAffordance')
need('ExpressiveContainedIconTextButton("Use as baseline", R.drawable.ic_baseline, modifier = Modifier.weight(1f))' in analysis, 'Use as baseline is not an equal-width contained icon action')
need('ExpressiveContainedIconTextButton("Delete", R.drawable.ic_delete, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)' in analysis, 'Delete is not paired with the same equal-width action geometry')

contained = block('private fun ExpressiveContainedIconTextButton(', '@Composable\nprivate fun ExpressiveLinearProgressIndicator')
need('modifier: Modifier = Modifier' in contained, 'contained icon/text action lacks reusable modifier parameter')
need('modifier = modifier.heightIn(min = 48.dp)' in contained, 'contained icon/text action lost 48 dp minimum geometry')
need('contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)' in contained, 'contained icon/text action padding drifted')

manager_reader = block('private fun readManagedTurnipDrivers(', 'private fun migrateLegacyTurnipBundleIfNeeded')
need('activeMode: DriverMode' in manager_reader, 'managed Turnip reader lacks actual driver-mode state')
need('activeMode == DriverMode.TURNIP && slot == activeSlot' in manager_reader, 'Turnip selected state is not gated by active Turnip mode')

settings = block('private fun SettingsPage(', '@Composable\nprivate fun UnavailableTurnipKeyValue')
need('LaunchedEffect(turnipManagerRevision, turnipSupport, turnipManagerBusy, mode)' in settings, 'driver manager does not refresh when active driver mode changes')
need('readManagedTurnipDrivers(context, context.getSharedPreferences("settings", Context.MODE_PRIVATE), mode)' in settings, 'driver manager read is not bound to current mode')
need('active = mode == DriverMode.SYSTEM' in settings, 'System active state is not bound to System mode')
need('activeMode = mode' in settings, 'Turnip manager presentation is not bound to current mode')

table = block('private fun TurnipDriverManagerTable(', 'private fun turnipDriverStateLabel')
need('activeMode: DriverMode' in table, 'Turnip table lacks active-mode input')
need('val driver = if (activeMode == DriverMode.TURNIP) rawDriver else rawDriver.copy(selected = false)' in table, 'Turnip presentation can retain stale selected state while System is active')

page_icons = block('private fun pageIcon(page: Page): Int = when (page) {', 'private fun pageTransitionIndex')
for token in [
    'Page.Profiles -> R.drawable.ic_profile',
    'Page.Encyclopedia -> R.drawable.ic_book',
    'Page.Analysis -> R.drawable.ic_analysis'
]:
    need(token in page_icons, f'page-specific icon mapping missing: {token}')

overview = block('private fun OverviewDestinationCard(', '@Composable\nprivate fun QuickAccessCard')
if current_version >= (1, 2, 2):
    overview_ui = block('private fun ExpressiveDestinationCard(', '@Composable\nprivate fun OverviewDestinationCard')
    need('ExpressiveDestinationCard(title, subtitle, pageIcon(destination)) { navigate(destination) }' in overview, 'Overview destination shared-card delegation drifted')
    need('containerColor = ComposeColor(0xFF291719), contentColor = VulkanAccentSoft' in overview_ui, 'Overview destination chevron container does not match shared red action affordance')
    need('Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = "Open $title", tint = VulkanAccentSoft, modifier = Modifier.size(20.dp))' in overview_ui, 'Overview destination chevron is not Vulkan accent red')
    need('onClick = onClick' in overview_ui, 'Overview destination chevron action drifted')
else:
    need('containerColor = ComposeColor(0xFF291719), contentColor = VulkanAccentSoft' in overview, 'Overview destination chevron container does not match shared red action affordance')
    need('Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = "Open $title", tint = VulkanAccentSoft, modifier = Modifier.size(20.dp))' in overview, 'Overview destination chevron is not Vulkan accent red')
    need('onClick = { navigate(destination) }' in overview, 'Overview destination chevron action drifted')

section_icons = block('private fun capabilitySectionIcon(title: String): Int = when {', '@Composable\nprivate fun preferExpandedTextLayout')
section_expectations = [
    'title.equals("Developer", true) -> R.drawable.ic_code' if release_minor >= 16 else 'title.equals("Developer", true) -> R.drawable.ic_person',
    'title.equals("Application", true) -> R.drawable.vulkanscope_scope_wordmark' if release_minor >= 16 else 'title.equals("Application", true) -> R.drawable.ic_app',
    'title.equals("Libraries", true) -> R.drawable.ic_library',
    'title.equals("Build toolchain", true) -> R.drawable.ic_build',
    'title.equals("Device ABI", true) -> R.drawable.ic_cpu',
    'title.equals("Android", true) || title.equals("Android runtime", true) || title.equals("Operating system", true) -> R.drawable.ic_android',
    ('title.equals("Updates", true) || title.equals("Update preferences", true) -> R.drawable.ic_download' if current_version >= (1, 2, 0) else ('title.equals("Updates", true) -> R.drawable.ic_zip_download' if release_minor >= 17 else 'title.equals("Updates", true) -> R.drawable.ic_download_update')) ,
    'title.equals("Encyclopedia", true) -> R.drawable.ic_book',
    'title.equals("Analysis workspace", true) -> R.drawable.ic_analysis',
    'title.equals("Local session history", true) -> R.drawable.ic_history',
    'title.equals("Watched evidence", true) -> R.drawable.ic_watch_add',
    'title.contains("Database", true) -> R.drawable.ic_action_database',
    'title.equals("Extension explorer", true) -> R.drawable.ic_extensions'
]
for token in section_expectations:
    need(token in section_icons, f'section icon audit mapping missing: {token}')

info = block('private fun InfoPage(', '@Composable\nprivate fun SettingsPage')
developer_identity_icon = 'R.drawable.ic_person' if release_minor >= 17 else ('R.drawable.ic_code' if release_minor >= 16 else 'R.drawable.ic_person')
need(f'ExpressiveIdentityBlock("Semih Boran", "EFI Shell · VulkanScope developer", {developer_identity_icon})' in info, 'developer identity does not use the release semantic glyph')
if current_version >= (1, 2, 2):
    need('R.drawable.ic_download, enabled = directUpdatesEnabled && networkAvailable && !updateCheckInFlight, trailingIcon = R.drawable.ic_receive, onClick = onCheckForUpdates' in info, 'Check for updates does not use the current download/receive in-flight contract')
elif current_version >= (1, 2, 0):
    need('R.drawable.ic_receive, enabled = directUpdatesEnabled && networkAvailable && !updateCheckInFlight, trailingIcon = R.drawable.ic_receive, onClick = onCheckForUpdates' in info, 'Check for updates does not use the current receive glyph/in-flight gate')
else:
    check_updates_icon = 'R.drawable.ic_zip_download' if release_minor >= 17 else ('R.drawable.ic_check_updates' if release_minor >= 16 else 'R.drawable.ic_download_update')
    need(f'{check_updates_icon}, enabled = directUpdatesEnabled && networkAvailable, onClick = onCheckForUpdates' in info, 'Check for updates does not use the release update glyph')

version_block = block('private fun ExpressiveVersionBlock(', '@Composable\nprivate fun ExpressiveInfoPill')
need(('painter = painterResource(R.drawable.vulkanscope_logo_foreground)' if release_minor >= 16 else 'painter = painterResource(R.drawable.ic_app)') in version_block, 'application identity block lost its release identity artwork')

action_expectations = [
    ('ExpressiveActionButton("Import analysis snapshot"', 'R.drawable.ic_action_import'),
    ('ExpressiveActionButton("Run guided System ↔ Turnip A/B"', 'R.drawable.ic_compare'),
    ('ExpressiveActionButton("Save local profile"', 'R.drawable.ic_save'),
    ('ExpressiveActionButton("Share link"', 'R.drawable.ic_share' if release_minor >= 15 else 'R.drawable.ic_link'),
    ('ExpressiveActionButton("Run Vulkan self-tests"', 'R.drawable.ic_self_test' if release_minor >= 16 else 'R.drawable.ic_test'),
    (('TransientActionButton("Copy name + value"' if current_version >= (1, 2, 0) else 'ExpressiveActionButton("Copy name + value"'), 'R.drawable.ic_copy'),
    ('ExpressiveActionButton("Share evidence"', 'R.drawable.ic_share'),
    (('TransientActionButton("Add to watched evidence"' if current_version >= (1, 2, 0) else 'ExpressiveActionButton("Add to watched evidence"'), 'R.drawable.ic_watch_add'),
    ('ExpressiveActionButton("Open in Encyclopedia"', 'R.drawable.ic_book')
]
for prefix, icon in action_expectations:
    line = next((line for line in main.splitlines() if prefix in line), '')
    need(bool(line) and icon in line, f'action icon audit mapping missing: {prefix} -> {icon}')

required_drawables = [
    'ic_person.xml', 'ic_android.xml', 'ic_book.xml', 'ic_analysis.xml', 'ic_app.xml', 'ic_library.xml',
    'ic_build.xml', 'ic_cpu.xml', 'ic_registry.xml', 'ic_export.xml', 'ic_history.xml', 'ic_compare.xml',
    'ic_graph.xml', 'ic_test.xml', 'ic_save.xml', 'ic_copy.xml', 'ic_share.xml', 'ic_baseline.xml',
    'ic_profile.xml', 'ic_download_update.xml'
]
if release_minor >= 16:
    required_drawables += ['ic_code.xml', 'ic_memory_heap.xml', 'ic_memory_type.xml', 'ic_self_test.xml', 'ic_check_updates.xml']
if release_minor >= 17 and current_version < (1, 2, 0):
    required_drawables += ['ic_zip_download.xml']
if current_version >= (1, 2, 0):
    required_drawables += ['ic_receive.xml', 'ic_download.xml']
for name in required_drawables:
    path = root / 'app/src/main/res/drawable' / name
    need(path.is_file(), f'required semantic icon missing: {name}')
    if path.is_file():
        try:
            ET.parse(path)
        except Exception as exc:
            errors.append(f'invalid vector drawable XML {name}: {exc}')

if not args.skip_release_records:
    need(rules_path.is_file(), 'PROJECT_RULES.md is missing')
    if rules_path.is_file():
        rules = rules_path.read_text(encoding='utf-8')
        need('## Release 1.0.11 active-driver exclusivity and semantic-icon UI requirements' in rules, 'PROJECT_RULES 1.0.11 contract missing')
    need(audit_path.is_file(), '1.0.11 audit record missing')
    need((root / 'tests/golden/1.0.10_regression_contract.json').is_file(), '1.0.10 immutable regression contract missing')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 1.0.11 active-driver exclusivity / semantic-icon UI contract')
