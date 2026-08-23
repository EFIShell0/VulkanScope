# VulkanScope 0.35.1

VulkanScope 0.35.1 is a full security, lifecycle and correctness follow-up to 0.35.0.

## Changes
- Cancels active GitHub update checks and APK downloads immediately when Direct GitHub updates are disabled.
- Prevents an already-completed update download from opening the package installer after update opt-out.
- Deletes rejected APK files from the private cache in addition to partial download cleanup.
- Hardens Android signing verification for key rotation: the installed current signer must be in the candidate APK signing lineage; multi-signer packages require exact current signer-set equality.
- Re-audits Vulkan 1.4.360 query coverage, Surface/WSI, formats, properties/limits, Display/HDR, Turnip, UI/TXT/HTML/Database completeness and native resource ownership.
- Preserves the 0.34.8 64-bit VkFormatProperties3 / VkFormatFeatureFlags2 handling and 0.35.0 Material 3 Expressive build fix.

## Version
- Version: `0.35.1`
- versionCode: `352`
- Vulkan published specification/query baseline: `1.4.360`
