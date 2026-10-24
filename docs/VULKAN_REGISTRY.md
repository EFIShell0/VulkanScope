# Vulkan registry lock — 1.4.366

The VulkanScope 4.0.15 source bundle includes the pinned, byte-locked Khronos Vulkan® 1.4.366 registry at `registry/upstream/vk.xml`. Build-time generated assets and native header references must agree with `registry/registry_lock.json`, CMake guards, `registry/generated/`, and Kotlin registry indices. No registry/header download occurs at runtime.

## Catalog and query coverage

| Item | Locked count |
| --- | ---: |
| Registered Vulkan® extensions | 479 |
| Command symbols | 842 |
| Token symbols | 6,274 |
| API type symbols | 2,467 |
| Validated physical-device extension providers | 305 (300 stable, 5 provisional) |
| Registry-queryable providers | 307 |
| Implemented physical-device structures | 110 |
| Validated runtime query groups | 104 |

Two registry-queryable providers are not yet validated in the native collector: `VK_ARM_cooperative_matrix_layouts` and `VK_GOOGLE_hdr_smpte_st_209450_metadata`. This is a **coverage gap**, not a claim of absent device support. Registry registration and dedicated native query implementation must not be conflated.

## Regeneration / verification

```powershell
python -B tools\verify_cmake_registry_lock.py
python -B tools\verify_release_source.py
python tools\generate_extension_reference.py --registry registry\upstream\vk.xml --output registry\generated\extension_reference.json --kotlin-output app\src\main\java\com\efishell\vulkanscope\VulkanExtensionReference.kt --baseline "Vulkan® 1.4.366"
```

Only regenerate native coverage after implementing and reviewing the matching Vulkan-Headers 1.4.366 structures and verifying them on appropriate devices. Do not mark pending providers validated in metadata without doing so.
