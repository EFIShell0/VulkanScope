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
    GIT_TAG 0b7f383797fa7be53ae28213e001ae60668ee511
    GIT_SHALLOW FALSE
)
FetchContent_MakeAvailable(vulkan_headers)

set(VULKANSCOPE_VULKAN_HEADERS_DIR "${vulkan_headers_SOURCE_DIR}/include")
set(VULKANSCOPE_VULKAN_XML "${vulkan_headers_SOURCE_DIR}/registry/vk.xml")
if(NOT EXISTS "${VULKANSCOPE_VULKAN_HEADERS_DIR}/vulkan/vulkan_core.h" OR NOT EXISTS "${VULKANSCOPE_VULKAN_XML}")
    message(FATAL_ERROR "Canonical Vulkan-Headers v1.4.360 was not fetched correctly")
endif()
file(READ "${VULKANSCOPE_VULKAN_HEADERS_DIR}/vulkan/vulkan_core.h" VULKAN_CORE_HEADER_TEXT)
string(REGEX MATCH "#define VK_HEADER_VERSION[ \t]+360([\r\n])" VULKAN_HEADER_360_MATCH "${VULKAN_CORE_HEADER_TEXT}")
if(NOT VULKAN_HEADER_360_MATCH)
    message(FATAL_ERROR "Vulkan header baseline mismatch: expected VK_HEADER_VERSION 360")
endif()
set(VULKANSCOPE_GENERATED_DIR "${CMAKE_CURRENT_SOURCE_DIR}")
set(VULKANSCOPE_GENERATED_FIELD_FILE "${VULKANSCOPE_GENERATED_DIR}/extension_field_coverage_generated.inc")
set(VULKANSCOPE_GENERATED_PNEXT_FILE "${VULKANSCOPE_GENERATED_DIR}/runtime_extension_pnext_generated.inc")
if(NOT EXISTS "${VULKANSCOPE_GENERATED_FIELD_FILE}" OR NOT EXISTS "${VULKANSCOPE_GENERATED_PNEXT_FILE}")
    message(FATAL_ERROR "Checked-in Vulkan generated metadata is missing")
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
    "-Wl,-z,relro"
    "-Wl,-z,now"
)
