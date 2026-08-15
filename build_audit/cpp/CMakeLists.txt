cmake_minimum_required(VERSION 3.22.1)
project(vulkanscope)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(VULKANSCOPE_HAS_ADRENOTOOLS OFF)
if(ANDROID_ABI STREQUAL "arm64-v8a")
    include(FetchContent)
    FetchContent_Declare(
        libadrenotools
        GIT_REPOSITORY https://github.com/bylaws/libadrenotools.git
        GIT_TAG 8fae8ce
        GIT_SHALLOW TRUE
        GIT_SUBMODULES_RECURSE TRUE
    )
    FetchContent_MakeAvailable(libadrenotools)
    set(VULKANSCOPE_HAS_ADRENOTOOLS ON)
endif()


include(FetchContent)
set(FETCHCONTENT_QUIET OFF)
FetchContent_Declare(
    vulkan_headers
    GIT_REPOSITORY https://github.com/KhronosGroup/Vulkan-Headers.git
    GIT_TAG e3b1eec08173d6b825cd3ac88c885a63b621504a
    GIT_SHALLOW FALSE
)
FetchContent_MakeAvailable(vulkan_headers)

set(VULKANSCOPE_VULKAN_HEADERS_DIR "${vulkan_headers_SOURCE_DIR}/include")
set(VULKANSCOPE_VULKAN_XML "${vulkan_headers_SOURCE_DIR}/registry/vk.xml")
if(NOT EXISTS "${VULKANSCOPE_VULKAN_HEADERS_DIR}/vulkan/vulkan_core.h" OR NOT EXISTS "${VULKANSCOPE_VULKAN_XML}")
    message(FATAL_ERROR "Canonical Vulkan-Headers v1.4.357 was not fetched correctly")
endif()
file(READ "${VULKANSCOPE_VULKAN_HEADERS_DIR}/vulkan/vulkan_core.h" VULKAN_CORE_HEADER_TEXT)
string(REGEX MATCH "#define VK_HEADER_VERSION[ \t]+357([\r\n])" VULKAN_HEADER_357_MATCH "${VULKAN_CORE_HEADER_TEXT}")
if(NOT VULKAN_HEADER_357_MATCH)
    message(FATAL_ERROR "Vulkan header baseline mismatch: expected VK_HEADER_VERSION 357")
endif()
find_package(Python3 COMPONENTS Interpreter REQUIRED)
set(VULKANSCOPE_GENERATED_DIR "${CMAKE_CURRENT_BINARY_DIR}/generated/vulkanscope_generated")
file(MAKE_DIRECTORY "${VULKANSCOPE_GENERATED_DIR}")
execute_process(
    COMMAND "${Python3_EXECUTABLE}" "${CMAKE_CURRENT_SOURCE_DIR}/../../../../tools/generate_extension_field_coverage.py"
        --registry "${VULKANSCOPE_VULKAN_XML}"
        --header "${VULKANSCOPE_VULKAN_HEADERS_DIR}/vulkan/vulkan_core.h"
        --out "${VULKANSCOPE_GENERATED_DIR}/extension_field_coverage_generated.inc"
    RESULT_VARIABLE VULKANSCOPE_FIELD_GENERATOR_RESULT
)
if(NOT VULKANSCOPE_FIELD_GENERATOR_RESULT EQUAL 0)
    message(FATAL_ERROR "Failed to generate Vulkan extension field coverage")
endif()
execute_process(
    COMMAND "${Python3_EXECUTABLE}" "${CMAKE_CURRENT_SOURCE_DIR}/../../../../tools/generate_extension_pnext_query.py"
        --registry "${VULKANSCOPE_VULKAN_XML}"
        --header "${VULKANSCOPE_VULKAN_HEADERS_DIR}/vulkan/vulkan_core.h"
        --out "${VULKANSCOPE_GENERATED_DIR}/runtime_extension_pnext_generated.inc"
    RESULT_VARIABLE VULKANSCOPE_PNEXT_GENERATOR_RESULT
)
if(NOT VULKANSCOPE_PNEXT_GENERATOR_RESULT EQUAL 0)
    message(FATAL_ERROR "Failed to generate Vulkan extension pNext query coverage")
endif()
execute_process(
    COMMAND "${Python3_EXECUTABLE}" "${CMAKE_CURRENT_SOURCE_DIR}/../../../../tools/audit_capsviewer_parity.py"
        --registry "${VULKANSCOPE_VULKAN_XML}"
        --header "${VULKANSCOPE_VULKAN_HEADERS_DIR}/vulkan/vulkan_core.h"
        --vulkanscope-root "${CMAKE_CURRENT_SOURCE_DIR}/../../../../"
        --out "${VULKANSCOPE_GENERATED_DIR}/capsviewer_parity_audit.json"
    RESULT_VARIABLE VULKANSCOPE_PARITY_AUDIT_RESULT
)
if(NOT VULKANSCOPE_PARITY_AUDIT_RESULT EQUAL 0)
    message(FATAL_ERROR "Failed to generate VulkanScope physical-device parity audit")
endif()

add_library(vulkanscope SHARED vulkanscope.cpp registry_query_catalog.h)
find_library(log-lib log)
find_library(android-lib android)
find_library(dl-lib dl)

target_include_directories(vulkanscope PRIVATE "${VULKANSCOPE_VULKAN_HEADERS_DIR}" "${VULKANSCOPE_GENERATED_DIR}")

target_link_libraries(vulkanscope PRIVATE
    ${log-lib}
    ${android-lib}
    ${dl-lib}
)

if(VULKANSCOPE_HAS_ADRENOTOOLS)
    target_include_directories(vulkanscope PRIVATE "${VULKANSCOPE_VULKAN_HEADERS_DIR}")

target_link_libraries(vulkanscope PRIVATE adrenotools)
    target_compile_definitions(vulkanscope PRIVATE VULKANSCOPE_HAS_ADRENOTOOLS=1)
endif()

target_compile_options(vulkanscope PRIVATE
    -Wall -Wextra -Werror
    -fstack-protector-strong
    -fvisibility=hidden
)

target_link_options(vulkanscope PRIVATE
    "-Wl,-z,max-page-size=16384"
    "-Wl,-z,common-page-size=16384"
)
