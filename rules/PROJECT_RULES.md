# VulkanScope Engineering Rules

## Non-negotiable
- Source-code comments are forbidden.
- Security, correctness, memory safety, performance and usability are never traded away for convenience.
- No known security vulnerability may be knowingly shipped.
- No known memory leak may be knowingly shipped.
- No unnecessary network permission or network dependency. Permitted runtime network paths are explicit user-initiated VulkanScope Database submission over HTTPS and a non-blocking startup update check against the official public VulkanScope GitHub release API.
- No guessed hardware capabilities.
- Unknown, unsupported and unavailable are distinct states.
- Vulkan loader version, device API version and driver version are distinct values.
- Vulkan surface color-space support is not equivalent to physical display gamut.
- Official Khronos and Android documentation are the primary sources for API behavior.
- Current technical claims must be verified against current upstream sources before release.
- Native ownership must be deterministic and Vulkan handles must be destroyed in valid dependency order.
- UI-thread blocking native work is forbidden.
- Large collections must be lazy and searchable.
- No unnecessary allocations or repeated Vulkan queries during UI recomposition.
- Runtime network access is forbidden except for an explicit user-initiated complete technical report submission to the fixed official VulkanScope Database HTTPS endpoint and the official VulkanScope update flow. Update checks may run at startup, but APK download and package installation must require an explicit user action.
- Device and display data remain on-device unless the user explicitly presses Submit complete report. Submission must exclude IMEI, Android ID, device serial, MAC addresses, account data, authentication tokens and private file paths.
- Database submission must not offer per-capability omission controls; the technical report is submitted as one complete dataset or not submitted at all.
- No automatic or background report upload is permitted.
- Runtime-enumerated Vulkan extension names must be displayed exactly as returned by the Vulkan implementation.
- Vulkan enum names must not be replaced by marketing names or inferred capabilities.
- A missing API query must be reported as unavailable or unknown, never as unsupported without evidence.

## ABI
- armeabi-v7a required.
- arm64-v8a required.
- x86_64 required.
- x86 intentionally excluded.

## Architecture
- Kotlin and Jetpack Compose for UI.
- Material 3 Expressive visual system.
- C++20 and Vulkan for native collection.
- Native/JNI boundary must remain small.
- Vulkan registry and specification data are authoritative references for naming and semantics.
- Native Vulkan compilation must use canonical Khronos Vulkan-Headers 1.4.357 or newer only when explicitly verified at build time; runtime must never fetch headers or registry data.
- Core Vulkan feature queries and version-promoted feature queries must remain distinguishable.

## Surface and display
- Surface data must come from a real Android Surface and VkSurfaceKHR path.
- Surface formats must be reported as exact format + color-space pairs.
- Color-space names must use canonical Vulkan names such as VK_COLOR_SPACE_DISPLAY_P3_NONLINEAR_EXT and VK_COLOR_SPACE_HDR10_ST2084_EXT.
- Android Display HDR data must remain distinct from Vulkan surface color-space data.
- Wide-color support must not be presented as a measured gamut percentage.
- HDR luminance values are displayed only when Android exposes them.
- Surface lifecycle must be synchronized so a destroyed Surface is never used by native Vulkan code.

## Extensions
- The Extensions screen lists every instance and device extension enumerated at runtime.
- Extension search is case-insensitive.
- Instance and device scope remain visible.
- Extension specVersion is displayed exactly as reported.
- VK_EXT_swapchain_colorspace and all other supported extensions must appear when the implementation enumerates them.
- Extensions that are not exposed by the device must never be labeled supported.

## Features and limits
- Vulkan 1.0 core features are queried through vkGetPhysicalDeviceFeatures.
- Vulkan 1.1, 1.2, 1.3 and 1.4 core feature structures are queried when the device API version exposes them.
- Feature support is not the same as feature enablement.
- Limits are reported from Vulkan properties and are not inferred from GPU model names.

## Formats
- Format properties are queried from Vulkan before being displayed.
- Invalid or invented VkFormat numeric values must never be queried.
- Format names must be canonical Vulkan names.

## Complete report collection
- A complete report must preserve supported, unsupported, unavailable, not-applicable and unknown results in one dataset; status categories must never be used as omission filters for export or database submission.
- Database submission remains disabled until every scheduled core, advanced and applicable extension feature/property query has either completed or produced an explicit unavailable/not-applicable result.
- The bounded submission size limit must be large enough for the exhaustive technical dataset; a report must not be truncated or selectively reduced to satisfy transport limits.
- Every runtime-enumerated device extension that has a validated VulkanCapsViewer 4.12 physical-device feature/property query mapping must be queried automatically; opening the Extensions screen must not be required for report completeness.
- Isolated Vulkan queries sharing the single probe worker must be scheduled sequentially so queued work cannot expire merely because earlier probes are still executing.
- Platform-only physical-device structures for non-Android platforms must not be queried on Android and must be documented as platform exclusions rather than unsupported device capabilities.
- Instance capability extensions without their own device feature/property pNext structure are covered by the appropriate instance or advanced physical-device query path and must not be fabricated as device feature groups.

## Release quality
- Test on all target ABIs.
- Test Vulkan 1.0 through the latest API version exposed by the installed Android Vulkan stack.
- Test missing optional extensions and features.
- Test Surface recreation and lifecycle transitions.
- Test HDR, wide-color, 10-bit and SDR-only displays.
- Test devices with multiple queue families and memory heaps.
- Release builds must enable shrinking and resource optimization.

## Build compatibility
- AGP 9.3.1 is required for the supplied project configuration.
- Gradle 9.7.x is the intended build family.
- AGP 9 built-in Kotlin is used; the deprecated/redundant `org.jetbrains.kotlin.android` plugin is not applied.
- The Compose compiler Gradle plugin remains applied for Compose compilation.
- JDK 17 or newer is required by the Android Gradle Plugin.
- Build-tool version changes must be checked against official Android Gradle Plugin compatibility documentation before being committed.

## Build gate
- Every supplied revision must compile Kotlin and native C++ for armeabi-v7a, arm64-v8a, and x86_64 with warnings treated as errors where configured.
- No unused native helper may remain. Every generated Vulkan metadata field must be consumed by the UI or intentionally exposed through the native report.
- Compose experimental APIs must be explicitly opted into only at the smallest required scope.
- Extension aggregation must preserve exact Vulkan extension names, scope, and specVersion without lossy transformations.
- Registry-driven query coverage is build-time authored from canonical Khronos metadata; the Android CMake build must not require Python or any runtime registry download. Generated native metadata is checked in and verified before release; runtime must never download or parse remote registry data.
- Generated registry metadata is informational unless the corresponding native struct/query path has been explicitly validated; unknown registry structures must remain unavailable rather than being guessed.


## Network address-family policy
- Runtime HTTPS clients must prefer IPv6 when a hostname resolves to both IPv6 and IPv4.
- IPv4 fallback must remain available; IPv6-only behavior is forbidden.
- Dual-stack connection establishment must use fast fallback so broken IPv6 does not impose the full connection timeout before IPv4 is attempted.
- Hostnames must remain hostnames through TLS; replacing HTTPS hosts with resolved IP literals is forbidden.
- IPv6-only and DNS64/NAT64 networks must remain supported by using the platform resolver rather than hard-coded address literals.

## Application updates
- Startup update checks must be asynchronous and must never block Vulkan collection, Surface lifecycle, navigation or first-frame UI.
- Update metadata must come only from the official EFIShell0/VulkanScope GitHub Releases API over HTTPS.
- Only published latest-release APK assets are eligible.
- ABI-specific APK selection must prefer the application's installed ABI; a clearly named universal APK may be used only as a compatible fallback.
- Update availability UI must use the existing VulkanScope banner visual language and remain non-modal. The explicit up-to-date result must leave the interface after approximately 8 seconds; update-available and failure results retain the existing approximately 10-second behavior when no download is in progress.
- APK download and installation require explicit user action. Silent installation is forbidden.
- Downloaded APKs must be exposed to Android's package installer through a non-exported FileProvider with temporary read permission.
- Android unknown-source authorization must be respected; VulkanScope must never attempt to bypass the platform package installer or its user confirmation.
- Info must expose a manual Check for updates action beside the installed application version information so a dismissed startup update result can be requested again without restarting VulkanScope. Settings must not duplicate this action. Manual checks reuse the same update-status/confirmation flow and must not block Vulkan collection or navigation.

- Runtime cleartext HTTP is forbidden; approved network flows must remain HTTPS-only.
- Network metadata/error responses consumed into memory must have explicit size limits; unbounded response-body materialization is forbidden.
- Downloaded update APKs must match the installed VulkanScope package identity and signing certificate and must carry a strictly newer versionCode before Android's installer is launched.
- Update asset filenames must be treated as untrusted input and confined to VulkanScope's private update cache directory.
- The VulkanScope Database endpoint is fixed in the application to the official HTTPS Worker root. User-editable database endpoints are forbidden. The fixed endpoint must be parsed and validated as HTTPS and must not contain user-info, query, fragment or extra path components.
- Database submission must be disabled while a Vulkan collection pass is active so an in-progress snapshot is not knowingly submitted as a complete technical report.

## Current upstream and structured-report baseline
- Release 0.32.3 compiles native Vulkan code against the official Khronos Vulkan-Headers v1.4.360 and verifies `VK_HEADER_VERSION` at CMake configure time.
- The generated exhaustive physical-device query catalog is validated through Vulkan 1.4.360. Vulkan 1.4.358 VK_EXT_image_tiling_control and Vulkan 1.4.359 VK_EXT_cooperative_matrix_maintenance1 are included in automatic runtime extension probing; Vulkan 1.4.360 adds no further extension. A future header update must not be presented as wider validated query coverage until its registry delta is independently audited.
- Unknown structures or fields introduced after the validated generated catalog baseline must not be guessed, synthesized, or silently reported as unsupported.
- Database submission must preserve the complete human-readable report and an additional lossless structured technical-report object when transport permits; the structured object is additive and must not remove legacy schema fields required by the deployed Database worker.
- Client-side database payload limits must match the deployed Database transport limit. If the full payload exceeds that limit, submission must fail locally without truncating, filtering, or reclassifying technical data.
- Turnip runtime loading must use only the validated `meta.json` `libraryName`, reject path separators, require an `.so` filename, and confirm the resolved library remains inside VulkanScope private bundle storage. Arbitrary first-JSON/first-library fallbacks are forbidden.
- TXT and HTML exports must carry the same material technical categories, including display modes, instance and device layer details, profile evaluation, queue video operation data, surface diagnostics, and presentation queue results.
- HTML status colors must preserve semantics: supported is green, available is blue, unsupported is red, unavailable/not-applicable is amber, and unknown is gray.
- Android display HDR capability objects are nullable. A null `Display.hdrCapabilities` must be represented as not exposed/unknown data and must never be force-dereferenced or converted into guessed HDR support/luminance values.


## Info / Settings visual scope
- Release 0.32.2 Material 3 Expressive-inspired visual changes are confined to the contents of the Info and Settings destinations opened from the top-bar info and gear icons.
- Shared navigation, top app bar geometry, SectionCard, DataRow, MetricCard and every Vulkan capability page outside Info/Settings must retain the established pre-0.32.0 visual language unless a later user request explicitly authorizes a broader redesign.
- Info and Settings actions should use clear icon + title + supporting-text hierarchy, generous touch targets and expressive rounded shapes while preserving VulkanScope's dark neutral surfaces and rose/red accent identity.
- Export, update, driver-import and Database actions must remain semantically identical; visual styling must not change report completeness, safety gates or submission behavior.

- 64-bit Vulkan bitmasks in structured Database payloads must include an exact unsigned decimal string representation in addition to any legacy numeric field; canonical names are additive and raw bits must never be discarded.
- Canonical application flag/enum display tables must use official VK_* names and values from the validated Vulkan registry baseline; unknown bits remain visible as UNKNOWN_BITS rather than being dropped.

- Vulkan 1.4.360 extension-query output must remain lossless across the in-app model, TXT export, HTML export, and structured database payload. Generic detailed query output may carry new fields, but canonical enum names must accompany raw enum values where a canonical Vulkan enum is known.
- Update APK downloads must continue to validate official release URL provenance, APK parseability, package identity, signing-certificate compatibility, and monotonically newer versionCode/versionName before invoking the package installer.
- Release 0.32.4 inherits the Vulkan-Headers 1.4.360 compile/query baseline from 0.32.3 without removing any query group or report field.

## Release 0.32.5 update confirmation and complete-export gate
- Discovering an update must not start an APK download. The update-available banner may invite the user to review/download, but the actual download begins only after an explicit confirmation action in the update confirmation dialog.
- A manual Info update check that finds a newer compatible release must open the same confirmation dialog used by the update-available banner. The non-interactive startup check must remain non-modal and must not open the confirmation dialog automatically.
- The confirmation dialog must identify the installed version, installed versionCode, installed ABI, selected download ABI, selected APK asset and available release version before download. A remote APK versionCode that has not yet been inspected must be described as pending verification rather than guessed.
- Release notes displayed before download must come from the official GitHub latest-release metadata already accepted by the update flow, remain bounded by the existing response-size limit, and be rendered as inert text. Large release-note line collections must use lazy scrolling rather than eager full-layout rendering.
- Downloaded APK package identity, signing certificate, versionCode and versionName verification remains mandatory after download and before launching Android's package installer.
- TXT and HTML complete-report export actions must use the same collection-completeness gate as Database submission. While CollectionStatus is COLLECTING, all three complete-report actions remain disabled and must not snapshot an in-progress report.
- Settings must expose the official public VulkanScope Database URL https://efishell0.github.io/VulkanScope_database/ without making the Database API endpoint user-editable.
- These visual/interaction changes remain confined to the existing update banner/dialog plus Info/Settings destinations; Vulkan capability pages and shared data presentation must not be restyled.


## Release 0.32.6 collection mutation gate
- A partial or `VK_INCOMPLETE` Surface-format enumeration must never be used to infer that unreturned format/color-space pairs are unsupported. Negative Surface catalog entries require a completed enumeration result.
- Queue capability fields collected into the report model, including video decode, video encode, optical flow, data graph and unknown queue bits, must remain visible in the Queues UI or an equivalent explicit technical detail view.
- While a complete Vulkan collection pass is active, driver-source changes and Turnip package selection/import are forbidden in both UI state and Activity callbacks; a race with recomposition must not allow the active driver inputs to mutate mid-collection.
- TXT, HTML and Database complete-report actions remain unavailable during active collection.
- SAF exports must preserve the exact complete-report snapshot captured when the export action is initiated; returning from a document picker must not regenerate the report from a newer or in-progress collection state.
- Installed Turnip runtime discovery must resolve only the validated metadata-declared library inside VulkanScope private bundle storage. Arbitrary first-JSON or first-`.so` fallbacks are forbidden.
- Manual update checking belongs beside application version information in Info and must not be duplicated in Settings.

## Release 0.32.7 Turnip SAF picker and Database 0.34.4 compatibility
- Turnip package selection uses a dedicated single-file SAF content picker whose accepted MIME set is restricted to ZIP-compatible types. A wildcard `*/*` picker is forbidden because archive-capable document providers may treat ZIP files as browsable containers instead of selectable package files.
- Turnip picker launch is re-entry guarded so rapid taps, recomposition or a pending Activity Result cannot launch a second SAF picker while one is already active.
- The selected package is still treated as untrusted content: ZIP structure, entry paths, size bounds, `meta.json`, schemaVersion, metadata-declared `libraryName`, canonical private-storage path and non-empty `.so` validation remain mandatory after selection; MIME type alone is never trusted as package validation.
- VulkanScope 0.32.7 remains schema-compatible with VulkanScope Database 0.34.4. Schema-v3 Display/HDR, layer-extension lists, queue video/optical-flow/data-graph fields, Surface diagnostics, exact U64 strings and Vulkan 1.4.360 registry/query metadata must remain present in complete submissions.
- Database UI-only changes never justify mutating raw Vulkan values or introducing producer fields that are not required by the published submission schema.

## 0.32.8 Turnip SAF and HDR presentation
- Turnip bundle selection must use the platform Storage Access Framework document picker through ACTION_OPEN_DOCUMENT / ActivityResultContracts.OpenDocument; ACTION_GET_CONTENT must not be used for this archive import because it may delegate to third-party file browsing UIs that treat ZIP archives as navigable containers instead of returning the archive document.
- The Turnip picker must request only ZIP-compatible MIME types and must retain the in-flight guard so a second picker cannot be launched while a document request is pending.
- MIME filtering is usability only; bounded ZIP extraction, zip-slip protection, required meta.json, schema validation and metadata-declared Vulkan .so validation remain authoritative.
- Driver bundle extraction byte accounting must count each decompressed byte exactly once.
- Android Display HDR types must be represented from the values actually exposed by Android. Logo assets are presentation only and must never create or infer HDR capability support.
- Dolby Vision and HDR10+ use the white-wordmark VulkanScope Database presentation assets; HDR10 uses its dedicated HDR10 logo on an opaque white card. Unknown HDR types remain visible as text.

- Android API 37 HLG+ must use the canonical Display.HdrCapabilities value 6; value 5 must remain unknown unless Android defines it in a future API.


## 0.32.9 Turnip SAF regression restoration
- The Turnip SAF launcher/opening behavior is restored exactly to the known-working VulkanScope 0.32.4 path: `ActivityResultContracts.OpenDocument()` with MIME candidates `application/zip`, `application/octet-stream`, and `*/*` as the final provider-compatibility fallback.
- The 0.32.7/0.32.8 picker re-entry/MIME experiments are superseded for the launcher itself because they did not resolve the observed device/provider regression. Do not reintroduce those picker changes without device-level evidence.
- The wildcard is only a document-picker compatibility fallback. It does not weaken package trust: ZIP entry-count/size bounds, zip-slip/canonical-path checks, required `meta.json`, schema validation, exact metadata-declared `libraryName`, private-storage containment, and non-empty Vulkan `.so` validation remain authoritative after selection.
- Driver import still refuses to commit a selected package while Vulkan collection is active; Settings keeps the import/driver controls disabled until complete collection.
- Dolby Vision and HDR10+ application artwork must use the exact white-wordmark assets shipped by VulkanScope Database 0.34.7. Black-wordmark variants must not be substituted on the dark application surface.

## 0.33.0 complete Turnip/SAF restoration
- This release supersedes the 0.32.6-0.32.9 Turnip/SAF experiments. All Turnip/SAF behavior must match the known 0.32.4 implementation, not only the picker launcher.
- The picker must use ActivityResultContracts.OpenDocument with `application/zip`, `application/octet-stream`, and `*/*` fallback exactly as 0.32.4 did.
- Turnip import must use the 0.32.4 install flow and must not add collection-state rejection before ZIP processing.
- Driver switching must use the 0.32.4 requestDriverModeChange behavior and must not be blocked by collection state.
- Installed Turnip library discovery must use the 0.32.4 metadata-first then non-empty `.so` fallback behavior.
- Settings driver controls and the Turnip ZIP import action must match 0.32.4 enablement behavior. Complete-report gating remains mandatory only for TXT, HTML, and Database submission.
- Native Turnip loader code, CMake integration, and VulkanProbeService must remain byte-identical to 0.32.4 unless a separately verified Turnip fix is requested.
- HDR presentation, Vulkan 1.4.360 query coverage, update confirmation, report completeness, and Database compatibility changes are independent and must remain preserved.


## 0.33.1 collection gate for driver controls
- This release preserves the restored VulkanScope 0.32.4 Turnip/SAF implementation itself. Picker, import, library discovery and driver-switch internals must not be rewritten by this gate.
- While `collectionStatus == COLLECTING` or no completed device report is available, both System Vulkan driver and Turnip / third-party driver controls must be disabled in Settings.
- Turnip ZIP package selection/import must be disabled under the same complete-report gate used by TXT, HTML and Database submission.
- Driver and package controls become interactive again only after the complete Vulkan collection pass has finished.
- The disabled state must be visually explicit and must not synthesize a different Turnip support result; it is a temporary collection-state gate only.
- This rule supersedes only the 0.33.0 Settings enablement clause. The underlying 0.32.4 Turnip/SAF behavior remains the reference implementation.


## Release 0.33.2 capability-page Material 3 Expressive redesign
- The earlier Info/Settings-only visual-scope restriction is superseded for Overview, Vulkan, Display & HDR, Surface, Features, Memory, Queues, Formats, Properties & Limits, Extensions, and Profiles by the explicit 0.33.2 redesign request.
- Info and Settings remain the visual reference and must not lose their established action hierarchy, touch targets, or content.
- Capability pages must use the same dark neutral + rose/red Material 3 Expressive language: rounded expressive containers, clear icon/title hierarchy, readable grouped values, compact status treatments, and consistent spacing.
- Visual redesign must not remove, truncate from the underlying data model, synthesize, merge, or reinterpret any Vulkan/Android capability information. Long technical values must remain fully available through wrapping rather than destructive omission.
- Supported, unsupported, available, unavailable/not applicable, and unknown/not queried remain semantically distinct. Styling must never change capability meaning.
- Search, filter, navigation, collection, export, Database, Turnip/SAF, update, and native query behavior are outside the redesign and must remain functionally unchanged.
- Shared top app bar and navigation geometry remain stable; the redesign applies to destination content surfaces.

## Release 0.33.3 full application audit
- VulkanScope Database 0.35.3 remains the current consumer compatibility target. Existing schema-v2 envelope and schema-v3 technicalReport fields must remain backward-compatible; additive fields may clarify semantics without removing legacy keys.
- Android display capability states must preserve evidence: `Display.isWideColorGamut == false` is Unsupported, not Unavailable/Not exposed. An empty reported HDR type list remains Unavailable to match VulkanScope Database 0.35.3 semantics; it must not be promoted to Unsupported without a separate explicit support-state signal.
- The structured display object keeps legacy `preferredWideGamut` and additionally emits `preferredWideGamutColorSpace`; it also emits `hdrCapabilityStatus` so future consumers need not infer HDR availability from an empty list.
- A `Surface` parcel received by the isolated Vulkan probe service is owned by that service-side parcel and must be released after the queued native probe completes, succeeds, or fails. The Activity-owned SurfaceView surface must never be released by the service rule.
- Probe checkpoint polling must not repeatedly read and parse an unchanged potentially large JSON checkpoint. File length/mtime changes gate parsing; polling waits when unchanged.
- Present-mode enumeration uses its dedicated bounded count and must not accidentally reuse the larger surface-format bound.
- Current upstream terminology must distinguish the published Khronos Vulkan specification from VulkanScope's independently pinned staging producer/query baseline. As checked on 2026-08-20, the published specification is Vulkan 1.4.358; VulkanScope's project baseline remains the separately validated header/query staging revision 1.4.360.
- No Turnip/SAF implementation change is introduced by this audit. The 0.32.4-restored Turnip/SAF internals and the 0.33.1 collection-state UI gate remain intact.

