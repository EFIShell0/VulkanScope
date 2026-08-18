struct RuntimePNextStorage {
    struct Base { virtual ~Base() = default; virtual VkBaseOutStructure* base() = 0; };
    template <typename T> struct Item final : Base {
        T value{};
        explicit Item(VkStructureType sType) { value.sType = sType; value.pNext = nullptr; }
        VkBaseOutStructure* base() override { return reinterpret_cast<VkBaseOutStructure*>(&value); }
    };
    std::vector<std::unique_ptr<Base>> items;
    VkBaseOutStructure* featureHead = nullptr;
    VkBaseOutStructure* featureTail = nullptr;
    VkBaseOutStructure* propertyHead = nullptr;
    VkBaseOutStructure* propertyTail = nullptr;
    template <typename T> T* add(VkStructureType sType, bool feature, bool property) {
        auto item = std::make_unique<Item<T>>(sType);
        T* value = &item->value;
        VkBaseOutStructure* node = item->base();
        items.emplace_back(std::move(item));
        if (feature) { if (!featureHead) featureHead = node; else featureTail->pNext = node; featureTail = node; }
        if (property) { if (!propertyHead) propertyHead = node; else propertyTail->pNext = node; propertyTail = node; }
        return value;
    }
};

static size_t appendAllGeneratedExtensionPNext(const char* selectedExtension, const std::vector<VkExtensionProperties>& devExts, RuntimePNextStorage& storage) {
    if (!selectedExtension || selectedExtension[0] == 0 || !hasExtension(devExts, selectedExtension)) return 0;
    size_t added = 0;
    if (std::strcmp(selectedExtension, "VK_EXT_texture_compression_astc_3d") == 0) {
        storage.add<VkPhysicalDeviceTextureCompressionASTC3DFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TEXTURE_COMPRESSION_ASTC_3D_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_compute_occupancy_priority") == 0) {
        storage.add<VkPhysicalDeviceComputeOccupancyPriorityFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COMPUTE_OCCUPANCY_PRIORITY_FEATURES_NV, true, false);
        storage.add<VkPhysicalDeviceComputeOccupancyPriorityFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COMPUTE_OCCUPANCY_PRIORITY_FEATURES_NV, false, true);
        added += 2;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_cooperative_matrix") == 0) {
        storage.add<VkPhysicalDeviceCooperativeMatrixFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_cooperative_matrix") == 0) {
        storage.add<VkPhysicalDeviceCooperativeMatrixPropertiesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_PROPERTIES_KHR, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_QCOM_cooperative_matrix_conversion") == 0) {
        storage.add<VkPhysicalDeviceCooperativeMatrixConversionFeaturesQCOM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_CONVERSION_FEATURES_QCOM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_cooperative_matrix_decode_vector") == 0) {
        storage.add<VkPhysicalDeviceCooperativeMatrixDecodeVectorFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_DECODE_VECTOR_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_ARM_data_graph_neural_accelerator_statistics") == 0) {
        storage.add<VkPhysicalDeviceDataGraphNeuralAcceleratorStatisticsFeaturesARM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DATA_GRAPH_NEURAL_ACCELERATOR_STATISTICS_FEATURES_ARM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_ARM_data_graph_optical_flow") == 0) {
        storage.add<VkPhysicalDeviceDataGraphOpticalFlowFeaturesARM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DATA_GRAPH_OPTICAL_FLOW_FEATURES_ARM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_descriptor_heap") == 0) {
        storage.add<VkPhysicalDeviceDescriptorHeapFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_HEAP_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_descriptor_heap") == 0) {
        storage.add<VkPhysicalDeviceDescriptorHeapPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_HEAP_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_device_address_commands") == 0) {
        storage.add<VkPhysicalDeviceDeviceAddressCommandsFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEVICE_ADDRESS_COMMANDS_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_device_fault") == 0) {
        storage.add<VkPhysicalDeviceFaultFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FAULT_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_device_fault") == 0) {
        storage.add<VkPhysicalDeviceFaultPropertiesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FAULT_PROPERTIES_KHR, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_QCOM_elapsed_timer_query") == 0) {
        storage.add<VkPhysicalDeviceElapsedTimerQueryFeaturesQCOM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ELAPSED_TIMER_QUERY_FEATURES_QCOM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_extended_flags") == 0) {
        storage.add<VkPhysicalDeviceExtendedFlagsFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_FLAGS_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_present_mode_fifo_latest_ready") == 0) {
        storage.add<VkPhysicalDevicePresentModeFifoLatestReadyFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_MODE_FIFO_LATEST_READY_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_fragment_density_map") == 0) {
        storage.add<VkPhysicalDeviceFragmentDensityMapPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_fragment_density_map2") == 0) {
        storage.add<VkPhysicalDeviceFragmentDensityMap2PropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_2_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_QCOM_image_processing3") == 0) {
        storage.add<VkPhysicalDeviceImageProcessing3FeaturesQCOM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_PROCESSING_3_FEATURES_QCOM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_internally_synchronized_queues") == 0) {
        storage.add<VkPhysicalDeviceInternallySynchronizedQueuesFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INTERNALLY_SYNCHRONIZED_QUEUES_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_maintenance10") == 0) {
        storage.add<VkPhysicalDeviceMaintenance10FeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_10_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_maintenance10") == 0) {
        storage.add<VkPhysicalDeviceMaintenance10PropertiesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_10_PROPERTIES_KHR, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_maintenance11") == 0) {
        storage.add<VkPhysicalDeviceMaintenance11FeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_11_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_maintenance7") == 0) {
        storage.add<VkPhysicalDeviceMaintenance7FeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_7_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_maintenance7") == 0) {
        storage.add<VkPhysicalDeviceMaintenance7PropertiesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_7_PROPERTIES_KHR, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_maintenance8") == 0) {
        storage.add<VkPhysicalDeviceMaintenance8FeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_8_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_maintenance9") == 0) {
        storage.add<VkPhysicalDeviceMaintenance9FeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_9_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_maintenance9") == 0) {
        storage.add<VkPhysicalDeviceMaintenance9PropertiesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_9_PROPERTIES_KHR, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_multisampled_render_to_swapchain") == 0) {
        storage.add<VkPhysicalDeviceMultisampledRenderToSwapchainFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTISAMPLED_RENDER_TO_SWAPCHAIN_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_opacity_micromap") == 0) {
        storage.add<VkPhysicalDeviceOpacityMicromapFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_OPACITY_MICROMAP_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_opacity_micromap") == 0) {
        storage.add<VkPhysicalDeviceOpacityMicromapPropertiesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_OPACITY_MICROMAP_PROPERTIES_KHR, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_pipeline_binary") == 0) {
        storage.add<VkPhysicalDevicePipelineBinaryFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_BINARY_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_pipeline_binary") == 0) {
        storage.add<VkPhysicalDevicePipelineBinaryPropertiesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_BINARY_PROPERTIES_KHR, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_SEC_pipeline_cache_incremental_mode") == 0) {
        storage.add<VkPhysicalDevicePipelineCacheIncrementalModeFeaturesSEC>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_CACHE_INCREMENTAL_MODE_FEATURES_SEC, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_present_id2") == 0) {
        storage.add<VkPhysicalDevicePresentId2FeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_ID_2_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_present_wait2") == 0) {
        storage.add<VkPhysicalDevicePresentWait2FeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_WAIT_2_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_primitive_restart_index") == 0) {
        storage.add<VkPhysicalDevicePrimitiveRestartIndexFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRIMITIVE_RESTART_INDEX_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_push_constant_bank") == 0) {
        storage.add<VkPhysicalDevicePushConstantBankFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PUSH_CONSTANT_BANK_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_push_constant_bank") == 0) {
        storage.add<VkPhysicalDevicePushConstantBankPropertiesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PUSH_CONSTANT_BANK_PROPERTIES_NV, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_QCOM_queue_perf_hint") == 0) {
        storage.add<VkPhysicalDeviceQueuePerfHintFeaturesQCOM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_QUEUE_PERF_HINT_FEATURES_QCOM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_QCOM_queue_perf_hint") == 0) {
        storage.add<VkPhysicalDeviceQueuePerfHintPropertiesQCOM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_QUEUE_PERF_HINT_PROPERTIES_QCOM, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_shader_abort") == 0) {
        storage.add<VkPhysicalDeviceShaderAbortFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_ABORT_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_shader_abort") == 0) {
        storage.add<VkPhysicalDeviceShaderAbortPropertiesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_ABORT_PROPERTIES_KHR, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_shader_constant_data") == 0) {
        storage.add<VkPhysicalDeviceShaderConstantDataFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_CONSTANT_DATA_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_ARM_shader_instrumentation") == 0) {
        storage.add<VkPhysicalDeviceShaderInstrumentationFeaturesARM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_INSTRUMENTATION_FEATURES_ARM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_ARM_shader_instrumentation") == 0) {
        storage.add<VkPhysicalDeviceShaderInstrumentationPropertiesARM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_INSTRUMENTATION_PROPERTIES_ARM, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_shader_long_vector") == 0) {
        storage.add<VkPhysicalDeviceShaderLongVectorFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_LONG_VECTOR_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_shader_long_vector") == 0) {
        storage.add<VkPhysicalDeviceShaderLongVectorPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_LONG_VECTOR_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_VALVE_shader_mixed_float_dot_product") == 0) {
        storage.add<VkPhysicalDeviceShaderMixedFloatDotProductFeaturesVALVE>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_MIXED_FLOAT_DOT_PRODUCT_FEATURES_VALVE, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_QCOM_shader_multiple_wait_queues") == 0) {
        storage.add<VkPhysicalDeviceShaderMultipleWaitQueuesFeaturesQCOM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_MULTIPLE_WAIT_QUEUES_FEATURES_QCOM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_QCOM_shader_multiple_wait_queues") == 0) {
        storage.add<VkPhysicalDeviceShaderMultipleWaitQueuesPropertiesQCOM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_MULTIPLE_WAIT_QUEUES_PROPERTIES_QCOM, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_shader_ocp_microscaling_types") == 0) {
        storage.add<VkPhysicalDeviceShaderOCPMicroscalingTypesFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_OCP_MICROSCALING_TYPES_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_shader_split_barrier") == 0) {
        storage.add<VkPhysicalDeviceShaderSplitBarrierFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SPLIT_BARRIER_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_shader_split_barrier") == 0) {
        storage.add<VkPhysicalDeviceShaderSplitBarrierPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SPLIT_BARRIER_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_shader_subgroup_partitioned") == 0) {
        storage.add<VkPhysicalDeviceShaderSubgroupPartitionedFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SUBGROUP_PARTITIONED_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_shader_uniform_buffer_unsized_array") == 0) {
        storage.add<VkPhysicalDeviceShaderUniformBufferUnsizedArrayFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_UNIFORM_BUFFER_UNSIZED_ARRAY_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_SEC_throttle_hint") == 0) {
        storage.add<VkPhysicalDeviceThrottleHintFeaturesSEC>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_THROTTLE_HINT_FEATURES_SEC, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_video_encode_feedback2") == 0) {
        storage.add<VkPhysicalDeviceVideoEncodeFeedback2FeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_ENCODE_FEEDBACK_2_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_transform_feedback") == 0) {
        storage.add<VkPhysicalDeviceTransformFeedbackPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TRANSFORM_FEEDBACK_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_subgroup_size_control") == 0) {
        storage.add<VkPhysicalDeviceSubgroupSizeControlProperties>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SUBGROUP_SIZE_CONTROL_PROPERTIES, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_provoking_vertex") == 0) {
        storage.add<VkPhysicalDeviceProvokingVertexPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROVOKING_VERTEX_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_line_rasterization") == 0) {
        storage.add<VkPhysicalDeviceLineRasterizationProperties>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_LINE_RASTERIZATION_PROPERTIES, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_texel_buffer_alignment") == 0) {
        storage.add<VkPhysicalDeviceTexelBufferAlignmentProperties>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TEXEL_BUFFER_ALIGNMENT_PROPERTIES, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_robustness2") == 0) {
        storage.add<VkPhysicalDeviceRobustness2PropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ROBUSTNESS_2_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_sample_locations") == 0) {
        storage.add<VkPhysicalDeviceSampleLocationsPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SAMPLE_LOCATIONS_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_custom_border_color") == 0) {
        storage.add<VkPhysicalDeviceCustomBorderColorPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CUSTOM_BORDER_COLOR_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_fragment_density_map_offset") == 0) {
        storage.add<VkPhysicalDeviceFragmentDensityMapOffsetPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_OFFSET_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_map_memory_placed") == 0) {
        storage.add<VkPhysicalDeviceMapMemoryPlacedPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAP_MEMORY_PLACED_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_acceleration_structure") == 0) {
        storage.add<VkPhysicalDeviceAccelerationStructurePropertiesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ACCELERATION_STRUCTURE_PROPERTIES_KHR, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_ray_tracing_pipeline") == 0) {
        storage.add<VkPhysicalDeviceRayTracingPipelinePropertiesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_PIPELINE_PROPERTIES_KHR, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_portability_subset") == 0) {
        storage.add<VkPhysicalDevicePortabilitySubsetPropertiesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PORTABILITY_SUBSET_PROPERTIES_KHR, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_maintenance3") == 0) {
        storage.add<VkPhysicalDeviceMaintenance3Properties>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_3_PROPERTIES, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_driver_properties") == 0) {
        storage.add<VkPhysicalDeviceDriverProperties>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DRIVER_PROPERTIES, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_timeline_semaphore") == 0) {
        storage.add<VkPhysicalDeviceTimelineSemaphoreProperties>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TIMELINE_SEMAPHORE_PROPERTIES, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_AMD_shader_core_properties") == 0) {
        storage.add<VkPhysicalDeviceShaderCorePropertiesAMD>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_CORE_PROPERTIES_AMD, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_device_generated_commands") == 0) {
        storage.add<VkPhysicalDeviceDeviceGeneratedCommandsPropertiesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEVICE_GENERATED_COMMANDS_PROPERTIES_NV, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_memory_decompression") == 0) {
        storage.add<VkPhysicalDeviceMemoryDecompressionPropertiesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MEMORY_DECOMPRESSION_PROPERTIES_EXT, false, true);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_AMD_anti_lag") == 0) {
        storage.add<VkPhysicalDeviceAntiLagFeaturesAMD>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ANTI_LAG_FEATURES_AMD, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_AMDX_shader_enqueue") == 0) {
        storage.add<VkPhysicalDeviceShaderEnqueueFeaturesAMDX>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_ENQUEUE_FEATURES_AMDX, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_AMDX_dense_geometry_format") == 0) {
        storage.add<VkPhysicalDeviceDenseGeometryFormatFeaturesAMDX>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DENSE_GEOMETRY_FORMAT_FEATURES_AMDX, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_ARM_pipeline_opacity_micromap") == 0) {
        storage.add<VkPhysicalDevicePipelineOpacityMicromapFeaturesARM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_OPACITY_MICROMAP_FEATURES_ARM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_ARM_performance_counters_by_region") == 0) {
        storage.add<VkPhysicalDevicePerformanceCountersByRegionFeaturesARM>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PERFORMANCE_COUNTERS_BY_REGION_FEATURES_ARM, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_texture_compression_astc_hdr") == 0) {
        storage.add<VkPhysicalDeviceTextureCompressionASTCHDRFeatures>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TEXTURE_COMPRESSION_ASTC_HDR_FEATURES, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_astc_decode_mode") == 0) {
        storage.add<VkPhysicalDeviceASTCDecodeFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ASTC_DECODE_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_pipeline_robustness") == 0) {
        storage.add<VkPhysicalDevicePipelineRobustnessFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_ROBUSTNESS_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_scalar_block_layout") == 0) {
        storage.add<VkPhysicalDeviceScalarBlockLayoutFeatures>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SCALAR_BLOCK_LAYOUT_FEATURES, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_ycbcr_2plane_444_formats") == 0) {
        storage.add<VkPhysicalDeviceYcbcr2Plane444FormatsFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_YCBCR_2_PLANE_444_FORMATS_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_image_robustness") == 0) {
        storage.add<VkPhysicalDeviceImageRobustnessFeatures>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_ROBUSTNESS_FEATURES, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_image_compression_control") == 0) {
        storage.add<VkPhysicalDeviceImageCompressionControlFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_COMPRESSION_CONTROL_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_image_2d_view_of_3d") == 0) {
        storage.add<VkPhysicalDeviceImage2DViewOf3DFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_2D_VIEW_OF_3D_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_shader_tile_image") == 0) {
        storage.add<VkPhysicalDeviceShaderTileImageFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_TILE_IMAGE_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_dynamic_rendering_local_read") == 0) {
        storage.add<VkPhysicalDeviceDynamicRenderingLocalReadFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DYNAMIC_RENDERING_LOCAL_READ_FEATURES, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_shader_maximal_reconvergence") == 0) {
        storage.add<VkPhysicalDeviceShaderMaximalReconvergenceFeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_MAXIMAL_RECONVERGENCE_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_KHR_maintenance5") == 0) {
        storage.add<VkPhysicalDeviceMaintenance5FeaturesKHR>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_5_FEATURES_KHR, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_present_barrier") == 0) {
        storage.add<VkPhysicalDevicePresentBarrierFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_BARRIER_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_device_diagnostics_config") == 0) {
        storage.add<VkPhysicalDeviceDiagnosticsConfigFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DIAGNOSTICS_CONFIG_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_cuda_kernel_launch") == 0) {
        storage.add<VkPhysicalDeviceCudaKernelLaunchFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_CUDA_KERNEL_LAUNCH_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_cooperative_matrix2") == 0) {
        storage.add<VkPhysicalDeviceCooperativeMatrix2FeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_2_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_NV_present_metering") == 0) {
        storage.add<VkPhysicalDevicePresentMeteringFeaturesNV>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_METERING_FEATURES_NV, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_image_tiling_control") == 0) {
        storage.add<VkPhysicalDeviceImageTilingControlFeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_TILING_CONTROL_FEATURES_EXT, true, false);
        ++added;
    }
    if (std::strcmp(selectedExtension, "VK_EXT_cooperative_matrix_maintenance1") == 0) {
        storage.add<VkPhysicalDeviceCooperativeMatrixMaintenance1FeaturesEXT>(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_MAINTENANCE_1_FEATURES_EXT, true, false);
        ++added;
    }
    return added;
}
static constexpr uint32_t kGeneratedRuntimePNextTypeCount = 102;
