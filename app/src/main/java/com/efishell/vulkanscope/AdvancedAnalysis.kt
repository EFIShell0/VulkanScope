package com.efishell.vulkanscope

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.math.BigDecimal
import java.security.MessageDigest
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

internal const val ANALYSIS_HISTORY_MAX_ITEMS = 8
internal const val ANALYSIS_HISTORY_MAX_UNCOMPRESSED_BYTES = 8 * 1024 * 1024
internal const val ANALYSIS_HISTORY_MAX_COMPRESSED_BYTES = 4 * 1024 * 1024
internal const val ANALYSIS_DATABASE_COMPARE_MAX_BYTES = 2 * 1024 * 1024
internal const val ANALYSIS_RAW_JSON_VISIBLE_LIMIT = 256
internal const val ANALYSIS_GLOBAL_SEARCH_VISIBLE_LIMIT = 160
internal const val ANALYSIS_CUSTOM_PROFILE_MAX_RULES = 64
internal const val ANALYSIS_CUSTOM_PROFILE_MAX_PROFILES = 32

internal data class EvidenceProvenance(
    val evidenceClass: String,
    val source: String,
    val queryPath: String,
    val queryGroup: String,
    val registryRelation: String,
    val interpretation: String
)

internal data class RequirementEvaluation(
    val token: String,
    val state: String,
    val evidence: String
)

internal data class CustomMinimumEvaluation(
    val rule: String,
    val state: String,
    val evidence: String
)

internal data class QueryDiagnosticRow(
    val key: String,
    val state: String,
    val value: String,
    val timing: String
)

internal data class RawJsonLeaf(
    val path: String,
    val value: String,
    val type: String
)

internal data class GenericAnalysisDiff(
    val key: String,
    val baseline: String?,
    val current: String?,
    val state: String
)

internal data class AnalysisHistoryRecord(
    val id: String,
    val timestampMs: Long,
    val applicationVersion: String,
    val driverMode: String,
    val deviceName: String,
    val snapshot: JSONObject
)

internal data class PresentationEvidencePath(
    val title: String,
    val state: String,
    val surfaceEvidence: String,
    val displayEvidence: String,
    val note: String
)

internal fun evidenceTokenForReference(key: String, value: String): String {
    val combined = "$key $value"
    Regex("VK_[A-Z0-9_]+", RegexOption.IGNORE_CASE).find(combined)?.value?.let { return it.uppercase(java.util.Locale.ROOT) }
    Regex("Vk[A-Za-z0-9_]+|vk[A-Za-z0-9_]+", RegexOption.IGNORE_CASE).find(combined)?.value?.let { return it }
    return key.substringAfterLast('/').substringAfterLast('·').trim().take(256)
}

internal fun evidenceProvenance(key: String, value: String): EvidenceProvenance {
    val normalized = key.lowercase()
    return when {
        normalized.startsWith("feature/") -> EvidenceProvenance(
            "Runtime feature evidence",
            key.substringAfter("feature/"),
            "vkGetPhysicalDeviceFeatures2 / validated feature chain",
            "Feature query group selected from the locked runtime catalog",
            "Registry feature/member naming is reference metadata only",
            "The displayed boolean comes from collected runtime feature evidence. Missing evidence is not converted to Unsupported."
        )
        normalized.startsWith("property/") -> EvidenceProvenance(
            "Runtime property evidence",
            key.substringAfter("property/").substringBeforeLast('/'),
            "vkGetPhysicalDeviceProperties2 / validated property chain",
            key.substringAfter("property/").substringBefore('/'),
            "Registry property/member naming is reference metadata only",
            "The value is retained from the selected driver's collected property evidence."
        )
        normalized.startsWith("limit/") -> EvidenceProvenance(
            "Runtime limit evidence",
            key.substringAfter("limit/"),
            "VkPhysicalDeviceProperties::limits or promoted property evidence",
            "Core/validated property collection",
            "Canonical Vulkan limit name",
            "The value is reported from the selected physical device; no minimum or quality judgment is inferred."
        )
        normalized.startsWith("extension/device/") -> EvidenceProvenance(
            "Runtime device-extension enumeration",
            key.substringAfter("extension/device/").substringBefore('/'),
            "vkEnumerateDeviceExtensionProperties",
            "Device extension enumeration",
            "Checked-in extension registry metadata may describe dependencies and promotion separately",
            "Presence proves that the selected device enumerated the extension. Absence is meaningful only when enumeration completed authoritatively."
        )
        normalized.startsWith("extension/instance/") -> EvidenceProvenance(
            "Runtime instance-extension enumeration",
            key.substringAfter("extension/instance/").substringBefore('/'),
            "vkEnumerateInstanceExtensionProperties",
            "Instance extension enumeration",
            "Checked-in extension registry metadata may describe dependencies and promotion separately",
            "Presence proves that the active loader/instance enumeration returned the extension."
        )
        normalized.startsWith("format/") -> EvidenceProvenance(
            "Runtime format evidence",
            key.substringAfter("format/").substringBefore('/'),
            "vkGetPhysicalDeviceFormatProperties2 / VkFormatProperties3 where available",
            "Format Properties 2/3",
            "Format and flag names come from the locked registry",
            "Raw format feature masks are retained. Canonical names do not replace the original numeric evidence."
        )
        normalized.startsWith("surface/") -> EvidenceProvenance(
            "Runtime Surface/WSI evidence",
            key.substringAfter("surface/"),
            "Android Surface -> VkSurfaceKHR -> KHR surface queries",
            "Surface query path",
            "Vulkan Surface color-space names are canonical registry names",
            "Surface evidence is not substituted for Android physical-display HDR or gamut evidence."
        )
        normalized.startsWith("display/") -> EvidenceProvenance(
            "Android display evidence",
            key.substringAfter("display/"),
            "Android Display / HdrCapabilities APIs",
            "Android display path",
            "Separate from Vulkan Surface registry metadata",
            "Display evidence describes Android's exposed display stack and is never inferred from Vulkan color spaces."
        )
        normalized.startsWith("queue/") -> EvidenceProvenance(
            "Runtime queue-family evidence",
            key.substringAfter("queue/"),
            "vkGetPhysicalDeviceQueueFamilyProperties2 and validated queue/video paths",
            "Queue Family Properties 2",
            "Canonical queue/video operation names are registry-derived",
            "A queried zero remains zero evidence. Unavailable or not-applicable query state is kept distinct."
        )
        normalized.startsWith("profile/") -> EvidenceProvenance(
            "Derived profile evaluation",
            key.substringAfter("profile/"),
            "Local Vulkan Profile evaluator over collected runtime evidence",
            "Profile evaluator",
            "Profile definitions are checked-in reference requirements",
            "PASS/FAIL/UNKNOWN is derived from explicit collected evidence; missing evidence remains UNKNOWN."
        )
        normalized.startsWith("query/") || normalized.startsWith("safety/") -> EvidenceProvenance(
            "Collector diagnostic evidence",
            key,
            "VulkanScope collection/query safety state",
            "Collector diagnostic path",
            "No registry support claim",
            "This describes query availability, completeness, or a local safety rejection. It is not a hardware capability inference."
        )
        normalized.startsWith("registry/") -> EvidenceProvenance(
            "Registry/reference metadata",
            key.substringAfter("registry/"),
            "Checked-in Vulkan registry/header provenance",
            "Offline registry metadata",
            value,
            "Registry/reference presence never proves selected-device runtime support."
        )
        else -> EvidenceProvenance(
            "Collected or derived evidence",
            key,
            "VulkanScope report/analysis model",
            "General evidence path",
            "Reference metadata remains separate from runtime evidence",
            "The value is displayed without converting missing or unknown information into Unsupported."
        )
    }
}

internal fun queryDiagnostics(entries: Map<String, String>, timingsMs: Map<String, Long> = emptyMap()): List<QueryDiagnosticRow> {
    val rows = entries.filterKeys { key ->
        key.startsWith("query/") || key.startsWith("safety/") || key.startsWith("physicalDeviceEnumeration/") || key.startsWith("instanceGroup/") || key.contains("Vulkan Query Status", true)
    }.map { (key, value) ->
        val state = when {
            value.equals("available", true) || value.equals("true", true) || value.startsWith("VK_SUCCESS", true) -> "AVAILABLE"
            value.contains("incomplete", true) -> "INCOMPLETE"
            value.contains("not_applicable", true) || value.contains("not applicable", true) -> "NOT APPLICABLE"
            value.contains("unavailable", true) -> "UNAVAILABLE"
            value.equals("false", true) && key.startsWith("safety/") -> "NO REJECTION"
            value.equals("true", true) && key.startsWith("safety/") -> "SAFETY REJECTED"
            value.contains("unknown", true) || value.isBlank() -> "UNKNOWN"
            else -> "EVIDENCE"
        }
        QueryDiagnosticRow(key, state, value, "Not recorded by the current per-query report contract")
    }
    val timelineRows = timingsMs.entries.mapIndexed { index, (group, elapsed) ->
        QueryDiagnosticRow("timeline/${index.toString().padStart(3, '0')}/$group", "COMPLETED", "Dedicated probe completed for $group", "$elapsed ms")
    }
    return (timelineRows + rows).sortedWith(compareBy<QueryDiagnosticRow> { it.key.substringBefore('/') }.thenBy { it.key })
}

internal fun requirementTokens(expression: String): List<String> = Regex("VK_VERSION_[0-9]+_[0-9]+|VK_[A-Za-z0-9_]+")
    .findAll(expression)
    .map { it.value }
    .distinct()
    .take(128)
    .toList()

private fun parseApiPair(value: String): Pair<Int, Int>? {
    val match = Regex("(?:Vulkan\\s*)?([0-9]+)\\.([0-9]+)", RegexOption.IGNORE_CASE).find(value) ?: return null
    return match.groupValues[1].toIntOrNull()?.let { major -> match.groupValues[2].toIntOrNull()?.let { minor -> major to minor } }
}

private fun compareApiPair(actual: Pair<Int, Int>, required: Pair<Int, Int>): Boolean = actual.first > required.first || actual.first == required.first && actual.second >= required.second

internal fun evaluateRequirementToken(token: String, entries: Map<String, String>): RequirementEvaluation {
    if (token.startsWith("VK_VERSION_")) {
        val parts = token.removePrefix("VK_VERSION_").split('_')
        val required = if (parts.size >= 2) (parts[0].toIntOrNull()?.let { major -> parts[1].toIntOrNull()?.let { minor -> major to minor } }) else null
        val actualText = entries["identity/deviceApiVersion"] ?: entries["identity/instanceApiVersion"] ?: "Unknown"
        val actual = parseApiPair(actualText)
        return when {
            required == null || actual == null -> RequirementEvaluation(token, "UNKNOWN", "Runtime API evidence is unavailable or unparsable: $actualText")
            compareApiPair(actual, required) -> RequirementEvaluation(token, "SATISFIED", "Runtime device API is $actualText")
            else -> RequirementEvaluation(token, "NOT SATISFIED", "Runtime device API is $actualText")
        }
    }
    val deviceKey = entries.keys.firstOrNull { it.startsWith("extension/device/$token") }
    val instanceKey = entries.keys.firstOrNull { it.startsWith("extension/instance/$token") }
    if (deviceKey != null || instanceKey != null) {
        val key = deviceKey ?: instanceKey.orEmpty()
        return RequirementEvaluation(token, "SATISFIED", "$key = ${entries[key]}")
    }
    val deviceComplete = entries["query/deviceExtensionStatus"].equals("available", true)
    val instanceComplete = entries["query/instanceExtensionStatus"].equals("available", true)
    return if (deviceComplete && instanceComplete) RequirementEvaluation(token, "NOT SATISFIED", "Not enumerated by completed instance/device extension evidence")
    else RequirementEvaluation(token, "UNKNOWN", "Extension enumeration is not authoritative enough to prove absence")
}

private fun customMinimumMatches(entries: Map<String, String>, prefix: String, name: String): List<Map.Entry<String, String>> {
    val normalized = name.trim()
    if (normalized.isBlank()) return emptyList()
    entries[prefix + normalized]?.let { value -> return entries.entries.filter { it.key == prefix + normalized } }
    entries[normalized]?.let { value -> return entries.entries.filter { it.key == normalized } }
    return entries.entries.filter { entry ->
        if (!entry.key.startsWith(prefix)) return@filter false
        val evidenceName = entry.key.substringAfter(prefix)
        evidenceName.equals(normalized, true) || evidenceName.endsWith(".$normalized", true) || evidenceName.endsWith("::$normalized", true)
    }.take(3)
}

internal fun evaluateCustomMinimumRule(rule: String, entries: Map<String, String>): CustomMinimumEvaluation {
    val trimmed = rule.trim()
    if (trimmed.isBlank()) return CustomMinimumEvaluation(rule, "UNKNOWN", "Empty rule")
    val apiMatch = Regex("""^api\s*>=\s*([0-9]+\.[0-9]+)$""", RegexOption.IGNORE_CASE).matchEntire(trimmed)
    if (apiMatch != null) {
        val required = parseApiPair(apiMatch.groupValues[1])
        val actualText = entries["identity/deviceApiVersion"] ?: "Unknown"
        val actual = parseApiPair(actualText)
        return when {
            required == null || actual == null -> CustomMinimumEvaluation(trimmed, "UNKNOWN", "Device API evidence is $actualText")
            compareApiPair(actual, required) -> CustomMinimumEvaluation(trimmed, "PASS", "Device API $actualText satisfies ${apiMatch.groupValues[1]}")
            else -> CustomMinimumEvaluation(trimmed, "FAIL", "Device API $actualText is below ${apiMatch.groupValues[1]}")
        }
    }
    if (trimmed.startsWith("extension:", true)) {
        val token = trimmed.substringAfter(':').trim()
        val result = evaluateRequirementToken(token, entries)
        return CustomMinimumEvaluation(trimmed, when (result.state) { "SATISFIED" -> "PASS"; "NOT SATISFIED" -> "FAIL"; else -> "UNKNOWN" }, result.evidence)
    }
    if (trimmed.startsWith("feature:", true)) {
        val body = trimmed.substringAfter(':').trim()
        val hasExpected = '=' in body
        val expected = body.substringAfter('=', "true").trim().lowercase()
        val name = body.substringBefore('=').trim()
        val trueTokens = setOf("true", "supported", "yes", "1")
        val falseTokens = setOf("false", "unsupported", "no", "0")
        if (hasExpected && expected !in trueTokens && expected !in falseTokens) return CustomMinimumEvaluation(trimmed, "UNKNOWN", "Feature expectation must be true/supported/yes/1 or false/unsupported/no/0")
        val matches = customMinimumMatches(entries, "feature/", name)
        if (matches.isEmpty()) return CustomMinimumEvaluation(trimmed, "UNKNOWN", "Feature evidence was not collected under that name")
        if (matches.size != 1) return CustomMinimumEvaluation(trimmed, "UNKNOWN", "Feature name is ambiguous; use an exact evidence key: ${matches.joinToString { it.key }}")
        val hit = matches.single()
        val actual = hit.value.trim().lowercase()
        val actualSupported = when (actual) {
            "supported", "true" -> true
            "unsupported", "false" -> false
            else -> return CustomMinimumEvaluation(trimmed, "UNKNOWN", "Feature evidence is not a boolean support state: ${hit.key} = ${hit.value}")
        }
        val expectedSupported = expected in trueTokens
        return CustomMinimumEvaluation(trimmed, if (actualSupported == expectedSupported) "PASS" else "FAIL", "${hit.key} = ${hit.value}")
    }
    if (trimmed.startsWith("limit:", true)) {
        val body = trimmed.substringAfter(':').trim()
        val match = Regex("""^(.+?)(>=|<=|==|>|<)(-?[0-9]+(?:\.[0-9]+)?)$""").matchEntire(body)
            ?: return CustomMinimumEvaluation(trimmed, "UNKNOWN", "Limit rule must use name>=number, name<=number, name==number, name>number or name<number")
        val name = match.groupValues[1].trim()
        val operator = match.groupValues[2]
        val expected = runCatching { BigDecimal(match.groupValues[3]) }.getOrNull() ?: return CustomMinimumEvaluation(trimmed, "UNKNOWN", "Invalid numeric minimum")
        val matches = customMinimumMatches(entries, "limit/", name)
        if (matches.isEmpty()) return CustomMinimumEvaluation(trimmed, "UNKNOWN", "Limit evidence was not collected under that name")
        if (matches.size != 1) return CustomMinimumEvaluation(trimmed, "UNKNOWN", "Limit name is ambiguous; use an exact evidence key: ${matches.joinToString { it.key }}")
        val hit = matches.single()
        val numeric = Regex("""-?[0-9]+(?:\.[0-9]+)?""").find(hit.value.replace(",", ""))?.value
            ?: return CustomMinimumEvaluation(trimmed, "UNKNOWN", "Limit value is not safely numeric: ${hit.value}")
        val actual = runCatching { BigDecimal(numeric) }.getOrNull() ?: return CustomMinimumEvaluation(trimmed, "UNKNOWN", "Limit value is not safely numeric: ${hit.value}")
        val comparison = actual.compareTo(expected)
        val pass = when (operator) { ">=" -> comparison >= 0; "<=" -> comparison <= 0; "==" -> comparison == 0; ">" -> comparison > 0; else -> comparison < 0 }
        return CustomMinimumEvaluation(trimmed, if (pass) "PASS" else "FAIL", "${hit.key} = ${hit.value}")
    }
    if (trimmed.startsWith("evidence:", true)) {
        val body = trimmed.substringAfter(':')
        val key = body.substringBefore('=').trim()
        val expected = body.substringAfter('=', "").trim()
        val actual = entries[key] ?: return CustomMinimumEvaluation(trimmed, "UNKNOWN", "Exact evidence key is not present")
        return CustomMinimumEvaluation(trimmed, if (actual.equals(expected, true)) "PASS" else "FAIL", "$key = $actual")
    }
    return CustomMinimumEvaluation(trimmed, "UNKNOWN", "Unsupported rule syntax")
}

internal fun flattenJson(value: Any?, prefix: String = "technical", maxLeaves: Int = 65536): List<RawJsonLeaf> {
    val out = ArrayList<RawJsonLeaf>()
    fun walk(node: Any?, path: String) {
        if (out.size >= maxLeaves) return
        when (node) {
            null, JSONObject.NULL -> out += RawJsonLeaf(path, "null", "null")
            is JSONObject -> {
                val keys = node.keys().asSequence().toList().sorted()
                if (keys.isEmpty()) out += RawJsonLeaf(path, "{}", "object")
                keys.forEach { key -> walk(node.opt(key), if (path.isBlank()) key else "$path.$key") }
            }
            is JSONArray -> {
                if (node.length() == 0) out += RawJsonLeaf(path, "[]", "array")
                for (index in 0 until node.length()) walk(node.opt(index), "$path[$index]")
            }
            is Boolean -> out += RawJsonLeaf(path, node.toString(), "boolean")
            is Number -> out += RawJsonLeaf(path, node.toString(), "number")
            else -> out += RawJsonLeaf(path, node.toString(), "string")
        }
    }
    walk(value, prefix)
    return out
}

internal fun genericDiff(baseline: Map<String, String>, current: Map<String, String>): List<GenericAnalysisDiff> = (baseline.keys + current.keys).toSortedSet().map { key ->
    val before = baseline[key]
    val after = current[key]
    GenericAnalysisDiff(key, before, after, when { before == null -> "Added"; after == null -> "Removed"; before == after -> "Unchanged"; else -> "Changed" })
}

internal fun rawLeavesToMap(leaves: List<RawJsonLeaf>): Map<String, String> = linkedMapOf<String, String>().apply { leaves.forEach { put(it.path, it.value) } }

internal fun presentationEvidencePaths(entries: Map<String, String>): List<PresentationEvidencePath> {
    val surfaceFormats = entries.filterKeys { it.startsWith("surface/format/") }
    val hdr10Surface = surfaceFormats.keys.any { it.contains("VK_COLOR_SPACE_HDR10_ST2084_EXT", true) }
    val hlgSurface = surfaceFormats.keys.any { it.contains("VK_COLOR_SPACE_HDR10_HLG_EXT", true) || it.contains("VK_COLOR_SPACE_BT2020_HLG_EXT", true) }
    val p3Surface = surfaceFormats.keys.any { it.contains("DISPLAY_P3", true) }
    val hdr10Display = entries.keys.any { it.startsWith("display/hdr/") && it.contains("HDR10", true) && !it.contains("HDR10+", true) }
    val hlgDisplay = entries.keys.any { it.startsWith("display/hdr/") && it.contains("HLG", true) }
    val wideGamutText = entries["display/wideGamut"]
    val wideGamut = when (wideGamutText?.lowercase()) { "true" -> true; "false" -> false; else -> null }
    fun state(a: Boolean, b: Boolean?): String = when { a && b == true -> "COMPATIBLE EVIDENCE"; a && b == false -> "SURFACE ONLY"; !a && b == true -> "DISPLAY ONLY"; else -> "NO MATCHING EVIDENCE" }
    return listOf(
        PresentationEvidencePath("HDR10 / ST2084", state(hdr10Surface, hdr10Display), if (hdr10Surface) "VK_COLOR_SPACE_HDR10_ST2084_EXT enumerated" else "No HDR10 ST2084 Surface pair retained", if (hdr10Display) "Android display reports HDR10" else "Android HDR10 evidence not present", "Compatible evidence does not prove end-to-end swapchain presentation succeeds."),
        PresentationEvidencePath("HLG", state(hlgSurface, hlgDisplay), if (hlgSurface) "HLG-related Vulkan Surface color space enumerated" else "No HLG-related Surface pair retained", if (hlgDisplay) "Android display reports HLG" else "Android HLG evidence not present", "Vulkan Surface and Android display evidence remain separate sources."),
        PresentationEvidencePath("Display P3 / wide gamut", state(p3Surface, wideGamut), if (p3Surface) "Display-P3 Vulkan Surface color space enumerated" else "No Display-P3 Surface pair retained", when (wideGamut) { true -> "Android display reports wide color gamut"; false -> "Android display reports wide color gamut unsupported"; null -> "Android wide-gamut evidence unavailable" }, "No gamut percentage or color-volume capability is inferred.")
    )
}

private fun snapshotDigest(snapshot: JSONObject): String = MessageDigest.getInstance("SHA-256").digest(snapshot.toString().toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it.toInt() and 0xff) }.take(20)

private fun historyDirectory(context: Context): File = File(context.filesDir, "analysis_history")

private fun gzip(bytes: ByteArray): ByteArray {
    val out = ByteArrayOutputStream()
    GZIPOutputStream(out).use { it.write(bytes) }
    return out.toByteArray()
}

private fun gunzipBounded(bytes: ByteArray): ByteArray {
    val out = ByteArrayOutputStream(minOf(64 * 1024, ANALYSIS_HISTORY_MAX_UNCOMPRESSED_BYTES))
    val buffer = ByteArray(8192)
    var total = 0
    GZIPInputStream(ByteArrayInputStream(bytes)).use { input ->
        while (true) {
            val read = input.read(buffer)
            if (read < 0) break
            if (read == 0) continue
            total += read
            if (total > ANALYSIS_HISTORY_MAX_UNCOMPRESSED_BYTES) error("Stored analysis session exceeds the local history bound")
            out.write(buffer, 0, read)
        }
    }
    return out.toByteArray()
}

internal fun saveAnalysisHistoryRecord(context: Context, applicationVersion: String, driverMode: String, deviceName: String, snapshot: JSONObject): AnalysisHistoryRecord? = runCatching {
    val snapshotBytes = snapshot.toString().toByteArray(Charsets.UTF_8)
    if (snapshotBytes.size > ANALYSIS_HISTORY_MAX_UNCOMPRESSED_BYTES) return@runCatching null
    val id = snapshotDigest(snapshot)
    val directory = historyDirectory(context)
    if (!directory.exists() && !directory.mkdirs()) return@runCatching null
    if (!directory.isDirectory) return@runCatching null
    val existing = directory.listFiles()?.firstOrNull { it.name.contains("-$id.json.gz") }
    if (existing != null) return@runCatching loadAnalysisHistoryRecords(context).firstOrNull { it.id == id }
    val record = JSONObject()
        .put("schema", "VulkanScopeAnalysisHistory1")
        .put("id", id)
        .put("timestampMs", System.currentTimeMillis())
        .put("applicationVersion", applicationVersion.take(128))
        .put("driverMode", driverMode.take(128))
        .put("deviceName", deviceName.take(256))
        .put("snapshot", snapshot)
    val compressed = gzip(record.toString().toByteArray(Charsets.UTF_8))
    if (compressed.size > ANALYSIS_HISTORY_MAX_COMPRESSED_BYTES) return@runCatching null
    val target = File(directory, "${record.getLong("timestampMs")}-$id.json.gz")
    val temp = File(directory, ".${target.name}.tmp")
    if (temp.exists() && !temp.delete()) return@runCatching null
    temp.outputStream().use { it.write(compressed) }
    if (!temp.renameTo(target)) {
        temp.delete()
        return@runCatching null
    }
    directory.listFiles()?.filter { it.name.endsWith(".json.gz") }?.sortedByDescending { it.lastModified() }?.drop(ANALYSIS_HISTORY_MAX_ITEMS)?.forEach { runCatching { it.delete() } }
    AnalysisHistoryRecord(id, record.getLong("timestampMs"), applicationVersion, driverMode, deviceName, JSONObject(snapshot.toString()))
}.getOrNull()

internal fun loadAnalysisHistoryRecords(context: Context): List<AnalysisHistoryRecord> = runCatching {
    val directory = historyDirectory(context)
    if (!directory.isDirectory) return@runCatching emptyList()
    directory.listFiles()?.filter { it.name.endsWith(".json.gz") && it.isFile && it.length() in 1..ANALYSIS_HISTORY_MAX_COMPRESSED_BYTES.toLong() }
        ?.sortedByDescending { it.lastModified() }
        ?.take(ANALYSIS_HISTORY_MAX_ITEMS)
        ?.mapNotNull { file ->
            runCatching {
                val root = JSONObject(gunzipBounded(file.readBytes()).toString(Charsets.UTF_8))
                if (root.optString("schema") != "VulkanScopeAnalysisHistory1") error("Invalid analysis history schema")
                val snapshot = root.optJSONObject("snapshot") ?: error("Analysis history snapshot missing")
                if (snapshot.optString("schema") != "VulkanScopeAnalysisSnapshot1") error("Invalid analysis snapshot schema")
                AnalysisHistoryRecord(
                    root.getString("id"),
                    root.getLong("timestampMs"),
                    root.optString("applicationVersion", "Unknown"),
                    root.optString("driverMode", "Unknown"),
                    root.optString("deviceName", "Unknown"),
                    snapshot
                )
            }.getOrNull()
        }.orEmpty()
}.getOrDefault(emptyList())

internal fun deleteAnalysisHistoryRecord(context: Context, id: String): Boolean {
    if (!id.matches(Regex("[a-f0-9]{20}"))) return false
    return runCatching {
        val directory = historyDirectory(context)
        if (!directory.isDirectory) return@runCatching false
        var deleted = false
        directory.listFiles()?.filter { it.name.contains("-$id.json.gz") }?.forEach { deleted = it.delete() || deleted }
        deleted
    }.getOrDefault(false)
}

