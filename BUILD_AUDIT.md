# VulkanScope 0.80.15 build / regression audit

## Release identity
- Version: 0.80.15
- versionCode: 815
- Immutable predecessor: VulkanScope 0.80.14
- Predecessor ZIP SHA-256: `fdfeb5937bc51264854f224ad43fc0571430f398932b876fbce1fd688610b315`
- Predecessor census: 333 files
- Vulkan registry/header baseline: Vulkan 1.4.362 / header 362
- Companion Database: 0.39.27

## Demonstrated presentation regressions
The user-supplied real-device 0.80.14 screenshots demonstrate three concrete presentation/usability defects: the permanent right-side scroll-indicator lane narrows content, Overview Explore controls clip horizontally at phone width, and the flat Extension detail evidence list loses the compact grouped hierarchy preferred by the user.

## 0.80.15 repair
Production runtime changes are limited to `MainActivity.kt` presentation/scroll behavior. Shared scroll hints no longer reserve width. Up is a transient top-center overlay and Down is a transient bottom-center overlay; at absolute top only Down is eligible, in the middle both are eligible, at absolute bottom only Up is eligible, and non-scrollable content shows neither. The 24dp decorative arrows fade after a 900ms idle interval and reappear when gesture, fling or programmatic scrolling resumes.

Overview Explore and Quick access now use width-aware wrapping grids. Extension/Format detail dialogs retain a bounded custom Material 3 surface and sticky Close action while restoring compact grouped tonal evidence cards with responsive stacking for narrow, large-text and long-string cases. The user-requested overlay indicators are allowed to sit over content and do not create a permanent side lane.

Vulkan/native/registry/generated/report/Database/security/privacy/resource behavior is unchanged from 0.80.14.

## Failing-before-fix and targeted evidence
- `tools/verify_responsive_overlay_ui_0815.py --root <immutable-0.80.14> --skip-version`: expected FAIL and observed FAIL for the old side lane, old indicator placement/geometry, clipped Explore strip, fixed Quick access layout and flat detail presentation.
- `tools/verify_responsive_overlay_ui_0815.py`: PASS.
- `tools/test_responsive_overlay_ui_0815_state_machine.py`: PASS for top/middle/bottom/non-scrollable direction and top/bottom viewport placement, idle disappearance/resumed-scrolling reappearance, responsive dialog rows and Overview grid columns.
- `tools/test_responsive_overlay_ui_0815_negative_mutations.py`: PASS, including side-lane restoration, side-position restoration, scroll-activity removal, idle-delay removal, smaller-arrow, flat-detail, loose-stacking, clipped-Explore, fixed-Quick-access and stale-version mutations plus an unrelated wording false-positive control.
- `tools/verify_regression_contracts.py`: PASS for immutable 0.80.14 -> 0.80.15 with 87 locked runtime/build-chain paths; only `MainActivity.kt` is allowlisted for production behavior change plus version metadata.

## Retained evidence
The following retained source/state contracts were rerun and pass on 0.80.15:
- 0.80.13 Material 3 Expressive UI coherence and responsive-layout state model.
- 0.80.14 PaddingValues compile-regression verifier and negative mutation suite.
- 0.80.6 TalkBack/large-text/display-size verifier, state model and negative mutations, updated only to recognize the stricter 0.80.15 adaptive Quick access and shared decorative arrow implementation.
- 0.80.7 locale/bidi/system-font/update-info source and state model.
- 0.80.8 release contract and terminal-state model.
- 0.80.9 Details-button-only source and state model.
- 0.80.4 detail/modal/TV/collection source and modal scroll state model.
- 0.80.5 update release-note scrolling/TV source and state model.
- 0.41.46 UI information architecture and negative mutation suite.
- 0.80.0 full-hardening source/state contract.
- Vulkan 1.4.362 targeted registry contract: 304 Android-queryable providers, 110 implemented structs, 104 query groups, Encyclopedia 842 commands / 6248 VK_* / 2461 Vk* / 476 extensions.
- CMake/header lock, compile-source regression guards, registry snapshot and concurrency/resource ceilings.
- `tools/verify_release.py --skip-regression-contracts --skip-nested-verifiers`: PASS for 0.80.15 / 815.

The aggregate `tools/quality_gate.py` was attempted, but the single long-running process exceeded the execution environment time limit after passing its early retained gates. It is not labeled as a one-command PASS; applicable constituent gates above were executed separately.

## Android compiler evidence boundary
`bash gradlew :app:compileReleaseKotlin --offline --no-daemon --stacktrace` was attempted. The Gradle wrapper tried to obtain Gradle 9.7.1 and failed before Gradle/Android/Kotlin task execution with `UnknownHostException: services.gradle.org` because the distribution is not cached in this environment. Therefore Android compile/lint/unit are NOT EXECUTED here. This is an environment bootstrap failure, not a demonstrated Kotlin compilation failure.

Real-device 0.80.15 portrait/landscape rendering, all OEM DPI/font-scale combinations, TalkBack traversal and Android TV D-pad validation remain separate runtime evidence classes and are NOT EXECUTED in this packaging environment.
