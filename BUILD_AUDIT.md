# VulkanScope 0.41.14 Build / Release Audit

- Version: 0.41.14 / versionCode 424.
- User-side 0.41.13 `assembleRelease` reached `:app:compileReleaseKotlin` after native builds for all three ABIs and failed because `imageFormatQueryGroupState(device: GpuInfo)` referenced a nonexistent type.
- Corrected signature: `imageFormatQueryGroupState(device: DeviceReport)`.
- This is a compile-correctness-only change; 0.41.13 query/report/Database semantics are unchanged.
- `python tools/verify_release.py` must PASS before packaging.
- A full Android build is claimed only if Gradle/Android toolchain execution actually succeeds.
