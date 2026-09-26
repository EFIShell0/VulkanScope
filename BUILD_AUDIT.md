# VulkanScope 1.5.1 profile/navigation/opening audit

## Evidence and scope
- Immutable predecessor: VulkanScope 1.5.0, SHA-256 `484e25750efa956be5ee7ff1d2ebda7acd6e6d31049d4c49c27f8e1aac0c09f9`, 697 archive entries.
- Two supplied runtime recordings were inspected before editing. The repair targets profile evidence readability, reference-sized/positioned four-tab navigation, cell-bounded selection feedback and opening-animation polish.
- Runtime production changes are limited to `MainActivity.kt`; `app/build.gradle.kts` changes version identity only. Native/JNI, manifest, registry/generated data, resources and dependency pins remain predecessor bytes.

## Implemented repair
- Profiles now expose mapped/met/verified-unmet/unknown totals, visible aggregate counts and extension/feature/property-limit/format/inherited-profile breakdowns. Unknown evidence remains unknown and catalog-only profiles remain zero-check UNKNOWN.
- The floating navigation is 310 dp maximum width by 54 dp high with a 42 dp local selected capsule, 8 dp horizontal capsule inset and 4 dp bottom visual gap. Scaffold duplicate safe-drawing inset is suppressed.
- Selection no longer uses a translating global indicator or default rectangular indication; each selected tab owns a clipped capsule with bounded alpha/scale motion.
- Opening animation is a full-screen dark/radial-light/logo/accent-line sequence with 1600 ms staged timing, inside the unchanged startup watchdog.

## Verification
- Targeted 1.5.1 static verifier/state/negative-mutation and predecessor production-boundary gates: PASS.
- Existing profile requirement/state/negative-mutation gates: PASS.
- Full 1.5.1 quality-gate route: PASS, including locked registry/spec, report integrity, profile, video, hardening, lifecycle, concurrency/resource and source package-hygiene gates.
- Android Gradle compile/lint/unit: NOT EXECUTED because the wrapper cannot resolve `services.gradle.org` in this container and fails before project tasks begin.
- Device/emulator visual/runtime/accessibility/profiler replay: NOT EXECUTED in this container.
- Clean-extract package reproducibility and final 1.5.1 static-gate rerun are required before release delivery.

# VulkanScope 1.5.0 compact floating-navigation and startup-liveness audit

## Evidence and classification
- Immutable predecessor: VulkanScope 1.4.16 release ZIP.
- Observed failures reproduced from supplied runtime captures: the floating primary navigation sat too far from the system navigation region, the selection capsule and tab content occupied excessive vertical/visual area, the prior edge-based selection motion could expand across too much horizontal space, scroll-down hints did not share one geometry contract with the floating navigation, transient collection/network/update surfaces remained wider than necessary, and the custom opening sequence could remain indefinitely at its pre-animation frame when the platform splash handoff callback was not observed.
- Classification: `REGRESSION` + `USABILITY` + `CORRECTNESS/STARTUP` + `TEST-GAP`.

## Production changes
- Primary navigation remains the same four destinations from 1.4.16 and remains a single bottom floating bar in portrait and landscape; no navigation rail was reintroduced.
- The bar is now capped at 326 dp by 54 dp with 20 dp icons and compact persistent labels. The selected capsule is 44 dp high with 7 dp horizontal inset inside one equal cell.
- Selection motion uses bounded center translation plus a 1.24 -> 0.96 -> 1.0 elastic width pulse rather than unbounded leading/trailing edges, preventing non-adjacent selections from stretching across intermediate tabs.
- The bar no longer adds the system-navigation bottom inset after Scaffold safe-content padding has already applied it. It keeps a 10 dp visual gap above the safe system-navigation region.
- Scroll content and scroll-boundary hints use one shared 74 dp overlay-clearance contract derived from the floating bar geometry, keeping the down indicator above the navigation surface.
- Collection/network/update status surfaces remain top-content overlays and were compacted to a 520 dp maximum width, 22 dp shape and reduced internal geometry. Navigation remains unobstructed.
- A bounded opening-sequence watchdog now makes custom-animation startup independent of Vulkan device availability and independent of a missing platform splash exit callback. It begins the visible custom sequence after a 900 ms fallback handoff and opens the startup gate after a further 2100 ms only if the normal completion callback still has not completed.
- Vulkan native collection, registry/header baseline, Surface/Display evidence separation, Profiles evaluation, report serialization/Database data, permissions and ABIs remain predecessor-equivalent.

## Targeted verification
- `tools/verify_release_1500.py`: PASS.
- `tools/test_release_1500_state_machine.py`: PASS.
- `tools/test_release_1500_negative_mutations.py`: PASS, including unrelated-document false-positive control.
- `tools/verify_release_1500_regression.py`: PASS against immutable 1.4.16 predecessor.
- Android `:app:compileReleaseKotlin` was attempted through the Gradle wrapper. Gradle 9.7.1 bootstrap could not resolve `services.gradle.org` and stopped with `UnknownHostException` before project Kotlin compilation; Android compile is therefore `NOT EXECUTED`, not PASS.
- Device/emulator runtime and visual comparison remain separate evidence classes and are `NOT EXECUTED` in this container.

# VulkanScope 1.4.16 Surface/status/navigation audit

## Immutable predecessor
- Source: `/mnt/data/VulkanScope-1.4.15.zip`.
- SHA-256: `7ecebe3ad9adea65580b713bc9c754b1253249743666e957fab43ba38bd61dc4`.
- Predecessor file census: 654 files.
- This release was rebuilt directly from the extracted 1.4.15 ZIP; the earlier incorrect 1.4.16 working tree was discarded.

## Requested defects and classification
- `USABILITY/REGRESSION`: five primary tabs made the selected indicator too close to square geometry for the requested floating-navigation reference.
- `USABILITY/INFORMATION-ARCHITECTURE`: Display remained a standalone primary tab instead of being the first destination inside Surface.
- `USABILITY`: collection/network/update status messages were still structural TopAppBar banners rather than floating transient overlays.

## Production repair
- Primary navigation is four equal destinations: Overview, Vulkan, Surface, Extensions. The selected indicator is inset and fully capsule-shaped while retaining the bounded two-edge elastic selection motion.
- Surface now owns a three-card chooser: Display & HDR, Surface & color spaces, Presentation. Existing evidence is redistributed only at presentation level; report/native data is unchanged.
- Back from a Surface subsection returns to the Surface chooser. Display quick access enters the nested Display & HDR subsection and Surface remains the selected primary destination.
- Collection, network/offline and update-status surfaces now use one translucent floating status container and are overlaid over content. Modal dialogs and unrelated popup/menu surfaces are intentionally unchanged.

## Evidence boundary
- Targeted 1.4.16 verifier/state/negative/regression gates are added and routed from the quality gate.
- Existing Vulkan registry/spec, report semantics, Surface integrity, Profiles, Video, hardening, probe lifecycle and concurrency/resource gates remain in the 1.4.16 route.
- Android Gradle compile/lint/unit are a separate evidence class and may be reported PASS only if Gradle actually executes them.

# VulkanScope 1.4.15 unified floating-navigation audit

## Evidence and classification
- Immutable predecessor: VulkanScope 1.4.14 ZIP.
- User-provided portrait/landscape reference media and the supplied interaction recording were inspected before editing. The reference bar uses a single centered floating rounded container, equal-width tab cells, a selected pill that stretches from the source cell toward the destination and then contracts, temporary dual-accent coloring during the handoff, and the same horizontal bar in landscape rather than a navigation rail.
- Observed predecessor defect: 1.4.14 still switched to a vertical navigation rail in landscape/expanded layouts and used independent per-item selected surfaces rather than one moving/stretching indicator. Landscape vertical placement and scroll-boundary-arrow clearance also differed from the supplied reference.
- Classification: `REGRESSION` + `USABILITY` + `TEST-GAP`.

## Production change
- Removed the production navigation-rail path. The same five-destination floating bottom navigation is used in portrait, landscape and expanded/freeform layouts.
- Locked the floating bar to a 352 dp maximum width and 62 dp height, centered horizontally. Landscape ignores side navigation-bar insets for horizontal centering and applies only explicit bottom placement; portrait still respects the bottom system-navigation inset.
- Replaced independent selected-cell backgrounds and per-destination wiggle motion with one elastic active indicator. The leading edge moves first, the trailing edge follows after a bounded 46 ms delay, and both settle exactly on the destination cell; source/destination tint overlap is retained only during the handoff.
- Moved main-page bottom scroll-boundary hints above the floating bar in both orientations and retained trailing content clearance so page content can scroll behind the translucent overlay without becoming unreachable.
- Vulkan collection, Profiles evaluation, technical report schema/content, Database transport, permissions, ABI policy and native/JNI code are unchanged from 1.4.14.

## Verification
- `tools/verify_release_1415.py`: PASS.
- `tools/test_release_1415_state_machine.py`: PASS, including left/right elastic transitions, exact final-cell settlement, five-cell width and scroll-arrow clearance.
- `tools/test_release_1415_negative_mutations.py`: PASS with geometry, landscape placement, elastic-delay, elastic-width, scroll-hint and Profiles-selection mutations plus an unrelated-document false-positive control.
- `tools/verify_release_1415_regression.py`: PASS against immutable 1.4.14.
- `tools/quality_gate.py`: PASS, including Vulkan 1.4.364 registry/spec, report semantics, profile requirements, video census, probe lifecycle/cancellation/timeout, hardening, concurrency/resource and package-manifest checks.
- Android `:app:compileReleaseKotlin`: NOT EXECUTED. Gradle 9.7.1 bootstrap failed before project compilation because `services.gradle.org` could not be resolved (`UnknownHostException`).
- Real-device visual replay of the supplied interaction sequence: NOT EXECUTED in the container; the release therefore does not claim device-level pixel identity until the user runs it on-device.

- Deterministic dual-ZIP byte identity: PASS.
- Clean-extract source/package byte equality and complete 1.4.15 quality-gate rerun: PASS for all 654 listed files.

# VulkanScope 1.4.14 floating primary-navigation regression audit

## Immutable predecessor
- Predecessor ZIP: `VulkanScope-1.4.13.zip`.
- Predecessor SHA-256: `d690b76c7e884701e9d9bb5995254b79cd9a22da32a100c24b3080793af39820`.
- Extracted predecessor production file census: 646 files.

## Observed failure and classification
- Real-device screenshot evidence shows only the selected `Overview` destination visible inside the compact bottom navigation container; the other four primary destinations are absent from the rendered bar.
- The compact bar is also laid out through `Scaffold.bottomBar`, so page content is resized above it rather than scrolling behind a translucent floating surface.
- The rendered floating container is larger and less tightly aligned than the supplied reference geometry.
- Classification: `REGRESSION` + `USABILITY` + `ACCESSIBILITY` + `TEST-GAP`.

## Repair
- Compact primary navigation is now a deterministic five-column Material 3 Expressive surface built from `Surface`, equal-weight selectable tab cells, icon-above-label layout and explicit selected state semantics.
- The outer floating surface is translucent, 64 dp high, horizontally inset, and no longer participates in `Scaffold.bottomBar` measurement.
- The bar is overlaid at the bottom center of the page content. Scrollable pages retain additional terminal content padding so the final item can still be brought fully above the overlay while intermediate content visibly passes beneath it.
- The wide/landscape navigation rail uses the same translucent container, selected-pill, icon/label hierarchy and tab semantics.
- The five primary destinations remain Overview, Vulkan, Surface, Display and Extensions. Profiles remains a secondary Overview destination and therefore keeps Overview selected.
- Vulkan collection, report schemas, profile evaluation, Database transport, native/JNI code, ABI policy, permissions and security boundaries are unchanged.

## Verification
- `tools/verify_release_1414.py`: PASS.
- `tools/test_release_1414_state_machine.py`: PASS.
- `tools/test_release_1414_negative_mutations.py`: PASS.
- `tools/verify_release_1414_regression.py`: PASS against the immutable 1.4.13 ZIP.
- `tools/verify_compile_regressions.py`: PASS.
- Profile requirement/report semantic/report surface integrity gates: PASS.
- Android `:app:compileReleaseKotlin`: NOT EXECUTED; the Gradle wrapper attempted to fetch Gradle 9.7.1 but `services.gradle.org` failed with `UnknownHostException` before project compilation.
- Real-device reproduction after the fix: NOT EXECUTED in this container environment and must not be represented as PASS.
- Release packaging gate: two independently created deterministic candidate ZIPs were byte-identical; clean extraction matched all 650 listed source files byte-for-byte; the complete 1.4.14 quality gate passed again from the clean extract.

# VulkanScope 1.4.13 researched primary-navigation audit

## Immutable predecessor
- Source release: VulkanScope 1.4.12 ZIP.
- Predecessor SHA-256: `39d3833869d12c9bbaab9462baa0809e7b98ac80658aa9ad892f806f2eb4b7c9`.
- Predecessor source census: 642 files.
- The 1.4.13 working tree was created by clean extraction of that immutable ZIP before production edits.

## Upstream/design research and classification
- Official Android Material 3 guidance identifies bottom `NavigationBar`/navigation-bar items for compact primary destinations and `NavigationRail` for landscape/tablet layouts.
- Current Material 3 Expressive short-navigation implementation defines top-icon items with a 64 dp bar, 24 dp icon, 56 x 32 dp full-corner active indicator and a 4 dp indicator-to-label gap. This is the closest platform component contract to the supplied reference screenshots.
- The predecessor manually reconstructed icon-pill geometry with nested `Surface`/`Box` elements instead of delegating the active indicator and item layout to the Material navigation components.
- Classification: `USABILITY` + `REGRESSION` + `TEST-GAP`.

## Production change
- Compact portrait navigation now uses Material 3 Expressive `ShortNavigationBar` and `ShortNavigationBarItem` with top-positioned icon + persistent label and the component-owned active indicator.
- The short bar is placed in one rounded floating VulkanScope surface outside the system navigation inset; internal component insets are explicitly zeroed to avoid double-padding.
- Landscape/expanded navigation now uses Material 3 `NavigationRail` and `NavigationRailItem`; the selected indicator is component-owned rather than hand-painted.
- Existing adaptive switching, mouse-wheel rail scrolling, keyboard/TV focus behavior, primary destination mapping and `Profiles -> Overview` selection semantics remain intact.
- Opening-animation header icon normalization, Vulkan Profiles reporting, native collection, report schemas, Database transport, permissions, ABI policy and Vulkan 1.4.364 registry/header lock are unchanged from 1.4.12.

## Verification
- `tools/verify_release_1413.py`: PASS.
- `tools/test_release_1413_state_machine.py`: PASS.
- `tools/test_release_1413_negative_mutations.py`: PASS, including component replacement, rail-item removal, Overview-selection regression and icon-normalization mutations plus a documentation-only false-positive control.
- `tools/verify_release_1413_regression.py`: PASS against the immutable 1.4.12 predecessor.
- Complete 1.4.13 quality gate: PASS, including registry/header locks, compile-static regression guards, Vulkan spec regressions, probe lifecycle/timeout/cancellation, report semantics/surface integrity, profile requirements, Vulkan Video census, hardening, concurrency/resource ceilings and package hygiene.
- Android `:app:compileReleaseKotlin`: NOT EXECUTED; Gradle 9.7.1 bootstrap attempted but `services.gradle.org` could not be resolved in the container (`UnknownHostException`) before project compilation.

# VulkanScope 1.4.12 compile/navigation/profile audit

## Evidence and classification
- Immutable predecessor: VulkanScope 1.4.11 working tree and delivered ZIP.
- Failing-before-fix regression oracle: the user-provided `:app:assembleRelease` reached `:app:compileReleaseKotlin` and failed at `MainActivity.kt:9164:12` with `Unresolved reference 'statusBadge'`.
- Root cause: `profileHtmlSummary` was a top-level helper but called `statusBadge`, which is intentionally local to `reportToHtml`; the helper therefore referenced a symbol outside its Kotlin scope.
- Classification: `REGRESSION` + `USABILITY` + `TEST-GAP`.

## Production changes
- Replaced the invalid top-level HTML helper dependency with `profileHtmlDetails`, which performs only globally available escaping/detail formatting. `reportToHtml` now composes its own local `statusBadge(evaluation.status)` with that helper inside the valid lexical scope.
- Retains the requested reference-screenshot-style primary navigation treatment: one rounded floating navigation surface, neutral unselected items, icon-above-label geometry, and selected-state emphasis confined to the icon pill rather than a full selected tile. The landscape/expanded navigation rail uses the vertical equivalent of the same visual state model.
- `Page.Profiles` is mapped to the `Overview` primary destination for shared navigation selection, so opening Profiles from Overview keeps Overview selected.
- The supplied Opening animation glyph remains the section-header icon and is normalized to the VulkanScope accent tint at 20 dp inside the existing header container.
- Vulkan Profiles evaluation remains fail-closed: verified missing requirements may FAIL, unavailable/incomplete profile evidence remains UNKNOWN, and coverage-limited mappings cannot become API-only PASS.
- Detailed profile evaluation now remains visible in the UI and is preserved in technical JSON, TXT and HTML. VulkanScope Database submission carries the same detailed technicalReport plus reportText and therefore retains the profile evidence without a separate reduced Database-only representation.
- Restored the established safe probe teardown/timeout settle windows and checkpoint polling cadence required by the existing lifecycle/hardening contracts; prioritized query ordering from the earlier responsiveness work remains retained.

## Upstream verification
- Khronos documentation describes Vulkan Profiles as capability baselines composed from features, extensions, limits, formats and related requirements rather than as a Vulkan core-version alias.
- The Vulkan Profiles library provides explicit profile support queries; VulkanScope continues to use its audited runtime-evidence evaluator and does not claim PASS where its mapped definition coverage is incomplete.
- Current Vulkan Roadmap documentation identifies Roadmap 2026 as requiring the Roadmap 2024 milestone; the retained evaluator contract remains conservative and does not promote coverage-limited mappings to PASS.

## Verification
- `tools/verify_release_1412.py`: PASS.
- `tools/test_release_1412_state_machine.py`: PASS.
- `tools/test_release_1412_negative_mutations.py`: PASS, including a mutation that reintroduces the out-of-scope `statusBadge` call; documentation-only false-positive control PASS.
- `tools/verify_release_1412_regression.py`: PASS against immutable 1.4.11; predecessor fails the new contract for the expected compile-scope/release-identity reason.
- `tools/quality_gate.py`: PASS for the 1.4.12 route, including Vulkan 1.4.364 registry/header lock, specification regressions, probe lifecycle/timeout/cancellation, report semantics/surface integrity, Vulkan Profiles evaluator/source/negative gates, Vulkan Video census, full hardening, concurrency/resource contracts and package hygiene.
- Android `:app:compileReleaseKotlin` / `:app:assembleRelease`: NOT EXECUTED in this container because Gradle 9.7.1 bootstrap cannot resolve `services.gradle.org` (`UnknownHostException`). The original user build log remains the failing-before-fix compiler oracle.
- Real-device screenshot/runtime comparison: NOT EXECUTED in this container; source/state contracts verify the requested navigation geometry/state model but do not substitute for a device screenshot.

# VulkanScope 1.4.11 navigation/profile/overview audit

## Evidence and classification
- Immutable predecessor: VulkanScope 1.4.10 working tree produced from the previously supplied 1.4.10 release ZIP.
- Observed predecessor failures:
  - `Opening animation` still did not visually match the requested reference-screenshot-style settings-header treatment closely enough.
  - Overview → Profiles did not keep `Overview` visually selected in shared navigation.
  - Vulkan Profiles export surfaces were too summary-heavy and did not expose the full failing/unknown detail set consistently across JSON/TXT/HTML-derived complete-report outputs.
  - Shared navigation visuals required a closer reference-screenshot-style floating-tab treatment.
- Classification: `REGRESSION` + `USABILITY` + `TEST-GAP`.

## Production change
- Restyled the compact bottom navigation bar and navigation rail toward the requested requested floating-tab presentation while preserving the existing five-item information architecture.
- Mapped `Page.Profiles` back to `Overview` in shared navigation-selection logic so opening Profiles from Overview keeps Overview visually selected.
- Refined the `Opening animation` section-header icon treatment: the supplied animation glyph is now rendered as the actual header icon with VulkanScope accent tint, reduced size and cleaner centering for visual consistency.
- Expanded Vulkan Profiles reporting helpers so derived profile evaluation exports now retain summary plus detailed failing/unknown requirement lists in technical JSON and richer detail output in TXT/HTML.
- Added Profiles-page summary metrics and explicit detail guidance. Native Vulkan collection, Database transport endpoint restrictions, manifest permissions, driver switching semantics, report collection completeness rules and ABI policy remain unchanged.

## Verification
- Source-level targeted review of navigation selection, profile-export serialization points and section-header icon routing: PASS.
- Targeted export-surface audit for `technicalReport` JSON, TXT report and HTML report profile-evaluation detail retention: PASS.
- Android `:app:compileReleaseKotlin`: NOT EXECUTED because the Gradle wrapper could not resolve `services.gradle.org` and failed before Gradle bootstrap with `UnknownHostException`.
- Runtime/device behavioral evidence for the requested UI refinements is NOT EXECUTED in this container-only packaging environment.

# VulkanScope 1.4.10 opening-animation icon-placement audit

## Evidence and classification
- Immutable predecessor: VulkanScope 1.4.9 working tree produced from the previously supplied 1.4.9 release.
- Observed predecessor failure: the `Opening animation` section header still resolved through the generic `ic_info` fallback while the supplied animation glyph was rendered as a second icon inside the preference row.
- Classification: `REGRESSION` + `USABILITY`.
- Required behavior: the supplied animation glyph replaces the generic header information glyph; the preference row contains no duplicate leading animation glyph.

## Production change
- `capabilitySectionIcon("Opening animation")` now resolves to `ic_opening_animation_toggle`.
- `SectionHeaderIcon` renders that raster glyph as an `Image` in the existing Material 3 Expressive section-header container.
- The duplicate row-level icon is removed; label/status/switch behavior is unchanged.
- The supplied icon asset is converted to an alpha-capable neutral-gray PNG so its dark screenshot background is not rendered as a rectangle inside the header container.
- Vulkan collection, native/JNI code, report schemas, network behavior, permissions, driver management, startup gating and navigation behavior are unchanged.

## Verification
- `tools/verify_release_1410.py`: PASS.
- `tools/test_release_1410_negative_mutations.py`: PASS for header fallback, wrong header painter, duplicate row icon and missing asset mutations; documentation-only false-positive control PASS.
- `tools/verify_release_1410_regression.py`: PASS; predecessor fails the new icon-placement contract for the expected reason.
- `tools/verify_compile_regressions.py`: PASS.
- Android `:app:compileReleaseKotlin`: NOT EXECUTED because the Gradle wrapper could not resolve `services.gradle.org` and failed before Gradle bootstrap with `UnknownHostException`.
- The legacy 0.80.6 source-pattern accessibility verifier contains superseded literal checks for earlier navigation/layout implementations; its large-text state-machine test passes and its source-pattern failures are not used as evidence for this narrowly scoped icon-placement release.

# VulkanScope 1.3.7 Material3 Expressive compile-fix audit

## Evidence and scope
- Immutable predecessor: VulkanScope 1.3.6, 559 files, ZIP SHA-256 `dba64e83c963240b02b31cc39ce007a3bc1d52b1958aa8808eaf4c13c80dd547`.
- Failing-before-fix evidence: user-provided `:app:assembleRelease` reaches `:app:compileReleaseKotlin` and fails at MainActivity.kt lines 9461, 9497, 9808 and 9855 because `LoadingIndicator` requires Material3 Expressive opt-in.
- Production delta: exactly two function-scope `@OptIn(ExperimentalMaterial3ExpressiveApi::class)` annotations plus release identity.
- The shown libadrenotools diagnostics are warnings only and are intentionally not changed by this release.

## Required final evidence
- `tools/verify_release_1307.py`: PASS.
- `tools/test_release_1307_state_machine.py`: PASS.
- `tools/test_release_1307_negative_mutations.py`: PASS, including missing/wrong opt-in mutations and documentation-only false-positive control.
- `tools/verify_release_1307_regression.py`: PASS against immutable 1.3.6.
- Immutable 1.3.6 failing oracle: expected FAIL specifically because `TurnipFileManagerDialog` lacks the narrow Expressive opt-in.
- Strict source package manifest/hygiene: PASS with 564 files.
- Container `:app:assembleRelease`: NOT EXECUTED because Gradle 9.7.1 bootstrap could not resolve `services.gradle.org` (`UnknownHostException`); this is not reported as PASS.
- Final release packaging: deterministic dual-ZIP byte identity, clean-extract source/package byte equality, strict package census and the complete targeted 1.3.7 verifier/state/negative-mutation/regression rerun are PASS.

# VulkanScope 1.3.6 in-app import/export storage completion audit

## Release identity
- Version: 1.3.6.
- versionCode: 1306.
- Immutable predecessor: VulkanScope 1.3.5.
- Predecessor ZIP SHA-256: `00d8bc56012e910f4440f890561caf68e5cc934ac2876d35ccfc29e7ad342794`.
- Predecessor package census: 554 files.
- Successor source census before packaging: 559 files.
- Scope: non-Turnip Analysis/report/TXT/HTML/JSON shared-storage import/export only.

## Implemented storage completion
- SAF remains absent: no OpenDocument/CreateDocument, persistable document URI or Downloads fallback was reintroduced.
- Shared-storage permission is checked only after an explicit import/export action through the existing Android all-files special-access model.
- Denied special access retains the established animated X / Permission denied feedback for 3 seconds.
- Added a SAF-free in-app shared-storage browser confined to canonical primary shared storage, with symlink/path-escape rejection, a 4096-entry listing ceiling, extension allow-lists and background filesystem enumeration.
- Export destinations reject unsafe/control/path names, require explicit overwrite confirmation for existing files and use same-directory temporary files with flush/fsync plus atomic replace where supported.
- Analysis snapshot import/export retains schema `VulkanScopeAnalysisSnapshot1`, semantic validation and the 8 MiB ceiling.
- Minimum-profile import/export retains schema `VulkanScopeMinimumProfile1`, 256 KiB input/output ceiling, 128-character name bound, 1..64 rule bound and 512-character per-rule bound.
- Raw `technicalReport` JSON export transports the existing schema-v3 serialization unchanged with an 8 MiB local export ceiling.
- TXT/HTML complete-report export creates a bounded private cache snapshot off the UI thread, persists pending snapshot metadata across configuration recreation, copies it to the selected shared-storage destination and cleans pending/stale snapshots.
- Turnip file-manager/import, updater, Database transport, Vulkan/native/JNI collection, registry/generated data, manifest permissions, resources, dependencies, ABIs and report schemas remain predecessor behavior.

## Targeted 1.3.6 evidence
- `tools/verify_release_1306.py`: PASS.
- `tools/test_release_1306_state_machine.py`: PASS.
- `tools/test_release_1306_negative_mutations.py`: PASS, including SAF comeback, canonical confinement, symlink rejection, directory bound, permission-denial duration, Analysis schema, profile ceiling, atomic move, saveable snapshot state and completeness-gate mutations plus an unrelated wording false-positive control.
- `tools/verify_release_1306_regression.py`: PASS against immutable 1.3.5.
- Current storage-only `tools/quality_gate.py`: PASS.
- Compile-static regression verifier: PASS.
- Strict source package manifest/hygiene: PASS with 559 files.
- Historical gates that require SAF or the intentionally unavailable 1.3.3/1.3.4 export placeholders were not run, as required by the 1.3.6 release contract.

## Android build evidence boundary
- Gradle wrapper startup was attempted for Kotlin compile, lint and unit tests.
- Gradle 9.7.1 could not be downloaded because `services.gradle.org` was unreachable (`UnknownHostException`).
- Android compile/lint/unit are therefore `NOT EXECUTED`, never PASS.

## Final package requirements
- Two independently created deterministic ZIPs must be byte-identical.
- Final ZIP must be clean-extracted.
- Full source-to-extract byte equality and strict package hygiene must pass.
- The targeted 1.3.6 verifier/state/negative/regression suite must pass again on the clean extract.

## Release 1.3.8 targeted UI refinement audit

Scope is restricted to the shared single-filter selector and Turnip file-manager presentation requested from real-device screenshots. Vulkan/native/JNI collection, report schemas, Database, updater, storage validation/security, manifest, ABI/API pins and resources are unchanged from immutable 1.3.7.

Findings/fixes:
- Shared single-filter popup now uses bounded fade/scale entry/exit motion and animated chevron/enabled state.
- Filter search now uses the shared Expressive search field, searches case-insensitively across the full filter set, resets to page 1, keeps a lazy 50-row page and exposes vertical scroll-boundary hints.
- Filter labels wrap instead of being hard-clipped; popup width is bounded by available selector width.
- Direct page entry accepts only decimal pages in 1..pageCount; invalid characters/out-of-range values do not replace the field/current page. With one result page, page entry and previous/next remain visible but disabled.
- Turnip file-manager custom dark surfaces now set explicit VulkanScope content colors; real-device dark-on-dark title/folder/summary regressions are removed.
- List/Compact is now an expressive segmented control with selected-state motion/check feedback. Validated package green and folder yellow remain semantic accents; selected package surface uses VulkanScope rose/red.
- Turnip search uses the shared Expressive search field. Footer/header/details groups use explicit dark tonal containers and responsive layout; details keep vertical scroll hints.

Evidence:
- tools/verify_release_1308.py: PASS
- tools/test_release_1308_state_machine.py: PASS
- tools/test_release_1308_negative_mutations.py: PASS
- tools/verify_release_1308_regression.py against immutable 1.3.7: PASS
- immutable 1.3.7 predecessor oracle with --skip-version: expected FAIL
- tools/verify_compile_regressions.py: PASS
- Android :app:assembleRelease: NOT EXECUTED (Gradle wrapper bootstrap could not resolve services.gradle.org; UnknownHostException before project compilation)

Final deterministic packaging/clean-extract evidence is executed as the release packaging step and reported with the delivered artifact.

# VulkanScope 1.3.9 targeted filter/storage UI audit

## Scope
- Immutable predecessor: VulkanScope 1.3.8, 569 files, ZIP SHA-256 `53ad9b4fd9cc6087738293553e5bbab3ade37a11e532b7e8058f5a284b3f8641`.
- Runtime production delta is limited to `MainActivity.kt`, four local vector view-mode icons and release identity.
- No native/JNI Vulkan, report schema/content, Database, updater, storage confinement/atomicity, Turnip validation, manifest permission, API/ABI or dependency change is authorized.

## User-reported findings and fixes
- Filter popup transient state is independent of the live label collection; live collection updates no longer recreate/close the popup.
- Popup position is selector-relative through `PopupPositionProvider`; horizontal and vertical placement are bounded to the window.
- Popup-window Back dismissal is disabled. Explicit Back handling clears IME focus first and closes the popup only when the IME is not active.
- Filter result and selector interaction indications are clipped to their clickable rounded shapes; existing strict numeric page entry and scroll hints remain.
- TXT and HTML complete-report actions are both full-width and always present; the weighted side-by-side animated layout is removed.
- Shared-storage import/export browser uses explicit dark Material 3 Expressive surfaces, clipped folder/file interactions, shared search and scroll hints.
- Export type is fixed by the action: the user edits only the base name, the extension is shown separately, appended by VulkanScope and revalidated at destination creation.
- Turnip file manager provides icon-only List / Compact / Grid / Details modes using packaged vector resources. Package selection cards are themselves clipped selectable actions.
- Runtime UI copy describes only the current storage workflow and does not discuss removed legacy picker architecture.

## Targeted evidence before final packaging
- `tools/verify_release_1309.py`: PASS.
- `tools/test_release_1309_state_machine.py`: PASS.
- `tools/test_release_1309_negative_mutations.py`: PASS.
- `tools/verify_release_1309_regression.py`: PASS against immutable 1.3.8.
- Immutable 1.3.8 predecessor oracle with release identity/rules checks skipped: expected FAIL on the live-filter popup state contract.
- Python syntax compilation for all four 1.3.9 gate scripts: PASS.
- Android `:app:assembleRelease`: `NOT EXECUTED`; Gradle 9.7.1 wrapper bootstrap cannot resolve `services.gradle.org` (`UnknownHostException`) before project compilation. This is not reported as PASS.

## Mandatory final packaging evidence
- Regenerate strict `files.txt` census after adding 1.3.9 rule/golden/gate artifacts.
- Source package hygiene must PASS.
- Two independently generated deterministic ZIPs must be byte-identical.
- Clean extraction must be byte-identical to source and pass package hygiene.
- The complete targeted 1.3.9 verifier/state/negative-mutation/regression suite must PASS again on the clean extraction.

## 1.3.9 source readiness before packaging
- Strict `files.txt` census regenerated after all 1.3.9 rule/golden/gate/vector additions: 579 files.
- `tools/verify_package_reproducibility.py --source .`: PASS.
- Complete targeted 1.3.9 verifier/state/negative-mutation/regression suite rerun after the final production changes: PASS.
- No additional production changes are permitted after this checkpoint; only deterministic packaging and clean-extract verification remain.

# VulkanScope 1.3.10 targeted filter/Turnip/About audit

## Scope and fixes
- Immutable predecessor: VulkanScope 1.3.9, 579 files, ZIP SHA-256 `874d91f8ab370a6390890ccbf8950b43da940b027c14586f17156e6f6dbdc389`.
- Filter selector body is informational; only the contained red chevron action is clickable. Popup motion is bounded and page/search result changes use bounded AnimatedContent motion.
- Filter results occupy a weighted middle region while pagination remains a fixed footer; strict numeric 1..pageCount validation and IME-first Back behavior remain intact.
- Turnip folder cards navigate only from contained red right-arrow actions. Package selection behavior is otherwise unchanged.
- The existing packaged Mesa logo asset remains byte-identical and is presented through one shared tonal/outlined badge in package list/grid/info surfaces.
- Managed Turnip source metadata name+location is used for exact duplicate-source presentation. Exact matches remain visible but are muted and blocked by both UI enablement and selection/import guards.
- About begins with a yellow non-affiliation disclosure and retains the previous descriptive copy below it.

## Targeted evidence before packaging
- `tools/verify_release_1310.py`: PASS.
- `tools/test_release_1310_state_machine.py`: PASS.
- `tools/test_release_1310_negative_mutations.py`: PASS.
- `tools/verify_release_1310_regression.py`: PASS against immutable 1.3.9.
- Immutable 1.3.9 predecessor oracle with version checks skipped: expected FAIL on the arrow-only filter contract.
- `tools/verify_compile_regressions.py`: PASS.
- Android `:app:assembleRelease`: NOT EXECUTED; Gradle 9.7.1 bootstrap cannot resolve `services.gradle.org` (`UnknownHostException`) before project compilation. This is not reported as PASS.
- Final deterministic dual-ZIP, clean-extract byte equality, package census and clean-extract targeted gates are required before delivery.

# VulkanScope 1.3.11 targeted compile/updater UI audit

## Failing-before-fix evidence
- User-executed `:app:assembleRelease` reached `:app:compileReleaseKotlin` after native builds completed.
- Kotlin failed at `MainActivity.kt` with `Unresolved reference 'fillMaxHeight'`.
- The source called `Modifier.fillMaxHeight()` but imported `fillMaxSize`/`fillMaxWidth` only.
- libadrenotools emitted C/C++ warnings but those tasks completed; those warning-only third-party diagnostics are not treated as the build failure.

## Fix and updater UI audit
- Added the missing `androidx.compose.foundation.layout.fillMaxHeight` import only; filter behavior is unchanged.
- Audited update preferences, status banner, consent dialog, confirmation dialog, transfer dialog and cancel-confirmation dialog. Custom updater dark surfaces now use explicit VulkanScope content colors and neutral copy uses VulkanScope text palette tokens.
- Retained semantic red error, blue offline and green monospace log colors where they encode state rather than neutral typography.
- Retained update security/state invariants: fixed official release provenance, package/signature/version checks, explicit Install, Pause/Resume/Cancel flow and unconditional temporary `.part` cleanup.

## Evidence boundary
- Only 1.3.11 targeted build/updater UI gates plus retained 1.3.10 UI contract are required before packaging.
- Android build execution from this environment remains a separate evidence class and is PASS only if Gradle actually reaches project compilation.

## Targeted 1.3.11 evidence
- `tools/verify_release_1311.py`: PASS.
- `tools/test_release_1311_state_machine.py`: PASS.
- `tools/test_release_1311_negative_mutations.py`: PASS.
- `tools/verify_release_1311_regression.py`: PASS against immutable 1.3.10.
- Immutable 1.3.10 predecessor oracle with version checks skipped: expected FAIL on missing `fillMaxHeight` import.
- Retained `tools/verify_release_1310.py --skip-version`: PASS.
- `tools/verify_compile_regressions.py`: PASS.
- Android `:app:assembleRelease` in this environment: `NOT EXECUTED`; Gradle 9.7.1 wrapper bootstrap failed before project compilation because `services.gradle.org` could not be resolved (`UnknownHostException`). This is not reported as PASS.

## Packaging boundary
- Regenerate strict `files.txt` after 1.3.11 rule/golden/gate additions.
- Require source package hygiene, deterministic dual-ZIP byte equality, clean-extract source/package byte equality and clean-extract rerun of the targeted 1.3.11 gates.

# VulkanScope 1.3.12 targeted filter/storage/driver-manager audit

## Scope
- Immutable predecessor: VulkanScope 1.3.11, 589 files, ZIP SHA-256 `10fb87b0d1e21306964a4e158bfcc4d83ffb07bb0a123a103a04475fd6358add`.
- Production runtime delta is restricted to `MainActivity.kt`; `app/build.gradle.kts` changes release identity only.
- No Vulkan/native/JNI, report schema/content, Database, updater behavior, Turnip archive validation/private schema, manifest, dependency/API/ABI or packaged-resource change is authorized.

## User-reported findings and fixes
- Filters with fewer than five choices no longer reserve a search field; one-page filter results no longer reserve page arrows/page-entry/footer space.
- Popup mounting is staged before visibility so fade/scale/vertical motion runs on first appearance. A contained red X action is always available in the popup header.
- Re-opening clears stale query state, navigates to the page containing the selected filter and initializes the lazy list at that selected row. Live label updates do not key/recreate transient popup state.
- Page-number entry accepts a temporary blank while editing, accepts only valid decimal pages in `1..pageCount`, and restores the active page when an empty field loses focus.
- The common shared-storage browser used by Analysis/profile/technicalReport JSON and TXT/HTML import/export now uses informational rows with only the contained VulkanScope-red right arrow as the folder/file action. Existing canonical-path, bounded-scan, fixed-extension and atomic-write rules are unchanged.
- System-driver and managed-Turnip cards were visually regrouped with Material 3 Expressive badges, state pills and tonal evidence containers. Existing System evidence, Turnip state, activation, details and removal semantics are unchanged.

## Targeted evidence
- `tools/verify_release_1312.py`: PASS.
- `tools/test_release_1312_state_machine.py`: PASS.
- `tools/test_release_1312_negative_mutations.py`: PASS.
- `tools/verify_release_1312_regression.py`: PASS against immutable 1.3.11.
- Immutable 1.3.11 predecessor oracle with version checks skipped: expected FAIL on the new compact-search contract.
- Retained `tools/verify_release_1311.py --skip-version`: PASS.
- `tools/verify_compile_regressions.py`: PASS.
- Android `:app:assembleRelease` in this environment: `NOT EXECUTED`; after restoring executable permission on the wrapper, Gradle 9.7.1 bootstrap failed before project compilation because `services.gradle.org` could not be resolved (`UnknownHostException`). This is not reported as PASS.

## Packaging boundary
- Regenerate strict `files.txt` after the 1.3.12 rule/golden/gate additions.
- Require source package hygiene, deterministic dual-ZIP byte equality, clean-extract source/package byte equality and clean-extract rerun of the complete targeted 1.3.12 suite before delivery.

# VulkanScope 1.3.13 targeted filter/driver-branding audit

## Scope and failing-before-fix evidence
- Immutable predecessor: VulkanScope 1.3.12, 594 files, ZIP SHA-256 `e68eef88cd73f4b3b095fd7aea768dfc5a75eab3f8af82e99b33a169c48d578d`.
- User runtime evidence reported the filter menu could occupy the wrong vertical region and requested that normal page scrolling remain available with X-only dismissal.
- Source audit confirmed 1.3.12 used a `PopupPositionProvider` that could choose an above-anchor placement and `dismissOnClickOutside=true`; result selection and the selector arrow could also close the menu.
- Source audit confirmed managed Turnip cards labeled package `driverVersion` metadata as Driver version, while package metadata uses that field for the Vulkan-version string.
- Source audit confirmed the System driver badge used the Android glyph despite retained vendor evidence and an existing packaged Overview GPU-vendor-logo catalogue.
- Source audit confirmed the Mesa asset was shown in original multicolor form in shared badges despite the requested VulkanScope-red branding.

## Patch classification
- `USABILITY`: in-layout below-selector filter expansion with normal parent-page scrolling and X-only dismissal.
- `BUG`: Turnip package metadata label correction without changing stored metadata or the existing slot/state version line.
- `USABILITY`: System GPU-vendor badge and unified VulkanScope-red Mesa presentation.
- Production runtime changes are limited to `MainActivity.kt`; release identity changes only in `app/build.gradle.kts`.

## Targeted 1.3.13 evidence
- `tools/verify_release_1313.py`: PASS.
- `tools/test_release_1313_state_machine.py`: PASS.
- `tools/test_release_1313_negative_mutations.py`: PASS after narrowing mutation anchors so the tests alter the actual 1.3.13 filter block.
- `tools/verify_release_1313_regression.py`: PASS against immutable 1.3.12.
- Immutable 1.3.12 predecessor oracle with release identity checks skipped: expected FAIL on the in-layout filter contract.
- Retained `tools/verify_release_1311.py --skip-version`: PASS.
- `tools/verify_compile_regressions.py`: PASS.
- Android `:app:assembleRelease`: `NOT EXECUTED`; Gradle 9.7.1 wrapper bootstrap cannot resolve `services.gradle.org` (`UnknownHostException`) before project compilation. This is not reported as PASS.

## Packaging boundary
- Regenerate strict `files.txt` after the 1.3.13 rule/golden/gate additions.
- Require source package hygiene, deterministic dual-ZIP byte equality, clean-extract source/package byte equality and clean-extract rerun of the complete targeted 1.3.13 suite before delivery.

## Final packaging evidence
- Strict source package census: 599 files, PASS.
- Two independent deterministic ZIP generations were byte-identical.
- Clean extraction was byte-identical to the source tree under `files.txt`.
- Clean extraction reran the complete targeted 1.3.13 verifier/state/negative-mutation/regression suite: PASS.
- Clean extraction reran the retained 1.3.11 updater/build verifier and compile-regression guard: PASS.
- Android build remains `NOT EXECUTED` in this environment because Gradle wrapper bootstrap cannot resolve `services.gradle.org`; no build PASS is claimed.

# VulkanScope 1.4.0 targeted filter/icon audit

## Scope and failing-before-fix evidence
- Immutable predecessor: VulkanScope 1.3.13, 599 files, ZIP SHA-256 `67c59fe966a45f60ba0382b56522ba699d55325d8e512f5e3a6ed66184da611f`.
- Source audit found Format explorer still routed its multi-selection usage filters through the legacy horizontal `ExpressiveFilterCarousel`/`FilterChip` implementation while the rest of the active single-filter surfaces used the new in-layout selector.
- The 1.3.13 shared single-filter selector used a dedicated red X to close the expanded menu and disabled the chevron while open, contrary to the requested chevron-toggle behavior.
- Overview Quick access still mapped its section badge to the generic home icon rather than the supplied 3x3-dot quick-access glyph.

## Patch classification
- `USABILITY`: unified the remaining active legacy filter surface with the current Material 3 Expressive selector/container language.
- `USABILITY`: removed the dedicated X and made the contained chevron the open/close toggle; the search control regains full width.
- `USABILITY`: added a dedicated nine-dot Quick access section icon without changing navigation semantics.
- Production runtime changes are restricted to `MainActivity.kt` plus the new `ic_quick_access_grid.xml`; `app/build.gradle.kts` changes release identity only.

## Targeted evidence boundary
- Required: 1.4.0 verifier, filter state-machine, negative mutations, 1.3.13→1.4.0 regression boundary, immutable-predecessor failing oracle, compile-regression check, strict package hygiene, relevant Android release-build attempt, deterministic dual ZIP, clean extraction, full source/package byte equality and clean-extract targeted rerun.
- Historical unrelated gate chains are intentionally not rerun.

## Targeted 1.4.0 evidence
- `tools/verify_release_1400.py`: PASS.
- `tools/test_release_1400_state_machine.py`: PASS.
- `tools/test_release_1400_negative_mutations.py`: PASS.
- `tools/verify_release_1400_regression.py`: PASS against immutable 1.3.13.
- Immutable 1.3.13 predecessor oracle with version checks skipped: expected FAIL on the old non-toggling open-chevron contract.
- `tools/verify_compile_regressions.py`: PASS.
- Quick access vector XML parse: PASS.
- Active production filter audit: no `FilterChip` or legacy `ExpressiveFilterCarousel` remains; Format explorer uses the unified multi-filter selector.
- Android `:app:assembleRelease`: `NOT EXECUTED`; Gradle 9.7.1 wrapper bootstrap failed before project compilation because `services.gradle.org` could not be resolved (`UnknownHostException`). This is not reported as PASS.

## Packaging boundary
- Regenerate strict `files.txt` after the 1.4.0 rule/golden/gate/resource additions.
- Require source package hygiene, deterministic dual-ZIP byte equality, clean-extract source/package byte equality and clean-extract rerun of the complete targeted 1.4.0 suite before delivery.

## Final packaging evidence
- Strict source/package census: 605 files, PASS.
- Two independent deterministic ZIP generations were byte-identical.
- Clean extraction was byte-identical to the source tree for all 605 files.
- Clean extraction reran `verify_release_1400.py`, `test_release_1400_state_machine.py`, `test_release_1400_negative_mutations.py`, `verify_release_1400_regression.py` and `verify_compile_regressions.py`: PASS.
- Quick-access vector XML parse: PASS.
- Android `:app:assembleRelease` remains `NOT EXECUTED` in this environment because Gradle bootstrap could not resolve `services.gradle.org`; no Android build PASS is claimed.

# VulkanScope 1.4.1 targeted filter-boundary/Turnip-slot audit

## Scope and root cause
- Immutable predecessor for this task: latest supplied VulkanScope 1.4.0 ZIP, 605 files, ZIP SHA-256 `f0a3c32f0bbc0f89b0fb0f5aca47e33a3c55e3389a2e96b02943c02243305c27`.
- Both active in-layout filter result surfaces use `LazyColumn`. Once a list reached its first/last item, the child had no more vertical distance to consume and the remaining nested-scroll delta/velocity propagated to the owning scrollable page.
- Managed Turnip metadata already retained validated `libraryName` and parsed `description`, and the Details dialog exposed them, but the slot cards did not.

## Patch classification
- `BUG/USABILITY`: contain only post-scroll residual Y delta and post-fling residual Y velocity at each filter result-list boundary. No pre-consumption is introduced, so normal list scrolling happens first and ordinary page scrolling outside the list remains available.
- `USABILITY`: show existing Turnip `libraryName` and nonblank `description` values directly on both wide and compact managed-slot cards with explicit fallbacks.
- Production runtime changes are restricted to `MainActivity.kt`; `app/build.gradle.kts` changes release identity only. Native/JNI, Vulkan/report/Database, Turnip archive-validation/import/activation/removal, storage-security, manifest, dependency and resource bytes are unchanged.

## Targeted 1.4.1 evidence
- `tools/verify_release_1401.py`: PASS.
- `tools/test_release_1401_state_machine.py`: PASS.
- `tools/test_release_1401_negative_mutations.py`: PASS.
- `tools/verify_release_1401_regression.py`: PASS against the immutable latest 1.4.0 package.
- Immutable 1.4.0 predecessor oracle with version checks skipped: expected FAIL because it lacks filter-boundary containment.
- `tools/verify_compile_regressions.py`: PASS.
- AndroidX nested-scroll API cross-check: `LazyColumn` participates in nested scrolling and a parent `NestedScrollConnection.onPostScroll` may consume remaining `available` delta; `onPostFling` provides remaining velocity for containment.
- Android `:app:assembleRelease`: `NOT EXECUTED`; the wrapper attempted to download Gradle 9.7.1 but this environment cannot resolve `services.gradle.org` (`UnknownHostException`) before project compilation. No Android build PASS is claimed.

## Final packaging evidence
- Strict source/package census: 611 files, PASS.
- Two independent deterministic ZIP generations were byte-identical.
- Clean extraction was byte-identical to the source tree under `files.txt`.
- Clean extraction reran the complete targeted 1.4.1 verifier/state/negative-mutation/regression suite and compile-regression guard: PASS.

# VulkanScope 1.4.5 Googlebook / Vulkan 1.4.364 full audit

## Scope
- Immutable predecessor: VulkanScope 1.4.4, 618 files, ZIP SHA-256 `749298194a15f825976bbc67603869e0a8ab6f6d89bfa37a929a7700808a2414`.
- Locked the collector to the supplied canonical Vulkan registry at Vulkan 1.4.364 / `VK_HEADER_VERSION 364` and added the new `VK_INTEL_device_info` physical-device returned-properties coverage.
- Added non-heuristic Googlebook/desktop Android evidence using only documented public system features; Googlebook identity/version remains unavailable when Android does not expose authoritative evidence.
- Extended adaptive navigation so landscape, TV and current app windows at least 600 dp wide use the Material 3 rail; compact portrait keeps the Material 3 Expressive short navigation bar.
- Re-audited report parity, query semantics, security/privacy, bounded resource use, crash-recovery contracts, usability and accessibility under `rules/PROJECT_RULES.md`.

## Targeted evidence
- `tools/verify_release_1405.py`: PASS.
- `tools/test_release_1405_state_machine.py`: PASS.
- `tools/test_release_1405_negative_mutations.py`: PASS, 10 required-failure mutations plus 1 false-positive control.
- `tools/verify_release_1405_regression.py`: PASS against immutable 1.4.4; 11 allowlisted `app/src/main` changes and 618 predecessor files.
- Bundled Vulkan `vk.xml` SHA-256 matches the supplied input: `4cfe3c137f3a95c15275b1cfda0aee407dbf131de2005a3552aa41c49f895c89`.
- Bundled Vulkan Video `video.xml` SHA-256 matches the supplied input: `d018b914014c06605e367a3b929670511e6f6de2f225c405a8b5e2d912408b76`.

## Security / optimization / accessibility review
- No Googlebook heuristic fingerprinting, new endpoint, sensitive Android permission or exported component was introduced.
- Existing HTTPS-only/backup-disabled manifest posture, fixed Database endpoint validation, disabled Database redirects, native stack protector + RELRO/NOW, release minification/resource shrinking and bounded archive/probe/report contracts remain enforced by source gates.
- Current rail keeps focus grouping, vertical scrolling, minimum target sizing and large-text adaptation; report export controls remain full-width and independently reachable.
- The desktop navigation decision is tied to current app-window width rather than physical display identity, improving Googlebook/freeform resizing behavior without additional collection work.

## Build/runtime evidence boundary
- Android `:app:assembleRelease`: `NOT EXECUTED` to compilation. Gradle wrapper bootstrap failed while resolving `services.gradle.org` with `UnknownHostException`; no compile result is claimed.
- Android lint/unit, emulator/device, real Googlebook/ChromeOS, TalkBack, keyboard/TV focus, rotation, Vulkan Validation Layers, sanitizers and profiler: `NOT EXECUTED` in this packaging environment.
- Strict canonical Vulkan header byte-level verification: `NOT EXECUTED` without a local canonical header path; SHA-256-locked registry-level verification remains executed.

# VulkanScope 1.4.6 launch sequencing and compact-navigation audit

## Scope and immutable predecessor
- Immutable predecessor: VulkanScope 1.4.5, 624 files, ZIP SHA-256 `b78132731a6c2b0e4ef0f616979d25b235368838342ee3c103610c210ada5a8e`.
- Production runtime changes are restricted to `app/src/main/java/com/efishell/vulkanscope/MainActivity.kt`; `app/build.gradle.kts` changes release identity only.
- Manifest, native/JNI collector and probe service, Vulkan registry/generated data, report/Database schemas, dependencies, ABI set and packaged artwork remain predecessor-equivalent.

## Failing-before-fix evidence
- The immutable 1.4.5 tree fails the 1.4.6 targeted verifier with the expected compact-navigation, opening-animation preference, startup-gate and custom-opening-sequence signatures.
- The requested compact phone navigation shape was not present in 1.4.5 because the compact path still used `ShortNavigationBar` rather than the requested top-icon/label Material 3 navigation bar.
- 1.4.5 had only the Android SplashScreen transition; it did not contain the requested independently visible post-splash VulkanScope logo sequence or a preference to disable it.

## Implemented fixes
- Compact primary navigation now uses Material 3 `NavigationBar` and `NavigationBarItem` for the five existing destinations, with persistent icon-above-label presentation, equal item distribution, an 80 dp minimum bar and the existing VulkanScope selected-pill palette. Wide/landscape/TV navigation remains the existing scrollable Material 3 `NavigationRail`.
- The post-splash opening sequence uses the already packaged `vulkanscope_logo_horizontal` image, bounded fade/scale/glow motion and a full-screen input-blocking surface. Its animation coroutine starts only after the AndroidX platform splash exit animation reports completion, and the normal VulkanScope application tree is not composed until this gate opens.
- Startup state explicitly gates base report collection, lazy Vulkan query groups, Surface-triggered collection, Android display inspection, default-network observation and the automatic startup update check. Gate completion is idempotent and deferred work begins only while the Activity is started.
- Startup-gate completion is saved across Activity recreation. Recreation after a completed opening sequence does not replay it; recreation that interrupts the sequence remains gated until a replacement visible sequence completes.
- Driver & Update Preferences contains a separate `Opening animation` capability card directly below Update preferences. The same Expressive direct-switch interaction is used; no confirmation state or dialog exists. `opening_animation_enabled` defaults to true and is persisted immediately.

## Targeted correctness, security, performance and accessibility evidence
- `tools/verify_release_1406.py`: PASS.
- `tools/test_release_1406_state_machine.py`: PASS, 7 startup/navigation cases.
- `tools/test_release_1406_negative_mutations.py`: PASS, 12 rejected regressions plus one accepted unrelated documentation control.
- `tools/verify_release_1406_regression.py`: PASS against immutable 1.4.5.
- `tools/quality_gate.py`: PASS, including Vulkan 1.4.364 registry lock/regeneration, compile-regression scan, targeted Vulkan specification regressions, report semantics/surface integrity, Vulkan Video registry/profile census, probe lifecycle/timeout/cancellation, hardening, concurrency/resource contracts and strict source package census.
- Strict source census after 1.4.6 targeted gate additions: 629 files.
- No permission, exported-component, network-endpoint, report-schema, Database-submission, identifier-collection or native-resource ownership change is introduced by this release.
- The opening sequence creates no native Vulkan resource, performs no report query and starts no network request; it uses bounded Compose state/animation and existing packaged artwork.
- Persistent compact labels and Material navigation semantics remain available, while the full-screen opening overlay prevents pointer activation of underlying content during the gated sequence.

## Build/runtime evidence boundary
- Android `:app:assembleRelease`: NOT EXECUTED to project compilation. `bash ./gradlew :app:assembleRelease` attempted Gradle 9.7.1 wrapper bootstrap, but `services.gradle.org` could not be resolved and terminated with `UnknownHostException` before Gradle project compilation.
- Emulator/device runtime, real phone/tablet/TV/Googlebook/ChromeOS, TalkBack, rotation, keyboard/TV focus and profiler execution are NOT EXECUTED in this environment and are not represented as PASS.
- Final delivery still requires deterministic dual-ZIP byte identity, clean extraction, full source-to-extract byte equality, strict package hygiene and rerunning the applicable 1.4.6 gates on the clean extracted tree.

# VulkanScope 1.4.7 compile repair and mouse pointer-input audit

## Release identity and immutable predecessor
- Version: 1.4.7.
- versionCode: 1407.
- Immutable predecessor: VulkanScope 1.4.6.
- Predecessor ZIP SHA-256: `4588e08984b20cabbdb5a26a7840da34130ec3952e50adea6d70fc2b1211a18f`.
- Predecessor source-package census: 629 files.
- Production runtime delta is restricted to `app/src/main/java/com/efishell/vulkanscope/MainActivity.kt`; `app/build.gradle.kts` changes release identity only.

## BUG/COMPILE finding and repair
- The supplied release build reached `:app:compileReleaseKotlin` after native builds for arm64-v8a, armeabi-v7a and x86_64.
- Kotlin reported a JVM platform declaration clash for `setOpeningAnimationEnabled(Z)V`: the mutable `openingAnimationEnabled` property generated a synthetic setter with the same JVM signature as the explicitly declared `setOpeningAnimationEnabled(Boolean)` helper.
- The helper was renamed to `persistOpeningAnimationPreference(Boolean)`. The property, SharedPreferences key, default-enabled behavior, direct Preferences switch and startup animation gate remain unchanged.
- A local Kotlin compiler regression oracle reproduced the predecessor naming collision in a minimal class and compiled the renamed-helper form successfully. This is narrow compiler evidence for the naming defect, not a substitute for the full Android build.

## USABILITY/INPUT finding and repair
- The Compose scrolling implementation intentionally excludes `PointerType.Mouse` from drag recognition, so mouse primary-button drag panning requires an application-level pointer path while wheel scrolling remains a distinct pointer scroll event.
- Added a bounded vertical pointer modifier that handles `PointerEventType.Scroll` using Android `ViewConfiguration.scaledVerticalScrollFactor` and consumes the event only when the bound `ScrollableState` consumes a non-zero delta.
- Added primary-button mouse drag panning with platform touch-slop protection. Ordinary mouse clicks remain unconsumed until vertical movement exceeds slop. After slop, upward pointer movement scrolls forward/down the content and downward movement scrolls backward/up the content.
- Pointer handling uses the Main event pass so nested/child scrollable content receives normal priority before a parent fallback path.
- Coverage includes the shared VulkanLazyPage path, the navigation rail, detail/dialog scrolling, Turnip file-manager list/grid, shared-storage browser, update log, release notes and filter result lists.
- The change is generic Android mouse-pointer compatibility. It does not infer Googlebook, ChromeOS, device model, brand or fingerprint identity.

## Retained correctness/security boundaries
- Vulkan 1.4.364 registry/header/query lock, Vulkan Video data, native collector/service, report schemas, Database submission behavior, updater trust boundaries, manifest permissions/export topology, ABI set and packaged artwork are byte-identical to the immutable predecessor.
- The 1.4.6 opening animation still blocks Vulkan/display/network/update startup work until completion when enabled; only the colliding preference-helper name changed.
- No new permission, endpoint, identifier, background upload, file authority, native allocation, Vulkan query or polling loop was introduced.

## Targeted release evidence
- `tools/verify_release_1407.py`: PASS.
- `tools/test_release_1407_state_machine.py`: PASS, 9 modeled pointer/gesture cases.
- `tools/test_release_1407_negative_mutations.py`: PASS, 10 targeted mutations plus one unrelated-audit false-positive control.
- `tools/verify_release_1407_regression.py`: PASS against the immutable 1.4.6 tree; predecessor fails on the observed setter collision and missing mouse pointer-scroll contract.
- `tools/quality_gate.py`: PASS for 1.4.7 including locked Vulkan registry regeneration/snapshot parity, compile-regression scanning, Vulkan spec regressions, report semantics/surface integrity, Vulkan Video census, probe lifecycle/timeout/cancellation, full hardening and concurrency/resource contracts.
- Source package hygiene before final packaging: PASS with 634 files.

## Android build/runtime evidence boundary
- Full `:app:compileReleaseKotlin` was attempted in this environment after the patch, but Gradle 9.7.1 bootstrap could not resolve `services.gradle.org` and exited with `UnknownHostException` before project compilation. Full Android compilation is therefore `NOT EXECUTED`, not PASS.
- Real-device mouse-wheel and primary-button drag behavior on Googlebook/ChromeOS/phone/tablet/TV is also `NOT EXECUTED` in this environment. The release includes source-level/API evidence and modeled gesture regressions, but hardware runtime verification remains distinct evidence.
- The libadrenotools diagnostics shown in the supplied predecessor build are third-party C/C++ warnings and were not changed by this targeted release.

## Final packaging requirements
- Regenerate the strict `files.txt` census after all 1.4.7 release artifacts are finalized.
- Produce two deterministic ZIPs and require byte-identical output.
- Extract the final ZIP into a clean directory, compare every listed file byte-for-byte with the source tree, rerun strict package hygiene and rerun the applicable 1.4.7 quality gate against the extracted tree.
## Executed 1.4.16 evidence
- `tools/verify_release_1416.py`: PASS.
- `tools/test_release_1416_state_machine.py`: PASS.
- `tools/test_release_1416_negative_mutations.py`: PASS, including documentation-only false-positive control.
- `tools/verify_release_1416_regression.py`: PASS against immutable 1.4.15 ZIP.
- `tools/quality_gate.py`: PASS, including Vulkan 1.4.364 registry/spec, report semantics, Surface integrity, Profiles, Vulkan Video, probe lifecycle/timeout/cancellation, hardening, concurrency/resource and package-census checks.
- `:app:compileReleaseKotlin`: NOT EXECUTED. The Gradle wrapper attempted to download Gradle 9.7.1 but `services.gradle.org` failed DNS resolution with `UnknownHostException` before Gradle/project compilation began.

# VulkanScope 1.5.2 AGP 9.4.1 full audit

## Release identity and immutable predecessor
- Version: 1.5.2.
- versionCode: 1502.
- Immutable predecessor: VulkanScope 1.5.1.
- Predecessor ZIP SHA-256: `3706e360cc5a860be64718824c9798e823f3a0e753b103833038f244e254d600`.
- Predecessor archive census: 702 entries.
- Production/build-chain delta is restricted to root `build.gradle.kts` AGP 9.4.0 -> 9.4.1 and `app/build.gradle.kts` release identity 1.5.1/1501 -> 1.5.2/1502.

## Build-chain finding and repair
- Official Android release documentation was checked on 2026-09-26. AGP 9.4.1 is the current stable 9.4 patch; AGP 9.5 is preview.
- AGP 9.4.1 contains D8/R8 crash/performance fixes and an R8 deterministic-output repair relevant to VulkanScope's minified/shrunk release builds.
- Gradle 9.7.1 is retained. AGP 9.4 requires Gradle 9.6.0 or newer, so the wrapper already satisfies the supported floor without expanding this release to a second uncompiled build-system migration.
- Compile/target SDK 37, NDK r29 `29.0.14206865`, Kotlin Compose plugin 2.4.10, ABI set and dependency pins remain unchanged.

## Full static audit result
- Vulkan registry/header/query baseline remains locked to Vulkan 1.4.364 / header 364 with unchanged generated registry data and native query sources.
- No application/runtime byte changed. Report JSON/TXT/HTML/Database semantics, Profiles tally semantics, Surface/Display separation and Vulkan Video census remain predecessor-equivalent.
- Manifest remains `allowBackup=false`, `usesCleartextTraffic=false`, `supportsRtl=true`; provider and probe service remain non-exported and no permission was added.
- Database HTTPS redirect blocking, update trust/signature/version validation, native library read-only handling, ZIP/path confinement and bounded report/archive/probe inputs remain unchanged.
- Activity coroutine/call/job cancellation, callback unregistration, probe process stop and service worker shutdown remain present. No new native allocation, coroutine scope, executor, polling loop or persistent observer was added.
- Primary navigation remains four 77.5 dp nominal-width by 54 dp cells at the 310 dp cap, exceeding the Android 48 dp minimum touch target in both dimensions; `Role.Tab`, persistent text labels and RTL support remain present.
- Static scan found no newly introduced WebView JavaScript bridge, Android ID/serial/telephony/account identifier collection, hidden upload path, cleartext endpoint, GlobalScope or blocking UI-thread sleep.
- Existing read/event `while(true)` loops are predecessor bytes and terminate on EOF, cancellation, pointer-scope disposal, bounded timeout/state transitions or explicit limits as covered by retained resource/lifecycle gates.

## Evidence
- `tools/verify_release_1502.py`: PASS.
- `tools/test_release_1502_state_machine.py`: PASS.
- `tools/test_release_1502_negative_mutations.py`: PASS.
- `tools/verify_release_1502_regression.py --predecessor <immutable-1.5.1-tree>`: PASS.
- `tools/quality_gate.py`: PASS for the 1.5.2 route, including Vulkan 1.4.364 registry lock/regeneration, 477 registered extensions, 305 Android-queryable extensions, targeted specification regressions, probe lifecycle/timeout/cancellation, report semantics, Surface integrity, Profiles source/state/negative mutations, 47 exact Vulkan Video capability combinations, full hardening and concurrency/resource contracts.
- The first aggregate run correctly rejected the retained 0.80.0 hardening verifier because its historical AGP selector still required 9.4.0. The verifier was updated only to select 9.4.1 for release >= 1.5.2; its state machine and negative-mutation suite then passed.
- Candidate deterministic dual-ZIP generation produced byte-identical archives; clean extraction was source/package byte-identical across all 672 listed files and the full 1.5.2 quality route passed from the extracted tree. Final packaging repeats the same acceptance after this evidence text is frozen.
- Android `:app:assembleRelease`: NOT EXECUTED to project compilation. The wrapper attempted to download Gradle 9.7.1 from `services.gradle.org` and stopped with `UnknownHostException` before Gradle/project execution. Android compile/lint/unit are therefore also NOT EXECUTED in this environment, never PASS.
- Emulator/device runtime, TalkBack/Switch Access, sanitizer, validation-layer and profiler evidence: NOT EXECUTED in this environment.


# VulkanScope 2.0.0 collection timing instrumentation audit
- Version 2.0.0 / versionCode 2000.
- Scope is instrumentation-first: no collector batching/session architecture change is shipped until device timing identifies the dominant phase.
- Added app-side phase timings and an optional bounded private-cache timing sidecar from the isolated Vulkan probe service.
- Sidecar path is canonical/cache-confined, exactly result-path + `.timing`, at most 16 KiB, local-only and deleted after bounded consumption.
- Capability semantics, native Vulkan collector, registry/header baseline, reports/Database payload, permissions, ABI/dependencies and AGP/Gradle remain unchanged from 1.5.5 except release identity and timing instrumentation source.
- Device-observed performance conclusions are intentionally deferred until 2.0.0 is run on target hardware.

## Executed 2.0.0 evidence
- `tools/verify_release_2000.py`: PASS.
- `tools/test_release_2000_state_machine.py`: PASS.
- `tools/test_release_2000_negative_mutations.py`: PASS.
- `tools/quality_gate.py`: PASS for the 2.0.0 route, including Vulkan 1.4.364 registry lock/snapshot, compile/spec regressions, probe publication/terminal ownership, lifecycle/timeout/cancellation, report/Surface/Profile/Video semantics, hardening and concurrency/resource contracts.
- Immutable predecessor regression contract: 160 production/build-chain/registry files remain byte-identical to VulkanScope 1.5.5; permitted production changes are limited to release identity plus MainActivity/AdvancedAnalysis/VulkanProbeService timing instrumentation.
- Android `:app:assembleRelease`: NOT EXECUTED at project evaluation/compile level because the Gradle wrapper could not download Gradle 9.7.1; `services.gradle.org` failed DNS resolution with `UnknownHostException`. No compile/assemble PASS claim is made.
- Device runtime performance timing: NOT EXECUTED in this environment. 2.0.0 is intended to collect this evidence on the target device before collector-architecture optimization.

## 2.0.0 packaging evidence
- Candidate deterministic dual-ZIP generation: PASS; candidate archives were byte-identical.
- Candidate SHA-256: `ca8e192469f20e0334c6e30803ca007cc4dcbca44249f525998719a2bbfc6c4f`.
- Candidate archive entries: 680.
- Clean extraction source/package byte equality: PASS for all 680 listed files.
- Extracted-tree `tools/verify_release_2000.py`, state-machine and negative-mutation suites: PASS.
- Extracted-tree full `tools/quality_gate.py`: PASS.
- Final deterministic packaging is repeated only after this evidence text is frozen.


# VulkanScope 2.0.2 corrective performance and UI audit
- Version 2.0.2 / versionCode 2002.
- Functional correctness baseline is VulkanScope 2.0.0. The 2.0.1 multi-query session experiment is not retained.
- Native C++ collector and registry/generated query bytes are unchanged from 2.0.0; background speed work is restricted to bounded parallel orchestration of the original one-shot query processes.
- Maximum configured background lanes: 4. Runtime lane count is 2/3/4 according to Android memoryClass thresholds 256/384 MiB.
- Automatic background queries remain one query per process, with the original 12 s / advanced 30 s query limits and 60 s total background budget.
- Parallel raw results are merged in original query-list order, preserving deterministic report precedence and the exact validated extension scheduling path.
- Surface root chooser now consumes dynamic transient-overlay, bottom-navigation and side navigation-bar insets.
- Application-owned evidence secondary-click quick menu is suppressed on ARC/Android-PC/freeform desktop environments while touch long-press and accessibility evidence actions remain intact.
- Four additional Vulkan probe services are non-exported and execute in separate private process names.
- Android build/runtime performance and capability-count parity remain separate runtime evidence classes until executed on target hardware.


## Executed 2.0.2 evidence before packaging
- `tools/verify_release_2002.py`: PASS.
- `tools/test_release_2002_state_machine.py`: PASS.
- `tools/test_release_2002_negative_mutations.py`: PASS.
- `tools/quality_gate.py`: PASS for the 2.0.2 route, including Vulkan 1.4.364 lock/snapshot, 477 registered extensions, 305 Android-queryable providers, specification regressions, probe publication/terminal ownership, lifecycle/timeout/cancellation, report/Surface/Profile/Video semantics, hardening and concurrency/resource contracts.
- 2.0.0 parity hashes: native C++ collector, generated/query registry data, validated extension coverage and Analysis model remained byte-identical to the 2.0.0 functional correctness baseline.
- Android `:app:assembleRelease`: NOT EXECUTED at Gradle project evaluation/compile level. The Gradle 9.7.1 wrapper attempted to download from `services.gradle.org` and failed DNS resolution with `UnknownHostException`; no Android compile/assemble PASS claim is made.
- Real-device performance, capability-count parity, Surface overlay placement and desktop secondary-click behavior remain runtime evidence classes until this package is exercised on target hardware.


## 2.0.2 candidate packaging evidence
- Deterministic candidate dual-ZIP identity: PASS.
- Candidate SHA-256: `6a23ff2e122d922b1d502e29cc3eb647cf88d84110dafef78059bc14940c251a` for both candidate archives.
- Candidate archive/file census: 684 listed files.
- Clean extraction source/package byte equality: PASS for all 684 listed files; no missing, extra or mismatched file.
- Candidate extracted-tree targeted 2.0.2 verifier/state/negative-mutation suites: PASS.
- Candidate extracted-tree full `tools/quality_gate.py`: PASS.
- Final deterministic packaging is repeated after this evidence text is frozen.


# VulkanScope 2.0.3 adaptive parallel scheduler audit
- Version: 2.0.3.
- versionCode: 2003.
- Functional correctness baseline remains 2.0.0/2.0.2 one-query-per-process collection.
- Production runtime delta from 2.0.2 is restricted to `MainActivity.kt` scheduler/lane-selection/quiescence/polling/timing attribution plus release identity. Native collector, probe service, registry/generated query data and validated extension coverage remain byte-identical.
- Static and packaging evidence is recorded by the 2.0.3 targeted and aggregate quality gates. Real-device latency remains separate runtime evidence.


## Executed 2.0.3 evidence
- Targeted 2.0.3 adaptive scheduler verifier: PASS.
- Adaptive lane/scheduler state-machine test: PASS.
- Targeted negative-mutation test: PASS.
- Full 2.0.3 static quality gate: PASS, including Vulkan 1.4.364 registry/spec locks, 477 registered extensions, 305 validated Android-queryable providers, report/Surface/Profile/Video semantics, probe publication/terminal ownership, lifecycle/timeout/cancellation, hardening and concurrency/resource ceilings.
- `:app:assembleRelease`: NOT EXECUTED at project evaluation/compilation level because Gradle wrapper bootstrap could not resolve `services.gradle.org` and terminated with `UnknownHostException` before Gradle 9.7.1 was available. This is not reported as PASS.


## 2.0.3 candidate packaging evidence
- Deterministic dual candidate ZIP generation: PASS; both archives SHA-256 `6f49f4dfec2c2236370fd4f839ed4b02e01c897bfc59f2abdeef1897c71a0967`.
- Candidate archive entries: 688.
- Clean extraction source/package byte equality: PASS for 688/688 listed files; no missing, differing or extra files.
- Extracted-tree full 2.0.3 static quality gate: PASS.

# VulkanScope 2.0.4 adaptive six-lane scheduler and Android TV long-press audit
- Version: 2.0.4.
- versionCode: 2004.
- Functional correctness baseline remains VulkanScope 2.0.3 / one-query-per-dedicated-process collection.
- Background scheduler ceiling increases from four to six private non-exported one-shot lanes only on devices that pass low-RAM, current memory-pressure, physical total/available-memory, available-ratio and CPU gates. Lower-resource devices retain bounded 2/3/4/5-lane operation.
- Active Turnip ICD/bundle paths are resolved once per immutable background collection pass instead of once per query; driver mutation remains collection-gated, so query identity is unchanged while repeated private-storage resolution is removed.
- Android TV focused evidence rows intercept long-press/repeat evidence from DPAD_CENTER, ENTER, NUMPAD_ENTER, BUTTON_SELECT and BUTTON_A and open the existing Evidence provenance/actions dialog. Initial short presses are not consumed as long presses. Recognized hold repeats/release are consumed to prevent duplicate actions.
- Googlebook/ARC/Android-PC/freeform secondary-click quick-menu suppression remains unchanged from 2.0.2.
- Native C++ collector, locked Vulkan 1.4.364 registry/generated query data, validated extension coverage, report/Database schema, permissions, endpoints, ABI set and AGP/Gradle versions remain unchanged from 2.0.3.

## Executed 2.0.4 evidence before packaging
- `tools/verify_release_2004.py`: PASS.
- `tools/test_release_2004_state_machine.py`: PASS.
- `tools/test_release_2004_negative_mutations.py`: PASS.
- Full static quality route reached PASS for Vulkan 1.4.364 registry/snapshot, 477 registered extensions, 305 Android-queryable providers, compile/spec regressions, probe publication/terminal ownership, lifecycle/timeout/cancellation, report/Surface/Profile/Video semantics, hardening and concurrency/resource contracts.
- The first aggregate package-hygiene check correctly rejected the stale `files.txt` census after the new 2.0.4 audit/test files were added. `files.txt` was regenerated and `tools/verify_package_reproducibility.py` then passed with 692 source files.
- Android `:app:assembleRelease`: NOT EXECUTED at project evaluation/compile level because the Gradle wrapper could not download Gradle 9.7.1; `services.gradle.org` failed DNS resolution with `UnknownHostException`. No Android compile/assemble PASS claim is made.
- Real-device 2.0.4 latency and Android TV remote long-press execution remain runtime evidence classes until this package is exercised on target hardware.

## 2.0.4 candidate packaging evidence
- Deterministic candidate dual-ZIP identity: PASS.
- Candidate SHA-256: `cbed0336a03f7854166eeb744b0655ebc62f47aabf985c5d34d60e81b9dd6582` for both candidate archives.
- Candidate source/file census: 692 listed files.
- Clean extraction source/package byte equality: PASS for all 692 files; no missing, differing or extra file.
- Candidate extracted-tree targeted 2.0.4 verifier/state/negative-mutation suites: PASS.
- Candidate extracted-tree registry/spec/probe/report/Surface/Profile/Video/hardening/concurrency/resource/package-hygiene route: PASS.
- Final deterministic packaging is repeated after this evidence text is frozen.


# VulkanScope 2.0.5 Android TV key-input compile-fix audit
- Version: 2.0.5.
- versionCode: 2005.
- Release purpose is limited to correcting the 2.0.4 Kotlin compile blocker in Android TV long-press input handling.
- User-provided `:app:assembleRelease` evidence reached all three native ABI builds and failed only at `:app:compileReleaseKotlin` with unresolved reference `nativeKeyEvent` in `MainActivity.kt`.
- Production runtime delta from 2.0.4 is restricted to `MainActivity.kt` TV key-input handling and release identity. Scheduler, probe service, native collector, registry/generated query data, validated extension coverage, manifest service lanes and build-chain versions remain unchanged.
- Android TV long press is reimplemented with public Compose key APIs and a 550 ms lifecycle-bound timer; short presses remain unconsumed.
- libadrenotools warnings remain warning-only upstream diagnostics and are not modified by this compile fix.

## Executed 2.0.5 evidence before packaging
- `tools/verify_release_2005.py`: PASS.
- `tools/test_release_2005_state_machine.py`: PASS.
- `tools/test_release_2005_negative_mutations.py`: PASS.
- Locked Vulkan 1.4.364 CMake/registry snapshot, compile-regression, specification-regression, probe publication/terminal ownership, lifecycle/timeout/cancellation, report/Surface/Profile/Video, hardening and concurrency/resource gates: PASS.
- Registry regeneration from the bundled SHA-256-locked `vk.xml` matched the checked-in manifest exactly; Python/JSON syntax census: PASS.
- Source package manifest/hygiene: PASS for 696 listed files before final evidence freeze.
- Android `:app:assembleRelease` in this environment: NOT EXECUTED at project compilation level because the Gradle 9.7.1 wrapper could not resolve `services.gradle.org` and stopped with `UnknownHostException` before Gradle was available.
- The user-provided Windows/Android Studio build evidence is the reproduced compile failure basis: all native ABI CMake builds completed and the build stopped at `:app:compileReleaseKotlin` only because `nativeKeyEvent` was unresolved.
- The replacement APIs are public Compose key APIs (`KeyEvent.key`, `KeyEvent.type`, `Key.nativeKeyCode`); no unavailable `nativeKeyEvent` reference remains in production source.

## 2.0.5 candidate packaging evidence
- Deterministic candidate dual-ZIP identity: PASS.
- Candidate SHA-256: `8e1d1dd1a390dd54aad231da2b7ef9c214488fc19464afa06b4ac04492e66d4e` for both candidate archives.
- Candidate archive/file census: 696 listed files.
- Clean extraction source/package byte equality: PASS for 696/696 listed files; no missing, differing or extra file.
- Candidate extracted-tree targeted 2.0.5 verifier/state/negative-mutation, registry snapshot, compile/spec regressions, probe lifecycle/timeout/cancellation, report/Surface/Profile/Video, concurrency/resource and package-hygiene gates: PASS.
- Final deterministic packaging is repeated after this evidence text is frozen.

# VulkanScope 2.0.6 Surface chooser scrollability audit

- Version: 2.0.6.
- versionCode: 2006.
- Immediate predecessor: VulkanScope 2.0.5.
- User-reported failure: the Surface landing chooser could exceed the available landscape viewport and its lower destination was not reachable because `SurfaceSectionCards` used a non-scrollable full-size `Column`.
- Production fix is confined to `MainActivity.kt` plus release identity: `SurfaceSectionCards` now owns a bounded `ScrollState`, uses the existing desktop pointer-wheel path plus Compose vertical scrolling/focus grouping, retains dynamic top/bottom/system-navigation safe insets and adds `ExpressiveScrollHints` using the same safe bounds.
- No Surface capability data, Vulkan query, native/JNI, report/Database schema, permission, endpoint, scheduler, TV long-press, ABI or dependency/build-chain behavior changed.
- `tools/verify_release_2006.py`: PASS.
- `tools/test_release_2006_state_machine.py`: PASS.
- `tools/test_release_2006_negative_mutations.py`: PASS.
- Full 2.0.6 static quality gate: PASS, including Vulkan 1.4.364 registry lock/regeneration, 477 registered extensions, 305 Android-queryable extensions, specification regressions, probe lifecycle/timeout/cancellation, report/Surface/Profile/Video semantics, full hardening, concurrency/resource ceilings and source-package hygiene for 700 files.
- Android `:app:assembleRelease`: NOT EXECUTED at project level in this environment because the Gradle wrapper cannot resolve `services.gradle.org` while downloading Gradle 9.7.1; bootstrap stops with `UnknownHostException` before Gradle evaluates the project.
- Candidate deterministic dual-ZIP generation: PASS; both candidates were byte-identical before final audit freeze.
- Candidate clean extraction: PASS; all 700 listed files matched source bytes exactly with no missing, mismatched or extra packaged files.
- Candidate extracted-tree full 2.0.6 quality gate: PASS.
- Final packaging repeats deterministic dual-ZIP identity, clean extraction/source equality and extracted-tree verification after this evidence is frozen.
