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

The current published Khronos Vulkan specification rechecked for VulkanScope 0.41.4 is Vulkan 1.4.360 (2026-08-14). VulkanScope pins Vulkan-Headers commit 0b7f383797fa7be53ae28213e001ae60668ee511 with VK_HEADER_VERSION 360, matching the project's published/query baseline while runtime loader, device API and driver versions remain separate evidence.

- VulkanScope 0.10.0 registry-driven query catalog and verification tools.

## 0.13.0 capability sources
- Current published Vulkan 1.4.360 specification / registered extensions: Khronos Vulkan Registry.
- VulkanScope published/query header baseline: pinned Vulkan-Headers commit with VK_HEADER_VERSION 360, independently audited by the project; runtime loader/device/driver versions remain separate evidence.
- Vulkan Video queue-family capabilities: `VK_KHR_video_queue`, `VkQueueFamilyVideoPropertiesKHR`, and `VkVideoCodecOperationFlagBitsKHR` from Khronos Vulkan Documentation.
- Vulkan Profiles / Roadmap 2022, 2024 and 2026: Khronos Vulkan Profiles and Roadmap profile definitions.

## 0.14.0 capability sources

- Khronos published Vulkan 1.4.360 specification and API registry, matching the project-pinned Vulkan-Headers/query baseline.
- `VK_KHR_video_queue` capability and video-format query definitions.
- `VK_KHR_video_decode_h264`, `VK_KHR_video_decode_h265`, `VK_KHR_video_decode_av1`, `VK_KHR_video_decode_vp9`.
- `VK_KHR_video_encode_h264`, `VK_KHR_video_encode_h265`, `VK_KHR_video_encode_av1`.

The runtime never downloads or parses these sources; the checked-in query catalog remains a build-time artifact.

## 0.15.1 build dependency pin
- libadrenotools master history was checked on 2026-08-13; the Android build is pinned to commit `8fae8ce` rather than a mutable branch.

- Current build-time canonical header source: KhronosGroup/Vulkan-Headers commit `0b7f383797fa7be53ae28213e001ae60668ee511`; `VK_HEADER_VERSION` is verified as 360 before native compilation. The current published specification is also Vulkan 1.4.360; the pinned header revision and runtime-reported API/loader/driver versions are still reported as distinct provenance.

## 0.34.9 Material 3 Expressive UI sources
- AndroidX Compose Material 3 release notes (current alpha baseline 1.5.0-alpha26): https://developer.android.com/jetpack/androidx/releases/compose-material3
- MaterialExpressiveTheme API: https://developer.android.com/reference/kotlin/androidx/compose/material3/MaterialExpressiveTheme.composable
- MotionScheme API: https://developer.android.com/reference/kotlin/androidx/compose/material3/MotionScheme
- ShortNavigationBar API: https://developer.android.com/reference/kotlin/androidx/compose/material3/ShortNavigationBar.composable
- ShortNavigationBarItem API: https://developer.android.com/reference/kotlin/androidx/compose/material3/ShortNavigationBarItem.composable
- IconButton and IconButtonDefaults expressive shape APIs: https://developer.android.com/reference/kotlin/androidx/compose/material3/IconButton.composable
- FilterChip and FilterChipDefaults shape APIs: https://developer.android.com/reference/kotlin/androidx/compose/material3/FilterChipDefaults
- ButtonDefaults morphing shape APIs: https://developer.android.com/reference/kotlin/androidx/compose/material3/ButtonDefaults
- LoadingIndicator API: https://developer.android.com/reference/kotlin/androidx/compose/material3/LoadingIndicator.composable
- LinearWavyProgressIndicator API: https://developer.android.com/reference/kotlin/androidx/compose/material3/LinearWavyProgressIndicator.composable

## 0.41.0 analysis/reference sources
- Khronos Vulkan 1.4.360 specification and Vulkan API Registry / vk.xml.
- Android API 37 Display.HdrCapabilities, including HDR_TYPE_HLG_PLUS value 6.
- ZXing Core 3.5.4 from Maven Central, Apache License 2.0, used only for local QR matrix generation.

## 0.41.2 compatibility sources
- Android java.nio.file package API level 26 reference: https://developer.android.com/reference/java/nio/file/package-summary.html
- Android system Os API reference for app-private atomic rename: https://developer.android.com/reference/android/system/Os
- AndroidX Core 1.19.0 release notes: https://developer.android.com/jetpack/androidx/releases/core
- AndroidX Lifecycle 2.11.0 release notes: https://developer.android.com/jetpack/androidx/releases/lifecycle


## 0.41.4 security/build sources
- Kotlin build-cache deserialization advisory CVE-2026-53914 / GHSA-r937-wjx7-w2jp: https://osv.dev/vulnerability/GHSA-r937-wjx7-w2jp
- Kotlin release status: https://kotlinlang.org/docs/releases.html
- Android Gradle Plugin 9.3 compatibility: https://developer.android.com/build/releases/agp-9-3-0-release-notes
- Current AndroidX version table: https://developer.android.com/jetpack/androidx/versions

## 0.41.5 Analysis/Profile sources
- Current Vulkan specification: https://registry.khronos.org/vulkan/specs/latest/html/vkspec.html — Vulkan 1.4.360 dated 2026-08-14.
- Vulkan Profiles overview: https://github.com/KhronosGroup/Vulkan-Profiles/blob/main/OVERVIEW.md
- Vulkan Profiles changelog: https://github.com/KhronosGroup/Vulkan-Profiles/blob/main/CHANGELOG.md
- Android 17 requirements: https://github.com/KhronosGroup/Vulkan-Profiles/blob/main/profiles/Android/VP_ANDROID_17_requirements.json
- Android 16 requirements: https://github.com/KhronosGroup/Vulkan-Profiles/blob/main/profiles/Android/VP_ANDROID_16_requirements.json
- Android 15 requirements: https://github.com/KhronosGroup/Vulkan-Profiles/blob/main/profiles/Android/VP_ANDROID_15_requirements.json
- Android Vulkan Profile 2025: https://github.com/KhronosGroup/Vulkan-Profiles/blob/main/profiles/Android/VP_ANDROID_vulkan_profile_2025.json
- Khronos Roadmap profiles: https://github.com/KhronosGroup/Vulkan-Headers/tree/main/registry/profiles

