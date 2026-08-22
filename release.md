# VulkanScope 0.34.8

VulkanScope 0.34.8 completes extended 64-bit Vulkan format-feature reporting and refreshes the Settings and Info icons to match the established Material 3 Expressive visual system.

## Changes
- `VkFormatProperties3` / `VkFormatFeatureFlags2` values are now consumed by the main Formats data model whenever Vulkan 1.3 or `VK_KHR_format_feature_flags2` makes them available.
- UI, structured report, TXT and HTML format sections now expose the same 64-bit format feature masks instead of falling back to legacy 32-bit masks.
- Legacy `VkFormatProperties` values remain the compatibility fallback when FormatFeatureFlags2 is unavailable.
- Unsigned 64-bit hexadecimal parsing preserves the full Vulkan mask bit pattern.
- Settings and Info use refreshed rounded Material 3 Expressive-aligned icons without changing navigation behavior.
- Direct GitHub updates remain enabled by default on fresh installs, with the existing opt-out and Obtainium guidance preserved.

## Version
- Version: `0.34.8`
- versionCode: `349`
