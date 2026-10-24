from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
K = ROOT / 'app/src/main/java/com/efishell/vulkanscope'
ui = (K / 'UiSettings.kt').read_text(encoding='utf-8')
chrome = (K / 'UiChrome.kt').read_text(encoding='utf-8')
reports = (K / 'UiReports.kt').read_text(encoding='utf-8')
ency = (K / 'UiEncyclopedia.kt').read_text(encoding='utf-8')
exts = (K / 'UiExtensions.kt').read_text(encoding='utf-8')
domain = (K / 'AnalysisDomain.kt').read_text(encoding='utf-8')
models = (K / 'CoreModels.kt').read_text(encoding='utf-8')

assert 'DRIVER_UPDATES("Drivers & Preferences"' in models
assert 'ChromeVisibilityPreferencesCard()' in ui
assert ui.index('ChromeVisibilityPreferencesCard()') > ui.index('CapabilitySectionCard("Opening animation")')
assert 'DatabaseSubmissionStatusCard(' in reports
status = reports.split('internal fun DatabaseSubmissionStatusCard(', 1)[1].split('\n}', 1)[0]
assert 'Icon(' not in status and 'painterResource(' not in status
assert 'Ready to submit' in status and 'Connection unavailable' in status and 'Report submitted' in status
assert 'EncyclopediaPage(initialQuery: String = "", report: VulkanReport? = null, device: DeviceReport? = null)' in ency
assert 'val availabilityFilters = listOf("All entries", "On this device")' in ency
assert 'deviceEncyclopediaTokens(report, device)' in ency
assert 'encyclopediaMatchesDeviceEvidence' in ency
assert 'ExtensionExplorerCard(' in exts and 'ExtensionExplorerPill(' in exts
assert 'Runtime-enumerated ${extension.scope.lowercase()} extension token from the active Vulkan path.' in exts
assert 'val glassHeight by animateDpAsState(' in chrome and 'label = "glassHeightTransition"' in chrome
assert 'Device extension census depth ≥ 40' in domain and 'Vulkan API level ≥ 1.4' in domain
print('PASS 4.0.7 UI contracts: preferences placement, icon-free submit state, encyclopedia filter, extension cards and pager blur animation')
