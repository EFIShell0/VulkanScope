from pathlib import Path
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parents[1]
k = root / 'app/src/main/java/com/efishell/vulkanscope'
layout = (k/'AdaptiveLazyLayout.kt').read_text(encoding='utf-8')
chrome = (k/'UiChrome.kt').read_text(encoding='utf-8')
settings = (k/'UiReports.kt').read_text(encoding='utf-8')
surface = (k/'UiDeviceScreens.kt').read_text(encoding='utf-8')
section = (k/'SectionDestinationGrid.kt').read_text(encoding='utf-8')
policy = (k/'PinnedPagerPolicy.kt').read_text(encoding='utf-8')
models = (k/'CoreModels.kt').read_text(encoding='utf-8')

# Verify the source registers pagers during DSL construction, not while an
# off-screen LazyColumn row happens to be composed.
assert 'val sourceItems = AdaptiveLazyListRecorder().also { content(it) }' in chrome
assert 'val pagerSource = sourceItems.collectionPagers.lastOrNull()' in chrome
assert 'sourceItems.renderedPagerIndex(spec.key, useTwoPaneLayout)' in chrome
assert 'registerPager(RecordedCollectionPager(pagerKey, totalItems, currentPage, pageSize, onPageChange))' in chrome
assert 'LaunchedEffect(pagerIndex, pagerHeightPx, layoutItemCount' not in chrome
assert 'LocalCollectionPagerRegistrationReporter' not in chrome
assert 'LocalCollectionPagerRegistrationReporter' not in models
assert 'val pagerRegistrations = remember(listState)' not in chrome
assert 'val pagerRegistration = pagerSource?.let' in chrome
assert 'val pinnedPager = pagerRegistration?.takeIf' in chrome
assert 'onPageChange = stablePagerCallback' in chrome
assert 'pagerCallback.value?.invoke(target)' in chrome
assert 'visiblePagerOffsetPx = pagerRow?.offset' in chrome
assert 'val stablePagerPinBoundaryPx = maxOf(headerBoundaryPx, maximumHeaderBoundaryPx)' in chrome
assert 'val pagerBlurFraction by animateFloatAsState(' in chrome
assert 'blurVisibility = pagerBlurFraction' in chrome
assert 'LocalCollectionPagerPinned provides pagerIsPinned' in chrome
assert 'PinnedPagerGlassSurface(' in chrome
assert 'headerVisible' not in policy

# The lazy-list row MUST have an actual pager whenever the pinned overlay
# has not taken over. An unconditional spacer was the old black-gap bug.
assert 'LocalCollectionPagerHandoffFraction.current' in chrome
assert 'val pagerHandoffFraction by animateFloatAsState(' in chrome
inline = chrome.split('internal fun LazyListScope.stickyCollectionPager(', 1)[1].split('\n}', 1)[0]
assert 'CollectionPager(' in inline and 'graphicsLayer { alpha = (1f - handoff).coerceIn(0f, 1f) }' in inline
assert 'reportHeight(pagerKey, size.height)' in inline
assert 'shouldPinCollectionPager(' in chrome
assert 'shouldPinCollectionPager(' in policy
assert 'if (handoffPager != null)' in chrome
assert 'pagerHandoffFraction' in chrome

# Landscape and portrait projection must both use the same full-width key
# contract and production Kotlin mapping function.
assert 'projectedPagerRowIndex(' in layout and 'internal fun projectedPagerRowIndex(' in policy
assert 'isFullWidthLazyItem(key)' in layout
assert 'key == "surface-destinations-fullwidth"' in layout
assert 'key == "settings-destinations-fullwidth"' in layout
assert 'item(key = interval.stableKey ?: "structural-${interval.start}")' in layout
assert 'item(key = "settings-destinations-fullwidth")' in (k/'UiSettings.kt').read_text(encoding='utf-8')
assert 'item(key = "surface-destinations-fullwidth")' in surface

# Shared Settings/Surface grid, same max width, same gutter, same centered tail.
assert 'SectionDestinationGrid(SettingsSection.entries.toList())' in settings
assert 'SectionDestinationGrid(SurfaceSection.entries.toList())' in surface
assert 'Modifier.widthIn(max = 1000.dp).fillMaxWidth()' in section
assert 'if (twoPane && rowSections.size == 1) Spacer(Modifier.weight(0.5f))' in section
assert 'Box(Modifier.weight(1f))' in section
assert 'AnimatedSurfaceSectionCard' in surface
assert 'AnimatedSettingsSectionCard' in settings
assert 'SurfaceSection.entries.forEachIndexed' not in surface

for icon in ('ic_chrome_visibility','ic_chrome_auto_hide','ic_chrome_header','ic_chrome_navigation'):
    ET.parse(root/'app/src/main/res/drawable'/f'{icon}.xml')
assert (root/'app/src/main/res/drawable/ic_chrome_visibility.xml').read_bytes() != (root/'app/src/main/res/drawable/ic_chrome_auto_hide.xml').read_bytes()
assert 'R.drawable.ic_trademark_notice' not in settings
assert not (root/'app/src/main/res/drawable/ic_trademark_notice.xml').exists()
print('PASS 4.0.15 pager registration lifecycle, source-to-row index mapping, shared Settings/Surface grid and blur controls (static)')
