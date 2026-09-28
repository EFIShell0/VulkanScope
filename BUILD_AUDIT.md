# VulkanScope 3.0.2 Build Audit

## Scope and immutable predecessor
- Immutable predecessor: VulkanScope 3.0.1, 801 files, ZIP SHA-256 `eaae03f93ea7c636b8d7bba2e21a871d65349e1ab690cea0dc88bfae5e5cac12`.
- Release identity: VulkanScope 3.0.2 (`versionCode 3002`).
- Production runtime-source delta is restricted to one explicit Material 3 Expressive opt-in annotation on `SharedStorageBrowserListing`; `app/build.gradle.kts` changes release identity only.

## Failing-before-fix evidence and root cause
- The supplied `:app:assembleRelease` log completes C/C++ work for arm64-v8a, armeabi-v7a and x86_64, then fails `:app:compileReleaseKotlin` at `MainActivity.kt:12564:17` with the Material 3 experimental-API diagnostic.
- The failing line is the existing `LoadingIndicator` in the top-level `SharedStorageBrowserListing` extracted in 3.0.1. The parent dialog is opted into `ExperimentalMaterial3ExpressiveApi`, but that annotation does not cover a separate top-level composable declaration.

## Repair boundary
- `SharedStorageBrowserListing` now declares `@OptIn(ExperimentalMaterial3ExpressiveApi::class)` immediately before `@Composable`.
- `LoadingIndicator` and the 3.0.1 landscape/portrait file-manager UI remain unchanged. No Vulkan/native/report/Database/Turnip/storage/input/rendering behavior is rewritten.

## Executed static/targeted evidence
- `tools/verify_release_3002.py`: PASS.
- `tools/test_release_3002_state_machine.py`: PASS.
- `tools/test_release_3002_negative_mutations.py`: PASS, including documentation-only false-positive control.
- `tools/verify_release_3002_regression.py --predecessor /mnt/data/vulkanscope_3_0_1_pred302`: PASS.
- `tools/test_release_3001_state_machine.py`: PASS, preserving the preceding UI/input/layout state contracts.
- `tools/verify_compile_regressions.py`: PASS through the complete release-specific static quality gate.
- Complete `tools/quality_gate.py`: PASS; strict canonical Vulkan header byte-level verification remains NOT EXECUTED because no local canonical header path was supplied.

## Android build evidence
- `TERM=xterm bash ./gradlew :app:compileReleaseKotlin --offline` was attempted.
- Gradle wrapper bootstrap attempted to resolve Gradle 9.7.1 from `services.gradle.org` and failed with `UnknownHostException` before project compilation.
- Android `:app:compileReleaseKotlin`, `:app:assembleRelease`, lint and post-fix device replay therefore remain NOT EXECUTED to a PASS result here.

## Packaging status
- Source census before packaging: 806 files.
- Final deterministic dual-ZIP identity, clean-extract byte equality and clean-tree release gates are executed after this audit text is frozen.

# VulkanScope 3.0.1 Build Audit

## Scope and immutable predecessor
- Immutable predecessor: VulkanScope 3.0.0, 796 files, ZIP SHA-256 `c34d7093bc2e52da7e5f11c167018103fb1cbff0413bc0941314c17cc7618a44`.
- Release identity: VulkanScope 3.0.1 (`versionCode 3001`).
- Production runtime changes are restricted to `app/src/main/java/com/efishell/vulkanscope/MainActivity.kt`; `app/build.gradle.kts` changes release identity only.
- Manifest, native/JNI collector, Vulkan 1.4.364 registry/generated data, reports/Database/export semantics, Turnip validation/install behavior, permissions, endpoints, ABI targets and dependencies remain predecessor-equivalent.

## Failing-before-fix evidence
- The supplied landscape shared-storage export screenshot shows the file/folder viewport compressed to a shallow strip by vertically stacked controls.
- The supplied Overview screenshot shows the first rounded page surface entering/touching the translucent app header at rest.
- Predecessor source inspection shows the opening red rule reached scale 1.0 and then shrank to 0.88/0.58 in later phases before fade-out.
- Predecessor input code suppressed the explicit desktop quick menu but still placed generic long-press handling in the same evidence row, so a desktop secondary gesture needed to be consumed before gesture interpretation.
- Post-fix runtime replay on portrait, landscape, ChromeOS and Android-PC/Googlebook-style hardware is not available here and is not claimed PASS.

## Research and repair boundary
- Current Android Compose gesture documentation was checked: the Initial pointer-event pass is the parent-first interception pass, and custom raw pointer handlers must explicitly consume owned changes. The secondary-button no-op therefore intercepts the complete mouse secondary sequence at the Initial pass.
- Desktop environment behavior uses only the existing documented ChromeOS ARC feature and Android `FEATURE_PC` evidence; no Googlebook model/brand/fingerprint heuristic is introduced.
- Opening rule scale now reaches 1.0 and stays at 1.0 through later phases; alpha may fade but length no longer retreats. The inherited duplicate `lineWidth` declaration is absent.
- Shared-storage file browsing uses a live-window two-pane landscape layout at >= 700 dp width: bounded scrollable controls occupy the left pane while the existing lazy folder/file browser owns the full-height right pane. Portrait remains vertically stacked.
- Primary/touch evidence-row hold feedback keeps the existing inward scale/fill and adds a 1 dp `VulkanAccentSoft` border animated to alpha 0.46, matching the file-manager chrome border tone.
- Initial content geometry now adds a 12 dp positive separation below the live app-header inset for lazy pages, the physical-device selector and the Surface landing page. Sticky pager clamp geometry still uses the actual header boundary.

## Executed static/targeted evidence
- `tools/verify_release_3001.py`: PASS.
- `tools/test_release_3001_state_machine.py`: PASS for opening-rule stability, portrait/landscape page separation, two-pane landscape browser geometry, complete desktop secondary-button consumption and hold-border state.
- `tools/test_release_3001_negative_mutations.py`: PASS, including documentation-only false-positive control.
- `tools/verify_release_3001_regression.py --predecessor /mnt/data/vulkanscope_3_0_0_pred301`: PASS.
- `tools/verify_compile_regressions.py`: PASS through the complete release-specific static quality gate.
- Complete `tools/quality_gate.py`: PASS; strict canonical Vulkan header byte-level verification remains NOT EXECUTED because no local canonical header path was supplied.

## Android build evidence
- `TERM=xterm bash ./gradlew :app:compileReleaseKotlin --offline` was attempted.
- Gradle wrapper bootstrap attempted to resolve Gradle 9.7.1 from `services.gradle.org` and failed with `UnknownHostException` before project compilation.
- Android `:app:compileReleaseKotlin`, `:app:assembleRelease`, lint and post-fix device replay therefore remain NOT EXECUTED to a PASS result here.

## Packaging status
- Source census: 801 files.
- Preliminary independent deterministic ZIP builds were byte-identical.
- Preliminary clean extraction matched the source tree byte-for-byte for all 801 files.
- On that clean extraction, `tools/verify_release_3001.py`, `tools/test_release_3001_state_machine.py`, `tools/test_release_3001_negative_mutations.py`, predecessor regression and the complete release-specific `tools/quality_gate.py` all PASS.
- Final deterministic packaging and a second clean-extraction byte/gate rerun are performed after this audit is frozen; post-fix device replay and Android Gradle compilation remain NOT EXECUTED to PASS.

# VulkanScope 3.0.0 Build Audit

## Scope and immutable predecessor
- Immutable predecessor: VulkanScope 2.1.16, 791 files, ZIP SHA-256 `dac7f4e5751aef4f194effa3c5f9c979b168439c610c42adc19bccd2f99fc567`.
- Release identity: VulkanScope 3.0.0 (`versionCode 3000`).
- Production runtime changes are restricted to `app/src/main/java/com/efishell/vulkanscope/MainActivity.kt`; `app/build.gradle.kts` changes release identity only.
- Manifest, native/JNI collector, Vulkan 1.4.364 registry/generated data, reports/Database/export semantics, Turnip handling, permissions, endpoints, ABI targets, dependency versions and packaged artwork remain predecessor-equivalent.

## Observed failures and upstream research
- The supplied portrait screenshot is the visual regression oracle for the page/header seam: the first rounded page surface terminates exactly at the translucent header edge instead of continuing slightly beneath it.
- Current official Android edge-to-edge documentation was checked on 2026-09-28. It confirms `enableEdgeToEdge()` for backward-compatible edge-to-edge drawing, live `WindowInsets.navigationBars` handling, and disabling navigation-bar contrast enforcement for a fully transparent three-button navigation bar.
- Post-fix portrait/landscape device replay is not available in this packaging environment and is not claimed PASS.

## Repair boundary
- Android navigation-bar color is transparent, edge-to-edge is enabled, and a bottom-or-side `SystemNavigationBackdrop` reuses the existing retained blurred page/chrome layer plus `VulkanGlassTint` behind system navigation controls.
- Ordinary page content begins 10 dp beneath the translucent header while the sticky pager clamp continues to use the real header boundary.
- The opening accent treatment now renders one continuous red rule extended across the former endpoint-dot span; separate endpoint dots are removed.
- Pager lane coordination begins 96 dp before the visible join region, the lane reporter uses `SideEffect`, and pager/status/scroll-indicator relocation uses the shared 120 ms bounded motion timing. Status surfaces and scroll indicators retain explicit z-order above scrolling content during crossings.

## Executed evidence
- `tools/verify_release_3000.py`: PASS.
- `tools/test_release_3000_state_machine.py`: PASS for portrait/landscape header underlap, bottom/side system-navigation protection and preemptive overlay-lane geometry.
- `tools/test_release_3000_negative_mutations.py`: PASS with documentation-only false-positive control.
- `tools/verify_release_3000_regression.py --predecessor /mnt/data/vulkanscope_2_1_16_pred3000`: PASS.
- `tools/verify_compile_regressions.py`: PASS.
- Complete release-specific `tools/quality_gate.py`: PASS; strict canonical Vulkan header byte-level verification remains NOT EXECUTED because no local canonical header path was supplied.

## Android build evidence
- `bash ./gradlew :app:compileReleaseKotlin --offline` was attempted.
- Gradle wrapper bootstrap attempted to resolve Gradle 9.7.1 from `services.gradle.org` and failed with `UnknownHostException` before project compilation.
- Android Kotlin compilation, `:app:assembleRelease`, lint and post-fix device replay therefore remain NOT EXECUTED to a PASS result in this environment.

# VulkanScope 2.1.16 Build Audit

## Scope and immutable predecessor
- Immutable predecessor: VulkanScope 2.1.15, 786 files, ZIP SHA-256 `49c99c11d2045b0ac9197580f3303ec823adbab6db917118d0cd526aa48a895a`.
- Release identity: VulkanScope 2.1.16 (`versionCode 2116`).
- Production runtime change is restricted to the lazy pager overlay coordinate conversion in `app/src/main/java/com/efishell/vulkanscope/MainActivity.kt`; `app/build.gradle.kts` changes release identity only.
- Manifest, native/JNI collector, Vulkan 1.4.364 registry/generated data, reports/Database/export semantics, Turnip handling, permissions, endpoints, ABI targets, dependencies, artwork and the 2.1.15 shared-glass implementation remain predecessor-equivalent.

## Failing-before-fix evidence and research
- The supplied 2026-09-28 portrait screenshot is the regression oracle: the pager is visibly placed over the `Showing` metric row while the reserved pager slot is lower.
- Current Android documentation was checked for `LazyListItemInfo.offset`, `LazyListLayoutInfo.viewportStartOffset`, content padding and item spacing. Non-zero before-content padding can make the viewport start offset negative, so raw lazy-item offsets are not sufficient as sibling overlay coordinates.
- Post-fix portrait/landscape device replay is not available here and is not claimed PASS.

## Repair boundary
- Direct anchor Y is mapped with `direct.offset - layoutInfo.viewportStartOffset`.
- Previous- and next-item bridge Y use the same viewport-start conversion after reconstructing the pager's lazy-list offset.
- One pager, exact-height spacer, header clamp, join progress, retained height, shared blur source/tint, transient-status ordering and top-scroll-indicator animation remain unchanged.
- No fixed orientation correction, extra pager animation or new rendering path is introduced.

## Executed static/targeted evidence
- `tools/verify_release_2116.py`: PASS.
- `tools/test_release_2116_state_machine.py`: PASS for non-zero viewport-start offsets, direct anchor landing, both adjacent bridge paths, dynamic top-content padding, header clamp and portrait/landscape safe width.
- `tools/test_release_2116_negative_mutations.py`: PASS, including direct/previous/next viewport-conversion regressions and documentation-only false-positive control.
- `tools/verify_release_2116_regression.py --predecessor /mnt/data/vulkanscope_2_1_15_pred216`: PASS; production MainActivity differs from 2.1.15 only in the three viewport-start coordinate conversions and Gradle differs only in release identity.
- `tools/verify_compile_regressions.py`: PASS.
- Complete release-specific `tools/quality_gate.py`: PASS; strict canonical Vulkan header byte-level verification remains NOT EXECUTED because no local canonical header path was supplied.

## Android build evidence
- `bash ./gradlew :app:compileReleaseKotlin --offline` was attempted in this environment.
- Gradle wrapper bootstrap still attempted to resolve Gradle 9.7.1 from `services.gradle.org` and failed with `UnknownHostException` before project compilation.
- Android `:app:compileReleaseKotlin`, `:app:assembleRelease`, lint and post-fix portrait/landscape device replay therefore remain NOT EXECUTED to a PASS result here.

## Final packaging evidence
- Strict sorted `files.txt` source census: 791 files, PASS.
- Two independent fixed-timestamp sorted-path deterministic ZIP generations were byte-identical, PASS.
- Clean extraction matched the source tree byte-for-byte for all 791 files, PASS.
- Clean extraction reran the 2.1.16 targeted verifier, viewport-alignment state machine, negative mutations, immutable-predecessor regression, compile regression and complete release-specific static quality gate: PASS.
- Post-fix portrait/landscape device replay and Android Gradle compilation remain separate unexecuted-to-PASS evidence classes and are not implied by packaging/static-gate PASS.

# VulkanScope 2.1.15 Build Audit

## Scope and immutable predecessor
- Immutable predecessor: VulkanScope 2.1.14, 781 files, ZIP SHA-256 `02037944d32d02377c0a45fe03020d915a8b0e34080ccb99b7e34b4e872e08ee`.
- Release identity: VulkanScope 2.1.15 (`versionCode 2115`).
- Production runtime changes are restricted to `app/src/main/java/com/efishell/vulkanscope/MainActivity.kt`; `app/build.gradle.kts` changes release identity only.
- Manifest, native/JNI collector, Vulkan 1.4.364 registry/generated data, reports/Database/export semantics, Turnip handling, permissions, endpoints, ABI targets, dependencies and packaged artwork remain predecessor-equivalent.

## Failing-before-fix runtime evidence
- The supplied 5.482-second 2.1.14 runtime video is the regression oracle: reverse scrolling does not make the pager settle into its reserved list position as one continuous object, and the pager-join backdrop differs visibly from the app-header glass.
- Android documentation was checked for the relevant Compose contracts: `LazyListItemInfo.offset` is relative to the lazy-list container; retained `GraphicsLayer` drawing can be replayed efficiently and RenderEffect-backed layers are rasterized offscreen.
- Post-fix portrait/landscape device replay is not available in this environment and is not claimed PASS.

## Repair boundary
- The lazy pager item is now permanently an exact-height spacer. One page-level `CollectionPager` is the only visual/interactive pager whenever its anchor is visible, bridgeable or already pinned, eliminating composable ownership transfer during return.
- While the anchor is visible, its live `LazyListItemInfo.offset` directly drives pager Y with no whole-card position tween; the only positional constraint is the live app-header clamp.
- Retained pager height remains page-owned and feeds both the spacer and the single pager measurement path.
- The active Vulkan lazy page reports its retained 24 dp blurred layer plus live root offset to the shared chrome host. Header and compact bottom navigation use that same source while active; pager join already samples the identical layer and `VulkanGlassTint`.
- Full-width join glass grows and shrinks from scroll-derived progress at the live header boundary; no separator border, gradient tint or independent height animation was added.
- The coordinated app header → pager → transient-status → top-scroll-indicator order remains unchanged.

## Executed static/targeted evidence
- `tools/verify_release_2115.py`: PASS.
- `tools/test_release_2115_state_machine.py`: PASS for portrait/landscape live-anchor following, header clamp, adjacent-item bridge, shared-root backdrop mapping, glass growth and overlay ordering.
- `tools/test_release_2115_negative_mutations.py`: PASS, including documentation-only false-positive control.
- `tools/verify_release_2115_regression.py --predecessor /mnt/data/vulkanscope_2_1_14`: PASS.
- `tools/verify_compile_regressions.py`: PASS.
- Complete release-specific `tools/quality_gate.py`: PASS; strict canonical Vulkan header byte-level verification remains NOT EXECUTED because no local canonical header path was supplied.

## Android build evidence
- `bash ./gradlew :app:compileReleaseKotlin --offline` was attempted in this environment.
- Gradle wrapper bootstrap still attempted to resolve Gradle 9.7.1 from `services.gradle.org` and failed with `UnknownHostException` before project compilation.
- Android `:app:compileReleaseKotlin`, `:app:assembleRelease`, lint and post-fix device replay therefore remain NOT EXECUTED to a PASS result here.

## Final packaging evidence
- Strict sorted `files.txt` source census: 786 files, PASS.
- Two independent fixed-timestamp sorted-path deterministic ZIP generations were byte-identical, PASS.
- Clean extraction matched the source tree byte-for-byte for all 786 files, PASS.
- Clean extraction reran the 2.1.15 targeted verifier, portrait/landscape state machine, negative mutations, immutable-predecessor regression, compile regression and complete release-specific static quality gate: PASS.
- Post-fix device replay and Android Gradle compilation remain separate unexecuted-to-PASS evidence classes and are not implied by packaging/static-gate PASS.

# VulkanScope 2.1.14 Build Audit

## Scope and immutable predecessor
- Immutable predecessor: VulkanScope 2.1.13, 776 files, ZIP SHA-256 `f8849bdda23cd3db39e2e8a6568070c0c11d0793e2c7b5b9248c316e03e50bd1`.
- Release identity: VulkanScope 2.1.14 (`versionCode 2114`).
- Production runtime changes are restricted to `app/src/main/java/com/efishell/vulkanscope/MainActivity.kt`; `app/build.gradle.kts` changes release identity only.
- Manifest, native/JNI collector, Vulkan 1.4.364 registry/generated data, reports/Database/export semantics, Turnip handling, permissions, endpoints, ABI targets, dependencies and packaged artwork remain predecessor-equivalent.

## Failing-before-fix runtime evidence
- The supplied 8.646-second 2.1.13 runtime video is the regression oracle: the pager remains visually detached during reverse return because the page overlay stays the visual owner even after the list position returns, and the pager-join blur differs from the app-header blur.
- Post-fix portrait/landscape device replay is not available in this environment and is not claimed PASS.

## Repair boundary
- Outside the bounded join region, the real `CollectionPager` is rendered inside its lazy-list item. Inside the join/pinned region, that item becomes a same-height spacer and the page overlay exclusively owns the pager. The two visible states are mutually exclusive.
- The measured pager height is retained by `VulkanLazyPage`, so lazy-item disposal/recomposition does not reset the landing slot to the 72 dp bootstrap estimate.
- Direct pager item offset remains authoritative. Immediate previous/next visible items bridge the narrow lazy-composition gap by reconstructing the pager offset from exact item offset, item size and configured vertical spacing.
- The pager overlay has no independent whole-card tween and follows scroll-derived geometry until the live header boundary clamps it.
- Pager join blur now uses the same 24 dp bounded RenderEffect radius and the same uniform `VulkanGlassTint` used by header/navigation chrome. The join glass reaches the moving pager bottom plus bounded padding while ordinary page content remains crisp.
- The coordinated header → pager → transient-status → top-scroll-indicator ordering and the existing smooth indicator inset animation are retained.

## Executed static/targeted evidence
- `tools/verify_release_2114.py`: PASS.
- `tools/test_release_2114_state_machine.py`: PASS for portrait/landscape single-visual ownership, reverse landing, adjacent-item bridge, retained height and overlay-stack geometry.
- `tools/test_release_2114_negative_mutations.py`: PASS, including documentation-only false-positive control.
- `tools/verify_release_2114_regression.py --predecessor /mnt/data/vulkanscope_2_1_13_inspect`: PASS.
- `tools/verify_compile_regressions.py`: PASS.
- Complete release-specific `tools/quality_gate.py`: PASS; strict canonical Vulkan header byte-level verification remains NOT EXECUTED because no local canonical header path was supplied.

## Android build evidence
- `./gradlew :app:compileReleaseKotlin --offline` was attempted in this environment.
- Gradle wrapper bootstrap attempted to resolve Gradle 9.7.1 from `services.gradle.org` and failed with `UnknownHostException` before project compilation.
- Android `:app:compileReleaseKotlin`, `:app:assembleRelease`, lint and post-fix device replay therefore remain NOT EXECUTED to a PASS result here.

## Final packaging evidence
- Strict sorted `files.txt` source census: 781 files, PASS.
- Two independent fixed-timestamp sorted-path deterministic ZIP generations were byte-identical, PASS.
- Clean extraction matched the source tree byte-for-byte for all 781 files, PASS.
- Clean extraction reran the 2.1.14 targeted verifier, portrait/landscape state machine, negative mutations, immutable-predecessor regression, compile regression and complete release-specific static quality gate: PASS.
- Post-fix device replay and Android Gradle compilation remain separate unexecuted-to-PASS evidence classes and are not implied by packaging/static-gate PASS.
