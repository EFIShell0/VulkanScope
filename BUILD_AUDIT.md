# VulkanScope 0.33.5 build audit

## Scope

- GitHub update discovery changed from `/releases/latest` to the bounded official `EFIShell0/VulkanScope` release list.
- Draft releases are excluded; stable and pre-release tags are considered only when their numeric version is newer than the installed build.
- Official release APK URL validation remains HTTPS-only and restricted to the VulkanScope GitHub Releases download path.
- Installed-ABI APK selection and universal fallback remain intact.
- Update-status banner now matches the OpenGLESScope visual structure, including the green `UP TO DATE` badge, neutral status text, spacing and animation timing.
- Vulkan collection, Turnip/SAF, report, Database and native query behavior were not changed.

## Static validation

- Version: 0.33.5 / versionCode 335: PASS
- Old application `/releases/latest` endpoint removed: PASS
- Official release-list endpoint with `per_page=20`: PASS
- Draft exclusion: PASS
- Stable/pre-release numeric version selection: PASS
- 2 MiB release-metadata response bound: PASS
- Strict official GitHub APK URL validation: PASS
- ABI-specific selection with universal fallback preserved: PASS
- OpenGLESScope-parity `UP TO DATE` badge colors and geometry: PASS
- Update banner enter/exit timing parity: PASS
- `PROJECT_RULES.md` updated: PASS

## Build attempt

A Gradle compile attempt was made, but the execution environment could not resolve `services.gradle.org` while the wrapper attempted to obtain Gradle 9.7.0. The failure was `java.net.UnknownHostException: services.gradle.org` before project compilation began.

Therefore this audit does not claim a successful APK compilation in the assistant environment.
