# VulkanScope Engineering Rules

## Non-negotiable
- Source-code comments are forbidden.
- Security, correctness, memory safety, performance and usability are never traded away for convenience.
- No known security vulnerability may be knowingly shipped.
- No known memory leak may be knowingly shipped.
- No unnecessary network permission or network dependency.
- No guessed hardware capabilities.
- Unknown, unsupported and unavailable are distinct states.
- Vulkan loader version, device API version and driver version are distinct values.
- Vulkan surface color-space support is not equivalent to physical display gamut.
- Official Khronos and Android documentation are the primary sources for API behavior.
- Current technical claims must be verified against current upstream sources before release.
- Native ownership must be deterministic and Vulkan handles must be destroyed in valid dependency order.
- UI-thread blocking native work is forbidden.
- Large collections must be lazy and searchable.
- No unnecessary allocations or repeated Vulkan queries during UI recomposition.
- No runtime network access is permitted.
- Device and display data must remain on-device.
- Runtime-enumerated Vulkan extension names must be displayed exactly as returned by the Vulkan implementation.
- Vulkan enum names must not be replaced by marketing names or inferred capabilities.
- A missing API query must be reported as unavailable or unknown, never as unsupported without evidence.

## ABI
- armeabi-v7a required.
- arm64-v8a required.
- x86_64 required.
- x86 intentionally excluded.

## Architecture
- Kotlin and Jetpack Compose for UI.
- Material 3 Expressive visual system.
- C++20 and Vulkan for native collection.
- Native/JNI boundary must remain small.
- Vulkan registry and specification data are authoritative references for naming and semantics.
- Native Vulkan compilation must use canonical Khronos Vulkan-Headers 1.4.357 or newer only when explicitly verified at build time; runtime must never fetch headers or registry data.
- Core Vulkan feature queries and version-promoted feature queries must remain distinguishable.

## Surface and display
- Surface data must come from a real Android Surface and VkSurfaceKHR path.
- Surface formats must be reported as exact format + color-space pairs.
- Color-space names must use canonical Vulkan names such as VK_COLOR_SPACE_DISPLAY_P3_NONLINEAR_EXT and VK_COLOR_SPACE_HDR10_ST2084_EXT.
- Android Display HDR data must remain distinct from Vulkan surface color-space data.
- Wide-color support must not be presented as a measured gamut percentage.
- HDR luminance values are displayed only when Android exposes them.
- Surface lifecycle must be synchronized so a destroyed Surface is never used by native Vulkan code.

## Extensions
- The Extensions screen lists every instance and device extension enumerated at runtime.
- Extension search is case-insensitive.
- Instance and device scope remain visible.
- Extension specVersion is displayed exactly as reported.
- VK_EXT_swapchain_colorspace and all other supported extensions must appear when the implementation enumerates them.
- Extensions that are not exposed by the device must never be labeled supported.

## Features and limits
- Vulkan 1.0 core features are queried through vkGetPhysicalDeviceFeatures.
- Vulkan 1.1, 1.2, 1.3 and 1.4 core feature structures are queried when the device API version exposes them.
- Feature support is not the same as feature enablement.
- Limits are reported from Vulkan properties and are not inferred from GPU model names.

## Formats
- Format properties are queried from Vulkan before being displayed.
- Invalid or invented VkFormat numeric values must never be queried.
- Format names must be canonical Vulkan names.

## Release quality
- Test on all target ABIs.
- Test Vulkan 1.0 through the latest API version exposed by the installed Android Vulkan stack.
- Test missing optional extensions and features.
- Test Surface recreation and lifecycle transitions.
- Test HDR, wide-color, 10-bit and SDR-only displays.
- Test devices with multiple queue families and memory heaps.
- Release builds must enable shrinking and resource optimization.

## Build compatibility
- AGP 9.3.1 is required for the supplied project configuration.
- Gradle 9.7.x is the intended build family.
- AGP 9 built-in Kotlin is used; the deprecated/redundant `org.jetbrains.kotlin.android` plugin is not applied.
- The Compose compiler Gradle plugin remains applied for Compose compilation.
- JDK 17 or newer is required by the Android Gradle Plugin.
- Build-tool version changes must be checked against official Android Gradle Plugin compatibility documentation before being committed.

## Build gate
- Every supplied revision must compile Kotlin and native C++ for armeabi-v7a, arm64-v8a, and x86_64 with warnings treated as errors where configured.
- No unused native helper may remain. Every generated Vulkan metadata field must be consumed by the UI or intentionally exposed through the native report.
- Compose experimental APIs must be explicitly opted into only at the smallest required scope.
- Extension aggregation must preserve exact Vulkan extension names, scope, and specVersion without lossy transformations.
- Registry-driven query coverage is build-time authored from canonical Khronos metadata; the Android CMake build must not require Python or any runtime registry download. Generated native metadata is checked in and verified before release; runtime must never download or parse remote registry data.
- Generated registry metadata is informational unless the corresponding native struct/query path has been explicitly validated; unknown registry structures must remain unavailable rather than being guessed.
