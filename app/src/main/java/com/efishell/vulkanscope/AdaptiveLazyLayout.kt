package com.efishell.vulkanscope

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
internal data class RecordedCollectionPager(
    val key: String,
    val totalItems: Int,
    val currentPage: Int,
    val pageSize: Int,
    val onPageChange: (Int) -> Unit
)

internal class AdaptiveLazyListRecorder : LazyListScope {
    internal val collectionPagers = mutableListOf<RecordedCollectionPager>()

    internal fun registerPager(descriptor: RecordedCollectionPager) {
        collectionPagers.add(descriptor)
    }

    // Index mapping matches the actual LazyColumn rows, NOT the logical source indexes.
    // The pager registration must exist even after its Lazy item has left composition.
    internal fun renderedPagerIndex(key: String, twoPane: Boolean): Int? =
        projectedPagerRowIndex(
            intervals.map { interval ->
                PagerProjectionSegment(
                    count = interval.count,
                    standalone = interval.standalone,
                    fullWidth = isFullWidthLazyItem(interval.stableKey?.toString().orEmpty()),
                    isPager = interval.stableKey == key
                )
            },
            twoPane
        )

    internal data class Interval(
        val start: Int,
        val count: Int,
        val standalone: Boolean,
        val stableKey: Any?,
        val content: @Composable LazyItemScope.(Int) -> Unit
    )

    internal val intervals = ArrayList<Interval>()
    var totalCount: Int = 0
        private set

    override fun item(key: Any?, contentType: Any?, content: @Composable LazyItemScope.() -> Unit) {
        intervals += Interval(totalCount, 1, true, key) { content() }
        totalCount++
    }

    override fun items(
        count: Int,
        key: ((index: Int) -> Any)?,
        contentType: (index: Int) -> Any?,
        itemContent: @Composable LazyItemScope.(index: Int) -> Unit
    ) {
        require(count >= 0)
        if (count == 0) return
        intervals += Interval(totalCount, count, false, null, itemContent)
        totalCount += count
    }

    override fun stickyHeader(key: Any?, contentType: Any?, content: @Composable LazyItemScope.(Int) -> Unit) {
        val headerIndex = totalCount
        item(key, contentType) { content(headerIndex) }
    }

    @Composable
    fun renderItem(index: Int, scope: LazyItemScope) {
        require(index in 0 until totalCount)
        var left = 0
        var right = intervals.lastIndex
        while (left < right) {
            val middle = (left + right + 1) ushr 1
            if (intervals[middle].start <= index) left = middle else right = middle - 1
        }
        val interval = intervals[left]
        interval.content(scope, index - interval.start)
    }
}

internal fun isFullWidthLazyItem(key: String): Boolean =
    key.contains("pager", ignoreCase = true) ||
        key == "settings-destinations-fullwidth" ||
        key == "surface-destinations-fullwidth"

internal fun adaptiveTwoColumnContent(recorder: AdaptiveLazyListRecorder): LazyListScope.() -> Unit = {
    val pendingCards = ArrayList<AdaptiveLazyListRecorder.Interval>(2)

    fun LazyListScope.flushCards() {
        if (pendingCards.isEmpty()) return
        val cards = pendingCards.toList()
        pendingCards.clear()
        val rowKey = cards.joinToString(separator = ":", prefix = "paired-cards:") {
            it.stableKey?.toString() ?: it.start.toString()
        }
        item(key = rowKey) {
            val originalScope = this
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (cards.size == 1) Spacer(Modifier.weight(0.5f))
                cards.forEach { card ->
                    Box(Modifier.weight(1f)) { recorder.renderItem(card.start, originalScope) }
                }
                if (cards.size == 1) Spacer(Modifier.weight(0.5f))
            }
        }
    }

    recorder.intervals.forEach { interval ->
        val key = interval.stableKey?.toString().orEmpty()
        val structural = isFullWidthLazyItem(key)
        when {
            interval.standalone && structural -> {
                flushCards()
                item(key = interval.stableKey ?: "structural-${interval.start}") {
                    recorder.renderItem(interval.start, this)
                }
            }
            interval.standalone -> {
                pendingCards.add(interval)
                if (pendingCards.size == 2) flushCards()
            }
            else -> {
                flushCards()
                val pairCount = (interval.count + 1) / 2
                items(count = pairCount, key = { "two-column-${interval.start}-$it" }) { rowIndex ->
                    val originalScope = this
                    val leftIndex = interval.start + rowIndex * 2
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (rowIndex * 2 + 1 < interval.count) {
                            Box(Modifier.weight(1f)) { recorder.renderItem(leftIndex, originalScope) }
                            Box(Modifier.weight(1f)) { recorder.renderItem(leftIndex + 1, originalScope) }
                        } else {
                            Spacer(Modifier.weight(0.5f))
                            Box(Modifier.weight(1f)) { recorder.renderItem(leftIndex, originalScope) }
                            Spacer(Modifier.weight(0.5f))
                        }
                    }
                }
            }
        }
    }
    flushCards()
}
