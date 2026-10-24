from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
K = ROOT / 'app/src/main/java/com/efishell/vulkanscope'
read = lambda name: (K / name).read_text(encoding='utf-8')
settings = read('UiSettings.kt')
chrome = read('UiChrome.kt')
root = read('VulkanScopeRoot.kt')
reports = read('UiReports.kt')
ency = read('UiEncyclopedia.kt')
encevidence = read('EncyclopediaDeviceEvidence.kt')
exts = read('UiExtensions.kt')
formats = read('UiFormatsAndProperties.kt')
shared = read('EvidenceCards.kt')
score = read('CapabilityEvidenceScore.kt')
models = read('CoreModels.kt')
ui = read('UiDeviceScreens.kt')
components = read('UiComponents.kt')
glass = read('PinnedPagerGlassSurface.kt')

assert 'DRIVER_UPDATES("Drivers & Preferences"' in models
assert settings.count('ChromeVisibilityPreferencesCard()') == 1
assert settings.index('ChromeVisibilityPreferencesCard()') > settings.index('CapabilitySectionCard("Opening animation")')
status = reports.split('internal fun DatabaseSubmissionStatusCard(', 1)[1].split('\n}', 1)[0]
assert 'Icon(' not in status and 'painterResource(' not in status
assert all(x in status for x in ('Ready to submit', 'Connection unavailable', 'Report submitted'))

settings_card = ui.split('internal fun ExpressiveDestinationCard(', 1)[1].split('\n@Composable\ninternal fun OverviewDestinationCard(', 1)[0]
assert 'OPEN SECTION' not in settings_card and 'Explore' not in settings_card
assert 'VULKAN SCOPE' not in settings_card and 'val tag =' not in settings_card
assert 'Modifier.fillMaxWidth().clip(shape).clickable' not in settings_card
assert 'IconButton(' in settings_card and 'onClick = onClick' in settings_card
assert 'modifier = Modifier.size(48.dp)' in settings_card

assert 'val container = if (!enabled) ComposeColor(0xFF272126)' in components
assert 'val titleColor = if (enabled) ComposeColor(0xFFF7F2F3) else ComposeColor(0xFFC9BFC1)' in components
assert 'val detailColor = if (enabled) ComposeColor(0xFFB6ACAE) else ComposeColor(0xFFAAA0A3)' in components
assert 'disabledContainerColor = ComposeColor(0xFF3D3238)' in components
assert 'disabledContentColor = ComposeColor(0xFFC9BFC1)' in components
assert 'ExpressiveActionButton("Check for updates"' in reports
assert 'ExpressiveActionButton("Run Vulkan self-tests"' in read('AnalysisScreens.kt')

assert 'LocalAppHeaderVisible provides headerVisible' in root
assert 'val pagerShouldBeVisible = LocalAppHeaderVisible.current &&' in chrome
assert 'retainedPagerVisual' in chrome and 'delay(275L)' in chrome
assert 'pagerJoinFraction by animateFloatAsState(' in chrome and 'pagerAndGlassJoinFraction' in chrome
assert 'alpha = pagerJoinFraction' in chrome and 'PinnedPagerGlassSurface(' in chrome
assert 'glassHeight' not in chrome
assert '.clip(shape)' in glass and '.border(1.dp, VulkanGlassBorder, shape)' in glass
assert 'drawLayer(blurredLayer)' in glass and 'drawRect(VulkanGlassTint)' in glass
assert 'fillMaxWidth().height(glassHeight)' not in chrome and 'if (glassHeight >' not in chrome

assert 'val availabilityFilters = listOf("All entries", "On this device")' in ency
assert 'collectedDeviceEncyclopediaEntries(report, device)' in ency
assert 'encyclopediaSearch(category, query)' in ency
assert 'if (!extension.supported) continue' in encevidence
assert 'if (!feature.supported) continue' in encevidence
assert 'if (!format.supported) continue' in encevidence
assert 'VULKAN_EXTENSION_REFERENCE[extension.name]' in encevidence
assert 'VulkanEvidenceCard(' in shared and 'VulkanEvidenceCard(' in exts and 'VulkanEvidenceCard(' in formats and 'VulkanEvidenceCard(' in ency
assert 'Vulkan API version' in score and 'Device extensions' in score and 'Advertised feature bits' in score
assert 'Collection integrity' in score and 'Surface presentation' in score
assert 'evidenceCoverage < 45' in score
assert 'internal fun imageFormatQueryGroupState' in read('ReportExportPipeline.kt')
assert 'internal enum class SupportFilter' in read('CapabilityFilters.kt')
assert 'internal fun imageFormatQueryGroupState' not in exts
for path in [ROOT / 'README.md', ROOT / 'changelog.md', ROOT / 'DATABASE_SETUP.md', ROOT / 'rules/PROJECT_RULES.md', *sorted((ROOT / 'docs').glob('*.md'))]:
    text = path.read_text(encoding='utf-8')
    assert not re.search('[ÇĞİÖŞÜçğıöşü]', text), f'Turkish text in {path.relative_to(ROOT)}'
print('PASS 4.0.9 Settings chevron-only navigation, readable disabled actions, bounded pager blur, score/filter modularity and English documentation (static)')
