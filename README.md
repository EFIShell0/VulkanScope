# VulkanScope

VulkanScope is an Android Vulkan, Surface, Display and HDR inspection tool with local hardware collection and explicit opt-in network services for database submission and application updates.

**Current version: 0.33.3**

## UI

The interface follows a dark Material 3 Expressive visual direction with a compact dashboard, large capability cards, quick access areas and dedicated inspection pages.

## Runtime data

The native collector queries the installed Android Vulkan loader and physical device directly. It enumerates instance extensions, device extensions, core Vulkan 1.0 features, Vulkan 1.1 through 1.4 core feature structures when exposed, queue families, memory heaps and types, selected format properties, physical-device limits, and a real Android VkSurfaceKHR surface.

# Screenshots

<p align="center">
  <img src="screenshots/1.jpg" width="200">
  <img src="screenshots/2.jpg" width="200">
  <img src="screenshots/3.jpg" width="200">
</p>

<p align="center">
  <img src="screenshots/4.jpg" width="200">
  <img src="screenshots/5.jpg" width="200">
  <img src="screenshots/6.jpg" width="200">
</p>

<p align="center">
  <img src="screenshots/7.png" width="500">
  <img src="screenshots/0.22.7-7.png" width="500">
  <img src="screenshots/database.png" width="500">
</p>

**NOTE: The first landscape image was taken from BlueStacks, and VM (hypervisor) software like BlueStacks, MuMuPlayer, LDPlayer, QEMU etc. is not supported.**

**NOTE 2: The last landscape photo is a screenshot of the database website.**

Database link: https://efishell0.github.io/VulkanScope_database/


The Surface page reports exact format and color-space pairs, present modes, surface capabilities and queue-family presentation support. The Display & HDR page separately reports Android display HDR types, luminance, wide-color capability, preferred wide-gamut color space and display modes.

The Extensions page lists runtime-enumerated instance and device extensions with exact Vulkan names, scope and specVersion and provides case-insensitive search.

## Branding

The launcher artwork uses only the approved VulkanScope logo supplied for this project. The source artwork is kept unchanged at `app/src/main/res/drawable-nodpi/vulkanscope_logo_master.png`.

## ABIs

- armeabi-v7a
- arm64-v8a
- x86_64

x86 is intentionally excluded.

## Reporting and export

Exports include application metadata (version, version code, package, ABI), developer name/nickname and GitHub account information. HTML exports embed the same approved horizontal VulkanScope logo used by the application so reports remain self-contained.


TXT and HTML exports include the currently collected runtime Vulkan features, detailed physical-device properties, limits, memory, queues, formats, surface data, layers and exact runtime extension names/specVersion values. HTML reports use colored status badges for supported, not supported, unavailable and unknown states. Extension-specific parity fields are surfaced through the same Features and Properties views when their corresponding runtime query completes.

VulkanScope may continue collecting optional Vulkan details after the main report is visible. During that time the UI shows a Material 3 Expressive "Collecting information…" status and briefly shows "Completed" when the current collection pass finishes.

## Security and privacy

Runtime network access is limited to the explicit VulkanScope Database submission flow and the official GitHub release update flow. Complete technical reports are uploaded only after the user presses Submit complete report, and sensitive device/account identifiers are excluded.

## Build

Open the `VulkanScope` directory in Android Studio with an Android SDK and NDK installation available. The native module uses CMake and C++20.

Build compatibility
- Android Gradle Plugin: 9.3.1
- Gradle: 9.7.x intended build family
- Kotlin: AGP 9 built-in Kotlin; Compose compiler plugin 2.3.21
- JDK: 17+
- Compile SDK: 36
- NDK: 28.2.13676358 or compatible installed NDK

## Driver selection

Settings provides two driver modes:
- **System Vulkan driver**: uses the Android system Vulkan loader/driver.
- **Turnip / third-party driver**: accepts an imported driver ZIP containing a compatible Vulkan ICD and, when supplied, a compatible `libvulkan.so` loader. Mesa-style `VK_DRIVER_FILES` / `VK_ICD_FILENAMES` are configured before the Vulkan loader is opened.

Changing the driver mode restarts the activity so the Vulkan inspection starts from a fresh driver-selection state. Android vendor Vulkan loaders may ignore ICD environment variables; a compatible bundled loader is therefore required for devices whose system loader does not expose external ICD selection.

- Vulkan instance version
- Instance extensions
- Instance layers
- Vulkan implementation capabilities exposed at the instance level
- Available instance-level functionality

Instance extensions can be searched and filtered, making it easier to locate a specific Vulkan extension on devices with large extension lists.

---

## Device

The **Device** section focuses on the physical Vulkan device selected by the application.

It exposes detailed physical-device properties including information such as:

- Device name
- Device type
- Vendor ID
- Device ID
- Driver version
- API version
- Driver properties
- Device properties
- GPU limits
- Device capabilities
- Device-level extensions

This section is particularly useful when comparing different Vulkan drivers, GPUs or Android devices.

---

## Extensions

The **Extensions** section provides a detailed list of Vulkan extensions exposed by the device.

Extensions can be:

- Searched by name
- Filtered
- Viewed as supported
- Viewed as unsupported where applicable
- Viewed together using the **All** filter

The search and filtering functions work together, allowing a specific extension to be located without having to manually scroll through a large list.

Extension information can be useful for determining whether a particular Vulkan feature or capability is exposed by the installed driver.

Examples of the types of extensions that may appear include extensions related to:

- Vulkan core functionality
- Memory
- Synchronization
- Descriptor management
- Dynamic rendering
- Ray tracing
- Mesh shading
- Variable rate shading
- Android-specific functionality
- External memory
- External synchronization
- Surface and presentation
- Format support
- Debugging
- Vendor-specific functionality

The exact extensions displayed depend on the Vulkan implementation and driver installed on the device.

---

## Features

The **Features** section displays Vulkan device feature support reported by the physical device.

This allows the user to see which Vulkan functionality is exposed by the driver rather than relying only on the Vulkan API version.

Feature information can include capabilities related to areas such as:

- Robust buffer access
- Geometry and tessellation
- Wide lines
- Large points
- Multi-viewport rendering
- Sampler features
- Texture compression
- Occlusion queries
- Pipeline statistics
- Shader functionality
- Sparse resources
- Multi-draw functionality
- Descriptor-related capabilities
- Dynamic rendering
- Synchronization
- Advanced shader functionality
- Vendor or extension-specific feature structures

Features exposed through Vulkan extensions can also be relevant even when the corresponding functionality is not part of the original Vulkan core feature set.

---

# Surface

The **Surface** section provides information about Vulkan presentation and window-surface capabilities.

This section is especially useful for determining what combinations of presentation properties are supported by the Android device and Vulkan driver.

Surface information can include:

- Surface capabilities
- Minimum image count
- Maximum image count
- Current extent
- Minimum image extent
- Maximum image extent
- Maximum image array layers
- Supported transforms
- Current transform
- Supported composite alpha modes
- Supported image usage flags
- Presentation modes
- Surface formats
- Surface color spaces

Because a device can expose a large number of surface formats and color spaces, VulkanScope provides a search field for the Surface information.

This makes it possible to quickly search for specific entries such as:

- `BT709`
- `BT2020`
- `sRGB`
- `Display P3`
- `HDR`
- `HDR10`
- `PQ`
- `HLG`
- `SRGB_NONLINEAR`
- `EXTENDED_SRGB_LINEAR`

The Surface section is therefore useful when investigating display, HDR, color-space and presentation compatibility.

---

## Surface Formats

Vulkan surface formats describe combinations of an image format and a color space that can be used for presentation.

VulkanScope displays the available combinations reported by the Vulkan implementation.

These may include different formats such as:

- `VK_FORMAT_B8G8R8A8_UNORM`
- `VK_FORMAT_R8G8B8A8_UNORM`
- `VK_FORMAT_A2B10G10R10_UNORM_PACK32`
- Other formats exposed by the driver

The associated color spaces can include standard SDR spaces as well as extended or HDR-related color spaces when supported.

This makes the Surface section useful for examining whether a device exposes particular color-space and presentation combinations.

---

## Surface Color Spaces

VulkanScope exposes the color-space information returned by Vulkan rather than treating the display as simply "SDR" or "HDR".

Depending on the device and driver, Vulkan may expose color spaces associated with standards and transfer functions such as:

- sRGB
- BT.709
- BT.2020
- Display P3
- HDR10 / ST2084
- HLG
- Extended sRGB
- Other Vulkan-defined or extension-defined color spaces

The exact entries depend on the Android display system, Vulkan driver and device configuration.

---

# Memory

The **Memory** section provides information about the physical device's Vulkan memory configuration.

It displays Vulkan memory heaps and memory types reported by the physical device.

Information includes:

- Memory heap count
- Memory type count
- Heap sizes
- Heap flags
- Memory type flags
- Memory type to heap relationships
- Device-local memory
- Host-visible memory
- Host-coherent memory
- Host-cached memory
- Lazily allocated memory where supported

This can be useful for understanding how the Vulkan driver exposes GPU and CPU-accessible memory to applications.

---

# Queues

The **Queues** section displays the queue families exposed by the Vulkan physical device.

For each queue family, VulkanScope can show information such as:

- Queue family index
- Queue count
- Queue capabilities
- Graphics support
- Compute support
- Transfer support
- Sparse binding support
- Protected queue support where exposed

Queue families are important because Vulkan applications use them to determine how graphics, compute and transfer workloads can be submitted to the GPU.

A GPU may expose multiple queue families with different capabilities, and VulkanScope makes these differences visible.

---

# Formats

The **Formats** section provides information about Vulkan image and buffer format capabilities.

It is useful for determining which Vulkan formats are supported by the physical device and what operations can be performed with them.

Format capabilities can involve:

- Format properties
- Linear tiling support
- Optimal tiling support
- Buffer support
- Sampled image support
- Storage image support
- Color attachment support
- Depth/stencil attachment support
- Blending support
- Transfer source support
- Transfer destination support

This is particularly useful when investigating compatibility for different texture, framebuffer, HDR, depth/stencil and rendering formats.

---

# Display

The **Display** section provides Vulkan display-related information exposed by the device.

Depending on the Android Vulkan implementation, this can include display and presentation-related properties exposed through Vulkan.

Display capabilities can be useful when investigating:

- Display modes
- Display resolution
- Refresh rates
- Presentation support
- Display-related Vulkan capabilities

The exact information available depends on what the Android Vulkan implementation exposes.

---

# Driver Information

VulkanScope is designed to expose information reported by the Vulkan driver rather than relying solely on the Android device model.

This is particularly useful on Android because the same GPU family can have significantly different Vulkan capabilities depending on:

- Android version
- GPU driver
- Vendor driver
- Driver version
- Vulkan API version
- Device firmware
- Vendor-specific extensions

Therefore, two devices with similar GPU hardware may expose different Vulkan capabilities.

---

# GPU Vendor Detection

VulkanScope recognizes major GPU vendors and architectures exposed through Vulkan.

Examples include:

### Qualcomm

Qualcomm Adreno GPUs are detected separately so that Adreno-specific information and Vulkan driver capabilities can be identified.

This is also relevant for devices using Qualcomm Vulkan drivers and Turnip-compatible environments.

### ARM

ARM Mali GPUs are recognized separately from other GPU vendors.

### Imagination

Imagination GPUs, including PowerVR implementations, are recognized as their own GPU vendor category.

### Broadcom

Broadcom Vulkan implementations are also detected separately rather than being grouped together with unrelated GPU vendors.

---

# Turnip / Adreno

VulkanScope contains support for detecting relevant Adreno environments and integrates Adreno-specific functionality where available.

This is intended to make the application useful for investigating Vulkan implementations on Qualcomm hardware, including environments where alternative Vulkan drivers such as Turnip may be used.

Turnip-related functionality is only exposed where the underlying device and architecture make it applicable.

The application does not assume that every ARM64 Android device is an Adreno device.

---

# ABI Information

VulkanScope can display the ABI of the installed application.

Examples include:

- `arm64-v8a`
- `armeabi-v7a`
- `x86_64`

This allows users to determine which native architecture is actually being used by their installed copy of VulkanScope.

The application can therefore be distributed as architecture-specific builds without requiring users to guess which APK they installed.

---

# Search and Filtering

Large Vulkan capability lists can contain hundreds of entries.

VulkanScope therefore provides search functionality in areas where the amount of information can become difficult to navigate manually.

Search can be used to quickly locate:

- Extension names
- Surface formats
- Color spaces
- Vulkan capabilities
- Other long lists of Vulkan information

Where supported, lists can also be filtered by support state:

- **Supported**
- **Unsupported**
- **All**

The filtering system works together with the search field rather than replacing it.

For example, users can search for a particular Vulkan extension and then display only supported entries.

---

# Export

VulkanScope can export the collected Vulkan information for external use.

Exported information is intended to make it easier to:

- Share device information
- Compare Vulkan implementations
- Report driver issues
- Document GPU capabilities
- Keep a record of Vulkan capabilities
- Troubleshoot application compatibility

The exported information can contain the detailed Vulkan information collected by the application rather than only the simplified Overview information.

---

# Multi-Architecture Support

VulkanScope supports building native components for multiple Android ABIs.

Supported architectures include:

| ABI | Description |
|---|---|
| `arm64-v8a` | 64-bit ARM devices |
| `armeabi-v7a` | 32-bit ARM devices |
| `x86_64` | 64-bit x86 Android devices |

Native Vulkan functionality is compiled separately for the appropriate target architecture.

This allows the same project to be used to produce architecture-specific Android builds.

---

# Why VulkanScope?

Android exposes a large amount of Vulkan information, but much of it is difficult for ordinary users to inspect.

VulkanScope brings this information together in a single application and organizes it into dedicated sections.

Instead of simply reporting:

> "Vulkan supported"

VulkanScope is intended to answer questions such as:

- Which GPU is actually being used?
- Which Vulkan API version is available?
- Which Vulkan extensions are exposed?
- Which features are supported?
- Which surface formats are available?
- Which color spaces are available?
- Does the driver expose HDR-related color spaces?
- Which presentation modes are available?
- How much device-local memory is exposed?
- Which queue families are available?
- Which image formats support particular operations?
- Which ABI is being used?
- Which Vulkan driver capabilities are available on the device?

---

# Open Source

VulkanScope is open-source software.

The source code is available on GitHub:

**https://github.com/EFIShell0**

Contributions, testing, bug reports and feedback are welcome.

---

# Requirements

VulkanScope requires an Android device with a Vulkan-capable implementation.

The exact information available depends on the Vulkan driver and Android implementation running on the device.

Some capabilities may be unavailable on older devices or drivers.

---

# Third-Party Components

VulkanScope uses third-party open-source components where required by the project.

Third-party components remain subject to their respective licenses and copyright notices.

---

## VulkanScope

**A detailed Vulkan capability viewer for Android.**

Inspect your GPU.  
Inspect your driver.  
Inspect your Vulkan implementation.
