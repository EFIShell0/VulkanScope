# 0.32.4

- Verified Vulkan 1.4.360 query export parity across TXT, HTML and structured database submission.
- Added canonical VkComponentTypeKHR names alongside raw cooperative-matrix maintenance1 component-type values.
- Shortened only the explicit up-to-date update banner from approximately 10 seconds to approximately 8 seconds.
- Re-audited the GitHub update flow and preserved official-URL, APK identity, signing-certificate and monotonic-version validation.

## 0.32.3
- Corrected `VK_MEMORY_HEAP_TILE_MEMORY_BIT_QCOM` to the Vulkan-Headers 1.4.360 value `0x8`.
- Added canonical Surface transform, composite-alpha and image-usage flag reporting while preserving raw values.
- Kept Database `technicalReport.schemaVersion` at 3 and added exact unsigned-decimal/canonical fields additively for backward compatibility.
- Validated Vulkan query coverage through Vulkan 1.4.360.
- Added automatic VK_EXT_image_tiling_control feature probing.
- Added automatic VK_EXT_cooperative_matrix_maintenance1 feature probing and bounded vkGetPhysicalDeviceCooperativeMatrixProperties2EXT reporting.
- Corrected application canonical Vulkan flag names and VkFormatFeatureFlags2 bit values.
- Added exact unsigned-decimal structured fields for 64-bit Vulkan masks without removing legacy fields.

# 0.32.2

- Fixed Android API 37 Kotlin compilation failure caused by nullable `Display.hdrCapabilities`.
- HDR capability absence is now represented without guessing: HDR types fall back to an empty reported list where the legacy API requires `HdrCapabilities`, and luminance fields report `Not exposed`.
- Avoided `!!`/forced dereference so displays that legitimately expose no `HdrCapabilities` cannot crash this report path.
- Preserved the 0.32.1 Info/Settings-only Material 3 Expressive visual scope and all existing Vulkan/report/export/database/security behavior.

# 0.32.1

- Rebased the 0.32.0 engineering/security/export/database fixes on the original 0.31.1 visual baseline.
- Restricted all new Material 3 Expressive presentation changes to the Info and Settings tabs opened from the top-bar info and gear icons.
- Redesigned Info developer/application identity and version metadata with expressive shape hierarchy while preserving all other page styling.
- Redesigned Settings actions, including TXT/HTML export, update, Turnip import and Database submission, as larger icon-bearing expressive action surfaces with clearer hierarchy and touch targets.
- Preserved every 0.32.0 Vulkan, Android API 37, NDK r29, Vulkan-Headers 1.4.360, Turnip hardening, structured Database report, export parity and linker-hardening change.
- No visual changes were made to Home, Properties, Features, Memory, Queues, Formats, Surface, Extensions, Profiles, navigation, global SectionCard styling, or data rows.

# 0.32.0

- Updated compileSdk/targetSdk to 37 and NDK to stable r29.
- Compiles native Vulkan code against official Vulkan-Headers v1.4.360 while keeping the independently validated generated query catalog explicitly labeled Vulkan 1.4.357 / VulkanCapsViewer 4.12.
- Hardened Turnip runtime ICD selection to validated meta.json declarations confined to private bundle storage.
- Added structured lossless technicalReport data to Database submissions without breaking the deployed schema-v2 envelope.
- Matched the app submission cap to the deployed Database 2 MiB transport limit and never truncates a complete report.
- Expanded TXT/HTML export parity for display modes, layer extensions, Profiles, queue video operations, surface diagnostics and presentation queues.
- Corrected HTML Available/Unavailable status colors.
- Redesigned Settings/Info actions as consistent icon-bearing premium action cards.
- Added explicit native RELRO/now linker hardening.
- Restored the missing Gradle wrapper launch scripts.

# 0.31.1

- Made database/export collection exhaustive for all applicable VulkanCapsViewer 4.12 physical-device extension feature/property mappings.
- Added automatic runtime extension-to-query scheduling for 299 validated CapsViewer 4.12 mappings.
- Serialized isolated background queries so waiting work cannot expire in the single-worker probe queue.
- Preserved unavailable/not-applicable query outcomes in the same complete technical report.
- Re-audited vendor-specific coverage and documented the only five CapsViewer extension-name exclusions: three instance capability dependencies plus OHOS/QNX platform-only entries.

# VulkanScope 0.31.0

- Expanded VulkanCapsViewer 4.12 feature/property parity and extension query coverage.
- Added missing validated background extension query groups without changing runtime extension support semantics.
- Corrected `VK_EXT_inline_uniform_block` gating.
- Preserved Vulkan 1.4.357, Surface/HDR, Turnip, database submission, update-security and ABI behavior.

# VulkanScope 0.30.6

- Removed the user-editable VulkanScope Database API endpoint field; complete-report submission now always uses the fixed official HTTPS Worker endpoint.
- Removed the obsolete database-endpoint SharedPreferences path so stale or user-modified endpoints cannot redirect technical reports.
- Disabled database submission while a Vulkan collection pass is active to avoid knowingly submitting an in-progress report snapshot as complete.
- Preserved explicit user initiation, full-report submission semantics, sensitive-identifier exclusions, HTTPS-only transport, IPv6-first fast fallback, and all existing Vulkan query/report coverage.
- Added bounded in-memory response parsing for GitHub update metadata and VulkanScope Database responses to prevent unbounded response-body allocation.
- Re-audited update download identity/signing/version checks, FileProvider confinement, cleartext blocking, coroutine/lifecycle cancellation, Surface synchronization, native ABI targets, release shrinking, and registry baseline invariants.
- Bumped versionName to 0.30.6 and versionCode to 306.

# VulkanScope 0.30.5

- Fixed the release Kotlin compilation failure caused by the manual update callback not being propagated through `PageContent`.
- Preserved the startup and Settings update-check flows while keeping the update banner non-modal.
- Set the live VulkanScope Database Worker root as the default API endpoint while preserving the editable HTTPS endpoint field.
- Hardened database endpoint validation so submissions accept an HTTPS API root only and cannot accidentally append `/v1/reports` to an existing path.
- Preserved all Vulkan query, report, Surface, Turnip, ABI, export and database-submission coverage.
- Bumped versionName to 0.30.5 and versionCode to 305.

# VulkanScope 0.22.7

- Added the missing `VK_KHR_ray_query` isolated feature-query group without removing or changing existing Vulkan query coverage.
- User-facing TXT, HTML and in-app detailed property labels no longer expose the internal `CapsViewer 4.12 parity` prefix; parity coverage remains intact internally.
- Styled the HTML GitHub link to match the report visual system and avoid the browser-default link appearance.
- Preserved the 0.22.5 horizontal logo, application/device ABI metadata, developer metadata and all existing functionality.
- Bumped versionName to 0.22.7 and versionCode to 150.

# VulkanScope 0.22.5

- Replaced the top-left `VulkanScope` text title with a horizontal lockup made directly from the existing application logo artwork.
- Preserved the existing Vulkan wordmark and SCOPE lettering geometry; the original horizontal divider is rotated vertically between them.
- Sized the horizontal logo to fit the existing Material 3 top app bar without changing page-title behavior or navigation.
- Preserved all existing functionality, query coverage, SAF/download behavior, Android TV D-pad support and report/export behavior.
- Added the approved horizontal VulkanScope logo to standalone HTML exports by embedding the same app artwork used in the top app bar.
- Added separate application ABI and supported device ABI fields to TXT and HTML exports; the application ABI is derived from the installed native library directory.
- Added developer metadata to TXT and HTML exports: Semih Boran, EFI Shell, and GitHub account `https://github.com/EFIShell0`.
- Kept the existing report data model and Vulkan query/report coverage unchanged.
- Bumped versionName to 0.22.5 and versionCode to 148.

# 0.22.0

- Fixed device-extension enumeration state handling so an enumeration failure is no longer serialized as an empty extension list.
- Distinguishes `available`, `unavailable`, and `incomplete` device-extension enumeration states.
- Preserves the exact runtime extension names and reported `specVersion` values when enumeration succeeds.
- Records the native Vulkan result/reason when device-extension enumeration cannot be completed.
- Updated TXT and HTML reports to expose device-extension enumeration status and reason separately from the extension list.
- Prevented the Extensions screen from treating a failed device-extension enumeration as evidence that registry-listed extensions are not enumerated.
- Isolated extension-specific queries no longer treat an unavailable/incomplete extension enumeration as proof that an extension is unsupported.
- Preserved the existing Vulkan query coverage, SAF export behavior, Android TV D-pad support, ABI support, and all existing application features.
- Bumped versionName to 0.22.0 and versionCode to 143.

# 0.21.14

- Added deterministic core-coverage validation after base Vulkan probe parsing. Incomplete base datasets are rejected and the base probe is retried once before exposing the report.
- Core coverage validation requires the Vulkan 1.0 feature set, queue families, memory heaps/types, and limits to be present before a base report is accepted. Device extension count is preserved as runtime data and is not treated as an unconditional non-zero requirement.

# 0.21.12

- Fixed base-report completion so core device extensions, Vulkan 1.0 features, limits, queues, and memory are finalized before the base result is returned.
- Base collection no longer performs or waits for Android Surface/WSI work; surface inspection remains in the dedicated surface probe.
- Preserved existing Vulkan query coverage and report fields while preventing early checkpoints from being mistaken for complete base results.

# 0.21.11

- Fixed the native release build failure caused by an unused `coreExtendedQueriesDeferred` variable under `-Werror`.
- Preserved the Vulkan query/reporting changes from 0.21.10 without changing query coverage.
- Corrected release metadata to versionCode 138.

# 0.21.10

- Hardened isolated Vulkan probe lifecycle: a timed-out or oversized probe result now attempts to stop and terminate only the dedicated `:vulkan_probe` process, preventing a stuck native call from lingering indefinitely.
- Added a 64 MiB safety limit for a single native probe result before it is parsed into memory.
- Moved base format enumeration out of the blocking base probe. The base report becomes ready before optional format enrichment, while the existing `format2` query path now populates the Formats model without reducing format coverage.
- Preserved the full 0.21.6/0.21.7 Vulkan query catalog and generated coverage; no query group was removed.
- Deduplicated extension-query feature entries by canonical feature name while preserving distinct property values.
- Normalized HTML status classification so `NOT_APPLICABLE` is rendered as a distinct not-applicable state.
- Updated stable AndroidX dependencies to Activity 1.13.0, Core KTX 1.17.0 and Lifecycle 2.9.4; Compose UI/Foundation/Animation remain on stable 1.11.4 and Material 3 on stable 1.4.0.
- Removed a remaining production source-code comment to comply with PROJECT_RULES.md.
- Bumped versionName to 0.21.10 and versionCode 137.

## 0.21.7

- Corrected the base format collection so only validated Vulkan format enums from the checked-in canonical format catalog are queried. No property or format coverage group was removed.
- Preserved the complete 0.21.6 query coverage while separating parameterized image-format results by tiling and external-handle type in reports.
- Corrected `VK_COLOR_SPACE_SRGB_NONLINEAR_KHR` classification to canonical sRGB instead of reporting it as unknown.
- Corrected HTML instance-extension rendering so runtime extension names are rendered as exact extension strings rather than escaped HTML markup.
- Added explicit queue video decode, video encode, data graph and unknown flag reporting while preserving the raw queue flag mask.
- Distinguished detailed-property query-result count from unique property-name count in the Properties view and exported reports.
- Bumped versionName to 0.21.7 and versionCode to 134.

## 0.21.6

- Rebased the release on VulkanScope 0.20.7 query and report coverage; no Vulkan query group, property group or report path was removed or consolidated.
- Replaced the collection status banner with English text: "Collecting information…", "VulkanScope is collecting Vulkan information in the background.", "Completed", and "Vulkan information updated."
- TXT and HTML export now uses Android's standard CreateDocument SAF flow on non-TV devices and falls back directly to the public Downloads location when the document picker is unavailable.
- Android TV saves TXT and HTML exports directly into the Android Downloads location.
- Android 10+ uses MediaStore Downloads with Environment.DIRECTORY_DOWNLOADS; older supported Android versions use the public Downloads directory after requesting the required legacy storage permission.
- Added Android TV D-pad focus grouping, initial focus and bring-into-view behavior for the navigation rail while preserving existing touch and mouse interaction.
- Bumped versionName to 0.21.6 and versionCode to 133.

## 0.20.7

- Fixed repeated Vulkan query-status entries accumulating in Properties & Limits after Android Surface recreation.
- Deduplicated merged property records while preserving distinct Vulkan values and the full query coverage.
- TXT and HTML exports now include the detected GPU name in the report and use a GPU-aware filename.
- Added success/failure toast feedback after TXT and HTML export attempts.

# VulkanScope Changelog

## 0.20.6 — 2026-08-16

- Fixed the full initial collection-session lifecycle: the completion banner is no longer allowed to finish when the base report and enrichment finish while background query groups are still running.
- Background core, advanced and runtime extension query jobs are now structurally awaited as part of the same collection session, so the session remains active until every scheduled group has completed or been resolved as not applicable.
- Removed the premature generation-wide pending-task cleanup that was clearing unfinished background work immediately after scheduling it.
- Preserved the existing green completion check, 2-second completion window and soft fade/shrink exit animation.
- Preserved ad-hoc page queries, surface refreshes, Turnip/custom-driver paths, all Vulkan query groups, extension-specific feature/property coverage, TXT/HTML reporting and all existing UI sections.
- No Vulkan feature, property, limit, extension, format, queue, memory, surface or report path was removed.
- Bumped versionName to 0.20.6 and versionCode to 123.

## 0.20.5 — 2026-08-16

- Reworked collection completion tracking around explicit pending task identifiers and collection generations.
- Fixed VulkanProbeService multi-intent shutdown so queued background Vulkan query requests are not terminated by an earlier start request. Each service request now uses its own startId with stopSelfResult, allowing the final queued query to keep the service alive until all scheduled probes finish.
- The collection banner now remains active until the complete initial native report, enrichment, and every scheduled background Vulkan query group has actually finished or been resolved as not applicable.
- Ad-hoc page queries and surface refreshes participate in the same completion state without prematurely hiding the banner.
- Completion is shown as a green check for 2 seconds, then exits with the existing soft animation.
- No Vulkan query group, extension, property, feature, limit, format, queue, memory, surface, or report path was removed.
- Version 0.20.5 / versionCode 122.

## 0.20.4 — Complete collection-session status

- Collection status is now tracked by the number of active Vulkan information jobs rather than only by the initial base report.
- The status remains active while core 1.1–1.4, advanced, extension-specific, metadata, surface, and other scheduled background Vulkan queries are still running.
- Completion is published only after the active query-job count reaches zero, preventing the status from disappearing while Properties & Limits or other sections are still being populated.
- A newly scheduled lazy query re-opens the same collection status instead of being treated as already complete.
- “Tamamlandı” remains visible for 2 seconds, then leaves with the existing soft fade/shrink transition.
- No existing Vulkan query group, feature, property, extension, export format, or UI section was removed.
- Bumped versionName to 0.20.4 and versionCode to 121.

## 0.20.3 — Unified information collection status

- Collection status now covers the full Vulkan information gathering lifecycle instead of only the initial Overview/base report.
- Core, advanced and runtime extension-specific background query groups are scheduled as part of the initial inspection so later Properties, Features, Formats, Memory, Queues and Extensions data is represented by the same collection state.
- Unsupported optional extension query groups are treated as completed without being reported as supported.
- The collection banner remains visible while background information is still being collected, regardless of the currently selected page.
- Completion is shown with a green checkmark and a soft fade/shrink transition before the banner disappears.
- No existing Vulkan query coverage or UI section was removed.

# 0.20.2

- Fixed the Vulkan `VK_EXT_transform_feedback` feature query to match the canonical `VkPhysicalDeviceTransformFeedbackFeaturesEXT` structure: `transformFeedback` and `geometryStreams` are queried and reported, with no invented `geometryStreamDecoration` member.
- Preserved all existing extension-specific feature/property, core Vulkan, surface, format, video, profile, TXT and HTML reporting paths.
- Removed obsolete standalone changelog files from older releases; release history remains in this canonical changelog.
- Bumped versionName to 0.20.2 and versionCode to 120.

# 0.20.1

- Fixed Android native compilation of the CapsViewer parity layer against the canonical Vulkan-Headers 1.4.357 baseline.
- Removed duplicate promoted alias switch cases without reducing runtime capability coverage.
- Excluded non-Android QNX and OHOS structure paths from the Android compilation unit; these remain unavailable rather than guessed.
- Preserved all existing Vulkan core, extension, surface, format, feature, property, TXT and HTML reporting paths.
- Hardened release verification to reject invalid platform-specific parity types and duplicate VkStructureType alias cases.

# 0.20.0

## Reporting and UI
- Extension-specific feature and property data from the expanded CapsViewer-parity query layer remains surfaced through the existing Features and Properties views once the corresponding runtime query completes.
- TXT export continues to include all currently collected runtime features and detailed properties, including newly added extension-specific fields.
- HTML export was redesigned with a responsive dark report layout, clearer section hierarchy, runtime extension details, and colored status badges for supported, not supported, unavailable, unknown, and not-applicable states.
- Device extension scope and specVersion are included in HTML export without lossy extension-name normalization.
- While base inspection and lazy detail queries are still running, the Material 3 Expressive UI shows a compact "Bilgiler alınıyor…" status with progress indication. Completion briefly shows "Tamamlandı" before the status disappears.
- Surface refresh and page-triggered lazy Vulkan queries also use the same non-blocking status treatment without blocking the UI thread.

0.19.10
- Added CapsViewer 4.12 vendor-specific feature/property parity coverage for every physical-device structure referenced by its dedicated extension readers that was not already covered by VulkanScope's validated 1.4.357 registry/core query paths.
- Added 259 additional parity physical-device structures across EXT, KHR, NV, AMD/AMDX, ARM, QCOM, HUAWEI, VALVE, ANDROID, MESA, INTEL, IMG, MSFT, NVX, SEC, OHOS and QNX families where applicable to the 4.12 source reference.
- Added field-level serialization for all newly covered parity structures; unsupported pointer fields remain explicitly unavailable and non-scalar/nested values use a raw-byte representation rather than guessed semantics.
- Preserved Vulkan 1.1-1.4 core ownership for promoted capabilities so extension aliases are not double-counted.
- Preserved exact runtime extension enumeration, extension specVersion, lazy optional query architecture, Turnip/custom-driver support, Surface/HDR/format/video/profile paths, and no-feature-removal policy.
- No database changes.
- Bumped versionName to 0.19.10 and versionCode to 117.

0.19.9
- Expanded validated extension-specific physical-device feature/property querying for major CapsViewer 4.12 parity gaps: descriptor buffer, acceleration structures, ray tracing pipeline/query, mesh shader, graphics pipeline library, shader object, host image copy, extended dynamic state, fragment shader barycentric, fragment shading rate, transform feedback, vertex attribute divisor, inline uniform block, private data and synchronization2.
- Preserved exact runtime extension enumeration, extension specVersion values, Vulkan 1.1-1.4 core queries, Surface/HDR/format/video/profile paths, Turnip/custom-driver support, and lazy probe architecture.
- Added isolated parity query groups without making registry-only extension names appear runtime-supported.
- Increased validated runtime query groups from 57 to 74 and tracked 107 physical-device structures in the checked-in coverage catalog.
- No database changes and no existing capability/query group was removed.

0.19.8
- Fixed duplicate Jetpack Compose lazy-list keys in the Formats, Features, and Physical-device Properties screens. Duplicate Vulkan property names are now safely keyed without dropping or merging entries.

# VulkanScope 0.19.7

- Fixed Turnip eligibility state: it is now derived from the actual base Vulkan device report instead of remaining permanently `UNKNOWN`.
- Turnip eligibility requires Android 9+, `arm64-v8a`, Qualcomm vendor ID `0x5143`, and a runtime-reported Adreno device name; the driver bundle remains a separate installation requirement.
- Preserved the existing rootless AdrenoTools/Turnip loading path and all Vulkan inspection features.
- Bumped versionName to 0.19.7 and versionCode to 114.

# VulkanScope 0.19.5

- Restored the Vulkan 0.15.5 base-probe architecture: the base inventory no longer creates or queries an Android WSI surface.
- Base inspection publishes `baseReportComplete=true` immediately after physical-device properties/features/memory/queues/extensions/formats are collected.
- Registry metadata and instance-layer metadata remain intact through the isolated `metadata` query.
- Surface capabilities, formats, present modes, colorspaces, and presentation support remain intact through the isolated surface query.
- Core 1.1–1.4 and advanced/extension queries remain isolated/lazy; no feature was removed.
- Prevents optional WSI/metadata operations from blocking the main inspection state.
- Bumped versionName to 0.19.5.

# 0.19.5
- Core 1.4 is now fetched only when the Core 1.4 section is actually opened; it is no longer part of the Features/Properties page startup workload. All Core 1.4 fields and the existing native query implementation remain present.
- Preserved Core 1.1/1.2/1.3 queries on the normal inspection pages and kept every advanced/extension query group intact.
- SurfaceView API 34+ now uses the attachment-bound lifecycle, and a recreated Android Surface triggers only the isolated surface-capability probe instead of restarting the full Vulkan device inspection.
- Bumped versionName to 0.19.5 and versionCode to 112.

# 0.19.3
- Separated Android Surface lifecycle refresh from full Vulkan device collection. A recreated Surface now triggers only the isolated Surface probe instead of restarting the entire Vulkan inspection.
- Prevents SurfaceView lifecycle churn from keeping the UI in an endless initial inspection loop while preserving all surface capability/HDR/present-mode queries.
- Bumped versionName to 0.19.3 and versionCode 111.

# 0.19.5
- Removed eager startup execution of Vulkan 1.1/1.2/1.3/1.4 core probes. The full core query paths remain intact and are now executed only when the Features or Properties inspection page needs them.
- Prevents a vendor-specific Core 1.4 query (including deep property arrays) from being part of the initial inspection lifecycle; an isolated query failure now cannot block the base Vulkan report.
- Kept all existing advanced and extension query groups; no Vulkan capability/query implementation was removed.
- Bumped versionName to 0.19.5 and versionCode to 110.

# 0.19.1

- Removed eager startup execution of advanced image, format, memory, sparse, queue, video and extension-detail probes. Those query paths remain implemented and are now loaded lazily from the corresponding inspection pages so a vendor-specific advanced query cannot destabilize the initial Vulkan report.
- Preserved Vulkan 1.1-1.4 core queries, Surface/HDR probing, runtime extension enumeration, extension specVersion data and all registry-driven query groups.
- Added a per-session lazy-query circuit breaker so a crashing optional Vulkan query is not automatically retried in a loop.
- Reset lazy-query state when the active driver mode changes.
- Bumped versionName to 0.19.1 and versionCode to 109.

# 0.19.0

- Restored the full base Vulkan report path after the format-query checkpoint; the collector no longer returns a partial report before surface and instance metadata collection.
- Preserved the validated baseReportReady checkpoint handoff so the UI can render the complete base device, feature, limit, queue, memory and format data without waiting on later optional metadata work.
- Preserved all runtime extension, layer, surface, versioned-feature and generated-registry query paths.

0.18.29 - Fixed the base probe stall after device collection by publishing a complete device report before registry/layer metadata; moved registry coverage and instance-layer extension enumeration into a separately isolated metadata probe so no base collection can remain stuck there. Preserved all existing queries and CapsViewer-parity fields.
0.18.28 - Fixed atomic native checkpoint publication and invalid intermediate JSON acceptance; repaired the base memory/type checkpoint closure without removing any query coverage.
0.18.27 - Added progressive base checkpoints before and after device extension, feature, limit and memory/queue stages; moved Vulkan 1.1-1.4 core feature/property collection to isolated validated probes without removing any query coverage.

0.18.26 - Reworked base collection so legacy Vulkan 1.0 properties, features, limits, queues, memory and runtime extensions are checkpointed before vendor-sensitive extended queries. Vulkan 1.1-1.4 feature/property queries remain intact and are now merged through isolated validated core probes. Added progressive format-safe checkpoints and preserved CapsViewer-style collection order without removing any feature/query coverage.

0.18.20 - Qualcomm-safe base discovery: Vulkan 1.1 core discovery instance with no optional instance extensions; physical-device enumeration result logging; no query/UI feature removal.

- 0.18.19: Hardened base Vulkan instance creation against loader/device API mismatch by capping the initial discovery instance at Vulkan 1.3, added runtime-gated VK_KHR_get_physical_device_properties2 enabling, fixed duplicated API version serialization, merged core14 feature/property results without removing any query coverage, and stopped futile enrichment when base discovery yields no devices.
- 0.18.18: Fixed layer-extension helper declaration order and added VK_INCOMPLETE-safe device-layer enumeration retry; no feature/query coverage removed.
# 0.18.14

- Restored full Surface, advanced, and extension enrichment after base probe.
- Base Vulkan collection is again Surface-independent; WSI failure cannot erase core device data.
- All enrichment probes remain strictly sequential behind the process-wide native probe lock.
- Preserved Surface2, extension-gated format queries, generated feature/property coverage, and Qualcomm probe isolation.

# 0.18.13

- Removed eager startup execution of isolated advanced and extension Vulkan probes so initial collection uses one base Vulkan instance plus the real Android Surface probe.
- Serialized all native Vulkan probe invocations process-wide to prevent overlapping driver initialization and global crash-guard state races.
- Increased base and Surface probe timeouts to 45 seconds and 35 seconds respectively.

0.18.12

- Fixed native release compilation against canonical Vulkan-Headers 1.4.357 after extension coverage generation introduced promoted and provisional symbol mismatches.
- Enabled the canonical provisional Vulkan header set for beta-only extension structures while keeping runtime support gated by exact extension enumeration.
- Corrected Provoking Vertex, Map Memory Placed, Portability Subset and YCbCr 2-plane 4:4:4 structure identifiers to their canonical Vulkan 1.4.357 names.
- Moved the extension helper declaration before its first use and made coverage generation refuse non-1.4.357 headers.

0.18.10

- Expanded runtime extension feature/property collection toward CapsViewer 4.12 parity using single Features2/Properties2 pNext chains.
- Added conditional extension-family format queries for PVRTC, ASTC HDR, YCbCr, ASTC 3D, YCbCr 2-plane 4:4:4, 4444, ARM tensor/format-pack, and Vulkan 1.4 core formats.
- Preserved RULES distinction between unsupported and unavailable data and avoided unrelated extension-format Vulkan calls.

# VulkanScope 0.18.9

- Unified Android Surface querying so `VK_KHR_get_surface_capabilities2` uses the Surface2 capability and format query paths whenever the implementation exposes the extension.
- Reduced base format-property probing to the canonical Vulkan core format range and removed extension-format brute-force queries that generated noisy unsupported-format driver logs.
- Preserved exact Vulkan surface format and color-space reporting, safety caps and legacy Surface queries only when Surface2 is unavailable.
- Updated release metadata to versionName 0.18.9 / versionCode 86.

## 0.18.8 — Probe serialization and Adreno stability hardening

- VulkanProbeService now serializes all native Vulkan requests through one worker thread instead of starting an independent thread for every startService request.
- This prevents overlapping Vulkan instance creation and physical-device enumeration inside the same :vulkan_probe process.
- Surface, advanced and extension query contracts remain unchanged, preserving the existing report model while eliminating the concurrent-probe race.
- Preserved exact Surface/View lifecycle behavior and isolated-query failure semantics.

## 0.18.7
- Restored real Android `Surface` -> `VkSurfaceKHR` WSI probing through a separate isolated probe path.
- Base physical-device enumeration no longer depends on WSI instance extensions.
- Surface capabilities, exact format/color-space pairs, present modes and queue presentation support are merged into the base report when the isolated Surface probe succeeds.
- Surface probe failures remain isolated and are reported as unavailable instead of discarding the core Vulkan report.

# 0.18.6

- Isolated base physical-device enumeration from surface and optional instance-extension enablement.
- Removed optional instance extension enablement from the isolated advanced-query path to reduce vendor-driver crash surface.
- Improved native signal diagnostics for physical-device enumeration termination.

# VulkanScope 0.18.4

- Fixed the native release build failure in the extended Vulkan feature probe caused by an unused `VkApplicationInfo` local under `-Werror`.
- The extended feature probe continues to use the centralized `createInstanceCompatible` path, so loader/API version negotiation remains single-sourced and consistent with the rest of the native collector.
- Incremented versionCode to 81 and versionName to 0.18.4.

# VulkanScope 0.18.3

- Hardened Vulkan instance creation with spec-aware API-version fallback across Vulkan 1.4 through 1.0 when the active loader/driver pair rejects a requested instance version.
- Made device-layer enumeration optional so an unavailable optional layer entry point no longer invalidates the base physical-device report.
- Added signal-safe native probe-stage diagnostics for isolated failures.
- Preserved separate loader, instance and physical-device Vulkan API version reporting.
- Improved the Overview state when the Vulkan inspection itself fails so it is not presented as a missing GPU.
- Incremented versionCode to 80 and versionName to 0.18.3.

# VulkanScope 0.18.1

- Preserved instance/device extension entries independently so each runtime-reported scope and `specVersion` remains exact.
- Hardened advanced native queries so missing optional Vulkan entry points resolve to `Unavailable` instead of invoking a null function pointer.
- Hardened probe result paths to remain inside the app cache directory.
- Switched probe service completion to `stopSelfResult(startId)` for safe handling of multiple start requests.
- Removed non-canonical placeholder physical-device struct names from the generated registry coverage manifest.
- Improved release verification for the updated metadata and query safety paths.

# VulkanScope 0.18.0

- Reworked Features presentation with Core 1.0-1.4 provenance and runtime support filters.
- Expanded Memory, Queue and Format presentation with canonical flag names and explicit unknown-bit reporting.
- Preserved the distinction between Vulkan surface color-space data and Android display HDR data.
- Kept advanced native query results visible through the detailed-properties path with explicit availability states.
- Incremented versionCode to 77 and versionName to 0.18.0.

# VulkanScope 0.17.12

- Fixed Vulkan 1.1+ physical-device query availability by enabling `VK_KHR_get_physical_device_properties2` whenever advertised, including stacks whose loader reports Vulkan 1.1 or newer.
- Added a fallback lookup for `vkGetPhysicalDeviceToolPropertiesEXT` when the Vulkan 1.3 core entry point is not exposed.
- Improved query-status diagnostics so missing `Properties2`/`Features2` entry points and the advertised KHR dependency are distinguishable.

# 0.17.10

- Removed unused native helpers that failed release builds under `-Werror`.
- Kept canonical registry-driven enum/flag formatting in the field generator without retaining unused runtime helpers.
- Removed obsolete raw-reader helper and unused generated array/float helpers.

# VulkanScope 0.17.7

- Fixed canonical Vulkan type usage in the core property, format, surface-present, and Vulkan Video collectors.
- Fixed Vulkan Video profile/capability construction to use the exact enum, bit, structure, and StdVideo types required by Vulkan-Headers 1.4.357.
- Fixed pipeline cache UUID access to use the canonical `VkPhysicalDeviceProperties::pipelineCacheUUID` field.
- Fixed present-mode and format enumeration buffer element types.
- Fixed generated OCP microscaling and ASTC 3D structure/type names.
- Preserved the rules.md safety, ABI, canonical-header, and Pythonless-build requirements.

# VulkanScope 0.17.6

- Fixed canonical generated Vulkan field/pNext integration for release builds.
- Fixed generated hex serializer ABI/signature mismatch.
- Fixed canonical Khronos OCP microscaling type/member spelling.
- Repaired generated pNext translation unit wrapper so generated statements are inside their function scope.
- Preserved Pythonless Android build requirements.

# Changelog

## 0.17.4
- Fixed generated native field serializer helper integration (`generatedEmitHexTyped`).
- Fixed versioned Vulkan 1.1/1.2/1.3 feature query code to use canonical Vulkan feature structs instead of a nonexistent generic chain type.
- Fixed a malformed generated numeric helper fragment that broke C++ parsing.
- Preserved Pythonless Android CMake build and canonical Vulkan-Headers 1.4.357 workflow.

# VulkanScope 0.17.3

- Removed the Python interpreter from the Android CMake configure/build dependency chain.
- Checked in canonical Vulkan metadata generated from Vulkan-Headers 1.4.357.
- Made native clean/reconfigure independent of a system Python installation.
- Preserved canonical Vulkan header verification and generated pNext/field coverage.


## 0.17.2

### Reliability, Security and Compatibility
- Hardened Vulkan memory heap/type enumeration against invalid driver-reported counts using canonical Vulkan array limits.
- Added safety caps for queue-family, sparse-image-property and video-format enumerations.
- Published isolated probe results atomically to prevent partial JSON reads and false unavailable states.
- Bounded imported AdrenoTools metadata parsing.
- Removed unused probe state and cleaned the native source tree to maintain the no-comments project rule.
- Validated the project against the current Vulkan 1.4.357 specification and Android display HDR API behavior.
# VulkanScope 0.17.1

## Feature-quality / parity hardening
- Hardened canonical enum and bitmask semantic decoding using Vulkan XML type relationships.
- Preserved unknown enum values and unknown flag bits in raw numeric form.
- Added canonical names for enum/flag arrays instead of numeric-only array output.
- Added decimal + hexadecimal representation for numeric arrays.
- Added a field-level CapsViewer parity audit mode that classifies missing, partial and covered physical-device feature/property fields when a 4.12 source checkout is supplied.
- Added vendor semantic audit metadata and explicit source-file provenance in parity reports.

# VulkanScope 0.17.0

- Canonical enum and bitmask semantic rendering
- Decimal + hexadecimal numeric rendering
- Type-aware raw-byte fallback for unsupported field types
- Stronger generated field coverage verification

# VulkanScope 0.16.9

- Added generated runtime pNext query integration to the native collector so canonical Vulkan feature/property coverage is actually compiled and executed.
- Added a build-time coverage audit path for canonical `vk.xml` physical-device feature/property structures.
- Added release-gate checks that fail when generated pNext code is not wired into the native collector.
- Kept canonical Vulkan-Headers 1.4.357 and Gradle 9.7.0 baselines.

# VulkanScope 0.16.8

- Canonical Vulkan-Headers 1.4.357 driven runtime extension feature/property pNext generation.
- Build-time vk.xml coverage generation for extension-specific and vendor-specific physical-device feature/property structures.
- Runtime queries now chain every validated structure belonging to the selected device extension before serializing results.
- Added release coverage gating for generated runtime pNext query generation.

# VulkanScope 0.16.7

## Complete extension/property field coverage

- Added generated runtime field coverage for every validated physical-device feature/property structure bundled with the 1.4.357 registry baseline.
- Added pNext-chain traversal with cycle and depth guards.
- Added automatic missing-field exposure without replacing exact runtime extension names or feature states.
- Added a build-time field-coverage generator so validated Vulkan metadata cannot silently disappear from the native report.

## 0.16.5
- Added feature search to the Overview Explore -> Features destination.
- Isolated Vulkan query instances no longer enable unrelated optional instance extensions; core and advanced queries use the Vulkan loader/core API directly.
- Query status reporting now distinguishes unavailable query execution from device capability results.

# Changelog

## 0.16.3
- Hardened Vulkan memory enumeration with explicit heap/type safety caps and report status fields.

## 0.16.2
- Updated the Gradle wrapper to Gradle 9.7.0.
- Aligned the project build rules with the Gradle 9.7.x build family.
- Bumped versionCode to 56 and versionName to 0.16.2.

# VulkanScope 0.16.1

- Synchronized Display navigation transition direction with the actual navigation order.
- Cached display capability collection between recompositions and refreshes it on activity resume.
- Added safety limits to physical-device, queue-family, tool, video-format and device-group enumeration.
- Removed an unaligned native property read in extension query reporting.
- Preserved canonical Vulkan and rules-driven status semantics.

# VulkanScope 0.16.0

- Expanded runtime information presentation for instance layer extensions, physical-device group membership, Vulkan tool purpose names, and queue flag names.
- Increased report schema to 4.

# VulkanScope 0.15.5

- Fixed Vulkan 1.4 copy-layout property query scope and bounded second-query allocations.
- Preserved explicit unavailable state when safety caps are exceeded.
- Bumped versionCode to 53 and versionName to 0.15.5.

# VulkanScope 0.15.4

- Fixed Core 1.4 detailed-property population in the shared physical-device property query path.
- Stopped re-running core 1.1–1.4 probes during normal report collection; core feature/property status now comes from the base Vulkan instance.
- Preserved separate unavailable/not-applicable semantics for optional isolated advanced and extension queries.
- Bumped versionCode to 52 and versionName to 0.15.4.

# Changelog

## 0.15.3

- Fixed Kotlin `DeviceReport` construction after surface query metadata expansion.
- Preserved explicit surface-format query safety state in the UI report model.
- Version code increased to 51.

# VulkanScope 0.15.3

## 2026-08-13

- Fixed the native surface-format safety-cap error path so no Vulkan error enum is synthesized when a driver reports an unsafe collection size.
- Surface format queries now distinguish a skipped second query from a real VkResult and preserve unavailable semantics.
- Bumped versionCode to 51 and versionName to 0.15.3.

# VulkanScope 0.15.1

## 2026-08-13

- Reworked Core 1.1–1.4 feature/property collection to use the single base Vulkan instance and physical-device enumeration instead of redundant per-version probe restarts.
- Fixed duplicate instance-layer parsing.
- Added explicit Android UI explanations for empty instance/device layer lists.
- Hardened raw physical-device property copying against unaligned access and bounded Vulkan 1.4 variable-length layout arrays to prevent unsafe allocations.
- Preserved offline, on-device, registry-driven query behavior and all required ABIs.

# VulkanScope 0.15.0

## 2026-08-13

- Fixed release shrinker rules to keep the actual isolated Vulkan probe service native entry points.
- Removed the remaining source-code comments from the native Vulkan header to comply with PROJECT_RULES.md.
- Hardened probe result-file cleanup so failed service starts cannot leave temporary JSON files in the cache.
- Bumped versionCode to 48 and versionName to 0.15.0.
- Re-verified the checked-in Vulkan registry baseline against current Khronos upstream documentation before release preparation.

# VulkanScope 0.14.0

- Added isolated Vulkan Video capability probing through `vkGetPhysicalDeviceVideoCapabilitiesKHR`.
- Added codec-specific decode capability queries for H.264, H.265, VP9 and AV1 when the corresponding runtime extensions are enumerated.
- Added general encode capability probing for H.264, H.265 and AV1, with bitrate, quality-level, rate-control and feedback information.
- Added Vulkan Video format compatibility queries through `vkGetPhysicalDeviceVideoFormatPropertiesKHR` for sampled-image usage, with canonical runtime format names.
- Added Vulkan Video standard-header version, coded extent, DPB/reference limits and bitstream alignment information to detailed properties and TXT/HTML reports.
- Kept each codec profile independent: an unsupported or crashing codec-specific query does not invalidate the other video profiles.
- Updated registry query catalog to schema 5 and 57 validated runtime query groups.
- Added Vulkan Video codec extension names to the runtime extension catalog/filter set.
- Updated release verification to 0.14.0 / versionCode 46.

# VulkanScope 0.13.0

- Added runtime Vulkan Profile evaluation for Android Baseline 2022, Roadmap 2022, Roadmap 2024 and Roadmap 2026.
- Profile results distinguish PASS, FAIL and UNKNOWN; unavailable feature/limit queries are never treated as unsupported.
- Added official Roadmap capability requirements and limit checks for the evaluated profiles.
- Extended Queue Family Properties 2 reporting with Vulkan Video codec-operation capabilities exposed by `VK_KHR_video_queue` through `VkQueueFamilyVideoPropertiesKHR`.
- Queue reports can now show H.264, H.265, AV1 and VP9 decode/encode queue capabilities when the driver exposes them.
- TXT and HTML reports now include per-device profile evaluation and video queue capabilities.
- Preserved the registry-driven query catalog, Vulkan 1.4.357 baseline, isolated probes, Surface/HDR data, Turnip support and all existing ABI targets.

# VulkanScope 0.12.2

- Fixed malformed C++ registry query catalog initializer that broke all native ABIs.
- Added the missing official Vulkan 1.4 core feature structure used by the isolated 1.4 probe.
- Replaced the invalid generic 1.4 feature-chain object with the real `VkPhysicalDeviceVulkan14Features` layout and explicit feature serialization.
- Fixed `VulkanProbeService` worker startup by using Kotlin's supported `thread` helper.
- Updated release verification to validate 0.13.0 / versionCode 45.

# VulkanScope 0.12.1

- Fixed release build failure caused by malformed registry query catalog brace generation.
- Added the actual Vulkan 1.4 core feature structure and removed the invalid generic feature-chain use from the 1.4 probe.
- Fixed VulkanProbeService worker launch to use Kotlin's supported `thread` helper.
- Preserved existing Vulkan 1.0–1.4, registry-driven queries, Turnip, Surface/HDR and report features.

# VulkanScope 0.12.0

- Makes validated device-extension query dispatch consume the generated registry descriptor catalog instead of a second hard-coded extension-name mapping.
- Adds report schema and validated header-baseline metadata to native, TXT and HTML reports.
- Keeps runtime extension enumeration exact and distinguishes registry reference entries from runtime non-support.
- Adds a release verification gate covering version consistency, ABI policy, manifest permissions, registry catalog integrity and report/export presence.
- Keeps the Vulkan 1.4.357 offline registry baseline and the current Khronos registry/specification as the release authority.

# VulkanScope 0.11.0

- Promotes the offline registry catalog to an explicit runtime query-descriptor catalog.
- Adds catalog schema and instance dependency metadata to reports.
- Moves dependency-aware instance-extension candidate selection to the generated native registry catalog.
- Adds Vulkan 1.4 to the validated query-group catalog so coverage reporting matches the existing isolated Vulkan 1.4 probe.
- Fixes the registry generator duplicate argument definition and advances its manifest schema.

# VulkanScope 0.9.0

## Registry-driven query coverage
- Added an offline registry-driven Vulkan query catalog generator based on the Khronos `vk.xml` registry.
- The generator maps physical-device feature/property structs and extension requirements to a machine-readable coverage manifest.
- Generated registry metadata is consumed by the native report through a compiled query-catalog header.
- Added explicit Vulkan 1.4.357 registry baseline and query-engine metadata to native reports.
- Added an inventory path for registry-defined physical-device structs that are not represented by the minimal native header.
- Runtime remains fully offline; the application never downloads registry data.
- Unknown or unreviewed registry structures remain unavailable instead of being queried through guessed structure IDs or fields.

## Preserved
- Vulkan 1.0 through 1.4 core queries.
- Extended format, queue, image, external, sparse, group and tool queries.
- Extension enumeration and runtime specVersion reporting.
- Dependency-aware instance-extension enabling for runtime-enumerated dependencies.
- Turnip/custom-driver support and isolated query failure handling.
- Surface, Android HDR/display and existing TXT/HTML reporting.

## Version
- versionName: 0.9.0
- versionCode: 39

## 0.16.5

Expanded validated Vulkan extension-specific feature coverage and completed all remaining generated feature query groups in the bundled registry catalog.

## 0.18.3
- Hardened Vulkan instance creation with spec-aware API-version fallback across Vulkan 1.4 through 1.0 when the active loader/driver pair rejects a requested instance version.
- Made device-layer enumeration optional so an unavailable optional layer entry point no longer invalidates the base physical-device report.
- Added explicit native probe stages to signal-safe crash reporting so isolated query failures retain their actual failing stage.
- Preserved distinct loader, instance and physical-device Vulkan API version reporting.
- Improved the Overview empty-device state so a failed Vulkan inspection is not presented as an undifferentiated missing GPU.
## 0.18.12

- Fixed Vulkan 1.4.357 generated extension field coverage for VK_NV_cooperative_matrix2 to match the canonical header fields.
- Fixed VK_EXT_ycbcr_2plane_444_formats canonical structure-type spelling in runtime pNext generation.
- Restored canonical VkPresentModeKHR name formatting in surface reporting.
- Fixed advanced format/image/sparse query extension-scope variable lifetime.
- Preserved RULES requirements for canonical Vulkan naming, extension-gated format queries, deterministic probe behavior, and generated metadata validation.


## 0.18.23
- Restored the intact Surface probe and all existing feature/property/versioned query output.
- Hardened only the base probe's prerequisite physical-device queries with stable core entry points; no query group or GUI field was removed.
- Fixed the source corruption introduced in 0.18.22.

## 0.22.7
- Completed `VK_KHR_ray_query` isolated feature-query aliases so all catalog forms resolve to the runtime feature query.
- Removed `CapsViewer 4.12 parity ·` from user-visible feature and property labels in TXT/HTML report parsing while retaining internal parity metadata in source/catalog files.
- Preserved existing Vulkan query coverage and report schemas.
