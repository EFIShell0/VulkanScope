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
qr_path = root / 'app/src/main/res/drawable/ic_qr.xml'
quality_path = root / 'tools/quality_gate.py'
rules_path = root / 'rules/PROJECT_RULES.md'
audit_path = root / 'rules/1.0.15_SAF_LAUNCH_FIRST_FULL_AUDIT.md'
for path in [main_path, gradle_path, qr_path, quality_path]:
    if not path.is_file(): errors.append(f'missing required file: {path.relative_to(root)}')
if errors:
    [print('FAIL:', x) for x in errors]
    raise SystemExit(1)
main = main_path.read_text(encoding='utf-8')
gradle = gradle_path.read_text(encoding='utf-8')
quality = quality_path.read_text(encoding='utf-8')

def need(cond, msg):
    if not cond: errors.append(msg)

version_match = re.search(r'versionName\s*=\s*"1\.0\.(\d+)"', gradle)
code_match = re.search(r'versionCode\s*=\s*(\d+)', gradle)
if not args.skip_version:
    need(version_match is not None and code_match is not None and int(version_match.group(1)) >= 15 and int(code_match.group(1)) >= 1015, 'retained 1.0.15+ release identity missing')
need('ExpressiveActionButton("Share link", "Android Sharesheet · no background upload", R.drawable.ic_share)' in main, 'Share link does not use the share glyph')
need('title.equals("Database permalink & QR", true) -> R.drawable.ic_qr' in main, 'Database permalink & QR does not use the QR glyph')
try:
    ET.parse(qr_path)
except ET.ParseError as exc:
    errors.append(f'ic_qr.xml is invalid XML: {exc}')
qr = qr_path.read_text(encoding='utf-8')
need('M4,4H10V10H4Z' in qr and 'M14,4H20V10H14Z' in qr and 'M4,14H10V20H4Z' in qr, 'QR glyph lacks three finder patterns')
section_start = main.find('private fun SectionHeaderIcon(')
section_end = main.find('@Composable\nprivate fun DisplaySectionBadgeIcon', section_start)
section = main[section_start:section_end] if section_start >= 0 and section_end > section_start else ''
need('title.equals("HDR capabilities", true) || title.equals("HDR / wide-color surface detection", true) -> {\n            DisplaySectionBadgeIcon("HDR")' in section, 'HDR section badge routing missing')
need('title.equals("Supported display modes", true) -> {\n            DisplaySectionBadgeIcon("MODE")' in section, 'MODE monitor badge missing')
need('title.equals("Display ↔ Vulkan interpretation", true)' in section and 'painter = painterResource(R.drawable.ic_surface)' in section, 'Display/Vulkan monitor+Surface badge missing')
need('painter = painterResource(R.drawable.ic_display)' in section, 'display badge no longer uses the monitor base glyph')
need('private fun AnimatedNavigationIcon(' in main, 'shared navigation animation composable missing')
need('icon = { AnimatedNavigationIcon(item.page, item.icon, animationTrigger, 24.dp) }' in main, 'bottom navigation does not use animated icons')
need('AnimatedNavigationIcon(\n                            page = item.page,' in main, 'navigation rail does not use animated icons')
need(main.count('animationTrigger += 1') >= 2, 'bottom navigation and rail do not both trigger icon motion on click')
for token in [
    'Page.Overview -> {\n                    scaleX = 1f + 0.16f * wave',
    'Page.Vulkan -> {\n                    rotationZ = -11f * wave',
    'Page.Surface -> {\n                    translationY = -5f * wave',
    'Page.Display -> {\n                    scaleX = 1f + 0.12f * wave',
    'Page.Extensions -> {\n                    rotationZ = 10f * wave'
]:
    need(token in main, f'distinct navigation motion missing: {token.splitlines()[0]}')
need('scaleY = 1f - 0.10f * wave' in main and 'alpha = 1f - 0.12f * wave' in main, 'Display navigation motion lost its screen-like squash/fade')
need('tween(durationMillis = 320)' in main, 'navigation animation is not bounded to the release contract')
for forbidden in ['rememberInfiniteTransition', 'infiniteRepeatable', 'while (true)']:
    need(forbidden not in main[main.find('private fun AnimatedNavigationIcon('):main.find('private fun navigationItems()')], f'unbounded navigation animation introduced: {forbidden}')
for name in ['verify_share_display_navigation_ui_1015.py','test_share_display_navigation_ui_1015_state_machine.py','test_share_display_navigation_ui_1015_negative_mutations.py']:
    need(name in quality, f'aggregate quality gate omits 1.0.15 UI suite: {name}')
if not args.skip_release_records:
    rules = rules_path.read_text(encoding='utf-8') if rules_path.is_file() else ''
    audit = audit_path.read_text(encoding='utf-8') if audit_path.is_file() else ''
    need('semantic Share/QR, display-badge and navigation-motion requirements' in rules, 'PROJECT_RULES 1.0.15 UI contract missing')
    for token in ['Share link', 'QR', 'HDR', 'MODE', 'navigation']:
        need(token.lower() in audit.lower(), f'1.0.15 audit UI evidence missing: {token}')
if errors:
    [print('FAIL:', x) for x in errors]
    raise SystemExit(1)
print('PASS VulkanScope 1.0.15 semantic Share/QR, display badges and navigation motion contract')
