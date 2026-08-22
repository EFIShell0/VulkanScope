# VulkanScope 0.34.8 Build Audit

- Application version: 0.34.8
- versionCode: 349
- Vulkan query/header baseline: 1.4.360
- Release verifier: PASS
- Manifest parse: PASS
- Obtainium config static verification: PASS
- Built-in direct updater: enabled by default on fresh installs
- Obtainium informational guidance: retained
- Add to Obtainium runtime action: absent
- `VkFormatProperties3` / `VkFormatFeatureFlags2` native query path: retained
- FormatFeatureFlags2 availability evidence: explicit
- Main Formats model consumes 64-bit Flags2 masks when available: PASS
- Legacy 32-bit format masks remain fallback-only: PASS
- Unsigned 64-bit mask parsing and unknown-bit rendering: PASS
- Settings and Info rounded Material 3 Expressive-aligned vector icons: PASS
- Settings/Info navigation geometry and accessibility descriptions: unchanged
- Database schema/submission behavior: unchanged
- Gradle assembleRelease attempt: NOT COMPLETED because the wrapper could not resolve services.gradle.org in this environment

A full Gradle Kotlin/native compilation is not claimed unless completed by the validation environment.
