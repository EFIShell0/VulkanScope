package com.efishell.vulkanscope

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

internal data class VulkanGraphNode(
    val token: String,
    val depth: Int,
    val parent: String?,
    val evidence: String
)

private data class VulkanGraphPosition(val x: Dp, val y: Dp)

@Composable
internal fun VulkanDependencyGraph(nodes: List<VulkanGraphNode>, modifier: Modifier = Modifier) {
    val shown = nodes.take(24)
    if (shown.isEmpty()) return
    val nodeWidth = 184.dp
    val nodeHeight = 72.dp
    val columnGap = 54.dp
    val rowGap = 18.dp
    val leftPad = 12.dp
    val topPad = 12.dp
    val depthGroups = shown.groupBy { it.depth }.toSortedMap()
    val positions = linkedMapOf<String, VulkanGraphPosition>()
    depthGroups.forEach { (depth, group) ->
        group.forEachIndexed { row, node ->
            positions[node.token] = VulkanGraphPosition(
                leftPad + (nodeWidth + columnGap) * depth.toFloat(),
                topPad + (nodeHeight + rowGap) * row.toFloat()
            )
        }
    }
    val maxDepth = shown.maxOfOrNull { it.depth } ?: 0
    val maxRows = max(1, depthGroups.values.maxOfOrNull { it.size } ?: 1)
    val contentWidth = leftPad * 2f + nodeWidth * (maxDepth + 1).toFloat() + columnGap * maxDepth.toFloat()
    val contentHeight = topPad * 2f + nodeHeight * maxRows.toFloat() + rowGap * (maxRows - 1).toFloat()
    val primary = MaterialTheme.colorScheme.primary
    val outline = MaterialTheme.colorScheme.outlineVariant
    val container = MaterialTheme.colorScheme.surfaceVariant
    val content = MaterialTheme.colorScheme.onSurfaceVariant
    Box(modifier.horizontalScroll(rememberScrollState())) {
        Box(Modifier.width(contentWidth).height(contentHeight)) {
            Canvas(Modifier.width(contentWidth).height(contentHeight)) {
                shown.forEach { node ->
                    val child = positions[node.token] ?: return@forEach
                    val parent = node.parent?.let(positions::get) ?: return@forEach
                    val start = Offset(
                        (parent.x + nodeWidth).toPx(),
                        (parent.y + nodeHeight / 2f).toPx()
                    )
                    val end = Offset(
                        child.x.toPx(),
                        (child.y + nodeHeight / 2f).toPx()
                    )
                    val midX = (start.x + end.x) / 2f
                    drawLine(outline, start, Offset(midX, start.y), strokeWidth = 2.dp.toPx())
                    drawLine(outline, Offset(midX, start.y), Offset(midX, end.y), strokeWidth = 2.dp.toPx())
                    drawLine(outline, Offset(midX, end.y), end, strokeWidth = 2.dp.toPx())
                }
            }
            shown.forEach { node ->
                val pos = positions[node.token] ?: return@forEach
                Surface(
                    modifier = Modifier.offset(pos.x, pos.y).size(nodeWidth, nodeHeight),
                    shape = RoundedCornerShape(14.dp),
                    color = if (node.depth == 0) primary.copy(alpha = 0.18f) else container,
                    tonalElevation = if (node.depth == 0) 3.dp else 1.dp
                ) {
                    Box(Modifier.size(nodeWidth, nodeHeight), contentAlignment = Alignment.Center) {
                        androidx.compose.foundation.layout.Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                node.token,
                                color = content,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                node.evidence,
                                color = if (node.evidence.startsWith("Enumerated") || node.evidence.startsWith("Runtime API satisfies")) primary else content,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}
