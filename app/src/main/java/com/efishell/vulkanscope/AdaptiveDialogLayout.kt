package com.efishell.vulkanscope

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
internal fun AdaptiveDialogColumns(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val twoPane = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE && configuration.screenWidthDp >= 640
    if (!twoPane) {
        Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) { content() }
    } else {
        Layout(content = content, modifier = modifier.fillMaxWidth()) { measurables, constraints ->
            val space = 12.dp.roundToPx()
            val laneWidth = ((constraints.maxWidth - space).coerceAtLeast(0)) / 2
            val widths = intArrayOf(laneWidth, constraints.maxWidth - space - laneWidth)
            val laneY = intArrayOf(0, 0)
            val placed = measurables.mapIndexed { index, measurable ->
                val lane = index % 2
                val placeable = measurable.measure(constraints.copy(minWidth = 0, maxWidth = widths[lane]))
                val y = laneY[lane]
                laneY[lane] = y + placeable.height + space
                Triple(placeable, lane, y)
            }
            val targetHeight = (laneY.maxOrNull() ?: 0).let { (it - space).coerceAtLeast(0) }
            layout(constraints.maxWidth, targetHeight.coerceIn(constraints.minHeight, constraints.maxHeight)) {
                placed.forEach { (placeable, lane, y) ->
                    placeable.placeRelative(if (lane == 0) 0 else laneWidth + space, y)
                }
            }
        }
    }
}
