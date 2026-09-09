#!/usr/bin/env python3
import argparse
import hashlib
import struct
import zlib
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
quality_path = root / 'tools/quality_gate.py'
rules_path = root / 'rules/PROJECT_RULES.md'
audit_path = root / 'rules/1.0.17_SEMANTIC_ICON_CORRECTIONS_DATABASE_1.0.8_AUDIT.md'
database_setup_path = root / 'DATABASE_SETUP.md'
scope_path = root / 'app/src/main/res/drawable-nodpi/vulkanscope_scope_wordmark.png'
launcher_path = root / 'app/src/main/res/drawable-nodpi/vulkanscope_logo_foreground.png'
zip_icon_path = root / 'app/src/main/res/drawable/ic_zip_download.xml'

for path in [main_path, gradle_path, quality_path, database_setup_path, scope_path, launcher_path, zip_icon_path]:
    if not path.is_file():
        errors.append(f'missing required file: {path.relative_to(root)}')
if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)

main = main_path.read_text(encoding='utf-8')
gradle = gradle_path.read_text(encoding='utf-8')
quality = quality_path.read_text(encoding='utf-8')
database_setup = database_setup_path.read_text(encoding='utf-8')
version_match = __import__('re').search(r'versionName\s*=\s*"([^"]+)"', gradle)
current_version = version_match.group(1) if version_match else '1.0.17'

def need(condition, message):
    if not condition:
        errors.append(message)

def block(start, end):
    a = main.find(start)
    b = main.find(end, a + len(start)) if a >= 0 else -1
    need(a >= 0 and b > a, f'block missing: {start}')
    return main[a:b] if a >= 0 and b > a else ''

def png_rgba(path):
    data = path.read_bytes()
    need(data[:8] == b'\x89PNG\r\n\x1a\n', 'launcher foreground is not PNG')
    if data[:8] != b'\x89PNG\r\n\x1a\n':
        return []
    pos = 8
    width = height = color_type = bit_depth = None
    compressed = bytearray()
    while pos + 12 <= len(data):
        length = struct.unpack('>I', data[pos:pos+4])[0]
        typ = data[pos+4:pos+8]
        payload = data[pos+8:pos+8+length]
        pos += 12 + length
        if typ == b'IHDR':
            width, height, bit_depth, color_type, _, _, _ = struct.unpack('>IIBBBBB', payload)
        elif typ == b'IDAT':
            compressed.extend(payload)
        elif typ == b'IEND':
            break
    need(bit_depth == 8 and color_type == 6, 'launcher foreground must remain RGBA8 PNG')
    if bit_depth != 8 or color_type != 6 or width is None or height is None:
        return []
    raw = zlib.decompress(bytes(compressed))
    stride = width * 4
    rows = []
    prev = bytearray(stride)
    offset = 0
    for _ in range(height):
        f = raw[offset]
        offset += 1
        row = bytearray(raw[offset:offset+stride])
        offset += stride
        for i in range(stride):
            a = row[i-4] if i >= 4 else 0
            b = prev[i]
            c = prev[i-4] if i >= 4 else 0
            if f == 1:
                row[i] = (row[i] + a) & 255
            elif f == 2:
                row[i] = (row[i] + b) & 255
            elif f == 3:
                row[i] = (row[i] + ((a + b) // 2)) & 255
            elif f == 4:
                p = a + b - c
                pa = abs(p - a)
                pb = abs(p - b)
                pc = abs(p - c)
                pr = a if pa <= pb and pa <= pc else b if pb <= pc else c
                row[i] = (row[i] + pr) & 255
            elif f != 0:
                need(False, f'unsupported PNG filter {f}')
                return []
        rows.extend(tuple(row[i:i+4]) for i in range(0, stride, 4))
        prev = row
    return rows

if not args.skip_version:
    need('versionCode = 1017' in gradle and 'versionName = "1.0.17"' in gradle, '1.0.17 release identity missing')

info = block('private fun InfoPage(', '@Composable\nprivate fun SettingsPage')
settings = block('private fun SettingsPage(', '@Composable\nprivate fun UnavailableTurnipKeyValue')
quick = block('private fun QuickAccessCard(', '@Composable\nprivate fun CompactNavigationRail')
analysis = block('private fun LazyListScope.analysisWorkspaceItems(', '@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun DetailAffordance')
section_icons = block('private fun capabilitySectionIcon(title: String): Int = when {', '@Composable\nprivate fun preferExpandedTextLayout')
section_header = block('private fun SectionHeaderIcon(', '@Composable\nprivate fun DisplaySectionBadgeIcon')
filter_carousel = block('private fun ExpressiveFilterCarousel(', '@Composable\nprivate fun ExpressiveFilterBar')
features = block('private fun FeaturesPage(', '@Composable\nprivate fun MemoryPage')
update_source = block('private fun UpdateSourceIcon()', '@Composable\nprivate fun UpdateStatusBanner')
version_block = block('private fun ExpressiveVersionBlock(', '@Composable\nprivate fun ExpressiveInfoPill')

need('title.equals("Developer", true) -> R.drawable.ic_code' in section_icons, 'Developer section header lost code glyph')
need('ExpressiveIdentityBlock("Semih Boran", "EFI Shell · VulkanScope developer", R.drawable.ic_person)' in info, 'Semih Boran identity row is not restored to person glyph')
need('ExpressiveIdentityBlock("Semih Boran", "EFI Shell · VulkanScope developer", R.drawable.ic_code)' not in info, 'Semih Boran identity row still uses code glyph')
need('title.equals("Application", true) -> R.drawable.vulkanscope_scope_wordmark' in section_icons, 'Application SCOPE wordmark mapping drifted')
need(hashlib.sha256(scope_path.read_bytes()).hexdigest() == '9167a36fe6f31e21d796bc01d42276d1e0f2397193ab6e814133b7bf0dc72ed9', 'SCOPE wordmark changed')
need(hashlib.sha256(launcher_path.read_bytes()).hexdigest() == 'bae047898f5f52b99fc246a12f881d5751e06d4a6e4c59ba6ac4c602809ff29f', 'transparent-white launcher foreground drifted')
pixels = png_rgba(launcher_path)
need(any(a > 0 and r >= 240 and g >= 240 and b >= 240 for r, g, b, a in pixels), 'launcher foreground contains no visible white logo pixels')
need(not any(a > 0 and r < 24 and g < 24 and b < 24 for r, g, b, a in pixels), 'launcher foreground reintroduced visible black pixels')
need('Image(painter = painterResource(R.drawable.vulkanscope_logo_foreground)' in version_block, 'Application identity no longer uses launcher foreground')

try:
    ET.parse(zip_icon_path)
except ET.ParseError as exc:
    errors.append(f'invalid ZIP/download vector XML: {exc}')
zip_icon = zip_icon_path.read_text(encoding='utf-8')
need('M3,7H7.4L9.2,9H14.2V19H3Z' in zip_icon, 'ZIP folder geometry missing')
need('M18.5,5V14M15.8,11.3L18.5,14L21.2,11.3' in zip_icon, 'ZIP download-arrow geometry missing')
need('R.drawable.ic_zip_download, enabled = directUpdatesEnabled && networkAvailable, onClick = onCheckForUpdates' in info, 'Check for updates does not use shared ZIP/download glyph')
need('title.equals("Updates", true) -> R.drawable.ic_zip_download' in section_icons, 'Direct GitHub Updates section does not use shared ZIP/download glyph')
need('painter = painterResource(R.drawable.ic_zip_download)' in update_source, 'Direct GitHub update status does not use shared ZIP/download glyph')
need('icon = R.drawable.ic_zip_download' in settings and 'title = "Import driver ZIP"' in settings, 'Turnip Import driver ZIP does not use shared ZIP/download glyph')

need('arrowTint: ComposeColor = VulkanTextPrimary' in filter_carousel, 'filter carousel lost isolated arrow tint parameter')
need('contentColor = arrowTint' in filter_carousel and 'disabledContentColor = arrowTint.copy(alpha = 0.42f)' in filter_carousel, 'filter carousel arrow tint is not applied without geometry changes')
need('ExpressiveFilterBar(sources, sources.indexOf(sourceFilter).coerceAtLeast(0), arrowTint = VulkanAccentSoft)' in features, 'Core/source filter carousel arrows are not Vulkan red')

need('if (title == "HDR & Color")' in quick and 'DisplaySectionBadgeIcon("HDR")' in quick, 'Quick access HDR & Color does not reuse Display HDR badge')
need('title.equals("Global Vulkan report search", true) -> R.drawable.ic_search' in section_icons, 'Global Vulkan report search does not use magnifier')
need('title.equals("Surface + Display presentation evidence", true)' in section_header and 'R.drawable.ic_display' in section_header and 'R.drawable.ic_surface' in section_header, 'Surface + Display presentation evidence composite missing')
need('CapabilitySectionCard("Raw structured technical Report")' in analysis, 'Raw structured technical Report title spacing is not corrected')
need('CapabilitySectionCard("Raw structured technicalReport")' not in analysis, 'old Raw structured technicalReport title remains')
need('title.equals("Raw structured technical Report", true)' in section_header and '"JSON"' in section_header and 'R.drawable.ic_registry' in section_header, 'Raw structured technical Report JSON badge treatment missing')
need('title.equals("Compare with VulkanScope Database", true) -> R.drawable.ic_action_database' in section_icons, 'Database compare primary mapping is not Database')
need('title.equals("Compare with VulkanScope Database", true)' in section_header and 'R.drawable.ic_action_database' in section_header and 'R.drawable.ic_compare' in section_header, 'Database + Compare composite missing')

need('private const val OFFICIAL_DATABASE_API_ENDPOINT = "https://vulkanscope-database-api.vulkanscope.workers.dev"' in main, 'official Database endpoint drifted')
need('.addPathSegments("v1/reports/$id").addQueryParameter("compact", "1")' in main and '.get().build()' in main, 'Database compact GET route drifted')
need('payload.optJSONObject("technicalReport") ?: error("Database report does not contain technicalReport")' in main, 'Database lookup no longer requires technicalReport')
need('put("schemaVersion", 2)' in main and 'put("technicalReport", technicalReportJson(context, report, display, mode))' in main, 'Database submission schema/envelope drifted')
need('.addPathSegments("v1/reports").build()' in main and '.post(payload.toRequestBody("application/json; charset=utf-8".toMediaType()))' in main, 'Database submission POST route drifted')
need('payload.size > 2 * 1024 * 1024' in main and 'No data was truncated.' in main, 'Database fail-closed size ceiling drifted')
need(f'VulkanScope Database 1.0.8 is the companion Database for VulkanScope {current_version}' in database_setup, 'Database 1.0.8 companion contract does not match current retained release')
need('schema 2 / technicalReport 3' in database_setup and 'normalizer 16' in database_setup, 'Database schema/normalizer documentation drifted')

for name in ['verify_semantic_icon_corrections_1017.py', 'test_semantic_icon_corrections_1017_state_machine.py', 'test_semantic_icon_corrections_1017_negative_mutations.py']:
    need(name in quality, f'aggregate quality gate omits 1.0.17 suite: {name}')

if not args.skip_release_records:
    need(rules_path.is_file(), 'PROJECT_RULES.md is missing')
    need(audit_path.is_file(), '1.0.17 audit record missing')
    if rules_path.is_file():
        rules = rules_path.read_text(encoding='utf-8')
        need('## Release 1.0.17 semantic icon correction and Database 1.0.8 compatibility requirements' in rules, 'PROJECT_RULES 1.0.17 contract missing')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 1.0.17 semantic icon corrections and Database 1.0.8 compatibility contract')
