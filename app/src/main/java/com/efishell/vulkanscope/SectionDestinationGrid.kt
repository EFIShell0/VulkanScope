package com.efishell.vulkanscope

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

// Shared Settings/Surface destination geometry. Two equal-width lanes in landscape,
// with an odd final tile centered at *one lane width*. Uses the same measured bound
// in both destinations, avoiding the one-column Surface regression from 4.0.12.
@Composable
internal fun <T> SectionDestinationGrid(
    sections: List<T>,
    card: @Composable (section: T, index: Int) -> Unit
) {
    val configuration = LocalConfiguration.current
    val twoPane = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE && configuration.screenWidthDp >= 600
    val columns = if (twoPane) 2 else 1
    Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), contentAlignment = Alignment.TopCenter) {
        Column(Modifier.widthIn(max = 1000.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            sections.chunked(columns).forEachIndexed { rowIndex, rowSections ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (twoPane && rowSections.size == 1) Spacer(Modifier.weight(0.5f))
                    rowSections.forEachIndexed { columnIndex, section ->
                        Box(Modifier.weight(1f)) { card(section, rowIndex * columns + columnIndex) }
                    }
                    if (twoPane && rowSections.size == 1) Spacer(Modifier.weight(0.5f))
                }
            }
        }
    }
}
