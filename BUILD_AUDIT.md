# VulkanScope 0.35.0 Build Audit

- Application version: 0.35.0
- versionCode: 351
- Vulkan query/header baseline: 1.4.360
- Compose UI/Foundation/Animation baseline: 1.12.0
- Compose Material 3 baseline: 1.5.0-alpha26
- `ShortNavigationBarItemDefaults.colors` API compatibility fix: PASS by source/API verification
- `selectedTextColorTopIconPosition` selected-label color: PASS
- `selectedTextColorStartIconPosition` selected-label color: PASS
- Obsolete `selectedTextColor` argument in ShortNavigationBar colors: removed
- 0.34.9 full Material 3 Expressive presentation: retained
- Vulkan collection/report/Database/Turnip/update paths: unchanged by this release
- XML/manifest parse: PASS
- Production Kotlin/C/C++ TODO/FIXME scan: PASS
- Release verifier: PASS after 0.35.0 contract update
- Gradle `assembleRelease` in this environment: NOT COMPLETED because the wrapper cannot resolve `services.gradle.org`

The user-provided Android Studio build log reached `compileReleaseKotlin` and identified the exact Material3 API mismatch corrected by this release. A complete local build is not claimed here.
