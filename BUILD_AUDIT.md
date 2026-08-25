# VulkanScope 0.41.10 Build / Release Audit

## Baseline

- VulkanScope: `0.41.10` / versionCode `420`
- Vulkan-Headers/query baseline: `1.4.360`
- Android Gradle Plugin: `9.3.2`
- Gradle wrapper: `9.7.1`
- Compile/target SDK: `37`
- NDK: `29.0.14206865`
- ABIs: `arm64-v8a`, `armeabi-v7a`, `x86_64`
- Submission schema: `2`
- technicalReport schema: `3`

## Image Format Properties2 contract

- Successful query payloads remain in `detailedProperties`.
- `imageFormatQueryResults` is a bounded exact tuple-state ledger and is excluded from Properties & Limits totals.
- `available` requires `VkResult=0` and a corresponding successful detailed-property payload.
- `unsupported` requires `VK_ERROR_FORMAT_NOT_SUPPORTED` (`-11`).
- `unavailable` preserves another non-zero `VkResult`.
- `not_applicable` is used only when the external-handle prerequisite extension is not enumerated; it carries no fabricated Vulkan result.
- Base, OPAQUE_FD and Android Hardware Buffer queries remain independent.
- The query recipe remains `VK_IMAGE_TYPE_2D`, transfer-source/transfer-destination/sampled usage and `flags=0`.

## Specification/toolchain audit

The release remains pinned to the current project Vulkan 1.4.360 header/query baseline. Android API 37 remains the maximum API supported by AGP 9.3. NDK r29 remains Android's current stable NDK. Gradle was advanced within the allowed 9.7.x family to 9.7.1, the current patch release.

## Release gates

`tools/verify_release.py`, registry/parity verification, JSON/XML parsing, Python syntax and package-hygiene checks are required to pass. The verifier checks tuple-state completeness, exact state/VkResult semantics, prerequisite-derived Not applicable evidence, independent external-handle queries and the 4096-entry Database safety bound.

A full Android/NDK release build is a separate gate and must not be claimed as passed unless Gradle and the Android toolchain actually execute successfully in the audit environment.

## Audit execution record — 2026-08-25

- `python3 tools/verify_release.py`: **PASS** after the final parity-source sanitization pass.
- Checked-in Vulkan 1.4.360 registry/query/parity manifests and generated consumers were revalidated by the release verifier, including the CapsViewer parity contracts and current release tuple-state invariants.
- Python tool source compilation: **PASS**.
- Project JSON parsing: **PASS**.
- Android/resource XML parsing: **PASS**.
- Source-package symlink/generated-build-artifact hygiene scan: **PASS**.
- Current upstream recheck: Khronos still publishes Vulkan 1.4.360 dated 2026-08-14; the current Vulkan reference pages preserve the separate external-handle Image Format Properties2 query semantics used by this release. Android documentation still lists API 37 as supported by AGP 9.3, AGP 9.3.2 is published with Android Studio Quail 2 Patch 1, NDK r29 `29.0.14206865` is the latest stable NDK, and Gradle recommends 9.7.1 over 9.7.0.
- `./gradlew :app:assembleRelease --offline --no-daemon` was attempted. The wrapper could not obtain Gradle 9.7.1 because the isolated audit environment cannot resolve `services.gradle.org`; therefore **no Android APK/NDK compile pass is claimed from this environment**. A real Android SDK/NDK environment remains the authoritative compilation gate.

## Candidate source-ZIP extraction gate

A root-layout candidate source ZIP was created from the clean release tree, extracted into a new empty directory, and `python3 tools/verify_release.py` returned **PASS** from the extracted package. The archive also preserved executable mode on `gradlew`. The published ZIP is rebuilt from this same audited source after this record is added and is rechecked once more after creation.
