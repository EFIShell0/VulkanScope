# VulkanScope 0.4

## Changes
- Removed the Instance navigation tab completely.
- Moved the existing Instance page content into the Vulkan tab.
- Removed the Explore section from the Vulkan tab because the same Explore content is already available in Overview.
- Removed the `Device Detected` and `Offline Inspection` labels from the Overview GPU/device hero area.
- Kept the existing Material 3 Expressive navigation and page-transition work unchanged.
- Kept the Google TV launcher support and supplied GPU vendor artwork changes from the preceding releases.
- Updated the application version to `0.4`.

# VulkanScope 0.3.1

## Changes
- Replaced the Huawei GPU vendor artwork with the supplied Huawei logo.
- Replaced the Vivante/VeriSilicon GPU vendor artwork with the supplied VeriSilicon logo.
- Removed vendor recognition and vendor logos for PoCL, Mesa, MediaTek, Khronos, Mobileye, Kazan, Codeplay, Apple and APE; these vendors are now presented as `Unknown`.
- Updated the application version to `0.3.1`.

# VulkanScope 0.3.0

## Changes
- Added Google TV / Android TV launcher support so VulkanScope can appear as a TV application.
- Removed the redundant Refresh button from the bottom of Overview.
- Updated the portrait navigation presentation to use the existing navigation icons with the Material 3 Expressive visual treatment.
- Added a smooth Material 3-style animated transition between navigation pages instead of an abrupt content swap.
- Updated the application version to `0.3.0`.

# VulkanScope 0.2.25

## Changes
- Replaced the application resource set with the newly supplied resources, including updated navigation icons, Vulkan/GPU vendor artwork and launcher assets.
- Consolidated version history into the single `changelog.md`; removed the separate per-version `changelog_*.md` files from the project ZIP.

# VulkanScope 0.2.24

## Changes
- Made the landscape navigation rail vertically scrollable when the available height cannot contain every navigation destination.
- Kept the existing compact Material 3 Expressive rail sizing, icons, labels, colors and portrait bottom navigation unchanged.

# VulkanScope 0.2.22

- Tightened the landscape navigation rail layout so all navigation destinations remain compact, aligned and visible without excessive empty space.
- Kept the navigation rail background consistent with the dark VulkanScope interface and retained the existing red accent and Vulkan icon.
- Kept the portrait bottom navigation bar unchanged; the navigation rail is used only in landscape mode.
- Reworked Overview Quick access cards into a smaller icon-over-label layout so all shortcuts fit cleanly in portrait mode without truncated labels.
- Kept the GPU/device hero card at the top of Overview in both orientations.
- Clarified the Turnip unavailable message to require both arm64-v8a and a Qualcomm Adreno GPU.

# VulkanScope 0.2.21

- Updated the UI to a more expressive Material 3 visual system with larger expressive shapes, tighter hierarchy and responsive navigation.
- Added a Material 3 Navigation Rail for landscape mode; portrait mode keeps the existing bottom navigation bar.
- Kept the existing Vulkan navigation mark and red accent treatment.
- Reworked Overview Quick access into a compact 4-column layout so all shortcuts fit cleanly in portrait mode.
- Kept the Overview GPU/device hero information at the top of the page in both orientations.
- Huawei Maleoon GPUs now use the Huawei vendor logo in the GPU hero card.
- Vivante GPUs now use the VeriSilicon vendor logo.
- Clarified the Turnip unavailable message to require both arm64-v8a and a Qualcomm Adreno GPU.

# VulkanScope 0.2.20

- Changed the Android application ID and namespace to `com.efishell.vulkanscope`.
- Updated native JNI entry points and restart-task affinity for the new package.

# VulkanScope 0.2.19

- Resized the Vulkan navigation mark to the same 24dp icon box used by the other bottom navigation icons.
- The Vulkan mark now follows the active red navigation color without the oversized selection pill.
- Kept the supplied Vulkan mark geometry unchanged apart from transparent-margin normalization.

## 0.2.16 build fix

- Restored the Display, Surface, Features, Memory, Queues and Formats Compose pages required by PageContent.
- Keeps the 0.2.15 detailed Vulkan property/export changes.
- Preserves the multi-ABI configuration and Adreno/arm64 Turnip eligibility check.

## 0.2.14

- Build arm64-v8a, armeabi-v7a and x86_64 APKs plus universal APK.
- Turnip is offered only on arm64-v8a devices whose system Vulkan GPU vendor is Qualcomm/Adreno (0x5143).
- Added instance layer/runtime inspection.
- Expanded format inspection and format feature bitmasks.
- Added search and supported/not-supported/all filters to Extensions and Surface.
- Added surface color-space candidate catalog for filtering unsupported pairs.

# VulkanScope 0.2.10 — Turnip/restart/UI fix

- Initial Vulkan inspection waits for the SurfaceView to be created, preventing an early empty inspection.
- Vulkan inspection errors are now visible in the Overview instead of looking like an empty app.
- Driver mode changes are persisted with a synchronous commit before restart.
- Restart now uses a separate-process RestartActivity so MIUI/HyperOS can relaunch the app after the Vulkan/native process is terminated.
- Restart confirmation is a Material 3 Compose dialog matching the app's dark rounded UI instead of the old Android AlertDialog.
- Turnip bundle installation remains transactional and does not delete the working bundle until the new bundle is ready.
- Native loader errors now report the actual Turnip dlopen/export failure where available.
- System Vulkan remains the default on a fresh install; selecting Turnip is retained across restart.

# Changelog

## 2026-08-08

- Fixed the native Vulkan build errors caused by missing `VK_FALSE` and `VK_ERROR_UNKNOWN` definitions in the minimal Vulkan header path by removing the unnecessary constant dependencies from the affected code.
- Fixed the Kotlin nullability warning/error at Vulkan report parsing for the optional `error` JSON field.
- Serialized native Vulkan collection so refresh and Surface lifecycle-triggered collection cannot execute concurrently against the same Vulkan surface.
- Ensured the hardware-data loading state is cleared with `finally` when collection exits.
- Kept `VK_EXT_swapchain_colorspace` instance-extension enablement and real `vkGetPhysicalDeviceSurfaceFormatsKHR` format/color-space querying intact.

## 0.2.4

- Replaced the Features navigation glyph with an information-circle icon.
- Added a settings action to the top app bar.
- Added Vulkan driver selection settings: System Vulkan driver and Turnip / third-party driver bundle.
- Added ZIP import for third-party Vulkan driver bundles and restart-on-selection behavior.
- Native Vulkan loading now supports Mesa-style VK_DRIVER_FILES / VK_ICD_FILENAMES and attempts a bundled libvulkan.so when a third-party bundle provides one.
- Recolored the Vulkan navigation mark to match the application's red accent while retaining the Vulkan mark geometry.

## 0.2.9 — Turnip driver handling fix

- Fixed startup crash caused by reading Activity SharedPreferences before Activity attachment.
- Turnip selection no longer changes active mode before a successful ZIP import/restart.
- ZIP import now uses a temporary directory and replaces the installed bundle only after validation succeeds.
- Any `.json` and `.so` filenames are accepted; `libraryName` is honored when present.
- Restart is requested only after a successful driver import or an explicit driver-mode change.
- Restart performs a real process restart so the native Vulkan library/ICD is reloaded.
- Direct Turnip ICD loading now uses `vk_icdGetInstanceProcAddr`/`vkGetInstanceProcAddr` for global Vulkan entry points.
- Turnip mode no longer silently falls back to the system Vulkan driver if the selected ICD cannot be loaded.

## 0.2.11 — rootless Turnip / AdrenoTools

- Replaced direct `dlopen(libvulkan_freedreno.so)` with `libadrenotools` custom-driver loading.
- Uses the Android app's `nativeLibraryDir` as the AdrenoTools hook directory.
- Keeps the imported Turnip package in app-private storage.
- Enables legacy native-library packaging, required by AdrenoTools hooks.
- Restricts native ABI to arm64-v8a, matching AdrenoTools support.
- System Vulkan remains the default unless the user explicitly selects Turnip.
- Turnip loading now reports an AdrenoTools-specific error instead of misleading `libhardware.so` namespace failures.

## 0.2.15

- Fixed Instance bottom-navigation selection so Instance no longer highlights Overview.
- Added a dedicated Instance icon instead of reusing the Vulkan icon.
- Added detailed Vulkan Core 1.1/1.2/1.3 physical-device properties via vkGetPhysicalDeviceProperties2.
- Added extension property inspection for VK_EXT_fragment_density_map and VK_EXT_fragment_density_map2 when exposed by the device.
- Added Vulkan Profiles catalog page matching the profile entries shown by the reference capability viewer.
- Expanded Instance operating-system/build information.
- Expanded queue-family reporting with protected/optical-flow flags and minImageTransferGranularity.
- Added complete TXT and HTML report export from Settings using the Android document picker.
