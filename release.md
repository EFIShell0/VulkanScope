# VulkanScope 0.34.2

VulkanScope 0.34.2 is a complete-report parity and presentation-correctness release following the 0.34.0 CapsViewer field-level fix.

## Changes

- Re-audited native Vulkan collection through UI, TXT, HTML and VulkanScope Database submission.
- Verified the 0.34.0 Host Image Copy `pCopySrcLayouts` / `pCopyDstLayouts` results propagate through Properties & Limits, TXT, HTML and structured Database `detailedProperties`.
- Added instance-layer extension membership to TXT export.
- Memory heap/type flags now carry canonical Vulkan names plus exact raw values in TXT and HTML.
- Queue flags and Vulkan Video codec-operation flags now carry canonical Vulkan names plus exact raw values in TXT and HTML.
- Format linear/optimal/buffer feature masks now carry canonical Vulkan names plus exact raw values in TXT and HTML.
- Registry exports now include implemented physical-device structure names as well as validated query groups.
- Expanded Info and offline HTML Android build/security-patch provenance to match the local TXT diagnostic view.
- Database submission remains privacy-bounded and excludes sensitive device identifiers/private paths.
- Current published Vulkan specification remains Vulkan 1.4.360 (2026-08-14).

## Version

- VulkanScope: `0.34.2`
- versionCode: `342`
- Package: `com.efishell.vulkanscope`
- Query/header baseline: Vulkan 1.4.360
