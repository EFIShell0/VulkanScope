#ifndef VULKAN_MIN_H
#define VULKAN_MIN_H
#include <stdint.h>
#include <stddef.h>
#ifdef __cplusplus
extern "C" {
#endif

typedef uint32_t VkFlags;
typedef uint32_t VkSampleCountFlags;
typedef uint32_t VkQueueFlags;
typedef uint32_t VkBool32;
typedef uint64_t VkDeviceSize;
typedef struct VkInstance_T* VkInstance;
typedef struct VkPhysicalDevice_T* VkPhysicalDevice;
typedef uint64_t VkSurfaceKHR;
typedef void* VkAllocationCallbacks;
typedef int32_t VkResult;
typedef void* PFN_vkVoidFunction;
#ifndef VK_ERROR_EXTENSION_NOT_PRESENT
#define VK_ERROR_EXTENSION_NOT_PRESENT (-7)
#endif
#ifndef VK_MAX_DEVICE_GROUP_SIZE
#define VK_MAX_DEVICE_GROUP_SIZE 32
#endif

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


typedef uint32_t VkVideoCodecOperationFlagsKHR;
typedef uint32_t VkVideoCapabilityFlagsKHR;
typedef uint32_t StdVideoH264ProfileIdc;
typedef uint32_t StdVideoH264LevelIdc;
typedef uint32_t StdVideoH265ProfileIdc;
typedef uint32_t StdVideoH265LevelIdc;
typedef uint32_t StdVideoVP9Profile;
typedef uint32_t StdVideoVP9Level;
typedef uint32_t StdVideoAV1Profile;
typedef uint32_t StdVideoAV1Level;
typedef int32_t VkStructureType;
typedef struct VkOffset2D { int32_t x; int32_t y; } VkOffset2D;
typedef struct VkComponentMapping { int32_t r; int32_t g; int32_t b; int32_t a; } VkComponentMapping;
typedef struct VkVideoProfileInfoKHR { uint32_t sType; const void* pNext; VkVideoCodecOperationFlagsKHR videoCodecOperation; VkFlags chromaSubsampling; VkFlags lumaBitDepth; VkFlags chromaBitDepth; } VkVideoProfileInfoKHR;
typedef struct VkVideoProfileListInfoKHR { uint32_t sType; const void* pNext; uint32_t profileCount; const VkVideoProfileInfoKHR* pProfiles; } VkVideoProfileListInfoKHR;
typedef struct VkPhysicalDeviceVideoFormatInfoKHR { uint32_t sType; const void* pNext; VkFlags imageUsage; } VkPhysicalDeviceVideoFormatInfoKHR;
typedef struct VkVideoFormatPropertiesKHR { uint32_t sType; void* pNext; int32_t format; VkComponentMapping componentMapping; VkFlags imageCreateFlags; int32_t imageType; int32_t imageTiling; VkFlags imageUsageFlags; } VkVideoFormatPropertiesKHR;
typedef struct VkVideoCapabilitiesKHR { uint32_t sType; void* pNext; VkVideoCapabilityFlagsKHR flags; VkDeviceSize minBitstreamBufferOffsetAlignment; VkDeviceSize minBitstreamBufferSizeAlignment; VkExtent2D pictureAccessGranularity; VkExtent2D minCodedExtent; VkExtent2D maxCodedExtent; uint32_t maxDpbSlots; uint32_t maxActiveReferencePictures; VkExtensionProperties stdHeaderVersion; } VkVideoCapabilitiesKHR;
typedef struct VkVideoDecodeCapabilitiesKHR { uint32_t sType; void* pNext; VkFlags flags; } VkVideoDecodeCapabilitiesKHR;
typedef struct VkVideoEncodeCapabilitiesKHR { uint32_t sType; void* pNext; VkFlags flags; VkFlags rateControlModes; uint32_t maxRateControlLayers; VkDeviceSize maxBitrate; uint32_t maxQualityLevels; VkExtent2D encodeInputPictureGranularity; VkFlags supportedEncodeFeedbackFlags; } VkVideoEncodeCapabilitiesKHR;
typedef struct VkVideoDecodeH264ProfileInfoKHR { uint32_t sType; const void* pNext; StdVideoH264ProfileIdc stdProfileIdc; int32_t pictureLayout; } VkVideoDecodeH264ProfileInfoKHR;
typedef struct VkVideoDecodeH265ProfileInfoKHR { uint32_t sType; const void* pNext; StdVideoH265ProfileIdc stdProfileIdc; } VkVideoDecodeH265ProfileInfoKHR;
typedef struct VkVideoDecodeVP9ProfileInfoKHR { uint32_t sType; const void* pNext; StdVideoVP9Profile stdProfile; } VkVideoDecodeVP9ProfileInfoKHR;
typedef struct VkVideoDecodeAV1ProfileInfoKHR { uint32_t sType; const void* pNext; StdVideoAV1Profile stdProfile; VkBool32 filmGrainSupport; } VkVideoDecodeAV1ProfileInfoKHR;
typedef struct VkVideoDecodeH264CapabilitiesKHR { uint32_t sType; void* pNext; StdVideoH264LevelIdc maxLevelIdc; VkOffset2D fieldOffsetGranularity; } VkVideoDecodeH264CapabilitiesKHR;
typedef struct VkVideoDecodeH265CapabilitiesKHR { uint32_t sType; void* pNext; StdVideoH265LevelIdc maxLevelIdc; } VkVideoDecodeH265CapabilitiesKHR;
typedef struct VkVideoDecodeVP9CapabilitiesKHR { uint32_t sType; void* pNext; StdVideoVP9Level maxLevel; } VkVideoDecodeVP9CapabilitiesKHR;
typedef struct VkVideoDecodeAV1CapabilitiesKHR { uint32_t sType; void* pNext; StdVideoAV1Level maxLevel; } VkVideoDecodeAV1CapabilitiesKHR;
typedef struct VkVideoEncodeH264ProfileInfoKHR { uint32_t sType; const void* pNext; StdVideoH264ProfileIdc stdProfileIdc; } VkVideoEncodeH264ProfileInfoKHR;
typedef struct VkVideoEncodeH265ProfileInfoKHR { uint32_t sType; const void* pNext; StdVideoH265ProfileIdc stdProfileIdc; } VkVideoEncodeH265ProfileInfoKHR;
typedef struct VkVideoEncodeAV1ProfileInfoKHR { uint32_t sType; const void* pNext; StdVideoAV1Profile stdProfile; } VkVideoEncodeAV1ProfileInfoKHR;
typedef struct VkPhysicalDeviceVideoEncodeFeedback2FeaturesKHR { uint32_t sType; void* pNext; VkBool32 videoEncodeFeedback2; } VkPhysicalDeviceVideoEncodeFeedback2FeaturesKHR;
typedef struct VkPhysicalDeviceCooperativeMatrixConversionFeaturesQCOM { uint32_t sType; void* pNext; VkBool32 cooperativeMatrixConversion; } VkPhysicalDeviceCooperativeMatrixConversionFeaturesQCOM;
typedef struct VkPhysicalDeviceElapsedTimerQueryFeaturesQCOM { uint32_t sType; void* pNext; VkBool32 elapsedTimerQuery; } VkPhysicalDeviceElapsedTimerQueryFeaturesQCOM;
typedef struct VkPhysicalDeviceQueuePerfHintFeaturesQCOM { uint32_t sType; void* pNext; VkBool32 queuePerfHint; } VkPhysicalDeviceQueuePerfHintFeaturesQCOM;
typedef struct VkPhysicalDeviceQueuePerfHintPropertiesQCOM { uint32_t sType; void* pNext; VkQueueFlags supportedQueues; } VkPhysicalDeviceQueuePerfHintPropertiesQCOM;



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


typedef struct VkPhysicalDeviceDescriptorHeapFeaturesEXT { uint32_t sType; void* pNext; VkBool32 descriptorHeap; VkBool32 descriptorHeapCaptureReplay; } VkPhysicalDeviceDescriptorHeapFeaturesEXT;
typedef struct VkPhysicalDeviceDescriptorHeapPropertiesEXT {
    uint32_t sType; void* pNext; uint64_t samplerHeapAlignment; uint64_t resourceHeapAlignment; uint64_t maxSamplerHeapSize; uint64_t maxResourceHeapSize;
    uint64_t minSamplerHeapReservedRange; uint64_t minSamplerHeapReservedRangeWithEmbedded; uint64_t minResourceHeapReservedRange;
    uint64_t samplerDescriptorSize; uint64_t imageDescriptorSize; uint64_t bufferDescriptorSize; uint64_t samplerDescriptorAlignment;
    uint64_t imageDescriptorAlignment; uint64_t bufferDescriptorAlignment; uint64_t maxPushDataSize; size_t imageCaptureReplayOpaqueDataSize;
    uint32_t maxDescriptorHeapEmbeddedSamplers; uint32_t samplerYcbcrConversionCount; VkBool32 sparseDescriptorHeaps; VkBool32 protectedDescriptorHeaps;
} VkPhysicalDeviceDescriptorHeapPropertiesEXT;
typedef struct VkPhysicalDeviceTextureCompressionASTC3DFeaturesEXT { uint32_t sType; void* pNext; VkBool32 textureCompressionASTC_3D; } VkPhysicalDeviceTextureCompressionASTC3DFeaturesEXT;
typedef struct VkPhysicalDeviceShaderLongVectorFeaturesEXT { uint32_t sType; void* pNext; VkBool32 longVector; } VkPhysicalDeviceShaderLongVectorFeaturesEXT;
typedef struct VkPhysicalDeviceShaderLongVectorPropertiesEXT { uint32_t sType; void* pNext; uint32_t maxVectorComponents; } VkPhysicalDeviceShaderLongVectorPropertiesEXT;
typedef struct VkPhysicalDeviceShaderSubgroupPartitionedFeaturesEXT { uint32_t sType; void* pNext; VkBool32 shaderSubgroupPartitioned; } VkPhysicalDeviceShaderSubgroupPartitionedFeaturesEXT;
typedef struct VkPhysicalDeviceInternallySynchronizedQueuesFeaturesKHR { uint32_t sType; void* pNext; VkBool32 internallySynchronizedQueues; } VkPhysicalDeviceInternallySynchronizedQueuesFeaturesKHR;
typedef struct VkPhysicalDevicePushConstantBankFeaturesNV { uint32_t sType; void* pNext; VkBool32 pushConstantBank; } VkPhysicalDevicePushConstantBankFeaturesNV;
typedef struct VkPhysicalDevicePushConstantBankPropertiesNV { uint32_t sType; void* pNext; uint32_t maxGraphicsPushConstantBanks; uint32_t maxComputePushConstantBanks; uint32_t maxGraphicsPushDataBanks; uint32_t maxComputePushDataBanks; } VkPhysicalDevicePushConstantBankPropertiesNV;
typedef struct VkPhysicalDeviceComputeOccupancyPriorityFeaturesNV { uint32_t sType; void* pNext; VkBool32 computeOccupancyPriority; } VkPhysicalDeviceComputeOccupancyPriorityFeaturesNV;


typedef struct VkPhysicalDeviceDataGraphNeuralAcceleratorStatisticsFeaturesARM { uint32_t sType; void* pNext; VkBool32 dataGraphNeuralAcceleratorStatistics; } VkPhysicalDeviceDataGraphNeuralAcceleratorStatisticsFeaturesARM;
typedef struct VkPhysicalDeviceShaderInstrumentationFeaturesARM { uint32_t sType; void* pNext; VkBool32 shaderInstrumentation; } VkPhysicalDeviceShaderInstrumentationFeaturesARM;
typedef struct VkPhysicalDeviceShaderInstrumentationPropertiesARM { uint32_t sType; void* pNext; uint32_t numMetrics; VkBool32 perBasicBlockGranularity; } VkPhysicalDeviceShaderInstrumentationPropertiesARM;
typedef struct VkPhysicalDeviceMultisampledRenderToSwapchainFeaturesEXT { uint32_t sType; void* pNext; VkBool32 multisampledRenderToSwapchain; } VkPhysicalDeviceMultisampledRenderToSwapchainFeaturesEXT;
typedef struct VkPhysicalDevicePrimitiveRestartIndexFeaturesEXT { uint32_t sType; void* pNext; VkBool32 primitiveRestartIndex; } VkPhysicalDevicePrimitiveRestartIndexFeaturesEXT;
typedef struct VkPhysicalDeviceShaderSplitBarrierFeaturesEXT { uint32_t sType; void* pNext; VkBool32 shaderSplitBarrier; } VkPhysicalDeviceShaderSplitBarrierFeaturesEXT;
typedef struct VkPhysicalDeviceShaderSplitBarrierPropertiesEXT { uint32_t sType; void* pNext; uint32_t splitBarrierReservedSharedMemory; } VkPhysicalDeviceShaderSplitBarrierPropertiesEXT;
typedef struct VkPhysicalDeviceFaultFeaturesKHR { uint32_t sType; void* pNext; VkBool32 deviceFault; VkBool32 deviceFaultVendorBinary; VkBool32 deviceFaultReportMasked; VkBool32 deviceFaultDeviceLostOnMasked; } VkPhysicalDeviceFaultFeaturesKHR;
typedef struct VkPhysicalDeviceFaultPropertiesKHR { uint32_t sType; void* pNext; uint32_t maxDeviceFaultCount; } VkPhysicalDeviceFaultPropertiesKHR;
typedef struct VkPhysicalDeviceOpacityMicromapFeaturesKHR { uint32_t sType; void* pNext; VkBool32 micromap; } VkPhysicalDeviceOpacityMicromapFeaturesKHR;
typedef struct VkPhysicalDeviceOpacityMicromapPropertiesKHR { uint32_t sType; void* pNext; uint32_t maxOpacity2StateSubdivisionLevel; uint32_t maxOpacity4StateSubdivisionLevel; uint32_t maxOpacityLossy4StateSubdivisionLevel; uint64_t maxMicromapTriangles; } VkPhysicalDeviceOpacityMicromapPropertiesKHR;
typedef struct VkPhysicalDeviceShaderAbortFeaturesKHR { uint32_t sType; void* pNext; VkBool32 shaderAbort; } VkPhysicalDeviceShaderAbortFeaturesKHR;
typedef struct VkPhysicalDeviceShaderAbortPropertiesKHR { uint32_t sType; void* pNext; uint64_t maxShaderAbortMessageSize; } VkPhysicalDeviceShaderAbortPropertiesKHR;
typedef struct VkPhysicalDeviceShaderConstantDataFeaturesKHR { uint32_t sType; void* pNext; VkBool32 shaderConstantData; } VkPhysicalDeviceShaderConstantDataFeaturesKHR;
typedef struct VkPhysicalDeviceCooperativeMatrixDecodeVectorFeaturesNV { uint32_t sType; void* pNext; VkBool32 cooperativeMatrixDecodeVector; } VkPhysicalDeviceCooperativeMatrixDecodeVectorFeaturesNV;
typedef struct VkPhysicalDeviceImageProcessing3FeaturesQCOM { uint32_t sType; void* pNext; VkBool32 imageGatherLinear; VkBool32 imageGatherExtendedModes; VkBool32 blockMatchExtendedClampToEdge; } VkPhysicalDeviceImageProcessing3FeaturesQCOM;
typedef struct VkPhysicalDeviceShaderMultipleWaitQueuesFeaturesQCOM { uint32_t sType; void* pNext; VkBool32 shaderMultipleWaitQueues; } VkPhysicalDeviceShaderMultipleWaitQueuesFeaturesQCOM;
typedef struct VkPhysicalDeviceShaderMultipleWaitQueuesPropertiesQCOM { uint32_t sType; void* pNext; uint32_t maxShaderWaitQueues; } VkPhysicalDeviceShaderMultipleWaitQueuesPropertiesQCOM;
typedef struct VkPhysicalDeviceShaderMixedFloatDotProductFeaturesVALVE { uint32_t sType; void* pNext; VkBool32 shaderMixedFloatDotProductFloat16AccFloat32; VkBool32 shaderMixedFloatDotProductFloat16AccFloat16; VkBool32 shaderMixedFloatDotProductBFloat16Acc; VkBool32 shaderMixedFloatDotProductFloat8AccFloat32; } VkPhysicalDeviceShaderMixedFloatDotProductFeaturesVALVE;
typedef struct VkPhysicalDeviceThrottleHintFeaturesSEC { uint32_t sType; void* pNext; VkBool32 throttleHint; } VkPhysicalDeviceThrottleHintFeaturesSEC;
typedef struct VkPhysicalDeviceVulkan14Features {
    uint32_t sType; void* pNext;
    VkBool32 globalPriorityQuery; VkBool32 shaderSubgroupRotate; VkBool32 shaderSubgroupRotateClustered;
    VkBool32 shaderFloatControls2; VkBool32 shaderExpectAssume; VkBool32 rectangularLines;
    VkBool32 bresenhamLines; VkBool32 smoothLines; VkBool32 stippledRectangularLines;
    VkBool32 stippledBresenhamLines; VkBool32 stippledSmoothLines;
    VkBool32 vertexAttributeInstanceRateDivisor; VkBool32 vertexAttributeInstanceRateZeroDivisor;
    VkBool32 indexTypeUint8; VkBool32 dynamicRenderingLocalRead; VkBool32 maintenance5;
    VkBool32 maintenance6; VkBool32 pipelineProtectedAccess; VkBool32 pipelineRobustness;
    VkBool32 hostImageCopy; VkBool32 pushDescriptor;
} VkPhysicalDeviceVulkan14Features;

typedef struct VkPhysicalDeviceVulkan14Properties {
    uint32_t sType; void* pNext; uint32_t lineSubPixelPrecisionBits; uint32_t maxVertexAttribDivisor; VkBool32 supportsNonZeroFirstInstance;
    uint32_t maxPushDescriptors; VkBool32 dynamicRenderingLocalReadDepthStencilAttachments; VkBool32 dynamicRenderingLocalReadMultisampledAttachments;
    VkBool32 earlyFragmentMultisampleCoverageAfterSampleCounting; VkBool32 earlyFragmentSampleMaskTestBeforeSampleCounting; VkBool32 depthStencilSwizzleOneSupport;
    VkBool32 polygonModePointSize; VkBool32 nonStrictSinglePixelWideLinesUseParallelogram; VkBool32 nonStrictWideLinesUseParallelogram;
    VkBool32 blockTexelViewCompatibleMultipleLayers; uint32_t maxCombinedImageSamplerDescriptorCount; VkBool32 fragmentShadingRateClampCombinerInputs;
    uint32_t defaultRobustnessStorageBuffers; uint32_t defaultRobustnessUniformBuffers; uint32_t defaultRobustnessVertexInputs; uint32_t defaultRobustnessImages;
    uint32_t copySrcLayoutCount; int32_t* pCopySrcLayouts; uint32_t copyDstLayoutCount; int32_t* pCopyDstLayouts;
    uint8_t optimalTilingLayoutUUID[16]; VkBool32 identicalMemoryTypeRequirements;
} VkPhysicalDeviceVulkan14Properties;

typedef struct VkPhysicalDeviceSparsePropertiesLayout {
    VkBool32 residencyStandard2DBlockShape; VkBool32 residencyStandard2DMultisampleBlockShape;
    VkBool32 residencyStandard3DBlockShape; VkBool32 residencyAlignedMipSize; VkBool32 residencyNonResidentStrict;
} VkPhysicalDeviceSparsePropertiesLayout;

typedef struct VkPhysicalDeviceLimitsLayout {
    uint32_t maxImageDimension1D; uint32_t maxImageDimension2D; uint32_t maxImageDimension3D; uint32_t maxImageDimensionCube;
    uint32_t maxImageArrayLayers; uint32_t maxTexelBufferElements; uint32_t maxUniformBufferRange; uint32_t maxStorageBufferRange;
    uint32_t maxPushConstantsSize; uint32_t maxMemoryAllocationCount; uint32_t maxSamplerAllocationCount; uint64_t bufferImageGranularity;
    uint64_t sparseAddressSpaceSize; uint32_t maxBoundDescriptorSets; uint32_t maxPerStageDescriptorSamplers; uint32_t maxPerStageDescriptorUniformBuffers;
    uint32_t maxPerStageDescriptorStorageBuffers; uint32_t maxPerStageDescriptorSampledImages; uint32_t maxPerStageDescriptorStorageImages;
    uint32_t maxPerStageDescriptorInputAttachments; uint32_t maxPerStageResources; uint32_t maxDescriptorSetSamplers;
    uint32_t maxDescriptorSetUniformBuffers; uint32_t maxDescriptorSetUniformBuffersDynamic; uint32_t maxDescriptorSetStorageBuffers;
    uint32_t maxDescriptorSetStorageBuffersDynamic; uint32_t maxDescriptorSetSampledImages; uint32_t maxDescriptorSetStorageImages;
    uint32_t maxDescriptorSetInputAttachments; uint32_t maxVertexInputAttributes; uint32_t maxVertexInputBindings; uint32_t maxVertexInputAttributeOffset;
    uint32_t maxVertexInputBindingStride; uint32_t maxVertexOutputComponents; uint32_t maxTessellationGenerationLevel; uint32_t maxTessellationPatchSize;
    uint32_t maxTessellationControlPerVertexInputComponents; uint32_t maxTessellationControlPerVertexOutputComponents;
    uint32_t maxTessellationControlPerPatchOutputComponents; uint32_t maxTessellationControlTotalOutputComponents;
    uint32_t maxTessellationEvaluationInputComponents; uint32_t maxTessellationEvaluationOutputComponents; uint32_t maxGeometryShaderInvocations;
    uint32_t maxGeometryInputComponents; uint32_t maxGeometryOutputComponents; uint32_t maxGeometryOutputVertices; uint32_t maxGeometryTotalOutputComponents;
    uint32_t maxFragmentInputComponents; uint32_t maxFragmentOutputAttachments; uint32_t maxFragmentDualSrcAttachments; uint32_t maxFragmentCombinedOutputResources;
    uint32_t maxComputeSharedMemorySize; uint32_t maxComputeWorkGroupCount[3]; uint32_t maxComputeWorkGroupInvocations; uint32_t maxComputeWorkGroupSize[3];
    uint32_t subPixelPrecisionBits; uint32_t subTexelPrecisionBits; uint32_t mipmapPrecisionBits; uint32_t maxDrawIndexedIndexValue; uint32_t maxDrawIndirectCount;
    float maxSamplerLodBias; float maxSamplerAnisotropy; uint32_t maxViewports; uint32_t maxViewportDimensions[2]; float viewportBoundsRange[2];
    uint32_t viewportSubPixelBits; size_t minMemoryMapAlignment; uint64_t minTexelBufferOffsetAlignment; uint64_t minUniformBufferOffsetAlignment;
    uint64_t minStorageBufferOffsetAlignment; int32_t minTexelOffset; uint32_t maxTexelOffset; int32_t minTexelGatherOffset; uint32_t maxTexelGatherOffset;
    float minInterpolationOffset; float maxInterpolationOffset; uint32_t subPixelInterpolationOffsetBits; uint32_t maxFramebufferWidth; uint32_t maxFramebufferHeight;
    uint32_t maxFramebufferLayers; VkSampleCountFlags framebufferColorSampleCounts; VkSampleCountFlags framebufferDepthSampleCounts;
    VkSampleCountFlags framebufferStencilSampleCounts; VkSampleCountFlags framebufferNoAttachmentsSampleCounts; uint32_t maxColorAttachments;
    VkSampleCountFlags sampledImageColorSampleCounts; VkSampleCountFlags sampledImageIntegerSampleCounts; VkSampleCountFlags sampledImageDepthSampleCounts;
    VkSampleCountFlags sampledImageStencilSampleCounts; VkSampleCountFlags storageImageSampleCounts; uint32_t maxSampleMaskWords; VkBool32 timestampComputeAndGraphics;
    float timestampPeriod; uint32_t maxClipDistances; uint32_t maxCullDistances; uint32_t maxCombinedClipAndCullDistances; uint32_t discreteQueuePriorities;
    float pointSizeRange[2]; float lineWidthRange[2]; float pointSizeGranularity; float lineWidthGranularity; VkBool32 strictLines; VkBool32 standardSampleLocations;
    uint64_t optimalBufferCopyOffsetAlignment; uint64_t optimalBufferCopyRowPitchAlignment; uint64_t nonCoherentAtomSize;
} VkPhysicalDeviceLimitsLayout;

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


typedef struct VkPhysicalDeviceMaintenance7PropertiesKHR { uint32_t sType; void* pNext; VkBool32 robustFragmentShadingRateAttachmentAccess; VkBool32 separateDepthStencilAttachmentAccess; uint32_t maxDescriptorSetTotalUniformBuffersDynamic; uint32_t maxDescriptorSetTotalStorageBuffersDynamic; uint32_t maxDescriptorSetTotalBuffersDynamic; uint32_t maxDescriptorSetUpdateAfterBindTotalUniformBuffersDynamic; uint32_t maxDescriptorSetUpdateAfterBindTotalStorageBuffersDynamic; uint32_t maxDescriptorSetUpdateAfterBindTotalBuffersDynamic; } VkPhysicalDeviceMaintenance7PropertiesKHR;
typedef struct VkPhysicalDeviceMaintenance9PropertiesKHR { uint32_t sType; void* pNext; VkBool32 image2DViewOf3DSparse; uint32_t defaultVertexAttributeValue; } VkPhysicalDeviceMaintenance9PropertiesKHR;
typedef struct VkPhysicalDeviceMaintenance10PropertiesKHR { uint32_t sType; void* pNext; VkBool32 rgba4OpaqueBlackSwizzled; VkBool32 resolveSrgbFormatAppliesTransferFunction; VkBool32 resolveSrgbFormatSupportsTransferFunctionControl; } VkPhysicalDeviceMaintenance10PropertiesKHR;
typedef struct VkPhysicalDeviceMaintenance7FeaturesKHR { uint32_t sType; void* pNext; VkBool32 maintenance7; } VkPhysicalDeviceMaintenance7FeaturesKHR;
typedef struct VkPhysicalDeviceMaintenance8FeaturesKHR { uint32_t sType; void* pNext; VkBool32 maintenance8; } VkPhysicalDeviceMaintenance8FeaturesKHR;
typedef struct VkPhysicalDeviceMaintenance9FeaturesKHR { uint32_t sType; void* pNext; VkBool32 maintenance9; } VkPhysicalDeviceMaintenance9FeaturesKHR;
typedef struct VkPhysicalDeviceMaintenance10FeaturesKHR { uint32_t sType; void* pNext; VkBool32 maintenance10; } VkPhysicalDeviceMaintenance10FeaturesKHR;
typedef struct VkPhysicalDevicePresentModeFifoLatestReadyFeaturesKHR { uint32_t sType; void* pNext; VkBool32 presentModeFifoLatestReady; } VkPhysicalDevicePresentModeFifoLatestReadyFeaturesKHR;
typedef struct VkPhysicalDevicePresentId2FeaturesKHR { uint32_t sType; void* pNext; VkBool32 presentId2; } VkPhysicalDevicePresentId2FeaturesKHR;
typedef struct VkPhysicalDevicePresentWait2FeaturesKHR { uint32_t sType; void* pNext; VkBool32 presentWait2; } VkPhysicalDevicePresentWait2FeaturesKHR;
typedef struct VkPhysicalDevicePipelineBinaryFeaturesKHR { uint32_t sType; void* pNext; VkBool32 pipelineBinaries; } VkPhysicalDevicePipelineBinaryFeaturesKHR;
typedef struct VkPhysicalDevicePipelineBinaryPropertiesKHR { uint32_t sType; void* pNext; VkBool32 pipelineBinaryInternalCache; VkBool32 pipelineBinaryInternalCacheControl; VkBool32 pipelineBinaryPrefersInternalCache; VkBool32 pipelineBinaryPrecompiledInternalCache; VkBool32 pipelineBinaryCompressedData; } VkPhysicalDevicePipelineBinaryPropertiesKHR;
typedef struct VkPhysicalDeviceCooperativeMatrixFeaturesKHR { uint32_t sType; void* pNext; VkBool32 cooperativeMatrix; VkBool32 cooperativeMatrixRobustBufferAccess; } VkPhysicalDeviceCooperativeMatrixFeaturesKHR;
typedef struct VkPhysicalDeviceCooperativeMatrixPropertiesKHR { uint32_t sType; void* pNext; uint32_t cooperativeMatrixSupportedStages; } VkPhysicalDeviceCooperativeMatrixPropertiesKHR;


typedef struct VkQueueFamilyOptimalImageTransferGranularityPropertiesKHR { uint32_t sType; void* pNext; VkExtent3D optimalImageTransferGranularity; } VkQueueFamilyOptimalImageTransferGranularityPropertiesKHR;
typedef struct VkQueueFamilyVideoPropertiesKHR { uint32_t sType; void* pNext; VkFlags videoCodecOperations; } VkQueueFamilyVideoPropertiesKHR;
typedef struct VkQueueFamilyProperties2 { uint32_t sType; void* pNext; VkQueueFamilyProperties queueFamilyProperties; } VkQueueFamilyProperties2;
typedef struct VkFormatProperties2 { uint32_t sType; void* pNext; VkFormatProperties formatProperties; } VkFormatProperties2;
typedef struct VkFormatProperties3 { uint32_t sType; void* pNext; uint64_t linearTilingFeatures; uint64_t optimalTilingFeatures; uint64_t bufferFeatures; } VkFormatProperties3;
typedef struct VkPhysicalDeviceImageFormatInfo2 { uint32_t sType; void* pNext; int32_t format; uint32_t type; uint32_t tiling; VkFlags usage; VkFlags flags; } VkPhysicalDeviceImageFormatInfo2;
typedef struct VkImageFormatProperties { VkExtent3D maxExtent; uint32_t maxMipLevels; uint32_t maxArrayLayers; VkSampleCountFlags sampleCounts; VkDeviceSize maxResourceSize; } VkImageFormatProperties;
typedef struct VkImageFormatProperties2 { uint32_t sType; void* pNext; VkImageFormatProperties imageFormatProperties; } VkImageFormatProperties2;
typedef struct VkPhysicalDeviceMemoryProperties2 { uint32_t sType; void* pNext; VkPhysicalDeviceMemoryProperties memoryProperties; } VkPhysicalDeviceMemoryProperties2;
typedef struct VkPhysicalDeviceMemoryBudgetPropertiesEXT { uint32_t sType; void* pNext; VkDeviceSize heapBudget[16]; VkDeviceSize heapUsage[16]; } VkPhysicalDeviceMemoryBudgetPropertiesEXT;
typedef struct VkPhysicalDeviceToolProperties { uint32_t sType; void* pNext; char name[256]; uint32_t version; uint32_t purposes; char description[256]; char layer[256]; } VkPhysicalDeviceToolProperties;
typedef struct VkPhysicalDeviceGroupProperties { uint32_t sType; void* pNext; uint32_t physicalDeviceCount; VkPhysicalDevice physicalDevices[32]; VkBool32 subsetAllocation; } VkPhysicalDeviceGroupProperties;
typedef struct VkExternalMemoryProperties { VkFlags externalMemoryFeatures; VkFlags exportFromImportedHandleTypes; VkFlags compatibleHandleTypes; } VkExternalMemoryProperties;
typedef struct VkPhysicalDeviceExternalImageFormatInfo { uint32_t sType; const void* pNext; VkFlags handleType; } VkPhysicalDeviceExternalImageFormatInfo;
typedef struct VkExternalImageFormatProperties { uint32_t sType; void* pNext; VkExternalMemoryProperties externalMemoryProperties; } VkExternalImageFormatProperties;
typedef struct VkPhysicalDeviceDataGraphOpticalFlowFeaturesARM { uint32_t sType; void* pNext; VkBool32 dataGraphOpticalFlow; } VkPhysicalDeviceDataGraphOpticalFlowFeaturesARM;
typedef struct VkPhysicalDevicePipelineCacheIncrementalModeFeaturesSEC { uint32_t sType; void* pNext; VkBool32 pipelineCacheIncrementalMode; } VkPhysicalDevicePipelineCacheIncrementalModeFeaturesSEC;
typedef struct VkPhysicalDeviceExtendedFlagsFeaturesKHR { uint32_t sType; void* pNext; VkBool32 extendedFlags; } VkPhysicalDeviceExtendedFlagsFeaturesKHR;
typedef struct VkPhysicalDeviceShaderOcpMicroscalingTypesFeaturesEXT { uint32_t sType; void* pNext; VkBool32 shaderOcpMicroscalingTypes; } VkPhysicalDeviceShaderOcpMicroscalingTypesFeaturesEXT;

typedef struct VkPhysicalDeviceExternalBufferInfo { uint32_t sType; const void* pNext; VkFlags flags; VkFlags usage; VkFlags handleType; } VkPhysicalDeviceExternalBufferInfo;
typedef struct VkExternalBufferProperties { uint32_t sType; void* pNext; VkExternalMemoryProperties externalMemoryProperties; } VkExternalBufferProperties;
typedef struct VkPhysicalDeviceExternalFenceInfo { uint32_t sType; const void* pNext; VkFlags handleType; } VkPhysicalDeviceExternalFenceInfo;
typedef struct VkExternalFenceProperties { uint32_t sType; void* pNext; VkFlags exportFromImportedHandleTypes; VkFlags compatibleHandleTypes; VkFlags externalFenceFeatures; } VkExternalFenceProperties;
typedef struct VkPhysicalDeviceExternalSemaphoreInfo { uint32_t sType; const void* pNext; VkFlags handleType; } VkPhysicalDeviceExternalSemaphoreInfo;
typedef struct VkExternalSemaphoreProperties { uint32_t sType; void* pNext; VkFlags exportFromImportedHandleTypes; VkFlags compatibleHandleTypes; VkFlags externalSemaphoreFeatures; } VkExternalSemaphoreProperties;
typedef struct VkPhysicalDeviceSparseImageFormatInfo2 { uint32_t sType; const void* pNext; int32_t format; uint32_t type; VkSampleCountFlags samples; VkFlags usage; uint32_t tiling; } VkPhysicalDeviceSparseImageFormatInfo2;
typedef struct VkSparseImageFormatProperties { VkFlags aspectMask; VkExtent3D imageGranularity; uint32_t flags; VkDeviceSize mipTailFirstLod; VkDeviceSize mipTailSize; VkDeviceSize mipTailOffset; VkDeviceSize mipTailStride; } VkSparseImageFormatProperties;
typedef struct VkSparseImageFormatProperties2 { uint32_t sType; void* pNext; VkSparseImageFormatProperties properties; } VkSparseImageFormatProperties2;
typedef struct VkPhysicalDeviceMaintenance11FeaturesKHR { uint32_t sType; void* pNext; VkBool32 maintenance11; } VkPhysicalDeviceMaintenance11FeaturesKHR;
typedef struct VkPhysicalDeviceShaderUniformBufferUnsizedArrayFeaturesEXT { uint32_t sType; void* pNext; VkBool32 shaderUniformBufferUnsizedArray; } VkPhysicalDeviceShaderUniformBufferUnsizedArrayFeaturesEXT;
typedef struct VkPhysicalDeviceDeviceAddressCommandsFeaturesKHR { uint32_t sType; void* pNext; VkBool32 deviceAddressCommands; } VkPhysicalDeviceDeviceAddressCommandsFeaturesKHR;

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
typedef VkResult (*PFN_vkGetPhysicalDeviceVideoCapabilitiesKHR)(VkPhysicalDevice, const VkVideoProfileInfoKHR*, VkVideoCapabilitiesKHR*);
typedef VkResult (*PFN_vkGetPhysicalDeviceVideoFormatPropertiesKHR)(VkPhysicalDevice, const VkPhysicalDeviceVideoFormatInfoKHR*, uint32_t*, VkVideoFormatPropertiesKHR*);
typedef void (*PFN_vkGetPhysicalDeviceQueueFamilyProperties)(VkPhysicalDevice, uint32_t*, VkQueueFamilyProperties*);
typedef VkResult (*PFN_vkEnumerateDeviceExtensionProperties)(VkPhysicalDevice, const char*, uint32_t*, VkExtensionProperties*);
typedef VkResult (*PFN_vkEnumerateDeviceLayerProperties)(VkPhysicalDevice, uint32_t*, VkLayerProperties*);
typedef void (*PFN_vkGetPhysicalDeviceToolProperties)(VkPhysicalDevice, uint32_t*, VkPhysicalDeviceToolProperties*);
typedef void (*PFN_vkGetPhysicalDeviceQueueFamilyProperties2)(VkPhysicalDevice, uint32_t*, VkQueueFamilyProperties2*);
typedef void (*PFN_vkGetPhysicalDeviceFormatProperties2)(VkPhysicalDevice, int32_t, VkFormatProperties2*);
typedef VkResult (*PFN_vkGetPhysicalDeviceImageFormatProperties2)(VkPhysicalDevice, const VkPhysicalDeviceImageFormatInfo2*, VkImageFormatProperties2*);
typedef void (*PFN_vkGetPhysicalDeviceMemoryProperties2)(VkPhysicalDevice, VkPhysicalDeviceMemoryProperties2*);
typedef void (*PFN_vkGetPhysicalDeviceExternalBufferProperties)(VkPhysicalDevice, const VkPhysicalDeviceExternalBufferInfo*, VkExternalBufferProperties*);
typedef void (*PFN_vkGetPhysicalDeviceExternalFenceProperties)(VkPhysicalDevice, const VkPhysicalDeviceExternalFenceInfo*, VkExternalFenceProperties*);
typedef void (*PFN_vkGetPhysicalDeviceExternalSemaphoreProperties)(VkPhysicalDevice, const VkPhysicalDeviceExternalSemaphoreInfo*, VkExternalSemaphoreProperties*);
typedef void (*PFN_vkGetPhysicalDeviceSparseImageFormatProperties2)(VkPhysicalDevice, const VkPhysicalDeviceSparseImageFormatInfo2*, uint32_t*, VkSparseImageFormatProperties2*);
typedef VkResult (*PFN_vkEnumeratePhysicalDeviceGroups)(VkInstance, uint32_t*, VkPhysicalDeviceGroupProperties*);

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
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TOOL_PROPERTIES 1000245000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MEMORY_BUDGET_PROPERTIES_EXT 1000237000
#define VK_STRUCTURE_TYPE_FORMAT_PROPERTIES_2 1000059002
#define VK_STRUCTURE_TYPE_FORMAT_PROPERTIES_3 1000360000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_FORMAT_INFO_2 1000059004
#define VK_STRUCTURE_TYPE_IMAGE_FORMAT_PROPERTIES_2 1000059003
#define VK_STRUCTURE_TYPE_QUEUE_FAMILY_PROPERTIES_2 1000059005
#define VK_STRUCTURE_TYPE_VIDEO_PROFILE_INFO_KHR 1000023000
#define VK_STRUCTURE_TYPE_VIDEO_CAPABILITIES_KHR 1000023001
#define VK_STRUCTURE_TYPE_QUEUE_FAMILY_VIDEO_PROPERTIES_KHR 1000023012
#define VK_STRUCTURE_TYPE_VIDEO_PROFILE_LIST_INFO_KHR 1000023013
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_FORMAT_INFO_KHR 1000023014
#define VK_STRUCTURE_TYPE_VIDEO_FORMAT_PROPERTIES_KHR 1000023015
#define VK_STRUCTURE_TYPE_VIDEO_DECODE_CAPABILITIES_KHR 1000024001
#define VK_STRUCTURE_TYPE_VIDEO_ENCODE_H264_CAPABILITIES_KHR 1000038000
#define VK_STRUCTURE_TYPE_VIDEO_ENCODE_H264_PROFILE_INFO_KHR 1000038007
#define VK_STRUCTURE_TYPE_VIDEO_ENCODE_H265_CAPABILITIES_KHR 1000039000
#define VK_STRUCTURE_TYPE_VIDEO_ENCODE_H265_PROFILE_INFO_KHR 1000039007
#define VK_STRUCTURE_TYPE_VIDEO_ENCODE_CAPABILITIES_KHR 1000299003
#define VK_STRUCTURE_TYPE_VIDEO_DECODE_H264_CAPABILITIES_KHR 1000040000
#define VK_STRUCTURE_TYPE_VIDEO_DECODE_H264_PROFILE_INFO_KHR 1000040003
#define VK_STRUCTURE_TYPE_VIDEO_DECODE_H265_CAPABILITIES_KHR 1000187000
#define VK_STRUCTURE_TYPE_VIDEO_DECODE_H265_PROFILE_INFO_KHR 1000187003
#define VK_STRUCTURE_TYPE_VIDEO_DECODE_AV1_CAPABILITIES_KHR 1000512000
#define VK_STRUCTURE_TYPE_VIDEO_DECODE_AV1_PROFILE_INFO_KHR 1000512003
#define VK_STRUCTURE_TYPE_VIDEO_ENCODE_AV1_CAPABILITIES_KHR 1000513000
#define VK_STRUCTURE_TYPE_VIDEO_ENCODE_AV1_PROFILE_INFO_KHR 1000513005
#define VK_STRUCTURE_TYPE_VIDEO_DECODE_VP9_CAPABILITIES_KHR 1000514001
#define VK_STRUCTURE_TYPE_VIDEO_DECODE_VP9_PROFILE_INFO_KHR 1000514003
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MEMORY_PROPERTIES_2 1000059006
#define VK_STRUCTURE_TYPE_SPARSE_IMAGE_FORMAT_PROPERTIES_2 1000059007
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SPARSE_IMAGE_FORMAT_INFO_2 1000059008
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_GROUP_PROPERTIES 1000070000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_SEMAPHORE_INFO 1000076000
#define VK_STRUCTURE_TYPE_EXTERNAL_SEMAPHORE_PROPERTIES 1000076001
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_IMAGE_FORMAT_INFO 1000071000
#define VK_STRUCTURE_TYPE_EXTERNAL_IMAGE_FORMAT_PROPERTIES 1000071001
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_BUFFER_INFO 1000071002
#define VK_STRUCTURE_TYPE_EXTERNAL_BUFFER_PROPERTIES 1000071003
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_FENCE_INFO 1000115002
#define VK_STRUCTURE_TYPE_EXTERNAL_FENCE_PROPERTIES 1000115003
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_11_FEATURES_KHR 1000657000
#define VK_STRUCTURE_TYPE_QUEUE_FAMILY_OPTIMAL_IMAGE_TRANSFER_GRANULARITY_PROPERTIES_KHR 1000657001
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEVICE_ADDRESS_COMMANDS_FEATURES_KHR 1000318006
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_UNIFORM_BUFFER_UNSIZED_ARRAY_FEATURES_EXT 1000642000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DATA_GRAPH_OPTICAL_FLOW_FEATURES_ARM 1000631000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_CACHE_INCREMENTAL_MODE_FEATURES_SEC 1000637000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_FLAGS_FEATURES_KHR 1000668004
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_OCP_MICROSCALING_TYPES_FEATURES_EXT 1000672000

#define VK_VIDEO_CODEC_OPERATION_DECODE_H264_BIT_KHR 0x00000001u
#define VK_VIDEO_CODEC_OPERATION_DECODE_H265_BIT_KHR 0x00000002u
#define VK_VIDEO_CODEC_OPERATION_DECODE_AV1_BIT_KHR 0x00000004u
#define VK_VIDEO_CODEC_OPERATION_DECODE_VP9_BIT_KHR 0x00000008u
#define VK_VIDEO_CODEC_OPERATION_ENCODE_H264_BIT_KHR 0x00010000u
#define VK_VIDEO_CODEC_OPERATION_ENCODE_H265_BIT_KHR 0x00020000u
#define VK_VIDEO_CODEC_OPERATION_ENCODE_AV1_BIT_KHR 0x00040000u
#define VK_VIDEO_CHROMA_SUBSAMPLING_420_BIT_KHR 0x00000002u
#define VK_VIDEO_COMPONENT_BIT_DEPTH_8_BIT_KHR 0x00000001u
#define VK_VIDEO_COMPONENT_BIT_DEPTH_10_BIT_KHR 0x00000004u
#define VK_IMAGE_USAGE_SAMPLED_BIT 0x00000004u
#define VK_VIDEO_DECODE_H264_PICTURE_LAYOUT_PROGRESSIVE_KHR 0x00000000u
#define STD_VIDEO_H264_PROFILE_IDC_BASELINE 66u
#define STD_VIDEO_H264_PROFILE_IDC_MAIN 77u
#define STD_VIDEO_H265_PROFILE_IDC_MAIN 1u
#define STD_VIDEO_VP9_PROFILE_0 0u
#define STD_VIDEO_AV1_PROFILE_MAIN 0u
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2 1000059000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2 1000059001
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_1_PROPERTIES 50
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_2_PROPERTIES 52
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_3_PROPERTIES 54
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_4_PROPERTIES 56
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_PROPERTIES_EXT 1000218001
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_2_PROPERTIES_EXT 1000332001
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_1_FEATURES 49
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_2_FEATURES 51
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_3_FEATURES 53
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_4_FEATURES 55
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_7_PROPERTIES_KHR 1000562001
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_9_PROPERTIES_KHR 1000584001
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_10_PROPERTIES_KHR 1000630001
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_7_FEATURES_KHR 1000562000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_8_FEATURES_KHR 1000574000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_9_FEATURES_KHR 1000584000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_10_FEATURES_KHR 1000630000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_MODE_FIFO_LATEST_READY_FEATURES_KHR 1000361000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_ID_2_FEATURES_KHR 1000479002
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_WAIT_2_FEATURES_KHR 1000480001
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_BINARY_FEATURES_KHR 1000483000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_BINARY_PROPERTIES_KHR 1000483004
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_FEATURES_KHR 1000506000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_PROPERTIES_KHR 1000506002
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_HEAP_PROPERTIES_EXT 1000135008
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_HEAP_FEATURES_EXT 1000135009
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TEXTURE_COMPRESSION_ASTC_3D_FEATURES_EXT 1000288000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_LONG_VECTOR_FEATURES_EXT 1000635000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_LONG_VECTOR_PROPERTIES_EXT 1000635001
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SUBGROUP_PARTITIONED_FEATURES_EXT 1000662000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INTERNALLY_SYNCHRONIZED_QUEUES_FEATURES_KHR 1000504000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PUSH_CONSTANT_BANK_FEATURES_NV 1000589000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PUSH_CONSTANT_BANK_PROPERTIES_NV 1000589001
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COMPUTE_OCCUPANCY_PRIORITY_FEATURES_NV 1000645001
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DATA_GRAPH_NEURAL_ACCELERATOR_STATISTICS_FEATURES_ARM 1000676002
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_INSTRUMENTATION_FEATURES_ARM 1000607000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_INSTRUMENTATION_PROPERTIES_ARM 1000607001
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTISAMPLED_RENDER_TO_SWAPCHAIN_FEATURES_EXT 1000455000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRIMITIVE_RESTART_INDEX_FEATURES_EXT 1000678000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SPLIT_BARRIER_FEATURES_EXT 1000305000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SPLIT_BARRIER_PROPERTIES_EXT 1000305001
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FAULT_FEATURES_KHR 1000573000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FAULT_PROPERTIES_KHR 1000573001
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_OPACITY_MICROMAP_FEATURES_KHR 1000396000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_OPACITY_MICROMAP_PROPERTIES_KHR 1000396001
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_ABORT_FEATURES_KHR 1000233000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_ABORT_PROPERTIES_KHR 1000233002
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_CONSTANT_DATA_FEATURES_KHR 1000231000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_DECODE_VECTOR_FEATURES_NV 1000689000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_PROCESSING_3_FEATURES_QCOM 1000303000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_MULTIPLE_WAIT_QUEUES_FEATURES_QCOM 1000304000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_MULTIPLE_WAIT_QUEUES_PROPERTIES_QCOM 1000304001
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_MIXED_FLOAT_DOT_PRODUCT_FEATURES_VALVE 1000673000
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_THROTTLE_HINT_FEATURES_SEC 1000674000

#ifndef VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_ENCODE_FEEDBACK_2_FEATURES_KHR
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_ENCODE_FEEDBACK_2_FEATURES_KHR 1000598000
#endif
#ifndef VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_CONVERSION_FEATURES_QCOM
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_CONVERSION_FEATURES_QCOM 1000172000
#endif
#ifndef VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ELAPSED_TIMER_QUERY_FEATURES_QCOM
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ELAPSED_TIMER_QUERY_FEATURES_QCOM 1000173000
#endif
#ifndef VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_QUEUE_PERF_HINT_FEATURES_QCOM
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_QUEUE_PERF_HINT_FEATURES_QCOM 1000303000
#endif
#ifndef VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_QUEUE_PERF_HINT_PROPERTIES_QCOM
#define VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_QUEUE_PERF_HINT_PROPERTIES_QCOM 1000303001
#endif

#define VK_API_VERSION_1_0 4194304u
#define VK_API_VERSION_MAJOR(v) ((uint32_t)(v) >> 22U)
#define VK_API_VERSION_MINOR(v) (((uint32_t)(v) >> 12U) & 0x3FFU)
#define VK_API_VERSION_PATCH(v) ((uint32_t)(v) & 0xFFFU)
#define VK_TRUE 1u
#define VK_FALSE 0u
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
