# VulkanScope Database endpoint

VulkanScope 0.41.13 uses the fixed official VulkanScope Database Worker root:

`https://vulkanscope-database-api.vulkanscope.workers.dev`

The endpoint is not user-editable. Complete-report submission remains explicit and user initiated; no automatic/background report upload is performed. Submission remains schema 2 / technicalReport 3.

VulkanScope Database 0.39.11 is the companion release for the 0.41.13 producer contract. It also corrects 0.41.12 complete submissions whose isolated Image Format Properties2 group explicitly completed as Unavailable/Not applicable.

A report is never truncated to satisfy the Database request-size bound. If a complete payload exceeds the 2 MiB submission bound, submission fails explicitly while local TXT/HTML report data remains intact.
