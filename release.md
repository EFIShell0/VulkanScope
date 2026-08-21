# VulkanScope 0.33.5

VulkanScope 0.33.5 fixes GitHub update discovery for pre-release builds and aligns the update-status banner with OpenGLESScope.

## Changes

- Replaced the `/releases/latest` dependency with a bounded official GitHub release-list query.
- Added controlled pre-release discovery while ignoring drafts.
- Selects only a valid numerically newer VulkanScope version.
- Preserves ABI-specific APK selection with universal fallback.
- Strengthened official GitHub release-asset URL validation.
- Matched the `UP TO DATE`, update-available, checking and downloading banner presentation to OpenGLESScope.
- Capability collection, reporting, Turnip handling and database schema behavior are unchanged.

## Version

- Version: `0.33.5`
- versionCode: `335`
- Vulkan query/header staging baseline: `1.4.360`
