# VulkanScope 0.41.6 Build / Release Audit

## Release identity

- VulkanScope: `0.41.6`
- versionCode: `416`
- Published/query Vulkan baseline: `1.4.360`
- Android compile/target SDK: `37`
- Minimum Android API: `24`
- Submission schema: `2`
- `technicalReport` schema: `3`

## Confirmed 0.41.5 failure and correction

The supplied Windows `assembleRelease` build completed native compilation for all three configured ABIs and failed in `:app:compileReleaseKotlin` at `MainActivity.kt` because `mutableIntStateOf` was unresolved. The source referenced `mutableIntStateOf(4)` but did not import `androidx.compose.runtime.mutableIntStateOf`. 0.41.6 restores that import and adds a verifier gate for primitive Compose state-factory imports.

The libadrenotools diagnostics in the supplied log are warnings and are not the task that failed. This patch does not silence or locally fork those third-party warnings.

## Scope and regression boundary

The change is compile-only plus release-gate hardening. Vulkan collection, Analysis/Profile semantics, self-tests, Surface/WSI, reporting, Database payloads, updater behavior, Turnip/imported-driver handling, ABI coverage and Android API compatibility are unchanged from 0.41.5.

## Verification

- `tools/verify_release.py`: required to PASS.
- Kotlin source import invariant: required to PASS.
- XML/JSON/Python syntax and source-package hygiene: required to PASS.
- A full Android Gradle build is only claimed if the execution environment has the required Gradle/Android dependency cache or network access.
