# VulkanScope 3.0.12 Build Audit

## Scope and predecessor
- Immutable predecessor: VulkanScope 3.0.11, 851 files, ZIP SHA-256 `746a625d4108a8b9839393bb3b4d4c1e89880ec237a663a24748624a32a43da3`.
- Release identity: VulkanScope 3.0.12 (`versionCode 3012`).
- Runtime production change is restricted to `app/src/main/java/com/efishell/vulkanscope/MainActivity.kt`; Gradle changes release identity only.

## Compile repair
- The supplied `:app:assembleRelease` log fails at `MainActivity.kt:1142` because suspend `inspectTurnipArchive` was called inside a non-suspend Sequence/result lambda.
- Folder scanning now uses an imperative loop within the enclosing suspend function, preserving bounded validated Turnip counts and explicit cancellation propagation.

## Executed targeted/static evidence
- `tools/verify_release_3012.py`: PASS.
- `tools/test_release_3012_state_machine.py`: PASS.
- `tools/test_release_3012_negative_mutations.py`: PASS.
- `tools/verify_release_3012_regression.py --predecessor /mnt/data/vs3011`: PASS.
- Complete release-specific `tools/quality_gate.py`: PASS, including the 3.0.11 validated-folder-count state machine and compile/spec/report/concurrency regressions. Strict canonical upstream header byte verification was not executed because no local locked header path was supplied.

## Android build evidence
- `bash gradlew :app:compileReleaseKotlin --offline` was attempted in this environment.
- Gradle wrapper bootstrap attempted to download Gradle 9.7.1 from `services.gradle.org` and failed with `UnknownHostException` before project compilation.
- Android `:app:compileReleaseKotlin` / `:app:assembleRelease` therefore remain NOT EXECUTED to a PASS result here. The supplied Windows build log is the failing-before-fix oracle for the repaired suspend-call error.

## Final packaging evidence
- Strict sorted `files.txt` source census: 856 files.
- Two independent fixed-timestamp sorted-path deterministic ZIP generations were byte-identical, PASS.
- Clean extraction matched the source tree byte-for-byte for all 856 files and reran the 3.0.12 targeted verifier, state machine, negative mutations, immutable-predecessor regression and release-specific static quality gate: PASS.

# VulkanScope 3.0.11 build audit

- Immutable predecessor: `VulkanScope-3.0.10.zip`, SHA-256 `e10372fa362816ac5c718aafe8e5b9d04e1cda404d94b8ccfc9cc0065ff02ba2`, 846 files.
- Release identity: VulkanScope 3.0.11 (`versionCode 3011`).
- Reported regression reproduced from source inspection and user screenshots: Turnip File Manager folder subtitles counted every readable `.zip` filename, so `Download` could report `63 files` while only three archives passed Turnip package inspection and were actually shown.
- Production repair: folder summaries now count only direct child ZIP candidates accepted by `inspectTurnipArchive`; the existing 256-ZIP bound, imported-package visibility, cancellation and import-time revalidation are preserved.
- Production diff boundary: `MainActivity.kt` only; `app/build.gradle.kts` changes release identity only. Native/JNI Vulkan code, manifest, registry/generated data, report/export schemas, Database behavior, permissions, ABI targets, packaged assets and dependencies remain predecessor-equivalent.
- Targeted evidence: 3.0.11 verifier, 63-ZIP/3-valid state machine, negative mutations and immutable-predecessor regression boundary: PASS.
- Release-specific static quality gate including retained 3.0.10-3.0.3 state machines, compile/spec regressions, report/profile/video/concurrency checks and package-manifest hygiene: PASS.
- Android Gradle compile attempt could not start because Gradle 9.7.1 is absent locally and `services.gradle.org` is unreachable from this environment; no Android Gradle PASS is claimed.
- Replay against the user's real shared-storage tree remains a separate device evidence class and is not claimed by this static audit.

# VulkanScope 3.0.10 build audit

## Scope and immutable predecessor
- Immutable predecessor: VulkanScope 3.0.9, 841 files, ZIP SHA-256 `8af4a85286b9102004e43dc22495f7003cc16c96deff788354d2025e5750132c`.
- Release identity: VulkanScope 3.0.10 (`versionCode 3010`).
- Requested production scope: repair the user-reported `LocalContext` Kotlin compile failure and the File Manager landscape View & sort interaction failure.
- Production runtime changes are restricted to `MainActivity.kt`; `app/build.gradle.kts` changes release identity only. Native/JNI Vulkan collection, registry/generated data, report/export schemas, Database transport/endpoints/bounds, manifest, permissions, ABI targets, assets and dependencies remain predecessor-equivalent.

## Failing-before-fix evidence
- The supplied `:app:assembleRelease` log builds arm64-v8a, armeabi-v7a and x86_64 native targets and reaches `:app:compileReleaseKotlin`, where `MainActivity.kt:17773` fails with `Unresolved reference 'LocalContext'`.
- The predecessor Database submitted-time composable contains one unqualified `LocalContext.current` reference while the same source file consistently uses the fully qualified Compose platform reference elsewhere.
- The landscape shared-storage File Manager renders controls in a dedicated vertically scrollable side pane. The predecessor View & sort control opens an anchored `DropdownMenu` from inside that pane, creating an orientation-specific popup/anchor path absent from portrait and matching the reported landscape-only non-responsive behavior.

## Repair
- `DatabaseSubmittedAt` now uses `androidx.compose.ui.platform.LocalContext.current`, removing the unresolved symbol without changing parsing, date/time localization or fallback behavior.
- `FileManagerOptionsChooser` keeps the same toolbar trigger and choices but renders the choice surface in a bounded `Dialog` independent of the landscape controls pane. It respects status/navigation insets, supports pointer and D-pad scrolling, preserves all six layout modes/all sort modes, and keeps X-only dismissal by disabling Back/outside-tap dismissal.
- Because Turnip and shared-storage import/export all call the same chooser, the landscape repair applies to every File Manager workflow without divergent implementations.

## Executed evidence
- `tools/verify_release_3010.py`: PASS.
- `tools/test_release_3010_state_machine.py`: PASS.
- `tools/test_release_3010_negative_mutations.py`: PASS, including unresolved-context, anchored-dropdown, outside-dismissal, D-pad-scroll and protected-native mutations plus documentation-only false-positive control.
- `tools/verify_release_3010_regression.py --predecessor <immutable 3.0.9 extraction>`: PASS.
- `tools/verify_compile_regressions.py`: PASS.
- `tools/verify_spec_regressions.py`: PASS.
- Complete release-specific `tools/quality_gate.py`: PASS, including 3.0.9 through 3.0.3 state-machine regression coverage, registry locks, report/profile/video contracts, concurrency/resource contracts and package hygiene.
- Strict canonical Vulkan-header byte-level verification remains NOT EXECUTED because no local canonical header path was supplied.

## Android build/runtime evidence
- `TERM=xterm bash gradlew :app:compileReleaseKotlin --offline --no-daemon` was attempted after the repair.
- The pinned Gradle 9.7.1 distribution is not locally cached; wrapper bootstrap attempts `services.gradle.org` and fails with `UnknownHostException` before Gradle project compilation begins.
- Post-fix Android Kotlin compilation, `:app:assembleRelease`, lint and real landscape pointer/remote replay therefore remain NOT EXECUTED to a PASS result in this artifact environment. The user-supplied build log is the failing-before-fix oracle; static/source gates are not represented as a successful Android build.

## Packaging evidence
- Strict sorted source census: 846 files, PASS.
- Two independent fixed-timestamp sorted-path candidate ZIP generations were byte-identical, PASS.
- Candidate clean extraction matched all 846 source files byte-for-byte, PASS.
- Candidate clean extraction reran the 3.0.10 verifier, state machine, negative mutations, immutable-predecessor regression and complete release-specific static quality gate: PASS.
- Final delivery is regenerated from this frozen audit and receives the same deterministic identity, clean-extract byte equality and clean-tree targeted/static verification before delivery.

# VulkanScope 3.0.9 build audit

## Scope and immutable predecessor
- Immutable predecessor: VulkanScope 3.0.8, 836 files, ZIP SHA-256 `8a6db5123add8f261edfd1aad0a15f528a300dd562f9ce2b3540c84fc493e347`.
- Release identity: VulkanScope 3.0.9 (`versionCode 3009`).
- Production runtime change is restricted to `app/src/main/java/com/efishell/vulkanscope/MainActivity.kt`; `app/build.gradle.kts` changes release identity only.
- Native/JNI Vulkan collection, `AdvancedAnalysis.kt`, `VulkanProbeService.kt`, `VulkanDependencyGraph.kt`, registry/generated data, report/export schemas, Database API routes/request bounds, manifest, permissions, ABI targets and dependency/build-chain pins remain predecessor-equivalent.

## Requested repair boundary
- Database public-report cards parse the exact server `submitted_at` ISO instant and present it as the Android device-local medium date plus the user's selected time format, with an explicit local time-zone label. Parse failure falls back to the original bounded string instead of fabricating a date.
- Database driver-mode text uses the same Overview identity rule: Turnip is `VulkanAccentSoft`, System/other is `VulkanTextPrimary`, and both are bold. The underlying `driver_mode` value remains unchanged.
- Encyclopedia, Requirements, Minimums, Graph, Quality and Tests section headers now use unique semantic visible treatments. Reused semantic roots are differentiated by meaningful lower-right/internal badges or overlays rather than arbitrary decoration.
- The permanent engineering rules now require semantic icon uniqueness within the same destination/tab/dialog/simultaneously visible action group.

## Executed static/targeted evidence
- `tools/verify_release_3009.py`: PASS.
- `tools/test_release_3009_state_machine.py`: PASS.
- `tools/test_release_3009_negative_mutations.py`: PASS, including raw-timestamp, driver-color, duplicate-icon, generic-info, permanent-rule deletion and protected-native mutations plus documentation-only false-positive control.
- `tools/verify_release_3009_regression.py --predecessor <immutable 3.0.8 extraction>`: PASS.
- 3.0.8, 3.0.7, 3.0.6, 3.0.5, 3.0.4 and 3.0.3 state-machine regressions: PASS through the release-specific quality gate.
- CMake registry lock, bundled registry snapshot, compile/spec regression, probe lifecycle/terminal/timeout/cancellation, report/surface/profile/video/concurrency/resource and package-hygiene contracts: PASS through `tools/quality_gate.py`.
- Strict canonical Vulkan-header byte-level verification remains NOT EXECUTED because no local canonical header path was supplied.

## Android build/runtime evidence
- `bash gradlew :app:compileReleaseKotlin --offline --no-daemon` was attempted after the 3.0.9 changes.
- The pinned Gradle 9.7.1 distribution is not locally cached; wrapper bootstrap attempts `services.gradle.org` and fails with `UnknownHostException` before Gradle project compilation begins.
- Android Kotlin compilation, `:app:assembleRelease`, lint and post-fix device visual/time-zone replay therefore remain NOT EXECUTED to a PASS result in this environment.

## Candidate packaging evidence
- Strict sorted `files.txt` source census: 841 files, PASS.
- Deterministic candidate ZIP clean extraction matched the source tree byte-for-byte for all 841 files, PASS.
- Candidate clean extraction reran the 3.0.9 verifier, state machine, negative mutations, immutable-predecessor regression and complete release-specific static quality gate: PASS.
- Final delivery is regenerated deterministically from this frozen tree and is rechecked after extraction before delivery.

# VulkanScope 3.0.8 build audit

## Scope and immutable predecessor
- Immutable predecessor: VulkanScope 3.0.7, 831 files, ZIP SHA-256 `df0775953977837dc7e6a004e275d99e39f89170b990f70b9a72928dd63372a0`.
- Release identity: VulkanScope 3.0.8 (`versionCode 3008`).
- Requested production scope: hardware D-pad/arrow navigation, Analysis icons, common statistics presentation, Requirements/Minimums/Encyclopedia usability, Database GPU badge presentation and file-manager navigation/count motion.
- Native/JNI Vulkan collection, `AdvancedAnalysis.kt`, `VulkanDependencyGraph.kt`, registry/generated data, report/export schemas, Database routes/bounds, manifest, permissions, ABI targets and dependencies remain predecessor-equivalent.

## Supplied screenshot/video evidence and repair boundary
- The supplied Properties screenshot shows the statistic summary as a long stack of neutral cards. The shared metric components now use the Vulkan-accent summary language and a compact responsive grid, so Properties and other metric-based pages receive the same presentation without page-specific duplication.
- The supplied 16.4-second Android Studio/device-mirror video shows arrow/remote attempts without expected focus/scroll progression. The immutable 3.0.7 source gated lazy-list, lazy-grid, primary-page and focus-card D-pad behavior on `UI_MODE_TYPE_TELEVISION`; 3.0.8 removes that form-factor gate and treats Android D-pad keycodes as the capability signal.
- Focus movement remains first choice. Lists/grids scroll only when a next semantic focus target is outside the current composed viewport; ScrollState-backed detail/dialog surfaces use bounded animated fallback scrolling.
- Requirements, Minimums and Encyclopedia are presentation/evidence-clarity redesigns only. Their underlying evaluators, registry baseline, custom-rule bounds and capability semantics are unchanged.
- Database list rows reuse the exact System/Turnip vendor badge component and still derive identity only from explicit `vendor_id`.
- File-manager directory, breadcrumb and selected/remaining transitions are visual only and leave filesystem/ZIP/import/export safety behavior unchanged.

## Executed static/targeted evidence
- Kotlin PSI syntax parse of `MainActivity.kt`, `AdvancedAnalysis.kt` and `VulkanDependencyGraph.kt`: PASS.
- `tools/verify_release_3008.py`: PASS.
- `tools/test_release_3008_state_machine.py`: PASS.
- `tools/test_release_3008_negative_mutations.py`: PASS, including television-gate, semantic-icon, Database-badge, metric-style, Requirements, directory-motion, Encyclopedia and protected-native mutations plus documentation-only false-positive control.
- `tools/verify_release_3008_regression.py --predecessor <immutable 3.0.7 extraction>`: PASS.
- 3.0.7, 3.0.6, 3.0.5, 3.0.4 and 3.0.3 state-machine regressions: PASS.
- CMake registry lock, bundled registry snapshot, compile/spec regression, probe lifecycle/terminal/timeout/cancellation, report/surface/profile/video/concurrency/resource contracts: PASS through the complete release-specific `tools/quality_gate.py`.
- Strict canonical Vulkan-header byte-level verification remains NOT EXECUTED because no local canonical header path was supplied.

## Android build/runtime evidence
- `./gradlew :app:compileReleaseKotlin --offline --no-daemon` was attempted after the 3.0.8 changes.
- The pinned Gradle 9.7.1 distribution is not locally cached; wrapper bootstrap attempts `services.gradle.org` and fails with `UnknownHostException` before Gradle project compilation begins.
- Android Kotlin compilation, `:app:assembleRelease`, lint and post-fix device/remote visual replay therefore remain NOT EXECUTED to a PASS result in this artifact environment. Static/source gates do not imply those runtime/build results.

## Final packaging evidence
- Strict sorted `files.txt` source census: 836 files, PASS.
- Two independent fixed-timestamp sorted-path deterministic candidate ZIP generations were byte-identical, PASS.
- Candidate clean extraction matched the source tree byte-for-byte for all 836 files, PASS.
- Candidate clean extraction reran the 3.0.8 verifier, state-machine test, negative mutations, immutable-predecessor regression and complete release-specific static quality gate: PASS.
- The final deliverable is regenerated from this frozen audit and receives the same deterministic identity, clean-extract byte-equality and clean-tree targeted/static verification before delivery.

# VulkanScope 3.0.7 build audit

## Scope and immutable predecessor
- Immutable predecessor: VulkanScope 3.0.6, 826 files, ZIP SHA-256 `e2dac49614bad04e8f73d3e92f44f2f2683bd7e65b049f855ad3e2dd5769e070`.
- Requested production scope: Database-list vendor artwork, file-manager search/layout motion, Quality transparency and active self-test result presentation.
- Native/JNI Vulkan collection, registry/generated data, report/export schemas, Database transport/endpoints, manifest, permissions, ABI targets and dependencies are predecessor-equivalent.

## Requested evidence
- Database report cards resolve explicit `vendor_id` through the established GPU vendor asset mapping and retain unknown-vendor fallback.
- Turnip and shared-storage search toolbars use bounded horizontal/size animation; file/folder cards use bounded content-size animation.
- Quality now exposes the 100-point baseline, fixed deductions, thresholds, trigger evidence and explicit non-ranking semantics.
- Self-test results expose target/scope, result counts, overall status and per-test returned Vulkan details without altering capability support.

## Executed static/targeted evidence
- `tools/verify_release_3007.py`: PASS.
- `tools/test_release_3007_state_machine.py`: PASS.
- `tools/test_release_3007_negative_mutations.py`: PASS, including Database-icon fallback, search-motion and Quality/self-test evidence mutations plus documentation false-positive control.
- `tools/verify_release_3007_regression.py --predecessor <immutable 3.0.6 extraction>`: PASS.
- `tools/verify_compile_regressions.py`, `tools/verify_spec_regressions.py`, CMake registry lock and bundled registry snapshot verification: PASS.
- Complete release-specific `tools/quality_gate.py`: PASS; strict canonical Vulkan-header byte-level verification remains NOT EXECUTED because no local canonical header path was supplied.

## Android build evidence
- `TERM=xterm ./gradlew :app:compileReleaseKotlin --offline --stacktrace` was attempted after the 3.0.7 changes.
- The pinned Gradle 9.7.1 distribution is not locally cached and wrapper bootstrap still attempts `services.gradle.org`, which fails with `UnknownHostException` before Gradle project compilation begins.
- Android Kotlin compilation, `:app:assembleRelease`, lint and device/TV runtime/visual replay therefore remain NOT EXECUTED to a PASS result in this artifact environment.

## Final packaging evidence
- Strict sorted `files.txt` source census: 831 files, PASS.
- Two independent fixed-timestamp sorted-path deterministic candidate ZIP builds were byte-identical, PASS.
- Candidate clean extraction matched the source tree byte-for-byte for all 831 files, PASS.
- Candidate clean extraction reran the 3.0.7 verifier, state-machine test, negative mutations, immutable-predecessor regression and complete release-specific static quality gate: PASS.
- The delivered ZIP is regenerated from this frozen audit and receives the same deterministic identity, clean-extract and clean-tree gate checks before delivery.

# VulkanScope 3.0.6 Build Audit

## Scope and immutable predecessor
- Immutable predecessor: VulkanScope 3.0.5, 821 files, ZIP SHA-256 `5a98bc562757d74ca438caabf94954c09428a17407ff0279c164dc39d54dc78c`.
- Release identity: VulkanScope 3.0.6 (`versionCode 3006`).
- Production runtime-source changes are restricted to `MainActivity.kt`; `app/build.gradle.kts` changes release identity only.
- `AdvancedAnalysis.kt`, `VulkanDependencyGraph.kt`, manifest, native/JNI Vulkan collection, Vulkan registry/generated assets, report/export schemas, permissions, ABI targets and dependency versions remain predecessor-equivalent.

## Failing-before-fix evidence and upstream check
- The user-supplied `:app:assembleRelease` log completes C/C++ compilation for arm64-v8a, armeabi-v7a and x86_64, then fails `:app:compileReleaseKotlin` at the Analysis lazy builder.
- Kotlin reports that the non-composable `LazyListScope.analysisWorkspaceItems` invokes a composable function and points to `LocalValidatedNetwork.current` inside the Database branch.
- Official Android Compose documentation was checked on 2026-09-29: the `LazyListScope.()` block is the lazy-list DSL, while `item` content is composable. The validated-network CompositionLocal read therefore belongs in an actual composable scope.

## Repair boundary
- `AnalysisPage` reads `LocalValidatedNetwork.current` in its existing `@Composable` scope and passes the resulting Boolean into `analysisWorkspaceItems`.
- `analysisWorkspaceItems` is still a normal `LazyListScope` builder and no longer invokes the CompositionLocal.
- Database list and exact Report ID controls retain the same validated-network gating, endpoints, explicit-action behavior and response bounds from 3.0.5.

## Executed static/targeted evidence
- `tools/verify_release_3006.py`: PASS.
- `tools/test_release_3006_state_machine.py`: PASS.
- `tools/test_release_3006_negative_mutations.py`: PASS, including reintroduction of the non-composable CompositionLocal read, forced-network bypass rejection and documentation-only false-positive control.
- `tools/verify_release_3006_regression.py --predecessor <immutable 3.0.5 extraction>`: PASS.
- `tools/test_release_3005_state_machine.py`: PASS.
- `tools/test_release_3004_state_machine.py`: PASS.
- `tools/test_release_3003_state_machine.py`: PASS.
- `tools/verify_compile_regressions.py`: PASS.
- `tools/verify_spec_regressions.py`: PASS.
- Complete release-specific `tools/quality_gate.py`: PASS; strict canonical Vulkan-header byte-level verification remains NOT EXECUTED because no local canonical header path was supplied.

## Android build evidence
- `TERM=xterm bash ./gradlew :app:compileReleaseKotlin --offline --stacktrace` was attempted after the repair.
- The pinned Gradle 9.7.1 distribution is not locally cached and wrapper bootstrap attempts `services.gradle.org`, which fails with `UnknownHostException` before Gradle project compilation begins.
- Post-fix Android Kotlin compilation, `:app:assembleRelease`, lint and device/TV runtime replay therefore remain NOT EXECUTED to a PASS result in this artifact environment.

## Final packaging evidence
- Strict sorted `files.txt` source census: 826 files, PASS.
- Two independent fixed-timestamp sorted-path deterministic candidate ZIP builds were byte-identical, PASS.
- Candidate clean extraction matched the source tree byte-for-byte for all 826 files, PASS.
- Candidate clean extraction reran the 3.0.6 verifier, state-machine test, negative mutations, immutable-predecessor regression, compile/spec guards and complete release-specific static quality gate: PASS.
- Final packaging is regenerated from this frozen audit and receives the same deterministic identity, byte-equality and clean-extract gate checks before delivery.

# VulkanScope 3.0.5 Build Audit

## Scope and immutable predecessor
- Immutable predecessor: VulkanScope 3.0.4, 816 files, ZIP SHA-256 `9be183e6863cbdfcea543faafa49404369f0cb297bfccd50b582d1adaec65c74`.
- Release identity: VulkanScope 3.0.5 (`versionCode 3005`).
- Production runtime-source changes are restricted to `MainActivity.kt` and `AdvancedAnalysis.kt`; `app/build.gradle.kts` changes release identity only.
- Manifest, native/JNI Vulkan collection, `VulkanDependencyGraph.kt`, Vulkan registry/generated assets, report/export schemas, permissions, ABI targets and dependency versions remain predecessor-equivalent.

## Implemented scope
- Turnip File Manager and every shared-storage Analysis/Reports import/export browser use independent full-screen rectangular surfaces with true AMOLED black application background, no enclosing manager border, retained system-navigation insets/backdrop and bounded fade/slide open/close motion.
- The file-manager toolbar places Search immediately to the right of the unified View & sort control; expanding search consumes remaining toolbar width rather than a separate row. Turnip and shared-storage folder/file cards use the Vulkan-red outline family.
- Diagnostic collection is reorganized into complete, phase, probe and scheduler-wait evidence; every displayed elapsed duration uses seconds with the exact millisecond value in parentheses.
- Database lookup is split into bounded public report-list and exact Report ID modes. Public list requests are explicit user actions, use only `GET /v1/reports` on the existing fixed HTTPS Database host, request at most 50 rows, use the documented cursor and retain at most 200 unique rows locally.

## Executed static/targeted evidence
- `tools/verify_release_3005.py`: PASS.
- `tools/test_release_3005_state_machine.py`: PASS.
- `tools/test_release_3005_negative_mutations.py`: PASS, including protected-file mutation rejection and documentation-only false-positive control.
- `tools/verify_release_3005_regression.py --predecessor <immutable 3.0.4 extraction>`: PASS.
- `tools/test_release_3004_state_machine.py`: PASS, retaining the corrected TV key receiver contract.
- `tools/test_release_3003_state_machine.py`: PASS, retaining minimum/profile/file-manager/TV/copy behavior not superseded by 3.0.5.
- `tools/verify_compile_regressions.py`: PASS.
- `tools/verify_spec_regressions.py`: PASS.
- Complete release-specific `tools/quality_gate.py`: PASS; strict canonical Vulkan-header byte-level verification remains NOT EXECUTED because no local canonical header path was supplied.

## Android build evidence
- `bash gradlew :app:compileReleaseKotlin --offline --stacktrace` was attempted in the artifact environment. The pinned Gradle 9.7.1 distribution is not locally cached and wrapper bootstrap attempts `services.gradle.org`, which fails with `UnknownHostException` before Gradle project compilation begins.
- Android Kotlin compilation, `:app:assembleRelease`, lint and device/TV runtime/visual replay therefore remain NOT EXECUTED to a PASS result in this environment. They are not represented as static-gate success.

## Final packaging evidence
- Strict sorted `files.txt` source census: 821 files, PASS.
- Two independent fixed-timestamp sorted-path deterministic candidate ZIP builds were byte-identical, PASS.
- Candidate clean extraction matched the source tree byte-for-byte for all 821 files, PASS.
- Candidate clean extraction reran the 3.0.5 verifier, state-machine test, negative mutations, immutable-predecessor regression, compile/spec guards and complete release-specific static quality gate: PASS.
- Final packaging is regenerated from this frozen audit and receives the same deterministic identity, byte-equality and clean-extract gate checks before delivery.

# VulkanScope 3.0.4 Build Audit

## Scope and immutable predecessor
- Immutable predecessor: VulkanScope 3.0.3, 811 files, ZIP SHA-256 `e81ec55e27168fa576d43bb4ed77b1aabcdde25ea873b20f58bf3322851b53e2`.
- Release identity: VulkanScope 3.0.4 (`versionCode 3004`).
- Production runtime-source changes are restricted to six keycode receiver repairs in `MainActivity.kt` and removal of one invalid import in `VulkanDependencyGraph.kt`; `app/build.gradle.kts` changes release identity only.
- Manifest, native/JNI collection, Vulkan registry/generated assets, report/Database/export schemas and endpoints, permissions, ABI targets and dependency versions remain predecessor-equivalent.

## Failing-before-fix evidence and upstream check
- The user-supplied `:app:assembleRelease` log completes all three required native ABI builds and reaches `:app:compileReleaseKotlin`.
- Kotlin reports six `MainActivity.kt` receiver-type errors for `nativeKeyCode` and one `VulkanDependencyGraph.kt` error because the imported `weight` symbol is internal.
- Official Android Compose API documentation was checked on 2026-09-29: `Key.nativeKeyCode` is the native-code extension, `KeyEvent.key` supplies that `Key`, and row `Modifier.weight` is a `RowScope` extension.

## Repair boundary
- The six affected TV-navigation reads use `event.key.nativeKeyCode`; all D-pad/Page Up/Page Down mappings and focus/scroll behavior remain byte-identical around the receiver expression.
- The explicit `androidx.compose.foundation.layout.weight` import is removed; the three existing `Modifier.weight(1f)` calls remain unchanged inside the `Row`.
- No Vulkan/native/report/Database/file-manager/analysis behavior is rewritten.

## Executed static/targeted evidence
- `tools/verify_release_3004.py`: PASS.
- `tools/test_release_3004_state_machine.py`: PASS.
- `tools/test_release_3004_negative_mutations.py`: PASS, including both observed compile-regression mutations and the documentation-only false-positive control.
- `tools/verify_release_3004_regression.py --predecessor <immutable 3.0.3 extraction>`: PASS.
- `tools/test_release_3003_state_machine.py`: PASS, retaining the preceding Analysis/file-manager/TV behavior contracts.
- `tools/verify_compile_regressions.py`: PASS.
- `tools/verify_spec_regressions.py`: PASS.
- Complete release-specific `tools/quality_gate.py`: PASS; strict canonical Vulkan header byte-level verification remains NOT EXECUTED because no local canonical header path was supplied.

## Android build evidence
- The predecessor failing `:app:assembleRelease` build is externally reproduced by the supplied log after all three native ABI builds complete.
- `TERM=xterm bash ./gradlew :app:compileReleaseKotlin --offline` was attempted after the repair. The pinned Gradle 9.7.1 distribution is not locally cached and wrapper bootstrap fails at `services.gradle.org` with `UnknownHostException` before project compilation begins.
- Post-fix Android Kotlin compilation, `:app:assembleRelease`, lint and device/TV runtime replay therefore remain NOT EXECUTED to a PASS result in this environment.

## Final packaging evidence
- Strict sorted `files.txt` source census: 816 files, PASS.
- Two independent fixed-timestamp sorted-path deterministic candidate ZIP builds were byte-identical, PASS.
- Candidate clean extraction matched the source tree byte-for-byte for all 816 files, PASS.
- Candidate clean extraction reran the 3.0.4 verifier, state-machine test, negative mutations, immutable-predecessor regression and complete release-specific static quality gate: PASS.
- Final packaging is regenerated from this frozen audit and receives the same clean-extraction byte/gate checks before delivery.

# VulkanScope 3.0.3 Build Audit

## Scope and immutable predecessor
- Immutable predecessor: VulkanScope 3.0.2, 806 files, ZIP SHA-256 `89ea5989b77dacc7a54f17978d687068140d5d62829edc466799337290517430`.
- Release identity: VulkanScope 3.0.3 (`versionCode 3003`).
- Production runtime changes are restricted to `MainActivity.kt` and `VulkanDependencyGraph.kt`; `app/build.gradle.kts` changes release identity only.
- Manifest, native/JNI collection, Vulkan registry/generated assets, report/Database/export schemas and endpoints, permissions, ABI targets and dependency versions remain byte-equivalent to 3.0.2.

## Implemented scope
- Analysis minimum Save/Load/Delete now use explicit question-style confirmations; Load/Delete are contained Vulkan-red actions with update/trash icons and Cancel does not mutate local minimum state.
- Dependency Graph is rebuilt as a bounded two-axis explorer with depth lanes, relationship edges, evidence-state legend/metrics and the complete bounded traversal retained below the visual map.
- Turnip package browsing is a full-screen application overlay with clickable `>` breadcrumbs, animated round search expansion, bounded one-level ZIP counts and X-only view/sort dismissal.
- Android TV lazy pages and file-manager lists/grids use focus-first D-pad navigation with bounded scroll/retry fallback plus Page Up/Page Down handling; redundant chevron focus is suppressed when the parent card owns the action.
- Successful Database report-ID copy shows animated green-check feedback for 3000 ms and then restores the normal copy icon.

## Executed static/targeted evidence
- `tools/verify_release_3003.py`: PASS.
- `tools/test_release_3003_state_machine.py`: PASS.
- `tools/test_release_3003_negative_mutations.py`: PASS, including protected-file mutation rejection and documentation-only false-positive control.
- `tools/verify_release_3003_regression.py --predecessor /mnt/data/vs302`: PASS.
- `tools/verify_spec_regressions.py`: PASS.
- `tools/verify_compile_regressions.py`: PASS.
- `tools/quality_gate.py` has no registered 3.0.3 successor contract and therefore is not recorded as PASS for this release.

## Android build evidence
- Gradle wrapper bootstrap was attempted with the project-pinned Gradle 9.7.1 distribution.
- The distribution was not locally cached and `services.gradle.org` could not be resolved in this isolated environment (`UnknownHostException`) before project compilation began.
- Android `:app:compileReleaseKotlin`, `:app:assembleRelease`, lint and post-fix phone/landscape/TV runtime replay are therefore not claimed as PASS here.

## Final packaging evidence
- Strict sorted `files.txt` source census: 811 files, PASS.
- Two independent fixed-timestamp sorted-path ZIP generations are byte-identical, PASS.
- Clean extraction matches the source tree byte-for-byte for all 811 files, PASS.
- Clean extraction reruns the 3.0.3 targeted verifier, state-machine tests, negative mutations, immutable-predecessor regression and Vulkan specification regression gate: PASS.

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
