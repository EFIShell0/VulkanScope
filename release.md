# VulkanScope 0.35.0

VulkanScope 0.35.0 is a build-compatibility fix for the full Material 3 Expressive UI pass introduced in 0.34.9.

## Changes
- Fixes the `ShortNavigationBarItemDefaults.colors` call for AndroidX Compose Material3 `1.5.0-alpha26`.
- Replaces the invalid `selectedTextColor` argument with `selectedTextColorTopIconPosition` and `selectedTextColorStartIconPosition`, matching the current Material 3 API.
- Preserves the existing VulkanScope selected-label color, dark/red visual identity, navigation order, labels and Material 3 Expressive presentation.
- Keeps all Vulkan collection, 0.34.8 `VkFormatProperties3` / `VkFormatFeatureFlags2` handling, report/export, Database, Turnip/SAF and update behavior unchanged.

## Version
- Version: `0.35.0`
- versionCode: `351`
