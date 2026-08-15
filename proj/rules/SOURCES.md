# Authoritative Technical Sources

- Khronos Vulkan Documentation: https://docs.vulkan.org/spec/latest/index.html
- Khronos Vulkan Specification: https://docs.vulkan.org/spec/latest/
- Khronos Vulkan API Registry: https://registry.khronos.org/vulkan/
- Khronos Vulkan-Docs repository: https://github.com/KhronosGroup/Vulkan-Docs
- Android Display API: https://developer.android.com/reference/android/view/Display
- Android HDR capabilities: https://developer.android.com/reference/android/view/Display.HdrCapabilities
- Android SurfaceView: https://developer.android.com/reference/android/view/SurfaceView
- Android SurfaceHolder: https://developer.android.com/reference/android/view/SurfaceHolder
- Android wide color gamut: https://developer.android.com/training/wide-color-gamut
- Android Material 3 in Compose: https://developer.android.com/develop/ui/compose/designsystems/material3
- AndroidX Compose Material 3 releases: https://developer.android.com/jetpack/androidx/releases/compose-material3

- VulkanCapsViewer 4.12 release notes: https://github.com/SaschaWillems/VulkanCapsViewer/releases/tag/4.12
- VulkanCapsViewer source: https://github.com/SaschaWillems/VulkanCapsViewer
- Vulkan Structure Type reference: https://docs.vulkan.org/refpages/latest/refpages/source/VkStructureType.html
- VK_KHR_device_fault: https://docs.vulkan.org/refpages/latest/refpages/source/VK_KHR_device_fault.html
- VK_KHR_shader_abort: https://docs.vulkan.org/refpages/latest/refpages/source/VK_KHR_shader_abort.html
- VK_KHR_shader_constant_data: https://docs.vulkan.org/refpages/latest/refpages/source/VK_KHR_shader_constant_data.html
- VK_EXT_shader_split_barrier: https://docs.vulkan.org/refpages/latest/refpages/source/VK_EXT_shader_split_barrier.html
- VK_QCOM_image_processing3: https://docs.vulkan.org/refpages/latest/refpages/source/VK_QCOM_image_processing3.html
- VK_QCOM_shader_multiple_wait_queues: https://docs.vulkan.org/refpages/latest/refpages/source/VK_QCOM_shader_multiple_wait_queues.html

The current upstream Vulkan documentation checked on 2026-08-13 reports Vulkan 1.4.357. The checked-in query catalog is a validated Vulkan 1.4.357 semantic baseline; upstream sources must be rechecked before release whenever Vulkan or Android APIs change.

- VulkanScope 0.10.0 registry-driven query catalog and verification tools.

## 0.13.0 capability sources
- Vulkan 1.4.357 canonical specification / all registered extensions: Khronos Vulkan Registry.
- Vulkan Video queue-family capabilities: `VK_KHR_video_queue`, `VkQueueFamilyVideoPropertiesKHR`, and `VkVideoCodecOperationFlagBitsKHR` from Khronos Vulkan Documentation.
- Vulkan Profiles / Roadmap 2022, 2024 and 2026: Khronos Vulkan Profiles and Roadmap profile definitions.

## 0.14.0 capability sources

- Khronos Vulkan 1.4.357 specification and API registry.
- `VK_KHR_video_queue` capability and video-format query definitions.
- `VK_KHR_video_decode_h264`, `VK_KHR_video_decode_h265`, `VK_KHR_video_decode_av1`, `VK_KHR_video_decode_vp9`.
- `VK_KHR_video_encode_h264`, `VK_KHR_video_encode_h265`, `VK_KHR_video_encode_av1`.

The runtime never downloads or parses these sources; the checked-in query catalog remains a build-time artifact.

## 0.15.1 build dependency pin
- libadrenotools master history was checked on 2026-08-13; the Android build is pinned to commit `8fae8ce` rather than a mutable branch.

- KhronosGroup/Vulkan-Headers commit e3b1eec08173d6b825cd3ac88c885a63b621504a (Vulkan-Headers 1.4.357), fetched at build time and verified by VK_HEADER_VERSION.

- Build-time canonical header source: KhronosGroup/Vulkan-Headers commit e3b1eec08173d6b825cd3ac88c885a63b621504a; `VK_HEADER_VERSION` is verified as 357 before native compilation.
