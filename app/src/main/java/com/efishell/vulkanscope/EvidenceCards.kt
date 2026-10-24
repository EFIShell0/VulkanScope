package com.efishell.vulkanscope

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
internal fun VulkanEvidenceCard(
    title: String,
    category: String,
    status: String,
    accent: Color,
    icon: Int,
    description: String,
    metadata: List<String> = emptyList(),
    detail: String = "",
    onDetails: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(21.dp)
    Surface(
        shape = shape,
        color = Color(0xFF1B171A),
        border = BorderStroke(1.dp, VulkanOutlineVariant.copy(alpha = 0.78f)),
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 15.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(shape = RoundedCornerShape(13.dp), color = accent.copy(alpha = 0.12f), border = BorderStroke(1.dp, accent.copy(alpha = 0.34f))) {
                    Icon(painterResource(icon), contentDescription = null, tint = accent, modifier = Modifier.padding(10.dp).size(19.dp))
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(trademarkVulkanDisplayText(title), color = VulkanTextPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text(category, color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                if (status.isNotBlank()) {
                    Surface(shape = RoundedCornerShape(10.dp), color = accent.copy(alpha = 0.13f), border = BorderStroke(1.dp, accent.copy(alpha = 0.30f))) {
                        Text(status, color = accent, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp))
                    }
                }
            }
            if (description.isNotBlank()) Text(trademarkVulkanDisplayText(description), color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
            if (metadata.isNotEmpty()) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    metadata.forEach { token ->
                        Surface(shape = RoundedCornerShape(9.dp), color = VulkanSurfaceLow, border = BorderStroke(1.dp, VulkanOutlineVariant.copy(alpha = 0.58f))) {
                            Text(trademarkVulkanDisplayText(token), color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp))
                        }
                    }
                }
            }
            if (detail.isNotBlank()) Text(trademarkVulkanDisplayText(detail), color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
            if (onDetails != null) {
                HorizontalDivider(color = VulkanOutlineVariant.copy(alpha = 0.45f))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    DetailAffordance(onDetails)
                }
            }
        }
    }
}
