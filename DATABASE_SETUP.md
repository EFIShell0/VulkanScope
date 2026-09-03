# VulkanScope Database endpoint

VulkanScope 0.80.3 uses the fixed official VulkanScope Database Worker root:

`https://vulkanscope-database-api.vulkanscope.workers.dev`

The endpoint is not user-editable. Complete-report submission remains explicit and user initiated; no automatic/background report upload is performed. Submission remains schema 2 / technicalReport 3.

VulkanScope 0.80.3 preserves the Vulkan 1.4.361 registry/header baseline, complete-report gating, exact query-state distinctions, dedicated probe process, base/background time bounds, driver-bound Surface rebind, authoritative Vulkan Profiles evaluation, locked `vk.xml` + `video.xml` Vulkan Video exact-profile census and the 0.41.46 information architecture. The 0.80.0 hardening remains intact. The local Encyclopedia remains an offline, bounded Vulkan symbol/reference search generated from the locked registry and now opens as a separate Overview-parent page; Analysis likewise opens separately. The search delimiter crash fix does not change submission data. It does not change the submission envelope, runtime network paths or Vulkan evidence collection.

VulkanScope Database 0.39.21 remains schema-compatible with VulkanScope 0.80.3 because the submission envelope remains schema 2 and technicalReport remains schema 3. Database 0.39.21 itself still carries 0.41.46 / versionCode 456 as its current-producer display metadata; that companion metadata is not silently rewritten by this application-only release. No D1 migration is required.

A report is never truncated to satisfy the 2 MiB Database transport ceiling. Oversized submission fails explicitly while local UI/TXT/HTML evidence remains intact.
