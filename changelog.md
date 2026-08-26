# VulkanScope 0.41.14

- Fixed the release-blocking Kotlin compilation failure in `imageFormatQueryGroupState` by using the actual `DeviceReport` model type instead of the nonexistent stale `GpuInfo` type.
- Preserved all VulkanScope 0.41.13 Image Format Properties2 query-group state, Database submission, schema 2 / technicalReport 3, Vulkan 1.4.360 and three-ABI behavior unchanged.
- Added a release-verifier regression gate for the helper model type.
