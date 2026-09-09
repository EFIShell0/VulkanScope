## 1.0.19
- Replaced the Direct GitHub Updates / Check for updates artwork with a simple downward-arrow download glyph while keeping Turnip ZIP import on the ZIP-folder/download glyph.
- Changed only horizontal filter-carousel arrow colors to VulkanScope red containers with white chevrons; vertical scroll arrows, geometry and motion are unchanged.
- Added queue+shield artwork for Queue query safety, Surface+export artwork for complete report export, TXT/HTML badges for their export actions, and a compass for Explore.
- Retained Vulkan 1.4.362 collection/report semantics, Database 1.0.8 compatibility, fixed HTTPS update/Database routes and the 1.0.18 compile-safe filter callback binding.

## 1.0.18

- Fixes the release Kotlin compile regression in `PhysicalDeviceSelector` by binding the `ExpressiveFilterBar` callback through the named `onSelected` parameter after the `arrowTint` default parameter was introduced.
- Extends the compile-regression gate so the same positional-callback/type-mismatch defect cannot pass source verification again.
- Performs a full retained security, memory/resource, crash/recovery, Vulkan registry/query, Database transport, UI/accessibility and package-regression audit without changing Vulkan collection or report semantics.
- Revalidates Vulkan 1.4.362/header 362 against the current Khronos registry published 2026-09-04 and keeps Database 1.0.8 schema 2 / technicalReport 3 / normalizer 16 compatibility.
- Keeps all 1.0.17 semantic icon and design corrections unchanged.

## 1.0.17

- Restores the Semih Boran developer identity-row icon to the existing person glyph while keeping the `</>` code glyph only on the Developer section header.
- Converts the shipped VulkanScope launcher/identity foreground artwork to transparent-white presentation by removing opaque black artwork pixels while preserving the white VulkanScope wordmark; the SCOPE Application header wordmark remains unchanged.
- Uses one ZIP-folder + download composite glyph for Check for updates, Direct GitHub update presentation and Turnip Import driver ZIP without changing update/download/import behavior.
- Recolors only the Core-version source-filter carousel arrows to the VulkanScope red accent; geometry, navigation and selection behavior are unchanged.
- Uses the Display HDR badge for Quick access HDR & Color, a magnifier for Global Vulkan report search, Display+Surface composite artwork for presentation evidence, registry+JSON treatment for Raw structured technical Report, and Database+Compare composite artwork for Database comparison.
- Corrects the Analysis section title from `Raw structured technicalReport` to `Raw structured technical Report` while preserving the canonical schema key `technicalReport` everywhere report serialization or Database interchange requires it.
- Revalidates VulkanScope Database 1.0.8 compatibility for explicit schema-2 / technicalReport-3 POST submission and compact report lookup; normalizer 16, Vulkan 1.4.362, fixed HTTPS origin, 2 MiB fail-closed ceiling, report privacy and no-background-upload rules remain unchanged.

## 1.0.16

- Replaces the Info/Application section glyph with the exact SCOPE wordmark cropped from the already shipped VulkanScope horizontal logo and uses the shipped adaptive-launcher foreground artwork beside the VulkanScope application identity.
- Replaces Developer person glyphs with a code `</>` symbol, gives Memory heaps and Memory types distinct RAM symbols, and gives Run Vulkan self-tests a chip/check diagnostic symbol.
- Gives Check for updates a dedicated downward-arrow glyph without the lower tray/short line while retaining the separate download/tray glyph in the update-download confirmation flow.
- Keeps all requested artwork local and packaged; no runtime image generation, font substitution, icon-network dependency or capability inference is introduced.
- Revalidates VulkanScope Database 1.0.8 compatibility for explicit schema-2 / technicalReport-3 POST submission and bounded compact report lookup, retaining normalizer 16, the 2 MiB fail-closed transport ceiling and fixed official HTTPS origin.
- Retains Vulkan 1.4.362/header 362, native collection, report serialization, Turnip/update security, three release ABIs and existing privacy/resource ceilings unchanged.

## 1.0.15

- Fixes false `SAF unavailable` fallback routing by removing PackageManager/default-handler and Android-TV availability guesses for system document pickers.
- Turnip import, Analysis snapshot/minimum-profile import/export, raw structured technicalReport export and complete TXT/HTML export now attempt the registered system document picker first and use bounded fallback only when picker launch throws ActivityNotFoundException or SecurityException.
- Keeps user cancellation as a normal picker result rather than treating it as missing SAF, narrows picker-launch exception handling, and replaces false global SAF-unavailable wording with truthful document-picker fallback messaging.
- Preserves exact bounded fallback roots/filename families, no broad storage permission, existing export-path toasts, Vulkan 1.4.362/header 362, schema 2/technicalReport 3, Database 1.0.2, native collection and security/resource ceilings.
- Restores the 1.0.14 compile-fix suite to the aggregate quality gate and adds 1.0.15 launch-first failing-before-fix, state-machine and negative-mutation coverage.
- Separates Share link from Copy link with a semantic share glyph, gives Database permalink & QR a dedicated QR glyph, and adds monitor-based HDR/MODE/Surface badges to the Display/HDR section headers without changing capability semantics.
- Adds finite 320 ms click animations to the five primary navigation icons in both the bottom bar and landscape/TV rail, with distinct home/Vulkan/Surface/Display/Extensions motion and no background or infinite animation.

## 1.0.14

- Fixes the release-blocking Kotlin type mismatch in the no-SAF Analysis/minimum-profile fallback import dialog by passing the selected candidate's validated `File` to the existing `(File) -> Unit` callback.
- Gives the first-install Direct-GitHub-disabled/Obtainium information banner a semantic GitHub source icon instead of the generic INFO badge, without changing update opt-in, network or download behavior.
- Retains Vulkan 1.4.362/header 362, schema 2/technicalReport 3, Database 1.0.2, SAF fallback bounds, update validation, native collection and security/resource ceilings.

## 1.0.13

- Recolors the supplied Android-head artwork into VulkanScope's established red/tone icon palette while retaining the exact supplied geometry and two-tone detail.
- Replaces the generic update-available information treatment with a dedicated update-available glyph and a contained Review affordance using the same right-chevron interaction pattern as Details.
- Gives the update confirmation dialog a semantic update glyph, keeps Cancel as the uncontained X action, and makes the positive Download update action a contained download-icon button while retaining validated-network gating and explicit APK validation.
- Differentiates VulkanScope Database actions: public-report fetch uses a download/database glyph, complete-report submission uses upload/database, browsing uses database/search, comparison cards use compare, and permalink/QR uses link.
- Retains Vulkan 1.4.362/header 362, submission schema 2, technicalReport schema 3, Database 1.0.2, SAF/no-SAF fallback behavior, native Vulkan collection, report semantics, active-driver exclusivity and security/resource ceilings.

## 1.0.12

- Fixes Storage Access Framework availability detection by resolving the launch-equivalent default document handler with `MATCH_DEFAULT_ONLY` at action time, while preserving Android TV and launch-failure fallback behavior.
- Adds Turnip-style bounded no-SAF selection dialogs for Analysis snapshot and minimum-profile JSON imports. Candidates remain confined to VulkanScope app-specific Documents, app-specific Download and private `files/analysis_exchange` roots with existing filename/size ceilings.
- Makes no-SAF Analysis snapshot, minimum-profile and raw structured technicalReport exports show a toast containing the exact saved path; complete TXT/HTML public-Download fallback keeps its existing result toast.
- Keeps Turnip fallback import limited to exact `turnip_01.zip` through `turnip_10.zip` in the existing bounded app-specific/private roots and routes it through the shared launch-equivalent SAF check.
- Fixes the dark `Fallback Turnip import` title by using the explicit primary text color and adds the semantic import glyph; the new Analysis fallback dialogs use the same title treatment.
- Replaces the generic Android outline with a packaged VectorDrawable conversion of the supplied Android-head artwork, preserving its green/dark artwork and rendering it without Vulkan-red tint.
- Simplifies the Updates / Check for updates glyph to a download arrow and tray by removing the unwanted outer circular-arrow path.
- Retains Vulkan 1.4.362, submission schema 2, technicalReport schema 3, Database 1.0.2, native Vulkan collection, report semantics, Turnip package validation, ABI/dependency pins and storage/security ceilings.

## 1.0.11

- Prevents System Vulkan and Turnip from being shown as active at the same time: a Turnip slot is visually ACTIVE only while Turnip is the current driver mode, with mode changes forcing manager refresh and stale selection suppression.
- Makes Local session history `Use as baseline` and `Delete` use equal-width contained Material 3 Expressive actions with matching geometry; `Use as baseline` gains a dedicated baseline/flag glyph.
- Aligns Overview Encyclopedia and Analysis destination chevrons with the established Vulkan-red trailing action affordance while keeping the chevron as the only activation target.
- Replaces generic information glyphs with semantic packaged vector icons across Developer, Android, Application, Updates, Encyclopedia, Analysis, Profiles, libraries, build/ABI/registry/history/comparison/test/export and related actions.
- Gives Check for updates and Settings Updates a dedicated update/download-history glyph; Analysis import/export, A/B, save, self-test, copy/share/watch and Encyclopedia actions use matching semantic icons.
- Retains Vulkan 1.4.362/header 362, schema 2/technicalReport 3, Database 1.0.2 compatibility, no-SAF bounds, validated-network gating, Turnip package security and the 1.0.10 PaddingValues compile repair.

## 1.0.10

- Fixes the release-blocking Kotlin compilation failure in `ExpressiveContainedIconTextButton` by importing `androidx.compose.foundation.layout.PaddingValues` for the existing 16 dp horizontal / 8 dp vertical content padding.
- Preserves all 1.0.9 driver-manager, Analysis, no-SAF, Database, detail-dialog, action-target, report, Vulkan 1.4.362 and security behavior unchanged.
- Records the supplied immutable-1.0.9 `:app:assembleRelease` failure at `MainActivity.kt:10199:26` as a compile regression and adds a dedicated failing-before-fix/negative-mutation gate.

## 1.0.9

- Repairs shared detailed-evidence dialog sizing so long Extensions/reference content cannot squeeze the Close footer into a clipped sliver; contained icon/text actions now use a 48 dp minimum target with vertically aligned glyph and label.
- Makes trailing chevrons the sole activation target for chevron-ended action/navigation surfaces, including Overview destinations, Analysis evidence actions, update checks and other shared action rows.
- Adds retained System Vulkan driver evidence/details from completed System collections without reusing Turnip evidence, while preserving explicit System/Turnip activation semantics.
- Contains Evidence provenance, Add to watch list and Share Copy link actions with dedicated evidence/watch/link glyphs, and adds visible press feedback while holding evidence rows for their long-press inspector.
- Locks Database report-id entry and fetch while validated internet is unavailable and shows the reason in amber.
- Expands every no-SAF import/export explanation with the exact bounded filename family and app-specific/public fallback location used by the implementation.

## 1.0.8

- Reworked Settings into one Driver manager with System Vulkan and managed Turnip activation state, explicit confirmation affordances and safer unavailable-slot removal presentation.
- Improved Overview active-driver emphasis, Analysis status color semantics, local-history actions, evidence actions and external-link affordances.
- Added bounded app-specific import/export fallback behavior for Analysis JSON flows when no Storage Access Framework document provider is available; report exports retain Downloads fallback.
- Standardized question dialogs, Cancel X actions, contained destructive/confirmation actions and external-link icon handling across the affected surfaces.

## 1.0.7
- Simplifies driver-source Settings so System Vulkan driver remains the only source selector while Turnip driver manager stays as a separate persistent section below it.
- Shows only an amber UNAVAILABLE explanation when Turnip eligibility is unsupported or cannot be established; installed metrics, import controls and slot rows are hidden in that state.
- Separates managed-slot state into ACTIVE, AVAILABLE and UNAVAILABLE using validated private-driver state plus recorded Source ZIP reachability without changing Vulkan capability evidence.
- Keeps collection and validated-network state independent so offline/connected transition feedback remains visible during collection and Database submission explains collection-only, network-only or combined locks.
- Reworks shared filter carousels with reserved edge-control space, surface-colored continuation masks and 220 ms arrow/mask state motion instead of abrupt black edge clipping.
- Adds trash iconography to Turnip Remove, a shared info glyph to Extensions/detail headers and an X glyph to contained Close actions while retaining the established Details affordance.
- Preserves Vulkan 1.4.362/header 362, schema 2/technicalReport 3, Database 1.0.2 compatibility, Turnip package safety bounds, three release ABIs and existing report/security semantics.

## 1.0.6
- Fixed Turnip source selection so returning from System to an already active Turnip package requests the real driver switch and the selector reflects the confirmed active mode.
- Added mandatory confirmation for System/Turnip source switches and managed Turnip activation; active-driver removal now explicitly discloses System fallback.
- Added an amber collection-time explanation below the driver selector describing why driver changes are locked during report collection.
- Reworked default-network transition handling to consume ordered NetworkCallback evidence so repeated genuine connection-loss events reliably produce Disconnected notifications without callback-time capability re-query races.
- Replaced the harsh filter-carousel black edge block with a softer multi-stop continuation shadow while retaining arrows, RTL, TalkBack and TV behavior.
- Aligned Turnip Details with the shared Extensions Details affordance and moved Turnip Remove plus common dialog Close actions into the Vulkan accent container.
- Retained Vulkan 1.4.362/header 362, report schemas, Database 1.0.2 compatibility, Turnip archive/provenance security bounds and the three-ABI release policy.

# VulkanScope 1.0.5

- Fixed the release-blocking Kotlin call-site regression in the direct-update consent and update-confirmation dialogs.
- Both Cancel actions now pass `onDismiss` explicitly as the `onClick` named argument to `ExpressiveTextButton`, preserving its default `enabled = true` parameter.
- No runtime Vulkan query, report, Database, Turnip, connectivity, updater-validation, resource-ceiling or dependency behavior changed from 1.0.4.

# VulkanScope 1.0.4

- Fixes connectivity-status exit rendering so a green Connected notice never changes into a disconnected notice while its exit animation is finishing.
- Adds a persistent blue offline information surface whenever Android reports no validated default network; it yields to active Vulkan collection status to avoid competing status banners.
- Disables built-in internet-dependent actions while offline, including Database submission/fetch/browsing, GitHub/Khronos web links and direct update checks/download confirmation, with fail-closed request-time network validation.
- Replaces direct Turnip-picker behavior with a private 10-slot Turnip driver manager for import, details, activation and removal. Import never activates a driver automatically.
- Persists each imported ZIP's bounded source provenance beside its private driver slot so ZIP name, source location, size, modified time and import time remain available after restart without entering technical reports or Database submissions.
- When SAF is unavailable, searches only permissionless VulkanScope app-specific locations for exact `turnip_01.zip` through `turnip_10.zip` names and presents selectable results; no all-files permission is requested.
- Adds edge fade/shadow treatment to shared Material 3 Expressive filter carousels so partially clipped Core/filter chips have a deliberate scroll affordance while preserving RTL, TalkBack and TV focus behavior.
- Preserves Vulkan 1.4.362/header 362 and all canonical collector/report evidence semantics; the 2026-09-07 upstream recheck still reports Vulkan Registry 1.4.362 dated 2026-09-04.

# VulkanScope 1.0.3

- Fixes the release-blocking Kotlin compilation errors caused by unsupported `\s`, `\.`, and `\d` escapes in four regular-expression literals introduced before 1.0.2 packaging.
- Converts only those regex literals to Kotlin raw strings, preserving their intended matching semantics for custom minimum API/limit parsing and Vulkan core dependency parsing.
- Preserves all 1.0.2 Material 3 Expressive filter, network-state, Turnip metadata, TalkBack/RTL/TV, Vulkan 1.4.362, report, Database, security and resource behavior unchanged.
- Keeps companion VulkanScope Database 1.0.2 and schema 2 / technicalReport 3 unchanged.

# VulkanScope 1.0.2

- Replaces chip-style filter strips throughout the app with a shared Material 3 Expressive carousel that has explicit left/right controls, RTL-aware direction, selected-item auto-visibility, Android TV focus compatibility and accessible arrow descriptions.
- Replaces flat evidence/property/query/limit count sentences with responsive expressive metric cards that adapt to narrow displays and enlarged text.
- Adds transient validated-network state notifications using Android's default-network capabilities only: green connected and red disconnected states use distinct icons, polite live regions and a bounded 4.5-second display without an HTTP/DNS probe.
- Expands the Turnip bundle surface with selected ZIP name/SAF location/size/modified time/import time plus authoritative AdrenoTools package metadata, driver version/date when declared, min API and Vulkan library details.
- Keeps Turnip source-document provenance app-private and out of technicalReport, TXT/HTML, Analysis snapshots and VulkanScope Database submission; missing package metadata remains explicitly unavailable rather than inferred.
- Retains Vulkan 1.4.362/header 362, 304 providers, 110 structs, 104 query groups, 47 Vulkan Video exact profiles, schema 2 / technicalReport 3, three release ABIs and existing Turnip/update/report security bounds.
- Updates the companion VulkanScope Database producer identity to 1.0.2 / versionCode 1002 without a D1 schema, normalizer or historical report rewrite.

# VulkanScope 1.0.1

- Makes bounded local Analysis history storage fail closed on directory/write/rename/read/delete I/O failures instead of allowing storage exceptions to escape the workflow.
- Uses exact decimal comparisons for custom minimum limits so 64-bit Vulkan values above 2^53 are not rounded through `Double`.
- Rejects invalid feature expectation tokens as Unknown and refuses ambiguous feature/limit suffix matches until an exact evidence key is supplied.
- Preserves Unknown/Incomplete/Unavailable/Not applicable states in dependency runtime evidence instead of reporting false-negative API/extension absence.
- Builds the heavy schema-v3 technicalReport leaf tree only for Raw JSON/Database Compare or explicit export.
- Resolves one watched-evidence writer per explicit action and corrects Query Diagnostics timing wording.
- Replaces the shared horizontally hidden filter strip with responsive Material 3 Expressive `FlowRow` wrapping for narrow, large-text, translated and RTL layouts.
- Keeps Vulkan 1.4.362/header 362, 304 providers, 110 structs, 104 query groups, 47 Vulkan Video profiles, report schema 2/technicalReport 3 and established security/resource ceilings unchanged.
- Updates the companion VulkanScope Database to 1.0.1.

# VulkanScope 1.0.0

- Adds an evidence/provenance inspector to ordinary key/value evidence with explicit Copy, Share, Watch and Encyclopedia actions; registry/reference metadata remains separate from runtime support evidence.
- Adds complete-current-report global search across extension, feature, property, limit, format, Surface, display, queue, profile and query evidence with bounded lazy results.
- Adds a guided and manual System-driver ↔ Turnip A/B workflow backed by private bounded local session history; differences are evidence changes rather than performance or quality rankings.
- Adds collection diagnostics with explicit report/query/safety state plus app-side elapsed timing for dedicated query groups collected during the current session; missing native per-query timing is never fabricated.
- Adds an extension/API requirement resolver that evaluates referenced tokens individually against authoritative runtime evidence without turning registry dependency expressions into inferred global support.
- Extends Format Explorer with combinable usage/capability filters for sampled/storage/attachment/filtering/blit/transfer/video/YCbCr paths.
- Adds a Vulkan Video Matrix combining each retained exact profile with matching queue-operation evidence, bounded sampled-format evidence and retained capability properties without promoting associations into codec-wide support.
- Adds a Surface + Android Display presentation-evidence view that reports compatible/separate evidence paths without claiming end-to-end HDR/gamut presentation support.
- Adds bounded local custom minimum profiles with PASS/FAIL/UNKNOWN evaluation and `VulkanScopeMinimumProfile1` JSON import/export.
- Adds a searchable raw schema-v3 `technicalReport` tree and explicit structured JSON export.
- Adds explicit fixed-origin VulkanScope Database report fetch + local structured comparison against the current technical report; no background report download or upload is introduced.
- Adds private compressed bounded session history with local baseline reuse and deletion.
- Consolidates shared Material 3 Expressive evidence rows, filters, metrics, status surfaces, toggle rows, detail dialogs and scroll hints so new analysis tools reuse one UI system.
- Preserves Vulkan 1.4.362/header 362, 304 provider extensions, 110 structs / 104 query groups, 47 exact Vulkan Video profiles, Encyclopedia 842/6248/2461/476, schema 2 / technicalReport 3, Database 1.0.0 compatibility and existing security/privacy/resource ceilings.

# VulkanScope 0.80.15

- Moves shared scroll-boundary hints back over page content instead of permanently reserving a right-side lane, restoring full content width on phone layouts.
- Enlarges the shared up/down arrows and makes them activity-aware: the Up hint overlays the viewport top, the Down hint overlays the viewport bottom; top=Down, middle=Up+Down, bottom=Up. Hints fade after scrolling stops and return when scrolling resumes.
- Reworks Extension/Format detail dialogs into compact grouped Material 3 Expressive evidence cards with responsive stacked rows on narrow/large-text/long-string layouts and a bounded sticky Close action.
- Replaces the horizontally clipped Overview Explore strip and fixed four-column Quick access phone grid with width-aware wrapping grids.
- Preserves Vulkan 1.4.362/header 362, 304/110/104 registry-query coverage, 47 Vulkan Video profile combinations, schema 2 / technicalReport 3, Database 1.0.0 compatibility and all report/security/privacy semantics.

# VulkanScope 0.80.14

- Fixed the release-blocking Kotlin compile regression introduced in 0.80.13 by removing invalid package-level imports for `calculateTopPadding` and `calculateBottomPadding`.
- Preserved the existing `PaddingValues` member calls and all 0.80.13 Material 3 Expressive UI behavior.
- No Vulkan, report, Database schema, native query, security, privacy or capability-state behavior changed.

# VulkanScope 0.80.13

- Reworks long Format/Extension evidence dialogs into responsive custom Material 3 Expressive modal surfaces with a dedicated title/body/action hierarchy and height-aware bounded scrolling.
- Replaces cramped right-aligned key/value columns with width-aware adaptive rows: long labels/values stack and remain left-aligned, while short values retain a compact two-column presentation on sufficient width.
- Flattens detail-dialog evidence rows into one calm divided list instead of nested rounded boxes, while keeping ordinary capability pages on the shared tonal key/value surface.
- Reserves a dedicated edge lane for top-level and modal scroll-boundary indicators so the arrows no longer overlap technical content.
- Moves Details to the shared expressive morphing TextButton treatment, normalizes Format/Extension cards to the common capability-card hierarchy and removes the remaining direct Analysis Switch bypass.
- Extends the Material 3 1.5.0-alpha27 shape scale with largeIncreased, extraLargeIncreased and extraExtraLarge, and routes major shared cards/dialogs through the application shape system.
- Reworks startup loading and empty states into the same dark-neutral / Vulkan-red Material 3 Expressive hierarchy and allows action-card titles/subtitles to wrap instead of truncating important labels.
- Preserves Vulkan 1.4.362/header 362, 304 provider extensions, 110 structs / 104 query groups, the Encyclopedia/Vulkan Video corpora, schema 2 / technicalReport 3, Database endpoint/privacy/security/resource behavior and explicit Details-only activation semantics. Companion Database metadata advances to 1.0.0 without a schema change.

# VulkanScope 0.80.12

- Fixed the release Kotlin compilation failure at the explicit button semantics assignment by importing the Compose `SemanticsPropertyReceiver.role` extension required by `Modifier.semantics { role = Role.Button }`.
- Preserved Vulkan 1.4.362/header 362, Database 0.39.24 compatibility, runtime query/report behavior, UI behavior and existing accessibility roles; this release changes production runtime bytes only for the missing import plus version metadata.

# VulkanScope 0.80.10

- Updates the locked Vulkan registry and Vulkan-Headers baseline to Vulkan 1.4.362 / header 362.
- Adds registry-driven provider coverage for VK_KHR_pipeline_library_group_handles and VK_VALVE_buffer_device_address_allocation_alignment.
- Regenerates the offline Encyclopedia to 842 commands, 6248 VK_* tokens, 2461 Vk* types and 476 registered extensions.
- Keeps explicit native query catalog counts at 110 structs / 104 groups and Vulkan Video at 47 exact profile combinations.
- Updates the companion Database identity to 0.39.24.

# Changelog

## 0.80.9
- Makes Format and Extension detail dialogs open only from the explicit Details button; surrounding cards remain non-actionable browse surfaces.
- Updates the companion Database contract to 0.39.23 with a VulkanScope 0.80.3+ new-submission floor and a Database-native Vulkan Encyclopedia sourced from the same locked Vulkan 1.4.361 reference corpus.
- Preserves the 0.80.8 persistent failed-collection state, AGP 9.4.0, Vulkan query/report semantics, TalkBack/large-text/RTL/TV behavior and security/resource bounds.

## 0.80.8
- Keeps a terminal Failed information-collection banner visible until a new collection replaces it.
- Updates Android Gradle Plugin to stable 9.4.0 while retaining Gradle 9.7.1 and API 37.
- Updates the companion Database contract to 0.39.22 with a VulkanScope 0.80.1+ new-submission floor.

## 0.80.7

- Adds content-aware `TextDirection.ContentOrLtr` to every Material typography role so Latin/Vulkan identifiers remain LTR under RTL system locales while Arabic/Hebrew content can resolve RTL naturally.
- Retains `android:supportsRtl=true`, relative Start/End layout behavior and exact raw Vulkan/report strings without injecting bidi control characters into evidence.
- Keeps the Android platform/default sans family and system glyph fallback; no bundled/downloadable/custom Compose font family is introduced, preserving OEM/user system-font substitution.
- Adds a blue circular Info (`i`) icon to the update-available banner in the same status position used by collection result icons and removes the older green UPDATE badge for that state.
- Preserves 0.80.6 TalkBack/large-text behavior, Vulkan 1.4.361/header 361, 302/110/104 query coverage, the 47-query Vulkan Video census, schema 2 / technicalReport 3 and all prior security/report/TV semantics.

## 0.80.6

- Improves TalkBack semantics by removing redundant decorative announcements, adding section/release-note headings, merging read-only key/value rows and exposing collection/update state changes through accessibility live regions.
- Makes Direct GitHub updates one Switch semantic target and driver source choices one RadioButton semantic target instead of duplicate parent/nested actions.
- Adds large-text/narrow-screen adaptive presentation for Android 14+ font scaling and enlarged display-size settings: Overview metrics stack, Quick Access drops from four to two columns, key/value metadata stacks and Info metadata pills stack.
- Replaces clipping-prone fixed Quick Access/navigation-rail heights with growable minimum heights; large-text rail labels can use two lines, paired Info export actions stack vertically, and collection status text can wrap.
- Uses a scalable textual app-header title under expanded text and reserves update-dialog space with a 220dp release-note viewport while retaining the normal 360dp ceiling otherwise.
- Preserves Vulkan 1.4.361/header 361, 302/110/104 query coverage, the 47-query Vulkan Video census, schema 2 / technicalReport 3 and all prior security/report/TV semantics.

## 0.80.5

- Adds shared up/down scroll-boundary indicators to the update dialog release-notes panel.
- Makes every meaningful release-note row an Android TV read-only focus/BringIntoView target so D-pad traversal drives the standard Compose Foundation LazyColumn scroll path.
- Keeps touch scrolling explicitly enabled and preserves the existing bounded 360dp release-notes viewport.
- Retains the existing Material 3 Expressive update hierarchy: 24dp update banner, 32dp dialog, matching 20dp inner cards and Review / Download APK / Cancel actions.
- Preserves Vulkan 1.4.361/header 361, 302/110/104 query coverage, the 47-query Vulkan Video census, schema 2 / technicalReport 3 and all 0.80.4 collection/detail/security semantics.

## 0.80.4

- Adds a visible `Details` affordance to Format and Extension rows that open full detail views.
- Makes Format and Extension detail dialogs vertically scrollable and adds the same upper/lower boundary indicators used by top-level pages.
- Explicitly preserves touch scrolling on shared lazy pages and makes detail-capable Format/Extension rows participate in the Android TV focus/BringIntoView browse path.
- Removes the `Collecting information...` label from the compact in-progress chip while retaining the activity icon and adjacent status text.
- Adds a distinct failed collection state: timeout/fatal error, incomplete base report, no devices or all-unknown/all-empty device evidence now show an X and `Failed` instead of the successful `Completed` state.
- Preserves Vulkan 1.4.361/header 361, 302/110/104 query coverage, the 47-query Vulkan Video census, schema 2 / technicalReport 3 and existing security/resource/report semantics.

## 0.80.3

- Moves the complete Encyclopedia and Analysis workspace out of Overview into separate Overview-parent destinations; Overview now keeps compact Material 3 Expressive entry cards only.
- Fixes an Encyclopedia search crash caused by generated registry rows containing literal `\\t` text while the runtime decoder expected tab delimiters.
- Makes generated symbol decoding fail closed for malformed rows and renders Encyclopedia matches as top-level lazy items.
- Preserves the locked Vulkan 1.4.361 symbol/reference census, VkResult semantics, runtime evidence, Database schemas and 0.80.0 security/resource hardening.

## 0.80.0

- Full rules-driven security, memory/resource, crash/concurrency, Vulkan specification, Android/toolchain and Material 3 design-integrity re-audit over immutable 0.41.46.
- Bounded raw user-selected Turnip archive input to 96 MiB before ZIP decompression, in addition to the existing 2048-entry, 32 MiB per-file and 64 MiB decompressed-total limits.
- Replaced normal changed-checkpoint full `JSONObject` materialization with metadata-only polling; terminal publications are validated with strict streaming JSON while crash/timeout recovery remains bounded. Missing base status no longer defaults to fabricated `unavailable` terminal evidence.
- Preserved Vulkan 1.4.361/header 361, API 37, NDK r29, report schemas, query coverage, Vulkan Video exact-profile census and the 0.41.46 Material 3 Expressive information architecture.

## 0.41.46

- Vulkan Video page now preserves explicit query failures while presenting retained exact-profile census rows as available query evidence rather than an erroneous Unknown root state.

- Adds a dedicated Vulkan Video page over the existing exact-profile capability, sampled-format and queue evidence, organized into Overview, Decode, Encode, Formats and Queues without broadening support inference.
- Removes the standalone Analysis destination and quick-access button; all Analysis tools now live as a separate lazy `Analysis workspace` immediately below Capability snapshot on Overview.
- Adds common Material-consistent up/down boundary indicators to every top-level scrollable page; the corresponding indicator disappears at the absolute top/bottom and both disappear for non-scrollable content.
- Adds an Info `Libraries` section with exact pinned direct Android/native dependency versions plus a separate build-toolchain section.
- Preserves Vulkan 1.4.361, the locked `vk.xml` + `video.xml` sources, 302/110/104 query coverage, the 47-query Vulkan Video census, schema 2 / technicalReport 3 and all report/Database evidence semantics.

## 0.41.45

- Adds a SHA-256-locked Khronos `video.xml` source alongside the existing locked `vk.xml` and generates Vulkan Video StdVideo profile/level metadata from both registries.
- Replaces the single-profile Vulkan Video capability samples with a bounded 47-query exact-profile census over every registry-defined H.264/H.265/VP9/AV1 codec-specific profile member, including all H.264 decode picture layouts and both AV1 film-grain modes.
- Keeps every capability result scoped to the exact 4:2:0 8-bit profile combination and never infers codec-wide or bit-depth-wide support; non-profile query failures remain Unavailable.
- Presents returned H.264/H.265/VP9/AV1 maximum levels using canonical `STD_VIDEO_*` names while retaining raw numeric values.
- Leaves Vulkan Video format enumeration explicitly sampled to avoid an unbounded Cartesian query expansion and preserves Vulkan 1.4.361, 302/110/104 physical-device query coverage, schema 2 and technicalReport 3.

## 0.41.44

- Replaces profile-name/API-only Vulkan Profiles checks with struct-qualified extension, feature, property, format, inheritance and OR-group evaluation for the normalized Android and Khronos Roadmap requirement sets.
- Corrects Android 15 `subgroupSupportedOperations` to the authoritative BASIC|VOTE|ARITHMETIC|BALLOT|SHUFFLE|SHUFFLE_RELATIVE mask `0x3F`.
- Evaluates Roadmap 2022/2024/2026 mapped requirements instead of API version alone and preserves Roadmap 2026's exact composition without inheriting Roadmap-2024-only promoted-v1.4/line groups.
- Makes FAIL require verified negative evidence, UNKNOWN preserve missing/incomplete evidence, and PASS require explicit complete coverage; partial Android 2025, inherited Android 15/16/17, Roadmap mappings and unpinned catalog-only profiles cannot produce a false PASS.
- Uses one canonical profile-evaluation source across Profiles UI, Analysis, JSON snapshot, TXT and HTML.
- Preserves Vulkan 1.4.361 query coverage, Surface/driver-generation handling, schema 2, technicalReport 3 and all existing report/database evidence semantics.

## 0.41.43

- Rebinds the hidden Android Surface host to each Vulkan driver generation so a driver change no longer starts the successor collection against the previous Surface identity.
- Defers the first complete collection for a changed driver until the replacement SurfaceView publishes a live Surface.
- Binds Surface created/destroyed callbacks to the Surface host generation so late callbacks from the previous driver host cannot become current or clear a newer Surface.
- Applies the same Surface rebind to forced same-mode Turnip package activation while leaving an unchanged driver selection as a no-op.
- Preserves ordinary Surface recreation as a Surface-only refresh, the one bounded native-window-in-use retry, 1631 evidence-row semantics, Vulkan 1.4.361 query coverage, schema 2 and technicalReport 3.
- Coordinates with VulkanScope Database 0.39.18 for current-producer metadata only; no Database evidence semantics or D1 schema changes are required.

## 0.41.42

- Separates HTML report state presentation so `Supported` remains green, `Available` is blue, `Unsupported` remains red, `Unavailable` / `Not available` is amber, `Not applicable` is neutral and `Unknown` remains gray.
- Corrects HTML badge classification order so `not available` can no longer be mistaken for a negative support result.
- Renames the registry/query evidence field from the ambiguous `Report schema` label to `Registry report schema` in UI, TXT and HTML without changing Database schema 2 or technicalReport schema 3.
- Preserves all VulkanScope 0.41.41 Surface/WSI lifecycle, UUID/raw-byte serialization and section-aware property provenance fixes without changing Vulkan query coverage or report payload semantics.
- Coordinates with VulkanScope Database 0.39.17 for query-state-aware Surface Compare semantics.
- Keeps Vulkan 1.4.361, 302 Android-queryable providers, 110 implemented physical-device structs, 104 validated query groups, schema 2 and technicalReport 3 unchanged.

## 0.41.41

- Preserves fixed-size Vulkan UUID byte arrays as exact raw bytes instead of streaming `uint8_t` elements as characters.
- Makes generated property merging section-aware so same-name fields from distinct physical-device property structs retain provenance while equivalent manual extension rows are upgraded to their canonical generated struct section.
- Adds Surface generation/identity gating so stale `SurfaceView` destruction or delayed Surface probe results cannot overwrite the current Android Surface state.
- Defers Surface-only refresh while full/background collection is active instead of requesting a second complete report collection.
- Adds one bounded retry when `vkCreateAndroidSurfaceKHR` reports `VK_ERROR_NATIVE_WINDOW_IN_USE_KHR`; a second failure remains explicit Unavailable evidence.
- Coordinates with VulkanScope Database 0.39.16 for historical pre-0.41.40 property/feature Compare identity compatibility and correct visible-field metric wording.

## 0.41.40

- Corrects registry-generated field typing so physical-device Properties members are no longer misclassified as Features merely because Vulkan typedefs share the same underlying C++ integer type.
- Keeps VkBool32 members of Features structs in the Features dataset while VkBool32/numeric members of Properties structs remain detailed property evidence with their actual value semantics.
- Improves generated array/extent presentation and preserves exact raw evidence for types that do not yet have a canonical formatter.
- Corrects HTML report status badges: Available/Pass are positive green states, Fail/Unsupported/Not exposed are negative red states, Unavailable is an explicit amber query-failure state, Not applicable is a separate neutral state, and Unknown remains gray.
- Keeps the clarified detailed-query totals: safety diagnostics are counted separately from property/query rows.
- Updates the intended companion Database to 0.39.15 for the split Loader API / Base probe instance API report-text contract.

## 0.41.39

- Fixed the base terminal JSON builder so every device object is closed before the devices array/root is closed.
- Added native JSON-container balance validation before a base result can be logged/published as a terminal checkpoint.
- Restored the process-owned base-probe teardown contract by removing VkInstance destruction from the one-shot base collector path; the dedicated probe process owns cleanup after terminal handoff.
- Added failing-before-fix and behavioral regression gates for one-device and multi-device base terminal JSON construction.

## 0.41.38

- Fixed the real-device startup failure where a native pre-return `.done` marker let the consumer treat a still-active writer as terminal, producing `malformed or non-terminal JSON` and racing two separate SIGKILL paths.
- Terminal ownership is now single-source: native code publishes only bounded atomic result/checkpoint files; `VulkanProbeService` publishes `.done` only after JNI has returned and a streaming terminal-JSON validation has succeeded.
- Base JNI no longer rewrites an already durable collector final checkpoint. Normal polling never promotes a terminal-shaped checkpoint without the service-owned marker; pre-return final checkpoints remain eligible only for bounded timeout/crash recovery.
- Hard timeout, normal completion and cancellation now share one atomic process-termination claim, eliminating duplicate service/worker self-SIGKILL.
- Added bounded post-marker stable reread, terminal JSON streaming validation/fail-closed fallback, a terminal-ownership behavioral state machine, failing-before-fix verification against 0.41.37, and negative mutation controls.

## 0.41.37
- Re-audited the entire dedicated Vulkan probe lifecycle after 0.41.36 still timed out on-device. The previous behavioral gate covered `.done` only after service return and missed terminal publication that becomes durable before JNI/service return.
- Native base collection now publishes the exact terminal sidecar immediately after the terminal JSON and before process-owned teardown; service marker publication remains an idempotent fallback.
- All Vulkan instance/surface/loader cleanup in the dedicated probe process is deferred to process exit for both system and Turnip paths, eliminating driver teardown from the publication/return critical path.
- The service self-terminates only after a durable terminal marker exists, while the consumer enforces a bounded stale-process teardown barrier before the next query.
- A service-owned hard-deadline watchdog now terminates a JNI-blocked probe at the exact consumer timeout + 100 ms even if OEM process discovery omits `:vulkan_probe`; the consumer waits a 150 ms settle window at timeout before cleanup/return.
- Cancellation is now a hard lifecycle boundary: `VulkanProbeService.onDestroy` terminates the dedicated process even when its executor is blocked inside JNI, while `runServiceProbe` drives non-cancellable teardown and a bounded stop settle before deleting request files and rethrowing cancellation.
- Reduced the base probe ceiling from 45 seconds to 20 seconds and removed automatic retry for terminal unavailable/timeout results; one retry remains only for explicit bounded `incomplete` partial-positive evidence.
- Added failing-before-fix timeout/handoff state-machine and production-binding gates, negative mutations, false-positive controls and updated resource-budget evidence. Vulkan 1.4.361 capability/reporting semantics and Database schema compatibility are unchanged.

## 0.41.36
- Repaired the demonstrated Turnip producer/consumer handoff race: the dedicated service no longer self-SIGKILLs before the main process can observe terminal publication.
- Added an atomic `.done` terminal sidecar; the consumer forces a bounded final reread on that marker and fails fast on missing, malformed or non-terminal terminal JSON instead of waiting for the normal probe timeout.
- Corrected base-ready checkpoint JSON closure and canonicalized terminal `baseReportComplete` to one root key; publication logs now reflect actual atomic-write success.
- Added a failing-before-fix handshake verifier, a behavioral state-machine regression with unrelated-marker false-positive control, negative mutation requirements, and package extract/byte-reproducibility verification.
- Expanded `PROJECT_RULES.md` with the mandatory evidence workflow for all future changes. Vulkan 1.4.361 coverage, Database schema/report compatibility and bounded resource/security contracts are unchanged.

## 0.41.35

- Fixed a real-device Turnip hang after the complete base checkpoint was already atomically published.
- Turnip Vulkan instance/surface/library cleanup is now reclaimed by one-shot dedicated-process teardown instead of running on the result-publication critical path.
- Accepted probe publications terminate the previous dedicated probe process before the next query; timeout-boundary publications are recovered instead of discarded.
- Exhaustive background detail collection now has a 60-second total budget; any remainder is retained as explicit Unavailable evidence rather than silently omitted or keeping collection active indefinitely.
- Added failing-before-fix lifecycle regressions and a 0.41.34→0.41.35 allowlisted golden contract.

## 0.41.34

- Fixed real Android release native compilation failures by restoring bounded instance-extension evidence used by the base collector before WSI/dependency fields are serialized.
- Removed the dead extension-group `hasExt` lambda that was promoted to a build error by VulkanScope's `-Werror` policy.
- Fixed the HTML export Kotlin compile failure by routing Device layer enumeration through the existing `table(...)` helper instead of the nonexistent `kv(...)` helper.
- Added a failing-before-fix compile-regression verifier and a 0.41.33→0.41.34 allowlisted golden contract.
- Vulkan 1.4.361 registry/header pins, query coverage, report/database schemas, resource ceilings and timeout/race behavior are unchanged.

## 0.41.33
- Fixed the Vulkan 1.4.361 CMake configure regression that incorrectly required the Vulkan-Headers repository `registry/vk.xml` bytes to match the separately locked Vulkan-Docs canonical `vk.xml` SHA-256.
- Kept the bundled canonical Vulkan-Docs 1.4.361 snapshot byte-locked, kept Vulkan-Headers pinned to exact commit `31386378257ac8653ce5b32c93baec385259ebbe`, and changed the fetched-header registry gate to semantic `VK_HEADER_VERSION 361` plus `VK_NV_private_data_base_handle` sentinel validation.
- Added an allowlisted 0.41.32→0.41.33 regression contract and a targeted CMake registry-lock verifier so the cross-repository byte-hash bug cannot silently return.
- No Vulkan query/report coverage, Database submission schema, timeout, memory, security, or user-visible capability semantics changed.

## 0.41.32
- Pinned VulkanScope to the canonical Vulkan 1.4.361 registry snapshot and Vulkan-Headers 1.4.361 contract, with the uploaded `vk.xml` bundled and SHA-256 locked for reproducible offline registry verification.
- Added Vulkan 1.4.361 `VK_NV_private_data_base_handle` coverage through `VkPhysicalDevicePrivateDataBaseHandleFeaturesNV::privateDataBaseHandle`, increasing validated physical-device provider coverage to 302 and implemented catalog structs to 110.
- Corrected strict registry accounting to 297 stable plus 5 provisional queryable physical-device providers, with alias-resolved EXT/KHR/core pNext comparison and explicit beta-header requirements for provisional structures.
- Repaired canonical extension-reference generation and replaced the incomplete 199-entry subset with the complete 474-extension Vulkan 1.4.361 registry census, including registry-derived revision/type/platform/promotion/dependency/deprecation/provisional metadata.
- Removed full successful native-report duplication through JNI: the dedicated probe process now publishes bounded complete JSON directly to the atomic checkpoint and returns Boolean publication status; the main process reads the opened checkpoint into one exact-size bounded byte array.
- Added explicit concurrency/resource gates for application mutex serialization, single service worker, fair native probe lock, timeout placement, UID/process-name constrained probe termination, atomic result publication, 64 MiB probe publication ceiling, 2 MiB Database transport ceiling and 8 MiB Analysis snapshot ceiling.
- Clarified that the `:vulkan_probe` service is a dedicated application process rather than Android `isolatedProcess=true`, avoiding an unsupported security/isolation claim.
- Added 0.41.31→0.41.32 allowlisted golden regression locking plus targeted Vulkan 1.4.361, registry-snapshot and resource/concurrency verifiers; deliberate negative mutations for unallowlisted runtime drift, missing new-extension coverage and broken probe serialization all fail as expected.
- Static security, report-completeness, timeout/race, bounded-allocation and package checks found no additional concrete vulnerability or RAM leak requiring speculative production changes. Android Gradle tasks and real-device sanitizer/Validation-Layer runs remain separately unclaimed where the environment could not execute them.

## 0.41.31
- Corrected format-property eligibility for `VK_FORMAT_A1B5G5R5_UNORM_PACK16` and `VK_FORMAT_A8_UNORM` so Vulkan 1.3 devices exposing `VK_KHR_maintenance5` are queried instead of being incorrectly skipped until core Vulkan 1.4.
- Added the canonical `VK_IMAGE_LAYOUT_TENSOR_ALIASING_ARM` name to image-layout reporting, preserving raw numeric fallback for genuinely unknown future values.
- Corrected WSI color-space detail: DCI-P3 now records the presentation-engine XYZ component interpretation, while `VK_COLOR_SPACE_DOLBYVISION_EXT` is explicitly identified as the legacy Vulkan enum and is not presented as proof that Dolby Vision metadata signaling is active.
- Repaired the locked-registry quality contract so registered provisional physical-device query providers are tracked separately from stable providers; the 301-extension coverage is now 299 stable plus 2 provisional AMDX providers and provisional querying requires explicit beta-header mode.
- Added a 0.41.30→0.41.31 allowlisted golden regression contract and targeted failing-before-fix spec tests; only `vulkanscope.cpp` is permitted to differ from the predecessor runtime file set.
- Re-audited report completeness/loss prevention, canonical names, extension/query/report parity, updater and Database networking, Turnip confinement, JNI/native ownership, memory bounds, UI-thread blocking and package hygiene; no additional concrete runtime defect was changed without evidence.

## 0.41.30
- Introduced a commit-pinned Vulkan registry/header lock for Vulkan 1.4.360, tying the authoritative Vulkan-Docs `vk.xml` revision and canonical Vulkan-Headers commit directly to release verification.
- Replaced the stale schema-4 registry generator with a reproducible schema-current generator that validates the native catalog and Kotlin runtime extension coverage against locked upstream inputs and can require exact registry-derived extension coverage.
- Added a golden 0.41.29 predecessor contract that locks unchanged production/build-chain content plus 104 query groups, 301 validated physical-device extensions, 109 implemented catalog structs and 10 instance dependency candidates.
- Added mandatory regression-contract, strict upstream-registry and combined quality-gate tools; checked-in registry metadata now records exact registry/header provenance and extension coverage.
- Added CI gates for locked upstream/spec regeneration and Android release lint/unit-test/build execution.
- Kept the 0.41.29 production runtime byte-identical apart from application version metadata because this audit found tooling/test/reproducibility gaps but no new proven runtime defect requiring a speculative code change.

## 0.41.29
- Made native `baseReportComplete` require both complete physical-device enumeration and complete device-extension enumeration for every returned GPU; partial/unavailable extension catalogs can no longer unlock complete-report export or Database submission.
- Added a Kotlin defense-in-depth base coverage gate requiring every device's `deviceExtensionStatus` to be `available` before the base report is accepted as complete.
- Corrected no-match extension and simple-feature queries so a device-extension `VK_INCOMPLETE` result stays Incomplete rather than being collapsed to Unavailable; completed absence remains Not applicable and true enumeration failure remains Unavailable.
- Propagated device-extension enumeration uncertainty to extension-dependent advanced groups (`queue2`, `format2`, `imageFormat2`, `external`, `sparse`, `memory2`, `videoCapabilities`) so optional/chained evidence cannot be silently omitted under an Available group root.
- Replaced Turnip legacy-bundle `walkTopDown().toList()` validation with streaming bounded traversal that validates canonical confinement and rejects symlink-like/special entries before descending, without materializing the whole tree in memory.
- Switched private Turnip import/backup transaction directory names from wall-clock timestamps to UUIDs and require a newly created import directory, preventing stale-directory reuse if clocks repeat or an old temporary directory survives a prior interruption.
- Re-audited Vulkan 1.4.360 canonical naming, raw unknown values, `VK_INCOMPLETE` retention, report/export state, update/Database security, JNI/native ownership, dependency pins and release-package hygiene without changing schemas or endpoints.

## 0.41.28
- Corrected simple extension-backed feature groups so an extension found on one GPU no longer leaves the group Available when another GPU's device-extension enumeration is incomplete or unavailable; the group is now Incomplete while positive per-device evidence is retained.
- Scoped the release verifier to `collectVulkanSimpleFeatureGroup`, preventing completeness code in another extension collector from masking a regression in the simple-feature path.
- Made the isolated native crash marker fail fast for advanced, extension, metadata, Surface and self-test probes; base collection keeps its complete-checkpoint recovery behavior.
- Preserved explicit crash/Incomplete states through existing report merging without fabricating unsupported capability conclusions or changing Database/report schemas.
- Re-audited Vulkan 1.4.360 naming/enumeration semantics, report/export lifecycle, Turnip synchronization, JNI/native ownership, dependency pins and release-package hygiene.

## 0.41.27
- Stopped unchanged isolated-probe checkpoints from being re-read and reparsed every 500 ms; polling now parses only when length, modification time or atomic-replacement inode changes.
- Corrected advanced Tool Properties and Vulkan Video format group status so `VK_INCOMPLETE` is reported as Incomplete at the group level while bounded partial positive evidence remains visible.
- Corrected extension-query completeness across multi-GPU devices: incomplete/unavailable device-extension enumeration no longer leaves a partially attributable extension group marked Available.
- Propagated `vkGetPhysicalDeviceCooperativeMatrixProperties2EXT` `VK_INCOMPLETE` to extension-group Incomplete status without discarding returned property rows.
- Reworked Turnip bundle validation so the 2048-entry safety limit counts the entire visited tree, rejects canonical aliases/symlink-like entries and reuses one validated file set for metadata, declared-driver and read-only checks.
- Serialized Turnip installation with the isolated-probe mutex and blocked every isolated probe, including optional self-tests, while private driver files are being mutated.
- Revalidated Vulkan 1.4.360 naming/evidence rules, report/export state, Android 17 native-code hardening, update/Database security, native/JNI ownership and package hygiene without changing schemas or endpoints.

## 0.41.26
- Moved complete TXT/HTML report serialization and destination I/O off the main thread; exports now persist the exact initiated report snapshot in private cache before SAF/Downloads handling.
- Made pending SAF exports survive Activity/configuration recreation through small saveable snapshot metadata instead of holding a report-sized transient Compose string.
- Removed report-sized UTF-8 `toByteArray` duplication from TXT/HTML export writes and added cleanup for temporary snapshots and known partial destination files.
- Re-enforced the complete-report collection gate at driver picker launch, Activity-result return, import entry and driver-source mutation so stale picker callbacks cannot change collection inputs.
- Added a separate driver-picker re-entry guard and prevented new full/Surface/ad-hoc Vulkan probes from racing an in-progress private driver import.
- Moved installed Turnip bundle traversal/read-only hardening out of recomposition and service-call main-thread paths onto `Dispatchers.IO`.
- Fixed stale reporting after replacing a Turnip ZIP while Turnip was already selected: every successful import now invalidates the previous report and forces a fresh complete collection against the newly installed bundle.
- Removed the broad release R8 keep rule for the entire `MainActivity` while explicitly retaining the `VulkanProbeService` class/native JNI names required by static JNI lookup.
- Revalidated Vulkan 1.4.360 canonical naming/evidence semantics, API 37/build pins, update/Database security, Turnip Android 17 hardening, JNI/native ownership and package hygiene without changing report/database schemas.

## 0.41.25
- Updated the active Material 3 Expressive dependency to 1.5.0-alpha27, the current AndroidX alpha published 2026-08-26, while keeping Compose UI/Foundation/Animation on stable 1.12.0.
- Updated the Compose compiler Gradle plugin from Kotlin 2.3.21 to current stable Kotlin 2.4.10 while retaining AGP 9 built-in Kotlin and the existing no-`kotlin-android` policy.
- Updated OkHttp from 5.2.0 to current stable 5.5.0 for current TLS/HTTP fixes while preserving VulkanScope's existing explicit IPv6-first system-DNS policy and approved network endpoints; ECH/alternate DNS remains opt-in and is not enabled.
- Hardened user-selected Turnip/AdrenoTools native code for Android 17/API 37 by marking every extracted `.so` read-only immediately after opening its destination and before writing package bytes.
- Added bounded pre-install verification that all imported native libraries remain canonically confined to the private temporary bundle, readable and non-writable before the atomic directory swap.
- Added fail-closed installed-bundle verification: every Turnip resolution checks/hardens the bounded whole native-library set, including dependent `.so` files even when the selected library was already read-only, and refuses loading if read-only state cannot be established.
- Moved the complete bounded Turnip ZIP import transaction off the UI thread to cancellable `Dispatchers.IO`, added cleanup on cancellation/failure, blocked concurrent imports, and fsynced native libraries/metadata before atomic installation.
- Re-audited Vulkan 1.4.360 naming/evidence semantics, complete-report retention, Surface/WSI state, multi-device attribution, updater/Database security, JNI/native ownership, bounds, lazy UI/export behavior and package hygiene without changing capability/report schemas.

## 0.41.24
- Hardened Surface/WSI enumeration provenance so successful retries replace stale `VK_INCOMPLETE` results, bounded partial format/present-mode evidence survives later retry failures, and completed empty/missing-FIFO results are explicit specification anomalies rather than complete negative evidence.
- Added runtime fallback from failing `VK_KHR_get_surface_capabilities2`/formats2 queries to the classic `VK_KHR_surface` path without discarding earlier bounded partial formats2 evidence; both attempts retain explicit provenance.
- Separated local physical-device safety-bound rejection from driver-returned `VkResult` values so VulkanScope no longer fabricates a Vulkan error for an application-side allocation guard.
- Preserved physical-device safety rejection/reason beside retained partial GPU evidence in base and Surface reports while keeping `baseReportComplete=false` and export/Database submission fail-closed.
- Generalized bounded partial-evidence retention across instance extensions/layers, per-layer extensions, device layers and device extensions when a later retry fails or exceeds a local bound.
- Propagated Surface format/present-mode enumeration completeness and specification-anomaly state through UI, Analysis, TXT, HTML and schema-3 technicalReport so missing catalog entries are classified unsupported only after proven-complete enumeration.
- Restored pre-probe signal handlers after successful isolated native probes, preventing the crash guard from altering signal disposition beyond the probe lifetime.
- Added additive scope/count provenance to legacy schema-2 single-GPU summary fields on multi-device reports while preserving the all-device schema-3 technicalReport and backward-compatible keys.
- Re-audited Vulkan naming/result semantics, query-state preservation, multi-device attribution, updater/Turnip/Database security, JNI/native ownership, bounds, lazy presentation, TV navigation and package hygiene.

## 0.41.23
- Fixed the `videoCapabilities` native routing mismatch so the advanced query now executes its Vulkan Video capability/format implementation instead of silently missing the evidence behind an unreachable extension-collector branch.
- Made sampled Vulkan Video profile results explicit: registered profile-specific query errors are Unsupported for the exact sampled profile only, while unrelated failures remain Unavailable with raw `VkResult` evidence.
- Preserved queue-family Vulkan Video operation masks returned with incomplete physical-device enumeration across UI, Analysis, TXT, HTML and technicalReport instead of dropping valid partial positive evidence.
- Corrected `VK_KHR_video_queue` and codec-extension prerequisite handling so incomplete device-extension enumeration remains Unknown rather than becoming a false Not applicable result.
- Made missing Vulkan Video format-query entry points explicit and preserved bounded `VK_INCOMPLETE` format evidence without fabricating codec-wide support conclusions.
- Decoupled Vulkan Video capability and format entry points so an unavailable `vkGetPhysicalDeviceVideoCapabilitiesKHR` no longer suppresses otherwise callable `vkGetPhysicalDeviceVideoFormatPropertiesKHR` evidence, and vice versa.
- Corrected encode capability valid usage by chaining generic `VkVideoEncodeCapabilitiesKHR` with the required H.264/H.265/AV1 codec-specific capability structure; successful sampled profiles now retain the corresponding codec-specific encode limits.
- Replaced raw fixed-size sampled-profile storage with typed Vulkan Video profile structures, removing unnecessary size/alignment assumptions and type-punning.
- Replaced the old generic sampled-image format recipe with separate Vulkan Video decode-output, decode-DPB, encode-input and encode-DPB usage queries for each bounded sampled profile.
- Restored updater release/version integrity by requiring the validated APK `versionName` to exactly match the selected GitHub release version in addition to the existing package, signer, versionCode, HTTPS, ABI and size checks.
- Re-audited report-state preservation, multi-device attribution, WSI/external capability rules, Turnip path confinement, Database submission, JNI/native ownership and release/package hygiene.

## 0.41.22
- Corrected Android HDR evidence so a successful empty HDR-type result is reported as a completed zero-result capability query rather than Unavailable; Unknown and query failure remain separate.
- Removed the remaining minor-only Vulkan API gates from versioned feature/property collection so future higher-major Vulkan versions cannot be misclassified as older than Vulkan 1.x.
- Corrected Vulkan Video format and Vulkan Tool zero/`VK_INCOMPLETE` enumeration semantics without fabricating support or absence.
- Expanded canonical `VkResult` reporting to every distinct result value in the pinned Vulkan 1.4.360 header and added canonical result text beside raw Image Format Properties2 tuple results.
- Added a physical-device selector so every runtime-enumerated GPU is reachable from device-specific UI pages; multi-device export filenames no longer imply a first-GPU-only report.
- Made TXT/HTML summary cards multi-device-safe and routed HTML HDR summary through the same state-aware formatter as UI/TXT, preventing first-GPU and empty-HDR semantic drift.
- Preserved device-layer Unknown/Incomplete/Unavailable states, Turnip tri-state eligibility and Surface presentation state instead of collapsing them into negative capability claims.
- Hardened Turnip ZIP import against duplicate canonical archive paths and made the isolated probe service consistently use the validated canonical cache result path.
- Re-audited report serialization, Vulkan naming, update/Database security, native/JNI ownership, bounds, performance/lazy presentation, TV navigation and package hygiene.

## 0.41.21
- Corrected live Surface/WSI validity and evidence-state handling: dependent non-null-Surface queries now require proven presentation support, per-queue `VkResult` is retained, unattempted queries no longer become fabricated Vulkan errors, and incomplete extension/enumeration evidence remains Unknown/Incomplete rather than false negative support.
- Added safe classic `VK_KHR_surface` fallback when `VK_KHR_get_surface_capabilities2` is advertised but its required entry points are unavailable, while preserving explicit path provenance.
- Split external memory, fence and semaphore capability queries into independent evidence paths and corrected/expanded exact OPAQUE_FD, SYNC_FD, DMA_BUF and Android Hardware Buffer handle-type reporting.
- Corrected Vulkan 1.0 promoted-command compatibility by enabling the advertised device-group/external-capability instance extensions and loading the KHR physical-device-group alias where required.
- Fixed the Format Properties2 path to use a direct void query only after an entry-point availability gate.
- Preserved bounded physical-device handles after repeated `VK_INCOMPLETE`; exact enumeration result/completeness now flows through UI, Analysis, TXT, HTML and technicalReport while incomplete base reports remain non-exportable/non-submittable.
- Extended partial physical-device semantics to isolated core, extension, Vulkan 1.4, advanced and simple feature groups so incomplete device sets cannot prove global absence or Not applicable.
- Prevented cross-process evidence misattribution on systems with multiple identical vendor/device IDs; ambiguous Surface/advanced/extension/self-test evidence is now Unavailable instead of being assigned to the first matching GPU.
- Fixed a duplicate Kotlin local declaration in the extension merge path that could block release compilation.
- Distinguished Android platform-API unavailability and unavailable HDR metadata from proven display capability absence.
- Re-audited manifest/update/Database/Turnip bounds, JNI/Vulkan/ANativeWindow ownership, query serialization, report generation, UI/TV focus and package hygiene; no new concrete static security vulnerability or resource leak was found in reviewed paths.

## 0.41.20
- Separated Vulkan loader API, base-probe instance API and physical-device API reporting.
- Preserved native physical-device-group and Surface-probe provenance that was previously dropped at the Kotlin merge boundary; bounded `VK_INCOMPLETE` group enumeration now retains partial group evidence, exact result and completeness state.
- Removed the synthetic per-device physical-device-group placeholder so only actual returned group properties describe group membership.
- Prevented ad-hoc query results and Surface changes from being lost during a still-running full collection; driver changes now clear stale report state, trigger fresh collection and suppress older-driver in-flight probe publication.
- Made lazy-query blank/error outcomes explicit Unavailable evidence instead of silent completion.
- Added structured query-safety rejection evidence, raw device ID parity and canonical Surface VkResult text to the schema-3 technical report, and made TXT/HTML report completeness explicit.
- Corrected profile extension-scope certainty and expanded the Android 16 r.7 lightweight evaluator with directly verified MUST features and safely comparable properties while retaining UNKNOWN for incomplete official coverage.
- Replaced friendly physical-device-type labels with exact canonical Vulkan enum names and expanded canonical VkResult presentation.
- Replaced minor-only Vulkan API gates with major+minor comparisons, preventing future higher-major API versions from being misclassified as older Vulkan 1.x devices.
- Made the checked-in extension-reference catalog explicitly a supplementary subset; blank registry metadata is Unavailable rather than interpreted as absence.
- Removed the duplicate extension-name catalog and synchronized registry audit metadata to the native 109-struct / 104-query-group / 268-runtime-registry-token-reference catalog; corrected the canonical registry-token metric naming while retaining only a compatibility alias for older structured consumers.
- Corrected the libadrenotools FetchContent pin to its full immutable commit and disabled shallow cloning for the hash pin, matching CMake requirements without changing the dependency revision.
- Re-audited update, Database, Turnip, manifest, JNI/native ownership, bounds and UI behavior without finding a new concrete static resource leak or security vulnerability.

## 0.41.19
- Serialized all isolated Vulkan service probes at the application layer so timeout windows start only when a query owns the native worker slot.
- Prevented stale Surface refresh results from overwriting newer feature/property/extension evidence collected in parallel.
- Deferred full recollection while Surface/ad-hoc tasks are pending, preventing mixed-generation report state.
- Removed forbidden third-party comparison-product naming from shipped source identifiers, runtime evidence labels, tools and rule/audit filenames while preserving validated coverage under neutral terminology.
- Kept neutral internal coverage prefixes out of user-visible feature/property labels so canonical Vulkan structure/field names remain clean.
- Removed nested README packaging residue and strengthened source-release hygiene checks.
- Revalidated Vulkan 1.4.360, Android API 37, AGP 9.3.2 / Gradle 9.7.1, schema 2 / technicalReport 3 and the three required ABIs.

## 0.41.18
- Removed remaining official-looking synthetic unknown Vulkan names; unknown/future values retain raw evidence.
- Preserved bounded partial `VK_INCOMPLETE` evidence and explicit status/reason/completeness for instance/device layer-extension enumeration.
- Propagated per-layer extension-query provenance through UI, Analysis snapshots, TXT, HTML and technicalReport.
- Made native `baseReportComplete` authoritative so timeout/crash paths cannot publish partial checkpoints as complete reports.
- Corrected crash-marker, Surface prerequisite and stale Surface/metadata failure semantics.
- Removed redundant isolated-Surface physical-device-properties work while preserving deterministic cleanup and bounded queries.
- Fixed release-verifier ordering so the Image Format Properties2 query-recipe gate is enforced before PASS.
- Cleaned the two inline deprecation-suppression expressions that produced Kotlin block-annotation parsing warnings in TXT/HTML version-code fallback paths.
- Removed three unused legacy Compose helpers from the active source tree.
- Revalidated security-sensitive report/update/import paths and native resource ownership without introducing a schema or Database migration.

## 0.41.17
- Removed the obsolete unused `instanceLayers(VulkanApi&)` native wrapper that failed release builds under `-Werror=-Wunused-function`.
- Preserved instance-layer provenance by keeping all active collection on `enumerateInstanceLayers`.
- Preserved 0.41.15 enumeration/HDR semantics and 0.41.16 compile corrections unchanged.

## 0.41.16
- Fixed native release compilation by comparing device-extension enumeration status by string contents instead of C-string pointer identity.
- Fixed Kotlin release compilation by moving the shared HDR type formatter to top-level serializer scope.
- Preserved 0.41.15 Vulkan enumeration and Android HDR provenance semantics unchanged.

## 0.41.15
- Added canonical names for the current shared-refresh and FIFO-latest-ready Vulkan present modes.
- Unknown future present-mode, color-space and format values now remain raw unknown evidence instead of synthetic Vulkan symbols.
- Preserved instance extension/layer enumeration status, reason, partial `VK_INCOMPLETE` evidence and completion state.
- Corrected failed physical-device and uncertain extension-enumeration paths so they do not become false Not applicable results.
- Distinguished Android HDR capability-object absence from a genuine empty supported-HDR-type list.
- Propagated the refined enumeration/HDR provenance through UI, Analysis, TXT, HTML and structured reports.
