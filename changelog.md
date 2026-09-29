## 3.0.12
- Fixes the Kotlin release-compilation failure in Turnip File Manager folder prevalidation by removing the non-suspend Sequence/runCatching lambda around the suspend archive inspector and using a cancellation-safe imperative scan instead.
- Preserves the 3.0.11 validated Turnip ZIP folder counts, 256-candidate bound, imported-package inclusion, path/archive safety, File Manager UI behavior, Vulkan collection and report/database semantics unchanged.

## 3.0.11
- Fixes Turnip File Manager folder subtitles so they count only ZIP packages that pass the same bounded Turnip archive inspection used for visible candidates, instead of counting every file whose name ends in `.zip`.
- Keeps already-imported but otherwise valid Turnip packages in the folder count, matching the packages still shown inside that folder.
- Preserves the existing 256-ZIP per-folder validation bound, explicit limited-count marker, import revalidation, path safety, Vulkan collection, report/database behavior, ABI targets and dependency versions.

## 3.0.10
- Fixes the release Kotlin compilation failure in Database submitted-time rendering by resolving Compose `LocalContext` through the established fully qualified platform reference.
- Makes the shared File Manager View & sort chooser orientation-independent by opening its bounded choice surface outside the landscape scrollable controls pane, preserving all layout/sort choices and X-only dismissal.
- Adds pointer/D-pad scrolling and system-bar-safe bounds to the View & sort surface so short landscape/freeform windows remain fully usable.
- Keeps Vulkan/native collection, report/export schemas, Database transport, permissions, ABI targets, registry/generated data, packaged assets and dependencies unchanged.

## 3.0.9
- Formats public Database submission timestamps as device-local Date and Time fields instead of raw ISO strings, with the local time-zone label retained for context.
- Makes Database System/Turnip driver labels use the exact Overview color distinction and bold hierarchy while keeping report data unchanged.
- Gives Encyclopedia, Requirements, Minimums, Graph, Quality and Tests section headers unique semantic icon treatments, using meaningful badge/overlay variants instead of repeating one identical glyph for different roles.
- Adds the semantic-icon uniqueness rule to the permanent VulkanScope engineering rules without changing Vulkan collection, report schemas, Database endpoints, permissions, ABI targets or dependencies.

## 3.0.8
- Redesigned Analysis Requirements, Minimums and Encyclopedia for clearer evidence boundaries, richer summaries and more usable detail presentation.
- Reworked capability statistics into compact Vulkan-accent summary grids across Properties and other metric-based pages.
- Replaced generic Analysis Quality/Graph/Test section glyphs with semantic icons and matched Database GPU badges to the Turnip driver identity treatment.
- Fixed hardware D-pad/arrow navigation outside television uiMode and extended focus/scroll handling across pages, file managers, filter lists and scrollable detail dialogs.
- Added animated file-manager breadcrumb, folder-transition and Turnip selected/remaining-count feedback.

## 3.0.7
- Replaces generic Database icons in the public report list with the GPU vendor artwork already used by VulkanScope, while keeping unknown vendor IDs explicit through the existing unknown-vendor asset.
- Makes Turnip and shared-storage file-manager search transitions expand, collapse and resize smoothly, and adds bounded content-size motion to folder/file cards so layout changes no longer snap.
- Rebuilds Quality as an auditable collection-integrity calculation with a visible 100-point baseline, fixed per-check deductions, threshold bands, triggered/clear evidence and explicit non-ranking semantics.
- Rebuilds active Test results with the exact target/scope, PASS/FAIL/UNAVAILABLE counts, overall outcome, per-test Vulkan result evidence and clear result semantics without mutating capability support.
- Preserves Vulkan/native collection, report/export schemas, Database endpoints and bounds, permissions, ABI targets, registry/generated data and dependency versions unchanged.

## 3.0.6
- Fixes the Analysis release-compilation regression by reading validated-network state from the existing `@Composable` Analysis page and passing the resulting Boolean into the non-composable lazy-list builder.
- Preserves the 3.0.5 full-screen file managers, diagnostic timing presentation, Database list/Report ID workflows, network gating, Vulkan collection, report/export schemas, ABI targets and dependency versions unchanged.

## 3.0.5
- Rebuilds Turnip and every shared-storage import/export file manager as animated full-screen AMOLED-black sections with no outer window frame, while preserving system navigation insets/backdrop behavior.
- Moves expandable search directly beside the unified View & sort control, removes the separate search row, keeps X-only View & sort dismissal, and gives the freed space back to folder/file browsing in portrait and landscape.
- Uses the established Vulkan-red outline treatment on Turnip and shared-storage folder/file cards while retaining bounded scans, path validation, file-type validation and atomic export semantics.
- Redesigns Analysis Diagnostic collection into end-to-end, phase and probe/scheduler timing groups; elapsed values are shown in seconds with the exact millisecond value in parentheses.
- Splits Database lookup into a bounded public report-list workflow and exact 64-character Report ID workflow, with local list filtering, pagination-aware loading and the existing local evidence comparison path.
- Preserves Vulkan/native collection, registry/generated data, report/export schemas, permissions, ABI targets and dependency versions unchanged.

## 3.0.4
- Fixes the Kotlin release compilation regression in Android TV navigation by reading the native Android keycode from each Compose key event's `key` value.
- Removes the invalid explicit Foundation `weight` import while preserving the dependency graph's three equal-width summary cards through the public `RowScope` weight API.
- Preserves all 3.0.3 Analysis confirmations, graph behavior, full-screen file manager, Android TV navigation semantics, report-copy feedback, Vulkan collection and report/Database behavior unchanged.

## 3.0.3
- Reworks Analysis custom minimum profile actions into VulkanScope-red contained Load/Delete controls, using the update-action and trash icons, and adds consistent question-style Cancel/Load, Cancel/Delete and Cancel/Save confirmations before local profile mutations.
- Redesigns the Analysis dependency graph into a bounded scrollable explorer with depth controls, evidence-state legend, summary metrics, readable relationship lanes and a separate complete traversal evidence list.
- Rebuilds the Turnip file manager as a full-screen surface with clickable breadcrumb navigation, animated round search expansion, one-level ZIP file counts for folders, retained system-navigation glass, and an X-only view/sort chooser dismissal path.
- Improves Android TV remote navigation with D-pad focus-first movement, bounded lazy-list/grid scroll fallback, Page Up/Page Down handling, full folder/file card activation and bring-into-view focus treatment across application navigation and file-manager content.
- Adds the same three-second animated green-check feedback used by copy actions to the successful Database report-ID copy control before restoring its normal copy icon.
- Keeps Vulkan/native collection, registry data, report/Database/export schema and endpoint semantics, permissions, ABI targets and dependency versions unchanged.

## 3.0.2
- Fixes the release Kotlin compilation failure in the extracted shared-storage listing by explicitly opting that top-level composable into the Material 3 Expressive loading API it already uses.
- Preserves the 3.0.1 file-manager layout, loading presentation, page/header spacing, desktop input behavior, opening animation, Vulkan collection and report semantics unchanged.

## 3.0.1
- Keeps the opening accent rule at full width after it completes instead of shrinking just before the opening surface fades.
- Reflows the shared-storage file manager into a full-height two-pane landscape layout so folders/files keep substantial browsing space while controls stay in a bounded scrollable side pane.
- Makes secondary mouse clicks a no-op on ChromeOS ARC and Android PC-form-factor environments before long-press or quick-menu handlers can react.
- Adds a smooth file-manager-red border to the existing press-and-hold evidence-row press-in feedback.
- Replaces the top-page underlap with a 12 dp live header separation so the first page surface no longer touches or enters the top chrome in portrait, landscape or freeform layouts.
- Keeps Vulkan/native collection, reports, Database/export semantics, Turnip behavior, permissions, endpoints, ABI targets, dependencies and registry data unchanged.

## 3.0.0
- Extends the existing retained glass treatment behind Android navigation controls with live bottom/side navigation insets while keeping application controls inset from system UI.
- Moves page content slightly beneath the translucent top chrome so rounded page surfaces no longer terminate exactly against the header edge.
- Simplifies the opening accent animation to one continuous red rule without endpoint dots.
- Starts pager/status/scroll-indicator collision avoidance earlier and uses prompt coordinated motion so overlays cross scrolling search/content surfaces without delayed overlap.
- Keeps Vulkan collection, reports, Database, Turnip, registry, ABI, permission and network semantics unchanged.

## 2.1.16
- Fixes the collection pager's lazy-to-overlay coordinate mapping by subtracting the live lazy viewport start offset before positioning the single page-level pager, so it occupies the exact permanent spacer instead of landing one top-content inset too high.
- Applies the same viewport conversion to both adjacent-item bridge paths, keeping reverse landing correct through lazy composition gaps in portrait, landscape, freeform and transient-overlay layouts without fixed header corrections.
- Preserves the 2.1.15 single pager, shared 24 dp retained-glass source/tint, overlay ordering, Vulkan/report/Database semantics, Turnip behavior, permissions, dependencies and registry data unchanged.

## 2.1.15
- Replaces pager ownership handoffs with one page-level `CollectionPager` for the pager's entire visible/pinned lifetime; the lazy list always owns only the exact-height anchor spacer, so reverse scrolling returns the same pager to the anchor coordinates without swapping composables.
- Uses the live `LazyListItemInfo.offset` as the pager's authoritative Y coordinate whenever the anchor is visible and only clamps that single pager at the live app-header boundary after the anchor reaches it; adjacent-item reconstruction is retained only for the narrow lazy-composition gap.
- Registers the page's retained 24 dp blurred backdrop as the shared chrome source so the app header, compact bottom navigation and pager-join extension sample the same blur layer with the same `VulkanGlassTint`, removing the disconnected blur/tone seam.
- Restores scroll-coupled full-width glass growth from the header boundary while keeping ordinary page content crisp, preserving portrait/landscape system insets, transient-status ordering and smooth scroll-indicator movement.
- Keeps Vulkan/native collection, reports, Database/export semantics, Turnip handling, manifest, permissions, endpoints, ABI targets, dependencies and registry data unchanged.

## 2.1.14
- Returns the shared collection pager to its real lazy-list item outside the bounded header join region, while keeping one mutually exclusive overlay only during join/pinned ownership so reverse scrolling lands at the list position instead of remaining detached.
- Retains measured pager height outside lazy-item lifetime and reconstructs the natural pager position from an immediately adjacent visible item when needed, preventing disposal/recomposition landing jumps in portrait and landscape.
- Matches the pager-join backdrop to the 24 dp app-header/navigation blur radius and the same uniform glass tint, with the bounded glass extension reaching the moving pager bottom while ordinary page content remains crisp.
- Keeps Vulkan/native collection, reports, Database/export semantics, Turnip handling, manifest, permissions, endpoints, ABI targets, dependencies and registry data unchanged.

## 2.1.13
- Tightens the single sticky pager so the header handoff uses one moving instance only, removes the duplicate box illusion, and lets the pager settle flush when it reaches or leaves the header.
- Re-bounds the retained glass region to the live header-plus-pager stack so list cards stay crisp while the top glass expands and contracts smoothly in portrait and landscape.
- Restores the frosted treatment on the header, pager chrome and compact bottom navigation while keeping the joined glass tone visually uniform through the handoff.
- Preserves the 2.1.12 page-number focus behavior, Vulkan/report/database behavior, storage/export semantics and SPIR-V™ presentation.

## 2.1.11

### Fixed
- Fixed the release Kotlin compile regression in the retained glass implementation by importing `rememberGraphicsLayer` and `drawLayer` from their actual Compose UI 1.12.0 packages.
- Preserved the complete 2.1.10 progressive pager-to-glass handoff, bounded blur/tint rendering, portrait/landscape overlay geometry and Vulkan/report behavior unchanged.

## 2.1.10

### Changed
- Replaced the abrupt pinned-pager handoff with a scroll-coupled progressive handoff: the normal-flow pager and overlay crossfade at the same geometry, the overlay follows the live pager position until it reaches the header boundary, and the reverse path restores the pager continuously.
- Extended the top glass backing continuously as the pager approaches the header so the full-width frosted region grows around the pager only during the join and shrinks in reverse during return.
- Reworked the top glass treatment around a retained graphics-layer blur plus dense dark plum/rose tinting, avoiding checker/mosaic cells, framebuffer readback and per-frame bitmap allocation.
- Preserved the ordered header → pager → transient-status → scroll-indicator lanes, page-number-only transitions, numeric-field focus behavior and portrait/landscape system-navigation insets.

## 2.1.9

### Fixed
- Reworked collection-pager pinning so the pinned visual is rendered outside the lazy-list clipping boundary and remains visible after its anchor leaves ordinary list visibility.
- Made pin/unpin tracking derive from the live header boundary and current system-navigation insets for both portrait and landscape window layouts.
- Kept the coordinated header → pager → transient-status → scroll-indicator lanes, number-only page transitions and focused page-edit arrow behavior unchanged.

## 2.1.8

### Fixed
- Fixed the Kotlin release compilation regression in the pinned-pager state reporter by forcing the remembered callback to return `Unit` after its map update/remove side effect.
- Preserved all 2.1.7 glass-header, pinned-pager, file-manager icon, page-editing and SPIR-V™ presentation behavior unchanged.

## 2.1.7

### Changed
- Replaced the block-pattern header treatment with a smooth dense translucent glass surface matching the supplied top-bar reference while keeping moving page content faintly visible beneath it.
- Gave every shared file-manager sort mode its own semantic icon and made the combined trigger reflect both the active layout and active sort.
- Reordered pinned paging overlays so the app header is followed by the pinned pager, transient status surfaces and the top scroll hint without intersections; the pager now joins and leaves the header glass smoothly.
- Made collection and filter pagination arrows discard active numeric-field editing, clear focus and transition the displayed destination page number softly.
- Applied the official SPIR-V™ trademark presentation to Vulkan self-test UI labels without changing canonical registry data or native query semantics.

## 2.1.6

### Changed
- Reworked the translucent app header into a denser frosted/mosaic glass treatment while keeping page content visible beneath it.
- Replaced the combined file-manager menu heading with a circular VulkanScope-red close action at the upper-left.
- Kept sticky page controls present after they pass the lazy-list visibility boundary and coordinated their pinned position with transient status banners and the top scroll indicator.
- Removed whole-pager page-change motion so only the displayed page number transitions softly while the pager surface stays fixed.
- Smoothed live top-overlay inset changes for lazy pages and the Surface destination chooser so status/scroll/pager lanes move together instead of intersecting.

## 2.1.5

### Changed
- Made the full-width app header substantially translucent so page content can scroll visibly behind it, while preserving initial content clearance and adding side-system-navigation safe insets for landscape.
- Re-aligned only the SCOPE glyph group in the horizontal VulkanScope wordmark and switched header/opening uses to the aligned local asset.
- Unified file-manager layout and sorting into one View & sort menu and extended all six layouts to the shared-storage browser used by TXT/HTML/JSON and Analysis import/export flows.
- Restored overflow-gated soft search-edge fades without shading empty or short leading text.
- Made sticky pagination overlays transparent outside the pager card, made the pager card translucent, and added smooth pinned positioning below the top scroll indicator.
- Reduced the primary app-page Back circle while retaining its stable reserved slot.

## 2.1.4

### Fixed
- Restored Kotlin compilation after the 2.1.3 paging expansion by using `stickyHeader` as the `LazyListScope` member API instead of a nonexistent top-level import.
- Restored the Vulkan Video evidence model/parser/state-label helpers accidentally removed during 2.1.3 pagination work.
- Restored queue-family Vulkan Video codec-query and codec-operation evidence rows.

## 2.1.3
- Reworked the shared top bar into a full-width translucent status/header surface with a balanced horizontal VulkanScope logo, restrained destination typography and a smaller circular animated Back action.
- Removed main-page edge fade masks, protected compact bottom navigation from landscape system-navigation side insets and limited search fading to genuinely overflowing, unfocused trailing text.
- Reduced shared collection pagination to 25 visible rows, rejected nonexistent page input, made page controls sticky immediately above result rows and added bounded directional page-change motion across list-heavy Vulkan, Surface, Video, queue and Analysis views.
- Added A–Z/Z–A, modified newest/oldest and created newest/oldest sorting to the Turnip File Manager and the shared-storage browser reused by TXT/HTML/JSON and Analysis import/export workflows.
- Applied the selected-Turnip Vulkan-red outline family to Material and custom dialog containers while preserving native/JNI, Vulkan 1.4.364 registry, report/Database/export semantics, permissions, endpoints, ABI targets and dependency/build pins.

## 2.1.2
- Moved the floating VulkanScope header below the Android status/cutout area, tightened its rounded geometry and made its left/right action slots symmetrical.
- Replaced the old section-label stack with a compact accent section pill beside the VulkanScope logo; Back and Settings now use matching animated red action capsules.
- Fixed collection page-number editing so digits do not navigate/rebuild the page until IME Done or focus loss.
- Reworked the File Manager layout popup into a ZArchiver-inspired 3×2 icon selector, made Dense grid materially denser than Grid and corrected Large tiles to horizontal icon/text alignment.
- Applied the same animated root-aware Back action to Turnip/shared-storage file browsers and strengthened soft fades for long search text plus scrolling file/folder boundaries.

## 2.1.1
- Fixed the 2.1.0 Kotlin compile regression in the shared 50-item collection pager by passing `onPageChange` explicitly at all four Features, Formats, Properties & Limits and Extensions call sites.
- Preserved the 50-item presentation-only pagination behavior and all 2.1.0 translucent UI, File Manager layout, determinate updater and Database report-result features unchanged.
- Kept Vulkan 1.4.364 native/query coverage, report/Database semantics, permissions, endpoints, ABI set and build-chain pins unchanged.

## 2.1.0
- Reworked the shared top header into a rounded translucent surface and added soft shared scroll-edge fades so cards, text and search controls transition cleanly at viewport intersections.
- Replaced the Turnip file-manager layout button row with one active-layout icon and a compact six-layout selector that remembers the last choice.
- Added 50-item UI pagination to Features, Formats, Properties & Limits and Extensions without truncating collected, exported or submitted report data.
- Made update download progress determinate from trusted release asset/transfer byte counts with a full-width percentage bar and completed-size validation.
- Added a boxed copyable Database report ID and an `Open report` action after validated successful submission.
- Preserved Vulkan 1.4.364 collection/query coverage, report and Database payload semantics, permissions, endpoints, ABI/dependency/build-chain pins and 2.0.6 probe/scheduler behavior.

## 2.0.6
- Made the Surface landing chooser vertically scrollable when its destination cards do not fit the available viewport, including compact landscape and system-navigation-constrained windows.
- Reused the existing dynamic transient-overlay, bottom-navigation and horizontal system-navigation insets while adding shared scroll-boundary hints, so the third Surface destination and its action remain fully reachable above the floating tab bar.
- Preserved 2.0.5 TV long-press input handling, 2.0.4/2.0.5 adaptive six-lane collection behavior, one-query-per-process isolation, Vulkan/report correctness and all security/network/build-chain constraints.

## 2.0.5
- Fixed the Android TV evidence long-press release compile blocker by removing the unavailable Compose `KeyEvent.nativeKeyEvent` extension path.
- Reimplemented TV OK/DPAD_CENTER/Enter long-press detection using the public Compose `KeyEvent.key`, `KeyEvent.type` and `Key.nativeKeyCode` APIs plus a bounded 550 ms hold timer.
- Short OK/Enter presses remain unconsumed, while a completed long press opens the existing Evidence provenance/actions dialog and consumes its release to avoid duplicate activation.
- Preserved the 2.0.4 adaptive six-lane scheduler, one-query-per-process isolation, Turnip path snapshot, capability/query coverage, report semantics, desktop secondary-click suppression and all build-chain versions unchanged.

## 2.0.4
- Increased the adaptive one-shot background scheduler ceiling from four to six isolated private process lanes on devices with sufficient physical memory, available memory and CPU capacity, while retaining automatic 2/3/4/5-lane fallback under tighter resource conditions.
- Resolved the active Turnip driver library/bundle paths once per background collection pass and reused that immutable snapshot across its one-shot probes, removing repeated private-storage metadata/library resolution without changing driver identity or query coverage.
- Added Android TV evidence actions on a long press of remote OK/DPAD_CENTER/Enter: holding the focused evidence row opens the existing Evidence provenance/actions dialog. Normal directional navigation and short OK/Enter presses remain unchanged, and desktop secondary-click suppression remains preserved.
- Retained one Vulkan query group per dedicated process lifetime, deterministic original-order merge, existing timeout/crash/cancellation recovery, 60-second background budget, Vulkan 1.4.364 registry/query coverage and report semantics.

## 2.0.3
- Replaced the conservative Java-heap-class lane heuristic with Android low-RAM state, current memory-pressure state, physical total/available memory and processor-count signals so capable phones can use four isolated one-shot lanes instead of being incorrectly held to two lanes.
- Replaced static modulo assignment with a bounded work-stealing-style atomic scheduler: each lane claims the next pending Vulkan query only after finishing its current one, avoiding tail stalls when one lane receives a slower query sequence.
- Removed the unconditional 150 ms pre-launch teardown sleep. A lane now fast-paths immediately when its previous private process is already gone; stale processes are still force-terminated and verified quiescent within the existing 1.5 second bound before a new query may start.
- Reduced bounded result-publication polling intervals so durable terminal results are observed with less host-side latency while preserving terminal JSON validation, process isolation, timeouts and size limits.
- Corrected diagnostic timing so `Slowest dedicated probe` uses the actual isolated host round-trip rather than queue wait plus probe time, and added `Longest scheduler wait` as a separate metric.
- Preserved the 2.0.0/2.0.2 one-query-per-process correctness model, Vulkan 1.4.364 registry/query coverage, deterministic report merge order and all capability semantics.

## 2.0.2
- Withdraws the 2.0.1 multi-query-in-one-process experiment and restores the 2.0.0 one-query-per-process ownership model so every background query again receives a fresh isolated Vulkan process and teardown boundary.
- Keeps the complete 2.0.0 query census and report merge semantics intact while reducing orchestration wall time through up to four independent one-shot background probe lanes. The runtime automatically lowers the active lane count on smaller Android heap classes.
- Keeps base, metadata, Surface and explicit ad-hoc probes on the original primary one-shot lane; only automatic background detail queries use the bounded parallel lane pool.
- Adds a diagnostic metric for the active parallel isolated-lane count while retaining all existing per-query timing evidence.
- Fixes the Surface landing destination so collection/network/update overlays reserve real top clearance there just as they already do on lazy capability pages; bottom navigation and side system insets are reserved as well.
- Suppresses the evidence right-click quick menu on desktop/PC/ARC-style Android environments while preserving touch long-press evidence actions and accessibility custom actions.
- Preserves Vulkan 1.4.364 registry/header locks, 477 registered / 305 Android-queryable extension coverage, report/Database semantics, crash isolation, timeouts, cancellation recovery, permissions, updater security and AGP/Gradle versions from 2.0.0.

## 2.0.0
- Added bounded collection-timing instrumentation without changing Vulkan capability-query semantics or completeness gates.
- Measures cold-start startup-gate delay, total collection time, base collection, metadata/Surface enrichment, background-detail collection, base probe attempts/decoding and every dedicated background probe round trip.
- Added isolated-probe service timing telemetry for process dispatch, native library loading, JNI collector wall-clock time, terminal validation and pre-terminal service time. Telemetry is written to a private cache sidecar capped at 16 KiB, validated to remain beside the service-owned result file, consumed locally and deleted after each probe.
- Expanded Analysis → Collection diagnostics with total/base/enrichment/background timing summary, dedicated-probe counts and slowest-probe evidence while explicitly distinguishing JNI collector wall-clock timing from per-Vulkan-command timing.
- Preserved Vulkan 1.4.364 registry/header locks, report/Database semantics, crash isolation, timeout/cancellation handling, permissions, updater security, native ownership and build-chain versions from 1.5.5.

## 1.5.5
- Fixed landscape page content width by honoring Android navigation-bar side insets inside the shared `VulkanLazyPage` container, preventing right-edge cards, text and chevron actions from rendering underneath the system navigation strip.
- Applied the same side-inset reservation to scroll indicators so landscape page arrows remain visually aligned with the narrowed safe content region.
- Preserved the 1.5.4 compact landscape bottom-navigation footprint and the 1.5.3 dynamic top/bottom obstruction clearances.
- No Vulkan/native/report/spec/build-chain behavior change; this release is a targeted landscape safe-width correction for page content.

## 1.5.4
- Fixed the compact landscape navigation bar so its width cap is actually enforced; the previous modifier chain still let the bar expand across almost the full screen width.
- Added a dedicated landscape compact-navigation size profile with a smaller floating height, indicator, icon and label rhythm so the bottom tabs visually match the tighter Telegram-style landscape treatment more closely.
- Preserved the 1.5.3 dynamic top/bottom obstruction clearances, so stacked transient banners and the floating compact tab bar continue to push page content clear of covered regions.
- No Vulkan/native/report/spec/build-chain behavior change; this is a targeted landscape responsive-navigation correction.

## 1.5.3
- Moved the compact bottom tab bar above the system navigation area by applying Android navigation-bar insets, fixing the misaligned/overlapped tab presentation seen on three-button navigation devices.
- Made every main capability page reserve dynamic top and bottom content clearance for transient status overlays and the bottom tab bar, so the last actionable controls can always be scrolled fully above those floating surfaces instead of being covered by them.
- Made the top clearance react to the real stacked overlay height, so multiple collection/network/update banners push content and scroll indicators down while shown and release that space again when they disappear.
- Kept Vulkan/native/report/spec/build-chain behavior unchanged from 1.5.2; this release is a targeted navigation/overlay usability, accessibility and obstruction fix.

# 1.5.2

- Updates the Android Gradle Plugin from 9.4.0 to the current stable 9.4.1 patch release while retaining the compatible Gradle 9.7.1 wrapper, Kotlin Compose plugin 2.4.10, compile/target SDK 37 and NDK r29 toolchain.
- Pulls in the AGP 9.4.1 D8/R8 correctness, crash, performance and deterministic shrinking fixes without changing VulkanScope runtime code, permissions, endpoints, report schemas or native query behavior.
- Re-audits the Vulkan 1.4.364 registry/header lock, report/profile/surface semantics, lifecycle and cancellation ownership, bounded archive/report handling, update and Database network trust boundaries, accessibility navigation targets, ABI policy and package hygiene.
- Keeps Vulkan 1.4.364 as the locked current published Vulkan baseline; no speculative registry, profile requirement or capability change is introduced.
- Adds a 1.5.2 release verifier, compatibility/accessibility state model, targeted negative mutations and immutable-predecessor regression boundary.

# 1.5.1

- Expands Vulkan Profiles presentation with mapped requirement totals, met/verified-unmet/unknown counts, visible aggregate metrics and per-category breakdowns while preserving exact missing/unknown evidence and UNKNOWN semantics.
- Prevents satisfied OR requirements from reporting failed checks from an unnecessary alternative branch and keeps unavailable mapped evidence explicitly unknown.
- Reduces the shared four-tab floating navigation to a 310 dp maximum width, keeps the compact 54 dp height and moves it to a 4 dp visual gap immediately above the platform navigation controls in both portrait and landscape.
- Replaces the moving/global selection effect and rectangular press indication with a cell-local, fully clipped capsule using bounded alpha/scale motion so selection never exposes square overflow.
- Refines the opening sequence into a restrained full-screen dark/radial-light/logo/accent-line animation that completes inside the existing startup watchdog budget.
- Keeps Vulkan collection, registry/profile definitions, native/JNI implementation, permissions, endpoints, ABIs and dependency pins unchanged from 1.5.0.

# 1.5.0

- Compacts and repositions the shared four-tab floating navigation so portrait and landscape use the same smaller bottom surface immediately above the system navigation region.
- Reduces navigation icon/label geometry and replaces the oversized edge-stretch selection effect with a bounded elastic capsule translation that cannot span intermediate tabs.
- Couples scroll-boundary hint clearance to the exact floating-navigation geometry so scroll arrows remain above the bar.
- Compacts collection/network/update status overlays without changing their state semantics or update actions.
- Adds a bounded startup watchdog so the opening animation cannot remain indefinitely on its pre-animation frame when the platform splash handoff callback is absent; the fallback is independent of Vulkan device availability.
- Keeps Vulkan collection/report/Profile/Database semantics and native implementation unchanged from 1.4.16.

## 1.4.16

- Rebuilds the requested changes directly from immutable 1.4.15.
- Changes the primary floating navigation to four equal tabs and makes the resting selection indicator a true inset horizontal capsule while preserving the bounded elastic selection motion.
- Moves Display & HDR under Surface. Surface now opens a three-card chooser: Display & HDR, Surface & color spaces, and Presentation.
- Makes collection, network/offline and update-status messages translucent floating overlays instead of TopAppBar layout banners; modal dialogs and unrelated menus are unchanged.
- Keeps Vulkan collection, Display/Surface evidence semantics, reports, Database submission, native code, permissions, ABIs and dependency pins unchanged.

# 1.4.15

- Uses one centered floating five-tab navigation bar in portrait, landscape and expanded/freeform layouts; the landscape navigation rail has been removed.
- Matches the supplied reference geometry more closely with a 352 dp maximum width, 62 dp height, centered landscape placement and orientation-aware bottom spacing.
- Replaces independent tab selection backgrounds with an elastic moving indicator whose leading edge stretches toward the destination before the trailing edge catches up, with temporary source/destination accent overlap during the handoff.
- Keeps page content scrollable behind the translucent floating bar while moving main-page scroll-boundary arrows above the overlay so they remain visible and unobstructed.
- Retains the five primary destinations, Profiles→Overview selection mapping, Vulkan/Profile/report behavior and 1.4.364 registry/query baseline unchanged.

# 1.4.14

- Fixes the compact primary navigation regression that could render only the selected destination instead of all five primary destinations.
- Converts compact navigation into a true translucent floating overlay so scrolling page content can pass beneath it while the final content remains reachable above the overlay.
- Tightens bar height, horizontal inset, icon/label alignment and selected-pill geometry to the supplied reference layout.
- Applies the same visual hierarchy and accessibility-selected semantics to the landscape/wide navigation rail.
- Keeps Profiles mapped to Overview selection and leaves Vulkan collection/report behavior unchanged.

## 1.4.13

- Replaces the hand-built compact primary navigation item geometry with Material 3 Expressive `ShortNavigationBar` / `ShortNavigationBarItem`, giving the compact bar the platform-defined 64 dp layout, 24 dp top icon and component-owned full-corner selected indicator.
- Replaces the hand-built landscape/expanded navigation item geometry with Material 3 `NavigationRail` / `NavigationRailItem`, preserving the same selected-indicator model in the vertical layout.
- Keeps the floating rounded VulkanScope navigation container, system-navigation inset separation, persistent labels, adaptive phone/tablet/landscape selection, desktop wheel scrolling and TV/keyboard focus behavior.
- Keeps Overview selected when opening Profiles and retains the existing opening-animation icon normalization and detailed Vulkan Profiles UI/JSON/TXT/HTML/Database report evidence.

# 1.4.12

- Fixes the release Kotlin compile failure caused by the top-level Vulkan Profiles HTML helper referencing the `reportToHtml`-local `statusBadge` function.
- Keeps the reference-screenshot-style primary navigation treatment with a single floating rounded container and selected icon-pill state; Profiles now correctly keeps Overview selected.
- Keeps the Opening animation glyph as the header icon with VulkanScope accent tint and corrected 20 dp visual size.
- Preserves detailed Vulkan Profiles evidence across UI, technical JSON, TXT, HTML and the Database submission payload, while incomplete/coverage-limited evidence remains UNKNOWN rather than being guessed as supported.
- Restores the validated probe teardown/timeout/checkpoint settle timings required by the lifecycle and hardening gates while retaining prioritized query ordering.

# 1.4.11

- Reworked the shared floating navigation styling to align more closely with the requested floating-tab visual model while preserving VulkanScope's five primary destinations.
- Fixed shared-navigation selection so opening Profiles from Overview keeps Overview visually selected.
- Refined the Opening animation header icon so the supplied animation glyph replaces the generic information treatment with adjusted VulkanScope-consistent sizing/tint.
- Expanded Vulkan Profiles detail presentation and exports: JSON now retains detailed failing/unknown arrays, TXT adds indented profile-detail lines, and HTML shows richer per-profile detail.
- Added visible/pass/fail/unknown profile summary metrics on the Profiles page.

## 1.4.10

- Corrects the Opening animation preference icon placement: the supplied animation glyph now replaces the generic information glyph in the Opening animation section header.
- Removes the duplicate animation glyph from the preference row so the section contains one semantic header icon and the switch row remains focused on label, status and toggle.
- Converts the supplied dark-background icon asset into a transparent neutral-gray presentation asset for clean rendering inside the existing Material 3 Expressive header container without changing its glyph geometry.

## 1.4.9

- Restores visible scroll-boundary hint arrows for desktop-style wheel and mouse interactions by driving hint visibility from actual scroll-position changes, not only touch-style in-progress state. This fixes the missing up/down indicator feedback when using the mouse wheel or other rotary-style scrolling inputs.
- Keeps markdown-backed document surfaces scrollable with the shared desktop pointer-scroll path and shared expressive scroll hints so release notes, packaged licenses and related long-form text views retain consistent desktop-style navigation affordances.
- Adds the supplied opening-animation preference icon asset and uses it in the Preferences > Opening animation switch row.

## 1.4.8

- Reworks the startup animation presentation with a layered high-quality red glow, a softer framed center panel and larger horizontal-logo treatment in landscape so the opening screen no longer shows the coarse red block artifact.
- Rebuilds compact bottom navigation and landscape navigation rail into rounded floating containers with selected icon pills, centered icon+label layout and consistent behavior across phone, tablet, freeform and desktop-style Android form factors.
- Adds secondary-click quick actions for evidence rows on mouse-driven Android environments such as Googlebook OS: right click now opens Copy name + value, Share evidence, Add to watched evidence, Open in Encyclopedia and More details.
- Keeps the existing wheel + primary-button drag scrolling path and applies the redesigned navigation/quick-action behavior without removing keyboard, touch, accessibility or long-press evidence flows.
- Improves Vulkan background collection responsiveness by prioritizing the most visible advanced groups first and tightening bounded dedicated-probe teardown/handoff polling so the app reaches usable collected state faster without changing report schemas, validation boundaries or the isolated one-shot probe model.

## 1.4.7

- Fixes the release Kotlin/JVM setter-signature collision by keeping the `openingAnimationEnabled` property and renaming its persistence helper so `setOpeningAnimationEnabled(Z)V` is generated only once.
- Adds generic Android mouse-pointer vertical scrolling across VulkanScope scroll surfaces: mouse wheel events use Android's platform vertical-scroll factor and primary-button drag pans content after touch slop without stealing ordinary clicks.
- Applies pointer scrolling to primary Vulkan pages, navigation rail, detail dialogs, file managers, update/database logs, release notes and filter lists while preserving touch, keyboard, accessibility and existing startup-animation gating behavior.
- Keeps Vulkan 1.4.364 registry/header/query data, native collector, report schemas, Database/updater boundaries, permissions, ABIs and packaged artwork unchanged from 1.4.6.

## 1.3.9

- Keeps the shared single-filter popup open while live Vulkan collection expands its label set, anchors the popup to the selector, and makes Back dismiss the search keyboard/focus before the popup itself.
- Keeps strict filter search/pagination behavior while clipping selected/pressed visuals to the actual clickable rounded rows.
- Restores complete-report export presentation so TXT and HTML are always separate full-width Material 3 Expressive actions instead of a fragile weighted side-by-side layout.
- Refines the in-app import/export browser with explicit VulkanScope dark content colors, clipped Material interactions, Expressive search/scroll hints and a fixed export-type suffix: only the base file name is editable.
- Replaces Turnip List/Compact text selectors with four semantic icon modes: List, Compact, Grid and Details; Grid uses a bounded adaptive lazy grid and Details expands package/folder metadata.
- Makes Turnip package cards selectable across their clipped card geometry so selection state and actionable geometry match, while keeping the independent Info action and all existing archive/slot validation.
- Removes legacy storage-architecture wording from runtime UI copy without changing permission, confinement, atomic-write, report-schema, Vulkan/native/JNI, Database, updater or Turnip-validation behavior.

## 1.3.7

- Fixes the release Kotlin compile failure at the four Material3 Expressive `LoadingIndicator` call sites by adding narrow function-scope `ExperimentalMaterial3ExpressiveApi` opt-ins to the Turnip file-manager and shared-storage browser dialogs.
- Keeps the existing loading UI and all 1.3.6 storage/import/export behavior unchanged; no file-wide opt-in, dependency upgrade, native-code change or third-party repin is introduced.
- Treats the libadrenotools C/C++ messages from the supplied build as non-fatal warnings and leaves that pinned third-party source unchanged.

## 1.3.6

- Completes the SAF-free non-Turnip storage exchange: Analysis snapshots, minimum profiles, raw `technicalReport` JSON, TXT and HTML now use the in-app shared-storage browser instead of remaining unavailable.
- Requests the existing Android all-files special access only after explicit import/export actions and retains the three-second animated X / Permission denied feedback when access is refused.
- Confines browsing and destinations to canonical primary shared storage, rejects symlink/path escapes and unsafe names, bounds directory enumeration, filters import extensions and performs filesystem work off the Compose/UI thread.
- Adds explicit overwrite confirmation plus same-directory temporary writes with flush/fsync and atomic replace where supported, cleaning failed temporary output.
- Retains Analysis snapshot schema/8 MiB validation, minimum-profile schema/256 KiB and rule bounds, exact schema-v3 `technicalReport` serialization, and complete-report readiness for TXT/HTML.
- Makes TXT/HTML export use a bounded private cache snapshot at export initiation with saveable pending metadata and cleanup after cancel/success/stale recovery.
- Keeps Turnip import/file-manager behavior, updater/Database security, Vulkan/native/JNI collection, registry/generated data, report schemas, API/ABI/dependency pins and manifest permission set unchanged from 1.3.5.

## 1.3.5

- Fixes a Kotlin compile blocker caused by a duplicate `registryCoverage` declaration in Info.
- Hardens the Turnip file manager against stale concurrent scans with owned job/generation state, cooperative cancellation, loading navigation guards and cleared rows while a new directory is loading.
- Restores standard Material press/ripple and accessibility click semantics to Turnip folder rows and the shared single-select filter control.
- Makes the file-manager selected/remaining and List/Compact header responsive at large font scales and narrow widths.
- Moves ZXing QR generation off the Compose/UI thread and rethrows Turnip-import coroutine cancellation instead of publishing stale failure UI after lifecycle teardown.
- Rebuilds the current 1.3.5 quality-gate route so superseded SAF/exact-string historical gates remain documentary while current Vulkan/spec, compile-static, lifecycle, report/profile/video, security and release contracts execute.
- Regenerates the strict release file census and retains Vulkan 1.4.362/header 362, API 37, report schemas, native/JNI collection, ABIs, dependency pins and fixed network-security boundaries unchanged.

## 1.3.4

- Adds the SAF-free in-app Turnip ZIP file manager using Android all-files special access only when the explicit Import driver ZIP action requires it.
- Shows folders plus prevalidated Turnip ZIPs only, with bounded scanning, search, List/Compact modes, package Info, Mesa artwork, modified date/size and remaining-slot-limited multi-selection.
- Revalidates selected archives before private-slot installation and retains the existing 10-slot Turnip validation/security transaction.

## 1.3.3

- Removes VulkanScope-owned SAF OpenDocument/CreateDocument routes and their SAF-specific storage fallback paths without introducing a replacement storage frontend in that release.
- Keeps already-installed Turnip slots and private bundle validation/activation/removal intact while Analysis/TXT/HTML/JSON storage exchange remains explicitly unavailable.

## 1.3.2

- Reworks the built-in GitHub APK download into a persistent in-place update transfer dialog after the existing explicit Download update confirmation. The dialog shows connection state, live transfer speed, byte/progress information and a bounded auto-scrolling monospace live log.
- Adds explicit Pause / Resume and Cancel controls. Cancel first pauses the transfer and opens a question dialog; Resume is an uncontained text action while Cancel download is a contained X action. Confirmed cancellation stops the active HTTP call, removes the partial APK, records cancellation in the live log and leaves a terminal Update canceled card with Close-X.
- Stops auto-opening Android's package installer when verification completes. A verified download remains visible as Update downloaded with a check icon until the user explicitly chooses the contained Install action or closes the terminal card.
- Preserves the official GitHub release URL restriction, 256 MiB download ceiling, private-cache confinement, package/signature/version validation, validated-network gating and explicit Android installer handoff. No new endpoint, permission, background download or silent-install path is added.

## 1.3.1

- Replaces shared single-select horizontal filter carousels with a bounded dropdown selector that provides case-insensitive search, up to 50 choices per page, previous/next paging and direct numeric page entry while preserving the standalone All switch and existing filter semantics.
- Keeps multi-select usage filters unchanged and leaves filtering as a presentation-only operation over already collected evidence.
- Adds an HDR-capability disclosure above detected-type artwork explaining that displayed official logo artwork corresponds to HDR types reported by Android on the current device, while HLG and HLG+ remain text-rendered because no authoritative official-logo asset is used for those formats.
- Retains Android HDR type mapping including API-37 HLG+ value 6, Vulkan 1.4.362/header 362, native collection, report schemas, storage/update behavior, fixed HTTPS origins, ABI policy and dependency pins unchanged.

## 1.3.0

- Presents the Vulkan API name as `Vulkan®` throughout VulkanScope UI text while preserving the `VulkanScope` product name and leaving canonical runtime evidence, Vulkan registry tokens, report keys and internal query identifiers unchanged.
- Adds a contained right-chevron `License` action under every direct application/native library in Info. License documents open in a bounded, scrollable markdown-style dialog with text artwork and a contained Close-X action.
- Packages the applicable Apache-2.0, BSD-2-Clause and Vulkan-Headers Apache-2.0 OR MIT license texts locally so license disclosure needs no network access.
- Retains Android 12 / API 31 minimum, Vulkan Registry/header 1.4.362, native collection, report schemas, fixed HTTPS origins, storage/update behavior, ABI policy and dependency pins unchanged.

## 1.2.5

- Fixes abrupt Settings destination entry: choosing Info, Reports & Database, or Driver & Update Preferences now transitions the chooser into the selected destination with bounded horizontal slide + fade motion instead of replacing the content immediately.
- Adds the matching bounded reverse transition when Back returns from a Settings destination to the three-card chooser.
- Keeps the existing staggered chooser-card entrance, shared ExpressiveDestinationCard component, switch/search/watch/history/share behavior, Android 12 minimum, Vulkan 1.4.362/header 362, report schemas, fixed HTTPS origins and native collection unchanged.

## 1.2.4

- Adds a History `Clear all` action matching the watched-evidence destructive flow: it is disabled when empty, asks for confirmation, uses the contained clear-all action plus Close-X hierarchy, and deletes retained local history through the existing bounded record-deletion path before reloading storage.
- Gives the three Settings destinations a finite Material 3 Expressive entrance transition: 260 ms fade plus short horizontal slide, staggered by 45 ms in Info → Reports & Database → Driver & Update Preferences order, while continuing to use the exact shared destination-card component.
- Changes only the trailing action artwork of Share link and Share evidence from the chain-link glyph to the same diagonal external-open glyph used by Open VulkanScope Database; the Share leading icon and Android Sharesheet behavior are unchanged.
- Retains Android 12 / API 31 minimum, Vulkan 1.4.362/header 362, schema 2/technicalReport 3, fixed HTTPS Database/update origins, native collection, three release ABIs and existing resource/privacy/security ceilings.

## 1.2.3

- Fixes the 1.2.2 Kotlin compile failure in watched-evidence input resolution by iterating the typed `Map.entries` collection instead of calling `firstOrNull` on `Map` itself.
- Makes Database submission results typed: a successful submission shows the complete lowercase 64-character report ID in bold primary text, while every failed submission result opens a bounded monospace diagnostic dialog with Database+X artwork, contained Copy all and contained Close actions.
- Treats a successful HTTP response without a valid 64-hex report ID as a failed response-validation result instead of reporting success without a usable report identity.
- Raises the APK minimum platform to Android 12 / API 31 and removes the now-unnecessary legacy external-storage permission and pre-Android-10 report-export permission path. Android 11 and earlier cannot install the APK.
- Preserves Vulkan 1.4.362/header 362, schema 2/technicalReport 3, fixed HTTPS Database/update origins, report privacy rules, native Vulkan collection, three release ABIs and existing resource ceilings.

## 1.2.2

- Removes the separate header Info action and makes Settings open a three-destination chooser containing Info, Reports & Database, and Driver & Update Preferences. The destination cards share the exact Overview Encyclopedia/Analysis card component and Back returns to the chooser before leaving Settings.
- Restricts all Switch interaction to the Switch control itself. The shared All filter keeps specific filters locked while enabled without making the surrounding explanation row toggleable.
- Adds animated trailing X clearing to every shared search field and validates watched-evidence input before enabling Add to watch list. Single-item removal and Clear all now require explicit question dialogs; Clear all is disabled for an empty list.
- Keeps three-second success/failure result states for copy/watch/link/database actions without advertising the timer in their text, changes Share trailing actions to the link glyph, and restores the 1.0.19 Check for updates leading download glyph while preserving the receive trailing glyph and 1.2.1 in-flight lock.
- Adds requested semantic artwork: Registry+REG, SCOPE+Info, Android+RUN, search badges for Extension/Format explorers, distinct Instance/Device layer composites, tablet Display artwork, contained prohibition+filter Clear usage filters, and HDR capability carousel arrows.
- Gives the System↔Turnip A/B actions chip/Android/user-supplied Mesa primary artwork with the existing compare glyph lower-right, using only local packaged assets and the VulkanScope action palette.
- Retains Vulkan 1.4.362/header 362, schema 2/technicalReport 3, fixed HTTPS origins, native Vulkan collection, driver/update confirmation, report semantics, resource ceilings and privacy/security behavior unchanged.

## 1.2.1
- Reorganizes Settings into three nested destinations ordered Info, Reports & Database, and Driver & Update Preferences; the third destination owns Vulkan driver management, direct GitHub update preference and Obtainium guidance.
- Disables Check for updates only while the update request is awaiting a result, then re-enables it immediately on success, failure or cancellation.
- Separates result-banner expiry from request lifetime so an older timeout cannot hide a newer update result.
- Retains Vulkan 1.4.362/header 362, native collection, report/Database schema, Turnip package handling and update-download security unchanged.

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


## 1.3.8
- Refined the shared single-filter selector with Material 3 Expressive popup motion, animated chevron/enabled state, shared search UI, wrapped labels and vertical scroll hints.
- Hardened filter pagination input so only existing numeric pages are accepted; one-page controls remain visible but disabled.
- Reworked Turnip file-manager colors to explicit VulkanScope dark content colors, fixing dark-on-dark text from real-device screenshots.
- Replaced List/Compact text buttons with an expressive segmented control and aligned search/footer/details presentation with the VulkanScope Material 3 Expressive language.
- No Vulkan/native/report/Database/updater/storage-security behavior change.

## 1.3.10
- Restricted the shared single-filter selector opener to the contained VulkanScope-red chevron action; the informational selector body no longer reacts to taps or draws press feedback.
- Smoothed filter popup entry/exit and page/search result transitions, and kept page controls in a fixed footer region that remains visible while the result list flexes around IME/search changes.
- Restricted Turnip folder navigation to contained red right-arrow actions in list/compact/details and grid presentations.
- Presented the existing official packaged Mesa logo through one consistent tonal/outlined badge in Turnip ZIP cards and package information without modifying the logo asset.
- Kept already imported Turnip ZIP sources visible when the exact stored source location and filename match, but render them muted and block selection, info actions and re-import.
- Added a yellow About disclosure stating that VulkanScope is not an official Khronos Group project, separated from the existing description by visual spacing.
- No Vulkan/native/report/Database/updater/storage-security or dependency change.

## 1.3.11
- Fixed the real release Kotlin compile blocker by importing the Compose `fillMaxHeight` layout extension used by the filter popup.
- Hardened update-status, update-preference and updater-dialog colors to the explicit VulkanScope dark Material 3 Expressive palette, including dialog title/text content colors and nested updater surfaces.
- Kept updater Pause/Resume/Cancel/Install state behavior, APK provenance/signature/version validation and partial-download cleanup unchanged.
- Left pinned libadrenotools warning-only diagnostics unchanged because they are not the cause of the Kotlin build failure.


## 1.3.12
- Made compact single-filter popups omit search when fewer than five filter choices exist and omit all pagination controls when the filtered result fits on one page.
- Added staged popup mount/reveal motion and a contained red close action beside the search field; re-opening a filter now starts on the currently selected filter instead of the beginning of the catalogue.
- Fixed direct page-number editing so the current value can be deleted and replaced from the numeric keyboard while valid pages remain strictly bounded to the current page count.
- Applied the Turnip-style contained red right-arrow interaction to the shared in-app storage browser used by JSON/TXT/HTML/profile/report import/export flows; folder/file card bodies are informational only.
- Refined System Vulkan driver and managed Turnip slot presentation with Material 3 Expressive badges, state treatment, tonal evidence pills and clearer active/inactive card hierarchy without changing activation/removal semantics.
- Retained the 1.3.11 compile fix, updater state/security rules, storage confinement/type locking, Vulkan collection and report/database behavior.

## 1.3.13
- Restyled every Mesa/Turnip logo presentation with VulkanScope accent-red tint/container treatment while preserving the packaged Mesa logo bytes.
- Replaced the System-driver Android badge with the same packaged GPU-vendor logo family used by Overview, sized inside the existing red driver badge.
- Corrected Turnip metadata labels so driver identity is shown as Driver name and package `driverVersion` metadata is identified as Vulkan version; the existing slot/state Vulkan-version line remains unchanged.
- Reworked the single-filter menu as a normal expanding section directly beneath its selector so it cannot flip above or overlap the selector, and the owning page remains scrollable while the menu is open.
- Made the contained red X the only enabled-menu close action; outside/page taps and filter-result selection no longer dismiss the menu, and Back only clears an active IME while the menu is open.

## 1.4.0
- Unified the remaining legacy Format explorer multi-filter carousel with the same in-layout Material 3 Expressive selector language used by VulkanScope's other filters; active production filter UI no longer uses the old horizontal FilterChip carousel.
- Removed the dedicated filter X action. The contained chevron now toggles both opening and closing while keeping result selection and ordinary page interaction non-dismissing.
- Restored the filter search control to full menu width after removing the X, while retaining selected-row restoration, bounded pagination and smooth in-layout motion.
- Replaced the Overview Quick access section's home glyph with a dedicated VulkanScope-accented 3x3 nine-dot grid icon matching the supplied visual reference.
- Kept Vulkan collection, reports, Database, updater, storage security, Turnip handling and driver behavior unchanged.

## 1.4.1
- Contained filter-result scrolling so reaching the first or last filter option no longer transfers residual scroll/fling movement to the parent page.
- Applied the same boundary containment to both shared single-selection filters and the Format explorer multi-selection filter without changing normal filter-list or page scrolling.
- Added each managed Turnip slot's validated Vulkan library filename and package description directly to wide and compact slot cards using existing metadata and responsive evidence pills.
- Kept Turnip ZIP validation/import/activation, Vulkan collection, reports, Database, updater and storage-security behavior unchanged.

## 1.4.2
- Reduced the expanded single-filter menu height in landscape so search, results and pagination remain visible without the panel stretching to the bottom edge.
- Kept filter results internally scrollable while retaining the taller established portrait layout.
- Centered the complete pagination control group in both portrait and landscape.
- Reduced the editable page-number field from 96 dp to a compact 72 dp and centered its numeric text.
- Preserved filter scroll containment, Turnip slot metadata, Vulkan collection, reports, Database, updater and storage-security behavior.
- Removed the top-level screenshots folder from the VulkanScope source ZIP package.

## 1.4.3
- Kept the landscape filter surface aligned to the full selector width while retaining the 1.4.2 bounded landscape height and pagination behavior.
- Retained Activity-owned Vulkan report state across orientation and ordinary window-size configuration changes instead of forcing a fresh base recollection.
- Preserved Surface-only refresh semantics when the Android Surface itself is recreated.

## 1.4.4
- Added explicit ChromeOS ARC environment evidence without guessing a host ChromeOS version.
- Expanded complex detail dialogs to use available landscape height with bounded margins and internal scrolling.
- Replaced the custom landscape/TV navigation approximation with Material 3 `NavigationRail` / `NavigationRailItem`, while compact portrait uses Material 3 Expressive `ShortNavigationBar`.
- Added a VulkanScope-branded Android SplashScreen transition using the packaged logo.

## 1.4.5
- Updated the locked Vulkan registry/header baseline to Vulkan 1.4.364 / `VK_HEADER_VERSION 364` from the supplied canonical registry and added complete `VK_INTEL_device_info` returned-properties coverage.
- Added documented Android PC/freeform evidence for Googlebook-class environments while explicitly refusing model/brand/fingerprint heuristics; Googlebook OS identity/version stays unavailable when no public Android API exposes it.
- Added Googlebook/desktop evidence parity across visible UI, Database JSON, complete TXT and complete HTML reports.
- Made primary navigation responsive to the current app window: landscape, TV and windows at least 600 dp wide use the Material 3 rail; compact portrait retains the Expressive short navigation bar.
- Re-ran current Vulkan registry, report semantics, Vulkan Video, lifecycle/timeout/cancellation, hardening, concurrency/resource, security, usability and accessibility source/state-machine gates.
- Kept Android build/device/runtime evidence explicit: Gradle compilation, real Googlebook/ChromeOS hardware, TalkBack/keyboard runtime, validation layers, sanitizers and profiler are not reported as PASS when they were not executed.

## 1.4.6
- Replaced the compact portrait primary navigation with Material 3 `NavigationBar` / `NavigationBarItem`, keeping all five Overview/Vulkan/Surface/Display/Extensions labels visible beneath their icons and the selected destination inside the VulkanScope accent pill; landscape, TV and windows at least 600 dp wide retain the responsive `NavigationRail`.
- Added a full-screen VulkanScope opening sequence using the packaged horizontal logo. The custom motion begins only after the AndroidX system splash exit animation has actually finished, so its timing is not consumed invisibly under the platform splash.
- Added a startup gate so enabled opening animation defers Vulkan report/query collection, Android display inspection, default-network observation and the automatic startup update check until the custom opening sequence completes. Interrupted Activity recreation remains gated; recreation after completed startup does not replay the sequence.
- Added a separate `Opening animation` card directly below Update preferences in Driver & Update Preferences. Its Expressive switch persists immediately with no confirmation dialog, defaults to enabled and applies on the next cold launch.
- Retained Vulkan 1.4.364 registry/query coverage, report/Database schemas, native collector/service, permissions, ABI/dependency pins, packaged artwork, updater security and existing resource/concurrency hardening unchanged from 1.4.5.
