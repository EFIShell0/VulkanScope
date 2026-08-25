# VulkanScope 0.41.7

- Fixed Image Format Properties2 collection so external-memory handle queries are executed independently from the handle-less base query, as required by Vulkan query semantics.
- `VK_ANDROID_external_memory_android_hardware_buffer` and `VK_KHR_external_memory_fd` Image Format Properties2 evidence is no longer suppressed solely because the base format/tiling query returned an error.
- Added compact Image Format Properties2 query diagnostics (attempted/success/format-not-supported/other-error counts) without expanding every negative format tuple into the report.
- Preserved VulkanScope 0.41.6 Kotlin compile-gate hardening, Vulkan 1.4.360, schema 2 / technicalReport 3 and three-ABI coverage.
- Recommended companion Database is VulkanScope Database 0.39.5 or newer for cross-producer compare warnings, common-evidence filtering and canonical Profile comparison.
