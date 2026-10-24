@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.efishell.vulkanscope

import android.graphics.Color
import android.view.Surface
import android.content.Context
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.type
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext





@Composable
internal fun OverviewPage(report: VulkanReport, device: DeviceReport?, display: DisplayReport, driverMode: DriverMode, navigate: (Page) -> Unit) {
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



internal const val ENCYCLOPEDIA_VISIBLE_RESULT_LIMIT = 24

internal data class EncyclopediaReferenceEntry(
    val title: String,
    val category: String,
    val definition: String,
    val detail: String = ""
)

internal data class VkResultReferenceEntry(
    val name: String,
    val value: Int,
    val meaning: String,
    val aliases: List<String> = emptyList()
)

internal val VULKAN_VK_RESULT_REFERENCE = listOf(
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

internal val VULKAN_ENCYCLOPEDIA_CORE = listOf(
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

internal val VULKAN_COMMON_COMMAND_MEANINGS = mapOf(
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

internal fun humanizeVulkanSymbolTail(name: String): String = name
    .removePrefix("vkCmd")
    .removePrefix("vkQueue")
    .removePrefix("vk")
    .replace(Regex("([a-z0-9])([A-Z])"), "$1 $2")
    .trim()
    .ifBlank { name }

internal fun commandReferenceDefinition(ref: VulkanRegistrySymbolReference): EncyclopediaReferenceEntry {
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

internal fun tokenReferenceDefinition(ref: VulkanRegistrySymbolReference): EncyclopediaReferenceEntry {
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

internal fun typeReferenceDefinition(ref: VulkanRegistrySymbolReference): EncyclopediaReferenceEntry {
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

internal fun extensionReferenceDefinition(ref: VulkanExtensionReference): EncyclopediaReferenceEntry {
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

internal fun encyclopediaMatches(entry: EncyclopediaReferenceEntry, query: String): Boolean =
    query.isBlank() || listOf(entry.title, entry.category, entry.definition, entry.detail).any { it.contains(query, ignoreCase = true) }

internal fun encyclopediaVkResultEntries(query: String): List<EncyclopediaReferenceEntry> = VULKAN_VK_RESULT_REFERENCE
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

internal fun encyclopediaExtensionEntries(query: String, limit: Int): List<EncyclopediaReferenceEntry> {
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

internal fun encyclopediaRegistryEntries(kind: VulkanRegistrySymbolKind, query: String, limit: Int): List<EncyclopediaReferenceEntry> {
    if (query.trim().length < 2) return emptyList()
    return searchVulkanRegistrySymbols(kind, query, limit).map { ref ->
        when (kind) {
            VulkanRegistrySymbolKind.COMMAND -> commandReferenceDefinition(ref)
            VulkanRegistrySymbolKind.TOKEN -> tokenReferenceDefinition(ref)
            VulkanRegistrySymbolKind.TYPE -> typeReferenceDefinition(ref)
        }
    }
}

internal fun encyclopediaSearch(category: String, rawQuery: String): List<EncyclopediaReferenceEntry> {
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

internal fun encyclopediaEntryIcon(category: String): Int = when {
    category.contains("VkResult", true) -> R.drawable.ic_test
    category.contains("command", true) -> R.drawable.ic_code
    category.contains("extension", true) -> R.drawable.ic_extensions
    category.contains("token", true) || category.contains("VK_", true) -> R.drawable.ic_registry
    category.contains("type", true) -> R.drawable.ic_properties
    else -> R.drawable.ic_book
}
