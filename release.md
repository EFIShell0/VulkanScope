# VulkanScope 0.41.3

- Separated queue capability support from `VkQueueFamilyVideoPropertiesKHR` query availability.
- `VkQueueFlags == 0` is no longer mislabeled as `VK_NONE`; it is shown as zero queue capability bits.
- A successfully queried zero `VkVideoCodecOperationFlagsKHR` mask is shown canonically as `VK_VIDEO_CODEC_OPERATION_NONE_KHR`.
- Missing `VK_KHR_video_queue` is reported as Not applicable for video-codec-operation queries; failed or missing query evidence remains Unavailable/Unknown instead of being inferred as unsupported.
- Queue UI, TXT, HTML and Database technicalReport now carry the same video codec query state and reason.
- Corrected the Vulkan 1.4.360 known queue-bit mask to `0x57F`, so `VK_QUEUE_SPARSE_BINDING_BIT` is no longer duplicated as an unknown bit.
- Preserved Vulkan 1.4.360, schema 2 / technicalReport 3, all 0.41.2 safety hardening, and the 1469 property/query + 4 safety diagnostic evidence split.
