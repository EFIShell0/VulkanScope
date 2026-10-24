package com.efishell.vulkanscope

internal fun collectionPagerPassedAnchor(
    anchorItemIndex: Int,
    firstVisibleItemIndex: Int,
    totalItems: Int,
    pageSize: Int
): Boolean = anchorItemIndex >= 0 && pageSize > 0 && totalItems > pageSize && firstVisibleItemIndex >= anchorItemIndex

internal fun shouldPinCollectionPager(
    anchorItemIndex: Int,
    firstVisibleItemIndex: Int,
    totalItems: Int,
    pageSize: Int,
    visiblePagerOffsetPx: Int?,
    viewportStartOffsetPx: Int,
    stablePinBoundaryPx: Int
): Boolean {
    if (anchorItemIndex < 0 || pageSize <= 0 || totalItems <= pageSize) return false
    return if (visiblePagerOffsetPx != null) {
        visiblePagerOffsetPx - viewportStartOffsetPx <= stablePinBoundaryPx
    } else {
        firstVisibleItemIndex >= anchorItemIndex
    }
}

// Pure Kotlin companion to the actual adaptive LazyColumn row projection. It is
// exercised by the JVM regression suite (unlike view-text-only source checks).
internal data class PagerProjectionSegment(
    val count: Int,
    val standalone: Boolean,
    val fullWidth: Boolean,
    val isPager: Boolean
)

internal fun projectedPagerRowIndex(segments: List<PagerProjectionSegment>, twoPane: Boolean): Int? {
    var row = 0
    var pending = 0
    for (segment in segments) {
        if (segment.isPager && (!segment.standalone || !segment.fullWidth)) return null
        if (!twoPane) {
            if (segment.isPager) return row
            row += segment.count
            continue
        }
        when {
            segment.standalone && segment.fullWidth -> {
                if (pending > 0) { row++; pending = 0 }
                if (segment.isPager) return row
                row++
            }
            segment.standalone -> {
                pending++
                if (pending == 2) { row++; pending = 0 }
            }
            else -> {
                if (pending > 0) { row++; pending = 0 }
                row += (segment.count + 1) / 2
            }
        }
    }
    return null
}
