package com.efishell.vulkanscope

import androidx.compose.runtime.Composable

internal enum class SupportFilter { ALL, SUPPORTED, UNSUPPORTED }

@Composable
internal fun SupportFilterRow(selected: SupportFilter, onSelected: (SupportFilter) -> Unit) {
    val values = listOf(SupportFilter.ALL, SupportFilter.SUPPORTED, SupportFilter.UNSUPPORTED)
    val labels = listOf("All", "Supported", "Not supported")
    ExpressiveFilterBar(labels, values.indexOf(selected).coerceAtLeast(0)) { onSelected(values[it]) }
}

internal val EMBEDDED_EXTENSION_REFERENCE_NAMES = VULKAN_EXTENSION_REFERENCE.keys
