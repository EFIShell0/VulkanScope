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
import android.util.JsonReader
import android.util.JsonToken
import android.hardware.display.DisplayManager
import android.content.Intent
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.provider.Settings
import androidx.core.content.FileProvider
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
import java.util.Collections
import java.util.concurrent.TimeUnit
import okhttp3.Call
import okhttp3.Dns
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.ShortNavigationBarItemDefaults
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject

private val VulkanBlack = ComposeColor(0xFF000000)
private val VulkanSurface = ComposeColor(0xFF101010)
private val VulkanSurfaceLow = ComposeColor(0xFF111111)
private val VulkanSurfaceRaised = ComposeColor(0xFF181516)
private val VulkanSurfaceTonal = ComposeColor(0xFF211E1F)
private val VulkanAccentContainer = ComposeColor(0xFF351719)
private val VulkanAccent = ComposeColor(0xFFA41E22)
private val VulkanAccentSoft = ComposeColor(0xFFE2676A)
private val VulkanTextPrimary = ComposeColor(0xFFF7F2F3)
private val VulkanTextSecondary = ComposeColor(0xFFB6ACAE)
private val VulkanTextMuted = ComposeColor(0xFF968D8F)
private val VulkanOutline = ComposeColor(0xFF494244)
private val VulkanOutlineVariant = ComposeColor(0xFF2A2527)
private val LocalDetailKeyValuePresentation = staticCompositionLocalOf { false }
private data class EvidenceActionEnvironment(val openEncyclopedia: (String) -> Unit, val addWatch: (String) -> Unit)
private val LocalEvidenceActionEnvironment = staticCompositionLocalOf<EvidenceActionEnvironment?> { null }
private typealias SharedStorageAccessRequest = ((() -> Unit), (() -> Unit)) -> Unit
private val LocalSharedStorageAccessRequest = staticCompositionLocalOf<SharedStorageAccessRequest> { { granted, _ -> granted() } }
private val VulkanExpressiveShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(22.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp),
    largeIncreased = RoundedCornerShape(32.dp),
    extraLargeIncreased = RoundedCornerShape(36.dp),
    extraExtraLarge = RoundedCornerShape(40.dp)
)

private val VulkanBaseTypography = Typography()
private val VulkanTypography = Typography(
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


private val VULKAN_TRADEMARK_DISPLAY_REGEX = Regex("""\bVulkan(?!Scope|®)""")

private fun trademarkVulkanDisplayText(text: String): String = VULKAN_TRADEMARK_DISPLAY_REGEX.replace(text, "Vulkan®")

private data class DisplayReport(
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

private data class ExtensionEntry(val name: String, val scope: String, val specVersion: Int, val supported: Boolean = true)
private data class LayerEntry(
    val name: String,
    val description: String,
    val specVersion: Int,
    val implementationVersion: Int,
    val extensions: List<ExtensionEntry> = emptyList(),
    val extensionStatus: String = "unknown",
    val extensionReason: String = "",
    val extensionsComplete: Boolean = false
)
private data class FeatureEntry(val name: String, val supported: Boolean)
private data class SurfaceFormatEntry(val format: String, val colorSpace: String, val classification: String, val description: String, val supported: Boolean = true)
private data class FormatEntry(val name: String, val supported: Boolean, val linear: Long, val optimal: Long, val buffer: Long)
private data class PropertyEntry(val section: String, val name: String, val value: String)
private data class ImageFormatQueryResultEntry(val name: String, val status: String, val vkResult: Int?, val reason: String = "")

private fun parseImageFormatQueryResults(array: JSONArray): List<ImageFormatQueryResultEntry> = (0 until array.length()).mapNotNull { index ->
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
private data class QueueEntry(val index: Int, val count: Int, val timestampBits: Int, val flags: Long, val graphics: Boolean, val compute: Boolean, val transfer: Boolean, val sparse: Boolean, val protected: Boolean, val videoDecode: Boolean, val videoEncode: Boolean, val opticalFlow: Boolean, val dataGraph: Boolean, val unknownFlags: Long, val granularity: String, val videoCodecOperations: Long = 0L, val videoCodecQueryStatus: String = "unknown", val videoCodecQueryReason: String = "")
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

private data class PresentationQueueEvidence(
    val queueFamily: Int,
    val supported: Boolean,
    val queryResult: Int? = null
)

private data class DeviceReport(
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

private data class RegistryCoverage(
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

private data class VulkanReport(
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

private enum class Page(val title: String) {
    Overview("Overview"), Vulkan("Vulkan"), Display("Display & HDR"), Surface("Surface"), Features("Features"), Memory("Memory"), Queues("Queues"), Video("Vulkan Video"), Formats("Formats"), Properties("Properties & Limits"), Extensions("Extensions"), Profiles("Profiles"), Encyclopedia("Encyclopedia"), Analysis("Analysis workspace"), Settings("Settings"), Info("Info")
}

private enum class SettingsSection(val label: String, val description: String, val icon: Int) {
    INFO("Info", "Developer, application, libraries, build, Android/device and Vulkan registry information.", R.drawable.ic_info),
    REPORTS("Reports & Database", "Complete TXT/HTML export, Database submission, compatibility state and public report access.", R.drawable.ic_database_submit),
    DRIVER_UPDATES("Driver & Update Preferences", "Vulkan driver management, built-in GitHub update preference and Obtainium guidance.", R.drawable.ic_settings)
}

private enum class DriverMode(val label: String) {
    SYSTEM("System Vulkan driver"),
    TURNIP("Turnip / third-party driver")
}

private enum class TurnipSupport { UNKNOWN, SUPPORTED, UNSUPPORTED }
private enum class CollectionStatus { IDLE, COLLECTING, COMPLETED, FAILED }
private enum class NetworkBannerState { HIDDEN, CONNECTED, DISCONNECTED }
private val LocalValidatedNetwork = staticCompositionLocalOf { false }

private fun hasValidatedInternetCapabilities(capabilities: NetworkCapabilities): Boolean =
    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

private fun hasValidatedInternet(context: Context): Boolean {
    val manager = context.getSystemService(ConnectivityManager::class.java)
    val network = runCatching { manager.activeNetwork }.getOrNull() ?: return false
    val capabilities = runCatching { manager.getNetworkCapabilities(network) }.getOrNull() ?: return false
    return hasValidatedInternetCapabilities(capabilities)
}

private fun isCompleteReportReady(report: VulkanReport, collectionStatus: CollectionStatus): Boolean =
    report.baseReportComplete && report.devices.isNotEmpty() && report.error == null && collectionStatus != CollectionStatus.COLLECTING

private fun collectionFinishedSuccessfully(report: VulkanReport?): Boolean {
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
private data class AppUpdate(
    val version: String,
    val assetName: String,
    val downloadUrl: String,
    val releaseNotes: String,
    val installedAbi: String,
    val downloadAbi: String,
    val installedVersion: String,
    val installedVersionCode: Long
)
private const val OFFICIAL_DATABASE_API_ENDPOINT = "https://vulkanscope-database-api.vulkanscope.workers.dev"
private const val OFFICIAL_DATABASE_WEB_URL = "https://efishell0.github.io/VulkanScope_database/"
private const val BACKGROUND_COLLECTION_BUDGET_MS = 60_000L
private const val TURNIP_ARCHIVE_INPUT_MAX_BYTES = 96L * 1024L * 1024L

private class BoundedDriverArchiveInputStream(input: InputStream, private val maxBytes: Long) : FilterInputStream(input) {
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

private fun strictProbeTerminalCandidate(candidate: String, baseGroup: Boolean): Boolean = runCatching {
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
private sealed interface UpdateStatus {
    data object Hidden : UpdateStatus
    data object Checking : UpdateStatus
    data object UpToDate : UpdateStatus
    data object DirectUpdatesDisabledIntro : UpdateStatus
    data class Available(val update: AppUpdate) : UpdateStatus
    data class Downloading(val update: AppUpdate) : UpdateStatus
    data class Failed(val message: String) : UpdateStatus
}

private enum class UpdateTransferPhase {
    CONNECTING,
    DOWNLOADING,
    PAUSED,
    VERIFYING,
    COMPLETED,
    CANCELED,
    FAILED
}

private data class UpdateTransferState(
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

private val ipv6FirstDns = Dns { hostname ->
    Dns.SYSTEM.lookup(hostname).sortedWith(compareBy<InetAddress> { if (it is Inet6Address) 0 else 1 })
}

private val ipv6PreferredHttpClient = OkHttpClient.Builder()
    .dns(ipv6FirstDns)
    .fastFallback(true)
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(20, TimeUnit.SECONDS)
    .followRedirects(true)
    .followSslRedirects(true)
    .build()

private val databaseHttpClient = ipv6PreferredHttpClient.newBuilder()
    .followRedirects(false)
    .followSslRedirects(false)
    .build()

private val ipv6PreferredDownloadClient = ipv6PreferredHttpClient.newBuilder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .build()

private fun readFileTextLimited(file: File, maxBytes: Int): String {
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

private const val TURNIP_MANAGER_MAX_DRIVERS = 10
private const val TURNIP_MANAGER_ROOT_NAME = "turnip_drivers"
private const val TURNIP_MANAGER_BUNDLE_NAME = "bundle"
private const val TURNIP_MANAGER_SOURCE_NAME = "source.json"
private const val TURNIP_ACTIVE_SLOT_PREF = "turnip_active_slot"

private data class TurnipBundleScan(val files: List<File>)

private data class TurnipSourceInfo(
    val name: String,
    val location: String,
    val sizeBytes: Long?,
    val modifiedAtMillis: Long?
)

private data class TurnipBundleInfo(
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

private data class ManagedTurnipDriver(
    val slot: Int,
    val selected: Boolean,
    val sourceAvailable: Boolean,
    val info: TurnipBundleInfo
)

private enum class TurnipFileManagerViewMode { LIST, COMPACT, GRID, DETAILS }

private fun turnipImportedSourceKey(path: String, name: String): String = path.trim() + "\u0000" + name.trim()

private data class TurnipArchiveCandidate(
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

private data class TurnipDirectoryListing(
    val folders: List<String>,
    val candidates: List<TurnipArchiveCandidate>,
    val entryLimitReached: Boolean,
    val zipLimitReached: Boolean
)

private data class TurnipFileManagerSetup(
    val root: File,
    val remaining: Int,
    val listing: TurnipDirectoryListing,
    val importedSourceKeys: Set<String>
)

private data class TurnipFileManagerState(
    val visible: Boolean = false,
    val rootPath: String = "",
    val directoryPath: String = "",
    val folders: List<String> = emptyList(),
    val candidates: List<TurnipArchiveCandidate> = emptyList(),
    val importedSourceKeys: Set<String> = emptySet(),
    val selectedPaths: Set<String> = emptySet(),
    val maxSelectable: Int = 0,
    val loading: Boolean = false,
    val importing: Boolean = false,
    val status: String? = null,
    val viewMode: TurnipFileManagerViewMode = TurnipFileManagerViewMode.LIST,
    val details: TurnipArchiveCandidate? = null
)

private suspend fun inspectTurnipArchive(file: File): TurnipArchiveCandidate? {
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

private suspend fun scanTurnipFileManagerDirectory(root: File, directory: File): TurnipDirectoryListing {
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
    children.sortBy { it.name.lowercase(java.util.Locale.ROOT) }
    val folders = children.asSequence().mapNotNull { child ->
        coroutineContext.ensureActive()
        runCatching {
            val canonical = child.canonicalFile
            if (!canonical.isDirectory || !canonical.canRead()) null
            else if (canonical != canonicalRoot && !canonical.path.startsWith(prefix)) null
            else if (child.absoluteFile.path != canonical.path) null
            else canonical.path
        }.getOrNull()
    }.toList()
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
    candidates.sortBy { it.name.lowercase(java.util.Locale.ROOT) }
    return TurnipDirectoryListing(folders, candidates, entryLimitReached, zipLimitReached)
}

private enum class SharedStorageBrowserMode { IMPORT, EXPORT }

private data class SharedStorageBrowserRequest(
    val title: String,
    val description: String,
    val mode: SharedStorageBrowserMode,
    val allowedExtensions: Set<String>,
    val suggestedFileName: String = "",
    val maxImportBytes: Long? = null
)

private data class SharedStorageFileEntry(
    val path: String,
    val name: String,
    val sizeBytes: Long,
    val modifiedAtMillis: Long
)

private data class SharedStorageDirectoryListing(
    val folders: List<String>,
    val files: List<SharedStorageFileEntry>,
    val entryLimitReached: Boolean
)

private fun sharedStorageRoot(): File = android.os.Environment.getExternalStorageDirectory().canonicalFile

private fun isCanonicalSharedStoragePath(root: File, candidate: File): Boolean {
    val canonicalRoot = root.canonicalFile
    val canonicalCandidate = candidate.canonicalFile
    val prefix = canonicalRoot.path.trimEnd(File.separatorChar) + File.separator
    return canonicalCandidate == canonicalRoot || canonicalCandidate.path.startsWith(prefix)
}

private suspend fun scanSharedStorageDirectory(
    root: File,
    directory: File,
    allowedExtensions: Set<String>,
    includeFiles: Boolean
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
    children.sortBy { it.name.lowercase(java.util.Locale.ROOT) }
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
                modifiedAtMillis = canonical.lastModified().coerceAtLeast(0L)
            )
        }
    }
    return SharedStorageDirectoryListing(folders, files, entryLimitReached)
}

private fun validatedSharedStorageImportFile(file: File, allowedExtensions: Set<String>, maxBytes: Long): File {
    val root = sharedStorageRoot()
    val canonical = file.canonicalFile
    if (!isCanonicalSharedStoragePath(root, canonical) || file.absoluteFile.path != canonical.path) error("Selected file is outside approved shared storage")
    if (!canonical.isFile || !canonical.canRead()) error("Selected file is unavailable")
    if (canonical.extension.lowercase(java.util.Locale.ROOT) !in allowedExtensions) error("Selected file type is not allowed")
    val length = canonical.length()
    if (length <= 0L || length > maxBytes) error("Selected file exceeds the allowed size")
    return canonical
}

private fun validatedSharedStorageDestination(directory: File, filename: String, allowedExtensions: Set<String>): File {
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

private fun replaceSharedStorageFileAtomically(destination: File, writer: (FileOutputStream) -> Unit) {
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

private fun writeSharedStorageBytes(destination: File, bytes: ByteArray, maxBytes: Int) {
    if (bytes.isEmpty() || bytes.size > maxBytes) error("Export exceeds the allowed size")
    replaceSharedStorageFileAtomically(destination) { output -> output.write(bytes) }
}

private fun copySharedStorageFile(destination: File, source: File, maxBytes: Long = 64L * 1024L * 1024L) {
    val canonicalSource = source.canonicalFile
    if (!canonicalSource.isFile || !canonicalSource.canRead()) error("Export snapshot is unavailable")
    val size = canonicalSource.length()
    if (size <= 0L || size > maxBytes) error("Export snapshot exceeds the allowed size")
    replaceSharedStorageFileAtomically(destination) { output ->
        FileInputStream(canonicalSource).use { input -> input.copyTo(output, 64 * 1024) }
    }
}

private data class SystemDriverSummary(
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

private fun turnipManagerRoot(filesDir: File): File = File(filesDir, TURNIP_MANAGER_ROOT_NAME)
private fun turnipSlotRoot(filesDir: File, slot: Int): File = File(turnipManagerRoot(filesDir), "driver_%02d".format(java.util.Locale.ROOT, slot))
private fun turnipSlotBundleRoot(filesDir: File, slot: Int): File = File(turnipSlotRoot(filesDir, slot), TURNIP_MANAGER_BUNDLE_NAME)
private fun turnipSlotSourceFile(filesDir: File, slot: Int): File = File(turnipSlotRoot(filesDir, slot), TURNIP_MANAGER_SOURCE_NAME)

private fun systemDriverSummaryFromReport(report: VulkanReport): SystemDriverSummary? {
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

private fun readSystemDriverSummary(prefs: android.content.SharedPreferences): SystemDriverSummary? {
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

private fun persistSystemDriverSummary(prefs: android.content.SharedPreferences, summary: SystemDriverSummary) {
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

private fun scanTurnipBundle(root: File, maxEntries: Int): TurnipBundleScan? {
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

private fun ensureTurnipNativeLibrariesReadOnly(scan: TurnipBundleScan): Boolean {
    val libraries = scan.files.filter { it.name.endsWith(".so", true) }
    if (libraries.isEmpty()) return false
    return libraries.all { library ->
        if (!library.isFile || !library.canRead()) return@all false
        runCatching {
            if (library.canWrite() && !library.setReadOnly()) false else !library.canWrite()
        }.getOrDefault(false)
    }
}

private fun resolveTurnipLibrary(bundleRoot: File): File? {
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

private fun resolveInstalledTurnipLibrary(filesDir: File): File? = resolveTurnipLibrary(File(filesDir, "turnip"))

private fun readTurnipSourceInfo(sourceFile: File): TurnipSourceInfo? {
    if (!sourceFile.isFile) return null
    val source = runCatching { JSONObject(readFileTextLimited(sourceFile, 64 * 1024)) }.getOrNull() ?: return null
    val name = source.optString("name").trim().take(512).takeIf { it.isNotEmpty() } ?: return null
    val location = source.optString("location").trim().take(4096).takeIf { it.isNotEmpty() } ?: "Private imported source"
    val size = source.optLong("sizeBytes", -1L).takeIf { it >= 0L }
    val modified = source.optLong("modifiedAtMillis", -1L).takeIf { it > 0L }
    return TurnipSourceInfo(name, location, size, modified)
}

private fun writeTurnipSourceInfo(sourceFile: File, source: TurnipSourceInfo, importedAtMillis: Long): Boolean {
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

private fun legacyTurnipSourceInfo(prefs: android.content.SharedPreferences): Pair<TurnipSourceInfo?, Long?> {
    val name = prefs.getString("turnip_source_name", null)?.takeIf { it.isNotBlank() }
    val location = prefs.getString("turnip_source_location", null)?.takeIf { it.isNotBlank() }
    val size = prefs.getLong("turnip_source_size", -1L).takeIf { it >= 0L }
    val modified = prefs.getLong("turnip_source_modified", -1L).takeIf { it > 0L }
    val imported = prefs.getLong("turnip_imported_at", -1L).takeIf { it > 0L }
    return if (name != null) TurnipSourceInfo(name, location ?: "Legacy imported source", size, modified) to imported else null to imported
}

private fun readTurnipBundleInfo(bundleRoot: File, sourceInfo: TurnipSourceInfo?, importedAtMillis: Long?): TurnipBundleInfo {
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

private fun readInstalledTurnipBundleInfo(filesDir: File, prefs: android.content.SharedPreferences): TurnipBundleInfo {
    val activeSlot = prefs.getInt(TURNIP_ACTIVE_SLOT_PREF, 0)
    if (activeSlot in 1..TURNIP_MANAGER_MAX_DRIVERS) {
        val sourceFile = turnipSlotSourceFile(filesDir, activeSlot)
        val source = readTurnipSourceInfo(sourceFile)
        val imported = if (sourceFile.isFile) runCatching { JSONObject(readFileTextLimited(sourceFile, 64 * 1024)).optLong("importedAtMillis", -1L).takeIf { it > 0L } }.getOrNull() else null
        val info = readTurnipBundleInfo(turnipSlotBundleRoot(filesDir, activeSlot), source, imported)
        if (info.installed) return info
    }
    val legacy = legacyTurnipSourceInfo(prefs)
    return readTurnipBundleInfo(File(filesDir, "turnip"), legacy.first, legacy.second)
}

private fun turnipSourceAvailable(context: Context, source: TurnipSourceInfo?): Boolean {
    val location = source?.location?.trim().orEmpty()
    if (location.isEmpty()) return false
    return if (location.startsWith(File.separator)) runCatching {
        File(location).canonicalFile.let { it.isFile && it.canRead() && it.length() > 0L }
    }.getOrDefault(false) else false
}

private fun readManagedTurnipDrivers(context: Context, prefs: android.content.SharedPreferences, activeMode: DriverMode): List<ManagedTurnipDriver> {
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

private fun migrateLegacyTurnipBundleIfNeeded(filesDir: File, prefs: android.content.SharedPreferences): Boolean {
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

private fun activeTurnipBundleRoot(filesDir: File, prefs: android.content.SharedPreferences): File? {
    val activeSlot = prefs.getInt(TURNIP_ACTIVE_SLOT_PREF, 0)
    if (activeSlot in 1..TURNIP_MANAGER_MAX_DRIVERS) {
        val managed = turnipSlotBundleRoot(filesDir, activeSlot)
        if (resolveTurnipLibrary(managed) != null) return managed
    }
    val legacy = File(filesDir, "turnip")
    return legacy.takeIf { resolveTurnipLibrary(it) != null }
}

private fun formatTimestampOrUnavailable(value: Long?): String = value?.let {
    java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.MEDIUM, java.text.DateFormat.SHORT).format(java.util.Date(it))
} ?: "Not available"

private fun readResponseTextLimited(body: ResponseBody, maxBytes: Int): String {
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

class MainActivity : ComponentActivity() {

    private lateinit var prefs: android.content.SharedPreferences
    private var turnipSupport by mutableStateOf(TurnipSupport.UNKNOWN)
    private var driverMode by mutableStateOf(DriverMode.SYSTEM)
    private val surfaceLock = Any()
    private val collectMutex = Mutex()
    private val probeMutex = Mutex()
    private var currentSurface: Surface? = null
    private var surfaceGeneration = 0L
    private var surfaceRefreshPending = false
    private var surfaceHostGeneration by mutableStateOf(0L)
    private var driverSurfaceRebindPending = false
    private var latestReport by mutableStateOf<VulkanReport?>(null)
    private var displayReportState by mutableStateOf<DisplayReport?>(null)
    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) { refreshDisplayReport() }
        override fun onDisplayRemoved(displayId: Int) { refreshDisplayReport() }
        override fun onDisplayChanged(displayId: Int) { refreshDisplayReport() }
    }
    private var reportLoading by mutableStateOf(true)
    private var collectionStatus by mutableStateOf(CollectionStatus.IDLE)
    private var updateStatus by mutableStateOf<UpdateStatus>(UpdateStatus.Hidden)
    private var updateCheckInFlight by mutableStateOf(false)
    private var networkBannerState by mutableStateOf(NetworkBannerState.HIDDEN)
    private var networkBannerGeneration = 0L
    private var lastValidatedNetwork: Boolean? = null
    private var trackedDefaultNetwork: Network? = null
    private var validatedNetworkAvailable by mutableStateOf(false)
    private var networkStateKnown by mutableStateOf(false)
    private var pendingDriverModeConfirmation by mutableStateOf<DriverMode?>(null)
    private var pendingTurnipActivationSlot by mutableStateOf<Int?>(null)
    private var turnipManagerRevision by mutableIntStateOf(0)
    private var turnipManagerBusy by mutableStateOf(false)
    private var turnipFileManagerState by mutableStateOf(TurnipFileManagerState())
    private var turnipFileManagerScanJob: Job? = null
    private var turnipFileManagerScanGeneration = 0L
    private var storagePermissionDeniedFeedback by mutableStateOf(false)
    private var awaitingAllFilesAccessReturn = false
    private var pendingSharedStorageGranted: (() -> Unit)? = null
    private var pendingSharedStorageDenied: (() -> Unit)? = null
    private var storagePermissionFeedbackGeneration = 0L
    private var networkCallbackRegistered = false
    private var updateConfirmation by mutableStateOf<AppUpdate?>(null)
    private var updateTransferState by mutableStateOf<UpdateTransferState?>(null)
    private var updateCancelConfirmationVisible by mutableStateOf(false)
    private var directUpdatesEnabled by mutableStateOf(true)
    private var directUpdatesConsentVisible by mutableStateOf(false)
    private var pendingUpdateApk: File? = null
    private var updateCheckJob: Job? = null
    private var updateStatusHideJob: Job? = null
    private var updateDownloadJob: Job? = null
    @Volatile private var updateDownloadPaused = false
    @Volatile private var updateDownloadCancelRequested = false
    @Volatile private var activeUpdateCheckCall: Call? = null
    @Volatile private var activeUpdateDownloadCall: Call? = null
    private var collectionInFlight = false
    @Volatile private var driverImportInFlight = false
    private var driverModeValidationJob: Job? = null
    private var collectionPending = false
    private val pendingCollectionTasks = mutableSetOf<String>()
    private var collectionGeneration = 0L
    private var collectionCompletionJob: Job? = null
    private val backgroundQueryGroups = (ISOLATED_CORE_GROUPS.keys + ISOLATED_ADVANCED_GROUPS.keys).toSet()
    private val activityScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val requestedQueryGroups = Collections.synchronizedSet(mutableSetOf<String>())
    private var queryTimingMs by mutableStateOf<Map<String, Long>>(emptyMap())
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            activityScope.launch {
                trackedDefaultNetwork = network
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    delay(80L)
                    if (trackedDefaultNetwork == network) {
                        val capabilities = runCatching { getSystemService(ConnectivityManager::class.java).getNetworkCapabilities(network) }.getOrNull()
                        applyValidatedNetworkState(capabilities?.let(::hasValidatedInternetCapabilities) == true)
                    }
                }
            }
        }
        override fun onLost(network: Network) {
            activityScope.launch {
                if (trackedDefaultNetwork == network) {
                    trackedDefaultNetwork = null
                    applyValidatedNetworkState(false)
                }
            }
        }
        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            activityScope.launch {
                trackedDefaultNetwork = network
                applyValidatedNetworkState(hasValidatedInternetCapabilities(networkCapabilities))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        getSystemService(DisplayManager::class.java).registerDisplayListener(displayListener, null)
        refreshDisplayReport()
        registerNetworkStateCallback()
    }

    override fun onStop() {
        if (networkCallbackRegistered) {
            runCatching { getSystemService(ConnectivityManager::class.java).unregisterNetworkCallback(networkCallback) }
            networkCallbackRegistered = false
        }
        getSystemService(DisplayManager::class.java).unregisterDisplayListener(displayListener)
        super.onStop()
    }

    private fun registerNetworkStateCallback() {
        if (networkCallbackRegistered) return
        val manager = getSystemService(ConnectivityManager::class.java)
        runCatching {
            manager.registerDefaultNetworkCallback(networkCallback)
            networkCallbackRegistered = true
        }
        trackedDefaultNetwork = runCatching { manager.activeNetwork }.getOrNull()
        applyValidatedNetworkState(validatedDefaultNetwork())
    }

    private fun validatedDefaultNetwork(): Boolean = hasValidatedInternet(this)

    private fun applyValidatedNetworkState(validated: Boolean) {
        val previous = lastValidatedNetwork
        validatedNetworkAvailable = validated
        networkStateKnown = true
        if (previous == null) {
            lastValidatedNetwork = validated
            networkBannerState = NetworkBannerState.HIDDEN
            return
        }
        if (previous == validated) return
        lastValidatedNetwork = validated
        networkBannerGeneration += 1L
        val generation = networkBannerGeneration
        networkBannerState = if (validated) NetworkBannerState.CONNECTED else NetworkBannerState.DISCONNECTED
        activityScope.launch {
            var remainingVisibleMillis = 4_500L
            while (networkBannerGeneration == generation && remainingVisibleMillis > 0L) {
                val step = minOf(remainingVisibleMillis, 100L)
                delay(step)
                remainingVisibleMillis -= step
            }
            if (networkBannerGeneration == generation) networkBannerState = NetworkBannerState.HIDDEN
        }
    }

    override fun onResume() {
        super.onResume()
        refreshDisplayReport()
        val pending = pendingUpdateApk
        if (pending != null && pending.exists() && directUpdatesEnabled && (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || packageManager.canRequestPackageInstalls())) {
            pendingUpdateApk = null
            launchPackageInstaller(pending)
        } else if (pending != null && !directUpdatesEnabled) {
            runCatching { pending.delete() }
            pendingUpdateApk = null
        }
        if (awaitingAllFilesAccessReturn) {
            awaitingAllFilesAccessReturn = false
            val granted = pendingSharedStorageGranted
            val denied = pendingSharedStorageDenied
            pendingSharedStorageGranted = null
            pendingSharedStorageDenied = null
            if (android.os.Environment.isExternalStorageManager()) granted?.invoke() else {
                showStoragePermissionDeniedFeedback()
                denied?.invoke()
            }
        }
    }

    override fun onDestroy() {
        activeUpdateCheckCall?.cancel()
        activeUpdateDownloadCall?.cancel()
        updateCheckJob?.cancel()
        updateStatusHideJob?.cancel()
        updateDownloadJob?.cancel()
        turnipFileManagerScanJob?.cancel()
        stopVulkanProbeProcess()
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
        directUpdatesEnabled = prefs.getBoolean("direct_updates_enabled", true)
        if (runCatching { migrateLegacyTurnipBundleIfNeeded(filesDir, prefs) }.getOrDefault(false)) turnipManagerRevision += 1
        displayReportState = displayReport()
        driverMode = DriverMode.values().find { it.name == prefs.getString("driver_mode", DriverMode.SYSTEM.name) } ?: DriverMode.SYSTEM
        if (!isTurnipPlatformEligible()) turnipSupport = TurnipSupport.UNSUPPORTED
        setContent {
            CompositionLocalProvider(LocalSharedStorageAccessRequest provides { granted, denied -> requestSharedStorageAccess(granted, denied) }) {
            VulkanScopeApp(
                displayReport = displayReportState ?: DisplayReport("Unknown", "Unknown", null, "Unknown", emptyList(), "Unknown", "Unknown", "Unknown", emptyList(), "unknown"),
                report = latestReport,
                loading = reportLoading,
                collectionStatus = collectionStatus,
                updateStatus = updateStatus,
                updateCheckInFlight = updateCheckInFlight,
                networkBannerState = networkBannerState,
                validatedNetworkAvailable = validatedNetworkAvailable,
                networkStateKnown = networkStateKnown,
                updateConfirmation = updateConfirmation,
                updateTransferState = updateTransferState,
                updateCancelConfirmationVisible = updateCancelConfirmationVisible,
                onRequestUpdateConfirmation = { update -> updateConfirmation = update },
                onDismissUpdateConfirmation = { updateConfirmation = null },
                onConfirmUpdateDownload = { update ->
                    updateConfirmation = null
                    startUpdateDownload(update)
                },
                onPauseUpdateDownload = { pauseUpdateDownload() },
                onResumeUpdateDownload = { resumeUpdateDownload() },
                onRequestCancelUpdateDownload = { requestCancelUpdateDownload() },
                onDismissCancelUpdateDownload = { dismissCancelUpdateDownload() },
                onConfirmCancelUpdateDownload = { confirmCancelUpdateDownload() },
                onInstallDownloadedUpdate = { installDownloadedUpdate() },
                onCloseUpdateTransfer = { closeUpdateTransfer() },
                onCheckForUpdates = { checkForApplicationUpdate(showProgress = true) },
                directUpdatesEnabled = directUpdatesEnabled,
                directUpdatesConsentVisible = directUpdatesConsentVisible,
                onDirectUpdatesChanged = { enabled -> requestDirectUpdatesChanged(enabled) },
                onDismissDirectUpdatesConsent = { directUpdatesConsentVisible = false },
                onConfirmDirectUpdatesConsent = { confirmDirectUpdatesConsent() },
                surfaceReady = { hostGeneration, surface ->
                    val changed = synchronized(surfaceLock) {
                        if (hostGeneration != surfaceHostGeneration) {
                            false
                        } else if (currentSurface === surface) {
                            false
                        } else {
                            currentSurface = surface
                            surfaceGeneration += 1L
                            surfaceRefreshPending = true
                            true
                        }
                    }
                    if (changed) {
                        if (driverSurfaceRebindPending && hostGeneration == surfaceHostGeneration) {
                            driverSurfaceRebindPending = false
                            requestReportCollection()
                        } else if (latestReport == null && !collectionInFlight && pendingCollectionTasks.isEmpty()) {
                            requestReportCollection()
                        } else if (latestReport != null) {
                            requestSurfaceRefresh(surface)
                        }
                    }
                },
                surfaceDestroyed = { hostGeneration, surface ->
                    synchronized(surfaceLock) {
                        if (hostGeneration != surfaceHostGeneration) {
                            return@synchronized
                        }
                        if (currentSurface === surface) {
                            currentSurface = null
                            surfaceGeneration += 1L
                            surfaceRefreshPending = false
                        }
                    }
                },
                surfaceHostGeneration = surfaceHostGeneration,
                driverMode = driverMode,
                turnipSupport = turnipSupport,
                turnipManagerRevision = turnipManagerRevision,
                turnipManagerBusy = turnipManagerBusy,
                storagePermissionDeniedFeedback = storagePermissionDeniedFeedback,
                onDriverModeChanged = { mode -> requestDriverModeChange(mode) },
                pendingDriverModeConfirmation = pendingDriverModeConfirmation,
                onConfirmDriverModeChange = { confirmDriverModeChange() },
                onDismissDriverModeConfirmation = { pendingDriverModeConfirmation = null },
                pendingTurnipActivationSlot = pendingTurnipActivationSlot,
                onConfirmTurnipActivation = { confirmManagedTurnipDriverActivation() },
                onDismissTurnipActivation = { pendingTurnipActivationSlot = null },
                onInstallDriverBundle = { requestDriverBundleImport() },
                onActivateTurnipDriver = { slot -> activateManagedTurnipDriver(slot) },
                onRemoveTurnipDriver = { slot -> removeManagedTurnipDriver(slot) },
                onPageOpened = { page -> requestPageQueries(page) },
                onRequestQuery = { group -> requestQueryGroup(group) },
                queryTimingMs = queryTimingMs
            )
            if (turnipFileManagerState.visible) {
                TurnipFileManagerDialog(
                    state = turnipFileManagerState,
                    onDismiss = { closeTurnipFileManager() },
                    onNavigate = { path -> loadTurnipFileManagerDirectory(path) },
                    onUp = { navigateTurnipFileManagerUp() },
                    onToggleSelection = { path -> toggleTurnipFileManagerSelection(path) },
                    onViewMode = { mode -> turnipFileManagerState = turnipFileManagerState.copy(viewMode = mode) },
                    onDetails = { candidate -> turnipFileManagerState = turnipFileManagerState.copy(details = candidate) },
                    onDismissDetails = { turnipFileManagerState = turnipFileManagerState.copy(details = null) },
                    onImport = { importSelectedTurnipFiles() }
                )
            }
            }
        }
        if (directUpdatesEnabled) {
            activityScope.launch { checkForApplicationUpdate(showProgress = false) }
        } else {
            showDirectUpdatesDisabledIntroIfFirstInstall()
        }
    }

    private fun showDirectUpdatesDisabledIntroIfFirstInstall() {
        if (prefs.getBoolean("direct_updates_intro_seen", false)) return
        val packageInfo = runCatching { packageManager.getPackageInfo(packageName, 0) }.getOrNull() ?: return
        prefs.edit().putBoolean("direct_updates_intro_seen", true).apply()
        if (packageInfo.firstInstallTime != packageInfo.lastUpdateTime) return
        updateStatus = UpdateStatus.DirectUpdatesDisabledIntro
        activityScope.launch {
            kotlinx.coroutines.delay(7_000L)
            if (updateStatus is UpdateStatus.DirectUpdatesDisabledIntro) updateStatus = UpdateStatus.Hidden
        }
    }

    private fun requestDirectUpdatesChanged(enabled: Boolean) {
        if (enabled) {
            directUpdatesConsentVisible = true
        } else {
            directUpdatesEnabled = false
            directUpdatesConsentVisible = false
            prefs.edit().putBoolean("direct_updates_enabled", false).apply()
            updateCheckJob?.cancel()
            updateStatusHideJob?.cancel()
            updateDownloadJob?.cancel()
            activeUpdateCheckCall?.cancel()
            activeUpdateDownloadCall?.cancel()
            updateCheckJob = null
            updateStatusHideJob = null
            updateDownloadJob = null
            updateDownloadPaused = false
            updateDownloadCancelRequested = false
            updateCancelConfirmationVisible = false
            updateTransferState?.apk?.let { runCatching { it.delete() } }
            updateTransferState = null
            updateCheckInFlight = false
            pendingUpdateApk?.let { runCatching { it.delete() } }
            pendingUpdateApk = null
            updateStatus = UpdateStatus.Hidden
            updateConfirmation = null
        }
    }

    private fun confirmDirectUpdatesConsent() {
        directUpdatesEnabled = true
        directUpdatesConsentVisible = false
        if (updateStatus is UpdateStatus.DirectUpdatesDisabledIntro) updateStatus = UpdateStatus.Hidden
        prefs.edit().putBoolean("direct_updates_enabled", true).apply()
    }

    private fun completeReportMutationReady(): Boolean {
        val report = latestReport ?: return false
        return isCompleteReportReady(report, collectionStatus) && !collectionInFlight && pendingCollectionTasks.isEmpty() && !collectionPending && !turnipManagerBusy && !driverImportInFlight
    }

    private fun requestDriverBundleImport() {
        if (!completeReportMutationReady()) {
            android.widget.Toast.makeText(this, "Wait for the complete Vulkan® collection pass before changing the driver package.", android.widget.Toast.LENGTH_LONG).show()
            return
        }
        if (turnipManagerBusy) {
            android.widget.Toast.makeText(this, "Driver manager is busy.", android.widget.Toast.LENGTH_SHORT).show()
            return
        }
        if (Build.VERSION.SDK_INT < 28 || !Build.SUPPORTED_ABIS.contains("arm64-v8a")) {
            android.widget.Toast.makeText(this, "Turnip requires Android 9+ and arm64-v8a.", android.widget.Toast.LENGTH_LONG).show()
            return
        }
        if (turnipSupport != TurnipSupport.SUPPORTED) {
            android.widget.Toast.makeText(this, "Turnip is available only when a Qualcomm Adreno Vulkan® device is detected.", android.widget.Toast.LENGTH_LONG).show()
            return
        }
        requestSharedStorageAccess(
            onGranted = { openTurnipFileManager() },
            onDenied = { }
        )
    }

    private fun requestSharedStorageAccess(onGranted: () -> Unit, onDenied: () -> Unit) {
        if (android.os.Environment.isExternalStorageManager()) {
            onGranted()
            return
        }
        if (awaitingAllFilesAccessReturn) return
        pendingSharedStorageGranted = onGranted
        pendingSharedStorageDenied = onDenied
        awaitingAllFilesAccessReturn = true
        val specific = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:$packageName"))
        val launched = runCatching { startActivity(specific); true }.getOrElse {
            runCatching { startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)); true }.getOrDefault(false)
        }
        if (!launched) {
            awaitingAllFilesAccessReturn = false
            pendingSharedStorageGranted = null
            pendingSharedStorageDenied = null
            showStoragePermissionDeniedFeedback()
            onDenied()
        }
    }

    private fun showStoragePermissionDeniedFeedback() {
        storagePermissionFeedbackGeneration += 1L
        val generation = storagePermissionFeedbackGeneration
        storagePermissionDeniedFeedback = true
        android.widget.Toast.makeText(this, "Permission denied", android.widget.Toast.LENGTH_SHORT).show()
        activityScope.launch {
            delay(3_000L)
            if (storagePermissionFeedbackGeneration == generation) storagePermissionDeniedFeedback = false
        }
    }

    private fun turnipListingStatus(listing: TurnipDirectoryListing): String? = when {
        listing.entryLimitReached && listing.zipLimitReached -> "This folder reached the 4096-entry scan limit and the 256-ZIP validation limit."
        listing.entryLimitReached -> "This folder reached the bounded 4096-entry scan limit."
        listing.zipLimitReached -> "This folder reached the bounded 256-ZIP validation limit."
        else -> null
    }

    private fun openTurnipFileManager() {
        if (!android.os.Environment.isExternalStorageManager() || turnipManagerBusy || driverImportInFlight) return
        turnipFileManagerScanJob?.cancel()
        turnipFileManagerScanGeneration += 1L
        val generation = turnipFileManagerScanGeneration
        val requestedRoot = android.os.Environment.getExternalStorageDirectory()
        turnipFileManagerState = TurnipFileManagerState(
            visible = true,
            rootPath = requestedRoot.absolutePath,
            directoryPath = requestedRoot.absolutePath,
            loading = true
        )
        turnipFileManagerScanJob = activityScope.launch {
            try {
                val setup = withContext(Dispatchers.IO) {
                    val root = requestedRoot.canonicalFile
                    if (!root.isDirectory || !root.canRead()) error("Shared storage is unavailable")
                    val managedDrivers = readManagedTurnipDrivers(this@MainActivity, prefs, driverMode)
                    val occupied = managedDrivers.size
                    val remaining = (TURNIP_MANAGER_MAX_DRIVERS - occupied).coerceAtLeast(0)
                    val importedSourceKeys = managedDrivers.mapNotNull { driver ->
                        val location = driver.info.zipLocation?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                        val name = driver.info.zipName?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                        turnipImportedSourceKey(location, name)
                    }.toSet()
                    val listing = if (remaining > 0) scanTurnipFileManagerDirectory(root, root) else TurnipDirectoryListing(emptyList(), emptyList(), false, false)
                    TurnipFileManagerSetup(root, remaining, listing, importedSourceKeys)
                }
                if (generation != turnipFileManagerScanGeneration) return@launch
                val latest = turnipFileManagerState
                if (!latest.visible) return@launch
                val root = setup.root
                val remaining = setup.remaining
                val listing = setup.listing
                if (remaining == 0) {
                    turnipFileManagerState = TurnipFileManagerState()
                    android.widget.Toast.makeText(this@MainActivity, "All 10 Turnip driver slots are occupied.", android.widget.Toast.LENGTH_LONG).show()
                } else {
                    turnipFileManagerState = latest.copy(
                        rootPath = root.path,
                        directoryPath = root.path,
                        folders = listing.folders,
                        candidates = listing.candidates,
                        importedSourceKeys = setup.importedSourceKeys,
                        maxSelectable = remaining,
                        loading = false,
                        status = turnipListingStatus(listing)
                    )
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                if (generation == turnipFileManagerScanGeneration && turnipFileManagerState.visible) {
                    turnipFileManagerState = turnipFileManagerState.copy(loading = false, status = error.message ?: "Shared storage is unavailable")
                }
            } finally {
                if (generation == turnipFileManagerScanGeneration) turnipFileManagerScanJob = null
            }
        }
    }

    private fun loadTurnipFileManagerDirectory(path: String) {
        val current = turnipFileManagerState
        if (!current.visible || current.loading || current.importing || !android.os.Environment.isExternalStorageManager()) return
        turnipFileManagerScanJob?.cancel()
        turnipFileManagerScanGeneration += 1L
        val generation = turnipFileManagerScanGeneration
        val root = File(current.rootPath)
        val target = File(path)
        turnipFileManagerState = current.copy(directoryPath = path, folders = emptyList(), candidates = emptyList(), loading = true, status = null, details = null)
        turnipFileManagerScanJob = activityScope.launch {
            try {
                val listing = withContext(Dispatchers.IO) { scanTurnipFileManagerDirectory(root, target) }
                if (generation != turnipFileManagerScanGeneration) return@launch
                val latest = turnipFileManagerState
                if (!latest.visible || latest.importing) return@launch
                turnipFileManagerState = latest.copy(
                    directoryPath = path,
                    folders = listing.folders,
                    candidates = listing.candidates,
                    selectedPaths = latest.selectedPaths,
                    loading = false,
                    status = turnipListingStatus(listing)
                )
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                if (generation == turnipFileManagerScanGeneration && turnipFileManagerState.visible && !turnipFileManagerState.importing) {
                    turnipFileManagerState = turnipFileManagerState.copy(loading = false, status = error.message ?: "Unable to read this folder")
                }
            } finally {
                if (generation == turnipFileManagerScanGeneration) turnipFileManagerScanJob = null
            }
        }
    }

    private fun navigateTurnipFileManagerUp() {
        val state = turnipFileManagerState
        if (!state.visible || state.loading || state.importing || state.directoryPath == state.rootPath) return
        val parentPath = File(state.directoryPath).parent ?: return
        val rootPrefix = state.rootPath.trimEnd(File.separatorChar) + File.separator
        if (parentPath == state.rootPath || parentPath.startsWith(rootPrefix)) loadTurnipFileManagerDirectory(parentPath)
    }

    private fun toggleTurnipFileManagerSelection(path: String) {
        val state = turnipFileManagerState
        val candidate = state.candidates.firstOrNull { it.path == path } ?: return
        if (!state.visible || state.loading || state.importing) return
        if (turnipImportedSourceKey(candidate.path, candidate.name) in state.importedSourceKeys) return
        val selected = state.selectedPaths.toMutableSet()
        if (path in selected) selected.remove(path) else {
            if (selected.size >= state.maxSelectable) {
                turnipFileManagerState = state.copy(status = "Only ${state.maxSelectable} remaining Turnip slot${if (state.maxSelectable == 1) " is" else "s are"} available.")
                return
            }
            selected.add(path)
        }
        turnipFileManagerState = state.copy(selectedPaths = selected, status = null)
    }

    private fun closeTurnipFileManager() {
        if (turnipFileManagerState.importing) return
        turnipFileManagerScanJob?.cancel()
        turnipFileManagerScanJob = null
        turnipFileManagerScanGeneration += 1L
        turnipFileManagerState = TurnipFileManagerState()
    }

    private fun importSelectedTurnipFiles() {
        val state = turnipFileManagerState
        if (!state.visible || state.loading || state.importing || state.selectedPaths.isEmpty() || driverImportInFlight || turnipManagerBusy) return
        if (!completeReportMutationReady()) {
            turnipFileManagerState = state.copy(status = "Driver import is locked until the complete Vulkan® collection pass finishes.")
            return
        }
        val selected = state.selectedPaths
            .filterNot { path -> turnipImportedSourceKey(path, File(path).name) in state.importedSourceKeys }
            .take(state.maxSelectable)
        if (selected.isEmpty()) {
            turnipFileManagerState = state.copy(selectedPaths = emptySet(), status = "Selected Turnip packages are already imported or unavailable.")
            return
        }
        driverImportInFlight = true
        turnipManagerBusy = true
        turnipFileManagerState = state.copy(importing = true, status = "Validating selected Turnip packages…")
        activityScope.launch {
            var imported = 0
            var failure: Throwable? = null
            for (path in selected) {
                val candidate = withContext(Dispatchers.IO) { inspectTurnipArchive(File(path)) }
                if (candidate == null) {
                    failure = IllegalArgumentException("A selected Turnip ZIP changed or no longer passes validation: ${File(path).name}")
                    break
                }
                val sourceInfo = TurnipSourceInfo(candidate.name, candidate.path, candidate.sizeBytes, candidate.modifiedAtMillis)
                val importFailure = try {
                    withContext(Dispatchers.IO) { installDriverBundleIo(sourceInfo) { FileInputStream(candidate.path) } }
                    null
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (error: Throwable) {
                    error
                }
                if (importFailure != null) {
                    failure = importFailure
                    break
                }
                imported += 1
                turnipFileManagerState = turnipFileManagerState.copy(status = "Imported $imported / ${selected.size} validated Turnip package${if (selected.size == 1) "" else "s"}…")
            }
            driverImportInFlight = false
            turnipManagerBusy = false
            if (imported > 0) turnipManagerRevision += 1
            if (failure == null) {
                turnipFileManagerState = TurnipFileManagerState()
                android.widget.Toast.makeText(this@MainActivity, "Imported $imported Turnip package${if (imported == 1) "" else "s"}.", android.widget.Toast.LENGTH_LONG).show()
            } else {
                val remainingSelected = selected.drop(imported).toSet()
                val newlyImportedKeys = selected.take(imported).map { path -> turnipImportedSourceKey(path, File(path).name) }.toSet()
                turnipFileManagerState = turnipFileManagerState.copy(
                    importing = false,
                    importedSourceKeys = turnipFileManagerState.importedSourceKeys + newlyImportedKeys,
                    selectedPaths = remainingSelected,
                    maxSelectable = (turnipFileManagerState.maxSelectable - imported).coerceAtLeast(0),
                    status = "${if (imported > 0) "Imported $imported package${if (imported == 1) "" else "s"}. " else ""}${failure?.message ?: "Import failed"}"
                )
            }
        }
    }

    private suspend fun installDriverBundleIo(sourceInfo: TurnipSourceInfo, openInput: () -> InputStream?): Int {
        migrateLegacyTurnipBundleIfNeeded(filesDir, prefs)
        val slot = (1..TURNIP_MANAGER_MAX_DRIVERS).firstOrNull { !turnipSlotRoot(filesDir, it).exists() }
            ?: throw IllegalStateException("The 10-driver Turnip manager limit has been reached")
        val managerRoot = turnipManagerRoot(filesDir)
        if (!managerRoot.mkdirs() && !managerRoot.isDirectory) throw IllegalStateException("Unable to create the private driver manager directory")
        val finalSlotRoot = turnipSlotRoot(filesDir, slot)
        val tempSlotRoot = File(managerRoot, ".import_${java.util.UUID.randomUUID()}")
        val tempBundleRoot = File(tempSlotRoot, TURNIP_MANAGER_BUNDLE_NAME)
        val importContext = currentCoroutineContext()
        try {
            if (!tempSlotRoot.mkdir() || !tempBundleRoot.mkdir()) throw IllegalStateException("Unable to create the private driver import directory")
            var entryCount = 0
            var totalBytes = 0L
            val canonicalTempDir = tempBundleRoot.canonicalFile
            val seenArchivePaths = HashSet<String>()
            val maxEntries = 2048
            val maxTotalBytes = 64L * 1024L * 1024L
            val maxFileBytes = 32L * 1024L * 1024L
            openInput()?.use { input ->
                ZipInputStream(BoundedDriverArchiveInputStream(input, TURNIP_ARCHIVE_INPUT_MAX_BYTES)).use { zip ->
                    var entry = zip.nextEntry
                    while (entry != null) {
                        importContext.ensureActive()
                        if (++entryCount > maxEntries) throw SecurityException("Driver bundle contains too many entries")
                        if (entry.name.isBlank() || entry.name.length > 1024) throw SecurityException("Driver bundle contains an invalid path")
                        val safe = File(tempBundleRoot, entry.name).canonicalFile
                        if (!safe.path.startsWith(canonicalTempDir.path + File.separator) && safe != canonicalTempDir) throw SecurityException("Unsafe driver bundle path")
                        if (!seenArchivePaths.add(safe.path)) throw SecurityException("Driver bundle contains duplicate archive paths")
                        if (entry.isDirectory) {
                            if (!safe.mkdirs() && !safe.isDirectory) throw IllegalStateException("Unable to create a driver bundle directory")
                        } else {
                            val parent = safe.parentFile ?: throw SecurityException("Driver bundle entry has no valid parent")
                            if (!parent.mkdirs() && !parent.isDirectory) throw IllegalStateException("Unable to create a driver bundle directory")
                            var fileBytes = 0L
                            FileOutputStream(safe).use { output ->
                                if (safe.name.endsWith(".so", true) && !safe.setReadOnly()) throw SecurityException("Unable to secure a native driver library before extraction")
                                val buffer = ByteArray(32 * 1024)
                                while (true) {
                                    importContext.ensureActive()
                                    val read = zip.read(buffer)
                                    if (read <= 0) break
                                    fileBytes += read
                                    totalBytes += read
                                    if (fileBytes > maxFileBytes || totalBytes > maxTotalBytes) throw SecurityException("Driver bundle is too large")
                                    output.write(buffer, 0, read)
                                }
                                if (safe.name.endsWith(".so", true) || safe.name.equals("meta.json", true)) output.fd.sync()
                            }
                        }
                        zip.closeEntry()
                        entry = zip.nextEntry
                    }
                }
            } ?: throw IllegalStateException("Unable to read bundle")

            importContext.ensureActive()
            val bundleScan = scanTurnipBundle(tempBundleRoot, maxEntries) ?: throw SecurityException("The extracted driver bundle could not be validated within the entry safety limit")
            val metadataFiles = bundleScan.files.filter { it.name.equals("meta.json", true) }
            if (metadataFiles.size != 1) throw IllegalArgumentException("The AdrenoTools package must contain exactly one meta.json")
            val metadataFile = metadataFiles.single()
            val metadata = runCatching { JSONObject(readFileTextLimited(metadataFile, 1024 * 1024)) }.getOrElse { throw IllegalArgumentException("The AdrenoTools meta.json is invalid") }
            val schemaVersion = metadata.optInt("schemaVersion", -1)
            if (schemaVersion != 1) throw IllegalArgumentException("Unsupported AdrenoTools package schema: ${if (schemaVersion < 0) "missing" else schemaVersion}")
            val declared = metadata.optString("libraryName").trim()
            if (declared.isBlank() || declared.contains('/') || declared.contains('\\') || !declared.endsWith(".so", true)) throw IllegalArgumentException("AdrenoTools meta.json does not declare a valid Vulkan library name")
            val libraries = bundleScan.files.filter { it.name == declared }
            if (libraries.size != 1) throw IllegalArgumentException("Driver metadata must resolve to exactly one Vulkan library: $declared")
            val library = libraries.single()
            if (!declared.contains("vulkan", true)) throw IllegalArgumentException("The selected AdrenoTools package does not declare a Vulkan driver library")
            if (!library.canRead() || library.length() == 0L) throw IllegalArgumentException("The Vulkan .so driver library is empty or unreadable")
            if (!ensureTurnipNativeLibrariesReadOnly(bundleScan)) throw SecurityException("The native driver libraries could not be secured as read-only")
            val importedAt = System.currentTimeMillis()
            if (!writeTurnipSourceInfo(File(tempSlotRoot, TURNIP_MANAGER_SOURCE_NAME), sourceInfo, importedAt)) throw IllegalStateException("Unable to persist private source ZIP provenance")
            importContext.ensureActive()
            if (finalSlotRoot.exists()) throw IllegalStateException("Turnip driver slot is already occupied")
            if (!tempSlotRoot.renameTo(finalSlotRoot)) throw IllegalStateException("Unable to install the driver bundle atomically")
            Log.i("VulkanScope", "Imported validated Turnip driver slot=%02d library=%s".format(java.util.Locale.ROOT, slot, declared))
            return slot
        } catch (error: Throwable) {
            tempSlotRoot.deleteRecursively()
            throw error
        }
    }

    private fun activateManagedTurnipDriver(slot: Int) {
        if (slot !in 1..TURNIP_MANAGER_MAX_DRIVERS || driverImportInFlight || turnipManagerBusy) return
        if (!completeReportMutationReady()) {
            android.widget.Toast.makeText(this, "Wait for the complete Vulkan® collection pass before activating a Turnip driver.", android.widget.Toast.LENGTH_LONG).show()
            return
        }
        pendingTurnipActivationSlot = slot
    }

    private fun confirmManagedTurnipDriverActivation() {
        val slot = pendingTurnipActivationSlot ?: return
        pendingTurnipActivationSlot = null
        if (slot !in 1..TURNIP_MANAGER_MAX_DRIVERS || driverImportInFlight || turnipManagerBusy || !completeReportMutationReady()) return
        turnipManagerBusy = true
        activityScope.launch {
            val valid = withContext(Dispatchers.IO) { probeMutex.withLock { resolveTurnipLibrary(turnipSlotBundleRoot(filesDir, slot)) != null } }
            if (!valid) {
                turnipManagerBusy = false
                turnipManagerRevision += 1
                android.widget.Toast.makeText(this@MainActivity, "The selected Turnip driver slot is invalid or incomplete.", android.widget.Toast.LENGTH_LONG).show()
                return@launch
            }
            val committed = withContext(Dispatchers.IO) { prefs.edit().putInt(TURNIP_ACTIVE_SLOT_PREF, slot).putString("turnip_bundle", turnipSlotBundleRoot(filesDir, slot).absolutePath).commit() }
            turnipManagerBusy = false
            if (!committed) {
                android.widget.Toast.makeText(this@MainActivity, "Unable to persist the selected Turnip driver.", android.widget.Toast.LENGTH_LONG).show()
                return@launch
            }
            turnipManagerRevision += 1
            applyDriverModeChange(DriverMode.TURNIP, true)
        }
    }

    private fun removeManagedTurnipDriver(slot: Int) {
        if (slot !in 1..TURNIP_MANAGER_MAX_DRIVERS || driverImportInFlight || turnipManagerBusy) return
        if (!completeReportMutationReady()) {
            android.widget.Toast.makeText(this, "Wait for the complete Vulkan® collection pass before removing a Turnip driver.", android.widget.Toast.LENGTH_LONG).show()
            return
        }
        turnipManagerBusy = true
        activityScope.launch {
            val wasSelected = prefs.getInt(TURNIP_ACTIVE_SLOT_PREF, 0) == slot
            val switchToSystem = wasSelected && driverMode == DriverMode.TURNIP
            if (wasSelected) {
                val committed = withContext(Dispatchers.IO) { prefs.edit().remove(TURNIP_ACTIVE_SLOT_PREF).remove("turnip_bundle").commit() }
                if (!committed) {
                    turnipManagerBusy = false
                    android.widget.Toast.makeText(this@MainActivity, "Unable to clear the active Turnip driver selection.", android.widget.Toast.LENGTH_LONG).show()
                    return@launch
                }
            }
            val removed = withContext(Dispatchers.IO) { probeMutex.withLock { turnipSlotRoot(filesDir, slot).deleteRecursively() } }
            turnipManagerBusy = false
            turnipManagerRevision += 1
            if (switchToSystem) applyDriverModeChange(DriverMode.SYSTEM, true)
            android.widget.Toast.makeText(this@MainActivity, if (removed) "Turnip driver removed from slot %02d.".format(java.util.Locale.ROOT, slot) else "Unable to remove Turnip driver slot %02d.".format(java.util.Locale.ROOT, slot), android.widget.Toast.LENGTH_LONG).show()
        }
    }

    private fun findTurnipBundle(mode: DriverMode = driverMode): String? {
        if (mode != DriverMode.TURNIP) return null
        return activeTurnipBundleRoot(filesDir, prefs)?.absolutePath
    }

    private fun resolveInstalledTurnipIcd(): String? = activeTurnipBundleRoot(filesDir, prefs)?.let(::resolveTurnipLibrary)?.absolutePath

    private fun findTurnipIcd(mode: DriverMode = driverMode): String? = if (mode == DriverMode.TURNIP) resolveInstalledTurnipIcd() else null

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

    private fun takePendingSurfaceRefresh(): Surface? = synchronized(surfaceLock) {
        val surface = currentSurface?.takeIf { it.isValid }
        if (surfaceRefreshPending && surface != null) {
            surfaceRefreshPending = false
            surface
        } else {
            null
        }
    }

    private fun settleCollectionState() {
        if (collectionInFlight || pendingCollectionTasks.isNotEmpty()) return
        if (collectionPending && !isFinishing && !isDestroyed) {
            collectionPending = false
            synchronized(surfaceLock) { surfaceRefreshPending = false }
            requestReportCollection()
            return
        }
        val surface = takePendingSurfaceRefresh()
        if (surface != null && latestReport != null && !isFinishing && !isDestroyed) {
            requestSurfaceRefresh(surface)
        } else {
            finishCollectionStatusSoon()
        }
    }

    private fun completeCollectionTask(taskId: String) {
        pendingCollectionTasks.remove(taskId)
        settleCollectionState()
    }

    private fun requestReportCollection() {
        if (isFinishing || isDestroyed) return
        if (driverImportInFlight) {
            collectionPending = true
            return
        }
        if (collectionInFlight || pendingCollectionTasks.isNotEmpty()) {
            collectionPending = true
            return
        }
        collectionGeneration += 1L
        val generation = collectionGeneration
        val modeSnapshot = driverMode
        requestedQueryGroups.clear()
        queryTimingMs = emptyMap()
        pendingCollectionTasks.clear()
        pendingCollectionTasks.add("$generation:base")
        pendingCollectionTasks.add("$generation:enrichment")
        backgroundQueryGroups.forEach { pendingCollectionTasks.add("$generation:group:$it") }
        collectionInFlight = true
        reportLoading = true
        collectionStatus = CollectionStatus.COLLECTING
        activityScope.launch {
            try {
                val base = runCatching { collectReport(modeSnapshot) }.getOrElse { e ->
                    Log.e("VulkanScope", "Vulkan base orchestration failed", e)
                    VulkanReport("Unknown", emptyList(), emptyList(), emptyList(), "Vulkan base report unavailable: ${e.message ?: "probe failed"}")
                }
                if (generation != collectionGeneration || modeSnapshot != driverMode) {
                    pendingCollectionTasks.removeAll { it.startsWith("$generation:") }
                    return@launch
                }
                latestReport = base
                updateTurnipSupport(base)
                reportLoading = false
                completeCollectionTask("$generation:base")
                val enriched = runCatching { enrichReport(base, modeSnapshot) }.getOrElse { e ->
                    Log.e("VulkanScope", "Vulkan advanced enrichment failed", e)
                    markSurfaceProbeState(markMetadataProbeUnavailable(base, e.message ?: "Unexpected enrichment failure."), "unavailable", e.message ?: "Unexpected enrichment failure.")
                }
                if (generation != collectionGeneration || modeSnapshot != driverMode) {
                    pendingCollectionTasks.removeAll { it.startsWith("$generation:") }
                    return@launch
                }
                latestReport = enriched
                completeCollectionTask("$generation:enrichment")
                startBackgroundInformationCollection(generation, modeSnapshot)
            } finally {
                collectionInFlight = false
                settleCollectionState()
            }
        }
    }

    private fun requestSurfaceRefresh(surface: Surface) {
        if (isFinishing || isDestroyed) return
        if (driverImportInFlight) {
            collectionPending = true
            return
        }
        val surfaceToken = synchronized(surfaceLock) {
            if (currentSurface !== surface || !surface.isValid) return
            surfaceGeneration
        }
        if (collectionInFlight || pendingCollectionTasks.isNotEmpty()) {
            synchronized(surfaceLock) {
                if (currentSurface === surface && surfaceGeneration == surfaceToken) surfaceRefreshPending = true
            }
            return
        }
        if (latestReport == null) return
        synchronized(surfaceLock) {
            if (currentSurface === surface && surfaceGeneration == surfaceToken) surfaceRefreshPending = false
        }
        val generation = collectionGeneration
        val modeSnapshot = driverMode
        val taskId = "surface:${System.identityHashCode(surface)}:$surfaceToken:${System.nanoTime()}"
        beginCollectionTask(taskId)
        activityScope.launch {
            try {
                val base = latestReport ?: return@launch
                runCatching {
                    val rawSurface = runSurfaceProbe(surface, modeSnapshot)
                    val stillCurrent = synchronized(surfaceLock) {
                        currentSurface === surface && surfaceGeneration == surfaceToken && surface.isValid
                    }
                    if (generation == collectionGeneration && modeSnapshot == driverMode && !collectionInFlight && stillCurrent) {
                        latestReport = mergeSurfaceProbeReport(latestReport ?: base, rawSurface)
                    } else if (!stillCurrent) {
                        synchronized(surfaceLock) {
                            if (currentSurface?.isValid == true) surfaceRefreshPending = true
                        }
                    }
                }.onFailure { e ->
                    Log.e("VulkanScope", "Surface refresh failed", e)
                    val stillCurrent = synchronized(surfaceLock) {
                        currentSurface === surface && surfaceGeneration == surfaceToken && surface.isValid
                    }
                    if (generation == collectionGeneration && modeSnapshot == driverMode && !collectionInFlight && stillCurrent) {
                        latestReport = markSurfaceProbeState(latestReport ?: base, "unavailable", e.message ?: "Surface refresh failed before a complete result was produced.")
                    } else if (!stillCurrent) {
                        synchronized(surfaceLock) {
                            if (currentSurface?.isValid == true) surfaceRefreshPending = true
                        }
                    }
                }
            } finally {
                completeCollectionTask(taskId)
            }
        }
    }

    private suspend fun startBackgroundInformationCollection(generation: Long = collectionGeneration, modeSnapshot: DriverMode = driverMode) {
        val report = latestReport
        if (report == null || report.devices.isEmpty()) {
            backgroundQueryGroups.forEach { completeCollectionTask("$generation:group:$it") }
            return
        }

        val enumeratedDeviceExtensions = report.devices
            .flatMap { device -> device.extensions.map { it.name } }
            .toSet()
        val exhaustiveExtensionGroups = VALIDATED_PHYSICAL_DEVICE_QUERY_EXTENSIONS
            .asSequence()
            .filter { it in enumeratedDeviceExtensions }
            .sorted()
            .map { "ext::$it" }
            .toList()
        val allGroups = backgroundQueryGroups.toList() + exhaustiveExtensionGroups
        exhaustiveExtensionGroups.forEach { pendingCollectionTasks.add("$generation:group:$it") }
        val newGroups = allGroups.filter { requestedQueryGroups.add(it) }
        allGroups.filterNot { newGroups.contains(it) }.forEach { completeCollectionTask("$generation:group:$it") }
        val deadlineNanos = System.nanoTime() + BACKGROUND_COLLECTION_BUDGET_MS * 1_000_000L

        fun unavailableRaw(group: String, reason: String): String = JSONObject()
            .put("status", "unavailable")
            .put("group", group)
            .put("reason", reason)
            .put("devices", JSONArray())
            .toString()

        suspend fun publishUnavailableGroups(groups: List<String>, reason: String) {
            if (groups.isEmpty()) return
            withContext(Dispatchers.Main.immediate) {
                if (generation != collectionGeneration || modeSnapshot != driverMode) return@withContext
                var currentReport = latestReport ?: report
                for (group in groups) {
                    val raw = unavailableRaw(group, reason)
                    val extensionName = if (group.startsWith("ext::")) group.removePrefix("ext::") else ISOLATED_EXTENSION_GROUPS[group]
                    currentReport = if (extensionName != null) {
                        mergeExtensionGroupReport(currentReport, raw, group, extensionName)
                    } else {
                        val label = ISOLATED_ADVANCED_GROUPS[group] ?: ISOLATED_CORE_GROUPS[group] ?: group
                        mergeAdvancedQueryReport(currentReport, raw, group, label)
                    }
                }
                latestReport = currentReport
            }
            groups.forEach { completeCollectionTask("$generation:group:$it") }
        }

        var groupIndex = 0
        for (group in newGroups) {
            val remainingNanos = deadlineNanos - System.nanoTime()
            if (remainingNanos <= 0L) {
                publishUnavailableGroups(
                    newGroups.drop(groupIndex),
                    "Unavailable: the bounded background Vulkan detail-collection budget of ${BACKGROUND_COLLECTION_BUDGET_MS / 1000L} seconds was exhausted. The base report remains complete; this optional detail can be retried from its page."
                )
                break
            }
            val taskId = "$generation:group:$group"
            try {
                if (generation != collectionGeneration || modeSnapshot != driverMode) {
                    pendingCollectionTasks.removeAll { it.startsWith("$generation:") }
                    return
                }
                val current = latestReport ?: break
                val extensionName = if (group.startsWith("ext::")) group.removePrefix("ext::") else ISOLATED_EXTENSION_GROUPS[group]
                val remainingBudgetMs = (remainingNanos / 1_000_000L).coerceAtLeast(1L)
                val timingStartNanos = System.nanoTime()
                val raw = runCatching {
                    withTimeout(remainingBudgetMs) { runIsolatedProbe(group, modeSnapshot) }
                }.getOrElse { e ->
                    Log.e("VulkanScope", "Vulkan background query failed: $group", e)
                    unavailableRaw(group, e.message ?: "The dedicated background query failed.")
                }.ifBlank {
                    JSONObject().put("status", "unavailable").put("group", group).put("reason", "The dedicated background query returned no data.").put("devices", JSONArray()).toString()
                }
                val elapsedMs = ((System.nanoTime() - timingStartNanos) / 1_000_000L).coerceAtLeast(0L)
                withContext(Dispatchers.Main.immediate) {
                    if (generation != collectionGeneration || modeSnapshot != driverMode) return@withContext
                    queryTimingMs = queryTimingMs + (group to elapsedMs)
                    val currentReport = latestReport ?: current
                    latestReport = if (extensionName != null) {
                        mergeExtensionGroupReport(currentReport, raw, group, extensionName)
                    } else {
                        val label = ISOLATED_ADVANCED_GROUPS[group] ?: ISOLATED_CORE_GROUPS[group] ?: group
                        mergeAdvancedQueryReport(currentReport, raw, group, label)
                    }
                }
            } finally {
                withContext(Dispatchers.Main.immediate) {
                    completeCollectionTask(taskId)
                }
            }
            groupIndex += 1
        }
    }

    private fun finishCollectionStatusSoon(delayMillis: Long = 2000L) {
        if (collectionInFlight || pendingCollectionTasks.isNotEmpty()) return
        collectionCompletionJob?.cancel()
        collectionStatus = if (collectionFinishedSuccessfully(latestReport)) CollectionStatus.COMPLETED else CollectionStatus.FAILED
        if (collectionStatus == CollectionStatus.FAILED) return
        collectionCompletionJob = activityScope.launch {
            delay(delayMillis)
            if (!collectionInFlight && pendingCollectionTasks.isEmpty()) {
                collectionStatus = CollectionStatus.IDLE
            }
        }
    }

    private fun checkForApplicationUpdate(showProgress: Boolean) {
        if (!validatedDefaultNetwork()) {
            if (showProgress) updateStatus = UpdateStatus.Failed("No validated internet connection. Internet-dependent update checks are unavailable until connectivity returns.")
            return
        }
        if (!directUpdatesEnabled) {
            if (showProgress) updateStatus = UpdateStatus.Failed("Direct GitHub updates are disabled. Use Obtainium for external update management or enable them in Settings.")
            return
        }
        if (updateCheckInFlight || updateDownloadJob != null || updateTransferState != null) return
        updateStatusHideJob?.cancel()
        updateStatusHideJob = null
        updateCheckInFlight = true
        updateCheckJob = activityScope.launch {
            if (showProgress) updateStatus = UpdateStatus.Checking
            val result = try {
                withContext(Dispatchers.IO) { fetchLatestCompatibleUpdateResult() }
            } finally {
                updateCheckInFlight = false
            }
            if (!directUpdatesEnabled) {
                updateStatus = UpdateStatus.Hidden
                updateConfirmation = null
                return@launch
            }
            updateStatus = when (result) {
                is UpdateCheckResult.Available -> UpdateStatus.Available(result.update)
                UpdateCheckResult.UpToDate -> if (showProgress) UpdateStatus.UpToDate else UpdateStatus.Hidden
                is UpdateCheckResult.Failed -> if (showProgress) UpdateStatus.Failed(result.message) else UpdateStatus.Hidden
            }
            if (showProgress && result is UpdateCheckResult.Available) {
                updateConfirmation = result.update
            }
            if (updateStatus !is UpdateStatus.Hidden && updateStatus !is UpdateStatus.Downloading) {
                val displayedStatus = updateStatus
                val displayDurationMillis = if (displayedStatus is UpdateStatus.UpToDate) 8_000L else 10_000L
                updateStatusHideJob = activityScope.launch {
                    delay(displayDurationMillis)
                    if (!updateCheckInFlight && updateStatus == displayedStatus && updateStatus !is UpdateStatus.Downloading) {
                        updateStatus = UpdateStatus.Hidden
                    }
                }
            }
        }
    }

    private sealed interface UpdateCheckResult {
        data class Available(val update: AppUpdate) : UpdateCheckResult
        data object UpToDate : UpdateCheckResult
        data class Failed(val message: String) : UpdateCheckResult
    }

    private fun fetchLatestCompatibleUpdateResult(): UpdateCheckResult {
        return try {
            val update = fetchLatestCompatibleUpdate()
            if (update != null) UpdateCheckResult.Available(update) else UpdateCheckResult.UpToDate
        } catch (error: Exception) {
            UpdateCheckResult.Failed(error.message ?: "Update check failed.")
        }
    }

    private fun fetchLatestCompatibleUpdate(): AppUpdate? {
        val request = Request.Builder()
            .url("https://api.github.com/repos/EFIShell0/VulkanScope/releases?per_page=20")
            .header("Accept", "application/vnd.github+json")
            .header("X-GitHub-Api-Version", "2022-11-28")
            .header("User-Agent", "VulkanScope/${installedVersionName()}")
            .get()
            .build()
        val call = ipv6PreferredHttpClient.newCall(request)
        activeUpdateCheckCall = call
        return try {
            call.execute().use { response ->
            if (!response.isSuccessful) error("Update check failed (HTTP ${response.code}).")
            val releases = JSONArray(readResponseTextLimited(response.body, 2 * 1024 * 1024))
            val current = installedVersionName()
            val candidates = (0 until releases.length())
                .mapNotNull { releases.optJSONObject(it) }
                .filter { !it.optBoolean("draft", false) }
                .mapNotNull { release ->
                    val version = release.optString("tag_name").trim().removePrefix("v")
                    if (version.isBlank() || !version.matches(Regex("\\d+(?:\\.\\d+){1,3}(?:-[0-9A-Za-z.-]+)?(?:\\+[0-9A-Za-z.-]+)?"))) null else release to version
                }
                .sortedWith { a, b -> -compareVersions(a.second, b.second) }
            val candidate = candidates.firstOrNull { isNewerVersion(it.second, current) } ?: return null
            val json = candidate.first
            val latest = candidate.second
            val assets = json.optJSONArray("assets") ?: error("A newer VulkanScope release exists, but its asset list is unavailable.")
            val apkAssets = (0 until assets.length()).mapNotNull { assets.optJSONObject(it) }.filter { it.optString("name").endsWith(".apk", true) }
            val abi = detectInstalledAbi(this)
            val abiTokens = when (abi) {
                "arm64-v8a" -> listOf("arm64-v8a", "arm64_v8a", "arm64")
                "armeabi-v7a" -> listOf("armeabi-v7a", "armeabi_v7a", "armv7")
                "x86_64" -> listOf("x86_64", "x86-64")
                else -> listOf(abi.lowercase())
            }
            val exact = apkAssets.firstOrNull { asset -> abiTokens.any { asset.optString("name").lowercase().contains(it) } }
            val universal = apkAssets.firstOrNull { it.optString("name").lowercase().contains("universal") }
            val selected = exact ?: universal ?: error("A newer VulkanScope release exists, but it has no APK compatible with the installed ABI ($abi).")
            val url = selected.optString("browser_download_url")
            val parsedUrl = url.toHttpUrlOrNull()
            if (parsedUrl == null || parsedUrl.scheme != "https" || parsedUrl.host != "github.com" || parsedUrl.username.isNotEmpty() || parsedUrl.password.isNotEmpty() || parsedUrl.query != null || parsedUrl.fragment != null || !parsedUrl.encodedPath.startsWith("/EFIShell0/VulkanScope/releases/download/")) error("The release APK URL is not an official VulkanScope GitHub release asset.")
            AppUpdate(
                version = latest,
                assetName = selected.optString("name"),
                downloadUrl = url,
                releaseNotes = json.optString("body").trim().ifBlank { "No release notes were provided for this GitHub release." },
                installedAbi = abi,
                downloadAbi = if (exact != null) abi else "universal",
                installedVersion = current,
                installedVersionCode = installedVersionCode()
            )
            }
        } finally {
            if (activeUpdateCheckCall === call) activeUpdateCheckCall = null
        }
    }

    private fun installedVersionName(): String = runCatching {
        packageManager.getPackageInfo(packageName, 0).versionName ?: "0.0.0"
    }.getOrDefault("0.0.0")

    private fun installedVersionCode(): Long = runCatching {
        val info = packageManager.getPackageInfo(packageName, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) info.longVersionCode else info.versionCode.toLong()
    }.getOrDefault(0L)

    private fun compareVersions(first: String, second: String): Int {
        fun parse(value: String): Pair<List<Int>, List<String>?> {
            val withoutBuild = value.substringBefore('+')
            val core = withoutBuild.substringBefore('-').split('.').map { it.toIntOrNull() ?: 0 }
            val suffix = withoutBuild.substringAfter('-', "").takeIf { it.isNotEmpty() }?.split('.')
            return core to suffix
        }
        val (aCore, aPre) = parse(first)
        val (bCore, bPre) = parse(second)
        repeat(maxOf(aCore.size, bCore.size)) { index ->
            val left = aCore.getOrElse(index) { 0 }
            val right = bCore.getOrElse(index) { 0 }
            if (left != right) return left.compareTo(right)
        }
        if (aPre == null && bPre != null) return 1
        if (aPre != null && bPre == null) return -1
        if (aPre == null || bPre == null) return 0
        repeat(maxOf(aPre.size, bPre.size)) { index ->
            val left = aPre.getOrNull(index) ?: return -1
            val right = bPre.getOrNull(index) ?: return 1
            val leftNumber = left.toIntOrNull()
            val rightNumber = right.toIntOrNull()
            val result = when {
                leftNumber != null && rightNumber != null -> leftNumber.compareTo(rightNumber)
                leftNumber != null -> -1
                rightNumber != null -> 1
                else -> left.compareTo(right)
            }
            if (result != 0) return result
        }
        return 0
    }

    private fun isNewerVersion(candidate: String, current: String): Boolean = compareVersions(candidate, current) > 0

    private fun startUpdateDownload(update: AppUpdate) {
        if (!validatedDefaultNetwork()) {
            updateStatus = UpdateStatus.Failed("No validated internet connection. Update download is unavailable until connectivity returns.")
            return
        }
        if (!directUpdatesEnabled || updateDownloadJob != null) return
        updateStatusHideJob?.cancel()
        updateStatusHideJob = null
        updateStatus = UpdateStatus.Hidden
        updateDownloadPaused = false
        updateDownloadCancelRequested = false
        updateCancelConfirmationVisible = false
        updateTransferState = UpdateTransferState(
            update = update,
            phase = UpdateTransferPhase.CONNECTING,
            connectionStatus = "Connecting",
            log = listOf("Preparing ${update.assetName}", "Connecting to the official VulkanScope release asset…")
        )
        updateDownloadJob = activityScope.launch {
            try {
                val apk = downloadUpdateApk(update)
                if (!directUpdatesEnabled) {
                    runCatching { apk.delete() }
                    updateTransferState = null
                } else {
                    updateTransferState = updateTransferState?.copy(
                        phase = UpdateTransferPhase.COMPLETED,
                        bytesPerSecond = 0L,
                        connectionStatus = "Completed",
                        apk = apk,
                        errorMessage = null,
                        log = appendBoundedUpdateLog(updateTransferState?.log.orEmpty(), "APK validation completed successfully. Update downloaded and ready to install.")
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                if (updateDownloadCancelRequested) {
                    updateTransferState = updateTransferState?.copy(
                        phase = UpdateTransferPhase.CANCELED,
                        bytesPerSecond = 0L,
                        connectionStatus = "Canceled",
                        apk = null,
                        errorMessage = null,
                        log = appendBoundedUpdateLog(updateTransferState?.log.orEmpty(), "Download canceled by user.")
                    )
                } else if (directUpdatesEnabled) {
                    val message = error.message ?: "Update download failed."
                    updateTransferState = updateTransferState?.copy(
                        phase = UpdateTransferPhase.FAILED,
                        bytesPerSecond = 0L,
                        connectionStatus = "Error",
                        apk = null,
                        errorMessage = message,
                        log = appendBoundedUpdateLog(updateTransferState?.log.orEmpty(), "ERROR: $message")
                    )
                } else {
                    updateTransferState = null
                }
            } finally {
                updateDownloadPaused = false
                updateDownloadCancelRequested = false
                updateCancelConfirmationVisible = false
                updateDownloadJob = null
            }
        }
    }

    private fun pauseUpdateDownload() {
        val state = updateTransferState ?: return
        if (state.phase != UpdateTransferPhase.CONNECTING && state.phase != UpdateTransferPhase.DOWNLOADING) return
        updateDownloadPaused = true
        updateTransferState = state.copy(
            phase = UpdateTransferPhase.PAUSED,
            bytesPerSecond = 0L,
            connectionStatus = "Paused",
            log = appendBoundedUpdateLog(state.log, "Download paused.")
        )
    }

    private fun resumeUpdateDownload() {
        val state = updateTransferState ?: return
        if (state.phase != UpdateTransferPhase.PAUSED || updateDownloadCancelRequested) return
        updateDownloadPaused = false
        updateTransferState = state.copy(
            phase = UpdateTransferPhase.DOWNLOADING,
            connectionStatus = if (validatedDefaultNetwork()) "Connected" else "Waiting for connection",
            log = appendBoundedUpdateLog(state.log, "Download resumed.")
        )
    }

    private fun requestCancelUpdateDownload() {
        val state = updateTransferState ?: return
        if (state.phase !in setOf(UpdateTransferPhase.CONNECTING, UpdateTransferPhase.DOWNLOADING, UpdateTransferPhase.PAUSED)) return
        if (state.phase != UpdateTransferPhase.PAUSED) pauseUpdateDownload()
        updateCancelConfirmationVisible = true
    }

    private fun dismissCancelUpdateDownload() {
        if (!updateCancelConfirmationVisible) return
        updateCancelConfirmationVisible = false
        resumeUpdateDownload()
    }

    private fun confirmCancelUpdateDownload() {
        val state = updateTransferState ?: return
        if (!updateCancelConfirmationVisible || state.phase != UpdateTransferPhase.PAUSED) return
        updateCancelConfirmationVisible = false
        updateDownloadCancelRequested = true
        updateDownloadPaused = false
        updateTransferState = state.copy(
            connectionStatus = "Canceling",
            log = appendBoundedUpdateLog(state.log, "Cancel confirmed. Stopping download…")
        )
        activeUpdateDownloadCall?.cancel()
    }

    private fun installDownloadedUpdate() {
        val state = updateTransferState ?: return
        if (state.phase != UpdateTransferPhase.COMPLETED) return
        val apk = state.apk?.takeIf { it.isFile } ?: return
        updateTransferState = state.copy(log = appendBoundedUpdateLog(state.log, "Opening Android package installer…"))
        requestPackageInstall(apk)
    }

    private fun closeUpdateTransfer() {
        val state = updateTransferState ?: return
        if (state.phase !in setOf(UpdateTransferPhase.COMPLETED, UpdateTransferPhase.CANCELED, UpdateTransferPhase.FAILED)) return
        state.apk?.let { runCatching { it.delete() } }
        updateTransferState = null
        updateCancelConfirmationVisible = false
    }

    private fun appendBoundedUpdateLog(existing: List<String>, message: String): List<String> =
        (existing + message).takeLast(120)

    private fun formatUpdateBytes(bytes: Long): String {
        if (bytes < 1024L) return "$bytes B"
        val units = listOf("KiB", "MiB", "GiB")
        var value = bytes.toDouble()
        var unitIndex = -1
        while (value >= 1024.0 && unitIndex < units.lastIndex) {
            value /= 1024.0
            unitIndex += 1
        }
        return if (value >= 100.0) "%.0f %s".format(java.util.Locale.US, value, units[unitIndex]) else "%.1f %s".format(java.util.Locale.US, value, units[unitIndex])
    }

    private fun formatUpdateSpeed(bytesPerSecond: Long): String =
        if (bytesPerSecond <= 0L) "—" else "${formatUpdateBytes(bytesPerSecond)}/s"

    private suspend fun publishUpdateTransfer(
        phase: UpdateTransferPhase,
        bytesDownloaded: Long,
        totalBytes: Long?,
        bytesPerSecond: Long,
        connectionStatus: String,
        logLine: String? = null
    ) {
        withContext(Dispatchers.Main.immediate) {
            val current = updateTransferState ?: return@withContext
            if (updateDownloadCancelRequested) return@withContext
            val effectivePaused = updateDownloadPaused && phase == UpdateTransferPhase.DOWNLOADING
            updateTransferState = current.copy(
                phase = if (effectivePaused) UpdateTransferPhase.PAUSED else phase,
                bytesDownloaded = bytesDownloaded,
                totalBytes = totalBytes,
                bytesPerSecond = if (effectivePaused) 0L else bytesPerSecond,
                connectionStatus = if (effectivePaused) "Paused" else connectionStatus,
                log = if (logLine == null) current.log else appendBoundedUpdateLog(current.log, logLine)
            )
        }
    }

    private suspend fun downloadUpdateApk(update: AppUpdate): File = withContext(Dispatchers.IO) {
        val safeAssetName = update.assetName.substringAfterLast('/').substringAfterLast('\\').takeIf { it.endsWith(".apk", true) && it.length in 5..160 } ?: error("The release asset has an invalid APK filename.")
        val updateDir = File(cacheDir, "updates").apply { mkdirs() }
        val target = File(updateDir, safeAssetName)
        if (target.parentFile?.canonicalFile != updateDir.canonicalFile) error("The release asset path is invalid.")
        val temp = File(updateDir, "$safeAssetName.part")
        try {
            currentCoroutineContext().ensureActive()
            if (updateDownloadCancelRequested) error("Update download canceled.")
            while (updateDownloadPaused && !updateDownloadCancelRequested) delay(100L)
            val request = Request.Builder().url(update.downloadUrl).header("User-Agent", "VulkanScope/${installedVersionName()}").get().build()
            val call = ipv6PreferredDownloadClient.newCall(request)
            activeUpdateDownloadCall = call
            try {
                call.execute().use { response ->
                    if (!response.isSuccessful) error("Update download failed (HTTP ${response.code}).")
                    val body = response.body
                    val contentLength = body.contentLength().takeIf { it >= 0L }
                    if (contentLength != null && contentLength > 256L * 1024L * 1024L) error("Update package exceeds the safety limit.")
                    publishUpdateTransfer(
                        phase = if (updateDownloadPaused) UpdateTransferPhase.PAUSED else UpdateTransferPhase.DOWNLOADING,
                        bytesDownloaded = 0L,
                        totalBytes = contentLength,
                        bytesPerSecond = 0L,
                        connectionStatus = if (updateDownloadPaused) "Paused" else "Connected",
                        logLine = "Connected. HTTP ${response.code}${contentLength?.let { "; ${formatUpdateBytes(it)} expected" } ?: ""}."
                    )
                    body.byteStream().use { input ->
                        FileOutputStream(temp).use { output ->
                            val buffer = ByteArray(64 * 1024)
                            var total = 0L
                            var speedWindowBytes = 0L
                            var speedWindowStarted = System.nanoTime()
                            var lastLogAt = speedWindowStarted
                            while (true) {
                                currentCoroutineContext().ensureActive()
                                if (updateDownloadCancelRequested) error("Update download canceled.")
                                while (updateDownloadPaused && !updateDownloadCancelRequested) {
                                    delay(100L)
                                    currentCoroutineContext().ensureActive()
                                }
                                if (updateDownloadCancelRequested) error("Update download canceled.")
                                val count = input.read(buffer)
                                if (count < 0) break
                                total += count
                                speedWindowBytes += count
                                if (total > 256L * 1024L * 1024L) error("Update package exceeds the safety limit.")
                                output.write(buffer, 0, count)
                                val now = System.nanoTime()
                                val elapsed = now - speedWindowStarted
                                if (elapsed >= 400_000_000L) {
                                    val speed = ((speedWindowBytes.toDouble() * 1_000_000_000.0) / elapsed.toDouble()).toLong().coerceAtLeast(0L)
                                    val connected = validatedDefaultNetwork()
                                    val logLine = if (now - lastLogAt >= 2_000_000_000L) {
                                        lastLogAt = now
                                        "Received ${formatUpdateBytes(total)}${contentLength?.let { " / ${formatUpdateBytes(it)}" } ?: ""} at ${formatUpdateSpeed(speed)}."
                                    } else null
                                    publishUpdateTransfer(
                                        phase = UpdateTransferPhase.DOWNLOADING,
                                        bytesDownloaded = total,
                                        totalBytes = contentLength,
                                        bytesPerSecond = speed,
                                        connectionStatus = if (connected) "Connected" else "Connection unavailable",
                                        logLine = logLine
                                    )
                                    speedWindowBytes = 0L
                                    speedWindowStarted = now
                                }
                            }
                            output.fd.sync()
                            publishUpdateTransfer(
                                phase = UpdateTransferPhase.VERIFYING,
                                bytesDownloaded = total,
                                totalBytes = contentLength,
                                bytesPerSecond = 0L,
                                connectionStatus = "Verifying",
                                logLine = "Download finished. Verifying package identity, signature and version…"
                            )
                        }
                    }
                }
            } finally {
                if (activeUpdateDownloadCall === call) activeUpdateDownloadCall = null
            }
            currentCoroutineContext().ensureActive()
            if (updateDownloadCancelRequested) error("Update download canceled.")
            if (!temp.renameTo(target)) { temp.copyTo(target, overwrite = true); temp.delete() }
            val archiveFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) PackageManager.GET_SIGNING_CERTIFICATES else PackageManager.GET_SIGNATURES
            try {
                val archive = packageManager.getPackageArchiveInfo(target.absolutePath, archiveFlags) ?: error("Downloaded file is not a valid Android package.")
                if (archive.packageName != packageName) error("Downloaded package identity does not match VulkanScope.")
                val installed = packageManager.getPackageInfo(packageName, archiveFlags)
                if (!packageSigningCertificatesMatch(installed, archive)) error("Downloaded package signing certificate does not match the installed VulkanScope build.")
                val archiveVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) archive.longVersionCode else archive.versionCode.toLong()
                val installedVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) installed.longVersionCode else installed.versionCode.toLong()
                if (archiveVersionCode <= installedVersionCode) error("Downloaded package versionCode is not newer than the installed VulkanScope build.")
                val archiveVersion = archive.versionName ?: error("Downloaded package has no version metadata.")
                if (archiveVersion != update.version) error("Downloaded package versionName does not match the selected VulkanScope release.")
                if (!isNewerVersion(archiveVersion, installedVersionName())) error("Downloaded package versionName is not newer than the installed VulkanScope version.")
                target
            } catch (error: Throwable) {
                runCatching { target.delete() }
                throw error
            }
        } finally {
            if (temp.exists()) temp.delete()
        }
    }

    private fun packageSigningCertificatesMatch(installed: android.content.pm.PackageInfo, archive: android.content.pm.PackageInfo): Boolean {
        fun encoded(signatures: Array<android.content.pm.Signature>): Set<String> = signatures.map { Base64.encodeToString(it.toByteArray(), Base64.NO_WRAP) }.toSet()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val installedInfo = installed.signingInfo ?: return false
            val archiveInfo = archive.signingInfo ?: return false
            val installedCurrent = encoded(installedInfo.apkContentsSigners)
            if (installedCurrent.isEmpty()) return false
            if (installedInfo.hasMultipleSigners() || archiveInfo.hasMultipleSigners()) {
                val archiveCurrent = encoded(archiveInfo.apkContentsSigners)
                return archiveCurrent.isNotEmpty() && installedCurrent == archiveCurrent
            }
            val archiveHistory = encoded(archiveInfo.signingCertificateHistory)
            return archiveHistory.isNotEmpty() && archiveHistory.containsAll(installedCurrent)
        }
        @Suppress("DEPRECATION")
        val installedLegacy = encoded(installed.signatures ?: emptyArray())
        @Suppress("DEPRECATION")
        val archiveLegacy = encoded(archive.signatures ?: emptyArray())
        return installedLegacy.isNotEmpty() && installedLegacy == archiveLegacy
    }

    private fun requestPackageInstall(apk: File) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !packageManager.canRequestPackageInstalls()) {
            pendingUpdateApk = apk
            startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:$packageName")))
            return
        }
        launchPackageInstaller(apk)
    }

    private fun launchPackageInstaller(apk: File) {
        val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", apk)
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    private fun requestDriverModeChange(mode: DriverMode) {
        if (!completeReportMutationReady() || driverImportInFlight || turnipManagerBusy) {
            android.widget.Toast.makeText(this, "Wait for the complete Vulkan® collection pass before changing the driver source.", android.widget.Toast.LENGTH_LONG).show()
            return
        }
        if (mode == DriverMode.TURNIP && turnipSupport != TurnipSupport.SUPPORTED) return
        if (mode == driverMode) return
        driverModeValidationJob?.cancel()
        if (mode == DriverMode.SYSTEM) {
            pendingDriverModeConfirmation = mode
            return
        }
        driverModeValidationJob = activityScope.launch {
            val installed = withContext(Dispatchers.IO) { findInstalledTurnipLibrary() != null }
            if (!completeReportMutationReady() || driverImportInFlight || turnipManagerBusy || mode == driverMode) return@launch
            if (installed) {
                pendingDriverModeConfirmation = mode
            } else {
                android.widget.Toast.makeText(this@MainActivity, "No active Turnip driver is selected. Open Settings and activate a driver from the Driver manager.", android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun confirmDriverModeChange() {
        val mode = pendingDriverModeConfirmation ?: return
        pendingDriverModeConfirmation = null
        if (!completeReportMutationReady() || driverImportInFlight || turnipManagerBusy || mode == driverMode) return
        if (mode == DriverMode.SYSTEM) {
            applyDriverModeChange(mode, false)
            return
        }
        driverModeValidationJob?.cancel()
        driverModeValidationJob = activityScope.launch {
            val installed = withContext(Dispatchers.IO) { findInstalledTurnipLibrary() != null }
            if (!completeReportMutationReady() || driverImportInFlight || turnipManagerBusy || mode == driverMode) return@launch
            if (installed) applyDriverModeChange(mode, false)
            else android.widget.Toast.makeText(this@MainActivity, "The active Turnip driver is no longer available.", android.widget.Toast.LENGTH_LONG).show()
        }
    }

    private fun prepareDriverSurfaceRebind() {
        synchronized(surfaceLock) {
            currentSurface = null
            surfaceGeneration += 1L
            surfaceRefreshPending = false
        }
        driverSurfaceRebindPending = true
        surfaceHostGeneration += 1L
    }

    private fun applyDriverModeChange(mode: DriverMode, forceRefresh: Boolean) {
        if (!completeReportMutationReady()) return
        if (!forceRefresh && mode == driverMode) return
        requestedQueryGroups.clear()
        queryTimingMs = emptyMap()
        prepareDriverSurfaceRebind()
        driverMode = mode
        latestReport = null
        reportLoading = true
        prefs.edit().putString("driver_mode", mode.name).apply()
    }

    private fun findInstalledTurnipLibrary(): File? = resolveInstalledTurnipIcd()?.let(::File)

    private fun refreshDisplayReport() {
        displayReportState = displayReport()
    }

    @Suppress("DEPRECATION")
    private fun activeDisplay(): android.view.Display? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        display ?: getSystemService(DisplayManager::class.java).getDisplay(android.view.Display.DEFAULT_DISPLAY)
    } else {
        windowManager.defaultDisplay
    }

    @Suppress("DEPRECATION")
    private fun displayReport(): DisplayReport {
        val display = activeDisplay()
            ?: return DisplayReport("Unknown", "Unknown", null, "Unknown", emptyList(), "Unknown", "Unknown", "Unknown", emptyList(), "unknown")
        val mode = display.mode
        val hdr = display.hdrCapabilities
        val hdrTypesResult = runCatching {
            if (android.os.Build.VERSION.SDK_INT >= 34) mode.supportedHdrTypes else hdr?.supportedHdrTypes ?: intArrayOf()
        }
        val hdrTypes = hdrTypesResult.getOrDefault(intArrayOf())
        val hdrNames = hdrTypes.filter { it != -1 }.map { hdrTypeName(it) }.distinct()
        val modes = display.supportedModes.map { "${it.physicalWidth} × ${it.physicalHeight} · ${formatHz(it.refreshRate)}" }.distinct().sorted()
        val wideGamut = if (android.os.Build.VERSION.SDK_INT >= 26) display.isWideColorGamut else null
        val preferred = if (android.os.Build.VERSION.SDK_INT >= 29) display.preferredWideGamutColorSpace?.name ?: "Not exposed" else "Unavailable: requires Android API 29+"
        val hdrCapabilityStatus = when {
            hdrTypesResult.isFailure -> "unavailable"
            android.os.Build.VERSION.SDK_INT >= 34 -> "available"
            hdr != null -> "available"
            else -> "unknown"
        }
        return DisplayReport(
            "${mode.physicalWidth} × ${mode.physicalHeight}",
            formatHz(mode.refreshRate),
            wideGamut,
            preferred,
            hdrNames,
            hdr?.let { formatLuminance(it.desiredMinLuminance) } ?: "Unknown: HDR capabilities unavailable",
            hdr?.let { formatLuminance(it.desiredMaxLuminance) } ?: "Unknown: HDR capabilities unavailable",
            hdr?.let { formatLuminance(it.desiredMaxAverageLuminance) } ?: "Unknown: HDR capabilities unavailable",
            modes,
            hdrCapabilityStatus
        )
    }

    private suspend fun collectReport(modeSnapshot: DriverMode): VulkanReport = withContext(Dispatchers.IO) {
        collectMutex.withLock {
            collectBaseReport(modeSnapshot)
        }
    }

    private suspend fun collectBaseReport(modeSnapshot: DriverMode): VulkanReport {
        var lastParsed: VulkanReport? = null
        var lastFailure = "Base probe returned no terminal result."
        for (attempt in 0 until 2) {
            val raw = runCatching { runProbe(null, modeSnapshot) }.getOrElse { e ->
                Log.e("VulkanScope", "Vulkan base probe failed (attempt=${attempt + 1})", e)
                lastFailure = e.message ?: "probe failed"
                break
            }
            Log.i("VulkanScope", "Vulkan base probe result bytes=${raw.length} attempt=${attempt + 1}")
            val root = runCatching { JSONObject(raw) }.getOrElse { e ->
                Log.e("VulkanScope", "Vulkan base JSON parse failed before report decoding: length=${raw.length} attempt=${attempt + 1}", e)
                lastFailure = e.message ?: "invalid probe result"
                break
            }
            val parsed = runCatching { parseReport(raw) }.getOrElse { e ->
                Log.e("VulkanScope", "Vulkan base report decode failed: length=${raw.length} attempt=${attempt + 1}", e)
                lastFailure = e.message ?: "invalid probe result"
                break
            }
            if (hasCompleteBaseCoverage(parsed)) return parsed
            lastParsed = parsed
            lastFailure = parsed.error ?: "Base probe returned an incomplete core Vulkan dataset."
            Log.w(
                "VulkanScope",
                "Rejecting incomplete base report attempt=${attempt + 1}: status=${root.optString("status", "unknown")} " +
                    parsed.devices.joinToString { d ->
                        "${d.name}: features=${d.features.size}, queues=${d.queues.size}, heaps=${d.heaps.size}, memoryTypes=${d.memoryTypes.size}, limits=${d.limits.size}, extensions=${d.extensions.size}"
                    }
            )
            val retryablePartial = attempt == 0 &&
                root.optString("status", "unavailable") == "incomplete" &&
                parsed.devices.isNotEmpty() &&
                parsed.error?.contains("timeout", ignoreCase = true) != true
            if (!retryablePartial) break
            Log.w("VulkanScope", "Retrying one bounded base probe because the first terminal result retained partial positive Vulkan evidence.")
        }
        return lastParsed?.copy(
            error = lastParsed.error ?: "Vulkan base report was incomplete: $lastFailure",
            baseReportComplete = false
        ) ?: VulkanReport(
            "Unknown",
            emptyList(),
            emptyList(),
            emptyList(),
            "Vulkan base report unavailable: $lastFailure"
        )
    }

    private fun hasCompleteBaseCoverage(report: VulkanReport): Boolean =
        report.baseReportComplete &&
            report.error == null &&
            report.devices.isNotEmpty() &&
            report.devices.all { device ->
                device.deviceExtensionStatus == "available" &&
                    device.features.size >= 55 &&
                    device.queues.isNotEmpty() &&
                    device.heaps.isNotEmpty() &&
                    device.memoryTypes.isNotEmpty() &&
                    device.limits.isNotEmpty()
            }

    private suspend fun enrichReport(base: VulkanReport, modeSnapshot: DriverMode): VulkanReport = withContext(Dispatchers.IO) {
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
            val rawMetadata = runIsolatedProbe("metadata", modeSnapshot)
            enriched = mergeMetadataReport(enriched, rawMetadata)
        }.onFailure { e ->
            Log.e("VulkanScope", "Vulkan metadata query failed", e)
            enriched = markMetadataProbeUnavailable(enriched, e.message ?: "The dedicated metadata query failed.")
        }

        if (enriched.devices.isEmpty()) return@withContext enriched

        val surfaceSnapshot = synchronized(surfaceLock) {
            currentSurface?.takeIf { it.isValid }?.let { it to surfaceGeneration }
        }
        if (surfaceSnapshot != null && enriched.devices.isNotEmpty()) {
            val (surface, surfaceToken) = surfaceSnapshot
            runCatching {
                val rawSurface = runSurfaceProbe(surface, modeSnapshot)
                val stillCurrent = synchronized(surfaceLock) {
                    currentSurface === surface && surfaceGeneration == surfaceToken && surface.isValid
                }
                if (stillCurrent) {
                    enriched = mergeSurfaceProbeReport(enriched, rawSurface)
                    synchronized(surfaceLock) {
                        if (currentSurface === surface && surfaceGeneration == surfaceToken) surfaceRefreshPending = false
                    }
                } else {
                    synchronized(surfaceLock) {
                        if (currentSurface?.isValid == true) surfaceRefreshPending = true
                    }
                }
            }.onFailure { e ->
                Log.e("VulkanScope", "Vulkan surface probe failed", e)
                val stillCurrent = synchronized(surfaceLock) {
                    currentSurface === surface && surfaceGeneration == surfaceToken && surface.isValid
                }
                if (stillCurrent) {
                    enriched = markSurfaceProbeState(enriched, "unavailable", e.message ?: "The dedicated Surface query failed.")
                    synchronized(surfaceLock) {
                        if (currentSurface === surface && surfaceGeneration == surfaceToken) surfaceRefreshPending = false
                    }
                } else {
                    synchronized(surfaceLock) {
                        if (currentSurface?.isValid == true) surfaceRefreshPending = true
                    }
                }
            }
        } else {
            enriched = markSurfaceProbeState(enriched, "unavailable", "No live Android Surface was available for dedicated Surface collection.")
        }

        enriched
    }

    private suspend fun runIsolatedProbe(group: String, modeSnapshot: DriverMode = driverMode): String =
        runServiceProbe(group, null, if (group in ISOLATED_ADVANCED_GROUPS.keys) 30_000L else 12_000L, modeSnapshot)

    private fun requestQueryGroup(group: String) {
        if (driverImportInFlight) {
            collectionPending = true
            return
        }
        if (collectionInFlight) return
        if (latestReport?.devices.isNullOrEmpty()) return
        if (!requestedQueryGroups.add(group)) return
        val generation = collectionGeneration
        val modeSnapshot = driverMode
        val taskId = "ad-hoc:$group:${System.nanoTime()}"
        beginCollectionTask(taskId)
        activityScope.launch {
            try {
                val current = latestReport ?: return@launch
                val label = ISOLATED_ADVANCED_GROUPS[group] ?: ISOLATED_CORE_GROUPS[group]
                val extensionName = if (group.startsWith("ext::")) group.removePrefix("ext::") else ISOLATED_EXTENSION_GROUPS[group]
                if (extensionName != null && current.devices.none { device -> device.extensions.any { it.name == extensionName } }) return@launch
                val timingStartNanos = System.nanoTime()
                val raw = runCatching { runIsolatedProbe(group, modeSnapshot) }.getOrElse { e ->
                    Log.e("VulkanScope", "Vulkan lazy query failed: $group", e)
                    JSONObject().put("status", "unavailable").put("group", group).put("reason", e.message ?: "The dedicated lazy query failed.").put("devices", JSONArray()).toString()
                }.ifBlank {
                    JSONObject().put("status", "unavailable").put("group", group).put("reason", "The dedicated lazy query returned no data.").put("devices", JSONArray()).toString()
                }
                val elapsedMs = ((System.nanoTime() - timingStartNanos) / 1_000_000L).coerceAtLeast(0L)
                withContext(Dispatchers.Main.immediate) {
                    if (generation != collectionGeneration || modeSnapshot != driverMode || latestReport == null) return@withContext
                    queryTimingMs = queryTimingMs + (group to elapsedMs)
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
            Page.Video -> listOf("queue2", "videoCapabilities")
            Page.Properties -> listOf("core11", "core12", "core13", "tools", "groups")
            Page.Extensions -> latestReport?.devices
                ?.flatMap { device -> device.extensions.map { it.name } }
                ?.toSet()
                ?.asSequence()
                ?.filter { it in VALIDATED_PHYSICAL_DEVICE_QUERY_EXTENSIONS }
                ?.sorted()
                ?.map { "ext::$it" }
                ?.toList()
                ?: emptyList()
            else -> emptyList()
        }
        groups.forEach(::requestQueryGroup)
    }

    internal suspend fun runVulkanSelfTests(vendorId: Long, deviceId: Long): String = runServiceProbe("selftest:$vendorId:$deviceId", null, 30_000L, driverMode)

    private suspend fun runProbe(surface: Surface?, modeSnapshot: DriverMode): String = runServiceProbe("base", surface, 20_000L, modeSnapshot)

    private suspend fun runSurfaceProbe(surface: Surface, modeSnapshot: DriverMode = driverMode): String {
        val first = runServiceProbe("surface", surface, 25_000L, modeSnapshot)
        val nativeWindowInUse = runCatching {
            JSONObject(first).optString("reason").contains("VkResult=-1000000001")
        }.getOrDefault(false)
        if (!nativeWindowInUse || !surface.isValid) return first
        delay(200L)
        if (!surface.isValid) return first
        return runServiceProbe("surface", surface, 25_000L, modeSnapshot)
    }

    private fun runningVulkanProbePids(): List<Int> = runCatching {
        val activityManager = getSystemService(ActivityManager::class.java) ?: return@runCatching emptyList()
        val expectedName = "${packageName}:vulkan_probe"
        activityManager.runningAppProcesses.orEmpty()
            .filter { it.uid == Process.myUid() && it.processName == expectedName }
            .map { it.pid }
            .distinct()
    }.getOrElse { error ->
        Log.w("VulkanScope", "Unable to inspect the dedicated Vulkan probe process", error)
        emptyList()
    }

    private fun stopVulkanProbeProcess() {
        runCatching { stopService(Intent(this@MainActivity, VulkanProbeService::class.java)) }
        runningVulkanProbePids().forEach { pid ->
            runCatching { Process.killProcess(pid) }.onFailure { error ->
                Log.w("VulkanScope", "Unable to kill stale Vulkan probe pid=$pid", error)
            }
        }
    }

    private suspend fun ensureVulkanProbeProcessQuiescent(timeoutMs: Long = 1_500L): Boolean {
        val deadline = System.nanoTime() + timeoutMs * 1_000_000L
        val stopRequested = runCatching {
            withContext(Dispatchers.Main.immediate) { stopService(Intent(this@MainActivity, VulkanProbeService::class.java)) }
        }.getOrDefault(false)
        if (stopRequested) delay(150L)
        while (true) {
            val pids = runningVulkanProbePids()
            if (pids.isEmpty()) return true
            pids.forEach { pid -> runCatching { Process.killProcess(pid) } }
            if (System.nanoTime() >= deadline) {
                Log.e("VulkanScope", "Previous dedicated Vulkan probe process remained alive after bounded teardown: pids=$pids")
                return false
            }
            delay(25L)
        }
    }

    private suspend fun runServiceProbe(group: String, surface: Surface?, timeoutMs: Long, modeSnapshot: DriverMode): String = probeMutex.withLock {
        fun unavailableProbe(reason: String): String = if (group == "base") {
            JSONObject().put("status", "unavailable").put("reason", reason).put("baseReportComplete", false).put("devices", JSONArray()).toString()
        } else {
            JSONObject().put("status", "unavailable").put("group", group).put("reason", reason).put("devices", JSONArray()).toString()
        }
        if (driverImportInFlight) return@withLock unavailableProbe("The Vulkan probe was deferred while the private driver bundle was being updated.")
        if (!ensureVulkanProbeProcessQuiescent()) return@withLock unavailableProbe("The previous dedicated Vulkan probe process could not be terminated within the bounded teardown window.")
        val maxProbeResultBytes = 64L * 1024L * 1024L
        val resultFile = File(cacheDir, "vulkan_probe_${java.util.UUID.randomUUID()}.json")
        val crashMarkerFile = File(resultFile.absolutePath + ".crash")
        val terminalFile = File(resultFile.absolutePath + ".done")
        resultFile.delete()
        crashMarkerFile.delete()
        terminalFile.delete()
        val driverPaths = withContext(Dispatchers.IO) {
            findTurnipIcd(modeSnapshot) to findTurnipBundle(modeSnapshot)
        }
        val intent = Intent(this@MainActivity, VulkanProbeService::class.java)
            .putExtra(VulkanProbeService.EXTRA_QUERY_GROUP, group)
            .putExtra(VulkanProbeService.EXTRA_DRIVER_MODE, modeSnapshot.name)
            .putExtra(VulkanProbeService.EXTRA_DRIVER_ICD, driverPaths.first)
            .putExtra(VulkanProbeService.EXTRA_DRIVER_BUNDLE, driverPaths.second)
            .putExtra(VulkanProbeService.EXTRA_HOOK_LIB_DIR, applicationInfo.nativeLibraryDir)
            .putExtra(VulkanProbeService.EXTRA_RESULT_PATH, resultFile.absolutePath)
            .putExtra(VulkanProbeService.EXTRA_TERMINAL_PATH, terminalFile.absolutePath)
            .putExtra(VulkanProbeService.EXTRA_TIMEOUT_MS, timeoutMs)
        if (surface != null) intent.putExtra(VulkanProbeService.EXTRA_SURFACE, surface)
        val started = runCatching { withContext(Dispatchers.Main.immediate) { startService(intent) } }.isSuccess
        if (!started) {
            resultFile.delete()
            crashMarkerFile.delete()
            terminalFile.delete()
            return@withLock if (group == "base") {
                "{\"status\":\"unavailable\",\"reason\":\"Unable to start the dedicated Vulkan probe process\",\"devices\":[]}"
            } else {
                "{\"status\":\"unavailable\",\"group\":${JSONObject.quote(group)},\"reason\":\"Unable to start the dedicated Vulkan query process\",\"devices\":[]}"
            }
        }
        val result = withContext(Dispatchers.IO) {
            var value: String? = null
            var lastObservedLength = -1L
            var lastObservedModified = -1L
            var lastObservedInode = -1L
            fun crashDetected(): Boolean = crashMarkerFile.isFile && crashMarkerFile.length() > 0L
            fun unavailable(reason: String): String = unavailableProbe(reason)
            fun terminalCandidate(candidate: String): Boolean {
                return strictProbeTerminalCandidate(candidate, group == "base")
            }
            fun readPublishedCandidate(): String? {
                if (!resultFile.isFile) return null
                val resultLength = resultFile.length()
                if (resultLength !in 1L..maxProbeResultBytes) return null
                return runCatching { readFileTextLimited(resultFile, maxProbeResultBytes.toInt()) }.getOrNull()
            }
            suspend fun readServiceTerminalCandidateWithGrace(graceMs: Long = 300L): String? {
                val deadline = System.nanoTime() + graceMs * 1_000_000L
                do {
                    val candidate = readPublishedCandidate()
                    if (candidate != null && terminalCandidate(candidate)) return candidate
                    delay(20L)
                } while (System.nanoTime() < deadline)
                return readPublishedCandidate()?.takeIf(::terminalCandidate)
            }
            try {
                withTimeout(timeoutMs) {
                    while (value == null) {
                        if (terminalFile.isFile) {
                            val terminalCandidateText = readServiceTerminalCandidateWithGrace()
                            value = terminalCandidateText ?: unavailable("The dedicated Vulkan $group probe signaled completion but no valid terminal JSON became readable within the bounded handoff window.")
                            Log.i("VulkanScope", "Observed service-owned terminal $group publication; accepted only after a bounded stable-result reread.")
                            continue
                        }
                        if (crashDetected()) {
                            if (group == "base") {
                                val completedBeforeCrash = if (resultFile.isFile && resultFile.length() in 1L..maxProbeResultBytes) {
                                    runCatching { readFileTextLimited(resultFile, maxProbeResultBytes.toInt()) }.getOrNull()?.takeIf { candidate ->
                                        runCatching { JSONObject(candidate).optBoolean("baseReportComplete", false) }.getOrDefault(false)
                                    }
                                } else null
                                value = completedBeforeCrash ?: unavailable("The dedicated Vulkan base probe terminated before publishing a complete base report.")
                            } else {
                                value = unavailable("The dedicated Vulkan $group probe terminated after a native signal before publishing a valid result.")
                            }
                            stopVulkanProbeProcess()
                            continue
                        }
                        if (resultFile.isFile) {
                            val resultLength = resultFile.length()
                            val resultModified = resultFile.lastModified()
                            if (resultLength > maxProbeResultBytes) {
                                value = unavailable("The Vulkan probe result exceeded the safety size limit.")
                                stopVulkanProbeProcess()
                                continue
                            }
                            val resultInode = runCatching { android.system.Os.stat(resultFile.path).st_ino }.getOrDefault(-1L)
                            val checkpointChanged = resultLength != lastObservedLength || resultModified != lastObservedModified || resultInode != lastObservedInode
                            if (resultLength > 0L && checkpointChanged) {
                                lastObservedLength = resultLength
                                lastObservedModified = resultModified
                                lastObservedInode = resultInode
                                if (crashDetected()) continue
                                kotlinx.coroutines.delay(20L)
                            } else {
                                kotlinx.coroutines.delay(40L)
                            }
                        } else {
                            kotlinx.coroutines.delay(60L)
                        }
                    }
                }
            } catch (_: kotlinx.coroutines.TimeoutCancellationException) {
                val publishedBeforeTimeout = readPublishedCandidate()?.takeIf(::terminalCandidate)
                stopVulkanProbeProcess()
                delay(150L)
                val settledPublication = publishedBeforeTimeout ?: readPublishedCandidate()?.takeIf(::terminalCandidate)
                value = settledPublication ?: unavailable("The dedicated $group probe did not complete within the timeout.")
                if (settledPublication != null) Log.w("VulkanScope", "Recovered an atomic $group probe publication at the timeout boundary instead of discarding it.")
            } catch (cancelled: CancellationException) {
                withContext(NonCancellable) {
                    val quiescent = ensureVulkanProbeProcessQuiescent()
                    if (!quiescent) Log.e("VulkanScope", "Cancelled Vulkan $group probe did not reach a confirmed quiescent state before request cleanup.")
                }
                throw cancelled
            } finally {
                resultFile.delete()
                crashMarkerFile.delete()
                terminalFile.delete()
            }
            value ?: unavailable("The dedicated $group probe returned no result.")
        }
        result
    }


}

private fun hdrTypeName(type: Int): String = when (type) {
    1 -> "Dolby Vision"
    2 -> "HDR10"
    3 -> "HLG"
    4 -> "HDR10+"
    6 -> "HLG+"
    else -> "Android HDR type $type"
}

private fun formatHz(value: Float): String = String.format(java.util.Locale.US, "%.2f Hz", value)
private fun formatLuminance(value: Float): String = if (!value.isFinite() || value == android.view.Display.HdrCapabilities.INVALID_LUMINANCE) "Unavailable" else String.format(java.util.Locale.US, "%.3f cd/m²", value)

private fun hdrTypesText(display: DisplayReport): String = when {
    display.hdrTypes.isNotEmpty() -> display.hdrTypes.joinToString(", ")
    display.hdrCapabilityStatus == "available" -> "None reported"
    display.hdrCapabilityStatus == "unknown" -> "Unknown"
    else -> "Unavailable"
}

private fun apiAtLeast(value: String, major: Int, minor: Int): Boolean {
    val parts = value.trim().split('.')
    val parsedMajor = parts.getOrNull(0)?.toIntOrNull() ?: return false
    val parsedMinor = parts.getOrNull(1)?.toIntOrNull() ?: return false
    return parsedMajor > major || (parsedMajor == major && parsedMinor >= minor)
}

private fun apiVersionAtLeast(value: String, required: String): Boolean {
    fun parse(version: String): Triple<Int, Int, Int>? {
        val parts = version.trim().split('.')
        val major = parts.getOrNull(0)?.toIntOrNull() ?: return null
        val minor = parts.getOrNull(1)?.toIntOrNull() ?: return null
        val patch = parts.getOrNull(2)?.toIntOrNull() ?: 0
        return Triple(major, minor, patch)
    }
    val actual = parse(value) ?: return false
    val minimum = parse(required) ?: return false
    return actual.first > minimum.first ||
        actual.first == minimum.first && (actual.second > minimum.second ||
            actual.second == minimum.second && actual.third >= minimum.third)
}

private fun surfaceColorSpaceExtensionEvidence(extensions: List<ExtensionEntry>, enumerationStatus: String): Pair<Boolean, String> {
    val present = extensions.any { it.name == "VK_EXT_swapchain_colorspace" }
    return when {
        present -> true to "available"
        enumerationStatus == "available" -> false to "not_exposed"
        else -> false to "unknown"
    }
}

private fun markMetadataProbeUnavailable(base: VulkanReport, reason: String): VulkanReport {
    return base.copy(
        devices = base.devices.map { device ->
            device.copy(surfaceColorSpaceExtensionAvailable = false, surfaceColorSpaceExtensionStatus = "unknown", surfaceColorSpaceExtensionEnabled = false)
        },
        instanceExtensionStatus = "unavailable",
        instanceExtensionReason = reason.take(1024),
        instanceLayerStatus = "unavailable",
        instanceLayerReason = reason.take(1024)
    )
}

private fun markSurfaceProbeState(base: VulkanReport, status: String, reason: String): VulkanReport {
    val colorEvidence = surfaceColorSpaceExtensionEvidence(base.instanceExtensions, base.instanceExtensionStatus)
    return base.copy(
    devices = base.devices.map { device ->
        device.copy(
            surfaceAvailable = false,
            surfacePresentationSupported = false,
            surfaceColorSpaceExtensionAvailable = colorEvidence.first,
            surfaceColorSpaceExtensionStatus = colorEvidence.second,
            surfaceColorSpaceExtensionEnabled = false,
            surfaceFormatQueryResult = -1,
            surfaceFormatQueryResultSecond = -1,
            surfaceFormatQuerySecondAttempted = false,
            surfaceFormatQueryAttempted = false,
            surfaceDependentWsiQueryStatus = "unknown",
            surfaceFormatQuerySafetyRejected = false,
            surfaceCapabilities = emptyList(),
            surfaceFormats = emptyList(),
            presentModes = emptyList(),
            presentationQueues = emptyList(),
            presentationQueueEvidence = emptyList(),
            surfaceQueueQuerySafetyRejected = false,
            surfaceQueryStatus = status,
            surfaceQueryReason = reason.take(1024),
            detailedProperties = replaceQueryStatus(
                device.detailedProperties,
                "Surface probe",
                when (status) {
                    "not_applicable" -> "Not applicable: ${reason.take(1024)}"
                    "unknown" -> "Unknown: ${reason.take(1024)}"
                    else -> "Unavailable: ${reason.take(1024)}"
                }
            )
        )
    }
)
}

private fun mergeMetadataReport(base: VulkanReport, raw: String): VulkanReport {
    val root = runCatching { JSONObject(raw) }.getOrElse {
        return markMetadataProbeUnavailable(base, "The dedicated metadata probe returned invalid JSON.")
    }
    if (root.optString("status", "unavailable") != "available") {
        val reason = root.optString("reason", "The dedicated metadata probe did not complete.")
        return markMetadataProbeUnavailable(base, reason)
    }
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
            runtimeRegistryTokenReferenceCount = registry.optInt("runtimeRegistryTokenReferenceCount", registry.optInt("runtimeExtensionTokenCount", base.registryCoverage.runtimeRegistryTokenReferenceCount)),
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
        instanceLayers += LayerEntry(layer.optString("name"), layer.optString("description"), layer.optInt("specVersion"), layer.optInt("implementationVersion"), layerExtensions, layer.optString("extensionStatus", "unknown"), layer.optString("extensionReason", ""), layer.optBoolean("extensionsComplete", false))
    }
    val instanceExtensionStatus = root.optString("instanceExtensionStatus", "unknown")
    val instanceExtensionReason = root.optString("instanceExtensionReason", "")
    val instanceLayerStatus = root.optString("instanceLayerStatus", "unknown")
    val instanceLayerReason = root.optString("instanceLayerReason", "")
    val mergedInstanceExtensions = when {
        root.optBoolean("instanceExtensionsComplete", false) -> instanceExtensions
        instanceExtensionStatus == "incomplete" -> (base.instanceExtensions + instanceExtensions).distinctBy { it.name to it.specVersion }
        else -> base.instanceExtensions
    }
    val mergedInstanceLayers = when {
        root.optBoolean("instanceLayersComplete", false) -> instanceLayers
        instanceLayerStatus == "incomplete" -> (base.instanceLayers + instanceLayers).distinctBy { it.name to it.specVersion }
        else -> base.instanceLayers
    }
    val colorEvidence = surfaceColorSpaceExtensionEvidence(mergedInstanceExtensions, instanceExtensionStatus)
    return base.copy(
        devices = base.devices.map { device ->
            if (colorEvidence.second == "unknown" && device.surfaceColorSpaceExtensionStatus != "unknown") device
            else device.copy(surfaceColorSpaceExtensionAvailable = colorEvidence.first, surfaceColorSpaceExtensionStatus = colorEvidence.second, surfaceColorSpaceExtensionEnabled = if (colorEvidence.second == "not_exposed") false else device.surfaceColorSpaceExtensionEnabled)
        },
        instanceExtensions = mergedInstanceExtensions,
        instanceLayers = mergedInstanceLayers,
        registryCoverage = if (root.optBoolean("registryCoverageComplete", false)) parsedCoverage else base.registryCoverage,
        instanceExtensionStatus = instanceExtensionStatus,
        instanceExtensionReason = instanceExtensionReason,
        instanceLayerStatus = instanceLayerStatus,
        instanceLayerReason = instanceLayerReason
    )
}

private fun sanitizeReportLabel(value: String): String = value.removePrefix("Validated extension coverage · ")
private fun sanitizeReportSection(section: String): String = sanitizeReportLabel(section)

private fun mergeQueryProperties(existing: List<PropertyEntry>, incoming: List<PropertyEntry>): List<PropertyEntry> =
    (existing + incoming).distinctBy { Triple(it.section, it.name, it.value) }

private fun replaceQueryStatus(properties: List<PropertyEntry>, name: String, value: String): List<PropertyEntry> =
    properties.filterNot { it.section == "Vulkan Query Status" && it.name == name } +
        PropertyEntry("Vulkan Query Status", name, value)

private fun replaceDetailedProperty(properties: List<PropertyEntry>, section: String, name: String, value: String): List<PropertyEntry> =
    properties.filterNot { it.section == section && it.name == name } + PropertyEntry(section, name, value)

private fun vkResultText(value: Int): String = when (value) {
    0 -> "VK_SUCCESS (0)"
    1 -> "VK_NOT_READY (1)"
    2 -> "VK_TIMEOUT (2)"
    3 -> "VK_EVENT_SET (3)"
    4 -> "VK_EVENT_RESET (4)"
    5 -> "VK_INCOMPLETE (5)"
    -1 -> "VK_ERROR_OUT_OF_HOST_MEMORY (-1)"
    -2 -> "VK_ERROR_OUT_OF_DEVICE_MEMORY (-2)"
    -3 -> "VK_ERROR_INITIALIZATION_FAILED (-3)"
    -4 -> "VK_ERROR_DEVICE_LOST (-4)"
    -5 -> "VK_ERROR_MEMORY_MAP_FAILED (-5)"
    -6 -> "VK_ERROR_LAYER_NOT_PRESENT (-6)"
    -7 -> "VK_ERROR_EXTENSION_NOT_PRESENT (-7)"
    -8 -> "VK_ERROR_FEATURE_NOT_PRESENT (-8)"
    -9 -> "VK_ERROR_INCOMPATIBLE_DRIVER (-9)"
    -10 -> "VK_ERROR_TOO_MANY_OBJECTS (-10)"
    -11 -> "VK_ERROR_FORMAT_NOT_SUPPORTED (-11)"
    -12 -> "VK_ERROR_FRAGMENTED_POOL (-12)"
    -13 -> "VK_ERROR_UNKNOWN (-13)"
    -1000011001 -> "VK_ERROR_VALIDATION_FAILED (-1000011001)"
    -1000069000 -> "VK_ERROR_OUT_OF_POOL_MEMORY (-1000069000)"
    -1000072003 -> "VK_ERROR_INVALID_EXTERNAL_HANDLE (-1000072003)"
    -1000257000 -> "VK_ERROR_INVALID_OPAQUE_CAPTURE_ADDRESS (-1000257000)"
    -1000161000 -> "VK_ERROR_FRAGMENTATION (-1000161000)"
    1000297000 -> "VK_PIPELINE_COMPILE_REQUIRED (1000297000)"
    -1000174001 -> "VK_ERROR_NOT_PERMITTED (-1000174001)"
    -1000000000 -> "VK_ERROR_SURFACE_LOST_KHR (-1000000000)"
    -1000000001 -> "VK_ERROR_NATIVE_WINDOW_IN_USE_KHR (-1000000001)"
    1000001003 -> "VK_SUBOPTIMAL_KHR (1000001003)"
    -1000001004 -> "VK_ERROR_OUT_OF_DATE_KHR (-1000001004)"
    -1000003001 -> "VK_ERROR_INCOMPATIBLE_DISPLAY_KHR (-1000003001)"
    -1000012000 -> "VK_ERROR_INVALID_SHADER_NV (-1000012000)"
    -1000023000 -> "VK_ERROR_IMAGE_USAGE_NOT_SUPPORTED_KHR (-1000023000)"
    -1000023001 -> "VK_ERROR_VIDEO_PICTURE_LAYOUT_NOT_SUPPORTED_KHR (-1000023001)"
    -1000023002 -> "VK_ERROR_VIDEO_PROFILE_OPERATION_NOT_SUPPORTED_KHR (-1000023002)"
    -1000023003 -> "VK_ERROR_VIDEO_PROFILE_FORMAT_NOT_SUPPORTED_KHR (-1000023003)"
    -1000023004 -> "VK_ERROR_VIDEO_PROFILE_CODEC_NOT_SUPPORTED_KHR (-1000023004)"
    -1000023005 -> "VK_ERROR_VIDEO_STD_VERSION_NOT_SUPPORTED_KHR (-1000023005)"
    -1000158000 -> "VK_ERROR_INVALID_DRM_FORMAT_MODIFIER_PLANE_LAYOUT_EXT (-1000158000)"
    -1000208000 -> "VK_ERROR_PRESENT_TIMING_QUEUE_FULL_EXT (-1000208000)"
    -1000255000 -> "VK_ERROR_FULL_SCREEN_EXCLUSIVE_MODE_LOST_EXT (-1000255000)"
    1000268000 -> "VK_THREAD_IDLE_KHR (1000268000)"
    1000268001 -> "VK_THREAD_DONE_KHR (1000268001)"
    1000268002 -> "VK_OPERATION_DEFERRED_KHR (1000268002)"
    1000268003 -> "VK_OPERATION_NOT_DEFERRED_KHR (1000268003)"
    -1000299000 -> "VK_ERROR_INVALID_VIDEO_STD_PARAMETERS_KHR (-1000299000)"
    -1000338000 -> "VK_ERROR_COMPRESSION_EXHAUSTED_EXT (-1000338000)"
    1000482000 -> "VK_INCOMPATIBLE_SHADER_BINARY_EXT (1000482000)"
    1000483000 -> "VK_PIPELINE_BINARY_MISSING_KHR (1000483000)"
    -1000483000 -> "VK_ERROR_NOT_ENOUGH_SPACE_KHR (-1000483000)"
    else -> "UNKNOWN(raw=$value)"
}

private fun presentationQueueEvidenceText(entry: PresentationQueueEvidence): String = when {
    entry.queryResult != null && entry.queryResult != 0 -> "Unavailable: ${vkResultText(entry.queryResult)}"
    entry.supported -> "PRESENT"
    else -> "NO PRESENT"
}

private fun appendSurfaceEnumerationDiagnostics(surface: JSONObject?, capabilities: MutableList<Pair<String, String>>) {
    if (surface == null) return
    if (surface.has("dependentWsiQueryStatus")) capabilities += "Dependent WSI query status" to surface.optString("dependentWsiQueryStatus", "unknown").replace('_', ' ')
    if (surface.has("capabilityQueryApi")) capabilities += "Surface capability query API" to surface.optString("capabilityQueryApi")
    if (surface.optBoolean("capabilities2QueryAttempted", false)) capabilities += "Surface capabilities2 query" to vkResultText(surface.optInt("capabilities2QueryResult"))
    if (surface.has("capabilities2FallbackUsed")) capabilities += "Surface capabilities2 classic fallback" to surface.optBoolean("capabilities2FallbackUsed", false).toString()
    if (surface.has("formatQueryAttempted")) capabilities += "Surface format query attempted" to surface.optBoolean("formatQueryAttempted", false).toString()
    if (surface.optBoolean("formatQueryAttempted", false)) capabilities += "Surface format count query" to vkResultText(surface.optInt("formatQueryResult"))
    if (surface.optBoolean("formatQuerySecondAttempted", false)) capabilities += "Surface format data query" to vkResultText(surface.optInt("formatQueryResultSecond")) else if (surface.optBoolean("formatQueryAttempted", false)) capabilities += "Surface format data query" to "Not attempted"
    if (surface.has("formatQueryAttemptCount")) capabilities += "Surface format bounded attempts" to surface.optInt("formatQueryAttemptCount").toString()
    if (surface.has("formatQuerySafetyRejected")) capabilities += "Surface format safety rejection" to if (surface.optBoolean("formatQuerySafetyRejected")) "YES · local bound rejected the result" else "NO"
    if (surface.has("formatQuerySpecAnomaly")) capabilities += "Surface format specification anomaly" to surface.optBoolean("formatQuerySpecAnomaly").toString()
    if (surface.optString("formatQueryReason").isNotBlank()) capabilities += "Surface format query provenance" to surface.optString("formatQueryReason").take(1024)
    if (surface.has("surfaceFormats2Attempted")) capabilities += "Surface formats2 attempted" to surface.optBoolean("surfaceFormats2Attempted").toString()
    if (surface.has("surfaceFormats2FallbackUsed")) capabilities += "Surface formats2 classic fallback" to surface.optBoolean("surfaceFormats2FallbackUsed").toString()
    if (surface.has("formatEnumerationComplete")) capabilities += "Surface format enumeration" to if (surface.optBoolean("formatEnumerationComplete")) "Complete" else "Incomplete / unavailable"
    if (surface.optBoolean("presentModeQueryAttempted", false)) capabilities += "Present mode count query" to vkResultText(surface.optInt("presentModeCountQueryResult"))
    if (surface.optBoolean("presentModeDataQueryAttempted", false)) capabilities += "Present mode data query" to vkResultText(surface.optInt("presentModeDataQueryResult")) else if (surface.optBoolean("presentModeQueryAttempted", false)) capabilities += "Present mode data query" to "Not attempted"
    if (surface.has("presentModeQueryAttemptCount")) capabilities += "Present mode bounded attempts" to surface.optInt("presentModeQueryAttemptCount").toString()
    if (surface.has("presentModeQuerySafetyRejected")) capabilities += "Present mode safety rejection" to if (surface.optBoolean("presentModeQuerySafetyRejected")) "YES · local bound rejected the result" else "NO"
    if (surface.has("presentModeQuerySpecAnomaly")) capabilities += "Present mode specification anomaly" to surface.optBoolean("presentModeQuerySpecAnomaly").toString()
    if (surface.optString("presentModeQueryReason").isNotBlank()) capabilities += "Present mode query provenance" to surface.optString("presentModeQueryReason").take(1024)
    if (surface.has("presentModeEnumerationComplete")) capabilities += "Present mode enumeration" to if (surface.optBoolean("presentModeEnumerationComplete")) "Complete" else "Incomplete / unavailable"
}

private fun physicalIdentityKey(vendorId: Long, deviceId: Long): Pair<Long, Long> = vendorId to deviceId

private fun basePhysicalIdentityUnique(report: VulkanReport, device: DeviceReport): Boolean {
    val key = physicalIdentityKey(device.vendorIdRaw, device.deviceIdRaw)
    return report.devices.count { physicalIdentityKey(it.vendorIdRaw, it.deviceIdRaw) == key } == 1
}

private fun jsonPhysicalIdentityMatches(array: JSONArray, vendorId: Long, deviceId: Long): List<JSONObject> =
    (0 until array.length()).mapNotNull { array.optJSONObject(it) }.filter {
        it.optLong("vendorId", Long.MIN_VALUE) == vendorId && it.optLong("deviceId", Long.MIN_VALUE) == deviceId
    }

private fun mergeSurfaceProbeReport(base: VulkanReport, raw: String): VulkanReport {
    val root = runCatching { JSONObject(raw) }.getOrElse {
        return markSurfaceProbeState(base, "unavailable", "The dedicated Surface probe returned invalid JSON.")
    }
    val status = root.optString("status", "unavailable")
    val reason = root.optString("reason", "The dedicated Surface probe did not return a result.")
    if (status != "available" && status != "incomplete") return markSurfaceProbeState(base, status, reason)
    val resultDevices = root.optJSONArray("devices") ?: JSONArray()
    return base.copy(devices = base.devices.map { device ->
        val matches = jsonPhysicalIdentityMatches(resultDevices, device.vendorIdRaw, device.deviceIdRaw)
        val identityUnique = basePhysicalIdentityUnique(base, device) && matches.size == 1
        val match = matches.singleOrNull()
        if (!identityUnique || match == null) {
            val mappingReason = if (!basePhysicalIdentityUnique(base, device) || matches.size > 1)
                "Surface evidence cannot be attributed safely because vendorId/deviceId identifies more than one physical device and no stable cross-process identity is available."
            else "The Surface probe completed but did not return matching evidence for this physical device."
            markSurfaceProbeState(base.copy(devices = listOf(device)), "unavailable", mappingReason).devices.first()
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
                if (surface?.has(key) == true) capabilities += key to canonicalSurfaceCapabilityValue(key, surface.optString(key))
            }
            appendSurfaceEnumerationDiagnostics(surface, capabilities)
            capabilities += "Surface probe instance API" to root.optString("selectedApiVersion", "Unknown")
            capabilities += "VK_KHR_get_surface_capabilities2 path" to root.optString("surfaceCapabilities2Status", "unknown").replace('_', ' ')
            if (root.optString("surfaceCapabilities2Reason").isNotBlank()) capabilities += "Surface capabilities2 path reason" to root.optString("surfaceCapabilities2Reason").take(1024)
            if (surface?.has("surfaceFormats2Used") == true) {
                capabilities += "Surface format query API" to if (surface.optBoolean("surfaceFormats2Used")) "vkGetPhysicalDeviceSurfaceFormats2KHR" else "vkGetPhysicalDeviceSurfaceFormatsKHR"
            }
            if (surface?.has("formatCount") == true) capabilities += "Surface format returned count" to surface.optInt("formatCount").toString()
            val presentModes = mutableListOf<String>()
            val presentArray = surface?.optJSONArray("presentModes") ?: JSONArray()
            for (i in 0 until presentArray.length()) presentModes += presentArray.optString(i)
            val presentationQueues = mutableListOf<Pair<Int, Boolean>>()
            val presentationQueueEvidence = mutableListOf<PresentationQueueEvidence>()
            val queueArray = surface?.optJSONArray("queuePresentation") ?: JSONArray()
            for (i in 0 until queueArray.length()) {
                val q = queueArray.optJSONObject(i) ?: continue
                val queueFamily = q.optInt("queueFamily")
                val supported = q.optBoolean("supported")
                presentationQueues += queueFamily to supported
                presentationQueueEvidence += PresentationQueueEvidence(queueFamily, supported, if (q.has("queryResult")) q.optInt("queryResult") else null)
            }
            val surfaceQueueSafetyRejected = surface?.optBoolean("queueQuerySafetyRejected", false) ?: false
            val nestedSurfaceStatus = surface?.optString("queryStatus", "available") ?: "available"
            val nestedSurfaceReason = surface?.optString("queryReason", "") ?: ""
            val surfaceQueryStatus = if (status == "incomplete" && nestedSurfaceStatus == "available") "incomplete" else nestedSurfaceStatus
            val surfaceQueryReason = listOf(reason.takeIf { status == "incomplete" && it.isNotBlank() }, nestedSurfaceReason.takeIf { it.isNotBlank() }).filterNotNull().joinToString(" ").take(1024)
            val surfaceStatusText = when (surfaceQueryStatus) {
                "available" -> "Available"
                "incomplete" -> "Incomplete: partial Surface evidence retained. ${surfaceQueryReason.take(1024)}".trim()
                "not_applicable" -> "Not applicable: ${surfaceQueryReason.take(1024)}".trim()
                "unknown" -> "Unknown: ${surfaceQueryReason.take(1024)}".trim()
                else -> "Unavailable: ${surfaceQueryReason.take(1024)}".trim()
            }
            val surfaceDetails = replaceDetailedProperty(replaceQueryStatus(device.detailedProperties, "Surface probe", surfaceStatusText), "Vulkan Query Safety", "Surface queue-family enumeration safety rejected", surfaceQueueSafetyRejected.toString())
            device.copy(
                surfaceAvailable = surface?.optBoolean("available") == true,
                surfacePresentationSupported = surface?.optBoolean("presentationSupported") == true,
                surfaceColorSpaceExtensionAvailable = root.optBoolean("surfaceColorSpaceExtensionAvailable", false),
                surfaceColorSpaceExtensionStatus = root.optString("surfaceColorSpaceExtensionStatus", if (root.optBoolean("surfaceColorSpaceExtensionAvailable", false)) "available" else "unknown"),
                surfaceColorSpaceExtensionEnabled = root.optBoolean("surfaceColorSpaceExtensionEnabled", false),
                surfaceFormatQueryResult = surface?.optInt("formatQueryResult", -1) ?: -1,
                surfaceFormatQueryResultSecond = surface?.optInt("formatQueryResultSecond", -1) ?: -1,
                surfaceFormatQuerySecondAttempted = surface?.optBoolean("formatQuerySecondAttempted", false) ?: false,
                surfaceFormatQueryAttempted = surface?.optBoolean("formatQueryAttempted", false) ?: false,
                surfaceDependentWsiQueryStatus = surface?.optString("dependentWsiQueryStatus", "unknown") ?: "unknown",
                surfaceFormatQuerySafetyRejected = surface?.optBoolean("formatQuerySafetyRejected", false) ?: false,
                surfaceFormatEnumerationComplete = surface?.optBoolean("formatEnumerationComplete", false) ?: false,
                surfaceFormatQuerySpecAnomaly = surface?.optBoolean("formatQuerySpecAnomaly", false) ?: false,
                surfacePresentModeEnumerationComplete = surface?.optBoolean("presentModeEnumerationComplete", false) ?: false,
                surfacePresentModeQuerySpecAnomaly = surface?.optBoolean("presentModeQuerySpecAnomaly", false) ?: false,
                surfaceCapabilities = capabilities,
                surfaceFormats = surfaceFormats,
                presentModes = presentModes,
                presentationQueues = presentationQueues,
                presentationQueueEvidence = presentationQueueEvidence,
                detailedProperties = surfaceDetails,
                surfaceQueueQuerySafetyRejected = surfaceQueueSafetyRejected,
                surfaceQueryStatus = surfaceQueryStatus,
                surfaceQueryReason = surfaceQueryReason.take(1024)
            )
        }
    })
}


private fun mergeAdvancedQueryReport(base: VulkanReport, raw: String, group: String, label: String): VulkanReport {
    val root = runCatching { JSONObject(raw) }.getOrElse {
        return base.copy(
            devices = base.devices.map { device ->
                device.copy(detailedProperties = replaceQueryStatus(device.detailedProperties, "$label query", "Unavailable: dedicated query returned invalid data."))
            },
            instanceGroupProperties = if (group == "groups") emptyList() else base.instanceGroupProperties,
            instanceGroupStatus = if (group == "groups") "unavailable" else base.instanceGroupStatus,
            instanceGroupReason = if (group == "groups") "The dedicated physical-device-group query returned invalid data." else base.instanceGroupReason,
            instanceGroupEnumerationResult = if (group == "groups") null else base.instanceGroupEnumerationResult,
            instanceGroupEnumerationComplete = if (group == "groups") false else base.instanceGroupEnumerationComplete
        )
    }
    val status = root.optString("status", "unavailable")
    val reason = root.optString("reason", "The dedicated advanced query did not return a result.")
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
    val instanceGroupProperties = if (group == "groups") {
        val values = mutableListOf<PropertyEntry>()
        val groupProperties = root.optJSONArray("groupProperties") ?: JSONArray()
        for (i in 0 until groupProperties.length()) {
            val prop = groupProperties.optJSONObject(i) ?: continue
            values += PropertyEntry(sanitizeReportSection(prop.optString("section")), prop.optString("name"), prop.optString("value"))
        }
        values
    } else {
        emptyList()
    }
    val instanceGroupEnumerationResult = if (group == "groups" && root.has("groupEnumerationResult") && !root.isNull("groupEnumerationResult")) root.optInt("groupEnumerationResult") else null
    val instanceGroupEnumerationComplete = group == "groups" && root.optBoolean("groupEnumerationComplete", false)
    val imageFormatQueryResultsByDevice: Map<Pair<Long, Long>, List<ImageFormatQueryResultEntry>> = if (group == "imageFormat2") {
        val valuesByDevice = mutableMapOf<Pair<Long, Long>, List<ImageFormatQueryResultEntry>>()
        for (i in 0 until resultDevices.length()) {
            val item = resultDevices.optJSONObject(i) ?: continue
            val vendor = item.optLong("vendorId", -1L)
            val deviceId = item.optLong("deviceId", -1L)
            val results = item.optJSONArray("imageFormatQueryResults") ?: JSONArray()
            valuesByDevice[vendor to deviceId] = parseImageFormatQueryResults(results)
        }
        valuesByDevice
    } else {
        emptyMap()
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
                val legacyLinear = parseUnsignedHexLong(Regex("(?:^|, )linear=0x([0-9a-fA-F]+)").find(value)?.groupValues?.getOrNull(1)) ?: return@mapNotNull null
                val legacyOptimal = parseUnsignedHexLong(Regex("(?:^|, )optimal=0x([0-9a-fA-F]+)").find(value)?.groupValues?.getOrNull(1)) ?: return@mapNotNull null
                val legacyBuffer = parseUnsignedHexLong(Regex("(?:^|, )buffer=0x([0-9a-fA-F]+)").find(value)?.groupValues?.getOrNull(1)) ?: return@mapNotNull null
                val featureFlags2Available = Regex("featureFlags2Available=(true|false)").find(value)?.groupValues?.getOrNull(1)?.toBooleanStrictOrNull() == true
                val flags2Linear = parseUnsignedHexLong(Regex("featureFlags2 linear=0x([0-9a-fA-F]+)").find(value)?.groupValues?.getOrNull(1))
                val flags2Optimal = parseUnsignedHexLong(Regex("featureFlags2 linear=0x[0-9a-fA-F]+ optimal=0x([0-9a-fA-F]+)").find(value)?.groupValues?.getOrNull(1))
                val flags2Buffer = parseUnsignedHexLong(Regex("featureFlags2 linear=0x[0-9a-fA-F]+ optimal=0x[0-9a-fA-F]+ buffer=0x([0-9a-fA-F]+)").find(value)?.groupValues?.getOrNull(1))
                val linear = if (featureFlags2Available && flags2Linear != null) flags2Linear else legacyLinear
                val optimal = if (featureFlags2Available && flags2Optimal != null) flags2Optimal else legacyOptimal
                val buffer = if (featureFlags2Available && flags2Buffer != null) flags2Buffer else legacyBuffer
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
    val resultIdentityCounts = (0 until resultDevices.length()).mapNotNull { resultDevices.optJSONObject(it) }.groupingBy { physicalIdentityKey(it.optLong("vendorId", Long.MIN_VALUE), it.optLong("deviceId", Long.MIN_VALUE)) }.eachCount()
    return base.copy(devices = base.devices.map { device ->
        val deviceId = device.deviceIdRaw
        val identityKey = physicalIdentityKey(device.vendorIdRaw, deviceId)
        val identityUnique = basePhysicalIdentityUnique(base, device) && resultIdentityCounts[identityKey] == 1
        val match = if (identityUnique) parsed.singleOrNull { it.first == device.vendorIdRaw && it.second == deviceId } else null
        val videoMatch = if (identityUnique) videoQueuesByDevice.singleOrNull { it.first == device.vendorIdRaw && it.second == deviceId } else null
        val formatMatch = if (identityUnique) formatEntriesByDevice[identityKey] else null
        val imageFormatQueryResultMatch = if (identityUnique) imageFormatQueryResultsByDevice[identityKey] else null
        val hasVideoQueueExtension = device.extensions.any { it.name == "VK_KHR_video_queue" }
        val videoQueueAbsenceKnown = device.deviceExtensionStatus == "available"
        val mergedQueues = device.queues.map { q ->
            when {
                status == "not_applicable" -> q.copy(videoCodecQueryStatus = "not_applicable", videoCodecQueryReason = reason.ifBlank { "The Queue Family Properties2 query does not apply to this device." })
                !hasVideoQueueExtension && videoQueueAbsenceKnown -> q.copy(videoCodecQueryStatus = "not_applicable", videoCodecQueryReason = "Complete device-extension enumeration did not report VK_KHR_video_queue for this device.")
                !hasVideoQueueExtension -> q.copy(videoCodecQueryStatus = "unknown", videoCodecQueryReason = "Device-extension enumeration is incomplete or unavailable, so absence of VK_KHR_video_queue cannot be established.")
                !identityUnique -> q.copy(videoCodecQueryStatus = "unavailable", videoCodecQueryReason = "Isolated queue evidence cannot be attributed safely because the physical-device identity is ambiguous across processes.")
                status != "available" && status != "incomplete" -> q.copy(videoCodecQueryStatus = "unavailable", videoCodecQueryReason = reason.ifBlank { "Queue Family Properties2 query did not complete." })
                videoMatch == null || !videoMatch.third.containsKey(q.index) -> q.copy(videoCodecQueryStatus = "unavailable", videoCodecQueryReason = "VkQueueFamilyVideoPropertiesKHR evidence was not returned for this queue family.")
                status == "incomplete" -> q.copy(videoCodecOperations = videoMatch.third.getValue(q.index), videoCodecQueryStatus = "incomplete", videoCodecQueryReason = reason.ifBlank { "Physical-device enumeration was incomplete; this queue's positive evidence was retained." })
                else -> q.copy(videoCodecOperations = videoMatch.third.getValue(q.index), videoCodecQueryStatus = "available", videoCodecQueryReason = "")
            }
        }
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
        val statusProperty = if (!identityUnique && resultIdentityCounts.containsKey(identityKey)) {
            PropertyEntry("Vulkan Query Status", "$label query", "Unavailable: dedicated-process evidence cannot be attributed safely because vendorId/deviceId is ambiguous across physical devices.")
        } else if (status == "available" && match != null) {
            PropertyEntry("Vulkan Query Status", "$label query", "Available")
        } else if (status == "incomplete") {
            PropertyEntry("Vulkan Query Status", "$label query", "Incomplete: ${reason.ifBlank { "the bounded query returned partial evidence." }}")
        } else if (status == "not_applicable") {
            PropertyEntry("Vulkan Query Status", "$label query", "Not applicable: ${reason.ifBlank { "the query does not apply to this device." }}")
        } else {
            PropertyEntry("Vulkan Query Status", "$label query", "Unavailable: ${reason.ifBlank { "the dedicated query did not complete." }}")
        }
        var merged = device.copy(
            features = mergedFeatures,
            detailedProperties = replaceQueryStatus(mergedProperties, "$label query", statusProperty.value),
            imageFormatQueryResults = if (group == "imageFormat2") { if ((status == "available" || status == "incomplete") && imageFormatQueryResultMatch != null) imageFormatQueryResultMatch else emptyList() } else device.imageFormatQueryResults,
            queues = mergedQueues,
            formats = mergedFormats
        )
        if (group == "core14") {
            val coreStatus = core14Status[device.vendorIdRaw to (deviceId ?: -1L)]
            if (coreStatus != null) {
                merged = merged.copy(vulkan14Status = coreStatus.first, vulkan14Reason = coreStatus.second)
            }
        }
        merged
    }, instanceGroupProperties = if (group == "groups") {
        if (status == "available" || status == "incomplete") instanceGroupProperties else emptyList()
    } else base.instanceGroupProperties,
        instanceGroupStatus = if (group == "groups") status else base.instanceGroupStatus,
        instanceGroupReason = if (group == "groups") reason.take(1024) else base.instanceGroupReason,
        instanceGroupEnumerationResult = if (group == "groups") instanceGroupEnumerationResult else base.instanceGroupEnumerationResult,
        instanceGroupEnumerationComplete = if (group == "groups") instanceGroupEnumerationComplete else base.instanceGroupEnumerationComplete
    )
}

private fun mergeExtensionGroupReport(base: VulkanReport, raw: String, group: String, extensionName: String): VulkanReport {
    val root = runCatching { JSONObject(raw) }.getOrElse {
        return base.copy(devices = base.devices.map { device ->
            if (device.extensions.any { it.name == extensionName }) {
                device.copy(detailedProperties = replaceQueryStatus(device.detailedProperties, "$extensionName query", "Unavailable: the dedicated $group probe returned invalid data."))
            } else device
        })
    }
    val status = root.optString("status", "unavailable")
    val reason = root.optString("reason", "The dedicated extension query did not return a result.")
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
    val parsedIdentityCounts = parsed.groupingBy { physicalIdentityKey(it.first, it.second) }.eachCount()
    return base.copy(devices = base.devices.map { device ->
        if (!device.extensions.any { it.name == extensionName }) device
        else {
            val identityKey = physicalIdentityKey(device.vendorIdRaw, device.deviceIdRaw)
            val identityUnique = basePhysicalIdentityUnique(base, device) && parsedIdentityCounts[identityKey] == 1
            val match = if (identityUnique) parsed.singleOrNull { it.first == device.vendorIdRaw && it.second == device.deviceIdRaw } else null
            when {
                !identityUnique && parsedIdentityCounts.containsKey(identityKey) -> device.copy(
                    detailedProperties = replaceQueryStatus(device.detailedProperties, "$extensionName query", "Unavailable: dedicated-process extension evidence cannot be attributed safely because vendorId/deviceId is ambiguous across physical devices.")
                )
                (status == "available" || status == "incomplete") && match != null -> {
                    val mergedFeatures = device.features + match.third.first.filterNot { incoming -> device.features.any { existing -> existing.name == incoming.name } }
                    val mergedProperties = mergeQueryProperties(device.detailedProperties, match.third.second)
                    device.copy(
                        features = mergedFeatures,
                        detailedProperties = replaceQueryStatus(mergedProperties, "$extensionName query", if (status == "available") "Available" else "Incomplete: ${reason.ifBlank { "physical-device enumeration was incomplete; partial positive evidence was retained." }}")
                    )
                }
                status == "not_applicable" -> device.copy(
                    detailedProperties = replaceQueryStatus(device.detailedProperties, "$extensionName query", "Not applicable: the extension was not enumerated by the dedicated probe.")
                )
                else -> device.copy(
                    detailedProperties = replaceQueryStatus(device.detailedProperties, "$extensionName query", "Unavailable: ${reason.ifBlank { "the dedicated extension query did not complete." }}")
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
        instanceLayers += LayerEntry(layer.optString("name"), layer.optString("description"), layer.optInt("specVersion"), layer.optInt("implementationVersion"), layerExtensions, layer.optString("extensionStatus", "unknown"), layer.optString("extensionReason", ""), layer.optBoolean("extensionsComplete", false))
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
            deviceLayers += LayerEntry(layer.optString("name"), layer.optString("description"), layer.optInt("specVersion"), layer.optInt("implementationVersion"), layerExtensions, layer.optString("extensionStatus", "unknown"), layer.optString("extensionReason", ""), layer.optBoolean("extensionsComplete", false))
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
            val knownQueueFlags = 0x57FL
            queues += QueueEntry(q.optInt("index"), q.optInt("count"), q.optInt("timestampValidBits"), queueFlags, q.optBoolean("graphics"), q.optBoolean("compute"), q.optBoolean("transfer"), q.optBoolean("sparse"), q.optBoolean("protected"), q.optBoolean("videoDecode"), q.optBoolean("videoEncode"), q.optBoolean("opticalFlow"), q.optBoolean("dataGraph"), queueFlags and knownQueueFlags.inv(), q.optString("minImageTransferGranularity", "0 × 0 × 0"), q.optLong("videoCodecOperations"), q.optString("videoCodecQueryStatus", "unknown"), q.optString("videoCodecQueryReason", ""))
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
        val imageFormatQueryResults = parseImageFormatQueryResults(item.optJSONArray("imageFormatQueryResults") ?: JSONArray())
        val queueQuerySafetyRejected = item.optBoolean("queueQuerySafetyRejected", false)
        val memoryHeapSafetyRejected = item.optBoolean("memoryHeapSafetyRejected", false)
        val memoryTypeSafetyRejected = item.optBoolean("memoryTypeSafetyRejected", false)
        detailedProperties += PropertyEntry("Vulkan Query Safety", "Queue-family enumeration safety rejected", queueQuerySafetyRejected.toString())
        detailedProperties += PropertyEntry("Vulkan Query Safety", "Memory-heap count safety rejected", memoryHeapSafetyRejected.toString())
        detailedProperties += PropertyEntry("Vulkan Query Safety", "Memory-type count safety rejected", memoryTypeSafetyRejected.toString())
        val surface = item.optJSONObject("surface")
        val surfaceAvailable = surface?.optBoolean("available") == true
        val surfacePresentationSupported = surface?.optBoolean("presentationSupported") == true
        val surfaceColorSpaceExtensionAvailable = surface?.optBoolean("colorSpaceExtensionAvailable") == true
        val surfaceColorSpaceExtensionEnabled = surface?.optBoolean("colorSpaceExtensionEnabled") == true
        val surfaceFormatQueryResult = surface?.optInt("formatQueryResult", -1) ?: -1
        val surfaceFormatQueryResultSecond = surface?.optInt("formatQueryResultSecond", -1) ?: -1
        val surfaceFormatQuerySecondAttempted = surface?.optBoolean("formatQuerySecondAttempted", false) ?: false
        val surfaceFormatQueryAttempted = surface?.optBoolean("formatQueryAttempted", false) ?: false
        val surfaceDependentWsiQueryStatus = surface?.optString("dependentWsiQueryStatus", "unknown") ?: "unknown"
        val surfaceFormatQuerySafetyRejected = surface?.optBoolean("formatQuerySafetyRejected", false) ?: false
        val surfaceQueueQuerySafetyRejected = surface?.optBoolean("queueQuerySafetyRejected", false) ?: false
        detailedProperties += PropertyEntry("Vulkan Query Safety", "Surface queue-family enumeration safety rejected", surfaceQueueQuerySafetyRejected.toString())
        if (surface != null) {
            listOf("minImageCount", "maxImageCount", "currentExtent", "minExtent", "maxExtent", "maxImageArrayLayers", "supportedTransforms", "currentTransform", "supportedCompositeAlpha", "supportedUsageFlags").forEach { key ->
                if (surface.has(key)) capabilityPairs += key to canonicalSurfaceCapabilityValue(key, surface.optString(key))
            }
            appendSurfaceEnumerationDiagnostics(surface, capabilityPairs)
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
            item.optLong("deviceId"),
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
            imageFormatQueryResults,
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
            vulkan14Reason = item.optString("vulkan14Reason", ""),
            queueQuerySafetyRejected = queueQuerySafetyRejected,
            memoryHeapSafetyRejected = memoryHeapSafetyRejected,
            memoryTypeSafetyRejected = memoryTypeSafetyRejected,
            surfaceQueueQuerySafetyRejected = surfaceQueueQuerySafetyRejected,
            deviceLayerStatus = item.optString("deviceLayerStatus", "unknown"),
            deviceLayerReason = item.optString("deviceLayerReason", "Device-layer enumeration status was not reported by the native collector."),
            deviceLayersComplete = item.optBoolean("deviceLayersComplete", false),
            surfaceQueryStatus = surface?.optString("queryStatus", "unknown") ?: "unknown",
            surfaceQueryReason = surface?.optString("queryReason", "Base probe does not own final live-Surface evidence.") ?: "Base probe does not own final live-Surface evidence.",
            surfaceFormatQueryAttempted = surfaceFormatQueryAttempted,
            surfaceDependentWsiQueryStatus = surfaceDependentWsiQueryStatus
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
            runtimeRegistryTokenReferenceCount = registry.optInt("runtimeRegistryTokenReferenceCount", registry.optInt("runtimeExtensionTokenCount", 0)),
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
        nativeStatus == "incomplete" -> root.optString("reason", "Physical-device enumeration was incomplete; partial positive evidence was retained.")
        nativeStatus == "not_applicable" -> root.optString("reason", "Vulkan device enumeration is not applicable.")
        devices.isEmpty() -> root.optString("reason", "Vulkan instance was created but no physical device was returned.")
        else -> null
    }
    Log.i("VulkanScope", "Vulkan parsed status=$nativeStatus devices=${devices.size} error=${effectiveError ?: "none"}")
    return VulkanReport(
        loaderVersion = root.optString("loaderVersion", "Unknown"),
        instanceExtensions = instanceExtensions,
        instanceLayers = instanceLayers,
        devices = devices,
        error = effectiveError,
        registryCoverage = registryCoverage,
        instanceExtensionStatus = root.optString("instanceExtensionStatus", "unknown"),
        instanceExtensionReason = root.optString("instanceExtensionReason", ""),
        instanceLayerStatus = root.optString("instanceLayerStatus", "unknown"),
        instanceLayerReason = root.optString("instanceLayerReason", ""),
        baseReportComplete = root.optBoolean("baseReportComplete", false),
        physicalDeviceEnumerationResult = if (root.has("physicalDeviceEnumerationResult") && !root.isNull("physicalDeviceEnumerationResult")) root.optInt("physicalDeviceEnumerationResult") else null,
        physicalDeviceEnumerationComplete = root.optBoolean("physicalDeviceEnumerationComplete", false),
        physicalDeviceEnumerationSafetyRejected = root.optBoolean("physicalDeviceEnumerationSafetyRejected", false),
        physicalDeviceEnumerationReason = root.optString("physicalDeviceEnumerationReason", ""),
        instanceApiVersion = root.optString("instanceApiVersion", root.optString("requestedInstanceApiVersion", "Unknown"))
    )
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
    0 -> "VK_PHYSICAL_DEVICE_TYPE_OTHER"
    1 -> "VK_PHYSICAL_DEVICE_TYPE_INTEGRATED_GPU"
    2 -> "VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU"
    3 -> "VK_PHYSICAL_DEVICE_TYPE_VIRTUAL_GPU"
    4 -> "VK_PHYSICAL_DEVICE_TYPE_CPU"
    else -> "UNKNOWN(raw=$type)"
}

@Composable
private fun VulkanScopeApp(
    displayReport: DisplayReport,
    report: VulkanReport?,
    loading: Boolean,
    collectionStatus: CollectionStatus,
    updateStatus: UpdateStatus,
    updateCheckInFlight: Boolean,
    networkBannerState: NetworkBannerState,
    validatedNetworkAvailable: Boolean,
    networkStateKnown: Boolean,
    updateConfirmation: AppUpdate?,
    updateTransferState: UpdateTransferState?,
    updateCancelConfirmationVisible: Boolean,
    onRequestUpdateConfirmation: (AppUpdate) -> Unit,
    onDismissUpdateConfirmation: () -> Unit,
    onConfirmUpdateDownload: (AppUpdate) -> Unit,
    onPauseUpdateDownload: () -> Unit,
    onResumeUpdateDownload: () -> Unit,
    onRequestCancelUpdateDownload: () -> Unit,
    onDismissCancelUpdateDownload: () -> Unit,
    onConfirmCancelUpdateDownload: () -> Unit,
    onInstallDownloadedUpdate: () -> Unit,
    onCloseUpdateTransfer: () -> Unit,
    onCheckForUpdates: () -> Unit,
    directUpdatesEnabled: Boolean,
    directUpdatesConsentVisible: Boolean,
    onDirectUpdatesChanged: (Boolean) -> Unit,
    onDismissDirectUpdatesConsent: () -> Unit,
    onConfirmDirectUpdatesConsent: () -> Unit,
    surfaceReady: (Long, Surface) -> Unit,
    surfaceDestroyed: (Long, Surface) -> Unit,
    surfaceHostGeneration: Long,
    driverMode: DriverMode,
    turnipSupport: TurnipSupport,
    turnipManagerRevision: Int,
    turnipManagerBusy: Boolean,
    storagePermissionDeniedFeedback: Boolean,
    onDriverModeChanged: (DriverMode) -> Unit,
    pendingDriverModeConfirmation: DriverMode?,
    onConfirmDriverModeChange: () -> Unit,
    onDismissDriverModeConfirmation: () -> Unit,
    pendingTurnipActivationSlot: Int?,
    onConfirmTurnipActivation: () -> Unit,
    onDismissTurnipActivation: () -> Unit,
    onInstallDriverBundle: () -> Unit,
    onActivateTurnipDriver: (Int) -> Unit,
    onRemoveTurnipDriver: (Int) -> Unit,
    onPageOpened: (Page) -> Unit,
    onRequestQuery: (String) -> Unit,
    queryTimingMs: Map<String, Long>
) {
    var page by rememberSaveable(
        stateSaver = androidx.compose.runtime.saveable.Saver<Page, String>(
            save = { it.name },
            restore = { saved -> Page.values().find { it.name == saved } ?: Page.Overview }
        )
    ) { mutableStateOf(Page.Overview) }
    var settingsSection by rememberSaveable { mutableStateOf<SettingsSection?>(null) }
    var encyclopediaSeed by rememberSaveable { mutableStateOf("") }
    var selectedDeviceIndex by rememberSaveable { mutableIntStateOf(0) }
    LaunchedEffect(page) { onPageOpened(page) }
    LaunchedEffect(report?.devices?.size) {
        val count = report?.devices?.size ?: 0
        selectedDeviceIndex = if (count == 0) 0 else selectedDeviceIndex.coerceIn(0, count - 1)
    }

    MaterialExpressiveTheme(
        colorScheme = darkColorScheme(
            background = VulkanBlack,
            surface = VulkanSurface,
            surfaceVariant = VulkanSurfaceTonal,
            primary = VulkanAccent,
            onPrimary = VulkanTextPrimary,
            primaryContainer = VulkanAccentContainer,
            onPrimaryContainer = VulkanTextPrimary,
            secondary = VulkanAccentSoft,
            onSecondary = VulkanTextPrimary,
            secondaryContainer = ComposeColor(0xFF2A2022),
            onSecondaryContainer = VulkanTextPrimary,
            tertiary = VulkanAccentSoft,
            onBackground = VulkanTextPrimary,
            onSurface = VulkanTextPrimary,
            outline = VulkanOutline,
            outlineVariant = VulkanOutlineVariant
        ),
        typography = VulkanTypography,
        shapes = VulkanExpressiveShapes,
        motionScheme = MotionScheme.expressive()
    ) {
        BackHandler(enabled = page != Page.Overview || settingsSection != null) {
            if (page == Page.Settings && settingsSection != null) settingsSection = null else page = Page.Overview
        }

        val configuration = androidx.compose.ui.platform.LocalConfiguration.current
        val isTelevision = configuration.uiMode and Configuration.UI_MODE_TYPE_MASK == Configuration.UI_MODE_TYPE_TELEVISION
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val useRail = isLandscape || isTelevision
        CompositionLocalProvider(LocalValidatedNetwork provides validatedNetworkAvailable) {
            Scaffold(
            containerColor = ComposeColor.Black,
            topBar = {
                Column {
                    AppHeader(
                        page,
                        onBack = { if (page == Page.Settings && settingsSection != null) settingsSection = null else page = Page.Overview },
                        onSettings = { settingsSection = null; page = Page.Settings }
                    )
                    CollectionStatusBanner(collectionStatus)
                    ConnectivityStatusHost(collectionStatus, networkStateKnown, validatedNetworkAvailable, networkBannerState)
                    UpdateStatusBanner(updateStatus, onRequestUpdateConfirmation)
                }
            },
            bottomBar = {
                if (!useRail) {
                    ShortNavigationBar(containerColor = ComposeColor(0xFF0A0A0A)) {
                        navigationItems().forEach { item ->
                            var animationTrigger by remember(item.page) { mutableIntStateOf(0) }
                            ShortNavigationBarItem(
                                selected = selectedNavigationPage(page) == item.page,
                                onClick = {
                                    animationTrigger += 1
                                    page = item.page
                                },
                                icon = { AnimatedNavigationIcon(item.page, item.icon, animationTrigger, 24.dp) },
                                label = { Text(trademarkVulkanDisplayText(item.label), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                colors = ShortNavigationBarItemDefaults.colors(
                                    selectedIconColor = VulkanAccentSoft,
                                    selectedTextColorTopIconPosition = VulkanTextPrimary,
                                    selectedTextColorStartIconPosition = VulkanTextPrimary,
                                    selectedIndicatorColor = VulkanAccentContainer,
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
                if (useRail) {
                    CompactNavigationRail(
                        selectedPage = selectedNavigationPage(page),
                        onPageSelected = { page = it },
                        requestInitialFocus = isTelevision
                    )
                }
                Box(Modifier.weight(1f)) {
                    androidx.compose.runtime.key(surfaceHostGeneration) {
                        SurfaceProbe(
                            modifier = Modifier.matchParentSize(),
                            hostGeneration = surfaceHostGeneration,
                            onCreated = surfaceReady,
                            onDestroyed = surfaceDestroyed
                        )
                    }
                    if (loading) LoadingView()
                    else {
                        val current = report
                        if (current == null) EmptyState("No Vulkan® report")
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
                                Column(Modifier.fillMaxSize()) {
                                    if (current.devices.size > 1) {
                                        PhysicalDeviceSelector(current.devices, selectedDeviceIndex) { selectedDeviceIndex = it }
                                    }
                                    Box(Modifier.weight(1f)) {
                                        val evidenceContext = androidx.compose.ui.platform.LocalContext.current
                                        CompositionLocalProvider(
                                            LocalEvidenceActionEnvironment provides EvidenceActionEnvironment(
                                                openEncyclopedia = { term -> encyclopediaSeed = term; page = Page.Encyclopedia },
                                                addWatch = { token ->
                                                    val clean = token.trim().take(256)
                                                    if (clean.isNotBlank()) {
                                                        val prefs = evidenceContext.getSharedPreferences("analysis_tools", Context.MODE_PRIVATE)
                                                        val watched = prefs.getStringSet("watched", emptySet())?.toMutableSet() ?: mutableSetOf()
                                                        if (watched.size < ANALYSIS_MAX_WATCHED || clean in watched) { watched += clean; prefs.edit().putStringSet("watched", watched).apply() }
                                                    }
                                                }
                                            )
                                        ) {
                                            PageContent(
                                                page = targetPage,
                                                report = current,
                                                display = displayReport,
                                                driverMode = driverMode,
                                                turnipSupport = turnipSupport,
                                                turnipManagerRevision = turnipManagerRevision,
                                                turnipManagerBusy = turnipManagerBusy,
                                                storagePermissionDeniedFeedback = storagePermissionDeniedFeedback,
                                                selectedDeviceIndex = selectedDeviceIndex,
                                                onDriverModeChanged = onDriverModeChanged,
                                                onInstallDriverBundle = onInstallDriverBundle,
                                                onActivateTurnipDriver = onActivateTurnipDriver,
                                                onRemoveTurnipDriver = onRemoveTurnipDriver,
                                                onCheckForUpdates = onCheckForUpdates,
                                                updateCheckInFlight = updateCheckInFlight,
                                                directUpdatesEnabled = directUpdatesEnabled,
                                                onDirectUpdatesChanged = onDirectUpdatesChanged,
                                                collectionStatus = collectionStatus,
                                                onNavigate = { target -> if (target == Page.Settings) settingsSection = null; page = target },
                                                settingsSection = settingsSection,
                                                onSettingsSectionChanged = { settingsSection = it },
                                                onRequestQuery = onRequestQuery,
                                                queryTimingMs = queryTimingMs,
                                                encyclopediaSeed = encyclopediaSeed
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            }
        }
        if (directUpdatesConsentVisible) {
            DirectUpdatesConsentDialog(
                appName = "VulkanScope",
                releaseSource = "github.com/EFIShell0/VulkanScope/releases",
                onDismiss = onDismissDirectUpdatesConsent,
                onConfirm = onConfirmDirectUpdatesConsent
            )
        }
        updateConfirmation?.let { update ->
            UpdateConfirmationDialog(
                update = update,
                networkAvailable = validatedNetworkAvailable,
                onDismiss = onDismissUpdateConfirmation,
                onConfirm = { onConfirmUpdateDownload(update) }
            )
        }
        updateTransferState?.let { transfer ->
            UpdateTransferDialog(
                state = transfer,
                networkAvailable = validatedNetworkAvailable,
                onPause = onPauseUpdateDownload,
                onResume = onResumeUpdateDownload,
                onRequestCancel = onRequestCancelUpdateDownload,
                onInstall = onInstallDownloadedUpdate,
                onClose = onCloseUpdateTransfer
            )
        }
        if (updateCancelConfirmationVisible) {
            UpdateCancelConfirmationDialog(
                onResume = onDismissCancelUpdateDownload,
                onConfirmCancel = onConfirmCancelUpdateDownload
            )
        }
        pendingDriverModeConfirmation?.let { target ->
            AlertDialog(
                onDismissRequest = onDismissDriverModeConfirmation,
                title = { QuestionDialogTitle(if (target == DriverMode.SYSTEM) "Switch to System Vulkan® driver?" else "Switch to Turnip driver?") },
                text = {
                    Text(
                        if (target == DriverMode.SYSTEM)
                            "VulkanScope will deactivate the current Turnip source and start a fresh Vulkan collection with Android's system Vulkan driver."
                        else
                            "VulkanScope will deactivate the System source and start a fresh Vulkan collection with the currently active Turnip driver."
                    )
                },
                confirmButton = { ExpressiveContainedTextButton(if (target == DriverMode.SYSTEM) "Use System" else "Use Turnip", onClick = onConfirmDriverModeChange) },
                dismissButton = { ExpressiveCancelButton(onClick = onDismissDriverModeConfirmation) }
            )
        }
        pendingTurnipActivationSlot?.let { slot ->
            AlertDialog(
                onDismissRequest = onDismissTurnipActivation,
                title = { QuestionDialogTitle("Activate Turnip driver?") },
                text = { Text("Slot %02d will become VulkanScope's active Turnip driver. The current driver selection will be deactivated and a fresh Vulkan® collection will start after activation.".format(java.util.Locale.ROOT, slot)) },
                confirmButton = { ExpressiveContainedTextButton("Activate", onClick = onConfirmTurnipActivation) },
                dismissButton = { ExpressiveCancelButton(onClick = onDismissTurnipActivation) }
            )
        }
    }
}

@Composable
private fun SurfaceProbe(
    modifier: Modifier = Modifier,
    hostGeneration: Long,
    onCreated: (Long, Surface) -> Unit,
    onDestroyed: (Long, Surface) -> Unit
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
                    override fun surfaceCreated(holder: SurfaceHolder) { onCreated(hostGeneration, holder.surface) }
                    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
                    override fun surfaceDestroyed(holder: SurfaceHolder) { onDestroyed(hostGeneration, holder.surface) }
                })
            }
        },
        update = {}
    )
}



@Composable
private fun PhysicalDeviceSelector(devices: List<DeviceReport>, selectedIndex: Int, onSelected: (Int) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Physical device", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
        ExpressiveFilterBar(devices.mapIndexed { index, device -> "GPU ${index + 1} · ${device.name.ifBlank { "Unknown" }.take(48)}" }, selectedIndex, onSelected = onSelected)
    }
}

@Composable
private fun VulkanLazyPage(
    verticalSpacing: Dp,
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit
) {
    val listState = rememberLazyListState()
    val navigationPadding = WindowInsets.navigationBars.asPaddingValues()
    Box(modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 18.dp,
                top = navigationPadding.calculateTopPadding(),
                end = 18.dp,
                bottom = navigationPadding.calculateBottomPadding()
            ),
            modifier = Modifier.fillMaxSize().focusGroup(),
            verticalArrangement = Arrangement.spacedBy(verticalSpacing),
            userScrollEnabled = true,
            content = content
        )
        ExpressiveScrollHints(listState, Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 10.dp))
    }
}

@Composable
private fun ScrollBoundaryIndicators(listState: LazyListState, modifier: Modifier = Modifier) {
    val showUp by remember(listState) { derivedStateOf { listState.canScrollBackward } }
    val showDown by remember(listState) { derivedStateOf { listState.canScrollForward } }
    val visible = rememberScrollIndicatorVisibility(listState.isScrollInProgress, showUp || showDown)
    ScrollBoundaryIndicatorColumn(showUp, showDown, visible, modifier)
}

@Composable
private fun ScrollBoundaryIndicators(gridState: LazyGridState, modifier: Modifier = Modifier) {
    val showUp by remember(gridState) { derivedStateOf { gridState.canScrollBackward } }
    val showDown by remember(gridState) { derivedStateOf { gridState.canScrollForward } }
    val visible = rememberScrollIndicatorVisibility(gridState.isScrollInProgress, showUp || showDown)
    ScrollBoundaryIndicatorColumn(showUp, showDown, visible, modifier)
}

@Composable
private fun ScrollBoundaryIndicators(scrollState: ScrollState, modifier: Modifier = Modifier) {
    val showUp by remember(scrollState) { derivedStateOf { scrollState.value > 0 } }
    val showDown by remember(scrollState) { derivedStateOf { scrollState.value < scrollState.maxValue } }
    val visible = rememberScrollIndicatorVisibility(scrollState.isScrollInProgress, showUp || showDown)
    ScrollBoundaryIndicatorColumn(showUp, showDown, visible, modifier)
}

@Composable
private fun ExpressiveScrollHints(listState: LazyListState, modifier: Modifier = Modifier) = ScrollBoundaryIndicators(listState, modifier)

@Composable
private fun ExpressiveScrollHints(gridState: LazyGridState, modifier: Modifier = Modifier) = ScrollBoundaryIndicators(gridState, modifier)

@Composable
private fun ExpressiveScrollHints(scrollState: ScrollState, modifier: Modifier = Modifier) = ScrollBoundaryIndicators(scrollState, modifier)

@Composable
private fun rememberScrollIndicatorVisibility(isScrollInProgress: Boolean, hasScrollableDirection: Boolean): Boolean {
    var visible by remember { mutableStateOf(hasScrollableDirection) }
    LaunchedEffect(isScrollInProgress, hasScrollableDirection) {
        if (!hasScrollableDirection) {
            visible = false
        } else {
            visible = true
            if (!isScrollInProgress) {
                delay(900)
                visible = false
            }
        }
    }
    return visible
}

@Composable
private fun ScrollBoundaryIndicatorColumn(showUp: Boolean, showDown: Boolean, visible: Boolean, modifier: Modifier = Modifier) {
    Box(modifier) {
        AnimatedVisibility(
            visible = visible && showUp,
            modifier = Modifier.align(Alignment.TopCenter),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ScrollBoundaryIndicatorBubble(up = true)
        }
        AnimatedVisibility(
            visible = visible && showDown,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ScrollBoundaryIndicatorBubble(up = false)
        }
    }
}

@Composable
private fun ScrollBoundaryIndicatorBubble(up: Boolean) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = VulkanAccentContainer.copy(alpha = 0.96f),
        tonalElevation = 4.dp,
        shadowElevation = 3.dp
    ) {
        Icon(
            painter = painterResource(if (up) R.drawable.ic_scroll_up else R.drawable.ic_scroll_down),
            contentDescription = null,
            tint = VulkanTextPrimary,
            modifier = Modifier.padding(8.dp).size(24.dp)
        )
    }
}

@Composable
private fun PageContent(
    page: Page,
    report: VulkanReport,
    display: DisplayReport,
    driverMode: DriverMode,
    turnipSupport: TurnipSupport,
    turnipManagerRevision: Int,
    turnipManagerBusy: Boolean,
    storagePermissionDeniedFeedback: Boolean,
    selectedDeviceIndex: Int,
    onDriverModeChanged: (DriverMode) -> Unit,
    onInstallDriverBundle: () -> Unit,
    onActivateTurnipDriver: (Int) -> Unit,
    onRemoveTurnipDriver: (Int) -> Unit,
    onCheckForUpdates: () -> Unit,
    updateCheckInFlight: Boolean,
    directUpdatesEnabled: Boolean,
    onDirectUpdatesChanged: (Boolean) -> Unit,
    collectionStatus: CollectionStatus,
    onNavigate: (Page) -> Unit,
    settingsSection: SettingsSection?,
    onSettingsSectionChanged: (SettingsSection?) -> Unit,
    onRequestQuery: (String) -> Unit,
    queryTimingMs: Map<String, Long>,
    encyclopediaSeed: String
) {
    val device = report.devices.getOrNull(selectedDeviceIndex) ?: report.devices.firstOrNull()
    when (page) {
        Page.Overview -> OverviewPage(report, device, display, driverMode, onNavigate)
        Page.Vulkan -> VulkanPage(report, device, turnipSupport)
        Page.Display -> DisplayPage(display, device)
        Page.Surface -> SurfacePage(device)
        Page.Features -> FeaturesPage(device)
        Page.Memory -> MemoryPage(device)
        Page.Queues -> QueuesPage(device)
        Page.Video -> VulkanVideoPage(device)
        Page.Formats -> FormatsPage(device)
        Page.Properties -> PropertiesPage(device, onRequestQuery)
        Page.Extensions -> ExtensionsPage(report, device)
        Page.Profiles -> ProfilesPage(report, device)
        Page.Encyclopedia -> EncyclopediaPage(encyclopediaSeed)
        Page.Analysis -> AnalysisPage(report, device, display, driverMode, turnipSupport, collectionStatus, queryTimingMs, onDriverModeChanged)
        Page.Settings -> SettingsPage(
            report = report,
            display = display,
            mode = driverMode,
            turnipSupport = turnipSupport,
            turnipManagerRevision = turnipManagerRevision,
            turnipManagerBusy = turnipManagerBusy,
            storagePermissionDeniedFeedback = storagePermissionDeniedFeedback,
            onModeChanged = onDriverModeChanged,
            onInstallDriverBundle = onInstallDriverBundle,
            onActivateTurnipDriver = onActivateTurnipDriver,
            onRemoveTurnipDriver = onRemoveTurnipDriver,
            collectionStatus = collectionStatus,
            directUpdatesEnabled = directUpdatesEnabled,
            onDirectUpdatesChanged = onDirectUpdatesChanged,
            onCheckForUpdates = onCheckForUpdates,
            updateCheckInFlight = updateCheckInFlight,
            selectedSection = settingsSection,
            onSectionSelected = onSettingsSectionChanged
        )
        Page.Info -> SettingsPage(
            report = report,
            display = display,
            mode = driverMode,
            turnipSupport = turnipSupport,
            turnipManagerRevision = turnipManagerRevision,
            turnipManagerBusy = turnipManagerBusy,
            storagePermissionDeniedFeedback = storagePermissionDeniedFeedback,
            onModeChanged = onDriverModeChanged,
            onInstallDriverBundle = onInstallDriverBundle,
            onActivateTurnipDriver = onActivateTurnipDriver,
            onRemoveTurnipDriver = onRemoveTurnipDriver,
            collectionStatus = collectionStatus,
            directUpdatesEnabled = directUpdatesEnabled,
            onDirectUpdatesChanged = onDirectUpdatesChanged,
            onCheckForUpdates = onCheckForUpdates,
            updateCheckInFlight = updateCheckInFlight,
            selectedSection = SettingsSection.INFO,
            onSectionSelected = { }
        )
    }
}



@Composable
private fun OverviewPage(report: VulkanReport, device: DeviceReport?, display: DisplayReport, driverMode: DriverMode, navigate: (Page) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val expandedTextLayout = preferExpandedTextLayout()
    var showSystemDriverDetails by remember { mutableStateOf(false) }
    var showTurnipDriverDetails by remember { mutableStateOf(false) }
    var activeTurnipDriver by remember { mutableStateOf<ManagedTurnipDriver?>(null) }
    val systemDriverSummary = remember(report, driverMode) { if (driverMode == DriverMode.SYSTEM) systemDriverSummaryFromReport(report) else null }
    LaunchedEffect(driverMode) {
        activeTurnipDriver = if (driverMode == DriverMode.TURNIP) {
            withContext(Dispatchers.IO) {
                readManagedTurnipDrivers(context, context.getSharedPreferences("settings", Context.MODE_PRIVATE), driverMode).firstOrNull { it.selected }
            }
        } else null
    }
    if (showSystemDriverDetails && systemDriverSummary != null) {
        SystemDriverDetailsDialog(systemDriverSummary, "Current Vulkan report", true) { showSystemDriverDetails = false }
    }
    if (showTurnipDriverDetails && activeTurnipDriver != null) {
        TurnipDriverDetailsDialog(activeTurnipDriver!!) { showTurnipDriverDetails = false }
    }
    VulkanLazyPage(verticalSpacing = 14.dp) {
        item {
            HeroCard(device, report, driverMode) {
                when {
                    systemDriverSummary != null -> showSystemDriverDetails = true
                    activeTurnipDriver != null -> showTurnipDriverDetails = true
                    else -> navigate(Page.Settings)
                }
            }
        }
        if (report.error != null) {
            item {
                CapabilitySectionCard("Vulkan inspection error") {
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
            if (expandedTextLayout) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard("Vulkan", device?.apiVersion ?: "Unknown", Modifier.fillMaxWidth())
                    MetricCard("Display", display.refreshRate, Modifier.fillMaxWidth())
                }
            } else {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard("Vulkan", device?.apiVersion ?: "Unknown", Modifier.weight(1f))
                    MetricCard("Display", display.refreshRate, Modifier.weight(1f))
                }
            }
        }
        item {
            if (expandedTextLayout) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard("HDR", when (display.hdrCapabilityStatus) { "available" -> "${display.hdrTypes.size} types"; "unknown" -> "Unknown"; else -> "Unavailable" }, Modifier.fillMaxWidth())
                    MetricCard("Wide gamut", when (display.wideGamut) { true -> "Supported"; false -> "Unsupported"; null -> "Unavailable" }, Modifier.fillMaxWidth())
                }
            } else {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard("HDR", when (display.hdrCapabilityStatus) { "available" -> "${display.hdrTypes.size} types"; "unknown" -> "Unknown"; else -> "Unavailable" }, Modifier.weight(1f))
                    MetricCard("Wide gamut", when (display.wideGamut) { true -> "Supported"; false -> "Unsupported"; null -> "Unavailable" }, Modifier.weight(1f))
                }
            }
        }
        item { CapabilitySectionCard("Quick access") {
            val quickAccessItems = listOf(
                "Vulkan" to Page.Vulkan,
                "Surface" to Page.Surface,
                "Display" to Page.Display,
                "HDR & Color" to Page.Display,
                "Extensions" to Page.Extensions,
                "Profiles" to Page.Profiles,
                "Vulkan Video" to Page.Video,
                "More" to Page.Features
            )
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val quickAccessColumns = when {
                    expandedTextLayout || maxWidth < 300.dp -> 1
                    maxWidth < 540.dp -> 2
                    maxWidth < 780.dp -> 3
                    else -> 4
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    quickAccessItems.chunked(quickAccessColumns).forEach { rowItems ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            rowItems.forEach { (title, destination) ->
                                QuickAccessCard(title, destination, navigate, Modifier.weight(1f))
                            }
                            repeat(quickAccessColumns - rowItems.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }
            }
        } }
        item { CapabilitySectionCard("Capability snapshot") {
            CapabilityKeyValue("GPU", device?.name ?: "Unknown")
            CapabilityKeyValue("Driver", device?.driverVersionText ?: device?.driverVersion ?: "Unknown")
            CapabilityKeyValue("Vendor ID", device?.vendorId ?: "Unknown")
            CapabilityKeyValue("Device ID", device?.deviceId ?: "Unknown")
            CapabilityKeyValue("Device type", device?.deviceType ?: "Unknown")
            CapabilityKeyValue("Loader API", report.loaderVersion)
            CapabilityKeyValue("Base probe instance API", report.instanceApiVersion)
            CapabilityKeyValue("Instance extensions", when (report.instanceExtensionStatus) {
                "available" -> report.instanceExtensions.size.toString()
                "incomplete" -> "${report.instanceExtensions.size} retained · Incomplete"
                "unavailable" -> "Unavailable"
                else -> "Unknown"
            })
            CapabilityKeyValue("Instance extension query", report.instanceExtensionStatus)
            CapabilityKeyValue("Device extensions", when (device?.deviceExtensionStatus) {
                "available" -> device.extensions.size.toString()
                "incomplete" -> "${device.extensions.size} retained · Incomplete"
                "unavailable" -> "Unavailable"
                else -> "Unknown"
            })
        } }
        item {
            OverviewDestinationCard(
                title = "Encyclopedia",
                subtitle = "Search Vulkan terms, VK_* symbols, commands, types, extensions and VkResult meanings.",
                destination = Page.Encyclopedia,
                navigate = navigate
            )
        }
        item {
            OverviewDestinationCard(
                title = "Analysis workspace",
                subtitle = "Open local comparison, profile minimums, dependency graph, diagnostics, watched evidence, sharing and optional tests.",
                destination = Page.Analysis,
                navigate = navigate
            )
        }
    }
}



private const val ENCYCLOPEDIA_VISIBLE_RESULT_LIMIT = 24

private data class EncyclopediaReferenceEntry(
    val title: String,
    val category: String,
    val definition: String,
    val detail: String = ""
)

private data class VkResultReferenceEntry(
    val name: String,
    val value: Int,
    val meaning: String,
    val aliases: List<String> = emptyList()
)

private val VULKAN_VK_RESULT_REFERENCE = listOf(
    VkResultReferenceEntry("VK_SUCCESS", 0, "Command successfully completed. This is success for that command only, not global capability evidence."),
    VkResultReferenceEntry("VK_NOT_READY", 1, "A fence or query has not completed yet; the operation can be checked again later."),
    VkResultReferenceEntry("VK_TIMEOUT", 2, "A wait operation did not finish within the requested timeout interval."),
    VkResultReferenceEntry("VK_EVENT_SET", 3, "The queried Vulkan® event is currently signaled."),
    VkResultReferenceEntry("VK_EVENT_RESET", 4, "The queried Vulkan® event is currently unsignaled."),
    VkResultReferenceEntry("VK_INCOMPLETE", 5, "A return array was too small for the complete result. Partial positive data may still be retained, but absence must not be inferred."),
    VkResultReferenceEntry("VK_ERROR_OUT_OF_HOST_MEMORY", -1, "A CPU/host-side memory allocation required by the Vulkan® operation failed."),
    VkResultReferenceEntry("VK_ERROR_OUT_OF_DEVICE_MEMORY", -2, "A device/GPU memory allocation required by the Vulkan® operation failed."),
    VkResultReferenceEntry("VK_ERROR_INITIALIZATION_FAILED", -3, "An object could not be initialized for an implementation-specific reason."),
    VkResultReferenceEntry("VK_ERROR_DEVICE_LOST", -4, "The logical or physical device was lost. Existing work may no longer complete and the device generally has to be recreated."),
    VkResultReferenceEntry("VK_ERROR_MEMORY_MAP_FAILED", -5, "Mapping a Vulkan® memory object into host address space failed."),
    VkResultReferenceEntry("VK_ERROR_LAYER_NOT_PRESENT", -6, "A requested Vulkan® layer is unavailable or could not be loaded."),
    VkResultReferenceEntry("VK_ERROR_EXTENSION_NOT_PRESENT", -7, "A requested Vulkan® extension is not exposed by the implementation for that creation/use path."),
    VkResultReferenceEntry("VK_ERROR_FEATURE_NOT_PRESENT", -8, "A requested Vulkan® feature is not supported for the attempted operation."),
    VkResultReferenceEntry("VK_ERROR_INCOMPATIBLE_DRIVER", -9, "The requested Vulkan® API/version is incompatible with the available driver implementation."),
    VkResultReferenceEntry("VK_ERROR_TOO_MANY_OBJECTS", -10, "The implementation cannot create another object of the requested type because an object-count limit was reached."),
    VkResultReferenceEntry("VK_ERROR_FORMAT_NOT_SUPPORTED", -11, "The exact requested format/use combination is not supported. VulkanScope never expands this into a broader format-wide claim."),
    VkResultReferenceEntry("VK_ERROR_FRAGMENTED_POOL", -12, "A pool allocation failed specifically because the pool's available memory is fragmented."),
    VkResultReferenceEntry("VK_ERROR_UNKNOWN", -13, "An unexpected runtime failure occurred that Vulkan® cannot classify more specifically."),
    VkResultReferenceEntry("VK_ERROR_VALIDATION_FAILED", -1000011001, "Invalid Vulkan® usage was detected by an implementation or validation layer and the command failed.", listOf("VK_ERROR_VALIDATION_FAILED_EXT")),
    VkResultReferenceEntry("VK_ERROR_OUT_OF_POOL_MEMORY", -1000069000, "A pool allocation failed because the pool cannot satisfy the request.", listOf("VK_ERROR_OUT_OF_POOL_MEMORY_KHR")),
    VkResultReferenceEntry("VK_ERROR_INVALID_EXTERNAL_HANDLE", -1000072003, "An imported/exported external handle is not valid for the requested Vulkan® handle type.", listOf("VK_ERROR_INVALID_EXTERNAL_HANDLE_KHR")),
    VkResultReferenceEntry("VK_ERROR_INVALID_OPAQUE_CAPTURE_ADDRESS", -1000257000, "A requested opaque capture/device address or related captured handle information is no longer valid or available.", listOf("VK_ERROR_INVALID_DEVICE_ADDRESS_EXT", "VK_ERROR_INVALID_OPAQUE_CAPTURE_ADDRESS_KHR")),
    VkResultReferenceEntry("VK_ERROR_FRAGMENTATION", -1000161000, "Creation failed because the relevant allocation space is too fragmented.", listOf("VK_ERROR_FRAGMENTATION_EXT")),
    VkResultReferenceEntry("VK_PIPELINE_COMPILE_REQUIRED", 1000297000, "Pipeline creation would require compilation, but the application requested a path that does not perform that compilation.", listOf("VK_PIPELINE_COMPILE_REQUIRED_EXT", "VK_ERROR_PIPELINE_COMPILE_REQUIRED_EXT")),
    VkResultReferenceEntry("VK_ERROR_NOT_PERMITTED", -1000174001, "The implementation denied a privileged request, such as acquiring a higher global queue priority.", listOf("VK_ERROR_NOT_PERMITTED_EXT", "VK_ERROR_NOT_PERMITTED_KHR")),
    VkResultReferenceEntry("VK_ERROR_SURFACE_LOST_KHR", -1000000000, "The presentation surface is no longer available and must be recreated before presentation can continue."),
    VkResultReferenceEntry("VK_ERROR_NATIVE_WINDOW_IN_USE_KHR", -1000000001, "The native window is already in use in a way that prevents Vulkan® from using it for this surface operation."),
    VkResultReferenceEntry("VK_SUBOPTIMAL_KHR", 1000001003, "Presentation can still succeed, but the swapchain no longer matches the surface properties exactly."),
    VkResultReferenceEntry("VK_ERROR_OUT_OF_DATE_KHR", -1000001004, "The surface changed and the swapchain is no longer compatible. Surface properties must be queried again and the swapchain recreated."),
    VkResultReferenceEntry("VK_ERROR_INCOMPATIBLE_DISPLAY_KHR", -1000003001, "The display and swapchain/image presentation configuration are incompatible for the requested operation."),
    VkResultReferenceEntry("VK_ERROR_INVALID_SHADER_NV", -1000012000, "One or more shaders failed the implementation's compile/link validation for this NVIDIA extension path."),
    VkResultReferenceEntry("VK_ERROR_IMAGE_USAGE_NOT_SUPPORTED_KHR", -1000023000, "The requested image usage flags are not supported for the exact Vulkan® Video/image query."),
    VkResultReferenceEntry("VK_ERROR_VIDEO_PICTURE_LAYOUT_NOT_SUPPORTED_KHR", -1000023001, "The requested Vulkan® Video picture layout is not supported."),
    VkResultReferenceEntry("VK_ERROR_VIDEO_PROFILE_OPERATION_NOT_SUPPORTED_KHR", -1000023002, "The video codec operation selected by the exact video profile is not supported."),
    VkResultReferenceEntry("VK_ERROR_VIDEO_PROFILE_FORMAT_NOT_SUPPORTED_KHR", -1000023003, "The format parameters in the exact Vulkan® Video profile chain are not supported."),
    VkResultReferenceEntry("VK_ERROR_VIDEO_PROFILE_CODEC_NOT_SUPPORTED_KHR", -1000023004, "Codec-specific parameters in the exact Vulkan® Video profile chain are not supported."),
    VkResultReferenceEntry("VK_ERROR_VIDEO_STD_VERSION_NOT_SUPPORTED_KHR", -1000023005, "The requested Vulkan® Video standard-header version is not supported."),
    VkResultReferenceEntry("VK_ERROR_INVALID_DRM_FORMAT_MODIFIER_PLANE_LAYOUT_EXT", -1000158000, "The supplied DRM format-modifier plane layout is invalid for the requested image configuration."),
    VkResultReferenceEntry("VK_ERROR_PRESENT_TIMING_QUEUE_FULL_EXT", -1000208000, "The swapchain's present-timing results queue has insufficient space for another requested timing record."),
    VkResultReferenceEntry("VK_ERROR_FULL_SCREEN_EXCLUSIVE_MODE_LOST_EXT", -1000255000, "An operation requiring application-controlled exclusive full-screen access failed because that exclusivity was lost."),
    VkResultReferenceEntry("VK_THREAD_IDLE_KHR", 1000268000, "A deferred operation is unfinished, but this thread currently has no work available to perform."),
    VkResultReferenceEntry("VK_THREAD_DONE_KHR", 1000268001, "A deferred operation is unfinished, but there is no more work to assign to additional threads."),
    VkResultReferenceEntry("VK_OPERATION_DEFERRED_KHR", 1000268002, "A deferred-host operation was requested and at least part of its work was actually deferred."),
    VkResultReferenceEntry("VK_OPERATION_NOT_DEFERRED_KHR", 1000268003, "A deferred-host operation was requested, but no part of the work was deferred."),
    VkResultReferenceEntry("VK_ERROR_INVALID_VIDEO_STD_PARAMETERS_KHR", -1000299000, "Video-standard parameters are syntactically/semantically invalid or exceed the codec/implementation capabilities for that exact request."),
    VkResultReferenceEntry("VK_ERROR_COMPRESSION_EXHAUSTED_EXT", -1000338000, "Image creation with requested fixed-rate compression failed because the implementation's compression resources are exhausted."),
    VkResultReferenceEntry("VK_INCOMPATIBLE_SHADER_BINARY_EXT", 1000482000, "The supplied binary shader code is not compatible with this device.", listOf("VK_ERROR_INCOMPATIBLE_SHADER_BINARY_EXT")),
    VkResultReferenceEntry("VK_PIPELINE_BINARY_MISSING_KHR", 1000483000, "A requested pipeline binary was not found in the implementation's internal cache."),
    VkResultReferenceEntry("VK_ERROR_NOT_ENOUGH_SPACE_KHR", -1000483000, "The caller did not provide enough output space for all required returned data.")
)

private val VULKAN_ENCYCLOPEDIA_CORE = listOf(
    EncyclopediaReferenceEntry("Supported", "Evidence state", "Positive runtime evidence confirms the exact reported capability or queried combination.", "Support is scoped to the exact query; it is never inferred from a GPU marketing name."),
    EncyclopediaReferenceEntry("Unsupported", "Evidence state", "Explicit runtime evidence proves the exact capability or queried combination is not supported.", "A negative result for one tuple/profile does not automatically make a broader feature or format unsupported."),
    EncyclopediaReferenceEntry("Unavailable", "Evidence state", "The query could not produce usable capability evidence.", "Unavailable is not proof of unsupported hardware; failures, timeouts, incomplete prerequisites or inaccessible paths can produce it."),
    EncyclopediaReferenceEntry("Not applicable", "Evidence state", "A prerequisite, API scope or extension is not exposed, so that query does not apply to the selected runtime path.", "For example, Vulkan® Video being Not applicable does not prove hardware video decode/encode is absent; it describes only the Vulkan® Video API path of the selected Vulkan® driver."),
    EncyclopediaReferenceEntry("Unknown", "Evidence state", "There is not enough authoritative evidence to classify the capability.", "Unknown is intentionally distinct from Unsupported and from Not applicable."),
    EncyclopediaReferenceEntry("Vulkan® instance", "Core concept", "VkInstance is the application-level connection to a Vulkan® implementation.", "Instance extensions/layers and the instance API version define what can be requested before selecting a physical device."),
    EncyclopediaReferenceEntry("Physical device", "Core concept", "VkPhysicalDevice represents a Vulkan®-capable implementation/device discovered from an instance.", "Features, properties, limits, memory types, queues, formats and extension enumeration are primarily queried from this object."),
    EncyclopediaReferenceEntry("Logical device", "Core concept", "VkDevice is the application-created logical interface to one selected physical device.", "Enabled device features/extensions and queues become usable through this object; enumeration alone does not enable them."),
    EncyclopediaReferenceEntry("Queue", "Core concept", "VkQueue is an execution/presentation queue obtained from a logical device.", "Queue-family flags describe what kinds of work can be submitted; presentation support is Surface-specific evidence."),
    EncyclopediaReferenceEntry("Command buffer", "Core concept", "VkCommandBuffer records Vulkan® commands for later submission to a queue.", "Most vkCmd* calls record work; they do not execute GPU work at the moment the function is called."),
    EncyclopediaReferenceEntry("Feature", "Capability class", "A Vulkan® feature is usually a Boolean capability that must be queried and, when required, enabled before use.", "Feature structs often participate in extensible pNext chains."),
    EncyclopediaReferenceEntry("Property", "Capability class", "A property is implementation information such as supported modes, identifiers, alignments or behavioral characteristics.", "Properties describe the implementation; they are not normally enabled like Boolean features."),
    EncyclopediaReferenceEntry("Limit", "Capability class", "A limit is a numeric boundary such as a maximum size/count or required alignment.", "The comparison direction matters: some requirements need at least a value, while others require not exceeding a maximum."),
    EncyclopediaReferenceEntry("Format", "Capability class", "VkFormat identifies an image/buffer element format.", "Format support depends on tiling, usage and feature flags; a single failed tuple must not be generalized into universal format unsupported status."),
    EncyclopediaReferenceEntry("Layer", "API concept", "A Vulkan® layer can intercept Vulkan® calls to provide validation, tooling or other behavior.", "Layer presence is separate from device capability support."),
    EncyclopediaReferenceEntry("Extension", "API concept", "An extension adds Vulkan® API functionality outside a particular core-version baseline.", "Being registered by Khronos is reference metadata; runtime support requires authoritative enumeration/query evidence."),
    EncyclopediaReferenceEntry("Surface / WSI", "Presentation", "VkSurfaceKHR represents a platform presentation target; WSI means Window System Integration.", "Surface formats, present modes, capabilities and presentation-queue support depend on the live Surface and can change with it."),
    EncyclopediaReferenceEntry("Swapchain", "Presentation", "VkSwapchainKHR manages presentable images associated with a Surface.", "VK_SUBOPTIMAL_KHR can remain usable, while VK_ERROR_OUT_OF_DATE_KHR requires re-query/recreation before successful presentation can continue."),
    EncyclopediaReferenceEntry("pNext", "Extensibility", "pNext links extensible Vulkan® structures into input/output chains.", "Each chained structure must use the correct sType; VulkanScope uses validated chains rather than guessing unsupported structures."),
    EncyclopediaReferenceEntry("sType", "Extensibility", "sType identifies the concrete Vulkan® structure type to the implementation.", "It is normally set to the corresponding VK_STRUCTURE_TYPE_* enumerant before the structure is passed to Vulkan®."),
    EncyclopediaReferenceEntry("Loader API version", "Versioning", "The highest Vulkan® API version reported by the Vulkan® loader entry-point path.", "It is not the same as a physical device's API version or the driver version."),
    EncyclopediaReferenceEntry("Device API version", "Versioning", "The Vulkan® core API version exposed by a particular VkPhysicalDevice.", "Core feature/property query eligibility follows this runtime device version plus relevant extension exposure."),
    EncyclopediaReferenceEntry("Driver version", "Versioning", "A vendor/driver-specific version value associated with the selected physical device.", "Its encoding can be vendor-specific and must not be confused with Vulkan® core API version."),
    EncyclopediaReferenceEntry("vk* command", "Naming", "Vulkan® commands use a lowercase vk prefix followed by an action/object name, for example vkCreateInstance or vkGetPhysicalDeviceProperties2.", "The command's returned VkResult, if any, reports that operation's status rather than a global device verdict."),
    EncyclopediaReferenceEntry("vkCmd* command", "Naming", "vkCmd* commands record operations into a VkCommandBuffer.", "Recording generally defers execution until the command buffer is submitted to an appropriate queue."),
    EncyclopediaReferenceEntry("vkQueue* command", "Naming", "vkQueue* commands act directly on a VkQueue, such as submitting work or presenting.", "Queue-family capabilities and any Surface-specific presentation evidence still constrain valid use."),
    EncyclopediaReferenceEntry("Vk* type", "Naming", "Vk* names are Vulkan® types: handles, structures, enums, bitmasks, aliases and related API types.", "Structure names do not themselves prove that a runtime supports the feature represented by that structure."),
    EncyclopediaReferenceEntry("VK_* token", "Naming", "VK_* names are Vulkan® constants, enumerants, bit flags, result codes, structure-type identifiers, extension-name macros and other registered tokens.", "A _BIT token is normally a bitflag value; vendor/KHR/EXT suffixes identify the namespace that introduced the token."),
    EncyclopediaReferenceEntry("VK_STRUCTURE_TYPE_*", "Naming", "A VK_STRUCTURE_TYPE_* enumerant is the sType discriminator for a Vulkan® structure.", "It lets the implementation identify the concrete structure in ordinary arguments and pNext chains."),
    EncyclopediaReferenceEntry("*_BIT", "Naming", "Names ending in _BIT (before any vendor suffix) are bitflag enumerants intended to be combined in compatible flag fields.", "A zero-valued *_NONE name means no bits are selected rather than a capability bit."),
    EncyclopediaReferenceEntry("KHR / EXT / vendor suffix", "Naming", "KHR identifies Khronos extensions, EXT identifies multi-vendor extensions, while tags such as AMD, NV, QCOM, ARM or INTEL identify vendor namespaces.", "A suffix records API namespace/provenance; it is not runtime support evidence.")
)

private val VULKAN_COMMON_COMMAND_MEANINGS = mapOf(
    "vkEnumerateInstanceVersion" to "Reports the highest Vulkan® core API version supported by the loader path.",
    "vkEnumerateInstanceExtensionProperties" to "Enumerates instance extensions exposed by the loader/implementation for the requested layer scope.",
    "vkCreateInstance" to "Creates a VkInstance using the requested application API version, layers and instance extensions.",
    "vkEnumeratePhysicalDevices" to "Enumerates VkPhysicalDevice handles available to a Vulkan® instance.",
    "vkEnumerateDeviceExtensionProperties" to "Enumerates device extensions exposed by a selected physical device.",
    "vkGetPhysicalDeviceFeatures2" to "Queries core and pNext-chained physical-device feature structures without enabling them.",
    "vkGetPhysicalDeviceProperties2" to "Queries core and pNext-chained physical-device properties/limits/identifiers.",
    "vkGetPhysicalDeviceMemoryProperties2" to "Queries memory heaps, memory types and pNext extension memory properties.",
    "vkGetPhysicalDeviceQueueFamilyProperties2" to "Queries queue-family counts, flags and pNext queue-family properties.",
    "vkGetPhysicalDeviceFormatProperties2" to "Queries format feature support, including extensible 64-bit format-feature data when exposed.",
    "vkGetPhysicalDeviceImageFormatProperties2" to "Tests an exact image type/format/tiling/usage/flags tuple and returns image-format properties when supported.",
    "vkGetPhysicalDeviceSurfaceSupportKHR" to "Tests whether one queue family can present to one concrete VkSurfaceKHR.",
    "vkGetPhysicalDeviceSurfaceCapabilitiesKHR" to "Queries presentation capabilities for a concrete Surface.",
    "vkGetPhysicalDeviceSurfaceFormatsKHR" to "Enumerates format/color-space pairs supported for a concrete Surface.",
    "vkGetPhysicalDeviceSurfacePresentModesKHR" to "Enumerates present modes supported for a concrete Surface.",
    "vkGetPhysicalDeviceVideoCapabilitiesKHR" to "Queries capabilities for one exact Vulkan® Video profile chain; success must not be generalized to every codec/profile combination.",
    "vkGetPhysicalDeviceVideoFormatPropertiesKHR" to "Enumerates video format properties for a specified Vulkan® Video profile/usage query."
)

private fun humanizeVulkanSymbolTail(name: String): String = name
    .removePrefix("vkCmd")
    .removePrefix("vkQueue")
    .removePrefix("vk")
    .replace(Regex("([a-z0-9])([A-Z])"), "$1 $2")
    .trim()
    .ifBlank { name }

private fun commandReferenceDefinition(ref: VulkanRegistrySymbolReference): EncyclopediaReferenceEntry {
    val special = VULKAN_COMMON_COMMAND_MEANINGS[ref.name]
    val generic = when {
        ref.name.startsWith("vkCmd") -> "Records the ${humanizeVulkanSymbolTail(ref.name)} operation into a command buffer; recording does not itself execute the GPU work."
        ref.name.startsWith("vkQueue") -> "Performs the ${humanizeVulkanSymbolTail(ref.name)} operation directly at queue scope."
        ref.name.startsWith("vkCreate") -> "Creates or initializes the ${humanizeVulkanSymbolTail(ref.name)} Vulkan® object/resource path."
        ref.name.startsWith("vkDestroy") -> "Destroys/releases the ${humanizeVulkanSymbolTail(ref.name)} Vulkan® object path owned by the application."
        ref.name.startsWith("vkEnumerate") -> "Enumerates ${humanizeVulkanSymbolTail(ref.name)} data exposed by the implementation."
        ref.name.startsWith("vkGet") -> "Queries or retrieves ${humanizeVulkanSymbolTail(ref.name)} information from Vulkan®."
        ref.name.startsWith("vkAllocate") -> "Allocates ${humanizeVulkanSymbolTail(ref.name)} resources through Vulkan®."
        ref.name.startsWith("vkFree") -> "Releases previously allocated ${humanizeVulkanSymbolTail(ref.name)} resources."
        ref.name.startsWith("vkBind") -> "Binds/associates ${humanizeVulkanSymbolTail(ref.name)} resources as defined by the command contract."
        ref.name.startsWith("vkMap") -> "Maps ${humanizeVulkanSymbolTail(ref.name)} memory/data into an application-accessible path."
        ref.name.startsWith("vkUnmap") -> "Ends the mapping represented by ${humanizeVulkanSymbolTail(ref.name)}."
        ref.name.startsWith("vkWait") -> "Waits for the synchronization/state represented by ${humanizeVulkanSymbolTail(ref.name)}."
        ref.name.startsWith("vkSet") -> "Sets or updates ${humanizeVulkanSymbolTail(ref.name)} state."
        ref.name.startsWith("vkReset") -> "Resets ${humanizeVulkanSymbolTail(ref.name)} state/resources to the command-defined state."
        else -> "Registered Vulkan® command for ${humanizeVulkanSymbolTail(ref.name)}. Exact parameters and valid usage are defined by its authoritative command specification."
    }
    return EncyclopediaReferenceEntry(ref.name, "Registry command", special ?: generic, "Provider/reference: ${ref.providers}. Registry presence is not runtime support evidence.")
}

private fun tokenReferenceDefinition(ref: VulkanRegistrySymbolReference): EncyclopediaReferenceEntry {
    VULKAN_VK_RESULT_REFERENCE.firstOrNull { it.name == ref.name || ref.name in it.aliases }?.let { result ->
        val aliasNote = if (ref.name == result.name) "" else " Alias of ${result.name}."
        return EncyclopediaReferenceEntry(ref.name, "VkResult token", "${result.meaning}$aliasNote", "Numeric value: ${result.value}. Provider/reference: ${ref.providers}.")
    }
    val meaning = when {
        ref.name.endsWith("_EXTENSION_NAME") -> "String macro containing the canonical extension name used for extension enumeration/enabling."
        ref.name.endsWith("_SPEC_VERSION") -> "Integer macro identifying the registry revision of the extension interface."
        ref.name.startsWith("VK_STRUCTURE_TYPE_") -> "VkStructureType enumerant used in a structure's sType field to identify that concrete structure."
        Regex("_BIT(?:_[A-Z0-9]+)?$").containsMatchIn(ref.name) -> "Bitflag enumerant used as one selectable bit in its owning Vulkan® flag type."
        ref.name.startsWith("VK_FORMAT_") -> "Vulkan® format-related enumerant/token. Its exact channel/layout or format-feature meaning is encoded by its owning enum and symbol name."
        ref.name.startsWith("VK_OBJECT_TYPE_") -> "VkObjectType enumerant identifying a Vulkan® object/handle category."
        ref.name.startsWith("VK_DESCRIPTOR_TYPE_") -> "VkDescriptorType enumerant identifying a descriptor resource/category."
        ref.name.startsWith("VK_IMAGE_LAYOUT_") -> "VkImageLayout enumerant describing an image subresource layout/state for synchronization and access rules."
        ref.name.startsWith("VK_PIPELINE_STAGE_") || ref.name.startsWith("VK_ACCESS_") -> "Synchronization-related pipeline-stage/access token used to describe execution or memory dependency scope."
        ref.name.startsWith("VK_QUEUE_") -> "Queue-related Vulkan® enumerant/flag describing queue capabilities, priority or queue behavior."
        ref.name.startsWith("VK_VIDEO_") -> "Vulkan® Video enumerant/flag used by video profile, capability, session or coding-operation structures."
        else -> "Registered VK_* Vulkan® token/enumerant in the locked registry. Its exact semantic domain is given by its owning enum/type."
    }
    return EncyclopediaReferenceEntry(ref.name, "Registry token · ${ref.owner}", meaning, "Provider/reference: ${ref.providers}. Token registration is not runtime support evidence.")
}

private fun typeReferenceDefinition(ref: VulkanRegistrySymbolReference): EncyclopediaReferenceEntry {
    val special = when (ref.name) {
        "VkInstance" -> "Handle for the application-level Vulkan® instance."
        "VkPhysicalDevice" -> "Handle representing a Vulkan®-capable physical-device implementation discovered from an instance."
        "VkDevice" -> "Handle for an application-created logical device."
        "VkQueue" -> "Handle for a logical-device execution/presentation queue."
        "VkCommandBuffer" -> "Handle for recorded Vulkan® command sequences submitted to a queue."
        "VkPhysicalDeviceFeatures2" -> "Extensible feature-query structure whose pNext chain can carry additional feature structures."
        "VkPhysicalDeviceProperties2" -> "Extensible property-query structure whose pNext chain can carry additional property structures."
        "VkResult" -> "Enumeration type used by Vulkan® commands to return success/status and runtime error codes."
        else -> null
    }
    val generic = when (ref.owner.lowercase()) {
        "struct" -> "Vulkan® structure type. Its fields are command inputs/outputs; extensible structures commonly use sType and pNext."
        "handle" -> "Vulkan® object handle type used to refer to an implementation/application-owned Vulkan® object."
        "enum" -> "Vulkan® enumeration type containing named VK_* enumerants."
        "bitmask" -> "Vulkan® flag/bitmask type used to combine compatible *_BIT enumerants."
        "basetype" -> "Vulkan® base scalar/integer type used by API structures and commands."
        "funcpointer" -> "Vulkan® function-pointer type used for callbacks or dynamically obtained API functions."
        else -> "Registered Vulkan® ${ref.owner} type in the locked registry."
    }
    return EncyclopediaReferenceEntry(ref.name, "Registry type · ${ref.owner}", special ?: generic, "Provider/reference: ${ref.providers}. Type registration is not runtime support evidence.")
}

private fun extensionReferenceDefinition(ref: VulkanExtensionReference): EncyclopediaReferenceEntry {
    val interfaceImpact = buildList {
        if (ref.commands.isNotEmpty()) add("${ref.commands.size} related command(s)")
        if (ref.enums.isNotEmpty()) add("${ref.enums.size} related token(s)")
        if (ref.queryGroup.isNotBlank()) add("VulkanScope query group ${ref.queryGroup}")
    }.joinToString(" · ").ifBlank { "registry metadata only" }
    val lifecycle = buildList {
        if (ref.promotedTo.isNotBlank()) add("promoted to ${ref.promotedTo}")
        if (ref.deprecatedBy.isNotBlank()) add("deprecated by ${ref.deprecatedBy}")
        if (ref.obsoletedBy.isNotBlank()) add("obsoleted by ${ref.obsoletedBy}")
        if (ref.provisional) add("provisional/beta")
    }.joinToString(" · ")
    val scope = listOf(ref.type.ifBlank { "unspecified scope" }, ref.platform.takeIf { it.isNotBlank() }?.let { "platform $it" }).filterNotNull().joinToString(" · ")
    val definition = "Registered $scope extension. Interface impact in the locked registry: $interfaceImpact."
    val detail = buildString {
        append("Revision ${ref.specVersion.ifBlank { "unspecified" }}")
        if (ref.depends.isNotBlank()) append(" · depends: ${ref.depends}")
        if (ref.requires.isNotBlank()) append(" · requires: ${ref.requires}")
        if (lifecycle.isNotBlank()) append(" · $lifecycle")
        append(". Registry registration is reference metadata, not proof that the selected runtime enumerates or supports it.")
    }
    return EncyclopediaReferenceEntry(ref.name, "Extension reference", definition, detail)
}

private fun encyclopediaMatches(entry: EncyclopediaReferenceEntry, query: String): Boolean =
    query.isBlank() || listOf(entry.title, entry.category, entry.definition, entry.detail).any { it.contains(query, ignoreCase = true) }

private fun encyclopediaVkResultEntries(query: String): List<EncyclopediaReferenceEntry> = VULKAN_VK_RESULT_REFERENCE
    .asSequence()
    .filter { result -> query.isBlank() || result.name.contains(query, true) || result.aliases.any { it.contains(query, true) } || result.meaning.contains(query, true) || result.value.toString() == query }
    .map { result ->
        EncyclopediaReferenceEntry(
            result.name,
            "VkResult · ${if (result.value >= 0) "success/status" else "runtime error"}",
            result.meaning,
            buildString {
                append("Numeric value: ${result.value}")
                if (result.aliases.isNotEmpty()) append(" · aliases: ${result.aliases.joinToString(", ")}")
                append(". VkResult describes that command outcome; it is not global capability evidence.")
            }
        )
    }
    .toList()

private fun encyclopediaExtensionEntries(query: String, limit: Int): List<EncyclopediaReferenceEntry> {
    if (query.isBlank()) return VULKAN_EXTENSION_REFERENCE.values.asSequence().sortedBy { it.name }.take(limit).map(::extensionReferenceDefinition).toList()
    return VULKAN_EXTENSION_REFERENCE.values.asSequence()
        .filter { ref ->
            ref.name.contains(query, true) || ref.author.contains(query, true) || ref.promotedTo.contains(query, true) ||
                ref.depends.contains(query, true) || ref.requires.contains(query, true) || ref.commands.any { it.contains(query, true) } || ref.enums.any { it.contains(query, true) }
        }
        .sortedWith(compareBy<VulkanExtensionReference> { if (it.name.equals(query, true)) 0 else if (it.name.startsWith(query, true)) 1 else 2 }.thenBy { it.name })
        .take(limit)
        .map(::extensionReferenceDefinition)
        .toList()
}

private fun encyclopediaRegistryEntries(kind: VulkanRegistrySymbolKind, query: String, limit: Int): List<EncyclopediaReferenceEntry> {
    if (query.trim().length < 2) return emptyList()
    return searchVulkanRegistrySymbols(kind, query, limit).map { ref ->
        when (kind) {
            VulkanRegistrySymbolKind.COMMAND -> commandReferenceDefinition(ref)
            VulkanRegistrySymbolKind.TOKEN -> tokenReferenceDefinition(ref)
            VulkanRegistrySymbolKind.TYPE -> typeReferenceDefinition(ref)
        }
    }
}

private fun encyclopediaSearch(category: String, rawQuery: String): List<EncyclopediaReferenceEntry> {
    val query = rawQuery.trim()
    val limit = ENCYCLOPEDIA_VISIBLE_RESULT_LIMIT
    return when (category) {
        "VkResult" -> encyclopediaVkResultEntries(query)
        "Commands" -> encyclopediaRegistryEntries(VulkanRegistrySymbolKind.COMMAND, query, limit)
        "VK_*" -> encyclopediaRegistryEntries(VulkanRegistrySymbolKind.TOKEN, query, limit)
        "Types" -> encyclopediaRegistryEntries(VulkanRegistrySymbolKind.TYPE, query, limit)
        "Extensions" -> encyclopediaExtensionEntries(query, limit)
        else -> {
            if (query.isBlank()) VULKAN_ENCYCLOPEDIA_CORE
            else buildList {
                addAll(VULKAN_ENCYCLOPEDIA_CORE.filter { encyclopediaMatches(it, query) })
                addAll(encyclopediaVkResultEntries(query))
                if (size < limit) addAll(encyclopediaExtensionEntries(query, limit - size))
                if (size < limit) addAll(encyclopediaRegistryEntries(VulkanRegistrySymbolKind.COMMAND, query, limit - size))
                if (size < limit) addAll(encyclopediaRegistryEntries(VulkanRegistrySymbolKind.TOKEN, query, limit - size))
                if (size < limit) addAll(encyclopediaRegistryEntries(VulkanRegistrySymbolKind.TYPE, query, limit - size))
            }.distinctBy { it.title }.take(limit)
        }
    }
}

@Composable
private fun EncyclopediaPage(initialQuery: String = "") {
    var query by rememberSaveable { mutableStateOf(initialQuery) }
    LaunchedEffect(initialQuery) { if (initialQuery.isNotBlank()) query = initialQuery }
    var category by rememberSaveable { mutableStateOf("All") }
    val entries = remember(query, category) { encyclopediaSearch(category, query) }
    VulkanLazyPage(verticalSpacing = 12.dp) {
        item {
            CapabilitySectionCard("Encyclopedia") {
                Text(
                    "Offline Vulkan reference built from the locked Vulkan 1.4.362 registry plus curated VulkanScope interpretation rules. Search exact symbols such as VK_SUCCESS, vkGetPhysicalDeviceFeatures2, VkPhysicalDeviceProperties2 or VK_KHR_swapchain.",
                    color = VulkanTextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
                CapabilityKeyValue("Registry symbol census", "$VULKAN_COMMAND_SYMBOL_COUNT vk* commands · $VULKAN_TOKEN_SYMBOL_COUNT VK_* tokens · $VULKAN_TYPE_SYMBOL_COUNT Vk* types · 476 registered extensions")
                Text("Runtime evidence and registry/reference symbols are separate evidence classes.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                ExpressiveSearchField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    placeholderText = "Search VK_SUCCESS, vkCreateInstance, VkFormat, VK_KHR_swapchain…"
                )
                val categories = listOf("All", "VkResult", "Commands", "VK_*", "Types", "Extensions")
                ExpressiveFilterBar(categories, categories.indexOf(category).coerceAtLeast(0)) { category = categories[it] }
                Text(
                    when {
                        (category == "Commands" || category == "VK_*" || category == "Types") && query.trim().length < 2 -> "Type at least 2 characters to search this large registry symbol family. Results are capped at $ENCYCLOPEDIA_VISIBLE_RESULT_LIMIT; refine the query for a specific symbol."
                        category == "Extensions" && query.isBlank() -> "Showing the first $ENCYCLOPEDIA_VISIBLE_RESULT_LIMIT registered extensions. Search by exact extension, command, token, dependency or promotion to narrow the local reference."
                        category == "VkResult" && query.isBlank() -> "${VULKAN_VK_RESULT_REFERENCE.size} canonical VkResult values from the Vulkan 1.4.362 reference are listed below; compatibility aliases are shown with their canonical result."
                        category == "All" && query.isBlank() -> "Core concepts, naming rules and VulkanScope evidence semantics are shown below. Search to resolve registry symbols."
                        entries.size >= ENCYCLOPEDIA_VISIBLE_RESULT_LIMIT -> "Showing the first $ENCYCLOPEDIA_VISIBLE_RESULT_LIMIT matches. Refine the query for a narrower result."
                        else -> "${entries.size} matching reference entr${if (entries.size == 1) "y" else "ies"}."
                    },
                    color = VulkanTextMuted,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    "Vulkan Video marked Not applicable describes the Vulkan Video API path exposed by the selected driver; it does not prove hardware video decode/encode is absent. Registry registration, a symbol name or an extension reference never substitutes for runtime evidence.",
                    color = VulkanTextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        if (entries.isEmpty()) {
            item {
                CapabilityItemCard {
                    Text(
                        if (query.trim().length < 2 && category in setOf("Commands", "VK_*", "Types")) "Search input is intentionally bounded before the large symbol index is touched." else "No matching local Vulkan reference entry.",
                        color = VulkanTextSecondary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(entries, key = { "${it.category}:${it.title}" }) { entry ->
                CapabilityItemCard {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(trademarkVulkanDisplayText(entry.title), color = VulkanTextPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text(trademarkVulkanDisplayText(entry.category), color = VulkanAccentSoft, style = MaterialTheme.typography.labelSmall)
                        Text(trademarkVulkanDisplayText(entry.definition), color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                        if (entry.detail.isNotBlank()) Text(trademarkVulkanDisplayText(entry.detail), color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalysisPage(report: VulkanReport, device: DeviceReport?, display: DisplayReport, driverMode: DriverMode, turnipSupport: TurnipSupport, collectionStatus: CollectionStatus, queryTimingMs: Map<String, Long>, onDriverModeChanged: (DriverMode) -> Unit) {
    var storageAction by remember { mutableStateOf<AnalysisStorageAction?>(null) }
    val analysisModel = rememberAnalysisWorkspaceModel(report, device, display, driverMode, turnipSupport, collectionStatus, queryTimingMs, onDriverModeChanged) { storageAction = it }
    VulkanLazyPage(verticalSpacing = 12.dp) {
        analysisWorkspaceItems(analysisModel, report, device)
    }
    storageAction?.let { action ->
        val request = when (action) {
            AnalysisStorageAction.IMPORT_SNAPSHOT -> SharedStorageBrowserRequest(
                title = "Import analysis snapshot",
                description = "Choose a bounded VulkanScope analysis JSON snapshot from shared storage.",
                mode = SharedStorageBrowserMode.IMPORT,
                allowedExtensions = setOf("json"),
                maxImportBytes = ANALYSIS_MAX_SNAPSHOT_BYTES.toLong()
            )
            AnalysisStorageAction.EXPORT_SNAPSHOT -> SharedStorageBrowserRequest(
                title = "Export analysis snapshot",
                description = "Choose a shared-storage folder and file name for the validated analysis snapshot.",
                mode = SharedStorageBrowserMode.EXPORT,
                allowedExtensions = setOf("json"),
                suggestedFileName = "VulkanScope-${safeFilePart(device?.name ?: "Unknown-GPU")}-analysis.json"
            )
            AnalysisStorageAction.IMPORT_MINIMUM_PROFILE -> SharedStorageBrowserRequest(
                title = "Import minimum profile",
                description = "Choose a VulkanScope minimum-profile JSON file. Schema and rule bounds are validated before use.",
                mode = SharedStorageBrowserMode.IMPORT,
                allowedExtensions = setOf("json"),
                maxImportBytes = 256L * 1024L
            )
            AnalysisStorageAction.EXPORT_MINIMUM_PROFILE -> SharedStorageBrowserRequest(
                title = "Export minimum profile",
                description = "Choose a shared-storage folder for the bounded VulkanScope minimum-profile JSON.",
                mode = SharedStorageBrowserMode.EXPORT,
                allowedExtensions = setOf("json"),
                suggestedFileName = "VulkanScope-${safeFilePart(analysisModel.state.customProfileName.ifBlank { "profile" })}-minimum.json"
            )
            AnalysisStorageAction.EXPORT_TECHNICAL_REPORT -> SharedStorageBrowserRequest(
                title = "Export technicalReport JSON",
                description = "Choose a shared-storage folder for the exact bounded schema-v3 technicalReport JSON used by Database submission.",
                mode = SharedStorageBrowserMode.EXPORT,
                allowedExtensions = setOf("json"),
                suggestedFileName = "VulkanScope-${safeFilePart(device?.name ?: "Unknown-GPU")}-technicalReport.json"
            )
        }
        SharedStorageBrowserDialog(
            request = request,
            onDismiss = { storageAction = null },
            onImport = { file ->
                when (action) {
                    AnalysisStorageAction.IMPORT_SNAPSHOT -> analysisModel.importSnapshotFile(file)
                    AnalysisStorageAction.IMPORT_MINIMUM_PROFILE -> analysisModel.importMinimumProfileFile(file)
                    else -> Result.failure(IllegalStateException("This action does not import files"))
                }
            },
            onExport = { file ->
                when (action) {
                    AnalysisStorageAction.EXPORT_SNAPSHOT -> analysisModel.exportSnapshotFile(file)
                    AnalysisStorageAction.EXPORT_MINIMUM_PROFILE -> analysisModel.exportMinimumProfileFile(file)
                    AnalysisStorageAction.EXPORT_TECHNICAL_REPORT -> analysisModel.exportRawTechnicalReportFile(file)
                    else -> Result.failure(IllegalStateException("This action does not export files"))
                }
            }
        )
    }
    analysisModel.state.selectedEvidence?.let { selected ->
        EvidenceInspectorDialog(selected.first, selected.second, onDismiss = { analysisModel.state.selectedEvidence = null }, onWatch = analysisModel.addWatched)
    }
    analysisModel.state.pendingHistoryDelete?.let { record ->
        AlertDialog(
            onDismissRequest = { analysisModel.state.pendingHistoryDelete = null },
            title = { QuestionDialogTitle("Delete analysis history snapshot?") },
            text = { Text("This deletes the selected bounded local analysis snapshot from VulkanScope private storage. The action cannot be undone.") },
            confirmButton = {
                ExpressiveContainedIconTextButton("Yes", R.drawable.ic_delete) {
                    analysisModel.state.pendingHistoryDelete = null
                    analysisModel.deleteHistory(record)
                }
            },
            dismissButton = { ExpressiveCancelButton { analysisModel.state.pendingHistoryDelete = null } }
        )
    }
    if (analysisModel.state.pendingHistoryDeleteAll) {
        AlertDialog(
            onDismissRequest = { analysisModel.state.pendingHistoryDeleteAll = false },
            title = { QuestionDialogTitle("Delete all analysis history?") },
            text = { Text("Delete all ${analysisModel.state.history.size} retained local analysis snapshots? This does not change current Vulkan® capability evidence.") },
            confirmButton = {
                ExpressiveContainedIconTextButton("Delete all", R.drawable.ic_clear_all, fontWeight = FontWeight.Bold) {
                    analysisModel.state.pendingHistoryDeleteAll = false
                    analysisModel.deleteAllHistory()
                }
            },
            dismissButton = { ExpressiveCloseButton { analysisModel.state.pendingHistoryDeleteAll = false } }
        )
    }
    analysisModel.state.pendingWatchDelete?.let { token ->
        AlertDialog(
            onDismissRequest = { analysisModel.state.pendingWatchDelete = null },
            title = { QuestionDialogTitle("Remove watched evidence?") },
            text = { Text("Remove $token from the local watched-evidence list?") },
            confirmButton = {
                ExpressiveContainedIconTextButton("Delete", R.drawable.ic_delete, fontWeight = FontWeight.Bold) {
                    analysisModel.state.pendingWatchDelete = null
                    analysisModel.persistWatched(analysisModel.state.watched - token)
                }
            },
            dismissButton = { ExpressiveCloseButton { analysisModel.state.pendingWatchDelete = null } }
        )
    }
    if (analysisModel.state.pendingWatchDeleteAll) {
        AlertDialog(
            onDismissRequest = { analysisModel.state.pendingWatchDeleteAll = false },
            title = { QuestionDialogTitle("Delete all watched evidence?") },
            text = { Text("Delete all ${analysisModel.state.watched.size} entries from the local watched-evidence list? This does not change Vulkan® capability evidence.") },
            confirmButton = {
                ExpressiveContainedIconTextButton("Delete all", R.drawable.ic_clear_all, fontWeight = FontWeight.Bold) {
                    analysisModel.state.pendingWatchDeleteAll = false
                    analysisModel.persistWatched(emptySet())
                }
            },
            dismissButton = { ExpressiveCloseButton { analysisModel.state.pendingWatchDeleteAll = false } }
        )
    }
}



@Composable
private fun VulkanPage(report: VulkanReport, device: DeviceReport?, turnipSupport: TurnipSupport) {
    VulkanLazyPage(verticalSpacing = 14.dp) {
        item { CapabilitySectionCard("Vulkan API") {
            CapabilityKeyValue("Loader API", report.loaderVersion)
            CapabilityKeyValue("Base probe instance API", report.instanceApiVersion)
            CapabilityKeyValue("Device API", device?.apiVersion ?: "Unknown")
            CapabilityKeyValue("Driver", device?.driverVersionText ?: device?.driverVersion ?: "Unknown")
            CapabilityKeyValue("Device type", device?.deviceType ?: "Unknown")
            CapabilityKeyValue("Vendor ID", device?.vendorId ?: "Unknown")
            CapabilityKeyValue("Device ID", device?.deviceId ?: "Unknown")
            CapabilityKeyValue("Physical-device enumeration result", report.physicalDeviceEnumerationResult?.let(::vkResultText) ?: "Not returned")
            CapabilityKeyValue("Physical-device enumeration complete", report.physicalDeviceEnumerationComplete.toString())
            CapabilityKeyValue("Physical-device enumeration safety rejection", if (report.physicalDeviceEnumerationSafetyRejected) "YES · local bound rejected the result" else "NO")
            if (report.physicalDeviceEnumerationReason.isNotBlank()) CapabilityKeyValue("Physical-device enumeration provenance", report.physicalDeviceEnumerationReason)
        } }
        item { CapabilitySectionCard("Physical device groups") {
            CapabilityKeyValue("Query status", report.instanceGroupStatus)
            if (report.instanceGroupReason.isNotBlank()) CapabilityKeyValue("Reason", report.instanceGroupReason)
            CapabilityKeyValue("Enumeration result", report.instanceGroupEnumerationResult?.let(::vkResultText) ?: "Unknown")
            CapabilityKeyValue("Enumeration complete", report.instanceGroupEnumerationComplete.toString())
            if (report.instanceGroupProperties.isEmpty()) {
                Text(if (report.instanceGroupEnumerationComplete) "No physical device groups were enumerated." else "No physical-device-group property payload is currently available.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
            } else {
                report.instanceGroupProperties.forEach { property -> CapabilityKeyValue(property.name, property.value) }
            }
        } }
        item { CapabilitySectionCard("Instance layers") {
            CapabilityKeyValue("Enumeration status", report.instanceLayerStatus)
            if (report.instanceLayerReason.isNotBlank()) CapabilityKeyValue("Enumeration reason", report.instanceLayerReason)
            if (report.instanceLayers.isEmpty()) {
                when (report.instanceLayerStatus) {
                    "available" -> {
                        Text("No instance layers are exposed by the active Vulkan® implementation.")
                        Text("This is normal on many Android production/driver configurations; validation layers are optional and are not bundled by VulkanScope.", color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.bodySmall)
                    }
                    "incomplete" -> Text("Instance-layer enumeration is incomplete; an empty list is not treated as proof that no layers exist.")
                    "unavailable" -> Text("Instance-layer enumeration is unavailable.")
                    else -> Text("Instance-layer enumeration state is unknown.")
                }
            }
            report.instanceLayers.forEach { layer ->
                CapabilityItemCard(containerColor = VulkanSurfaceTonal) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(layer.name, fontWeight = FontWeight.SemiBold)
                        Text("spec ${layer.specVersion} · implementation ${layer.implementationVersion}", color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall)
                        if (layer.description.isNotBlank()) Text(layer.description, color = VulkanTextMuted, style = MaterialTheme.typography.bodySmall)
                        Text("Extension enumeration: ${layer.extensionStatus}${if (layer.extensionsComplete) " · complete" else " · incomplete"}", color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall)
                        if (layer.extensionReason.isNotBlank()) Text(layer.extensionReason, color = VulkanTextMuted, style = MaterialTheme.typography.bodySmall)
                        layer.extensions.forEach { ext -> Text("${ext.name} · spec ${ext.specVersion}", color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall) }
                    }
                }
            }
        } }
        item { CapabilitySectionCard("Device layers") {
            CapabilityKeyValue("Enumeration status", device?.deviceLayerStatus ?: "unknown")
            CapabilityKeyValue("Enumeration complete", device?.deviceLayersComplete?.toString() ?: "false")
            if (!device?.deviceLayerReason.isNullOrBlank()) CapabilityKeyValue("Enumeration reason", device?.deviceLayerReason ?: "")
            if (device?.deviceLayers.isNullOrEmpty()) {
                when (device?.deviceLayerStatus) {
                    "available" -> {
                        Text("No device layers are exposed.")
                        Text("Device layers are legacy functionality; modern Vulkan® uses instance layers.", color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.bodySmall)
                    }
                    "incomplete" -> Text("Device-layer enumeration is incomplete; an empty list is not proof that no device layers exist.")
                    "unavailable" -> Text("Device-layer enumeration is unavailable.")
                    else -> Text("Device-layer enumeration state is unknown.")
                }
            }
            device?.deviceLayers?.forEach { layer ->
                CapabilityItemCard(containerColor = VulkanSurfaceTonal) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(layer.name, fontWeight = FontWeight.SemiBold)
                        Text("spec ${layer.specVersion} · implementation ${layer.implementationVersion}", color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall)
                        if (layer.description.isNotBlank()) Text(layer.description, color = VulkanTextMuted, style = MaterialTheme.typography.bodySmall)
                        Text("Extension enumeration: ${layer.extensionStatus}${if (layer.extensionsComplete) " · complete" else " · incomplete"}", color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall)
                        if (layer.extensionReason.isNotBlank()) Text(layer.extensionReason, color = VulkanTextMuted, style = MaterialTheme.typography.bodySmall)
                        layer.extensions.forEach { ext -> Text("${ext.name} · spec ${ext.specVersion}", color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall) }
                    }
                }
            }
        } }
        item { CapabilitySectionCard("Instance extensions") {
            CapabilityKeyValue("Enumeration status", report.instanceExtensionStatus)
            if (report.instanceExtensionReason.isNotBlank()) CapabilityKeyValue("Enumeration reason", report.instanceExtensionReason)
            if (report.instanceExtensions.isEmpty()) {
                when (report.instanceExtensionStatus) {
                    "available" -> EmptyState("No instance extensions exposed")
                    "incomplete" -> EmptyState("Instance-extension enumeration incomplete")
                    "unavailable" -> EmptyState("Instance-extension enumeration unavailable")
                    else -> EmptyState("Instance-extension enumeration unknown")
                }
            }
            report.instanceExtensions.sortedBy { it.name }.forEach { ext -> CapabilityKeyValue(ext.name, "spec ${ext.specVersion}") }
        } }
        item { CapabilitySectionCard("Operating system") {
            CapabilityKeyValue("Architecture", Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown")
            CapabilityKeyValue("Version", Build.VERSION.RELEASE)
            CapabilityKeyValue("Codename", Build.VERSION.CODENAME)
            CapabilityKeyValue("SDK", Build.VERSION.SDK_INT.toString())
            CapabilityKeyValue("Build ID", Build.ID)
            CapabilityKeyValue("Build incremental", Build.VERSION.INCREMENTAL)
            CapabilityKeyValue("Security patch", Build.VERSION.SECURITY_PATCH)
            CapabilityKeyValue("Brand", Build.BRAND)
            CapabilityKeyValue("Manufacturer", Build.MANUFACTURER)
            CapabilityKeyValue("Product", Build.PRODUCT)
            CapabilityKeyValue("Device", Build.DEVICE)
            CapabilityKeyValue("Board", Build.BOARD)
            CapabilityKeyValue("Hardware", Build.HARDWARE)
            CapabilityKeyValue("Fingerprint", Build.FINGERPRINT)
        } }
        item { CapabilitySectionCard("Android runtime") {
            CapabilityKeyValue("Architecture", Build.SUPPORTED_ABIS.joinToString(", "))
            CapabilityKeyValue("Manufacturer", Build.MANUFACTURER)
            CapabilityKeyValue("Model", Build.MODEL)
            CapabilityKeyValue("Android", Build.VERSION.RELEASE)
            CapabilityKeyValue("SDK", Build.VERSION.SDK_INT.toString())
            CapabilityKeyValue("Build ID", Build.ID)
            CapabilityKeyValue("Build incremental", Build.VERSION.INCREMENTAL)
            CapabilityKeyValue("Turnip eligibility", when (turnipSupport) {
                TurnipSupport.SUPPORTED -> "Eligible: arm64-v8a + Qualcomm Adreno Vulkan evidence detected"
                TurnipSupport.UNSUPPORTED -> "Not eligible: current platform/Vulkan® evidence does not satisfy the Turnip gate"
                TurnipSupport.UNKNOWN -> "Unknown: waiting for complete platform/Vulkan® evidence"
            })
        } }
        item { CapabilitySectionCard("Feature coverage") {
            val supported = device?.features?.count { it.supported } ?: 0
            val total = device?.features?.size ?: 0
            CapabilityKeyValue("Queried feature fields", "$supported / $total supported")
            CapabilityKeyValue("Instance extensions", when (report.instanceExtensionStatus) {
                "available" -> report.instanceExtensions.size.toString()
                "incomplete" -> "${report.instanceExtensions.size} retained · Incomplete"
                "unavailable" -> "Unavailable"
                else -> "Unknown"
            })
            CapabilityKeyValue("Instance extension query", report.instanceExtensionStatus)
            CapabilityKeyValue("Device extensions", when (device?.deviceExtensionStatus) {
                "available" -> device.extensions.size.toString()
                "incomplete" -> "${device.extensions.size} retained · Incomplete"
                "unavailable" -> "Unavailable"
                else -> "Unknown"
            })
        } }
    }
}



@Composable
private fun HeroCard(device: DeviceReport?, report: VulkanReport, driverMode: DriverMode, onDriverDetails: () -> Unit) {
    Surface(color = VulkanSurfaceRaised, shape = MaterialTheme.shapes.extraLargeIncreased, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                VendorLogo(device?.vendorIdRaw, Modifier.size(82.dp))
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(device?.name ?: if (report.error != null) "Vulkan® unavailable" else "Vulkan® device unavailable", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(if (device != null) vendorInfo(device.vendorIdRaw).name else report.error?.take(120) ?: "Unknown vendor", color = ComposeColor(0xFFBDBDBD), maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(
                        driverMode.label,
                        color = if (driverMode == DriverMode.TURNIP) VulkanAccentSoft else VulkanTextPrimary,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (device != null) {
                CapabilityKeyValue("Vendor ID", device.vendorId)
                Text("GPU name, vendor ID and device ID are read from VkPhysicalDeviceProperties returned by the active Vulkan® implementation; the vendor label is only a presentation mapping of the numeric vendorID.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    DetailAffordance(onDriverDetails)
                }
            }
        }
    }
}



@Composable
private fun ExpressiveDestinationCard(title: String, subtitle: String, icon: Int, onClick: () -> Unit) {
    val shape = MaterialTheme.shapes.large
    Surface(
        color = ComposeColor(0xFF181516),
        shape = shape,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(shape = RoundedCornerShape(18.dp), color = ComposeColor(0xFF351719)) {
                Icon(painterResource(icon), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.padding(10.dp).size(21.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(trademarkVulkanDisplayText(title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = VulkanTextPrimary)
                Text(trademarkVulkanDisplayText(subtitle), style = MaterialTheme.typography.bodySmall, color = VulkanTextSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.iconButtonColors(containerColor = ComposeColor(0xFF291719), contentColor = VulkanAccentSoft)
            ) {
                Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = "Open ${trademarkVulkanDisplayText(title)}", tint = VulkanAccentSoft, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun OverviewDestinationCard(title: String, subtitle: String, destination: Page, navigate: (Page) -> Unit) {
    ExpressiveDestinationCard(title, subtitle, pageIcon(destination)) { navigate(destination) }
}

@Composable
private fun QuickAccessCard(title: String, destination: Page, navigate: (Page) -> Unit, modifier: Modifier) {
    Card(
        onClick = { navigate(destination) },
        colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFF1A1718)),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.heightIn(min = 72.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 9.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
        ) {
            if (title == "HDR & Color") {
                DisplaySectionBadgeIcon("HDR")
            } else {
                Icon(
                    painterResource(pageIcon(destination)),
                    contentDescription = null,
                    modifier = Modifier.size(19.dp),
                    tint = ComposeColor(0xFFE2676A)
                )
            }
            Text(
                title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}



@Composable
private fun CompactNavigationRail(selectedPage: Page, onPageSelected: (Page) -> Unit, requestInitialFocus: Boolean) {
    val firstFocusRequester = remember { FocusRequester() }
    val expandedTextLayout = preferExpandedTextLayout()
    LaunchedEffect(requestInitialFocus) { if (requestInitialFocus) firstFocusRequester.requestFocus() }
    Surface(
        modifier = Modifier.width(if (expandedTextLayout) 104.dp else 80.dp),
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
                var animationTrigger by remember(item.page) { mutableIntStateOf(0) }
                val bringIntoViewRequester = remember { BringIntoViewRequester() }
                val scope = rememberCoroutineScope()
                var focused by remember { mutableStateOf(false) }
                val shape = RoundedCornerShape(18.dp)
                Card(
                    onClick = {
                        animationTrigger += 1
                        onPageSelected(item.page)
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) VulkanAccentContainer else if (focused) ComposeColor(0xFF2A1517) else ComposeColor.Transparent
                    ),
                    shape = shape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 54.dp)
                        .then(if (index == 0) Modifier.focusRequester(firstFocusRequester) else Modifier)
                        .bringIntoViewRequester(bringIntoViewRequester)
                        .onFocusChanged { state ->
                            focused = state.isFocused
                            if (state.isFocused) scope.launch { bringIntoViewRequester.bringIntoView() }
                        }
                        .border(if (focused) 2.dp else 0.dp, if (focused) ComposeColor(0xFFE2676A) else ComposeColor.Transparent, shape)
                ) {
                    Column(
                        Modifier.fillMaxWidth().padding(horizontal = 2.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically)
                    ) {
                        AnimatedNavigationIcon(
                            page = item.page,
                            icon = item.icon,
                            trigger = animationTrigger,
                            size = 21.dp,
                            tint = if (selected) VulkanAccentSoft else ComposeColor(0xFFB8B8B8)
                        )
                        Text(
                            trademarkVulkanDisplayText(item.label),
                            color = if (selected) VulkanTextPrimary else ComposeColor(0xFFB8B8B8),
                            fontSize = if (expandedTextLayout) 11.sp else 9.sp,
                            lineHeight = if (expandedTextLayout) 13.sp else 10.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                            maxLines = if (expandedTextLayout) 2 else 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun ExploreDestinationTile(page: Page, onNavigate: (Page) -> Unit, modifier: Modifier = Modifier) {
    val shape = MaterialTheme.shapes.medium
    Card(
        onClick = { onNavigate(page) },
        colors = CardDefaults.cardColors(containerColor = VulkanSurfaceTonal),
        shape = shape,
        modifier = modifier.heightIn(min = 52.dp).then(tvBrowseModifier(shape))
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 11.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Surface(shape = MaterialTheme.shapes.small, color = VulkanAccentContainer) {
                Icon(painterResource(pageIcon(page)), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.padding(7.dp).size(18.dp))
            }
            Text(trademarkVulkanDisplayText(page.title), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = VulkanTextPrimary, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun ExploreCard(onNavigate: (Page) -> Unit) {
    val expandedTextLayout = preferExpandedTextLayout()
    val pages = listOf(Page.Features, Page.Memory, Page.Queues, Page.Video, Page.Formats, Page.Properties)
    CapabilitySectionCard("Explore") {
        Text("Detailed Vulkan® inspection areas", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val columns = when {
                expandedTextLayout || maxWidth < 300.dp -> 1
                maxWidth < 620.dp -> 2
                else -> 3
            }
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                pages.chunked(columns).forEach { rowPages ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        rowPages.forEach { page -> ExploreDestinationTile(page, onNavigate, Modifier.weight(1f)) }
                        repeat(columns - rowPages.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppHeader(page: Page, onBack: () -> Unit, onSettings: () -> Unit) {
    val expandedTextLayout = preferExpandedTextLayout()
    TopAppBar(
        navigationIcon = {
            if (page != Page.Overview) {
                ExpressiveIconButton(R.drawable.ic_back, "Back", onBack)
            }
        },
        title = {
            if (expandedTextLayout) {
                Text(
                    "VulkanScope · ${trademarkVulkanDisplayText(page.title)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = VulkanTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.semantics { heading() }
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Image(
                        painter = painterResource(R.drawable.vulkanscope_logo_horizontal),
                        contentDescription = "VulkanScope",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.width(148.dp).height(28.dp)
                    )
                    Text(trademarkVulkanDisplayText(page.title), style = MaterialTheme.typography.labelMedium, color = ComposeColor(0xFF9E9E9E), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.semantics { heading() })
                }
            }
        },
        actions = {
            if (page != Page.Settings) {
                ExpressiveIconButton(R.drawable.ic_settings, "Settings", onSettings)
            }
        },
        colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(containerColor = ComposeColor.Black)
    )
}

@Composable
private fun DisplayPage(display: DisplayReport, device: DeviceReport?) {
    VulkanLazyPage(verticalSpacing = 14.dp) {
        item { CapabilitySectionCard("Display") {
            CapabilityKeyValue("Physical mode", display.resolution)
            CapabilityKeyValue("Current refresh", display.refreshRate)
            CapabilityKeyValue("Wide color gamut", when (display.wideGamut) { true -> "SUPPORTED"; false -> "UNSUPPORTED"; null -> "UNAVAILABLE" })
            CapabilityKeyValue("Preferred wide-gamut color space", display.preferredWideGamut)
        } }
        item { CapabilitySectionCard("HDR capabilities") {
            if (display.hdrTypes.isEmpty()) {
                CapabilityKeyValue("HDR types", when (display.hdrCapabilityStatus) {
                    "available" -> "None reported"
                    "unknown" -> "Unknown"
                    else -> "Unavailable"
                })
            } else {
                Text(
                    "The official logos below represent HDR types detected by Android on this device. HLG and HLG+ are shown as text because no official logo is defined by the authoritative standards sources used by VulkanScope.",
                    color = VulkanTextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
                HdrCapabilitiesCarousel(display.hdrTypes)
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp), color = ComposeColor(0xFF303030))
            CapabilityKeyValue("Minimum luminance", display.minLuminance)
            CapabilityKeyValue("Maximum luminance", display.maxLuminance)
            CapabilityKeyValue("Maximum average luminance", display.averageLuminance)
        } }
        item { CapabilitySectionCard("Supported display modes") {
            if (display.modes.isEmpty()) EmptyState("Display mode list unavailable")
            display.modes.forEachIndexed { index, mode -> CapabilityKeyValue("Mode ${index + 1}", mode) }
        } }
        item { CapabilitySectionCard("Display ↔ Vulkan interpretation") {
            CapabilityKeyValue("Android wide gamut", when (display.wideGamut) { true -> "Supported"; false -> "Unsupported"; null -> "Unavailable" })
            CapabilityKeyValue("Vulkan® surface query", device?.surfaceQueryStatus?.uppercase() ?: "UNKNOWN")
            if (!device?.surfaceQueryReason.isNullOrBlank()) CapabilityKeyValue("Surface query reason", device?.surfaceQueryReason ?: "")
            CapabilityKeyValue("Vulkan® surface data", when (device?.surfaceQueryStatus) {
                "available" -> "Available from VkSurfaceKHR"
                "incomplete" -> "Incomplete: partial Surface evidence retained"
                "not_applicable" -> "Not applicable"
                "unavailable" -> "Unavailable"
                else -> "Unknown"
            })
            Text("A Vulkan® color-space capability is not treated as a measurement of the panel's physical gamut.", color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.bodySmall)
        } }
    }
}


@Composable
private fun HdrCapabilitiesCarousel(types: List<String>) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val canMoveLeft = scrollState.value > 0
    val canMoveRight = scrollState.value < scrollState.maxValue
    Box(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().horizontalScroll(scrollState), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            types.forEach { HdrTypeCard(it) }
        }
        AnimatedVisibility(visible = canMoveLeft, modifier = Modifier.align(Alignment.CenterStart), enter = fadeIn(), exit = fadeOut()) {
            IconButton(
                onClick = { scope.launch { scrollState.animateScrollTo((scrollState.value - 360).coerceAtLeast(0)) } },
                colors = IconButtonDefaults.iconButtonColors(containerColor = VulkanAccentContainer, contentColor = VulkanTextPrimary),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(painterResource(R.drawable.ic_chevron_left), contentDescription = "Scroll HDR capabilities left", modifier = Modifier.size(23.dp))
            }
        }
        AnimatedVisibility(visible = canMoveRight, modifier = Modifier.align(Alignment.CenterEnd), enter = fadeIn(), exit = fadeOut()) {
            IconButton(
                onClick = { scope.launch { scrollState.animateScrollTo((scrollState.value + 360).coerceAtMost(scrollState.maxValue)) } },
                colors = IconButtonDefaults.iconButtonColors(containerColor = VulkanAccentContainer, contentColor = VulkanTextPrimary),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = "Scroll HDR capabilities right", modifier = Modifier.size(23.dp))
            }
        }
    }
}

@Composable
private fun HdrTypeCard(type: String) {
    val normalized = type.trim().lowercase(java.util.Locale.ROOT)
    val logo = when (normalized) {
        "dolby vision" -> R.drawable.hdr_dolby_vision
        "dolby vision 2" -> R.drawable.hdr_dolby_vision_2
        "hdr10" -> R.drawable.hdr_hdr10
        "hdr10+" -> R.drawable.hdr_hdr10_plus
        "hdr10+ advanced" -> R.drawable.hdr_hdr10_plus_advanced
        "hdr vivid" -> R.drawable.hdr_vivid
        else -> null
    }
    val whiteCard = normalized == "hdr10"
    val shape = RoundedCornerShape(18.dp)
    Surface(
        shape = shape,
        color = if (whiteCard) ComposeColor.White else ComposeColor(0xFF111111),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (whiteCard) ComposeColor(0xFFE0E0E0) else ComposeColor(0xFF2B2B2B)),
        modifier = Modifier.then(tvBrowseModifier(shape))
    ) {
        if (logo != null) {
            Image(
                painter = painterResource(logo),
                contentDescription = type,
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(154.dp).height(62.dp).padding(horizontal = 13.dp, vertical = 10.dp)
            )
        } else {
            Text(
                type,
                color = if (whiteCard) ComposeColor.Black else ComposeColor.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
private fun SurfacePage(device: DeviceReport?) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf(SupportFilter.ALL) }
    val entries = remember(device) {
        val formats = device?.surfaceFormats ?: emptyList()
        val completeEnumeration = device?.surfaceFormatEnumerationComplete == true && !device.surfaceFormatQuerySpecAnomaly
        if (completeEnumeration) buildSurfaceCatalog(formats) else formats
    }
    val filtered = remember(query, filter, entries) {
        entries.filter { entry ->
            val statusOk = when (filter) { SupportFilter.ALL -> true; SupportFilter.SUPPORTED -> entry.supported; SupportFilter.UNSUPPORTED -> !entry.supported }
            statusOk && (query.isBlank() || entry.format.contains(query, true) || entry.colorSpace.contains(query, true) || entry.classification.contains(query, true) || entry.description.contains(query, true))
        }
    }
    VulkanLazyPage(verticalSpacing = 12.dp) {
        item { CapabilitySectionCard("VkSurfaceKHR") {
            CapabilityKeyValue("Status", device?.surfaceQueryStatus?.replace('_', ' ')?.uppercase() ?: "UNKNOWN")
            if (!device?.surfaceQueryReason.isNullOrBlank()) CapabilityKeyValue("Reason", device?.surfaceQueryReason ?: "")
            CapabilityKeyValue("Presentation support", when {
                device == null -> "Unavailable"
                device.surfacePresentationSupported -> "Supported"
                device.surfaceQueryStatus == "available" -> "Not supported by this Vulkan device"
                device.surfaceQueryStatus == "not_applicable" -> "Not applicable"
                device.surfaceQueryStatus == "incomplete" -> "Unknown: Surface query is incomplete"
                device.surfaceQueryStatus == "unknown" -> "Unknown"
                else -> "Unavailable"
            })
            CapabilityKeyValue("Surface queue-family safety rejection", when (device?.surfaceQueueQuerySafetyRejected) { true -> "YES · result discarded"; false -> "NO"; null -> "Unavailable" })
            CapabilityKeyValue("Dependent WSI queries", when (device?.surfaceDependentWsiQueryStatus) {
                "available" -> "Available"
                "incomplete" -> "Incomplete"
                "not_applicable" -> "Not applicable: no queue family supports presentation to this Surface"
                else -> "Unknown: presentation support was not proven"
            })
            CapabilityKeyValue("Surface format query", when {
                device == null -> "Unavailable"
                device.surfaceFormatQueryAttempted -> vkResultText(device.surfaceFormatQueryResult)
                device.surfaceDependentWsiQueryStatus == "not_applicable" -> "Not applicable"
                else -> "Unknown: query not attempted because Surface support was not proven"
            })
            CapabilityKeyValue("Second format query", when {
                device == null -> "Unavailable"
                device.surfaceFormatQuerySafetyRejected -> "Rejected: safety bound"
                device.surfaceFormatQuerySecondAttempted -> vkResultText(device.surfaceFormatQueryResultSecond)
                device.surfaceDependentWsiQueryStatus == "not_applicable" -> "Not applicable"
                device.surfaceFormatQueryAttempted -> "Not attempted"
                else -> "Unknown: query not attempted because Surface support was not proven"
            })
            CapabilityKeyValue("Returned format pairs", when {
                device == null -> "Unavailable"
                device.surfaceFormatQueryAttempted -> device.surfaceFormats.size.toString()
                device.surfaceDependentWsiQueryStatus == "not_applicable" -> "Not applicable"
                else -> "Unknown"
            })
            CapabilityKeyValue("Surface-format enumeration complete", when {
                device == null -> "Unavailable"
                device.surfaceFormatEnumerationComplete && !device.surfaceFormatQuerySpecAnomaly -> "YES"
                device.surfaceFormatQuerySpecAnomaly -> "NO · specification anomaly"
                else -> "NO"
            })
            CapabilityKeyValue("Present-mode enumeration complete", when {
                device == null -> "Unavailable"
                device.surfacePresentModeEnumerationComplete && !device.surfacePresentModeQuerySpecAnomaly -> "YES"
                device.surfacePresentModeQuerySpecAnomaly -> "NO · specification anomaly"
                else -> "NO"
            })
            device?.surfaceCapabilities?.forEach { CapabilityKeyValue(it.first, it.second) }
        } }
        item { CapabilitySectionCard("Search surface formats / color spaces") {
            ExpressiveSearchField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), placeholderText = "Search BT.709, BT.2020, P3, HDR10, format…")
            Spacer(Modifier.height(6.dp))
            SupportFilterRow(filter) { filter = it }
            Text("${filtered.size} entries", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.labelMedium)
        } }
        item { CapabilitySectionCard("HDR / wide-color surface detection") {
            CapabilityKeyValue("VK_EXT_swapchain_colorspace", when {
                device?.surfaceColorSpaceExtensionEnabled == true -> "Enabled"
                device?.surfaceColorSpaceExtensionStatus == "available" -> "Available but not enabled"
                device?.surfaceColorSpaceExtensionStatus == "not_exposed" -> "Not exposed"
                else -> "Unknown: instance-extension enumeration is incomplete or unavailable"
            })
            val hdrPairs = device?.surfaceFormats?.count { it.classification == "HDR10 / PQ" || it.classification == "HDR10 / HLG" || it.classification == "Dolby Vision" } ?: 0
            val wideColorPairs = device?.surfaceFormats?.count { it.classification == "Display-P3" || it.classification == "Display-P3 / Linear" || it.classification == "BT.2020" || it.classification == "scRGB" || it.classification == "scRGB / Linear" } ?: 0
            CapabilityKeyValue("HDR color-space pairs", when { device?.surfacePresentationSupported == true -> hdrPairs.toString(); device?.surfaceDependentWsiQueryStatus == "not_applicable" -> "Not applicable"; else -> "Unknown" })
            CapabilityKeyValue("Wide-color pairs", when { device?.surfacePresentationSupported == true -> wideColorPairs.toString(); device?.surfaceDependentWsiQueryStatus == "not_applicable" -> "Not applicable"; else -> "Unknown" })
            Text(if (device?.surfaceFormatEnumerationComplete == true && !device.surfaceFormatQuerySpecAnomaly) "Supported entries are returned directly by the completed surface-format enumeration. Catalog entries absent from that complete result are shown as not supported for this surface." else "Surface-format enumeration was not confirmed complete, so VulkanScope shows only returned entries and does not infer unsupported format/color-space pairs from missing results.", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
        } }
        item { CapabilitySectionCard("Format + color-space pairs") {
            if (filtered.isEmpty()) Text("No matching entries") else Text("${filtered.size} matching format/color-space pairs", color = VulkanTextMuted, style = MaterialTheme.typography.bodySmall)
        } }
        items(filtered, key = { "${it.format}|${it.colorSpace}|${it.supported}" }) { format ->
            CapabilityItemCard(containerColor = VulkanSurfaceTonal) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(format.format, fontWeight = FontWeight.SemiBold)
                    Text(format.colorSpace, color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                    CapabilityStatusBadge(if (format.supported) "SUPPORTED · ${format.classification}" else "NOT SUPPORTED · ${format.classification}", format.supported)
                    Text(format.description, color = VulkanTextMuted, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        item { CapabilitySectionCard("Present modes") {
            if (device?.presentModes.isNullOrEmpty()) EmptyState(when (device?.surfaceDependentWsiQueryStatus) { "not_applicable" -> "Present-mode query not applicable because this physical device cannot present to the Surface"; "unknown" -> "Present-mode query not attempted because Surface support was not proven"; else -> "Present mode data unavailable" })
            device?.presentModes?.forEachIndexed { index, mode -> CapabilityKeyValue("Mode ${index + 1}", mode) }
        } }
        item { CapabilitySectionCard("Presentation support") {
            if (device?.presentationQueueEvidence.isNullOrEmpty() && device?.presentationQueues.isNullOrEmpty()) EmptyState("Presentation queue data unavailable")
            if (!device?.presentationQueueEvidence.isNullOrEmpty()) {
                device?.presentationQueueEvidence?.forEach { CapabilityKeyValue("Queue family ${it.queueFamily}", presentationQueueEvidenceText(it)) }
            } else {
                device?.presentationQueues?.forEach { CapabilityKeyValue("Queue family ${it.first}", if (it.second) "PRESENT" else "NO PRESENT") }
            }
        } }
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
    VulkanLazyPage(verticalSpacing = 8.dp) {
        item {
            CapabilitySectionCard("Feature explorer") {
            Text("Runtime feature support. Core 1.0, promoted core versions and extension-provided feature blocks remain distinguishable.", style = MaterialTheme.typography.bodySmall, color = ComposeColor(0xFFB6ACAE))
            ExpressiveSearchField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), labelText = "Search features")
            SupportFilterRow(filter) { filter = it }
            ExpressiveFilterBar(sources, sources.indexOf(sourceFilter).coerceAtLeast(0), arrowTint = VulkanAccentSoft) { sourceFilter = sources[it] }
            ExpressiveMetricGrid(listOf("Visible features" to filtered.size.toString()), Modifier.padding(top = 4.dp))
            }
        }
        itemsIndexed(filtered, key = { index, feature -> "feature:${feature.name}:$index" }) { _, feature ->
            CapabilityItemCard {
                Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(featureNameOnly(feature.name), fontWeight = FontWeight.Medium)
                        Text(featureSource(feature.name), color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.labelSmall)
                    }
                    CapabilityStatusBadge(if (feature.supported) "SUPPORTED" else "NOT SUPPORTED", feature.supported)
                }
            }
        }
        if (filtered.isEmpty()) item { EmptyState("No matching features") }
    }
}

@Composable
private fun MemoryPage(device: DeviceReport?) {
    VulkanLazyPage(verticalSpacing = 14.dp) {
        item { CapabilitySectionCard("Memory query safety") {
            CapabilityKeyValue("Heap-count safety rejection", when (device?.memoryHeapSafetyRejected) { true -> "YES · bounded data only"; false -> "NO"; null -> "Unavailable" })
            CapabilityKeyValue("Type-count safety rejection", when (device?.memoryTypeSafetyRejected) { true -> "YES · bounded data only"; false -> "NO"; null -> "Unavailable" })
        } }
        item { CapabilitySectionCard("Memory heaps") {
            if (device?.heaps.isNullOrEmpty()) EmptyState("Memory heap data unavailable")
            device?.heaps?.forEach { heap ->
                CapabilityItemCard(containerColor = VulkanSurfaceTonal) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text("Heap ${heap.index}", fontWeight = FontWeight.SemiBold)
                        CapabilityKeyValue("Size", formatBytes(heap.size))
                        CapabilityKeyValue("Flags", memoryHeapFlags(heap.flags))
                    }
                }
            }
        } }
        item { CapabilitySectionCard("Memory types") {
            if (device?.memoryTypes.isNullOrEmpty()) EmptyState("Memory type data unavailable")
            device?.memoryTypes?.forEach { type ->
                CapabilityItemCard(containerColor = VulkanSurfaceTonal) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text("Type ${type.index}", fontWeight = FontWeight.SemiBold)
                        CapabilityKeyValue("Heap", type.heap.toString())
                        CapabilityKeyValue("Properties", memoryTypeFlags(type.flags))
                    }
                }
            }
        } }
    }
}

@Composable
private fun QueuesPage(device: DeviceReport?) {
    VulkanLazyPage(verticalSpacing = 10.dp) {
        item { CapabilitySectionCard("Queue query safety") {
            CapabilityKeyValue("Enumeration safety rejection", when (device?.queueQuerySafetyRejected) { true -> "YES · result discarded"; false -> "NO"; null -> "Unavailable" })
        } }
        items(device?.queues ?: emptyList(), key = { it.index }) { queue ->
            CapabilitySectionCard("Queue family ${queue.index}") {
                CapabilityKeyValue("Queue count", queue.count.toString())
                CapabilityKeyValue("Timestamp valid bits", queue.timestampBits.toString())
                CapabilityKeyValue("Capabilities", queueCapabilityFlags(queue.flags))
                CapabilityKeyValue("Graphics", if (queue.graphics) "YES" else "NO")
                CapabilityKeyValue("Compute", if (queue.compute) "YES" else "NO")
                CapabilityKeyValue("Transfer", if (queue.transfer) "YES" else "NO")
                CapabilityKeyValue("Sparse binding", if (queue.sparse) "YES" else "NO")
                CapabilityKeyValue("Protected", if (queue.protected) "YES" else "NO")
                CapabilityKeyValue("Video decode", if (queue.videoDecode) "YES" else "NO")
                CapabilityKeyValue("Video encode", if (queue.videoEncode) "YES" else "NO")
                CapabilityKeyValue("Optical flow", if (queue.opticalFlow) "YES" else "NO")
                CapabilityKeyValue("Data graph", if (queue.dataGraph) "YES" else "NO")
                CapabilityKeyValue("Min image transfer granularity", queue.granularity)
                if (queue.unknownFlags != 0L) CapabilityKeyValue("Unknown queue flag bits", "0x${queue.unknownFlags.toString(16).uppercase()}")
                CapabilityKeyValue("Video codec query", queueVideoCodecQueryState(queue))
                if (queueVideoCodecEvidenceRetained(queue)) CapabilityKeyValue("Video codec operations", videoCodecOperationFlags(queue.videoCodecOperations))
            }
        }
        if (device?.queues.isNullOrEmpty()) item { EmptyState("Queue family data unavailable") }
    }
}



private data class VideoProfileEvidence(
    val operation: String,
    val direction: String,
    val codec: String,
    val variant: String,
    val status: String,
    val properties: List<Pair<String, String>>
)

private data class VulkanVideoEvidence(
    val general: List<PropertyEntry>,
    val profiles: List<VideoProfileEvidence>,
    val formats: List<PropertyEntry>
)

private fun parseVulkanVideoEvidence(device: DeviceReport?): VulkanVideoEvidence {
    val rows = device?.detailedProperties.orEmpty().filter { it.section == "Vulkan Video" }
    val operationOrder = listOf("H.264 decode", "H.265 decode", "VP9 decode", "AV1 decode", "H.264 encode", "H.265 encode", "AV1 encode")
    val grouped = linkedMapOf<Pair<String, String>, MutableList<Pair<String, String>>>()
    val formats = mutableListOf<PropertyEntry>()
    val general = mutableListOf<PropertyEntry>()
    rows.forEach { entry ->
        when {
            entry.name.startsWith("Video formats · ") -> formats += entry
            entry.name.startsWith("Video · ") -> {
                val body = entry.name.removePrefix("Video · ")
                val operation = operationOrder.firstOrNull { body.startsWith("$it · ") }
                if (operation == null) {
                    general += entry
                } else {
                    val tail = body.removePrefix("$operation · ")
                    val bracketEnd = tail.lastIndexOf(']')
                    val variant = if (bracketEnd >= 0) tail.substring(0, bracketEnd + 1).trim() else tail.trim()
                    val property = if (bracketEnd >= 0) tail.substring(bracketEnd + 1).trim().ifBlank { "status" } else "status"
                    grouped.getOrPut(operation to variant) { mutableListOf() } += property to entry.value
                }
            }
            else -> general += entry
        }
    }
    val profiles = grouped.map { (identity, values) ->
        val operation = identity.first
        val direction = if (operation.endsWith("decode")) "Decode" else "Encode"
        val codec = operation.removeSuffix(" decode").removeSuffix(" encode")
        val status = values.lastOrNull { it.first == "status" }?.second ?: "Unknown: exact profile status was not reported."
        VideoProfileEvidence(operation, direction, codec, identity.second, status, values.filterNot { it.first == "status" })
    }.sortedWith(compareBy<VideoProfileEvidence> { operationOrder.indexOf(it.operation).let { index -> if (index < 0) Int.MAX_VALUE else index } }.thenBy { it.variant })
    return VulkanVideoEvidence(general, profiles, formats)
}

private fun videoEvidenceState(value: String): String = when {
    value.startsWith("Supported", true) -> "SUPPORTED"
    value.startsWith("Unsupported", true) -> "UNSUPPORTED"
    value.startsWith("Available", true) -> "AVAILABLE"
    value.startsWith("Unavailable", true) -> "UNAVAILABLE"
    value.startsWith("Not applicable", true) -> "NOT APPLICABLE"
    value.startsWith("Incomplete", true) || value.startsWith("Partial", true) -> "INCOMPLETE"
    value.startsWith("Unknown", true) -> "UNKNOWN"
    else -> "UNKNOWN"
}

private fun videoPropertyLabel(name: String): String = when (name) {
    "maxLevel", "maxLevelIdc" -> "Maximum level"
    "codedExtent" -> "Coded extent"
    "DPB" -> "Decoded picture buffer"
    "bitstreamAlignment" -> "Bitstream alignment"
    "stdHeader" -> "StdVideo header"
    "fieldOffsetGranularity" -> "Field offset granularity"
    "capabilityFlags" -> "Capability flags"
    "pictureAccessGranularity" -> "Picture access granularity"
    "encodeFlags" -> "Encode flags"
    "rateControlModes" -> "Rate-control modes"
    "maxRateControlLayers" -> "Maximum rate-control layers"
    "maxBitrate" -> "Maximum bitrate"
    "maxQualityLevels" -> "Maximum quality levels"
    "encodeInputPictureGranularity" -> "Encode input granularity"
    "supportedEncodeFeedbackFlags" -> "Encode feedback flags"
    "codecFlags" -> "Codec flags"
    "maxSliceCount" -> "Maximum slice count"
    "maxSliceSegmentCount" -> "Maximum slice-segment count"
    "maxTiles" -> "Maximum tiles"
    "ctbSizes" -> "CTB sizes"
    "transformBlockSizes" -> "Transform block sizes"
    "maxPPictureL0ReferenceCount" -> "Maximum P-picture L0 references"
    "maxBPictureL0ReferenceCount" -> "Maximum B-picture L0 references"
    "maxL1ReferenceCount" -> "Maximum L1 references"
    "maxTemporalLayerCount" -> "Maximum temporal layers"
    "maxSubLayerCount" -> "Maximum sub-layers"
    "expectDyadicTemporalLayerPattern" -> "Expects dyadic temporal-layer pattern"
    "expectDyadicTemporalSubLayerPattern" -> "Expects dyadic temporal sub-layer pattern"
    "qpRange" -> "QP range"
    "prefersGopRemainingFrames" -> "Prefers GOP remaining frames"
    "requiresGopRemainingFrames" -> "Requires GOP remaining frames"
    "stdSyntaxFlags" -> "StdVideo syntax flags"
    "codedPictureAlignment" -> "Coded picture alignment"
    "minTileSize" -> "Minimum tile size"
    "maxTileSize" -> "Maximum tile size"
    "superblockSizes" -> "Superblock sizes"
    "maxSingleReferenceCount" -> "Maximum single references"
    "singleReferenceNameMask" -> "Single-reference name mask"
    "maxUnidirectionalCompoundReferenceCount" -> "Maximum unidirectional compound references"
    "maxUnidirectionalCompoundGroup1ReferenceCount" -> "Maximum unidirectional compound group 1 references"
    "unidirectionalCompoundReferenceNameMask" -> "Unidirectional compound reference-name mask"
    "maxBidirectionalCompoundReferenceCount" -> "Maximum bidirectional compound references"
    "maxBidirectionalCompoundGroup1ReferenceCount" -> "Maximum bidirectional compound group 1 references"
    "maxBidirectionalCompoundGroup2ReferenceCount" -> "Maximum bidirectional compound group 2 references"
    "bidirectionalCompoundReferenceNameMask" -> "Bidirectional compound reference-name mask"
    "maxSpatialLayerCount" -> "Maximum spatial layers"
    "maxOperatingPoints" -> "Maximum operating points"
    "qIndexRange" -> "Q-index range"
    else -> name
}

@Composable
private fun VideoEvidenceStateBadge(state: String) {
    val background = when (state) {
        "SUPPORTED" -> ComposeColor(0xFF173421)
        "UNSUPPORTED" -> ComposeColor(0xFF3A1D20)
        "AVAILABLE" -> ComposeColor(0xFF172B3A)
        "UNAVAILABLE" -> ComposeColor(0xFF332A16)
        "NOT APPLICABLE" -> ComposeColor(0xFF272727)
        "INCOMPLETE" -> ComposeColor(0xFF30243A)
        else -> ComposeColor(0xFF252525)
    }
    val foreground = when (state) {
        "SUPPORTED" -> ComposeColor(0xFF73C991)
        "UNSUPPORTED" -> ComposeColor(0xFFFF8A8A)
        "AVAILABLE" -> ComposeColor(0xFF7CC4FF)
        "UNAVAILABLE" -> ComposeColor(0xFFFFC857)
        "NOT APPLICABLE" -> ComposeColor(0xFFC4C4C4)
        "INCOMPLETE" -> ComposeColor(0xFFD3A4FF)
        else -> ComposeColor(0xFFA8A8A8)
    }
    Surface(shape = RoundedCornerShape(999.dp), color = background) {
        Text(state, color = foreground, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
    }
}

@Composable
private fun VideoProfileEvidenceCard(profile: VideoProfileEvidence) {
    CapabilityItemCard {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("${profile.codec} ${profile.direction.lowercase()}", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                    Text(profile.variant, color = VulkanTextPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                }
                VideoEvidenceStateBadge(videoEvidenceState(profile.status))
            }
            Text(profile.status, color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
            profile.properties.forEach { (name, value) -> CapabilityKeyValue(videoPropertyLabel(name), value) }
        }
    }
}

private fun videoOperationNeedle(profile: VideoProfileEvidence): String = when {
    profile.direction == "Decode" && profile.codec == "H.264" -> "DECODE_H264"
    profile.direction == "Decode" && profile.codec == "H.265" -> "DECODE_H265"
    profile.direction == "Decode" && profile.codec == "VP9" -> "DECODE_VP9"
    profile.direction == "Decode" && profile.codec == "AV1" -> "DECODE_AV1"
    profile.direction == "Encode" && profile.codec == "H.264" -> "ENCODE_H264"
    profile.direction == "Encode" && profile.codec == "H.265" -> "ENCODE_H265"
    profile.direction == "Encode" && profile.codec == "AV1" -> "ENCODE_AV1"
    else -> ""
}

private fun videoProfileQueueFamilies(profile: VideoProfileEvidence, queues: List<QueueEntry>): List<Int> {
    val needle = videoOperationNeedle(profile)
    if (needle.isBlank()) return emptyList()
    return queues.filter { queue -> queueVideoCodecEvidenceRetained(queue) && videoCodecOperationFlags(queue.videoCodecOperations).contains(needle, true) }.map { it.index }
}

@Composable
private fun VulkanVideoPage(device: DeviceReport?) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    val evidence = remember(device) { parseVulkanVideoEvidence(device) }
    val tabs = listOf("Overview", "Matrix", "Decode", "Encode", "Formats", "Queues")
    val registry = evidence.general.lastOrNull { it.name == "videoRegistry" }?.value
    val recipe = evidence.general.lastOrNull { it.name == "queryRecipe" }?.value
    val capabilityQuery = evidence.general.lastOrNull { it.name == "Video capability query" }?.value
    val formatQuery = evidence.general.lastOrNull { it.name == "Video format query" }?.value
    val decodeProfiles = remember(evidence.profiles) { evidence.profiles.filter { it.direction == "Decode" } }
    val encodeProfiles = remember(evidence.profiles) { evidence.profiles.filter { it.direction == "Encode" } }
    val queryStatus = evidence.general.lastOrNull { it.name == "queryStatus" }?.value
        ?: capabilityQuery?.takeIf { it.startsWith("Unavailable", true) || it.startsWith("Unknown", true) }
        ?: if (evidence.profiles.isNotEmpty()) "Available: exact-profile Vulkan Video census evidence was collected." else "Unknown: Vulkan Video query status was not reported."
    VulkanLazyPage(verticalSpacing = 10.dp) {
        item {
            CapabilitySectionCard("Vulkan Video") {
                Text("Exact-profile Vulkan® Video evidence collected by VulkanScope. Capability results below apply only to the queried exact 4:2:0 8-bit luma/chroma profile combinations and never imply codec-wide support.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                ExpressiveFilterBar(tabs, tab) { tab = it }
            }
        }
        if (tab == 0) {
            item {
                CapabilitySectionCard("Query overview") {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Capability query", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        VideoEvidenceStateBadge(videoEvidenceState(queryStatus))
                    }
                    CapabilityKeyValue("Query status", queryStatus)
                    CapabilityKeyValue("Exact general profile", "VK_VIDEO_CHROMA_SUBSAMPLING_420_BIT_KHR · 8-bit luma · 8-bit chroma")
                    CapabilityKeyValue("Exact profile combinations", evidence.profiles.size.toString())
                    CapabilityKeyValue("Decode combinations", decodeProfiles.size.toString())
                    CapabilityKeyValue("Encode combinations", encodeProfiles.size.toString())
                    registry?.let { CapabilityKeyValue("Registry identity", it) }
                    capabilityQuery?.let { CapabilityKeyValue("Capability entry point", it) }
                    recipe?.let { CapabilityKeyValue("Query recipe", it) }
                }
            }
            item {
                CapabilitySectionCard("Codec summary") {
                    listOf("H.264", "H.265", "VP9", "AV1").forEach { codec ->
                        val codecRows = evidence.profiles.filter { it.codec == codec }
                        val supported = codecRows.count { videoEvidenceState(it.status) == "SUPPORTED" }
                        val unsupported = codecRows.count { videoEvidenceState(it.status) == "UNSUPPORTED" }
                        val unresolved = codecRows.size - supported - unsupported
                        CapabilityKeyValue(codec, "${codecRows.size} exact combinations · $supported supported · $unsupported unsupported · $unresolved other evidence states")
                    }
                }
            }
            item {
                CapabilitySectionCard("Format scope") {
                    CapabilityKeyValue("Format query", formatQuery ?: "Unknown: sampled-profile format query status was not reported.")
                    Text("Format rows are intentionally sampled-profile evidence. They are not a complete profile/chroma/bit-depth Cartesian census and are kept separate from the exact-profile capability results.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                }
            }
        } else if (tab == 1) {
            item { CapabilitySectionCard("Exact-profile matrix") {
                Text("Each row keeps exact-profile capability state, matching queue codec-operation evidence and bounded sampled-format evidence separate. A queue/format association does not upgrade the exact-profile capability state.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
            } }
            items(evidence.profiles, key = { "matrix:${it.operation}:${it.variant}" }) { profile ->
                val queueFamilies = videoProfileQueueFamilies(profile, device?.queues.orEmpty())
                val sampledFormats = evidence.formats.filter { it.name.contains(profile.operation, true) || it.name.contains(profile.codec, true) }
                CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("${profile.codec} ${profile.direction.lowercase()}", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                            Text(profile.variant, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall)
                        }
                        VideoEvidenceStateBadge(videoEvidenceState(profile.status))
                    }
                    CapabilityKeyValue("Exact profile status", profile.status)
                    CapabilityKeyValue("Matching queue operation evidence", if (queueFamilies.isEmpty()) "No matching retained queue-operation token" else queueFamilies.joinToString { "Queue $it" })
                    CapabilityKeyValue("Bounded sampled-format rows", sampledFormats.size.toString())
                    profile.properties.firstOrNull { it.first == "codedExtent" }?.let { CapabilityKeyValue("Coded extent", it.second) }
                    profile.properties.firstOrNull { it.first == "DPB" }?.let { CapabilityKeyValue("Decoded picture buffer", it.second) }
                    profile.properties.firstOrNull { it.first == "rateControlModes" }?.let { CapabilityKeyValue("Rate-control modes", it.second) }
                } }
            }
            if (evidence.profiles.isEmpty()) item { EmptyState("No exact-profile Vulkan® Video evidence reported") }
        } else if (tab == 2) {
            item { CapabilitySectionCard("Decode profiles") { Text("H.264, H.265, VP9 and AV1 exact decode-profile combinations from the locked registry-driven census.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall) } }
            items(decodeProfiles, key = { "decode:${it.operation}:${it.variant}" }) { VideoProfileEvidenceCard(it) }
            if (decodeProfiles.isEmpty()) item { EmptyState("No decode-profile evidence reported") }
        } else if (tab == 3) {
            item { CapabilitySectionCard("Encode profiles") { Text("H.264, H.265 and AV1 exact encode-profile combinations from the locked registry-driven census.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall) } }
            items(encodeProfiles, key = { "encode:${it.operation}:${it.variant}" }) { VideoProfileEvidenceCard(it) }
            if (encodeProfiles.isEmpty()) item { EmptyState("No encode-profile evidence reported") }
        } else if (tab == 4) {
            item {
                CapabilitySectionCard("Sampled-profile formats") {
                    CapabilityKeyValue("Format query", formatQuery ?: "Unknown: sampled-profile format query status was not reported.")
                    Text("These rows preserve the existing bounded sampled-profile format recipes. A listed format is evidence only for the exact sampled profile and usage token shown in that row.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                }
            }
            items(evidence.formats, key = { it.name }) { entry ->
                CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(entry.name.removePrefix("Video formats · "), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                    Text(entry.value, color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                } }
            }
            if (evidence.formats.isEmpty()) item { EmptyState("No sampled-profile Vulkan® Video format evidence reported") }
        } else {
            item {
                CapabilitySectionCard("Queue support") {
                    Text("Queue-family video flags and codec-operation masks are independent runtime evidence. A false queue flag remains an exact queried value and is not reclassified as Unsupported unless an owning support query establishes that state.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                }
            }
            items(device?.queues.orEmpty(), key = { it.index }) { queue ->
                CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Queue family ${queue.index}", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                        VideoEvidenceStateBadge(when (queue.videoCodecQueryStatus) {
                            "available" -> "AVAILABLE"
                            "incomplete" -> "INCOMPLETE"
                            "not_applicable" -> "NOT APPLICABLE"
                            "unavailable" -> "UNAVAILABLE"
                            else -> "UNKNOWN"
                        })
                    }
                    CapabilityKeyValue("Video decode flag", if (queue.videoDecode) "YES" else "NO")
                    CapabilityKeyValue("Video encode flag", if (queue.videoEncode) "YES" else "NO")
                    CapabilityKeyValue("Codec query", queueVideoCodecQueryState(queue))
                    if (queueVideoCodecEvidenceRetained(queue)) CapabilityKeyValue("Codec operations", videoCodecOperationFlags(queue.videoCodecOperations))
                } }
            }
            if (device?.queues.isNullOrEmpty()) item { EmptyState("Queue family data unavailable") }
        }
    }
}



private const val ANALYSIS_MAX_SNAPSHOT_BYTES = 8 * 1024 * 1024
private const val ANALYSIS_MAX_ENTRIES = 32768
private const val ANALYSIS_MAX_KEY_LENGTH = 1024
private const val ANALYSIS_MAX_VALUE_LENGTH = 16384
private const val ANALYSIS_MAX_WATCHED = 256

private fun readBoundedAnalysisBytes(input: java.io.InputStream, maxBytes: Int): ByteArray {
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

private fun validateAnalysisSnapshot(obj: JSONObject): JSONObject {
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

private data class VulkanAnalysisDiff(val key: String, val baseline: String?, val current: String?, val state: String) {
    val kind: String get() = key.substringBefore('/').lowercase()
}

private fun vulkanAnalysisEntries(report: VulkanReport, device: DeviceReport?, display: DisplayReport, mode: DriverMode): JSONObject = JSONObject().apply {
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
private fun vulkanAnalysisSnapshot(report: VulkanReport, device: DeviceReport?, display: DisplayReport, mode: DriverMode, applicationVersion: String): JSONObject = JSONObject()
    .put("schema", "VulkanScopeAnalysisSnapshot1")
    .put("applicationVersion", applicationVersion)
    .put("entries", vulkanAnalysisEntries(report, device, display, mode))

private fun objectStringMap(obj: JSONObject): Map<String, String> = linkedMapOf<String, String>().apply { obj.keys().forEach { key -> put(key, obj.optString(key, "Unavailable")) } }

private fun snapshotDeviceExtensionsComplete(entries: Map<String, String>): Boolean = entries["query/deviceExtensionStatus"] == "available"

private fun snapshotSurfaceFormatsComplete(entries: Map<String, String>): Boolean =
    entries["surface/available"] == "true" && entries["surface/formatQuerySecondAttempted"] == "true" &&
        entries["surface/formatQueryResultSecond"] == "0" && entries["safety/surfaceFormatRejected"] != "true"

private fun vulkanSnapshotDiff(baseline: JSONObject, current: JSONObject): List<VulkanAnalysisDiff> {
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
private fun vulkanExtensionQueryGroup(name: String): String? = ISOLATED_EXTENSION_GROUPS.entries.firstOrNull { it.value == name }?.key

private data class HeuristicDiagnosticScore(val score: Int?, val level: String, val factors: List<String>)

private fun heuristicDiagnosticEvidenceScore(report: VulkanReport, device: DeviceReport?): HeuristicDiagnosticScore {
    if (device == null) return HeuristicDiagnosticScore(null, "Unavailable", listOf("No completed physical-device report is available"))
    var score = 100
    val factors = mutableListOf<String>()
    fun deduct(points: Int, label: String) {
        score = (score - points).coerceAtLeast(0)
        factors += "-$points · $label"
    }
    if (!report.error.isNullOrBlank()) deduct(35, "Top-level collection error")
    if (device.queueQuerySafetyRejected) deduct(15, "Queue-family enumeration safety rejection")
    if (device.memoryHeapSafetyRejected) deduct(10, "Memory-heap enumeration safety rejection")
    if (device.memoryTypeSafetyRejected) deduct(10, "Memory-type enumeration safety rejection")
    if (device.surfaceQueueQuerySafetyRejected) deduct(10, "Surface queue-family enumeration safety rejection")
    if (device.surfaceFormatQuerySafetyRejected) deduct(10, "Surface-format enumeration safety rejection")
    if (device.deviceExtensionStatus != "available") deduct(10, "Device-extension enumeration is ${device.deviceExtensionStatus}")
    if (device.surfaceFormatQuerySecondAttempted && device.surfaceFormatQueryResultSecond != 0) deduct(5, "Second Surface-format query returned VkResult ${device.surfaceFormatQueryResultSecond}")
    val level = when {
        score >= 95 -> "No explicit collection anomalies"
        score >= 80 -> "Minor explicit anomalies"
        score >= 60 -> "Multiple explicit anomalies"
        else -> "Severe explicit collection anomalies"
    }
    if (factors.isEmpty()) factors += "No explicit collection/safety anomaly was recorded by VulkanScope"
    return HeuristicDiagnosticScore(score, level, factors)
}

private fun analysisQueryTokens(query: String): List<String> = Regex("\\\"([^\\\"]+)\\\"|\\S+").findAll(query).map { it.groups[1]?.value ?: it.value }.toList()

private fun analysisKind(key: String): String = key.substringBefore('/').lowercase()

private fun extensionVendorToken(name: String): String = name.removePrefix("VK_").substringBefore('_').uppercase()

private fun matchesAnalysisQuery(row: VulkanAnalysisDiff, query: String): Boolean {
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

private fun extensionDependencyTokens(expression: String): List<String> = Regex("VK_(?:VERSION_[0-9_]+|[A-Z0-9]+_[A-Za-z0-9_]+)").findAll(expression).map { it.value }.distinct().toList()

private data class DependencyGraphEntry(val token: String, val depth: Int, val parent: String?)

private fun dependencyGraphEntries(root: String, maxDepth: Int = 4, maxNodes: Int = 64): List<DependencyGraphEntry> {
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

private fun dependencyRuntimeEvidence(token: String, report: VulkanReport, device: DeviceReport?): String {
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

private fun matchesExtensionExplorerQuery(name: String, scope: String, enumerated: Boolean, query: String): Boolean {
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

private fun matchesFormatExplorerQuery(format: FormatEntry, query: String): Boolean {
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

private fun databaseReportUrl(id: String): String = OFFICIAL_DATABASE_WEB_URL.trimEnd('/') + "/#reports/" + Uri.encode(id) + "/Overview"

private suspend fun fetchDatabaseTechnicalReport(context: Context, reportId: String): JSONObject = withContext(Dispatchers.IO) {
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

private fun loadSavedMinimumProfiles(raw: String?): Map<String, String> {
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

private fun encodeSavedMinimumProfiles(profiles: Map<String, String>): String = JSONObject().apply {
    put("schema", "VulkanScopeMinimumProfiles1")
    put("profiles", JSONObject().apply { profiles.toSortedMap().entries.take(ANALYSIS_CUSTOM_PROFILE_MAX_PROFILES).forEach { (name, rules) -> put(name, rules) } })
}.toString()

private class AnalysisWorkspaceState(initialWatched: Set<String>, initialProfiles: Map<String, String>, initialAbWorkflowStage: String) {
    var tab by mutableIntStateOf(0)
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
    var savedProfiles by mutableStateOf(initialProfiles)
    var graphInput by mutableStateOf("VK_KHR_swapchain")
    var graphDepth by mutableIntStateOf(4)
    var rawQuery by mutableStateOf("")
    var databaseReportId by mutableStateOf("")
    var databaseStatus by mutableStateOf("No Database report loaded")
    var databaseLoading by mutableStateOf(false)
    var databaseRemoteLeaves by mutableStateOf<List<RawJsonLeaf>>(emptyList())
    var databaseIncludeUnchanged by mutableStateOf(false)
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

private enum class AnalysisStorageAction {
    IMPORT_SNAPSHOT,
    EXPORT_SNAPSHOT,
    IMPORT_MINIMUM_PROFILE,
    EXPORT_MINIMUM_PROFILE,
    EXPORT_TECHNICAL_REPORT
}

private data class AnalysisWorkspaceModel(
    val state: AnalysisWorkspaceState,
    val diffRows: List<VulkanAnalysisDiff>,
    val globalResults: List<Pair<String, String>>,
    val diagnostics: List<QueryDiagnosticRow>,
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
    val driverHealth: HeuristicDiagnosticScore,
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
    val fetchDatabaseReport: () -> Unit,
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
private fun rememberAnalysisWorkspaceModel(
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
    val driverHealth = remember(report, device) { heuristicDiagnosticEvidenceScore(report, device) }
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
        driverHealth = driverHealth,
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
        fetchDatabaseReport = {
            if (!hasValidatedInternet(context)) {
                state.databaseRemoteLeaves = emptyList()
                state.databaseStatus = "Database fetch unavailable · no validated internet connection"
            } else if (!state.databaseLoading) {
                state.databaseLoading = true
                scope.launch {
                    val result = runCatching { fetchDatabaseTechnicalReport(context, state.databaseReportId) }
                    result.onSuccess { technical -> state.databaseRemoteLeaves = flattenJson(technical); state.databaseStatus = "Database technicalReport loaded · ${state.databaseReportId.take(12)}…" }
                        .onFailure { state.databaseRemoteLeaves = emptyList(); state.databaseStatus = it.message ?: "Database report fetch failed" }
                    state.databaseLoading = false
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

private fun historyLabel(record: AnalysisHistoryRecord): String = java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT, java.text.DateFormat.SHORT).format(java.util.Date(record.timestampMs))

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun LazyListScope.analysisWorkspaceItems(model: AnalysisWorkspaceModel, report: VulkanReport, device: DeviceReport?) {
    val state = model.state
    val tabs = listOf("Compare", "Search", "Drivers", "Diagnostics", "Requirements", "Minimums", "Graph", "Presentation", "Raw JSON", "Database", "History", "Watched", "Quality", "Share", "Tests")
    item {
        CapabilitySectionCard("Analysis workspace") {
            Text("Analysis, history, custom minimums and optional tests stay local unless you explicitly fetch a public Database report or share a link. None of these tools mutate canonical TXT, HTML or Database capability evidence.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
            Text("Analysis JSON exchange uses VulkanScope's in-app shared-storage browser; storage access is requested only after an explicit import/export action.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
            ExpressiveFilterBar(tabs, state.tab) { state.tab = it }
        }
    }
    when (state.tab) {
        0 -> {
            item { CapabilitySectionCard("Offline report compare") {
                CapabilityKeyValue("Snapshot status", state.analysisStatus)
                SharedStoragePermissionActionButton("Import analysis snapshot", "Open the in-app shared-storage browser · JSON · 8 MiB bound", R.drawable.ic_action_import) { model.importSnapshot() }
                SharedStoragePermissionActionButton("Export analysis snapshot", "Choose a shared-storage folder and JSON file name", R.drawable.ic_export) { model.exportSnapshot() }
                if (state.baseline != null) {
                    ExpressiveToggleRow("Show unchanged", "Include evidence rows whose canonical value is unchanged.", state.includeUnchanged) { state.includeUnchanged = it }
                    ExpressiveFilterBar(listOf("All", "Added", "Removed", "Changed", "Regression"), listOf("All", "Added", "Removed", "Changed", "Regression").indexOf(state.diffStateFilter).coerceAtLeast(0)) { state.diffStateFilter = listOf("All", "Added", "Removed", "Changed", "Regression")[it] }
                    ExpressiveSearchField(value = state.diffQuery, onValueChange = { state.diffQuery = it }, modifier = Modifier.fillMaxWidth(), placeholderText = "Search evidence differences…")
                }
            } }
            if (state.baseline != null) {
                item { CapabilitySectionCard("Diff summary") {
                    ExpressiveMetric("Added", model.diffRows.count { it.state == "Added" }.toString())
                    ExpressiveMetric("Removed", model.diffRows.count { it.state.startsWith("Removed") }.toString())
                    ExpressiveMetric("Changed", model.diffRows.count { it.state.startsWith("Changed") }.toString())
                    ExpressiveMetric("Regression candidates", model.diffRows.count { it.state.contains("regression candidate") }.toString())
                    Text("Regression candidates are evidence changes only; VulkanScope does not declare a driver defect from a diff alone.", color = VulkanTextMuted, style = MaterialTheme.typography.bodySmall)
                } }
                items(model.diffRows, key = { it.key }) { row ->
                    CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        CapabilityKeyValue(row.key, row.state)
                        row.baseline?.let { CapabilityKeyValue("Baseline", it) }
                        row.current?.let { CapabilityKeyValue("Current", it) }
                        ExpressiveContainedIconTextButton("Evidence provenance", R.drawable.ic_evidence) { state.selectedEvidence = row.key to (row.current ?: row.baseline.orEmpty()) }
                    } }
                }
            }
        }
        1 -> {
            item { CapabilitySectionCard("Global Vulkan report search") {
                Text("Searches the completed current-device evidence model. Registry/reference symbols remain separate and can be opened in Encyclopedia from each result.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                ExpressiveSearchField(value = state.globalQuery, onValueChange = { state.globalQuery = it }, modifier = Modifier.fillMaxWidth(), placeholderText = "Search descriptorBuffer, maxImageDimension2D, HDR10…")
                ExpressiveFilterBar(listOf("All", "extension", "feature", "property", "limit", "format", "surface", "display", "queue", "profile", "query"), listOf("All", "extension", "feature", "property", "limit", "format", "surface", "display", "queue", "profile", "query").indexOf(state.globalKindFilter).coerceAtLeast(0)) { state.globalKindFilter = listOf("All", "extension", "feature", "property", "limit", "format", "surface", "display", "queue", "profile", "query")[it] }
                CapabilityKeyValue("Visible result bound", "$ANALYSIS_GLOBAL_SEARCH_VISIBLE_LIMIT")
            } }
            if (state.globalQuery.trim().length < 2) item { EmptyState("Type at least two characters to search the complete current report") }
            items(model.globalResults, key = { it.first }) { result ->
                CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    CapabilityKeyValue(result.first, result.second)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { DetailAffordance { state.selectedEvidence = result } }
                } }
            }
        }
        2 -> {
            item { CapabilitySectionCard("System driver ↔ Turnip A/B") {
                Text("Each completed driver session is captured into bounded local history. Switching drivers remains an explicit user action and uses VulkanScope's existing driver-change/collection path.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                CapabilityKeyValue("System snapshot", model.systemHistory?.let(::historyLabel) ?: "No completed local System session")
                CapabilityKeyValue("Turnip snapshot", model.turnipHistory?.let(::historyLabel) ?: "No completed local Turnip session")
                CapabilityKeyValue("Guided workflow", state.abWorkflowStatus)
                ExpressiveActionButton("Run guided System ↔ Turnip A/B", "One explicit workflow retains the current completed session, collects the missing System/Turnip side, then exposes the local evidence diff", R.drawable.ic_compare, enabled = model.canSwitchSystem || model.canSwitchTurnip) { model.startDriverAbWorkflow() }
                if (state.abWorkflowStage in setOf("collect-system", "collect-turnip")) ExpressiveTextButton("Cancel guided A/B") { model.cancelDriverAbWorkflow() }
                ExpressiveActionButton("Switch to System and collect", "Manual single-side collection using the existing driver selection workflow", R.drawable.ic_compare, enabled = model.canSwitchSystem) { model.switchToSystem() }
                ExpressiveActionButton("Switch to Turnip and collect", "Manual single-side collection; requires a validated installed compatible driver bundle", R.drawable.ic_compare, enabled = model.canSwitchTurnip) { model.switchToTurnip() }
                Text("The guided workflow may perform the second driver switch after the first requested collection completes because that continuation was explicitly authorized by Run guided A/B. No driver mutation starts without that user action, and no network upload is involved.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
            } }
            if (model.systemHistory != null && model.turnipHistory != null) {
                item { CapabilitySectionCard("A/B summary") {
                    ExpressiveMetric("Changed evidence", model.driverDiffRows.size.toString())
                    ExpressiveMetric("Added", model.driverDiffRows.count { it.state == "Added" }.toString())
                    ExpressiveMetric("Removed", model.driverDiffRows.count { it.state.startsWith("Removed") }.toString())
                    ExpressiveMetric("Regression candidates", model.driverDiffRows.count { it.state.contains("regression candidate") }.toString())
                    Text("The baseline is the latest System session and the current side is the latest Turnip session. Differences are evidence changes, not performance or quality rankings.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                } }
                items(model.driverDiffRows.take(ANALYSIS_GLOBAL_SEARCH_VISIBLE_LIMIT), key = { "ab:${it.key}" }) { row -> CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    CapabilityKeyValue(row.key, row.state)
                    row.baseline?.let { CapabilityKeyValue("System", it) }
                    row.current?.let { CapabilityKeyValue("Turnip", it) }
                } } }
            } else item { EmptyState("Collect at least one completed System session and one completed Turnip session") }
        }
        3 -> {
            item { CapabilitySectionCard("Collection diagnostics") {
                Text("Shows explicit query/completeness/safety evidence already present in the report. The base collector does not publish elapsed time for every Vulkan® query; dedicated or on-demand probes display measured app-side elapsed time when that timing evidence exists, and VulkanScope does not invent missing timings.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                ExpressiveMetric("Diagnostic rows", model.diagnostics.size.toString())
                ExpressiveMetric("Safety rejections", model.diagnostics.count { it.state == "SAFETY REJECTED" }.toString())
                ExpressiveMetric("Incomplete", model.diagnostics.count { it.state == "INCOMPLETE" }.toString())
            } }
            items(model.diagnostics, key = { "diag:${it.key}" }) { row -> CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ExpressiveStatus(row.state)
                CapabilityKeyValue(row.key, row.value)
                CapabilityKeyValue("Per-query timing", row.timing)
            } } }
        }
        4 -> {
            item { CapabilitySectionCard("Capability requirement resolver") {
                Text("Registry dependency expressions are reference metadata. VulkanScope evaluates referenced tokens individually against authoritative runtime API/extension evidence and does not collapse a registry expression into an inferred global support claim.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                ExpressiveSearchField(value = state.requirementInput, onValueChange = { state.requirementInput = it }, modifier = Modifier.fillMaxWidth(), placeholderText = "VK_EXT_descriptor_buffer")
                model.requirementReference?.let { ref ->
                    CapabilityKeyValue("Registry depends", ref.depends.ifBlank { "No dependency expression in checked-in reference" })
                    CapabilityKeyValue("Registry requires", ref.requires.ifBlank { "No requires expression in checked-in reference" })
                    CapabilityKeyValue("Promoted to", ref.promotedTo.ifBlank { "Not promoted in checked-in reference" })
                }
            } }
            items(model.requirementEvaluations, key = { "req:${it.token}" }) { result -> CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                ExpressiveStatus(result.state)
                CapabilityKeyValue(result.token, result.evidence)
            } } }
        }
        5 -> {
            item { CapabilitySectionCard("Vulkan Profiles and custom minimums") {
                Text("Official bundled Profile evaluation remains authoritative for included profiles. The custom builder is a separate local rule layer and never rewrites runtime evidence.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                ExpressiveSearchField(value = state.profileQuery, onValueChange = { state.profileQuery = it }, modifier = Modifier.fillMaxWidth(), placeholderText = "Search bundled profiles…")
                ExpressiveFilterBar(listOf("All", "FAIL", "UNKNOWN", "PASS"), listOf("All", "FAIL", "UNKNOWN", "PASS").indexOf(state.profileStatusFilter).coerceAtLeast(0)) { state.profileStatusFilter = listOf("All", "FAIL", "UNKNOWN", "PASS")[it] }
            } }
            items(model.profileResults, key = { "profile:${it.name}" }) { result -> CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                CapabilityKeyValue(result.name, profileSummary(result))
                CapabilityKeyValue("Mapped checks", result.checkedRequirementCount.toString())
                result.failingLimits.takeIf { it.isNotEmpty() }?.let { CapabilityKeyValue("Verified property/limit failures", it.joinToString("; ")) }
                result.unknownLimits.takeIf { it.isNotEmpty() }?.let { CapabilityKeyValue("Unavailable properties/limits", it.joinToString("; ")) }
                result.failingRequirementGroups.takeIf { it.isNotEmpty() }?.let { CapabilityKeyValue("Failed inherited/OR requirements", it.joinToString("; ")) }
                result.unknownRequirementGroups.takeIf { it.isNotEmpty() }?.let { CapabilityKeyValue("Unresolved inherited/OR requirements", it.joinToString("; ")) }
            } } }
            item { CapabilitySectionCard("Custom minimum builder") {
                OutlinedTextField(value = state.customProfileName, onValueChange = { state.customProfileName = it.take(128) }, modifier = Modifier.fillMaxWidth(), label = { Text("Profile name") }, singleLine = true)
                OutlinedTextField(value = state.customProfileRules, onValueChange = { if (it.length <= 16_384) state.customProfileRules = it }, modifier = Modifier.fillMaxWidth().heightIn(min = 140.dp), label = { Text("Rules") }, supportingText = { Text("One rule per line: api>=1.3 · extension:VK_KHR_dynamic_rendering · feature:name=true · limit:name>=number · evidence:path=value") })
                CapabilityKeyValue("Local status", state.customProfileStatus)
                ExpressiveActionButton("Save local profile", "Bounded local profile; no capability mutation", R.drawable.ic_save) { model.saveMinimumProfile() }
                SharedStoragePermissionActionButton("Import profile JSON", "Open the in-app shared-storage browser · JSON · 256 KiB bound", R.drawable.ic_action_import) { model.importMinimumProfile() }
                SharedStoragePermissionActionButton("Export profile JSON", "Choose a shared-storage folder and JSON file name", R.drawable.ic_export) { model.exportMinimumProfile() }
            } }
            items(state.savedProfiles.toSortedMap().entries.toList(), key = { "saved:${it.key}" }) { entry -> CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                CapabilityKeyValue(entry.key, "${entry.value.lineSequence().count { it.isNotBlank() }} rule(s)")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { ExpressiveTextButton("Load") { model.loadMinimumProfile(entry.key) }; ExpressiveTextButton("Delete") { model.deleteMinimumProfile(entry.key) } }
            } } }
            item { CapabilitySectionCard("Current custom evaluation") {
                ExpressiveMetric("PASS", model.customMinimumEvaluations.count { it.state == "PASS" }.toString())
                ExpressiveMetric("FAIL", model.customMinimumEvaluations.count { it.state == "FAIL" }.toString())
                ExpressiveMetric("UNKNOWN", model.customMinimumEvaluations.count { it.state == "UNKNOWN" }.toString())
            } }
            items(model.customMinimumEvaluations, key = { "minimum:${it.rule}" }) { evaluation -> CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                ExpressiveStatus(evaluation.state)
                CapabilityKeyValue(evaluation.rule, evaluation.evidence)
            } } }
        }
        6 -> {
            item { CapabilitySectionCard("Capability dependency graph") {
                Text("Registry edges and runtime enumeration remain separate evidence classes. Graph traversal is bounded and cycle-safe.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                ExpressiveSearchField(value = state.graphInput, onValueChange = { state.graphInput = it }, modifier = Modifier.fillMaxWidth(), placeholderText = "VK_KHR_swapchain")
                ExpressiveFilterBar((1..4).map { "Depth $it" }, state.graphDepth - 1) { state.graphDepth = it + 1 }
                CapabilityKeyValue("Root", model.graphRootToken.ifBlank { "Enter a Vulkan® extension token" })
                model.graphRootRef?.let { CapabilityKeyValue("Registry depends", it.depends.ifBlank { "Unavailable in checked-in reference asset" }) }
            } }
            if (model.graphRootRef != null) {
                item { CapabilitySectionCard("Visual registry-reference graph") { VulkanDependencyGraph(model.visualGraphNodes, Modifier.fillMaxWidth()); CapabilityKeyValue("Bounded traversal", "${model.graphEntries.size} reference node(s)") } }
                items(model.graphEntries, key = { "dep:${it.depth}:${it.token}" }) { entry -> CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    CapabilityKeyValue(entry.token, dependencyRuntimeEvidence(entry.token, report, device))
                    entry.parent?.let { CapabilityKeyValue("Registry edge", "$it → ${entry.token}") }
                } } }
            }
        }
        7 -> {
            item { CapabilitySectionCard("Surface + Display presentation evidence") {
                Text("These are compatible evidence paths, not end-to-end presentation guarantees. Vulkan® Surface color spaces and Android physical-display HDR/wide-gamut evidence remain separate sources.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
            } }
            items(model.presentationPaths, key = { it.title }) { path -> CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                ExpressiveStatus(path.state)
                Text(path.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                CapabilityKeyValue("Vulkan® Surface", path.surfaceEvidence)
                CapabilityKeyValue("Android Display", path.displayEvidence)
                Text(path.note, color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
            } } }
        }
        8 -> {
            item { CapabilitySectionCard("Raw structured technical Report") {
                Text("Read-only tree leaves from the exact local schema-v3 technicalReport object used by Database submission. Values are searchable and exportable without changing the canonical report.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                ExpressiveSearchField(value = state.rawQuery, onValueChange = { state.rawQuery = it }, modifier = Modifier.fillMaxWidth(), placeholderText = "Search JSON path or value…")
                CapabilityKeyValue("Visible leaf bound", "$ANALYSIS_RAW_JSON_VISIBLE_LIMIT")
                SharedStoragePermissionActionButton("Export technicalReport JSON", "Choose a shared-storage folder · exact schema-v3 JSON · 8 MiB bound", R.drawable.ic_export) { model.exportRawTechnicalReport() }
            } }
            items(model.rawLeaves, key = { "raw:${it.path}" }) { leaf -> CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                CapabilityKeyValue(leaf.path, leaf.value)
                Text(leaf.type, color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                ExpressiveContainedIconTextButton("Evidence actions", R.drawable.ic_evidence) { state.selectedEvidence = leaf.path to leaf.value }
            } } }
        }
        9 -> {
            item {
                val networkAvailable = LocalValidatedNetwork.current
                CapabilitySectionCard("Compare with VulkanScope Database") {
                Text("Fetching is explicit and uses only the fixed official Database HTTPS origin. The downloaded public technicalReport is compared locally against the current technicalReport.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                ExpressiveSearchField(
                    value = state.databaseReportId,
                    onValueChange = { state.databaseReportId = it.lowercase().filter { ch -> ch in '0'..'9' || ch in 'a'..'f' }.take(64) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = networkAvailable,
                    placeholderText = "64-character report id"
                )
                CapabilityKeyValue("Status", state.databaseStatus)
                ExpressiveActionButton("Fetch public report", if (networkAvailable) "Explicit GET from the fixed official Database endpoint" else "Unavailable without a validated internet connection", R.drawable.ic_database_fetch, enabled = networkAvailable && !state.databaseLoading && state.databaseReportId.length == 64) { model.fetchDatabaseReport() }
                if (!networkAvailable) Text("Public Database report-id lookup is locked until Android reports a validated internet connection.", color = ComposeColor(0xFFFFC857), style = MaterialTheme.typography.bodySmall)
                if (state.databaseLoading) LoadingIndicator(color = VulkanAccentSoft, modifier = Modifier.size(32.dp))
                if (state.databaseRemoteLeaves.isNotEmpty()) ExpressiveToggleRow("Show unchanged", "Include identical structured JSON leaves.", state.databaseIncludeUnchanged) { state.databaseIncludeUnchanged = it }
            } }
            if (state.databaseRemoteLeaves.isNotEmpty()) {
                item { CapabilitySectionCard("Database comparison summary") {
                    ExpressiveMetric("Differences", model.databaseDiff.count { it.state != "Unchanged" }.toString())
                    ExpressiveMetric("Added locally", model.databaseDiff.count { it.state == "Added" }.toString())
                    ExpressiveMetric("Missing locally", model.databaseDiff.count { it.state == "Removed" }.toString())
                    Text("A difference is a report-evidence difference only; it is not a device ranking or market-share statement.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                } }
                items(model.databaseDiff.take(ANALYSIS_GLOBAL_SEARCH_VISIBLE_LIMIT), key = { "db:${it.key}" }) { row -> CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    CapabilityKeyValue(row.key, row.state)
                    row.baseline?.let { CapabilityKeyValue("Database", it) }
                    row.current?.let { CapabilityKeyValue("Current", it) }
                } } }
            }
        }
        10 -> {
            item { CapabilitySectionCard("Local session history") {
                CapabilityKeyValue("Retention", "${state.history.size} / $ANALYSIS_HISTORY_MAX_ITEMS sessions")
                CapabilityKeyValue("Status", state.historyStatus)
                Text("Completed analysis snapshots are compressed into private app storage with strict count/size bounds. No history is uploaded automatically.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExpressiveContainedIconTextButton("Clear all", R.drawable.ic_clear_all, modifier = Modifier.weight(1f), enabled = state.history.isNotEmpty()) { state.pendingHistoryDeleteAll = true }
                }
            } }
            items(state.history, key = { "history:${it.id}" }) { record -> CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                CapabilityKeyValue(historyLabel(record), record.driverMode)
                CapabilityKeyValue("GPU", record.deviceName)
                CapabilityKeyValue("App version", record.applicationVersion)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExpressiveContainedIconTextButton("Use as baseline", R.drawable.ic_baseline, modifier = Modifier.weight(1f)) { model.useHistoryAsBaseline(record) }
                    ExpressiveContainedIconTextButton("Delete", R.drawable.ic_delete, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold) { state.pendingHistoryDelete = record }
                }
            } } }
            if (state.history.isEmpty()) item { EmptyState("No completed local analysis session retained yet") }
        }
        11 -> {
            item { CapabilitySectionCard("Watched evidence") {
                ExpressiveSearchField(value = state.watchInput, onValueChange = { state.watchInput = it }, modifier = Modifier.fillMaxWidth(), placeholderText = "Extension, feature, limit, format or evidence token…")
                CapabilityKeyValue("Watch-list usage", "${state.watched.size} / $ANALYSIS_MAX_WATCHED")
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExpressiveContainedIconTextButton("Add to watch list", R.drawable.ic_watch_add, modifier = Modifier.weight(1f), enabled = model.canAddWatch) { model.addWatched(state.watchInput); state.watchInput = "" }
                    ExpressiveContainedIconTextButton("Clear all", R.drawable.ic_clear_all, modifier = Modifier.weight(1f), enabled = state.watched.isNotEmpty()) { state.pendingWatchDeleteAll = true }
                }
                ExpressiveSearchField(value = state.watchQuery, onValueChange = { state.watchQuery = it }, modifier = Modifier.fillMaxWidth(), placeholderText = "Filter watched entries…")
                ExpressiveFilterBar(listOf("All", "Matched", "Missing"), listOf("All", "Matched", "Missing").indexOf(state.watchStateFilter).coerceAtLeast(0)) { state.watchStateFilter = listOf("All", "Matched", "Missing")[it] }
            } }
            items(model.visibleWatched, key = { "watch:$it" }) { token ->
                val evidence = model.watchedEvidence[token] ?: (0 to emptyList())
                CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    CapabilityKeyValue(token, if (evidence.first == 0) "Not present in current snapshot" else "${evidence.first} matching evidence item(s)")
                    evidence.second.forEach { CapabilityKeyValue(it.first, it.second) }
                    ExpressiveContainedIconTextButton("Remove", R.drawable.ic_delete) { state.pendingWatchDelete = token }
                } }
            }
        }
        12 -> {
            item { CapabilitySectionCard("Diagnostic evidence score") {
                CapabilityKeyValue("Score", model.driverHealth.score?.let { "$it / 100" } ?: "Unavailable")
                CapabilityKeyValue("Interpretation", model.driverHealth.level)
                Text("This heuristic summarizes explicit collection/safety anomalies only. It is not Vulkan® conformance, a benchmark, GPU ranking, performance score or vendor-quality claim.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
            } }
            items(model.driverHealth.factors, key = { "quality:$it" }) { factor -> CapabilityItemCard { Text(factor, modifier = Modifier.padding(14.dp)) } }
        }
        13 -> {
            item { CapabilitySectionCard("Database permalink & QR") {
                CapabilityKeyValue("Target", if (model.lastSharedReportId.isBlank()) "VulkanScope Database" else "Last submitted report · ${model.lastSharedReportId.take(12)}…")
                CapabilityKeyValue("Permalink", model.sharedReportUrl)
                ExpressiveActionButton("Share link", "Android Sharesheet · no background upload", R.drawable.ic_share, trailingIcon = R.drawable.ic_open_external) { model.shareLink() }
                TransientActionButton("Copy link", "Copy permalink to clipboard", R.drawable.ic_copy) { runCatching { model.copyLink() }.isSuccess }
            } }
            item {
                Surface(color = VulkanSurfaceRaised, shape = MaterialTheme.shapes.extraLarge, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Scan to open the current VulkanScope Database permalink", color = VulkanTextPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                        Text("The QR payload is exactly the permalink shown above. The quiet zone and high-contrast code area are kept unobstructed for reliable scanning.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                        Surface(color = ComposeColor.White, shape = RoundedCornerShape(20.dp), modifier = Modifier.padding(4.dp)) {
                            VulkanQrCode(model.sharedReportUrl, Modifier.padding(14.dp).size(220.dp))
                        }
                    }
                }
            }
        }
        else -> {
            item { CapabilitySectionCard("Optional active tests") {
                Text("Active tests are isolated from capability collection. FAIL means the test failed; it does not rewrite the reported feature as Unsupported.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                CapabilityKeyValue("Target GPU", device?.let { "${it.name} · ${it.vendorId}:${it.deviceId}" } ?: "Unavailable")
                ExpressiveActionButton("Run Vulkan self-tests", "Selected GPU only · minimal VkDevice, SPIR-V shader-module, pipeline-layout and compute-pipeline creation", R.drawable.ic_self_test, enabled = !state.testRunning && model.selfTestsAvailable) { model.runSelfTests() }
                if (state.testRunning) LoadingIndicator(color = VulkanAccentSoft, modifier = Modifier.size(32.dp))
            } }
            state.testResult?.let { result ->
                item { CapabilitySectionCard("Self-test result") { CapabilityKeyValue("Status", result.optString("status", "unknown")); result.optString("reason").takeIf { it.isNotBlank() }?.let { CapabilityKeyValue("Reason", it) } } }
                val tests = result.optJSONArray("tests")
                if (tests != null) items((0 until tests.length()).mapNotNull { tests.optJSONObject(it) }) { test -> CapabilityItemCard { Column(Modifier.padding(14.dp)) {
                    CapabilityKeyValue(test.optString("name", "Test"), test.optString("status", "unknown"))
                    test.optString("detail").takeIf { it.isNotBlank() }?.let { CapabilityKeyValue("Detail", it) }
                } } }
            }
        }
    }
}

@Composable
private fun ChevronAffordance(label: String, contentDescription: String, onClick: () -> Unit) {
    Surface(
        color = VulkanAccentContainer,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.semantics { role = Role.Button }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(start = 12.dp, end = 2.dp, top = 2.dp, bottom = 2.dp)
        ) {
            Text(trademarkVulkanDisplayText(label), color = VulkanAccentSoft, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
            IconButton(onClick = onClick, modifier = Modifier.size(44.dp)) {
                Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = trademarkVulkanDisplayText(contentDescription), tint = VulkanAccentSoft, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DetailAffordance(onClick: () -> Unit) = ChevronAffordance("Details", "Open details", onClick)

@Composable
private fun ScrollableDetailDialog(title: String, onDismiss: () -> Unit, content: @Composable () -> Unit) = ExpressiveDetailDialog(title, onDismiss, content)

@Composable
private fun ExpressiveDetailDialog(title: String, onDismiss: () -> Unit, content: @Composable () -> Unit) {
    val scrollState = rememberScrollState()
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val horizontalMargin = if (configuration.screenWidthDp < 360) 10.dp else 18.dp
    val verticalMargin = if (configuration.screenHeightDp < 520) 8.dp else 16.dp
    val dialogMaxHeight = maxOf(300.dp, configuration.screenHeightDp.dp - verticalMargin * 2)
    val bodyMaxHeight = minOf(540.dp, maxOf(96.dp, dialogMaxHeight - 170.dp))
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize().padding(horizontal = horizontalMargin, vertical = verticalMargin), contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier.fillMaxWidth().widthIn(max = 560.dp).heightIn(max = dialogMaxHeight),
                shape = MaterialTheme.shapes.extraLarge,
                color = VulkanSurfaceRaised,
                tonalElevation = 4.dp,
                shadowElevation = 8.dp
            ) {
                Column(Modifier.fillMaxWidth().heightIn(max = dialogMaxHeight)) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Surface(shape = RoundedCornerShape(18.dp), color = VulkanAccentContainer) {
                            Icon(painterResource(R.drawable.ic_info), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.padding(10.dp).size(21.dp))
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = VulkanTextPrimary)
                            Text("Detailed Vulkan® evidence", style = MaterialTheme.typography.labelMedium, color = VulkanTextSecondary)
                        }
                    }
                    HorizontalDivider(color = VulkanOutlineVariant)
                    Box(Modifier.fillMaxWidth().weight(1f, fill = false).heightIn(min = 96.dp, max = bodyMaxHeight).padding(horizontal = 14.dp, vertical = 12.dp)) {
                        Column(
                            Modifier.fillMaxWidth().verticalScroll(scrollState).focusGroup(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CompositionLocalProvider(LocalDetailKeyValuePresentation provides true) { content() }
                        }
                        ExpressiveScrollHints(scrollState, Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 6.dp))
                    }
                    HorizontalDivider(color = VulkanOutlineVariant)
                    Row(
                        Modifier.fillMaxWidth().heightIn(min = 66.dp).padding(horizontal = 14.dp, vertical = 9.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ExpressiveContainedIconTextButton("Close", R.drawable.ic_close) { onDismiss() }
                    }
                }
            }
        }
    }
}

private val FORMAT_USAGE_FILTERS = linkedMapOf(
    "Sampled" to listOf("SAMPLED_IMAGE"),
    "Storage" to listOf("STORAGE_IMAGE"),
    "Color attachment" to listOf("COLOR_ATTACHMENT"),
    "Depth/stencil" to listOf("DEPTH_STENCIL_ATTACHMENT"),
    "Linear filtering" to listOf("SAMPLED_IMAGE_FILTER_LINEAR"),
    "Blit source" to listOf("BLIT_SRC"),
    "Blit destination" to listOf("BLIT_DST"),
    "Transfer source" to listOf("TRANSFER_SRC"),
    "Transfer destination" to listOf("TRANSFER_DST"),
    "Video decode" to listOf("VIDEO_DECODE"),
    "Video encode" to listOf("VIDEO_ENCODE"),
    "YCbCr / chroma" to listOf("YCBCR", "CHROMA_SAMPLES")
)

private fun formatMatchesUsageFilters(format: FormatEntry, selected: Set<String>): Boolean {
    if (selected.isEmpty()) return true
    val decoded = listOf(formatFeatureFlags(format.linear), formatFeatureFlags(format.optimal), formatFeatureFlags(format.buffer)).joinToString(" ")
    return selected.all { label -> FORMAT_USAGE_FILTERS[label].orEmpty().any { token -> decoded.contains(token, true) } }
}

@Composable
private fun FormatsPage(device: DeviceReport?) {
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<FormatEntry?>(null) }
    var usageFilters by remember { mutableStateOf<Set<String>>(emptySet()) }
    val formats = remember(device) { device?.formats ?: emptyList() }
    val filtered = remember(query, formats, usageFilters) { formats.filter { matchesFormatExplorerQuery(it, query) && formatMatchesUsageFilters(it, usageFilters) } }
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
            ExpressiveMetricGrid(listOf("Visible formats" to filtered.size.toString(), "Active usage filters" to usageFilters.size.toString()), Modifier.padding(top = 4.dp))
            Text("Use Details for full decoded/raw format evidence.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
            }
        }
        itemsIndexed(filtered, key = { index, format -> "format:${format.name}:$index" }) { _, format ->
            CapabilityItemCard(containerColor = VulkanSurfaceRaised) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(format.name, fontWeight = FontWeight.Medium)
                    CapabilityKeyValue("Status", if (format.supported) "SUPPORTED" else "NOT SUPPORTED")
                    CapabilityKeyValue("Linear", formatFeatureFlags(format.linear))
                    CapabilityKeyValue("Optimal", formatFeatureFlags(format.optimal))
                    CapabilityKeyValue("Buffer", formatFeatureFlags(format.buffer))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { DetailAffordance { selected = format } }
                }
            }
        }
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
            }
            ExpressiveMetricGrid(metrics)
        }
        if (filter == "Limits" || filter == "All") {
            limitGroups.toSortedMap().forEach { (category, entries) ->
                item { CapabilitySectionCard(category) {
                    val visible = entries.filter { it in visibleLimits }
                    if (visible.isEmpty()) Text("No matching limits", color = ComposeColor(0xFF9E9E9E))
                    visible.forEach { (name, value) -> CapabilityKeyValue(name, value) }
                } }
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
            itemsIndexed(filtered, key = { index, property -> "${property.section}:${property.name}:$index" }) { _, property ->
                CapabilityItemCard {
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
    return if (remaining != 0L) " | UNKNOWN_BITS=0x${remaining.toULong().toString(16).uppercase()}" else ""
}

private fun memoryHeapFlags(bits: Long): String = canonicalFlagNames(bits, listOf(
    0x1L to "VK_MEMORY_HEAP_DEVICE_LOCAL_BIT",
    0x2L to "VK_MEMORY_HEAP_MULTI_INSTANCE_BIT",
    0x8L to "VK_MEMORY_HEAP_TILE_MEMORY_BIT_QCOM"
))

private fun memoryTypeFlags(bits: Long): String = canonicalFlagNames(bits, listOf(
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

private fun queueCapabilityFlags(bits: Long): String {
    if (bits == 0L) return "0 · no VkQueueFlagBits reported"
    return canonicalFlagNames(bits, listOf(
        0x1L to "VK_QUEUE_GRAPHICS_BIT", 0x2L to "VK_QUEUE_COMPUTE_BIT", 0x4L to "VK_QUEUE_TRANSFER_BIT",
        0x8L to "VK_QUEUE_SPARSE_BINDING_BIT", 0x10L to "VK_QUEUE_PROTECTED_BIT", 0x20L to "VK_QUEUE_VIDEO_DECODE_BIT_KHR",
        0x40L to "VK_QUEUE_VIDEO_ENCODE_BIT_KHR", 0x100L to "VK_QUEUE_OPTICAL_FLOW_BIT_NV", 0x400L to "VK_QUEUE_DATA_GRAPH_BIT_ARM"
    ))
}

private fun videoCodecOperationFlags(bits: Long): String {
    if (bits == 0L) return "VK_VIDEO_CODEC_OPERATION_NONE_KHR"
    return canonicalFlagNames(bits, listOf(
        0x1L to "VK_VIDEO_CODEC_OPERATION_DECODE_H264_BIT_KHR", 0x2L to "VK_VIDEO_CODEC_OPERATION_DECODE_H265_BIT_KHR",
        0x4L to "VK_VIDEO_CODEC_OPERATION_DECODE_AV1_BIT_KHR", 0x8L to "VK_VIDEO_CODEC_OPERATION_DECODE_VP9_BIT_KHR",
        0x10000L to "VK_VIDEO_CODEC_OPERATION_ENCODE_H264_BIT_KHR", 0x20000L to "VK_VIDEO_CODEC_OPERATION_ENCODE_H265_BIT_KHR",
        0x40000L to "VK_VIDEO_CODEC_OPERATION_ENCODE_AV1_BIT_KHR"
    ))
}

private fun queueVideoCodecEvidenceRetained(queue: QueueEntry): Boolean = queue.videoCodecQueryStatus == "available" || queue.videoCodecQueryStatus == "incomplete"

private fun queueVideoCodecQueryState(queue: QueueEntry): String = when (queue.videoCodecQueryStatus) {
    "available" -> "Available"
    "incomplete" -> "Incomplete${queue.videoCodecQueryReason.takeIf { it.isNotBlank() }?.let { " · $it" } ?: ""}"
    "not_applicable" -> "Not applicable${queue.videoCodecQueryReason.takeIf { it.isNotBlank() }?.let { " · $it" } ?: ""}"
    "unavailable" -> "Unavailable${queue.videoCodecQueryReason.takeIf { it.isNotBlank() }?.let { " · $it" } ?: ""}"
    else -> "Unknown · VkQueueFamilyVideoPropertiesKHR query evidence is not available"
}

private fun parseUnsignedHexLong(value: String?): Long? = value?.let { runCatching { java.lang.Long.parseUnsignedLong(it, 16) }.getOrNull() }

private fun canonicalFlagNames(bits: Long, names: List<Pair<Long, String>>): String {
    if (bits == 0L) return "0"
    val known = names.filter { (bit, _) -> (bits and bit) != 0L }.map { it.second }
    return known.joinToString(" | ").ifBlank { "0" } + rawBitsSuffix(bits, names.map { it.first }.toSet())
}


private fun surfaceTransformFlags(bits: Long): String = canonicalFlagNames(bits, listOf(
    0x1L to "VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR", 0x2L to "VK_SURFACE_TRANSFORM_ROTATE_90_BIT_KHR",
    0x4L to "VK_SURFACE_TRANSFORM_ROTATE_180_BIT_KHR", 0x8L to "VK_SURFACE_TRANSFORM_ROTATE_270_BIT_KHR",
    0x10L to "VK_SURFACE_TRANSFORM_HORIZONTAL_MIRROR_BIT_KHR", 0x20L to "VK_SURFACE_TRANSFORM_HORIZONTAL_MIRROR_ROTATE_90_BIT_KHR",
    0x40L to "VK_SURFACE_TRANSFORM_HORIZONTAL_MIRROR_ROTATE_180_BIT_KHR", 0x80L to "VK_SURFACE_TRANSFORM_HORIZONTAL_MIRROR_ROTATE_270_BIT_KHR",
    0x100L to "VK_SURFACE_TRANSFORM_INHERIT_BIT_KHR"
))

private fun compositeAlphaFlags(bits: Long): String = canonicalFlagNames(bits, listOf(
    0x1L to "VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR", 0x2L to "VK_COMPOSITE_ALPHA_PRE_MULTIPLIED_BIT_KHR",
    0x4L to "VK_COMPOSITE_ALPHA_POST_MULTIPLIED_BIT_KHR", 0x8L to "VK_COMPOSITE_ALPHA_INHERIT_BIT_KHR"
))

private fun imageUsageFlags(bits: Long): String = canonicalFlagNames(bits, listOf(
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

private fun canonicalSurfaceCapabilityValue(name: String, value: String): String {
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
private fun formatFeatureFlags(bits: Long): String = canonicalFlagNames(bits, listOf(
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

private data class ProfileResult(
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
    val checkedRequirementCount: Int = 0
)

private enum class ProfilePropertyComparison { MINIMUM, MAXIMUM, EQUAL_BOOL, BITMASK_CONTAINS, VECTOR_MINIMUM }

private data class ProfileFeatureRequirement(val structure: String, val member: String)
private data class ProfilePropertyRequirement(val structure: String, val member: String, val comparison: ProfilePropertyComparison, val requiredValue: String)
private data class ProfileFormatRequirement(val format: String, val linearMask: Long = 0L, val optimalMask: Long = 0L, val bufferMask: Long = 0L)
private data class ProfileCapabilityRequirement(
    val extensions: List<String> = emptyList(),
    val features: List<ProfileFeatureRequirement> = emptyList(),
    val properties: List<ProfilePropertyRequirement> = emptyList(),
    val formats: List<ProfileFormatRequirement> = emptyList()
)
private data class ProfileAlternativeGroup(val label: String, val alternatives: List<ProfileCapabilityRequirement>)
private data class ProfileRequirements(
    val name: String,
    val revision: String,
    val minApiVersion: String,
    val capability: ProfileCapabilityRequirement,
    val requiredProfiles: List<String> = emptyList(),
    val alternativeGroups: List<ProfileAlternativeGroup> = emptyList(),
    val completeCoverage: Boolean = false,
    val coverageNote: String
)
private data class ProfileCatalogEntry(val name: String, val revision: String, val minApiVersion: String, val coverageNote: String)
private data class ProfileEvidenceResult(
    val missingExtensions: MutableList<String> = mutableListOf(),
    val unknownExtensions: MutableList<String> = mutableListOf(),
    val missingFeatures: MutableList<String> = mutableListOf(),
    val unknownFeatures: MutableList<String> = mutableListOf(),
    val failingProperties: MutableList<String> = mutableListOf(),
    val unknownProperties: MutableList<String> = mutableListOf(),
    val failingFormats: MutableList<String> = mutableListOf(),
    val unknownFormats: MutableList<String> = mutableListOf(),
    var checked: Int = 0
)

private val PROFILE_INSTANCE_EXTENSIONS = setOf(
    "VK_KHR_surface", "VK_KHR_android_surface", "VK_KHR_get_physical_device_properties2", "VK_KHR_get_surface_capabilities2",
    "VK_KHR_device_group_creation", "VK_KHR_external_fence_capabilities", "VK_KHR_external_memory_capabilities", "VK_KHR_external_semaphore_capabilities",
    "VK_KHR_surface_maintenance1", "VK_EXT_debug_report", "VK_EXT_debug_utils", "VK_EXT_surface_maintenance1", "VK_EXT_swapchain_colorspace",
    "VK_GOOGLE_surfaceless_query"
)

private fun profileFeatures(structure: String, vararg members: String): List<ProfileFeatureRequirement> = members.map { ProfileFeatureRequirement(structure, it) }
private fun profileProperties(structure: String, comparison: ProfilePropertyComparison, vararg values: Pair<String, String>): List<ProfilePropertyRequirement> = values.map { ProfilePropertyRequirement(structure, it.first, comparison, it.second) }
private fun profileCapability(
    extensions: List<String> = emptyList(),
    features: List<ProfileFeatureRequirement> = emptyList(),
    properties: List<ProfilePropertyRequirement> = emptyList(),
    formats: List<ProfileFormatRequirement> = emptyList()
) = ProfileCapabilityRequirement(extensions, features, properties, formats)

private fun mergeProfileCapabilities(vararg capabilities: ProfileCapabilityRequirement): ProfileCapabilityRequirement = ProfileCapabilityRequirement(
    extensions = capabilities.flatMap { it.extensions }.distinct(),
    features = capabilities.flatMap { it.features }.distinct(),
    properties = capabilities.flatMap { it.properties }.distinct(),
    formats = capabilities.flatMap { it.formats }.distinct()
)

private val ANDROID_4444_FORMAT_REQUIREMENTS = listOf(
    ProfileFormatRequirement("VK_FORMAT_A4B4G4R4_UNORM_PACK16_EXT", linearMask = 0xC880L, optimalMask = 0xCC81L),
    ProfileFormatRequirement("VK_FORMAT_A4R4G4B4_UNORM_PACK16_EXT", linearMask = 0xC880L, optimalMask = 0xCC81L)
)

private fun android2025Requirements(): ProfileRequirements = ProfileRequirements(
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

private fun android15Requirements(): ProfileRequirements = ProfileRequirements(
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

private fun android16Requirements(): ProfileRequirements = ProfileRequirements(
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

private fun android17Requirements(): ProfileRequirements = ProfileRequirements(
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

private fun roadmap2022Capability(): ProfileCapabilityRequirement = profileCapability(
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

private fun roadmap2024BaseCapability(): ProfileCapabilityRequirement = profileCapability(
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

private fun roadmap2024PromotedVulkan14Capability(): ProfileCapabilityRequirement = profileCapability(
    extensions = listOf("VK_KHR_dynamic_rendering", "VK_KHR_shader_subgroup_rotate", "VK_KHR_shader_float_controls2", "VK_KHR_shader_expect_assume", "VK_KHR_line_rasterization", "VK_KHR_vertex_attribute_divisor", "VK_KHR_index_type_uint8", "VK_KHR_maintenance5", "VK_KHR_dynamic_rendering_local_read", "VK_KHR_push_descriptor"),
    features = profileFeatures("VkPhysicalDeviceShaderSubgroupRotateFeaturesKHR", "shaderSubgroupRotate") +
        profileFeatures("VkPhysicalDeviceShaderFloatControls2FeaturesKHR", "shaderFloatControls2") +
        profileFeatures("VkPhysicalDeviceShaderExpectAssumeFeaturesKHR", "shaderExpectAssume") +
        profileFeatures("VkPhysicalDeviceVertexAttributeDivisorFeaturesKHR", "vertexAttributeInstanceRateDivisor") +
        profileFeatures("VkPhysicalDeviceIndexTypeUint8FeaturesKHR", "indexTypeUint8") +
        profileFeatures("VkPhysicalDeviceDynamicRenderingLocalReadFeaturesKHR", "dynamicRenderingLocalRead") +
        profileFeatures("VkPhysicalDeviceMaintenance5FeaturesKHR", "maintenance5")
)

private fun roadmap2024Capability(): ProfileCapabilityRequirement = mergeProfileCapabilities(roadmap2024BaseCapability(), roadmap2024PromotedVulkan14Capability())

private fun roadmapLineAlternatives(structure: String): ProfileAlternativeGroup = ProfileAlternativeGroup("one required line-rasterization mode", listOf("rectangularLines", "bresenhamLines", "smoothLines", "stippledRectangularLines", "stippledBresenhamLines", "stippledSmoothLines").map { member ->
    profileCapability(extensions = listOf("VK_KHR_line_rasterization"), features = profileFeatures(structure, member))
})

private fun roadmap2026Capability(): ProfileCapabilityRequirement = profileCapability(
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

private fun roadmap2026ProfileCapability(): ProfileCapabilityRequirement = mergeProfileCapabilities(
    roadmap2022Capability(),
    roadmap2024BaseCapability(),
    roadmap2026Capability()
)

private fun vulkanProfileRequirements(): List<ProfileRequirements> = listOf(
    android2025Requirements(),
    android15Requirements(),
    android16Requirements(),
    android17Requirements(),
    ProfileRequirements("VP_KHR_roadmap_2022", "r.2", "1.3.204", roadmap2022Capability(), completeCoverage = false, coverageNote = "Current Roadmap profile version 2 required feature/property/extension set is mapped from the audited 2026-05-21 revision; PASS remains disabled until generated source-equivalence is independently proven"),
    ProfileRequirements("VP_KHR_roadmap_2024", "r.2", "1.3.276", roadmap2024Capability(), requiredProfiles = listOf("VP_KHR_roadmap_2022"), alternativeGroups = listOf(roadmapLineAlternatives("VkPhysicalDeviceLineRasterizationFeaturesKHR")), completeCoverage = false, coverageNote = "Current Roadmap profile version 2 direct requirements, Roadmap 2022 inheritance and line-mode OR group are mapped; PASS remains disabled until generated source-equivalence is independently proven"),
    ProfileRequirements("VP_KHR_roadmap_2026", "r.2", "1.4.328", roadmap2026ProfileCapability(), alternativeGroups = listOf(roadmapLineAlternatives("VkPhysicalDeviceVulkan14Features")), completeCoverage = false, coverageNote = "Current Roadmap profile version 2 exact capability composition is normalized: base/2022 capabilities, Roadmap 2024 direct capabilities, Roadmap 2026 capabilities and only the 2026 Vulkan 1.4 line-mode OR group. Roadmap 2024 promoted-v1.4 and 2024 line alternatives are intentionally not inherited because the official 2026 profile does not compose them; PASS remains disabled until generated source-equivalence is independently proven")
)

private fun vulkanProfileCatalog(): List<ProfileCatalogEntry> = listOf(
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

private fun extensionPromotedToSatisfied(name: String, deviceApiVersion: String): Boolean {
    val promoted = vulkanExtensionReference(name).promotedTo
    val match = Regex("Vulkan\\s+(\\d+)\\.(\\d+)", RegexOption.IGNORE_CASE).find(promoted) ?: return false
    return apiAtLeast(deviceApiVersion, match.groupValues[1].toInt(), match.groupValues[2].toInt())
}

private fun profileFeatureEvidenceKeys(requirement: ProfileFeatureRequirement): List<String> {
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

private fun profilePropertyValue(device: DeviceReport, requirement: ProfilePropertyRequirement): String? {
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

private fun profileDecimal(value: String): java.math.BigDecimal? = Regex("-?\\d+(?:\\.\\d+)?").find(value)?.value?.toBigDecimalOrNull()
private fun profileInteger(value: String): java.math.BigInteger? {
    val hex = Regex("0x([0-9A-Fa-f]+)").find(value)
    if (hex != null) return runCatching { java.math.BigInteger(hex.groupValues[1], 16) }.getOrNull()
    return Regex("-?\\d+").find(value)?.value?.let { runCatching { java.math.BigInteger(it) }.getOrNull() }
}
private fun profileVector(value: String): List<java.math.BigDecimal> = Regex("-?\\d+(?:\\.\\d+)?").findAll(value).mapNotNull { it.value.toBigDecimalOrNull() }.toList()
private fun profileBoolean(value: String): Boolean? = when (value.trim().lowercase()) { "true" -> true; "false" -> false; else -> null }

private fun profilePropertyState(device: DeviceReport, requirement: ProfilePropertyRequirement): Pair<String, String> {
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

private fun profileCanonicalFormatName(name: String): String = when (name) {
    "VK_FORMAT_A4B4G4R4_UNORM_PACK16_EXT" -> "VK_FORMAT_A4B4G4R4_UNORM_PACK16"
    "VK_FORMAT_A4R4G4B4_UNORM_PACK16_EXT" -> "VK_FORMAT_A4R4G4B4_UNORM_PACK16"
    else -> name
}

private fun evaluateProfileCapability(report: VulkanReport?, device: DeviceReport, capability: ProfileCapabilityRequirement): ProfileEvidenceResult {
    val evidence = ProfileEvidenceResult()
    val instanceExtensions = report?.instanceExtensions?.map { it.name }?.toSet().orEmpty()
    val deviceExtensions = device.extensions.map { it.name }.toSet()
    capability.extensions.forEach { extension ->
        evidence.checked += 1
        if (extension in instanceExtensions || extension in deviceExtensions || extensionPromotedToSatisfied(extension, device.apiVersion)) return@forEach
        val complete = if (extension in PROFILE_INSTANCE_EXTENSIONS) report?.instanceExtensionStatus == "available" else device.deviceExtensionStatus == "available"
        if (complete) evidence.missingExtensions += extension else evidence.unknownExtensions += extension
    }
    val featureMap = device.features.associateBy { it.name.trim() }
    capability.features.forEach { requirement ->
        evidence.checked += 1
        val entries = profileFeatureEvidenceKeys(requirement).mapNotNull { featureMap[it] }
        when {
            entries.any { it.supported } -> Unit
            entries.isNotEmpty() -> evidence.missingFeatures += "${requirement.structure}.${requirement.member}"
            else -> evidence.unknownFeatures += "${requirement.structure}.${requirement.member}"
        }
    }
    capability.properties.forEach { requirement ->
        evidence.checked += 1
        val state = profilePropertyState(device, requirement)
        if (state.first == "FAIL") evidence.failingProperties += state.second
        if (state.first == "UNKNOWN") evidence.unknownProperties += state.second
    }
    val formats = device.formats.associateBy { profileCanonicalFormatName(it.name) }
    capability.formats.forEach { requirement ->
        evidence.checked += 1
        val canonical = profileCanonicalFormatName(requirement.format)
        val actual = formats[canonical]
        if (actual == null) {
            evidence.unknownFormats += requirement.format
        } else {
            val missingLinear = (actual.linear and requirement.linearMask) != requirement.linearMask
            val missingOptimal = (actual.optimal and requirement.optimalMask) != requirement.optimalMask
            val missingBuffer = (actual.buffer and requirement.bufferMask) != requirement.bufferMask
            if (missingLinear || missingOptimal || missingBuffer) evidence.failingFormats += requirement.format
        }
    }
    return evidence
}

private fun profileEvidenceState(evidence: ProfileEvidenceResult): String = when {
    evidence.missingExtensions.isNotEmpty() || evidence.missingFeatures.isNotEmpty() || evidence.failingProperties.isNotEmpty() || evidence.failingFormats.isNotEmpty() -> "FAIL"
    evidence.unknownExtensions.isNotEmpty() || evidence.unknownFeatures.isNotEmpty() || evidence.unknownProperties.isNotEmpty() || evidence.unknownFormats.isNotEmpty() -> "UNKNOWN"
    else -> "PASS"
}

private fun evaluateProfile(report: VulkanReport?, device: DeviceReport?, requirements: ProfileRequirements, allRequirements: Map<String, ProfileRequirements>, stack: Set<String> = emptySet()): ProfileResult {
    if (device == null) return ProfileResult(requirements.name, requirements.revision, "UNKNOWN", emptyList(), requirements.capability.extensions, emptyList(), requirements.capability.features.map { "${it.structure}.${it.member}" }, emptyList(), requirements.capability.properties.map { "${it.structure}.${it.member}" }, coverageNote = requirements.coverageNote, minimumApiVersion = requirements.minApiVersion)
    if (requirements.name in stack) return ProfileResult(requirements.name, requirements.revision, "UNKNOWN", emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), coverageNote = "Required-profile cycle rejected", minimumApiVersion = requirements.minApiVersion, unknownRequirementGroups = listOf("required-profile cycle"))
    val evidence = evaluateProfileCapability(report, device, requirements.capability)
    val failingGroups = mutableListOf<String>()
    val unknownGroups = mutableListOf<String>()
    requirements.requiredProfiles.forEach { requiredName ->
        evidence.checked += 1
        val required = allRequirements[requiredName]
        if (required == null) {
            unknownGroups += "Required profile $requiredName is cataloged but not mapped"
        } else {
            val result = evaluateProfile(report, device, required, allRequirements, stack + requirements.name)
            evidence.checked += result.checkedRequirementCount
            when (result.status) {
                "FAIL" -> failingGroups += "Required profile $requiredName failed"
                "PASS" -> Unit
                else -> unknownGroups += "Required profile $requiredName is unresolved"
            }
        }
    }
    requirements.alternativeGroups.forEach { group ->
        val alternativeEvidence = group.alternatives.map { evaluateProfileCapability(report, device, it) }
        evidence.checked += alternativeEvidence.sumOf { it.checked }
        val states = alternativeEvidence.map { profileEvidenceState(it) }
        when {
            states.any { it == "PASS" } -> Unit
            states.all { it == "FAIL" } -> failingGroups += group.label
            else -> unknownGroups += group.label
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
        failingFormats = evidence.failingFormats.distinct(), unknownFormats = evidence.unknownFormats.distinct(), failingRequirementGroups = failingGroups.distinct(), unknownRequirementGroups = unknownGroups.distinct(), checkedRequirementCount = evidence.checked
    )
}

private fun vulkanProfileEvaluations(report: VulkanReport?, device: DeviceReport?): List<ProfileResult> {
    val requirements = vulkanProfileRequirements()
    val mapped = requirements.associateBy { it.name }
    val evaluated = requirements.map { evaluateProfile(report, device, it, mapped) }
    val evaluatedNames = evaluated.map { it.name }.toSet()
    val catalogOnly = vulkanProfileCatalog().filterNot { it.name in evaluatedNames }.map { entry ->
        ProfileResult(entry.name, entry.revision, "UNKNOWN", emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), coverageNote = entry.coverageNote, minimumApiVersion = entry.minApiVersion, unknownRequirementGroups = listOf("Authoritative requirements are not mapped in this release"))
    }
    return vulkanProfileCatalog().mapNotNull { catalog -> (evaluated + catalogOnly).firstOrNull { it.name == catalog.name } }
}

private fun profileSummary(result: ProfileResult): String = buildString {
    append(result.status)
    if (result.checkedRequirementCount > 0) append(" · ${result.checkedRequirementCount} mapped requirement check(s)")
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

@Composable
private fun ProfilesPage(report: VulkanReport, device: DeviceReport?) {
    var query by remember { mutableStateOf("") }
    val results = remember(report, device) { vulkanProfileEvaluations(report, device) }
    val filtered = results.filter { it.name.contains(query, true) }
    VulkanLazyPage(verticalSpacing = 8.dp) {
        item {
            CapabilitySectionCard("Profile explorer") {
                Text("Profile requirements are checked from struct-qualified runtime evidence and audited normalized official definitions. Missing evidence remains UNKNOWN; catalog-only definitions and coverage-limited mappings never become API-only PASS claims.", color = ComposeColor(0xFFB6ACAE), style = MaterialTheme.typography.bodySmall)
                ExpressiveSearchField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), placeholderText = "Search profiles…")
            }
        }
        items(filtered) { result ->
            CapabilityItemCard {
                Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(result.name, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                        CapabilityStatusBadge(result.status, when (result.status) { "PASS" -> true; "FAIL" -> false; else -> null })
                    }
                    val apiText = if (result.minimumApiVersion.isBlank()) result.revision else "${result.revision} · Vulkan® ${result.minimumApiVersion} minimum"
                    Text(apiText, color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.labelMedium)
                    Text(profileSummary(result), style = MaterialTheme.typography.bodySmall)
                    if (result.missingExtensions.isNotEmpty()) CapabilityKeyValue("Verified missing extensions", result.missingExtensions.joinToString(", "))
                    if (result.unknownExtensions.isNotEmpty()) CapabilityKeyValue("Unknown extension requirements", result.unknownExtensions.joinToString(", "))
                    if (result.missingFeatures.isNotEmpty()) CapabilityKeyValue("Unsupported features", result.missingFeatures.joinToString(", "))
                    if (result.unknownFeatures.isNotEmpty()) CapabilityKeyValue("Unavailable feature queries", result.unknownFeatures.joinToString(", "))
                    if (result.failingLimits.isNotEmpty()) CapabilityKeyValue("Failing properties / limits", result.failingLimits.joinToString("; "))
                    if (result.unknownLimits.isNotEmpty()) CapabilityKeyValue("Unavailable properties / limits", result.unknownLimits.joinToString(", "))
                    if (result.failingFormats.isNotEmpty()) CapabilityKeyValue("Failing formats", result.failingFormats.joinToString(", "))
                    if (result.unknownFormats.isNotEmpty()) CapabilityKeyValue("Unavailable formats", result.unknownFormats.joinToString(", "))
                    if (result.failingRequirementGroups.isNotEmpty()) CapabilityKeyValue("Failed inherited / OR requirements", result.failingRequirementGroups.joinToString("; "))
                    if (result.unknownRequirementGroups.isNotEmpty()) CapabilityKeyValue("Unresolved inherited / OR requirements", result.unknownRequirementGroups.joinToString("; "))
                    if (result.coverageNote.isNotBlank()) CapabilityKeyValue("Evaluator coverage", result.coverageNote)
                }
            }
        }
    }
}

private data class LibraryVersionInfo(val name: String, val version: String, val detail: String, val licenseName: String, val licenseAsset: String)

private val VULKANSCOPE_LIBRARY_VERSIONS = listOf(
    LibraryVersionInfo("AndroidX Core KTX", "1.19.0", "Android application support", "Apache License 2.0", "licenses/apache_2_0.md"),
    LibraryVersionInfo("AndroidX Activity Compose", "1.13.0", "Compose activity integration", "Apache License 2.0", "licenses/apache_2_0.md"),
    LibraryVersionInfo("Compose UI", "1.12.0", "Compose UI runtime", "Apache License 2.0", "licenses/apache_2_0.md"),
    LibraryVersionInfo("Compose Foundation", "1.12.0", "Compose foundation components", "Apache License 2.0", "licenses/apache_2_0.md"),
    LibraryVersionInfo("Compose Animation", "1.12.0", "Compose animation primitives", "Apache License 2.0", "licenses/apache_2_0.md"),
    LibraryVersionInfo("Material 3", "1.5.0-alpha27", "Material 3 and expressive components", "Apache License 2.0", "licenses/apache_2_0.md"),
    LibraryVersionInfo("Lifecycle Runtime Compose", "2.11.0", "Lifecycle-aware Compose state", "Apache License 2.0", "licenses/apache_2_0.md"),
    LibraryVersionInfo("OkHttp", "5.5.0", "Explicit network requests", "Apache License 2.0", "licenses/apache_2_0.md"),
    LibraryVersionInfo("ZXing Core", "3.5.4", "Local QR code generation", "Apache License 2.0", "licenses/apache_2_0.md"),
    LibraryVersionInfo("Vulkan® Headers", "1.4.362", "Pinned commit ee2ec5fd83dafce291024683b50dc89219333076", "Apache-2.0 OR MIT", "licenses/vulkan_headers.md"),
    LibraryVersionInfo("libadrenotools", "8fae8ce254dfc1344527e05301e43f37dea2df80", "arm64-v8a driver-loading integration · pinned commit", "BSD 2-Clause License", "licenses/libadrenotools_bsd_2_clause.md")
)

@Composable
private fun LibraryLicenseDialog(library: LibraryVersionInfo, onDismiss: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val horizontalMargin = if (configuration.screenWidthDp < 360) 10.dp else 18.dp
    val verticalMargin = if (configuration.screenHeightDp < 520) 8.dp else 16.dp
    val dialogMaxHeight = maxOf(320.dp, configuration.screenHeightDp.dp - verticalMargin * 2)
    var markdown by remember(library.licenseAsset) { mutableStateOf<String?>(null) }
    LaunchedEffect(library.licenseAsset) {
        markdown = withContext(Dispatchers.IO) {
            runCatching { context.assets.open(library.licenseAsset).bufferedReader().use { it.readText() } }
                .getOrElse { "# License unavailable\n\nThe packaged license document could not be read." }
        }
    }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize().padding(horizontal = horizontalMargin, vertical = verticalMargin), contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier.fillMaxWidth().widthIn(max = 620.dp).heightIn(max = dialogMaxHeight),
                shape = MaterialTheme.shapes.extraLarge,
                color = VulkanSurfaceRaised,
                tonalElevation = 4.dp,
                shadowElevation = 8.dp
            ) {
                Column(Modifier.fillMaxWidth().heightIn(max = dialogMaxHeight)) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Surface(shape = RoundedCornerShape(18.dp), color = VulkanAccentContainer) {
                            Icon(painterResource(R.drawable.ic_action_text), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.padding(10.dp).size(21.dp))
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("${library.name} license", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = VulkanTextPrimary)
                            Text(library.licenseName, style = MaterialTheme.typography.labelMedium, color = VulkanTextSecondary)
                        }
                    }
                    HorizontalDivider(color = VulkanOutlineVariant)
                    Surface(
                        modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 14.dp, vertical = 12.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = ComposeColor(0xFF0D0D0D),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant)
                    ) {
                        val text = markdown
                        if (text == null) {
                            Box(Modifier.fillMaxSize().padding(18.dp), contentAlignment = Alignment.Center) {
                                Text("Loading license…", color = VulkanTextSecondary, style = MaterialTheme.typography.bodyMedium)
                            }
                        } else {
                            ReleaseNotesContent(text, Modifier.fillMaxSize())
                        }
                    }
                    HorizontalDivider(color = VulkanOutlineVariant)
                    Row(
                        Modifier.fillMaxWidth().heightIn(min = 66.dp).padding(horizontal = 14.dp, vertical = 9.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ExpressiveContainedIconTextButton("Close", R.drawable.ic_close, onClick = onDismiss)
                    }
                }
            }
        }
    }
}


@Composable
private fun InfoPage(report: VulkanReport, display: DisplayReport, mode: DriverMode, collectionStatus: CollectionStatus, onCheckForUpdates: () -> Unit, directUpdatesEnabled: Boolean, updateCheckInFlight: Boolean, showInfo: Boolean = true, showReporting: Boolean = true) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val networkAvailable = LocalValidatedNetwork.current
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()
    val installedAbi = remember { detectInstalledAbi(context) }
    val packageInfo = remember { runCatching { context.packageManager.getPackageInfo(context.packageName, 0) }.getOrNull() }
    val versionName = packageInfo?.versionName ?: "Unknown"
    val versionCode = if (packageInfo != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) packageInfo.longVersionCode.toString() else {
        @Suppress("DEPRECATION")
        packageInfo?.versionCode?.toString() ?: "Unknown"
    }
    val completeReportReady = isCompleteReportReady(report, collectionStatus)
    var submissionState by remember { mutableStateOf("Ready") }
    var submissionInFlight by remember { mutableStateOf(false) }
    var submissionSuccessId by remember { mutableStateOf<String?>(null) }
    var submissionFailureLog by remember { mutableStateOf<String?>(null) }
    var selectedLibraryLicense by remember { mutableStateOf<LibraryVersionInfo?>(null) }
    val exportStem = remember(report) { exportFileStem(report) }
    var pendingExportFilename by rememberSaveable { mutableStateOf("") }
    var pendingExportPath by rememberSaveable { mutableStateOf("") }
    var pendingExportMime by rememberSaveable { mutableStateOf("") }
    var exportPreparing by remember { mutableStateOf(false) }
    fun pendingSnapshot(): ExportSnapshot? {
        if (pendingExportFilename.isBlank() || pendingExportPath.isBlank() || pendingExportMime !in setOf("text/plain", "text/html")) return null
        return ExportSnapshot(pendingExportFilename, pendingExportPath, pendingExportMime)
    }
    fun resetPendingSnapshot() {
        pendingExportFilename = ""
        pendingExportPath = ""
        pendingExportMime = ""
    }
    fun discardPendingSnapshot() {
        val snapshot = pendingSnapshot()
        resetPendingSnapshot()
        if (snapshot != null) scope.launch { withContext(Dispatchers.IO) { deleteExportSnapshot(context, snapshot) } }
    }
    fun prepareReportStorageExport(mime: String) {
        if (!completeReportReady || exportPreparing || pendingExportPath.isNotBlank() || mime !in setOf("text/plain", "text/html")) return
        exportPreparing = true
        val isHtml = mime == "text/html"
        val filename = "$exportStem.${if (isHtml) "html" else "txt"}"
        scope.launch {
            try {
                val snapshot = withContext(Dispatchers.IO) {
                    createExportSnapshot(context, filename, mime) {
                        if (isHtml) reportToHtml(context, report, display, mode) else reportToText(context, report, display, mode)
                    }
                }
                pendingExportFilename = snapshot.filename
                pendingExportPath = snapshot.path
                pendingExportMime = snapshot.mime
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Throwable) {
                Log.e("VulkanScope", "Report snapshot creation failed", error)
                android.widget.Toast.makeText(context, "${if (isHtml) "HTML" else "TXT"} report could not be prepared", android.widget.Toast.LENGTH_SHORT).show()
            } finally {
                exportPreparing = false
            }
        }
    }
    val registryCoverage = report.registryCoverage
    LaunchedEffect(Unit) { withContext(Dispatchers.IO) { cleanupStaleExportSnapshots(context) } }
    submissionFailureLog?.let { log ->
        DatabaseSubmissionFailureDialog(log = log, onDismiss = { submissionFailureLog = null })
    }
    selectedLibraryLicense?.let { library ->
        LibraryLicenseDialog(library = library, onDismiss = { selectedLibraryLicense = null })
    }
    pendingSnapshot()?.let { snapshot ->
        val mime = snapshot.mime
        val isHtml = mime == "text/html"
        SharedStorageBrowserDialog(
            request = SharedStorageBrowserRequest(
                title = if (isHtml) "Export HTML report" else "Export TXT report",
                description = "Choose a shared-storage folder for the complete ${if (isHtml) "offline HTML" else "plain-text"} VulkanScope report.",
                mode = SharedStorageBrowserMode.EXPORT,
                allowedExtensions = setOf(if (isHtml) "html" else "txt"),
                suggestedFileName = snapshot.filename
            ),
            onDismiss = { discardPendingSnapshot() },
            onExport = { destination ->
                try {
                    val saved = withContext(Dispatchers.IO) {
                        val target = validatedSharedStorageDestination(
                            destination.parentFile ?: error("Destination folder is unavailable"),
                            destination.name,
                            setOf(if (isHtml) "html" else "txt")
                        )
                        val source = validatedExportSnapshot(context, snapshot)
                        copySharedStorageFile(target, source)
                        target
                    }
                    android.widget.Toast.makeText(context, "${if (isHtml) "HTML" else "TXT"} report saved to ${saved.absolutePath}", android.widget.Toast.LENGTH_LONG).show()
                    Result.success("Saved ${saved.name}")
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (error: Throwable) {
                    Result.failure(error)
                }
            }
        )
    }
    VulkanLazyPage(verticalSpacing = 14.dp) {
        if (showInfo) item {
            CapabilitySectionCard("Developer") {
                ExpressiveIdentityBlock("Semih Boran", "EFI Shell · VulkanScope developer", R.drawable.ic_person)
                ExpressiveExternalLinkRow("Open GitHub profile", if (networkAvailable) "EFIShell0 · Projects and public profile" else "Unavailable without a validated internet connection", R.drawable.ic_action_github, enabled = networkAvailable) { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/EFIShell0"))) } }
            }
        }
        if (showInfo) item {
            CapabilitySectionCard("Application") {
                ExpressiveVersionBlock("VulkanScope", versionName, versionCode, context.packageName, installedAbi)
                ExpressiveActionButton("Check for updates", when { !directUpdatesEnabled -> "Direct GitHub updates are disabled in Settings"; !networkAvailable -> "Unavailable without a validated internet connection"; !updateCheckInFlight -> "Official EFIShell0/VulkanScope GitHub release channel"; else -> "Checking official release channel…" }, R.drawable.ic_download, enabled = directUpdatesEnabled && networkAvailable && !updateCheckInFlight, trailingIcon = R.drawable.ic_receive, onClick = onCheckForUpdates)
                ExpressiveExternalLinkRow("Open GitHub repository", if (networkAvailable) "Source, releases and project history" else "Unavailable without a validated internet connection", R.drawable.ic_action_github, enabled = networkAvailable) { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/EFIShell0/VulkanScope"))) } }
                Text(if (directUpdatesEnabled) "Direct update checks use the official VulkanScope GitHub release channel. APK download still requires explicit review and confirmation." else "Direct GitHub update checks are currently disabled. Obtainium can manage updates from the official GitHub Releases source without VulkanScope running its own update discovery.", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
            }
        }
        if (showInfo) item {
            CapabilitySectionCard("Libraries") {
                Text("Direct application and native library identities are reported from the release's pinned build configuration. Build tools are listed separately and are not presented as runtime libraries.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                VULKANSCOPE_LIBRARY_VERSIONS.forEach { library ->
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        CapabilityKeyValue(library.name, library.version)
                        Text(trademarkVulkanDisplayText(library.detail), color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                        Text(library.licenseName, color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            ChevronAffordance("License", "Open ${library.name} license") { selectedLibraryLicense = library }
                        }
                    }
                }
            }
        }
        if (showInfo) item {
            CapabilitySectionCard("Build toolchain") {
                CapabilityKeyValue("Android Gradle Plugin", "9.4.0")
                CapabilityKeyValue("Kotlin Compose plugin", "2.4.10")
                CapabilityKeyValue("Gradle wrapper", "9.7.1")
                CapabilityKeyValue("Android NDK", "29.0.14206865")
                CapabilityKeyValue("C++ language level", "C++20")
                CapabilityKeyValue("CMake minimum", "3.22.1")
            }
        }
        if (showInfo) item {
            CapabilitySectionCard("Device ABI") {
                CapabilityKeyValue("Installed ABI", installedAbi)
                CapabilityKeyValue("Supported ABIs", Build.SUPPORTED_ABIS.joinToString(", "))
                Text("Installed ABI is the native ABI used by this VulkanScope installation; supported ABIs are the ABIs reported by Android for the device.", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
            }
        }
        if (showInfo) item {
            CapabilitySectionCard("Android") {
                CapabilityKeyValue("Manufacturer", Build.MANUFACTURER)
                CapabilityKeyValue("Brand", Build.BRAND)
                CapabilityKeyValue("Model", Build.MODEL)
                CapabilityKeyValue("Android", Build.VERSION.RELEASE)
                CapabilityKeyValue("SDK", Build.VERSION.SDK_INT.toString())
                CapabilityKeyValue("Security patch", Build.VERSION.SECURITY_PATCH.ifBlank { "Unavailable" })
                CapabilityKeyValue("Codename", Build.VERSION.CODENAME)
                CapabilityKeyValue("Product", Build.PRODUCT)
                CapabilityKeyValue("Device", Build.DEVICE)
                CapabilityKeyValue("Board", Build.BOARD)
                CapabilityKeyValue("Hardware", Build.HARDWARE)
                CapabilityKeyValue("Build ID", Build.ID)
                CapabilityKeyValue("Incremental", Build.VERSION.INCREMENTAL)
                CapabilityKeyValue("Build fingerprint", Build.FINGERPRINT)
            }
        }
        if (showInfo) item {
            CapabilitySectionCard("Vulkan registry / query engine") {
                CapabilityKeyValue("Baseline", registryCoverage.baseline)
                CapabilityKeyValue("Engine", registryCoverage.mode)
                CapabilityKeyValue("Physical-device structs", registryCoverage.implementedPhysicalDeviceStructCount.toString())
                CapabilityKeyValue("Validated query groups", registryCoverage.validatedRuntimeQueryGroupCount.toString())
                CapabilityKeyValue("Runtime registry token references", registryCoverage.runtimeRegistryTokenReferenceCount.toString())
                CapabilityKeyValue("Catalog schema", registryCoverage.catalogSchemaVersion.toString())
                CapabilityKeyValue("Registry report schema", registryCoverage.reportSchema)
                CapabilityKeyValue("Header baseline", registryCoverage.headerBaseline)
                CapabilityKeyValue("Instance dependency candidates", registryCoverage.instanceDependencyCandidateCount.toString())
                Text("Registry metadata is build-time/offline. Runtime does not download or parse the Khronos registry. Unknown structures remain unavailable unless a validated native query path exists.", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
            }
        }
        if (showInfo) item {
            CapabilitySectionCard("About") {
                Text("VulkanScope is not an official Khronos Group project.", color = ComposeColor(0xFFFFC857), fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Text("VulkanScope is a Vulkan® capability and device inspection utility for Android. It reports information exposed by the active Vulkan® implementation and selected driver mode while keeping Android display/HDR evidence separate from Vulkan® capability claims.", color = ComposeColor(0xFFB0B0B0))
            }
        }
        if (showReporting) item {
            CapabilitySectionCard("Export complete report") {
                Text("TXT/HTML export uses VulkanScope's in-app shared-storage browser. Storage access is requested only when you explicitly start an export. Report serialization runs off the UI thread through a private temporary snapshot and the destination write is atomic.", color = ComposeColor(0xFFB6ACAE), style = MaterialTheme.typography.bodySmall)
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SharedStoragePermissionActionButton(
                        "Export TXT",
                        when { exportPreparing -> "Preparing private report snapshot…"; completeReportReady -> "Plain-text complete report · choose shared-storage destination"; else -> "Waiting for complete Vulkan® collection" },
                        R.drawable.ic_action_text,
                        Modifier.fillMaxWidth(),
                        completeReportReady && !exportPreparing && pendingExportPath.isBlank(),
                        false
                    ) { prepareReportStorageExport("text/plain") }
                    SharedStoragePermissionActionButton(
                        "Export HTML",
                        when { exportPreparing -> "Preparing private report snapshot…"; completeReportReady -> "Styled offline complete report · choose shared-storage destination"; else -> "Waiting for complete Vulkan® collection" },
                        R.drawable.ic_action_html,
                        Modifier.fillMaxWidth(),
                        completeReportReady && !exportPreparing && pendingExportPath.isBlank(),
                        false
                    ) { prepareReportStorageExport("text/html") }
                }
                if (!completeReportReady) Text("TXT and HTML export remain disabled until the complete Vulkan® collection pass has finished, matching the Database completeness gate.", color = ComposeColor(0xFFFFC857), style = MaterialTheme.typography.bodySmall)
            }
        }
        if (showReporting) item {
            CapabilitySectionCard("VulkanScope Database") {
                Text("Submit the complete technical VulkanScope report to the public database. Capability fields cannot be selectively omitted, and sensitive device identifiers or private paths are not included.", color = ComposeColor(0xFFB6ACAE), style = MaterialTheme.typography.bodySmall)
                TransientActionButton(if (submissionInFlight) "Submitting…" else "Submit complete report", when {
                    submissionInFlight -> "Uploading the complete technical dataset"
                    !completeReportReady && !networkAvailable -> "Waiting for complete Vulkan collection · internet unavailable"
                    !completeReportReady -> "Waiting for complete Vulkan collection"
                    !networkAvailable -> "Unavailable without a validated internet connection"
                    else -> "Structured JSON + canonical TXT report"
                }, R.drawable.ic_database_submit, enabled = !submissionInFlight && completeReportReady && networkAvailable, idleTrailingIcon = R.drawable.ic_upload) {
                    if (submissionInFlight) false else {
                        submissionInFlight = true
                        submissionSuccessId = null
                        submissionFailureLog = null
                        submissionState = "Submitting complete technical report…"
                        try {
                            val result = submitDatabaseReport(context, report, display, mode)
                            submissionState = result.summary
                            if (result.success) {
                                submissionSuccessId = result.reportId
                            } else {
                                submissionFailureLog = result.log
                            }
                            result.success
                        } catch (error: CancellationException) {
                            throw error
                        } catch (error: Throwable) {
                            submissionState = "Submission failed: ${error.message ?: error.javaClass.simpleName}"
                            submissionFailureLog = databaseSubmissionExceptionLog("unexpected", error)
                            false
                        } finally {
                            submissionInFlight = false
                        }
                    }
                }
                Text("Compatibility notice: when a newer VulkanScope release raises the Database submission floor, reports from older app versions are rejected by the server. A rejection is returned as a submission error and is shown here instead of being treated as a successful upload.", color = ComposeColor(0xFFFFC857), style = MaterialTheme.typography.bodySmall)
                Text(submissionState, color = ComposeColor(0xFFAAAAAA), style = MaterialTheme.typography.bodySmall)
                submissionSuccessId?.let { reportId ->
                    Text("Report ID", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                    Text(reportId, color = VulkanTextPrimary, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
                if (!completeReportReady && !networkAvailable) {
                    Text("Database submission is locked for two independent reasons: Vulkan® collection is incomplete and Android does not report a validated internet connection.", color = ComposeColor(0xFFFFC857), style = MaterialTheme.typography.bodySmall)
                    Text("When internet returns during collection, the network lock clears immediately; submission still waits for complete report evidence.", color = ComposeColor(0xFF9CCBFF), style = MaterialTheme.typography.bodySmall)
                } else {
                    if (!completeReportReady) Text("Wait for the complete Vulkan® collection pass to finish before submitting.", color = ComposeColor(0xFFFFC857), style = MaterialTheme.typography.bodySmall)
                    if (!networkAvailable) Text("Database upload and public report browsing are disabled until Android reports a validated internet connection.", color = ComposeColor(0xFF9CCBFF), style = MaterialTheme.typography.bodySmall)
                }
                ExpressiveExternalLinkRow("Open VulkanScope Database", if (networkAvailable) "Browse public VulkanScope hardware reports" else "Unavailable without a validated internet connection", R.drawable.ic_database_browse, enabled = networkAvailable) { uriHandler.openUri(OFFICIAL_DATABASE_WEB_URL) }
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
    TurnipSupport.UNKNOWN -> "Checking the installed Vulkan® implementation; Qualcomm Adreno support is not yet known."
    TurnipSupport.SUPPORTED -> "Uses an installed AdrenoTools-compatible Turnip driver."
    TurnipSupport.UNSUPPORTED -> "Unavailable: this device does not expose a supported arm64-v8a Qualcomm Adreno Vulkan® device."
}


@Composable
private fun SettingsSectionCards(onSelected: (SettingsSection) -> Unit) {
    var cardsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { cardsVisible = true }
    Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SettingsSection.entries.forEachIndexed { index, section ->
            AnimatedVisibility(
                visible = cardsVisible,
                enter = fadeIn(tween(durationMillis = 260, delayMillis = index * 45)) + slideInHorizontally(tween(durationMillis = 260, delayMillis = index * 45)) { it / 10 },
                exit = fadeOut(tween(durationMillis = 120))
            ) {
                ExpressiveDestinationCard(section.label, section.description, section.icon) { onSelected(section) }
            }
        }
    }
}

@Composable
private fun DatabaseSubmissionFailureDialog(log: String, onDismiss: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scrollState = rememberScrollState()
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = VulkanSurfaceRaised,
        tonalElevation = 0.dp,
        title = { DatabaseFailureDialogTitle() },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("The Database submission did not complete successfully. The complete bounded submission log is shown below.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                Surface(shape = MaterialTheme.shapes.medium, color = ComposeColor(0xFF0D0D0D), border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant)) {
                    Text(
                        log,
                        modifier = Modifier.fillMaxWidth().heightIn(max = 360.dp).verticalScroll(scrollState).padding(14.dp),
                        color = VulkanTextPrimary,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        },
        confirmButton = { ExpressiveContainedIconTextButton("Copy all", R.drawable.ic_copy) { copyEvidenceText(context, "VulkanScope Database submission log", log) } },
        dismissButton = { ExpressiveContainedIconTextButton("Close", R.drawable.ic_close, onClick = onDismiss) }
    )
}

@Composable
private fun DatabaseFailureDialogTitle() {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(shape = RoundedCornerShape(18.dp), color = VulkanAccentContainer) {
            Box(Modifier.padding(8.dp).size(24.dp)) {
                Icon(painterResource(R.drawable.ic_database_submit), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.align(Alignment.TopStart).size(19.dp))
                Surface(shape = RoundedCornerShape(6.dp), color = VulkanSurfaceRaised, modifier = Modifier.align(Alignment.BottomEnd)) {
                    Icon(painterResource(R.drawable.ic_close), contentDescription = null, tint = ComposeColor(0xFFFF6B6B), modifier = Modifier.padding(1.dp).size(10.dp))
                }
            }
        }
        Text("Database submission failed", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = VulkanTextPrimary, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun TransientActionButton(
    title: String,
    subtitle: String,
    icon: Int,
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true,
    idleTrailingIcon: Int = icon,
    action: suspend () -> Boolean
) {
    val scope = rememberCoroutineScope()
    var state by remember { mutableStateOf(0) }
    val busy = state != 0
    val trailing = when (state) {
        2 -> R.drawable.ic_check
        3 -> R.drawable.ic_close
        else -> idleTrailingIcon
    }
    val trailingTint = when (state) {
        2 -> ComposeColor(0xFF73C991)
        3 -> ComposeColor(0xFFFF6B6B)
        else -> VulkanAccentSoft
    }
    val semanticTitle = when (state) {
        2 -> "$title · completed"
        3 -> "$title · failed"
        else -> title
    }
    val semanticSubtitle = when (state) {
        2 -> "Completed successfully"
        3 -> "The action failed"
        else -> subtitle
    }
    ExpressiveActionButton(
        semanticTitle,
        semanticSubtitle,
        icon,
        modifier,
        enabled && !busy,
        trailingIcon = trailing,
        trailingTint = trailingTint
    ) {
        if (!enabled || busy) return@ExpressiveActionButton
        state = 1
        scope.launch {
            val success = try {
                action()
            } catch (error: CancellationException) {
                state = 0
                throw error
            } catch (_: Throwable) {
                false
            }
            state = if (success) 2 else 3
            delay(3000)
            state = 0
        }
    }
}

@Composable
private fun SettingsPage(
    report: VulkanReport,
    display: DisplayReport,
    mode: DriverMode,
    turnipSupport: TurnipSupport,
    turnipManagerRevision: Int,
    turnipManagerBusy: Boolean,
    storagePermissionDeniedFeedback: Boolean,
    onModeChanged: (DriverMode) -> Unit,
    onInstallDriverBundle: () -> Unit,
    onActivateTurnipDriver: (Int) -> Unit,
    onRemoveTurnipDriver: (Int) -> Unit,
    collectionStatus: CollectionStatus,
    directUpdatesEnabled: Boolean,
    onDirectUpdatesChanged: (Boolean) -> Unit,
    onCheckForUpdates: () -> Unit,
    updateCheckInFlight: Boolean,
    selectedSection: SettingsSection?,
    onSectionSelected: (SettingsSection?) -> Unit
) {
    AnimatedContent(
        targetState = selectedSection,
        modifier = Modifier.fillMaxSize(),
        transitionSpec = {
            when {
                initialState == null && targetState != null ->
                    slideInHorizontally(tween(durationMillis = 280)) { it / 8 } + fadeIn(tween(durationMillis = 220)) togetherWith
                        slideOutHorizontally(tween(durationMillis = 180)) { -it / 12 } + fadeOut(tween(durationMillis = 150))
                initialState != null && targetState == null ->
                    slideInHorizontally(tween(durationMillis = 240)) { -it / 10 } + fadeIn(tween(durationMillis = 200)) togetherWith
                        slideOutHorizontally(tween(durationMillis = 200)) { it / 10 } + fadeOut(tween(durationMillis = 150))
                else ->
                    fadeIn(tween(durationMillis = 200)) togetherWith fadeOut(tween(durationMillis = 150))
            }
        },
        label = "settingsSectionTransition"
    ) { targetSection ->
        when (targetSection) {
            null -> VulkanLazyPage(verticalSpacing = 10.dp) {
                item { SettingsSectionCards { onSectionSelected(it) } }
            }
            SettingsSection.INFO -> InfoPage(
                report,
                display,
                mode,
                collectionStatus,
                onCheckForUpdates,
                directUpdatesEnabled,
                updateCheckInFlight,
                showReporting = false
            )
            SettingsSection.REPORTS -> InfoPage(
                report,
                display,
                mode,
                collectionStatus,
                onCheckForUpdates,
                directUpdatesEnabled,
                updateCheckInFlight,
                showInfo = false
            )
            SettingsSection.DRIVER_UPDATES -> DriverUpdatePreferencesPage(
                report = report,
                mode = mode,
                turnipSupport = turnipSupport,
                turnipManagerRevision = turnipManagerRevision,
                turnipManagerBusy = turnipManagerBusy,
                storagePermissionDeniedFeedback = storagePermissionDeniedFeedback,
                onModeChanged = onModeChanged,
                onInstallDriverBundle = onInstallDriverBundle,
                onActivateTurnipDriver = onActivateTurnipDriver,
                onRemoveTurnipDriver = onRemoveTurnipDriver,
                collectionStatus = collectionStatus,
                directUpdatesEnabled = directUpdatesEnabled,
                onDirectUpdatesChanged = onDirectUpdatesChanged
            )
        }
    }
}

@Composable
private fun DriverUpdatePreferencesPage(
    report: VulkanReport,
    mode: DriverMode,
    turnipSupport: TurnipSupport,
    turnipManagerRevision: Int,
    turnipManagerBusy: Boolean,
    storagePermissionDeniedFeedback: Boolean,
    onModeChanged: (DriverMode) -> Unit,
    onInstallDriverBundle: () -> Unit,
    onActivateTurnipDriver: (Int) -> Unit,
    onRemoveTurnipDriver: (Int) -> Unit,
    collectionStatus: CollectionStatus,
    directUpdatesEnabled: Boolean,
    onDirectUpdatesChanged: (Boolean) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val completeReportReady = isCompleteReportReady(report, collectionStatus)
    val settingsPrefs = remember(context) { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }
    var drivers by remember { mutableStateOf<List<ManagedTurnipDriver>>(emptyList()) }
    var managerLoading by remember { mutableStateOf(false) }
    var detailsDriver by remember { mutableStateOf<ManagedTurnipDriver?>(null) }
    var removeDriver by remember { mutableStateOf<ManagedTurnipDriver?>(null) }
    var cachedSystemSummary by remember { mutableStateOf(readSystemDriverSummary(settingsPrefs)) }
    var detailsSystemDriver by remember { mutableStateOf(false) }
    val currentSystemSummary = remember(report, mode, completeReportReady) {
        if (mode == DriverMode.SYSTEM && completeReportReady) systemDriverSummaryFromReport(report) else null
    }
    val displayedSystemSummary = currentSystemSummary ?: cachedSystemSummary

    LaunchedEffect(currentSystemSummary) {
        currentSystemSummary?.let { summary ->
            cachedSystemSummary = summary
            withContext(Dispatchers.IO) { persistSystemDriverSummary(settingsPrefs, summary) }
        }
    }

    LaunchedEffect(turnipManagerRevision, turnipSupport, turnipManagerBusy, mode) {
        if (turnipSupport == TurnipSupport.SUPPORTED && !turnipManagerBusy) {
            managerLoading = true
            drivers = withContext(Dispatchers.IO) {
                readManagedTurnipDrivers(context, context.getSharedPreferences("settings", Context.MODE_PRIVATE), mode)
            }
            managerLoading = false
        } else if (turnipSupport != TurnipSupport.SUPPORTED) {
            drivers = emptyList()
            managerLoading = false
        }
    }

    VulkanLazyPage(verticalSpacing = 14.dp) {
        item {
            CapabilitySectionCard("Update preferences") {
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Direct GitHub updates", color = VulkanTextPrimary, fontWeight = FontWeight.SemiBold)
                        Text(if (directUpdatesEnabled) "Enabled · update checks use the official VulkanScope GitHub Releases channel" else "Disabled · recommended when Obtainium manages updates", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                    ExpressiveSwitch(checked = directUpdatesEnabled, onCheckedChange = onDirectUpdatesChanged)
                }
                Text("Direct GitHub updates are enabled by default so new installations receive update checks. When disabled, VulkanScope performs no startup update check and will not download update APKs. Obtainium can track the universal APK from the official GitHub Releases channel without enabling the built-in updater.", color = VulkanTextMuted, style = MaterialTheme.typography.bodySmall)
            }
        }
        item {
            CapabilitySectionCard("Driver manager") {
                Text("System Vulkan® driver is the default source. Turnip packages remain separately managed and become active only after explicit activation and confirmation.", color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.bodySmall)
                SystemDriverManagerRow(
                    active = mode == DriverMode.SYSTEM,
                    enabled = completeReportReady && !turnipManagerBusy,
                    summary = displayedSystemSummary,
                    onDetails = { detailsSystemDriver = true },
                    onActivate = { onModeChanged(DriverMode.SYSTEM) }
                )
                if (collectionStatus == CollectionStatus.COLLECTING) {
                    Text("Driver changes are temporarily locked while VulkanScope is collecting a report. Switching Vulkan® drivers mid-collection would mix evidence from different driver sessions.", color = ComposeColor(0xFFFFC857), style = MaterialTheme.typography.bodySmall)
                }
                HorizontalDivider(color = VulkanOutlineVariant)
                when (turnipSupport) {
                    TurnipSupport.UNSUPPORTED -> {
                        Text("UNAVAILABLE", color = ComposeColor(0xFFFFC857), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        Text("Turnip requires an arm64-v8a device with a detected Qualcomm Adreno Vulkan® implementation. Driver slots and import controls are hidden because this device does not satisfy that eligibility gate.", color = ComposeColor(0xFFFFC857), style = MaterialTheme.typography.bodySmall)
                    }
                    TurnipSupport.UNKNOWN -> {
                        Text("UNAVAILABLE", color = ComposeColor(0xFFFFC857), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        Text("Turnip availability cannot be confirmed until VulkanScope has authoritative platform and Vulkan® device evidence.", color = ComposeColor(0xFFFFC857), style = MaterialTheme.typography.bodySmall)
                    }
                    TurnipSupport.SUPPORTED -> {
                        val occupied = drivers.size
                        ExpressiveMetricGrid(
                            listOf(
                                "Installed" to occupied.toString(),
                                "Maximum" to TURNIP_MANAGER_MAX_DRIVERS.toString(),
                                "Available slots" to (TURNIP_MANAGER_MAX_DRIVERS - occupied).coerceAtLeast(0).toString()
                            )
                        )
                        Text("Turnip ZIP import uses VulkanScope's in-app file manager. It shows folders and pre-validated Turnip ZIP packages only; imported packages are copied into VulkanScope private storage and revalidated before installation.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                        AnimatedContent(targetState = storagePermissionDeniedFeedback, label = "storagePermissionFeedback") { denied ->
                            ExpressiveActionButton(
                                title = if (denied) "Permission denied" else "Import driver ZIP",
                                subtitle = if (denied) "Storage access was not granted" else "Open VulkanScope file manager · ${(TURNIP_MANAGER_MAX_DRIVERS - occupied).coerceAtLeast(0)} slots remaining",
                                icon = if (denied) R.drawable.ic_close else R.drawable.ic_zip_download,
                                enabled = completeReportReady && !managerLoading && !turnipManagerBusy && occupied < TURNIP_MANAGER_MAX_DRIVERS,
                                onClick = onInstallDriverBundle
                            )
                        }
                        if (managerLoading) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                ExpressiveLinearProgressIndicator(Modifier.width(72.dp))
                                Text("Reading private driver slots…", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                            }
                        } else if (drivers.isEmpty()) {
                            EmptyState("No Turnip drivers imported")
                        } else {
                            TurnipDriverManagerTable(
                                drivers = drivers,
                                activeMode = mode,
                                enabled = completeReportReady && !turnipManagerBusy,
                                onDetails = { detailsDriver = it },
                                onActivate = onActivateTurnipDriver,
                                onRemove = { removeDriver = it }
                            )
                        }
                        if (!completeReportReady) Text("Driver import, activation and removal unlock after the complete Vulkan® collection pass finishes.", color = ComposeColor(0xFFFFC857), style = MaterialTheme.typography.bodySmall)
                        Text("Turnip packages must use the validated AdrenoTools schema (meta.json + metadata-declared Vulkan® .so). Up to 10 private driver packages can be retained.", color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

    if (detailsSystemDriver && displayedSystemSummary != null) {
        SystemDriverDetailsDialog(
            summary = displayedSystemSummary,
            evidenceSource = if (currentSystemSummary != null) "Current completed System report" else "Last completed System report retained in private UI metadata",
            active = mode == DriverMode.SYSTEM,
            onDismiss = { detailsSystemDriver = false }
        )
    }
    detailsDriver?.let { driver ->
        TurnipDriverDetailsDialog(driver = driver, onDismiss = { detailsDriver = null })
    }
    removeDriver?.let { driver ->
        AlertDialog(
            onDismissRequest = { if (!turnipManagerBusy) removeDriver = null },
            title = { QuestionDialogTitle("Delete Turnip driver?") },
            text = { Text((if (driver.selected) "This slot is active. Removing it will deactivate Turnip, switch VulkanScope to the System driver, and delete the private package. " else "") + "Slot %02d · %s will be deleted from VulkanScope private storage. This cannot be undone.".format(java.util.Locale.ROOT, driver.slot, driver.info.zipName ?: driver.info.driverName ?: "Imported driver")) },
            confirmButton = {
                ExpressiveContainedIconTextButton("Delete", R.drawable.ic_delete, enabled = !turnipManagerBusy, fontWeight = FontWeight.Bold) {
                    removeDriver = null
                    onRemoveTurnipDriver(driver.slot)
                }
            },
            dismissButton = { ExpressiveCancelButton(enabled = !turnipManagerBusy) { removeDriver = null } }
        )
    }

}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TurnipFileManagerDialog(
    state: TurnipFileManagerState,
    onDismiss: () -> Unit,
    onNavigate: (String) -> Unit,
    onUp: () -> Unit,
    onToggleSelection: (String) -> Unit,
    onViewMode: (TurnipFileManagerViewMode) -> Unit,
    onDetails: (TurnipArchiveCandidate) -> Unit,
    onDismissDetails: () -> Unit,
    onImport: () -> Unit
) {
    var search by remember(state.directoryPath) { mutableStateOf("") }
    val filteredFolders = remember(state.folders, search) {
        if (search.isBlank()) state.folders else state.folders.filter { File(it).name.contains(search, true) }
    }
    val filteredCandidates = remember(state.candidates, search) {
        if (search.isBlank()) state.candidates else state.candidates.filter {
            it.name.contains(search, true) || it.driverName?.contains(search, true) == true || it.driverVersion?.contains(search, true) == true
        }
    }
    val atRoot = state.directoryPath == state.rootPath
    val expandedTextLayout = preferExpandedTextLayout()
    Dialog(
        onDismissRequest = { if (!state.importing) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = !state.importing, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = VulkanSurfaceRaised,
            contentColor = VulkanTextPrimary,
            border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant),
            tonalElevation = 8.dp,
            shadowElevation = 10.dp
        ) {
            Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    IconButton(
                        onClick = onUp,
                        enabled = !atRoot && !state.loading && !state.importing,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = VulkanSurfaceLow, contentColor = VulkanTextPrimary, disabledContainerColor = VulkanSurfaceLow, disabledContentColor = VulkanTextMuted)
                    ) { Icon(painterResource(R.drawable.ic_back), contentDescription = "Parent folder") }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text("Turnip file manager", color = VulkanTextPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(state.directoryPath, color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                    IconButton(
                        onClick = onDismiss,
                        enabled = !state.importing,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = VulkanSurfaceLow, contentColor = VulkanTextPrimary, disabledContainerColor = VulkanSurfaceLow, disabledContentColor = VulkanTextMuted)
                    ) { Icon(painterResource(R.drawable.ic_close), contentDescription = "Close") }
                }

                Surface(shape = MaterialTheme.shapes.extraLarge, color = VulkanSurfaceTonal, contentColor = VulkanTextPrimary, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant)) {
                    if (expandedTextLayout) {
                        Column(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            TurnipFileManagerSelectionSummary(state)
                            TurnipFileManagerViewModeButtons(state, onViewMode, Modifier.fillMaxWidth())
                        }
                    } else {
                        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(Modifier.weight(0.85f)) { TurnipFileManagerSelectionSummary(state) }
                            TurnipFileManagerViewModeButtons(state, onViewMode, Modifier.weight(1.15f))
                        }
                    }
                }

                ExpressiveSearchField(
                    value = search,
                    onValueChange = { search = it.take(120) },
                    modifier = Modifier.fillMaxWidth(),
                    labelText = "Search",
                    placeholderText = "Folders and validated Turnip packages",
                    enabled = !state.importing
                )

                state.status?.let { message ->
                    val warning = message.contains("failed", true) || message.contains("unavailable", true) || message.contains("limit", true)
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        color = if (warning) ComposeColor(0xFF2A2115) else VulkanSurfaceLow,
                        contentColor = if (warning) ComposeColor(0xFFFFC857) else VulkanTextSecondary,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (warning) ComposeColor(0xFF55401E) else VulkanOutlineVariant)
                    ) {
                        Text(message, modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp), color = if (warning) ComposeColor(0xFFFFC857) else VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                }

                Box(Modifier.weight(1f).fillMaxWidth()) {
                    if (state.loading) {
                        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            LoadingIndicator(color = VulkanAccentSoft)
                            Text("Scanning folders and validating Turnip ZIPs…", color = VulkanTextSecondary, textAlign = TextAlign.Center)
                        }
                    } else if (state.viewMode == TurnipFileManagerViewMode.GRID) {
                        val gridState = rememberLazyGridState()
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(156.dp),
                            state = gridState,
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            gridItems(filteredFolders, key = { "folder:$it" }) { path ->
                                TurnipFileManagerFolderGridCard(path = path, enabled = !state.importing) { onNavigate(path) }
                            }
                            gridItems(filteredCandidates, key = { "zip:${it.path}" }) { candidate ->
                                val alreadyImported = turnipImportedSourceKey(candidate.path, candidate.name) in state.importedSourceKeys
                                TurnipFileManagerCandidateGridCard(
                                    candidate = candidate,
                                    selected = !alreadyImported && candidate.path in state.selectedPaths,
                                    alreadyImported = alreadyImported,
                                    enabled = !state.importing && !alreadyImported,
                                    onSelectedChange = { onToggleSelection(candidate.path) },
                                    onDetails = { onDetails(candidate) }
                                )
                            }
                            if (filteredFolders.isEmpty() && filteredCandidates.isEmpty()) {
                                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                                    EmptyState(if (search.isBlank()) "No folders or validated Turnip ZIPs in this location" else "No matching folders or validated Turnip ZIPs")
                                }
                            }
                        }
                        ExpressiveScrollHints(gridState, Modifier.fillMaxSize().padding(horizontal = 6.dp, vertical = 6.dp))
                    } else {
                        val listState = rememberLazyListState()
                        val compact = state.viewMode == TurnipFileManagerViewMode.COMPACT
                        val details = state.viewMode == TurnipFileManagerViewMode.DETAILS
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 9.dp),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            items(filteredFolders, key = { "folder:$it" }) { path ->
                                TurnipFileManagerFolderRow(path = path, compact = compact, details = details, enabled = !state.importing) { onNavigate(path) }
                            }
                            items(filteredCandidates, key = { "zip:${it.path}" }) { candidate ->
                                val alreadyImported = turnipImportedSourceKey(candidate.path, candidate.name) in state.importedSourceKeys
                                TurnipFileManagerCandidateRow(
                                    candidate = candidate,
                                    selected = !alreadyImported && candidate.path in state.selectedPaths,
                                    compact = compact,
                                    details = details,
                                    alreadyImported = alreadyImported,
                                    enabled = !state.importing && !alreadyImported,
                                    onSelectedChange = { onToggleSelection(candidate.path) },
                                    onDetails = { onDetails(candidate) }
                                )
                            }
                            if (filteredFolders.isEmpty() && filteredCandidates.isEmpty()) {
                                item { EmptyState(if (search.isBlank()) "No folders or validated Turnip ZIPs in this location" else "No matching folders or validated Turnip ZIPs") }
                            }
                        }
                        ExpressiveScrollHints(listState, Modifier.fillMaxSize().padding(horizontal = 6.dp, vertical = 6.dp))
                    }
                }

                Surface(shape = MaterialTheme.shapes.extraLarge, color = VulkanSurfaceLow, contentColor = VulkanTextPrimary, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant)) {
                    if (expandedTextLayout) {
                        Column(Modifier.fillMaxWidth().padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Other files are hidden. ZIP validation is repeated during import.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                                if (state.importing) Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    LoadingIndicator(color = VulkanAccentSoft, modifier = Modifier.size(28.dp)); Text("Importing…", color = VulkanTextSecondary, fontWeight = FontWeight.SemiBold)
                                } else ExpressiveContainedIconTextButton("Import ${state.selectedPaths.size}", R.drawable.ic_zip_download, enabled = state.selectedPaths.isNotEmpty() && state.selectedPaths.size <= state.maxSelectable, fontWeight = FontWeight.Bold, onClick = onImport)
                            }
                        }
                    } else {
                        Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Other files are hidden. ZIP validation is repeated during import.", modifier = Modifier.weight(1f), color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                            if (state.importing) Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                LoadingIndicator(color = VulkanAccentSoft, modifier = Modifier.size(28.dp)); Text("Importing…", color = VulkanTextSecondary, fontWeight = FontWeight.SemiBold)
                            } else ExpressiveContainedIconTextButton("Import ${state.selectedPaths.size}", R.drawable.ic_zip_download, enabled = state.selectedPaths.isNotEmpty() && state.selectedPaths.size <= state.maxSelectable, fontWeight = FontWeight.Bold, onClick = onImport)
                        }
                    }
                }
            }
        }
    }
    state.details?.let { TurnipArchiveCandidateDetailsDialog(candidate = it, onDismiss = onDismissDetails) }
}

@Composable
private fun TurnipFileManagerSelectionSummary(state: TurnipFileManagerState) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            Surface(shape = RoundedCornerShape(999.dp), color = VulkanAccentContainer, contentColor = VulkanAccentSoft) {
                Text(state.selectedPaths.size.toString(), modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
            }
            Text("selected", color = VulkanTextPrimary, fontWeight = FontWeight.Bold)
        }
        Text("${(state.maxSelectable - state.selectedPaths.size).coerceAtLeast(0)} / ${state.maxSelectable} remaining", color = VulkanTextSecondary, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun TurnipFileManagerViewModeButtons(state: TurnipFileManagerState, onViewMode: (TurnipFileManagerViewMode) -> Unit, modifier: Modifier = Modifier) {
    val outerShape = RoundedCornerShape(22.dp)
    Surface(modifier = modifier.clip(outerShape), shape = outerShape, color = VulkanSurfaceLow, contentColor = VulkanTextPrimary, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant)) {
        Row(Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            TurnipFileManagerViewModeButton(R.drawable.ic_view_list, "List view", state.viewMode == TurnipFileManagerViewMode.LIST, !state.importing, Modifier.weight(1f)) { onViewMode(TurnipFileManagerViewMode.LIST) }
            TurnipFileManagerViewModeButton(R.drawable.ic_view_compact, "Compact view", state.viewMode == TurnipFileManagerViewMode.COMPACT, !state.importing, Modifier.weight(1f)) { onViewMode(TurnipFileManagerViewMode.COMPACT) }
            TurnipFileManagerViewModeButton(R.drawable.ic_view_grid, "Grid view", state.viewMode == TurnipFileManagerViewMode.GRID, !state.importing, Modifier.weight(1f)) { onViewMode(TurnipFileManagerViewMode.GRID) }
            TurnipFileManagerViewModeButton(R.drawable.ic_view_details, "Details view", state.viewMode == TurnipFileManagerViewMode.DETAILS, !state.importing, Modifier.weight(1f)) { onViewMode(TurnipFileManagerViewMode.DETAILS) }
        }
    }
}

@Composable
private fun TurnipFileManagerViewModeButton(icon: Int, description: String, selected: Boolean, enabled: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(17.dp)
    val scale by animateFloatAsState(if (selected) 1f else 0.96f, animationSpec = tween(160), label = "turnipViewModeScale")
    Surface(
        modifier = modifier.graphicsLayer(scaleX = scale, scaleY = scale).clip(shape).clickable(enabled = enabled, role = Role.RadioButton, onClick = onClick),
        shape = shape,
        color = if (selected) VulkanAccentContainer else ComposeColor.Transparent,
        contentColor = if (selected) VulkanAccentSoft else VulkanTextSecondary
    ) {
        Box(Modifier.fillMaxWidth().heightIn(min = 44.dp), contentAlignment = Alignment.Center) {
            Icon(painterResource(icon), contentDescription = description, modifier = Modifier.size(21.dp))
            AnimatedVisibility(visible = selected, enter = fadeIn(tween(120)) + scaleIn(tween(140), initialScale = 0.7f), exit = fadeOut(tween(90)) + scaleOut(tween(110), targetScale = 0.7f), modifier = Modifier.align(Alignment.TopEnd)) {
                Surface(shape = RoundedCornerShape(999.dp), color = VulkanAccentSoft, contentColor = VulkanSurface, modifier = Modifier.padding(4.dp)) {
                    Icon(painterResource(R.drawable.ic_check), contentDescription = null, modifier = Modifier.padding(2.dp).size(8.dp))
                }
            }
        }
    }
}

@Composable
private fun MesaOfficialLogoBadge(size: Dp, muted: Boolean = false, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = VulkanAccentContainer,
        contentColor = VulkanAccentSoft,
        border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.34f))
    ) {
        Image(
            painter = painterResource(R.drawable.mesa3d_logo),
            contentDescription = "Mesa",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(VulkanAccentSoft),
            modifier = Modifier.padding(6.dp).size(size).alpha(if (muted) 0.42f else 1f)
        )
    }
}

@Composable
private fun FileManagerNavigateArrow(enabled: Boolean, description: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Surface(
        shape = shape,
        color = if (enabled) VulkanAccentContainer else VulkanSurfaceLow,
        contentColor = if (enabled) VulkanAccentSoft else VulkanTextMuted,
        modifier = Modifier.size(44.dp).clip(shape).clickable(enabled = enabled, role = Role.Button, onClick = onClick)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = description, modifier = Modifier.size(21.dp))
        }
    }
}

@Composable
private fun TurnipFileManagerFolderRow(path: String, compact: Boolean, details: Boolean, enabled: Boolean, onOpen: () -> Unit) {
    val shape = MaterialTheme.shapes.large
    Surface(shape = shape, color = VulkanSurfaceTonal, contentColor = VulkanTextPrimary, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant), tonalElevation = if (compact) 0.dp else 1.dp, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = if (compact) 8.dp else 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(shape = RoundedCornerShape(14.dp), color = ComposeColor(0xFF2A2418), contentColor = ComposeColor(0xFFFFC857)) {
                Icon(painterResource(R.drawable.ic_folder), contentDescription = null, modifier = Modifier.padding(if (compact) 7.dp else 8.dp).size(if (compact) 22.dp else 25.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(File(path).name.ifBlank { path }, color = VulkanTextPrimary, fontWeight = FontWeight.SemiBold, maxLines = if (compact) 1 else 2, overflow = TextOverflow.Ellipsis)
                if (!compact) Text(if (details) path else "Folder", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall, maxLines = if (details) 2 else 1, overflow = TextOverflow.Ellipsis)
            }
            FileManagerNavigateArrow(enabled = enabled, description = "Open folder") { onOpen() }
        }
    }
}

@Composable
private fun TurnipFileManagerCandidateRow(candidate: TurnipArchiveCandidate, selected: Boolean, compact: Boolean, details: Boolean, alreadyImported: Boolean, enabled: Boolean, onSelectedChange: () -> Unit, onDetails: () -> Unit) {
    val shape = MaterialTheme.shapes.large
    val rowAlpha = if (alreadyImported) 0.56f else 1f
    Surface(
        modifier = Modifier.fillMaxWidth().alpha(rowAlpha).clip(shape).clickable(enabled = enabled, role = Role.Checkbox) { onSelectedChange() },
        shape = shape,
        color = if (alreadyImported) VulkanSurfaceLow else if (selected) VulkanAccentContainer else VulkanSurfaceTonal,
        contentColor = if (alreadyImported) VulkanTextMuted else VulkanTextPrimary,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (selected && !alreadyImported) VulkanAccentSoft.copy(alpha = 0.58f) else VulkanOutlineVariant),
        tonalElevation = if (compact) 0.dp else 1.dp
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = if (compact) 7.dp else 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Checkbox(checked = selected, onCheckedChange = { onSelectedChange() }, enabled = enabled, colors = CheckboxDefaults.colors(checkedColor = VulkanAccent, checkmarkColor = VulkanTextPrimary, uncheckedColor = VulkanOutline, disabledCheckedColor = VulkanTextMuted, disabledUncheckedColor = VulkanOutlineVariant))
            MesaOfficialLogoBadge(size = if (compact) 30.dp else 36.dp, muted = alreadyImported)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(candidate.name, color = if (alreadyImported) VulkanTextMuted else ComposeColor(0xFF72DE91), fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(candidate.driverName ?: candidate.libraryName, color = if (alreadyImported) VulkanTextMuted else VulkanTextPrimary, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (alreadyImported) Text("Already imported · same file name and location", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                if (!compact) Text("${candidate.driverVersion ?: candidate.packageVersion ?: "Version not exposed"} · ${formatBytes(candidate.sizeBytes)} · ${formatTimestampOrUnavailable(candidate.modifiedAtMillis.takeIf { it > 0L })}", color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall, maxLines = 3, overflow = TextOverflow.Ellipsis)
                if (details) Text("Schema ${candidate.schemaVersion} · ${candidate.vendor ?: "Vendor not exposed"} · ${candidate.libraryName} (${formatBytes(candidate.librarySizeBytes)})", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall, maxLines = 3, overflow = TextOverflow.Ellipsis)
            }
            IconButton(onClick = onDetails, enabled = enabled, colors = IconButtonDefaults.iconButtonColors(containerColor = if (selected && !alreadyImported) ComposeColor(0xFF4A2023) else VulkanSurfaceLow, contentColor = VulkanAccentSoft, disabledContainerColor = VulkanSurfaceLow, disabledContentColor = VulkanTextMuted)) {
                Icon(painterResource(R.drawable.ic_info), contentDescription = "Turnip package info")
            }
        }
    }
}

@Composable
private fun TurnipFileManagerFolderGridCard(path: String, enabled: Boolean, onOpen: () -> Unit) {
    val shape = MaterialTheme.shapes.large
    Surface(shape = shape, color = VulkanSurfaceTonal, contentColor = VulkanTextPrimary, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(shape = RoundedCornerShape(18.dp), color = ComposeColor(0xFF2A2418), contentColor = ComposeColor(0xFFFFC857)) { Icon(painterResource(R.drawable.ic_folder), contentDescription = null, modifier = Modifier.padding(10.dp).size(30.dp)) }
            Text(File(path).name.ifBlank { path }, color = VulkanTextPrimary, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text("Folder", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
            FileManagerNavigateArrow(enabled = enabled, description = "Open folder") { onOpen() }
        }
    }
}

@Composable
private fun TurnipFileManagerCandidateGridCard(candidate: TurnipArchiveCandidate, selected: Boolean, alreadyImported: Boolean, enabled: Boolean, onSelectedChange: () -> Unit, onDetails: () -> Unit) {
    val shape = MaterialTheme.shapes.large
    Surface(
        modifier = Modifier.fillMaxWidth().alpha(if (alreadyImported) 0.56f else 1f).clip(shape).clickable(enabled = enabled, role = Role.Checkbox) { onSelectedChange() },
        shape = shape,
        color = if (alreadyImported) VulkanSurfaceLow else if (selected) VulkanAccentContainer else VulkanSurfaceTonal,
        contentColor = if (alreadyImported) VulkanTextMuted else VulkanTextPrimary,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (selected && !alreadyImported) VulkanAccentSoft.copy(alpha = 0.58f) else VulkanOutlineVariant)
    ) {
        Column(Modifier.fillMaxWidth().padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Checkbox(checked = selected, onCheckedChange = { onSelectedChange() }, enabled = enabled, colors = CheckboxDefaults.colors(checkedColor = VulkanAccent, checkmarkColor = VulkanTextPrimary, uncheckedColor = VulkanOutline, disabledUncheckedColor = VulkanOutlineVariant))
                IconButton(onClick = onDetails, enabled = enabled, colors = IconButtonDefaults.iconButtonColors(containerColor = VulkanSurfaceLow, contentColor = VulkanAccentSoft, disabledContainerColor = VulkanSurfaceLow, disabledContentColor = VulkanTextMuted), modifier = Modifier.size(42.dp)) { Icon(painterResource(R.drawable.ic_info), contentDescription = "Turnip package info", modifier = Modifier.size(20.dp)) }
            }
            MesaOfficialLogoBadge(size = 38.dp, muted = alreadyImported, modifier = Modifier.align(Alignment.CenterHorizontally))
            Text(candidate.name, color = if (alreadyImported) VulkanTextMuted else ComposeColor(0xFF72DE91), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxWidth())
            if (alreadyImported) Text("Already imported", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Text(candidate.driverVersion ?: candidate.packageVersion ?: "Version not exposed", color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxWidth())
        }
    }
}


@Composable
private fun TurnipArchiveCandidateDetailsDialog(candidate: TurnipArchiveCandidate, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = VulkanSurfaceRaised,
        titleContentColor = VulkanTextPrimary,
        textContentColor = VulkanTextSecondary,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MesaOfficialLogoBadge(size = 38.dp)
                Column(Modifier.weight(1f)) {
                    Text("Turnip package", color = VulkanTextPrimary, fontWeight = FontWeight.Bold)
                    Text(candidate.name, color = ComposeColor(0xFF72DE91), style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
        },
        text = {
            Box(Modifier.fillMaxWidth().heightIn(max = 480.dp)) {
                val detailsScroll = rememberScrollState()
                Column(Modifier.fillMaxWidth().verticalScroll(detailsScroll).padding(end = 2.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CapabilityKeyValue("Validation", "Passed pre-import package checks")
                    CapabilityKeyValue("Schema", candidate.schemaVersion.toString())
                    CapabilityKeyValue("Driver name", candidate.driverName ?: "Not exposed")
                    CapabilityKeyValue("Vulkan version", candidate.driverVersion ?: "Not exposed")
                    CapabilityKeyValue("Driver date", candidate.driverDate ?: "Not exposed")
                    CapabilityKeyValue("Package version", candidate.packageVersion ?: "Not exposed")
                    CapabilityKeyValue("Vendor", candidate.vendor ?: "Not exposed")
                    CapabilityKeyValue("Author", candidate.author ?: "Not exposed")
                    CapabilityKeyValue("Minimum API", candidate.minApi?.toString() ?: "Not exposed")
                    CapabilityKeyValue("Vulkan library", candidate.libraryName)
                    CapabilityKeyValue("Library size", formatBytes(candidate.librarySizeBytes))
                    CapabilityKeyValue("ZIP size", formatBytes(candidate.sizeBytes))
                    CapabilityKeyValue("Modified", formatTimestampOrUnavailable(candidate.modifiedAtMillis.takeIf { it > 0L }))
                    candidate.description?.let { CapabilityKeyValue("Description", it) }
                    CapabilityKeyValue("Path", candidate.path)
                }
                ExpressiveScrollHints(detailsScroll, Modifier.fillMaxSize().padding(horizontal = 2.dp, vertical = 2.dp))
            }
        },
        confirmButton = { ExpressiveContainedIconTextButton("Close", R.drawable.ic_close, onClick = onDismiss) }
    )
}

@Composable
private fun SharedStoragePermissionActionButton(
    title: String,
    subtitle: String,
    icon: Int,
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true,
    compact: Boolean = false,
    onGranted: () -> Unit
) {
    val requestAccess = LocalSharedStorageAccessRequest.current
    val scope = rememberCoroutineScope()
    var denied by remember { mutableStateOf(false) }
    var feedbackGeneration by remember { mutableIntStateOf(0) }
    AnimatedContent(targetState = denied, label = "sharedStoragePermissionFeedback") { permissionDenied ->
        ExpressiveActionButton(
            title = if (permissionDenied) "Permission denied" else title,
            subtitle = if (permissionDenied) "Shared-storage access was not granted" else subtitle,
            icon = if (permissionDenied) R.drawable.ic_close else icon,
            modifier = modifier,
            enabled = enabled,
            compact = compact,
            trailingIcon = if (permissionDenied) R.drawable.ic_close else R.drawable.ic_chevron_right,
            trailingTint = if (permissionDenied) ComposeColor(0xFFFF6B6B) else null
        ) {
            if (!enabled) return@ExpressiveActionButton
            requestAccess(
                {
                    denied = false
                    onGranted()
                },
                {
                    feedbackGeneration += 1
                    val generation = feedbackGeneration
                    denied = true
                    scope.launch {
                        delay(3_000L)
                        if (feedbackGeneration == generation) denied = false
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SharedStorageBrowserDialog(
    request: SharedStorageBrowserRequest,
    onDismiss: () -> Unit,
    onImport: suspend (File) -> Result<String> = { Result.failure(IllegalStateException("Import is unavailable")) },
    onExport: suspend (File) -> Result<String> = { Result.failure(IllegalStateException("Export is unavailable")) }
) {
    val root = remember { runCatching { sharedStorageRoot() }.getOrNull() }
    val scope = rememberCoroutineScope()
    var directoryPath by remember(request.title) { mutableStateOf(root?.path.orEmpty()) }
    var listing by remember(request.title) { mutableStateOf(SharedStorageDirectoryListing(emptyList(), emptyList(), false)) }
    var loading by remember(request.title) { mutableStateOf(root != null) }
    var busy by remember(request.title) { mutableStateOf(false) }
    var status by remember(request.title) { mutableStateOf<String?>(if (root == null) "Shared storage is unavailable" else null) }
    var search by remember(request.title, directoryPath) { mutableStateOf("") }
    val exportExtension = remember(request.mode, request.allowedExtensions) {
        if (request.mode == SharedStorageBrowserMode.EXPORT) request.allowedExtensions.singleOrNull()?.lowercase(java.util.Locale.ROOT) else null
    }
    val suggestedBase = remember(request.title, exportExtension) {
        val suggested = request.suggestedFileName.take(180)
        val suffix = exportExtension?.let { ".$it" }.orEmpty()
        if (suffix.isNotEmpty() && suggested.endsWith(suffix, ignoreCase = true)) suggested.dropLast(suffix.length) else suggested
    }
    var filenameBase by remember(request.title) { mutableStateOf(suggestedBase.take(160)) }
    var pendingOverwrite by remember(request.title) { mutableStateOf<File?>(null) }
    val directory = remember(directoryPath) { directoryPath.takeIf { it.isNotBlank() }?.let(::File) }
    val atRoot = root != null && directoryPath == root.path
    val filteredFolders = remember(listing.folders, search) { if (search.isBlank()) listing.folders else listing.folders.filter { File(it).name.contains(search, true) } }
    val filteredFiles = remember(listing.files, search) { if (search.isBlank()) listing.files else listing.files.filter { it.name.contains(search, true) } }
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    val imeVisible = WindowInsets.ime.getBottom(density) > 0

    LaunchedEffect(directoryPath, request.mode, request.allowedExtensions) {
        val scanRoot = root ?: return@LaunchedEffect
        val target = directory ?: return@LaunchedEffect
        loading = true
        status = null
        listing = SharedStorageDirectoryListing(emptyList(), emptyList(), false)
        try {
            val scanned = withContext(Dispatchers.IO) { scanSharedStorageDirectory(scanRoot, target, request.allowedExtensions, request.mode == SharedStorageBrowserMode.IMPORT) }
            listing = scanned
            if (scanned.entryLimitReached) status = "This folder reached the bounded 4096-entry scan limit."
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Throwable) {
            status = error.message ?: "Unable to read this folder"
        } finally {
            loading = false
        }
    }

    fun navigateUp() {
        val scanRoot = root ?: return
        val current = directory ?: return
        if (current.path == scanRoot.path || loading || busy) return
        val parent = runCatching { current.parentFile?.canonicalFile }.getOrNull() ?: return
        if (isCanonicalSharedStoragePath(scanRoot, parent)) directoryPath = parent.path
    }

    fun runExport(destination: File) {
        if (busy) return
        busy = true
        status = "Saving…"
        scope.launch {
            val result = try { onExport(destination) }
            catch (cancelled: CancellationException) { busy = false; throw cancelled }
            catch (error: Throwable) { Result.failure(error) }
            busy = false
            result.onSuccess { status = it; onDismiss() }.onFailure { status = it.message ?: "Export failed" }
        }
    }

    BackHandler(enabled = !busy) {
        if (imeVisible) focusManager.clearFocus(force = true)
        else if (!atRoot) navigateUp()
        else onDismiss()
    }
    Dialog(onDismissRequest = { if (!busy) onDismiss() }, properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = false, dismissOnClickOutside = false)) {
        Surface(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = VulkanSurfaceRaised,
            contentColor = VulkanTextPrimary,
            border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant),
            tonalElevation = 8.dp,
            shadowElevation = 10.dp
        ) {
            Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(shape = MaterialTheme.shapes.extraLarge, color = VulkanSurfaceTonal, contentColor = VulkanTextPrimary, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant)) {
                    Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = ::navigateUp, enabled = !atRoot && !loading && !busy, colors = IconButtonDefaults.iconButtonColors(containerColor = VulkanSurfaceLow, contentColor = VulkanTextPrimary, disabledContainerColor = VulkanSurfaceLow, disabledContentColor = VulkanTextMuted)) {
                            Icon(painterResource(R.drawable.ic_back), contentDescription = "Parent folder")
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(request.title, color = VulkanTextPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(directoryPath.ifBlank { "Shared storage unavailable" }, color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                        IconButton(onClick = onDismiss, enabled = !busy, colors = IconButtonDefaults.iconButtonColors(containerColor = VulkanSurfaceLow, contentColor = VulkanTextPrimary, disabledContainerColor = VulkanSurfaceLow, disabledContentColor = VulkanTextMuted)) {
                            Icon(painterResource(R.drawable.ic_close), contentDescription = "Close")
                        }
                    }
                }

                Surface(shape = MaterialTheme.shapes.large, color = VulkanSurfaceLow, contentColor = VulkanTextSecondary) {
                    Text(request.description, modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp), color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                }

                ExpressiveSearchField(
                    value = search,
                    onValueChange = { search = it.take(120) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !busy,
                    labelText = if (request.mode == SharedStorageBrowserMode.IMPORT) "Search folders and files" else "Search folders",
                    placeholderText = if (request.mode == SharedStorageBrowserMode.IMPORT) "Folders and supported files" else "Folders"
                )

                if (request.mode == SharedStorageBrowserMode.EXPORT) {
                    Surface(shape = MaterialTheme.shapes.large, color = VulkanSurfaceTonal, contentColor = VulkanTextPrimary, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant)) {
                        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("File name", color = VulkanTextSecondary, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = filenameBase,
                                    onValueChange = { value -> filenameBase = value.take(160).filterNot { it == '/' || it == '\\' || it.code < 0x20 } },
                                    modifier = Modifier.weight(1f),
                                    enabled = !busy,
                                    singleLine = true,
                                    label = { Text("Name") },
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VulkanAccentSoft, unfocusedBorderColor = VulkanOutline, focusedTextColor = VulkanTextPrimary, unfocusedTextColor = VulkanTextPrimary, cursorColor = VulkanAccentSoft)
                                )
                                Surface(shape = RoundedCornerShape(16.dp), color = VulkanAccentContainer, contentColor = VulkanAccentSoft, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.35f))) {
                                    Text(exportExtension?.let { ".$it" } ?: "type", modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp), color = VulkanAccentSoft, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                                }
                            }
                            Text("The file type is fixed for this export; only the name can be changed.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                status?.let { message ->
                    val warning = message.contains("failed", true) || message.contains("invalid", true) || message.contains("unavailable", true) || message.contains("limit", true)
                    Surface(shape = MaterialTheme.shapes.medium, color = if (warning) ComposeColor(0xFF2A2115) else VulkanSurfaceTonal, contentColor = if (warning) ComposeColor(0xFFFFC857) else VulkanTextSecondary, border = androidx.compose.foundation.BorderStroke(1.dp, if (warning) ComposeColor(0xFF55401E) else VulkanOutlineVariant)) {
                        Text(message, modifier = Modifier.fillMaxWidth().padding(10.dp), color = if (warning) ComposeColor(0xFFFFC857) else VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                }

                Box(Modifier.weight(1f).fillMaxWidth()) {
                    if (loading) {
                        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            LoadingIndicator(color = VulkanAccentSoft)
                            Text("Reading shared storage…", color = VulkanTextSecondary)
                        }
                    } else {
                        val listState = rememberLazyListState()
                        LazyColumn(state = listState, modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 8.dp)) {
                            items(filteredFolders, key = { "shared-folder:$it" }) { path -> SharedStorageFolderRow(path = path, enabled = !busy) { directoryPath = path } }
                            if (request.mode == SharedStorageBrowserMode.IMPORT) {
                                items(filteredFiles, key = { "shared-file:${it.path}" }) { entry ->
                                    SharedStorageFileRow(entry = entry, enabled = !busy) {
                                        if (busy) return@SharedStorageFileRow
                                        busy = true
                                        status = "Validating ${entry.name}…"
                                        scope.launch {
                                            val result = try { onImport(File(entry.path)) }
                                            catch (cancelled: CancellationException) { busy = false; throw cancelled }
                                            catch (error: Throwable) { Result.failure(error) }
                                            busy = false
                                            result.onSuccess { status = it; onDismiss() }.onFailure { status = it.message ?: "Import failed" }
                                        }
                                    }
                                }
                            }
                            if (filteredFolders.isEmpty() && (request.mode == SharedStorageBrowserMode.EXPORT || filteredFiles.isEmpty())) item { EmptyState(if (search.isBlank()) "No matching folders or files in this location" else "No matching results") }
                        }
                        ExpressiveScrollHints(listState, Modifier.fillMaxSize().padding(horizontal = 6.dp, vertical = 6.dp))
                    }
                }

                Surface(shape = MaterialTheme.shapes.extraLarge, color = VulkanSurfaceLow, contentColor = VulkanTextPrimary, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant)) {
                    Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            if (request.mode == SharedStorageBrowserMode.IMPORT) "Only ${request.allowedExtensions.joinToString { ".$it" }} files are shown. Selection is revalidated before import." else "Choose a folder and name. Saving uses an atomic temporary write and asks before replacing an existing file.",
                            modifier = Modifier.weight(1f), color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall
                        )
                        if (busy) LoadingIndicator(color = VulkanAccentSoft, modifier = Modifier.size(30.dp))
                        else if (request.mode == SharedStorageBrowserMode.EXPORT) {
                            ExpressiveContainedIconTextButton("Save", R.drawable.ic_save, enabled = root != null && directory != null && filenameBase.trim().isNotBlank() && exportExtension != null) {
                                val extension = exportExtension ?: return@ExpressiveContainedIconTextButton
                                val fixedName = "${filenameBase.trim()}.$extension"
                                val target = try { validatedSharedStorageDestination(directory ?: return@ExpressiveContainedIconTextButton, fixedName, setOf(extension)) }
                                catch (error: Throwable) { status = error.message ?: "Invalid destination"; return@ExpressiveContainedIconTextButton }
                                if (target.exists()) pendingOverwrite = target else runExport(target)
                            }
                        }
                    }
                }
            }
        }
    }

    pendingOverwrite?.let { target ->
        AlertDialog(
            onDismissRequest = { if (!busy) pendingOverwrite = null },
            containerColor = VulkanSurfaceRaised,
            titleContentColor = VulkanTextPrimary,
            textContentColor = VulkanTextSecondary,
            title = { QuestionDialogTitle("Overwrite existing file?") },
            text = { Text("${target.name} already exists in this folder. Replace it with the new VulkanScope export?") },
            confirmButton = { ExpressiveContainedIconTextButton("Replace", R.drawable.ic_save, enabled = !busy) { pendingOverwrite = null; runExport(target) } },
            dismissButton = { ExpressiveCancelButton(enabled = !busy) { pendingOverwrite = null } }
        )
    }
}

@Composable
private fun SharedStorageFolderRow(path: String, enabled: Boolean, onOpen: () -> Unit) {
    val shape = MaterialTheme.shapes.large
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        color = VulkanSurfaceTonal,
        contentColor = VulkanTextPrimary,
        border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant),
        tonalElevation = 1.dp
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(shape = RoundedCornerShape(14.dp), color = ComposeColor(0xFF2A2418), contentColor = ComposeColor(0xFFFFC857)) {
                Icon(painterResource(R.drawable.ic_folder), contentDescription = null, modifier = Modifier.padding(8.dp).size(24.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(File(path).name.ifBlank { path }, color = VulkanTextPrimary, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text("Folder", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
            }
            FileManagerNavigateArrow(enabled = enabled, description = "Open folder", onClick = onOpen)
        }
    }
}

@Composable
private fun SharedStorageFileRow(entry: SharedStorageFileEntry, enabled: Boolean, onSelect: () -> Unit) {
    val icon = when (entry.name.substringAfterLast('.', "").lowercase(java.util.Locale.ROOT)) {
        "html", "htm" -> R.drawable.ic_action_html
        "txt" -> R.drawable.ic_action_text
        else -> R.drawable.ic_export
    }
    val shape = MaterialTheme.shapes.large
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        color = VulkanSurfaceTonal,
        contentColor = VulkanTextPrimary,
        border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant),
        tonalElevation = 1.dp
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(shape = RoundedCornerShape(14.dp), color = VulkanAccentContainer, contentColor = VulkanAccentSoft) {
                Icon(painterResource(icon), contentDescription = null, modifier = Modifier.padding(8.dp).size(24.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(entry.name, color = VulkanTextPrimary, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text("${formatBytes(entry.sizeBytes)} · ${formatTimestampOrUnavailable(entry.modifiedAtMillis.takeIf { it > 0L })}", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            FileManagerNavigateArrow(enabled = enabled, description = "Select ${entry.name}", onClick = onSelect)
        }
    }
}


private fun vendorIdFromDisplay(value: String?): Long? {
    val text = value?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    return if (text.startsWith("0x", ignoreCase = true)) text.substring(2).toLongOrNull(16) else text.toLongOrNull()
}

@Composable
private fun SystemDriverVendorBadge(vendorId: String?) {
    val info = vendorInfo(vendorIdFromDisplay(vendorId) ?: -1L)
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = VulkanAccentContainer,
        contentColor = VulkanAccentSoft,
        border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.28f)),
        modifier = Modifier.size(50.dp)
    ) {
        Image(
            painter = painterResource(info.logo),
            contentDescription = "${info.name} GPU vendor",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize().padding(7.dp)
        )
    }
}

@Composable
private fun SystemDriverManagerRow(
    active: Boolean,
    enabled: Boolean,
    summary: SystemDriverSummary?,
    onDetails: () -> Unit,
    onActivate: () -> Unit
) {
    val shape = MaterialTheme.shapes.extraLarge
    Surface(
        modifier = Modifier.fillMaxWidth().then(tvBrowseModifier(shape)),
        shape = shape,
        color = if (active) ComposeColor(0xFF21191A) else VulkanSurfaceRaised,
        contentColor = VulkanTextPrimary,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (active) VulkanAccentSoft.copy(alpha = 0.46f) else VulkanOutlineVariant),
        tonalElevation = if (active) 2.dp else 1.dp
    ) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SystemDriverVendorBadge(summary?.vendorId)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("System Vulkan® driver", color = VulkanTextPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text("Android platform Vulkan® source", color = VulkanTextSecondary, style = MaterialTheme.typography.labelMedium)
                }
                if (active) TurnipStatePill("ACTIVE", true, true)
                else ExpressiveContainedTextButton("Activate", enabled = enabled, onClick = onActivate)
            }
            if (summary != null) {
                ExpressiveInfoPill("GPU", summary.deviceName, Modifier.fillMaxWidth())
                ExpressiveInfoPill("Driver", summary.driverName ?: summary.driverInfo ?: summary.driverVersion, Modifier.fillMaxWidth())
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExpressiveInfoPill("Version", summary.driverVersion, Modifier.weight(1f))
                    ExpressiveInfoPill("Source", "System", Modifier.weight(1f))
                }
                HorizontalDivider(color = VulkanOutlineVariant)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    DetailAffordance(onDetails)
                }
            } else {
                Surface(shape = MaterialTheme.shapes.medium, color = VulkanSurfaceLow, contentColor = VulkanTextSecondary) {
                    Text(
                        "Detailed System driver evidence becomes available after a completed System-driver collection. Turnip evidence is never reused as System evidence.",
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        color = VulkanTextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun SystemDriverDetailsDialog(summary: SystemDriverSummary, evidenceSource: String, active: Boolean, onDismiss: () -> Unit) {
    ScrollableDetailDialog(title = "System Vulkan driver", onDismiss = onDismiss) {
        CapabilityKeyValue("State", if (active) "Active" else "Inactive · retained System evidence")
        CapabilityKeyValue("Evidence source", evidenceSource)
        CapabilityKeyValue("Captured", formatTimestampOrUnavailable(summary.capturedAtMillis))
        CapabilityKeyValue("GPU", summary.deviceName)
        CapabilityKeyValue("Vulkan® API", summary.apiVersion)
        CapabilityKeyValue("Driver version", summary.driverVersion)
        CapabilityKeyValue("Driver name", summary.driverName ?: "Not exposed by the completed System report")
        CapabilityKeyValue("Driver info", summary.driverInfo ?: "Not exposed by the completed System report")
        CapabilityKeyValue("Driver ID", summary.driverId ?: "Not exposed by the completed System report")
        CapabilityKeyValue("Conformance version", summary.conformanceVersion ?: "Not exposed by the completed System report")
        CapabilityKeyValue("Loader version", summary.loaderVersion)
        CapabilityKeyValue("Instance API version", summary.instanceApiVersion)
        CapabilityKeyValue("Vendor ID", summary.vendorId)
        CapabilityKeyValue("Device ID", summary.deviceId)
        CapabilityKeyValue("Device type", summary.deviceType)
        Text("The retained System summary is private UI metadata only and is not added to technicalReport, TXT/HTML exports or VulkanScope Database submissions.", color = VulkanTextMuted, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun UnavailableTurnipKeyValue(key: String, value: String) {
    Row(Modifier.fillMaxWidth().alpha(0.48f), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
        Text(trademarkVulkanDisplayText(key), color = VulkanTextMuted, modifier = Modifier.weight(0.82f), style = MaterialTheme.typography.labelSmall, textDecoration = TextDecoration.LineThrough)
        Text(trademarkVulkanDisplayText(value.ifBlank { "Unavailable" }), modifier = Modifier.weight(1.18f), color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall, textDecoration = TextDecoration.LineThrough)
    }
}

@Composable
private fun TurnipDriverManagerTable(
    drivers: List<ManagedTurnipDriver>,
    activeMode: DriverMode,
    enabled: Boolean,
    onDetails: (ManagedTurnipDriver) -> Unit,
    onActivate: (Int) -> Unit,
    onRemove: (ManagedTurnipDriver) -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val wide = maxWidth >= 620.dp && !preferExpandedTextLayout()
        val useTwoColumnPills = maxWidth >= 390.dp && !preferExpandedTextLayout()
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            drivers.forEach { rawDriver ->
                val driver = if (activeMode == DriverMode.TURNIP) rawDriver else rawDriver.copy(selected = false)
                val info = driver.info
                val unavailable = turnipDriverStateLabel(driver) == "UNAVAILABLE"
                val cardShape = MaterialTheme.shapes.extraLarge
                Surface(
                    modifier = Modifier.fillMaxWidth().alpha(if (unavailable) 0.68f else 1f).then(tvBrowseModifier(cardShape)),
                    shape = cardShape,
                    color = if (driver.selected) ComposeColor(0xFF21191A) else VulkanSurfaceRaised,
                    contentColor = VulkanTextPrimary,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (driver.selected) VulkanAccentSoft.copy(alpha = 0.46f) else VulkanOutlineVariant),
                    tonalElevation = if (driver.selected) 2.dp else 1.dp
                ) {
                    if (wide) {
                        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            MesaOfficialLogoBadge(size = 30.dp, muted = unavailable)
                            Surface(shape = RoundedCornerShape(14.dp), color = if (driver.selected) VulkanAccentContainer else VulkanSurfaceLow, contentColor = if (driver.selected) VulkanAccentSoft else VulkanTextSecondary) {
                                Text("%02d".format(java.util.Locale.ROOT, driver.slot), modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                            }
                            Column(Modifier.weight(1.25f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(info.driverName ?: if (info.installed) "Turnip package" else "Invalid package", color = VulkanTextPrimary, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Text(info.driverVersion ?: "Version not provided", color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall)
                            }
                            Column(Modifier.weight(1.35f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(info.zipName ?: "Source name unavailable", color = VulkanTextPrimary, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Text(info.zipSizeBytes?.let { android.text.format.Formatter.formatShortFileSize(androidx.compose.ui.platform.LocalContext.current, it) } ?: "Size unavailable", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                            }
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp), horizontalAlignment = Alignment.End) {
                                TurnipStatePill(turnipDriverStateLabel(driver), driver.selected, driver.sourceAvailable && info.installed)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    if (!unavailable) DetailAffordance { onDetails(driver) }
                                    if (!unavailable && !driver.selected) ExpressiveContainedTextButton("Activate", enabled = enabled && info.installed) { onActivate(driver.slot) }
                                    ExpressiveContainedIconTextButton("Remove", R.drawable.ic_delete, enabled = enabled) { onRemove(driver) }
                                }
                            }
                            }
                            HorizontalDivider(color = VulkanOutlineVariant)
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ExpressiveInfoPill("Vulkan® library", info.libraryName ?: "Not available", Modifier.weight(1f))
                                ExpressiveInfoPill("Description", info.description?.takeIf { it.isNotBlank() } ?: "Not provided", Modifier.weight(1f))
                            }
                        }
                    } else {
                        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                MesaOfficialLogoBadge(size = 34.dp, muted = unavailable)
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        info.driverName ?: if (info.installed) "Turnip package" else "Invalid package",
                                        color = VulkanTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        textDecoration = if (unavailable) TextDecoration.LineThrough else TextDecoration.None
                                    )
                                    Text(
                                        "Slot %02d · %s".format(java.util.Locale.ROOT, driver.slot, info.driverVersion ?: "version not provided"),
                                        color = VulkanTextSecondary,
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                TurnipStatePill(turnipDriverStateLabel(driver), driver.selected, driver.sourceAvailable && info.installed)
                            }
                            ExpressiveInfoPill("Source ZIP", info.zipName ?: "Source name unavailable", Modifier.fillMaxWidth())
                            ExpressiveInfoPill("Vulkan® library", info.libraryName ?: "Not available", Modifier.fillMaxWidth())
                            ExpressiveInfoPill("Description", info.description?.takeIf { it.isNotBlank() } ?: "Not provided", Modifier.fillMaxWidth())
                            if (useTwoColumnPills) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    ExpressiveInfoPill("Driver name", info.driverName ?: "Not provided", Modifier.weight(1f))
                                    ExpressiveInfoPill("Package", info.packageVersion ?: "Not provided", Modifier.weight(1f))
                                }
                            } else {
                                ExpressiveInfoPill("Driver name", info.driverName ?: "Not provided", Modifier.fillMaxWidth())
                                ExpressiveInfoPill("Package", info.packageVersion ?: "Not provided", Modifier.fillMaxWidth())
                            }
                            HorizontalDivider(color = VulkanOutlineVariant)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (!unavailable) DetailAffordance { onDetails(driver) }
                                if (!unavailable && !driver.selected) ExpressiveContainedTextButton("Activate", enabled = enabled && info.installed) { onActivate(driver.slot) }
                                ExpressiveContainedIconTextButton("Remove", R.drawable.ic_delete, enabled = enabled) { onRemove(driver) }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun turnipDriverStateLabel(driver: ManagedTurnipDriver): String = when {
    driver.selected -> "ACTIVE"
    driver.info.installed && driver.sourceAvailable -> "AVAILABLE"
    else -> "UNAVAILABLE"
}

private fun turnipDriverStateColor(driver: ManagedTurnipDriver): ComposeColor = when (turnipDriverStateLabel(driver)) {
    "ACTIVE" -> ComposeColor(0xFF73C991)
    "AVAILABLE" -> ComposeColor(0xFF9CCBFF)
    else -> ComposeColor(0xFFFFC857)
}

@Composable
private fun TurnipStatePill(label: String, active: Boolean, valid: Boolean) {
    val container = when {
        active -> ComposeColor(0xFF173421)
        valid -> ComposeColor(0xFF16344F)
        else -> ComposeColor(0xFF332A16)
    }
    val content = when {
        active -> ComposeColor(0xFF73C991)
        valid -> ComposeColor(0xFF9CCBFF)
        else -> ComposeColor(0xFFFFC857)
    }
    Surface(shape = RoundedCornerShape(999.dp), color = container) {
        Text(label, color = content, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
    }
}

@Composable
private fun TurnipDriverDetailsDialog(driver: ManagedTurnipDriver, onDismiss: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val info = driver.info
    ScrollableDetailDialog(title = "Turnip slot %02d".format(java.util.Locale.ROOT, driver.slot), onDismiss = onDismiss) {
        CapabilityKeyValue("State", turnipDriverStateLabel(driver).lowercase().replaceFirstChar { it.uppercase() })
        CapabilityKeyValue("Source availability", if (driver.sourceAvailable) "Available" else "Unavailable")
        Text("Source ZIP", color = VulkanTextPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        CapabilityKeyValue("ZIP name", info.zipName ?: "Not available")
        CapabilityKeyValue("Source location", info.zipLocation ?: "Not available")
        CapabilityKeyValue("ZIP size", info.zipSizeBytes?.let { android.text.format.Formatter.formatShortFileSize(context, it) } ?: "Not available")
        CapabilityKeyValue("ZIP modified", formatTimestampOrUnavailable(info.zipModifiedAtMillis))
        CapabilityKeyValue("Imported", formatTimestampOrUnavailable(info.importedAtMillis))
        Text("Driver package", color = VulkanTextPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        CapabilityKeyValue("Driver name", info.driverName ?: "Not provided by package")
        CapabilityKeyValue("Vulkan version", info.driverVersion ?: "Not provided by package")
        CapabilityKeyValue("Driver date", info.driverDate ?: "Not provided by package")
        CapabilityKeyValue("Package version", info.packageVersion ?: "Not provided by package")
        CapabilityKeyValue("Vendor", info.vendor ?: "Not provided by package")
        CapabilityKeyValue("Author", info.author ?: "Not provided by package")
        CapabilityKeyValue("Minimum Android API", info.minApi?.toString() ?: "Not provided by package")
        CapabilityKeyValue("Metadata schema", info.schemaVersion?.toString() ?: "Not available")
        CapabilityKeyValue("Vulkan® library", info.libraryName ?: "Not available")
        CapabilityKeyValue("Library size", info.librarySizeBytes?.let { android.text.format.Formatter.formatShortFileSize(context, it) } ?: "Not available")
        info.description?.let { CapabilityKeyValue("Description", it) }
        Text("Source-document provenance is private app metadata and is not added to technicalReport or VulkanScope Database submissions.", color = VulkanTextMuted, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun DriverOption(option: DriverMode, selected: Boolean, description: String, enabled: Boolean, onClick: () -> Unit) {
    val textColor = if (enabled) ComposeColor(0xFFFFFFFF) else ComposeColor(0xFF666666)
    Card(
        colors = CardDefaults.cardColors(containerColor = if (selected) VulkanAccentContainer else VulkanSurfaceLow),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth().selectable(selected = selected, enabled = enabled, role = Role.RadioButton, onClick = onClick).semantics(mergeDescendants = true) { }
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            ExpressiveRadioButton(selected = selected, enabled = enabled, onClick = null)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(option.label, color = textColor, fontWeight = FontWeight.SemiBold)
                Text(description, color = if (enabled) ComposeColor(0xFF8F8F8F) else ComposeColor(0xFF555555), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun ExtensionsPage(report: VulkanReport, device: DeviceReport?) {
    val uriHandler = LocalUriHandler.current
    val networkAvailable = LocalValidatedNetwork.current
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("All") }
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
            ExpressiveMetricGrid(listOf("Visible entries" to total.toString(), "Embedded reference subset" to EMBEDDED_EXTENSION_REFERENCE_NAMES.size.toString()), Modifier.padding(top = 4.dp))
            }
        }
        items(filteredSupported, key = { "supported:${it.scope}:${it.name}:${it.specVersion}" }) { extension ->
            CapabilityItemCard(containerColor = VulkanSurfaceRaised) {
                Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(extension.name, fontWeight = FontWeight.SemiBold)
                    Text("SUPPORTED · ${extension.scope} · spec ${extension.specVersion}", color = ComposeColor(0xFF73C991), style = MaterialTheme.typography.labelSmall)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { DetailAffordance { selectedSupported = extension } }
                }
            }
        }
        items(filteredCatalog, key = { "catalog:$it" }) { name ->
            CapabilityItemCard(containerColor = ComposeColor(0xFF211B12)) {
                Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(name, fontWeight = FontWeight.SemiBold)
                    Text("NOT ENUMERATED · registry reference only", color = ComposeColor(0xFFFFC857), style = MaterialTheme.typography.labelSmall)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { DetailAffordance { selectedCatalog = name } }
                }
            }
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
                CapabilityKeyValue("Registry baseline", "Vulkan® 1.4.362")
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
                Text("Runtime enumeration, registry metadata and dedicated feature/property query evidence remain separate. The checked-in metadata is generated from the locked Vulkan® 1.4.362 registry; the Khronos link remains authoritative for the complete interface definition.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
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
                CapabilityKeyValue("Registry baseline", "Vulkan® 1.4.362")
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
                Text("This entry comes from the checked-in Vulkan® 1.4.362 registry census. Runtime absence is not an Unsupported claim; runtime enumeration and registry registration remain separate evidence.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
        }
    }
}


private fun imageFormatQueryGroupState(device: DeviceReport): Pair<String, String> {
    val raw = device.detailedProperties.lastOrNull { it.section == "Vulkan Query Status" && it.name == "Image Format Properties 2 query" }?.value.orEmpty()
    return when {
        raw == "Available" -> "available" to ""
        raw.startsWith("Unavailable: ") -> "unavailable" to raw.removePrefix("Unavailable: ").trim()
        raw.startsWith("Not applicable: ") -> "not_applicable" to raw.removePrefix("Not applicable: ").trim()
        else -> "unknown" to raw.trim()
    }
}

private fun technicalReportJson(context: Context, report: VulkanReport, display: DisplayReport, mode: DriverMode): JSONObject = JSONObject().apply {
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
            put("profileEvaluation", JSONArray().apply { vulkanProfileEvaluations(report, d).forEach { profile -> put(JSONObject().apply { put("name", profile.name); put("revision", profile.revision); put("status", profile.status); put("summary", profileSummary(profile)) }) } })
        }) }
    })
    put("profileCatalog", JSONArray().apply { vulkanProfileCatalog().forEach { value -> put(JSONObject().apply { put("name", value.name); put("revision", value.revision) }) } })
}

private fun databaseSubmissionJson(context: Context, report: VulkanReport, display: DisplayReport, mode: DriverMode): String {
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

private data class DatabaseSubmissionResult(val success: Boolean, val reportId: String?, val summary: String, val log: String)

private fun boundedDatabaseSubmissionLog(value: String): String {
    val limit = 96 * 1024
    return if (value.length <= limit) value else value.take(limit) + "\n[log truncated at $limit characters]"
}

private fun databaseSubmissionExceptionLog(phase: String, error: Throwable): String = boundedDatabaseSubmissionLog(buildString {
    appendLine("VulkanScope Database submission")
    appendLine("result=failure")
    appendLine("phase=$phase")
    appendLine("exception=${error.javaClass.name}")
    appendLine("message=${error.message ?: "Unavailable"}")
    append(error.stackTraceToString())
})

private fun databaseSubmissionFailure(summary: String, phase: String, detail: String, httpCode: Int? = null, responseBody: String? = null): DatabaseSubmissionResult {
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

private suspend fun submitDatabaseReport(context: Context, report: VulkanReport, display: DisplayReport, mode: DriverMode): DatabaseSubmissionResult = withContext(Dispatchers.IO) {
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

private fun safeFilePart(value: String): String = value.replace(Regex("[^A-Za-z0-9._-]+"), "_").trim('_').ifBlank { "Unknown-GPU" }

private fun exportFileStem(report: VulkanReport): String {
    if (report.devices.size > 1) return "VulkanScope-${report.devices.size}-GPUs-report"
    val gpuName = report.devices.firstOrNull()?.name?.trim().orEmpty().ifBlank { "Unknown-GPU" }
    return "VulkanScope-${safeFilePart(gpuName)}-report"
}

private data class ExportSnapshot(val filename: String, val path: String, val mime: String)

private fun exportSnapshotRoot(context: Context): File {
    val root = File(context.cacheDir, "report_exports")
    if (!root.exists() && !root.mkdirs()) throw IllegalStateException("Unable to create the report export cache")
    return root.canonicalFile
}

private fun validatedExportSnapshot(context: Context, snapshot: ExportSnapshot): File {
    if (snapshot.filename.isBlank() || snapshot.path.isBlank() || snapshot.mime !in setOf("text/plain", "text/html")) error("Invalid report export snapshot")
    val root = exportSnapshotRoot(context)
    val file = File(snapshot.path).canonicalFile
    val prefix = root.path.trimEnd(File.separatorChar) + File.separator
    if (!file.path.startsWith(prefix) || !file.isFile || !file.canRead() || file.length() <= 0L) error("Report export snapshot is unavailable")
    return file
}

private suspend fun createExportSnapshot(context: Context, filename: String, mime: String, contentFactory: () -> String): ExportSnapshot {
    require(mime == "text/plain" || mime == "text/html")
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

private fun deleteExportSnapshot(context: Context, snapshot: ExportSnapshot) {
    val root = runCatching { exportSnapshotRoot(context) }.getOrNull() ?: return
    val file = runCatching { File(snapshot.path).canonicalFile }.getOrNull() ?: return
    val prefix = root.path.trimEnd(File.separatorChar) + File.separator
    if (file.path.startsWith(prefix) && file.isFile) runCatching { file.delete() }
}

private fun cleanupStaleExportSnapshots(context: Context) {
    val root = runCatching { exportSnapshotRoot(context) }.getOrNull() ?: return
    val cutoff = System.currentTimeMillis() - 24L * 60L * 60L * 1000L
    root.listFiles().orEmpty().asSequence().filter { it.isFile && it.name.startsWith("vulkanscope_report_") }.take(512).forEach { file ->
        val canonical = runCatching { file.canonicalFile }.getOrNull() ?: return@forEach
        if (canonical.lastModified() < cutoff) runCatching { canonical.delete() }
    }
}

private fun reportGpuSummary(report: VulkanReport): String = when (report.devices.size) {
    0 -> "Unknown"
    1 -> report.devices.first().name
    else -> "${report.devices.size} physical devices"
}

private fun reportToText(context: Context, report: VulkanReport, display: DisplayReport, mode: DriverMode): String = buildString {
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
        vulkanProfileEvaluations(report, d).forEach { p -> appendLine("${p.name} | ${p.revision} | ${p.status} | ${profileSummary(p)}") }
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

private fun htmlEscape(value: String): String = value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")

private fun reportToHtml(context: Context, report: VulkanReport, display: DisplayReport, mode: DriverMode): String = buildString {
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
        context.resources.openRawResource(R.drawable.vulkanscope_logo_horizontal).use { input ->
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
        "${device.name} / ${evaluation.name}" to "${statusBadge(evaluation.status)} ${htmlEscape("${evaluation.revision}; ${profileSummary(evaluation)}")}" 
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

private enum class SupportFilter { ALL, SUPPORTED, UNSUPPORTED }

@Composable
private fun SupportFilterRow(selected: SupportFilter, onSelected: (SupportFilter) -> Unit) {
    val values = listOf(SupportFilter.ALL, SupportFilter.SUPPORTED, SupportFilter.UNSUPPORTED)
    val labels = listOf("All", "Supported", "Not supported")
    ExpressiveFilterBar(labels, values.indexOf(selected).coerceAtLeast(0)) { onSelected(values[it]) }
}

private val EMBEDDED_EXTENSION_REFERENCE_NAMES = VULKAN_EXTENSION_REFERENCE.keys

private fun List<String>.distinctScopes(): String = distinct().joinToString(" / ")

private data class NavigationItem(val page: Page, val label: String, val icon: Int)

private fun selectedNavigationPage(page: Page): Page = when (page) {
    Page.Features, Page.Memory, Page.Queues, Page.Video, Page.Formats, Page.Properties, Page.Encyclopedia, Page.Analysis, Page.Settings, Page.Info -> Page.Overview
    else -> page
}

@Composable
private fun AnimatedNavigationIcon(
    page: Page,
    icon: Int,
    trigger: Int,
    size: Dp,
    tint: ComposeColor? = null
) {
    val motion = remember(page) { androidx.compose.animation.core.Animatable(0f) }
    LaunchedEffect(trigger) {
        if (trigger > 0) {
            motion.snapTo(0f)
            motion.animateTo(1f, animationSpec = tween(durationMillis = 320))
            motion.snapTo(0f)
        }
    }
    val wave = kotlin.math.sin(motion.value * kotlin.math.PI).toFloat()
    val iconModifier = Modifier
        .size(size)
        .graphicsLayer {
            when (page) {
                Page.Overview -> {
                    scaleX = 1f + 0.16f * wave
                    scaleY = 1f + 0.16f * wave
                }
                Page.Vulkan -> {
                    rotationZ = -11f * wave
                    scaleX = 1f + 0.08f * wave
                    scaleY = 1f + 0.08f * wave
                }
                Page.Surface -> {
                    translationY = -5f * wave
                    rotationZ = 4f * wave
                }
                Page.Display -> {
                    scaleX = 1f + 0.12f * wave
                    scaleY = 1f - 0.10f * wave
                    alpha = 1f - 0.12f * wave
                }
                Page.Extensions -> {
                    rotationZ = 10f * wave
                    scaleX = 1f + 0.09f * wave
                    scaleY = 1f + 0.09f * wave
                }
                else -> {
                    scaleX = 1f + 0.08f * wave
                    scaleY = 1f + 0.08f * wave
                }
            }
        }
    if (tint == null) {
        Icon(painterResource(icon), contentDescription = null, modifier = iconModifier)
    } else {
        Icon(painterResource(icon), contentDescription = null, modifier = iconModifier, tint = tint)
    }
}

private fun navigationItems(): List<NavigationItem> = listOf(
    NavigationItem(Page.Overview, "Overview", R.drawable.ic_home),
    NavigationItem(Page.Vulkan, "Vulkan", R.drawable.ic_vulkan),
    NavigationItem(Page.Surface, "Surface", R.drawable.ic_surface),
    NavigationItem(Page.Display, "Display", R.drawable.ic_tablet),
    NavigationItem(Page.Extensions, "Extensions", R.drawable.ic_extensions)
)

private fun pageIcon(page: Page): Int = when (page) {
    Page.Overview -> R.drawable.ic_home
    Page.Vulkan -> R.drawable.ic_vulkan
    Page.Display -> R.drawable.ic_tablet
    Page.Surface -> R.drawable.ic_surface
    Page.Features -> R.drawable.ic_features
    Page.Memory -> R.drawable.ic_memory
    Page.Queues -> R.drawable.ic_queues
    Page.Video -> R.drawable.ic_video
    Page.Formats -> R.drawable.ic_formats
    Page.Properties -> R.drawable.ic_properties
    Page.Extensions -> R.drawable.ic_extensions
    Page.Profiles -> R.drawable.ic_profile
    Page.Encyclopedia -> R.drawable.ic_book
    Page.Analysis -> R.drawable.ic_analysis
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
    Page.Video -> 8
    Page.Formats -> 9
    Page.Properties -> 10
    Page.Profiles -> 11
    Page.Encyclopedia -> 12
    Page.Analysis -> 13
    Page.Settings -> 14
    Page.Info -> 15
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
    Card(colors = CardDefaults.cardColors(containerColor = VulkanSurfaceLow), shape = MaterialTheme.shapes.medium, modifier = modifier) {
        Image(
            painter = painterResource(info.logo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().padding(8.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun UpdateAvailableIcon() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = VulkanAccentContainer,
        modifier = Modifier.size(34.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(R.drawable.ic_update_available),
                contentDescription = null,
                tint = VulkanAccentSoft,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


@Composable
private fun UpdateSourceIcon() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = VulkanAccentContainer,
        modifier = Modifier.size(34.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(R.drawable.ic_download),
                contentDescription = null,
                tint = VulkanAccentSoft,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun UpdateStatusBadge(label: String) {
    Surface(shape = RoundedCornerShape(999.dp), color = ComposeColor(0xFF163D24)) {
        Text(label, color = ComposeColor(0xFF55D98A), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp))
    }
}

@Composable
private fun ConnectivityStatusHost(collectionStatus: CollectionStatus, networkStateKnown: Boolean, networkAvailable: Boolean, transitionState: NetworkBannerState) {
    var showPersistentOffline by remember { mutableStateOf(false) }
    LaunchedEffect(networkStateKnown, networkAvailable, transitionState) {
        showPersistentOffline = false
        if (networkStateKnown && !networkAvailable && transitionState == NetworkBannerState.HIDDEN) {
            delay(380L)
            showPersistentOffline = true
        }
    }
    NetworkStatusBanner(transitionState)
    AnimatedVisibility(
        visible = showPersistentOffline,
        enter = fadeIn(animationSpec = androidx.compose.animation.core.tween(240)) + expandVertically(animationSpec = androidx.compose.animation.core.tween(240)),
        exit = fadeOut(animationSpec = androidx.compose.animation.core.tween(220)) + shrinkVertically(animationSpec = androidx.compose.animation.core.tween(220))
    ) {
        OfflineFeatureAvailabilityBanner(collectionStatus == CollectionStatus.COLLECTING)
    }
}

@Composable
private fun OfflineFeatureAvailabilityBanner(collectionInProgress: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp).semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },
        color = VulkanSurfaceRaised,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 0.dp
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Surface(shape = RoundedCornerShape(50), color = ComposeColor(0xFF16344F), modifier = Modifier.size(34.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(painter = painterResource(R.drawable.ic_info), contentDescription = null, tint = ComposeColor(0xFF5CA9FF), modifier = Modifier.size(19.dp))
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text("Internet features are unavailable", color = ComposeColor(0xFF9CCBFF), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                Text(if (collectionInProgress) "Vulkan® collection continues offline. Internet-dependent actions remain locked by network state, while report-dependent actions also remain locked until collection completes." else "Vulkan® inspection stays available offline. Database submission/fetching, web links and update checks remain disabled until Android reports a validated internet connection.", color = VulkanTextSecondary, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun NetworkStatusBanner(state: NetworkBannerState) {
    var renderedState by remember { mutableStateOf(NetworkBannerState.CONNECTED) }
    LaunchedEffect(state) {
        if (state != NetworkBannerState.HIDDEN) renderedState = state
    }
    AnimatedVisibility(
        visible = state != NetworkBannerState.HIDDEN,
        enter = fadeIn(animationSpec = androidx.compose.animation.core.tween(240)) + expandVertically(animationSpec = androidx.compose.animation.core.tween(240)),
        exit = fadeOut(animationSpec = androidx.compose.animation.core.tween(360)) + shrinkVertically(animationSpec = androidx.compose.animation.core.tween(360))
    ) {
        val connected = renderedState == NetworkBannerState.CONNECTED
        val stateColor = if (connected) ComposeColor(0xFF55D98A) else ComposeColor(0xFFFF7676)
        val stateContainer = if (connected) ComposeColor(0xFF163D24) else ComposeColor(0xFF431C20)
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp).semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },
            color = VulkanSurfaceRaised,
            shape = MaterialTheme.shapes.large,
            tonalElevation = 0.dp
        ) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(shape = RoundedCornerShape(50), color = stateContainer, modifier = Modifier.size(34.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(if (connected) R.drawable.ic_network_connected else R.drawable.ic_network_disconnected),
                            contentDescription = null,
                            tint = stateColor,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text(if (connected) "Connected to network" else "No internet connection", color = stateColor, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    Text(if (connected) "Android reports a validated default network." else "Android does not currently report a validated default network.", color = VulkanTextSecondary, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun UpdateStatusBanner(status: UpdateStatus, onInstallUpdate: (AppUpdate) -> Unit) {
    AnimatedVisibility(
        visible = status !is UpdateStatus.Hidden,
        enter = fadeIn(animationSpec = androidx.compose.animation.core.tween(220)) + expandVertically(animationSpec = androidx.compose.animation.core.tween(220)),
        exit = fadeOut(animationSpec = androidx.compose.animation.core.tween(360)) + shrinkVertically(animationSpec = androidx.compose.animation.core.tween(360))
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp).semantics { liveRegion = LiveRegionMode.Polite },
            color = VulkanSurfaceRaised,
            contentColor = VulkanTextPrimary,
            shape = MaterialTheme.shapes.large,
            tonalElevation = 0.dp
        ) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                when (status) {
                    UpdateStatus.Checking -> { ExpressiveLinearProgressIndicator(Modifier.width(72.dp)); Text("Checking for updates…", color = VulkanTextSecondary, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f)) }
                    UpdateStatus.UpToDate -> { UpdateStatusBadge("UP TO DATE"); Text("VulkanScope is up to date.", color = VulkanTextSecondary, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f)) }
                    UpdateStatus.DirectUpdatesDisabledIntro -> { UpdateSourceIcon(); Text("Direct GitHub updates are currently disabled. Obtainium can manage updates externally, or direct updates can be enabled in Settings.", color = VulkanTextSecondary, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f)) }
                    is UpdateStatus.Available -> { UpdateAvailableIcon(); Text("VulkanScope ${status.update.version} available", color = VulkanAccentSoft, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f)); ChevronAffordance("Review", "Review update") { onInstallUpdate(status.update) } }
                    is UpdateStatus.Downloading -> { ExpressiveLinearProgressIndicator(Modifier.width(72.dp)); Text("Downloading update…", color = VulkanTextSecondary, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f)) }
                    is UpdateStatus.Failed -> Text(status.message, color = ComposeColor(0xFFFF8A8A), style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
                    UpdateStatus.Hidden -> Unit
                }
            }
        }
    }
}

@Composable
private fun UpdateDialogKeyValue(key: String, value: String) {
    val expandedTextLayout = preferExpandedTextLayout()
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val stacked = expandedTextLayout || maxWidth < 360.dp || key.length > 24 || value.length > 32 || value.contains("\n")
        val modifier = Modifier.fillMaxWidth().then(tvBrowseModifier(RoundedCornerShape(12.dp))).semantics(mergeDescendants = true) { }
        if (stacked) {
            Column(modifier.padding(vertical = 3.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(trademarkVulkanDisplayText(key), color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                Text(trademarkVulkanDisplayText(value.ifBlank { "Unavailable" }), color = VulkanTextPrimary, style = MaterialTheme.typography.bodySmall)
            }
        } else {
            Row(modifier.padding(vertical = 3.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                Text(trademarkVulkanDisplayText(key), color = VulkanTextMuted, modifier = Modifier.weight(0.82f), style = MaterialTheme.typography.labelSmall)
                Text(trademarkVulkanDisplayText(value.ifBlank { "Unavailable" }), modifier = Modifier.weight(1.18f), color = VulkanTextPrimary, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}


@Composable
private fun DirectUpdatesConsentDialog(appName: String, releaseSource: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = VulkanSurfaceRaised,
        titleContentColor = VulkanTextPrimary,
        textContentColor = VulkanTextPrimary,
        tonalElevation = 0.dp,
        title = { QuestionDialogTitle("Enable direct GitHub updates?") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("$appName will check for updates and download APKs directly from $releaseSource.", color = VulkanTextPrimary)
                Text("If you use Obtainium, leave this disabled so Obtainium remains the single update manager. Enabling direct updates makes the app independently check the same official GitHub Releases source and may duplicate update notifications.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = { ExpressivePrimaryButton("Enable direct updates", onClick = onConfirm) },
        dismissButton = { ExpressiveCancelButton(onClick = onDismiss) }
    )
}

@Composable
private fun UpdateConfirmationDialog(update: AppUpdate, networkAvailable: Boolean, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    val expandedTextLayout = preferExpandedTextLayout()
    val releaseNotesMaxHeight = if (expandedTextLayout) 220.dp else 360.dp
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = VulkanSurfaceRaised,
        titleContentColor = VulkanTextPrimary,
        textContentColor = VulkanTextPrimary,
        tonalElevation = 0.dp,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                SemanticDialogTitle("Download VulkanScope ${update.version}?", R.drawable.ic_update_available)
                Text("Review the target build and release notes before any APK download starts.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(shape = MaterialTheme.shapes.medium, color = VulkanSurfaceTonal, contentColor = VulkanTextPrimary) {
                    Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        UpdateDialogKeyValue("Installed version", "${update.installedVersion} (versionCode ${update.installedVersionCode})")
                        UpdateDialogKeyValue("Available release", update.version)
                        UpdateDialogKeyValue("Installed ABI", update.installedAbi)
                        UpdateDialogKeyValue("Download ABI", update.downloadAbi)
                        UpdateDialogKeyValue("APK asset", update.assetName)
                        UpdateDialogKeyValue("Downloaded versionCode", "Verified from the APK before installation")
                    }
                }
                Text("Release notes", color = VulkanTextPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Surface(shape = MaterialTheme.shapes.medium, color = VulkanSurfaceLow, contentColor = VulkanTextPrimary) {
                    ReleaseNotesContent(update.releaseNotes, Modifier.fillMaxWidth().heightIn(max = releaseNotesMaxHeight))
                }
                Text("The APK is validated for official release provenance, package identity, signing certificate, versionCode and versionName before Android's installer is opened.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                if (!networkAvailable) Text("Download is disabled until Android reports a validated internet connection.", color = ComposeColor(0xFF9CCBFF), style = MaterialTheme.typography.labelSmall)
            }
        },
        confirmButton = { ExpressivePrimaryIconTextButton("Download update", R.drawable.ic_download_update, enabled = networkAvailable, onClick = onConfirm) },
        dismissButton = { ExpressiveCancelButton(onClick = onDismiss) }
    )
}

private fun formatUpdateBytesDisplay(bytes: Long): String {
    if (bytes < 1024L) return "$bytes B"
    val units = listOf("KiB", "MiB", "GiB")
    var value = bytes.toDouble()
    var unitIndex = -1
    while (value >= 1024.0 && unitIndex < units.lastIndex) {
        value /= 1024.0
        unitIndex += 1
    }
    return if (value >= 100.0) "%.0f %s".format(java.util.Locale.US, value, units[unitIndex]) else "%.1f %s".format(java.util.Locale.US, value, units[unitIndex])
}

private fun formatUpdateSpeedDisplay(bytesPerSecond: Long): String =
    if (bytesPerSecond <= 0L) "—" else "${formatUpdateBytesDisplay(bytesPerSecond)}/s"

@Composable
private fun UpdateTransferDialog(
    state: UpdateTransferState,
    networkAvailable: Boolean,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onRequestCancel: () -> Unit,
    onInstall: () -> Unit,
    onClose: () -> Unit
) {
    val terminal = state.phase in setOf(UpdateTransferPhase.COMPLETED, UpdateTransferPhase.CANCELED, UpdateTransferPhase.FAILED)
    val title = when (state.phase) {
        UpdateTransferPhase.CONNECTING -> "Connecting to update"
        UpdateTransferPhase.DOWNLOADING -> "Downloading VulkanScope ${state.update.version}"
        UpdateTransferPhase.PAUSED -> "Update download paused"
        UpdateTransferPhase.VERIFYING -> "Verifying update"
        UpdateTransferPhase.COMPLETED -> "Update downloaded"
        UpdateTransferPhase.CANCELED -> "Update canceled"
        UpdateTransferPhase.FAILED -> "Update download failed"
    }
    val titleIcon = when (state.phase) {
        UpdateTransferPhase.COMPLETED -> R.drawable.ic_check
        UpdateTransferPhase.CANCELED -> R.drawable.ic_close
        UpdateTransferPhase.FAILED -> R.drawable.ic_network_disconnected
        else -> R.drawable.ic_download_update
    }
    val connection = when {
        state.phase == UpdateTransferPhase.CANCELED -> "Canceled"
        state.phase == UpdateTransferPhase.FAILED -> "Error"
        state.phase == UpdateTransferPhase.COMPLETED -> "Completed"
        state.phase == UpdateTransferPhase.PAUSED -> "Paused"
        !networkAvailable && state.phase in setOf(UpdateTransferPhase.CONNECTING, UpdateTransferPhase.DOWNLOADING) -> "Connection unavailable"
        else -> state.connectionStatus
    }
    val progressText = state.totalBytes?.takeIf { it > 0L }?.let { total ->
        val percent = ((state.bytesDownloaded.toDouble() / total.toDouble()) * 100.0).coerceIn(0.0, 100.0)
        "${formatUpdateBytesDisplay(state.bytesDownloaded)} / ${formatUpdateBytesDisplay(total)} (${"%.1f".format(java.util.Locale.US, percent)}%)"
    } ?: formatUpdateBytesDisplay(state.bytesDownloaded)
    val logState = rememberLazyListState()
    LaunchedEffect(state.log.size) {
        if (state.log.isNotEmpty()) logState.animateScrollToItem(state.log.lastIndex)
    }
    AlertDialog(
        onDismissRequest = { if (terminal) onClose() },
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = VulkanSurfaceRaised,
        titleContentColor = VulkanTextPrimary,
        textContentColor = VulkanTextPrimary,
        tonalElevation = 0.dp,
        title = { SemanticDialogTitle(title, titleIcon) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (state.phase in setOf(UpdateTransferPhase.CONNECTING, UpdateTransferPhase.DOWNLOADING, UpdateTransferPhase.VERIFYING)) {
                    ExpressiveLinearProgressIndicator(Modifier.fillMaxWidth())
                }
                Surface(shape = MaterialTheme.shapes.medium, color = VulkanSurfaceTonal, contentColor = VulkanTextPrimary) {
                    Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        UpdateDialogKeyValue("Connection", connection)
                        UpdateDialogKeyValue("Download speed", formatUpdateSpeedDisplay(state.bytesPerSecond))
                        UpdateDialogKeyValue("Downloaded", progressText)
                        UpdateDialogKeyValue("APK asset", state.update.assetName)
                    }
                }
                state.errorMessage?.let {
                    Text(it, color = ComposeColor(0xFFFF8A8A), style = MaterialTheme.typography.bodySmall)
                }
                Text("Live log", color = VulkanTextPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Surface(shape = MaterialTheme.shapes.medium, color = VulkanBlack, contentColor = VulkanTextPrimary) {
                    Box(Modifier.fillMaxWidth().heightIn(min = 120.dp, max = 220.dp).padding(12.dp)) {
                        LazyColumn(
                            state = logState,
                            modifier = Modifier.fillMaxWidth().focusGroup(),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            userScrollEnabled = true
                        ) {
                            itemsIndexed(state.log, key = { index, _ -> "update-log:$index" }) { _, line ->
                                Text(line, color = ComposeColor(0xFFB9D6B9), style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))
                            }
                        }
                        ExpressiveScrollHints(logState, Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 2.dp))
                    }
                }
                if (state.phase == UpdateTransferPhase.COMPLETED) {
                    Text("The verified APK remains available here until you choose Install or Close.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                }
            }
        },
        confirmButton = {
            when (state.phase) {
                UpdateTransferPhase.CONNECTING, UpdateTransferPhase.DOWNLOADING, UpdateTransferPhase.PAUSED ->
                    ExpressiveContainedIconTextButton("Cancel", R.drawable.ic_close, onClick = onRequestCancel)
                UpdateTransferPhase.COMPLETED ->
                    ExpressivePrimaryIconTextButton("Install", R.drawable.ic_download_update, onClick = onInstall)
                else -> Unit
            }
        },
        dismissButton = {
            when (state.phase) {
                UpdateTransferPhase.CONNECTING, UpdateTransferPhase.DOWNLOADING -> ExpressiveTextButton("Pause", onClick = onPause)
                UpdateTransferPhase.PAUSED -> ExpressiveTextButton("Resume", onClick = onResume)
                UpdateTransferPhase.COMPLETED, UpdateTransferPhase.CANCELED, UpdateTransferPhase.FAILED -> ExpressiveCloseButton(onClick = onClose)
                UpdateTransferPhase.VERIFYING -> Unit
            }
        }
    )
}

@Composable
private fun UpdateCancelConfirmationDialog(onResume: () -> Unit, onConfirmCancel: () -> Unit) {
    AlertDialog(
        onDismissRequest = onResume,
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = VulkanSurfaceRaised,
        titleContentColor = VulkanTextPrimary,
        textContentColor = VulkanTextPrimary,
        tonalElevation = 0.dp,
        title = { QuestionDialogTitle("Cancel update download?") },
        text = {
            Text("The download has been paused. Canceling removes the partial APK. Resume continues the current download.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodyMedium)
        },
        confirmButton = { ExpressiveContainedIconTextButton("Cancel download", R.drawable.ic_close, onClick = onConfirmCancel) },
        dismissButton = { ExpressiveTextButton("Resume", onClick = onResume) }
    )
}

@Composable
private fun ReleaseNotesContent(markdown: String, modifier: Modifier = Modifier) {
    val lines = remember(markdown) { markdown.lines() }
    val listState = rememberLazyListState()
    Box(modifier.padding(14.dp)) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth().focusGroup(),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            userScrollEnabled = true
        ) {
            itemsIndexed(lines, key = { index, _ -> "release-note:$index" }) { _, raw ->
                ReleaseNoteLine(raw)
            }
        }
        ExpressiveScrollHints(listState, Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 6.dp))
    }
}

@Composable
private fun ReleaseNoteLine(raw: String) {
    val line = raw.trimEnd()
    if (line.isBlank() || line.startsWith("```")) {
        Spacer(Modifier.height(if (line.isBlank()) 3.dp else 1.dp))
        return
    }
    val shape = RoundedCornerShape(10.dp)
    val isHeading = line.startsWith("# ") || line.startsWith("## ") || line.startsWith("### ")
    val text = when {
        line.startsWith("### ") -> line.removePrefix("### ")
        line.startsWith("## ") -> line.removePrefix("## ")
        line.startsWith("# ") -> line.removePrefix("# ")
        line.startsWith("- ") || line.startsWith("* ") -> "• " + line.drop(2)
        line.startsWith("> ") -> line.drop(2)
        else -> line
    }
    val style = when {
        line.startsWith("### ") -> MaterialTheme.typography.titleSmall
        line.startsWith("## ") -> MaterialTheme.typography.titleMedium
        line.startsWith("# ") -> MaterialTheme.typography.titleLarge
        else -> MaterialTheme.typography.bodySmall
    }
    val weight = when {
        line.startsWith("# ") -> FontWeight.Bold
        line.startsWith("## ") || line.startsWith("### ") -> FontWeight.SemiBold
        else -> FontWeight.Normal
    }
    val color = when {
        line.startsWith("# ") -> ComposeColor.White
        line.startsWith("## ") -> ComposeColor(0xFFF7F2F3)
        line.startsWith("### ") -> ComposeColor(0xFFF3EDEF)
        line.startsWith("> ") -> ComposeColor(0xFFFFB4BC)
        line.startsWith("- ") || line.startsWith("* ") -> ComposeColor(0xFFD3CBCD)
        else -> ComposeColor(0xFFBEB6B8)
    }
    Surface(
        color = ComposeColor.Transparent,
        shape = shape,
        modifier = Modifier.fillMaxWidth().then(tvBrowseModifier(shape)).semantics(mergeDescendants = true) { if (isHeading) heading() }
    ) {
        Text(
            trademarkVulkanDisplayText(text),
            color = color,
            style = style,
            fontWeight = weight,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
        val failed = status == CollectionStatus.FAILED
        Surface(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp).semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },
            color = VulkanSurfaceRaised,
            shape = MaterialTheme.shapes.large,
            tonalElevation = 0.dp
        ) {
            Column(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (collecting) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = VulkanAccentContainer,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_action_update),
                                    contentDescription = null,
                                    tint = VulkanAccentSoft,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Text(
                            "VulkanScope is collecting Vulkan information in the background.",
                            color = ComposeColor(0xFF9E9E9E),
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        val stateColor = if (failed) ComposeColor(0xFFFF7676) else ComposeColor(0xFF55D98A)
                        val stateContainer = if (failed) ComposeColor(0xFF431C20) else ComposeColor(0xFF163D24)
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = stateContainer,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(if (failed) R.drawable.ic_close else R.drawable.ic_check),
                                    contentDescription = null,
                                    tint = stateColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text(
                            if (failed) "Failed" else "Completed",
                            color = stateColor,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            if (failed) "No complete Vulkan information was collected." else "Vulkan information updated.",
                            color = ComposeColor(0xFF9E9E9E),
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                if (collecting) ExpressiveLinearProgressIndicator(Modifier.fillMaxWidth())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LoadingView() {
    VulkanLazyPage(verticalSpacing = 12.dp, modifier = Modifier.background(VulkanBlack).semantics { liveRegion = LiveRegionMode.Polite }) {
        item {
            CapabilitySectionCard("Vulkan inspection") {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Surface(shape = MaterialTheme.shapes.large, color = VulkanAccentContainer) {
                        Box(Modifier.size(58.dp), contentAlignment = Alignment.Center) {
                            LoadingIndicator(color = VulkanAccentSoft, modifier = Modifier.size(34.dp))
                        }
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text("Inspecting Vulkan®…", color = VulkanTextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("Collecting the complete Vulkan® evidence set for this session.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Surface(shape = MaterialTheme.shapes.medium, color = VulkanSurfaceTonal) {
                    Column(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Collection in progress", color = VulkanTextPrimary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                        Text("Capability pages become available after the base report reaches a validated terminal state. Missing evidence is not converted into Unsupported while collection is incomplete.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                }
                ExpressiveLinearProgressIndicator(Modifier.fillMaxWidth())
                Text("Supported, Unsupported, Unavailable, Not applicable and Unknown remain separate evidence states.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Surface(color = VulkanSurfaceLow, shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(shape = MaterialTheme.shapes.medium, color = VulkanSurfaceTonal) {
                Icon(painterResource(R.drawable.ic_info), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.padding(9.dp).size(20.dp))
            }
            Text(trademarkVulkanDisplayText(message), color = VulkanTextSecondary, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        }
    }
}

private fun capabilitySectionIcon(title: String): Int = when {
    title.equals("Display", true) -> R.drawable.ic_tablet
    title.equals("Instance layers", true) -> R.drawable.ic_library
    title.equals("Device layers", true) -> R.drawable.ic_cpu
    title.equals("Developer", true) -> R.drawable.ic_code
    title.equals("Application", true) -> R.drawable.vulkanscope_scope_wordmark
    title.equals("Libraries", true) -> R.drawable.ic_library
    title.equals("Build toolchain", true) -> R.drawable.ic_build
    title.equals("Device ABI", true) -> R.drawable.ic_cpu
    title.equals("Android", true) || title.equals("Android runtime", true) || title.equals("Operating system", true) -> R.drawable.ic_android
    title.equals("Updates", true) || title.equals("Update preferences", true) -> R.drawable.ic_download
    title.equals("Encyclopedia", true) -> R.drawable.ic_book
    title.equals("Global Vulkan report search", true) -> R.drawable.ic_search
    title.equals("Analysis workspace", true) -> R.drawable.ic_analysis
    title.equals("Local session history", true) -> R.drawable.ic_history
    title.equals("Watched evidence", true) -> R.drawable.ic_watch_add
    title.equals("A/B summary", true) || title.equals("Diff summary", true) || title.equals("Offline report compare", true) || title.equals("System driver ↔ Turnip A/B", true) -> R.drawable.ic_compare
    title.equals("Capability dependency graph", true) || title.equals("Visual registry-reference graph", true) -> R.drawable.ic_graph
    title.equals("Optional active tests", true) || title.equals("Self-test result", true) -> R.drawable.ic_test
    title.equals("Vulkan registry / query engine", true) || title.equals("Raw structured technical Report", true) -> R.drawable.ic_registry
    title.equals("Export complete report", true) -> R.drawable.ic_surface
    title.equals("Compare with VulkanScope Database", true) -> R.drawable.ic_action_database
    title.equals("Database comparison summary", true) -> R.drawable.ic_compare
    title.equals("Database permalink & QR", true) -> R.drawable.ic_qr
    title.equals("VkSurfaceKHR", true) -> R.drawable.ic_surface_khr
    title.equals("Search surface formats / color spaces", true) -> R.drawable.ic_surface_search
    title.contains("Database", true) -> R.drawable.ic_action_database
    title.contains("profile", true) -> R.drawable.ic_profile
    title.contains("diagnostic", true) || title.equals("Query overview", true) || title.equals("Collection diagnostics", true) -> R.drawable.ic_evidence
    title.equals("Extension explorer", true) -> R.drawable.ic_extensions
    title.equals("Explore", true) -> R.drawable.ic_compass
    title.contains("quick access", true) -> R.drawable.ic_quick_access_grid
    title.contains("snapshot", true) || title.contains("inspection", true) -> R.drawable.ic_vulkan
    title.startsWith("Vulkan", true) -> R.drawable.ic_vulkan
    title.contains("driver", true) -> R.drawable.ic_vulkan
    title.contains("display", true) || title.contains("HDR", true) -> R.drawable.ic_display
    title.contains("surface", true) || title.contains("present", true) -> R.drawable.ic_surface
    title.contains("feature", true) || title.contains("requirement", true) || title.contains("evaluation", true) || title.contains("minimum", true) -> R.drawable.ic_features
    title.equals("Memory heaps", true) -> R.drawable.ic_memory_heap
    title.equals("Memory types", true) -> R.drawable.ic_memory_type
    title.contains("memory", true) || title.startsWith("Heap", true) -> R.drawable.ic_memory
    title.contains("queue", true) -> R.drawable.ic_queues
    title.contains("format", true) || title.contains("color space", true) -> R.drawable.ic_formats
    title.contains("extension", true) || title.contains("layer", true) -> R.drawable.ic_extensions
    title.contains("property", true) || title.contains("limit", true) || title.contains("runtime", true) -> R.drawable.ic_properties
    else -> R.drawable.ic_info
}


@Composable
private fun preferExpandedTextLayout(): Boolean {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    return configuration.fontScale >= 1.3f || configuration.screenWidthDp < 360
}

@Composable
private fun tvBrowseModifier(shape: Shape): Modifier {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isTelevision = configuration.uiMode and Configuration.UI_MODE_TYPE_MASK == Configuration.UI_MODE_TYPE_TELEVISION
    if (!isTelevision) return Modifier
    val requester = remember { BringIntoViewRequester() }
    var focused by remember { mutableStateOf(false) }
    LaunchedEffect(focused) { if (focused) requester.bringIntoView() }
    return Modifier
        .bringIntoViewRequester(requester)
        .onFocusChanged { state -> focused = state.isFocused }
        .focusable()
        .border(if (focused) 2.dp else 0.dp, if (focused) ComposeColor(0xFFE2676A) else ComposeColor.Transparent, shape)
}

@Composable
private fun ExpressiveIconButton(icon: Int, contentDescription: String, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        shapes = IconButtonDefaults.shapes(
            shape = RoundedCornerShape(18.dp),
            pressedShape = RoundedCornerShape(24.dp)
        ),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = VulkanSurfaceRaised,
            contentColor = VulkanTextPrimary
        ),
        modifier = Modifier.size(48.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = VulkanTextPrimary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun ExpressiveSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    labelText: String? = null,
    enabled: Boolean = true,
    placeholderText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        singleLine = true,
        shape = RoundedCornerShape(24.dp),
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = value.isNotEmpty(),
                enter = fadeIn(tween(150)) + slideInHorizontally(tween(150)) { it / 2 },
                exit = fadeOut(tween(120)) + slideOutHorizontally(tween(120)) { it / 2 }
            ) {
                IconButton(onClick = { onValueChange("") }, enabled = enabled, modifier = Modifier.size(40.dp)) {
                    Icon(painterResource(R.drawable.ic_close), contentDescription = "Clear search", modifier = Modifier.size(18.dp))
                }
            }
        },
        label = if (labelText == null) null else { { Text(labelText) } },
        placeholder = if (placeholderText == null) null else { { Text(placeholderText) } },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = VulkanTextPrimary,
            unfocusedTextColor = VulkanTextPrimary,
            focusedContainerColor = VulkanSurfaceTonal,
            unfocusedContainerColor = VulkanSurfaceLow,
            cursorColor = VulkanAccentSoft,
            focusedBorderColor = VulkanAccentSoft,
            unfocusedBorderColor = VulkanOutline,
            focusedLeadingIconColor = VulkanAccentSoft,
            unfocusedLeadingIconColor = VulkanTextMuted,
            focusedTrailingIconColor = VulkanAccentSoft,
            unfocusedTrailingIconColor = VulkanTextSecondary,
            focusedLabelColor = VulkanAccentSoft,
            unfocusedLabelColor = VulkanTextSecondary,
            focusedPlaceholderColor = VulkanTextSecondary,
            unfocusedPlaceholderColor = VulkanTextMuted
        )
    )
}

@Composable
private fun ExpressiveAssistChip(
    label: String,
    leadingIcon: Int? = null,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(18.dp),
        leadingIcon = if (leadingIcon == null) null else {
            {
                Icon(
                    painter = painterResource(leadingIcon),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        },
        label = { Text(label, fontWeight = FontWeight.Medium) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = VulkanSurfaceTonal,
            labelColor = VulkanTextPrimary,
            leadingIconContentColor = VulkanAccentSoft,
            disabledContainerColor = VulkanSurfaceLow,
            disabledLabelColor = VulkanTextMuted,
            disabledLeadingIconContentColor = VulkanTextMuted
        )
    )
}

@Composable
private fun ExpressiveSwitch(checked: Boolean, onCheckedChange: ((Boolean) -> Unit)?) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        thumbContent = if (checked) {
            {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    tint = VulkanAccent,
                    modifier = Modifier.size(14.dp)
                )
            }
        } else null,
        colors = SwitchDefaults.colors(
            checkedThumbColor = VulkanTextPrimary,
            checkedTrackColor = VulkanAccent,
            uncheckedThumbColor = VulkanTextSecondary,
            uncheckedTrackColor = VulkanSurfaceTonal,
            uncheckedBorderColor = VulkanOutline
        )
    )
}

@Composable
private fun ExpressiveRadioButton(selected: Boolean, enabled: Boolean, onClick: (() -> Unit)?) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (selected && enabled) ComposeColor(0xFF2A2022) else ComposeColor.Transparent
    ) {
        RadioButton(
            selected = selected,
            enabled = enabled,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = VulkanAccentSoft,
                unselectedColor = VulkanTextMuted,
                disabledSelectedColor = ComposeColor(0xFF6C696A),
                disabledUnselectedColor = ComposeColor(0xFF5A5758)
            )
        )
    }
}

@Composable
private fun QuestionDialogTitle(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(shape = RoundedCornerShape(18.dp), color = VulkanAccentContainer) {
            Icon(painterResource(R.drawable.ic_question), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.padding(9.dp).size(20.dp))
        }
        Text(trademarkVulkanDisplayText(text), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = VulkanTextPrimary, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun SemanticDialogTitle(text: String, icon: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(shape = RoundedCornerShape(18.dp), color = VulkanAccentContainer) {
            Icon(painterResource(icon), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.padding(9.dp).size(20.dp))
        }
        Text(trademarkVulkanDisplayText(text), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = VulkanTextPrimary, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ExpressiveCancelButton(enabled: Boolean = true, onClick: () -> Unit) {
    TextButton(onClick = onClick, enabled = enabled, colors = ButtonDefaults.textButtonColors(contentColor = VulkanAccentSoft, disabledContentColor = VulkanTextMuted)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                Icon(painterResource(R.drawable.ic_close), contentDescription = null, modifier = Modifier.size(17.dp))
            }
            Text("Cancel", fontWeight = FontWeight.Normal, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun ExpressiveCloseButton(enabled: Boolean = true, onClick: () -> Unit) {
    TextButton(onClick = onClick, enabled = enabled, colors = ButtonDefaults.textButtonColors(contentColor = VulkanAccentSoft, disabledContentColor = VulkanTextMuted)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                Icon(painterResource(R.drawable.ic_close), contentDescription = null, modifier = Modifier.size(17.dp))
            }
            Text("Close", fontWeight = FontWeight.Normal, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ExpressivePrimaryButton(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shapes = ButtonDefaults.shapes(
            shape = RoundedCornerShape(20.dp),
            pressedShape = RoundedCornerShape(26.dp)
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = VulkanAccent,
            contentColor = VulkanTextPrimary
        )
    ) {
        Text(trademarkVulkanDisplayText(label), fontWeight = FontWeight.SemiBold)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ExpressivePrimaryIconTextButton(label: String, icon: Int, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shapes = ButtonDefaults.shapes(
            shape = RoundedCornerShape(20.dp),
            pressedShape = RoundedCornerShape(26.dp)
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = VulkanAccent,
            contentColor = VulkanTextPrimary,
            disabledContainerColor = VulkanSurfaceLow,
            disabledContentColor = VulkanTextMuted
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            Icon(painterResource(icon), contentDescription = null, modifier = Modifier.size(18.dp))
            Text(label, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ExpressiveTextButton(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        shapes = ButtonDefaults.shapes(
            shape = RoundedCornerShape(18.dp),
            pressedShape = RoundedCornerShape(24.dp)
        ),
        colors = ButtonDefaults.textButtonColors(contentColor = VulkanAccentSoft)
    ) {
        Text(label, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ExpressiveContainedTextButton(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        shapes = ButtonDefaults.shapes(
            shape = RoundedCornerShape(18.dp),
            pressedShape = RoundedCornerShape(24.dp)
        ),
        colors = ButtonDefaults.textButtonColors(
            containerColor = VulkanAccentContainer,
            contentColor = VulkanAccentSoft,
            disabledContainerColor = VulkanSurfaceLow,
            disabledContentColor = VulkanTextMuted
        )
    ) {
        Text(label, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ExpressiveContainedIconTextButton(label: String, icon: Int, modifier: Modifier = Modifier, enabled: Boolean = true, fontWeight: FontWeight = FontWeight.SemiBold, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = 48.dp),
        shapes = ButtonDefaults.shapes(
            shape = RoundedCornerShape(18.dp),
            pressedShape = RoundedCornerShape(24.dp)
        ),
        colors = ButtonDefaults.textButtonColors(
            containerColor = VulkanAccentContainer,
            contentColor = VulkanAccentSoft,
            disabledContainerColor = VulkanSurfaceLow,
            disabledContentColor = VulkanTextMuted
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp), modifier = Modifier.heightIn(min = 24.dp)) {
            Icon(painterResource(icon), contentDescription = null, modifier = Modifier.size(18.dp))
            Text(trademarkVulkanDisplayText(label), fontWeight = fontWeight, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun ExpressiveLinearProgressIndicator(modifier: Modifier = Modifier) {
    LinearWavyProgressIndicator(
        modifier = modifier.height(12.dp),
        color = VulkanAccentSoft,
        trackColor = ComposeColor(0xFF2A2022)
    )
}

@Composable
private fun SectionHeaderIcon(title: String, sectionIcon: Int) {
    when {
        title.equals("Vulkan registry / query engine", true) -> SectionVectorBadgeIcon(R.drawable.ic_registry, "REG")
        title.equals("About", true) -> AboutSectionIcon()
        title.equals("Android runtime", true) -> AndroidRuntimeSectionIcon()
        title.equals("Extension explorer", true) -> SectionVectorBadgeIcon(R.drawable.ic_extensions, overlayIcon = R.drawable.ic_search)
        title.equals("Format explorer", true) -> SectionVectorBadgeIcon(R.drawable.ic_formats, overlayIcon = R.drawable.ic_search)
        title.equals("Instance layers", true) -> SectionVectorBadgeIcon(R.drawable.ic_library, overlayIcon = R.drawable.ic_extensions)
        title.equals("Device layers", true) -> SectionVectorBadgeIcon(R.drawable.ic_cpu, overlayIcon = R.drawable.ic_library)
        sectionIcon == R.drawable.vulkanscope_scope_wordmark -> {
            Image(
                painter = painterResource(sectionIcon),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 11.dp).width(32.dp).height(18.dp)
            )
        }
        sectionIcon == R.drawable.ic_android -> {
            Image(
                painter = painterResource(sectionIcon),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 12.dp).width(27.dp).height(16.dp)
            )
        }
        title.equals("VkSurfaceKHR", true) -> {
            Box(Modifier.padding(7.dp).size(27.dp)) {
                Icon(painter = painterResource(R.drawable.ic_surface), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.align(Alignment.TopStart).size(21.dp))
                Surface(shape = RoundedCornerShape(3.dp), color = VulkanSurface, modifier = Modifier.align(Alignment.BottomEnd).border(0.7.dp, VulkanAccentSoft, RoundedCornerShape(3.dp))) {
                    Text("KHR", color = VulkanAccentSoft, fontSize = 4.1.sp, lineHeight = 4.3.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 1.dp, vertical = 0.8.dp))
                }
            }
        }
        title.equals("Search surface formats / color spaces", true) -> {
            Box(Modifier.padding(7.dp).size(27.dp)) {
                Icon(painter = painterResource(R.drawable.ic_surface), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.align(Alignment.TopStart).size(21.dp))
                Surface(shape = RoundedCornerShape(4.dp), color = VulkanSurface, modifier = Modifier.align(Alignment.BottomEnd)) {
                    Icon(painter = painterResource(R.drawable.ic_search), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.padding(1.2.dp).size(9.dp))
                }
            }
        }
        title.equals("Queue query safety", true) -> {
            Box(Modifier.padding(7.dp).size(27.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_queues),
                    contentDescription = null,
                    tint = VulkanAccentSoft,
                    modifier = Modifier.align(Alignment.TopStart).size(21.dp)
                )
                Surface(shape = RoundedCornerShape(4.dp), color = VulkanSurface, modifier = Modifier.align(Alignment.BottomEnd)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_shield),
                        contentDescription = null,
                        tint = VulkanAccentSoft,
                        modifier = Modifier.padding(1.3.dp).size(9.dp)
                    )
                }
            }
        }
        title.equals("Export complete report", true) -> {
            Box(Modifier.padding(7.dp).size(27.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_surface),
                    contentDescription = null,
                    tint = VulkanAccentSoft,
                    modifier = Modifier.align(Alignment.TopStart).size(21.dp)
                )
                Surface(shape = RoundedCornerShape(4.dp), color = VulkanSurface, modifier = Modifier.align(Alignment.BottomEnd)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_export),
                        contentDescription = null,
                        tint = VulkanAccentSoft,
                        modifier = Modifier.padding(1.3.dp).size(9.dp)
                    )
                }
            }
        }
        title.equals("Surface + Display presentation evidence", true) -> {
            Box(Modifier.padding(7.dp).size(27.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_display),
                    contentDescription = null,
                    tint = VulkanAccentSoft,
                    modifier = Modifier.align(Alignment.TopStart).size(21.dp)
                )
                Surface(shape = RoundedCornerShape(4.dp), color = VulkanSurface, modifier = Modifier.align(Alignment.BottomEnd)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_surface),
                        contentDescription = null,
                        tint = VulkanAccentSoft,
                        modifier = Modifier.padding(1.5.dp).size(9.dp)
                    )
                }
            }
        }
        title.equals("Raw structured technical Report", true) -> {
            Box(Modifier.padding(7.dp).size(27.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_registry),
                    contentDescription = null,
                    tint = VulkanAccentSoft,
                    modifier = Modifier.align(Alignment.TopStart).size(21.dp)
                )
                Surface(
                    shape = RoundedCornerShape(3.dp),
                    color = VulkanSurface,
                    modifier = Modifier.align(Alignment.BottomEnd).border(0.7.dp, VulkanAccentSoft, RoundedCornerShape(3.dp))
                ) {
                    Text(
                        "JSON",
                        color = VulkanAccentSoft,
                        fontSize = 4.2.sp,
                        lineHeight = 4.5.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 1.1.dp, vertical = 0.8.dp)
                    )
                }
            }
        }
        title.equals("Compare with VulkanScope Database", true) -> {
            Box(Modifier.padding(7.dp).size(27.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_action_database),
                    contentDescription = null,
                    tint = VulkanAccentSoft,
                    modifier = Modifier.align(Alignment.TopStart).size(21.dp)
                )
                Surface(shape = RoundedCornerShape(4.dp), color = VulkanSurface, modifier = Modifier.align(Alignment.BottomEnd)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_compare),
                        contentDescription = null,
                        tint = VulkanAccentSoft,
                        modifier = Modifier.padding(1.2.dp).size(9.dp)
                    )
                }
            }
        }
        title.equals("HDR capabilities", true) || title.equals("HDR / wide-color surface detection", true) -> {
            DisplaySectionBadgeIcon("HDR")
        }
        title.equals("Supported display modes", true) -> {
            DisplaySectionBadgeIcon("MODE")
        }
        title.equals("Display ↔ Vulkan interpretation", true) -> {
            Box(Modifier.padding(7.dp).size(27.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_display),
                    contentDescription = null,
                    tint = VulkanAccentSoft,
                    modifier = Modifier.align(Alignment.TopStart).size(22.dp)
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = VulkanSurface,
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_surface),
                        contentDescription = null,
                        tint = VulkanAccentSoft,
                        modifier = Modifier.padding(1.5.dp).size(8.dp)
                    )
                }
            }
        }
        else -> {
            Icon(
                painter = painterResource(sectionIcon),
                contentDescription = null,
                tint = VulkanAccentSoft,
                modifier = Modifier.padding(10.dp).size(21.dp)
            )
        }
    }
}

@Composable
private fun SectionVectorBadgeIcon(primary: Int, badgeText: String? = null, overlayIcon: Int? = null) {
    Box(Modifier.padding(7.dp).size(27.dp)) {
        Icon(painterResource(primary), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.align(Alignment.TopStart).size(21.dp))
        Surface(shape = RoundedCornerShape(3.dp), color = VulkanSurface, modifier = Modifier.align(Alignment.BottomEnd).border(0.7.dp, VulkanAccentSoft, RoundedCornerShape(3.dp))) {
            if (overlayIcon != null) {
                Icon(painterResource(overlayIcon), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.padding(1.2.dp).size(9.dp))
            } else {
                Text(badgeText.orEmpty(), color = VulkanAccentSoft, fontSize = 4.0.sp, lineHeight = 4.3.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 1.dp, vertical = 0.8.dp))
            }
        }
    }
}

@Composable
private fun AndroidRuntimeSectionIcon() {
    Box(Modifier.padding(7.dp).size(27.dp)) {
        Image(painterResource(R.drawable.ic_android), contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier.align(Alignment.TopStart).width(22.dp).height(15.dp))
        Surface(shape = RoundedCornerShape(3.dp), color = VulkanSurface, modifier = Modifier.align(Alignment.BottomEnd).border(0.7.dp, VulkanAccentSoft, RoundedCornerShape(3.dp))) {
            Text("RUN", color = VulkanAccentSoft, fontSize = 4.0.sp, lineHeight = 4.3.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 1.dp, vertical = 0.8.dp))
        }
    }
}

@Composable
private fun AboutSectionIcon() {
    Box(Modifier.padding(6.dp).size(30.dp)) {
        Image(painterResource(R.drawable.vulkanscope_scope_wordmark), contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier.align(Alignment.CenterStart).width(27.dp).height(15.dp))
        Surface(shape = RoundedCornerShape(4.dp), color = VulkanSurface, modifier = Modifier.align(Alignment.BottomEnd)) {
            Icon(painterResource(R.drawable.ic_info), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.padding(1.2.dp).size(9.dp))
        }
    }
}

@Composable
private fun DisplaySectionBadgeIcon(label: String) {
    Box(Modifier.padding(7.dp).size(27.dp)) {
        Icon(
            painter = painterResource(R.drawable.ic_display),
            contentDescription = null,
            tint = VulkanAccentSoft,
            modifier = Modifier.align(Alignment.TopStart).size(22.dp)
        )
        Surface(
            shape = RoundedCornerShape(3.dp),
            color = VulkanSurface,
            modifier = Modifier.align(Alignment.BottomEnd).border(0.7.dp, VulkanAccentSoft, RoundedCornerShape(3.dp))
        ) {
            Text(
                label,
                color = VulkanAccentSoft,
                fontSize = if (label == "MODE") 4.2.sp else 5.2.sp,
                lineHeight = if (label == "MODE") 4.5.sp else 5.4.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 1.2.dp, vertical = 0.8.dp)
            )
        }
    }
}

@Composable
private fun CapabilitySectionCard(title: String, content: @Composable () -> Unit) {
    val shape = MaterialTheme.shapes.extraLarge
    Surface(
        color = ComposeColor(0xFF181516),
        shape = shape,
        modifier = Modifier.fillMaxWidth().then(tvBrowseModifier(shape))
    ) {
        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val sectionIcon = capabilitySectionIcon(title)
                Surface(shape = RoundedCornerShape(18.dp), color = ComposeColor(0xFF351719)) {
                    SectionHeaderIcon(title, sectionIcon)
                }
                Text(
                    trademarkVulkanDisplayText(title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = ComposeColor(0xFFF7F2F3),
                    modifier = Modifier.weight(1f).semantics { heading() }
                )
            }
            HorizontalDivider(color = ComposeColor(0xFF2A2527))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { content() }
        }
    }
}

@Composable
private fun CapabilityItemCard(
    containerColor: ComposeColor = VulkanSurfaceRaised,
    content: @Composable () -> Unit
) {
    val shape = MaterialTheme.shapes.large
    Surface(color = containerColor, shape = shape, modifier = Modifier.fillMaxWidth().then(tvBrowseModifier(shape))) {
        Column(Modifier.fillMaxWidth()) { content() }
    }
}

@Composable
private fun rememberFilterScrollBoundaryConnection(): NestedScrollConnection = remember {
    object : NestedScrollConnection {
        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
            return if (available.y != 0f) Offset(0f, available.y) else Offset.Zero
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            return if (available.y != 0f) Velocity(0f, available.y) else Velocity.Zero
        }
    }
}

@Composable
private fun ExpressiveSingleFilterSelector(
    labels: List<String>,
    selectedIndex: Int?,
    enabled: Boolean,
    indicatorTint: ComposeColor,
    onSelected: (Int) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var dropdownMounted by remember { mutableStateOf(false) }
    var dropdownVisible by remember { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    var page by rememberSaveable { mutableIntStateOf(0) }
    var pageField by remember { mutableStateOf(TextFieldValue("1")) }
    val pageSize = 50
    val showSearch = labels.size >= 5
    val indexed = remember(labels, query) {
        labels.mapIndexed { index, label -> index to label }
            .filter { (_, label) -> query.isBlank() || label.contains(query, ignoreCase = true) }
    }
    val pageCount = maxOf(1, (indexed.size + pageSize - 1) / pageSize)
    val selectedLabel = selectedIndex?.takeIf { it in labels.indices }?.let(labels::get) ?: "Select filter"
    val selectorAlpha by animateFloatAsState(if (enabled) 1f else 0.52f, animationSpec = tween(180), label = "filterSelectorAlpha")
    val selectorScale by animateFloatAsState(if (enabled) 1f else 0.985f, animationSpec = tween(180), label = "filterSelectorScale")
    val arrowRotation by animateFloatAsState(if (expanded) 180f else 0f, animationSpec = tween(180), label = "filterSelectorArrow")
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    val imeVisible = WindowInsets.ime.getBottom(density) > 0

    LaunchedEffect(enabled) {
        if (!enabled) expanded = false
    }
    LaunchedEffect(showSearch) {
        if (!showSearch && query.isNotEmpty()) query = ""
    }
    LaunchedEffect(expanded, enabled) {
        if (expanded && enabled) {
            query = ""
            val openingPage = selectedIndex?.takeIf { it in labels.indices }?.div(pageSize) ?: 0
            page = openingPage.coerceIn(0, pageCount - 1)
            pageField = TextFieldValue((page + 1).toString())
            dropdownMounted = true
            dropdownVisible = false
            delay(20)
            dropdownVisible = true
        } else {
            dropdownVisible = false
            delay(190)
            dropdownMounted = false
        }
    }
    LaunchedEffect(query, pageCount) {
        page = page.coerceIn(0, pageCount - 1)
        if (pageField.text.isNotEmpty()) pageField = TextFieldValue((page + 1).toString())
    }
    LaunchedEffect(page, pageCount) {
        page = page.coerceIn(0, pageCount - 1)
        if (pageField.text.isNotEmpty()) pageField = TextFieldValue((page + 1).toString())
    }

    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val configuration = androidx.compose.ui.platform.LocalConfiguration.current
        val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val dropdownWidth = if (landscape) maxWidth else maxWidth.coerceAtMost(560.dp)
        val screenHeight = configuration.screenHeightDp.dp
        val imeHeight = with(density) { WindowInsets.ime.getBottom(density).toDp() }
        val usableHeight = (screenHeight - imeHeight).coerceAtLeast(220.dp)
        val dropdownMaxHeight = if (landscape) {
            ((usableHeight.value * 0.62f).dp).coerceIn(220.dp, 440.dp)
        } else {
            (usableHeight - 96.dp).coerceIn(240.dp, 620.dp)
        }
        val visibleRows = indexed.drop(page * pageSize).take(pageSize).size.coerceIn(1, if (landscape) 4 else 7)
        val dropdownDesiredHeight = (
            28.dp +
                (if (showSearch) 96.dp else 0.dp) +
                (58.dp * visibleRows) +
                (if (pageCount > 1 && indexed.isNotEmpty()) 78.dp else 0.dp)
            ).coerceAtMost(dropdownMaxHeight)
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            val selectorShape = MaterialTheme.shapes.medium
            Surface(
                shape = selectorShape,
                color = VulkanSurfaceTonal,
                contentColor = VulkanTextPrimary,
                border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutline),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 58.dp)
                    .alpha(selectorAlpha)
                    .graphicsLayer(scaleX = selectorScale, scaleY = selectorScale)
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text("Filter", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                        Text(
                            trademarkVulkanDisplayText(selectedLabel),
                            color = VulkanTextPrimary,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    val arrowShape = RoundedCornerShape(15.dp)
                    Surface(
                        shape = arrowShape,
                        color = if (expanded) VulkanAccent else VulkanAccentContainer,
                        contentColor = indicatorTint,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(arrowShape)
                            .clickable(enabled = enabled, role = Role.Button) { expanded = !expanded }
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(R.drawable.ic_expand_more),
                                contentDescription = if (expanded) "Close filter menu" else "Open filter menu",
                                tint = indicatorTint,
                                modifier = Modifier.size(24.dp).graphicsLayer(rotationZ = arrowRotation)
                            )
                        }
                    }
                }
            }

            BackHandler(enabled = dropdownMounted && expanded) {
                if (imeVisible) focusManager.clearFocus(force = true)
            }

            if (dropdownMounted && enabled) {
                AnimatedVisibility(
                    visible = dropdownVisible,
                    enter = fadeIn(tween(220)) + scaleIn(tween(240), initialScale = 0.97f, transformOrigin = TransformOrigin(0.5f, 0f)) + expandVertically(tween(220), expandFrom = Alignment.Top),
                    exit = fadeOut(tween(150)) + scaleOut(tween(170), targetScale = 0.985f, transformOrigin = TransformOrigin(0.5f, 0f)) + shrinkVertically(tween(170), shrinkTowards = Alignment.Top)
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = VulkanSurfaceRaised,
                        contentColor = VulkanTextPrimary,
                        border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutline),
                        shadowElevation = 8.dp,
                        modifier = Modifier.width(dropdownWidth).height(dropdownDesiredHeight)
                    ) {
                        Column(Modifier.fillMaxHeight().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (showSearch) {
                                ExpressiveSearchField(
                                    value = query,
                                    onValueChange = { value ->
                                        query = value.take(120)
                                        page = 0
                                        pageField = TextFieldValue("1")
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    labelText = "Search filters",
                                    placeholderText = "Type a filter name…"
                                )
                            }
                            if (showSearch) {
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        if (indexed.isEmpty()) "No matching filters" else "${indexed.size} result${if (indexed.size == 1) "" else "s"} · up to $pageSize per page",
                                        color = VulkanTextSecondary,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (indexed.isNotEmpty() && pageCount > 1) {
                                        Text("Page ${page + 1} / $pageCount", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                            Box(Modifier.weight(1f).fillMaxWidth().heightIn(min = 56.dp)) {
                                AnimatedContent(
                                    targetState = page to query,
                                    transitionSpec = {
                                        val direction = if (targetState.first >= initialState.first) 1 else -1
                                        (fadeIn(tween(180)) + slideInHorizontally(tween(190)) { direction * (it / 10) }) togetherWith
                                            (fadeOut(tween(120)) + slideOutHorizontally(tween(140)) { -direction * (it / 10) })
                                    },
                                    label = "filterPageTransition"
                                ) { (targetPage, targetQuery) ->
                                    val targetIndexed = labels.mapIndexed { index, label -> index to label }
                                        .filter { (_, label) -> targetQuery.isBlank() || label.contains(targetQuery, ignoreCase = true) }
                                    val targetPageItems = targetIndexed.drop(targetPage * pageSize).take(pageSize)
                                    val selectedOffset = if (targetQuery.isBlank()) targetPageItems.indexOfFirst { it.first == selectedIndex } else -1
                                    val targetListState = rememberLazyListState(initialFirstVisibleItemIndex = selectedOffset.coerceAtLeast(0))
                                    val boundaryScrollConnection = rememberFilterScrollBoundaryConnection()
                                    Box(Modifier.fillMaxSize().nestedScroll(boundaryScrollConnection)) {
                                        LazyColumn(
                                            state = targetListState,
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.spacedBy(5.dp),
                                            contentPadding = PaddingValues(vertical = 2.dp)
                                        ) {
                                            items(targetPageItems, key = { it.first }) { (index, label) ->
                                                val selected = index == selectedIndex
                                                val rowShape = RoundedCornerShape(18.dp)
                                                Surface(
                                                    shape = rowShape,
                                                    color = if (selected) VulkanAccentContainer else ComposeColor.Transparent,
                                                    contentColor = if (selected) VulkanTextPrimary else VulkanTextSecondary,
                                                    modifier = Modifier.fillMaxWidth().clip(rowShape).clickable(role = Role.RadioButton) { onSelected(index) }
                                                ) {
                                                    Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                        Surface(shape = RoundedCornerShape(999.dp), color = if (selected) VulkanAccentSoft else VulkanOutlineVariant, modifier = Modifier.size(10.dp)) {}
                                                        Text(
                                                            trademarkVulkanDisplayText(label),
                                                            modifier = Modifier.weight(1f),
                                                            textAlign = TextAlign.Start,
                                                            softWrap = true,
                                                            color = if (selected) VulkanTextPrimary else VulkanTextSecondary,
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                    }
                                                }
                                            }
                                            if (targetPageItems.isEmpty()) {
                                                item {
                                                    Text(
                                                        "No filter matches this search.",
                                                        color = VulkanTextMuted,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 16.dp),
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                            }
                                        }
                                        ExpressiveScrollHints(targetListState, Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 4.dp))
                                    }
                                }
                            }
                            if (indexed.isNotEmpty() && pageCount > 1) {
                                HorizontalDivider(color = VulkanOutlineVariant)
                                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        IconButton(
                                            onClick = { if (page > 0) page -= 1 },
                                            enabled = page > 0,
                                            colors = IconButtonDefaults.iconButtonColors(
                                                containerColor = VulkanAccentContainer,
                                                contentColor = VulkanTextPrimary,
                                                disabledContainerColor = VulkanSurfaceLow,
                                                disabledContentColor = VulkanTextMuted
                                            )
                                        ) {
                                            Icon(painterResource(R.drawable.ic_chevron_left), contentDescription = "Previous filter page")
                                        }
                                        OutlinedTextField(
                                            value = pageField,
                                            onValueChange = { value ->
                                                val candidate = value.text
                                                when {
                                                    candidate.isEmpty() -> pageField = value
                                                    candidate.length <= pageCount.toString().length && candidate.all { it.isDigit() } && !(candidate.length > 1 && candidate.startsWith('0')) -> {
                                                        val requested = candidate.toIntOrNull()
                                                        if (requested != null && requested in 1..pageCount) {
                                                            pageField = value
                                                            page = requested - 1
                                                        }
                                                    }
                                                }
                                            },
                                            modifier = Modifier.width(72.dp).onFocusChanged { focus ->
                                                if (focus.isFocused) {
                                                    pageField = pageField.copy(selection = TextRange(0, pageField.text.length))
                                                } else if (pageField.text.isBlank()) {
                                                    pageField = TextFieldValue((page + 1).toString())
                                                }
                                            },
                                            singleLine = true,
                                            label = { Text("Page") },
                                            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = VulkanAccentSoft,
                                                unfocusedBorderColor = VulkanOutline,
                                                focusedTextColor = VulkanTextPrimary,
                                                unfocusedTextColor = VulkanTextPrimary,
                                                cursorColor = VulkanAccentSoft
                                            )
                                        )
                                        Text("/ $pageCount", color = VulkanTextSecondary, style = MaterialTheme.typography.bodyMedium)
                                        IconButton(
                                            onClick = { if (page + 1 < pageCount) page += 1 },
                                            enabled = page + 1 < pageCount,
                                            colors = IconButtonDefaults.iconButtonColors(
                                                containerColor = VulkanAccentContainer,
                                                contentColor = VulkanTextPrimary,
                                                disabledContainerColor = VulkanSurfaceLow,
                                                disabledContentColor = VulkanTextMuted
                                            )
                                        ) {
                                            Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = "Next filter page")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
private fun ExpressiveFilterBar(labels: List<String>, selectedIndex: Int, arrowTint: ComposeColor = VulkanTextPrimary, onSelected: (Int) -> Unit) {
    if (labels.firstOrNull()?.equals("All", true) == true && labels.size > 1) {
        val allEnabled = selectedIndex == 0
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ExpressiveSwitch(checked = allEnabled, onCheckedChange = { enabled -> onSelected(if (enabled) 0 else 1) })
                Column(Modifier.weight(1f)) {
                    Text("All", fontWeight = FontWeight.SemiBold, color = VulkanTextPrimary)
                    Text(if (allEnabled) "All entries are shown; specific filters are locked." else "Specific filter selection is enabled.", color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall)
                }
            }
            ExpressiveSingleFilterSelector(
                labels = labels.drop(1),
                selectedIndex = if (selectedIndex <= 0) null else selectedIndex - 1,
                enabled = !allEnabled,
                indicatorTint = arrowTint,
                onSelected = { onSelected(it + 1) }
            )
        }
    } else {
        ExpressiveSingleFilterSelector(labels, selectedIndex, true, arrowTint, onSelected)
    }
}

@Composable
private fun ExpressiveMultiFilterBar(labels: List<String>, selectedLabels: Set<String>, onToggle: (String) -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var contentMounted by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    val showSearch = labels.size >= 5
    val visibleLabels = remember(labels, query) {
        labels.filter { query.isBlank() || it.contains(query, ignoreCase = true) }
    }
    val summary = when (selectedLabels.size) {
        0 -> "No filters selected"
        1 -> selectedLabels.firstOrNull() ?: "1 filter selected"
        else -> "${selectedLabels.size} filters selected"
    }
    val arrowRotation by animateFloatAsState(if (expanded) 180f else 0f, animationSpec = tween(180), label = "multiFilterArrow")
    LaunchedEffect(expanded) {
        if (expanded) {
            contentMounted = true
            contentVisible = false
            delay(20)
            contentVisible = true
        } else {
            contentVisible = false
            delay(190)
            contentMounted = false
        }
    }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        val selectorShape = MaterialTheme.shapes.medium
        Surface(
            shape = selectorShape,
            color = VulkanSurfaceTonal,
            contentColor = VulkanTextPrimary,
            border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutline),
            modifier = Modifier.fillMaxWidth().heightIn(min = 58.dp)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("Filters", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                    Text(
                        trademarkVulkanDisplayText(summary),
                        color = VulkanTextPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                val arrowShape = RoundedCornerShape(15.dp)
                Surface(
                    shape = arrowShape,
                    color = if (expanded) VulkanAccent else VulkanAccentContainer,
                    contentColor = VulkanTextPrimary,
                    modifier = Modifier.size(44.dp).clip(arrowShape).clickable(role = Role.Button) { expanded = !expanded }
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(R.drawable.ic_expand_more),
                            contentDescription = if (expanded) "Close filter menu" else "Open filter menu",
                            tint = VulkanTextPrimary,
                            modifier = Modifier.size(24.dp).graphicsLayer(rotationZ = arrowRotation)
                        )
                    }
                }
            }
        }
        if (contentMounted) {
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(220)) + scaleIn(tween(240), initialScale = 0.97f, transformOrigin = TransformOrigin(0.5f, 0f)) + expandVertically(tween(220), expandFrom = Alignment.Top),
                exit = fadeOut(tween(150)) + scaleOut(tween(170), targetScale = 0.985f, transformOrigin = TransformOrigin(0.5f, 0f)) + shrinkVertically(tween(170), shrinkTowards = Alignment.Top)
            ) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = VulkanSurfaceRaised,
                    contentColor = VulkanTextPrimary,
                    border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutline),
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth().heightIn(max = 620.dp)
                ) {
                    Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (showSearch) {
                            ExpressiveSearchField(
                                value = query,
                                onValueChange = { query = it.take(120) },
                                modifier = Modifier.fillMaxWidth(),
                                labelText = "Search filters",
                                placeholderText = "Type a filter name…"
                            )
                            Text(
                                "${visibleLabels.size} result${if (visibleLabels.size == 1) "" else "s"}",
                                color = VulkanTextSecondary,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        val listState = rememberLazyListState()
                        val boundaryScrollConnection = rememberFilterScrollBoundaryConnection()
                        Box(Modifier.fillMaxWidth().heightIn(min = 56.dp, max = 420.dp).nestedScroll(boundaryScrollConnection)) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(5.dp),
                                contentPadding = PaddingValues(vertical = 2.dp)
                            ) {
                                items(visibleLabels, key = { it }) { label ->
                                    val selected = label in selectedLabels
                                    val rowShape = RoundedCornerShape(18.dp)
                                    Surface(
                                        shape = rowShape,
                                        color = if (selected) VulkanAccentContainer else ComposeColor.Transparent,
                                        contentColor = if (selected) VulkanTextPrimary else VulkanTextSecondary,
                                        modifier = Modifier.fillMaxWidth().clip(rowShape).clickable(role = Role.Checkbox) { onToggle(label) }
                                    ) {
                                        Row(
                                            Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Checkbox(
                                                checked = selected,
                                                onCheckedChange = { onToggle(label) },
                                                colors = CheckboxDefaults.colors(
                                                    checkedColor = VulkanAccent,
                                                    checkmarkColor = VulkanTextPrimary,
                                                    uncheckedColor = VulkanOutline
                                                )
                                            )
                                            Text(
                                                trademarkVulkanDisplayText(label),
                                                modifier = Modifier.weight(1f),
                                                color = if (selected) VulkanTextPrimary else VulkanTextSecondary,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                                if (visibleLabels.isEmpty()) {
                                    item {
                                        Text(
                                            "No filter matches this search.",
                                            color = VulkanTextMuted,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 16.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                            ExpressiveScrollHints(listState, Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpressiveToggleRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ExpressiveSwitch(checked = checked, onCheckedChange = onCheckedChange)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(trademarkVulkanDisplayText(title), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(trademarkVulkanDisplayText(subtitle), style = MaterialTheme.typography.labelSmall, color = VulkanTextSecondary)
        }
    }
}

@Composable
private fun ExpressiveMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(shape = MaterialTheme.shapes.medium, color = VulkanSurfaceTonal, modifier = modifier) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(trademarkVulkanDisplayText(value), color = VulkanTextPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text(trademarkVulkanDisplayText(label), color = VulkanTextSecondary, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun ExpressiveMetricGrid(metrics: List<Pair<String, String>>, modifier: Modifier = Modifier) {
    if (metrics.isEmpty()) return
    val expandedText = preferExpandedTextLayout()
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val columns = when {
            expandedText || maxWidth < 360.dp -> 1
            maxWidth < 760.dp -> 2
            else -> 3
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            metrics.chunked(columns).forEach { rowMetrics ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowMetrics.forEach { (label, value) -> ExpressiveMetric(label, value, Modifier.weight(1f)) }
                    repeat(columns - rowMetrics.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
private fun ExpressiveStatus(state: String) {
    val positive = when (state.uppercase()) {
        "PASS", "SATISFIED", "SUPPORTED", "AVAILABLE", "COMPATIBLE EVIDENCE", "NO REJECTION" -> true
        "FAIL", "NOT SATISFIED", "UNSUPPORTED", "SAFETY REJECTED" -> false
        else -> null
    }
    CapabilityStatusBadge(state, positive)
}

private fun copyEvidenceText(context: Context, label: String, text: String) {
    (context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager)?.setPrimaryClip(android.content.ClipData.newPlainText(label, text))
}

private fun shareEvidenceText(context: Context, text: String) {
    runCatching { context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text), "Share VulkanScope evidence")) }
}

@Composable
private fun EvidenceInspectorDialog(key: String, value: String, onDismiss: () -> Unit, onWatch: ((String) -> Unit)? = null) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val environment = LocalEvidenceActionEnvironment.current
    val provenance = remember(key, value) { evidenceProvenance(key, value) }
    val referenceToken = remember(key, value) { evidenceTokenForReference(key, value) }
    val resolvedWatch = onWatch ?: environment?.addWatch
    ExpressiveDetailDialog("Evidence provenance", onDismiss) {
        ExpressiveEvidenceRow("Evidence key", key)
        ExpressiveEvidenceRow("Value", value.ifBlank { "Unavailable" })
        ExpressiveEvidenceRow("Evidence class", provenance.evidenceClass)
        ExpressiveEvidenceRow("Source", provenance.source)
        ExpressiveEvidenceRow("Query/API path", provenance.queryPath)
        ExpressiveEvidenceRow("Query group", provenance.queryGroup)
        ExpressiveEvidenceRow("Registry relationship", provenance.registryRelation)
        Text(trademarkVulkanDisplayText(provenance.interpretation), color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
        TransientActionButton("Copy name + value", "Copy the exact displayed evidence pair", R.drawable.ic_copy) { runCatching { copyEvidenceText(context, "VulkanScope evidence", "$key = $value") }.isSuccess }
        ExpressiveActionButton("Share evidence", "Android Sharesheet · explicit user action", R.drawable.ic_share, trailingIcon = R.drawable.ic_open_external) { shareEvidenceText(context, "$key = $value") }
        TransientActionButton("Add to watched evidence", "Local bounded watch list", R.drawable.ic_watch_add, enabled = referenceToken.isNotBlank() && resolvedWatch != null, idleTrailingIcon = R.drawable.ic_add) { runCatching { resolvedWatch?.invoke(referenceToken) }.isSuccess }
        ExpressiveActionButton("Open in Encyclopedia", "Open the closest Vulkan symbol/reference token", R.drawable.ic_book, enabled = referenceToken.isNotBlank() && environment != null) { environment?.openEncyclopedia?.invoke(referenceToken) }
    }
}

@Composable
private fun ExpressiveEvidenceRow(key: String, value: String) {
    val expandedTextLayout = preferExpandedTextLayout()
    val detailPresentation = LocalDetailKeyValuePresentation.current
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val stacked = if (detailPresentation) {
            expandedTextLayout || maxWidth < 420.dp || key.length > 22 || value.length > 30 || value.contains("\n")
        } else {
            expandedTextLayout || maxWidth < 360.dp || key.length > 26 || value.length > 34 || value.contains("\n")
        }
        val shape: Shape = if (detailPresentation) MaterialTheme.shapes.medium else RoundedCornerShape(16.dp)
        Surface(
            color = VulkanSurfaceTonal,
            shape = shape,
            border = if (detailPresentation) androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant) else null,
            modifier = Modifier.fillMaxWidth().then(tvBrowseModifier(shape)).semantics(mergeDescendants = true) { }
        ) {
            if (stacked) {
                Column(
                    Modifier.fillMaxWidth().padding(horizontal = if (detailPresentation) 13.dp else 14.dp, vertical = if (detailPresentation) 10.dp else 11.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(trademarkVulkanDisplayText(key), color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall, fontWeight = if (detailPresentation) FontWeight.SemiBold else FontWeight.Normal)
                    Text(trademarkVulkanDisplayText(value.ifBlank { "Unavailable" }), color = ComposeColor(0xFFE7DFE1), style = if (detailPresentation) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall)
                }
            } else {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = if (detailPresentation) 13.dp else 14.dp, vertical = if (detailPresentation) 10.dp else 11.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(trademarkVulkanDisplayText(key), color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(0.88f))
                    Text(trademarkVulkanDisplayText(value.ifBlank { "Unavailable" }), color = ComposeColor(0xFFE7DFE1), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1.12f))
                }
            }
        }
    }
}

@Composable
private fun CapabilityKeyValue(key: String, value: String) {
    var showEvidenceActions by remember(key, value) { mutableStateOf(false) }
    var pressed by remember(key, value) { mutableStateOf(false) }
    val pressScale by animateFloatAsState(if (pressed) 0.985f else 1f, tween(110), label = "evidenceHoldScale")
    val pressHighlightAlpha by animateFloatAsState(if (pressed) 0.58f else 0f, tween(110), label = "evidenceHoldHighlight")
    Box(
        Modifier.fillMaxWidth()
            .graphicsLayer(scaleX = pressScale, scaleY = pressScale)
            .background(VulkanAccentContainer.copy(alpha = pressHighlightAlpha), RoundedCornerShape(14.dp))
            .pointerInput(key, value) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        try {
                            tryAwaitRelease()
                        } finally {
                            pressed = false
                        }
                    },
                    onLongPress = { showEvidenceActions = true }
                )
            }
            .semantics {
                customActions = listOf(CustomAccessibilityAction("Evidence actions") { showEvidenceActions = true; true })
            }
    ) {
        ExpressiveEvidenceRow(key, value)
    }
    if (showEvidenceActions) EvidenceInspectorDialog(key, value, onDismiss = { showEvidenceActions = false })
}

private fun evidenceStateAccent(state: String): ComposeColor = when (state.trim().uppercase().replace('_', ' ')) {
    "SUPPORTED", "PASS" -> ComposeColor(0xFF73C991)
    "AVAILABLE" -> ComposeColor(0xFF7CC4FF)
    "UNSUPPORTED", "FAIL" -> ComposeColor(0xFFFF8A8A)
    "UNAVAILABLE" -> ComposeColor(0xFFFFC857)
    "INCOMPLETE" -> ComposeColor(0xFFD3A4FF)
    "NOT APPLICABLE" -> ComposeColor(0xFFC4C4C4)
    "UNKNOWN", "UNRESOLVED" -> ComposeColor(0xFFA8A8A8)
    else -> VulkanTextSecondary
}

@Composable
private fun CapabilityStatusBadge(label: String, positive: Boolean? = null) {
    val normalized = label.trim().uppercase().replace('_', ' ')
    val available = positive == null && (normalized == "AVAILABLE" || normalized.startsWith("AVAILABLE "))
    val unknown = positive == null && (normalized == "UNKNOWN" || normalized.startsWith("UNKNOWN ") || normalized == "UNRESOLVED")
    val unavailable = positive == null && (normalized == "UNAVAILABLE" || normalized.startsWith("UNAVAILABLE "))
    val incomplete = positive == null && normalized == "INCOMPLETE"
    val notApplicable = positive == null && (normalized == "NOT APPLICABLE" || normalized.startsWith("NOT APPLICABLE "))
    val background = when {
        positive == true -> ComposeColor(0xFF173421)
        positive == false -> ComposeColor(0xFF3A1D20)
        available -> ComposeColor(0xFF172B3A)
        unavailable -> ComposeColor(0xFF332A16)
        incomplete -> ComposeColor(0xFF30243A)
        notApplicable -> ComposeColor(0xFF272727)
        unknown -> ComposeColor(0xFF252525)
        else -> VulkanSurfaceTonal
    }
    val foreground = when {
        positive == true -> ComposeColor(0xFF73C991)
        positive == false -> ComposeColor(0xFFFF8A8A)
        available -> ComposeColor(0xFF7CC4FF)
        unavailable -> ComposeColor(0xFFFFC857)
        incomplete -> ComposeColor(0xFFD3A4FF)
        notApplicable -> ComposeColor(0xFFC4C4C4)
        unknown -> ComposeColor(0xFFA8A8A8)
        else -> VulkanTextSecondary
    }
    Surface(shape = RoundedCornerShape(999.dp), color = background) {
        Text(label, color = foreground, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp))
    }
}

@Composable
private fun ActionButtonIconArtwork(title: String, icon: Int, tint: ComposeColor) {
    when {
        title.equals("Run guided System ↔ Turnip A/B", true) -> {
            CompositeActionVectorIcon(R.drawable.ic_cpu, R.drawable.ic_compare, tint)
            return
        }
        title.equals("Switch to System and collect", true) -> {
            CompositeAndroidActionIcon(R.drawable.ic_compare, tint)
            return
        }
        title.equals("Switch to Turnip and collect", true) -> {
            CompositeMesaActionIcon(R.drawable.ic_compare, tint)
            return
        }
    }
    val badge = when {
        title.equals("Export TXT", true) -> "TXT"
        title.equals("Export HTML", true) -> "HTML"
        else -> null
    }
    if (badge == null) {
        Icon(painter = painterResource(icon), contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        return
    }
    Box(Modifier.size(24.dp)) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.align(Alignment.TopStart).size(19.dp)
        )
        Surface(
            shape = RoundedCornerShape(3.dp),
            color = VulkanSurface,
            modifier = Modifier.align(Alignment.BottomEnd).border(0.7.dp, tint, RoundedCornerShape(3.dp))
        ) {
            Text(
                badge,
                color = tint,
                fontSize = if (badge == "HTML") 3.7.sp else 4.4.sp,
                lineHeight = if (badge == "HTML") 4.0.sp else 4.7.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = if (badge == "HTML") 0.8.dp else 1.0.dp, vertical = 0.7.dp)
            )
        }
    }
}

@Composable
private fun CompositeActionVectorIcon(primary: Int, overlay: Int, tint: ComposeColor) {
    Box(Modifier.size(24.dp)) {
        Icon(painterResource(primary), contentDescription = null, tint = tint, modifier = Modifier.align(Alignment.TopStart).size(19.dp))
        Surface(shape = RoundedCornerShape(4.dp), color = VulkanSurface, modifier = Modifier.align(Alignment.BottomEnd)) {
            Icon(painterResource(overlay), contentDescription = null, tint = tint, modifier = Modifier.padding(1.2.dp).size(9.dp))
        }
    }
}

@Composable
private fun CompositeAndroidActionIcon(overlay: Int, tint: ComposeColor) {
    Box(Modifier.size(24.dp)) {
        Image(painterResource(R.drawable.ic_android), contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier.align(Alignment.TopStart).width(20.dp).height(14.dp))
        Surface(shape = RoundedCornerShape(4.dp), color = VulkanSurface, modifier = Modifier.align(Alignment.BottomEnd)) {
            Icon(painterResource(overlay), contentDescription = null, tint = tint, modifier = Modifier.padding(1.2.dp).size(9.dp))
        }
    }
}

@Composable
private fun CompositeMesaActionIcon(overlay: Int, tint: ComposeColor) {
    Box(Modifier.size(24.dp)) {
        Image(painterResource(R.drawable.mesa3d_logo), contentDescription = null, contentScale = ContentScale.Fit, colorFilter = ColorFilter.tint(VulkanAccentSoft), modifier = Modifier.align(Alignment.TopStart).size(20.dp))
        Surface(shape = RoundedCornerShape(4.dp), color = VulkanSurface, modifier = Modifier.align(Alignment.BottomEnd)) {
            Icon(painterResource(overlay), contentDescription = null, tint = tint, modifier = Modifier.padding(1.2.dp).size(9.dp))
        }
    }
}

@Composable
private fun ExpressiveActionButton(
    title: String,
    subtitle: String,
    icon: Int,
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true,
    compact: Boolean = false,
    trailingIcon: Int = R.drawable.ic_chevron_right,
    trailingTint: ComposeColor? = null,
    onClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    val shape = if (compact) MaterialTheme.shapes.large else MaterialTheme.shapes.largeIncreased
    val container = if (!enabled) ComposeColor(0xFF111111) else if (focused) ComposeColor(0xFF2A1517) else ComposeColor(0xFF1A1718)
    val iconContainer = if (enabled) ComposeColor(0xFF351719) else ComposeColor(0xFF181818)
    val accent = if (enabled) ComposeColor(0xFFE2676A) else ComposeColor(0xFF606064)
    val titleColor = if (enabled) ComposeColor(0xFFF7F2F3) else ComposeColor(0xFF6C696A)
    val detailColor = if (enabled) ComposeColor(0xFFB6ACAE) else ComposeColor(0xFF5A5758)
    Surface(
        color = container,
        shape = shape,
        modifier = modifier.border(if (focused && enabled) 2.dp else 0.dp, if (focused && enabled) ComposeColor(0xFFE2676A) else ComposeColor.Transparent, shape)
    ) {
        if (compact) {
            Column(
                Modifier.fillMaxWidth().padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(17.dp), color = iconContainer) {
                        Box(Modifier.padding(9.dp)) { ActionButtonIconArtwork(title, icon, accent) }
                    }
                    IconButton(
                        onClick = onClick,
                        enabled = enabled,
                        modifier = Modifier.size(48.dp).onFocusChanged { focused = it.isFocused },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = if (enabled) ComposeColor(0xFF291719) else ComposeColor(0xFF171717), contentColor = accent, disabledContentColor = accent)
                    ) {
                        AnimatedContent(targetState = trailingIcon, label = "actionTrailingIconCompact") { currentIcon ->
                            Icon(painter = painterResource(currentIcon), contentDescription = trademarkVulkanDisplayText(title), tint = trailingTint ?: accent, modifier = Modifier.size(18.dp))
                        }
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(trademarkVulkanDisplayText(title), color = titleColor, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(trademarkVulkanDisplayText(subtitle), color = detailColor, style = MaterialTheme.typography.labelSmall)
                }
            }
        } else {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 17.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(shape = RoundedCornerShape(18.dp), color = iconContainer) {
                    Box(Modifier.padding(10.dp)) { ActionButtonIconArtwork(title, icon, accent) }
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(trademarkVulkanDisplayText(title), color = titleColor, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(trademarkVulkanDisplayText(subtitle), color = detailColor, style = MaterialTheme.typography.labelSmall)
                }
                IconButton(
                    onClick = onClick,
                    enabled = enabled,
                    modifier = Modifier.size(48.dp).onFocusChanged { focused = it.isFocused },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = if (enabled) ComposeColor(0xFF291719) else ComposeColor(0xFF171717), contentColor = accent, disabledContentColor = accent)
                ) {
                    AnimatedContent(targetState = trailingIcon, label = "actionTrailingIcon") { currentIcon ->
                        Icon(painter = painterResource(currentIcon), contentDescription = trademarkVulkanDisplayText(title), tint = trailingTint ?: accent, modifier = Modifier.size(19.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpressiveExternalLinkRow(title: String, subtitle: String, icon: Int, enabled: Boolean = true, onOpen: () -> Unit) {
    Surface(color = if (enabled) ComposeColor(0xFF1A1718) else ComposeColor(0xFF111111), shape = MaterialTheme.shapes.largeIncreased, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 17.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Surface(shape = RoundedCornerShape(18.dp), color = if (enabled) ComposeColor(0xFF351719) else ComposeColor(0xFF181818)) {
                Icon(painterResource(icon), contentDescription = null, tint = if (enabled) VulkanAccentSoft else VulkanTextMuted, modifier = Modifier.padding(11.dp).size(22.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(trademarkVulkanDisplayText(title), color = if (enabled) VulkanTextPrimary else VulkanTextMuted, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyLarge)
                Text(trademarkVulkanDisplayText(subtitle), color = if (enabled) VulkanTextSecondary else VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
            }
            IconButton(onClick = onOpen, enabled = enabled, colors = IconButtonDefaults.iconButtonColors(containerColor = if (enabled) VulkanAccentContainer else VulkanSurfaceLow, contentColor = VulkanAccentSoft, disabledContentColor = VulkanTextMuted), modifier = Modifier.size(48.dp)) {
                Icon(painterResource(R.drawable.ic_open_external), contentDescription = "Open external link", modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun ExpressiveIdentityBlock(title: String, subtitle: String, icon: Int) {
    Surface(color = VulkanSurfaceRaised, shape = MaterialTheme.shapes.largeIncreased, modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().padding(17.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(shape = RoundedCornerShape(20.dp), color = ComposeColor(0xFF351719)) {
                Icon(painter = painterResource(icon), contentDescription = null, tint = ComposeColor(0xFFE2676A), modifier = Modifier.padding(12.dp).size(24.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(trademarkVulkanDisplayText(title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = ComposeColor(0xFFF7F2F3))
                Text(trademarkVulkanDisplayText(subtitle), style = MaterialTheme.typography.bodySmall, color = ComposeColor(0xFFB6ACAE))
            }
        }
    }
}

@Composable
private fun ExpressiveVersionBlock(application: String, version: String, versionCode: String, packageName: String, abi: String) {
    val expandedTextLayout = preferExpandedTextLayout()
    Surface(color = VulkanSurfaceRaised, shape = MaterialTheme.shapes.extraLarge, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                Surface(shape = RoundedCornerShape(19.dp), color = ComposeColor(0xFF351719)) {
                    Image(painter = painterResource(R.drawable.vulkanscope_logo_foreground), contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier.size(46.dp))
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text(application, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text("Version $version", color = ComposeColor(0xFFE98A8C), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                }
                Surface(shape = RoundedCornerShape(999.dp), color = ComposeColor(0xFF272224)) {
                    Text("#$versionCode", color = ComposeColor(0xFFC7BEC0), style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp))
                }
            }
            if (expandedTextLayout) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExpressiveInfoPill("Installed ABI", abi, Modifier.fillMaxWidth())
                    ExpressiveInfoPill("Package", packageName, Modifier.fillMaxWidth())
                }
            } else {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExpressiveInfoPill("Installed ABI", abi, Modifier.weight(1f))
                    ExpressiveInfoPill("Package", packageName, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ExpressiveInfoPill(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(color = VulkanSurfaceTonal, shape = MaterialTheme.shapes.medium, modifier = modifier) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(trademarkVulkanDisplayText(label), color = ComposeColor(0xFF968D8F), style = MaterialTheme.typography.labelSmall)
            Text(trademarkVulkanDisplayText(value), color = ComposeColor(0xFFE7DFE1), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium, maxLines = 3, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun MetricCard(title: String, value: String, modifier: Modifier) {
    val shape = MaterialTheme.shapes.large
    Surface(color = ComposeColor(0xFF181516), shape = shape, modifier = modifier.then(tvBrowseModifier(shape))) {
        Column(Modifier.padding(17.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Surface(shape = RoundedCornerShape(999.dp), color = ComposeColor(0xFF2A2022)) {
                Text(trademarkVulkanDisplayText(title), color = ComposeColor(0xFFE98A8C), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp))
            }
            Text(trademarkVulkanDisplayText(value), color = ComposeColor(0xFFF7F2F3), fontSize = 20.sp, fontWeight = FontWeight.SemiBold, maxLines = 3, overflow = TextOverflow.Ellipsis)
        }
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
