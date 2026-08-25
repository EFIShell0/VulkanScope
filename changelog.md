# VulkanScope 0.41.10

- Reworked Image Format Properties2 reporting into a complete bounded tuple-state ledger while keeping successful property payloads in normal detailed-property evidence.
- Every scheduled base tuple now records an exact `available`, `unsupported`, or `unavailable` outcome. `VK_ERROR_FORMAT_NOT_SUPPORTED` remains the only direct Unsupported result.
- OPAQUE_FD and Android Hardware Buffer tuple variants are still queried independently when their prerequisite device extension is enumerated.
- When `VK_KHR_external_memory_fd` or `VK_ANDROID_external_memory_android_hardware_buffer` is not enumerated, the corresponding tuple variants are explicitly recorded as `not_applicable` with the missing prerequisite instead of collapsing to `Unknown / Not reported`.
- Successful tuple ledger entries require `VkResult=0`; unsupported requires `VkResult=-11`; unavailable preserves another non-zero `VkResult`; not-applicable entries have no fabricated `VkResult`.
- Properties & Limits totals remain unchanged by the tuple-state ledger. UI, TXT, HTML, Analysis snapshots and Database technicalReport preserve the exact query state separately.
- Preserved the fixed Image Format Properties2 query recipe (`VK_IMAGE_TYPE_2D`, transfer-source/transfer-destination/sampled usage, `flags=0`) and independent external-handle execution.
- Updated the Gradle wrapper from 9.7.0 to 9.7.1, the current 9.7 patch release, while retaining AGP 9.3.2, API 37, NDK r29, Vulkan-Headers/query baseline 1.4.360, three release ABIs, schema 2 and technicalReport 3.
