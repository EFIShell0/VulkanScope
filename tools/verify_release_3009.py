#!/usr/bin/env python3
import argparse
import hashlib
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', type=Path, default=Path(__file__).resolve().parents[1])
args = parser.parse_args()
root = args.root.resolve()
errors = []

def need(condition, message):
    if not condition:
        errors.append(message)

def read(rel):
    path = root / rel
    need(path.is_file(), f'missing file: {rel}')
    return path.read_text(encoding='utf-8') if path.is_file() else ''

def sha(rel):
    path = root / rel
    return hashlib.sha256(path.read_bytes()).hexdigest() if path.is_file() else ''

def block(text, start, end):
    a = text.find(start)
    b = text.find(end, a + len(start)) if a >= 0 else -1
    need(a >= 0 and b > a, f'missing block: {start}')
    return text[a:b] if a >= 0 and b > a else ''

main = read('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
gradle = read('app/build.gradle.kts')
rules = read('rules/PROJECT_RULES.md')
audit = read('rules/3.0.9_DATABASE_TIME_DRIVER_ICON_AUDIT.md')
changelog = read('changelog.md')

declared_files = [line.strip() for line in read('files.txt').splitlines() if line.strip()]
actual_files = sorted(path.relative_to(root).as_posix() for path in root.rglob('*') if path.is_file())
need(declared_files == actual_files, 'files.txt strict census mismatch')

need('versionCode = 3009' in gradle and 'versionName = "3.0.9"' in gradle, '3.0.9 release identity missing')
need(changelog.startswith('## 3.0.9\n'), '3.0.9 changelog entry missing or not first')
need('Visible icon treatments must preserve semantic identity' in rules, 'permanent semantic-icon uniqueness rule missing')
need('## Release 3.0.9 Database date/time, driver identity and semantic icon uniqueness requirements' in rules, '3.0.9 project rules missing')
need('# VulkanScope 3.0.9 Database Time, Driver Identity and Semantic Icon Audit' in audit, '3.0.9 audit heading missing')

database = block(main, '        9 -> {', '        10 -> {')
need('SystemDriverVendorBadge(reportRow.vendorId)' in database, 'Database vendor badge regression')
need('DatabaseDriverModePill(reportRow.driverMode)' in database, 'Database driver-mode styled pill missing')
need('DatabaseSubmittedAt(reportRow.submittedAt)' in database, 'Database localized submitted-time presentation missing')
need('CapabilityKeyValue("Submitted", reportRow.submittedAt.ifBlank { "Unknown" })' not in database, 'raw Database ISO timestamp still used as primary list presentation')

driver = block(main, 'private fun DatabaseDriverModePill(', 'private data class DatabaseSubmittedDisplay')
for token in ['driverMode.contains("turnip", ignoreCase = true)', 'VulkanAccentSoft', 'VulkanTextPrimary', 'fontWeight = FontWeight.Bold']:
    need(token in driver, f'Database Overview-matched driver styling missing: {token}')

time_block = block(main, 'private data class DatabaseSubmittedDisplay', '@Composable\nprivate fun ExpressiveInfoPill(')
for token in ['java.time.Instant.parse', 'android.text.format.DateFormat.getMediumDateFormat', 'android.text.format.DateFormat.getTimeFormat', 'java.util.TimeZone.getDefault()', 'timeZone.inDaylightTime(date)', 'CapabilityKeyValue("Submitted", raw.ifBlank { "Unknown" })', 'Device local time · ${display.zone}']:
    need(token in time_block, f'Database submitted-time contract missing: {token}')

section_icons = block(main, 'private fun SectionHeaderIcon(', '@Composable\nprivate fun SectionVectorBadgeIcon(')
expected_variants = {
    'Encyclopedia': 'SectionVectorBadgeIcon(R.drawable.ic_book, "REF")',
    'Reference search': 'SectionVectorBadgeIcon(R.drawable.ic_book, overlayIcon = R.drawable.ic_search)',
    'How to read encyclopedia entries': 'SectionVectorBadgeIcon(R.drawable.ic_book, overlayIcon = R.drawable.ic_question)',
    'Capability requirement resolver': 'SectionVectorBadgeIcon(R.drawable.ic_registry, overlayIcon = R.drawable.ic_search)',
    'Requirement evaluation summary': 'SectionVectorBadgeIcon(R.drawable.ic_registry, overlayIcon = R.drawable.ic_check)',
    'Vulkan Profiles and custom minimums': 'SectionVectorBadgeIcon(R.drawable.ic_profile, "VP")',
    'Custom minimum builder': 'SectionVectorBadgeIcon(R.drawable.ic_profile, overlayIcon = R.drawable.ic_add)',
    'Saved minimum profiles': 'SectionVectorBadgeIcon(R.drawable.ic_profile, overlayIcon = R.drawable.ic_save)',
    'Current custom evaluation': 'SectionVectorBadgeIcon(R.drawable.ic_profile, overlayIcon = R.drawable.ic_check)',
    'Dependency graph explorer': 'SectionVectorBadgeIcon(R.drawable.ic_graph, overlayIcon = R.drawable.ic_search)',
    'Graph overview': 'SectionVectorBadgeIcon(R.drawable.ic_graph, overlayIcon = R.drawable.ic_quick_access_grid)',
    'Interactive dependency map': 'SectionVectorBadgeIcon(R.drawable.ic_graph, overlayIcon = R.drawable.ic_compass)',
    'Collection integrity score': 'SectionVectorBadgeIcon(R.drawable.ic_shield, overlayIcon = R.drawable.ic_check)',
    'Scoring method': 'SectionVectorBadgeIcon(R.drawable.ic_shield, overlayIcon = R.drawable.ic_evidence)',
    'What this score does not mean': 'SectionVectorBadgeIcon(R.drawable.ic_shield, overlayIcon = R.drawable.ic_question)',
    'Vulkan active self-tests': 'SectionVectorBadgeIcon(R.drawable.ic_test, "RUN")',
    'Test result summary': 'SectionVectorBadgeIcon(R.drawable.ic_self_test, "SUM")',
    'Result semantics': 'SectionVectorBadgeIcon(R.drawable.ic_test, overlayIcon = R.drawable.ic_question)',
}
seen = {}
for title, variant in expected_variants.items():
    line = f'title.equals("{title}", true) -> {variant}'
    need(line in section_icons, f'unique semantic section-icon variant missing: {title}')
    if variant in seen:
        errors.append(f'duplicate visible semantic icon treatment: {seen[variant]} and {title}')
    seen[variant] = title
need('R.drawable.ic_info' not in '\n'.join(line for line in section_icons.splitlines() if any(title in line for title in expected_variants)), 'requested Analysis/Encyclopedia headings still use generic info icon')

icon_map = block(main, 'private fun capabilitySectionIcon(', '@Composable\nprivate fun preferExpandedTextLayout(')
for title in expected_variants:
    need(title in icon_map, f'fallback semantic icon mapping missing: {title}')

need('UI_MODE_TYPE_TELEVISION' not in block(main, 'private fun Modifier.tvRemoteLazyListNavigation(', '@Composable\nprivate fun Modifier.tvRemoteLazyGridNavigation('), '3.0.8 hardware D-pad repair regressed')
need('SystemDriverVendorBadge(reportRow.vendorId)' in database, '3.0.8 Database vendor badge regressed')
need('targetState = selectedCount' in main and 'targetState = remainingCount' in main, '3.0.8 Turnip selected/remaining motion regressed')
need('LocalValidatedNetwork.current' not in block(main, 'private fun LazyListScope.analysisWorkspaceItems(', '@Composable\nprivate fun ChevronAffordance('), '3.0.6 non-composable CompositionLocal regression reintroduced')
need('event.nativeKeyCode' not in main, '3.0.4 nativeKeyCode receiver regression reintroduced')
need('// ' not in main and '/*' not in main, 'source-code comment rule violated in MainActivity.kt')

protected = {
    'app/src/main/AndroidManifest.xml': 'f89dab96d5c123cecb227d6a44f4deef5b8b92d2688e46fb55df9bd1c54f18a8',
    'app/src/main/cpp/vulkanscope.cpp': 'fa2d5aea65b493138aa7c0cdbfa25bd715fa6e2265daa4c5c6b70b777e62f3ff',
    'app/src/main/java/com/efishell/vulkanscope/AdvancedAnalysis.kt': '4bb7d65a1206873f0629792d3012139dcf3247157622e8200e40595104e62904',
    'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt': '6da56de2847870fbe2ad1777c951f405fafb3859b2552e3d60857686bc477544',
    'app/src/main/java/com/efishell/vulkanscope/VulkanDependencyGraph.kt': '6ca8bab0c89a28b322ccb449f8e2314f13597488d69fec6c1a263340c030d71d',
    'registry/registry_lock.json': '79c46ebb498997b31c56b3171296ff672d229c8434a31ac77c8a17f86e4f9d0a',
    'registry/generated/registry_query_manifest.json': '60136afc325fef0dfd1ea6bfaf57da6ae61096b96a241dd126ad0fdecc3f6d00',
}
for rel, expected in protected.items():
    need(sha(rel) == expected, f'protected predecessor-equivalent file drift: {rel}')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 3.0.9 Database time/driver/semantic-icon verifier')
