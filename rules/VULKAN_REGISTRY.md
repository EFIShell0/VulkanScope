# Vulkan registry baseline

VulkanScope 0.11.0 uses Vulkan 1.4.360 semantics as the validated baseline. The Khronos Vulkan registry and specification are the authoritative sources for names, `VkStructureType` semantics, extension dependencies and promoted core behavior.

The application enumerates instance and device extensions from the active implementation exactly as returned. Dependency-aware instance extension enabling is restricted to runtime-enumerated candidates.

Registry parsing is build-time/offline only. Runtime never downloads or parses `vk.xml`.

A registry-defined structure is executable only when an explicit native query path has been validated for ABI layout, extension/API exposure, pNext construction and serialization. Otherwise the structure remains unavailable rather than being guessed.

The native query catalog contains explicit descriptors for every validated runtime query group. Instance-extension dependency candidates are selected only from runtime-enumerated extensions and the generated catalog; no extension name is enabled solely because it appears in the registry.
