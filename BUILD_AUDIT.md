# VulkanScope 0.41.3 Build Audit

## Completed static/release checks
- `tools/verify_release.py`: PASS.
- Vulkan 1.4.360 queue flag set rechecked against current Khronos specification.
- Known queue mask corrected to `0x57F`, including `VK_QUEUE_SPARSE_BINDING_BIT`.
- Queue capability support and Vulkan Video codec-operation query availability are separated.
- UI, TXT, HTML and technicalReport queue fields are kept in parity.
- Schema 2 / technicalReport 3 remain unchanged.

## Gradle release build attempt
`./gradlew :app:assembleRelease --no-daemon` was attempted in this environment.

The build stopped before project compilation because Gradle 9.7.0 was not cached and `services.gradle.org` could not be resolved (`java.net.UnknownHostException`). No APK build success is claimed from this environment.
