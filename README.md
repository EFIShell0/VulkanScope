# VulkanScope

VulkanScope is an offline Android Vulkan, Surface, Display and HDR inspection tool.

## UI

The interface follows a dark Material 3 Expressive visual direction with a compact dashboard, large capability cards, quick access areas and dedicated inspection pages.

## Runtime data

The native collector queries the installed Android Vulkan loader and physical device directly. It enumerates instance extensions, device extensions, core Vulkan 1.0 features, Vulkan 1.1 through 1.4 core feature structures when exposed, queue families, memory heaps and types, selected format properties, physical-device limits, and a real Android VkSurfaceKHR surface.

The Surface page reports exact format and color-space pairs, present modes, surface capabilities and queue-family presentation support. The Display & HDR page separately reports Android display HDR types, luminance, wide-color capability, preferred wide-gamut color space and display modes.

The Extensions page lists runtime-enumerated instance and device extensions with exact Vulkan names, scope and specVersion and provides case-insensitive search.

## Branding

The launcher artwork uses only the approved VulkanScope logo supplied for this project. The source artwork is kept unchanged at `app/src/main/res/drawable-nodpi/vulkanscope_logo_master.png`.

## ABIs

- armeabi-v7a
- arm64-v8a
- x86_64

x86 is intentionally excluded.

## Security and privacy

The application declares no Internet permission and performs no runtime network access. Hardware and display information stays on the device.

## Build

Open the `VulkanScope` directory in Android Studio with an Android SDK and NDK installation available. The native module uses CMake and C++20.

Build compatibility
- Android Gradle Plugin: 9.3.1
- Gradle: 9.6.x supported
- Kotlin: AGP 9 built-in Kotlin; Compose compiler plugin 2.3.21
- JDK: 17+
- Compile SDK: 36
- NDK: 28.2.13676358 or compatible installed NDK

## Driver selection

Settings provides two driver modes:
- **System Vulkan driver**: uses the Android system Vulkan loader/driver.
- **Turnip / third-party driver**: accepts an imported driver ZIP containing a compatible Vulkan ICD and, when supplied, a compatible `libvulkan.so` loader. Mesa-style `VK_DRIVER_FILES` / `VK_ICD_FILENAMES` are configured before the Vulkan loader is opened.

Changing the driver mode restarts the activity so the Vulkan inspection starts from a fresh driver-selection state. Android vendor Vulkan loaders may ignore ICD environment variables; a compatible bundled loader is therefore required for devices whose system loader does not expose external ICD selection.
