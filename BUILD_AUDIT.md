# VulkanScope 0.34.3 Build Audit

- Application version: 0.34.3
- versionCode: 343
- Vulkan query/header baseline: 1.4.360
- Complete-report parity audit: PASS
- Host Image Copy two-step layout-array collection: retained
- UI -> TXT -> HTML -> technicalReport -> Database generic detailed-property path: verified
- Canonical/raw memory, queue, video-codec and format masks in TXT/HTML: verified
- Instance-layer extension parity in TXT/HTML/structured report: verified
- Source package integrity and release verifier: see validation output generated with this release.

- Full Gradle Kotlin/native compile: not executed in this environment because Gradle 9.7.0 is not cached and `services.gradle.org` is unreachable. No successful APK compilation is claimed.

## 0.34.3 Host Image Copy promoted-name parity
- Vulkan 1.4 pointer-backed Host Image Copy properties use the canonical member names `pCopySrcLayouts` and `pCopyDstLayouts`.
- The Core 1.4 section is preserved so promoted-core provenance remains distinguishable from `VK_EXT_host_image_copy`.
- Database comparison is expected to alias the promoted property family without rewriting raw report provenance.


## 0.34.4 first-install notice validation

- One-time first-install gating: checked.
- Seven-second non-modal update-style banner: checked.
- No network request introduced by the notice: checked.
- Existing direct-update consent flow preserved: checked.
