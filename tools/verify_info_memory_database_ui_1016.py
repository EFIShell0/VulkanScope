#!/usr/bin/env python3
import argparse
import hashlib
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
quality_path = root / 'tools/quality_gate.py'
rules_path = root / 'rules/PROJECT_RULES.md'
audit_path = root / 'rules/1.0.16_INFO_MEMORY_DATABASE_ICON_AUDIT.md'
database_setup_path = root / 'DATABASE_SETUP.md'
scope_path = root / 'app/src/main/res/drawable-nodpi/vulkanscope_scope_wordmark.png'
launcher_foreground_path = root / 'app/src/main/res/drawable-nodpi/vulkanscope_logo_foreground.png'

for path in [main_path, gradle_path, quality_path, scope_path, launcher_foreground_path, database_setup_path]:
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

def need(condition, message):
    if not condition:
        errors.append(message)

def block(start, end):
    a = main.find(start)
    b = main.find(end, a + len(start)) if a >= 0 else -1
    need(a >= 0 and b > a, f'block missing: {start}')
    return main[a:b] if a >= 0 and b > a else ''

if not args.skip_version:
    need('versionCode = 1016' in gradle and 'versionName = "1.0.16"' in gradle, '1.0.16 release identity missing')

info = block('private fun InfoPage(', '@Composable\nprivate fun SettingsPage')
section_icons = block('private fun capabilitySectionIcon(title: String): Int = when {', '@Composable\nprivate fun preferExpandedTextLayout')
section_header = block('private fun SectionHeaderIcon(', '@Composable\nprivate fun DisplaySectionBadgeIcon')
version_block = block('private fun ExpressiveVersionBlock(', '@Composable\nprivate fun ExpressiveInfoPill')
analysis = block('private fun LazyListScope.analysisWorkspaceItems(', '@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun DetailAffordance')

need('title.equals("Application", true) -> R.drawable.vulkanscope_scope_wordmark' in section_icons, 'Application section does not map to the SCOPE wordmark')
need('sectionIcon == R.drawable.vulkanscope_scope_wordmark' in section_header and 'Image(' in section_header and '.width(32.dp).height(18.dp)' in section_header, 'Application SCOPE wordmark is not rendered as fitted untinted artwork')
need(hashlib.sha256(scope_path.read_bytes()).hexdigest() == '9167a36fe6f31e21d796bc01d42276d1e0f2397193ab6e814133b7bf0dc72ed9', 'SCOPE wordmark resource drifted from the exact cropped shipped logo')
need(hashlib.sha256(launcher_foreground_path.read_bytes()).hexdigest() == '7e760465e1a9da36ac3b4d4c86e913d8391af57c45291d58062f25baa254b801', 'launcher foreground logo resource drifted')
need('Image(painter = painterResource(R.drawable.vulkanscope_logo_foreground)' in version_block and 'contentScale = ContentScale.Fit' in version_block, 'VulkanScope application identity does not reuse the launcher foreground logo')
need('R.drawable.ic_app' not in version_block, 'old generic application glyph remains in the VulkanScope identity block')

need('title.equals("Developer", true) -> R.drawable.ic_code' in section_icons, 'Developer section does not use the code glyph')
need('ExpressiveIdentityBlock("Semih Boran", "EFI Shell · VulkanScope developer", R.drawable.ic_code)' in info, 'Developer identity does not use the code glyph')
need('R.drawable.ic_check_updates, enabled = directUpdatesEnabled && networkAvailable, onClick = onCheckForUpdates' in info, 'Check for updates does not use the line-free update glyph')

need('title.equals("Memory heaps", true) -> R.drawable.ic_memory_heap' in section_icons, 'Memory heaps does not use its dedicated RAM glyph')
need('title.equals("Memory types", true) -> R.drawable.ic_memory_type' in section_icons, 'Memory types does not use its dedicated RAM glyph')
need('R.drawable.ic_self_test' in next((line for line in analysis.splitlines() if 'ExpressiveActionButton("Run Vulkan self-tests"' in line), ''), 'Run Vulkan self-tests does not use the dedicated self-test glyph')

vector_names = ['ic_code.xml', 'ic_memory_heap.xml', 'ic_memory_type.xml', 'ic_self_test.xml', 'ic_check_updates.xml']
vector_bytes = {}
for name in vector_names:
    path = root / 'app/src/main/res/drawable' / name
    need(path.is_file(), f'missing semantic vector: {name}')
    if path.is_file():
        try:
            ET.parse(path)
            vector_bytes[name] = path.read_bytes()
        except ET.ParseError as exc:
            errors.append(f'invalid vector drawable XML {name}: {exc}')
need(vector_bytes.get('ic_memory_heap.xml') != vector_bytes.get('ic_memory_type.xml'), 'Memory heaps and Memory types reuse identical icon geometry')
if (root / 'app/src/main/res/drawable/ic_check_updates.xml').is_file():
    check_updates = (root / 'app/src/main/res/drawable/ic_check_updates.xml').read_text(encoding='utf-8')
    need('M12,4v11M8.5,11.5L12,15l3.5,-3.5' in check_updates, 'Check for updates arrow geometry drifted')
    for forbidden in ['M5,17h14', 'M6.5,17v2.5h11V17', 'h14']:
        need(forbidden not in check_updates, 'Check for updates reintroduced the lower tray/short line')

need('private const val OFFICIAL_DATABASE_API_ENDPOINT = "https://vulkanscope-database-api.vulkanscope.workers.dev"' in main, 'official Database API endpoint drifted')
need('.addPathSegments("v1/reports/$id").addQueryParameter("compact", "1")' in main and '.get().build()' in main, 'Database report lookup route/compact GET drifted')
need('payload.optJSONObject("technicalReport") ?: error("Database report does not contain technicalReport")' in main, 'Database lookup no longer requires technicalReport')
need('put("schemaVersion", 2)' in main and 'put("technicalReport", technicalReportJson(context, report, display, mode))' in main, 'Database submission schema/envelope drifted')
need('.addPathSegments("v1/reports").build()' in main and '.post(payload.toRequestBody("application/json; charset=utf-8".toMediaType()))' in main, 'Database submission POST route drifted')
need('payload.size > 2 * 1024 * 1024' in main and 'No data was truncated.' in main, 'Database 2 MiB fail-closed transport ceiling drifted')
need('VulkanScope Database 1.0.8' in database_setup and 'schema 2 / technicalReport 3' in database_setup and 'normalizer 16' in database_setup, 'Database 1.0.8 companion contract is not documented')

for name in ['verify_info_memory_database_ui_1016.py', 'test_info_memory_database_ui_1016_state_machine.py', 'test_info_memory_database_ui_1016_negative_mutations.py']:
    need(name in quality, f'aggregate quality gate omits 1.0.16 suite: {name}')

if not args.skip_release_records:
    need(rules_path.is_file(), 'PROJECT_RULES.md is missing')
    need(audit_path.is_file(), '1.0.16 audit record missing')
    if rules_path.is_file():
        need('## Release 1.0.16 Info, Memory and Database compatibility requirements' in rules_path.read_text(encoding='utf-8'), 'PROJECT_RULES 1.0.16 contract missing')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 1.0.16 Info/Memory semantic artwork and Database 1.0.8 compatibility contract')
