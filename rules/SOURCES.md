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
- libadrenotools master history was checked on 2026-08-13; the Android build is pinned to the full immutable commit `8fae8ce254dfc1344527e05301e43f37dea2df80` rather than a mutable branch. CMake shallow cloning is disabled for this hash pin because CMake does not permit `GIT_SHALLOW` with a commit hash.

- Current build-time canonical header source: KhronosGroup/Vulkan-Headers commit `0b7f383797fa7be53ae28213e001ae60668ee511`; `VK_HEADER_VERSION` is verified as 360 before native compilation. The current published specification is also Vulkan 1.4.360; the pinned header revision and runtime-reported API/loader/driver versions are still reported as distinct provenance.

## 0.34.9 Material 3 Expressive UI sources
- AndroidX Compose Material 3 release notes (project-pinned audited expressive baseline 1.5.0-alpha26; upstream release tables may publish newer alphas independently): https://developer.android.com/jetpack/androidx/releases/compose-material3
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


## 0.41.10 Image Format Properties2 / build recheck
- Khronos Vulkan 1.4.360 specification, dated 2026-08-14: https://registry.khronos.org/vulkan/specs/latest/html/vkspec.html
- `vkGetPhysicalDeviceImageFormatProperties2`: https://docs.vulkan.org/refpages/latest/refpages/source/vkGetPhysicalDeviceImageFormatProperties2.html
- `VkPhysicalDeviceExternalImageFormatInfo`: https://docs.vulkan.org/refpages/latest/refpages/source/VkPhysicalDeviceExternalImageFormatInfo.html
- `VkExternalImageFormatProperties`: https://docs.vulkan.org/refpages/latest/refpages/source/VkExternalImageFormatProperties.html
- Android Gradle Plugin 9.3 compatibility: https://developer.android.com/build/releases/agp-9-3-0-release-notes
- Android Studio Quail 2 fixed issues / AGP 9.3.2 publication: https://developer.android.com/studio/releases/fixed-bugs/studio/2026.1.2
- Android NDK downloads, stable r29 `29.0.14206865`: https://developer.android.com/ndk/downloads
- Gradle 9.7.1 release notes: https://docs.gradle.org/9.7.1/release-notes.html


## 0.41.20 final correctness/build sources
- Khronos current Vulkan Registry/specification: https://registry.khronos.org/vulkan/ — rechecked 2026-08-27 as Vulkan 1.4.360 dated 2026-08-14.
- Android 17 SDK setup / API 37: https://developer.android.com/about/versions/17/setup-sdk
- Android Gradle Plugin 9.3 release notes / compatibility: https://developer.android.com/build/releases/agp-9-3-0-release-notes
- AndroidX current versions: https://developer.android.com/jetpack/androidx/versions — stable Compose UI/Foundation/Animation 1.12.0, Activity 1.13.0 and Core 1.19.0 remain aligned with the project pins; Material3 is intentionally held to the rules-pinned 1.5.0-alpha26 expressive baseline rather than automatically following newer alphas.
- CMake ExternalProject Git options: https://cmake.org/cmake/help/v3.22/module/ExternalProject.html — commit hashes are not valid with `GIT_SHALLOW` enabled.
- libadrenotools pinned source commit: https://github.com/bylaws/libadrenotools/commit/8fae8ce254dfc1344527e05301e43f37dea2df80


## 0.41.21 Surface / external capability sources
- Khronos current Vulkan Registry/specification: https://registry.khronos.org/vulkan/ — rechecked 2026-08-28 as Vulkan 1.4.360 dated 2026-08-14.
- `vkGetPhysicalDeviceSurfaceSupportKHR`: https://docs.vulkan.org/refpages/latest/refpages/source/vkGetPhysicalDeviceSurfaceSupportKHR.html
- `vkGetPhysicalDeviceSurfaceCapabilitiesKHR`: https://docs.vulkan.org/refpages/latest/refpages/source/vkGetPhysicalDeviceSurfaceCapabilitiesKHR.html
- `vkGetPhysicalDeviceSurfaceFormatsKHR`: https://docs.vulkan.org/refpages/latest/refpages/source/vkGetPhysicalDeviceSurfaceFormatsKHR.html — a non-null Surface must be supported by the physical device before this query.
- `vkGetPhysicalDeviceSurfacePresentModesKHR`: https://docs.vulkan.org/refpages/latest/refpages/source/vkGetPhysicalDeviceSurfacePresentModesKHR.html — a non-null Surface must be supported by the physical device before this query.
- `VkExternalFenceHandleTypeFlagBits` / FD import: https://docs.vulkan.org/refpages/latest/refpages/source/VkImportFenceFdInfoKHR.html
- `VkExternalSemaphoreHandleTypeFlagBits` / FD import: https://docs.vulkan.org/refpages/latest/refpages/source/VkImportSemaphoreFdInfoKHR.html
- `VkExternalMemoryHandleTypeFlagBits`: https://docs.vulkan.org/refpages/latest/refpages/source/VkExternalMemoryHandleTypeFlagBits.html
- Android 17 SDK / API 37: https://developer.android.com/about/versions/17/setup-sdk
- Android Gradle Plugin 9.3 compatibility: https://developer.android.com/build/releases/agp-9-3-0-release-notes


## 0.41.22 full re-audit sources
- Khronos Vulkan API Registry 1.4.360, dated 2026-08-14: https://registry.khronos.org/vulkan/specs/latest/registry.html
- Pinned Vulkan-Headers 1.4.360 `vulkan_core.h` at commit `0b7f383797fa7be53ae28213e001ae60668ee511`: https://raw.githubusercontent.com/KhronosGroup/Vulkan-Headers/0b7f383797fa7be53ae28213e001ae60668ee511/include/vulkan/vulkan_core.h
- Android 17 SDK setup / API 37: https://developer.android.com/about/versions/17/setup-sdk
- Android Gradle Plugin 9.3 release notes and 9.3.2 fixed-issue publication: https://developer.android.com/build/releases/agp-9-3-0-release-notes
- Android Gradle Plugin compatibility table: https://developer.android.com/build/releases/about-agp
- Android Display.Mode / HDR capability API reference used for state-aware HDR evidence: https://developer.android.com/reference/android/view/Display.Mode
- Vulkan Video format properties query reference used for zero-result / `VK_INCOMPLETE` semantics: https://docs.vulkan.org/refpages/latest/refpages/source/vkGetPhysicalDeviceVideoFormatPropertiesKHR.html

## 0.41.25 Android 17 hardening and dependency freshness sources
- Khronos current Vulkan Registry/specification: https://registry.khronos.org/vulkan/ — rechecked 2026-08-28 as Vulkan 1.4.360 dated 2026-08-14.
- Android 17 behavior changes / native dynamic-code loading hardening: https://developer.android.com/about/versions/17/behavior-changes-17
- Android dynamic code loading security guidance: https://developer.android.com/privacy-and-security/risks/dynamic-code-loading
- AndroidX current versions, updated 2026-08-26: https://developer.android.com/jetpack/androidx/versions — Compose UI/Foundation/Animation 1.12.0, Core 1.19.0, Activity 1.13.0, Lifecycle 2.11.0 and Material 3 1.5.0-alpha27.
- Compose Material 3 1.5.0-alpha27 release notes: https://developer.android.com/jetpack/androidx/releases/compose-material3
- Kotlin current stable release status: https://kotlinlang.org/docs/releases.html — Kotlin 2.4.10.
- Kotlin Compose compiler migration guide for AGP 9 built-in Kotlin: https://kotlinlang.org/docs/compose-compiler-migration-guide.html
- Android Kotlin/AGP compatibility table: https://developer.android.com/build/kotlin-support
- Android NDK downloads: https://developer.android.com/ndk/downloads — stable r29 `29.0.14206865` remains the project pin.
- OkHttp current release/change log: https://lysine.dev/okhttp/changelogs/changelog/ — 5.5.0 dated 2026-08-16; ECH/alternate DNS is opt-in.
- OkHttp 5.x `Dns` API: https://lysine.dev/okhttp/5.x/okhttp/okhttp3/-dns/ — `lookup(hostname)` remains the single abstract fun-interface method and `newCall` is an open extension point.
- ZXing Core Maven Central versions: https://central.sonatype.com/artifact/com.google.zxing/core/versions — 3.5.4 remains current.

## 0.41.26 export lifecycle / driver mutation sources
- Khronos current Vulkan API Registry: https://registry.khronos.org/vulkan/specs/latest/registry.html — rechecked 2026-08-28 as Vulkan 1.4.360 dated 2026-08-14.
- Android Compose state guidance: https://developer.android.com/develop/ui/compose/state-saving — `rememberSaveable` is for small restoration metadata across Activity/system process recreation; large report payloads should not be stored in the saved-state Bundle.
- Android Compose state model: https://developer.android.com/develop/ui/compose/state — plain `remember` does not survive configuration recreation while `rememberSaveable` can restore Bundle-saveable state.
- Android Storage Access Framework document creation: https://developer.android.com/training/data-storage/shared/documents-files — `ACTION_CREATE_DOCUMENT`/CreateDocument provides the user-selected destination URI.
- Android Gradle Plugin 9.3 compatibility: https://developer.android.com/build/releases/agp-9-3-0-release-notes — API 37 maximum, Gradle 9.5 minimum and R8 configuration analysis support.
- AndroidX current versions: https://developer.android.com/jetpack/androidx/versions — Compose UI/Foundation 1.12.0 and Material 3 1.5.0-alpha27 remain current reviewed pins.
- Kotlin release status: https://kotlinlang.org/docs/releases.html — 2.4.10 remains the latest Kotlin 2.4 bug-fix release.
- Android NDK downloads: https://developer.android.com/ndk/downloads — stable r29 `29.0.14206865` remains the project pin.


## 0.80.0 full security/memory/specification re-audit sources
- Khronos current Vulkan Registry/specification, rechecked 2026-09-03 as Vulkan 1.4.361: https://registry.khronos.org/vulkan/
- Locked Vulkan 1.4.361 registry source: https://raw.githubusercontent.com/KhronosGroup/Vulkan-Docs/1.4.361/xml/vk.xml
- Android 17 SDK setup / API 37: https://developer.android.com/about/versions/17/setup-sdk
- Android Gradle Plugin 9.4 release notes and API 37 compatibility: https://developer.android.com/build/releases/agp-9-4-0-release-notes
- Android Gradle Plugin compatibility/version table: https://developer.android.com/build/releases/about-agp
- Android NDK downloads, stable r29 `29.0.14206865`: https://developer.android.com/ndk/downloads
- AndroidX Core releases, stable 1.19.0: https://developer.android.com/jetpack/androidx/releases/core
- AndroidX Activity releases, stable 1.13.0: https://developer.android.com/jetpack/androidx/releases/activity
- AndroidX Compose releases, UI/Foundation/Animation stable 1.12.0: https://developer.android.com/jetpack/androidx/releases/compose
- AndroidX Compose Material 3 releases, project Expressive pin 1.5.0-alpha27: https://developer.android.com/jetpack/androidx/releases/compose-material3
- AndroidX Lifecycle releases, stable 2.11.0: https://developer.android.com/jetpack/androidx/releases/lifecycle
- Kotlin releases, stable 2.4.10: https://kotlinlang.org/docs/releases.html
- OkHttp releases, project pin 5.5.0: https://github.com/square/okhttp/releases
- ZXing Core Maven Central versions, project pin 3.5.4: https://central.sonatype.com/artifact/com.google.zxing/core/versions
- Android dynamic code loading security guidance: https://developer.android.com/privacy-and-security/risks/dynamic-code-loading
