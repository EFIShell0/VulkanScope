static size_t appendParityExtensionPNext(const char* selectedExtension, const std::vector<VkExtensionProperties>& devExts, RuntimePNextStorage& storage) {
    if (!selectedExtension || selectedExtension[0] == 0 || !hasExtension(devExts, selectedExtension)) return 0;
    size_t added = 0;
    if (std::strcmp(selectedExtension, "VK_EXT_4444_formats") == 0) {
        storage.add<VkPhysicalDevice4444FormatsFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_4444_FORMATS_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_acceleration_structure") == 0) {
        storage.add<VkPhysicalDeviceAccelerationStructureFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ACCELERATION_STRUCTURE_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_device_address_binding_report") == 0) {
        storage.add<VkPhysicalDeviceAddressBindingReportFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ADDRESS_BINDING_REPORT_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_SEC_amigo_profiling") == 0) {
        storage.add<VkPhysicalDeviceAmigoProfilingFeaturesSEC>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_AMIGO_PROFILING_FEATURES_SEC, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_attachment_feedback_loop_dynamic_state") == 0) {
        storage.add<VkPhysicalDeviceAttachmentFeedbackLoopDynamicStateFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ATTACHMENT_FEEDBACK_LOOP_DYNAMIC_STATE_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_attachment_feedback_loop_layout") == 0) {
        storage.add<VkPhysicalDeviceAttachmentFeedbackLoopLayoutFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ATTACHMENT_FEEDBACK_LOOP_LAYOUT_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_blend_operation_advanced") == 0) {
        storage.add<VkPhysicalDeviceBlendOperationAdvancedFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_BLEND_OPERATION_ADVANCED_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_blend_operation_advanced") == 0) {
        storage.add<VkPhysicalDeviceBlendOperationAdvancedPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_BLEND_OPERATION_ADVANCED_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_border_color_swizzle") == 0) {
        storage.add<VkPhysicalDeviceBorderColorSwizzleFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_BORDER_COLOR_SWIZZLE_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_buffer_device_address") == 0) {
        storage.add<VkPhysicalDeviceBufferDeviceAddressFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_BUFFER_DEVICE_ADDRESS_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_cluster_acceleration_structure") == 0) {
        storage.add<VkPhysicalDeviceClusterAccelerationStructureFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CLUSTER_ACCELERATION_STRUCTURE_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_cluster_acceleration_structure") == 0) {
        storage.add<VkPhysicalDeviceClusterAccelerationStructurePropertiesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CLUSTER_ACCELERATION_STRUCTURE_PROPERTIES_NV, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_HUAWEI_cluster_culling_shader") == 0) {
        storage.add<VkPhysicalDeviceClusterCullingShaderFeaturesHUAWEI>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CLUSTER_CULLING_SHADER_FEATURES_HUAWEI, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_HUAWEI_cluster_culling_shader") == 0) {
        storage.add<VkPhysicalDeviceClusterCullingShaderPropertiesHUAWEI>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CLUSTER_CULLING_SHADER_PROPERTIES_HUAWEI, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_AMD_device_coherent_memory") == 0) {
        storage.add<VkPhysicalDeviceCoherentMemoryFeaturesAMD>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COHERENT_MEMORY_FEATURES_AMD, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_color_write_enable") == 0) {
        storage.add<VkPhysicalDeviceColorWriteEnableFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COLOR_WRITE_ENABLE_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_command_buffer_inheritance") == 0) {
        storage.add<VkPhysicalDeviceCommandBufferInheritanceFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COMMAND_BUFFER_INHERITANCE_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_compute_shader_derivatives") == 0) {
        storage.add<VkPhysicalDeviceComputeShaderDerivativesFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COMPUTE_SHADER_DERIVATIVES_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_compute_shader_derivatives") == 0) {
        storage.add<VkPhysicalDeviceComputeShaderDerivativesFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COMPUTE_SHADER_DERIVATIVES_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_compute_shader_derivatives") == 0) {
        storage.add<VkPhysicalDeviceComputeShaderDerivativesPropertiesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COMPUTE_SHADER_DERIVATIVES_PROPERTIES_KHR, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_conditional_rendering") == 0) {
        storage.add<VkPhysicalDeviceConditionalRenderingFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CONDITIONAL_RENDERING_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_conservative_rasterization") == 0) {
        storage.add<VkPhysicalDeviceConservativeRasterizationPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CONSERVATIVE_RASTERIZATION_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_cooperative_matrix2") == 0) {
        storage.add<VkPhysicalDeviceCooperativeMatrix2PropertiesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_2_PROPERTIES_NV, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_cooperative_matrix") == 0) {
        storage.add<VkPhysicalDeviceCooperativeMatrixFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_cooperative_matrix") == 0) {
        storage.add<VkPhysicalDeviceCooperativeMatrixPropertiesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_PROPERTIES_NV, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_cooperative_vector") == 0) {
        storage.add<VkPhysicalDeviceCooperativeVectorFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_VECTOR_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_cooperative_vector") == 0) {
        storage.add<VkPhysicalDeviceCooperativeVectorPropertiesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_VECTOR_PROPERTIES_NV, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_copy_memory_indirect") == 0) {
        storage.add<VkPhysicalDeviceCopyMemoryIndirectFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COPY_MEMORY_INDIRECT_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_copy_memory_indirect") == 0) {
        storage.add<VkPhysicalDeviceCopyMemoryIndirectFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COPY_MEMORY_INDIRECT_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_copy_memory_indirect") == 0) {
        storage.add<VkPhysicalDeviceCopyMemoryIndirectPropertiesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COPY_MEMORY_INDIRECT_PROPERTIES_KHR, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_copy_memory_indirect") == 0) {
        storage.add<VkPhysicalDeviceCopyMemoryIndirectPropertiesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COPY_MEMORY_INDIRECT_PROPERTIES_NV, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_corner_sampled_image") == 0) {
        storage.add<VkPhysicalDeviceCornerSampledImageFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CORNER_SAMPLED_IMAGE_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_coverage_reduction_mode") == 0) {
        storage.add<VkPhysicalDeviceCoverageReductionModeFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COVERAGE_REDUCTION_MODE_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_QCOM_filter_cubic_clamp") == 0) {
        storage.add<VkPhysicalDeviceCubicClampFeaturesQCOM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CUBIC_CLAMP_FEATURES_QCOM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_QCOM_filter_cubic_weights") == 0) {
        storage.add<VkPhysicalDeviceCubicWeightsFeaturesQCOM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CUBIC_WEIGHTS_FEATURES_QCOM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_cuda_kernel_launch") == 0) {
        storage.add<VkPhysicalDeviceCudaKernelLaunchPropertiesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CUDA_KERNEL_LAUNCH_PROPERTIES_NV, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_custom_border_color") == 0) {
        storage.add<VkPhysicalDeviceCustomBorderColorFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CUSTOM_BORDER_COLOR_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_custom_resolve") == 0) {
        storage.add<VkPhysicalDeviceCustomResolveFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CUSTOM_RESOLVE_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_ARM_data_graph") == 0) {
        storage.add<VkPhysicalDeviceDataGraphFeaturesARM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DATA_GRAPH_FEATURES_ARM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_QCOM_data_graph_model") == 0) {
        storage.add<VkPhysicalDeviceDataGraphModelFeaturesQCOM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DATA_GRAPH_MODEL_FEATURES_QCOM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_dedicated_allocation_image_aliasing") == 0) {
        storage.add<VkPhysicalDeviceDedicatedAllocationImageAliasingFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEDICATED_ALLOCATION_IMAGE_ALIASING_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_depth_bias_control") == 0) {
        storage.add<VkPhysicalDeviceDepthBiasControlFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_BIAS_CONTROL_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_depth_clamp_control") == 0) {
        storage.add<VkPhysicalDeviceDepthClampControlFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_CLAMP_CONTROL_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_depth_clamp_zero_one") == 0) {
        storage.add<VkPhysicalDeviceDepthClampZeroOneFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_CLAMP_ZERO_ONE_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_depth_clamp_zero_one") == 0) {
        storage.add<VkPhysicalDeviceDepthClampZeroOneFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_CLAMP_ZERO_ONE_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_depth_clip_control") == 0) {
        storage.add<VkPhysicalDeviceDepthClipControlFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_CLIP_CONTROL_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_depth_clip_enable") == 0) {
        storage.add<VkPhysicalDeviceDepthClipEnableFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEPTH_CLIP_ENABLE_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_descriptor_buffer") == 0) {
        storage.add<VkPhysicalDeviceDescriptorBufferFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_BUFFER_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_descriptor_buffer") == 0) {
        storage.add<VkPhysicalDeviceDescriptorBufferPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_BUFFER_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_descriptor_pool_overallocation") == 0) {
        storage.add<VkPhysicalDeviceDescriptorPoolOverallocationFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_POOL_OVERALLOCATION_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_VALVE_descriptor_set_host_mapping") == 0) {
        storage.add<VkPhysicalDeviceDescriptorSetHostMappingFeaturesVALVE>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_SET_HOST_MAPPING_FEATURES_VALVE, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_device_generated_commands_compute") == 0) {
        storage.add<VkPhysicalDeviceDeviceGeneratedCommandsComputeFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEVICE_GENERATED_COMMANDS_COMPUTE_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_device_generated_commands") == 0) {
        storage.add<VkPhysicalDeviceDeviceGeneratedCommandsFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEVICE_GENERATED_COMMANDS_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_device_generated_commands") == 0) {
        storage.add<VkPhysicalDeviceDeviceGeneratedCommandsFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEVICE_GENERATED_COMMANDS_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_device_generated_commands") == 0) {
        storage.add<VkPhysicalDeviceDeviceGeneratedCommandsPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEVICE_GENERATED_COMMANDS_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_device_memory_report") == 0) {
        storage.add<VkPhysicalDeviceDeviceMemoryReportFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEVICE_MEMORY_REPORT_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_discard_rectangles") == 0) {
        storage.add<VkPhysicalDeviceDiscardRectanglePropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DISCARD_RECTANGLE_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_displacement_micromap") == 0) {
        storage.add<VkPhysicalDeviceDisplacementMicromapFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DISPLACEMENT_MICROMAP_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_displacement_micromap") == 0) {
        storage.add<VkPhysicalDeviceDisplacementMicromapPropertiesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DISPLACEMENT_MICROMAP_PROPERTIES_NV, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_physical_device_drm") == 0) {
        storage.add<VkPhysicalDeviceDrmPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DRM_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_dynamic_rendering_unused_attachments") == 0) {
        storage.add<VkPhysicalDeviceDynamicRenderingUnusedAttachmentsFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DYNAMIC_RENDERING_UNUSED_ATTACHMENTS_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_scissor_exclusive") == 0) {
        storage.add<VkPhysicalDeviceExclusiveScissorFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXCLUSIVE_SCISSOR_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_extended_dynamic_state2") == 0) {
        storage.add<VkPhysicalDeviceExtendedDynamicState2FeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_DYNAMIC_STATE_2_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_extended_dynamic_state3") == 0) {
        storage.add<VkPhysicalDeviceExtendedDynamicState3FeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_DYNAMIC_STATE_3_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_extended_dynamic_state3") == 0) {
        storage.add<VkPhysicalDeviceExtendedDynamicState3PropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_DYNAMIC_STATE_3_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_extended_dynamic_state") == 0) {
        storage.add<VkPhysicalDeviceExtendedDynamicStateFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_DYNAMIC_STATE_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_extended_sparse_address_space") == 0) {
        storage.add<VkPhysicalDeviceExtendedSparseAddressSpaceFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_SPARSE_ADDRESS_SPACE_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_extended_sparse_address_space") == 0) {
        storage.add<VkPhysicalDeviceExtendedSparseAddressSpacePropertiesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_SPARSE_ADDRESS_SPACE_PROPERTIES_NV, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_external_compute_queue") == 0) {
        storage.add<VkPhysicalDeviceExternalComputeQueuePropertiesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_COMPUTE_QUEUE_PROPERTIES_NV, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_ANDROID_external_format_resolve") == 0) {
        storage.add<VkPhysicalDeviceExternalFormatResolveFeaturesANDROID>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_FORMAT_RESOLVE_FEATURES_ANDROID, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_ANDROID_external_format_resolve") == 0) {
        storage.add<VkPhysicalDeviceExternalFormatResolvePropertiesANDROID>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_FORMAT_RESOLVE_PROPERTIES_ANDROID, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_external_memory_host") == 0) {
        storage.add<VkPhysicalDeviceExternalMemoryHostPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_MEMORY_HOST_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_external_memory_rdma") == 0) {
        storage.add<VkPhysicalDeviceExternalMemoryRDMAFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_MEMORY_RDMA_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_device_fault") == 0) {
        storage.add<VkPhysicalDeviceFaultFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FAULT_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_ARM_format_pack") == 0) {
        storage.add<VkPhysicalDeviceFormatPackFeaturesARM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FORMAT_PACK_FEATURES_ARM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_fragment_density_map2") == 0) {
        storage.add<VkPhysicalDeviceFragmentDensityMap2FeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_2_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_fragment_density_map") == 0) {
        storage.add<VkPhysicalDeviceFragmentDensityMapFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_VALVE_fragment_density_map_layered") == 0) {
        storage.add<VkPhysicalDeviceFragmentDensityMapLayeredFeaturesVALVE>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_LAYERED_FEATURES_VALVE, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_VALVE_fragment_density_map_layered") == 0) {
        storage.add<VkPhysicalDeviceFragmentDensityMapLayeredPropertiesVALVE>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_LAYERED_PROPERTIES_VALVE, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_fragment_density_map_offset") == 0) {
        storage.add<VkPhysicalDeviceFragmentDensityMapOffsetFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_OFFSET_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_QCOM_fragment_density_map_offset") == 0) {
        storage.add<VkPhysicalDeviceFragmentDensityMapOffsetFeaturesQCOM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_OFFSET_FEATURES_QCOM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_QCOM_fragment_density_map_offset") == 0) {
        storage.add<VkPhysicalDeviceFragmentDensityMapOffsetPropertiesQCOM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_OFFSET_PROPERTIES_QCOM, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_fragment_shader_barycentric") == 0 || std::strcmp(selectedExtension, "VK_NV_fragment_shader_barycentric") == 0) {
        storage.add<VkPhysicalDeviceFragmentShaderBarycentricFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_SHADER_BARYCENTRIC_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_fragment_shader_barycentric") == 0) {
        storage.add<VkPhysicalDeviceFragmentShaderBarycentricPropertiesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_SHADER_BARYCENTRIC_PROPERTIES_KHR, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_fragment_shader_interlock") == 0) {
        storage.add<VkPhysicalDeviceFragmentShaderInterlockFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_SHADER_INTERLOCK_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_fragment_shading_rate_enums") == 0) {
        storage.add<VkPhysicalDeviceFragmentShadingRateEnumsFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_SHADING_RATE_ENUMS_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_fragment_shading_rate_enums") == 0) {
        storage.add<VkPhysicalDeviceFragmentShadingRateEnumsPropertiesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_SHADING_RATE_ENUMS_PROPERTIES_NV, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_fragment_shading_rate") == 0) {
        storage.add<VkPhysicalDeviceFragmentShadingRateFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_SHADING_RATE_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_fragment_shading_rate") == 0) {
        storage.add<VkPhysicalDeviceFragmentShadingRatePropertiesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_SHADING_RATE_PROPERTIES_KHR, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_frame_boundary") == 0) {
        storage.add<VkPhysicalDeviceFrameBoundaryFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAME_BOUNDARY_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_global_priority_query") == 0 || std::strcmp(selectedExtension, "VK_KHR_global_priority") == 0) {
        storage.add<VkPhysicalDeviceGlobalPriorityQueryFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_GLOBAL_PRIORITY_QUERY_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_AMD_gpa_interface") == 0) {
        storage.add<VkPhysicalDeviceGpaFeaturesAMD>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_GPA_FEATURES_AMD, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_graphics_pipeline_library") == 0) {
        storage.add<VkPhysicalDeviceGraphicsPipelineLibraryFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_GRAPHICS_PIPELINE_LIBRARY_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_graphics_pipeline_library") == 0) {
        storage.add<VkPhysicalDeviceGraphicsPipelineLibraryPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_GRAPHICS_PIPELINE_LIBRARY_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_HUAWEI_hdr_vivid") == 0) {
        storage.add<VkPhysicalDeviceHdrVividFeaturesHUAWEI>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_HDR_VIVID_FEATURES_HUAWEI, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_host_image_copy") == 0) {
        storage.add<VkPhysicalDeviceHostImageCopyFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_HOST_IMAGE_COPY_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_host_image_copy") == 0) {
        storage.add<VkPhysicalDeviceHostImageCopyPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_HOST_IMAGE_COPY_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_MESA_image_alignment_control") == 0) {
        storage.add<VkPhysicalDeviceImageAlignmentControlFeaturesMESA>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_ALIGNMENT_CONTROL_FEATURES_MESA, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_MESA_image_alignment_control") == 0) {
        storage.add<VkPhysicalDeviceImageAlignmentControlPropertiesMESA>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_ALIGNMENT_CONTROL_PROPERTIES_MESA, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_image_compression_control_swapchain") == 0) {
        storage.add<VkPhysicalDeviceImageCompressionControlSwapchainFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_COMPRESSION_CONTROL_SWAPCHAIN_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_QCOM_image_processing2") == 0) {
        storage.add<VkPhysicalDeviceImageProcessing2FeaturesQCOM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_PROCESSING_2_FEATURES_QCOM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_QCOM_image_processing2") == 0) {
        storage.add<VkPhysicalDeviceImageProcessing2PropertiesQCOM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_PROCESSING_2_PROPERTIES_QCOM, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_QCOM_image_processing") == 0) {
        storage.add<VkPhysicalDeviceImageProcessingFeaturesQCOM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_PROCESSING_FEATURES_QCOM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_QCOM_image_processing") == 0) {
        storage.add<VkPhysicalDeviceImageProcessingPropertiesQCOM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_PROCESSING_PROPERTIES_QCOM, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_image_sliced_view_of_3d") == 0) {
        storage.add<VkPhysicalDeviceImageSlicedViewOf3DFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_SLICED_VIEW_OF_3D_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_image_view_min_lod") == 0) {
        storage.add<VkPhysicalDeviceImageViewMinLodFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_VIEW_MIN_LOD_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_index_type_uint8") == 0) {
        storage.add<VkPhysicalDeviceIndexTypeUint8FeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INDEX_TYPE_UINT8_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_index_type_uint8") == 0) {
        storage.add<VkPhysicalDeviceIndexTypeUint8FeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INDEX_TYPE_UINT8_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_inherited_viewport_scissor") == 0) {
        storage.add<VkPhysicalDeviceInheritedViewportScissorFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INHERITED_VIEWPORT_SCISSOR_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_HUAWEI_invocation_mask") == 0) {
        storage.add<VkPhysicalDeviceInvocationMaskFeaturesHUAWEI>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INVOCATION_MASK_FEATURES_HUAWEI, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_MSFT_layered_driver") == 0) {
        storage.add<VkPhysicalDeviceLayeredDriverPropertiesMSFT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LAYERED_DRIVER_PROPERTIES_MSFT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_legacy_dithering") == 0) {
        storage.add<VkPhysicalDeviceLegacyDitheringFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LEGACY_DITHERING_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_legacy_vertex_attributes") == 0) {
        storage.add<VkPhysicalDeviceLegacyVertexAttributesFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LEGACY_VERTEX_ATTRIBUTES_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_legacy_vertex_attributes") == 0) {
        storage.add<VkPhysicalDeviceLegacyVertexAttributesPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LEGACY_VERTEX_ATTRIBUTES_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_line_rasterization") == 0) {
        storage.add<VkPhysicalDeviceLineRasterizationFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINE_RASTERIZATION_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_line_rasterization") == 0) {
        storage.add<VkPhysicalDeviceLineRasterizationFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINE_RASTERIZATION_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_line_rasterization") == 0) {
        storage.add<VkPhysicalDeviceLineRasterizationPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINE_RASTERIZATION_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_line_rasterization") == 0) {
        storage.add<VkPhysicalDeviceLineRasterizationPropertiesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINE_RASTERIZATION_PROPERTIES_KHR, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_linear_color_attachment") == 0) {
        storage.add<VkPhysicalDeviceLinearColorAttachmentFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINEAR_COLOR_ATTACHMENT_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_maintenance5") == 0) {
        storage.add<VkPhysicalDeviceMaintenance5PropertiesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_5_PROPERTIES_KHR, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_maintenance6") == 0) {
        storage.add<VkPhysicalDeviceMaintenance6FeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_6_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_maintenance6") == 0) {
        storage.add<VkPhysicalDeviceMaintenance6PropertiesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_6_PROPERTIES_KHR, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_map_memory_placed") == 0) {
        storage.add<VkPhysicalDeviceMapMemoryPlacedFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAP_MEMORY_PLACED_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_memory_decompression") == 0) {
        storage.add<VkPhysicalDeviceMemoryDecompressionFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MEMORY_DECOMPRESSION_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_memory_decompression") == 0) {
        storage.add<VkPhysicalDeviceMemoryDecompressionFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MEMORY_DECOMPRESSION_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_memory_decompression") == 0) {
        storage.add<VkPhysicalDeviceMemoryDecompressionPropertiesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MEMORY_DECOMPRESSION_PROPERTIES_NV, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_memory_priority") == 0) {
        storage.add<VkPhysicalDeviceMemoryPriorityFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MEMORY_PRIORITY_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_mesh_shader") == 0) {
        storage.add<VkPhysicalDeviceMeshShaderFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MESH_SHADER_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_mesh_shader") == 0) {
        storage.add<VkPhysicalDeviceMeshShaderFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MESH_SHADER_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_mesh_shader") == 0) {
        storage.add<VkPhysicalDeviceMeshShaderPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MESH_SHADER_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_mesh_shader") == 0) {
        storage.add<VkPhysicalDeviceMeshShaderPropertiesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MESH_SHADER_PROPERTIES_NV, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_multi_draw") == 0) {
        storage.add<VkPhysicalDeviceMultiDrawFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTI_DRAW_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_multi_draw") == 0) {
        storage.add<VkPhysicalDeviceMultiDrawPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTI_DRAW_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_multisampled_render_to_single_sampled") == 0) {
        storage.add<VkPhysicalDeviceMultisampledRenderToSingleSampledFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTISAMPLED_RENDER_TO_SINGLE_SAMPLED_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NVX_multiview_per_view_attributes") == 0) {
        storage.add<VkPhysicalDeviceMultiviewPerViewAttributesPropertiesNVX>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTIVIEW_PER_VIEW_ATTRIBUTES_PROPERTIES_NVX, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_QCOM_multiview_per_view_render_areas") == 0) {
        storage.add<VkPhysicalDeviceMultiviewPerViewRenderAreasFeaturesQCOM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTIVIEW_PER_VIEW_RENDER_AREAS_FEATURES_QCOM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_QCOM_multiview_per_view_viewports") == 0) {
        storage.add<VkPhysicalDeviceMultiviewPerViewViewportsFeaturesQCOM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTIVIEW_PER_VIEW_VIEWPORTS_FEATURES_QCOM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_mutable_descriptor_type") == 0 || std::strcmp(selectedExtension, "VK_VALVE_mutable_descriptor_type") == 0) {
        storage.add<VkPhysicalDeviceMutableDescriptorTypeFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MUTABLE_DESCRIPTOR_TYPE_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_nested_command_buffer") == 0) {
        storage.add<VkPhysicalDeviceNestedCommandBufferFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_NESTED_COMMAND_BUFFER_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_nested_command_buffer") == 0) {
        storage.add<VkPhysicalDeviceNestedCommandBufferPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_NESTED_COMMAND_BUFFER_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_non_seamless_cube_map") == 0) {
        storage.add<VkPhysicalDeviceNonSeamlessCubeMapFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_NON_SEAMLESS_CUBE_MAP_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_opacity_micromap") == 0) {
        storage.add<VkPhysicalDeviceOpacityMicromapFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_OPACITY_MICROMAP_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_opacity_micromap") == 0) {
        storage.add<VkPhysicalDeviceOpacityMicromapPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_OPACITY_MICROMAP_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_optical_flow") == 0) {
        storage.add<VkPhysicalDeviceOpticalFlowFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_OPTICAL_FLOW_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_optical_flow") == 0) {
        storage.add<VkPhysicalDeviceOpticalFlowPropertiesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_OPTICAL_FLOW_PROPERTIES_NV, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_pci_bus_info") == 0) {
        storage.add<VkPhysicalDevicePCIBusInfoPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PCI_BUS_INFO_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_pageable_device_local_memory") == 0) {
        storage.add<VkPhysicalDevicePageableDeviceLocalMemoryFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PAGEABLE_DEVICE_LOCAL_MEMORY_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_partitioned_acceleration_structure") == 0) {
        storage.add<VkPhysicalDevicePartitionedAccelerationStructureFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PARTITIONED_ACCELERATION_STRUCTURE_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_partitioned_acceleration_structure") == 0) {
        storage.add<VkPhysicalDevicePartitionedAccelerationStructurePropertiesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PARTITIONED_ACCELERATION_STRUCTURE_PROPERTIES_NV, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_per_stage_descriptor_set") == 0) {
        storage.add<VkPhysicalDevicePerStageDescriptorSetFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PER_STAGE_DESCRIPTOR_SET_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_ARM_performance_counters_by_region") == 0) {
        storage.add<VkPhysicalDevicePerformanceCountersByRegionPropertiesARM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PERFORMANCE_COUNTERS_BY_REGION_PROPERTIES_ARM, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_performance_query") == 0) {
        storage.add<VkPhysicalDevicePerformanceQueryFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PERFORMANCE_QUERY_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_performance_query") == 0) {
        storage.add<VkPhysicalDevicePerformanceQueryPropertiesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PERFORMANCE_QUERY_PROPERTIES_KHR, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_pipeline_executable_properties") == 0) {
        storage.add<VkPhysicalDevicePipelineExecutablePropertiesFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_EXECUTABLE_PROPERTIES_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_pipeline_library_group_handles") == 0) {
        storage.add<VkPhysicalDevicePipelineLibraryGroupHandlesFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_LIBRARY_GROUP_HANDLES_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_pipeline_properties") == 0) {
        storage.add<VkPhysicalDevicePipelinePropertiesFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_PROPERTIES_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_pipeline_protected_access") == 0) {
        storage.add<VkPhysicalDevicePipelineProtectedAccessFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_PROTECTED_ACCESS_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_pipeline_robustness") == 0) {
        storage.add<VkPhysicalDevicePipelineRobustnessPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_ROBUSTNESS_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_portability_subset") == 0) {
        storage.add<VkPhysicalDevicePortabilitySubsetFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PORTABILITY_SUBSET_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_present_id") == 0) {
        storage.add<VkPhysicalDevicePresentIdFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_ID_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_present_mode_fifo_latest_ready") == 0) {
        storage.add<VkPhysicalDevicePresentModeFifoLatestReadyFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_MODE_FIFO_LATEST_READY_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_present_timing") == 0) {
        storage.add<VkPhysicalDevicePresentTimingFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_TIMING_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_present_wait") == 0) {
        storage.add<VkPhysicalDevicePresentWaitFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_WAIT_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_primitive_topology_list_restart") == 0) {
        storage.add<VkPhysicalDevicePrimitiveTopologyListRestartFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRIMITIVE_TOPOLOGY_LIST_RESTART_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_primitives_generated_query") == 0) {
        storage.add<VkPhysicalDevicePrimitivesGeneratedQueryFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRIMITIVES_GENERATED_QUERY_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_provoking_vertex") == 0) {
        storage.add<VkPhysicalDeviceProvokingVertexFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROVOKING_VERTEX_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_push_descriptor") == 0) {
        storage.add<VkPhysicalDevicePushDescriptorPropertiesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PUSH_DESCRIPTOR_PROPERTIES_KHR, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_rgba10x6_formats") == 0) {
        storage.add<VkPhysicalDeviceRGBA10X6FormatsFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RGBA10X6_FORMATS_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_ARM_rasterization_order_attachment_access") == 0 || std::strcmp(selectedExtension, "VK_EXT_rasterization_order_attachment_access") == 0) {
        storage.add<VkPhysicalDeviceRasterizationOrderAttachmentAccessFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RASTERIZATION_ORDER_ATTACHMENT_ACCESS_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_raw_access_chains") == 0) {
        storage.add<VkPhysicalDeviceRawAccessChainsFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAW_ACCESS_CHAINS_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_ray_query") == 0) {
        storage.add<VkPhysicalDeviceRayQueryFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_QUERY_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_ray_tracing_invocation_reorder") == 0) {
        storage.add<VkPhysicalDeviceRayTracingInvocationReorderFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_INVOCATION_REORDER_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_ray_tracing_invocation_reorder") == 0) {
        storage.add<VkPhysicalDeviceRayTracingInvocationReorderFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_INVOCATION_REORDER_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_ray_tracing_invocation_reorder") == 0) {
        storage.add<VkPhysicalDeviceRayTracingInvocationReorderPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_INVOCATION_REORDER_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_ray_tracing_invocation_reorder") == 0) {
        storage.add<VkPhysicalDeviceRayTracingInvocationReorderPropertiesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_INVOCATION_REORDER_PROPERTIES_NV, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_ray_tracing_linear_swept_spheres") == 0) {
        storage.add<VkPhysicalDeviceRayTracingLinearSweptSpheresFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_LINEAR_SWEPT_SPHERES_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_ray_tracing_maintenance1") == 0) {
        storage.add<VkPhysicalDeviceRayTracingMaintenance1FeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_MAINTENANCE_1_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_ray_tracing_motion_blur") == 0) {
        storage.add<VkPhysicalDeviceRayTracingMotionBlurFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_MOTION_BLUR_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_ray_tracing_pipeline") == 0) {
        storage.add<VkPhysicalDeviceRayTracingPipelineFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_PIPELINE_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_ray_tracing_position_fetch") == 0) {
        storage.add<VkPhysicalDeviceRayTracingPositionFetchFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_POSITION_FETCH_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_ray_tracing") == 0) {
        storage.add<VkPhysicalDeviceRayTracingPropertiesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_PROPERTIES_NV, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_ray_tracing_validation") == 0) {
        storage.add<VkPhysicalDeviceRayTracingValidationFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_VALIDATION_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_IMG_relaxed_line_rasterization") == 0) {
        storage.add<VkPhysicalDeviceRelaxedLineRasterizationFeaturesIMG>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RELAXED_LINE_RASTERIZATION_FEATURES_IMG, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_ARM_render_pass_striped") == 0) {
        storage.add<VkPhysicalDeviceRenderPassStripedFeaturesARM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RENDER_PASS_STRIPED_FEATURES_ARM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_ARM_render_pass_striped") == 0) {
        storage.add<VkPhysicalDeviceRenderPassStripedPropertiesARM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RENDER_PASS_STRIPED_PROPERTIES_ARM, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_representative_fragment_test") == 0) {
        storage.add<VkPhysicalDeviceRepresentativeFragmentTestFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_REPRESENTATIVE_FRAGMENT_TEST_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_robustness2") == 0) {
        storage.add<VkPhysicalDeviceRobustness2FeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ROBUSTNESS_2_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_robustness2") == 0) {
        storage.add<VkPhysicalDeviceRobustness2FeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ROBUSTNESS_2_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_robustness2") == 0) {
        storage.add<VkPhysicalDeviceRobustness2PropertiesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ROBUSTNESS_2_PROPERTIES_KHR, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_ARM_scheduling_controls") == 0) {
        storage.add<VkPhysicalDeviceSchedulingControlsFeaturesARM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SCHEDULING_CONTROLS_FEATURES_ARM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_ARM_scheduling_controls") == 0) {
        storage.add<VkPhysicalDeviceSchedulingControlsPropertiesARM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SCHEDULING_CONTROLS_PROPERTIES_ARM, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_shader_64bit_indexing") == 0) {
        storage.add<VkPhysicalDeviceShader64BitIndexingFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_64_BIT_INDEXING_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_shader_atomic_float16_vector") == 0) {
        storage.add<VkPhysicalDeviceShaderAtomicFloat16VectorFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_ATOMIC_FLOAT16_VECTOR_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_shader_atomic_float2") == 0) {
        storage.add<VkPhysicalDeviceShaderAtomicFloat2FeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_ATOMIC_FLOAT_2_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_shader_atomic_float") == 0) {
        storage.add<VkPhysicalDeviceShaderAtomicFloatFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_ATOMIC_FLOAT_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_shader_bfloat16") == 0) {
        storage.add<VkPhysicalDeviceShaderBfloat16FeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_BFLOAT16_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_shader_clock") == 0) {
        storage.add<VkPhysicalDeviceShaderClockFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_CLOCK_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_ARM_shader_core_builtins") == 0) {
        storage.add<VkPhysicalDeviceShaderCoreBuiltinsFeaturesARM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_CORE_BUILTINS_FEATURES_ARM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_ARM_shader_core_builtins") == 0) {
        storage.add<VkPhysicalDeviceShaderCoreBuiltinsPropertiesARM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_CORE_BUILTINS_PROPERTIES_ARM, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_AMD_shader_core_properties2") == 0) {
        storage.add<VkPhysicalDeviceShaderCoreProperties2AMD>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_CORE_PROPERTIES_2_AMD, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_ARM_shader_core_properties") == 0) {
        storage.add<VkPhysicalDeviceShaderCorePropertiesARM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_CORE_PROPERTIES_ARM, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_AMD_shader_early_and_late_fragment_tests") == 0) {
        storage.add<VkPhysicalDeviceShaderEarlyAndLateFragmentTestsFeaturesAMD>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_EARLY_AND_LATE_FRAGMENT_TESTS_FEATURES_AMD, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_AMDX_shader_enqueue") == 0) {
        storage.add<VkPhysicalDeviceShaderEnqueuePropertiesAMDX>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_ENQUEUE_PROPERTIES_AMDX, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_shader_expect_assume") == 0) {
        storage.add<VkPhysicalDeviceShaderExpectAssumeFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_EXPECT_ASSUME_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_shader_float8") == 0) {
        storage.add<VkPhysicalDeviceShaderFloat8FeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_FLOAT8_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_shader_float_controls2") == 0) {
        storage.add<VkPhysicalDeviceShaderFloatControls2FeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_FLOAT_CONTROLS_2_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_shader_fma") == 0) {
        storage.add<VkPhysicalDeviceShaderFmaFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_FMA_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_shader_image_atomic_int64") == 0) {
        storage.add<VkPhysicalDeviceShaderImageAtomicInt64FeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_IMAGE_ATOMIC_INT64_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_shader_image_footprint") == 0) {
        storage.add<VkPhysicalDeviceShaderImageFootprintFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_IMAGE_FOOTPRINT_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_INTEL_shader_integer_functions2") == 0) {
        storage.add<VkPhysicalDeviceShaderIntegerFunctions2FeaturesINTEL>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_INTEGER_FUNCTIONS_2_FEATURES_INTEL, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_shader_module_identifier") == 0) {
        storage.add<VkPhysicalDeviceShaderModuleIdentifierFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_MODULE_IDENTIFIER_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_shader_module_identifier") == 0) {
        storage.add<VkPhysicalDeviceShaderModuleIdentifierPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_MODULE_IDENTIFIER_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_shader_object") == 0) {
        storage.add<VkPhysicalDeviceShaderObjectFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_OBJECT_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_shader_object") == 0) {
        storage.add<VkPhysicalDeviceShaderObjectPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_OBJECT_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_shader_quad_control") == 0) {
        storage.add<VkPhysicalDeviceShaderQuadControlFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_QUAD_CONTROL_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_shader_relaxed_extended_instruction") == 0) {
        storage.add<VkPhysicalDeviceShaderRelaxedExtendedInstructionFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_RELAXED_EXTENDED_INSTRUCTION_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_shader_replicated_composites") == 0) {
        storage.add<VkPhysicalDeviceShaderReplicatedCompositesFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_REPLICATED_COMPOSITES_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_shader_sm_builtins") == 0) {
        storage.add<VkPhysicalDeviceShaderSMBuiltinsFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SM_BUILTINS_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_shader_sm_builtins") == 0) {
        storage.add<VkPhysicalDeviceShaderSMBuiltinsPropertiesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SM_BUILTINS_PROPERTIES_NV, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_shader_subgroup_rotate") == 0) {
        storage.add<VkPhysicalDeviceShaderSubgroupRotateFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SUBGROUP_ROTATE_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_shader_subgroup_uniform_control_flow") == 0) {
        storage.add<VkPhysicalDeviceShaderSubgroupUniformControlFlowFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SUBGROUP_UNIFORM_CONTROL_FLOW_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_shader_tile_image") == 0) {
        storage.add<VkPhysicalDeviceShaderTileImagePropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_TILE_IMAGE_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_shader_untyped_pointers") == 0) {
        storage.add<VkPhysicalDeviceShaderUntypedPointersFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_UNTYPED_POINTERS_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_shading_rate_image") == 0) {
        storage.add<VkPhysicalDeviceShadingRateImageFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADING_RATE_IMAGE_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_shading_rate_image") == 0) {
        storage.add<VkPhysicalDeviceShadingRateImagePropertiesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADING_RATE_IMAGE_PROPERTIES_NV, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_subpass_merge_feedback") == 0) {
        storage.add<VkPhysicalDeviceSubpassMergeFeedbackFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SUBPASS_MERGE_FEEDBACK_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_HUAWEI_subpass_shading") == 0) {
        storage.add<VkPhysicalDeviceSubpassShadingFeaturesHUAWEI>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SUBPASS_SHADING_FEATURES_HUAWEI, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_HUAWEI_subpass_shading") == 0) {
        storage.add<VkPhysicalDeviceSubpassShadingPropertiesHUAWEI>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SUBPASS_SHADING_PROPERTIES_HUAWEI, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_swapchain_maintenance1") == 0) {
        storage.add<VkPhysicalDeviceSwapchainMaintenance1FeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SWAPCHAIN_MAINTENANCE_1_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_swapchain_maintenance1") == 0) {
        storage.add<VkPhysicalDeviceSwapchainMaintenance1FeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SWAPCHAIN_MAINTENANCE_1_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_ARM_tensors") == 0) {
        storage.add<VkPhysicalDeviceTensorFeaturesARM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TENSOR_FEATURES_ARM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_ARM_tensors") == 0) {
        storage.add<VkPhysicalDeviceTensorPropertiesARM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TENSOR_PROPERTIES_ARM, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_texel_buffer_alignment") == 0) {
        storage.add<VkPhysicalDeviceTexelBufferAlignmentFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TEXEL_BUFFER_ALIGNMENT_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_QCOM_tile_memory_heap") == 0) {
        storage.add<VkPhysicalDeviceTileMemoryHeapFeaturesQCOM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TILE_MEMORY_HEAP_FEATURES_QCOM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_QCOM_tile_memory_heap") == 0) {
        storage.add<VkPhysicalDeviceTileMemoryHeapPropertiesQCOM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TILE_MEMORY_HEAP_PROPERTIES_QCOM, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_QCOM_tile_properties") == 0) {
        storage.add<VkPhysicalDeviceTilePropertiesFeaturesQCOM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TILE_PROPERTIES_FEATURES_QCOM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_QCOM_tile_shading") == 0) {
        storage.add<VkPhysicalDeviceTileShadingFeaturesQCOM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TILE_SHADING_FEATURES_QCOM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_QCOM_tile_shading") == 0) {
        storage.add<VkPhysicalDeviceTileShadingPropertiesQCOM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TILE_SHADING_PROPERTIES_QCOM, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_transform_feedback") == 0) {
        storage.add<VkPhysicalDeviceTransformFeedbackFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TRANSFORM_FEEDBACK_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_unified_image_layouts") == 0) {
        storage.add<VkPhysicalDeviceUnifiedImageLayoutsFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_UNIFIED_IMAGE_LAYOUTS_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_vertex_attribute_divisor") == 0) {
        storage.add<VkPhysicalDeviceVertexAttributeDivisorFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VERTEX_ATTRIBUTE_DIVISOR_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_vertex_attribute_divisor") == 0) {
        storage.add<VkPhysicalDeviceVertexAttributeDivisorFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VERTEX_ATTRIBUTE_DIVISOR_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_vertex_attribute_divisor") == 0) {
        storage.add<VkPhysicalDeviceVertexAttributeDivisorPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VERTEX_ATTRIBUTE_DIVISOR_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_vertex_attribute_divisor") == 0) {
        storage.add<VkPhysicalDeviceVertexAttributeDivisorPropertiesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VERTEX_ATTRIBUTE_DIVISOR_PROPERTIES_KHR, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_vertex_attribute_robustness") == 0) {
        storage.add<VkPhysicalDeviceVertexAttributeRobustnessFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VERTEX_ATTRIBUTE_ROBUSTNESS_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_vertex_input_dynamic_state") == 0) {
        storage.add<VkPhysicalDeviceVertexInputDynamicStateFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VERTEX_INPUT_DYNAMIC_STATE_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_video_decode_vp9") == 0) {
        storage.add<VkPhysicalDeviceVideoDecodeVP9FeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_DECODE_VP9_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_video_encode_av1") == 0) {
        storage.add<VkPhysicalDeviceVideoEncodeAV1FeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_ENCODE_AV1_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_video_encode_intra_refresh") == 0) {
        storage.add<VkPhysicalDeviceVideoEncodeIntraRefreshFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_ENCODE_INTRA_REFRESH_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_video_encode_quantization_map") == 0) {
        storage.add<VkPhysicalDeviceVideoEncodeQuantizationMapFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_ENCODE_QUANTIZATION_MAP_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_VALVE_video_encode_rgb_conversion") == 0) {
        storage.add<VkPhysicalDeviceVideoEncodeRgbConversionFeaturesVALVE>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_ENCODE_RGB_CONVERSION_FEATURES_VALVE, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_video_maintenance1") == 0) {
        storage.add<VkPhysicalDeviceVideoMaintenance1FeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_MAINTENANCE_1_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_video_maintenance2") == 0) {
        storage.add<VkPhysicalDeviceVideoMaintenance2FeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_MAINTENANCE_2_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_workgroup_memory_explicit_layout") == 0) {
        storage.add<VkPhysicalDeviceWorkgroupMemoryExplicitLayoutFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_WORKGROUP_MEMORY_EXPLICIT_LAYOUT_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_QCOM_ycbcr_degamma") == 0) {
        storage.add<VkPhysicalDeviceYcbcrDegammaFeaturesQCOM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_YCBCR_DEGAMMA_FEATURES_QCOM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_ycbcr_image_arrays") == 0) {
        storage.add<VkPhysicalDeviceYcbcrImageArraysFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_YCBCR_IMAGE_ARRAYS_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_zero_initialize_device_memory") == 0) {
        storage.add<VkPhysicalDeviceZeroInitializeDeviceMemoryFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ZERO_INITIALIZE_DEVICE_MEMORY_FEATURES_EXT, true, false);
        ++added;
    }
    return added;
}

static constexpr uint32_t kCapsViewerParityStructCount = 259;