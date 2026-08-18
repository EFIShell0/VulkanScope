# VulkanCapsViewer 4.12 parity audit

Baseline: VulkanCapsViewer 4.12, Vulkan-Headers 1.4.357.

## Source parity

- VulkanCapsViewer 4.12 extension feature/property handlers inspected: 304 extension names.
- VulkanScope physical-device pNext mappings matching those handlers: 299 extension names.
- VulkanScope now automatically schedules every one of those 299 mappings when the extension is actually enumerated by the active Vulkan device.
- CapsViewer physical-device type tokens inspected: 395.
- VulkanScope native source covers all of those type tokens except the three documented cases below.

## Vendor/query namespace coverage

- AMD: 6
- AMDX: 2
- ANDROID: 1
- ARM: 13
- EXT: 111
- HUAWEI: 4
- IMG: 1
- INTEL: 1
- KHR: 87
- MESA: 1
- MSFT: 1
- NV: 45
- NVX: 1
- QCOM: 17
- SEC: 3
- VALVE: 5

## Deliberate non-device/platform cases

- `VK_KHR_external_fence_capabilities`: instance capability dependency; covered by VulkanScope external fence capability querying rather than a fabricated device-extension feature group.
- `VK_KHR_external_memory_capabilities`: instance capability dependency; covered by VulkanScope external memory capability querying rather than a fabricated device-extension feature group.
- `VK_KHR_external_semaphore_capabilities`: instance capability dependency; covered by VulkanScope external semaphore capability querying rather than a fabricated device-extension feature group.
- `VK_OHOS_native_buffer` / `VkPhysicalDevicePresentationPropertiesOHOS`: OHOS-only and not queried on Android.
- `VK_QNX_external_memory_screen_buffer` / `VkPhysicalDeviceExternalMemoryScreenBufferFeaturesQNX`: QNX Screen-only and not queried on Android.
- `VkPhysicalDeviceIDProperties`: represented through VulkanScope's validated Vulkan 1.1+ core property path; it is not duplicated as a separate extension serializer.

## Complete-report behavior

- Extension feature/property collection is no longer limited to the hand-selected UI query-group list.
- Runtime-enumerated extensions are intersected with the 299 validated CapsViewer 4.12 physical-device mappings and queried automatically.
- The isolated probe service uses one worker, so complete-report collection executes these probes sequentially instead of allowing queued requests to time out before execution.
- Failed queries are retained in the same report as explicit unavailable query-status records.
- Not-enumerated extensions are not mislabeled unsupported.
