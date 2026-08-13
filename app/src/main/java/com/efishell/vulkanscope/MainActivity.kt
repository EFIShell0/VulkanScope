package com.efishell.vulkanscope

import android.graphics.Color
import android.os.Bundle
import android.content.res.Configuration
import android.os.Build
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.util.Log
import android.view.WindowManager
import android.content.Intent
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

private data class DisplayReport(
    val resolution: String,
    val refreshRate: String,
    val wideGamut: Boolean,
    val preferredWideGamut: String,
    val hdrTypes: List<String>,
    val minLuminance: String,
    val maxLuminance: String,
    val averageLuminance: String,
    val modes: List<String>
)

private data class ExtensionEntry(val name: String, val scope: String, val specVersion: Int, val supported: Boolean = true)
private data class LayerEntry(val name: String, val description: String, val specVersion: Int, val implementationVersion: Int)
private data class FeatureEntry(val name: String, val supported: Boolean)
private data class SurfaceFormatEntry(val format: String, val colorSpace: String, val classification: String, val description: String, val supported: Boolean = true)
private data class FormatEntry(val name: String, val linear: Long, val optimal: Long, val buffer: Long)
private data class PropertyEntry(val section: String, val name: String, val value: String)
private data class QueueEntry(val index: Int, val count: Int, val timestampBits: Int, val flags: Long, val graphics: Boolean, val compute: Boolean, val transfer: Boolean, val sparse: Boolean, val protected: Boolean, val opticalFlow: Boolean, val granularity: String)
private data class MemoryHeapEntry(val index: Int, val size: Long, val flags: Long)
private data class MemoryTypeEntry(val index: Int, val heap: Int, val flags: Long)
private data class DeviceReport(
    val name: String,
    val apiVersion: String,
    val driverVersion: String,
    val vendorId: String,
    val vendorIdRaw: Long,
    val deviceId: String,
    val deviceType: String,
    val extensions: List<ExtensionEntry>,
    val features: List<FeatureEntry>,
    val queues: List<QueueEntry>,
    val heaps: List<MemoryHeapEntry>,
    val memoryTypes: List<MemoryTypeEntry>,
    val formats: List<FormatEntry>,
    val limits: List<Pair<String, String>>,
    val detailedProperties: List<PropertyEntry>,
    val surfaceAvailable: Boolean,
    val surfacePresentationSupported: Boolean,
    val surfaceColorSpaceExtensionAvailable: Boolean,
    val surfaceColorSpaceExtensionEnabled: Boolean,
    val surfaceFormatQueryResult: Int,
    val surfaceFormatQueryResultSecond: Int,
    val surfaceCapabilities: List<Pair<String, String>>,
    val surfaceFormats: List<SurfaceFormatEntry>,
    val presentModes: List<String>,
    val presentationQueues: List<Pair<Int, Boolean>>
)

private data class VulkanReport(val loaderVersion: String, val instanceExtensions: List<ExtensionEntry>, val instanceLayers: List<LayerEntry>, val devices: List<DeviceReport>, val error: String?)

private enum class Page(val title: String) {
    Overview("Overview"), Vulkan("Vulkan"), Display("Display & HDR"), Surface("Surface"), Features("Features"), Memory("Memory"), Queues("Queues"), Formats("Formats"), Properties("Properties & Limits"), Extensions("Extensions"), Profiles("Profiles"), Settings("Settings"), Info("Info")
}

private enum class DriverMode(val label: String) {
    SYSTEM("System Vulkan driver"),
    TURNIP("Turnip / third-party driver")
}

class MainActivity : ComponentActivity() {
    external fun collectVulkanData(surface: Surface?, driverMode: String, driverIcdPath: String?, driverBundlePath: String?, hookLibDir: String): String
    external fun isAdrenoDevice(): Boolean

    private lateinit var prefs: android.content.SharedPreferences
    private var turnipSupported: Boolean = false
    private var driverMode: DriverMode = DriverMode.SYSTEM
    private var restartRequest by mutableStateOf<DriverMode?>(null)

    private val surfaceLock = Any()
    private val collectMutex = Mutex()
    private var currentSurface: Surface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.navigationBarColor = android.graphics.Color.rgb(17, 17, 17)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        prefs = getSharedPreferences("settings", MODE_PRIVATE)
        driverMode = DriverMode.values().find { it.name == prefs.getString("driver_mode", DriverMode.SYSTEM.name) } ?: DriverMode.SYSTEM
        System.loadLibrary("vulkanscope")
        turnipSupported = Build.SUPPORTED_ABIS.any { it == "arm64-v8a" } && runCatching { isAdrenoDevice() }.getOrDefault(false)
        if (!turnipSupported && driverMode == DriverMode.TURNIP) {
            driverMode = DriverMode.SYSTEM
            prefs.edit().putString("driver_mode", DriverMode.SYSTEM.name).apply()
        }
        setContent {
            VulkanScopeApp(
                displayReport = displayReport(),
                surfaceReady = { surface ->
                    synchronized(surfaceLock) { currentSurface = surface }
                },
                surfaceDestroyed = {
                    synchronized(surfaceLock) { currentSurface = null }
                },
                collect = { collectReport() },
                driverMode = driverMode,
                turnipSupported = turnipSupported,
                onDriverModeChanged = { mode ->
                    requestDriverModeChange(mode)
                },
                onInstallDriverBundle = { openDriverBundlePicker() },
                restartRequest = restartRequest,
                onRestartRequestDismiss = { restartRequest = null },
                onRestartConfirmed = { mode ->
                    restartRequest = null
                    if (prefs.edit().putString("driver_mode", mode.name).commit()) {
                        driverMode = mode
                        restartApplication()
                    } else {
                        Log.e("VulkanScope", "Could not persist driver mode before restart")
                    }
                }
            )
        }
    }

    private val driverPickerLauncher = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) installDriverBundle(uri)
    }

    private fun openDriverBundlePicker() {
        if (!turnipSupported) {
            android.widget.Toast.makeText(this, "Unavailable on this ABI. Turnip support requires arm64-v8a and a Qualcomm Adreno GPU.", android.widget.Toast.LENGTH_LONG).show()
            return
        }
        driverPickerLauncher.launch(arrayOf("application/zip", "application/octet-stream", "*/*"))
    }

    private fun installDriverBundle(uri: Uri) {
        val finalDir = File(filesDir, "turnip")
        val tempDir = File(filesDir, "turnip_import_${System.currentTimeMillis()}")
        try {
            tempDir.mkdirs()
            contentResolver.openInputStream(uri)?.use { input ->
                ZipInputStream(input).use { zip ->
                    var entry = zip.nextEntry
                    while (entry != null) {
                        val safe = File(tempDir, entry.name).canonicalFile
                        if (!safe.path.startsWith(tempDir.canonicalPath + File.separator) && safe != tempDir.canonicalFile) {
                            throw SecurityException("Unsafe driver bundle path")
                        }
                        if (entry.isDirectory) safe.mkdirs() else {
                            safe.parentFile?.mkdirs()
                            FileOutputStream(safe).use { output -> zip.copyTo(output) }
                        }
                        zip.closeEntry()
                        entry = zip.nextEntry
                    }
                }
            } ?: throw IllegalStateException("Unable to read bundle")

            val metadataFile = tempDir.walkTopDown().firstOrNull { it.isFile && it.name.equals("meta.json", true) }
                ?: throw IllegalArgumentException("This is not an AdrenoTools driver package. Required file: meta.json")
            val metadata = runCatching { JSONObject(metadataFile.readText()) }.getOrElse {
                throw IllegalArgumentException("The AdrenoTools meta.json is invalid")
            }
            val schemaVersion = metadata.optInt("schemaVersion", -1)
            if (schemaVersion != 1) {
                throw IllegalArgumentException("Unsupported AdrenoTools package schema: ${if (schemaVersion < 0) "missing" else schemaVersion}")
            }
            val declared = metadata.optString("libraryName").trim()
            if (declared.isBlank() || !declared.endsWith(".so", true)) {
                throw IllegalArgumentException("AdrenoTools meta.json does not declare a Vulkan library")
            }
            val library = tempDir.walkTopDown().firstOrNull { it.isFile && it.name == declared }
                ?: throw IllegalArgumentException("Driver metadata references missing library: $declared")
            if (!declared.contains("vulkan", true)) {
                throw IllegalArgumentException("The selected AdrenoTools package does not declare a Vulkan driver library")
            }

            // Validate that the chosen library can at least be opened before replacing
            // the previously working bundle. The actual Vulkan entry point is checked
            // by the native collector after the restart.
            if (!library.canRead() || library.length() == 0L) {
                throw IllegalArgumentException("The Vulkan .so driver library is empty or unreadable")
            }

            if (finalDir.exists()) finalDir.deleteRecursively()
            if (!tempDir.renameTo(finalDir)) {
                tempDir.copyRecursively(finalDir, overwrite = true)
                tempDir.deleteRecursively()
            }

            prefs.edit().putString("turnip_bundle", finalDir.absolutePath).commit()
            Log.i("VulkanScope", "Imported AdrenoTools driver bundle: ${metadataFile.name}, library: ${library.name}")

            // Importing a bundle does NOT immediately switch the active driver. Ask
            // about the restart only after the ZIP has been completely and successfully
            // installed.
            requestDriverModeChange(DriverMode.TURNIP, forceRestart = true)
        } catch (e: Exception) {
            tempDir.deleteRecursively()
            Log.e("VulkanScope", "Turnip bundle installation failed", e)
            android.widget.Toast.makeText(
                this,
                "Driver import failed: ${e.message ?: "The selected ZIP could not be installed."}",
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun findDriverLibrary(root: File): File? {
        val candidates = root.walkTopDown().filter { it.isFile && it.extension.equals("so", true) }.toList()
        if (candidates.isEmpty()) return null
        val json = root.walkTopDown().firstOrNull { it.isFile && it.extension.equals("json", true) }
        val declared = json?.let { runCatching { JSONObject(it.readText()).optString("libraryName") }.getOrNull() }.orEmpty()
        if (declared.isNotBlank()) candidates.firstOrNull { it.name == declared }?.let { return it }
        return candidates.firstOrNull { it.name.contains("vulkan", true) } ?: candidates.first()
    }

    private fun findTurnipBundle(): String? {
        if (driverMode != DriverMode.TURNIP) return null
        val root = File(filesDir, "turnip")
        return if (root.exists()) root.absolutePath else null
    }

    private fun findTurnipIcd(): String? {
        if (driverMode != DriverMode.TURNIP) return null
        val root = File(filesDir, "turnip")
        val json = root.walkTopDown().firstOrNull { it.isFile && it.extension.equals("json", true) }
        val declared = json?.let { runCatching { JSONObject(it.readText()).optString("libraryName") }.getOrNull() }.orEmpty()
        if (declared.isNotBlank()) {
            root.walkTopDown().firstOrNull { it.isFile && it.name == declared }?.let { return it.absolutePath }
        }
        return root.walkTopDown().firstOrNull { it.isFile && it.extension.equals("so", true) }?.absolutePath
    }

    private fun requestDriverModeChange(mode: DriverMode, forceRestart: Boolean = false) {
        if (mode == DriverMode.TURNIP && !turnipSupported) {
            return
        }
        if (mode == DriverMode.TURNIP && findInstalledTurnipLibrary() == null) {
            openDriverBundlePicker()
            return
        }
        if (mode == driverMode && !forceRestart) return
        restartRequest = mode
    }

    private fun restartApplication() {
        // Launch a tiny helper Activity in a separate process. That process survives
        // the termination of the current Vulkan/native process and starts MainActivity
        // only after the old process is gone. This is considerably more reliable on
        // MIUI/HyperOS than scheduling an AlarmManager PendingIntent and immediately
        // killing the foreground process.
        val intent = Intent(this, RestartActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        moveTaskToBack(true)
        // Give the helper process enough time to finish its Activity launch before
        // the current process is terminated. This matters on MIUI/HyperOS where a
        // very short delay can leave the second process without a visible task.
        Handler(Looper.getMainLooper()).postDelayed({
            android.os.Process.killProcess(android.os.Process.myPid())
        }, 600L)
    }

    private fun findInstalledTurnipLibrary(): File? {
        if (!turnipSupported) return null
        val root = File(filesDir, "turnip")
        if (!root.exists()) return null
        val json = root.walkTopDown().firstOrNull { it.isFile && it.extension.equals("json", true) }
        val declared = json?.let { runCatching { JSONObject(it.readText()).optString("libraryName") }.getOrNull() }.orEmpty()
        if (declared.isNotBlank()) root.walkTopDown().firstOrNull { it.isFile && it.name == declared }?.let { return it }
        return root.walkTopDown().firstOrNull { it.isFile && it.extension.equals("so", true) && it.length() > 0L }
    }

    private fun displayReport(): DisplayReport {
        val display = getSystemService(WindowManager::class.java).defaultDisplay
        val mode = display.mode
        val hdr = display.hdrCapabilities
        val hdrNames = hdr.supportedHdrTypes.map { hdrTypeName(it) }
        val modes = display.supportedModes.map { "${it.physicalWidth} × ${it.physicalHeight} · ${formatHz(it.refreshRate)}" }.distinct().sorted()
        val wideGamut = android.os.Build.VERSION.SDK_INT >= 26 && display.isWideColorGamut
        val preferred = if (android.os.Build.VERSION.SDK_INT >= 29) display.preferredWideGamutColorSpace?.name ?: "Not exposed" else "Not exposed"
        return DisplayReport(
            "${mode.physicalWidth} × ${mode.physicalHeight}",
            formatHz(mode.refreshRate),
            wideGamut,
            preferred,
            hdrNames,
            formatLuminance(hdr.desiredMinLuminance),
            formatLuminance(hdr.desiredMaxLuminance),
            formatLuminance(hdr.desiredMaxAverageLuminance),
            modes
        )
    }

    private suspend fun collectReport(): VulkanReport = withContext(Dispatchers.IO) {
        collectMutex.withLock {
            synchronized(surfaceLock) {
                val raw = collectVulkanData(currentSurface, driverMode.name, findTurnipIcd(), findTurnipBundle(), applicationInfo.nativeLibraryDir)
                try {
                    parseReport(raw)
                } catch (e: Exception) {
                    Log.e("VulkanScope", "Vulkan JSON parse failed: length=${raw.length}, error=${e.message}", e)
                    val tail = raw.takeLast(512).replace("\n", "\\n")
                    Log.e("VulkanScope", "Vulkan JSON tail: $tail")
                    VulkanReport(
                        loaderVersion = "Unknown",
                        instanceExtensions = emptyList(),
                        instanceLayers = emptyList(),
                        devices = emptyList(),
                        error = "Vulkan report parse failed: ${e.message}"
                    )
                }
            }
        }
    }
}

private fun hdrTypeName(type: Int): String = when (type) {
    1 -> "Dolby Vision"
    2 -> "HDR10"
    3 -> "HLG"
    4 -> "HDR10+"
    5 -> "HLG+"
    else -> "Unknown ($type)"
}

private fun formatHz(value: Float): String = String.format(java.util.Locale.US, "%.2f Hz", value)
private fun formatLuminance(value: Float): String = if (value <= 0f || value.isNaN()) "Not exposed" else String.format(java.util.Locale.US, "%.3f cd/m²", value)

private fun parseReport(raw: String): VulkanReport {
    val root = JSONObject(raw)
    val error = if (root.has("error") && !root.isNull("error")) root.optString("error") else null
    val instanceExtensions = parseExtensions(root.optJSONArray("instanceExtensions"))
    val instanceLayers = mutableListOf<LayerEntry>()
    val layerArray = root.optJSONArray("instanceLayers") ?: JSONArray()
    for (i in 0 until layerArray.length()) {
        val layer = layerArray.optJSONObject(i) ?: continue
        instanceLayers += LayerEntry(layer.optString("name"), layer.optString("description"), layer.optInt("specVersion"), layer.optInt("implementationVersion"))
    }
    val devices = mutableListOf<DeviceReport>()
    val deviceArray = root.optJSONArray("devices") ?: JSONArray()
    for (i in 0 until deviceArray.length()) {
        val item = deviceArray.optJSONObject(i) ?: continue
        val extensions = parseExtensions(item.optJSONArray("deviceExtensions"))
        val features = mutableListOf<FeatureEntry>()
        val featureArray = item.optJSONArray("features") ?: JSONArray()
        for (j in 0 until featureArray.length()) {
            val feature = featureArray.optJSONObject(j) ?: continue
            features += FeatureEntry(feature.optString("name"), feature.optBoolean("supported"))
        }
        val versionedFeatureArray = item.optJSONArray("versionedFeatures") ?: JSONArray()
        for (j in 0 until versionedFeatureArray.length()) {
            val feature = versionedFeatureArray.optJSONObject(j) ?: continue
            features += FeatureEntry(feature.optString("name"), feature.optBoolean("supported"))
        }
        val queues = mutableListOf<QueueEntry>()
        val queueArray = item.optJSONArray("queues") ?: JSONArray()
        for (j in 0 until queueArray.length()) {
            val q = queueArray.optJSONObject(j) ?: continue
            queues += QueueEntry(q.optInt("index"), q.optInt("count"), q.optInt("timestampValidBits"), q.optLong("flags"), q.optBoolean("graphics"), q.optBoolean("compute"), q.optBoolean("transfer"), q.optBoolean("sparse"), q.optBoolean("protected"), q.optBoolean("opticalFlow"), q.optString("minImageTransferGranularity", "0 × 0 × 0"))
        }
        val heaps = mutableListOf<MemoryHeapEntry>()
        val memoryTypes = mutableListOf<MemoryTypeEntry>()
        val memory = item.optJSONObject("memory")
        val heapArray = memory?.optJSONArray("heaps") ?: JSONArray()
        for (j in 0 until heapArray.length()) {
            val heap = heapArray.optJSONObject(j) ?: continue
            heaps += MemoryHeapEntry(heap.optInt("index"), heap.optLong("size"), heap.optLong("flags"))
        }
        val typeArray = memory?.optJSONArray("types") ?: JSONArray()
        for (j in 0 until typeArray.length()) {
            val type = typeArray.optJSONObject(j) ?: continue
            memoryTypes += MemoryTypeEntry(type.optInt("index"), type.optInt("heap"), type.optLong("flags"))
        }
        val formats = mutableListOf<FormatEntry>()
        val limits = mutableListOf<Pair<String, String>>()
        val formatArray = item.optJSONArray("formats") ?: JSONArray()
        for (j in 0 until formatArray.length()) {
            val format = formatArray.optJSONObject(j) ?: continue
            formats += FormatEntry(format.optString("name"), format.optLong("linear"), format.optLong("optimal"), format.optLong("buffer"))
        }
        val limitArray = item.optJSONArray("limits") ?: JSONArray()
        for (j in 0 until limitArray.length()) {
            val limit = limitArray.optJSONObject(j) ?: continue
            limits += limit.optString("name") to limit.optString("value")
        }
        val surfaceFormats = mutableListOf<SurfaceFormatEntry>()
        val presentModes = mutableListOf<String>()
        val presentationQueues = mutableListOf<Pair<Int, Boolean>>()
        val capabilityPairs = mutableListOf<Pair<String, String>>()
        val detailedProperties = mutableListOf<PropertyEntry>()
        val detailedPropertyArray = item.optJSONArray("detailedProperties") ?: JSONArray()
        for (j in 0 until detailedPropertyArray.length()) {
            val prop = detailedPropertyArray.optJSONObject(j) ?: continue
            detailedProperties += PropertyEntry(prop.optString("section"), prop.optString("name"), prop.optString("value"))
        }
        val surface = item.optJSONObject("surface")
        val surfaceAvailable = surface?.optBoolean("available") == true
        val surfacePresentationSupported = surface?.optBoolean("presentationSupported") == true
        val surfaceColorSpaceExtensionAvailable = surface?.optBoolean("colorSpaceExtensionAvailable") == true
        val surfaceColorSpaceExtensionEnabled = surface?.optBoolean("colorSpaceExtensionEnabled") == true
        val surfaceFormatQueryResult = surface?.optInt("formatQueryResult", -1) ?: -1
        val surfaceFormatQueryResultSecond = surface?.optInt("formatQueryResultSecond", -1) ?: -1
        if (surface != null) {
            listOf("minImageCount", "maxImageCount", "currentExtent", "minExtent", "maxExtent", "maxImageArrayLayers", "supportedTransforms", "currentTransform", "supportedCompositeAlpha", "supportedUsageFlags").forEach { key ->
                if (surface.has(key)) capabilityPairs += key to surface.optString(key)
            }
            val surfaceFormatArray = surface.optJSONArray("formats") ?: JSONArray()
            for (j in 0 until surfaceFormatArray.length()) {
                val sf = surfaceFormatArray.optJSONObject(j) ?: continue
                surfaceFormats += SurfaceFormatEntry(sf.optString("format"), sf.optString("colorSpace"), sf.optString("class"), sf.optString("description"), true)
            }
            val presentArray = surface.optJSONArray("presentModes") ?: JSONArray()
            for (j in 0 until presentArray.length()) presentModes += presentArray.optString(j)
            val presentationArray = surface.optJSONArray("queuePresentation") ?: JSONArray()
            for (j in 0 until presentationArray.length()) {
                val p = presentationArray.optJSONObject(j) ?: continue
                presentationQueues += p.optInt("queueFamily") to p.optBoolean("supported")
            }
        }
        devices += DeviceReport(
            item.optString("name", "Unknown GPU"),
            item.optString("apiVersion", "Unknown"),
            item.optString("driverVersion", "Unknown"),
            "0x${item.optLong("vendorId").toString(16).uppercase()}",
            item.optLong("vendorId"),
            "0x${item.optLong("deviceId").toString(16).uppercase()}",
            deviceTypeName(item.optInt("deviceType")),
            extensions,
            features,
            queues,
            heaps,
            memoryTypes,
            formats,
            limits,
            detailedProperties,
            surfaceAvailable,
            surfacePresentationSupported,
            surfaceColorSpaceExtensionAvailable,
            surfaceColorSpaceExtensionEnabled,
            surfaceFormatQueryResult,
            surfaceFormatQueryResultSecond,
            capabilityPairs,
            surfaceFormats,
            presentModes,
            presentationQueues
        )
    }
    return VulkanReport(root.optString("loaderVersion", "Unknown"), instanceExtensions, instanceLayers, devices, error)
}

private fun parseExtensions(array: JSONArray?): List<ExtensionEntry> {
    if (array == null) return emptyList()
    val result = mutableListOf<ExtensionEntry>()
    for (i in 0 until array.length()) {
        val item = array.optJSONObject(i) ?: continue
        result += ExtensionEntry(item.optString("name"), item.optString("scope"), item.optInt("specVersion"))
    }
    return result.sortedBy { it.name }
}

private fun deviceTypeName(type: Int): String = when (type) {
    1 -> "Integrated GPU"
    2 -> "Discrete GPU"
    3 -> "Virtual GPU"
    4 -> "CPU"
    else -> "Other / Unknown"
}

@Composable
private fun VulkanScopeApp(
    displayReport: DisplayReport,
    surfaceReady: (Surface) -> Unit,
    surfaceDestroyed: () -> Unit,
    collect: suspend () -> VulkanReport,
    driverMode: DriverMode,
    turnipSupported: Boolean,
    onDriverModeChanged: (DriverMode) -> Unit,
    onInstallDriverBundle: () -> Unit,
    restartRequest: DriverMode?,
    onRestartRequestDismiss: () -> Unit,
    onRestartConfirmed: (DriverMode) -> Unit
) {
    var report by remember { mutableStateOf<VulkanReport?>(null) }
    var page by remember { mutableStateOf(Page.Overview) }
    var loading by remember { mutableStateOf(true) }
    var surfaceGeneration by remember { mutableStateOf(0) }

    LaunchedEffect(surfaceGeneration) {
        if (surfaceGeneration == 0) return@LaunchedEffect
        loading = true
        try {
            kotlinx.coroutines.delay(120L)
            report = collect()
        } finally {
            loading = false
        }
    }

    val red = ComposeColor(0xFFF21D2F)
    MaterialTheme(colorScheme = darkColorScheme(background = ComposeColor.Black, surface = ComposeColor(0xFF101010), surfaceVariant = ComposeColor(0xFF191919), primary = red, onPrimary = ComposeColor.White, secondary = red, tertiary = ComposeColor(0xFFFF6573), onBackground = ComposeColor(0xFFF4F4F4), onSurface = ComposeColor(0xFFF4F4F4))) {
        BackHandler(enabled = page != Page.Overview) { page = Page.Overview }

        if (restartRequest != null) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = onRestartRequestDismiss,
                containerColor = ComposeColor(0xFF171717),
                shape = RoundedCornerShape(28.dp),
                title = { Text("Restart required", fontWeight = FontWeight.SemiBold) },
                text = {
                    Text(
                        "VulkanScope must restart to apply the ${restartRequest!!.label} selection. Restart now?",
                        color = ComposeColor(0xFFD0D0D0)
                    )
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(onClick = onRestartRequestDismiss) {
                        Text("Cancel")
                    }
                },
                confirmButton = {
                    androidx.compose.material3.Button(onClick = { onRestartConfirmed(restartRequest!!) }) {
                        Text("Restart")
                    }
                }
            )
        }

        val isLandscape = androidx.compose.ui.platform.LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
        Scaffold(
            containerColor = ComposeColor.Black,
            topBar = { AppHeader(page, onBack = { page = Page.Overview }, onSettings = { page = Page.Settings }, onInfo = { page = Page.Info }) },
            bottomBar = {
                if (!isLandscape) {
                    NavigationBar(containerColor = ComposeColor(0xFF0A0A0A), tonalElevation = 0.dp) {
                        navigationItems().forEach { item ->
                            NavigationBarItem(
                                selected = selectedNavigationPage(page) == item.page,
                                onClick = { page = item.page },
                                icon = { Icon(painterResource(item.icon), contentDescription = item.label, modifier = Modifier.size(24.dp)) },
                                label = { Text(item.label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ComposeColor.White,
                                    selectedTextColor = ComposeColor.White,
                                    indicatorColor = red,
                                    unselectedIconColor = ComposeColor(0xFFB8B8B8),
                                    unselectedTextColor = ComposeColor(0xFFB8B8B8)
                                )
                            )
                        }
                    }
                }
            }
        ) { padding ->
            Row(Modifier.fillMaxSize().padding(padding)) {
                if (isLandscape) {
                    CompactNavigationRail(
                        selectedPage = selectedNavigationPage(page),
                        onPageSelected = { page = it },
                        red = red
                    )
                }
                Box(Modifier.weight(1f)) {
                    SurfaceProbe(
                        modifier = Modifier.matchParentSize(),
                        onCreated = { surface ->
                            surfaceReady(surface)
                            surfaceGeneration += 1
                        },
                        onDestroyed = surfaceDestroyed
                    )
                    if (loading) LoadingView()
                    else {
                        val current = report
                        if (current == null) EmptyState("No Vulkan report")
                        else {
                            AnimatedContent(
                                targetState = page,
                                transitionSpec = {
                                    val forward = targetState.ordinal > initialState.ordinal
                                    if (forward) {
                                        slideInHorizontally(animationSpec = spring()) { it / 5 } + fadeIn(animationSpec = spring()) togetherWith
                                            slideOutHorizontally(animationSpec = spring()) { -it / 5 } + fadeOut(animationSpec = spring())
                                    } else {
                                        slideInHorizontally(animationSpec = spring()) { -it / 5 } + fadeIn(animationSpec = spring()) togetherWith
                                            slideOutHorizontally(animationSpec = spring()) { it / 5 } + fadeOut(animationSpec = spring())
                                    }
                                },
                                label = "pageTransition"
                            ) { targetPage ->
                                PageContent(targetPage, current, displayReport, driverMode, turnipSupported, onDriverModeChanged, onInstallDriverBundle, onNavigate = { page = it })
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppHeader(page: Page, onBack: () -> Unit, onSettings: () -> Unit, onInfo: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            if (page != Page.Overview) {
                IconButton(onClick = onBack) {
                    Icon(painterResource(R.drawable.ic_back), contentDescription = "Back")
                }
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("VulkanScope", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(page.title, style = MaterialTheme.typography.labelMedium, color = ComposeColor(0xFF9E9E9E), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        },
        actions = {
            if (page != Page.Settings && page != Page.Info) {
                IconButton(onClick = onInfo) {
                    Icon(painterResource(R.drawable.ic_info), contentDescription = "Info")
                }
                IconButton(onClick = onSettings) {
                    Icon(painterResource(R.drawable.ic_settings), contentDescription = "Settings")
                }
            }
        },
        colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(containerColor = ComposeColor.Black)
    )
}

@Composable
private fun DisplayPage(display: DisplayReport, device: DeviceReport?) {
    LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { SectionCard("Display") {
            KeyValue("Physical mode", display.resolution)
            KeyValue("Current refresh", display.refreshRate)
            KeyValue("Wide color gamut", if (display.wideGamut) "YES" else "NO / NOT EXPOSED")
            KeyValue("Preferred wide gamut color space", display.preferredWideGamut)
        } }
        item { SectionCard("HDR capabilities") {
            if (display.hdrTypes.isEmpty()) Text("No HDR type reported", color = ComposeColor(0xFF9E9E9E))
            display.hdrTypes.forEach { Text(it, fontWeight = FontWeight.Medium) }
            HorizontalDivider(Modifier.padding(vertical = 6.dp), color = ComposeColor(0xFF303030))
            KeyValue("Minimum luminance", display.minLuminance)
            KeyValue("Maximum luminance", display.maxLuminance)
            KeyValue("Maximum average luminance", display.averageLuminance)
        } }
        item { SectionCard("Supported display modes") { display.modes.forEach { Text(it) } } }
        item { SectionCard("Display ↔ Vulkan interpretation") {
            KeyValue("Android wide gamut", if (display.wideGamut) "Reported" else "Not reported")
            KeyValue("Vulkan surface data", if (device?.surfaceAvailable == true) "Queried from VkSurfaceKHR" else "Unavailable")
            Text("A Vulkan color-space capability is not treated as a measurement of the panel's physical gamut.", color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.bodySmall)
        } }
    }
}

@Composable
private fun SurfacePage(device: DeviceReport?) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf(SupportFilter.ALL) }
    val entries = remember(device) { buildSurfaceCatalog(device?.surfaceFormats ?: emptyList()) }
    val filtered = remember(query, filter, entries) {
        entries.filter { entry ->
            val statusOk = when (filter) { SupportFilter.ALL -> true; SupportFilter.SUPPORTED -> entry.supported; SupportFilter.UNSUPPORTED -> !entry.supported }
            statusOk && (query.isBlank() || entry.format.contains(query, true) || entry.colorSpace.contains(query, true) || entry.classification.contains(query, true) || entry.description.contains(query, true))
        }
    }
    LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { SectionCard("VkSurfaceKHR") {
            KeyValue("Status", if (device?.surfaceAvailable == true) "Available" else "Not available")
            KeyValue("Presentation support", when {
                device?.surfacePresentationSupported == true -> "Supported"
                device?.surfaceAvailable == true -> "Not supported by this Vulkan device"
                else -> "Unavailable"
            })
            KeyValue("Surface format query", when (device?.surfaceFormatQueryResult) { 0 -> "VK_SUCCESS"; 5 -> "VK_INCOMPLETE"; null, -1 -> "Unavailable"; else -> device.surfaceFormatQueryResult.toString() })
            KeyValue("Returned format pairs", device?.surfaceFormats?.size?.toString() ?: "Unavailable")
            device?.surfaceCapabilities?.forEach { KeyValue(it.first, it.second) }
        } }
        item { SectionCard("Search surface formats / color spaces") {
            OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("Search BT.709, BT.2020, P3, HDR10, format…") })
            Spacer(Modifier.height(6.dp))
            SupportFilterRow(filter) { filter = it }
            Text("${filtered.size} entries", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.labelMedium)
        } }
        item { SectionCard("HDR / wide-color surface detection") {
            KeyValue("VK_EXT_swapchain_colorspace", when { device?.surfaceColorSpaceExtensionEnabled == true -> "Enabled"; device?.surfaceColorSpaceExtensionAvailable == true -> "Available but not enabled"; else -> "Not exposed" })
            val hdrPairs = device?.surfaceFormats?.count { it.classification == "HDR10 / PQ" || it.classification == "HDR10 / HLG" || it.classification == "Dolby Vision" } ?: 0
            val wideColorPairs = device?.surfaceFormats?.count { it.classification == "Display-P3" || it.classification == "Display-P3 / Linear" || it.classification == "BT.2020" || it.classification == "scRGB" || it.classification == "scRGB / Linear" } ?: 0
            KeyValue("HDR color-space pairs", if (device?.surfacePresentationSupported != true) "Unavailable" else hdrPairs.toString())
            KeyValue("Wide-color pairs", if (device?.surfacePresentationSupported != true) "Unavailable" else wideColorPairs.toString())
            Text("Supported entries are returned directly by vkGetPhysicalDeviceSurfaceFormatsKHR. Unsupported entries are generated from the app's known inspection catalog and are not driver-reported capabilities.", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
        } }
        item { SectionCard("Format + color-space pairs") {
            if (filtered.isEmpty()) Text("No matching entries")
            filtered.forEach { format ->
                Column(Modifier.padding(vertical = 5.dp)) {
                    Text(format.format, fontWeight = FontWeight.SemiBold)
                    Text(format.colorSpace, color = ComposeColor(0xFFD0D0D0), style = MaterialTheme.typography.bodySmall)
                    Text(if (format.supported) "SUPPORTED · ${format.classification}" else "NOT SUPPORTED · ${format.classification}", color = if (format.supported) ComposeColor(0xFF7DFF9B) else ComposeColor(0xFFFF6B6B), style = MaterialTheme.typography.labelSmall)
                    Text(format.description, color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
                }
            }
        } }
        item { SectionCard("Present modes") { device?.presentModes?.forEach { Text(it) } } }
        item { SectionCard("Presentation support") { device?.presentationQueues?.forEach { Text("Queue family ${it.first}: ${if (it.second) "PRESENT" else "NO PRESENT"}") } } }
    }
}

private fun buildSurfaceCatalog(supported: List<SurfaceFormatEntry>): List<SurfaceFormatEntry> {
    if (supported.isEmpty()) return emptyList()
    val supportedKeys = supported.map { it.format + "|" + it.colorSpace }.toSet()
    val commonColorSpaces = listOf(
        "VK_COLOR_SPACE_SRGB_NONLINEAR_KHR" to "sRGB",
        "VK_COLOR_SPACE_DISPLAY_P3_NONLINEAR_EXT" to "Display-P3",
        "VK_COLOR_SPACE_EXTENDED_SRGB_LINEAR_EXT" to "scRGB / Linear",
        "VK_COLOR_SPACE_DISPLAY_P3_LINEAR_EXT" to "Display-P3 / Linear",
        "VK_COLOR_SPACE_DCI_P3_NONLINEAR_EXT" to "DCI-P3",
        "VK_COLOR_SPACE_BT709_LINEAR_EXT" to "BT.709 / Linear",
        "VK_COLOR_SPACE_BT709_NONLINEAR_EXT" to "BT.709",
        "VK_COLOR_SPACE_BT2020_LINEAR_EXT" to "BT.2020",
        "VK_COLOR_SPACE_HDR10_ST2084_EXT" to "HDR10 / PQ",
        "VK_COLOR_SPACE_DOLBYVISION_EXT" to "Dolby Vision",
        "VK_COLOR_SPACE_HDR10_HLG_EXT" to "HDR10 / HLG",
        "VK_COLOR_SPACE_ADOBERGB_LINEAR_EXT" to "Adobe RGB / Linear",
        "VK_COLOR_SPACE_ADOBERGB_NONLINEAR_EXT" to "Adobe RGB",
        "VK_COLOR_SPACE_PASS_THROUGH_EXT" to "Pass-through",
        "VK_COLOR_SPACE_EXTENDED_SRGB_NONLINEAR_EXT" to "scRGB"
    )
    val formats = supported.map { it.format }.distinct().take(12)
    val generated = formats.flatMap { format -> commonColorSpaces.map { (cs, cls) ->
        val key = "$format|$cs"
        supported.find { it.format == format && it.colorSpace == cs } ?: SurfaceFormatEntry(format, cs, cls, "Known Vulkan color-space candidate; not returned by this surface.", key in supportedKeys)
    } }
    return (supported + generated.filter { !supportedKeys.contains(it.format + "|" + it.colorSpace) }).distinctBy { it.format + "|" + it.colorSpace }.sortedWith(compareByDescending<SurfaceFormatEntry> { it.supported }.thenBy { it.colorSpace }.thenBy { it.format })
}

@Composable
private fun FeaturesPage(device: DeviceReport?) {
    LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        item { Text("Core feature bits reported by vkGetPhysicalDeviceFeatures", style = MaterialTheme.typography.bodySmall, color = ComposeColor(0xFF8F8F8F), modifier = Modifier.padding(bottom = 8.dp)) }
        items(device?.features ?: emptyList()) { feature ->
            DataRow(feature.name, if (feature.supported) "SUPPORTED" else "NOT SUPPORTED", feature.supported)
        }
    }
}

@Composable
private fun MemoryPage(device: DeviceReport?) {
    LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { SectionCard("Memory heaps") {
            device?.heaps?.forEach { heap ->
                KeyValue("Heap ${heap.index}", "${formatBytes(heap.size)} · flags ${heap.flags}")
            }
        } }
        item { SectionCard("Memory types") {
            device?.memoryTypes?.forEach { type ->
                KeyValue("Type ${type.index}", "heap ${type.heap} · flags ${type.flags}")
            }
        } }
    }
}

@Composable
private fun QueuesPage(device: DeviceReport?) {
    LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(device?.queues ?: emptyList()) { queue ->
            SectionCard("Queue family ${queue.index}") {
                KeyValue("Queue count", queue.count.toString())
                KeyValue("Timestamp valid bits", queue.timestampBits.toString())
                KeyValue("Graphics", if (queue.graphics) "YES" else "NO")
                KeyValue("Compute", if (queue.compute) "YES" else "NO")
                KeyValue("Transfer", if (queue.transfer) "YES" else "NO")
                KeyValue("Sparse binding", if (queue.sparse) "YES" else "NO")
                KeyValue("Flags", queue.flags.toString())
            }
        }
    }
}

@Composable
private fun FormatsPage(device: DeviceReport?) {
    var query by remember { mutableStateOf("") }
    val formats = remember(device) { device?.formats ?: emptyList() }
    val filtered = remember(query, formats) { if (query.isBlank()) formats else formats.filter { it.name.contains(query, true) } }
    LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Text("Implementation-reported format properties. Each format exposes the exact Linear / Optimal / Buffer feature bitmasks returned by vkGetPhysicalDeviceFormatProperties.", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
            OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, placeholder = { Text("Search formats…") })
            Text("${filtered.size} formats", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(vertical = 6.dp))
        }
        items(filtered) { format ->
            Card(colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFF111111)), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(format.name, fontWeight = FontWeight.Medium)
                    KeyValue("Linear tiling flags", format.linear.toString())
                    KeyValue("Optimal tiling flags", format.optimal.toString())
                    KeyValue("Buffer features", format.buffer.toString())
                }
            }
        }
    }
}


@Composable
private fun SurfaceProbe(
    modifier: Modifier = Modifier,
    onCreated: (Surface) -> Unit,
    onDestroyed: () -> Unit
) {
    AndroidView(
        modifier = modifier.alpha(0f),
        factory = { context ->
            SurfaceView(context).apply {
                setBackgroundColor(Color.BLACK)
                holder.addCallback(object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) { onCreated(holder.surface) }
                    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
                    override fun surfaceDestroyed(holder: SurfaceHolder) { onDestroyed() }
                })
            }
        },
        update = {}
    )
}

@Composable
private fun PageContent(page: Page, report: VulkanReport, display: DisplayReport, driverMode: DriverMode, turnipSupported: Boolean, onDriverModeChanged: (DriverMode) -> Unit, onInstallDriverBundle: () -> Unit, onNavigate: (Page) -> Unit) {
    val device = report.devices.firstOrNull()
    when (page) {
        Page.Overview -> OverviewPage(report, device, display, driverMode, onNavigate)
        Page.Vulkan -> VulkanPage(report, device, turnipSupported)
        Page.Display -> DisplayPage(display, device)
        Page.Surface -> SurfacePage(device)
        Page.Features -> FeaturesPage(device)
        Page.Memory -> MemoryPage(device)
        Page.Queues -> QueuesPage(device)
        Page.Formats -> FormatsPage(device)
        Page.Properties -> PropertiesPage(device)
        Page.Extensions -> ExtensionsPage(report, device)
        Page.Profiles -> ProfilesPage()
        Page.Settings -> SettingsPage(report, display, driverMode, turnipSupported, onDriverModeChanged, onInstallDriverBundle)
        Page.Info -> InfoPage()
    }
}

@Composable
private fun OverviewPage(report: VulkanReport, device: DeviceReport?, display: DisplayReport, driverMode: DriverMode, navigate: (Page) -> Unit) {
    LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            HeroCard(device, report, driverMode)
        }
        if (report.error != null) {
            item {
                SectionCard("Vulkan inspection error") {
                    Text(report.error, color = ComposeColor(0xFFFF6B6B))
                    Text(
                        "The driver mode is shown separately from the inspection result.",
                        color = ComposeColor(0xFF9E9E9E),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        item {
            ExploreCard { destination -> navigate(destination) }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Vulkan", device?.apiVersion ?: "Unknown", Modifier.weight(1f))
                MetricCard("Display", display.refreshRate, Modifier.weight(1f))
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("HDR", if (display.hdrTypes.isEmpty()) "No" else "${display.hdrTypes.size} types", Modifier.weight(1f))
                MetricCard("Wide gamut", if (display.wideGamut) "Supported" else "Not exposed", Modifier.weight(1f))
            }
        }
        item { SectionCard("Quick access") {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    QuickAccessCard("Vulkan", Page.Vulkan, navigate, Modifier.weight(1f))
                    QuickAccessCard("Surface", Page.Surface, navigate, Modifier.weight(1f))
                    QuickAccessCard("Display", Page.Display, navigate, Modifier.weight(1f))
                    QuickAccessCard("HDR & Color", Page.Display, navigate, Modifier.weight(1f))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    QuickAccessCard("Extensions", Page.Extensions, navigate, Modifier.weight(1f))
                    QuickAccessCard("Profiles", Page.Profiles, navigate, Modifier.weight(1f))
                    QuickAccessCard("More", Page.Features, navigate, Modifier.weight(1f))
                    Spacer(Modifier.weight(1f))
                }
            }
        } }
        item { SectionCard("Capability snapshot") {
            KeyValue("GPU", device?.name ?: "Unknown")
            KeyValue("Driver", device?.driverVersion ?: "Unknown")
            KeyValue("Vendor ID", device?.vendorId ?: "Unknown")
            KeyValue("Device ID", device?.deviceId ?: "Unknown")
            KeyValue("Device type", device?.deviceType ?: "Unknown")
            KeyValue("Loader API", report.loaderVersion)
            KeyValue("Instance extensions", report.instanceExtensions.size.toString())
            KeyValue("Device extensions", device?.extensions?.size?.toString() ?: "0")
        } }
    }
}

@Composable
private fun VulkanPage(report: VulkanReport, device: DeviceReport?, turnipSupported: Boolean) {
    LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { SectionCard("Vulkan API") {
            KeyValue("Loader / instance API", report.loaderVersion)
            KeyValue("Device API", device?.apiVersion ?: "Unknown")
            KeyValue("Driver", device?.driverVersion ?: "Unknown")
            KeyValue("Device type", device?.deviceType ?: "Unknown")
            KeyValue("Vendor ID", device?.vendorId ?: "Unknown")
            KeyValue("Device ID", device?.deviceId ?: "Unknown")
        } }
        item { SectionCard("Instance layers") {
            if (report.instanceLayers.isEmpty()) Text("No instance layers exposed")
            report.instanceLayers.forEach { layer ->
                Column(Modifier.padding(vertical = 5.dp)) {
                    Text(layer.name, fontWeight = FontWeight.SemiBold)
                    Text("spec ${layer.specVersion} · implementation ${layer.implementationVersion}", color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.labelSmall)
                    if (layer.description.isNotBlank()) Text(layer.description, color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
                }
            }
        } }
        item { SectionCard("Instance extensions") {
            report.instanceExtensions.sortedBy { it.name }.forEach { ext -> Text("${ext.name} · spec ${ext.specVersion}") }
        } }
        item { SectionCard("Operating system") {
            KeyValue("Architecture", Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown")
            KeyValue("Version", Build.VERSION.RELEASE)
            KeyValue("Codename", Build.VERSION.CODENAME)
            KeyValue("SDK", Build.VERSION.SDK_INT.toString())
            KeyValue("Build ID", Build.ID)
            KeyValue("Build incremental", Build.VERSION.INCREMENTAL)
            KeyValue("Security patch", Build.VERSION.SECURITY_PATCH)
            KeyValue("Brand", Build.BRAND)
            KeyValue("Manufacturer", Build.MANUFACTURER)
            KeyValue("Product", Build.PRODUCT)
            KeyValue("Device", Build.DEVICE)
            KeyValue("Board", Build.BOARD)
            KeyValue("Hardware", Build.HARDWARE)
            KeyValue("Fingerprint", Build.FINGERPRINT)
        } }
        item { SectionCard("Android runtime") {
            KeyValue("Architecture", Build.SUPPORTED_ABIS.joinToString(", "))
            KeyValue("Manufacturer", Build.MANUFACTURER)
            KeyValue("Model", Build.MODEL)
            KeyValue("Android", Build.VERSION.RELEASE)
            KeyValue("SDK", Build.VERSION.SDK_INT.toString())
            KeyValue("Build ID", Build.ID)
            KeyValue("Build incremental", Build.VERSION.INCREMENTAL)
            KeyValue("Turnip eligibility", if (turnipSupported) "arm64-v8a + Qualcomm Adreno detected" else "Not eligible")
        } }
        item { SectionCard("Feature coverage") {
            val supported = device?.features?.count { it.supported } ?: 0
            val total = device?.features?.size ?: 0
            KeyValue("Queried feature fields", "$supported / $total supported")
            KeyValue("Instance extensions", report.instanceExtensions.size.toString())
            KeyValue("Device extensions", device?.extensions?.size?.toString() ?: "0")
        } }
    }
}

@Composable
private fun HeroCard(device: DeviceReport?, report: VulkanReport, driverMode: DriverMode) {
    Card(colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFF171717)), shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                VendorLogo(device?.vendorIdRaw, device?.name, Modifier.size(82.dp))
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(device?.name ?: "Vulkan device unavailable", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(if (device != null) vendorInfo(device.vendorIdRaw).name else "Unknown vendor", color = ComposeColor(0xFFBDBDBD))
                    Text(driverMode.label, color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun QuickAccessCard(title: String, destination: Page, navigate: (Page) -> Unit, modifier: Modifier) {
    Card(
        onClick = { navigate(destination) },
        colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFF171717)),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier.height(72.dp)
    ) {
        Column(
            Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
        ) {
            Icon(
                painterResource(pageIcon(destination)),
                contentDescription = null,
                modifier = Modifier.size(19.dp),
                tint = ComposeColor(0xFFF21D2F)
            )
            Text(
                title,
                fontSize = 11.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CompactNavigationRail(selectedPage: Page, onPageSelected: (Page) -> Unit, red: ComposeColor) {
    Surface(
        modifier = Modifier.width(80.dp),
        color = ComposeColor(0xFF101010)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            navigationItems().forEach { item ->
                val selected = selectedPage == item.page
                Card(
                    onClick = { onPageSelected(item.page) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) red else ComposeColor.Transparent
                    ),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth().height(54.dp)
                ) {
                    Column(
                        Modifier.fillMaxSize().padding(horizontal = 2.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically)
                    ) {
                        Icon(
                            painterResource(item.icon),
                            contentDescription = item.label,
                            modifier = Modifier.size(21.dp),
                            tint = if (selected) ComposeColor.White else ComposeColor(0xFFB8B8B8)
                        )
                        Text(
                            item.label,
                            color = if (selected) ComposeColor.White else ComposeColor(0xFFB8B8B8),
                            fontSize = 9.sp,
                            lineHeight = 10.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreCard(onNavigate: (Page) -> Unit) {
    SectionCard("Explore") {
        Text("Detailed Vulkan inspection areas", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(Page.Features, Page.Memory, Page.Queues, Page.Formats, Page.Properties).forEach { page ->
                AssistChip(
                    onClick = { onNavigate(page) },
                    leadingIcon = { Icon(painterResource(pageIcon(page)), contentDescription = null, modifier = Modifier.size(18.dp)) },
                    label = { Text(page.title) }
                )
            }
        }
    }
}

@Composable
private fun PropertiesPage(device: DeviceReport?) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("All") }
    val properties = remember(device) { device?.detailedProperties ?: emptyList() }
    val sections = remember(properties) { listOf("All") + properties.map { it.section }.distinct() }
    val filtered = remember(query, filter, properties) {
        properties.filter {
            (filter == "All" || it.section == filter) &&
                (query.isBlank() || it.name.contains(query, true) || it.value.contains(query, true) || it.section.contains(query, true))
        }
    }
    LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Detailed physical-device properties queried through vkGetPhysicalDeviceProperties2. Core 1.1/1.2/1.3 entries are shown separately, matching the detailed property view used by Vulkan capability viewers.", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
            OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, placeholder = { Text("Search properties…") })
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                sections.forEach { section -> FilterChip(selected = filter == section, onClick = { filter = section }, label = { Text(section) }) }
            }
            Text("${filtered.size} properties", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(vertical = 6.dp))
        }
        items(filtered) { property ->
            Card(colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFF111111)), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(property.name, fontWeight = FontWeight.Medium)
                    Text(property.section, color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.labelSmall)
                    Text(property.value, color = ComposeColor(0xFFD8D8D8), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

private fun vulkanProfileCatalog(): List<Pair<String, String>> = listOf(
    "VP_LUNARG_minimum_requirements_1_3" to "r.1",
    "VP_LUNARG_minimum_requirements_1_2" to "r.1",
    "VP_LUNARG_minimum_requirements_1_1" to "r.1",
    "VP_LUNARG_minimum_requirements_1_0" to "r.1",
    "VP_LUNARG_desktop_baseline_2024" to "r.1",
    "VP_LUNARG_desktop_baseline_2023" to "r.2",
    "VP_LUNARG_desktop_baseline_2022" to "r.2",
    "VP_KHR_roadmap_2024" to "r.1",
    "VP_KHR_roadmap_2022" to "r.1",
    "VP_ANDROID_baseline_2022" to "r.2",
    "VP_ANDROID_baseline_2021" to "r.3",
    "VP_ANDROID_16_minimums" to "r.1",
    "VP_ANDROID_15_minimums" to "r.1"
)

@Composable
private fun ProfilesPage() {
    var query by remember { mutableStateOf("") }
    val profiles = remember { vulkanProfileCatalog() }

    val filtered = profiles.filter { it.first.contains(query, true) }
    LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Text("Vulkan Profiles catalog entries corresponding to the profile list visible in Vulkan Hardware Capability Viewer. These are profile definitions/catalog entries; this page does not claim that the current device satisfies every profile.", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
            OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, placeholder = { Text("Search profiles…") })
        }
        items(filtered) { profile ->
            Card(colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFF111111)), shape = RoundedCornerShape(16.dp)) {
                Row(Modifier.fillMaxWidth().padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(profile.first, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                    Text(profile.second, color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun InfoPage() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val installedAbi = remember { detectInstalledAbi(context) }
    val packageInfo = remember {
        runCatching { context.packageManager.getPackageInfo(context.packageName, 0) }.getOrNull()
    }
    val versionName = packageInfo?.versionName ?: "Unknown"
    val versionCode = if (packageInfo != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageInfo.longVersionCode.toString()
    } else {
        @Suppress("DEPRECATION")
        (packageInfo?.versionCode?.toString() ?: "Unknown")
    }
    LazyColumn(
        contentPadding = WindowInsets.navigationBars.asPaddingValues(),
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionCard("Developer") {
                KeyValue("Developer", "Semih Boran")
                KeyValue("Nickname", "EFI Shell")
                Button(
                    onClick = {
                        runCatching {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/EFIShell0")))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("GitHub: github.com/EFIShell0")
                }
            }
        }
        item {
            SectionCard("Application") {
                KeyValue("Application", "VulkanScope")
                KeyValue("Version", versionName)
                KeyValue("Version code", versionCode)
                KeyValue("Package", context.packageName)
                KeyValue("Installed ABI", installedAbi)
            }
        }
        item {
            SectionCard("Device ABI") {
                KeyValue("Supported ABIs", Build.SUPPORTED_ABIS.joinToString(", "))
                Text(
                    "Installed ABI is the native ABI used by this VulkanScope installation; supported ABIs are the ABIs reported by Android for the device.",
                    color = ComposeColor(0xFF8F8F8F),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        item {
            SectionCard("Android") {
                KeyValue("Manufacturer", Build.MANUFACTURER)
                KeyValue("Model", Build.MODEL)
                KeyValue("Android", Build.VERSION.RELEASE)
                KeyValue("SDK", Build.VERSION.SDK_INT.toString())
            }
        }
        item {
            SectionCard("About") {
                Text(
                    "VulkanScope is a Vulkan capability and device inspection utility. It reports information exposed by the Android Vulkan implementation and the selected driver mode.",
                    color = ComposeColor(0xFFB0B0B0)
                )
            }
        }
    }
}

private fun detectInstalledAbi(context: Context): String {
    val nativeDir = context.applicationInfo.nativeLibraryDir.orEmpty().lowercase()
    return when {
        nativeDir.contains("arm64") -> "arm64-v8a"
        nativeDir.contains("armeabi-v7a") || nativeDir.endsWith("/arm") -> "armeabi-v7a"
        nativeDir.contains("x86_64") -> "x86_64"
        nativeDir.contains("x86") -> "x86"
        else -> Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown"
    }
}

@Composable
private fun SettingsPage(report: VulkanReport, display: DisplayReport, mode: DriverMode, turnipSupported: Boolean, onModeChanged: (DriverMode) -> Unit, onInstallDriverBundle: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val textLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri ->
        if (uri != null) writeExport(context, uri, reportToText(report, display, mode), "text/plain")
    }
    val htmlLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/html")) { uri ->
        if (uri != null) writeExport(context, uri, reportToHtml(report, display, mode), "text/html")
    }
    val bundleDir = File(context.filesDir, "turnip")
    val bundleInstalled = bundleDir.exists() && bundleDir.walkTopDown().any { it.isFile && it.extension.equals("so", true) }
    LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { SectionCard("Vulkan driver") {
            Text("Choose which Vulkan driver source VulkanScope should request. A restart is performed after changing the driver so the Vulkan loader is opened again with the new selection.", color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.bodySmall)
            DriverOption(DriverMode.SYSTEM, mode == DriverMode.SYSTEM, "Uses Android's system Vulkan loader/driver.", enabled = true) { onModeChanged(DriverMode.SYSTEM) }
            DriverOption(DriverMode.TURNIP, mode == DriverMode.TURNIP, if (turnipSupported) "Uses an installed AdrenoTools-compatible Turnip driver." else "Unavailable on this ABI. Turnip support requires arm64-v8a and a Qualcomm Adreno GPU.", enabled = turnipSupported) { onModeChanged(DriverMode.TURNIP) }
            if (turnipSupported) {
                Text("Turnip requires an AdrenoTools-compatible driver package (meta.json + Vulkan .so).", color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.bodySmall)
            }
        } }
        if (turnipSupported && mode == DriverMode.TURNIP) {
            item { SectionCard("Turnip / third-party driver bundle") {
                KeyValue("Status", if (bundleInstalled) "Bundle installed" else "Not installed")
                Button(onClick = onInstallDriverBundle, modifier = Modifier.fillMaxWidth()) { Text("Import driver ZIP") }
                Text("Import an AdrenoTools-compatible Turnip ZIP. The driver remains inside VulkanScope's private storage; Android's system Vulkan driver is never replaced.", color = ComposeColor(0xFF777777), style = MaterialTheme.typography.bodySmall)
            } }
        }
        item { SectionCard("Export complete report") {
            Text("Export the complete currently collected VulkanScope report, including device properties, detailed Core 1.1/1.2/1.3 properties, features, memory, queues, formats, surface data, extensions, layers and Android/display information.", color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.bodySmall)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { textLauncher.launch("VulkanScope-report.txt") }, modifier = Modifier.weight(1f)) { Text("Export TXT") }
                Button(onClick = { htmlLauncher.launch("VulkanScope-report.html") }, modifier = Modifier.weight(1f)) { Text("Export HTML") }
            }
        } }
        item { SectionCard("Important") {
            Text("VulkanScope uses libadrenotools for rootless driver loading on modern Android. This avoids the libhardware.so / linker-namespace problem of directly loading libvulkan_freedreno.so.", color = ComposeColor(0xFFAAAAAA), style = MaterialTheme.typography.bodySmall)
        } }
    }
}

@Composable
private fun DriverOption(option: DriverMode, selected: Boolean, description: String, enabled: Boolean, onClick: () -> Unit) {
    val textColor = if (enabled) ComposeColor(0xFFFFFFFF) else ComposeColor(0xFF666666)
    Card(onClick = onClick, enabled = enabled, colors = CardDefaults.cardColors(containerColor = if (selected) ComposeColor(0xFF1D1113) else ComposeColor(0xFF111111)), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            androidx.compose.material3.RadioButton(selected = selected, enabled = enabled, onClick = onClick)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(option.label, color = textColor, fontWeight = FontWeight.SemiBold)
                Text(description, color = if (enabled) ComposeColor(0xFF8F8F8F) else ComposeColor(0xFF555555), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun ExtensionsPage(report: VulkanReport, device: DeviceReport?) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf(SupportFilter.ALL) }
    val supported = remember(report, device) {
        (report.instanceExtensions + (device?.extensions ?: emptyList()))
            .groupBy { it.name }
            .map { (_, values) -> ExtensionEntry(values.first().name, values.map { it.scope }.distinctScopes(), values.maxOf { it.specVersion }, true) }
            .sortedBy { it.name }
    }
    val knownUnsupported = remember { KNOWN_VULKAN_EXTENSIONS - supported.map { it.name }.toSet() }
    val all = remember(supported, knownUnsupported) {
        (supported + knownUnsupported.map { ExtensionEntry(it, "Known registry", 0, false) }).sortedBy { it.name }
    }
    val filtered = remember(query, filter, all) {
        all.filter { entry ->
            val statusOk = when (filter) { SupportFilter.ALL -> true; SupportFilter.SUPPORTED -> entry.supported; SupportFilter.UNSUPPORTED -> !entry.supported }
            statusOk && (query.isBlank() || entry.name.contains(query, true) || entry.scope.contains(query, true))
        }
    }
    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp), singleLine = true, placeholder = { Text("Search Vulkan extension names") })
        SupportFilterRow(filter) { filter = it }
        Text("${filtered.size} extensions", modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp), color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.labelMedium)
        LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues(), modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            items(filtered, key = { it.name }) { extension ->
                Card(colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFF111111)), shape = RoundedCornerShape(18.dp)) {
                    Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(extension.name, fontWeight = FontWeight.SemiBold)
                        Text(if (extension.supported) "SUPPORTED · ${extension.scope} · spec ${extension.specVersion}" else "NOT SUPPORTED · known registry entry", color = if (extension.supported) ComposeColor(0xFF7DFF9B) else ComposeColor(0xFFFF6B6B), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}


private fun writeExport(context: Context, uri: Uri, content: String, mime: String) {
    runCatching { context.contentResolver.openOutputStream(uri)?.use { it.write(content.toByteArray(Charsets.UTF_8)) } }
}

private fun reportToText(report: VulkanReport, display: DisplayReport, mode: DriverMode): String = buildString {
    appendLine("VulkanScope report")
    appendLine("=================")
    appendLine("Driver mode: ${mode.label}")
    appendLine("Loader / instance API: ${report.loaderVersion}")
    appendLine("Display: ${display.resolution} @ ${display.refreshRate}")
    appendLine("Wide gamut: ${display.wideGamut}")
    appendLine("Preferred wide gamut: ${display.preferredWideGamut}")
    appendLine("HDR types: ${display.hdrTypes.joinToString(", ")}")
    appendLine("HDR luminance: min=${display.minLuminance}, max=${display.maxLuminance}, average=${display.averageLuminance}")
    appendLine("Android: ${Build.MANUFACTURER} ${Build.MODEL}, ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
    appendLine("Codename=${Build.VERSION.CODENAME}, Security patch=${Build.VERSION.SECURITY_PATCH}")
    appendLine("Brand=${Build.BRAND}, Product=${Build.PRODUCT}, Device=${Build.DEVICE}, Board=${Build.BOARD}, Hardware=${Build.HARDWARE}")
    appendLine("Build ID=${Build.ID}, Incremental=${Build.VERSION.INCREMENTAL}")
    appendLine("Fingerprint=${Build.FINGERPRINT}")
    appendLine("ABIs: ${Build.SUPPORTED_ABIS.joinToString(", ")}")
    if (report.error != null) appendLine("Report error: ${report.error}")
    appendLine()
    appendLine("INSTANCE LAYERS")
    report.instanceLayers.forEach { appendLine("${it.name} | spec ${it.specVersion} | implementation ${it.implementationVersion} | ${it.description}") }
    appendLine()
    appendLine("INSTANCE EXTENSIONS")
    report.instanceExtensions.forEach { appendLine("${it.name} | spec ${it.specVersion}") }
    appendLine(); appendLine("VULKAN PROFILES CATALOG")
    vulkanProfileCatalog().forEach { appendLine("${it.first} | ${it.second}") }
        report.devices.forEachIndexed { index, d ->
        appendLine(); appendLine("DEVICE #${index + 1}: ${d.name}")
        appendLine("API: ${d.apiVersion}"); appendLine("Driver version: ${d.driverVersion}"); appendLine("Vendor: ${d.vendorId}"); appendLine("Device ID: ${d.deviceId}"); appendLine("Type: ${d.deviceType}")
        appendLine(); appendLine("DEVICE EXTENSIONS"); d.extensions.forEach { appendLine("${it.name} | ${it.scope} | spec ${it.specVersion}") }
        appendLine(); appendLine("FEATURES"); d.features.forEach { appendLine("${it.name} = ${it.supported}") }
        appendLine(); appendLine("DETAILED PROPERTIES"); d.detailedProperties.forEach { appendLine("[${it.section}] ${it.name} = ${it.value}") }
        appendLine(); appendLine("LIMITS"); d.limits.forEach { appendLine("${it.first} = ${it.second}") }
        appendLine(); appendLine("MEMORY HEAPS"); d.heaps.forEach { appendLine("Heap ${it.index}: ${formatBytes(it.size)} | flags ${it.flags}") }
        appendLine(); appendLine("MEMORY TYPES"); d.memoryTypes.forEach { appendLine("Type ${it.index}: heap ${it.heap} | flags ${it.flags}") }
        appendLine(); appendLine("QUEUES"); d.queues.forEach { appendLine("Family ${it.index}: count=${it.count}, timestampBits=${it.timestampBits}, flags=${it.flags}, graphics=${it.graphics}, compute=${it.compute}, transfer=${it.transfer}, sparse=${it.sparse}, protected=${it.protected}, opticalFlow=${it.opticalFlow}, granularity=${it.granularity}") }
        appendLine(); appendLine("FORMATS"); d.formats.forEach { appendLine("${it.name}: linear=${it.linear}, optimal=${it.optimal}, buffer=${it.buffer}") }
        appendLine(); appendLine("SURFACE")
        appendLine("Available=${d.surfaceAvailable}, presentation=${d.surfacePresentationSupported}")
        d.surfaceCapabilities.forEach { appendLine("${it.first} = ${it.second}") }
        d.surfaceFormats.forEach { appendLine("${it.format} | ${it.colorSpace} | ${it.classification} | ${if (it.supported) "SUPPORTED" else "NOT SUPPORTED"} | ${it.description}") }
        appendLine("Present modes: ${d.presentModes.joinToString(", ")}")
        d.presentationQueues.forEach { appendLine("Queue ${it.first}: present=${it.second}") }
    }
}

private fun htmlEscape(value: String): String = value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")

private fun reportToHtml(report: VulkanReport, display: DisplayReport, mode: DriverMode): String = buildString {
    append("<!doctype html><html><head><meta charset=\"utf-8\"><title>VulkanScope report</title><style>body{font-family:system-ui,sans-serif;background:#101010;color:#eee;margin:24px}h1,h2{color:#fff}table{border-collapse:collapse;width:100%;margin-bottom:24px}td,th{border:1px solid #444;padding:6px;text-align:left;vertical-align:top}th{background:#202020}.section{margin-top:28px}</style></head><body>")
    append("<h1>VulkanScope report</h1><p><b>Driver mode:</b> ${htmlEscape(mode.label)}<br><b>Loader / instance API:</b> ${htmlEscape(report.loaderVersion)}<br><b>Display:</b> ${htmlEscape(display.resolution)} @ ${htmlEscape(display.refreshRate)}<br><b>HDR:</b> ${htmlEscape(display.hdrTypes.joinToString(", "))}</p>")
    if (report.error != null) append("<p><b>Report error:</b> ${htmlEscape(report.error)}</p>")
    fun table(title: String, headers: String, rows: List<Pair<String,String>>) { append("<div class=\"section\"><h2>${htmlEscape(title)}</h2><table><tr>$headers</tr>"); rows.forEach { append("<tr><td>${htmlEscape(it.first)}</td><td>${htmlEscape(it.second)}</td></tr>") }; append("</table></div>") }
    table("Android / display", "<th>Property</th><th>Value</th>", listOf("Manufacturer" to Build.MANUFACTURER, "Model" to Build.MODEL, "Android" to Build.VERSION.RELEASE, "SDK" to Build.VERSION.SDK_INT.toString(), "ABIs" to Build.SUPPORTED_ABIS.joinToString(", "), "Resolution" to display.resolution, "Refresh rate" to display.refreshRate, "Wide gamut" to display.wideGamut.toString(), "Preferred wide gamut" to display.preferredWideGamut, "HDR types" to display.hdrTypes.joinToString(", "), "HDR min luminance" to display.minLuminance, "HDR max luminance" to display.maxLuminance, "HDR average luminance" to display.averageLuminance))
    table("Instance extensions", "<th>Name</th><th>Scope / spec</th>", report.instanceExtensions.map { it.name to "${it.scope} / ${it.specVersion}" })
    table("Instance layers", "<th>Name</th><th>Details</th>", report.instanceLayers.map { it.name to "spec ${it.specVersion}, implementation ${it.implementationVersion}<br>${it.description}" })
    table("Vulkan Profiles catalog", "<th>Profile</th><th>Spec revision</th>", vulkanProfileCatalog())
        report.devices.forEach { d ->
        append("<div class=\"section\"><h2>Device: ${htmlEscape(d.name)}</h2>")
        table("Device properties", "<th>Property</th><th>Value</th>", listOf("API" to d.apiVersion, "Driver version" to d.driverVersion, "Vendor" to d.vendorId, "Device ID" to d.deviceId, "Type" to d.deviceType))
        table("Detailed properties", "<th>Property</th><th>Value</th>", d.detailedProperties.map { "[${it.section}] ${it.name}" to it.value })
        table("Features", "<th>Feature</th><th>Supported</th>", d.features.map { it.name to it.supported.toString() })
        table("Limits", "<th>Limit</th><th>Value</th>", d.limits)
        table("Memory", "<th>Entry</th><th>Value</th>", d.heaps.map { "Heap ${it.index}" to "${formatBytes(it.size)} | flags ${it.flags}" } + d.memoryTypes.map { "Type ${it.index}" to "heap ${it.heap} | flags ${it.flags}" })
        table("Queues", "<th>Family</th><th>Details</th>", d.queues.map { "${it.index}" to "count=${it.count}, timestampBits=${it.timestampBits}, flags=${it.flags}, graphics=${it.graphics}, compute=${it.compute}, transfer=${it.transfer}, sparse=${it.sparse}" })
        table("Formats", "<th>Format</th><th>Feature masks</th>", d.formats.map { it.name to "linear=${it.linear}, optimal=${it.optimal}, buffer=${it.buffer}" })
        table("Surface formats / color spaces", "<th>Format / color space</th><th>Status / description</th>", d.surfaceFormats.map { "${it.format}<br>${it.colorSpace}" to "${if (it.supported) "SUPPORTED" else "NOT SUPPORTED"} · ${it.classification}<br>${it.description}" })
        table("Present modes", "<th>Mode</th><th>Value</th>", d.presentModes.map { it to "supported" })
        append("</div>")
    }
    append("</body></html>")
}

private enum class SupportFilter { ALL, SUPPORTED, UNSUPPORTED }

@Composable
private fun SupportFilterRow(selected: SupportFilter, onSelected: (SupportFilter) -> Unit) {
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(selected = selected == SupportFilter.ALL, onClick = { onSelected(SupportFilter.ALL) }, label = { Text("All") })
        FilterChip(selected = selected == SupportFilter.SUPPORTED, onClick = { onSelected(SupportFilter.SUPPORTED) }, label = { Text("Supported") })
        FilterChip(selected = selected == SupportFilter.UNSUPPORTED, onClick = { onSelected(SupportFilter.UNSUPPORTED) }, label = { Text("Not supported") })
    }
}

private val KNOWN_VULKAN_EXTENSIONS = setOf(
    "VK_ANDROID_external_format_resolve", "VK_ANDROID_external_memory_android_hardware_buffer", "VK_EXT_4444_formats",
    "VK_EXT_astc_decode_mode", "VK_EXT_blend_operation_advanced", "VK_EXT_border_color_swizzle", "VK_EXT_calibrated_timestamps",
    "VK_EXT_color_write_enable", "VK_EXT_conditional_rendering", "VK_EXT_conservative_rasterization", "VK_EXT_custom_border_color",
    "VK_EXT_depth_clamp_zero_one", "VK_EXT_depth_clip_control", "VK_EXT_depth_clip_enable", "VK_EXT_depth_range_unrestricted",
    "VK_EXT_descriptor_buffer", "VK_EXT_descriptor_indexing", "VK_EXT_device_address_binding_report", "VK_EXT_device_fault",
    "VK_EXT_device_memory_report", "VK_EXT_dynamic_rendering_unused_attachments", "VK_EXT_extended_dynamic_state", "VK_EXT_extended_dynamic_state2",
    "VK_EXT_extended_dynamic_state3", "VK_EXT_fragment_density_map", "VK_EXT_fragment_density_map2", "VK_EXT_global_priority",
    "VK_EXT_global_priority_query", "VK_EXT_host_query_reset", "VK_EXT_image_2d_view_of_3d", "VK_EXT_image_compression_control",
    "VK_EXT_image_compression_control_swapchain", "VK_EXT_image_drm_format_modifier", "VK_EXT_image_robustness", "VK_EXT_index_type_uint8",
    "VK_EXT_inline_uniform_block", "VK_EXT_line_rasterization", "VK_EXT_pipeline_creation_cache_control", "VK_EXT_pipeline_robustness",
    "VK_EXT_private_data", "VK_EXT_shader_atomic_float", "VK_EXT_shader_demote_to_helper_invocation", "VK_EXT_shader_image_atomic_int64",
    "VK_EXT_shader_subgroup_ballot", "VK_EXT_shader_subgroup_vote", "VK_EXT_subgroup_size_control", "VK_EXT_swapchain_colorspace",
    "VK_EXT_transform_feedback", "VK_EXT_vertex_attribute_divisor", "VK_EXT_vertex_input_dynamic_state", "VK_EXT_ycbcr_2plane_444_formats",
    "VK_GOOGLE_display_timing", "VK_IMG_filter_cubic", "VK_KHR_16bit_storage", "VK_KHR_8bit_storage", "VK_KHR_acceleration_structure",
    "VK_KHR_bind_memory2", "VK_KHR_buffer_device_address", "VK_KHR_calibrated_timestamps", "VK_KHR_copy_commands2", "VK_KHR_create_renderpass2",
    "VK_KHR_deferred_host_operations", "VK_KHR_depth_stencil_resolve", "VK_KHR_descriptor_update_template", "VK_KHR_device_group",
    "VK_KHR_draw_indirect_count", "VK_KHR_driver_properties", "VK_KHR_dynamic_rendering", "VK_KHR_external_fence", "VK_KHR_external_fence_capabilities",
    "VK_KHR_external_memory", "VK_KHR_external_memory_capabilities", "VK_KHR_external_semaphore", "VK_KHR_external_semaphore_capabilities",
    "VK_KHR_format_feature_flags2", "VK_KHR_fragment_shader_barycentric", "VK_KHR_fragment_shading_rate", "VK_KHR_get_memory_requirements2",
    "VK_KHR_get_physical_device_properties2", "VK_KHR_global_priority", "VK_KHR_image_format_list", "VK_KHR_imageless_framebuffer",
    "VK_KHR_incremental_present", "VK_KHR_index_type_uint8", "VK_KHR_maintenance1", "VK_KHR_maintenance2", "VK_KHR_maintenance3",
    "VK_KHR_maintenance4", "VK_KHR_maintenance5", "VK_KHR_maintenance6", "VK_KHR_multiview", "VK_KHR_pipeline_library", "VK_KHR_present_id",
    "VK_KHR_present_wait", "VK_KHR_push_descriptor", "VK_KHR_ray_query", "VK_KHR_ray_tracing_pipeline", "VK_KHR_relaxed_block_layout",
    "VK_KHR_sampler_mirror_clamp_to_edge", "VK_KHR_sampler_ycbcr_conversion", "VK_KHR_separate_depth_stencil_layouts", "VK_KHR_shader_atomic_int64",
    "VK_KHR_shader_clock", "VK_KHR_shader_float16_int8", "VK_KHR_shader_float_controls", "VK_KHR_shader_non_semantic_info", "VK_KHR_shader_subgroup_extended_types",
    "VK_KHR_shader_subgroup_uniform_control_flow", "VK_KHR_shader_terminate_invocation", "VK_KHR_shared_presentable_image", "VK_KHR_spirv_1_4",
    "VK_KHR_storage_buffer_storage_class", "VK_KHR_swapchain", "VK_KHR_synchronization2", "VK_KHR_timeline_semaphore", "VK_KHR_uniform_buffer_standard_layout",
    "VK_KHR_variable_pointers", "VK_KHR_vertex_attribute_divisor", "VK_KHR_vulkan_memory_model", "VK_KHR_workgroup_memory_explicit_layout",
    "VK_KHR_zero_initialize_workgroup_memory", "VK_NV_optical_flow", "VK_QCOM_filter_cubic_weights", "VK_QCOM_fragment_density_map_offset",
    "VK_QCOM_image_processing", "VK_QCOM_image_processing2", "VK_QCOM_multiview_per_view_render_areas", "VK_QCOM_multiview_per_view_viewports",
    "VK_QCOM_render_pass_shader_resolve", "VK_QCOM_render_pass_store_ops", "VK_QCOM_render_pass_transform", "VK_QCOM_rotated_copy_commands",
    "VK_QCOM_tile_properties", "VK_QCOM_ycbcr_degamma"
)

private fun List<String>.distinctScopes(): String = distinct().joinToString(" / ")

private data class NavigationItem(val page: Page, val label: String, val icon: Int)

private fun selectedNavigationPage(page: Page): Page = when (page) {
    Page.Features, Page.Memory, Page.Queues, Page.Formats, Page.Properties, Page.Settings, Page.Info -> Page.Overview
    else -> page
}

private fun navigationItems(): List<NavigationItem> = listOf(
    NavigationItem(Page.Overview, "Overview", R.drawable.ic_home),
    NavigationItem(Page.Vulkan, "Vulkan", R.drawable.ic_vulkan),
    NavigationItem(Page.Surface, "Surface", R.drawable.ic_surface),
    NavigationItem(Page.Display, "Display", R.drawable.ic_display),
    NavigationItem(Page.Extensions, "Extensions", R.drawable.ic_extensions)
)

private fun pageIcon(page: Page): Int = when (page) {
    Page.Overview -> R.drawable.ic_home
    Page.Vulkan -> R.drawable.ic_vulkan
    Page.Display -> R.drawable.ic_display
    Page.Surface -> R.drawable.ic_surface
    Page.Features -> R.drawable.ic_features
    Page.Memory -> R.drawable.ic_memory
    Page.Queues -> R.drawable.ic_queues
    Page.Formats -> R.drawable.ic_formats
    Page.Properties -> R.drawable.ic_properties
    Page.Extensions -> R.drawable.ic_extensions
    Page.Profiles -> R.drawable.ic_extensions
    Page.Settings -> R.drawable.ic_settings
    Page.Info -> R.drawable.ic_info
}

private data class VendorInfo(val name: String, val logo: Int)

private fun vendorInfo(vendorId: Long): VendorInfo = when (vendorId) {
    0x5143L -> VendorInfo("Qualcomm", R.drawable.gpu_vendor_qualcomm)
    0x13B5L -> VendorInfo("Arm", R.drawable.gpu_vendor_arm)
    0x1010L -> VendorInfo("Imagination Technologies", R.drawable.gpu_vendor_imagination)
    0x19E5L -> VendorInfo("Huawei", R.drawable.gpu_vendor_huawei)
    0x10DEL -> VendorInfo("NVIDIA", R.drawable.gpu_vendor_nvidia)
    0x1002L -> VendorInfo("AMD", R.drawable.gpu_vendor_amd)
    0x8086L -> VendorInfo("Intel", R.drawable.gpu_vendor_intel)
    0x144DL -> VendorInfo("Samsung", R.drawable.gpu_vendor_samsung)
    0x14E4L -> VendorInfo("Broadcom", R.drawable.gpu_vendor_broadcom)
    0x10001L -> VendorInfo("Vivante / VeriSilicon", R.drawable.gpu_vendor_vsi)
    0x10002L -> VendorInfo("VeriSilicon", R.drawable.gpu_vendor_vsi)
    else -> VendorInfo("Unknown vendor", R.drawable.gpu_vendor_unknown)
}

@Composable
private fun VendorLogo(vendorId: Long?, deviceName: String?, modifier: Modifier) {
    val normalizedName = deviceName.orEmpty().lowercase()
    val info = if (normalizedName.contains("maleoon")) VendorInfo("Huawei", R.drawable.gpu_vendor_huawei) else vendorInfo(vendorId ?: -1L)
    Card(colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFF111111)), shape = RoundedCornerShape(18.dp), modifier = modifier) {
        Image(
            painter = painterResource(info.logo),
            contentDescription = info.name,
            modifier = Modifier.fillMaxSize().padding(8.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun LoadingView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Inspecting Vulkan…", color = ComposeColor(0xFF9E9E9E)) }
}

@Composable
private fun EmptyState(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(message) }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFF121212)), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}

@Composable
private fun MetricCard(title: String, value: String, modifier: Modifier) {
    Card(colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFF121212)), shape = RoundedCornerShape(22.dp), modifier = modifier) {
        Column(Modifier.padding(17.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(title, color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.labelMedium)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun StatusChip(text: String) {
    AssistChip(onClick = {}, label = { Text(text) })
}

@Composable
private fun KeyValue(key: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
        Text(key, color = ComposeColor(0xFF8F8F8F), modifier = Modifier.weight(0.9f))
        Text(value, modifier = Modifier.weight(1.1f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
    }
}

@Composable
private fun DataRow(name: String, state: String, positive: Boolean) {
    Row(Modifier.fillMaxWidth().background(ComposeColor(0xFF111111), RoundedCornerShape(16.dp)).padding(15.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(name, modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis)
        Text(state, color = if (positive) ComposeColor(0xFFFFFFFF) else ComposeColor(0xFF777777), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelSmall)
    }
}

private fun formatBytes(value: Long): String {
    if (value <= 0) return "0 B"
    val units = arrayOf("B", "KiB", "MiB", "GiB", "TiB")
    var number = value.toDouble()
    var index = 0
    while (number >= 1024.0 && index < units.lastIndex) {
        number /= 1024.0
        index++
    }
    return String.format(java.util.Locale.US, "%.2f %s", number, units[index])
}
