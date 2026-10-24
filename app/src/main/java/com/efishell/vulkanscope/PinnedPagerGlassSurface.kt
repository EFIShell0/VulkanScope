package com.efishell.vulkanscope

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.unit.dp

@Composable
internal fun PinnedPagerGlassSurface(
    blurredLayer: GraphicsLayer,
    originX: Float,
    originY: Float,
    blurVisibility: Float,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(25.dp)
    Box(Modifier.fillMaxWidth().clip(shape).border(1.dp, VulkanGlassBorder.copy(alpha = VulkanGlassBorder.alpha * blurVisibility), shape)) {
        if (blurVisibility > 0.001f) {
            Box(
                Modifier.matchParentSize()
                    .graphicsLayer { alpha = blurVisibility }
                    .drawWithContent {
                        withTransform({ translate(-originX, -originY) }) {
                            drawLayer(blurredLayer)
                        }
                        drawRect(VulkanGlassTint)
                    }
            )
        }
        Box(Modifier.padding(3.dp)) { content() }
    }
}
