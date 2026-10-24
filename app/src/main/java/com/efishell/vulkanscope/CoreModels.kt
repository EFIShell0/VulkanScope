@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.efishell.vulkanscope

import android.os.Build
import android.view.Surface
import android.util.JsonReader
import android.util.JsonToken
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import java.io.File
import java.io.FileOutputStream
import java.io.FileInputStream
import java.io.ByteArrayOutputStream
import java.io.FilterInputStream
import java.io.InputStream
import java.io.StringReader
import java.net.Inet6Address
import java.net.InetAddress
import java.util.zip.ZipInputStream
import java.util.concurrent.TimeUnit
import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import androidx.compose.foundation.Image
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import org.json.JSONArray
import org.json.JSONObject


internal val VulkanBlack = ComposeColor(0xFF000000)
internal val VulkanSurface = ComposeColor(0xFF101010)
internal val VulkanSurfaceLow = ComposeColor(0xFF1B1418)
internal val VulkanSurfaceRaised = ComposeColor(0xFF20171B)
internal val VulkanSurfaceTonal = ComposeColor(0xFF2A1B23)
internal val VulkanAccentContainer = ComposeColor(0xFF351719)
internal val VulkanAccent = ComposeColor(0xFFA41E22)
internal val VulkanAccentSoft = ComposeColor(0xFFE2676A)
internal val VulkanTextPrimary = ComposeColor(0xFFF7F2F3)
internal val VulkanTextSecondary = ComposeColor(0xFFB6ACAE)
internal val VulkanTextMuted = ComposeColor(0xFF968D8F)
internal val VulkanOutline = ComposeColor(0xFF494244)
internal val VulkanOutlineVariant = ComposeColor(0xFF54343E)
internal val VulkanGlassTint = ComposeColor(0xA823171D)
internal val VulkanGlassBorder = ComposeColor(0x3D6A565D)
internal val VulkanPagerSurface = ComposeColor(0xFF1B1719)
internal val PrimaryNavigationMaxWidth = 310.dp
internal val PrimaryNavigationHeight = 54.dp
internal val PrimaryNavigationIndicatorHeight = 42.dp
internal val PrimaryNavigationIndicatorHorizontalInset = 8.dp
internal val PrimaryNavigationBottomGap = 4.dp
internal val PrimaryNavigationContentGap = 10.dp
internal val PageChromeSeparation = 12.dp
internal val OverlayCoordinationLead = 96.dp
internal const val OverlayCoordinationMotionMillis = 120
internal val LocalDetailKeyValuePresentation = staticCompositionLocalOf { false }
internal data class EvidenceActionEnvironment(val openEncyclopedia: (String) -> Unit, val addWatch: (String) -> Unit)
internal val LocalEvidenceActionEnvironment = staticCompositionLocalOf<EvidenceActionEnvironment?> { null }
internal typealias SharedStorageAccessRequest = ((() -> Unit), (() -> Unit)) -> Unit
internal val LocalSharedStorageAccessRequest = staticCompositionLocalOf<SharedStorageAccessRequest> { { granted, _ -> granted() } }
internal typealias SharedStorageBrowserLauncher = (SharedStorageBrowserOverlayRequest) -> Unit
internal val LocalSharedStorageBrowserLauncher = staticCompositionLocalOf<SharedStorageBrowserLauncher> { { } }
internal val LocalTransientOverlayContentInset = staticCompositionLocalOf { 0.dp }
internal val LocalPinnedPagerContentInset = staticCompositionLocalOf { 0.dp }
internal val LocalCollectionPagerPinned = staticCompositionLocalOf { false }
internal val LocalCollectionPagerHandoffFraction = staticCompositionLocalOf { 0f }
internal typealias StickyPagerStateReporter = (String, Boolean, Int) -> Unit
internal val LocalStickyPagerStateReporter = staticCompositionLocalOf<StickyPagerStateReporter> { { _, _, _ -> } }
internal data class CollectionPagerRegistration(
    val key: String,
    val itemIndex: Int,
    val heightPx: Int,
    val totalItems: Int,
    val currentPage: Int,
    val pageSize: Int,
    val onPageChange: (Int) -> Unit,
    val onMeasuredHeight: (Int) -> Unit
)
internal data class ChromeBackdropSource(val layer: GraphicsLayer, val rootOffset: Offset)
internal typealias ChromeBackdropReporter = (GraphicsLayer, Offset?) -> Unit
internal val LocalChromeBackdropReporter = staticCompositionLocalOf<ChromeBackdropReporter> { { _, _ -> } }
internal typealias CollectionPagerMeasuredHeightLookup = (String) -> Int
internal typealias CollectionPagerMeasuredHeightReporter = (String, Int) -> Unit
internal val LocalCollectionPagerMeasuredHeightLookup = staticCompositionLocalOf<CollectionPagerMeasuredHeightLookup> { { _ -> 0 } }
internal val LocalCollectionPagerMeasuredHeightReporter = staticCompositionLocalOf<CollectionPagerMeasuredHeightReporter> { { _, _ -> } }
internal val LocalAppHeaderContentInset = staticCompositionLocalOf { 0.dp }
internal val LocalAppHeaderVisible = staticCompositionLocalOf { true }
internal val LocalPrimaryLazyListState = staticCompositionLocalOf<LazyListState?> { null }
internal val LocalBottomNavigationContentInset = staticCompositionLocalOf { PrimaryNavigationBottomGap + PrimaryNavigationHeight + PrimaryNavigationContentGap }
internal val VulkanExpressiveShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(22.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp),
    largeIncreased = RoundedCornerShape(32.dp),
    extraLargeIncreased = RoundedCornerShape(36.dp),
    extraExtraLarge = RoundedCornerShape(40.dp)
)

internal val VulkanBaseTypography = Typography()
internal val VulkanTypography = Typography(
    displayLarge = VulkanBaseTypography.displayLarge.copy(textDirection = TextDirection.ContentOrLtr),
    displayMedium = VulkanBaseTypography.displayMedium.copy(textDirection = TextDirection.ContentOrLtr),
    displaySmall = VulkanBaseTypography.displaySmall.copy(textDirection = TextDirection.ContentOrLtr),
    headlineLarge = VulkanBaseTypography.headlineLarge.copy(textDirection = TextDirection.ContentOrLtr),
    headlineMedium = VulkanBaseTypography.headlineMedium.copy(textDirection = TextDirection.ContentOrLtr),
    headlineSmall = VulkanBaseTypography.headlineSmall.copy(textDirection = TextDirection.ContentOrLtr),
    titleLarge = VulkanBaseTypography.titleLarge.copy(textDirection = TextDirection.ContentOrLtr),
    titleMedium = VulkanBaseTypography.titleMedium.copy(textDirection = TextDirection.ContentOrLtr),
    titleSmall = VulkanBaseTypography.titleSmall.copy(textDirection = TextDirection.ContentOrLtr),
    bodyLarge = VulkanBaseTypography.bodyLarge.copy(textDirection = TextDirection.ContentOrLtr),
    bodyMedium = VulkanBaseTypography.bodyMedium.copy(textDirection = TextDirection.ContentOrLtr),
    bodySmall = VulkanBaseTypography.bodySmall.copy(textDirection = TextDirection.ContentOrLtr),
    labelLarge = VulkanBaseTypography.labelLarge.copy(textDirection = TextDirection.ContentOrLtr),
    labelMedium = VulkanBaseTypography.labelMedium.copy(textDirection = TextDirection.ContentOrLtr),
    labelSmall = VulkanBaseTypography.labelSmall.copy(textDirection = TextDirection.ContentOrLtr)
)


internal fun elapsedMillis(startNanos: Long): Long = ((System.nanoTime() - startNanos) / 1_000_000L).coerceAtLeast(0L)

internal val VULKAN_TRADEMARK_DISPLAY_REGEX = Regex("""\bVulkan(?!Scope|®)""")
internal val SPIRV_TRADEMARK_DISPLAY_REGEX = Regex("""\bSPIR-V(?!™)""")

internal val KHRONOS_TRADEMARK_DISPLAY_REGEX = Regex("""\bKhronos(?!®|\s+Group\s+logo)""")
internal fun trademarkVulkanDisplayText(text: String): String =
    KHRONOS_TRADEMARK_DISPLAY_REGEX.replace(
        SPIRV_TRADEMARK_DISPLAY_REGEX.replace(VULKAN_TRADEMARK_DISPLAY_REGEX.replace(text, "Vulkan®"), "SPIR-V™"),
        "Khronos®"
    )
internal fun trademarkSpirvDisplayText(text: String): String = SPIRV_TRADEMARK_DISPLAY_REGEX.replace(text, "SPIR-V™")

internal data class DisplayReport(
    val resolution: String,
    val refreshRate: String,
    val wideGamut: Boolean?,
    val preferredWideGamut: String,
    val hdrTypes: List<String>,
    val minLuminance: String,
    val maxLuminance: String,
    val averageLuminance: String,
    val modes: List<String>,
    val hdrCapabilityStatus: String = "unavailable"
)

internal data class ExtensionEntry(val name: String, val scope: String, val specVersion: Int, val supported: Boolean = true)
internal data class LayerEntry(
    val name: String,
    val description: String,
    val specVersion: Int,
    val implementationVersion: Int,
    val extensions: List<ExtensionEntry> = emptyList(),
    val extensionStatus: String = "unknown",
    val extensionReason: String = "",
    val extensionsComplete: Boolean = false
)
internal data class FeatureEntry(val name: String, val supported: Boolean)
internal data class SurfaceFormatEntry(val format: String, val colorSpace: String, val classification: String, val description: String, val supported: Boolean = true)
internal data class FormatEntry(val name: String, val supported: Boolean, val linear: Long, val optimal: Long, val buffer: Long)
internal data class PropertyEntry(val section: String, val name: String, val value: String)
internal data class ImageFormatQueryResultEntry(val name: String, val status: String, val vkResult: Int?, val reason: String = "")

internal fun parseImageFormatQueryResults(array: JSONArray): List<ImageFormatQueryResultEntry> = (0 until array.length()).mapNotNull { index ->
    val result = array.optJSONObject(index) ?: return@mapNotNull null
    val name = sanitizeReportLabel(result.optString("name"))
    val status = result.optString("status").lowercase()
    val hasVkResult = result.has("vkResult") && !result.isNull("vkResult")
    val vkResult = if (hasVkResult) result.optInt("vkResult", Int.MIN_VALUE).takeUnless { it == Int.MIN_VALUE } else null
    val reason = result.optString("reason").take(1024)
    val valid = name.isNotBlank() && when (status) {
        "available" -> vkResult == 0 && reason.isBlank()
        "unsupported" -> vkResult == -11 && reason.isBlank()
        "unavailable" -> vkResult != null && vkResult != 0 && vkResult != -11
        "not_applicable" -> vkResult == null && reason.isNotBlank()
        else -> false
    }
    if (valid) ImageFormatQueryResultEntry(name, status, vkResult, reason) else null
}
internal data class QueueEntry(val index: Int, val count: Int, val timestampBits: Int, val flags: Long, val graphics: Boolean, val compute: Boolean, val transfer: Boolean, val sparse: Boolean, val protected: Boolean, val videoDecode: Boolean, val videoEncode: Boolean, val opticalFlow: Boolean, val dataGraph: Boolean, val unknownFlags: Long, val granularity: String, val videoCodecOperations: Long = 0L, val videoCodecQueryStatus: String = "unknown", val videoCodecQueryReason: String = "")
internal data class MemoryHeapEntry(val index: Int, val size: Long, val flags: Long)
internal data class MemoryTypeEntry(val index: Int, val heap: Int, val flags: Long)
internal val ISOLATED_CORE_GROUPS = linkedMapOf(
    "core11" to "Vulkan 1.1 Core",
    "core12" to "Vulkan 1.2 Core",
    "core13" to "Vulkan 1.3 Core",
    "core14" to "Vulkan 1.4 Core"
)

internal val ISOLATED_ADVANCED_GROUPS = linkedMapOf(
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

internal val ISOLATED_EXTENSION_GROUPS = linkedMapOf(
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
    "inlineUniformBlockParity" to "VK_EXT_inline_uniform_block",
    "privateDataParity" to "VK_EXT_private_data",
    "synchronization2Parity" to "VK_KHR_synchronization2",
    "caps412SamplerFilterMinmax" to "VK_EXT_sampler_filter_minmax",
    "caps412DescriptorIndexing" to "VK_EXT_descriptor_indexing",
    "caps412Multiview" to "VK_KHR_multiview",
    "caps412Maintenance2" to "VK_KHR_maintenance2",
    "caps412ShaderFloatControls" to "VK_KHR_shader_float_controls",
    "caps412DepthStencilResolve" to "VK_KHR_depth_stencil_resolve",
    "caps412ShaderIntegerDotProduct" to "VK_KHR_shader_integer_dot_product",
    "caps412Maintenance4" to "VK_KHR_maintenance4",
    "caps412SubgroupSizeControl" to "VK_EXT_subgroup_size_control",
    "caps412HostQueryReset" to "VK_EXT_host_query_reset",
    "caps412ShaderDemoteToHelperInvocation" to "VK_EXT_shader_demote_to_helper_invocation",
    "caps412PipelineCreationCacheControl" to "VK_EXT_pipeline_creation_cache_control",
    "caps412DynamicRendering" to "VK_KHR_dynamic_rendering",
    "caps412ShaderFloat16Int8" to "VK_KHR_shader_float16_int8",
    "caps41216bitStorage" to "VK_KHR_16bit_storage",
    "caps412ImagelessFramebuffer" to "VK_KHR_imageless_framebuffer",
    "caps412VariablePointers" to "VK_KHR_variable_pointers",
    "caps412SamplerYcbcrConversion" to "VK_KHR_sampler_ycbcr_conversion",
    "caps412ShaderSubgroupExtendedTypes" to "VK_KHR_shader_subgroup_extended_types",
    "caps4128bitStorage" to "VK_KHR_8bit_storage",
    "caps412ShaderAtomicInt64" to "VK_KHR_shader_atomic_int64",
    "caps412TimelineSemaphore" to "VK_KHR_timeline_semaphore",
    "caps412VulkanMemoryModel" to "VK_KHR_vulkan_memory_model",
    "caps412ShaderTerminateInvocation" to "VK_KHR_shader_terminate_invocation",
    "caps412SeparateDepthStencilLayouts" to "VK_KHR_separate_depth_stencil_layouts",
    "caps412UniformBufferStandardLayout" to "VK_KHR_uniform_buffer_standard_layout",
    "caps412BufferDeviceAddress" to "VK_KHR_buffer_device_address",
    "caps412ZeroInitializeWorkgroupMemory" to "VK_KHR_zero_initialize_workgroup_memory"
)

internal data class PresentationQueueEvidence(
    val queueFamily: Int,
    val supported: Boolean,
    val queryResult: Int? = null
)

internal data class DeviceReport(
    val name: String,
    val apiVersion: String,
    val driverVersion: String,
    val driverVersionText: String,
    val vendorId: String,
    val vendorIdRaw: Long,
    val deviceId: String,
    val deviceIdRaw: Long,
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
    val imageFormatQueryResults: List<ImageFormatQueryResultEntry> = emptyList(),
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
    val vulkan14Reason: String = "",
    val queueQuerySafetyRejected: Boolean = false,
    val memoryHeapSafetyRejected: Boolean = false,
    val memoryTypeSafetyRejected: Boolean = false,
    val surfaceQueueQuerySafetyRejected: Boolean = false,
    val deviceLayerStatus: String = "unknown",
    val deviceLayerReason: String = "",
    val deviceLayersComplete: Boolean = false,
    val surfaceQueryStatus: String = "unknown",
    val surfaceQueryReason: String = "",
    val surfaceColorSpaceExtensionStatus: String = "unknown",
    val presentationQueueEvidence: List<PresentationQueueEvidence> = emptyList(),
    val surfaceFormatQueryAttempted: Boolean = false,
    val surfaceDependentWsiQueryStatus: String = "unknown",
    val surfaceFormatEnumerationComplete: Boolean = false,
    val surfaceFormatQuerySpecAnomaly: Boolean = false,
    val surfacePresentModeEnumerationComplete: Boolean = false,
    val surfacePresentModeQuerySpecAnomaly: Boolean = false
)

internal data class RegistryCoverage(
    val baseline: String = "Unknown",
    val mode: String = "Unknown",
    val implementedPhysicalDeviceStructCount: Int = 0,
    val validatedRuntimeQueryGroupCount: Int = 0,
    val runtimeRegistryTokenReferenceCount: Int = 0,
    val catalogSchemaVersion: Int = 0,
    val reportSchema: String = "Unknown",
    val headerBaseline: String = "Unknown",
    val instanceDependencyCandidateCount: Int = 0,
    val implementedPhysicalDeviceStructs: List<String> = emptyList(),
    val validatedRuntimeQueryGroups: List<String> = emptyList()
)

internal data class VulkanReport(
    val loaderVersion: String,
    val instanceExtensions: List<ExtensionEntry>,
    val instanceLayers: List<LayerEntry>,
    val devices: List<DeviceReport>,
    val error: String?,
    val registryCoverage: RegistryCoverage = RegistryCoverage(),
    val instanceExtensionStatus: String = "unknown",
    val instanceExtensionReason: String = "",
    val instanceLayerStatus: String = "unknown",
    val instanceLayerReason: String = "",
    val baseReportComplete: Boolean = false,
    val physicalDeviceEnumerationResult: Int? = null,
    val physicalDeviceEnumerationComplete: Boolean = false,
    val physicalDeviceEnumerationSafetyRejected: Boolean = false,
    val physicalDeviceEnumerationReason: String = "",
    val instanceApiVersion: String = "Unknown",
    val instanceGroupProperties: List<PropertyEntry> = emptyList(),
    val instanceGroupStatus: String = "unknown",
    val instanceGroupReason: String = "",
    val instanceGroupEnumerationResult: Int? = null,
    val instanceGroupEnumerationComplete: Boolean = false
)

internal enum class Page(val title: String) {
    Overview("Overview"), Vulkan("Vulkan"), Display("Display & HDR"), Surface("Surface"), Features("Features"), Memory("Memory"), Queues("Queues"), Video("Vulkan Video"), Formats("Formats"), Properties("Properties & Limits"), Extensions("Extensions"), Profiles("Profiles"), Encyclopedia("Encyclopedia"), Analysis("Analysis workspace"), Settings("Settings"), Info("Info")
}

internal enum class SettingsSection(val label: String, val description: String, val icon: Int) {
    INFO("Info", "Developer, application, libraries, build, Android/device and Vulkan registry information.", R.drawable.ic_info),
    REPORTS("Reports & Database", "Complete TXT/HTML export, Database submission, compatibility state and public report access.", R.drawable.ic_database_submit),
    DRIVER_UPDATES("Drivers & Preferences", "Vulkan drivers, update preferences, opening animation and app bar visibility.", R.drawable.ic_settings)
}

internal enum class SurfaceSection(val label: String, val description: String, val icon: Int) {
    DISPLAY("Display & HDR", "Android display modes, HDR capabilities, wide color gamut and Vulkan interpretation.", R.drawable.ic_tablet),
    SURFACE_FORMATS("Surface & color spaces", "VkSurfaceKHR capabilities, format/color-space pairs and HDR or wide-color surface evidence.", R.drawable.ic_surface),
    PRESENTATION("Presentation", "Present modes and queue-family presentation support for the active Android Surface.", R.drawable.ic_surface_khr)
}

internal enum class DriverMode(val label: String) {
    SYSTEM("System Vulkan driver"),
    TURNIP("Turnip / third-party driver")
}

internal enum class TurnipSupport { UNKNOWN, SUPPORTED, UNSUPPORTED }
internal enum class CollectionStatus { IDLE, COLLECTING, COMPLETED, FAILED }
internal enum class NetworkBannerState { HIDDEN, CONNECTED, DISCONNECTED }
internal val LocalValidatedNetwork = staticCompositionLocalOf { false }

internal fun hasValidatedInternetCapabilities(capabilities: NetworkCapabilities): Boolean =
    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

internal fun hasValidatedInternet(context: Context): Boolean {
    val manager = context.getSystemService(ConnectivityManager::class.java)
    val network = runCatching { manager.activeNetwork }.getOrNull() ?: return false
    val capabilities = runCatching { manager.getNetworkCapabilities(network) }.getOrNull() ?: return false
    return hasValidatedInternetCapabilities(capabilities)
}

internal fun isCompleteReportReady(report: VulkanReport, collectionStatus: CollectionStatus): Boolean =
    report.baseReportComplete && report.devices.isNotEmpty() && report.error == null && collectionStatus != CollectionStatus.COLLECTING

internal fun collectionFinishedSuccessfully(report: VulkanReport?): Boolean {
    if (report == null || report.error != null || !report.baseReportComplete || report.devices.isEmpty()) return false
    val unavailableValues = setOf("", "unknown", "unknown gpu", "unavailable", "not available", "not reported", "not applicable", "n/a")
    fun meaningful(value: String): Boolean = value.trim().lowercase() !in unavailableValues
    return report.devices.any { device ->
        meaningful(device.name) ||
            Regex("^\\d+\\.\\d+(?:\\.\\d+)?$").matches(device.apiVersion.trim()) ||
            device.vendorIdRaw != 0L ||
            device.deviceIdRaw != 0L ||
            device.extensions.isNotEmpty() ||
            device.features.isNotEmpty() ||
            device.queues.isNotEmpty() ||
            device.heaps.isNotEmpty() ||
            device.memoryTypes.isNotEmpty() ||
            device.formats.isNotEmpty() ||
            device.limits.isNotEmpty()
    }
}
internal data class AppUpdate(
    val version: String,
    val assetName: String,
    val downloadUrl: String,
    val releaseNotes: String,
    val installedAbi: String,
    val downloadAbi: String,
    val installedVersion: String,
    val installedVersionCode: Long,
    val assetSizeBytes: Long?
)
internal const val OFFICIAL_DATABASE_API_ENDPOINT = "https://vulkanscope-database-api.vulkanscope.workers.dev"
internal const val OFFICIAL_DATABASE_WEB_URL = "https://efishell0.github.io/VulkanScope_database/"
internal const val BACKGROUND_COLLECTION_BUDGET_MS = 60_000L
internal const val BACKGROUND_PROBE_LANES = 6
internal const val TURNIP_ARCHIVE_INPUT_MAX_BYTES = 96L * 1024L * 1024L
internal const val CHROMEOS_ARC_FEATURE = "org.chromium.arc"
internal const val COLLECTION_PAGE_SIZE = 25

internal fun isChromeOsRuntime(context: Context): Boolean = runCatching {
    context.packageManager.hasSystemFeature(CHROMEOS_ARC_FEATURE)
}.getOrDefault(false)

internal fun isAndroidPcFormFactor(context: Context): Boolean = runCatching {
    context.packageManager.hasSystemFeature(PackageManager.FEATURE_PC)
}.getOrDefault(false)

internal fun hasFreeformWindowManagement(context: Context): Boolean = runCatching {
    context.packageManager.hasSystemFeature(PackageManager.FEATURE_FREEFORM_WINDOW_MANAGEMENT)
}.getOrDefault(false)

internal fun platformRuntimeName(context: Context): String = when {
    isChromeOsRuntime(context) -> "ChromeOS Android Runtime (ARC)"
    isAndroidPcFormFactor(context) -> "Android desktop / PC runtime"
    else -> "Android"
}

internal fun hostPlatformVersionEvidence(context: Context): String = if (isChromeOsRuntime(context)) {
    "Unavailable through Android public APIs"
} else {
    Build.VERSION.RELEASE.ifBlank { "Unavailable" }
}

internal fun googlebookEnvironmentEvidence(context: Context): String = when {
    isChromeOsRuntime(context) -> "ChromeOS ARC detected; Googlebook identity is not inferred"
    isAndroidPcFormFactor(context) && hasFreeformWindowManagement(context) -> "Android PC form factor with freeform window management detected; Googlebook-compatible environment evidence only"
    isAndroidPcFormFactor(context) -> "Android PC form factor detected; Googlebook identity is not exposed by a documented public Android API"
    else -> "Googlebook identity is not exposed by a documented public Android API"
}

internal fun googlebookOsVersionEvidence(): String = "Unavailable through documented public Android APIs"

internal class BoundedDriverArchiveInputStream(input: InputStream, private val maxBytes: Long) : FilterInputStream(input) {
    private var consumedBytes = 0L

    private fun account(count: Int): Int {
        if (count > 0) {
            consumedBytes += count.toLong()
            if (consumedBytes > maxBytes) throw SecurityException("Driver bundle archive exceeds the compressed-input safety limit")
        }
        return count
    }

    override fun read(): Int {
        val value = super.read()
        if (value >= 0) account(1)
        return value
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int = account(super.read(buffer, offset, length))
}

internal fun strictProbeTerminalCandidate(candidate: String, baseGroup: Boolean): Boolean = runCatching {
    JsonReader(StringReader(candidate)).use { reader ->
        reader.isLenient = false
        if (reader.peek() != JsonToken.BEGIN_OBJECT) return@use false
        var status: String? = null
        var baseComplete = false
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "status" -> if (reader.peek() == JsonToken.STRING) status = reader.nextString() else reader.skipValue()
                "baseReportComplete" -> if (reader.peek() == JsonToken.BOOLEAN) baseComplete = reader.nextBoolean() else reader.skipValue()
                else -> reader.skipValue()
            }
        }
        reader.endObject()
        if (reader.peek() != JsonToken.END_DOCUMENT) return@use false
        !baseGroup || baseComplete || status == "unavailable" || status == "incomplete" || status == "not_applicable"
    }
}.getOrDefault(false)
internal sealed interface UpdateStatus {
    data object Hidden : UpdateStatus
    data object Checking : UpdateStatus
    data object UpToDate : UpdateStatus
    data object DirectUpdatesDisabledIntro : UpdateStatus
    data class Available(val update: AppUpdate) : UpdateStatus
    data class Downloading(val update: AppUpdate) : UpdateStatus
    data class Failed(val message: String) : UpdateStatus
}

internal enum class UpdateTransferPhase {
    CONNECTING,
    DOWNLOADING,
    PAUSED,
    VERIFYING,
    COMPLETED,
    CANCELED,
    FAILED
}

internal data class UpdateTransferState(
    val update: AppUpdate,
    val phase: UpdateTransferPhase,
    val bytesDownloaded: Long = 0L,
    val totalBytes: Long? = null,
    val bytesPerSecond: Long = 0L,
    val connectionStatus: String = "Connecting",
    val log: List<String> = emptyList(),
    val apk: File? = null,
    val errorMessage: String? = null
)

internal val ipv6FirstDns = Dns { hostname ->
    Dns.SYSTEM.lookup(hostname).sortedWith(compareBy<InetAddress> { if (it is Inet6Address) 0 else 1 })
}

internal val ipv6PreferredHttpClient = OkHttpClient.Builder()
    .dns(ipv6FirstDns)
    .fastFallback(true)
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(20, TimeUnit.SECONDS)
    .followRedirects(true)
    .followSslRedirects(true)
    .build()

internal val databaseHttpClient = ipv6PreferredHttpClient.newBuilder()
    .followRedirects(false)
    .followSslRedirects(false)
    .build()

internal val ipv6PreferredDownloadClient = ipv6PreferredHttpClient.newBuilder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .build()

internal fun readFileTextLimited(file: File, maxBytes: Int): String {
    require(maxBytes > 0)
    FileInputStream(file).use { input ->
        val size = input.channel.size()
        if (size < 0L || size > maxBytes.toLong() || size > Int.MAX_VALUE.toLong()) error("File exceeds the safety limit.")
        val bytes = ByteArray(size.toInt())
        var offset = 0
        while (offset < bytes.size) {
            val count = input.read(bytes, offset, bytes.size - offset)
            if (count < 0) error("File ended before the published probe result was complete.")
            if (count == 0) continue
            offset += count
        }
        if (input.read() != -1) error("File exceeds the published probe result size.")
        return bytes.toString(Charsets.UTF_8)
    }
}

internal const val TURNIP_MANAGER_MAX_DRIVERS = 10
internal const val TURNIP_MANAGER_ROOT_NAME = "turnip_drivers"
internal const val TURNIP_MANAGER_BUNDLE_NAME = "bundle"
internal const val TURNIP_MANAGER_SOURCE_NAME = "source.json"
internal const val TURNIP_ACTIVE_SLOT_PREF = "turnip_active_slot"

internal data class TurnipBundleScan(val files: List<File>)

internal data class TurnipSourceInfo(
    val name: String,
    val location: String,
    val sizeBytes: Long?,
    val modifiedAtMillis: Long?
)

internal data class TurnipBundleInfo(
    val installed: Boolean,
    val zipName: String?,
    val zipLocation: String?,
    val zipSizeBytes: Long?,
    val zipModifiedAtMillis: Long?,
    val importedAtMillis: Long?,
    val driverName: String?,
    val driverVersion: String?,
    val driverDate: String?,
    val description: String?,
    val packageVersion: String?,
    val vendor: String?,
    val author: String?,
    val minApi: Int?,
    val schemaVersion: Int?,
    val libraryName: String?,
    val librarySizeBytes: Long?
)

internal data class ManagedTurnipDriver(
    val slot: Int,
    val selected: Boolean,
    val sourceAvailable: Boolean,
    val info: TurnipBundleInfo
)

internal enum class FileManagerViewMode { LIST, COMPACT, DETAILS, GRID, DENSE_GRID, LARGE_GRID }

internal enum class FileManagerSortMode { NAME_ASC, NAME_DESC, MODIFIED_NEWEST, MODIFIED_OLDEST, CREATED_NEWEST, CREATED_OLDEST }

internal fun fileManagerCreationMillis(file: File): Long = runCatching {
    java.nio.file.Files.readAttributes(
        file.toPath(),
        java.nio.file.attribute.BasicFileAttributes::class.java,
        java.nio.file.LinkOption.NOFOLLOW_LINKS
    ).creationTime().toMillis().coerceAtLeast(0L)
}.getOrDefault(file.lastModified().coerceAtLeast(0L))

internal fun sortFileManagerChildren(children: MutableList<File>, mode: FileManagerSortMode) {
    val locale = java.util.Locale.ROOT
    when (mode) {
        FileManagerSortMode.NAME_ASC -> children.sortBy { it.name.lowercase(locale) }
        FileManagerSortMode.NAME_DESC -> children.sortByDescending { it.name.lowercase(locale) }
        FileManagerSortMode.MODIFIED_NEWEST -> children.sortWith(compareByDescending<File> { it.lastModified() }.thenBy { it.name.lowercase(locale) })
        FileManagerSortMode.MODIFIED_OLDEST -> children.sortWith(compareBy<File> { it.lastModified() }.thenBy { it.name.lowercase(locale) })
        FileManagerSortMode.CREATED_NEWEST -> children.sortWith(compareByDescending<File> { fileManagerCreationMillis(it) }.thenBy { it.name.lowercase(locale) })
        FileManagerSortMode.CREATED_OLDEST -> children.sortWith(compareBy<File> { fileManagerCreationMillis(it) }.thenBy { it.name.lowercase(locale) })
    }
}

internal fun fileManagerSortLabel(mode: FileManagerSortMode): String = when (mode) {
    FileManagerSortMode.NAME_ASC -> "A–Z"
    FileManagerSortMode.NAME_DESC -> "Z–A"
    FileManagerSortMode.MODIFIED_NEWEST -> "Modified · newest first"
    FileManagerSortMode.MODIFIED_OLDEST -> "Modified · oldest first"
    FileManagerSortMode.CREATED_NEWEST -> "Created · newest first"
    FileManagerSortMode.CREATED_OLDEST -> "Created · oldest first"
}

internal fun turnipImportedSourceKey(path: String, name: String): String = path.trim() + "\u0000" + name.trim()

internal data class TurnipArchiveCandidate(
    val path: String,
    val name: String,
    val sizeBytes: Long,
    val modifiedAtMillis: Long,
    val schemaVersion: Int,
    val libraryName: String,
    val librarySizeBytes: Long,
    val driverName: String?,
    val driverVersion: String?,
    val driverDate: String?,
    val description: String?,
    val packageVersion: String?,
    val vendor: String?,
    val author: String?,
    val minApi: Int?
)

internal data class TurnipFolderEntry(
    val path: String,
    val validZipFileCount: Int,
    val zipCountLimited: Boolean
)

internal data class TurnipDirectoryListing(
    val folders: List<TurnipFolderEntry>,
    val candidates: List<TurnipArchiveCandidate>,
    val entryLimitReached: Boolean,
    val zipLimitReached: Boolean
)

internal data class TurnipFileManagerSetup(
    val root: File,
    val remaining: Int,
    val listing: TurnipDirectoryListing,
    val importedSourceKeys: Set<String>
)

internal data class TurnipFileManagerState(
    val visible: Boolean = false,
    val rootPath: String = "",
    val directoryPath: String = "",
    val folders: List<TurnipFolderEntry> = emptyList(),
    val candidates: List<TurnipArchiveCandidate> = emptyList(),
    val importedSourceKeys: Set<String> = emptySet(),
    val selectedPaths: Set<String> = emptySet(),
    val maxSelectable: Int = 0,
    val loading: Boolean = false,
    val importing: Boolean = false,
    val status: String? = null,
    val viewMode: FileManagerViewMode = FileManagerViewMode.LIST,
    val sortMode: FileManagerSortMode = FileManagerSortMode.NAME_ASC,
    val details: TurnipArchiveCandidate? = null
)

internal suspend fun inspectTurnipArchive(file: File): TurnipArchiveCandidate? {
    val canonicalFile = runCatching { file.canonicalFile }.getOrNull() ?: return null
    if (!canonicalFile.isFile || !canonicalFile.canRead() || file.absoluteFile.path != canonicalFile.path) return null
    if (!canonicalFile.name.endsWith(".zip", true)) return null
    val compressedBytes = canonicalFile.length()
    if (compressedBytes <= 0L || compressedBytes > TURNIP_ARCHIVE_INPUT_MAX_BYTES) return null
    val coroutineContext = currentCoroutineContext()
    return try {
        var entryCount = 0
        var totalBytes = 0L
        var metadataBytes: ByteArray? = null
        var metadataCount = 0
        val entries = ArrayList<Pair<String, Long>>()
        val seen = HashSet<String>()
        FileInputStream(canonicalFile).use { source ->
            ZipInputStream(BoundedDriverArchiveInputStream(source, TURNIP_ARCHIVE_INPUT_MAX_BYTES)).use { zip ->
                val buffer = ByteArray(32 * 1024)
                var entry = zip.nextEntry
                while (entry != null) {
                    coroutineContext.ensureActive()
                    if (++entryCount > 2048) error("Too many entries")
                    val rawName = entry.name
                    if (rawName.isBlank() || rawName.length > 1024 || rawName.startsWith('/') || rawName.startsWith('\\') || rawName.contains('\\')) error("Unsafe archive path")
                    val segments = rawName.split('/').filter { it.isNotEmpty() }
                    if (segments.isEmpty() || segments.any { it == "." || it == ".." }) error("Unsafe archive path")
                    val normalized = segments.joinToString("/")
                    if (!seen.add(normalized)) error("Duplicate archive path")
                    if (!entry.isDirectory) {
                        var fileBytes = 0L
                        val captureMetadata = segments.last().equals("meta.json", true)
                        val metadataOutput = if (captureMetadata) ByteArrayOutputStream() else null
                        if (captureMetadata) metadataCount += 1
                        while (true) {
                            coroutineContext.ensureActive()
                            val count = zip.read(buffer)
                            if (count <= 0) break
                            fileBytes += count
                            totalBytes += count
                            if (fileBytes > 32L * 1024L * 1024L || totalBytes > 64L * 1024L * 1024L) error("Archive exceeds safety bounds")
                            if (captureMetadata) {
                                if (fileBytes > 1024L * 1024L) error("Metadata exceeds safety bound")
                                metadataOutput!!.write(buffer, 0, count)
                            }
                        }
                        if (captureMetadata) metadataBytes = metadataOutput!!.toByteArray()
                        entries += segments.last() to fileBytes
                    }
                    zip.closeEntry()
                    entry = zip.nextEntry
                }
            }
        }
        if (metadataCount != 1) error("Exactly one meta.json is required")
        val metadata = JSONObject((metadataBytes ?: error("Missing metadata")).toString(Charsets.UTF_8))
        val schemaVersion = metadata.optInt("schemaVersion", -1)
        if (schemaVersion != 1) error("Unsupported schema")
        val libraryName = metadata.optString("libraryName").trim()
        if (libraryName.isBlank() || libraryName.contains('/') || libraryName.contains('\\') || !libraryName.endsWith(".so", true) || !libraryName.contains("vulkan", true)) error("Invalid Vulkan library declaration")
        val libraries = entries.filter { it.first == libraryName }
        if (libraries.size != 1 || libraries.single().second <= 0L) error("Declared Vulkan library is missing or empty")
        fun text(key: String, max: Int = 2048): String? = metadata.optString(key).trim().take(max).takeIf { it.isNotEmpty() }
        TurnipArchiveCandidate(
            path = canonicalFile.path,
            name = canonicalFile.name.take(512),
            sizeBytes = compressedBytes,
            modifiedAtMillis = canonicalFile.lastModified().coerceAtLeast(0L),
            schemaVersion = schemaVersion,
            libraryName = libraryName,
            librarySizeBytes = libraries.single().second,
            driverName = text("name"),
            driverVersion = text("driverVersion"),
            driverDate = text("driverDate") ?: text("date"),
            description = text("description", 4096),
            packageVersion = text("packageVersion"),
            vendor = text("vendor"),
            author = text("author"),
            minApi = metadata.optInt("minApi", -1).takeIf { it >= 0 }
        )
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (_: Exception) {
        null
    }
}

internal suspend fun scanTurnipFileManagerDirectory(root: File, directory: File, sortMode: FileManagerSortMode): TurnipDirectoryListing {
    val coroutineContext = currentCoroutineContext()
    coroutineContext.ensureActive()
    val canonicalRoot = root.canonicalFile
    val canonicalDirectory = directory.canonicalFile
    val prefix = canonicalRoot.path.trimEnd(File.separatorChar) + File.separator
    if (canonicalDirectory != canonicalRoot && !canonicalDirectory.path.startsWith(prefix)) error("Directory is outside the approved storage root")
    if (!canonicalDirectory.isDirectory || !canonicalDirectory.canRead()) error("Directory is unavailable")
    val children = ArrayList<File>(256)
    var entryLimitReached = false
    java.nio.file.Files.newDirectoryStream(canonicalDirectory.toPath()).use { stream ->
        val iterator = stream.iterator()
        while (iterator.hasNext()) {
            coroutineContext.ensureActive()
            if (children.size >= 4096) {
                entryLimitReached = true
                break
            }
            children += iterator.next().toFile()
        }
    }
    sortFileManagerChildren(children, sortMode)
    val folders = ArrayList<TurnipFolderEntry>()
    for (child in children) {
        coroutineContext.ensureActive()
        try {
            val canonical = child.canonicalFile
            if (!canonical.isDirectory || !canonical.canRead()) continue
            if (canonical != canonicalRoot && !canonical.path.startsWith(prefix)) continue
            if (child.absoluteFile.path != canonical.path) continue
            var inspectedZipFileCount = 0
            var validZipFileCount = 0
            var zipCountLimited = false
            java.nio.file.Files.newDirectoryStream(canonical.toPath()).use { childStream ->
                val childIterator = childStream.iterator()
                while (childIterator.hasNext()) {
                    coroutineContext.ensureActive()
                    val nested = childIterator.next().toFile()
                    if (!nested.name.endsWith(".zip", true)) continue
                    if (inspectedZipFileCount >= 256) {
                        zipCountLimited = true
                        break
                    }
                    inspectedZipFileCount += 1
                    if (inspectTurnipArchive(nested) != null) validZipFileCount += 1
                }
            }
            folders += TurnipFolderEntry(canonical.path, validZipFileCount, zipCountLimited)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
        }
    }
    val candidates = ArrayList<TurnipArchiveCandidate>()
    var inspectedZipCount = 0
    var zipLimitReached = false
    for (child in children) {
        coroutineContext.ensureActive()
        if (!child.name.endsWith(".zip", true)) continue
        if (inspectedZipCount >= 256) {
            zipLimitReached = true
            break
        }
        inspectedZipCount += 1
        inspectTurnipArchive(child)?.let(candidates::add)
    }
    return TurnipDirectoryListing(folders, candidates, entryLimitReached, zipLimitReached)
}

internal enum class SharedStorageBrowserMode { IMPORT, EXPORT }

internal data class SharedStorageBrowserRequest(
    val title: String,
    val description: String,
    val mode: SharedStorageBrowserMode,
    val allowedExtensions: Set<String>,
    val suggestedFileName: String = "",
    val maxImportBytes: Long? = null
)

internal data class SharedStorageBrowserOverlayRequest(
    val request: SharedStorageBrowserRequest,
    val onImport: suspend (File) -> Result<String> = { Result.failure(IllegalStateException("Import is unavailable")) },
    val onExport: suspend (File) -> Result<String> = { Result.failure(IllegalStateException("Export is unavailable")) },
    val onClosed: () -> Unit = {}
)

internal data class SharedStorageFileEntry(
    val path: String,
    val name: String,
    val sizeBytes: Long,
    val modifiedAtMillis: Long,
    val createdAtMillis: Long
)

internal data class SharedStorageDirectoryListing(
    val folders: List<String>,
    val files: List<SharedStorageFileEntry>,
    val entryLimitReached: Boolean
)

internal fun sharedStorageRoot(): File = android.os.Environment.getExternalStorageDirectory().canonicalFile

internal fun isCanonicalSharedStoragePath(root: File, candidate: File): Boolean {
    val canonicalRoot = root.canonicalFile
    val canonicalCandidate = candidate.canonicalFile
    val prefix = canonicalRoot.path.trimEnd(File.separatorChar) + File.separator
    return canonicalCandidate == canonicalRoot || canonicalCandidate.path.startsWith(prefix)
}

internal suspend fun scanSharedStorageDirectory(
    root: File,
    directory: File,
    allowedExtensions: Set<String>,
    includeFiles: Boolean,
    sortMode: FileManagerSortMode
): SharedStorageDirectoryListing {
    val coroutineContext = currentCoroutineContext()
    coroutineContext.ensureActive()
    val canonicalRoot = root.canonicalFile
    val canonicalDirectory = directory.canonicalFile
    if (!isCanonicalSharedStoragePath(canonicalRoot, canonicalDirectory)) error("Directory is outside shared storage")
    if (directory.absoluteFile.path != canonicalDirectory.path && directory.absoluteFile != canonicalRoot) error("Symbolic-link directories are not supported")
    if (!canonicalDirectory.isDirectory || !canonicalDirectory.canRead()) error("Directory is unavailable")
    val children = ArrayList<File>(256)
    var entryLimitReached = false
    java.nio.file.Files.newDirectoryStream(canonicalDirectory.toPath()).use { stream ->
        val iterator = stream.iterator()
        while (iterator.hasNext()) {
            coroutineContext.ensureActive()
            if (children.size >= 4096) {
                entryLimitReached = true
                break
            }
            children += iterator.next().toFile()
        }
    }
    sortFileManagerChildren(children, sortMode)
    val folders = ArrayList<String>()
    val files = ArrayList<SharedStorageFileEntry>()
    for (child in children) {
        coroutineContext.ensureActive()
        val canonical = runCatching { child.canonicalFile }.getOrNull() ?: continue
        if (!isCanonicalSharedStoragePath(canonicalRoot, canonical)) continue
        if (child.absoluteFile.path != canonical.path) continue
        if (canonical.isDirectory && canonical.canRead()) {
            folders += canonical.path
            continue
        }
        if (!includeFiles || !canonical.isFile || !canonical.canRead()) continue
        val extension = canonical.extension.lowercase(java.util.Locale.ROOT)
        if (extension in allowedExtensions) {
            files += SharedStorageFileEntry(
                path = canonical.path,
                name = canonical.name.take(512),
                sizeBytes = canonical.length().coerceAtLeast(0L),
                modifiedAtMillis = canonical.lastModified().coerceAtLeast(0L),
                createdAtMillis = fileManagerCreationMillis(canonical)
            )
        }
    }
    return SharedStorageDirectoryListing(folders, files, entryLimitReached)
}

internal fun validatedSharedStorageImportFile(file: File, allowedExtensions: Set<String>, maxBytes: Long): File {
    val root = sharedStorageRoot()
    val canonical = file.canonicalFile
    if (!isCanonicalSharedStoragePath(root, canonical) || file.absoluteFile.path != canonical.path) error("Selected file is outside approved shared storage")
    if (!canonical.isFile || !canonical.canRead()) error("Selected file is unavailable")
    if (canonical.extension.lowercase(java.util.Locale.ROOT) !in allowedExtensions) error("Selected file type is not allowed")
    val length = canonical.length()
    if (length <= 0L || length > maxBytes) error("Selected file exceeds the allowed size")
    return canonical
}

internal fun validatedSharedStorageDestination(directory: File, filename: String, allowedExtensions: Set<String>): File {
    val root = sharedStorageRoot()
    val canonicalDirectory = directory.canonicalFile
    if (!isCanonicalSharedStoragePath(root, canonicalDirectory) || !canonicalDirectory.isDirectory || !canonicalDirectory.canWrite()) error("Destination folder is unavailable")
    val clean = filename.trim()
    if (clean.isBlank() || clean.length > 180 || clean == "." || clean == ".." || clean.contains('/') || clean.contains('\\') || clean.any { it.code < 0x20 }) error("Invalid file name")
    if (File(clean).name != clean) error("Invalid file name")
    if (clean.substringAfterLast('.', "").lowercase(java.util.Locale.ROOT) !in allowedExtensions) error("File extension is not allowed")
    val target = File(canonicalDirectory, clean).canonicalFile
    if (!isCanonicalSharedStoragePath(root, target) || target.parentFile?.canonicalFile != canonicalDirectory) error("Unsafe destination path")
    if (target.exists() && (!target.isFile || target.absoluteFile.path != target.canonicalFile.path)) error("Destination is not a regular file")
    return target
}

internal fun replaceSharedStorageFileAtomically(destination: File, writer: (FileOutputStream) -> Unit) {
    val directory = destination.parentFile?.canonicalFile ?: error("Destination folder is unavailable")
    val temp = File(directory, ".vulkanscope-${java.util.UUID.randomUUID()}.tmp").canonicalFile
    if (temp.parentFile != directory) error("Unsafe temporary destination")
    try {
        FileOutputStream(temp, false).use { output ->
            writer(output)
            output.flush()
            output.fd.sync()
        }
        try {
            java.nio.file.Files.move(
                temp.toPath(),
                destination.toPath(),
                java.nio.file.StandardCopyOption.ATOMIC_MOVE,
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
            )
        } catch (_: java.nio.file.AtomicMoveNotSupportedException) {
            java.nio.file.Files.move(temp.toPath(), destination.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING)
        }
        if (!destination.isFile || destination.length() <= 0L) error("Saved file could not be verified")
    } catch (error: Throwable) {
        runCatching { temp.delete() }
        throw error
    }
}

internal fun writeSharedStorageBytes(destination: File, bytes: ByteArray, maxBytes: Int) {
    if (bytes.isEmpty() || bytes.size > maxBytes) error("Export exceeds the allowed size")
    replaceSharedStorageFileAtomically(destination) { output -> output.write(bytes) }
}

internal fun copySharedStorageFile(destination: File, source: File, maxBytes: Long = 64L * 1024L * 1024L) {
    val canonicalSource = source.canonicalFile
    if (!canonicalSource.isFile || !canonicalSource.canRead()) error("Export snapshot is unavailable")
    val size = canonicalSource.length()
    if (size <= 0L || size > maxBytes) error("Export snapshot exceeds the allowed size")
    replaceSharedStorageFileAtomically(destination) { output ->
        FileInputStream(canonicalSource).use { input -> input.copyTo(output, 64 * 1024) }
    }
}

internal data class SystemDriverSummary(
    val capturedAtMillis: Long,
    val deviceName: String,
    val apiVersion: String,
    val driverVersion: String,
    val driverName: String?,
    val driverInfo: String?,
    val driverId: String?,
    val conformanceVersion: String?,
    val vendorId: String,
    val deviceId: String,
    val deviceType: String,
    val loaderVersion: String,
    val instanceApiVersion: String
)

internal fun turnipManagerRoot(filesDir: File): File = File(filesDir, TURNIP_MANAGER_ROOT_NAME)
internal fun turnipSlotRoot(filesDir: File, slot: Int): File = File(turnipManagerRoot(filesDir), "driver_%02d".format(java.util.Locale.ROOT, slot))
internal fun turnipSlotBundleRoot(filesDir: File, slot: Int): File = File(turnipSlotRoot(filesDir, slot), TURNIP_MANAGER_BUNDLE_NAME)
internal fun turnipSlotSourceFile(filesDir: File, slot: Int): File = File(turnipSlotRoot(filesDir, slot), TURNIP_MANAGER_SOURCE_NAME)

internal fun systemDriverSummaryFromReport(report: VulkanReport): SystemDriverSummary? {
    val device = report.devices.firstOrNull() ?: return null
    fun property(name: String): String? = device.detailedProperties.firstOrNull { it.name == name }?.value?.trim()?.takeIf { it.isNotBlank() }?.take(2048)
    return SystemDriverSummary(
        capturedAtMillis = System.currentTimeMillis(),
        deviceName = device.name.take(512),
        apiVersion = device.apiVersion.take(128),
        driverVersion = device.driverVersionText.ifBlank { device.driverVersion }.take(512),
        driverName = property("driverName"),
        driverInfo = property("driverInfo"),
        driverId = property("driverID"),
        conformanceVersion = property("conformanceVersion"),
        vendorId = device.vendorId.take(128),
        deviceId = device.deviceId.take(128),
        deviceType = device.deviceType.take(256),
        loaderVersion = report.loaderVersion.take(128),
        instanceApiVersion = report.instanceApiVersion.take(128)
    )
}

internal fun readSystemDriverSummary(prefs: android.content.SharedPreferences): SystemDriverSummary? {
    val raw = prefs.getString("system_driver_summary_v1", null) ?: return null
    if (raw.length > 16_384) return null
    return runCatching {
        val root = JSONObject(raw)
        fun required(name: String, max: Int): String = root.optString(name).take(max).takeIf { it.isNotBlank() } ?: error("Missing System driver summary field")
        fun optional(name: String, max: Int): String? = root.optString(name).trim().take(max).takeIf { it.isNotBlank() }
        SystemDriverSummary(
            capturedAtMillis = root.optLong("capturedAtMillis").takeIf { it > 0L } ?: error("Invalid System driver summary timestamp"),
            deviceName = required("deviceName", 512),
            apiVersion = required("apiVersion", 128),
            driverVersion = required("driverVersion", 512),
            driverName = optional("driverName", 2048),
            driverInfo = optional("driverInfo", 2048),
            driverId = optional("driverId", 256),
            conformanceVersion = optional("conformanceVersion", 256),
            vendorId = required("vendorId", 128),
            deviceId = required("deviceId", 128),
            deviceType = required("deviceType", 256),
            loaderVersion = required("loaderVersion", 128),
            instanceApiVersion = required("instanceApiVersion", 128)
        )
    }.getOrNull()
}

internal fun persistSystemDriverSummary(prefs: android.content.SharedPreferences, summary: SystemDriverSummary) {
    val root = JSONObject()
        .put("capturedAtMillis", summary.capturedAtMillis)
        .put("deviceName", summary.deviceName)
        .put("apiVersion", summary.apiVersion)
        .put("driverVersion", summary.driverVersion)
        .put("driverName", summary.driverName)
        .put("driverInfo", summary.driverInfo)
        .put("driverId", summary.driverId)
        .put("conformanceVersion", summary.conformanceVersion)
        .put("vendorId", summary.vendorId)
        .put("deviceId", summary.deviceId)
        .put("deviceType", summary.deviceType)
        .put("loaderVersion", summary.loaderVersion)
        .put("instanceApiVersion", summary.instanceApiVersion)
    prefs.edit().putString("system_driver_summary_v1", root.toString()).apply()
}

internal fun scanTurnipBundle(root: File, maxEntries: Int): TurnipBundleScan? {
    if (maxEntries <= 0) return null
    val canonicalRoot = runCatching { root.canonicalFile }.getOrNull() ?: return null
    if (!canonicalRoot.isDirectory) return null
    val rootPrefix = canonicalRoot.path.trimEnd(File.separatorChar) + File.separator
    val seenCanonicalPaths = HashSet<String>()
    val files = ArrayList<File>()
    val directories = java.util.ArrayDeque<File>()
    directories.add(canonicalRoot)
    var visitedEntries = 0
    while (directories.isNotEmpty()) {
        val directory = directories.removeLast()
        val children = runCatching { directory.listFiles() }.getOrNull() ?: return null
        for (entry in children) {
            if (++visitedEntries > maxEntries) return null
            val canonicalEntry = runCatching { entry.canonicalFile }.getOrNull() ?: return null
            if (!canonicalEntry.path.startsWith(rootPrefix)) return null
            if (canonicalEntry.path != entry.absoluteFile.path) return null
            if (!seenCanonicalPaths.add(canonicalEntry.path)) return null
            when {
                entry.isDirectory -> directories.add(canonicalEntry)
                entry.isFile -> files.add(canonicalEntry)
                else -> return null
            }
        }
    }
    return TurnipBundleScan(files)
}

internal fun ensureTurnipNativeLibrariesReadOnly(scan: TurnipBundleScan): Boolean {
    val libraries = scan.files.filter { it.name.endsWith(".so", true) }
    if (libraries.isEmpty()) return false
    return libraries.all { library ->
        if (!library.isFile || !library.canRead()) return@all false
        runCatching {
            if (library.canWrite() && !library.setReadOnly()) false else !library.canWrite()
        }.getOrDefault(false)
    }
}

internal fun resolveTurnipLibrary(bundleRoot: File): File? {
    val scan = scanTurnipBundle(bundleRoot, 2048) ?: return null
    val metadataFiles = scan.files.filter { it.name.equals("meta.json", true) }
    if (metadataFiles.size != 1) return null
    val metadata = runCatching { JSONObject(readFileTextLimited(metadataFiles.single(), 1024 * 1024)) }.getOrNull() ?: return null
    if (metadata.optInt("schemaVersion", -1) != 1) return null
    val declared = metadata.optString("libraryName").trim()
    if (declared.isBlank() || declared.contains('/') || declared.contains('\\') || !declared.endsWith(".so", true) || !declared.contains("vulkan", true)) return null
    val libraries = scan.files.filter { it.name == declared }
    if (libraries.size != 1) return null
    val library = libraries.single()
    if (!library.isFile || !library.canRead() || library.length() <= 0L) return null
    if (!ensureTurnipNativeLibrariesReadOnly(scan)) return null
    if (library.canWrite()) return null
    return library
}
internal fun readTurnipSourceInfo(sourceFile: File): TurnipSourceInfo? {
    if (!sourceFile.isFile) return null
    val source = runCatching { JSONObject(readFileTextLimited(sourceFile, 64 * 1024)) }.getOrNull() ?: return null
    val name = source.optString("name").trim().take(512).takeIf { it.isNotEmpty() } ?: return null
    val location = source.optString("location").trim().take(4096).takeIf { it.isNotEmpty() } ?: "Private imported source"
    val size = source.optLong("sizeBytes", -1L).takeIf { it >= 0L }
    val modified = source.optLong("modifiedAtMillis", -1L).takeIf { it > 0L }
    return TurnipSourceInfo(name, location, size, modified)
}

internal fun writeTurnipSourceInfo(sourceFile: File, source: TurnipSourceInfo, importedAtMillis: Long): Boolean {
    val parent = sourceFile.parentFile ?: return false
    if (!parent.mkdirs() && !parent.isDirectory) return false
    val payload = JSONObject().apply {
        put("name", source.name.take(512))
        put("location", source.location.take(4096))
        source.sizeBytes?.let { put("sizeBytes", it) }
        source.modifiedAtMillis?.let { put("modifiedAtMillis", it) }
        put("importedAtMillis", importedAtMillis)
    }.toString().toByteArray(Charsets.UTF_8)
    if (payload.size > 64 * 1024) return false
    val temp = File(parent, sourceFile.name + ".tmp")
    return runCatching {
        FileOutputStream(temp).use { output -> output.write(payload); output.fd.sync() }
        if (sourceFile.exists() && !sourceFile.delete()) return@runCatching false
        if (!temp.renameTo(sourceFile)) return@runCatching false
        true
    }.getOrDefault(false).also { if (!it) temp.delete() }
}

internal fun legacyTurnipSourceInfo(prefs: android.content.SharedPreferences): Pair<TurnipSourceInfo?, Long?> {
    val name = prefs.getString("turnip_source_name", null)?.takeIf { it.isNotBlank() }
    val location = prefs.getString("turnip_source_location", null)?.takeIf { it.isNotBlank() }
    val size = prefs.getLong("turnip_source_size", -1L).takeIf { it >= 0L }
    val modified = prefs.getLong("turnip_source_modified", -1L).takeIf { it > 0L }
    val imported = prefs.getLong("turnip_imported_at", -1L).takeIf { it > 0L }
    return if (name != null) TurnipSourceInfo(name, location ?: "Legacy imported source", size, modified) to imported else null to imported
}

internal fun readTurnipBundleInfo(bundleRoot: File, sourceInfo: TurnipSourceInfo?, importedAtMillis: Long?): TurnipBundleInfo {
    val library = resolveTurnipLibrary(bundleRoot) ?: return TurnipBundleInfo(false, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
    val scan = scanTurnipBundle(bundleRoot, 2048) ?: return TurnipBundleInfo(false, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
    val metadataFile = scan.files.singleOrNull { it.name.equals("meta.json", true) }
    val metadata = metadataFile?.let { runCatching { JSONObject(readFileTextLimited(it, 1024 * 1024)) }.getOrNull() }
    fun text(key: String): String? = metadata?.optString(key)?.trim()?.takeIf { it.isNotEmpty() }
    val minApi = metadata?.optInt("minApi", -1)?.takeIf { it >= 0 }
    val schemaVersion = metadata?.optInt("schemaVersion", -1)?.takeIf { it >= 0 }
    return TurnipBundleInfo(
        installed = true,
        zipName = sourceInfo?.name,
        zipLocation = sourceInfo?.location,
        zipSizeBytes = sourceInfo?.sizeBytes,
        zipModifiedAtMillis = sourceInfo?.modifiedAtMillis,
        importedAtMillis = importedAtMillis,
        driverName = text("name"),
        driverVersion = text("driverVersion"),
        driverDate = text("driverDate") ?: text("date"),
        description = text("description"),
        packageVersion = text("packageVersion"),
        vendor = text("vendor"),
        author = text("author"),
        minApi = minApi,
        schemaVersion = schemaVersion,
        libraryName = metadata?.optString("libraryName")?.trim()?.takeIf { it.isNotEmpty() } ?: library.name,
        librarySizeBytes = library.length().takeIf { it >= 0L }
    )
}
internal fun turnipSourceAvailable(context: Context, source: TurnipSourceInfo?): Boolean {
    val location = source?.location?.trim().orEmpty()
    if (location.isEmpty()) return false
    return if (location.startsWith(File.separator)) runCatching {
        File(location).canonicalFile.let { it.isFile && it.canRead() && it.length() > 0L }
    }.getOrDefault(false) else false
}

internal fun readManagedTurnipDrivers(context: Context, prefs: android.content.SharedPreferences, activeMode: DriverMode): List<ManagedTurnipDriver> {
    val filesDir = context.filesDir
    val activeSlot = prefs.getInt(TURNIP_ACTIVE_SLOT_PREF, 0)
    return (1..TURNIP_MANAGER_MAX_DRIVERS).mapNotNull { slot ->
        val slotRoot = turnipSlotRoot(filesDir, slot)
        if (!slotRoot.exists()) return@mapNotNull null
        val sourceFile = turnipSlotSourceFile(filesDir, slot)
        val source = readTurnipSourceInfo(sourceFile)
        val imported = if (sourceFile.isFile) runCatching { JSONObject(readFileTextLimited(sourceFile, 64 * 1024)).optLong("importedAtMillis", -1L).takeIf { it > 0L } }.getOrNull() else null
        val info = readTurnipBundleInfo(turnipSlotBundleRoot(filesDir, slot), source, imported)
        ManagedTurnipDriver(slot, activeMode == DriverMode.TURNIP && slot == activeSlot, turnipSourceAvailable(context, source), info)
    }
}

internal fun migrateLegacyTurnipBundleIfNeeded(filesDir: File, prefs: android.content.SharedPreferences): Boolean {
    val legacy = File(filesDir, "turnip")
    if (!legacy.isDirectory || resolveTurnipLibrary(legacy) == null) return false
    if ((1..TURNIP_MANAGER_MAX_DRIVERS).any { turnipSlotRoot(filesDir, it).exists() }) return false
    val slot = 1
    val slotRoot = turnipSlotRoot(filesDir, slot)
    val bundleRoot = turnipSlotBundleRoot(filesDir, slot)
    val managerRoot = turnipManagerRoot(filesDir)
    if (!managerRoot.mkdirs() && !managerRoot.isDirectory) return false
    if (!slotRoot.mkdir()) return false
    if (!legacy.renameTo(bundleRoot)) {
        slotRoot.deleteRecursively()
        return false
    }
    val legacySource = legacyTurnipSourceInfo(prefs)
    if (legacySource.first != null && !writeTurnipSourceInfo(turnipSlotSourceFile(filesDir, slot), legacySource.first!!, legacySource.second ?: System.currentTimeMillis())) {
        bundleRoot.renameTo(legacy)
        slotRoot.deleteRecursively()
        return false
    }
    val committed = prefs.edit().putInt(TURNIP_ACTIVE_SLOT_PREF, slot).putString("turnip_bundle", bundleRoot.absolutePath).commit()
    if (!committed) {
        turnipSlotSourceFile(filesDir, slot).delete()
        bundleRoot.renameTo(legacy)
        slotRoot.deleteRecursively()
        return false
    }
    return true
}

internal fun activeTurnipBundleRoot(filesDir: File, prefs: android.content.SharedPreferences): File? {
    val activeSlot = prefs.getInt(TURNIP_ACTIVE_SLOT_PREF, 0)
    if (activeSlot in 1..TURNIP_MANAGER_MAX_DRIVERS) {
        val managed = turnipSlotBundleRoot(filesDir, activeSlot)
        if (resolveTurnipLibrary(managed) != null) return managed
    }
    val legacy = File(filesDir, "turnip")
    return legacy.takeIf { resolveTurnipLibrary(it) != null }
}

internal fun formatTimestampOrUnavailable(value: Long?): String = value?.let {
    java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.MEDIUM, java.text.DateFormat.SHORT).format(java.util.Date(it))
} ?: "Not available"

internal fun readResponseTextLimited(body: ResponseBody, maxBytes: Int): String {
    require(maxBytes > 0)
    val declared = body.contentLength()
    if (declared > maxBytes) error("Response exceeds the safety limit.")
    val output = ByteArrayOutputStream(minOf(maxBytes, 64 * 1024))
    body.byteStream().use { input ->
        val buffer = ByteArray(8192)
        var total = 0
        while (true) {
            val count = input.read(buffer)
            if (count < 0) break
            total += count
            if (total > maxBytes) error("Response exceeds the safety limit.")
            output.write(buffer, 0, count)
        }
    }
    return output.toString(Charsets.UTF_8.name())
}
