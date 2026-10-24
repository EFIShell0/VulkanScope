from pathlib import Path
r=Path(__file__).resolve().parents[1]
s=(r/'app/src/main/java/com/efishell/vulkanscope/UiChrome.kt').read_text()
m=(r/'app/src/main/java/com/efishell/vulkanscope/CoreModels.kt').read_text()
assert 'LocalCollectionPagerHandoffFraction = staticCompositionLocalOf { 0f }' in m
assert 'label = "collectionPagerDetachAttach"' in s
assert 'targetValue = if (pagerIsPinned) 1f else 0f' in s
assert 'durationMillis = 280' in s
assert 'LocalCollectionPagerHandoffFraction provides pagerHandoffFraction' in s
assert 'val handoff = LocalCollectionPagerHandoffFraction.current' in s
assert 'graphicsLayer { alpha = (1f - handoff).coerceIn(0f, 1f) }' in s
assert 'graphicsLayer { alpha = pagerHandoffFraction }' in s
assert 'pagerHandoffFraction > 0.001f' in s
assert 'val registration = handoffPager' in s
assert 'if (pinned) {' not in s.split('internal fun LazyListScope.stickyCollectionPager(',1)[1].split('\n}',1)[0]
assert 'pagerBlurIndependentOfPinnedPager' in s
assert 'reportHeight(pagerKey, size.height)' in s
assert 'Spacer(Modifier.fillMaxWidth().height(height))' not in s
print('PASS continuous pager detach/attach handoff with complementary alpha; regression guard only')
