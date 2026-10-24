@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.efishell.vulkanscope

import android.view.Surface
import android.content.Intent
import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import okhttp3.Request
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject


internal const val ANALYSIS_MAX_ENTRIES = 32768
internal const val ANALYSIS_MAX_KEY_LENGTH = 1024
internal const val ANALYSIS_MAX_VALUE_LENGTH = 16384
internal const val ANALYSIS_MAX_WATCHED = 256

internal fun readBoundedAnalysisBytes(input: java.io.InputStream, maxBytes: Int): ByteArray {
    val out = java.io.ByteArrayOutputStream(minOf(maxBytes, 64 * 1024))
    val buffer = ByteArray(8192)
    var total = 0
    while (true) {
        val read = input.read(buffer)
        if (read < 0) break
        if (read == 0) continue
        total += read
        if (total > maxBytes) error("Snapshot exceeds ${maxBytes / (1024 * 1024)} MiB")
        out.write(buffer, 0, read)
    }
    return out.toByteArray()
}

internal fun validateAnalysisSnapshot(obj: JSONObject): JSONObject {
    if (obj.optString("schema") != "VulkanScopeAnalysisSnapshot1") error("Unsupported VulkanScope analysis snapshot")
    val version = obj.optString("applicationVersion", "Unknown")
    if (version.length > 128) error("Snapshot application version is invalid")
    val entries = obj.optJSONObject("entries") ?: error("Snapshot entries are missing")
    var count = 0
    val keys = entries.keys()
    while (keys.hasNext()) {
        val key = keys.next()
        count += 1
        if (count > ANALYSIS_MAX_ENTRIES) error("Snapshot contains too many evidence entries")
        if (key.isEmpty() || key.length > ANALYSIS_MAX_KEY_LENGTH) error("Snapshot contains an invalid evidence key")
        val value = entries.opt(key)
        if (value !is String || value.length > ANALYSIS_MAX_VALUE_LENGTH) error("Snapshot contains an invalid evidence value")
    }
    return obj
}

internal data class VulkanAnalysisDiff(val key: String, val baseline: String?, val current: String?, val state: String) {
    val kind: String get() = key.substringBefore('/').lowercase()
}

internal fun vulkanAnalysisEntries(report: VulkanReport, device: DeviceReport?, display: DisplayReport, mode: DriverMode): JSONObject = JSONObject().apply {
    fun putCompatible(base: String, value: String, index: Int) {
        if (!has(base)) put(base, value) else put("$base/duplicate/$index", value)
    }
    put("identity/loaderVersion", report.loaderVersion)
    put("identity/instanceApiVersion", report.instanceApiVersion)
    put("identity/driverMode", mode.label)
    put("query/reportError", report.error ?: "")
    put("query/instanceExtensionStatus", report.instanceExtensionStatus)
    put("query/instanceExtensionReason", report.instanceExtensionReason)
    put("query/instanceLayerStatus", report.instanceLayerStatus)
    put("query/instanceLayerReason", report.instanceLayerReason)
    put("query/baseReportComplete", report.baseReportComplete.toString())
    report.instanceExtensions.forEachIndexed { index, item -> putCompatible("extension/instance/${item.name}", "present · spec ${item.specVersion}", index) }
    report.instanceLayers.forEachIndexed { index, layer ->
        val layerBase = "layer/instance/${layer.name}/$index"
        put(layerBase, "spec=${layer.specVersion};implementation=${layer.implementationVersion};description=${layer.description}")
        put("$layerBase/extensionQueryStatus", layer.extensionStatus)
        put("$layerBase/extensionQueryReason", layer.extensionReason)
        put("$layerBase/extensionsComplete", layer.extensionsComplete.toString())
        layer.extensions.forEachIndexed { extensionIndex, extension -> put("$layerBase/extension/${extension.name}/$extensionIndex", "present · spec ${extension.specVersion}") }
    }
    if (device != null) {
        put("identity/deviceName", device.name)
        put("identity/deviceApiVersion", device.apiVersion)
        put("identity/driverVersion", device.driverVersionText.ifBlank { device.driverVersion })
        put("identity/vendorId", device.vendorId)
        put("identity/deviceId", device.deviceId)
        put("identity/deviceType", device.deviceType)
        put("query/deviceExtensionStatus", device.deviceExtensionStatus)
        put("query/deviceLayerStatus", device.deviceLayerStatus)
        put("query/deviceLayerReason", device.deviceLayerReason)
        put("query/deviceLayersComplete", device.deviceLayersComplete.toString())
        put("query/deviceExtensionReason", device.deviceExtensionReason)
        put("query/extendedStatus", device.extendedQueryStatus)
        put("query/extendedReason", device.extendedQueryReason)
        put("query/vulkan14Status", device.vulkan14Status)
        put("query/vulkan14Reason", device.vulkan14Reason)
        device.deviceLayers.forEachIndexed { index, layer ->
            val layerBase = "layer/device/${layer.name}/$index"
            put(layerBase, "spec=${layer.specVersion};implementation=${layer.implementationVersion};description=${layer.description}")
            put("$layerBase/extensionQueryStatus", layer.extensionStatus)
            put("$layerBase/extensionQueryReason", layer.extensionReason)
            put("$layerBase/extensionsComplete", layer.extensionsComplete.toString())
            layer.extensions.forEachIndexed { extensionIndex, extension -> put("$layerBase/extension/${extension.name}/$extensionIndex", "present · spec ${extension.specVersion}") }
        }
        device.extensions.forEachIndexed { index, item -> putCompatible("extension/device/${item.name}", "present · spec ${item.specVersion}", index) }
        device.features.forEachIndexed { index, item -> putCompatible("feature/${item.name}", if (item.supported) "supported" else "not supported", index) }
        device.limits.forEachIndexed { index, item -> putCompatible("limit/${item.first}", item.second, index) }
        device.heaps.forEach { heap -> put("memory/heap/${heap.index}", "size=${heap.size};flags=${java.lang.Long.toUnsignedString(heap.flags)}") }
        device.memoryTypes.forEach { type -> put("memory/type/${type.index}", "heap=${type.heap};flags=${java.lang.Long.toUnsignedString(type.flags)}") }
        device.queues.forEach { queue ->
            put("queue/${queue.index}", "count=${queue.count};timestampBits=${queue.timestampBits};flags=${java.lang.Long.toUnsignedString(queue.flags)};granularity=${queue.granularity}")
            put("queue/${queue.index}/videoQueryStatus", queue.videoCodecQueryStatus)
            put("queue/${queue.index}/videoQueryReason", queue.videoCodecQueryReason)
            if (queueVideoCodecEvidenceRetained(queue)) put("queue/${queue.index}/videoCodecOperations", java.lang.Long.toUnsignedString(queue.videoCodecOperations))
        }
        device.formats.forEachIndexed { index, item -> putCompatible("format/${item.name}", "supported=${item.supported};linear=${java.lang.Long.toUnsignedString(item.linear)};optimal=${java.lang.Long.toUnsignedString(item.optimal)};buffer=${java.lang.Long.toUnsignedString(item.buffer)}", index) }
        device.detailedProperties.forEachIndexed { index, item -> put("property/${item.section}/${item.name}/$index", item.value) }
        device.imageFormatQueryResults.forEach { item -> put("imageFormatQuery/${item.name}", "${item.status}|VkResult=${item.vkResult ?: "null"}|Reason=${item.reason}") }
        put("surface/queryStatus", device.surfaceQueryStatus)
        put("surface/queryReason", device.surfaceQueryReason)
        put("surface/available", device.surfaceAvailable.toString())
        put("surface/presentationSupported", device.surfacePresentationSupported.toString())
        put("surface/colorSpaceExtensionStatus", device.surfaceColorSpaceExtensionStatus)
        put("surface/colorSpaceExtensionAvailable", device.surfaceColorSpaceExtensionAvailable.toString())
        put("surface/colorSpaceExtensionEnabled", device.surfaceColorSpaceExtensionEnabled.toString())
        put("surface/dependentWsiQueryStatus", device.surfaceDependentWsiQueryStatus)
        put("surface/formatQueryAttempted", device.surfaceFormatQueryAttempted.toString())
        put("surface/formatQueryResult", if (device.surfaceFormatQueryAttempted) vkResultText(device.surfaceFormatQueryResult) else "Not attempted")
        put("surface/formatQueryResultSecond", if (device.surfaceFormatQuerySecondAttempted) vkResultText(device.surfaceFormatQueryResultSecond) else "Not attempted")
        put("surface/formatQuerySecondAttempted", device.surfaceFormatQuerySecondAttempted.toString())
        put("surface/formatEnumerationComplete", device.surfaceFormatEnumerationComplete.toString())
        put("surface/formatQuerySpecAnomaly", device.surfaceFormatQuerySpecAnomaly.toString())
        put("surface/presentModeEnumerationComplete", device.surfacePresentModeEnumerationComplete.toString())
        put("surface/presentModeQuerySpecAnomaly", device.surfacePresentModeQuerySpecAnomaly.toString())
        device.surfaceCapabilities.forEachIndexed { index, item -> putCompatible("surface/capability/${item.first}", item.second, index) }
        device.surfaceFormats.forEachIndexed { index, item -> putCompatible("surface/format/${item.format}/${item.colorSpace}", "${item.classification};supported=${item.supported}", index) }
        device.presentModes.forEachIndexed { index, item -> putCompatible("surface/presentMode/$item", "present", index) }
        if (device.presentationQueueEvidence.isNotEmpty()) {
            device.presentationQueueEvidence.forEachIndexed { index, item ->
                putCompatible("surface/presentationQueue/${item.queueFamily}/supported", item.supported.toString(), index)
                putCompatible("surface/presentationQueue/${item.queueFamily}/queryResult", item.queryResult?.let(::vkResultText) ?: "Unknown", index)
            }
        } else {
            device.presentationQueues.forEachIndexed { index, item -> putCompatible("surface/presentationQueue/${item.first}", item.second.toString(), index) }
        }
        put("safety/queueRejected", device.queueQuerySafetyRejected.toString())
        put("safety/memoryHeapRejected", device.memoryHeapSafetyRejected.toString())
        put("safety/memoryTypeRejected", device.memoryTypeSafetyRejected.toString())
        put("safety/surfaceQueueRejected", device.surfaceQueueQuerySafetyRejected.toString())
        put("safety/surfaceFormatRejected", device.surfaceFormatQuerySafetyRejected.toString())
        vulkanProfileEvaluations(report, device).forEach { result ->
            val minimum = result.minimumApiVersion.takeIf { it.isNotBlank() }?.let { ";minimum=$it" }.orEmpty()
            put("profile/${result.name}", "${result.status};revision=${result.revision}$minimum;checked=${result.checkedRequirementCount};coverage=${result.coverageNote}")
        }
    }
    put("display/resolution", display.resolution)
    put("display/refreshRate", display.refreshRate)
    put("display/wideGamut", display.wideGamut?.toString() ?: "Unavailable")
    put("display/preferredWideGamut", display.preferredWideGamut)
    put("display/hdrCapabilityStatus", display.hdrCapabilityStatus)
    put("display/minLuminance", display.minLuminance)
    put("display/maxLuminance", display.maxLuminance)
    put("display/averageLuminance", display.averageLuminance)
    display.hdrTypes.forEachIndexed { index, item -> putCompatible("display/hdr/$item", "present", index) }
    display.modes.forEachIndexed { index, item -> put("display/mode/$index", item) }
    put("registry/baseline", report.registryCoverage.baseline)
    put("registry/headerBaseline", report.registryCoverage.headerBaseline)
    put("registry/catalogSchema", report.registryCoverage.catalogSchemaVersion.toString())
    put("registry/reportSchema", report.registryCoverage.reportSchema)
    put("registry/implementedStructCount", report.registryCoverage.implementedPhysicalDeviceStructCount.toString())
    put("registry/runtimeQueryGroupCount", report.registryCoverage.validatedRuntimeQueryGroupCount.toString())
    put("registry/runtimeRegistryTokenReferenceCount", report.registryCoverage.runtimeRegistryTokenReferenceCount.toString())
    put("registry/instanceDependencyCandidateCount", report.registryCoverage.instanceDependencyCandidateCount.toString())
    put("physicalDeviceEnumeration/result", report.physicalDeviceEnumerationResult?.let(::vkResultText) ?: "Unknown")
    put("physicalDeviceEnumeration/complete", report.physicalDeviceEnumerationComplete.toString())
    put("physicalDeviceEnumeration/safetyRejected", report.physicalDeviceEnumerationSafetyRejected.toString())
    put("physicalDeviceEnumeration/reason", report.physicalDeviceEnumerationReason.ifBlank { "None" })
    put("instanceGroup/queryStatus", report.instanceGroupStatus)
    put("instanceGroup/queryReason", report.instanceGroupReason)
    put("instanceGroup/enumerationResult", report.instanceGroupEnumerationResult?.let(::vkResultText) ?: "Unknown")
    put("instanceGroup/enumerationComplete", report.instanceGroupEnumerationComplete.toString())
    report.instanceGroupProperties.forEachIndexed { index, property ->
        put("instanceGroup/$index/section", property.section)
        put("instanceGroup/$index/name", property.name)
        put("instanceGroup/$index/value", property.value)
    }
}
internal fun vulkanAnalysisSnapshot(report: VulkanReport, device: DeviceReport?, display: DisplayReport, mode: DriverMode, applicationVersion: String): JSONObject = JSONObject()
    .put("schema", "VulkanScopeAnalysisSnapshot1")
    .put("applicationVersion", applicationVersion)
    .put("entries", vulkanAnalysisEntries(report, device, display, mode))

internal fun objectStringMap(obj: JSONObject): Map<String, String> = linkedMapOf<String, String>().apply { obj.keys().forEach { key -> put(key, obj.optString(key, "Unavailable")) } }

internal fun snapshotDeviceExtensionsComplete(entries: Map<String, String>): Boolean = entries["query/deviceExtensionStatus"] == "available"

internal fun snapshotSurfaceFormatsComplete(entries: Map<String, String>): Boolean =
    entries["surface/available"] == "true" && entries["surface/formatQuerySecondAttempted"] == "true" &&
        entries["surface/formatQueryResultSecond"] == "0" && entries["safety/surfaceFormatRejected"] != "true"

internal fun vulkanSnapshotDiff(baseline: JSONObject, current: JSONObject): List<VulkanAnalysisDiff> {
    val old = objectStringMap(baseline.optJSONObject("entries") ?: JSONObject())
    val now = objectStringMap(current.optJSONObject("entries") ?: JSONObject())
    val currentDeviceExtensionsComplete = snapshotDeviceExtensionsComplete(now)
    val currentSurfaceFormatsComplete = snapshotSurfaceFormatsComplete(now)
    return (old.keys + now.keys).toSortedSet().map { key ->
        val before = old[key]
        val after = now[key]
        val beforeSupported = before == "supported" || before == "present" || before?.contains("supported=true") == true || before?.startsWith("present · spec ") == true
        val afterUnsupported = after == "not supported" || after?.contains("supported=false") == true
        val removalHasCompleteEvidence = when {
            key.startsWith("extension/device/") -> currentDeviceExtensionsComplete
            key.startsWith("surface/format/") -> currentSurfaceFormatsComplete
            else -> true
        }
        val regression = before != null && when {
            key.startsWith("feature/") -> beforeSupported && afterUnsupported
            key.startsWith("extension/device/") || key.startsWith("surface/format/") -> removalHasCompleteEvidence && (after == null || beforeSupported && afterUnsupported)
            else -> false
        }
        val state = when {
            before == null -> "Added"
            after == null && regression -> "Removed · regression candidate"
            after == null -> "Removed · evidence incomplete/changed"
            before == after -> "Unchanged"
            regression -> "Changed · regression candidate"
            else -> "Changed"
        }
        VulkanAnalysisDiff(key, before, after, state)
    }
}
internal fun vulkanExtensionQueryGroup(name: String): String? = ISOLATED_EXTENSION_GROUPS.entries.firstOrNull { it.value == name }?.key

internal fun analysisQueryTokens(query: String): List<String> = Regex("\\\"([^\\\"]+)\\\"|\\S+").findAll(query).map { it.groups[1]?.value ?: it.value }.toList()

internal fun analysisKind(key: String): String = key.substringBefore('/').lowercase()

internal fun extensionVendorToken(name: String): String = name.removePrefix("VK_").substringBefore('_').uppercase()

internal fun matchesAnalysisQuery(row: VulkanAnalysisDiff, query: String): Boolean {
    if (query.isBlank()) return true
    val refName = row.key.split('/').firstOrNull { it.startsWith("VK_") }
    val ref = refName?.let(::vulkanExtensionReference)
    return analysisQueryTokens(query).all { token ->
        val split = token.split(':', limit = 2)
        if (split.size == 1) {
            row.key.contains(token, true) || row.state.contains(token, true) || row.baseline?.contains(token, true) == true || row.current?.contains(token, true) == true
        } else {
            val field = split[0].lowercase()
            val value = split[1]
            when (field) {
                "state" -> row.state.contains(value, true)
                "kind" -> analysisKind(row.key).equals(value, true)
                "changed" -> (row.state != "Unchanged") == value.equals("true", true)
                "vendor" -> refName?.let { extensionVendorToken(it).equals(value, true) } == true
                "scope" -> row.key.startsWith("extension/${value.lowercase()}/", true)
                "core" -> ref?.promotedTo?.contains(value, true) == true
                else -> row.key.contains(token, true) || row.baseline?.contains(token, true) == true || row.current?.contains(token, true) == true
            }
        }
    }
}

internal fun extensionDependencyTokens(expression: String): List<String> = Regex("VK_(?:VERSION_[0-9_]+|[A-Z0-9]+_[A-Za-z0-9_]+)").findAll(expression).map { it.value }.distinct().toList()

internal data class DependencyGraphEntry(val token: String, val depth: Int, val parent: String?)

internal enum class MinimumProfileConfirmationKind { SAVE, LOAD, DELETE }

internal data class MinimumProfileConfirmation(val kind: MinimumProfileConfirmationKind, val name: String)

internal fun dependencyGraphEntries(root: String, maxDepth: Int = 4, maxNodes: Int = 64): List<DependencyGraphEntry> {
    if (!root.startsWith("VK_") || maxDepth < 0 || maxNodes < 1) return emptyList()
    val out = mutableListOf<DependencyGraphEntry>()
    val seen = linkedSetOf<String>()
    val queue = ArrayDeque<DependencyGraphEntry>()
    queue.add(DependencyGraphEntry(root, 0, null))
    while (queue.isNotEmpty() && out.size < maxNodes) {
        val current = queue.removeFirst()
        if (!seen.add(current.token)) continue
        out += current
        if (current.depth >= maxDepth || current.token.startsWith("VK_VERSION_")) continue
        val ref = vulkanExtensionReference(current.token)
        extensionDependencyTokens(ref.depends).forEach { child ->
            if (child !in seen && out.size + queue.size < maxNodes) queue.add(DependencyGraphEntry(child, current.depth + 1, current.token))
        }
    }
    return out
}

internal fun dependencyRuntimeEvidence(token: String, report: VulkanReport, device: DeviceReport?): String {
    if (token.startsWith("VK_VERSION_")) {
        val match = Regex("""VK_VERSION_(\d+)_(\d+)""").matchEntire(token) ?: return "Unknown core dependency"
        val requiredMajor = match.groupValues[1].toInt()
        val requiredMinor = match.groupValues[2].toInt()
        val apiText = device?.apiVersion?.trim().orEmpty()
        val apiParts = apiText.split('.')
        val actualMajor = apiParts.getOrNull(0)?.toIntOrNull()
        val actualMinor = apiParts.getOrNull(1)?.toIntOrNull()
        if (actualMajor == null || actualMinor == null) return "Unknown · runtime device API evidence is unavailable or unparsable"
        return if (actualMajor > requiredMajor || actualMajor == requiredMajor && actualMinor >= requiredMinor) "Runtime API satisfies $requiredMajor.$requiredMinor" else "Runtime API does not expose $requiredMajor.$requiredMinor"
    }
    if (report.instanceExtensions.any { it.name == token }) return "Enumerated · instance"
    if (device?.extensions.orEmpty().any { it.name == token }) return "Enumerated · device"
    val referenceType = vulkanExtensionReference(token).type.lowercase()
    fun absence(scope: String, status: String?, reason: String): String = when (status) {
        "available" -> "Not enumerated by completed $scope extension evidence"
        "incomplete" -> "Unknown · $scope extension enumeration is incomplete"
        "unavailable" -> "Unknown · $scope extension enumeration is unavailable${reason.takeIf { it.isNotBlank() }?.let { ": $it" }.orEmpty()}"
        "not_applicable" -> "Not applicable · $scope extension enumeration does not apply"
        else -> "Unknown · $scope extension enumeration is not authoritative"
    }
    return when (referenceType) {
        "instance" -> absence("instance", report.instanceExtensionStatus, report.instanceExtensionReason)
        "device" -> if (device == null) "Unknown · no selected physical-device evidence" else absence("device", device.deviceExtensionStatus, device.deviceExtensionReason)
        else -> {
            val instanceAuthoritative = report.instanceExtensionStatus == "available"
            val deviceAuthoritative = device?.deviceExtensionStatus == "available"
            if (instanceAuthoritative && deviceAuthoritative) "Not enumerated by completed instance/device extension evidence" else "Unknown · extension scope/enumeration evidence is not authoritative enough to prove absence"
        }
    }
}

internal fun matchesExtensionExplorerQuery(name: String, scope: String, enumerated: Boolean, query: String): Boolean {
    if (query.isBlank()) return true
    val ref = vulkanExtensionReference(name)
    return analysisQueryTokens(query).all { token ->
        val split = token.split(':', limit = 2)
        if (split.size == 1) name.contains(token, true) || scope.contains(token, true) || ref.depends.contains(token, true) || ref.promotedTo.contains(token, true)
        else when (split[0].lowercase()) {
            "vendor" -> extensionVendorToken(name).equals(split[1], true)
            "scope" -> scope.equals(split[1], true)
            "core", "promoted" -> ref.promotedTo.contains(split[1], true)
            "depends" -> ref.depends.contains(split[1], true)
            "requires" -> ref.requires.contains(split[1], true)
            "deprecated" -> ref.deprecatedBy.contains(split[1], true) || ref.obsoletedBy.contains(split[1], true)
            "command" -> ref.commands.any { it.contains(split[1], true) }
            "enum" -> ref.enums.any { it.contains(split[1], true) }
            "handler" -> ((vulkanExtensionQueryGroup(name) != null || ref.queryGroup.isNotBlank()) == split[1].equals("true", true))
            "enumerated", "supported" -> enumerated == split[1].equals("true", true)
            else -> name.contains(token, true)
        }
    }
}

internal fun matchesFormatExplorerQuery(format: FormatEntry, query: String): Boolean {
    if (query.isBlank()) return true
    val decoded = formatFeatureFlags(format.linear) + " " + formatFeatureFlags(format.optimal) + " " + formatFeatureFlags(format.buffer)
    return analysisQueryTokens(query).all { token ->
        val split = token.split(':', limit = 2)
        if (split.size == 1) format.name.contains(token, true) || decoded.contains(token, true)
        else when (split[0].lowercase()) {
            "name" -> format.name.contains(split[1], true)
            "supported" -> format.supported == split[1].equals("true", true)
            "feature" -> decoded.contains(split[1], true)
            else -> format.name.contains(token, true) || decoded.contains(token, true)
        }
    }
}

internal fun databaseReportUrl(id: String): String = OFFICIAL_DATABASE_WEB_URL.trimEnd('/') + "/#reports/" + Uri.encode(id) + "/Overview"

internal data class DatabaseReportSummary(
    val id: String,
    val submittedAt: String,
    val gpuName: String,
    val vendorId: String,
    val deviceId: String,
    val driverMode: String,
    val driverVersion: String,
    val deviceApiVersion: String,
    val manufacturer: String,
    val model: String
)

internal data class DatabaseReportCursor(val submittedAt: String, val id: String)

internal data class DatabaseReportPage(val reports: List<DatabaseReportSummary>, val nextCursor: DatabaseReportCursor?)

internal suspend fun fetchDatabaseReportPage(context: Context, cursor: DatabaseReportCursor?): DatabaseReportPage = withContext(Dispatchers.IO) {
    if (!hasValidatedInternet(context)) error("Database list is unavailable without a validated internet connection")
    val baseUrl = OFFICIAL_DATABASE_API_ENDPOINT.toHttpUrlOrNull() ?: error("The official VulkanScope Database endpoint is invalid")
    if (baseUrl.scheme != "https" || baseUrl.host != "vulkanscope-database-api.vulkanscope.workers.dev" || baseUrl.username.isNotEmpty() || baseUrl.password.isNotEmpty() || baseUrl.query != null || baseUrl.fragment != null || baseUrl.encodedPath != "/") error("The official VulkanScope Database endpoint is invalid")
    val builder = baseUrl.newBuilder().addPathSegments("v1/reports").addQueryParameter("limit", "50")
    cursor?.let {
        if (!it.id.matches(Regex("[a-f0-9]{64}"))) error("Database list cursor is invalid")
        builder.addQueryParameter("beforeSubmittedAt", it.submittedAt)
        builder.addQueryParameter("beforeId", it.id)
    }
    val request = Request.Builder().url(builder.build()).header("Accept", "application/json").get().build()
    databaseHttpClient.newCall(request).execute().use { response ->
        val body = readResponseTextLimited(response.body, ANALYSIS_DATABASE_COMPARE_MAX_BYTES)
        if (!response.isSuccessful) {
            val message = runCatching { JSONObject(body).optString("error") }.getOrDefault("").ifBlank { "HTTP ${response.code}" }
            error("Database list fetch failed: $message")
        }
        val payload = JSONObject(body)
        val array = payload.optJSONArray("reports") ?: JSONArray()
        val reports = buildList {
            for (index in 0 until minOf(array.length(), 50)) {
                val item = array.optJSONObject(index) ?: continue
                val id = item.optString("id").lowercase()
                if (!id.matches(Regex("[a-f0-9]{64}"))) continue
                add(
                    DatabaseReportSummary(
                        id = id,
                        submittedAt = item.optString("submitted_at"),
                        gpuName = item.optString("gpu_name", "Unknown"),
                        vendorId = item.optString("vendor_id", "Unknown"),
                        deviceId = item.optString("device_id", "Unknown"),
                        driverMode = item.optString("driver_mode", "Unknown"),
                        driverVersion = item.optString("driver_version", "Unknown"),
                        deviceApiVersion = item.optString("device_api_version", "Unknown"),
                        manufacturer = item.optString("manufacturer", "Unknown"),
                        model = item.optString("model", "Unknown")
                    )
                )
            }
        }
        val next = payload.optJSONObject("nextCursor")?.let {
            val submittedAt = it.optString("submittedAt")
            val id = it.optString("id").lowercase()
            if (submittedAt.isNotBlank() && id.matches(Regex("[a-f0-9]{64}"))) DatabaseReportCursor(submittedAt, id) else null
        }
        DatabaseReportPage(reports, next)
    }
}

internal suspend fun fetchDatabaseTechnicalReport(context: Context, reportId: String): JSONObject = withContext(Dispatchers.IO) {
    if (!hasValidatedInternet(context)) error("Database fetch is unavailable without a validated internet connection")
    val id = reportId.trim().lowercase()
    if (!id.matches(Regex("[a-f0-9]{64}"))) error("Database report id must be a lowercase 64-character SHA-256 id")
    val baseUrl = OFFICIAL_DATABASE_API_ENDPOINT.toHttpUrlOrNull() ?: error("The official VulkanScope Database endpoint is invalid")
    if (baseUrl.scheme != "https" || baseUrl.host != "vulkanscope-database-api.vulkanscope.workers.dev" || baseUrl.username.isNotEmpty() || baseUrl.password.isNotEmpty() || baseUrl.query != null || baseUrl.fragment != null || baseUrl.encodedPath != "/") error("The official VulkanScope Database endpoint is invalid")
    val url = baseUrl.newBuilder().addPathSegments("v1/reports/$id").addQueryParameter("compact", "1").build()
    val request = Request.Builder().url(url).header("Accept", "application/json").get().build()
    databaseHttpClient.newCall(request).execute().use { response ->
        val body = readResponseTextLimited(response.body, ANALYSIS_DATABASE_COMPARE_MAX_BYTES)
        if (!response.isSuccessful) {
            val message = runCatching { JSONObject(body).optString("error") }.getOrDefault("").ifBlank { "HTTP ${response.code}" }
            error("Database report fetch failed: $message")
        }
        val payload = JSONObject(body)
        payload.optJSONObject("technicalReport") ?: error("Database report does not contain technicalReport")
    }
}

internal fun loadSavedMinimumProfiles(raw: String?): Map<String, String> {
    if (raw.isNullOrBlank()) return emptyMap()
    return runCatching {
        val root = JSONObject(raw)
        if (root.optString("schema") != "VulkanScopeMinimumProfiles1") error("Invalid minimum profile store")
        val profiles = root.optJSONObject("profiles") ?: JSONObject()
        linkedMapOf<String, String>().apply {
            val keys = profiles.keys().asSequence().toList().sorted().take(ANALYSIS_CUSTOM_PROFILE_MAX_PROFILES)
            keys.forEach { name ->
                val rules = profiles.optString(name)
                if (name.isNotBlank() && name.length <= 128 && rules.length <= 16_384 && rules.lineSequence().filter { it.isNotBlank() }.count() <= ANALYSIS_CUSTOM_PROFILE_MAX_RULES) put(name, rules)
            }
        }
    }.getOrDefault(emptyMap())
}

internal fun encodeSavedMinimumProfiles(profiles: Map<String, String>): String = JSONObject().apply {
    put("schema", "VulkanScopeMinimumProfiles1")
    put("profiles", JSONObject().apply { profiles.toSortedMap().entries.take(ANALYSIS_CUSTOM_PROFILE_MAX_PROFILES).forEach { (name, rules) -> put(name, rules) } })
}.toString()

internal class AnalysisWorkspaceState(initialWatched: Set<String>, initialProfiles: Map<String, String>, initialAbWorkflowStage: String) {
    var tab by mutableIntStateOf(0)
    var contentPage by mutableIntStateOf(0)
    var savedProfilesPage by mutableIntStateOf(0)
    var baseline by mutableStateOf<JSONObject?>(null)
    var analysisStatus by mutableStateOf("No baseline imported")
    var diffQuery by mutableStateOf("")
    var diffStateFilter by mutableStateOf("All")
    var diffKindFilter by mutableStateOf("All")
    var includeUnchanged by mutableStateOf(false)
    var globalQuery by mutableStateOf("")
    var globalKindFilter by mutableStateOf("All")
    var requirementInput by mutableStateOf("VK_KHR_dynamic_rendering")
    var profileQuery by mutableStateOf("")
    var profileStatusFilter by mutableStateOf("All")
    var customProfileName by mutableStateOf("My minimum")
    var customProfileRules by mutableStateOf("api>=1.3\nextension:VK_KHR_dynamic_rendering")
    var customProfileStatus by mutableStateOf("Local custom profiles do not modify Vulkan capability evidence")
    var pendingMinimumConfirmation by mutableStateOf<MinimumProfileConfirmation?>(null)
    var savedProfiles by mutableStateOf(initialProfiles)
    var graphInput by mutableStateOf("VK_KHR_swapchain")
    var graphDepth by mutableIntStateOf(4)
    var rawQuery by mutableStateOf("")
    var databaseLookupMode by mutableIntStateOf(0)
    var databaseReportId by mutableStateOf("")
    var databaseStatus by mutableStateOf("No Database report loaded")
    var databaseLoading by mutableStateOf(false)
    var databaseRemoteLeaves by mutableStateOf<List<RawJsonLeaf>>(emptyList())
    var databaseIncludeUnchanged by mutableStateOf(false)
    var databaseListQuery by mutableStateOf("")
    var databaseReports by mutableStateOf<List<DatabaseReportSummary>>(emptyList())
    var databaseListCursor by mutableStateOf<DatabaseReportCursor?>(null)
    var databaseListStatus by mutableStateOf("Press Load reports to fetch a bounded public Database page")
    var databaseListLoading by mutableStateOf(false)
    var history by mutableStateOf<List<AnalysisHistoryRecord>>(emptyList())
    var historyStatus by mutableStateOf("Local history is bounded and stored only on this device")
    var pendingHistoryDelete by mutableStateOf<AnalysisHistoryRecord?>(null)
    var pendingHistoryDeleteAll by mutableStateOf(false)
    var pendingWatchDelete by mutableStateOf<String?>(null)
    var pendingWatchDeleteAll by mutableStateOf(false)
    var abWorkflowStage by mutableStateOf(initialAbWorkflowStage)
    var abWorkflowStatus by mutableStateOf("Guided A/B is idle")
    var watchInput by mutableStateOf("")
    var watchQuery by mutableStateOf("")
    var watchStateFilter by mutableStateOf("All")
    var watched by mutableStateOf(initialWatched)
    var selectedEvidence by mutableStateOf<Pair<String, String>?>(null)
    var testRunning by mutableStateOf(false)
    var testResult by mutableStateOf<JSONObject?>(null)
}

internal enum class AnalysisStorageAction {
    IMPORT_SNAPSHOT,
    EXPORT_SNAPSHOT,
    IMPORT_MINIMUM_PROFILE,
    EXPORT_MINIMUM_PROFILE,
    EXPORT_TECHNICAL_REPORT
}

internal data class AnalysisWorkspaceModel(
    val state: AnalysisWorkspaceState,
    val diffRows: List<VulkanAnalysisDiff>,
    val globalResults: List<Pair<String, String>>,
    val diagnostics: List<QueryDiagnosticRow>,
    val timingsMs: Map<String, Long>,
    val requirementReference: VulkanExtensionReference?,
    val requirementEvaluations: List<RequirementEvaluation>,
    val profileResults: List<ProfileResult>,
    val customMinimumEvaluations: List<CustomMinimumEvaluation>,
    val graphRootToken: String,
    val graphRootRef: VulkanExtensionReference?,
    val graphEntries: List<DependencyGraphEntry>,
    val visualGraphNodes: List<VulkanGraphNode>,
    val presentationPaths: List<PresentationEvidencePath>,
    val rawLeaves: List<RawJsonLeaf>,
    val databaseDiff: List<GenericAnalysisDiff>,
    val systemHistory: AnalysisHistoryRecord?,
    val turnipHistory: AnalysisHistoryRecord?,
    val driverDiffRows: List<VulkanAnalysisDiff>,
    val watchedEvidence: Map<String, Pair<Int, List<Pair<String, String>>>>,
    val visibleWatched: List<String>,
    val canAddWatch: Boolean,
    val capabilityScore: CapabilityEvidenceScore,
    val lastSharedReportId: String,
    val sharedReportUrl: String,
    val selfTestsAvailable: Boolean,
    val canSwitchSystem: Boolean,
    val canSwitchTurnip: Boolean,
    val importSnapshot: () -> Unit,
    val exportSnapshot: () -> Unit,
    val importMinimumProfile: () -> Unit,
    val exportMinimumProfile: () -> Unit,
    val importSnapshotFile: suspend (File) -> Result<String>,
    val exportSnapshotFile: suspend (File) -> Result<String>,
    val importMinimumProfileFile: suspend (File) -> Result<String>,
    val exportMinimumProfileFile: suspend (File) -> Result<String>,
    val exportRawTechnicalReportFile: suspend (File) -> Result<String>,
    val saveMinimumProfile: () -> Unit,
    val loadMinimumProfile: (String) -> Unit,
    val deleteMinimumProfile: (String) -> Unit,
    val exportRawTechnicalReport: () -> Unit,
    val fetchDatabaseReport: (String) -> Unit,
    val fetchDatabaseReportList: (Boolean) -> Unit,
    val useHistoryAsBaseline: (AnalysisHistoryRecord) -> Unit,
    val deleteHistory: (AnalysisHistoryRecord) -> Unit,
    val deleteAllHistory: () -> Unit,
    val addWatched: (String) -> Unit,
    val persistWatched: (Set<String>) -> Unit,
    val switchToSystem: () -> Unit,
    val switchToTurnip: () -> Unit,
    val startDriverAbWorkflow: () -> Unit,
    val cancelDriverAbWorkflow: () -> Unit,
    val shareLink: () -> Unit,
    val copyLink: () -> Unit,
    val runSelfTests: () -> Unit
)

@Composable
internal fun rememberAnalysisWorkspaceModel(
    report: VulkanReport,
    device: DeviceReport?,
    display: DisplayReport,
    mode: DriverMode,
    turnipSupport: TurnipSupport,
    collectionStatus: CollectionStatus,
    queryTimingMs: Map<String, Long>,
    onDriverModeChanged: (DriverMode) -> Unit,
    onStorageAction: (AnalysisStorageAction) -> Unit
): AnalysisWorkspaceModel {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? MainActivity
    val scope = rememberCoroutineScope()
    val prefs = remember { context.getSharedPreferences("analysis_tools", Context.MODE_PRIVATE) }
    val initialProfiles = remember { loadSavedMinimumProfiles(prefs.getString("minimum_profiles", null)) }
    val state = remember { AnalysisWorkspaceState(prefs.getStringSet("watched", emptySet())?.toSet() ?: emptySet(), initialProfiles, prefs.getString("ab_workflow_stage", "idle") ?: "idle") }
    val packageInfo = remember(context) { runCatching { context.packageManager.getPackageInfo(context.packageName, 0) }.getOrNull() }
    val applicationVersion = packageInfo?.versionName ?: "Unknown"
    val current = remember(report, device, display, mode, applicationVersion) { vulkanAnalysisSnapshot(report, device, display, mode, applicationVersion) }
    val currentEntries = remember(current) { objectStringMap(current.optJSONObject("entries") ?: JSONObject()) }
    val technicalLeavesNeeded = state.tab == 8 || state.tab == 9
    val currentRawLeaves = remember(report, display, mode, technicalLeavesNeeded) {
        if (technicalLeavesNeeded) flattenJson(technicalReportJson(context, report, display, mode)) else emptyList()
    }
    LaunchedEffect(report, device, mode, applicationVersion, collectionStatus, state.abWorkflowStage, turnipSupport) {
        val complete = report.baseReportComplete && report.error == null && device != null && collectionStatus != CollectionStatus.COLLECTING
        if (complete) {
            val saved = withContext(Dispatchers.IO) { saveAnalysisHistoryRecord(context, applicationVersion, mode.label, device.name, current) }
            state.history = withContext(Dispatchers.IO) { loadAnalysisHistoryRecords(context) }
            state.historyStatus = if (saved != null) "Current completed session is retained in bounded local history" else "Current session could not be persisted because local storage was unavailable or the bounded history size was exceeded"
            when (state.abWorkflowStage) {
                "collect-system" -> if (mode == DriverMode.SYSTEM) {
                    if (turnipSupport == TurnipSupport.SUPPORTED) {
                        state.abWorkflowStage = "collect-turnip"
                        state.abWorkflowStatus = "System session retained; switching to Turnip for the second guided collection"
                        prefs.edit().putString("ab_workflow_stage", state.abWorkflowStage).apply()
                        onDriverModeChanged(DriverMode.TURNIP)
                    } else {
                        state.abWorkflowStage = "blocked"
                        state.abWorkflowStatus = "System session retained, but no validated compatible Turnip driver is available"
                        prefs.edit().putString("ab_workflow_stage", state.abWorkflowStage).apply()
                    }
                }
                "collect-turnip" -> if (mode == DriverMode.TURNIP) {
                    state.abWorkflowStage = "complete"
                    state.abWorkflowStatus = "Guided System ↔ Turnip collection completed; latest retained sessions are ready to compare"
                    prefs.edit().putString("ab_workflow_stage", state.abWorkflowStage).apply()
                }
            }
        } else {
            state.history = withContext(Dispatchers.IO) { loadAnalysisHistoryRecords(context) }
        }
    }
    val diffRows = remember(state.baseline, current, state.includeUnchanged, state.diffQuery, state.diffStateFilter, state.diffKindFilter) {
        state.baseline?.let { vulkanSnapshotDiff(it, current) }.orEmpty().filter { row ->
            val stateOk = state.diffStateFilter == "All" || when (state.diffStateFilter) {
                "Added" -> row.state == "Added"
                "Removed" -> row.state.startsWith("Removed")
                "Changed" -> row.state.startsWith("Changed")
                "Regression" -> row.state.contains("regression candidate")
                else -> true
            }
            val kindOk = state.diffKindFilter == "All" || row.kind.equals(state.diffKindFilter, true)
            (state.includeUnchanged || row.state != "Unchanged") && stateOk && kindOk && matchesAnalysisQuery(row, state.diffQuery)
        }
    }
    val globalResults = remember(currentEntries, state.globalQuery, state.globalKindFilter) {
        val query = state.globalQuery.trim()
        if (query.length < 2) emptyList() else currentEntries.entries.asSequence().filter { (key, value) ->
            val kind = key.substringBefore('/').lowercase()
            (state.globalKindFilter == "All" || kind.equals(state.globalKindFilter, true)) && (key.contains(query, true) || value.contains(query, true))
        }.take(ANALYSIS_GLOBAL_SEARCH_VISIBLE_LIMIT).map { it.key to it.value }.toList()
    }
    val diagnostics = remember(currentEntries, queryTimingMs) { queryDiagnostics(currentEntries, queryTimingMs) }
    val requirementReference = remember(state.requirementInput) {
        val token = state.requirementInput.trim()
        if (token.startsWith("VK_")) vulkanExtensionReference(token) else null
    }
    val requirementEvaluations = remember(requirementReference, state.requirementInput, currentEntries) {
        val token = state.requirementInput.trim()
        if (token.isBlank()) emptyList() else {
            val expressions = requirementReference?.let { listOf(it.depends, it.requires, it.promotedTo).filter { value -> value.isNotBlank() } }.orEmpty()
            (listOf(token) + expressions.flatMap(::requirementTokens)).distinct().take(128).map { evaluateRequirementToken(it, currentEntries) }
        }
    }
    val profileResults = remember(report, device, state.profileQuery, state.profileStatusFilter) {
        vulkanProfileEvaluations(report, device).filter { result -> (state.profileStatusFilter == "All" || result.status == state.profileStatusFilter) && result.name.contains(state.profileQuery, true) }
    }
    val customMinimumEvaluations = remember(state.customProfileRules, currentEntries) {
        state.customProfileRules.lineSequence().map { it.trim() }.filter { it.isNotBlank() }.take(ANALYSIS_CUSTOM_PROFILE_MAX_RULES).map { evaluateCustomMinimumRule(it, currentEntries) }.toList()
    }
    val graphRootToken = state.graphInput.trim()
    val graphRootRef = remember(graphRootToken) { if (graphRootToken.startsWith("VK_")) vulkanExtensionReference(graphRootToken) else null }
    val graphEntries = remember(graphRootToken, state.graphDepth) { dependencyGraphEntries(graphRootToken, state.graphDepth) }
    val visualGraphNodes = remember(graphEntries, report, device) { graphEntries.take(24).map { entry -> VulkanGraphNode(entry.token, entry.depth, entry.parent, dependencyRuntimeEvidence(entry.token, report, device)) } }
    val presentationPaths = remember(currentEntries) { presentationEvidencePaths(currentEntries) }
    val rawLeaves = remember(currentRawLeaves, state.rawQuery) {
        val query = state.rawQuery.trim()
        if (query.isBlank()) currentRawLeaves.take(ANALYSIS_RAW_JSON_VISIBLE_LIMIT) else currentRawLeaves.asSequence().filter { it.path.contains(query, true) || it.value.contains(query, true) }.take(ANALYSIS_RAW_JSON_VISIBLE_LIMIT).toList()
    }
    val databaseDiff = remember(state.databaseRemoteLeaves, currentRawLeaves, state.databaseIncludeUnchanged) {
        if (state.databaseRemoteLeaves.isEmpty()) emptyList() else genericDiff(rawLeavesToMap(state.databaseRemoteLeaves), rawLeavesToMap(currentRawLeaves)).filter { state.databaseIncludeUnchanged || it.state != "Unchanged" }
    }
    val systemHistory = remember(state.history) { state.history.firstOrNull { it.driverMode == DriverMode.SYSTEM.label } }
    val turnipHistory = remember(state.history) { state.history.firstOrNull { it.driverMode == DriverMode.TURNIP.label } }
    val driverDiffRows = remember(systemHistory, turnipHistory) {
        if (systemHistory == null || turnipHistory == null) emptyList() else vulkanSnapshotDiff(systemHistory.snapshot, turnipHistory.snapshot).filter { it.state != "Unchanged" }
    }
    val watchedEvidence = remember(currentEntries, state.watched) {
        state.watched.associateWith { token ->
            var count = 0
            val preview = ArrayList<Pair<String, String>>(10)
            currentEntries.forEach { (key, value) ->
                if (key.contains(token, true) || value.contains(token, true)) { count += 1; if (preview.size < 10) preview += key to value }
            }
            count to preview.toList()
        }
    }
    val visibleWatched = remember(state.watched, watchedEvidence, state.watchQuery, state.watchStateFilter) {
        state.watched.sorted().filter { token ->
            val matched = (watchedEvidence[token]?.first ?: 0) > 0
            val stateOk = state.watchStateFilter == "All" || state.watchStateFilter == "Matched" && matched || state.watchStateFilter == "Missing" && !matched
            stateOk && (state.watchQuery.isBlank() || token.contains(state.watchQuery, true))
        }
    }
    fun resolveWatchInput(raw: String): String? {
        val input = raw.trim()
        if (input.length !in 2..256) return null
        val direct = evidenceTokenForReference(input, input).trim().take(256)
        val canonical = Regex("^(VK_[A-Z0-9_]+|Vk[A-Za-z0-9_]+|vk[A-Za-z0-9_]+)$", RegexOption.IGNORE_CASE).matches(direct)
        if (canonical) return direct
        val matchedEntry = currentEntries.entries.firstOrNull { entry -> entry.key.contains(input, true) || entry.value.contains(input, true) } ?: return null
        return evidenceTokenForReference(matchedEntry.key, matchedEntry.value).trim().take(256).ifBlank { null }
    }
    val canAddWatch = remember(state.watchInput, state.watched, currentEntries) {
        val token = resolveWatchInput(state.watchInput)
        token != null && token !in state.watched && state.watched.size < ANALYSIS_MAX_WATCHED
    }
    val capabilityScore = remember(report, device) { calculateCapabilityEvidenceScore(report, device) }
    val sharePrefs = remember { context.getSharedPreferences("database_share", Context.MODE_PRIVATE) }
    val lastSharedReportId = sharePrefs.getString("last_report_id", "").orEmpty()
    val sharedReportUrl = remember(lastSharedReportId) { if (lastSharedReportId.matches(Regex("[a-f0-9]{64}"))) databaseReportUrl(lastSharedReportId) else OFFICIAL_DATABASE_WEB_URL }
    val persistProfiles = { profiles: Map<String, String> -> prefs.edit().putString("minimum_profiles", encodeSavedMinimumProfiles(profiles)).apply() }
    val addWatched: (String) -> Unit = { raw ->
        val token = resolveWatchInput(raw)
        if (token != null && token !in state.watched && state.watched.size < ANALYSIS_MAX_WATCHED) {
            state.watched = state.watched + token
            prefs.edit().putStringSet("watched", state.watched).apply()
        }
    }
    val importSnapshotFile: suspend (File) -> Result<String> = { selected ->
        val result = withContext(Dispatchers.IO) {
            runCatching {
                val file = validatedSharedStorageImportFile(selected, setOf("json"), ANALYSIS_MAX_SNAPSHOT_BYTES.toLong())
                val bytes = FileInputStream(file).use { input -> readBoundedAnalysisBytes(input, ANALYSIS_MAX_SNAPSHOT_BYTES) }
                validateAnalysisSnapshot(JSONObject(bytes.toString(Charsets.UTF_8)))
            }
        }
        result.onSuccess { snapshot ->
            state.baseline = snapshot
            state.analysisStatus = "Baseline imported · ${snapshot.optString("applicationVersion", "Unknown version")}" 
        }.onFailure { state.analysisStatus = it.message ?: "Analysis snapshot import failed" }
        result.map { "Analysis snapshot imported" }
    }
    val exportSnapshotFile: suspend (File) -> Result<String> = { destination ->
        val result = withContext(Dispatchers.IO) {
            runCatching {
                val target = validatedSharedStorageDestination(destination.parentFile ?: error("Destination folder is unavailable"), destination.name, setOf("json"))
                val validated = validateAnalysisSnapshot(JSONObject(current.toString()))
                val bytes = validated.toString(2).toByteArray(Charsets.UTF_8)
                if (bytes.size > ANALYSIS_MAX_SNAPSHOT_BYTES) error("Current analysis snapshot exceeds 8 MiB and was not truncated")
                writeSharedStorageBytes(target, bytes, ANALYSIS_MAX_SNAPSHOT_BYTES)
                target
            }
        }
        result.onSuccess { state.analysisStatus = "Analysis snapshot exported · ${it.absolutePath}" }
            .onFailure { state.analysisStatus = it.message ?: "Analysis snapshot export failed" }
        result.map { "Saved ${it.name}" }
    }
    val importMinimumProfileFile: suspend (File) -> Result<String> = { selected ->
        val result = withContext(Dispatchers.IO) {
            runCatching {
                val file = validatedSharedStorageImportFile(selected, setOf("json"), 256L * 1024L)
                val bytes = FileInputStream(file).use { input -> readBoundedAnalysisBytes(input, 256 * 1024) }
                val root = JSONObject(bytes.toString(Charsets.UTF_8))
                if (root.optString("schema") != "VulkanScopeMinimumProfile1") error("Unsupported minimum profile schema")
                val name = root.optString("name").trim()
                val array = root.optJSONArray("rules") ?: error("Minimum profile rules are missing")
                if (name.isBlank() || name.length > 128 || array.length() !in 1..ANALYSIS_CUSTOM_PROFILE_MAX_RULES) error("Minimum profile bounds are invalid")
                val rules = (0 until array.length()).map { array.optString(it) }.filter { it.isNotBlank() }
                if (rules.size != array.length() || rules.any { it.length > 512 }) error("Minimum profile contains an invalid rule")
                name to rules.joinToString("\n")
            }
        }
        result.onSuccess { (name, rules) ->
            state.customProfileName = name
            state.customProfileRules = rules
            state.customProfileStatus = "Minimum profile imported · $name"
        }.onFailure { state.customProfileStatus = it.message ?: "Minimum profile import failed" }
        result.map { "Minimum profile imported" }
    }
    val exportMinimumProfileFile: suspend (File) -> Result<String> = { destination ->
        val result = withContext(Dispatchers.IO) {
            runCatching {
                val rules = state.customProfileRules.lineSequence().map { it.trim() }.filter { it.isNotBlank() }.take(ANALYSIS_CUSTOM_PROFILE_MAX_RULES + 1).toList()
                if (rules.isEmpty() || rules.size > ANALYSIS_CUSTOM_PROFILE_MAX_RULES || rules.any { it.length > 512 }) error("Minimum profile must contain 1..$ANALYSIS_CUSTOM_PROFILE_MAX_RULES bounded rules")
                val name = state.customProfileName.trim()
                if (name.isBlank() || name.length > 128) error("Minimum profile name is required and must be at most 128 characters")
                val target = validatedSharedStorageDestination(destination.parentFile ?: error("Destination folder is unavailable"), destination.name, setOf("json"))
                val bytes = JSONObject().put("schema", "VulkanScopeMinimumProfile1").put("name", name).put("rules", JSONArray(rules)).toString(2).toByteArray(Charsets.UTF_8)
                writeSharedStorageBytes(target, bytes, 256 * 1024)
                target
            }
        }
        result.onSuccess { state.customProfileStatus = "Minimum profile exported · ${it.absolutePath}" }
            .onFailure { state.customProfileStatus = it.message ?: "Minimum profile export failed" }
        result.map { "Saved ${it.name}" }
    }
    val exportRawTechnicalReportFile: suspend (File) -> Result<String> = { destination ->
        val result = withContext(Dispatchers.IO) {
            runCatching {
                val target = validatedSharedStorageDestination(destination.parentFile ?: error("Destination folder is unavailable"), destination.name, setOf("json"))
                val bytes = technicalReportJson(context, report, display, mode).toString(2).toByteArray(Charsets.UTF_8)
                if (bytes.size > ANALYSIS_MAX_SNAPSHOT_BYTES) error("Structured technical report exceeds the 8 MiB local export bound")
                writeSharedStorageBytes(target, bytes, ANALYSIS_MAX_SNAPSHOT_BYTES)
                target
            }
        }
        result.onSuccess { state.analysisStatus = "Structured technicalReport exported · ${it.absolutePath}" }
            .onFailure { state.analysisStatus = it.message ?: "Structured technicalReport export failed" }
        result.map { "Saved ${it.name}" }
    }
    return AnalysisWorkspaceModel(
        state = state,
        diffRows = diffRows,
        globalResults = globalResults,
        diagnostics = diagnostics,
        timingsMs = queryTimingMs,
        requirementReference = requirementReference,
        requirementEvaluations = requirementEvaluations,
        profileResults = profileResults,
        customMinimumEvaluations = customMinimumEvaluations,
        graphRootToken = graphRootToken,
        graphRootRef = graphRootRef,
        graphEntries = graphEntries,
        visualGraphNodes = visualGraphNodes,
        presentationPaths = presentationPaths,
        rawLeaves = rawLeaves,
        databaseDiff = databaseDiff,
        systemHistory = systemHistory,
        turnipHistory = turnipHistory,
        driverDiffRows = driverDiffRows,
        watchedEvidence = watchedEvidence,
        visibleWatched = visibleWatched,
        canAddWatch = canAddWatch,
        capabilityScore = capabilityScore,
        lastSharedReportId = lastSharedReportId,
        sharedReportUrl = sharedReportUrl,
        selfTestsAvailable = activity != null && device != null,
        canSwitchSystem = collectionStatus != CollectionStatus.COLLECTING && mode != DriverMode.SYSTEM,
        canSwitchTurnip = collectionStatus != CollectionStatus.COLLECTING && mode != DriverMode.TURNIP && turnipSupport == TurnipSupport.SUPPORTED,
        importSnapshot = { onStorageAction(AnalysisStorageAction.IMPORT_SNAPSHOT) },
        exportSnapshot = { onStorageAction(AnalysisStorageAction.EXPORT_SNAPSHOT) },
        importMinimumProfile = { onStorageAction(AnalysisStorageAction.IMPORT_MINIMUM_PROFILE) },
        exportMinimumProfile = { onStorageAction(AnalysisStorageAction.EXPORT_MINIMUM_PROFILE) },
        importSnapshotFile = importSnapshotFile,
        exportSnapshotFile = exportSnapshotFile,
        importMinimumProfileFile = importMinimumProfileFile,
        exportMinimumProfileFile = exportMinimumProfileFile,
        exportRawTechnicalReportFile = exportRawTechnicalReportFile,
        saveMinimumProfile = {
            val name = state.customProfileName.trim().take(128)
            val rules = state.customProfileRules.lineSequence().map { it.trim() }.filter { it.isNotBlank() }.take(ANALYSIS_CUSTOM_PROFILE_MAX_RULES + 1).toList()
            if (name.isBlank() || rules.isEmpty() || rules.size > ANALYSIS_CUSTOM_PROFILE_MAX_RULES || state.savedProfiles.size >= ANALYSIS_CUSTOM_PROFILE_MAX_PROFILES && name !in state.savedProfiles) {
                state.customProfileStatus = "Minimum profile violates local bounds"
            } else {
                state.savedProfiles = state.savedProfiles + (name to rules.joinToString("\n"))
                persistProfiles(state.savedProfiles)
                state.customProfileStatus = "Saved local minimum profile · $name"
            }
        },
        loadMinimumProfile = { name -> state.savedProfiles[name]?.let { state.customProfileName = name; state.customProfileRules = it; state.customProfileStatus = "Loaded local minimum profile · $name" } },
        deleteMinimumProfile = { name -> state.savedProfiles = state.savedProfiles - name; persistProfiles(state.savedProfiles); state.customProfileStatus = "Deleted local minimum profile · $name" },
        exportRawTechnicalReport = { onStorageAction(AnalysisStorageAction.EXPORT_TECHNICAL_REPORT) },
        fetchDatabaseReport = { requestedId ->
            val id = requestedId.trim().lowercase()
            if (!hasValidatedInternet(context)) {
                state.databaseRemoteLeaves = emptyList()
                state.databaseStatus = "Database fetch unavailable · no validated internet connection"
            } else if (!state.databaseLoading && id.matches(Regex("[a-f0-9]{64}"))) {
                state.databaseReportId = id
                state.databaseLoading = true
                scope.launch {
                    val result = runCatching { fetchDatabaseTechnicalReport(context, id) }
                    result.onSuccess { technical -> state.databaseRemoteLeaves = flattenJson(technical); state.databaseStatus = "Database technicalReport loaded · ${id.take(12)}…" }
                        .onFailure { state.databaseRemoteLeaves = emptyList(); state.databaseStatus = it.message ?: "Database report fetch failed" }
                    state.databaseLoading = false
                }
            }
        },
        fetchDatabaseReportList = { reset ->
            if (!hasValidatedInternet(context)) {
                state.databaseListStatus = "Database list unavailable · no validated internet connection"
            } else if (!state.databaseListLoading) {
                val cursor = if (reset) null else state.databaseListCursor
                if (!reset && cursor == null && state.databaseReports.isNotEmpty()) {
                    state.databaseListStatus = "No more reports are available in the current bounded list"
                } else {
                    state.databaseListLoading = true
                    scope.launch {
                        val result = runCatching { fetchDatabaseReportPage(context, cursor) }
                        result.onSuccess { page ->
                            state.databaseReports = if (reset) page.reports else (state.databaseReports + page.reports).distinctBy { it.id }.take(200)
                            state.databaseListCursor = page.nextCursor
                            state.databaseListStatus = if (page.reports.isEmpty()) "No public reports returned" else "${state.databaseReports.size} public reports loaded"
                        }.onFailure { state.databaseListStatus = it.message ?: "Database list fetch failed" }
                        state.databaseListLoading = false
                    }
                }
            }
        },
        useHistoryAsBaseline = { record -> state.baseline = JSONObject(record.snapshot.toString()); state.analysisStatus = "Baseline from local history · ${record.driverMode}"; state.tab = 0 },
        deleteHistory = { record -> scope.launch { withContext(Dispatchers.IO) { deleteAnalysisHistoryRecord(context, record.id) }; state.history = withContext(Dispatchers.IO) { loadAnalysisHistoryRecords(context) } } },
        deleteAllHistory = { scope.launch {
            val ids = state.history.map { it.id }
            withContext(Dispatchers.IO) { ids.forEach { deleteAnalysisHistoryRecord(context, it) } }
            state.history = withContext(Dispatchers.IO) { loadAnalysisHistoryRecords(context) }
        } },
        addWatched = addWatched,
        persistWatched = { value -> state.watched = value; prefs.edit().putStringSet("watched", value).apply() },
        switchToSystem = { if (collectionStatus != CollectionStatus.COLLECTING) onDriverModeChanged(DriverMode.SYSTEM) },
        switchToTurnip = { if (collectionStatus != CollectionStatus.COLLECTING && turnipSupport == TurnipSupport.SUPPORTED) onDriverModeChanged(DriverMode.TURNIP) },
        startDriverAbWorkflow = {
            if (device == null || collectionStatus == CollectionStatus.COLLECTING) {
                state.abWorkflowStatus = "Wait for a completed current-device collection before starting guided A/B"
            } else if (turnipSupport != TurnipSupport.SUPPORTED) {
                state.abWorkflowStage = "blocked"
                state.abWorkflowStatus = "A validated compatible Turnip driver is required for guided A/B"
                prefs.edit().putString("ab_workflow_stage", state.abWorkflowStage).apply()
            } else {
                scope.launch {
                    withContext(Dispatchers.IO) { saveAnalysisHistoryRecord(context, applicationVersion, mode.label, device.name, current) }
                    state.history = withContext(Dispatchers.IO) { loadAnalysisHistoryRecords(context) }
                    if (mode == DriverMode.SYSTEM) {
                        state.abWorkflowStage = "collect-turnip"
                        state.abWorkflowStatus = "System session retained; collecting Turnip next"
                        prefs.edit().putString("ab_workflow_stage", state.abWorkflowStage).apply()
                        onDriverModeChanged(DriverMode.TURNIP)
                    } else {
                        state.abWorkflowStage = "collect-system"
                        state.abWorkflowStatus = "Turnip session retained; collecting System first, then Turnip"
                        prefs.edit().putString("ab_workflow_stage", state.abWorkflowStage).apply()
                        onDriverModeChanged(DriverMode.SYSTEM)
                    }
                }
            }
        },
        cancelDriverAbWorkflow = {
            state.abWorkflowStage = "idle"
            state.abWorkflowStatus = "Guided A/B cancelled; existing local history is unchanged"
            prefs.edit().putString("ab_workflow_stage", "idle").apply()
        },
        shareLink = { runCatching { context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, sharedReportUrl), "Share VulkanScope link")) } },
        copyLink = { (context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager)?.setPrimaryClip(android.content.ClipData.newPlainText("VulkanScope link", sharedReportUrl)) },
        runSelfTests = {
            val host = activity
            val target = device
            if (host != null && target != null && !state.testRunning) {
                state.testRunning = true
                scope.launch {
                    state.testResult = withContext(Dispatchers.IO) { runCatching { JSONObject(host.runVulkanSelfTests(target.vendorIdRaw, target.deviceIdRaw)) }.getOrElse { JSONObject().put("status", "unavailable").put("reason", it.message ?: "Self-test failed") } }
                    state.testRunning = false
                }
            }
        }
    )
}
