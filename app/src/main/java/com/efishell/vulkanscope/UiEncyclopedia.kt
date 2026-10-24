@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.efishell.vulkanscope

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun EncyclopediaPage(initialQuery: String = "", report: VulkanReport? = null, device: DeviceReport? = null) {
    var query by rememberSaveable { mutableStateOf(initialQuery) }
    var category by rememberSaveable { mutableStateOf("All") }
    var availabilityFilter by rememberSaveable { mutableStateOf("All entries") }
    var page by rememberSaveable { mutableIntStateOf(0) }
    LaunchedEffect(initialQuery) {
        if (initialQuery.isNotBlank()) {
            query = initialQuery
            page = 0
        }
    }
    val deviceEntries = remember(report, device) { collectedDeviceEncyclopediaEntries(report, device) }
    val entries = remember(query, category, availabilityFilter, deviceEntries) {
        if (availabilityFilter == "On this device") {
            deviceEntries.asSequence()
                .filter { category == "All" || (category == "Extensions" && it.category == "Extension reference") ||
                    (category == "VK_*" && it.category == "Device format") }
                .filter { encyclopediaMatches(it, query.trim()) }
                .sortedBy { it.title }
                .toList()
        } else encyclopediaSearch(category, query)
    }
    val pageCount = maxOf(1, (entries.size + COLLECTION_PAGE_SIZE - 1) / COLLECTION_PAGE_SIZE)
    LaunchedEffect(entries.size) { if (page >= pageCount) page = pageCount - 1 }
    val pagedEntries = entries.drop(page * COLLECTION_PAGE_SIZE).take(COLLECTION_PAGE_SIZE)
    val queryTrimmed = query.trim()
    val filteringDeviceOnly = availabilityFilter == "On this device"
    val statusText = when {
        filteringDeviceOnly && deviceEntries.isEmpty() -> "Collect a complete device report first to enable the On this device encyclopedia filter."
        !filteringDeviceOnly && (category == "Commands" || category == "VK_*" || category == "Types") && queryTrimmed.length < 2 -> "Type at least 2 characters before searching this large symbol family."
        category == "Extensions" && queryTrimmed.isBlank() && filteringDeviceOnly -> "Showing extensions actually enumerated by the selected Vulkan runtime, not all registry extensions."
        category == "Extensions" && queryTrimmed.isBlank() -> "Showing the first $ENCYCLOPEDIA_VISIBLE_RESULT_LIMIT registered extensions. Search to narrow the local reference."
        category == "VkResult" && queryTrimmed.isBlank() -> "${VULKAN_VK_RESULT_REFERENCE.size} canonical VkResult values are available from the locked Vulkan 1.4.366 reference."
        category == "All" && queryTrimmed.isBlank() && filteringDeviceOnly -> "Showing offline reference entries that also match the selected device's collected evidence."
        category == "All" && queryTrimmed.isBlank() -> "Core concepts and VulkanScope evidence semantics are shown first. Search to resolve a registry symbol."
        !filteringDeviceOnly && entries.size >= ENCYCLOPEDIA_VISIBLE_RESULT_LIMIT -> "The result set is capped at $ENCYCLOPEDIA_VISIBLE_RESULT_LIMIT entries. Refine the query for a narrower match."
        else -> "${entries.size} matching reference entr${if (entries.size == 1) "y" else "ies"}."
    }
    VulkanLazyPage(verticalSpacing = 12.dp) {
        item {
            CapabilitySectionCard("Encyclopedia") {
                Text(
                    "A local, offline Vulkan reference for registry symbols, result codes, extensions and VulkanScope evidence semantics. The source baseline is the checked-in Vulkan 1.4.366 registry; browsing this page never performs a network request.",
                    color = VulkanTextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
                ExpressiveMetricGrid(
                    listOf(
                        "COMMANDS" to VULKAN_COMMAND_SYMBOL_COUNT.toString(),
                        "VK_* TOKENS" to VULKAN_TOKEN_SYMBOL_COUNT.toString(),
                        "Vk* TYPES" to VULKAN_TYPE_SYMBOL_COUNT.toString(),
                        "EXTENSIONS" to VULKAN_EXTENSION_REFERENCE.size.toString()
                    )
                )
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = VulkanSurfaceLow,
                    border = BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.28f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(painterResource(R.drawable.ic_shield), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.size(20.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text("Evidence boundary", color = VulkanTextPrimary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                            Text("A registry relationship or symbol registration is reference metadata. It never substitutes for runtime capability evidence collected from the selected Vulkan device.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
        item {
            CapabilitySectionCard("Reference search") {
                ExpressiveSearchField(
                    value = query,
                    onValueChange = { query = it; page = 0 },
                    modifier = Modifier.fillMaxWidth(),
                    placeholderText = "VK_SUCCESS · vkCreateInstance · VkFormat · VK_KHR_swapchain"
                )
                val categories = listOf("All", "VkResult", "Commands", "VK_*", "Types", "Extensions")
                ExpressiveFilterBar(categories, categories.indexOf(category).coerceAtLeast(0)) {
                    category = categories[it]
                    page = 0
                }
                val availabilityFilters = listOf("All entries", "On this device")
                ExpressiveFilterBar(availabilityFilters, availabilityFilters.indexOf(availabilityFilter).coerceAtLeast(0)) {
                    availabilityFilter = availabilityFilters[it]
                    page = 0
                }
                ExpressiveMetricGrid(
                    listOf(
                        "CATEGORY" to category,
                        "FILTER" to availabilityFilter,
                        "MATCHES" to entries.size.toString(),
                        "PAGE" to "${page + 1} / $pageCount"
                    )
                )
                Text(statusText, color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
            }
        }
        item {
            CapabilitySectionCard("How to read encyclopedia entries") {
                CapabilityKeyValue("Title", "Exact local symbol or concept name")
                CapabilityKeyValue("Category", "Reference family such as VkResult, command, token, type or extension")
                CapabilityKeyValue("Definition", "Concise meaning from the checked-in reference or VulkanScope interpretation rule")
                CapabilityKeyValue("Detail", "Additional dependency, promotion, alias or evidence guidance when available")
                CapabilityKeyValue("On this device", "Shown when a local encyclopedia entry can be matched back to collected evidence from the selected device")
                Text(
                    "Vulkan Video marked Not applicable describes the Vulkan Video API path exposed by the selected driver; it does not prove hardware video decode or encode is absent.",
                    color = VulkanTextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        stickyCollectionPager(totalItems = entries.size, currentPage = page, onPageChange = { page = it })
        if (entries.isEmpty()) {
            item {
                EmptyState(
                    when {
                        filteringDeviceOnly && deviceEntries.isEmpty() -> "Run collection first to filter encyclopedia entries by the current device"
                        queryTrimmed.length < 2 && category in setOf("Commands", "VK_*", "Types") -> "Enter at least two characters to search this large registry family"
                        else -> "No matching local Vulkan reference entry"
                    }
                )
            }
        } else {
            items(pagedEntries, key = { "${it.category}:${it.title}" }) { entry ->
                val onDevice = remember(entry, deviceEntries) { deviceEntries.any { it.title == entry.title && it.category == entry.category } }
                VulkanEvidenceCard(
                    title = entry.title,
                    category = entry.category,
                    status = if (onDevice) "ON DEVICE" else "REFERENCE",
                    accent = if (onDevice) ComposeColor(0xFF80DFA8) else VulkanAccentSoft,
                    icon = encyclopediaEntryIcon(entry.category),
                    description = entry.definition,
                    metadata = if (onDevice) listOf("Runtime evidence") else listOf("Registry reference"),
                    detail = entry.detail
                )
            }
        }
    }
}
