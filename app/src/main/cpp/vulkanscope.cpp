#include "vulkan_min.h"
#include <jni.h>
#include <android/native_window_jni.h>
#include <dlfcn.h>
#include <algorithm>
#include <array>
#include <cstdint>
#include <cstdlib>
#include <cstring>
#include <sstream>
#include <string>
#include <vector>
#include <android/log.h>
#include <android/dlext.h>
#if defined(VULKANSCOPE_HAS_ADRENOTOOLS)
#include <adrenotools/driver.h>
#endif

namespace {
struct VulkanApi {
    void* library = nullptr;
    PFN_vkGetInstanceProcAddr getInstanceProcAddr = nullptr;
    PFN_vkEnumerateInstanceVersion enumerateInstanceVersion = nullptr;
    PFN_vkEnumerateInstanceExtensionProperties enumerateInstanceExtensionProperties = nullptr;
    PFN_vkEnumerateInstanceLayerProperties enumerateInstanceLayerProperties = nullptr;
    PFN_vkCreateInstance createInstance = nullptr;
    PFN_vkDestroyInstance destroyInstance = nullptr;
    PFN_vkEnumeratePhysicalDevices enumeratePhysicalDevices = nullptr;
    PFN_vkGetPhysicalDeviceProperties getPhysicalDeviceProperties = nullptr;
    PFN_vkGetPhysicalDeviceProperties2 getPhysicalDeviceProperties2 = nullptr;
    PFN_vkGetPhysicalDeviceFeatures getPhysicalDeviceFeatures = nullptr;
    PFN_vkGetPhysicalDeviceFeatures2 getPhysicalDeviceFeatures2 = nullptr;
    PFN_vkGetPhysicalDeviceMemoryProperties getPhysicalDeviceMemoryProperties = nullptr;
    PFN_vkGetPhysicalDeviceQueueFamilyProperties getPhysicalDeviceQueueFamilyProperties = nullptr;
    PFN_vkEnumerateDeviceExtensionProperties enumerateDeviceExtensionProperties = nullptr;
    PFN_vkGetPhysicalDeviceFormatProperties getPhysicalDeviceFormatProperties = nullptr;
    PFN_vkCreateAndroidSurfaceKHR createAndroidSurfaceKHR = nullptr;
    PFN_vkDestroySurfaceKHR destroySurfaceKHR = nullptr;
    PFN_vkGetPhysicalDeviceSurfaceCapabilitiesKHR getPhysicalDeviceSurfaceCapabilitiesKHR = nullptr;
    PFN_vkGetPhysicalDeviceSurfaceFormatsKHR getPhysicalDeviceSurfaceFormatsKHR = nullptr;
    PFN_vkGetPhysicalDeviceSurfacePresentModesKHR getPhysicalDeviceSurfacePresentModesKHR = nullptr;
    PFN_vkGetPhysicalDeviceSurfaceSupportKHR getPhysicalDeviceSurfaceSupportKHR = nullptr;
    std::string openError;

    ~VulkanApi() { if (library) dlclose(library); }

    template <typename T>
    T load(const char* name) const { return reinterpret_cast<T>(dlsym(library, name)); }

    template <typename T>
    T loadInstance(VkInstance instance, const char* name) const { return reinterpret_cast<T>(getInstanceProcAddr(instance, name)); }

    bool open(const char* driverMode, const char* driverIcdPath, const char* driverBundlePath, const char* hookLibDir) {
        // These paths are meaningful only for the optional arm64 Turnip path.
        // Keep the JNI/native ABI identical on all Android architectures while
        // explicitly consuming them on system-driver builds too (-Werror).
        (void)driverIcdPath;
        (void)driverBundlePath;
        (void)hookLibDir;

        const bool wantTurnip = driverMode && std::strcmp(driverMode, "TURNIP") == 0;

        if (wantTurnip) {
#if !defined(VULKANSCOPE_HAS_ADRENOTOOLS)
            // These arguments are meaningful only for the arm64 + AdrenoTools path.
            // Keep the common JNI/native signature identical across all ABIs, but
            // explicitly consume the parameters here because this target is built
            // with -Werror and must also compile cleanly for armeabi-v7a/x86_64.
            openError = "Turnip is only available on arm64-v8a builds";
            return false;
#else
            if (!driverIcdPath || driverIcdPath[0] == '\0') {
                openError = "Turnip library path is empty";
                return false;
            }
            if (!driverBundlePath || driverBundlePath[0] == '\0') {
                openError = "Turnip driver directory is empty";
                return false;
            }
            if (!hookLibDir || hookLibDir[0] == '\0') {
                openError = "AdrenoTools hook library directory is empty";
                return false;
            }

            // Do NOT dlopen libvulkan_freedreno.so directly. On modern Android this
            // enters the app's classloader linker namespace and private dependencies
            // such as libhardware.so are intentionally invisible. libadrenotools
            // creates the required linker namespace/hook and redirects Android's
            // system Vulkan loader to the user-selected driver, without root.
            const char* slash = std::strrchr(driverIcdPath, '/');
            const char* driverName = slash ? slash + 1 : driverIcdPath;
            std::string customDir(driverBundlePath);
            if (customDir.back() != '/') customDir.push_back('/');

            void* loaded = adrenotools_open_libvulkan(
                RTLD_NOW | RTLD_LOCAL,
                ADRENOTOOLS_DRIVER_CUSTOM,
                nullptr,
                hookLibDir,
                customDir.c_str(),
                driverName,
                nullptr,
                nullptr
            );
            if (!loaded) {
                openError = "AdrenoTools could not initialize the rootless Turnip loader. "
                            "Check that the APK uses legacy native-library packaging and that the Turnip ZIP contains an arm64 Vulkan .so.";
                return false;
            }
            library = loaded;
            getInstanceProcAddr = load<PFN_vkGetInstanceProcAddr>("vkGetInstanceProcAddr");
            if (!getInstanceProcAddr) {
                openError = "AdrenoTools loaded libvulkan but vkGetInstanceProcAddr is unavailable";
                return false;
            }
#endif
        } else {
            unsetenv("VK_DRIVER_FILES");
            unsetenv("VK_ICD_FILENAMES");
            // Android exposes the Vulkan loader as a public NDK library. Prefer
            // android_dlopen_ext so this also works on devices whose linker
            // namespace does not resolve a plain dlopen() call from the app.
            library = android_dlopen_ext("libvulkan.so", RTLD_NOW | RTLD_LOCAL, nullptr);
            if (!library) {
                library = dlopen("libvulkan.so", RTLD_NOW | RTLD_LOCAL);
            }
            if (!library) {
                library = dlopen("libvulkan.so.1", RTLD_NOW | RTLD_LOCAL);
            }
            if (library) {
                getInstanceProcAddr = load<PFN_vkGetInstanceProcAddr>("vkGetInstanceProcAddr");
            }
            if (!library || !getInstanceProcAddr) {
                const char* error = dlerror();
                openError = std::string("System Vulkan loader unavailable: ") + (error ? error : "vkGetInstanceProcAddr was not exported");
            }
        }
        if (!library || !getInstanceProcAddr) {
            if (openError.empty()) openError = "Vulkan loader entry point unavailable";
            return false;
        }

        // All global Vulkan entry points are obtained through vkGetInstanceProcAddr.
        // This is essential for a directly loaded ICD: vkCreateInstance is normally not
        // exported as a standalone symbol by Mesa/Turnip.
        enumerateInstanceVersion = reinterpret_cast<PFN_vkEnumerateInstanceVersion>(getInstanceProcAddr(nullptr, "vkEnumerateInstanceVersion"));
        enumerateInstanceExtensionProperties = reinterpret_cast<PFN_vkEnumerateInstanceExtensionProperties>(getInstanceProcAddr(nullptr, "vkEnumerateInstanceExtensionProperties"));
        enumerateInstanceLayerProperties = reinterpret_cast<PFN_vkEnumerateInstanceLayerProperties>(getInstanceProcAddr(nullptr, "vkEnumerateInstanceLayerProperties"));
        createInstance = reinterpret_cast<PFN_vkCreateInstance>(getInstanceProcAddr(nullptr, "vkCreateInstance"));

        // vkDestroyInstance is an instance-level command. Android's Vulkan loader
        // correctly rejects querying it with VK_NULL_HANDLE. Load it only after
        // vkCreateInstance has produced a real instance (see loadInstanceFunctions()).
        destroyInstance = nullptr;
        if (!enumerateInstanceExtensionProperties || !createInstance) {
            openError = "Vulkan loader opened, but required global entry points are unavailable";
            return false;
        }
        return true;
    }

    bool loadInstanceFunctions(VkInstance instance) {
        destroyInstance = loadInstance<PFN_vkDestroyInstance>(instance, "vkDestroyInstance");
        enumeratePhysicalDevices = loadInstance<PFN_vkEnumeratePhysicalDevices>(instance, "vkEnumeratePhysicalDevices");
        getPhysicalDeviceProperties = loadInstance<PFN_vkGetPhysicalDeviceProperties>(instance, "vkGetPhysicalDeviceProperties");
        getPhysicalDeviceProperties2 = loadInstance<PFN_vkGetPhysicalDeviceProperties2>(instance, "vkGetPhysicalDeviceProperties2");
        if (!getPhysicalDeviceProperties2) getPhysicalDeviceProperties2 = loadInstance<PFN_vkGetPhysicalDeviceProperties2>(instance, "vkGetPhysicalDeviceProperties2KHR");
        getPhysicalDeviceFeatures = loadInstance<PFN_vkGetPhysicalDeviceFeatures>(instance, "vkGetPhysicalDeviceFeatures");
        getPhysicalDeviceFeatures2 = loadInstance<PFN_vkGetPhysicalDeviceFeatures2>(instance, "vkGetPhysicalDeviceFeatures2");
        if (!getPhysicalDeviceFeatures2) getPhysicalDeviceFeatures2 = loadInstance<PFN_vkGetPhysicalDeviceFeatures2>(instance, "vkGetPhysicalDeviceFeatures2KHR");
        getPhysicalDeviceMemoryProperties = loadInstance<PFN_vkGetPhysicalDeviceMemoryProperties>(instance, "vkGetPhysicalDeviceMemoryProperties");
        getPhysicalDeviceQueueFamilyProperties = loadInstance<PFN_vkGetPhysicalDeviceQueueFamilyProperties>(instance, "vkGetPhysicalDeviceQueueFamilyProperties");
        enumerateDeviceExtensionProperties = loadInstance<PFN_vkEnumerateDeviceExtensionProperties>(instance, "vkEnumerateDeviceExtensionProperties");
        getPhysicalDeviceFormatProperties = loadInstance<PFN_vkGetPhysicalDeviceFormatProperties>(instance, "vkGetPhysicalDeviceFormatProperties");
        createAndroidSurfaceKHR = loadInstance<PFN_vkCreateAndroidSurfaceKHR>(instance, "vkCreateAndroidSurfaceKHR");
        destroySurfaceKHR = loadInstance<PFN_vkDestroySurfaceKHR>(instance, "vkDestroySurfaceKHR");
        getPhysicalDeviceSurfaceCapabilitiesKHR = loadInstance<PFN_vkGetPhysicalDeviceSurfaceCapabilitiesKHR>(instance, "vkGetPhysicalDeviceSurfaceCapabilitiesKHR");
        getPhysicalDeviceSurfaceFormatsKHR = loadInstance<PFN_vkGetPhysicalDeviceSurfaceFormatsKHR>(instance, "vkGetPhysicalDeviceSurfaceFormatsKHR");
        getPhysicalDeviceSurfacePresentModesKHR = loadInstance<PFN_vkGetPhysicalDeviceSurfacePresentModesKHR>(instance, "vkGetPhysicalDeviceSurfacePresentModesKHR");
        getPhysicalDeviceSurfaceSupportKHR = loadInstance<PFN_vkGetPhysicalDeviceSurfaceSupportKHR>(instance, "vkGetPhysicalDeviceSurfaceSupportKHR");
        return enumeratePhysicalDevices && getPhysicalDeviceProperties && getPhysicalDeviceFeatures && getPhysicalDeviceMemoryProperties && getPhysicalDeviceQueueFamilyProperties && enumerateDeviceExtensionProperties && getPhysicalDeviceFormatProperties;
    }
};

std::string escapeJson(const std::string& value) {
    std::string out;
    out.reserve(value.size() + 8);
    for (unsigned char c : value) {
        switch (c) {
            case '\\': out += "\\\\"; break;
            case '"': out += "\\\""; break;
            case '\n': out += "\\n"; break;
            case '\r': out += "\\r"; break;
            case '\t': out += "\\t"; break;
            default: if (c >= 0x20) out += static_cast<char>(c); break;
        }
    }
    return out;
}

std::string versionString(uint32_t value) {
    std::ostringstream out;
    out << VK_API_VERSION_MAJOR(value) << '.' << VK_API_VERSION_MINOR(value) << '.' << VK_API_VERSION_PATCH(value);
    return out.str();
}

std::string jsonString(const std::string& value) { return "\"" + escapeJson(value) + "\""; }
std::string jsonBool(bool value) { return value ? "true" : "false"; }

std::string formatName(int32_t value) {
    static const std::pair<int32_t, const char*> names[] = {
        {0, "VK_FORMAT_UNDEFINED"}, {9, "VK_FORMAT_R8_UNORM"}, {15, "VK_FORMAT_R8_SRGB"}, {16, "VK_FORMAT_R8G8_UNORM"}, {22, "VK_FORMAT_R8G8_SRGB"},
        {23, "VK_FORMAT_R8G8B8_UNORM"}, {29, "VK_FORMAT_R8G8B8_SRGB"}, {30, "VK_FORMAT_B8G8R8_UNORM"}, {36, "VK_FORMAT_B8G8R8_SRGB"},
        {37, "VK_FORMAT_R8G8B8A8_UNORM"}, {43, "VK_FORMAT_R8G8B8A8_SRGB"}, {44, "VK_FORMAT_B8G8R8A8_UNORM"}, {50, "VK_FORMAT_B8G8R8A8_SRGB"},
        {51, "VK_FORMAT_A8B8G8R8_UNORM_PACK32"}, {57, "VK_FORMAT_A8B8G8R8_SRGB_PACK32"}, {58, "VK_FORMAT_A2R10G10B10_UNORM_PACK32"},
        {64, "VK_FORMAT_A2B10G10R10_UNORM_PACK32"}, {70, "VK_FORMAT_R16_UNORM"}, {76, "VK_FORMAT_R16_SFLOAT"},
        {77, "VK_FORMAT_R16G16_UNORM"}, {83, "VK_FORMAT_R16G16_SFLOAT"}, {84, "VK_FORMAT_R16G16B16A16_UNORM"},
        {90, "VK_FORMAT_R16G16B16A16_SFLOAT"}, {91, "VK_FORMAT_R32_UINT"}, {92, "VK_FORMAT_R32_SINT"}, {93, "VK_FORMAT_R32_SFLOAT"},
        {94, "VK_FORMAT_R32G32_UINT"}, {95, "VK_FORMAT_R32G32_SINT"}, {96, "VK_FORMAT_R32G32_SFLOAT"},
        {97, "VK_FORMAT_R32G32B32_UINT"}, {98, "VK_FORMAT_R32G32B32_SINT"}, {99, "VK_FORMAT_R32G32B32_SFLOAT"},
        {100, "VK_FORMAT_R32G32B32A32_UINT"}, {101, "VK_FORMAT_R32G32B32A32_SINT"}, {102, "VK_FORMAT_R32G32B32A32_SFLOAT"},
        {124, "VK_FORMAT_D16_UNORM"}, {126, "VK_FORMAT_D32_SFLOAT"}, {129, "VK_FORMAT_D24_UNORM_S8_UINT"}, {130, "VK_FORMAT_D32_SFLOAT_S8_UINT"}
    };
    static const std::pair<int32_t, const char*> compressed[] = {
        {131,"VK_FORMAT_BC1_RGB_UNORM_BLOCK"},{132,"VK_FORMAT_BC1_RGB_SRGB_BLOCK"},{133,"VK_FORMAT_BC1_RGBA_UNORM_BLOCK"},{134,"VK_FORMAT_BC1_RGBA_SRGB_BLOCK"},
        {135,"VK_FORMAT_BC2_UNORM_BLOCK"},{136,"VK_FORMAT_BC2_SRGB_BLOCK"},{137,"VK_FORMAT_BC3_UNORM_BLOCK"},{138,"VK_FORMAT_BC3_SRGB_BLOCK"},
        {139,"VK_FORMAT_BC4_UNORM_BLOCK"},{140,"VK_FORMAT_BC4_SNORM_BLOCK"},{141,"VK_FORMAT_BC5_UNORM_BLOCK"},{142,"VK_FORMAT_BC5_SNORM_BLOCK"},
        {143,"VK_FORMAT_BC6H_UFLOAT_BLOCK"},{144,"VK_FORMAT_BC6H_SFLOAT_BLOCK"},{145,"VK_FORMAT_BC7_UNORM_BLOCK"},{146,"VK_FORMAT_BC7_SRGB_BLOCK"},
        {147,"VK_FORMAT_ETC2_R8G8B8_UNORM_BLOCK"},{148,"VK_FORMAT_ETC2_R8G8B8_SRGB_BLOCK"},{149,"VK_FORMAT_ETC2_R8G8B8A1_UNORM_BLOCK"},{150,"VK_FORMAT_ETC2_R8G8B8A1_SRGB_BLOCK"},
        {151,"VK_FORMAT_ETC2_R8G8B8A8_UNORM_BLOCK"},{152,"VK_FORMAT_ETC2_R8G8B8A8_SRGB_BLOCK"},{153,"VK_FORMAT_EAC_R11_UNORM_BLOCK"},{154,"VK_FORMAT_EAC_R11_SNORM_BLOCK"},
        {155,"VK_FORMAT_EAC_R11G11_UNORM_BLOCK"},{156,"VK_FORMAT_EAC_R11G11_SNORM_BLOCK"},
        {157,"VK_FORMAT_ASTC_4x4_UNORM_BLOCK"},{158,"VK_FORMAT_ASTC_4x4_SRGB_BLOCK"},{159,"VK_FORMAT_ASTC_5x4_UNORM_BLOCK"},{160,"VK_FORMAT_ASTC_5x4_SRGB_BLOCK"},
        {161,"VK_FORMAT_ASTC_5x5_UNORM_BLOCK"},{162,"VK_FORMAT_ASTC_5x5_SRGB_BLOCK"},{163,"VK_FORMAT_ASTC_6x5_UNORM_BLOCK"},{164,"VK_FORMAT_ASTC_6x5_SRGB_BLOCK"},
        {165,"VK_FORMAT_ASTC_6x6_UNORM_BLOCK"},{166,"VK_FORMAT_ASTC_6x6_SRGB_BLOCK"},{167,"VK_FORMAT_ASTC_8x5_UNORM_BLOCK"},{168,"VK_FORMAT_ASTC_8x5_SRGB_BLOCK"},
        {169,"VK_FORMAT_ASTC_8x6_UNORM_BLOCK"},{170,"VK_FORMAT_ASTC_8x6_SRGB_BLOCK"},{171,"VK_FORMAT_ASTC_8x8_UNORM_BLOCK"},{172,"VK_FORMAT_ASTC_8x8_SRGB_BLOCK"},
        {173,"VK_FORMAT_ASTC_10x5_UNORM_BLOCK"},{174,"VK_FORMAT_ASTC_10x5_SRGB_BLOCK"},{175,"VK_FORMAT_ASTC_10x6_UNORM_BLOCK"},{176,"VK_FORMAT_ASTC_10x6_SRGB_BLOCK"},
        {177,"VK_FORMAT_ASTC_10x8_UNORM_BLOCK"},{178,"VK_FORMAT_ASTC_10x8_SRGB_BLOCK"},{179,"VK_FORMAT_ASTC_10x10_UNORM_BLOCK"},{180,"VK_FORMAT_ASTC_10x10_SRGB_BLOCK"},
        {181,"VK_FORMAT_ASTC_12x10_UNORM_BLOCK"},{182,"VK_FORMAT_ASTC_12x10_SRGB_BLOCK"},{183,"VK_FORMAT_ASTC_12x12_UNORM_BLOCK"},{184,"VK_FORMAT_ASTC_12x12_SRGB_BLOCK"},
        {185,"VK_FORMAT_G8B8G8R8_422_UNORM"},{186,"VK_FORMAT_B8G8R8G8_422_UNORM"},{187,"VK_FORMAT_G8_B8_R8_3PLANE_420_UNORM"},{188,"VK_FORMAT_G8_B8R8_2PLANE_420_UNORM"},
        {189,"VK_FORMAT_G8_B8_R8_3PLANE_422_UNORM"},{190,"VK_FORMAT_G8_B8R8_2PLANE_422_UNORM"},{191,"VK_FORMAT_G8_B8_R8_3PLANE_444_UNORM"},{192,"VK_FORMAT_R10X6_UNORM_PACK16"},
        {193,"VK_FORMAT_R10X6G10X6_UNORM_2PACK16"},{194,"VK_FORMAT_R10X6G10X6B10X6A10X6_UNORM_4PACK16"},{195,"VK_FORMAT_R12X4_UNORM_PACK16"},{196,"VK_FORMAT_R12X4G12X4_UNORM_2PACK16"},
        {197,"VK_FORMAT_R12X4G12X4B12X4A12X4_UNORM_4PACK16"},{198,"VK_FORMAT_G10X6B10X6G10X6R10X6_422_UNORM_4PACK16"},{199,"VK_FORMAT_B10X6G10X6R10X6G10X6_422_UNORM_4PACK16"},
        {200,"VK_FORMAT_G12X4B12X4G12X4R12X4_422_UNORM_4PACK16"},{201,"VK_FORMAT_B12X4G12X4R12X4G12X4_422_UNORM_4PACK16"},
        {202,"VK_FORMAT_G16B16G16R16_422_UNORM"},{203,"VK_FORMAT_B16G16R16G16_422_UNORM"},{204,"VK_FORMAT_G16_B16_R16_3PLANE_420_UNORM"},
        {205,"VK_FORMAT_G16_B16R16_2PLANE_420_UNORM"},{206,"VK_FORMAT_G16_B16_R16_3PLANE_422_UNORM"},{207,"VK_FORMAT_G16_B16R16_2PLANE_422_UNORM"},{208,"VK_FORMAT_G16_B16_R16_3PLANE_444_UNORM"}
    };
    for (const auto& item : compressed) if (item.first == value) return item.second;
    for (const auto& item : names) if (item.first == value) return item.second;
    return "VK_FORMAT_" + std::to_string(value);
}

std::string colorSpaceName(int32_t value) {
    switch (value) {
        case VK_COLOR_SPACE_SRGB_NONLINEAR_KHR: return "VK_COLOR_SPACE_SRGB_NONLINEAR_KHR";
        case VK_COLOR_SPACE_DISPLAY_P3_NONLINEAR_EXT: return "VK_COLOR_SPACE_DISPLAY_P3_NONLINEAR_EXT";
        case VK_COLOR_SPACE_EXTENDED_SRGB_LINEAR_EXT: return "VK_COLOR_SPACE_EXTENDED_SRGB_LINEAR_EXT";
        case VK_COLOR_SPACE_DISPLAY_P3_LINEAR_EXT: return "VK_COLOR_SPACE_DISPLAY_P3_LINEAR_EXT";
        case VK_COLOR_SPACE_DCI_P3_NONLINEAR_EXT: return "VK_COLOR_SPACE_DCI_P3_NONLINEAR_EXT";
        case VK_COLOR_SPACE_BT709_LINEAR_EXT: return "VK_COLOR_SPACE_BT709_LINEAR_EXT";
        case VK_COLOR_SPACE_BT709_NONLINEAR_EXT: return "VK_COLOR_SPACE_BT709_NONLINEAR_EXT";
        case VK_COLOR_SPACE_BT2020_LINEAR_EXT: return "VK_COLOR_SPACE_BT2020_LINEAR_EXT";
        case VK_COLOR_SPACE_HDR10_ST2084_EXT: return "VK_COLOR_SPACE_HDR10_ST2084_EXT";
        case VK_COLOR_SPACE_DOLBYVISION_EXT: return "VK_COLOR_SPACE_DOLBYVISION_EXT";
        case VK_COLOR_SPACE_HDR10_HLG_EXT: return "VK_COLOR_SPACE_HDR10_HLG_EXT";
        case VK_COLOR_SPACE_ADOBERGB_LINEAR_EXT: return "VK_COLOR_SPACE_ADOBERGB_LINEAR_EXT";
        case VK_COLOR_SPACE_ADOBERGB_NONLINEAR_EXT: return "VK_COLOR_SPACE_ADOBERGB_NONLINEAR_EXT";
        case VK_COLOR_SPACE_PASS_THROUGH_EXT: return "VK_COLOR_SPACE_PASS_THROUGH_EXT";
        case VK_COLOR_SPACE_EXTENDED_SRGB_NONLINEAR_EXT: return "VK_COLOR_SPACE_EXTENDED_SRGB_NONLINEAR_EXT";
        case VK_COLOR_SPACE_DISPLAY_NATIVE_AMD: return "VK_COLOR_SPACE_DISPLAY_NATIVE_AMD";
        default: return "VK_COLOR_SPACE_" + std::to_string(value);
    }
}


std::string colorSpaceDescription(int32_t value) {
    switch (value) {
        case VK_COLOR_SPACE_SRGB_NONLINEAR_KHR: return "BT.709 primaries · D65 · sRGB transfer";
        case VK_COLOR_SPACE_DISPLAY_P3_NONLINEAR_EXT: return "Display-P3 primaries · D65 · Display-P3 transfer";
        case VK_COLOR_SPACE_EXTENDED_SRGB_LINEAR_EXT: return "sRGB primaries · D65 · linear transfer";
        case VK_COLOR_SPACE_DISPLAY_P3_LINEAR_EXT: return "Display-P3 primaries · D65 · linear transfer";
        case VK_COLOR_SPACE_DCI_P3_NONLINEAR_EXT: return "DCI-P3 primaries · DCI white point · DCI-P3 transfer";
        case VK_COLOR_SPACE_BT709_LINEAR_EXT: return "BT.709 primaries · D65 · linear transfer";
        case VK_COLOR_SPACE_BT709_NONLINEAR_EXT: return "BT.709 primaries · D65 · BT.709 transfer";
        case VK_COLOR_SPACE_BT2020_LINEAR_EXT: return "BT.2020 primaries · D65 · linear transfer";
        case VK_COLOR_SPACE_HDR10_ST2084_EXT: return "BT.2020 primaries · D65 · ST2084 PQ";
        case VK_COLOR_SPACE_DOLBYVISION_EXT: return "Dolby Vision presentation color space";
        case VK_COLOR_SPACE_HDR10_HLG_EXT: return "BT.2020 primaries · D65 · HLG";
        case VK_COLOR_SPACE_ADOBERGB_LINEAR_EXT: return "Adobe RGB primaries · D65 · linear transfer";
        case VK_COLOR_SPACE_ADOBERGB_NONLINEAR_EXT: return "Adobe RGB primaries · D65 · Adobe RGB transfer";
        case VK_COLOR_SPACE_PASS_THROUGH_EXT: return "Color components passed through without an explicitly enumerated color space";
        case VK_COLOR_SPACE_EXTENDED_SRGB_NONLINEAR_EXT: return "sRGB primaries · D65 · scRGB transfer";
        case VK_COLOR_SPACE_DISPLAY_NATIVE_AMD: return "Display native color space";
        default: return "No canonical description available";
    }
}

std::string colorSpaceClass(int32_t value) {
    switch (value) {
        case VK_COLOR_SPACE_HDR10_ST2084_EXT: return "HDR10 / PQ";
        case VK_COLOR_SPACE_HDR10_HLG_EXT: return "HDR10 / HLG";
        case VK_COLOR_SPACE_DOLBYVISION_EXT: return "Dolby Vision";
        case VK_COLOR_SPACE_BT2020_LINEAR_EXT: return "BT.2020";
        case VK_COLOR_SPACE_DISPLAY_P3_NONLINEAR_EXT: return "Display-P3";
        case VK_COLOR_SPACE_DISPLAY_P3_LINEAR_EXT: return "Display-P3 / Linear";
        case VK_COLOR_SPACE_DCI_P3_NONLINEAR_EXT: return "DCI-P3";
        case VK_COLOR_SPACE_BT709_LINEAR_EXT: return "BT.709 / Linear";
        case VK_COLOR_SPACE_BT709_NONLINEAR_EXT: return "BT.709";
        case VK_COLOR_SPACE_ADOBERGB_LINEAR_EXT: return "Adobe RGB / Linear";
        case VK_COLOR_SPACE_ADOBERGB_NONLINEAR_EXT: return "Adobe RGB";
        case VK_COLOR_SPACE_EXTENDED_SRGB_LINEAR_EXT: return "scRGB / Linear";
        case VK_COLOR_SPACE_EXTENDED_SRGB_NONLINEAR_EXT: return "scRGB";
        case VK_COLOR_SPACE_PASS_THROUGH_EXT: return "Pass-through";
        case VK_COLOR_SPACE_DISPLAY_NATIVE_AMD: return "Display Native";
        default: return "sRGB";
    }
}

std::string featureName(size_t index) {
    static const char* names[] = {
        "robustBufferAccess", "fullDrawIndexUint32", "imageCubeArray", "independentBlend", "geometryShader", "tessellationShader",
        "sampleRateShading", "dualSrcBlend", "logicOp", "multiDrawIndirect", "drawIndirectFirstInstance", "depthClamp",
        "depthBiasClamp", "fillModeNonSolid", "depthBounds", "wideLines", "largePoints", "alphaToOne", "multiViewport",
        "samplerAnisotropy", "textureCompressionETC2", "textureCompressionASTC_LDR", "textureCompressionBC", "occlusionQueryPrecise",
        "pipelineStatisticsQuery", "vertexPipelineStoresAndAtomics", "fragmentStoresAndAtomics", "shaderTessellationAndGeometryPointSize",
        "shaderImageGatherExtended", "shaderStorageImageExtendedFormats", "shaderStorageImageMultisample", "shaderStorageImageReadWithoutFormat",
        "shaderStorageImageWriteWithoutFormat", "shaderUniformBufferArrayDynamicIndexing", "shaderSampledImageArrayDynamicIndexing",
        "shaderStorageBufferArrayDynamicIndexing", "shaderStorageImageArrayDynamicIndexing", "shaderClipDistance", "shaderCullDistance",
        "shaderFloat64", "shaderInt64", "shaderInt16", "shaderResourceResidency", "shaderResourceMinLod", "sparseBinding", "sparseResidencyBuffer",
        "sparseResidencyImage2D", "sparseResidencyImage3D", "sparseResidency2Samples", "sparseResidency4Samples", "sparseResidency8Samples",
        "sparseResidency16Samples", "sparseResidencyAliased", "variableMultisampleRate", "inheritedQueries"
    };
    return index < sizeof(names) / sizeof(names[0]) ? names[index] : "feature_" + std::to_string(index);
}

std::vector<VkExtensionProperties> instanceExtensions(VulkanApi& api) {
    uint32_t count = 0;
    if (api.enumerateInstanceExtensionProperties(nullptr, &count, nullptr) != VK_SUCCESS) return {};
    std::vector<VkExtensionProperties> values(count);
    if (count && api.enumerateInstanceExtensionProperties(nullptr, &count, values.data()) != VK_SUCCESS) return {};
    values.resize(count);
    return values;
}

std::vector<VkLayerProperties> instanceLayers(VulkanApi& api) {
    uint32_t count = 0;
    if (!api.enumerateInstanceLayerProperties || api.enumerateInstanceLayerProperties(&count, nullptr) != VK_SUCCESS) return {};
    std::vector<VkLayerProperties> values(count);
    if (count && api.enumerateInstanceLayerProperties(&count, values.data()) != VK_SUCCESS) return {};
    values.resize(count);
    return values;
}

std::string layersJson(const std::vector<VkLayerProperties>& values) {
    std::ostringstream out;
    out << '[';
    for (size_t i = 0; i < values.size(); ++i) {
        if (i) out << ',';
        out << "{\"name\":" << jsonString(values[i].layerName)
            << ",\"description\":" << jsonString(values[i].description)
            << ",\"specVersion\":" << values[i].specVersion
            << ",\"implementationVersion\":" << values[i].implementationVersion << '}';
    }
    out << ']';
    return out.str();
}

std::vector<VkExtensionProperties> deviceExtensions(VulkanApi& api, VkPhysicalDevice device) {
    uint32_t count = 0;
    if (api.enumerateDeviceExtensionProperties(device, nullptr, &count, nullptr) != VK_SUCCESS) return {};
    std::vector<VkExtensionProperties> values(count);
    if (count && api.enumerateDeviceExtensionProperties(device, nullptr, &count, values.data()) != VK_SUCCESS) return {};
    values.resize(count);
    return values;
}

bool hasExtension(const std::vector<VkExtensionProperties>& values, const char* name) {
    return std::any_of(values.begin(), values.end(), [name](const VkExtensionProperties& value) { return std::strcmp(value.extensionName, name) == 0; });
}


std::vector<std::string> versionedFeatureNames(uint32_t version) {
    if (version == 11) return {
        "storageBuffer16BitAccess", "uniformAndStorageBuffer16BitAccess", "storagePushConstant16", "storageInputOutput16", "multiview",
        "multiviewGeometryShader", "multiviewTessellationShader", "variablePointersStorageBuffer", "variablePointers", "protectedMemory",
        "samplerYcbcrConversion", "shaderDrawParameters"
    };
    if (version == 12) return {
        "samplerMirrorClampToEdge", "drawIndirectCount", "storageBuffer8BitAccess", "uniformAndStorageBuffer8BitAccess", "storagePushConstant8",
        "shaderBufferInt64Atomics", "shaderSharedInt64Atomics", "shaderFloat16", "shaderInt8", "descriptorIndexing",
        "shaderInputAttachmentArrayDynamicIndexing", "shaderUniformTexelBufferArrayDynamicIndexing", "shaderStorageTexelBufferArrayDynamicIndexing",
        "shaderUniformBufferArrayNonUniformIndexing", "shaderSampledImageArrayNonUniformIndexing", "shaderStorageBufferArrayNonUniformIndexing",
        "shaderStorageImageArrayNonUniformIndexing", "shaderInputAttachmentArrayNonUniformIndexing", "shaderUniformTexelBufferArrayNonUniformIndexing",
        "shaderStorageTexelBufferArrayNonUniformIndexing", "descriptorBindingUniformBufferUpdateAfterBind", "descriptorBindingSampledImageUpdateAfterBind",
        "descriptorBindingStorageImageUpdateAfterBind", "descriptorBindingStorageBufferUpdateAfterBind", "descriptorBindingUniformTexelBufferUpdateAfterBind",
        "descriptorBindingStorageTexelBufferUpdateAfterBind", "descriptorBindingUpdateUnusedWhilePending", "descriptorBindingPartiallyBound",
        "descriptorBindingVariableDescriptorCount", "runtimeDescriptorArray", "samplerFilterMinmax", "scalarBlockLayout", "imagelessFramebuffer",
        "uniformBufferStandardLayout", "shaderSubgroupExtendedTypes", "separateDepthStencilLayouts", "hostQueryReset", "timelineSemaphore",
        "bufferDeviceAddress", "bufferDeviceAddressCaptureReplay", "bufferDeviceAddressMultiDevice", "vulkanMemoryModel",
        "vulkanMemoryModelDeviceScope", "vulkanMemoryModelAvailabilityVisibilityChains", "shaderOutputViewportIndex", "shaderOutputLayer",
        "subgroupBroadcastDynamicId"
    };
    if (version == 13) return {
        "robustImageAccess", "inlineUniformBlock", "descriptorBindingInlineUniformBlockUpdateAfterBind", "pipelineCreationCacheControl",
        "privateData", "shaderDemoteToHelperInvocation", "shaderTerminateInvocation", "subgroupSizeControl", "computeFullSubgroups",
        "synchronization2", "textureCompressionASTC_HDR", "shaderZeroInitializeWorkgroupMemory", "dynamicRendering", "shaderIntegerDotProduct",
        "maintenance4"
    };
    return {
        "globalPriorityQuery", "shaderSubgroupRotate", "shaderSubgroupRotateClustered", "shaderFloatControls2", "shaderExpectAssume",
        "rectangularLines", "bresenhamLines", "smoothLines", "stippledRectangularLines", "stippledBresenhamLines", "stippledSmoothLines",
        "vertexAttributeInstanceRateDivisor", "vertexAttributeInstanceRateZeroDivisor", "indexTypeUint8", "dynamicRenderingLocalRead",
        "maintenance5", "maintenance6", "pipelineProtectedAccess", "pipelineRobustness", "hostImageCopy", "pushDescriptor"
    };
}

void appendVersionedFeatures(std::ostringstream& out, uint32_t apiVersion, VulkanApi& api, VkPhysicalDevice device) {
    if (!api.getPhysicalDeviceFeatures2 || VK_API_VERSION_MINOR(apiVersion) < 1) {
        out << "[]";
        return;
    }
    VkFeatureChain v11{};
    VkFeatureChain v12{};
    VkFeatureChain v13{};
    VkFeatureChain v14{};
    VkPhysicalDeviceFeatures2 base{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, nullptr, {}};
    const uint32_t minor = VK_API_VERSION_MINOR(apiVersion);
    v11.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_1_FEATURES;
    v12.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_2_FEATURES;
    v13.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_3_FEATURES;
    v14.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_4_FEATURES;
    if (minor >= 2) v11.pNext = &v12;
    if (minor >= 3) v12.pNext = &v13;
    if (minor >= 4) v13.pNext = &v14;
    if (minor >= 1) base.pNext = &v11;
    api.getPhysicalDeviceFeatures2(device, &base);
    out << '[';
    bool first = true;
    const VkFeatureChain* chains[] = {&v11, &v12, &v13, &v14};
    const uint32_t versions[] = {11, 12, 13, 14};
    for (size_t c = 0; c < 4; ++c) {
        if (minor < versions[c]) break;
        const auto names = versionedFeatureNames(versions[c]);
        for (size_t i = 0; i < names.size(); ++i) {
            if (!first) out << ',';
            first = false;
            out << "{\"name\":" << jsonString("Vulkan " + std::to_string(versions[c]) + " · " + names[i]) << ",\"supported\":" << jsonBool(chains[c]->values[i] == VK_TRUE) << '}';
        }
    }
    out << ']';
}

std::string hexBytes(const uint8_t* data, size_t count) {
    static const char* digits = "0123456789ABCDEF";
    std::string out;
    out.reserve(count * 2);
    for (size_t i = 0; i < count; ++i) { out.push_back(digits[data[i] >> 4]); out.push_back(digits[data[i] & 0x0F]); }
    return out;
}

void appendProperty(std::ostringstream& out, bool& first, const char* section, const char* name, const std::string& value) {
    if (!first) out << ',';
    first = false;
    out << "{\"section\":" << jsonString(section) << ",\"name\":" << jsonString(name) << ",\"value\":" << jsonString(value) << '}';
}
void appendProperty(std::ostringstream& out, bool& first, const char* section, const char* name, uint64_t value) { appendProperty(out, first, section, name, std::to_string(value)); }
void appendProperty(std::ostringstream& out, bool& first, const char* section, const char* name, uint32_t value) { appendProperty(out, first, section, name, std::to_string(value)); }
void appendBoolProperty(std::ostringstream& out, bool& first, const char* section, const char* name, VkBool32 value) { appendProperty(out, first, section, name, value == VK_TRUE ? "true" : "false"); }
void appendProperty(std::ostringstream& out, bool& first, const char* section, const char* name, const char* value) { appendProperty(out, first, section, name, std::string(value ? value : "")); }

void appendCoreProperties(std::ostringstream& out, uint32_t apiVersion, VulkanApi& api, VkPhysicalDevice device, const std::vector<VkExtensionProperties>& devExts) {
    out << '[';
    bool first = true;
    if (!api.getPhysicalDeviceProperties2 || VK_API_VERSION_MINOR(apiVersion) < 1) { out << ']'; return; }

    VkPhysicalDeviceProperties2 base{};
    base.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2;
    VkPhysicalDeviceVulkan11Properties p11{}; p11.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_1_PROPERTIES;
    VkPhysicalDeviceVulkan12Properties p12{}; p12.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_2_PROPERTIES;
    VkPhysicalDeviceVulkan13Properties p13{}; p13.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_3_PROPERTIES;
    VkPhysicalDeviceFragmentDensityMapPropertiesEXT fdm{}; fdm.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_PROPERTIES_EXT;
    VkPhysicalDeviceFragmentDensityMap2PropertiesEXT fdm2{}; fdm2.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_2_PROPERTIES_EXT;
    const bool hasFdm = hasExtension(devExts, "VK_EXT_fragment_density_map");
    const bool hasFdm2 = hasExtension(devExts, "VK_EXT_fragment_density_map2");
    const uint32_t minor = VK_API_VERSION_MINOR(apiVersion);
    if (minor >= 1) base.pNext = &p11;
    if (minor >= 2) p11.pNext = &p12;
    if (minor >= 3) p12.pNext = &p13;
    if (hasFdm) { if (minor >= 3) p13.pNext = &fdm; else if (minor >= 2) p12.pNext = &fdm; else p11.pNext = &fdm; }
    if (hasFdm2) { if (hasFdm) fdm.pNext = &fdm2; else if (minor >= 3) p13.pNext = &fdm2; else if (minor >= 2) p12.pNext = &fdm2; else p11.pNext = &fdm2; }
    api.getPhysicalDeviceProperties2(device, &base);

    if (minor >= 1) {
        appendProperty(out, first, "Core 1.1", "deviceUUID", hexBytes(p11.deviceUUID, 16));
        appendProperty(out, first, "Core 1.1", "driverUUID", hexBytes(p11.driverUUID, 16));
        appendProperty(out, first, "Core 1.1", "deviceLUID", hexBytes(p11.deviceLUID, 8));
        appendProperty(out, first, "Core 1.1", "deviceNodeMask", p11.deviceNodeMask);
        appendBoolProperty(out, first, "Core 1.1", "deviceLUIDValid", p11.deviceLUIDValid);
        appendProperty(out, first, "Core 1.1", "subgroupSize", p11.subgroupSize);
        appendProperty(out, first, "Core 1.1", "subgroupSupportedStages", p11.subgroupSupportedStages);
        appendProperty(out, first, "Core 1.1", "subgroupSupportedOperations", p11.subgroupSupportedOperations);
        appendBoolProperty(out, first, "Core 1.1", "subgroupQuadOperationsInAllStages", p11.subgroupQuadOperationsInAllStages);
        appendProperty(out, first, "Core 1.1", "pointClippingBehavior", p11.pointClippingBehavior);
        appendProperty(out, first, "Core 1.1", "maxMultiviewViewCount", p11.maxMultiviewViewCount);
        appendProperty(out, first, "Core 1.1", "maxMultiviewInstanceIndex", p11.maxMultiviewInstanceIndex);
        appendBoolProperty(out, first, "Core 1.1", "protectedNoFault", p11.protectedNoFault);
        appendProperty(out, first, "Core 1.1", "maxPerSetDescriptors", p11.maxPerSetDescriptors);
        appendProperty(out, first, "Core 1.1", "maxMemoryAllocationSize", p11.maxMemoryAllocationSize);
    }
    if (minor >= 2) {
        appendProperty(out, first, "Core 1.2", "driverID", p12.driverID);
        appendProperty(out, first, "Core 1.2", "driverName", p12.driverName);
        appendProperty(out, first, "Core 1.2", "driverInfo", p12.driverInfo);
        appendProperty(out, first, "Core 1.2", "conformanceVersion", std::to_string(p12.conformanceVersion[0]) + "." + std::to_string(p12.conformanceVersion[1]) + "." + std::to_string(p12.conformanceVersion[2]) + "." + std::to_string(p12.conformanceVersion[3]));
        appendProperty(out, first, "Core 1.2", "denormBehaviorIndependence", p12.denormBehaviorIndependence);
        appendProperty(out, first, "Core 1.2", "roundingModeIndependence", p12.roundingModeIndependence);
        const struct BoolField { const char* name; VkBool32 value; } bools[] = {
            {"shaderSignedZeroInfNanPreserveFloat16",p12.shaderSignedZeroInfNanPreserveFloat16},{"shaderSignedZeroInfNanPreserveFloat32",p12.shaderSignedZeroInfNanPreserveFloat32},{"shaderSignedZeroInfNanPreserveFloat64",p12.shaderSignedZeroInfNanPreserveFloat64},{"shaderDenormPreserveFloat16",p12.shaderDenormPreserveFloat16},{"shaderDenormPreserveFloat32",p12.shaderDenormPreserveFloat32},{"shaderDenormPreserveFloat64",p12.shaderDenormPreserveFloat64},{"shaderDenormFlushToZeroFloat16",p12.shaderDenormFlushToZeroFloat16},{"shaderDenormFlushToZeroFloat32",p12.shaderDenormFlushToZeroFloat32},{"shaderDenormFlushToZeroFloat64",p12.shaderDenormFlushToZeroFloat64},{"shaderRoundingModeRTEFloat16",p12.shaderRoundingModeRTEFloat16},{"shaderRoundingModeRTEFloat32",p12.shaderRoundingModeRTEFloat32},{"shaderRoundingModeRTEFloat64",p12.shaderRoundingModeRTEFloat64},{"shaderRoundingModeRTZFloat16",p12.shaderRoundingModeRTZFloat16},{"shaderRoundingModeRTZFloat32",p12.shaderRoundingModeRTZFloat32},{"shaderRoundingModeRTZFloat64",p12.shaderRoundingModeRTZFloat64},{"shaderUniformBufferArrayNonUniformIndexingNative",p12.shaderUniformBufferArrayNonUniformIndexingNative},{"shaderSampledImageArrayNonUniformIndexingNative",p12.shaderSampledImageArrayNonUniformIndexingNative},{"shaderStorageBufferArrayNonUniformIndexingNative",p12.shaderStorageBufferArrayNonUniformIndexingNative},{"shaderStorageImageArrayNonUniformIndexingNative",p12.shaderStorageImageArrayNonUniformIndexingNative},{"shaderInputAttachmentArrayNonUniformIndexingNative",p12.shaderInputAttachmentArrayNonUniformIndexingNative},{"robustBufferAccessUpdateAfterBind",p12.robustBufferAccessUpdateAfterBind},{"quadDivergentImplicitLod",p12.quadDivergentImplicitLod}
        };
        for (const auto& f : bools) appendBoolProperty(out, first, "Core 1.2", f.name, f.value);
        const struct U32Field { const char* name; uint32_t value; } u32s[] = {
            {"maxUpdateAfterBindDescriptorsInAllPools",p12.maxUpdateAfterBindDescriptorsInAllPools},{"maxPerStageDescriptorUpdateAfterBindSamplers",p12.maxPerStageDescriptorUpdateAfterBindSamplers},{"maxPerStageDescriptorUpdateAfterBindUniformBuffers",p12.maxPerStageDescriptorUpdateAfterBindUniformBuffers},{"maxPerStageDescriptorUpdateAfterBindStorageBuffers",p12.maxPerStageDescriptorUpdateAfterBindStorageBuffers},{"maxPerStageDescriptorUpdateAfterBindSampledImages",p12.maxPerStageDescriptorUpdateAfterBindSampledImages},{"maxPerStageDescriptorUpdateAfterBindStorageImages",p12.maxPerStageDescriptorUpdateAfterBindStorageImages},{"maxPerStageDescriptorUpdateAfterBindInputAttachments",p12.maxPerStageDescriptorUpdateAfterBindInputAttachments},{"maxPerStageUpdateAfterBindResources",p12.maxPerStageUpdateAfterBindResources},{"maxDescriptorSetUpdateAfterBindSamplers",p12.maxDescriptorSetUpdateAfterBindSamplers},{"maxDescriptorSetUpdateAfterBindUniformBuffers",p12.maxDescriptorSetUpdateAfterBindUniformBuffers},{"maxDescriptorSetUpdateAfterBindUniformBuffersDynamic",p12.maxDescriptorSetUpdateAfterBindUniformBuffersDynamic},{"maxDescriptorSetUpdateAfterBindStorageBuffers",p12.maxDescriptorSetUpdateAfterBindStorageBuffers},{"maxDescriptorSetUpdateAfterBindStorageBuffersDynamic",p12.maxDescriptorSetUpdateAfterBindStorageBuffersDynamic},{"maxDescriptorSetUpdateAfterBindSampledImages",p12.maxDescriptorSetUpdateAfterBindSampledImages},{"maxDescriptorSetUpdateAfterBindStorageImages",p12.maxDescriptorSetUpdateAfterBindStorageImages},{"maxDescriptorSetUpdateAfterBindInputAttachments",p12.maxDescriptorSetUpdateAfterBindInputAttachments},{"supportedDepthResolveModes",p12.supportedDepthResolveModes},{"supportedStencilResolveModes",p12.supportedStencilResolveModes},{"framebufferIntegerColorSampleCounts",p12.framebufferIntegerColorSampleCounts}
        };
        for (const auto& f : u32s) appendProperty(out, first, "Core 1.2", f.name, f.value);
        appendBoolProperty(out, first, "Core 1.2", "independentResolveNone", p12.independentResolveNone);
        appendBoolProperty(out, first, "Core 1.2", "independentResolve", p12.independentResolve);
        appendBoolProperty(out, first, "Core 1.2", "filterMinmaxSingleComponentFormats", p12.filterMinmaxSingleComponentFormats);
        appendBoolProperty(out, first, "Core 1.2", "filterMinmaxImageComponentMapping", p12.filterMinmaxImageComponentMapping);
        appendProperty(out, first, "Core 1.2", "maxTimelineSemaphoreValueDifference", p12.maxTimelineSemaphoreValueDifference);
    }
    if (minor >= 3) {
        const struct U32Field { const char* name; uint32_t value; } u32s[] = {
            {"minSubgroupSize",p13.minSubgroupSize},{"maxSubgroupSize",p13.maxSubgroupSize},{"maxComputeWorkgroupSubgroups",p13.maxComputeWorkgroupSubgroups},{"requiredSubgroupSizeStages",p13.requiredSubgroupSizeStages},{"maxInlineUniformBlockSize",p13.maxInlineUniformBlockSize},{"maxPerStageDescriptorInlineUniformBlocks",p13.maxPerStageDescriptorInlineUniformBlocks},{"maxPerStageDescriptorUpdateAfterBindInlineUniformBlocks",p13.maxPerStageDescriptorUpdateAfterBindInlineUniformBlocks},{"maxDescriptorSetInlineUniformBlocks",p13.maxDescriptorSetInlineUniformBlocks},{"maxDescriptorSetUpdateAfterBindInlineUniformBlocks",p13.maxDescriptorSetUpdateAfterBindInlineUniformBlocks},{"maxInlineUniformTotalSize",p13.maxInlineUniformTotalSize}
        };
        for (const auto& f : u32s) appendProperty(out, first, "Core 1.3", f.name, f.value);
        const struct BoolField { const char* name; VkBool32 value; } bools[] = {
            {"integerDotProduct8BitUnsignedAccelerated",p13.integerDotProduct8BitUnsignedAccelerated},{"integerDotProduct8BitSignedAccelerated",p13.integerDotProduct8BitSignedAccelerated},{"integerDotProduct8BitMixedSignednessAccelerated",p13.integerDotProduct8BitMixedSignednessAccelerated},{"integerDotProduct4x8BitPackedUnsignedAccelerated",p13.integerDotProduct4x8BitPackedUnsignedAccelerated},{"integerDotProduct4x8BitPackedSignedAccelerated",p13.integerDotProduct4x8BitPackedSignedAccelerated},{"integerDotProduct4x8BitPackedMixedSignednessAccelerated",p13.integerDotProduct4x8BitPackedMixedSignednessAccelerated},{"integerDotProduct16BitUnsignedAccelerated",p13.integerDotProduct16BitUnsignedAccelerated},{"integerDotProduct16BitSignedAccelerated",p13.integerDotProduct16BitSignedAccelerated},{"integerDotProduct16BitMixedSignednessAccelerated",p13.integerDotProduct16BitMixedSignednessAccelerated},{"integerDotProduct32BitUnsignedAccelerated",p13.integerDotProduct32BitUnsignedAccelerated},{"integerDotProduct32BitSignedAccelerated",p13.integerDotProduct32BitSignedAccelerated},{"integerDotProduct32BitMixedSignednessAccelerated",p13.integerDotProduct32BitMixedSignednessAccelerated},{"integerDotProduct64BitUnsignedAccelerated",p13.integerDotProduct64BitUnsignedAccelerated},{"integerDotProduct64BitSignedAccelerated",p13.integerDotProduct64BitSignedAccelerated},{"integerDotProduct64BitMixedSignednessAccelerated",p13.integerDotProduct64BitMixedSignednessAccelerated},{"integerDotProductAccumulatingSaturating8BitUnsignedAccelerated",p13.integerDotProductAccumulatingSaturating8BitUnsignedAccelerated},{"integerDotProductAccumulatingSaturating8BitSignedAccelerated",p13.integerDotProductAccumulatingSaturating8BitSignedAccelerated},{"integerDotProductAccumulatingSaturating8BitMixedSignednessAccelerated",p13.integerDotProductAccumulatingSaturating8BitMixedSignednessAccelerated},{"integerDotProductAccumulatingSaturating4x8BitPackedUnsignedAccelerated",p13.integerDotProductAccumulatingSaturating4x8BitPackedUnsignedAccelerated},{"integerDotProductAccumulatingSaturating4x8BitPackedSignedAccelerated",p13.integerDotProductAccumulatingSaturating4x8BitPackedSignedAccelerated},{"integerDotProductAccumulatingSaturating4x8BitPackedMixedSignednessAccelerated",p13.integerDotProductAccumulatingSaturating4x8BitPackedMixedSignednessAccelerated},{"integerDotProductAccumulatingSaturating16BitUnsignedAccelerated",p13.integerDotProductAccumulatingSaturating16BitUnsignedAccelerated},{"integerDotProductAccumulatingSaturating16BitSignedAccelerated",p13.integerDotProductAccumulatingSaturating16BitSignedAccelerated},{"integerDotProductAccumulatingSaturating16BitMixedSignednessAccelerated",p13.integerDotProductAccumulatingSaturating16BitMixedSignednessAccelerated},{"integerDotProductAccumulatingSaturating32BitUnsignedAccelerated",p13.integerDotProductAccumulatingSaturating32BitUnsignedAccelerated},{"integerDotProductAccumulatingSaturating32BitSignedAccelerated",p13.integerDotProductAccumulatingSaturating32BitSignedAccelerated},{"integerDotProductAccumulatingSaturating32BitMixedSignednessAccelerated",p13.integerDotProductAccumulatingSaturating32BitMixedSignednessAccelerated},{"integerDotProductAccumulatingSaturating64BitUnsignedAccelerated",p13.integerDotProductAccumulatingSaturating64BitUnsignedAccelerated},{"integerDotProductAccumulatingSaturating64BitSignedAccelerated",p13.integerDotProductAccumulatingSaturating64BitSignedAccelerated},{"integerDotProductAccumulatingSaturating64BitMixedSignednessAccelerated",p13.integerDotProductAccumulatingSaturating64BitMixedSignednessAccelerated},
        };
        for (const auto& f : bools) appendBoolProperty(out, first, "Core 1.3", f.name, f.value);
        appendProperty(out, first, "Core 1.3", "storageTexelBufferOffsetAlignmentBytes", p13.storageTexelBufferOffsetAlignmentBytes);
        appendProperty(out, first, "Core 1.3", "storageTexelBufferOffsetSingleTexelAlignment", p13.storageTexelBufferOffsetSingleTexelAlignment);
        appendProperty(out, first, "Core 1.3", "uniformTexelBufferOffsetAlignmentBytes", p13.uniformTexelBufferOffsetAlignmentBytes);
        appendProperty(out, first, "Core 1.3", "uniformTexelBufferOffsetSingleTexelAlignment", p13.uniformTexelBufferOffsetSingleTexelAlignment);
        appendProperty(out, first, "Core 1.3", "maxBufferSize", p13.maxBufferSize);
    }
    if (hasFdm) {
        appendProperty(out, first, "VK_EXT_fragment_density_map", "minFragmentDensityTexelSize", std::to_string(fdm.minFragmentDensityTexelSize.width) + " × " + std::to_string(fdm.minFragmentDensityTexelSize.height));
        appendProperty(out, first, "VK_EXT_fragment_density_map", "maxFragmentDensityTexelSize", std::to_string(fdm.maxFragmentDensityTexelSize.width) + " × " + std::to_string(fdm.maxFragmentDensityTexelSize.height));
        appendBoolProperty(out, first, "VK_EXT_fragment_density_map", "fragmentDensityInvocations", fdm.fragmentDensityInvocations);
    }
    if (hasFdm2) {
        appendBoolProperty(out, first, "VK_EXT_fragment_density_map2", "subsampledLoads", fdm2.subsampledLoads);
        appendBoolProperty(out, first, "VK_EXT_fragment_density_map2", "subsampledCoarseReconstructionEarlyAccess", fdm2.subsampledCoarseReconstructionEarlyAccess);
        appendProperty(out, first, "VK_EXT_fragment_density_map2", "maxSubsampledArrayLayers", fdm2.maxSubsampledArrayLayers);
        appendProperty(out, first, "VK_EXT_fragment_density_map2", "maxDescriptorSetSubsampledSamplers", fdm2.maxDescriptorSetSubsampledSamplers);
    }
    out << ']';
}

std::string extensionsJson(const std::vector<VkExtensionProperties>& values, const char* scope) {
    std::ostringstream out;
    out << '[';
    for (size_t i = 0; i < values.size(); ++i) {
        if (i) out << ',';
        out << "{\"name\":" << jsonString(values[i].extensionName) << ",\"specVersion\":" << values[i].specVersion << ",\"scope\":" << jsonString(scope) << '}';
    }
    out << ']';
    return out.str();
}

std::string collect(jobject surfaceObject, JNIEnv* env, const char* driverMode, const char* driverIcdPath, const char* driverBundlePath, const char* hookLibDir) {
    VulkanApi api;
    if (!api.open(driverMode, driverIcdPath, driverBundlePath, hookLibDir)) {
        return std::string("{\"error\":") + jsonString(api.openError.empty() ? "Vulkan loader unavailable" : api.openError) + "}";
    }

    uint32_t loaderVersion = VK_API_VERSION_1_0;
    if (api.enumerateInstanceVersion) api.enumerateInstanceVersion(&loaderVersion);
    const auto instanceExts = instanceExtensions(api);
    const auto instanceLayerValues = instanceLayers(api);
    const bool androidSurfaceAvailable = hasExtension(instanceExts, "VK_KHR_android_surface");
    const bool surfaceAvailable = hasExtension(instanceExts, "VK_KHR_surface");
    const bool swapchainColorspaceAvailable = hasExtension(instanceExts, "VK_EXT_swapchain_colorspace");

    std::vector<const char*> enabledExtensions;
    if (surfaceAvailable) enabledExtensions.push_back("VK_KHR_surface");
    if (androidSurfaceAvailable) enabledExtensions.push_back("VK_KHR_android_surface");
    if (swapchainColorspaceAvailable) enabledExtensions.push_back("VK_EXT_swapchain_colorspace");

    VkApplicationInfo appInfo{VK_STRUCTURE_TYPE_APPLICATION_INFO, nullptr, "VulkanScope", 1, "VulkanScope", 1, loaderVersion};
    VkInstanceCreateInfo createInfo{VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO, nullptr, 0, &appInfo, 0, nullptr, static_cast<uint32_t>(enabledExtensions.size()), enabledExtensions.data()};
    VkInstance instance = nullptr;
    VkResult result = api.createInstance(&createInfo, nullptr, &instance);
    if (result != VK_SUCCESS) {
        std::ostringstream out;
        out << "{\"error\":\"vkCreateInstance failed: " << result << "\",\"loaderVersion\":" << jsonString(versionString(loaderVersion)) << '}';
        return out.str();
    }
    api.loadInstanceFunctions(instance);

    uint32_t deviceCount = 0;
    api.enumeratePhysicalDevices(instance, &deviceCount, nullptr);
    std::vector<VkPhysicalDevice> devices(deviceCount);
    if (deviceCount) api.enumeratePhysicalDevices(instance, &deviceCount, devices.data());

    VkSurfaceKHR surface = 0;
    ANativeWindow* window = nullptr;
    bool surfaceCreated = false;
    if (surfaceObject && androidSurfaceAvailable && api.createAndroidSurfaceKHR && api.destroySurfaceKHR && api.getPhysicalDeviceSurfaceCapabilitiesKHR && api.getPhysicalDeviceSurfaceFormatsKHR && api.getPhysicalDeviceSurfacePresentModesKHR && api.getPhysicalDeviceSurfaceSupportKHR) {
        window = ANativeWindow_fromSurface(env, surfaceObject);
        if (window) {
            VkAndroidSurfaceCreateInfoKHR surfaceInfo{VK_STRUCTURE_TYPE_ANDROID_SURFACE_CREATE_INFO_KHR, nullptr, 0, window};
            if (api.createAndroidSurfaceKHR(instance, &surfaceInfo, nullptr, &surface) == VK_SUCCESS) surfaceCreated = true;
            ANativeWindow_release(window);
        }
    }

    std::ostringstream out;
    out << "{\"loaderVersion\":" << jsonString(versionString(loaderVersion));
    out << ",\"instanceExtensions\":" << extensionsJson(instanceExts, "Instance");
    out << ",\"instanceLayers\":" << layersJson(instanceLayerValues);
    out << ",\"surfaceColorSpaceExtensionAvailable\":" << jsonBool(swapchainColorspaceAvailable);
    out << ",\"surfaceColorSpaceExtensionEnabled\":" << jsonBool(swapchainColorspaceAvailable);
    out << ",\"deviceCount\":" << deviceCount;
    out << ",\"devices\":[";

    for (uint32_t deviceIndex = 0; deviceIndex < deviceCount; ++deviceIndex) {
        if (deviceIndex) out << ',';
        VkPhysicalDevice device = devices[deviceIndex];
        std::array<uint8_t, 4096> properties{};
        api.getPhysicalDeviceProperties(device, properties.data());
        uint32_t apiVersion = 0;
        uint32_t driverVersion = 0;
        uint32_t vendorId = 0;
        uint32_t deviceId = 0;
        uint32_t deviceType = 0;
        std::memcpy(&apiVersion, properties.data(), sizeof(apiVersion));
        std::memcpy(&driverVersion, properties.data() + 4, sizeof(driverVersion));
        std::memcpy(&vendorId, properties.data() + 8, sizeof(vendorId));
        std::memcpy(&deviceId, properties.data() + 12, sizeof(deviceId));
        std::memcpy(&deviceType, properties.data() + 16, sizeof(deviceType));
        const size_t nameLength = strnlen(reinterpret_cast<const char*>(properties.data() + 20), 256);
        const std::string deviceName(reinterpret_cast<const char*>(properties.data() + 20), nameLength);
        const auto devExts = deviceExtensions(api, device);
        VkPhysicalDeviceFeatures features{};
        api.getPhysicalDeviceFeatures(device, &features);
        VkPhysicalDeviceMemoryProperties memory{};
        api.getPhysicalDeviceMemoryProperties(device, &memory);
        uint32_t queueCount = 0;
        api.getPhysicalDeviceQueueFamilyProperties(device, &queueCount, nullptr);
        std::vector<VkQueueFamilyProperties> queues(queueCount);
        if (queueCount) api.getPhysicalDeviceQueueFamilyProperties(device, &queueCount, queues.data());

        out << "{\"name\":" << jsonString(deviceName) << ",\"apiVersion\":" << jsonString(versionString(apiVersion))
            << ",\"driverVersion\":" << jsonString(std::to_string(driverVersion)) << ",\"vendorId\":" << vendorId
            << ",\"deviceId\":" << deviceId << ",\"deviceType\":" << deviceType
            << ",\"deviceExtensions\":" << extensionsJson(devExts, "Device") << ",\"features\":[";
        for (size_t i = 0; i < 55; ++i) {
            if (i) out << ',';
            out << "{\"name\":" << jsonString(featureName(i)) << ",\"supported\":" << jsonBool(features.values[i] == VK_TRUE) << '}';
        }
        out << "],\"limits\":[";
        const auto readU32 = [&properties](size_t offset) { uint32_t value = 0; std::memcpy(&value, properties.data() + offset, sizeof(value)); return value; };
        const size_t limitsBase = 292;
        const std::pair<const char*, uint32_t> limit32[] = {
            {"maxImageDimension1D", 0}, {"maxImageDimension2D", 4}, {"maxImageDimension3D", 8}, {"maxImageDimensionCube", 12},
            {"maxImageArrayLayers", 16}, {"maxTexelBufferElements", 20}, {"maxUniformBufferRange", 24}, {"maxStorageBufferRange", 28},
            {"maxPushConstantsSize", 32}, {"maxMemoryAllocationCount", 36}, {"maxSamplerAllocationCount", 40}
        };
        for (size_t i = 0; i < sizeof(limit32) / sizeof(limit32[0]); ++i) {
            if (i) out << ',';
            out << "{\"name\":" << jsonString(limit32[i].first) << ",\"value\":" << readU32(limitsBase + limit32[i].second) << '}';
        }
        uint64_t bufferImageGranularity = 0;
        std::memcpy(&bufferImageGranularity, properties.data() + limitsBase + (sizeof(void*) == 8 ? 48 : 44), sizeof(bufferImageGranularity));
        out << ",{\"name\":\"bufferImageGranularity\",\"value\":" << bufferImageGranularity << "}]";

        out << ",\"versionedFeatures\":";
        appendVersionedFeatures(out, apiVersion, api, device);
        out << ",\"detailedProperties\":";
        appendCoreProperties(out, apiVersion, api, device, devExts);
        out << ",\"queues\":[";
        for (uint32_t i = 0; i < queueCount; ++i) {
            if (i) out << ',';
            const auto& q = queues[i];
            out << "{\"index\":" << i << ",\"count\":" << q.queueCount << ",\"timestampValidBits\":" << q.timestampValidBits
                << ",\"flags\":" << q.queueFlags << ",\"graphics\":" << jsonBool((q.queueFlags & 1u) != 0)
                << ",\"compute\":" << jsonBool((q.queueFlags & 2u) != 0) << ",\"transfer\":" << jsonBool((q.queueFlags & 4u) != 0)
                << ",\"sparse\":" << jsonBool((q.queueFlags & 8u) != 0)
                << ",\"protected\":" << jsonBool((q.queueFlags & 0x10u) != 0)
                << ",\"opticalFlow\":" << jsonBool((q.queueFlags & 0x100u) != 0)
                << ",\"minImageTransferGranularity\":" << jsonString(std::to_string(q.minImageTransferGranularity.width) + " × " + std::to_string(q.minImageTransferGranularity.height) + " × " + std::to_string(q.minImageTransferGranularity.depth)) << '}';
        }
        out << "] ,\"memory\":{\"heapCount\":" << memory.memoryHeapCount << ",\"heaps\":[";
        for (uint32_t i = 0; i < memory.memoryHeapCount; ++i) {
            if (i) out << ',';
            out << "{\"index\":" << i << ",\"size\":" << memory.memoryHeaps[i].size << ",\"flags\":" << memory.memoryHeaps[i].flags << '}';
        }
        out << "],\"typeCount\":" << memory.memoryTypeCount << ",\"types\":[";
        for (uint32_t i = 0; i < memory.memoryTypeCount; ++i) {
            if (i) out << ',';
            out << "{\"index\":" << i << ",\"heap\":" << memory.memoryTypes[i].heapIndex << ",\"flags\":" << memory.memoryTypes[i].propertyFlags << '}';
        }
        out << "]},\"formats\":[";
        const int32_t formats[] = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,199,200,201,202,203,204,205,206,207,208};
        bool firstFormat = true;
        for (int32_t format : formats) {
            VkFormatProperties props{};
            api.getPhysicalDeviceFormatProperties(device, format, &props);
            if (props.linearTilingFeatures == 0 && props.optimalTilingFeatures == 0 && props.bufferFeatures == 0) continue;
            if (!firstFormat) out << ',';
            firstFormat = false;
            out << "{\"name\":" << jsonString(formatName(format)) << ",\"linear\":" << props.linearTilingFeatures
                << ",\"optimal\":" << props.optimalTilingFeatures << ",\"buffer\":" << props.bufferFeatures << '}';
        }
        out << "]";

        out << ",\"surface\":{\"available\":" << jsonBool(surfaceCreated)
            << ",\"colorSpaceExtensionAvailable\":" << jsonBool(swapchainColorspaceAvailable)
            << ",\"colorSpaceExtensionEnabled\":" << jsonBool(swapchainColorspaceAvailable);
        if (surfaceCreated) {
            bool presentationSupported = false;
            for (uint32_t i = 0; i < queueCount; ++i) {
                VkBool32 supported = 0;
                if (api.getPhysicalDeviceSurfaceSupportKHR(device, i, surface, &supported) == VK_SUCCESS && supported == VK_TRUE) {
                    presentationSupported = true;
                    break;
                }
            }
            out << ",\"presentationSupported\":" << jsonBool(presentationSupported);
            if (presentationSupported) {
                VkSurfaceCapabilitiesKHR caps{};
                const VkResult capResult = api.getPhysicalDeviceSurfaceCapabilitiesKHR(device, surface, &caps);
                out << ",\"capabilityResult\":" << capResult;
                if (capResult == VK_SUCCESS) {
                    out << ",\"minImageCount\":" << caps.minImageCount
                        << ",\"maxImageCount\":" << caps.maxImageCount << ",\"currentExtent\":" << jsonString(std::to_string(caps.currentExtent.width) + " × " + std::to_string(caps.currentExtent.height))
                        << ",\"minExtent\":" << jsonString(std::to_string(caps.minImageExtent.width) + " × " + std::to_string(caps.minImageExtent.height))
                        << ",\"maxExtent\":" << jsonString(std::to_string(caps.maxImageExtent.width) + " × " + std::to_string(caps.maxImageExtent.height))
                        << ",\"maxImageArrayLayers\":" << caps.maxImageArrayLayers << ",\"supportedTransforms\":" << caps.supportedTransforms
                        << ",\"currentTransform\":" << caps.currentTransform << ",\"supportedCompositeAlpha\":" << caps.supportedCompositeAlpha
                        << ",\"supportedUsageFlags\":" << caps.supportedUsageFlags;
                }

                uint32_t formatCount = 0;
                const VkResult formatCountResult = api.getPhysicalDeviceSurfaceFormatsKHR(device, surface, &formatCount, nullptr);
                std::vector<VkSurfaceFormatKHR> surfaceFormats;
                VkResult formatResult = formatCountResult;
                if (formatCountResult == VK_SUCCESS || formatCountResult == VK_INCOMPLETE) {
                    surfaceFormats.resize(formatCount);
                    if (formatCount) formatResult = api.getPhysicalDeviceSurfaceFormatsKHR(device, surface, &formatCount, surfaceFormats.data());
                    else formatResult = VK_SUCCESS;
                }
                out << ",\"formatQueryResult\":" << formatCountResult
                    << ",\"formatQueryResultSecond\":" << formatResult << ",\"formatCount\":" << formatCount << ",\"formats\":[";
                for (uint32_t i = 0; i < formatCount && i < surfaceFormats.size(); ++i) {
                    if (i) out << ',';
                    out << "{\"format\":" << jsonString(formatName(surfaceFormats[i].format)) << ",\"colorSpace\":" << jsonString(colorSpaceName(surfaceFormats[i].colorSpace))
                        << ",\"class\":" << jsonString(colorSpaceClass(surfaceFormats[i].colorSpace))
                        << ",\"description\":" << jsonString(colorSpaceDescription(surfaceFormats[i].colorSpace)) << '}';
                }
                out << "]";

                uint32_t modeCount = 0;
                const VkResult modeCountResult = api.getPhysicalDeviceSurfacePresentModesKHR(device, surface, &modeCount, nullptr);
                std::vector<uint32_t> modes;
                if (modeCountResult == VK_SUCCESS || modeCountResult == VK_INCOMPLETE) {
                    modes.resize(modeCount);
                    if (modeCount) api.getPhysicalDeviceSurfacePresentModesKHR(device, surface, &modeCount, modes.data());
                }
                out << ",\"presentModes\":[";
                for (uint32_t i = 0; i < modeCount && i < modes.size(); ++i) {
                    if (i) out << ',';
                    const char* name = modes[i] == 0 ? "VK_PRESENT_MODE_IMMEDIATE_KHR" : modes[i] == 1 ? "VK_PRESENT_MODE_MAILBOX_KHR" : modes[i] == 2 ? "VK_PRESENT_MODE_FIFO_KHR" : modes[i] == 3 ? "VK_PRESENT_MODE_FIFO_RELAXED_KHR" : modes[i] == 1000111000 ? "VK_PRESENT_MODE_SHARED_DEMAND_REFRESH_KHR" : modes[i] == 1000111001 ? "VK_PRESENT_MODE_SHARED_CONTINUOUS_REFRESH_KHR" : "VK_PRESENT_MODE_UNKNOWN";
                    out << jsonString(name);
                }
                out << "],\"queuePresentation\":[";
                bool firstPresent = true;
                for (uint32_t i = 0; i < queueCount; ++i) {
                    VkBool32 supported = 0;
                    if (api.getPhysicalDeviceSurfaceSupportKHR(device, i, surface, &supported) != VK_SUCCESS) continue;
                    if (!firstPresent) out << ',';
                    firstPresent = false;
                    out << "{\"queueFamily\":" << i << ",\"supported\":" << jsonBool(supported == VK_TRUE) << '}';
                }
                out << "]";
            }
        } else {
            out << ",\"presentationSupported\":false";
        }
        out << "}";
        // Close the device object. Without this brace the JSON returned to Kotlin
        // ends after the surface object, so JSONObject.parse() reports an
        // "Unterminated object" error and the UI receives zero devices.
        out << "}";

    }
    out << "]}";
    if (surfaceCreated) api.destroySurfaceKHR(instance, surface, nullptr);
    api.destroyInstance(instance, nullptr);
    return out.str();
}

bool detectAdrenoSystemDriver() {
    VulkanApi api;
    if (!api.open("SYSTEM", nullptr, nullptr, nullptr)) return false;
    uint32_t loaderVersion = VK_API_VERSION_1_0;
    if (api.enumerateInstanceVersion) api.enumerateInstanceVersion(&loaderVersion);
    VkApplicationInfo appInfo{VK_STRUCTURE_TYPE_APPLICATION_INFO, nullptr, "VulkanScopeProbe", 1, "VulkanScope", 1, loaderVersion};
    VkInstanceCreateInfo createInfo{VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO, nullptr, 0, &appInfo, 0, nullptr, 0, nullptr};
    VkInstance instance = nullptr;
    if (api.createInstance(&createInfo, nullptr, &instance) != VK_SUCCESS || !instance) return false;
    api.loadInstanceFunctions(instance);
    uint32_t count = 0;
    bool adreno = false;
    if (api.enumeratePhysicalDevices && api.getPhysicalDeviceProperties && api.enumeratePhysicalDevices(instance, &count, nullptr) == VK_SUCCESS) {
        std::vector<VkPhysicalDevice> devices(count);
        if (count && api.enumeratePhysicalDevices(instance, &count, devices.data()) == VK_SUCCESS) {
            for (VkPhysicalDevice device : devices) {
                std::array<uint8_t, 4096> properties{};
                api.getPhysicalDeviceProperties(device, properties.data());
                uint32_t vendorId = 0;
                std::memcpy(&vendorId, properties.data() + 8, sizeof(vendorId));
                if (vendorId == 0x5143u) { adreno = true; break; }
            }
        }
    }
    if (api.destroyInstance) api.destroyInstance(instance, nullptr);
    return adreno;
}

} // namespace

extern "C" JNIEXPORT jstring JNICALL
Java_com_efishell_vulkanscope_MainActivity_collectVulkanData(JNIEnv* env, jobject, jobject surface, jstring driverModeString, jstring driverIcdPathString, jstring driverBundlePathString, jstring hookLibDirString) {
    const char* driverMode = driverModeString ? env->GetStringUTFChars(driverModeString, nullptr) : nullptr;
    const char* driverIcdPath = driverIcdPathString ? env->GetStringUTFChars(driverIcdPathString, nullptr) : nullptr;
    const char* driverBundlePath = driverBundlePathString ? env->GetStringUTFChars(driverBundlePathString, nullptr) : nullptr;
    const char* hookLibDir = hookLibDirString ? env->GetStringUTFChars(hookLibDirString, nullptr) : nullptr;
    const std::string result = collect(surface, env, driverMode, driverIcdPath, driverBundlePath, hookLibDir);
    if (driverBundlePathString && driverBundlePath) env->ReleaseStringUTFChars(driverBundlePathString, driverBundlePath);
    if (hookLibDirString && hookLibDir) env->ReleaseStringUTFChars(hookLibDirString, hookLibDir);
    if (driverIcdPathString && driverIcdPath) env->ReleaseStringUTFChars(driverIcdPathString, driverIcdPath);
    if (driverModeString && driverMode) env->ReleaseStringUTFChars(driverModeString, driverMode);
    return env->NewStringUTF(result.c_str());
}


extern "C" JNIEXPORT jboolean JNICALL
Java_com_efishell_vulkanscope_MainActivity_isAdrenoDevice(JNIEnv* /*env*/, jobject /*thiz*/) {
    return detectAdrenoSystemDriver() ? JNI_TRUE : JNI_FALSE;
}
