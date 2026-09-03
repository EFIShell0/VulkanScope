static void appendParityStructFields(std::vector<GeneratedField>& dst, uint32_t sType, void* ptr) {
    switch (sType) {
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_4444_FORMATS_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevice4444FormatsFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevice4444FormatsFeaturesEXT";
            generatedEmitBool(dst, section, "formatA4B4G4R4", value.formatA4B4G4R4);
            generatedEmitBool(dst, section, "formatA4R4G4B4", value.formatA4R4G4B4);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ACCELERATION_STRUCTURE_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceAccelerationStructureFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceAccelerationStructureFeaturesKHR";
            generatedEmitBool(dst, section, "accelerationStructure", value.accelerationStructure);
            generatedEmitBool(dst, section, "accelerationStructureCaptureReplay", value.accelerationStructureCaptureReplay);
            generatedEmitBool(dst, section, "accelerationStructureHostCommands", value.accelerationStructureHostCommands);
            generatedEmitBool(dst, section, "accelerationStructureIndirectBuild", value.accelerationStructureIndirectBuild);
            generatedEmitBool(dst, section, "descriptorBindingAccelerationStructureUpdateAfterBind", value.descriptorBindingAccelerationStructureUpdateAfterBind);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ADDRESS_BINDING_REPORT_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceAddressBindingReportFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceAddressBindingReportFeaturesEXT";
            generatedEmitBool(dst, section, "reportAddressBinding", value.reportAddressBinding);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_AMIGO_PROFILING_FEATURES_SEC: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceAmigoProfilingFeaturesSEC*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceAmigoProfilingFeaturesSEC";
            generatedEmitBool(dst, section, "amigoProfiling", value.amigoProfiling);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ATTACHMENT_FEEDBACK_LOOP_DYNAMIC_STATE_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceAttachmentFeedbackLoopDynamicStateFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceAttachmentFeedbackLoopDynamicStateFeaturesEXT";
            generatedEmitBool(dst, section, "attachmentFeedbackLoopDynamicState", value.attachmentFeedbackLoopDynamicState);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ATTACHMENT_FEEDBACK_LOOP_LAYOUT_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceAttachmentFeedbackLoopLayoutFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceAttachmentFeedbackLoopLayoutFeaturesEXT";
            generatedEmitBool(dst, section, "attachmentFeedbackLoopLayout", value.attachmentFeedbackLoopLayout);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_BLEND_OPERATION_ADVANCED_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceBlendOperationAdvancedFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceBlendOperationAdvancedFeaturesEXT";
            generatedEmitBool(dst, section, "advancedBlendCoherentOperations", value.advancedBlendCoherentOperations);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_BLEND_OPERATION_ADVANCED_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceBlendOperationAdvancedPropertiesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceBlendOperationAdvancedPropertiesEXT";
            generatedEmitBool(dst, section, "advancedBlendAllOperations", value.advancedBlendAllOperations);
            generatedEmitBool(dst, section, "advancedBlendCorrelatedOverlap", value.advancedBlendCorrelatedOverlap);
            generatedEmitBool(dst, section, "advancedBlendIndependentBlend", value.advancedBlendIndependentBlend);
            generatedEmitAuto(dst, section, "advancedBlendMaxColorAttachments", value.advancedBlendMaxColorAttachments);
            generatedEmitBool(dst, section, "advancedBlendNonPremultipliedDstColor", value.advancedBlendNonPremultipliedDstColor);
            generatedEmitBool(dst, section, "advancedBlendNonPremultipliedSrcColor", value.advancedBlendNonPremultipliedSrcColor);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_BORDER_COLOR_SWIZZLE_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceBorderColorSwizzleFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceBorderColorSwizzleFeaturesEXT";
            generatedEmitBool(dst, section, "borderColorSwizzle", value.borderColorSwizzle);
            generatedEmitBool(dst, section, "borderColorSwizzleFromImage", value.borderColorSwizzleFromImage);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_BUFFER_DEVICE_ADDRESS_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceBufferDeviceAddressFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceBufferDeviceAddressFeaturesEXT";
            generatedEmitBool(dst, section, "bufferDeviceAddress", value.bufferDeviceAddress);
            generatedEmitBool(dst, section, "bufferDeviceAddressCaptureReplay", value.bufferDeviceAddressCaptureReplay);
            generatedEmitBool(dst, section, "bufferDeviceAddressMultiDevice", value.bufferDeviceAddressMultiDevice);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CLUSTER_ACCELERATION_STRUCTURE_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceClusterAccelerationStructureFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceClusterAccelerationStructureFeaturesNV";
            generatedEmitBool(dst, section, "clusterAccelerationStructure", value.clusterAccelerationStructure);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CLUSTER_ACCELERATION_STRUCTURE_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceClusterAccelerationStructurePropertiesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceClusterAccelerationStructurePropertiesNV";
            generatedEmitAuto(dst, section, "clusterBottomLevelByteAlignment", value.clusterBottomLevelByteAlignment);
            generatedEmitAuto(dst, section, "clusterByteAlignment", value.clusterByteAlignment);
            generatedEmitAuto(dst, section, "clusterScratchByteAlignment", value.clusterScratchByteAlignment);
            generatedEmitAuto(dst, section, "clusterTemplateBoundsByteAlignment", value.clusterTemplateBoundsByteAlignment);
            generatedEmitAuto(dst, section, "clusterTemplateByteAlignment", value.clusterTemplateByteAlignment);
            generatedEmitAuto(dst, section, "maxClusterGeometryIndex", value.maxClusterGeometryIndex);
            generatedEmitAuto(dst, section, "maxTrianglesPerCluster", value.maxTrianglesPerCluster);
            generatedEmitAuto(dst, section, "maxVerticesPerCluster", value.maxVerticesPerCluster);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CLUSTER_CULLING_SHADER_FEATURES_HUAWEI: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceClusterCullingShaderFeaturesHUAWEI*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceClusterCullingShaderFeaturesHUAWEI";
            generatedEmitBool(dst, section, "clustercullingShader", value.clustercullingShader);
            generatedEmitBool(dst, section, "multiviewClusterCullingShader", value.multiviewClusterCullingShader);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CLUSTER_CULLING_SHADER_PROPERTIES_HUAWEI: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceClusterCullingShaderPropertiesHUAWEI*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceClusterCullingShaderPropertiesHUAWEI";
            generatedEmitAuto(dst, section, "indirectBufferOffsetAlignment", value.indirectBufferOffsetAlignment);
            generatedEmitAuto(dst, section, "maxOutputClusterCount", value.maxOutputClusterCount);
            generatedEmitAuto(dst, section, "maxWorkGroupCount", value.maxWorkGroupCount);
            generatedEmitAuto(dst, section, "maxWorkGroupSize", value.maxWorkGroupSize);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COHERENT_MEMORY_FEATURES_AMD: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCoherentMemoryFeaturesAMD*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceCoherentMemoryFeaturesAMD";
            generatedEmitBool(dst, section, "deviceCoherentMemory", value.deviceCoherentMemory);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COLOR_WRITE_ENABLE_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceColorWriteEnableFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceColorWriteEnableFeaturesEXT";
            generatedEmitBool(dst, section, "colorWriteEnable", value.colorWriteEnable);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COMMAND_BUFFER_INHERITANCE_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCommandBufferInheritanceFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceCommandBufferInheritanceFeaturesNV";
            generatedEmitBool(dst, section, "commandBufferInheritance", value.commandBufferInheritance);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COMPUTE_SHADER_DERIVATIVES_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceComputeShaderDerivativesFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceComputeShaderDerivativesFeaturesNV";
            generatedEmitBool(dst, section, "computeDerivativeGroupLinear", value.computeDerivativeGroupLinear);
            generatedEmitBool(dst, section, "computeDerivativeGroupQuads", value.computeDerivativeGroupQuads);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COMPUTE_SHADER_DERIVATIVES_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceComputeShaderDerivativesPropertiesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceComputeShaderDerivativesPropertiesKHR";
            generatedEmitBool(dst, section, "meshAndTaskShaderDerivatives", value.meshAndTaskShaderDerivatives);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CONDITIONAL_RENDERING_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceConditionalRenderingFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceConditionalRenderingFeaturesEXT";
            generatedEmitBool(dst, section, "conditionalRendering", value.conditionalRendering);
            generatedEmitBool(dst, section, "inheritedConditionalRendering", value.inheritedConditionalRendering);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CONSERVATIVE_RASTERIZATION_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceConservativeRasterizationPropertiesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceConservativeRasterizationPropertiesEXT";
            generatedEmitBool(dst, section, "conservativePointAndLineRasterization", value.conservativePointAndLineRasterization);
            generatedEmitBool(dst, section, "conservativeRasterizationPostDepthCoverage", value.conservativeRasterizationPostDepthCoverage);
            generatedEmitBool(dst, section, "degenerateLinesRasterized", value.degenerateLinesRasterized);
            generatedEmitBool(dst, section, "degenerateTrianglesRasterized", value.degenerateTrianglesRasterized);
            generatedEmitAuto(dst, section, "extraPrimitiveOverestimationSizeGranularity", value.extraPrimitiveOverestimationSizeGranularity);
            generatedEmitBool(dst, section, "fullyCoveredFragmentShaderInputVariable", value.fullyCoveredFragmentShaderInputVariable);
            generatedEmitAuto(dst, section, "maxExtraPrimitiveOverestimationSize", value.maxExtraPrimitiveOverestimationSize);
            generatedEmitAuto(dst, section, "primitiveOverestimationSize", value.primitiveOverestimationSize);
            generatedEmitBool(dst, section, "primitiveUnderestimation", value.primitiveUnderestimation);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_2_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCooperativeMatrix2PropertiesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceCooperativeMatrix2PropertiesNV";
            generatedEmitAuto(dst, section, "cooperativeMatrixFlexibleDimensionsMaxDimension", value.cooperativeMatrixFlexibleDimensionsMaxDimension);
            generatedEmitAuto(dst, section, "cooperativeMatrixWorkgroupScopeMaxWorkgroupSize", value.cooperativeMatrixWorkgroupScopeMaxWorkgroupSize);
            generatedEmitAuto(dst, section, "cooperativeMatrixWorkgroupScopeReservedSharedMemory", value.cooperativeMatrixWorkgroupScopeReservedSharedMemory);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCooperativeMatrixFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceCooperativeMatrixFeaturesNV";
            generatedEmitBool(dst, section, "cooperativeMatrix", value.cooperativeMatrix);
            generatedEmitBool(dst, section, "cooperativeMatrixRobustBufferAccess", value.cooperativeMatrixRobustBufferAccess);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCooperativeMatrixPropertiesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceCooperativeMatrixPropertiesNV";
            generatedEmitAuto(dst, section, "cooperativeMatrixSupportedStages", value.cooperativeMatrixSupportedStages);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_VECTOR_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCooperativeVectorFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceCooperativeVectorFeaturesNV";
            generatedEmitBool(dst, section, "cooperativeVector", value.cooperativeVector);
            generatedEmitBool(dst, section, "cooperativeVectorTraining", value.cooperativeVectorTraining);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_VECTOR_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCooperativeVectorPropertiesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceCooperativeVectorPropertiesNV";
            generatedEmitAuto(dst, section, "cooperativeVectorSupportedStages", value.cooperativeVectorSupportedStages);
            generatedEmitBool(dst, section, "cooperativeVectorTrainingFloat16Accumulation", value.cooperativeVectorTrainingFloat16Accumulation);
            generatedEmitBool(dst, section, "cooperativeVectorTrainingFloat32Accumulation", value.cooperativeVectorTrainingFloat32Accumulation);
            generatedEmitAuto(dst, section, "maxCooperativeVectorComponents", value.maxCooperativeVectorComponents);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COPY_MEMORY_INDIRECT_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCopyMemoryIndirectFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceCopyMemoryIndirectFeaturesKHR";
            generatedEmitBool(dst, section, "indirectMemoryCopy", value.indirectMemoryCopy);
            generatedEmitBool(dst, section, "indirectMemoryToImageCopy", value.indirectMemoryToImageCopy);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COPY_MEMORY_INDIRECT_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCopyMemoryIndirectFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceCopyMemoryIndirectFeaturesNV";
            generatedEmitBool(dst, section, "indirectCopy", value.indirectCopy);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COPY_MEMORY_INDIRECT_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCopyMemoryIndirectPropertiesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceCopyMemoryIndirectPropertiesNV";
            generatedEmitAuto(dst, section, "supportedQueues", value.supportedQueues);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CORNER_SAMPLED_IMAGE_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCornerSampledImageFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceCornerSampledImageFeaturesNV";
            generatedEmitBool(dst, section, "cornerSampledImage", value.cornerSampledImage);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COVERAGE_REDUCTION_MODE_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCoverageReductionModeFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceCoverageReductionModeFeaturesNV";
            generatedEmitBool(dst, section, "coverageReductionMode", value.coverageReductionMode);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CUBIC_CLAMP_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCubicClampFeaturesQCOM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceCubicClampFeaturesQCOM";
            generatedEmitBool(dst, section, "cubicRangeClamp", value.cubicRangeClamp);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CUBIC_WEIGHTS_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCubicWeightsFeaturesQCOM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceCubicWeightsFeaturesQCOM";
            generatedEmitBool(dst, section, "selectableCubicWeights", value.selectableCubicWeights);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CUDA_KERNEL_LAUNCH_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCudaKernelLaunchPropertiesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceCudaKernelLaunchPropertiesNV";
            generatedEmitAuto(dst, section, "computeCapabilityMajor", value.computeCapabilityMajor);
            generatedEmitAuto(dst, section, "computeCapabilityMinor", value.computeCapabilityMinor);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CUSTOM_BORDER_COLOR_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCustomBorderColorFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceCustomBorderColorFeaturesEXT";
            generatedEmitBool(dst, section, "customBorderColorWithoutFormat", value.customBorderColorWithoutFormat);
            generatedEmitBool(dst, section, "customBorderColors", value.customBorderColors);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CUSTOM_RESOLVE_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCustomResolveFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceCustomResolveFeaturesEXT";
            generatedEmitBool(dst, section, "customResolve", value.customResolve);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DATA_GRAPH_FEATURES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDataGraphFeaturesARM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDataGraphFeaturesARM";
            generatedEmitBool(dst, section, "dataGraph", value.dataGraph);
            generatedEmitBool(dst, section, "dataGraphDescriptorBuffer", value.dataGraphDescriptorBuffer);
            generatedEmitBool(dst, section, "dataGraphShaderModule", value.dataGraphShaderModule);
            generatedEmitBool(dst, section, "dataGraphSpecializationConstants", value.dataGraphSpecializationConstants);
            generatedEmitBool(dst, section, "dataGraphUpdateAfterBind", value.dataGraphUpdateAfterBind);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DATA_GRAPH_MODEL_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDataGraphModelFeaturesQCOM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDataGraphModelFeaturesQCOM";
            generatedEmitBool(dst, section, "dataGraphModel", value.dataGraphModel);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEDICATED_ALLOCATION_IMAGE_ALIASING_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDedicatedAllocationImageAliasingFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDedicatedAllocationImageAliasingFeaturesNV";
            generatedEmitBool(dst, section, "dedicatedAllocationImageAliasing", value.dedicatedAllocationImageAliasing);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_BIAS_CONTROL_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDepthBiasControlFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDepthBiasControlFeaturesEXT";
            generatedEmitBool(dst, section, "depthBiasControl", value.depthBiasControl);
            generatedEmitBool(dst, section, "depthBiasExact", value.depthBiasExact);
            generatedEmitBool(dst, section, "floatRepresentation", value.floatRepresentation);
            generatedEmitBool(dst, section, "leastRepresentableValueForceUnormRepresentation", value.leastRepresentableValueForceUnormRepresentation);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_CLAMP_CONTROL_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDepthClampControlFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDepthClampControlFeaturesEXT";
            generatedEmitBool(dst, section, "depthClampControl", value.depthClampControl);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_CLAMP_ZERO_ONE_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDepthClampZeroOneFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDepthClampZeroOneFeaturesKHR";
            generatedEmitBool(dst, section, "depthClampZeroOne", value.depthClampZeroOne);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_CLIP_CONTROL_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDepthClipControlFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDepthClipControlFeaturesEXT";
            generatedEmitBool(dst, section, "depthClipControl", value.depthClipControl);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_CLIP_ENABLE_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDepthClipEnableFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDepthClipEnableFeaturesEXT";
            generatedEmitBool(dst, section, "depthClipEnable", value.depthClipEnable);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_BUFFER_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDescriptorBufferFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDescriptorBufferFeaturesEXT";
            generatedEmitBool(dst, section, "descriptorBuffer", value.descriptorBuffer);
            generatedEmitBool(dst, section, "descriptorBufferCaptureReplay", value.descriptorBufferCaptureReplay);
            generatedEmitBool(dst, section, "descriptorBufferImageLayoutIgnored", value.descriptorBufferImageLayoutIgnored);
            generatedEmitBool(dst, section, "descriptorBufferPushDescriptors", value.descriptorBufferPushDescriptors);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_BUFFER_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDescriptorBufferPropertiesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDescriptorBufferPropertiesEXT";
            generatedEmitAuto(dst, section, "accelerationStructureCaptureReplayDescriptorDataSize", value.accelerationStructureCaptureReplayDescriptorDataSize);
            generatedEmitAuto(dst, section, "accelerationStructureDescriptorSize", value.accelerationStructureDescriptorSize);
            generatedEmitBool(dst, section, "allowSamplerImageViewPostSubmitCreation", value.allowSamplerImageViewPostSubmitCreation);
            generatedEmitAuto(dst, section, "bufferCaptureReplayDescriptorDataSize", value.bufferCaptureReplayDescriptorDataSize);
            generatedEmitBool(dst, section, "bufferlessPushDescriptors", value.bufferlessPushDescriptors);
            generatedEmitBool(dst, section, "combinedImageSamplerDescriptorSingleArray", value.combinedImageSamplerDescriptorSingleArray);
            generatedEmitAuto(dst, section, "combinedImageSamplerDescriptorSize", value.combinedImageSamplerDescriptorSize);
            generatedEmitAuto(dst, section, "descriptorBufferAddressSpaceSize", value.descriptorBufferAddressSpaceSize);
            generatedEmitAuto(dst, section, "descriptorBufferOffsetAlignment", value.descriptorBufferOffsetAlignment);
            generatedEmitAuto(dst, section, "imageCaptureReplayDescriptorDataSize", value.imageCaptureReplayDescriptorDataSize);
            generatedEmitAuto(dst, section, "imageViewCaptureReplayDescriptorDataSize", value.imageViewCaptureReplayDescriptorDataSize);
            generatedEmitAuto(dst, section, "inputAttachmentDescriptorSize", value.inputAttachmentDescriptorSize);
            generatedEmitAuto(dst, section, "maxDescriptorBufferBindings", value.maxDescriptorBufferBindings);
            generatedEmitAuto(dst, section, "maxEmbeddedImmutableSamplerBindings", value.maxEmbeddedImmutableSamplerBindings);
            generatedEmitAuto(dst, section, "maxEmbeddedImmutableSamplers", value.maxEmbeddedImmutableSamplers);
            generatedEmitAuto(dst, section, "maxResourceDescriptorBufferBindings", value.maxResourceDescriptorBufferBindings);
            generatedEmitAuto(dst, section, "maxResourceDescriptorBufferRange", value.maxResourceDescriptorBufferRange);
            generatedEmitAuto(dst, section, "maxSamplerDescriptorBufferBindings", value.maxSamplerDescriptorBufferBindings);
            generatedEmitAuto(dst, section, "maxSamplerDescriptorBufferRange", value.maxSamplerDescriptorBufferRange);
            generatedEmitAuto(dst, section, "resourceDescriptorBufferAddressSpaceSize", value.resourceDescriptorBufferAddressSpaceSize);
            generatedEmitAuto(dst, section, "robustStorageBufferDescriptorSize", value.robustStorageBufferDescriptorSize);
            generatedEmitAuto(dst, section, "robustStorageTexelBufferDescriptorSize", value.robustStorageTexelBufferDescriptorSize);
            generatedEmitAuto(dst, section, "robustUniformBufferDescriptorSize", value.robustUniformBufferDescriptorSize);
            generatedEmitAuto(dst, section, "robustUniformTexelBufferDescriptorSize", value.robustUniformTexelBufferDescriptorSize);
            generatedEmitAuto(dst, section, "sampledImageDescriptorSize", value.sampledImageDescriptorSize);
            generatedEmitAuto(dst, section, "samplerCaptureReplayDescriptorDataSize", value.samplerCaptureReplayDescriptorDataSize);
            generatedEmitAuto(dst, section, "samplerDescriptorBufferAddressSpaceSize", value.samplerDescriptorBufferAddressSpaceSize);
            generatedEmitAuto(dst, section, "samplerDescriptorSize", value.samplerDescriptorSize);
            generatedEmitAuto(dst, section, "storageBufferDescriptorSize", value.storageBufferDescriptorSize);
            generatedEmitAuto(dst, section, "storageImageDescriptorSize", value.storageImageDescriptorSize);
            generatedEmitAuto(dst, section, "storageTexelBufferDescriptorSize", value.storageTexelBufferDescriptorSize);
            generatedEmitAuto(dst, section, "uniformBufferDescriptorSize", value.uniformBufferDescriptorSize);
            generatedEmitAuto(dst, section, "uniformTexelBufferDescriptorSize", value.uniformTexelBufferDescriptorSize);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_POOL_OVERALLOCATION_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDescriptorPoolOverallocationFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDescriptorPoolOverallocationFeaturesNV";
            generatedEmitBool(dst, section, "descriptorPoolOverallocation", value.descriptorPoolOverallocation);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_SET_HOST_MAPPING_FEATURES_VALVE: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDescriptorSetHostMappingFeaturesVALVE*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDescriptorSetHostMappingFeaturesVALVE";
            generatedEmitBool(dst, section, "descriptorSetHostMapping", value.descriptorSetHostMapping);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEVICE_GENERATED_COMMANDS_COMPUTE_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDeviceGeneratedCommandsComputeFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDeviceGeneratedCommandsComputeFeaturesNV";
            generatedEmitBool(dst, section, "deviceGeneratedCompute", value.deviceGeneratedCompute);
            generatedEmitBool(dst, section, "deviceGeneratedComputeCaptureReplay", value.deviceGeneratedComputeCaptureReplay);
            generatedEmitBool(dst, section, "deviceGeneratedComputePipelines", value.deviceGeneratedComputePipelines);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEVICE_GENERATED_COMMANDS_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDeviceGeneratedCommandsFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDeviceGeneratedCommandsFeaturesEXT";
            generatedEmitBool(dst, section, "deviceGeneratedCommands", value.deviceGeneratedCommands);
            generatedEmitBool(dst, section, "dynamicGeneratedPipelineLayout", value.dynamicGeneratedPipelineLayout);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEVICE_GENERATED_COMMANDS_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDeviceGeneratedCommandsFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDeviceGeneratedCommandsFeaturesNV";
            generatedEmitBool(dst, section, "deviceGeneratedCommands", value.deviceGeneratedCommands);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEVICE_GENERATED_COMMANDS_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDeviceGeneratedCommandsPropertiesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDeviceGeneratedCommandsPropertiesEXT";
            generatedEmitBool(dst, section, "deviceGeneratedCommandsMultiDrawIndirectCount", value.deviceGeneratedCommandsMultiDrawIndirectCount);
            generatedEmitBool(dst, section, "deviceGeneratedCommandsTransformFeedback", value.deviceGeneratedCommandsTransformFeedback);
            generatedEmitAuto(dst, section, "maxIndirectCommandsIndirectStride", value.maxIndirectCommandsIndirectStride);
            generatedEmitAuto(dst, section, "maxIndirectCommandsTokenCount", value.maxIndirectCommandsTokenCount);
            generatedEmitAuto(dst, section, "maxIndirectCommandsTokenOffset", value.maxIndirectCommandsTokenOffset);
            generatedEmitAuto(dst, section, "maxIndirectPipelineCount", value.maxIndirectPipelineCount);
            generatedEmitAuto(dst, section, "maxIndirectSequenceCount", value.maxIndirectSequenceCount);
            generatedEmitAuto(dst, section, "maxIndirectShaderObjectCount", value.maxIndirectShaderObjectCount);
            generatedEmitAuto(dst, section, "supportedIndirectCommandsInputModes", value.supportedIndirectCommandsInputModes);
            generatedEmitAuto(dst, section, "supportedIndirectCommandsShaderStages", value.supportedIndirectCommandsShaderStages);
            generatedEmitAuto(dst, section, "supportedIndirectCommandsShaderStagesPipelineBinding", value.supportedIndirectCommandsShaderStagesPipelineBinding);
            generatedEmitAuto(dst, section, "supportedIndirectCommandsShaderStagesShaderBinding", value.supportedIndirectCommandsShaderStagesShaderBinding);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEVICE_MEMORY_REPORT_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDeviceMemoryReportFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDeviceMemoryReportFeaturesEXT";
            generatedEmitBool(dst, section, "deviceMemoryReport", value.deviceMemoryReport);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DISCARD_RECTANGLE_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDiscardRectanglePropertiesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDiscardRectanglePropertiesEXT";
            generatedEmitAuto(dst, section, "maxDiscardRectangles", value.maxDiscardRectangles);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DISPLACEMENT_MICROMAP_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDisplacementMicromapFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDisplacementMicromapFeaturesNV";
            generatedEmitBool(dst, section, "displacementMicromap", value.displacementMicromap);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DISPLACEMENT_MICROMAP_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDisplacementMicromapPropertiesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDisplacementMicromapPropertiesNV";
            generatedEmitAuto(dst, section, "maxDisplacementMicromapSubdivisionLevel", value.maxDisplacementMicromapSubdivisionLevel);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DRM_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDrmPropertiesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDrmPropertiesEXT";
            generatedEmitBool(dst, section, "hasPrimary", value.hasPrimary);
            generatedEmitBool(dst, section, "hasRender", value.hasRender);
            generatedEmitAuto(dst, section, "primaryMajor", value.primaryMajor);
            generatedEmitAuto(dst, section, "primaryMinor", value.primaryMinor);
            generatedEmitAuto(dst, section, "renderMajor", value.renderMajor);
            generatedEmitAuto(dst, section, "renderMinor", value.renderMinor);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DYNAMIC_RENDERING_UNUSED_ATTACHMENTS_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDynamicRenderingUnusedAttachmentsFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDynamicRenderingUnusedAttachmentsFeaturesEXT";
            generatedEmitBool(dst, section, "dynamicRenderingUnusedAttachments", value.dynamicRenderingUnusedAttachments);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXCLUSIVE_SCISSOR_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExclusiveScissorFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceExclusiveScissorFeaturesNV";
            generatedEmitBool(dst, section, "exclusiveScissor", value.exclusiveScissor);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_DYNAMIC_STATE_2_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExtendedDynamicState2FeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceExtendedDynamicState2FeaturesEXT";
            generatedEmitBool(dst, section, "extendedDynamicState2", value.extendedDynamicState2);
            generatedEmitBool(dst, section, "extendedDynamicState2LogicOp", value.extendedDynamicState2LogicOp);
            generatedEmitBool(dst, section, "extendedDynamicState2PatchControlPoints", value.extendedDynamicState2PatchControlPoints);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_DYNAMIC_STATE_3_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExtendedDynamicState3FeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceExtendedDynamicState3FeaturesEXT";
            generatedEmitBool(dst, section, "extendedDynamicState3AlphaToCoverageEnable", value.extendedDynamicState3AlphaToCoverageEnable);
            generatedEmitBool(dst, section, "extendedDynamicState3AlphaToOneEnable", value.extendedDynamicState3AlphaToOneEnable);
            generatedEmitBool(dst, section, "extendedDynamicState3ColorBlendAdvanced", value.extendedDynamicState3ColorBlendAdvanced);
            generatedEmitBool(dst, section, "extendedDynamicState3ColorBlendEnable", value.extendedDynamicState3ColorBlendEnable);
            generatedEmitBool(dst, section, "extendedDynamicState3ColorBlendEquation", value.extendedDynamicState3ColorBlendEquation);
            generatedEmitBool(dst, section, "extendedDynamicState3ColorWriteMask", value.extendedDynamicState3ColorWriteMask);
            generatedEmitBool(dst, section, "extendedDynamicState3ConservativeRasterizationMode", value.extendedDynamicState3ConservativeRasterizationMode);
            generatedEmitBool(dst, section, "extendedDynamicState3CoverageModulationMode", value.extendedDynamicState3CoverageModulationMode);
            generatedEmitBool(dst, section, "extendedDynamicState3CoverageModulationTable", value.extendedDynamicState3CoverageModulationTable);
            generatedEmitBool(dst, section, "extendedDynamicState3CoverageModulationTableEnable", value.extendedDynamicState3CoverageModulationTableEnable);
            generatedEmitBool(dst, section, "extendedDynamicState3CoverageReductionMode", value.extendedDynamicState3CoverageReductionMode);
            generatedEmitBool(dst, section, "extendedDynamicState3CoverageToColorEnable", value.extendedDynamicState3CoverageToColorEnable);
            generatedEmitBool(dst, section, "extendedDynamicState3CoverageToColorLocation", value.extendedDynamicState3CoverageToColorLocation);
            generatedEmitBool(dst, section, "extendedDynamicState3DepthClampEnable", value.extendedDynamicState3DepthClampEnable);
            generatedEmitBool(dst, section, "extendedDynamicState3DepthClipEnable", value.extendedDynamicState3DepthClipEnable);
            generatedEmitBool(dst, section, "extendedDynamicState3DepthClipNegativeOneToOne", value.extendedDynamicState3DepthClipNegativeOneToOne);
            generatedEmitBool(dst, section, "extendedDynamicState3ExtraPrimitiveOverestimationSize", value.extendedDynamicState3ExtraPrimitiveOverestimationSize);
            generatedEmitBool(dst, section, "extendedDynamicState3LineRasterizationMode", value.extendedDynamicState3LineRasterizationMode);
            generatedEmitBool(dst, section, "extendedDynamicState3LineStippleEnable", value.extendedDynamicState3LineStippleEnable);
            generatedEmitBool(dst, section, "extendedDynamicState3LogicOpEnable", value.extendedDynamicState3LogicOpEnable);
            generatedEmitBool(dst, section, "extendedDynamicState3PolygonMode", value.extendedDynamicState3PolygonMode);
            generatedEmitBool(dst, section, "extendedDynamicState3ProvokingVertexMode", value.extendedDynamicState3ProvokingVertexMode);
            generatedEmitBool(dst, section, "extendedDynamicState3RasterizationSamples", value.extendedDynamicState3RasterizationSamples);
            generatedEmitBool(dst, section, "extendedDynamicState3RasterizationStream", value.extendedDynamicState3RasterizationStream);
            generatedEmitBool(dst, section, "extendedDynamicState3RepresentativeFragmentTestEnable", value.extendedDynamicState3RepresentativeFragmentTestEnable);
            generatedEmitBool(dst, section, "extendedDynamicState3SampleLocationsEnable", value.extendedDynamicState3SampleLocationsEnable);
            generatedEmitBool(dst, section, "extendedDynamicState3SampleMask", value.extendedDynamicState3SampleMask);
            generatedEmitBool(dst, section, "extendedDynamicState3ShadingRateImageEnable", value.extendedDynamicState3ShadingRateImageEnable);
            generatedEmitBool(dst, section, "extendedDynamicState3TessellationDomainOrigin", value.extendedDynamicState3TessellationDomainOrigin);
            generatedEmitBool(dst, section, "extendedDynamicState3ViewportSwizzle", value.extendedDynamicState3ViewportSwizzle);
            generatedEmitBool(dst, section, "extendedDynamicState3ViewportWScalingEnable", value.extendedDynamicState3ViewportWScalingEnable);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_DYNAMIC_STATE_3_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExtendedDynamicState3PropertiesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceExtendedDynamicState3PropertiesEXT";
            generatedEmitBool(dst, section, "dynamicPrimitiveTopologyUnrestricted", value.dynamicPrimitiveTopologyUnrestricted);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_DYNAMIC_STATE_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExtendedDynamicStateFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceExtendedDynamicStateFeaturesEXT";
            generatedEmitBool(dst, section, "extendedDynamicState", value.extendedDynamicState);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_SPARSE_ADDRESS_SPACE_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExtendedSparseAddressSpaceFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceExtendedSparseAddressSpaceFeaturesNV";
            generatedEmitBool(dst, section, "extendedSparseAddressSpace", value.extendedSparseAddressSpace);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_SPARSE_ADDRESS_SPACE_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExtendedSparseAddressSpacePropertiesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceExtendedSparseAddressSpacePropertiesNV";
            generatedEmitAuto(dst, section, "extendedSparseAddressSpaceSize", value.extendedSparseAddressSpaceSize);
            generatedEmitAuto(dst, section, "extendedSparseBufferUsageFlags", value.extendedSparseBufferUsageFlags);
            generatedEmitAuto(dst, section, "extendedSparseImageUsageFlags", value.extendedSparseImageUsageFlags);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_COMPUTE_QUEUE_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExternalComputeQueuePropertiesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceExternalComputeQueuePropertiesNV";
            generatedEmitAuto(dst, section, "externalDataSize", value.externalDataSize);
            generatedEmitAuto(dst, section, "maxExternalQueues", value.maxExternalQueues);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_FORMAT_RESOLVE_FEATURES_ANDROID: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExternalFormatResolveFeaturesANDROID*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceExternalFormatResolveFeaturesANDROID";
            generatedEmitBool(dst, section, "externalFormatResolve", value.externalFormatResolve);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_FORMAT_RESOLVE_PROPERTIES_ANDROID: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExternalFormatResolvePropertiesANDROID*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceExternalFormatResolvePropertiesANDROID";
            generatedEmitAuto(dst, section, "externalFormatResolveChromaOffsetX", value.externalFormatResolveChromaOffsetX);
            generatedEmitAuto(dst, section, "externalFormatResolveChromaOffsetY", value.externalFormatResolveChromaOffsetY);
            generatedEmitBool(dst, section, "nullColorAttachmentWithExternalFormatResolve", value.nullColorAttachmentWithExternalFormatResolve);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_MEMORY_HOST_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExternalMemoryHostPropertiesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceExternalMemoryHostPropertiesEXT";
            generatedEmitAuto(dst, section, "minImportedHostPointerAlignment", value.minImportedHostPointerAlignment);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_MEMORY_RDMA_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExternalMemoryRDMAFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceExternalMemoryRDMAFeaturesNV";
            generatedEmitBool(dst, section, "externalMemoryRDMA", value.externalMemoryRDMA);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FAULT_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFaultFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceFaultFeaturesEXT";
            generatedEmitBool(dst, section, "deviceFault", value.deviceFault);
            generatedEmitBool(dst, section, "deviceFaultVendorBinary", value.deviceFaultVendorBinary);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FORMAT_PACK_FEATURES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFormatPackFeaturesARM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceFormatPackFeaturesARM";
            generatedEmitBool(dst, section, "formatPack", value.formatPack);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_2_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentDensityMap2FeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceFragmentDensityMap2FeaturesEXT";
            generatedEmitBool(dst, section, "fragmentDensityMapDeferred", value.fragmentDensityMapDeferred);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentDensityMapFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceFragmentDensityMapFeaturesEXT";
            generatedEmitBool(dst, section, "fragmentDensityMap", value.fragmentDensityMap);
            generatedEmitBool(dst, section, "fragmentDensityMapDynamic", value.fragmentDensityMapDynamic);
            generatedEmitBool(dst, section, "fragmentDensityMapNonSubsampledImages", value.fragmentDensityMapNonSubsampledImages);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_LAYERED_FEATURES_VALVE: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentDensityMapLayeredFeaturesVALVE*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceFragmentDensityMapLayeredFeaturesVALVE";
            generatedEmitBool(dst, section, "fragmentDensityMapLayered", value.fragmentDensityMapLayered);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_LAYERED_PROPERTIES_VALVE: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentDensityMapLayeredPropertiesVALVE*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceFragmentDensityMapLayeredPropertiesVALVE";
            generatedEmitAuto(dst, section, "maxFragmentDensityMapLayers", value.maxFragmentDensityMapLayers);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_OFFSET_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentDensityMapOffsetFeaturesQCOM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceFragmentDensityMapOffsetFeaturesQCOM";
            generatedEmitBool(dst, section, "fragmentDensityMapOffset", value.fragmentDensityMapOffset);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_OFFSET_PROPERTIES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentDensityMapOffsetPropertiesQCOM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceFragmentDensityMapOffsetPropertiesQCOM";
            generatedEmitAuto(dst, section, "fragmentDensityOffsetGranularity", value.fragmentDensityOffsetGranularity);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_SHADER_BARYCENTRIC_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentShaderBarycentricFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceFragmentShaderBarycentricFeaturesKHR";
            generatedEmitBool(dst, section, "fragmentShaderBarycentric", value.fragmentShaderBarycentric);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_SHADER_BARYCENTRIC_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentShaderBarycentricPropertiesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceFragmentShaderBarycentricPropertiesKHR";
            generatedEmitBool(dst, section, "triStripVertexOrderIndependentOfProvokingVertex", value.triStripVertexOrderIndependentOfProvokingVertex);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_SHADER_INTERLOCK_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentShaderInterlockFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceFragmentShaderInterlockFeaturesEXT";
            generatedEmitBool(dst, section, "fragmentShaderPixelInterlock", value.fragmentShaderPixelInterlock);
            generatedEmitBool(dst, section, "fragmentShaderSampleInterlock", value.fragmentShaderSampleInterlock);
            generatedEmitBool(dst, section, "fragmentShaderShadingRateInterlock", value.fragmentShaderShadingRateInterlock);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_SHADING_RATE_ENUMS_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentShadingRateEnumsFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceFragmentShadingRateEnumsFeaturesNV";
            generatedEmitBool(dst, section, "fragmentShadingRateEnums", value.fragmentShadingRateEnums);
            generatedEmitBool(dst, section, "noInvocationFragmentShadingRates", value.noInvocationFragmentShadingRates);
            generatedEmitBool(dst, section, "supersampleFragmentShadingRates", value.supersampleFragmentShadingRates);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_SHADING_RATE_ENUMS_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentShadingRateEnumsPropertiesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceFragmentShadingRateEnumsPropertiesNV";
            generatedEmitAuto(dst, section, "maxFragmentShadingRateInvocationCount", value.maxFragmentShadingRateInvocationCount);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_SHADING_RATE_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentShadingRateFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceFragmentShadingRateFeaturesKHR";
            generatedEmitBool(dst, section, "attachmentFragmentShadingRate", value.attachmentFragmentShadingRate);
            generatedEmitBool(dst, section, "pipelineFragmentShadingRate", value.pipelineFragmentShadingRate);
            generatedEmitBool(dst, section, "primitiveFragmentShadingRate", value.primitiveFragmentShadingRate);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_SHADING_RATE_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentShadingRatePropertiesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceFragmentShadingRatePropertiesKHR";
            generatedEmitBool(dst, section, "fragmentShadingRateNonTrivialCombinerOps", value.fragmentShadingRateNonTrivialCombinerOps);
            generatedEmitBool(dst, section, "fragmentShadingRateStrictMultiplyCombiner", value.fragmentShadingRateStrictMultiplyCombiner);
            generatedEmitBool(dst, section, "fragmentShadingRateWithConservativeRasterization", value.fragmentShadingRateWithConservativeRasterization);
            generatedEmitBool(dst, section, "fragmentShadingRateWithCustomSampleLocations", value.fragmentShadingRateWithCustomSampleLocations);
            generatedEmitBool(dst, section, "fragmentShadingRateWithFragmentShaderInterlock", value.fragmentShadingRateWithFragmentShaderInterlock);
            generatedEmitBool(dst, section, "fragmentShadingRateWithSampleMask", value.fragmentShadingRateWithSampleMask);
            generatedEmitBool(dst, section, "fragmentShadingRateWithShaderDepthStencilWrites", value.fragmentShadingRateWithShaderDepthStencilWrites);
            generatedEmitBool(dst, section, "fragmentShadingRateWithShaderSampleMask", value.fragmentShadingRateWithShaderSampleMask);
            generatedEmitBool(dst, section, "layeredShadingRateAttachments", value.layeredShadingRateAttachments);
            generatedEmitAuto(dst, section, "maxFragmentShadingRateAttachmentTexelSize", value.maxFragmentShadingRateAttachmentTexelSize);
            generatedEmitAuto(dst, section, "maxFragmentShadingRateAttachmentTexelSizeAspectRatio", value.maxFragmentShadingRateAttachmentTexelSizeAspectRatio);
            generatedEmitAuto(dst, section, "maxFragmentShadingRateCoverageSamples", value.maxFragmentShadingRateCoverageSamples);
            generatedEmitAuto(dst, section, "maxFragmentShadingRateRasterizationSamples", value.maxFragmentShadingRateRasterizationSamples);
            generatedEmitAuto(dst, section, "maxFragmentSize", value.maxFragmentSize);
            generatedEmitAuto(dst, section, "maxFragmentSizeAspectRatio", value.maxFragmentSizeAspectRatio);
            generatedEmitAuto(dst, section, "minFragmentShadingRateAttachmentTexelSize", value.minFragmentShadingRateAttachmentTexelSize);
            generatedEmitBool(dst, section, "primitiveFragmentShadingRateWithMultipleViewports", value.primitiveFragmentShadingRateWithMultipleViewports);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAME_BOUNDARY_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFrameBoundaryFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceFrameBoundaryFeaturesEXT";
            generatedEmitBool(dst, section, "frameBoundary", value.frameBoundary);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_GLOBAL_PRIORITY_QUERY_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceGlobalPriorityQueryFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceGlobalPriorityQueryFeaturesKHR";
            generatedEmitBool(dst, section, "globalPriorityQuery", value.globalPriorityQuery);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_GPA_FEATURES_AMD: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceGpaFeaturesAMD*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceGpaFeaturesAMD";
            generatedEmitBool(dst, section, "clockModes", value.clockModes);
            generatedEmitBool(dst, section, "perfCounters", value.perfCounters);
            generatedEmitBool(dst, section, "sqThreadTracing", value.sqThreadTracing);
            generatedEmitBool(dst, section, "streamingPerfCounters", value.streamingPerfCounters);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_GRAPHICS_PIPELINE_LIBRARY_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceGraphicsPipelineLibraryFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceGraphicsPipelineLibraryFeaturesEXT";
            generatedEmitBool(dst, section, "graphicsPipelineLibrary", value.graphicsPipelineLibrary);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_GRAPHICS_PIPELINE_LIBRARY_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceGraphicsPipelineLibraryPropertiesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceGraphicsPipelineLibraryPropertiesEXT";
            generatedEmitBool(dst, section, "graphicsPipelineLibraryFastLinking", value.graphicsPipelineLibraryFastLinking);
            generatedEmitBool(dst, section, "graphicsPipelineLibraryIndependentInterpolationDecoration", value.graphicsPipelineLibraryIndependentInterpolationDecoration);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_HDR_VIVID_FEATURES_HUAWEI: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceHdrVividFeaturesHUAWEI*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceHdrVividFeaturesHUAWEI";
            generatedEmitBool(dst, section, "hdrVivid", value.hdrVivid);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_HOST_IMAGE_COPY_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceHostImageCopyFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceHostImageCopyFeaturesEXT";
            generatedEmitBool(dst, section, "hostImageCopy", value.hostImageCopy);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_HOST_IMAGE_COPY_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceHostImageCopyPropertiesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceHostImageCopyPropertiesEXT";
            generatedEmitAuto(dst, section, "copyDstLayoutCount", value.copyDstLayoutCount);
            generatedEmitAuto(dst, section, "copySrcLayoutCount", value.copySrcLayoutCount);
            generatedEmitBool(dst, section, "identicalMemoryTypeRequirements", value.identicalMemoryTypeRequirements);
            generatedEmitAuto(dst, section, "optimalTilingLayoutUUID", value.optimalTilingLayoutUUID);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_ALIGNMENT_CONTROL_FEATURES_MESA: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceImageAlignmentControlFeaturesMESA*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceImageAlignmentControlFeaturesMESA";
            generatedEmitBool(dst, section, "imageAlignmentControl", value.imageAlignmentControl);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_ALIGNMENT_CONTROL_PROPERTIES_MESA: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceImageAlignmentControlPropertiesMESA*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceImageAlignmentControlPropertiesMESA";
            generatedEmitAuto(dst, section, "supportedImageAlignmentMask", value.supportedImageAlignmentMask);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_COMPRESSION_CONTROL_SWAPCHAIN_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceImageCompressionControlSwapchainFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceImageCompressionControlSwapchainFeaturesEXT";
            generatedEmitBool(dst, section, "imageCompressionControlSwapchain", value.imageCompressionControlSwapchain);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_PROCESSING_2_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceImageProcessing2FeaturesQCOM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceImageProcessing2FeaturesQCOM";
            generatedEmitBool(dst, section, "textureBlockMatch2", value.textureBlockMatch2);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_PROCESSING_2_PROPERTIES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceImageProcessing2PropertiesQCOM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceImageProcessing2PropertiesQCOM";
            generatedEmitAuto(dst, section, "maxBlockMatchWindow", value.maxBlockMatchWindow);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_PROCESSING_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceImageProcessingFeaturesQCOM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceImageProcessingFeaturesQCOM";
            generatedEmitBool(dst, section, "textureBlockMatch", value.textureBlockMatch);
            generatedEmitBool(dst, section, "textureBoxFilter", value.textureBoxFilter);
            generatedEmitBool(dst, section, "textureSampleWeighted", value.textureSampleWeighted);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_PROCESSING_PROPERTIES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceImageProcessingPropertiesQCOM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceImageProcessingPropertiesQCOM";
            generatedEmitAuto(dst, section, "maxBlockMatchRegion", value.maxBlockMatchRegion);
            generatedEmitAuto(dst, section, "maxBoxFilterBlockSize", value.maxBoxFilterBlockSize);
            generatedEmitAuto(dst, section, "maxWeightFilterDimension", value.maxWeightFilterDimension);
            generatedEmitAuto(dst, section, "maxWeightFilterPhases", value.maxWeightFilterPhases);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_SLICED_VIEW_OF_3D_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceImageSlicedViewOf3DFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceImageSlicedViewOf3DFeaturesEXT";
            generatedEmitBool(dst, section, "imageSlicedViewOf3D", value.imageSlicedViewOf3D);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_VIEW_MIN_LOD_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceImageViewMinLodFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceImageViewMinLodFeaturesEXT";
            generatedEmitBool(dst, section, "minLod", value.minLod);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INDEX_TYPE_UINT8_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceIndexTypeUint8FeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceIndexTypeUint8FeaturesKHR";
            generatedEmitBool(dst, section, "indexTypeUint8", value.indexTypeUint8);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INHERITED_VIEWPORT_SCISSOR_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceInheritedViewportScissorFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceInheritedViewportScissorFeaturesNV";
            generatedEmitBool(dst, section, "inheritedViewportScissor2D", value.inheritedViewportScissor2D);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INVOCATION_MASK_FEATURES_HUAWEI: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceInvocationMaskFeaturesHUAWEI*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceInvocationMaskFeaturesHUAWEI";
            generatedEmitBool(dst, section, "invocationMask", value.invocationMask);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LAYERED_DRIVER_PROPERTIES_MSFT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceLayeredDriverPropertiesMSFT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceLayeredDriverPropertiesMSFT";
            generatedEmitAuto(dst, section, "underlyingAPI", value.underlyingAPI);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LEGACY_DITHERING_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceLegacyDitheringFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceLegacyDitheringFeaturesEXT";
            generatedEmitBool(dst, section, "legacyDithering", value.legacyDithering);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LEGACY_VERTEX_ATTRIBUTES_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceLegacyVertexAttributesFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceLegacyVertexAttributesFeaturesEXT";
            generatedEmitBool(dst, section, "legacyVertexAttributes", value.legacyVertexAttributes);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LEGACY_VERTEX_ATTRIBUTES_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceLegacyVertexAttributesPropertiesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceLegacyVertexAttributesPropertiesEXT";
            generatedEmitBool(dst, section, "nativeUnalignedPerformance", value.nativeUnalignedPerformance);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINE_RASTERIZATION_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceLineRasterizationFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceLineRasterizationFeaturesKHR";
            generatedEmitBool(dst, section, "bresenhamLines", value.bresenhamLines);
            generatedEmitBool(dst, section, "rectangularLines", value.rectangularLines);
            generatedEmitBool(dst, section, "smoothLines", value.smoothLines);
            generatedEmitBool(dst, section, "stippledBresenhamLines", value.stippledBresenhamLines);
            generatedEmitBool(dst, section, "stippledRectangularLines", value.stippledRectangularLines);
            generatedEmitBool(dst, section, "stippledSmoothLines", value.stippledSmoothLines);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINE_RASTERIZATION_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceLineRasterizationPropertiesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceLineRasterizationPropertiesKHR";
            generatedEmitAuto(dst, section, "lineSubPixelPrecisionBits", value.lineSubPixelPrecisionBits);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINEAR_COLOR_ATTACHMENT_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceLinearColorAttachmentFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceLinearColorAttachmentFeaturesNV";
            generatedEmitBool(dst, section, "linearColorAttachment", value.linearColorAttachment);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_5_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMaintenance5PropertiesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceMaintenance5PropertiesKHR";
            generatedEmitBool(dst, section, "depthStencilSwizzleOneSupport", value.depthStencilSwizzleOneSupport);
            generatedEmitBool(dst, section, "earlyFragmentMultisampleCoverageAfterSampleCounting", value.earlyFragmentMultisampleCoverageAfterSampleCounting);
            generatedEmitBool(dst, section, "earlyFragmentSampleMaskTestBeforeSampleCounting", value.earlyFragmentSampleMaskTestBeforeSampleCounting);
            generatedEmitBool(dst, section, "nonStrictSinglePixelWideLinesUseParallelogram", value.nonStrictSinglePixelWideLinesUseParallelogram);
            generatedEmitBool(dst, section, "nonStrictWideLinesUseParallelogram", value.nonStrictWideLinesUseParallelogram);
            generatedEmitBool(dst, section, "polygonModePointSize", value.polygonModePointSize);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_6_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMaintenance6FeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceMaintenance6FeaturesKHR";
            generatedEmitBool(dst, section, "maintenance6", value.maintenance6);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_6_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMaintenance6PropertiesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceMaintenance6PropertiesKHR";
            generatedEmitBool(dst, section, "blockTexelViewCompatibleMultipleLayers", value.blockTexelViewCompatibleMultipleLayers);
            generatedEmitBool(dst, section, "fragmentShadingRateClampCombinerInputs", value.fragmentShadingRateClampCombinerInputs);
            generatedEmitAuto(dst, section, "maxCombinedImageSamplerDescriptorCount", value.maxCombinedImageSamplerDescriptorCount);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAP_MEMORY_PLACED_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMapMemoryPlacedFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceMapMemoryPlacedFeaturesEXT";
            generatedEmitBool(dst, section, "memoryMapPlaced", value.memoryMapPlaced);
            generatedEmitBool(dst, section, "memoryMapRangePlaced", value.memoryMapRangePlaced);
            generatedEmitBool(dst, section, "memoryUnmapReserve", value.memoryUnmapReserve);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MEMORY_DECOMPRESSION_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMemoryDecompressionFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceMemoryDecompressionFeaturesNV";
            generatedEmitBool(dst, section, "memoryDecompression", value.memoryDecompression);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MEMORY_DECOMPRESSION_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMemoryDecompressionPropertiesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceMemoryDecompressionPropertiesNV";
            generatedEmitAuto(dst, section, "decompressionMethods", value.decompressionMethods);
            generatedEmitAuto(dst, section, "maxDecompressionIndirectCount", value.maxDecompressionIndirectCount);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MEMORY_PRIORITY_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMemoryPriorityFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceMemoryPriorityFeaturesEXT";
            generatedEmitBool(dst, section, "memoryPriority", value.memoryPriority);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MESH_SHADER_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMeshShaderFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceMeshShaderFeaturesEXT";
            generatedEmitBool(dst, section, "meshShader", value.meshShader);
            generatedEmitBool(dst, section, "meshShaderQueries", value.meshShaderQueries);
            generatedEmitBool(dst, section, "multiviewMeshShader", value.multiviewMeshShader);
            generatedEmitBool(dst, section, "primitiveFragmentShadingRateMeshShader", value.primitiveFragmentShadingRateMeshShader);
            generatedEmitBool(dst, section, "taskShader", value.taskShader);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MESH_SHADER_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMeshShaderFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceMeshShaderFeaturesNV";
            generatedEmitBool(dst, section, "meshShader", value.meshShader);
            generatedEmitBool(dst, section, "taskShader", value.taskShader);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MESH_SHADER_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMeshShaderPropertiesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceMeshShaderPropertiesEXT";
            generatedEmitAuto(dst, section, "maxMeshMultiviewViewCount", value.maxMeshMultiviewViewCount);
            generatedEmitAuto(dst, section, "maxMeshOutputComponents", value.maxMeshOutputComponents);
            generatedEmitAuto(dst, section, "maxMeshOutputLayers", value.maxMeshOutputLayers);
            generatedEmitAuto(dst, section, "maxMeshOutputMemorySize", value.maxMeshOutputMemorySize);
            generatedEmitAuto(dst, section, "maxMeshOutputPrimitives", value.maxMeshOutputPrimitives);
            generatedEmitAuto(dst, section, "maxMeshOutputVertices", value.maxMeshOutputVertices);
            generatedEmitAuto(dst, section, "maxMeshPayloadAndOutputMemorySize", value.maxMeshPayloadAndOutputMemorySize);
            generatedEmitAuto(dst, section, "maxMeshPayloadAndSharedMemorySize", value.maxMeshPayloadAndSharedMemorySize);
            generatedEmitAuto(dst, section, "maxMeshSharedMemorySize", value.maxMeshSharedMemorySize);
            generatedEmitAuto(dst, section, "maxMeshWorkGroupCount", value.maxMeshWorkGroupCount);
            generatedEmitAuto(dst, section, "maxMeshWorkGroupInvocations", value.maxMeshWorkGroupInvocations);
            generatedEmitAuto(dst, section, "maxMeshWorkGroupSize", value.maxMeshWorkGroupSize);
            generatedEmitAuto(dst, section, "maxMeshWorkGroupTotalCount", value.maxMeshWorkGroupTotalCount);
            generatedEmitAuto(dst, section, "maxPreferredMeshWorkGroupInvocations", value.maxPreferredMeshWorkGroupInvocations);
            generatedEmitAuto(dst, section, "maxPreferredTaskWorkGroupInvocations", value.maxPreferredTaskWorkGroupInvocations);
            generatedEmitAuto(dst, section, "maxTaskPayloadAndSharedMemorySize", value.maxTaskPayloadAndSharedMemorySize);
            generatedEmitAuto(dst, section, "maxTaskPayloadSize", value.maxTaskPayloadSize);
            generatedEmitAuto(dst, section, "maxTaskSharedMemorySize", value.maxTaskSharedMemorySize);
            generatedEmitAuto(dst, section, "maxTaskWorkGroupCount", value.maxTaskWorkGroupCount);
            generatedEmitAuto(dst, section, "maxTaskWorkGroupInvocations", value.maxTaskWorkGroupInvocations);
            generatedEmitAuto(dst, section, "maxTaskWorkGroupSize", value.maxTaskWorkGroupSize);
            generatedEmitAuto(dst, section, "maxTaskWorkGroupTotalCount", value.maxTaskWorkGroupTotalCount);
            generatedEmitAuto(dst, section, "meshOutputPerPrimitiveGranularity", value.meshOutputPerPrimitiveGranularity);
            generatedEmitAuto(dst, section, "meshOutputPerVertexGranularity", value.meshOutputPerVertexGranularity);
            generatedEmitBool(dst, section, "prefersCompactPrimitiveOutput", value.prefersCompactPrimitiveOutput);
            generatedEmitBool(dst, section, "prefersCompactVertexOutput", value.prefersCompactVertexOutput);
            generatedEmitBool(dst, section, "prefersLocalInvocationPrimitiveOutput", value.prefersLocalInvocationPrimitiveOutput);
            generatedEmitBool(dst, section, "prefersLocalInvocationVertexOutput", value.prefersLocalInvocationVertexOutput);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MESH_SHADER_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMeshShaderPropertiesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceMeshShaderPropertiesNV";
            generatedEmitAuto(dst, section, "maxDrawMeshTasksCount", value.maxDrawMeshTasksCount);
            generatedEmitAuto(dst, section, "maxMeshMultiviewViewCount", value.maxMeshMultiviewViewCount);
            generatedEmitAuto(dst, section, "maxMeshOutputPrimitives", value.maxMeshOutputPrimitives);
            generatedEmitAuto(dst, section, "maxMeshOutputVertices", value.maxMeshOutputVertices);
            generatedEmitAuto(dst, section, "maxMeshTotalMemorySize", value.maxMeshTotalMemorySize);
            generatedEmitAuto(dst, section, "maxMeshWorkGroupInvocations", value.maxMeshWorkGroupInvocations);
            generatedEmitAuto(dst, section, "maxMeshWorkGroupSize", value.maxMeshWorkGroupSize);
            generatedEmitAuto(dst, section, "maxTaskOutputCount", value.maxTaskOutputCount);
            generatedEmitAuto(dst, section, "maxTaskTotalMemorySize", value.maxTaskTotalMemorySize);
            generatedEmitAuto(dst, section, "maxTaskWorkGroupInvocations", value.maxTaskWorkGroupInvocations);
            generatedEmitAuto(dst, section, "maxTaskWorkGroupSize", value.maxTaskWorkGroupSize);
            generatedEmitAuto(dst, section, "meshOutputPerPrimitiveGranularity", value.meshOutputPerPrimitiveGranularity);
            generatedEmitAuto(dst, section, "meshOutputPerVertexGranularity", value.meshOutputPerVertexGranularity);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTI_DRAW_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMultiDrawFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceMultiDrawFeaturesEXT";
            generatedEmitBool(dst, section, "multiDraw", value.multiDraw);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTI_DRAW_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMultiDrawPropertiesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceMultiDrawPropertiesEXT";
            generatedEmitAuto(dst, section, "maxMultiDrawCount", value.maxMultiDrawCount);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTISAMPLED_RENDER_TO_SINGLE_SAMPLED_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMultisampledRenderToSingleSampledFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceMultisampledRenderToSingleSampledFeaturesEXT";
            generatedEmitBool(dst, section, "multisampledRenderToSingleSampled", value.multisampledRenderToSingleSampled);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTIVIEW_PER_VIEW_ATTRIBUTES_PROPERTIES_NVX: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMultiviewPerViewAttributesPropertiesNVX*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceMultiviewPerViewAttributesPropertiesNVX";
            generatedEmitBool(dst, section, "perViewPositionAllComponents", value.perViewPositionAllComponents);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTIVIEW_PER_VIEW_RENDER_AREAS_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMultiviewPerViewRenderAreasFeaturesQCOM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceMultiviewPerViewRenderAreasFeaturesQCOM";
            generatedEmitBool(dst, section, "multiviewPerViewRenderAreas", value.multiviewPerViewRenderAreas);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTIVIEW_PER_VIEW_VIEWPORTS_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMultiviewPerViewViewportsFeaturesQCOM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceMultiviewPerViewViewportsFeaturesQCOM";
            generatedEmitBool(dst, section, "multiviewPerViewViewports", value.multiviewPerViewViewports);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MUTABLE_DESCRIPTOR_TYPE_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMutableDescriptorTypeFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceMutableDescriptorTypeFeaturesEXT";
            generatedEmitBool(dst, section, "mutableDescriptorType", value.mutableDescriptorType);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_NESTED_COMMAND_BUFFER_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceNestedCommandBufferFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceNestedCommandBufferFeaturesEXT";
            generatedEmitBool(dst, section, "nestedCommandBuffer", value.nestedCommandBuffer);
            generatedEmitBool(dst, section, "nestedCommandBufferRendering", value.nestedCommandBufferRendering);
            generatedEmitBool(dst, section, "nestedCommandBufferSimultaneousUse", value.nestedCommandBufferSimultaneousUse);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_NESTED_COMMAND_BUFFER_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceNestedCommandBufferPropertiesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceNestedCommandBufferPropertiesEXT";
            generatedEmitAuto(dst, section, "maxCommandBufferNestingLevel", value.maxCommandBufferNestingLevel);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_NON_SEAMLESS_CUBE_MAP_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceNonSeamlessCubeMapFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceNonSeamlessCubeMapFeaturesEXT";
            generatedEmitBool(dst, section, "nonSeamlessCubeMap", value.nonSeamlessCubeMap);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_OPACITY_MICROMAP_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceOpacityMicromapFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceOpacityMicromapFeaturesEXT";
            generatedEmitBool(dst, section, "micromap", value.micromap);
            generatedEmitBool(dst, section, "micromapCaptureReplay", value.micromapCaptureReplay);
            generatedEmitBool(dst, section, "micromapHostCommands", value.micromapHostCommands);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_OPACITY_MICROMAP_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceOpacityMicromapPropertiesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceOpacityMicromapPropertiesEXT";
            generatedEmitAuto(dst, section, "maxOpacity2StateSubdivisionLevel", value.maxOpacity2StateSubdivisionLevel);
            generatedEmitAuto(dst, section, "maxOpacity4StateSubdivisionLevel", value.maxOpacity4StateSubdivisionLevel);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_OPTICAL_FLOW_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceOpticalFlowFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceOpticalFlowFeaturesNV";
            generatedEmitBool(dst, section, "opticalFlow", value.opticalFlow);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_OPTICAL_FLOW_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceOpticalFlowPropertiesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceOpticalFlowPropertiesNV";
            generatedEmitBool(dst, section, "bidirectionalFlowSupported", value.bidirectionalFlowSupported);
            generatedEmitBool(dst, section, "costSupported", value.costSupported);
            generatedEmitBool(dst, section, "globalFlowSupported", value.globalFlowSupported);
            generatedEmitBool(dst, section, "hintSupported", value.hintSupported);
            generatedEmitAuto(dst, section, "maxHeight", value.maxHeight);
            generatedEmitAuto(dst, section, "maxNumRegionsOfInterest", value.maxNumRegionsOfInterest);
            generatedEmitAuto(dst, section, "maxWidth", value.maxWidth);
            generatedEmitAuto(dst, section, "minHeight", value.minHeight);
            generatedEmitAuto(dst, section, "minWidth", value.minWidth);
            generatedEmitAuto(dst, section, "supportedHintGridSizes", value.supportedHintGridSizes);
            generatedEmitAuto(dst, section, "supportedOutputGridSizes", value.supportedOutputGridSizes);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PCI_BUS_INFO_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePCIBusInfoPropertiesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevicePCIBusInfoPropertiesEXT";
            generatedEmitAuto(dst, section, "pciBus", value.pciBus);
            generatedEmitAuto(dst, section, "pciDevice", value.pciDevice);
            generatedEmitAuto(dst, section, "pciDomain", value.pciDomain);
            generatedEmitAuto(dst, section, "pciFunction", value.pciFunction);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PAGEABLE_DEVICE_LOCAL_MEMORY_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePageableDeviceLocalMemoryFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevicePageableDeviceLocalMemoryFeaturesEXT";
            generatedEmitBool(dst, section, "pageableDeviceLocalMemory", value.pageableDeviceLocalMemory);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PARTITIONED_ACCELERATION_STRUCTURE_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePartitionedAccelerationStructureFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevicePartitionedAccelerationStructureFeaturesNV";
            generatedEmitBool(dst, section, "partitionedAccelerationStructure", value.partitionedAccelerationStructure);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PARTITIONED_ACCELERATION_STRUCTURE_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePartitionedAccelerationStructurePropertiesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevicePartitionedAccelerationStructurePropertiesNV";
            generatedEmitAuto(dst, section, "maxPartitionCount", value.maxPartitionCount);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PER_STAGE_DESCRIPTOR_SET_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePerStageDescriptorSetFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevicePerStageDescriptorSetFeaturesNV";
            generatedEmitBool(dst, section, "dynamicPipelineLayout", value.dynamicPipelineLayout);
            generatedEmitBool(dst, section, "perStageDescriptorSet", value.perStageDescriptorSet);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PERFORMANCE_COUNTERS_BY_REGION_PROPERTIES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePerformanceCountersByRegionPropertiesARM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevicePerformanceCountersByRegionPropertiesARM";
            generatedEmitBool(dst, section, "identityTransformOrder", value.identityTransformOrder);
            generatedEmitAuto(dst, section, "maxPerRegionPerformanceCounters", value.maxPerRegionPerformanceCounters);
            generatedEmitAuto(dst, section, "performanceCounterRegionSize", value.performanceCounterRegionSize);
            generatedEmitAuto(dst, section, "regionAlignment", value.regionAlignment);
            generatedEmitAuto(dst, section, "rowStrideAlignment", value.rowStrideAlignment);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PERFORMANCE_QUERY_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePerformanceQueryFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevicePerformanceQueryFeaturesKHR";
            generatedEmitBool(dst, section, "performanceCounterMultipleQueryPools", value.performanceCounterMultipleQueryPools);
            generatedEmitBool(dst, section, "performanceCounterQueryPools", value.performanceCounterQueryPools);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PERFORMANCE_QUERY_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePerformanceQueryPropertiesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevicePerformanceQueryPropertiesKHR";
            generatedEmitBool(dst, section, "allowCommandBufferQueryCopies", value.allowCommandBufferQueryCopies);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_EXECUTABLE_PROPERTIES_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePipelineExecutablePropertiesFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevicePipelineExecutablePropertiesFeaturesKHR";
            generatedEmitBool(dst, section, "pipelineExecutableInfo", value.pipelineExecutableInfo);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_LIBRARY_GROUP_HANDLES_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePipelineLibraryGroupHandlesFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevicePipelineLibraryGroupHandlesFeaturesEXT";
            generatedEmitBool(dst, section, "pipelineLibraryGroupHandles", value.pipelineLibraryGroupHandles);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_PROPERTIES_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePipelinePropertiesFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevicePipelinePropertiesFeaturesEXT";
            generatedEmitBool(dst, section, "pipelinePropertiesIdentifier", value.pipelinePropertiesIdentifier);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_PROTECTED_ACCESS_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePipelineProtectedAccessFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevicePipelineProtectedAccessFeaturesEXT";
            generatedEmitBool(dst, section, "pipelineProtectedAccess", value.pipelineProtectedAccess);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_ROBUSTNESS_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePipelineRobustnessPropertiesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevicePipelineRobustnessPropertiesEXT";
            generatedEmitAuto(dst, section, "defaultRobustnessImages", value.defaultRobustnessImages);
            generatedEmitAuto(dst, section, "defaultRobustnessStorageBuffers", value.defaultRobustnessStorageBuffers);
            generatedEmitAuto(dst, section, "defaultRobustnessUniformBuffers", value.defaultRobustnessUniformBuffers);
            generatedEmitAuto(dst, section, "defaultRobustnessVertexInputs", value.defaultRobustnessVertexInputs);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PORTABILITY_SUBSET_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePortabilitySubsetFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevicePortabilitySubsetFeaturesKHR";
            generatedEmitBool(dst, section, "constantAlphaColorBlendFactors", value.constantAlphaColorBlendFactors);
            generatedEmitBool(dst, section, "events", value.events);
            generatedEmitBool(dst, section, "imageView2DOn3DImage", value.imageView2DOn3DImage);
            generatedEmitBool(dst, section, "imageViewFormatReinterpretation", value.imageViewFormatReinterpretation);
            generatedEmitBool(dst, section, "imageViewFormatSwizzle", value.imageViewFormatSwizzle);
            generatedEmitBool(dst, section, "multisampleArrayImage", value.multisampleArrayImage);
            generatedEmitBool(dst, section, "mutableComparisonSamplers", value.mutableComparisonSamplers);
            generatedEmitBool(dst, section, "pointPolygons", value.pointPolygons);
            generatedEmitBool(dst, section, "samplerMipLodBias", value.samplerMipLodBias);
            generatedEmitBool(dst, section, "separateStencilMaskRef", value.separateStencilMaskRef);
            generatedEmitBool(dst, section, "shaderSampleRateInterpolationFunctions", value.shaderSampleRateInterpolationFunctions);
            generatedEmitBool(dst, section, "tessellationIsolines", value.tessellationIsolines);
            generatedEmitBool(dst, section, "tessellationPointMode", value.tessellationPointMode);
            generatedEmitBool(dst, section, "triangleFans", value.triangleFans);
            generatedEmitBool(dst, section, "vertexAttributeAccessBeyondStride", value.vertexAttributeAccessBeyondStride);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_ID_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePresentIdFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevicePresentIdFeaturesKHR";
            generatedEmitBool(dst, section, "presentId", value.presentId);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_MODE_FIFO_LATEST_READY_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePresentModeFifoLatestReadyFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevicePresentModeFifoLatestReadyFeaturesEXT";
            generatedEmitBool(dst, section, "presentModeFifoLatestReady", value.presentModeFifoLatestReady);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_TIMING_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePresentTimingFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevicePresentTimingFeaturesEXT";
            generatedEmitBool(dst, section, "presentAtAbsoluteTime", value.presentAtAbsoluteTime);
            generatedEmitBool(dst, section, "presentAtRelativeTime", value.presentAtRelativeTime);
            generatedEmitBool(dst, section, "presentTiming", value.presentTiming);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_WAIT_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePresentWaitFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevicePresentWaitFeaturesKHR";
            generatedEmitBool(dst, section, "presentWait", value.presentWait);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRIMITIVE_TOPOLOGY_LIST_RESTART_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePrimitiveTopologyListRestartFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevicePrimitiveTopologyListRestartFeaturesEXT";
            generatedEmitBool(dst, section, "primitiveTopologyListRestart", value.primitiveTopologyListRestart);
            generatedEmitBool(dst, section, "primitiveTopologyPatchListRestart", value.primitiveTopologyPatchListRestart);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRIMITIVES_GENERATED_QUERY_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePrimitivesGeneratedQueryFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevicePrimitivesGeneratedQueryFeaturesEXT";
            generatedEmitBool(dst, section, "primitivesGeneratedQuery", value.primitivesGeneratedQuery);
            generatedEmitBool(dst, section, "primitivesGeneratedQueryWithNonZeroStreams", value.primitivesGeneratedQueryWithNonZeroStreams);
            generatedEmitBool(dst, section, "primitivesGeneratedQueryWithRasterizerDiscard", value.primitivesGeneratedQueryWithRasterizerDiscard);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROVOKING_VERTEX_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceProvokingVertexFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceProvokingVertexFeaturesEXT";
            generatedEmitBool(dst, section, "provokingVertexLast", value.provokingVertexLast);
            generatedEmitBool(dst, section, "transformFeedbackPreservesProvokingVertex", value.transformFeedbackPreservesProvokingVertex);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PUSH_DESCRIPTOR_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePushDescriptorPropertiesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevicePushDescriptorPropertiesKHR";
            generatedEmitAuto(dst, section, "maxPushDescriptors", value.maxPushDescriptors);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RGBA10X6_FORMATS_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRGBA10X6FormatsFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceRGBA10X6FormatsFeaturesEXT";
            generatedEmitBool(dst, section, "formatRgba10x6WithoutYCbCrSampler", value.formatRgba10x6WithoutYCbCrSampler);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RASTERIZATION_ORDER_ATTACHMENT_ACCESS_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRasterizationOrderAttachmentAccessFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceRasterizationOrderAttachmentAccessFeaturesEXT";
            generatedEmitBool(dst, section, "rasterizationOrderColorAttachmentAccess", value.rasterizationOrderColorAttachmentAccess);
            generatedEmitBool(dst, section, "rasterizationOrderDepthAttachmentAccess", value.rasterizationOrderDepthAttachmentAccess);
            generatedEmitBool(dst, section, "rasterizationOrderStencilAttachmentAccess", value.rasterizationOrderStencilAttachmentAccess);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAW_ACCESS_CHAINS_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRawAccessChainsFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceRawAccessChainsFeaturesNV";
            generatedEmitBool(dst, section, "shaderRawAccessChains", value.shaderRawAccessChains);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_QUERY_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRayQueryFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceRayQueryFeaturesKHR";
            generatedEmitBool(dst, section, "rayQuery", value.rayQuery);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_INVOCATION_REORDER_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRayTracingInvocationReorderFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceRayTracingInvocationReorderFeaturesEXT";
            generatedEmitBool(dst, section, "rayTracingInvocationReorder", value.rayTracingInvocationReorder);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_INVOCATION_REORDER_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRayTracingInvocationReorderFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceRayTracingInvocationReorderFeaturesNV";
            generatedEmitBool(dst, section, "rayTracingInvocationReorder", value.rayTracingInvocationReorder);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_INVOCATION_REORDER_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRayTracingInvocationReorderPropertiesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceRayTracingInvocationReorderPropertiesEXT";
            generatedEmitAuto(dst, section, "maxShaderBindingTableRecordIndex", value.maxShaderBindingTableRecordIndex);
            generatedEmitAuto(dst, section, "rayTracingInvocationReorderReorderingHint", value.rayTracingInvocationReorderReorderingHint);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_INVOCATION_REORDER_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRayTracingInvocationReorderPropertiesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceRayTracingInvocationReorderPropertiesNV";
            generatedEmitAuto(dst, section, "rayTracingInvocationReorderReorderingHint", value.rayTracingInvocationReorderReorderingHint);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_LINEAR_SWEPT_SPHERES_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRayTracingLinearSweptSpheresFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceRayTracingLinearSweptSpheresFeaturesNV";
            generatedEmitBool(dst, section, "linearSweptSpheres", value.linearSweptSpheres);
            generatedEmitBool(dst, section, "spheres", value.spheres);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_MAINTENANCE_1_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRayTracingMaintenance1FeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceRayTracingMaintenance1FeaturesKHR";
            generatedEmitBool(dst, section, "rayTracingMaintenance1", value.rayTracingMaintenance1);
            generatedEmitBool(dst, section, "rayTracingPipelineTraceRaysIndirect2", value.rayTracingPipelineTraceRaysIndirect2);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_MOTION_BLUR_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRayTracingMotionBlurFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceRayTracingMotionBlurFeaturesNV";
            generatedEmitBool(dst, section, "rayTracingMotionBlur", value.rayTracingMotionBlur);
            generatedEmitBool(dst, section, "rayTracingMotionBlurPipelineTraceRaysIndirect", value.rayTracingMotionBlurPipelineTraceRaysIndirect);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_PIPELINE_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRayTracingPipelineFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceRayTracingPipelineFeaturesKHR";
            generatedEmitBool(dst, section, "rayTracingPipeline", value.rayTracingPipeline);
            generatedEmitBool(dst, section, "rayTracingPipelineShaderGroupHandleCaptureReplay", value.rayTracingPipelineShaderGroupHandleCaptureReplay);
            generatedEmitBool(dst, section, "rayTracingPipelineShaderGroupHandleCaptureReplayMixed", value.rayTracingPipelineShaderGroupHandleCaptureReplayMixed);
            generatedEmitBool(dst, section, "rayTracingPipelineTraceRaysIndirect", value.rayTracingPipelineTraceRaysIndirect);
            generatedEmitBool(dst, section, "rayTraversalPrimitiveCulling", value.rayTraversalPrimitiveCulling);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_POSITION_FETCH_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRayTracingPositionFetchFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceRayTracingPositionFetchFeaturesKHR";
            generatedEmitBool(dst, section, "rayTracingPositionFetch", value.rayTracingPositionFetch);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRayTracingPropertiesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceRayTracingPropertiesNV";
            generatedEmitAuto(dst, section, "maxDescriptorSetAccelerationStructures", value.maxDescriptorSetAccelerationStructures);
            generatedEmitAuto(dst, section, "maxGeometryCount", value.maxGeometryCount);
            generatedEmitAuto(dst, section, "maxInstanceCount", value.maxInstanceCount);
            generatedEmitAuto(dst, section, "maxRecursionDepth", value.maxRecursionDepth);
            generatedEmitAuto(dst, section, "maxShaderGroupStride", value.maxShaderGroupStride);
            generatedEmitAuto(dst, section, "maxTriangleCount", value.maxTriangleCount);
            generatedEmitAuto(dst, section, "shaderGroupBaseAlignment", value.shaderGroupBaseAlignment);
            generatedEmitAuto(dst, section, "shaderGroupHandleSize", value.shaderGroupHandleSize);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_VALIDATION_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRayTracingValidationFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceRayTracingValidationFeaturesNV";
            generatedEmitBool(dst, section, "rayTracingValidation", value.rayTracingValidation);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RELAXED_LINE_RASTERIZATION_FEATURES_IMG: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRelaxedLineRasterizationFeaturesIMG*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceRelaxedLineRasterizationFeaturesIMG";
            generatedEmitBool(dst, section, "relaxedLineRasterization", value.relaxedLineRasterization);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RENDER_PASS_STRIPED_FEATURES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRenderPassStripedFeaturesARM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceRenderPassStripedFeaturesARM";
            generatedEmitBool(dst, section, "renderPassStriped", value.renderPassStriped);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RENDER_PASS_STRIPED_PROPERTIES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRenderPassStripedPropertiesARM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceRenderPassStripedPropertiesARM";
            generatedEmitAuto(dst, section, "maxRenderPassStripes", value.maxRenderPassStripes);
            generatedEmitAuto(dst, section, "renderPassStripeGranularity", value.renderPassStripeGranularity);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_REPRESENTATIVE_FRAGMENT_TEST_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRepresentativeFragmentTestFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceRepresentativeFragmentTestFeaturesNV";
            generatedEmitBool(dst, section, "representativeFragmentTest", value.representativeFragmentTest);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ROBUSTNESS_2_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRobustness2FeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceRobustness2FeaturesKHR";
            generatedEmitBool(dst, section, "nullDescriptor", value.nullDescriptor);
            generatedEmitBool(dst, section, "robustBufferAccess2", value.robustBufferAccess2);
            generatedEmitBool(dst, section, "robustImageAccess2", value.robustImageAccess2);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ROBUSTNESS_2_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRobustness2PropertiesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceRobustness2PropertiesKHR";
            generatedEmitAuto(dst, section, "robustStorageBufferAccessSizeAlignment", value.robustStorageBufferAccessSizeAlignment);
            generatedEmitAuto(dst, section, "robustUniformBufferAccessSizeAlignment", value.robustUniformBufferAccessSizeAlignment);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SCHEDULING_CONTROLS_FEATURES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceSchedulingControlsFeaturesARM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceSchedulingControlsFeaturesARM";
            generatedEmitBool(dst, section, "schedulingControls", value.schedulingControls);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SCHEDULING_CONTROLS_PROPERTIES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceSchedulingControlsPropertiesARM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceSchedulingControlsPropertiesARM";
            generatedEmitAuto(dst, section, "schedulingControlsFlags", value.schedulingControlsFlags);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_64_BIT_INDEXING_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShader64BitIndexingFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShader64BitIndexingFeaturesEXT";
            generatedEmitBool(dst, section, "shader64BitIndexing", value.shader64BitIndexing);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_ATOMIC_FLOAT16_VECTOR_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderAtomicFloat16VectorFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderAtomicFloat16VectorFeaturesNV";
            generatedEmitBool(dst, section, "shaderFloat16VectorAtomics", value.shaderFloat16VectorAtomics);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_ATOMIC_FLOAT_2_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderAtomicFloat2FeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderAtomicFloat2FeaturesEXT";
            generatedEmitBool(dst, section, "shaderBufferFloat16AtomicAdd", value.shaderBufferFloat16AtomicAdd);
            generatedEmitBool(dst, section, "shaderBufferFloat16AtomicMinMax", value.shaderBufferFloat16AtomicMinMax);
            generatedEmitBool(dst, section, "shaderBufferFloat16Atomics", value.shaderBufferFloat16Atomics);
            generatedEmitBool(dst, section, "shaderBufferFloat32AtomicMinMax", value.shaderBufferFloat32AtomicMinMax);
            generatedEmitBool(dst, section, "shaderBufferFloat64AtomicMinMax", value.shaderBufferFloat64AtomicMinMax);
            generatedEmitBool(dst, section, "shaderImageFloat32AtomicMinMax", value.shaderImageFloat32AtomicMinMax);
            generatedEmitBool(dst, section, "shaderSharedFloat16AtomicAdd", value.shaderSharedFloat16AtomicAdd);
            generatedEmitBool(dst, section, "shaderSharedFloat16AtomicMinMax", value.shaderSharedFloat16AtomicMinMax);
            generatedEmitBool(dst, section, "shaderSharedFloat16Atomics", value.shaderSharedFloat16Atomics);
            generatedEmitBool(dst, section, "shaderSharedFloat32AtomicMinMax", value.shaderSharedFloat32AtomicMinMax);
            generatedEmitBool(dst, section, "shaderSharedFloat64AtomicMinMax", value.shaderSharedFloat64AtomicMinMax);
            generatedEmitBool(dst, section, "sparseImageFloat32AtomicMinMax", value.sparseImageFloat32AtomicMinMax);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_ATOMIC_FLOAT_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderAtomicFloatFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderAtomicFloatFeaturesEXT";
            generatedEmitBool(dst, section, "shaderBufferFloat32AtomicAdd", value.shaderBufferFloat32AtomicAdd);
            generatedEmitBool(dst, section, "shaderBufferFloat32Atomics", value.shaderBufferFloat32Atomics);
            generatedEmitBool(dst, section, "shaderBufferFloat64AtomicAdd", value.shaderBufferFloat64AtomicAdd);
            generatedEmitBool(dst, section, "shaderBufferFloat64Atomics", value.shaderBufferFloat64Atomics);
            generatedEmitBool(dst, section, "shaderImageFloat32AtomicAdd", value.shaderImageFloat32AtomicAdd);
            generatedEmitBool(dst, section, "shaderImageFloat32Atomics", value.shaderImageFloat32Atomics);
            generatedEmitBool(dst, section, "shaderSharedFloat32AtomicAdd", value.shaderSharedFloat32AtomicAdd);
            generatedEmitBool(dst, section, "shaderSharedFloat32Atomics", value.shaderSharedFloat32Atomics);
            generatedEmitBool(dst, section, "shaderSharedFloat64AtomicAdd", value.shaderSharedFloat64AtomicAdd);
            generatedEmitBool(dst, section, "shaderSharedFloat64Atomics", value.shaderSharedFloat64Atomics);
            generatedEmitBool(dst, section, "sparseImageFloat32AtomicAdd", value.sparseImageFloat32AtomicAdd);
            generatedEmitBool(dst, section, "sparseImageFloat32Atomics", value.sparseImageFloat32Atomics);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_BFLOAT16_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderBfloat16FeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderBfloat16FeaturesKHR";
            generatedEmitBool(dst, section, "shaderBFloat16CooperativeMatrix", value.shaderBFloat16CooperativeMatrix);
            generatedEmitBool(dst, section, "shaderBFloat16DotProduct", value.shaderBFloat16DotProduct);
            generatedEmitBool(dst, section, "shaderBFloat16Type", value.shaderBFloat16Type);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_CLOCK_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderClockFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderClockFeaturesKHR";
            generatedEmitBool(dst, section, "shaderDeviceClock", value.shaderDeviceClock);
            generatedEmitBool(dst, section, "shaderSubgroupClock", value.shaderSubgroupClock);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_CORE_BUILTINS_FEATURES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderCoreBuiltinsFeaturesARM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderCoreBuiltinsFeaturesARM";
            generatedEmitBool(dst, section, "shaderCoreBuiltins", value.shaderCoreBuiltins);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_CORE_BUILTINS_PROPERTIES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderCoreBuiltinsPropertiesARM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderCoreBuiltinsPropertiesARM";
            generatedEmitAuto(dst, section, "shaderCoreCount", value.shaderCoreCount);
            generatedEmitAuto(dst, section, "shaderCoreMask", value.shaderCoreMask);
            generatedEmitAuto(dst, section, "shaderWarpsPerCore", value.shaderWarpsPerCore);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_CORE_PROPERTIES_2_AMD: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderCoreProperties2AMD*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderCoreProperties2AMD";
            generatedEmitAuto(dst, section, "activeComputeUnitCount", value.activeComputeUnitCount);
            generatedEmitAuto(dst, section, "shaderCoreFeatures", value.shaderCoreFeatures);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_CORE_PROPERTIES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderCorePropertiesARM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderCorePropertiesARM";
            generatedEmitAuto(dst, section, "fmaRate", value.fmaRate);
            generatedEmitAuto(dst, section, "pixelRate", value.pixelRate);
            generatedEmitAuto(dst, section, "texelRate", value.texelRate);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_EARLY_AND_LATE_FRAGMENT_TESTS_FEATURES_AMD: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderEarlyAndLateFragmentTestsFeaturesAMD*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderEarlyAndLateFragmentTestsFeaturesAMD";
            generatedEmitBool(dst, section, "shaderEarlyAndLateFragmentTests", value.shaderEarlyAndLateFragmentTests);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_ENQUEUE_PROPERTIES_AMDX: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderEnqueuePropertiesAMDX*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderEnqueuePropertiesAMDX";
            generatedEmitAuto(dst, section, "executionGraphDispatchAddressAlignment", value.executionGraphDispatchAddressAlignment);
            generatedEmitAuto(dst, section, "maxExecutionGraphDepth", value.maxExecutionGraphDepth);
            generatedEmitAuto(dst, section, "maxExecutionGraphShaderOutputNodes", value.maxExecutionGraphShaderOutputNodes);
            generatedEmitAuto(dst, section, "maxExecutionGraphShaderPayloadCount", value.maxExecutionGraphShaderPayloadCount);
            generatedEmitAuto(dst, section, "maxExecutionGraphShaderPayloadSize", value.maxExecutionGraphShaderPayloadSize);
            generatedEmitAuto(dst, section, "maxExecutionGraphWorkgroupCount", value.maxExecutionGraphWorkgroupCount);
            generatedEmitAuto(dst, section, "maxExecutionGraphWorkgroups", value.maxExecutionGraphWorkgroups);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_EXPECT_ASSUME_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderExpectAssumeFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderExpectAssumeFeaturesKHR";
            generatedEmitBool(dst, section, "shaderExpectAssume", value.shaderExpectAssume);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_FLOAT8_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderFloat8FeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderFloat8FeaturesEXT";
            generatedEmitBool(dst, section, "shaderFloat8", value.shaderFloat8);
            generatedEmitBool(dst, section, "shaderFloat8CooperativeMatrix", value.shaderFloat8CooperativeMatrix);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_FLOAT_CONTROLS_2_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderFloatControls2FeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderFloatControls2FeaturesKHR";
            generatedEmitBool(dst, section, "shaderFloatControls2", value.shaderFloatControls2);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_FMA_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderFmaFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderFmaFeaturesKHR";
            generatedEmitBool(dst, section, "shaderFmaFloat16", value.shaderFmaFloat16);
            generatedEmitBool(dst, section, "shaderFmaFloat32", value.shaderFmaFloat32);
            generatedEmitBool(dst, section, "shaderFmaFloat64", value.shaderFmaFloat64);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_IMAGE_ATOMIC_INT64_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderImageAtomicInt64FeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderImageAtomicInt64FeaturesEXT";
            generatedEmitBool(dst, section, "shaderImageInt64Atomics", value.shaderImageInt64Atomics);
            generatedEmitBool(dst, section, "sparseImageInt64Atomics", value.sparseImageInt64Atomics);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_IMAGE_FOOTPRINT_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderImageFootprintFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderImageFootprintFeaturesNV";
            generatedEmitBool(dst, section, "imageFootprint", value.imageFootprint);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_INTEGER_FUNCTIONS_2_FEATURES_INTEL: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderIntegerFunctions2FeaturesINTEL*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderIntegerFunctions2FeaturesINTEL";
            generatedEmitBool(dst, section, "shaderIntegerFunctions2", value.shaderIntegerFunctions2);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_MODULE_IDENTIFIER_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderModuleIdentifierFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderModuleIdentifierFeaturesEXT";
            generatedEmitBool(dst, section, "shaderModuleIdentifier", value.shaderModuleIdentifier);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_MODULE_IDENTIFIER_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderModuleIdentifierPropertiesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderModuleIdentifierPropertiesEXT";
            generatedEmitAuto(dst, section, "shaderModuleIdentifierAlgorithmUUID", value.shaderModuleIdentifierAlgorithmUUID);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_OBJECT_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderObjectFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderObjectFeaturesEXT";
            generatedEmitBool(dst, section, "shaderObject", value.shaderObject);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_OBJECT_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderObjectPropertiesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderObjectPropertiesEXT";
            generatedEmitAuto(dst, section, "shaderBinaryUUID", value.shaderBinaryUUID);
            generatedEmitAuto(dst, section, "shaderBinaryVersion", value.shaderBinaryVersion);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_QUAD_CONTROL_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderQuadControlFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderQuadControlFeaturesKHR";
            generatedEmitBool(dst, section, "shaderQuadControl", value.shaderQuadControl);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_RELAXED_EXTENDED_INSTRUCTION_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderRelaxedExtendedInstructionFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderRelaxedExtendedInstructionFeaturesKHR";
            generatedEmitBool(dst, section, "shaderRelaxedExtendedInstruction", value.shaderRelaxedExtendedInstruction);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_REPLICATED_COMPOSITES_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderReplicatedCompositesFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderReplicatedCompositesFeaturesEXT";
            generatedEmitBool(dst, section, "shaderReplicatedComposites", value.shaderReplicatedComposites);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SM_BUILTINS_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderSMBuiltinsFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderSMBuiltinsFeaturesNV";
            generatedEmitBool(dst, section, "shaderSMBuiltins", value.shaderSMBuiltins);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SM_BUILTINS_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderSMBuiltinsPropertiesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderSMBuiltinsPropertiesNV";
            generatedEmitAuto(dst, section, "shaderSMCount", value.shaderSMCount);
            generatedEmitAuto(dst, section, "shaderWarpsPerSM", value.shaderWarpsPerSM);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SUBGROUP_ROTATE_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderSubgroupRotateFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderSubgroupRotateFeaturesKHR";
            generatedEmitBool(dst, section, "shaderSubgroupRotate", value.shaderSubgroupRotate);
            generatedEmitBool(dst, section, "shaderSubgroupRotateClustered", value.shaderSubgroupRotateClustered);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SUBGROUP_UNIFORM_CONTROL_FLOW_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderSubgroupUniformControlFlowFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderSubgroupUniformControlFlowFeaturesKHR";
            generatedEmitBool(dst, section, "shaderSubgroupUniformControlFlow", value.shaderSubgroupUniformControlFlow);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_TILE_IMAGE_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderTileImagePropertiesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderTileImagePropertiesEXT";
            generatedEmitBool(dst, section, "shaderTileImageCoherentReadAccelerated", value.shaderTileImageCoherentReadAccelerated);
            generatedEmitBool(dst, section, "shaderTileImageReadFromHelperInvocation", value.shaderTileImageReadFromHelperInvocation);
            generatedEmitBool(dst, section, "shaderTileImageReadSampleFromPixelRateInvocation", value.shaderTileImageReadSampleFromPixelRateInvocation);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_UNTYPED_POINTERS_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderUntypedPointersFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderUntypedPointersFeaturesKHR";
            generatedEmitBool(dst, section, "shaderUntypedPointers", value.shaderUntypedPointers);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADING_RATE_IMAGE_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShadingRateImageFeaturesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShadingRateImageFeaturesNV";
            generatedEmitBool(dst, section, "shadingRateCoarseSampleOrder", value.shadingRateCoarseSampleOrder);
            generatedEmitBool(dst, section, "shadingRateImage", value.shadingRateImage);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADING_RATE_IMAGE_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShadingRateImagePropertiesNV*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShadingRateImagePropertiesNV";
            generatedEmitAuto(dst, section, "shadingRateMaxCoarseSamples", value.shadingRateMaxCoarseSamples);
            generatedEmitAuto(dst, section, "shadingRatePaletteSize", value.shadingRatePaletteSize);
            generatedEmitAuto(dst, section, "shadingRateTexelSize", value.shadingRateTexelSize);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SUBPASS_MERGE_FEEDBACK_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceSubpassMergeFeedbackFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceSubpassMergeFeedbackFeaturesEXT";
            generatedEmitBool(dst, section, "subpassMergeFeedback", value.subpassMergeFeedback);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SUBPASS_SHADING_FEATURES_HUAWEI: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceSubpassShadingFeaturesHUAWEI*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceSubpassShadingFeaturesHUAWEI";
            generatedEmitBool(dst, section, "subpassShading", value.subpassShading);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SUBPASS_SHADING_PROPERTIES_HUAWEI: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceSubpassShadingPropertiesHUAWEI*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceSubpassShadingPropertiesHUAWEI";
            generatedEmitAuto(dst, section, "maxSubpassShadingWorkgroupSizeAspectRatio", value.maxSubpassShadingWorkgroupSizeAspectRatio);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SWAPCHAIN_MAINTENANCE_1_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceSwapchainMaintenance1FeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceSwapchainMaintenance1FeaturesKHR";
            generatedEmitBool(dst, section, "swapchainMaintenance1", value.swapchainMaintenance1);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TENSOR_FEATURES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceTensorFeaturesARM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceTensorFeaturesARM";
            generatedEmitBool(dst, section, "descriptorBindingStorageTensorUpdateAfterBind", value.descriptorBindingStorageTensorUpdateAfterBind);
            generatedEmitBool(dst, section, "shaderStorageTensorArrayDynamicIndexing", value.shaderStorageTensorArrayDynamicIndexing);
            generatedEmitBool(dst, section, "shaderStorageTensorArrayNonUniformIndexing", value.shaderStorageTensorArrayNonUniformIndexing);
            generatedEmitBool(dst, section, "shaderTensorAccess", value.shaderTensorAccess);
            generatedEmitBool(dst, section, "tensorNonPacked", value.tensorNonPacked);
            generatedEmitBool(dst, section, "tensors", value.tensors);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TENSOR_PROPERTIES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceTensorPropertiesARM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceTensorPropertiesARM";
            generatedEmitAuto(dst, section, "maxDescriptorSetStorageTensors", value.maxDescriptorSetStorageTensors);
            generatedEmitAuto(dst, section, "maxDescriptorSetUpdateAfterBindStorageTensors", value.maxDescriptorSetUpdateAfterBindStorageTensors);
            generatedEmitAuto(dst, section, "maxPerDimensionTensorElements", value.maxPerDimensionTensorElements);
            generatedEmitAuto(dst, section, "maxPerStageDescriptorSetStorageTensors", value.maxPerStageDescriptorSetStorageTensors);
            generatedEmitAuto(dst, section, "maxPerStageDescriptorUpdateAfterBindStorageTensors", value.maxPerStageDescriptorUpdateAfterBindStorageTensors);
            generatedEmitAuto(dst, section, "maxTensorDimensionCount", value.maxTensorDimensionCount);
            generatedEmitAuto(dst, section, "maxTensorElements", value.maxTensorElements);
            generatedEmitAuto(dst, section, "maxTensorShaderAccessArrayLength", value.maxTensorShaderAccessArrayLength);
            generatedEmitAuto(dst, section, "maxTensorShaderAccessSize", value.maxTensorShaderAccessSize);
            generatedEmitAuto(dst, section, "maxTensorSize", value.maxTensorSize);
            generatedEmitAuto(dst, section, "maxTensorStride", value.maxTensorStride);
            generatedEmitBool(dst, section, "shaderStorageTensorArrayNonUniformIndexingNative", value.shaderStorageTensorArrayNonUniformIndexingNative);
            generatedEmitAuto(dst, section, "shaderTensorSupportedStages", value.shaderTensorSupportedStages);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TEXEL_BUFFER_ALIGNMENT_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceTexelBufferAlignmentFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceTexelBufferAlignmentFeaturesEXT";
            generatedEmitBool(dst, section, "texelBufferAlignment", value.texelBufferAlignment);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TILE_MEMORY_HEAP_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceTileMemoryHeapFeaturesQCOM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceTileMemoryHeapFeaturesQCOM";
            generatedEmitBool(dst, section, "tileMemoryHeap", value.tileMemoryHeap);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TILE_MEMORY_HEAP_PROPERTIES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceTileMemoryHeapPropertiesQCOM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceTileMemoryHeapPropertiesQCOM";
            generatedEmitBool(dst, section, "queueSubmitBoundary", value.queueSubmitBoundary);
            generatedEmitBool(dst, section, "tileBufferTransfers", value.tileBufferTransfers);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TILE_PROPERTIES_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceTilePropertiesFeaturesQCOM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceTilePropertiesFeaturesQCOM";
            generatedEmitBool(dst, section, "tileProperties", value.tileProperties);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TILE_SHADING_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceTileShadingFeaturesQCOM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceTileShadingFeaturesQCOM";
            generatedEmitBool(dst, section, "tileShading", value.tileShading);
            generatedEmitBool(dst, section, "tileShadingAnisotropicApron", value.tileShadingAnisotropicApron);
            generatedEmitBool(dst, section, "tileShadingApron", value.tileShadingApron);
            generatedEmitBool(dst, section, "tileShadingAtomicOps", value.tileShadingAtomicOps);
            generatedEmitBool(dst, section, "tileShadingColorAttachments", value.tileShadingColorAttachments);
            generatedEmitBool(dst, section, "tileShadingDepthAttachments", value.tileShadingDepthAttachments);
            generatedEmitBool(dst, section, "tileShadingDispatchTile", value.tileShadingDispatchTile);
            generatedEmitBool(dst, section, "tileShadingFragmentStage", value.tileShadingFragmentStage);
            generatedEmitBool(dst, section, "tileShadingImageProcessing", value.tileShadingImageProcessing);
            generatedEmitBool(dst, section, "tileShadingInputAttachments", value.tileShadingInputAttachments);
            generatedEmitBool(dst, section, "tileShadingPerTileDispatch", value.tileShadingPerTileDispatch);
            generatedEmitBool(dst, section, "tileShadingPerTileDraw", value.tileShadingPerTileDraw);
            generatedEmitBool(dst, section, "tileShadingSampledAttachments", value.tileShadingSampledAttachments);
            generatedEmitBool(dst, section, "tileShadingStencilAttachments", value.tileShadingStencilAttachments);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TILE_SHADING_PROPERTIES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceTileShadingPropertiesQCOM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceTileShadingPropertiesQCOM";
            generatedEmitAuto(dst, section, "maxApronSize", value.maxApronSize);
            generatedEmitAuto(dst, section, "maxTileShadingRate", value.maxTileShadingRate);
            generatedEmitBool(dst, section, "preferNonCoherent", value.preferNonCoherent);
            generatedEmitAuto(dst, section, "tileGranularity", value.tileGranularity);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TRANSFORM_FEEDBACK_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceTransformFeedbackFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceTransformFeedbackFeaturesEXT";
            generatedEmitBool(dst, section, "geometryStreams", value.geometryStreams);
            generatedEmitBool(dst, section, "transformFeedback", value.transformFeedback);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_UNIFIED_IMAGE_LAYOUTS_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceUnifiedImageLayoutsFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceUnifiedImageLayoutsFeaturesKHR";
            generatedEmitBool(dst, section, "unifiedImageLayouts", value.unifiedImageLayouts);
            generatedEmitBool(dst, section, "unifiedImageLayoutsVideo", value.unifiedImageLayoutsVideo);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VERTEX_ATTRIBUTE_DIVISOR_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVertexAttributeDivisorFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceVertexAttributeDivisorFeaturesKHR";
            generatedEmitBool(dst, section, "vertexAttributeInstanceRateDivisor", value.vertexAttributeInstanceRateDivisor);
            generatedEmitBool(dst, section, "vertexAttributeInstanceRateZeroDivisor", value.vertexAttributeInstanceRateZeroDivisor);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VERTEX_ATTRIBUTE_DIVISOR_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVertexAttributeDivisorPropertiesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceVertexAttributeDivisorPropertiesEXT";
            generatedEmitAuto(dst, section, "maxVertexAttribDivisor", value.maxVertexAttribDivisor);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VERTEX_ATTRIBUTE_DIVISOR_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVertexAttributeDivisorPropertiesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceVertexAttributeDivisorPropertiesKHR";
            generatedEmitAuto(dst, section, "maxVertexAttribDivisor", value.maxVertexAttribDivisor);
            generatedEmitBool(dst, section, "supportsNonZeroFirstInstance", value.supportsNonZeroFirstInstance);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VERTEX_ATTRIBUTE_ROBUSTNESS_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVertexAttributeRobustnessFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceVertexAttributeRobustnessFeaturesEXT";
            generatedEmitBool(dst, section, "vertexAttributeRobustness", value.vertexAttributeRobustness);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VERTEX_INPUT_DYNAMIC_STATE_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVertexInputDynamicStateFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceVertexInputDynamicStateFeaturesEXT";
            generatedEmitBool(dst, section, "vertexInputDynamicState", value.vertexInputDynamicState);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_DECODE_VP9_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVideoDecodeVP9FeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceVideoDecodeVP9FeaturesKHR";
            generatedEmitBool(dst, section, "videoDecodeVP9", value.videoDecodeVP9);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_ENCODE_AV1_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVideoEncodeAV1FeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceVideoEncodeAV1FeaturesKHR";
            generatedEmitBool(dst, section, "videoEncodeAV1", value.videoEncodeAV1);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_ENCODE_INTRA_REFRESH_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVideoEncodeIntraRefreshFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceVideoEncodeIntraRefreshFeaturesKHR";
            generatedEmitBool(dst, section, "videoEncodeIntraRefresh", value.videoEncodeIntraRefresh);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_ENCODE_QUANTIZATION_MAP_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVideoEncodeQuantizationMapFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceVideoEncodeQuantizationMapFeaturesKHR";
            generatedEmitBool(dst, section, "videoEncodeQuantizationMap", value.videoEncodeQuantizationMap);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_ENCODE_RGB_CONVERSION_FEATURES_VALVE: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVideoEncodeRgbConversionFeaturesVALVE*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceVideoEncodeRgbConversionFeaturesVALVE";
            generatedEmitBool(dst, section, "videoEncodeRgbConversion", value.videoEncodeRgbConversion);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_MAINTENANCE_1_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVideoMaintenance1FeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceVideoMaintenance1FeaturesKHR";
            generatedEmitBool(dst, section, "videoMaintenance1", value.videoMaintenance1);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_MAINTENANCE_2_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVideoMaintenance2FeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceVideoMaintenance2FeaturesKHR";
            generatedEmitBool(dst, section, "videoMaintenance2", value.videoMaintenance2);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_WORKGROUP_MEMORY_EXPLICIT_LAYOUT_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceWorkgroupMemoryExplicitLayoutFeaturesKHR*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceWorkgroupMemoryExplicitLayoutFeaturesKHR";
            generatedEmitBool(dst, section, "workgroupMemoryExplicitLayout", value.workgroupMemoryExplicitLayout);
            generatedEmitBool(dst, section, "workgroupMemoryExplicitLayout16BitAccess", value.workgroupMemoryExplicitLayout16BitAccess);
            generatedEmitBool(dst, section, "workgroupMemoryExplicitLayout8BitAccess", value.workgroupMemoryExplicitLayout8BitAccess);
            generatedEmitBool(dst, section, "workgroupMemoryExplicitLayoutScalarBlockLayout", value.workgroupMemoryExplicitLayoutScalarBlockLayout);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_YCBCR_DEGAMMA_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceYcbcrDegammaFeaturesQCOM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceYcbcrDegammaFeaturesQCOM";
            generatedEmitBool(dst, section, "ycbcrDegamma", value.ycbcrDegamma);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_YCBCR_IMAGE_ARRAYS_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceYcbcrImageArraysFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceYcbcrImageArraysFeaturesEXT";
            generatedEmitBool(dst, section, "ycbcrImageArrays", value.ycbcrImageArrays);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ZERO_INITIALIZE_DEVICE_MEMORY_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceZeroInitializeDeviceMemoryFeaturesEXT*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceZeroInitializeDeviceMemoryFeaturesEXT";
            generatedEmitBool(dst, section, "zeroInitializeDeviceMemory", value.zeroInitializeDeviceMemory);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SAMPLER_FILTER_MINMAX_PROPERTIES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceSamplerFilterMinmaxProperties*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceSamplerFilterMinmaxProperties";
            generatedEmitBool(dst, section, "filterMinmaxSingleComponentFormats", value.filterMinmaxSingleComponentFormats);
            generatedEmitBool(dst, section, "filterMinmaxImageComponentMapping", value.filterMinmaxImageComponentMapping);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INLINE_UNIFORM_BLOCK_PROPERTIES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceInlineUniformBlockProperties*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceInlineUniformBlockProperties";
            generatedEmitAuto(dst, section, "maxInlineUniformBlockSize", value.maxInlineUniformBlockSize);
            generatedEmitAuto(dst, section, "maxPerStageDescriptorInlineUniformBlocks", value.maxPerStageDescriptorInlineUniformBlocks);
            generatedEmitAuto(dst, section, "maxPerStageDescriptorUpdateAfterBindInlineUniformBlocks", value.maxPerStageDescriptorUpdateAfterBindInlineUniformBlocks);
            generatedEmitAuto(dst, section, "maxDescriptorSetInlineUniformBlocks", value.maxDescriptorSetInlineUniformBlocks);
            generatedEmitAuto(dst, section, "maxDescriptorSetUpdateAfterBindInlineUniformBlocks", value.maxDescriptorSetUpdateAfterBindInlineUniformBlocks);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_INDEXING_PROPERTIES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDescriptorIndexingProperties*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDescriptorIndexingProperties";
            generatedEmitAuto(dst, section, "maxUpdateAfterBindDescriptorsInAllPools", value.maxUpdateAfterBindDescriptorsInAllPools);
            generatedEmitBool(dst, section, "shaderUniformBufferArrayNonUniformIndexingNative", value.shaderUniformBufferArrayNonUniformIndexingNative);
            generatedEmitBool(dst, section, "shaderSampledImageArrayNonUniformIndexingNative", value.shaderSampledImageArrayNonUniformIndexingNative);
            generatedEmitBool(dst, section, "shaderStorageBufferArrayNonUniformIndexingNative", value.shaderStorageBufferArrayNonUniformIndexingNative);
            generatedEmitBool(dst, section, "shaderStorageImageArrayNonUniformIndexingNative", value.shaderStorageImageArrayNonUniformIndexingNative);
            generatedEmitBool(dst, section, "shaderInputAttachmentArrayNonUniformIndexingNative", value.shaderInputAttachmentArrayNonUniformIndexingNative);
            generatedEmitBool(dst, section, "robustBufferAccessUpdateAfterBind", value.robustBufferAccessUpdateAfterBind);
            generatedEmitBool(dst, section, "quadDivergentImplicitLod", value.quadDivergentImplicitLod);
            generatedEmitAuto(dst, section, "maxPerStageDescriptorUpdateAfterBindSamplers", value.maxPerStageDescriptorUpdateAfterBindSamplers);
            generatedEmitAuto(dst, section, "maxPerStageDescriptorUpdateAfterBindUniformBuffers", value.maxPerStageDescriptorUpdateAfterBindUniformBuffers);
            generatedEmitAuto(dst, section, "maxPerStageDescriptorUpdateAfterBindStorageBuffers", value.maxPerStageDescriptorUpdateAfterBindStorageBuffers);
            generatedEmitAuto(dst, section, "maxPerStageDescriptorUpdateAfterBindSampledImages", value.maxPerStageDescriptorUpdateAfterBindSampledImages);
            generatedEmitAuto(dst, section, "maxPerStageDescriptorUpdateAfterBindStorageImages", value.maxPerStageDescriptorUpdateAfterBindStorageImages);
            generatedEmitAuto(dst, section, "maxPerStageDescriptorUpdateAfterBindInputAttachments", value.maxPerStageDescriptorUpdateAfterBindInputAttachments);
            generatedEmitAuto(dst, section, "maxPerStageUpdateAfterBindResources", value.maxPerStageUpdateAfterBindResources);
            generatedEmitAuto(dst, section, "maxDescriptorSetUpdateAfterBindSamplers", value.maxDescriptorSetUpdateAfterBindSamplers);
            generatedEmitAuto(dst, section, "maxDescriptorSetUpdateAfterBindUniformBuffers", value.maxDescriptorSetUpdateAfterBindUniformBuffers);
            generatedEmitAuto(dst, section, "maxDescriptorSetUpdateAfterBindUniformBuffersDynamic", value.maxDescriptorSetUpdateAfterBindUniformBuffersDynamic);
            generatedEmitAuto(dst, section, "maxDescriptorSetUpdateAfterBindStorageBuffers", value.maxDescriptorSetUpdateAfterBindStorageBuffers);
            generatedEmitAuto(dst, section, "maxDescriptorSetUpdateAfterBindStorageBuffersDynamic", value.maxDescriptorSetUpdateAfterBindStorageBuffersDynamic);
            generatedEmitAuto(dst, section, "maxDescriptorSetUpdateAfterBindSampledImages", value.maxDescriptorSetUpdateAfterBindSampledImages);
            generatedEmitAuto(dst, section, "maxDescriptorSetUpdateAfterBindStorageImages", value.maxDescriptorSetUpdateAfterBindStorageImages);
            generatedEmitAuto(dst, section, "maxDescriptorSetUpdateAfterBindInputAttachments", value.maxDescriptorSetUpdateAfterBindInputAttachments);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTIVIEW_PROPERTIES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMultiviewProperties*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceMultiviewProperties";
            generatedEmitAuto(dst, section, "maxMultiviewViewCount", value.maxMultiviewViewCount);
            generatedEmitAuto(dst, section, "maxMultiviewInstanceIndex", value.maxMultiviewInstanceIndex);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_POINT_CLIPPING_PROPERTIES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePointClippingProperties*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevicePointClippingProperties";
            generatedEmitAuto(dst, section, "pointClippingBehavior", value.pointClippingBehavior);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FLOAT_CONTROLS_PROPERTIES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFloatControlsProperties*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceFloatControlsProperties";
            generatedEmitAuto(dst, section, "denormBehaviorIndependence", value.denormBehaviorIndependence);
            generatedEmitAuto(dst, section, "roundingModeIndependence", value.roundingModeIndependence);
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
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_STENCIL_RESOLVE_PROPERTIES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDepthStencilResolveProperties*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDepthStencilResolveProperties";
            generatedEmitAuto(dst, section, "supportedDepthResolveModes", value.supportedDepthResolveModes);
            generatedEmitAuto(dst, section, "supportedStencilResolveModes", value.supportedStencilResolveModes);
            generatedEmitBool(dst, section, "independentResolveNone", value.independentResolveNone);
            generatedEmitBool(dst, section, "independentResolve", value.independentResolve);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_INTEGER_DOT_PRODUCT_PROPERTIES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderIntegerDotProductProperties*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderIntegerDotProductProperties";
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
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_4_PROPERTIES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMaintenance4Properties*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceMaintenance4Properties";
            generatedEmitAuto(dst, section, "maxBufferSize", value.maxBufferSize);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INLINE_UNIFORM_BLOCK_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceInlineUniformBlockFeatures*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceInlineUniformBlockFeatures";
            generatedEmitBool(dst, section, "inlineUniformBlock", value.inlineUniformBlock);
            generatedEmitBool(dst, section, "descriptorBindingInlineUniformBlockUpdateAfterBind", value.descriptorBindingInlineUniformBlockUpdateAfterBind);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_INDEXING_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDescriptorIndexingFeatures*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDescriptorIndexingFeatures";
            generatedEmitBool(dst, section, "shaderInputAttachmentArrayDynamicIndexing", value.shaderInputAttachmentArrayDynamicIndexing);
            generatedEmitBool(dst, section, "shaderUniformTexelBufferArrayDynamicIndexing", value.shaderUniformTexelBufferArrayDynamicIndexing);
            generatedEmitBool(dst, section, "shaderStorageTexelBufferArrayDynamicIndexing", value.shaderStorageTexelBufferArrayDynamicIndexing);
            generatedEmitBool(dst, section, "shaderUniformBufferArrayNonUniformIndexing", value.shaderUniformBufferArrayNonUniformIndexing);
            generatedEmitBool(dst, section, "shaderSampledImageArrayNonUniformIndexing", value.shaderSampledImageArrayNonUniformIndexing);
            generatedEmitBool(dst, section, "shaderStorageBufferArrayNonUniformIndexing", value.shaderStorageBufferArrayNonUniformIndexing);
            generatedEmitBool(dst, section, "shaderStorageImageArrayNonUniformIndexing", value.shaderStorageImageArrayNonUniformIndexing);
            generatedEmitBool(dst, section, "shaderInputAttachmentArrayNonUniformIndexing", value.shaderInputAttachmentArrayNonUniformIndexing);
            generatedEmitBool(dst, section, "shaderUniformTexelBufferArrayNonUniformIndexing", value.shaderUniformTexelBufferArrayNonUniformIndexing);
            generatedEmitBool(dst, section, "shaderStorageTexelBufferArrayNonUniformIndexing", value.shaderStorageTexelBufferArrayNonUniformIndexing);
            generatedEmitBool(dst, section, "descriptorBindingUniformBufferUpdateAfterBind", value.descriptorBindingUniformBufferUpdateAfterBind);
            generatedEmitBool(dst, section, "descriptorBindingSampledImageUpdateAfterBind", value.descriptorBindingSampledImageUpdateAfterBind);
            generatedEmitBool(dst, section, "descriptorBindingStorageImageUpdateAfterBind", value.descriptorBindingStorageImageUpdateAfterBind);
            generatedEmitBool(dst, section, "descriptorBindingStorageBufferUpdateAfterBind", value.descriptorBindingStorageBufferUpdateAfterBind);
            generatedEmitBool(dst, section, "descriptorBindingUniformTexelBufferUpdateAfterBind", value.descriptorBindingUniformTexelBufferUpdateAfterBind);
            generatedEmitBool(dst, section, "descriptorBindingStorageTexelBufferUpdateAfterBind", value.descriptorBindingStorageTexelBufferUpdateAfterBind);
            generatedEmitBool(dst, section, "descriptorBindingUpdateUnusedWhilePending", value.descriptorBindingUpdateUnusedWhilePending);
            generatedEmitBool(dst, section, "descriptorBindingPartiallyBound", value.descriptorBindingPartiallyBound);
            generatedEmitBool(dst, section, "descriptorBindingVariableDescriptorCount", value.descriptorBindingVariableDescriptorCount);
            generatedEmitBool(dst, section, "runtimeDescriptorArray", value.runtimeDescriptorArray);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SUBGROUP_SIZE_CONTROL_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceSubgroupSizeControlFeatures*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceSubgroupSizeControlFeatures";
            generatedEmitBool(dst, section, "subgroupSizeControl", value.subgroupSizeControl);
            generatedEmitBool(dst, section, "computeFullSubgroups", value.computeFullSubgroups);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_HOST_QUERY_RESET_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceHostQueryResetFeatures*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceHostQueryResetFeatures";
            generatedEmitBool(dst, section, "hostQueryReset", value.hostQueryReset);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_DEMOTE_TO_HELPER_INVOCATION_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderDemoteToHelperInvocationFeatures*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderDemoteToHelperInvocationFeatures";
            generatedEmitBool(dst, section, "shaderDemoteToHelperInvocation", value.shaderDemoteToHelperInvocation);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRIVATE_DATA_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePrivateDataFeatures*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevicePrivateDataFeatures";
            generatedEmitBool(dst, section, "privateData", value.privateData);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_CREATION_CACHE_CONTROL_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePipelineCreationCacheControlFeatures*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevicePipelineCreationCacheControlFeatures";
            generatedEmitBool(dst, section, "pipelineCreationCacheControl", value.pipelineCreationCacheControl);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DYNAMIC_RENDERING_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDynamicRenderingFeatures*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceDynamicRenderingFeatures";
            generatedEmitBool(dst, section, "dynamicRendering", value.dynamicRendering);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTIVIEW_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMultiviewFeatures*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceMultiviewFeatures";
            generatedEmitBool(dst, section, "multiview", value.multiview);
            generatedEmitBool(dst, section, "multiviewGeometryShader", value.multiviewGeometryShader);
            generatedEmitBool(dst, section, "multiviewTessellationShader", value.multiviewTessellationShader);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_FLOAT16_INT8_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderFloat16Int8Features*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderFloat16Int8Features";
            generatedEmitBool(dst, section, "shaderFloat16", value.shaderFloat16);
            generatedEmitBool(dst, section, "shaderInt8", value.shaderInt8);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_16BIT_STORAGE_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevice16BitStorageFeatures*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevice16BitStorageFeatures";
            generatedEmitBool(dst, section, "storageBuffer16BitAccess", value.storageBuffer16BitAccess);
            generatedEmitBool(dst, section, "uniformAndStorageBuffer16BitAccess", value.uniformAndStorageBuffer16BitAccess);
            generatedEmitBool(dst, section, "storagePushConstant16", value.storagePushConstant16);
            generatedEmitBool(dst, section, "storageInputOutput16", value.storageInputOutput16);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGELESS_FRAMEBUFFER_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceImagelessFramebufferFeatures*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceImagelessFramebufferFeatures";
            generatedEmitBool(dst, section, "imagelessFramebuffer", value.imagelessFramebuffer);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VARIABLE_POINTERS_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVariablePointersFeatures*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceVariablePointersFeatures";
            generatedEmitBool(dst, section, "variablePointersStorageBuffer", value.variablePointersStorageBuffer);
            generatedEmitBool(dst, section, "variablePointers", value.variablePointers);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SAMPLER_YCBCR_CONVERSION_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceSamplerYcbcrConversionFeatures*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceSamplerYcbcrConversionFeatures";
            generatedEmitBool(dst, section, "samplerYcbcrConversion", value.samplerYcbcrConversion);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SUBGROUP_EXTENDED_TYPES_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderSubgroupExtendedTypesFeatures*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderSubgroupExtendedTypesFeatures";
            generatedEmitBool(dst, section, "shaderSubgroupExtendedTypes", value.shaderSubgroupExtendedTypes);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_8BIT_STORAGE_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevice8BitStorageFeatures*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDevice8BitStorageFeatures";
            generatedEmitBool(dst, section, "storageBuffer8BitAccess", value.storageBuffer8BitAccess);
            generatedEmitBool(dst, section, "uniformAndStorageBuffer8BitAccess", value.uniformAndStorageBuffer8BitAccess);
            generatedEmitBool(dst, section, "storagePushConstant8", value.storagePushConstant8);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_ATOMIC_INT64_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderAtomicInt64Features*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderAtomicInt64Features";
            generatedEmitBool(dst, section, "shaderBufferInt64Atomics", value.shaderBufferInt64Atomics);
            generatedEmitBool(dst, section, "shaderSharedInt64Atomics", value.shaderSharedInt64Atomics);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TIMELINE_SEMAPHORE_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceTimelineSemaphoreFeatures*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceTimelineSemaphoreFeatures";
            generatedEmitBool(dst, section, "timelineSemaphore", value.timelineSemaphore);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_MEMORY_MODEL_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVulkanMemoryModelFeatures*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceVulkanMemoryModelFeatures";
            generatedEmitBool(dst, section, "vulkanMemoryModel", value.vulkanMemoryModel);
            generatedEmitBool(dst, section, "vulkanMemoryModelDeviceScope", value.vulkanMemoryModelDeviceScope);
            generatedEmitBool(dst, section, "vulkanMemoryModelAvailabilityVisibilityChains", value.vulkanMemoryModelAvailabilityVisibilityChains);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_TERMINATE_INVOCATION_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderTerminateInvocationFeatures*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderTerminateInvocationFeatures";
            generatedEmitBool(dst, section, "shaderTerminateInvocation", value.shaderTerminateInvocation);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SEPARATE_DEPTH_STENCIL_LAYOUTS_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceSeparateDepthStencilLayoutsFeatures*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceSeparateDepthStencilLayoutsFeatures";
            generatedEmitBool(dst, section, "separateDepthStencilLayouts", value.separateDepthStencilLayouts);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_UNIFORM_BUFFER_STANDARD_LAYOUT_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceUniformBufferStandardLayoutFeatures*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceUniformBufferStandardLayoutFeatures";
            generatedEmitBool(dst, section, "uniformBufferStandardLayout", value.uniformBufferStandardLayout);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_BUFFER_DEVICE_ADDRESS_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceBufferDeviceAddressFeatures*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceBufferDeviceAddressFeatures";
            generatedEmitBool(dst, section, "bufferDeviceAddress", value.bufferDeviceAddress);
            generatedEmitBool(dst, section, "bufferDeviceAddressCaptureReplay", value.bufferDeviceAddressCaptureReplay);
            generatedEmitBool(dst, section, "bufferDeviceAddressMultiDevice", value.bufferDeviceAddressMultiDevice);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_INTEGER_DOT_PRODUCT_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderIntegerDotProductFeatures*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceShaderIntegerDotProductFeatures";
            generatedEmitBool(dst, section, "shaderIntegerDotProduct", value.shaderIntegerDotProduct);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SYNCHRONIZATION_2_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceSynchronization2Features*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceSynchronization2Features";
            generatedEmitBool(dst, section, "synchronization2", value.synchronization2);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ZERO_INITIALIZE_WORKGROUP_MEMORY_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceZeroInitializeWorkgroupMemoryFeatures*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceZeroInitializeWorkgroupMemoryFeatures";
            generatedEmitBool(dst, section, "shaderZeroInitializeWorkgroupMemory", value.shaderZeroInitializeWorkgroupMemory);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_4_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMaintenance4Features*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceMaintenance4Features";
            generatedEmitBool(dst, section, "maintenance4", value.maintenance4);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_QUEUE_PERF_HINT_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceQueuePerfHintFeaturesQCOM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceQueuePerfHintFeaturesQCOM";
            generatedEmitBool(dst, section, "queuePerfHint", value.queuePerfHint);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_PROCESSING_3_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceImageProcessing3FeaturesQCOM*>(ptr);
            const char* section = "Validated extension coverage · VkPhysicalDeviceImageProcessing3FeaturesQCOM";
            generatedEmitBool(dst, section, "imageGatherLinear", value.imageGatherLinear);
            generatedEmitBool(dst, section, "imageGatherExtendedModes", value.imageGatherExtendedModes);
            generatedEmitBool(dst, section, "blockMatchExtendedClampToEdge", value.blockMatchExtendedClampToEdge);
            break;
        }
        default: break;
    }
}

static constexpr uint32_t kValidatedParitySerializerCount = 297;
