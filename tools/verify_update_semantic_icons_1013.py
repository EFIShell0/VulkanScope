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
audit_path = root / 'rules/1.0.13_UPDATE_SEMANTIC_ICON_AUDIT.md'

for path in [main_path, gradle_path]:
    if not path.is_file():
        errors.append(f'missing required file: {path.relative_to(root)}')
if errors:
    for error in errors: print('FAIL:', error)
    raise SystemExit(1)

main = main_path.read_text(encoding='utf-8')
gradle = gradle_path.read_text(encoding='utf-8')
version_match = re.search(r'versionName\s*=\s*"1\.0\.(\d+)"', gradle)
release_minor = int(version_match.group(1)) if version_match else 0

def need(condition, message):
    if not condition: errors.append(message)

def block(start, end):
    a = main.find(start)
    b = main.find(end, a + len(start)) if a >= 0 else -1
    need(a >= 0 and b > a, f'block missing: {start}')
    return main[a:b] if a >= 0 and b > a else ''

if not args.skip_version:
    need(any(f'versionCode = {code}' in gradle and f'versionName = "{name}"' in gradle for name, code in [('1.0.13', 1013), ('1.0.14', 1014), ('1.0.15', 1015), ('1.0.16', 1016), ('1.0.17', 1017), ('1.0.18', 1018)]), 'release identity is not a retained 1.0.13+ identity')
need('kBaseline = "Vulkan 1.4.362"' in (root / 'app/src/main/cpp/registry_query_catalog.h').read_text(encoding='utf-8'), 'Vulkan baseline drifted')

android_path = root / 'app/src/main/res/drawable/ic_android.xml'
need(android_path.is_file(), 'Android artwork drawable missing')
if android_path.is_file():
    try: ET.parse(android_path)
    except Exception as exc: errors.append(f'invalid Android drawable XML: {exc}')
    android_xml = android_path.read_text(encoding='utf-8')
    need('android:viewportWidth="152"' in android_xml and 'android:viewportHeight="89"' in android_xml, 'Android supplied-artwork viewport drifted')
    need('M151.025,85.224' in android_xml and 'M115.225,67.663' in android_xml, 'Android supplied-artwork path geometry drifted')
    need('android:fillColor="#E2676A"' in android_xml and 'android:fillColor="#351719"' in android_xml, 'Android artwork does not use VulkanScope two-tone palette')
    need('#34A853' not in android_xml and '#202124' not in android_xml, '1.0.12 Android colors were not superseded')

for name in ['ic_update_available.xml', 'ic_database_fetch.xml', 'ic_database_submit.xml', 'ic_database_browse.xml']:
    path = root / 'app/src/main/res/drawable' / name
    need(path.is_file(), f'missing semantic drawable: {name}')
    if path.is_file():
        try: ET.parse(path)
        except Exception as exc: errors.append(f'invalid vector drawable XML {name}: {exc}')

banner = block('private fun UpdateStatusBanner(', '@Composable\nprivate fun UpdateDialogKeyValue')
for token in [
    'is UpdateStatus.Available -> { UpdateAvailableIcon();',
    'ChevronAffordance("Review", "Review update")',
    'VulkanScope ${status.update.version} available',
    'color = VulkanAccentSoft'
]: need(token in banner, f'update-available banner requirement missing: {token}')
need('ExpressiveTextButton("Review")' not in banner, 'Review regressed to uncontained text action')

update_icon = block('private fun UpdateAvailableIcon()', '@Composable\nprivate fun UpdateStatusBadge')
need('R.drawable.ic_update_available' in update_icon, 'update-available banner uses no semantic glyph')
need('color = VulkanAccentContainer' in update_icon and 'tint = VulkanAccentSoft' in update_icon, 'update-available glyph is outside VulkanScope accent system')

chevron = block('private fun ChevronAffordance(', '@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun DetailAffordance')
for token in ['color = VulkanAccentContainer', 'R.drawable.ic_chevron_right', 'IconButton(onClick = onClick', 'modifier = Modifier.semantics { role = Role.Button }']:
    need(token in chevron, f'shared Review/Details chevron affordance missing: {token}')
need('private fun DetailAffordance(onClick: () -> Unit) = ChevronAffordance("Details", "Open details", onClick)' in main, 'Details no longer shares the common chevron affordance')

update_dialog = block('private fun UpdateConfirmationDialog(', '@Composable\nprivate fun ReleaseNotesContent')
for token in [
    'SemanticDialogTitle("Download VulkanScope ${update.version}?", R.drawable.ic_update_available)',
    'ExpressivePrimaryIconTextButton("Download update", R.drawable.ic_download_update, enabled = networkAvailable, onClick = onConfirm)',
    'ExpressiveCancelButton(onClick = onDismiss)',
    'if (!networkAvailable) Text("Download is disabled until Android reports a validated internet connection."'
]: need(token in update_dialog, f'update confirmation requirement missing: {token}')

cancel = block('private fun ExpressiveCancelButton(', '@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun ExpressivePrimaryButton')
need('R.drawable.ic_close' in cancel and 'Text("Cancel", fontWeight = FontWeight.Normal' in cancel, 'Cancel must remain uncontained normal-weight X action')
primary_icon = block('private fun ExpressivePrimaryIconTextButton(', '@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun ExpressiveTextButton')
need('Button(' in primary_icon and 'Icon(painterResource(icon)' in primary_icon and 'Text(label' in primary_icon, 'positive update action is not a contained icon/text button')

need('R.drawable.ic_database_fetch' in next((line for line in main.splitlines() if 'ExpressiveActionButton("Fetch public report"' in line), ''), 'Database fetch lacks dedicated fetch glyph')
need('ExpressiveActionButton(if (submissionInFlight) "Submitting…" else "Submit complete report"' in main and '}, R.drawable.ic_database_submit, enabled = !submissionInFlight && completeReportReady && networkAvailable)' in main, 'Database submit lacks dedicated submit glyph')
need('R.drawable.ic_database_browse' in next((line for line in main.splitlines() if 'ExpressiveExternalLinkRow("Open VulkanScope Database"' in line), ''), 'Database browse lacks dedicated browse glyph')
section_icons = block('private fun capabilitySectionIcon(title: String): Int = when {', '@Composable\nprivate fun preferExpandedTextLayout')
if release_minor >= 17:
    need('title.equals("Compare with VulkanScope Database", true) -> R.drawable.ic_action_database' in section_icons, 'Database compare primary glyph is not Database on 1.0.17+')
    need('title.equals("Database comparison summary", true) -> R.drawable.ic_compare' in section_icons, 'Database comparison summary lost compare glyph')
    section_1017 = block('private fun SectionHeaderIcon(', '@Composable\nprivate fun DisplaySectionBadgeIcon')
    need('title.equals("Compare with VulkanScope Database", true)' in section_1017 and 'R.drawable.ic_compare' in section_1017, 'Database compare overlay glyph missing on 1.0.17+')
else:
    need('title.equals("Compare with VulkanScope Database", true) || title.equals("Database comparison summary", true) -> R.drawable.ic_compare' in section_icons, 'Database comparison sections do not use compare glyph')
need(('title.equals("Database permalink & QR", true) -> R.drawable.ic_qr' if release_minor >= 15 else 'title.equals("Database permalink & QR", true) -> R.drawable.ic_link') in section_icons, 'Database permalink/QR section semantic glyph drifted')
need('title.contains("Database", true) -> R.drawable.ic_action_database' in section_icons, 'generic Database section identity glyph disappeared')

if release_minor >= 15:
    section = block('private fun SectionHeaderIcon(', '@Composable\nprivate fun DisplaySectionBadgeIcon')
    need('sectionIcon == R.drawable.ic_android ->' in section and 'Image(' in section and 'contentScale = ContentScale.Fit' in section, 'Android two-tone artwork is not rendered without generic tint')
else:
    section = block('private fun CapabilitySectionCard(', '@Composable\nprivate fun CapabilityItemCard')
    need('if (sectionIcon == R.drawable.ic_android)' in section and 'Image(' in section and 'contentScale = ContentScale.Fit' in section, 'Android two-tone artwork is not rendered without generic tint')

if not args.skip_release_records:
    need(rules_path.is_file(), 'PROJECT_RULES.md is missing')
    if rules_path.is_file():
        need('## Release 1.0.13 update-action and semantic-icon refinement requirements' in rules_path.read_text(encoding='utf-8'), 'PROJECT_RULES 1.0.13 contract missing')
    need(audit_path.is_file(), '1.0.13 audit record missing')
    need((root / 'tests/golden/1.0.12_regression_contract.json').is_file(), '1.0.12 immutable regression contract missing')

if errors:
    for error in errors: print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 1.0.13 update affordance / Android palette / Database semantic-icon contract')
