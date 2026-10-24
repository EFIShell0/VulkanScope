@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.efishell.vulkanscope

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp



@Composable
internal fun ExtensionsPage(report: VulkanReport, device: DeviceReport?) {
    val uriHandler = LocalUriHandler.current
    val networkAvailable = LocalValidatedNetwork.current
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("All") }
    var page by rememberSaveable { mutableIntStateOf(0) }
    var selectedSupported by remember { mutableStateOf<ExtensionEntry?>(null) }
    var selectedCatalog by remember { mutableStateOf<String?>(null) }
    val supported = remember(report, device) {
        (report.instanceExtensions + (device?.extensions ?: emptyList()))
            .sortedWith(compareBy<ExtensionEntry> { it.name }.thenBy { it.scope }.thenBy { it.specVersion })
    }
    val supportedNames = remember(supported) { supported.map { it.name }.toSet() }
    val catalog = remember(supportedNames, report.instanceExtensionStatus, device?.deviceExtensionStatus) {
        val completeRuntimeScope = report.instanceExtensionStatus == "available" && (device == null || device.deviceExtensionStatus == "available")
        if (completeRuntimeScope) EMBEDDED_EXTENSION_REFERENCE_NAMES.filterNot { it in supportedNames }.sorted() else emptyList()
    }
    val filteredSupported = remember(query, supported, filter) {
        if (filter == "Not enumerated") emptyList() else supported.filter { matchesExtensionExplorerQuery(it.name, it.scope, true, query) }
    }
    val filteredCatalog = remember(query, catalog, filter) {
        if (filter == "Supported") emptyList() else catalog.filter { matchesExtensionExplorerQuery(it, "registry", false, query) }
    }
    val total = filteredSupported.size + filteredCatalog.size
    val pageCount = maxOf(1, (total + COLLECTION_PAGE_SIZE - 1) / COLLECTION_PAGE_SIZE)
    LaunchedEffect(query, filter) { page = 0 }
    LaunchedEffect(total) { page = page.coerceIn(0, pageCount - 1) }
    val pageStart = page * COLLECTION_PAGE_SIZE
    val pageEnd = minOf(pageStart + COLLECTION_PAGE_SIZE, total)
    val supportedStart = pageStart.coerceAtMost(filteredSupported.size)
    val supportedEnd = pageEnd.coerceAtMost(filteredSupported.size)
    val catalogStart = (pageStart - filteredSupported.size).coerceAtLeast(0).coerceAtMost(filteredCatalog.size)
    val catalogEnd = (pageEnd - filteredSupported.size).coerceAtLeast(0).coerceAtMost(filteredCatalog.size)
    val pageSupported = if (supportedEnd > supportedStart) filteredSupported.subList(supportedStart, supportedEnd) else emptyList()
    val pageCatalog = if (catalogEnd > catalogStart) filteredCatalog.subList(catalogStart, catalogEnd) else emptyList()
    VulkanLazyPage(verticalSpacing = 7.dp) {
        item {
            CapabilitySectionCard("Extension explorer") {
                Text("Runtime-enumerated extensions are shown exactly as reported. The checked-in registry-reference subset is supplementary and is never labeled unsupported.", color = ComposeColor(0xFFB6ACAE), style = MaterialTheme.typography.bodySmall)
                if (report.instanceExtensionStatus != "available") {
                    Text("Instance extension enumeration: ${report.instanceExtensionStatus.uppercase()}${if (report.instanceExtensionReason.isBlank()) "" else " — ${report.instanceExtensionReason}"}", color = evidenceStateAccent(report.instanceExtensionStatus), style = MaterialTheme.typography.bodySmall)
                }
                if (device != null && device.deviceExtensionStatus != "available") {
                    Text("Device extension enumeration: ${device.deviceExtensionStatus.uppercase()}${if (device.deviceExtensionReason.isBlank()) "" else " — ${device.deviceExtensionReason}"}", color = evidenceStateAccent(device.deviceExtensionStatus), style = MaterialTheme.typography.bodySmall)
                }
                ExpressiveSearchField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), placeholderText = "Search · vendor:KHR promoted:1.3 depends:VK_KHR command:vkGet enum:STRUCTURE handler:true")
                val extensionFilters = listOf("All", "Supported", "Not enumerated")
                ExpressiveFilterBar(extensionFilters, extensionFilters.indexOf(filter).coerceAtLeast(0)) { filter = extensionFilters[it] }
                ExpressiveMetricGrid(
                    listOf(
                        "Matching entries" to total.toString(),
                        "Showing" to if (total == 0) "0" else "${pageStart + 1}-$pageEnd",
                        "Embedded reference subset" to EMBEDDED_EXTENSION_REFERENCE_NAMES.size.toString()
                    ),
                    Modifier.padding(top = 4.dp)
                )
            }
        }
        stickyCollectionPager(totalItems = total, currentPage = page, onPageChange = { page = it })
        itemsIndexed(pageSupported, key = { index, extension -> "supported:${extension.scope}:${extension.name}:${extension.specVersion}:${supportedStart + index}" }) { _, extension ->
            ExtensionExplorerCard(
                title = extension.name,
                summary = "Runtime-enumerated ${extension.scope.lowercase()} extension token from the active Vulkan path.",
                statusLabel = "SUPPORTED",
                accent = ComposeColor(0xFF80DFA8),
                icon = R.drawable.ic_extensions,
                detailText = "Scope ${extension.scope} · spec ${extension.specVersion}",
                extraPills = listOf(extension.scope, "Spec ${extension.specVersion}"),
                onClick = { selectedSupported = extension }
            )
        }
        itemsIndexed(pageCatalog, key = { index, name -> "catalog:$name:${catalogStart + index}" }) { _, name ->
            ExtensionExplorerCard(
                title = name,
                summary = "Present in the embedded Vulkan registry subset, but not enumerated by the completed runtime extension census.",
                statusLabel = "REFERENCE ONLY",
                accent = ComposeColor(0xFFFFC77D),
                icon = R.drawable.ic_registry,
                detailText = "Registry entry · not shown in runtime enumeration",
                extraPills = listOf("Registry", "Not enumerated"),
                onClick = { selectedCatalog = name }
            )
        }
        if (total == 0) item { EmptyState("No matching extensions") }
    }
    selectedSupported?.let { extension ->
        val group = vulkanExtensionQueryGroup(extension.name)
        ScrollableDetailDialog(
            title = extension.name,
            onDismiss = { selectedSupported = null }
        ) {
                val ref = vulkanExtensionReference(extension.name)
                CapabilityKeyValue("Runtime evidence", "Exact extension token enumerated")
                CapabilityKeyValue("Scope", extension.scope)
                CapabilityKeyValue("Runtime specVersion", extension.specVersion.toString())
                CapabilityKeyValue("Registry author tag", ref.author)
                CapabilityKeyValue("Registry baseline", "Vulkan® 1.4.366")
                CapabilityKeyValue("Embedded registry revision", ref.specVersion.ifBlank { "Unavailable in checked-in reference asset" })
                CapabilityKeyValue("Registry status", if (ref.provisional) "Provisional / beta" else "Registered")
                CapabilityKeyValue("Extension type", ref.type.ifBlank { "Unavailable in checked-in reference asset" })
                CapabilityKeyValue("Platform", ref.platform.ifBlank { "Unavailable in checked-in reference asset" })
                CapabilityKeyValue("Promoted to core", ref.promotedTo.ifBlank { "Unavailable in checked-in reference asset" })
                CapabilityKeyValue("Registry requires", ref.requires.ifBlank { "Unavailable in checked-in reference asset" })
                CapabilityKeyValue("Deprecated by", ref.deprecatedBy.ifBlank { "Unavailable in checked-in reference asset" })
                CapabilityKeyValue("Obsoleted by", ref.obsoletedBy.ifBlank { "Unavailable in checked-in reference asset" })
                CapabilityKeyValue("Registry depends", ref.depends.ifBlank { "Unavailable in checked-in reference asset" })
                CapabilityKeyValue("Dedicated query handler", group ?: ref.queryGroup.ifBlank { "No dedicated handler cataloged" })
                CapabilityKeyValue("Related commands", ref.commands.takeIf { it.isNotEmpty() }?.joinToString(", ") ?: "See authoritative Khronos extension page")
                CapabilityKeyValue("Related enums/tokens", ref.enums.takeIf { it.isNotEmpty() }?.joinToString(", ") ?: "See authoritative Khronos extension page")
                ExpressiveContainedIconTextButton("Open Khronos specification", R.drawable.ic_open_external, enabled = networkAvailable) { uriHandler.openUri(ref.specUrl) }
                Text("Runtime enumeration, registry metadata and dedicated feature/property query evidence remain separate. The checked-in metadata is generated from the locked Vulkan® 1.4.366 registry; the Khronos link remains authoritative for the complete interface definition.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
        }
    }
    selectedCatalog?.let { name ->
        ScrollableDetailDialog(
            title = name,
            onDismiss = { selectedCatalog = null }
        ) {
                val ref = vulkanExtensionReference(name)
                CapabilityKeyValue("Runtime evidence", "Not enumerated in the completed runtime extension set")
                CapabilityKeyValue("Registry author tag", ref.author)
                CapabilityKeyValue("Registry baseline", "Vulkan® 1.4.366")
                CapabilityKeyValue("Embedded registry revision", ref.specVersion.ifBlank { "Unavailable in checked-in reference asset" })
                CapabilityKeyValue("Registry status", if (ref.provisional) "Provisional / beta" else "Registered")
                CapabilityKeyValue("Extension type", ref.type.ifBlank { "Unavailable in checked-in reference asset" })
                CapabilityKeyValue("Platform", ref.platform.ifBlank { "Unavailable in checked-in reference asset" })
                CapabilityKeyValue("Promoted to core", ref.promotedTo.ifBlank { "Unavailable in checked-in reference asset" })
                CapabilityKeyValue("Registry requires", ref.requires.ifBlank { "Unavailable in checked-in reference asset" })
                CapabilityKeyValue("Deprecated by", ref.deprecatedBy.ifBlank { "Unavailable in checked-in reference asset" })
                CapabilityKeyValue("Obsoleted by", ref.obsoletedBy.ifBlank { "Unavailable in checked-in reference asset" })
                CapabilityKeyValue("Registry depends", ref.depends.ifBlank { "Unavailable in checked-in reference asset" })
                CapabilityKeyValue("Dedicated query handler", vulkanExtensionQueryGroup(name) ?: ref.queryGroup.ifBlank { "No dedicated handler cataloged" })
                CapabilityKeyValue("Related commands", ref.commands.takeIf { it.isNotEmpty() }?.joinToString(", ") ?: "See authoritative Khronos extension page")
                CapabilityKeyValue("Related enums/tokens", ref.enums.takeIf { it.isNotEmpty() }?.joinToString(", ") ?: "See authoritative Khronos extension page")
                ExpressiveContainedIconTextButton("Open Khronos specification", R.drawable.ic_open_external, enabled = networkAvailable) { uriHandler.openUri(ref.specUrl) }
                Text("This entry comes from the checked-in Vulkan® 1.4.366 registry census. Runtime absence is not an Unsupported claim; runtime enumeration and registry registration remain separate evidence.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
        }
    }
}


@Composable
private fun ExtensionExplorerCard(
    title: String,
    summary: String,
    statusLabel: String,
    accent: ComposeColor,
    icon: Int,
    detailText: String,
    extraPills: List<String>,
    onClick: () -> Unit
) {
    VulkanEvidenceCard(
        title = title,
        category = "Vulkan extension",
        status = statusLabel,
        accent = accent,
        icon = icon,
        description = summary,
        metadata = extraPills,
        detail = detailText,
        onDetails = onClick
    )
}
