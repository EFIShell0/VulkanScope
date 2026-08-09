#ifndef VULKAN_MIN_H
#define VULKAN_MIN_H
#include <stdint.h>
#include <stddef.h>
#ifdef __cplusplus
extern "C" {
#endif

typedef uint32_t VkFlags;
typedef uint32_t VkBool32;
typedef uint64_t VkDeviceSize;
typedef struct VkInstance_T* VkInstance;
typedef struct VkPhysicalDevice_T* VkPhysicalDevice;
typedef uint64_t VkSurfaceKHR;
typedef void* VkAllocationCallbacks;
typedef int32_t VkResult;
typedef void* PFN_vkVoidFunction;

typedef struct VkExtensionProperties { char extensionName[256]; uint32_t specVersion; } VkExtensionProperties;
typedef struct VkLayerProperties { char layerName[256]; uint32_t specVersion; uint32_t implementationVersion; char description[256]; } VkLayerProperties;
typedef struct VkApplicationInfo { uint32_t sType; const void* pNext; const char* pApplicationName; uint32_t applicationVersion; const char* pEngineName; uint32_t engineVersion; uint32_t apiVersion; } VkApplicationInfo;
typedef struct VkInstanceCreateInfo { uint32_t sType; const void* pNext; VkFlags flags; const VkApplicationInfo* pApplicationInfo; uint32_t enabledLayerCount; const char* const* ppEnabledLayerNames; uint32_t enabledExtensionCount; const char* const* ppEnabledExtensionNames; } VkInstanceCreateInfo;
typedef struct VkAndroidSurfaceCreateInfoKHR { uint32_t sType; const void* pNext; VkFlags flags; struct ANativeWindow* window; } VkAndroidSurfaceCreateInfoKHR;
typedef struct VkExtent2D { uint32_t width; uint32_t height; } VkExtent2D;
typedef struct VkExtent3D { uint32_t width; uint32_t height; uint32_t depth; } VkExtent3D;
typedef struct VkSurfaceCapabilitiesKHR { uint32_t minImageCount; uint32_t maxImageCount; VkExtent2D currentExtent; VkExtent2D minImageExtent; VkExtent2D maxImageExtent; uint32_t maxImageArrayLayers; VkFlags supportedTransforms; uint32_t currentTransform; VkFlags supportedCompositeAlpha; VkFlags supportedUsageFlags; } VkSurfaceCapabilitiesKHR;
typedef struct VkSurfaceFormatKHR { int32_t format; int32_t colorSpace; } VkSurfaceFormatKHR;
typedef struct VkQueueFamilyProperties { VkFlags queueFlags; uint32_t queueCount; uint32_t timestampValidBits; VkExtent3D minImageTransferGranularity; } VkQueueFamilyProperties;
typedef struct VkMemoryType { VkFlags propertyFlags; uint32_t heapIndex; } VkMemoryType;
typedef struct VkMemoryHeap { VkDeviceSize size; VkFlags flags; } VkMemoryHeap;
typedef struct VkPhysicalDeviceMemoryProperties { uint32_t memoryTypeCount; VkMemoryType memoryTypes[32]; uint32_t memoryHeapCount; VkMemoryHeap memoryHeaps[16]; } VkPhysicalDeviceMemoryProperties;
typedef struct VkFormatProperties { VkFlags linearTilingFeatures; VkFlags optimalTilingFeatures; VkFlags bufferFeatures; } VkFormatProperties;
typedef struct VkPhysicalDeviceFeatures { uint32_t values[55]; } VkPhysicalDeviceFeatures;

// Physical-device properties queried through vkGetPhysicalDeviceProperties2.
typedef struct VkPhysicalDeviceProperties2 { uint32_t sType; void* pNext; uint8_t properties[1024]; } VkPhysicalDeviceProperties2;
typedef struct VkPhysicalDeviceVulkan11Properties {
    uint32_t sType; void* pNext; uint8_t deviceUUID[16]; uint8_t driverUUID[16]; uint8_t deviceLUID[8];
    uint32_t deviceNodeMask; VkBool32 deviceLUIDValid; uint32_t subgroupSize; uint32_t subgroupSupportedStages;
    uint32_t subgroupSupportedOperations; VkBool32 subgroupQuadOperationsInAllStages; uint32_t pointClippingBehavior;
    uint32_t maxMultiviewViewCount; uint32_t maxMultiviewInstanceIndex; VkBool32 protectedNoFault;
    uint32_t maxPerSetDescriptors; uint64_t maxMemoryAllocationSize;
} VkPhysicalDeviceVulkan11Properties;
typedef struct VkPhysicalDeviceVulkan12Properties {
    uint32_t sType; void* pNext; uint32_t driverID; char driverName[256]; char driverInfo[256];
    uint8_t conformanceVersion[4]; uint32_t denormBehaviorIndependence; uint32_t roundingModeIndependence;
    VkBool32 shaderSignedZeroInfNanPreserveFloat16; VkBool32 shaderSignedZeroInfNanPreserveFloat32; VkBool32 shaderSignedZeroInfNanPreserveFloat64;
    VkBool32 shaderDenormPreserveFloat16; VkBool32 shaderDenormPreserveFloat32; VkBool32 shaderDenormPreserveFloat64;
    VkBool32 shaderDenormFlushToZeroFloat16; VkBool32 shaderDenormFlushToZeroFloat32; VkBool32 shaderDenormFlushToZeroFloat64;
    VkBool32 shaderRoundingModeRTEFloat16; VkBool32 shaderRoundingModeRTEFloat32; VkBool32 shaderRoundingModeRTEFloat64;
    VkBool32 shaderRoundingModeRTZFloat16; VkBool32 shaderRoundingModeRTZFloat32; VkBool32 shaderRoundingModeRTZFloat64;
    uint32_t maxUpdateAfterBindDescriptorsInAllPools;
    VkBool32 shaderUniformBufferArrayNonUniformIndexingNative; VkBool32 shaderSampledImageArrayNonUniformIndexingNative;
    VkBool32 shaderStorageBufferArrayNonUniformIndexingNative; VkBool32 shaderStorageImageArrayNonUniformIndexingNative;
    VkBool32 shaderInputAttachmentArrayNonUniformIndexingNative; VkBool32 robustBufferAccessUpdateAfterBind; VkBool32 quadDivergentImplicitLod;
    uint32_t maxPerStageDescriptorUpdateAfterBindSamplers; uint32_t maxPerStageDescriptorUpdateAfterBindUniformBuffers;
    uint32_t maxPerStageDescriptorUpdateAfterBindStorageBuffers; uint32_t maxPerStageDescriptorUpdateAfterBindSampledImages;
    uint32_t maxPerStageDescriptorUpdateAfterBindStorageImages; uint32_t maxPerStageDescriptorUpdateAfterBindInputAttachments;
    uint32_t maxPerStageUpdateAfterBindResources; uint32_t maxDescriptorSetUpdateAfterBindSamplers;
    uint32_t maxDescriptorSetUpdateAfterBindUniformBuffers; uint32_t maxDescriptorSetUpdateAfterBindUniformBuffersDynamic;
    uint32_t maxDescriptorSetUpdateAfterBindStorageBuffers; uint32_t maxDescriptorSetUpdateAfterBindStorageBuffersDynamic;
    uint32_t maxDescriptorSetUpdateAfterBindSampledImages; uint32_t maxDescriptorSetUpdateAfterBindStorageImages;
    uint32_t maxDescriptorSetUpdateAfterBindInputAttachments; uint32_t supportedDepthResolveModes; uint32_t supportedStencilResolveModes;
    VkBool32 independentResolveNone; VkBool32 independentResolve; VkBool32 filterMinmaxSingleComponentFormats;
    VkBool32 filterMinmaxImageComponentMapping; uint64_t maxTimelineSemaphoreValueDifference; uint32_t framebufferIntegerColorSampleCounts;
} VkPhysicalDeviceVulkan12Properties;
typedef struct VkPhysicalDeviceFragmentDensityMapPropertiesEXT {
    uint32_t sType; void* pNext; VkExtent2D minFragmentDensityTexelSize; VkExtent2D maxFragmentDensityTexelSize; VkBool32 fragmentDensityInvocations;
} VkPhysicalDeviceFragmentDensityMapPropertiesEXT;
typedef struct VkPhysicalDeviceFragmentDensityMap2PropertiesEXT {
    uint32_t sType; void* pNext; VkBool32 subsampledLoads; VkBool32 subsampledCoarseReconstructionEarlyAccess; uint32_t maxSubsampledArrayLayers; uint32_t maxDescriptorSetSubsampledSamplers;
} VkPhysicalDeviceFragmentDensityMap2PropertiesEXT;

typedef struct VkPhysicalDeviceVulkan13Properties {
    uint32_t sType; void* pNext; uint32_t minSubgroupSize; uint32_t maxSubgroupSize; uint32_t maxComputeWorkgroupSubgroups;
    uint32_t requiredSubgroupSizeStages; uint32_t maxInlineUniformBlockSize; uint32_t maxPerStageDescriptorInlineUniformBlocks;
    uint32_t maxPerStageDescriptorUpdateAfterBindInlineUniformBlocks; uint32_t maxDescriptorSetInlineUniformBlocks;
    uint32_t maxDescriptorSetUpdateAfterBindInlineUniformBlocks; uint32_t maxInlineUniformTotalSize;
    VkBool32 integerDotProduct8BitUnsignedAccelerated; VkBool32 integerDotProduct8BitSignedAccelerated; VkBool32 integerDotProduct8BitMixedSignednessAccelerated;
    VkBool32 integerDotProduct4x8BitPackedUnsignedAccelerated; VkBool32 integerDotProduct4x8BitPackedSignedAccelerated; VkBool32 integerDotProduct4x8BitPackedMixedSignednessAccelerated;
    VkBool32 integerDotProduct16BitUnsignedAccelerated; VkBool32 integerDotProduct16BitSignedAccelerated; VkBool32 integerDotProduct16BitMixedSignednessAccelerated;
    VkBool32 integerDotProduct32BitUnsignedAccelerated; VkBool32 integerDotProduct32BitSignedAccelerated; VkBool32 integerDotProduct32BitMixedSignednessAccelerated;
    VkBool32 integerDotProduct64BitUnsignedAccelerated; VkBool32 integerDotProduct64BitSignedAccelerated; VkBool32 integerDotProduct64BitMixedSignednessAccelerated;
    VkBool32 integerDotProductAccumulatingSaturating8BitUnsignedAccelerated; VkBool32 integerDotProductAccumulatingSaturating8BitSignedAccelerated; VkBool32 integerDotProductAccumulatingSaturating8BitMixedSignednessAccelerated;
    VkBool32 integerDotProductAccumulatingSaturating4x8BitPackedUnsignedAccelerated; VkBool32 integerDotProductAccumulatingSaturating4x8BitPackedSignedAccelerated; VkBool32 integerDotProductAccumulatingSaturating4x8BitPackedMixedSignednessAccelerated;
    VkBool32 integerDotProductAccumulatingSaturating16BitUnsignedAccelerated; VkBool32 integerDotProductAccumulatingSaturating16BitSignedAccelerated; VkBool32 integerDotProductAccumulatingSaturating16BitMixedSignednessAccelerated;
    VkBool32 integerDotProductAccumulatingSaturating32BitUnsignedAccelerated; VkBool32 integerDotProductAccumulatingSaturating32BitSignedAccelerated; VkBool32 integerDotProductAccumulatingSaturating32BitMixedSignednessAccelerated;
    VkBool32 integerDotProductAccumulatingSaturating64BitUnsignedAccelerated; VkBool32 integerDotProductAccumulatingSaturating64BitSignedAccelerated; VkBool32 integerDotProductAccumulatingSaturating64BitMixedSignednessAccelerated;
    uint64_t storageTexelBufferOffsetAlignmentBytes; VkBool32 storageTexelBufferOffsetSingleTexelAlignment;
    uint64_t uniformTexelBufferOffsetAlignmentBytes; VkBool32 uniformTexelBufferOffsetSingleTexelAlignment; uint64_t maxBufferSize;
} VkPhysicalDeviceVulkan13Properties;

typedef struct VkPhysicalDeviceFeatures2 { uint32_t sType; void* pNext; VkPhysicalDeviceFeatures features; } VkPhysicalDeviceFeatures2;
typedef struct VkFeatureChain { uint32_t sType; void* pNext; uint32_t values[64]; } VkFeatureChain;

typedef VkResult (*PFN_vkEnumerateInstanceVersion)(uint32_t*);
typedef VkResult (*PFN_vkEnumerateInstanceExtensionProperties)(const char*, uint32_t*, VkExtensionProperties*);
typedef VkResult (*PFN_vkEnumerateInstanceLayerProperties)(uint32_t*, VkLayerProperties*);
typedef VkResult (*PFN_vkCreateInstance)(const VkInstanceCreateInfo*, const VkAllocationCallbacks*, VkInstance*);
typedef void (*PFN_vkDestroyInstance)(VkInstance, const VkAllocationCallbacks*);
typedef PFN_vkVoidFunction (*PFN_vkGetInstanceProcAddr)(VkInstance, const char*);
typedef VkResult (*PFN_vkEnumeratePhysicalDevices)(VkInstance, uint32_t*, VkPhysicalDevice*);
typedef void (*PFN_vkGetPhysicalDeviceProperties)(VkPhysicalDevice, void*);
typedef void (*PFN_vkGetPhysicalDeviceFeatures)(VkPhysicalDevice, VkPhysicalDeviceFeatures*);
typedef void (*PFN_vkGetPhysicalDeviceFeatures2)(VkPhysicalDevice, VkPhysicalDeviceFeatures2*);
typedef void (*PFN_vkGetPhysicalDeviceProperties2)(VkPhysicalDevice, VkPhysicalDeviceProperties2*);
typedef void (*PFN_vkGetPhysicalDeviceMemoryProperties)(VkPhysicalDevice, VkPhysicalDeviceMemoryProperties*);
typedef void (*PFN_vkGetPhysicalDeviceQueueFamilyProperties)(VkPhysicalDevice, uint32_t*, VkQueueFamilyProperties*);
typedef VkResult (*PFN_vkEnumerateDeviceExtensionProperties)(VkPhysicalDevice, const char*, uint32_t*, VkExtensionProperties*);
typedef void (*PFN_vkGetPhysicalDeviceFormatProperties)(VkPhysicalDevice, int32_t, VkFormatProperties*);
typedef VkResult (*PFN_vkCreateAndroidSurfaceKHR)(VkInstance, const VkAndroidSurfaceCreateInfoKHR*, const VkAllocationCallbacks*, VkSurfaceKHR*);
typedef void (*PFN_vkDestroySurfaceKHR)(VkInstance, VkSurfaceKHR, const VkAllocationCallbacks*);
typedef VkResult (*PFN_vkGetPhysicalDeviceSurfaceCapabilitiesKHR)(VkPhysicalDevice, VkSurfaceKHR, VkSurfaceCapabilitiesKHR*);
typedef VkResult (*PFN_vkGetPhysicalDeviceSurfaceFormatsKHR)(VkPhysicalDevice, VkSurfaceKHR, uint32_t*, VkSurfaceFormatKHR*);
typedef VkResult (*PFN_vkGetPhysicalDeviceSurfacePresentModesKHR)(VkPhysicalDevice, VkSurfaceKHR, uint32_t*, uint32_t*);
typedef VkResult (*PFN_vkGetPhysicalDeviceSurfaceSupportKHR)(VkPhysicalDevice, uint32_t, VkSurfaceKHR, VkBool32*);

#define VK_SUCCESS 0
#define VK_INCOMPLETE 5
#define VK_STRUCTURE_TYPE_APPLICATION_INFO 0
#define VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO 1
#define VK_STRUCTURE_TYPE_ANDROID_SURFACE_CREATE_INFO_KHR 1000008000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2 1000059000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2 1000059001
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_1_PROPERTIES 50
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_2_PROPERTIES 52
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_3_PROPERTIES 54
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_PROPERTIES_EXT 1000218001
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_2_PROPERTIES_EXT 1000332001
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_1_FEATURES 49
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_2_FEATURES 51
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_3_FEATURES 53
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_4_FEATURES 55
#define VK_API_VERSION_1_0 4194304u
#define VK_API_VERSION_MAJOR(v) ((uint32_t)(v) >> 22U)
#define VK_API_VERSION_MINOR(v) (((uint32_t)(v) >> 12U) & 0x3FFU)
#define VK_API_VERSION_PATCH(v) ((uint32_t)(v) & 0xFFFU)
#define VK_TRUE 1u
#define VK_FORMAT_R8G8B8A8_UNORM 37
#define VK_FORMAT_R8G8B8A8_SRGB 43
#define VK_FORMAT_B8G8R8A8_UNORM 44
#define VK_FORMAT_B8G8R8A8_SRGB 50
#define VK_FORMAT_A2R10G10B10_UNORM_PACK32 58
#define VK_FORMAT_A2B10G10R10_UNORM_PACK32 64
#define VK_FORMAT_R16G16B16A16_SFLOAT 97
#define VK_COLOR_SPACE_SRGB_NONLINEAR_KHR 0
#define VK_COLOR_SPACE_DISPLAY_P3_NONLINEAR_EXT 1000104001
#define VK_COLOR_SPACE_EXTENDED_SRGB_LINEAR_EXT 1000104002
#define VK_COLOR_SPACE_DISPLAY_P3_LINEAR_EXT 1000104003
#define VK_COLOR_SPACE_DCI_P3_LINEAR_EXT VK_COLOR_SPACE_DISPLAY_P3_LINEAR_EXT
#define VK_COLOR_SPACE_DCI_P3_NONLINEAR_EXT 1000104004
#define VK_COLOR_SPACE_BT709_LINEAR_EXT 1000104005
#define VK_COLOR_SPACE_BT709_NONLINEAR_EXT 1000104006
#define VK_COLOR_SPACE_BT2020_LINEAR_EXT 1000104007
#define VK_COLOR_SPACE_HDR10_ST2084_EXT 1000104008
#define VK_COLOR_SPACE_DOLBYVISION_EXT 1000104009
#define VK_COLOR_SPACE_HDR10_HLG_EXT 1000104010
#define VK_COLOR_SPACE_ADOBERGB_LINEAR_EXT 1000104011
#define VK_COLOR_SPACE_ADOBERGB_NONLINEAR_EXT 1000104012
#define VK_COLOR_SPACE_PASS_THROUGH_EXT 1000104013
#define VK_COLOR_SPACE_EXTENDED_SRGB_NONLINEAR_EXT 1000104014
#define VK_COLOR_SPACE_DISPLAY_NATIVE_AMD 1000213000

#ifdef __cplusplus
}
#endif
#endif
