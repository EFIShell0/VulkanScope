# VulkanScope Database endpoint

VulkanScope 0.30.6 uses the deployed official VulkanScope Database Worker root:

`https://vulkanscope-database-api.vulkanscope.workers.dev`

The endpoint is not user-editable. Settings exposes only the explicit `Submit complete report` action. VulkanScope validates the fixed HTTPS root and appends `/v1/reports` for complete-report submission.

The Submit button sends the complete currently collected technical report only after the user presses it. Submission is disabled while a Vulkan collection pass is active. There is no per-field capability selection and no automatic/background submission.
