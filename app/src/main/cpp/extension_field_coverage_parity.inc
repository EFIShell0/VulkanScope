static void appendParityStructFields(std::vector<GeneratedField>& dst, uint32_t sType, void* ptr) {
    switch (sType) {
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_4444_FORMATS_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevice4444FormatsFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevice4444FormatsFeaturesEXT";
            generatedEmitAuto(dst, section, "formatA4B4G4R4", value.formatA4B4G4R4);
            generatedEmitAuto(dst, section, "formatA4R4G4B4", value.formatA4R4G4B4);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ACCELERATION_STRUCTURE_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceAccelerationStructureFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceAccelerationStructureFeaturesKHR";
            generatedEmitAuto(dst, section, "accelerationStructure", value.accelerationStructure);
            generatedEmitAuto(dst, section, "accelerationStructureCaptureReplay", value.accelerationStructureCaptureReplay);
            generatedEmitAuto(dst, section, "accelerationStructureHostCommands", value.accelerationStructureHostCommands);
            generatedEmitAuto(dst, section, "accelerationStructureIndirectBuild", value.accelerationStructureIndirectBuild);
            generatedEmitAuto(dst, section, "descriptorBindingAccelerationStructureUpdateAfterBind", value.descriptorBindingAccelerationStructureUpdateAfterBind);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ADDRESS_BINDING_REPORT_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceAddressBindingReportFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceAddressBindingReportFeaturesEXT";
            generatedEmitAuto(dst, section, "reportAddressBinding", value.reportAddressBinding);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_AMIGO_PROFILING_FEATURES_SEC: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceAmigoProfilingFeaturesSEC*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceAmigoProfilingFeaturesSEC";
            generatedEmitAuto(dst, section, "amigoProfiling", value.amigoProfiling);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ATTACHMENT_FEEDBACK_LOOP_DYNAMIC_STATE_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceAttachmentFeedbackLoopDynamicStateFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceAttachmentFeedbackLoopDynamicStateFeaturesEXT";
            generatedEmitAuto(dst, section, "attachmentFeedbackLoopDynamicState", value.attachmentFeedbackLoopDynamicState);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ATTACHMENT_FEEDBACK_LOOP_LAYOUT_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceAttachmentFeedbackLoopLayoutFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceAttachmentFeedbackLoopLayoutFeaturesEXT";
            generatedEmitAuto(dst, section, "attachmentFeedbackLoopLayout", value.attachmentFeedbackLoopLayout);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_BLEND_OPERATION_ADVANCED_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceBlendOperationAdvancedFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceBlendOperationAdvancedFeaturesEXT";
            generatedEmitAuto(dst, section, "advancedBlendCoherentOperations", value.advancedBlendCoherentOperations);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_BLEND_OPERATION_ADVANCED_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceBlendOperationAdvancedPropertiesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceBlendOperationAdvancedPropertiesEXT";
            generatedEmitAuto(dst, section, "advancedBlendAllOperations", value.advancedBlendAllOperations);
            generatedEmitAuto(dst, section, "advancedBlendCorrelatedOverlap", value.advancedBlendCorrelatedOverlap);
            generatedEmitAuto(dst, section, "advancedBlendIndependentBlend", value.advancedBlendIndependentBlend);
            generatedEmitAuto(dst, section, "advancedBlendMaxColorAttachments", value.advancedBlendMaxColorAttachments);
            generatedEmitAuto(dst, section, "advancedBlendNonPremultipliedDstColor", value.advancedBlendNonPremultipliedDstColor);
            generatedEmitAuto(dst, section, "advancedBlendNonPremultipliedSrcColor", value.advancedBlendNonPremultipliedSrcColor);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_BORDER_COLOR_SWIZZLE_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceBorderColorSwizzleFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceBorderColorSwizzleFeaturesEXT";
            generatedEmitAuto(dst, section, "borderColorSwizzle", value.borderColorSwizzle);
            generatedEmitAuto(dst, section, "borderColorSwizzleFromImage", value.borderColorSwizzleFromImage);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_BUFFER_DEVICE_ADDRESS_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceBufferDeviceAddressFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceBufferDeviceAddressFeaturesEXT";
            generatedEmitAuto(dst, section, "bufferDeviceAddress", value.bufferDeviceAddress);
            generatedEmitAuto(dst, section, "bufferDeviceAddressCaptureReplay", value.bufferDeviceAddressCaptureReplay);
            generatedEmitAuto(dst, section, "bufferDeviceAddressMultiDevice", value.bufferDeviceAddressMultiDevice);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CLUSTER_ACCELERATION_STRUCTURE_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceClusterAccelerationStructureFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceClusterAccelerationStructureFeaturesNV";
            generatedEmitAuto(dst, section, "clusterAccelerationStructure", value.clusterAccelerationStructure);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CLUSTER_ACCELERATION_STRUCTURE_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceClusterAccelerationStructurePropertiesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceClusterAccelerationStructurePropertiesNV";
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
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceClusterCullingShaderFeaturesHUAWEI";
            generatedEmitAuto(dst, section, "clustercullingShader", value.clustercullingShader);
            generatedEmitAuto(dst, section, "multiviewClusterCullingShader", value.multiviewClusterCullingShader);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CLUSTER_CULLING_SHADER_PROPERTIES_HUAWEI: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceClusterCullingShaderPropertiesHUAWEI*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceClusterCullingShaderPropertiesHUAWEI";
            generatedEmitAuto(dst, section, "indirectBufferOffsetAlignment", value.indirectBufferOffsetAlignment);
            generatedEmitAuto(dst, section, "maxOutputClusterCount", value.maxOutputClusterCount);
            generatedEmitAuto(dst, section, "maxWorkGroupCount", value.maxWorkGroupCount);
            generatedEmitAuto(dst, section, "maxWorkGroupSize", value.maxWorkGroupSize);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COHERENT_MEMORY_FEATURES_AMD: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCoherentMemoryFeaturesAMD*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceCoherentMemoryFeaturesAMD";
            generatedEmitAuto(dst, section, "deviceCoherentMemory", value.deviceCoherentMemory);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COLOR_WRITE_ENABLE_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceColorWriteEnableFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceColorWriteEnableFeaturesEXT";
            generatedEmitAuto(dst, section, "colorWriteEnable", value.colorWriteEnable);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COMMAND_BUFFER_INHERITANCE_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCommandBufferInheritanceFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceCommandBufferInheritanceFeaturesNV";
            generatedEmitAuto(dst, section, "commandBufferInheritance", value.commandBufferInheritance);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COMPUTE_SHADER_DERIVATIVES_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceComputeShaderDerivativesFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceComputeShaderDerivativesFeaturesNV";
            generatedEmitAuto(dst, section, "computeDerivativeGroupLinear", value.computeDerivativeGroupLinear);
            generatedEmitAuto(dst, section, "computeDerivativeGroupQuads", value.computeDerivativeGroupQuads);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COMPUTE_SHADER_DERIVATIVES_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceComputeShaderDerivativesPropertiesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceComputeShaderDerivativesPropertiesKHR";
            generatedEmitAuto(dst, section, "meshAndTaskShaderDerivatives", value.meshAndTaskShaderDerivatives);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CONDITIONAL_RENDERING_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceConditionalRenderingFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceConditionalRenderingFeaturesEXT";
            generatedEmitAuto(dst, section, "conditionalRendering", value.conditionalRendering);
            generatedEmitAuto(dst, section, "inheritedConditionalRendering", value.inheritedConditionalRendering);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CONSERVATIVE_RASTERIZATION_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceConservativeRasterizationPropertiesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceConservativeRasterizationPropertiesEXT";
            generatedEmitAuto(dst, section, "conservativePointAndLineRasterization", value.conservativePointAndLineRasterization);
            generatedEmitAuto(dst, section, "conservativeRasterizationPostDepthCoverage", value.conservativeRasterizationPostDepthCoverage);
            generatedEmitAuto(dst, section, "degenerateLinesRasterized", value.degenerateLinesRasterized);
            generatedEmitAuto(dst, section, "degenerateTrianglesRasterized", value.degenerateTrianglesRasterized);
            generatedEmitAuto(dst, section, "extraPrimitiveOverestimationSizeGranularity", value.extraPrimitiveOverestimationSizeGranularity);
            generatedEmitAuto(dst, section, "fullyCoveredFragmentShaderInputVariable", value.fullyCoveredFragmentShaderInputVariable);
            generatedEmitAuto(dst, section, "maxExtraPrimitiveOverestimationSize", value.maxExtraPrimitiveOverestimationSize);
            generatedEmitAuto(dst, section, "primitiveOverestimationSize", value.primitiveOverestimationSize);
            generatedEmitAuto(dst, section, "primitiveUnderestimation", value.primitiveUnderestimation);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_2_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCooperativeMatrix2PropertiesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceCooperativeMatrix2PropertiesNV";
            generatedEmitAuto(dst, section, "cooperativeMatrixFlexibleDimensionsMaxDimension", value.cooperativeMatrixFlexibleDimensionsMaxDimension);
            generatedEmitAuto(dst, section, "cooperativeMatrixWorkgroupScopeMaxWorkgroupSize", value.cooperativeMatrixWorkgroupScopeMaxWorkgroupSize);
            generatedEmitAuto(dst, section, "cooperativeMatrixWorkgroupScopeReservedSharedMemory", value.cooperativeMatrixWorkgroupScopeReservedSharedMemory);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCooperativeMatrixFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceCooperativeMatrixFeaturesNV";
            generatedEmitAuto(dst, section, "cooperativeMatrix", value.cooperativeMatrix);
            generatedEmitAuto(dst, section, "cooperativeMatrixRobustBufferAccess", value.cooperativeMatrixRobustBufferAccess);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCooperativeMatrixPropertiesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceCooperativeMatrixPropertiesNV";
            generatedEmitAuto(dst, section, "cooperativeMatrixSupportedStages", value.cooperativeMatrixSupportedStages);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_VECTOR_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCooperativeVectorFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceCooperativeVectorFeaturesNV";
            generatedEmitAuto(dst, section, "cooperativeVector", value.cooperativeVector);
            generatedEmitAuto(dst, section, "cooperativeVectorTraining", value.cooperativeVectorTraining);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_VECTOR_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCooperativeVectorPropertiesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceCooperativeVectorPropertiesNV";
            generatedEmitAuto(dst, section, "cooperativeVectorSupportedStages", value.cooperativeVectorSupportedStages);
            generatedEmitAuto(dst, section, "cooperativeVectorTrainingFloat16Accumulation", value.cooperativeVectorTrainingFloat16Accumulation);
            generatedEmitAuto(dst, section, "cooperativeVectorTrainingFloat32Accumulation", value.cooperativeVectorTrainingFloat32Accumulation);
            generatedEmitAuto(dst, section, "maxCooperativeVectorComponents", value.maxCooperativeVectorComponents);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COPY_MEMORY_INDIRECT_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCopyMemoryIndirectFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceCopyMemoryIndirectFeaturesKHR";
            generatedEmitAuto(dst, section, "indirectMemoryCopy", value.indirectMemoryCopy);
            generatedEmitAuto(dst, section, "indirectMemoryToImageCopy", value.indirectMemoryToImageCopy);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COPY_MEMORY_INDIRECT_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCopyMemoryIndirectFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceCopyMemoryIndirectFeaturesNV";
            generatedEmitAuto(dst, section, "indirectCopy", value.indirectCopy);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COPY_MEMORY_INDIRECT_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCopyMemoryIndirectPropertiesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceCopyMemoryIndirectPropertiesNV";
            generatedEmitAuto(dst, section, "supportedQueues", value.supportedQueues);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CORNER_SAMPLED_IMAGE_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCornerSampledImageFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceCornerSampledImageFeaturesNV";
            generatedEmitAuto(dst, section, "cornerSampledImage", value.cornerSampledImage);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COVERAGE_REDUCTION_MODE_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCoverageReductionModeFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceCoverageReductionModeFeaturesNV";
            generatedEmitAuto(dst, section, "coverageReductionMode", value.coverageReductionMode);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CUBIC_CLAMP_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCubicClampFeaturesQCOM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceCubicClampFeaturesQCOM";
            generatedEmitAuto(dst, section, "cubicRangeClamp", value.cubicRangeClamp);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CUBIC_WEIGHTS_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCubicWeightsFeaturesQCOM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceCubicWeightsFeaturesQCOM";
            generatedEmitAuto(dst, section, "selectableCubicWeights", value.selectableCubicWeights);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CUDA_KERNEL_LAUNCH_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCudaKernelLaunchPropertiesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceCudaKernelLaunchPropertiesNV";
            generatedEmitAuto(dst, section, "computeCapabilityMajor", value.computeCapabilityMajor);
            generatedEmitAuto(dst, section, "computeCapabilityMinor", value.computeCapabilityMinor);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CUSTOM_BORDER_COLOR_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCustomBorderColorFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceCustomBorderColorFeaturesEXT";
            generatedEmitAuto(dst, section, "customBorderColorWithoutFormat", value.customBorderColorWithoutFormat);
            generatedEmitAuto(dst, section, "customBorderColors", value.customBorderColors);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CUSTOM_RESOLVE_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceCustomResolveFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceCustomResolveFeaturesEXT";
            generatedEmitAuto(dst, section, "customResolve", value.customResolve);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DATA_GRAPH_FEATURES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDataGraphFeaturesARM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDataGraphFeaturesARM";
            generatedEmitAuto(dst, section, "dataGraph", value.dataGraph);
            generatedEmitAuto(dst, section, "dataGraphDescriptorBuffer", value.dataGraphDescriptorBuffer);
            generatedEmitAuto(dst, section, "dataGraphShaderModule", value.dataGraphShaderModule);
            generatedEmitAuto(dst, section, "dataGraphSpecializationConstants", value.dataGraphSpecializationConstants);
            generatedEmitAuto(dst, section, "dataGraphUpdateAfterBind", value.dataGraphUpdateAfterBind);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DATA_GRAPH_MODEL_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDataGraphModelFeaturesQCOM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDataGraphModelFeaturesQCOM";
            generatedEmitAuto(dst, section, "dataGraphModel", value.dataGraphModel);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEDICATED_ALLOCATION_IMAGE_ALIASING_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDedicatedAllocationImageAliasingFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDedicatedAllocationImageAliasingFeaturesNV";
            generatedEmitAuto(dst, section, "dedicatedAllocationImageAliasing", value.dedicatedAllocationImageAliasing);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_BIAS_CONTROL_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDepthBiasControlFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDepthBiasControlFeaturesEXT";
            generatedEmitAuto(dst, section, "depthBiasControl", value.depthBiasControl);
            generatedEmitAuto(dst, section, "depthBiasExact", value.depthBiasExact);
            generatedEmitAuto(dst, section, "floatRepresentation", value.floatRepresentation);
            generatedEmitAuto(dst, section, "leastRepresentableValueForceUnormRepresentation", value.leastRepresentableValueForceUnormRepresentation);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_CLAMP_CONTROL_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDepthClampControlFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDepthClampControlFeaturesEXT";
            generatedEmitAuto(dst, section, "depthClampControl", value.depthClampControl);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_CLAMP_ZERO_ONE_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDepthClampZeroOneFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDepthClampZeroOneFeaturesKHR";
            generatedEmitAuto(dst, section, "depthClampZeroOne", value.depthClampZeroOne);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_CLIP_CONTROL_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDepthClipControlFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDepthClipControlFeaturesEXT";
            generatedEmitAuto(dst, section, "depthClipControl", value.depthClipControl);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_CLIP_ENABLE_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDepthClipEnableFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDepthClipEnableFeaturesEXT";
            generatedEmitAuto(dst, section, "depthClipEnable", value.depthClipEnable);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_BUFFER_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDescriptorBufferFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDescriptorBufferFeaturesEXT";
            generatedEmitAuto(dst, section, "descriptorBuffer", value.descriptorBuffer);
            generatedEmitAuto(dst, section, "descriptorBufferCaptureReplay", value.descriptorBufferCaptureReplay);
            generatedEmitAuto(dst, section, "descriptorBufferImageLayoutIgnored", value.descriptorBufferImageLayoutIgnored);
            generatedEmitAuto(dst, section, "descriptorBufferPushDescriptors", value.descriptorBufferPushDescriptors);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_BUFFER_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDescriptorBufferPropertiesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDescriptorBufferPropertiesEXT";
            generatedEmitAuto(dst, section, "accelerationStructureCaptureReplayDescriptorDataSize", value.accelerationStructureCaptureReplayDescriptorDataSize);
            generatedEmitAuto(dst, section, "accelerationStructureDescriptorSize", value.accelerationStructureDescriptorSize);
            generatedEmitAuto(dst, section, "allowSamplerImageViewPostSubmitCreation", value.allowSamplerImageViewPostSubmitCreation);
            generatedEmitAuto(dst, section, "bufferCaptureReplayDescriptorDataSize", value.bufferCaptureReplayDescriptorDataSize);
            generatedEmitAuto(dst, section, "bufferlessPushDescriptors", value.bufferlessPushDescriptors);
            generatedEmitAuto(dst, section, "combinedImageSamplerDescriptorSingleArray", value.combinedImageSamplerDescriptorSingleArray);
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
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDescriptorPoolOverallocationFeaturesNV";
            generatedEmitAuto(dst, section, "descriptorPoolOverallocation", value.descriptorPoolOverallocation);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_SET_HOST_MAPPING_FEATURES_VALVE: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDescriptorSetHostMappingFeaturesVALVE*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDescriptorSetHostMappingFeaturesVALVE";
            generatedEmitAuto(dst, section, "descriptorSetHostMapping", value.descriptorSetHostMapping);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEVICE_GENERATED_COMMANDS_COMPUTE_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDeviceGeneratedCommandsComputeFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDeviceGeneratedCommandsComputeFeaturesNV";
            generatedEmitAuto(dst, section, "deviceGeneratedCompute", value.deviceGeneratedCompute);
            generatedEmitAuto(dst, section, "deviceGeneratedComputeCaptureReplay", value.deviceGeneratedComputeCaptureReplay);
            generatedEmitAuto(dst, section, "deviceGeneratedComputePipelines", value.deviceGeneratedComputePipelines);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEVICE_GENERATED_COMMANDS_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDeviceGeneratedCommandsFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDeviceGeneratedCommandsFeaturesEXT";
            generatedEmitAuto(dst, section, "deviceGeneratedCommands", value.deviceGeneratedCommands);
            generatedEmitAuto(dst, section, "dynamicGeneratedPipelineLayout", value.dynamicGeneratedPipelineLayout);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEVICE_GENERATED_COMMANDS_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDeviceGeneratedCommandsFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDeviceGeneratedCommandsFeaturesNV";
            generatedEmitAuto(dst, section, "deviceGeneratedCommands", value.deviceGeneratedCommands);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEVICE_GENERATED_COMMANDS_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDeviceGeneratedCommandsPropertiesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDeviceGeneratedCommandsPropertiesEXT";
            generatedEmitAuto(dst, section, "deviceGeneratedCommandsMultiDrawIndirectCount", value.deviceGeneratedCommandsMultiDrawIndirectCount);
            generatedEmitAuto(dst, section, "deviceGeneratedCommandsTransformFeedback", value.deviceGeneratedCommandsTransformFeedback);
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
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDeviceMemoryReportFeaturesEXT";
            generatedEmitAuto(dst, section, "deviceMemoryReport", value.deviceMemoryReport);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DISCARD_RECTANGLE_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDiscardRectanglePropertiesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDiscardRectanglePropertiesEXT";
            generatedEmitAuto(dst, section, "maxDiscardRectangles", value.maxDiscardRectangles);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DISPLACEMENT_MICROMAP_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDisplacementMicromapFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDisplacementMicromapFeaturesNV";
            generatedEmitAuto(dst, section, "displacementMicromap", value.displacementMicromap);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DISPLACEMENT_MICROMAP_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDisplacementMicromapPropertiesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDisplacementMicromapPropertiesNV";
            generatedEmitAuto(dst, section, "maxDisplacementMicromapSubdivisionLevel", value.maxDisplacementMicromapSubdivisionLevel);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DRM_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDrmPropertiesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDrmPropertiesEXT";
            generatedEmitAuto(dst, section, "hasPrimary", value.hasPrimary);
            generatedEmitAuto(dst, section, "hasRender", value.hasRender);
            generatedEmitAuto(dst, section, "primaryMajor", value.primaryMajor);
            generatedEmitAuto(dst, section, "primaryMinor", value.primaryMinor);
            generatedEmitAuto(dst, section, "renderMajor", value.renderMajor);
            generatedEmitAuto(dst, section, "renderMinor", value.renderMinor);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DYNAMIC_RENDERING_UNUSED_ATTACHMENTS_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDynamicRenderingUnusedAttachmentsFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDynamicRenderingUnusedAttachmentsFeaturesEXT";
            generatedEmitAuto(dst, section, "dynamicRenderingUnusedAttachments", value.dynamicRenderingUnusedAttachments);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXCLUSIVE_SCISSOR_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExclusiveScissorFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceExclusiveScissorFeaturesNV";
            generatedEmitAuto(dst, section, "exclusiveScissor", value.exclusiveScissor);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_DYNAMIC_STATE_2_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExtendedDynamicState2FeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceExtendedDynamicState2FeaturesEXT";
            generatedEmitAuto(dst, section, "extendedDynamicState2", value.extendedDynamicState2);
            generatedEmitAuto(dst, section, "extendedDynamicState2LogicOp", value.extendedDynamicState2LogicOp);
            generatedEmitAuto(dst, section, "extendedDynamicState2PatchControlPoints", value.extendedDynamicState2PatchControlPoints);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_DYNAMIC_STATE_3_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExtendedDynamicState3FeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceExtendedDynamicState3FeaturesEXT";
            generatedEmitAuto(dst, section, "extendedDynamicState3AlphaToCoverageEnable", value.extendedDynamicState3AlphaToCoverageEnable);
            generatedEmitAuto(dst, section, "extendedDynamicState3AlphaToOneEnable", value.extendedDynamicState3AlphaToOneEnable);
            generatedEmitAuto(dst, section, "extendedDynamicState3ColorBlendAdvanced", value.extendedDynamicState3ColorBlendAdvanced);
            generatedEmitAuto(dst, section, "extendedDynamicState3ColorBlendEnable", value.extendedDynamicState3ColorBlendEnable);
            generatedEmitAuto(dst, section, "extendedDynamicState3ColorBlendEquation", value.extendedDynamicState3ColorBlendEquation);
            generatedEmitAuto(dst, section, "extendedDynamicState3ColorWriteMask", value.extendedDynamicState3ColorWriteMask);
            generatedEmitAuto(dst, section, "extendedDynamicState3ConservativeRasterizationMode", value.extendedDynamicState3ConservativeRasterizationMode);
            generatedEmitAuto(dst, section, "extendedDynamicState3CoverageModulationMode", value.extendedDynamicState3CoverageModulationMode);
            generatedEmitAuto(dst, section, "extendedDynamicState3CoverageModulationTable", value.extendedDynamicState3CoverageModulationTable);
            generatedEmitAuto(dst, section, "extendedDynamicState3CoverageModulationTableEnable", value.extendedDynamicState3CoverageModulationTableEnable);
            generatedEmitAuto(dst, section, "extendedDynamicState3CoverageReductionMode", value.extendedDynamicState3CoverageReductionMode);
            generatedEmitAuto(dst, section, "extendedDynamicState3CoverageToColorEnable", value.extendedDynamicState3CoverageToColorEnable);
            generatedEmitAuto(dst, section, "extendedDynamicState3CoverageToColorLocation", value.extendedDynamicState3CoverageToColorLocation);
            generatedEmitAuto(dst, section, "extendedDynamicState3DepthClampEnable", value.extendedDynamicState3DepthClampEnable);
            generatedEmitAuto(dst, section, "extendedDynamicState3DepthClipEnable", value.extendedDynamicState3DepthClipEnable);
            generatedEmitAuto(dst, section, "extendedDynamicState3DepthClipNegativeOneToOne", value.extendedDynamicState3DepthClipNegativeOneToOne);
            generatedEmitAuto(dst, section, "extendedDynamicState3ExtraPrimitiveOverestimationSize", value.extendedDynamicState3ExtraPrimitiveOverestimationSize);
            generatedEmitAuto(dst, section, "extendedDynamicState3LineRasterizationMode", value.extendedDynamicState3LineRasterizationMode);
            generatedEmitAuto(dst, section, "extendedDynamicState3LineStippleEnable", value.extendedDynamicState3LineStippleEnable);
            generatedEmitAuto(dst, section, "extendedDynamicState3LogicOpEnable", value.extendedDynamicState3LogicOpEnable);
            generatedEmitAuto(dst, section, "extendedDynamicState3PolygonMode", value.extendedDynamicState3PolygonMode);
            generatedEmitAuto(dst, section, "extendedDynamicState3ProvokingVertexMode", value.extendedDynamicState3ProvokingVertexMode);
            generatedEmitAuto(dst, section, "extendedDynamicState3RasterizationSamples", value.extendedDynamicState3RasterizationSamples);
            generatedEmitAuto(dst, section, "extendedDynamicState3RasterizationStream", value.extendedDynamicState3RasterizationStream);
            generatedEmitAuto(dst, section, "extendedDynamicState3RepresentativeFragmentTestEnable", value.extendedDynamicState3RepresentativeFragmentTestEnable);
            generatedEmitAuto(dst, section, "extendedDynamicState3SampleLocationsEnable", value.extendedDynamicState3SampleLocationsEnable);
            generatedEmitAuto(dst, section, "extendedDynamicState3SampleMask", value.extendedDynamicState3SampleMask);
            generatedEmitAuto(dst, section, "extendedDynamicState3ShadingRateImageEnable", value.extendedDynamicState3ShadingRateImageEnable);
            generatedEmitAuto(dst, section, "extendedDynamicState3TessellationDomainOrigin", value.extendedDynamicState3TessellationDomainOrigin);
            generatedEmitAuto(dst, section, "extendedDynamicState3ViewportSwizzle", value.extendedDynamicState3ViewportSwizzle);
            generatedEmitAuto(dst, section, "extendedDynamicState3ViewportWScalingEnable", value.extendedDynamicState3ViewportWScalingEnable);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_DYNAMIC_STATE_3_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExtendedDynamicState3PropertiesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceExtendedDynamicState3PropertiesEXT";
            generatedEmitAuto(dst, section, "dynamicPrimitiveTopologyUnrestricted", value.dynamicPrimitiveTopologyUnrestricted);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_DYNAMIC_STATE_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExtendedDynamicStateFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceExtendedDynamicStateFeaturesEXT";
            generatedEmitAuto(dst, section, "extendedDynamicState", value.extendedDynamicState);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_SPARSE_ADDRESS_SPACE_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExtendedSparseAddressSpaceFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceExtendedSparseAddressSpaceFeaturesNV";
            generatedEmitAuto(dst, section, "extendedSparseAddressSpace", value.extendedSparseAddressSpace);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_SPARSE_ADDRESS_SPACE_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExtendedSparseAddressSpacePropertiesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceExtendedSparseAddressSpacePropertiesNV";
            generatedEmitAuto(dst, section, "extendedSparseAddressSpaceSize", value.extendedSparseAddressSpaceSize);
            generatedEmitAuto(dst, section, "extendedSparseBufferUsageFlags", value.extendedSparseBufferUsageFlags);
            generatedEmitAuto(dst, section, "extendedSparseImageUsageFlags", value.extendedSparseImageUsageFlags);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_COMPUTE_QUEUE_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExternalComputeQueuePropertiesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceExternalComputeQueuePropertiesNV";
            generatedEmitAuto(dst, section, "externalDataSize", value.externalDataSize);
            generatedEmitAuto(dst, section, "maxExternalQueues", value.maxExternalQueues);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_FORMAT_RESOLVE_FEATURES_ANDROID: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExternalFormatResolveFeaturesANDROID*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceExternalFormatResolveFeaturesANDROID";
            generatedEmitAuto(dst, section, "externalFormatResolve", value.externalFormatResolve);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_FORMAT_RESOLVE_PROPERTIES_ANDROID: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExternalFormatResolvePropertiesANDROID*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceExternalFormatResolvePropertiesANDROID";
            generatedEmitAuto(dst, section, "externalFormatResolveChromaOffsetX", value.externalFormatResolveChromaOffsetX);
            generatedEmitAuto(dst, section, "externalFormatResolveChromaOffsetY", value.externalFormatResolveChromaOffsetY);
            generatedEmitAuto(dst, section, "nullColorAttachmentWithExternalFormatResolve", value.nullColorAttachmentWithExternalFormatResolve);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_MEMORY_HOST_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExternalMemoryHostPropertiesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceExternalMemoryHostPropertiesEXT";
            generatedEmitAuto(dst, section, "minImportedHostPointerAlignment", value.minImportedHostPointerAlignment);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_MEMORY_RDMA_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceExternalMemoryRDMAFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceExternalMemoryRDMAFeaturesNV";
            generatedEmitAuto(dst, section, "externalMemoryRDMA", value.externalMemoryRDMA);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FAULT_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFaultFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceFaultFeaturesEXT";
            generatedEmitAuto(dst, section, "deviceFault", value.deviceFault);
            generatedEmitAuto(dst, section, "deviceFaultVendorBinary", value.deviceFaultVendorBinary);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FORMAT_PACK_FEATURES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFormatPackFeaturesARM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceFormatPackFeaturesARM";
            generatedEmitAuto(dst, section, "formatPack", value.formatPack);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_2_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentDensityMap2FeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceFragmentDensityMap2FeaturesEXT";
            generatedEmitAuto(dst, section, "fragmentDensityMapDeferred", value.fragmentDensityMapDeferred);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentDensityMapFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceFragmentDensityMapFeaturesEXT";
            generatedEmitAuto(dst, section, "fragmentDensityMap", value.fragmentDensityMap);
            generatedEmitAuto(dst, section, "fragmentDensityMapDynamic", value.fragmentDensityMapDynamic);
            generatedEmitAuto(dst, section, "fragmentDensityMapNonSubsampledImages", value.fragmentDensityMapNonSubsampledImages);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_LAYERED_FEATURES_VALVE: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentDensityMapLayeredFeaturesVALVE*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceFragmentDensityMapLayeredFeaturesVALVE";
            generatedEmitAuto(dst, section, "fragmentDensityMapLayered", value.fragmentDensityMapLayered);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_LAYERED_PROPERTIES_VALVE: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentDensityMapLayeredPropertiesVALVE*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceFragmentDensityMapLayeredPropertiesVALVE";
            generatedEmitAuto(dst, section, "maxFragmentDensityMapLayers", value.maxFragmentDensityMapLayers);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_OFFSET_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentDensityMapOffsetFeaturesQCOM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceFragmentDensityMapOffsetFeaturesQCOM";
            generatedEmitAuto(dst, section, "fragmentDensityMapOffset", value.fragmentDensityMapOffset);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_OFFSET_PROPERTIES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentDensityMapOffsetPropertiesQCOM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceFragmentDensityMapOffsetPropertiesQCOM";
            generatedEmitAuto(dst, section, "fragmentDensityOffsetGranularity", value.fragmentDensityOffsetGranularity);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_SHADER_BARYCENTRIC_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentShaderBarycentricFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceFragmentShaderBarycentricFeaturesKHR";
            generatedEmitAuto(dst, section, "fragmentShaderBarycentric", value.fragmentShaderBarycentric);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_SHADER_BARYCENTRIC_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentShaderBarycentricPropertiesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceFragmentShaderBarycentricPropertiesKHR";
            generatedEmitAuto(dst, section, "triStripVertexOrderIndependentOfProvokingVertex", value.triStripVertexOrderIndependentOfProvokingVertex);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_SHADER_INTERLOCK_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentShaderInterlockFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceFragmentShaderInterlockFeaturesEXT";
            generatedEmitAuto(dst, section, "fragmentShaderPixelInterlock", value.fragmentShaderPixelInterlock);
            generatedEmitAuto(dst, section, "fragmentShaderSampleInterlock", value.fragmentShaderSampleInterlock);
            generatedEmitAuto(dst, section, "fragmentShaderShadingRateInterlock", value.fragmentShaderShadingRateInterlock);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_SHADING_RATE_ENUMS_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentShadingRateEnumsFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceFragmentShadingRateEnumsFeaturesNV";
            generatedEmitAuto(dst, section, "fragmentShadingRateEnums", value.fragmentShadingRateEnums);
            generatedEmitAuto(dst, section, "noInvocationFragmentShadingRates", value.noInvocationFragmentShadingRates);
            generatedEmitAuto(dst, section, "supersampleFragmentShadingRates", value.supersampleFragmentShadingRates);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_SHADING_RATE_ENUMS_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentShadingRateEnumsPropertiesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceFragmentShadingRateEnumsPropertiesNV";
            generatedEmitAuto(dst, section, "maxFragmentShadingRateInvocationCount", value.maxFragmentShadingRateInvocationCount);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_SHADING_RATE_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentShadingRateFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceFragmentShadingRateFeaturesKHR";
            generatedEmitAuto(dst, section, "attachmentFragmentShadingRate", value.attachmentFragmentShadingRate);
            generatedEmitAuto(dst, section, "pipelineFragmentShadingRate", value.pipelineFragmentShadingRate);
            generatedEmitAuto(dst, section, "primitiveFragmentShadingRate", value.primitiveFragmentShadingRate);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_SHADING_RATE_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFragmentShadingRatePropertiesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceFragmentShadingRatePropertiesKHR";
            generatedEmitAuto(dst, section, "fragmentShadingRateNonTrivialCombinerOps", value.fragmentShadingRateNonTrivialCombinerOps);
            generatedEmitAuto(dst, section, "fragmentShadingRateStrictMultiplyCombiner", value.fragmentShadingRateStrictMultiplyCombiner);
            generatedEmitAuto(dst, section, "fragmentShadingRateWithConservativeRasterization", value.fragmentShadingRateWithConservativeRasterization);
            generatedEmitAuto(dst, section, "fragmentShadingRateWithCustomSampleLocations", value.fragmentShadingRateWithCustomSampleLocations);
            generatedEmitAuto(dst, section, "fragmentShadingRateWithFragmentShaderInterlock", value.fragmentShadingRateWithFragmentShaderInterlock);
            generatedEmitAuto(dst, section, "fragmentShadingRateWithSampleMask", value.fragmentShadingRateWithSampleMask);
            generatedEmitAuto(dst, section, "fragmentShadingRateWithShaderDepthStencilWrites", value.fragmentShadingRateWithShaderDepthStencilWrites);
            generatedEmitAuto(dst, section, "fragmentShadingRateWithShaderSampleMask", value.fragmentShadingRateWithShaderSampleMask);
            generatedEmitAuto(dst, section, "layeredShadingRateAttachments", value.layeredShadingRateAttachments);
            generatedEmitAuto(dst, section, "maxFragmentShadingRateAttachmentTexelSize", value.maxFragmentShadingRateAttachmentTexelSize);
            generatedEmitAuto(dst, section, "maxFragmentShadingRateAttachmentTexelSizeAspectRatio", value.maxFragmentShadingRateAttachmentTexelSizeAspectRatio);
            generatedEmitAuto(dst, section, "maxFragmentShadingRateCoverageSamples", value.maxFragmentShadingRateCoverageSamples);
            generatedEmitAuto(dst, section, "maxFragmentShadingRateRasterizationSamples", value.maxFragmentShadingRateRasterizationSamples);
            generatedEmitAuto(dst, section, "maxFragmentSize", value.maxFragmentSize);
            generatedEmitAuto(dst, section, "maxFragmentSizeAspectRatio", value.maxFragmentSizeAspectRatio);
            generatedEmitAuto(dst, section, "minFragmentShadingRateAttachmentTexelSize", value.minFragmentShadingRateAttachmentTexelSize);
            generatedEmitAuto(dst, section, "primitiveFragmentShadingRateWithMultipleViewports", value.primitiveFragmentShadingRateWithMultipleViewports);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAME_BOUNDARY_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFrameBoundaryFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceFrameBoundaryFeaturesEXT";
            generatedEmitAuto(dst, section, "frameBoundary", value.frameBoundary);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_GLOBAL_PRIORITY_QUERY_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceGlobalPriorityQueryFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceGlobalPriorityQueryFeaturesKHR";
            generatedEmitAuto(dst, section, "globalPriorityQuery", value.globalPriorityQuery);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_GPA_FEATURES_AMD: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceGpaFeaturesAMD*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceGpaFeaturesAMD";
            generatedEmitAuto(dst, section, "clockModes", value.clockModes);
            generatedEmitAuto(dst, section, "perfCounters", value.perfCounters);
            generatedEmitAuto(dst, section, "sqThreadTracing", value.sqThreadTracing);
            generatedEmitAuto(dst, section, "streamingPerfCounters", value.streamingPerfCounters);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_GRAPHICS_PIPELINE_LIBRARY_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceGraphicsPipelineLibraryFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceGraphicsPipelineLibraryFeaturesEXT";
            generatedEmitAuto(dst, section, "graphicsPipelineLibrary", value.graphicsPipelineLibrary);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_GRAPHICS_PIPELINE_LIBRARY_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceGraphicsPipelineLibraryPropertiesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceGraphicsPipelineLibraryPropertiesEXT";
            generatedEmitAuto(dst, section, "graphicsPipelineLibraryFastLinking", value.graphicsPipelineLibraryFastLinking);
            generatedEmitAuto(dst, section, "graphicsPipelineLibraryIndependentInterpolationDecoration", value.graphicsPipelineLibraryIndependentInterpolationDecoration);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_HDR_VIVID_FEATURES_HUAWEI: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceHdrVividFeaturesHUAWEI*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceHdrVividFeaturesHUAWEI";
            generatedEmitAuto(dst, section, "hdrVivid", value.hdrVivid);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_HOST_IMAGE_COPY_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceHostImageCopyFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceHostImageCopyFeaturesEXT";
            generatedEmitAuto(dst, section, "hostImageCopy", value.hostImageCopy);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_HOST_IMAGE_COPY_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceHostImageCopyPropertiesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceHostImageCopyPropertiesEXT";
            generatedEmitAuto(dst, section, "copyDstLayoutCount", value.copyDstLayoutCount);
            generatedEmitAuto(dst, section, "copySrcLayoutCount", value.copySrcLayoutCount);
            generatedEmitAuto(dst, section, "identicalMemoryTypeRequirements", value.identicalMemoryTypeRequirements);
            generatedEmitAuto(dst, section, "optimalTilingLayoutUUID", value.optimalTilingLayoutUUID);
            generatedEmitAuto(dst, section, "pCopyDstLayouts", value.pCopyDstLayouts);
            generatedEmitAuto(dst, section, "pCopySrcLayouts", value.pCopySrcLayouts);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_ALIGNMENT_CONTROL_FEATURES_MESA: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceImageAlignmentControlFeaturesMESA*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceImageAlignmentControlFeaturesMESA";
            generatedEmitAuto(dst, section, "imageAlignmentControl", value.imageAlignmentControl);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_ALIGNMENT_CONTROL_PROPERTIES_MESA: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceImageAlignmentControlPropertiesMESA*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceImageAlignmentControlPropertiesMESA";
            generatedEmitAuto(dst, section, "supportedImageAlignmentMask", value.supportedImageAlignmentMask);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_COMPRESSION_CONTROL_SWAPCHAIN_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceImageCompressionControlSwapchainFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceImageCompressionControlSwapchainFeaturesEXT";
            generatedEmitAuto(dst, section, "imageCompressionControlSwapchain", value.imageCompressionControlSwapchain);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_PROCESSING_2_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceImageProcessing2FeaturesQCOM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceImageProcessing2FeaturesQCOM";
            generatedEmitAuto(dst, section, "textureBlockMatch2", value.textureBlockMatch2);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_PROCESSING_2_PROPERTIES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceImageProcessing2PropertiesQCOM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceImageProcessing2PropertiesQCOM";
            generatedEmitAuto(dst, section, "maxBlockMatchWindow", value.maxBlockMatchWindow);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_PROCESSING_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceImageProcessingFeaturesQCOM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceImageProcessingFeaturesQCOM";
            generatedEmitAuto(dst, section, "textureBlockMatch", value.textureBlockMatch);
            generatedEmitAuto(dst, section, "textureBoxFilter", value.textureBoxFilter);
            generatedEmitAuto(dst, section, "textureSampleWeighted", value.textureSampleWeighted);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_PROCESSING_PROPERTIES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceImageProcessingPropertiesQCOM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceImageProcessingPropertiesQCOM";
            generatedEmitAuto(dst, section, "maxBlockMatchRegion", value.maxBlockMatchRegion);
            generatedEmitAuto(dst, section, "maxBoxFilterBlockSize", value.maxBoxFilterBlockSize);
            generatedEmitAuto(dst, section, "maxWeightFilterDimension", value.maxWeightFilterDimension);
            generatedEmitAuto(dst, section, "maxWeightFilterPhases", value.maxWeightFilterPhases);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_SLICED_VIEW_OF_3D_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceImageSlicedViewOf3DFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceImageSlicedViewOf3DFeaturesEXT";
            generatedEmitAuto(dst, section, "imageSlicedViewOf3D", value.imageSlicedViewOf3D);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_VIEW_MIN_LOD_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceImageViewMinLodFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceImageViewMinLodFeaturesEXT";
            generatedEmitAuto(dst, section, "minLod", value.minLod);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INDEX_TYPE_UINT8_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceIndexTypeUint8FeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceIndexTypeUint8FeaturesKHR";
            generatedEmitAuto(dst, section, "indexTypeUint8", value.indexTypeUint8);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INHERITED_VIEWPORT_SCISSOR_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceInheritedViewportScissorFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceInheritedViewportScissorFeaturesNV";
            generatedEmitAuto(dst, section, "inheritedViewportScissor2D", value.inheritedViewportScissor2D);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INVOCATION_MASK_FEATURES_HUAWEI: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceInvocationMaskFeaturesHUAWEI*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceInvocationMaskFeaturesHUAWEI";
            generatedEmitAuto(dst, section, "invocationMask", value.invocationMask);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LAYERED_DRIVER_PROPERTIES_MSFT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceLayeredDriverPropertiesMSFT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceLayeredDriverPropertiesMSFT";
            generatedEmitAuto(dst, section, "underlyingAPI", value.underlyingAPI);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LEGACY_DITHERING_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceLegacyDitheringFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceLegacyDitheringFeaturesEXT";
            generatedEmitAuto(dst, section, "legacyDithering", value.legacyDithering);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LEGACY_VERTEX_ATTRIBUTES_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceLegacyVertexAttributesFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceLegacyVertexAttributesFeaturesEXT";
            generatedEmitAuto(dst, section, "legacyVertexAttributes", value.legacyVertexAttributes);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LEGACY_VERTEX_ATTRIBUTES_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceLegacyVertexAttributesPropertiesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceLegacyVertexAttributesPropertiesEXT";
            generatedEmitAuto(dst, section, "nativeUnalignedPerformance", value.nativeUnalignedPerformance);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINE_RASTERIZATION_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceLineRasterizationFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceLineRasterizationFeaturesKHR";
            generatedEmitAuto(dst, section, "bresenhamLines", value.bresenhamLines);
            generatedEmitAuto(dst, section, "rectangularLines", value.rectangularLines);
            generatedEmitAuto(dst, section, "smoothLines", value.smoothLines);
            generatedEmitAuto(dst, section, "stippledBresenhamLines", value.stippledBresenhamLines);
            generatedEmitAuto(dst, section, "stippledRectangularLines", value.stippledRectangularLines);
            generatedEmitAuto(dst, section, "stippledSmoothLines", value.stippledSmoothLines);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINE_RASTERIZATION_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceLineRasterizationPropertiesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceLineRasterizationPropertiesKHR";
            generatedEmitAuto(dst, section, "lineSubPixelPrecisionBits", value.lineSubPixelPrecisionBits);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINEAR_COLOR_ATTACHMENT_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceLinearColorAttachmentFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceLinearColorAttachmentFeaturesNV";
            generatedEmitAuto(dst, section, "linearColorAttachment", value.linearColorAttachment);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_5_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMaintenance5PropertiesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceMaintenance5PropertiesKHR";
            generatedEmitAuto(dst, section, "depthStencilSwizzleOneSupport", value.depthStencilSwizzleOneSupport);
            generatedEmitAuto(dst, section, "earlyFragmentMultisampleCoverageAfterSampleCounting", value.earlyFragmentMultisampleCoverageAfterSampleCounting);
            generatedEmitAuto(dst, section, "earlyFragmentSampleMaskTestBeforeSampleCounting", value.earlyFragmentSampleMaskTestBeforeSampleCounting);
            generatedEmitAuto(dst, section, "nonStrictSinglePixelWideLinesUseParallelogram", value.nonStrictSinglePixelWideLinesUseParallelogram);
            generatedEmitAuto(dst, section, "nonStrictWideLinesUseParallelogram", value.nonStrictWideLinesUseParallelogram);
            generatedEmitAuto(dst, section, "polygonModePointSize", value.polygonModePointSize);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_6_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMaintenance6FeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceMaintenance6FeaturesKHR";
            generatedEmitAuto(dst, section, "maintenance6", value.maintenance6);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_6_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMaintenance6PropertiesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceMaintenance6PropertiesKHR";
            generatedEmitAuto(dst, section, "blockTexelViewCompatibleMultipleLayers", value.blockTexelViewCompatibleMultipleLayers);
            generatedEmitAuto(dst, section, "fragmentShadingRateClampCombinerInputs", value.fragmentShadingRateClampCombinerInputs);
            generatedEmitAuto(dst, section, "maxCombinedImageSamplerDescriptorCount", value.maxCombinedImageSamplerDescriptorCount);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAP_MEMORY_PLACED_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMapMemoryPlacedFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceMapMemoryPlacedFeaturesEXT";
            generatedEmitAuto(dst, section, "memoryMapPlaced", value.memoryMapPlaced);
            generatedEmitAuto(dst, section, "memoryMapRangePlaced", value.memoryMapRangePlaced);
            generatedEmitAuto(dst, section, "memoryUnmapReserve", value.memoryUnmapReserve);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MEMORY_DECOMPRESSION_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMemoryDecompressionFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceMemoryDecompressionFeaturesNV";
            generatedEmitAuto(dst, section, "memoryDecompression", value.memoryDecompression);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MEMORY_DECOMPRESSION_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMemoryDecompressionPropertiesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceMemoryDecompressionPropertiesNV";
            generatedEmitAuto(dst, section, "decompressionMethods", value.decompressionMethods);
            generatedEmitAuto(dst, section, "maxDecompressionIndirectCount", value.maxDecompressionIndirectCount);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MEMORY_PRIORITY_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMemoryPriorityFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceMemoryPriorityFeaturesEXT";
            generatedEmitAuto(dst, section, "memoryPriority", value.memoryPriority);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MESH_SHADER_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMeshShaderFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceMeshShaderFeaturesEXT";
            generatedEmitAuto(dst, section, "meshShader", value.meshShader);
            generatedEmitAuto(dst, section, "meshShaderQueries", value.meshShaderQueries);
            generatedEmitAuto(dst, section, "multiviewMeshShader", value.multiviewMeshShader);
            generatedEmitAuto(dst, section, "primitiveFragmentShadingRateMeshShader", value.primitiveFragmentShadingRateMeshShader);
            generatedEmitAuto(dst, section, "taskShader", value.taskShader);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MESH_SHADER_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMeshShaderFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceMeshShaderFeaturesNV";
            generatedEmitAuto(dst, section, "meshShader", value.meshShader);
            generatedEmitAuto(dst, section, "taskShader", value.taskShader);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MESH_SHADER_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMeshShaderPropertiesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceMeshShaderPropertiesEXT";
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
            generatedEmitAuto(dst, section, "prefersCompactPrimitiveOutput", value.prefersCompactPrimitiveOutput);
            generatedEmitAuto(dst, section, "prefersCompactVertexOutput", value.prefersCompactVertexOutput);
            generatedEmitAuto(dst, section, "prefersLocalInvocationPrimitiveOutput", value.prefersLocalInvocationPrimitiveOutput);
            generatedEmitAuto(dst, section, "prefersLocalInvocationVertexOutput", value.prefersLocalInvocationVertexOutput);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MESH_SHADER_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMeshShaderPropertiesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceMeshShaderPropertiesNV";
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
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceMultiDrawFeaturesEXT";
            generatedEmitAuto(dst, section, "multiDraw", value.multiDraw);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTI_DRAW_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMultiDrawPropertiesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceMultiDrawPropertiesEXT";
            generatedEmitAuto(dst, section, "maxMultiDrawCount", value.maxMultiDrawCount);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTISAMPLED_RENDER_TO_SINGLE_SAMPLED_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMultisampledRenderToSingleSampledFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceMultisampledRenderToSingleSampledFeaturesEXT";
            generatedEmitAuto(dst, section, "multisampledRenderToSingleSampled", value.multisampledRenderToSingleSampled);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTIVIEW_PER_VIEW_ATTRIBUTES_PROPERTIES_NVX: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMultiviewPerViewAttributesPropertiesNVX*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceMultiviewPerViewAttributesPropertiesNVX";
            generatedEmitAuto(dst, section, "perViewPositionAllComponents", value.perViewPositionAllComponents);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTIVIEW_PER_VIEW_RENDER_AREAS_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMultiviewPerViewRenderAreasFeaturesQCOM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceMultiviewPerViewRenderAreasFeaturesQCOM";
            generatedEmitAuto(dst, section, "multiviewPerViewRenderAreas", value.multiviewPerViewRenderAreas);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTIVIEW_PER_VIEW_VIEWPORTS_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMultiviewPerViewViewportsFeaturesQCOM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceMultiviewPerViewViewportsFeaturesQCOM";
            generatedEmitAuto(dst, section, "multiviewPerViewViewports", value.multiviewPerViewViewports);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MUTABLE_DESCRIPTOR_TYPE_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMutableDescriptorTypeFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceMutableDescriptorTypeFeaturesEXT";
            generatedEmitAuto(dst, section, "mutableDescriptorType", value.mutableDescriptorType);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_NESTED_COMMAND_BUFFER_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceNestedCommandBufferFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceNestedCommandBufferFeaturesEXT";
            generatedEmitAuto(dst, section, "nestedCommandBuffer", value.nestedCommandBuffer);
            generatedEmitAuto(dst, section, "nestedCommandBufferRendering", value.nestedCommandBufferRendering);
            generatedEmitAuto(dst, section, "nestedCommandBufferSimultaneousUse", value.nestedCommandBufferSimultaneousUse);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_NESTED_COMMAND_BUFFER_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceNestedCommandBufferPropertiesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceNestedCommandBufferPropertiesEXT";
            generatedEmitAuto(dst, section, "maxCommandBufferNestingLevel", value.maxCommandBufferNestingLevel);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_NON_SEAMLESS_CUBE_MAP_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceNonSeamlessCubeMapFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceNonSeamlessCubeMapFeaturesEXT";
            generatedEmitAuto(dst, section, "nonSeamlessCubeMap", value.nonSeamlessCubeMap);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_OPACITY_MICROMAP_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceOpacityMicromapFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceOpacityMicromapFeaturesEXT";
            generatedEmitAuto(dst, section, "micromap", value.micromap);
            generatedEmitAuto(dst, section, "micromapCaptureReplay", value.micromapCaptureReplay);
            generatedEmitAuto(dst, section, "micromapHostCommands", value.micromapHostCommands);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_OPACITY_MICROMAP_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceOpacityMicromapPropertiesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceOpacityMicromapPropertiesEXT";
            generatedEmitAuto(dst, section, "maxOpacity2StateSubdivisionLevel", value.maxOpacity2StateSubdivisionLevel);
            generatedEmitAuto(dst, section, "maxOpacity4StateSubdivisionLevel", value.maxOpacity4StateSubdivisionLevel);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_OPTICAL_FLOW_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceOpticalFlowFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceOpticalFlowFeaturesNV";
            generatedEmitAuto(dst, section, "opticalFlow", value.opticalFlow);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_OPTICAL_FLOW_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceOpticalFlowPropertiesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceOpticalFlowPropertiesNV";
            generatedEmitAuto(dst, section, "bidirectionalFlowSupported", value.bidirectionalFlowSupported);
            generatedEmitAuto(dst, section, "costSupported", value.costSupported);
            generatedEmitAuto(dst, section, "globalFlowSupported", value.globalFlowSupported);
            generatedEmitAuto(dst, section, "hintSupported", value.hintSupported);
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
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevicePCIBusInfoPropertiesEXT";
            generatedEmitAuto(dst, section, "pciBus", value.pciBus);
            generatedEmitAuto(dst, section, "pciDevice", value.pciDevice);
            generatedEmitAuto(dst, section, "pciDomain", value.pciDomain);
            generatedEmitAuto(dst, section, "pciFunction", value.pciFunction);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PAGEABLE_DEVICE_LOCAL_MEMORY_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePageableDeviceLocalMemoryFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevicePageableDeviceLocalMemoryFeaturesEXT";
            generatedEmitAuto(dst, section, "pageableDeviceLocalMemory", value.pageableDeviceLocalMemory);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PARTITIONED_ACCELERATION_STRUCTURE_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePartitionedAccelerationStructureFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevicePartitionedAccelerationStructureFeaturesNV";
            generatedEmitAuto(dst, section, "partitionedAccelerationStructure", value.partitionedAccelerationStructure);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PARTITIONED_ACCELERATION_STRUCTURE_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePartitionedAccelerationStructurePropertiesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevicePartitionedAccelerationStructurePropertiesNV";
            generatedEmitAuto(dst, section, "maxPartitionCount", value.maxPartitionCount);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PER_STAGE_DESCRIPTOR_SET_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePerStageDescriptorSetFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevicePerStageDescriptorSetFeaturesNV";
            generatedEmitAuto(dst, section, "dynamicPipelineLayout", value.dynamicPipelineLayout);
            generatedEmitAuto(dst, section, "perStageDescriptorSet", value.perStageDescriptorSet);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PERFORMANCE_COUNTERS_BY_REGION_PROPERTIES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePerformanceCountersByRegionPropertiesARM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevicePerformanceCountersByRegionPropertiesARM";
            generatedEmitAuto(dst, section, "identityTransformOrder", value.identityTransformOrder);
            generatedEmitAuto(dst, section, "maxPerRegionPerformanceCounters", value.maxPerRegionPerformanceCounters);
            generatedEmitAuto(dst, section, "performanceCounterRegionSize", value.performanceCounterRegionSize);
            generatedEmitAuto(dst, section, "regionAlignment", value.regionAlignment);
            generatedEmitAuto(dst, section, "rowStrideAlignment", value.rowStrideAlignment);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PERFORMANCE_QUERY_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePerformanceQueryFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevicePerformanceQueryFeaturesKHR";
            generatedEmitAuto(dst, section, "performanceCounterMultipleQueryPools", value.performanceCounterMultipleQueryPools);
            generatedEmitAuto(dst, section, "performanceCounterQueryPools", value.performanceCounterQueryPools);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PERFORMANCE_QUERY_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePerformanceQueryPropertiesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevicePerformanceQueryPropertiesKHR";
            generatedEmitAuto(dst, section, "allowCommandBufferQueryCopies", value.allowCommandBufferQueryCopies);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_EXECUTABLE_PROPERTIES_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePipelineExecutablePropertiesFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevicePipelineExecutablePropertiesFeaturesKHR";
            generatedEmitAuto(dst, section, "pipelineExecutableInfo", value.pipelineExecutableInfo);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_LIBRARY_GROUP_HANDLES_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePipelineLibraryGroupHandlesFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevicePipelineLibraryGroupHandlesFeaturesEXT";
            generatedEmitAuto(dst, section, "pipelineLibraryGroupHandles", value.pipelineLibraryGroupHandles);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_PROPERTIES_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePipelinePropertiesFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevicePipelinePropertiesFeaturesEXT";
            generatedEmitAuto(dst, section, "pipelinePropertiesIdentifier", value.pipelinePropertiesIdentifier);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_PROTECTED_ACCESS_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePipelineProtectedAccessFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevicePipelineProtectedAccessFeaturesEXT";
            generatedEmitAuto(dst, section, "pipelineProtectedAccess", value.pipelineProtectedAccess);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_ROBUSTNESS_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePipelineRobustnessPropertiesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevicePipelineRobustnessPropertiesEXT";
            generatedEmitAuto(dst, section, "defaultRobustnessImages", value.defaultRobustnessImages);
            generatedEmitAuto(dst, section, "defaultRobustnessStorageBuffers", value.defaultRobustnessStorageBuffers);
            generatedEmitAuto(dst, section, "defaultRobustnessUniformBuffers", value.defaultRobustnessUniformBuffers);
            generatedEmitAuto(dst, section, "defaultRobustnessVertexInputs", value.defaultRobustnessVertexInputs);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PORTABILITY_SUBSET_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePortabilitySubsetFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevicePortabilitySubsetFeaturesKHR";
            generatedEmitAuto(dst, section, "constantAlphaColorBlendFactors", value.constantAlphaColorBlendFactors);
            generatedEmitAuto(dst, section, "events", value.events);
            generatedEmitAuto(dst, section, "imageView2DOn3DImage", value.imageView2DOn3DImage);
            generatedEmitAuto(dst, section, "imageViewFormatReinterpretation", value.imageViewFormatReinterpretation);
            generatedEmitAuto(dst, section, "imageViewFormatSwizzle", value.imageViewFormatSwizzle);
            generatedEmitAuto(dst, section, "multisampleArrayImage", value.multisampleArrayImage);
            generatedEmitAuto(dst, section, "mutableComparisonSamplers", value.mutableComparisonSamplers);
            generatedEmitAuto(dst, section, "pointPolygons", value.pointPolygons);
            generatedEmitAuto(dst, section, "samplerMipLodBias", value.samplerMipLodBias);
            generatedEmitAuto(dst, section, "separateStencilMaskRef", value.separateStencilMaskRef);
            generatedEmitAuto(dst, section, "shaderSampleRateInterpolationFunctions", value.shaderSampleRateInterpolationFunctions);
            generatedEmitAuto(dst, section, "tessellationIsolines", value.tessellationIsolines);
            generatedEmitAuto(dst, section, "tessellationPointMode", value.tessellationPointMode);
            generatedEmitAuto(dst, section, "triangleFans", value.triangleFans);
            generatedEmitAuto(dst, section, "vertexAttributeAccessBeyondStride", value.vertexAttributeAccessBeyondStride);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_ID_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePresentIdFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevicePresentIdFeaturesKHR";
            generatedEmitAuto(dst, section, "presentId", value.presentId);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_MODE_FIFO_LATEST_READY_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePresentModeFifoLatestReadyFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevicePresentModeFifoLatestReadyFeaturesEXT";
            generatedEmitAuto(dst, section, "presentModeFifoLatestReady", value.presentModeFifoLatestReady);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_TIMING_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePresentTimingFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevicePresentTimingFeaturesEXT";
            generatedEmitAuto(dst, section, "presentAtAbsoluteTime", value.presentAtAbsoluteTime);
            generatedEmitAuto(dst, section, "presentAtRelativeTime", value.presentAtRelativeTime);
            generatedEmitAuto(dst, section, "presentTiming", value.presentTiming);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_WAIT_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePresentWaitFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevicePresentWaitFeaturesKHR";
            generatedEmitAuto(dst, section, "presentWait", value.presentWait);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRIMITIVE_TOPOLOGY_LIST_RESTART_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePrimitiveTopologyListRestartFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevicePrimitiveTopologyListRestartFeaturesEXT";
            generatedEmitAuto(dst, section, "primitiveTopologyListRestart", value.primitiveTopologyListRestart);
            generatedEmitAuto(dst, section, "primitiveTopologyPatchListRestart", value.primitiveTopologyPatchListRestart);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRIMITIVES_GENERATED_QUERY_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePrimitivesGeneratedQueryFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevicePrimitivesGeneratedQueryFeaturesEXT";
            generatedEmitAuto(dst, section, "primitivesGeneratedQuery", value.primitivesGeneratedQuery);
            generatedEmitAuto(dst, section, "primitivesGeneratedQueryWithNonZeroStreams", value.primitivesGeneratedQueryWithNonZeroStreams);
            generatedEmitAuto(dst, section, "primitivesGeneratedQueryWithRasterizerDiscard", value.primitivesGeneratedQueryWithRasterizerDiscard);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROVOKING_VERTEX_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceProvokingVertexFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceProvokingVertexFeaturesEXT";
            generatedEmitAuto(dst, section, "provokingVertexLast", value.provokingVertexLast);
            generatedEmitAuto(dst, section, "transformFeedbackPreservesProvokingVertex", value.transformFeedbackPreservesProvokingVertex);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PUSH_DESCRIPTOR_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePushDescriptorPropertiesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevicePushDescriptorPropertiesKHR";
            generatedEmitAuto(dst, section, "maxPushDescriptors", value.maxPushDescriptors);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RGBA10X6_FORMATS_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRGBA10X6FormatsFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceRGBA10X6FormatsFeaturesEXT";
            generatedEmitAuto(dst, section, "formatRgba10x6WithoutYCbCrSampler", value.formatRgba10x6WithoutYCbCrSampler);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RASTERIZATION_ORDER_ATTACHMENT_ACCESS_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRasterizationOrderAttachmentAccessFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceRasterizationOrderAttachmentAccessFeaturesEXT";
            generatedEmitAuto(dst, section, "rasterizationOrderColorAttachmentAccess", value.rasterizationOrderColorAttachmentAccess);
            generatedEmitAuto(dst, section, "rasterizationOrderDepthAttachmentAccess", value.rasterizationOrderDepthAttachmentAccess);
            generatedEmitAuto(dst, section, "rasterizationOrderStencilAttachmentAccess", value.rasterizationOrderStencilAttachmentAccess);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAW_ACCESS_CHAINS_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRawAccessChainsFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceRawAccessChainsFeaturesNV";
            generatedEmitAuto(dst, section, "shaderRawAccessChains", value.shaderRawAccessChains);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_QUERY_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRayQueryFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceRayQueryFeaturesKHR";
            generatedEmitAuto(dst, section, "rayQuery", value.rayQuery);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_INVOCATION_REORDER_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRayTracingInvocationReorderFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceRayTracingInvocationReorderFeaturesEXT";
            generatedEmitAuto(dst, section, "rayTracingInvocationReorder", value.rayTracingInvocationReorder);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_INVOCATION_REORDER_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRayTracingInvocationReorderFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceRayTracingInvocationReorderFeaturesNV";
            generatedEmitAuto(dst, section, "rayTracingInvocationReorder", value.rayTracingInvocationReorder);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_INVOCATION_REORDER_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRayTracingInvocationReorderPropertiesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceRayTracingInvocationReorderPropertiesEXT";
            generatedEmitAuto(dst, section, "maxShaderBindingTableRecordIndex", value.maxShaderBindingTableRecordIndex);
            generatedEmitAuto(dst, section, "rayTracingInvocationReorderReorderingHint", value.rayTracingInvocationReorderReorderingHint);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_INVOCATION_REORDER_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRayTracingInvocationReorderPropertiesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceRayTracingInvocationReorderPropertiesNV";
            generatedEmitAuto(dst, section, "rayTracingInvocationReorderReorderingHint", value.rayTracingInvocationReorderReorderingHint);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_LINEAR_SWEPT_SPHERES_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRayTracingLinearSweptSpheresFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceRayTracingLinearSweptSpheresFeaturesNV";
            generatedEmitAuto(dst, section, "linearSweptSpheres", value.linearSweptSpheres);
            generatedEmitAuto(dst, section, "spheres", value.spheres);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_MAINTENANCE_1_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRayTracingMaintenance1FeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceRayTracingMaintenance1FeaturesKHR";
            generatedEmitAuto(dst, section, "rayTracingMaintenance1", value.rayTracingMaintenance1);
            generatedEmitAuto(dst, section, "rayTracingPipelineTraceRaysIndirect2", value.rayTracingPipelineTraceRaysIndirect2);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_MOTION_BLUR_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRayTracingMotionBlurFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceRayTracingMotionBlurFeaturesNV";
            generatedEmitAuto(dst, section, "rayTracingMotionBlur", value.rayTracingMotionBlur);
            generatedEmitAuto(dst, section, "rayTracingMotionBlurPipelineTraceRaysIndirect", value.rayTracingMotionBlurPipelineTraceRaysIndirect);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_PIPELINE_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRayTracingPipelineFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceRayTracingPipelineFeaturesKHR";
            generatedEmitAuto(dst, section, "rayTracingPipeline", value.rayTracingPipeline);
            generatedEmitAuto(dst, section, "rayTracingPipelineShaderGroupHandleCaptureReplay", value.rayTracingPipelineShaderGroupHandleCaptureReplay);
            generatedEmitAuto(dst, section, "rayTracingPipelineShaderGroupHandleCaptureReplayMixed", value.rayTracingPipelineShaderGroupHandleCaptureReplayMixed);
            generatedEmitAuto(dst, section, "rayTracingPipelineTraceRaysIndirect", value.rayTracingPipelineTraceRaysIndirect);
            generatedEmitAuto(dst, section, "rayTraversalPrimitiveCulling", value.rayTraversalPrimitiveCulling);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_POSITION_FETCH_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRayTracingPositionFetchFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceRayTracingPositionFetchFeaturesKHR";
            generatedEmitAuto(dst, section, "rayTracingPositionFetch", value.rayTracingPositionFetch);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRayTracingPropertiesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceRayTracingPropertiesNV";
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
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceRayTracingValidationFeaturesNV";
            generatedEmitAuto(dst, section, "rayTracingValidation", value.rayTracingValidation);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RELAXED_LINE_RASTERIZATION_FEATURES_IMG: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRelaxedLineRasterizationFeaturesIMG*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceRelaxedLineRasterizationFeaturesIMG";
            generatedEmitAuto(dst, section, "relaxedLineRasterization", value.relaxedLineRasterization);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RENDER_PASS_STRIPED_FEATURES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRenderPassStripedFeaturesARM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceRenderPassStripedFeaturesARM";
            generatedEmitAuto(dst, section, "renderPassStriped", value.renderPassStriped);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RENDER_PASS_STRIPED_PROPERTIES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRenderPassStripedPropertiesARM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceRenderPassStripedPropertiesARM";
            generatedEmitAuto(dst, section, "maxRenderPassStripes", value.maxRenderPassStripes);
            generatedEmitAuto(dst, section, "renderPassStripeGranularity", value.renderPassStripeGranularity);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_REPRESENTATIVE_FRAGMENT_TEST_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRepresentativeFragmentTestFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceRepresentativeFragmentTestFeaturesNV";
            generatedEmitAuto(dst, section, "representativeFragmentTest", value.representativeFragmentTest);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ROBUSTNESS_2_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRobustness2FeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceRobustness2FeaturesKHR";
            generatedEmitAuto(dst, section, "nullDescriptor", value.nullDescriptor);
            generatedEmitAuto(dst, section, "robustBufferAccess2", value.robustBufferAccess2);
            generatedEmitAuto(dst, section, "robustImageAccess2", value.robustImageAccess2);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ROBUSTNESS_2_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceRobustness2PropertiesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceRobustness2PropertiesKHR";
            generatedEmitAuto(dst, section, "robustStorageBufferAccessSizeAlignment", value.robustStorageBufferAccessSizeAlignment);
            generatedEmitAuto(dst, section, "robustUniformBufferAccessSizeAlignment", value.robustUniformBufferAccessSizeAlignment);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SCHEDULING_CONTROLS_FEATURES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceSchedulingControlsFeaturesARM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceSchedulingControlsFeaturesARM";
            generatedEmitAuto(dst, section, "schedulingControls", value.schedulingControls);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SCHEDULING_CONTROLS_PROPERTIES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceSchedulingControlsPropertiesARM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceSchedulingControlsPropertiesARM";
            generatedEmitAuto(dst, section, "schedulingControlsFlags", value.schedulingControlsFlags);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_64_BIT_INDEXING_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShader64BitIndexingFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShader64BitIndexingFeaturesEXT";
            generatedEmitAuto(dst, section, "shader64BitIndexing", value.shader64BitIndexing);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_ATOMIC_FLOAT16_VECTOR_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderAtomicFloat16VectorFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderAtomicFloat16VectorFeaturesNV";
            generatedEmitAuto(dst, section, "shaderFloat16VectorAtomics", value.shaderFloat16VectorAtomics);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_ATOMIC_FLOAT_2_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderAtomicFloat2FeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderAtomicFloat2FeaturesEXT";
            generatedEmitAuto(dst, section, "shaderBufferFloat16AtomicAdd", value.shaderBufferFloat16AtomicAdd);
            generatedEmitAuto(dst, section, "shaderBufferFloat16AtomicMinMax", value.shaderBufferFloat16AtomicMinMax);
            generatedEmitAuto(dst, section, "shaderBufferFloat16Atomics", value.shaderBufferFloat16Atomics);
            generatedEmitAuto(dst, section, "shaderBufferFloat32AtomicMinMax", value.shaderBufferFloat32AtomicMinMax);
            generatedEmitAuto(dst, section, "shaderBufferFloat64AtomicMinMax", value.shaderBufferFloat64AtomicMinMax);
            generatedEmitAuto(dst, section, "shaderImageFloat32AtomicMinMax", value.shaderImageFloat32AtomicMinMax);
            generatedEmitAuto(dst, section, "shaderSharedFloat16AtomicAdd", value.shaderSharedFloat16AtomicAdd);
            generatedEmitAuto(dst, section, "shaderSharedFloat16AtomicMinMax", value.shaderSharedFloat16AtomicMinMax);
            generatedEmitAuto(dst, section, "shaderSharedFloat16Atomics", value.shaderSharedFloat16Atomics);
            generatedEmitAuto(dst, section, "shaderSharedFloat32AtomicMinMax", value.shaderSharedFloat32AtomicMinMax);
            generatedEmitAuto(dst, section, "shaderSharedFloat64AtomicMinMax", value.shaderSharedFloat64AtomicMinMax);
            generatedEmitAuto(dst, section, "sparseImageFloat32AtomicMinMax", value.sparseImageFloat32AtomicMinMax);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_ATOMIC_FLOAT_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderAtomicFloatFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderAtomicFloatFeaturesEXT";
            generatedEmitAuto(dst, section, "shaderBufferFloat32AtomicAdd", value.shaderBufferFloat32AtomicAdd);
            generatedEmitAuto(dst, section, "shaderBufferFloat32Atomics", value.shaderBufferFloat32Atomics);
            generatedEmitAuto(dst, section, "shaderBufferFloat64AtomicAdd", value.shaderBufferFloat64AtomicAdd);
            generatedEmitAuto(dst, section, "shaderBufferFloat64Atomics", value.shaderBufferFloat64Atomics);
            generatedEmitAuto(dst, section, "shaderImageFloat32AtomicAdd", value.shaderImageFloat32AtomicAdd);
            generatedEmitAuto(dst, section, "shaderImageFloat32Atomics", value.shaderImageFloat32Atomics);
            generatedEmitAuto(dst, section, "shaderSharedFloat32AtomicAdd", value.shaderSharedFloat32AtomicAdd);
            generatedEmitAuto(dst, section, "shaderSharedFloat32Atomics", value.shaderSharedFloat32Atomics);
            generatedEmitAuto(dst, section, "shaderSharedFloat64AtomicAdd", value.shaderSharedFloat64AtomicAdd);
            generatedEmitAuto(dst, section, "shaderSharedFloat64Atomics", value.shaderSharedFloat64Atomics);
            generatedEmitAuto(dst, section, "sparseImageFloat32AtomicAdd", value.sparseImageFloat32AtomicAdd);
            generatedEmitAuto(dst, section, "sparseImageFloat32Atomics", value.sparseImageFloat32Atomics);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_BFLOAT16_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderBfloat16FeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderBfloat16FeaturesKHR";
            generatedEmitAuto(dst, section, "shaderBFloat16CooperativeMatrix", value.shaderBFloat16CooperativeMatrix);
            generatedEmitAuto(dst, section, "shaderBFloat16DotProduct", value.shaderBFloat16DotProduct);
            generatedEmitAuto(dst, section, "shaderBFloat16Type", value.shaderBFloat16Type);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_CLOCK_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderClockFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderClockFeaturesKHR";
            generatedEmitAuto(dst, section, "shaderDeviceClock", value.shaderDeviceClock);
            generatedEmitAuto(dst, section, "shaderSubgroupClock", value.shaderSubgroupClock);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_CORE_BUILTINS_FEATURES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderCoreBuiltinsFeaturesARM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderCoreBuiltinsFeaturesARM";
            generatedEmitAuto(dst, section, "shaderCoreBuiltins", value.shaderCoreBuiltins);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_CORE_BUILTINS_PROPERTIES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderCoreBuiltinsPropertiesARM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderCoreBuiltinsPropertiesARM";
            generatedEmitAuto(dst, section, "shaderCoreCount", value.shaderCoreCount);
            generatedEmitAuto(dst, section, "shaderCoreMask", value.shaderCoreMask);
            generatedEmitAuto(dst, section, "shaderWarpsPerCore", value.shaderWarpsPerCore);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_CORE_PROPERTIES_2_AMD: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderCoreProperties2AMD*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderCoreProperties2AMD";
            generatedEmitAuto(dst, section, "activeComputeUnitCount", value.activeComputeUnitCount);
            generatedEmitAuto(dst, section, "shaderCoreFeatures", value.shaderCoreFeatures);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_CORE_PROPERTIES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderCorePropertiesARM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderCorePropertiesARM";
            generatedEmitAuto(dst, section, "fmaRate", value.fmaRate);
            generatedEmitAuto(dst, section, "pixelRate", value.pixelRate);
            generatedEmitAuto(dst, section, "texelRate", value.texelRate);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_EARLY_AND_LATE_FRAGMENT_TESTS_FEATURES_AMD: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderEarlyAndLateFragmentTestsFeaturesAMD*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderEarlyAndLateFragmentTestsFeaturesAMD";
            generatedEmitAuto(dst, section, "shaderEarlyAndLateFragmentTests", value.shaderEarlyAndLateFragmentTests);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_ENQUEUE_PROPERTIES_AMDX: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderEnqueuePropertiesAMDX*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderEnqueuePropertiesAMDX";
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
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderExpectAssumeFeaturesKHR";
            generatedEmitAuto(dst, section, "shaderExpectAssume", value.shaderExpectAssume);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_FLOAT8_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderFloat8FeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderFloat8FeaturesEXT";
            generatedEmitAuto(dst, section, "shaderFloat8", value.shaderFloat8);
            generatedEmitAuto(dst, section, "shaderFloat8CooperativeMatrix", value.shaderFloat8CooperativeMatrix);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_FLOAT_CONTROLS_2_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderFloatControls2FeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderFloatControls2FeaturesKHR";
            generatedEmitAuto(dst, section, "shaderFloatControls2", value.shaderFloatControls2);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_FMA_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderFmaFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderFmaFeaturesKHR";
            generatedEmitAuto(dst, section, "shaderFmaFloat16", value.shaderFmaFloat16);
            generatedEmitAuto(dst, section, "shaderFmaFloat32", value.shaderFmaFloat32);
            generatedEmitAuto(dst, section, "shaderFmaFloat64", value.shaderFmaFloat64);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_IMAGE_ATOMIC_INT64_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderImageAtomicInt64FeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderImageAtomicInt64FeaturesEXT";
            generatedEmitAuto(dst, section, "shaderImageInt64Atomics", value.shaderImageInt64Atomics);
            generatedEmitAuto(dst, section, "sparseImageInt64Atomics", value.sparseImageInt64Atomics);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_IMAGE_FOOTPRINT_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderImageFootprintFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderImageFootprintFeaturesNV";
            generatedEmitAuto(dst, section, "imageFootprint", value.imageFootprint);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_INTEGER_FUNCTIONS_2_FEATURES_INTEL: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderIntegerFunctions2FeaturesINTEL*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderIntegerFunctions2FeaturesINTEL";
            generatedEmitAuto(dst, section, "shaderIntegerFunctions2", value.shaderIntegerFunctions2);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_MODULE_IDENTIFIER_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderModuleIdentifierFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderModuleIdentifierFeaturesEXT";
            generatedEmitAuto(dst, section, "shaderModuleIdentifier", value.shaderModuleIdentifier);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_MODULE_IDENTIFIER_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderModuleIdentifierPropertiesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderModuleIdentifierPropertiesEXT";
            generatedEmitAuto(dst, section, "shaderModuleIdentifierAlgorithmUUID", value.shaderModuleIdentifierAlgorithmUUID);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_OBJECT_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderObjectFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderObjectFeaturesEXT";
            generatedEmitAuto(dst, section, "shaderObject", value.shaderObject);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_OBJECT_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderObjectPropertiesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderObjectPropertiesEXT";
            generatedEmitAuto(dst, section, "shaderBinaryUUID", value.shaderBinaryUUID);
            generatedEmitAuto(dst, section, "shaderBinaryVersion", value.shaderBinaryVersion);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_QUAD_CONTROL_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderQuadControlFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderQuadControlFeaturesKHR";
            generatedEmitAuto(dst, section, "shaderQuadControl", value.shaderQuadControl);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_RELAXED_EXTENDED_INSTRUCTION_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderRelaxedExtendedInstructionFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderRelaxedExtendedInstructionFeaturesKHR";
            generatedEmitAuto(dst, section, "shaderRelaxedExtendedInstruction", value.shaderRelaxedExtendedInstruction);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_REPLICATED_COMPOSITES_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderReplicatedCompositesFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderReplicatedCompositesFeaturesEXT";
            generatedEmitAuto(dst, section, "shaderReplicatedComposites", value.shaderReplicatedComposites);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SM_BUILTINS_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderSMBuiltinsFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderSMBuiltinsFeaturesNV";
            generatedEmitAuto(dst, section, "shaderSMBuiltins", value.shaderSMBuiltins);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SM_BUILTINS_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderSMBuiltinsPropertiesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderSMBuiltinsPropertiesNV";
            generatedEmitAuto(dst, section, "shaderSMCount", value.shaderSMCount);
            generatedEmitAuto(dst, section, "shaderWarpsPerSM", value.shaderWarpsPerSM);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SUBGROUP_ROTATE_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderSubgroupRotateFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderSubgroupRotateFeaturesKHR";
            generatedEmitAuto(dst, section, "shaderSubgroupRotate", value.shaderSubgroupRotate);
            generatedEmitAuto(dst, section, "shaderSubgroupRotateClustered", value.shaderSubgroupRotateClustered);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SUBGROUP_UNIFORM_CONTROL_FLOW_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderSubgroupUniformControlFlowFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderSubgroupUniformControlFlowFeaturesKHR";
            generatedEmitAuto(dst, section, "shaderSubgroupUniformControlFlow", value.shaderSubgroupUniformControlFlow);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_TILE_IMAGE_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderTileImagePropertiesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderTileImagePropertiesEXT";
            generatedEmitAuto(dst, section, "shaderTileImageCoherentReadAccelerated", value.shaderTileImageCoherentReadAccelerated);
            generatedEmitAuto(dst, section, "shaderTileImageReadFromHelperInvocation", value.shaderTileImageReadFromHelperInvocation);
            generatedEmitAuto(dst, section, "shaderTileImageReadSampleFromPixelRateInvocation", value.shaderTileImageReadSampleFromPixelRateInvocation);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_UNTYPED_POINTERS_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderUntypedPointersFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderUntypedPointersFeaturesKHR";
            generatedEmitAuto(dst, section, "shaderUntypedPointers", value.shaderUntypedPointers);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADING_RATE_IMAGE_FEATURES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShadingRateImageFeaturesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShadingRateImageFeaturesNV";
            generatedEmitAuto(dst, section, "shadingRateCoarseSampleOrder", value.shadingRateCoarseSampleOrder);
            generatedEmitAuto(dst, section, "shadingRateImage", value.shadingRateImage);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADING_RATE_IMAGE_PROPERTIES_NV: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShadingRateImagePropertiesNV*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShadingRateImagePropertiesNV";
            generatedEmitAuto(dst, section, "shadingRateMaxCoarseSamples", value.shadingRateMaxCoarseSamples);
            generatedEmitAuto(dst, section, "shadingRatePaletteSize", value.shadingRatePaletteSize);
            generatedEmitAuto(dst, section, "shadingRateTexelSize", value.shadingRateTexelSize);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SUBPASS_MERGE_FEEDBACK_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceSubpassMergeFeedbackFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceSubpassMergeFeedbackFeaturesEXT";
            generatedEmitAuto(dst, section, "subpassMergeFeedback", value.subpassMergeFeedback);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SUBPASS_SHADING_FEATURES_HUAWEI: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceSubpassShadingFeaturesHUAWEI*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceSubpassShadingFeaturesHUAWEI";
            generatedEmitAuto(dst, section, "subpassShading", value.subpassShading);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SUBPASS_SHADING_PROPERTIES_HUAWEI: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceSubpassShadingPropertiesHUAWEI*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceSubpassShadingPropertiesHUAWEI";
            generatedEmitAuto(dst, section, "maxSubpassShadingWorkgroupSizeAspectRatio", value.maxSubpassShadingWorkgroupSizeAspectRatio);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SWAPCHAIN_MAINTENANCE_1_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceSwapchainMaintenance1FeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceSwapchainMaintenance1FeaturesKHR";
            generatedEmitAuto(dst, section, "swapchainMaintenance1", value.swapchainMaintenance1);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TENSOR_FEATURES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceTensorFeaturesARM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceTensorFeaturesARM";
            generatedEmitAuto(dst, section, "descriptorBindingStorageTensorUpdateAfterBind", value.descriptorBindingStorageTensorUpdateAfterBind);
            generatedEmitAuto(dst, section, "shaderStorageTensorArrayDynamicIndexing", value.shaderStorageTensorArrayDynamicIndexing);
            generatedEmitAuto(dst, section, "shaderStorageTensorArrayNonUniformIndexing", value.shaderStorageTensorArrayNonUniformIndexing);
            generatedEmitAuto(dst, section, "shaderTensorAccess", value.shaderTensorAccess);
            generatedEmitAuto(dst, section, "tensorNonPacked", value.tensorNonPacked);
            generatedEmitAuto(dst, section, "tensors", value.tensors);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TENSOR_PROPERTIES_ARM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceTensorPropertiesARM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceTensorPropertiesARM";
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
            generatedEmitAuto(dst, section, "shaderStorageTensorArrayNonUniformIndexingNative", value.shaderStorageTensorArrayNonUniformIndexingNative);
            generatedEmitAuto(dst, section, "shaderTensorSupportedStages", value.shaderTensorSupportedStages);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TEXEL_BUFFER_ALIGNMENT_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceTexelBufferAlignmentFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceTexelBufferAlignmentFeaturesEXT";
            generatedEmitAuto(dst, section, "texelBufferAlignment", value.texelBufferAlignment);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TILE_MEMORY_HEAP_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceTileMemoryHeapFeaturesQCOM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceTileMemoryHeapFeaturesQCOM";
            generatedEmitAuto(dst, section, "tileMemoryHeap", value.tileMemoryHeap);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TILE_MEMORY_HEAP_PROPERTIES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceTileMemoryHeapPropertiesQCOM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceTileMemoryHeapPropertiesQCOM";
            generatedEmitAuto(dst, section, "queueSubmitBoundary", value.queueSubmitBoundary);
            generatedEmitAuto(dst, section, "tileBufferTransfers", value.tileBufferTransfers);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TILE_PROPERTIES_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceTilePropertiesFeaturesQCOM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceTilePropertiesFeaturesQCOM";
            generatedEmitAuto(dst, section, "tileProperties", value.tileProperties);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TILE_SHADING_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceTileShadingFeaturesQCOM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceTileShadingFeaturesQCOM";
            generatedEmitAuto(dst, section, "tileShading", value.tileShading);
            generatedEmitAuto(dst, section, "tileShadingAnisotropicApron", value.tileShadingAnisotropicApron);
            generatedEmitAuto(dst, section, "tileShadingApron", value.tileShadingApron);
            generatedEmitAuto(dst, section, "tileShadingAtomicOps", value.tileShadingAtomicOps);
            generatedEmitAuto(dst, section, "tileShadingColorAttachments", value.tileShadingColorAttachments);
            generatedEmitAuto(dst, section, "tileShadingDepthAttachments", value.tileShadingDepthAttachments);
            generatedEmitAuto(dst, section, "tileShadingDispatchTile", value.tileShadingDispatchTile);
            generatedEmitAuto(dst, section, "tileShadingFragmentStage", value.tileShadingFragmentStage);
            generatedEmitAuto(dst, section, "tileShadingImageProcessing", value.tileShadingImageProcessing);
            generatedEmitAuto(dst, section, "tileShadingInputAttachments", value.tileShadingInputAttachments);
            generatedEmitAuto(dst, section, "tileShadingPerTileDispatch", value.tileShadingPerTileDispatch);
            generatedEmitAuto(dst, section, "tileShadingPerTileDraw", value.tileShadingPerTileDraw);
            generatedEmitAuto(dst, section, "tileShadingSampledAttachments", value.tileShadingSampledAttachments);
            generatedEmitAuto(dst, section, "tileShadingStencilAttachments", value.tileShadingStencilAttachments);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TILE_SHADING_PROPERTIES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceTileShadingPropertiesQCOM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceTileShadingPropertiesQCOM";
            generatedEmitAuto(dst, section, "maxApronSize", value.maxApronSize);
            generatedEmitAuto(dst, section, "maxTileShadingRate", value.maxTileShadingRate);
            generatedEmitAuto(dst, section, "preferNonCoherent", value.preferNonCoherent);
            generatedEmitAuto(dst, section, "tileGranularity", value.tileGranularity);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TRANSFORM_FEEDBACK_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceTransformFeedbackFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceTransformFeedbackFeaturesEXT";
            generatedEmitAuto(dst, section, "geometryStreams", value.geometryStreams);
            generatedEmitAuto(dst, section, "transformFeedback", value.transformFeedback);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_UNIFIED_IMAGE_LAYOUTS_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceUnifiedImageLayoutsFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceUnifiedImageLayoutsFeaturesKHR";
            generatedEmitAuto(dst, section, "unifiedImageLayouts", value.unifiedImageLayouts);
            generatedEmitAuto(dst, section, "unifiedImageLayoutsVideo", value.unifiedImageLayoutsVideo);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VERTEX_ATTRIBUTE_DIVISOR_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVertexAttributeDivisorFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceVertexAttributeDivisorFeaturesKHR";
            generatedEmitAuto(dst, section, "vertexAttributeInstanceRateDivisor", value.vertexAttributeInstanceRateDivisor);
            generatedEmitAuto(dst, section, "vertexAttributeInstanceRateZeroDivisor", value.vertexAttributeInstanceRateZeroDivisor);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VERTEX_ATTRIBUTE_DIVISOR_PROPERTIES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVertexAttributeDivisorPropertiesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceVertexAttributeDivisorPropertiesEXT";
            generatedEmitAuto(dst, section, "maxVertexAttribDivisor", value.maxVertexAttribDivisor);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VERTEX_ATTRIBUTE_DIVISOR_PROPERTIES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVertexAttributeDivisorPropertiesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceVertexAttributeDivisorPropertiesKHR";
            generatedEmitAuto(dst, section, "maxVertexAttribDivisor", value.maxVertexAttribDivisor);
            generatedEmitAuto(dst, section, "supportsNonZeroFirstInstance", value.supportsNonZeroFirstInstance);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VERTEX_ATTRIBUTE_ROBUSTNESS_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVertexAttributeRobustnessFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceVertexAttributeRobustnessFeaturesEXT";
            generatedEmitAuto(dst, section, "vertexAttributeRobustness", value.vertexAttributeRobustness);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VERTEX_INPUT_DYNAMIC_STATE_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVertexInputDynamicStateFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceVertexInputDynamicStateFeaturesEXT";
            generatedEmitAuto(dst, section, "vertexInputDynamicState", value.vertexInputDynamicState);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_DECODE_VP9_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVideoDecodeVP9FeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceVideoDecodeVP9FeaturesKHR";
            generatedEmitAuto(dst, section, "videoDecodeVP9", value.videoDecodeVP9);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_ENCODE_AV1_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVideoEncodeAV1FeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceVideoEncodeAV1FeaturesKHR";
            generatedEmitAuto(dst, section, "videoEncodeAV1", value.videoEncodeAV1);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_ENCODE_INTRA_REFRESH_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVideoEncodeIntraRefreshFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceVideoEncodeIntraRefreshFeaturesKHR";
            generatedEmitAuto(dst, section, "videoEncodeIntraRefresh", value.videoEncodeIntraRefresh);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_ENCODE_QUANTIZATION_MAP_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVideoEncodeQuantizationMapFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceVideoEncodeQuantizationMapFeaturesKHR";
            generatedEmitAuto(dst, section, "videoEncodeQuantizationMap", value.videoEncodeQuantizationMap);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_ENCODE_RGB_CONVERSION_FEATURES_VALVE: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVideoEncodeRgbConversionFeaturesVALVE*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceVideoEncodeRgbConversionFeaturesVALVE";
            generatedEmitAuto(dst, section, "videoEncodeRgbConversion", value.videoEncodeRgbConversion);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_MAINTENANCE_1_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVideoMaintenance1FeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceVideoMaintenance1FeaturesKHR";
            generatedEmitAuto(dst, section, "videoMaintenance1", value.videoMaintenance1);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_MAINTENANCE_2_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVideoMaintenance2FeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceVideoMaintenance2FeaturesKHR";
            generatedEmitAuto(dst, section, "videoMaintenance2", value.videoMaintenance2);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_WORKGROUP_MEMORY_EXPLICIT_LAYOUT_FEATURES_KHR: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceWorkgroupMemoryExplicitLayoutFeaturesKHR*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceWorkgroupMemoryExplicitLayoutFeaturesKHR";
            generatedEmitAuto(dst, section, "workgroupMemoryExplicitLayout", value.workgroupMemoryExplicitLayout);
            generatedEmitAuto(dst, section, "workgroupMemoryExplicitLayout16BitAccess", value.workgroupMemoryExplicitLayout16BitAccess);
            generatedEmitAuto(dst, section, "workgroupMemoryExplicitLayout8BitAccess", value.workgroupMemoryExplicitLayout8BitAccess);
            generatedEmitAuto(dst, section, "workgroupMemoryExplicitLayoutScalarBlockLayout", value.workgroupMemoryExplicitLayoutScalarBlockLayout);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_YCBCR_DEGAMMA_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceYcbcrDegammaFeaturesQCOM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceYcbcrDegammaFeaturesQCOM";
            generatedEmitAuto(dst, section, "ycbcrDegamma", value.ycbcrDegamma);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_YCBCR_IMAGE_ARRAYS_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceYcbcrImageArraysFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceYcbcrImageArraysFeaturesEXT";
            generatedEmitAuto(dst, section, "ycbcrImageArrays", value.ycbcrImageArrays);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ZERO_INITIALIZE_DEVICE_MEMORY_FEATURES_EXT: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceZeroInitializeDeviceMemoryFeaturesEXT*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceZeroInitializeDeviceMemoryFeaturesEXT";
            generatedEmitAuto(dst, section, "zeroInitializeDeviceMemory", value.zeroInitializeDeviceMemory);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SAMPLER_FILTER_MINMAX_PROPERTIES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceSamplerFilterMinmaxProperties*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceSamplerFilterMinmaxProperties";
            generatedEmitAuto(dst, section, "filterMinmaxSingleComponentFormats", value.filterMinmaxSingleComponentFormats);
            generatedEmitAuto(dst, section, "filterMinmaxImageComponentMapping", value.filterMinmaxImageComponentMapping);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INLINE_UNIFORM_BLOCK_PROPERTIES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceInlineUniformBlockProperties*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceInlineUniformBlockProperties";
            generatedEmitAuto(dst, section, "maxInlineUniformBlockSize", value.maxInlineUniformBlockSize);
            generatedEmitAuto(dst, section, "maxPerStageDescriptorInlineUniformBlocks", value.maxPerStageDescriptorInlineUniformBlocks);
            generatedEmitAuto(dst, section, "maxPerStageDescriptorUpdateAfterBindInlineUniformBlocks", value.maxPerStageDescriptorUpdateAfterBindInlineUniformBlocks);
            generatedEmitAuto(dst, section, "maxDescriptorSetInlineUniformBlocks", value.maxDescriptorSetInlineUniformBlocks);
            generatedEmitAuto(dst, section, "maxDescriptorSetUpdateAfterBindInlineUniformBlocks", value.maxDescriptorSetUpdateAfterBindInlineUniformBlocks);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_INDEXING_PROPERTIES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDescriptorIndexingProperties*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDescriptorIndexingProperties";
            generatedEmitAuto(dst, section, "maxUpdateAfterBindDescriptorsInAllPools", value.maxUpdateAfterBindDescriptorsInAllPools);
            generatedEmitAuto(dst, section, "shaderUniformBufferArrayNonUniformIndexingNative", value.shaderUniformBufferArrayNonUniformIndexingNative);
            generatedEmitAuto(dst, section, "shaderSampledImageArrayNonUniformIndexingNative", value.shaderSampledImageArrayNonUniformIndexingNative);
            generatedEmitAuto(dst, section, "shaderStorageBufferArrayNonUniformIndexingNative", value.shaderStorageBufferArrayNonUniformIndexingNative);
            generatedEmitAuto(dst, section, "shaderStorageImageArrayNonUniformIndexingNative", value.shaderStorageImageArrayNonUniformIndexingNative);
            generatedEmitAuto(dst, section, "shaderInputAttachmentArrayNonUniformIndexingNative", value.shaderInputAttachmentArrayNonUniformIndexingNative);
            generatedEmitAuto(dst, section, "robustBufferAccessUpdateAfterBind", value.robustBufferAccessUpdateAfterBind);
            generatedEmitAuto(dst, section, "quadDivergentImplicitLod", value.quadDivergentImplicitLod);
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
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceMultiviewProperties";
            generatedEmitAuto(dst, section, "maxMultiviewViewCount", value.maxMultiviewViewCount);
            generatedEmitAuto(dst, section, "maxMultiviewInstanceIndex", value.maxMultiviewInstanceIndex);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_POINT_CLIPPING_PROPERTIES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePointClippingProperties*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevicePointClippingProperties";
            generatedEmitAuto(dst, section, "pointClippingBehavior", value.pointClippingBehavior);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FLOAT_CONTROLS_PROPERTIES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceFloatControlsProperties*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceFloatControlsProperties";
            generatedEmitAuto(dst, section, "denormBehaviorIndependence", value.denormBehaviorIndependence);
            generatedEmitAuto(dst, section, "roundingModeIndependence", value.roundingModeIndependence);
            generatedEmitAuto(dst, section, "shaderSignedZeroInfNanPreserveFloat16", value.shaderSignedZeroInfNanPreserveFloat16);
            generatedEmitAuto(dst, section, "shaderSignedZeroInfNanPreserveFloat32", value.shaderSignedZeroInfNanPreserveFloat32);
            generatedEmitAuto(dst, section, "shaderSignedZeroInfNanPreserveFloat64", value.shaderSignedZeroInfNanPreserveFloat64);
            generatedEmitAuto(dst, section, "shaderDenormPreserveFloat16", value.shaderDenormPreserveFloat16);
            generatedEmitAuto(dst, section, "shaderDenormPreserveFloat32", value.shaderDenormPreserveFloat32);
            generatedEmitAuto(dst, section, "shaderDenormPreserveFloat64", value.shaderDenormPreserveFloat64);
            generatedEmitAuto(dst, section, "shaderDenormFlushToZeroFloat16", value.shaderDenormFlushToZeroFloat16);
            generatedEmitAuto(dst, section, "shaderDenormFlushToZeroFloat32", value.shaderDenormFlushToZeroFloat32);
            generatedEmitAuto(dst, section, "shaderDenormFlushToZeroFloat64", value.shaderDenormFlushToZeroFloat64);
            generatedEmitAuto(dst, section, "shaderRoundingModeRTEFloat16", value.shaderRoundingModeRTEFloat16);
            generatedEmitAuto(dst, section, "shaderRoundingModeRTEFloat32", value.shaderRoundingModeRTEFloat32);
            generatedEmitAuto(dst, section, "shaderRoundingModeRTEFloat64", value.shaderRoundingModeRTEFloat64);
            generatedEmitAuto(dst, section, "shaderRoundingModeRTZFloat16", value.shaderRoundingModeRTZFloat16);
            generatedEmitAuto(dst, section, "shaderRoundingModeRTZFloat32", value.shaderRoundingModeRTZFloat32);
            generatedEmitAuto(dst, section, "shaderRoundingModeRTZFloat64", value.shaderRoundingModeRTZFloat64);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_STENCIL_RESOLVE_PROPERTIES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDepthStencilResolveProperties*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDepthStencilResolveProperties";
            generatedEmitAuto(dst, section, "supportedDepthResolveModes", value.supportedDepthResolveModes);
            generatedEmitAuto(dst, section, "supportedStencilResolveModes", value.supportedStencilResolveModes);
            generatedEmitAuto(dst, section, "independentResolveNone", value.independentResolveNone);
            generatedEmitAuto(dst, section, "independentResolve", value.independentResolve);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_INTEGER_DOT_PRODUCT_PROPERTIES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderIntegerDotProductProperties*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderIntegerDotProductProperties";
            generatedEmitAuto(dst, section, "integerDotProduct8BitUnsignedAccelerated", value.integerDotProduct8BitUnsignedAccelerated);
            generatedEmitAuto(dst, section, "integerDotProduct8BitSignedAccelerated", value.integerDotProduct8BitSignedAccelerated);
            generatedEmitAuto(dst, section, "integerDotProduct8BitMixedSignednessAccelerated", value.integerDotProduct8BitMixedSignednessAccelerated);
            generatedEmitAuto(dst, section, "integerDotProduct4x8BitPackedUnsignedAccelerated", value.integerDotProduct4x8BitPackedUnsignedAccelerated);
            generatedEmitAuto(dst, section, "integerDotProduct4x8BitPackedSignedAccelerated", value.integerDotProduct4x8BitPackedSignedAccelerated);
            generatedEmitAuto(dst, section, "integerDotProduct4x8BitPackedMixedSignednessAccelerated", value.integerDotProduct4x8BitPackedMixedSignednessAccelerated);
            generatedEmitAuto(dst, section, "integerDotProduct16BitUnsignedAccelerated", value.integerDotProduct16BitUnsignedAccelerated);
            generatedEmitAuto(dst, section, "integerDotProduct16BitSignedAccelerated", value.integerDotProduct16BitSignedAccelerated);
            generatedEmitAuto(dst, section, "integerDotProduct16BitMixedSignednessAccelerated", value.integerDotProduct16BitMixedSignednessAccelerated);
            generatedEmitAuto(dst, section, "integerDotProduct32BitUnsignedAccelerated", value.integerDotProduct32BitUnsignedAccelerated);
            generatedEmitAuto(dst, section, "integerDotProduct32BitSignedAccelerated", value.integerDotProduct32BitSignedAccelerated);
            generatedEmitAuto(dst, section, "integerDotProduct32BitMixedSignednessAccelerated", value.integerDotProduct32BitMixedSignednessAccelerated);
            generatedEmitAuto(dst, section, "integerDotProduct64BitUnsignedAccelerated", value.integerDotProduct64BitUnsignedAccelerated);
            generatedEmitAuto(dst, section, "integerDotProduct64BitSignedAccelerated", value.integerDotProduct64BitSignedAccelerated);
            generatedEmitAuto(dst, section, "integerDotProduct64BitMixedSignednessAccelerated", value.integerDotProduct64BitMixedSignednessAccelerated);
            generatedEmitAuto(dst, section, "integerDotProductAccumulatingSaturating8BitUnsignedAccelerated", value.integerDotProductAccumulatingSaturating8BitUnsignedAccelerated);
            generatedEmitAuto(dst, section, "integerDotProductAccumulatingSaturating8BitSignedAccelerated", value.integerDotProductAccumulatingSaturating8BitSignedAccelerated);
            generatedEmitAuto(dst, section, "integerDotProductAccumulatingSaturating8BitMixedSignednessAccelerated", value.integerDotProductAccumulatingSaturating8BitMixedSignednessAccelerated);
            generatedEmitAuto(dst, section, "integerDotProductAccumulatingSaturating4x8BitPackedUnsignedAccelerated", value.integerDotProductAccumulatingSaturating4x8BitPackedUnsignedAccelerated);
            generatedEmitAuto(dst, section, "integerDotProductAccumulatingSaturating4x8BitPackedSignedAccelerated", value.integerDotProductAccumulatingSaturating4x8BitPackedSignedAccelerated);
            generatedEmitAuto(dst, section, "integerDotProductAccumulatingSaturating4x8BitPackedMixedSignednessAccelerated", value.integerDotProductAccumulatingSaturating4x8BitPackedMixedSignednessAccelerated);
            generatedEmitAuto(dst, section, "integerDotProductAccumulatingSaturating16BitUnsignedAccelerated", value.integerDotProductAccumulatingSaturating16BitUnsignedAccelerated);
            generatedEmitAuto(dst, section, "integerDotProductAccumulatingSaturating16BitSignedAccelerated", value.integerDotProductAccumulatingSaturating16BitSignedAccelerated);
            generatedEmitAuto(dst, section, "integerDotProductAccumulatingSaturating16BitMixedSignednessAccelerated", value.integerDotProductAccumulatingSaturating16BitMixedSignednessAccelerated);
            generatedEmitAuto(dst, section, "integerDotProductAccumulatingSaturating32BitUnsignedAccelerated", value.integerDotProductAccumulatingSaturating32BitUnsignedAccelerated);
            generatedEmitAuto(dst, section, "integerDotProductAccumulatingSaturating32BitSignedAccelerated", value.integerDotProductAccumulatingSaturating32BitSignedAccelerated);
            generatedEmitAuto(dst, section, "integerDotProductAccumulatingSaturating32BitMixedSignednessAccelerated", value.integerDotProductAccumulatingSaturating32BitMixedSignednessAccelerated);
            generatedEmitAuto(dst, section, "integerDotProductAccumulatingSaturating64BitUnsignedAccelerated", value.integerDotProductAccumulatingSaturating64BitUnsignedAccelerated);
            generatedEmitAuto(dst, section, "integerDotProductAccumulatingSaturating64BitSignedAccelerated", value.integerDotProductAccumulatingSaturating64BitSignedAccelerated);
            generatedEmitAuto(dst, section, "integerDotProductAccumulatingSaturating64BitMixedSignednessAccelerated", value.integerDotProductAccumulatingSaturating64BitMixedSignednessAccelerated);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_4_PROPERTIES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMaintenance4Properties*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceMaintenance4Properties";
            generatedEmitAuto(dst, section, "maxBufferSize", value.maxBufferSize);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INLINE_UNIFORM_BLOCK_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceInlineUniformBlockFeatures*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceInlineUniformBlockFeatures";
            generatedEmitAuto(dst, section, "inlineUniformBlock", value.inlineUniformBlock);
            generatedEmitAuto(dst, section, "descriptorBindingInlineUniformBlockUpdateAfterBind", value.descriptorBindingInlineUniformBlockUpdateAfterBind);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_INDEXING_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDescriptorIndexingFeatures*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDescriptorIndexingFeatures";
            generatedEmitAuto(dst, section, "shaderInputAttachmentArrayDynamicIndexing", value.shaderInputAttachmentArrayDynamicIndexing);
            generatedEmitAuto(dst, section, "shaderUniformTexelBufferArrayDynamicIndexing", value.shaderUniformTexelBufferArrayDynamicIndexing);
            generatedEmitAuto(dst, section, "shaderStorageTexelBufferArrayDynamicIndexing", value.shaderStorageTexelBufferArrayDynamicIndexing);
            generatedEmitAuto(dst, section, "shaderUniformBufferArrayNonUniformIndexing", value.shaderUniformBufferArrayNonUniformIndexing);
            generatedEmitAuto(dst, section, "shaderSampledImageArrayNonUniformIndexing", value.shaderSampledImageArrayNonUniformIndexing);
            generatedEmitAuto(dst, section, "shaderStorageBufferArrayNonUniformIndexing", value.shaderStorageBufferArrayNonUniformIndexing);
            generatedEmitAuto(dst, section, "shaderStorageImageArrayNonUniformIndexing", value.shaderStorageImageArrayNonUniformIndexing);
            generatedEmitAuto(dst, section, "shaderInputAttachmentArrayNonUniformIndexing", value.shaderInputAttachmentArrayNonUniformIndexing);
            generatedEmitAuto(dst, section, "shaderUniformTexelBufferArrayNonUniformIndexing", value.shaderUniformTexelBufferArrayNonUniformIndexing);
            generatedEmitAuto(dst, section, "shaderStorageTexelBufferArrayNonUniformIndexing", value.shaderStorageTexelBufferArrayNonUniformIndexing);
            generatedEmitAuto(dst, section, "descriptorBindingUniformBufferUpdateAfterBind", value.descriptorBindingUniformBufferUpdateAfterBind);
            generatedEmitAuto(dst, section, "descriptorBindingSampledImageUpdateAfterBind", value.descriptorBindingSampledImageUpdateAfterBind);
            generatedEmitAuto(dst, section, "descriptorBindingStorageImageUpdateAfterBind", value.descriptorBindingStorageImageUpdateAfterBind);
            generatedEmitAuto(dst, section, "descriptorBindingStorageBufferUpdateAfterBind", value.descriptorBindingStorageBufferUpdateAfterBind);
            generatedEmitAuto(dst, section, "descriptorBindingUniformTexelBufferUpdateAfterBind", value.descriptorBindingUniformTexelBufferUpdateAfterBind);
            generatedEmitAuto(dst, section, "descriptorBindingStorageTexelBufferUpdateAfterBind", value.descriptorBindingStorageTexelBufferUpdateAfterBind);
            generatedEmitAuto(dst, section, "descriptorBindingUpdateUnusedWhilePending", value.descriptorBindingUpdateUnusedWhilePending);
            generatedEmitAuto(dst, section, "descriptorBindingPartiallyBound", value.descriptorBindingPartiallyBound);
            generatedEmitAuto(dst, section, "descriptorBindingVariableDescriptorCount", value.descriptorBindingVariableDescriptorCount);
            generatedEmitAuto(dst, section, "runtimeDescriptorArray", value.runtimeDescriptorArray);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SUBGROUP_SIZE_CONTROL_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceSubgroupSizeControlFeatures*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceSubgroupSizeControlFeatures";
            generatedEmitAuto(dst, section, "subgroupSizeControl", value.subgroupSizeControl);
            generatedEmitAuto(dst, section, "computeFullSubgroups", value.computeFullSubgroups);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_HOST_QUERY_RESET_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceHostQueryResetFeatures*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceHostQueryResetFeatures";
            generatedEmitAuto(dst, section, "hostQueryReset", value.hostQueryReset);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_DEMOTE_TO_HELPER_INVOCATION_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderDemoteToHelperInvocationFeatures*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderDemoteToHelperInvocationFeatures";
            generatedEmitAuto(dst, section, "shaderDemoteToHelperInvocation", value.shaderDemoteToHelperInvocation);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRIVATE_DATA_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePrivateDataFeatures*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevicePrivateDataFeatures";
            generatedEmitAuto(dst, section, "privateData", value.privateData);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_CREATION_CACHE_CONTROL_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevicePipelineCreationCacheControlFeatures*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevicePipelineCreationCacheControlFeatures";
            generatedEmitAuto(dst, section, "pipelineCreationCacheControl", value.pipelineCreationCacheControl);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DYNAMIC_RENDERING_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceDynamicRenderingFeatures*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceDynamicRenderingFeatures";
            generatedEmitAuto(dst, section, "dynamicRendering", value.dynamicRendering);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTIVIEW_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMultiviewFeatures*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceMultiviewFeatures";
            generatedEmitAuto(dst, section, "multiview", value.multiview);
            generatedEmitAuto(dst, section, "multiviewGeometryShader", value.multiviewGeometryShader);
            generatedEmitAuto(dst, section, "multiviewTessellationShader", value.multiviewTessellationShader);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_FLOAT16_INT8_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderFloat16Int8Features*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderFloat16Int8Features";
            generatedEmitAuto(dst, section, "shaderFloat16", value.shaderFloat16);
            generatedEmitAuto(dst, section, "shaderInt8", value.shaderInt8);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_16BIT_STORAGE_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevice16BitStorageFeatures*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevice16BitStorageFeatures";
            generatedEmitAuto(dst, section, "storageBuffer16BitAccess", value.storageBuffer16BitAccess);
            generatedEmitAuto(dst, section, "uniformAndStorageBuffer16BitAccess", value.uniformAndStorageBuffer16BitAccess);
            generatedEmitAuto(dst, section, "storagePushConstant16", value.storagePushConstant16);
            generatedEmitAuto(dst, section, "storageInputOutput16", value.storageInputOutput16);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGELESS_FRAMEBUFFER_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceImagelessFramebufferFeatures*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceImagelessFramebufferFeatures";
            generatedEmitAuto(dst, section, "imagelessFramebuffer", value.imagelessFramebuffer);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VARIABLE_POINTERS_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVariablePointersFeatures*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceVariablePointersFeatures";
            generatedEmitAuto(dst, section, "variablePointersStorageBuffer", value.variablePointersStorageBuffer);
            generatedEmitAuto(dst, section, "variablePointers", value.variablePointers);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SAMPLER_YCBCR_CONVERSION_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceSamplerYcbcrConversionFeatures*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceSamplerYcbcrConversionFeatures";
            generatedEmitAuto(dst, section, "samplerYcbcrConversion", value.samplerYcbcrConversion);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SUBGROUP_EXTENDED_TYPES_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderSubgroupExtendedTypesFeatures*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderSubgroupExtendedTypesFeatures";
            generatedEmitAuto(dst, section, "shaderSubgroupExtendedTypes", value.shaderSubgroupExtendedTypes);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_8BIT_STORAGE_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDevice8BitStorageFeatures*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDevice8BitStorageFeatures";
            generatedEmitAuto(dst, section, "storageBuffer8BitAccess", value.storageBuffer8BitAccess);
            generatedEmitAuto(dst, section, "uniformAndStorageBuffer8BitAccess", value.uniformAndStorageBuffer8BitAccess);
            generatedEmitAuto(dst, section, "storagePushConstant8", value.storagePushConstant8);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_ATOMIC_INT64_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderAtomicInt64Features*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderAtomicInt64Features";
            generatedEmitAuto(dst, section, "shaderBufferInt64Atomics", value.shaderBufferInt64Atomics);
            generatedEmitAuto(dst, section, "shaderSharedInt64Atomics", value.shaderSharedInt64Atomics);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TIMELINE_SEMAPHORE_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceTimelineSemaphoreFeatures*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceTimelineSemaphoreFeatures";
            generatedEmitAuto(dst, section, "timelineSemaphore", value.timelineSemaphore);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_MEMORY_MODEL_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceVulkanMemoryModelFeatures*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceVulkanMemoryModelFeatures";
            generatedEmitAuto(dst, section, "vulkanMemoryModel", value.vulkanMemoryModel);
            generatedEmitAuto(dst, section, "vulkanMemoryModelDeviceScope", value.vulkanMemoryModelDeviceScope);
            generatedEmitAuto(dst, section, "vulkanMemoryModelAvailabilityVisibilityChains", value.vulkanMemoryModelAvailabilityVisibilityChains);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_TERMINATE_INVOCATION_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderTerminateInvocationFeatures*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderTerminateInvocationFeatures";
            generatedEmitAuto(dst, section, "shaderTerminateInvocation", value.shaderTerminateInvocation);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SEPARATE_DEPTH_STENCIL_LAYOUTS_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceSeparateDepthStencilLayoutsFeatures*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceSeparateDepthStencilLayoutsFeatures";
            generatedEmitAuto(dst, section, "separateDepthStencilLayouts", value.separateDepthStencilLayouts);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_UNIFORM_BUFFER_STANDARD_LAYOUT_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceUniformBufferStandardLayoutFeatures*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceUniformBufferStandardLayoutFeatures";
            generatedEmitAuto(dst, section, "uniformBufferStandardLayout", value.uniformBufferStandardLayout);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_BUFFER_DEVICE_ADDRESS_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceBufferDeviceAddressFeatures*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceBufferDeviceAddressFeatures";
            generatedEmitAuto(dst, section, "bufferDeviceAddress", value.bufferDeviceAddress);
            generatedEmitAuto(dst, section, "bufferDeviceAddressCaptureReplay", value.bufferDeviceAddressCaptureReplay);
            generatedEmitAuto(dst, section, "bufferDeviceAddressMultiDevice", value.bufferDeviceAddressMultiDevice);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_INTEGER_DOT_PRODUCT_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceShaderIntegerDotProductFeatures*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceShaderIntegerDotProductFeatures";
            generatedEmitAuto(dst, section, "shaderIntegerDotProduct", value.shaderIntegerDotProduct);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SYNCHRONIZATION_2_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceSynchronization2Features*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceSynchronization2Features";
            generatedEmitAuto(dst, section, "synchronization2", value.synchronization2);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ZERO_INITIALIZE_WORKGROUP_MEMORY_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceZeroInitializeWorkgroupMemoryFeatures*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceZeroInitializeWorkgroupMemoryFeatures";
            generatedEmitAuto(dst, section, "shaderZeroInitializeWorkgroupMemory", value.shaderZeroInitializeWorkgroupMemory);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_4_FEATURES: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceMaintenance4Features*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceMaintenance4Features";
            generatedEmitAuto(dst, section, "maintenance4", value.maintenance4);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_QUEUE_PERF_HINT_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceQueuePerfHintFeaturesQCOM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceQueuePerfHintFeaturesQCOM";
            generatedEmitAuto(dst, section, "queuePerfHint", value.queuePerfHint);
            break;
        }
        case VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_PROCESSING_3_FEATURES_QCOM: {
            const auto& value = *reinterpret_cast<const VkPhysicalDeviceImageProcessing3FeaturesQCOM*>(ptr);
            const char* section = "CapsViewer 4.12 parity · VkPhysicalDeviceImageProcessing3FeaturesQCOM";
            generatedEmitAuto(dst, section, "imageGatherLinear", value.imageGatherLinear);
            generatedEmitAuto(dst, section, "imageGatherExtendedModes", value.imageGatherExtendedModes);
            generatedEmitAuto(dst, section, "blockMatchExtendedClampToEdge", value.blockMatchExtendedClampToEdge);
            break;
        }
        default: break;
    }
}

static constexpr uint32_t kCapsViewerParitySerializerCount = 297;