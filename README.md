# VulkanScope

VulkanScope is an Android application for inspecting and displaying detailed Vulkan GPU and device capabilities.

It is designed to provide a clear and comprehensive view of the Vulkan implementation available on an Android device, including GPU information, Vulkan versions, extensions, features, surface capabilities, formats, memory, queues and more.

## Features

- Detailed Vulkan device information
- GPU vendor and device detection
- Vulkan API version information
- Instance and device extensions
- Vulkan features and capabilities
- Surface capabilities and supported formats
- Display information
- Memory information
- Queue family information
- Vulkan format information
- Supported / unsupported filtering for extensions and capabilities
- Search functionality for large capability lists
- Turnip / Adreno-related information
- GPU vendor detection including:
  - Qualcomm Adreno
  - ARM Mali
  - Imagination PowerVR
  - Broadcom
  - Other Vulkan-capable GPUs
- Export of Vulkan information
- Support for multiple Android ABIs

## Supported Architectures

VulkanScope can be built for multiple Android architectures, including:

- `arm64-v8a`
- `armeabi-v7a`
- `x86_64`

The application also displays the ABI used by the installed APK so that users can easily determine which architecture they are running.

## Vulkan Information

VulkanScope provides information in several categories:

### Overview
A quick overview of the Vulkan implementation and GPU.

### Instance
Information about the Vulkan instance and instance-level capabilities.

### Device
Detailed information about the physical Vulkan device.

### Extensions
Lists available Vulkan extensions with search and support filtering.

### Surface
Displays surface-related Vulkan capabilities, including supported formats, color spaces and presentation capabilities.

### Features
Shows Vulkan feature support reported by the physical device.

### Memory
Displays Vulkan memory heaps and memory types.

### Queues
Shows available queue families and their capabilities.

### Formats
Displays Vulkan format capabilities supported by the device.

## Export

VulkanScope can export the collected information so it can be saved and shared for diagnostic or compatibility purposes.

## Requirements

- Android device with Vulkan support
- Android version compatible with the application's minimum SDK
- A Vulkan-capable GPU

Some Vulkan capabilities depend on the device's GPU, Android version, Vulkan driver and vendor implementation.

## Open Source

VulkanScope is open-source software.

The source code is available on GitHub:

https://github.com/EFIShell0

## Developer

**Semih Boran**

Nickname: **EFI Shell**

GitHub:  
https://github.com/EFIShell0

## License

This project is open source. See the repository for the applicable license and third-party component licenses.

## Third-Party Components

VulkanScope uses third-party libraries and components where required by the project.

Their respective licenses and copyright notices remain applicable.

---

**VulkanScope** — Vulkan capability and GPU information for Android.
