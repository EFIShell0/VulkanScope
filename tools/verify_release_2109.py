#!/usr/bin/env python3
import argparse
import hashlib
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
gradle = read('app/build.gradle.kts')
rules = read('rules/PROJECT_RULES.md')
audit = read('rules/2.1.9_PINNED_PAGER_OVERLAY_AUDIT.md')
changelog = read('changelog.md')

if not args.skip_version:
    need('versionCode = 2109' in gradle and 'versionName = "2.1.9"' in gradle, '2.1.9 release identity missing')

for token in [
    'private data class CollectionPagerRegistration(',
    'val layoutItemCount: Int,',
    'private val LocalCollectionPagerRegistrationReporter',
    'val pagerRegistrations = remember(listState) { mutableStateMapOf<String, CollectionPagerRegistration>() }',
    'val headerBoundaryPx = with(density) { headerContentInset.roundToPx() }',
    '.filter { it.itemIndex >= 0 && it.heightPx > 0 && it.layoutItemCount == listState.layoutInfo.totalItemsCount }',
    'info != null -> info.offset <= headerBoundaryPx',
    'firstIndex > registration.itemIndex -> true',
    'firstIndex == registration.itemIndex -> firstOffset > 0',
    'val activeReporterKey = activePinnedPager?.let { "${it.key}@${System.identityHashCode(listState)}" }',
    'LocalCollectionPagerRegistrationReporter provides registrationReporter',
    'visible = activePinnedPager != null',
    '.padding(start = 18.dp + horizontalNavigationStartInset, end = 18.dp + horizontalNavigationEndInset)',
    '.offset(y = headerContentInset)',
    'pagerRegistrations[registration.key] = current.copy(currentPage = targetPage)',
    'modifier = Modifier.zIndex(6f).alpha(if (pinned) 0f else 1f)',
    'layoutItemCount = layoutItemCount',
    'onPageChange = { targetPage -> latestOnPageChange.value(targetPage) }',
]:
    need(token in main, f'clipping-independent pager contract missing: {token}')

lazy_page = block(main, '@Composable\nprivate fun VulkanLazyPage(', '@Composable\nprivate fun SoftScrollIntersectionShadows')
sticky = block(main, 'private fun LazyListScope.stickyCollectionPager(', '@Composable\nprivate fun ScrollBoundaryIndicators(listState:')
need('CollectionPager(' in lazy_page and 'AnimatedVisibility(' in lazy_page, 'page-level pinned pager overlay host missing')
for token in [
    'info != null -> info.offset <= headerBoundaryPx',
    'firstIndex > registration.itemIndex -> true',
    'firstIndex == registration.itemIndex -> firstOffset > 0',
    '.filter { it.itemIndex >= 0 && it.heightPx > 0 && it.layoutItemCount == listState.layoutInfo.totalItemsCount }',
    '.padding(start = 18.dp + horizontalNavigationStartInset, end = 18.dp + horizontalNavigationEndInset)',
    'reportStickyPagerState(reporterKey, true, registration.heightPx)',
]:
    need(token in lazy_page, f'page-level overlay tracking contract missing: {token}')
need('stickyHeader(key = pagerKey)' in sticky, 'normal-flow sticky pager anchor missing')
need('stickyPagerHeaderJoinOffset' not in sticky, 'predecessor child-translation pinning remains in sticky pager')
need('stickyPagerBackdropAlpha' not in sticky, 'predecessor in-item pinned backdrop remains in sticky pager')
need('.offset(y = pinnedOffset)' not in sticky, 'predecessor clipped child offset remains in sticky pager')
need('.offset(y = headerContentInset)' not in sticky, 'sticky pager content is translated inside the lazy clipping boundary')
need('reportStickyPagerState(pagerKey' not in sticky, 'sticky item still owns global pinned-lane lifetime')
need('reportStickyPagerState(reporterKey, true, registration.heightPx)' in lazy_page, 'page overlay does not own pinned-lane height')
need('renderedPinnedPager' in lazy_page and 'delay(240)' in lazy_page, 'bounded pin/unpin exit rendering missing')

for token in [
    'fun requestPageChange(targetPage: Int)',
    'focusManager.clearFocus(force = true)',
    'label = "pageNumberTransition"',
    'fun requestFilterPageChange(targetPage: Int)',
    'label = "filterPageNumberTransition"',
    'private fun Modifier.frostedHeaderBackdrop()',
    'SPIRV_TRADEMARK_DISPLAY_REGEX',
    'SPIR-V™',
    'fileManagerSortModeIcon(candidate)',
]:
    need(token in main, f'retained 2.1.7/2.1.8 contract missing: {token}')

need('// ' not in main and '/*' not in main, 'source-code comment rule violated in MainActivity.kt')

protected = {
    'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt': '6da56de2847870fbe2ad1777c951f405fafb3859b2552e3d60857686bc477544',
    'app/src/main/AndroidManifest.xml': 'f89dab96d5c123cecb227d6a44f4deef5b8b92d2688e46fb55df9bd1c54f18a8',
    'app/src/main/cpp/vulkanscope.cpp': 'fa2d5aea65b493138aa7c0cdbfa25bd715fa6e2265daa4c5c6b70b777e62f3ff',
    'registry/registry_lock.json': '79c46ebb498997b31c56b3171296ff672d229c8434a31ac77c8a17f86e4f9d0a',
}
for rel, expected in protected.items():
    path = root / rel
    need(path.is_file(), f'protected file missing: {rel}')
    if path.is_file():
        need(hashlib.sha256(path.read_bytes()).hexdigest() == expected, f'protected file drift: {rel}')

need('Release 2.1.9 clipping-independent pinned-pager overlay requirements' in rules, 'PROJECT_RULES missing 2.1.9 contract')
for token in ['390×848', '8.08-second', 'Modifier.offset', 'layoutInfo.totalItemsCount', 'portrait and landscape']:
    need(token in audit, f'2.1.9 audit missing: {token}')
need(changelog.startswith('## 2.1.9\n'), '2.1.9 changelog entry missing or not current')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 2.1.9 clipping-independent pinned-pager overlay contract')
