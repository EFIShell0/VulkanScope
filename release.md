# VulkanScope 0.32.4

VulkanScope 0.32.4 is a focused build-fix release based on 0.32.1. It fixes the Android API 37 Kotlin nullability change for `Display.hdrCapabilities` without changing Vulkan query coverage or UI outside the already-authorized Info and Settings scope. If HDR capabilities are not exposed, VulkanScope records that absence as `Not exposed` instead of forcing a non-null value or inventing luminance data.

All 0.32.1 engineering, export, Database, security, Vulkan and scoped Material 3 Expressive changes are preserved.
