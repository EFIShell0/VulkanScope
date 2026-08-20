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

The current published Khronos Vulkan specification checked on 2026-08-20 is Vulkan 1.4.358 (2026-07-31). VulkanScope independently pins a Vulkan-Headers staging commit with VK_HEADER_VERSION 360 and retains its separately validated producer/query catalog through that staging baseline. The 1.4.360 project baseline must never be described as the current published Khronos specification until Khronos publishes that revision.

- VulkanScope 0.10.0 registry-driven query catalog and verification tools.

## 0.13.0 capability sources
- Current published Vulkan 1.4.358 specification / registered extensions: Khronos Vulkan Registry.
- VulkanScope producer/query staging baseline: pinned Vulkan-Headers commit with VK_HEADER_VERSION 360, independently audited by the project.
- Vulkan Video queue-family capabilities: `VK_KHR_video_queue`, `VkQueueFamilyVideoPropertiesKHR`, and `VkVideoCodecOperationFlagBitsKHR` from Khronos Vulkan Documentation.
- Vulkan Profiles / Roadmap 2022, 2024 and 2026: Khronos Vulkan Profiles and Roadmap profile definitions.

## 0.14.0 capability sources

- Khronos published Vulkan 1.4.358 specification and API registry, plus the project-pinned Vulkan-Headers staging commit used for the 1.4.360 producer/query baseline.
- `VK_KHR_video_queue` capability and video-format query definitions.
- `VK_KHR_video_decode_h264`, `VK_KHR_video_decode_h265`, `VK_KHR_video_decode_av1`, `VK_KHR_video_decode_vp9`.
- `VK_KHR_video_encode_h264`, `VK_KHR_video_encode_h265`, `VK_KHR_video_encode_av1`.

The runtime never downloads or parses these sources; the checked-in query catalog remains a build-time artifact.

## 0.15.1 build dependency pin
- libadrenotools master history was checked on 2026-08-13; the Android build is pinned to commit `8fae8ce` rather than a mutable branch.

- Current build-time canonical header source: KhronosGroup/Vulkan-Headers commit `0b7f383797fa7be53ae28213e001ae60668ee511`; `VK_HEADER_VERSION` is verified as 360 before native compilation. This is VulkanScope's project staging baseline, not a claim that Khronos has published specification revision 1.4.360.
