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
