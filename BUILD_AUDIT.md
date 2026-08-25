# VulkanScope 0.41.7 Build / Release Audit

## Release identity

- VulkanScope: `0.41.7`
- versionCode: `417`
- Published/query Vulkan baseline: `1.4.360`
- Android compile/target SDK: `37`
- Minimum Android API: `24`
- Submission schema: `2`
- `technicalReport` schema: `3`

## Confirmed correctness issue

The Image Format Properties2 collector gated every `VkPhysicalDeviceExternalImageFormatInfo` query behind success of the handle-less query for the same format/tiling/usage tuple. Vulkan defines the external-handle query as a distinct `vkGetPhysicalDeviceImageFormatProperties2` call. A base `VK_ERROR_FORMAT_NOT_SUPPORTED` therefore does not justify skipping OPAQUE_FD or Android Hardware Buffer queries. This could produce `Not reported` Database comparisons even when the external query would have returned evidence.

0.41.7 removes that dependency. Base and enabled external-handle variants are attempted independently. Compact per-device diagnostic rows report attempt, success, `VK_ERROR_FORMAT_NOT_SUPPORTED` and other-error counts; only successful full capability payloads are expanded into the existing detailed-properties list, preserving report-size bounds.

## Regression boundary

No submission schema migration, Database endpoint change, Turnip bundle/security change, permission change, ABI removal, automatic upload, analytics or Vulkan baseline change is introduced. 0.41.6 Compose compile-gate checks remain mandatory.

## Verification

- `tools/verify_release.py`: required to PASS.
- Native source invariant: external image-format queries must not be nested under `baseResult == VK_SUCCESS`.
- Query diagnostics and both OPAQUE_FD/AHB paths are release-gated.
- XML/JSON/Python syntax and package hygiene: required to PASS.
- Full Android compilation is claimed only when the build environment can actually execute the Gradle/Android toolchain.

## Environment build attempt

`./gradlew :app:assembleRelease --offline --no-daemon` was attempted in the audit environment after the source verifier passed. The wrapper could not start the Android build because Gradle 9.7.0 was not cached and the environment could not resolve `services.gradle.org`. This is an environment/toolchain availability limit, not a claimed compile pass. The user-side Android Studio/Gradle build remains the authoritative full compilation gate.
