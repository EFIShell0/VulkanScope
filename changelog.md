# VulkanScope 0.41.6

- Fixed the release-blocking Kotlin compilation error in `AnalysisPage`: `mutableIntStateOf` is now imported from `androidx.compose.runtime`.
- Retained primitive Int state for dependency-graph depth instead of falling back to boxed state.
- Added release-verifier checks that fail when supported primitive Compose state factories are referenced without their required runtime imports.
- Preserved all 0.41.5 Analysis, Profile, selected-device self-test and Database permalink behavior.
- Preserved Vulkan 1.4.360, Android API 37, arm64-v8a/armeabi-v7a/x86_64, schema 2 / technicalReport 3 and existing security/report-completeness rules.
- The libadrenotools messages visible earlier in native compilation are third-party warnings; they were not the cause of the Kotlin build failure and are not suppressed by this patch.
