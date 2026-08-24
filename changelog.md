# VulkanScope 0.41.3

## Queue and Vulkan Video semantics
- Fixed zero queue flag presentation so `VkQueueFlags` no longer uses a nonexistent generic `VK_NONE` name.
- Added explicit per-queue video-codec query state and reason.
- `VK_VIDEO_CODEC_OPERATION_NONE_KHR` is used only when `VkQueueFamilyVideoPropertiesKHR` was actually queried and returned a zero mask.
- Missing or failed video-property queries remain Not applicable, Unavailable or Unknown and are never converted to Unsupported.
- Queue support booleans continue to come directly from `VkQueueFamilyProperties.queueFlags`; `false` therefore means the corresponding queue capability bit is not reported.
- Fixed the known queue-bit mask from `0x577` to the Vulkan 1.4.360 `0x57F`, including `VK_QUEUE_SPARSE_BINDING_BIT`.

## Reporting parity
- UI, TXT, HTML and Database technicalReport expose identical queue/video query semantics.
- Canonical schema versions remain unchanged.
