# Build VulkanScope 4.0.15

## Pinned toolchain

| Tool | Version |
| --- | --- |
| Java | JDK 17 or later |
| Android Gradle Plugin | 9.4.1 |
| Kotlin Compose plugin | 2.4.21 |
| Gradle Wrapper | 9.8.1 |
| Compile SDK | API 37, minor API 2 |
| Target SDK | 37 |
| Minimum SDK | 31 (Android 12) |
| Android NDK | 29.0.14206865 |
| CMake | Minimum 3.22.1 |
| C++ | C++20 |
| Vulkan-Headers | v1.4.366 |

## Windows PowerShell

Run the commands in the extracted repository root:

```powershell
python -B tools\quality_gate.py
.\gradlew.bat --no-daemon :app:assembleRelease
python -B tools\verify_release_apks.py
```

Or on macOS/Linux, execute `./gradlew --no-daemon :app:assembleRelease` with the Android SDK and NDK installed.

## Release APK names

- `app/build/outputs/apk/release/vulkanscope-4.0.15-universal.apk`
- `app/build/outputs/apk/release/vulkanscope-4.0.15-arm64-v8a.apk`
- `app/build/outputs/apk/release/vulkanscope-4.0.15-x86_64.apk`

ARMv7 (`armeabi-v7a`) and 32-bit x86 are not built. Upstream Vulkan-Headers and libadrenotools are pinned build-time dependencies; a fresh build may require network access if they are not cached.

The checked-in GitHub Actions workflow also invokes the release build and APK output verification. **A successful Python quality gate is not evidence that the full Android APK compiled or that pager animations were inspected on a physical device.** Review [QA](QA.md) before a production release.
