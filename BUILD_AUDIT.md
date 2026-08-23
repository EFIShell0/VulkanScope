# VulkanScope 0.35.1 Build Audit

- Application version: 0.35.1
- versionCode: 352
- Vulkan published/query/header baseline: 1.4.360
- Compose UI/Foundation/Animation baseline: 1.12.0
- Compose Material 3 baseline: 1.5.0-alpha26
- Direct-update cancellation hardening: PASS by source verification
- APK rejection cleanup: PASS by source verification
- Signing-lineage direction hardening: PASS by source verification
- Vulkan 1.4.360 query/report coverage static audit: PASS
- 64-bit VkFormatProperties3 path retained: PASS
- UI/TXT/HTML/Database complete-dataset paths retained: PASS
- Native Vulkan/Surface ownership static review: PASS
- XML/manifest parse: PASS
- Production TODO/FIXME scan: PASS
- Release verifier: PASS after 0.35.1 contract update
- Gradle assembleRelease: NOT COMPLETED because the Gradle 9.7 wrapper distribution is not cached and services.gradle.org cannot be resolved in this environment.
