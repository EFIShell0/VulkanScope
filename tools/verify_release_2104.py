#!/usr/bin/env python3
import argparse
import hashlib
import re
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
    p = root / rel
    need(p.is_file(), f'missing file: {rel}')
    return p.read_text(encoding='utf-8') if p.is_file() else ''

def block(text, start, end):
    a = text.find(start)
    b = text.find(end, a + len(start)) if a >= 0 else -1
    need(a >= 0 and b > a, f'missing block: {start}')
    return text[a:b] if a >= 0 and b > a else ''

main = read('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
gradle = read('app/build.gradle.kts')
rules = read('rules/PROJECT_RULES.md')
audit = read('rules/2.1.4_KOTLIN_COMPILE_RESTORATION_AUDIT.md')
changelog = read('changelog.md')

need('versionCode = 2104' in gradle and 'versionName = "2.1.4"' in gradle, '2.1.4 release identity missing')
need('import androidx.compose.foundation.lazy.stickyHeader' not in main, 'invalid top-level stickyHeader import returned')
for token in [
    'private data class VideoProfileEvidence(',
    'private data class VulkanVideoEvidence(',
    'private fun parseVulkanVideoEvidence(',
    'private fun videoEvidenceState(',
    'private fun videoPropertyLabel(',
]:
    need(token in main, f'Vulkan Video compile helper missing: {token}')
queues = block(main, 'private fun QueuesPage(', '\n@Composable\nprivate fun VideoEvidenceStateBadge')
need('CapabilityKeyValue("Video codec query", queueVideoCodecQueryState(queue))' in queues, 'queue video-codec query evidence was dropped')
need('CapabilityKeyValue("Video codec operations", videoCodecOperationFlags(queue.videoCodecOperations))' in queues, 'queue video-codec operation evidence was dropped')
need('// ' not in main and '/*' not in main, 'source-code comment rule violated in MainActivity.kt')
need('private const val COLLECTION_PAGE_SIZE = 25' in main, 'shared page size is not 25')

logo = root / 'app/src/main/res/drawable-nodpi/vulkanscope_logo_horizontal_balanced.png'
need(logo.is_file(), 'balanced horizontal logo missing')
if logo.is_file():
    need(hashlib.sha256(logo.read_bytes()).hexdigest() == 'a764480b2789f748de0ba8a772a7899b18edf33982347300d969d68f41ca9d82', 'balanced horizontal logo drift')
need(main.count('R.drawable.vulkanscope_logo_horizontal_balanced') >= 2, 'balanced horizontal logo is not used by header/opening surfaces')

header = block(main, 'private fun AppHeader(', '\n@Composable\nprivate fun DisplayPage')
for token in [
    '.fillMaxWidth()',
    'VulkanSurfaceRaised.copy(alpha = 0.92f)',
    '.statusBarsPadding()',
    'buttonSize = 42.dp',
    'buttonSize = 50.dp',
    'R.drawable.vulkanscope_logo_horizontal_balanced',
]:
    need(token in header, f'Telegram-style header contract missing: {token}')
need('shape = RoundedCornerShape(28.dp)' not in header, 'rounded floating outer header returned')
need('shape = CircleShape' in block(main, 'private fun HeaderActionButton(', '\n@Composable\nprivate fun AnimatedHeaderActionButton'), 'header actions are not true circles')

search = block(main, 'private fun ExpressiveSearchField(', '\n@Composable\nprivate fun ExpressiveAssistChip')
need('val textOverflows =' in search, 'search overflow measurement missing')
need('if (textOverflows && !focused)' in search, 'search fade is not gated to inactive overflow')
need('Alignment.CenterStart' not in search, 'search leading-character fade returned')
need('.align(Alignment.CenterEnd)' in search, 'search trailing overflow fade missing')

pager = block(main, 'private fun CollectionPager(', '\n\nprivate fun LazyListScope.stickyCollectionPager')
need('(candidate.toIntOrNull() ?: 0) in 1..pageCount' in pager, 'nonexistent page numbers are still accepted')
on_value = re.search(r'onValueChange = \{ value ->(.*?)\n\s*\},\n\s*modifier =', pager, re.S)
need(on_value is not None, 'pager input block missing')
if on_value:
    need('onPageChange' not in on_value.group(1), 'pager still navigates per keystroke')
need('ImeAction.Done' in pager and 'commitPageSelection()' in pager, 'pager deferred commit missing')
sticky = block(main, 'private fun LazyListScope.stickyCollectionPager(', '\n@Composable\nprivate fun ScrollBoundaryIndicators')
for token in ['stickyHeader(key = pagerKey)', 'AnimatedContent(', 'slideInHorizontally', 'slideOutHorizontally']:
    need(token in sticky, f'sticky/smooth pager behavior missing: {token}')

for token in [
    'private fun FeaturesPage(',
    'private fun FormatsPage(',
    'private fun PropertiesPage(',
    'private fun ExtensionsPage(',
    'private fun SurfaceFormatsPage(',
    'private fun ProfilesPage(',
    'private fun EncyclopediaPage(',
    'private fun QueuesPage(',
    'private fun VulkanVideoPage(',
    'private fun LazyListScope.analysisWorkspaceItems(',
]:
    start = main.find(token)
    need(start >= 0, f'pagination target missing: {token}')
    if start >= 0:
        next_fun = main.find('\n@Composable\nprivate fun ', start + len(token))
        if token.startswith('private fun LazyListScope.analysisWorkspaceItems'):
            next_fun = main.find('\nprivate fun ', start + len(token))
        section = main[start:next_fun if next_fun > start else min(len(main), start + 18000)]
        need('stickyCollectionPager(' in section or 'analysisPager(' in section, f'25-item sticky pagination missing from {token}')

vulkan_page = block(main, 'private fun VulkanLazyPage(', '\n@Composable\nprivate fun SoftScrollIntersectionShadows')
need('SoftScrollIntersectionShadows' not in vulkan_page, 'main-page soft intersection fades returned')

nav = block(main, 'private fun CompactBottomNavigationBar(', '\n@Composable\nprivate fun ExploreDestinationTile')
for token in ['calculateLeftPadding(layoutDirection)', 'calculateRightPadding(layoutDirection)', '16.dp + startSystemInset', '16.dp + endSystemInset']:
    need(token in nav, f'landscape system navigation inset missing: {token}')

for token in [
    'private enum class FileManagerSortMode { NAME_ASC, NAME_DESC, MODIFIED_NEWEST, MODIFIED_OLDEST, CREATED_NEWEST, CREATED_OLDEST }',
    'java.nio.file.attribute.BasicFileAttributes::class.java',
    'sortFileManagerChildren(children, sortMode)',
    'private fun FileManagerSortChooser(',
    'Modified · newest first',
    'Modified · oldest first',
    'Created · newest first',
    'Created · oldest first',
    'turnip_file_manager_sort_mode',
]:
    need(token in main, f'file-manager sorting contract missing: {token}')
need('scanTurnipFileManagerDirectory(root, target, requestedSort)' in main, 'Turnip scanner does not receive selected sort mode')
need('scanSharedStorageDirectory(scanRoot, target, request.allowedExtensions, request.mode == SharedStorageBrowserMode.IMPORT, sortMode)' in main, 'shared import/export scanner does not receive selected sort mode')
need('pagerKey = "analysis-saved-profiles"' in main and 'analysis-custom-minimums' in main, 'independent paging missing for saved/custom minimum analysis sections')

need(main.count('VulkanAccentSoft.copy(alpha = 0.46f)') >= 18, 'dialog red-outline family is not applied broadly enough')
need(main.count('modifier = Modifier.border(1.dp, VulkanAccentSoft.copy(alpha = 0.46f), MaterialTheme.shapes.extraLarge)') >= 10, 'Material AlertDialog red outlines missing')
for custom_marker in ['private fun ExpressiveDetailDialog(', 'private fun LibraryLicenseDialog(', 'private fun TurnipFileManagerDialog(', 'private fun SharedStorageBrowserDialog(']:
    start = main.find(custom_marker)
    need(start >= 0, f'custom dialog missing: {custom_marker}')
    if start >= 0:
        section = main[start:start+8000]
        need('border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.46f))' in section, f'custom dialog red outline missing: {custom_marker}')

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
    p = root / rel
    need(p.is_file(), f'correctness baseline file missing: {rel}')
    if p.is_file():
        need(hashlib.sha256(p.read_bytes()).hexdigest() == expected, f'correctness baseline byte drift: {rel}')

need('Release 2.1.4 Kotlin compile restoration requirements' in rules, 'PROJECT_RULES missing 2.1.4 contract')
for token in ['stickyHeader', 'VideoProfileEvidence', 'parseVulkanVideoEvidence', 'Video codec query']:
    need(token in audit, f'2.1.4 audit missing: {token}')
need(changelog.startswith('## 2.1.4\n'), '2.1.4 changelog entry missing or not current')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 2.1.4 Telegram header/paging/sort/dialog contract')
