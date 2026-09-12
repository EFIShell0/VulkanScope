# VulkanScope

**VulkanScope** is an advanced Vulkan capability inspection and reporting tool for Android. It queries the Vulkan implementation exposed by the active driver and presents detailed information about the GPU, Vulkan core capabilities, extensions, features, properties, limits, memory, queues, formats, Surface/WSI support, Vulkan Video, Android display/HDR capabilities, Vulkan Profiles, and more.

Database: https://efishell0.github.io/VulkanScope_database/

**Current version: 1.2.5**

This app supports **Obtainium**; the project/release URL can be used for update tracking.

> VulkanScope reports what the active Vulkan implementation actually exposes. It does not infer support from the Android version, GPU model, or Vulkan API version alone.

## Highlights

- Vulkan 1.0–1.4 core capability inspection
- Query coverage validated against **Vulkan-Headers 1.4.362**
- Extensive Khronos and vendor-specific feature/property queries
- Runtime instance and device extension enumeration with `specVersion`
- Detailed physical-device properties and limits
- Memory heap/type inspection with canonical Vulkan flags
- Queue-family and presentation capability inspection
- 64-bit format-feature inspection with exact raw values
- Real Android `VkSurfaceKHR` capability inspection
- Surface formats, color spaces, present modes, transforms, composite alpha, and usage flags
- Android Display and HDR capability reporting
- Vulkan Video decode/encode capability inspection
- Vulkan Profile evaluation
- Offline registry-driven Vulkan query metadata
- Local **Encyclopedia** with bounded offline search across `VkResult`, `vk*`, `VK_*`, `Vk*`, and extension symbols
- Separate **Analysis workspace** with evidence provenance, global report search, snapshot comparison, System ↔ Turnip A/B comparison, collection diagnostics, requirement/minimum evaluation, dependency analysis, raw structured report inspection, Database comparison, local history, watched evidence, sharing, and optional dedicated-process Vulkan self-tests
- Registry-driven extension dependency/reference graphs kept separate from runtime support evidence
- Selected-format **Image Format Properties2** drill-down using collected runtime evidence
- Local VulkanScope Database permalink and QR-code sharing
- Unified System Vulkan / managed Turnip driver manager with explicit activation, bounded private slots, provenance-aware imports, and metadata-authoritative, path-confined, size-bounded bundle validation
- TXT and self-contained HTML complete reports, plus explicit schema-v3 structured JSON export from Analysis
- Explicit complete-report submission to VulkanScope Database
- Secure GitHub-based direct update checking with explicit review/confirmation, validated-network gating, and Obtainium-compatible external update management
- Multi-ABI native Android builds
- Dark Material 3 Expressive interface

## UI

VulkanScope uses a dark Material 3 Expressive design focused on dense technical information without hiding raw capability data.

The interface is organized into dedicated inspection areas for device information, properties, features, extensions, memory, queues, formats, Surface/WSI, display/HDR, Vulkan Video, Profiles, settings/driver management, and application information. **Encyclopedia** and **Analysis workspace** appear on Overview as compact destination cards and open as separate Overview-parent pages instead of embedding their complete contents directly in Overview. Analysis-only state is created only while the Analysis page is active, and Encyclopedia search/index work stays on the Encyclopedia page.

Top-level scrollable pages, bounded detail dialogs, and bounded update release notes use the same activity-aware scroll-boundary hints. The indicators overlay content instead of permanently reserving a side lane: at the absolute top only the Down hint is eligible, in the middle both Up and Down are eligible, at the absolute bottom only Up is eligible, and non-scrollable content shows neither. The hints fade out after scrolling becomes idle and fade back in when gesture, fling, or programmatic scrolling resumes.

Overview `Explore` and `Quick access` use width-aware wrapping layouts so destinations remain usable on narrow phones, enlarged display sizes, large text, and landscape layouts. Format and Extension detail dialogs use bounded Material 3 Expressive surfaces with compact grouped evidence cards; long labels, values, translated strings, and large-text layouts stack responsively instead of being forced into cramped columns.

Shared filter groups use a horizontal Material 3 Expressive carousel with explicit left/right controls, RTL-aware direction, selected-item auto-visibility, accessibility descriptions, Android TV focus support, and bounded continuation motion. The five primary navigation destinations also use short, finite click animations rather than continuous background motion.

Validated Android default-network state is surfaced independently from Vulkan collection state. Internet-backed actions are disabled when no validated network is available instead of performing synthetic HTTP/DNS connectivity probes.

Status values are kept semantically distinct where applicable:

- **Supported**
- **Unsupported**
- **Available**
- **Unavailable**
- **Incomplete**
- **Not applicable**
- **Unknown / not queried**

A capability that was not queried or could not be determined is not silently converted into `Unsupported`. `VK_INCOMPLETE` remains partial positive evidence, while `Not applicable` means that a query path does not apply to the currently exposed runtime prerequisites rather than proving that the underlying hardware capability does not exist.

# Screenshots

<p align="center">
  <img src="https://raw.githubusercontent.com/EFIShell0/VulkanScope/main/screenshots/overview-3.jpg" width="200">
  <img src="https://raw.githubusercontent.com/EFIShell0/VulkanScope/main/screenshots/properties-3.jpg" width="200">
  <img src="https://raw.githubusercontent.com/EFIShell0/VulkanScope/main/screenshots/vulkan-3.jpg" width="200">
</p>

<p align="center">
  <img src="https://raw.githubusercontent.com/EFIShell0/VulkanScope/main/screenshots/surface-3.jpg" width="200">
  <img src="https://raw.githubusercontent.com/EFIShell0/VulkanScope/main/screenshots/display-3.jpg" width="200">
  <img src="https://raw.githubusercontent.com/EFIShell0/VulkanScope/main/screenshots/extensions-3.jpg" width="200">
</p>

<p align="center">
  <img src="https://raw.githubusercontent.com/EFIShell0/VulkanScope/main/screenshots/mumuplayer-3.png" width="500">
  <img src="https://raw.githubusercontent.com/EFIShell0/VulkanScope/main/screenshots/database_new3.png" width="500">
</p>

**NOTE: The first landscape image was taken from MuMuPlayer, and VM (hypervisor) software like BlueStacks, MuMuPlayer, LDPlayer, QEMU etc. is not supported.**

**NOTE 2: The last landscape photo is a screenshot of the database website.**

Database link: https://efishell0.github.io/VulkanScope_database/

## Vulkan coverage

VulkanScope queries the active Vulkan loader and selected physical device directly.

Coverage includes:

- Vulkan instance version
- Instance extensions
- Instance layers and layer-provided extensions
- Physical-device identification
- Core Vulkan 1.0 features
- Vulkan 1.1 feature/property structures
- Vulkan 1.2 feature/property structures
- Vulkan 1.3 feature/property structures
- Vulkan 1.4 feature/property structures
- Physical-device limits
- Sparse properties
- Subgroup properties
- Driver properties
- Device extensions and exact extension `specVersion`
- Extension-specific feature structures
- Extension-specific property structures
- Khronos, EXT, and vendor-specific capability structures

The bundled query catalog is validated against the **Vulkan 1.4.362** registry/header baseline and the pinned Vulkan-Headers revision. The validated Android-queryable physical-device coverage contains **304 provider extensions** (**299 stable + 5 provisional**), **110 implemented physical-device structures**, and **104 validated query groups**. The bundled extension reference contains the complete **476-extension** Vulkan 1.4.362 registry census. Unknown or unreviewed structures are not queried using guessed `sType` values, layouts, or field definitions.

### Vulkan 1.4.362 baseline additions

The current query set includes the validated additions carried through the Vulkan 1.4.361 and 1.4.362 baselines, including:

- `VK_NV_private_data_base_handle`
  - `VkPhysicalDevicePrivateDataBaseHandleFeaturesNV`
  - `privateDataBaseHandle`
- `VK_EXT_image_tiling_control`
  - `VkPhysicalDeviceImageTilingControlFeaturesEXT`
  - `imageTilingControl`
- `VK_EXT_cooperative_matrix_maintenance1`
  - maintenance feature fields
  - `vkGetPhysicalDeviceCooperativeMatrixProperties2EXT`
  - cooperative-matrix property records
  - canonical component-type names with raw enum values
- `VK_KHR_pipeline_library_group_handles`
  - validated provider coverage through the canonical KHR structure path
  - KHR/EXT alias-equivalent coverage remains represented without fabricating duplicate capabilities
- `VK_VALVE_buffer_device_address_allocation_alignment`
  - `bufferDeviceAddressAllocationAlignment`
  - `maxBufferDeviceAddressAllocationAlignment`

Extension-specific queries are run only when their requirements are actually exposed by the runtime. Provisional registry providers remain explicitly separated from stable providers and are not queried as normal stable capability structures without the required beta-header conditions.

## Device information

The Device and Properties views expose detailed physical-device information such as:

- Device name and type
- Vendor ID and device ID
- Vulkan API version
- Driver version and driver properties
- Pipeline-cache UUID
- Physical-device limits
- Sparse residency properties
- Subgroup capabilities
- Core-version-specific properties
- Extension-specific properties
- Raw values where preserving the original Vulkan value is important

This makes VulkanScope useful for comparing vendor drivers, Android firmware revisions, and third-party Vulkan drivers on otherwise similar hardware.

## Extensions

VulkanScope enumerates the exact instance and device extensions exposed at runtime.

For each extension, the application can preserve information such as:

- Canonical Vulkan extension name
- Instance or device scope
- `specVersion`
- Runtime availability
- Associated queried feature/property information where implemented

Large extension lists can be searched and filtered.

Extension detail views can also show:

- runtime enumeration evidence,
- dedicated VulkanScope query-handler evidence,
- embedded Khronos registry dependency/reference metadata,
- bounded dependency traversal and graph visualization,
- links to the authoritative Khronos extension reference where applicable.

Registry metadata is presented separately from runtime support. A registry dependency or known query handler is not treated as proof that the active device exposes an extension.

Vendor-specific coverage includes applicable functionality from ecosystems such as AMD/AMDX, ARM, HUAWEI, IMG, INTEL, NV/NVX, QCOM, SEC, VALVE, and other Vulkan vendors represented by the registry.

## Features

Feature reporting includes both core and extension-defined feature structures.

Examples include:

- Robust buffer access
- Geometry and tessellation
- Multi-viewport functionality
- Texture compression
- Sparse resources
- Shader functionality
- Descriptor functionality
- Dynamic rendering
- Synchronization
- Mesh/ray-tracing related capabilities where exposed
- Vulkan Video related capabilities
- Vendor-specific feature structures

Feature values are reported from Vulkan queries rather than inferred from extension names alone.

## Memory

The Memory view reports the physical device's Vulkan memory model in detail:

- Memory heap count
- Memory type count
- Heap size
- Heap index relationships
- Raw heap flags
- Canonical heap flag names
- Raw memory-property flags
- Canonical memory-property flag names

Canonical memory-property reporting can include:

- `VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT`
- `VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT`
- `VK_MEMORY_PROPERTY_HOST_COHERENT_BIT`
- `VK_MEMORY_PROPERTY_HOST_CACHED_BIT`
- `VK_MEMORY_PROPERTY_LAZILY_ALLOCATED_BIT`
- `VK_MEMORY_PROPERTY_PROTECTED_BIT`
- Applicable AMD/NV extension-defined memory bits

Heap flags include applicable core and extension-defined values, including QCOM tile-memory reporting where exposed.

Unknown future bits are preserved as raw values instead of being discarded.

## Queue families

For every queue family, VulkanScope can report:

- Queue family index
- Queue count
- Queue capability flags
- Graphics support
- Compute support
- Transfer support
- Sparse binding support
- Protected queue support
- Presentation support
- Vulkan Video codec-operation flags where exposed

Queue and video-operation masks are preserved as raw values and also decoded to canonical Vulkan names. A zero queue capability mask is shown as zero capability bits rather than a fabricated generic `VK_NONE`. Vulkan Video codec-operation query availability is tracked separately per queue family; a successfully queried zero codec-operation mask is represented as `VK_VIDEO_CODEC_OPERATION_NONE_KHR`, while unavailable, unknown, and not-applicable states remain distinct.

## Formats

VulkanScope inspects Vulkan image and buffer format capabilities.

Reported format data can include:

- Linear tiling feature flags
- Optimal tiling feature flags
- Buffer feature flags
- Sampled-image support
- Storage-image support
- Color/depth-stencil attachment support
- Blending support
- Transfer source/destination support
- Video-related format capabilities
- Extension-defined format features

`VkFormatFeatureFlags2` is treated as a 64-bit mask. Exact values are preserved so high-bit extension flags are not lost to JavaScript-style numeric precision limits when reports are submitted to the database.

Canonical flag names are derived from the current Vulkan registry baseline while raw values remain available for verification.

Selected formats can also expose an **Image Format Properties2** drill-down using already-collected runtime evidence. VulkanScope does not manufacture an Image Format Properties2 result when the corresponding query evidence is absent. When `VkFormatProperties3` / `VkFormatFeatureFlags2` data is available, its 64-bit values remain authoritative; legacy 32-bit format-feature data is fallback evidence only.

The **Format Explorer** also provides combinable usage/capability filters for sampled, storage, attachment, filtering, blit, transfer, Vulkan Video, and YCbCr-related paths. These filters operate on collected evidence and do not infer format support that was not reported by the active implementation.

## Surface / WSI

VulkanScope creates a real Android `VkSurfaceKHR` and queries presentation capabilities against that surface.

Information includes:

- Minimum and maximum image counts
- Current, minimum, and maximum extents
- Maximum image-array layers
- Supported transforms
- Current transform
- Supported composite-alpha modes
- Supported image-usage flags
- Surface formats
- Surface color spaces
- Present modes
- Queue-family presentation support
- Surface-query diagnostics where needed

Surface transform, composite-alpha, and usage masks are preserved as raw values and decoded to canonical Vulkan names.

Surface format and present-mode enumeration use bounded, retry-aware collection with explicit completeness state. Partial `VK_INCOMPLETE` results remain partial positive evidence instead of being converted to unsupported, and driver-controlled second-stage counts are revalidated before use.

Analysis can combine Vulkan Surface evidence with Android Display/HDR evidence in a dedicated **Presentation** view, but the two sources remain explicitly separate. VulkanScope does not promote their coexistence into an unsupported end-to-end HDR, gamut, or presentation guarantee.

## Surface formats and color spaces

VulkanScope displays the exact Vulkan format + color-space pairs reported by the implementation.

Depending on the driver and device, this may expose SDR, wide-gamut, extended-range, or HDR-related color spaces such as:

- sRGB
- BT.709-related spaces
- BT.2020-related spaces
- Display P3
- HDR10 / ST2084
- HLG
- Extended sRGB
- Other core or extension-defined Vulkan color spaces

Vulkan Surface color-space support and Android physical-display HDR/wide-color capabilities are reported separately; one is not used as a substitute for the other.

## Android Display & HDR

The Display & HDR view reports Android display information independently from Vulkan Surface information.

Depending on the Android version and device, VulkanScope can report:

- HDR capability types
- Desired maximum luminance
- Desired maximum average luminance
- Desired minimum luminance
- Wide-color capability
- Preferred wide-gamut color space
- Display modes
- Resolution
- Refresh rates

If Android does not expose `HdrCapabilities`, VulkanScope reports the information as unavailable/not exposed rather than manufacturing HDR values.

## Vulkan Video

VulkanScope includes a dedicated Vulkan Video page driven by the Vulkan implementation exposed by the selected driver. The page separates overview state, exact-profile decode/encode evidence, bounded sampled-profile format evidence, and queue-family video-operation evidence.

The release-locked exact capability census intentionally uses one bounded **4:2:0 / 8-bit luma / 8-bit chroma** slice rather than inferring codec-wide support from a single generic query. When all owning extensions are exposed, the bounded profile census contains **47 exact combinations**:

### Decode

- H.264: 6 profiles × 3 picture-layout variants
- H.265 / HEVC: 5 profiles
- VP9: 4 profiles
- AV1: 3 profiles × 2 film-grain states

### Encode

- H.264: 6 profiles
- H.265 / HEVC: 5 profiles
- AV1: 3 profiles

Additional information can include:

- Video queue codec operations
- Exact video-profile capability results
- Coded extent information
- DPB/reference limits
- Bitstream alignment
- Rate-control capabilities
- Quality-level information
- Feedback capabilities
- Bounded sampled-profile Vulkan Video format properties
- Standard-header version information

Each queried codec/profile combination is evaluated independently. A successful exact query is evidence only for that exact combination; it is not promoted into codec-wide support. Required-extension absence becomes `Not applicable` only when the prerequisite extension enumeration is authoritative.

The **Vulkan Video Matrix** in Analysis correlates each retained exact profile with matching queue-operation evidence, bounded sampled-format evidence, and retained capability properties. These associations remain evidence links only and are not promoted into codec-wide support claims.

`Not applicable` on Vulkan Video means that the selected Vulkan driver does not expose the Vulkan Video API path required for that query. It **does not** mean that the phone or GPU lacks hardware video decoding/encoding through Android MediaCodec or other non-Vulkan video interfaces.

## Vulkan Profiles

VulkanScope evaluates supported Vulkan Profiles using the available runtime capability data.

Profile results distinguish between:

- **PASS**
- **FAIL**
- **UNKNOWN**

Unavailable or unqueried information is not automatically treated as failure.

The included profile catalog can cover profiles such as Android Baseline and Vulkan Roadmap profiles, depending on the bundled profile definitions.

## Encyclopedia

VulkanScope includes a separate **Encyclopedia** page opened from a compact Overview destination card. It is a local Vulkan reference surface and does not make runtime support claims from registry presence alone.

The default glossary explains VulkanScope evidence states and major Vulkan concepts, including instances, physical/logical devices, queues, command buffers, features, properties, limits, formats, layers, extensions, Surface/WSI, swapchains, `pNext`, `sType`, loader/device/driver version separation, and Vulkan naming conventions.

### `VkResult` reference

The Encyclopedia includes all **50 canonical Vulkan 1.4.362 `VkResult` values** with numeric values, concise command-outcome meanings, and aliases where applicable. Examples include `VK_SUCCESS`, `VK_INCOMPLETE`, `VK_ERROR_DEVICE_LOST`, `VK_ERROR_FORMAT_NOT_SUPPORTED`, WSI results, Vulkan Video query results, deferred-operation statuses, and current KHR result values.

`VK_SUCCESS` is interpreted only as success of the command that returned it; it is not treated as global device-support evidence. `VK_INCOMPLETE` remains partial-result evidence and cannot prove absence.

### Offline registry-symbol search

A deterministic build-time index generated from the locked Vulkan 1.4.362 `vk.xml` provides bounded local search across:

- **842** registered `vk*` command names
- **6248** registered `VK_*` tokens/enumerants/macros
- **2461** registered `Vk*` API types
- **476** registered extensions through the extension-reference catalog

Search categories are **All**, **VkResult**, **Commands**, **VK_***, **Types**, and **Extensions**. Large symbol indexes are not searched until at least two characters are entered, and visible results are capped at **24**. Result rows are lazy-list items rather than one eagerly composed result block.

The generated symbol decoder validates field delimiters and skips malformed index rows instead of allowing malformed generated/reference data to crash search. Registry ownership/provider metadata remains reference metadata only and never substitutes for selected-device runtime evidence.

Vulkan naming families such as `vkCmd*`, `vkQueue*`, `vkCreate*`, `vkDestroy*`, `vkEnumerate*`, `vkGet*`, `VK_STRUCTURE_TYPE_*`, `*_BIT`, and `Vk*` type categories are described according to their role.

## Analysis

VulkanScope exposes the **Analysis workspace** as a separate Overview-parent page opened from a compact Overview destination card. Analysis-only state is created while the page is active, and the workspace remains local and evidence-based. Analysis tools do not rewrite the canonical Vulkan capability report, TXT/HTML output, or Database submission evidence.

The current workspace provides dedicated views for **Compare**, **Search**, **Drivers**, **Diagnostics**, **Requirements**, **Minimums**, **Graph**, **Presentation**, **Raw JSON**, **Database**, **History**, **Watched**, **Quality**, **Share**, and **Tests**.

### Evidence provenance and global report search

Ordinary key/value evidence can be opened in an evidence/provenance inspector with explicit local actions such as copy, share, add-to-watch-list, and Encyclopedia lookup. Registry/reference metadata remains separate from selected-device runtime evidence.

Global report search operates on the completed current-device evidence model and can search across:

- Extensions
- Features
- Properties
- Limits
- Formats
- Surface/WSI evidence
- Android display/HDR evidence
- Queue evidence
- Profile evidence
- Query evidence

Large result sets remain bounded and lazily presented.

### Snapshot comparison

- Export and import bounded `VulkanScopeAnalysisSnapshot1` snapshots
- Compare a saved baseline against the current device/driver report entirely offline
- Search and filter resulting evidence differences
- Optionally include unchanged evidence
- Keep unknown/unavailable evidence distinct from unsupported
- Validate snapshot schema, total size, entry count, key length, and value length before use

Difference and regression-candidate labels describe evidence changes only; they are not Vulkan conformance, performance, or driver-quality judgments.

### System Vulkan ↔ Turnip A/B

Analysis can retain completed System Vulkan and Turnip sessions in bounded private local history and compare their evidence. A guided workflow can switch between the two sources using VulkanScope's normal explicit driver-change and collection path.

The A/B view compares collected evidence only. It does not rank drivers, infer performance, or treat a difference as proof of a defect.

### Collection diagnostics and requirement resolution

Collection diagnostics expose explicit report/query/safety state together with app-side elapsed timing for supported query groups. Missing native per-query timing is not fabricated.

The requirement resolver evaluates referenced Vulkan API/extension requirements against authoritative runtime evidence token by token. Registry dependency expressions remain reference metadata and are not converted into inferred global support.

### Minimums and profile analysis

The Analysis workspace can reuse VulkanScope's existing profile evidence and can also evaluate bounded custom minimum profiles with **PASS / FAIL / UNKNOWN** results.

Custom minimum profiles support `VulkanScopeMinimumProfile1` JSON import/export. Large integer Vulkan limits are compared using exact decimal handling so 64-bit values are not rounded through floating-point conversion. Missing evidence remains `UNKNOWN` rather than being converted into failure.

### Extension dependency analysis

VulkanScope includes a generated Khronos registry extension-reference catalog for offline dependency inspection. Dependency traversal is cycle-safe and bounded, and the visual graph is deliberately limited for legibility. Registry relationships are shown separately from actual runtime extension enumeration and query results.

### Raw structured technical report

Analysis can inspect the complete schema-v3 `technicalReport` as a searchable structured tree and can explicitly export it as JSON.

This is a user-initiated local operation. Building or exporting the raw structured report does not introduce a background upload path.

### VulkanScope Database comparison

A public VulkanScope Database report can be fetched explicitly by its validated 64-character report ID from the fixed official HTTPS Database origin. The returned `technicalReport` is compared locally against the current structured report.

Database comparison is an evidence comparison only; it is not a device ranking or market-share statement. Fetching is disabled while Android does not report a validated internet connection.

### Local session history

Completed Analysis sessions can be retained in bounded compressed private app storage. History entries can be reused as a comparison baseline or deleted explicitly.

Local history is not uploaded automatically.

### Watched evidence

Selected capability/evidence tokens can be stored in a persistent local watch list. The watch list stays on-device and is bounded to **256 entries**.

### Diagnostic evidence score

The local diagnostic evidence score is derived only from explicit collector errors and query-safety rejections. It is **not** a Vulkan conformance result, benchmark, GPU ranking, performance score, or vendor-quality score. Missing capability evidence is not penalized by inference.

### Local sharing

The Analysis workspace can present the official VulkanScope Database permalink for the most recently submitted report, share or copy the link through Android, and generate its QR code locally. QR encoding does not use a remote QR service.

### Optional dedicated-process Vulkan self-tests

The Analysis workspace can explicitly run minimal Vulkan diagnostics in the dedicated probe process for:

- `VkDevice` creation
- SPIR-V shader-module creation
- Pipeline-layout creation
- Minimal compute-pipeline creation

These tests use the selected Vulkan driver path, require no optional Vulkan feature or extension, do not dispatch GPU work, and create only the minimal required Vulkan objects. If a safe matching path is unavailable, VulkanScope reports `UNAVAILABLE` instead of guessing success. Self-test results remain separate from normal capability collection and do not rewrite features as supported or unsupported.

Analysis import/export actions open the system document picker first. If the picker cannot be launched, VulkanScope uses bounded app-specific/private fallback locations without requesting broad storage permission and reports the saved path where applicable.

## Registry-driven query system

VulkanScope bundles offline registry-derived metadata used to organize and validate feature/property probing.

The system includes:

- Physical-device feature metadata
- Physical-device property metadata
- Extension requirements
- Core promotion information
- Dependency metadata
- Query manifests
- Validated native query mappings
- Coverage verification tooling

The registry metadata is bundled with the application and is not downloaded at runtime. The generated extension-reference catalog also feeds the Analysis dependency view, while runtime support decisions continue to come only from actual Vulkan API/extension/query evidence.

## Turnip & third-party Vulkan drivers

VulkanScope supports compatible Android environments where an alternative Vulkan implementation such as Turnip is loaded explicitly.

Settings uses a unified **Driver manager** for:

- **System Vulkan**
- Up to **10** privately managed Turnip / third-party driver slots
- Explicit import
- Package/details inspection
- Explicit activation
- Explicit removal

Importing a driver package never activates it automatically. System Vulkan and Turnip are mutually exclusive active modes in the interface, and source changes require explicit user action/confirmation.

Imported driver bundles are validated before use. Installed-bundle resolution is metadata-authoritative: a valid bundle requires exactly one bounded `meta.json`, `schemaVersion` 1, exactly one declared Vulkan `.so`, containment inside the app-private driver path, and a readable non-empty declared library. Arbitrary "first `.so` in the archive" fallback loading is not used.

The application also applies path validation so the selected library cannot escape the private imported-driver directory. ZIP import is bounded before and during decompression: raw/compressed archive input is capped at **96 MiB**, with a maximum of **2048 entries**, **32 MiB per extracted file**, and **64 MiB aggregate extracted data**, together with path-length, duplicate-path, canonical-containment, and private-temporary-directory checks. Turnip import is gated to Android 9+ `arm64-v8a` together with the existing runtime checks.

VulkanScope preserves bounded source-package provenance privately for managed Turnip slots, including available source ZIP metadata and authoritative package metadata. Source-document provenance is excluded from `technicalReport`, TXT/HTML reports, Analysis snapshots, and VulkanScope Database submission.

The system document picker is attempted first for Turnip import. If it cannot be launched, fallback discovery is confined to VulkanScope app-specific/private locations and exact `turnip_01.zip` through `turnip_10.zip` filenames. No broad/all-files storage permission is requested.

Mesa-style variables such as `VK_DRIVER_FILES` / `VK_ICD_FILENAMES` can be configured before the loader is opened when required by the selected driver setup.

Actual third-party driver compatibility depends on the Android device, ABI, loader, and imported driver package.

## Query safety and evidence semantics

VulkanScope treats driver-controlled Vulkan enumeration counts and incomplete results as untrusted runtime evidence. Applicable multi-stage queries revalidate returned counts before indexing or resizing buffers, and bounded retry/size rules are used for enumerations such as device extensions, physical devices, queue families, Vulkan tools, Vulkan Video formats, device groups, Surface formats/present modes, cooperative-matrix properties, and Sparse Image Format Properties2.

`VK_INCOMPLETE` remains partial positive evidence. Failed or unavailable second-stage queries are not emitted as complete support, and native queue, memory, and Surface safety-rejection evidence remains visible through the detailed report pipeline.

Dedicated-probe publication is bounded to **64 MiB**. Normal intermediate checkpoint polling is metadata-only; complete JSON is materialized only at terminal, crash, or timeout-recovery boundaries, and terminal state is validated with strict streaming JSON parsing. Missing terminal status is not fabricated as `Unavailable`. Background detail collection has a **60-second** total budget so incomplete optional detail work cannot keep collection alive indefinitely.

## Reports

VulkanScope can export the complete collected technical report as:

- **TXT**
- **HTML**

Analysis can additionally export the schema-v3 **`technicalReport` JSON** explicitly for structured inspection/comparison. Analysis snapshots and custom minimum profiles have their own bounded JSON formats and are not substitutes for the canonical complete report.

Reports can contain:

- Application/version metadata
- Device and driver information
- Core and extension features
- Detailed queried properties
- Limits
- Instance/device extensions
- Layers and layer extensions
- Memory heaps/types
- Queue families
- Vulkan Video codec-operation information
- Formats and exact feature masks
- Surface/WSI data
- Display/HDR information
- Vulkan Profile results
- Registry/query coverage metadata

HTML reports are self-contained and use semantic status styling. Properties & Limits reporting also separates normal Vulkan property/query rows from mandatory query-safety diagnostic rows so safety evidence is not misrepresented as additional Vulkan properties.

Canonical names are accompanied by raw values where appropriate so the report remains useful for specification-level verification.

## VulkanScope Database

VulkanScope can explicitly submit a complete technical report to the VulkanScope Database.

Submission is **opt-in**: no hardware report is uploaded until the user chooses the database submission action.

The structured technical payload preserves the same underlying data model used by TXT/HTML reporting, including exact 64-bit values as decimal strings where needed to avoid precision loss.

Submission keeps the distinction between:

- Supported
- Unsupported
- Available
- Unavailable
- Incomplete
- Not applicable
- Unknown / not queried

The report is not intentionally truncated to make it fit a transport limit; an oversized submission fails rather than silently dropping capability data.

VulkanScope **1.0.19** uses **VulkanScope Database 1.0.8** as its companion Database. Submission remains **schema 2 / technicalReport 3**, with Database normalizer **16**. New submissions require VulkanScope **0.80.3 or newer**; historical stored reports remain readable.

VulkanScope can also present the official Database permalink and generate a QR code locally for a submitted report. QR encoding is performed on-device; no remote QR-generation service, analytics endpoint, or automatic report upload is introduced. Report identifiers are validated as lowercase SHA-256 hexadecimal identifiers.

Analysis can also explicitly fetch a public compact report from the same fixed official HTTPS origin by validated report ID and compare its returned `technicalReport` locally with the current report. VulkanScope 1.0.19 uses versionCode **1019**, matching the Database 1.0.x producer-identity contract.

## Update system

VulkanScope can check the official GitHub releases for application updates.

The **Direct GitHub updates** setting defaults to enabled. It can be disabled in Settings when **Obtainium** is used as the single external update manager. Disabling direct updates prevents VulkanScope from performing its own startup update check or downloading update APKs.

Direct update actions are available only while Android reports a validated default network. Update discovery, review, and download remain separate from Vulkan hardware collection.

Before an APK is handed to Android's installer, VulkanScope validates the update candidate, including applicable package identity, release/version consistency, version progression, and signing-certificate identity. An APK that does not match VulkanScope's expected application identity is not treated as a valid update.

APK download still requires explicit review and confirmation. Release notes shown during the update flow are bounded, inert text from the accepted official GitHub release metadata.

Obtainium can track the universal APK from the official VulkanScope GitHub Releases source without enabling VulkanScope's built-in updater.

## Security & privacy

VulkanScope is designed so capability inspection itself remains local.

Network access is limited to explicit network-backed features such as:

- VulkanScope Database submission and explicit public-report lookup
- Official GitHub release update checks/downloads
- User-initiated external documentation/project links

Security-related design choices include:

- Explicit report submission with a fixed HTTPS Database endpoint and redirects disabled
- No automatic/background hardware-report upload
- Cleartext traffic disabled and application backups disabled
- Non-exported FileProvider and dedicated Vulkan probe service
- Bounded APK download, report, probe, Analysis snapshot, and driver-import sizes
- **96 MiB** raw Turnip archive input ceiling, **2048-entry**, **32 MiB/file**, and **64 MiB extracted-total** limits
- Canonical-path, duplicate-path, and private-directory validation for imported driver files
- Exact metadata-declared imported-driver library selection
- APK package/signature/version verification for updates
- Native linker hardening, stack protection, RELRO/NOW
- No guessed Vulkan structure layouts or `sType` values
- Bounded second-stage Vulkan enumeration handling with returned-count revalidation
- Explicit preservation of partial `VK_INCOMPLETE` evidence
- **64 MiB** probe publication ceiling, metadata-only intermediate polling, and strict streaming terminal validation
- **60-second** background-detail budget
- **2 MiB** fail-closed Database transport ceiling; reports are not silently truncated
- **8 MiB** Analysis snapshot ceiling with bounded schema/entry/key/value validation
- Local-only Encyclopedia search, QR generation, Analysis snapshot processing, local session history, and custom minimum evaluation
- System document picker attempted first for supported imports/exports, with bounded app-specific/private fallbacks only when launch is unavailable
- No broad/all-files storage permission for Turnip or Analysis fallback exchange flows
- Turnip source-document provenance kept app-private and excluded from technical reports, Analysis snapshots, and Database submissions
- Validated-default-network gating for Internet-backed actions without synthetic HTTP/DNS connectivity probes
- Optional dedicated-process Vulkan self-tests with deterministic cleanup
- No WebView/JavaScript bridge in the capability/reporting path

Users should still review imported third-party Vulkan driver packages before using them.

## Supported ABIs

Native builds are provided for:

| ABI | Status |
|---|---|
| `arm64-v8a` | Supported |
| `armeabi-v7a` | Supported |
| `x86_64` | Supported |
| `x86` | Intentionally excluded |

## Android and build baseline

VulkanScope 1.0.19 uses the current project baseline:

- **Compile SDK:** Android API 37
- **Target SDK:** Android API 37
- **Minimum SDK:** Android API 24
- **Android Gradle Plugin:** 9.4.0
- **Kotlin Compose plugin:** 2.4.10
- **Gradle wrapper:** 9.7.1
- **JDK:** 17+
- **NDK:** 29.0.14206865 (r29)
- **CMake minimum:** 3.22.1
- **Native language level:** C++20
- **Vulkan-Headers:** 1.4.362, pinned commit `ee2ec5fd83dafce291024683b50dc89219333076`

The project uses CMake for the native Vulkan collector.

### Build

From the project root:

```bash
./gradlew assembleRelease
```

On Windows:

```bat
gradlew.bat assembleRelease
```

An Android SDK/NDK installation matching the project configuration is required.

## Requirements

- Android device with a Vulkan-capable implementation
- Compatible Android version for the installed APK
- A real device is recommended for meaningful hardware/Surface/display inspection

The exact data available depends on the Vulkan loader, GPU driver, Android framework, display stack, and device configuration.

Virtualized Android environments may expose synthetic or incomplete GPU/display information and are not considered supported hardware targets.

## Branding

The application uses the VulkanScope branding and approved project logo assets stored in the repository.

The master launcher artwork is kept at:

`app/src/main/res/drawable-nodpi/vulkanscope_logo_master.png`

## Open source

VulkanScope is open-source software.

Repository:

**https://github.com/EFIShell0/VulkanScope**

Bug reports, testing feedback, and contributions are welcome.

## Third-party components

VulkanScope 1.0.19 reports its direct application/native library identities in the in-app **Info → Libraries** section. Current release pins include:

- AndroidX Core KTX **1.19.0**
- AndroidX Activity Compose **1.13.0**
- Compose UI / Foundation / Animation **1.12.0**
- Material 3 **1.5.0-alpha27**
- Lifecycle Runtime Compose **2.11.0**
- OkHttp **5.5.0**
- ZXing Core **3.5.4**
- Vulkan-Headers **1.4.362**, pinned commit `ee2ec5fd83dafce291024683b50dc89219333076`
- libadrenotools pinned commit `8fae8ce254dfc1344527e05301e43f37dea2df80` for `arm64-v8a` driver-loading integration

Build-tool identities are displayed separately in Info and are not presented as runtime libraries. Each third-party component remains subject to its own license, copyright notice, and upstream terms.

---

## VulkanScope

**Inspect your GPU. Inspect your driver. Inspect your Vulkan implementation.**
