# VulkanScope 0.34.4

VulkanScope 0.34.3 adds IzzyOnDroid-compatible self-update behavior without changing Vulkan capability coverage.

## Changes

- Direct GitHub update checks are disabled by default for every installation.
- Settings now owns the opt-in Direct GitHub updates switch.
- Enabling it requires explicit confirmation of the official GitHub Releases source and warns that direct APK updates bypass IzzyOnDroid repository scanning and verification.
- No startup update request is made while the feature is disabled.
- Manual update checks in Info remain unavailable until the feature is enabled.
- Existing APK URL, package, signing certificate, ABI and version validation remains unchanged when direct updates are enabled.
- Added Fastlane metadata suitable for IzzyOnDroid ingestion.
- Vulkan 1.4.360 query/header coverage and UI/TXT/HTML/Database reporting remain unchanged.

## Version

- Version: `0.34.3`
- versionCode: `344`
- Package: `com.efishell.vulkanscope`
- Vulkan baseline: `1.4.360`


## 0.34.4 first-install update notice

- Added a one-time seven-second first-install information banner for the default-disabled direct GitHub updater, reusing the existing update-status banner design.
- The notice is non-modal, performs no network request, appears only on genuine first installation, and does not replace the separate IzzyOnDroid bypass confirmation required when direct updates are enabled.
