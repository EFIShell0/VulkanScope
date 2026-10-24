@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.efishell.vulkanscope

import android.graphics.Shader
import android.view.Surface
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.key
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONObject



@Composable
internal fun FormatsPage(device: DeviceReport?) {
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<FormatEntry?>(null) }
    var usageFilters by remember { mutableStateOf<Set<String>>(emptySet()) }
    var page by rememberSaveable { mutableIntStateOf(0) }
    val formats = remember(device) { device?.formats ?: emptyList() }
    val filtered = remember(query, formats, usageFilters) { formats.filter { matchesFormatExplorerQuery(it, query) && formatMatchesUsageFilters(it, usageFilters) } }
    val pageCount = maxOf(1, (filtered.size + COLLECTION_PAGE_SIZE - 1) / COLLECTION_PAGE_SIZE)
    LaunchedEffect(query, usageFilters) { page = 0 }
    LaunchedEffect(filtered.size) { page = page.coerceIn(0, pageCount - 1) }
    val pageStart = page * COLLECTION_PAGE_SIZE
    val paged = filtered.drop(pageStart).take(COLLECTION_PAGE_SIZE)
    VulkanLazyPage(verticalSpacing = 8.dp) {
        item {
            CapabilitySectionCard("Format explorer") {
                Text("Implementation-reported format capabilities. Bitmasks are expanded to canonical Vulkan® feature names; unknown bits remain visible in hexadecimal.", color = ComposeColor(0xFFB6ACAE), style = MaterialTheme.typography.bodySmall)
                ExpressiveSearchField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), placeholderText = "Search · supported:true feature:SAMPLED name:R16")
                Text("Usage requirements · all selected filters must be present in collected format-feature evidence", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                ExpressiveMultiFilterBar(FORMAT_USAGE_FILTERS.keys.toList(), usageFilters) { label ->
                    usageFilters = if (label in usageFilters) usageFilters - label else usageFilters + label
                }
                if (usageFilters.isNotEmpty()) ExpressiveContainedIconTextButton("Clear usage filters", R.drawable.ic_clear_filters) { usageFilters = emptySet() }
                ExpressiveMetricGrid(
                    listOf(
                        "Matching formats" to filtered.size.toString(),
                        "Showing" to if (filtered.isEmpty()) "0" else "${pageStart + 1}-${pageStart + paged.size}",
                        "Active usage filters" to usageFilters.size.toString()
                    ),
                    Modifier.padding(top = 4.dp)
                )
                Text("Use Details for full decoded/raw format evidence.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
            }
        }
        stickyCollectionPager(totalItems = filtered.size, currentPage = page, onPageChange = { page = it })
        itemsIndexed(paged, key = { index, format -> "format:${format.name}:${pageStart + index}" }) { _, format ->
            VulkanEvidenceCard(
                title = format.name,
                category = "VkFormat capability",
                status = if (format.supported) "SUPPORTED" else "NOT SUPPORTED",
                accent = if (format.supported) ComposeColor(0xFF80DFA8) else ComposeColor(0xFFFFC77D),
                icon = R.drawable.ic_formats,
                description = "Format usage support is determined by the specific collected feature masks.",
                metadata = listOf("Linear ${if (format.linear != 0L) "yes" else "no"}", "Optimal ${if (format.optimal != 0L) "yes" else "no"}", "Buffer ${if (format.buffer != 0L) "yes" else "no"}"),
                onDetails = { selected = format }
            )
        }
        if (filtered.isEmpty()) item { EmptyState("No matching formats") }
    }
    selected?.let { format ->
        val imageProperties = remember(device?.detailedProperties, format.name) {
            device?.detailedProperties.orEmpty().filter { it.section == "Image Format Properties2" && (it.name == format.name || it.name.startsWith(format.name + " · ")) }
        }
        val imageQueryOutcomes = remember(device?.imageFormatQueryResults, format.name) {
            device?.imageFormatQueryResults.orEmpty().filter { it.name == format.name || it.name.startsWith(format.name + " · ") }
        }
        ScrollableDetailDialog(
            title = format.name,
            onDismiss = { selected = null }
        ) {
            CapabilityKeyValue("Runtime status", if (format.supported) "SUPPORTED" else "NOT SUPPORTED")
            CapabilityKeyValue("Linear decoded", formatFeatureFlags(format.linear))
            CapabilityKeyValue("Linear raw", "${java.lang.Long.toUnsignedString(format.linear)} · 0x${java.lang.Long.toUnsignedString(format.linear, 16).uppercase()}")
            CapabilityKeyValue("Optimal decoded", formatFeatureFlags(format.optimal))
            CapabilityKeyValue("Optimal raw", "${java.lang.Long.toUnsignedString(format.optimal)} · 0x${java.lang.Long.toUnsignedString(format.optimal, 16).uppercase()}")
            CapabilityKeyValue("Buffer decoded", formatFeatureFlags(format.buffer))
            CapabilityKeyValue("Buffer raw", "${java.lang.Long.toUnsignedString(format.buffer)} · 0x${java.lang.Long.toUnsignedString(format.buffer, 16).uppercase()}")
            imageProperties.forEach { CapabilityKeyValue(it.name.removePrefix(format.name).removePrefix(" · ").ifBlank { "Image properties" }, it.value) }
            if (imageProperties.isEmpty() && imageQueryOutcomes.isEmpty()) CapabilityKeyValue("Image Format Properties2", "Unavailable or not yet queried")
            imageQueryOutcomes.forEach { outcome ->
                CapabilityKeyValue(
                    outcome.name.removePrefix(format.name).removePrefix(" · ").ifBlank { "Base query" },
                    when (outcome.status) {
                        "available" -> "Available · ${vkResultText(0)}"
                        "unsupported" -> "Unsupported · ${vkResultText(-11)}"
                        "unavailable" -> "Unavailable · ${outcome.vkResult?.let(::vkResultText) ?: "VkResult unavailable"}"
                        "not_applicable" -> "Not applicable · ${outcome.reason}"
                        else -> "Unknown"
                    }
                )
            }
            Text("VkFormatProperties3 64-bit Flags2 evidence is authoritative when available; legacy 32-bit values are fallback-only. Image Format Properties2 uses a fixed VulkanScope query recipe: VK_IMAGE_TYPE_2D, TRANSFER_SRC | TRANSFER_DST | SAMPLED usage and flags=0. Successful property payloads remain in normal detailed-property evidence. A separate exact tuple-state ledger records Available, Unsupported, Unavailable and Not applicable query outcomes without inflating the Properties & Limits totals. Missing prerequisite external-memory extensions are represented as Not applicable rather than ambiguous Not reported evidence.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
internal fun PropertiesPage(device: DeviceReport?, onRequestQuery: (String) -> Unit) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("All") }
    var page by rememberSaveable { mutableIntStateOf(0) }
    val properties = remember(device) { device?.detailedProperties ?: emptyList() }
    val sections = remember(properties, device?.apiVersion, device?.vulkan14Status) {
        val base = listOf("All", "Limits") + properties.map { it.section }.distinct()
        if (apiAtLeast(device?.apiVersion ?: "", 1, 4) && !base.contains("Core 1.4")) base + "Core 1.4" else base
    }
    LaunchedEffect(filter) {
        if (filter == "Core 1.4") onRequestQuery("core14")
    }
    val filtered = remember(query, filter, properties) {
        properties.filter {
            (filter != "Limits" && (filter == "All" || it.section == filter)) &&
                (query.isBlank() || it.name.contains(query, true) || it.value.contains(query, true) || it.section.contains(query, true))
        }
    }
    val visibleLimits = remember(query, device?.limits) {
        (device?.limits ?: emptyList()).filter {
            query.isBlank() || it.first.contains(query, true) || it.second.contains(query, true)
        }
    }
    val evidenceResultCount = filtered.size
    val safetyEvidenceCount = filtered.count { it.section == "Vulkan Query Safety" }
    val propertyResultCount = evidenceResultCount - safetyEvidenceCount
    val uniquePropertyNames = filtered.asSequence().filterNot { it.section == "Vulkan Query Safety" }.map { it.name }.distinct().count()
    val uniqueSafetyNames = filtered.asSequence().filter { it.section == "Vulkan Query Safety" }.map { it.name }.distinct().count()
    val limitResultCount = visibleLimits.size
    val pagedLimitSource = if (filter == "Limits" || filter == "All") visibleLimits else emptyList()
    val pagedPropertySource = if (filter == "Limits") emptyList() else filtered
    val totalRows = pagedLimitSource.size + pagedPropertySource.size
    val pageCount = maxOf(1, (totalRows + COLLECTION_PAGE_SIZE - 1) / COLLECTION_PAGE_SIZE)
    LaunchedEffect(query, filter) { page = 0 }
    LaunchedEffect(totalRows) { page = page.coerceIn(0, pageCount - 1) }
    val pageStart = page * COLLECTION_PAGE_SIZE
    val pageEnd = minOf(pageStart + COLLECTION_PAGE_SIZE, totalRows)
    val limitStart = pageStart.coerceAtMost(pagedLimitSource.size)
    val limitEnd = pageEnd.coerceAtMost(pagedLimitSource.size)
    val propertyStart = (pageStart - pagedLimitSource.size).coerceAtLeast(0).coerceAtMost(pagedPropertySource.size)
    val propertyEnd = (pageEnd - pagedLimitSource.size).coerceAtLeast(0).coerceAtMost(pagedPropertySource.size)
    val pageLimits = if (limitEnd > limitStart) pagedLimitSource.subList(limitStart, limitEnd) else emptyList()
    val pageProperties = if (propertyEnd > propertyStart) pagedPropertySource.subList(propertyStart, propertyEnd) else emptyList()
    VulkanLazyPage(verticalSpacing = 10.dp) {
        item {
            CapabilitySectionCard("Properties & limits explorer") {
                Text("Physical-device properties and limits are shown only from runtime Vulkan® queries. Advanced query groups keep their explicit availability state.", color = ComposeColor(0xFFB6ACAE), style = MaterialTheme.typography.bodySmall)
                ExpressiveSearchField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), placeholderText = "Search properties and limits…")
                ExpressiveFilterBar(sections, sections.indexOf(filter).coerceAtLeast(0)) { filter = sections[it] }
            }
        }
        item {
            val metrics = when (filter) {
                "Limits" -> listOf("Limits" to limitResultCount.toString())
                "All" -> listOf(
                    "Evidence rows" to evidenceResultCount.toString(),
                    "Property / query" to propertyResultCount.toString(),
                    "Safety diagnostics" to safetyEvidenceCount.toString(),
                    "Unique names" to uniquePropertyNames.toString(),
                    "Limits" to limitResultCount.toString()
                )
                "Vulkan Query Safety" -> listOf("Safety diagnostics" to safetyEvidenceCount.toString(), "Unique diagnostics" to uniqueSafetyNames.toString())
                else -> listOf("Property / query" to propertyResultCount.toString(), "Unique names" to uniquePropertyNames.toString())
            } + listOf("Showing" to if (totalRows == 0) "0" else "${pageStart + 1}-$pageEnd")
            ExpressiveMetricGrid(metrics)
        }
        stickyCollectionPager(totalItems = totalRows, currentPage = page, onPageChange = { page = it })
        pageLimits.groupBy { limitCategory(it.first) }.forEach { (category, entries) ->
            item {
                CapabilitySectionCard(category) {
                    entries.forEach { (name, value) -> CapabilityKeyValue(name, value) }
                }
            }
        }
        if (filter != "Limits") {
            if (filter == "Core 1.4") {
                val status = device?.vulkan14Status ?: "unavailable"
                val message = when (status) {
                    "not_applicable" -> "Vulkan 1.4 is not exposed by this physical device. Device API: ${device?.apiVersion ?: "Unknown"}."
                    "unavailable" -> device?.vulkan14Reason?.ifBlank { "The Vulkan 1.4 property/feature query is unavailable on this device or Vulkan stack." } ?: "Vulkan 1.4 query status unavailable."
                    else -> "Vulkan 1.4 properties were queried from the active physical device."
                }
                if (properties.none { it.section == "Core 1.4" }) {
                    item { CapabilitySectionCard("Vulkan® 1.4 status") { Text(message, color = evidenceStateAccent(status)) } }
                }
            }
            itemsIndexed(pageProperties, key = { index, property -> "${property.section}:${property.name}:${propertyStart + index}" }) { _, property ->
                CapabilityItemCard {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(property.name, fontWeight = FontWeight.Medium)
                        Text(property.section, color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.labelSmall)
                        Text(property.value, color = ComposeColor(0xFFD8D8D8), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        if (totalRows == 0) item { EmptyState("No matching properties or limits") }
    }
}

internal fun limitCategory(name: String): String = when {
    name.contains("Image", true) || name.contains("Framebuffer", true) || name.contains("Attachment", true) -> "Images and framebuffer"
    name.contains("Shader", true) || name.contains("Tessellation", true) || name.contains("Geometry", true) -> "Shader and pipeline"
    name.contains("Compute", true) -> "Compute"
    name.contains("Descriptor", true) || name.contains("Sampler", true) -> "Descriptors and samplers"
    name.contains("Memory", true) || name.contains("Allocation", true) || name.contains("Buffer", true) -> "Memory and buffers"
    name.contains("Viewport", true) || name.contains("Scissor", true) || name.contains("View", true) -> "Rasterization and views"
    name.contains("Draw", true) || name.contains("Vertex", true) || name.contains("Index", true) -> "Drawing"
    name.contains("Timestamp", true) || name.contains("Query", true) -> "Queries and timestamps"
    else -> "Other limits"
}

internal fun featureSource(name: String): String = when {
    name.startsWith("Vulkan 1.1 · ") -> "Core 1.1"
    name.startsWith("Vulkan 1.2 · ") -> "Core 1.2"
    name.startsWith("Vulkan 1.3 · ") -> "Core 1.3"
    name.startsWith("Vulkan 1.4 · ") -> "Core 1.4"
    name.contains(" · ") -> name.substringBefore(" · ").ifBlank { "Extension" }
    else -> "Core 1.0"
}

internal fun featureNameOnly(name: String): String = name.substringAfter(" · ", name)

internal fun rawBitsSuffix(bits: Long, known: Set<Long>): String {
    var remaining = bits
    known.forEach { remaining = remaining and it.inv() }
    return if (remaining != 0L) " | UNKNOWN_BITS=0x${remaining.toULong().toString(16).uppercase()}" else ""
}

internal fun memoryHeapFlags(bits: Long): String = canonicalFlagNames(bits, listOf(
    0x1L to "VK_MEMORY_HEAP_DEVICE_LOCAL_BIT",
    0x2L to "VK_MEMORY_HEAP_MULTI_INSTANCE_BIT",
    0x8L to "VK_MEMORY_HEAP_TILE_MEMORY_BIT_QCOM"
))

internal fun memoryTypeFlags(bits: Long): String = canonicalFlagNames(bits, listOf(
    0x1L to "VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT",
    0x2L to "VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT",
    0x4L to "VK_MEMORY_PROPERTY_HOST_COHERENT_BIT",
    0x8L to "VK_MEMORY_PROPERTY_HOST_CACHED_BIT",
    0x10L to "VK_MEMORY_PROPERTY_LAZILY_ALLOCATED_BIT",
    0x20L to "VK_MEMORY_PROPERTY_PROTECTED_BIT",
    0x40L to "VK_MEMORY_PROPERTY_DEVICE_COHERENT_BIT_AMD",
    0x80L to "VK_MEMORY_PROPERTY_DEVICE_UNCACHED_BIT_AMD",
    0x100L to "VK_MEMORY_PROPERTY_RDMA_CAPABLE_BIT_NV"
))

internal fun queueCapabilityFlags(bits: Long): String {
    if (bits == 0L) return "0 · no VkQueueFlagBits reported"
    return canonicalFlagNames(bits, listOf(
        0x1L to "VK_QUEUE_GRAPHICS_BIT", 0x2L to "VK_QUEUE_COMPUTE_BIT", 0x4L to "VK_QUEUE_TRANSFER_BIT",
        0x8L to "VK_QUEUE_SPARSE_BINDING_BIT", 0x10L to "VK_QUEUE_PROTECTED_BIT", 0x20L to "VK_QUEUE_VIDEO_DECODE_BIT_KHR",
        0x40L to "VK_QUEUE_VIDEO_ENCODE_BIT_KHR", 0x100L to "VK_QUEUE_OPTICAL_FLOW_BIT_NV", 0x400L to "VK_QUEUE_DATA_GRAPH_BIT_ARM"
    ))
}

internal fun videoCodecOperationFlags(bits: Long): String {
    if (bits == 0L) return "VK_VIDEO_CODEC_OPERATION_NONE_KHR"
    return canonicalFlagNames(bits, listOf(
        0x1L to "VK_VIDEO_CODEC_OPERATION_DECODE_H264_BIT_KHR", 0x2L to "VK_VIDEO_CODEC_OPERATION_DECODE_H265_BIT_KHR",
        0x4L to "VK_VIDEO_CODEC_OPERATION_DECODE_AV1_BIT_KHR", 0x8L to "VK_VIDEO_CODEC_OPERATION_DECODE_VP9_BIT_KHR",
        0x10000L to "VK_VIDEO_CODEC_OPERATION_ENCODE_H264_BIT_KHR", 0x20000L to "VK_VIDEO_CODEC_OPERATION_ENCODE_H265_BIT_KHR",
        0x40000L to "VK_VIDEO_CODEC_OPERATION_ENCODE_AV1_BIT_KHR"
    ))
}

internal fun queueVideoCodecEvidenceRetained(queue: QueueEntry): Boolean = queue.videoCodecQueryStatus == "available" || queue.videoCodecQueryStatus == "incomplete"

internal fun queueVideoCodecQueryState(queue: QueueEntry): String = when (queue.videoCodecQueryStatus) {
    "available" -> "Available"
    "incomplete" -> "Incomplete${queue.videoCodecQueryReason.takeIf { it.isNotBlank() }?.let { " · $it" } ?: ""}"
    "not_applicable" -> "Not applicable${queue.videoCodecQueryReason.takeIf { it.isNotBlank() }?.let { " · $it" } ?: ""}"
    "unavailable" -> "Unavailable${queue.videoCodecQueryReason.takeIf { it.isNotBlank() }?.let { " · $it" } ?: ""}"
    else -> "Unknown · VkQueueFamilyVideoPropertiesKHR query evidence is not available"
}

internal fun parseUnsignedHexLong(value: String?): Long? = value?.let { runCatching { java.lang.Long.parseUnsignedLong(it, 16) }.getOrNull() }

internal fun canonicalFlagNames(bits: Long, names: List<Pair<Long, String>>): String {
    if (bits == 0L) return "0"
    val known = names.filter { (bit, _) -> (bits and bit) != 0L }.map { it.second }
    return known.joinToString(" | ").ifBlank { "0" } + rawBitsSuffix(bits, names.map { it.first }.toSet())
}


internal fun surfaceTransformFlags(bits: Long): String = canonicalFlagNames(bits, listOf(
    0x1L to "VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR", 0x2L to "VK_SURFACE_TRANSFORM_ROTATE_90_BIT_KHR",
    0x4L to "VK_SURFACE_TRANSFORM_ROTATE_180_BIT_KHR", 0x8L to "VK_SURFACE_TRANSFORM_ROTATE_270_BIT_KHR",
    0x10L to "VK_SURFACE_TRANSFORM_HORIZONTAL_MIRROR_BIT_KHR", 0x20L to "VK_SURFACE_TRANSFORM_HORIZONTAL_MIRROR_ROTATE_90_BIT_KHR",
    0x40L to "VK_SURFACE_TRANSFORM_HORIZONTAL_MIRROR_ROTATE_180_BIT_KHR", 0x80L to "VK_SURFACE_TRANSFORM_HORIZONTAL_MIRROR_ROTATE_270_BIT_KHR",
    0x100L to "VK_SURFACE_TRANSFORM_INHERIT_BIT_KHR"
))

internal fun compositeAlphaFlags(bits: Long): String = canonicalFlagNames(bits, listOf(
    0x1L to "VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR", 0x2L to "VK_COMPOSITE_ALPHA_PRE_MULTIPLIED_BIT_KHR",
    0x4L to "VK_COMPOSITE_ALPHA_POST_MULTIPLIED_BIT_KHR", 0x8L to "VK_COMPOSITE_ALPHA_INHERIT_BIT_KHR"
))

internal fun imageUsageFlags(bits: Long): String = canonicalFlagNames(bits, listOf(
    0x1L to "VK_IMAGE_USAGE_TRANSFER_SRC_BIT", 0x2L to "VK_IMAGE_USAGE_TRANSFER_DST_BIT",
    0x4L to "VK_IMAGE_USAGE_SAMPLED_BIT", 0x8L to "VK_IMAGE_USAGE_STORAGE_BIT",
    0x10L to "VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT", 0x20L to "VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT",
    0x40L to "VK_IMAGE_USAGE_TRANSIENT_ATTACHMENT_BIT", 0x80L to "VK_IMAGE_USAGE_INPUT_ATTACHMENT_BIT",
    0x100L to "VK_IMAGE_USAGE_FRAGMENT_SHADING_RATE_ATTACHMENT_BIT_KHR", 0x200L to "VK_IMAGE_USAGE_FRAGMENT_DENSITY_MAP_BIT_EXT",
    0x400L to "VK_IMAGE_USAGE_VIDEO_DECODE_DST_BIT_KHR", 0x800L to "VK_IMAGE_USAGE_VIDEO_DECODE_SRC_BIT_KHR",
    0x1000L to "VK_IMAGE_USAGE_VIDEO_DECODE_DPB_BIT_KHR", 0x2000L to "VK_IMAGE_USAGE_VIDEO_ENCODE_DST_BIT_KHR",
    0x4000L to "VK_IMAGE_USAGE_VIDEO_ENCODE_SRC_BIT_KHR", 0x8000L to "VK_IMAGE_USAGE_VIDEO_ENCODE_DPB_BIT_KHR",
    0x40000L to "VK_IMAGE_USAGE_INVOCATION_MASK_BIT_HUAWEI", 0x80000L to "VK_IMAGE_USAGE_ATTACHMENT_FEEDBACK_LOOP_BIT_EXT",
    0x100000L to "VK_IMAGE_USAGE_SAMPLE_WEIGHT_BIT_QCOM", 0x200000L to "VK_IMAGE_USAGE_SAMPLE_BLOCK_MATCH_BIT_QCOM",
    0x400000L to "VK_IMAGE_USAGE_HOST_TRANSFER_BIT", 0x800000L to "VK_IMAGE_USAGE_TENSOR_ALIASING_BIT_ARM",
    0x2000000L to "VK_IMAGE_USAGE_VIDEO_ENCODE_QUANTIZATION_DELTA_MAP_BIT_KHR", 0x4000000L to "VK_IMAGE_USAGE_VIDEO_ENCODE_EMPHASIS_MAP_BIT_KHR",
    0x8000000L to "VK_IMAGE_USAGE_TILE_MEMORY_BIT_QCOM"
))

internal fun canonicalSurfaceCapabilityValue(name: String, value: String): String {
    if (name == "capabilityResult") return value.toIntOrNull()?.let(::vkResultText) ?: value
    val bits = value.toLongOrNull() ?: return value
    val canonical = when (name) {
        "supportedTransforms", "currentTransform" -> surfaceTransformFlags(bits)
        "supportedCompositeAlpha" -> compositeAlphaFlags(bits)
        "supportedUsageFlags" -> imageUsageFlags(bits)
        else -> return value
    }
    return "$value · $canonical"
}
internal fun formatFeatureFlags(bits: Long): String = canonicalFlagNames(bits, listOf(
    0x1L to "VK_FORMAT_FEATURE_2_SAMPLED_IMAGE_BIT", 0x2L to "VK_FORMAT_FEATURE_2_STORAGE_IMAGE_BIT",
    0x4L to "VK_FORMAT_FEATURE_2_STORAGE_IMAGE_ATOMIC_BIT", 0x8L to "VK_FORMAT_FEATURE_2_UNIFORM_TEXEL_BUFFER_BIT",
    0x10L to "VK_FORMAT_FEATURE_2_STORAGE_TEXEL_BUFFER_BIT", 0x20L to "VK_FORMAT_FEATURE_2_STORAGE_TEXEL_BUFFER_ATOMIC_BIT",
    0x40L to "VK_FORMAT_FEATURE_2_VERTEX_BUFFER_BIT", 0x80L to "VK_FORMAT_FEATURE_2_COLOR_ATTACHMENT_BIT",
    0x100L to "VK_FORMAT_FEATURE_2_COLOR_ATTACHMENT_BLEND_BIT", 0x200L to "VK_FORMAT_FEATURE_2_DEPTH_STENCIL_ATTACHMENT_BIT",
    0x400L to "VK_FORMAT_FEATURE_2_BLIT_SRC_BIT", 0x800L to "VK_FORMAT_FEATURE_2_BLIT_DST_BIT",
    0x1000L to "VK_FORMAT_FEATURE_2_SAMPLED_IMAGE_FILTER_LINEAR_BIT", 0x2000L to "VK_FORMAT_FEATURE_2_SAMPLED_IMAGE_FILTER_CUBIC_BIT",
    0x4000L to "VK_FORMAT_FEATURE_2_TRANSFER_SRC_BIT", 0x8000L to "VK_FORMAT_FEATURE_2_TRANSFER_DST_BIT",
    0x10000L to "VK_FORMAT_FEATURE_2_SAMPLED_IMAGE_FILTER_MINMAX_BIT", 0x20000L to "VK_FORMAT_FEATURE_2_MIDPOINT_CHROMA_SAMPLES_BIT",
    0x40000L to "VK_FORMAT_FEATURE_2_SAMPLED_IMAGE_YCBCR_CONVERSION_LINEAR_FILTER_BIT", 0x80000L to "VK_FORMAT_FEATURE_2_SAMPLED_IMAGE_YCBCR_CONVERSION_SEPARATE_RECONSTRUCTION_FILTER_BIT",
    0x100000L to "VK_FORMAT_FEATURE_2_SAMPLED_IMAGE_YCBCR_CONVERSION_CHROMA_RECONSTRUCTION_EXPLICIT_BIT", 0x200000L to "VK_FORMAT_FEATURE_2_SAMPLED_IMAGE_YCBCR_CONVERSION_CHROMA_RECONSTRUCTION_EXPLICIT_FORCEABLE_BIT",
    0x400000L to "VK_FORMAT_FEATURE_2_DISJOINT_BIT", 0x800000L to "VK_FORMAT_FEATURE_2_COSITED_CHROMA_SAMPLES_BIT",
    0x1000000L to "VK_FORMAT_FEATURE_2_FRAGMENT_DENSITY_MAP_BIT_EXT", 0x2000000L to "VK_FORMAT_FEATURE_2_VIDEO_DECODE_OUTPUT_BIT_KHR",
    0x4000000L to "VK_FORMAT_FEATURE_2_VIDEO_DECODE_DPB_BIT_KHR", 0x8000000L to "VK_FORMAT_FEATURE_2_VIDEO_ENCODE_INPUT_BIT_KHR",
    0x10000000L to "VK_FORMAT_FEATURE_2_VIDEO_ENCODE_DPB_BIT_KHR", 0x20000000L to "VK_FORMAT_FEATURE_2_ACCELERATION_STRUCTURE_VERTEX_BUFFER_BIT_KHR",
    0x40000000L to "VK_FORMAT_FEATURE_2_FRAGMENT_SHADING_RATE_ATTACHMENT_BIT_KHR", 0x80000000L to "VK_FORMAT_FEATURE_2_STORAGE_READ_WITHOUT_FORMAT_BIT",
    0x100000000L to "VK_FORMAT_FEATURE_2_STORAGE_WRITE_WITHOUT_FORMAT_BIT", 0x200000000L to "VK_FORMAT_FEATURE_2_SAMPLED_IMAGE_DEPTH_COMPARISON_BIT",
    0x400000000L to "VK_FORMAT_FEATURE_2_WEIGHT_IMAGE_BIT_QCOM", 0x800000000L to "VK_FORMAT_FEATURE_2_WEIGHT_SAMPLED_IMAGE_BIT_QCOM",
    0x1000000000L to "VK_FORMAT_FEATURE_2_BLOCK_MATCHING_BIT_QCOM", 0x2000000000L to "VK_FORMAT_FEATURE_2_BOX_FILTER_SAMPLED_BIT_QCOM",
    0x4000000000L to "VK_FORMAT_FEATURE_2_LINEAR_COLOR_ATTACHMENT_BIT_NV", 0x8000000000L to "VK_FORMAT_FEATURE_2_TENSOR_SHADER_BIT_ARM",
    0x10000000000L to "VK_FORMAT_FEATURE_2_OPTICAL_FLOW_IMAGE_BIT_NV", 0x20000000000L to "VK_FORMAT_FEATURE_2_OPTICAL_FLOW_VECTOR_BIT_NV",
    0x40000000000L to "VK_FORMAT_FEATURE_2_OPTICAL_FLOW_COST_BIT_NV", 0x80000000000L to "VK_FORMAT_FEATURE_2_TENSOR_IMAGE_ALIASING_BIT_ARM",
    0x100000000000L to "VK_FORMAT_FEATURE_2_BLOCK_MATCHING_SXD_BIT_QCOM", 0x200000000000L to "VK_FORMAT_FEATURE_2_SAMPLED_IMAGE_FILTER_LINEAR_2D_BIT_IMG",
    0x400000000000L to "VK_FORMAT_FEATURE_2_HOST_IMAGE_TRANSFER_BIT", 0x1000000000000L to "VK_FORMAT_FEATURE_2_TENSOR_DATA_GRAPH_BIT_ARM",
    0x2000000000000L to "VK_FORMAT_FEATURE_2_VIDEO_ENCODE_QUANTIZATION_DELTA_MAP_BIT_KHR", 0x4000000000000L to "VK_FORMAT_FEATURE_2_VIDEO_ENCODE_EMPHASIS_MAP_BIT_KHR",
    0x8000000000000L to "VK_FORMAT_FEATURE_2_ACCELERATION_STRUCTURE_RADIUS_BUFFER_BIT_NV", 0x10000000000000L to "VK_FORMAT_FEATURE_2_DEPTH_COPY_ON_COMPUTE_QUEUE_BIT_KHR",
    0x20000000000000L to "VK_FORMAT_FEATURE_2_DEPTH_COPY_ON_TRANSFER_QUEUE_BIT_KHR", 0x40000000000000L to "VK_FORMAT_FEATURE_2_STENCIL_COPY_ON_COMPUTE_QUEUE_BIT_KHR",
    0x80000000000000L to "VK_FORMAT_FEATURE_2_STENCIL_COPY_ON_TRANSFER_QUEUE_BIT_KHR", 0x100000000000000L to "VK_FORMAT_FEATURE_2_DATA_GRAPH_OPTICAL_FLOW_IMAGE_BIT_ARM",
    0x200000000000000L to "VK_FORMAT_FEATURE_2_DATA_GRAPH_OPTICAL_FLOW_VECTOR_BIT_ARM", 0x400000000000000L to "VK_FORMAT_FEATURE_2_DATA_GRAPH_OPTICAL_FLOW_COST_BIT_ARM",
    0x800000000000000L to "VK_FORMAT_FEATURE_2_COPY_IMAGE_INDIRECT_DST_BIT_KHR"
))

internal data class ProfileCheckCounts(
    val total: Int = 0,
    val met: Int = 0,
    val failed: Int = 0,
    val unknown: Int = 0
)

internal class MutableProfileCheckCounts {
    var total: Int = 0
    var met: Int = 0
    var failed: Int = 0
    var unknown: Int = 0

    fun record(state: String) {
        total += 1
        when (state) {
            "PASS" -> met += 1
            "FAIL" -> failed += 1
            else -> unknown += 1
        }
    }

    fun add(other: MutableProfileCheckCounts) {
        total += other.total
        met += other.met
        failed += other.failed
        unknown += other.unknown
    }

    fun add(other: ProfileCheckCounts) {
        total += other.total
        met += other.met
        failed += other.failed
        unknown += other.unknown
    }

    fun snapshot(): ProfileCheckCounts = ProfileCheckCounts(total, met, failed, unknown)
}

internal data class ProfileResult(
    val name: String,
    val revision: String,
    val status: String,
    val missingExtensions: List<String>,
    val unknownExtensions: List<String>,
    val missingFeatures: List<String>,
    val unknownFeatures: List<String>,
    val failingLimits: List<String>,
    val unknownLimits: List<String>,
    val failingBooleanLimits: List<String> = emptyList(),
    val unknownBooleanLimits: List<String> = emptyList(),
    val coverageNote: String = "",
    val minimumApiVersion: String = "",
    val failingFormats: List<String> = emptyList(),
    val unknownFormats: List<String> = emptyList(),
    val failingRequirementGroups: List<String> = emptyList(),
    val unknownRequirementGroups: List<String> = emptyList(),
    val checkedRequirementCount: Int = 0,
    val metRequirementCount: Int = 0,
    val failedRequirementCount: Int = 0,
    val unknownRequirementCount: Int = 0,
    val extensionChecks: ProfileCheckCounts = ProfileCheckCounts(),
    val featureChecks: ProfileCheckCounts = ProfileCheckCounts(),
    val propertyChecks: ProfileCheckCounts = ProfileCheckCounts(),
    val formatChecks: ProfileCheckCounts = ProfileCheckCounts(),
    val groupChecks: ProfileCheckCounts = ProfileCheckCounts()
)

internal enum class ProfilePropertyComparison { MINIMUM, MAXIMUM, EQUAL_BOOL, BITMASK_CONTAINS, VECTOR_MINIMUM }

internal data class ProfileFeatureRequirement(val structure: String, val member: String)
internal data class ProfilePropertyRequirement(val structure: String, val member: String, val comparison: ProfilePropertyComparison, val requiredValue: String)
internal data class ProfileFormatRequirement(val format: String, val linearMask: Long = 0L, val optimalMask: Long = 0L, val bufferMask: Long = 0L)
internal data class ProfileCapabilityRequirement(
    val extensions: List<String> = emptyList(),
    val features: List<ProfileFeatureRequirement> = emptyList(),
    val properties: List<ProfilePropertyRequirement> = emptyList(),
    val formats: List<ProfileFormatRequirement> = emptyList()
)
internal data class ProfileAlternativeGroup(val label: String, val alternatives: List<ProfileCapabilityRequirement>)
internal data class ProfileRequirements(
    val name: String,
    val revision: String,
    val minApiVersion: String,
    val capability: ProfileCapabilityRequirement,
    val requiredProfiles: List<String> = emptyList(),
    val alternativeGroups: List<ProfileAlternativeGroup> = emptyList(),
    val completeCoverage: Boolean = false,
    val coverageNote: String
)
internal data class ProfileCatalogEntry(val name: String, val revision: String, val minApiVersion: String, val coverageNote: String)
internal data class ProfileEvidenceResult(
    val missingExtensions: MutableList<String> = mutableListOf(),
    val unknownExtensions: MutableList<String> = mutableListOf(),
    val missingFeatures: MutableList<String> = mutableListOf(),
    val unknownFeatures: MutableList<String> = mutableListOf(),
    val failingProperties: MutableList<String> = mutableListOf(),
    val unknownProperties: MutableList<String> = mutableListOf(),
    val failingFormats: MutableList<String> = mutableListOf(),
    val unknownFormats: MutableList<String> = mutableListOf(),
    val totalChecks: MutableProfileCheckCounts = MutableProfileCheckCounts(),
    val extensionChecks: MutableProfileCheckCounts = MutableProfileCheckCounts(),
    val featureChecks: MutableProfileCheckCounts = MutableProfileCheckCounts(),
    val propertyChecks: MutableProfileCheckCounts = MutableProfileCheckCounts(),
    val formatChecks: MutableProfileCheckCounts = MutableProfileCheckCounts(),
    val groupChecks: MutableProfileCheckCounts = MutableProfileCheckCounts()
)

internal fun ProfileEvidenceResult.recordCheck(category: MutableProfileCheckCounts, state: String) {
    totalChecks.record(state)
    category.record(state)
}

internal fun ProfileEvidenceResult.addChecks(other: ProfileEvidenceResult) {
    totalChecks.add(other.totalChecks)
    extensionChecks.add(other.extensionChecks)
    featureChecks.add(other.featureChecks)
    propertyChecks.add(other.propertyChecks)
    formatChecks.add(other.formatChecks)
    groupChecks.add(other.groupChecks)
}

internal fun ProfileEvidenceResult.addChecks(other: ProfileResult) {
    totalChecks.add(ProfileCheckCounts(other.checkedRequirementCount, other.metRequirementCount, other.failedRequirementCount, other.unknownRequirementCount))
    extensionChecks.add(other.extensionChecks)
    featureChecks.add(other.featureChecks)
    propertyChecks.add(other.propertyChecks)
    formatChecks.add(other.formatChecks)
    groupChecks.add(other.groupChecks)
}

internal val PROFILE_INSTANCE_EXTENSIONS = setOf(
    "VK_KHR_surface", "VK_KHR_android_surface", "VK_KHR_get_physical_device_properties2", "VK_KHR_get_surface_capabilities2",
    "VK_KHR_device_group_creation", "VK_KHR_external_fence_capabilities", "VK_KHR_external_memory_capabilities", "VK_KHR_external_semaphore_capabilities",
    "VK_KHR_surface_maintenance1", "VK_EXT_debug_report", "VK_EXT_debug_utils", "VK_EXT_surface_maintenance1", "VK_EXT_swapchain_colorspace",
    "VK_GOOGLE_surfaceless_query"
)

internal fun profileFeatures(structure: String, vararg members: String): List<ProfileFeatureRequirement> = members.map { ProfileFeatureRequirement(structure, it) }
internal fun profileProperties(structure: String, comparison: ProfilePropertyComparison, vararg values: Pair<String, String>): List<ProfilePropertyRequirement> = values.map { ProfilePropertyRequirement(structure, it.first, comparison, it.second) }
internal fun profileCapability(
    extensions: List<String> = emptyList(),
    features: List<ProfileFeatureRequirement> = emptyList(),
    properties: List<ProfilePropertyRequirement> = emptyList(),
    formats: List<ProfileFormatRequirement> = emptyList()
) = ProfileCapabilityRequirement(extensions, features, properties, formats)

internal fun mergeProfileCapabilities(vararg capabilities: ProfileCapabilityRequirement): ProfileCapabilityRequirement = ProfileCapabilityRequirement(
    extensions = capabilities.flatMap { it.extensions }.distinct(),
    features = capabilities.flatMap { it.features }.distinct(),
    properties = capabilities.flatMap { it.properties }.distinct(),
    formats = capabilities.flatMap { it.formats }.distinct()
)

internal val ANDROID_4444_FORMAT_REQUIREMENTS = listOf(
    ProfileFormatRequirement("VK_FORMAT_A4B4G4R4_UNORM_PACK16_EXT", linearMask = 0xC880L, optimalMask = 0xCC81L),
    ProfileFormatRequirement("VK_FORMAT_A4R4G4B4_UNORM_PACK16_EXT", linearMask = 0xC880L, optimalMask = 0xCC81L)
)

internal fun android2025Requirements(): ProfileRequirements = ProfileRequirements(
    name = "VP_ANDROID_vulkan_profile_2025",
    revision = "r.2",
    minApiVersion = "1.1.128",
    capability = profileCapability(
        extensions = listOf(
            "VK_KHR_android_surface", "VK_KHR_bind_memory2", "VK_KHR_create_renderpass2", "VK_KHR_dedicated_allocation", "VK_KHR_descriptor_update_template",
            "VK_KHR_device_group", "VK_KHR_device_group_creation", "VK_KHR_driver_properties", "VK_KHR_external_fence", "VK_KHR_external_fence_capabilities",
            "VK_KHR_external_fence_fd", "VK_KHR_external_memory", "VK_KHR_external_memory_capabilities", "VK_KHR_external_memory_fd", "VK_KHR_external_semaphore",
            "VK_KHR_external_semaphore_capabilities", "VK_KHR_external_semaphore_fd", "VK_KHR_get_memory_requirements2", "VK_KHR_get_physical_device_properties2",
            "VK_KHR_get_surface_capabilities2", "VK_KHR_image_format_list", "VK_KHR_incremental_present", "VK_KHR_maintenance1", "VK_KHR_maintenance2", "VK_KHR_maintenance3",
            "VK_KHR_multiview", "VK_KHR_relaxed_block_layout", "VK_KHR_sampler_mirror_clamp_to_edge", "VK_KHR_sampler_ycbcr_conversion", "VK_KHR_shader_draw_parameters",
            "VK_KHR_shader_float16_int8", "VK_KHR_shader_float_controls", "VK_KHR_storage_buffer_storage_class", "VK_KHR_surface", "VK_KHR_swapchain", "VK_KHR_variable_pointers",
            "VK_KHR_vulkan_memory_model", "VK_ANDROID_external_memory_android_hardware_buffer", "VK_GOOGLE_display_timing", "VK_EXT_debug_report", "VK_EXT_host_query_reset",
            "VK_EXT_index_type_uint8", "VK_EXT_queue_family_foreign", "VK_EXT_scalar_block_layout", "VK_EXT_separate_stencil_usage", "VK_EXT_swapchain_colorspace"
        ),
        features = profileFeatures("VkPhysicalDeviceFeatures", "depthBiasClamp", "drawIndirectFirstInstance", "fragmentStoresAndAtomics", "fullDrawIndexUint32", "imageCubeArray", "independentBlend", "largePoints", "occlusionQueryPrecise", "robustBufferAccess", "sampleRateShading", "shaderInt16", "shaderSampledImageArrayDynamicIndexing", "shaderStorageBufferArrayDynamicIndexing", "shaderStorageImageArrayDynamicIndexing", "shaderStorageImageExtendedFormats", "shaderStorageImageReadWithoutFormat", "shaderUniformBufferArrayDynamicIndexing", "textureCompressionASTC_LDR", "textureCompressionETC2") +
            profileFeatures("VkPhysicalDeviceMultiviewFeatures", "multiview") +
            profileFeatures("VkPhysicalDeviceSamplerYcbcrConversionFeatures", "samplerYcbcrConversion") +
            profileFeatures("VkPhysicalDeviceShaderDrawParameterFeatures", "shaderDrawParameters") +
            profileFeatures("VkPhysicalDeviceVariablePointerFeatures", "variablePointers", "variablePointersStorageBuffer"),
        properties = profileProperties("VkPhysicalDeviceProperties", ProfilePropertyComparison.MAXIMUM,
            "bufferImageGranularity" to "4096", "minStorageBufferOffsetAlignment" to "256", "minTexelBufferOffsetAlignment" to "256", "minUniformBufferOffsetAlignment" to "256", "nonCoherentAtomSize" to "64", "optimalBufferCopyRowPitchAlignment" to "64", "optimalBufferCopyOffsetAlignment" to "64", "pointSizeGranularity" to "0.125") +
            profileProperties("VkPhysicalDeviceProperties", ProfilePropertyComparison.MINIMUM,
                "discreteQueuePriorities" to "2", "maxBoundDescriptorSets" to "4", "maxColorAttachments" to "8", "maxComputeSharedMemorySize" to "16384", "maxComputeWorkGroupInvocations" to "256", "maxDescriptorSetInputAttachments" to "8", "maxDescriptorSetSampledImages" to "256", "maxDescriptorSetSamplers" to "256", "maxDescriptorSetStorageBuffers" to "210", "maxDescriptorSetStorageBuffersDynamic" to "8", "maxDescriptorSetStorageImages" to "126", "maxDescriptorSetUniformBuffers" to "216", "maxDescriptorSetUniformBuffersDynamic" to "8", "maxFramebufferHeight" to "4096", "maxFramebufferLayers" to "256", "maxFramebufferWidth" to "4096", "maxFragmentCombinedOutputResources" to "8", "maxFragmentInputComponents" to "112", "maxFragmentOutputAttachments" to "8", "maxImageArrayLayers" to "256", "maxImageDimension1D" to "4096", "maxImageDimension2D" to "4096", "maxImageDimension3D" to "2048", "maxImageDimensionCube" to "4096", "maxMemoryAllocationCount" to "4096", "maxPerStageDescriptorInputAttachments" to "8", "maxPerStageDescriptorSampledImages" to "48", "maxPerStageDescriptorSamplers" to "32", "maxPerStageDescriptorStorageBuffers" to "35", "maxPerStageDescriptorStorageImages" to "8", "maxPerStageDescriptorUniformBuffers" to "36", "maxPerStageResources" to "224", "maxPushConstantsSize" to "128", "maxSamplerAllocationCount" to "4000", "maxSamplerLodBias" to "15", "maxStorageBufferRange" to "134217728", "maxTexelBufferElements" to "65536", "maxUniformBufferRange" to "65536", "maxVertexInputAttributeOffset" to "2047", "maxVertexInputAttributes" to "16", "maxVertexInputBindingStride" to "2048", "maxVertexInputBindings" to "16", "maxVertexOutputComponents" to "128", "mipmapPrecisionBits" to "4", "subPixelInterpolationOffsetBits" to "4", "subPixelPrecisionBits" to "4", "subTexelPrecisionBits" to "8") +
            profileProperties("VkPhysicalDeviceProperties", ProfilePropertyComparison.VECTOR_MINIMUM, "maxComputeWorkGroupCount" to "65535,65535,65535", "maxComputeWorkGroupSize" to "256,256,64", "maxViewportDimensions" to "4096,4096") +
            profileProperties("VkPhysicalDeviceProperties", ProfilePropertyComparison.EQUAL_BOOL, "standardSampleLocations" to "true") +
            profileProperties("VkPhysicalDeviceMultiviewProperties", ProfilePropertyComparison.MINIMUM, "maxMultiviewViewCount" to "6", "maxMultiviewInstanceIndex" to "134217727")
    ),
    completeCoverage = false,
    coverageNote = "Official r.2 extensions, feature structures and the safely directional scalar/vector property subset are mapped; the full official format/property matrix remains outside this release, so PASS is intentionally disabled"
)

internal fun android15Requirements(): ProfileRequirements = ProfileRequirements(
    name = "VP_ANDROID_15_requirements",
    revision = "r.2",
    minApiVersion = "1.3.273",
    capability = profileCapability(
        extensions = listOf("VK_KHR_maintenance5", "VK_KHR_shader_float16_int8", "VK_KHR_16bit_storage", "VK_KHR_vertex_attribute_divisor", "VK_EXT_custom_border_color", "VK_EXT_device_memory_report", "VK_EXT_external_memory_acquire_unmodified", "VK_EXT_index_type_uint8", "VK_EXT_load_store_op_none", "VK_EXT_primitive_topology_list_restart", "VK_EXT_provoking_vertex", "VK_EXT_scalar_block_layout", "VK_EXT_surface_maintenance1", "VK_EXT_swapchain_maintenance1", "VK_EXT_4444_formats", "VK_ANDROID_external_format_resolve", "VK_GOOGLE_surfaceless_query"),
        features = profileFeatures("VkPhysicalDeviceFeatures", "drawIndirectFirstInstance", "shaderImageGatherExtended", "shaderStorageImageExtendedFormats", "shaderStorageImageReadWithoutFormat", "shaderStorageImageWriteWithoutFormat", "samplerAnisotropy") +
            profileFeatures("VkPhysicalDeviceVulkan12Features", "shaderFloat16", "shaderInt8") +
            profileFeatures("VkPhysicalDeviceCustomBorderColorFeaturesEXT", "customBorderColors") +
            profileFeatures("VkPhysicalDevicePrimitiveTopologyListRestartFeaturesEXT", "primitiveTopologyListRestart") +
            profileFeatures("VkPhysicalDeviceProvokingVertexFeaturesEXT", "provokingVertexLast") +
            profileFeatures("VkPhysicalDeviceIndexTypeUint8FeaturesEXT", "indexTypeUint8") +
            profileFeatures("VkPhysicalDeviceVertexAttributeDivisorFeaturesKHR", "vertexAttributeInstanceRateDivisor") +
            profileFeatures("VkPhysicalDeviceSamplerYcbcrConversionFeatures", "samplerYcbcrConversion") +
            profileFeatures("VkPhysicalDeviceShaderFloat16Int8Features", "shaderFloat16", "shaderInt8") +
            profileFeatures("VkPhysicalDeviceShaderSubgroupExtendedTypesFeatures", "shaderSubgroupExtendedTypes") +
            profileFeatures("VkPhysicalDevice8BitStorageFeatures", "storageBuffer8BitAccess") +
            profileFeatures("VkPhysicalDevice16BitStorageFeatures", "storageBuffer16BitAccess"),
        properties = profileProperties("VkPhysicalDeviceProperties", ProfilePropertyComparison.MINIMUM, "maxPerStageDescriptorUniformBuffers" to "13", "maxPerStageDescriptorStorageBuffers" to "12", "maxColorAttachments" to "8", "maxPerStageDescriptorSampledImages" to "128", "maxPerStageDescriptorSamplers" to "128") +
            profileProperties("VkPhysicalDeviceVulkan11Properties", ProfilePropertyComparison.BITMASK_CONTAINS, "subgroupSupportedOperations" to "0x3F"),
        formats = ANDROID_4444_FORMAT_REQUIREMENTS
    ),
    requiredProfiles = listOf("VP_ANDROID_vulkan_profile_2022"),
    alternativeGroups = listOf(
        ProfileAlternativeGroup("primitivesGeneratedQuery OR pipelineStatisticsQuery", listOf(
            profileCapability(extensions = listOf("VK_EXT_primitives_generated_query"), features = profileFeatures("VkPhysicalDevicePrimitivesGeneratedQueryFeaturesEXT", "primitivesGeneratedQuery")),
            profileCapability(features = profileFeatures("VkPhysicalDeviceFeatures", "pipelineStatisticsQuery"))
        )),
        ProfileAlternativeGroup("software OR hardware Bresenham lines", listOf(
            profileCapability(extensions = listOf("VK_EXT_line_rasterization"), features = profileFeatures("VkPhysicalDeviceLineRasterizationFeaturesEXT", "bresenhamLines")),
            profileCapability(extensions = listOf("VK_IMG_relaxed_line_rasterization"), features = profileFeatures("VkPhysicalDeviceRelaxedLineRasterizationFeaturesIMG", "relaxedLineRasterization"))
        ))
    ),
    completeCoverage = false,
    coverageNote = "Official r.2 direct MUST extensions/features/properties/formats and both OR capability groups are mapped; required VP_ANDROID_vulkan_profile_2022 remains catalog-only, so inherited profile coverage is incomplete and PASS is disabled"
)

internal fun android16Requirements(): ProfileRequirements = ProfileRequirements(
    name = "VP_ANDROID_16_requirements",
    revision = "r.7",
    minApiVersion = "1.3.276",
    capability = profileCapability(
        extensions = listOf("VK_KHR_8bit_storage", "VK_KHR_load_store_op_none", "VK_KHR_maintenance6", "VK_KHR_map_memory2", "VK_KHR_shader_expect_assume", "VK_KHR_shader_float_controls2", "VK_KHR_shader_maximal_reconvergence", "VK_KHR_shader_subgroup_rotate", "VK_KHR_shader_subgroup_uniform_control_flow", "VK_KHR_swapchain_mutable_format", "VK_EXT_host_image_copy", "VK_EXT_image_2d_view_of_3d", "VK_EXT_pipeline_protected_access", "VK_EXT_pipeline_robustness", "VK_EXT_transform_feedback"),
        features = profileFeatures("VkPhysicalDeviceFeatures", "fullDrawIndexUint32", "shaderInt16") +
            profileFeatures("VkPhysicalDeviceVulkan12Features", "samplerMirrorClampToEdge", "scalarBlockLayout") +
            profileFeatures("VkPhysicalDeviceProtectedMemoryFeatures", "protectedMemory") +
            profileFeatures("VkPhysicalDeviceShaderIntegerDotProductFeatures", "shaderIntegerDotProduct") +
            profileFeatures("VkPhysicalDeviceTransformFeedbackFeaturesEXT", "transformFeedback") +
            profileFeatures("VkPhysicalDeviceImage2DViewOf3DFeaturesEXT", "image2DViewOf3D") +
            profileFeatures("VkPhysicalDeviceShaderSubgroupUniformControlFlowFeaturesKHR", "shaderSubgroupUniformControlFlow"),
        properties = profileProperties("VkPhysicalDeviceProperties", ProfilePropertyComparison.MAXIMUM, "bufferImageGranularity" to "4096", "lineWidthGranularity" to "0.5", "pointSizeGranularity" to "0.125") +
            profileProperties("VkPhysicalDeviceProperties", ProfilePropertyComparison.MINIMUM, "maxColorAttachments" to "8", "maxComputeWorkGroupInvocations" to "256", "maxImageArrayLayers" to "2048", "maxImageDimension1D" to "8192", "maxImageDimension2D" to "8192", "maxImageDimensionCube" to "8192", "maxDescriptorSetStorageBuffers" to "96", "maxDescriptorSetUniformBuffers" to "90", "maxFragmentCombinedOutputResources" to "16", "maxPerStageDescriptorUniformBuffers" to "15", "maxPerStageResources" to "200", "maxSamplerLodBias" to "14", "maxUniformBufferRange" to "65536", "maxVertexOutputComponents" to "72", "mipmapPrecisionBits" to "6", "subTexelPrecisionBits" to "8") +
            profileProperties("VkPhysicalDeviceProperties", ProfilePropertyComparison.VECTOR_MINIMUM, "maxComputeWorkGroupSize" to "256,256,64") +
            profileProperties("VkPhysicalDeviceProperties", ProfilePropertyComparison.EQUAL_BOOL, "standardSampleLocations" to "true", "timestampComputeAndGraphics" to "true") +
            profileProperties("VkPhysicalDeviceFloatControlsProperties", ProfilePropertyComparison.EQUAL_BOOL, "shaderSignedZeroInfNanPreserveFloat16" to "true", "shaderSignedZeroInfNanPreserveFloat32" to "true") +
            profileProperties("VkPhysicalDeviceVulkan11Properties", ProfilePropertyComparison.BITMASK_CONTAINS, "subgroupSupportedStages" to "0x20")
    ),
    requiredProfiles = listOf("VP_ANDROID_15_requirements"),
    alternativeGroups = listOf(ProfileAlternativeGroup("multisampled-to-single-sampled OR shader stencil export", listOf(
        profileCapability(extensions = listOf("VK_EXT_multisampled_render_to_single_sampled")),
        profileCapability(extensions = listOf("VK_EXT_shader_stencil_export"))
    ))),
    completeCoverage = false,
    coverageNote = "Official r.7 direct MUST extensions/features/properties and its OR capability group are mapped with Android 15 inheritance; the Android 15 parent remains transitively incomplete because its Android 2022 parent is catalog-only, so PASS is disabled"
)

internal fun android17Requirements(): ProfileRequirements = ProfileRequirements(
    name = "VP_ANDROID_17_requirements",
    revision = "r.4",
    minApiVersion = "1.4.335",
    capability = profileCapability(
        extensions = listOf("VK_KHR_maintenance7", "VK_KHR_maintenance8", "VK_KHR_maintenance9", "VK_KHR_pipeline_binary", "VK_KHR_pipeline_library", "VK_KHR_present_id2", "VK_KHR_present_wait2", "VK_KHR_shader_maximal_reconvergence", "VK_KHR_shader_quad_control", "VK_KHR_shader_subgroup_uniform_control_flow", "VK_KHR_swapchain_mutable_format", "VK_KHR_workgroup_memory_explicit_layout", "VK_EXT_calibrated_timestamps", "VK_EXT_custom_border_color", "VK_EXT_debug_utils", "VK_EXT_descriptor_indexing", "VK_EXT_device_address_binding_report", "VK_EXT_device_memory_report", "VK_EXT_external_memory_acquire_unmodified", "VK_EXT_graphics_pipeline_library", "VK_EXT_hdr_metadata", "VK_EXT_image_2d_view_of_3d", "VK_EXT_image_compression_control", "VK_EXT_image_compression_control_swapchain", "VK_EXT_present_mode_fifo_latest_ready", "VK_EXT_present_timing", "VK_EXT_primitive_topology_list_restart", "VK_EXT_provoking_vertex", "VK_EXT_surface_maintenance1", "VK_EXT_swapchain_maintenance1", "VK_EXT_transform_feedback", "VK_ANDROID_external_format_resolve", "VK_GOOGLE_surfaceless_query"),
        features = profileFeatures("VkPhysicalDevice16BitStorageFeatures", "storageInputOutput16", "uniformAndStorageBuffer16BitAccess") +
            profileFeatures("VkPhysicalDeviceCustomBorderColorFeaturesEXT", "customBorderColors") +
            profileFeatures("VkPhysicalDeviceDescriptorIndexingFeaturesEXT", "descriptorBindingVariableDescriptorCount") +
            profileFeatures("VkPhysicalDeviceFeatures", "dualSrcBlend", "multiDrawIndirect", "shaderInt64", "shaderStorageImageWriteWithoutFormat") +
            profileFeatures("VkPhysicalDeviceProtectedMemoryFeatures", "protectedMemory") +
            profileFeatures("VkPhysicalDeviceShaderAtomicInt64Features", "shaderBufferInt64Atomics") +
            profileFeatures("VkPhysicalDeviceShaderFloat16Int8Features", "shaderFloat16") +
            profileFeatures("VkPhysicalDeviceVulkan12Features", "descriptorBindingVariableDescriptorCount", "shaderFloat16") +
            profileFeatures("VkPhysicalDeviceVulkan14Features", "hostImageCopy"),
        properties = profileProperties("VkPhysicalDeviceProperties", ProfilePropertyComparison.MINIMUM, "maxDescriptorSetStorageImages" to "144") +
            profileProperties("VkPhysicalDeviceVulkan11Properties", ProfilePropertyComparison.BITMASK_CONTAINS, "subgroupSupportedStages" to "0x10"),
        formats = ANDROID_4444_FORMAT_REQUIREMENTS
    ),
    requiredProfiles = listOf("VP_ANDROID_vulkan_profile_2025"),
    alternativeGroups = listOf(
        ProfileAlternativeGroup("primitivesGeneratedQuery OR pipelineStatisticsQuery", listOf(
            profileCapability(extensions = listOf("VK_EXT_primitives_generated_query"), features = profileFeatures("VkPhysicalDevicePrimitivesGeneratedQueryFeaturesEXT", "primitivesGeneratedQuery")),
            profileCapability(features = profileFeatures("VkPhysicalDeviceFeatures", "pipelineStatisticsQuery"))
        )),
        ProfileAlternativeGroup("multisampled-to-single-sampled OR shader stencil export", listOf(
            profileCapability(extensions = listOf("VK_EXT_multisampled_render_to_single_sampled")),
            profileCapability(extensions = listOf("VK_EXT_shader_stencil_export"))
        ))
    ),
    completeCoverage = false,
    coverageNote = "Official r.4 direct MUST extensions/features/properties/formats, both OR groups and Android 2025 inheritance are mapped; Android 2025 itself retains an intentionally incomplete full-format matrix, so PASS is disabled"
)

internal fun roadmap2022Capability(): ProfileCapabilityRequirement = profileCapability(
    extensions = listOf("VK_KHR_global_priority"),
    features = profileFeatures("VkPhysicalDeviceFeatures", "robustBufferAccess", "fullDrawIndexUint32", "imageCubeArray", "independentBlend", "sampleRateShading", "drawIndirectFirstInstance", "depthClamp", "depthBiasClamp", "samplerAnisotropy", "occlusionQueryPrecise", "fragmentStoresAndAtomics", "shaderStorageImageExtendedFormats", "shaderUniformBufferArrayDynamicIndexing", "shaderSampledImageArrayDynamicIndexing", "shaderStorageBufferArrayDynamicIndexing", "shaderStorageImageArrayDynamicIndexing") +
        profileFeatures("VkPhysicalDeviceVulkan11Features", "multiview", "samplerYcbcrConversion") +
        profileFeatures("VkPhysicalDeviceVulkan12Features", "uniformBufferStandardLayout", "subgroupBroadcastDynamicId", "imagelessFramebuffer", "separateDepthStencilLayouts", "hostQueryReset", "timelineSemaphore", "shaderSubgroupExtendedTypes", "vulkanMemoryModel", "vulkanMemoryModelDeviceScope", "bufferDeviceAddress", "descriptorIndexing", "shaderUniformTexelBufferArrayDynamicIndexing", "shaderStorageTexelBufferArrayDynamicIndexing", "shaderUniformBufferArrayNonUniformIndexing", "shaderSampledImageArrayNonUniformIndexing", "shaderStorageBufferArrayNonUniformIndexing", "shaderStorageImageArrayNonUniformIndexing", "shaderUniformTexelBufferArrayNonUniformIndexing", "shaderStorageTexelBufferArrayNonUniformIndexing", "descriptorBindingSampledImageUpdateAfterBind", "descriptorBindingStorageImageUpdateAfterBind", "descriptorBindingStorageBufferUpdateAfterBind", "descriptorBindingUniformTexelBufferUpdateAfterBind", "descriptorBindingStorageTexelBufferUpdateAfterBind", "descriptorBindingUpdateUnusedWhilePending", "descriptorBindingPartiallyBound", "descriptorBindingVariableDescriptorCount", "runtimeDescriptorArray", "scalarBlockLayout") +
        profileFeatures("VkPhysicalDeviceVulkan13Features", "robustImageAccess", "shaderTerminateInvocation", "shaderZeroInitializeWorkgroupMemory", "synchronization2", "shaderIntegerDotProduct", "maintenance4", "pipelineCreationCacheControl", "subgroupSizeControl", "computeFullSubgroups", "shaderDemoteToHelperInvocation", "inlineUniformBlock", "dynamicRendering", "privateData", "descriptorBindingInlineUniformBlockUpdateAfterBind") +
        profileFeatures("VkPhysicalDeviceGlobalPriorityQueryFeaturesKHR", "globalPriorityQuery"),
    properties = profileProperties("VkPhysicalDeviceVulkan11Properties", ProfilePropertyComparison.MINIMUM, "maxMultiviewViewCount" to "6", "maxMultiviewInstanceIndex" to "134217727", "subgroupSize" to "4") +
        profileProperties("VkPhysicalDeviceVulkan11Properties", ProfilePropertyComparison.BITMASK_CONTAINS, "subgroupSupportedStages" to "0x30", "subgroupSupportedOperations" to "0xBF") +
        profileProperties("VkPhysicalDeviceVulkan12Properties", ProfilePropertyComparison.MINIMUM, "maxTimelineSemaphoreValueDifference" to "2147483647", "maxPerStageDescriptorUpdateAfterBindSamplers" to "500000", "maxPerStageDescriptorUpdateAfterBindUniformBuffers" to "12", "maxPerStageDescriptorUpdateAfterBindStorageBuffers" to "500000", "maxPerStageDescriptorUpdateAfterBindSampledImages" to "500000", "maxPerStageDescriptorUpdateAfterBindStorageImages" to "500000", "maxPerStageDescriptorUpdateAfterBindInputAttachments" to "7", "maxPerStageUpdateAfterBindResources" to "500000", "maxDescriptorSetUpdateAfterBindSamplers" to "500000", "maxDescriptorSetUpdateAfterBindUniformBuffers" to "72", "maxDescriptorSetUpdateAfterBindUniformBuffersDynamic" to "8", "maxDescriptorSetUpdateAfterBindStorageBuffers" to "500000", "maxDescriptorSetUpdateAfterBindStorageBuffersDynamic" to "4", "maxDescriptorSetUpdateAfterBindSampledImages" to "500000", "maxDescriptorSetUpdateAfterBindStorageImages" to "500000", "maxDescriptorSetUpdateAfterBindInputAttachments" to "7") +
        profileProperties("VkPhysicalDeviceVulkan12Properties", ProfilePropertyComparison.EQUAL_BOOL, "shaderSignedZeroInfNanPreserveFloat16" to "true", "shaderSignedZeroInfNanPreserveFloat32" to "true") +
        profileProperties("VkPhysicalDeviceVulkan13Properties", ProfilePropertyComparison.MINIMUM, "maxBufferSize" to "1073741824", "maxInlineUniformBlockSize" to "256", "maxPerStageDescriptorInlineUniformBlocks" to "4", "maxPerStageDescriptorUpdateAfterBindInlineUniformBlocks" to "4", "maxDescriptorSetInlineUniformBlocks" to "4", "maxDescriptorSetUpdateAfterBindInlineUniformBlocks" to "4", "maxInlineUniformTotalSize" to "256") +
        profileProperties("VkPhysicalDeviceProperties", ProfilePropertyComparison.MAXIMUM, "bufferImageGranularity" to "4096") +
        profileProperties("VkPhysicalDeviceProperties", ProfilePropertyComparison.MINIMUM, "maxImageDimension1D" to "8192", "maxImageDimension2D" to "8192", "maxImageDimensionCube" to "8192", "maxImageArrayLayers" to "2048", "maxUniformBufferRange" to "65536", "maxPerStageDescriptorSamplers" to "64", "maxPerStageDescriptorUniformBuffers" to "15", "maxPerStageDescriptorStorageBuffers" to "30", "maxPerStageDescriptorSampledImages" to "200", "maxPerStageDescriptorStorageImages" to "16", "maxPerStageResources" to "200", "maxDescriptorSetSamplers" to "576", "maxDescriptorSetUniformBuffers" to "90", "maxDescriptorSetStorageBuffers" to "96", "maxDescriptorSetSampledImages" to "1800", "maxDescriptorSetStorageImages" to "144", "maxFragmentCombinedOutputResources" to "16", "maxComputeWorkGroupInvocations" to "256", "subTexelPrecisionBits" to "8", "mipmapPrecisionBits" to "6", "maxSamplerLodBias" to "14", "maxColorAttachments" to "7") +
        profileProperties("VkPhysicalDeviceProperties", ProfilePropertyComparison.VECTOR_MINIMUM, "maxComputeWorkGroupSize" to "256,256,64") +
        profileProperties("VkPhysicalDeviceProperties", ProfilePropertyComparison.EQUAL_BOOL, "standardSampleLocations" to "true")
)

internal fun roadmap2024BaseCapability(): ProfileCapabilityRequirement = profileCapability(
    extensions = listOf("VK_KHR_load_store_op_none", "VK_KHR_shader_quad_control", "VK_KHR_shader_maximal_reconvergence", "VK_KHR_shader_subgroup_uniform_control_flow", "VK_KHR_map_memory2"),
    features = profileFeatures("VkPhysicalDeviceFeatures", "multiDrawIndirect", "shaderInt16", "shaderImageGatherExtended") +
        profileFeatures("VkPhysicalDeviceVulkan11Features", "shaderDrawParameters", "storageBuffer16BitAccess") +
        profileFeatures("VkPhysicalDeviceVulkan12Features", "shaderInt8", "shaderFloat16", "storageBuffer8BitAccess") +
        profileFeatures("VkPhysicalDeviceShaderQuadControlFeaturesKHR", "shaderQuadControl") +
        profileFeatures("VkPhysicalDeviceShaderMaximalReconvergenceFeaturesKHR", "shaderMaximalReconvergence") +
        profileFeatures("VkPhysicalDeviceShaderSubgroupUniformControlFlowFeaturesKHR", "shaderSubgroupUniformControlFlow"),
    properties = profileProperties("VkPhysicalDeviceProperties", ProfilePropertyComparison.MINIMUM, "maxColorAttachments" to "8", "maxBoundDescriptorSets" to "7") +
        profileProperties("VkPhysicalDeviceProperties", ProfilePropertyComparison.EQUAL_BOOL, "timestampComputeAndGraphics" to "true") +
        profileProperties("VkPhysicalDeviceVulkan12Properties", ProfilePropertyComparison.EQUAL_BOOL, "shaderRoundingModeRTEFloat16" to "true", "shaderRoundingModeRTEFloat32" to "true")
)

internal fun roadmap2024PromotedVulkan14Capability(): ProfileCapabilityRequirement = profileCapability(
    extensions = listOf("VK_KHR_dynamic_rendering", "VK_KHR_shader_subgroup_rotate", "VK_KHR_shader_float_controls2", "VK_KHR_shader_expect_assume", "VK_KHR_line_rasterization", "VK_KHR_vertex_attribute_divisor", "VK_KHR_index_type_uint8", "VK_KHR_maintenance5", "VK_KHR_dynamic_rendering_local_read", "VK_KHR_push_descriptor"),
    features = profileFeatures("VkPhysicalDeviceShaderSubgroupRotateFeaturesKHR", "shaderSubgroupRotate") +
        profileFeatures("VkPhysicalDeviceShaderFloatControls2FeaturesKHR", "shaderFloatControls2") +
        profileFeatures("VkPhysicalDeviceShaderExpectAssumeFeaturesKHR", "shaderExpectAssume") +
        profileFeatures("VkPhysicalDeviceVertexAttributeDivisorFeaturesKHR", "vertexAttributeInstanceRateDivisor") +
        profileFeatures("VkPhysicalDeviceIndexTypeUint8FeaturesKHR", "indexTypeUint8") +
        profileFeatures("VkPhysicalDeviceDynamicRenderingLocalReadFeaturesKHR", "dynamicRenderingLocalRead") +
        profileFeatures("VkPhysicalDeviceMaintenance5FeaturesKHR", "maintenance5")
)

internal fun roadmap2024Capability(): ProfileCapabilityRequirement = mergeProfileCapabilities(roadmap2024BaseCapability(), roadmap2024PromotedVulkan14Capability())

internal fun roadmapLineAlternatives(structure: String): ProfileAlternativeGroup = ProfileAlternativeGroup("one required line-rasterization mode", listOf("rectangularLines", "bresenhamLines", "smoothLines", "stippledRectangularLines", "stippledBresenhamLines", "stippledSmoothLines").map { member ->
    profileCapability(extensions = listOf("VK_KHR_line_rasterization"), features = profileFeatures(structure, member))
})

internal fun roadmap2026Capability(): ProfileCapabilityRequirement = profileCapability(
    extensions = listOf("VK_KHR_dynamic_rendering", "VK_KHR_shader_subgroup_rotate", "VK_KHR_shader_float_controls2", "VK_KHR_shader_expect_assume", "VK_KHR_vertex_attribute_divisor", "VK_KHR_index_type_uint8", "VK_KHR_maintenance5", "VK_KHR_dynamic_rendering_local_read", "VK_KHR_push_descriptor", "VK_KHR_robustness2", "VK_KHR_pipeline_binary", "VK_KHR_fragment_shading_rate", "VK_KHR_shader_clock", "VK_KHR_workgroup_memory_explicit_layout", "VK_KHR_compute_shader_derivatives", "VK_KHR_maintenance7", "VK_KHR_maintenance8", "VK_KHR_maintenance9", "VK_KHR_depth_clamp_zero_one", "VK_KHR_copy_memory_indirect", "VK_KHR_shader_untyped_pointers", "VK_KHR_surface", "VK_KHR_swapchain", "VK_KHR_get_surface_capabilities2", "VK_KHR_present_mode_fifo_latest_ready", "VK_KHR_present_id2", "VK_KHR_present_wait2", "VK_KHR_surface_maintenance1", "VK_KHR_swapchain_maintenance1", "VK_KHR_cooperative_matrix"),
    features = profileFeatures("VkPhysicalDeviceVulkan14Features", "globalPriorityQuery", "shaderSubgroupRotate", "shaderExpectAssume", "shaderFloatControls2", "vertexAttributeInstanceRateDivisor", "indexTypeUint8", "dynamicRenderingLocalRead", "maintenance5", "hostImageCopy", "pushDescriptor") +
        profileFeatures("VkPhysicalDeviceRobustness2FeaturesKHR", "robustBufferAccess2", "robustImageAccess2", "nullDescriptor") +
        profileFeatures("VkPhysicalDevicePipelineBinaryFeaturesKHR", "pipelineBinaries") +
        profileFeatures("VkPhysicalDeviceFragmentShadingRateFeaturesKHR", "pipelineFragmentShadingRate") +
        profileFeatures("VkPhysicalDeviceShaderClockFeaturesKHR", "shaderSubgroupClock") +
        profileFeatures("VkPhysicalDeviceWorkgroupMemoryExplicitLayoutFeaturesKHR", "workgroupMemoryExplicitLayout") +
        profileFeatures("VkPhysicalDeviceComputeShaderDerivativesFeaturesKHR", "computeDerivativeGroupLinear") +
        profileFeatures("VkPhysicalDeviceMaintenance7FeaturesKHR", "maintenance7") +
        profileFeatures("VkPhysicalDeviceMaintenance8FeaturesKHR", "maintenance8") +
        profileFeatures("VkPhysicalDeviceMaintenance9FeaturesKHR", "maintenance9") +
        profileFeatures("VkPhysicalDeviceDepthClampZeroOneFeaturesKHR", "depthClampZeroOne") +
        profileFeatures("VkPhysicalDeviceCopyMemoryIndirectFeaturesKHR", "indirectMemoryCopy") +
        profileFeatures("VkPhysicalDeviceShaderUntypedPointersFeaturesKHR", "shaderUntypedPointers") +
        profileFeatures("VkPhysicalDevicePresentModeFifoLatestReadyFeaturesKHR", "presentModeFifoLatestReady") +
        profileFeatures("VkPhysicalDevicePresentId2FeaturesKHR", "presentId2") +
        profileFeatures("VkPhysicalDevicePresentWait2FeaturesKHR", "presentWait2") +
        profileFeatures("VkPhysicalDeviceSwapchainMaintenance1FeaturesKHR", "swapchainMaintenance1") +
        profileFeatures("VkPhysicalDeviceCooperativeMatrixFeaturesKHR", "cooperativeMatrix"),
    properties = profileProperties("VkPhysicalDeviceProperties", ProfilePropertyComparison.MINIMUM, "maxPerStageDescriptorUniformBuffers" to "200", "maxPerStageDescriptorStorageBuffers" to "200", "maxPerStageDescriptorInputAttachments" to "8", "maxDescriptorSetStorageBuffers" to "1800", "maxDescriptorSetUniformBuffers" to "1800", "maxDescriptorSetInputAttachments" to "8", "maxVertexOutputComponents" to "124", "maxTessellationControlPerVertexInputComponents" to "128", "maxTessellationControlPerVertexOutputComponents" to "128", "maxTessellationControlTotalOutputComponents" to "4096", "maxTessellationEvaluationInputComponents" to "128", "maxTessellationEvaluationOutputComponents" to "128", "maxGeometryOutputComponents" to "128", "maxFragmentInputComponents" to "112", "maxFragmentOutputAttachments" to "8", "maxComputeSharedMemorySize" to "32768", "subPixelPrecisionBits" to "8", "maxFramebufferWidth" to "8192", "maxFramebufferHeight" to "8192") +
        profileProperties("VkPhysicalDeviceProperties", ProfilePropertyComparison.VECTOR_MINIMUM, "maxViewportDimensions" to "8192,8192") +
        profileProperties("VkPhysicalDeviceMaintenance7PropertiesKHR", ProfilePropertyComparison.MINIMUM, "maxDescriptorSetTotalUniformBuffersDynamic" to "8", "maxDescriptorSetTotalStorageBuffersDynamic" to "4", "maxDescriptorSetTotalBuffersDynamic" to "12", "maxDescriptorSetUpdateAfterBindTotalUniformBuffersDynamic" to "8", "maxDescriptorSetUpdateAfterBindTotalStorageBuffersDynamic" to "4", "maxDescriptorSetUpdateAfterBindTotalBuffersDynamic" to "12")
)

internal fun roadmap2026ProfileCapability(): ProfileCapabilityRequirement = mergeProfileCapabilities(
    roadmap2022Capability(),
    roadmap2024BaseCapability(),
    roadmap2026Capability()
)

internal fun vulkanProfileRequirements(): List<ProfileRequirements> = listOf(
    android2025Requirements(),
    android15Requirements(),
    android16Requirements(),
    android17Requirements(),
    ProfileRequirements("VP_KHR_roadmap_2022", "r.2", "1.3.204", roadmap2022Capability(), completeCoverage = false, coverageNote = "Current Roadmap profile version 2 required feature/property/extension set is mapped from the audited 2026-05-21 revision; PASS remains disabled until generated source-equivalence is independently proven"),
    ProfileRequirements("VP_KHR_roadmap_2024", "r.2", "1.3.276", roadmap2024Capability(), requiredProfiles = listOf("VP_KHR_roadmap_2022"), alternativeGroups = listOf(roadmapLineAlternatives("VkPhysicalDeviceLineRasterizationFeaturesKHR")), completeCoverage = false, coverageNote = "Current Roadmap profile version 2 direct requirements, Roadmap 2022 inheritance and line-mode OR group are mapped; PASS remains disabled until generated source-equivalence is independently proven"),
    ProfileRequirements("VP_KHR_roadmap_2026", "r.2", "1.4.328", roadmap2026ProfileCapability(), alternativeGroups = listOf(roadmapLineAlternatives("VkPhysicalDeviceVulkan14Features")), completeCoverage = false, coverageNote = "Current Roadmap profile version 2 exact capability composition is normalized: base/2022 capabilities, Roadmap 2024 direct capabilities, Roadmap 2026 capabilities and only the 2026 Vulkan 1.4 line-mode OR group. Roadmap 2024 promoted-v1.4 and 2024 line alternatives are intentionally not inherited because the official 2026 profile does not compose them; PASS remains disabled until generated source-equivalence is independently proven")
)

internal fun vulkanProfileCatalog(): List<ProfileCatalogEntry> = listOf(
    ProfileCatalogEntry("VP_ANDROID_17_requirements", "r.4", "1.4.335", "Mapped evaluator"),
    ProfileCatalogEntry("VP_ANDROID_16_requirements", "r.7", "1.3.276", "Mapped evaluator"),
    ProfileCatalogEntry("VP_ANDROID_15_requirements", "r.2", "1.3.273", "Mapped evaluator"),
    ProfileCatalogEntry("VP_ANDROID_vulkan_profile_2025", "r.2", "1.1.128", "Mapped partial evaluator"),
    ProfileCatalogEntry("VP_ANDROID_vulkan_profile_2022", "current canonical Android profile", "", "Authoritative requirement definition is cataloged but not normalized into this release; no API-only support inference is performed"),
    ProfileCatalogEntry("VP_ANDROID_vulkan_profile_2021", "current canonical Android profile", "", "Authoritative requirement definition is cataloged but not normalized into this release; no API-only support inference is performed"),
    ProfileCatalogEntry("VP_KHR_roadmap_2026", "r.2", "1.4.328", "Mapped evaluator"),
    ProfileCatalogEntry("VP_KHR_roadmap_2024", "r.2", "1.3.276", "Mapped evaluator"),
    ProfileCatalogEntry("VP_KHR_roadmap_2022", "r.2", "1.3.204", "Mapped evaluator"),
    ProfileCatalogEntry("VP_LUNARG_minimum_requirements_1_4", "current", "", "Authoritative requirement definition is not pinned into the normalized evaluator in this release; API version alone is not profile support evidence"),
    ProfileCatalogEntry("VP_LUNARG_minimum_requirements_1_3", "current", "", "Authoritative requirement definition is not pinned into the normalized evaluator in this release; API version alone is not profile support evidence"),
    ProfileCatalogEntry("VP_LUNARG_minimum_requirements_1_2", "current", "", "Authoritative requirement definition is not pinned into the normalized evaluator in this release; API version alone is not profile support evidence"),
    ProfileCatalogEntry("VP_LUNARG_minimum_requirements_1_1", "current", "", "Authoritative requirement definition is not pinned into the normalized evaluator in this release; API version alone is not profile support evidence"),
    ProfileCatalogEntry("VP_LUNARG_minimum_requirements_1_0", "current", "", "Authoritative requirement definition is not pinned into the normalized evaluator in this release; API version alone is not profile support evidence")
)

internal fun extensionPromotedToSatisfied(name: String, deviceApiVersion: String): Boolean {
    val promoted = vulkanExtensionReference(name).promotedTo
    val match = Regex("Vulkan\\s+(\\d+)\\.(\\d+)", RegexOption.IGNORE_CASE).find(promoted) ?: return false
    return apiAtLeast(deviceApiVersion, match.groupValues[1].toInt(), match.groupValues[2].toInt())
}

internal fun profileFeatureEvidenceKeys(requirement: ProfileFeatureRequirement): List<String> {
    val direct = when (requirement.structure) {
        "VkPhysicalDeviceFeatures" -> requirement.member
        "VkPhysicalDeviceVulkan11Features" -> "Vulkan 1.1 · ${requirement.member}"
        "VkPhysicalDeviceVulkan12Features" -> "Vulkan 1.2 · ${requirement.member}"
        "VkPhysicalDeviceVulkan13Features" -> "Vulkan 1.3 · ${requirement.member}"
        "VkPhysicalDeviceVulkan14Features" -> "Vulkan 1.4 · ${requirement.member}"
        else -> "${requirement.structure} · ${requirement.member}"
    }
    val coreAlias = when (requirement.structure) {
        "VkPhysicalDevice16BitStorageFeatures", "VkPhysicalDeviceMultiviewFeatures", "VkPhysicalDeviceSamplerYcbcrConversionFeatures", "VkPhysicalDeviceShaderDrawParameterFeatures", "VkPhysicalDeviceVariablePointerFeatures", "VkPhysicalDeviceVariablePointerFeaturesKHR", "VkPhysicalDeviceProtectedMemoryFeatures" -> "Vulkan 1.1 · ${requirement.member}"
        "VkPhysicalDevice8BitStorageFeatures", "VkPhysicalDeviceDescriptorIndexingFeatures", "VkPhysicalDeviceDescriptorIndexingFeaturesEXT", "VkPhysicalDeviceHostQueryResetFeatures", "VkPhysicalDeviceImagelessFramebufferFeatures", "VkPhysicalDeviceSamplerMirrorClampToEdgeFeatures", "VkPhysicalDeviceScalarBlockLayoutFeatures", "VkPhysicalDeviceShaderAtomicInt64Features", "VkPhysicalDeviceShaderFloat16Int8Features", "VkPhysicalDeviceShaderSubgroupExtendedTypesFeatures", "VkPhysicalDeviceTimelineSemaphoreFeatures", "VkPhysicalDeviceBufferDeviceAddressFeatures", "VkPhysicalDeviceVulkanMemoryModelFeatures", "VkPhysicalDeviceUniformBufferStandardLayoutFeatures", "VkPhysicalDeviceSeparateDepthStencilLayoutsFeatures" -> "Vulkan 1.2 · ${requirement.member}"
        "VkPhysicalDeviceDynamicRenderingFeatures", "VkPhysicalDeviceInlineUniformBlockFeatures", "VkPhysicalDeviceMaintenance4Features", "VkPhysicalDevicePipelineCreationCacheControlFeatures", "VkPhysicalDevicePrivateDataFeatures", "VkPhysicalDeviceShaderDemoteToHelperInvocationFeatures", "VkPhysicalDeviceShaderIntegerDotProductFeatures", "VkPhysicalDeviceShaderTerminateInvocationFeatures", "VkPhysicalDeviceSubgroupSizeControlFeatures", "VkPhysicalDeviceSynchronization2Features", "VkPhysicalDeviceZeroInitializeWorkgroupMemoryFeatures" -> "Vulkan 1.3 · ${requirement.member}"
        else -> null
    }
    val explicitAlias = when (requirement.structure) {
        "VkPhysicalDeviceVertexAttributeDivisorFeaturesKHR" -> "VkPhysicalDeviceVertexAttributeDivisorFeaturesEXT · ${requirement.member}"
        "VkPhysicalDeviceIndexTypeUint8FeaturesKHR" -> "VkPhysicalDeviceIndexTypeUint8FeaturesEXT · ${requirement.member}"
        "VkPhysicalDeviceLineRasterizationFeaturesKHR" -> "VkPhysicalDeviceLineRasterizationFeaturesEXT · ${requirement.member}"
        "VkPhysicalDeviceGlobalPriorityQueryFeaturesKHR" -> "VkPhysicalDeviceGlobalPriorityQueryFeaturesEXT · ${requirement.member}"
        "VkPhysicalDeviceRobustness2FeaturesKHR" -> "VkPhysicalDeviceRobustness2FeaturesEXT · ${requirement.member}"
        else -> null
    }
    return listOfNotNull(direct, coreAlias, explicitAlias).distinct()
}

internal fun profilePropertyValue(device: DeviceReport, requirement: ProfilePropertyRequirement): String? {
    if (requirement.structure == "VkPhysicalDeviceProperties") return device.limits.firstOrNull { it.first == requirement.member }?.second
    val sections = when (requirement.structure) {
        "VkPhysicalDeviceVulkan11Properties" -> listOf("Core 1.1")
        "VkPhysicalDeviceVulkan12Properties" -> listOf("Core 1.2")
        "VkPhysicalDeviceVulkan13Properties" -> listOf("Core 1.3")
        "VkPhysicalDeviceVulkan14Properties" -> listOf("Core 1.4")
        "VkPhysicalDeviceMultiviewProperties" -> listOf("VkPhysicalDeviceMultiviewProperties", "Core 1.1")
        "VkPhysicalDeviceFloatControlsProperties" -> listOf("VkPhysicalDeviceFloatControlsProperties", "Core 1.2")
        else -> listOf(requirement.structure)
    }
    return device.detailedProperties.firstOrNull { entry -> sections.any { section -> entry.section == section || entry.section.endsWith(" · $section") } && entry.name == requirement.member }?.value
}

internal fun profileDecimal(value: String): java.math.BigDecimal? = Regex("-?\\d+(?:\\.\\d+)?").find(value)?.value?.toBigDecimalOrNull()
internal fun profileInteger(value: String): java.math.BigInteger? {
    val hex = Regex("0x([0-9A-Fa-f]+)").find(value)
    if (hex != null) return runCatching { java.math.BigInteger(hex.groupValues[1], 16) }.getOrNull()
    return Regex("-?\\d+").find(value)?.value?.let { runCatching { java.math.BigInteger(it) }.getOrNull() }
}
internal fun profileVector(value: String): List<java.math.BigDecimal> = Regex("-?\\d+(?:\\.\\d+)?").findAll(value).mapNotNull { it.value.toBigDecimalOrNull() }.toList()
internal fun profileBoolean(value: String): Boolean? = when (value.trim().lowercase()) { "true" -> true; "false" -> false; else -> null }

internal fun profilePropertyState(device: DeviceReport, requirement: ProfilePropertyRequirement): Pair<String, String> {
    val value = profilePropertyValue(device, requirement) ?: return "UNKNOWN" to "${requirement.structure}.${requirement.member} ${requirement.comparison.name.lowercase()} ${requirement.requiredValue}"
    return when (requirement.comparison) {
        ProfilePropertyComparison.MINIMUM -> {
            val actual = profileDecimal(value); val required = requirement.requiredValue.toBigDecimalOrNull()
            if (actual == null || required == null) "UNKNOWN" to "${requirement.structure}.${requirement.member}" else if (actual < required) "FAIL" to "${requirement.member}=$actual < $required" else "PASS" to ""
        }
        ProfilePropertyComparison.MAXIMUM -> {
            val actual = profileDecimal(value); val required = requirement.requiredValue.toBigDecimalOrNull()
            if (actual == null || required == null) "UNKNOWN" to "${requirement.structure}.${requirement.member}" else if (actual > required) "FAIL" to "${requirement.member}=$actual > $required" else "PASS" to ""
        }
        ProfilePropertyComparison.EQUAL_BOOL -> {
            val actual = profileBoolean(value); val required = profileBoolean(requirement.requiredValue)
            if (actual == null || required == null) "UNKNOWN" to "${requirement.structure}.${requirement.member}" else if (actual != required) "FAIL" to "${requirement.member}=$actual != $required" else "PASS" to ""
        }
        ProfilePropertyComparison.BITMASK_CONTAINS -> {
            val actual = profileInteger(value); val required = profileInteger(requirement.requiredValue)
            if (actual == null || required == null) "UNKNOWN" to "${requirement.structure}.${requirement.member}" else if (actual.and(required) != required) "FAIL" to "${requirement.member}=$actual missing mask ${requirement.requiredValue}" else "PASS" to ""
        }
        ProfilePropertyComparison.VECTOR_MINIMUM -> {
            val actual = profileVector(value); val required = requirement.requiredValue.split(',').mapNotNull { it.trim().toBigDecimalOrNull() }
            if (actual.size < required.size || required.isEmpty()) "UNKNOWN" to "${requirement.structure}.${requirement.member}" else {
                val failingIndex = required.indices.firstOrNull { actual[it] < required[it] }
                if (failingIndex != null) "FAIL" to "${requirement.member}[${failingIndex}]=${actual[failingIndex]} < ${required[failingIndex]}" else "PASS" to ""
            }
        }
    }
}

internal fun profileCanonicalFormatName(name: String): String = when (name) {
    "VK_FORMAT_A4B4G4R4_UNORM_PACK16_EXT" -> "VK_FORMAT_A4B4G4R4_UNORM_PACK16"
    "VK_FORMAT_A4R4G4B4_UNORM_PACK16_EXT" -> "VK_FORMAT_A4R4G4B4_UNORM_PACK16"
    else -> name
}

internal fun evaluateProfileCapability(report: VulkanReport?, device: DeviceReport, capability: ProfileCapabilityRequirement): ProfileEvidenceResult {
    val evidence = ProfileEvidenceResult()
    val instanceExtensions = report?.instanceExtensions?.map { it.name }?.toSet().orEmpty()
    val deviceExtensions = device.extensions.map { it.name }.toSet()
    capability.extensions.forEach { extension ->
        if (extension in instanceExtensions || extension in deviceExtensions || extensionPromotedToSatisfied(extension, device.apiVersion)) {
            evidence.recordCheck(evidence.extensionChecks, "PASS")
            return@forEach
        }
        val complete = if (extension in PROFILE_INSTANCE_EXTENSIONS) report?.instanceExtensionStatus == "available" else device.deviceExtensionStatus == "available"
        if (complete) evidence.missingExtensions += extension else evidence.unknownExtensions += extension
        evidence.recordCheck(evidence.extensionChecks, if (complete) "FAIL" else "UNKNOWN")
    }
    val featureMap = device.features.associateBy { it.name.trim() }
    capability.features.forEach { requirement ->
        val entries = profileFeatureEvidenceKeys(requirement).mapNotNull { featureMap[it] }
        when {
            entries.any { it.supported } -> evidence.recordCheck(evidence.featureChecks, "PASS")
            entries.isNotEmpty() -> {
                evidence.missingFeatures += "${requirement.structure}.${requirement.member}"
                evidence.recordCheck(evidence.featureChecks, "FAIL")
            }
            else -> {
                evidence.unknownFeatures += "${requirement.structure}.${requirement.member}"
                evidence.recordCheck(evidence.featureChecks, "UNKNOWN")
            }
        }
    }
    capability.properties.forEach { requirement ->
        val state = profilePropertyState(device, requirement)
        evidence.recordCheck(evidence.propertyChecks, state.first)
        if (state.first == "FAIL") evidence.failingProperties += state.second
        if (state.first == "UNKNOWN") evidence.unknownProperties += state.second
    }
    val formats = device.formats.associateBy { profileCanonicalFormatName(it.name) }
    capability.formats.forEach { requirement ->
        val canonical = profileCanonicalFormatName(requirement.format)
        val actual = formats[canonical]
        if (actual == null) {
            evidence.unknownFormats += requirement.format
            evidence.recordCheck(evidence.formatChecks, "UNKNOWN")
        } else {
            val missingLinear = (actual.linear and requirement.linearMask) != requirement.linearMask
            val missingOptimal = (actual.optimal and requirement.optimalMask) != requirement.optimalMask
            val missingBuffer = (actual.buffer and requirement.bufferMask) != requirement.bufferMask
            if (missingLinear || missingOptimal || missingBuffer) {
                evidence.failingFormats += requirement.format
                evidence.recordCheck(evidence.formatChecks, "FAIL")
            } else {
                evidence.recordCheck(evidence.formatChecks, "PASS")
            }
        }
    }
    return evidence
}

internal fun profileEvidenceState(evidence: ProfileEvidenceResult): String = when {
    evidence.missingExtensions.isNotEmpty() || evidence.missingFeatures.isNotEmpty() || evidence.failingProperties.isNotEmpty() || evidence.failingFormats.isNotEmpty() -> "FAIL"
    evidence.unknownExtensions.isNotEmpty() || evidence.unknownFeatures.isNotEmpty() || evidence.unknownProperties.isNotEmpty() || evidence.unknownFormats.isNotEmpty() -> "UNKNOWN"
    else -> "PASS"
}

internal fun evaluateProfile(report: VulkanReport?, device: DeviceReport?, requirements: ProfileRequirements, allRequirements: Map<String, ProfileRequirements>, stack: Set<String> = emptySet()): ProfileResult {
    if (device == null) {
        val extensionCount = requirements.capability.extensions.size
        val featureCount = requirements.capability.features.size
        val propertyCount = requirements.capability.properties.size
        val formatCount = requirements.capability.formats.size
        val groupCount = requirements.requiredProfiles.size + requirements.alternativeGroups.size
        val total = extensionCount + featureCount + propertyCount + formatCount + groupCount
        return ProfileResult(
            requirements.name, requirements.revision, "UNKNOWN",
            emptyList(), requirements.capability.extensions, emptyList(), requirements.capability.features.map { "${it.structure}.${it.member}" },
            emptyList(), requirements.capability.properties.map { "${it.structure}.${it.member}" }, coverageNote = requirements.coverageNote, minimumApiVersion = requirements.minApiVersion,
            unknownFormats = requirements.capability.formats.map { it.format },
            unknownRequirementGroups = requirements.requiredProfiles.map { "Required profile $it is unavailable without device evidence" } + requirements.alternativeGroups.map { it.label },
            checkedRequirementCount = total,
            unknownRequirementCount = total,
            extensionChecks = ProfileCheckCounts(extensionCount, unknown = extensionCount),
            featureChecks = ProfileCheckCounts(featureCount, unknown = featureCount),
            propertyChecks = ProfileCheckCounts(propertyCount, unknown = propertyCount),
            formatChecks = ProfileCheckCounts(formatCount, unknown = formatCount),
            groupChecks = ProfileCheckCounts(groupCount, unknown = groupCount)
        )
    }
    if (requirements.name in stack) return ProfileResult(requirements.name, requirements.revision, "UNKNOWN", emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), coverageNote = "Required-profile cycle rejected", minimumApiVersion = requirements.minApiVersion, unknownRequirementGroups = listOf("required-profile cycle"), checkedRequirementCount = 1, unknownRequirementCount = 1, groupChecks = ProfileCheckCounts(1, unknown = 1))
    val evidence = evaluateProfileCapability(report, device, requirements.capability)
    val failingGroups = mutableListOf<String>()
    val unknownGroups = mutableListOf<String>()
    requirements.requiredProfiles.forEach { requiredName ->
        val required = allRequirements[requiredName]
        if (required == null) {
            unknownGroups += "Required profile $requiredName is cataloged but not mapped"
            evidence.recordCheck(evidence.groupChecks, "UNKNOWN")
        } else {
            val result = evaluateProfile(report, device, required, allRequirements, stack + requirements.name)
            evidence.addChecks(result)
            when (result.status) {
                "FAIL" -> {
                    failingGroups += "Required profile $requiredName failed"
                    evidence.recordCheck(evidence.groupChecks, "FAIL")
                }
                "PASS" -> evidence.recordCheck(evidence.groupChecks, "PASS")
                else -> {
                    unknownGroups += "Required profile $requiredName is unresolved"
                    evidence.recordCheck(evidence.groupChecks, "UNKNOWN")
                }
            }
        }
    }
    requirements.alternativeGroups.forEach { group ->
        val alternativeEvidence = group.alternatives.map { evaluateProfileCapability(report, device, it) }
        val states = alternativeEvidence.map { profileEvidenceState(it) }
        val groupState = when {
            states.any { it == "PASS" } -> "PASS"
            states.all { it == "FAIL" } -> "FAIL"
            else -> "UNKNOWN"
        }
        val selectedAlternativeIndex = when (groupState) {
            "PASS" -> states.indexOf("PASS")
            "FAIL" -> alternativeEvidence.indices.minByOrNull { index -> alternativeEvidence[index].totalChecks.failed } ?: 0
            else -> alternativeEvidence.indices.minByOrNull { index -> alternativeEvidence[index].totalChecks.unknown * 1000 + alternativeEvidence[index].totalChecks.failed } ?: 0
        }
        alternativeEvidence.getOrNull(selectedAlternativeIndex)?.let { evidence.addChecks(it) }
        evidence.recordCheck(evidence.groupChecks, groupState)
        when (groupState) {
            "FAIL" -> failingGroups += group.label
            "UNKNOWN" -> unknownGroups += group.label
        }
    }
    val apiOk = apiVersionAtLeast(device.apiVersion, requirements.minApiVersion)
    if (!apiOk) failingGroups += "API ${device.apiVersion} < ${requirements.minApiVersion}"
    val verifiedFailure = profileEvidenceState(evidence) == "FAIL" || failingGroups.isNotEmpty()
    val hasUnknown = profileEvidenceState(evidence) == "UNKNOWN" || unknownGroups.isNotEmpty()
    val status = when {
        verifiedFailure -> "FAIL"
        hasUnknown || !requirements.completeCoverage -> "UNKNOWN"
        else -> "PASS"
    }
    return ProfileResult(
        requirements.name, requirements.revision, status,
        evidence.missingExtensions.distinct(), evidence.unknownExtensions.distinct(), evidence.missingFeatures.distinct(), evidence.unknownFeatures.distinct(),
        evidence.failingProperties.distinct(), evidence.unknownProperties.distinct(), coverageNote = requirements.coverageNote, minimumApiVersion = requirements.minApiVersion,
        failingFormats = evidence.failingFormats.distinct(), unknownFormats = evidence.unknownFormats.distinct(), failingRequirementGroups = failingGroups.distinct(), unknownRequirementGroups = unknownGroups.distinct(),
        checkedRequirementCount = evidence.totalChecks.total,
        metRequirementCount = evidence.totalChecks.met,
        failedRequirementCount = evidence.totalChecks.failed,
        unknownRequirementCount = evidence.totalChecks.unknown,
        extensionChecks = evidence.extensionChecks.snapshot(),
        featureChecks = evidence.featureChecks.snapshot(),
        propertyChecks = evidence.propertyChecks.snapshot(),
        formatChecks = evidence.formatChecks.snapshot(),
        groupChecks = evidence.groupChecks.snapshot()
    )
}

internal fun vulkanProfileEvaluations(report: VulkanReport?, device: DeviceReport?): List<ProfileResult> {
    val requirements = vulkanProfileRequirements()
    val mapped = requirements.associateBy { it.name }
    val evaluated = requirements.map { evaluateProfile(report, device, it, mapped) }
    val evaluatedNames = evaluated.map { it.name }.toSet()
    val catalogOnly = vulkanProfileCatalog().filterNot { it.name in evaluatedNames }.map { entry ->
        ProfileResult(entry.name, entry.revision, "UNKNOWN", emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), coverageNote = entry.coverageNote, minimumApiVersion = entry.minApiVersion, unknownRequirementGroups = listOf("Authoritative requirements are not mapped in this release"))
    }
    return vulkanProfileCatalog().mapNotNull { catalog -> (evaluated + catalogOnly).firstOrNull { it.name == catalog.name } }
}

internal fun profileCheckSummary(counts: ProfileCheckCounts): String = "${counts.met}/${counts.total} met · ${counts.failed} unmet · ${counts.unknown} unknown"

internal fun profileSummary(result: ProfileResult): String = buildString {
    append(result.status)
    if (result.checkedRequirementCount > 0) append(" · ${result.checkedRequirementCount} mapped checks · ${result.metRequirementCount} met · ${result.failedRequirementCount} unmet · ${result.unknownRequirementCount} unknown")
    if (result.missingExtensions.isNotEmpty()) append(" · ${result.missingExtensions.size} verified missing extension(s)")
    if (result.unknownExtensions.isNotEmpty()) append(" · ${result.unknownExtensions.size} extension requirement(s) unknown")
    if (result.missingFeatures.isNotEmpty()) append(" · ${result.missingFeatures.size} unsupported feature(s)")
    if (result.unknownFeatures.isNotEmpty()) append(" · ${result.unknownFeatures.size} feature query(ies) unavailable")
    if (result.failingLimits.isNotEmpty()) append(" · ${result.failingLimits.size} failing property/limit requirement(s)")
    if (result.unknownLimits.isNotEmpty()) append(" · ${result.unknownLimits.size} property/limit requirement(s) unavailable")
    if (result.failingFormats.isNotEmpty()) append(" · ${result.failingFormats.size} failing format requirement(s)")
    if (result.unknownFormats.isNotEmpty()) append(" · ${result.unknownFormats.size} format requirement(s) unavailable")
    if (result.failingRequirementGroups.isNotEmpty()) append(" · ${result.failingRequirementGroups.size} failed inherited/OR group(s)")
    if (result.unknownRequirementGroups.isNotEmpty()) append(" · ${result.unknownRequirementGroups.size} inherited/OR group(s) unresolved")
    if (result.coverageNote.isNotBlank()) append(" · coverage-limited")
}

internal fun profileDetailPairs(result: ProfileResult): List<Pair<String, String>> = buildList {
    add("Mapped requirement checks" to result.checkedRequirementCount.toString())
    add("Mapped checks met" to result.metRequirementCount.toString())
    add("Mapped checks unmet" to result.failedRequirementCount.toString())
    add("Mapped checks unknown" to result.unknownRequirementCount.toString())
    if (result.extensionChecks.total > 0) add("Extension checks" to profileCheckSummary(result.extensionChecks))
    if (result.featureChecks.total > 0) add("Feature checks" to profileCheckSummary(result.featureChecks))
    if (result.propertyChecks.total > 0) add("Property / limit checks" to profileCheckSummary(result.propertyChecks))
    if (result.formatChecks.total > 0) add("Format checks" to profileCheckSummary(result.formatChecks))
    if (result.groupChecks.total > 0) add("Inherited profile checks" to profileCheckSummary(result.groupChecks))
    if (result.minimumApiVersion.isNotBlank()) add("Minimum Vulkan API" to result.minimumApiVersion)
    if (result.missingExtensions.isNotEmpty()) add("Verified missing extensions" to result.missingExtensions.joinToString(", "))
    if (result.unknownExtensions.isNotEmpty()) add("Unknown extension requirements" to result.unknownExtensions.joinToString(", "))
    if (result.missingFeatures.isNotEmpty()) add("Unsupported features" to result.missingFeatures.joinToString(", "))
    if (result.unknownFeatures.isNotEmpty()) add("Unavailable feature queries" to result.unknownFeatures.joinToString(", "))
    if (result.failingLimits.isNotEmpty()) add("Failing properties / limits" to result.failingLimits.joinToString("; "))
    if (result.unknownLimits.isNotEmpty()) add("Unavailable properties / limits" to result.unknownLimits.joinToString("; "))
    if (result.failingFormats.isNotEmpty()) add("Failing formats" to result.failingFormats.joinToString(", "))
    if (result.unknownFormats.isNotEmpty()) add("Unavailable formats" to result.unknownFormats.joinToString(", "))
    if (result.failingRequirementGroups.isNotEmpty()) add("Failed inherited / OR requirements" to result.failingRequirementGroups.joinToString("; "))
    if (result.unknownRequirementGroups.isNotEmpty()) add("Unresolved inherited / OR requirements" to result.unknownRequirementGroups.joinToString("; "))
    if (result.coverageNote.isNotBlank()) add("Evaluator coverage" to result.coverageNote)
}

internal fun profileEvaluationJson(profile: ProfileResult): JSONObject = JSONObject().apply {
    put("name", profile.name)
    put("revision", profile.revision)
    put("status", profile.status)
    put("summary", profileSummary(profile))
    put("checkedRequirementCount", profile.checkedRequirementCount)
    put("metRequirementCount", profile.metRequirementCount)
    put("failedRequirementCount", profile.failedRequirementCount)
    put("unknownRequirementCount", profile.unknownRequirementCount)
    put("checkBreakdown", JSONObject().apply {
        fun putCounts(name: String, counts: ProfileCheckCounts) {
            put(name, JSONObject().apply {
                put("total", counts.total)
                put("met", counts.met)
                put("failed", counts.failed)
                put("unknown", counts.unknown)
            })
        }
        putCounts("extensions", profile.extensionChecks)
        putCounts("features", profile.featureChecks)
        putCounts("properties", profile.propertyChecks)
        putCounts("formats", profile.formatChecks)
        putCounts("inheritedProfiles", profile.groupChecks)
    })
    put("minimumApiVersion", if (profile.minimumApiVersion.isBlank()) JSONObject.NULL else profile.minimumApiVersion)
    put("coverageNote", if (profile.coverageNote.isBlank()) JSONObject.NULL else profile.coverageNote)
    put("missingExtensions", JSONArray(profile.missingExtensions))
    put("unknownExtensions", JSONArray(profile.unknownExtensions))
    put("missingFeatures", JSONArray(profile.missingFeatures))
    put("unknownFeatures", JSONArray(profile.unknownFeatures))
    put("failingLimits", JSONArray(profile.failingLimits))
    put("unknownLimits", JSONArray(profile.unknownLimits))
    put("failingFormats", JSONArray(profile.failingFormats))
    put("unknownFormats", JSONArray(profile.unknownFormats))
    put("failingRequirementGroups", JSONArray(profile.failingRequirementGroups))
    put("unknownRequirementGroups", JSONArray(profile.unknownRequirementGroups))
}

internal fun profileHtmlDetails(profile: ProfileResult): String = buildString {
    append(htmlEscape("${profile.revision}; ${profileSummary(profile)}"))
    profileDetailPairs(profile).forEach { (label, value) ->
        append("<br><span class=\"muted\">")
        append(htmlEscape(label))
        append(": </span>")
        append(htmlEscape(value))
    }
}

@Composable
internal fun ProfileCheckMetricGrid(total: Int, met: Int, failed: Int, unknown: Int, modifier: Modifier = Modifier) {
    val metrics = listOf(
        Triple("Mapped", total.toString(), VulkanTextPrimary),
        Triple("Met", met.toString(), ComposeColor(0xFF73C991)),
        Triple("Unmet", failed.toString(), ComposeColor(0xFFFF8A8A)),
        Triple("Unknown", unknown.toString(), ComposeColor(0xFFA8A8A8))
    )
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val columns = if (maxWidth < 320.dp) 2 else 4
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            metrics.chunked(columns).forEach { rowMetrics ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    rowMetrics.forEach { (label, value, color) ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = VulkanSurfaceTonal,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                Modifier.fillMaxWidth().padding(horizontal = 9.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(1.dp)
                            ) {
                                Text(value, color = color, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                Text(label, color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                            }
                        }
                    }
                    repeat(columns - rowMetrics.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
internal fun ProfileCheckBreakdown(result: ProfileResult) {
    if (result.checkedRequirementCount == 0) {
        Text("No normalized requirement checks are mapped for this catalog entry in this release.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
        return
    }
    ProfileCheckMetricGrid(
        total = result.checkedRequirementCount,
        met = result.metRequirementCount,
        failed = result.failedRequirementCount,
        unknown = result.unknownRequirementCount,
        modifier = Modifier.padding(top = 3.dp, bottom = 2.dp)
    )
    if (result.extensionChecks.total > 0) CapabilityKeyValue("Extensions", profileCheckSummary(result.extensionChecks))
    if (result.featureChecks.total > 0) CapabilityKeyValue("Features", profileCheckSummary(result.featureChecks))
    if (result.propertyChecks.total > 0) CapabilityKeyValue("Properties / limits", profileCheckSummary(result.propertyChecks))
    if (result.formatChecks.total > 0) CapabilityKeyValue("Formats", profileCheckSummary(result.formatChecks))
    if (result.groupChecks.total > 0) CapabilityKeyValue("Inherited profiles", profileCheckSummary(result.groupChecks))
}
