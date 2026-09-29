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

def block(text, start, end):
    a = text.find(start)
    b = text.find(end, a + len(start)) if a >= 0 else -1
    need(a >= 0 and b > a, f'missing block: {start}')
    return text[a:b] if a >= 0 and b > a else ''

def sha(rel):
    p = root / rel
    return hashlib.sha256(p.read_bytes()).hexdigest() if p.is_file() else ''

main = read('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
gradle = read('app/build.gradle.kts')
rules = read('rules/PROJECT_RULES.md')
audit = read('rules/3.0.7_DATABASE_FILE_MANAGER_QUALITY_TESTS_AUDIT.md')
changelog = read('changelog.md')

declared_files = [line.strip() for line in read('files.txt').splitlines() if line.strip()]
actual_files = sorted(path.relative_to(root).as_posix() for path in root.rglob('*') if path.is_file())
need(declared_files == actual_files, 'files.txt strict census mismatch')

need('versionCode = 3007' in gradle and 'versionName = "3.0.7"' in gradle, '3.0.7 release identity missing')
need(changelog.startswith('## 3.0.7\n'), '3.0.7 changelog entry missing or not first')
need('## Release 3.0.7 Database vendor identity, animated file-manager search, auditable Quality and self-test result requirements' in rules, '3.0.7 project rules missing')
need('# VulkanScope 3.0.7 Database, File Manager, Quality and Self-Test UI Audit' in audit, '3.0.7 audit heading missing')

workspace = block(main, 'private fun LazyListScope.analysisWorkspaceItems(', '@Composable\nprivate fun ChevronAffordance(')
database = block(workspace, '        9 -> {', '        10 -> {')
quality = block(workspace, '        12 -> {', '        13 -> {')
tests_start = workspace.rfind('        else -> {')
need(tests_start >= 0, 'missing self-test branch')
tests = workspace[tests_start:] if tests_start >= 0 else ''
turnip_manager = block(main, 'private fun TurnipFileManagerDialog(', '@Composable\nprivate fun TurnipFileManagerSelectionSummary')
shared_manager = block(main, 'private fun SharedStorageBrowserDialog(', '@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun SharedStorageBrowserListing')

need('VendorLogo(' in database and 'vendorIdFromDisplay(reportRow.vendorId)' in database, 'Database report cards do not use explicit vendor-id GPU artwork')
need('R.drawable.ic_action_database' not in database, 'generic Database icon remains in report-list card presentation')
need('private fun vendorIdFromDisplay(value: String?): Long?' in main, 'existing explicit vendor-id parser missing')
need('R.drawable.gpu_vendor_unknown' in main, 'unknown GPU vendor fallback asset mapping missing')

for label, section in [('Turnip', turnip_manager), ('shared-storage', shared_manager)]:
    need('animateContentSize(animationSpec = tween(240' in section, f'{label} search container is not size-animated')
    need('expandHorizontally(' in section and 'shrinkHorizontally(' in section, f'{label} search does not use bounded horizontal expand/shrink motion')
    need('targetState = searchExpanded' in section, f'{label} search state is not animated')
    need('FileManagerOptionsChooser(' in section, f'{label} View & sort control missing')
    need('R.drawable.ic_search' in section and 'R.drawable.ic_close' in section, f'{label} animated search action states missing')
need(main.count('animateContentSize(animationSpec = tween(220') >= 8, 'folder/file cards are not broadly protected by bounded content-size animation')
need('import androidx.compose.animation.animateContentSize' in main, 'animateContentSize import missing')
need('import androidx.compose.animation.expandHorizontally' in main and 'import androidx.compose.animation.shrinkHorizontally' in main, 'horizontal animation imports missing')

need('private data class HeuristicDiagnosticCheck(' in main, 'structured Quality check model missing')
for token in ['"Top-level collection error",\n            35', '"Queue-family enumeration safety",\n            15', '"Memory-heap enumeration safety",\n            10', '"Memory-type enumeration safety",\n            10', '"Surface queue-family enumeration safety",\n            10', '"Surface-format enumeration safety",\n            10', '"Device-extension enumeration",\n            10', '"Second Surface-format query",\n            5']:
    need(token in main, f'Quality fixed deduction contract missing: {token.split(",")[0]}')
need('val deductedPoints = checks.filter { it.triggered }.sumOf { it.deduction }' in main, 'Quality deductions are not additive from triggered checks')
need('val score = (100 - deductedPoints).coerceAtLeast(0)' in main, 'Quality score no longer starts at 100 with a zero floor')
for token in ['Collection integrity score', 'Starting score', 'Scoring method', '95–100', '80–94', '60–79', '0–59', 'Observed evidence', 'What this score does not mean']:
    need(token in quality, f'Quality transparency UI missing: {token}')
need('It does not use GPU model, vendor, feature count, benchmark data or market ranking.' in quality, 'Quality non-ranking input semantics missing')
need('not Vulkan® conformance' in quality and 'driver certification' in quality, 'Quality non-conformance semantics missing')

for token in ['Vulkan active self-tests', 'Execution scope', 'PASS means', 'FAIL means', 'UNAVAILABLE means', 'Test result summary', '"PASS" to passCount.toString()', '"FAIL" to failCount.toString()', '"UNAVAILABLE" to unavailableCount.toString()', '"TOTAL" to testRows.size.toString()', 'Vulkan result', 'Result semantics']:
    need(token in tests, f'detailed self-test UI missing: {token}')
need('do not contribute to the Collection integrity score' in tests, 'self-test/Quality separation missing')
need('Capability evidence is not rewritten by this result.' in tests, 'FAIL capability-semantics guard missing')

need('val networkAvailable = LocalValidatedNetwork.current' in main, '3.0.6 composable network-state repair regressed')
need('LocalValidatedNetwork.current' not in workspace, '3.0.6 non-composable CompositionLocal regression reintroduced')
need('event.nativeKeyCode' not in main and main.count('event.key.nativeKeyCode') == 7, '3.0.4 TV nativeKeyCode repair regressed')
need('listOf("Database list", "Report ID")' in database, '3.0.5 Database mode split regressed')
need('addQueryParameter("limit", "50")' in main and 'take(200)' in main, '3.0.5 Database bounds regressed')
need('color = VulkanBlack' in turnip_manager and 'color = VulkanBlack' in shared_manager, 'full-screen AMOLED file-manager background regressed')
need('onDismissRequest = { }' in main, 'X-only View & sort dismissal regressed')

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
print('PASS VulkanScope 3.0.7 Database/file-manager/Quality/self-test verifier')
