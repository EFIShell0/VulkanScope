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
        GIT_TAG 8fae8ce254dfc1344527e05301e43f37dea2df80
        GIT_SHALLOW FALSE
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
    GIT_TAG 31386378257ac8653ce5b32c93baec385259ebbe
    GIT_SHALLOW FALSE
)
FetchContent_MakeAvailable(vulkan_headers)

set(VULKANSCOPE_VULKAN_HEADERS_DIR "${vulkan_headers_SOURCE_DIR}/include")
set(VULKANSCOPE_VULKAN_XML "${vulkan_headers_SOURCE_DIR}/registry/vk.xml")
get_filename_component(VULKANSCOPE_PROJECT_ROOT "${CMAKE_CURRENT_SOURCE_DIR}/../../../.." ABSOLUTE)
set(VULKANSCOPE_LOCKED_VULKAN_XML "${VULKANSCOPE_PROJECT_ROOT}/registry/upstream/vk.xml")
if(NOT EXISTS "${VULKANSCOPE_LOCKED_VULKAN_XML}")
    message(FATAL_ERROR "Bundled canonical Vulkan 1.4.361 registry snapshot is missing")
endif()
file(SHA256 "${VULKANSCOPE_LOCKED_VULKAN_XML}" VULKANSCOPE_LOCKED_VULKAN_XML_SHA256)
if(NOT VULKANSCOPE_LOCKED_VULKAN_XML_SHA256 STREQUAL "3ff4984b841932e04eebeb4ce2a6613ebd37c00ffb2e96549785b2c5d7da9e1d")
    message(FATAL_ERROR "Bundled Vulkan registry snapshot hash mismatch")
endif()
if(NOT EXISTS "${VULKANSCOPE_VULKAN_HEADERS_DIR}/vulkan/vulkan_core.h" OR NOT EXISTS "${VULKANSCOPE_VULKAN_XML}")
    message(FATAL_ERROR "Canonical Vulkan-Headers v1.4.361 was not fetched correctly")
endif()
file(READ "${VULKANSCOPE_VULKAN_HEADERS_DIR}/vulkan/vulkan_core.h" VULKAN_CORE_HEADER_TEXT)
string(REGEX MATCH "#define VK_HEADER_VERSION[ \t]+361([\r\n])" VULKAN_HEADER_361_MATCH "${VULKAN_CORE_HEADER_TEXT}")
if(NOT VULKAN_HEADER_361_MATCH)
    message(FATAL_ERROR "Vulkan header baseline mismatch: expected VK_HEADER_VERSION 361")
endif()
file(READ "${VULKANSCOPE_VULKAN_XML}" VULKAN_HEADERS_REGISTRY_TEXT)
string(FIND "${VULKAN_HEADERS_REGISTRY_TEXT}" "<name>VK_HEADER_VERSION</name> 361" VULKAN_REGISTRY_HEADER_361_POS)
if(VULKAN_REGISTRY_HEADER_361_POS EQUAL -1)
    message(FATAL_ERROR "Vulkan-Headers registry semantic baseline mismatch: expected VK_HEADER_VERSION 361")
endif()
string(FIND "${VULKAN_HEADERS_REGISTRY_TEXT}" "VK_NV_private_data_base_handle" VULKAN_REGISTRY_361_SENTINEL_POS)
if(VULKAN_REGISTRY_361_SENTINEL_POS EQUAL -1)
    message(FATAL_ERROR "Vulkan-Headers registry is missing the Vulkan 1.4.361 sentinel extension")
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
