package com.efishell.vulkanscope

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
internal fun VulkanPremiumPanel(
    modifier: Modifier = Modifier,
    color: Color = VulkanSurfaceRaised,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    emphasis: Boolean = false,
    selected: Boolean = false,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = if (color == VulkanSurfaceTonal) VulkanSurfaceRaised else color,
        border = BorderStroke(
            if (selected) 1.5.dp else 1.dp,
            VulkanAccentSoft.copy(alpha = when {
                selected -> 0.78f
                emphasis -> 0.42f
                else -> 0.27f
            })
        ),
        tonalElevation = if (emphasis) 3.dp else 1.dp
    ) { content() }
}
