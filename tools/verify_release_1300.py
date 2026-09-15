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
errors = []

def need(condition, message):
    if not condition:
        errors.append(message)

if not args.skip_version:
    need('versionCode = 1300' in gradle and 'versionName = "1.3.0"' in gradle, '1.3.0 release identity missing')
need(re.search(r'\bminSdk\s*=\s*31\b', gradle) is not None, 'Android 12 minimum drifted')
need('private val VULKAN_TRADEMARK_DISPLAY_REGEX = Regex("""\\bVulkan(?!Scope|®)""")' in main, 'display-only Vulkan trademark matcher missing or unsafe')
need('VULKAN_TRADEMARK_DISPLAY_REGEX.replace(text, "Vulkan®")' in main, 'Vulkan trademark display replacement missing')
need('Vulkan®Scope' not in main, 'VulkanScope product name was incorrectly trademark-expanded')
need('"Vulkan® Query Status"' not in main, 'canonical Vulkan Query Status key was mutated')
need('"Vulkan® Query Safety"' not in main, 'canonical Vulkan Query Safety key was mutated')
need('"VkPhysicalDeviceVulkan®' not in main, 'canonical VkPhysicalDeviceVulkan* token was mutated')
need('PropertyEntry("Vulkan Query Status", name, value)' in main, 'canonical Vulkan Query Status storage key drifted')
need('it.section == "Vulkan Query Safety"' in main, 'canonical Vulkan Query Safety section matching drifted')
need('Text(trademarkVulkanDisplayText(key)' in main, 'evidence keys are not presentation-normalized')
need('Text(trademarkVulkanDisplayText(value.ifBlank { "Unavailable" })' in main, 'evidence values are not presentation-normalized')
need('Text(trademarkVulkanDisplayText(page.title)' in main, 'page titles are not presentation-normalized')
need('Text(trademarkVulkanDisplayText(item.label)' in main, 'navigation labels are not presentation-normalized')
need('Text(trademarkVulkanDisplayText(title)' in main, 'shared titles are not presentation-normalized')
need('Text(trademarkVulkanDisplayText(subtitle)' in main, 'shared subtitles are not presentation-normalized')

expected_libraries = {
    'AndroidX Core KTX': ('1.19.0', 'Apache License 2.0', 'licenses/apache_2_0.md'),
    'AndroidX Activity Compose': ('1.13.0', 'Apache License 2.0', 'licenses/apache_2_0.md'),
    'Compose UI': ('1.12.0', 'Apache License 2.0', 'licenses/apache_2_0.md'),
    'Compose Foundation': ('1.12.0', 'Apache License 2.0', 'licenses/apache_2_0.md'),
    'Compose Animation': ('1.12.0', 'Apache License 2.0', 'licenses/apache_2_0.md'),
    'Material 3': ('1.5.0-alpha27', 'Apache License 2.0', 'licenses/apache_2_0.md'),
    'Lifecycle Runtime Compose': ('2.11.0', 'Apache License 2.0', 'licenses/apache_2_0.md'),
    'OkHttp': ('5.5.0', 'Apache License 2.0', 'licenses/apache_2_0.md'),
    'ZXing Core': ('3.5.4', 'Apache License 2.0', 'licenses/apache_2_0.md'),
    'Vulkan® Headers': ('1.4.362', 'Apache-2.0 OR MIT', 'licenses/vulkan_headers.md'),
    'libadrenotools': ('8fae8ce254dfc1344527e05301e43f37dea2df80', 'BSD 2-Clause License', 'licenses/libadrenotools_bsd_2_clause.md'),
}
need('private data class LibraryVersionInfo(val name: String, val version: String, val detail: String, val licenseName: String, val licenseAsset: String)' in main, 'library license metadata fields missing')
for name, (version, license_name, asset) in expected_libraries.items():
    pattern = re.compile(r'LibraryVersionInfo\(\"' + re.escape(name) + r'\",\s*\"' + re.escape(version) + r'\",[^\n]*?\"' + re.escape(license_name) + r'\",\s*\"' + re.escape(asset) + r'\"\)')
    need(pattern.search(main) is not None, f'library license mapping missing or drifted: {name}')

asset_expectations = {
    'app/src/main/assets/licenses/apache_2_0.md': ['Apache License', 'Version 2.0, January 2004'],
    'app/src/main/assets/licenses/libadrenotools_bsd_2_clause.md': ['BSD 2-Clause License', 'Redistribution and use in source and binary forms'],
    'app/src/main/assets/licenses/vulkan_headers.md': ['Vulkan® Headers', 'Apache-2.0 OR MIT', 'MIT License'],
}
for rel, needles in asset_expectations.items():
    path = root / rel
    need(path.is_file(), f'packaged license asset missing: {rel}')
    if path.is_file():
        text = path.read_text(encoding='utf-8')
        need(len(text) >= 1000, f'packaged license asset is unexpectedly incomplete: {rel}')
        for needle in needles:
            need(needle in text, f'packaged license asset lacks expected text {needle!r}: {rel}')

license_start = main.find('private fun LibraryLicenseDialog(library: LibraryVersionInfo, onDismiss: () -> Unit)')
license_end = main.find('@Composable\nprivate fun InfoPage', license_start + 1) if license_start >= 0 else -1
license_dialog = main[license_start:license_end] if license_start >= 0 and license_end > license_start else ''
need(bool(license_dialog), 'library license dialog missing')
need('context.assets.open(library.licenseAsset)' in license_dialog, 'license dialog does not read the packaged asset')
need('R.drawable.ic_action_text' in license_dialog, 'license dialog text artwork missing')
need('ReleaseNotesContent(text, Modifier.fillMaxSize())' in license_dialog, 'license document is not using the scrollable markdown-style renderer')
need('ExpressiveContainedIconTextButton("Close", R.drawable.ic_close, onClick = onDismiss)' in license_dialog, 'contained Close-X action missing from license dialog')
need('VULKANSCOPE_LIBRARY_VERSIONS.forEach { library ->' in main, 'Info library list is not driven from the complete direct-library catalog')
need('ChevronAffordance("License", "Open ${library.name} license") { selectedLibraryLicense = library }' in main, 'Details-style License chevron affordance missing')
need('LibraryLicenseDialog(library = library, onDismiss = { selectedLibraryLicense = null })' in main, 'selected library license is not presented in the dialog')

manifest = (root / 'app/src/main/AndroidManifest.xml').read_text(encoding='utf-8')
need('MANAGE_EXTERNAL_STORAGE' not in manifest, 'unrequested broad storage permission was introduced')
need('READ_EXTERNAL_STORAGE' not in manifest, 'legacy storage permission was reintroduced')
need('OFFICIAL_DATABASE_API_ENDPOINT = "https://vulkanscope-database-api.vulkanscope.workers.dev"' in main, 'Database endpoint drifted')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 1.3.0 Vulkan trademark presentation and library-license contract')
