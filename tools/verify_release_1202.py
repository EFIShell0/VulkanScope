#!/usr/bin/env python3
import argparse
import re
import xml.etree.ElementTree as ET
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
    need(version_match is not None and code_match is not None and current_version >= (1, 2, 2) and int(code_match.group(1)) == expected_code, 'retained 1.2.2+ release identity missing')

app_header = between('private fun AppHeader(', '@Composable\nprivate fun DisplayPage')
need('onInfo' not in app_header and 'ExpressiveIconButton(R.drawable.ic_info, "Info"' not in app_header, 'header still exposes a separate Info action')
need('ExpressiveIconButton(R.drawable.ic_settings, "Settings", onSettings)' in app_header, 'header Settings action missing')
need('var settingsSection by rememberSaveable { mutableStateOf<SettingsSection?>(null) }' in main, 'Settings chooser does not start at its three-destination root')
need('if (page == Page.Settings && settingsSection != null) settingsSection = null else page = Page.Overview' in main, 'nested Settings back behavior missing')
settings_enum = between('private enum class SettingsSection', 'private enum class DriverMode')
need(settings_enum.find('INFO("Info"') < settings_enum.find('REPORTS("Reports & Database"') < settings_enum.find('DRIVER_UPDATES("Driver & Update Preferences"'), 'Settings destination order/labels drifted')
need('private fun ExpressiveDestinationCard(' in main, 'shared expressive destination card missing')
need('ExpressiveDestinationCard(title, subtitle, pageIcon(destination))' in main, 'Overview destination does not use the shared destination card')
need('ExpressiveDestinationCard(section.label, section.description, section.icon)' in main, 'Settings destinations do not use the exact shared destination card')
settings_page = between('private fun SettingsPage(', '@Composable\nprivate fun SystemDriverManagerRow')
root_destination_ok = ('if (selectedSection == null)' in settings_page or ('targetState = selectedSection' in settings_page and 'null -> VulkanLazyPage' in settings_page)) and 'SettingsSectionCards { onSectionSelected(it) }' in settings_page
need(root_destination_ok, 'Settings root does not show only destination cards')
nested_destinations_ok = ('if (selectedSection == SettingsSection.INFO)' in settings_page and 'if (selectedSection == SettingsSection.REPORTS)' in settings_page) or ('SettingsSection.INFO -> InfoPage(' in settings_page and 'SettingsSection.REPORTS -> InfoPage(' in settings_page and 'SettingsSection.DRIVER_UPDATES -> DriverUpdatePreferencesPage(' in settings_page)
need(nested_destinations_ok, 'Settings nested destinations are not separated')

filter_bar = between('private fun ExpressiveFilterBar(', 'private fun ExpressiveMultiFilterBar')
need('Role.Switch' not in main, 'a row still claims Switch semantics outside the Switch control')
need('.toggleable(' not in filter_bar, 'All explanatory row remains toggleable')
need('ExpressiveSwitch(checked = allEnabled, onCheckedChange = { enabled -> onSelected(if (enabled) 0 else 1) })' in filter_bar, 'All switch is not the sole active target')
need('enabled = !allEnabled' in filter_bar, 'specific filters are not locked while All is active')
toggle_row = between('private fun ExpressiveToggleRow(', '@Composable\nprivate fun ExpressiveMetric')
need('.clickable(' not in toggle_row and '.toggleable(' not in toggle_row, 'shared switch row has a second click target')
need('ExpressiveSwitch(checked = checked, onCheckedChange = onCheckedChange)' in toggle_row, 'shared switch control lost direct ownership')
update_pref = between('CapabilitySectionCard("Update preferences")', 'CapabilitySectionCard("Driver manager")')
need('.toggleable(' not in update_pref and 'ExpressiveSwitch(checked = directUpdatesEnabled, onCheckedChange = onDirectUpdatesChanged)' in update_pref, 'Direct GitHub Updates row is not switch-only')

search = between('private fun ExpressiveSearchField(', '@Composable\nprivate fun ExpressiveFilterChip')
need('AnimatedVisibility(' in search and 'visible = value.isNotEmpty()' in search, 'search clear action does not animate in only for non-empty text')
need('IconButton(onClick = { onValueChange("") }, enabled = enabled' in search, 'search clear action does not clear the complete field or respect disabled state')
need('R.drawable.ic_close' in search and 'contentDescription = "Clear search"' in search, 'search clear X artwork/semantics missing')

need('returning to normal in 3 seconds' not in main, 'transient feedback still advertises the three-second timer')
need('delay(3000)' in main, 'three-second transient result lifetime was removed instead of only hiding its explanatory copy')
share_link = re.search(r'ExpressiveActionButton\("Share link"[^\n]+', main)
need(share_link is not None and f'trailingIcon = R.drawable.{"ic_open_external" if current_version >= (1, 2, 4) else "ic_link"}' in share_link.group(0), 'Share link trailing artwork drifted from the applicable release contract')
share_evidence = re.search(r'ExpressiveActionButton\("Share evidence"[^\n]+', main)
need(share_evidence is not None and f'trailingIcon = R.drawable.{"ic_open_external" if current_version >= (1, 2, 4) else "ic_link"}' in share_evidence.group(0), 'Share evidence trailing artwork drifted from the applicable release contract')
need('ExpressiveActionButton("Check for updates"' in main and 'R.drawable.ic_download' in re.search(r'ExpressiveActionButton\("Check for updates"[^\n]+', main).group(0), 'Check for updates did not restore the 1.0.19 leading download glyph')
need('trailingIcon = R.drawable.ic_receive' in re.search(r'ExpressiveActionButton\("Check for updates"[^\n]+', main).group(0), 'Check for updates lost receive trailing artwork')
need('enabled = directUpdatesEnabled && networkAvailable && !updateCheckInFlight' in main, 'Check for updates no longer remains disabled while a result is outstanding')

need('val canAddWatch' in main and 'resolveWatchInput' in main, 'watched Add validation is missing')
need('enabled = model.canAddWatch' in main, 'Add to watch list is not disabled for invalid input')
need('pendingWatchDelete by mutableStateOf<String?>(null)' in main and 'QuestionDialogTitle("Remove watched evidence?")' in main, 'single watched-entry delete confirmation missing')
need('pendingWatchDeleteAll by mutableStateOf(false)' in main and 'QuestionDialogTitle("Delete all watched evidence?")' in main, 'watched clear-all confirmation missing')
need('ExpressiveCloseButton' in main and 'ExpressiveContainedIconTextButton("Delete", R.drawable.ic_delete' in main, 'watched delete dialog action hierarchy drifted')
need('ExpressiveContainedIconTextButton("Clear all", R.drawable.ic_clear_all' in main and 'enabled = state.watched.isNotEmpty()' in main, 'watched Clear all state/action missing')

need('HdrCapabilitiesCarousel(display.hdrTypes)' in main, 'HDR capabilities does not use the arrow carousel')
hdr = between('private fun HdrCapabilitiesCarousel(', '@Composable\nprivate fun HdrTypeCard')
need('ic_chevron_left' in hdr and 'ic_chevron_right' in hdr and 'VulkanAccentContainer' in hdr and 'VulkanTextPrimary' in hdr, 'HDR carousel arrows do not use Vulkan accent containers and white/primary chevrons')
need('ExpressiveContainedIconTextButton("Clear usage filters", R.drawable.ic_clear_filters)' in main, 'Clear usage filters is not a contained semantic action')
need('NavigationItem(Page.Display, "Display", R.drawable.ic_tablet)' in main and 'Page.Display -> R.drawable.ic_tablet' in main, 'Display destination did not switch to tablet artwork')

section_icon = between('private fun SectionHeaderIcon(', '@Composable\nprivate fun DisplaySectionBadgeIcon')
need('SectionVectorBadgeIcon(R.drawable.ic_registry, "REG")' in section_icon, 'Vulkan Registry REG badge missing')
need('title.equals("About", true) -> AboutSectionIcon()' in section_icon, 'About SCOPE+Info composite missing')
need('title.equals("Android runtime", true) -> AndroidRuntimeSectionIcon()' in section_icon, 'Android Runtime RUN composite missing')
need('title.equals("Extension explorer", true) -> SectionVectorBadgeIcon(R.drawable.ic_extensions, overlayIcon = R.drawable.ic_search)' in section_icon, 'Extension explorer search badge missing')
need('title.equals("Format explorer", true) -> SectionVectorBadgeIcon(R.drawable.ic_formats, overlayIcon = R.drawable.ic_search)' in section_icon, 'Format explorer search badge missing')
need('title.equals("Instance layers", true) -> SectionVectorBadgeIcon(R.drawable.ic_library, overlayIcon = R.drawable.ic_extensions)' in section_icon, 'Instance layers semantic composite missing')
need('title.equals("Device layers", true) -> SectionVectorBadgeIcon(R.drawable.ic_cpu, overlayIcon = R.drawable.ic_library)' in section_icon, 'Device layers semantic composite missing')
need('Text("RUN"' in main and 'Text("REG"' not in main, 'runtime textual badge helper drifted')

artwork = between('private fun ActionButtonIconArtwork(', '@Composable\nprivate fun ExpressiveActionButton')
need('CompositeActionVectorIcon(R.drawable.ic_cpu, R.drawable.ic_compare, tint)' in artwork, 'guided System↔Turnip action composite missing')
need('CompositeAndroidActionIcon(R.drawable.ic_compare, tint)' in artwork, 'Switch to System Android+compare composite missing')
need('CompositeMesaActionIcon(R.drawable.ic_compare, tint)' in artwork, 'Switch to Turnip Mesa+compare composite missing')
need('ColorFilter.tint(tint)' in artwork, 'Mesa artwork is not constrained to the app action palette')

resource_paths = [
    root / 'app/src/main/res/drawable/ic_tablet.xml',
    root / 'app/src/main/res/drawable/ic_clear_filters.xml',
    root / 'app/src/main/res/drawable/ic_clear_all.xml',
]
for path in resource_paths:
    need(path.is_file(), f'missing local semantic resource: {path.name}')
    if path.is_file():
        try:
            ET.parse(path)
        except Exception as exc:
            errors.append(f'invalid vector resource {path.name}: {exc}')
mesa = root / 'app/src/main/res/drawable-nodpi/mesa3d_logo.webp'
need(mesa.is_file(), 'packaged Mesa artwork missing')
if mesa.is_file():
    need(0 < mesa.stat().st_size <= 512 * 1024, 'Mesa artwork is empty or exceeds the bounded local resource ceiling')
    need(mesa.read_bytes()[:4] == b'RIFF' and mesa.read_bytes()[8:12] == b'WEBP', 'Mesa artwork is not a WebP resource')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 1.2.2 Settings/search/watch/semantic-artwork contract')
