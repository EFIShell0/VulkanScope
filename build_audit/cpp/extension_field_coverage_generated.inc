static const char* generatedCanonicalEnumName(const char*, int64_t) { return nullptr; }
static std::string generatedCanonicalFlagsName(const char*, uint64_t) { return {}; }
static void appendGeneratedStructFields(std::vector<GeneratedField>& dst, uint32_t sType, void* ptr) {
    switch (sType) {
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_FORMAT_INFO_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVideoFormatInfoKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceVideoFormatInfoKHR";
    generatedEmitNumeric(dst, section, "imageUsage", value.imageUsage);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_ENCODE_FEEDBACK_2_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVideoEncodeFeedback2FeaturesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceVideoEncodeFeedback2FeaturesKHR";
    generatedEmitBool(dst, section, "videoEncodeFeedback2", value.videoEncodeFeedback2);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_CONVERSION_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCooperativeMatrixConversionFeaturesQCOM*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceCooperativeMatrixConversionFeaturesQCOM";
    generatedEmitBool(dst, section, "cooperativeMatrixConversion", value.cooperativeMatrixConversion);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ELAPSED_TIMER_QUERY_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceElapsedTimerQueryFeaturesQCOM*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceElapsedTimerQueryFeaturesQCOM";
    generatedEmitBool(dst, section, "elapsedTimerQuery", value.elapsedTimerQuery);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_QUEUE_PERF_HINT_PROPERTIES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceQueuePerfHintPropertiesQCOM*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceQueuePerfHintPropertiesQCOM";
    generatedEmitHex(dst, section, "supportedQueues", reinterpret_cast<const uint8_t*>(&value.supportedQueues), sizeof(value.supportedQueues));
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_1_PROPERTIES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVulkan11Properties*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceVulkan11Properties";
    generatedEmitHex(dst, section, "deviceUUID", reinterpret_cast<const uint8_t*>(value.deviceUUID), sizeof(value.deviceUUID));
    generatedEmitHex(dst, section, "driverUUID", reinterpret_cast<const uint8_t*>(value.driverUUID), sizeof(value.driverUUID));
    generatedEmitHex(dst, section, "deviceLUID", reinterpret_cast<const uint8_t*>(value.deviceLUID), sizeof(value.deviceLUID));
    generatedEmitNumeric(dst, section, "deviceNodeMask", value.deviceNodeMask);
    generatedEmitBool(dst, section, "deviceLUIDValid", value.deviceLUIDValid);
    generatedEmitNumeric(dst, section, "subgroupSize", value.subgroupSize);
    generatedEmitNumeric(dst, section, "subgroupSupportedStages", value.subgroupSupportedStages);
    generatedEmitNumeric(dst, section, "subgroupSupportedOperations", value.subgroupSupportedOperations);
    generatedEmitBool(dst, section, "subgroupQuadOperationsInAllStages", value.subgroupQuadOperationsInAllStages);
    generatedEmitNumeric(dst, section, "pointClippingBehavior", value.pointClippingBehavior);
    generatedEmitNumeric(dst, section, "maxMultiviewViewCount", value.maxMultiviewViewCount);
    generatedEmitNumeric(dst, section, "maxMultiviewInstanceIndex", value.maxMultiviewInstanceIndex);
    generatedEmitBool(dst, section, "protectedNoFault", value.protectedNoFault);
    generatedEmitNumeric(dst, section, "maxPerSetDescriptors", value.maxPerSetDescriptors);
    generatedEmitNumeric(dst, section, "maxMemoryAllocationSize", value.maxMemoryAllocationSize);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_2_PROPERTIES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVulkan12Properties*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceVulkan12Properties";
    generatedEmitNumeric(dst, section, "driverID", value.driverID);
    generatedEmitString(dst, section, "driverName", value.driverName, sizeof(value.driverName));
    generatedEmitString(dst, section, "driverInfo", value.driverInfo, sizeof(value.driverInfo));
    generatedEmitHex(dst, section, "conformanceVersion", reinterpret_cast<const uint8_t*>(value.conformanceVersion), sizeof(value.conformanceVersion));
    generatedEmitNumeric(dst, section, "denormBehaviorIndependence", value.denormBehaviorIndependence);
    generatedEmitNumeric(dst, section, "roundingModeIndependence", value.roundingModeIndependence);
    generatedEmitBool(dst, section, "shaderSignedZeroInfNanPreserveFloat16", value.shaderSignedZeroInfNanPreserveFloat16);
    generatedEmitBool(dst, section, "shaderSignedZeroInfNanPreserveFloat32", value.shaderSignedZeroInfNanPreserveFloat32);
    generatedEmitBool(dst, section, "shaderSignedZeroInfNanPreserveFloat64", value.shaderSignedZeroInfNanPreserveFloat64);
    generatedEmitBool(dst, section, "shaderDenormPreserveFloat16", value.shaderDenormPreserveFloat16);
    generatedEmitBool(dst, section, "shaderDenormPreserveFloat32", value.shaderDenormPreserveFloat32);
    generatedEmitBool(dst, section, "shaderDenormPreserveFloat64", value.shaderDenormPreserveFloat64);
    generatedEmitBool(dst, section, "shaderDenormFlushToZeroFloat16", value.shaderDenormFlushToZeroFloat16);
    generatedEmitBool(dst, section, "shaderDenormFlushToZeroFloat32", value.shaderDenormFlushToZeroFloat32);
    generatedEmitBool(dst, section, "shaderDenormFlushToZeroFloat64", value.shaderDenormFlushToZeroFloat64);
    generatedEmitBool(dst, section, "shaderRoundingModeRTEFloat16", value.shaderRoundingModeRTEFloat16);
    generatedEmitBool(dst, section, "shaderRoundingModeRTEFloat32", value.shaderRoundingModeRTEFloat32);
    generatedEmitBool(dst, section, "shaderRoundingModeRTEFloat64", value.shaderRoundingModeRTEFloat64);
    generatedEmitBool(dst, section, "shaderRoundingModeRTZFloat16", value.shaderRoundingModeRTZFloat16);
    generatedEmitBool(dst, section, "shaderRoundingModeRTZFloat32", value.shaderRoundingModeRTZFloat32);
    generatedEmitBool(dst, section, "shaderRoundingModeRTZFloat64", value.shaderRoundingModeRTZFloat64);
    generatedEmitNumeric(dst, section, "maxUpdateAfterBindDescriptorsInAllPools", value.maxUpdateAfterBindDescriptorsInAllPools);
    generatedEmitBool(dst, section, "shaderUniformBufferArrayNonUniformIndexingNative", value.shaderUniformBufferArrayNonUniformIndexingNative);
    generatedEmitBool(dst, section, "shaderSampledImageArrayNonUniformIndexingNative", value.shaderSampledImageArrayNonUniformIndexingNative);
    generatedEmitBool(dst, section, "shaderStorageBufferArrayNonUniformIndexingNative", value.shaderStorageBufferArrayNonUniformIndexingNative);
    generatedEmitBool(dst, section, "shaderStorageImageArrayNonUniformIndexingNative", value.shaderStorageImageArrayNonUniformIndexingNative);
    generatedEmitBool(dst, section, "shaderInputAttachmentArrayNonUniformIndexingNative", value.shaderInputAttachmentArrayNonUniformIndexingNative);
    generatedEmitBool(dst, section, "robustBufferAccessUpdateAfterBind", value.robustBufferAccessUpdateAfterBind);
    generatedEmitBool(dst, section, "quadDivergentImplicitLod", value.quadDivergentImplicitLod);
    generatedEmitNumeric(dst, section, "maxPerStageDescriptorUpdateAfterBindSamplers", value.maxPerStageDescriptorUpdateAfterBindSamplers);
    generatedEmitNumeric(dst, section, "maxPerStageDescriptorUpdateAfterBindUniformBuffers", value.maxPerStageDescriptorUpdateAfterBindUniformBuffers);
    generatedEmitNumeric(dst, section, "maxPerStageDescriptorUpdateAfterBindStorageBuffers", value.maxPerStageDescriptorUpdateAfterBindStorageBuffers);
    generatedEmitNumeric(dst, section, "maxPerStageDescriptorUpdateAfterBindSampledImages", value.maxPerStageDescriptorUpdateAfterBindSampledImages);
    generatedEmitNumeric(dst, section, "maxPerStageDescriptorUpdateAfterBindStorageImages", value.maxPerStageDescriptorUpdateAfterBindStorageImages);
    generatedEmitNumeric(dst, section, "maxPerStageDescriptorUpdateAfterBindInputAttachments", value.maxPerStageDescriptorUpdateAfterBindInputAttachments);
    generatedEmitNumeric(dst, section, "maxPerStageUpdateAfterBindResources", value.maxPerStageUpdateAfterBindResources);
    generatedEmitNumeric(dst, section, "maxDescriptorSetUpdateAfterBindSamplers", value.maxDescriptorSetUpdateAfterBindSamplers);
    generatedEmitNumeric(dst, section, "maxDescriptorSetUpdateAfterBindUniformBuffers", value.maxDescriptorSetUpdateAfterBindUniformBuffers);
    generatedEmitNumeric(dst, section, "maxDescriptorSetUpdateAfterBindUniformBuffersDynamic", value.maxDescriptorSetUpdateAfterBindUniformBuffersDynamic);
    generatedEmitNumeric(dst, section, "maxDescriptorSetUpdateAfterBindStorageBuffers", value.maxDescriptorSetUpdateAfterBindStorageBuffers);
    generatedEmitNumeric(dst, section, "maxDescriptorSetUpdateAfterBindStorageBuffersDynamic", value.maxDescriptorSetUpdateAfterBindStorageBuffersDynamic);
    generatedEmitNumeric(dst, section, "maxDescriptorSetUpdateAfterBindSampledImages", value.maxDescriptorSetUpdateAfterBindSampledImages);
    generatedEmitNumeric(dst, section, "maxDescriptorSetUpdateAfterBindStorageImages", value.maxDescriptorSetUpdateAfterBindStorageImages);
    generatedEmitNumeric(dst, section, "maxDescriptorSetUpdateAfterBindInputAttachments", value.maxDescriptorSetUpdateAfterBindInputAttachments);
    generatedEmitNumeric(dst, section, "supportedDepthResolveModes", value.supportedDepthResolveModes);
    generatedEmitNumeric(dst, section, "supportedStencilResolveModes", value.supportedStencilResolveModes);
    generatedEmitBool(dst, section, "independentResolveNone", value.independentResolveNone);
    generatedEmitBool(dst, section, "independentResolve", value.independentResolve);
    generatedEmitBool(dst, section, "filterMinmaxSingleComponentFormats", value.filterMinmaxSingleComponentFormats);
    generatedEmitBool(dst, section, "filterMinmaxImageComponentMapping", value.filterMinmaxImageComponentMapping);
    generatedEmitNumeric(dst, section, "maxTimelineSemaphoreValueDifference", value.maxTimelineSemaphoreValueDifference);
    generatedEmitNumeric(dst, section, "framebufferIntegerColorSampleCounts", value.framebufferIntegerColorSampleCounts);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentDensityMapPropertiesEXT*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceFragmentDensityMapPropertiesEXT";
    generatedEmitString(dst, section, "minFragmentDensityTexelSize", (std::to_string(value.minFragmentDensityTexelSize.width) + " × " + std::to_string(value.minFragmentDensityTexelSize.height)));
    generatedEmitString(dst, section, "maxFragmentDensityTexelSize", (std::to_string(value.maxFragmentDensityTexelSize.width) + " × " + std::to_string(value.maxFragmentDensityTexelSize.height)));
    generatedEmitBool(dst, section, "fragmentDensityInvocations", value.fragmentDensityInvocations);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_2_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentDensityMap2PropertiesEXT*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceFragmentDensityMap2PropertiesEXT";
    generatedEmitBool(dst, section, "subsampledLoads", value.subsampledLoads);
    generatedEmitBool(dst, section, "subsampledCoarseReconstructionEarlyAccess", value.subsampledCoarseReconstructionEarlyAccess);
    generatedEmitNumeric(dst, section, "maxSubsampledArrayLayers", value.maxSubsampledArrayLayers);
    generatedEmitNumeric(dst, section, "maxDescriptorSetSubsampledSamplers", value.maxDescriptorSetSubsampledSamplers);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_HEAP_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDescriptorHeapFeaturesEXT*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceDescriptorHeapFeaturesEXT";
    generatedEmitBool(dst, section, "descriptorHeap", value.descriptorHeap);
    generatedEmitBool(dst, section, "descriptorHeapCaptureReplay", value.descriptorHeapCaptureReplay);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_HEAP_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDescriptorHeapPropertiesEXT*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceDescriptorHeapPropertiesEXT";
    generatedEmitNumeric(dst, section, "samplerHeapAlignment", value.samplerHeapAlignment);
    generatedEmitNumeric(dst, section, "resourceHeapAlignment", value.resourceHeapAlignment);
    generatedEmitNumeric(dst, section, "maxSamplerHeapSize", value.maxSamplerHeapSize);
    generatedEmitNumeric(dst, section, "maxResourceHeapSize", value.maxResourceHeapSize);
    generatedEmitNumeric(dst, section, "minSamplerHeapReservedRange", value.minSamplerHeapReservedRange);
    generatedEmitNumeric(dst, section, "minSamplerHeapReservedRangeWithEmbedded", value.minSamplerHeapReservedRangeWithEmbedded);
    generatedEmitNumeric(dst, section, "minResourceHeapReservedRange", value.minResourceHeapReservedRange);
    generatedEmitNumeric(dst, section, "samplerDescriptorSize", value.samplerDescriptorSize);
    generatedEmitNumeric(dst, section, "imageDescriptorSize", value.imageDescriptorSize);
    generatedEmitNumeric(dst, section, "bufferDescriptorSize", value.bufferDescriptorSize);
    generatedEmitNumeric(dst, section, "samplerDescriptorAlignment", value.samplerDescriptorAlignment);
    generatedEmitNumeric(dst, section, "imageDescriptorAlignment", value.imageDescriptorAlignment);
    generatedEmitNumeric(dst, section, "bufferDescriptorAlignment", value.bufferDescriptorAlignment);
    generatedEmitNumeric(dst, section, "maxPushDataSize", value.maxPushDataSize);
    generatedEmitHex(dst, section, "imageCaptureReplayOpaqueDataSize", reinterpret_cast<const uint8_t*>(&value.imageCaptureReplayOpaqueDataSize), sizeof(value.imageCaptureReplayOpaqueDataSize));
    generatedEmitNumeric(dst, section, "maxDescriptorHeapEmbeddedSamplers", value.maxDescriptorHeapEmbeddedSamplers);
    generatedEmitNumeric(dst, section, "samplerYcbcrConversionCount", value.samplerYcbcrConversionCount);
    generatedEmitBool(dst, section, "sparseDescriptorHeaps", value.sparseDescriptorHeaps);
    generatedEmitBool(dst, section, "protectedDescriptorHeaps", value.protectedDescriptorHeaps);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TEXTURE_COMPRESSION_ASTC_3D_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceTextureCompressionASTC3DFeaturesEXT*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceTextureCompressionASTC3DFeaturesEXT";
    generatedEmitBool(dst, section, "textureCompressionASTC_3D", value.textureCompressionASTC_3D);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_LONG_VECTOR_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderLongVectorFeaturesEXT*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceShaderLongVectorFeaturesEXT";
    generatedEmitBool(dst, section, "longVector", value.longVector);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_LONG_VECTOR_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderLongVectorPropertiesEXT*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceShaderLongVectorPropertiesEXT";
    generatedEmitNumeric(dst, section, "maxVectorComponents", value.maxVectorComponents);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SUBGROUP_PARTITIONED_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderSubgroupPartitionedFeaturesEXT*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceShaderSubgroupPartitionedFeaturesEXT";
    generatedEmitBool(dst, section, "shaderSubgroupPartitioned", value.shaderSubgroupPartitioned);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INTERNALLY_SYNCHRONIZED_QUEUES_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceInternallySynchronizedQueuesFeaturesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceInternallySynchronizedQueuesFeaturesKHR";
    generatedEmitBool(dst, section, "internallySynchronizedQueues", value.internallySynchronizedQueues);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PUSH_CONSTANT_BANK_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePushConstantBankFeaturesNV*>(ptr);
            const char* section = "Generated · VkPhysicalDevicePushConstantBankFeaturesNV";
    generatedEmitBool(dst, section, "pushConstantBank", value.pushConstantBank);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PUSH_CONSTANT_BANK_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePushConstantBankPropertiesNV*>(ptr);
            const char* section = "Generated · VkPhysicalDevicePushConstantBankPropertiesNV";
    generatedEmitNumeric(dst, section, "maxGraphicsPushConstantBanks", value.maxGraphicsPushConstantBanks);
    generatedEmitNumeric(dst, section, "maxComputePushConstantBanks", value.maxComputePushConstantBanks);
    generatedEmitNumeric(dst, section, "maxGraphicsPushDataBanks", value.maxGraphicsPushDataBanks);
    generatedEmitNumeric(dst, section, "maxComputePushDataBanks", value.maxComputePushDataBanks);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COMPUTE_OCCUPANCY_PRIORITY_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceComputeOccupancyPriorityFeaturesNV*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceComputeOccupancyPriorityFeaturesNV";
    generatedEmitBool(dst, section, "computeOccupancyPriority", value.computeOccupancyPriority);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DATA_GRAPH_NEURAL_ACCELERATOR_STATISTICS_FEATURES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDataGraphNeuralAcceleratorStatisticsFeaturesARM*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceDataGraphNeuralAcceleratorStatisticsFeaturesARM";
    generatedEmitBool(dst, section, "dataGraphNeuralAcceleratorStatistics", value.dataGraphNeuralAcceleratorStatistics);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_INSTRUMENTATION_FEATURES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderInstrumentationFeaturesARM*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceShaderInstrumentationFeaturesARM";
    generatedEmitBool(dst, section, "shaderInstrumentation", value.shaderInstrumentation);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_INSTRUMENTATION_PROPERTIES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderInstrumentationPropertiesARM*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceShaderInstrumentationPropertiesARM";
    generatedEmitNumeric(dst, section, "numMetrics", value.numMetrics);
    generatedEmitBool(dst, section, "perBasicBlockGranularity", value.perBasicBlockGranularity);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTISAMPLED_RENDER_TO_SWAPCHAIN_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMultisampledRenderToSwapchainFeaturesEXT*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceMultisampledRenderToSwapchainFeaturesEXT";
    generatedEmitBool(dst, section, "multisampledRenderToSwapchain", value.multisampledRenderToSwapchain);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRIMITIVE_RESTART_INDEX_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePrimitiveRestartIndexFeaturesEXT*>(ptr);
            const char* section = "Generated · VkPhysicalDevicePrimitiveRestartIndexFeaturesEXT";
    generatedEmitBool(dst, section, "primitiveRestartIndex", value.primitiveRestartIndex);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SPLIT_BARRIER_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderSplitBarrierFeaturesEXT*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceShaderSplitBarrierFeaturesEXT";
    generatedEmitBool(dst, section, "shaderSplitBarrier", value.shaderSplitBarrier);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SPLIT_BARRIER_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderSplitBarrierPropertiesEXT*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceShaderSplitBarrierPropertiesEXT";
    generatedEmitNumeric(dst, section, "splitBarrierReservedSharedMemory", value.splitBarrierReservedSharedMemory);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FAULT_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFaultFeaturesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceFaultFeaturesKHR";
    generatedEmitBool(dst, section, "deviceFault", value.deviceFault);
    generatedEmitBool(dst, section, "deviceFaultVendorBinary", value.deviceFaultVendorBinary);
    generatedEmitBool(dst, section, "deviceFaultReportMasked", value.deviceFaultReportMasked);
    generatedEmitBool(dst, section, "deviceFaultDeviceLostOnMasked", value.deviceFaultDeviceLostOnMasked);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FAULT_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFaultPropertiesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceFaultPropertiesKHR";
    generatedEmitNumeric(dst, section, "maxDeviceFaultCount", value.maxDeviceFaultCount);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_OPACITY_MICROMAP_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceOpacityMicromapFeaturesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceOpacityMicromapFeaturesKHR";
    generatedEmitBool(dst, section, "micromap", value.micromap);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_OPACITY_MICROMAP_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceOpacityMicromapPropertiesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceOpacityMicromapPropertiesKHR";
    generatedEmitNumeric(dst, section, "maxOpacity2StateSubdivisionLevel", value.maxOpacity2StateSubdivisionLevel);
    generatedEmitNumeric(dst, section, "maxOpacity4StateSubdivisionLevel", value.maxOpacity4StateSubdivisionLevel);
    generatedEmitNumeric(dst, section, "maxOpacityLossy4StateSubdivisionLevel", value.maxOpacityLossy4StateSubdivisionLevel);
    generatedEmitNumeric(dst, section, "maxMicromapTriangles", value.maxMicromapTriangles);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_ABORT_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderAbortFeaturesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceShaderAbortFeaturesKHR";
    generatedEmitBool(dst, section, "shaderAbort", value.shaderAbort);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_ABORT_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderAbortPropertiesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceShaderAbortPropertiesKHR";
    generatedEmitNumeric(dst, section, "maxShaderAbortMessageSize", value.maxShaderAbortMessageSize);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_CONSTANT_DATA_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderConstantDataFeaturesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceShaderConstantDataFeaturesKHR";
    generatedEmitBool(dst, section, "shaderConstantData", value.shaderConstantData);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_DECODE_VECTOR_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCooperativeMatrixDecodeVectorFeaturesNV*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceCooperativeMatrixDecodeVectorFeaturesNV";
    generatedEmitBool(dst, section, "cooperativeMatrixDecodeVector", value.cooperativeMatrixDecodeVector);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_MULTIPLE_WAIT_QUEUES_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderMultipleWaitQueuesFeaturesQCOM*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceShaderMultipleWaitQueuesFeaturesQCOM";
    generatedEmitBool(dst, section, "shaderMultipleWaitQueues", value.shaderMultipleWaitQueues);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_MULTIPLE_WAIT_QUEUES_PROPERTIES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderMultipleWaitQueuesPropertiesQCOM*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceShaderMultipleWaitQueuesPropertiesQCOM";
    generatedEmitNumeric(dst, section, "maxShaderWaitQueues", value.maxShaderWaitQueues);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_MIXED_FLOAT_DOT_PRODUCT_FEATURES_VALVE: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderMixedFloatDotProductFeaturesVALVE*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceShaderMixedFloatDotProductFeaturesVALVE";
    generatedEmitBool(dst, section, "shaderMixedFloatDotProductFloat16AccFloat32", value.shaderMixedFloatDotProductFloat16AccFloat32);
    generatedEmitBool(dst, section, "shaderMixedFloatDotProductFloat16AccFloat16", value.shaderMixedFloatDotProductFloat16AccFloat16);
    generatedEmitBool(dst, section, "shaderMixedFloatDotProductBFloat16Acc", value.shaderMixedFloatDotProductBFloat16Acc);
    generatedEmitBool(dst, section, "shaderMixedFloatDotProductFloat8AccFloat32", value.shaderMixedFloatDotProductFloat8AccFloat32);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_THROTTLE_HINT_FEATURES_SEC: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceThrottleHintFeaturesSEC*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceThrottleHintFeaturesSEC";
    generatedEmitBool(dst, section, "throttleHint", value.throttleHint);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_4_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVulkan14Features*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceVulkan14Features";
    generatedEmitBool(dst, section, "globalPriorityQuery", value.globalPriorityQuery);
    generatedEmitBool(dst, section, "shaderSubgroupRotate", value.shaderSubgroupRotate);
    generatedEmitBool(dst, section, "shaderSubgroupRotateClustered", value.shaderSubgroupRotateClustered);
    generatedEmitBool(dst, section, "shaderFloatControls2", value.shaderFloatControls2);
    generatedEmitBool(dst, section, "shaderExpectAssume", value.shaderExpectAssume);
    generatedEmitBool(dst, section, "rectangularLines", value.rectangularLines);
    generatedEmitBool(dst, section, "bresenhamLines", value.bresenhamLines);
    generatedEmitBool(dst, section, "smoothLines", value.smoothLines);
    generatedEmitBool(dst, section, "stippledRectangularLines", value.stippledRectangularLines);
    generatedEmitBool(dst, section, "stippledBresenhamLines", value.stippledBresenhamLines);
    generatedEmitBool(dst, section, "stippledSmoothLines", value.stippledSmoothLines);
    generatedEmitBool(dst, section, "vertexAttributeInstanceRateDivisor", value.vertexAttributeInstanceRateDivisor);
    generatedEmitBool(dst, section, "vertexAttributeInstanceRateZeroDivisor", value.vertexAttributeInstanceRateZeroDivisor);
    generatedEmitBool(dst, section, "indexTypeUint8", value.indexTypeUint8);
    generatedEmitBool(dst, section, "dynamicRenderingLocalRead", value.dynamicRenderingLocalRead);
    generatedEmitBool(dst, section, "maintenance5", value.maintenance5);
    generatedEmitBool(dst, section, "maintenance6", value.maintenance6);
    generatedEmitBool(dst, section, "pipelineProtectedAccess", value.pipelineProtectedAccess);
    generatedEmitBool(dst, section, "pipelineRobustness", value.pipelineRobustness);
    generatedEmitBool(dst, section, "hostImageCopy", value.hostImageCopy);
    generatedEmitBool(dst, section, "pushDescriptor", value.pushDescriptor);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_4_PROPERTIES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVulkan14Properties*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceVulkan14Properties";
    generatedEmitNumeric(dst, section, "lineSubPixelPrecisionBits", value.lineSubPixelPrecisionBits);
    generatedEmitNumeric(dst, section, "maxVertexAttribDivisor", value.maxVertexAttribDivisor);
    generatedEmitBool(dst, section, "supportsNonZeroFirstInstance", value.supportsNonZeroFirstInstance);
    generatedEmitNumeric(dst, section, "maxPushDescriptors", value.maxPushDescriptors);
    generatedEmitBool(dst, section, "dynamicRenderingLocalReadDepthStencilAttachments", value.dynamicRenderingLocalReadDepthStencilAttachments);
    generatedEmitBool(dst, section, "dynamicRenderingLocalReadMultisampledAttachments", value.dynamicRenderingLocalReadMultisampledAttachments);
    generatedEmitBool(dst, section, "earlyFragmentMultisampleCoverageAfterSampleCounting", value.earlyFragmentMultisampleCoverageAfterSampleCounting);
    generatedEmitBool(dst, section, "earlyFragmentSampleMaskTestBeforeSampleCounting", value.earlyFragmentSampleMaskTestBeforeSampleCounting);
    generatedEmitBool(dst, section, "depthStencilSwizzleOneSupport", value.depthStencilSwizzleOneSupport);
    generatedEmitBool(dst, section, "polygonModePointSize", value.polygonModePointSize);
    generatedEmitBool(dst, section, "nonStrictSinglePixelWideLinesUseParallelogram", value.nonStrictSinglePixelWideLinesUseParallelogram);
    generatedEmitBool(dst, section, "nonStrictWideLinesUseParallelogram", value.nonStrictWideLinesUseParallelogram);
    generatedEmitBool(dst, section, "blockTexelViewCompatibleMultipleLayers", value.blockTexelViewCompatibleMultipleLayers);
    generatedEmitNumeric(dst, section, "maxCombinedImageSamplerDescriptorCount", value.maxCombinedImageSamplerDescriptorCount);
    generatedEmitBool(dst, section, "fragmentShadingRateClampCombinerInputs", value.fragmentShadingRateClampCombinerInputs);
    generatedEmitNumeric(dst, section, "defaultRobustnessStorageBuffers", value.defaultRobustnessStorageBuffers);
    generatedEmitNumeric(dst, section, "defaultRobustnessUniformBuffers", value.defaultRobustnessUniformBuffers);
    generatedEmitNumeric(dst, section, "defaultRobustnessVertexInputs", value.defaultRobustnessVertexInputs);
    generatedEmitNumeric(dst, section, "defaultRobustnessImages", value.defaultRobustnessImages);
    generatedEmitNumeric(dst, section, "copySrcLayoutCount", value.copySrcLayoutCount);
    generatedEmitNumeric(dst, section, "copyDstLayoutCount", value.copyDstLayoutCount);
    generatedEmitHex(dst, section, "optimalTilingLayoutUUID", reinterpret_cast<const uint8_t*>(value.optimalTilingLayoutUUID), sizeof(value.optimalTilingLayoutUUID));
    generatedEmitBool(dst, section, "identicalMemoryTypeRequirements", value.identicalMemoryTypeRequirements);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_3_PROPERTIES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVulkan13Properties*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceVulkan13Properties";
    generatedEmitNumeric(dst, section, "minSubgroupSize", value.minSubgroupSize);
    generatedEmitNumeric(dst, section, "maxSubgroupSize", value.maxSubgroupSize);
    generatedEmitNumeric(dst, section, "maxComputeWorkgroupSubgroups", value.maxComputeWorkgroupSubgroups);
    generatedEmitNumeric(dst, section, "requiredSubgroupSizeStages", value.requiredSubgroupSizeStages);
    generatedEmitNumeric(dst, section, "maxInlineUniformBlockSize", value.maxInlineUniformBlockSize);
    generatedEmitNumeric(dst, section, "maxPerStageDescriptorInlineUniformBlocks", value.maxPerStageDescriptorInlineUniformBlocks);
    generatedEmitNumeric(dst, section, "maxPerStageDescriptorUpdateAfterBindInlineUniformBlocks", value.maxPerStageDescriptorUpdateAfterBindInlineUniformBlocks);
    generatedEmitNumeric(dst, section, "maxDescriptorSetInlineUniformBlocks", value.maxDescriptorSetInlineUniformBlocks);
    generatedEmitNumeric(dst, section, "maxDescriptorSetUpdateAfterBindInlineUniformBlocks", value.maxDescriptorSetUpdateAfterBindInlineUniformBlocks);
    generatedEmitNumeric(dst, section, "maxInlineUniformTotalSize", value.maxInlineUniformTotalSize);
    generatedEmitBool(dst, section, "integerDotProduct8BitUnsignedAccelerated", value.integerDotProduct8BitUnsignedAccelerated);
    generatedEmitBool(dst, section, "integerDotProduct8BitSignedAccelerated", value.integerDotProduct8BitSignedAccelerated);
    generatedEmitBool(dst, section, "integerDotProduct8BitMixedSignednessAccelerated", value.integerDotProduct8BitMixedSignednessAccelerated);
    generatedEmitBool(dst, section, "integerDotProduct4x8BitPackedUnsignedAccelerated", value.integerDotProduct4x8BitPackedUnsignedAccelerated);
    generatedEmitBool(dst, section, "integerDotProduct4x8BitPackedSignedAccelerated", value.integerDotProduct4x8BitPackedSignedAccelerated);
    generatedEmitBool(dst, section, "integerDotProduct4x8BitPackedMixedSignednessAccelerated", value.integerDotProduct4x8BitPackedMixedSignednessAccelerated);
    generatedEmitBool(dst, section, "integerDotProduct16BitUnsignedAccelerated", value.integerDotProduct16BitUnsignedAccelerated);
    generatedEmitBool(dst, section, "integerDotProduct16BitSignedAccelerated", value.integerDotProduct16BitSignedAccelerated);
    generatedEmitBool(dst, section, "integerDotProduct16BitMixedSignednessAccelerated", value.integerDotProduct16BitMixedSignednessAccelerated);
    generatedEmitBool(dst, section, "integerDotProduct32BitUnsignedAccelerated", value.integerDotProduct32BitUnsignedAccelerated);
    generatedEmitBool(dst, section, "integerDotProduct32BitSignedAccelerated", value.integerDotProduct32BitSignedAccelerated);
    generatedEmitBool(dst, section, "integerDotProduct32BitMixedSignednessAccelerated", value.integerDotProduct32BitMixedSignednessAccelerated);
    generatedEmitBool(dst, section, "integerDotProduct64BitUnsignedAccelerated", value.integerDotProduct64BitUnsignedAccelerated);
    generatedEmitBool(dst, section, "integerDotProduct64BitSignedAccelerated", value.integerDotProduct64BitSignedAccelerated);
    generatedEmitBool(dst, section, "integerDotProduct64BitMixedSignednessAccelerated", value.integerDotProduct64BitMixedSignednessAccelerated);
    generatedEmitBool(dst, section, "integerDotProductAccumulatingSaturating8BitUnsignedAccelerated", value.integerDotProductAccumulatingSaturating8BitUnsignedAccelerated);
    generatedEmitBool(dst, section, "integerDotProductAccumulatingSaturating8BitSignedAccelerated", value.integerDotProductAccumulatingSaturating8BitSignedAccelerated);
    generatedEmitBool(dst, section, "integerDotProductAccumulatingSaturating8BitMixedSignednessAccelerated", value.integerDotProductAccumulatingSaturating8BitMixedSignednessAccelerated);
    generatedEmitBool(dst, section, "integerDotProductAccumulatingSaturating4x8BitPackedUnsignedAccelerated", value.integerDotProductAccumulatingSaturating4x8BitPackedUnsignedAccelerated);
    generatedEmitBool(dst, section, "integerDotProductAccumulatingSaturating4x8BitPackedSignedAccelerated", value.integerDotProductAccumulatingSaturating4x8BitPackedSignedAccelerated);
    generatedEmitBool(dst, section, "integerDotProductAccumulatingSaturating4x8BitPackedMixedSignednessAccelerated", value.integerDotProductAccumulatingSaturating4x8BitPackedMixedSignednessAccelerated);
    generatedEmitBool(dst, section, "integerDotProductAccumulatingSaturating16BitUnsignedAccelerated", value.integerDotProductAccumulatingSaturating16BitUnsignedAccelerated);
    generatedEmitBool(dst, section, "integerDotProductAccumulatingSaturating16BitSignedAccelerated", value.integerDotProductAccumulatingSaturating16BitSignedAccelerated);
    generatedEmitBool(dst, section, "integerDotProductAccumulatingSaturating16BitMixedSignednessAccelerated", value.integerDotProductAccumulatingSaturating16BitMixedSignednessAccelerated);
    generatedEmitBool(dst, section, "integerDotProductAccumulatingSaturating32BitUnsignedAccelerated", value.integerDotProductAccumulatingSaturating32BitUnsignedAccelerated);
    generatedEmitBool(dst, section, "integerDotProductAccumulatingSaturating32BitSignedAccelerated", value.integerDotProductAccumulatingSaturating32BitSignedAccelerated);
    generatedEmitBool(dst, section, "integerDotProductAccumulatingSaturating32BitMixedSignednessAccelerated", value.integerDotProductAccumulatingSaturating32BitMixedSignednessAccelerated);
    generatedEmitBool(dst, section, "integerDotProductAccumulatingSaturating64BitUnsignedAccelerated", value.integerDotProductAccumulatingSaturating64BitUnsignedAccelerated);
    generatedEmitBool(dst, section, "integerDotProductAccumulatingSaturating64BitSignedAccelerated", value.integerDotProductAccumulatingSaturating64BitSignedAccelerated);
    generatedEmitBool(dst, section, "integerDotProductAccumulatingSaturating64BitMixedSignednessAccelerated", value.integerDotProductAccumulatingSaturating64BitMixedSignednessAccelerated);
    generatedEmitNumeric(dst, section, "storageTexelBufferOffsetAlignmentBytes", value.storageTexelBufferOffsetAlignmentBytes);
    generatedEmitBool(dst, section, "storageTexelBufferOffsetSingleTexelAlignment", value.storageTexelBufferOffsetSingleTexelAlignment);
    generatedEmitNumeric(dst, section, "uniformTexelBufferOffsetAlignmentBytes", value.uniformTexelBufferOffsetAlignmentBytes);
    generatedEmitBool(dst, section, "uniformTexelBufferOffsetSingleTexelAlignment", value.uniformTexelBufferOffsetSingleTexelAlignment);
    generatedEmitNumeric(dst, section, "maxBufferSize", value.maxBufferSize);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_7_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMaintenance7PropertiesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceMaintenance7PropertiesKHR";
    generatedEmitBool(dst, section, "robustFragmentShadingRateAttachmentAccess", value.robustFragmentShadingRateAttachmentAccess);
    generatedEmitBool(dst, section, "separateDepthStencilAttachmentAccess", value.separateDepthStencilAttachmentAccess);
    generatedEmitNumeric(dst, section, "maxDescriptorSetTotalUniformBuffersDynamic", value.maxDescriptorSetTotalUniformBuffersDynamic);
    generatedEmitNumeric(dst, section, "maxDescriptorSetTotalStorageBuffersDynamic", value.maxDescriptorSetTotalStorageBuffersDynamic);
    generatedEmitNumeric(dst, section, "maxDescriptorSetTotalBuffersDynamic", value.maxDescriptorSetTotalBuffersDynamic);
    generatedEmitNumeric(dst, section, "maxDescriptorSetUpdateAfterBindTotalUniformBuffersDynamic", value.maxDescriptorSetUpdateAfterBindTotalUniformBuffersDynamic);
    generatedEmitNumeric(dst, section, "maxDescriptorSetUpdateAfterBindTotalStorageBuffersDynamic", value.maxDescriptorSetUpdateAfterBindTotalStorageBuffersDynamic);
    generatedEmitNumeric(dst, section, "maxDescriptorSetUpdateAfterBindTotalBuffersDynamic", value.maxDescriptorSetUpdateAfterBindTotalBuffersDynamic);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_9_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMaintenance9PropertiesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceMaintenance9PropertiesKHR";
    generatedEmitBool(dst, section, "image2DViewOf3DSparse", value.image2DViewOf3DSparse);
    generatedEmitNumeric(dst, section, "defaultVertexAttributeValue", value.defaultVertexAttributeValue);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_10_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMaintenance10PropertiesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceMaintenance10PropertiesKHR";
    generatedEmitBool(dst, section, "rgba4OpaqueBlackSwizzled", value.rgba4OpaqueBlackSwizzled);
    generatedEmitBool(dst, section, "resolveSrgbFormatAppliesTransferFunction", value.resolveSrgbFormatAppliesTransferFunction);
    generatedEmitBool(dst, section, "resolveSrgbFormatSupportsTransferFunctionControl", value.resolveSrgbFormatSupportsTransferFunctionControl);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_7_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMaintenance7FeaturesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceMaintenance7FeaturesKHR";
    generatedEmitBool(dst, section, "maintenance7", value.maintenance7);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_8_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMaintenance8FeaturesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceMaintenance8FeaturesKHR";
    generatedEmitBool(dst, section, "maintenance8", value.maintenance8);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_9_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMaintenance9FeaturesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceMaintenance9FeaturesKHR";
    generatedEmitBool(dst, section, "maintenance9", value.maintenance9);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_10_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMaintenance10FeaturesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceMaintenance10FeaturesKHR";
    generatedEmitBool(dst, section, "maintenance10", value.maintenance10);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_MODE_FIFO_LATEST_READY_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePresentModeFifoLatestReadyFeaturesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDevicePresentModeFifoLatestReadyFeaturesKHR";
    generatedEmitBool(dst, section, "presentModeFifoLatestReady", value.presentModeFifoLatestReady);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_ID_2_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePresentId2FeaturesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDevicePresentId2FeaturesKHR";
    generatedEmitBool(dst, section, "presentId2", value.presentId2);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_WAIT_2_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePresentWait2FeaturesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDevicePresentWait2FeaturesKHR";
    generatedEmitBool(dst, section, "presentWait2", value.presentWait2);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_BINARY_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePipelineBinaryFeaturesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDevicePipelineBinaryFeaturesKHR";
    generatedEmitBool(dst, section, "pipelineBinaries", value.pipelineBinaries);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_BINARY_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePipelineBinaryPropertiesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDevicePipelineBinaryPropertiesKHR";
    generatedEmitBool(dst, section, "pipelineBinaryInternalCache", value.pipelineBinaryInternalCache);
    generatedEmitBool(dst, section, "pipelineBinaryInternalCacheControl", value.pipelineBinaryInternalCacheControl);
    generatedEmitBool(dst, section, "pipelineBinaryPrefersInternalCache", value.pipelineBinaryPrefersInternalCache);
    generatedEmitBool(dst, section, "pipelineBinaryPrecompiledInternalCache", value.pipelineBinaryPrecompiledInternalCache);
    generatedEmitBool(dst, section, "pipelineBinaryCompressedData", value.pipelineBinaryCompressedData);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCooperativeMatrixFeaturesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceCooperativeMatrixFeaturesKHR";
    generatedEmitBool(dst, section, "cooperativeMatrix", value.cooperativeMatrix);
    generatedEmitBool(dst, section, "cooperativeMatrixRobustBufferAccess", value.cooperativeMatrixRobustBufferAccess);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCooperativeMatrixPropertiesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceCooperativeMatrixPropertiesKHR";
    generatedEmitNumeric(dst, section, "cooperativeMatrixSupportedStages", value.cooperativeMatrixSupportedStages);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_FORMAT_INFO_2: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceImageFormatInfo2*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceImageFormatInfo2";
    generatedEmitNumeric(dst, section, "format", value.format);
    generatedEmitNumeric(dst, section, "type", value.type);
    generatedEmitNumeric(dst, section, "tiling", value.tiling);
    generatedEmitNumeric(dst, section, "usage", value.usage);
    generatedEmitNumeric(dst, section, "flags", value.flags);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MEMORY_PROPERTIES_2: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMemoryProperties2*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceMemoryProperties2";
    generatedEmitHex(dst, section, "memoryProperties", reinterpret_cast<const uint8_t*>(&value.memoryProperties), sizeof(value.memoryProperties));
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MEMORY_BUDGET_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMemoryBudgetPropertiesEXT*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceMemoryBudgetPropertiesEXT";
    generatedEmitArray(dst, section, "heapBudget", value.heapBudget, sizeof(value.heapBudget) / sizeof(value.heapBudget[0]));
    generatedEmitArray(dst, section, "heapUsage", value.heapUsage, sizeof(value.heapUsage) / sizeof(value.heapUsage[0]));
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TOOL_PROPERTIES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceToolProperties*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceToolProperties";
    generatedEmitString(dst, section, "name", value.name, sizeof(value.name));
    generatedEmitNumeric(dst, section, "version", value.version);
    generatedEmitNumeric(dst, section, "purposes", value.purposes);
    generatedEmitString(dst, section, "description", value.description, sizeof(value.description));
    generatedEmitString(dst, section, "layer", value.layer, sizeof(value.layer));
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_GROUP_PROPERTIES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceGroupProperties*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceGroupProperties";
    generatedEmitNumeric(dst, section, "physicalDeviceCount", value.physicalDeviceCount);
    generatedEmitHex(dst, section, "physicalDevices", reinterpret_cast<const uint8_t*>(value.physicalDevices), sizeof(value.physicalDevices));
    generatedEmitBool(dst, section, "subsetAllocation", value.subsetAllocation);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_IMAGE_FORMAT_INFO: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExternalImageFormatInfo*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceExternalImageFormatInfo";
    generatedEmitNumeric(dst, section, "handleType", value.handleType);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DATA_GRAPH_OPTICAL_FLOW_FEATURES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDataGraphOpticalFlowFeaturesARM*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceDataGraphOpticalFlowFeaturesARM";
    generatedEmitBool(dst, section, "dataGraphOpticalFlow", value.dataGraphOpticalFlow);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_CACHE_INCREMENTAL_MODE_FEATURES_SEC: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePipelineCacheIncrementalModeFeaturesSEC*>(ptr);
            const char* section = "Generated · VkPhysicalDevicePipelineCacheIncrementalModeFeaturesSEC";
    generatedEmitBool(dst, section, "pipelineCacheIncrementalMode", value.pipelineCacheIncrementalMode);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_FLAGS_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExtendedFlagsFeaturesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceExtendedFlagsFeaturesKHR";
    generatedEmitBool(dst, section, "extendedFlags", value.extendedFlags);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_OCP_MICROSCALING_TYPES_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderOcpMicroscalingTypesFeaturesEXT*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceShaderOcpMicroscalingTypesFeaturesEXT";
    generatedEmitBool(dst, section, "shaderOcpMicroscalingTypes", value.shaderOcpMicroscalingTypes);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_BUFFER_INFO: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExternalBufferInfo*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceExternalBufferInfo";
    generatedEmitNumeric(dst, section, "flags", value.flags);
    generatedEmitNumeric(dst, section, "usage", value.usage);
    generatedEmitNumeric(dst, section, "handleType", value.handleType);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_FENCE_INFO: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExternalFenceInfo*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceExternalFenceInfo";
    generatedEmitNumeric(dst, section, "handleType", value.handleType);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_SEMAPHORE_INFO: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExternalSemaphoreInfo*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceExternalSemaphoreInfo";
    generatedEmitNumeric(dst, section, "handleType", value.handleType);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SPARSE_IMAGE_FORMAT_INFO_2: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceSparseImageFormatInfo2*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceSparseImageFormatInfo2";
    generatedEmitNumeric(dst, section, "format", value.format);
    generatedEmitNumeric(dst, section, "type", value.type);
    generatedEmitHex(dst, section, "samples", reinterpret_cast<const uint8_t*>(&value.samples), sizeof(value.samples));
    generatedEmitNumeric(dst, section, "usage", value.usage);
    generatedEmitNumeric(dst, section, "tiling", value.tiling);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_11_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMaintenance11FeaturesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceMaintenance11FeaturesKHR";
    generatedEmitBool(dst, section, "maintenance11", value.maintenance11);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_UNIFORM_BUFFER_UNSIZED_ARRAY_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderUniformBufferUnsizedArrayFeaturesEXT*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceShaderUniformBufferUnsizedArrayFeaturesEXT";
    generatedEmitBool(dst, section, "shaderUniformBufferUnsizedArray", value.shaderUniformBufferUnsizedArray);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEVICE_ADDRESS_COMMANDS_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDeviceAddressCommandsFeaturesKHR*>(ptr);
            const char* section = "Generated · VkPhysicalDeviceDeviceAddressCommandsFeaturesKHR";
    generatedEmitBool(dst, section, "deviceAddressCommands", value.deviceAddressCommands);
            break;
        }
        default: break;
    }
}
static constexpr uint32_t kGeneratedPhysicalDeviceStructSerializerCount = 72;
