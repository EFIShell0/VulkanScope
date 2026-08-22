# VulkanScope 0.34.7 Build Audit

- Application version: 0.34.7
- versionCode: 348
- Vulkan query/header baseline: 1.4.360
- Release verifier: PASS
- Manifest parse: PASS
- Obtainium config static verification: PASS
- Add to Obtainium runtime action: removed
- Obtainium informational guidance: retained
- Universal-APK filter: `(?i).*universal.*\.apk$`
- Obtainium architecture auto-filter: disabled
- Built-in direct updater: enabled by default on fresh installs
- Disabled-state first-install information banner logic is retained; a normal fresh install with the default-enabled updater proceeds directly to the non-blocking update check
- Current runtime source contains no IzzyOnDroid-specific messaging
- Existing package/signature/version/ABI update verification remains present
- Vulkan capability/report/Database behavior was not changed by this release

A full Gradle Kotlin/native compilation is not claimed unless completed by the validation environment.
- Gradle assembleRelease attempt: NOT COMPLETED because the wrapper could not resolve services.gradle.org in this environment
