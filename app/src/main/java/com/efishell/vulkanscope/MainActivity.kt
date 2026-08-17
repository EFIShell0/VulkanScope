package com.efishell.vulkanscope

import android.app.ActivityManager
import android.graphics.Color
import android.os.Bundle
import android.content.res.Configuration
import android.os.Build
import android.os.Process
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.util.Log
import android.util.Base64
import android.hardware.display.DisplayManager
import android.content.Intent
import android.content.Context
import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream
import java.util.Collections
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject

private data class DisplayReport(
    val resolution: String,
    val refreshRate: String,
    val wideGamut: Boolean,
    val preferredWideGamut: String,
    val hdrTypes: List<String>,
    val minLuminance: String,
    val maxLuminance: String,
    val averageLuminance: String,
    val modes: List<String>
)

private data class ExtensionEntry(val name: String, val scope: String, val specVersion: Int, val supported: Boolean = true)
private data class LayerEntry(val name: String, val description: String, val specVersion: Int, val implementationVersion: Int, val extensions: List<ExtensionEntry> = emptyList())
private data class FeatureEntry(val name: String, val supported: Boolean)
private data class SurfaceFormatEntry(val format: String, val colorSpace: String, val classification: String, val description: String, val supported: Boolean = true)
private data class FormatEntry(val name: String, val supported: Boolean, val linear: Long, val optimal: Long, val buffer: Long)
private data class PropertyEntry(val section: String, val name: String, val value: String)
private data class QueueEntry(val index: Int, val count: Int, val timestampBits: Int, val flags: Long, val graphics: Boolean, val compute: Boolean, val transfer: Boolean, val sparse: Boolean, val protected: Boolean, val videoDecode: Boolean, val videoEncode: Boolean, val opticalFlow: Boolean, val dataGraph: Boolean, val unknownFlags: Long, val granularity: String, val videoCodecOperations: Long = 0L)
private data class MemoryHeapEntry(val index: Int, val size: Long, val flags: Long)
private data class MemoryTypeEntry(val index: Int, val heap: Int, val flags: Long)
private val ISOLATED_CORE_GROUPS = linkedMapOf(
    "core11" to "Vulkan 1.1 Core",
    "core12" to "Vulkan 1.2 Core",
    "core13" to "Vulkan 1.3 Core",
    "core14" to "Vulkan 1.4 Core"
)

private val ISOLATED_ADVANCED_GROUPS = linkedMapOf(
    "tools" to "Vulkan Tools",
    "queue2" to "Queue Family Properties 2",
    "format2" to "Format Properties 2/3",
    "imageFormat2" to "Image Format Properties 2",
    "external" to "External Memory/Fence/Semaphore",
    "sparse" to "Sparse Image Format Properties 2",
    "memory2" to "Memory Properties 2",
    "groups" to "Physical Device Groups",
    "videoCapabilities" to "Vulkan Video Capabilities"
)

private val ISOLATED_EXTENSION_GROUPS = linkedMapOf(
    "descriptorHeap" to "VK_EXT_descriptor_heap",
    "astc3D" to "VK_EXT_texture_compression_astc_3d",
    "shaderLongVector" to "VK_EXT_shader_long_vector",
    "shaderSubgroupPartitioned" to "VK_EXT_shader_subgroup_partitioned",
    "internallySynchronizedQueues" to "VK_KHR_internally_synchronized_queues",
    "pushConstantBank" to "VK_NV_push_constant_bank",
    "computeOccupancyPriority" to "VK_NV_compute_occupancy_priority",
    "dataGraphNeuralAcceleratorStatistics" to "VK_ARM_data_graph_neural_accelerator_statistics",
    "shaderInstrumentation" to "VK_ARM_shader_instrumentation",
    "multisampledRenderToSwapchain" to "VK_EXT_multisampled_render_to_swapchain",
    "primitiveRestartIndex" to "VK_EXT_primitive_restart_index",
    "shaderSplitBarrier" to "VK_EXT_shader_split_barrier",
    "deviceFault" to "VK_KHR_device_fault",
    "opacityMicromap" to "VK_KHR_opacity_micromap",
    "shaderAbort" to "VK_KHR_shader_abort",
    "shaderConstantData" to "VK_KHR_shader_constant_data",
    "videoEncodeFeedback2" to "VK_KHR_video_encode_feedback2",
    "cooperativeMatrixDecodeVector" to "VK_NV_cooperative_matrix_decode_vector",
    "cooperativeMatrixConversion" to "VK_QCOM_cooperative_matrix_conversion",
    "elapsedTimerQuery" to "VK_QCOM_elapsed_timer_query",
    "imageProcessing3" to "VK_QCOM_image_processing3",
    "queuePerfHint" to "VK_QCOM_queue_perf_hint",
    "shaderMultipleWaitQueues" to "VK_QCOM_shader_multiple_wait_queues",
    "throttleHint" to "VK_SEC_throttle_hint",
    "shaderMixedFloatDotProduct" to "VK_VALVE_shader_mixed_float_dot_product",
    "maintenance7" to "VK_KHR_maintenance7",
    "maintenance8" to "VK_KHR_maintenance8",
    "maintenance9" to "VK_KHR_maintenance9",
    "maintenance10" to "VK_KHR_maintenance10",
    "fifoLatestReady" to "VK_KHR_present_mode_fifo_latest_ready",
    "presentId2" to "VK_KHR_present_id2",
    "presentWait2" to "VK_KHR_present_wait2",
    "pipelineBinary" to "VK_KHR_pipeline_binary",
    "cooperativeMatrix" to "VK_KHR_cooperative_matrix",
    "fragmentDensityMap" to "VK_EXT_fragment_density_map",
    "fragmentDensityMap2" to "VK_EXT_fragment_density_map2",
    "maintenance11" to "VK_KHR_maintenance11",
    "deviceAddressCommands" to "VK_KHR_device_address_commands",
    "shaderUniformBufferUnsizedArray" to "VK_EXT_shader_uniform_buffer_unsized_array",
    "dataGraphOpticalFlow" to "VK_ARM_data_graph_optical_flow",
    "pipelineCacheIncrementalMode" to "VK_SEC_pipeline_cache_incremental_mode",
    "extendedFlags" to "VK_KHR_extended_flags",
    "shaderOcpMicroscalingTypes" to "VK_EXT_shader_ocp_microscaling_types",
    "descriptorBufferParity" to "VK_EXT_descriptor_buffer",
    "accelerationStructureParity" to "VK_KHR_acceleration_structure",
    "rayTracingPipelineParity" to "VK_KHR_ray_tracing_pipeline",
    "rayQueryParity" to "VK_KHR_ray_query",
    "meshShaderParity" to "VK_EXT_mesh_shader",
    "graphicsPipelineLibraryParity" to "VK_EXT_graphics_pipeline_library",
    "shaderObjectParity" to "VK_EXT_shader_object",
    "hostImageCopyParity" to "VK_EXT_host_image_copy",
    "extendedDynamicStateParity" to "VK_EXT_extended_dynamic_state",
    "extendedDynamicState3Parity" to "VK_EXT_extended_dynamic_state3",
    "fragmentShaderBarycentricParity" to "VK_KHR_fragment_shader_barycentric",
    "fragmentShadingRateParity" to "VK_KHR_fragment_shading_rate",
    "transformFeedbackParity" to "VK_EXT_transform_feedback",
    "vertexAttributeDivisorParity" to "VK_EXT_vertex_attribute_divisor",
    "inlineUniformBlockParity" to "VK_KHR_inline_uniform_block",
    "privateDataParity" to "VK_EXT_private_data",
    "synchronization2Parity" to "VK_KHR_synchronization2"
)

private data class DeviceReport(
    val name: String,
    val apiVersion: String,
    val driverVersion: String,
    val driverVersionText: String,
    val vendorId: String,
    val vendorIdRaw: Long,
    val deviceId: String,
    val deviceType: String,
    val deviceLayers: List<LayerEntry>,
    val extensions: List<ExtensionEntry>,
    val deviceExtensionStatus: String = "unknown",
    val deviceExtensionReason: String = "",
    val features: List<FeatureEntry>,
    val queues: List<QueueEntry>,
    val heaps: List<MemoryHeapEntry>,
    val memoryTypes: List<MemoryTypeEntry>,
    val formats: List<FormatEntry>,
    val limits: List<Pair<String, String>>,
    val detailedProperties: List<PropertyEntry>,
    val extendedQueryStatus: String,
    val extendedQueryReason: String,
    val surfaceAvailable: Boolean,
    val surfacePresentationSupported: Boolean,
    val surfaceColorSpaceExtensionAvailable: Boolean,
    val surfaceColorSpaceExtensionEnabled: Boolean,
    val surfaceFormatQueryResult: Int,
    val surfaceFormatQueryResultSecond: Int,
    val surfaceFormatQuerySecondAttempted: Boolean,
    val surfaceFormatQuerySafetyRejected: Boolean,
    val surfaceCapabilities: List<Pair<String, String>>,
    val surfaceFormats: List<SurfaceFormatEntry>,
    val presentModes: List<String>,
    val presentationQueues: List<Pair<Int, Boolean>>,
    val vulkan14Status: String = "not_applicable",
    val vulkan14Reason: String = ""
)

private data class RegistryCoverage(
    val baseline: String = "Unknown",
    val mode: String = "Unknown",
    val implementedPhysicalDeviceStructCount: Int = 0,
    val validatedRuntimeQueryGroupCount: Int = 0,
    val runtimeExtensionTokenCount: Int = 0,
    val catalogSchemaVersion: Int = 0,
    val reportSchema: String = "Unknown",
    val headerBaseline: String = "Unknown",
    val instanceDependencyCandidateCount: Int = 0,
    val implementedPhysicalDeviceStructs: List<String> = emptyList(),
    val validatedRuntimeQueryGroups: List<String> = emptyList()
)

private data class VulkanReport(val loaderVersion: String, val instanceExtensions: List<ExtensionEntry>, val instanceLayers: List<LayerEntry>, val devices: List<DeviceReport>, val error: String?, val registryCoverage: RegistryCoverage = RegistryCoverage())

private enum class Page(val title: String) {
    Overview("Overview"), Vulkan("Vulkan"), Display("Display & HDR"), Surface("Surface"), Features("Features"), Memory("Memory"), Queues("Queues"), Formats("Formats"), Properties("Properties & Limits"), Extensions("Extensions"), Profiles("Profiles"), Settings("Settings"), Info("Info")
}

private enum class DriverMode(val label: String) {
    SYSTEM("System Vulkan driver"),
    TURNIP("Turnip / third-party driver")
}

private enum class TurnipSupport { UNKNOWN, SUPPORTED, UNSUPPORTED }
private enum class CollectionStatus { IDLE, COLLECTING, COMPLETED }

class MainActivity : ComponentActivity() {

    private lateinit var prefs: android.content.SharedPreferences
    private var turnipSupport by mutableStateOf(TurnipSupport.UNKNOWN)
    private var driverMode by mutableStateOf(DriverMode.SYSTEM)
    private val surfaceLock = Any()
    private val collectMutex = Mutex()
    private var currentSurface: Surface? = null
    private var latestReport by mutableStateOf<VulkanReport?>(null)
    private var displayReportState by mutableStateOf<DisplayReport?>(null)
    private var reportLoading by mutableStateOf(true)
    private var collectionStatus by mutableStateOf(CollectionStatus.IDLE)
    private var collectionInFlight = false
    private var collectionPending = false
    private val pendingCollectionTasks = mutableSetOf<String>()
    private var collectionGeneration = 0L
    private var collectionCompletionJob: Job? = null
    private val backgroundQueryGroups = (ISOLATED_CORE_GROUPS.keys + ISOLATED_ADVANCED_GROUPS.keys + ISOLATED_EXTENSION_GROUPS.keys).toSet()
    private val activityScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val requestedQueryGroups = Collections.synchronizedSet(mutableSetOf<String>())

    override fun onResume() {
        super.onResume()
        displayReportState = displayReport()
    }

    override fun onDestroy() {
        activityScope.cancel()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.navigationBarColor = android.graphics.Color.rgb(17, 17, 17)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        prefs = getSharedPreferences("settings", MODE_PRIVATE)
        displayReportState = displayReport()
        driverMode = DriverMode.values().find { it.name == prefs.getString("driver_mode", DriverMode.SYSTEM.name) } ?: DriverMode.SYSTEM
        if (!isTurnipPlatformEligible()) turnipSupport = TurnipSupport.UNSUPPORTED
        setContent {
            VulkanScopeApp(
                displayReport = displayReportState ?: DisplayReport("Unknown", "Unknown", false, "Not exposed", emptyList(), "Not exposed", "Not exposed", "Not exposed", emptyList()),
                report = latestReport,
                loading = reportLoading,
                collectionStatus = collectionStatus,
                surfaceReady = { surface ->
                    val changed = synchronized(surfaceLock) {
                        val different = currentSurface !== surface
                        currentSurface = surface
                        different
                    }
                    if (changed) {
                        if (latestReport == null) {
                            requestReportCollection()
                        } else {
                            requestSurfaceRefresh(surface)
                        }
                    }
                },
                surfaceDestroyed = {
                    synchronized(surfaceLock) { currentSurface = null }
                },
                driverMode = driverMode,
                turnipSupport = turnipSupport,
                onDriverModeChanged = { mode -> requestDriverModeChange(mode) },
                onInstallDriverBundle = { openDriverBundlePicker() },
                onPageOpened = { page -> requestPageQueries(page) },
                onRequestQuery = { group -> requestQueryGroup(group) }
            )
        }
    }

    private val driverPickerLauncher = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) installDriverBundle(uri)
    }

    private fun openDriverBundlePicker() {
        if (!Build.SUPPORTED_ABIS.contains("arm64-v8a")) {
            android.widget.Toast.makeText(this, "Turnip requires Android 9+ and arm64-v8a.", android.widget.Toast.LENGTH_LONG).show()
            return
        }
        if (turnipSupport != TurnipSupport.SUPPORTED) {
            android.widget.Toast.makeText(this, "Turnip is available only when a Qualcomm Adreno Vulkan device is detected.", android.widget.Toast.LENGTH_LONG).show()
            return
        }
        driverPickerLauncher.launch(arrayOf("application/zip", "application/octet-stream", "*/*"))
    }

    private fun installDriverBundle(uri: Uri) {
        val finalDir = File(filesDir, "turnip")
        val tempDir = File(filesDir, "turnip_import_${System.currentTimeMillis()}")
        try {
            tempDir.mkdirs()
            var entryCount = 0
            var totalBytes = 0L
            val maxEntries = 2048
            val maxTotalBytes = 64L * 1024L * 1024L
            val maxFileBytes = 32L * 1024L * 1024L
            contentResolver.openInputStream(uri)?.use { input ->
                ZipInputStream(input).use { zip ->
                    var entry = zip.nextEntry
                    while (entry != null) {
                        if (++entryCount > maxEntries) throw SecurityException("Driver bundle contains too many entries")
                        if (entry.name.isBlank()) throw SecurityException("Driver bundle contains an empty path")
                        val safe = File(tempDir, entry.name).canonicalFile
                        if (!safe.path.startsWith(tempDir.canonicalPath + File.separator) && safe != tempDir.canonicalFile) {
                            throw SecurityException("Unsafe driver bundle path")
                        }
                        if (entry.isDirectory) safe.mkdirs() else {
                            safe.parentFile?.mkdirs()
                            var fileBytes = 0L
                            FileOutputStream(safe).use { output ->
                                val buffer = ByteArray(32 * 1024)
                                while (true) {
                                    val read = zip.read(buffer)
                                    if (read <= 0) break
                                    fileBytes += read
                                    totalBytes += read
                                    if (fileBytes > maxFileBytes || totalBytes > maxTotalBytes) throw SecurityException("Driver bundle is too large")
                                    output.write(buffer, 0, read)
                                }
                            }
                        }
                        zip.closeEntry()
                        entry = zip.nextEntry
                    }
                }
            } ?: throw IllegalStateException("Unable to read bundle")

            val metadataFile = tempDir.walkTopDown().firstOrNull { it.isFile && it.name.equals("meta.json", true) }
                ?: throw IllegalArgumentException("This is not an AdrenoTools driver package. Required file: meta.json")
            if (metadataFile.length() > 1024L * 1024L) {
                throw IllegalArgumentException("AdrenoTools meta.json is too large")
            }
            val metadata = runCatching { JSONObject(metadataFile.readText()) }.getOrElse {
                throw IllegalArgumentException("The AdrenoTools meta.json is invalid")
            }
            val schemaVersion = metadata.optInt("schemaVersion", -1)
            if (schemaVersion != 1) {
                throw IllegalArgumentException("Unsupported AdrenoTools package schema: ${if (schemaVersion < 0) "missing" else schemaVersion}")
            }
            val declared = metadata.optString("libraryName").trim()
            if (declared.isBlank() || !declared.endsWith(".so", true)) {
                throw IllegalArgumentException("AdrenoTools meta.json does not declare a Vulkan library")
            }
            val library = tempDir.walkTopDown().firstOrNull { it.isFile && it.name == declared }
                ?: throw IllegalArgumentException("Driver metadata references missing library: $declared")
            if (!declared.contains("vulkan", true)) {
                throw IllegalArgumentException("The selected AdrenoTools package does not declare a Vulkan driver library")
            }

            
            
            
            if (!library.canRead() || library.length() == 0L) {
                throw IllegalArgumentException("The Vulkan .so driver library is empty or unreadable")
            }

            val backupDir = File(filesDir, "turnip_backup_${System.currentTimeMillis()}")
            if (finalDir.exists() && !finalDir.renameTo(backupDir)) throw IllegalStateException("Unable to replace the existing driver bundle")
            if (!tempDir.renameTo(finalDir)) {
                if (backupDir.exists()) backupDir.renameTo(finalDir)
                throw IllegalStateException("Unable to install the driver bundle atomically")
            }
            if (backupDir.exists()) backupDir.deleteRecursively()

            prefs.edit().putString("turnip_bundle", finalDir.absolutePath).commit()
            Log.i("VulkanScope", "Imported AdrenoTools driver bundle: ${metadataFile.name}, library: ${library.name}")

            
            
            
            requestDriverModeChange(DriverMode.TURNIP)
        } catch (e: Exception) {
            tempDir.deleteRecursively()
            Log.e("VulkanScope", "Turnip bundle installation failed", e)
            android.widget.Toast.makeText(
                this,
                "Driver import failed: ${e.message ?: "The selected ZIP could not be installed."}",
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun findTurnipBundle(): String? {
        if (driverMode != DriverMode.TURNIP) return null
        val root = File(filesDir, "turnip")
        return if (root.exists()) root.absolutePath else null
    }

    private fun findTurnipIcd(): String? {
        if (driverMode != DriverMode.TURNIP) return null
        val root = File(filesDir, "turnip")
        val json = root.walkTopDown().firstOrNull { it.isFile && it.extension.equals("json", true) }
        val declared = json?.let { runCatching { JSONObject(it.readText()).optString("libraryName") }.getOrNull() }.orEmpty()
        if (declared.isNotBlank()) {
            root.walkTopDown().firstOrNull { it.isFile && it.name == declared }?.let { return it.absolutePath }
        }
        return root.walkTopDown().firstOrNull { it.isFile && it.extension.equals("so", true) }?.absolutePath
    }

    private fun isTurnipPlatformEligible(): Boolean =
        Build.VERSION.SDK_INT >= 28 && Build.SUPPORTED_ABIS.contains("arm64-v8a")

    private fun updateTurnipSupport(report: VulkanReport) {
        if (!isTurnipPlatformEligible()) {
            turnipSupport = TurnipSupport.UNSUPPORTED
            return
        }
        if (report.error != null && report.devices.isEmpty()) {
            turnipSupport = TurnipSupport.UNKNOWN
            return
        }
        val hasQualcommAdreno = report.devices.any {
            it.vendorIdRaw == 0x5143L && it.name.contains("Adreno", ignoreCase = true)
        }
        turnipSupport = if (hasQualcommAdreno) TurnipSupport.SUPPORTED else TurnipSupport.UNSUPPORTED
    }

    private fun beginCollectionTask(taskId: String) {
        collectionCompletionJob?.cancel()
        collectionCompletionJob = null
        pendingCollectionTasks.add(taskId)
        collectionStatus = CollectionStatus.COLLECTING
    }

    private fun completeCollectionTask(taskId: String) {
        pendingCollectionTasks.remove(taskId)
        if (pendingCollectionTasks.isEmpty() && !collectionInFlight) {
            finishCollectionStatusSoon()
        }
    }

    private fun requestReportCollection() {
        if (isFinishing || isDestroyed) return
        if (collectionInFlight) {
            collectionPending = true
            return
        }
        collectionGeneration += 1L
        val generation = collectionGeneration
        requestedQueryGroups.clear()
        pendingCollectionTasks.clear()
        pendingCollectionTasks.add("$generation:base")
        pendingCollectionTasks.add("$generation:enrichment")
        backgroundQueryGroups.forEach { pendingCollectionTasks.add("$generation:group:$it") }
        collectionInFlight = true
        reportLoading = true
        collectionStatus = CollectionStatus.COLLECTING
        activityScope.launch {
            try {
                val base = runCatching { collectReport() }.getOrElse { e ->
                    Log.e("VulkanScope", "Vulkan base orchestration failed", e)
                    VulkanReport("Unknown", emptyList(), emptyList(), emptyList(), "Vulkan base report unavailable: ${e.message ?: "probe failed"}")
                }
                latestReport = base
                updateTurnipSupport(base)
                reportLoading = false
                completeCollectionTask("$generation:base")
                val enriched = runCatching { enrichReport(base) }.getOrElse { e ->
                    Log.e("VulkanScope", "Vulkan advanced enrichment failed; preserving base report", e)
                    base
                }
                latestReport = enriched
                completeCollectionTask("$generation:enrichment")
                startBackgroundInformationCollection(generation)
            } finally {
                collectionInFlight = false
                if (pendingCollectionTasks.isEmpty()) finishCollectionStatusSoon()
                if (collectionPending && !isFinishing && !isDestroyed && pendingCollectionTasks.isEmpty()) {
                    collectionPending = false
                    requestReportCollection()
                }
            }
        }
    }

    private fun requestSurfaceRefresh(surface: Surface) {
        if (isFinishing || isDestroyed) return
        if (collectionInFlight || latestReport == null) return
        val taskId = "surface:${System.identityHashCode(surface)}:${System.nanoTime()}"
        beginCollectionTask(taskId)
        activityScope.launch {
            try {
                val base = latestReport ?: return@launch
                runCatching {
                    val rawSurface = runSurfaceProbe(surface)
                    latestReport = mergeSurfaceProbeReport(base, rawSurface)
                }.onFailure { e ->
                    Log.e("VulkanScope", "Surface refresh failed; preserving existing Vulkan report", e)
                }
            } finally {
                completeCollectionTask(taskId)
            }
        }
    }

    private suspend fun startBackgroundInformationCollection(generation: Long = collectionGeneration) {
        val report = latestReport
        if (report == null || report.devices.isEmpty()) {
            backgroundQueryGroups.forEach { completeCollectionTask("$generation:group:$it") }
            return
        }

        val newGroups = backgroundQueryGroups.filter { requestedQueryGroups.add(it) }
        backgroundQueryGroups.filterNot { newGroups.contains(it) }.forEach { completeCollectionTask("$generation:group:$it") }
        kotlinx.coroutines.coroutineScope {
            val jobs = newGroups.map { group ->
                launch(Dispatchers.IO) {
                    val taskId = "$generation:group:$group"
                    try {
                        val current = latestReport ?: return@launch
                        val extensionName = ISOLATED_EXTENSION_GROUPS[group]
                        if (extensionName != null && current.devices.none { device -> device.extensions.any { it.name == extensionName } }) return@launch
                        val raw = runCatching { runIsolatedProbe(group) }.getOrElse { e ->
                            Log.e("VulkanScope", "Vulkan background query failed: $group", e)
                            ""
                        }
                        if (raw.isNotBlank()) {
                            withContext(Dispatchers.Main.immediate) {
                                val currentReport = latestReport ?: current
                                latestReport = if (extensionName != null) {
                                    mergeExtensionGroupReport(currentReport, raw, group, extensionName)
                                } else {
                                    val label = ISOLATED_ADVANCED_GROUPS[group] ?: ISOLATED_CORE_GROUPS[group] ?: group
                                    mergeAdvancedQueryReport(currentReport, raw, group, label)
                                }
                            }
                        }
                    } finally {
                        withContext(Dispatchers.Main.immediate) {
                            completeCollectionTask(taskId)
                        }
                    }
                }
            }
            jobs.forEach { it.join() }
        }
    }

    private fun finishCollectionStatusSoon(delayMillis: Long = 2000L) {
        if (collectionInFlight || pendingCollectionTasks.isNotEmpty()) return
        collectionCompletionJob?.cancel()
        collectionStatus = CollectionStatus.COMPLETED
        collectionCompletionJob = activityScope.launch {
            delay(delayMillis)
            if (!collectionInFlight && pendingCollectionTasks.isEmpty()) {
                collectionStatus = CollectionStatus.IDLE
            }
        }
    }

    private fun requestDriverModeChange(mode: DriverMode) {

        if (mode == DriverMode.TURNIP && turnipSupport != TurnipSupport.SUPPORTED) return
        if (mode == DriverMode.TURNIP && findInstalledTurnipLibrary() == null) {
            openDriverBundlePicker()
            return
        }
        if (mode == driverMode) return
        requestedQueryGroups.clear()
        driverMode = mode
        prefs.edit().putString("driver_mode", mode.name).apply()
        if (synchronized(surfaceLock) { currentSurface?.isValid == true }) requestReportCollection()
    }

    private fun findInstalledTurnipLibrary(): File? {
        if (turnipSupport != TurnipSupport.SUPPORTED) return null
        val root = File(filesDir, "turnip")
        if (!root.exists()) return null
        val json = root.walkTopDown().firstOrNull { it.isFile && it.extension.equals("json", true) }
        val declared = json?.let { runCatching { JSONObject(it.readText()).optString("libraryName") }.getOrNull() }.orEmpty()
        if (declared.isNotBlank()) root.walkTopDown().firstOrNull { it.isFile && it.name == declared }?.let { return it }
        return root.walkTopDown().firstOrNull { it.isFile && it.extension.equals("so", true) && it.length() > 0L }
    }

    @Suppress("DEPRECATION")
    private fun displayReport(): DisplayReport {
        val display = getSystemService(DisplayManager::class.java).getDisplay(android.view.Display.DEFAULT_DISPLAY)
            ?: return DisplayReport("Unknown", "Unknown", false, "Not exposed", emptyList(), "Not exposed", "Not exposed", "Not exposed", emptyList())
        val mode = display.mode
        val hdr = display.hdrCapabilities
        val hdrTypes = if (android.os.Build.VERSION.SDK_INT >= 34) mode.supportedHdrTypes else hdr.supportedHdrTypes
        val hdrNames = hdrTypes.map { hdrTypeName(it) }
        val modes = display.supportedModes.map { "${it.physicalWidth} × ${it.physicalHeight} · ${formatHz(it.refreshRate)}" }.distinct().sorted()
        val wideGamut = android.os.Build.VERSION.SDK_INT >= 26 && display.isWideColorGamut
        val preferred = if (android.os.Build.VERSION.SDK_INT >= 29) display.preferredWideGamutColorSpace?.name ?: "Not exposed" else "Not exposed"
        return DisplayReport(
            "${mode.physicalWidth} × ${mode.physicalHeight}",
            formatHz(mode.refreshRate),
            wideGamut,
            preferred,
            hdrNames,
            formatLuminance(hdr.desiredMinLuminance),
            formatLuminance(hdr.desiredMaxLuminance),
            formatLuminance(hdr.desiredMaxAverageLuminance),
            modes
        )
    }

    private suspend fun collectReport(): VulkanReport = withContext(Dispatchers.IO) {
        collectMutex.withLock {
            collectBaseReport()
        }
    }

    private suspend fun collectBaseReport(): VulkanReport {
        var lastFailure: String? = null
        repeat(2) { attempt ->
            val raw = runCatching { runProbe(null) }.getOrElse { e ->
                Log.e("VulkanScope", "Vulkan base probe failed (attempt=${attempt + 1})", e)
                lastFailure = e.message ?: "probe failed"
                return@repeat
            }
            Log.i("VulkanScope", "Vulkan base probe result bytes=${raw.length} attempt=${attempt + 1}")
            val parsed = runCatching { parseReport(raw) }.getOrElse { e ->
                Log.e("VulkanScope", "Vulkan base JSON parse failed: length=${raw.length} attempt=${attempt + 1}", e)
                lastFailure = e.message ?: "invalid probe result"
                return@repeat
            }
            if (hasCompleteBaseCoverage(parsed)) return parsed
            lastFailure = "Base probe returned an incomplete core Vulkan dataset."
            Log.w(
                "VulkanScope",
                "Rejecting incomplete base report attempt=${attempt + 1}: " +
                    parsed.devices.joinToString { d ->
                        "${d.name}: features=${d.features.size}, queues=${d.queues.size}, heaps=${d.heaps.size}, memoryTypes=${d.memoryTypes.size}, limits=${d.limits.size}, extensions=${d.extensions.size}"
                    }
            )
        }
        return VulkanReport(
            "Unknown",
            emptyList(),
            emptyList(),
            emptyList(),
            "Vulkan base report was incomplete after a retry: ${lastFailure ?: "incomplete core dataset"}"
        )
    }

    private fun hasCompleteBaseCoverage(report: VulkanReport): Boolean =
        report.error == null &&
            report.devices.isNotEmpty() &&
            report.devices.all { device ->
                device.features.size >= 55 &&
                    device.queues.isNotEmpty() &&
                    device.heaps.isNotEmpty() &&
                    device.memoryTypes.isNotEmpty() &&
                    device.limits.isNotEmpty()
            }

    private suspend fun enrichReport(base: VulkanReport): VulkanReport = withContext(Dispatchers.IO) {
        var enriched = base.copy(devices = base.devices.map { device ->
            if (apiAtLeast(device.apiVersion, 1, 1)) {
                device.copy(
                    extendedQueryStatus = if (device.extendedQueryStatus == "unavailable") "unavailable" else "available",
                    extendedQueryReason = if (device.extendedQueryStatus == "unavailable") device.extendedQueryReason else "Core Vulkan 1.1+ feature/property data was queried directly from the active base Vulkan instance."
                )
            } else {
                device.copy(extendedQueryStatus = "not_applicable", extendedQueryReason = "The device API version is below Vulkan 1.1.")
            }
        })

        runCatching {
            val rawMetadata = runIsolatedProbe("metadata")
            enriched = mergeMetadataReport(enriched, rawMetadata)
        }.onFailure { e ->
            Log.e("VulkanScope", "Vulkan metadata query failed; preserving base report", e)
        }

        if (enriched.devices.isEmpty()) return@withContext enriched

        val surface = synchronized(surfaceLock) { currentSurface?.takeIf { it.isValid } }
        if (surface != null && enriched.devices.isNotEmpty()) {
            runCatching {
                val rawSurface = runSurfaceProbe(surface)
                enriched = mergeSurfaceProbeReport(enriched, rawSurface)
            }.onFailure { e ->
                Log.e("VulkanScope", "Vulkan surface probe failed without discarding base report", e)
            }
        }

        enriched
    }

    private suspend fun runIsolatedProbe(group: String): String =
        runServiceProbe(group, null, if (group in ISOLATED_ADVANCED_GROUPS.keys) 30_000L else 12_000L)

    private fun requestQueryGroup(group: String) {
        if (latestReport?.devices.isNullOrEmpty()) return
        if (!requestedQueryGroups.add(group)) return
        val taskId = "ad-hoc:$group:${System.nanoTime()}"
        beginCollectionTask(taskId)
        activityScope.launch {
            try {
                val current = latestReport ?: return@launch
                val label = ISOLATED_ADVANCED_GROUPS[group] ?: ISOLATED_CORE_GROUPS[group]
                val extensionName = ISOLATED_EXTENSION_GROUPS[group]
                if (extensionName != null && current.devices.none { device -> device.extensions.any { it.name == extensionName } }) return@launch
                val raw = runCatching { runIsolatedProbe(group) }.getOrElse { e ->
                    Log.e("VulkanScope", "Vulkan lazy query failed: $group", e)
                    ""
                }
                if (raw.isBlank()) return@launch
                withContext(Dispatchers.Main.immediate) {
                    val currentReport = latestReport ?: current
                    latestReport = if (extensionName != null) {
                        mergeExtensionGroupReport(currentReport, raw, group, extensionName)
                    } else if (label != null) {
                        mergeAdvancedQueryReport(currentReport, raw, group, label)
                    } else {
                        currentReport
                    }
                }
            } finally {
                completeCollectionTask(taskId)
            }
        }
    }

    private fun requestPageQueries(page: Page) {
        val groups = when (page) {
            Page.Features -> ISOLATED_CORE_GROUPS.keys.toList()
            Page.Formats -> listOf("format2", "imageFormat2")
            Page.Memory -> listOf("memory2", "external", "sparse")
            Page.Queues -> listOf("queue2", "videoCapabilities")
            Page.Properties -> listOf("core11", "core12", "core13", "tools", "groups")
            Page.Extensions -> ISOLATED_EXTENSION_GROUPS.keys.toList()
            else -> emptyList()
        }
        groups.forEach(::requestQueryGroup)
    }

    private suspend fun runProbe(surface: Surface?): String = runServiceProbe("base", surface, 45_000L)

    private suspend fun runSurfaceProbe(surface: Surface): String = runServiceProbe("surface", surface, 25_000L)

    private fun stopVulkanProbeProcess() {
        runCatching { stopService(Intent(this@MainActivity, VulkanProbeService::class.java)) }
        runCatching {
            val activityManager = getSystemService(ActivityManager::class.java) ?: return
            val expectedName = "${packageName}:vulkan_probe"
            activityManager.runningAppProcesses.orEmpty()
                .filter { it.uid == Process.myUid() && it.processName == expectedName }
                .forEach { process -> Process.killProcess(process.pid) }
        }.onFailure { error ->
            Log.w("VulkanScope", "Unable to stop the isolated Vulkan probe process", error)
        }
    }

    private suspend fun runServiceProbe(group: String, surface: Surface?, timeoutMs: Long): String {
        val maxProbeResultBytes = 64L * 1024L * 1024L
        val resultFile = File(cacheDir, "vulkan_probe_${java.util.UUID.randomUUID()}.json")
        val crashMarkerFile = File(resultFile.absolutePath + ".crash")
        resultFile.delete()
        crashMarkerFile.delete()
        val intent = Intent(this@MainActivity, VulkanProbeService::class.java)
            .putExtra(VulkanProbeService.EXTRA_QUERY_GROUP, group)
            .putExtra(VulkanProbeService.EXTRA_DRIVER_MODE, driverMode.name)
            .putExtra(VulkanProbeService.EXTRA_DRIVER_ICD, findTurnipIcd())
            .putExtra(VulkanProbeService.EXTRA_DRIVER_BUNDLE, findTurnipBundle())
            .putExtra(VulkanProbeService.EXTRA_HOOK_LIB_DIR, applicationInfo.nativeLibraryDir)
            .putExtra(VulkanProbeService.EXTRA_RESULT_PATH, resultFile.absolutePath)
        if (surface != null) intent.putExtra(VulkanProbeService.EXTRA_SURFACE, surface)
        val started = runCatching {
            withContext(Dispatchers.Main.immediate) { startService(intent) }
        }.isSuccess
        if (!started) {
            resultFile.delete()
            return if (group == "base") {
                "{\"status\":\"unavailable\",\"reason\":\"Unable to start the isolated Vulkan probe process\",\"devices\":[]}"
            } else {
                "{\"status\":\"unavailable\",\"group\":${JSONObject.quote(group)},\"reason\":\"Unable to start the isolated Vulkan query process\",\"devices\":[]}"
            }
        }
        val result = withContext(Dispatchers.IO) {
            var value: String? = null
            var partialCandidate: String? = null
            try {
                withTimeout(timeoutMs) {
                    while (value == null) {
                        if (resultFile.isFile) {
                            val resultLength = resultFile.length()
                            if (resultLength > maxProbeResultBytes) {
                                value = if (group == "base") {
                                    "{\"status\":\"unavailable\",\"reason\":\"The Vulkan probe result exceeded the safety size limit.\",\"devices\":[]}"
                                } else {
                                    "{\"status\":\"unavailable\",\"group\":${JSONObject.quote(group)},\"reason\":\"The Vulkan query result exceeded the safety size limit.\",\"devices\":[]}"
                                }
                                stopVulkanProbeProcess()
                                continue
                            }
                            if (resultLength > 0L) {
                                val candidate = runCatching { resultFile.readText() }.getOrElse { error ->
                                    if (group == "base") {
                                        "{\"status\":\"unavailable\",\"reason\":${JSONObject.quote(error.message ?: "Unable to read Vulkan probe result")},\"devices\":[]}"
                                    } else {
                                        "{\"status\":\"unavailable\",\"group\":${JSONObject.quote(group)},\"reason\":${JSONObject.quote(error.message ?: "Unable to read Vulkan query result")},\"devices\":[]}"
                                    }
                                }
                                if (group != "base") {
                                    value = candidate
                                } else {
                                    val parsedCandidate = runCatching { JSONObject(candidate) }.getOrNull()
                                    if (parsedCandidate != null) {
                                        partialCandidate = candidate
                                        val complete = parsedCandidate.optBoolean("baseReportComplete", false)
                                        val ready = parsedCandidate.optBoolean("baseReportReady", false)
                                        val status = parsedCandidate.optString("status", "unavailable")
                                        if (complete || ready || status == "unavailable" || crashMarkerFile.isFile) value = candidate
                                    } else {
                                        Log.w("VulkanScope", "Ignoring invalid intermediate base probe checkpoint bytes=${candidate.length}")
                                        if (crashMarkerFile.isFile && partialCandidate != null) value = partialCandidate
                                    }
                                }
                            }
                        } else {
                            kotlinx.coroutines.delay(60L)
                        }
                    }
                }
            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                stopVulkanProbeProcess()
                value = partialCandidate ?: if (group == "base") {
                    "{\"status\":\"unavailable\",\"reason\":\"The isolated Vulkan probe did not complete within the timeout.\",\"devices\":[]}"
                } else {
                    "{\"status\":\"unavailable\",\"group\":${JSONObject.quote(group)},\"reason\":\"The isolated $group query did not complete within the timeout.\",\"devices\":[]}"
                }
            } finally {
                resultFile.delete()
                crashMarkerFile.delete()
            }
            value ?: if (group == "base") {
                "{\"status\":\"unavailable\",\"reason\":\"The isolated Vulkan probe returned no result.\",\"devices\":[]}"
            } else {
                "{\"status\":\"unavailable\",\"group\":${JSONObject.quote(group)},\"reason\":\"The isolated $group query returned no result.\",\"devices\":[]}"
            }
        }
        return result
    }

}

private fun hdrTypeName(type: Int): String = when (type) {
    1 -> "Dolby Vision"
    2 -> "HDR10"
    3 -> "HLG"
    4 -> "HDR10+"
    5 -> "HLG+"
    else -> "Unknown ($type)"
}

private fun formatHz(value: Float): String = String.format(java.util.Locale.US, "%.2f Hz", value)
private fun formatLuminance(value: Float): String = if (value <= 0f || value.isNaN()) "Not exposed" else String.format(java.util.Locale.US, "%.3f cd/m²", value)

private fun apiAtLeast(value: String, major: Int, minor: Int): Boolean {
    val parts = value.trim().split('.')
    val parsedMajor = parts.getOrNull(0)?.toIntOrNull() ?: return false
    val parsedMinor = parts.getOrNull(1)?.toIntOrNull() ?: return false
    return parsedMajor > major || (parsedMajor == major && parsedMinor >= minor)
}

private fun mergeMetadataReport(base: VulkanReport, raw: String): VulkanReport {
    val root = runCatching { JSONObject(raw) }.getOrElse {
        return base
    }
    if (root.optString("status", "unavailable") != "available") return base
    val registry = root.optJSONObject("registryCoverage")
    val parsedCoverage = if (registry != null) {
        val structs = mutableListOf<String>()
        val structArray = registry.optJSONArray("implementedPhysicalDeviceStructs") ?: JSONArray()
        for (i in 0 until structArray.length()) structs += structArray.optString(i)
        val groups = mutableListOf<String>()
        val groupArray = registry.optJSONArray("validatedRuntimeQueryGroups") ?: JSONArray()
        for (i in 0 until groupArray.length()) groups += groupArray.optString(i)
        RegistryCoverage(
            baseline = registry.optString("baseline", base.registryCoverage.baseline),
            mode = registry.optString("mode", base.registryCoverage.mode),
            implementedPhysicalDeviceStructCount = registry.optInt("implementedPhysicalDeviceStructCount", base.registryCoverage.implementedPhysicalDeviceStructCount),
            validatedRuntimeQueryGroupCount = registry.optInt("validatedRuntimeQueryGroupCount", base.registryCoverage.validatedRuntimeQueryGroupCount),
            runtimeExtensionTokenCount = registry.optInt("runtimeExtensionTokenCount", base.registryCoverage.runtimeExtensionTokenCount),
            catalogSchemaVersion = registry.optInt("catalogSchemaVersion", base.registryCoverage.catalogSchemaVersion),
            reportSchema = registry.optString("reportSchema", base.registryCoverage.reportSchema),
            headerBaseline = registry.optString("headerBaseline", base.registryCoverage.headerBaseline),
            instanceDependencyCandidateCount = registry.optInt("instanceDependencyCandidateCount", base.registryCoverage.instanceDependencyCandidateCount),
            implementedPhysicalDeviceStructs = structs,
            validatedRuntimeQueryGroups = groups
        )
    } else base.registryCoverage
    val instanceExtensions = parseExtensions(root.optJSONArray("instanceExtensions"), "Instance")
    val instanceLayers = mutableListOf<LayerEntry>()
    val layerArray = root.optJSONArray("instanceLayers") ?: JSONArray()
    for (i in 0 until layerArray.length()) {
        val layer = layerArray.optJSONObject(i) ?: continue
        val layerExtensions = parseExtensions(layer.optJSONArray("extensions"), "Instance layer")
        instanceLayers += LayerEntry(layer.optString("name"), layer.optString("description"), layer.optInt("specVersion"), layer.optInt("implementationVersion"), layerExtensions)
    }
    return base.copy(
        instanceExtensions = if (instanceExtensions.isNotEmpty()) instanceExtensions else base.instanceExtensions,
        instanceLayers = if (root.optBoolean("instanceLayersComplete", false)) instanceLayers else base.instanceLayers,
        registryCoverage = if (root.optBoolean("registryCoverageComplete", false)) parsedCoverage else base.registryCoverage
    )
}

private fun sanitizeReportLabel(value: String): String = value.removePrefix("CapsViewer 4.12 parity · ")
private fun sanitizeReportSection(section: String): String = sanitizeReportLabel(section)

private fun mergeQueryProperties(existing: List<PropertyEntry>, incoming: List<PropertyEntry>): List<PropertyEntry> =
    (existing + incoming).distinctBy { Triple(it.section, it.name, it.value) }

private fun replaceQueryStatus(properties: List<PropertyEntry>, name: String, value: String): List<PropertyEntry> =
    properties.filterNot { it.section == "Vulkan Query Status" && it.name == name } +
        PropertyEntry("Vulkan Query Status", name, value)

private fun mergeSurfaceProbeReport(base: VulkanReport, raw: String): VulkanReport {
    val root = runCatching { JSONObject(raw) }.getOrElse {
        return base.copy(devices = base.devices.map { device ->
            device.copy(detailedProperties = replaceQueryStatus(device.detailedProperties, "Surface probe", "Unavailable: isolated surface probe returned invalid data."))
        })
    }
    val status = root.optString("status", "unavailable")
    val reason = root.optString("reason", "The isolated surface probe did not return a result.")
    val resultDevices = root.optJSONArray("devices") ?: JSONArray()
    return base.copy(devices = base.devices.map { device ->
        val deviceId = device.deviceId.removePrefix("0x").toLongOrNull(16)
        val match = (0 until resultDevices.length()).asSequence()
            .mapNotNull { resultDevices.optJSONObject(it) }
            .firstOrNull { it.optLong("vendorId", -1L) == device.vendorIdRaw && it.optLong("deviceId", -1L) == deviceId }
        if (status != "available" || match == null) {
            device.copy(
                surfaceAvailable = false,
                surfacePresentationSupported = false,
                surfaceColorSpaceExtensionAvailable = root.optBoolean("surfaceColorSpaceExtensionAvailable", false),
                surfaceColorSpaceExtensionEnabled = false,
                surfaceFormatQueryResult = -1,
                surfaceFormatQueryResultSecond = -1,
                surfaceFormatQuerySecondAttempted = false,
                surfaceFormatQuerySafetyRejected = false,
                surfaceCapabilities = emptyList(),
                surfaceFormats = emptyList(),
                presentModes = emptyList(),
                presentationQueues = emptyList(),
                detailedProperties = replaceQueryStatus(device.detailedProperties, "Surface probe", "Unavailable: $reason")
            )
        } else {
            val surface = match.optJSONObject("surface")
            val surfaceFormats = mutableListOf<SurfaceFormatEntry>()
            val formatArray = surface?.optJSONArray("formats") ?: JSONArray()
            for (i in 0 until formatArray.length()) {
                val sf = formatArray.optJSONObject(i) ?: continue
                surfaceFormats += SurfaceFormatEntry(sf.optString("format"), sf.optString("colorSpace"), sf.optString("class"), sf.optString("description"), true)
            }
            val capabilities = mutableListOf<Pair<String, String>>()
            listOf("minImageCount", "maxImageCount", "currentExtent", "minExtent", "maxExtent", "maxImageArrayLayers", "supportedTransforms", "currentTransform", "supportedCompositeAlpha", "supportedUsageFlags", "capabilityResult").forEach { key ->
                if (surface?.has(key) == true) capabilities += key to surface.optString(key)
            }
            val presentModes = mutableListOf<String>()
            val presentArray = surface?.optJSONArray("presentModes") ?: JSONArray()
            for (i in 0 until presentArray.length()) presentModes += presentArray.optString(i)
            val presentationQueues = mutableListOf<Pair<Int, Boolean>>()
            val queueArray = surface?.optJSONArray("queuePresentation") ?: JSONArray()
            for (i in 0 until queueArray.length()) {
                val q = queueArray.optJSONObject(i) ?: continue
                presentationQueues += q.optInt("queueFamily") to q.optBoolean("supported")
            }
            device.copy(
                surfaceAvailable = surface?.optBoolean("available") == true,
                surfacePresentationSupported = surface?.optBoolean("presentationSupported") == true,
                surfaceColorSpaceExtensionAvailable = root.optBoolean("surfaceColorSpaceExtensionAvailable", false),
                surfaceColorSpaceExtensionEnabled = root.optBoolean("surfaceColorSpaceExtensionEnabled", false),
                surfaceFormatQueryResult = surface?.optInt("formatQueryResult", -1) ?: -1,
                surfaceFormatQueryResultSecond = surface?.optInt("formatQueryResultSecond", -1) ?: -1,
                surfaceFormatQuerySecondAttempted = surface?.optBoolean("formatQuerySecondAttempted", false) ?: false,
                surfaceFormatQuerySafetyRejected = surface?.optBoolean("formatQuerySafetyRejected", false) ?: false,
                surfaceCapabilities = capabilities,
                surfaceFormats = surfaceFormats,
                presentModes = presentModes,
                presentationQueues = presentationQueues,
                detailedProperties = replaceQueryStatus(device.detailedProperties, "Surface probe", "Available")
            )
        }
    })
}

private fun mergeAdvancedQueryReport(base: VulkanReport, raw: String, group: String, label: String): VulkanReport {
    val root = runCatching { JSONObject(raw) }.getOrElse {
        return base.copy(devices = base.devices.map { device ->
            device.copy(detailedProperties = replaceQueryStatus(device.detailedProperties, "$label query", "Unavailable: isolated query returned invalid data."))
        })
    }
    val status = root.optString("status", "unavailable")
    val reason = root.optString("reason", "The isolated advanced query did not return a result.")
    val resultDevices = root.optJSONArray("devices") ?: JSONArray()
    val parsed = mutableListOf<Triple<Long, Long, Pair<List<FeatureEntry>, List<PropertyEntry>>>>()
    val core14Status = mutableMapOf<Pair<Long, Long>, Pair<String, String>>()
    for (i in 0 until resultDevices.length()) {
        val item = resultDevices.optJSONObject(i) ?: continue
        val vendor = item.optLong("vendorId", -1L)
        val deviceId = item.optLong("deviceId", -1L)
        val features = mutableListOf<FeatureEntry>()
        val featureArray = item.optJSONArray("features") ?: JSONArray()
        for (j in 0 until featureArray.length()) {
            val feature = featureArray.optJSONObject(j) ?: continue
            features += FeatureEntry(sanitizeReportLabel(feature.optString("name")), feature.optBoolean("supported"))
        }
        val properties = mutableListOf<PropertyEntry>()
        val pa = item.optJSONArray("properties") ?: JSONArray()
        for (j in 0 until pa.length()) {
            val prop = pa.optJSONObject(j) ?: continue
            properties += PropertyEntry(sanitizeReportSection(prop.optString("section")), prop.optString("name"), prop.optString("value"))
        }
        parsed += Triple(vendor, deviceId, features to properties)
        if (group == "core14") {
            core14Status[vendor to deviceId] = item.optString("status", "available") to item.optString("reason", "")
        }
    }
    val formatEntriesByDevice: Map<Pair<Long, Long>, List<FormatEntry>> = if (group == "format2") {
        val valuesByDevice = mutableMapOf<Pair<Long, Long>, List<FormatEntry>>()
        for (i in 0 until resultDevices.length()) {
            val item = resultDevices.optJSONObject(i) ?: continue
            val vendor = item.optLong("vendorId", -1L)
            val deviceId = item.optLong("deviceId", -1L)
            val properties = item.optJSONArray("properties") ?: JSONArray()
            val entries = (0 until properties.length()).mapNotNull { index ->
                val prop = properties.optJSONObject(index) ?: return@mapNotNull null
                val name = prop.optString("name")
                val value = prop.optString("value")
                val linear = Regex("(?:^|, )linear=0x([0-9a-fA-F]+)").find(value)?.groupValues?.getOrNull(1)?.toLongOrNull(16) ?: return@mapNotNull null
                val optimal = Regex("(?:^|, )optimal=0x([0-9a-fA-F]+)").find(value)?.groupValues?.getOrNull(1)?.toLongOrNull(16) ?: return@mapNotNull null
                val buffer = Regex("(?:^|, )buffer=0x([0-9a-fA-F]+)").find(value)?.groupValues?.getOrNull(1)?.toLongOrNull(16) ?: return@mapNotNull null
                FormatEntry(name, linear != 0L || optimal != 0L || buffer != 0L, linear, optimal, buffer)
            }
            valuesByDevice[vendor to deviceId] = entries
        }
        valuesByDevice
    } else {
        emptyMap()
    }
    val videoQueuesByDevice: List<Triple<Long, Long, Map<Int, Long>>> = if (group == "queue2") {
        val valuesByDevice = mutableListOf<Triple<Long, Long, Map<Int, Long>>>()
        for (i in 0 until resultDevices.length()) {
            val item = resultDevices.optJSONObject(i) ?: continue
            val vendor = item.optLong("vendorId", -1L)
            val deviceId = item.optLong("deviceId", -1L)
            val values = mutableMapOf<Int, Long>()
            val vq = item.optJSONArray("videoQueues") ?: JSONArray()
            for (j in 0 until vq.length()) {
                val v = vq.optJSONObject(j) ?: continue
                values[v.optInt("index")] = v.optLong("videoCodecOperations")
            }
            valuesByDevice += Triple(vendor, deviceId, values)
        }
        valuesByDevice
    } else {
        emptyList()
    }
    return base.copy(devices = base.devices.map { device ->
        val deviceId = device.deviceId.removePrefix("0x").toLongOrNull(16)
        val match = parsed.firstOrNull { it.first == device.vendorIdRaw && it.second == deviceId }
        val videoMatch = videoQueuesByDevice.firstOrNull { it.first == device.vendorIdRaw && it.second == deviceId }
        val formatMatch = formatEntriesByDevice[device.vendorIdRaw to (deviceId ?: -1L)]
        val mergedQueues = if (videoMatch != null) device.queues.map { q -> q.copy(videoCodecOperations = videoMatch.third[q.index] ?: q.videoCodecOperations) } else device.queues
        val mergedFormats = if (formatMatch != null) {
            val byName = LinkedHashMap<String, FormatEntry>()
            device.formats.forEach { byName[it.name] = it }
            formatMatch.forEach { byName[it.name] = it }
            byName.values.toList()
        } else {
            device.formats
        }
        val mergedFeatures = if (match != null) device.features + match.third.first.filterNot { incoming -> device.features.any { it.name == incoming.name } } else device.features
        val mergedProperties = if (match != null) mergeQueryProperties(device.detailedProperties, match.third.second) else device.detailedProperties
        val statusProperty = if (status == "available" && match != null) {
            PropertyEntry("Vulkan Query Status", "$label query", "Available")
        } else if (status == "not_applicable") {
            PropertyEntry("Vulkan Query Status", "$label query", "Not applicable: ${reason.ifBlank { "the query does not apply to this device." }}")
        } else {
            PropertyEntry("Vulkan Query Status", "$label query", "Unavailable: ${reason.ifBlank { "the isolated query did not complete." }}")
        }
        var merged = device.copy(features = mergedFeatures, detailedProperties = replaceQueryStatus(mergedProperties, "$label query", statusProperty.value), queues = mergedQueues, formats = mergedFormats)
        if (group == "core14") {
            val coreStatus = core14Status[device.vendorIdRaw to (deviceId ?: -1L)]
            if (coreStatus != null) {
                merged = merged.copy(vulkan14Status = coreStatus.first, vulkan14Reason = coreStatus.second)
            }
        }
        merged
    })
}

private fun mergeExtensionGroupReport(base: VulkanReport, raw: String, group: String, extensionName: String): VulkanReport {
    val root = runCatching { JSONObject(raw) }.getOrElse {
        return base.copy(devices = base.devices.map { device ->
            if (device.extensions.any { it.name == extensionName }) {
                device.copy(detailedProperties = replaceQueryStatus(device.detailedProperties, "$extensionName query", "Unavailable: the isolated $group probe returned invalid data."))
            } else device
        })
    }
    val status = root.optString("status", "unavailable")
    val reason = root.optString("reason", "The isolated extension query did not return a result.")
    val resultDevices = root.optJSONArray("devices") ?: JSONArray()
    val parsed = mutableListOf<Triple<Long, Long, Pair<List<FeatureEntry>, List<PropertyEntry>>>>()
    for (i in 0 until resultDevices.length()) {
        val item = resultDevices.optJSONObject(i) ?: continue
        val vendor = item.optLong("vendorId", -1L)
        val deviceId = item.optLong("deviceId", -1L)
        val features = mutableListOf<FeatureEntry>()
        val featureArray = item.optJSONArray("features") ?: JSONArray()
        for (j in 0 until featureArray.length()) {
            val f = featureArray.optJSONObject(j) ?: continue
            features += FeatureEntry(sanitizeReportLabel(f.optString("name")), f.optBoolean("supported"))
        }
        val properties = mutableListOf<PropertyEntry>()
        val propertyArray = item.optJSONArray("properties") ?: JSONArray()
        for (j in 0 until propertyArray.length()) {
            val prop = propertyArray.optJSONObject(j) ?: continue
            properties += PropertyEntry(sanitizeReportSection(prop.optString("section")), prop.optString("name"), prop.optString("value"))
        }
        parsed += Triple(vendor, deviceId, features to properties)
    }
    return base.copy(devices = base.devices.map { device ->
        if (!device.extensions.any { it.name == extensionName }) device
        else {
            val deviceId = device.deviceId.removePrefix("0x").toLongOrNull(16)
            val match = parsed.firstOrNull { it.first == device.vendorIdRaw && it.second == deviceId }
            when {
                status == "available" && match != null -> {
                    val mergedFeatures = device.features + match.third.first.filterNot { incoming -> device.features.any { existing -> existing.name == incoming.name } }
                    val mergedProperties = mergeQueryProperties(device.detailedProperties, match.third.second)
                    device.copy(
                        features = mergedFeatures,
                        detailedProperties = replaceQueryStatus(mergedProperties, "$extensionName query", "Available")
                    )
                }
                status == "not_applicable" -> device.copy(
                    detailedProperties = replaceQueryStatus(device.detailedProperties, "$extensionName query", "Not applicable: the extension was not enumerated by the isolated probe.")
                )
                else -> device.copy(
                    detailedProperties = replaceQueryStatus(device.detailedProperties, "$extensionName query", "Unavailable: ${reason.ifBlank { "the isolated extension query did not complete." }}")
                )
            }
        }
    })
}

private fun parseReport(raw: String): VulkanReport {
    val root = JSONObject(raw)
    val error = if (root.has("error") && !root.isNull("error")) root.optString("error") else null
    val nativeStatus = root.optString("status", if (error == null) "available" else "unavailable")
    val instanceExtensions = parseExtensions(root.optJSONArray("instanceExtensions"))
    val instanceLayers = mutableListOf<LayerEntry>()
    val layerArray = root.optJSONArray("instanceLayers") ?: JSONArray()
    for (i in 0 until layerArray.length()) {
        val layer = layerArray.optJSONObject(i) ?: continue
        val layerExtensions = parseExtensions(layer.optJSONArray("extensions"), "Instance layer")
        instanceLayers += LayerEntry(layer.optString("name"), layer.optString("description"), layer.optInt("specVersion"), layer.optInt("implementationVersion"), layerExtensions)
    }
    val devices = mutableListOf<DeviceReport>()
    val deviceArray = root.optJSONArray("devices") ?: JSONArray()
    for (i in 0 until deviceArray.length()) {
        val item = deviceArray.optJSONObject(i) ?: continue
        val deviceLayers = mutableListOf<LayerEntry>()
        val deviceLayerArray = item.optJSONArray("deviceLayers") ?: JSONArray()
        for (j in 0 until deviceLayerArray.length()) {
            val layer = deviceLayerArray.optJSONObject(j) ?: continue
            val layerExtensions = parseExtensions(layer.optJSONArray("extensions"), "Device layer")
            deviceLayers += LayerEntry(layer.optString("name"), layer.optString("description"), layer.optInt("specVersion"), layer.optInt("implementationVersion"), layerExtensions)
        }
        val extensions = parseExtensions(item.optJSONArray("deviceExtensions"))
        val features = mutableListOf<FeatureEntry>()
        val featureArray = item.optJSONArray("features") ?: JSONArray()
        for (j in 0 until featureArray.length()) {
            val feature = featureArray.optJSONObject(j) ?: continue
            features += FeatureEntry(sanitizeReportLabel(feature.optString("name")), feature.optBoolean("supported"))
        }
        val versionedFeatureArray = item.optJSONArray("versionedFeatures") ?: JSONArray()
        for (j in 0 until versionedFeatureArray.length()) {
            val feature = versionedFeatureArray.optJSONObject(j) ?: continue
            features += FeatureEntry(sanitizeReportLabel(feature.optString("name")), feature.optBoolean("supported"))
        }
        val queues = mutableListOf<QueueEntry>()
        val queueArray = item.optJSONArray("queues") ?: JSONArray()
        for (j in 0 until queueArray.length()) {
            val q = queueArray.optJSONObject(j) ?: continue
            val queueFlags = q.optLong("flags")
            val knownQueueFlags = 0x577L
            queues += QueueEntry(q.optInt("index"), q.optInt("count"), q.optInt("timestampValidBits"), queueFlags, q.optBoolean("graphics"), q.optBoolean("compute"), q.optBoolean("transfer"), q.optBoolean("sparse"), q.optBoolean("protected"), q.optBoolean("videoDecode"), q.optBoolean("videoEncode"), q.optBoolean("opticalFlow"), q.optBoolean("dataGraph"), queueFlags and knownQueueFlags.inv(), q.optString("minImageTransferGranularity", "0 × 0 × 0"), q.optLong("videoCodecOperations"))
        }
        val heaps = mutableListOf<MemoryHeapEntry>()
        val memoryTypes = mutableListOf<MemoryTypeEntry>()
        val memory = item.optJSONObject("memory")
        val heapArray = memory?.optJSONArray("heaps") ?: JSONArray()
        for (j in 0 until heapArray.length()) {
            val heap = heapArray.optJSONObject(j) ?: continue
            heaps += MemoryHeapEntry(heap.optInt("index"), heap.optLong("size"), heap.optLong("flags"))
        }
        val typeArray = memory?.optJSONArray("types") ?: JSONArray()
        for (j in 0 until typeArray.length()) {
            val type = typeArray.optJSONObject(j) ?: continue
            memoryTypes += MemoryTypeEntry(type.optInt("index"), type.optInt("heap"), type.optLong("flags"))
        }
        val formats = mutableListOf<FormatEntry>()
        val limits = mutableListOf<Pair<String, String>>()
        val formatArray = item.optJSONArray("formats") ?: JSONArray()
        for (j in 0 until formatArray.length()) {
            val format = formatArray.optJSONObject(j) ?: continue
            formats += FormatEntry(format.optString("name"), format.optBoolean("supported"), format.optLong("linear"), format.optLong("optimal"), format.optLong("buffer"))
        }
        val limitArray = item.optJSONArray("limits") ?: JSONArray()
        for (j in 0 until limitArray.length()) {
            val limit = limitArray.optJSONObject(j) ?: continue
            limits += limit.optString("name") to limit.optString("value")
        }
        val surfaceFormats = mutableListOf<SurfaceFormatEntry>()
        val presentModes = mutableListOf<String>()
        val presentationQueues = mutableListOf<Pair<Int, Boolean>>()
        val capabilityPairs = mutableListOf<Pair<String, String>>()
        val detailedProperties = mutableListOf<PropertyEntry>()
        val detailedPropertyArray = item.optJSONArray("detailedProperties") ?: JSONArray()
        for (j in 0 until detailedPropertyArray.length()) {
            val prop = detailedPropertyArray.optJSONObject(j) ?: continue
            detailedProperties += PropertyEntry(sanitizeReportSection(prop.optString("section")), prop.optString("name"), prop.optString("value"))
        }
        val surface = item.optJSONObject("surface")
        val surfaceAvailable = surface?.optBoolean("available") == true
        val surfacePresentationSupported = surface?.optBoolean("presentationSupported") == true
        val surfaceColorSpaceExtensionAvailable = surface?.optBoolean("colorSpaceExtensionAvailable") == true
        val surfaceColorSpaceExtensionEnabled = surface?.optBoolean("colorSpaceExtensionEnabled") == true
        val surfaceFormatQueryResult = surface?.optInt("formatQueryResult", -1) ?: -1
        val surfaceFormatQueryResultSecond = surface?.optInt("formatQueryResultSecond", -1) ?: -1
        val surfaceFormatQuerySecondAttempted = surface?.optBoolean("formatQuerySecondAttempted", false) ?: false
        val surfaceFormatQuerySafetyRejected = surface?.optBoolean("formatQuerySafetyRejected", false) ?: false
        if (surface != null) {
            listOf("minImageCount", "maxImageCount", "currentExtent", "minExtent", "maxExtent", "maxImageArrayLayers", "supportedTransforms", "currentTransform", "supportedCompositeAlpha", "supportedUsageFlags").forEach { key ->
                if (surface.has(key)) capabilityPairs += key to surface.optString(key)
            }
            val surfaceFormatArray = surface.optJSONArray("formats") ?: JSONArray()
            for (j in 0 until surfaceFormatArray.length()) {
                val sf = surfaceFormatArray.optJSONObject(j) ?: continue
                surfaceFormats += SurfaceFormatEntry(sf.optString("format"), sf.optString("colorSpace"), sf.optString("class"), sf.optString("description"), true)
            }
            val presentArray = surface.optJSONArray("presentModes") ?: JSONArray()
            for (j in 0 until presentArray.length()) presentModes += presentArray.optString(j)
            val presentationArray = surface.optJSONArray("queuePresentation") ?: JSONArray()
            for (j in 0 until presentationArray.length()) {
                val p = presentationArray.optJSONObject(j) ?: continue
                presentationQueues += p.optInt("queueFamily") to p.optBoolean("supported")
            }
        }
        devices += DeviceReport(
            item.optString("name", "Unknown GPU"),
            item.optString("apiVersion", "Unknown"),
            item.optString("driverVersion", "Unknown"),
            item.optString("driverVersionText", item.optString("driverVersion", "Unknown")),
            "0x${item.optLong("vendorId").toString(16).uppercase()}",
            item.optLong("vendorId"),
            "0x${item.optLong("deviceId").toString(16).uppercase()}",
            deviceTypeName(item.optInt("deviceType")),
            deviceLayers,
            extensions,
            item.optString("deviceExtensionStatus", "unknown"),
            item.optString("deviceExtensionReason", "Device-extension enumeration status was not reported by the native collector."),
            features,
            queues,
            heaps,
            memoryTypes,
            formats,
            limits,
            detailedProperties,
            item.optString("extendedQueryStatus", "unknown"),
            item.optString("extendedQueryReason", "Extended physical-device query status was not reported by the native collector."),
            surfaceAvailable,
            surfacePresentationSupported,
            surfaceColorSpaceExtensionAvailable,
            surfaceColorSpaceExtensionEnabled,
            surfaceFormatQueryResult,
            surfaceFormatQueryResultSecond,
            surfaceFormatQuerySecondAttempted,
            surfaceFormatQuerySafetyRejected,
            capabilityPairs,
            surfaceFormats,
            presentModes,
            presentationQueues,
            vulkan14Status = item.optString("vulkan14Status", "not_applicable"),
            vulkan14Reason = item.optString("vulkan14Reason", "")
        )
    }
    val registry = root.optJSONObject("registryCoverage")
    val registryCoverage = if (registry != null) {
        val structs = mutableListOf<String>()
        val structArray = registry.optJSONArray("implementedPhysicalDeviceStructs") ?: JSONArray()
        for (i in 0 until structArray.length()) structs += structArray.optString(i)
        val groups = mutableListOf<String>()
        val groupArray = registry.optJSONArray("validatedRuntimeQueryGroups") ?: JSONArray()
        for (i in 0 until groupArray.length()) groups += groupArray.optString(i)
        RegistryCoverage(
            baseline = registry.optString("baseline", "Unknown"),
            mode = registry.optString("mode", "Unknown"),
            implementedPhysicalDeviceStructCount = registry.optInt("implementedPhysicalDeviceStructCount", 0),
            validatedRuntimeQueryGroupCount = registry.optInt("validatedRuntimeQueryGroupCount", 0),
            runtimeExtensionTokenCount = registry.optInt("runtimeExtensionTokenCount", 0),
            catalogSchemaVersion = registry.optInt("catalogSchemaVersion", 0),
            reportSchema = registry.optString("reportSchema", "Unknown"),
            headerBaseline = registry.optString("headerBaseline", "Unknown"),
            instanceDependencyCandidateCount = registry.optInt("instanceDependencyCandidateCount", 0),
            implementedPhysicalDeviceStructs = structs,
            validatedRuntimeQueryGroups = groups
        )
    } else RegistryCoverage()
    val effectiveError = when {
        error != null -> error
        nativeStatus == "unavailable" -> root.optString("reason", "Vulkan base probe reported unavailable.")
        nativeStatus == "not_applicable" -> root.optString("reason", "Vulkan device enumeration is not applicable.")
        devices.isEmpty() -> root.optString("reason", "Vulkan instance was created but no physical device was returned.")
        else -> null
    }
    Log.i("VulkanScope", "Vulkan parsed status=$nativeStatus devices=${devices.size} error=${effectiveError ?: "none"}")
    return VulkanReport(root.optString("loaderVersion", "Unknown"), instanceExtensions, instanceLayers, devices, effectiveError, registryCoverage)
}

private fun parseExtensions(array: JSONArray?, scopeOverride: String? = null): List<ExtensionEntry> {
    if (array == null) return emptyList()
    val result = mutableListOf<ExtensionEntry>()
    for (i in 0 until array.length()) {
        val item = array.optJSONObject(i) ?: continue
        result += ExtensionEntry(item.optString("name"), scopeOverride ?: item.optString("scope"), item.optInt("specVersion"))
    }
    return result.sortedBy { it.name }
}

private fun deviceTypeName(type: Int): String = when (type) {
    1 -> "Integrated GPU"
    2 -> "Discrete GPU"
    3 -> "Virtual GPU"
    4 -> "CPU"
    else -> "Other / Unknown"
}

@Composable
private fun VulkanScopeApp(
    displayReport: DisplayReport,
    report: VulkanReport?,
    loading: Boolean,
    collectionStatus: CollectionStatus,
    surfaceReady: (Surface) -> Unit,
    surfaceDestroyed: () -> Unit,
    driverMode: DriverMode,
    turnipSupport: TurnipSupport,
    onDriverModeChanged: (DriverMode) -> Unit,
    onInstallDriverBundle: () -> Unit,
    onPageOpened: (Page) -> Unit,
    onRequestQuery: (String) -> Unit
) {
    var page by remember { mutableStateOf(Page.Overview) }
    LaunchedEffect(page) { onPageOpened(page) }

    val red = ComposeColor(0xFFF21D2F)
    MaterialTheme(colorScheme = darkColorScheme(background = ComposeColor.Black, surface = ComposeColor(0xFF101010), surfaceVariant = ComposeColor(0xFF191919), primary = red, onPrimary = ComposeColor.White, secondary = red, tertiary = ComposeColor(0xFFFF6573), onBackground = ComposeColor(0xFFF4F4F4), onSurface = ComposeColor(0xFFF4F4F4))) {
        BackHandler(enabled = page != Page.Overview) { page = Page.Overview }

        val isLandscape = androidx.compose.ui.platform.LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
        Scaffold(
            containerColor = ComposeColor.Black,
            topBar = {
                Column {
                    AppHeader(page, onBack = { page = Page.Overview }, onSettings = { page = Page.Settings }, onInfo = { page = Page.Info })
                    CollectionStatusBanner(collectionStatus)
                }
            },
            bottomBar = {
                if (!isLandscape) {
                    NavigationBar(containerColor = ComposeColor(0xFF0A0A0A), tonalElevation = 0.dp) {
                        navigationItems().forEach { item ->
                            NavigationBarItem(
                                selected = selectedNavigationPage(page) == item.page,
                                onClick = { page = item.page },
                                icon = { Icon(painterResource(item.icon), contentDescription = item.label, modifier = Modifier.size(24.dp)) },
                                label = { Text(item.label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ComposeColor.White,
                                    selectedTextColor = ComposeColor.White,
                                    indicatorColor = red,
                                    unselectedIconColor = ComposeColor(0xFFB8B8B8),
                                    unselectedTextColor = ComposeColor(0xFFB8B8B8)
                                )
                            )
                        }
                    }
                }
            }
        ) { padding ->
            Row(Modifier.fillMaxSize().padding(padding)) {
                if (isLandscape) {
                    CompactNavigationRail(
                        selectedPage = selectedNavigationPage(page),
                        onPageSelected = { page = it },
                        red = red
                    )
                }
                Box(Modifier.weight(1f)) {
                    SurfaceProbe(
                        modifier = Modifier.matchParentSize(),
                        onCreated = { surface ->
                            surfaceReady(surface)
                        },
                        onDestroyed = surfaceDestroyed
                    )
                    if (loading) LoadingView()
                    else {
                        val current = report
                        if (current == null) EmptyState("No Vulkan report")
                        else {
                            AnimatedContent(
                                targetState = page,
                                transitionSpec = {
                                    val forward = pageTransitionIndex(targetState) > pageTransitionIndex(initialState)
                                    if (forward) {
                                        slideInHorizontally(animationSpec = spring()) { it / 5 } + fadeIn(animationSpec = spring()) togetherWith
                                            slideOutHorizontally(animationSpec = spring()) { -it / 5 } + fadeOut(animationSpec = spring())
                                    } else {
                                        slideInHorizontally(animationSpec = spring()) { -it / 5 } + fadeIn(animationSpec = spring()) togetherWith
                                            slideOutHorizontally(animationSpec = spring()) { it / 5 } + fadeOut(animationSpec = spring())
                                    }
                                },
                                label = "pageTransition"
                            ) { targetPage ->
                                PageContent(targetPage, current, displayReport, driverMode, turnipSupport, onDriverModeChanged, onInstallDriverBundle, onNavigate = { page = it }, onRequestQuery = onRequestQuery)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SurfaceProbe(
    modifier: Modifier = Modifier,
    onCreated: (Surface) -> Unit,
    onDestroyed: () -> Unit
) {
    AndroidView(
        modifier = modifier.alpha(0f),
        factory = { context ->
            SurfaceView(context).apply {
                setBackgroundColor(Color.BLACK)
                if (Build.VERSION.SDK_INT >= 34) {
                    setSurfaceLifecycle(SurfaceView.SURFACE_LIFECYCLE_FOLLOWS_ATTACHMENT)
                }
                holder.addCallback(object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) { onCreated(holder.surface) }
                    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
                    override fun surfaceDestroyed(holder: SurfaceHolder) { onDestroyed() }
                })
            }
        },
        update = {}
    )
}



@Composable
private fun PageContent(page: Page, report: VulkanReport, display: DisplayReport, driverMode: DriverMode, turnipSupport: TurnipSupport, onDriverModeChanged: (DriverMode) -> Unit, onInstallDriverBundle: () -> Unit, onNavigate: (Page) -> Unit, onRequestQuery: (String) -> Unit) {
    val device = report.devices.firstOrNull()
    when (page) {
        Page.Overview -> OverviewPage(report, device, display, driverMode, onNavigate)
        Page.Vulkan -> VulkanPage(report, device, turnipSupport == TurnipSupport.SUPPORTED)
        Page.Display -> DisplayPage(display, device)
        Page.Surface -> SurfacePage(device)
        Page.Features -> FeaturesPage(device)
        Page.Memory -> MemoryPage(device)
        Page.Queues -> QueuesPage(device)
        Page.Formats -> FormatsPage(device)
        Page.Properties -> PropertiesPage(device, onRequestQuery)
        Page.Extensions -> ExtensionsPage(report, device)
        Page.Profiles -> ProfilesPage(report, device)
        Page.Settings -> SettingsPage(report, display, driverMode, turnipSupport, onDriverModeChanged, onInstallDriverBundle)
        Page.Info -> InfoPage(report.registryCoverage)
    }
}



@Composable
private fun OverviewPage(report: VulkanReport, device: DeviceReport?, display: DisplayReport, driverMode: DriverMode, navigate: (Page) -> Unit) {
    LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            HeroCard(device, report, driverMode)
        }
        if (report.error != null) {
            item {
                SectionCard("Vulkan inspection error") {
                    Text(report.error, color = ComposeColor(0xFFFF6B6B))
                    Text(
                        "The driver mode is shown separately from the inspection result.",
                        color = ComposeColor(0xFF9E9E9E),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        item {
            ExploreCard { destination -> navigate(destination) }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Vulkan", device?.apiVersion ?: "Unknown", Modifier.weight(1f))
                MetricCard("Display", display.refreshRate, Modifier.weight(1f))
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("HDR", if (display.hdrTypes.isEmpty()) "No" else "${display.hdrTypes.size} types", Modifier.weight(1f))
                MetricCard("Wide gamut", if (display.wideGamut) "Supported" else "Not exposed", Modifier.weight(1f))
            }
        }
        item { SectionCard("Quick access") {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    QuickAccessCard("Vulkan", Page.Vulkan, navigate, Modifier.weight(1f))
                    QuickAccessCard("Surface", Page.Surface, navigate, Modifier.weight(1f))
                    QuickAccessCard("Display", Page.Display, navigate, Modifier.weight(1f))
                    QuickAccessCard("HDR & Color", Page.Display, navigate, Modifier.weight(1f))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    QuickAccessCard("Extensions", Page.Extensions, navigate, Modifier.weight(1f))
                    QuickAccessCard("Profiles", Page.Profiles, navigate, Modifier.weight(1f))
                    QuickAccessCard("More", Page.Features, navigate, Modifier.weight(1f))
                    Spacer(Modifier.weight(1f))
                }
            }
        } }
        item { SectionCard("Capability snapshot") {
            KeyValue("GPU", device?.name ?: "Unknown")
            KeyValue("Driver", device?.driverVersionText ?: device?.driverVersion ?: "Unknown")
            KeyValue("Vendor ID", device?.vendorId ?: "Unknown")
            KeyValue("Device ID", device?.deviceId ?: "Unknown")
            KeyValue("Device type", device?.deviceType ?: "Unknown")
            KeyValue("Loader API", report.loaderVersion)
            KeyValue("Instance extensions", report.instanceExtensions.size.toString())
            KeyValue("Device extensions", device?.extensions?.size?.toString() ?: "0")
        } }
    }
}



@Composable
private fun VulkanPage(report: VulkanReport, device: DeviceReport?, turnipSupported: Boolean) {
    LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { SectionCard("Vulkan API") {
            KeyValue("Loader / instance API", report.loaderVersion)
            KeyValue("Device API", device?.apiVersion ?: "Unknown")
            KeyValue("Driver", device?.driverVersionText ?: device?.driverVersion ?: "Unknown")
            KeyValue("Device type", device?.deviceType ?: "Unknown")
            KeyValue("Vendor ID", device?.vendorId ?: "Unknown")
            KeyValue("Device ID", device?.deviceId ?: "Unknown")
        } }
        item { SectionCard("Instance layers") {
            if (report.instanceLayers.isEmpty()) {
                Text("No instance layers are exposed by the active Vulkan implementation.")
                Text("This is normal on many Android production/driver configurations; validation layers are optional and are not bundled by VulkanScope.", color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.bodySmall)
            }
            report.instanceLayers.forEach { layer ->
                Column(Modifier.padding(vertical = 5.dp)) {
                    Text(layer.name, fontWeight = FontWeight.SemiBold)
                    Text("spec ${layer.specVersion} · implementation ${layer.implementationVersion}", color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.labelSmall)
                    if (layer.description.isNotBlank()) Text(layer.description, color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
                }
            }
        } }
        item { SectionCard("Device layers") {
            if (device?.deviceLayers.isNullOrEmpty()) {
                Text("No device layers are exposed.")
                Text("Device layers are legacy functionality; modern Vulkan uses instance layers.", color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.bodySmall)
            }
            device?.deviceLayers?.forEach { layer ->
                Column(Modifier.padding(vertical = 5.dp)) {
                    Text(layer.name, fontWeight = FontWeight.SemiBold)
                    Text("spec ${layer.specVersion} · implementation ${layer.implementationVersion}", color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.labelSmall)
                    if (layer.description.isNotBlank()) Text(layer.description, color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
                    layer.extensions.forEach { ext -> Text("${ext.name} · spec ${ext.specVersion}", color = ComposeColor(0xFFBDBDBD), style = MaterialTheme.typography.labelSmall) }
                }
            }
        } }
        item { SectionCard("Instance extensions") {
            report.instanceExtensions.sortedBy { it.name }.forEach { ext -> Text("${ext.name} · spec ${ext.specVersion}") }
        } }
        item { SectionCard("Operating system") {
            KeyValue("Architecture", Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown")
            KeyValue("Version", Build.VERSION.RELEASE)
            KeyValue("Codename", Build.VERSION.CODENAME)
            KeyValue("SDK", Build.VERSION.SDK_INT.toString())
            KeyValue("Build ID", Build.ID)
            KeyValue("Build incremental", Build.VERSION.INCREMENTAL)
            KeyValue("Security patch", Build.VERSION.SECURITY_PATCH)
            KeyValue("Brand", Build.BRAND)
            KeyValue("Manufacturer", Build.MANUFACTURER)
            KeyValue("Product", Build.PRODUCT)
            KeyValue("Device", Build.DEVICE)
            KeyValue("Board", Build.BOARD)
            KeyValue("Hardware", Build.HARDWARE)
            KeyValue("Fingerprint", Build.FINGERPRINT)
        } }
        item { SectionCard("Android runtime") {
            KeyValue("Architecture", Build.SUPPORTED_ABIS.joinToString(", "))
            KeyValue("Manufacturer", Build.MANUFACTURER)
            KeyValue("Model", Build.MODEL)
            KeyValue("Android", Build.VERSION.RELEASE)
            KeyValue("SDK", Build.VERSION.SDK_INT.toString())
            KeyValue("Build ID", Build.ID)
            KeyValue("Build incremental", Build.VERSION.INCREMENTAL)
            KeyValue("Turnip eligibility", if (turnipSupported) "arm64-v8a + Qualcomm Adreno detected" else "Unknown / not eligible")
        } }
        item { SectionCard("Feature coverage") {
            val supported = device?.features?.count { it.supported } ?: 0
            val total = device?.features?.size ?: 0
            KeyValue("Queried feature fields", "$supported / $total supported")
            KeyValue("Instance extensions", report.instanceExtensions.size.toString())
            KeyValue("Device extensions", device?.extensions?.size?.toString() ?: "0")
        } }
    }
}



@Composable
private fun HeroCard(device: DeviceReport?, report: VulkanReport, driverMode: DriverMode) {
    Card(colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFF171717)), shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                VendorLogo(device?.vendorIdRaw, Modifier.size(82.dp))
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(device?.name ?: if (report.error != null) "Vulkan unavailable" else "Vulkan device unavailable", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(if (device != null) vendorInfo(device.vendorIdRaw).name else report.error?.take(120) ?: "Unknown vendor", color = ComposeColor(0xFFBDBDBD), maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(driverMode.label, color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}



@Composable
private fun QuickAccessCard(title: String, destination: Page, navigate: (Page) -> Unit, modifier: Modifier) {
    Card(
        onClick = { navigate(destination) },
        colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFF171717)),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier.height(72.dp)
    ) {
        Column(
            Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
        ) {
            Icon(
                painterResource(pageIcon(destination)),
                contentDescription = null,
                modifier = Modifier.size(19.dp),
                tint = ComposeColor(0xFFF21D2F)
            )
            Text(
                title,
                fontSize = 11.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}



@Composable
private fun CompactNavigationRail(selectedPage: Page, onPageSelected: (Page) -> Unit, red: ComposeColor) {
    val firstFocusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { firstFocusRequester.requestFocus() }
    Surface(
        modifier = Modifier.width(80.dp),
        color = ComposeColor(0xFF101010)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .focusGroup()
                .padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            navigationItems().forEachIndexed { index, item ->
                val selected = selectedPage == item.page
                val bringIntoViewRequester = remember { BringIntoViewRequester() }
                val scope = rememberCoroutineScope()
                Card(
                    onClick = { onPageSelected(item.page) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) red else ComposeColor.Transparent
                    ),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .then(if (index == 0) Modifier.focusRequester(firstFocusRequester) else Modifier)
                        .bringIntoViewRequester(bringIntoViewRequester)
                        .onFocusChanged { state ->
                            if (state.isFocused) scope.launch { bringIntoViewRequester.bringIntoView() }
                        }
                ) {
                    Column(
                        Modifier.fillMaxSize().padding(horizontal = 2.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically)
                    ) {
                        Icon(
                            painterResource(item.icon),
                            contentDescription = item.label,
                            modifier = Modifier.size(21.dp),
                            tint = if (selected) ComposeColor.White else ComposeColor(0xFFB8B8B8)
                        )
                        Text(
                            item.label,
                            color = if (selected) ComposeColor.White else ComposeColor(0xFFB8B8B8),
                            fontSize = 9.sp,
                            lineHeight = 10.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun ExploreCard(onNavigate: (Page) -> Unit) {
    SectionCard("Explore") {
        Text("Detailed Vulkan inspection areas", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(Page.Features, Page.Memory, Page.Queues, Page.Formats, Page.Properties).forEach { page ->
                AssistChip(
                    onClick = { onNavigate(page) },
                    leadingIcon = { Icon(painterResource(pageIcon(page)), contentDescription = null, modifier = Modifier.size(18.dp)) },
                    label = { Text(page.title) }
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppHeader(page: Page, onBack: () -> Unit, onSettings: () -> Unit, onInfo: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            if (page != Page.Overview) {
                IconButton(onClick = onBack) {
                    Icon(painterResource(R.drawable.ic_back), contentDescription = "Back")
                }
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Image(
                    painter = painterResource(R.drawable.vulkanscope_logo_horizontal),
                    contentDescription = "VulkanScope",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(148.dp).height(28.dp)
                )
                Text(page.title, style = MaterialTheme.typography.labelMedium, color = ComposeColor(0xFF9E9E9E), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        },
        actions = {
            if (page != Page.Settings && page != Page.Info) {
                IconButton(onClick = onInfo) {
                    Icon(painterResource(R.drawable.ic_info), contentDescription = "Info")
                }
                IconButton(onClick = onSettings) {
                    Icon(painterResource(R.drawable.ic_settings), contentDescription = "Settings")
                }
            }
        },
        colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(containerColor = ComposeColor.Black)
    )
}

@Composable
private fun DisplayPage(display: DisplayReport, device: DeviceReport?) {
    LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { SectionCard("Display") {
            KeyValue("Physical mode", display.resolution)
            KeyValue("Current refresh", display.refreshRate)
            KeyValue("Wide color gamut", if (display.wideGamut) "YES" else "NO / NOT EXPOSED")
            KeyValue("Preferred wide gamut color space", display.preferredWideGamut)
        } }
        item { SectionCard("HDR capabilities") {
            if (display.hdrTypes.isEmpty()) Text("No HDR type reported", color = ComposeColor(0xFF9E9E9E))
            display.hdrTypes.forEach { Text(it, fontWeight = FontWeight.Medium) }
            HorizontalDivider(Modifier.padding(vertical = 6.dp), color = ComposeColor(0xFF303030))
            KeyValue("Minimum luminance", display.minLuminance)
            KeyValue("Maximum luminance", display.maxLuminance)
            KeyValue("Maximum average luminance", display.averageLuminance)
        } }
        item { SectionCard("Supported display modes") { display.modes.forEach { Text(it) } } }
        item { SectionCard("Display ↔ Vulkan interpretation") {
            KeyValue("Android wide gamut", if (display.wideGamut) "Reported" else "Not reported")
            KeyValue("Vulkan surface data", if (device?.surfaceAvailable == true) "Queried from VkSurfaceKHR" else "Unavailable")
            Text("A Vulkan color-space capability is not treated as a measurement of the panel's physical gamut.", color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.bodySmall)
        } }
    }
}

@Composable
private fun SurfacePage(device: DeviceReport?) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf(SupportFilter.ALL) }
    val entries = remember(device) { buildSurfaceCatalog(device?.surfaceFormats ?: emptyList()) }
    val filtered = remember(query, filter, entries) {
        entries.filter { entry ->
            val statusOk = when (filter) { SupportFilter.ALL -> true; SupportFilter.SUPPORTED -> entry.supported; SupportFilter.UNSUPPORTED -> !entry.supported }
            statusOk && (query.isBlank() || entry.format.contains(query, true) || entry.colorSpace.contains(query, true) || entry.classification.contains(query, true) || entry.description.contains(query, true))
        }
    }
    LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { SectionCard("VkSurfaceKHR") {
            KeyValue("Status", if (device?.surfaceAvailable == true) "Available" else "Not available")
            KeyValue("Presentation support", when {
                device?.surfacePresentationSupported == true -> "Supported"
                device?.surfaceAvailable == true -> "Not supported by this Vulkan device"
                else -> "Unavailable"
            })
            KeyValue("Surface format query", when (device?.surfaceFormatQueryResult) { 0 -> "VK_SUCCESS"; 5 -> "VK_INCOMPLETE"; null, -1 -> "Unavailable"; else -> device.surfaceFormatQueryResult.toString() })
            KeyValue("Second format query", when {
                device?.surfaceFormatQuerySecondAttempted == true && device.surfaceFormatQueryResultSecond == 0 -> "VK_SUCCESS"
                device?.surfaceFormatQuerySecondAttempted == true && device.surfaceFormatQueryResultSecond == 5 -> "VK_INCOMPLETE"
                device?.surfaceFormatQuerySecondAttempted == true -> device.surfaceFormatQueryResultSecond.toString()
                device?.surfaceFormatQuerySafetyRejected == true -> "Skipped: safety cap"
                else -> "Unavailable"
            })
            KeyValue("Returned format pairs", device?.surfaceFormats?.size?.toString() ?: "Unavailable")
            device?.surfaceCapabilities?.forEach { KeyValue(it.first, it.second) }
        } }
        item { SectionCard("Search surface formats / color spaces") {
            OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("Search BT.709, BT.2020, P3, HDR10, format…") })
            Spacer(Modifier.height(6.dp))
            SupportFilterRow(filter) { filter = it }
            Text("${filtered.size} entries", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.labelMedium)
        } }
        item { SectionCard("HDR / wide-color surface detection") {
            KeyValue("VK_EXT_swapchain_colorspace", when { device?.surfaceColorSpaceExtensionEnabled == true -> "Enabled"; device?.surfaceColorSpaceExtensionAvailable == true -> "Available but not enabled"; else -> "Not exposed" })
            val hdrPairs = device?.surfaceFormats?.count { it.classification == "HDR10 / PQ" || it.classification == "HDR10 / HLG" || it.classification == "Dolby Vision" } ?: 0
            val wideColorPairs = device?.surfaceFormats?.count { it.classification == "Display-P3" || it.classification == "Display-P3 / Linear" || it.classification == "BT.2020" || it.classification == "scRGB" || it.classification == "scRGB / Linear" } ?: 0
            KeyValue("HDR color-space pairs", if (device?.surfacePresentationSupported != true) "Unavailable" else hdrPairs.toString())
            KeyValue("Wide-color pairs", if (device?.surfacePresentationSupported != true) "Unavailable" else wideColorPairs.toString())
            Text("Supported entries are returned directly by vkGetPhysicalDeviceSurfaceFormatsKHR. Unsupported entries are generated from the app's known inspection catalog and are not driver-reported capabilities.", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
        } }
        item { SectionCard("Format + color-space pairs") {
            if (filtered.isEmpty()) Text("No matching entries")
            filtered.forEach { format ->
                Column(Modifier.padding(vertical = 5.dp)) {
                    Text(format.format, fontWeight = FontWeight.SemiBold)
                    Text(format.colorSpace, color = ComposeColor(0xFFD0D0D0), style = MaterialTheme.typography.bodySmall)
                    Text(if (format.supported) "SUPPORTED · ${format.classification}" else "NOT SUPPORTED · ${format.classification}", color = if (format.supported) ComposeColor(0xFF7DFF9B) else ComposeColor(0xFFFF6B6B), style = MaterialTheme.typography.labelSmall)
                    Text(format.description, color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
                }
            }
        } }
        item { SectionCard("Present modes") { device?.presentModes?.forEach { Text(it) } } }
        item { SectionCard("Presentation support") { device?.presentationQueues?.forEach { Text("Queue family ${it.first}: ${if (it.second) "PRESENT" else "NO PRESENT"}") } } }
    }
}

private fun buildSurfaceCatalog(supported: List<SurfaceFormatEntry>): List<SurfaceFormatEntry> {
    if (supported.isEmpty()) return emptyList()
    val supportedKeys = supported.map { it.format + "|" + it.colorSpace }.toSet()
    val commonColorSpaces = listOf(
        "VK_COLOR_SPACE_SRGB_NONLINEAR_KHR" to "sRGB",
        "VK_COLOR_SPACE_DISPLAY_P3_NONLINEAR_EXT" to "Display-P3",
        "VK_COLOR_SPACE_EXTENDED_SRGB_LINEAR_EXT" to "scRGB / Linear",
        "VK_COLOR_SPACE_DISPLAY_P3_LINEAR_EXT" to "Display-P3 / Linear",
        "VK_COLOR_SPACE_DCI_P3_NONLINEAR_EXT" to "DCI-P3",
        "VK_COLOR_SPACE_BT709_LINEAR_EXT" to "BT.709 / Linear",
        "VK_COLOR_SPACE_BT709_NONLINEAR_EXT" to "BT.709",
        "VK_COLOR_SPACE_BT2020_LINEAR_EXT" to "BT.2020",
        "VK_COLOR_SPACE_HDR10_ST2084_EXT" to "HDR10 / PQ",
        "VK_COLOR_SPACE_DOLBYVISION_EXT" to "Dolby Vision",
        "VK_COLOR_SPACE_HDR10_HLG_EXT" to "HDR10 / HLG",
        "VK_COLOR_SPACE_ADOBERGB_LINEAR_EXT" to "Adobe RGB / Linear",
        "VK_COLOR_SPACE_ADOBERGB_NONLINEAR_EXT" to "Adobe RGB",
        "VK_COLOR_SPACE_PASS_THROUGH_EXT" to "Pass-through",
        "VK_COLOR_SPACE_EXTENDED_SRGB_NONLINEAR_EXT" to "scRGB"
    )
    val formats = supported.map { it.format }.distinct().take(12)
    val generated = formats.flatMap { format -> commonColorSpaces.map { (cs, cls) ->
        val key = "$format|$cs"
        supported.find { it.format == format && it.colorSpace == cs } ?: SurfaceFormatEntry(format, cs, cls, "Known Vulkan color-space candidate; not returned by this surface.", key in supportedKeys)
    } }
    return (supported + generated.filter { !supportedKeys.contains(it.format + "|" + it.colorSpace) }).distinctBy { it.format + "|" + it.colorSpace }.sortedWith(compareByDescending<SurfaceFormatEntry> { it.supported }.thenBy { it.colorSpace }.thenBy { it.format })
}

@Composable
private fun FeaturesPage(device: DeviceReport?) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf(SupportFilter.ALL) }
    var sourceFilter by remember { mutableStateOf("All") }
    val all = device?.features ?: emptyList()
    val sources = remember(all) { listOf("All") + all.map { featureSource(it.name) }.distinct() }
    val filtered = remember(all, query, filter, sourceFilter) {
        all.filter { feature ->
            val supportMatches = when (filter) {
                SupportFilter.ALL -> true
                SupportFilter.SUPPORTED -> feature.supported
                SupportFilter.UNSUPPORTED -> !feature.supported
            }
            val sourceMatches = sourceFilter == "All" || featureSource(feature.name) == sourceFilter
            val textMatches = query.isBlank() || feature.name.contains(query, ignoreCase = true)
            supportMatches && sourceMatches && textMatches
        }
    }
    LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Text("Runtime feature support. Core 1.0, promoted core versions and extension-provided feature blocks remain distinguishable.", style = MaterialTheme.typography.bodySmall, color = ComposeColor(0xFF8F8F8F))
            OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), label = { Text("Search features") }, singleLine = true)
            SupportFilterRow(filter) { filter = it }
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                sources.forEach { source -> FilterChip(selected = sourceFilter == source, onClick = { sourceFilter = source }, label = { Text(source) }) }
            }
            Text("${filtered.size} features", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(vertical = 6.dp))
        }
        itemsIndexed(filtered, key = { index, feature -> "feature:${feature.name}:$index" }) { _, feature ->
            Card(colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFF111111)), shape = RoundedCornerShape(16.dp)) {
                Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(featureNameOnly(feature.name), fontWeight = FontWeight.Medium)
                        Text(featureSource(feature.name), color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.labelSmall)
                    }
                    Text(if (feature.supported) "SUPPORTED" else "NOT SUPPORTED", fontWeight = FontWeight.SemiBold, color = if (feature.supported) ComposeColor(0xFF73C991) else ComposeColor(0xFFFF6B6B), style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        if (filtered.isEmpty()) item { EmptyState("No matching features") }
    }
}

@Composable
private fun MemoryPage(device: DeviceReport?) {
    LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { SectionCard("Memory heaps") {
            if (device?.heaps.isNullOrEmpty()) EmptyState("Memory heap data unavailable")
            device?.heaps?.forEach { heap ->
                Column(Modifier.padding(vertical = 5.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("Heap ${heap.index}", fontWeight = FontWeight.SemiBold)
                    KeyValue("Size", formatBytes(heap.size))
                    KeyValue("Flags", memoryHeapFlags(heap.flags))
                }
            }
        } }
        item { SectionCard("Memory types") {
            if (device?.memoryTypes.isNullOrEmpty()) EmptyState("Memory type data unavailable")
            device?.memoryTypes?.forEach { type ->
                Column(Modifier.padding(vertical = 5.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("Type ${type.index}", fontWeight = FontWeight.SemiBold)
                    KeyValue("Heap", type.heap.toString())
                    KeyValue("Properties", memoryTypeFlags(type.flags))
                }
            }
        } }
    }
}

@Composable
private fun QueuesPage(device: DeviceReport?) {
    LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(device?.queues ?: emptyList(), key = { it.index }) { queue ->
            SectionCard("Queue family ${queue.index}") {
                KeyValue("Queue count", queue.count.toString())
                KeyValue("Timestamp valid bits", queue.timestampBits.toString())
                KeyValue("Capabilities", queueCapabilityFlags(queue.flags))
                KeyValue("Graphics", if (queue.graphics) "YES" else "NO")
                KeyValue("Compute", if (queue.compute) "YES" else "NO")
                KeyValue("Transfer", if (queue.transfer) "YES" else "NO")
                KeyValue("Sparse binding", if (queue.sparse) "YES" else "NO")
                KeyValue("Protected", if (queue.protected) "YES" else "NO")
                KeyValue("Optical flow", if (queue.opticalFlow) "YES" else "NO")
                KeyValue("Min image transfer granularity", queue.granularity)
                if (queue.videoCodecOperations != 0L) KeyValue("Video codec operations", videoCodecOperationsName(queue.videoCodecOperations))
            }
        }
        if (device?.queues.isNullOrEmpty()) item { EmptyState("Queue family data unavailable") }
    }
}

@Composable
private fun FormatsPage(device: DeviceReport?) {
    var query by remember { mutableStateOf("") }
    val formats = remember(device) { device?.formats ?: emptyList() }
    val filtered = remember(query, formats) { if (query.isBlank()) formats else formats.filter { it.name.contains(query, true) } }
    LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Text("Implementation-reported format capabilities. Bitmasks are expanded to canonical Vulkan feature names; unknown bits remain visible in hexadecimal.", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
            OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, placeholder = { Text("Search formats…") })
            Text("${filtered.size} formats", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(vertical = 6.dp))
        }
        itemsIndexed(filtered, key = { index, format -> "format:${format.name}:$index" }) { _, format ->
            Card(colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFF111111)), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(format.name, fontWeight = FontWeight.Medium)
                    KeyValue("Status", if (format.supported) "SUPPORTED" else "NOT SUPPORTED")
                    KeyValue("Linear", formatFeatureFlags(format.linear))
                    KeyValue("Optimal", formatFeatureFlags(format.optimal))
                    KeyValue("Buffer", formatFeatureFlags(format.buffer))
                }
            }
        }
    }
}

@Composable
private fun PropertiesPage(device: DeviceReport?, onRequestQuery: (String) -> Unit) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("All") }
    val properties = remember(device) { device?.detailedProperties ?: emptyList() }
    val limitGroups = remember(device?.limits) { (device?.limits ?: emptyList()).groupBy { limitCategory(it.first) } }
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
    LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Physical-device properties and limits are shown only from runtime Vulkan queries. Advanced query groups keep their explicit availability state.", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
            OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, placeholder = { Text("Search properties and limits…") })
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                sections.forEach { section -> FilterChip(selected = filter == section, onClick = { filter = section }, label = { Text(section) }) }
            }
        }
        if (filter == "Limits") {
            limitGroups.toSortedMap().forEach { (category, entries) ->
                item { SectionCard(category) {
                    val visible = entries.filter { query.isBlank() || it.first.contains(query, true) || it.second.contains(query, true) }
                    if (visible.isEmpty()) Text("No matching limits", color = ComposeColor(0xFF9E9E9E))
                    visible.forEach { (name, value) ->
                        Column(Modifier.padding(vertical = 4.dp)) {
                            Text(name, fontWeight = FontWeight.Medium)
                            Text(value, color = ComposeColor(0xFFD8D8D8), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                } }
            }
        } else {
                if (filter == "Core 1.4") {
                val status = device?.vulkan14Status ?: "unavailable"
                val message = when (status) {
                    "not_applicable" -> "Vulkan 1.4 is not exposed by this physical device. Device API: ${device?.apiVersion ?: "Unknown"}."
                    "unavailable" -> device?.vulkan14Reason?.ifBlank { "The Vulkan 1.4 property/feature query is unavailable on this device or Vulkan stack." } ?: "Vulkan 1.4 query status unavailable."
                    else -> "Vulkan 1.4 properties were queried from the active physical device."
                }
                if (properties.none { it.section == "Core 1.4" }) {
                    item { SectionCard("Vulkan 1.4 status") { Text(message, color = ComposeColor(0xFFFFC857)) } }
                }
            }
            item {
                val uniqueNames = filtered.asSequence().map { it.name }.distinct().count()
                Text("${filtered.size} query results · $uniqueNames unique property names", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.labelMedium)
            }
            itemsIndexed(filtered, key = { index, property -> "${property.section}:${property.name}:$index" }) { _, property ->
                Card(colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFF111111)), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(property.name, fontWeight = FontWeight.Medium)
                        Text(property.section, color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.labelSmall)
                        Text(property.value, color = ComposeColor(0xFFD8D8D8), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            if (filtered.isEmpty()) item { EmptyState("No matching properties") }
        }
    }
}

private fun limitCategory(name: String): String = when {
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

private fun featureSource(name: String): String = when {
    name.startsWith("Vulkan 1.1 · ") -> "Core 1.1"
    name.startsWith("Vulkan 1.2 · ") -> "Core 1.2"
    name.startsWith("Vulkan 1.3 · ") -> "Core 1.3"
    name.startsWith("Vulkan 1.4 · ") -> "Core 1.4"
    name.contains(" · ") -> name.substringBefore(" · ").ifBlank { "Extension" }
    else -> "Core 1.0"
}

private fun featureNameOnly(name: String): String = name.substringAfter(" · ", name)

private fun rawBitsSuffix(bits: Long, known: Set<Long>): String {
    var remaining = bits
    known.forEach { remaining = remaining and it.inv() }
    return if (remaining != 0L) " | UNKNOWN_BITS=0x${remaining.toString(16).uppercase()}" else ""
}

private fun memoryHeapFlags(bits: Long): String = buildList {
    if ((bits and 0x1L) != 0L) add("DEVICE_LOCAL")
    if ((bits and 0x2L) != 0L) add("MULTI_INSTANCE")
}.joinToString(" | ").ifBlank { "NONE" } + rawBitsSuffix(bits, setOf(0x1L, 0x2L))

private fun memoryTypeFlags(bits: Long): String = buildList {
    if ((bits and 0x1L) != 0L) add("DEVICE_LOCAL")
    if ((bits and 0x2L) != 0L) add("HOST_VISIBLE")
    if ((bits and 0x4L) != 0L) add("HOST_COHERENT")
    if ((bits and 0x8L) != 0L) add("HOST_CACHED")
    if ((bits and 0x10L) != 0L) add("LAZILY_ALLOCATED")
    if ((bits and 0x20L) != 0L) add("PROTECTED")
    if ((bits and 0x40L) != 0L) add("DEVICE_COHERENT_AMD")
    if ((bits and 0x80L) != 0L) add("DEVICE_UNCACHED_AMD")
    if ((bits and 0x100L) != 0L) add("RDMA_CAPABLE_NV")
}.joinToString(" | ").ifBlank { "NONE" } + rawBitsSuffix(bits, setOf(0x1L,0x2L,0x4L,0x8L,0x10L,0x20L,0x40L,0x80L,0x100L))

private fun queueCapabilityFlags(bits: Long): String = buildList {
    if ((bits and 0x1L) != 0L) add("GRAPHICS")
    if ((bits and 0x2L) != 0L) add("COMPUTE")
    if ((bits and 0x4L) != 0L) add("TRANSFER")
    if ((bits and 0x8L) != 0L) add("SPARSE_BINDING")
    if ((bits and 0x10L) != 0L) add("PROTECTED")
}.joinToString(" | ").ifBlank { "NONE" } + rawBitsSuffix(bits, setOf(0x1L,0x2L,0x4L,0x8L,0x10L))

private fun formatFeatureFlags(bits: Long): String {
    if (bits == 0L) return "NONE"
    val names = listOf(
        0x1L to "SAMPLED_IMAGE", 0x2L to "STORAGE_IMAGE", 0x4L to "STORAGE_IMAGE_ATOMIC", 0x8L to "UNIFORM_TEXEL_BUFFER",
        0x10L to "STORAGE_TEXEL_BUFFER", 0x20L to "STORAGE_TEXEL_BUFFER_ATOMIC", 0x40L to "UNIFORM_BUFFER", 0x80L to "STORAGE_BUFFER",
        0x100L to "STORAGE_BUFFER_ATOMIC", 0x200L to "VERTEX_BUFFER", 0x400L to "COLOR_ATTACHMENT", 0x800L to "COLOR_ATTACHMENT_BLEND",
        0x1000L to "DEPTH_STENCIL_ATTACHMENT", 0x2000L to "BLIT_SRC", 0x4000L to "BLIT_DST", 0x8000L to "SAMPLED_IMAGE_FILTER_LINEAR",
        0x10000L to "TRANSFER_SRC", 0x20000L to "TRANSFER_DST", 0x40000L to "MIDPOINT_CHROMA_SAMPLES", 0x80000L to "SAMPLED_IMAGE_YCBCR_CONVERSION_LINEAR_FILTER",
        0x100000L to "SAMPLED_IMAGE_YCBCR_CONVERSION_SEPARATE_RECONSTRUCTION_FILTER", 0x200000L to "SAMPLED_IMAGE_YCBCR_CONVERSION_CHROMA_RECONSTRUCTION_EXPLICIT", 0x400000L to "SAMPLED_IMAGE_YCBCR_CONVERSION_CHROMA_RECONSTRUCTION_EXPLICIT_FORCEABLE",
        0x800000L to "DISJOINT", 0x1000000L to "COSITED_CHROMA_SAMPLES"
    )
    val known = names.filter { (bit, _) -> (bits and bit) != 0L }.map { it.second }
    return known.joinToString(" | ").ifBlank { "NONE" } + rawBitsSuffix(bits, names.map { it.first }.toSet())
}

private data class ProfileResult(val name: String, val revision: String, val status: String, val missingExtensions: List<String>, val missingFeatures: List<String>, val unknownFeatures: List<String>, val failingLimits: List<String>, val unknownLimits: List<String>, val failingBooleanLimits: List<String> = emptyList(), val unknownBooleanLimits: List<String> = emptyList())

private data class ProfileRequirements(
    val name: String,
    val revision: String,
    val minApiMinor: Int,
    val extensions: List<String>,
    val features: List<String>,
    val limits: Map<String, Long>,
    val booleanLimits: Map<String, Boolean> = emptyMap()
)

private fun vulkanProfileRequirements(): List<ProfileRequirements> = listOf(
    ProfileRequirements(
        "VP_ANDROID_baseline_2022", "r.2", 1,
        listOf("VK_KHR_android_surface", "VK_ANDROID_external_memory_android_hardware_buffer", "VK_KHR_swapchain", "VK_KHR_maintenance1", "VK_KHR_maintenance2", "VK_KHR_maintenance3", "VK_KHR_dedicated_allocation"),
        emptyList(), emptyMap()
    ),
    ProfileRequirements(
        "VP_KHR_roadmap_2022", "r.1", 3,
        listOf("VK_KHR_global_priority", "VK_KHR_sampler_ycbcr_conversion", "VK_KHR_timeline_semaphore", "VK_KHR_buffer_device_address", "VK_KHR_vulkan_memory_model", "VK_KHR_synchronization2", "VK_KHR_dynamic_rendering"),
        listOf("robustBufferAccess", "fullDrawIndexUint32", "imageCubeArray", "independentBlend", "sampleRateShading", "drawIndirectFirstInstance", "depthClamp", "depthBiasClamp", "samplerAnisotropy", "occlusionQueryPrecise", "fragmentStoresAndAtomics", "shaderStorageImageExtendedFormats", "shaderUniformBufferArrayDynamicIndexing", "shaderSampledImageArrayDynamicIndexing", "shaderStorageBufferArrayDynamicIndexing", "shaderStorageImageArrayDynamicIndexing", "samplerYcbcrConversion", "subgroupSize", "timelineSemaphore", "bufferDeviceAddress", "vulkanMemoryModel", "vulkanMemoryModelDeviceScope", "synchronization2", "dynamicRendering"),
        mapOf("maxImageDimension1D" to 8192, "maxImageDimension2D" to 8192, "maxImageDimensionCube" to 8192, "maxImageArrayLayers" to 2048, "maxUniformBufferRange" to 65536, "bufferImageGranularity" to 4096, "maxPerStageDescriptorSamplers" to 64, "maxPerStageDescriptorUniformBuffers" to 15, "maxPerStageDescriptorStorageBuffers" to 30, "maxPerStageDescriptorSampledImages" to 200, "maxPerStageDescriptorStorageImages" to 16, "maxPerStageResources" to 200, "maxDescriptorSetSamplers" to 576, "maxDescriptorSetUniformBuffers" to 90, "maxDescriptorSetStorageBuffers" to 96, "maxDescriptorSetSampledImages" to 1800, "maxDescriptorSetStorageImages" to 144, "maxFragmentCombinedOutputResources" to 16, "maxComputeWorkGroupInvocations" to 256, "subTexelPrecisionBits" to 8, "mipmapPrecisionBits" to 6),
        mapOf("standardSampleLocations" to true, "timestampComputeAndGraphics" to true)
    ),
    ProfileRequirements(
        "VP_KHR_roadmap_2024", "r.1", 3,
        listOf("VK_KHR_dynamic_rendering_local_read", "VK_KHR_load_store_op_none", "VK_KHR_shader_quad_control", "VK_KHR_shader_maximal_reconvergence", "VK_KHR_shader_subgroup_uniform_control_flow", "VK_KHR_shader_subgroup_rotate", "VK_KHR_shader_float_controls2", "VK_KHR_shader_expect_assume", "VK_KHR_line_rasterization", "VK_KHR_vertex_attribute_divisor", "VK_KHR_index_type_uint8", "VK_KHR_map_memory2", "VK_KHR_maintenance5", "VK_KHR_push_descriptor"),
        listOf("multiDrawIndirect", "shaderInt16", "shaderImageGatherExtended", "shaderDrawParameters", "storageBuffer16BitAccess", "shaderInt8", "shaderFloat16", "storageBuffer8BitAccess", "dynamicRenderingLocalRead", "shaderQuadControl", "shaderMaximalReconvergence", "shaderSubgroupUniformControlFlow", "shaderSubgroupRotate", "shaderFloatControls2", "shaderExpectAssume", "rectangularLines", "bresenhamLines", "smoothLines", "stippledRectangularLines", "stippledBresenhamLines", "stippledSmoothLines", "vertexAttributeInstanceRateDivisor", "indexTypeUint8", "maintenance5", "pushDescriptor"),
        mapOf("maxColorAttachments" to 8, "maxBoundDescriptorSets" to 7),
        mapOf("timestampComputeAndGraphics" to true)
    ),
    ProfileRequirements(
        "VP_KHR_roadmap_2026", "r.1", 4,
        listOf("VK_KHR_robustness2", "VK_KHR_pipeline_binary", "VK_KHR_fragment_shading_rate", "VK_KHR_shader_clock", "VK_KHR_workgroup_memory_explicit_layout", "VK_KHR_compute_shader_derivatives", "VK_KHR_maintenance7", "VK_KHR_maintenance8", "VK_KHR_maintenance9", "VK_KHR_depth_clamp_zero_one", "VK_KHR_copy_memory_indirect", "VK_KHR_shader_untyped_pointers", "VK_KHR_surface", "VK_KHR_swapchain", "VK_KHR_get_surface_capabilities2", "VK_KHR_present_mode_fifo_latest_ready", "VK_KHR_present_id2", "VK_KHR_present_wait2", "VK_KHR_surface_maintenance1", "VK_KHR_swapchain_maintenance1", "VK_KHR_cooperative_matrix"),
        listOf("hostImageCopy", "pushDescriptor", "robustBufferAccess2", "robustImageAccess2", "nullDescriptor", "pipelineBinaries", "pipelineFragmentShadingRate", "shaderSubgroupClock", "workgroupMemoryExplicitLayout", "computeDerivativeGroupLinear", "maintenance7", "maintenance8", "maintenance9", "depthClampZeroOne", "indirectMemoryCopy", "shaderUntypedPointers", "presentModeFifoLatestReady", "presentId2", "presentWait2", "swapchainMaintenance1", "cooperativeMatrix"),
        mapOf("maxPerStageDescriptorUniformBuffers" to 200, "maxPerStageDescriptorStorageBuffers" to 200, "maxPerStageDescriptorInputAttachments" to 8, "maxDescriptorSetStorageBuffers" to 1800, "maxDescriptorSetUniformBuffers" to 1800, "maxDescriptorSetInputAttachments" to 8, "maxVertexOutputComponents" to 124, "maxTessellationControlPerVertexInputComponents" to 128, "maxTessellationControlPerVertexOutputComponents" to 128, "maxTessellationControlTotalOutputComponents" to 4096, "maxTessellationEvaluationInputComponents" to 128, "maxTessellationEvaluationOutputComponents" to 128, "maxGeometryOutputComponents" to 128, "maxFragmentInputComponents" to 112, "maxFragmentOutputAttachments" to 8, "maxComputeSharedMemorySize" to 32768, "subPixelPrecisionBits" to 8, "maxFramebufferWidth" to 8192, "maxFramebufferHeight" to 8192)
    )
)

private fun vulkanProfileCatalog(): List<Pair<String, String>> = listOf(
    "VP_LUNARG_minimum_requirements_1_3" to "r.1",
    "VP_LUNARG_minimum_requirements_1_2" to "r.1",
    "VP_LUNARG_minimum_requirements_1_1" to "r.1",
    "VP_LUNARG_minimum_requirements_1_0" to "r.1",
    "VP_LUNARG_desktop_baseline_2024" to "r.1",
    "VP_LUNARG_desktop_baseline_2023" to "r.2",
    "VP_LUNARG_desktop_baseline_2022" to "r.2",
    "VP_KHR_roadmap_2026" to "r.1",
    "VP_KHR_roadmap_2024" to "r.1",
    "VP_KHR_roadmap_2022" to "r.1",
    "VP_ANDROID_baseline_2022" to "r.2",
    "VP_ANDROID_baseline_2021" to "r.3",
    "VP_ANDROID_16_minimums" to "r.1",
    "VP_ANDROID_15_minimums" to "r.1"
)

private fun normalizedFeatureName(name: String): String = name.substringAfter("·").trim()

private fun numericLimit(limits: List<Pair<String, String>>, key: String): Long? {
    val value = limits.firstOrNull { it.first == key }?.second ?: return null
    return Regex("-?\\d+").find(value)?.value?.toLongOrNull()
}

private fun evaluateProfile(report: VulkanReport?, device: DeviceReport?, requirements: ProfileRequirements): ProfileResult {
    if (device == null) return ProfileResult(requirements.name, requirements.revision, "UNKNOWN", emptyList(), emptyList(), requirements.features, emptyList(), requirements.limits.keys.toList(), emptyList(), requirements.booleanLimits.keys.toList())
    val extensions = buildSet {
        addAll(report?.instanceExtensions?.map { it.name } ?: emptyList())
        addAll(device.extensions.map { it.name })
    }
    val missingExtensions = requirements.extensions.filterNot { it in extensions }
    val supportedFeatures = device.features.filter { it.supported }.map { normalizedFeatureName(it.name) }.toSet()
    val knownFeatures = device.features.map { normalizedFeatureName(it.name) }.toSet()
    val missingFeatures = requirements.features.filter { it in knownFeatures && it !in supportedFeatures }
    val unknownFeatures = requirements.features.filterNot { it in knownFeatures }
    val failingLimits = mutableListOf<String>()
    val unknownLimits = mutableListOf<String>()
    for ((name, required) in requirements.limits) {
        val actual = numericLimit(device.limits, name)
        when {
            actual == null -> unknownLimits += "$name >= $required"
            actual < required -> failingLimits += "$name=$actual < $required"
        }
    }
    val failingBooleanLimits = mutableListOf<String>()
    val unknownBooleanLimits = mutableListOf<String>()
    for ((name, required) in requirements.booleanLimits) {
        val value = device.limits.firstOrNull { it.first == name }?.second
        val actual = value?.trim()?.equals("true", true)
        when {
            actual == null -> unknownBooleanLimits += "$name = $required"
            actual != required -> failingBooleanLimits += "$name=$actual != $required"
        }
    }
    val apiOk = apiAtLeast(device.apiVersion, 1, requirements.minApiMinor)
    val status = when {
        !apiOk || missingExtensions.isNotEmpty() || missingFeatures.isNotEmpty() || failingLimits.isNotEmpty() || failingBooleanLimits.isNotEmpty() -> "FAIL"
        unknownFeatures.isNotEmpty() || unknownLimits.isNotEmpty() || unknownBooleanLimits.isNotEmpty() -> "UNKNOWN"
        else -> "PASS"
    }
    return ProfileResult(requirements.name, requirements.revision, status, missingExtensions, missingFeatures, unknownFeatures, failingLimits, unknownLimits, failingBooleanLimits, unknownBooleanLimits)
}

private fun profileSummary(result: ProfileResult): String = buildString {
    append(result.status)
    if (result.missingExtensions.isNotEmpty()) append(" · ${result.missingExtensions.size} missing extension(s)")
    if (result.missingFeatures.isNotEmpty()) append(" · ${result.missingFeatures.size} unsupported feature(s)")
    if (result.failingLimits.isNotEmpty()) append(" · ${result.failingLimits.size} failing limit(s)")
    if (result.unknownFeatures.isNotEmpty()) append(" · ${result.unknownFeatures.size} feature query(ies) unavailable")
    if (result.unknownLimits.isNotEmpty()) append(" · ${result.unknownLimits.size} limit(s) unavailable")
    if (result.failingBooleanLimits.isNotEmpty()) append(" · ${result.failingBooleanLimits.size} boolean limit failure(s)")
    if (result.unknownBooleanLimits.isNotEmpty()) append(" · ${result.unknownBooleanLimits.size} boolean limit(s) unavailable")
}

private fun videoCodecOperationsName(bits: Long): String {
    val names = buildList {
        if ((bits and 0x00000001L) != 0L) add("decode H.264")
        if ((bits and 0x00000002L) != 0L) add("decode H.265")
        if ((bits and 0x00000004L) != 0L) add("decode AV1")
        if ((bits and 0x00000008L) != 0L) add("decode VP9")
        if ((bits and 0x00010000L) != 0L) add("encode H.264")
        if ((bits and 0x00020000L) != 0L) add("encode H.265")
        if ((bits and 0x00040000L) != 0L) add("encode AV1")
    }
    return if (names.isEmpty()) "None reported" else names.joinToString(", ")
}

@Composable
private fun ProfilesPage(report: VulkanReport, device: DeviceReport?) {
    var query by remember { mutableStateOf("") }
    val profiles = remember { vulkanProfileRequirements() }
    val results = remember(report, device) { profiles.map { evaluateProfile(report, device, it) } }
    val filtered = results.filter { it.name.contains(query, true) }
    LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Text("Profile support is evaluated only from runtime Vulkan values. Missing query data is UNKNOWN, never inferred as unsupported.", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
            OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, placeholder = { Text("Search profiles…") })
        }
        items(filtered) { result ->
            val req = profiles.first { it.name == result.name }
            Card(colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFF111111)), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(result.name, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                        Text(result.status, fontWeight = FontWeight.Bold, color = when (result.status) { "PASS" -> ComposeColor(0xFF73C991); "FAIL" -> ComposeColor(0xFFFF6B6B); else -> ComposeColor(0xFFFFC857) })
                    }
                    Text("${req.revision} · Vulkan ${req.minApiMinor}.0 minimum", color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.labelMedium)
                    Text(profileSummary(result), style = MaterialTheme.typography.bodySmall)
                    if (result.missingExtensions.isNotEmpty()) KeyValue("Missing extensions", result.missingExtensions.joinToString(", "))
                    if (result.missingFeatures.isNotEmpty()) KeyValue("Unsupported features", result.missingFeatures.joinToString(", "))
                    if (result.unknownFeatures.isNotEmpty()) KeyValue("Unavailable feature queries", result.unknownFeatures.joinToString(", "))
                    if (result.failingLimits.isNotEmpty()) KeyValue("Failing limits", result.failingLimits.joinToString("; "))
                    if (result.unknownLimits.isNotEmpty()) KeyValue("Unavailable limits", result.unknownLimits.joinToString(", "))
                    if (result.failingBooleanLimits.isNotEmpty()) KeyValue("Failing boolean limits", result.failingBooleanLimits.joinToString("; "))
                    if (result.unknownBooleanLimits.isNotEmpty()) KeyValue("Unavailable boolean limits", result.unknownBooleanLimits.joinToString(", "))
                }
            }
        }
    }
}

@Composable
private fun InfoPage(registryCoverage: RegistryCoverage) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val installedAbi = remember { detectInstalledAbi(context) }
    val packageInfo = remember {
        runCatching { context.packageManager.getPackageInfo(context.packageName, 0) }.getOrNull()
    }
    val versionName = packageInfo?.versionName ?: "Unknown"
    val versionCode = if (packageInfo != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageInfo.longVersionCode.toString()
    } else {
        @Suppress("DEPRECATION")
        (packageInfo?.versionCode?.toString() ?: "Unknown")
    }
    LazyColumn(
        contentPadding = WindowInsets.navigationBars.asPaddingValues(),
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionCard("Developer") {
                KeyValue("Developer", "Semih Boran")
                KeyValue("Nickname", "EFI Shell")
                Button(
                    onClick = {
                        runCatching {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/EFIShell0")))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("GitHub: github.com/EFIShell0")
                }
            }
        }
        item {
            SectionCard("Application") {
                KeyValue("Application", "VulkanScope")
                KeyValue("Version", versionName)
                KeyValue("Version code", versionCode)
                KeyValue("Package", context.packageName)
                KeyValue("Installed ABI", installedAbi)
            }
        }
        item {
            SectionCard("Device ABI") {
                KeyValue("Supported ABIs", Build.SUPPORTED_ABIS.joinToString(", "))
                Text(
                    "Installed ABI is the native ABI used by this VulkanScope installation; supported ABIs are the ABIs reported by Android for the device.",
                    color = ComposeColor(0xFF8F8F8F),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        item {
            SectionCard("Android") {
                KeyValue("Manufacturer", Build.MANUFACTURER)
                KeyValue("Model", Build.MODEL)
                KeyValue("Android", Build.VERSION.RELEASE)
                KeyValue("SDK", Build.VERSION.SDK_INT.toString())
            }
        }
        item {
            SectionCard("Vulkan registry / query engine") {
                KeyValue("Baseline", registryCoverage.baseline)
                KeyValue("Engine", registryCoverage.mode)
                KeyValue("Physical-device structs", registryCoverage.implementedPhysicalDeviceStructCount.toString())
                KeyValue("Validated query groups", registryCoverage.validatedRuntimeQueryGroupCount.toString())
                KeyValue("Runtime registry tokens", registryCoverage.runtimeExtensionTokenCount.toString())
                KeyValue("Catalog schema", registryCoverage.catalogSchemaVersion.toString())
                KeyValue("Report schema", registryCoverage.reportSchema)
                KeyValue("Header baseline", registryCoverage.headerBaseline)
                KeyValue("Instance dependency candidates", registryCoverage.instanceDependencyCandidateCount.toString())
                Text("Registry metadata is build-time/offline. Runtime does not download or parse the Khronos registry. Unknown structures remain unavailable unless a validated native query path exists.", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
            }
        }
        item {
            SectionCard("About") {
                Text(
                    "VulkanScope is a Vulkan capability and device inspection utility. It reports information exposed by the Android Vulkan implementation and the selected driver mode.",
                    color = ComposeColor(0xFFB0B0B0)
                )
            }
        }
    }
}

private fun detectInstalledAbi(context: Context): String {
    val nativeDir = context.applicationInfo.nativeLibraryDir.orEmpty().lowercase()
    return when {
        nativeDir.contains("arm64") -> "arm64-v8a"
        nativeDir.contains("armeabi-v7a") || nativeDir.endsWith("/arm") -> "armeabi-v7a"
        nativeDir.contains("x86_64") -> "x86_64"
        nativeDir.contains("x86") -> "x86"
        else -> Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown"
    }
}


private fun turnipSupportDescription(support: TurnipSupport): String = when (support) {
    TurnipSupport.UNKNOWN -> "Checking the installed Vulkan implementation; Qualcomm Adreno support is not yet known."
    TurnipSupport.SUPPORTED -> "Uses an installed AdrenoTools-compatible Turnip driver."
    TurnipSupport.UNSUPPORTED -> "Unavailable: this device does not expose a supported arm64-v8a Qualcomm Adreno Vulkan device."
}

@Composable
private fun SettingsPage(report: VulkanReport, display: DisplayReport, mode: DriverMode, turnipSupport: TurnipSupport, onModeChanged: (DriverMode) -> Unit, onInstallDriverBundle: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val exportStem = remember(report) { exportFileStem(report) }
    val pendingLegacyExport = remember { mutableStateOf<ExportPayload?>(null) }
    val legacyPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        val pending = pendingLegacyExport.value
        pendingLegacyExport.value = null
        if (pending == null) return@rememberLauncherForActivityResult
        if (granted) {
            writeExportToDownloads(context, pending.filename, pending.content, pending.mime)
        } else {
            android.widget.Toast.makeText(context, "Storage permission is required to save the report.", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
    val textLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        writeExport(context, uri, reportToText(context, report, display, mode), "text/plain")
    }
    val htmlLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/html")) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        writeExport(context, uri, reportToHtml(context, report, display, mode), "text/html")
    }
    fun exportDocument(filename: String, content: String, mime: String, launcher: ActivityResultLauncher<String>) {
        exportWithSafOrDownloads(context, filename, content, mime, launcher) { payload ->
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                pendingLegacyExport.value = payload
                legacyPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else {
                writeExportToDownloads(context, payload.filename, payload.content, payload.mime)
            }
        }
    }
    val bundleDir = File(context.filesDir, "turnip")
    val bundleInstalled = bundleDir.exists() && bundleDir.walkTopDown().any { it.isFile && it.extension.equals("so", true) }
    LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { SectionCard("Vulkan driver") {
            Text("Choose which Vulkan driver source VulkanScope should request. Driver inspection runs in an isolated probe process, so changing drivers does not restart the UI process.", color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.bodySmall)
            DriverOption(DriverMode.SYSTEM, mode == DriverMode.SYSTEM, "Uses Android's system Vulkan loader/driver.", enabled = true) { onModeChanged(DriverMode.SYSTEM) }
            DriverOption(DriverMode.TURNIP, mode == DriverMode.TURNIP, turnipSupportDescription(turnipSupport), enabled = turnipSupport == TurnipSupport.SUPPORTED) { onModeChanged(DriverMode.TURNIP) }
            if (turnipSupport == TurnipSupport.SUPPORTED) {
                Text("Turnip requires an AdrenoTools-compatible driver package (meta.json + Vulkan .so).", color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.bodySmall)
            }
        } }
        if (turnipSupport == TurnipSupport.SUPPORTED && mode == DriverMode.TURNIP) {
            item { SectionCard("Turnip / third-party driver bundle") {
                KeyValue("Status", if (bundleInstalled) "Bundle installed" else "Not installed")
                Button(onClick = onInstallDriverBundle, modifier = Modifier.fillMaxWidth()) { Text("Import driver ZIP") }
                Text("Import an AdrenoTools-compatible Turnip ZIP. The driver remains inside VulkanScope's private storage; Android's system Vulkan driver is never replaced.", color = ComposeColor(0xFF777777), style = MaterialTheme.typography.bodySmall)
            } }
        }
        item { SectionCard("Export complete report") {
            Text("Export the complete currently collected VulkanScope report, including device properties, detailed Core 1.1/1.2/1.3/1.4 properties when available, features, memory, queues, formats, surface data, extensions, layers and Android/display information.", color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.bodySmall)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { exportDocument("${exportStem}.txt", reportToText(context, report, display, mode), "text/plain", textLauncher) }, modifier = Modifier.weight(1f)) { Text("Export TXT") }
                Button(onClick = { exportDocument("${exportStem}.html", reportToHtml(context, report, display, mode), "text/html", htmlLauncher) }, modifier = Modifier.weight(1f)) { Text("Export HTML") }
            }
        } }
        item { SectionCard("Important") {
            Text("VulkanScope uses libadrenotools for rootless driver loading on modern Android. This avoids the libhardware.so / linker-namespace problem of directly loading libvulkan_freedreno.so.", color = ComposeColor(0xFFAAAAAA), style = MaterialTheme.typography.bodySmall)
        } }
    }
}

@Composable
private fun DriverOption(option: DriverMode, selected: Boolean, description: String, enabled: Boolean, onClick: () -> Unit) {
    val textColor = if (enabled) ComposeColor(0xFFFFFFFF) else ComposeColor(0xFF666666)
    Card(onClick = onClick, enabled = enabled, colors = CardDefaults.cardColors(containerColor = if (selected) ComposeColor(0xFF1D1113) else ComposeColor(0xFF111111)), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            androidx.compose.material3.RadioButton(selected = selected, enabled = enabled, onClick = onClick)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(option.label, color = textColor, fontWeight = FontWeight.SemiBold)
                Text(description, color = if (enabled) ComposeColor(0xFF8F8F8F) else ComposeColor(0xFF555555), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun ExtensionsPage(report: VulkanReport, device: DeviceReport?) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("All") }
    val supported = remember(report, device) {
        (report.instanceExtensions + (device?.extensions ?: emptyList()))
            .sortedWith(compareBy<ExtensionEntry> { it.name }.thenBy { it.scope }.thenBy { it.specVersion })
    }
    val supportedNames = remember(supported) { supported.map { it.name }.toSet() }
    val catalog = remember(supportedNames, device?.deviceExtensionStatus) {
        if (device == null || device.deviceExtensionStatus == "available") KNOWN_VULKAN_EXTENSIONS.filterNot { it in supportedNames }.sorted() else emptyList()
    }
    val filteredSupported = remember(query, supported, filter) {
        if (filter == "Not enumerated") emptyList() else supported.filter { query.isBlank() || it.name.contains(query, true) || it.scope.contains(query, true) }
    }
    val filteredCatalog = remember(query, catalog, filter) {
        if (filter == "Supported") emptyList() else catalog.filter { query.isBlank() || it.contains(query, true) }
    }
    val total = filteredSupported.size + filteredCatalog.size
    LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
        item {
            Text("Runtime-enumerated extensions are shown exactly as reported. Registry references are never labeled unsupported.", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
            if (device != null && device.deviceExtensionStatus != "available") {
                Text("Device extension enumeration: ${device.deviceExtensionStatus.uppercase()}${if (device.deviceExtensionReason.isBlank()) "" else " — ${device.deviceExtensionReason}"}", color = ComposeColor(0xFFFFD76B), style = MaterialTheme.typography.bodySmall)
            }
            OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, placeholder = { Text("Search Vulkan extension names") })
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All", "Supported", "Not enumerated").forEach { value -> FilterChip(selected = filter == value, onClick = { filter = value }, label = { Text(value) }) }
            }
            Text("$total entries", modifier = Modifier.padding(vertical = 6.dp), color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.labelMedium)
        }
        items(filteredSupported, key = { "supported:${it.scope}:${it.name}:${it.specVersion}" }) { extension ->
            Card(colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFF111111)), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(extension.name, fontWeight = FontWeight.SemiBold)
                    Text("SUPPORTED · ${extension.scope} · spec ${extension.specVersion}", color = ComposeColor(0xFF73C991), style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        items(filteredCatalog, key = { "catalog:$it" }) { name ->
            Card(colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFF17130A)), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(name, fontWeight = FontWeight.SemiBold)
                    Text("NOT ENUMERATED · registry reference only", color = ComposeColor(0xFFFFC857), style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        if (total == 0) item { EmptyState("No matching extensions") }
    }
}


private fun exportFileStem(report: VulkanReport): String {
    val gpuName = report.devices.firstOrNull()?.name?.trim().orEmpty().ifBlank { "Unknown-GPU" }
    val safeGpu = gpuName.replace(Regex("[^A-Za-z0-9._-]+"), "_").trim('_').ifBlank { "Unknown-GPU" }
    return "VulkanScope-${safeGpu}-report"
}

private data class ExportPayload(val filename: String, val content: String, val mime: String)

private fun isTvDevice(context: Context): Boolean {
    val uiModeType = context.resources.configuration.uiMode and Configuration.UI_MODE_TYPE_MASK
    return context.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK) || uiModeType == Configuration.UI_MODE_TYPE_TELEVISION
}

private fun exportWithSafOrDownloads(context: Context, filename: String, content: String, mime: String, launcher: ActivityResultLauncher<String>, onDownloadsFallback: (ExportPayload) -> Unit) {
    val payload = ExportPayload(filename, content, mime)
    if (isTvDevice(context)) {
        onDownloadsFallback(payload)
        return
    }
    runCatching { launcher.launch(filename) }
        .onFailure { error ->
            Log.w("VulkanScope", "Document picker unavailable; using Downloads", error)
            onDownloadsFallback(payload)
        }
}

private fun writeExportToDownloads(context: Context, filename: String, content: String, mime: String) {
    val success = runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = android.content.ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, filename)
                put(MediaStore.Downloads.MIME_TYPE, mime)
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: throw IllegalStateException("Unable to create the Downloads entry")
            try {
                context.contentResolver.openOutputStream(uri)?.use { it.write(content.toByteArray(Charsets.UTF_8)) }
                    ?: throw IllegalStateException("Unable to open the Downloads entry")
                values.clear()
                values.put(MediaStore.Downloads.IS_PENDING, 0)
                context.contentResolver.update(uri, values, null, null)
            } catch (error: Throwable) {
                context.contentResolver.delete(uri, null, null)
                throw error
            }
        } else {
            val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!directory.exists() && !directory.mkdirs()) throw IllegalStateException("Unable to create the Downloads directory")
            val target = uniqueDownloadFile(directory, filename)
            FileOutputStream(target).use { it.write(content.toByteArray(Charsets.UTF_8)) }
        }
    }.isSuccess
    val message = if (success) {
        if (mime == "text/html") "HTML report saved to Downloads" else "TXT report saved to Downloads"
    } else {
        if (mime == "text/html") "HTML report could not be saved" else "TXT report could not be saved"
    }
    android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
}

private fun uniqueDownloadFile(directory: File, filename: String): File {
    val original = File(directory, filename)
    if (!original.exists()) return original
    val dot = filename.lastIndexOf('.')
    val base = if (dot > 0) filename.substring(0, dot) else filename
    val extension = if (dot > 0) filename.substring(dot) else ""
    var index = 2
    while (true) {
        val candidate = File(directory, "${base} (${index})${extension}")
        if (!candidate.exists()) return candidate
        index++
    }
}

private fun writeExport(context: Context, uri: Uri, content: String, mime: String) {
    val success = runCatching {
        context.contentResolver.openOutputStream(uri)?.use { it.write(content.toByteArray(Charsets.UTF_8)) }
            ?: throw IllegalStateException("Unable to open the selected destination")
    }.isSuccess
    val message = if (success) {
        if (mime == "text/html") "HTML report saved successfully" else "TXT report saved successfully"
    } else {
        if (mime == "text/html") "HTML report could not be saved" else "TXT report could not be saved"
    }
    android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
}

private fun reportToText(context: Context, report: VulkanReport, display: DisplayReport, mode: DriverMode): String = buildString {
    val packageInfo = runCatching { context.packageManager.getPackageInfo(context.packageName, 0) }.getOrNull()
    val appVersionName = packageInfo?.versionName ?: "Unknown"
    val appVersionCode = if (packageInfo != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageInfo.longVersionCode.toString()
    } else {
        @Suppress("DEPRECATION") packageInfo?.versionCode?.toString() ?: "Unknown"
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
    appendLine("GPU: ${report.devices.firstOrNull()?.name ?: "Unknown"}")
    appendLine("Driver mode: ${mode.label}")
    appendLine("Loader / instance API: ${report.loaderVersion}")
    appendLine("Display: ${display.resolution} @ ${display.refreshRate}")
    appendLine("Wide gamut: ${display.wideGamut}")
    appendLine("Preferred wide gamut: ${display.preferredWideGamut}")
    appendLine("HDR types: ${display.hdrTypes.joinToString(", ")}")
    appendLine("HDR luminance: min=${display.minLuminance}, max=${display.maxLuminance}, average=${display.averageLuminance}")
    appendLine("Android: ${Build.MANUFACTURER} ${Build.MODEL}, ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
    appendLine("Codename=${Build.VERSION.CODENAME}, Security patch=${Build.VERSION.SECURITY_PATCH}")
    appendLine("Brand=${Build.BRAND}, Product=${Build.PRODUCT}, Device=${Build.DEVICE}, Board=${Build.BOARD}, Hardware=${Build.HARDWARE}")
    appendLine("Build ID=${Build.ID}, Incremental=${Build.VERSION.INCREMENTAL}")
    appendLine("Fingerprint=${Build.FINGERPRINT}")
    appendLine("Supported device ABIs: $supportedDeviceAbis")
    if (report.error != null) appendLine("Report error: ${report.error}")
    appendLine()
    appendLine("VULKAN REGISTRY COVERAGE")
    appendLine("Baseline=${report.registryCoverage.baseline}")
    appendLine("Mode=${report.registryCoverage.mode}")
    appendLine("Implemented physical-device structs=${report.registryCoverage.implementedPhysicalDeviceStructCount}")
    appendLine("Validated runtime query groups=${report.registryCoverage.validatedRuntimeQueryGroupCount}")
    appendLine("Runtime extension tokens=${report.registryCoverage.runtimeExtensionTokenCount}")
    appendLine("Catalog schema=${report.registryCoverage.catalogSchemaVersion}")
    appendLine("Report schema=${report.registryCoverage.reportSchema}")
    appendLine("Header baseline=${report.registryCoverage.headerBaseline}")
    appendLine("Instance dependency candidates=${report.registryCoverage.instanceDependencyCandidateCount}")
    appendLine("Validated groups=${report.registryCoverage.validatedRuntimeQueryGroups.joinToString(", ")}")
    appendLine()
    appendLine("INSTANCE LAYERS")
    report.instanceLayers.forEach { appendLine("${it.name} | spec ${it.specVersion} | implementation ${it.implementationVersion} | ${it.description}") }
    appendLine()
    appendLine("INSTANCE EXTENSIONS")
    report.instanceExtensions.forEach { appendLine("${it.name} | spec ${it.specVersion}") }
    appendLine(); appendLine("VULKAN PROFILE EVALUATION")
    report.devices.forEachIndexed { index, d ->
        appendLine("Device #${index + 1}: ${d.name}")
        vulkanProfileRequirements().map { evaluateProfile(report, d, it) }.forEach { p -> appendLine("${p.name} | ${p.revision} | ${p.status} | ${profileSummary(p)}") }
    }
        appendLine(); appendLine("VULKAN PROFILES CATALOG")
    vulkanProfileCatalog().forEach { appendLine("${it.first} | ${it.second}") }
        report.devices.forEachIndexed { index, d ->
        appendLine(); appendLine("DEVICE #${index + 1}: ${d.name}")
        appendLine("API: ${d.apiVersion}"); appendLine("Driver version: ${d.driverVersionText}"); appendLine("Vendor: ${d.vendorId}"); appendLine("Device ID: ${d.deviceId}"); appendLine("Type: ${d.deviceType}"); appendLine("Extended query status: ${d.extendedQueryStatus}"); appendLine("Extended query reason: ${d.extendedQueryReason}"); appendLine("Vulkan 1.4 status: ${d.vulkan14Status}"); appendLine("Vulkan 1.4 reason: ${d.vulkan14Reason}"); appendLine("Device extension enumeration status: ${d.deviceExtensionStatus}"); appendLine("Device extension enumeration reason: ${d.deviceExtensionReason}")
        appendLine(); appendLine("DEVICE LAYERS"); d.deviceLayers.forEach { appendLine("${it.name} | spec ${it.specVersion} | implementation ${it.implementationVersion} | ${it.description}"); it.extensions.forEach { ext -> appendLine("  ${ext.name} | spec ${ext.specVersion}") } }; appendLine(); appendLine("DEVICE EXTENSIONS"); d.extensions.forEach { appendLine("${it.name} | ${it.scope} | spec ${it.specVersion}") }
        appendLine(); appendLine("FEATURES"); d.features.forEach { appendLine("${it.name} = ${it.supported}") }
        appendLine(); appendLine("DETAILED QUERY RESULTS (${d.detailedProperties.size} results; ${d.detailedProperties.map { "${it.section} / ${it.name}" }.distinct().size} unique report fields)"); d.detailedProperties.forEach { appendLine("[${it.section}] ${it.name} = ${it.value}") }
        appendLine(); appendLine("LIMITS"); d.limits.forEach { appendLine("${it.first} = ${it.second}") }
        appendLine(); appendLine("MEMORY HEAPS"); d.heaps.forEach { appendLine("Heap ${it.index}: ${formatBytes(it.size)} | flags ${it.flags}") }
        appendLine(); appendLine("MEMORY TYPES"); d.memoryTypes.forEach { appendLine("Type ${it.index}: heap ${it.heap} | flags ${it.flags}") }
        appendLine(); appendLine("QUEUES"); d.queues.forEach { appendLine("Family ${it.index}: count=${it.count}, timestampBits=${it.timestampBits}, flags=${it.flags}, graphics=${it.graphics}, compute=${it.compute}, transfer=${it.transfer}, sparse=${it.sparse}, protected=${it.protected}, videoDecode=${it.videoDecode}, videoEncode=${it.videoEncode}, opticalFlow=${it.opticalFlow}, dataGraph=${it.dataGraph}, unknownFlags=0x${it.unknownFlags.toString(16).uppercase()}, granularity=${it.granularity}") }
        appendLine(); appendLine("FORMATS"); d.formats.forEach { appendLine("${it.name}: ${if (it.supported) "SUPPORTED" else "NOT SUPPORTED"}, linear=${it.linear}, optimal=${it.optimal}, buffer=${it.buffer}") }
        appendLine(); appendLine("SURFACE")
        appendLine("Available=${d.surfaceAvailable}, presentation=${d.surfacePresentationSupported}")
        d.surfaceCapabilities.forEach { appendLine("${it.first} = ${it.second}") }
        d.surfaceFormats.forEach { appendLine("${it.format} | ${it.colorSpace} | ${it.classification} | ${if (it.supported) "SUPPORTED" else "NOT SUPPORTED"} | ${it.description}") }
        appendLine("Present modes: ${d.presentModes.joinToString(", ")}")
        d.presentationQueues.forEach { appendLine("Queue ${it.first}: present=${it.second}") }
    }
}

private fun htmlEscape(value: String): String = value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")

private fun reportToHtml(context: Context, report: VulkanReport, display: DisplayReport, mode: DriverMode): String = buildString {
    val packageInfo = runCatching { context.packageManager.getPackageInfo(context.packageName, 0) }.getOrNull()
    val appVersionName = packageInfo?.versionName ?: "Unknown"
    val appVersionCode = if (packageInfo != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageInfo.longVersionCode.toString()
    } else {
        @Suppress("DEPRECATION") packageInfo?.versionCode?.toString() ?: "Unknown"
    }
    val applicationAbi = detectInstalledAbi(context)
    val supportedDeviceAbis = Build.SUPPORTED_ABIS.joinToString(", ")
    val logoData = runCatching {
        context.resources.openRawResource(R.drawable.vulkanscope_logo_horizontal).use { input ->
            Base64.encodeToString(input.readBytes(), Base64.NO_WRAP)
        }
    }.getOrNull()
    append("<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"><title>VulkanScope report</title>")
    append("<style>body{font-family:Inter,system-ui,-apple-system,BlinkMacSystemFont,\"Segoe UI\",sans-serif;background:#0a0a0b;color:#f4f4f5;margin:0;line-height:1.45}.wrap{max-width:1320px;margin:0 auto;padding:28px}.hero{background:linear-gradient(135deg,#1a1517,#0f1012);border:1px solid #2d2d31;border-radius:26px;padding:30px;box-shadow:0 16px 50px rgba(0,0,0,.28)}h1{margin:0 0 8px;font-size:36px}h2{margin:0 0 14px;font-size:22px}.muted{color:#a7a7ae}.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(210px,1fr));gap:12px;margin-top:18px}.metric{background:#121214;border:1px solid #292a2e;border-radius:17px;padding:14px}.section{margin-top:24px;background:#111113;border:1px solid #292a2e;border-radius:22px;padding:18px;overflow:auto}.section h2{position:sticky;left:0}table{border-collapse:collapse;width:100%;min-width:660px}td,th{border-bottom:1px solid #28282c;padding:10px 8px;text-align:left;vertical-align:top}th{color:#cbcad0;font-weight:600}.badge{display:inline-block;border-radius:999px;padding:3px 9px;font-size:11px;font-weight:800;letter-spacing:.03em}.yes{background:#133b28;color:#74e2a6}.no{background:#49171c;color:#ff8f98}.neutral{background:#403713;color:#ffd76b}.unknown{background:#292a2f;color:#c6c6cc}.code{font-family:ui-monospace,SFMono-Regular,Menlo,monospace}.small{font-size:13px}.subtle{color:#7f8088}.github-link{color:#7f94d8;text-decoration:none;font-weight:600}.github-link:hover{color:#ff6b74;text-decoration:underline}.github-link:visited{color:#7f94d8}</style></head><body><div class=\"wrap\">")
    append("<div class=\"hero\">")
    if (logoData != null) {
        append("<div style=\"display:flex;align-items:center;justify-content:flex-start;margin-bottom:14px;\"><img src=\"data:image/png;base64,$logoData\" alt=\"VulkanScope\" style=\"display:block;width:min(522px,100%);height:auto;max-height:76px;object-fit:contain;object-position:left center;\"></div>")
    } else {
        append("<h1>VulkanScope</h1>")
    }
    append("<div class=\"muted\">Runtime Vulkan inspection report</div><div class=\"grid\">")
    fun metric(label: String, value: String) { append("<div class=\"metric\"><div class=\"muted small\">${htmlEscape(label)}</div><strong>${htmlEscape(value)}</strong></div>") }
    metric("GPU", report.devices.firstOrNull()?.name ?: "Unknown")
    metric("Driver", mode.label)
    metric("Loader / instance API", report.loaderVersion)
    metric("Display", "${display.resolution} @ ${display.refreshRate}")
    metric("HDR", display.hdrTypes.joinToString(", ").ifBlank { "Not exposed" })
    append("</div>")
    if (report.error != null) append("<p><span class=\"badge no\">ERROR</span> ${htmlEscape(report.error)}</p>")
    append("</div>")

    fun statusBadge(value: String): String {
        val lower = value.trim().lowercase().replace('_', ' ')
        val cls = when {
            lower == "true" || lower == "yes" || lower == "supported" || lower == "available" -> "yes"
            lower == "false" || lower == "no" || lower == "not supported" || lower == "unsupported" -> "no"
            lower.contains("unavailable") || lower.contains("not applicable") -> "neutral"
            lower.contains("unknown") -> "unknown"
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
        "Manufacturer" to htmlEscape(Build.MANUFACTURER), "Model" to htmlEscape(Build.MODEL), "Android" to htmlEscape(Build.VERSION.RELEASE),
        "SDK" to Build.VERSION.SDK_INT.toString(), "Resolution" to htmlEscape(display.resolution),
        "Refresh rate" to htmlEscape(display.refreshRate), "Wide gamut" to statusBadge(display.wideGamut.toString()),
        "Preferred wide gamut" to htmlEscape(display.preferredWideGamut), "HDR types" to htmlEscape(display.hdrTypes.joinToString(", ").ifBlank { "Not exposed" }),
        "HDR min luminance" to htmlEscape(display.minLuminance), "HDR max luminance" to htmlEscape(display.maxLuminance), "HDR average luminance" to htmlEscape(display.averageLuminance)
    ))

    table("Vulkan Registry Coverage", "<th>Property</th><th>Value</th>", listOf(
        "Baseline" to htmlEscape(report.registryCoverage.baseline), "Mode" to htmlEscape(report.registryCoverage.mode),
        "Implemented physical-device structs" to report.registryCoverage.implementedPhysicalDeviceStructCount.toString(),
        "Validated runtime query groups" to report.registryCoverage.validatedRuntimeQueryGroupCount.toString(),
        "Runtime extension tokens" to report.registryCoverage.runtimeExtensionTokenCount.toString(),
        "Catalog schema" to report.registryCoverage.catalogSchemaVersion.toString(), "Report schema" to htmlEscape(report.registryCoverage.reportSchema),
        "Header baseline" to htmlEscape(report.registryCoverage.headerBaseline), "Instance dependency candidates" to report.registryCoverage.instanceDependencyCandidateCount.toString(),
        "Validated groups" to htmlEscape(report.registryCoverage.validatedRuntimeQueryGroups.joinToString(", "))
    ))

    append("<div class=\"section\"><h2>Instance extensions</h2><table><thead><tr><th>Name</th><th>Scope / spec</th></tr></thead><tbody>")
    report.instanceExtensions.forEach { ext ->
        append("<tr><td class=\"code\">${htmlEscape(ext.name)}</td><td>${htmlEscape("${ext.scope} / ${ext.specVersion}")}</td></tr>")
    }
    append("</tbody></table></div>")

    report.devices.forEach { d ->
        append("<div class=\"section\"><h2>Device: ${htmlEscape(d.name)}</h2>")
        table("Device properties", "<th>Property</th><th>Value</th>", listOf(
            "API" to htmlEscape(d.apiVersion), "Driver version" to htmlEscape(d.driverVersionText), "Vendor" to htmlEscape(d.vendorId),
            "Device ID" to htmlEscape(d.deviceId), "Type" to htmlEscape(d.deviceType), "Extended query status" to statusBadge(d.extendedQueryStatus),
            "Extended query reason" to htmlEscape(d.extendedQueryReason), "Device extension enumeration status" to statusBadge(d.deviceExtensionStatus), "Device extension enumeration reason" to htmlEscape(d.deviceExtensionReason), "Vulkan 1.4 status" to statusBadge(d.vulkan14Status), "Vulkan 1.4 reason" to htmlEscape(d.vulkan14Reason)
        ))
        append("<div class=\"section\"><h2>Device extensions</h2><table><thead><tr><th>Extension</th><th>Scope</th><th>Spec version</th><th>Status</th></tr></thead><tbody>")
        d.extensions.forEach { ext ->
            append("<tr><td class=\"code\">${htmlEscape(ext.name)}</td><td>${htmlEscape(ext.scope)}</td><td>${ext.specVersion}</td><td>${statusBadge(if (ext.supported) "SUPPORTED" else "NOT SUPPORTED")}</td></tr>")
        }
        append("</tbody></table></div>")
        table("Device layers", "<th>Layer</th><th>Details</th>", d.deviceLayers.map { it.name to htmlEscape("spec ${it.specVersion}, implementation ${it.implementationVersion}; ${it.description}; extensions=${it.extensions.size}") })
        table("Features", "<th>Feature</th><th>Status</th>", d.features.map { htmlEscape(it.name) to statusBadge(if (it.supported) "SUPPORTED" else "NOT SUPPORTED") })
        table("Detailed query results (${d.detailedProperties.size} results; ${d.detailedProperties.map { "${it.section} / ${it.name}" }.distinct().size} unique report fields)", "<th>Section / property</th><th>Value</th>", d.detailedProperties.map { "${it.section} / ${it.name}" to htmlEscape(it.value) })
        table("Limits", "<th>Limit</th><th>Value</th>", d.limits.map { it.first to htmlEscape(it.second) })
        table("Memory", "<th>Entry</th><th>Value</th>", d.heaps.map { "Heap ${it.index}" to htmlEscape("${formatBytes(it.size)} | flags ${it.flags}") } + d.memoryTypes.map { "Type ${it.index}" to htmlEscape("heap ${it.heap} | flags ${it.flags}") })
        table("Queues", "<th>Family</th><th>Details</th>", d.queues.map { "${it.index}" to htmlEscape("count=${it.count}, timestampBits=${it.timestampBits}, flags=${it.flags}, graphics=${it.graphics}, compute=${it.compute}, transfer=${it.transfer}, sparse=${it.sparse}, protected=${it.protected}, videoDecode=${it.videoDecode}, videoEncode=${it.videoEncode}, opticalFlow=${it.opticalFlow}, dataGraph=${it.dataGraph}, unknownFlags=0x${it.unknownFlags.toString(16).uppercase()}, granularity=${it.granularity}") })
        table("Formats", "<th>Format</th><th>Status / feature masks</th>", d.formats.map { htmlEscape(it.name) to "${statusBadge(if (it.supported) "SUPPORTED" else "NOT SUPPORTED")} ${htmlEscape("linear=${it.linear}, optimal=${it.optimal}, buffer=${it.buffer}")}" })
        table("Surface formats / color spaces", "<th>Format / color space</th><th>Status / description</th>", d.surfaceFormats.map { "${it.format} / ${it.colorSpace}" to "${statusBadge(if (it.supported) "SUPPORTED" else "NOT SUPPORTED")} ${htmlEscape("${it.classification}; ${it.description}")}" })
        table("Present modes", "<th>Mode</th><th>Status</th>", d.presentModes.map { it to statusBadge("SUPPORTED") })
        append("</div>")
    }
    append("</div></body></html>")
}

private enum class SupportFilter { ALL, SUPPORTED, UNSUPPORTED }

@Composable
private fun SupportFilterRow(selected: SupportFilter, onSelected: (SupportFilter) -> Unit) {
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(selected = selected == SupportFilter.ALL, onClick = { onSelected(SupportFilter.ALL) }, label = { Text("All") })
        FilterChip(selected = selected == SupportFilter.SUPPORTED, onClick = { onSelected(SupportFilter.SUPPORTED) }, label = { Text("Supported") })
        FilterChip(selected = selected == SupportFilter.UNSUPPORTED, onClick = { onSelected(SupportFilter.UNSUPPORTED) }, label = { Text("Not supported") })
    }
}

private val KNOWN_VULKAN_EXTENSIONS = setOf(
    "VK_ANDROID_external_format_resolve", "VK_ANDROID_external_memory_android_hardware_buffer", "VK_EXT_4444_formats",
    "VK_EXT_astc_decode_mode", "VK_EXT_blend_operation_advanced", "VK_EXT_border_color_swizzle", "VK_EXT_calibrated_timestamps",
    "VK_EXT_color_write_enable", "VK_EXT_conditional_rendering", "VK_EXT_conservative_rasterization", "VK_EXT_custom_border_color",
    "VK_EXT_depth_clamp_zero_one", "VK_EXT_depth_clip_control", "VK_EXT_depth_clip_enable", "VK_EXT_depth_range_unrestricted",
    "VK_EXT_descriptor_buffer", "VK_EXT_descriptor_indexing", "VK_EXT_device_address_binding_report", "VK_EXT_device_fault",
    "VK_EXT_device_memory_report", "VK_EXT_dynamic_rendering_unused_attachments", "VK_EXT_extended_dynamic_state", "VK_EXT_extended_dynamic_state2",
    "VK_EXT_extended_dynamic_state3", "VK_EXT_fragment_density_map", "VK_EXT_fragment_density_map2", "VK_EXT_global_priority",
    "VK_EXT_global_priority_query", "VK_EXT_host_query_reset", "VK_EXT_image_2d_view_of_3d", "VK_EXT_image_compression_control",
    "VK_EXT_image_compression_control_swapchain", "VK_EXT_image_drm_format_modifier", "VK_EXT_image_robustness", "VK_EXT_index_type_uint8",
    "VK_EXT_inline_uniform_block", "VK_EXT_line_rasterization", "VK_EXT_pipeline_creation_cache_control", "VK_EXT_pipeline_robustness",
    "VK_EXT_private_data", "VK_EXT_shader_atomic_float", "VK_EXT_shader_demote_to_helper_invocation", "VK_EXT_shader_image_atomic_int64",
    "VK_EXT_shader_subgroup_ballot", "VK_EXT_shader_subgroup_vote", "VK_EXT_subgroup_size_control", "VK_EXT_swapchain_colorspace",
    "VK_EXT_transform_feedback", "VK_EXT_vertex_attribute_divisor", "VK_EXT_vertex_input_dynamic_state", "VK_EXT_ycbcr_2plane_444_formats",
    "VK_GOOGLE_display_timing", "VK_IMG_filter_cubic", "VK_KHR_16bit_storage", "VK_KHR_8bit_storage", "VK_KHR_acceleration_structure",
    "VK_KHR_bind_memory2", "VK_KHR_buffer_device_address", "VK_KHR_calibrated_timestamps", "VK_KHR_copy_commands2", "VK_KHR_create_renderpass2",
    "VK_KHR_deferred_host_operations", "VK_KHR_depth_stencil_resolve", "VK_KHR_descriptor_update_template", "VK_KHR_device_group",
    "VK_KHR_draw_indirect_count", "VK_KHR_driver_properties", "VK_KHR_dynamic_rendering", "VK_KHR_external_fence", "VK_KHR_external_fence_capabilities",
    "VK_KHR_external_memory", "VK_KHR_external_memory_capabilities", "VK_KHR_external_semaphore", "VK_KHR_external_semaphore_capabilities",
    "VK_KHR_format_feature_flags2", "VK_KHR_fragment_shader_barycentric", "VK_KHR_fragment_shading_rate", "VK_KHR_get_memory_requirements2",
    "VK_KHR_get_physical_device_properties2", "VK_KHR_global_priority", "VK_KHR_image_format_list", "VK_KHR_imageless_framebuffer",
    "VK_KHR_incremental_present", "VK_KHR_index_type_uint8", "VK_KHR_maintenance1", "VK_KHR_maintenance2", "VK_KHR_maintenance3",
    "VK_KHR_maintenance4", "VK_KHR_maintenance5", "VK_KHR_maintenance6", "VK_KHR_maintenance7", "VK_KHR_maintenance8", "VK_KHR_maintenance9", "VK_KHR_maintenance10", "VK_KHR_maintenance11",
    "VK_KHR_present_mode_fifo_latest_ready", "VK_KHR_present_id2", "VK_KHR_present_wait2",
    "VK_KHR_surface_maintenance1", "VK_KHR_swapchain_maintenance1", "VK_KHR_video_queue", "VK_KHR_video_decode_queue", "VK_KHR_video_encode_queue", "VK_KHR_video_decode_h264", "VK_KHR_video_decode_h265", "VK_KHR_video_decode_av1", "VK_KHR_video_decode_vp9", "VK_KHR_video_encode_h264", "VK_KHR_video_encode_h265", "VK_KHR_video_encode_av1", "VK_KHR_pipeline_binary",
    "VK_KHR_cooperative_matrix", "VK_KHR_robustness2", "VK_KHR_copy_memory_indirect",
    "VK_KHR_shader_untyped_pointers", "VK_KHR_compute_shader_derivatives",
 "VK_KHR_multiview", "VK_KHR_pipeline_library", "VK_KHR_present_id",
    "VK_KHR_present_wait", "VK_KHR_push_descriptor", "VK_KHR_ray_query", "VK_KHR_ray_tracing_pipeline", "VK_KHR_relaxed_block_layout",
    "VK_KHR_sampler_mirror_clamp_to_edge", "VK_KHR_sampler_ycbcr_conversion", "VK_KHR_separate_depth_stencil_layouts", "VK_KHR_shader_atomic_int64",
    "VK_KHR_shader_clock", "VK_KHR_shader_float16_int8", "VK_KHR_shader_float_controls", "VK_KHR_shader_non_semantic_info", "VK_KHR_shader_subgroup_extended_types",
    "VK_KHR_shader_subgroup_uniform_control_flow", "VK_KHR_shader_terminate_invocation", "VK_KHR_shared_presentable_image", "VK_KHR_spirv_1_4",
    "VK_KHR_storage_buffer_storage_class", "VK_KHR_swapchain", "VK_KHR_synchronization2", "VK_KHR_timeline_semaphore", "VK_KHR_uniform_buffer_standard_layout",
    "VK_KHR_variable_pointers", "VK_KHR_vertex_attribute_divisor", "VK_KHR_vulkan_memory_model", "VK_KHR_workgroup_memory_explicit_layout",
    "VK_KHR_zero_initialize_workgroup_memory", "VK_NV_optical_flow", "VK_QCOM_filter_cubic_weights", "VK_QCOM_fragment_density_map_offset",
    "VK_QCOM_image_processing", "VK_QCOM_image_processing2", "VK_QCOM_multiview_per_view_render_areas", "VK_QCOM_multiview_per_view_viewports",
    "VK_QCOM_render_pass_shader_resolve", "VK_QCOM_render_pass_store_ops", "VK_QCOM_render_pass_transform", "VK_QCOM_rotated_copy_commands",
    "VK_QCOM_tile_properties", "VK_QCOM_ycbcr_degamma"
)

private fun List<String>.distinctScopes(): String = distinct().joinToString(" / ")

private data class NavigationItem(val page: Page, val label: String, val icon: Int)

private fun selectedNavigationPage(page: Page): Page = when (page) {
    Page.Features, Page.Memory, Page.Queues, Page.Formats, Page.Properties, Page.Settings, Page.Info -> Page.Overview
    else -> page
}

private fun navigationItems(): List<NavigationItem> = listOf(
    NavigationItem(Page.Overview, "Overview", R.drawable.ic_home),
    NavigationItem(Page.Vulkan, "Vulkan", R.drawable.ic_vulkan),
    NavigationItem(Page.Surface, "Surface", R.drawable.ic_surface),
    NavigationItem(Page.Display, "Display", R.drawable.ic_display),
    NavigationItem(Page.Extensions, "Extensions", R.drawable.ic_extensions)
)

private fun pageIcon(page: Page): Int = when (page) {
    Page.Overview -> R.drawable.ic_home
    Page.Vulkan -> R.drawable.ic_vulkan
    Page.Display -> R.drawable.ic_display
    Page.Surface -> R.drawable.ic_surface
    Page.Features -> R.drawable.ic_features
    Page.Memory -> R.drawable.ic_memory
    Page.Queues -> R.drawable.ic_queues
    Page.Formats -> R.drawable.ic_formats
    Page.Properties -> R.drawable.ic_properties
    Page.Extensions -> R.drawable.ic_extensions
    Page.Profiles -> R.drawable.ic_extensions
    Page.Settings -> R.drawable.ic_settings
    Page.Info -> R.drawable.ic_info
}


private fun pageTransitionIndex(page: Page): Int = when (page) {
    Page.Overview -> 0
    Page.Vulkan -> 1
    Page.Surface -> 2
    Page.Display -> 3
    Page.Extensions -> 4
    Page.Features -> 5
    Page.Memory -> 6
    Page.Queues -> 7
    Page.Formats -> 8
    Page.Properties -> 9
    Page.Profiles -> 10
    Page.Settings -> 11
    Page.Info -> 12
}

private data class VendorInfo(val name: String, val logo: Int)

private fun vendorInfo(vendorId: Long): VendorInfo = when (vendorId) {
    0x5143L -> VendorInfo("Qualcomm", R.drawable.gpu_vendor_qualcomm)
    0x13B5L -> VendorInfo("Arm", R.drawable.gpu_vendor_arm)
    0x1010L -> VendorInfo("Imagination Technologies", R.drawable.gpu_vendor_imagination)
    0x19E5L -> VendorInfo("Huawei", R.drawable.gpu_vendor_huawei)
    0x10DEL -> VendorInfo("NVIDIA", R.drawable.gpu_vendor_nvidia)
    0x1002L -> VendorInfo("AMD", R.drawable.gpu_vendor_amd)
    0x8086L -> VendorInfo("Intel", R.drawable.gpu_vendor_intel)
    0x144DL -> VendorInfo("Samsung", R.drawable.gpu_vendor_samsung)
    0x14E4L -> VendorInfo("Broadcom", R.drawable.gpu_vendor_broadcom)
    0x10001L -> VendorInfo("Vivante", R.drawable.gpu_vendor_vivante)
    0x10002L -> VendorInfo("VeriSilicon", R.drawable.gpu_vendor_vsi)
    else -> VendorInfo("Unknown vendor", R.drawable.gpu_vendor_unknown)
}

@Composable
private fun VendorLogo(vendorId: Long?, modifier: Modifier) {
    val info = vendorInfo(vendorId ?: -1L)
    Card(colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFF111111)), shape = RoundedCornerShape(18.dp), modifier = modifier) {
        Image(
            painter = painterResource(info.logo),
            contentDescription = info.name,
            modifier = Modifier.fillMaxSize().padding(8.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun CollectionStatusBanner(status: CollectionStatus) {
    AnimatedVisibility(
        visible = status != CollectionStatus.IDLE,
        enter = fadeIn(animationSpec = androidx.compose.animation.core.tween(260)) + expandVertically(animationSpec = androidx.compose.animation.core.tween(260)),
        exit = fadeOut(animationSpec = androidx.compose.animation.core.tween(420)) + shrinkVertically(animationSpec = androidx.compose.animation.core.tween(420))
    ) {
        val collecting = status == CollectionStatus.COLLECTING
        Surface(modifier = Modifier.fillMaxWidth(), color = ComposeColor(0xFF111111), tonalElevation = 0.dp) {
            Column(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (collecting) {
                        AssistChip(onClick = {}, enabled = false, label = { Text("Collecting information…") })
                        Text(
                            "VulkanScope is collecting Vulkan information in the background.",
                            color = ComposeColor(0xFF9E9E9E),
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = ComposeColor(0xFF163D24),
                            modifier = Modifier.size(30.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("✓", color = ComposeColor(0xFF55D98A), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                        }
                        Text(
                            "Completed",
                            color = ComposeColor(0xFF55D98A),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Vulkan information updated.",
                            color = ComposeColor(0xFF9E9E9E),
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                if (collecting) LinearProgressIndicator(Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun LoadingView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Inspecting Vulkan…", color = ComposeColor(0xFF9E9E9E)) }
}

@Composable
private fun EmptyState(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(message) }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFF121212)), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}

@Composable
private fun MetricCard(title: String, value: String, modifier: Modifier) {
    Card(colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFF121212)), shape = RoundedCornerShape(22.dp), modifier = modifier) {
        Column(Modifier.padding(17.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(title, color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.labelMedium)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun KeyValue(key: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
        Text(key, color = ComposeColor(0xFF8F8F8F), modifier = Modifier.weight(0.9f))
        Text(value, modifier = Modifier.weight(1.1f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
    }
}

@Composable
private fun DataRow(name: String, state: String, positive: Boolean) {
    Row(Modifier.fillMaxWidth().background(ComposeColor(0xFF111111), RoundedCornerShape(16.dp)).padding(15.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(name, modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis)
        Text(state, color = if (positive) ComposeColor(0xFFFFFFFF) else ComposeColor(0xFF777777), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelSmall)
    }
}

private fun formatBytes(value: Long): String {
    if (value <= 0) return "0 B"
    val units = arrayOf("B", "KiB", "MiB", "GiB", "TiB")
    var number = value.toDouble()
    var index = 0
    while (number >= 1024.0 && index < units.lastIndex) {
        number /= 1024.0
        index++
    }
    return String.format(java.util.Locale.US, "%.2f %s", number, units[index])
}
