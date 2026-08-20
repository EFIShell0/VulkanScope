# VulkanScope 0.33.4 build audit

## Requested parity work
- Info now owns Developer, Application, Device ABI, Android, Vulkan registry/query engine, About, complete TXT/HTML export, and VulkanScope Database actions.
- Settings contains only System/Turnip driver selection and the conditional Turnip ZIP import workflow.
- Application primary accent is Khronos Vulkan dark red #A41E22 (RGB 164, 30, 34).
- HTML report shell uses Vulkan-red hero, border, available-state and link tones while preserving semantic status colors and self-contained offline behavior.
- Android TV uses the navigation rail regardless of orientation and read-only capability surfaces participate in D-pad focus traversal with bring-into-view behavior.

## Static checks
- Version: 0.33.4 / versionCode 334.
- Old primary #F21D2F removed from MainActivity.
- Old soft accent #FF7A88 removed from MainActivity.
- Settings contains no export or Database section.
- Info contains all requested sections and repository/update actions.
- TV browse modifier is attached to capability section, item, key/value, metric, legacy section/key-value/data row and HDR surfaces.
- No new source comments were added.

## Build environment limitation
A Gradle Kotlin compilation was attempted with `./gradlew :app:compileDebugKotlin --no-daemon`. The wrapper could not download Gradle 9.7.0 because the execution environment cannot resolve `services.gradle.org` (`UnknownHostException`). No APK build success is claimed.
