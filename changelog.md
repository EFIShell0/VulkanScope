# 0.34.8

- Completed `VK_KHR_format_feature_flags2` / Vulkan 1.3 format-feature data flow: `VkFormatProperties3` 64-bit masks now feed the main Formats model, UI, structured report, TXT and HTML exports, with legacy `VkFormatProperties` fallback only when FormatFeatureFlags2 is unavailable.
- Added unsigned 64-bit parsing for format-feature masks so future high-bit values remain lossless and unknown bits can still be preserved by the canonical/raw formatter.
- Refreshed Settings and Info icons with rounded Material 3 Expressive-aligned vector artwork while preserving existing navigation geometry, touch targets and VulkanScope visual hierarchy.

# 0.34.7

- Direct GitHub updates are now enabled by default on fresh installations so users receive release checks without prior setup.
- Existing users' saved update preference is preserved; disabling Direct GitHub updates still stops startup checks.
- Obtainium remains an optional external update manager and its informational guidance and portable configuration are retained.
- The Add to Obtainium button remains removed.

# 0.34.6

- Removed the Add to Obtainium action from Settings to keep update controls minimal.
- Kept Direct GitHub updates opt-in and disabled by default.
- Kept Obtainium guidance as informational text and retained the portable obtainium-config.json.

# 0.34.5

- Replaced IzzyOnDroid-specific update messaging with Obtainium-oriented update management.
- Added one-tap Obtainium import configured to select the universal APK from official GitHub Releases.
- Kept the built-in updater opt-in and disabled by default.
- Updated first-install and consent messaging to avoid duplicate update-manager notifications.
- Added `obtainium-config.json`.

# 0.34.4

- Added a one-time seven-second first-install information banner for the default-disabled direct GitHub updater, reusing the existing update-status banner design.
- Direct GitHub updates remain opt-in and disabled by default; IzzyOnDroid bypass consent remains mandatory.

# 0.34.2

- Full UI/TXT/HTML/Database report-path parity audit.
- Added canonical + raw Vulkan masks to TXT/HTML memory, queue and format reporting.
- Added instance-layer extensions to TXT.
- Added implemented registry-structure names to TXT/HTML provenance.
- Expanded local Android security/build provenance presentation.
- Preserved 0.34.0 Host Image Copy array reporting through every complete-report consumer.
- versionName 0.34.2 / versionCode 343.

# 0.34.0

- Completed strict Host Image Copy field parity for VulkanCapsViewer 4.12.
- Added bounded two-call pCopySrcLayouts/pCopyDstLayouts collection and canonical VkImageLayout names with raw values.
- Corrected field-audit alias handling for promoted EXT/KHR/core structures.
- Updated published Vulkan provenance to 1.4.360.
- Bumped versionName to 0.34.0 and versionCode to 341.

## 0.34.3
- Made the direct GitHub self-updater opt-in and disabled by default.
- Added a Settings consent gate describing the official APK source and IzzyOnDroid screening/verification bypass.
- Added Fastlane metadata for IzzyOnDroid repository ingestion.
- Preserved Vulkan 1.4.360 capability/reporting behavior.
