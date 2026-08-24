# VulkanScope Database endpoint

VulkanScope 0.41.3 uses the fixed official VulkanScope Database Worker root:

`https://vulkanscope-database-api.vulkanscope.workers.dev`

The endpoint is not user-editable. Complete-report submission remains explicit and user initiated; no automatic/background report upload is performed. VulkanScope 0.41.3 keeps submission schema 2 / technicalReport 3 and remains within the compatible 0.x producer family accepted by VulkanScope Database 0.37.1.

A report is never truncated to satisfy the Database request-size bound. If a complete payload exceeds the allowed submission size, submission fails explicitly while local TXT/HTML report data remains intact.
