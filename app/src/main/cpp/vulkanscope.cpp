#define VK_NO_PROTOTYPES 1
#define VK_USE_PLATFORM_ANDROID_KHR 1
#define VK_ENABLE_BETA_EXTENSIONS 1
#include <vulkan/vulkan.h>
#include "registry_query_catalog.h"
#include "video_registry_generated.h"
#include <jni.h>
#include <android/native_window_jni.h>
#include <dlfcn.h>
#include <algorithm>
#include <array>
#include <cstdint>
#include <cctype>
#include <cstdlib>
#include <cstring>
#include <sstream>
#include <string>
#include <vector>
#include <memory>
#include <type_traits>
#include <utility>
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
constexpr std::array<int, 7> kProbeSignals{SIGSEGV, SIGABRT, SIGBUS, SIGILL, SIGFPE, SIGSYS, SIGTRAP};
std::array<struct sigaction, kProbeSignals.size()> g_previousProbeSignalActions{};
std::array<bool, kProbeSignals.size()> g_previousProbeSignalActionValid{};

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
    action.sa_flags = 0;
    for (size_t i = 0; i < kProbeSignals.size(); ++i) {
        struct sigaction previous{};
        const int result = sigaction(kProbeSignals[i], &action, &previous);
        g_previousProbeSignalActionValid[i] = result == 0;
        if (result == 0) g_previousProbeSignalActions[i] = previous;
    }
}

void clearProbeCrashGuard(const char* resultPath) {
    for (size_t i = 0; i < kProbeSignals.size(); ++i) {
        if (g_previousProbeSignalActionValid[i]) {
            (void)sigaction(kProbeSignals[i], &g_previousProbeSignalActions[i], nullptr);
            g_previousProbeSignalActionValid[i] = false;
        }
    }
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

VKAPI_ATTR void VKAPI_CALL probeNoopDestroyInstance(VkInstance, const VkAllocationCallbacks*) {}
VKAPI_ATTR void VKAPI_CALL probeNoopDestroySurface(VkInstance, VkSurfaceKHR, const VkAllocationCallbacks*) {}

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
    PFN_vkGetPhysicalDeviceCooperativeMatrixProperties2EXT getPhysicalDeviceCooperativeMatrixProperties2EXT = nullptr;
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
    bool customDriverProcess = false;

    void queryProperties2(VkPhysicalDevice device, VkPhysicalDeviceProperties2* properties);
    void queryFeatures2(VkPhysicalDevice device, VkPhysicalDeviceFeatures2* features);

    ~VulkanApi() = default;

    template <typename T>
    T load(const char* name) const { return reinterpret_cast<T>(dlsym(library, name)); }

    template <typename T>
    T loadInstance(VkInstance instance, const char* name) const { return reinterpret_cast<T>(getInstanceProcAddr(instance, name)); }

    bool open(const char* driverMode, const char* driverIcdPath, const char* driverBundlePath, const char* hookLibDir) {
        
        
        
        (void)driverIcdPath;
        (void)driverBundlePath;
        (void)hookLibDir;

        const bool wantTurnip = driverMode && std::strcmp(driverMode, "TURNIP") == 0;
        customDriverProcess = wantTurnip;

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
        const PFN_vkDestroyInstance driverDestroyInstance = loadInstance<PFN_vkDestroyInstance>(instance, "vkDestroyInstance");
        (void)driverDestroyInstance;
        destroyInstance = probeNoopDestroyInstance;
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
        getPhysicalDeviceCooperativeMatrixProperties2EXT = loadInstance<PFN_vkGetPhysicalDeviceCooperativeMatrixProperties2EXT>(instance, "vkGetPhysicalDeviceCooperativeMatrixProperties2EXT");
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
        if (!enumeratePhysicalDeviceGroups) enumeratePhysicalDeviceGroups = loadInstance<PFN_vkEnumeratePhysicalDeviceGroups>(instance, "vkEnumeratePhysicalDeviceGroupsKHR");
        enumerateDeviceExtensionProperties = loadInstance<PFN_vkEnumerateDeviceExtensionProperties>(instance, "vkEnumerateDeviceExtensionProperties");
        enumerateDeviceLayerProperties = loadInstance<PFN_vkEnumerateDeviceLayerProperties>(instance, "vkEnumerateDeviceLayerProperties");
        getPhysicalDeviceFormatProperties = loadInstance<PFN_vkGetPhysicalDeviceFormatProperties>(instance, "vkGetPhysicalDeviceFormatProperties");
        createAndroidSurfaceKHR = loadInstance<PFN_vkCreateAndroidSurfaceKHR>(instance, "vkCreateAndroidSurfaceKHR");
        const PFN_vkDestroySurfaceKHR driverDestroySurfaceKHR = loadInstance<PFN_vkDestroySurfaceKHR>(instance, "vkDestroySurfaceKHR");
        (void)driverDestroySurfaceKHR;
        destroySurfaceKHR = probeNoopDestroySurface;
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

bool apiVersionAtLeast(uint32_t value, uint32_t major, uint32_t minor) {
    const uint32_t valueMajor = VK_API_VERSION_MAJOR(value);
    const uint32_t valueMinor = VK_API_VERSION_MINOR(value);
    return valueMajor > major || (valueMajor == major && valueMinor >= minor);
}

std::string jsonString(const std::string& value) { return "\"" + escapeJson(value) + "\""; }
std::string jsonBool(bool value) { return value ? "true" : "false"; }
bool jsonContainersBalanced(const std::string& value) {
    if (value.size() < 2 || value.front() != '{' || value.back() != '}') return false;
    std::vector<char> stack;
    bool inString = false;
    bool escaped = false;
    for (char c : value) {
        if (inString) {
            if (escaped) {
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                inString = false;
            }
            continue;
        }
        if (c == '"') {
            inString = true;
            continue;
        }
        if (c == '{' || c == '[') {
            stack.push_back(c);
            continue;
        }
        if (c == '}' || c == ']') {
            if (stack.empty()) return false;
            const char open = stack.back();
            if ((c == '}' && open != '{') || (c == ']' && open != '[')) return false;
            stack.pop_back();
        }
    }
    return !inString && !escaped && stack.empty();
}
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

struct SurfaceFormatEnumeration {
    VkResult countResult = VK_SUCCESS;
    VkResult dataResult = VK_SUCCESS;
    bool countAttempted = false;
    bool dataAttempted = false;
    bool safetyRejected = false;
    bool specAnomaly = false;
    bool complete = false;
    uint32_t attemptCount = 0;
    std::string reason;
    std::vector<VkSurfaceFormatKHR> values;
};

struct SurfacePresentModeEnumeration {
    VkResult countResult = VK_SUCCESS;
    VkResult dataResult = VK_SUCCESS;
    bool countAttempted = false;
    bool dataAttempted = false;
    bool safetyRejected = false;
    bool specAnomaly = false;
    bool complete = false;
    uint32_t attemptCount = 0;
    std::string reason;
    std::vector<VkPresentModeKHR> values;
};

SurfaceFormatEnumeration enumerateSurfaceFormatsRobust(VulkanApi& api, VkPhysicalDevice device, VkSurfaceKHR surface, bool useFormats2) {
    SurfaceFormatEnumeration result;
    std::vector<VkSurfaceFormatKHR> partialValues;
    for (uint32_t attempt = 0; attempt < 4; ++attempt) {
        result.attemptCount = attempt + 1;
        uint32_t count = 0;
        VkResult countResult = VK_SUCCESS;
        if (useFormats2) {
            if (!api.getPhysicalDeviceSurfaceFormats2KHR) {
                result.reason = "vkGetPhysicalDeviceSurfaceFormats2KHR entry point is unavailable.";
                return result;
            }
            VkPhysicalDeviceSurfaceInfo2KHR info{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SURFACE_INFO_2_KHR, nullptr, surface};
            result.countAttempted = true;
            countResult = api.getPhysicalDeviceSurfaceFormats2KHR(device, &info, &count, nullptr);
        } else {
            if (!api.getPhysicalDeviceSurfaceFormatsKHR) {
                result.reason = "vkGetPhysicalDeviceSurfaceFormatsKHR entry point is unavailable.";
                return result;
            }
            result.countAttempted = true;
            countResult = api.getPhysicalDeviceSurfaceFormatsKHR(device, surface, &count, nullptr);
        }
        result.countResult = countResult;
        if (countResult != VK_SUCCESS && countResult != VK_INCOMPLETE) {
            result.values = partialValues;
            result.reason = partialValues.empty() ? "Surface-format count query failed." : "A later Surface-format count retry failed; earlier bounded partial format evidence was retained.";
            return result;
        }
        if (count > kMaxSurfaceFormatEntries) {
            result.safetyRejected = true;
            result.values = partialValues;
            result.reason = "Surface-format count exceeded the local bounded-allocation safety limit; earlier bounded partial format evidence was retained when available.";
            return result;
        }
        if (count == 0) {
            if (countResult == VK_SUCCESS) {
                result.specAnomaly = true;
                result.values.clear();
                result.reason = "The Surface-format query completed with zero entries even though Vulkan requires at least one supported surface format for a supported surface.";
                return result;
            }
            continue;
        }
        const uint32_t capacity = count;
        uint32_t returnedCount = capacity;
        result.dataAttempted = true;
        if (useFormats2) {
            std::vector<VkSurfaceFormat2KHR> values(capacity);
            for (auto& value : values) value = {VK_STRUCTURE_TYPE_SURFACE_FORMAT_2_KHR, nullptr, {VK_FORMAT_UNDEFINED, VK_COLOR_SPACE_SRGB_NONLINEAR_KHR}};
            VkPhysicalDeviceSurfaceInfo2KHR info{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SURFACE_INFO_2_KHR, nullptr, surface};
            result.dataResult = api.getPhysicalDeviceSurfaceFormats2KHR(device, &info, &returnedCount, values.data());
            if (returnedCount > capacity) {
                result.safetyRejected = true;
                result.values = partialValues;
                result.reason = "Surface-format data query returned a count larger than the bounded allocation capacity; earlier bounded partial format evidence was retained when available.";
                return result;
            }
            values.resize(returnedCount);
            result.values.clear();
            result.values.reserve(values.size());
            for (const auto& value : values) result.values.push_back(value.surfaceFormat);
        } else {
            std::vector<VkSurfaceFormatKHR> values(capacity);
            result.dataResult = api.getPhysicalDeviceSurfaceFormatsKHR(device, surface, &returnedCount, values.data());
            if (returnedCount > capacity) {
                result.safetyRejected = true;
                result.values = partialValues;
                result.reason = "Surface-format data query returned a count larger than the bounded allocation capacity; earlier bounded partial format evidence was retained when available.";
                return result;
            }
            values.resize(returnedCount);
            result.values = std::move(values);
        }
        if (result.dataResult == VK_SUCCESS) {
            if (result.values.empty()) {
                result.specAnomaly = true;
                result.reason = "The Surface-format data query completed with zero entries even though Vulkan requires at least one supported surface format for a supported surface.";
                return result;
            }
            result.complete = true;
            result.reason.clear();
            return result;
        }
        if (result.dataResult != VK_INCOMPLETE) {
            result.values = partialValues;
            result.reason = partialValues.empty() ? "Surface-format data query failed." : "A later Surface-format data retry failed; earlier bounded partial format evidence was retained.";
            return result;
        }
        if (!result.values.empty()) partialValues = result.values;
    }
    result.values = partialValues;
    result.reason = "Surface-format enumeration remained VK_INCOMPLETE after four bounded attempts; returned entries are retained as partial positive evidence.";
    return result;
}

SurfacePresentModeEnumeration enumerateSurfacePresentModesRobust(VulkanApi& api, VkPhysicalDevice device, VkSurfaceKHR surface) {
    SurfacePresentModeEnumeration result;
    std::vector<VkPresentModeKHR> partialValues;
    if (!api.getPhysicalDeviceSurfacePresentModesKHR) {
        result.reason = "vkGetPhysicalDeviceSurfacePresentModesKHR entry point is unavailable.";
        return result;
    }
    for (uint32_t attempt = 0; attempt < 4; ++attempt) {
        result.attemptCount = attempt + 1;
        uint32_t count = 0;
        result.countAttempted = true;
        const VkResult countResult = api.getPhysicalDeviceSurfacePresentModesKHR(device, surface, &count, nullptr);
        result.countResult = countResult;
        if (countResult != VK_SUCCESS && countResult != VK_INCOMPLETE) {
            result.values = partialValues;
            result.reason = partialValues.empty() ? "Present-mode count query failed." : "A later present-mode count retry failed; earlier bounded partial mode evidence was retained.";
            return result;
        }
        if (count > kMaxPresentModeEntries) {
            result.safetyRejected = true;
            result.values = partialValues;
            result.reason = "Present-mode count exceeded the local bounded-allocation safety limit; earlier bounded partial mode evidence was retained when available.";
            return result;
        }
        if (count == 0) {
            if (countResult == VK_SUCCESS) {
                result.specAnomaly = true;
                result.values.clear();
                result.reason = "The present-mode query completed with zero entries even though VK_PRESENT_MODE_FIFO_KHR is required to be supported.";
                return result;
            }
            continue;
        }
        const uint32_t capacity = count;
        uint32_t returnedCount = capacity;
        std::vector<VkPresentModeKHR> values(capacity);
        result.dataAttempted = true;
        result.dataResult = api.getPhysicalDeviceSurfacePresentModesKHR(device, surface, &returnedCount, values.data());
        if (returnedCount > capacity) {
            result.safetyRejected = true;
            result.values = partialValues;
            result.reason = "Present-mode data query returned a count larger than the bounded allocation capacity; earlier bounded partial mode evidence was retained when available.";
            return result;
        }
        values.resize(returnedCount);
        result.values = std::move(values);
        if (result.dataResult == VK_SUCCESS) {
            const bool fifoPresent = std::find(result.values.begin(), result.values.end(), VK_PRESENT_MODE_FIFO_KHR) != result.values.end();
            if (!fifoPresent) {
                result.specAnomaly = true;
                result.reason = "The completed present-mode enumeration omitted VK_PRESENT_MODE_FIFO_KHR, which Vulkan requires to be supported.";
                return result;
            }
            result.complete = true;
            result.reason.clear();
            return result;
        }
        if (result.dataResult != VK_INCOMPLETE) {
            result.values = partialValues;
            result.reason = partialValues.empty() ? "Present-mode data query failed." : "A later present-mode data retry failed; earlier bounded partial mode evidence was retained.";
            return result;
        }
        if (!result.values.empty()) partialValues = result.values;
    }
    result.values = partialValues;
    result.reason = "Present-mode enumeration remained VK_INCOMPLETE after four bounded attempts; returned entries are retained as partial positive evidence.";
    return result;
}

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
    return "UNKNOWN(" + std::to_string(value) + ")";
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
    if (value >= 1000156000 && value <= 1000156033) return apiVersionAtLeast(apiVersion, 1, 1) || has("VK_KHR_sampler_ycbcr_conversion");
    if (value >= 1000288000 && value <= 1000288029) return has("VK_EXT_texture_compression_astc_3d");
    if (value >= 1000330000 && value <= 1000330003) return has("VK_EXT_ycbcr_2plane_444_formats");
    if (value >= 1000340000 && value <= 1000340001) return has("VK_EXT_4444_formats");
    if (value == 1000460000) return has("VK_ARM_tensors");
    if (value == 1000460001) return has("VK_ARM_tensors") && has("VK_KHR_shader_bfloat16");
    if (value == 1000460002 || value == 1000460003) return has("VK_ARM_tensors") && has("VK_EXT_shader_float8");
    if (value == 1000464000) return has("VK_NV_optical_flow");
    if (value >= 1000609000 && value <= 1000609013) return has("VK_ARM_format_pack");
    if (value >= 1000470000 && value <= 1000470001) return apiVersionAtLeast(apiVersion, 1, 4) || has("VK_KHR_maintenance5");
    return true;
}

std::string presentModeName(VkPresentModeKHR value) {
    switch (value) {
        case VK_PRESENT_MODE_IMMEDIATE_KHR: return "VK_PRESENT_MODE_IMMEDIATE_KHR";
        case VK_PRESENT_MODE_MAILBOX_KHR: return "VK_PRESENT_MODE_MAILBOX_KHR";
        case VK_PRESENT_MODE_FIFO_KHR: return "VK_PRESENT_MODE_FIFO_KHR";
        case VK_PRESENT_MODE_FIFO_RELAXED_KHR: return "VK_PRESENT_MODE_FIFO_RELAXED_KHR";
        case VK_PRESENT_MODE_SHARED_DEMAND_REFRESH_KHR: return "VK_PRESENT_MODE_SHARED_DEMAND_REFRESH_KHR";
        case VK_PRESENT_MODE_SHARED_CONTINUOUS_REFRESH_KHR: return "VK_PRESENT_MODE_SHARED_CONTINUOUS_REFRESH_KHR";
        case VK_PRESENT_MODE_FIFO_LATEST_READY_KHR: return "VK_PRESENT_MODE_FIFO_LATEST_READY_KHR";
        default: return "UNKNOWN(" + std::to_string(static_cast<int32_t>(value)) + ")";
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
        default: return "UNKNOWN(" + std::to_string(value) + ")";
    }
}


std::string colorSpaceDescription(int32_t value) {
    switch (value) {
        case VK_COLOR_SPACE_SRGB_NONLINEAR_KHR: return "BT.709 primaries · D65 · sRGB transfer";
        case VK_COLOR_SPACE_DISPLAY_P3_NONLINEAR_EXT: return "Display-P3 primaries · D65 · Display-P3 transfer";
        case VK_COLOR_SPACE_EXTENDED_SRGB_LINEAR_EXT: return "sRGB primaries · D65 · linear transfer";
        case VK_COLOR_SPACE_DISPLAY_P3_LINEAR_EXT: return "Display-P3 primaries · D65 · linear transfer";
        case VK_COLOR_SPACE_DCI_P3_NONLINEAR_EXT: return "DCI-P3 color space · DCI white point · DCI-P3 transfer · presentation engine interprets components as XYZ";
        case VK_COLOR_SPACE_BT709_LINEAR_EXT: return "BT.709 primaries · D65 · linear transfer";
        case VK_COLOR_SPACE_BT709_NONLINEAR_EXT: return "BT.709 primaries · D65 · BT.709 transfer";
        case VK_COLOR_SPACE_BT2020_LINEAR_EXT: return "BT.2020 primaries · D65 · linear transfer";
        case VK_COLOR_SPACE_HDR10_ST2084_EXT: return "BT.2020 primaries · D65 · ST2084 PQ";
        case VK_COLOR_SPACE_DOLBYVISION_EXT: return "Legacy Vulkan Dolby Vision color-space enum · does not signal Dolby Vision metadata";
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
        case VK_COLOR_SPACE_DOLBYVISION_EXT: return "Legacy Dolby Vision enum";
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
    return index < sizeof(names) / sizeof(names[0]) ? names[index] : "UNKNOWN(raw-index=" + std::to_string(index) + ")";
}

bool hasExtension(const std::vector<VkExtensionProperties>& values, const char* name);

struct InstanceExtensionEnumeration {
    std::vector<VkExtensionProperties> values;
    std::string status;
    std::string reason;
    bool complete;
};

struct InstanceLayerEnumeration {
    std::vector<VkLayerProperties> values;
    std::string status;
    std::string reason;
    bool complete;
};

struct LayerExtensionEnumeration {
    std::vector<VkExtensionProperties> values;
    std::string status;
    std::string reason;
    bool complete;
};

struct DeviceLayerEnumeration {
    std::vector<VkLayerProperties> values;
    std::string status;
    std::string reason;
    bool complete;
};

InstanceExtensionEnumeration enumerateInstanceExtensions(VulkanApi& api) {
    if (!api.enumerateInstanceExtensionProperties) return {{}, "unavailable", "vkEnumerateInstanceExtensionProperties is unavailable.", false};
    std::vector<VkExtensionProperties> partialValues;
    for (uint32_t attempt = 0; attempt < 4; ++attempt) {
        uint32_t count = 0;
        const VkResult countResult = api.enumerateInstanceExtensionProperties(nullptr, &count, nullptr);
        if (countResult != VK_SUCCESS && countResult != VK_INCOMPLETE) return partialValues.empty() ? InstanceExtensionEnumeration{{}, "unavailable", std::string("Instance-extension count query failed. VkResult=") + std::to_string(countResult), false} : InstanceExtensionEnumeration{std::move(partialValues), "incomplete", std::string("A later instance-extension count retry failed; earlier bounded partial extension evidence was retained. VkResult=") + std::to_string(countResult), false};
        if (count > kMaxExtensionEntries) return partialValues.empty() ? InstanceExtensionEnumeration{{}, "unavailable", "Instance-extension count exceeds the safety limit.", false} : InstanceExtensionEnumeration{std::move(partialValues), "incomplete", "A later instance-extension count retry exceeded the safety limit; earlier bounded partial extension evidence was retained.", false};
        if (count == 0 && countResult == VK_SUCCESS) return {{}, "available", "", true};
        std::vector<VkExtensionProperties> values(count);
        const size_t capacity = values.size();
        const VkResult dataResult = count ? api.enumerateInstanceExtensionProperties(nullptr, &count, values.data()) : VK_INCOMPLETE;
        if (count > capacity) return partialValues.empty() ? InstanceExtensionEnumeration{{}, "unavailable", "Instance-extension enumeration returned a count larger than the allocated capacity.", false} : InstanceExtensionEnumeration{std::move(partialValues), "incomplete", "A later instance-extension data retry exceeded the bounded allocation capacity; earlier partial extension evidence was retained.", false};
        values.resize(count);
        if (dataResult == VK_SUCCESS) return {std::move(values), "available", "", true};
        if (dataResult != VK_INCOMPLETE) return partialValues.empty() ? InstanceExtensionEnumeration{{}, "unavailable", std::string("Instance-extension data query failed. VkResult=") + std::to_string(dataResult), false} : InstanceExtensionEnumeration{std::move(partialValues), "incomplete", std::string("A later instance-extension data retry failed; earlier bounded partial extension evidence was retained. VkResult=") + std::to_string(dataResult), false};
        if (!values.empty()) partialValues = std::move(values);
    }
    return {std::move(partialValues), "incomplete", "Instance-extension enumeration remained VK_INCOMPLETE after four bounded attempts; returned entries are retained as partial positive evidence only.", false};
}

InstanceLayerEnumeration enumerateInstanceLayers(VulkanApi& api) {
    if (!api.enumerateInstanceLayerProperties) return {{}, "unavailable", "vkEnumerateInstanceLayerProperties is unavailable.", false};
    std::vector<VkLayerProperties> partialValues;
    for (uint32_t attempt = 0; attempt < 4; ++attempt) {
        uint32_t count = 0;
        const VkResult countResult = api.enumerateInstanceLayerProperties(&count, nullptr);
        if (countResult != VK_SUCCESS && countResult != VK_INCOMPLETE) return partialValues.empty() ? InstanceLayerEnumeration{{}, "unavailable", std::string("Instance-layer count query failed. VkResult=") + std::to_string(countResult), false} : InstanceLayerEnumeration{std::move(partialValues), "incomplete", std::string("A later instance-layer count retry failed; earlier bounded partial layer evidence was retained. VkResult=") + std::to_string(countResult), false};
        if (count > kMaxLayerEntries) return partialValues.empty() ? InstanceLayerEnumeration{{}, "unavailable", "Instance-layer count exceeds the safety limit.", false} : InstanceLayerEnumeration{std::move(partialValues), "incomplete", "A later instance-layer count retry exceeded the safety limit; earlier bounded partial layer evidence was retained.", false};
        if (count == 0 && countResult == VK_SUCCESS) return {{}, "available", "", true};
        std::vector<VkLayerProperties> values(count);
        const size_t capacity = values.size();
        const VkResult dataResult = count ? api.enumerateInstanceLayerProperties(&count, values.data()) : VK_INCOMPLETE;
        if (count > capacity) return partialValues.empty() ? InstanceLayerEnumeration{{}, "unavailable", "Instance-layer enumeration returned a count larger than the allocated capacity.", false} : InstanceLayerEnumeration{std::move(partialValues), "incomplete", "A later instance-layer data retry exceeded the bounded allocation capacity; earlier partial layer evidence was retained.", false};
        values.resize(count);
        if (dataResult == VK_SUCCESS) return {std::move(values), "available", "", true};
        if (dataResult != VK_INCOMPLETE) return partialValues.empty() ? InstanceLayerEnumeration{{}, "unavailable", std::string("Instance-layer data query failed. VkResult=") + std::to_string(dataResult), false} : InstanceLayerEnumeration{std::move(partialValues), "incomplete", std::string("A later instance-layer data retry failed; earlier bounded partial layer evidence was retained. VkResult=") + std::to_string(dataResult), false};
        if (!values.empty()) partialValues = std::move(values);
    }
    return {std::move(partialValues), "incomplete", "Instance-layer enumeration remained VK_INCOMPLETE after four bounded attempts; returned entries are retained as partial positive evidence only.", false};
}

LayerExtensionEnumeration enumerateInstanceLayerExtensions(VulkanApi& api, const char* layerName) {
    if (!api.enumerateInstanceExtensionProperties) return {{}, "unavailable", "vkEnumerateInstanceExtensionProperties is unavailable for layer extension enumeration.", false};
    std::vector<VkExtensionProperties> partialValues;
    for (uint32_t attempt = 0; attempt < 4; ++attempt) {
        uint32_t count = 0;
        const VkResult countResult = api.enumerateInstanceExtensionProperties(layerName, &count, nullptr);
        if (countResult != VK_SUCCESS && countResult != VK_INCOMPLETE) return partialValues.empty() ? LayerExtensionEnumeration{{}, "unavailable", std::string("Instance-layer extension count query failed. VkResult=") + std::to_string(countResult), false} : LayerExtensionEnumeration{std::move(partialValues), "incomplete", std::string("A later instance-layer extension count retry failed; earlier bounded partial extension evidence was retained. VkResult=") + std::to_string(countResult), false};
        if (count > kMaxExtensionEntries) return partialValues.empty() ? LayerExtensionEnumeration{{}, "unavailable", "Instance-layer extension count exceeds the safety limit.", false} : LayerExtensionEnumeration{std::move(partialValues), "incomplete", "A later instance-layer extension count retry exceeded the safety limit; earlier bounded partial extension evidence was retained.", false};
        if (count == 0 && countResult == VK_SUCCESS) return {{}, "available", "", true};
        std::vector<VkExtensionProperties> values(count);
        const size_t capacity = values.size();
        const VkResult dataResult = count ? api.enumerateInstanceExtensionProperties(layerName, &count, values.data()) : VK_INCOMPLETE;
        if (count > capacity) return partialValues.empty() ? LayerExtensionEnumeration{{}, "unavailable", "Instance-layer extension enumeration returned a count larger than the allocated capacity.", false} : LayerExtensionEnumeration{std::move(partialValues), "incomplete", "A later instance-layer extension data retry exceeded the bounded allocation capacity; earlier partial extension evidence was retained.", false};
        values.resize(count);
        if (dataResult == VK_SUCCESS) return {std::move(values), "available", "", true};
        if (dataResult != VK_INCOMPLETE) return partialValues.empty() ? LayerExtensionEnumeration{{}, "unavailable", std::string("Instance-layer extension data query failed. VkResult=") + std::to_string(dataResult), false} : LayerExtensionEnumeration{std::move(partialValues), "incomplete", std::string("A later instance-layer extension data retry failed; earlier bounded partial extension evidence was retained. VkResult=") + std::to_string(dataResult), false};
        if (!values.empty()) partialValues = std::move(values);
    }
    return {std::move(partialValues), "incomplete", "Instance-layer extension enumeration remained VK_INCOMPLETE after four bounded attempts; returned entries are retained as partial positive evidence only.", false};
}

DeviceLayerEnumeration enumerateDeviceLayers(VulkanApi& api, VkPhysicalDevice device) {
    if (!api.enumerateDeviceLayerProperties) return {{}, "unavailable", "vkEnumerateDeviceLayerProperties is unavailable.", false};
    std::vector<VkLayerProperties> partialValues;
    for (uint32_t attempt = 0; attempt < 4; ++attempt) {
        uint32_t count = 0;
        const VkResult countResult = api.enumerateDeviceLayerProperties(device, &count, nullptr);
        if (countResult != VK_SUCCESS && countResult != VK_INCOMPLETE) return partialValues.empty() ? DeviceLayerEnumeration{{}, "unavailable", std::string("Device-layer count query failed. VkResult=") + std::to_string(countResult), false} : DeviceLayerEnumeration{std::move(partialValues), "incomplete", std::string("A later device-layer count retry failed; earlier bounded partial layer evidence was retained. VkResult=") + std::to_string(countResult), false};
        if (count > kMaxLayerEntries) return partialValues.empty() ? DeviceLayerEnumeration{{}, "unavailable", "Device-layer count exceeds the safety limit.", false} : DeviceLayerEnumeration{std::move(partialValues), "incomplete", "A later device-layer count retry exceeded the safety limit; earlier bounded partial layer evidence was retained.", false};
        if (count == 0 && countResult == VK_SUCCESS) return {{}, "available", "", true};
        std::vector<VkLayerProperties> values(count);
        const size_t capacity = values.size();
        const VkResult dataResult = count ? api.enumerateDeviceLayerProperties(device, &count, values.data()) : VK_INCOMPLETE;
        if (count > capacity) return partialValues.empty() ? DeviceLayerEnumeration{{}, "unavailable", "Device-layer enumeration returned a count larger than the allocated capacity.", false} : DeviceLayerEnumeration{std::move(partialValues), "incomplete", "A later device-layer data retry exceeded the bounded allocation capacity; earlier partial layer evidence was retained.", false};
        values.resize(count);
        if (dataResult == VK_SUCCESS) return {std::move(values), "available", "", true};
        if (dataResult != VK_INCOMPLETE) return partialValues.empty() ? DeviceLayerEnumeration{{}, "unavailable", std::string("Device-layer data query failed. VkResult=") + std::to_string(dataResult), false} : DeviceLayerEnumeration{std::move(partialValues), "incomplete", std::string("A later device-layer data retry failed; earlier bounded partial layer evidence was retained. VkResult=") + std::to_string(dataResult), false};
        if (!values.empty()) partialValues = std::move(values);
    }
    return {std::move(partialValues), "incomplete", "Device-layer enumeration remained VK_INCOMPLETE after four bounded attempts; returned entries are retained as partial positive evidence only.", false};
}

LayerExtensionEnumeration enumerateDeviceLayerExtensions(VulkanApi& api, VkPhysicalDevice device, const char* layerName) {
    if (!api.enumerateDeviceExtensionProperties) return {{}, "unavailable", "vkEnumerateDeviceExtensionProperties is unavailable for device-layer extension enumeration.", false};
    std::vector<VkExtensionProperties> partialValues;
    for (uint32_t attempt = 0; attempt < 4; ++attempt) {
        uint32_t count = 0;
        const VkResult countResult = api.enumerateDeviceExtensionProperties(device, layerName, &count, nullptr);
        if (countResult != VK_SUCCESS && countResult != VK_INCOMPLETE) return partialValues.empty() ? LayerExtensionEnumeration{{}, "unavailable", std::string("Device-layer extension count query failed. VkResult=") + std::to_string(countResult), false} : LayerExtensionEnumeration{std::move(partialValues), "incomplete", std::string("A later device-layer extension count retry failed; earlier bounded partial extension evidence was retained. VkResult=") + std::to_string(countResult), false};
        if (count > kMaxExtensionEntries) return partialValues.empty() ? LayerExtensionEnumeration{{}, "unavailable", "Device-layer extension count exceeds the safety limit.", false} : LayerExtensionEnumeration{std::move(partialValues), "incomplete", "A later device-layer extension count retry exceeded the safety limit; earlier bounded partial extension evidence was retained.", false};
        if (count == 0 && countResult == VK_SUCCESS) return {{}, "available", "", true};
        std::vector<VkExtensionProperties> values(count);
        const size_t capacity = values.size();
        const VkResult dataResult = count ? api.enumerateDeviceExtensionProperties(device, layerName, &count, values.data()) : VK_INCOMPLETE;
        if (count > capacity) return partialValues.empty() ? LayerExtensionEnumeration{{}, "unavailable", "Device-layer extension enumeration returned a count larger than the allocated capacity.", false} : LayerExtensionEnumeration{std::move(partialValues), "incomplete", "A later device-layer extension data retry exceeded the bounded allocation capacity; earlier partial extension evidence was retained.", false};
        values.resize(count);
        if (dataResult == VK_SUCCESS) return {std::move(values), "available", "", true};
        if (dataResult != VK_INCOMPLETE) return partialValues.empty() ? LayerExtensionEnumeration{{}, "unavailable", std::string("Device-layer extension data query failed. VkResult=") + std::to_string(dataResult), false} : LayerExtensionEnumeration{std::move(partialValues), "incomplete", std::string("A later device-layer extension data retry failed; earlier bounded partial extension evidence was retained. VkResult=") + std::to_string(dataResult), false};
        if (!values.empty()) partialValues = std::move(values);
    }
    return {std::move(partialValues), "incomplete", "Device-layer extension enumeration remained VK_INCOMPLETE after four bounded attempts; returned entries are retained as partial positive evidence only.", false};
}

std::string layersJson(VulkanApi& api, const std::vector<VkLayerProperties>& values) {
    std::ostringstream out;
    out << '[';
    for (size_t i = 0; i < values.size(); ++i) {
        if (i) out << ',';
        const auto extensionEnumeration = enumerateInstanceLayerExtensions(api, values[i].layerName);
        out << "{\"name\":" << jsonString(values[i].layerName)
            << ",\"description\":" << jsonString(values[i].description)
            << ",\"specVersion\":" << values[i].specVersion
            << ",\"implementationVersion\":" << values[i].implementationVersion
            << ",\"extensionStatus\":" << jsonString(extensionEnumeration.status)
            << ",\"extensionReason\":" << jsonString(extensionEnumeration.reason)
            << ",\"extensionsComplete\":" << jsonBool(extensionEnumeration.complete)
            << ",\"extensions\":[";
        for (size_t j = 0; j < extensionEnumeration.values.size(); ++j) {
            if (j) out << ',';
            out << "{\"name\":" << jsonString(extensionEnumeration.values[j].extensionName) << ",\"specVersion\":" << extensionEnumeration.values[j].specVersion << '}';
        }
        out << "]}";
    }
    out << ']';
    return out.str();
}

std::string deviceLayersJson(VulkanApi& api, VkPhysicalDevice device, const std::vector<VkLayerProperties>& layers) {
    std::ostringstream out;
    out << '[';
    for (size_t i = 0; i < layers.size(); ++i) {
        if (i) out << ',';
        const auto extensionEnumeration = enumerateDeviceLayerExtensions(api, device, layers[i].layerName);
        out << "{\"name\":" << jsonString(layers[i].layerName)
            << ",\"description\":" << jsonString(layers[i].description)
            << ",\"specVersion\":" << layers[i].specVersion
            << ",\"implementationVersion\":" << layers[i].implementationVersion
            << ",\"extensionStatus\":" << jsonString(extensionEnumeration.status)
            << ",\"extensionReason\":" << jsonString(extensionEnumeration.reason)
            << ",\"extensionsComplete\":" << jsonBool(extensionEnumeration.complete)
            << ",\"extensions\":[";
        for (size_t j = 0; j < extensionEnumeration.values.size(); ++j) {
            if (j) out << ',';
            out << "{\"name\":" << jsonString(extensionEnumeration.values[j].extensionName) << ",\"specVersion\":" << extensionEnumeration.values[j].specVersion << '}';
        }
        out << "]}";
    }
    out << ']';
    return out.str();
}

struct DeviceExtensionEnumeration {
    std::vector<VkExtensionProperties> values;
    const char* status;
    std::string reason;
};

DeviceExtensionEnumeration enumerateDeviceExtensions(VulkanApi& api, VkPhysicalDevice device) {
    if (!api.enumerateDeviceExtensionProperties) {
        return {{}, "unavailable", "vkEnumerateDeviceExtensionProperties is unavailable in the active Vulkan stack."};
    }
    std::vector<VkExtensionProperties> partialValues;
    for (uint32_t attempt = 0; attempt < 4; ++attempt) {
        uint32_t count = 0;
        const VkResult countResult = api.enumerateDeviceExtensionProperties(device, nullptr, &count, nullptr);
        if (countResult != VK_SUCCESS && countResult != VK_INCOMPLETE) {
            if (!partialValues.empty()) return {std::move(partialValues), "incomplete", std::string("A later device-extension count retry failed; earlier bounded partial extension evidence was retained. VkResult=") + std::to_string(countResult)};
            return {{}, "unavailable", std::string("Count query failed. VkResult=") + std::to_string(countResult)};
        }
        if (count > kMaxExtensionEntries) {
            if (!partialValues.empty()) return {std::move(partialValues), "incomplete", "A later device-extension count retry exceeded the safety limit; earlier bounded partial extension evidence was retained."};
            return {{}, "unavailable", "The reported device-extension count exceeds the safety limit."};
        }
        if (count == 0) {
            if (countResult == VK_SUCCESS) return {{}, "available", "The Vulkan implementation reported zero device extensions."};
            continue;
        }
        const uint32_t capacity = count;
        std::vector<VkExtensionProperties> values(capacity);
        uint32_t returnedCount = capacity;
        const VkResult dataResult = api.enumerateDeviceExtensionProperties(device, nullptr, &returnedCount, values.data());
        if (returnedCount > capacity) {
            if (!partialValues.empty()) return {std::move(partialValues), "incomplete", "A later device-extension data retry exceeded the bounded allocation capacity; earlier partial extension evidence was retained."};
            return {{}, "unavailable", "The device-extension data query returned a count larger than the bounded allocation."};
        }
        values.resize(returnedCount);
        if (dataResult == VK_SUCCESS) {
            return {std::move(values), "available", ""};
        }
        if (dataResult != VK_INCOMPLETE) {
            if (!partialValues.empty()) return {std::move(partialValues), "incomplete", std::string("A later device-extension data retry failed; earlier bounded partial extension evidence was retained. VkResult=") + std::to_string(dataResult)};
            return {{}, "unavailable", std::string("Data query failed. VkResult=") + std::to_string(dataResult)};
        }
        if (!values.empty()) partialValues = std::move(values);
    }
    return {std::move(partialValues), "incomplete", "Device-extension enumeration remained VK_INCOMPLETE after four bounded attempts; returned entries are retained as partial positive evidence only."};
}

const std::vector<VkExtensionProperties>& deviceExtensions(const DeviceExtensionEnumeration& enumeration) {
    return enumeration.values;
}

bool hasExtension(const std::vector<VkExtensionProperties>& values, const char* name) {
    return std::any_of(values.begin(), values.end(), [name](const VkExtensionProperties& value) { return std::strcmp(value.extensionName, name) == 0; });
}

struct PhysicalDeviceEnumeration {
    VkResult result = VK_SUCCESS;
    bool resultAvailable = false;
    bool safetyRejected = false;
    bool complete = false;
    std::string localReason;
    std::vector<VkPhysicalDevice> values;
};

PhysicalDeviceEnumeration enumeratePhysicalDevicesRobust(VulkanApi& api, VkInstance instance) {
    PhysicalDeviceEnumeration result;
    if (!api.enumeratePhysicalDevices) {
        result.localReason = "vkEnumeratePhysicalDevices entry point is unavailable.";
        return result;
    }
    std::vector<VkPhysicalDevice> partialDevices;
    for (uint32_t attempt = 0; attempt < 4; ++attempt) {
        uint32_t count = 0;
        const VkResult countResult = api.enumeratePhysicalDevices(instance, &count, nullptr);
        result.result = countResult;
        result.resultAvailable = true;
        if (countResult != VK_SUCCESS && countResult != VK_INCOMPLETE) {
            result.values = partialDevices;
            if (!partialDevices.empty()) result.localReason = "A later physical-device count retry failed; earlier bounded partial device evidence was retained.";
            return result;
        }
        if (count > kMaxPhysicalDeviceEntries) {
            result.safetyRejected = true;
            result.localReason = "Physical-device count exceeded the local bounded-allocation safety limit.";
            result.values = partialDevices;
            return result;
        }
        if (count == 0) {
            if (countResult == VK_SUCCESS) {
                result.complete = true;
                result.values.clear();
                return result;
            }
            continue;
        }
        const uint32_t capacity = count;
        std::vector<VkPhysicalDevice> devices(capacity);
        uint32_t returnedCount = capacity;
        const VkResult dataResult = api.enumeratePhysicalDevices(instance, &returnedCount, devices.data());
        result.result = dataResult;
        result.resultAvailable = true;
        if (returnedCount > capacity) {
            result.safetyRejected = true;
            result.localReason = "Physical-device data query returned a count larger than the bounded allocation capacity.";
            result.values = partialDevices;
            return result;
        }
        devices.resize(returnedCount);
        if (dataResult == VK_SUCCESS) {
            result.complete = true;
            result.values = std::move(devices);
            return result;
        }
        if (dataResult != VK_INCOMPLETE) {
            result.values = partialDevices;
            if (!partialDevices.empty()) result.localReason = "A later physical-device data retry failed; earlier bounded partial device evidence was retained.";
            return result;
        }
        if (!devices.empty()) partialDevices = std::move(devices);
    }
    result.result = VK_INCOMPLETE;
    result.resultAvailable = true;
    result.complete = false;
    result.values = std::move(partialDevices);
    return result;
}

std::vector<const char*> buildQueryInstanceExtensions(VulkanApi& api, const std::vector<VkExtensionProperties>* available = nullptr) {
    InstanceExtensionEnumeration ownedEnumeration;
    const std::vector<VkExtensionProperties>* extensions = available;
    if (!extensions) {
        ownedEnumeration = enumerateInstanceExtensions(api);
        extensions = &ownedEnumeration.values;
    }
    std::vector<const char*> result;
    const char* queryExtensions[] = {
        "VK_KHR_get_physical_device_properties2",
        "VK_KHR_device_group_creation",
        "VK_KHR_external_memory_capabilities",
        "VK_KHR_external_fence_capabilities",
        "VK_KHR_external_semaphore_capabilities",
    };
    for (const char* extension : queryExtensions) {
        if (hasExtension(*extensions, extension)) result.push_back(extension);
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
    if (!api.getPhysicalDeviceFeatures2 || targetMinor < 1 || targetMinor > 4 || !apiVersionAtLeast(apiVersion, 1, targetMinor)) {
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
static bool generatedSectionIsFeatureStruct(const char* section) {
    return section != nullptr && std::strstr(section, "Features") != nullptr;
}

void generatedEmitBool(std::vector<GeneratedField>& dst, const char* section, const char* name, VkBool32 value) {
    const bool featureStruct = generatedSectionIsFeatureStruct(section);
    const std::string fieldName = featureStruct ? (std::string(section) + " · " + name) : std::string(name);
    dst.push_back({featureStruct, section ? section : "", fieldName, value == VK_TRUE ? "true" : "false"});
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


static const char* canonicalImageLayoutName(VkImageLayout value) {
    if (value == VK_IMAGE_LAYOUT_UNDEFINED) return "VK_IMAGE_LAYOUT_UNDEFINED";
    if (value == VK_IMAGE_LAYOUT_GENERAL) return "VK_IMAGE_LAYOUT_GENERAL";
    if (value == VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL) return "VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL";
    if (value == VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL) return "VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL";
    if (value == VK_IMAGE_LAYOUT_DEPTH_STENCIL_READ_ONLY_OPTIMAL) return "VK_IMAGE_LAYOUT_DEPTH_STENCIL_READ_ONLY_OPTIMAL";
    if (value == VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL) return "VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL";
    if (value == VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL) return "VK_IMAGE_LAYOUT_TRANSFER_SRC_OPTIMAL";
    if (value == VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL) return "VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL";
    if (value == VK_IMAGE_LAYOUT_PREINITIALIZED) return "VK_IMAGE_LAYOUT_PREINITIALIZED";
    if (value == VK_IMAGE_LAYOUT_DEPTH_READ_ONLY_STENCIL_ATTACHMENT_OPTIMAL) return "VK_IMAGE_LAYOUT_DEPTH_READ_ONLY_STENCIL_ATTACHMENT_OPTIMAL";
    if (value == VK_IMAGE_LAYOUT_DEPTH_ATTACHMENT_STENCIL_READ_ONLY_OPTIMAL) return "VK_IMAGE_LAYOUT_DEPTH_ATTACHMENT_STENCIL_READ_ONLY_OPTIMAL";
    if (value == VK_IMAGE_LAYOUT_DEPTH_ATTACHMENT_OPTIMAL) return "VK_IMAGE_LAYOUT_DEPTH_ATTACHMENT_OPTIMAL";
    if (value == VK_IMAGE_LAYOUT_DEPTH_READ_ONLY_OPTIMAL) return "VK_IMAGE_LAYOUT_DEPTH_READ_ONLY_OPTIMAL";
    if (value == VK_IMAGE_LAYOUT_STENCIL_ATTACHMENT_OPTIMAL) return "VK_IMAGE_LAYOUT_STENCIL_ATTACHMENT_OPTIMAL";
    if (value == VK_IMAGE_LAYOUT_STENCIL_READ_ONLY_OPTIMAL) return "VK_IMAGE_LAYOUT_STENCIL_READ_ONLY_OPTIMAL";
    if (value == VK_IMAGE_LAYOUT_READ_ONLY_OPTIMAL) return "VK_IMAGE_LAYOUT_READ_ONLY_OPTIMAL";
    if (value == VK_IMAGE_LAYOUT_ATTACHMENT_OPTIMAL) return "VK_IMAGE_LAYOUT_ATTACHMENT_OPTIMAL";
    if (value == VK_IMAGE_LAYOUT_RENDERING_LOCAL_READ) return "VK_IMAGE_LAYOUT_RENDERING_LOCAL_READ";
    if (value == VK_IMAGE_LAYOUT_PRESENT_SRC_KHR) return "VK_IMAGE_LAYOUT_PRESENT_SRC_KHR";
    if (value == VK_IMAGE_LAYOUT_SHARED_PRESENT_KHR) return "VK_IMAGE_LAYOUT_SHARED_PRESENT_KHR";
    if (value == VK_IMAGE_LAYOUT_VIDEO_DECODE_DST_KHR) return "VK_IMAGE_LAYOUT_VIDEO_DECODE_DST_KHR";
    if (value == VK_IMAGE_LAYOUT_VIDEO_DECODE_SRC_KHR) return "VK_IMAGE_LAYOUT_VIDEO_DECODE_SRC_KHR";
    if (value == VK_IMAGE_LAYOUT_VIDEO_DECODE_DPB_KHR) return "VK_IMAGE_LAYOUT_VIDEO_DECODE_DPB_KHR";
    if (value == VK_IMAGE_LAYOUT_VIDEO_ENCODE_DST_KHR) return "VK_IMAGE_LAYOUT_VIDEO_ENCODE_DST_KHR";
    if (value == VK_IMAGE_LAYOUT_VIDEO_ENCODE_SRC_KHR) return "VK_IMAGE_LAYOUT_VIDEO_ENCODE_SRC_KHR";
    if (value == VK_IMAGE_LAYOUT_VIDEO_ENCODE_DPB_KHR) return "VK_IMAGE_LAYOUT_VIDEO_ENCODE_DPB_KHR";
    if (value == VK_IMAGE_LAYOUT_VIDEO_ENCODE_QUANTIZATION_MAP_KHR) return "VK_IMAGE_LAYOUT_VIDEO_ENCODE_QUANTIZATION_MAP_KHR";
    if (value == VK_IMAGE_LAYOUT_FRAGMENT_DENSITY_MAP_OPTIMAL_EXT) return "VK_IMAGE_LAYOUT_FRAGMENT_DENSITY_MAP_OPTIMAL_EXT";
    if (value == VK_IMAGE_LAYOUT_FRAGMENT_SHADING_RATE_ATTACHMENT_OPTIMAL_KHR) return "VK_IMAGE_LAYOUT_FRAGMENT_SHADING_RATE_ATTACHMENT_OPTIMAL_KHR";
    if (value == VK_IMAGE_LAYOUT_ATTACHMENT_FEEDBACK_LOOP_OPTIMAL_EXT) return "VK_IMAGE_LAYOUT_ATTACHMENT_FEEDBACK_LOOP_OPTIMAL_EXT";
    if (value == VK_IMAGE_LAYOUT_TENSOR_ALIASING_ARM) return "VK_IMAGE_LAYOUT_TENSOR_ALIASING_ARM";
    if (value == VK_IMAGE_LAYOUT_ZERO_INITIALIZED_EXT) return "VK_IMAGE_LAYOUT_ZERO_INITIALIZED_EXT";
    return nullptr;
}

static std::string imageLayoutValueString(VkImageLayout value) {
    const char* name = canonicalImageLayoutName(value);
    const int32_t raw = static_cast<int32_t>(value);
    if (name) return std::string(name) + " (raw=" + std::to_string(raw) + ")";
    return std::string("UNKNOWN(raw=") + std::to_string(raw) + ")";
}

static std::string imageLayoutListString(const VkImageLayout* values, uint32_t count) {
    std::string out;
    for (uint32_t i = 0; i < count; ++i) {
        if (i) out += ", ";
        out += imageLayoutValueString(values[i]);
    }
    return out;
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
        } else if constexpr (std::is_same_v<std::remove_cv_t<E>, uint8_t>) {
            generatedEmitHexTyped(dst, section, name, "raw", reinterpret_cast<const uint8_t*>(value), sizeof(value));
        } else if constexpr (std::is_integral_v<std::remove_cv_t<E>> || std::is_enum_v<std::remove_cv_t<E>>) {
            generatedEmitArray(dst, section, name, value, sizeof(value) / sizeof(value[0]));
        } else {
            generatedEmitHexTyped(dst, section, name, "raw", reinterpret_cast<const uint8_t*>(&value), sizeof(value));
        }
    } else if constexpr (std::is_same_v<D, VkExtent2D>) {
        generatedEmitString(dst, section, name, std::to_string(value.width) + " × " + std::to_string(value.height));
    } else if constexpr (std::is_same_v<D, VkExtent3D>) {
        generatedEmitString(dst, section, name, std::to_string(value.width) + " × " + std::to_string(value.height) + " × " + std::to_string(value.depth));
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

uint32_t getQueueFamilyPropertiesPrimary(VulkanApi& api, VkPhysicalDevice device, std::vector<VkQueueFamilyProperties>& out, bool* safetyRejected) {
    if (safetyRejected) *safetyRejected = false;
    if (api.getPhysicalDeviceQueueFamilyProperties) {
        uint32_t count = 0;
        api.getPhysicalDeviceQueueFamilyProperties(device, &count, nullptr);
        if (count > kMaxQueueFamilyEntries) { if (safetyRejected) *safetyRejected = true; return 0; }
        out.resize(count);
        const size_t capacity = out.size();
        if (count) api.getPhysicalDeviceQueueFamilyProperties(device, &count, out.data());
        if (count > capacity) { out.clear(); if (safetyRejected) *safetyRejected = true; return 0; }
        out.resize(count);
        return count;
    }
    if (api.getPhysicalDeviceQueueFamilyProperties2) {
        uint32_t count = 0;
        api.getPhysicalDeviceQueueFamilyProperties2(device, &count, nullptr);
        if (count > kMaxQueueFamilyEntries) { if (safetyRejected) *safetyRejected = true; return 0; }
        std::vector<VkQueueFamilyProperties2> values(count);
        for (auto& value : values) value.sType = VK_STRUCTURE_TYPE_QUEUE_FAMILY_PROPERTIES_2;
        const size_t capacity = values.size();
        if (count) api.getPhysicalDeviceQueueFamilyProperties2(device, &count, values.data());
        if (count > capacity) { out.clear(); if (safetyRejected) *safetyRejected = true; return 0; }
        out.resize(count);
        for (uint32_t i = 0; i < count; ++i) out[i] = values[i].queueFamilyProperties;
        return count;
    }
    return 0;
}


void appendCoreProperties(std::ostringstream& out, uint32_t apiVersion, VulkanApi& api, VkPhysicalDevice device, const std::vector<VkExtensionProperties>& devExts, uint32_t targetMinor, bool includeExtensions) {
    out << '[';
    bool first = true;
    if (!api.getPhysicalDeviceProperties2 || targetMinor < 1 || targetMinor > 4 || !apiVersionAtLeast(apiVersion, 1, targetMinor)) { out << ']'; return; }

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
    if (targetMinor == 1) base.pNext = &p11;
    if (targetMinor == 2) base.pNext = &p12;
    if (targetMinor == 3) base.pNext = &p13;
    if (targetMinor == 4) base.pNext = &p14;
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
    bool copySrcWithinLimit = false;
    bool copyDstWithinLimit = false;
    bool copySrcCollected = false;
    bool copyDstCollected = false;
    api.queryProperties2(device, &base);
    if (targetMinor == 4) {
        copySrcWithinLimit = p14.copySrcLayoutCount <= kMaxVulkan14LayoutEntries;
        copyDstWithinLimit = p14.copyDstLayoutCount <= kMaxVulkan14LayoutEntries;
        if (copySrcWithinLimit) copySrc.resize(p14.copySrcLayoutCount);
        if (copyDstWithinLimit) copyDst.resize(p14.copyDstLayoutCount);
        const size_t copySrcCapacity = copySrc.size();
        const size_t copyDstCapacity = copyDst.size();
        p14.pCopySrcLayouts = copySrcWithinLimit && !copySrc.empty() ? copySrc.data() : nullptr;
        p14.pCopyDstLayouts = copyDstWithinLimit && !copyDst.empty() ? copyDst.data() : nullptr;
        if (copySrcWithinLimit || copyDstWithinLimit) api.queryProperties2(device, &base);
        copySrcCollected = copySrcWithinLimit && p14.copySrcLayoutCount <= copySrcCapacity;
        copyDstCollected = copyDstWithinLimit && p14.copyDstLayoutCount <= copyDstCapacity;
        if (copySrcCollected) copySrc.resize(p14.copySrcLayoutCount);
        else copySrc.clear();
        if (copyDstCollected) copyDst.resize(p14.copyDstLayoutCount);
        else copyDst.clear();
    }
    if (targetMinor == 1) {
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
    if (targetMinor == 2) {
        appendProperty(out, first, "Core 1.2", "driverID", p12.driverID);
        appendProperty(out, first, "Vulkan Registry", "baseline", vulkanscope_registry::kBaseline);
    appendProperty(out, first, "Vulkan Registry", "queryEngine", vulkanscope_registry::kMode);
    appendProperty(out, first, "Vulkan Registry", "implementedPhysicalDeviceStructCount", static_cast<uint64_t>(vulkanscope_registry::kImplementedPhysicalDeviceStructCount));
    appendProperty(out, first, "Vulkan Registry", "runtimeRegistryTokenReferenceCount", static_cast<uint64_t>(vulkanscope_registry::kRuntimeRegistryTokenReferenceCount));
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
    if (targetMinor == 3) {
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
    if (targetMinor == 4) {
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
        if (!copySrcWithinLimit) {
            appendProperty(out, first, "Core 1.4", "pCopySrcLayouts", "Unavailable: safety cap exceeded");
        } else if (!copySrcCollected) {
            appendProperty(out, first, "Core 1.4", "pCopySrcLayouts", "Unavailable: returned layout count exceeded the bounded allocation");
        } else {
            appendProperty(out, first, "Core 1.4", "pCopySrcLayouts", imageLayoutListString(copySrc.data(), static_cast<uint32_t>(copySrc.size())));
        }
        if (!copyDstWithinLimit) {
            appendProperty(out, first, "Core 1.4", "pCopyDstLayouts", "Unavailable: safety cap exceeded");
        } else if (!copyDstCollected) {
            appendProperty(out, first, "Core 1.4", "pCopyDstLayouts", "Unavailable: returned layout count exceeded the bounded allocation");
        } else {
            appendProperty(out, first, "Core 1.4", "pCopyDstLayouts", imageLayoutListString(copyDst.data(), static_cast<uint32_t>(copyDst.size())));
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
        << ",\"runtimeRegistryTokenReferenceCount\":" << vulkanscope_registry::kRuntimeRegistryTokenReferenceCount
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
    const auto surfaceInstanceExtensionEnumeration = enumerateInstanceExtensions(api);
    const auto& instanceExts = surfaceInstanceExtensionEnumeration.values;
    const bool androidSurfaceAvailable = hasExtension(instanceExts, "VK_KHR_android_surface");
    const bool surfaceAvailable = hasExtension(instanceExts, "VK_KHR_surface");
    const bool swapchainColorspaceAvailable = hasExtension(instanceExts, "VK_EXT_swapchain_colorspace");
    const bool surfaceCapabilities2Available = hasExtension(instanceExts, "VK_KHR_get_surface_capabilities2");
    if (!surfaceAvailable || !androidSurfaceAvailable) {
        const std::string missingExtension = !surfaceAvailable ? "VK_KHR_surface" : "VK_KHR_android_surface";
        const std::string missingReason = surfaceInstanceExtensionEnumeration.complete
            ? missingExtension + " is not exposed by the Vulkan instance."
            : std::string("Instance-extension enumeration is not complete, so absence of ") + missingExtension + " cannot be established. " + surfaceInstanceExtensionEnumeration.reason;
        const char* missingStatus = surfaceInstanceExtensionEnumeration.complete ? "not_applicable" : "unavailable";
        return std::string("{\"status\":") + jsonString(missingStatus) + ",\"reason\":" + jsonString(missingReason) + ",\"devices\":[]}";
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
    const bool surfaceCapabilities2Usable = surfaceCapabilities2Available && api.getPhysicalDeviceSurfaceCapabilities2KHR && api.getPhysicalDeviceSurfaceFormats2KHR;
    const char* surfaceCapabilities2Status = surfaceCapabilities2Usable ? "available" : (surfaceCapabilities2Available ? "unavailable" : (surfaceInstanceExtensionEnumeration.complete ? "not_exposed" : "unknown"));
    const std::string surfaceCapabilities2Reason = surfaceCapabilities2Available && !surfaceCapabilities2Usable
        ? "VK_KHR_get_surface_capabilities2 is advertised but one or more required entry points are unavailable; classic VK_KHR_surface queries are used as a fallback."
        : "";

    g_probeStage = 22;
    const auto surfaceDevicesResult = enumeratePhysicalDevicesRobust(api, instance);
    const VkResult surfaceDeviceResult = surfaceDevicesResult.result;
    std::vector<VkPhysicalDevice> devices = surfaceDevicesResult.values;
    const uint32_t deviceCount = static_cast<uint32_t>(devices.size());
    const bool surfaceDeviceEnumerationComplete = surfaceDevicesResult.complete;
    if (!surfaceDevicesResult.resultAvailable || ((surfaceDevicesResult.safetyRejected || (surfaceDeviceResult != VK_SUCCESS && surfaceDeviceResult != VK_INCOMPLETE)) && devices.empty())) {
        api.destroyInstance(instance, nullptr);
        const std::string reason = surfaceDevicesResult.safetyRejected
            ? std::string("Surface probe physical-device enumeration was rejected by a local safety bound. ") + surfaceDevicesResult.localReason
            : (!surfaceDevicesResult.resultAvailable
                ? surfaceDevicesResult.localReason
                : std::string("Surface probe physical-device enumeration failed. VkResult=") + std::to_string(surfaceDeviceResult));
        return std::string("{\"status\":\"unavailable\",\"reason\":") + jsonString(reason) + ",\"physicalDeviceEnumerationSafetyRejected\":" + jsonBool(surfaceDevicesResult.safetyRejected) + ",\"devices\":[]}";
    }
    if (deviceCount == 0) {
        api.destroyInstance(instance, nullptr);
        const char* status = surfaceDeviceEnumerationComplete ? "not_applicable" : "incomplete";
        const std::string reason = surfaceDeviceEnumerationComplete
            ? "Surface probe Vulkan instance was created successfully but no physical devices were enumerated."
            : "Surface probe physical-device enumeration remained VK_INCOMPLETE and returned no bounded partial device evidence.";
        return std::string("{\"status\":") + jsonString(status) + ",\"reason\":" + jsonString(reason) + ",\"physicalDeviceEnumerationResult\":" + std::to_string(surfaceDeviceResult) + ",\"physicalDeviceEnumerationComplete\":" + jsonBool(surfaceDeviceEnumerationComplete) + ",\"physicalDeviceEnumerationSafetyRejected\":false,\"devices\":[]}";
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

    const char* swapchainColorspaceStatus = swapchainColorspaceAvailable ? "available" : (surfaceInstanceExtensionEnumeration.complete ? "not_exposed" : "unknown");
    std::ostringstream out;
    out << "{\"status\":\"available\",\"reason\":\"\",\"surfaceColorSpaceExtensionStatus\":" << jsonString(swapchainColorspaceStatus)
        << ",\"surfaceColorSpaceExtensionAvailable\":" << jsonBool(swapchainColorspaceAvailable)
        << ",\"surfaceColorSpaceExtensionEnabled\":" << jsonBool(swapchainColorspaceAvailable)
        << ",\"surfaceCapabilities2Status\":" << jsonString(surfaceCapabilities2Status)
        << ",\"surfaceCapabilities2Reason\":" << jsonString(surfaceCapabilities2Reason)
        << ",\"physicalDeviceEnumerationResult\":" << surfaceDeviceResult
        << ",\"physicalDeviceEnumerationComplete\":" << jsonBool(surfaceDeviceEnumerationComplete)
        << ",\"physicalDeviceEnumerationSafetyRejected\":" << jsonBool(surfaceDevicesResult.safetyRejected)
        << ",\"physicalDeviceEnumerationReason\":" << jsonString(surfaceDevicesResult.localReason)
        << ",\"selectedApiVersion\":" << jsonString(versionString(selectedApiVersion)) << ",\"devices\":[";
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
        const size_t queueCapacity = queues.size();
        if (queueCount) api.getPhysicalDeviceQueueFamilyProperties(device, &queueCount, queues.data());
        if (queueCount > queueCapacity) { queueSafetyRejected = true; queueCount = 0; queues.clear(); }
        bool presentationSupported = false;
        bool presentationQueryComplete = !queueSafetyRejected;
        std::vector<bool> presentationByQueue(queueCount, false);
        std::vector<VkResult> presentationQueryResults(queueCount, VK_ERROR_UNKNOWN);
        for (uint32_t i = 0; i < queueCount; ++i) {
            VkBool32 supported = VK_FALSE;
            const VkResult supportResult = api.getPhysicalDeviceSurfaceSupportKHR(device, i, surface, &supported);
            presentationQueryResults[i] = supportResult;
            if (supportResult != VK_SUCCESS) presentationQueryComplete = false;
            if (supportResult == VK_SUCCESS && supported == VK_TRUE) {
                presentationSupported = true;
                presentationByQueue[i] = true;
            }
        }
        out << ",\"surface\":{\"available\":true,\"presentationSupported\":" << jsonBool(presentationSupported) << ",\"queueQuerySafetyRejected\":" << jsonBool(queueSafetyRejected) << ",\"presentationQueryComplete\":" << jsonBool(presentationQueryComplete);
        VkResult capResult = VK_SUCCESS;
        bool capabilityQueryAttempted = false;
        bool capabilities2QueryAttempted = false;
        bool capabilities2FallbackUsed = false;
        VkResult capabilities2QueryResult = VK_SUCCESS;
        SurfaceFormatEnumeration formatEnumeration;
        SurfaceFormatEnumeration formats2Enumeration;
        bool formats2QueryAttempted = false;
        bool formats2FallbackUsed = false;
        SurfacePresentModeEnumeration presentModeEnumeration;
        if (presentationSupported) {
            capabilityQueryAttempted = true;
            VkSurfaceCapabilitiesKHR caps{};
            if (surfaceCapabilities2Usable) {
                capabilities2QueryAttempted = true;
                VkPhysicalDeviceSurfaceInfo2KHR info{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SURFACE_INFO_2_KHR, nullptr, surface};
                VkSurfaceCapabilities2KHR caps2{VK_STRUCTURE_TYPE_SURFACE_CAPABILITIES_2_KHR, nullptr, {}};
                capabilities2QueryResult = api.getPhysicalDeviceSurfaceCapabilities2KHR(device, &info, &caps2);
                if (capabilities2QueryResult == VK_SUCCESS) {
                    capResult = VK_SUCCESS;
                    caps = caps2.surfaceCapabilities;
                } else {
                    capabilities2FallbackUsed = true;
                    capResult = api.getPhysicalDeviceSurfaceCapabilitiesKHR(device, surface, &caps);
                }
            } else {
                capResult = api.getPhysicalDeviceSurfaceCapabilitiesKHR(device, surface, &caps);
            }
            out << ",\"capabilityResult\":" << capResult
                << ",\"capabilities2QueryAttempted\":" << jsonBool(capabilities2QueryAttempted)
                << ",\"capabilities2FallbackUsed\":" << jsonBool(capabilities2FallbackUsed);
            if (capabilities2QueryAttempted) out << ",\"capabilities2QueryResult\":" << capabilities2QueryResult;
            out << ",\"capabilityQueryApi\":" << jsonString(capabilities2QueryAttempted && !capabilities2FallbackUsed ? "vkGetPhysicalDeviceSurfaceCapabilities2KHR" : "vkGetPhysicalDeviceSurfaceCapabilitiesKHR");
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
            g_probeStage = 25;
            if (surfaceCapabilities2Usable) {
                formats2QueryAttempted = true;
                formats2Enumeration = enumerateSurfaceFormatsRobust(api, device, surface, true);
                if (formats2Enumeration.complete) {
                    formatEnumeration = formats2Enumeration;
                } else if (!formats2Enumeration.safetyRejected) {
                    formats2FallbackUsed = true;
                    formatEnumeration = enumerateSurfaceFormatsRobust(api, device, surface, false);
                    if (formatEnumeration.values.empty() && !formats2Enumeration.values.empty()) {
                        formatEnumeration.values = formats2Enumeration.values;
                        formatEnumeration.complete = false;
                        const std::string fallbackReason = formatEnumeration.reason;
                        formatEnumeration.reason = "Classic Surface-format fallback did not produce retained entries; earlier bounded partial vkGetPhysicalDeviceSurfaceFormats2KHR evidence was retained.";
                        if (!fallbackReason.empty()) formatEnumeration.reason += " " + fallbackReason;
                    }
                } else {
                    formatEnumeration = formats2Enumeration;
                }
            } else {
                formatEnumeration = enumerateSurfaceFormatsRobust(api, device, surface, false);
            }
            out << ",\"formatQueryAttempted\":" << jsonBool(formatEnumeration.countAttempted)
                << ",\"surfaceFormats2Used\":" << jsonBool(formats2QueryAttempted && !formats2FallbackUsed)
                << ",\"surfaceFormats2Attempted\":" << jsonBool(formats2QueryAttempted)
                << ",\"surfaceFormats2FallbackUsed\":" << jsonBool(formats2FallbackUsed)
                << ",\"formatQueryResult\":" << formatEnumeration.countResult
                << ",\"formatQueryResultSecond\":" << formatEnumeration.dataResult
                << ",\"formatQuerySecondAttempted\":" << jsonBool(formatEnumeration.dataAttempted)
                << ",\"formatQueryAttemptCount\":" << formatEnumeration.attemptCount
                << ",\"formatQuerySafetyRejected\":" << jsonBool(formatEnumeration.safetyRejected)
                << ",\"formatQuerySpecAnomaly\":" << jsonBool(formatEnumeration.specAnomaly)
                << ",\"formatQueryReason\":" << jsonString(formatEnumeration.reason)
                << ",\"formatEnumerationComplete\":" << jsonBool(formatEnumeration.complete);
            if (formats2QueryAttempted) {
                out << ",\"surfaceFormats2CountQueryAttempted\":" << jsonBool(formats2Enumeration.countAttempted)
                    << ",\"surfaceFormats2CountQueryResult\":" << formats2Enumeration.countResult
                    << ",\"surfaceFormats2DataQueryAttempted\":" << jsonBool(formats2Enumeration.dataAttempted)
                    << ",\"surfaceFormats2DataQueryResult\":" << formats2Enumeration.dataResult
                    << ",\"surfaceFormats2SafetyRejected\":" << jsonBool(formats2Enumeration.safetyRejected)
                    << ",\"surfaceFormats2SpecAnomaly\":" << jsonBool(formats2Enumeration.specAnomaly)
                    << ",\"surfaceFormats2Reason\":" << jsonString(formats2Enumeration.reason);
            }
            out << ",\"formatCount\":" << formatEnumeration.values.size() << ",\"formats\":[";
            for (size_t i = 0; i < formatEnumeration.values.size(); ++i) {
                if (i) out << ',';
                const VkSurfaceFormatKHR format = formatEnumeration.values[i];
                out << "{\"format\":" << jsonString(formatName(format.format))
                    << ",\"colorSpace\":" << jsonString(colorSpaceName(format.colorSpace))
                    << ",\"class\":" << jsonString(colorSpaceClass(format.colorSpace))
                    << ",\"description\":" << jsonString(colorSpaceDescription(format.colorSpace)) << '}';
            }
            out << "]";
            g_probeStage = 26;
            presentModeEnumeration = enumerateSurfacePresentModesRobust(api, device, surface);
            out << ",\"presentModeQueryAttempted\":" << jsonBool(presentModeEnumeration.countAttempted)
                << ",\"presentModeCountQueryResult\":" << presentModeEnumeration.countResult
                << ",\"presentModeDataQueryResult\":" << presentModeEnumeration.dataResult
                << ",\"presentModeDataQueryAttempted\":" << jsonBool(presentModeEnumeration.dataAttempted)
                << ",\"presentModeQueryAttemptCount\":" << presentModeEnumeration.attemptCount
                << ",\"presentModeQuerySafetyRejected\":" << jsonBool(presentModeEnumeration.safetyRejected)
                << ",\"presentModeQuerySpecAnomaly\":" << jsonBool(presentModeEnumeration.specAnomaly)
                << ",\"presentModeQueryReason\":" << jsonString(presentModeEnumeration.reason)
                << ",\"presentModeEnumerationComplete\":" << jsonBool(presentModeEnumeration.complete)
                << ",\"presentModes\":[";
            for (size_t i = 0; i < presentModeEnumeration.values.size(); ++i) {
                if (i) out << ',';
                out << jsonString(presentModeName(presentModeEnumeration.values[i]));
            }
            out << "]";
        } else {
            out << ",\"formatQueryAttempted\":false,\"formatQuerySecondAttempted\":false,\"formatQuerySafetyRejected\":false,\"formatEnumerationComplete\":false,\"formatCount\":0,\"formats\":[]"
                << ",\"presentModeQueryAttempted\":false,\"presentModeDataQueryAttempted\":false,\"presentModeQuerySafetyRejected\":false,\"presentModeEnumerationComplete\":false,\"presentModes\":[]";
        }
        const bool dependentWsiQueriesComplete = !presentationSupported || (capResult == VK_SUCCESS && formatEnumeration.complete && presentModeEnumeration.complete);
        const char* dependentWsiQueryStatus = presentationSupported ? (dependentWsiQueriesComplete ? "available" : "incomplete") : ((presentationQueryComplete && !queueSafetyRejected) ? "not_applicable" : "unknown");
        out << ",\"dependentWsiQueryStatus\":" << jsonString(dependentWsiQueryStatus);
        const bool surfaceQueryComplete = surfaceDeviceEnumerationComplete && surfaceInstanceExtensionEnumeration.complete && presentationQueryComplete && !queueSafetyRejected && dependentWsiQueriesComplete;
        std::string surfaceQueryReason;
        if (!surfaceQueryComplete) {
            if (!surfaceDeviceEnumerationComplete) surfaceQueryReason += (surfaceDevicesResult.localReason.empty() ? "Physical-device enumeration is incomplete; returned devices are partial positive evidence only. " : surfaceDevicesResult.localReason + " ");
            if (!surfaceInstanceExtensionEnumeration.complete) surfaceQueryReason += "Instance-extension enumeration is incomplete or unavailable, so extension-dependent Surface evidence may be incomplete. ";
            if (queueSafetyRejected) surfaceQueryReason += "Queue-family enumeration exceeded a safety bound. ";
            if (!presentationQueryComplete) surfaceQueryReason += "One or more vkGetPhysicalDeviceSurfaceSupportKHR calls failed. ";
            if (capabilityQueryAttempted && capResult != VK_SUCCESS) surfaceQueryReason += "Surface capability query failed. ";
            if (presentationSupported && !formatEnumeration.complete) surfaceQueryReason += "Surface-format enumeration is incomplete or unavailable. ";
            if (presentationSupported && !presentModeEnumeration.complete) surfaceQueryReason += "Present-mode enumeration is incomplete or unavailable.";
            while (!surfaceQueryReason.empty() && surfaceQueryReason.back() == ' ') surfaceQueryReason.pop_back();
        }
        out << ",\"queryStatus\":" << jsonString(surfaceQueryComplete ? "available" : "incomplete") << ",\"queryReason\":" << jsonString(surfaceQueryReason) << ",\"queuePresentation\":[";
        bool firstPresentation = true;
        for (uint32_t i = 0; i < queueCount; ++i) {
            if (!firstPresentation) out << ',';
            firstPresentation = false;
            out << "{\"queueFamily\":" << i << ",\"supported\":" << jsonBool(presentationByQueue[i]) << ",\"queryResult\":" << presentationQueryResults[i] << '}';
        }
        out << "]}";
        out << '}';
    }
    out << "]}";
    api.destroySurfaceKHR(instance, surface, nullptr);
    api.destroyInstance(instance, nullptr);
    return out.str();
}


constexpr size_t kMaxProbePublishedBytes = 64ULL * 1024ULL * 1024ULL;

bool publishProbeCheckpoint(const char* path, const std::string& text) {
    if (!path || path[0] == '\0' || text.empty() || text.size() > kMaxProbePublishedBytes) return false;
    const std::string tempPath = std::string(path) + ".checkpoint.tmp";
    unlink(tempPath.c_str());
    int fd = open(tempPath.c_str(), O_CREAT | O_TRUNC | O_WRONLY | O_CLOEXEC, 0600);
    if (fd < 0) return false;
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
            return true;
        }
    } else {
        close(fd);
    }
    unlink(tempPath.c_str());
    return false;
}

std::string collect(jobject surfaceObject, JNIEnv* env, const char* driverMode, const char* driverIcdPath, const char* driverBundlePath, const char* hookLibDir, const char* checkpointPath, bool* finalPublicationOut) {
    if (finalPublicationOut) *finalPublicationOut = false;

    VulkanApi api;
    if (!api.open(driverMode, driverIcdPath, driverBundlePath, hookLibDir)) {
        return std::string("{\"error\":") + jsonString(api.openError.empty() ? "Vulkan loader unavailable" : api.openError) + "}";
    }

    uint32_t loaderVersion = VK_API_VERSION_1_0;
    if (api.enumerateInstanceVersion) { VkResult versionResult = api.enumerateInstanceVersion(&loaderVersion); if (versionResult != VK_SUCCESS) loaderVersion = VK_API_VERSION_1_0; }
    const auto baseInstanceExtensionEnumeration = enumerateInstanceExtensions(api);
    const auto& instanceExts = baseInstanceExtensionEnumeration.values;
    const bool surfaceExtensionAvailable = hasExtension(instanceExts, "VK_KHR_surface");
    const bool androidSurfaceExtensionAvailable = hasExtension(instanceExts, "VK_KHR_android_surface");
    const bool swapchainColorspaceAvailable = hasExtension(instanceExts, "VK_EXT_swapchain_colorspace");
    std::vector<const char*> enabledExtensions;

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
        return "{\"status\":\"unavailable\",\"reason\":\"Required Vulkan physical-device query entry points are unavailable.\",\"devices\":[]}";
    }

    g_probeStage = 4;
    __android_log_print(ANDROID_LOG_INFO, "VulkanProbe", "base enumerate physical devices: instanceApi=%s", versionString(instanceApiVersion).c_str());
    const auto baseDevicesResult = enumeratePhysicalDevicesRobust(api, instance);
    const VkResult deviceEnumerationResult = baseDevicesResult.result;
    std::vector<VkPhysicalDevice> devices = baseDevicesResult.values;
    const uint32_t deviceCount = static_cast<uint32_t>(devices.size());
    __android_log_print(ANDROID_LOG_INFO, "VulkanProbe", "base enumerate resultAvailable=%d result=%d complete=%d safetyRejected=%d count=%u", baseDevicesResult.resultAvailable ? 1 : 0, static_cast<int>(deviceEnumerationResult), baseDevicesResult.complete ? 1 : 0, baseDevicesResult.safetyRejected ? 1 : 0, deviceCount);
    const bool deviceEnumerationComplete = baseDevicesResult.complete;
    if (!baseDevicesResult.resultAvailable || ((baseDevicesResult.safetyRejected || (deviceEnumerationResult != VK_SUCCESS && deviceEnumerationResult != VK_INCOMPLETE)) && devices.empty())) {
        const std::string failureReason = baseDevicesResult.safetyRejected
            ? std::string("Physical-device enumeration was rejected by a local safety bound. ") + baseDevicesResult.localReason
            : (!baseDevicesResult.resultAvailable ? baseDevicesResult.localReason : std::string("vkEnumeratePhysicalDevices failed. VkResult=") + std::to_string(deviceEnumerationResult));
        std::ostringstream failure;
        failure << "{\"status\":\"unavailable\",\"reason\":" << jsonString(failureReason) << ",\"baseReportComplete\":false,\"physicalDeviceEnumerationResult\":";
        if (baseDevicesResult.resultAvailable) failure << static_cast<int>(deviceEnumerationResult); else failure << "null";
        failure << ",\"physicalDeviceEnumerationComplete\":false,\"physicalDeviceEnumerationSafetyRejected\":" << jsonBool(baseDevicesResult.safetyRejected) << ",\"physicalDeviceEnumerationReason\":" << jsonString(baseDevicesResult.localReason) << ",\"devices\":[]}";
        return failure.str();
    }
    if (devices.empty()) {
        const char* status = deviceEnumerationComplete ? "not_applicable" : "incomplete";
        const char* reason = deviceEnumerationComplete ? "Vulkan instance was created but no physical devices were enumerated." : "Physical-device enumeration remained VK_INCOMPLETE and returned no bounded partial device handles.";
        return std::string("{\"status\":") + jsonString(status) + ",\"reason\":" + jsonString(reason) + ",\"baseReportComplete\":false,\"physicalDeviceEnumerationResult\":" + std::to_string(deviceEnumerationResult) + ",\"physicalDeviceEnumerationComplete\":" + jsonBool(deviceEnumerationComplete) + ",\"physicalDeviceEnumerationSafetyRejected\":false,\"physicalDeviceEnumerationReason\":\"\",\"devices\":[]}";
    }
    const char* baseEnumerationStatus = deviceEnumerationComplete ? "available" : "incomplete";
    const std::string baseEnumerationReason = deviceEnumerationComplete ? "" : (!baseDevicesResult.localReason.empty() ? baseDevicesResult.localReason : "Physical-device enumeration remained VK_INCOMPLETE after bounded retries; returned devices are retained as partial positive evidence and the base report is not complete.");
    (void)surfaceObject;
    (void)env;

    g_probeStage = 6;
    std::vector<VkPhysicalDeviceProperties> cachedProperties(deviceCount);
    std::ostringstream checkpoint;
    checkpoint << "{\"status\":" << jsonString(baseEnumerationStatus) << ",\"reason\":" << jsonString(baseEnumerationReason) << ",\"baseReportComplete\":false,\"physicalDeviceEnumerationResult\":" << static_cast<int>(deviceEnumerationResult) << ",\"physicalDeviceEnumerationComplete\":" << jsonBool(deviceEnumerationComplete) << ",\"physicalDeviceEnumerationSafetyRejected\":" << jsonBool(baseDevicesResult.safetyRejected) << ",\"physicalDeviceEnumerationReason\":" << jsonString(baseDevicesResult.localReason) << ",\"loaderVersion\":" << jsonString(versionString(loaderVersion));
    checkpoint << ",\"instanceApiVersion\":" << jsonString(versionString(instanceApiVersion));
    checkpoint << ",\"vulkanRegistryVersion\":\"1.4.362\",\"deviceCount\":" << deviceCount << ",\"devices\":[";
    for (uint32_t deviceIndex = 0; deviceIndex < deviceCount; ++deviceIndex) {
        if (deviceIndex) checkpoint << ',';
        __android_log_print(ANDROID_LOG_INFO, "VulkanProbe", "base preflight device[%u] properties begin", deviceIndex);
        if (!getDevicePropertiesStable(api, devices[deviceIndex], cachedProperties[deviceIndex])) {
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
    out << "{\"status\":" << jsonString(baseEnumerationStatus) << ",\"reason\":" << jsonString(baseEnumerationReason) << ",\"baseReportComplete\":false,\"physicalDeviceEnumerationResult\":" << static_cast<int>(deviceEnumerationResult) << ",\"physicalDeviceEnumerationComplete\":" << jsonBool(deviceEnumerationComplete) << ",\"physicalDeviceEnumerationSafetyRejected\":" << jsonBool(baseDevicesResult.safetyRejected) << ",\"physicalDeviceEnumerationReason\":" << jsonString(baseDevicesResult.localReason) << ",\"loaderVersion\":" << jsonString(versionString(loaderVersion));
    out << ",\"instanceApiVersion\":" << jsonString(versionString(instanceApiVersion));
    out << ",\"vulkanRegistryVersion\":\"1.4.362\"";
    out << ",\"surfaceColorSpaceExtensionAvailable\":" << jsonBool(swapchainColorspaceAvailable);
    out << ",\"surfaceExtensionAvailable\":" << jsonBool(surfaceExtensionAvailable);
    out << ",\"androidSurfaceExtensionAvailable\":" << jsonBool(androidSurfaceExtensionAvailable);
    out << ",\"surfaceColorSpaceExtensionEnabled\":false";
    out << ",\"deviceCount\":" << deviceCount;
    out << ",\"devices\":[";
    bool allDeviceExtensionEnumerationsComplete = true;
    std::string deviceExtensionEnumerationReason;

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
        const auto deviceExtensionEnumeration = enumerateDeviceExtensions(api, device);
        const auto& devExts = deviceExtensions(deviceExtensionEnumeration);
        if (std::strcmp(deviceExtensionEnumeration.status, "available") != 0) {
            allDeviceExtensionEnumerationsComplete = false;
            if (!deviceExtensionEnumeration.reason.empty() && deviceExtensionEnumerationReason.find(deviceExtensionEnumeration.reason) == std::string::npos) {
                if (!deviceExtensionEnumerationReason.empty()) deviceExtensionEnumerationReason += " ";
                deviceExtensionEnumerationReason += deviceExtensionEnumeration.reason;
            }
        }
        __android_log_print(ANDROID_LOG_INFO, "VulkanProbe", "base device[%u] extension enumeration end status=%s count=%zu", deviceIndex, deviceExtensionEnumeration.status, devExts.size());
        g_probeStage = 62;
        const auto deviceLayerEnumeration = enumerateDeviceLayers(api, device);
        const std::string deviceLayers = deviceLayersJson(api, device, deviceLayerEnumeration.values);
        __android_log_print(ANDROID_LOG_INFO, "VulkanProbe", "base device[%u] layer enumeration end status=%s count=%zu bytes=%zu", deviceIndex, deviceLayerEnumeration.status.c_str(), deviceLayerEnumeration.values.size(), deviceLayers.size());
        {
            std::ostringstream extensionSnapshot;
            extensionSnapshot << "{\"status\":" << jsonString(baseEnumerationStatus) << ",\"reason\":" << jsonString(baseEnumerationReason) << ",\"baseReportComplete\":false,\"physicalDeviceEnumerationResult\":" << static_cast<int>(deviceEnumerationResult) << ",\"physicalDeviceEnumerationComplete\":" << jsonBool(deviceEnumerationComplete) << ",\"physicalDeviceEnumerationSafetyRejected\":" << jsonBool(baseDevicesResult.safetyRejected) << ",\"physicalDeviceEnumerationReason\":" << jsonString(baseDevicesResult.localReason) << ",\"loaderVersion\":" << jsonString(versionString(loaderVersion))
                << ",\"instanceApiVersion\":" << jsonString(versionString(instanceApiVersion))
                << ",\"vulkanRegistryVersion\":\"1.4.362\",\"deviceCount\":" << deviceCount << ",\"devices\":[{\"name\":" << jsonString(deviceName)
                << ",\"apiVersion\":" << jsonString(versionString(apiVersion))
                << ",\"driverVersion\":" << jsonString(std::to_string(driverVersion))
                << ",\"driverVersionText\":" << jsonString(driverVersionText(vendorId, driverVersion))
                << ",\"vendorId\":" << vendorId << ",\"deviceId\":" << deviceId << ",\"deviceType\":" << deviceType
                << ",\"deviceExtensionStatus\":" << jsonString(deviceExtensionEnumeration.status)
                << ",\"deviceExtensionReason\":" << jsonString(deviceExtensionEnumeration.reason)
                << ",\"deviceExtensions\":" << extensionsJson(devExts, "Device")
                << ",\"deviceLayerStatus\":" << jsonString(deviceLayerEnumeration.status)
                << ",\"deviceLayerReason\":" << jsonString(deviceLayerEnumeration.reason)
                << ",\"deviceLayersComplete\":" << jsonBool(deviceLayerEnumeration.complete)
                << ",\"deviceLayers\":" << deviceLayers
                << ",\"features\":[],\"versionedFeatures\":[],\"queues\":[],\"memory\":{\"heapCount\":0,\"heaps\":[],\"typeCount\":0,\"types\":[]},\"formats\":[],\"detailedProperties\":[],\"surface\":{\"available\":false}}]}";
            publishProbeCheckpoint(checkpointPath, extensionSnapshot.str());
            __android_log_print(ANDROID_LOG_INFO, "VulkanProbe", "base extension checkpoint published device=%u", deviceIndex);
        }
        const bool coreExtendedQueriesAvailable = api.getPhysicalDeviceProperties2 && api.getPhysicalDeviceFeatures2 && apiVersionAtLeast(apiVersion, 1, 1);
        const char* extendedQueryStatus = coreExtendedQueriesAvailable ? "available" : (apiVersionAtLeast(apiVersion, 1, 1) ? "unavailable" : "not_applicable");
        std::string extendedQueryReason = coreExtendedQueriesAvailable
            ? "Core Vulkan 1.1–1.4 feature and property data are collected by isolated validated core probes."
            : (apiVersionAtLeast(apiVersion, 1, 1) ? std::string("The Vulkan 1.1+ physical-device query entry points are unavailable: properties2=") + (api.getPhysicalDeviceProperties2 ? "present" : "missing") + ", features2=" + (api.getPhysicalDeviceFeatures2 ? "present" : "missing") + ", KHR_get_physical_device_properties2=" + (hasExtension(instanceExts, "VK_KHR_get_physical_device_properties2") ? "advertised" : (baseInstanceExtensionEnumeration.complete ? "not advertised" : "unknown because instance-extension enumeration is incomplete")) : "The device API version is below Vulkan 1.1.");
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
        bool queueSafetyRejected = false;
        queueCount = getQueueFamilyPropertiesPrimary(api, device, queues, &queueSafetyRejected);
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
            << ",\"deviceExtensionStatus\":" << jsonString(deviceExtensionEnumeration.status)
            << ",\"deviceExtensionReason\":" << jsonString(deviceExtensionEnumeration.reason)
            << ",\"deviceExtensions\":" << extensionsJson(devExts, "Device")
            << ",\"deviceLayerStatus\":" << jsonString(deviceLayerEnumeration.status)
            << ",\"deviceLayerReason\":" << jsonString(deviceLayerEnumeration.reason)
            << ",\"deviceLayersComplete\":" << jsonBool(deviceLayerEnumeration.complete)
            << ",\"deviceLayers\":" << deviceLayers << ",\"features\":[";
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
        out << ",\"vulkan14Status\":" << jsonString(apiVersionAtLeast(apiVersion, 1, 4) ? "deferred" : "not_applicable")
            << ",\"vulkan14Reason\":" << jsonString(apiVersionAtLeast(apiVersion, 1, 4)
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
                << ",\"unknownFlags\":" << (q.queueFlags & ~0x57Fu)
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
            baseReadySnapshot += ",\"surface\":{\"available\":false}}]}";
            const bool baseReadyPublished = publishProbeCheckpoint(checkpointPath, baseReadySnapshot);
            __android_log_print(baseReadyPublished ? ANDROID_LOG_INFO : ANDROID_LOG_ERROR, "VulkanProbe", baseReadyPublished ? "base core checkpoint published before optional surface enrichment device=%u" : "base core checkpoint publication failed before optional surface enrichment device=%u", deviceIndex);
        }
        out << ",\"surface\":{\"available\":false,\"queryStatus\":\"unknown\",\"queryReason\":\"Base probe does not own final live-Surface evidence.\"}";
        out << '}';
    }
    out << "]}";
    std::string finalResult = out.str();
    const bool baseReportComplete = deviceEnumerationComplete && allDeviceExtensionEnumerationsComplete;
    const std::string completenessMarker = "\"baseReportComplete\":false";
    const size_t completenessMarkerPos = finalResult.find(completenessMarker);
    const bool uniqueCompletenessMarker = completenessMarkerPos != std::string::npos && finalResult.find(completenessMarker, completenessMarkerPos + completenessMarker.size()) == std::string::npos;
    if (!uniqueCompletenessMarker || !jsonContainersBalanced(finalResult)) {
        return "{\"status\":\"unavailable\",\"reason\":\"Base report terminal JSON construction failed.\",\"baseReportComplete\":false,\"devices\":[]}";
    }
    finalResult.replace(completenessMarkerPos, completenessMarker.size(), std::string("\"baseReportComplete\":") + jsonBool(baseReportComplete));
    const std::string deviceExtensionCompleteness = std::string(",\"deviceExtensionEnumerationsComplete\":") + jsonBool(allDeviceExtensionEnumerationsComplete) + ",\"deviceExtensionEnumerationReason\":" + jsonString(deviceExtensionEnumerationReason);
    finalResult.pop_back();
    finalResult += deviceExtensionCompleteness + "}";
    if (!jsonContainersBalanced(finalResult)) {
        return "{\"status\":\"unavailable\",\"reason\":\"Base report terminal JSON construction failed after completeness metadata.\",\"baseReportComplete\":false,\"devices\":[]}";
    }
    const bool finalCheckpointPublished = publishProbeCheckpoint(checkpointPath, finalResult);
    if (finalPublicationOut) *finalPublicationOut = finalCheckpointPublished;
    if (finalCheckpointPublished) {
        __android_log_print(ANDROID_LOG_INFO, "VulkanProbe", baseReportComplete ? "base report complete checkpoint published before JNI return; terminal marker remains service-owned" : "partial base report checkpoint published before JNI return; terminal marker remains service-owned");
    } else {
        __android_log_print(ANDROID_LOG_ERROR, "VulkanProbe", "base terminal checkpoint publication failed before JNI return");
    }
    return finalResult;
}

std::string collectVulkanMetadata(const char* driverMode, const char* driverIcdPath, const char* driverBundlePath, const char* hookLibDir) {
    VulkanApi api;
    if (!api.open(driverMode, driverIcdPath, driverBundlePath, hookLibDir)) {
        return std::string("{\"status\":\"unavailable\",\"group\":\"metadata\",\"reason\":") + jsonString(api.openError.empty() ? "Vulkan loader unavailable" : api.openError) + ",\"instanceExtensions\":[],\"instanceLayers\":[]}";
    }
    const auto instanceExtensionEnumeration = enumerateInstanceExtensions(api);
    const auto instanceLayerEnumeration = enumerateInstanceLayers(api);
    const auto& instanceExts = instanceExtensionEnumeration.values;
    const auto& instanceLayerValues = instanceLayerEnumeration.values;
    std::ostringstream out;
    out << "{\"status\":\"available\",\"group\":\"metadata\",\"reason\":\"\"";
    out << ",\"registryCoverage\":" << registryCoverageJson();
    out << ",\"instanceExtensionDependencyQueryEnabled\":" << jsonBool(hasExtension(instanceExts, "VK_KHR_get_physical_device_properties2"));
    out << ",\"instanceExtensionStatus\":" << jsonString(instanceExtensionEnumeration.status);
    out << ",\"instanceExtensionReason\":" << jsonString(instanceExtensionEnumeration.reason);
    out << ",\"instanceExtensionsComplete\":" << jsonBool(instanceExtensionEnumeration.complete);
    out << ",\"instanceExtensions\":" << extensionsJson(instanceExts, "Instance");
    out << ",\"instanceLayerStatus\":" << jsonString(instanceLayerEnumeration.status);
    out << ",\"instanceLayerReason\":" << jsonString(instanceLayerEnumeration.reason);
    out << ",\"instanceLayersComplete\":" << jsonBool(instanceLayerEnumeration.complete);
    out << ",\"instanceLayers\":" << layersJson(api, instanceLayerValues);
    out << ",\"registryCoverageComplete\":true}";
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
    const VkResult queryDeviceResult = queryDevicesResult.result;
    std::vector<VkPhysicalDevice> devices = queryDevicesResult.values;
    const uint32_t count = static_cast<uint32_t>(devices.size());
    const bool physicalDeviceEnumerationComplete = queryDevicesResult.complete;
    if (!queryDevicesResult.resultAvailable || (queryDevicesResult.safetyRejected && queryDevicesResult.values.empty())) {
        api.destroyInstance(instance, nullptr);
        const std::string reason = queryDevicesResult.safetyRejected ? std::string("Physical-device enumeration was rejected by a local safety bound. ") + queryDevicesResult.localReason : queryDevicesResult.localReason;
        return std::string("{\"status\":\"unavailable\",\"group\":") + jsonString(group) + ",\"reason\":" + jsonString(reason) + ",\"physicalDeviceEnumerationSafetyRejected\":" + jsonBool(queryDevicesResult.safetyRejected) + ",\"devices\":[]}";
    }
    if (queryDeviceResult != VK_SUCCESS && queryDeviceResult != VK_INCOMPLETE && queryDevicesResult.values.empty()) {
        api.destroyInstance(instance, nullptr);
        return std::string("{\"status\":\"unavailable\",\"group\":") + jsonString(group) + ",\"reason\":" + jsonString(std::string("vkEnumeratePhysicalDevices failed. VkResult=") + std::to_string(queryDeviceResult)) + ",\"devices\":[]}";
    }
    if (count == 0) {
        api.destroyInstance(instance, nullptr);
        const char* status = physicalDeviceEnumerationComplete ? "not_applicable" : "incomplete";
        const char* reason = physicalDeviceEnumerationComplete ? "No physical Vulkan devices were enumerated." : "Physical-device enumeration remained VK_INCOMPLETE and returned no bounded partial device handles.";
        return std::string("{\"status\":") + jsonString(status) + ",\"group\":" + jsonString(group) + ",\"reason\":" + jsonString(reason) + ",\"devices\":[]}";
    }
    std::ostringstream out;
    out << "{\"status\":" << jsonString(physicalDeviceEnumerationComplete ? "available" : "incomplete") << ",\"group\":" << jsonString(group) << ",\"reason\":" << jsonString(physicalDeviceEnumerationComplete ? "" : (!queryDevicesResult.localReason.empty() ? queryDevicesResult.localReason : "Physical-device enumeration remained VK_INCOMPLETE; bounded partial positive evidence was retained.")) << ",\"physicalDeviceEnumerationResult\":" << static_cast<int>(queryDeviceResult) << ",\"physicalDeviceEnumerationComplete\":" << jsonBool(physicalDeviceEnumerationComplete) << ",\"devices\":[";
    bool firstDevice = true;
    bool matchedAny = false;
    for (uint32_t i = 0; i < count; ++i) {
        VkPhysicalDeviceProperties physicalProperties{};
        getDevicePropertiesPrimary(api, devices[i], physicalProperties);
        const uint32_t apiVersion = physicalProperties.apiVersion;
        if (!apiVersionAtLeast(apiVersion, 1, targetMinor)) continue;
        matchedAny = true;
        const uint32_t vendorId = physicalProperties.vendorID;
        const uint32_t deviceId = physicalProperties.deviceID;
        const char* name = physicalProperties.deviceName;
        const auto deviceExtensionEnumeration = enumerateDeviceExtensions(api, devices[i]);
        const auto& devExts = deviceExtensions(deviceExtensionEnumeration);
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
        if (!physicalDeviceEnumerationComplete) return std::string("{\"status\":\"incomplete\",\"group\":") + jsonString(group) + ",\"reason\":\"Physical-device enumeration was incomplete, so absence of a device meeting the requested core API version cannot be established.\",\"devices\":[]}";
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
    const bool directExtensionQuery = group && std::strncmp(group, "ext::", 5) == 0;
    const char* extensionName = directExtensionQuery ? group + 5 : ((descriptor && std::strcmp(descriptor->scope, "device-extension") == 0) ? descriptor->extension : nullptr);
    if (!extensionName || extensionName[0] == '\0' || std::strncmp(extensionName, "VK_", 3) != 0 || std::strlen(extensionName) > VK_MAX_EXTENSION_NAME_SIZE - 1) {
        return std::string("{\"status\":\"unavailable\",\"group\":") + jsonString(group ? group : "") + ",\"reason\":\"Unknown or invalid device-extension Vulkan query group.\",\"devices\":[]}";
    }
    for (const char* p = extensionName; *p; ++p) {
        if (!(std::isalnum(static_cast<unsigned char>(*p)) || *p == '_')) {
            return std::string("{\"status\":\"unavailable\",\"group\":") + jsonString(group ? group : "") + ",\"reason\":\"Invalid Vulkan extension name.\",\"devices\":[]}";
        }
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
    const VkResult createResult = api.createInstanceCompatible(std::min(loaderVersion, VK_API_VERSION_1_3), queryInstanceExtensions, &instance, &selectedInstanceApiVersion);
    if (createResult != VK_SUCCESS || !instance) {
        return std::string("{\"status\":\"unavailable\",\"group\":") + jsonString(group) + ",\"reason\":" + jsonString(std::string("vkCreateInstance failed with VkResult ") + std::to_string(createResult)) + ",\"devices\":[]}";
    }
    if (!api.loadInstanceFunctions(instance) || !api.getPhysicalDeviceProperties2 || !api.getPhysicalDeviceFeatures2) {
        api.destroyInstance(instance, nullptr);
        return std::string("{\"status\":\"unavailable\",\"group\":") + jsonString(group) + ",\"reason\":\"Vulkan extended physical-device query entry points are unavailable.\",\"devices\":[]}";
    }
    const auto extensionDevicesResult = enumeratePhysicalDevicesRobust(api, instance);
    const VkResult extensionDeviceResult = extensionDevicesResult.result;
    std::vector<VkPhysicalDevice> devices = extensionDevicesResult.values;
    const uint32_t count = static_cast<uint32_t>(devices.size());
    const bool physicalDeviceEnumerationComplete = extensionDevicesResult.complete;
    if (!extensionDevicesResult.resultAvailable || (extensionDevicesResult.safetyRejected && extensionDevicesResult.values.empty())) {
        api.destroyInstance(instance, nullptr);
        const std::string reason = extensionDevicesResult.safetyRejected ? std::string("Physical-device enumeration was rejected by a local safety bound. ") + extensionDevicesResult.localReason : extensionDevicesResult.localReason;
        return std::string("{\"status\":\"unavailable\",\"group\":") + jsonString(group) + ",\"reason\":" + jsonString(reason) + ",\"physicalDeviceEnumerationSafetyRejected\":" + jsonBool(extensionDevicesResult.safetyRejected) + ",\"devices\":[]}";
    }
    if (extensionDeviceResult != VK_SUCCESS && extensionDeviceResult != VK_INCOMPLETE && extensionDevicesResult.values.empty()) {
        api.destroyInstance(instance, nullptr);
        return std::string("{\"status\":\"unavailable\",\"group\":") + jsonString(group) + ",\"reason\":" + jsonString(std::string("vkEnumeratePhysicalDevices failed. VkResult=") + std::to_string(extensionDeviceResult)) + ",\"devices\":[]}";
    }
    if (count == 0) {
        api.destroyInstance(instance, nullptr);
        const char* status = physicalDeviceEnumerationComplete ? "not_applicable" : "incomplete";
        const char* reason = physicalDeviceEnumerationComplete ? "No physical Vulkan devices were enumerated." : "Physical-device enumeration remained VK_INCOMPLETE and returned no bounded partial device handles.";
        return std::string("{\"status\":") + jsonString(status) + ",\"group\":" + jsonString(group) + ",\"reason\":" + jsonString(reason) + ",\"devices\":[]}";
    }
    std::ostringstream out;
    bool extensionQueryIncomplete = !physicalDeviceEnumerationComplete;
    std::string extensionQueryReason = physicalDeviceEnumerationComplete ? "" : (!extensionDevicesResult.localReason.empty() ? extensionDevicesResult.localReason : "Physical-device enumeration remained VK_INCOMPLETE; bounded partial positive evidence was retained.");
    auto markExtensionIncomplete = [&](const std::string& reason) {
        extensionQueryIncomplete = true;
        if (reason.empty() || extensionQueryReason.find(reason) != std::string::npos) return;
        if (!extensionQueryReason.empty()) extensionQueryReason += " ";
        extensionQueryReason += reason;
    };
    const std::string extensionStatusToken = "__VULKANSCOPE_EXTENSION_STATUS__";
    const std::string extensionReasonToken = "__VULKANSCOPE_EXTENSION_REASON__";
    out << "{\"status\":" << jsonString(extensionStatusToken) << ",\"group\":" << jsonString(group) << ",\"extension\":" << jsonString(extensionName) << ",\"reason\":" << jsonString(extensionReasonToken) << ",\"physicalDeviceEnumerationResult\":" << static_cast<int>(extensionDeviceResult) << ",\"physicalDeviceEnumerationComplete\":" << jsonBool(physicalDeviceEnumerationComplete) << ",\"devices\":[";
    bool firstDevice = true;
    bool matchedAny = false;
    bool extensionEnumerationUncertain = false;
    bool extensionEnumerationIncomplete = false;
    std::string extensionEnumerationReason;
    for (uint32_t i = 0; i < count; ++i) {
        const auto deviceExtensionEnumeration = enumerateDeviceExtensions(api, devices[i]);
        const auto& devExts = deviceExtensions(deviceExtensionEnumeration);
        const bool extensionPresent = hasExtension(devExts, extensionName);
        if (std::strcmp(deviceExtensionEnumeration.status, "available") != 0) {
            extensionEnumerationUncertain = true;
            if (std::strcmp(deviceExtensionEnumeration.status, "incomplete") == 0) extensionEnumerationIncomplete = true;
            if (extensionEnumerationReason.empty()) extensionEnumerationReason = deviceExtensionEnumeration.reason;
            markExtensionIncomplete(deviceExtensionEnumeration.reason.empty() ? "Device-extension enumeration was incomplete or unavailable for at least one physical device; extension absence cannot be established there." : deviceExtensionEnumeration.reason);
        }
        if (!extensionPresent) continue;
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
        const std::string genericPropertySection = extensionName[0] ? (std::string("Extension · ") + extensionName) : "Advanced Query";
        auto addFeature = [&](const char* featureName, VkBool32 value) { featureEntries.push_back({std::string(extensionName) + " · " + featureName, value == VK_TRUE}); };
        auto addProperty = [&](const std::string& propertyName, const std::string& value) {
            propertyEntries.push_back({genericPropertySection, propertyName, value});
        };
        auto propertyValuesEquivalent = [](const std::string& left, const std::string& right) {
            if (left == right) return true;
            if (!left.empty() && right.rfind(left + " (0x", 0) == 0) return true;
            if (!right.empty() && left.rfind(right + " (0x", 0) == 0) return true;
            return false;
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
        } else if (std::strcmp(extensionName, "VK_EXT_inline_uniform_block") == 0) {
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
        if (std::strcmp(extensionName, "VK_EXT_host_image_copy") == 0 && api.getPhysicalDeviceProperties2) {
            constexpr uint32_t kMaxHostImageCopyLayoutEntries = 65536;
            VkPhysicalDeviceHostImageCopyPropertiesEXT hostProperties{};
            hostProperties.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_HOST_IMAGE_COPY_PROPERTIES_EXT;
            VkPhysicalDeviceProperties2 hostQuery{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2, &hostProperties, {}};
            api.queryProperties2(devices[i], &hostQuery);
            const bool srcWithinLimit = hostProperties.copySrcLayoutCount <= kMaxHostImageCopyLayoutEntries;
            const bool dstWithinLimit = hostProperties.copyDstLayoutCount <= kMaxHostImageCopyLayoutEntries;
            std::vector<VkImageLayout> srcLayouts(srcWithinLimit ? hostProperties.copySrcLayoutCount : 0);
            std::vector<VkImageLayout> dstLayouts(dstWithinLimit ? hostProperties.copyDstLayoutCount : 0);
            const size_t srcCapacity = srcLayouts.size();
            const size_t dstCapacity = dstLayouts.size();
            hostProperties.pCopySrcLayouts = srcWithinLimit && !srcLayouts.empty() ? srcLayouts.data() : nullptr;
            hostProperties.pCopyDstLayouts = dstWithinLimit && !dstLayouts.empty() ? dstLayouts.data() : nullptr;
            if (srcWithinLimit || dstWithinLimit) api.queryProperties2(devices[i], &hostQuery);
            const bool srcCollected = srcWithinLimit && hostProperties.copySrcLayoutCount <= srcCapacity;
            const bool dstCollected = dstWithinLimit && hostProperties.copyDstLayoutCount <= dstCapacity;
            if (srcCollected) srcLayouts.resize(hostProperties.copySrcLayoutCount); else srcLayouts.clear();
            if (dstCollected) dstLayouts.resize(hostProperties.copyDstLayoutCount); else dstLayouts.clear();
            addProperty("pCopySrcLayouts", !srcWithinLimit ? "Unavailable: layout count exceeds the 65536-entry safety bound." : !srcCollected ? "Unavailable: returned layout count exceeded the bounded allocation." : imageLayoutListString(srcLayouts.data(), static_cast<uint32_t>(srcLayouts.size())));
            addProperty("pCopyDstLayouts", !dstWithinLimit ? "Unavailable: layout count exceeds the 65536-entry safety bound." : !dstCollected ? "Unavailable: returned layout count exceeded the bounded allocation." : imageLayoutListString(dstLayouts.data(), static_cast<uint32_t>(dstLayouts.size())));
        }
        if (runtimePNextAdded > 0) addProperty("generatedRuntimePNextTypes", std::to_string(runtimePNextAdded));
        if (std::strcmp(extensionName, "VK_EXT_cooperative_matrix_maintenance1") == 0) {
            auto componentTypeName = [](VkComponentTypeKHR type) -> const char* {
                if (type == VK_COMPONENT_TYPE_FLOAT16_KHR) return "VK_COMPONENT_TYPE_FLOAT16_KHR";
                if (type == VK_COMPONENT_TYPE_FLOAT32_KHR) return "VK_COMPONENT_TYPE_FLOAT32_KHR";
                if (type == VK_COMPONENT_TYPE_FLOAT64_KHR) return "VK_COMPONENT_TYPE_FLOAT64_KHR";
                if (type == VK_COMPONENT_TYPE_SINT8_KHR) return "VK_COMPONENT_TYPE_SINT8_KHR";
                if (type == VK_COMPONENT_TYPE_SINT16_KHR) return "VK_COMPONENT_TYPE_SINT16_KHR";
                if (type == VK_COMPONENT_TYPE_SINT32_KHR) return "VK_COMPONENT_TYPE_SINT32_KHR";
                if (type == VK_COMPONENT_TYPE_SINT64_KHR) return "VK_COMPONENT_TYPE_SINT64_KHR";
                if (type == VK_COMPONENT_TYPE_UINT8_KHR) return "VK_COMPONENT_TYPE_UINT8_KHR";
                if (type == VK_COMPONENT_TYPE_UINT16_KHR) return "VK_COMPONENT_TYPE_UINT16_KHR";
                if (type == VK_COMPONENT_TYPE_UINT32_KHR) return "VK_COMPONENT_TYPE_UINT32_KHR";
                if (type == VK_COMPONENT_TYPE_UINT64_KHR) return "VK_COMPONENT_TYPE_UINT64_KHR";
                if (type == VK_COMPONENT_TYPE_BFLOAT16_KHR) return "VK_COMPONENT_TYPE_BFLOAT16_KHR";
                if (type == VK_COMPONENT_TYPE_SINT8_PACKED_NV) return "VK_COMPONENT_TYPE_SINT8_PACKED_NV";
                if (type == VK_COMPONENT_TYPE_UINT8_PACKED_NV) return "VK_COMPONENT_TYPE_UINT8_PACKED_NV";
                if (type == VK_COMPONENT_TYPE_FLOAT8_E4M3_EXT) return "VK_COMPONENT_TYPE_FLOAT8_E4M3_EXT";
                if (type == VK_COMPONENT_TYPE_FLOAT8_E5M2_EXT) return "VK_COMPONENT_TYPE_FLOAT8_E5M2_EXT";
                if (type == VK_COMPONENT_TYPE_FLOAT6_E2M3_EXT) return "VK_COMPONENT_TYPE_FLOAT6_E2M3_EXT";
                if (type == VK_COMPONENT_TYPE_FLOAT6_E3M2_EXT) return "VK_COMPONENT_TYPE_FLOAT6_E3M2_EXT";
                if (type == VK_COMPONENT_TYPE_FLOAT4_E2M1_EXT) return "VK_COMPONENT_TYPE_FLOAT4_E2M1_EXT";
                if (type == VK_COMPONENT_TYPE_FLOAT8_UNSIGNED_E8M0_EXT) return "VK_COMPONENT_TYPE_FLOAT8_UNSIGNED_E8M0_EXT";
                if (type == VK_COMPONENT_TYPE_MXINT8_EXT) return "VK_COMPONENT_TYPE_MXINT8_EXT";
                return "UNKNOWN";
            };
            VkPhysicalDeviceCooperativeMatrixMaintenance1FeaturesEXT maintenance{};
            maintenance.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_MAINTENANCE_1_FEATURES_EXT;
            VkPhysicalDeviceFeatures2 maintenanceQuery{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2, &maintenance, {}};
            if (api.getPhysicalDeviceFeatures2) api.queryFeatures2(devices[i], &maintenanceQuery);
            if (!maintenance.cooperativeMatrixProperties2) {
                addProperty("cooperativeMatrixProperties2Query", "Not applicable: cooperativeMatrixProperties2 is not supported.");
            } else if (!api.getPhysicalDeviceCooperativeMatrixProperties2EXT) {
                addProperty("cooperativeMatrixProperties2Query", "Unavailable: vkGetPhysicalDeviceCooperativeMatrixProperties2EXT is not exposed by the Vulkan loader.");
            } else {
                VkPhysicalDeviceCooperativeMatrixInfo2EXT info{};
                info.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_COOPERATIVE_MATRIX_INFO_2_EXT;
                info.scope = VK_SCOPE_SUBGROUP_KHR;
                info.invocations = 0;
                info.subgroupSize = 0;
                info.flags = 0;
                uint32_t propertyCount = 0;
                VkResult propertyResult = api.getPhysicalDeviceCooperativeMatrixProperties2EXT(devices[i], &info, &propertyCount, nullptr);
                if (propertyResult == VK_INCOMPLETE) markExtensionIncomplete("vkGetPhysicalDeviceCooperativeMatrixProperties2EXT count query returned VK_INCOMPLETE; bounded partial property evidence is not a complete enumeration.");
                if ((propertyResult == VK_SUCCESS || propertyResult == VK_INCOMPLETE) && propertyCount <= 4096) {
                    std::vector<VkCooperativeMatrixProperties2EXT> properties(propertyCount);
                    for (auto& property : properties) { property.sType = VK_STRUCTURE_TYPE_COOPERATIVE_MATRIX_PROPERTIES_2_EXT; property.pNext = nullptr; }
                    const size_t propertyCapacity = properties.size();
                    if (propertyCount > 0) propertyResult = api.getPhysicalDeviceCooperativeMatrixProperties2EXT(devices[i], &info, &propertyCount, properties.data());
                    if (propertyResult == VK_INCOMPLETE) markExtensionIncomplete("vkGetPhysicalDeviceCooperativeMatrixProperties2EXT data query returned VK_INCOMPLETE; bounded partial property evidence was retained.");
                    if (propertyCount > propertyCapacity) {
                        addProperty("cooperativeMatrixProperties2Query", "Unavailable: data query count exceeded the bounded allocation.");
                        addProperty("cooperativeMatrixProperties2Count", std::to_string(propertyCount));
                    } else if (propertyResult != VK_SUCCESS && propertyResult != VK_INCOMPLETE) {
                        addProperty("cooperativeMatrixProperties2Query", "Unavailable: VkResult=" + std::to_string(propertyResult));
                        addProperty("cooperativeMatrixProperties2Count", std::to_string(propertyCount));
                    } else {
                        addProperty("cooperativeMatrixProperties2Query", propertyResult == VK_SUCCESS ? "Available" : "Partial: VK_INCOMPLETE; returned entries are positive evidence only.");
                        addProperty("cooperativeMatrixProperties2Count", std::to_string(propertyCount));
                        const uint32_t emitCount = propertyCount;
                        for (uint32_t propertyIndex = 0; propertyIndex < emitCount; ++propertyIndex) {
                            const auto& property = properties[propertyIndex];
                            const std::string prefix = "cooperativeMatrixProperties2[" + std::to_string(propertyIndex) + "].";
                            addProperty(prefix + "MGranularity", std::to_string(property.MGranularity));
                            addProperty(prefix + "NGranularity", std::to_string(property.NGranularity));
                            addProperty(prefix + "KGranularity", std::to_string(property.KGranularity));
                            addProperty(prefix + "AType", std::string(componentTypeName(property.AType)) + " (raw=" + std::to_string(static_cast<int32_t>(property.AType)) + ")");
                            addProperty(prefix + "BType", std::string(componentTypeName(property.BType)) + " (raw=" + std::to_string(static_cast<int32_t>(property.BType)) + ")");
                            addProperty(prefix + "CType", std::string(componentTypeName(property.CType)) + " (raw=" + std::to_string(static_cast<int32_t>(property.CType)) + ")");
                            addProperty(prefix + "ResultType", std::string(componentTypeName(property.ResultType)) + " (raw=" + std::to_string(static_cast<int32_t>(property.ResultType)) + ")");
                            addProperty(prefix + "ATypeRaw", std::to_string(static_cast<int32_t>(property.AType)));
                            addProperty(prefix + "BTypeRaw", std::to_string(static_cast<int32_t>(property.BType)));
                            addProperty(prefix + "CTypeRaw", std::to_string(static_cast<int32_t>(property.CType)));
                            addProperty(prefix + "ResultTypeRaw", std::to_string(static_cast<int32_t>(property.ResultType)));
                        }
                    }
                } else if (propertyCount > 4096) {
                    addProperty("cooperativeMatrixProperties2Query", "Unavailable: property count exceeds the 4096-entry safety bound.");
                    addProperty("cooperativeMatrixProperties2Count", std::to_string(propertyCount));
                } else {
                    addProperty("cooperativeMatrixProperties2Query", "Unavailable: VkResult=" + std::to_string(propertyResult));
                    addProperty("cooperativeMatrixProperties2Count", std::to_string(propertyCount));
                }
            }
        }
        const char* groupName = group;
        if (std::strcmp(groupName, "videoEncodeFeedback2") == 0) {
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
                for (auto& existing : propertyEntries) {
                    if (existing.section == generated.section && existing.name == generated.name && propertyValuesEquivalent(existing.value, generated.value)) {
                        duplicate = true;
                        break;
                    }
                    if (existing.section == genericPropertySection && existing.name == generated.name && propertyValuesEquivalent(existing.value, generated.value)) {
                        existing.section = generated.section;
                        duplicate = true;
                        break;
                    }
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
    if (!matchedAny && !physicalDeviceEnumerationComplete) {
        return std::string("{\"status\":\"incomplete\",\"group\":") + jsonString(group) + ",\"extension\":" + jsonString(extensionName) + ",\"reason\":\"Physical-device enumeration was incomplete, so extension absence cannot be established across all devices.\",\"devices\":[]}";
    }
    if (!matchedAny && extensionEnumerationIncomplete) {
        return std::string("{\"status\":\"incomplete\",\"group\":") + jsonString(group) + ",\"extension\":" + jsonString(extensionName) + ",\"reason\":" + jsonString(extensionEnumerationReason.empty() ? "Device-extension enumeration remained VK_INCOMPLETE, so extension absence cannot be established." : extensionEnumerationReason) + ",\"devices\":[]}";
    }
    if (!matchedAny && extensionEnumerationUncertain) {
        return std::string("{\"status\":\"unavailable\",\"group\":") + jsonString(group) + ",\"extension\":" + jsonString(extensionName) + ",\"reason\":" + jsonString(extensionEnumerationReason.empty() ? "Device-extension enumeration was unavailable, so extension applicability could not be established." : extensionEnumerationReason) + ",\"devices\":[]}";
    }
    if (!matchedAny) return std::string("{\"status\":\"not_applicable\",\"group\":") + jsonString(group) + ",\"extension\":" + jsonString(extensionName) + ",\"reason\":\"The selected extension was not enumerated by any physical device after complete extension enumeration.\",\"devices\":[]}";
    std::string extensionJson = out.str();
    auto replaceExtensionToken = [&](const std::string& token, const std::string& value) {
        const std::string encodedToken = jsonString(token);
        const std::size_t position = extensionJson.find(encodedToken);
        if (position != std::string::npos) extensionJson.replace(position, encodedToken.size(), jsonString(value));
    };
    replaceExtensionToken(extensionStatusToken, extensionQueryIncomplete ? "incomplete" : "available");
    replaceExtensionToken(extensionReasonToken, extensionQueryReason);
    return extensionJson;
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
        return std::string("{\"status\":\"unavailable\",\"reason\":") + jsonString(std::string("Unable to create Vulkan instance for the dedicated Vulkan 1.4 probe. VkResult=") + std::to_string(createResult)) + ",\"devices\":[]}";
    }
    if (!api.loadInstanceFunctions(instance) || !api.getPhysicalDeviceProperties2 || !api.getPhysicalDeviceFeatures2) {
        api.destroyInstance(instance, nullptr);
        return "{\"status\":\"unavailable\",\"reason\":\"Vulkan 1.4 probe entry points are unavailable.\",\"devices\":[]}";
    }
    const auto v14DevicesResult = enumeratePhysicalDevicesRobust(api, instance);
    const VkResult v14DeviceResult = v14DevicesResult.result;
    std::vector<VkPhysicalDevice> devices = v14DevicesResult.values;
    const uint32_t count = static_cast<uint32_t>(devices.size());
    const bool physicalDeviceEnumerationComplete = v14DevicesResult.complete;
    if (!v14DevicesResult.resultAvailable || (v14DevicesResult.safetyRejected && v14DevicesResult.values.empty())) {
        api.destroyInstance(instance, nullptr);
        const std::string reason = v14DevicesResult.safetyRejected ? std::string("Physical-device enumeration was rejected by a local safety bound. ") + v14DevicesResult.localReason : v14DevicesResult.localReason;
        return std::string("{\"status\":\"unavailable\",\"reason\":") + jsonString(reason) + ",\"physicalDeviceEnumerationSafetyRejected\":" + jsonBool(v14DevicesResult.safetyRejected) + ",\"devices\":[]}";
    }
    if (v14DeviceResult != VK_SUCCESS && v14DeviceResult != VK_INCOMPLETE && v14DevicesResult.values.empty()) {
        api.destroyInstance(instance, nullptr);
        return std::string("{\"status\":\"unavailable\",\"reason\":") + jsonString(std::string("vkEnumeratePhysicalDevices failed. VkResult=") + std::to_string(v14DeviceResult)) + ",\"devices\":[]}";
    }
    if (count == 0) {
        api.destroyInstance(instance, nullptr);
        return physicalDeviceEnumerationComplete ? "{\"status\":\"not_applicable\",\"reason\":\"No physical Vulkan devices were enumerated.\",\"devices\":[]}" : "{\"status\":\"incomplete\",\"reason\":\"Physical-device enumeration remained VK_INCOMPLETE and returned no bounded partial device handles.\",\"devices\":[]}";
    }
    std::ostringstream out;
    out << "{\"status\":" << jsonString(physicalDeviceEnumerationComplete ? "available" : "incomplete") << ",\"reason\":" << jsonString(physicalDeviceEnumerationComplete ? "" : (!v14DevicesResult.localReason.empty() ? v14DevicesResult.localReason : "Physical-device enumeration remained VK_INCOMPLETE; bounded partial positive evidence was retained.")) << ",\"physicalDeviceEnumerationResult\":" << static_cast<int>(v14DeviceResult) << ",\"physicalDeviceEnumerationComplete\":" << jsonBool(physicalDeviceEnumerationComplete) << ",\"devices\":[";
    bool firstDevice = true;
    bool saw14 = false;
    for (uint32_t i = 0; i < count; ++i) {
        VkPhysicalDeviceProperties physicalProperties{};
        getDevicePropertiesPrimary(api, devices[i], physicalProperties);
        const uint32_t apiVersion = physicalProperties.apiVersion;
        if (!apiVersionAtLeast(apiVersion, 1, 4)) continue;
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
        const size_t copySrcCapacity = copySrcLayouts.size();
        const size_t copyDstCapacity = copyDstLayouts.size();
        p14.pCopySrcLayouts = copySrcWithinLimit && !copySrcLayouts.empty() ? copySrcLayouts.data() : nullptr;
        p14.pCopyDstLayouts = copyDstWithinLimit && !copyDstLayouts.empty() ? copyDstLayouts.data() : nullptr;
        if (copySrcWithinLimit || copyDstWithinLimit) api.queryProperties2(devices[i], &properties2);
        const bool copySrcCollected = copySrcWithinLimit && p14.copySrcLayoutCount <= copySrcCapacity;
        const bool copyDstCollected = copyDstWithinLimit && p14.copyDstLayoutCount <= copyDstCapacity;
        if (copySrcCollected) copySrcLayouts.resize(p14.copySrcLayoutCount); else copySrcLayouts.clear();
        if (copyDstCollected) copyDstLayouts.resize(p14.copyDstLayoutCount); else copyDstLayouts.clear();

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
        addProp("pCopySrcLayouts", !copySrcWithinLimit ? "Unavailable: safety cap exceeded" : !copySrcCollected ? "Unavailable: returned layout count exceeded the bounded allocation" : imageLayoutListString(copySrcLayouts.data(), static_cast<uint32_t>(copySrcLayouts.size())));
        addProp("pCopyDstLayouts", !copyDstWithinLimit ? "Unavailable: safety cap exceeded" : !copyDstCollected ? "Unavailable: returned layout count exceeded the bounded allocation" : imageLayoutListString(copyDstLayouts.data(), static_cast<uint32_t>(copyDstLayouts.size())));
        addProp("optimalTilingLayoutUUID", hexBytes(p14.optimalTilingLayoutUUID, 16));
        addProp("identicalMemoryTypeRequirements", p14.identicalMemoryTypeRequirements == VK_TRUE ? "true" : "false");
        out << "]}";
    }
    out << "]}";
    if (!saw14) {
        out.str("");
        out.clear();
        if (physicalDeviceEnumerationComplete) out << "{\"status\":\"not_applicable\",\"reason\":\"The installed Vulkan device API version is below 1.4.\",\"devices\":[]}";
        else out << "{\"status\":\"incomplete\",\"reason\":\"Physical-device enumeration was incomplete, so absence of a Vulkan 1.4 device cannot be established.\",\"devices\":[]}";
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
    const VkResult advancedDeviceResult = advancedDevicesResult.result;
    std::vector<VkPhysicalDevice> devices = advancedDevicesResult.values;
    const uint32_t count = static_cast<uint32_t>(devices.size());
    const bool physicalDeviceEnumerationComplete = advancedDevicesResult.complete;
    if (!advancedDevicesResult.resultAvailable || (advancedDevicesResult.safetyRejected && advancedDevicesResult.values.empty())) {
        api.destroyInstance(instance, nullptr);
        const std::string reason = advancedDevicesResult.safetyRejected ? std::string("Physical-device enumeration was rejected by a local safety bound. ") + advancedDevicesResult.localReason : advancedDevicesResult.localReason;
        return std::string("{\"status\":\"unavailable\",\"group\":") + jsonString(group) + ",\"reason\":" + jsonString(reason) + ",\"physicalDeviceEnumerationSafetyRejected\":" + jsonBool(advancedDevicesResult.safetyRejected) + ",\"devices\":[]}";
    }
    if (advancedDeviceResult != VK_SUCCESS && advancedDeviceResult != VK_INCOMPLETE && advancedDevicesResult.values.empty()) {
        api.destroyInstance(instance, nullptr);
        return std::string("{\"status\":\"unavailable\",\"group\":") + jsonString(group ? group : "") + ",\"reason\":" + jsonString(std::string("vkEnumeratePhysicalDevices failed. VkResult=") + std::to_string(advancedDeviceResult)) + ",\"devices\":[]}";
    }
    if (count == 0) {
        api.destroyInstance(instance, nullptr);
        const char* status = physicalDeviceEnumerationComplete ? "not_applicable" : "incomplete";
        const char* reason = physicalDeviceEnumerationComplete ? "No physical Vulkan devices were enumerated." : "Physical-device enumeration remained VK_INCOMPLETE and returned no bounded partial device handles.";
        return std::string("{\"status\":") + jsonString(status) + ",\"group\":" + jsonString(group ? group : "") + ",\"reason\":" + jsonString(reason) + ",\"devices\":[]}";
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

    std::vector<VkPhysicalDeviceGroupProperties> physicalDeviceGroups;
    VkResult physicalDeviceGroupEnumerationResult = VK_SUCCESS;
    bool physicalDeviceGroupEnumerationComplete = true;
    if (group && std::strcmp(group, "groups") == 0) {
        uint32_t groupCount = 0;
        const VkResult groupCountResult = api.enumeratePhysicalDeviceGroups(instance, &groupCount, nullptr);
        if (groupCountResult != VK_SUCCESS && groupCountResult != VK_INCOMPLETE) {
            api.destroyInstance(instance, nullptr);
            return std::string("{\"status\":\"unavailable\",\"group\":\"groups\",\"reason\":") + jsonString(std::string("vkEnumeratePhysicalDeviceGroups count query failed. VkResult=") + std::to_string(groupCountResult)) + ",\"devices\":[]}";
        }
        if (groupCount > kMaxDeviceGroupEntries) {
            api.destroyInstance(instance, nullptr);
            return "{\"status\":\"unavailable\",\"group\":\"groups\",\"reason\":\"Physical device group count exceeds safety limit.\",\"devices\":[]}";
        }
        physicalDeviceGroups.resize(groupCount);
        for (auto& item : physicalDeviceGroups) { item.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_GROUP_PROPERTIES; item.pNext = nullptr; }
        if (groupCount > 0) {
            const size_t groupCapacity = physicalDeviceGroups.size();
            physicalDeviceGroupEnumerationResult = api.enumeratePhysicalDeviceGroups(instance, &groupCount, physicalDeviceGroups.data());
            if ((physicalDeviceGroupEnumerationResult != VK_SUCCESS && physicalDeviceGroupEnumerationResult != VK_INCOMPLETE) || groupCount > groupCapacity) {
                api.destroyInstance(instance, nullptr);
                const std::string detail = groupCount > groupCapacity ? "Physical device group data query exceeded the bounded allocation." : std::string("vkEnumeratePhysicalDeviceGroups data query failed. VkResult=") + std::to_string(physicalDeviceGroupEnumerationResult);
                return std::string("{\"status\":\"unavailable\",\"group\":\"groups\",\"reason\":") + jsonString(detail) + ",\"devices\":[]}";
            }
            physicalDeviceGroups.resize(groupCount);
        } else {
            physicalDeviceGroupEnumerationResult = groupCountResult;
        }
        physicalDeviceGroupEnumerationComplete = groupCountResult == VK_SUCCESS && physicalDeviceGroupEnumerationResult == VK_SUCCESS;
    }

    std::ostringstream out;
    const bool groupEnumerationIncomplete = group && std::strcmp(group, "groups") == 0 && !physicalDeviceGroupEnumerationComplete;
    bool advancedEnumerationIncomplete = groupEnumerationIncomplete || !physicalDeviceEnumerationComplete;
    std::string advancedEnumerationReason;
    auto markAdvancedIncomplete = [&](const std::string& reason) {
        advancedEnumerationIncomplete = true;
        if (reason.empty() || advancedEnumerationReason.find(reason) != std::string::npos) return;
        if (!advancedEnumerationReason.empty()) advancedEnumerationReason += " ";
        advancedEnumerationReason += reason;
    };
    if (!physicalDeviceEnumerationComplete) markAdvancedIncomplete(!advancedDevicesResult.localReason.empty() ? advancedDevicesResult.localReason : "Physical-device enumeration remained VK_INCOMPLETE; bounded partial positive evidence was retained.");
    if (groupEnumerationIncomplete) markAdvancedIncomplete("vkEnumeratePhysicalDeviceGroups returned VK_INCOMPLETE; bounded partial group evidence was retained.");
    const bool advancedDependsOnDeviceExtensions = group && (
        std::strcmp(group, "queue2") == 0 ||
        std::strcmp(group, "format2") == 0 ||
        std::strcmp(group, "imageFormat2") == 0 ||
        std::strcmp(group, "external") == 0 ||
        std::strcmp(group, "sparse") == 0 ||
        std::strcmp(group, "memory2") == 0 ||
        std::strcmp(group, "videoCapabilities") == 0);
    const std::string advancedStatusToken = "__VULKANSCOPE_ADVANCED_STATUS__";
    const std::string advancedReasonToken = "__VULKANSCOPE_ADVANCED_REASON__";
    out << "{\"status\":" << jsonString(advancedStatusToken) << ",\"group\":" << jsonString(group ? group : "") << ",\"reason\":" << jsonString(advancedReasonToken) << ",\"physicalDeviceEnumerationResult\":" << static_cast<int>(advancedDeviceResult) << ",\"physicalDeviceEnumerationComplete\":" << jsonBool(physicalDeviceEnumerationComplete) << ",\"devices\":[";
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
        const auto deviceExtensionEnumeration = enumerateDeviceExtensions(api, devices[i]);
        const auto& devExts = deviceExtensions(deviceExtensionEnumeration);
        if (advancedDependsOnDeviceExtensions && std::strcmp(deviceExtensionEnumeration.status, "available") != 0) {
            markAdvancedIncomplete(deviceExtensionEnumeration.reason.empty() ? "Device-extension enumeration was incomplete or unavailable for an extension-dependent advanced query; optional or extension-gated evidence may be missing." : deviceExtensionEnumeration.reason);
        }
        auto hasExt = [&](const char* n) { return hasExtension(devExts, n); };
        const uint32_t vendorId = physicalProperties.vendorID;
        const uint32_t deviceId = physicalProperties.deviceID;
        const char* name = physicalProperties.deviceName;
        if (group && std::strcmp(group, "tools") == 0) {
            addDevicePrefix(i);
            uint32_t toolCount = 0;
            const VkResult toolCountResult = api.getPhysicalDeviceToolProperties(devices[i], &toolCount, nullptr);
            if (toolCountResult == VK_INCOMPLETE) markAdvancedIncomplete("vkGetPhysicalDeviceToolProperties count query returned VK_INCOMPLETE; tool absence is not proven.");
            out << ",\"properties\":[";
            if (toolCountResult != VK_SUCCESS && toolCountResult != VK_INCOMPLETE) {
                out << "{\"section\":\"Vulkan Tool Query\",\"name\":\"Status\",\"value\":" << jsonString(std::string("Unavailable: count query VkResult=") + std::to_string(toolCountResult)) << '}';
            } else if (toolCount > kMaxToolEntries) {
                out << "{\"section\":\"Vulkan Tool Query\",\"name\":\"Status\",\"value\":\"Unavailable: tool count exceeds safety limit.\"}";
            } else if (toolCount > 0) {
                std::vector<VkPhysicalDeviceToolProperties> tools(toolCount);
                for (auto& t : tools) { t.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TOOL_PROPERTIES; t.pNext = nullptr; }
                const size_t toolCapacity = tools.size();
                const VkResult toolResult = api.getPhysicalDeviceToolProperties(devices[i], &toolCount, tools.data());
                if (toolResult == VK_INCOMPLETE) markAdvancedIncomplete("vkGetPhysicalDeviceToolProperties data query returned VK_INCOMPLETE; bounded partial tool evidence was retained.");
                if ((toolResult != VK_SUCCESS && toolResult != VK_INCOMPLETE) || toolCount > toolCapacity) {
                    const std::string detail = toolCount > toolCapacity ? "Unavailable: data query count exceeded the bounded allocation." : std::string("Unavailable: data query VkResult=") + std::to_string(toolResult);
                    out << "{\"section\":\"Vulkan Tool Query\",\"name\":\"Status\",\"value\":" << jsonString(detail) << '}';
                } else {
                    for (uint32_t t = 0; t < toolCount; ++t) {
                        if (t) out << ',';
                        auto toolPurposes = [](uint32_t purposes) {
                            std::string value;
                            uint32_t knownMask = 0;
                            const std::pair<uint32_t, const char*> names[] = {{0x1u, "VK_TOOL_PURPOSE_VALIDATION_BIT"}, {0x2u, "VK_TOOL_PURPOSE_PROFILING_BIT"}, {0x4u, "VK_TOOL_PURPOSE_TRACING_BIT"}, {0x8u, "VK_TOOL_PURPOSE_ADDITIONAL_FEATURES_BIT"}, {0x10u, "VK_TOOL_PURPOSE_MODIFYING_FEATURES_BIT"}, {0x20u, "VK_TOOL_PURPOSE_DEBUG_REPORTING_BIT_EXT"}, {0x40u, "VK_TOOL_PURPOSE_DEBUG_MARKERS_BIT_EXT"}};
                            for (const auto& entry : names) { knownMask |= entry.first; if ((purposes & entry.first) != 0) { if (!value.empty()) value += " | "; value += entry.second; } }
                            const uint32_t unknownBits = purposes & ~knownMask;
                            if (value.empty()) value = "0";
                            if (unknownBits != 0) { std::ostringstream raw; raw << std::uppercase << std::hex << unknownBits; value += " | UNKNOWN_BITS=0x" + raw.str(); }
                            return value;
                        };
                        const std::string completeness = toolResult == VK_INCOMPLETE ? " | enumeration=PARTIAL_VK_INCOMPLETE" : " | enumeration=COMPLETE";
                        out << "{\"section\":\"Vulkan Tool\",\"name\":" << jsonString(tools[t].name) << ",\"value\":" << jsonString(std::string(tools[t].description) + " | version " + std::string(tools[t].version) + " | purposes=" + toolPurposes(tools[t].purposes) + " | layer=" + tools[t].layer + completeness) << '}';
                    }
                }
            } else {
                out << "{\"section\":\"Vulkan Tool Query\",\"name\":\"Status\",\"value\":" << jsonString(toolCountResult == VK_SUCCESS ? "Available: zero active Vulkan tools were reported." : "Incomplete: VK_INCOMPLETE returned zero tool entries; absence is not proven.") << '}';
            }
            out << "]}";
        } else if (group && std::strcmp(group, "queue2") == 0) {
            addDevicePrefix(i);
            uint32_t qcount = 0;
            api.getPhysicalDeviceQueueFamilyProperties2(devices[i], &qcount, nullptr);
            if (qcount > kMaxQueueFamilyEntries) {
                out << ",\"properties\":[{\"section\":\"Queue Family Properties2\",\"name\":\"Query\",\"value\":\"Unavailable: queue-family count exceeds safety limit.\"}]}";
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
                const size_t queueCapacity = queues.size();
                api.getPhysicalDeviceQueueFamilyProperties2(devices[i], &qcount, queues.data());
                if (qcount > queueCapacity) {
                    out << ",\"properties\":[{\"section\":\"Queue Family Properties2\",\"name\":\"Query\",\"value\":\"Unavailable: data query count exceeded the bounded allocation.\"}]}";
                    continue;
                }
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
            const bool formatFeatureFlags2 = apiVersion >= VK_API_VERSION_1_3 || hasExt("VK_KHR_format_feature_flags2");
            for (VkFormat fmt : knownFormatValues()) {
                if (!shouldQueryFormat(fmt, apiVersion, devExts)) continue;
                VkFormatProperties3 p3{}; p3.sType = VK_STRUCTURE_TYPE_FORMAT_PROPERTIES_3; p3.pNext = nullptr;
                VkFormatProperties2 p2{}; p2.sType = VK_STRUCTURE_TYPE_FORMAT_PROPERTIES_2; p2.pNext = formatFeatureFlags2 ? static_cast<void*>(&p3) : nullptr;
                api.getPhysicalDeviceFormatProperties2(devices[i], fmt, &p2);
                if (!firstProp) out << ',';
                firstProp = false;
                std::ostringstream value;
                value << "linear=0x" << std::hex << p2.formatProperties.linearTilingFeatures << ", optimal=0x" << p2.formatProperties.optimalTilingFeatures << ", buffer=0x" << p2.formatProperties.bufferFeatures << ", featureFlags2Available=" << (formatFeatureFlags2 ? "true" : "false") << ", featureFlags2 linear=0x" << (formatFeatureFlags2 ? p3.linearTilingFeatures : 0) << " optimal=0x" << (formatFeatureFlags2 ? p3.optimalTilingFeatures : 0) << " buffer=0x" << (formatFeatureFlags2 ? p3.bufferFeatures : 0);
                out << "{\"section\":\"Format Properties2\",\"name\":" << jsonString(formatName(fmt)) << ",\"value\":" << jsonString(value.str()) << '}';
            }
            out << "]}";
        } else if (group && std::strcmp(group, "imageFormat2") == 0) {
            addDevicePrefix(i);
            out << ",\"properties\":[";
            bool firstProp = true;
            const VkImageTiling tilings[] = {VK_IMAGE_TILING_LINEAR, VK_IMAGE_TILING_OPTIMAL};
            const bool hasOpaqueFd = hasExt("VK_KHR_external_memory_fd");
            const bool hasAhb = hasExt("VK_ANDROID_external_memory_android_hardware_buffer");
            uint32_t baseAttempts = 0, baseSuccess = 0, baseFormatUnsupported = 0, baseOtherErrors = 0;
            uint32_t externalAttempts[2] = {0, 0}, externalSuccess[2] = {0, 0}, externalFormatUnsupported[2] = {0, 0}, externalOtherErrors[2] = {0, 0};
            struct ImageFormatQueryResultEntry { std::string name; const char* status; int32_t vkResult; bool hasVkResult; std::string reason; };
            std::vector<ImageFormatQueryResultEntry> queryResults;
            queryResults.reserve(2048);
            int32_t firstBaseOtherResult = 0;
            int32_t firstExternalOtherResult[2] = {0, 0};
            for (VkFormat fmt : knownFormatValues()) {
                if (!shouldQueryFormat(fmt, apiVersion, devExts)) continue;
                for (uint32_t tiling : tilings) {
                    const VkExternalMemoryHandleTypeFlagBits handleTypes[2] = {VK_EXTERNAL_MEMORY_HANDLE_TYPE_OPAQUE_FD_BIT, VK_EXTERNAL_MEMORY_HANDLE_TYPE_ANDROID_HARDWARE_BUFFER_BIT_ANDROID};
                    const bool handleEnabled[2] = {hasOpaqueFd, hasAhb};
                    const char* handleNames[2] = {"OPAQUE_FD", "ANDROID_HARDWARE_BUFFER"};
                    const char* missingReasons[2] = {"VK_KHR_external_memory_fd was not enumerated for this device.", "VK_ANDROID_external_memory_android_hardware_buffer was not enumerated for this device."};
                    VkPhysicalDeviceImageFormatInfo2 info{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_IMAGE_FORMAT_INFO_2, nullptr, fmt, VK_IMAGE_TYPE_2D, static_cast<VkImageTiling>(tiling), VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_SAMPLED_BIT, 0};
                    VkImageFormatProperties2 props{VK_STRUCTURE_TYPE_IMAGE_FORMAT_PROPERTIES_2, nullptr, {}};
                    ++baseAttempts;
                    const VkResult baseResult = api.getPhysicalDeviceImageFormatProperties2(devices[i], &info, &props);
                    const std::string baseName = formatName(fmt) + " · " + (tiling == VK_IMAGE_TILING_LINEAR ? std::string("LINEAR") : std::string("OPTIMAL"));
                    if (baseResult == VK_SUCCESS) {
                        ++baseSuccess;
                        queryResults.push_back({baseName, "available", 0, true, ""});
                        if (!firstProp) out << ','; firstProp = false;
                        std::ostringstream value; value << "tiling=" << (tiling == VK_IMAGE_TILING_LINEAR ? "LINEAR" : "OPTIMAL") << ", extent=" << props.imageFormatProperties.maxExtent.width << " × " << props.imageFormatProperties.maxExtent.height << " × " << props.imageFormatProperties.maxExtent.depth << ", mipLevels=" << props.imageFormatProperties.maxMipLevels << ", arrayLayers=" << props.imageFormatProperties.maxArrayLayers << ", sampleCounts=0x" << std::hex << props.imageFormatProperties.sampleCounts << ", maxResourceSize=" << std::dec << props.imageFormatProperties.maxResourceSize;
                        out << "{\"section\":\"Image Format Properties2\",\"name\":" << jsonString(baseName) << ",\"value\":" << jsonString(value.str()) << '}';
                    } else if (baseResult == VK_ERROR_FORMAT_NOT_SUPPORTED) {
                        ++baseFormatUnsupported;
                        queryResults.push_back({baseName, "unsupported", static_cast<int32_t>(baseResult), true, ""});
                    } else {
                        ++baseOtherErrors;
                        if (firstBaseOtherResult == 0) firstBaseOtherResult = static_cast<int32_t>(baseResult);
                        queryResults.push_back({baseName, "unavailable", static_cast<int32_t>(baseResult), true, ""});
                    }

                    for (uint32_t handleIndex = 0; handleIndex < 2; ++handleIndex) {
                        const std::string externalName = baseName + " · " + handleNames[handleIndex];
                        if (!handleEnabled[handleIndex]) {
                            queryResults.push_back({externalName, "not_applicable", 0, false, missingReasons[handleIndex]});
                            continue;
                        }
                        ++externalAttempts[handleIndex];
                        VkPhysicalDeviceExternalImageFormatInfo extImageInfo{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_IMAGE_FORMAT_INFO, nullptr, handleTypes[handleIndex]};
                        VkExternalImageFormatProperties extImageProps{VK_STRUCTURE_TYPE_EXTERNAL_IMAGE_FORMAT_PROPERTIES, nullptr, {}};
                        info.pNext = &extImageInfo;
                        props = {VK_STRUCTURE_TYPE_IMAGE_FORMAT_PROPERTIES_2, &extImageProps, {}};
                        const VkResult r = api.getPhysicalDeviceImageFormatProperties2(devices[i], &info, &props);
                        if (r == VK_SUCCESS) {
                            ++externalSuccess[handleIndex];
                            queryResults.push_back({externalName, "available", 0, true, ""});
                            if (!firstProp) out << ','; firstProp = false;
                            std::ostringstream value; value << "tiling=" << (tiling == VK_IMAGE_TILING_LINEAR ? "LINEAR" : "OPTIMAL") << ", extent=" << props.imageFormatProperties.maxExtent.width << " × " << props.imageFormatProperties.maxExtent.height << " × " << props.imageFormatProperties.maxExtent.depth << ", mipLevels=" << props.imageFormatProperties.maxMipLevels << ", arrayLayers=" << props.imageFormatProperties.maxArrayLayers << ", sampleCounts=0x" << std::hex << props.imageFormatProperties.sampleCounts << ", maxResourceSize=" << std::dec << props.imageFormatProperties.maxResourceSize << ", externalHandle=" << handleNames[handleIndex] << ", externalMemoryFeatures=0x" << std::hex << extImageProps.externalMemoryProperties.externalMemoryFeatures << ", exportFromImported=0x" << extImageProps.externalMemoryProperties.exportFromImportedHandleTypes << ", compatibleHandles=0x" << extImageProps.externalMemoryProperties.compatibleHandleTypes;
                            out << "{\"section\":\"Image Format Properties2\",\"name\":" << jsonString(externalName) << ",\"value\":" << jsonString(value.str()) << '}';
                        } else if (r == VK_ERROR_FORMAT_NOT_SUPPORTED) {
                            ++externalFormatUnsupported[handleIndex];
                            queryResults.push_back({externalName, "unsupported", static_cast<int32_t>(r), true, ""});
                        } else {
                            ++externalOtherErrors[handleIndex];
                            if (firstExternalOtherResult[handleIndex] == 0) firstExternalOtherResult[handleIndex] = static_cast<int32_t>(r);
                            queryResults.push_back({externalName, "unavailable", static_cast<int32_t>(r), true, ""});
                        }
                    }
                }
            }
            auto emitImageFormatSummary = [&](const std::string& name, uint32_t attempts, uint32_t successes, uint32_t unsupported, uint32_t otherErrors, int32_t firstOtherResult) {
                if (!firstProp) out << ','; firstProp = false;
                std::ostringstream value;
                value << "attempted=" << attempts << ", success=" << successes << ", formatNotSupported=" << unsupported << ", otherErrors=" << otherErrors;
                if (otherErrors) value << ", firstOtherVkResult=" << firstOtherResult;
                out << "{\"section\":\"Image Format Properties2 Query Diagnostics\",\"name\":" << jsonString(name) << ",\"value\":" << jsonString(value.str()) << '}';
            };
            if (!firstProp) out << ','; firstProp = false;
            out << "{\"section\":\"Image Format Properties2 Query Diagnostics\",\"name\":\"Query parameters\",\"value\":\"imageType=VK_IMAGE_TYPE_2D, usage=VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_SAMPLED_BIT, flags=0\"}";
            emitImageFormatSummary("Base image-format queries", baseAttempts, baseSuccess, baseFormatUnsupported, baseOtherErrors, firstBaseOtherResult);
            if (hasOpaqueFd) emitImageFormatSummary("OPAQUE_FD external image-format queries", externalAttempts[0], externalSuccess[0], externalFormatUnsupported[0], externalOtherErrors[0], firstExternalOtherResult[0]);
            if (hasAhb) emitImageFormatSummary("ANDROID_HARDWARE_BUFFER external image-format queries", externalAttempts[1], externalSuccess[1], externalFormatUnsupported[1], externalOtherErrors[1], firstExternalOtherResult[1]);
            out << "],\"imageFormatQueryResults\":[";
            for (size_t resultIndex = 0; resultIndex < queryResults.size(); ++resultIndex) {
                if (resultIndex) out << ',';
                const auto& result = queryResults[resultIndex];
                out << "{\"name\":" << jsonString(result.name) << ",\"status\":" << jsonString(result.status) << ",\"vkResult\":";
                if (result.hasVkResult) out << result.vkResult; else out << "null";
                out << ",\"reason\":" << jsonString(result.reason) << '}';
            }
            out << "]}";
        } else if (group && std::strcmp(group, "videoCapabilities") == 0) {
            addDevicePrefix(i);
            std::vector<std::pair<std::string, std::string>> videoProperties;
            auto addProperty = [&](const std::string& propertyName, const std::string& propertyValue) { videoProperties.emplace_back(propertyName, propertyValue); };

            const bool deviceExtensionsComplete = std::strcmp(deviceExtensionEnumeration.status, "available") == 0;
            const bool videoQueue = hasExt("VK_KHR_video_queue");
            if (!videoQueue) {
                addProperty("queryStatus", deviceExtensionsComplete ? "Not applicable: VK_KHR_video_queue was not enumerated by this physical device." : "Unknown: device-extension enumeration is incomplete or unavailable, so absence of VK_KHR_video_queue cannot be established.");
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
                auto exactProfileStatus = [](VkResult result) {
                    if (result == VK_SUCCESS) return std::string("Supported for exact 4:2:0 8-bit profile");
                    const bool unsupported = result == VK_ERROR_VIDEO_PICTURE_LAYOUT_NOT_SUPPORTED_KHR || result == VK_ERROR_VIDEO_PROFILE_OPERATION_NOT_SUPPORTED_KHR || result == VK_ERROR_VIDEO_PROFILE_FORMAT_NOT_SUPPORTED_KHR || result == VK_ERROR_VIDEO_PROFILE_CODEC_NOT_SUPPORTED_KHR;
                    return std::string(unsupported ? "Unsupported for exact 4:2:0 8-bit profile (VkResult=" : "Unavailable (VkResult=") + std::to_string(result) + ")";
                };
                addProperty("videoRegistry", std::string("Khronos video.xml SHA-256 ") + kVideoRegistrySha256 + "; Vulkan registry SHA-256 " + kVideoRegistryVulkanSha256);
                addProperty("queryRecipe", "Registry-driven codec-profile census. Codec-specific profile member values come from the locked Vulkan 1.4.362 vk.xml and are cross-checked against locked Khronos video.xml StdVideo enums. Capability queries use one exact general profile: VK_VIDEO_CHROMA_SUBSAMPLING_420_BIT_KHR with 8-bit luma/chroma. H.264 decode includes every registry-defined pictureLayout value and AV1 decode includes both filmGrainSupport values. Results apply only to each exact 4:2:0 8-bit profile combination and are not codec-wide or bit-depth-wide claims. Video format enumeration remains separately labelled sampled-profile evidence.");
                if (!api.getPhysicalDeviceVideoCapabilitiesKHR) {
                    addProperty("Video capability query", "Unavailable: vkGetPhysicalDeviceVideoCapabilitiesKHR is unavailable in this Vulkan stack.");
                } else {
                auto emitDecode = [&](const char* name, const std::string& variant, const char* extension, VkVideoCodecOperationFlagsKHR operation, uint32_t profileSType, uint32_t profileValue, uint32_t capSType, int32_t auxiliaryValue) {
                    if (!hasExt(extension) || !hasExt("VK_KHR_video_decode_queue")) {
                        addProperty(std::string("Video · ") + name + " · " + variant, deviceExtensionsComplete ? "Not applicable: required video decode extension was not enumerated." : "Unknown: device-extension enumeration is incomplete or unavailable, so required video decode extension absence cannot be established.");
                        return;
                    }
                    VkVideoProfileInfoKHR profile = makeBaseProfile(operation);
                    if (std::strcmp(name, "H.264 decode") == 0) {
                        VkVideoDecodeH264ProfileInfoKHR codec{};
                        codec.sType = static_cast<VkStructureType>(profileSType); codec.pNext = nullptr; codec.stdProfileIdc = static_cast<StdVideoH264ProfileIdc>(profileValue); codec.pictureLayout = static_cast<VkVideoDecodeH264PictureLayoutFlagBitsKHR>(auxiliaryValue);
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
                        const std::string prefix = std::string("Video · ") + name + " · " + variant + " ";
                        addProperty(prefix + "status", exactProfileStatus(r));
                        if (r == VK_SUCCESS) {
                            addProperty(prefix + "maxLevelIdc", std::string(videoH264LevelName(static_cast<int32_t>(codecCaps.maxLevelIdc))) + " (raw=" + std::to_string(static_cast<int32_t>(codecCaps.maxLevelIdc)) + ")");
                            addProperty(prefix + "fieldOffsetGranularity", std::to_string(codecCaps.fieldOffsetGranularity.x) + " × " + std::to_string(codecCaps.fieldOffsetGranularity.y));
                            addProperty(prefix + "codedExtent", std::to_string(caps.minCodedExtent.width) + " × " + std::to_string(caps.minCodedExtent.height) + " .. " + std::to_string(caps.maxCodedExtent.width) + " × " + std::to_string(caps.maxCodedExtent.height));
                            addProperty(prefix + "DPB", std::to_string(caps.maxDpbSlots) + " slots / " + std::to_string(caps.maxActiveReferencePictures) + " active refs");
                            addProperty(prefix + "bitstreamAlignment", std::to_string(caps.minBitstreamBufferOffsetAlignment) + " offset / " + std::to_string(caps.minBitstreamBufferSizeAlignment) + " size");
                            addProperty(prefix + "stdHeader", std::string(caps.stdHeaderVersion.extensionName) + " " + std::to_string(caps.stdHeaderVersion.specVersion));
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
                        const std::string prefix = std::string("Video · ") + name + " · " + variant + " ";
                        addProperty(prefix + "status", exactProfileStatus(r));
                        if (r == VK_SUCCESS) {
                            addProperty(prefix + "maxLevelIdc", std::string(videoH265LevelName(static_cast<int32_t>(codecCaps.maxLevelIdc))) + " (raw=" + std::to_string(static_cast<int32_t>(codecCaps.maxLevelIdc)) + ")");
                            addProperty(prefix + "codedExtent", std::to_string(caps.minCodedExtent.width) + " × " + std::to_string(caps.minCodedExtent.height) + " .. " + std::to_string(caps.maxCodedExtent.width) + " × " + std::to_string(caps.maxCodedExtent.height));
                            addProperty(prefix + "DPB", std::to_string(caps.maxDpbSlots) + " slots / " + std::to_string(caps.maxActiveReferencePictures) + " active refs");
                            addProperty(prefix + "bitstreamAlignment", std::to_string(caps.minBitstreamBufferOffsetAlignment) + " offset / " + std::to_string(caps.minBitstreamBufferSizeAlignment) + " size");
                            addProperty(prefix + "stdHeader", std::string(caps.stdHeaderVersion.extensionName) + " " + std::to_string(caps.stdHeaderVersion.specVersion));
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
                        const std::string prefix = std::string("Video · ") + name + " · " + variant + " ";
                        addProperty(prefix + "status", exactProfileStatus(r));
                        if (r == VK_SUCCESS) {
                            addProperty(prefix + "maxLevel", std::string(videoVP9LevelName(static_cast<int32_t>(codecCaps.maxLevel))) + " (raw=" + std::to_string(static_cast<int32_t>(codecCaps.maxLevel)) + ")");
                            addProperty(prefix + "codedExtent", std::to_string(caps.minCodedExtent.width) + " × " + std::to_string(caps.minCodedExtent.height) + " .. " + std::to_string(caps.maxCodedExtent.width) + " × " + std::to_string(caps.maxCodedExtent.height));
                            addProperty(prefix + "DPB", std::to_string(caps.maxDpbSlots) + " slots / " + std::to_string(caps.maxActiveReferencePictures) + " active refs");
                        }
                    } else {
                        VkVideoDecodeAV1ProfileInfoKHR codec{};
                        codec.sType = static_cast<VkStructureType>(profileSType); codec.pNext = nullptr; codec.stdProfile = static_cast<StdVideoAV1Profile>(profileValue); codec.filmGrainSupport = static_cast<VkBool32>(auxiliaryValue);
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
                        const std::string prefix = std::string("Video · ") + name + " · " + variant + " ";
                        addProperty(prefix + "status", exactProfileStatus(r));
                        if (r == VK_SUCCESS) {
                            addProperty(prefix + "maxLevel", std::string(videoAV1LevelName(static_cast<int32_t>(codecCaps.maxLevel))) + " (raw=" + std::to_string(static_cast<int32_t>(codecCaps.maxLevel)) + ")");
                            addProperty(prefix + "codedExtent", std::to_string(caps.minCodedExtent.width) + " × " + std::to_string(caps.minCodedExtent.height) + " .. " + std::to_string(caps.maxCodedExtent.width) + " × " + std::to_string(caps.maxCodedExtent.height));
                            addProperty(prefix + "DPB", std::to_string(caps.maxDpbSlots) + " slots / " + std::to_string(caps.maxActiveReferencePictures) + " active refs");
                        }
                    }
                };

                auto emitEncode = [&](const char* name, const std::string& variant, const char* extension, VkVideoCodecOperationFlagsKHR operation, uint32_t profileSType, uint32_t profileValue) {
                    if (!hasExt(extension) || !hasExt("VK_KHR_video_encode_queue")) {
                        addProperty(std::string("Video · ") + name + " · " + variant, deviceExtensionsComplete ? "Not applicable: required video encode extension was not enumerated." : "Unknown: device-extension enumeration is incomplete or unavailable, so required video encode extension absence cannot be established.");
                        return;
                    }
                    VkVideoProfileInfoKHR profile = makeBaseProfile(operation);
                    VkVideoEncodeH264ProfileInfoKHR h264Profile{};
                    VkVideoEncodeH265ProfileInfoKHR h265Profile{};
                    VkVideoEncodeAV1ProfileInfoKHR av1Profile{};
                    VkVideoEncodeH264CapabilitiesKHR h264Caps{};
                    VkVideoEncodeH265CapabilitiesKHR h265Caps{};
                    VkVideoEncodeAV1CapabilitiesKHR av1Caps{};
                    void* codecCaps = nullptr;
                    if (std::strcmp(name, "H.264 encode") == 0) {
                        h264Profile.sType = static_cast<VkStructureType>(profileSType); h264Profile.pNext = nullptr; h264Profile.stdProfileIdc = static_cast<StdVideoH264ProfileIdc>(profileValue);
                        h264Caps.sType = VK_STRUCTURE_TYPE_VIDEO_ENCODE_H264_CAPABILITIES_KHR; h264Caps.pNext = nullptr;
                        profile.pNext = &h264Profile;
                        codecCaps = &h264Caps;
                    } else if (std::strcmp(name, "H.265 encode") == 0) {
                        h265Profile.sType = static_cast<VkStructureType>(profileSType); h265Profile.pNext = nullptr; h265Profile.stdProfileIdc = static_cast<StdVideoH265ProfileIdc>(profileValue);
                        h265Caps.sType = VK_STRUCTURE_TYPE_VIDEO_ENCODE_H265_CAPABILITIES_KHR; h265Caps.pNext = nullptr;
                        profile.pNext = &h265Profile;
                        codecCaps = &h265Caps;
                    } else {
                        av1Profile.sType = static_cast<VkStructureType>(profileSType); av1Profile.pNext = nullptr; av1Profile.stdProfile = static_cast<StdVideoAV1Profile>(profileValue);
                        av1Caps.sType = VK_STRUCTURE_TYPE_VIDEO_ENCODE_AV1_CAPABILITIES_KHR; av1Caps.pNext = nullptr;
                        profile.pNext = &av1Profile;
                        codecCaps = &av1Caps;
                    }
                    VkVideoEncodeCapabilitiesKHR encodeCaps{};
                    encodeCaps.sType = VK_STRUCTURE_TYPE_VIDEO_ENCODE_CAPABILITIES_KHR;
                    encodeCaps.pNext = codecCaps;
                    VkVideoCapabilitiesKHR caps{};
                    caps.sType = VK_STRUCTURE_TYPE_VIDEO_CAPABILITIES_KHR;
                    caps.pNext = &encodeCaps;
                    const VkResult r = api.getPhysicalDeviceVideoCapabilitiesKHR(devices[i], &profile, &caps);
                    const std::string prefix = std::string("Video · ") + name + " · " + variant + " ";
                    addProperty(prefix + "status", exactProfileStatus(r));
                    if (r == VK_SUCCESS) {
                        addProperty(prefix + "capabilityFlags", std::to_string(caps.flags));
                        addProperty(prefix + "bitstreamAlignment", std::to_string(caps.minBitstreamBufferOffsetAlignment) + " offset / " + std::to_string(caps.minBitstreamBufferSizeAlignment) + " size");
                        addProperty(prefix + "pictureAccessGranularity", std::to_string(caps.pictureAccessGranularity.width) + " × " + std::to_string(caps.pictureAccessGranularity.height));
                        addProperty(prefix + "codedExtent", std::to_string(caps.minCodedExtent.width) + " × " + std::to_string(caps.minCodedExtent.height) + " .. " + std::to_string(caps.maxCodedExtent.width) + " × " + std::to_string(caps.maxCodedExtent.height));
                        addProperty(prefix + "DPB", std::to_string(caps.maxDpbSlots) + " slots / " + std::to_string(caps.maxActiveReferencePictures) + " active refs");
                        addProperty(prefix + "stdHeader", std::string(caps.stdHeaderVersion.extensionName) + " " + std::to_string(caps.stdHeaderVersion.specVersion));
                        addProperty(prefix + "encodeFlags", std::to_string(encodeCaps.flags));
                        addProperty(prefix + "rateControlModes", std::to_string(encodeCaps.rateControlModes));
                        addProperty(prefix + "maxRateControlLayers", std::to_string(encodeCaps.maxRateControlLayers));
                        addProperty(prefix + "maxBitrate", std::to_string(encodeCaps.maxBitrate));
                        addProperty(prefix + "maxQualityLevels", std::to_string(encodeCaps.maxQualityLevels));
                        addProperty(prefix + "encodeInputPictureGranularity", std::to_string(encodeCaps.encodeInputPictureGranularity.width) + " × " + std::to_string(encodeCaps.encodeInputPictureGranularity.height));
                        addProperty(prefix + "supportedEncodeFeedbackFlags", std::to_string(encodeCaps.supportedEncodeFeedbackFlags));
                        if (std::strcmp(name, "H.264 encode") == 0) {
                            addProperty(prefix + "codecFlags", std::to_string(h264Caps.flags));
                            addProperty(prefix + "maxLevelIdc", std::string(videoH264LevelName(static_cast<int32_t>(h264Caps.maxLevelIdc))) + " (raw=" + std::to_string(static_cast<int32_t>(h264Caps.maxLevelIdc)) + ")");
                            addProperty(prefix + "maxSliceCount", std::to_string(h264Caps.maxSliceCount));
                            addProperty(prefix + "maxPPictureL0ReferenceCount", std::to_string(h264Caps.maxPPictureL0ReferenceCount));
                            addProperty(prefix + "maxBPictureL0ReferenceCount", std::to_string(h264Caps.maxBPictureL0ReferenceCount));
                            addProperty(prefix + "maxL1ReferenceCount", std::to_string(h264Caps.maxL1ReferenceCount));
                            addProperty(prefix + "maxTemporalLayerCount", std::to_string(h264Caps.maxTemporalLayerCount));
                            addProperty(prefix + "expectDyadicTemporalLayerPattern", h264Caps.expectDyadicTemporalLayerPattern == VK_TRUE ? "true" : "false");
                            addProperty(prefix + "qpRange", std::to_string(h264Caps.minQp) + " .. " + std::to_string(h264Caps.maxQp));
                            addProperty(prefix + "prefersGopRemainingFrames", h264Caps.prefersGopRemainingFrames == VK_TRUE ? "true" : "false");
                            addProperty(prefix + "requiresGopRemainingFrames", h264Caps.requiresGopRemainingFrames == VK_TRUE ? "true" : "false");
                            addProperty(prefix + "stdSyntaxFlags", std::to_string(h264Caps.stdSyntaxFlags));
                        } else if (std::strcmp(name, "H.265 encode") == 0) {
                            addProperty(prefix + "codecFlags", std::to_string(h265Caps.flags));
                            addProperty(prefix + "maxLevelIdc", std::string(videoH265LevelName(static_cast<int32_t>(h265Caps.maxLevelIdc))) + " (raw=" + std::to_string(static_cast<int32_t>(h265Caps.maxLevelIdc)) + ")");
                            addProperty(prefix + "maxSliceSegmentCount", std::to_string(h265Caps.maxSliceSegmentCount));
                            addProperty(prefix + "maxTiles", std::to_string(h265Caps.maxTiles.width) + " × " + std::to_string(h265Caps.maxTiles.height));
                            addProperty(prefix + "ctbSizes", std::to_string(h265Caps.ctbSizes));
                            addProperty(prefix + "transformBlockSizes", std::to_string(h265Caps.transformBlockSizes));
                            addProperty(prefix + "maxPPictureL0ReferenceCount", std::to_string(h265Caps.maxPPictureL0ReferenceCount));
                            addProperty(prefix + "maxBPictureL0ReferenceCount", std::to_string(h265Caps.maxBPictureL0ReferenceCount));
                            addProperty(prefix + "maxL1ReferenceCount", std::to_string(h265Caps.maxL1ReferenceCount));
                            addProperty(prefix + "maxSubLayerCount", std::to_string(h265Caps.maxSubLayerCount));
                            addProperty(prefix + "expectDyadicTemporalSubLayerPattern", h265Caps.expectDyadicTemporalSubLayerPattern == VK_TRUE ? "true" : "false");
                            addProperty(prefix + "qpRange", std::to_string(h265Caps.minQp) + " .. " + std::to_string(h265Caps.maxQp));
                            addProperty(prefix + "prefersGopRemainingFrames", h265Caps.prefersGopRemainingFrames == VK_TRUE ? "true" : "false");
                            addProperty(prefix + "requiresGopRemainingFrames", h265Caps.requiresGopRemainingFrames == VK_TRUE ? "true" : "false");
                            addProperty(prefix + "stdSyntaxFlags", std::to_string(h265Caps.stdSyntaxFlags));
                        } else {
                            addProperty(prefix + "codecFlags", std::to_string(av1Caps.flags));
                            addProperty(prefix + "maxLevel", std::string(videoAV1LevelName(static_cast<int32_t>(av1Caps.maxLevel))) + " (raw=" + std::to_string(static_cast<int32_t>(av1Caps.maxLevel)) + ")");
                            addProperty(prefix + "codedPictureAlignment", std::to_string(av1Caps.codedPictureAlignment.width) + " × " + std::to_string(av1Caps.codedPictureAlignment.height));
                            addProperty(prefix + "maxTiles", std::to_string(av1Caps.maxTiles.width) + " × " + std::to_string(av1Caps.maxTiles.height));
                            addProperty(prefix + "minTileSize", std::to_string(av1Caps.minTileSize.width) + " × " + std::to_string(av1Caps.minTileSize.height));
                            addProperty(prefix + "maxTileSize", std::to_string(av1Caps.maxTileSize.width) + " × " + std::to_string(av1Caps.maxTileSize.height));
                            addProperty(prefix + "superblockSizes", std::to_string(av1Caps.superblockSizes));
                            addProperty(prefix + "maxSingleReferenceCount", std::to_string(av1Caps.maxSingleReferenceCount));
                            addProperty(prefix + "singleReferenceNameMask", std::to_string(av1Caps.singleReferenceNameMask));
                            addProperty(prefix + "maxUnidirectionalCompoundReferenceCount", std::to_string(av1Caps.maxUnidirectionalCompoundReferenceCount));
                            addProperty(prefix + "maxUnidirectionalCompoundGroup1ReferenceCount", std::to_string(av1Caps.maxUnidirectionalCompoundGroup1ReferenceCount));
                            addProperty(prefix + "unidirectionalCompoundReferenceNameMask", std::to_string(av1Caps.unidirectionalCompoundReferenceNameMask));
                            addProperty(prefix + "maxBidirectionalCompoundReferenceCount", std::to_string(av1Caps.maxBidirectionalCompoundReferenceCount));
                            addProperty(prefix + "maxBidirectionalCompoundGroup1ReferenceCount", std::to_string(av1Caps.maxBidirectionalCompoundGroup1ReferenceCount));
                            addProperty(prefix + "maxBidirectionalCompoundGroup2ReferenceCount", std::to_string(av1Caps.maxBidirectionalCompoundGroup2ReferenceCount));
                            addProperty(prefix + "bidirectionalCompoundReferenceNameMask", std::to_string(av1Caps.bidirectionalCompoundReferenceNameMask));
                            addProperty(prefix + "maxTemporalLayerCount", std::to_string(av1Caps.maxTemporalLayerCount));
                            addProperty(prefix + "maxSpatialLayerCount", std::to_string(av1Caps.maxSpatialLayerCount));
                            addProperty(prefix + "maxOperatingPoints", std::to_string(av1Caps.maxOperatingPoints));
                            addProperty(prefix + "qIndexRange", std::to_string(av1Caps.minQIndex) + " .. " + std::to_string(av1Caps.maxQIndex));
                            addProperty(prefix + "prefersGopRemainingFrames", av1Caps.prefersGopRemainingFrames == VK_TRUE ? "true" : "false");
                            addProperty(prefix + "requiresGopRemainingFrames", av1Caps.requiresGopRemainingFrames == VK_TRUE ? "true" : "false");
                            addProperty(prefix + "stdSyntaxFlags", std::to_string(av1Caps.stdSyntaxFlags));
                        }
                    }
                };

                for (const auto& profileEntry : kVideoH264DecodeProfiles) {
                    for (const auto& layoutEntry : kVideoH264PictureLayouts) {
                        emitDecode("H.264 decode", std::string(profileEntry.displayName) + " / " + layoutEntry.displayName + " [" + profileEntry.token + "; " + layoutEntry.token + "]", "VK_KHR_video_decode_h264", VK_VIDEO_CODEC_OPERATION_DECODE_H264_BIT_KHR, VK_STRUCTURE_TYPE_VIDEO_DECODE_H264_PROFILE_INFO_KHR, static_cast<uint32_t>(profileEntry.value), VK_STRUCTURE_TYPE_VIDEO_DECODE_H264_CAPABILITIES_KHR, layoutEntry.value);
                    }
                }
                for (const auto& profileEntry : kVideoH265DecodeProfiles) emitDecode("H.265 decode", std::string(profileEntry.displayName) + " [" + profileEntry.token + "]", "VK_KHR_video_decode_h265", VK_VIDEO_CODEC_OPERATION_DECODE_H265_BIT_KHR, VK_STRUCTURE_TYPE_VIDEO_DECODE_H265_PROFILE_INFO_KHR, static_cast<uint32_t>(profileEntry.value), VK_STRUCTURE_TYPE_VIDEO_DECODE_H265_CAPABILITIES_KHR, 0);
                for (const auto& profileEntry : kVideoVP9DecodeProfiles) emitDecode("VP9 decode", std::string(profileEntry.displayName) + " [" + profileEntry.token + "]", "VK_KHR_video_decode_vp9", VK_VIDEO_CODEC_OPERATION_DECODE_VP9_BIT_KHR, VK_STRUCTURE_TYPE_VIDEO_DECODE_VP9_PROFILE_INFO_KHR, static_cast<uint32_t>(profileEntry.value), VK_STRUCTURE_TYPE_VIDEO_DECODE_VP9_CAPABILITIES_KHR, 0);
                for (const auto& profileEntry : kVideoAV1DecodeProfiles) {
                    for (const auto& filmGrainEntry : kVideoAV1FilmGrainModes) {
                        emitDecode("AV1 decode", std::string(profileEntry.displayName) + " / " + filmGrainEntry.displayName + " [" + profileEntry.token + "; " + filmGrainEntry.token + "]", "VK_KHR_video_decode_av1", VK_VIDEO_CODEC_OPERATION_DECODE_AV1_BIT_KHR, VK_STRUCTURE_TYPE_VIDEO_DECODE_AV1_PROFILE_INFO_KHR, static_cast<uint32_t>(profileEntry.value), VK_STRUCTURE_TYPE_VIDEO_DECODE_AV1_CAPABILITIES_KHR, filmGrainEntry.value);
                    }
                }
                for (const auto& profileEntry : kVideoH264EncodeProfiles) emitEncode("H.264 encode", std::string(profileEntry.displayName) + " [" + profileEntry.token + "]", "VK_KHR_video_encode_h264", VK_VIDEO_CODEC_OPERATION_ENCODE_H264_BIT_KHR, VK_STRUCTURE_TYPE_VIDEO_ENCODE_H264_PROFILE_INFO_KHR, static_cast<uint32_t>(profileEntry.value));
                for (const auto& profileEntry : kVideoH265EncodeProfiles) emitEncode("H.265 encode", std::string(profileEntry.displayName) + " [" + profileEntry.token + "]", "VK_KHR_video_encode_h265", VK_VIDEO_CODEC_OPERATION_ENCODE_H265_BIT_KHR, VK_STRUCTURE_TYPE_VIDEO_ENCODE_H265_PROFILE_INFO_KHR, static_cast<uint32_t>(profileEntry.value));
                for (const auto& profileEntry : kVideoAV1EncodeProfiles) emitEncode("AV1 encode", std::string(profileEntry.displayName) + " [" + profileEntry.token + "]", "VK_KHR_video_encode_av1", VK_VIDEO_CODEC_OPERATION_ENCODE_AV1_BIT_KHR, VK_STRUCTURE_TYPE_VIDEO_ENCODE_AV1_PROFILE_INFO_KHR, static_cast<uint32_t>(profileEntry.value));
                }

                if (api.getPhysicalDeviceVideoFormatPropertiesKHR) {
                    auto videoFormatFailureStatus = [](VkResult result) {
                        const bool unsupported = result == VK_ERROR_IMAGE_USAGE_NOT_SUPPORTED_KHR || result == VK_ERROR_VIDEO_PICTURE_LAYOUT_NOT_SUPPORTED_KHR || result == VK_ERROR_VIDEO_PROFILE_OPERATION_NOT_SUPPORTED_KHR || result == VK_ERROR_VIDEO_PROFILE_FORMAT_NOT_SUPPORTED_KHR || result == VK_ERROR_VIDEO_PROFILE_CODEC_NOT_SUPPORTED_KHR;
                        return std::string(unsupported ? "Unsupported for sampled profile/usage (VkResult=" : "Unavailable (VkResult=") + std::to_string(result) + ")";
                    };
                    auto enumerateVideoFormats = [&](const std::string& label, VkVideoProfileInfoKHR& profile, VkImageUsageFlags usage) {
                        VkVideoProfileListInfoKHR list{VK_STRUCTURE_TYPE_VIDEO_PROFILE_LIST_INFO_KHR, nullptr, 1, &profile};
                        VkPhysicalDeviceVideoFormatInfoKHR info{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VIDEO_FORMAT_INFO_KHR, &list, usage};
                        uint32_t formatCount = 0;
                        VkResult r = api.getPhysicalDeviceVideoFormatPropertiesKHR(devices[i], &info, &formatCount, nullptr);
                        if (r == VK_INCOMPLETE) markAdvancedIncomplete("vkGetPhysicalDeviceVideoFormatPropertiesKHR count query returned VK_INCOMPLETE for at least one sampled profile/usage; absence is not proven.");
                        if (r != VK_SUCCESS && r != VK_INCOMPLETE) {
                            addProperty(label, videoFormatFailureStatus(r));
                            return;
                        }
                        if (formatCount == 0) {
                            addProperty(label, r == VK_SUCCESS ? "Available: zero matching video formats reported for this sampled profile and usage." : "Incomplete: VK_INCOMPLETE returned zero entries; absence is not proven.");
                            return;
                        }
                        if (formatCount > kMaxVideoFormatEntries) {
                            addProperty(label, "Unavailable: result count exceeds safety limit.");
                            return;
                        }
                        std::vector<VkVideoFormatPropertiesKHR> formats(formatCount);
                        for (auto& f : formats) { f.sType = VK_STRUCTURE_TYPE_VIDEO_FORMAT_PROPERTIES_KHR; f.pNext = nullptr; }
                        const size_t formatCapacity = formats.size();
                        r = api.getPhysicalDeviceVideoFormatPropertiesKHR(devices[i], &info, &formatCount, formats.data());
                        if (r == VK_INCOMPLETE) markAdvancedIncomplete("vkGetPhysicalDeviceVideoFormatPropertiesKHR data query returned VK_INCOMPLETE for at least one sampled profile/usage; bounded partial format evidence was retained.");
                        if (r != VK_SUCCESS && r != VK_INCOMPLETE) {
                            addProperty(label, videoFormatFailureStatus(r));
                            return;
                        }
                        if (formatCount > formatCapacity) {
                            addProperty(label, "Unavailable: data query count exceeded the bounded allocation.");
                            return;
                        }
                        formats.resize(formatCount);
                        std::ostringstream values;
                        if (r == VK_INCOMPLETE) values << "Partial: VK_INCOMPLETE; returned entries are positive evidence only. ";
                        if (formatCount == 0 && r == VK_SUCCESS) values << "Available: zero matching video formats reported for this sampled profile and usage.";
                        else if (formatCount == 0) values << "Incomplete: VK_INCOMPLETE returned zero entries; absence is not proven.";
                        for (uint32_t fi = 0; fi < formatCount; ++fi) {
                            if (fi) values << "; ";
                            values << formatName(formats[fi].format) << " (imageUsageFlags=0x" << std::hex << formats[fi].imageUsageFlags << ", imageCreateFlags=0x" << formats[fi].imageCreateFlags << std::dec << ")";
                        }
                        addProperty(label, values.str());
                    };
                    auto queryDecodeFormats = [&](const char* name, VkVideoCodecOperationFlagsKHR operation, const char* extension, uint32_t codecSType, uint32_t profileValue) {
                        const std::string prefix = std::string("Video formats · ") + name + " decode sampled-profile · ";
                        if (!hasExt(extension) || !hasExt("VK_KHR_video_decode_queue")) {
                            const std::string value = deviceExtensionsComplete ? "Not applicable: required video decode extension was not enumerated." : "Unknown: device-extension enumeration is incomplete or unavailable, so required video decode extension absence cannot be established.";
                            addProperty(prefix + "VK_IMAGE_USAGE_VIDEO_DECODE_DST_BIT_KHR", value);
                            addProperty(prefix + "VK_IMAGE_USAGE_VIDEO_DECODE_DPB_BIT_KHR", value);
                            return;
                        }
                        VkVideoProfileInfoKHR profile = makeBaseProfile(operation);
                        VkVideoDecodeH264ProfileInfoKHR h264Profile{};
                        VkVideoDecodeH265ProfileInfoKHR h265Profile{};
                        VkVideoDecodeVP9ProfileInfoKHR vp9Profile{};
                        VkVideoDecodeAV1ProfileInfoKHR av1Profile{};
                        if (std::strcmp(name, "H.264") == 0) {
                            h264Profile.sType = static_cast<VkStructureType>(codecSType); h264Profile.pNext = nullptr; h264Profile.stdProfileIdc = static_cast<StdVideoH264ProfileIdc>(profileValue); h264Profile.pictureLayout = VK_VIDEO_DECODE_H264_PICTURE_LAYOUT_PROGRESSIVE_KHR; profile.pNext = &h264Profile;
                        } else if (std::strcmp(name, "H.265") == 0) {
                            h265Profile.sType = static_cast<VkStructureType>(codecSType); h265Profile.pNext = nullptr; h265Profile.stdProfileIdc = static_cast<StdVideoH265ProfileIdc>(profileValue); profile.pNext = &h265Profile;
                        } else if (std::strcmp(name, "VP9") == 0) {
                            vp9Profile.sType = static_cast<VkStructureType>(codecSType); vp9Profile.pNext = nullptr; vp9Profile.stdProfile = static_cast<StdVideoVP9Profile>(profileValue); profile.pNext = &vp9Profile;
                        } else {
                            av1Profile.sType = static_cast<VkStructureType>(codecSType); av1Profile.pNext = nullptr; av1Profile.stdProfile = static_cast<StdVideoAV1Profile>(profileValue); av1Profile.filmGrainSupport = VK_FALSE; profile.pNext = &av1Profile;
                        }
                        enumerateVideoFormats(prefix + "VK_IMAGE_USAGE_VIDEO_DECODE_DST_BIT_KHR", profile, VK_IMAGE_USAGE_VIDEO_DECODE_DST_BIT_KHR);
                        enumerateVideoFormats(prefix + "VK_IMAGE_USAGE_VIDEO_DECODE_DPB_BIT_KHR", profile, VK_IMAGE_USAGE_VIDEO_DECODE_DPB_BIT_KHR);
                    };
                    auto queryEncodeFormats = [&](const char* name, VkVideoCodecOperationFlagsKHR operation, const char* extension, uint32_t codecSType, uint32_t profileValue) {
                        const std::string prefix = std::string("Video formats · ") + name + " encode sampled-profile · ";
                        if (!hasExt(extension) || !hasExt("VK_KHR_video_encode_queue")) {
                            const std::string value = deviceExtensionsComplete ? "Not applicable: required video encode extension was not enumerated." : "Unknown: device-extension enumeration is incomplete or unavailable, so required video encode extension absence cannot be established.";
                            addProperty(prefix + "VK_IMAGE_USAGE_VIDEO_ENCODE_SRC_BIT_KHR", value);
                            addProperty(prefix + "VK_IMAGE_USAGE_VIDEO_ENCODE_DPB_BIT_KHR", value);
                            return;
                        }
                        VkVideoProfileInfoKHR profile = makeBaseProfile(operation);
                        VkVideoEncodeH264ProfileInfoKHR h264Profile{};
                        VkVideoEncodeH265ProfileInfoKHR h265Profile{};
                        VkVideoEncodeAV1ProfileInfoKHR av1Profile{};
                        if (std::strcmp(name, "H.264") == 0) {
                            h264Profile.sType = static_cast<VkStructureType>(codecSType); h264Profile.pNext = nullptr; h264Profile.stdProfileIdc = static_cast<StdVideoH264ProfileIdc>(profileValue); profile.pNext = &h264Profile;
                        } else if (std::strcmp(name, "H.265") == 0) {
                            h265Profile.sType = static_cast<VkStructureType>(codecSType); h265Profile.pNext = nullptr; h265Profile.stdProfileIdc = static_cast<StdVideoH265ProfileIdc>(profileValue); profile.pNext = &h265Profile;
                        } else {
                            av1Profile.sType = static_cast<VkStructureType>(codecSType); av1Profile.pNext = nullptr; av1Profile.stdProfile = static_cast<StdVideoAV1Profile>(profileValue); profile.pNext = &av1Profile;
                        }
                        enumerateVideoFormats(prefix + "VK_IMAGE_USAGE_VIDEO_ENCODE_SRC_BIT_KHR", profile, VK_IMAGE_USAGE_VIDEO_ENCODE_SRC_BIT_KHR);
                        enumerateVideoFormats(prefix + "VK_IMAGE_USAGE_VIDEO_ENCODE_DPB_BIT_KHR", profile, VK_IMAGE_USAGE_VIDEO_ENCODE_DPB_BIT_KHR);
                    };
                    queryDecodeFormats("H.264", VK_VIDEO_CODEC_OPERATION_DECODE_H264_BIT_KHR, "VK_KHR_video_decode_h264", VK_STRUCTURE_TYPE_VIDEO_DECODE_H264_PROFILE_INFO_KHR, STD_VIDEO_H264_PROFILE_IDC_BASELINE);
                    queryDecodeFormats("H.265", VK_VIDEO_CODEC_OPERATION_DECODE_H265_BIT_KHR, "VK_KHR_video_decode_h265", VK_STRUCTURE_TYPE_VIDEO_DECODE_H265_PROFILE_INFO_KHR, STD_VIDEO_H265_PROFILE_IDC_MAIN);
                    queryDecodeFormats("VP9", VK_VIDEO_CODEC_OPERATION_DECODE_VP9_BIT_KHR, "VK_KHR_video_decode_vp9", VK_STRUCTURE_TYPE_VIDEO_DECODE_VP9_PROFILE_INFO_KHR, STD_VIDEO_VP9_PROFILE_0);
                    queryDecodeFormats("AV1", VK_VIDEO_CODEC_OPERATION_DECODE_AV1_BIT_KHR, "VK_KHR_video_decode_av1", VK_STRUCTURE_TYPE_VIDEO_DECODE_AV1_PROFILE_INFO_KHR, STD_VIDEO_AV1_PROFILE_MAIN);
                    queryEncodeFormats("H.264", VK_VIDEO_CODEC_OPERATION_ENCODE_H264_BIT_KHR, "VK_KHR_video_encode_h264", VK_STRUCTURE_TYPE_VIDEO_ENCODE_H264_PROFILE_INFO_KHR, STD_VIDEO_H264_PROFILE_IDC_MAIN);
                    queryEncodeFormats("H.265", VK_VIDEO_CODEC_OPERATION_ENCODE_H265_BIT_KHR, "VK_KHR_video_encode_h265", VK_STRUCTURE_TYPE_VIDEO_ENCODE_H265_PROFILE_INFO_KHR, STD_VIDEO_H265_PROFILE_IDC_MAIN);
                    queryEncodeFormats("AV1", VK_VIDEO_CODEC_OPERATION_ENCODE_AV1_BIT_KHR, "VK_KHR_video_encode_av1", VK_STRUCTURE_TYPE_VIDEO_ENCODE_AV1_PROFILE_INFO_KHR, STD_VIDEO_AV1_PROFILE_MAIN);
                } else {
                    addProperty("Video format query", "Unavailable: vkGetPhysicalDeviceVideoFormatPropertiesKHR is unavailable in this Vulkan stack.");
                }
            }
            out << ",\"properties\":[";
            for (size_t propertyIndex = 0; propertyIndex < videoProperties.size(); ++propertyIndex) {
                if (propertyIndex) out << ',';
                out << "{\"section\":\"Vulkan Video\",\"name\":" << jsonString(videoProperties[propertyIndex].first) << ",\"value\":" << jsonString(videoProperties[propertyIndex].second) << '}';
            }
            out << "]}";
        } else if (group && std::strcmp(group, "external") == 0) {
            addDevicePrefix(i);
            out << ",\"properties\":[";
            bool firstExternal = true;
            auto emitExternalState = [&](const char* name, const std::string& value) {
                if (!firstExternal) out << ',';
                firstExternal = false;
                out << "{\"section\":\"External Capabilities\",\"name\":" << jsonString(name) << ",\"value\":" << jsonString(value) << '}';
            };
            const bool deviceExtensionsComplete = std::strcmp(deviceExtensionEnumeration.status, "available") == 0;
            auto prerequisiteState = [&](const char* extensionName) {
                return deviceExtensionsComplete ? std::string("Not applicable: ") + extensionName + " was not enumerated for this device." : "Unknown: device-extension enumeration is incomplete or unavailable, so extension absence cannot be established.";
            };
            auto queryExternalBuffer = [&](const char* extensionName, VkExternalMemoryHandleTypeFlagBits handleType, const char* handleName) {
                if (!hasExt(extensionName)) { emitExternalState(handleName, prerequisiteState(extensionName)); return; }
                if (!api.getPhysicalDeviceExternalBufferProperties) { emitExternalState(handleName, "Unavailable: vkGetPhysicalDeviceExternalBufferProperties entry point is unavailable."); return; }
                VkPhysicalDeviceExternalBufferInfo info{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_BUFFER_INFO, nullptr, 0, VK_BUFFER_USAGE_TRANSFER_SRC_BIT | VK_BUFFER_USAGE_TRANSFER_DST_BIT | VK_BUFFER_USAGE_STORAGE_BUFFER_BIT, handleType};
                VkExternalBufferProperties properties{VK_STRUCTURE_TYPE_EXTERNAL_BUFFER_PROPERTIES, nullptr, {}};
                api.getPhysicalDeviceExternalBufferProperties(devices[i], &info, &properties);
                std::ostringstream value;
                value << "features=0x" << std::hex << properties.externalMemoryProperties.externalMemoryFeatures << ", export=0x" << properties.externalMemoryProperties.exportFromImportedHandleTypes << ", compatible=0x" << properties.externalMemoryProperties.compatibleHandleTypes;
                emitExternalState(handleName, value.str());
            };
            auto queryExternalFence = [&](VkExternalFenceHandleTypeFlagBits handleType, const char* handleName) {
                if (!hasExt("VK_KHR_external_fence_fd")) { emitExternalState(handleName, prerequisiteState("VK_KHR_external_fence_fd")); return; }
                if (!api.getPhysicalDeviceExternalFenceProperties) { emitExternalState(handleName, "Unavailable: vkGetPhysicalDeviceExternalFenceProperties entry point is unavailable."); return; }
                VkPhysicalDeviceExternalFenceInfo info{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_FENCE_INFO, nullptr, handleType};
                VkExternalFenceProperties properties{VK_STRUCTURE_TYPE_EXTERNAL_FENCE_PROPERTIES, nullptr, 0, 0, 0};
                api.getPhysicalDeviceExternalFenceProperties(devices[i], &info, &properties);
                std::ostringstream value;
                value << "features=0x" << std::hex << properties.externalFenceFeatures << ", export=0x" << properties.exportFromImportedHandleTypes << ", compatible=0x" << properties.compatibleHandleTypes;
                emitExternalState(handleName, value.str());
            };
            auto queryExternalSemaphore = [&](VkExternalSemaphoreHandleTypeFlagBits handleType, const char* handleName) {
                if (!hasExt("VK_KHR_external_semaphore_fd")) { emitExternalState(handleName, prerequisiteState("VK_KHR_external_semaphore_fd")); return; }
                if (!api.getPhysicalDeviceExternalSemaphoreProperties) { emitExternalState(handleName, "Unavailable: vkGetPhysicalDeviceExternalSemaphoreProperties entry point is unavailable."); return; }
                VkPhysicalDeviceExternalSemaphoreInfo info{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTERNAL_SEMAPHORE_INFO, nullptr, handleType};
                VkExternalSemaphoreProperties properties{VK_STRUCTURE_TYPE_EXTERNAL_SEMAPHORE_PROPERTIES, nullptr, 0, 0, 0};
                api.getPhysicalDeviceExternalSemaphoreProperties(devices[i], &info, &properties);
                std::ostringstream value;
                value << "features=0x" << std::hex << properties.externalSemaphoreFeatures << ", export=0x" << properties.exportFromImportedHandleTypes << ", compatible=0x" << properties.compatibleHandleTypes;
                emitExternalState(handleName, value.str());
            };
            queryExternalBuffer("VK_KHR_external_memory_fd", VK_EXTERNAL_MEMORY_HANDLE_TYPE_OPAQUE_FD_BIT, "VK_EXTERNAL_MEMORY_HANDLE_TYPE_OPAQUE_FD_BIT");
            queryExternalBuffer("VK_EXT_external_memory_dma_buf", VK_EXTERNAL_MEMORY_HANDLE_TYPE_DMA_BUF_BIT_EXT, "VK_EXTERNAL_MEMORY_HANDLE_TYPE_DMA_BUF_BIT_EXT");
            queryExternalBuffer("VK_ANDROID_external_memory_android_hardware_buffer", VK_EXTERNAL_MEMORY_HANDLE_TYPE_ANDROID_HARDWARE_BUFFER_BIT_ANDROID, "VK_EXTERNAL_MEMORY_HANDLE_TYPE_ANDROID_HARDWARE_BUFFER_BIT_ANDROID");
            queryExternalFence(VK_EXTERNAL_FENCE_HANDLE_TYPE_OPAQUE_FD_BIT, "VK_EXTERNAL_FENCE_HANDLE_TYPE_OPAQUE_FD_BIT");
            queryExternalFence(VK_EXTERNAL_FENCE_HANDLE_TYPE_SYNC_FD_BIT, "VK_EXTERNAL_FENCE_HANDLE_TYPE_SYNC_FD_BIT");
            queryExternalSemaphore(VK_EXTERNAL_SEMAPHORE_HANDLE_TYPE_OPAQUE_FD_BIT, "VK_EXTERNAL_SEMAPHORE_HANDLE_TYPE_OPAQUE_FD_BIT");
            queryExternalSemaphore(VK_EXTERNAL_SEMAPHORE_HANDLE_TYPE_SYNC_FD_BIT, "VK_EXTERNAL_SEMAPHORE_HANDLE_TYPE_SYNC_FD_BIT");
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
                    out << "{\"section\":\"Sparse Image Format Properties2\",\"name\":" << jsonString(formatName(fmt)) << ",\"value\":" << jsonString("Unavailable: property count exceeds safety limit.") << '}';
                } else if (pc) {
                    const size_t sparseCapacity = props.size();
                    api.getPhysicalDeviceSparseImageFormatProperties2(devices[i], &info, &pc, props.data());
                    if (pc > sparseCapacity) {
                        if (!firstProp) out << ',';
                        firstProp = false;
                        out << "{\"section\":\"Sparse Image Format Properties2\",\"name\":" << jsonString(formatName(fmt)) << ",\"value\":" << jsonString("Unavailable: data query count exceeded the bounded allocation.") << '}';
                    } else {
                        for (uint32_t k = 0; k < pc; ++k) {
                            if (!firstProp) out << ','; firstProp = false;
                            const auto& x = props[k].properties;
                            std::ostringstream value; value << "aspectMask=0x" << std::hex << x.aspectMask << ", granularity=" << std::dec << x.imageGranularity.width << " × " << x.imageGranularity.height << " × " << x.imageGranularity.depth << ", flags=0x" << std::hex << x.flags;
                            out << "{\"section\":\"Sparse Image Format Properties2\",\"name\":" << jsonString(formatName(fmt) + " #" + std::to_string(k)) << ",\"value\":" << jsonString(value.str()) << '}';
                        }
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
            out << ",\"properties\":[]}";
        } else {
            out << "{\"vendorId\":" << vendorId << ",\"deviceId\":" << deviceId << ",\"name\":" << jsonString(name ? name : "Unknown GPU") << "}";
        }
    }
    if (group && std::strcmp(group, "groups") == 0) {
        out << "]";
        if (!physicalDeviceGroups.empty()) {
            out << ",\"groupProperties\":[";
            for (std::size_t g = 0; g < physicalDeviceGroups.size(); ++g) {
                if (g) out << ',';
                std::ostringstream groupValue;
                groupValue << "physicalDeviceCount=" << physicalDeviceGroups[g].physicalDeviceCount << ", subsetAllocation=" << (physicalDeviceGroups[g].subsetAllocation == VK_TRUE ? "true" : "false") << ", devices=";
                for (uint32_t d = 0; d < physicalDeviceGroups[g].physicalDeviceCount && d < VK_MAX_DEVICE_GROUP_SIZE; ++d) {
                    if (d) groupValue << "; ";
                    VkPhysicalDeviceProperties groupProperties{};
                    getDevicePropertiesPrimary(api, physicalDeviceGroups[g].physicalDevices[d], groupProperties);
                    const uint32_t gpVendor = groupProperties.vendorID;
                    const uint32_t gpDevice = groupProperties.deviceID;
                    const char* gpName = groupProperties.deviceName;
                    groupValue << (gpName ? gpName : "Unknown GPU") << " [0x" << std::hex << gpVendor << ":0x" << gpDevice << std::dec << "]";
                }
                out << "{\"section\":\"Physical Device Group\",\"name\":\"Group " << g << "\",\"value\":" << jsonString(groupValue.str()) << '}';
            }
            out << "]";
        }
        out << ",\"groupEnumerationResult\":" << static_cast<int32_t>(physicalDeviceGroupEnumerationResult) << ",\"groupEnumerationComplete\":" << jsonBool(physicalDeviceGroupEnumerationComplete) << "}";
    } else {
        out << "]}";
    }
    api.destroyInstance(instance, nullptr);
    std::string advancedJson = out.str();
    auto replaceAdvancedToken = [&](const std::string& token, const std::string& value) {
        const std::string encodedToken = jsonString(token);
        const std::size_t position = advancedJson.find(encodedToken);
        if (position != std::string::npos) advancedJson.replace(position, encodedToken.size(), jsonString(value));
    };
    replaceAdvancedToken(advancedStatusToken, advancedEnumerationIncomplete ? "incomplete" : "available");
    replaceAdvancedToken(advancedReasonToken, advancedEnumerationReason);
    return advancedJson;
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
    else if (std::strcmp(group, "extendedDynamicStateParity") == 0 || std::strcmp(group, "extendedDynamicState") == 0) { extensionName = "VK_EXT_extended_dynamic_state"; structureType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_EXTENDED_DYNAMIC_STATE_FEATURES_EXT; }
    else if (std::strcmp(group, "privateDataParity") == 0 || std::strcmp(group, "privateData") == 0) { extensionName = "VK_EXT_private_data"; structureType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PRIVATE_DATA_FEATURES; }
    else if (std::strcmp(group, "synchronization2Parity") == 0 || std::strcmp(group, "synchronization2") == 0) { extensionName = "VK_KHR_synchronization2"; structureType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SYNCHRONIZATION_2_FEATURES; }
    else if (std::strcmp(group, "shaderOcpMicroscalingTypes") == 0) { extensionName = "VK_EXT_shader_ocp_microscaling_types"; structureType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_SHADER_OCP_MICROSCALING_TYPES_FEATURES_EXT; }
    else if (std::strcmp(group, "rayQueryParity") == 0 || std::strcmp(group, "rayQuery") == 0 || std::strcmp(group, "VK_KHR_ray_query") == 0 || std::strcmp(group, "VK_KHR_ray_query / rayQuery") == 0) { extensionName = "VK_KHR_ray_query"; structureType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_RAY_QUERY_FEATURES_KHR; }
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
    const VkResult extensionDeviceResult = extensionDevicesResult.result;
    std::vector<VkPhysicalDevice> devs = extensionDevicesResult.values;
    const uint32_t count = static_cast<uint32_t>(devs.size());
    const bool physicalDeviceEnumerationComplete = extensionDevicesResult.complete;
    if (!extensionDevicesResult.resultAvailable || (extensionDevicesResult.safetyRejected && extensionDevicesResult.values.empty())) { api.destroyInstance(inst,nullptr); const std::string reason = extensionDevicesResult.safetyRejected ? std::string("Physical-device enumeration was rejected by a local safety bound. ") + extensionDevicesResult.localReason : extensionDevicesResult.localReason; return std::string("{\"status\":\"unavailable\",\"group\":")+jsonString(group)+",\"extension\":"+jsonString(extensionName)+",\"reason\":"+jsonString(reason)+",\"physicalDeviceEnumerationSafetyRejected\":"+jsonBool(extensionDevicesResult.safetyRejected)+",\"devices\":[]}"; }
    if (extensionDeviceResult != VK_SUCCESS && extensionDeviceResult != VK_INCOMPLETE && extensionDevicesResult.values.empty()) { api.destroyInstance(inst,nullptr); return std::string("{\"status\":\"unavailable\",\"group\":")+jsonString(group)+",\"extension\":"+jsonString(extensionName)+",\"reason\":"+jsonString(std::string("vkEnumeratePhysicalDevices failed. VkResult=")+std::to_string(extensionDeviceResult))+",\"devices\":[]}"; }
    if (count == 0) { api.destroyInstance(inst,nullptr); return physicalDeviceEnumerationComplete ? std::string("{\"status\":\"not_applicable\",\"group\":")+jsonString(group)+",\"extension\":"+jsonString(extensionName)+",\"reason\":\"No physical devices.\",\"devices\":[]}" : std::string("{\"status\":\"incomplete\",\"group\":")+jsonString(group)+",\"extension\":"+jsonString(extensionName)+",\"reason\":\"Physical-device enumeration remained VK_INCOMPLETE and returned no bounded partial device handles.\",\"devices\":[]}"; }
    bool simpleFeatureIncomplete = !physicalDeviceEnumerationComplete;
    std::string simpleFeatureReason = physicalDeviceEnumerationComplete ? "" : (!extensionDevicesResult.localReason.empty() ? extensionDevicesResult.localReason : "Physical-device enumeration remained VK_INCOMPLETE; bounded partial positive evidence was retained.");
    auto markSimpleFeatureIncomplete = [&](const std::string& reason) {
        simpleFeatureIncomplete = true;
        if (!reason.empty() && simpleFeatureReason.find(reason) == std::string::npos) {
            if (!simpleFeatureReason.empty()) simpleFeatureReason += " ";
            simpleFeatureReason += reason;
        }
    };
    const std::string simpleFeatureStatusToken = "__VULKANSCOPE_SIMPLE_FEATURE_STATUS__";
    const std::string simpleFeatureReasonToken = "__VULKANSCOPE_SIMPLE_FEATURE_REASON__";
    std::ostringstream out; out<<"{\"status\":"<<jsonString(simpleFeatureStatusToken)<<",\"group\":"<<jsonString(group)<<",\"extension\":"<<jsonString(extensionName)<<",\"reason\":"<<jsonString(simpleFeatureReasonToken)<<",\"physicalDeviceEnumerationResult\":"<<static_cast<int>(extensionDeviceResult)<<",\"physicalDeviceEnumerationComplete\":"<<jsonBool(physicalDeviceEnumerationComplete)<<",\"devices\":["; bool first=false; bool matchedAny=false; bool extensionEnumerationUncertain=false; bool extensionEnumerationIncomplete=false; std::string extensionEnumerationReason;
    for(uint32_t i=0;i<count;++i){auto extensionEnumeration = enumerateDeviceExtensions(api, devs[i]); const auto& exts = deviceExtensions(extensionEnumeration); if (std::strcmp(extensionEnumeration.status, "available") != 0) { extensionEnumerationUncertain=true; if (std::strcmp(extensionEnumeration.status, "incomplete") == 0) extensionEnumerationIncomplete=true; if (extensionEnumerationReason.empty()) extensionEnumerationReason=extensionEnumeration.reason; markSimpleFeatureIncomplete(extensionEnumeration.reason.empty() ? "Device-extension enumeration was incomplete or unavailable for at least one physical device; extension presence or absence cannot be established there." : extensionEnumeration.reason); } bool supported = std::any_of(exts.begin(), exts.end(), [&](const VkExtensionProperties& e){ return std::strcmp(e.extensionName, extensionName) == 0; }); if (!supported) continue; matchedAny=true; VkPhysicalDeviceProperties physicalProperties{}; getDevicePropertiesPrimary(api, devs[i], physicalProperties); uint32_t vendor=physicalProperties.vendorID, deviceId=physicalProperties.deviceID; const char* name=physicalProperties.deviceName; if(first)out<<',';first=true; out<<"{\"vendorId\":"<<vendor<<",\"deviceId\":"<<deviceId<<",\"name\":"<<jsonString(name?name:"Unknown GPU")<<",\"features\":[";
        if(std::strcmp(group,"maintenance11")==0){VkPhysicalDeviceMaintenance11FeaturesKHR f{static_cast<VkStructureType>(structureType),nullptr,VK_FALSE};VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}};api.queryFeatures2(devs[i],&f2);out<<"{\"name\":\"VK_KHR_maintenance11 / maintenance11\",\"supported\":"<<jsonBool(f.maintenance11==VK_TRUE)<<"}";}
        else if(std::strcmp(group,"deviceAddressCommands")==0){VkPhysicalDeviceDeviceAddressCommandsFeaturesKHR f{static_cast<VkStructureType>(structureType),nullptr,VK_FALSE};VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}};api.queryFeatures2(devs[i],&f2);out<<"{\"name\":\"VK_KHR_device_address_commands / deviceAddressCommands\",\"supported\":"<<jsonBool(f.deviceAddressCommands==VK_TRUE)<<"}";}
        else if(std::strcmp(group,"shaderUniformBufferUnsizedArray")==0){VkPhysicalDeviceShaderUniformBufferUnsizedArrayFeaturesEXT f{static_cast<VkStructureType>(structureType),nullptr,VK_FALSE};VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}};api.queryFeatures2(devs[i],&f2);out<<"{\"name\":\"VK_EXT_shader_uniform_buffer_unsized_array / shaderUniformBufferUnsizedArray\",\"supported\":"<<jsonBool(f.shaderUniformBufferUnsizedArray==VK_TRUE)<<"}";}
        else if(std::strcmp(group,"dataGraphOpticalFlow")==0){VkPhysicalDeviceDataGraphOpticalFlowFeaturesARM f{static_cast<VkStructureType>(structureType),nullptr,VK_FALSE};VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}};api.queryFeatures2(devs[i],&f2);out<<"{\"name\":\"VK_ARM_data_graph_optical_flow / dataGraphOpticalFlow\",\"supported\":"<<jsonBool(f.dataGraphOpticalFlow==VK_TRUE)<<"}";}
        else if(std::strcmp(group,"pipelineCacheIncrementalMode")==0){VkPhysicalDevicePipelineCacheIncrementalModeFeaturesSEC f{static_cast<VkStructureType>(structureType),nullptr,VK_FALSE};VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}};api.queryFeatures2(devs[i],&f2);out<<"{\"name\":\"VK_SEC_pipeline_cache_incremental_mode / pipelineCacheIncrementalMode\",\"supported\":"<<jsonBool(f.pipelineCacheIncrementalMode==VK_TRUE)<<"}";}
        else if(std::strcmp(group,"extendedFlags")==0){VkPhysicalDeviceExtendedFlagsFeaturesKHR f{static_cast<VkStructureType>(structureType),nullptr,VK_FALSE};VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}};api.queryFeatures2(devs[i],&f2);out<<"{\"name\":\"VK_KHR_extended_flags / extendedFlags\",\"supported\":"<<jsonBool(f.extendedFlags==VK_TRUE)<<"}";}
        else if(std::strcmp(group,"extendedDynamicStateParity")==0 || std::strcmp(group,"extendedDynamicState")==0){VkPhysicalDeviceExtendedDynamicStateFeaturesEXT f{static_cast<VkStructureType>(structureType),nullptr,VK_FALSE};VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}};api.queryFeatures2(devs[i],&f2);out<<"{\"name\":\"VK_EXT_extended_dynamic_state / extendedDynamicState\",\"supported\":"<<jsonBool(f.extendedDynamicState==VK_TRUE)<<"}";}
        else if(std::strcmp(group,"privateDataParity")==0 || std::strcmp(group,"privateData")==0){VkPhysicalDevicePrivateDataFeatures f{static_cast<VkStructureType>(structureType),nullptr,VK_FALSE};VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}};api.queryFeatures2(devs[i],&f2);out<<"{\"name\":\"VK_EXT_private_data / privateData\",\"supported\":"<<jsonBool(f.privateData==VK_TRUE)<<"}";}
        else if(std::strcmp(group,"synchronization2Parity")==0 || std::strcmp(group,"synchronization2")==0){VkPhysicalDeviceSynchronization2Features f{static_cast<VkStructureType>(structureType),nullptr,VK_FALSE};VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}};api.queryFeatures2(devs[i],&f2);out<<"{\"name\":\"VK_KHR_synchronization2 / synchronization2\",\"supported\":"<<jsonBool(f.synchronization2==VK_TRUE)<<"}";}
        else if(std::strcmp(group,"rayQueryParity")==0 || std::strcmp(group,"rayQuery")==0 || std::strcmp(group,"VK_KHR_ray_query")==0 || std::strcmp(group,"VK_KHR_ray_query / rayQuery")==0){VkPhysicalDeviceRayQueryFeaturesKHR f{static_cast<VkStructureType>(structureType),nullptr,VK_FALSE};VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}};api.queryFeatures2(devs[i],&f2);out<<"{\"name\":\"VK_KHR_ray_query / rayQuery\",\"supported\":"<<jsonBool(f.rayQuery==VK_TRUE)<<"}";}
        else {VkPhysicalDeviceShaderOCPMicroscalingTypesFeaturesEXT f{static_cast<VkStructureType>(structureType),nullptr,VK_FALSE,VK_FALSE,VK_FALSE,VK_FALSE};VkPhysicalDeviceFeatures2 f2{VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2,&f,{}};api.queryFeatures2(devs[i],&f2);out<<"{\"name\":\"VK_EXT_shader_ocp_microscaling_types / shaderFloat4\",\"supported\":"<<jsonBool(f.shaderFloat4==VK_TRUE)<<"},{\"name\":\"VK_EXT_shader_ocp_microscaling_types / shaderFloat6\",\"supported\":"<<jsonBool(f.shaderFloat6==VK_TRUE)<<"},{\"name\":\"VK_EXT_shader_ocp_microscaling_types / shaderFloat8UnsignedE8M0\",\"supported\":"<<jsonBool(f.shaderFloat8UnsignedE8M0==VK_TRUE)<<"},{\"name\":\"VK_EXT_shader_ocp_microscaling_types / shaderMXInt8\",\"supported\":"<<jsonBool(f.shaderMXInt8==VK_TRUE)<<"}";}
        out<<"]}";
    }
    if (!matchedAny) {
        api.destroyInstance(inst,nullptr);
        if (!physicalDeviceEnumerationComplete) return std::string("{\"status\":\"incomplete\",\"group\":")+jsonString(group)+",\"extension\":"+jsonString(extensionName)+",\"reason\":\"Physical-device enumeration was incomplete, so extension absence cannot be established across all devices.\",\"devices\":[]}";
        if (extensionEnumerationIncomplete) return std::string("{\"status\":\"incomplete\",\"group\":")+jsonString(group)+",\"extension\":"+jsonString(extensionName)+",\"reason\":"+jsonString(extensionEnumerationReason.empty() ? "Device-extension enumeration remained VK_INCOMPLETE, so extension absence cannot be established." : extensionEnumerationReason)+",\"devices\":[]}";
        if (extensionEnumerationUncertain) return std::string("{\"status\":\"unavailable\",\"group\":")+jsonString(group)+",\"extension\":"+jsonString(extensionName)+",\"reason\":"+jsonString(extensionEnumerationReason.empty() ? "Device-extension enumeration was unavailable, so extension applicability could not be established." : extensionEnumerationReason)+",\"devices\":[]}";
        return std::string("{\"status\":\"not_applicable\",\"group\":")+jsonString(group)+",\"extension\":"+jsonString(extensionName)+",\"reason\":\"The extension was not enumerated by any physical device.\",\"devices\":[]}";
    }
    out<<"]}";api.destroyInstance(inst,nullptr);
    std::string simpleFeatureJson = out.str();
    auto replaceSimpleFeatureToken = [&](const std::string& token, const std::string& value) {
        const std::string encodedToken = jsonString(token);
        const std::size_t position = simpleFeatureJson.find(encodedToken);
        if (position != std::string::npos) simpleFeatureJson.replace(position, encodedToken.size(), jsonString(value));
    };
    replaceSimpleFeatureToken(simpleFeatureStatusToken, simpleFeatureIncomplete ? "incomplete" : "available");
    replaceSimpleFeatureToken(simpleFeatureReasonToken, simpleFeatureReason);
    return simpleFeatureJson;
}


std::string collectVulkanSelfTest(const char* driverMode, const char* driverIcdPath, const char* driverBundlePath, const char* hookLibDir, uint32_t targetVendorId, uint32_t targetDeviceId) {
    VulkanApi api;
    if (!api.open(driverMode, driverIcdPath, driverBundlePath, hookLibDir)) return std::string("{\"status\":\"unavailable\",\"reason\":") + jsonString(api.openError.empty() ? "Vulkan loader unavailable" : api.openError) + ",\"tests\":[]}";
    uint32_t loaderVersion = VK_API_VERSION_1_0;
    if (api.enumerateInstanceVersion && api.enumerateInstanceVersion(&loaderVersion) != VK_SUCCESS) loaderVersion = VK_API_VERSION_1_0;
    const auto queryInstanceExtensions = buildQueryInstanceExtensions(api);
    VkInstance instance = VK_NULL_HANDLE;
    uint32_t selectedInstanceApiVersion = VK_API_VERSION_1_0;
    const VkResult instanceResult = api.createInstanceCompatible(loaderVersion, queryInstanceExtensions, &instance, &selectedInstanceApiVersion);
    if (instanceResult != VK_SUCCESS || instance == VK_NULL_HANDLE) return std::string("{\"status\":\"unavailable\",\"reason\":") + jsonString(std::string("vkCreateInstance failed with VkResult ") + std::to_string(instanceResult)) + ",\"tests\":[]}";
    if (!api.loadInstanceFunctions(instance) || !api.enumeratePhysicalDevices || !api.getPhysicalDeviceQueueFamilyProperties) {
        if (api.destroyInstance) api.destroyInstance(instance, nullptr);
        return "{\"status\":\"unavailable\",\"reason\":\"Required Vulkan self-test entry points are unavailable.\",\"tests\":[]}";
    }
    const auto enumerated = enumeratePhysicalDevicesRobust(api, instance);
    const bool physicalDeviceEnumerationComplete = enumerated.complete;
    if (!enumerated.resultAvailable || enumerated.safetyRejected || (enumerated.result != VK_SUCCESS && enumerated.result != VK_INCOMPLETE) || enumerated.values.empty()) {
        api.destroyInstance(instance, nullptr);
        return "{\"status\":\"unavailable\",\"reason\":\"No bounded physical Vulkan device evidence was available for self-test.\",\"tests\":[]}";
    }
    std::vector<VkPhysicalDevice> matchingPhysicalDevices;
    for (VkPhysicalDevice candidate : enumerated.values) {
        VkPhysicalDeviceProperties properties{};
        getDevicePropertiesPrimary(api, candidate, properties);
        if (properties.vendorID == targetVendorId && properties.deviceID == targetDeviceId) matchingPhysicalDevices.push_back(candidate);
    }
    if (matchingPhysicalDevices.size() > 1) {
        api.destroyInstance(instance, nullptr);
        return "{\"status\":\"unavailable\",\"reason\":\"The selected Vulkan physical-device identity is ambiguous in the isolated self-test process; vendorId/deviceId matches more than one device and no stable cross-process identity is available.\",\"tests\":[]}";
    }
    if (matchingPhysicalDevices.empty()) {
        api.destroyInstance(instance, nullptr);
        return physicalDeviceEnumerationComplete
            ? "{\"status\":\"unavailable\",\"reason\":\"The selected Vulkan physical device was not found in the isolated self-test process.\",\"tests\":[]}"
            : "{\"status\":\"unavailable\",\"reason\":\"Physical-device enumeration was incomplete, so absence of the selected Vulkan physical device cannot be established safely.\",\"tests\":[]}";
    }
    VkPhysicalDevice physical = matchingPhysicalDevices.front();
    uint32_t queueCount = 0;
    api.getPhysicalDeviceQueueFamilyProperties(physical, &queueCount, nullptr);
    if (queueCount == 0 || queueCount > 4096) {
        api.destroyInstance(instance, nullptr);
        return "{\"status\":\"unavailable\",\"reason\":\"No bounded queue-family set was available for self-test.\",\"tests\":[]}";
    }
    std::vector<VkQueueFamilyProperties> queues(queueCount);
    const size_t queueCapacity = queues.size();
    api.getPhysicalDeviceQueueFamilyProperties(physical, &queueCount, queues.data());
    if (queueCount > queueCapacity) {
        api.destroyInstance(instance, nullptr);
        return "{\"status\":\"unavailable\",\"reason\":\"Queue-family data query exceeded the bounded allocation.\",\"tests\":[]}";
    }
    uint32_t queueFamily = UINT32_MAX;
    for (uint32_t i = 0; i < queueCount; ++i) if (queues[i].queueCount > 0 && (queues[i].queueFlags & VK_QUEUE_COMPUTE_BIT) != 0) { queueFamily = i; break; }
    if (queueFamily == UINT32_MAX) for (uint32_t i = 0; i < queueCount; ++i) if (queues[i].queueCount > 0 && (queues[i].queueFlags & VK_QUEUE_GRAPHICS_BIT) != 0) { queueFamily = i; break; }
    if (queueFamily == UINT32_MAX) {
        api.destroyInstance(instance, nullptr);
        return "{\"status\":\"unavailable\",\"reason\":\"No graphics or compute queue family was available for self-test.\",\"tests\":[]}";
    }
    auto createDevice = api.loadInstance<PFN_vkCreateDevice>(instance, "vkCreateDevice");
    auto getDeviceProcAddr = api.loadInstance<PFN_vkGetDeviceProcAddr>(instance, "vkGetDeviceProcAddr");
    auto destroyDeviceFromInstance = api.loadInstance<PFN_vkDestroyDevice>(instance, "vkDestroyDevice");
    if (!createDevice || !getDeviceProcAddr || !destroyDeviceFromInstance) {
        api.destroyInstance(instance, nullptr);
        return "{\"status\":\"unavailable\",\"reason\":\"Safe device create/destroy entry points were unavailable.\",\"tests\":[]}";
    }
    const float priority = 1.0f;
    VkDeviceQueueCreateInfo queueInfo{VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO, nullptr, 0, queueFamily, 1, &priority};
    VkDeviceCreateInfo deviceInfo{VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO, nullptr, 0, 1, &queueInfo, 0, nullptr, 0, nullptr, nullptr};
    VkDevice device = VK_NULL_HANDLE;
    const VkResult deviceResult = createDevice(physical, &deviceInfo, nullptr, &device);
    std::vector<std::string> tests;
    tests.push_back(std::string("{\"name\":\"VkDevice creation\",\"status\":") + jsonString(deviceResult == VK_SUCCESS && device != VK_NULL_HANDLE ? "PASS" : "FAIL") + ",\"detail\":" + jsonString(std::string("VkResult ") + std::to_string(deviceResult)) + "}");
    if (deviceResult != VK_SUCCESS || device == VK_NULL_HANDLE) {
        api.destroyInstance(instance, nullptr);
        std::ostringstream out;
        out << "{\"status\":\"completed_with_failures\",\"tests\":[" << tests.front() << "]}";
        return out.str();
    }
    auto destroyDevice = reinterpret_cast<PFN_vkDestroyDevice>(getDeviceProcAddr(device, "vkDestroyDevice"));
    if (!destroyDevice) destroyDevice = destroyDeviceFromInstance;
    auto createShaderModule = reinterpret_cast<PFN_vkCreateShaderModule>(getDeviceProcAddr(device, "vkCreateShaderModule"));
    auto destroyShaderModule = reinterpret_cast<PFN_vkDestroyShaderModule>(getDeviceProcAddr(device, "vkDestroyShaderModule"));
    auto createPipelineLayout = reinterpret_cast<PFN_vkCreatePipelineLayout>(getDeviceProcAddr(device, "vkCreatePipelineLayout"));
    auto destroyPipelineLayout = reinterpret_cast<PFN_vkDestroyPipelineLayout>(getDeviceProcAddr(device, "vkDestroyPipelineLayout"));
    auto createComputePipelines = reinterpret_cast<PFN_vkCreateComputePipelines>(getDeviceProcAddr(device, "vkCreateComputePipelines"));
    auto destroyPipeline = reinterpret_cast<PFN_vkDestroyPipeline>(getDeviceProcAddr(device, "vkDestroyPipeline"));
    static const uint32_t spirv[] = {
        0x07230203,0x00010000,0x00000000,0x00000005,0x00000000,
        0x00020011,0x00000001,
        0x0003000e,0x00000000,0x00000001,
        0x0005000f,0x00000005,0x00000003,0x6e69616d,0x00000000,
        0x00060010,0x00000003,0x00000011,0x00000001,0x00000001,0x00000001,
        0x00020013,0x00000001,
        0x00030021,0x00000002,0x00000001,
        0x00050036,0x00000001,0x00000003,0x00000000,0x00000002,
        0x000200f8,0x00000004,
        0x000100fd,
        0x00010038
    };
    VkShaderModule shader = VK_NULL_HANDLE;
    VkResult shaderResult = VK_ERROR_INITIALIZATION_FAILED;
    if (createShaderModule && destroyShaderModule) {
        VkShaderModuleCreateInfo shaderInfo{VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO, nullptr, 0, sizeof(spirv), spirv};
        shaderResult = createShaderModule(device, &shaderInfo, nullptr, &shader);
    }
    const bool shaderPathAvailable = createShaderModule && destroyShaderModule;
    tests.push_back(std::string("{\"name\":\"Minimal SPIR-V shader module\",\"status\":") + jsonString(!shaderPathAvailable ? "UNAVAILABLE" : (shaderResult == VK_SUCCESS && shader != VK_NULL_HANDLE ? "PASS" : "FAIL")) + ",\"detail\":" + jsonString(shaderPathAvailable ? std::string("VkResult ") + std::to_string(shaderResult) : "Safe shader create/destroy path unavailable") + "}");
    VkPipelineLayout layout = VK_NULL_HANDLE;
    VkResult layoutResult = VK_ERROR_INITIALIZATION_FAILED;
    if (createPipelineLayout && destroyPipelineLayout) {
        VkPipelineLayoutCreateInfo layoutInfo{VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO, nullptr, 0, 0, nullptr, 0, nullptr};
        layoutResult = createPipelineLayout(device, &layoutInfo, nullptr, &layout);
    }
    const bool layoutPathAvailable = createPipelineLayout && destroyPipelineLayout;
    tests.push_back(std::string("{\"name\":\"Minimal pipeline layout\",\"status\":") + jsonString(!layoutPathAvailable ? "UNAVAILABLE" : (layoutResult == VK_SUCCESS && layout != VK_NULL_HANDLE ? "PASS" : "FAIL")) + ",\"detail\":" + jsonString(layoutPathAvailable ? std::string("VkResult ") + std::to_string(layoutResult) : "Safe pipeline-layout create/destroy path unavailable") + "}");
    VkPipeline pipeline = VK_NULL_HANDLE;
    VkResult pipelineResult = VK_ERROR_INITIALIZATION_FAILED;
    const bool pipelinePathAvailable = createComputePipelines && destroyPipeline;
    if (shader != VK_NULL_HANDLE && layout != VK_NULL_HANDLE && pipelinePathAvailable) {
        VkPipelineShaderStageCreateInfo stage{VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO, nullptr, 0, VK_SHADER_STAGE_COMPUTE_BIT, shader, "main", nullptr};
        VkComputePipelineCreateInfo pipelineInfo{VK_STRUCTURE_TYPE_COMPUTE_PIPELINE_CREATE_INFO, nullptr, 0, stage, layout, VK_NULL_HANDLE, 0};
        pipelineResult = createComputePipelines(device, VK_NULL_HANDLE, 1, &pipelineInfo, nullptr, &pipeline);
    }
    const char* pipelineStatus = !pipelinePathAvailable || shader == VK_NULL_HANDLE || layout == VK_NULL_HANDLE ? "UNAVAILABLE" : (pipelineResult == VK_SUCCESS && pipeline != VK_NULL_HANDLE ? "PASS" : "FAIL");
    tests.push_back(std::string("{\"name\":\"Minimal compute pipeline creation\",\"status\":") + jsonString(pipelineStatus) + ",\"detail\":" + jsonString(!pipelinePathAvailable ? "Safe pipeline create/destroy path unavailable" : (shader == VK_NULL_HANDLE || layout == VK_NULL_HANDLE ? "Prerequisite shader module or pipeline layout unavailable" : std::string("VkResult ") + std::to_string(pipelineResult))) + "}");
    if (pipeline != VK_NULL_HANDLE && destroyPipeline) destroyPipeline(device, pipeline, nullptr);
    if (layout != VK_NULL_HANDLE && destroyPipelineLayout) destroyPipelineLayout(device, layout, nullptr);
    if (shader != VK_NULL_HANDLE && destroyShaderModule) destroyShaderModule(device, shader, nullptr);
    destroyDevice(device, nullptr);
    api.destroyInstance(instance, nullptr);
    bool failed = false;
    bool unavailable = false;
    for (const auto& test : tests) {
        if (test.find("\"status\":\"FAIL\"") != std::string::npos) failed = true;
        if (test.find("\"status\":\"UNAVAILABLE\"") != std::string::npos) unavailable = true;
    }
    const char* overallStatus = failed ? "completed_with_failures" : (unavailable ? "completed_with_unavailable" : "completed");
    std::ostringstream out;
    out << "{\"status\":" << jsonString(overallStatus) << ",\"tests\":[";
    for (size_t i = 0; i < tests.size(); ++i) { if (i) out << ','; out << tests[i]; }
    out << "]}";
    return out.str();
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_efishell_vulkanscope_VulkanProbeService_collectVulkanData(JNIEnv* env, jobject, jobject surface, jstring driverModeString, jstring driverIcdPathString, jstring driverBundlePathString, jstring hookLibDirString, jstring resultPathString) {
    const char* driverMode = driverModeString ? env->GetStringUTFChars(driverModeString, nullptr) : nullptr;
    const char* driverIcdPath = driverIcdPathString ? env->GetStringUTFChars(driverIcdPathString, nullptr) : nullptr;
    const char* driverBundlePath = driverBundlePathString ? env->GetStringUTFChars(driverBundlePathString, nullptr) : nullptr;
    const char* hookLibDir = hookLibDirString ? env->GetStringUTFChars(hookLibDirString, nullptr) : nullptr;
    const char* resultPath = resultPathString ? env->GetStringUTFChars(resultPathString, nullptr) : nullptr;
    installProbeCrashGuard(resultPath);
    bool finalPublishedByCollector = false;
    const std::string result = collect(surface, env, driverMode, driverIcdPath, driverBundlePath, hookLibDir, resultPath, &finalPublishedByCollector);
    const bool published = finalPublishedByCollector || publishProbeCheckpoint(resultPath, result);
    clearProbeCrashGuard(resultPath);
    if (resultPathString && resultPath) env->ReleaseStringUTFChars(resultPathString, resultPath);
    if (hookLibDirString && hookLibDir) env->ReleaseStringUTFChars(hookLibDirString, hookLibDir);
    if (driverBundlePathString && driverBundlePath) env->ReleaseStringUTFChars(driverBundlePathString, driverBundlePath);
    if (driverIcdPathString && driverIcdPath) env->ReleaseStringUTFChars(driverIcdPathString, driverIcdPath);
    if (driverModeString && driverMode) env->ReleaseStringUTFChars(driverModeString, driverMode);
    return published ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_efishell_vulkanscope_VulkanProbeService_collectVulkanSurfaceData(JNIEnv* env, jobject, jobject surface, jstring driverModeString, jstring driverIcdPathString, jstring driverBundlePathString, jstring hookLibDirString, jstring resultPathString) {
    const char* driverMode = driverModeString ? env->GetStringUTFChars(driverModeString, nullptr) : nullptr;
    const char* driverIcdPath = driverIcdPathString ? env->GetStringUTFChars(driverIcdPathString, nullptr) : nullptr;
    const char* driverBundlePath = driverBundlePathString ? env->GetStringUTFChars(driverBundlePathString, nullptr) : nullptr;
    const char* hookLibDir = hookLibDirString ? env->GetStringUTFChars(hookLibDirString, nullptr) : nullptr;
    const char* resultPath = resultPathString ? env->GetStringUTFChars(resultPathString, nullptr) : nullptr;
    installProbeCrashGuard(resultPath);
    const std::string result = collectVulkanSurface(surface, env, driverMode, driverIcdPath, driverBundlePath, hookLibDir);
    const bool published = publishProbeCheckpoint(resultPath, result);
    clearProbeCrashGuard(resultPath);
    if (resultPathString && resultPath) env->ReleaseStringUTFChars(resultPathString, resultPath);
    if (hookLibDirString && hookLibDir) env->ReleaseStringUTFChars(hookLibDirString, hookLibDir);
    if (driverBundlePathString && driverBundlePath) env->ReleaseStringUTFChars(driverBundlePathString, driverBundlePath);
    if (driverIcdPathString && driverIcdPath) env->ReleaseStringUTFChars(driverIcdPathString, driverIcdPath);
    if (driverModeString && driverMode) env->ReleaseStringUTFChars(driverModeString, driverMode);
    return published ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT jboolean JNICALL
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
    if (groupName.rfind("selftest:", 0) == 0) {
        uint32_t targetVendorId = 0;
        uint32_t targetDeviceId = 0;
        const size_t first = groupName.find(':');
        const size_t second = groupName.find(':', first + 1);
        bool targetValid = first != std::string::npos && second != std::string::npos && second + 1 < groupName.size();
        if (targetValid) {
            const std::string vendorText = groupName.substr(first + 1, second - first - 1);
            const std::string deviceText = groupName.substr(second + 1);
            char* vendorEnd = nullptr;
            char* deviceEnd = nullptr;
            const unsigned long vendor = std::strtoul(vendorText.c_str(), &vendorEnd, 10);
            const unsigned long device = std::strtoul(deviceText.c_str(), &deviceEnd, 10);
            targetValid = !vendorText.empty() && !deviceText.empty() && vendorEnd && *vendorEnd == '\0' && deviceEnd && *deviceEnd == '\0' && vendor <= UINT32_MAX && device <= UINT32_MAX;
            if (targetValid) { targetVendorId = static_cast<uint32_t>(vendor); targetDeviceId = static_cast<uint32_t>(device); }
        }
        result = targetValid ? collectVulkanSelfTest(driverMode, driverIcdPath, driverBundlePath, hookLibDir, targetVendorId, targetDeviceId) : "{\"status\":\"unavailable\",\"reason\":\"Invalid selected Vulkan self-test target.\",\"tests\":[]}";
    } else if (groupName == "metadata") {
        result = collectVulkanMetadata(driverMode, driverIcdPath, driverBundlePath, hookLibDir);
    } else if (groupName.rfind("ext::", 0) == 0) {
        result = collectVulkanExtensionGroup(driverMode, driverIcdPath, driverBundlePath, hookLibDir, groupName.c_str());
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
    const bool published = publishProbeCheckpoint(resultPath, result);
    clearProbeCrashGuard(resultPath);
    if (resultPathString && resultPath) env->ReleaseStringUTFChars(resultPathString, resultPath);
    if (hookLibDirString && hookLibDir) env->ReleaseStringUTFChars(hookLibDirString, hookLibDir);
    if (driverBundlePathString && driverBundlePath) env->ReleaseStringUTFChars(driverBundlePathString, driverBundlePath);
    if (driverIcdPathString && driverIcdPath) env->ReleaseStringUTFChars(driverIcdPathString, driverIcdPath);
    if (driverModeString && driverMode) env->ReleaseStringUTFChars(driverModeString, driverMode);
    if (groupString && group) env->ReleaseStringUTFChars(groupString, group);
    return published ? JNI_TRUE : JNI_FALSE;
}
