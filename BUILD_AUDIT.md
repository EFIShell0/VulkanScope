# VulkanScope 1.0.19 build / regression audit

## Release identity
- Version: 1.0.19.
- versionCode: 1019.
- Immutable predecessor: VulkanScope 1.0.18.
- Predecessor ZIP SHA-256: `3c79a6ed0041b3ec966dab4431acacc5ed7a4f9220ee9bd3c5706deeb5f4e065`.
- Predecessor package census: 475 files.
- Vulkan baseline: 1.4.362 / header 362.
- Companion Database: VulkanScope Database 1.0.8.
- Submission schema / technicalReport schema / Database normalizer: 2 / 3 / 16.

## Requested UI refinement
- `Check for updates`, the Direct GitHub Updates section artwork and direct-update source/status artwork now use one local simple download glyph: downward arrow plus horizontal baseline. Turnip `Import driver ZIP` deliberately retains the existing ZIP-folder/download glyph.
- The shared horizontal filter carousel keeps its existing geometry, state, animation, scrolling and RTL behavior; only its left/right round button colors change to VulkanScope red containers with white chevrons. Vertical up/down scroll-boundary indicators are unchanged.
- `Queue query safety` keeps the queue/list glyph and adds a lower-right shield badge.
- `Export complete report` uses the existing Surface glyph with the existing export-arrow artwork at lower-right.
- `Export TXT` and `Export HTML` retain their existing primary glyphs and add compact lower-right `TXT` / `HTML` badges.
- `Explore` now uses a local compass glyph; Quick access retains its existing home glyph.
- No runtime image generation or network-loaded artwork is introduced.

## Immutable predecessor / production scope
- `tests/golden/1.0.18_regression_contract.json`: PASS.
- `tools/verify_regression_contracts.py`: PASS; baseline=1.0.18, successor=1.0.19, runtimeFiles=129, queryGroups=104, extensions=304, structs=110.
- Runtime production changes are restricted to `app/src/main/java/com/efishell/vulkanscope/MainActivity.kt` plus new local vectors `ic_download.xml`, `ic_compass.xml` and `ic_shield.xml`; `app/build.gradle.kts` changes release identity only.
- Native/JNI Vulkan collection, Android manifest, generated registry/query data, existing packaged resources, report serialization and fixed endpoint constants remain predecessor bytes.

## Specification, correctness, security and resource evidence
- The checked-in canonical baseline remains Vulkan 1.4.362 / header 362. Current Khronos registry evidence was rechecked on 2026-09-09 and remains 1.4.362 published 2026-09-04.
- Unknown/unavailable/unsupported/not-applicable evidence semantics, report completeness and physical-device/runtime-query behavior are unchanged.
- Cleartext traffic and application backup remain disabled; the Vulkan probe service and FileProvider remain non-exported.
- Database and update origins remain fixed HTTPS roots; no user-editable endpoint, broad storage permission, WebView bridge, background Database upload or automatic APK install path is introduced.
- The UI-only patch adds local vector resources and short-lived Compose presentation nodes; it introduces no native handle owner, repeated Vulkan collection during recomposition, new worker/thread, persistent cache, or unbounded allocation.
- `tools/verify_compile_regressions.py`: PASS.
- `tools/verify_spec_regressions.py`: PASS.
- `tools/verify_concurrency_resource_contracts.py`: PASS; registryExtensions=476/4096, probeLimit=67108864, databaseLimit=2097152, analysisLimit=8388608.

## Current-release quality evidence
Executed on the final source tree:
- `tools/verify_semantic_ui_refinement_1019.py`: PASS.
- `tools/test_semantic_ui_refinement_1019_state_machine.py`: PASS.
- `tools/test_semantic_ui_refinement_1019_negative_mutations.py`: PASS; 12 targeted defects rejected plus unrelated false-positive control accepted.
- `tools/verify_regression_contracts.py`: PASS.
- `tools/verify_compile_regressions.py`: PASS.
- `tools/verify_spec_regressions.py`: PASS.
- `tools/verify_concurrency_resource_contracts.py`: PASS.
- `tools/verify_release.py --skip-nested-verifiers`: PASS; version=1.0.19, code=1019, Vulkan 1.4.362 and locked Vulkan-Headers commit retained.
- `tools/verify_package_reproducibility.py`: PASS on source manifest/hygiene.

The aggregate historical `tools/quality_gate.py` is not labeled PASS in this container. Its complete historical mutation chain exceeds the available execution window and includes presentation-era gates that are superseded by the current 1.0.19 semantic contract. Current-release and retained shared release/specification/resource gates listed above were executed separately.

## Database 1.0.8 compatibility
VulkanScope 1.0.19 keeps the fixed Worker origin, explicit schema-2 POST, schema-3 `technicalReport`, compact bounded GET lookup, normalizer-16 consumer contract and 2 MiB fail-closed no-truncation ceiling. Generic 1.0.x producer identity remains versionCode `1000 + patch`, so 1.0.19 / 1019 is the valid pair. No Database migration or stored-report rewrite is required.

Live production submission/lookup from a physical Android device is NOT EXECUTED in this environment.

## Android build evidence
A post-change `:app:compileDebugKotlin` attempt was made through the checked-in Gradle wrapper. The wrapper could not obtain Gradle 9.7.1 because this container cannot resolve `services.gradle.org` and terminated with `java.net.UnknownHostException` before the Android task started. Therefore Android compile/assemble/lint/unit tasks are NOT EXECUTED and are not labeled PASS.

Real-device rendering across DPI, font scale, RTL/TV focus, long-duration heap profiling and device-matrix Vulkan probing are also NOT EXECUTED here.

## Package evidence
- Final source/package census: 483 files.
- Source package manifest/hygiene: PASS.
- Deterministic ZIP packaging uses exact sorted `files.txt`, fixed 1980 ZIP timestamps, fixed regular-file attributes and deflate compression.
- Two independent deterministic archive builds from the final source tree are byte-identical: PASS.
- Clean extract path census and source/package byte equality: 483/483 PASS.
- Clean-extract 1.0.19 semantic verifier/state-machine/negative-mutation suite: PASS; 12 targeted defects rejected plus false-positive control accepted.
- Clean-extract immutable 1.0.18 -> 1.0.19 regression contract: PASS.
- Clean-extract compile/specification/concurrency-resource retained gates: PASS.
- Clean-extract release verification: PASS; version 1.0.19 / versionCode 1019 / Vulkan 1.4.362 / locked Vulkan-Headers commit.
- The external `.sha256` record is generated from the final deterministic archive and is not embedded into the archive itself.
