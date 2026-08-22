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
- Update discovery uses a bounded list of official EFIShell0/VulkanScope GitHub Releases so pre-release builds remain discoverable when no stable latest release exists. Draft releases are never eligible. Only validated newer-version APK assets from that official release list are eligible.
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


## 0.33.4 Info, theme and Android TV parity requirements
- Info owns Developer, Application, Device ABI, Android, Vulkan registry/query engine, About, complete TXT/HTML export, and VulkanScope Database actions in the same information architecture used by the companion OpenGLESScope application.
- Settings contains only Vulkan driver source selection and the Turnip ZIP import workflow; report export and Database submission are not Settings responsibilities.
- The primary VulkanScope accent is the official Khronos Vulkan dark red, RGB 164,30,34 / #A41E22. Derived focus and decorative tones may be lighter or darker only to preserve contrast and state visibility.
- HTML report presentation uses the same self-contained responsive report shell quality as OpenGLESScope while deriving hero, borders, available-state accent and links from the Vulkan red family.
- Android TV uses the navigation rail in portrait and landscape. Read-only capability section, item and key/value surfaces are focusable browse targets on television devices and request bring-into-view when focused.
- Television read-only browse focus must never imply clickability or mutate report state when Enter/Center is pressed.


## 0.33.5 update discovery and banner parity requirements
- VulkanScope update discovery must use the bounded official EFIShell0/VulkanScope GitHub Releases list rather than depending on `/releases/latest`, because GitHub excludes pre-releases from that endpoint and may return 404 when only pre-release builds exist.
- Draft releases are ignored. Stable and pre-release entries are eligible only when their tag parses as a valid numeric VulkanScope version newer than the installed version.
- Candidate releases are compared numerically and must not be trusted merely because of GitHub list position.
- Release metadata response materialization remains bounded to 2 MiB.
- APK assets remain ABI-specific with universal fallback and must pass strict HTTPS `github.com/EFIShell0/VulkanScope/releases/download/` URL validation with no user-info, query or fragment components.
- The manual up-to-date banner must match the companion OpenGLESScope visual structure: green `UP TO DATE` capability badge followed by neutral explanatory text, using the same spacing and enter/exit timing.


## Release 0.33.6 update-dialog parity requirements
- Application version is 0.33.6 with versionCode 336.
- The update status banner keeps the established 0.33.5 parity: identical enter/exit timing, spacing, progress presentation, UP TO DATE badge geometry, Review action hierarchy and 8-second/10-second visibility behavior used by OpenGLESScope, with VulkanScope identity text and Vulkan brand accent where branding is required.
- The update confirmation dialog uses the same layout geometry, key/value body-small typography, 0.9/1.1 label-value weight split, release-notes scrolling bounds, button labels and explanatory/security wording as OpenGLESScope, substituting only VulkanScope identity and brand-specific accent color where applicable.
- Manual checks that discover a compatible newer release open the same confirmation flow; startup checks remain non-modal and never automatically open the dialog.
- These parity changes must not weaken official-release URL validation, ABI selection, package/signing/version verification, bounded downloads, private-cache confinement, Vulkan collection semantics, Turnip handling or Database behavior.


## Release 0.33.7 full producer/report/display audit
- Application version is 0.33.7 with versionCode 337.
- The current published Khronos Vulkan specification checked for release 0.34.0 is Vulkan 1.4.360 dated 2026-08-14. VulkanScope pins the matching Vulkan-Headers 1.4.360 commit and validated query catalog; published specification, headers, runtime device API version and driver version remain distinct concepts.
- The Vulkan 1.4.359 registry delta, including VK_EXT_cooperative_matrix_maintenance1 and vkGetPhysicalDeviceCooperativeMatrixProperties2EXT, remains part of automatic validated runtime query coverage. VK_EXT_image_tiling_control from the preceding registry delta remains covered. No validated native query group, feature, property, limit, extension, format, memory, queue, Surface/WSI, Vulkan Video, Profile or registry-coverage field may be removed by this release.
- Android display evidence is refreshed while the Activity is started by DisplayManager.DisplayListener and on lifecycle resume. The active application display is preferred over a permanently pinned DEFAULT_DISPLAY so foldable/external-display changes cannot silently leave exports or Database submissions with stale display metadata.
- Android API 34+ mode-specific HDR types use Display.Mode.getSupportedHdrTypes. HDR_TYPE_INVALID (-1) is never rendered or submitted as a capability. Unknown future non-invalid HDR values remain visible with their raw Android numeric value and are never mapped to a known HDR type without Android documentation.
- API levels where wide-gamut state is not queryable remain Unavailable rather than Unsupported. Display HDR/wide-gamut data remains separate from Vulkan Surface format/color-space support.
- Update version ordering follows semantic pre-release precedence for VulkanScope numeric tags: a stable release outranks a pre-release with the same numeric core, later pre-release identifiers outrank earlier ones, and build metadata does not affect precedence. Drafts remain ineligible and only strictly newer validated releases may be offered.
- Self-contained HTML reports carry a restrictive local-only Content Security Policy and no-referrer policy. Report-derived text remains HTML-escaped. The report hero exposes the GPU metric exactly once.
- Properties & Limits "All" view must expose both limits and detailed property/query records; selecting All must not silently hide limits that are available in the report model.
- The official VulkanScope Database API endpoint is defense-in-depth pinned to the exact HTTPS host vulkanscope-database-api.vulkanscope.workers.dev and API root. Submission remains explicit, complete-report-only, 2 MiB bounded and schema-v2/schema-v3 compatible with VulkanScope Database 0.35.6.
- Release verification scripts must validate the actual release version and the pinned Vulkan-Headers 1.4.360 baseline rather than obsolete 0.33.3/1.4.357 values.
- These corrections must not weaken native probe isolation, bounded enumeration, Vulkan handle ownership/destruction order, Turnip bundle validation, Surface lifecycle synchronization, ABI policy, APK signature/package/version validation, IPv6-first fast fallback, or capability-state semantics.


## Release 0.33.8 Kotlin release-build repair
- Application version is 0.33.8 with versionCode 338.
- The update confirmation dialog uses `TextAlign.End` for value-column alignment, so `androidx.compose.ui.text.style.TextAlign` must remain explicitly imported in `MainActivity.kt`.
- Release verification must fail if the required `TextAlign` import is absent while the dialog still uses `TextAlign.End`.
- This release is a build-correctness repair only. Vulkan collection, registry/query coverage, TXT/HTML exports, Database schema/submission, Display/HDR semantics, update behavior, Turnip/SAF behavior and security policy must remain unchanged from 0.33.7.
- Warnings originating inside the pinned third-party libadrenotools dependency are not VulkanScope Kotlin compile failures and must not be misreported as the cause of this release build failure.


## Release 0.33.9 Properties & Limits summary parity
- Application version is 0.33.9 with versionCode 339.
- Properties & Limits must show `query results` and `unique property names` summary counts immediately below the explorer controls for every filter, including All and Limits.
- The All summary counts every currently visible detailed-property row plus every currently visible limit row after the active search query is applied.
- The Limits summary counts only currently visible limit rows after the active search query is applied.
- Unique-name counts are computed from the same rows represented by the corresponding result count and must not exclude limits from the All view.
- This release changes presentation/summary accounting only. Vulkan collection, native queries, TXT/HTML reports, Database submission/schema, Display/HDR semantics, update behavior, Turnip/SAF behavior and security policy remain unchanged from 0.33.8.


## Release 0.33.10 Properties & Limits summary semantics
- Application version is 0.33.10 with versionCode 340.
- The 0.33.9 requirement that All merge property rows and limit rows into one `query results` count is superseded. Property-query counts and limit counts are different data classes and must never be combined under a property/query label.
- In the All filter, the summary must show three independent values: currently visible detailed-property query results, unique property names derived only from those visible property rows, and currently visible limit rows.
- In the Limits filter, the summary must show only the currently visible limit-row count; it must not label limits as property query results or include limit names in `unique property names`.
- In every property-section filter, `query results` and `unique property names` are derived only from the currently visible detailed-property rows after the active search query is applied.
- Search continues to filter properties and limits independently, and every displayed count must describe exactly the visible data class it labels.
- This release changes UI summary semantics only. Vulkan collection/native queries, detailedProperties/limits report data, TXT/HTML export, Database submission/schema, Display/HDR, update, Turnip/SAF and security behavior remain unchanged from 0.33.9.



## Release 0.34.0 CapsViewer field-level parity
- Application version is 0.34.0 with versionCode 341.
- VulkanCapsViewer 4.12 parity is evaluated at field level, not only by extension or structure scheduling. Canonical alias structures promoted between EXT/KHR/core may share the same VkStructureType and are considered one queried capability path when the Vulkan header defines them as aliases.
- VK_EXT_host_image_copy property collection must use the Vulkan two-call count/allocate/fill pattern for pCopySrcLayouts and pCopyDstLayouts. Pointer addresses or generic pointer placeholders are forbidden report values.
- Host Image Copy layout arrays are bounded to 65536 entries per source/destination list before allocation. Oversized counts remain explicit Unavailable evidence and must not allocate unbounded memory.
- Every reported VkImageLayout uses a canonical Vulkan enum name when known and retains its raw numeric value. Unknown future layout values remain visible as UNKNOWN_VK_IMAGE_LAYOUT with the raw value.
- The same canonical Host Image Copy layout-array evidence must reach UI, TXT, HTML and technicalReport/Database through the existing complete-report property path.
- The published Vulkan specification baseline for this release is Vulkan 1.4.360 (2026-08-14), matching the pinned Vulkan-Headers/query baseline.

## Release 0.34.2 complete-report parity audit
- Application version is 0.34.2 with versionCode 342.
- Every collected feature, detailed property, limit, extension, layer extension, memory entry, queue entry, format entry, Surface/WSI result, Display/HDR value, Vulkan Profile result and registry/query provenance item must have an explicit in-app or report-consumption path.
- The generic detailed-property pipeline is authoritative for validated extension/property data: native query result -> DeviceReport.detailedProperties -> Properties & Limits UI -> TXT DETAILED QUERY RESULTS -> HTML Detailed query results -> technicalReport.devices[].detailedProperties -> VulkanScope Database Properties/detail/compare views.
- Host Image Copy source/destination VkImageLayout arrays added in 0.34.0 must remain visible through that complete pipeline and must never regress to pointer-address or pointer-placeholder output.
- TXT and HTML must preserve canonical Vulkan flag names together with exact raw values for memory flags, queue flags, video codec-operation flags and format feature masks. Unknown bits must remain visible.
- TXT instance-layer output must include the extensions belonging to each instance layer, matching HTML and structured Database data.
- Local UI and offline HTML must expose Android security-patch/build provenance already present in the complete TXT diagnostic report. Database submission keeps its privacy-bounded top-level Android metadata contract and must not add serial, Android ID, IMEI, MAC, account or private-path data.
- Registry coverage exports must include both validated query-group names and implemented physical-device-structure names; counts alone are not a complete audit trail.
- Vulkan 1.4.360 dated 2026-08-14 is the current published specification and remains distinct from runtime device API and driver versions.

## Release 0.34.2 Host Image Copy promoted-name parity
- VkPhysicalDeviceVulkan14Properties Host Image Copy pointer-backed members are reported with their canonical member names pCopySrcLayouts and pCopyDstLayouts.
- Core 1.4 Host Image Copy evidence remains in the Core 1.4 section so promoted-core provenance is not erased.
- VK_EXT_host_image_copy evidence remains extension-scoped when the extension query path is used.
- Database comparison may canonicalize promoted Core 1.4 Host Image Copy fields to their VK_EXT_host_image_copy equivalents for side-by-side comparison, but raw report section/name provenance must remain unchanged.
- copySrcLayoutCount, pCopySrcLayouts, copyDstLayoutCount, pCopyDstLayouts, optimalTilingLayoutUUID and identicalMemoryTypeRequirements form one promoted capability family for compare aliasing only.

## Release 0.34.3 IzzyOnDroid direct-update policy
- Application version is 0.34.3 with versionCode 344.
- The built-in GitHub self-updater is opt-in and disabled by default for every installation. No startup update discovery request may run until the user has explicitly enabled direct updates.
- Enabling direct updates must require an explicit confirmation that names the official EFIShell0/VulkanScope GitHub Releases source and states that APKs installed through this path bypass IzzyOnDroid repository scanning and verification.
- Disabling direct updates must immediately stop future automatic checks and hide pending update UI. Existing package/signing/version/ABI validation remains mandatory whenever direct updates are enabled.
- Info may expose manual update checking only while direct updates are enabled. Settings is the single owner of the direct-update opt-in state.
- IzzyOnDroid repository identity must not be inferred from Android installer package identity because repository clients can install packages from multiple repositories. The policy is implemented safely by keeping the self-updater disabled by default independent of installer identity.
- Fastlane metadata for IzzyOnDroid must be versioned with the release source and must use the same tagged revision as the APK release.
- No capability collection, TXT/HTML/Database reporting, Turnip/SAF behavior, Vulkan 1.4.360 coverage, or CapsViewer parity may regress because of this release.

## Release 0.34.4 IzzyOnDroid first-install notice
- Application version is 0.34.4 with versionCode 345.
- Direct GitHub updates remain opt-in and disabled by default for every installation.
- A genuine first installation with direct updates disabled shows one seven-second non-modal information banner using the existing update-status banner visual language.
- The notice states only that direct GitHub updates are disabled by default and can optionally be enabled in Settings; it must not imply that IzzyOnDroid updates are disabled.
- The one-time notice must not perform a network request and must not appear on ordinary application upgrades.
- The separate consent that names the official GitHub Releases source and explains the IzzyOnDroid repository scanning/verification bypass remains mandatory before direct updates can be enabled.
- Vulkan 1.4.360 collection, report completeness, Database submission and capability-state semantics must not regress.


## Release 0.34.5 Obtainium update-management integration
- Application version is 0.34.5 with versionCode 346.
- The built-in direct GitHub updater remains disabled by default and performs no startup network request until explicitly enabled.
- Runtime UI must not claim an IzzyOnDroid-specific security or repository relationship. Obtainium is presented only as an optional external update manager.
- Settings exposes an Add to Obtainium action using Obtainium's documented `obtainium://app/` import path.
- The generated Obtainium configuration targets the official EFIShell0/VulkanScope GitHub repository and filters release assets to filenames containing `universal` and ending in `.apk`; architecture auto-filtering is disabled so the universal release asset is selected deterministically.
- If Obtainium is unavailable, the action falls back to the official Obtainium project page instead of silently failing.
- Enabling the built-in updater must explain that, when Obtainium is also used, both update managers can check the same official GitHub Releases source and duplicate update notifications.
- The first-install seven-second information banner keeps the established update-banner visual language and now recommends Obtainium as the external update-management path without performing a network request.
- Existing APK package/signing/version/ABI validation, explicit installation consent, Vulkan 1.4.360 capability collection and complete UI/TXT/HTML/Database reporting must not regress.

## Release 0.34.6 Obtainium UI simplification
- Application version is 0.34.6 with versionCode 347.
- The Direct GitHub updates setting remains present, opt-in and disabled by default.
- Settings must not expose an Add to Obtainium button or invoke an Obtainium deep link; Obtainium remains an optional external update manager described by informational text only.
- The portable obtainium-config.json remains in the source release and continues to target the official application GitHub repository with a universal-APK filter.
- When Direct GitHub updates are disabled, no startup update discovery request is performed by the application.
- Existing direct-update provenance, package identity, signing-certificate, version and ABI validation remains mandatory when the built-in updater is enabled.
- Graphics capability collection, reporting, export and Database behavior must not regress.

## Release 0.34.7 default-enabled direct GitHub updates
- Application version is 0.34.7 with versionCode 348.
- This release supersedes the default-off/opt-in-by-default clauses of releases 0.34.3 through 0.34.6; their security validation and explicit APK install-action requirements remain in force.
- Direct GitHub updates are enabled by default only when no saved `direct_updates_enabled` preference exists. Existing users' explicit saved choice must be preserved across upgrades.
- A fresh installation may perform the existing non-blocking startup update check against the fixed official EFIShell0/VulkanScope GitHub Releases API endpoint.
- Disabling Direct GitHub updates must persist `false`, stop future startup update discovery, hide pending update UI and prevent update APK download until the user explicitly enables the setting again.
- Obtainium remains optional external update-management guidance only; no Add to Obtainium runtime button or deep-link action is reintroduced.
- The portable `obtainium-config.json` remains source metadata and continues to select the official universal APK release asset.
- Update APK download and installation remain explicit user actions. Existing official-release provenance, HTTPS, package identity, signing-certificate, versionCode/versionName and ABI validation requirements remain mandatory.
- Vulkan collection, report/export completeness, Database submission, Display/HDR semantics and capability-state semantics must not regress.

