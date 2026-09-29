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

main = read('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
advanced = read('app/src/main/java/com/efishell/vulkanscope/AdvancedAnalysis.kt')
graph = read('app/src/main/java/com/efishell/vulkanscope/VulkanDependencyGraph.kt')
gradle = read('app/build.gradle.kts')
rules = read('rules/PROJECT_RULES.md')
audit = read('rules/3.0.6_ANALYSIS_COMPOSITIONLOCAL_COMPILE_AUDIT.md')
changelog = read('changelog.md')

declared_files = [line.strip() for line in read('files.txt').splitlines() if line.strip()]
actual_files = sorted(path.relative_to(root).as_posix() for path in root.rglob('*') if path.is_file())
need(declared_files == actual_files, 'files.txt strict census mismatch')

need('versionCode = 3006' in gradle and 'versionName = "3.0.6"' in gradle, '3.0.6 release identity missing')
need(changelog.startswith('## 3.0.6\n'), '3.0.6 changelog entry missing or not first')
need('## Release 3.0.6 Analysis CompositionLocal compile restoration requirements' in rules, '3.0.6 project rules missing')
need('# VulkanScope 3.0.6 Analysis CompositionLocal Compile Audit' in audit, '3.0.6 audit heading missing')

analysis_page = block(main, '@Composable\nprivate fun AnalysisPage', '@Composable\nprivate fun VulkanPage(')
need('val networkAvailable = LocalValidatedNetwork.current' in analysis_page, 'validated-network CompositionLocal is not read from AnalysisPage composable scope')
need('analysisWorkspaceItems(analysisModel, report, device, networkAvailable)' in analysis_page, 'validated-network Boolean is not passed into lazy Analysis builder')
need(analysis_page.find('val networkAvailable = LocalValidatedNetwork.current') < analysis_page.find('VulkanLazyPage(verticalSpacing = 12.dp)'), 'validated-network state is not acquired before lazy builder declaration')

workspace = block(main, 'private fun LazyListScope.analysisWorkspaceItems(', '@Composable\nprivate fun ChevronAffordance(')
need('device: DeviceReport?, networkAvailable: Boolean)' in workspace, 'lazy Analysis builder does not accept ordinary validated-network data')
need('LocalValidatedNetwork.current' not in workspace, 'lazy Analysis builder still invokes CompositionLocal.current')
need('enabled = networkAvailable' in workspace, 'Report ID input no longer uses validated-network gating')
need('enabled = networkAvailable && !state.databaseListLoading' in workspace, 'Database list actions no longer use validated-network gating')
need('enabled = networkAvailable && !state.databaseLoading && state.databaseReportId.length == 64' in workspace, 'exact Report ID fetch gating changed')
need('networkAvailable = true' not in workspace and 'networkAvailable = false' not in workspace, 'validated-network state is forced inside lazy builder')

need('listOf("Database list", "Report ID")' in workspace, '3.0.5 Database mode selector regressed')
need('private suspend fun fetchDatabaseReportPage(' in main, '3.0.5 Database list fetch missing')
need('addPathSegments("v1/reports")' in main and 'addQueryParameter("limit", "50")' in main, '3.0.5 bounded Database list request regressed')
need('take(200)' in main, '3.0.5 Database in-app bound regressed')
need('internal fun formatAnalysisElapsedTime(elapsedMs: Long): String' in advanced and '"%.3f s (%d ms)"' in advanced, '3.0.5 diagnostic seconds-plus-milliseconds formatter regressed')
need('CapabilitySectionCard("Diagnostic collection")' in main and 'Probe and scheduler timing' in main, '3.0.5 Diagnostic collection layout regressed')
need('FileManagerOptionsChooser(' in main and 'contentDescription = "Search files and folders"' in main, '3.0.5 file-manager toolbar controls regressed')
need('color = VulkanBlack' in main and 'shape = RoundedCornerShape(0.dp)' in main, '3.0.5 full-screen AMOLED manager treatment regressed')
need('event.nativeKeyCode' not in main and main.count('event.key.nativeKeyCode') == 7, '3.0.4 TV nativeKeyCode repair regressed')
need('import androidx.compose.foundation.layout.weight' not in graph and graph.count('Modifier.weight(1f)') == 3, '3.0.4 graph weight repair regressed')
for token in [
    'MinimumProfileConfirmation(MinimumProfileConfirmationKind.SAVE',
    'MinimumProfileConfirmation(MinimumProfileConfirmationKind.LOAD',
    'MinimumProfileConfirmation(MinimumProfileConfirmationKind.DELETE',
    'private fun Modifier.tvRemoteLazyListNavigation(state: LazyListState): Modifier',
    'private fun Modifier.tvRemoteLazyGridNavigation(state: LazyGridState): Modifier',
    'delay(3000L)',
]:
    need(token in main, f'predecessor behavior anchor missing: {token}')

need('// ' not in main and '/*' not in main, 'source-code comment rule violated in MainActivity.kt')
need('// ' not in advanced and '/*' not in advanced, 'source-code comment rule violated in AdvancedAnalysis.kt')

protected = {
    'app/src/main/AndroidManifest.xml': 'f89dab96d5c123cecb227d6a44f4deef5b8b92d2688e46fb55df9bd1c54f18a8',
    'app/src/main/cpp/vulkanscope.cpp': 'fa2d5aea65b493138aa7c0cdbfa25bd715fa6e2265daa4c5c6b70b777e62f3ff',
    'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt': '6da56de2847870fbe2ad1777c951f405fafb3859b2552e3d60857686bc477544',
    'app/src/main/java/com/efishell/vulkanscope/VulkanDependencyGraph.kt': '6ca8bab0c89a28b322ccb449f8e2314f13597488d69fec6c1a263340c030d71d',
    'registry/registry_lock.json': '79c46ebb498997b31c56b3171296ff672d229c8434a31ac77c8a17f86e4f9d0a',
    'registry/generated/registry_query_manifest.json': '60136afc325fef0dfd1ea6bfaf57da6ae61096b96a241dd126ad0fdecc3f6d00',
}
for rel, expected in protected.items():
    path = root / rel
    need(path.is_file(), f'missing protected file: {rel}')
    if path.is_file():
        need(hashlib.sha256(path.read_bytes()).hexdigest() == expected, f'protected file drift: {rel}')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 3.0.6 Analysis CompositionLocal compile restoration verifier')
