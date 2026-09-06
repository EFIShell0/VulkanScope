# VulkanScope Database endpoint

VulkanScope 0.80.15 uses the fixed official VulkanScope Database Worker root:

`https://vulkanscope-database-api.vulkanscope.workers.dev`

The endpoint is not user-editable. Complete-report submission remains explicit and user initiated; no automatic/background report upload is performed. Submission remains schema 2 / technicalReport 3.

VulkanScope 0.80.15 preserves the 0.80.10 Vulkan 1.4.362 producer/query behavior and all report/security semantics while applying a user-requested Material 3 Expressive presentation-coherence pass to Compose UI surfaces.

VulkanScope Database 0.39.27 is the companion Database for VulkanScope 0.80.15. It retains schema 2 / technicalReport 3, normalizer 16 and the Vulkan 1.4.362 producer/query contract; 0.80.15 does not introduce a Database schema or D1 migration change. New submissions still require VulkanScope 0.80.3 or newer. Existing historical reports remain readable and are not rewritten or rehashed.

A report is never truncated to satisfy the 2 MiB Database transport ceiling. Oversized submission fails explicitly while local UI/TXT/HTML evidence remains intact.

VulkanScope Database 0.39.27 accepts new submissions only from VulkanScope 0.80.3 or newer. Historical stored reports remain readable.
