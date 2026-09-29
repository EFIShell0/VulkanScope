package com.efishell.vulkanscope

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

internal data class VulkanGraphNode(
    val token: String,
    val depth: Int,
    val parent: String?,
    val evidence: String
)

private data class VulkanGraphPosition(val x: Dp, val y: Dp)

private enum class VulkanGraphEvidenceState { PRESENT, ABSENT, UNKNOWN, NOT_APPLICABLE }

private fun VulkanGraphNode.evidenceState(): VulkanGraphEvidenceState = when {
    evidence.startsWith("Enumerated") || evidence.startsWith("Runtime API satisfies") -> VulkanGraphEvidenceState.PRESENT
    evidence.startsWith("Not enumerated") || evidence.startsWith("Runtime API does not expose") -> VulkanGraphEvidenceState.ABSENT
    evidence.startsWith("Not applicable") -> VulkanGraphEvidenceState.NOT_APPLICABLE
    else -> VulkanGraphEvidenceState.UNKNOWN
}

private fun graphStateLabel(state: VulkanGraphEvidenceState): String = when (state) {
    VulkanGraphEvidenceState.PRESENT -> "Present"
    VulkanGraphEvidenceState.ABSENT -> "Not exposed"
    VulkanGraphEvidenceState.UNKNOWN -> "Unknown"
    VulkanGraphEvidenceState.NOT_APPLICABLE -> "N/A"
}

@Composable
private fun graphStateColor(state: VulkanGraphEvidenceState): Color = when (state) {
    VulkanGraphEvidenceState.PRESENT -> Color(0xFF73C991)
    VulkanGraphEvidenceState.ABSENT -> Color(0xFFFF7B7B)
    VulkanGraphEvidenceState.UNKNOWN -> Color(0xFFFFC857)
    VulkanGraphEvidenceState.NOT_APPLICABLE -> MaterialTheme.colorScheme.onSurfaceVariant
}

@Composable
private fun VulkanGraphLegend(nodes: List<VulkanGraphNode>) {
    val states = remember(nodes) { nodes.groupingBy { it.evidenceState() }.eachCount() }
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        VulkanGraphEvidenceState.entries.forEach { state ->
            val count = states[state] ?: 0
            val tint = graphStateColor(state)
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = tint.copy(alpha = 0.12f),
                contentColor = tint,
                border = androidx.compose.foundation.BorderStroke(1.dp, tint.copy(alpha = 0.38f))
            ) {
                Row(
                    Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(Modifier.size(7.dp).clip(RoundedCornerShape(99.dp)).background(tint))
                    Text("${graphStateLabel(state)} · $count", color = tint, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
internal fun VulkanDependencyGraph(nodes: List<VulkanGraphNode>, modifier: Modifier = Modifier) {
    val shown = nodes.take(24)
    if (shown.isEmpty()) return
    val nodeWidth = 210.dp
    val nodeHeight = 94.dp
    val columnGap = 62.dp
    val rowGap = 18.dp
    val leftPad = 18.dp
    val topPad = 48.dp
    val bottomPad = 18.dp
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
    val contentHeight = topPad + bottomPad + nodeHeight * maxRows.toFloat() + rowGap * (maxRows - 1).toFloat()
    val viewportHeight = min(contentHeight.value, 520f).dp.coerceAtLeast(190.dp)
    val primary = MaterialTheme.colorScheme.primary
    val outline = MaterialTheme.colorScheme.outlineVariant
    val container = MaterialTheme.colorScheme.surfaceVariant
    val content = MaterialTheme.colorScheme.onSurfaceVariant
    val horizontalState = rememberScrollState()
    val verticalState = rememberScrollState()

    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.weight(1f)) {
                Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(shown.size.toString(), color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("nodes", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                }
            }
            Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.weight(1f)) {
                Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(shown.count { it.parent != null }.toString(), color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("edges", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                }
            }
            Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.weight(1f)) {
                Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text((maxDepth + 1).toString(), color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("levels", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        VulkanGraphLegend(shown)
        Text("Swipe horizontally for dependency depth and vertically when a level contains many nodes.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
            border = androidx.compose.foundation.BorderStroke(1.dp, outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(viewportHeight)
                    .horizontalScroll(horizontalState)
                    .verticalScroll(verticalState)
            ) {
                Box(Modifier.width(contentWidth).height(contentHeight)) {
                    Canvas(Modifier.width(contentWidth).height(contentHeight)) {
                        for (depth in 0..maxDepth) {
                            val x = (leftPad + (nodeWidth + columnGap) * depth.toFloat() - 9.dp).toPx()
                            drawLine(outline.copy(alpha = 0.36f), Offset(x, 0f), Offset(x, size.height), strokeWidth = 1.dp.toPx())
                        }
                        shown.forEach { node ->
                            val child = positions[node.token] ?: return@forEach
                            val parent = node.parent?.let(positions::get) ?: return@forEach
                            val start = Offset((parent.x + nodeWidth).toPx(), (parent.y + nodeHeight / 2f).toPx())
                            val end = Offset(child.x.toPx(), (child.y + nodeHeight / 2f).toPx())
                            val midX = (start.x + end.x) / 2f
                            drawLine(outline, start, Offset(midX, start.y), strokeWidth = 2.dp.toPx())
                            drawLine(outline, Offset(midX, start.y), Offset(midX, end.y), strokeWidth = 2.dp.toPx())
                            drawLine(outline, Offset(midX, end.y), end, strokeWidth = 2.dp.toPx())
                            drawCircle(outline, radius = 3.dp.toPx(), center = end)
                        }
                    }
                    for (depth in 0..maxDepth) {
                        Text(
                            if (depth == 0) "ROOT" else "DEPTH $depth",
                            color = if (depth == 0) primary else content,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.offset(leftPad + (nodeWidth + columnGap) * depth.toFloat(), 14.dp).width(nodeWidth)
                        )
                    }
                    shown.forEach { node ->
                        val pos = positions[node.token] ?: return@forEach
                        val state = node.evidenceState()
                        val stateColor = graphStateColor(state)
                        Surface(
                            modifier = Modifier.offset(pos.x, pos.y).size(nodeWidth, nodeHeight),
                            shape = RoundedCornerShape(18.dp),
                            color = if (node.depth == 0) primary.copy(alpha = 0.18f) else container,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (node.depth == 0) primary.copy(alpha = 0.48f) else stateColor.copy(alpha = 0.34f)),
                            tonalElevation = if (node.depth == 0) 3.dp else 1.dp
                        ) {
                            Column(
                                Modifier.padding(horizontal = 11.dp, vertical = 9.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    node.token,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.labelMedium,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    fontFamily = FontFamily.Monospace
                                )
                                HorizontalDivider(color = outline.copy(alpha = 0.55f))
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                    Box(Modifier.size(7.dp).clip(RoundedCornerShape(99.dp)).background(stateColor))
                                    Text(graphStateLabel(state), color = stateColor, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                                Text(node.evidence, color = content, style = MaterialTheme.typography.labelSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
        }
        if (nodes.size > shown.size) {
            Spacer(Modifier.height(1.dp))
            Text("Visual graph is capped at ${shown.size} nodes; the complete bounded traversal remains available in the evidence list below.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
        }
    }
}
