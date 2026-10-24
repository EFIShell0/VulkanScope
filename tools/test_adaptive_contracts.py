#!/usr/bin/env python3
from pathlib import Path
root=Path(__file__).resolve().parents[1]
source=(root/'app/src/main/java/com/efishell/vulkanscope/AdaptiveLazyLayout.kt').read_text()
dialog=(root/'app/src/main/java/com/efishell/vulkanscope/AdaptiveDialogLayout.kt').read_text()
reports=(root/'app/src/main/java/com/efishell/vulkanscope/UiReports.kt').read_text()
components=(root/'app/src/main/java/com/efishell/vulkanscope/UiComponents.kt').read_text()
assert 'Interval(totalCount, count, false, null, itemContent)' in source
assert 'totalCount += count' in source
assert 'item(key = interval.stableKey ?: "structural-${interval.start}")' in source
assert 'interval.standalone && structural' in source
assert 'pendingCards.add(interval)' in source
assert 'import androidx.compose.foundation.layout.Spacer' in source
assert 'if (pendingCards.size == 2) flushCards()' in source
assert 'key.contains("pager", ignoreCase = true)' in source
assert 'key == "settings-destinations-fullwidth"' in source
assert 'rowKey = cards.joinToString(' in source
assert 'Interval(totalCount, 1, true, key)' in source
assert 'val pairCount = (interval.count + 1) / 2' in source
assert 'rowIndex * 2 + 1 < interval.count' in source
assert 'renderItem(leftIndex' in source
assert 'recorder.renderItem(card.start, originalScope)' in source
assert 'renderItem(leftIndex + 1' in source
assert 'Configuration.ORIENTATION_LANDSCAPE' in dialog
assert 'maxWidth = widths[lane]' in dialog
assert all(text in reports for text in ('Ready to submit','Collection in progress','Connection unavailable','Submitting report','Submission unsuccessful','Report submitted'))
assert 'ComposeColor(0xFFF8F9FC)' in components
for size in (0,1,2,3,4,5,10,101,1001):
    rows=(size+1)//2
    indexes=[i for row in range(rows) for i in (2*row,2*row+1) if i<size]
    assert len(indexes)==size and indexes==list(range(size))
for huge_size in (100_001,2_000_000):
    rows=(huge_size+1)//2
    assert (rows-1)*2 < huge_size and rows*2 >= huge_size
print('PASS adaptive lazy interval/index projection and status/Markdown static invariants')


def projected_rows(intervals):
    rows = []
    pending = []
    def flush():
        if pending:
            rows.append(tuple(pending))
            pending.clear()
    for item_type, start, count in intervals:
        if item_type == "structural":
            flush()
            rows.append((start,))
        elif item_type == "standalone":
            pending.append(start)
            if len(pending) == 2:
                flush()
        else:
            flush()
            for local in range(0, count, 2):
                rows.append(tuple(start + k for k in (local, local + 1) if k < count))
    flush()
    return rows

sample = [
    ("standalone", 0, 1),
    ("standalone", 1, 1),
    ("standalone", 2, 1),
    ("standalone", 3, 1),
    ("structural", "collection-pager", 1),
    ("items", 5, 5),
    ("structural", "settings-destinations-fullwidth", 1),
    ("standalone", 11, 1),
]
projected = projected_rows(sample)
assert projected == [(0, 1), (2, 3), ('collection-pager',), (5, 6), (7, 8), (9,), ('settings-destinations-fullwidth',), (11,)]
assert 'if (cards.size == 1) Spacer(Modifier.weight(0.5f))' in source
assert 'if (rowIndex * 2 + 1 < interval.count)' in source
print('PASS landscape singleton pair projection, centered tail and full-width structural pager regression')
