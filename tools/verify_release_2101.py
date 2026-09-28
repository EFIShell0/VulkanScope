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

app_gradle = read('app/build.gradle.kts')
root_gradle = read('build.gradle.kts')
main = read('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
service = read('app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt')
manifest = read('app/src/main/AndroidManifest.xml')
rules = read('rules/PROJECT_RULES.md')
changelog = read('changelog.md')
audit2100 = read('rules/2.1.0_UI_PAGINATION_TRANSFER_DATABASE_AUDIT.md')
audit2101 = read('rules/2.1.1_COLLECTION_PAGER_COMPILE_FIX_AUDIT.md')
wrapper = read('gradle/wrapper/gradle-wrapper.properties')

need('versionCode = 2101' in app_gradle and 'versionName = "2.1.1"' in app_gradle, '2.1.1 release identity missing')
need('com.android.application") version "9.4.1"' in root_gradle, 'AGP 9.4.1 drifted')
need('gradle-9.7.1-bin.zip' in wrapper, 'Gradle wrapper drifted from 9.7.1')
need('compileSdk = 37' in app_gradle and 'targetSdk = 37' in app_gradle, 'compile/target SDK drifted')
need('android:usesCleartextTraffic="false"' in manifest, 'cleartext transport protection missing')
need('// ' not in main and '/*' not in main and '// ' not in service and '/*' not in service, 'source-code comment rule violated in Kotlin source')

for token in [
    'val headerShape = RoundedCornerShape(26.dp)',
    '.clip(headerShape)',
    'containerColor = VulkanSurfaceRaised.copy(alpha = 0.78f)',
    'private fun SoftScrollIntersectionShadows(',
    'SoftScrollIntersectionShadows(showTopFade, showBottomFade, Modifier.fillMaxSize())',
    'VulkanBlack.copy(alpha = 0.82f)',
    'VulkanBlack.copy(alpha = 0.74f)',
]:
    need(token in main, f'translucent/soft-edge UI contract missing: {token}')

for token in [
    'private enum class TurnipFileManagerViewMode { LIST, COMPACT, DETAILS, GRID, DENSE_GRID, LARGE_GRID }',
    'private fun TurnipFileManagerViewModeChooser(',
    'contentDescription = "Change file manager layout"',
    'TurnipFileManagerViewMode.entries.forEach',
    'putString("turnip_file_manager_view_mode", mode.name)',
    'TurnipFileManagerViewMode.valueOf(prefs.getString("turnip_file_manager_view_mode"',
    'TurnipFileManagerViewMode.DENSE_GRID -> R.drawable.ic_view_grid_dense',
    'TurnipFileManagerViewMode.LARGE_GRID -> R.drawable.ic_view_tiles_large',
]:
    need(token in main, f'file-manager layout contract missing: {token}')
need((root / 'app/src/main/res/drawable/ic_view_grid_dense.xml').is_file(), 'dense-grid icon missing')
need((root / 'app/src/main/res/drawable/ic_view_tiles_large.xml').is_file(), 'large-tiles icon missing')

need('private const val COLLECTION_PAGE_SIZE = 50' in main, '50-item collection page size missing')
need('private fun CollectionPager(' in main, 'shared collection pager missing')
for marker in ['private fun FeaturesPage(', 'private fun FormatsPage(', 'private fun PropertiesPage(', 'private fun ExtensionsPage(']:
    start = main.find(marker)
    need(start >= 0, f'page block missing: {marker}')
    if start >= 0:
        next_fun = main.find('\n@Composable\nprivate fun ', start + len(marker))
        if next_fun < 0:
            next_fun = len(main)
        block = main[start:next_fun]
        need('COLLECTION_PAGE_SIZE' in block, f'50-item paging not applied in {marker}')
        need('CollectionPager(' in block, f'pager UI missing in {marker}')
need('pagedLimitSource' in main and 'pagedPropertySource' in main, 'combined Properties/Limits paging contract missing')

for token in [
    'val assetSizeBytes: Long?',
    'assetSizeBytes = selected.optLong("size").takeIf { it > 0L }',
    'val transferTotal = contentLength ?: expectedSize',
    'if (expectedSize != null && total != expectedSize)',
    'private fun ExpressiveDeterminateDownloadProgress(',
    '.fillMaxWidth(animatedProgress)',
    'ExpressiveDeterminateDownloadProgress(progressFraction, Modifier.fillMaxWidth())',
]:
    need(token in main, f'determinate update-progress contract missing: {token}')

for token in [
    'Text("Report ID"',
    'copyEvidenceText(context, "VulkanScope report ID", reportId)',
    'contentDescription = "Copy report ID"',
    '"Open report"',
    'R.drawable.ic_open_external',
    'uriHandler.openUri(databaseReportUrl(reportId))',
]:
    need(token in main, f'Database result-action contract missing: {token}')
need('Regex("[a-f0-9]{64}")' in main and 'Database report id must be a lowercase 64-character SHA-256 id' in main, 'validated 64-hex Database report ID guard missing')
need('private fun databaseReportUrl(id: String): String' in main, 'existing public report URL helper missing')

immutable = {
    'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt': '6da56de2847870fbe2ad1777c951f405fafb3859b2552e3d60857686bc477544',
    'app/src/main/AndroidManifest.xml': 'f89dab96d5c123cecb227d6a44f4deef5b8b92d2688e46fb55df9bd1c54f18a8',
    'app/src/main/cpp/vulkanscope.cpp': 'fa2d5aea65b493138aa7c0cdbfa25bd715fa6e2265daa4c5c6b70b777e62f3ff',
    'app/src/main/cpp/registry_query_catalog.h': 'bef2bbfa855eafdd56934409c4dd541dd4273ecf78ee3db5aa98646b16c5d299',
    'registry/registry_lock.json': '79c46ebb498997b31c56b3171296ff672d229c8434a31ac77c8a17f86e4f9d0a',
    'registry/generated/registry_query_manifest.json': '60136afc325fef0dfd1ea6bfaf57da6ae61096b96a241dd126ad0fdecc3f6d00',
    'app/src/main/java/com/efishell/vulkanscope/AdvancedAnalysis.kt': '6ba807d6e5ab99f780c49fa87ae6e075e8d3cb6833cf060890e0ab9a80277fcb',
    'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt': 'dec891e7fe1251deb3911d4b9ec394d8b7b8468bacd8a19953733aabd3a94629',
}
for rel, expected in immutable.items():
    path = root / rel
    need(path.is_file(), f'correctness-baseline file missing: {rel}')
    if path.is_file():
        need(hashlib.sha256(path.read_bytes()).hexdigest() == expected, f'correctness-baseline byte drift: {rel}')

need('Release 2.1.0 translucent UI' in rules, 'PROJECT_RULES missing retained 2.1.0 contract')
need('Release 2.1.1 collection pager Kotlin compile-fix requirements' in rules, 'PROJECT_RULES missing 2.1.1 compile-fix contract')
for token in ['COLLECTION_PAGE_SIZE', 'presentation-only', 'asset-size mismatch', 'Open report', '64-hex']:
    need(token in audit2100, f'2.1.0 audit missing retained contract evidence: {token}')

pager_lines = [line.strip() for line in main.splitlines() if 'CollectionPager(' in line and not line.strip().startswith('private fun CollectionPager')]
need(len(pager_lines) == 4, f'expected exactly four production CollectionPager calls, found {len(pager_lines)}')
for line in pager_lines:
    need('totalItems = ' in line and 'currentPage = page' in line and 'onPageChange = { page = it }' in line, f'unsafe CollectionPager invocation: {line}')
    need(') { page = it }' not in line, f'trailing-lambda CollectionPager compile regression returned: {line}')
for token in ['No value passed for parameter', 'onPageChange', 'trailing-lambda', 'presentation-only', '706 files']:
    need(token in audit2101, f'2.1.1 audit missing compile-regression evidence: {token}')
need('every production `CollectionPager` call' in rules, '2.1.1 named onPageChange rule missing')
need(changelog.startswith('## 2.1.1\n'), '2.1.1 changelog entry missing or not current')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 2.1.1 collection-pager compile-fix contract')
