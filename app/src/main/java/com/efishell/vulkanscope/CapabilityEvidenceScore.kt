package com.efishell.vulkanscope

import kotlin.math.roundToInt
import kotlin.math.sqrt

internal data class CapabilityEvidenceCheck(
    val title: String,
    val triggered: Boolean,
    val evidence: String,
    val applicable: Boolean = true,
    val weight: Int = 1
)

internal data class CapabilityScoreComponent(
    val title: String,
    val weight: Int,
    val fraction: Double?,
    val evidence: String,
    val methodology: String
)

internal data class CapabilityEvidenceScore(
    val score: Int?,
    val level: String,
    val checks: List<CapabilityEvidenceCheck>,
    val components: List<CapabilityScoreComponent> = emptyList(),
    val evidenceCoverage: Int = 0
)

internal fun calculateCapabilityEvidenceScore(report: VulkanReport, device: DeviceReport?): CapabilityEvidenceScore {
    if (device == null) return CapabilityEvidenceScore(null, "No device evidence", emptyList())
    val extensionStatus = device.deviceExtensionStatus.lowercase()
    val uniqueDeviceExtensions = device.extensions.filter { it.supported }.map { it.name }.distinct().size
    val apiTokens = device.apiVersion.trim().split('.')
    val apiMajor = apiTokens.getOrNull(0)?.toIntOrNull()
    val apiMinor = apiTokens.getOrNull(1)?.toIntOrNull()
    val validApi = apiMajor == 1 && apiMinor != null && apiMinor >= 0
    val apiFraction = if (validApi) (0.20 + 0.20 * apiMinor!!.coerceAtMost(4)).coerceIn(0.0, 1.0) else null
    val extensionFraction = if (extensionStatus == "available") {
        sqrt(uniqueDeviceExtensions.coerceAtLeast(0).toDouble() / 160.0).coerceIn(0.0, 1.0)
    } else null
    val featureCount = device.features.size
    val enabledFeatureCount = device.features.count { it.supported }
    val featureFraction = if (featureCount > 0) enabledFeatureCount.toDouble() / featureCount else null

    val checks = listOf(
        CapabilityEvidenceCheck("Base report completed", !report.baseReportComplete || !report.error.isNullOrBlank(), "complete=${report.baseReportComplete}; error=${report.error ?: "none"}"),
        CapabilityEvidenceCheck("Queue-family enumeration safety", device.queueQuerySafetyRejected, "Safety rejection: ${device.queueQuerySafetyRejected}"),
        CapabilityEvidenceCheck("Memory-heap enumeration safety", device.memoryHeapSafetyRejected, "Safety rejection: ${device.memoryHeapSafetyRejected}"),
        CapabilityEvidenceCheck("Memory-type enumeration safety", device.memoryTypeSafetyRejected, "Safety rejection: ${device.memoryTypeSafetyRejected}"),
        CapabilityEvidenceCheck("Surface queue enumeration safety", device.surfaceQueueQuerySafetyRejected, "Safety rejection: ${device.surfaceQueueQuerySafetyRejected}", device.surfaceQueryStatus == "available"),
        CapabilityEvidenceCheck("Surface format enumeration safety", device.surfaceFormatQuerySafetyRejected, "Safety rejection: ${device.surfaceFormatQuerySafetyRejected}", device.surfaceFormatQueryAttempted),
        CapabilityEvidenceCheck("Device extension enumeration", extensionStatus != "available", "Query status: ${device.deviceExtensionStatus}", extensionStatus !in setOf("unknown", "not_applicable", "not-applicable", "")),
        CapabilityEvidenceCheck("Surface format retry", device.surfaceFormatQueryResultSecond != 0, "VkResult: ${device.surfaceFormatQueryResultSecond}", device.surfaceFormatQuerySecondAttempted)
    )
    val assessedChecks = checks.filter { it.applicable }
    val passedChecks = assessedChecks.count { !it.triggered }
    val integrityFraction = if (assessedChecks.isNotEmpty()) passedChecks.toDouble() / assessedChecks.size else null
    val surfaceFraction = if (device.surfaceQueryStatus.lowercase() == "available") {
        if (device.surfacePresentationSupported) 1.0 else 0.0
    } else null

    val components = listOf(
        CapabilityScoreComponent("Vulkan API version", 30, apiFraction,
            "Device API: ${device.apiVersion}", "Vulkan 1.0 = 20%, 1.1 = 40%, 1.2 = 60%, 1.3 = 80%, 1.4+ = 100% of this component."),
        CapabilityScoreComponent("Device extensions", 25, extensionFraction,
            "$uniqueDeviceExtensions unique enumerated device extensions; status=${device.deviceExtensionStatus}",
            "Diminishing-return breadth curve: sqrt(extension count / 160), capped at 1.0. Raw extension count is not a speed ranking."),
        CapabilityScoreComponent("Advertised feature bits", 20, featureFraction,
            "$enabledFeatureCount true out of $featureCount enumerated feature bits",
            "Fraction of explicitly queried feature bits reported true; missing data is excluded."),
        CapabilityScoreComponent("Collection integrity", 20, integrityFraction,
            "$passedChecks / ${assessedChecks.size} applicable collection-safety checks passed",
            "The collection status, safety guards and attempted enumeration outcomes are counted separately from device capability."),
        CapabilityScoreComponent("Surface presentation", 5, surfaceFraction,
            "Query=${device.surfaceQueryStatus}; presentation=${device.surfacePresentationSupported}",
            "A presentation query is only counted when surface query evidence is available. Not applicable does not reduce the score.")
    )
    val available = components.filter { it.fraction != null }
    val evidenceCoverage = available.sumOf { it.weight }
    val score = if (evidenceCoverage < 45) null else (
        100.0 * available.sumOf { it.weight * it.fraction!! } / evidenceCoverage
    ).roundToInt().coerceIn(0, 100)
    val level = when {
        score == null -> "Insufficient device evidence"
        score >= 90 -> "Very broad local capability evidence"
        score >= 75 -> "Broad local capability evidence"
        score >= 55 -> "Moderate local capability evidence"
        else -> "Limited local capability evidence"
    }
    return CapabilityEvidenceScore(score, level, checks, components, evidenceCoverage)
}
