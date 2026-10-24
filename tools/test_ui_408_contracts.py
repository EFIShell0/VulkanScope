from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
K = ROOT / 'app/src/main/java/com/efishell/vulkanscope'
read = lambda name: (K / name).read_text(encoding='utf-8')
settings = read('UiSettings.kt')
chrome = read('UiChrome.kt')
reports = read('UiReports.kt')
ency = read('UiEncyclopedia.kt')
encevidence = read('EncyclopediaDeviceEvidence.kt')
exts = read('UiExtensions.kt')
formats = read('UiFormatsAndProperties.kt')
shared = read('EvidenceCards.kt')
score = read('CapabilityEvidenceScore.kt')
models = read('CoreModels.kt')
ui = read('UiDeviceScreens.kt')

assert 'DRIVER_UPDATES("Drivers & Preferences"' in models
assert settings.count('ChromeVisibilityPreferencesCard()') == 1
assert settings.index('ChromeVisibilityPreferencesCard()') > settings.index('CapabilitySectionCard("Opening animation")')
assert 'DatabaseSubmissionStatusCard(' in reports
status = reports.split('internal fun DatabaseSubmissionStatusCard(', 1)[1].split('\n}', 1)[0]
assert 'Icon(' not in status and 'painterResource(' not in status
assert 'Ready to submit' in status and 'Connection unavailable' in status and 'Report submitted' in status
assert '.clickable(role = Role.Button, onClick = onClick)' in ui
assert 'OPEN SECTION' in ui and 'ABOUT & DEVICE' in ui and 'EXPORT & SHARING' in ui
assert 'val availabilityFilters = listOf("All entries", "On this device")' in ency
assert 'collectedDeviceEncyclopediaEntries(report, device)' in ency
assert 'encyclopediaSearch(category, query)' in ency
assert 'if (!extension.supported) continue' in encevidence
assert 'if (!feature.supported) continue' in encevidence
assert 'if (!format.supported) continue' in encevidence
assert 'entry.detail.contains' not in encevidence
assert 'VULKAN_EXTENSION_REFERENCE[extension.name]' in encevidence
assert 'VulkanEvidenceCard(' in shared and 'VulkanEvidenceCard(' in exts and 'VulkanEvidenceCard(' in formats and 'VulkanEvidenceCard(' in ency
assert read('UiDeviceScreens.kt').count('VulkanEvidenceCard(') >= 2
assert 'modifier = Modifier.fillMaxWidth()' in shared
assert 'pagerJoinFraction by animateFloatAsState(' in chrome
assert 'retainedPagerVisual' in chrome and 'delay(260L)' in chrome
assert '.graphicsLayer { alpha = pagerJoinFraction }' in chrome
assert 'alpha = pagerJoinFraction' in chrome and 'pagerAndGlassJoinFraction' in chrome
assert 'Vulkan API version' in score and 'Device extensions' in score and 'Advertised feature bits' in score
assert 'Collection integrity' in score and 'Surface presentation' in score
assert 'evidenceCoverage < 45' in score
assert 'sqrt(uniqueDeviceExtensions.coerceAtLeast(0).toDouble() / 160.0)' in score
assert 'Not applicable' not in score or 'not_applicable' in score
assert 'internal fun imageFormatQueryGroupState' in read('ReportExportPipeline.kt')
assert 'internal enum class SupportFilter' in read('CapabilityFilters.kt')
assert 'internal fun imageFormatQueryGroupState' not in exts
for path in [ROOT / 'README.md', ROOT / 'changelog.md', ROOT / 'DATABASE_SETUP.md', ROOT / 'rules/PROJECT_RULES.md', *sorted((ROOT / 'docs').glob('*.md'))]:
    text = path.read_text(encoding='utf-8')
    assert not re.search('[ÇĞİÖŞÜçğıöşü]', text), f'Non-English/Turkish text in {path.relative_to(ROOT)}'
print('PASS 4.0.8 redesigned shared cards, pager-blur lifetime, score modules, exact device filter and English-only docs')
