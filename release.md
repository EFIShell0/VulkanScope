# VulkanScope 4.0.15 — consolidated release notes

This document summarizes the **final capabilities and user-visible changes established from the 4.0.0 series through 4.0.15**. It describes the retained 4.0.15 behavior, not earlier experiments or superseded visual treatments. Build errors and temporary development issues are intentionally excluded.

## Vulkan 1.4.366 and capability inspection

- Updated the locked Vulkan registry/header baseline to **Vulkan 1.4.366** and refreshed the offline symbol/reference catalogs.
- Retained **305 validated physical-device extension query providers**, with 110 implemented structures and 104 validated query groups. Two additional registry-queryable providers remain explicitly identified as pending native query coverage.
- Expanded offline reference metadata to **479 extensions, 842 commands, 6,274 tokens and 2,467 Vulkan types**.
- Preserved explicit runtime evidence for device/instance extensions, features, properties, limits, queue families, memory, formats, WSI/Surface, Vulkan Video and profiles; registry presence does not establish runtime support.

## Analysis and local tools

- Kept Analysis as a separate workspace for search, comparisons, minimums, diagnostics, dependencies, presentation, local history, watch lists, sharing, raw structured report inspection and optional self-tests.
- Replaced the previous simplistic diagnostics percentage with a transparent, **five-component local evidence score**: Vulkan API version 30%, positive device extensions 25%, reported feature bits 20%, collection integrity 20% and surface presentation 5%. Missing evidence is excluded, and insufficient coverage yields an unavailable score.
- Added an offline **On this device** Encyclopedia filter driven by positive current-device evidence; results are filtered before paging.
- Retained explicit System/Turnip evidence comparison and bounded local snapshot/profile file workflows. Optional self-tests remain distinct from Vulkan CTS conformance testing.

## Reports, sharing and Database

- Brought complete-report **TXT, self-contained HTML and schema-v3 JSON exports together** under Reports & Database. Analysis comparison snapshots and custom minimums keep their own separate JSON formats.
- Improved the icon-free Database status panel for Ready, Unavailable, Uploading, Success and Failure conditions.
- Standardized dark action tiles and retained a dedicated right-side command control instead of treating every descriptive card as a button.
- Integrated companion **VulkanScope Database 4.0.6** submission compatibility: **only VulkanScope 4.0.15 (`versionCode 4015`)** can submit new reports to the matching deployed Worker. Previously stored reports remain readable.
- Kept report uploading explicit and user-initiated, with complete-data submission, bounded transport and no background hardware upload.

## Navigation and Material 3 interface

- Standardized compact dark cards for Settings, Encyclopedia, Extensions, formats, features and Surface evidence with legible labels and consistent action affordances.
- Renamed the driver settings destination **Drivers & Preferences** and placed bar visibility controls below Opening animation. A default-enabled master auto-hide switch and independent top/bottom controls hide app-owned bars after **three seconds of inactivity**.
- Retained Android system bars and transient network/update notifications independently from app chrome.
- Reworked the collection pager as a real inline control that hands off to the fixed pager through a **280 ms animated attach/detach**, retaining interactive arrow and page-input controls across the transition. The full-width and in-card frosted blur effects independently fade over **320 ms** as the app header hides or reappears.
- Improved portrait/landscape responsiveness with ordered two-column result layouts, full-width structural headers/pagers, and **shared centered Settings/Surface destination grids** with a centered odd final card.
- Unified button contrast and inactive states; removed the decorative icon from the **Trademarks and artwork** About heading while preserving written ownership notices.
- Kept high-contrast Markdown in release notes and licenses and English-only user/developer documentation.

## Source architecture and distribution

- Split large application logic into responsibility-based Kotlin files for UI, navigation, pager lifecycle, adaptive layout, Analysis scoring, Encyclopedia device evidence, shared cards and report export/transport.
- Retained the native Vulkan collector, isolated background query scheduling, locked registry generation and security boundaries.
- Updated source build configuration to Android **compile SDK 37 (minor API 2), target SDK 37, minimum SDK 31**, AGP **9.4.1**, Kotlin **2.4.21**, Gradle **9.8.1** and Vulkan-Headers **v1.4.366**.
- Limited release APKs to **ARM64, x86_64 and universal**. 32-bit ARMv7/x86 APK support is not included.
- Aligned release artifact names to `vulkanscope-4.0.15-<abi>.apk`, including the `universal` package.

## Documentation and credits

- Reorganized the public README into navigable documentation categories and retained the project's existing screenshot URLs and artwork.
- Reconciled architecture, build, registry, report, privacy, design, QA and trademark documents with the final 4.0.15 source layout.
- VulkanScope remains an independent project; **Vulkan®**, **SPIR-V™** and Khronos branding belong to their respective trademark holders.

## Verification scope

This summary reflects the checked-in final source behavior and documented tests. Source-level quality checks do **not** establish that every visual animation was verified on a physical Android device; complete Android release builds and device acceptance remain separate validation steps. No transient compile failures or temporary intermediate designs are included in these release notes.
