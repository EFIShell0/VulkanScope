cmake_minimum_required(VERSION 3.22.1)
project(vulkanscope)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# libadrenotools is used only by the 64-bit ARM build. Its upstream CMake
# intentionally rejects armeabi-v7a, so it must never enter those build graphs.
set(VULKANSCOPE_HAS_ADRENOTOOLS OFF)
if(ANDROID_ABI STREQUAL "arm64-v8a")
    include(FetchContent)
    FetchContent_Declare(
        libadrenotools
        GIT_REPOSITORY https://github.com/bylaws/libadrenotools.git
        GIT_TAG master
        GIT_SHALLOW TRUE
        GIT_SUBMODULES_RECURSE TRUE
    )
    FetchContent_MakeAvailable(libadrenotools)
    set(VULKANSCOPE_HAS_ADRENOTOOLS ON)
endif()

add_library(vulkanscope SHARED vulkanscope.cpp)
find_library(log-lib log)
find_library(android-lib android)
find_library(dl-lib dl)

target_link_libraries(vulkanscope PRIVATE
    ${log-lib}
    ${android-lib}
    ${dl-lib}
)

if(VULKANSCOPE_HAS_ADRENOTOOLS)
    target_link_libraries(vulkanscope PRIVATE adrenotools)
    target_compile_definitions(vulkanscope PRIVATE VULKANSCOPE_HAS_ADRENOTOOLS=1)
endif()

target_compile_options(vulkanscope PRIVATE
    -Wall -Wextra -Werror
    -fstack-protector-strong
    -fvisibility=hidden
)
