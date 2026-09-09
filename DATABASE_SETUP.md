# VulkanScope Database endpoint

VulkanScope 1.0.19 uses the fixed official VulkanScope Database Worker root:

`https://vulkanscope-database-api.vulkanscope.workers.dev`

The endpoint is not user-editable. Complete-report submission remains explicit and user initiated; no automatic/background report upload is performed. Submission remains schema 2 / technicalReport 3.

VulkanScope 1.0.19 preserves the Vulkan 1.4.362 producer/query and canonical report behavior of 1.0.18. The release refines local semantic artwork and horizontal filter-carousel colors without changing runtime Vulkan/report behavior, update security, export behavior or Database transport. Database submission serialization, endpoint confinement, complete-report gating, privacy exclusions, report identity, Vulkan capability semantics and native collection remain unchanged.

VulkanScope Database 1.0.8 is the companion Database for VulkanScope 1.0.19. It retains schema 2 / technicalReport 3, normalizer 16, the Vulkan 1.4.362 producer/query contract and the generic 1.0.x producer-identity mapping where versionCode equals 1000 plus the patch version. VulkanScope 1.0.19 / versionCode 1019 is therefore valid while a mismatched versionCode is rejected.

Complete report submission is an explicit HTTPS POST to `/v1/reports`. The Analysis Database-compare tool performs only an explicit user-initiated bounded HTTPS GET of `/v1/reports/<lowercase-64-hex-id>?compact=1` from the same fixed official origin and requires the returned payload to contain `technicalReport` before local comparison.

A report is never truncated to satisfy the 2 MiB Database transport ceiling. Oversized submission fails explicitly while local UI/TXT/HTML evidence remains intact. Capability fields cannot be selectively omitted from a Database submission, and no automatic/background report upload is permitted.

VulkanScope Database 1.0.8 accepts new submissions from VulkanScope 0.80.3 or newer under its published producer contract. Existing historical reports remain readable and are not rewritten or rehashed.
