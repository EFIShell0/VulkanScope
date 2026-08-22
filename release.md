# VulkanScope 0.34.7

VulkanScope 0.34.7 changes the fresh-install update default while preserving both the built-in GitHub updater and optional Obtainium guidance. Vulkan capability coverage is unchanged.

## Changes

- **Direct GitHub updates** are enabled by default on fresh installations.
- Existing installations keep their previously saved update preference.
- Disabling Direct GitHub updates still prevents startup update discovery and APK download.
- Obtainium remains an optional external update manager described in Settings and Info.
- The **Add to Obtainium** button remains removed.
- `obtainium-config.json` remains in the source release with universal-APK selection and architecture auto-filtering disabled.
- No Vulkan capability, report, export or Database behavior was intentionally changed.

## Version

- Version: `0.34.7`
- versionCode: `348`
- Package: `com.efishell.vulkanscope`
- Vulkan baseline: `1.4.360`
