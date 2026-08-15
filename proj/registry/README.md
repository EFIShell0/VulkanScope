# Registry-driven Vulkan query system

VulkanScope treats the Khronos `vk.xml` registry as the authoritative build-time API catalog. The generator can emit a JSON manifest and a native catalog header. Runtime never downloads or parses the registry.

The runtime query engine uses only explicitly validated native query paths. Registry structures that are present in the catalog but do not have a validated native path remain `unavailable`; no guessed `sType`, field layout, or capability is produced.

Build-time generation:

`python tools/generate_vk_registry.py --registry <path-to-vk.xml> --header <VULKAN_HEADERS_DIR>/include/vulkan/vulkan_core.h --out registry/generated/vulkan_registry_manifest.json --header-out app/src/main/cpp/registry_query_catalog.h`

Verification:

`python tools/verify_registry_catalog.py registry/generated/vulkan_registry_manifest.json <VULKAN_HEADERS_DIR>/include/vulkan/vulkan_core.h app/src/main/cpp/registry_query_catalog.h`

The shipped catalog is a checked-in build artifact for offline builds. Re-generating it is a build/development step, not an application runtime dependency.
