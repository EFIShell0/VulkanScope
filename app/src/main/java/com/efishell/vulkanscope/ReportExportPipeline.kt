
package com.efishell.vulkanscope

import android.graphics.Color
import android.os.Build
import android.view.Surface
import android.util.Base64
import android.content.Context
import java.io.File
import java.io.FileOutputStream
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject


internal fun imageFormatQueryGroupState(device: DeviceReport): Pair<String, String> {
    val raw = device.detailedProperties.lastOrNull { it.section == "Vulkan Query Status" && it.name == "Image Format Properties 2 query" }?.value.orEmpty()
    return when {
        raw == "Available" -> "available" to ""
        raw.startsWith("Unavailable: ") -> "unavailable" to raw.removePrefix("Unavailable: ").trim()
        raw.startsWith("Not applicable: ") -> "not_applicable" to raw.removePrefix("Not applicable: ").trim()
        else -> "unknown" to raw.trim()
    }
}

internal fun technicalReportJson(context: Context, report: VulkanReport, display: DisplayReport, mode: DriverMode): JSONObject = JSONObject().apply {
    put("schemaVersion", 3)
    put("loaderInstanceApiVersion", report.loaderVersion)
    put("loaderApiVersion", report.loaderVersion)
    put("instanceApiVersion", report.instanceApiVersion)
    put("driverMode", mode.label)
    put("collectionError", report.error ?: JSONObject.NULL)
    put("applicationAbi", detectInstalledAbi(context))
    put("supportedDeviceAbis", JSONArray(Build.SUPPORTED_ABIS.toList()))
    put("instanceExtensionStatus", report.instanceExtensionStatus)
    put("instanceExtensionReason", report.instanceExtensionReason)
    put("instanceLayerStatus", report.instanceLayerStatus)
    put("instanceLayerReason", report.instanceLayerReason)
    put("baseReportComplete", report.baseReportComplete)
    put("physicalDeviceEnumerationResult", report.physicalDeviceEnumerationResult ?: JSONObject.NULL)
    put("physicalDeviceEnumerationResultName", report.physicalDeviceEnumerationResult?.let(::vkResultText) ?: JSONObject.NULL)
    put("physicalDeviceEnumerationComplete", report.physicalDeviceEnumerationComplete)
    put("physicalDeviceEnumerationSafetyRejected", report.physicalDeviceEnumerationSafetyRejected)
    put("physicalDeviceEnumerationReason", report.physicalDeviceEnumerationReason)
    put("instanceGroupStatus", report.instanceGroupStatus)
    put("instanceGroupReason", report.instanceGroupReason)
    put("instanceGroupEnumerationResult", report.instanceGroupEnumerationResult ?: JSONObject.NULL)
    put("instanceGroupEnumerationComplete", report.instanceGroupEnumerationComplete)
    put("instanceGroupProperties", JSONArray().apply {
        report.instanceGroupProperties.forEach { property ->
            put(JSONObject().put("section", property.section).put("name", property.name).put("value", property.value))
        }
    })
    put("display", JSONObject().apply {
        put("resolution", display.resolution)
        put("refreshRate", display.refreshRate)
        put("wideGamut", display.wideGamut ?: JSONObject.NULL)
        put("preferredWideGamut", display.preferredWideGamut)
        put("preferredWideGamutColorSpace", display.preferredWideGamut)
        put("hdrTypes", JSONArray(display.hdrTypes))
        put("hdrCapabilityStatus", display.hdrCapabilityStatus)
        put("minLuminance", display.minLuminance)
        put("maxLuminance", display.maxLuminance)
        put("averageLuminance", display.averageLuminance)
        put("modes", JSONArray(display.modes))
    })
    put("registryCoverage", JSONObject().apply {
        put("baseline", report.registryCoverage.baseline)
        put("mode", report.registryCoverage.mode)
        put("implementedPhysicalDeviceStructCount", report.registryCoverage.implementedPhysicalDeviceStructCount)
        put("validatedRuntimeQueryGroupCount", report.registryCoverage.validatedRuntimeQueryGroupCount)
        put("runtimeRegistryTokenReferenceCount", report.registryCoverage.runtimeRegistryTokenReferenceCount)
        put("runtimeExtensionTokenCount", report.registryCoverage.runtimeRegistryTokenReferenceCount)
        put("catalogSchemaVersion", report.registryCoverage.catalogSchemaVersion)
        put("reportSchema", report.registryCoverage.reportSchema)
        put("headerBaseline", report.registryCoverage.headerBaseline)
        put("instanceDependencyCandidateCount", report.registryCoverage.instanceDependencyCandidateCount)
        put("implementedPhysicalDeviceStructs", JSONArray(report.registryCoverage.implementedPhysicalDeviceStructs))
        put("validatedRuntimeQueryGroups", JSONArray(report.registryCoverage.validatedRuntimeQueryGroups))
    })
    put("instanceExtensions", JSONArray().apply {
        report.instanceExtensions.forEach { ext -> put(JSONObject().apply { put("name", ext.name); put("scope", ext.scope); put("specVersion", ext.specVersion); put("supported", ext.supported) }) }
    })
    put("instanceLayers", JSONArray().apply {
        report.instanceLayers.forEach { layer -> put(JSONObject().apply {
            put("name", layer.name); put("description", layer.description); put("specVersion", layer.specVersion); put("implementationVersion", layer.implementationVersion)
            put("extensionStatus", layer.extensionStatus); put("extensionReason", layer.extensionReason); put("extensionsComplete", layer.extensionsComplete)
            put("extensions", JSONArray().apply { layer.extensions.forEach { ext -> put(JSONObject().apply { put("name", ext.name); put("scope", ext.scope); put("specVersion", ext.specVersion); put("supported", ext.supported) }) } })
        }) }
    })
    put("devices", JSONArray().apply {
        report.devices.forEach { d -> put(JSONObject().apply {
            put("name", d.name); put("apiVersion", d.apiVersion); put("driverVersionRaw", d.driverVersion); put("driverVersionText", d.driverVersionText)
            put("vendorId", d.vendorId); put("vendorIdRaw", d.vendorIdRaw); put("deviceId", d.deviceId); put("deviceIdRaw", d.deviceIdRaw); put("deviceType", d.deviceType)
            put("deviceExtensionStatus", d.deviceExtensionStatus); put("deviceExtensionReason", d.deviceExtensionReason)
            put("extendedQueryStatus", d.extendedQueryStatus); put("extendedQueryReason", d.extendedQueryReason)
            put("vulkan14Status", d.vulkan14Status); put("vulkan14Reason", d.vulkan14Reason)
            val imageFormatGroupState = imageFormatQueryGroupState(d)
            put("imageFormatQueryStatus", imageFormatGroupState.first); put("imageFormatQueryReason", imageFormatGroupState.second)
            put("deviceLayerStatus", d.deviceLayerStatus); put("deviceLayerReason", d.deviceLayerReason); put("deviceLayersComplete", d.deviceLayersComplete)
            put("querySafety", JSONObject().apply {
                put("queueFamilyEnumerationRejected", d.queueQuerySafetyRejected)
                put("memoryHeapCountRejected", d.memoryHeapSafetyRejected)
                put("memoryTypeCountRejected", d.memoryTypeSafetyRejected)
                put("surfaceQueueFamilyEnumerationRejected", d.surfaceQueueQuerySafetyRejected)
            })
            put("deviceLayers", JSONArray().apply { d.deviceLayers.forEach { layer -> put(JSONObject().apply {
                put("name", layer.name); put("description", layer.description); put("specVersion", layer.specVersion); put("implementationVersion", layer.implementationVersion)
                put("extensionStatus", layer.extensionStatus); put("extensionReason", layer.extensionReason); put("extensionsComplete", layer.extensionsComplete)
                put("extensions", JSONArray().apply { layer.extensions.forEach { ext -> put(JSONObject().apply { put("name", ext.name); put("scope", ext.scope); put("specVersion", ext.specVersion); put("supported", ext.supported) }) } })
            }) } })
            put("extensions", JSONArray().apply { d.extensions.forEach { ext -> put(JSONObject().apply { put("name", ext.name); put("scope", ext.scope); put("specVersion", ext.specVersion); put("supported", ext.supported) }) } })
            put("features", JSONArray().apply { d.features.forEach { feature -> put(JSONObject().apply { put("name", feature.name); put("supported", feature.supported) }) } })
            put("detailedProperties", JSONArray().apply { d.detailedProperties.forEach { prop -> put(JSONObject().apply { put("section", prop.section); put("name", prop.name); put("value", prop.value) }) } })
            put("imageFormatQueryResults", JSONArray().apply { d.imageFormatQueryResults.forEach { result -> put(JSONObject().apply { put("name", result.name); put("status", result.status); put("vkResult", result.vkResult ?: JSONObject.NULL); put("vkResultName", result.vkResult?.let(::vkResultText) ?: JSONObject.NULL); put("reason", result.reason) }) } })
            put("limits", JSONArray().apply { d.limits.forEach { value -> put(JSONObject().apply { put("name", value.first); put("value", value.second) }) } })
            put("memoryHeaps", JSONArray().apply { d.heaps.forEach { heap -> put(JSONObject().apply { put("index", heap.index); put("size", heap.size); put("sizeU64", heap.size.toULong().toString()); put("flags", heap.flags); put("flagsU64", heap.flags.toULong().toString()); put("flagsCanonical", memoryHeapFlags(heap.flags)) }) } })
            put("memoryTypes", JSONArray().apply { d.memoryTypes.forEach { type -> put(JSONObject().apply { put("index", type.index); put("heap", type.heap); put("flags", type.flags); put("flagsU64", type.flags.toULong().toString()); put("flagsCanonical", memoryTypeFlags(type.flags)) }) } })
            put("queues", JSONArray().apply { d.queues.forEach { q -> put(JSONObject().apply {
                put("index", q.index); put("count", q.count); put("timestampBits", q.timestampBits); put("flags", q.flags); put("flagsU64", q.flags.toULong().toString()); put("flagsCanonical", queueCapabilityFlags(q.flags)); put("graphics", q.graphics); put("compute", q.compute); put("transfer", q.transfer); put("sparse", q.sparse); put("protected", q.protected); put("videoDecode", q.videoDecode); put("videoEncode", q.videoEncode); put("opticalFlow", q.opticalFlow); put("dataGraph", q.dataGraph); put("unknownFlags", q.unknownFlags); put("granularity", q.granularity); put("videoCodecOperations", if (queueVideoCodecEvidenceRetained(q)) q.videoCodecOperations else JSONObject.NULL); put("videoCodecOperationsU64", if (queueVideoCodecEvidenceRetained(q)) q.videoCodecOperations.toULong().toString() else JSONObject.NULL); put("videoCodecOperationsCanonical", if (queueVideoCodecEvidenceRetained(q)) videoCodecOperationFlags(q.videoCodecOperations) else "Unknown"); put("videoCodecQueryStatus", q.videoCodecQueryStatus); put("videoCodecQueryReason", q.videoCodecQueryReason)
            }) } })
            put("formats", JSONArray().apply { d.formats.forEach { f -> put(JSONObject().apply { put("name", f.name); put("supported", f.supported); put("linear", f.linear); put("linearU64", f.linear.toULong().toString()); put("linearCanonical", formatFeatureFlags(f.linear)); put("optimal", f.optimal); put("optimalU64", f.optimal.toULong().toString()); put("optimalCanonical", formatFeatureFlags(f.optimal)); put("buffer", f.buffer); put("bufferU64", f.buffer.toULong().toString()); put("bufferCanonical", formatFeatureFlags(f.buffer)) }) } })
            put("surface", JSONObject().apply {
                put("queryStatus", d.surfaceQueryStatus); put("queryReason", d.surfaceQueryReason); put("available", d.surfaceAvailable); put("presentationSupported", d.surfacePresentationSupported); put("colorSpaceExtensionStatus", d.surfaceColorSpaceExtensionStatus); put("colorSpaceExtensionAvailable", d.surfaceColorSpaceExtensionAvailable); put("colorSpaceExtensionEnabled", d.surfaceColorSpaceExtensionEnabled)
                put("dependentWsiQueryStatus", d.surfaceDependentWsiQueryStatus); put("formatQueryAttempted", d.surfaceFormatQueryAttempted); put("formatQueryResult", if (d.surfaceFormatQueryAttempted) d.surfaceFormatQueryResult else JSONObject.NULL); put("formatQueryResultCanonical", if (d.surfaceFormatQueryAttempted) vkResultText(d.surfaceFormatQueryResult) else "Not attempted"); put("formatQueryResultSecond", if (d.surfaceFormatQuerySecondAttempted) d.surfaceFormatQueryResultSecond else JSONObject.NULL); put("formatQueryResultSecondCanonical", if (d.surfaceFormatQuerySecondAttempted) vkResultText(d.surfaceFormatQueryResultSecond) else "Not attempted"); put("formatQuerySecondAttempted", d.surfaceFormatQuerySecondAttempted); put("formatQuerySafetyRejected", d.surfaceFormatQuerySafetyRejected); put("formatEnumerationComplete", d.surfaceFormatEnumerationComplete); put("formatQuerySpecAnomaly", d.surfaceFormatQuerySpecAnomaly); put("presentModeEnumerationComplete", d.surfacePresentModeEnumerationComplete); put("presentModeQuerySpecAnomaly", d.surfacePresentModeQuerySpecAnomaly)
                put("capabilities", JSONArray().apply { d.surfaceCapabilities.forEach { value -> put(JSONObject().apply { put("name", value.first); put("value", value.second) }) } })
                put("formats", JSONArray().apply { d.surfaceFormats.forEach { f -> put(JSONObject().apply { put("format", f.format); put("colorSpace", f.colorSpace); put("classification", f.classification); put("description", f.description); put("supported", f.supported) }) } })
                put("presentModes", JSONArray(d.presentModes))
                put("presentationQueues", JSONArray().apply {
                    if (d.presentationQueueEvidence.isNotEmpty()) {
                        d.presentationQueueEvidence.forEach { q -> put(JSONObject().apply { put("queue", q.queueFamily); put("supported", q.supported); q.queryResult?.let { result -> put("queryResult", result); put("queryResultName", vkResultText(result)) } ?: put("queryResult", JSONObject.NULL) }) }
                    } else {
                        d.presentationQueues.forEach { q -> put(JSONObject().apply { put("queue", q.first); put("supported", q.second); put("queryResult", JSONObject.NULL) }) }
                    }
                })
            })
            put("profileEvaluation", JSONArray().apply { vulkanProfileEvaluations(report, d).forEach { profile -> put(profileEvaluationJson(profile)) } })
        }) }
    })
    put("profileCatalog", JSONArray().apply { vulkanProfileCatalog().forEach { value -> put(JSONObject().apply { put("name", value.name); put("revision", value.revision) }) } })
}

internal fun databaseSubmissionJson(context: Context, report: VulkanReport, display: DisplayReport, mode: DriverMode): String {
    val packageInfo = runCatching { context.packageManager.getPackageInfo(context.packageName, 0) }.getOrNull()
    val appVersionCode = if (packageInfo != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageInfo.longVersionCode
    } else {
        @Suppress("DEPRECATION")
        packageInfo?.versionCode?.toLong() ?: 0L
    }
    val device = report.devices.firstOrNull()
    return JSONObject().apply {
        put("schemaVersion", 2)
        put("application", JSONObject().apply {
            put("name", "VulkanScope")
            put("version", packageInfo?.versionName ?: "Unknown")
            put("versionCode", appVersionCode)
            put("packageName", context.packageName)
            put("applicationAbi", detectInstalledAbi(context))
            put("supportedDeviceAbis", JSONArray(Build.SUPPORTED_ABIS.toList()))
        })
        put("device", JSONObject().apply {
            put("platform", platformRuntimeName(context))
            put("chromeOsArcRuntime", isChromeOsRuntime(context))
            put("androidPcFormFactor", isAndroidPcFormFactor(context))
            put("freeformWindowManagement", hasFreeformWindowManagement(context))
            put("googlebookEnvironmentEvidence", googlebookEnvironmentEvidence(context))
            put("googlebookOsVersion", googlebookOsVersionEvidence())
            put("hostOsVersion", hostPlatformVersionEvidence(context))
            put("manufacturer", Build.MANUFACTURER)
            put("brand", Build.BRAND)
            put("model", Build.MODEL)
            put("device", Build.DEVICE)
            put("product", Build.PRODUCT)
            put("androidRelease", Build.VERSION.RELEASE)
            put("sdk", Build.VERSION.SDK_INT)
            put("securityPatch", Build.VERSION.SECURITY_PATCH)
        })
        put("gpu", JSONObject().apply {
            put("name", device?.name ?: "Unknown")
            put("vendorId", device?.vendorId ?: "Unknown")
            put("deviceId", device?.deviceId ?: "Unknown")
            put("deviceType", device?.deviceType ?: "Unknown")
            put("summaryScope", if (report.devices.size > 1) "firstPhysicalDeviceCompatibilitySummary" else "singlePhysicalDevice")
            put("physicalDeviceCount", report.devices.size)
        })
        put("driver", JSONObject().apply {
            put("mode", mode.label)
            put("version", device?.driverVersionText ?: "Unknown")
            put("rawVersion", device?.driverVersion ?: "Unknown")
            put("summaryScope", if (report.devices.size > 1) "firstPhysicalDeviceCompatibilitySummary" else "singlePhysicalDevice")
        })
        put("vulkan", JSONObject().apply {
            put("loaderInstanceApiVersion", report.loaderVersion)
            put("loaderApiVersion", report.loaderVersion)
            put("instanceApiVersion", report.instanceApiVersion)
            put("deviceApiVersion", device?.apiVersion ?: "Unknown")
            put("deviceApiSummaryScope", if (report.devices.size > 1) "firstPhysicalDeviceCompatibilitySummary" else "singlePhysicalDevice")
            put("registryBaseline", report.registryCoverage.baseline)
            put("headerBaseline", report.registryCoverage.headerBaseline)
            put("reportSchema", report.registryCoverage.reportSchema)
        })
        put("collection", JSONObject().apply {
            put("status", if (report.baseReportComplete && report.error == null) "available" else "incomplete")
            put("error", report.error ?: JSONObject.NULL)
            put("deviceCount", report.devices.size)
        })
        put("technicalReport", technicalReportJson(context, report, display, mode))
        put("reportText", reportToText(context, report, display, mode))
    }.toString()
}

internal data class DatabaseSubmissionResult(val success: Boolean, val reportId: String?, val summary: String, val log: String)

internal fun boundedDatabaseSubmissionLog(value: String): String {
    val limit = 96 * 1024
    return if (value.length <= limit) value else value.take(limit) + "\n[log truncated at $limit characters]"
}

internal fun databaseSubmissionExceptionLog(phase: String, error: Throwable): String = boundedDatabaseSubmissionLog(buildString {
    appendLine("VulkanScope Database submission")
    appendLine("result=failure")
    appendLine("phase=$phase")
    appendLine("exception=${error.javaClass.name}")
    appendLine("message=${error.message ?: "Unavailable"}")
    append(error.stackTraceToString())
})

internal fun databaseSubmissionFailure(summary: String, phase: String, detail: String, httpCode: Int? = null, responseBody: String? = null): DatabaseSubmissionResult {
    val log = boundedDatabaseSubmissionLog(buildString {
        appendLine("VulkanScope Database submission")
        appendLine("result=failure")
        appendLine("phase=$phase")
        if (httpCode != null) appendLine("httpStatus=$httpCode")
        appendLine("detail=$detail")
        if (responseBody != null) {
            appendLine("responseBody:")
            append(responseBody.ifBlank { "<empty>" })
        }
    })
    return DatabaseSubmissionResult(false, null, summary, log)
}

internal suspend fun submitDatabaseReport(context: Context, report: VulkanReport, display: DisplayReport, mode: DriverMode): DatabaseSubmissionResult = withContext(Dispatchers.IO) {
    if (!hasValidatedInternet(context)) return@withContext databaseSubmissionFailure("Submission blocked: no validated internet connection is available.", "network-validation", "Android does not report a validated internet connection.")
    if (!report.baseReportComplete) return@withContext databaseSubmissionFailure("Submission blocked: the native base report did not publish its complete-report marker.", "report-validation", "baseReportComplete=false")
    if (report.devices.isEmpty()) return@withContext databaseSubmissionFailure("Submission blocked: the complete report contains no Vulkan® physical device.", "report-validation", "devices=0")
    if (report.error != null) return@withContext databaseSubmissionFailure("Submission blocked: the Vulkan® collection is incomplete. Re-run collection before submitting.", "report-validation", "collectionError=${report.error}")
    val baseUrl = OFFICIAL_DATABASE_API_ENDPOINT.toHttpUrlOrNull() ?: return@withContext databaseSubmissionFailure("The official VulkanScope Database endpoint is invalid.", "endpoint-validation", "Endpoint parsing failed.")
    if (baseUrl.scheme != "https" || baseUrl.host != "vulkanscope-database-api.vulkanscope.workers.dev" || baseUrl.username.isNotEmpty() || baseUrl.password.isNotEmpty() || baseUrl.query != null || baseUrl.fragment != null || baseUrl.encodedPath != "/") return@withContext databaseSubmissionFailure("The official VulkanScope Database endpoint is invalid.", "endpoint-validation", "The fixed endpoint failed origin/path constraints.")
    val submissionUrl = baseUrl.newBuilder().addPathSegments("v1/reports").build()
    val payload = try {
        databaseSubmissionJson(context, report, display, mode).toByteArray(Charsets.UTF_8)
    } catch (error: Throwable) {
        return@withContext DatabaseSubmissionResult(false, null, "Submission failed: the complete report could not be serialized locally.", databaseSubmissionExceptionLog("serialization", error))
    }
    if (payload.size > 2 * 1024 * 1024) return@withContext databaseSubmissionFailure("Submission rejected locally: the complete report exceeds the current VulkanScope Database 2 MiB transport limit. No data was truncated.", "payload-validation", "payloadBytes=${payload.size}; limitBytes=${2 * 1024 * 1024}")
    try {
        val request = Request.Builder()
            .url(submissionUrl)
            .header("Accept", "application/json")
            .post(payload.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()
        databaseHttpClient.newCall(request).execute().use { response ->
            val body = readResponseTextLimited(response.body, 64 * 1024)
            if (!response.isSuccessful) {
                val message = runCatching { JSONObject(body).optString("error") }.getOrDefault("").ifBlank { "Request was rejected" }
                databaseSubmissionFailure("Submission failed (HTTP ${response.code}): $message", "http-response", message, response.code, body)
            } else {
                val id = runCatching { JSONObject(body).optString("id") }.getOrDefault("")
                if (!id.matches(Regex("[a-f0-9]{64}"))) {
                    databaseSubmissionFailure("Submission failed: the Database response did not contain a valid 64-character report ID.", "response-validation", "Successful HTTP response contained an invalid or missing report id.", response.code, body)
                } else {
                    context.getSharedPreferences("database_share", Context.MODE_PRIVATE).edit().putString("last_report_id", id).apply()
                    DatabaseSubmissionResult(true, id, "Report submitted successfully.", boundedDatabaseSubmissionLog(buildString {
                        appendLine("VulkanScope Database submission")
                        appendLine("result=success")
                        appendLine("httpStatus=${response.code}")
                        appendLine("reportId=$id")
                        appendLine("payloadBytes=${payload.size}")
                    }))
                }
            }
        }
    } catch (error: CancellationException) {
        throw error
    } catch (error: Throwable) {
        DatabaseSubmissionResult(false, null, "Submission failed: ${error.message?.take(240) ?: "network error"}", databaseSubmissionExceptionLog("network-request", error))
    }
}

internal fun safeFilePart(value: String): String = value.replace(Regex("[^A-Za-z0-9._-]+"), "_").trim('_').ifBlank { "Unknown-GPU" }

internal fun exportFileStem(report: VulkanReport): String {
    if (report.devices.size > 1) return "VulkanScope-${report.devices.size}-GPUs-report"
    val gpuName = report.devices.firstOrNull()?.name?.trim().orEmpty().ifBlank { "Unknown-GPU" }
    return "VulkanScope-${safeFilePart(gpuName)}-report"
}

internal data class ExportSnapshot(val filename: String, val path: String, val mime: String)

internal fun exportSnapshotRoot(context: Context): File {
    val root = File(context.cacheDir, "report_exports")
    if (!root.exists() && !root.mkdirs()) throw IllegalStateException("Unable to create the report export cache")
    return root.canonicalFile
}

internal fun validatedExportSnapshot(context: Context, snapshot: ExportSnapshot): File {
    if (snapshot.filename.isBlank() || snapshot.path.isBlank() || snapshot.mime !in setOf("text/plain", "text/html", "application/json")) error("Invalid report export snapshot")
    val root = exportSnapshotRoot(context)
    val file = File(snapshot.path).canonicalFile
    val prefix = root.path.trimEnd(File.separatorChar) + File.separator
    if (!file.path.startsWith(prefix) || !file.isFile || !file.canRead() || file.length() <= 0L) error("Report export snapshot is unavailable")
    return file
}

internal suspend fun createExportSnapshot(context: Context, filename: String, mime: String, contentFactory: () -> String): ExportSnapshot {
    require(mime in setOf("text/plain", "text/html", "application/json"))
    val exportContext = currentCoroutineContext()
    val root = exportSnapshotRoot(context)
    val file = File.createTempFile("vulkanscope_report_", ".tmp", root).canonicalFile
    val prefix = root.path.trimEnd(File.separatorChar) + File.separator
    if (!file.path.startsWith(prefix)) {
        file.delete()
        throw SecurityException("Unsafe report export cache path")
    }
    try {
        FileOutputStream(file, false).use { output ->
            val writer = output.writer(Charsets.UTF_8).buffered()
            val content = contentFactory()
            exportContext.ensureActive()
            writer.write(content)
            writer.flush()
            output.fd.sync()
        }
        exportContext.ensureActive()
        if (!file.isFile || !file.canRead() || file.length() <= 0L || file.length() > 64L * 1024L * 1024L) error("The report snapshot could not be persisted within the 64 MiB export bound")
        return ExportSnapshot(filename, file.absolutePath, mime)
    } catch (error: Throwable) {
        runCatching { file.delete() }
        throw error
    }
}

internal fun deleteExportSnapshot(context: Context, snapshot: ExportSnapshot) {
    val root = runCatching { exportSnapshotRoot(context) }.getOrNull() ?: return
    val file = runCatching { File(snapshot.path).canonicalFile }.getOrNull() ?: return
    val prefix = root.path.trimEnd(File.separatorChar) + File.separator
    if (file.path.startsWith(prefix) && file.isFile) runCatching { file.delete() }
}

internal fun cleanupStaleExportSnapshots(context: Context) {
    val root = runCatching { exportSnapshotRoot(context) }.getOrNull() ?: return
    val cutoff = System.currentTimeMillis() - 24L * 60L * 60L * 1000L
    root.listFiles().orEmpty().asSequence().filter { it.isFile && it.name.startsWith("vulkanscope_report_") }.take(512).forEach { file ->
        val canonical = runCatching { file.canonicalFile }.getOrNull() ?: return@forEach
        if (canonical.lastModified() < cutoff) runCatching { canonical.delete() }
    }
}

internal fun reportGpuSummary(report: VulkanReport): String = when (report.devices.size) {
    0 -> "Unknown"
    1 -> report.devices.first().name
    else -> "${report.devices.size} physical devices"
}

internal fun reportToText(context: Context, report: VulkanReport, display: DisplayReport, mode: DriverMode): String = buildString {
    val packageInfo = runCatching { context.packageManager.getPackageInfo(context.packageName, 0) }.getOrNull()
    val appVersionName = packageInfo?.versionName ?: "Unknown"
    val appVersionCode = if (packageInfo != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageInfo.longVersionCode.toString()
    } else {
        @Suppress("DEPRECATION")
        packageInfo?.versionCode?.toString() ?: "Unknown"
    }
    val applicationAbi = detectInstalledAbi(context)
    val supportedDeviceAbis = Build.SUPPORTED_ABIS.joinToString(", ")
    appendLine("VulkanScope report")
    appendLine("=================")
    appendLine("Application: VulkanScope")
    appendLine("Application version: $appVersionName")
    appendLine("Application version code: $appVersionCode")
    appendLine("Application package: ${context.packageName}")
    appendLine("Application ABI: $applicationAbi")
    appendLine("Developer: Semih Boran")
    appendLine("Nickname: EFI Shell")
    appendLine("GitHub: https://github.com/EFIShell0")
    appendLine("GPU: ${reportGpuSummary(report)}")
    appendLine("Driver mode: ${mode.label}")
    appendLine("Loader API: ${report.loaderVersion}")
    appendLine("Base probe instance API: ${report.instanceApiVersion}")
    appendLine("Base report complete: ${report.baseReportComplete}")
    appendLine("Display: ${display.resolution} @ ${display.refreshRate}")
    appendLine("Wide gamut: ${display.wideGamut?.toString() ?: "Unavailable"}")
    appendLine("Preferred wide-gamut color space: ${display.preferredWideGamut}")
    appendLine("HDR capability status: ${display.hdrCapabilityStatus}")
    appendLine("HDR types: ${hdrTypesText(display)}")
    appendLine("HDR luminance: min=${display.minLuminance}, max=${display.maxLuminance}, average=${display.averageLuminance}")
    appendLine("Display modes: ${display.modes.joinToString(" | ").ifBlank { "Not exposed" }}")
    appendLine("Platform: ${platformRuntimeName(context)}")
    appendLine("ChromeOS ARC runtime: ${if (isChromeOsRuntime(context)) "Detected" else "Not detected"}")
    appendLine("Android PC form factor: ${if (isAndroidPcFormFactor(context)) "Detected" else "Not detected"}")
    appendLine("Freeform window management: ${if (hasFreeformWindowManagement(context)) "Detected" else "Not detected"}")
    appendLine("Googlebook environment: ${googlebookEnvironmentEvidence(context)}")
    appendLine("Googlebook OS version: ${googlebookOsVersionEvidence()}")
    appendLine("Host OS version: ${hostPlatformVersionEvidence(context)}")
    appendLine("Android: ${Build.MANUFACTURER} ${Build.MODEL}, ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
    appendLine("Codename=${Build.VERSION.CODENAME}, Security patch=${Build.VERSION.SECURITY_PATCH}")
    appendLine("Brand=${Build.BRAND}, Product=${Build.PRODUCT}, Device=${Build.DEVICE}, Board=${Build.BOARD}, Hardware=${Build.HARDWARE}")
    appendLine("Build ID=${Build.ID}, Incremental=${Build.VERSION.INCREMENTAL}")
    appendLine("Fingerprint=${Build.FINGERPRINT}")
    appendLine("Supported device ABIs: $supportedDeviceAbis")
    if (report.error != null) appendLine("Report error: ${report.error}")
    appendLine("Physical-device enumeration result: ${report.physicalDeviceEnumerationResult?.let(::vkResultText) ?: "Unknown"}")
    appendLine("Physical-device enumeration complete: ${report.physicalDeviceEnumerationComplete}")
    appendLine("Physical-device enumeration safety rejected: ${report.physicalDeviceEnumerationSafetyRejected}")
    if (report.physicalDeviceEnumerationReason.isNotBlank()) appendLine("Physical-device enumeration provenance: ${report.physicalDeviceEnumerationReason}")
    appendLine()
    appendLine("VULKAN REGISTRY COVERAGE")
    appendLine("Baseline=${report.registryCoverage.baseline}")
    appendLine("Mode=${report.registryCoverage.mode}")
    appendLine("Implemented physical-device structs=${report.registryCoverage.implementedPhysicalDeviceStructCount}")
    appendLine("Validated runtime query groups=${report.registryCoverage.validatedRuntimeQueryGroupCount}")
    appendLine("Runtime registry token references=${report.registryCoverage.runtimeRegistryTokenReferenceCount}")
    appendLine("Catalog schema=${report.registryCoverage.catalogSchemaVersion}")
    appendLine("Registry report schema=${report.registryCoverage.reportSchema}")
    appendLine("Header baseline=${report.registryCoverage.headerBaseline}")
    appendLine("Instance dependency candidates=${report.registryCoverage.instanceDependencyCandidateCount}")
    appendLine("Implemented structs=${report.registryCoverage.implementedPhysicalDeviceStructs.joinToString(", ")}")
    appendLine("Validated groups=${report.registryCoverage.validatedRuntimeQueryGroups.joinToString(", ")}")
    appendLine()
    appendLine("PHYSICAL DEVICE GROUPS")
    appendLine("Query status: ${report.instanceGroupStatus}")
    if (report.instanceGroupReason.isNotBlank()) appendLine("Reason: ${report.instanceGroupReason}")
    appendLine("Enumeration result: ${report.instanceGroupEnumerationResult?.let(::vkResultText) ?: "Unknown"}")
    appendLine("Enumeration complete: ${report.instanceGroupEnumerationComplete}")
    if (report.instanceGroupProperties.isEmpty()) appendLine(if (report.instanceGroupEnumerationComplete) "No physical device groups were enumerated" else "No group-property payload available")
    report.instanceGroupProperties.forEach { appendLine("[${it.section}] ${it.name} = ${it.value}") }
    appendLine()
    appendLine("INSTANCE LAYERS")
    appendLine("Enumeration status: ${report.instanceLayerStatus}")
    appendLine("Enumeration reason: ${report.instanceLayerReason.ifBlank { "None" }}")
    report.instanceLayers.forEach { layer ->
        appendLine("${layer.name} | spec ${layer.specVersion} | implementation ${layer.implementationVersion} | ${layer.description}")
        appendLine("  Extension enumeration: ${layer.extensionStatus} | complete=${layer.extensionsComplete} | reason=${layer.extensionReason.ifBlank { "None" }}")
        layer.extensions.forEach { ext -> appendLine("  ${ext.name} | ${ext.scope} | spec ${ext.specVersion} | supported=${ext.supported}") }
    }
    appendLine()
    appendLine("INSTANCE EXTENSIONS")
    appendLine("Enumeration status: ${report.instanceExtensionStatus}")
    appendLine("Enumeration reason: ${report.instanceExtensionReason.ifBlank { "None" }}")
    report.instanceExtensions.forEach { appendLine("${it.name} | spec ${it.specVersion}") }
    appendLine(); appendLine("VULKAN PROFILE EVALUATION")
    report.devices.forEachIndexed { index, d ->
        appendLine("Device #${index + 1}: ${d.name}")
        vulkanProfileEvaluations(report, d).forEach { p ->
            appendLine("${p.name} | ${p.revision} | ${p.status} | ${profileSummary(p)}")
            profileDetailPairs(p).forEach { (label, value) -> appendLine("  - $label: $value") }
        }
    }
        appendLine(); appendLine("VULKAN PROFILES CATALOG")
    vulkanProfileCatalog().forEach { appendLine("${it.name} | ${it.revision}") }
        report.devices.forEachIndexed { index, d ->
        appendLine(); appendLine("DEVICE #${index + 1}: ${d.name}")
        appendLine("API: ${d.apiVersion}"); appendLine("Driver version: ${d.driverVersionText}"); appendLine("Vendor: ${d.vendorId}"); appendLine("Device ID: ${d.deviceId}"); appendLine("Type: ${d.deviceType}"); appendLine("Extended query status: ${d.extendedQueryStatus}"); appendLine("Extended query reason: ${d.extendedQueryReason}"); appendLine("Vulkan® 1.4 status: ${d.vulkan14Status}"); appendLine("Vulkan® 1.4 reason: ${d.vulkan14Reason}"); appendLine("Device extension enumeration status: ${d.deviceExtensionStatus}"); appendLine("Device extension enumeration reason: ${d.deviceExtensionReason}"); appendLine("Device layer enumeration status: ${d.deviceLayerStatus}"); appendLine("Device layer enumeration reason: ${d.deviceLayerReason.ifBlank { "None" }}"); appendLine("Device layer enumeration complete: ${d.deviceLayersComplete}")
        appendLine(); appendLine("DEVICE LAYERS"); d.deviceLayers.forEach { appendLine("${it.name} | spec ${it.specVersion} | implementation ${it.implementationVersion} | ${it.description}"); appendLine("  Extension enumeration: ${it.extensionStatus} | complete=${it.extensionsComplete} | reason=${it.extensionReason.ifBlank { "None" }}"); it.extensions.forEach { ext -> appendLine("  ${ext.name} | spec ${ext.specVersion}") } }; appendLine(); appendLine("DEVICE EXTENSIONS"); d.extensions.forEach { appendLine("${it.name} | ${it.scope} | spec ${it.specVersion}") }
        appendLine(); appendLine("FEATURES"); d.features.forEach { appendLine("${it.name} = ${it.supported}") }
        val detailedSafetyCount = d.detailedProperties.count { it.section == "Vulkan Query Safety" }
        val detailedPropertyCount = d.detailedProperties.size - detailedSafetyCount
        appendLine(); appendLine("DETAILED QUERY RESULTS (${d.detailedProperties.size} evidence rows; $detailedPropertyCount property/query rows; $detailedSafetyCount safety diagnostics; ${d.detailedProperties.map { "${it.section} / ${it.name}" }.distinct().size} unique report fields)"); d.detailedProperties.forEach { appendLine("[${it.section}] ${it.name} = ${it.value}") }
        appendLine(); appendLine("IMAGE FORMAT PROPERTIES2 QUERY OUTCOMES (${d.imageFormatQueryResults.size} exact tuple states; excluded from property/query totals)")
        d.imageFormatQueryResults.forEach { result ->
            val reasonSuffix = if (result.reason.isBlank()) "" else " | Reason=${result.reason}"
            appendLine("${result.name} | ${result.status.uppercase()} | ${result.vkResult?.let(::vkResultText) ?: "VkResult unavailable"}$reasonSuffix")
        }
        appendLine(); appendLine("LIMITS"); d.limits.forEach { appendLine("${it.first} = ${it.second}") }
        appendLine(); appendLine("QUERY SAFETY"); appendLine("Queue-family enumeration safety rejected = ${d.queueQuerySafetyRejected}"); appendLine("Memory-heap count safety rejected = ${d.memoryHeapSafetyRejected}"); appendLine("Memory-type count safety rejected = ${d.memoryTypeSafetyRejected}"); appendLine("Surface queue-family enumeration safety rejected = ${d.surfaceQueueQuerySafetyRejected}")
        appendLine(); appendLine("MEMORY HEAPS"); d.heaps.forEach { appendLine("Heap ${it.index}: ${formatBytes(it.size)} | flags=${memoryHeapFlags(it.flags)} | raw=${it.flags.toULong()} (0x${it.flags.toULong().toString(16).uppercase()})") }
        appendLine(); appendLine("MEMORY TYPES"); d.memoryTypes.forEach { appendLine("Type ${it.index}: heap ${it.heap} | flags=${memoryTypeFlags(it.flags)} | raw=${it.flags.toULong()} (0x${it.flags.toULong().toString(16).uppercase()})") }
        appendLine(); appendLine("QUEUES"); d.queues.forEach { appendLine("Family ${it.index}: count=${it.count}, timestampBits=${it.timestampBits}, flags=${queueCapabilityFlags(it.flags)}, rawFlags=${it.flags.toULong()} (0x${it.flags.toULong().toString(16).uppercase()}), graphics=${it.graphics}, compute=${it.compute}, transfer=${it.transfer}, sparse=${it.sparse}, protected=${it.protected}, videoDecode=${it.videoDecode}, videoEncode=${it.videoEncode}, opticalFlow=${it.opticalFlow}, dataGraph=${it.dataGraph}, unknownFlags=0x${it.unknownFlags.toULong().toString(16).uppercase()}, granularity=${it.granularity}, videoCodecQueryStatus=${it.videoCodecQueryStatus}, videoCodecQueryReason=${it.videoCodecQueryReason.ifBlank { "None" }}, videoCodecOperations=${if (queueVideoCodecEvidenceRetained(it)) videoCodecOperationFlags(it.videoCodecOperations) else "Unknown"}, rawVideoCodecOperations=${if (queueVideoCodecEvidenceRetained(it)) "${it.videoCodecOperations.toULong()} (0x${it.videoCodecOperations.toULong().toString(16).uppercase()})" else "Unknown"}") }
        appendLine(); appendLine("FORMATS"); d.formats.forEach { appendLine("${it.name}: ${if (it.supported) "SUPPORTED" else "NOT SUPPORTED"}, linear=${formatFeatureFlags(it.linear)} [raw ${it.linear.toULong()} / 0x${it.linear.toULong().toString(16).uppercase()}], optimal=${formatFeatureFlags(it.optimal)} [raw ${it.optimal.toULong()} / 0x${it.optimal.toULong().toString(16).uppercase()}], buffer=${formatFeatureFlags(it.buffer)} [raw ${it.buffer.toULong()} / 0x${it.buffer.toULong().toString(16).uppercase()}]") }
        appendLine(); appendLine("SURFACE")
        appendLine("Query status=${d.surfaceQueryStatus}")
        appendLine("Query reason=${d.surfaceQueryReason.ifBlank { "None" }}")
        appendLine("Available=${d.surfaceAvailable}, presentation=${d.surfacePresentationSupported}")
        appendLine("Color-space extension: status=${d.surfaceColorSpaceExtensionStatus}, available=${d.surfaceColorSpaceExtensionAvailable}, enabled=${d.surfaceColorSpaceExtensionEnabled}")
        appendLine("Dependent WSI query status=${d.surfaceDependentWsiQueryStatus}")
        appendLine("Format query: attempted=${d.surfaceFormatQueryAttempted}, first=${if (d.surfaceFormatQueryAttempted) vkResultText(d.surfaceFormatQueryResult) else "Not attempted"}, second=${if (d.surfaceFormatQuerySecondAttempted) vkResultText(d.surfaceFormatQueryResultSecond) else "Not attempted"}, secondAttempted=${d.surfaceFormatQuerySecondAttempted}, safetyRejected=${d.surfaceFormatQuerySafetyRejected}, complete=${d.surfaceFormatEnumerationComplete}, specAnomaly=${d.surfaceFormatQuerySpecAnomaly}")
        appendLine("Present-mode enumeration: complete=${d.surfacePresentModeEnumerationComplete}, specAnomaly=${d.surfacePresentModeQuerySpecAnomaly}")
        d.surfaceCapabilities.forEach { appendLine("${it.first} = ${it.second}") }
        d.surfaceFormats.forEach { appendLine("${it.format} | ${it.colorSpace} | ${it.classification} | ${if (it.supported) "SUPPORTED" else "NOT SUPPORTED"} | ${it.description}") }
        appendLine("Present modes: ${d.presentModes.joinToString(", ")}")
        if (d.presentationQueueEvidence.isNotEmpty()) d.presentationQueueEvidence.forEach { appendLine("Queue ${it.queueFamily}: ${presentationQueueEvidenceText(it)}; queryResult=${it.queryResult?.let(::vkResultText) ?: "Unknown"}") }
        else d.presentationQueues.forEach { appendLine("Queue ${it.first}: present=${it.second}") }
    }
}

internal fun htmlEscape(value: String): String = value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")

internal fun reportToHtml(context: Context, report: VulkanReport, display: DisplayReport, mode: DriverMode): String = buildString {
    val packageInfo = runCatching { context.packageManager.getPackageInfo(context.packageName, 0) }.getOrNull()
    val appVersionName = packageInfo?.versionName ?: "Unknown"
    val appVersionCode = if (packageInfo != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageInfo.longVersionCode.toString()
    } else {
        @Suppress("DEPRECATION")
        packageInfo?.versionCode?.toString() ?: "Unknown"
    }
    val applicationAbi = detectInstalledAbi(context)
    val supportedDeviceAbis = Build.SUPPORTED_ABIS.joinToString(", ")
    val logoData = runCatching {
        context.resources.openRawResource(R.drawable.vulkanscope_logo_horizontal_aligned).use { input ->
            Base64.encodeToString(input.readBytes(), Base64.NO_WRAP)
        }
    }.getOrNull()
    append("<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"><meta name=\"referrer\" content=\"no-referrer\"><meta http-equiv=\"Content-Security-Policy\" content=\"default-src 'none'; img-src data:; style-src 'unsafe-inline'; base-uri 'none'; form-action 'none'; object-src 'none'\"><title>VulkanScope report</title>")
    append("<style>body{font-family:Inter,system-ui,-apple-system,BlinkMacSystemFont,\"Segoe UI\",sans-serif;background:#0a0a0b;color:#f4f4f5;margin:0;line-height:1.45}.wrap{max-width:1320px;margin:0 auto;padding:28px}.hero{background:linear-gradient(135deg,#241012,#0f1012);border:1px solid #3a2022;border-radius:26px;padding:30px;box-shadow:0 16px 50px rgba(0,0,0,.28)}h1{margin:0 0 8px;font-size:36px}h2{margin:0 0 14px;font-size:22px}.muted{color:#a7a7ae}.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(210px,1fr));gap:12px;margin-top:18px}.metric{background:#141111;border:1px solid #342326;border-radius:17px;padding:14px}.section{margin-top:24px;background:#111113;border:1px solid #302124;border-radius:22px;padding:18px;overflow:auto}.section h2{position:sticky;left:0}table{border-collapse:collapse;width:100%;min-width:660px}td,th{border-bottom:1px solid #2c2022;padding:10px 8px;text-align:left;vertical-align:top}th{color:#cbcad0;font-weight:600}.badge{display:inline-block;border-radius:999px;padding:3px 9px;font-size:11px;font-weight:800;letter-spacing:.03em}.yes{background:#133b28;color:#74e2a6}.available{background:#182f52;color:#a9c9ff}.no{background:#49171c;color:#ff8f98}.unavailable{background:#493019;color:#ffc27a}.neutral{background:#30313a;color:#d0d0d6}.incomplete{background:#403713;color:#ffd76b}.unknown{background:#292a2f;color:#c6c6cc}.code{font-family:ui-monospace,SFMono-Regular,Menlo,monospace}.small{font-size:13px}.subtle{color:#7f8088}.github-link{color:#d65c60;text-decoration:none;font-weight:600}.github-link:hover{color:#ed8a8d;text-decoration:underline}.github-link:visited{color:#d65c60}</style></head><body><div class=\"wrap\">")
    append("<div class=\"hero\">")
    if (logoData != null) {
        append("<div style=\"display:flex;align-items:center;justify-content:flex-start;margin-bottom:14px;\"><img src=\"data:image/png;base64,$logoData\" alt=\"VulkanScope\" style=\"display:block;width:min(522px,100%);height:auto;max-height:76px;object-fit:contain;object-position:left center;\"></div>")
    } else {
        append("<h1>VulkanScope</h1>")
    }
    append("<div class=\"muted\">Runtime Vulkan® inspection report</div><div class=\"grid\">")
    fun metric(label: String, value: String) { append("<div class=\"metric\"><div class=\"muted small\">${htmlEscape(label)}</div><strong>${htmlEscape(value)}</strong></div>") }
    metric("GPU", reportGpuSummary(report))
    metric("Driver", mode.label)
    metric("Loader API", report.loaderVersion)
    metric("Base probe instance API", report.instanceApiVersion)
    metric("Base report complete", report.baseReportComplete.toString())
    metric("Display", "${display.resolution} @ ${display.refreshRate}")
    metric("HDR", hdrTypesText(display))
    append("</div>")
    if (report.error != null) append("<p><span class=\"badge no\">ERROR</span> ${htmlEscape(report.error)}</p>")
    append("</div>")

    fun statusBadge(value: String): String {
        val lower = value.trim().lowercase().replace('_', ' ')
        val cls = when {
            lower == "true" || lower == "yes" || lower == "supported" || lower == "pass" -> "yes"
            lower == "available" -> "available"
            lower.contains("unavailable") || lower == "not available" -> "unavailable"
            lower == "false" || lower == "no" || lower == "not supported" || lower == "unsupported" || lower == "fail" || lower == "error" || lower == "not exposed" -> "no"
            lower.contains("not applicable") -> "neutral"
            lower.contains("incomplete") -> "incomplete"
            lower.contains("unknown") || lower.contains("not reported") -> "unknown"
            else -> "unknown"
        }
        return "<span class=\"badge $cls\">${htmlEscape(value.uppercase())}</span>"
    }

    fun table(title: String, headers: String, rows: List<Pair<String, String>>) {
        append("<div class=\"section\"><h2>${htmlEscape(title)}</h2><table><thead><tr>$headers</tr></thead><tbody>")
        rows.forEach { append("<tr><td>${htmlEscape(it.first)}</td><td>${it.second}</td></tr>") }
        append("</tbody></table></div>")
    }

    table("Application", "<th>Property</th><th>Value</th>", listOf(
        "Version" to htmlEscape(appVersionName),
        "Version code" to htmlEscape(appVersionCode),
        "Package" to htmlEscape(context.packageName),
        "Application ABI" to htmlEscape(applicationAbi),
        "Supported device ABIs" to htmlEscape(supportedDeviceAbis),
        "Developer" to "Semih Boran",
        "Nickname" to "EFI Shell",
        "GitHub" to "<a class=\"github-link\" href=\"https://github.com/EFIShell0\" rel=\"noopener noreferrer\">github.com/EFIShell0</a>"
    ))

    table("Android / display", "<th>Property</th><th>Value</th>", listOf(
        "Platform" to htmlEscape(platformRuntimeName(context)), "ChromeOS ARC runtime" to if (isChromeOsRuntime(context)) "Detected" else "Not detected", "Android PC form factor" to if (isAndroidPcFormFactor(context)) "Detected" else "Not detected",
        "Freeform window management" to if (hasFreeformWindowManagement(context)) "Detected" else "Not detected", "Googlebook environment" to htmlEscape(googlebookEnvironmentEvidence(context)), "Googlebook OS version" to htmlEscape(googlebookOsVersionEvidence()), "Host OS version" to htmlEscape(hostPlatformVersionEvidence(context)),
        "Manufacturer" to htmlEscape(Build.MANUFACTURER), "Brand" to htmlEscape(Build.BRAND), "Model" to htmlEscape(Build.MODEL), "Android" to htmlEscape(Build.VERSION.RELEASE),
        "SDK" to Build.VERSION.SDK_INT.toString(), "Security patch" to htmlEscape(Build.VERSION.SECURITY_PATCH.ifBlank { "Unavailable" }), "Codename" to htmlEscape(Build.VERSION.CODENAME),
        "Product" to htmlEscape(Build.PRODUCT), "Device" to htmlEscape(Build.DEVICE), "Board" to htmlEscape(Build.BOARD), "Hardware" to htmlEscape(Build.HARDWARE),
        "Build ID" to htmlEscape(Build.ID), "Incremental" to htmlEscape(Build.VERSION.INCREMENTAL), "Build fingerprint" to htmlEscape(Build.FINGERPRINT), "Resolution" to htmlEscape(display.resolution),
        "Refresh rate" to htmlEscape(display.refreshRate), "Wide gamut" to statusBadge(display.wideGamut?.toString() ?: "Unavailable"),
        "Preferred wide-gamut color space" to htmlEscape(display.preferredWideGamut), "HDR capability status" to statusBadge(display.hdrCapabilityStatus), "HDR types" to htmlEscape(hdrTypesText(display)),
        "HDR min luminance" to htmlEscape(display.minLuminance), "HDR max luminance" to htmlEscape(display.maxLuminance), "HDR average luminance" to htmlEscape(display.averageLuminance),
        "Display modes" to htmlEscape(display.modes.joinToString(" | ").ifBlank { "Not exposed" })
    ))

    table("Physical-device enumeration", "<th>Property</th><th>Value</th>", listOf(
        "Result" to htmlEscape(report.physicalDeviceEnumerationResult?.let(::vkResultText) ?: "Unknown"),
        "Complete" to report.physicalDeviceEnumerationComplete.toString(),
        "Safety rejected" to report.physicalDeviceEnumerationSafetyRejected.toString(),
        "Provenance" to htmlEscape(report.physicalDeviceEnumerationReason.ifBlank { "None" })
    ))

    table("Vulkan® Registry Coverage", "<th>Property</th><th>Value</th>", listOf(
        "Baseline" to htmlEscape(report.registryCoverage.baseline), "Mode" to htmlEscape(report.registryCoverage.mode),
        "Implemented physical-device structs" to report.registryCoverage.implementedPhysicalDeviceStructCount.toString(),
        "Validated runtime query groups" to report.registryCoverage.validatedRuntimeQueryGroupCount.toString(),
        "Runtime registry token references" to report.registryCoverage.runtimeRegistryTokenReferenceCount.toString(),
        "Catalog schema" to report.registryCoverage.catalogSchemaVersion.toString(), "Registry report schema" to htmlEscape(report.registryCoverage.reportSchema),
        "Header baseline" to htmlEscape(report.registryCoverage.headerBaseline), "Instance dependency candidates" to report.registryCoverage.instanceDependencyCandidateCount.toString(),
        "Implemented structs" to htmlEscape(report.registryCoverage.implementedPhysicalDeviceStructs.joinToString(", ")),
        "Validated groups" to htmlEscape(report.registryCoverage.validatedRuntimeQueryGroups.joinToString(", "))
    ))

    table("Physical device groups", "<th>Group</th><th>Value</th>", buildList {
        add("Query status" to htmlEscape(report.instanceGroupStatus))
        if (report.instanceGroupReason.isNotBlank()) add("Reason" to htmlEscape(report.instanceGroupReason))
        add("Enumeration result" to htmlEscape(report.instanceGroupEnumerationResult?.let(::vkResultText) ?: "Unknown"))
        add("Enumeration complete" to report.instanceGroupEnumerationComplete.toString())
        if (report.instanceGroupProperties.isEmpty()) add("Evidence" to htmlEscape(if (report.instanceGroupEnumerationComplete) "No physical device groups were enumerated" else "No group-property payload available"))
        report.instanceGroupProperties.forEach { add(it.name to htmlEscape(it.value)) }
    })

    table("Instance extension enumeration", "<th>Property</th><th>Value</th>", listOf(
        "Status" to statusBadge(report.instanceExtensionStatus),
        "Reason" to htmlEscape(report.instanceExtensionReason.ifBlank { "None" })
    ))
    append("<div class=\"section\"><h2>Instance extensions</h2><table><thead><tr><th>Name</th><th>Scope / spec</th></tr></thead><tbody>")
    report.instanceExtensions.forEach { ext ->
        append("<tr><td class=\"code\">${htmlEscape(ext.name)}</td><td>${htmlEscape("${ext.scope} / ${ext.specVersion}")}</td></tr>")
    }
    append("</tbody></table></div>")

    table("Instance layer enumeration", "<th>Property</th><th>Value</th>", listOf(
        "Status" to statusBadge(report.instanceLayerStatus),
        "Reason" to htmlEscape(report.instanceLayerReason.ifBlank { "None" })
    ))
    table("Instance layers", "<th>Layer</th><th>Details</th>", report.instanceLayers.map { layer ->
        layer.name to htmlEscape("spec ${layer.specVersion}, implementation ${layer.implementationVersion}; ${layer.description}; extensionQuery=${layer.extensionStatus}; complete=${layer.extensionsComplete}; reason=${layer.extensionReason.ifBlank { "None" }}; extensions=${layer.extensions.joinToString(", ") { "${it.name}(${it.specVersion})" }.ifBlank { "none" }}")
    })

    val profileRows = report.devices.flatMap { device -> vulkanProfileEvaluations(report, device).map { evaluation ->
        "${device.name} / ${evaluation.name}" to "${statusBadge(evaluation.status)} ${profileHtmlDetails(evaluation)}"
    } }
    table("Vulkan® Profile evaluation", "<th>Device / profile</th><th>Status / details</th>", profileRows)
    table("Vulkan® Profiles catalog", "<th>Profile</th><th>Revision</th>", vulkanProfileCatalog().map { it.name to htmlEscape(it.revision) })

    report.devices.forEach { d ->
        append("<div class=\"section\"><h2>Device: ${htmlEscape(d.name)}</h2>")
        table("Device properties", "<th>Property</th><th>Value</th>", listOf(
            "API" to htmlEscape(d.apiVersion), "Driver version" to htmlEscape(d.driverVersionText), "Vendor" to htmlEscape(d.vendorId),
            "Device ID" to htmlEscape(d.deviceId), "Type" to htmlEscape(d.deviceType), "Extended query status" to statusBadge(d.extendedQueryStatus),
            "Extended query reason" to htmlEscape(d.extendedQueryReason), "Device extension enumeration status" to statusBadge(d.deviceExtensionStatus), "Device extension enumeration reason" to htmlEscape(d.deviceExtensionReason.ifBlank { "None" }), "Device layer enumeration status" to statusBadge(d.deviceLayerStatus), "Device layer enumeration reason" to htmlEscape(d.deviceLayerReason.ifBlank { "None" }), "Vulkan 1.4 status" to statusBadge(d.vulkan14Status), "Vulkan 1.4 reason" to htmlEscape(d.vulkan14Reason.ifBlank { "None" })
        ))
        append("<div class=\"section\"><h2>Device extensions</h2><table><thead><tr><th>Extension</th><th>Scope</th><th>Spec version</th><th>Status</th></tr></thead><tbody>")
        d.extensions.forEach { ext ->
            append("<tr><td class=\"code\">${htmlEscape(ext.name)}</td><td>${htmlEscape(ext.scope)}</td><td>${ext.specVersion}</td><td>${statusBadge(if (ext.supported) "SUPPORTED" else "NOT SUPPORTED")}</td></tr>")
        }
        append("</tbody></table></div>")
        table("Device layer enumeration", "<th>Property</th><th>Value</th>", listOf("Status" to statusBadge(d.deviceLayerStatus), "Reason" to htmlEscape(d.deviceLayerReason.ifBlank { "None" }), "Complete" to htmlEscape(d.deviceLayersComplete.toString())))
        table("Device layers", "<th>Layer</th><th>Details</th>", d.deviceLayers.map { layer -> layer.name to htmlEscape("spec ${layer.specVersion}, implementation ${layer.implementationVersion}; ${layer.description}; extensionQuery=${layer.extensionStatus}; complete=${layer.extensionsComplete}; reason=${layer.extensionReason.ifBlank { "None" }}; extensions=${layer.extensions.joinToString(", ") { "${it.name}(${it.specVersion})" }.ifBlank { "none" }}") })
        table("Features", "<th>Feature</th><th>Status</th>", d.features.map { htmlEscape(it.name) to statusBadge(if (it.supported) "SUPPORTED" else "NOT SUPPORTED") })
        val htmlDetailedSafetyCount = d.detailedProperties.count { it.section == "Vulkan Query Safety" }
        val htmlDetailedPropertyCount = d.detailedProperties.size - htmlDetailedSafetyCount
        table("Detailed query results (${d.detailedProperties.size} evidence rows; $htmlDetailedPropertyCount property/query rows; $htmlDetailedSafetyCount safety diagnostics; ${d.detailedProperties.map { "${it.section} / ${it.name}" }.distinct().size} unique report fields)", "<th>Section / property</th><th>Value</th>", d.detailedProperties.map { "${it.section} / ${it.name}" to htmlEscape(it.value) })
        table("Image Format Properties2 query outcomes (${d.imageFormatQueryResults.size} exact tuple states; excluded from property/query totals)", "<th>Format / tiling / external handle</th><th>Query result</th>", d.imageFormatQueryResults.map { result ->
            val value = when (result.status) {
                "available" -> statusBadge("AVAILABLE") + " ${htmlEscape(vkResultText(0))}"
                "unsupported" -> statusBadge("UNSUPPORTED") + " ${htmlEscape(vkResultText(-11))}"
                "unavailable" -> statusBadge("UNAVAILABLE") + " ${htmlEscape(result.vkResult?.let(::vkResultText) ?: "VkResult unavailable")}"
                "not_applicable" -> statusBadge("NOT APPLICABLE") + " ${htmlEscape(result.reason)}"
                else -> statusBadge("UNKNOWN")
            }
            result.name to value
        })
        table("Limits", "<th>Limit</th><th>Value</th>", d.limits.map { it.first to htmlEscape(it.second) })
        table("Memory", "<th>Entry</th><th>Value</th>", d.heaps.map { "Heap ${it.index}" to htmlEscape("${formatBytes(it.size)} | flags=${memoryHeapFlags(it.flags)} | raw=${it.flags.toULong()} (0x${it.flags.toULong().toString(16).uppercase()})") } + d.memoryTypes.map { "Type ${it.index}" to htmlEscape("heap ${it.heap} | flags=${memoryTypeFlags(it.flags)} | raw=${it.flags.toULong()} (0x${it.flags.toULong().toString(16).uppercase()})") })
        table("Queues", "<th>Family</th><th>Details</th>", d.queues.map { "${it.index}" to htmlEscape("count=${it.count}, timestampBits=${it.timestampBits}, flags=${queueCapabilityFlags(it.flags)}, rawFlags=${it.flags.toULong()} (0x${it.flags.toULong().toString(16).uppercase()}), graphics=${it.graphics}, compute=${it.compute}, transfer=${it.transfer}, sparse=${it.sparse}, protected=${it.protected}, videoDecode=${it.videoDecode}, videoEncode=${it.videoEncode}, opticalFlow=${it.opticalFlow}, dataGraph=${it.dataGraph}, unknownFlags=0x${it.unknownFlags.toULong().toString(16).uppercase()}, granularity=${it.granularity}, videoCodecQueryStatus=${it.videoCodecQueryStatus}, videoCodecQueryReason=${it.videoCodecQueryReason.ifBlank { "None" }}, videoCodecOperations=${if (queueVideoCodecEvidenceRetained(it)) videoCodecOperationFlags(it.videoCodecOperations) else "Unknown"}, rawVideoCodecOperations=${if (queueVideoCodecEvidenceRetained(it)) "${it.videoCodecOperations.toULong()} (0x${it.videoCodecOperations.toULong().toString(16).uppercase()})" else "Unknown"}") })
        table("Formats", "<th>Format</th><th>Status / feature masks</th>", d.formats.map { htmlEscape(it.name) to "${statusBadge(if (it.supported) "SUPPORTED" else "NOT SUPPORTED")} ${htmlEscape("linear=${formatFeatureFlags(it.linear)} [raw ${it.linear.toULong()} / 0x${it.linear.toULong().toString(16).uppercase()}], optimal=${formatFeatureFlags(it.optimal)} [raw ${it.optimal.toULong()} / 0x${it.optimal.toULong().toString(16).uppercase()}], buffer=${formatFeatureFlags(it.buffer)} [raw ${it.buffer.toULong()} / 0x${it.buffer.toULong().toString(16).uppercase()}]")}" })
        table("Query safety", "<th>Property</th><th>Value</th>", listOf(
            "Queue-family enumeration safety rejected" to htmlEscape(d.queueQuerySafetyRejected.toString()),
            "Memory-heap count safety rejected" to htmlEscape(d.memoryHeapSafetyRejected.toString()),
            "Memory-type count safety rejected" to htmlEscape(d.memoryTypeSafetyRejected.toString()),
            "Surface queue-family enumeration safety rejected" to htmlEscape(d.surfaceQueueQuerySafetyRejected.toString())
        ))
        table("Surface query diagnostics", "<th>Property</th><th>Value</th>", listOf(
            "Surface query status" to statusBadge(d.surfaceQueryStatus), "Surface query reason" to htmlEscape(d.surfaceQueryReason.ifBlank { "None" }), "Surface available" to statusBadge(if (d.surfaceAvailable) "AVAILABLE" else "NOT AVAILABLE"),
            "Presentation supported" to statusBadge(if (d.surfacePresentationSupported) "SUPPORTED" else if (d.surfaceQueryStatus == "available") "NOT SUPPORTED" else "UNKNOWN"),
            "Color-space extension status" to statusBadge(d.surfaceColorSpaceExtensionStatus),
            "Color-space extension available" to statusBadge(if (d.surfaceColorSpaceExtensionStatus == "available") "AVAILABLE" else if (d.surfaceColorSpaceExtensionStatus == "not_exposed") "NOT EXPOSED" else "UNKNOWN"),
            "Color-space extension enabled" to htmlEscape(d.surfaceColorSpaceExtensionEnabled.toString()),
            "Dependent WSI query status" to statusBadge(d.surfaceDependentWsiQueryStatus),
            "Format query attempted" to htmlEscape(d.surfaceFormatQueryAttempted.toString()),
            "Format query first result" to htmlEscape(if (d.surfaceFormatQueryAttempted) vkResultText(d.surfaceFormatQueryResult) else "Not attempted"),
            "Format query second result" to htmlEscape(if (d.surfaceFormatQuerySecondAttempted) vkResultText(d.surfaceFormatQueryResultSecond) else "Not attempted"),
            "Second query attempted" to htmlEscape(d.surfaceFormatQuerySecondAttempted.toString()),
            "Safety rejected" to htmlEscape(d.surfaceFormatQuerySafetyRejected.toString()),
            "Format enumeration complete" to htmlEscape(d.surfaceFormatEnumerationComplete.toString()),
            "Format specification anomaly" to htmlEscape(d.surfaceFormatQuerySpecAnomaly.toString()),
            "Present-mode enumeration complete" to htmlEscape(d.surfacePresentModeEnumerationComplete.toString()),
            "Present-mode specification anomaly" to htmlEscape(d.surfacePresentModeQuerySpecAnomaly.toString())
        ) + d.surfaceCapabilities.map { it.first to htmlEscape(it.second) })
        table("Surface formats / color spaces", "<th>Format / color space</th><th>Status / description</th>", d.surfaceFormats.map { "${it.format} / ${it.colorSpace}" to "${statusBadge(if (it.supported) "SUPPORTED" else "NOT SUPPORTED")} ${htmlEscape("${it.classification}; ${it.description}")}" })
        table("Present modes", "<th>Mode</th><th>Status</th>", d.presentModes.map { it to statusBadge("SUPPORTED") })
        val htmlPresentationQueues = if (d.presentationQueueEvidence.isNotEmpty()) d.presentationQueueEvidence.map { entry ->
            "Queue ${entry.queueFamily}" to if (entry.queryResult != null && entry.queryResult != 0) statusBadge("UNAVAILABLE") + " ${htmlEscape(vkResultText(entry.queryResult))}" else statusBadge(if (entry.supported) "SUPPORTED" else "NOT SUPPORTED")
        } else d.presentationQueues.map { "Queue ${it.first}" to statusBadge(if (it.second) "SUPPORTED" else "NOT SUPPORTED") }
        table("Presentation queues", "<th>Queue family</th><th>Status</th>", htmlPresentationQueues)
        append("</div>")
    }
    append("</div></body></html>")
}
