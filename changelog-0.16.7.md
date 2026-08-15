# 0.18.6

- Isolated base physical-device enumeration from surface and optional instance-extension enablement.
- Advanced query instance creation no longer enables VK_KHR_get_physical_device_properties2 on the enumeration path; Vulkan 1.1+ core entry points are used when exposed by the selected instance API version.
- Surface data is reported as unavailable by the base probe until a dedicated surface probe is used, preventing surface initialization from taking down base device enumeration.
- Improved native signal diagnostics for physical-device enumeration termination.


## 0.18.30
- Base Vulkan report becomes ready immediately after the complete core properties/features/limits/queues/memory/extensions/formats checkpoint.
- Base probe no longer continues into optional Surface/metadata work before returning to the UI.
- Optional Surface, metadata, core-version and extension queries remain isolated enrichment stages.
- No Vulkan capability/query was removed.
