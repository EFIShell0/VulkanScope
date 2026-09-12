# VulkanScope Database endpoint

VulkanScope 1.2.5 uses the fixed official VulkanScope Database Worker root:

`https://vulkanscope-database-api.vulkanscope.workers.dev`

The endpoint is not user-editable. Complete-report submission remains explicit and user initiated; no automatic or background report upload is performed. Submission remains schema 2 / technicalReport 3. VulkanScope 1.2.5 retains the typed submission-result handling, bounded failure diagnostics and exact successful report-ID presentation introduced in 1.2.3; this release does not change the submitted schema.

VulkanScope 1.2.5 preserves the Vulkan 1.4.362 producer/query and canonical report behavior of 1.2.4. Native Vulkan collection, report serialization, endpoint confinement, privacy exclusions, report identity and capability semantics remain unchanged. Successful submission requires a valid lowercase 64-hex report ID; failed local validation, serialization, HTTP, response-validation and network-request outcomes expose a bounded copyable diagnostic log.

The Database service may independently raise its accepted producer-version floor. If that happens, reports from an older VulkanScope build can be rejected by the server; VulkanScope surfaces the returned rejection as an error rather than treating it as a successful submission. This package does not claim an unverified fixed companion Database application version.

Complete report submission is an explicit HTTPS POST to `/v1/reports`. The Analysis Database-compare tool performs only an explicit user-initiated bounded HTTPS GET of `/v1/reports/<lowercase-64-hex-id>?compact=1` from the same fixed official origin and requires the returned payload to contain `technicalReport` before local comparison.

A report is never truncated to satisfy the 2 MiB Database transport ceiling. Oversized submission fails explicitly while local UI/TXT/HTML evidence remains intact. Capability fields cannot be selectively omitted from a Database submission, and no automatic/background report upload is permitted.
