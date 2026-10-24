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

assert 'val container = if (!enabled) ComposeColor(0xFF101010)' in components
assert 'else ComposeColor(0xFF58252F)' in components
assert 'modifier = modifier.then(if (wholeRowClickable' not in components
assert 'wholeRowClickable' not in components and 'wholeRowClickable' not in reports
assert 'ExpressiveActionButton("Check for updates"' in reports
assert 'ExpressiveActionButton("Run Vulkan self-tests"' in read('AnalysisScreens.kt')
assert 'LocalAppHeaderVisible provides headerVisible' in root
assert 'val pagerShouldBeVisible = (pagerVisual?.progress ?: 0f) > 0.001f' in chrome
assert 'val pagerBlurFraction by animateFloatAsState(' in chrome
assert 'targetValue = if (LocalAppHeaderVisible.current) 1f else 0f' in chrome
assert 'pagerBlurIndependentOfPinnedPager' in chrome
assert 'if (displayedPager != null && pagerJoinFraction > 0.001f && pagerBlurFraction > 0.001f)' in chrome
assert 'drawLayer(blurredLayer)' in chrome
assert 'blurVisibility = pagerBlurFraction' in chrome
assert 'val pagerJoinFraction by animateFloatAsState(' in chrome
assert 'blurVisibility: Float' in glass
assert '.graphicsLayer { alpha = blurVisibility }' in glass
assert 'drawLayer(blurredLayer)' in glass and 'drawRect(VulkanGlassTint)' in glass
assert 'val pagerShouldBeVisible = LocalAppHeaderVisible.current' not in chrome
assert 'R.drawable.ic_khronos_swirl' in reports
assert (ROOT / 'app/src/main/res/drawable/ic_khronos_swirl.xml').exists()
assert 'ic_chrome_auto_hide' in read('ChromeVisibilityPreferences.kt')
assert 'ic_chrome_header' in read('ChromeVisibilityPreferences.kt')
assert 'ic_chrome_navigation' in read('ChromeVisibilityPreferences.kt')

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
print('PASS 4.0.10 pinned pager retained, independent blur fade, chevron-only actions, Khronos icon, score/filter modularity and English docs (static)')
