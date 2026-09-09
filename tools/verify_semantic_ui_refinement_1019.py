#!/usr/bin/env python3
import argparse
import json
import re
import xml.etree.ElementTree as ET
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', type=Path, default=Path(__file__).resolve().parents[1])
parser.add_argument('--skip-version', action='store_true')
args = parser.parse_args()
root = args.root.resolve()
errors = []

def need(condition, message):
    if not condition:
        errors.append(message)

def block(text, start, end):
    a = text.find(start)
    b = text.find(end, a + len(start)) if a >= 0 else -1
    need(a >= 0 and b > a, f'block missing: {start}')
    return text[a:b] if a >= 0 and b > a else ''

main_path = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
gradle_path = root / 'app/build.gradle.kts'
manifest_path = root / 'app/src/main/AndroidManifest.xml'
lock_path = root / 'registry/registry_lock.json'
database_setup_path = root / 'DATABASE_SETUP.md'
rules_path = root / 'rules/PROJECT_RULES.md'
audit_path = root / 'rules/1.0.19_SEMANTIC_UI_REFINEMENT_AUDIT.md'
quality_path = root / 'tools/quality_gate.py'
for path in [main_path, gradle_path, manifest_path, lock_path, database_setup_path, rules_path, quality_path]:
    need(path.is_file(), f'missing required file: {path.relative_to(root)}')
for name in ['ic_download.xml', 'ic_compass.xml', 'ic_shield.xml']:
    path = root / 'app/src/main/res/drawable' / name
    need(path.is_file(), f'missing requested vector: {name}')
    if path.is_file():
        try:
            ET.parse(path)
        except ET.ParseError as exc:
            errors.append(f'invalid vector XML {name}: {exc}')
if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)

main = main_path.read_text(encoding='utf-8')
gradle = gradle_path.read_text(encoding='utf-8')
manifest = manifest_path.read_text(encoding='utf-8')
lock = json.loads(lock_path.read_text(encoding='utf-8'))
database_setup = database_setup_path.read_text(encoding='utf-8')
rules = rules_path.read_text(encoding='utf-8')
quality = quality_path.read_text(encoding='utf-8')

if not args.skip_version:
    need('versionCode = 1019' in gradle and 'versionName = "1.0.19"' in gradle, '1.0.19 release identity missing')

info = block(main, 'private fun InfoPage(', '@Composable\nprivate fun SettingsPage')
settings = block(main, 'private fun SettingsPage(', '@Composable\nprivate fun UnavailableTurnipKeyValue')
section_icons = block(main, 'private fun capabilitySectionIcon(title: String): Int = when {', '@Composable\nprivate fun preferExpandedTextLayout')
section_header = block(main, 'private fun SectionHeaderIcon(', '@Composable\nprivate fun DisplaySectionBadgeIcon')
filter_carousel = block(main, 'private fun ExpressiveFilterCarousel(', '@Composable\nprivate fun ExpressiveFilterBar')
update_source = block(main, 'private fun UpdateSourceIcon()', '@Composable\nprivate fun UpdateStatusBanner')
action_art = block(main, 'private fun ActionButtonIconArtwork(', '@Composable\nprivate fun ExpressiveActionButton')
action_button = block(main, 'private fun ExpressiveActionButton(', '@Composable\nprivate fun ExpressiveExternalLinkRow')
scroll_indicators = block(main, 'private fun ScrollBoundaryIndicatorBubble(', '@Composable\nprivate fun QuickAccessCard')

need('R.drawable.ic_download, enabled = directUpdatesEnabled && networkAvailable, onClick = onCheckForUpdates' in info, 'Check for updates does not use the simple download glyph')
need('title.equals("Updates", true) -> R.drawable.ic_download' in section_icons, 'Direct GitHub Updates section does not use the simple download glyph')
need('painter = painterResource(R.drawable.ic_download)' in update_source, 'Direct GitHub update status does not use the simple download glyph')
need('icon = R.drawable.ic_zip_download' in settings and 'title = "Import driver ZIP"' in settings, 'Turnip Import driver ZIP lost the ZIP-folder/download glyph')

download_xml = (root / 'app/src/main/res/drawable/ic_download.xml').read_text(encoding='utf-8')
need('M12,4V14M8.5,10.5L12,14L15.5,10.5M5,19H19' in download_xml, 'simple download arrow/baseline geometry drifted')

need(filter_carousel.count('containerColor = VulkanAccentContainer') == 2, 'horizontal carousel arrow containers are not both Vulkan red')
need(filter_carousel.count('contentColor = VulkanTextPrimary') == 2, 'horizontal carousel chevrons are not both white')
need(filter_carousel.count('disabledContainerColor = VulkanAccentContainer.copy(alpha = 0.72f)') == 2, 'disabled horizontal carousel containers lost red treatment')
need(filter_carousel.count('disabledContentColor = VulkanTextPrimary.copy(alpha = 0.72f)') == 2, 'disabled horizontal carousel chevrons lost white treatment')
need('containerColor = VulkanSurfaceTonal' not in filter_carousel, 'old gray horizontal carousel container remains')
need('contentColor = arrowTint' not in filter_carousel, 'horizontal carousel still tints chevrons instead of keeping them white')
need('arrowTint: ComposeColor = VulkanTextPrimary' in filter_carousel, 'compile-compatible carousel parameter contract drifted')
need('private fun ExpressiveFilterBar(labels: List<String>, selectedIndex: Int, arrowTint: ComposeColor = VulkanTextPrimary, onSelected: (Int) -> Unit)' in main, 'compile-safe ExpressiveFilterBar signature drifted')
need('ExpressiveFilterBar(devices.mapIndexed { index, device -> "GPU ${index + 1} · ${device.name.ifBlank { "Unknown" }.take(48)}" }, selectedIndex, onSelected = onSelected)' in main, 'PhysicalDeviceSelector named callback binding regressed')
need('R.drawable.ic_scroll_up else R.drawable.ic_scroll_down' in scroll_indicators, 'vertical scroll arrows were changed while horizontal-only color work was requested')

need('title.equals("Queue query safety", true)' in section_header, 'Queue query safety composite branch missing')
queue_branch = block(section_header, 'title.equals("Queue query safety", true) -> {', 'title.equals("Export complete report", true) -> {')
need('R.drawable.ic_queues' in queue_branch and 'R.drawable.ic_shield' in queue_branch, 'Queue query safety is not queues + shield')

need('title.equals("Export complete report", true) -> R.drawable.ic_surface' in section_icons, 'Export complete report primary mapping is not Surface')
export_branch = block(section_header, 'title.equals("Export complete report", true) -> {', 'title.equals("Surface + Display presentation evidence", true) -> {')
need('R.drawable.ic_surface' in export_branch and 'R.drawable.ic_export' in export_branch, 'Export complete report is not Surface + export-arrow composite')

need('title.equals("Export TXT", true) -> "TXT"' in action_art, 'Export TXT badge mapping missing')
need('title.equals("Export HTML", true) -> "HTML"' in action_art, 'Export HTML badge mapping missing')
need('ActionButtonIconArtwork(title, icon, accent)' in action_button and action_button.count('ActionButtonIconArtwork(title, icon, accent)') == 2, 'TXT/HTML composite artwork is not used in both action-button layouts')
need('ExpressiveActionButton("Export TXT"' in info and 'R.drawable.ic_action_text' in info, 'Export TXT no longer keeps its existing text icon')
need('ExpressiveActionButton("Export HTML"' in info and 'R.drawable.ic_action_html' in info, 'Export HTML no longer keeps its existing HTML icon')

need('title.equals("Explore", true) -> R.drawable.ic_compass' in section_icons, 'Explore does not use the compass glyph')
need('title.contains("quick access", true) -> R.drawable.ic_home' in section_icons, 'Quick access home mapping changed unintentionally')
compass_xml = (root / 'app/src/main/res/drawable/ic_compass.xml').read_text(encoding='utf-8')
shield_xml = (root / 'app/src/main/res/drawable/ic_shield.xml').read_text(encoding='utf-8')
need('M12,3A9,9' in compass_xml and 'M15.8,8.2L13.5,13.5L8.2,15.8L10.5,10.5Z' in compass_xml, 'compass geometry drifted')
need('M12,3.5L19,6.2V11.4' in shield_xml, 'shield geometry drifted')

need(manifest.count('android.permission.INTERNET') == 1, 'INTERNET permission count drifted')
need('android:usesCleartextTraffic="false"' in manifest, 'cleartext traffic protection missing')
need('android:allowBackup="false"' in manifest, 'backup protection drifted')
need('private const val OFFICIAL_DATABASE_API_ENDPOINT = "https://vulkanscope-database-api.vulkanscope.workers.dev"' in main, 'fixed Database HTTPS origin drifted')
need('https://api.github.com/repos/EFIShell0/VulkanScope/releases?per_page=20' in main, 'official update API route drifted')
need('put("schemaVersion", 2)' in main and 'put("technicalReport", technicalReportJson(context, report, display, mode))' in main, 'Database submission schema drifted')
need('payload.size > 2 * 1024 * 1024' in main and 'No data was truncated.' in main, 'Database no-truncation ceiling drifted')
need(lock.get('apiBaseline') == 'Vulkan 1.4.362' and lock.get('headerVersion') == 362, 'Vulkan 1.4.362/header 362 pin drifted')
need(lock.get('publishedDate') == '2026-09-04', 'pinned registry publication date drifted')
need('VulkanScope Database 1.0.8 is the companion Database for VulkanScope 1.0.19' in database_setup, 'Database companion documentation does not match 1.0.19')
need('versionCode 1019' in database_setup and 'schema 2 / technicalReport 3' in database_setup and 'normalizer 16' in database_setup, 'Database 1.0.19 producer/schema documentation drifted')
need('## Release 1.0.19 semantic UI refinement requirements' in rules, 'PROJECT_RULES 1.0.19 contract missing')
need(audit_path.is_file(), '1.0.19 audit record missing')
for tool in ['verify_semantic_ui_refinement_1019.py', 'test_semantic_ui_refinement_1019_state_machine.py', 'test_semantic_ui_refinement_1019_negative_mutations.py']:
    need(tool in quality, f'aggregate quality gate omits 1.0.19 tool: {tool}')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 1.0.19 semantic UI refinement contract')
