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

## 0.2.9 – Turnip driver handling fix
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
