# VulkanScope 1.2.5 build / regression audit

## Release identity
- Version: 1.2.5.
- versionCode: 1205.
- Immutable predecessor: VulkanScope 1.2.4.
- Predecessor ZIP SHA-256: `6f4b69b8af3843781cf3334382de716df3798b12b257ecfaa0d82046e7401d92`.
- Predecessor package census: 514 files.
- Minimum Android platform: Android 12 / API 31.
- targetSdk: 37.
- Vulkan registry/query baseline: Vulkan 1.4.362 / header 362.
- Submission schema / technicalReport schema: 2 / 3.

## Demonstrated 1.2.4 UI regression
The 1.2.4 Settings root animates only the three destination cards when the chooser itself appears. Pressing the destination action updates `selectedSection`, after which `SettingsPage` directly returns Info, Reports & Database, or Driver & Update Preferences. Because the destination state itself is not animated, the chooser is replaced abruptly. This matches the reported real-device behavior.

## 1.2.5 correction
- `SettingsPage` now owns one bounded `AnimatedContent` state keyed by nullable `selectedSection`.
- The `AnimatedContent` lambda renders from its `targetSection` argument, keeping transition identity correct while both initial and target content may coexist during animation.
- Chooser -> destination uses a finite 280 ms short horizontal entrance plus 220 ms fade, paired with a 180 ms chooser exit slide and 150 ms fade.
- Destination -> chooser uses a finite reverse slide/fade.
- Peer destination replacement, if reached, uses bounded fade only.
- The existing 260 ms / 45 ms-staggered entrance of the three `ExpressiveDestinationCard` chooser cards remains unchanged.
- Driver & Update Preferences was extracted into `DriverUpdatePreferencesPage` so all three destination bodies participate in the same transition without changing driver, update-preference, storage or collection behavior.

## Immutable predecessor and release-specific evidence
- `tools/verify_release_1205.py --root <immutable 1.2.4> --skip-version`: expected FAIL and observed FAIL for missing Settings destination-state animation.
- `tools/verify_release_1205.py`: PASS.
- `tools/test_release_1205_state_machine.py`: PASS for all three forward destinations, reverse navigation and bounded peer fallback.
- `tools/test_release_1205_negative_mutations.py`: PASS, including target-state ownership, finite slide/fade timing, all three destination branches, stale identity and unrelated false-positive control.
- `tools/verify_regression_contracts.py`: PASS; baseline=1.2.4, successor=1.2.5, runtimeFiles=141, queryGroups=104, extensions=304, structs=110.
- Production runtime change from 1.2.4 is restricted by the golden contract to `MainActivity.kt`; `app/build.gradle.kts` changes release identity only.

## Retained-verifier maintenance
The requested transition refactor moved Driver & Update Preferences out of the body of `SettingsPage` without changing its semantics. Retained source verifiers were updated only where their old textual location assumptions became stale:
- The retained 1.2.2 Settings verifier accepts the 1.2.5 `AnimatedContent` destination structure while preserving the exact three-destination hierarchy, shared card requirement, switch-only hit targets, search/watch behavior, semantic artwork and update busy-state invariants.
- The retained 1.2.4 verifier is successor-aware while retaining History clear-all, chooser-card entrance motion and Share external-open artwork requirements.
- The retained 0.80.6 accessibility verifier inspects `DriverUpdatePreferencesPage` for the Direct GitHub Updates switch on 1.2.5+, preserving the requirement that only the Switch itself owns the toggle action.
- Corresponding retained negative-mutation suites remain active and pass.

## Current upstream and retained quality evidence
- The official Khronos Registry was rechecked on 2026-09-12 and remains Vulkan Registry 1.4.362 published 2026-09-04. The checked-in Vulkan 1.4.362/header 362 baseline remains pinned.
- Android Developers documents `AnimatedContent` as the state-driven composable for animating replacement content and requires the target-state lambda value to identify rendered content; the 1.2.5 implementation follows that model.
- Every applicable constituent command listed by `tools/quality_gate.py` was executed in bounded groups and passed. The monolithic command was also attempted but exceeded the execution window before completion; it is not labeled as one-command PASS.
- Retained coverage includes Vulkan specification/registry gates, compile-source guards, Material 3 Expressive, responsive/accessibility/TV/system-language, update/Turnip/driver/SAF/share suites, probe lifecycle/publication/timeout/cancellation, report/Surface/HTML, profile/Video, resource ceilings and release semantics.
- `tools/verify_registry_snapshot.py`: PASS; header 362, 476 registered extensions, 304 Android-queryable providers, 299 stable + 5 provisional.
- `tools/verify_concurrency_resource_contracts.py`: PASS; registryExtensions=476/4096, probeLimit=67108864, databaseLimit=2097152, analysisLimit=8388608.
- `tools/verify_release.py --skip-regression-contracts --skip-nested-verifiers`: PASS for 1.2.5 / 1205.
- Locked registry regeneration from the bundled 1.4.362 `vk.xml`: PASS and byte-identical to the checked-in generated manifest.
- Python verifier syntax and generated/regression JSON parsing: PASS.

## Security, correctness, memory and performance audit
- No permission, endpoint, network route, background upload, report field, native/JNI query, capability inference, storage root or dependency changes are introduced.
- Animation is finite and composition-local; no persistent worker, infinite transition, bitmap owner or unbounded collection is added.
- Settings back ownership is unchanged: Back returns from a nested Settings destination to the three-card chooser before leaving Settings.
- Existing Android 12 minimum, fixed HTTPS origins, complete-report gating, three release ABIs and privacy/resource ceilings remain unchanged.

## Android build and runtime evidence boundary
A successor compile attempt was made with `bash gradlew :app:compileReleaseKotlin --offline --no-daemon --stacktrace`. Gradle 9.7.1 is not cached in this environment; the wrapper attempted to resolve `services.gradle.org` and failed with `UnknownHostException` before Gradle/Android/Kotlin tasks could execute. Android compile/assemble/lint/unit are therefore NOT EXECUTED here and are not represented as PASS.

Real-device visual frame pacing, TalkBack/TV focus behavior, live Database/update flow, System/Turnip switching and sanitizer/profiler execution are also NOT EXECUTED here.

## Package gate
- `files.txt` is the exact sorted release census and strict hygiene list.
- `README.md`, `release.md`, fastlane paths, Python caches and `.pyc` files are forbidden from the package.
- Deterministic archives use the exact `files.txt` set, fixed 1980 ZIP timestamps, fixed regular-file attributes and deterministic deflate compression.
- Final release requires two independently generated byte-identical ZIPs, clean extraction, source/package path and byte equality, clean-extract current-release verification and an external SHA-256 record.
