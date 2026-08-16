#define VK_NO_PROTOTYPES 1
#define VK_USE_PLATFORM_ANDROID_KHR 1
#define VK_ENABLE_BETA_EXTENSIONS 1
#include <vulkan/vulkan.h>
#include "registry_query_catalog.h"
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
#include <memory>
#include <type_traits>
#include <android/log.h>
#include <android/dlext.h>
#include <fcntl.h>
#include <signal.h>
#include <unistd.h>
#if defined(VULKANSCOPE_HAS_ADRENOTOOLS)
#include <adrenotools/driver.h>
#endif

namespace {
int g_probeCrashFd = -1;
volatile sig_atomic_t g_probeStage = 0;
volatile sig_atomic_t g_probePartialPublished = 0;
int g_probeCrashMarkerFd = -1;

void probeSignalHandler(int signalNumber) {
    if (g_probeCrashFd >= 0 && !g_probePartialPublished) {
        const char* payload = nullptr;
        size_t payloadSize = 0;
        switch (g_probeStage) {
            case 1: { static constexpr char value[] = "{\"status\":\"unavailable\",\"reason\":\"Native Vulkan loader initialization terminated by signal.\",\"devices\":[]}"; payload = value; payloadSize = sizeof(value) - 1; break; }
            case 2: { static constexpr char value[] = "{\"status\":\"unavailable\",\"reason\":\"Vulkan instance creation terminated by signal.\",\"devices\":[]}"; payload = value; payloadSize = sizeof(value) - 1; break; }
            case 3: { static constexpr char value[] = "{\"status\":\"unavailable\",\"reason\":\"Vulkan instance function loading terminated by signal.\",\"devices\":[]}"; payload = value; payloadSize = sizeof(value) - 1; break; }
            case 4: { static constexpr char value[] = "{\"status\":\"unavailable\",\"reason\":\"Physical-device enumeration count query terminated by signal.\",\"devices\":[]}"; payload = value; payloadSize = sizeof(value) - 1; break; }
            case 5: { static constexpr char value[] = "{\"status\":\"unavailable\",\"reason\":\"Physical-device enumeration call terminated by a native signal; Vulkan implementation crash/abort suspected.\",\"devices\":[]}"; payload = value; payloadSize = sizeof(value) - 1; break; }
            case 6: { static constexpr char value[] = "{\"status\":\"unavailable\",\"reason\":\"Physical-device property query terminated by signal.\",\"devices\":[]}"; payload = value; payloadSize = sizeof(value) - 1; break; }
            case 7: { static constexpr char value[] = "{\"status\":\"unavailable\",\"reason\":\"Physical-device feature query terminated by signal.\",\"devices\":[]}"; payload = value; payloadSize = sizeof(value) - 1; break; }
            case 8: { static constexpr char value[] = "{\"status\":\"unavailable\",\"reason\":\"Physical-device memory query terminated by signal.\",\"devices\":[]}"; payload = value; payloadSize = sizeof(value) - 1; break; }
            case 9: { static constexpr char value[] = "{\"status\":\"unavailable\",\"reason\":\"Queue-family query terminated by signal.\",\"devices\":[]}"; payload = value; payloadSize = sizeof(value) - 1; break; }
            case 61: { static constexpr char value[] = "{\"status\":\"unavailable\",\"reason\":\"Device extension enumeration terminated by signal.\",\"devices\":[]}"; payload = value; payloadSize = sizeof(value) - 1; break; }
            case 62: { static constexpr char value[] = "{\"status\":\"unavailable\",\"reason\":\"Device layer enumeration terminated by signal.\",\"devices\":[]}"; payload = value; payloadSize = sizeof(value) - 1; break; }
            case 20: { static constexpr char value[] = "{\"status\":\"unavailable\",\"reason\":\"Surface probe instance creation terminated by signal.\",\"devices\":[]}"; payload = value; payloadSize = sizeof(value) - 1; break; }
            case 21: { static constexpr char value[] = "{\"status\":\"unavailable\",\"reason\":\"Surface probe instance function loading terminated by signal.\",\"devices\":[]}"; payload = value; payloadSize = sizeof(value) - 1; break; }
            case 22: { static constexpr char value[] = "{\"status\":\"unavailable\",\"reason\":\"Surface probe physical-device enumeration terminated by signal.\",\"devices\":[]}"; payload = value; payloadSize = sizeof(value) - 1; break; }
            case 23: { static constexpr char value[] = "{\"status\":\"unavailable\",\"reason\":\"Android Surface conversion or VkSurfaceKHR creation terminated by signal.\",\"devices\":[]}"; payload = value; payloadSize = sizeof(value) - 1; break; }
            case 24: { static constexpr char value[] = "{\"status\":\"unavailable\",\"reason\":\"VkSurfaceKHR capability query terminated by signal.\",\"devices\":[]}"; payload = value; payloadSize = sizeof(value) - 1; break; }
            case 25: { static constexpr char value[] = "{\"status\":\"unavailable\",\"reason\":\"VkSurfaceKHR format query terminated by signal.\",\"devices\":[]}"; payload = value; payloadSize = sizeof(value) - 1; break; }
            case 26: { static constexpr char value[] = "{\"status\":\"unavailable\",\"reason\":\"VkSurfaceKHR present-mode query terminated by signal.\",\"devices\":[]}"; payload = value; payloadSize = sizeof(value) - 1; break; }
            case 50: { static constexpr char value[] = "{\"status\":\"unavailable\",\"reason\":\"Base Vulkan report preparation terminated by signal.\",\"devices\":[]}"; payload = value; payloadSize = sizeof(value) - 1; break; }
            case 51: { static constexpr char value[] = "{\"status\":\"unavailable\",\"reason\":\"Instance-layer aggregation terminated by signal.\",\"devices\":[]}"; payload = value; payloadSize = sizeof(value) - 1; break; }
            case 52: { static constexpr char value[] = "{\"status\":\"unavailable\",\"reason\":\"Registry coverage serialization terminated by signal.\",\"devices\":[]}"; payload = value; payloadSize = sizeof(value) - 1; break; }
            case 53: { static constexpr char value[] = "{\"status\":\"unavailable\",\"reason\":\"Base device report serialization terminated by signal.\",\"devices\":[]}"; payload = value; payloadSize = sizeof(value) - 1; break; }
            case 54: { static constexpr char value[] = "{\"status\":\"unavailable\",\"reason\":\"Instance extension aggregation terminated by signal.\",\"devices\":[]}"; payload = value; payloadSize = sizeof(value) - 1; break; }
            default: { static constexpr char value[] = "{\"status\":\"unavailable\",\"reason\":\"Native Vulkan probe terminated by signal.\",\"devices\":[]}"; payload = value; payloadSize = sizeof(value) - 1; break; }
        }
        (void)write(g_probeCrashFd, payload, payloadSize);
    }
    if (g_probeCrashMarkerFd >= 0) {
        const char marker = '1';
        (void)write(g_probeCrashMarkerFd, &marker, 1);
    }
    _exit(128 + signalNumber);
}

void installProbeCrashGuard(const char* resultPath) {
    if (!resultPath || resultPath[0] == '\0') return;
    g_probeStage = 0;
    g_probePartialPublished = 0;
    g_probeCrashFd = open(resultPath, O_CREAT | O_TRUNC | O_WRONLY | O_CLOEXEC, 0600);
    if (g_probeCrashFd < 0) return;
    std::string markerPath = std::string(resultPath) + ".crash";
    unlink(markerPath.c_str());
    g_probeCrashMarkerFd = open(markerPath.c_str(), O_CREAT | O_TRUNC | O_WRONLY | O_CLOEXEC, 0600);
    struct sigaction action{};
    action.sa_handler = probeSignalHandler;
    sigemptyset(&action.sa_mask);
    sigaction(SIGSEGV, &action, nullptr);
    sigaction(SIGABRT, &action, nullptr);
    sigaction(SIGBUS, &action, nullptr);
    sigaction(SIGILL, &action, nullptr);
    sigaction(SIGFPE, &action, nullptr);
    sigaction(SIGSYS, &action, nullptr);
    sigaction(SIGTRAP, &action, nullptr);
}

void clearProbeCrashGuard(const char* resultPath) {
    if (g_probeCrashFd >= 0) {
        close(g_probeCrashFd);
        g_probeCrashFd = -1;
    }
    if (g_probeCrashMarkerFd >= 0) {
        close(g_probeCrashMarkerFd);
        g_probeCrashMarkerFd = -1;
    }
    if (resultPath && resultPath[0] != '\0') {
        std::string markerPath = std::string(resultPath) + ".crash";
        unlink(markerPath.c_str());
    }
    g_probePartialPublished = 0;
    g_probeStage = 0;
}
struct GeneratedField {
    bool feature;
    std::string section;
    std::string name;
    std::string value;
};

static void appendGeneratedStructFields(std::vector<GeneratedField>& dst, uint32_t sType, void* ptr);

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
    PFN_vkGetPhysicalDeviceVideoCapabilitiesKHR getPhysicalDeviceVideoCapabilitiesKHR = nullptr;
    PFN_vkGetPhysicalDeviceVideoFormatPropertiesKHR getPhysicalDeviceVideoFormatPropertiesKHR = nullptr;
    PFN_vkGetPhysicalDeviceToolProperties getPhysicalDeviceToolProperties = nullptr;
    PFN_vkGetPhysicalDeviceQueueFamilyProperties2 getPhysicalDeviceQueueFamilyProperties2 = nullptr;
    PFN_vkGetPhysicalDeviceFormatProperties2 getPhysicalDeviceFormatProperties2 = nullptr;
    PFN_vkGetPhysicalDeviceImageFormatProperties2 getPhysicalDeviceImageFormatProperties2 = nullptr;
    PFN_vkGetPhysicalDeviceMemoryProperties2 getPhysicalDeviceMemoryProperties2 = nullptr;
    PFN_vkGetPhysicalDeviceExternalBufferProperties getPhysicalDeviceExternalBufferProperties = nullptr;
    PFN_vkGetPhysicalDeviceExternalFenceProperties getPhysicalDeviceExternalFenceProperties = nullptr;
    PFN_vkGetPhysicalDeviceExternalSemaphoreProperties getPhysicalDeviceExternalSemaphoreProperties = nullptr;
    PFN_vkGetPhysicalDeviceSparseImageFormatProperties2 getPhysicalDeviceSparseImageFormatProperties2 = nullptr;
    PFN_vkEnumeratePhysicalDeviceGroups enumeratePhysicalDeviceGroups = nullptr;
    PFN_vkEnumerateDeviceExtensionProperties enumerateDeviceExtensionProperties = nullptr;
    PFN_vkEnumerateDeviceLayerProperties enumerateDeviceLayerProperties = nullptr;
    PFN_vkGetPhysicalDeviceFormatProperties getPhysicalDeviceFormatProperties = nullptr;
    PFN_vkCreateAndroidSurfaceKHR createAndroidSurfaceKHR = nullptr;
    PFN_vkDestroySurfaceKHR destroySurfaceKHR = nullptr;
    PFN_vkGetPhysicalDeviceSurfaceCapabilitiesKHR getPhysicalDeviceSurfaceCapabilitiesKHR = nullptr;
    PFN_vkGetPhysicalDeviceSurfaceFormatsKHR getPhysicalDeviceSurfaceFormatsKHR = nullptr;
    PFN_vkGetPhysicalDeviceSurfacePresentModesKHR getPhysicalDeviceSurfacePresentModesKHR = nullptr;
    PFN_vkGetPhysicalDeviceSurfaceSupportKHR getPhysicalDeviceSurfaceSupportKHR = nullptr;
    PFN_vkGetPhysicalDeviceSurfaceCapabilities2KHR getPhysicalDeviceSurfaceCapabilities2KHR = nullptr;
    PFN_vkGetPhysicalDeviceSurfaceFormats2KHR getPhysicalDeviceSurfaceFormats2KHR = nullptr;
    std::string openError;
    std::vector<GeneratedField> generatedFields;
    bool captureGeneratedFields = false;

    void queryProperties2(VkPhysicalDevice device, VkPhysicalDeviceProperties2* properties);
    void queryFeatures2(VkPhysicalDevice device, VkPhysicalDeviceFeatures2* features);

    ~VulkanApi() { if (library) dlclose(library); }

    template <typename T>
    T load(const char* name) const { return reinterpret_cast<T>(dlsym(library, name)); }

    template <typename T>
    T loadInstance(VkInstance instance, const char* name) const { return reinterpret_cast<T>(getInstanceProcAddr(instance, name)); }

    bool open(const char* driverMode, const char* driverIcdPath, const char* driverBundlePath, const char* hookLibDir) {
        
        
        
        (void)driverIcdPath;
        (void)driverBundlePath;
        (void)hookLibDir;

        const bool wantTurnip = driverMode && std::strcmp(driverMode, "TURNIP") == 0;

        if (wantTurnip) {
#if !defined(VULKANSCOPE_HAS_ADRENOTOOLS)
            
            
            
            
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

        
        
        
        enumerateInstanceVersion = reinterpret_cast<PFN_vkEnumerateInstanceVersion>(getInstanceProcAddr(nullptr, "vkEnumerateInstanceVersion"));
        enumerateInstanceExtensionProperties = reinterpret_cast<PFN_vkEnumerateInstanceExtensionProperties>(getInstanceProcAddr(nullptr, "vkEnumerateInstanceExtensionProperties"));
        enumerateInstanceLayerProperties = reinterpret_cast<PFN_vkEnumerateInstanceLayerProperties>(getInstanceProcAddr(nullptr, "vkEnumerateInstanceLayerProperties"));
        createInstance = reinterpret_cast<PFN_vkCreateInstance>(getInstanceProcAddr(nullptr, "vkCreateInstance"));

        
        
        
        destroyInstance = nullptr;
        if (!enumerateInstanceExtensionProperties || !createInstance) {
            openError = "Vulkan loader opened, but required global entry points are unavailable";
            return false;
        }
        return true;
    }

    VkResult createInstanceCompatible(uint32_t loaderVersion, const std::vector<const char*>& enabledExtensions, VkInstance* instance, uint32_t* selectedApiVersion) {
        if (!createInstance || !instance || !selectedApiVersion) return VK_ERROR_INITIALIZATION_FAILED;
        const uint32_t candidates[] = {VK_API_VERSION_1_4, VK_API_VERSION_1_3, VK_API_VERSION_1_2, VK_API_VERSION_1_1, VK_API_VERSION_1_0};
        VkResult lastResult = VK_ERROR_INCOMPATIBLE_DRIVER;
        *instance = nullptr;
        *selectedApiVersion = VK_API_VERSION_1_0;
        for (uint32_t candidate : candidates) {
            if (candidate > loaderVersion) continue;
            VkApplicationInfo appInfo{VK_STRUCTURE_TYPE_APPLICATION_INFO, nullptr, "VulkanScope", 1, "VulkanScope", 1, candidate};
            VkInstanceCreateInfo createInfo{VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO, nullptr, 0, &appInfo, 0, nullptr, static_cast<uint32_t>(enabledExtensions.size()), enabledExtensions.data()};
            lastResult = createInstance(&createInfo, nullptr, instance);
            if (lastResult == VK_SUCCESS && *instance) {
                *selectedApiVersion = candidate;
                return VK_SUCCESS;
            }
            *instance = nullptr;
            if (lastResult != VK_ERROR_INCOMPATIBLE_DRIVER) break;
        }
        return lastResult;
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
        getPhysicalDeviceVideoCapabilitiesKHR = loadInstance<PFN_vkGetPhysicalDeviceVideoCapabilitiesKHR>(instance, "vkGetPhysicalDeviceVideoCapabilitiesKHR");
        getPhysicalDeviceVideoFormatPropertiesKHR = loadInstance<PFN_vkGetPhysicalDeviceVideoFormatPropertiesKHR>(instance, "vkGetPhysicalDeviceVideoFormatPropertiesKHR");
        getPhysicalDeviceToolProperties = loadInstance<PFN_vkGetPhysicalDeviceToolProperties>(instance, "vkGetPhysicalDeviceToolProperties");
        if (!getPhysicalDeviceToolProperties) getPhysicalDeviceToolProperties = loadInstance<PFN_vkGetPhysicalDeviceToolProperties>(instance, "vkGetPhysicalDeviceToolPropertiesEXT");
        getPhysicalDeviceQueueFamilyProperties2 = loadInstance<PFN_vkGetPhysicalDeviceQueueFamilyProperties2>(instance, "vkGetPhysicalDeviceQueueFamilyProperties2");
        if (!getPhysicalDeviceQueueFamilyProperties2) getPhysicalDeviceQueueFamilyProperties2 = loadInstance<PFN_vkGetPhysicalDeviceQueueFamilyProperties2>(instance, "vkGetPhysicalDeviceQueueFamilyProperties2KHR");
        getPhysicalDeviceFormatProperties2 = loadInstance<PFN_vkGetPhysicalDeviceFormatProperties2>(instance, "vkGetPhysicalDeviceFormatProperties2");
        if (!getPhysicalDeviceFormatProperties2) getPhysicalDeviceFormatProperties2 = loadInstance<PFN_vkGetPhysicalDeviceFormatProperties2>(instance, "vkGetPhysicalDeviceFormatProperties2KHR");
        getPhysicalDeviceImageFormatProperties2 = loadInstance<PFN_vkGetPhysicalDeviceImageFormatProperties2>(instance, "vkGetPhysicalDeviceImageFormatProperties2");
        if (!getPhysicalDeviceImageFormatProperties2) getPhysicalDeviceImageFormatProperties2 = loadInstance<PFN_vkGetPhysicalDeviceImageFormatProperties2>(instance, "vkGetPhysicalDeviceImageFormatProperties2KHR");
        getPhysicalDeviceMemoryProperties2 = loadInstance<PFN_vkGetPhysicalDeviceMemoryProperties2>(instance, "vkGetPhysicalDeviceMemoryProperties2");
        if (!getPhysicalDeviceMemoryProperties2) getPhysicalDeviceMemoryProperties2 = loadInstance<PFN_vkGetPhysicalDeviceMemoryProperties2>(instance, "vkGetPhysicalDeviceMemoryProperties2KHR");
        getPhysicalDeviceExternalBufferProperties = loadInstance<PFN_vkGetPhysicalDeviceExternalBufferProperties>(instance, "vkGetPhysicalDeviceExternalBufferProperties");
        if (!getPhysicalDeviceExternalBufferProperties) getPhysicalDeviceExternalBufferProperties = loadInstance<PFN_vkGetPhysicalDeviceExternalBufferProperties>(instance, "vkGetPhysicalDeviceExternalBufferPropertiesKHR");
        getPhysicalDeviceExternalFenceProperties = loadInstance<PFN_vkGetPhysicalDeviceExternalFenceProperties>(instance, "vkGetPhysicalDeviceExternalFenceProperties");
        if (!getPhysicalDeviceExternalFenceProperties) getPhysicalDeviceExternalFenceProperties = loadInstance<PFN_vkGetPhysicalDeviceExternalFenceProperties>(instance, "vkGetPhysicalDeviceExternalFencePropertiesKHR");
        getPhysicalDeviceExternalSemaphoreProperties = loadInstance<PFN_vkGetPhysicalDeviceExternalSemaphoreProperties>(instance, "vkGetPhysicalDeviceExternalSemaphoreProperties");
        if (!getPhysicalDeviceExternalSemaphoreProperties) getPhysicalDeviceExternalSemaphoreProperties = loadInstance<PFN_vkGetPhysicalDeviceExternalSemaphoreProperties>(instance, "vkGetPhysicalDeviceExternalSemaphorePropertiesKHR");
        getPhysicalDeviceSparseImageFormatProperties2 = loadInstance<PFN_vkGetPhysicalDeviceSparseImageFormatProperties2>(instance, "vkGetPhysicalDeviceSparseImageFormatProperties2");
        if (!getPhysicalDeviceSparseImageFormatProperties2) getPhysicalDeviceSparseImageFormatProperties2 = loadInstance<PFN_vkGetPhysicalDeviceSparseImageFormatProperties2>(instance, "vkGetPhysicalDeviceSparseImageFormatProperties2KHR");
        enumeratePhysicalDeviceGroups = loadInstance<PFN_vkEnumeratePhysicalDeviceGroups>(instance, "vkEnumeratePhysicalDeviceGroups");
        enumerateDeviceExtensionProperties = loadInstance<PFN_vkEnumerateDeviceExtensionProperties>(instance, "vkEnumerateDeviceExtensionProperties");
        enumerateDeviceLayerProperties = loadInstance<PFN_vkEnumerateDeviceLayerProperties>(instance, "vkEnumerateDeviceLayerProperties");
        getPhysicalDeviceFormatProperties = loadInstance<PFN_vkGetPhysicalDeviceFormatProperties>(instance, "vkGetPhysicalDeviceFormatProperties");
        createAndroidSurfaceKHR = loadInstance<PFN_vkCreateAndroidSurfaceKHR>(instance, "vkCreateAndroidSurfaceKHR");
        destroySurfaceKHR = loadInstance<PFN_vkDestroySurfaceKHR>(instance, "vkDestroySurfaceKHR");
        getPhysicalDeviceSurfaceCapabilitiesKHR = loadInstance<PFN_vkGetPhysicalDeviceSurfaceCapabilitiesKHR>(instance, "vkGetPhysicalDeviceSurfaceCapabilitiesKHR");
        getPhysicalDeviceSurfaceFormatsKHR = loadInstance<PFN_vkGetPhysicalDeviceSurfaceFormatsKHR>(instance, "vkGetPhysicalDeviceSurfaceFormatsKHR");
        getPhysicalDeviceSurfacePresentModesKHR = loadInstance<PFN_vkGetPhysicalDeviceSurfacePresentModesKHR>(instance, "vkGetPhysicalDeviceSurfacePresentModesKHR");
        getPhysicalDeviceSurfaceSupportKHR = loadInstance<PFN_vkGetPhysicalDeviceSurfaceSupportKHR>(instance, "vkGetPhysicalDeviceSurfaceSupportKHR");
        getPhysicalDeviceSurfaceCapabilities2KHR = loadInstance<PFN_vkGetPhysicalDeviceSurfaceCapabilities2KHR>(instance, "vkGetPhysicalDeviceSurfaceCapabilities2KHR");
        getPhysicalDeviceSurfaceFormats2KHR = loadInstance<PFN_vkGetPhysicalDeviceSurfaceFormats2KHR>(instance, "vkGetPhysicalDeviceSurfaceFormats2KHR");
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
constexpr uint32_t kMaxExtensionEntries = 4096;
constexpr uint32_t kMaxLayerEntries = 1024;
constexpr uint32_t kMaxPhysicalDeviceEntries = 256;
constexpr uint32_t kMaxQueueFamilyEntries = 256;
constexpr uint32_t kMaxMemoryHeapEntries = VK_MAX_MEMORY_HEAPS;
constexpr uint32_t kMaxMemoryTypeEntries = VK_MAX_MEMORY_TYPES;
constexpr uint32_t kMaxToolEntries = 256;
constexpr uint32_t kMaxDeviceGroupEntries = 256;
constexpr uint32_t kMaxVideoFormatEntries = 4096;
constexpr uint32_t kMaxSparseImageFormatEntries = 4096;
constexpr uint32_t kMaxSurfaceFormatEntries = 4096;
constexpr uint32_t kMaxPresentModeEntries = 256;

std::string formatName(int32_t value) {
    static const std::pair<int32_t, const char*> names[] = {
        {0, "VK_FORMAT_UNDEFINED"},
        {1, "VK_FORMAT_R4G4_UNORM_PACK8"},
        {2, "VK_FORMAT_R4G4B4A4_UNORM_PACK16"},
        {3, "VK_FORMAT_B4G4R4A4_UNORM_PACK16"},
        {4, "VK_FORMAT_R5G6B5_UNORM_PACK16"},
        {5, "VK_FORMAT_B5G6R5_UNORM_PACK16"},
        {6, "VK_FORMAT_R5G5B5A1_UNORM_PACK16"},
        {7, "VK_FORMAT_B5G5R5A1_UNORM_PACK16"},
        {8, "VK_FORMAT_A1R5G5B5_UNORM_PACK16"},
        {9, "VK_FORMAT_R8_UNORM"},
        {10, "VK_FORMAT_R8_SNORM"},
        {11, "VK_FORMAT_R8_USCALED"},
        {12, "VK_FORMAT_R8_SSCALED"},
        {13, "VK_FORMAT_R8_UINT"},
        {14, "VK_FORMAT_R8_SINT"},
        {15, "VK_FORMAT_R8_SRGB"},
        {16, "VK_FORMAT_R8G8_UNORM"},
        {17, "VK_FORMAT_R8G8_SNORM"},
        {18, "VK_FORMAT_R8G8_USCALED"},
        {19, "VK_FORMAT_R8G8_SSCALED"},
        {20, "VK_FORMAT_R8G8_UINT"},
        {21, "VK_FORMAT_R8G8_SINT"},
        {22, "VK_FORMAT_R8G8_SRGB"},
        {23, "VK_FORMAT_R8G8B8_UNORM"},
        {24, "VK_FORMAT_R8G8B8_SNORM"},
        {25, "VK_FORMAT_R8G8B8_USCALED"},
        {26, "VK_FORMAT_R8G8B8_SSCALED"},
        {27, "VK_FORMAT_R8G8B8_UINT"},
        {28, "VK_FORMAT_R8G8B8_SINT"},
        {29, "VK_FORMAT_R8G8B8_SRGB"},
        {30, "VK_FORMAT_B8G8R8_UNORM"},
        {31, "VK_FORMAT_B8G8R8_SNORM"},
        {32, "VK_FORMAT_B8G8R8_USCALED"},
        {33, "VK_FORMAT_B8G8R8_SSCALED"},
        {34, "VK_FORMAT_B8G8R8_UINT"},
        {35, "VK_FORMAT_B8G8R8_SINT"},
        {36, "VK_FORMAT_B8G8R8_SRGB"},
        {37, "VK_FORMAT_R8G8B8A8_UNORM"},
        {38, "VK_FORMAT_R8G8B8A8_SNORM"},
        {39, "VK_FORMAT_R8G8B8A8_USCALED"},
        {40, "VK_FORMAT_R8G8B8A8_SSCALED"},
        {41, "VK_FORMAT_R8G8B8A8_UINT"},
        {42, "VK_FORMAT_R8G8B8A8_SINT"},
        {43, "VK_FORMAT_R8G8B8A8_SRGB"},
        {44, "VK_FORMAT_B8G8R8A8_UNORM"},
        {45, "VK_FORMAT_B8G8R8A8_SNORM"},
        {46, "VK_FORMAT_B8G8R8A8_USCALED"},
        {47, "VK_FORMAT_B8G8R8A8_SSCALED"},
        {48, "VK_FORMAT_B8G8R8A8_UINT"},
        {49, "VK_FORMAT_B8G8R8A8_SINT"},
        {50, "VK_FORMAT_B8G8R8A8_SRGB"},
        {51, "VK_FORMAT_A8B8G8R8_UNORM_PACK32"},
        {52, "VK_FORMAT_A8B8G8R8_SNORM_PACK32"},
        {53, "VK_FORMAT_A8B8G8R8_USCALED_PACK32"},
        {54, "VK_FORMAT_A8B8G8R8_SSCALED_PACK32"},
        {55, "VK_FORMAT_A8B8G8R8_UINT_PACK32"},
        {56, "VK_FORMAT_A8B8G8R8_SINT_PACK32"},
        {57, "VK_FORMAT_A8B8G8R8_SRGB_PACK32"},
        {58, "VK_FORMAT_A2R10G10B10_UNORM_PACK32"},
        {59, "VK_FORMAT_A2R10G10B10_SNORM_PACK32"},
        {60, "VK_FORMAT_A2R10G10B10_USCALED_PACK32"},
        {61, "VK_FORMAT_A2R10G10B10_SSCALED_PACK32"},
        {62, "VK_FORMAT_A2R10G10B10_UINT_PACK32"},
        {63, "VK_FORMAT_A2R10G10B10_SINT_PACK32"},
        {64, "VK_FORMAT_A2B10G10R10_UNORM_PACK32"},
        {65, "VK_FORMAT_A2B10G10R10_SNORM_PACK32"},
        {66, "VK_FORMAT_A2B10G10R10_USCALED_PACK32"},
        {67, "VK_FORMAT_A2B10G10R10_SSCALED_PACK32"},
        {68, "VK_FORMAT_A2B10G10R10_UINT_PACK32"},
        {69, "VK_FORMAT_A2B10G10R10_SINT_PACK32"},
        {70, "VK_FORMAT_R16_UNORM"},
        {71, "VK_FORMAT_R16_SNORM"},
        {72, "VK_FORMAT_R16_USCALED"},
        {73, "VK_FORMAT_R16_SSCALED"},
        {74, "VK_FORMAT_R16_UINT"},
        {75, "VK_FORMAT_R16_SINT"},
        {76, "VK_FORMAT_R16_SFLOAT"},
        {77, "VK_FORMAT_R16G16_UNORM"},
        {78, "VK_FORMAT_R16G16_SNORM"},
        {79, "VK_FORMAT_R16G16_USCALED"},
        {80, "VK_FORMAT_R16G16_SSCALED"},
        {81, "VK_FORMAT_R16G16_UINT"},
        {82, "VK_FORMAT_R16G16_SINT"},
        {83, "VK_FORMAT_R16G16_SFLOAT"},
        {84, "VK_FORMAT_R16G16B16_UNORM"},
        {85, "VK_FORMAT_R16G16B16_SNORM"},
        {86, "VK_FORMAT_R16G16B16_USCALED"},
        {87, "VK_FORMAT_R16G16B16_SSCALED"},
        {88, "VK_FORMAT_R16G16B16_UINT"},
        {89, "VK_FORMAT_R16G16B16_SINT"},
        {90, "VK_FORMAT_R16G16B16_SFLOAT"},
        {91, "VK_FORMAT_R16G16B16A16_UNORM"},
        {92, "VK_FORMAT_R16G16B16A16_SNORM"},
        {93, "VK_FORMAT_R16G16B16A16_USCALED"},
        {94, "VK_FORMAT_R16G16B16A16_SSCALED"},
        {95, "VK_FORMAT_R16G16B16A16_UINT"},
        {96, "VK_FORMAT_R16G16B16A16_SINT"},
        {97, "VK_FORMAT_R16G16B16A16_SFLOAT"},
        {98, "VK_FORMAT_R32_UINT"},
        {99, "VK_FORMAT_R32_SINT"},
        {100, "VK_FORMAT_R32_SFLOAT"},
        {101, "VK_FORMAT_R32G32_UINT"},
        {102, "VK_FORMAT_R32G32_SINT"},
        {103, "VK_FORMAT_R32G32_SFLOAT"},
        {104, "VK_FORMAT_R32G32B32_UINT"},
        {105, "VK_FORMAT_R32G32B32_SINT"},
        {106, "VK_FORMAT_R32G32B32_SFLOAT"},
        {107, "VK_FORMAT_R32G32B32A32_UINT"},
        {108, "VK_FORMAT_R32G32B32A32_SINT"},
        {109, "VK_FORMAT_R32G32B32A32_SFLOAT"},
        {110, "VK_FORMAT_R64_UINT"},
        {111, "VK_FORMAT_R64_SINT"},
        {112, "VK_FORMAT_R64_SFLOAT"},
        {113, "VK_FORMAT_R64G64_UINT"},
        {114, "VK_FORMAT_R64G64_SINT"},
        {115, "VK_FORMAT_R64G64_SFLOAT"},
        {116, "VK_FORMAT_R64G64B64_UINT"},
        {117, "VK_FORMAT_R64G64B64_SINT"},
        {118, "VK_FORMAT_R64G64B64_SFLOAT"},
        {119, "VK_FORMAT_R64G64B64A64_UINT"},
        {120, "VK_FORMAT_R64G64B64A64_SINT"},
        {121, "VK_FORMAT_R64G64B64A64_SFLOAT"},
        {122, "VK_FORMAT_B10G11R11_UFLOAT_PACK32"},
        {123, "VK_FORMAT_E5B9G9R9_UFLOAT_PACK32"},
        {124, "VK_FORMAT_D16_UNORM"},
        {125, "VK_FORMAT_X8_D24_UNORM_PACK32"},
        {126, "VK_FORMAT_D32_SFLOAT"},
        {127, "VK_FORMAT_S8_UINT"},
        {128, "VK_FORMAT_D16_UNORM_S8_UINT"},
        {129, "VK_FORMAT_D24_UNORM_S8_UINT"},
        {130, "VK_FORMAT_D32_SFLOAT_S8_UINT"},
        {131, "VK_FORMAT_BC1_RGB_UNORM_BLOCK"},
        {132, "VK_FORMAT_BC1_RGB_SRGB_BLOCK"},
        {133, "VK_FORMAT_BC1_RGBA_UNORM_BLOCK"},
        {134, "VK_FORMAT_BC1_RGBA_SRGB_BLOCK"},
        {135, "VK_FORMAT_BC2_UNORM_BLOCK"},
        {136, "VK_FORMAT_BC2_SRGB_BLOCK"},
        {137, "VK_FORMAT_BC3_UNORM_BLOCK"},
        {138, "VK_FORMAT_BC3_SRGB_BLOCK"},
        {139, "VK_FORMAT_BC4_UNORM_BLOCK"},
        {140, "VK_FORMAT_BC4_SNORM_BLOCK"},
        {141, "VK_FORMAT_BC5_UNORM_BLOCK"},
        {142, "VK_FORMAT_BC5_SNORM_BLOCK"},
        {143, "VK_FORMAT_BC6H_UFLOAT_BLOCK"},
        {144, "VK_FORMAT_BC6H_SFLOAT_BLOCK"},
        {145, "VK_FORMAT_BC7_UNORM_BLOCK"},
        {146, "VK_FORMAT_BC7_SRGB_BLOCK"},
        {147, "VK_FORMAT_ETC2_R8G8B8_UNORM_BLOCK"},
        {148, "VK_FORMAT_ETC2_R8G8B8_SRGB_BLOCK"},
        {149, "VK_FORMAT_ETC2_R8G8B8A1_UNORM_BLOCK"},
        {150, "VK_FORMAT_ETC2_R8G8B8A1_SRGB_BLOCK"},
        {151, "VK_FORMAT_ETC2_R8G8B8A8_UNORM_BLOCK"},
        {152, "VK_FORMAT_ETC2_R8G8B8A8_SRGB_BLOCK"},
        {153, "VK_FORMAT_EAC_R11_UNORM_BLOCK"},
        {154, "VK_FORMAT_EAC_R11_SNORM_BLOCK"},
        {155, "VK_FORMAT_EAC_R11G11_UNORM_BLOCK"},
        {156, "VK_FORMAT_EAC_R11G11_SNORM_BLOCK"},
        {157, "VK_FORMAT_ASTC_4x4_UNORM_BLOCK"},
        {158, "VK_FORMAT_ASTC_4x4_SRGB_BLOCK"},
        {159, "VK_FORMAT_ASTC_5x4_UNORM_BLOCK"},
        {160, "VK_FORMAT_ASTC_5x4_SRGB_BLOCK"},
        {161, "VK_FORMAT_ASTC_5x5_UNORM_BLOCK"},
        {162, "VK_FORMAT_ASTC_5x5_SRGB_BLOCK"},
        {163, "VK_FORMAT_ASTC_6x5_UNORM_BLOCK"},
        {164, "VK_FORMAT_ASTC_6x5_SRGB_BLOCK"},
        {165, "VK_FORMAT_ASTC_6x6_UNORM_BLOCK"},
        {166, "VK_FORMAT_ASTC_6x6_SRGB_BLOCK"},
        {167, "VK_FORMAT_ASTC_8x5_UNORM_BLOCK"},
        {168, "VK_FORMAT_ASTC_8x5_SRGB_BLOCK"},
        {169, "VK_FORMAT_ASTC_8x6_UNORM_BLOCK"},
        {170, "VK_FORMAT_ASTC_8x6_SRGB_BLOCK"},
        {171, "VK_FORMAT_ASTC_8x8_UNORM_BLOCK"},
        {172, "VK_FORMAT_ASTC_8x8_SRGB_BLOCK"},
        {173, "VK_FORMAT_ASTC_10x5_UNORM_BLOCK"},
        {174, "VK_FORMAT_ASTC_10x5_SRGB_BLOCK"},
        {175, "VK_FORMAT_ASTC_10x6_UNORM_BLOCK"},
        {176, "VK_FORMAT_ASTC_10x6_SRGB_BLOCK"},
        {177, "VK_FORMAT_ASTC_10x8_UNORM_BLOCK"},
        {178, "VK_FORMAT_ASTC_10x8_SRGB_BLOCK"},
        {179, "VK_FORMAT_ASTC_10x10_UNORM_BLOCK"},
        {180, "VK_FORMAT_ASTC_10x10_SRGB_BLOCK"},
        {181, "VK_FORMAT_ASTC_12x10_UNORM_BLOCK"},
        {182, "VK_FORMAT_ASTC_12x10_SRGB_BLOCK"},
        {183, "VK_FORMAT_ASTC_12x12_UNORM_BLOCK"},
        {184, "VK_FORMAT_ASTC_12x12_SRGB_BLOCK"},
        {1000054000, "VK_FORMAT_PVRTC1_2BPP_UNORM_BLOCK_IMG"},
        {1000054001, "VK_FORMAT_PVRTC1_4BPP_UNORM_BLOCK_IMG"},
        {1000054002, "VK_FORMAT_PVRTC2_2BPP_UNORM_BLOCK_IMG"},
        {1000054003, "VK_FORMAT_PVRTC2_4BPP_UNORM_BLOCK_IMG"},
        {1000054004, "VK_FORMAT_PVRTC1_2BPP_SRGB_BLOCK_IMG"},
        {1000054005, "VK_FORMAT_PVRTC1_4BPP_SRGB_BLOCK_IMG"},
        {1000054006, "VK_FORMAT_PVRTC2_2BPP_SRGB_BLOCK_IMG"},
        {1000054007, "VK_FORMAT_PVRTC2_4BPP_SRGB_BLOCK_IMG"},
        {1000066000, "VK_FORMAT_ASTC_4x4_SFLOAT_BLOCK"},
        {1000066001, "VK_FORMAT_ASTC_5x4_SFLOAT_BLOCK"},
        {1000066002, "VK_FORMAT_ASTC_5x5_SFLOAT_BLOCK"},
        {1000066003, "VK_FORMAT_ASTC_6x5_SFLOAT_BLOCK"},
        {1000066004, "VK_FORMAT_ASTC_6x6_SFLOAT_BLOCK"},
        {1000066005, "VK_FORMAT_ASTC_8x5_SFLOAT_BLOCK"},
        {1000066006, "VK_FORMAT_ASTC_8x6_SFLOAT_BLOCK"},
        {1000066007, "VK_FORMAT_ASTC_8x8_SFLOAT_BLOCK"},
        {1000066008, "VK_FORMAT_ASTC_10x5_SFLOAT_BLOCK"},
        {1000066009, "VK_FORMAT_ASTC_10x6_SFLOAT_BLOCK"},
        {1000066010, "VK_FORMAT_ASTC_10x8_SFLOAT_BLOCK"},
        {1000066011, "VK_FORMAT_ASTC_10x10_SFLOAT_BLOCK"},
        {1000066012, "VK_FORMAT_ASTC_12x10_SFLOAT_BLOCK"},
        {1000066013, "VK_FORMAT_ASTC_12x12_SFLOAT_BLOCK"},
        {1000156000, "VK_FORMAT_G8B8G8R8_422_UNORM"},
        {1000156001, "VK_FORMAT_B8G8R8G8_422_UNORM"},
        {1000156002, "VK_FORMAT_G8_B8_R8_3PLANE_420_UNORM"},
        {1000156003, "VK_FORMAT_G8_B8R8_2PLANE_420_UNORM"},
        {1000156004, "VK_FORMAT_G8_B8_R8_3PLANE_422_UNORM"},
        {1000156005, "VK_FORMAT_G8_B8R8_2PLANE_422_UNORM"},
        {1000156006, "VK_FORMAT_G8_B8_R8_3PLANE_444_UNORM"},
        {1000156007, "VK_FORMAT_R10X6_UNORM_PACK16"},
        {1000156008, "VK_FORMAT_R10X6G10X6_UNORM_2PACK16"},
        {1000156009, "VK_FORMAT_R10X6G10X6B10X6A10X6_UNORM_4PACK16"},
        {1000156010, "VK_FORMAT_G10X6B10X6G10X6R10X6_422_UNORM_4PACK16"},
        {1000156011, "VK_FORMAT_B10X6G10X6R10X6G10X6_422_UNORM_4PACK16"},
        {1000156012, "VK_FORMAT_G10X6_B10X6_R10X6_3PLANE_420_UNORM_3PACK16"},
        {1000156013, "VK_FORMAT_G10X6_B10X6R10X6_2PLANE_420_UNORM_3PACK16"},
        {1000156014, "VK_FORMAT_G10X6_B10X6_R10X6_3PLANE_422_UNORM_3PACK16"},
        {1000156015, "VK_FORMAT_G10X6_B10X6R10X6_2PLANE_422_UNORM_3PACK16"},
        {1000156016, "VK_FORMAT_G10X6_B10X6_R10X6_3PLANE_444_UNORM_3PACK16"},
        {1000156017, "VK_FORMAT_R12X4_UNORM_PACK16"},
        {1000156018, "VK_FORMAT_R12X4G12X4_UNORM_2PACK16"},
        {1000156019, "VK_FORMAT_R12X4G12X4B12X4A12X4_UNORM_4PACK16"},
        {1000156020, "VK_FORMAT_G12X4B12X4G12X4R12X4_422_UNORM_4PACK16"},
        {1000156021, "VK_FORMAT_B12X4G12X4R12X4G12X4_422_UNORM_4PACK16"},
        {1000156022, "VK_FORMAT_G12X4_B12X4_R12X4_3PLANE_420_UNORM_3PACK16"},
        {1000156023, "VK_FORMAT_G12X4_B12X4R12X4_2PLANE_420_UNORM_3PACK16"},
        {1000156024, "VK_FORMAT_G12X4_B12X4_R12X4_3PLANE_422_UNORM_3PACK16"},
        {1000156025, "VK_FORMAT_G12X4_B12X4R12X4_2PLANE_422_UNORM_3PACK16"},
        {1000156026, "VK_FORMAT_G12X4_B12X4_R12X4_3PLANE_444_UNORM_3PACK16"},
        {1000156027, "VK_FORMAT_G16B16G16R16_422_UNORM"},
        {1000156028, "VK_FORMAT_B16G16R16G16_422_UNORM"},
        {1000156029, "VK_FORMAT_G16_B16_R16_3PLANE_420_UNORM"},
        {1000156030, "VK_FORMAT_G16_B16R16_2PLANE_420_UNORM"},
        {1000156031, "VK_FORMAT_G16_B16_R16_3PLANE_422_UNORM"},
        {1000156032, "VK_FORMAT_G16_B16R16_2PLANE_422_UNORM"},
        {1000156033, "VK_FORMAT_G16_B16_R16_3PLANE_444_UNORM"},
        {1000288000, "VK_FORMAT_ASTC_3x3x3_UNORM_BLOCK_EXT"},
        {1000288001, "VK_FORMAT_ASTC_3x3x3_SRGB_BLOCK_EXT"},
        {1000288002, "VK_FORMAT_ASTC_3x3x3_SFLOAT_BLOCK_EXT"},
        {1000288003, "VK_FORMAT_ASTC_4x3x3_UNORM_BLOCK_EXT"},
        {1000288004, "VK_FORMAT_ASTC_4x3x3_SRGB_BLOCK_EXT"},
        {1000288005, "VK_FORMAT_ASTC_4x3x3_SFLOAT_BLOCK_EXT"},
        {1000288006, "VK_FORMAT_ASTC_4x4x3_UNORM_BLOCK_EXT"},
        {1000288007, "VK_FORMAT_ASTC_4x4x3_SRGB_BLOCK_EXT"},
        {1000288008, "VK_FORMAT_ASTC_4x4x3_SFLOAT_BLOCK_EXT"},
        {1000288009, "VK_FORMAT_ASTC_4x4x4_UNORM_BLOCK_EXT"},
        {1000288010, "VK_FORMAT_ASTC_4x4x4_SRGB_BLOCK_EXT"},
        {1000288011, "VK_FORMAT_ASTC_4x4x4_SFLOAT_BLOCK_EXT"},
        {1000288012, "VK_FORMAT_ASTC_5x4x4_UNORM_BLOCK_EXT"},
        {1000288013, "VK_FORMAT_ASTC_5x4x4_SRGB_BLOCK_EXT"},
        {1000288014, "VK_FORMAT_ASTC_5x4x4_SFLOAT_BLOCK_EXT"},
        {1000288015, "VK_FORMAT_ASTC_5x5x4_UNORM_BLOCK_EXT"},
        {1000288016, "VK_FORMAT_ASTC_5x5x4_SRGB_BLOCK_EXT"},
        {1000288017, "VK_FORMAT_ASTC_5x5x4_SFLOAT_BLOCK_EXT"},
        {1000288018, "VK_FORMAT_ASTC_5x5x5_UNORM_BLOCK_EXT"},
        {1000288019, "VK_FORMAT_ASTC_5x5x5_SRGB_BLOCK_EXT"},
        {1000288020, "VK_FORMAT_ASTC_5x5x5_SFLOAT_BLOCK_EXT"},
        {1000288021, "VK_FORMAT_ASTC_6x5x5_UNORM_BLOCK_EXT"},
        {1000288022, "VK_FORMAT_ASTC_6x5x5_SRGB_BLOCK_EXT"},
        {1000288023, "VK_FORMAT_ASTC_6x5x5_SFLOAT_BLOCK_EXT"},
        {1000288024, "VK_FORMAT_ASTC_6x6x5_UNORM_BLOCK_EXT"},
        {1000288025, "VK_FORMAT_ASTC_6x6x5_SRGB_BLOCK_EXT"},
        {1000288026, "VK_FORMAT_ASTC_6x6x5_SFLOAT_BLOCK_EXT"},
        {1000288027, "VK_FORMAT_ASTC_6x6x6_UNORM_BLOCK_EXT"},
        {1000288028, "VK_FORMAT_ASTC_6x6x6_SRGB_BLOCK_EXT"},
        {1000288029, "VK_FORMAT_ASTC_6x6x6_SFLOAT_BLOCK_EXT"},
        {1000330000, "VK_FORMAT_G8_B8R8_2PLANE_444_UNORM"},
        {1000330001, "VK_FORMAT_G10X6_B10X6R10X6_2PLANE_444_UNORM_3PACK16"},
        {1000330002, "VK_FORMAT_G12X4_B12X4R12X4_2PLANE_444_UNORM_3PACK16"},
        {1000330003, "VK_FORMAT_G16_B16R16_2PLANE_444_UNORM"},
        {1000340000, "VK_FORMAT_A4R4G4B4_UNORM_PACK16"},
        {1000340001, "VK_FORMAT_A4B4G4R4_UNORM_PACK16"},
        {1000460000, "VK_FORMAT_R8_BOOL_ARM"},
        {1000460001, "VK_FORMAT_R16_SFLOAT_FPENCODING_BFLOAT16_ARM"},
        {1000460002, "VK_FORMAT_R8_SFLOAT_FPENCODING_FLOAT8E4M3_ARM"},
        {1000460003, "VK_FORMAT_R8_SFLOAT_FPENCODING_FLOAT8E5M2_ARM"},
        {1000464000, "VK_FORMAT_R16G16_SFIXED5_NV"},
        {1000470000, "VK_FORMAT_A1B5G5R5_UNORM_PACK16"},
        {1000470001, "VK_FORMAT_A8_UNORM"},
        {1000609000, "VK_FORMAT_R10X6_UINT_PACK16_ARM"},
        {1000609001, "VK_FORMAT_R10X6G10X6_UINT_2PACK16_ARM"},
        {1000609002, "VK_FORMAT_R10X6G10X6B10X6A10X6_UINT_4PACK16_ARM"},
        {1000609003, "VK_FORMAT_R12X4_UINT_PACK16_ARM"},
        {1000609004, "VK_FORMAT_R12X4G12X4_UINT_2PACK16_ARM"},
        {1000609005, "VK_FORMAT_R12X4G12X4B12X4A12X4_UINT_4PACK16_ARM"},
        {1000609006, "VK_FORMAT_R14X2_UINT_PACK16_ARM"},
        {1000609007, "VK_FORMAT_R14X2G14X2_UINT_2PACK16_ARM"},
        {1000609008, "VK_FORMAT_R14X2G14X2B14X2A14X2_UINT_4PACK16_ARM"},
        {1000609009, "VK_FORMAT_R14X2_UNORM_PACK16_ARM"},
        {1000609010, "VK_FORMAT_R14X2G14X2_UNORM_2PACK16_ARM"},
        {1000609011, "VK_FORMAT_R14X2G14X2B14X2A14X2_UNORM_4PACK16_ARM"},
        {1000609012, "VK_FORMAT_G14X2_B14X2R14X2_2PLANE_420_UNORM_3PACK16_ARM"},
        {1000609013, "VK_FORMAT_G14X2_B14X2R14X2_2PLANE_422_UNORM_3PACK16_ARM"},
    };
    for (const auto& item : names) if (item.first == value) return item.second;
    return "VK_FORMAT_" + std::to_string(value);
}

std::vector<VkFormat> knownFormatValues() {
    static const int32_t values[] = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180,181,182,183,184,1000054000,1000054001,1000054002,1000054003,1000054004,1000054005,1000054006,1000054007,1000066000,1000066001,1000066002,1000066003,1000066004,1000066005,1000066006,1000066007,1000066008,1000066009,1000066010,1000066011,1000066012,1000066013,1000156000,1000156001,1000156002,1000156003,1000156004,1000156005,1000156006,1000156007,1000156008,1000156009,1000156010,1000156011,1000156012,1000156013,1000156014,1000156015,1000156016,1000156017,1000156018,1000156019,1000156020,1000156021,1000156022,1000156023,1000156024,1000156025,1000156026,1000156027,1000156028,1000156029,1000156030,1000156031,1000156032,1000156033,1000288000,1000288001,1000288002,1000288003,1000288004,1000288005,1000288006,1000288007,1000288008,1000288009,1000288010,1000288011,1000288012,1000288013,1000288014,1000288015,1000288016,1000288017,1000288018,1000288019,1000288020,1000288021,1000288022,1000288023,1000288024,1000288025,1000288026,1000288027,1000288028,1000288029,1000330000,1000330001,1000330002,1000330003,1000340000,1000340001,1000460000,1000460001,1000460002,1000460003,1000464000,1000470000,1000470001,1000609000,1000609001,1000609002,1000609003,1000609004,1000609005,1000609006,1000609007,1000609008,1000609009,1000609010,1000609011,1000609012,1000609013};
    std::vector<VkFormat> result;
    result.reserve(sizeof(values) / sizeof(values[0]));
    for (const int32_t value : values) result.push_back(static_cast<VkFormat>(value));
    return result;
}

bool hasExtension(const std::vector<VkExtensionProperties>& values, const char* name);

static bool shouldQueryFormat(VkFormat format, uint32_t apiVersion, const std::vector<VkExtensionProperties>& devExts) {
    const int32_t value = static_cast<int32_t>(format);
    auto has = [&](const char* name) { return hasExtension(devExts, name); };
    if (value >= 1000054000 && value <= 1000054007) return has("VK_IMG_format_pvrtc");
    if (value >= 1000066000 && value <= 1000066013) return has("VK_EXT_texture_compression_astc_hdr");
    if (value >= 1000156000 && value <= 1000156033) return VK_API_VERSION_MINOR(apiVersion) >= 1 || has("VK_KHR_sampler_ycbcr_conversion");
    if (value >= 1000288000 && value <= 1000288029) return has("VK_EXT_texture_compression_astc_3d");
    if (value >= 1000330000 && value <= 1000330003) return has("VK_EXT_ycbcr_2plane_444_formats");
    if (value >= 1000340000 && value <= 1000340001) return has("VK_EXT_4444_formats");
    if (value == 1000460000) return has("VK_ARM_tensors");
    if (value == 1000460001) return has("VK_ARM_tensors") && has("VK_KHR_shader_bfloat16");
    if (value == 1000460002 || value == 1000460003) return has("VK_ARM_tensors") && has("VK_EXT_shader_float8");
    if (value == 1000464000) return has("VK_NV_optical_flow");
    if (value >= 1000609000 && value <= 1000609013) return has("VK_ARM_format_pack");
    if (value >= 1000470000 && value <= 1000470001) return VK_API_VERSION_MINOR(apiVersion) >= 4;
    return true;
}

std::string presentModeName(VkPresentModeKHR value) {
    switch (value) {
        case VK_PRESENT_MODE_IMMEDIATE_KHR: return "VK_PRESENT_MODE_IMMEDIATE_KHR";
        case VK_PRESENT_MODE_MAILBOX_KHR: return "VK_PRESENT_MODE_MAILBOX_KHR";
        case VK_PRESENT_MODE_FIFO_KHR: return "VK_PRESENT_MODE_FIFO_KHR";
        case VK_PRESENT_MODE_FIFO_RELAXED_KHR: return "VK_PRESENT_MODE_FIFO_RELAXED_KHR";
        default: return "VK_PRESENT_MODE_" + std::to_string(static_cast<int32_t>(value));
    }
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
        case VK_COLOR_SPACE_SRGB_NONLINEAR_KHR: return "sRGB";
        case VK_COLOR_SPACE_EXTENDED_SRGB_NONLINEAR_EXT: return "scRGB";
        case VK_COLOR_SPACE_PASS_THROUGH_EXT: return "Pass-through";
        case VK_COLOR_SPACE_DISPLAY_NATIVE_AMD: return "Display Native";
        default: return "Unknown / Unrecognized";
    }
}

std::string driverVersionText(uint32_t vendorId, uint32_t driverVersion) {
    if (vendorId == 0x10DEu) {
        const uint32_t major = (driverVersion >> 22) & 0x3FFu;
        const uint32_t minor = (driverVersion >> 14) & 0x0FFu;
        const uint32_t branch = (driverVersion >> 6) & 0x0FFu;
        const uint32_t build = driverVersion & 0x3Fu;
        return std::to_string(major) + "." + std::to_string(minor) + "." + std::to_string(branch) + "." + std::to_string(build);
    }
    return std::to_string(driverVersion);
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

bool hasExtension(const std::vector<VkExtensionProperties>& values, const char* name);

std::vector<VkExtensionProperties> instanceExtensions(VulkanApi& api) {
    if (!api.enumerateInstanceExtensionProperties) return {};
    for (uint32_t attempt = 0; attempt < 4; ++attempt) {
        uint32_t count = 0;
        const VkResult countResult = api.enumerateInstanceExtensionProperties(nullptr, &count, nullptr);
        if (countResult != VK_SUCCESS && countResult != VK_INCOMPLETE) return {};
        if (count > kMaxExtensionEntries) return {};
        std::vector<VkExtensionProperties> values(count);
        const VkResult dataResult = count ? api.enumerateInstanceExtensionProperties(nullptr, &count, values.data()) : VK_SUCCESS;
        if (dataResult == VK_SUCCESS) {
            values.resize(count);
            return values;
        }
        if (dataResult != VK_INCOMPLETE) return {};
    }
    return {};
}

std::vector<VkLayerProperties> instanceLayers(VulkanApi& api) {
    if (!api.enumerateInstanceLayerProperties) return {};
    for (uint32_t attempt = 0; attempt < 4; ++attempt) {
        uint32_t count = 0;
        const VkResult countResult = api.enumerateInstanceLayerProperties(&count, nullptr);
        if (countResult != VK_SUCCESS || count > kMaxLayerEntries) return {};
        std::vector<VkLayerProperties> values(count);
        const VkResult dataResult = count ? api.enumerateInstanceLayerProperties(&count, values.data()) : VK_SUCCESS;
        if (dataResult == VK_SUCCESS) {
            values.resize(count);
            return values;
        }
        if (dataResult != VK_INCOMPLETE) return {};
    }
    return {};
}

std::vector<VkExtensionProperties> instanceLayerExtensions(VulkanApi& api, const char* layerName);
std::vector<VkExtensionProperties> deviceLayerExtensions(VulkanApi& api, VkPhysicalDevice device, const char* layerName);

std::string layersJson(VulkanApi& api, const std::vector<VkLayerProperties>& values) {
    std::ostringstream out;
    out << '[';
    for (size_t i = 0; i < values.size(); ++i) {
        if (i) out << ',';
        out << "{\"name\":" << jsonString(values[i].layerName)
            << ",\"description\":" << jsonString(values[i].description)
            << ",\"specVersion\":" << values[i].specVersion
            << ",\"implementationVersion\":" << values[i].implementationVersion
            << ",\"extensions\":";
        const auto extensions = instanceLayerExtensions(api, values[i].layerName);
        out << '[';
        for (size_t j = 0; j < extensions.size(); ++j) {
            if (j) out << ',';
            out << "{\"name\":" << jsonString(extensions[j].extensionName) << ",\"specVersion\":" << extensions[j].specVersion << '}';
        }
        out << "]}";
    }
    out << ']';
    return out.str();
}

std::string deviceLayersJson(VulkanApi& api, VkPhysicalDevice device) {
    if (!api.enumerateDeviceLayerProperties) return "[]";
    std::vector<VkLayerProperties> layers;
    for (uint32_t attempt = 0; attempt < 4; ++attempt) {
        uint32_t count = 0;
        VkResult result = api.enumerateDeviceLayerProperties(device, &count, nullptr);
        if (result != VK_SUCCESS && result != VK_INCOMPLETE) return "[]";
        if (count > kMaxLayerEntries) return "[]";
        if (count == 0) { layers.clear(); break; }
        layers.resize(count);
        result = api.enumerateDeviceLayerProperties(device, &count, layers.data());
        if (result == VK_SUCCESS) { layers.resize(count); break; }
        if (result != VK_INCOMPLETE) return "[]";
        layers.clear();
    }
    std::ostringstream out;
    out << '[';
    for (size_t i = 0; i < layers.size(); ++i) {
        if (i) out << ',';
        out << "{\"name\":" << jsonString(layers[i].layerName)
            << ",\"description\":" << jsonString(layers[i].description)
            << ",\"specVersion\":" << layers[i].specVersion
            << ",\"implementationVersion\":" << layers[i].implementationVersion
            << ",\"extensions\":";
        const auto extensions = deviceLayerExtensions(api, device, layers[i].layerName);
        out << '[';
        for (size_t j = 0; j < extensions.size(); ++j) {
            if (j) out << ',';
            out << "{\"name\":" << jsonString(extensions[j].extensionName) << ",\"specVersion\":" << extensions[j].specVersion << '}';
        }
        out << "]}";
    }
    out << ']';
    return out.str();
}

std::vector<VkExtensionProperties> deviceExtensions(VulkanApi& api, VkPhysicalDevice device) {
    if (!api.enumerateDeviceExtensionProperties) return {};
    for (uint32_t attempt = 0; attempt < 4; ++attempt) {
        uint32_t count = 0;
        const VkResult countResult = api.enumerateDeviceExtensionProperties(device, nullptr, &count, nullptr);
        if (countResult != VK_SUCCESS && countResult != VK_INCOMPLETE) return {};
        if (count > kMaxExtensionEntries) return {};
        std::vector<VkExtensionProperties> values(count);
        const VkResult dataResult = count ? api.enumerateDeviceExtensionProperties(device, nullptr, &count, values.data()) : VK_SUCCESS;
        if (dataResult == VK_SUCCESS) {
            values.resize(count);
            return values;
        }
        if (dataResult != VK_INCOMPLETE) return {};
    }
    return {};
}

bool hasExtension(const std::vector<VkExtensionProperties>& values, const char* name) {
    return std::any_of(values.begin(), values.end(), [name](const VkExtensionProperties& value) { return std::strcmp(value.extensionName, name) == 0; });
}

std::pair<VkResult, std::vector<VkPhysicalDevice>> enumeratePhysicalDevicesRobust(VulkanApi& api, VkInstance instance) {
    if (!api.enumeratePhysicalDevices) return {VK_ERROR_INITIALIZATION_FAILED, {}};
    for (uint32_t attempt = 0; attempt < 4; ++attempt) {
        uint32_t count = 0;
        VkResult countResult = api.enumeratePhysicalDevices(instance, &count, nullptr);
        if (countResult != VK_SUCCESS && countResult != VK_INCOMPLETE) return {countResult, {}};
        if (count > kMaxPhysicalDeviceEntries) return {VK_ERROR_OUT_OF_HOST_MEMORY, {}};
        if (count == 0) return {VK_SUCCESS, {}};
        std::vector<VkPhysicalDevice> devices(count);
        VkResult dataResult = api.enumeratePhysicalDevices(instance, &count, devices.data());
        if (dataResult == VK_SUCCESS) {
            devices.resize(count);
            return {VK_SUCCESS, std::move(devices)};
        }
        if (dataResult != VK_INCOMPLETE) return {dataResult, {}};
    }
    return {VK_INCOMPLETE, {}};
}

std::vector<VkExtensionProperties> instanceLayerExtensions(VulkanApi& api, const char* layerName) {
    if (!api.enumerateInstanceExtensionProperties) return {};
    for (uint32_t attempt = 0; attempt < 4; ++attempt) {
        uint32_t count = 0;
        VkResult countResult = api.enumerateInstanceExtensionProperties(layerName, &count, nullptr);
        if (countResult != VK_SUCCESS && countResult != VK_INCOMPLETE) return {};
        if (count > kMaxExtensionEntries) return {};
        if (count == 0) return {};
        std::vector<VkExtensionProperties> values(count);
        VkResult dataResult = api.enumerateInstanceExtensionProperties(layerName, &count, values.data());
        if (dataResult == VK_SUCCESS) {
            values.resize(count);
            return values;
        }
        if (dataResult != VK_INCOMPLETE) return {};
    }
    return {};
}

std::vector<VkExtensionProperties> deviceLayerExtensions(VulkanApi& api, VkPhysicalDevice device, const char* layerName) {
    if (!api.enumerateDeviceExtensionProperties) return {};
    for (uint32_t attempt = 0; attempt < 4; ++attempt) {
        uint32_t count = 0;
        VkResult countResult = api.enumerateDeviceExtensionProperties(device, layerName, &count, nullptr);
        if (countResult != VK_SUCCESS && countResult != VK_INCOMPLETE) return {};
        if (count > kMaxExtensionEntries) return {};
        if (count == 0) return {};
        std::vector<VkExtensionProperties> values(count);
        VkResult dataResult = api.enumerateDeviceExtensionProperties(device, layerName, &count, values.data());
        if (dataResult == VK_SUCCESS) {
            values.resize(count);
            return values;
        }
        if (dataResult != VK_INCOMPLETE) return {};
    }
    return {};
}


std::vector<const char*> buildQueryInstanceExtensions(VulkanApi& api, const std::vector<VkExtensionProperties>* available = nullptr) {
    std::vector<VkExtensionProperties> owned;
    const std::vector<VkExtensionProperties>* extensions = available;
    if (!extensions) {
        owned = instanceExtensions(api);
        extensions = &owned;
    }
    std::vector<const char*> result;
    if (hasExtension(*extensions, "VK_KHR_get_physical_device_properties2")) {
        result.push_back("VK_KHR_get_physical_device_properties2");
    }
    return result;
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

void appendVersionedFeatures(std::ostringstream& out, uint32_t apiVersion, VulkanApi& api, VkPhysicalDevice device, uint32_t targetMinor) {
    const uint32_t minor = VK_API_VERSION_MINOR(apiVersion);
    if (!api.getPhysicalDeviceFeatures2 || targetMinor < 1 || targetMinor > 4 || minor < targetMinor) {
        out << "[]";
        return;
    }
    out << '[';
    if (targetMinor == 4) {
        VkPhysicalDeviceVulkan14Features features{};
        features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_4_FEATURES;
        VkPhysicalDeviceFeatures2 base{};
        base.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2;
        base.pNext = &features;
        api.queryFeatures2(device, &base);
        const struct FeatureField { const char* name; VkBool32 value; } fields[] = {
            {"globalPriorityQuery", features.globalPriorityQuery}, {"shaderSubgroupRotate", features.shaderSubgroupRotate}, {"shaderSubgroupRotateClustered", features.shaderSubgroupRotateClustered},
            {"shaderFloatControls2", features.shaderFloatControls2}, {"shaderExpectAssume", features.shaderExpectAssume}, {"rectangularLines", features.rectangularLines},
            {"bresenhamLines", features.bresenhamLines}, {"smoothLines", features.smoothLines}, {"stippledRectangularLines", features.stippledRectangularLines},
            {"stippledBresenhamLines", features.stippledBresenhamLines}, {"stippledSmoothLines", features.stippledSmoothLines}, {"vertexAttributeInstanceRateDivisor", features.vertexAttributeInstanceRateDivisor},
            {"vertexAttributeInstanceRateZeroDivisor", features.vertexAttributeInstanceRateZeroDivisor}, {"indexTypeUint8", features.indexTypeUint8}, {"dynamicRenderingLocalRead", features.dynamicRenderingLocalRead},
            {"maintenance5", features.maintenance5}, {"maintenance6", features.maintenance6}, {"pipelineProtectedAccess", features.pipelineProtectedAccess},
            {"pipelineRobustness", features.pipelineRobustness}, {"hostImageCopy", features.hostImageCopy}, {"pushDescriptor", features.pushDescriptor}
        };
        for (size_t i = 0; i < sizeof(fields) / sizeof(fields[0]); ++i) {
            const auto& field = fields[i];
            if (i) out << ',';
            out << "{\"name\":" << jsonString("Vulkan 1.4 · " + std::string(field.name)) << ",\"supported\":" << jsonBool(field.value == VK_TRUE) << '}';
        }
    } else if (targetMinor == 1) {
        VkPhysicalDeviceVulkan11Features f{};
        f.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_1_FEATURES;
        VkPhysicalDeviceFeatures2 base{};
        base.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2;
        base.pNext = &f;
        api.queryFeatures2(device, &base);
        const struct F { const char* name; VkBool32 value; } fields[] = {
            {"storageBuffer16BitAccess", f.storageBuffer16BitAccess}, {"uniformAndStorageBuffer16BitAccess", f.uniformAndStorageBuffer16BitAccess},
            {"storagePushConstant16", f.storagePushConstant16}, {"storageInputOutput16", f.storageInputOutput16}, {"multiview", f.multiview},
            {"multiviewGeometryShader", f.multiviewGeometryShader}, {"multiviewTessellationShader", f.multiviewTessellationShader},
            {"variablePointersStorageBuffer", f.variablePointersStorageBuffer}, {"variablePointers", f.variablePointers},
            {"protectedMemory", f.protectedMemory}, {"samplerYcbcrConversion", f.samplerYcbcrConversion}, {"shaderDrawParameters", f.shaderDrawParameters}
        };
        for (size_t i=0;i<sizeof(fields)/sizeof(fields[0]);++i){ if(i) out << ','; out << "{\"name\":" << jsonString(std::string("Vulkan 1.1 · ")+fields[i].name) << ",\"supported\":" << jsonBool(fields[i].value==VK_TRUE) << '}'; }
    } else if (targetMinor == 2) {
        VkPhysicalDeviceVulkan12Features f{};
        f.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_2_FEATURES;
        VkPhysicalDeviceFeatures2 base{};
        base.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2;
        base.pNext = &f;
        api.queryFeatures2(device, &base);
        const struct F { const char* name; VkBool32 value; } fields[] = {
            {"samplerMirrorClampToEdge", f.samplerMirrorClampToEdge}, {"drawIndirectCount", f.drawIndirectCount}, {"storageBuffer8BitAccess", f.storageBuffer8BitAccess},
            {"uniformAndStorageBuffer8BitAccess", f.uniformAndStorageBuffer8BitAccess}, {"storagePushConstant8", f.storagePushConstant8}, {"shaderBufferInt64Atomics", f.shaderBufferInt64Atomics},
            {"shaderSharedInt64Atomics", f.shaderSharedInt64Atomics}, {"shaderFloat16", f.shaderFloat16}, {"shaderInt8", f.shaderInt8}, {"descriptorIndexing", f.descriptorIndexing},
            {"shaderInputAttachmentArrayDynamicIndexing", f.shaderInputAttachmentArrayDynamicIndexing}, {"shaderUniformTexelBufferArrayDynamicIndexing", f.shaderUniformTexelBufferArrayDynamicIndexing},
            {"shaderStorageTexelBufferArrayDynamicIndexing", f.shaderStorageTexelBufferArrayDynamicIndexing}, {"shaderUniformBufferArrayNonUniformIndexing", f.shaderUniformBufferArrayNonUniformIndexing},
            {"shaderSampledImageArrayNonUniformIndexing", f.shaderSampledImageArrayNonUniformIndexing}, {"shaderStorageBufferArrayNonUniformIndexing", f.shaderStorageBufferArrayNonUniformIndexing},
            {"shaderStorageImageArrayNonUniformIndexing", f.shaderStorageImageArrayNonUniformIndexing}, {"shaderInputAttachmentArrayNonUniformIndexing", f.shaderInputAttachmentArrayNonUniformIndexing},
            {"shaderUniformTexelBufferArrayNonUniformIndexing", f.shaderUniformTexelBufferArrayNonUniformIndexing}, {"shaderStorageTexelBufferArrayNonUniformIndexing", f.shaderStorageTexelBufferArrayNonUniformIndexing},
            {"descriptorBindingUniformBufferUpdateAfterBind", f.descriptorBindingUniformBufferUpdateAfterBind}, {"descriptorBindingSampledImageUpdateAfterBind", f.descriptorBindingSampledImageUpdateAfterBind},
            {"descriptorBindingStorageImageUpdateAfterBind", f.descriptorBindingStorageImageUpdateAfterBind}, {"descriptorBindingStorageBufferUpdateAfterBind", f.descriptorBindingStorageBufferUpdateAfterBind},
            {"descriptorBindingUniformTexelBufferUpdateAfterBind", f.descriptorBindingUniformTexelBufferUpdateAfterBind}, {"descriptorBindingStorageTexelBufferUpdateAfterBind", f.descriptorBindingStorageTexelBufferUpdateAfterBind},
            {"descriptorBindingUpdateUnusedWhilePending", f.descriptorBindingUpdateUnusedWhilePending}, {"descriptorBindingPartiallyBound", f.descriptorBindingPartiallyBound},
            {"descriptorBindingVariableDescriptorCount", f.descriptorBindingVariableDescriptorCount}, {"runtimeDescriptorArray", f.runtimeDescriptorArray}, {"samplerFilterMinmax", f.samplerFilterMinmax},
            {"scalarBlockLayout", f.scalarBlockLayout}, {"imagelessFramebuffer", f.imagelessFramebuffer}, {"uniformBufferStandardLayout", f.uniformBufferStandardLayout},
            {"shaderSubgroupExtendedTypes", f.shaderSubgroupExtendedTypes}, {"separateDepthStencilLayouts", f.separateDepthStencilLayouts}, {"hostQueryReset", f.hostQueryReset},
            {"timelineSemaphore", f.timelineSemaphore}, {"bufferDeviceAddress", f.bufferDeviceAddress}, {"bufferDeviceAddressCaptureReplay", f.bufferDeviceAddressCaptureReplay},
            {"bufferDeviceAddressMultiDevice", f.bufferDeviceAddressMultiDevice}, {"vulkanMemoryModel", f.vulkanMemoryModel}, {"vulkanMemoryModelDeviceScope", f.vulkanMemoryModelDeviceScope},
            {"vulkanMemoryModelAvailabilityVisibilityChains", f.vulkanMemoryModelAvailabilityVisibilityChains}, {"shaderOutputViewportIndex", f.shaderOutputViewportIndex},
            {"shaderOutputLayer", f.shaderOutputLayer}, {"subgroupBroadcastDynamicId", f.subgroupBroadcastDynamicId}
        };
        for (size_t i=0;i<sizeof(fields)/sizeof(fields[0]);++i){ if(i) out << ','; out << "{\"name\":" << jsonString(std::string("Vulkan 1.2 · ")+fields[i].name) << ",\"supported\":" << jsonBool(fields[i].value==VK_TRUE) << '}'; }
    } else {
        VkPhysicalDeviceVulkan13Features f{};
        f.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_3_FEATURES;
        VkPhysicalDeviceFeatures2 base{};
        base.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2;
        base.pNext = &f;
        api.queryFeatures2(device, &base);
        const struct F { const char* name; VkBool32 value; } fields[] = {
            {"robustImageAccess", f.robustImageAccess}, {"inlineUniformBlock", f.inlineUniformBlock}, {"descriptorBindingInlineUniformBlockUpdateAfterBind", f.descriptorBindingInlineUniformBlockUpdateAfterBind},
            {"pipelineCreationCacheControl", f.pipelineCreationCacheControl}, {"privateData", f.privateData}, {"shaderDemoteToHelperInvocation", f.shaderDemoteToHelperInvocation},
            {"shaderTerminateInvocation", f.shaderTerminateInvocation}, {"subgroupSizeControl", f.subgroupSizeControl}, {"computeFullSubgroups", f.computeFullSubgroups},
            {"synchronization2", f.synchronization2}, {"textureCompressionASTC_HDR", f.textureCompressionASTC_HDR}, {"shaderZeroInitializeWorkgroupMemory", f.shaderZeroInitializeWorkgroupMemory},
            {"dynamicRendering", f.dynamicRendering}, {"shaderIntegerDotProduct", f.shaderIntegerDotProduct}, {"maintenance4", f.maintenance4}
        };
        for (size_t i=0;i<sizeof(fields)/sizeof(fields[0]);++i){ if(i) out << ','; out << "{\"name\":" << jsonString(std::string("Vulkan 1.3 · ")+fields[i].name) << ",\"supported\":" << jsonBool(fields[i].value==VK_TRUE) << '}'; }
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
template <typename T, std::enable_if_t<std::is_integral_v<T> || std::is_enum_v<T>, int> = 0>
void appendProperty(std::ostringstream& out, bool& first, const char* section, const char* name, T value) {
    if constexpr (std::is_signed_v<T>) appendProperty(out, first, section, name, std::to_string(static_cast<int64_t>(value)));
    else appendProperty(out, first, section, name, std::to_string(static_cast<uint64_t>(value)));
}
void appendBoolProperty(std::ostringstream& out, bool& first, const char* section, const char* name, VkBool32 value) { appendProperty(out, first, section, name, value == VK_TRUE ? "true" : "false"); }
void appendProperty(std::ostringstream& out, bool& first, const char* section, const char* name, const char* value) { appendProperty(out, first, section, name, std::string(value ? value : "")); }


void generatedEmitString(std::vector<GeneratedField>& dst, const char* section, const char* name, const char* data, size_t count) {
    size_t length = 0;
    while (length < count && data[length] != '\0') ++length;
    dst.push_back({false, section, name, std::string(data, length)});
}
void generatedEmitString(std::vector<GeneratedField>& dst, const char* section, const char* name, const std::string& value) {
    dst.push_back({false, section, name, value});
}
void generatedEmitHexTyped(std::vector<GeneratedField>& dst, const char* section, const char* name, const char* type, const uint8_t* data, size_t count) {
    std::string value = std::string("type=") + (type ? type : "unknown") + "; bytes=" + std::to_string(count) + "; 0x" + hexBytes(data, count);
    dst.push_back({false, section, name, value});
}
void generatedEmitHexTyped(std::vector<GeneratedField>& dst, const char* section, const char* name, const uint8_t* data, size_t count) {
    generatedEmitHexTyped(dst, section, name, "raw", data, count);
}
void generatedEmitBool(std::vector<GeneratedField>& dst, const char* section, const char* name, VkBool32 value) {
    std::string fullName = std::string(section) + " · " + name;
    dst.push_back({true, section, fullName, value == VK_TRUE ? "true" : "false"});
}

template <typename T> void generatedEmitNumeric(std::vector<GeneratedField>& dst, const char* section, const char* name, T value) {
    std::ostringstream ss;
    if constexpr (std::is_signed_v<T>) {
        ss << value;
        if (value >= 0) ss << " (0x" << std::uppercase << std::hex << static_cast<std::make_unsigned_t<T>>(value) << ")";
    } else {
        ss << value << " (0x" << std::uppercase << std::hex << value << ")";
    }
    dst.push_back({false, section, name, ss.str()});
}

template <typename T> void generatedEmitArray(std::vector<GeneratedField>& dst, const char* section, const char* name, const T* values, size_t count) {
    std::string joined;
    joined.reserve(count * 28);
    for (size_t i = 0; i < count; ++i) {
        if (i) joined += ", ";
        std::ostringstream ss;
        if constexpr (std::is_integral_v<T>) {
            ss << values[i] << " (0x" << std::uppercase << std::hex << static_cast<std::make_unsigned_t<T>>(values[i]) << ")";
        } else {
            ss << values[i];
        }
        joined += ss.str();
    }
    dst.push_back({false, section, name, joined});
}


template <typename T> void generatedEmitAuto(std::vector<GeneratedField>& dst, const char* section, const char* name, T&& value) {
    using U = std::remove_reference_t<T>;
    using D = std::decay_t<T>;
    if constexpr (std::is_pointer_v<U>) {
        generatedEmitString(dst, section, name, std::string("unavailable; pointer field"));
    } else if constexpr (std::is_array_v<U>) {
        using E = std::remove_extent_t<U>;
        if constexpr (std::is_same_v<std::remove_cv_t<E>, char>) {
            generatedEmitString(dst, section, name, value, sizeof(value));
        } else {
            generatedEmitHexTyped(dst, section, name, "raw", reinterpret_cast<const uint8_t*>(&value), sizeof(value));
        }
    } else if constexpr (std::is_same_v<D, VkBool32>) {
        generatedEmitBool(dst, section, name, value);
    } else if constexpr (std::is_integral_v<D> || std::is_enum_v<D>) {
        generatedEmitNumeric(dst, section, name, value);
    } else if constexpr (std::is_floating_point_v<D>) {
        generatedEmitString(dst, section, name, std::to_string(value));
    } else {
        generatedEmitHexTyped(dst, section, name, "raw", reinterpret_cast<const uint8_t*>(&value), sizeof(value));
    }
}


#include <extension_field_coverage_generated.inc>
#include <extension_field_coverage_parity.inc>
#include <runtime_extension_pnext_generated.inc>
#include <runtime_extension_pnext_parity.inc>

struct GeneratedPNextHeader {
    uint32_t sType;
    const void* pNext;
};

static void captureGeneratedPNextFields(std::vector<GeneratedField>& dst, const void* chain) {
    const void* visited[128]{};
    size_t visitedCount = 0;
    const void* current = chain;
    while (current && visitedCount < 128) {
        bool seen = false;
        for (size_t i = 0; i < visitedCount; ++i) { if (visited[i] == current) { seen = true; break; } }
        if (seen) break;
        visited[visitedCount++] = current;
        const auto* node = reinterpret_cast<const GeneratedPNextHeader*>(current);
        appendGeneratedStructFields(dst, node->sType, const_cast<void*>(current));
        appendParityStructFields(dst, node->sType, const_cast<void*>(current));
        current = node->pNext;
    }
}

void VulkanApi::queryProperties2(VkPhysicalDevice device, VkPhysicalDeviceProperties2* properties) {
    if (!getPhysicalDeviceProperties2) return;
    getPhysicalDeviceProperties2(device, properties);
    if (captureGeneratedFields && properties) captureGeneratedPNextFields(generatedFields, properties->pNext);
}

void VulkanApi::queryFeatures2(VkPhysicalDevice device, VkPhysicalDeviceFeatures2* features) {
    if (!getPhysicalDeviceFeatures2) return;
    getPhysicalDeviceFeatures2(device, features);
    if (captureGeneratedFields && features) captureGeneratedPNextFields(generatedFields, features->pNext);
}


bool getDevicePropertiesStable(VulkanApi& api, VkPhysicalDevice device, VkPhysicalDeviceProperties& out) {
    if (!api.getPhysicalDeviceProperties) return false;
    api.getPhysicalDeviceProperties(device, &out);
    return true;
}

bool getDevicePropertiesPrimary(VulkanApi& api, VkPhysicalDevice device, VkPhysicalDeviceProperties& out) {
    if (api.getPhysicalDeviceProperties2) {
        VkPhysicalDeviceProperties2 p2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2, nullptr, {}};
        api.queryProperties2(device, &p2);
        out = p2.properties;
        return true;
    }
    if (api.getPhysicalDeviceProperties) {
        api.getPhysicalDeviceProperties(device, &out);
        return true;
    }
    return false;
}

bool getDeviceFeaturesPrimary(VulkanApi& api, VkPhysicalDevice device, VkPhysicalDeviceFeatures& out) {
    if (api.getPhysicalDeviceFeatures) {
        api.getPhysicalDeviceFeatures(device, &out);
        return true;
    }
    if (api.getPhysicalDeviceFeatures2) {
        VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, nullptr, {}};
        api.queryFeatures2(device, &f2);
        out = f2.features;
        return true;
    }
    return false;
}

bool getDeviceMemoryPrimary(VulkanApi& api, VkPhysicalDevice device, VkPhysicalDeviceMemoryProperties& out) {
    if (api.getPhysicalDeviceMemoryProperties) {
        api.getPhysicalDeviceMemoryProperties(device, &out);
        return true;
    }
    if (api.getPhysicalDeviceMemoryProperties2) {
        VkPhysicalDeviceMemoryProperties2 m2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MEMORY_PROPERTIES_2, nullptr, {}};
        api.getPhysicalDeviceMemoryProperties2(device, &m2);
        out = m2.memoryProperties;
        return true;
    }
    return false;
}

uint32_t getQueueFamilyPropertiesPrimary(VulkanApi& api, VkPhysicalDevice device, std::vector<VkQueueFamilyProperties>& out) {
    if (api.getPhysicalDeviceQueueFamilyProperties) {
        uint32_t count = 0;
        api.getPhysicalDeviceQueueFamilyProperties(device, &count, nullptr);
        if (count > kMaxQueueFamilyEntries) return 0;
        out.resize(count);
        if (count) api.getPhysicalDeviceQueueFamilyProperties(device, &count, out.data());
        out.resize(count);
        return count;
    }
    if (api.getPhysicalDeviceQueueFamilyProperties2) {
        uint32_t count = 0;
        api.getPhysicalDeviceQueueFamilyProperties2(device, &count, nullptr);
        if (count > kMaxQueueFamilyEntries) return 0;
        std::vector<VkQueueFamilyProperties2> values(count);
        for (auto& value : values) value.sType = VK_STRUCTURE_TYPE_QUEUE_FAMILY_PROPERTIES_2;
        if (count) api.getPhysicalDeviceQueueFamilyProperties2(device, &count, values.data());
        out.resize(count);
        for (uint32_t i = 0; i < count; ++i) out[i] = values[i].queueFamilyProperties;
        return count;
    }
    return 0;
}


void appendCoreProperties(std::ostringstream& out, uint32_t apiVersion, VulkanApi& api, VkPhysicalDevice device, const std::vector<VkExtensionProperties>& devExts, uint32_t targetMinor, bool includeExtensions) {
    out << '[';
    bool first = true;
    if (!api.getPhysicalDeviceProperties2 || VK_API_VERSION_MINOR(apiVersion) < 1) { out << ']'; return; }

    VkPhysicalDeviceProperties2 base{};
    base.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2;
    VkPhysicalDeviceVulkan11Properties p11{}; p11.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_1_PROPERTIES;
    VkPhysicalDeviceVulkan12Properties p12{}; p12.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_2_PROPERTIES;
    VkPhysicalDeviceVulkan13Properties p13{}; p13.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_3_PROPERTIES;
    VkPhysicalDeviceVulkan14Properties p14{}; p14.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_4_PROPERTIES;
    VkPhysicalDeviceFragmentDensityMapPropertiesEXT fdm{}; fdm.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_PROPERTIES_EXT;
    VkPhysicalDeviceFragmentDensityMap2PropertiesEXT fdm2{}; fdm2.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_2_PROPERTIES_EXT;
    const bool hasFdm = hasExtension(devExts, "VK_EXT_fragment_density_map");
    const bool hasFdm2 = hasExtension(devExts, "VK_EXT_fragment_density_map2");
    const uint32_t apiMinor = VK_API_VERSION_MINOR(apiVersion);
    const uint32_t minor = std::min(apiMinor, targetMinor);
    if (targetMinor == 1 && apiMinor >= 1) base.pNext = &p11;
    if (targetMinor == 2 && apiMinor >= 2) base.pNext = &p12;
    if (targetMinor == 3 && apiMinor >= 3) base.pNext = &p13;
    if (targetMinor == 4 && apiMinor >= 4) base.pNext = &p14;
    void* extensionTail = nullptr;
    if (includeExtensions && hasFdm2) extensionTail = &fdm2;
    if (includeExtensions && hasFdm) { fdm.pNext = extensionTail; extensionTail = &fdm; }
    if (targetMinor == 1) p11.pNext = extensionTail;
    else if (targetMinor == 2) p12.pNext = extensionTail;
    else if (targetMinor == 3) p13.pNext = extensionTail;
    else p14.pNext = extensionTail;
    base.pNext = targetMinor == 1 ? static_cast<void*>(&p11) : targetMinor == 2 ? static_cast<void*>(&p12) : targetMinor == 3 ? static_cast<void*>(&p13) : static_cast<void*>(&p14);
    constexpr uint32_t kMaxVulkan14LayoutEntries = 65536;
    std::vector<VkImageLayout> copySrc;
    std::vector<VkImageLayout> copyDst;
    api.queryProperties2(device, &base);
    if (targetMinor == 4 && minor >= 4) {
        const bool copySrcWithinLimit = p14.copySrcLayoutCount <= kMaxVulkan14LayoutEntries;
        const bool copyDstWithinLimit = p14.copyDstLayoutCount <= kMaxVulkan14LayoutEntries;
        if (copySrcWithinLimit) copySrc.resize(p14.copySrcLayoutCount);
        if (copyDstWithinLimit) copyDst.resize(p14.copyDstLayoutCount);
        p14.pCopySrcLayouts = copySrcWithinLimit && !copySrc.empty() ? copySrc.data() : nullptr;
        p14.pCopyDstLayouts = copyDstWithinLimit && !copyDst.empty() ? copyDst.data() : nullptr;
        if (copySrcWithinLimit && copyDstWithinLimit) api.queryProperties2(device, &base);
    }
    if (targetMinor == 1 && minor >= 1) {
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
    if (targetMinor == 2 && minor >= 2) {
        appendProperty(out, first, "Core 1.2", "driverID", p12.driverID);
        appendProperty(out, first, "Vulkan Registry", "baseline", vulkanscope_registry::kBaseline);
    appendProperty(out, first, "Vulkan Registry", "queryEngine", vulkanscope_registry::kMode);
    appendProperty(out, first, "Vulkan Registry", "implementedPhysicalDeviceStructCount", static_cast<uint64_t>(vulkanscope_registry::kImplementedPhysicalDeviceStructCount));
    appendProperty(out, first, "Vulkan Registry", "runtimeExtensionTokenCount", static_cast<uint64_t>(vulkanscope_registry::kRuntimeExtensionTokenCount));
    appendProperty(out, first, "Vulkan Registry", "validatedRuntimeQueryGroupCount", static_cast<uint64_t>(vulkanscope_registry::kValidatedRuntimeQueryGroupCount));
    appendProperty(out, first, "Core 1.2", "driverName", p12.driverName);
        appendProperty(out, first, "Core 1.2", "driverInfo", p12.driverInfo);
        appendProperty(out, first, "Core 1.2", "conformanceVersion", std::to_string(p12.conformanceVersion.major) + "." + std::to_string(p12.conformanceVersion.minor) + "." + std::to_string(p12.conformanceVersion.subminor) + "." + std::to_string(p12.conformanceVersion.patch));
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
    if (targetMinor == 3 && minor >= 3) {
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
    if (targetMinor == 4 && minor >= 4) {
        appendProperty(out, first, "Core 1.4", "lineSubPixelPrecisionBits", p14.lineSubPixelPrecisionBits);
        appendProperty(out, first, "Core 1.4", "maxVertexAttribDivisor", p14.maxVertexAttribDivisor);
        appendBoolProperty(out, first, "Core 1.4", "supportsNonZeroFirstInstance", p14.supportsNonZeroFirstInstance);
        appendProperty(out, first, "Core 1.4", "maxPushDescriptors", p14.maxPushDescriptors);
        appendBoolProperty(out, first, "Core 1.4", "dynamicRenderingLocalReadDepthStencilAttachments", p14.dynamicRenderingLocalReadDepthStencilAttachments);
        appendBoolProperty(out, first, "Core 1.4", "dynamicRenderingLocalReadMultisampledAttachments", p14.dynamicRenderingLocalReadMultisampledAttachments);
        appendBoolProperty(out, first, "Core 1.4", "earlyFragmentMultisampleCoverageAfterSampleCounting", p14.earlyFragmentMultisampleCoverageAfterSampleCounting);
        appendBoolProperty(out, first, "Core 1.4", "earlyFragmentSampleMaskTestBeforeSampleCounting", p14.earlyFragmentSampleMaskTestBeforeSampleCounting);
        appendBoolProperty(out, first, "Core 1.4", "depthStencilSwizzleOneSupport", p14.depthStencilSwizzleOneSupport);
        appendBoolProperty(out, first, "Core 1.4", "polygonModePointSize", p14.polygonModePointSize);
        appendBoolProperty(out, first, "Core 1.4", "nonStrictSinglePixelWideLinesUseParallelogram", p14.nonStrictSinglePixelWideLinesUseParallelogram);
        appendBoolProperty(out, first, "Core 1.4", "nonStrictWideLinesUseParallelogram", p14.nonStrictWideLinesUseParallelogram);
        appendBoolProperty(out, first, "Core 1.4", "blockTexelViewCompatibleMultipleLayers", p14.blockTexelViewCompatibleMultipleLayers);
        appendProperty(out, first, "Core 1.4", "maxCombinedImageSamplerDescriptorCount", p14.maxCombinedImageSamplerDescriptorCount);
        appendBoolProperty(out, first, "Core 1.4", "fragmentShadingRateClampCombinerInputs", p14.fragmentShadingRateClampCombinerInputs);
        appendProperty(out, first, "Core 1.4", "defaultRobustnessStorageBuffers", p14.defaultRobustnessStorageBuffers);
        appendProperty(out, first, "Core 1.4", "defaultRobustnessUniformBuffers", p14.defaultRobustnessUniformBuffers);
        appendProperty(out, first, "Core 1.4", "defaultRobustnessVertexInputs", p14.defaultRobustnessVertexInputs);
        appendProperty(out, first, "Core 1.4", "defaultRobustnessImages", p14.defaultRobustnessImages);
        appendProperty(out, first, "Core 1.4", "copySrcLayoutCount", p14.copySrcLayoutCount);
        appendProperty(out, first, "Core 1.4", "copyDstLayoutCount", p14.copyDstLayoutCount);
        if (p14.copySrcLayoutCount <= kMaxVulkan14LayoutEntries) {
            std::string srcLayouts;
            for (uint32_t i = 0; i < p14.copySrcLayoutCount && i < copySrc.size(); ++i) { if (i) srcLayouts += ", "; srcLayouts += std::to_string(copySrc[i]); }
            appendProperty(out, first, "Core 1.4", "copySrcLayouts", srcLayouts);
        } else {
            appendProperty(out, first, "Core 1.4", "copySrcLayouts", "Unavailable: safety cap exceeded");
        }
        if (p14.copyDstLayoutCount <= kMaxVulkan14LayoutEntries) {
            std::string dstLayouts;
            for (uint32_t i = 0; i < p14.copyDstLayoutCount && i < copyDst.size(); ++i) { if (i) dstLayouts += ", "; dstLayouts += std::to_string(copyDst[i]); }
            appendProperty(out, first, "Core 1.4", "copyDstLayouts", dstLayouts);
        } else {
            appendProperty(out, first, "Core 1.4", "copyDstLayouts", "Unavailable: safety cap exceeded");
        }
        appendProperty(out, first, "Core 1.4", "optimalTilingLayoutUUID", hexBytes(p14.optimalTilingLayoutUUID, 16));
        appendBoolProperty(out, first, "Core 1.4", "identicalMemoryTypeRequirements", p14.identicalMemoryTypeRequirements);
    }
    if (includeExtensions && hasFdm) {
        appendProperty(out, first, "VK_EXT_fragment_density_map", "minFragmentDensityTexelSize", std::to_string(fdm.minFragmentDensityTexelSize.width) + " × " + std::to_string(fdm.minFragmentDensityTexelSize.height));
        appendProperty(out, first, "VK_EXT_fragment_density_map", "maxFragmentDensityTexelSize", std::to_string(fdm.maxFragmentDensityTexelSize.width) + " × " + std::to_string(fdm.maxFragmentDensityTexelSize.height));
        appendBoolProperty(out, first, "VK_EXT_fragment_density_map", "fragmentDensityInvocations", fdm.fragmentDensityInvocations);
    }
    if (includeExtensions && hasFdm2) {
        appendBoolProperty(out, first, "VK_EXT_fragment_density_map2", "subsampledLoads", fdm2.subsampledLoads);
        appendBoolProperty(out, first, "VK_EXT_fragment_density_map2", "subsampledCoarseReconstructionEarlyAccess", fdm2.subsampledCoarseReconstructionEarlyAccess);
        appendProperty(out, first, "VK_EXT_fragment_density_map2", "maxSubsampledArrayLayers", fdm2.maxSubsampledArrayLayers);
        appendProperty(out, first, "VK_EXT_fragment_density_map2", "maxDescriptorSetSubsampledSamplers", fdm2.maxDescriptorSetSubsampledSamplers);
    }
    out << ']';
}


std::string registryCoverageJson() {
    std::ostringstream out;
    out << "{\"baseline\":" << jsonString(vulkanscope_registry::kBaseline)
        << ",\"mode\":" << jsonString(vulkanscope_registry::kMode)
        << ",\"implementedPhysicalDeviceStructCount\":" << vulkanscope_registry::kImplementedPhysicalDeviceStructCount
        << ",\"validatedRuntimeQueryGroupCount\":" << vulkanscope_registry::kValidatedRuntimeQueryGroupCount
        << ",\"runtimeExtensionTokenCount\":" << vulkanscope_registry::kRuntimeExtensionTokenCount
        << ",\"catalogSchemaVersion\":" << vulkanscope_registry::kCatalogSchemaVersion
        << ",\"headerBaseline\":" << jsonString(vulkanscope_registry::kHeaderBaseline)
        << ",\"reportSchema\":" << jsonString(vulkanscope_registry::kReportSchema)
        << ",\"instanceDependencyCandidateCount\":" << vulkanscope_registry::kInstanceDependencyCandidates.size()
        << ",\"implementedPhysicalDeviceStructs\":[";
    bool firstStruct = true;
    for (std::size_t i = 0; i < vulkanscope_registry::kImplementedPhysicalDeviceStructs.size(); ++i) {
        const char* value = vulkanscope_registry::kImplementedPhysicalDeviceStructs[i];
        if (!value) continue;
        if (!firstStruct) out << ',';
        firstStruct = false;
        out << jsonString(value);
    }
    out << "],\"validatedRuntimeQueryGroups\":[";
    for (std::size_t i = 0; i < vulkanscope_registry::kValidatedRuntimeQueryGroups.size(); ++i) {
        if (i) out << ',';
        out << jsonString(vulkanscope_registry::kValidatedRuntimeQueryGroups[i]);
    }
    out << "],\"queryDescriptors\":[";
    for (std::size_t i = 0; i < vulkanscope_registry::kValidatedQueryDescriptors.size(); ++i) {
        if (i) out << ',';
        const auto& descriptor = vulkanscope_registry::kValidatedQueryDescriptors[i];
        out << "{\"group\":" << jsonString(descriptor.group)
            << ",\"scope\":" << jsonString(descriptor.scope)
            << ",\"extension\":" << jsonString(descriptor.extension)
            << ",\"minApiMinor\":" << descriptor.minApiMinor
            << ",\"queryKind\":" << jsonString(descriptor.queryKind) << "}";
    }
    out << "]}";
    return out.str();
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


std::string collectVulkanSurface(jobject surfaceObject, JNIEnv* env, const char* driverMode, const char* driverIcdPath, const char* driverBundlePath, const char* hookLibDir) {
    if (!surfaceObject || !env) {
        return "{\"status\":\"unavailable\",\"reason\":\"No live Android Surface was supplied to the isolated surface probe.\",\"devices\":[]}";
    }
    VulkanApi api;
    if (!api.open(driverMode, driverIcdPath, driverBundlePath, hookLibDir)) {
        return std::string("{\"status\":\"unavailable\",\"reason\":") + jsonString(api.openError.empty() ? "Vulkan loader unavailable" : api.openError) + ",\"devices\":[]}";
    }
    uint32_t loaderVersion = VK_API_VERSION_1_0;
    if (api.enumerateInstanceVersion && api.enumerateInstanceVersion(&loaderVersion) != VK_SUCCESS) loaderVersion = VK_API_VERSION_1_0;
    const auto instanceExts = instanceExtensions(api);
    const bool androidSurfaceAvailable = hasExtension(instanceExts, "VK_KHR_android_surface");
    const bool surfaceAvailable = hasExtension(instanceExts, "VK_KHR_surface");
    const bool swapchainColorspaceAvailable = hasExtension(instanceExts, "VK_EXT_swapchain_colorspace");
    const bool surfaceCapabilities2Available = hasExtension(instanceExts, "VK_KHR_get_surface_capabilities2");
    if (!surfaceAvailable || !androidSurfaceAvailable) {
        return std::string("{\"status\":\"unavailable\",\"reason\":") + jsonString(!surfaceAvailable ? "VK_KHR_surface is not exposed by the Vulkan instance." : "VK_KHR_android_surface is not exposed by the Vulkan instance.") + ",\"devices\":[]}";
    }

    std::vector<const char*> enabledExtensions;
    enabledExtensions.push_back("VK_KHR_surface");
    enabledExtensions.push_back("VK_KHR_android_surface");
    if (swapchainColorspaceAvailable) enabledExtensions.push_back("VK_EXT_swapchain_colorspace");
    if (surfaceCapabilities2Available) enabledExtensions.push_back("VK_KHR_get_surface_capabilities2");

    VkInstance instance = nullptr;
    uint32_t selectedApiVersion = VK_API_VERSION_1_0;
    g_probeStage = 20;
    const VkResult createResult = api.createInstanceCompatible(std::min(loaderVersion, VK_API_VERSION_1_1), enabledExtensions, &instance, &selectedApiVersion);
    if (createResult != VK_SUCCESS || !instance) {
        return std::string("{\"status\":\"unavailable\",\"reason\":") + jsonString(std::string("Unable to create isolated surface Vulkan instance. VkResult=") + std::to_string(createResult)) + ",\"devices\":[]}";
    }
    g_probeStage = 21;
    if (!api.loadInstanceFunctions(instance) || !api.createAndroidSurfaceKHR || !api.destroySurfaceKHR || !api.getPhysicalDeviceSurfaceCapabilitiesKHR || !api.getPhysicalDeviceSurfaceFormatsKHR || !api.getPhysicalDeviceSurfacePresentModesKHR || !api.getPhysicalDeviceSurfaceSupportKHR) {
        api.destroyInstance(instance, nullptr);
        return "{\"status\":\"unavailable\",\"reason\":\"Required Android WSI entry points are unavailable.\",\"devices\":[]}";
    }
    if (surfaceCapabilities2Available && (!api.getPhysicalDeviceSurfaceCapabilities2KHR || !api.getPhysicalDeviceSurfaceFormats2KHR)) {
        api.destroyInstance(instance, nullptr);
        return "{\"status\":\"unavailable\",\"reason\":\"VK_KHR_get_surface_capabilities2 is advertised but its required surface query entry points are unavailable.\",\"devices\":[]}";
    }

    g_probeStage = 22;
    const auto surfaceDevicesResult = enumeratePhysicalDevicesRobust(api, instance);
    const VkResult surfaceDeviceResult = surfaceDevicesResult.first;
    std::vector<VkPhysicalDevice> devices = surfaceDevicesResult.second;
    const uint32_t deviceCount = static_cast<uint32_t>(devices.size());
    if (surfaceDeviceResult != VK_SUCCESS || deviceCount == 0) {
        api.destroyInstance(instance, nullptr);
        return std::string("{\"status\":\"unavailable\",\"reason\":") + jsonString(std::string("Surface probe physical-device enumeration failed. VkResult=") + std::to_string(surfaceDeviceResult)) + ",\"devices\":[]}";
    }

    g_probeStage = 23;
    ANativeWindow* window = ANativeWindow_fromSurface(env, surfaceObject);
    if (!window) {
        api.destroyInstance(instance, nullptr);
        return "{\"status\":\"unavailable\",\"reason\":\"Unable to obtain a native window from the Android Surface.\",\"devices\":[]}";
    }
    VkAndroidSurfaceCreateInfoKHR surfaceInfo{VK_STRUCTURE_TYPE_ANDROID_SURFACE_CREATE_INFO_KHR, nullptr, 0, window};
    VkSurfaceKHR surface = VK_NULL_HANDLE;
    const VkResult surfaceResult = api.createAndroidSurfaceKHR(instance, &surfaceInfo, nullptr, &surface);
    ANativeWindow_release(window);
    if (surfaceResult != VK_SUCCESS || surface == VK_NULL_HANDLE) {
        api.destroyInstance(instance, nullptr);
        return std::string("{\"status\":\"unavailable\",\"reason\":") + jsonString(std::string("vkCreateAndroidSurfaceKHR failed. VkResult=") + std::to_string(surfaceResult)) + ",\"devices\":[]}";
    }

    std::ostringstream out;
    out << "{\"status\":\"available\",\"reason\":\"\",\"surfaceColorSpaceExtensionAvailable\":" << jsonBool(swapchainColorspaceAvailable) << ",\"surfaceColorSpaceExtensionEnabled\":" << jsonBool(swapchainColorspaceAvailable) << ",\"selectedApiVersion\":" << jsonString(versionString(selectedApiVersion)) << ",\"devices\":[";
    for (uint32_t deviceIndex = 0; deviceIndex < deviceCount; ++deviceIndex) {
        if (deviceIndex) out << ',';
        VkPhysicalDevice device = devices[deviceIndex];
        VkPhysicalDeviceProperties properties{};
        getDevicePropertiesPrimary(api, device, properties);
        out << "{\"name\":" << jsonString(std::string(properties.deviceName)) << ",\"vendorId\":" << properties.vendorID << ",\"deviceId\":" << properties.deviceID;
        g_probeStage = 24;
        uint32_t queueCount = 0;
        api.getPhysicalDeviceQueueFamilyProperties(device, &queueCount, nullptr);
        bool queueSafetyRejected = queueCount > kMaxQueueFamilyEntries;
        if (queueSafetyRejected) queueCount = 0;
        std::vector<VkQueueFamilyProperties> queues(queueCount);
        if (queueCount) api.getPhysicalDeviceQueueFamilyProperties(device, &queueCount, queues.data());
        bool presentationSupported = false;
        std::vector<bool> presentationByQueue(queueCount, false);
        for (uint32_t i = 0; i < queueCount; ++i) {
            VkBool32 supported = VK_FALSE;
            const VkResult supportResult = api.getPhysicalDeviceSurfaceSupportKHR(device, i, surface, &supported);
            if (supportResult == VK_SUCCESS && supported == VK_TRUE) {
                presentationSupported = true;
                presentationByQueue[i] = true;
            }
        }
        out << ",\"surface\":{\"available\":true,\"presentationSupported\":" << jsonBool(presentationSupported) << ",\"queueQuerySafetyRejected\":" << jsonBool(queueSafetyRejected);
        if (presentationSupported) {
            VkSurfaceCapabilitiesKHR caps{};
            VkResult capResult = VK_ERROR_EXTENSION_NOT_PRESENT;
            if (surfaceCapabilities2Available) {
                VkPhysicalDeviceSurfaceInfo2KHR info{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SURFACE_INFO_2_KHR, nullptr, surface};
                VkSurfaceCapabilities2KHR caps2{VK_STRUCTURE_TYPE_SURFACE_CAPABILITIES_2_KHR, nullptr, {}};
                capResult = api.getPhysicalDeviceSurfaceCapabilities2KHR(device, &info, &caps2);
                if (capResult == VK_SUCCESS) caps = caps2.surfaceCapabilities;
            } else {
                capResult = api.getPhysicalDeviceSurfaceCapabilitiesKHR(device, surface, &caps);
            }
            out << ",\"capabilityResult\":" << capResult;
            if (capResult == VK_SUCCESS) {
                out << ",\"minImageCount\":" << caps.minImageCount
                    << ",\"maxImageCount\":" << caps.maxImageCount
                    << ",\"currentExtent\":" << jsonString(std::to_string(caps.currentExtent.width) + " × " + std::to_string(caps.currentExtent.height))
                    << ",\"minExtent\":" << jsonString(std::to_string(caps.minImageExtent.width) + " × " + std::to_string(caps.minImageExtent.height))
                    << ",\"maxExtent\":" << jsonString(std::to_string(caps.maxImageExtent.width) + " × " + std::to_string(caps.maxImageExtent.height))
                    << ",\"maxImageArrayLayers\":" << caps.maxImageArrayLayers
                    << ",\"supportedTransforms\":" << caps.supportedTransforms
                    << ",\"currentTransform\":" << caps.currentTransform
                    << ",\"supportedCompositeAlpha\":" << caps.supportedCompositeAlpha
                    << ",\"supportedUsageFlags\":" << caps.supportedUsageFlags;
            }
        }
        g_probeStage = 25;
        uint32_t formatCount = 0;
        VkResult formatCountResult = VK_ERROR_EXTENSION_NOT_PRESENT;
        VkResult formatResult = VK_ERROR_EXTENSION_NOT_PRESENT;
        bool formatQuerySecondAttempted = false;
        bool formatQuerySafetyRejected = false;
        std::vector<VkSurfaceFormat2KHR> surfaceFormats2;
        std::vector<VkSurfaceFormatKHR> surfaceFormats;
        if (surfaceCapabilities2Available) {
            VkPhysicalDeviceSurfaceInfo2KHR info{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SURFACE_INFO_2_KHR, nullptr, surface};
            formatCountResult = api.getPhysicalDeviceSurfaceFormats2KHR(device, &info, &formatCount, nullptr);
            if ((formatCountResult == VK_SUCCESS || formatCountResult == VK_INCOMPLETE) && formatCount <= kMaxSurfaceFormatEntries) {
                surfaceFormats2.resize(formatCount);
                for (auto& entry : surfaceFormats2) {
                    entry = {VK_STRUCTURE_TYPE_SURFACE_FORMAT_2_KHR, nullptr, {VK_FORMAT_UNDEFINED, VK_COLOR_SPACE_SRGB_NONLINEAR_KHR}};
                }
                formatQuerySecondAttempted = true;
                if (formatCount == 0) {
                    formatResult = VK_SUCCESS;
                } else {
                    formatResult = api.getPhysicalDeviceSurfaceFormats2KHR(device, &info, &formatCount, surfaceFormats2.data());
                    if (formatCount < surfaceFormats2.size()) surfaceFormats2.resize(formatCount);
                }
            } else if (formatCountResult == VK_SUCCESS || formatCountResult == VK_INCOMPLETE) {
                formatQuerySafetyRejected = true;
            }
        } else {
            formatCountResult = api.getPhysicalDeviceSurfaceFormatsKHR(device, surface, &formatCount, nullptr);
            if ((formatCountResult == VK_SUCCESS || formatCountResult == VK_INCOMPLETE) && formatCount <= kMaxSurfaceFormatEntries) {
                surfaceFormats.resize(formatCount);
                formatQuerySecondAttempted = true;
                if (formatCount == 0) {
                    formatResult = VK_SUCCESS;
                } else {
                    formatResult = api.getPhysicalDeviceSurfaceFormatsKHR(device, surface, &formatCount, surfaceFormats.data());
                    if (formatCount < surfaceFormats.size()) surfaceFormats.resize(formatCount);
                }
            } else if (formatCountResult == VK_SUCCESS || formatCountResult == VK_INCOMPLETE) {
                formatQuerySafetyRejected = true;
            }
        }
        const size_t reportedFormatCount = surfaceCapabilities2Available ? surfaceFormats2.size() : surfaceFormats.size();
        out << ",\"surfaceFormats2Used\":" << jsonBool(surfaceCapabilities2Available)
            << ",\"formatQueryResult\":" << formatCountResult
            << ",\"formatQueryResultSecond\":" << formatResult
            << ",\"formatQuerySecondAttempted\":" << jsonBool(formatQuerySecondAttempted)
            << ",\"formatQuerySafetyRejected\":" << jsonBool(formatQuerySafetyRejected)
            << ",\"formatCount\":" << reportedFormatCount << ",\"formats\":[";
        for (size_t i = 0; i < reportedFormatCount; ++i) {
            if (i) out << ',';
            const VkSurfaceFormatKHR format = surfaceCapabilities2Available ? surfaceFormats2[i].surfaceFormat : surfaceFormats[i];
            out << "{\"format\":" << jsonString(formatName(format.format))
                << ",\"colorSpace\":" << jsonString(colorSpaceName(format.colorSpace))
                << ",\"class\":" << jsonString(colorSpaceClass(format.colorSpace))
                << ",\"description\":" << jsonString(colorSpaceDescription(format.colorSpace)) << '}';
        }
        out << "]";
        g_probeStage = 26;
        uint32_t modeCount = 0;
        const VkResult modeCountResult = api.getPhysicalDeviceSurfacePresentModesKHR(device, surface, &modeCount, nullptr);
        std::vector<VkPresentModeKHR> modes;
        if (modeCountResult == VK_SUCCESS || modeCountResult == VK_INCOMPLETE) {
            if (modeCount <= kMaxPresentModeEntries) {
                modes.resize(modeCount);
                if (modeCount) {
                    const VkResult modeResult = api.getPhysicalDeviceSurfacePresentModesKHR(device, surface, &modeCount, modes.data());
                    if (modeResult == VK_SUCCESS || modeResult == VK_INCOMPLETE) modes.resize(std::min<uint32_t>(modeCount, static_cast<uint32_t>(modes.size())));
                    else modes.clear();
                }
            }
        }
        out << ",\"presentModes\":[";
        for (size_t i = 0; i < modes.size(); ++i) {
            if (i) out << ',';
            const char* name = modes[i] == VK_PRESENT_MODE_IMMEDIATE_KHR ? "VK_PRESENT_MODE_IMMEDIATE_KHR" : modes[i] == VK_PRESENT_MODE_MAILBOX_KHR ? "VK_PRESENT_MODE_MAILBOX_KHR" : modes[i] == VK_PRESENT_MODE_FIFO_KHR ? "VK_PRESENT_MODE_FIFO_KHR" : modes[i] == VK_PRESENT_MODE_FIFO_RELAXED_KHR ? "VK_PRESENT_MODE_FIFO_RELAXED_KHR" : modes[i] == VK_PRESENT_MODE_SHARED_DEMAND_REFRESH_KHR ? "VK_PRESENT_MODE_SHARED_DEMAND_REFRESH_KHR" : modes[i] == VK_PRESENT_MODE_SHARED_CONTINUOUS_REFRESH_KHR ? "VK_PRESENT_MODE_SHARED_CONTINUOUS_REFRESH_KHR" : modes[i] == VK_PRESENT_MODE_FIFO_LATEST_READY_KHR ? "VK_PRESENT_MODE_FIFO_LATEST_READY_KHR" : "VK_PRESENT_MODE_UNKNOWN";
            out << jsonString(name);
        }
        out << "],\"queuePresentation\":[";
        bool firstPresentation = true;
        for (uint32_t i = 0; i < queueCount; ++i) {
            if (!firstPresentation) out << ',';
            firstPresentation = false;
            out << "{\"queueFamily\":" << i << ",\"supported\":" << jsonBool(presentationByQueue[i]) << '}';
        }
        out << "]}";
        out << '}';
    }
    out << "]}";
    api.destroySurfaceKHR(instance, surface, nullptr);
    api.destroyInstance(instance, nullptr);
    return out.str();
}


void publishProbeCheckpoint(const char* path, const std::string& text) {
    if (!path || path[0] == '\0' || text.empty()) return;
    const std::string tempPath = std::string(path) + ".checkpoint.tmp";
    unlink(tempPath.c_str());
    int fd = open(tempPath.c_str(), O_CREAT | O_TRUNC | O_WRONLY | O_CLOEXEC, 0600);
    if (fd < 0) return;
    const char* data = text.data();
    size_t remaining = text.size();
    while (remaining > 0) {
        const ssize_t written = write(fd, data, remaining);
        if (written <= 0) break;
        data += written;
        remaining -= static_cast<size_t>(written);
    }
    if (remaining == 0) {
        (void)fsync(fd);
        close(fd);
        if (rename(tempPath.c_str(), path) == 0) {
            g_probePartialPublished = 1;
            return;
        }
    } else {
        close(fd);
    }
    unlink(tempPath.c_str());
}

std::string collect(jobject surfaceObject, JNIEnv* env, const char* driverMode, const char* driverIcdPath, const char* driverBundlePath, const char* hookLibDir, const char* checkpointPath) {

    VulkanApi api;
    if (!api.open(driverMode, driverIcdPath, driverBundlePath, hookLibDir)) {
        return std::string("{\"error\":") + jsonString(api.openError.empty() ? "Vulkan loader unavailable" : api.openError) + "}";
    }

    uint32_t loaderVersion = VK_API_VERSION_1_0;
    if (api.enumerateInstanceVersion) { VkResult versionResult = api.enumerateInstanceVersion(&loaderVersion); if (versionResult != VK_SUCCESS) loaderVersion = VK_API_VERSION_1_0; }
    const auto instanceExts = instanceExtensions(api);
    const bool swapchainColorspaceAvailable = hasExtension(instanceExts, "VK_EXT_swapchain_colorspace");
    const bool surfaceExtensionAvailable = hasExtension(instanceExts, "VK_KHR_surface");
    const bool androidSurfaceExtensionAvailable = hasExtension(instanceExts, "VK_KHR_android_surface");
    const bool getSurfaceCapabilities2Available = hasExtension(instanceExts, "VK_KHR_get_surface_capabilities2");
    const bool hasLiveSurface = false;

    std::vector<const char*> enabledExtensions;
    if (hasLiveSurface) {
        enabledExtensions.push_back("VK_KHR_surface");
        enabledExtensions.push_back("VK_KHR_android_surface");
        if (swapchainColorspaceAvailable) enabledExtensions.push_back("VK_EXT_swapchain_colorspace");
        if (getSurfaceCapabilities2Available) enabledExtensions.push_back("VK_KHR_get_surface_capabilities2");
    }

    g_probeStage = 2;
    VkInstance instance = nullptr;
    uint32_t instanceApiVersion = VK_API_VERSION_1_0;
    const uint32_t safeBaseLoaderVersion = std::min(loaderVersion, VK_API_VERSION_1_1);
    enabledExtensions.clear();
    __android_log_print(ANDROID_LOG_INFO, "VulkanProbe", "base discovery create: loader=%s target<=1.1 extensions=0", versionString(loaderVersion).c_str());
    const VkResult result = api.createInstanceCompatible(safeBaseLoaderVersion, enabledExtensions, &instance, &instanceApiVersion);
    if (result != VK_SUCCESS || !instance) {
        std::ostringstream out;
        out << "{\"error\":\"vkCreateInstance failed: " << result << "\",\"loaderVersion\":" << jsonString(versionString(loaderVersion)) << ",\"requestedInstanceApiVersion\":" << jsonString(versionString(instanceApiVersion)) << '}';
        return out.str();
    }
    g_probeStage = 3;
    if (!api.loadInstanceFunctions(instance) ||
        !api.destroyInstance || !api.enumeratePhysicalDevices || !api.getPhysicalDeviceProperties ||
        !api.getPhysicalDeviceFeatures || !api.getPhysicalDeviceMemoryProperties ||
        !api.getPhysicalDeviceQueueFamilyProperties || !api.enumerateDeviceExtensionProperties) {
        if (api.destroyInstance) api.destroyInstance(instance, nullptr);
        return "{\"status\":\"unavailable\",\"reason\":\"Required Vulkan physical-device query entry points are unavailable.\",\"devices\":[]}";
    }

    VkSurfaceKHR liveSurface = VK_NULL_HANDLE;
    if (hasLiveSurface) {
        if (getSurfaceCapabilities2Available && (!api.getPhysicalDeviceSurfaceCapabilities2KHR || !api.getPhysicalDeviceSurfaceFormats2KHR)) {
            api.destroyInstance(instance, nullptr);
            return "{\"status\":\"available\",\"reason\":\"VK_KHR_get_surface_capabilities2 is advertised but its required entry points are unavailable.\",\"surfaceColorSpaceExtensionAvailable\":" + jsonBool(swapchainColorspaceAvailable) + ",\"surfaceColorSpaceExtensionEnabled\":false,\"deviceCount\":0,\"devices\":[]}";
        }
        g_probeStage = 20;
        ANativeWindow* nativeWindow = ANativeWindow_fromSurface(env, surfaceObject);
        if (!nativeWindow) {
            api.destroyInstance(instance, nullptr);
            return std::string("{\"status\":\"available\",\"reason\":") + jsonString("Unable to obtain a native window from the Android Surface.") + ",\"surfaceColorSpaceExtensionAvailable\":" + jsonBool(swapchainColorspaceAvailable) + ",\"surfaceColorSpaceExtensionEnabled\":false,\"deviceCount\":0,\"devices\":[]}";
        }
        VkAndroidSurfaceCreateInfoKHR createInfo{VK_STRUCTURE_TYPE_ANDROID_SURFACE_CREATE_INFO_KHR, nullptr, 0, nativeWindow};
        const VkResult surfaceResult = api.createAndroidSurfaceKHR ? api.createAndroidSurfaceKHR(instance, &createInfo, nullptr, &liveSurface) : VK_ERROR_EXTENSION_NOT_PRESENT;
        ANativeWindow_release(nativeWindow);
        if (surfaceResult != VK_SUCCESS || liveSurface == VK_NULL_HANDLE) {
            api.destroyInstance(instance, nullptr);
            return std::string("{\"status\":\"available\",\"reason\":") + jsonString(std::string("vkCreateAndroidSurfaceKHR failed. VkResult=") + std::to_string(surfaceResult)) + ",\"surfaceColorSpaceExtensionAvailable\":" + jsonBool(swapchainColorspaceAvailable) + ",\"surfaceColorSpaceExtensionEnabled\":false,\"deviceCount\":0,\"devices\":[]}";
        }
    }

    g_probeStage = 4;
    __android_log_print(ANDROID_LOG_INFO, "VulkanProbe", "base enumerate physical devices: instanceApi=%s", versionString(instanceApiVersion).c_str());
    const auto baseDevicesResult = enumeratePhysicalDevicesRobust(api, instance);
    const VkResult deviceEnumerationResult = baseDevicesResult.first;
    std::vector<VkPhysicalDevice> devices = baseDevicesResult.second;
    const uint32_t deviceCount = static_cast<uint32_t>(devices.size());
    __android_log_print(ANDROID_LOG_INFO, "VulkanProbe", "base enumerate result=%d count=%u", static_cast<int>(deviceEnumerationResult), deviceCount);
    if (deviceEnumerationResult != VK_SUCCESS) {
        api.destroyInstance(instance, nullptr);
        return std::string("{\"error\":\"vkEnumeratePhysicalDevices failed: ") + std::to_string(deviceEnumerationResult) + "\"}";
    }
    (void)surfaceObject;
    (void)env;

    g_probeStage = 6;
    std::vector<VkPhysicalDeviceProperties> cachedProperties(deviceCount);
    std::ostringstream checkpoint;
    checkpoint << "{\"status\":\"available\",\"reason\":\"\",\"baseReportComplete\":false,\"loaderVersion\":" << jsonString(versionString(loaderVersion));
    checkpoint << ",\"instanceApiVersion\":" << jsonString(versionString(instanceApiVersion));
    checkpoint << ",\"vulkanRegistryVersion\":\"1.4.357\",\"deviceCount\":" << deviceCount << ",\"devices\":[";
    for (uint32_t deviceIndex = 0; deviceIndex < deviceCount; ++deviceIndex) {
        if (deviceIndex) checkpoint << ',';
        __android_log_print(ANDROID_LOG_INFO, "VulkanProbe", "base preflight device[%u] properties begin", deviceIndex);
        if (!getDevicePropertiesStable(api, devices[deviceIndex], cachedProperties[deviceIndex])) {
            api.destroyInstance(instance, nullptr);
            return "{\"status\":\"unavailable\",\"reason\":\"No physical-device property query entry point is available.\",\"devices\":[]}";
        }
        const auto& p = cachedProperties[deviceIndex];
        __android_log_print(ANDROID_LOG_INFO, "VulkanProbe", "base preflight device[%u] properties end name=%s api=%s", deviceIndex, p.deviceName, versionString(p.apiVersion).c_str());
        checkpoint << "{\"name\":" << jsonString(std::string(p.deviceName))
                   << ",\"apiVersion\":" << jsonString(versionString(p.apiVersion))
                   << ",\"driverVersion\":" << jsonString(std::to_string(p.driverVersion))
                   << ",\"vendorId\":" << p.vendorID
                   << ",\"deviceId\":" << p.deviceID
                   << ",\"deviceType\":" << static_cast<uint32_t>(p.deviceType)
                   << ",\"features\":[],\"versionedFeatures\":[],\"queues\":[],\"memory\":{\"heapCount\":0,\"heaps\":[],\"typeCount\":0,\"types\":[]},\"formats\":[],\"detailedProperties\":[]}";
    }
    checkpoint << "]}";
    publishProbeCheckpoint(checkpointPath, checkpoint.str());
    __android_log_print(ANDROID_LOG_INFO, "VulkanProbe", "base preflight checkpoint published devices=%u", deviceCount);

    g_probeStage = 50;
    std::ostringstream out;
    out << "{\"status\":\"available\",\"reason\":\"\",\"baseReportComplete\":false,\"loaderVersion\":" << jsonString(versionString(loaderVersion));
    out << ",\"instanceApiVersion\":" << jsonString(versionString(instanceApiVersion));
    out << ",\"vulkanRegistryVersion\":\"1.4.357\"";
    out << ",\"surfaceColorSpaceExtensionAvailable\":" << jsonBool(swapchainColorspaceAvailable);
    out << ",\"surfaceExtensionAvailable\":" << jsonBool(surfaceExtensionAvailable);
    out << ",\"androidSurfaceExtensionAvailable\":" << jsonBool(androidSurfaceExtensionAvailable);
    out << ",\"surfaceColorSpaceExtensionEnabled\":false";
    out << ",\"deviceCount\":" << deviceCount;
    out << ",\"devices\":[";

    for (uint32_t deviceIndex = 0; deviceIndex < deviceCount; ++deviceIndex) {
        if (deviceIndex) out << ',';
        VkPhysicalDevice device = devices[deviceIndex];
        VkPhysicalDeviceProperties properties = cachedProperties[deviceIndex];
        g_probeStage = 7;
        __android_log_print(ANDROID_LOG_INFO, "VulkanProbe", "base device[%u] using cached properties name=%s api=%s", deviceIndex, properties.deviceName, versionString(properties.apiVersion).c_str());
        __android_log_print(ANDROID_LOG_INFO, "VulkanProbe", "base device[%u] basic features/memory/queues begin", deviceIndex);
        const uint32_t apiVersion = properties.apiVersion;
        const uint32_t driverVersion = properties.driverVersion;
        const uint32_t vendorId = properties.vendorID;
        const uint32_t deviceId = properties.deviceID;
        const uint32_t deviceType = static_cast<uint32_t>(properties.deviceType);
        const std::string deviceName(properties.deviceName);
        g_probeStage = 61;
        __android_log_print(ANDROID_LOG_INFO, "VulkanProbe", "base device[%u] extension enumeration begin", deviceIndex);
        const auto devExts = deviceExtensions(api, device);
        __android_log_print(ANDROID_LOG_INFO, "VulkanProbe", "base device[%u] extension enumeration end count=%zu", deviceIndex, devExts.size());
        g_probeStage = 62;
        const std::string deviceLayers = deviceLayersJson(api, device);
        __android_log_print(ANDROID_LOG_INFO, "VulkanProbe", "base device[%u] layer enumeration end bytes=%zu", deviceIndex, deviceLayers.size());
        {
            std::ostringstream extensionSnapshot;
            extensionSnapshot << "{\"status\":\"available\",\"reason\":\"\",\"baseReportComplete\":false,\"loaderVersion\":" << jsonString(versionString(loaderVersion))
                << ",\"instanceApiVersion\":" << jsonString(versionString(instanceApiVersion))
                << ",\"vulkanRegistryVersion\":\"1.4.357\",\"deviceCount\":" << deviceCount << ",\"devices\":[{\"name\":" << jsonString(deviceName)
                << ",\"apiVersion\":" << jsonString(versionString(apiVersion))
                << ",\"driverVersion\":" << jsonString(std::to_string(driverVersion))
                << ",\"driverVersionText\":" << jsonString(driverVersionText(vendorId, driverVersion))
                << ",\"vendorId\":" << vendorId << ",\"deviceId\":" << deviceId << ",\"deviceType\":" << deviceType
                << ",\"deviceExtensions\":" << extensionsJson(devExts, "Device")
                << ",\"deviceLayers\":" << deviceLayers
                << ",\"features\":[],\"versionedFeatures\":[],\"queues\":[],\"memory\":{\"heapCount\":0,\"heaps\":[],\"typeCount\":0,\"types\":[]},\"formats\":[],\"detailedProperties\":[],\"surface\":{\"available\":false}}]}";
            publishProbeCheckpoint(checkpointPath, extensionSnapshot.str());
            __android_log_print(ANDROID_LOG_INFO, "VulkanProbe", "base extension checkpoint published device=%u", deviceIndex);
        }
        const bool coreExtendedQueriesAvailable = api.getPhysicalDeviceProperties2 && api.getPhysicalDeviceFeatures2 && VK_API_VERSION_MINOR(apiVersion) >= 1;
        const char* extendedQueryStatus = coreExtendedQueriesAvailable ? "available" : (VK_API_VERSION_MINOR(apiVersion) >= 1 ? "unavailable" : "not_applicable");
        std::string extendedQueryReason = coreExtendedQueriesAvailable
            ? "Core Vulkan 1.1–1.4 feature and property data are collected by isolated validated core probes."
            : (VK_API_VERSION_MINOR(apiVersion) >= 1 ? std::string("The Vulkan 1.1+ physical-device query entry points are unavailable: properties2=") + (api.getPhysicalDeviceProperties2 ? "present" : "missing") + ", features2=" + (api.getPhysicalDeviceFeatures2 ? "present" : "missing") + ", KHR_get_physical_device_properties2=" + (hasExtension(instanceExts, "VK_KHR_get_physical_device_properties2") ? "advertised" : "not advertised") : "The device API version is below Vulkan 1.1.");
        VkPhysicalDeviceFeatures features{};
        getDeviceFeaturesPrimary(api, device, features);
        VkPhysicalDeviceMemoryProperties memory{};
        g_probeStage = 8;
        getDeviceMemoryPrimary(api, device, memory);
        const bool memoryHeapSafetyRejected = memory.memoryHeapCount > kMaxMemoryHeapEntries;
        const bool memoryTypeSafetyRejected = memory.memoryTypeCount > kMaxMemoryTypeEntries;
        const uint32_t memoryHeapCount = std::min(memory.memoryHeapCount, kMaxMemoryHeapEntries);
        const uint32_t memoryTypeCount = std::min(memory.memoryTypeCount, kMaxMemoryTypeEntries);
        uint32_t queueCount = 0;
        g_probeStage = 9;
        std::vector<VkQueueFamilyProperties> queues;
        queueCount = getQueueFamilyPropertiesPrimary(api, device, queues);
        const bool queueSafetyRejected = queueCount > kMaxQueueFamilyEntries;
        if (queueSafetyRejected) { queueCount = 0; queues.clear(); }
        __android_log_print(ANDROID_LOG_INFO, "VulkanProbe", "base device[%u] basic features/memory/queues end queueCount=%u", deviceIndex, queueCount);

        out << "{\"name\":" << jsonString(deviceName) << ",\"apiVersion\":" << jsonString(versionString(apiVersion))
            << ",\"driverVersion\":" << jsonString(std::to_string(driverVersion))
            << ",\"driverVersionText\":" << jsonString(driverVersionText(vendorId, driverVersion))
            << ",\"vendorId\":" << vendorId
            << ",\"deviceId\":" << deviceId << ",\"deviceType\":" << deviceType
            << ",\"queueQuerySafetyRejected\":" << jsonBool(queueSafetyRejected)
            << ",\"memoryHeapSafetyRejected\":" << jsonBool(memoryHeapSafetyRejected)
            << ",\"memoryTypeSafetyRejected\":" << jsonBool(memoryTypeSafetyRejected)
            << ",\"extendedQueryStatus\":" << jsonString(extendedQueryStatus)
            << ",\"extendedQueryReason\":" << jsonString(extendedQueryReason)
            << ",\"deviceExtensions\":" << extensionsJson(devExts, "Device") << ",\"deviceLayers\":" << deviceLayers << ",\"features\":[";
        const std::array<VkBool32, 55> coreFeatureValues = {
            features.robustBufferAccess, features.fullDrawIndexUint32, features.imageCubeArray, features.independentBlend, features.geometryShader,
            features.tessellationShader, features.sampleRateShading, features.dualSrcBlend, features.logicOp, features.multiDrawIndirect,
            features.drawIndirectFirstInstance, features.depthClamp, features.depthBiasClamp, features.fillModeNonSolid, features.depthBounds,
            features.wideLines, features.largePoints, features.alphaToOne, features.multiViewport, features.samplerAnisotropy,
            features.textureCompressionETC2, features.textureCompressionASTC_LDR, features.textureCompressionBC, features.occlusionQueryPrecise,
            features.pipelineStatisticsQuery, features.vertexPipelineStoresAndAtomics, features.fragmentStoresAndAtomics, features.shaderTessellationAndGeometryPointSize,
            features.shaderImageGatherExtended, features.shaderStorageImageExtendedFormats, features.shaderStorageImageMultisample, features.shaderStorageImageReadWithoutFormat,
            features.shaderStorageImageWriteWithoutFormat, features.shaderUniformBufferArrayDynamicIndexing, features.shaderSampledImageArrayDynamicIndexing,
            features.shaderStorageBufferArrayDynamicIndexing, features.shaderStorageImageArrayDynamicIndexing, features.shaderClipDistance, features.shaderCullDistance,
            features.shaderFloat64, features.shaderInt64, features.shaderInt16, features.shaderResourceResidency, features.shaderResourceMinLod, features.sparseBinding,
            features.sparseResidencyBuffer, features.sparseResidencyImage2D, features.sparseResidencyImage3D, features.sparseResidency2Samples,
            features.sparseResidency4Samples, features.sparseResidency8Samples, features.sparseResidency16Samples, features.sparseResidencyAliased,
            features.variableMultisampleRate, features.inheritedQueries
        };
        for (size_t i = 0; i < coreFeatureValues.size(); ++i) {
            if (i) out << ',';
            out << "{\"name\":" << jsonString(featureName(i)) << ",\"supported\":" << jsonBool(coreFeatureValues[i] == VK_TRUE) << '}';
        }
        out << "],\"limits\":[";
        const VkPhysicalDeviceLimits* limits = &properties.limits;
        const VkPhysicalDeviceSparseProperties* sparseProperties = &properties.sparseProperties;
        struct LimitField { const char* name; std::string value; };
        std::vector<LimitField> limitFields;
        auto addU32 = [&](const char* name, uint32_t value) { limitFields.push_back({name, std::to_string(value)}); };
        auto addI32 = [&](const char* name, int32_t value) { limitFields.push_back({name, std::to_string(value)}); };
        auto addU64 = [&](const char* name, uint64_t value) { limitFields.push_back({name, std::to_string(value)}); };
        auto addF32 = [&](const char* name, float value) { limitFields.push_back({name, std::to_string(value)}); };
        auto addBool = [&](const char* name, VkBool32 value) { limitFields.push_back({name, value == VK_TRUE ? "true" : "false"}); };
        auto addPair = [&](const char* name, uint32_t a, uint32_t b) { limitFields.push_back({name, std::to_string(a) + " × " + std::to_string(b)}); };
        auto addTriple = [&](const char* name, uint32_t a, uint32_t b, uint32_t c) { limitFields.push_back({name, std::to_string(a) + " × " + std::to_string(b) + " × " + std::to_string(c)}); };
        auto addFloatPair = [&](const char* name, float a, float b) { limitFields.push_back({name, std::to_string(a) + " … " + std::to_string(b)}); };
        addU32("maxImageDimension1D", limits->maxImageDimension1D); addU32("maxImageDimension2D", limits->maxImageDimension2D); addU32("maxImageDimension3D", limits->maxImageDimension3D); addU32("maxImageDimensionCube", limits->maxImageDimensionCube);
        addU32("maxImageArrayLayers", limits->maxImageArrayLayers); addU32("maxTexelBufferElements", limits->maxTexelBufferElements); addU32("maxUniformBufferRange", limits->maxUniformBufferRange); addU32("maxStorageBufferRange", limits->maxStorageBufferRange);
        addU32("maxPushConstantsSize", limits->maxPushConstantsSize); addU32("maxMemoryAllocationCount", limits->maxMemoryAllocationCount); addU32("maxSamplerAllocationCount", limits->maxSamplerAllocationCount); addU64("bufferImageGranularity", limits->bufferImageGranularity); addU64("sparseAddressSpaceSize", limits->sparseAddressSpaceSize);
        addU32("maxBoundDescriptorSets", limits->maxBoundDescriptorSets); addU32("maxPerStageDescriptorSamplers", limits->maxPerStageDescriptorSamplers); addU32("maxPerStageDescriptorUniformBuffers", limits->maxPerStageDescriptorUniformBuffers); addU32("maxPerStageDescriptorStorageBuffers", limits->maxPerStageDescriptorStorageBuffers); addU32("maxPerStageDescriptorSampledImages", limits->maxPerStageDescriptorSampledImages); addU32("maxPerStageDescriptorStorageImages", limits->maxPerStageDescriptorStorageImages); addU32("maxPerStageDescriptorInputAttachments", limits->maxPerStageDescriptorInputAttachments); addU32("maxPerStageResources", limits->maxPerStageResources);
        addU32("maxDescriptorSetSamplers", limits->maxDescriptorSetSamplers); addU32("maxDescriptorSetUniformBuffers", limits->maxDescriptorSetUniformBuffers); addU32("maxDescriptorSetUniformBuffersDynamic", limits->maxDescriptorSetUniformBuffersDynamic); addU32("maxDescriptorSetStorageBuffers", limits->maxDescriptorSetStorageBuffers); addU32("maxDescriptorSetStorageBuffersDynamic", limits->maxDescriptorSetStorageBuffersDynamic); addU32("maxDescriptorSetSampledImages", limits->maxDescriptorSetSampledImages); addU32("maxDescriptorSetStorageImages", limits->maxDescriptorSetStorageImages); addU32("maxDescriptorSetInputAttachments", limits->maxDescriptorSetInputAttachments);
        addU32("maxVertexInputAttributes", limits->maxVertexInputAttributes); addU32("maxVertexInputBindings", limits->maxVertexInputBindings); addU32("maxVertexInputAttributeOffset", limits->maxVertexInputAttributeOffset); addU32("maxVertexInputBindingStride", limits->maxVertexInputBindingStride); addU32("maxVertexOutputComponents", limits->maxVertexOutputComponents); addU32("maxTessellationGenerationLevel", limits->maxTessellationGenerationLevel); addU32("maxTessellationPatchSize", limits->maxTessellationPatchSize); addU32("maxTessellationControlPerVertexInputComponents", limits->maxTessellationControlPerVertexInputComponents); addU32("maxTessellationControlPerVertexOutputComponents", limits->maxTessellationControlPerVertexOutputComponents); addU32("maxTessellationControlPerPatchOutputComponents", limits->maxTessellationControlPerPatchOutputComponents); addU32("maxTessellationControlTotalOutputComponents", limits->maxTessellationControlTotalOutputComponents); addU32("maxTessellationEvaluationInputComponents", limits->maxTessellationEvaluationInputComponents); addU32("maxTessellationEvaluationOutputComponents", limits->maxTessellationEvaluationOutputComponents); addU32("maxGeometryShaderInvocations", limits->maxGeometryShaderInvocations); addU32("maxGeometryInputComponents", limits->maxGeometryInputComponents); addU32("maxGeometryOutputComponents", limits->maxGeometryOutputComponents); addU32("maxGeometryOutputVertices", limits->maxGeometryOutputVertices); addU32("maxGeometryTotalOutputComponents", limits->maxGeometryTotalOutputComponents);
        addU32("maxFragmentInputComponents", limits->maxFragmentInputComponents); addU32("maxFragmentOutputAttachments", limits->maxFragmentOutputAttachments); addU32("maxFragmentDualSrcAttachments", limits->maxFragmentDualSrcAttachments); addU32("maxFragmentCombinedOutputResources", limits->maxFragmentCombinedOutputResources); addU32("maxComputeSharedMemorySize", limits->maxComputeSharedMemorySize); addTriple("maxComputeWorkGroupCount", limits->maxComputeWorkGroupCount[0], limits->maxComputeWorkGroupCount[1], limits->maxComputeWorkGroupCount[2]); addU32("maxComputeWorkGroupInvocations", limits->maxComputeWorkGroupInvocations); addTriple("maxComputeWorkGroupSize", limits->maxComputeWorkGroupSize[0], limits->maxComputeWorkGroupSize[1], limits->maxComputeWorkGroupSize[2]);
        addU32("subPixelPrecisionBits", limits->subPixelPrecisionBits); addU32("subTexelPrecisionBits", limits->subTexelPrecisionBits); addU32("mipmapPrecisionBits", limits->mipmapPrecisionBits); addU32("maxDrawIndexedIndexValue", limits->maxDrawIndexedIndexValue); addU32("maxDrawIndirectCount", limits->maxDrawIndirectCount); addF32("maxSamplerLodBias", limits->maxSamplerLodBias); addF32("maxSamplerAnisotropy", limits->maxSamplerAnisotropy); addU32("maxViewports", limits->maxViewports); addPair("maxViewportDimensions", limits->maxViewportDimensions[0], limits->maxViewportDimensions[1]); addFloatPair("viewportBoundsRange", limits->viewportBoundsRange[0], limits->viewportBoundsRange[1]); addU32("viewportSubPixelBits", limits->viewportSubPixelBits); addU64("minMemoryMapAlignment", static_cast<uint64_t>(limits->minMemoryMapAlignment)); addU64("minTexelBufferOffsetAlignment", limits->minTexelBufferOffsetAlignment); addU64("minUniformBufferOffsetAlignment", limits->minUniformBufferOffsetAlignment); addU64("minStorageBufferOffsetAlignment", limits->minStorageBufferOffsetAlignment);
        addI32("minTexelOffset", limits->minTexelOffset); addU32("maxTexelOffset", limits->maxTexelOffset); addI32("minTexelGatherOffset", limits->minTexelGatherOffset); addU32("maxTexelGatherOffset", limits->maxTexelGatherOffset); addF32("minInterpolationOffset", limits->minInterpolationOffset); addF32("maxInterpolationOffset", limits->maxInterpolationOffset); addU32("subPixelInterpolationOffsetBits", limits->subPixelInterpolationOffsetBits); addU32("maxFramebufferWidth", limits->maxFramebufferWidth); addU32("maxFramebufferHeight", limits->maxFramebufferHeight); addU32("maxFramebufferLayers", limits->maxFramebufferLayers); addU32("framebufferColorSampleCounts", limits->framebufferColorSampleCounts); addU32("framebufferDepthSampleCounts", limits->framebufferDepthSampleCounts); addU32("framebufferStencilSampleCounts", limits->framebufferStencilSampleCounts); addU32("framebufferNoAttachmentsSampleCounts", limits->framebufferNoAttachmentsSampleCounts); addU32("maxColorAttachments", limits->maxColorAttachments); addU32("sampledImageColorSampleCounts", limits->sampledImageColorSampleCounts); addU32("sampledImageIntegerSampleCounts", limits->sampledImageIntegerSampleCounts); addU32("sampledImageDepthSampleCounts", limits->sampledImageDepthSampleCounts); addU32("sampledImageStencilSampleCounts", limits->sampledImageStencilSampleCounts); addU32("storageImageSampleCounts", limits->storageImageSampleCounts); addU32("maxSampleMaskWords", limits->maxSampleMaskWords); addBool("timestampComputeAndGraphics", limits->timestampComputeAndGraphics); addF32("timestampPeriod", limits->timestampPeriod); addU32("maxClipDistances", limits->maxClipDistances); addU32("maxCullDistances", limits->maxCullDistances); addU32("maxCombinedClipAndCullDistances", limits->maxCombinedClipAndCullDistances); addU32("discreteQueuePriorities", limits->discreteQueuePriorities); addFloatPair("pointSizeRange", limits->pointSizeRange[0], limits->pointSizeRange[1]); addFloatPair("lineWidthRange", limits->lineWidthRange[0], limits->lineWidthRange[1]); addF32("pointSizeGranularity", limits->pointSizeGranularity); addF32("lineWidthGranularity", limits->lineWidthGranularity); addBool("strictLines", limits->strictLines); addBool("standardSampleLocations", limits->standardSampleLocations); addU64("optimalBufferCopyOffsetAlignment", limits->optimalBufferCopyOffsetAlignment); addU64("optimalBufferCopyRowPitchAlignment", limits->optimalBufferCopyRowPitchAlignment); addU64("nonCoherentAtomSize", limits->nonCoherentAtomSize);
        for (size_t i = 0; i < limitFields.size(); ++i) {
            if (i) out << ',';
            out << "{\"name\":" << jsonString(limitFields[i].name) << ",\"value\":" << jsonString(limitFields[i].value) << '}';
        }
    
        out << ']';
        {
            std::string featureLimitSnapshot = out.str();
            featureLimitSnapshot += ",\"versionedFeatures\":[],\"detailedProperties\":[],\"queues\":[],\"memory\":{\"heapCount\":0,\"heaps\":[],\"typeCount\":0,\"types\":[]},\"formats\":[],\"surface\":{\"available\":false}}]}";
            publishProbeCheckpoint(checkpointPath, featureLimitSnapshot);
            __android_log_print(ANDROID_LOG_INFO, "VulkanProbe", "base feature-limit checkpoint published device=%u", deviceIndex);
        }

        out << ",\"versionedFeatures\":[]";
        out << ",\"detailedProperties\":[";
        bool detailedFirst = true;
        appendProperty(out, detailedFirst, "Core 1.0", "pipelineCacheUUID", hexBytes(properties.pipelineCacheUUID, VK_UUID_SIZE));
        appendBoolProperty(out, detailedFirst, "Core 1.0", "sparseResidencyStandard2DBlockShapeSupported", sparseProperties->residencyStandard2DBlockShape);
        appendBoolProperty(out, detailedFirst, "Core 1.0", "sparseResidencyStandard2DMultisampleBlockShape", sparseProperties->residencyStandard2DMultisampleBlockShape);
        appendBoolProperty(out, detailedFirst, "Core 1.0", "sparseResidencyStandard3DBlockShape", sparseProperties->residencyStandard3DBlockShape);
        appendBoolProperty(out, detailedFirst, "Core 1.0", "sparseResidencyAlignedMipSize", sparseProperties->residencyAlignedMipSize);
        appendBoolProperty(out, detailedFirst, "Core 1.0", "sparseResidencyNonResidentStrict", sparseProperties->residencyNonResidentStrict);
        out << ']';
        out << ",\"vulkan14Status\":" << jsonString(VK_API_VERSION_MINOR(apiVersion) >= 4 ? "deferred" : "not_applicable")
            << ",\"vulkan14Reason\":" << jsonString(VK_API_VERSION_MINOR(apiVersion) >= 4
                ? "Vulkan 1.4 core feature and property queries are collected in an isolated validated probe."
                : "The device API version is below Vulkan 1.4.");
        out << ",\"queues\":[";
        for (uint32_t i = 0; i < queueCount; ++i) {
            if (i) out << ',';
            const auto& q = queues[i];
            out << "{\"index\":" << i << ",\"count\":" << q.queueCount << ",\"timestampValidBits\":" << q.timestampValidBits
                << ",\"flags\":" << q.queueFlags << ",\"graphics\":" << jsonBool((q.queueFlags & 1u) != 0)
                << ",\"compute\":" << jsonBool((q.queueFlags & 2u) != 0) << ",\"transfer\":" << jsonBool((q.queueFlags & 4u) != 0)
                << ",\"sparse\":" << jsonBool((q.queueFlags & 8u) != 0)
                << ",\"protected\":" << jsonBool((q.queueFlags & 0x10u) != 0)
                << ",\"videoDecode\":" << jsonBool((q.queueFlags & 0x20u) != 0)
                << ",\"videoEncode\":" << jsonBool((q.queueFlags & 0x40u) != 0)
                << ",\"opticalFlow\":" << jsonBool((q.queueFlags & 0x100u) != 0)
                << ",\"dataGraph\":" << jsonBool((q.queueFlags & 0x400u) != 0)
                << ",\"unknownFlags\":" << (q.queueFlags & ~0x577u)
                << ",\"minImageTransferGranularity\":" << jsonString(std::to_string(q.minImageTransferGranularity.width) + " × " + std::to_string(q.minImageTransferGranularity.height) + " × " + std::to_string(q.minImageTransferGranularity.depth)) << '}';
        }
        out << "] ,\"memory\":{\"heapCount\":" << memoryHeapCount << ",\"heaps\":[";
        for (uint32_t i = 0; i < memoryHeapCount; ++i) {
            if (i) out << ',';
            out << "{\"index\":" << i << ",\"size\":" << memory.memoryHeaps[i].size << ",\"flags\":" << memory.memoryHeaps[i].flags << '}';
        }
        out << "],\"typeCount\":" << memoryTypeCount << ",\"types\":[";
        for (uint32_t i = 0; i < memoryTypeCount; ++i) {
            if (i) out << ',';
            out << "{\"index\":" << i << ",\"heap\":" << memory.memoryTypes[i].heapIndex << ",\"flags\":" << memory.memoryTypes[i].propertyFlags << '}';
        }
        std::string safeDeviceSnapshot = out.str();
        safeDeviceSnapshot += "]},\"formats\":[],\"surface\":{\"available\":false}}]}";
        std::string safeSnapshot = safeDeviceSnapshot;
        publishProbeCheckpoint(checkpointPath, safeSnapshot);
        __android_log_print(ANDROID_LOG_INFO, "VulkanProbe", "base safe checkpoint published device=%u features=%zu limits=%zu queues=%u heaps=%u types=%u", deviceIndex, coreFeatureValues.size(), limitFields.size(), queueCount, memoryHeapCount, memoryTypeCount);
        out << "]},\"formats\":[]";
        {
            std::string baseReadySnapshot = out.str();
            baseReadySnapshot += ",\"surface\":{\"available\":false}}]";
            publishProbeCheckpoint(checkpointPath, baseReadySnapshot);
            __android_log_print(ANDROID_LOG_INFO, "VulkanProbe", "base core checkpoint published before optional surface enrichment device=%u", deviceIndex);
        }
        if (liveSurface != VK_NULL_HANDLE) {
            api.destroySurfaceKHR(instance, liveSurface, nullptr);
            liveSurface = VK_NULL_HANDLE;
        }
        out << ",\"surface\":{";
        if (liveSurface == VK_NULL_HANDLE) {
            out << "\"available\":false,\"colorSpaceExtensionAvailable\":" << jsonBool(swapchainColorspaceAvailable) << ",\"colorSpaceExtensionEnabled\":false,\"presentationSupported\":false}";
        } else {
            g_probeStage = 24;
            bool presentationSupported = false;
            out << "\"available\":true,\"colorSpaceExtensionAvailable\":" << jsonBool(swapchainColorspaceAvailable) << ",\"colorSpaceExtensionEnabled\":" << jsonBool(swapchainColorspaceAvailable);
            out << ",\"queuePresentation\":[";
            for (uint32_t qi = 0; qi < queueCount; ++qi) {
                VkBool32 supported = VK_FALSE;
                const VkResult r = api.getPhysicalDeviceSurfaceSupportKHR ? api.getPhysicalDeviceSurfaceSupportKHR(device, qi, liveSurface, &supported) : VK_ERROR_EXTENSION_NOT_PRESENT;
                if (r == VK_SUCCESS && supported == VK_TRUE) presentationSupported = true;
                if (qi) out << ',';
                out << "{\"queueFamily\":" << qi << ",\"supported\":" << jsonBool(r == VK_SUCCESS && supported == VK_TRUE) << "}";
            }
            out << "],\"presentationSupported\":" << jsonBool(presentationSupported);
            VkSurfaceCapabilitiesKHR caps{};
            VkResult capResult = VK_ERROR_EXTENSION_NOT_PRESENT;
            if (getSurfaceCapabilities2Available) {
                VkPhysicalDeviceSurfaceInfo2KHR info{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SURFACE_INFO_2_KHR, nullptr, liveSurface};
                VkSurfaceCapabilities2KHR caps2{VK_STRUCTURE_TYPE_SURFACE_CAPABILITIES_2_KHR, nullptr, {}};
                capResult = api.getPhysicalDeviceSurfaceCapabilities2KHR(device, &info, &caps2);
                if (capResult == VK_SUCCESS) caps = caps2.surfaceCapabilities;
            } else if (api.getPhysicalDeviceSurfaceCapabilitiesKHR) {
                capResult = api.getPhysicalDeviceSurfaceCapabilitiesKHR(device, liveSurface, &caps);
            }
            out << ",\"capabilityResult\":" << capResult;
            if (capResult == VK_SUCCESS) {
                out << ",\"minImageCount\":" << caps.minImageCount << ",\"maxImageCount\":" << caps.maxImageCount
                    << ",\"currentExtent\":" << jsonString(std::to_string(caps.currentExtent.width) + " × " + std::to_string(caps.currentExtent.height))
                    << ",\"minExtent\":" << jsonString(std::to_string(caps.minImageExtent.width) + " × " + std::to_string(caps.minImageExtent.height))
                    << ",\"maxExtent\":" << jsonString(std::to_string(caps.maxImageExtent.width) + " × " + std::to_string(caps.maxImageExtent.height))
                    << ",\"maxImageArrayLayers\":" << caps.maxImageArrayLayers << ",\"supportedTransforms\":" << caps.supportedTransforms
                    << ",\"currentTransform\":" << caps.currentTransform << ",\"supportedCompositeAlpha\":" << caps.supportedCompositeAlpha
                    << ",\"supportedUsageFlags\":" << caps.supportedUsageFlags;
            }
            g_probeStage = 25;
            out << ",\"formats\":[";
            if (getSurfaceCapabilities2Available && api.getPhysicalDeviceSurfaceFormats2KHR) {
                uint32_t fc = 0;
                VkPhysicalDeviceSurfaceInfo2KHR info{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SURFACE_INFO_2_KHR, nullptr, liveSurface};
                VkResult r = api.getPhysicalDeviceSurfaceFormats2KHR(device, &info, &fc, nullptr);
                if ((r == VK_SUCCESS || r == VK_INCOMPLETE) && fc <= kMaxSurfaceFormatEntries) {
                    std::vector<VkSurfaceFormat2KHR> formats2(fc);
                    for (auto& x : formats2) { x.sType = VK_STRUCTURE_TYPE_SURFACE_FORMAT_2_KHR; x.pNext = nullptr; }
                    if (fc > 0) r = api.getPhysicalDeviceSurfaceFormats2KHR(device, &info, &fc, formats2.data());
                    for (uint32_t fi = 0; fi < fc && fi < formats2.size(); ++fi) {
                        if (fi) out << ',';
                        const auto& f = formats2[fi].surfaceFormat;
                        out << "{\"format\":" << jsonString(formatName(f.format)) << ",\"colorSpace\":" << jsonString(colorSpaceName(f.colorSpace))
                            << ",\"class\":" << jsonString(colorSpaceClass(f.colorSpace)) << ",\"description\":" << jsonString(colorSpaceDescription(f.colorSpace)) << '}';
                    }
                }
            } else if (!getSurfaceCapabilities2Available && api.getPhysicalDeviceSurfaceFormatsKHR) {
                uint32_t fc = 0;
                VkResult r = api.getPhysicalDeviceSurfaceFormatsKHR(device, liveSurface, &fc, nullptr);
                if ((r == VK_SUCCESS || r == VK_INCOMPLETE) && fc <= kMaxSurfaceFormatEntries) {
                    std::vector<VkSurfaceFormatKHR> formats(fc);
                    if (fc > 0) r = api.getPhysicalDeviceSurfaceFormatsKHR(device, liveSurface, &fc, formats.data());
                    for (uint32_t fi = 0; fi < fc && fi < formats.size(); ++fi) {
                        if (fi) out << ',';
                        const auto& f = formats[fi];
                        out << "{\"format\":" << jsonString(formatName(f.format)) << ",\"colorSpace\":" << jsonString(colorSpaceName(f.colorSpace))
                            << ",\"class\":" << jsonString(colorSpaceClass(f.colorSpace)) << ",\"description\":" << jsonString(colorSpaceDescription(f.colorSpace)) << '}';
                    }
                }
            }
            out << "]";
            g_probeStage = 26;
            out << ",\"presentModes\":[";
            uint32_t pmc = 0;
            VkResult pmr = api.getPhysicalDeviceSurfacePresentModesKHR ? api.getPhysicalDeviceSurfacePresentModesKHR(device, liveSurface, &pmc, nullptr) : VK_ERROR_EXTENSION_NOT_PRESENT;
            if ((pmr == VK_SUCCESS || pmr == VK_INCOMPLETE) && pmc <= kMaxSurfaceFormatEntries) {
                std::vector<VkPresentModeKHR> modes(pmc);
                if (pmc > 0) pmr = api.getPhysicalDeviceSurfacePresentModesKHR(device, liveSurface, &pmc, modes.data());
                for (uint32_t mi = 0; mi < pmc && mi < modes.size(); ++mi) {
                    if (mi) out << ',';
                    out << jsonString(presentModeName(modes[mi]));
                }
            }
            out << "]}";
        }
        out << "}";
    }
    out << "]}";
    std::string finalResult = out.str();
    if (!finalResult.empty() && finalResult.back() == '}') {
        finalResult.pop_back();
        finalResult += ",\"baseReportComplete\":true}";
    } else {
        finalResult += "\"baseReportComplete\":true}";
    }
    publishProbeCheckpoint(checkpointPath, finalResult);
    __android_log_print(ANDROID_LOG_INFO, "VulkanProbe", "base report complete checkpoint published before optional metadata");
    api.destroyInstance(instance, nullptr);
    return finalResult;
}

std::string collectVulkanMetadata(const char* driverMode, const char* driverIcdPath, const char* driverBundlePath, const char* hookLibDir) {
    VulkanApi api;
    if (!api.open(driverMode, driverIcdPath, driverBundlePath, hookLibDir)) {
        return std::string("{\"status\":\"unavailable\",\"group\":\"metadata\",\"reason\":") + jsonString(api.openError.empty() ? "Vulkan loader unavailable" : api.openError) + ",\"instanceExtensions\":[],\"instanceLayers\":[]}";
    }
    const auto instanceExts = instanceExtensions(api);
    const auto instanceLayerValues = instanceLayers(api);
    std::ostringstream out;
    out << "{\"status\":\"available\",\"group\":\"metadata\",\"reason\":\"\"";
    out << ",\"registryCoverage\":" << registryCoverageJson();
    out << ",\"instanceExtensionDependencyQueryEnabled\":" << jsonBool(hasExtension(instanceExts, "VK_KHR_get_physical_device_properties2"));
    out << ",\"instanceExtensions\":" << extensionsJson(instanceExts, "Instance");
    out << ",\"instanceLayers\":" << layersJson(api, instanceLayerValues);
    out << ",\"instanceLayersComplete\":true,\"registryCoverageComplete\":true}";
    return out.str();
}

std::string collectVulkanCoreGroup(const char* driverMode, const char* driverIcdPath, const char* driverBundlePath, const char* hookLibDir, uint32_t targetMinor) {
    const char* group = targetMinor == 1 ? "core11" : targetMinor == 2 ? "core12" : targetMinor == 3 ? "core13" : "core14";
    VulkanApi api;
    if (!api.open(driverMode, driverIcdPath, driverBundlePath, hookLibDir)) {
        return std::string("{\"status\":\"unavailable\",\"group\":") + jsonString(group) + ",\"reason\":" + jsonString(api.openError.empty() ? "Vulkan loader unavailable" : api.openError) + ",\"devices\":[]}";
    }
    uint32_t loaderVersion = VK_API_VERSION_1_0;
    if (api.enumerateInstanceVersion) {
        VkResult versionResult = api.enumerateInstanceVersion(&loaderVersion);
        if (versionResult != VK_SUCCESS) loaderVersion = VK_API_VERSION_1_0;
    }
    const auto queryInstanceExtensions = buildQueryInstanceExtensions(api);
    VkInstance instance = nullptr;
    uint32_t selectedInstanceApiVersion = VK_API_VERSION_1_0;
    g_probeStage = 2;
    const uint32_t targetInstanceVersion = targetMinor >= 4 ? std::min(loaderVersion, VK_API_VERSION_1_4) : std::min(loaderVersion, VK_API_VERSION_1_3);
    const VkResult createResult = api.createInstanceCompatible(targetInstanceVersion, queryInstanceExtensions, &instance, &selectedInstanceApiVersion);
    if (createResult != VK_SUCCESS || !instance) {
        return std::string("{\"status\":\"unavailable\",\"group\":") + jsonString(group) + ",\"reason\":" + jsonString(std::string("vkCreateInstance failed with VkResult ") + std::to_string(createResult)) + ",\"devices\":[]}";
    }
    if (!api.loadInstanceFunctions(instance) || !api.getPhysicalDeviceProperties2 || !api.getPhysicalDeviceFeatures2) {
        api.destroyInstance(instance, nullptr);
        return std::string("{\"status\":\"unavailable\",\"group\":") + jsonString(group) + ",\"reason\":\"Vulkan extended physical-device query entry points are unavailable.\",\"devices\":[]}";
    }
    const auto queryDevicesResult = enumeratePhysicalDevicesRobust(api, instance);
    const VkResult queryDeviceResult = queryDevicesResult.first;
    std::vector<VkPhysicalDevice> devices = queryDevicesResult.second;
    const uint32_t count = static_cast<uint32_t>(devices.size());
    if (queryDeviceResult != VK_SUCCESS || count == 0) {
        api.destroyInstance(instance, nullptr);
        return std::string("{\"status\":\"not_applicable\",\"group\":") + jsonString(group) + ",\"reason\":\"No physical Vulkan devices were enumerated.\",\"devices\":[]}";
    }
    std::ostringstream out;
    out << "{\"status\":\"available\",\"group\":" << jsonString(group) << ",\"reason\":\"\",\"devices\":[";
    bool firstDevice = true;
    bool matchedAny = false;
    for (uint32_t i = 0; i < count; ++i) {
        VkPhysicalDeviceProperties physicalProperties{};
        getDevicePropertiesPrimary(api, devices[i], physicalProperties);
        const uint32_t apiVersion = physicalProperties.apiVersion;
        if (VK_API_VERSION_MINOR(apiVersion) < targetMinor) continue;
        matchedAny = true;
        const uint32_t vendorId = physicalProperties.vendorID;
        const uint32_t deviceId = physicalProperties.deviceID;
        const char* name = physicalProperties.deviceName;
        const auto devExts = deviceExtensions(api, devices[i]);
        if (!firstDevice) out << ',';
        firstDevice = false;
        out << "{\"vendorId\":" << vendorId << ",\"deviceId\":" << deviceId << ",\"name\":" << jsonString(name ? name : "Unknown GPU") << ",\"apiVersion\":" << jsonString(versionString(apiVersion)) << ",\"features\":";
        appendVersionedFeatures(out, apiVersion, api, devices[i], targetMinor);
        out << ",\"properties\":";
        appendCoreProperties(out, apiVersion, api, devices[i], devExts, targetMinor, false);
        out << '}';
    }
    out << "]}";
    if (!matchedAny) {
        api.destroyInstance(instance, nullptr);
        return std::string("{\"status\":\"not_applicable\",\"group\":") + jsonString(group) + ",\"reason\":\"The installed Vulkan device API version is below the requested core version.\",\"devices\":[]}";
    }
    api.destroyInstance(instance, nullptr);
    return out.str();
}

std::string collectVulkanExtensionGroup(const char* driverMode, const char* driverIcdPath, const char* driverBundlePath, const char* hookLibDir, const char* group) {
    VulkanApi api;
    if (!api.open(driverMode, driverIcdPath, driverBundlePath, hookLibDir)) {
        return std::string("{\"status\":\"unavailable\",\"group\":") + jsonString(group ? group : "") + ",\"reason\":" + jsonString(api.openError.empty() ? "Vulkan loader unavailable" : api.openError) + ",\"devices\":[]}";
    }
    const auto* descriptor = vulkanscope_registry::findQueryDescriptor(group);
    if (!descriptor || std::strcmp(descriptor->scope, "device-extension") != 0 || descriptor->extension[0] == '\0') {
        return std::string("{\"status\":\"unavailable\",\"group\":") + jsonString(group ? group : "") + ",\"reason\":\"Unknown or non-device-extension Vulkan query group.\",\"devices\":[]}";
    }
    const char* extensionName = descriptor->extension;

    uint32_t loaderVersion = VK_API_VERSION_1_0;
    if (api.enumerateInstanceVersion) {
        VkResult versionResult = api.enumerateInstanceVersion(&loaderVersion);
        if (versionResult != VK_SUCCESS) loaderVersion = VK_API_VERSION_1_0;
    }
    const auto queryInstanceExtensions = buildQueryInstanceExtensions(api);
    VkInstance instance = nullptr;
    uint32_t selectedInstanceApiVersion = VK_API_VERSION_1_0;
    g_probeStage = 2;
    const VkResult createResult = api.createInstanceCompatible(std::min(loaderVersion, VK_API_VERSION_1_3), queryInstanceExtensions, &instance, &selectedInstanceApiVersion);
    if (createResult != VK_SUCCESS || !instance) {
        return std::string("{\"status\":\"unavailable\",\"group\":") + jsonString(group) + ",\"reason\":" + jsonString(std::string("vkCreateInstance failed with VkResult ") + std::to_string(createResult)) + ",\"devices\":[]}";
    }
    if (!api.loadInstanceFunctions(instance) || !api.getPhysicalDeviceProperties2 || !api.getPhysicalDeviceFeatures2) {
        api.destroyInstance(instance, nullptr);
        return std::string("{\"status\":\"unavailable\",\"group\":") + jsonString(group) + ",\"reason\":\"Vulkan extended physical-device query entry points are unavailable.\",\"devices\":[]}";
    }
    const auto extensionDevicesResult = enumeratePhysicalDevicesRobust(api, instance);
    const VkResult extensionDeviceResult = extensionDevicesResult.first;
    std::vector<VkPhysicalDevice> devices = extensionDevicesResult.second;
    const uint32_t count = static_cast<uint32_t>(devices.size());
    if (extensionDeviceResult != VK_SUCCESS || count == 0) {
        api.destroyInstance(instance, nullptr);
        return std::string("{\"status\":\"not_applicable\",\"group\":") + jsonString(group) + ",\"reason\":\"No physical Vulkan devices were enumerated.\",\"devices\":[]}";
    }
    std::ostringstream out;
    out << "{\"status\":\"available\",\"group\":" << jsonString(group) << ",\"extension\":" << jsonString(extensionName) << ",\"reason\":\"\",\"devices\":[";
    bool firstDevice = true;
    bool matchedAny = false;
    for (uint32_t i = 0; i < count; ++i) {
        const auto devExts = deviceExtensions(api, devices[i]);
        if (!hasExtension(devExts, extensionName) && group && std::strcmp(group, "videoCapabilities") != 0) continue;
        matchedAny = true;
        VkPhysicalDeviceProperties physicalProperties{};
        getDevicePropertiesPrimary(api, devices[i], physicalProperties);
        const uint32_t vendorId = physicalProperties.vendorID;
        const uint32_t deviceId = physicalProperties.deviceID;
        const char* name = physicalProperties.deviceName;
        struct GroupFeature { std::string name; bool supported; };
        struct GroupProperty { std::string section; std::string name; std::string value; };
        std::vector<GroupFeature> featureEntries;
        std::vector<GroupProperty> propertyEntries;
        auto addFeature = [&](const char* featureName, VkBool32 value) { featureEntries.push_back({std::string(extensionName) + " · " + featureName, value == VK_TRUE}); };
        auto hasExt = [&](const char* name) { return hasExtension(devExts, name); };
        auto addProperty = [&](const std::string& propertyName, const std::string& value) {
            const std::string section = extensionName[0] ? (std::string("Extension · ") + extensionName) : "Advanced Query";
            propertyEntries.push_back({section, propertyName, value});
        };
        if (std::strcmp(extensionName, "VK_EXT_descriptor_buffer") == 0 || std::strcmp(group, "descriptorBuffer") == 0) {
            VkPhysicalDeviceDescriptorBufferFeaturesEXT f{};
            f.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_BUFFER_FEATURES_EXT;
            VkPhysicalDeviceFeatures2 q{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &f, {}};
            api.queryFeatures2(devices[i], &q);
            addFeature("descriptorBuffer", f.descriptorBuffer);
            addFeature("descriptorBufferCaptureReplay", f.descriptorBufferCaptureReplay);
            addFeature("descriptorBufferImageLayoutIgnored", f.descriptorBufferImageLayoutIgnored);
            addFeature("descriptorBufferPushDescriptors", f.descriptorBufferPushDescriptors);
        } else if (std::strcmp(extensionName, "VK_KHR_acceleration_structure") == 0) {
            VkPhysicalDeviceAccelerationStructureFeaturesKHR f{};
            f.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ACCELERATION_STRUCTURE_FEATURES_KHR;
            VkPhysicalDeviceFeatures2 q{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &f, {}};
            api.queryFeatures2(devices[i], &q);
            addFeature("accelerationStructure", f.accelerationStructure);
            addFeature("accelerationStructureCaptureReplay", f.accelerationStructureCaptureReplay);
            addFeature("accelerationStructureIndirectBuild", f.accelerationStructureIndirectBuild);
            addFeature("accelerationStructureHostCommands", f.accelerationStructureHostCommands);
            addFeature("descriptorBindingAccelerationStructureUpdateAfterBind", f.descriptorBindingAccelerationStructureUpdateAfterBind);
        } else if (std::strcmp(extensionName, "VK_KHR_ray_tracing_pipeline") == 0) {
            VkPhysicalDeviceRayTracingPipelineFeaturesKHR f{};
            f.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_TRACING_PIPELINE_FEATURES_KHR;
            VkPhysicalDeviceFeatures2 q{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &f, {}};
            api.queryFeatures2(devices[i], &q);
            addFeature("rayTracingPipeline", f.rayTracingPipeline);
            addFeature("rayTracingPipelineShaderGroupHandleCaptureReplay", f.rayTracingPipelineShaderGroupHandleCaptureReplay);
            addFeature("rayTracingPipelineShaderGroupHandleCaptureReplayMixed", f.rayTracingPipelineShaderGroupHandleCaptureReplayMixed);
            addFeature("rayTracingPipelineTraceRaysIndirect", f.rayTracingPipelineTraceRaysIndirect);
        } else if (std::strcmp(extensionName, "VK_KHR_ray_query") == 0) {
            VkPhysicalDeviceRayQueryFeaturesKHR f{};
            f.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_QUERY_FEATURES_KHR;
            VkPhysicalDeviceFeatures2 q{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &f, {}};
            api.queryFeatures2(devices[i], &q);
            addFeature("rayQuery", f.rayQuery);
        } else if (std::strcmp(extensionName, "VK_EXT_mesh_shader") == 0) {
            VkPhysicalDeviceMeshShaderFeaturesEXT f{};
            f.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MESH_SHADER_FEATURES_EXT;
            VkPhysicalDeviceFeatures2 q{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &f, {}};
            api.queryFeatures2(devices[i], &q);
            addFeature("taskShader", f.taskShader);
            addFeature("meshShader", f.meshShader);
            addFeature("multiviewMeshShader", f.multiviewMeshShader);
            addFeature("primitiveFragmentShadingRateMeshShader", f.primitiveFragmentShadingRateMeshShader);
            addFeature("meshShaderQueries", f.meshShaderQueries);
        } else if (std::strcmp(extensionName, "VK_EXT_graphics_pipeline_library") == 0) {
            VkPhysicalDeviceGraphicsPipelineLibraryFeaturesEXT f{};
            f.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_GRAPHICS_PIPELINE_LIBRARY_FEATURES_EXT;
            VkPhysicalDeviceFeatures2 q{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &f, {}};
            api.queryFeatures2(devices[i], &q);
            addFeature("graphicsPipelineLibrary", f.graphicsPipelineLibrary);
        } else if (std::strcmp(extensionName, "VK_EXT_shader_object") == 0) {
            VkPhysicalDeviceShaderObjectFeaturesEXT f{};
            f.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_OBJECT_FEATURES_EXT;
            VkPhysicalDeviceFeatures2 q{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &f, {}};
            api.queryFeatures2(devices[i], &q);
            addFeature("shaderObject", f.shaderObject);
        } else if (std::strcmp(extensionName, "VK_EXT_host_image_copy") == 0) {
            VkPhysicalDeviceHostImageCopyFeatures f{};
            f.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_HOST_IMAGE_COPY_FEATURES;
            VkPhysicalDeviceFeatures2 q{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &f, {}};
            api.queryFeatures2(devices[i], &q);
            addFeature("hostImageCopy", f.hostImageCopy);
        } else if (std::strcmp(extensionName, "VK_EXT_extended_dynamic_state") == 0) {
            VkPhysicalDeviceExtendedDynamicStateFeaturesEXT f{};
            f.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_DYNAMIC_STATE_FEATURES_EXT;
            VkPhysicalDeviceFeatures2 q{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &f, {}};
            api.queryFeatures2(devices[i], &q);
            addFeature("extendedDynamicState", f.extendedDynamicState);
        } else if (std::strcmp(extensionName, "VK_EXT_extended_dynamic_state3") == 0) {
            VkPhysicalDeviceExtendedDynamicState3FeaturesEXT f{};
            f.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_DYNAMIC_STATE_3_FEATURES_EXT;
            VkPhysicalDeviceFeatures2 q{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &f, {}};
            api.queryFeatures2(devices[i], &q);
            addFeature("extendedDynamicState3TessellationDomainOrigin", f.extendedDynamicState3TessellationDomainOrigin);
            addFeature("extendedDynamicState3DepthClampEnable", f.extendedDynamicState3DepthClampEnable);
            addFeature("extendedDynamicState3PolygonMode", f.extendedDynamicState3PolygonMode);
            addFeature("extendedDynamicState3RasterizationSamples", f.extendedDynamicState3RasterizationSamples);
            addFeature("extendedDynamicState3SampleMask", f.extendedDynamicState3SampleMask);
            addFeature("extendedDynamicState3AlphaToCoverageEnable", f.extendedDynamicState3AlphaToCoverageEnable);
            addFeature("extendedDynamicState3AlphaToOneEnable", f.extendedDynamicState3AlphaToOneEnable);
            addFeature("extendedDynamicState3LogicOpEnable", f.extendedDynamicState3LogicOpEnable);
            addFeature("extendedDynamicState3ColorBlendEnable", f.extendedDynamicState3ColorBlendEnable);
            addFeature("extendedDynamicState3ColorBlendEquation", f.extendedDynamicState3ColorBlendEquation);
            addFeature("extendedDynamicState3ColorWriteMask", f.extendedDynamicState3ColorWriteMask);
            addFeature("extendedDynamicState3RasterizationStream", f.extendedDynamicState3RasterizationStream);
            addFeature("extendedDynamicState3ConservativeRasterizationMode", f.extendedDynamicState3ConservativeRasterizationMode);
            addFeature("extendedDynamicState3ExtraPrimitiveOverestimationSize", f.extendedDynamicState3ExtraPrimitiveOverestimationSize);
            addFeature("extendedDynamicState3DepthClipEnable", f.extendedDynamicState3DepthClipEnable);
            addFeature("extendedDynamicState3SampleLocationsEnable", f.extendedDynamicState3SampleLocationsEnable);
            addFeature("extendedDynamicState3ColorBlendAdvanced", f.extendedDynamicState3ColorBlendAdvanced);
            addFeature("extendedDynamicState3ProvokingVertexMode", f.extendedDynamicState3ProvokingVertexMode);
            addFeature("extendedDynamicState3LineRasterizationMode", f.extendedDynamicState3LineRasterizationMode);
            addFeature("extendedDynamicState3LineStippleEnable", f.extendedDynamicState3LineStippleEnable);
            addFeature("extendedDynamicState3DepthClipNegativeOneToOne", f.extendedDynamicState3DepthClipNegativeOneToOne);
            addFeature("extendedDynamicState3ViewportWScalingEnable", f.extendedDynamicState3ViewportWScalingEnable);
            addFeature("extendedDynamicState3ViewportSwizzle", f.extendedDynamicState3ViewportSwizzle);
            addFeature("extendedDynamicState3CoverageToColorEnable", f.extendedDynamicState3CoverageToColorEnable);
            addFeature("extendedDynamicState3CoverageToColorLocation", f.extendedDynamicState3CoverageToColorLocation);
            addFeature("extendedDynamicState3CoverageModulationMode", f.extendedDynamicState3CoverageModulationMode);
            addFeature("extendedDynamicState3CoverageModulationTableEnable", f.extendedDynamicState3CoverageModulationTableEnable);
            addFeature("extendedDynamicState3CoverageModulationTable", f.extendedDynamicState3CoverageModulationTable);
            addFeature("extendedDynamicState3CoverageReductionMode", f.extendedDynamicState3CoverageReductionMode);
            addFeature("extendedDynamicState3RepresentativeFragmentTestEnable", f.extendedDynamicState3RepresentativeFragmentTestEnable);
            addFeature("extendedDynamicState3ShadingRateImageEnable", f.extendedDynamicState3ShadingRateImageEnable);
        } else if (std::strcmp(extensionName, "VK_KHR_fragment_shader_barycentric") == 0) {
            VkPhysicalDeviceFragmentShaderBarycentricFeaturesKHR f{};
            f.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_SHADER_BARYCENTRIC_FEATURES_KHR;
            VkPhysicalDeviceFeatures2 q{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &f, {}};
            api.queryFeatures2(devices[i], &q);
            addFeature("fragmentShaderBarycentric", f.fragmentShaderBarycentric);
        } else if (std::strcmp(extensionName, "VK_KHR_fragment_shading_rate") == 0) {
            VkPhysicalDeviceFragmentShadingRateFeaturesKHR f{};
            f.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_SHADING_RATE_FEATURES_KHR;
            VkPhysicalDeviceFeatures2 q{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &f, {}};
            api.queryFeatures2(devices[i], &q);
            addFeature("pipelineFragmentShadingRate", f.pipelineFragmentShadingRate);
            addFeature("primitiveFragmentShadingRate", f.primitiveFragmentShadingRate);
            addFeature("attachmentFragmentShadingRate", f.attachmentFragmentShadingRate);
        } else if (std::strcmp(extensionName, "VK_EXT_transform_feedback") == 0) {
            VkPhysicalDeviceTransformFeedbackFeaturesEXT f{};
            f.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TRANSFORM_FEEDBACK_FEATURES_EXT;
            VkPhysicalDeviceFeatures2 q{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &f, {}};
            api.queryFeatures2(devices[i], &q);
            addFeature("transformFeedback", f.transformFeedback);
            addFeature("geometryStreams", f.geometryStreams);
        } else if (std::strcmp(extensionName, "VK_EXT_vertex_attribute_divisor") == 0) {
            VkPhysicalDeviceVertexAttributeDivisorFeaturesEXT f{};
            f.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VERTEX_ATTRIBUTE_DIVISOR_FEATURES_EXT;
            VkPhysicalDeviceFeatures2 q{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &f, {}};
            api.queryFeatures2(devices[i], &q);
            addFeature("vertexAttributeInstanceRateDivisor", f.vertexAttributeInstanceRateDivisor);
            addFeature("vertexAttributeInstanceRateZeroDivisor", f.vertexAttributeInstanceRateZeroDivisor);
        } else if (std::strcmp(extensionName, "VK_KHR_inline_uniform_block") == 0) {
            VkPhysicalDeviceInlineUniformBlockFeatures f{};
            f.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INLINE_UNIFORM_BLOCK_FEATURES;
            VkPhysicalDeviceFeatures2 q{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &f, {}};
            api.queryFeatures2(devices[i], &q);
            addFeature("inlineUniformBlock", f.inlineUniformBlock);
            addFeature("descriptorBindingInlineUniformBlockUpdateAfterBind", f.descriptorBindingInlineUniformBlockUpdateAfterBind);
        } else if (std::strcmp(extensionName, "VK_EXT_private_data") == 0) {
            VkPhysicalDevicePrivateDataFeatures f{};
            f.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRIVATE_DATA_FEATURES;
            VkPhysicalDeviceFeatures2 q{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &f, {}};
            api.queryFeatures2(devices[i], &q);
            addFeature("privateData", f.privateData);
        } else if (std::strcmp(extensionName, "VK_KHR_synchronization2") == 0) {
            VkPhysicalDeviceSynchronization2Features f{};
            f.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SYNCHRONIZATION_2_FEATURES;
            VkPhysicalDeviceFeatures2 q{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &f, {}};
            api.queryFeatures2(devices[i], &q);
            addFeature("synchronization2", f.synchronization2);
        }
        api.captureGeneratedFields = true;
        api.generatedFields.clear();
        api.generatedFields.reserve(1024);
        RuntimePNextStorage runtimePNext;
        const size_t runtimePNextAdded = appendAllGeneratedExtensionPNext(extensionName, devExts, runtimePNext) + appendParityExtensionPNext(extensionName, devExts, runtimePNext);
        VkPhysicalDeviceFeatures2 generatedFeatures{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, runtimePNext.featureHead, {}};
        VkPhysicalDeviceProperties2 generatedProperties{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2, runtimePNext.propertyHead, {}};
        if (runtimePNext.featureHead && api.getPhysicalDeviceFeatures2) api.queryFeatures2(devices[i], &generatedFeatures);
        if (runtimePNext.propertyHead && api.getPhysicalDeviceProperties2) api.queryProperties2(devices[i], &generatedProperties);
        if (runtimePNextAdded > 0) addProperty("generatedRuntimePNextTypes", std::to_string(runtimePNextAdded));
        const char* groupName = group;
        if (std::strcmp(groupName, "videoCapabilities") == 0) {
            const bool videoQueue = hasExt("VK_KHR_video_queue");
            if (!videoQueue) {
                addProperty("queryStatus", "Unavailable: VK_KHR_video_queue is not enumerated by this physical device.");
            } else if (!api.getPhysicalDeviceVideoCapabilitiesKHR) {
                addProperty("queryStatus", "Unavailable: vkGetPhysicalDeviceVideoCapabilitiesKHR is unavailable in this Vulkan stack.");
            } else {
                auto makeBaseProfile = [](VkVideoCodecOperationFlagsKHR operation) {
                    VkVideoProfileInfoKHR p{};
                    p.sType = VK_STRUCTURE_TYPE_VIDEO_PROFILE_INFO_KHR;
                    p.pNext = nullptr;
                    p.videoCodecOperation = static_cast<VkVideoCodecOperationFlagBitsKHR>(operation);
                    p.chromaSubsampling = VK_VIDEO_CHROMA_SUBSAMPLING_420_BIT_KHR;
                    p.lumaBitDepth = VK_VIDEO_COMPONENT_BIT_DEPTH_8_BIT_KHR;
                    p.chromaBitDepth = VK_VIDEO_COMPONENT_BIT_DEPTH_8_BIT_KHR;
                    return p;
                };
                auto emitDecode = [&](const char* name, const char* extension, VkVideoCodecOperationFlagsKHR operation, uint32_t profileSType, uint32_t profileValue, uint32_t capSType) {
                    if (!hasExt(extension) || !hasExt("VK_KHR_video_decode_queue")) {
                        addProperty(std::string("Video · ") + name, "Unavailable: required video decode extension is not enumerated.");
                        return;
                    }
                    VkVideoProfileInfoKHR profile = makeBaseProfile(operation);
                    if (std::strcmp(name, "H.264 decode") == 0) {
                        VkVideoDecodeH264ProfileInfoKHR codec{};
                        codec.sType = static_cast<VkStructureType>(profileSType); codec.pNext = nullptr; codec.stdProfileIdc = static_cast<StdVideoH264ProfileIdc>(profileValue); codec.pictureLayout = VK_VIDEO_DECODE_H264_PICTURE_LAYOUT_PROGRESSIVE_KHR;
                        profile.pNext = &codec;
                        VkVideoDecodeH264CapabilitiesKHR codecCaps{};
                        codecCaps.sType = static_cast<VkStructureType>(capSType); codecCaps.pNext = nullptr;
                        VkVideoDecodeCapabilitiesKHR decodeCaps{};
                        decodeCaps.sType = VK_STRUCTURE_TYPE_VIDEO_DECODE_CAPABILITIES_KHR;
                        decodeCaps.pNext = &codecCaps;
                        VkVideoCapabilitiesKHR caps{};
                        caps.sType = VK_STRUCTURE_TYPE_VIDEO_CAPABILITIES_KHR;
                        caps.pNext = &decodeCaps;
                        const VkResult r = api.getPhysicalDeviceVideoCapabilitiesKHR(devices[i], &profile, &caps);
                        addProperty(std::string("Video · ") + name + " status", r == VK_SUCCESS ? "Supported" : ("Unavailable (VkResult=" + std::to_string(r) + ")"));
                        if (r == VK_SUCCESS) {
                            addProperty(std::string("Video · ") + name + " maxLevelIdc", std::to_string(codecCaps.maxLevelIdc));
                            addProperty(std::string("Video · ") + name + " fieldOffsetGranularity", std::to_string(codecCaps.fieldOffsetGranularity.x) + " × " + std::to_string(codecCaps.fieldOffsetGranularity.y));
                            addProperty(std::string("Video · ") + name + " codedExtent", std::to_string(caps.minCodedExtent.width) + " × " + std::to_string(caps.minCodedExtent.height) + " .. " + std::to_string(caps.maxCodedExtent.width) + " × " + std::to_string(caps.maxCodedExtent.height));
                            addProperty(std::string("Video · ") + name + " DPB", std::to_string(caps.maxDpbSlots) + " slots / " + std::to_string(caps.maxActiveReferencePictures) + " active refs");
                            addProperty(std::string("Video · ") + name + " bitstreamAlignment", std::to_string(caps.minBitstreamBufferOffsetAlignment) + " offset / " + std::to_string(caps.minBitstreamBufferSizeAlignment) + " size");
                            addProperty(std::string("Video · ") + name + " stdHeader", std::string(caps.stdHeaderVersion.extensionName) + " " + std::to_string(caps.stdHeaderVersion.specVersion));
                        }
                    } else if (std::strcmp(name, "H.265 decode") == 0) {
                        VkVideoDecodeH265ProfileInfoKHR codec{};
                        codec.sType = static_cast<VkStructureType>(profileSType); codec.pNext = nullptr; codec.stdProfileIdc = static_cast<StdVideoH265ProfileIdc>(profileValue);
                        profile.pNext = &codec;
                        VkVideoDecodeH265CapabilitiesKHR codecCaps{};
                        codecCaps.sType = static_cast<VkStructureType>(capSType); codecCaps.pNext = nullptr;
                        VkVideoDecodeCapabilitiesKHR decodeCaps{};
                        decodeCaps.sType = VK_STRUCTURE_TYPE_VIDEO_DECODE_CAPABILITIES_KHR;
                        decodeCaps.pNext = &codecCaps;
                        VkVideoCapabilitiesKHR caps{};
                        caps.sType = VK_STRUCTURE_TYPE_VIDEO_CAPABILITIES_KHR;
                        caps.pNext = &decodeCaps;
                        const VkResult r = api.getPhysicalDeviceVideoCapabilitiesKHR(devices[i], &profile, &caps);
                        addProperty(std::string("Video · ") + name + " status", r == VK_SUCCESS ? "Supported" : ("Unavailable (VkResult=" + std::to_string(r) + ")"));
                        if (r == VK_SUCCESS) {
                            addProperty(std::string("Video · ") + name + " maxLevelIdc", std::to_string(codecCaps.maxLevelIdc));
                            addProperty(std::string("Video · ") + name + " codedExtent", std::to_string(caps.minCodedExtent.width) + " × " + std::to_string(caps.minCodedExtent.height) + " .. " + std::to_string(caps.maxCodedExtent.width) + " × " + std::to_string(caps.maxCodedExtent.height));
                            addProperty(std::string("Video · ") + name + " DPB", std::to_string(caps.maxDpbSlots) + " slots / " + std::to_string(caps.maxActiveReferencePictures) + " active refs");
                            addProperty(std::string("Video · ") + name + " bitstreamAlignment", std::to_string(caps.minBitstreamBufferOffsetAlignment) + " offset / " + std::to_string(caps.minBitstreamBufferSizeAlignment) + " size");
                            addProperty(std::string("Video · ") + name + " stdHeader", std::string(caps.stdHeaderVersion.extensionName) + " " + std::to_string(caps.stdHeaderVersion.specVersion));
                        }
                    } else if (std::strcmp(name, "VP9 decode") == 0) {
                        VkVideoDecodeVP9ProfileInfoKHR codec{};
                        codec.sType = static_cast<VkStructureType>(profileSType); codec.pNext = nullptr; codec.stdProfile = static_cast<StdVideoVP9Profile>(profileValue);
                        profile.pNext = &codec;
                        VkVideoDecodeVP9CapabilitiesKHR codecCaps{};
                        codecCaps.sType = static_cast<VkStructureType>(capSType); codecCaps.pNext = nullptr;
                        VkVideoDecodeCapabilitiesKHR decodeCaps{};
                        decodeCaps.sType = VK_STRUCTURE_TYPE_VIDEO_DECODE_CAPABILITIES_KHR;
                        decodeCaps.pNext = &codecCaps;
                        VkVideoCapabilitiesKHR caps{};
                        caps.sType = VK_STRUCTURE_TYPE_VIDEO_CAPABILITIES_KHR;
                        caps.pNext = &decodeCaps;
                        const VkResult r = api.getPhysicalDeviceVideoCapabilitiesKHR(devices[i], &profile, &caps);
                        addProperty(std::string("Video · ") + name + " status", r == VK_SUCCESS ? "Supported" : ("Unavailable (VkResult=" + std::to_string(r) + ")"));
                        if (r == VK_SUCCESS) {
                            addProperty(std::string("Video · ") + name + " maxLevel", std::to_string(codecCaps.maxLevel));
                            addProperty(std::string("Video · ") + name + " codedExtent", std::to_string(caps.minCodedExtent.width) + " × " + std::to_string(caps.minCodedExtent.height) + " .. " + std::to_string(caps.maxCodedExtent.width) + " × " + std::to_string(caps.maxCodedExtent.height));
                            addProperty(std::string("Video · ") + name + " DPB", std::to_string(caps.maxDpbSlots) + " slots / " + std::to_string(caps.maxActiveReferencePictures) + " active refs");
                        }
                    } else {
                        VkVideoDecodeAV1ProfileInfoKHR codec{};
                        codec.sType = static_cast<VkStructureType>(profileSType); codec.pNext = nullptr; codec.stdProfile = static_cast<StdVideoAV1Profile>(profileValue); codec.filmGrainSupport = VK_FALSE;
                        profile.pNext = &codec;
                        VkVideoDecodeAV1CapabilitiesKHR codecCaps{};
                        codecCaps.sType = static_cast<VkStructureType>(capSType); codecCaps.pNext = nullptr;
                        VkVideoDecodeCapabilitiesKHR decodeCaps{};
                        decodeCaps.sType = VK_STRUCTURE_TYPE_VIDEO_DECODE_CAPABILITIES_KHR;
                        decodeCaps.pNext = &codecCaps;
                        VkVideoCapabilitiesKHR caps{};
                        caps.sType = VK_STRUCTURE_TYPE_VIDEO_CAPABILITIES_KHR;
                        caps.pNext = &decodeCaps;
                        const VkResult r = api.getPhysicalDeviceVideoCapabilitiesKHR(devices[i], &profile, &caps);
                        addProperty(std::string("Video · ") + name + " status", r == VK_SUCCESS ? "Supported" : ("Unavailable (VkResult=" + std::to_string(r) + ")"));
                        if (r == VK_SUCCESS) {
                            addProperty(std::string("Video · ") + name + " maxLevel", std::to_string(codecCaps.maxLevel));
                            addProperty(std::string("Video · ") + name + " codedExtent", std::to_string(caps.minCodedExtent.width) + " × " + std::to_string(caps.minCodedExtent.height) + " .. " + std::to_string(caps.maxCodedExtent.width) + " × " + std::to_string(caps.maxCodedExtent.height));
                            addProperty(std::string("Video · ") + name + " DPB", std::to_string(caps.maxDpbSlots) + " slots / " + std::to_string(caps.maxActiveReferencePictures) + " active refs");
                        }
                    }
                };

                auto emitEncode = [&](const char* name, const char* extension, VkVideoCodecOperationFlagsKHR operation, uint32_t profileSType, uint32_t profileValue) {
                    if (!hasExt(extension) || !hasExt("VK_KHR_video_encode_queue")) {
                        addProperty(std::string("Video · ") + name, "Unavailable: required video encode extension is not enumerated.");
                        return;
                    }
                    VkVideoProfileInfoKHR profile = makeBaseProfile(operation);
                    alignas(VkVideoEncodeAV1ProfileInfoKHR) std::array<uint8_t, 64> profileStorage{};
                    if (std::strcmp(name, "H.264 encode") == 0) {
                        auto* codec = reinterpret_cast<VkVideoEncodeH264ProfileInfoKHR*>(profileStorage.data());
                        codec->sType = static_cast<VkStructureType>(profileSType); codec->pNext = nullptr; codec->stdProfileIdc = static_cast<StdVideoH264ProfileIdc>(profileValue);
                        profile.pNext = codec;
                    } else if (std::strcmp(name, "H.265 encode") == 0) {
                        auto* codec = reinterpret_cast<VkVideoEncodeH265ProfileInfoKHR*>(profileStorage.data());
                        codec->sType = static_cast<VkStructureType>(profileSType); codec->pNext = nullptr; codec->stdProfileIdc = static_cast<StdVideoH265ProfileIdc>(profileValue);
                        profile.pNext = codec;
                    } else {
                        auto* codec = reinterpret_cast<VkVideoEncodeAV1ProfileInfoKHR*>(profileStorage.data());
                        codec->sType = static_cast<VkStructureType>(profileSType); codec->pNext = nullptr; codec->stdProfile = static_cast<StdVideoAV1Profile>(profileValue);
                        profile.pNext = codec;
                    }
                    VkVideoEncodeCapabilitiesKHR encodeCaps{};
                    encodeCaps.sType = VK_STRUCTURE_TYPE_VIDEO_ENCODE_CAPABILITIES_KHR;
                    encodeCaps.pNext = nullptr;
                    VkVideoCapabilitiesKHR caps{};
                    caps.sType = VK_STRUCTURE_TYPE_VIDEO_CAPABILITIES_KHR;
                    caps.pNext = &encodeCaps;
                    const VkResult r = api.getPhysicalDeviceVideoCapabilitiesKHR(devices[i], &profile, &caps);
                    addProperty(std::string("Video · ") + name + " status", r == VK_SUCCESS ? "Supported" : ("Unavailable (VkResult=" + std::to_string(r) + ")"));
                    if (r == VK_SUCCESS) {
                        addProperty(std::string("Video · ") + name + " maxBitrate", std::to_string(encodeCaps.maxBitrate));
                        addProperty(std::string("Video · ") + name + " maxQualityLevels", std::to_string(encodeCaps.maxQualityLevels));
                        addProperty(std::string("Video · ") + name + " maxRateControlLayers", std::to_string(encodeCaps.maxRateControlLayers));
                        addProperty(std::string("Video · ") + name + " rateControlModes", std::to_string(encodeCaps.rateControlModes));
                        addProperty(std::string("Video · ") + name + " supportedEncodeFeedbackFlags", std::to_string(encodeCaps.supportedEncodeFeedbackFlags));
                        addProperty(std::string("Video · ") + name + " codedExtent", std::to_string(caps.minCodedExtent.width) + " × " + std::to_string(caps.minCodedExtent.height) + " .. " + std::to_string(caps.maxCodedExtent.width) + " × " + std::to_string(caps.maxCodedExtent.height));
                        addProperty(std::string("Video · ") + name + " stdHeader", std::string(caps.stdHeaderVersion.extensionName) + " " + std::to_string(caps.stdHeaderVersion.specVersion));
                    }
                };

                emitDecode("H.264 decode", "VK_KHR_video_decode_h264", VK_VIDEO_CODEC_OPERATION_DECODE_H264_BIT_KHR, VK_STRUCTURE_TYPE_VIDEO_DECODE_H264_PROFILE_INFO_KHR, STD_VIDEO_H264_PROFILE_IDC_BASELINE, VK_STRUCTURE_TYPE_VIDEO_DECODE_H264_CAPABILITIES_KHR);
                emitDecode("H.265 decode", "VK_KHR_video_decode_h265", VK_VIDEO_CODEC_OPERATION_DECODE_H265_BIT_KHR, VK_STRUCTURE_TYPE_VIDEO_DECODE_H265_PROFILE_INFO_KHR, STD_VIDEO_H265_PROFILE_IDC_MAIN, VK_STRUCTURE_TYPE_VIDEO_DECODE_H265_CAPABILITIES_KHR);
                emitDecode("VP9 decode", "VK_KHR_video_decode_vp9", VK_VIDEO_CODEC_OPERATION_DECODE_VP9_BIT_KHR, VK_STRUCTURE_TYPE_VIDEO_DECODE_VP9_PROFILE_INFO_KHR, STD_VIDEO_VP9_PROFILE_0, VK_STRUCTURE_TYPE_VIDEO_DECODE_VP9_CAPABILITIES_KHR);
                emitDecode("AV1 decode", "VK_KHR_video_decode_av1", VK_VIDEO_CODEC_OPERATION_DECODE_AV1_BIT_KHR, VK_STRUCTURE_TYPE_VIDEO_DECODE_AV1_PROFILE_INFO_KHR, STD_VIDEO_AV1_PROFILE_MAIN, VK_STRUCTURE_TYPE_VIDEO_DECODE_AV1_CAPABILITIES_KHR);
                emitEncode("H.264 encode", "VK_KHR_video_encode_h264", VK_VIDEO_CODEC_OPERATION_ENCODE_H264_BIT_KHR, VK_STRUCTURE_TYPE_VIDEO_ENCODE_H264_PROFILE_INFO_KHR, STD_VIDEO_H264_PROFILE_IDC_MAIN);
                emitEncode("H.265 encode", "VK_KHR_video_encode_h265", VK_VIDEO_CODEC_OPERATION_ENCODE_H265_BIT_KHR, VK_STRUCTURE_TYPE_VIDEO_ENCODE_H265_PROFILE_INFO_KHR, STD_VIDEO_H265_PROFILE_IDC_MAIN);
                emitEncode("AV1 encode", "VK_KHR_video_encode_av1", VK_VIDEO_CODEC_OPERATION_ENCODE_AV1_BIT_KHR, VK_STRUCTURE_TYPE_VIDEO_ENCODE_AV1_PROFILE_INFO_KHR, STD_VIDEO_AV1_PROFILE_MAIN);

                if (api.getPhysicalDeviceVideoFormatPropertiesKHR) {
                    auto queryVideoFormats = [&](const char* name, VkVideoCodecOperationFlagsKHR op, const char* extension, uint32_t codecSType, uint32_t profileValue) {
                        if (!hasExt(extension) || !hasExt("VK_KHR_video_decode_queue")) return;
                        VkVideoProfileInfoKHR profile = makeBaseProfile(op);
                        alignas(VkVideoDecodeAV1ProfileInfoKHR) std::array<uint8_t, 64> codecStorage{};
                        if (std::strcmp(name, "H.264") == 0) {
                            auto* codec = reinterpret_cast<VkVideoDecodeH264ProfileInfoKHR*>(codecStorage.data()); codec->sType = static_cast<VkStructureType>(codecSType); codec->pNext = nullptr; codec->stdProfileIdc = static_cast<StdVideoH264ProfileIdc>(profileValue); codec->pictureLayout = VK_VIDEO_DECODE_H264_PICTURE_LAYOUT_PROGRESSIVE_KHR; profile.pNext = codec;
                        } else if (std::strcmp(name, "H.265") == 0) {
                            auto* codec = reinterpret_cast<VkVideoDecodeH265ProfileInfoKHR*>(codecStorage.data()); codec->sType = static_cast<VkStructureType>(codecSType); codec->pNext = nullptr; codec->stdProfileIdc = static_cast<StdVideoH265ProfileIdc>(profileValue); profile.pNext = codec;
                        } else if (std::strcmp(name, "VP9") == 0) {
                            auto* codec = reinterpret_cast<VkVideoDecodeVP9ProfileInfoKHR*>(codecStorage.data()); codec->sType = static_cast<VkStructureType>(codecSType); codec->pNext = nullptr; codec->stdProfile = static_cast<StdVideoVP9Profile>(profileValue); profile.pNext = codec;
                        } else {
                            auto* codec = reinterpret_cast<VkVideoDecodeAV1ProfileInfoKHR*>(codecStorage.data()); codec->sType = static_cast<VkStructureType>(codecSType); codec->pNext = nullptr; codec->stdProfile = static_cast<StdVideoAV1Profile>(profileValue); codec->filmGrainSupport = VK_FALSE; profile.pNext = codec;
                        }
                        VkVideoProfileListInfoKHR list{VK_STRUCTURE_TYPE_VIDEO_PROFILE_LIST_INFO_KHR, nullptr, 1, &profile};
                        VkPhysicalDeviceVideoFormatInfoKHR info{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_FORMAT_INFO_KHR, &list, VK_IMAGE_USAGE_SAMPLED_BIT};
                        uint32_t formatCount = 0;
                        VkResult r = api.getPhysicalDeviceVideoFormatPropertiesKHR(devices[i], &info, &formatCount, nullptr);
                        if (r != VK_SUCCESS || formatCount == 0) {
                            addProperty(std::string("Video formats · ") + name + " decode (sampled)", "Unavailable (VkResult=" + std::to_string(r) + ")");
                            return;
                        }
                        if (formatCount > kMaxVideoFormatEntries) {
                            addProperty(std::string("Video formats · ") + name + " decode (sampled)", "Unavailable: result count exceeds safety limit.");
                            return;
                        }
                        std::vector<VkVideoFormatPropertiesKHR> formats(formatCount);
                        for (auto& f : formats) { f.sType = VK_STRUCTURE_TYPE_VIDEO_FORMAT_PROPERTIES_KHR; f.pNext = nullptr; }
                        r = api.getPhysicalDeviceVideoFormatPropertiesKHR(devices[i], &info, &formatCount, formats.data());
                        if (r != VK_SUCCESS && r != VK_INCOMPLETE) {
                            addProperty(std::string("Video formats · ") + name + " decode (sampled)", "Unavailable (VkResult=" + std::to_string(r) + ")");
                            return;
                        }
                        std::ostringstream values;
                        for (uint32_t fi = 0; fi < formatCount; ++fi) {
                            if (fi) values << "; ";
                            values << formatName(formats[fi].format) << " (usage=0x" << std::hex << formats[fi].imageUsageFlags << std::dec << ")";
                        }
                        addProperty(std::string("Video formats · ") + name + " decode (sampled)", values.str());
                    };
                    queryVideoFormats("H.264", VK_VIDEO_CODEC_OPERATION_DECODE_H264_BIT_KHR, "VK_KHR_video_decode_h264", VK_STRUCTURE_TYPE_VIDEO_DECODE_H264_PROFILE_INFO_KHR, STD_VIDEO_H264_PROFILE_IDC_BASELINE);
                    queryVideoFormats("H.265", VK_VIDEO_CODEC_OPERATION_DECODE_H265_BIT_KHR, "VK_KHR_video_decode_h265", VK_STRUCTURE_TYPE_VIDEO_DECODE_H265_PROFILE_INFO_KHR, STD_VIDEO_H265_PROFILE_IDC_MAIN);
                    queryVideoFormats("VP9", VK_VIDEO_CODEC_OPERATION_DECODE_VP9_BIT_KHR, "VK_KHR_video_decode_vp9", VK_STRUCTURE_TYPE_VIDEO_DECODE_VP9_PROFILE_INFO_KHR, STD_VIDEO_VP9_PROFILE_0);
                    queryVideoFormats("AV1", VK_VIDEO_CODEC_OPERATION_DECODE_AV1_BIT_KHR, "VK_KHR_video_decode_av1", VK_STRUCTURE_TYPE_VIDEO_DECODE_AV1_PROFILE_INFO_KHR, STD_VIDEO_AV1_PROFILE_MAIN);
                }
            }
        } else if (std::strcmp(groupName, "videoEncodeFeedback2") == 0) {
            VkPhysicalDeviceVideoEncodeFeedback2FeaturesKHR f{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_ENCODE_FEEDBACK_2_FEATURES_KHR,nullptr,VK_FALSE};
            VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}};
            api.queryFeatures2(devices[i], &f2);
            addFeature("videoEncodeFeedback2", f.videoEncodeFeedback2);
        } else if (std::strcmp(groupName, "cooperativeMatrixConversion") == 0) {
            VkPhysicalDeviceCooperativeMatrixConversionFeaturesQCOM f{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_CONVERSION_FEATURES_QCOM,nullptr,VK_FALSE};
            VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}};
            api.queryFeatures2(devices[i], &f2);
            addFeature("cooperativeMatrixConversion", f.cooperativeMatrixConversion);
        } else if (std::strcmp(groupName, "elapsedTimerQuery") == 0) {
            VkPhysicalDeviceElapsedTimerQueryFeaturesQCOM f{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_ELAPSED_TIMER_QUERY_FEATURES_QCOM,nullptr,VK_FALSE};
            VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}};
            api.queryFeatures2(devices[i], &f2);
            addFeature("elapsedTimerQuery", f.elapsedTimerQuery);
        } else if (std::strcmp(groupName, "queuePerfHint") == 0) {
            VkPhysicalDeviceQueuePerfHintFeaturesQCOM f{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_QUEUE_PERF_HINT_FEATURES_QCOM,nullptr,VK_FALSE};
            VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}};
            api.queryFeatures2(devices[i], &f2);
            addFeature("queuePerfHint", f.queuePerfHint);
            VkPhysicalDeviceQueuePerfHintPropertiesQCOM p{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_QUEUE_PERF_HINT_PROPERTIES_QCOM,nullptr,0};
            VkPhysicalDeviceProperties2 p2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2,&p,{}};
            api.queryProperties2(devices[i], &p2);
            addProperty("supportedQueues", std::to_string(p.supportedQueues));
        } else if (std::strcmp(groupName, "dataGraphNeuralAcceleratorStatistics") == 0) {
            VkPhysicalDeviceDataGraphNeuralAcceleratorStatisticsFeaturesARM f{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DATA_GRAPH_NEURAL_ACCELERATOR_STATISTICS_FEATURES_ARM,nullptr,VK_FALSE};
            VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}}; api.queryFeatures2(devices[i], &f2); addFeature("dataGraphNeuralAcceleratorStatistics", f.dataGraphNeuralAcceleratorStatistics);
        } else if (std::strcmp(groupName, "shaderInstrumentation") == 0) {
            VkPhysicalDeviceShaderInstrumentationFeaturesARM f{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_INSTRUMENTATION_FEATURES_ARM,nullptr,VK_FALSE}; VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}}; api.queryFeatures2(devices[i], &f2); addFeature("shaderInstrumentation", f.shaderInstrumentation);
            VkPhysicalDeviceShaderInstrumentationPropertiesARM p{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_INSTRUMENTATION_PROPERTIES_ARM,nullptr,0,VK_FALSE}; VkPhysicalDeviceProperties2 p2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2,&p,{}}; api.queryProperties2(devices[i], &p2); addProperty("numMetrics", std::to_string(p.numMetrics)); addProperty("perBasicBlockGranularity", jsonBool(p.perBasicBlockGranularity));
        } else if (std::strcmp(groupName, "multisampledRenderToSwapchain") == 0) {
            VkPhysicalDeviceMultisampledRenderToSwapchainFeaturesEXT f{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MULTISAMPLED_RENDER_TO_SWAPCHAIN_FEATURES_EXT,nullptr,VK_FALSE}; VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}}; api.queryFeatures2(devices[i], &f2); addFeature("multisampledRenderToSwapchain", f.multisampledRenderToSwapchain);
        } else if (std::strcmp(groupName, "primitiveRestartIndex") == 0) {
            VkPhysicalDevicePrimitiveRestartIndexFeaturesEXT f{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRIMITIVE_RESTART_INDEX_FEATURES_EXT,nullptr,VK_FALSE}; VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}}; api.queryFeatures2(devices[i], &f2); addFeature("primitiveRestartIndex", f.primitiveRestartIndex);
        } else if (std::strcmp(groupName, "shaderSplitBarrier") == 0) {
            VkPhysicalDeviceShaderSplitBarrierFeaturesEXT f{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SPLIT_BARRIER_FEATURES_EXT,nullptr,VK_FALSE}; VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}}; api.queryFeatures2(devices[i], &f2); addFeature("shaderSplitBarrier", f.shaderSplitBarrier);
            VkPhysicalDeviceShaderSplitBarrierPropertiesEXT p{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SPLIT_BARRIER_PROPERTIES_EXT,nullptr,0}; VkPhysicalDeviceProperties2 p2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2,&p,{}}; api.queryProperties2(devices[i], &p2); addProperty("splitBarrierReservedSharedMemory", std::to_string(p.splitBarrierReservedSharedMemory));
        } else if (std::strcmp(groupName, "deviceFault") == 0) {
            VkPhysicalDeviceFaultFeaturesKHR f{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FAULT_FEATURES_KHR,nullptr,VK_FALSE,VK_FALSE,VK_FALSE,VK_FALSE}; VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}}; api.queryFeatures2(devices[i], &f2); addFeature("deviceFault", f.deviceFault); addFeature("deviceFaultVendorBinary", f.deviceFaultVendorBinary); addFeature("deviceFaultReportMasked", f.deviceFaultReportMasked); addFeature("deviceFaultDeviceLostOnMasked", f.deviceFaultDeviceLostOnMasked);
            VkPhysicalDeviceFaultPropertiesKHR p{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FAULT_PROPERTIES_KHR,nullptr,0}; VkPhysicalDeviceProperties2 p2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2,&p,{}}; api.queryProperties2(devices[i], &p2); addProperty("maxDeviceFaultCount", std::to_string(p.maxDeviceFaultCount));
        } else if (std::strcmp(groupName, "opacityMicromap") == 0) {
            VkPhysicalDeviceOpacityMicromapFeaturesKHR f{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_OPACITY_MICROMAP_FEATURES_KHR,nullptr,VK_FALSE}; VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}}; api.queryFeatures2(devices[i], &f2); addFeature("micromap", f.micromap);
            VkPhysicalDeviceOpacityMicromapPropertiesKHR p{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_OPACITY_MICROMAP_PROPERTIES_KHR,nullptr,0,0,0,0}; VkPhysicalDeviceProperties2 p2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2,&p,{}}; api.queryProperties2(devices[i], &p2); addProperty("maxOpacity2StateSubdivisionLevel", std::to_string(p.maxOpacity2StateSubdivisionLevel)); addProperty("maxOpacity4StateSubdivisionLevel", std::to_string(p.maxOpacity4StateSubdivisionLevel)); addProperty("maxOpacityLossy4StateSubdivisionLevel", std::to_string(p.maxOpacityLossy4StateSubdivisionLevel)); addProperty("maxMicromapTriangles", std::to_string(p.maxMicromapTriangles));
        } else if (std::strcmp(groupName, "shaderAbort") == 0) {
            VkPhysicalDeviceShaderAbortFeaturesKHR f{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_ABORT_FEATURES_KHR,nullptr,VK_FALSE}; VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}}; api.queryFeatures2(devices[i], &f2); addFeature("shaderAbort", f.shaderAbort);
            VkPhysicalDeviceShaderAbortPropertiesKHR p{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_ABORT_PROPERTIES_KHR,nullptr,0}; VkPhysicalDeviceProperties2 p2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2,&p,{}}; api.queryProperties2(devices[i], &p2); addProperty("maxShaderAbortMessageSize", std::to_string(p.maxShaderAbortMessageSize));
        } else if (std::strcmp(groupName, "shaderConstantData") == 0) {
            VkPhysicalDeviceShaderConstantDataFeaturesKHR f{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_CONSTANT_DATA_FEATURES_KHR,nullptr,VK_FALSE}; VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}}; api.queryFeatures2(devices[i], &f2); addFeature("shaderConstantData", f.shaderConstantData);
        } else if (std::strcmp(groupName, "cooperativeMatrixDecodeVector") == 0) {
            VkPhysicalDeviceCooperativeMatrixDecodeVectorFeaturesNV f{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_DECODE_VECTOR_FEATURES_NV,nullptr,VK_FALSE}; VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}}; api.queryFeatures2(devices[i], &f2); addFeature("cooperativeMatrixDecodeVector", f.cooperativeMatrixDecodeVector);
        } else if (std::strcmp(groupName, "imageProcessing3") == 0) {
            VkPhysicalDeviceImageProcessing3FeaturesQCOM f{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_PROCESSING_3_FEATURES_QCOM,nullptr,VK_FALSE,VK_FALSE,VK_FALSE}; VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}}; api.queryFeatures2(devices[i], &f2); addFeature("imageGatherLinear", f.imageGatherLinear); addFeature("imageGatherExtendedModes", f.imageGatherExtendedModes); addFeature("blockMatchExtendedClampToEdge", f.blockMatchExtendedClampToEdge);
        } else if (std::strcmp(groupName, "shaderMultipleWaitQueues") == 0) {
            VkPhysicalDeviceShaderMultipleWaitQueuesFeaturesQCOM f{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_MULTIPLE_WAIT_QUEUES_FEATURES_QCOM,nullptr,VK_FALSE}; VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}}; api.queryFeatures2(devices[i], &f2); addFeature("shaderMultipleWaitQueues", f.shaderMultipleWaitQueues);
            VkPhysicalDeviceShaderMultipleWaitQueuesPropertiesQCOM p{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_MULTIPLE_WAIT_QUEUES_PROPERTIES_QCOM,nullptr,0}; VkPhysicalDeviceProperties2 p2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2,&p,{}}; api.queryProperties2(devices[i], &p2); addProperty("maxShaderWaitQueues", std::to_string(p.maxShaderWaitQueues));
        } else if (std::strcmp(groupName, "shaderMixedFloatDotProduct") == 0) {
            VkPhysicalDeviceShaderMixedFloatDotProductFeaturesVALVE f{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_MIXED_FLOAT_DOT_PRODUCT_FEATURES_VALVE,nullptr,VK_FALSE,VK_FALSE,VK_FALSE,VK_FALSE}; VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}}; api.queryFeatures2(devices[i], &f2); addFeature("shaderMixedFloatDotProductFloat16AccFloat32", f.shaderMixedFloatDotProductFloat16AccFloat32); addFeature("shaderMixedFloatDotProductFloat16AccFloat16", f.shaderMixedFloatDotProductFloat16AccFloat16); addFeature("shaderMixedFloatDotProductBFloat16Acc", f.shaderMixedFloatDotProductBFloat16Acc); addFeature("shaderMixedFloatDotProductFloat8AccFloat32", f.shaderMixedFloatDotProductFloat8AccFloat32);
        } else if (std::strcmp(groupName, "throttleHint") == 0) {
            VkPhysicalDeviceThrottleHintFeaturesSEC f{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_THROTTLE_HINT_FEATURES_SEC,nullptr,VK_FALSE}; VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}}; api.queryFeatures2(devices[i], &f2); addFeature("throttleHint", f.throttleHint);
        } else if (std::strcmp(groupName, "descriptorHeap") == 0) {
            VkPhysicalDeviceDescriptorHeapFeaturesEXT features{};
            features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_HEAP_FEATURES_EXT;
            VkPhysicalDeviceFeatures2 features2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &features, {}};
            api.queryFeatures2(devices[i], &features2);
            addFeature("descriptorHeap", features.descriptorHeap);
            addFeature("descriptorHeapCaptureReplay", features.descriptorHeapCaptureReplay);
            VkPhysicalDeviceDescriptorHeapPropertiesEXT properties{};
            properties.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DESCRIPTOR_HEAP_PROPERTIES_EXT;
            VkPhysicalDeviceProperties2 properties2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2, &properties, {}};
            api.queryProperties2(devices[i], &properties2);
            addProperty("samplerHeapAlignment", std::to_string(properties.samplerHeapAlignment));
            addProperty("resourceHeapAlignment", std::to_string(properties.resourceHeapAlignment));
            addProperty("maxSamplerHeapSize", std::to_string(properties.maxSamplerHeapSize));
            addProperty("maxResourceHeapSize", std::to_string(properties.maxResourceHeapSize));
            addProperty("minSamplerHeapReservedRange", std::to_string(properties.minSamplerHeapReservedRange));
            addProperty("minSamplerHeapReservedRangeWithEmbedded", std::to_string(properties.minSamplerHeapReservedRangeWithEmbedded));
            addProperty("minResourceHeapReservedRange", std::to_string(properties.minResourceHeapReservedRange));
            addProperty("samplerDescriptorSize", std::to_string(properties.samplerDescriptorSize));
            addProperty("imageDescriptorSize", std::to_string(properties.imageDescriptorSize));
            addProperty("bufferDescriptorSize", std::to_string(properties.bufferDescriptorSize));
            addProperty("samplerDescriptorAlignment", std::to_string(properties.samplerDescriptorAlignment));
            addProperty("imageDescriptorAlignment", std::to_string(properties.imageDescriptorAlignment));
            addProperty("bufferDescriptorAlignment", std::to_string(properties.bufferDescriptorAlignment));
            addProperty("maxPushDataSize", std::to_string(properties.maxPushDataSize));
            addProperty("imageCaptureReplayOpaqueDataSize", std::to_string(properties.imageCaptureReplayOpaqueDataSize));
            addProperty("maxDescriptorHeapEmbeddedSamplers", std::to_string(properties.maxDescriptorHeapEmbeddedSamplers));
            addProperty("samplerYcbcrConversionCount", std::to_string(properties.samplerYcbcrConversionCount));
            addProperty("sparseDescriptorHeaps", properties.sparseDescriptorHeaps == VK_TRUE ? "true" : "false");
            addProperty("protectedDescriptorHeaps", properties.protectedDescriptorHeaps == VK_TRUE ? "true" : "false");
        } else if (std::strcmp(groupName, "astc3D") == 0) {
            VkPhysicalDeviceTextureCompressionASTC3DFeaturesEXT features{};
            features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TEXTURE_COMPRESSION_ASTC_3D_FEATURES_EXT;
            VkPhysicalDeviceFeatures2 features2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &features, {}};
            api.queryFeatures2(devices[i], &features2);
            addFeature("textureCompressionASTC_3D", features.textureCompressionASTC_3D);
        } else if (std::strcmp(groupName, "shaderLongVector") == 0) {
            VkPhysicalDeviceShaderLongVectorFeaturesEXT features{};
            features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_LONG_VECTOR_FEATURES_EXT;
            VkPhysicalDeviceFeatures2 features2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &features, {}};
            api.queryFeatures2(devices[i], &features2);
            addFeature("longVector", features.longVector);
            VkPhysicalDeviceShaderLongVectorPropertiesEXT properties{};
            properties.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_LONG_VECTOR_PROPERTIES_EXT;
            VkPhysicalDeviceProperties2 properties2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2, &properties, {}};
            api.queryProperties2(devices[i], &properties2);
            addProperty("maxVectorComponents", std::to_string(properties.maxVectorComponents));
        } else if (std::strcmp(groupName, "shaderSubgroupPartitioned") == 0) {
            VkPhysicalDeviceShaderSubgroupPartitionedFeaturesEXT features{};
            features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_SUBGROUP_PARTITIONED_FEATURES_EXT;
            VkPhysicalDeviceFeatures2 features2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &features, {}};
            api.queryFeatures2(devices[i], &features2);
            addFeature("shaderSubgroupPartitioned", features.shaderSubgroupPartitioned);
        } else if (std::strcmp(groupName, "internallySynchronizedQueues") == 0) {
            VkPhysicalDeviceInternallySynchronizedQueuesFeaturesKHR features{};
            features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_INTERNALLY_SYNCHRONIZED_QUEUES_FEATURES_KHR;
            VkPhysicalDeviceFeatures2 features2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &features, {}};
            api.queryFeatures2(devices[i], &features2);
            addFeature("internallySynchronizedQueues", features.internallySynchronizedQueues);
        } else if (std::strcmp(groupName, "pushConstantBank") == 0) {
            VkPhysicalDevicePushConstantBankFeaturesNV features{};
            features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PUSH_CONSTANT_BANK_FEATURES_NV;
            VkPhysicalDeviceFeatures2 features2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &features, {}};
            api.queryFeatures2(devices[i], &features2);
            addFeature("pushConstantBank", features.pushConstantBank);
            VkPhysicalDevicePushConstantBankPropertiesNV properties{};
            properties.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PUSH_CONSTANT_BANK_PROPERTIES_NV;
            VkPhysicalDeviceProperties2 properties2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2, &properties, {}};
            api.queryProperties2(devices[i], &properties2);
            addProperty("maxGraphicsPushConstantBanks", std::to_string(properties.maxGraphicsPushConstantBanks));
            addProperty("maxComputePushConstantBanks", std::to_string(properties.maxComputePushConstantBanks));
            addProperty("maxGraphicsPushDataBanks", std::to_string(properties.maxGraphicsPushDataBanks));
            addProperty("maxComputePushDataBanks", std::to_string(properties.maxComputePushDataBanks));
        } else if (std::strcmp(groupName, "computeOccupancyPriority") == 0) {
            VkPhysicalDeviceComputeOccupancyPriorityFeaturesNV features{};
            features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COMPUTE_OCCUPANCY_PRIORITY_FEATURES_NV;
            VkPhysicalDeviceFeatures2 features2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &features, {}};
            api.queryFeatures2(devices[i], &features2);
            addFeature("computeOccupancyPriority", features.computeOccupancyPriority);
        } else if (std::strcmp(groupName, "maintenance7") == 0) {
            VkPhysicalDeviceMaintenance7FeaturesKHR features{};
            features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_7_FEATURES_KHR;
            VkPhysicalDeviceFeatures2 features2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &features, {}};
            api.queryFeatures2(devices[i], &features2);
            addFeature("maintenance7", features.maintenance7);
            VkPhysicalDeviceMaintenance7PropertiesKHR properties{};
            properties.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_7_PROPERTIES_KHR;
            VkPhysicalDeviceProperties2 properties2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2, &properties, {}};
            api.queryProperties2(devices[i], &properties2);
            addProperty("robustFragmentShadingRateAttachmentAccess", properties.robustFragmentShadingRateAttachmentAccess == VK_TRUE ? "true" : "false");
            addProperty("separateDepthStencilAttachmentAccess", properties.separateDepthStencilAttachmentAccess == VK_TRUE ? "true" : "false");
            addProperty("maxDescriptorSetTotalUniformBuffersDynamic", std::to_string(properties.maxDescriptorSetTotalUniformBuffersDynamic));
            addProperty("maxDescriptorSetTotalStorageBuffersDynamic", std::to_string(properties.maxDescriptorSetTotalStorageBuffersDynamic));
            addProperty("maxDescriptorSetTotalBuffersDynamic", std::to_string(properties.maxDescriptorSetTotalBuffersDynamic));
            addProperty("maxDescriptorSetUpdateAfterBindTotalUniformBuffersDynamic", std::to_string(properties.maxDescriptorSetUpdateAfterBindTotalUniformBuffersDynamic));
            addProperty("maxDescriptorSetUpdateAfterBindTotalStorageBuffersDynamic", std::to_string(properties.maxDescriptorSetUpdateAfterBindTotalStorageBuffersDynamic));
            addProperty("maxDescriptorSetUpdateAfterBindTotalBuffersDynamic", std::to_string(properties.maxDescriptorSetUpdateAfterBindTotalBuffersDynamic));
        } else if (std::strcmp(groupName, "maintenance8") == 0) {
            VkPhysicalDeviceMaintenance8FeaturesKHR features{};
            features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_8_FEATURES_KHR;
            VkPhysicalDeviceFeatures2 features2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &features, {}};
            api.queryFeatures2(devices[i], &features2);
            addFeature("maintenance8", features.maintenance8);
        } else if (std::strcmp(groupName, "maintenance9") == 0) {
            VkPhysicalDeviceMaintenance9FeaturesKHR features{};
            features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_9_FEATURES_KHR;
            VkPhysicalDeviceFeatures2 features2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &features, {}};
            api.queryFeatures2(devices[i], &features2);
            addFeature("maintenance9", features.maintenance9);
            VkPhysicalDeviceMaintenance9PropertiesKHR properties{};
            properties.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_9_PROPERTIES_KHR;
            VkPhysicalDeviceProperties2 properties2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2, &properties, {}};
            api.queryProperties2(devices[i], &properties2);
            addProperty("image2DViewOf3DSparse", properties.image2DViewOf3DSparse == VK_TRUE ? "true" : "false");
            addProperty("defaultVertexAttributeValue", properties.defaultVertexAttributeValue == 0 ? "VK_DEFAULT_VERTEX_ATTRIBUTE_VALUE_ZERO_ZERO_ZERO_ZERO_KHR" : properties.defaultVertexAttributeValue == 1 ? "VK_DEFAULT_VERTEX_ATTRIBUTE_VALUE_ZERO_ZERO_ZERO_ONE_KHR" : std::to_string(properties.defaultVertexAttributeValue));
        } else if (std::strcmp(groupName, "maintenance10") == 0) {
            VkPhysicalDeviceMaintenance10FeaturesKHR features{};
            features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_10_FEATURES_KHR;
            VkPhysicalDeviceFeatures2 features2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &features, {}};
            api.queryFeatures2(devices[i], &features2);
            addFeature("maintenance10", features.maintenance10);
            VkPhysicalDeviceMaintenance10PropertiesKHR properties{};
            properties.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_10_PROPERTIES_KHR;
            VkPhysicalDeviceProperties2 properties2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2, &properties, {}};
            api.queryProperties2(devices[i], &properties2);
            addProperty("rgba4OpaqueBlackSwizzled", properties.rgba4OpaqueBlackSwizzled == VK_TRUE ? "true" : "false");
            addProperty("resolveSrgbFormatAppliesTransferFunction", properties.resolveSrgbFormatAppliesTransferFunction == VK_TRUE ? "true" : "false");
            addProperty("resolveSrgbFormatSupportsTransferFunctionControl", properties.resolveSrgbFormatSupportsTransferFunctionControl == VK_TRUE ? "true" : "false");
        } else if (std::strcmp(groupName, "fragmentDensityMap") == 0) {
            VkPhysicalDeviceFragmentDensityMapPropertiesEXT properties{};
            properties.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_PROPERTIES_EXT;
            VkPhysicalDeviceProperties2 properties2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2, &properties, {}};
            api.queryProperties2(devices[i], &properties2);
            addProperty("minFragmentDensityTexelSize", std::to_string(properties.minFragmentDensityTexelSize.width) + " × " + std::to_string(properties.minFragmentDensityTexelSize.height));
            addProperty("maxFragmentDensityTexelSize", std::to_string(properties.maxFragmentDensityTexelSize.width) + " × " + std::to_string(properties.maxFragmentDensityTexelSize.height));
            addProperty("fragmentDensityInvocations", properties.fragmentDensityInvocations == VK_TRUE ? "true" : "false");
        } else if (std::strcmp(groupName, "fragmentDensityMap2") == 0) {
            VkPhysicalDeviceFragmentDensityMap2PropertiesEXT properties{};
            properties.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FRAGMENT_DENSITY_MAP_2_PROPERTIES_EXT;
            VkPhysicalDeviceProperties2 properties2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2, &properties, {}};
            api.queryProperties2(devices[i], &properties2);
            addProperty("subsampledLoads", properties.subsampledLoads == VK_TRUE ? "true" : "false");
            addProperty("subsampledCoarseReconstructionEarlyAccess", properties.subsampledCoarseReconstructionEarlyAccess == VK_TRUE ? "true" : "false");
            addProperty("maxSubsampledArrayLayers", std::to_string(properties.maxSubsampledArrayLayers));
        } else if (std::strcmp(groupName, "fifoLatestReady") == 0) {
            VkPhysicalDevicePresentModeFifoLatestReadyFeaturesKHR features{};
            features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_MODE_FIFO_LATEST_READY_FEATURES_KHR;
            VkPhysicalDeviceFeatures2 features2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &features, {}};
            api.queryFeatures2(devices[i], &features2);
            addFeature("presentModeFifoLatestReady", features.presentModeFifoLatestReady);
        } else if (std::strcmp(groupName, "presentId2") == 0) {
            VkPhysicalDevicePresentId2FeaturesKHR features{};
            features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_ID_2_FEATURES_KHR;
            VkPhysicalDeviceFeatures2 features2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &features, {}};
            api.queryFeatures2(devices[i], &features2);
            addFeature("presentId2", features.presentId2);
        } else if (std::strcmp(groupName, "presentWait2") == 0) {
            VkPhysicalDevicePresentWait2FeaturesKHR features{};
            features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRESENT_WAIT_2_FEATURES_KHR;
            VkPhysicalDeviceFeatures2 features2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &features, {}};
            api.queryFeatures2(devices[i], &features2);
            addFeature("presentWait2", features.presentWait2);
        } else if (std::strcmp(groupName, "pipelineBinary") == 0) {
            VkPhysicalDevicePipelineBinaryFeaturesKHR features{};
            features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_BINARY_FEATURES_KHR;
            VkPhysicalDeviceFeatures2 features2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &features, {}};
            api.queryFeatures2(devices[i], &features2);
            addFeature("pipelineBinaries", features.pipelineBinaries);
            VkPhysicalDevicePipelineBinaryPropertiesKHR properties{};
            properties.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_BINARY_PROPERTIES_KHR;
            VkPhysicalDeviceProperties2 properties2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2, &properties, {}};
            api.queryProperties2(devices[i], &properties2);
            addProperty("pipelineBinaryInternalCache", properties.pipelineBinaryInternalCache == VK_TRUE ? "true" : "false");
            addProperty("pipelineBinaryInternalCacheControl", properties.pipelineBinaryInternalCacheControl == VK_TRUE ? "true" : "false");
            addProperty("pipelineBinaryPrefersInternalCache", properties.pipelineBinaryPrefersInternalCache == VK_TRUE ? "true" : "false");
            addProperty("pipelineBinaryPrecompiledInternalCache", properties.pipelineBinaryPrecompiledInternalCache == VK_TRUE ? "true" : "false");
            addProperty("pipelineBinaryCompressedData", properties.pipelineBinaryCompressedData == VK_TRUE ? "true" : "false");
        } else if (std::strcmp(groupName, "dataGraphOpticalFlow") == 0) {
            VkPhysicalDeviceDataGraphOpticalFlowFeaturesARM features{};
            features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DATA_GRAPH_OPTICAL_FLOW_FEATURES_ARM;
            VkPhysicalDeviceFeatures2 features2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &features, {}};
            api.queryFeatures2(devices[i], &features2);
            addFeature("dataGraphOpticalFlow", features.dataGraphOpticalFlow);
        } else if (std::strcmp(groupName, "deviceAddressCommands") == 0) {
            VkPhysicalDeviceDeviceAddressCommandsFeaturesKHR features{};
            features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEVICE_ADDRESS_COMMANDS_FEATURES_KHR;
            VkPhysicalDeviceFeatures2 features2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &features, {}};
            api.queryFeatures2(devices[i], &features2);
            addFeature("deviceAddressCommands", features.deviceAddressCommands);
        } else if (std::strcmp(groupName, "extendedFlags") == 0) {
            VkPhysicalDeviceExtendedFlagsFeaturesKHR features{};
            features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_FLAGS_FEATURES_KHR;
            VkPhysicalDeviceFeatures2 features2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &features, {}};
            api.queryFeatures2(devices[i], &features2);
            addFeature("extendedFlags", features.extendedFlags);
        } else if (std::strcmp(groupName, "maintenance11") == 0) {
            VkPhysicalDeviceMaintenance11FeaturesKHR features{};
            features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_11_FEATURES_KHR;
            VkPhysicalDeviceFeatures2 features2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &features, {}};
            api.queryFeatures2(devices[i], &features2);
            addFeature("maintenance11", features.maintenance11);
        } else if (std::strcmp(groupName, "pipelineCacheIncrementalMode") == 0) {
            VkPhysicalDevicePipelineCacheIncrementalModeFeaturesSEC features{};
            features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_CACHE_INCREMENTAL_MODE_FEATURES_SEC;
            VkPhysicalDeviceFeatures2 features2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &features, {}};
            api.queryFeatures2(devices[i], &features2);
            addFeature("pipelineCacheIncrementalMode", features.pipelineCacheIncrementalMode);
        } else if (std::strcmp(groupName, "shaderOcpMicroscalingTypes") == 0) {
            VkPhysicalDeviceShaderOCPMicroscalingTypesFeaturesEXT features{};
            features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_OCP_MICROSCALING_TYPES_FEATURES_EXT;
            VkPhysicalDeviceFeatures2 features2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &features, {}};
            api.queryFeatures2(devices[i], &features2);
            addFeature("shaderFloat4", features.shaderFloat4);
            addFeature("shaderFloat6", features.shaderFloat6);
            addFeature("shaderFloat8UnsignedE8M0", features.shaderFloat8UnsignedE8M0);
            addFeature("shaderMXInt8", features.shaderMXInt8);
        } else if (std::strcmp(groupName, "shaderUniformBufferUnsizedArray") == 0) {
            VkPhysicalDeviceShaderUniformBufferUnsizedArrayFeaturesEXT features{};
            features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_UNIFORM_BUFFER_UNSIZED_ARRAY_FEATURES_EXT;
            VkPhysicalDeviceFeatures2 features2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &features, {}};
            api.queryFeatures2(devices[i], &features2);
            addFeature("shaderUniformBufferUnsizedArray", features.shaderUniformBufferUnsizedArray);
        } else if (std::strcmp(groupName, "cooperativeMatrix") == 0) {
            VkPhysicalDeviceCooperativeMatrixFeaturesKHR features{};
            features.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_FEATURES_KHR;
            VkPhysicalDeviceFeatures2 features2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &features, {}};
            api.queryFeatures2(devices[i], &features2);
            addFeature("cooperativeMatrix", features.cooperativeMatrix);
            addFeature("cooperativeMatrixRobustBufferAccess", features.cooperativeMatrixRobustBufferAccess);
            VkPhysicalDeviceCooperativeMatrixPropertiesKHR properties{};
            properties.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_PROPERTIES_KHR;
            VkPhysicalDeviceProperties2 properties2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2, &properties, {}};
            api.queryProperties2(devices[i], &properties2);
            addProperty("cooperativeMatrixSupportedStages", std::to_string(properties.cooperativeMatrixSupportedStages));
        }
        for (const auto& generated : api.generatedFields) {
            if (generated.feature) {
                bool duplicate = false;
                for (const auto& existing : featureEntries) {
                    if (existing.name == generated.name) { duplicate = true; break; }
                }
                if (!duplicate) featureEntries.push_back({generated.name, generated.value == "true"});
            } else {
                bool duplicate = false;
                for (const auto& existing : propertyEntries) {
                    if (existing.name == generated.name) { duplicate = true; break; }
                }
                if (!duplicate) propertyEntries.push_back({generated.section, generated.name, generated.value});
            }
        }
        api.generatedFields.clear();
        if (!firstDevice) out << ',';
        firstDevice = false;
        out << "{\"vendorId\":" << vendorId << ",\"deviceId\":" << deviceId << ",\"name\":" << jsonString(name ? name : "Unknown GPU") << ",\"features\":[";
        for (size_t f = 0; f < featureEntries.size(); ++f) {
            if (f) out << ',';
            out << "{\"name\":" << jsonString(featureEntries[f].name) << ",\"supported\":" << jsonBool(featureEntries[f].supported) << '}';
        }
        out << "],\"properties\":[";
        for (size_t p = 0; p < propertyEntries.size(); ++p) {
            if (p) out << ',';
            out << "{\"section\":" << jsonString(propertyEntries[p].section) << ",\"name\":" << jsonString(propertyEntries[p].name) << ",\"value\":" << jsonString(propertyEntries[p].value) << '}';
        }
        out << "]}";
    }
    out << "]}";
    api.destroyInstance(instance, nullptr);
    if (!matchedAny) return std::string("{\"status\":\"not_applicable\",\"group\":") + jsonString(group) + ",\"extension\":" + jsonString(extensionName) + ",\"reason\":\"The selected extension was not enumerated by any physical device.\",\"devices\":[]}";
    return out.str();
}

std::string collectVulkan14(const char* driverMode, const char* driverIcdPath, const char* driverBundlePath, const char* hookLibDir) {
    VulkanApi api;
    if (!api.open(driverMode, driverIcdPath, driverBundlePath, hookLibDir)) {
        return std::string("{\"status\":\"unavailable\",\"reason\":") + jsonString(api.openError.empty() ? "Vulkan loader unavailable" : api.openError) + ",\"devices\":[]}";
    }
    uint32_t loaderVersion = VK_API_VERSION_1_0;
    if (api.enumerateInstanceVersion) {
        VkResult versionResult = api.enumerateInstanceVersion(&loaderVersion);
        if (versionResult != VK_SUCCESS) loaderVersion = VK_API_VERSION_1_0;
    }
    const auto queryInstanceExtensions = buildQueryInstanceExtensions(api);
    VkInstance instance = nullptr;
    uint32_t selectedInstanceApiVersion = VK_API_VERSION_1_0;
    g_probeStage = 2;
    const VkResult createResult = api.createInstanceCompatible(loaderVersion, queryInstanceExtensions, &instance, &selectedInstanceApiVersion);
    if (createResult != VK_SUCCESS || !instance) {
        return std::string("{\"status\":\"unavailable\",\"reason\":") + jsonString(std::string("Unable to create Vulkan instance for the isolated Vulkan 1.4 probe. VkResult=") + std::to_string(createResult)) + ",\"devices\":[]}";
    }
    if (!api.loadInstanceFunctions(instance) || !api.getPhysicalDeviceProperties2 || !api.getPhysicalDeviceFeatures2) {
        api.destroyInstance(instance, nullptr);
        return "{\"status\":\"unavailable\",\"reason\":\"Vulkan 1.4 probe entry points are unavailable.\",\"devices\":[]}";
    }
    const auto v14DevicesResult = enumeratePhysicalDevicesRobust(api, instance);
    const VkResult v14DeviceResult = v14DevicesResult.first;
    std::vector<VkPhysicalDevice> devices = v14DevicesResult.second;
    const uint32_t count = static_cast<uint32_t>(devices.size());
    if (v14DeviceResult != VK_SUCCESS || count == 0) {
        api.destroyInstance(instance, nullptr);
        return "{\"status\":\"not_applicable\",\"reason\":\"No physical Vulkan devices were enumerated.\",\"devices\":[]}";
    }
    std::ostringstream out;
    out << "{\"status\":\"available\",\"reason\":\"\",\"devices\":[";
    bool firstDevice = true;
    bool saw14 = false;
    for (uint32_t i = 0; i < count; ++i) {
        VkPhysicalDeviceProperties physicalProperties{};
        getDevicePropertiesPrimary(api, devices[i], physicalProperties);
        const uint32_t apiVersion = physicalProperties.apiVersion;
        if (VK_API_VERSION_MINOR(apiVersion) < 4) continue;
        saw14 = true;
        const uint32_t vendorId = physicalProperties.vendorID;
        const uint32_t deviceId = physicalProperties.deviceID;
        const char* name = physicalProperties.deviceName;
        constexpr uint32_t kMaxVulkan14LayoutEntries = 65536;
        VkPhysicalDeviceVulkan14Properties p14{};
        p14.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_4_PROPERTIES;
        VkPhysicalDeviceProperties2 properties2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2, &p14, {}};
        api.queryProperties2(devices[i], &properties2);
        const bool copySrcWithinLimit = p14.copySrcLayoutCount <= kMaxVulkan14LayoutEntries;
        const bool copyDstWithinLimit = p14.copyDstLayoutCount <= kMaxVulkan14LayoutEntries;
        std::vector<VkImageLayout> copySrcLayouts(copySrcWithinLimit ? p14.copySrcLayoutCount : 0);
        std::vector<VkImageLayout> copyDstLayouts(copyDstWithinLimit ? p14.copyDstLayoutCount : 0);
        p14.pCopySrcLayouts = copySrcWithinLimit && !copySrcLayouts.empty() ? copySrcLayouts.data() : nullptr;
        p14.pCopyDstLayouts = copyDstWithinLimit && !copyDstLayouts.empty() ? copyDstLayouts.data() : nullptr;
        if (copySrcWithinLimit && copyDstWithinLimit) api.queryProperties2(devices[i], &properties2);

        VkPhysicalDeviceVulkan14Features v14{};
        v14.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_4_FEATURES;
        VkPhysicalDeviceFeatures2 features2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &v14, {}};
        api.queryFeatures2(devices[i], &features2);
        const std::array<VkBool32, 21> v14Values = {
            v14.globalPriorityQuery, v14.shaderSubgroupRotate, v14.shaderSubgroupRotateClustered,
            v14.shaderFloatControls2, v14.shaderExpectAssume, v14.rectangularLines, v14.bresenhamLines,
            v14.smoothLines, v14.stippledRectangularLines, v14.stippledBresenhamLines, v14.stippledSmoothLines,
            v14.vertexAttributeInstanceRateDivisor, v14.vertexAttributeInstanceRateZeroDivisor, v14.indexTypeUint8,
            v14.dynamicRenderingLocalRead, v14.maintenance5, v14.maintenance6, v14.pipelineProtectedAccess,
            v14.pipelineRobustness, v14.hostImageCopy, v14.pushDescriptor
        };

        if (!firstDevice) out << ',';
        firstDevice = false;
        out << "{\"vendorId\":" << vendorId << ",\"deviceId\":" << deviceId << ",\"name\":" << jsonString(name ? name : "Unknown GPU") << ",\"apiVersion\":" << jsonString(versionString(apiVersion)) << ",\"features\":[";
        const auto names = versionedFeatureNames(14);
        for (size_t f = 0; f < names.size(); ++f) {
            if (f) out << ',';
            out << "{\"name\":" << jsonString("Vulkan 1.4 · " + names[f]) << ",\"supported\":" << jsonBool(v14Values[f] == VK_TRUE) << '}';
        }
        out << "],\"properties\":[";
        bool first = true;
        auto addProp = [&](const char* propName, const std::string& value) { if (!first) out << ','; first = false; out << "{\"section\":\"Core 1.4\",\"name\":" << jsonString(propName) << ",\"value\":" << jsonString(value) << '}'; };
        addProp("lineSubPixelPrecisionBits", std::to_string(p14.lineSubPixelPrecisionBits));
        addProp("maxVertexAttribDivisor", std::to_string(p14.maxVertexAttribDivisor));
        addProp("supportsNonZeroFirstInstance", p14.supportsNonZeroFirstInstance == VK_TRUE ? "true" : "false");
        addProp("maxPushDescriptors", std::to_string(p14.maxPushDescriptors));
        addProp("dynamicRenderingLocalReadDepthStencilAttachments", p14.dynamicRenderingLocalReadDepthStencilAttachments == VK_TRUE ? "true" : "false");
        addProp("dynamicRenderingLocalReadMultisampledAttachments", p14.dynamicRenderingLocalReadMultisampledAttachments == VK_TRUE ? "true" : "false");
        addProp("earlyFragmentMultisampleCoverageAfterSampleCounting", p14.earlyFragmentMultisampleCoverageAfterSampleCounting == VK_TRUE ? "true" : "false");
        addProp("earlyFragmentSampleMaskTestBeforeSampleCounting", p14.earlyFragmentSampleMaskTestBeforeSampleCounting == VK_TRUE ? "true" : "false");
        addProp("depthStencilSwizzleOneSupport", p14.depthStencilSwizzleOneSupport == VK_TRUE ? "true" : "false");
        addProp("polygonModePointSize", p14.polygonModePointSize == VK_TRUE ? "true" : "false");
        addProp("nonStrictSinglePixelWideLinesUseParallelogram", p14.nonStrictSinglePixelWideLinesUseParallelogram == VK_TRUE ? "true" : "false");
        addProp("nonStrictWideLinesUseParallelogram", p14.nonStrictWideLinesUseParallelogram == VK_TRUE ? "true" : "false");
        addProp("blockTexelViewCompatibleMultipleLayers", p14.blockTexelViewCompatibleMultipleLayers == VK_TRUE ? "true" : "false");
        addProp("maxCombinedImageSamplerDescriptorCount", std::to_string(p14.maxCombinedImageSamplerDescriptorCount));
        addProp("fragmentShadingRateClampCombinerInputs", p14.fragmentShadingRateClampCombinerInputs == VK_TRUE ? "true" : "false");
        addProp("defaultRobustnessStorageBuffers", std::to_string(p14.defaultRobustnessStorageBuffers));
        addProp("defaultRobustnessUniformBuffers", std::to_string(p14.defaultRobustnessUniformBuffers));
        addProp("defaultRobustnessVertexInputs", std::to_string(p14.defaultRobustnessVertexInputs));
        addProp("defaultRobustnessImages", std::to_string(p14.defaultRobustnessImages));
        addProp("copySrcLayoutCount", std::to_string(p14.copySrcLayoutCount));
        addProp("copyDstLayoutCount", std::to_string(p14.copyDstLayoutCount));
        std::string srcLayouts;
        for (uint32_t j = 0; j < p14.copySrcLayoutCount && j < copySrcLayouts.size(); ++j) { if (j) srcLayouts += ", "; srcLayouts += std::to_string(copySrcLayouts[j]); }
        std::string dstLayouts;
        for (uint32_t j = 0; j < p14.copyDstLayoutCount && j < copyDstLayouts.size(); ++j) { if (j) dstLayouts += ", "; dstLayouts += std::to_string(copyDstLayouts[j]); }
        addProp("copySrcLayouts", copySrcWithinLimit ? srcLayouts : "Unavailable: safety cap exceeded");
        addProp("copyDstLayouts", copyDstWithinLimit ? dstLayouts : "Unavailable: safety cap exceeded");
        addProp("optimalTilingLayoutUUID", hexBytes(p14.optimalTilingLayoutUUID, 16));
        addProp("identicalMemoryTypeRequirements", p14.identicalMemoryTypeRequirements == VK_TRUE ? "true" : "false");
        out << "]}";
    }
    out << "]}";
    if (!saw14) {
        out.str("");
        out.clear();
        out << "{\"status\":\"not_applicable\",\"reason\":\"The installed Vulkan device API version is below 1.4.\",\"devices\":[]}";
    }
    api.destroyInstance(instance, nullptr);
    return out.str();
}

}



std::string collectVulkanAdvancedGroup(const char* driverMode, const char* driverIcdPath, const char* driverBundlePath, const char* hookLibDir, const char* group) {
    VulkanApi api;
    if (!api.open(driverMode, driverIcdPath, driverBundlePath, hookLibDir)) {
        return std::string("{\"status\":\"unavailable\",\"group\":") + jsonString(group ? group : "") + ",\"reason\":" + jsonString(api.openError.empty() ? "Vulkan loader unavailable" : api.openError) + ",\"devices\":[]}";
    }
    uint32_t loaderVersion = VK_API_VERSION_1_0;
    if (api.enumerateInstanceVersion && api.enumerateInstanceVersion(&loaderVersion) != VK_SUCCESS) loaderVersion = VK_API_VERSION_1_0;
    const auto queryInstanceExtensions = buildQueryInstanceExtensions(api);
    VkInstance instance = nullptr;
    uint32_t selectedInstanceApiVersion = VK_API_VERSION_1_0;
    g_probeStage = 2;
    const VkResult createResult = api.createInstanceCompatible(std::min(loaderVersion, VK_API_VERSION_1_3), queryInstanceExtensions, &instance, &selectedInstanceApiVersion);
    if (createResult != VK_SUCCESS || !instance) {
        return std::string("{\"status\":\"unavailable\",\"group\":") + jsonString(group ? group : "") + ",\"reason\":" + jsonString(std::string("Unable to create a Vulkan instance for the advanced query. VkResult=") + std::to_string(createResult)) + ",\"devices\":[]}";
    }
    if (!api.loadInstanceFunctions(instance)) {
        api.destroyInstance(instance, nullptr);
        return std::string("{\"status\":\"unavailable\",\"group\":") + jsonString(group ? group : "") + ",\"reason\":\"Required Vulkan instance entry points are unavailable.\",\"devices\":[]}";
    }
    const auto advancedDevicesResult = enumeratePhysicalDevicesRobust(api, instance);
    const VkResult advancedDeviceResult = advancedDevicesResult.first;
    std::vector<VkPhysicalDevice> devices = advancedDevicesResult.second;
    const uint32_t count = static_cast<uint32_t>(devices.size());
    if (advancedDeviceResult != VK_SUCCESS || count == 0) {
        api.destroyInstance(instance, nullptr);
        return std::string("{\"status\":\"not_applicable\",\"group\":") + jsonString(group ? group : "") + ",\"reason\":\"No physical Vulkan devices were enumerated.\",\"devices\":[]}";
    }
    if (group && std::strcmp(group, "tools") == 0 && !api.getPhysicalDeviceToolProperties) {
        api.destroyInstance(instance, nullptr);
        return "{\"status\":\"unavailable\",\"group\":\"tools\",\"reason\":\"vkGetPhysicalDeviceToolProperties is unavailable in this Vulkan stack.\",\"devices\":[]}";
    }
    if (group && std::strcmp(group, "queue2") == 0 && !api.getPhysicalDeviceQueueFamilyProperties2) {
        api.destroyInstance(instance, nullptr);
        return "{\"status\":\"unavailable\",\"group\":\"queue2\",\"reason\":\"vkGetPhysicalDeviceQueueFamilyProperties2 is unavailable in this Vulkan stack.\",\"devices\":[]}";
    }
    if (group && std::strcmp(group, "format2") == 0 && !api.getPhysicalDeviceFormatProperties2) {
        api.destroyInstance(instance, nullptr);
        return "{\"status\":\"unavailable\",\"group\":\"format2\",\"reason\":\"vkGetPhysicalDeviceFormatProperties2 is unavailable in this Vulkan stack.\",\"devices\":[]}";
    }
    if (group && std::strcmp(group, "imageFormat2") == 0 && !api.getPhysicalDeviceImageFormatProperties2) {
        api.destroyInstance(instance, nullptr);
        return "{\"status\":\"unavailable\",\"group\":\"imageFormat2\",\"reason\":\"vkGetPhysicalDeviceImageFormatProperties2 is unavailable in this Vulkan stack.\",\"devices\":[]}";
    }
    if (group && std::strcmp(group, "memory2") == 0 && !api.getPhysicalDeviceMemoryProperties2) {
        api.destroyInstance(instance, nullptr);
        return "{\"status\":\"unavailable\",\"group\":\"memory2\",\"reason\":\"vkGetPhysicalDeviceMemoryProperties2 is unavailable in this Vulkan stack.\",\"devices\":[]}";
    }
    if (group && std::strcmp(group, "sparse") == 0 && !api.getPhysicalDeviceSparseImageFormatProperties2) {
        api.destroyInstance(instance, nullptr);
        return "{\"status\":\"unavailable\",\"group\":\"sparse\",\"reason\":\"vkGetPhysicalDeviceSparseImageFormatProperties2 is unavailable in this Vulkan stack.\",\"devices\":[]}";
    }
    if (group && std::strcmp(group, "groups") == 0 && !api.enumeratePhysicalDeviceGroups) {
        api.destroyInstance(instance, nullptr);
        return "{\"status\":\"unavailable\",\"group\":\"groups\",\"reason\":\"vkEnumeratePhysicalDeviceGroups is unavailable in this Vulkan stack.\",\"devices\":[]}";
    }
    if (group && std::strcmp(group, "external") == 0 &&
        (!api.getPhysicalDeviceExternalBufferProperties || !api.getPhysicalDeviceExternalFenceProperties || !api.getPhysicalDeviceExternalSemaphoreProperties)) {
        api.destroyInstance(instance, nullptr);
        return "{\"status\":\"unavailable\",\"group\":\"external\",\"reason\":\"One or more external capability query entry points are unavailable in this Vulkan stack.\",\"devices\":[]}";
    }

    std::ostringstream out;
    out << "{\"status\":\"available\",\"group\":" << jsonString(group ? group : "") << ",\"reason\":\"\",\"devices\":[";
    bool firstDevice = true;
    auto addDevicePrefix = [&](uint32_t i) {
        VkPhysicalDeviceProperties physicalProperties{};
        getDevicePropertiesPrimary(api, devices[i], physicalProperties);
        const uint32_t vendorId = physicalProperties.vendorID;
        const uint32_t deviceId = physicalProperties.deviceID;
        const uint32_t apiVersion = physicalProperties.apiVersion;
        const char* name = physicalProperties.deviceName;
        if (!firstDevice) out << ',';
        firstDevice = false;
        out << "{\"vendorId\":" << vendorId << ",\"deviceId\":" << deviceId << ",\"name\":" << jsonString(name ? name : "Unknown GPU") << ",\"apiVersion\":" << jsonString(versionString(apiVersion));
    };
    for (uint32_t i = 0; i < count; ++i) {
        VkPhysicalDeviceProperties physicalProperties{};
        getDevicePropertiesPrimary(api, devices[i], physicalProperties);
        const uint32_t apiVersion = physicalProperties.apiVersion;
        const auto devExts = deviceExtensions(api, devices[i]);
        auto hasExt = [&](const char* n) { return hasExtension(devExts, n); };
        const uint32_t vendorId = physicalProperties.vendorID;
        const uint32_t deviceId = physicalProperties.deviceID;
        const char* name = physicalProperties.deviceName;
        if (group && std::strcmp(group, "tools") == 0) {
            addDevicePrefix(i);
            uint32_t toolCount = 0;
            api.getPhysicalDeviceToolProperties(devices[i], &toolCount, nullptr);
            if (toolCount > 0) {
                if (toolCount > kMaxToolEntries) toolCount = 0;
                std::vector<VkPhysicalDeviceToolProperties> tools(toolCount);
                for (auto& t : tools) { t.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TOOL_PROPERTIES; t.pNext = nullptr; }
                api.getPhysicalDeviceToolProperties(devices[i], &toolCount, tools.data());
                {
                    out << ",\"properties\":[";
                    for (uint32_t t = 0; t < toolCount; ++t) {
                        if (t) out << ',';
                        auto toolPurposes = [](uint32_t purposes) {
                            std::string value;
                            const std::pair<uint32_t, const char*> names[] = {{0x1u, "WARNING"}, {0x2u, "VALIDATION"}, {0x4u, "PROFILING"}, {0x8u, "TRACING"}, {0x10u, "ADDITIONAL_FEATURES"}, {0x20u, "MODIFYING_FEATURES"}};
                            for (const auto& entry : names) { if ((purposes & entry.first) != 0) { if (!value.empty()) value += ", "; value += entry.second; } }
                            if (value.empty()) value = "NONE";
                            return value;
                        };
                        out << "{\"section\":\"Vulkan Tool\",\"name\":" << jsonString(tools[t].name) << ",\"value\":" << jsonString(std::string(tools[t].description) + " | version " + std::string(tools[t].version) + " | purposes=" + toolPurposes(tools[t].purposes) + " | layer=" + tools[t].layer) << '}';
                    }
                    out << "]";
                }
            }
            out << "}";
        } else if (group && std::strcmp(group, "queue2") == 0) {
            addDevicePrefix(i);
            uint32_t qcount = 0;
            api.getPhysicalDeviceQueueFamilyProperties2(devices[i], &qcount, nullptr);
            if (qcount > kMaxQueueFamilyEntries) {
                out << ",\"properties\":[],\"safetyRejected\":true,\"reason\":\"Queue-family count exceeds safety limit.\"}";
                continue;
            }
            std::vector<VkQueueFamilyProperties2> queues(qcount);
            const bool maintenance11 = hasExt("VK_KHR_maintenance11");
            const bool videoQueue = hasExt("VK_KHR_video_queue");
            std::vector<VkQueueFamilyOptimalImageTransferGranularityPropertiesKHR> opt(qcount);
            std::vector<VkQueueFamilyVideoPropertiesKHR> video(qcount);
            for (uint32_t q = 0; q < qcount; ++q) {
                queues[q].sType = VK_STRUCTURE_TYPE_QUEUE_FAMILY_PROPERTIES_2;
                queues[q].pNext = nullptr;
                if (videoQueue) {
                    video[q].sType = VK_STRUCTURE_TYPE_QUEUE_FAMILY_VIDEO_PROPERTIES_KHR;
                    video[q].pNext = nullptr;
                    queues[q].pNext = &video[q];
                }
                if (maintenance11) {
                    opt[q].sType = VK_STRUCTURE_TYPE_QUEUE_FAMILY_OPTIMAL_IMAGE_TRANSFER_GRANULARITY_PROPERTIES_KHR;
                    opt[q].pNext = nullptr;
                    if (videoQueue) video[q].pNext = &opt[q]; else queues[q].pNext = &opt[q];
                }
            }
            if (qcount) {
                api.getPhysicalDeviceQueueFamilyProperties2(devices[i], &qcount, queues.data());
                out << ",\"properties\":[";
                for (uint32_t q = 0; q < qcount; ++q) {
                    if (q) out << ',';
                    const auto& x = queues[q].queueFamilyProperties;
                    std::ostringstream gran; gran << x.minImageTransferGranularity.width << " × " << x.minImageTransferGranularity.height << " × " << x.minImageTransferGranularity.depth;
                    std::string value = "queueCount=" + std::to_string(x.queueCount) + ", flags=0x";
                    std::ostringstream flags; flags << std::hex << x.queueFlags; value += flags.str() + ", timestampValidBits=" + std::to_string(x.timestampValidBits) + ", minImageTransferGranularity=" + gran.str();
                    if (maintenance11) value += ", optimalImageTransferGranularity=" + std::to_string(opt[q].optimalImageTransferGranularity.width) + " × " + std::to_string(opt[q].optimalImageTransferGranularity.height) + " × " + std::to_string(opt[q].optimalImageTransferGranularity.depth);
                    if (videoQueue) {
                        std::ostringstream vf; vf << "0x" << std::hex << video[q].videoCodecOperations;
                        value += ", videoCodecOperations=" + vf.str();
                    }
                    out << "{\"section\":\"Queue Family Properties2\",\"name\":\"Queue Family " << q << "\",\"value\":" << jsonString(value) << '}';
                }
                out << "]";
                if (videoQueue) {
                    out << ",\"videoQueues\":[";
                    for (uint32_t q = 0; q < qcount; ++q) {
                        if (q) out << ',';
                        out << "{\"index\":" << q << ",\"videoCodecOperations\":" << video[q].videoCodecOperations << '}';
                    }
                    out << "]";
                }
            }
            out << "}";
        } else if (group && std::strcmp(group, "format2") == 0) {
            addDevicePrefix(i);
            out << ",\"properties\":[";
            bool firstProp = true;
            const bool formatFeatureFlags2 = VK_API_VERSION_MINOR(apiVersion) >= 3 || hasExt("VK_KHR_format_feature_flags2");
            for (VkFormat fmt : knownFormatValues()) {
                if (!shouldQueryFormat(fmt, apiVersion, devExts)) continue;
                VkFormatProperties3 p3{}; p3.sType = VK_STRUCTURE_TYPE_FORMAT_PROPERTIES_3; p3.pNext = nullptr;
                VkFormatProperties2 p2{}; p2.sType = VK_STRUCTURE_TYPE_FORMAT_PROPERTIES_2; p2.pNext = formatFeatureFlags2 ? static_cast<void*>(&p3) : nullptr;
                if (api.getPhysicalDeviceFormatProperties2(devices[i], fmt, &p2), true) {
                    if (!firstProp) out << ',';
                    firstProp = false;
                    std::ostringstream value;
                    value << "linear=0x" << std::hex << p2.formatProperties.linearTilingFeatures << ", optimal=0x" << p2.formatProperties.optimalTilingFeatures << ", buffer=0x" << p2.formatProperties.bufferFeatures << ", featureFlags2 linear=0x" << (formatFeatureFlags2 ? p3.linearTilingFeatures : 0) << " optimal=0x" << (formatFeatureFlags2 ? p3.optimalTilingFeatures : 0) << " buffer=0x" << (formatFeatureFlags2 ? p3.bufferFeatures : 0);
                    out << "{\"section\":\"Format Properties2\",\"name\":" << jsonString(formatName(fmt)) << ",\"value\":" << jsonString(value.str()) << '}';
                }
            }
            out << "]}";
        } else if (group && std::strcmp(group, "imageFormat2") == 0) {
            addDevicePrefix(i);
            out << ",\"properties\":[";
            bool firstProp = true;
            const VkImageTiling tilings[] = {VK_IMAGE_TILING_LINEAR, VK_IMAGE_TILING_OPTIMAL};
            const bool hasOpaqueFd = hasExt("VK_KHR_external_memory_fd");
            const bool hasAhb = hasExt("VK_ANDROID_external_memory_android_hardware_buffer");
            for (VkFormat fmt : knownFormatValues()) {
                if (!shouldQueryFormat(fmt, apiVersion, devExts)) continue;
                for (uint32_t tiling : tilings) {
                    const VkExternalMemoryHandleTypeFlagBits handleTypes[2] = {VK_EXTERNAL_MEMORY_HANDLE_TYPE_OPAQUE_FD_BIT, VK_EXTERNAL_MEMORY_HANDLE_TYPE_ANDROID_HARDWARE_BUFFER_BIT_ANDROID};
                    const bool handleEnabled[2] = {hasOpaqueFd, hasAhb};
                    VkPhysicalDeviceImageFormatInfo2 info{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_FORMAT_INFO_2, nullptr, fmt, VK_IMAGE_TYPE_2D, static_cast<VkImageTiling>(tiling), VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_SAMPLED_BIT, 0};
                    VkImageFormatProperties2 props{VK_STRUCTURE_TYPE_IMAGE_FORMAT_PROPERTIES_2, nullptr, {}};
                    const VkResult baseResult = api.getPhysicalDeviceImageFormatProperties2(devices[i], &info, &props);
                    if (baseResult == VK_SUCCESS) {
                        if (!firstProp) out << ','; firstProp = false;
                        std::ostringstream value; value << "tiling=" << (tiling == VK_IMAGE_TILING_LINEAR ? "LINEAR" : "OPTIMAL") << ", extent=" << props.imageFormatProperties.maxExtent.width << " × " << props.imageFormatProperties.maxExtent.height << " × " << props.imageFormatProperties.maxExtent.depth << ", mipLevels=" << props.imageFormatProperties.maxMipLevels << ", arrayLayers=" << props.imageFormatProperties.maxArrayLayers << ", sampleCounts=0x" << std::hex << props.imageFormatProperties.sampleCounts << ", maxResourceSize=" << std::dec << props.imageFormatProperties.maxResourceSize;
                        out << "{\"section\":\"Image Format Properties2\",\"name\":" << jsonString(formatName(fmt) + " · " + (tiling == VK_IMAGE_TILING_LINEAR ? std::string("LINEAR") : std::string("OPTIMAL"))) << ",\"value\":" << jsonString(value.str()) << '}';
                    }
                    if (baseResult == VK_SUCCESS) {
                        for (uint32_t handleIndex = 0; handleIndex < 2; ++handleIndex) {
                            if (!handleEnabled[handleIndex]) continue;
                            VkPhysicalDeviceExternalImageFormatInfo extImageInfo{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_IMAGE_FORMAT_INFO, nullptr, handleTypes[handleIndex]};
                            VkExternalImageFormatProperties extImageProps{VK_STRUCTURE_TYPE_EXTERNAL_IMAGE_FORMAT_PROPERTIES, nullptr, {}};
                            info.pNext = &extImageInfo;
                            props = {VK_STRUCTURE_TYPE_IMAGE_FORMAT_PROPERTIES_2, &extImageProps, {}};
                            const VkResult r = api.getPhysicalDeviceImageFormatProperties2(devices[i], &info, &props);
                            if (r != VK_SUCCESS) continue;
                            if (!firstProp) out << ','; firstProp = false;
                            std::ostringstream value; value << "tiling=" << (tiling == VK_IMAGE_TILING_LINEAR ? "LINEAR" : "OPTIMAL") << ", extent=" << props.imageFormatProperties.maxExtent.width << " × " << props.imageFormatProperties.maxExtent.height << " × " << props.imageFormatProperties.maxExtent.depth << ", mipLevels=" << props.imageFormatProperties.maxMipLevels << ", arrayLayers=" << props.imageFormatProperties.maxArrayLayers << ", sampleCounts=0x" << std::hex << props.imageFormatProperties.sampleCounts << ", maxResourceSize=" << std::dec << props.imageFormatProperties.maxResourceSize << ", externalHandle=" << (handleIndex == 0 ? "OPAQUE_FD" : "ANDROID_HARDWARE_BUFFER") << ", externalMemoryFeatures=0x" << std::hex << extImageProps.externalMemoryProperties.externalMemoryFeatures << ", exportFromImported=0x" << extImageProps.externalMemoryProperties.exportFromImportedHandleTypes << ", compatibleHandles=0x" << extImageProps.externalMemoryProperties.compatibleHandleTypes;
                            out << "{\"section\":\"Image Format Properties2\",\"name\":" << jsonString(formatName(fmt) + " · " + (tiling == VK_IMAGE_TILING_LINEAR ? std::string("LINEAR") : std::string("OPTIMAL")) + " · " + (handleIndex == 0 ? std::string("OPAQUE_FD") : std::string("ANDROID_HARDWARE_BUFFER"))) << ",\"value\":" << jsonString(value.str()) << '}';
                        }
                    }
                }
            }
            out << "]}";
        } else if (group && std::strcmp(group, "external") == 0) {
            addDevicePrefix(i);
            out << ",\"properties\":[";
            bool firstExternal = true;
            if (hasExt("VK_KHR_external_memory_fd")) {
                VkPhysicalDeviceExternalBufferInfo bi{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_BUFFER_INFO, nullptr, 0, VK_BUFFER_USAGE_TRANSFER_SRC_BIT | VK_BUFFER_USAGE_TRANSFER_DST_BIT | VK_BUFFER_USAGE_STORAGE_BUFFER_BIT, VK_EXTERNAL_MEMORY_HANDLE_TYPE_OPAQUE_FD_BIT};
                VkExternalBufferProperties bp{VK_STRUCTURE_TYPE_EXTERNAL_BUFFER_PROPERTIES, nullptr, {}};
                api.getPhysicalDeviceExternalBufferProperties(devices[i], &bi, &bp);
                out << "{\"section\":\"External Capabilities\",\"name\":\"Opaque FD Buffer\",\"value\":" << jsonString("features=0x" + [&](){ std::ostringstream x; x << std::hex << bp.externalMemoryProperties.externalMemoryFeatures; return x.str(); }() + ", export=0x" + [&](){ std::ostringstream x; x << std::hex << bp.externalMemoryProperties.exportFromImportedHandleTypes; return x.str(); }() + ", compatible=0x" + [&](){ std::ostringstream x; x << std::hex << bp.externalMemoryProperties.compatibleHandleTypes; return x.str(); }()) << "}"; firstExternal = false;
            }
            if (hasExt("VK_KHR_external_fence_fd")) {
                if (!firstExternal) out << ','; firstExternal = false;
                VkPhysicalDeviceExternalFenceInfo fi{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_FENCE_INFO, nullptr, VK_EXTERNAL_FENCE_HANDLE_TYPE_SYNC_FD_BIT};
                VkExternalFenceProperties fp{VK_STRUCTURE_TYPE_EXTERNAL_FENCE_PROPERTIES, nullptr, 0, 0, 0};
                api.getPhysicalDeviceExternalFenceProperties(devices[i], &fi, &fp);
                out << "{\"section\":\"External Capabilities\",\"name\":\"Opaque FD Fence\",\"value\":\"features=0x" << std::hex << fp.externalFenceFeatures << ", export=0x" << fp.exportFromImportedHandleTypes << ", compatible=0x" << fp.compatibleHandleTypes << "\"}";
            }
            if (hasExt("VK_KHR_external_semaphore_fd")) {
                if (!firstExternal) out << ',';
                VkPhysicalDeviceExternalSemaphoreInfo si{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_SEMAPHORE_INFO, nullptr, VK_EXTERNAL_SEMAPHORE_HANDLE_TYPE_SYNC_FD_BIT};
                VkExternalSemaphoreProperties sp{VK_STRUCTURE_TYPE_EXTERNAL_SEMAPHORE_PROPERTIES, nullptr, 0, 0, 0};
                api.getPhysicalDeviceExternalSemaphoreProperties(devices[i], &si, &sp);
                out << "{\"section\":\"External Capabilities\",\"name\":\"Opaque FD Semaphore\",\"value\":\"features=0x" << std::hex << sp.externalSemaphoreFeatures << ", export=0x" << sp.exportFromImportedHandleTypes << ", compatible=0x" << sp.compatibleHandleTypes << "\"}";
            }
            out << "]}";
        } else if (group && std::strcmp(group, "sparse") == 0) {
            addDevicePrefix(i);
            out << ",\"properties\":[";
            bool firstProp = true;
            for (VkFormat fmt : knownFormatValues()) {
                if (!shouldQueryFormat(fmt, apiVersion, devExts)) continue;
                VkPhysicalDeviceSparseImageFormatInfo2 info{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SPARSE_IMAGE_FORMAT_INFO_2, nullptr, fmt, VK_IMAGE_TYPE_2D, VK_SAMPLE_COUNT_1_BIT, VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_SAMPLED_BIT, VK_IMAGE_TILING_OPTIMAL};
                uint32_t pc = 0;
                api.getPhysicalDeviceSparseImageFormatProperties2(devices[i], &info, &pc, nullptr);
                const bool sparseSafetyRejected = pc > kMaxSparseImageFormatEntries;
                if (sparseSafetyRejected) pc = 0;
                std::vector<VkSparseImageFormatProperties2> props(pc);
                for (auto& x : props) { x.sType = VK_STRUCTURE_TYPE_SPARSE_IMAGE_FORMAT_PROPERTIES_2; x.pNext = nullptr; }
                if (sparseSafetyRejected) {
                    if (!firstProp) out << ',';
                    firstProp = false;
                    out << "{\"section\":\"Sparse Image Format Properties2\",\"name\":\"" << jsonString(formatName(fmt)) << "\",\"value\":" << jsonString("Unavailable: property count exceeds safety limit.") << '}';
                } else if (pc) {
                    api.getPhysicalDeviceSparseImageFormatProperties2(devices[i], &info, &pc, props.data());
                    for (uint32_t k = 0; k < pc; ++k) {
                        if (!firstProp) out << ','; firstProp = false;
                        const auto& x = props[k].properties;
                        std::ostringstream value; value << "aspectMask=0x" << std::hex << x.aspectMask << ", granularity=" << std::dec << x.imageGranularity.width << " × " << x.imageGranularity.height << " × " << x.imageGranularity.depth << ", flags=0x" << std::hex << x.flags;
                        out << "{\"section\":\"Sparse Image Format Properties2\",\"name\":" << jsonString(formatName(fmt) + " #" + std::to_string(k)) << ",\"value\":" << jsonString(value.str()) << '}';
                    }
                }
            }
            out << "]}";
        } else if (group && std::strcmp(group, "memory2") == 0) {
            addDevicePrefix(i);
            VkPhysicalDeviceMemoryBudgetPropertiesEXT budget{};
            VkPhysicalDeviceMemoryProperties2 mem{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MEMORY_PROPERTIES_2, nullptr, {}};
            if (hasExt("VK_EXT_memory_budget")) { budget.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MEMORY_BUDGET_PROPERTIES_EXT; budget.pNext = nullptr; mem.pNext = &budget; }
            api.getPhysicalDeviceMemoryProperties2(devices[i], &mem);
            out << ",\"properties\":[";
            const uint32_t budgetHeapCount = std::min(mem.memoryProperties.memoryHeapCount, kMaxMemoryHeapEntries);
            for (uint32_t h = 0; h < budgetHeapCount; ++h) {
                if (h) out << ',';
                std::ostringstream value; value << "size=" << mem.memoryProperties.memoryHeaps[h].size << ", flags=0x" << std::hex << mem.memoryProperties.memoryHeaps[h].flags;
                if (hasExt("VK_EXT_memory_budget")) value << ", budget=" << std::dec << budget.heapBudget[h] << ", usage=" << budget.heapUsage[h];
                out << "{\"section\":\"Memory Properties2\",\"name\":\"Heap " << h << "\",\"value\":" << jsonString(value.str()) << '}';
            }
            out << "]}";
        } else if (group && std::strcmp(group, "groups") == 0) {
            addDevicePrefix(i);
            out << ",\"properties\":[{\"section\":\"Physical Device Groups\",\"name\":\"Group containing device\",\"value\":\"Query performed for this Vulkan instance\"}]" << "}";
        } else {
            out << "{\"vendorId\":" << vendorId << ",\"deviceId\":" << deviceId << ",\"name\":" << jsonString(name ? name : "Unknown GPU") << "}";
        }
    }
    if (group && std::strcmp(group, "groups") == 0 && api.enumeratePhysicalDeviceGroups) {
        uint32_t gc = 0;
        if (api.enumeratePhysicalDeviceGroups(instance, &gc, nullptr) == VK_SUCCESS) {
            if (gc > kMaxDeviceGroupEntries) { api.destroyInstance(instance, nullptr); return "{\"status\":\"unavailable\",\"group\":\"groups\",\"reason\":\"Physical device group count exceeds safety limit.\",\"devices\":[]}"; }
            std::vector<VkPhysicalDeviceGroupProperties> groups(gc);
            for (auto& g : groups) { g.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_GROUP_PROPERTIES; g.pNext = nullptr; }
            if (gc && api.enumeratePhysicalDeviceGroups(instance, &gc, groups.data()) == VK_SUCCESS) {
                out << "]";
                std::ostringstream groupJson;
                for (uint32_t g = 0; g < gc; ++g) {
                    if (g == 0) out << ",\"groupProperties\":["; else out << ',';
                    std::ostringstream groupValue;
                    groupValue << "physicalDeviceCount=" << groups[g].physicalDeviceCount << ", subsetAllocation=" << (groups[g].subsetAllocation == VK_TRUE ? "true" : "false") << ", devices=";
                    for (uint32_t d = 0; d < groups[g].physicalDeviceCount && d < VK_MAX_DEVICE_GROUP_SIZE; ++d) {
                        if (d) groupValue << "; ";
                        VkPhysicalDeviceProperties groupProperties{};
                        getDevicePropertiesPrimary(api, groups[g].physicalDevices[d], groupProperties);
                        const uint32_t gpVendor = groupProperties.vendorID;
                        const uint32_t gpDevice = groupProperties.deviceID;
                        const char* gpName = groupProperties.deviceName;
                        groupValue << (gpName ? gpName : "Unknown GPU") << " [0x" << std::hex << gpVendor << ":0x" << gpDevice << std::dec << "]";
                    }
                    out << "{\"section\":\"Physical Device Group\",\"name\":\"Group " << g << "\",\"value\":" << jsonString(groupValue.str()) << '}';
                }
                if (gc) out << "]";
                out << "}";
            } else { out << "]}"; }
        } else { out << "]}"; }
    } else {
        out << "]}";
    }
    api.destroyInstance(instance, nullptr);
    return out.str();
}

std::string collectVulkanSimpleFeatureGroup(const char* driverMode, const char* driverIcdPath, const char* driverBundlePath, const char* hookLibDir, const char* group) {
    const char* extensionName = nullptr;
    uint32_t structureType = 0;
    if (std::strcmp(group, "maintenance11") == 0) { extensionName = "VK_KHR_maintenance11"; structureType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_MAINTENANCE_11_FEATURES_KHR; }
    else if (std::strcmp(group, "deviceAddressCommands") == 0) { extensionName = "VK_KHR_device_address_commands"; structureType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DEVICE_ADDRESS_COMMANDS_FEATURES_KHR; }
    else if (std::strcmp(group, "shaderUniformBufferUnsizedArray") == 0) { extensionName = "VK_EXT_shader_uniform_buffer_unsized_array"; structureType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_UNIFORM_BUFFER_UNSIZED_ARRAY_FEATURES_EXT; }
    else if (std::strcmp(group, "dataGraphOpticalFlow") == 0) { extensionName = "VK_ARM_data_graph_optical_flow"; structureType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DATA_GRAPH_OPTICAL_FLOW_FEATURES_ARM; }
    else if (std::strcmp(group, "pipelineCacheIncrementalMode") == 0) { extensionName = "VK_SEC_pipeline_cache_incremental_mode"; structureType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PIPELINE_CACHE_INCREMENTAL_MODE_FEATURES_SEC; }
    else if (std::strcmp(group, "extendedFlags") == 0) { extensionName = "VK_KHR_extended_flags"; structureType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_FLAGS_FEATURES_KHR; }
    else if (std::strcmp(group, "shaderOcpMicroscalingTypes") == 0) { extensionName = "VK_EXT_shader_ocp_microscaling_types"; structureType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_OCP_MICROSCALING_TYPES_FEATURES_EXT; }
    else return std::string("{\"status\":\"unavailable\",\"group\":") + jsonString(group) + ",\"reason\":\"Unknown feature query group.\",\"devices\":[]}";
    VulkanApi api;
    if (!api.open(driverMode, driverIcdPath, driverBundlePath, hookLibDir)) return std::string("{\"status\":\"unavailable\",\"group\":") + jsonString(group) + ",\"extension\":" + jsonString(extensionName) + ",\"reason\":" + jsonString(api.openError) + ",\"devices\":[]}";
    uint32_t loaderVersion = VK_API_VERSION_1_0; if (api.enumerateInstanceVersion && api.enumerateInstanceVersion(&loaderVersion) != VK_SUCCESS) loaderVersion = VK_API_VERSION_1_0;
    const auto queryInstanceExtensions = buildQueryInstanceExtensions(api);
    VkInstance inst=nullptr;
    uint32_t selectedInstanceApiVersion = VK_API_VERSION_1_0;
    g_probeStage = 2;
    if (api.createInstanceCompatible(std::min(loaderVersion, VK_API_VERSION_1_3), queryInstanceExtensions, &inst, &selectedInstanceApiVersion) != VK_SUCCESS || !inst || !api.loadInstanceFunctions(inst) || !api.getPhysicalDeviceFeatures2) { if(inst) api.destroyInstance(inst,nullptr); return std::string("{\"status\":\"unavailable\",\"group\":")+jsonString(group)+",\"extension\":"+jsonString(extensionName)+",\"reason\":\"Extended feature query is unavailable.\",\"devices\":[]}"; }
    const auto extensionDevicesResult = enumeratePhysicalDevicesRobust(api, inst);
    const VkResult extensionDeviceResult = extensionDevicesResult.first;
    std::vector<VkPhysicalDevice> devs = extensionDevicesResult.second;
    const uint32_t count = static_cast<uint32_t>(devs.size());
    if (extensionDeviceResult != VK_SUCCESS || count == 0) { api.destroyInstance(inst,nullptr); return std::string("{\"status\":\"not_applicable\",\"group\":")+jsonString(group)+",\"extension\":"+jsonString(extensionName)+",\"reason\":\"No physical devices.\",\"devices\":[]}"; }
    std::ostringstream out; out<<"{\"status\":\"available\",\"group\":"<<jsonString(group)<<",\"extension\":"<<jsonString(extensionName)<<",\"reason\":\"\",\"devices\":["; bool first=false;
    for(uint32_t i=0;i<count;++i){auto exts = deviceExtensions(api, devs[i]); bool supported = std::any_of(exts.begin(), exts.end(), [&](const VkExtensionProperties& e){ return std::strcmp(e.extensionName, extensionName) == 0; }); if (!supported) continue; VkPhysicalDeviceProperties physicalProperties{}; getDevicePropertiesPrimary(api, devs[i], physicalProperties); uint32_t vendor=physicalProperties.vendorID, deviceId=physicalProperties.deviceID; const char* name=physicalProperties.deviceName; if(first)out<<',';first=true; out<<"{\"vendorId\":"<<vendor<<",\"deviceId\":"<<deviceId<<",\"name\":"<<jsonString(name?name:"Unknown GPU")<<",\"features\":[";
        if(std::strcmp(group,"maintenance11")==0){VkPhysicalDeviceMaintenance11FeaturesKHR f{static_cast<VkStructureType>(structureType),nullptr,VK_FALSE};VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}};api.queryFeatures2(devs[i],&f2);out<<"{\"name\":\"VK_KHR_maintenance11 / maintenance11\",\"supported\":"<<jsonBool(f.maintenance11==VK_TRUE)<<"}";}
        else if(std::strcmp(group,"deviceAddressCommands")==0){VkPhysicalDeviceDeviceAddressCommandsFeaturesKHR f{static_cast<VkStructureType>(structureType),nullptr,VK_FALSE};VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}};api.queryFeatures2(devs[i],&f2);out<<"{\"name\":\"VK_KHR_device_address_commands / deviceAddressCommands\",\"supported\":"<<jsonBool(f.deviceAddressCommands==VK_TRUE)<<"}";}
        else if(std::strcmp(group,"shaderUniformBufferUnsizedArray")==0){VkPhysicalDeviceShaderUniformBufferUnsizedArrayFeaturesEXT f{static_cast<VkStructureType>(structureType),nullptr,VK_FALSE};VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}};api.queryFeatures2(devs[i],&f2);out<<"{\"name\":\"VK_EXT_shader_uniform_buffer_unsized_array / shaderUniformBufferUnsizedArray\",\"supported\":"<<jsonBool(f.shaderUniformBufferUnsizedArray==VK_TRUE)<<"}";}
        else if(std::strcmp(group,"dataGraphOpticalFlow")==0){VkPhysicalDeviceDataGraphOpticalFlowFeaturesARM f{static_cast<VkStructureType>(structureType),nullptr,VK_FALSE};VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}};api.queryFeatures2(devs[i],&f2);out<<"{\"name\":\"VK_ARM_data_graph_optical_flow / dataGraphOpticalFlow\",\"supported\":"<<jsonBool(f.dataGraphOpticalFlow==VK_TRUE)<<"}";}
        else if(std::strcmp(group,"pipelineCacheIncrementalMode")==0){VkPhysicalDevicePipelineCacheIncrementalModeFeaturesSEC f{static_cast<VkStructureType>(structureType),nullptr,VK_FALSE};VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}};api.queryFeatures2(devs[i],&f2);out<<"{\"name\":\"VK_SEC_pipeline_cache_incremental_mode / pipelineCacheIncrementalMode\",\"supported\":"<<jsonBool(f.pipelineCacheIncrementalMode==VK_TRUE)<<"}";}
        else if(std::strcmp(group,"extendedFlags")==0){VkPhysicalDeviceExtendedFlagsFeaturesKHR f{static_cast<VkStructureType>(structureType),nullptr,VK_FALSE};VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}};api.queryFeatures2(devs[i],&f2);out<<"{\"name\":\"VK_KHR_extended_flags / extendedFlags\",\"supported\":"<<jsonBool(f.extendedFlags==VK_TRUE)<<"}";}
        else {VkPhysicalDeviceShaderOCPMicroscalingTypesFeaturesEXT f{static_cast<VkStructureType>(structureType),nullptr,VK_FALSE,VK_FALSE,VK_FALSE,VK_FALSE};VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}};api.queryFeatures2(devs[i],&f2);out<<"{\"name\":\"VK_EXT_shader_ocp_microscaling_types / shaderFloat4\",\"supported\":"<<jsonBool(f.shaderFloat4==VK_TRUE)<<"},{\"name\":\"VK_EXT_shader_ocp_microscaling_types / shaderFloat6\",\"supported\":"<<jsonBool(f.shaderFloat6==VK_TRUE)<<"},{\"name\":\"VK_EXT_shader_ocp_microscaling_types / shaderFloat8UnsignedE8M0\",\"supported\":"<<jsonBool(f.shaderFloat8UnsignedE8M0==VK_TRUE)<<"},{\"name\":\"VK_EXT_shader_ocp_microscaling_types / shaderMXInt8\",\"supported\":"<<jsonBool(f.shaderMXInt8==VK_TRUE)<<"}";}
        out<<"]}";
    }
    out<<"]}";api.destroyInstance(inst,nullptr);return out.str();
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_efishell_vulkanscope_VulkanProbeService_collectVulkanData(JNIEnv* env, jobject, jobject surface, jstring driverModeString, jstring driverIcdPathString, jstring driverBundlePathString, jstring hookLibDirString, jstring resultPathString) {
    const char* driverMode = driverModeString ? env->GetStringUTFChars(driverModeString, nullptr) : nullptr;
    const char* driverIcdPath = driverIcdPathString ? env->GetStringUTFChars(driverIcdPathString, nullptr) : nullptr;
    const char* driverBundlePath = driverBundlePathString ? env->GetStringUTFChars(driverBundlePathString, nullptr) : nullptr;
    const char* hookLibDir = hookLibDirString ? env->GetStringUTFChars(hookLibDirString, nullptr) : nullptr;
    const char* resultPath = resultPathString ? env->GetStringUTFChars(resultPathString, nullptr) : nullptr;
    installProbeCrashGuard(resultPath);
    const std::string result = collect(surface, env, driverMode, driverIcdPath, driverBundlePath, hookLibDir, resultPath);
    clearProbeCrashGuard(resultPath);
    if (resultPathString && resultPath) env->ReleaseStringUTFChars(resultPathString, resultPath);
    if (hookLibDirString && hookLibDir) env->ReleaseStringUTFChars(hookLibDirString, hookLibDir);
    if (driverBundlePathString && driverBundlePath) env->ReleaseStringUTFChars(driverBundlePathString, driverBundlePath);
    if (driverIcdPathString && driverIcdPath) env->ReleaseStringUTFChars(driverIcdPathString, driverIcdPath);
    if (driverModeString && driverMode) env->ReleaseStringUTFChars(driverModeString, driverMode);
    return env->NewStringUTF(result.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_efishell_vulkanscope_VulkanProbeService_collectVulkanSurfaceData(JNIEnv* env, jobject, jobject surface, jstring driverModeString, jstring driverIcdPathString, jstring driverBundlePathString, jstring hookLibDirString, jstring resultPathString) {
    const char* driverMode = driverModeString ? env->GetStringUTFChars(driverModeString, nullptr) : nullptr;
    const char* driverIcdPath = driverIcdPathString ? env->GetStringUTFChars(driverIcdPathString, nullptr) : nullptr;
    const char* driverBundlePath = driverBundlePathString ? env->GetStringUTFChars(driverBundlePathString, nullptr) : nullptr;
    const char* hookLibDir = hookLibDirString ? env->GetStringUTFChars(hookLibDirString, nullptr) : nullptr;
    const char* resultPath = resultPathString ? env->GetStringUTFChars(resultPathString, nullptr) : nullptr;
    installProbeCrashGuard(resultPath);
    const std::string result = collectVulkanSurface(surface, env, driverMode, driverIcdPath, driverBundlePath, hookLibDir);
    clearProbeCrashGuard(resultPath);
    if (resultPathString && resultPath) env->ReleaseStringUTFChars(resultPathString, resultPath);
    if (hookLibDirString && hookLibDir) env->ReleaseStringUTFChars(hookLibDirString, hookLibDir);
    if (driverBundlePathString && driverBundlePath) env->ReleaseStringUTFChars(driverBundlePathString, driverBundlePath);
    if (driverIcdPathString && driverIcdPath) env->ReleaseStringUTFChars(driverIcdPathString, driverIcdPath);
    if (driverModeString && driverMode) env->ReleaseStringUTFChars(driverModeString, driverMode);
    return env->NewStringUTF(result.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_efishell_vulkanscope_VulkanProbeService_collectVulkanQueryData(JNIEnv* env, jobject, jstring groupString, jstring driverModeString, jstring driverIcdPathString, jstring driverBundlePathString, jstring hookLibDirString, jstring resultPathString) {
    const char* group = groupString ? env->GetStringUTFChars(groupString, nullptr) : nullptr;
    const char* driverMode = driverModeString ? env->GetStringUTFChars(driverModeString, nullptr) : nullptr;
    const char* driverIcdPath = driverIcdPathString ? env->GetStringUTFChars(driverIcdPathString, nullptr) : nullptr;
    const char* driverBundlePath = driverBundlePathString ? env->GetStringUTFChars(driverBundlePathString, nullptr) : nullptr;
    const char* hookLibDir = hookLibDirString ? env->GetStringUTFChars(hookLibDirString, nullptr) : nullptr;
    const char* resultPath = resultPathString ? env->GetStringUTFChars(resultPathString, nullptr) : nullptr;
    installProbeCrashGuard(resultPath);
    const std::string groupName = group ? group : "";
    const auto* descriptor = vulkanscope_registry::findQueryDescriptor(groupName.c_str());
    std::string result;
    if (groupName == "metadata") {
        result = collectVulkanMetadata(driverMode, driverIcdPath, driverBundlePath, hookLibDir);
    } else if (!descriptor) {
        result = std::string("{\"status\":\"unavailable\",\"group\":") + jsonString(groupName) + ",\"reason\":\"Unknown registry query group.\",\"devices\":[]}";
    } else if (std::strcmp(descriptor->scope, "core") == 0 && descriptor->minApiMinor == 1) {
        result = collectVulkanCoreGroup(driverMode, driverIcdPath, driverBundlePath, hookLibDir, 1);
    } else if (std::strcmp(descriptor->scope, "core") == 0 && descriptor->minApiMinor == 2) {
        result = collectVulkanCoreGroup(driverMode, driverIcdPath, driverBundlePath, hookLibDir, 2);
    } else if (std::strcmp(descriptor->scope, "core") == 0 && descriptor->minApiMinor == 3) {
        result = collectVulkanCoreGroup(driverMode, driverIcdPath, driverBundlePath, hookLibDir, 3);
    } else if (std::strcmp(descriptor->scope, "core") == 0 && descriptor->minApiMinor == 4) {
        result = collectVulkan14(driverMode, driverIcdPath, driverBundlePath, hookLibDir);
    } else if (std::strcmp(descriptor->scope, "advanced") == 0) {
        result = collectVulkanAdvancedGroup(driverMode, driverIcdPath, driverBundlePath, hookLibDir, groupName.c_str());
    } else if (std::strcmp(descriptor->queryKind, "feature-only") == 0) {
        result = collectVulkanSimpleFeatureGroup(driverMode, driverIcdPath, driverBundlePath, hookLibDir, groupName.c_str());
    } else if (std::strcmp(descriptor->scope, "device-extension") == 0) {
        result = collectVulkanExtensionGroup(driverMode, driverIcdPath, driverBundlePath, hookLibDir, groupName.c_str());
    } else {
        result = std::string("{\"status\":\"unavailable\",\"group\":") + jsonString(groupName) + ",\"reason\":\"Registry query descriptor is not executable.\",\"devices\":[]}";
    }
    clearProbeCrashGuard(resultPath);
    if (resultPathString && resultPath) env->ReleaseStringUTFChars(resultPathString, resultPath);
    if (hookLibDirString && hookLibDir) env->ReleaseStringUTFChars(hookLibDirString, hookLibDir);
    if (driverBundlePathString && driverBundlePath) env->ReleaseStringUTFChars(driverBundlePathString, driverBundlePath);
    if (driverIcdPathString && driverIcdPath) env->ReleaseStringUTFChars(driverIcdPathString, driverIcdPath);
    if (driverModeString && driverMode) env->ReleaseStringUTFChars(driverModeString, driverMode);
    if (groupString && group) env->ReleaseStringUTFChars(groupString, group);
    return env->NewStringUTF(result.c_str());
}
