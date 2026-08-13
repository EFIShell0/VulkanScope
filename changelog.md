# VulkanScope 0.15.5

- Fixed Vulkan 1.4 copy-layout property query scope and bounded second-query allocations.
- Preserved explicit unavailable state when safety caps are exceeded.
- Bumped versionCode to 53 and versionName to 0.15.5.

# VulkanScope 0.15.4

- Fixed Core 1.4 detailed-property population in the shared physical-device property query path.
- Stopped re-running core 1.1–1.4 probes during normal report collection; core feature/property status now comes from the base Vulkan instance.
- Preserved separate unavailable/not-applicable semantics for optional isolated advanced and extension queries.
- Bumped versionCode to 52 and versionName to 0.15.4.

# Changelog

## 0.15.3

- Fixed Kotlin `DeviceReport` construction after surface query metadata expansion.
- Preserved explicit surface-format query safety state in the UI report model.
- Version code increased to 51.

# VulkanScope 0.15.3

## 2026-08-13

- Fixed the native surface-format safety-cap error path so no Vulkan error enum is synthesized when a driver reports an unsafe collection size.
- Surface format queries now distinguish a skipped second query from a real VkResult and preserve unavailable semantics.
- Bumped versionCode to 51 and versionName to 0.15.3.

# VulkanScope 0.15.1

## 2026-08-13

- Reworked Core 1.1–1.4 feature/property collection to use the single base Vulkan instance and physical-device enumeration instead of redundant per-version probe restarts.
- Fixed duplicate instance-layer parsing.
- Added explicit Android UI explanations for empty instance/device layer lists.
- Hardened raw physical-device property copying against unaligned access and bounded Vulkan 1.4 variable-length layout arrays to prevent unsafe allocations.
- Preserved offline, on-device, registry-driven query behavior and all required ABIs.

# VulkanScope 0.15.0

## 2026-08-13

- Fixed release shrinker rules to keep the actual isolated Vulkan probe service native entry points.
- Removed the remaining source-code comments from the native Vulkan header to comply with PROJECT_RULES.md.
- Hardened probe result-file cleanup so failed service starts cannot leave temporary JSON files in the cache.
- Bumped versionCode to 48 and versionName to 0.15.0.
- Re-verified the checked-in Vulkan registry baseline against current Khronos upstream documentation before release preparation.

# VulkanScope 0.14.0

- Added isolated Vulkan Video capability probing through `vkGetPhysicalDeviceVideoCapabilitiesKHR`.
- Added codec-specific decode capability queries for H.264, H.265, VP9 and AV1 when the corresponding runtime extensions are enumerated.
- Added general encode capability probing for H.264, H.265 and AV1, with bitrate, quality-level, rate-control and feedback information.
- Added Vulkan Video format compatibility queries through `vkGetPhysicalDeviceVideoFormatPropertiesKHR` for sampled-image usage, with canonical runtime format names.
- Added Vulkan Video standard-header version, coded extent, DPB/reference limits and bitstream alignment information to detailed properties and TXT/HTML reports.
- Kept each codec profile independent: an unsupported or crashing codec-specific query does not invalidate the other video profiles.
- Updated registry query catalog to schema 5 and 57 validated runtime query groups.
- Added Vulkan Video codec extension names to the runtime extension catalog/filter set.
- Updated release verification to 0.14.0 / versionCode 46.

# VulkanScope 0.13.0

- Added runtime Vulkan Profile evaluation for Android Baseline 2022, Roadmap 2022, Roadmap 2024 and Roadmap 2026.
- Profile results distinguish PASS, FAIL and UNKNOWN; unavailable feature/limit queries are never treated as unsupported.
- Added official Roadmap capability requirements and limit checks for the evaluated profiles.
- Extended Queue Family Properties 2 reporting with Vulkan Video codec-operation capabilities exposed by `VK_KHR_video_queue` through `VkQueueFamilyVideoPropertiesKHR`.
- Queue reports can now show H.264, H.265, AV1 and VP9 decode/encode queue capabilities when the driver exposes them.
- TXT and HTML reports now include per-device profile evaluation and video queue capabilities.
- Preserved the registry-driven query catalog, Vulkan 1.4.357 baseline, isolated probes, Surface/HDR data, Turnip support and all existing ABI targets.

# VulkanScope 0.12.2

- Fixed malformed C++ registry query catalog initializer that broke all native ABIs.
- Added the missing official Vulkan 1.4 core feature structure used by the isolated 1.4 probe.
- Replaced the invalid generic 1.4 feature-chain object with the real `VkPhysicalDeviceVulkan14Features` layout and explicit feature serialization.
- Fixed `VulkanProbeService` worker startup by using Kotlin's supported `thread` helper.
- Updated release verification to validate 0.13.0 / versionCode 45.

# VulkanScope 0.12.1

- Fixed release build failure caused by malformed registry query catalog brace generation.
- Added the actual Vulkan 1.4 core feature structure and removed the invalid generic feature-chain use from the 1.4 probe.
- Fixed VulkanProbeService worker launch to use Kotlin's supported `thread` helper.
- Preserved existing Vulkan 1.0–1.4, registry-driven queries, Turnip, Surface/HDR and report features.

# VulkanScope 0.12.0

- Makes validated device-extension query dispatch consume the generated registry descriptor catalog instead of a second hard-coded extension-name mapping.
- Adds report schema and validated header-baseline metadata to native, TXT and HTML reports.
- Keeps runtime extension enumeration exact and distinguishes registry reference entries from runtime non-support.
- Adds a release verification gate covering version consistency, ABI policy, manifest permissions, registry catalog integrity and report/export presence.
- Keeps the Vulkan 1.4.357 offline registry baseline and the current Khronos registry/specification as the release authority.

# VulkanScope 0.11.0

- Promotes the offline registry catalog to an explicit runtime query-descriptor catalog.
- Adds catalog schema and instance dependency metadata to reports.
- Moves dependency-aware instance-extension candidate selection to the generated native registry catalog.
- Adds Vulkan 1.4 to the validated query-group catalog so coverage reporting matches the existing isolated Vulkan 1.4 probe.
- Fixes the registry generator duplicate argument definition and advances its manifest schema.

# VulkanScope 0.9.0

## Registry-driven query coverage
- Added an offline registry-driven Vulkan query catalog generator based on the Khronos `vk.xml` registry.
- The generator maps physical-device feature/property structs and extension requirements to a machine-readable coverage manifest.
- Generated registry metadata is consumed by the native report through a compiled query-catalog header.
- Added explicit Vulkan 1.4.357 registry baseline and query-engine metadata to native reports.
- Added an inventory path for registry-defined physical-device structs that are not represented by the minimal native header.
- Runtime remains fully offline; the application never downloads registry data.
- Unknown or unreviewed registry structures remain unavailable instead of being queried through guessed structure IDs or fields.

## Preserved
- Vulkan 1.0 through 1.4 core queries.
- Extended format, queue, image, external, sparse, group and tool queries.
- Extension enumeration and runtime specVersion reporting.
- Dependency-aware instance-extension enabling for runtime-enumerated dependencies.
- Turnip/custom-driver support and isolated query failure handling.
- Surface, Android HDR/display and existing TXT/HTML reporting.

## Version
- versionName: 0.9.0
- versionCode: 39
