package com.efishell.vulkanscope

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun ChromeVisibilityPreferencesCard() {
    val preferences = LocalChromeAutoHideSettings.current ?: return
    CapabilitySectionCard("App bar visibility") {
        Text(
            "Make room for Vulkan data without hiding Android notifications or system controls.",
            color = VulkanTextSecondary,
            style = MaterialTheme.typography.bodySmall
        )
        ChromeVisibilityToggle(
            title = "Automatic bar hiding",
            description = if (preferences.enabled) "ON  ·  Bars return when you touch or scroll" else "OFF  ·  Both app bars stay visible",
            icon = R.drawable.ic_chrome_auto_hide,
            checked = preferences.enabled,
            enabled = true,
            onChanged = preferences::updateEnabled,
            primary = true
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "CHOOSE WHICH BARS TO HIDE",
                color = VulkanAccentSoft,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 2.dp, top = 3.dp)
            )
            ChromeVisibilityToggle(
                title = "Top app header",
                description = "VulkanScope logo, page heading and actions",
                icon = R.drawable.ic_chrome_header,
                checked = preferences.hideHeader,
                enabled = preferences.enabled,
                onChanged = preferences::updateHideHeader
            )
            ChromeVisibilityToggle(
                title = "Bottom navigation",
                description = "Overview, Vulkan, Display and other tabs",
                icon = R.drawable.ic_chrome_navigation,
                checked = preferences.hideNavigation,
                enabled = preferences.enabled,
                onChanged = preferences::updateHideNavigation
            )
        }
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = VulkanSurfaceLow,
            border = BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.20f))
        ) {
            Text(
                "After 3 seconds of inactivity, the selected app bars slide away. Touch, swipe or use navigation to reveal them. Changes are saved automatically.",
                color = VulkanTextSecondary,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 11.dp)
            )
        }
    }
}

@Composable
private fun ChromeVisibilityToggle(
    title: String,
    description: String,
    icon: Int,
    checked: Boolean,
    enabled: Boolean,
    onChanged: (Boolean) -> Unit,
    primary: Boolean = false
) {
    val shape = RoundedCornerShape(if (primary) 18.dp else 14.dp)
    Surface(
        shape = shape,
        color = if (primary && enabled) VulkanAccentContainer else VulkanSurfaceTonal,
        border = BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = if (primary && enabled) 0.42f else 0.18f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = if (enabled) VulkanSurfaceRaised else VulkanSurfaceLow,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = if (enabled) VulkanAccentSoft else VulkanTextMuted,
                    modifier = Modifier.padding(10.dp).size(22.dp)
                )
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, color = if (enabled) VulkanTextPrimary else VulkanTextMuted, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                Text(description, color = if (enabled) VulkanTextSecondary else VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
            }
            ExpressiveSwitch(checked = checked, onCheckedChange = if (enabled) onChanged else null, enabled = enabled)
        }
    }
}
