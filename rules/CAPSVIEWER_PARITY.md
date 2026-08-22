# CapsViewer parity methodology

This is a source-level audit only. It never infers runtime support from the database or from device model names.

When a local CapsViewer 4.12 checkout is available, run `tools/compare_capsviewer_4_12.py` or `tools/audit_capsviewer_parity.py --mode source` to classify source-referenced physical-device structs and fields as missing or partial.

A field is considered covered only when the canonical Vulkan header defines it, the runtime pNext query can produce it, and the generated/native report path consumes it. Unknown fields must remain raw/unknown rather than being guessed.

CapsViewer 4.13 adds extended format feature flags through `VK_KHR_format_feature_flags2`. VulkanScope parity for this path requires all three stages: a validated `VkFormatProperties3` pNext query when Vulkan 1.3 or the extension exposes it, explicit availability evidence, and consumption of the resulting 64-bit masks by the main Formats UI/export model. Falling back to legacy 32-bit masks while a valid Flags2 result exists is partial coverage.
