@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.efishell.vulkanscope

import android.app.ActivityManager
import android.graphics.Color
import android.os.Bundle
import android.os.Build
import android.os.Process
import android.view.Surface
import android.util.Log
import android.util.Base64
import android.hardware.display.DisplayManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.provider.Settings
import androidx.core.content.FileProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import java.io.File
import java.io.FileOutputStream
import java.io.FileInputStream
import java.io.InputStream
import java.util.zip.ZipInputStream
import java.util.Collections
import okhttp3.Call
import okhttp3.Request
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.ui.input.key.type
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.key
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject



class MainActivity : ComponentActivity() {

    private lateinit var prefs: android.content.SharedPreferences
    private var turnipSupport by mutableStateOf(TurnipSupport.UNKNOWN)
    private var driverMode by mutableStateOf(DriverMode.SYSTEM)
    private val surfaceLock = Any()
    private val collectMutex = Mutex()
    private val probeMutex = Mutex()
    private val driverBundleMutex = Mutex()
    private val backgroundProbeMutexes = List(BACKGROUND_PROBE_LANES) { Mutex() }
    private val backgroundProbeServices = arrayOf(
        VulkanProbeServiceBg0::class.java,
        VulkanProbeServiceBg1::class.java,
        VulkanProbeServiceBg2::class.java,
        VulkanProbeServiceBg3::class.java,
        VulkanProbeServiceBg4::class.java,
        VulkanProbeServiceBg5::class.java
    )
    private val backgroundProbeProcessSuffixes = arrayOf(
        ":vulkan_probe_bg0",
        ":vulkan_probe_bg1",
        ":vulkan_probe_bg2",
        ":vulkan_probe_bg3",
        ":vulkan_probe_bg4",
        ":vulkan_probe_bg5"
    )
    private var currentSurface: Surface? = null
    private var surfaceGeneration = 0L
    private var surfaceRefreshPending = false
    private var surfaceHostGeneration by mutableStateOf(0L)
    private var driverSurfaceRebindPending = false
    private var latestReport by mutableStateOf<VulkanReport?>(null)
    private var displayReportState by mutableStateOf<DisplayReport?>(null)
    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) { refreshDisplayReport() }
        override fun onDisplayRemoved(displayId: Int) { refreshDisplayReport() }
        override fun onDisplayChanged(displayId: Int) { refreshDisplayReport() }
    }
    private var reportLoading by mutableStateOf(true)
    private var collectionStatus by mutableStateOf(CollectionStatus.IDLE)
    private var updateStatus by mutableStateOf<UpdateStatus>(UpdateStatus.Hidden)
    private var updateCheckInFlight by mutableStateOf(false)
    private var networkBannerState by mutableStateOf(NetworkBannerState.HIDDEN)
    private var networkBannerGeneration = 0L
    private var lastValidatedNetwork: Boolean? = null
    private var trackedDefaultNetwork: Network? = null
    private var validatedNetworkAvailable by mutableStateOf(false)
    private var networkStateKnown by mutableStateOf(false)
    private var pendingDriverModeConfirmation by mutableStateOf<DriverMode?>(null)
    private var pendingTurnipActivationSlot by mutableStateOf<Int?>(null)
    private var turnipManagerRevision by mutableIntStateOf(0)
    private var turnipManagerBusy by mutableStateOf(false)
    private var turnipFileManagerState by mutableStateOf(TurnipFileManagerState())
    private var turnipFileManagerScanJob: Job? = null
    private var turnipFileManagerScanGeneration = 0L
    private var storagePermissionDeniedFeedback by mutableStateOf(false)
    private var awaitingAllFilesAccessReturn = false
    private var pendingSharedStorageGranted: (() -> Unit)? = null
    private var pendingSharedStorageDenied: (() -> Unit)? = null
    private var storagePermissionFeedbackGeneration = 0L
    private var networkCallbackRegistered = false
    private var updateConfirmation by mutableStateOf<AppUpdate?>(null)
    private var updateTransferState by mutableStateOf<UpdateTransferState?>(null)
    private var updateCancelConfirmationVisible by mutableStateOf(false)
    private var directUpdatesEnabled by mutableStateOf(true)
    private var directUpdatesConsentVisible by mutableStateOf(false)
    private var openingAnimationEnabled by mutableStateOf(true)
    private val startupActivityStartNanos = System.nanoTime()
    private var startupTimingColdLaunch = true
    private var startupGateDelayMs: Long? = null
    private var startupGateOpen by mutableStateOf(false)
    private var platformSplashExited by mutableStateOf(false)
    private var startupPostAnimationWorkStarted = false
    private var activityStarted = false
    private var displayListenerRegistered = false
    private var pendingUpdateApk: File? = null
    private var updateCheckJob: Job? = null
    private var updateStatusHideJob: Job? = null
    private var updateDownloadJob: Job? = null
    @Volatile private var updateDownloadPaused = false
    @Volatile private var updateDownloadCancelRequested = false
    @Volatile private var activeUpdateCheckCall: Call? = null
    @Volatile private var activeUpdateDownloadCall: Call? = null
    private var collectionInFlight = false
    @Volatile private var driverImportInFlight = false
    private var driverModeValidationJob: Job? = null
    private var collectionPending = false
    private val pendingCollectionTasks = mutableSetOf<String>()
    private var collectionGeneration = 0L
    private var collectionCompletionJob: Job? = null
    private val backgroundQueryGroups = (ISOLATED_CORE_GROUPS.keys + ISOLATED_ADVANCED_GROUPS.keys).toSet()
    private val activityScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val requestedQueryGroups = Collections.synchronizedSet(mutableSetOf<String>())
    private var queryTimingMs by mutableStateOf<Map<String, Long>>(emptyMap())
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            activityScope.launch {
                trackedDefaultNetwork = network
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    delay(80L)
                    if (trackedDefaultNetwork == network) {
                        val capabilities = runCatching { getSystemService(ConnectivityManager::class.java).getNetworkCapabilities(network) }.getOrNull()
                        applyValidatedNetworkState(capabilities?.let(::hasValidatedInternetCapabilities) == true)
                    }
                }
            }
        }
        override fun onLost(network: Network) {
            activityScope.launch {
                if (trackedDefaultNetwork == network) {
                    trackedDefaultNetwork = null
                    applyValidatedNetworkState(false)
                }
            }
        }
        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            activityScope.launch {
                trackedDefaultNetwork = network
                applyValidatedNetworkState(hasValidatedInternetCapabilities(networkCapabilities))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        activityStarted = true
        if (startupGateOpen) startRuntimeObservers()
    }

    override fun onStop() {
        activityStarted = false
        if (networkCallbackRegistered) {
            runCatching { getSystemService(ConnectivityManager::class.java).unregisterNetworkCallback(networkCallback) }
            networkCallbackRegistered = false
        }
        if (displayListenerRegistered) {
            runCatching { getSystemService(DisplayManager::class.java).unregisterDisplayListener(displayListener) }
            displayListenerRegistered = false
        }
        super.onStop()
    }

    private fun startRuntimeObservers() {
        if (!startupGateOpen || !activityStarted) return
        if (!displayListenerRegistered) {
            getSystemService(DisplayManager::class.java).registerDisplayListener(displayListener, null)
            displayListenerRegistered = true
        }
        refreshDisplayReport()
        registerNetworkStateCallback()
        if (startupPostAnimationWorkStarted) return
        startupPostAnimationWorkStarted = true
        val surfaceReadyForCollection = synchronized(surfaceLock) { currentSurface?.isValid == true }
        if (surfaceReadyForCollection && latestReport == null && !collectionInFlight && pendingCollectionTasks.isEmpty()) requestReportCollection()
        if (directUpdatesEnabled) {
            activityScope.launch { checkForApplicationUpdate(showProgress = false) }
        } else {
            showDirectUpdatesDisabledIntroIfFirstInstall()
        }
    }

    private fun completeOpeningSequence() {
        if (startupTimingColdLaunch && startupGateDelayMs == null) startupGateDelayMs = elapsedMillis(startupActivityStartNanos)
        startupGateOpen = true
        startRuntimeObservers()
    }

    private fun armOpeningSequenceWatchdog() {
        if (!openingAnimationEnabled || startupGateOpen) return
        activityScope.launch {
            delay(900L)
            if (!startupGateOpen && !platformSplashExited) platformSplashExited = true
            delay(2_100L)
            if (!startupGateOpen) completeOpeningSequence()
        }
    }

    private fun persistOpeningAnimationPreference(enabled: Boolean) {
        openingAnimationEnabled = enabled
        prefs.edit().putBoolean("opening_animation_enabled", enabled).apply()
    }

    private fun registerNetworkStateCallback() {
        if (networkCallbackRegistered) return
        val manager = getSystemService(ConnectivityManager::class.java)
        runCatching {
            manager.registerDefaultNetworkCallback(networkCallback)
            networkCallbackRegistered = true
        }
        trackedDefaultNetwork = runCatching { manager.activeNetwork }.getOrNull()
        applyValidatedNetworkState(validatedDefaultNetwork())
    }

    private fun validatedDefaultNetwork(): Boolean = hasValidatedInternet(this)

    private fun applyValidatedNetworkState(validated: Boolean) {
        val previous = lastValidatedNetwork
        validatedNetworkAvailable = validated
        networkStateKnown = true
        if (previous == null) {
            lastValidatedNetwork = validated
            networkBannerState = NetworkBannerState.HIDDEN
            return
        }
        if (previous == validated) return
        lastValidatedNetwork = validated
        networkBannerGeneration += 1L
        val generation = networkBannerGeneration
        networkBannerState = if (validated) NetworkBannerState.CONNECTED else NetworkBannerState.DISCONNECTED
        activityScope.launch {
            var remainingVisibleMillis = 4_500L
            while (networkBannerGeneration == generation && remainingVisibleMillis > 0L) {
                val step = minOf(remainingVisibleMillis, 100L)
                delay(step)
                remainingVisibleMillis -= step
            }
            if (networkBannerGeneration == generation) networkBannerState = NetworkBannerState.HIDDEN
        }
    }

    override fun onResume() {
        super.onResume()
        if (startupGateOpen) refreshDisplayReport()
        val pending = pendingUpdateApk
        if (pending != null && pending.exists() && directUpdatesEnabled && (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || packageManager.canRequestPackageInstalls())) {
            pendingUpdateApk = null
            launchPackageInstaller(pending)
        } else if (pending != null && !directUpdatesEnabled) {
            runCatching { pending.delete() }
            pendingUpdateApk = null
        }
        if (awaitingAllFilesAccessReturn) {
            awaitingAllFilesAccessReturn = false
            val granted = pendingSharedStorageGranted
            val denied = pendingSharedStorageDenied
            pendingSharedStorageGranted = null
            pendingSharedStorageDenied = null
            if (android.os.Environment.isExternalStorageManager()) granted?.invoke() else {
                showStoragePermissionDeniedFeedback()
                denied?.invoke()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("startup_gate_open", startupGateOpen)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        activeUpdateCheckCall?.cancel()
        activeUpdateDownloadCall?.cancel()
        updateCheckJob?.cancel()
        updateStatusHideJob?.cancel()
        updateDownloadJob?.cancel()
        turnipFileManagerScanJob?.cancel()
        stopAllVulkanProbeProcesses()
        activityScope.cancel()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        startupTimingColdLaunch = savedInstanceState == null
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        splashScreen.setOnExitAnimationListener { provider ->
            provider.view.animate()
                .alpha(0f)
                .scaleX(1.08f)
                .scaleY(1.08f)
                .setDuration(360L)
                .withEndAction {
                    provider.remove()
                    platformSplashExited = true
                }
                .start()
        }
        window.navigationBarColor = Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        prefs = getSharedPreferences("settings", MODE_PRIVATE)
        directUpdatesEnabled = prefs.getBoolean("direct_updates_enabled", true)
        openingAnimationEnabled = prefs.getBoolean("opening_animation_enabled", true)
        startupGateOpen = savedInstanceState?.getBoolean("startup_gate_open", false) == true || !openingAnimationEnabled
        platformSplashExited = savedInstanceState != null
        if (runCatching { migrateLegacyTurnipBundleIfNeeded(filesDir, prefs) }.getOrDefault(false)) turnipManagerRevision += 1
        driverMode = DriverMode.values().find { it.name == prefs.getString("driver_mode", DriverMode.SYSTEM.name) } ?: DriverMode.SYSTEM
        if (!isTurnipPlatformEligible()) turnipSupport = TurnipSupport.UNSUPPORTED
        val suppressDesktopSecondaryInput = isChromeOsRuntime(this) || isAndroidPcFormFactor(this)
        setContent {
            CompositionLocalProvider(LocalSharedStorageAccessRequest provides { granted, denied -> requestSharedStorageAccess(granted, denied) }) {
            Box(Modifier.fillMaxSize().consumeDesktopSecondaryMouseInput(suppressDesktopSecondaryInput)) {
            if (startupGateOpen || !openingAnimationEnabled) {
            VulkanScopeApp(
                displayReport = displayReportState ?: DisplayReport("Unknown", "Unknown", null, "Unknown", emptyList(), "Unknown", "Unknown", "Unknown", emptyList(), "unknown"),
                report = latestReport,
                loading = reportLoading,
                collectionStatus = collectionStatus,
                updateStatus = updateStatus,
                updateCheckInFlight = updateCheckInFlight,
                networkBannerState = networkBannerState,
                validatedNetworkAvailable = validatedNetworkAvailable,
                networkStateKnown = networkStateKnown,
                updateConfirmation = updateConfirmation,
                updateTransferState = updateTransferState,
                updateCancelConfirmationVisible = updateCancelConfirmationVisible,
                onRequestUpdateConfirmation = { update -> updateConfirmation = update },
                onDismissUpdateConfirmation = { updateConfirmation = null },
                onConfirmUpdateDownload = { update ->
                    updateConfirmation = null
                    startUpdateDownload(update)
                },
                onPauseUpdateDownload = { pauseUpdateDownload() },
                onResumeUpdateDownload = { resumeUpdateDownload() },
                onRequestCancelUpdateDownload = { requestCancelUpdateDownload() },
                onDismissCancelUpdateDownload = { dismissCancelUpdateDownload() },
                onConfirmCancelUpdateDownload = { confirmCancelUpdateDownload() },
                onInstallDownloadedUpdate = { installDownloadedUpdate() },
                onCloseUpdateTransfer = { closeUpdateTransfer() },
                onCheckForUpdates = { checkForApplicationUpdate(showProgress = true) },
                directUpdatesEnabled = directUpdatesEnabled,
                directUpdatesConsentVisible = directUpdatesConsentVisible,
                onDirectUpdatesChanged = { enabled -> requestDirectUpdatesChanged(enabled) },
                openingAnimationEnabled = openingAnimationEnabled,
                onOpeningAnimationChanged = { enabled -> persistOpeningAnimationPreference(enabled) },
                onDismissDirectUpdatesConsent = { directUpdatesConsentVisible = false },
                onConfirmDirectUpdatesConsent = { confirmDirectUpdatesConsent() },
                surfaceReady = { hostGeneration, surface ->
                    val changed = synchronized(surfaceLock) {
                        if (hostGeneration != surfaceHostGeneration) {
                            false
                        } else if (currentSurface === surface) {
                            false
                        } else {
                            currentSurface = surface
                            surfaceGeneration += 1L
                            surfaceRefreshPending = true
                            true
                        }
                    }
                    if (changed && startupGateOpen && activityStarted) {
                        if (driverSurfaceRebindPending && hostGeneration == surfaceHostGeneration) {
                            driverSurfaceRebindPending = false
                            requestReportCollection()
                        } else if (latestReport == null && !collectionInFlight && pendingCollectionTasks.isEmpty()) {
                            requestReportCollection()
                        } else if (latestReport != null) {
                            requestSurfaceRefresh(surface)
                        }
                    }
                },
                surfaceDestroyed = { hostGeneration, surface ->
                    synchronized(surfaceLock) {
                        if (hostGeneration != surfaceHostGeneration) {
                            return@synchronized
                        }
                        if (currentSurface === surface) {
                            currentSurface = null
                            surfaceGeneration += 1L
                            surfaceRefreshPending = false
                        }
                    }
                },
                surfaceHostGeneration = surfaceHostGeneration,
                driverMode = driverMode,
                turnipSupport = turnipSupport,
                turnipManagerRevision = turnipManagerRevision,
                turnipManagerBusy = turnipManagerBusy,
                storagePermissionDeniedFeedback = storagePermissionDeniedFeedback,
                onDriverModeChanged = { mode -> requestDriverModeChange(mode) },
                pendingDriverModeConfirmation = pendingDriverModeConfirmation,
                onConfirmDriverModeChange = { confirmDriverModeChange() },
                onDismissDriverModeConfirmation = { pendingDriverModeConfirmation = null },
                pendingTurnipActivationSlot = pendingTurnipActivationSlot,
                onConfirmTurnipActivation = { confirmManagedTurnipDriverActivation() },
                onDismissTurnipActivation = { pendingTurnipActivationSlot = null },
                onInstallDriverBundle = { requestDriverBundleImport() },
                onActivateTurnipDriver = { slot -> activateManagedTurnipDriver(slot) },
                onRemoveTurnipDriver = { slot -> removeManagedTurnipDriver(slot) },
                onPageOpened = { page -> requestPageQueries(page) },
                onRequestQuery = { group -> requestQueryGroup(group) },
                queryTimingMs = queryTimingMs
            )
            AnimatedVisibility(
                visible = turnipFileManagerState.visible,
                enter = fadeIn(tween(180)) + slideInVertically(tween(220, easing = FastOutSlowInEasing)) { it / 10 },
                exit = fadeOut(tween(160)) + slideOutVertically(tween(210, easing = FastOutSlowInEasing)) { it / 12 }
            ) {
                TurnipFileManagerDialog(
                    state = turnipFileManagerState,
                    onDismiss = { closeTurnipFileManager() },
                    onNavigate = { path -> loadTurnipFileManagerDirectory(path) },
                    onUp = { navigateTurnipFileManagerUp() },
                    onToggleSelection = { path -> toggleTurnipFileManagerSelection(path) },
                    onViewMode = { mode ->
                        prefs.edit().putString("turnip_file_manager_view_mode", mode.name).apply()
                        turnipFileManagerState = turnipFileManagerState.copy(viewMode = mode)
                    },
                    onSortMode = { mode ->
                        prefs.edit().putString("turnip_file_manager_sort_mode", mode.name).apply()
                        loadTurnipFileManagerDirectory(turnipFileManagerState.directoryPath, mode)
                    },
                    onDetails = { candidate -> turnipFileManagerState = turnipFileManagerState.copy(details = candidate) },
                    onDismissDetails = { turnipFileManagerState = turnipFileManagerState.copy(details = null) },
                    onImport = { importSelectedTurnipFiles() }
                )
            }
            } else {
                VulkanScopeOpeningAnimation(startAnimation = platformSplashExited, onFinished = { completeOpeningSequence() })
            }
            }
            }
        }
        armOpeningSequenceWatchdog()
        if (startupGateOpen) completeOpeningSequence()
    }

    private fun showDirectUpdatesDisabledIntroIfFirstInstall() {
        if (prefs.getBoolean("direct_updates_intro_seen", false)) return
        val packageInfo = runCatching { packageManager.getPackageInfo(packageName, 0) }.getOrNull() ?: return
        prefs.edit().putBoolean("direct_updates_intro_seen", true).apply()
        if (packageInfo.firstInstallTime != packageInfo.lastUpdateTime) return
        updateStatus = UpdateStatus.DirectUpdatesDisabledIntro
        activityScope.launch {
            kotlinx.coroutines.delay(7_000L)
            if (updateStatus is UpdateStatus.DirectUpdatesDisabledIntro) updateStatus = UpdateStatus.Hidden
        }
    }

    private fun requestDirectUpdatesChanged(enabled: Boolean) {
        if (enabled) {
            directUpdatesConsentVisible = true
        } else {
            directUpdatesEnabled = false
            directUpdatesConsentVisible = false
            prefs.edit().putBoolean("direct_updates_enabled", false).apply()
            updateCheckJob?.cancel()
            updateStatusHideJob?.cancel()
            updateDownloadJob?.cancel()
            activeUpdateCheckCall?.cancel()
            activeUpdateDownloadCall?.cancel()
            updateCheckJob = null
            updateStatusHideJob = null
            updateDownloadJob = null
            updateDownloadPaused = false
            updateDownloadCancelRequested = false
            updateCancelConfirmationVisible = false
            updateTransferState?.apk?.let { runCatching { it.delete() } }
            updateTransferState = null
            updateCheckInFlight = false
            pendingUpdateApk?.let { runCatching { it.delete() } }
            pendingUpdateApk = null
            updateStatus = UpdateStatus.Hidden
            updateConfirmation = null
        }
    }

    private fun confirmDirectUpdatesConsent() {
        directUpdatesEnabled = true
        directUpdatesConsentVisible = false
        if (updateStatus is UpdateStatus.DirectUpdatesDisabledIntro) updateStatus = UpdateStatus.Hidden
        prefs.edit().putBoolean("direct_updates_enabled", true).apply()
    }

    private fun completeReportMutationReady(): Boolean {
        val report = latestReport ?: return false
        return isCompleteReportReady(report, collectionStatus) && !collectionInFlight && pendingCollectionTasks.isEmpty() && !collectionPending && !turnipManagerBusy && !driverImportInFlight
    }

    private fun requestDriverBundleImport() {
        if (!completeReportMutationReady()) {
            android.widget.Toast.makeText(this, "Wait for the complete Vulkan® collection pass before changing the driver package.", android.widget.Toast.LENGTH_LONG).show()
            return
        }
        if (turnipManagerBusy) {
            android.widget.Toast.makeText(this, "Driver manager is busy.", android.widget.Toast.LENGTH_SHORT).show()
            return
        }
        if (Build.VERSION.SDK_INT < 28 || !Build.SUPPORTED_ABIS.contains("arm64-v8a")) {
            android.widget.Toast.makeText(this, "Turnip requires Android 9+ and arm64-v8a.", android.widget.Toast.LENGTH_LONG).show()
            return
        }
        if (turnipSupport != TurnipSupport.SUPPORTED) {
            android.widget.Toast.makeText(this, "Turnip is available only when a Qualcomm Adreno Vulkan® device is detected.", android.widget.Toast.LENGTH_LONG).show()
            return
        }
        requestSharedStorageAccess(
            onGranted = { openTurnipFileManager() },
            onDenied = { }
        )
    }

    private fun requestSharedStorageAccess(onGranted: () -> Unit, onDenied: () -> Unit) {
        if (android.os.Environment.isExternalStorageManager()) {
            onGranted()
            return
        }
        if (awaitingAllFilesAccessReturn) return
        pendingSharedStorageGranted = onGranted
        pendingSharedStorageDenied = onDenied
        awaitingAllFilesAccessReturn = true
        val specific = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:$packageName"))
        val launched = runCatching { startActivity(specific); true }.getOrElse {
            runCatching { startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)); true }.getOrDefault(false)
        }
        if (!launched) {
            awaitingAllFilesAccessReturn = false
            pendingSharedStorageGranted = null
            pendingSharedStorageDenied = null
            showStoragePermissionDeniedFeedback()
            onDenied()
        }
    }

    private fun showStoragePermissionDeniedFeedback() {
        storagePermissionFeedbackGeneration += 1L
        val generation = storagePermissionFeedbackGeneration
        storagePermissionDeniedFeedback = true
        android.widget.Toast.makeText(this, "Permission denied", android.widget.Toast.LENGTH_SHORT).show()
        activityScope.launch {
            delay(3_000L)
            if (storagePermissionFeedbackGeneration == generation) storagePermissionDeniedFeedback = false
        }
    }

    private fun turnipListingStatus(listing: TurnipDirectoryListing): String? = when {
        listing.entryLimitReached && listing.zipLimitReached -> "This folder reached the 4096-entry scan limit and the 256-ZIP validation limit."
        listing.entryLimitReached -> "This folder reached the bounded 4096-entry scan limit."
        listing.zipLimitReached -> "This folder reached the bounded 256-ZIP validation limit."
        else -> null
    }

    private fun openTurnipFileManager() {
        if (!android.os.Environment.isExternalStorageManager() || turnipManagerBusy || driverImportInFlight) return
        turnipFileManagerScanJob?.cancel()
        turnipFileManagerScanGeneration += 1L
        val generation = turnipFileManagerScanGeneration
        val requestedRoot = android.os.Environment.getExternalStorageDirectory()
        val preferredViewMode = runCatching {
            FileManagerViewMode.valueOf(prefs.getString("turnip_file_manager_view_mode", FileManagerViewMode.LIST.name) ?: FileManagerViewMode.LIST.name)
        }.getOrDefault(FileManagerViewMode.LIST)
        val preferredSortMode = runCatching {
            FileManagerSortMode.valueOf(prefs.getString("turnip_file_manager_sort_mode", FileManagerSortMode.NAME_ASC.name) ?: FileManagerSortMode.NAME_ASC.name)
        }.getOrDefault(FileManagerSortMode.NAME_ASC)
        turnipFileManagerState = TurnipFileManagerState(
            visible = true,
            rootPath = requestedRoot.absolutePath,
            directoryPath = requestedRoot.absolutePath,
            loading = true,
            viewMode = preferredViewMode,
            sortMode = preferredSortMode
        )
        turnipFileManagerScanJob = activityScope.launch {
            try {
                val setup = withContext(Dispatchers.IO) {
                    val root = requestedRoot.canonicalFile
                    if (!root.isDirectory || !root.canRead()) error("Shared storage is unavailable")
                    val managedDrivers = readManagedTurnipDrivers(this@MainActivity, prefs, driverMode)
                    val occupied = managedDrivers.size
                    val remaining = (TURNIP_MANAGER_MAX_DRIVERS - occupied).coerceAtLeast(0)
                    val importedSourceKeys = managedDrivers.mapNotNull { driver ->
                        val location = driver.info.zipLocation?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                        val name = driver.info.zipName?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                        turnipImportedSourceKey(location, name)
                    }.toSet()
                    val listing = if (remaining > 0) scanTurnipFileManagerDirectory(root, root, preferredSortMode) else TurnipDirectoryListing(emptyList(), emptyList(), false, false)
                    TurnipFileManagerSetup(root, remaining, listing, importedSourceKeys)
                }
                if (generation != turnipFileManagerScanGeneration) return@launch
                val latest = turnipFileManagerState
                if (!latest.visible) return@launch
                val root = setup.root
                val remaining = setup.remaining
                val listing = setup.listing
                if (remaining == 0) {
                    turnipFileManagerState = TurnipFileManagerState()
                    android.widget.Toast.makeText(this@MainActivity, "All 10 Turnip driver slots are occupied.", android.widget.Toast.LENGTH_LONG).show()
                } else {
                    turnipFileManagerState = latest.copy(
                        rootPath = root.path,
                        directoryPath = root.path,
                        folders = listing.folders,
                        candidates = listing.candidates,
                        importedSourceKeys = setup.importedSourceKeys,
                        maxSelectable = remaining,
                        loading = false,
                        status = turnipListingStatus(listing)
                    )
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                if (generation == turnipFileManagerScanGeneration && turnipFileManagerState.visible) {
                    turnipFileManagerState = turnipFileManagerState.copy(loading = false, status = error.message ?: "Shared storage is unavailable")
                }
            } finally {
                if (generation == turnipFileManagerScanGeneration) turnipFileManagerScanJob = null
            }
        }
    }

    private fun loadTurnipFileManagerDirectory(path: String, sortOverride: FileManagerSortMode? = null) {
        val current = turnipFileManagerState
        if (!current.visible || current.loading || current.importing || !android.os.Environment.isExternalStorageManager()) return
        turnipFileManagerScanJob?.cancel()
        turnipFileManagerScanGeneration += 1L
        val generation = turnipFileManagerScanGeneration
        val root = File(current.rootPath)
        val target = File(path)
        val requestedSort = sortOverride ?: current.sortMode
        turnipFileManagerState = current.copy(directoryPath = path, folders = emptyList(), candidates = emptyList(), loading = true, status = null, details = null, sortMode = requestedSort)
        turnipFileManagerScanJob = activityScope.launch {
            try {
                val listing = withContext(Dispatchers.IO) { scanTurnipFileManagerDirectory(root, target, requestedSort) }
                if (generation != turnipFileManagerScanGeneration) return@launch
                val latest = turnipFileManagerState
                if (!latest.visible || latest.importing) return@launch
                turnipFileManagerState = latest.copy(
                    directoryPath = path,
                    folders = listing.folders,
                    candidates = listing.candidates,
                    selectedPaths = latest.selectedPaths,
                    loading = false,
                    status = turnipListingStatus(listing)
                )
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                if (generation == turnipFileManagerScanGeneration && turnipFileManagerState.visible && !turnipFileManagerState.importing) {
                    turnipFileManagerState = turnipFileManagerState.copy(loading = false, status = error.message ?: "Unable to read this folder")
                }
            } finally {
                if (generation == turnipFileManagerScanGeneration) turnipFileManagerScanJob = null
            }
        }
    }

    private fun navigateTurnipFileManagerUp() {
        val state = turnipFileManagerState
        if (!state.visible || state.loading || state.importing || state.directoryPath == state.rootPath) return
        val parentPath = File(state.directoryPath).parent ?: return
        val rootPrefix = state.rootPath.trimEnd(File.separatorChar) + File.separator
        if (parentPath == state.rootPath || parentPath.startsWith(rootPrefix)) loadTurnipFileManagerDirectory(parentPath)
    }

    private fun toggleTurnipFileManagerSelection(path: String) {
        val state = turnipFileManagerState
        val candidate = state.candidates.firstOrNull { it.path == path } ?: return
        if (!state.visible || state.loading || state.importing) return
        if (turnipImportedSourceKey(candidate.path, candidate.name) in state.importedSourceKeys) return
        val selected = state.selectedPaths.toMutableSet()
        if (path in selected) selected.remove(path) else {
            if (selected.size >= state.maxSelectable) {
                turnipFileManagerState = state.copy(status = "Only ${state.maxSelectable} remaining Turnip slot${if (state.maxSelectable == 1) " is" else "s are"} available.")
                return
            }
            selected.add(path)
        }
        turnipFileManagerState = state.copy(selectedPaths = selected, status = null)
    }

    private fun closeTurnipFileManager() {
        if (turnipFileManagerState.importing || !turnipFileManagerState.visible) return
        turnipFileManagerScanJob?.cancel()
        turnipFileManagerScanJob = null
        turnipFileManagerScanGeneration += 1L
        turnipFileManagerState = turnipFileManagerState.copy(visible = false, details = null, status = null)
        activityScope.launch {
            kotlinx.coroutines.delay(220L)
            if (!turnipFileManagerState.visible) turnipFileManagerState = TurnipFileManagerState()
        }
    }

    private fun importSelectedTurnipFiles() {
        val state = turnipFileManagerState
        if (!state.visible || state.loading || state.importing || state.selectedPaths.isEmpty() || driverImportInFlight || turnipManagerBusy) return
        if (!completeReportMutationReady()) {
            turnipFileManagerState = state.copy(status = "Driver import is locked until the complete Vulkan® collection pass finishes.")
            return
        }
        val selected = state.selectedPaths
            .filterNot { path -> turnipImportedSourceKey(path, File(path).name) in state.importedSourceKeys }
            .take(state.maxSelectable)
        if (selected.isEmpty()) {
            turnipFileManagerState = state.copy(selectedPaths = emptySet(), status = "Selected Turnip packages are already imported or unavailable.")
            return
        }
        driverImportInFlight = true
        turnipManagerBusy = true
        turnipFileManagerState = state.copy(importing = true, status = "Validating selected Turnip packages…")
        activityScope.launch {
            var imported = 0
            var failure: Throwable? = null
            for (path in selected) {
                val candidate = withContext(Dispatchers.IO) { inspectTurnipArchive(File(path)) }
                if (candidate == null) {
                    failure = IllegalArgumentException("A selected Turnip ZIP changed or no longer passes validation: ${File(path).name}")
                    break
                }
                val sourceInfo = TurnipSourceInfo(candidate.name, candidate.path, candidate.sizeBytes, candidate.modifiedAtMillis)
                val importFailure = try {
                    withContext(Dispatchers.IO) { installDriverBundleIo(sourceInfo) { FileInputStream(candidate.path) } }
                    null
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (error: Throwable) {
                    error
                }
                if (importFailure != null) {
                    failure = importFailure
                    break
                }
                imported += 1
                turnipFileManagerState = turnipFileManagerState.copy(status = "Imported $imported / ${selected.size} validated Turnip package${if (selected.size == 1) "" else "s"}…")
            }
            driverImportInFlight = false
            turnipManagerBusy = false
            if (imported > 0) turnipManagerRevision += 1
            if (failure == null) {
                turnipFileManagerState = TurnipFileManagerState()
                android.widget.Toast.makeText(this@MainActivity, "Imported $imported Turnip package${if (imported == 1) "" else "s"}.", android.widget.Toast.LENGTH_LONG).show()
            } else {
                val remainingSelected = selected.drop(imported).toSet()
                val newlyImportedKeys = selected.take(imported).map { path -> turnipImportedSourceKey(path, File(path).name) }.toSet()
                turnipFileManagerState = turnipFileManagerState.copy(
                    importing = false,
                    importedSourceKeys = turnipFileManagerState.importedSourceKeys + newlyImportedKeys,
                    selectedPaths = remainingSelected,
                    maxSelectable = (turnipFileManagerState.maxSelectable - imported).coerceAtLeast(0),
                    status = "${if (imported > 0) "Imported $imported package${if (imported == 1) "" else "s"}. " else ""}${failure?.message ?: "Import failed"}"
                )
            }
        }
    }

    private suspend fun installDriverBundleIo(sourceInfo: TurnipSourceInfo, openInput: () -> InputStream?): Int {
        migrateLegacyTurnipBundleIfNeeded(filesDir, prefs)
        val slot = (1..TURNIP_MANAGER_MAX_DRIVERS).firstOrNull { !turnipSlotRoot(filesDir, it).exists() }
            ?: throw IllegalStateException("The 10-driver Turnip manager limit has been reached")
        val managerRoot = turnipManagerRoot(filesDir)
        if (!managerRoot.mkdirs() && !managerRoot.isDirectory) throw IllegalStateException("Unable to create the private driver manager directory")
        val finalSlotRoot = turnipSlotRoot(filesDir, slot)
        val tempSlotRoot = File(managerRoot, ".import_${java.util.UUID.randomUUID()}")
        val tempBundleRoot = File(tempSlotRoot, TURNIP_MANAGER_BUNDLE_NAME)
        val importContext = currentCoroutineContext()
        try {
            if (!tempSlotRoot.mkdir() || !tempBundleRoot.mkdir()) throw IllegalStateException("Unable to create the private driver import directory")
            var entryCount = 0
            var totalBytes = 0L
            val canonicalTempDir = tempBundleRoot.canonicalFile
            val seenArchivePaths = HashSet<String>()
            val maxEntries = 2048
            val maxTotalBytes = 64L * 1024L * 1024L
            val maxFileBytes = 32L * 1024L * 1024L
            openInput()?.use { input ->
                ZipInputStream(BoundedDriverArchiveInputStream(input, TURNIP_ARCHIVE_INPUT_MAX_BYTES)).use { zip ->
                    var entry = zip.nextEntry
                    while (entry != null) {
                        importContext.ensureActive()
                        if (++entryCount > maxEntries) throw SecurityException("Driver bundle contains too many entries")
                        if (entry.name.isBlank() || entry.name.length > 1024) throw SecurityException("Driver bundle contains an invalid path")
                        val safe = File(tempBundleRoot, entry.name).canonicalFile
                        if (!safe.path.startsWith(canonicalTempDir.path + File.separator) && safe != canonicalTempDir) throw SecurityException("Unsafe driver bundle path")
                        if (!seenArchivePaths.add(safe.path)) throw SecurityException("Driver bundle contains duplicate archive paths")
                        if (entry.isDirectory) {
                            if (!safe.mkdirs() && !safe.isDirectory) throw IllegalStateException("Unable to create a driver bundle directory")
                        } else {
                            val parent = safe.parentFile ?: throw SecurityException("Driver bundle entry has no valid parent")
                            if (!parent.mkdirs() && !parent.isDirectory) throw IllegalStateException("Unable to create a driver bundle directory")
                            var fileBytes = 0L
                            FileOutputStream(safe).use { output ->
                                if (safe.name.endsWith(".so", true) && !safe.setReadOnly()) throw SecurityException("Unable to secure a native driver library before extraction")
                                val buffer = ByteArray(32 * 1024)
                                while (true) {
                                    importContext.ensureActive()
                                    val read = zip.read(buffer)
                                    if (read <= 0) break
                                    fileBytes += read
                                    totalBytes += read
                                    if (fileBytes > maxFileBytes || totalBytes > maxTotalBytes) throw SecurityException("Driver bundle is too large")
                                    output.write(buffer, 0, read)
                                }
                                if (safe.name.endsWith(".so", true) || safe.name.equals("meta.json", true)) output.fd.sync()
                            }
                        }
                        zip.closeEntry()
                        entry = zip.nextEntry
                    }
                }
            } ?: throw IllegalStateException("Unable to read bundle")

            importContext.ensureActive()
            val bundleScan = scanTurnipBundle(tempBundleRoot, maxEntries) ?: throw SecurityException("The extracted driver bundle could not be validated within the entry safety limit")
            val metadataFiles = bundleScan.files.filter { it.name.equals("meta.json", true) }
            if (metadataFiles.size != 1) throw IllegalArgumentException("The AdrenoTools package must contain exactly one meta.json")
            val metadataFile = metadataFiles.single()
            val metadata = runCatching { JSONObject(readFileTextLimited(metadataFile, 1024 * 1024)) }.getOrElse { throw IllegalArgumentException("The AdrenoTools meta.json is invalid") }
            val schemaVersion = metadata.optInt("schemaVersion", -1)
            if (schemaVersion != 1) throw IllegalArgumentException("Unsupported AdrenoTools package schema: ${if (schemaVersion < 0) "missing" else schemaVersion}")
            val declared = metadata.optString("libraryName").trim()
            if (declared.isBlank() || declared.contains('/') || declared.contains('\\') || !declared.endsWith(".so", true)) throw IllegalArgumentException("AdrenoTools meta.json does not declare a valid Vulkan library name")
            val libraries = bundleScan.files.filter { it.name == declared }
            if (libraries.size != 1) throw IllegalArgumentException("Driver metadata must resolve to exactly one Vulkan library: $declared")
            val library = libraries.single()
            if (!declared.contains("vulkan", true)) throw IllegalArgumentException("The selected AdrenoTools package does not declare a Vulkan driver library")
            if (!library.canRead() || library.length() == 0L) throw IllegalArgumentException("The Vulkan .so driver library is empty or unreadable")
            if (!ensureTurnipNativeLibrariesReadOnly(bundleScan)) throw SecurityException("The native driver libraries could not be secured as read-only")
            val importedAt = System.currentTimeMillis()
            if (!writeTurnipSourceInfo(File(tempSlotRoot, TURNIP_MANAGER_SOURCE_NAME), sourceInfo, importedAt)) throw IllegalStateException("Unable to persist private source ZIP provenance")
            importContext.ensureActive()
            if (finalSlotRoot.exists()) throw IllegalStateException("Turnip driver slot is already occupied")
            if (!tempSlotRoot.renameTo(finalSlotRoot)) throw IllegalStateException("Unable to install the driver bundle atomically")
            Log.i("VulkanScope", "Imported validated Turnip driver slot=%02d library=%s".format(java.util.Locale.ROOT, slot, declared))
            return slot
        } catch (error: Throwable) {
            tempSlotRoot.deleteRecursively()
            throw error
        }
    }

    private fun activateManagedTurnipDriver(slot: Int) {
        if (slot !in 1..TURNIP_MANAGER_MAX_DRIVERS || driverImportInFlight || turnipManagerBusy) return
        if (!completeReportMutationReady()) {
            android.widget.Toast.makeText(this, "Wait for the complete Vulkan® collection pass before activating a Turnip driver.", android.widget.Toast.LENGTH_LONG).show()
            return
        }
        pendingTurnipActivationSlot = slot
    }

    private fun confirmManagedTurnipDriverActivation() {
        val slot = pendingTurnipActivationSlot ?: return
        pendingTurnipActivationSlot = null
        if (slot !in 1..TURNIP_MANAGER_MAX_DRIVERS || driverImportInFlight || turnipManagerBusy || !completeReportMutationReady()) return
        turnipManagerBusy = true
        activityScope.launch {
            val valid = withContext(Dispatchers.IO) { driverBundleMutex.withLock { resolveTurnipLibrary(turnipSlotBundleRoot(filesDir, slot)) != null } }
            if (!valid) {
                turnipManagerBusy = false
                turnipManagerRevision += 1
                android.widget.Toast.makeText(this@MainActivity, "The selected Turnip driver slot is invalid or incomplete.", android.widget.Toast.LENGTH_LONG).show()
                return@launch
            }
            val committed = withContext(Dispatchers.IO) { prefs.edit().putInt(TURNIP_ACTIVE_SLOT_PREF, slot).putString("turnip_bundle", turnipSlotBundleRoot(filesDir, slot).absolutePath).commit() }
            turnipManagerBusy = false
            if (!committed) {
                android.widget.Toast.makeText(this@MainActivity, "Unable to persist the selected Turnip driver.", android.widget.Toast.LENGTH_LONG).show()
                return@launch
            }
            turnipManagerRevision += 1
            applyDriverModeChange(DriverMode.TURNIP, true)
        }
    }

    private fun removeManagedTurnipDriver(slot: Int) {
        if (slot !in 1..TURNIP_MANAGER_MAX_DRIVERS || driverImportInFlight || turnipManagerBusy) return
        if (!completeReportMutationReady()) {
            android.widget.Toast.makeText(this, "Wait for the complete Vulkan® collection pass before removing a Turnip driver.", android.widget.Toast.LENGTH_LONG).show()
            return
        }
        turnipManagerBusy = true
        activityScope.launch {
            val wasSelected = prefs.getInt(TURNIP_ACTIVE_SLOT_PREF, 0) == slot
            val switchToSystem = wasSelected && driverMode == DriverMode.TURNIP
            if (wasSelected) {
                val committed = withContext(Dispatchers.IO) { prefs.edit().remove(TURNIP_ACTIVE_SLOT_PREF).remove("turnip_bundle").commit() }
                if (!committed) {
                    turnipManagerBusy = false
                    android.widget.Toast.makeText(this@MainActivity, "Unable to clear the active Turnip driver selection.", android.widget.Toast.LENGTH_LONG).show()
                    return@launch
                }
            }
            val removed = withContext(Dispatchers.IO) { driverBundleMutex.withLock { turnipSlotRoot(filesDir, slot).deleteRecursively() } }
            turnipManagerBusy = false
            turnipManagerRevision += 1
            if (switchToSystem) applyDriverModeChange(DriverMode.SYSTEM, true)
            android.widget.Toast.makeText(this@MainActivity, if (removed) "Turnip driver removed from slot %02d.".format(java.util.Locale.ROOT, slot) else "Unable to remove Turnip driver slot %02d.".format(java.util.Locale.ROOT, slot), android.widget.Toast.LENGTH_LONG).show()
        }
    }

    private fun findTurnipBundle(mode: DriverMode = driverMode): String? {
        if (mode != DriverMode.TURNIP) return null
        return activeTurnipBundleRoot(filesDir, prefs)?.absolutePath
    }

    private fun resolveInstalledTurnipIcd(): String? = activeTurnipBundleRoot(filesDir, prefs)?.let(::resolveTurnipLibrary)?.absolutePath

    private fun findTurnipIcd(mode: DriverMode = driverMode): String? = if (mode == DriverMode.TURNIP) resolveInstalledTurnipIcd() else null

    private fun isTurnipPlatformEligible(): Boolean =
        Build.VERSION.SDK_INT >= 28 && Build.SUPPORTED_ABIS.contains("arm64-v8a")

    private fun updateTurnipSupport(report: VulkanReport) {
        if (!isTurnipPlatformEligible()) {
            turnipSupport = TurnipSupport.UNSUPPORTED
            return
        }
        if (report.error != null && report.devices.isEmpty()) {
            turnipSupport = TurnipSupport.UNKNOWN
            return
        }
        val hasQualcommAdreno = report.devices.any {
            it.vendorIdRaw == 0x5143L && it.name.contains("Adreno", ignoreCase = true)
        }
        turnipSupport = if (hasQualcommAdreno) TurnipSupport.SUPPORTED else TurnipSupport.UNSUPPORTED
    }

    private fun beginCollectionTask(taskId: String) {
        collectionCompletionJob?.cancel()
        collectionCompletionJob = null
        pendingCollectionTasks.add(taskId)
        collectionStatus = CollectionStatus.COLLECTING
    }

    private fun takePendingSurfaceRefresh(): Surface? = synchronized(surfaceLock) {
        val surface = currentSurface?.takeIf { it.isValid }
        if (surfaceRefreshPending && surface != null) {
            surfaceRefreshPending = false
            surface
        } else {
            null
        }
    }

    private fun settleCollectionState() {
        if (collectionInFlight || pendingCollectionTasks.isNotEmpty()) return
        if (collectionPending && !isFinishing && !isDestroyed) {
            collectionPending = false
            synchronized(surfaceLock) { surfaceRefreshPending = false }
            requestReportCollection()
            return
        }
        val surface = takePendingSurfaceRefresh()
        if (surface != null && latestReport != null && !isFinishing && !isDestroyed) {
            requestSurfaceRefresh(surface)
        } else {
            finishCollectionStatusSoon()
        }
    }

    private fun completeCollectionTask(taskId: String) {
        pendingCollectionTasks.remove(taskId)
        settleCollectionState()
    }

    private fun requestReportCollection() {
        if (!startupGateOpen || !activityStarted) return
        if (isFinishing || isDestroyed) return
        if (driverImportInFlight) {
            collectionPending = true
            return
        }
        if (collectionInFlight || pendingCollectionTasks.isNotEmpty()) {
            collectionPending = true
            return
        }
        collectionGeneration += 1L
        val generation = collectionGeneration
        val modeSnapshot = driverMode
        val collectionStartNanos = System.nanoTime()
        requestedQueryGroups.clear()
        queryTimingMs = startupGateDelayMs?.let { mapOf("startup/gate_delay" to it) } ?: emptyMap()
        pendingCollectionTasks.clear()
        pendingCollectionTasks.add("$generation:base")
        pendingCollectionTasks.add("$generation:enrichment")
        backgroundQueryGroups.forEach { pendingCollectionTasks.add("$generation:group:$it") }
        collectionInFlight = true
        reportLoading = true
        collectionStatus = CollectionStatus.COLLECTING
        activityScope.launch {
            try {
                val basePhaseTimings = linkedMapOf<String, Long>()
                val baseStartNanos = System.nanoTime()
                val base = runCatching { collectReport(modeSnapshot, basePhaseTimings) }.getOrElse { e ->
                    Log.e("VulkanScope", "Vulkan base orchestration failed", e)
                    VulkanReport("Unknown", emptyList(), emptyList(), emptyList(), "Vulkan base report unavailable: ${e.message ?: "probe failed"}")
                }
                val baseElapsedMs = elapsedMillis(baseStartNanos)
                if (generation != collectionGeneration || modeSnapshot != driverMode) {
                    pendingCollectionTasks.removeAll { it.startsWith("$generation:") }
                    return@launch
                }
                queryTimingMs = queryTimingMs + basePhaseTimings + ("collection/base_total" to baseElapsedMs)
                latestReport = base
                updateTurnipSupport(base)
                reportLoading = false
                completeCollectionTask("$generation:base")
                val enrichmentPhaseTimings = linkedMapOf<String, Long>()
                val enrichmentStartNanos = System.nanoTime()
                val enriched = runCatching { enrichReport(base, modeSnapshot, enrichmentPhaseTimings) }.getOrElse { e ->
                    Log.e("VulkanScope", "Vulkan advanced enrichment failed", e)
                    markSurfaceProbeState(markMetadataProbeUnavailable(base, e.message ?: "Unexpected enrichment failure."), "unavailable", e.message ?: "Unexpected enrichment failure.")
                }
                val enrichmentElapsedMs = elapsedMillis(enrichmentStartNanos)
                if (generation != collectionGeneration || modeSnapshot != driverMode) {
                    pendingCollectionTasks.removeAll { it.startsWith("$generation:") }
                    return@launch
                }
                queryTimingMs = queryTimingMs + enrichmentPhaseTimings + ("collection/enrichment_total" to enrichmentElapsedMs)
                latestReport = enriched
                completeCollectionTask("$generation:enrichment")
                val backgroundStartNanos = System.nanoTime()
                startBackgroundInformationCollection(generation, modeSnapshot)
                queryTimingMs = queryTimingMs +
                    ("collection/background_total" to elapsedMillis(backgroundStartNanos)) +
                    ("collection/total" to elapsedMillis(collectionStartNanos))
            } finally {
                collectionInFlight = false
                settleCollectionState()
            }
        }
    }

    private fun requestSurfaceRefresh(surface: Surface) {
        if (isFinishing || isDestroyed) return
        if (driverImportInFlight) {
            collectionPending = true
            return
        }
        val surfaceToken = synchronized(surfaceLock) {
            if (currentSurface !== surface || !surface.isValid) return
            surfaceGeneration
        }
        if (collectionInFlight || pendingCollectionTasks.isNotEmpty()) {
            synchronized(surfaceLock) {
                if (currentSurface === surface && surfaceGeneration == surfaceToken) surfaceRefreshPending = true
            }
            return
        }
        if (latestReport == null) return
        synchronized(surfaceLock) {
            if (currentSurface === surface && surfaceGeneration == surfaceToken) surfaceRefreshPending = false
        }
        val generation = collectionGeneration
        val modeSnapshot = driverMode
        val taskId = "surface:${System.identityHashCode(surface)}:$surfaceToken:${System.nanoTime()}"
        beginCollectionTask(taskId)
        activityScope.launch {
            try {
                val base = latestReport ?: return@launch
                runCatching {
                    val rawSurface = runSurfaceProbe(surface, modeSnapshot)
                    val stillCurrent = synchronized(surfaceLock) {
                        currentSurface === surface && surfaceGeneration == surfaceToken && surface.isValid
                    }
                    if (generation == collectionGeneration && modeSnapshot == driverMode && !collectionInFlight && stillCurrent) {
                        latestReport = mergeSurfaceProbeReport(latestReport ?: base, rawSurface)
                    } else if (!stillCurrent) {
                        synchronized(surfaceLock) {
                            if (currentSurface?.isValid == true) surfaceRefreshPending = true
                        }
                    }
                }.onFailure { e ->
                    Log.e("VulkanScope", "Surface refresh failed", e)
                    val stillCurrent = synchronized(surfaceLock) {
                        currentSurface === surface && surfaceGeneration == surfaceToken && surface.isValid
                    }
                    if (generation == collectionGeneration && modeSnapshot == driverMode && !collectionInFlight && stillCurrent) {
                        latestReport = markSurfaceProbeState(latestReport ?: base, "unavailable", e.message ?: "Surface refresh failed before a complete result was produced.")
                    } else if (!stillCurrent) {
                        synchronized(surfaceLock) {
                            if (currentSurface?.isValid == true) surfaceRefreshPending = true
                        }
                    }
                }
            } finally {
                completeCollectionTask(taskId)
            }
        }
    }

    private fun backgroundProbeLaneCount(): Int {
        val activityManager = getSystemService(ActivityManager::class.java) ?: return 2
        val memoryInfo = ActivityManager.MemoryInfo()
        val memoryInfoAvailable = runCatching {
            activityManager.getMemoryInfo(memoryInfo)
            memoryInfo.totalMem > 0L
        }.getOrDefault(false)
        if (!memoryInfoAvailable) return 2
        val mib = 1024L * 1024L
        val totalMiB = memoryInfo.totalMem / mib
        val availableMiB = memoryInfo.availMem / mib
        val availableRatio = memoryInfo.availMem.toDouble() / memoryInfo.totalMem.toDouble()
        val processors = Runtime.getRuntime().availableProcessors().coerceAtLeast(1)
        return when {
            activityManager.isLowRamDevice || memoryInfo.lowMemory -> 2
            totalMiB < 3072L || availableMiB < 768L || availableRatio < 0.12 || processors < 4 -> 2
            totalMiB < 5120L || availableMiB < 1280L || availableRatio < 0.16 || processors < 6 -> 3
            totalMiB < 6144L || availableMiB < 1536L || availableRatio < 0.18 || processors < 8 -> 4
            totalMiB < 7168L || availableMiB < 2048L || availableRatio < 0.22 -> 5
            else -> BACKGROUND_PROBE_LANES
        }
    }

    private suspend fun startBackgroundInformationCollection(generation: Long = collectionGeneration, modeSnapshot: DriverMode = driverMode) {
        val report = latestReport
        if (report == null || report.devices.isEmpty()) {
            backgroundQueryGroups.forEach { completeCollectionTask("$generation:group:$it") }
            return
        }

        val enumeratedDeviceExtensions = report.devices
            .flatMap { device -> device.extensions.map { it.name } }
            .toSet()
        val exhaustiveExtensionGroups = VALIDATED_PHYSICAL_DEVICE_QUERY_EXTENSIONS
            .asSequence()
            .filter { it in enumeratedDeviceExtensions }
            .sorted()
            .map { "ext::$it" }
            .toList()
        val prioritizedBackgroundGroups = backgroundQueryGroups.toList().sortedBy { group ->
            when (group) {
                "queue2" -> 0
                "memory2" -> 1
                "format2" -> 2
                "imageFormat2" -> 3
                "external" -> 4
                "tools" -> 5
                "groups" -> 6
                "sparse" -> 7
                "videoCapabilities" -> 8
                else -> 9
            }
        }
        val allGroups = prioritizedBackgroundGroups + exhaustiveExtensionGroups
        exhaustiveExtensionGroups.forEach { pendingCollectionTasks.add("$generation:group:$it") }
        val newGroups = allGroups.filter { requestedQueryGroups.add(it) }
        allGroups.filterNot { newGroups.contains(it) }.forEach { completeCollectionTask("$generation:group:$it") }
        val deadlineNanos = System.nanoTime() + BACKGROUND_COLLECTION_BUDGET_MS * 1_000_000L
        val activeBackgroundProbeLanes = backgroundProbeLaneCount()
        val backgroundDriverPaths = withContext(Dispatchers.IO) {
            findTurnipIcd(modeSnapshot) to findTurnipBundle(modeSnapshot)
        }

        fun unavailableRaw(group: String, reason: String): String = JSONObject()
            .put("status", "unavailable")
            .put("group", group)
            .put("reason", reason)
            .put("devices", JSONArray())
            .toString()

        suspend fun publishUnavailableGroups(groups: List<String>, reason: String) {
            if (groups.isEmpty()) return
            withContext(Dispatchers.Main.immediate) {
                if (generation != collectionGeneration || modeSnapshot != driverMode) return@withContext
                var currentReport = latestReport ?: report
                for (group in groups) {
                    val raw = unavailableRaw(group, reason)
                    val extensionName = if (group.startsWith("ext::")) group.removePrefix("ext::") else ISOLATED_EXTENSION_GROUPS[group]
                    currentReport = if (extensionName != null) {
                        mergeExtensionGroupReport(currentReport, raw, group, extensionName)
                    } else {
                        val label = ISOLATED_ADVANCED_GROUPS[group] ?: ISOLATED_CORE_GROUPS[group] ?: group
                        mergeAdvancedQueryReport(currentReport, raw, group, label)
                    }
                }
                latestReport = currentReport
            }
            groups.forEach { completeCollectionTask("$generation:group:$it") }
        }

        if (generation != collectionGeneration || modeSnapshot != driverMode) {
            pendingCollectionTasks.removeAll { it.startsWith("$generation:") }
            return
        }
        val backgroundResults = coroutineScope {
            val nextGroup = java.util.concurrent.atomic.AtomicInteger(0)
            (0 until activeBackgroundProbeLanes).map { lane ->
                async(Dispatchers.IO) {
                    val laneResults = mutableListOf<Pair<Int, Triple<String, String, Long>>>()
                    while (true) {
                        val index = nextGroup.getAndIncrement()
                        if (index >= newGroups.size) break
                        val group = newGroups[index]
                        val remainingNanos = deadlineNanos - System.nanoTime()
                        val budgetReason = "Unavailable: the bounded background Vulkan detail-collection budget of ${BACKGROUND_COLLECTION_BUDGET_MS / 1000L} seconds was exhausted. The base report remains complete; this optional detail can be retried from its page."
                        if (remainingNanos <= 0L) {
                            laneResults += index to Triple(group, unavailableRaw(group, budgetReason), 0L)
                            continue
                        }
                        val remainingBudgetMs = (remainingNanos / 1_000_000L).coerceAtLeast(1L)
                        val scheduledStartNanos = System.nanoTime()
                        val raw = try {
                            withTimeout(remainingBudgetMs) {
                                runBackgroundIsolatedProbe(group, lane, modeSnapshot, backgroundDriverPaths)
                            }
                        } catch (cancelled: CancellationException) {
                            throw cancelled
                        } catch (error: Throwable) {
                            Log.e("VulkanScope", "Vulkan background query failed: $group", error)
                            unavailableRaw(group, error.message ?: "The dedicated background query failed.")
                        }.ifBlank {
                            unavailableRaw(group, "The dedicated background query returned no data.")
                        }
                        laneResults += index to Triple(group, raw, elapsedMillis(scheduledStartNanos))
                    }
                    laneResults
                }
            }.awaitAll().flatten().sortedBy { it.first }.map { it.second }
        }
        withContext(Dispatchers.Main.immediate) {
            if (generation != collectionGeneration || modeSnapshot != driverMode) {
                pendingCollectionTasks.removeAll { it.startsWith("$generation:") }
                return@withContext
            }
            var currentReport = latestReport ?: report
            val timingMap = queryTimingMs.toMutableMap()
            timingMap["collection/background_process_lanes"] = activeBackgroundProbeLanes.toLong()
            for ((group, raw, scheduledElapsedMs) in backgroundResults) {
                val hostElapsedMs = timingMap["service/$group/host_total"] ?: scheduledElapsedMs
                timingMap["probe_total/$group"] = hostElapsedMs
                timingMap["probe_queue_wait/$group"] = (scheduledElapsedMs - hostElapsedMs).coerceAtLeast(0L)
                val extensionName = if (group.startsWith("ext::")) group.removePrefix("ext::") else ISOLATED_EXTENSION_GROUPS[group]
                currentReport = if (extensionName != null) {
                    mergeExtensionGroupReport(currentReport, raw, group, extensionName)
                } else {
                    val label = ISOLATED_ADVANCED_GROUPS[group] ?: ISOLATED_CORE_GROUPS[group] ?: group
                    mergeAdvancedQueryReport(currentReport, raw, group, label)
                }
            }
            queryTimingMs = timingMap
            latestReport = currentReport
        }
        newGroups.forEach { completeCollectionTask("$generation:group:$it") }
    }

    private fun finishCollectionStatusSoon(delayMillis: Long = 2000L) {
        if (collectionInFlight || pendingCollectionTasks.isNotEmpty()) return
        collectionCompletionJob?.cancel()
        collectionStatus = if (collectionFinishedSuccessfully(latestReport)) CollectionStatus.COMPLETED else CollectionStatus.FAILED
        if (collectionStatus == CollectionStatus.FAILED) return
        collectionCompletionJob = activityScope.launch {
            delay(delayMillis)
            if (!collectionInFlight && pendingCollectionTasks.isEmpty()) {
                collectionStatus = CollectionStatus.IDLE
            }
        }
    }

    private fun checkForApplicationUpdate(showProgress: Boolean) {
        if (!validatedDefaultNetwork()) {
            if (showProgress) updateStatus = UpdateStatus.Failed("No validated internet connection. Internet-dependent update checks are unavailable until connectivity returns.")
            return
        }
        if (!directUpdatesEnabled) {
            if (showProgress) updateStatus = UpdateStatus.Failed("Direct GitHub updates are disabled. Use Obtainium for external update management or enable them in Settings.")
            return
        }
        if (updateCheckInFlight || updateDownloadJob != null || updateTransferState != null) return
        updateStatusHideJob?.cancel()
        updateStatusHideJob = null
        updateCheckInFlight = true
        updateCheckJob = activityScope.launch {
            if (showProgress) updateStatus = UpdateStatus.Checking
            val result = try {
                withContext(Dispatchers.IO) { fetchLatestCompatibleUpdateResult() }
            } finally {
                updateCheckInFlight = false
            }
            if (!directUpdatesEnabled) {
                updateStatus = UpdateStatus.Hidden
                updateConfirmation = null
                return@launch
            }
            updateStatus = when (result) {
                is UpdateCheckResult.Available -> UpdateStatus.Available(result.update)
                UpdateCheckResult.UpToDate -> if (showProgress) UpdateStatus.UpToDate else UpdateStatus.Hidden
                is UpdateCheckResult.Failed -> if (showProgress) UpdateStatus.Failed(result.message) else UpdateStatus.Hidden
            }
            if (showProgress && result is UpdateCheckResult.Available) {
                updateConfirmation = result.update
            }
            if (updateStatus !is UpdateStatus.Hidden && updateStatus !is UpdateStatus.Downloading) {
                val displayedStatus = updateStatus
                val displayDurationMillis = if (displayedStatus is UpdateStatus.UpToDate) 8_000L else 10_000L
                updateStatusHideJob = activityScope.launch {
                    delay(displayDurationMillis)
                    if (!updateCheckInFlight && updateStatus == displayedStatus && updateStatus !is UpdateStatus.Downloading) {
                        updateStatus = UpdateStatus.Hidden
                    }
                }
            }
        }
    }

    private sealed interface UpdateCheckResult {
        data class Available(val update: AppUpdate) : UpdateCheckResult
        data object UpToDate : UpdateCheckResult
        data class Failed(val message: String) : UpdateCheckResult
    }

    private fun fetchLatestCompatibleUpdateResult(): UpdateCheckResult {
        return try {
            val update = fetchLatestCompatibleUpdate()
            if (update != null) UpdateCheckResult.Available(update) else UpdateCheckResult.UpToDate
        } catch (error: Exception) {
            UpdateCheckResult.Failed(error.message ?: "Update check failed.")
        }
    }

    private fun fetchLatestCompatibleUpdate(): AppUpdate? {
        val request = Request.Builder()
            .url("https://api.github.com/repos/EFIShell0/VulkanScope/releases?per_page=20")
            .header("Accept", "application/vnd.github+json")
            .header("X-GitHub-Api-Version", "2022-11-28")
            .header("User-Agent", "VulkanScope/${installedVersionName()}")
            .get()
            .build()
        val call = ipv6PreferredHttpClient.newCall(request)
        activeUpdateCheckCall = call
        return try {
            call.execute().use { response ->
            if (!response.isSuccessful) error("Update check failed (HTTP ${response.code}).")
            val releases = JSONArray(readResponseTextLimited(response.body, 2 * 1024 * 1024))
            val current = installedVersionName()
            val candidates = (0 until releases.length())
                .mapNotNull { releases.optJSONObject(it) }
                .filter { !it.optBoolean("draft", false) }
                .mapNotNull { release ->
                    val version = release.optString("tag_name").trim().removePrefix("v")
                    if (version.isBlank() || !version.matches(Regex("\\d+(?:\\.\\d+){1,3}(?:-[0-9A-Za-z.-]+)?(?:\\+[0-9A-Za-z.-]+)?"))) null else release to version
                }
                .sortedWith { a, b -> -compareVersions(a.second, b.second) }
            val candidate = candidates.firstOrNull { isNewerVersion(it.second, current) } ?: return null
            val json = candidate.first
            val latest = candidate.second
            val assets = json.optJSONArray("assets") ?: error("A newer VulkanScope release exists, but its asset list is unavailable.")
            val apkAssets = (0 until assets.length()).mapNotNull { assets.optJSONObject(it) }.filter { it.optString("name").endsWith(".apk", true) }
            val abi = detectInstalledAbi(this)
            val abiTokens = when (abi) {
                "arm64-v8a" -> listOf("arm64-v8a", "arm64_v8a", "arm64")
                "x86_64" -> listOf("x86_64", "x86-64")
                else -> listOf(abi.lowercase())
            }
            val exact = apkAssets.firstOrNull { asset -> abiTokens.any { asset.optString("name").lowercase().contains(it) } }
            val universal = apkAssets.firstOrNull { it.optString("name").lowercase().contains("universal") }
            val selected = exact ?: universal ?: error("A newer VulkanScope release exists, but it has no APK compatible with the installed ABI ($abi).")
            val url = selected.optString("browser_download_url")
            val parsedUrl = url.toHttpUrlOrNull()
            if (parsedUrl == null || parsedUrl.scheme != "https" || parsedUrl.host != "github.com" || parsedUrl.username.isNotEmpty() || parsedUrl.password.isNotEmpty() || parsedUrl.query != null || parsedUrl.fragment != null || !parsedUrl.encodedPath.startsWith("/EFIShell0/VulkanScope/releases/download/")) error("The release APK URL is not an official VulkanScope GitHub release asset.")
            AppUpdate(
                version = latest,
                assetName = selected.optString("name"),
                downloadUrl = url,
                releaseNotes = json.optString("body").trim().ifBlank { "No release notes were provided for this GitHub release." },
                installedAbi = abi,
                downloadAbi = if (exact != null) abi else "universal",
                installedVersion = current,
                installedVersionCode = installedVersionCode(),
                assetSizeBytes = selected.optLong("size").takeIf { it > 0L }
            )
            }
        } finally {
            if (activeUpdateCheckCall === call) activeUpdateCheckCall = null
        }
    }

    private fun installedVersionName(): String = runCatching {
        packageManager.getPackageInfo(packageName, 0).versionName ?: "0.0.0"
    }.getOrDefault("0.0.0")

    private fun installedVersionCode(): Long = runCatching {
        val info = packageManager.getPackageInfo(packageName, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) info.longVersionCode else info.versionCode.toLong()
    }.getOrDefault(0L)

    private fun compareVersions(first: String, second: String): Int {
        fun parse(value: String): Pair<List<Int>, List<String>?> {
            val withoutBuild = value.substringBefore('+')
            val core = withoutBuild.substringBefore('-').split('.').map { it.toIntOrNull() ?: 0 }
            val suffix = withoutBuild.substringAfter('-', "").takeIf { it.isNotEmpty() }?.split('.')
            return core to suffix
        }
        val (aCore, aPre) = parse(first)
        val (bCore, bPre) = parse(second)
        repeat(maxOf(aCore.size, bCore.size)) { index ->
            val left = aCore.getOrElse(index) { 0 }
            val right = bCore.getOrElse(index) { 0 }
            if (left != right) return left.compareTo(right)
        }
        if (aPre == null && bPre != null) return 1
        if (aPre != null && bPre == null) return -1
        if (aPre == null || bPre == null) return 0
        repeat(maxOf(aPre.size, bPre.size)) { index ->
            val left = aPre.getOrNull(index) ?: return -1
            val right = bPre.getOrNull(index) ?: return 1
            val leftNumber = left.toIntOrNull()
            val rightNumber = right.toIntOrNull()
            val result = when {
                leftNumber != null && rightNumber != null -> leftNumber.compareTo(rightNumber)
                leftNumber != null -> -1
                rightNumber != null -> 1
                else -> left.compareTo(right)
            }
            if (result != 0) return result
        }
        return 0
    }

    private fun isNewerVersion(candidate: String, current: String): Boolean = compareVersions(candidate, current) > 0

    private fun startUpdateDownload(update: AppUpdate) {
        if (!validatedDefaultNetwork()) {
            updateStatus = UpdateStatus.Failed("No validated internet connection. Update download is unavailable until connectivity returns.")
            return
        }
        if (!directUpdatesEnabled || updateDownloadJob != null) return
        updateStatusHideJob?.cancel()
        updateStatusHideJob = null
        updateStatus = UpdateStatus.Hidden
        updateDownloadPaused = false
        updateDownloadCancelRequested = false
        updateCancelConfirmationVisible = false
        updateTransferState = UpdateTransferState(
            update = update,
            phase = UpdateTransferPhase.CONNECTING,
            totalBytes = update.assetSizeBytes,
            connectionStatus = "Connecting",
            log = listOf("Preparing ${update.assetName}", "Connecting to the official VulkanScope release asset…")
        )
        updateDownloadJob = activityScope.launch {
            try {
                val apk = downloadUpdateApk(update)
                if (!directUpdatesEnabled) {
                    runCatching { apk.delete() }
                    updateTransferState = null
                } else {
                    updateTransferState = updateTransferState?.copy(
                        phase = UpdateTransferPhase.COMPLETED,
                        bytesPerSecond = 0L,
                        connectionStatus = "Completed",
                        apk = apk,
                        errorMessage = null,
                        log = appendBoundedUpdateLog(updateTransferState?.log.orEmpty(), "APK validation completed successfully. Update downloaded and ready to install.")
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Throwable) {
                if (updateDownloadCancelRequested) {
                    updateTransferState = updateTransferState?.copy(
                        phase = UpdateTransferPhase.CANCELED,
                        bytesPerSecond = 0L,
                        connectionStatus = "Canceled",
                        apk = null,
                        errorMessage = null,
                        log = appendBoundedUpdateLog(updateTransferState?.log.orEmpty(), "Download canceled by user.")
                    )
                } else if (directUpdatesEnabled) {
                    val message = error.message ?: "Update download failed."
                    updateTransferState = updateTransferState?.copy(
                        phase = UpdateTransferPhase.FAILED,
                        bytesPerSecond = 0L,
                        connectionStatus = "Error",
                        apk = null,
                        errorMessage = message,
                        log = appendBoundedUpdateLog(updateTransferState?.log.orEmpty(), "ERROR: $message")
                    )
                } else {
                    updateTransferState = null
                }
            } finally {
                updateDownloadPaused = false
                updateDownloadCancelRequested = false
                updateCancelConfirmationVisible = false
                updateDownloadJob = null
            }
        }
    }

    private fun pauseUpdateDownload() {
        val state = updateTransferState ?: return
        if (state.phase != UpdateTransferPhase.CONNECTING && state.phase != UpdateTransferPhase.DOWNLOADING) return
        updateDownloadPaused = true
        updateTransferState = state.copy(
            phase = UpdateTransferPhase.PAUSED,
            bytesPerSecond = 0L,
            connectionStatus = "Paused",
            log = appendBoundedUpdateLog(state.log, "Download paused.")
        )
    }

    private fun resumeUpdateDownload() {
        val state = updateTransferState ?: return
        if (state.phase != UpdateTransferPhase.PAUSED || updateDownloadCancelRequested) return
        updateDownloadPaused = false
        updateTransferState = state.copy(
            phase = UpdateTransferPhase.DOWNLOADING,
            connectionStatus = if (validatedDefaultNetwork()) "Connected" else "Waiting for connection",
            log = appendBoundedUpdateLog(state.log, "Download resumed.")
        )
    }

    private fun requestCancelUpdateDownload() {
        val state = updateTransferState ?: return
        if (state.phase !in setOf(UpdateTransferPhase.CONNECTING, UpdateTransferPhase.DOWNLOADING, UpdateTransferPhase.PAUSED)) return
        if (state.phase != UpdateTransferPhase.PAUSED) pauseUpdateDownload()
        updateCancelConfirmationVisible = true
    }

    private fun dismissCancelUpdateDownload() {
        if (!updateCancelConfirmationVisible) return
        updateCancelConfirmationVisible = false
        resumeUpdateDownload()
    }

    private fun confirmCancelUpdateDownload() {
        val state = updateTransferState ?: return
        if (!updateCancelConfirmationVisible || state.phase != UpdateTransferPhase.PAUSED) return
        updateCancelConfirmationVisible = false
        updateDownloadCancelRequested = true
        updateDownloadPaused = false
        updateTransferState = state.copy(
            connectionStatus = "Canceling",
            log = appendBoundedUpdateLog(state.log, "Cancel confirmed. Stopping download…")
        )
        activeUpdateDownloadCall?.cancel()
    }

    private fun installDownloadedUpdate() {
        val state = updateTransferState ?: return
        if (state.phase != UpdateTransferPhase.COMPLETED) return
        val apk = state.apk?.takeIf { it.isFile } ?: return
        updateTransferState = state.copy(log = appendBoundedUpdateLog(state.log, "Opening Android package installer…"))
        requestPackageInstall(apk)
    }

    private fun closeUpdateTransfer() {
        val state = updateTransferState ?: return
        if (state.phase !in setOf(UpdateTransferPhase.COMPLETED, UpdateTransferPhase.CANCELED, UpdateTransferPhase.FAILED)) return
        state.apk?.let { runCatching { it.delete() } }
        updateTransferState = null
        updateCancelConfirmationVisible = false
    }

    private fun appendBoundedUpdateLog(existing: List<String>, message: String): List<String> =
        (existing + message).takeLast(120)

    private fun formatUpdateBytes(bytes: Long): String {
        if (bytes < 1024L) return "$bytes B"
        val units = listOf("KiB", "MiB", "GiB")
        var value = bytes.toDouble()
        var unitIndex = -1
        while (value >= 1024.0 && unitIndex < units.lastIndex) {
            value /= 1024.0
            unitIndex += 1
        }
        return if (value >= 100.0) "%.0f %s".format(java.util.Locale.US, value, units[unitIndex]) else "%.1f %s".format(java.util.Locale.US, value, units[unitIndex])
    }

    private fun formatUpdateSpeed(bytesPerSecond: Long): String =
        if (bytesPerSecond <= 0L) "—" else "${formatUpdateBytes(bytesPerSecond)}/s"

    private suspend fun publishUpdateTransfer(
        phase: UpdateTransferPhase,
        bytesDownloaded: Long,
        totalBytes: Long?,
        bytesPerSecond: Long,
        connectionStatus: String,
        logLine: String? = null
    ) {
        withContext(Dispatchers.Main.immediate) {
            val current = updateTransferState ?: return@withContext
            if (updateDownloadCancelRequested) return@withContext
            val effectivePaused = updateDownloadPaused && phase == UpdateTransferPhase.DOWNLOADING
            updateTransferState = current.copy(
                phase = if (effectivePaused) UpdateTransferPhase.PAUSED else phase,
                bytesDownloaded = bytesDownloaded,
                totalBytes = totalBytes,
                bytesPerSecond = if (effectivePaused) 0L else bytesPerSecond,
                connectionStatus = if (effectivePaused) "Paused" else connectionStatus,
                log = if (logLine == null) current.log else appendBoundedUpdateLog(current.log, logLine)
            )
        }
    }

    private suspend fun downloadUpdateApk(update: AppUpdate): File = withContext(Dispatchers.IO) {
        val safeAssetName = update.assetName.substringAfterLast('/').substringAfterLast('\\').takeIf { it.endsWith(".apk", true) && it.length in 5..160 } ?: error("The release asset has an invalid APK filename.")
        val updateDir = File(cacheDir, "updates").apply { mkdirs() }
        val target = File(updateDir, safeAssetName)
        if (target.parentFile?.canonicalFile != updateDir.canonicalFile) error("The release asset path is invalid.")
        val temp = File(updateDir, "$safeAssetName.part")
        try {
            currentCoroutineContext().ensureActive()
            if (updateDownloadCancelRequested) error("Update download canceled.")
            while (updateDownloadPaused && !updateDownloadCancelRequested) delay(100L)
            val request = Request.Builder().url(update.downloadUrl).header("User-Agent", "VulkanScope/${installedVersionName()}").get().build()
            val call = ipv6PreferredDownloadClient.newCall(request)
            activeUpdateDownloadCall = call
            try {
                call.execute().use { response ->
                    if (!response.isSuccessful) error("Update download failed (HTTP ${response.code}).")
                    val body = response.body
                    val contentLength = body.contentLength().takeIf { it >= 0L }
                    val expectedSize = update.assetSizeBytes
                    val transferTotal = contentLength ?: expectedSize
                    if (transferTotal != null && transferTotal > 256L * 1024L * 1024L) error("Update package exceeds the safety limit.")
                    publishUpdateTransfer(
                        phase = if (updateDownloadPaused) UpdateTransferPhase.PAUSED else UpdateTransferPhase.DOWNLOADING,
                        bytesDownloaded = 0L,
                        totalBytes = transferTotal,
                        bytesPerSecond = 0L,
                        connectionStatus = if (updateDownloadPaused) "Paused" else "Connected",
                        logLine = "Connected. HTTP ${response.code}${contentLength?.let { "; ${formatUpdateBytes(it)} expected" } ?: ""}."
                    )
                    body.byteStream().use { input ->
                        FileOutputStream(temp).use { output ->
                            val buffer = ByteArray(64 * 1024)
                            var total = 0L
                            var speedWindowBytes = 0L
                            var speedWindowStarted = System.nanoTime()
                            var lastLogAt = speedWindowStarted
                            while (true) {
                                currentCoroutineContext().ensureActive()
                                if (updateDownloadCancelRequested) error("Update download canceled.")
                                while (updateDownloadPaused && !updateDownloadCancelRequested) {
                                    delay(100L)
                                    currentCoroutineContext().ensureActive()
                                }
                                if (updateDownloadCancelRequested) error("Update download canceled.")
                                val count = input.read(buffer)
                                if (count < 0) break
                                total += count
                                speedWindowBytes += count
                                if (total > 256L * 1024L * 1024L) error("Update package exceeds the safety limit.")
                                output.write(buffer, 0, count)
                                val now = System.nanoTime()
                                val elapsed = now - speedWindowStarted
                                if (elapsed >= 400_000_000L) {
                                    val speed = ((speedWindowBytes.toDouble() * 1_000_000_000.0) / elapsed.toDouble()).toLong().coerceAtLeast(0L)
                                    val connected = validatedDefaultNetwork()
                                    val logLine = if (now - lastLogAt >= 2_000_000_000L) {
                                        lastLogAt = now
                                        "Received ${formatUpdateBytes(total)}${contentLength?.let { " / ${formatUpdateBytes(it)}" } ?: ""} at ${formatUpdateSpeed(speed)}."
                                    } else null
                                    publishUpdateTransfer(
                                        phase = UpdateTransferPhase.DOWNLOADING,
                                        bytesDownloaded = total,
                                        totalBytes = transferTotal,
                                        bytesPerSecond = speed,
                                        connectionStatus = if (connected) "Connected" else "Connection unavailable",
                                        logLine = logLine
                                    )
                                    speedWindowBytes = 0L
                                    speedWindowStarted = now
                                }
                            }
                            output.fd.sync()
                            if (expectedSize != null && total != expectedSize) error("Downloaded package size does not match the selected GitHub release asset metadata.")
                            publishUpdateTransfer(
                                phase = UpdateTransferPhase.VERIFYING,
                                bytesDownloaded = total,
                                totalBytes = transferTotal,
                                bytesPerSecond = 0L,
                                connectionStatus = "Verifying",
                                logLine = "Download finished. Verifying package identity, signature and version…"
                            )
                        }
                    }
                }
            } finally {
                if (activeUpdateDownloadCall === call) activeUpdateDownloadCall = null
            }
            currentCoroutineContext().ensureActive()
            if (updateDownloadCancelRequested) error("Update download canceled.")
            if (!temp.renameTo(target)) { temp.copyTo(target, overwrite = true); temp.delete() }
            val archiveFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) PackageManager.GET_SIGNING_CERTIFICATES else PackageManager.GET_SIGNATURES
            try {
                val archive = packageManager.getPackageArchiveInfo(target.absolutePath, archiveFlags) ?: error("Downloaded file is not a valid Android package.")
                if (archive.packageName != packageName) error("Downloaded package identity does not match VulkanScope.")
                val installed = packageManager.getPackageInfo(packageName, archiveFlags)
                if (!packageSigningCertificatesMatch(installed, archive)) error("Downloaded package signing certificate does not match the installed VulkanScope build.")
                val archiveVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) archive.longVersionCode else archive.versionCode.toLong()
                val installedVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) installed.longVersionCode else installed.versionCode.toLong()
                if (archiveVersionCode <= installedVersionCode) error("Downloaded package versionCode is not newer than the installed VulkanScope build.")
                val archiveVersion = archive.versionName ?: error("Downloaded package has no version metadata.")
                if (archiveVersion != update.version) error("Downloaded package versionName does not match the selected VulkanScope release.")
                if (!isNewerVersion(archiveVersion, installedVersionName())) error("Downloaded package versionName is not newer than the installed VulkanScope version.")
                target
            } catch (error: Throwable) {
                runCatching { target.delete() }
                throw error
            }
        } finally {
            if (temp.exists()) temp.delete()
        }
    }

    private fun packageSigningCertificatesMatch(installed: android.content.pm.PackageInfo, archive: android.content.pm.PackageInfo): Boolean {
        fun encoded(signatures: Array<android.content.pm.Signature>): Set<String> = signatures.map { Base64.encodeToString(it.toByteArray(), Base64.NO_WRAP) }.toSet()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val installedInfo = installed.signingInfo ?: return false
            val archiveInfo = archive.signingInfo ?: return false
            val installedCurrent = encoded(installedInfo.apkContentsSigners)
            if (installedCurrent.isEmpty()) return false
            if (installedInfo.hasMultipleSigners() || archiveInfo.hasMultipleSigners()) {
                val archiveCurrent = encoded(archiveInfo.apkContentsSigners)
                return archiveCurrent.isNotEmpty() && installedCurrent == archiveCurrent
            }
            val archiveHistory = encoded(archiveInfo.signingCertificateHistory)
            return archiveHistory.isNotEmpty() && archiveHistory.containsAll(installedCurrent)
        }
        @Suppress("DEPRECATION")
        val installedLegacy = encoded(installed.signatures ?: emptyArray())
        @Suppress("DEPRECATION")
        val archiveLegacy = encoded(archive.signatures ?: emptyArray())
        return installedLegacy.isNotEmpty() && installedLegacy == archiveLegacy
    }

    private fun requestPackageInstall(apk: File) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !packageManager.canRequestPackageInstalls()) {
            pendingUpdateApk = apk
            startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:$packageName")))
            return
        }
        launchPackageInstaller(apk)
    }

    private fun launchPackageInstaller(apk: File) {
        val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", apk)
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    private fun requestDriverModeChange(mode: DriverMode) {
        if (!completeReportMutationReady() || driverImportInFlight || turnipManagerBusy) {
            android.widget.Toast.makeText(this, "Wait for the complete Vulkan® collection pass before changing the driver source.", android.widget.Toast.LENGTH_LONG).show()
            return
        }
        if (mode == DriverMode.TURNIP && turnipSupport != TurnipSupport.SUPPORTED) return
        if (mode == driverMode) return
        driverModeValidationJob?.cancel()
        if (mode == DriverMode.SYSTEM) {
            pendingDriverModeConfirmation = mode
            return
        }
        driverModeValidationJob = activityScope.launch {
            val installed = withContext(Dispatchers.IO) { findInstalledTurnipLibrary() != null }
            if (!completeReportMutationReady() || driverImportInFlight || turnipManagerBusy || mode == driverMode) return@launch
            if (installed) {
                pendingDriverModeConfirmation = mode
            } else {
                android.widget.Toast.makeText(this@MainActivity, "No active Turnip driver is selected. Open Settings and activate a driver from the Driver manager.", android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun confirmDriverModeChange() {
        val mode = pendingDriverModeConfirmation ?: return
        pendingDriverModeConfirmation = null
        if (!completeReportMutationReady() || driverImportInFlight || turnipManagerBusy || mode == driverMode) return
        if (mode == DriverMode.SYSTEM) {
            applyDriverModeChange(mode, false)
            return
        }
        driverModeValidationJob?.cancel()
        driverModeValidationJob = activityScope.launch {
            val installed = withContext(Dispatchers.IO) { findInstalledTurnipLibrary() != null }
            if (!completeReportMutationReady() || driverImportInFlight || turnipManagerBusy || mode == driverMode) return@launch
            if (installed) applyDriverModeChange(mode, false)
            else android.widget.Toast.makeText(this@MainActivity, "The active Turnip driver is no longer available.", android.widget.Toast.LENGTH_LONG).show()
        }
    }

    private fun prepareDriverSurfaceRebind() {
        synchronized(surfaceLock) {
            currentSurface = null
            surfaceGeneration += 1L
            surfaceRefreshPending = false
        }
        driverSurfaceRebindPending = true
        surfaceHostGeneration += 1L
    }

    private fun applyDriverModeChange(mode: DriverMode, forceRefresh: Boolean) {
        if (!completeReportMutationReady()) return
        if (!forceRefresh && mode == driverMode) return
        requestedQueryGroups.clear()
        queryTimingMs = emptyMap()
        prepareDriverSurfaceRebind()
        driverMode = mode
        latestReport = null
        reportLoading = true
        prefs.edit().putString("driver_mode", mode.name).apply()
    }

    private fun findInstalledTurnipLibrary(): File? = resolveInstalledTurnipIcd()?.let(::File)

    private fun refreshDisplayReport() {
        displayReportState = displayReport()
    }

    @Suppress("DEPRECATION")
    private fun activeDisplay(): android.view.Display? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        display ?: getSystemService(DisplayManager::class.java).getDisplay(android.view.Display.DEFAULT_DISPLAY)
    } else {
        windowManager.defaultDisplay
    }

    @Suppress("DEPRECATION")
    private fun displayReport(): DisplayReport {
        val display = activeDisplay()
            ?: return DisplayReport("Unknown", "Unknown", null, "Unknown", emptyList(), "Unknown", "Unknown", "Unknown", emptyList(), "unknown")
        val mode = display.mode
        val hdr = display.hdrCapabilities
        val hdrTypesResult = runCatching {
            if (android.os.Build.VERSION.SDK_INT >= 34) mode.supportedHdrTypes else hdr?.supportedHdrTypes ?: intArrayOf()
        }
        val hdrTypes = hdrTypesResult.getOrDefault(intArrayOf())
        val hdrNames = hdrTypes.filter { it != -1 }.map { hdrTypeName(it) }.distinct()
        val modes = display.supportedModes.map { "${it.physicalWidth} × ${it.physicalHeight} · ${formatHz(it.refreshRate)}" }.distinct().sorted()
        val wideGamut = if (android.os.Build.VERSION.SDK_INT >= 26) display.isWideColorGamut else null
        val preferred = if (android.os.Build.VERSION.SDK_INT >= 29) display.preferredWideGamutColorSpace?.name ?: "Not exposed" else "Unavailable: requires Android API 29+"
        val hdrCapabilityStatus = when {
            hdrTypesResult.isFailure -> "unavailable"
            android.os.Build.VERSION.SDK_INT >= 34 -> "available"
            hdr != null -> "available"
            else -> "unknown"
        }
        return DisplayReport(
            "${mode.physicalWidth} × ${mode.physicalHeight}",
            formatHz(mode.refreshRate),
            wideGamut,
            preferred,
            hdrNames,
            hdr?.let { formatLuminance(it.desiredMinLuminance) } ?: "Unknown: HDR capabilities unavailable",
            hdr?.let { formatLuminance(it.desiredMaxLuminance) } ?: "Unknown: HDR capabilities unavailable",
            hdr?.let { formatLuminance(it.desiredMaxAverageLuminance) } ?: "Unknown: HDR capabilities unavailable",
            modes,
            hdrCapabilityStatus
        )
    }

    private suspend fun collectReport(modeSnapshot: DriverMode, timings: MutableMap<String, Long>): VulkanReport = withContext(Dispatchers.IO) {
        collectMutex.withLock {
            collectBaseReport(modeSnapshot, timings)
        }
    }

    private suspend fun collectBaseReport(modeSnapshot: DriverMode, timings: MutableMap<String, Long>): VulkanReport {
        var lastParsed: VulkanReport? = null
        var lastFailure = "Base probe returned no terminal result."
        for (attempt in 0 until 2) {
            val attemptNumber = attempt + 1
            val probeStartNanos = System.nanoTime()
            val rawResult = runCatching { runProbe(null, modeSnapshot) }
            timings["base/attempt_${attemptNumber}/probe"] = elapsedMillis(probeStartNanos)
            val raw = rawResult.getOrElse { e ->
                Log.e("VulkanScope", "Vulkan base probe failed (attempt=$attemptNumber)", e)
                lastFailure = e.message ?: "probe failed"
                break
            }
            Log.i("VulkanScope", "Vulkan base probe result bytes=${raw.length} attempt=$attemptNumber")
            val decodeStartNanos = System.nanoTime()
            val root = runCatching { JSONObject(raw) }.getOrElse { e ->
                Log.e("VulkanScope", "Vulkan base JSON parse failed before report decoding: length=${raw.length} attempt=$attemptNumber", e)
                lastFailure = e.message ?: "invalid probe result"
                break
            }
            val parsed = runCatching { parseReport(raw) }.getOrElse { e ->
                Log.e("VulkanScope", "Vulkan base report decode failed: length=${raw.length} attempt=$attemptNumber", e)
                lastFailure = e.message ?: "invalid probe result"
                break
            }
            timings["base/attempt_${attemptNumber}/decode"] = elapsedMillis(decodeStartNanos)
            if (hasCompleteBaseCoverage(parsed)) return parsed
            lastParsed = parsed
            lastFailure = parsed.error ?: "Base probe returned an incomplete core Vulkan dataset."
            Log.w(
                "VulkanScope",
                "Rejecting incomplete base report attempt=$attemptNumber: status=${root.optString("status", "unknown")} " +
                    parsed.devices.joinToString { d ->
                        "${d.name}: features=${d.features.size}, queues=${d.queues.size}, heaps=${d.heaps.size}, memoryTypes=${d.memoryTypes.size}, limits=${d.limits.size}, extensions=${d.extensions.size}"
                    }
            )
            val retryablePartial = attempt == 0 &&
                root.optString("status", "unavailable") == "incomplete" &&
                parsed.devices.isNotEmpty() &&
                parsed.error?.contains("timeout", ignoreCase = true) != true
            if (!retryablePartial) break
            Log.w("VulkanScope", "Retrying one bounded base probe because the first terminal result retained partial positive Vulkan evidence.")
        }
        return lastParsed?.copy(
            error = lastParsed.error ?: "Vulkan base report was incomplete: $lastFailure",
            baseReportComplete = false
        ) ?: VulkanReport(
            "Unknown",
            emptyList(),
            emptyList(),
            emptyList(),
            "Vulkan base report unavailable: $lastFailure"
        )
    }

    private fun hasCompleteBaseCoverage(report: VulkanReport): Boolean =
        report.baseReportComplete &&
            report.error == null &&
            report.devices.isNotEmpty() &&
            report.devices.all { device ->
                device.deviceExtensionStatus == "available" &&
                    device.features.size >= 55 &&
                    device.queues.isNotEmpty() &&
                    device.heaps.isNotEmpty() &&
                    device.memoryTypes.isNotEmpty() &&
                    device.limits.isNotEmpty()
            }

    private suspend fun enrichReport(base: VulkanReport, modeSnapshot: DriverMode, timings: MutableMap<String, Long>): VulkanReport = withContext(Dispatchers.IO) {
        var enriched = base.copy(devices = base.devices.map { device ->
            if (apiAtLeast(device.apiVersion, 1, 1)) {
                device.copy(
                    extendedQueryStatus = if (device.extendedQueryStatus == "unavailable") "unavailable" else "available",
                    extendedQueryReason = if (device.extendedQueryStatus == "unavailable") device.extendedQueryReason else "Core Vulkan 1.1+ feature/property data was queried directly from the active base Vulkan instance."
                )
            } else {
                device.copy(extendedQueryStatus = "not_applicable", extendedQueryReason = "The device API version is below Vulkan 1.1.")
            }
        })

        val metadataStartNanos = System.nanoTime()
        runCatching {
            val rawMetadata = runIsolatedProbe("metadata", modeSnapshot)
            enriched = mergeMetadataReport(enriched, rawMetadata)
        }.onFailure { e ->
            Log.e("VulkanScope", "Vulkan metadata query failed", e)
            enriched = markMetadataProbeUnavailable(enriched, e.message ?: "The dedicated metadata query failed.")
        }
        timings["enrichment/metadata"] = elapsedMillis(metadataStartNanos)

        if (enriched.devices.isEmpty()) return@withContext enriched

        val surfaceSnapshot = synchronized(surfaceLock) {
            currentSurface?.takeIf { it.isValid }?.let { it to surfaceGeneration }
        }
        if (surfaceSnapshot != null && enriched.devices.isNotEmpty()) {
            val (surface, surfaceToken) = surfaceSnapshot
            val surfaceStartNanos = System.nanoTime()
            runCatching {
                val rawSurface = runSurfaceProbe(surface, modeSnapshot)
                val stillCurrent = synchronized(surfaceLock) {
                    currentSurface === surface && surfaceGeneration == surfaceToken && surface.isValid
                }
                if (stillCurrent) {
                    enriched = mergeSurfaceProbeReport(enriched, rawSurface)
                    synchronized(surfaceLock) {
                        if (currentSurface === surface && surfaceGeneration == surfaceToken) surfaceRefreshPending = false
                    }
                } else {
                    synchronized(surfaceLock) {
                        if (currentSurface?.isValid == true) surfaceRefreshPending = true
                    }
                }
            }.onFailure { e ->
                Log.e("VulkanScope", "Vulkan surface probe failed", e)
                val stillCurrent = synchronized(surfaceLock) {
                    currentSurface === surface && surfaceGeneration == surfaceToken && surface.isValid
                }
                if (stillCurrent) {
                    enriched = markSurfaceProbeState(enriched, "unavailable", e.message ?: "The dedicated Surface query failed.")
                    synchronized(surfaceLock) {
                        if (currentSurface === surface && surfaceGeneration == surfaceToken) surfaceRefreshPending = false
                    }
                } else {
                    synchronized(surfaceLock) {
                        if (currentSurface?.isValid == true) surfaceRefreshPending = true
                    }
                }
            }
            timings["enrichment/surface"] = elapsedMillis(surfaceStartNanos)
        } else {
            timings["enrichment/surface"] = 0L
            enriched = markSurfaceProbeState(enriched, "unavailable", "No live Android Surface was available for dedicated Surface collection.")
        }

        enriched
    }

    private suspend fun runIsolatedProbe(group: String, modeSnapshot: DriverMode = driverMode): String =
        runServiceProbe(group, null, if (group in ISOLATED_ADVANCED_GROUPS.keys) 30_000L else 12_000L, modeSnapshot)

    private fun requestQueryGroup(group: String) {
        if (!startupGateOpen) return
        if (driverImportInFlight) {
            collectionPending = true
            return
        }
        if (collectionInFlight) return
        if (latestReport?.devices.isNullOrEmpty()) return
        if (!requestedQueryGroups.add(group)) return
        val generation = collectionGeneration
        val modeSnapshot = driverMode
        val taskId = "ad-hoc:$group:${System.nanoTime()}"
        beginCollectionTask(taskId)
        activityScope.launch {
            try {
                val current = latestReport ?: return@launch
                val label = ISOLATED_ADVANCED_GROUPS[group] ?: ISOLATED_CORE_GROUPS[group]
                val extensionName = if (group.startsWith("ext::")) group.removePrefix("ext::") else ISOLATED_EXTENSION_GROUPS[group]
                if (extensionName != null && current.devices.none { device -> device.extensions.any { it.name == extensionName } }) return@launch
                val timingStartNanos = System.nanoTime()
                val raw = runCatching { runIsolatedProbe(group, modeSnapshot) }.getOrElse { e ->
                    Log.e("VulkanScope", "Vulkan lazy query failed: $group", e)
                    JSONObject().put("status", "unavailable").put("group", group).put("reason", e.message ?: "The dedicated lazy query failed.").put("devices", JSONArray()).toString()
                }.ifBlank {
                    JSONObject().put("status", "unavailable").put("group", group).put("reason", "The dedicated lazy query returned no data.").put("devices", JSONArray()).toString()
                }
                val elapsedMs = ((System.nanoTime() - timingStartNanos) / 1_000_000L).coerceAtLeast(0L)
                withContext(Dispatchers.Main.immediate) {
                    if (generation != collectionGeneration || modeSnapshot != driverMode || latestReport == null) return@withContext
                    queryTimingMs = queryTimingMs + ("probe_total/$group" to elapsedMs)
                    val currentReport = latestReport ?: current
                    latestReport = if (extensionName != null) {
                        mergeExtensionGroupReport(currentReport, raw, group, extensionName)
                    } else if (label != null) {
                        mergeAdvancedQueryReport(currentReport, raw, group, label)
                    } else {
                        currentReport
                    }
                }
            } finally {
                completeCollectionTask(taskId)
            }
        }
    }

    private fun requestPageQueries(page: Page) {
        val groups = when (page) {
            Page.Features -> ISOLATED_CORE_GROUPS.keys.toList()
            Page.Formats -> listOf("format2", "imageFormat2")
            Page.Memory -> listOf("memory2", "external", "sparse")
            Page.Queues -> listOf("queue2", "videoCapabilities")
            Page.Video -> listOf("queue2", "videoCapabilities")
            Page.Properties -> listOf("core11", "core12", "core13", "tools", "groups")
            Page.Extensions -> latestReport?.devices
                ?.flatMap { device -> device.extensions.map { it.name } }
                ?.toSet()
                ?.asSequence()
                ?.filter { it in VALIDATED_PHYSICAL_DEVICE_QUERY_EXTENSIONS }
                ?.sorted()
                ?.map { "ext::$it" }
                ?.toList()
                ?: emptyList()
            else -> emptyList()
        }
        groups.forEach(::requestQueryGroup)
    }

    internal suspend fun runVulkanSelfTests(vendorId: Long, deviceId: Long): String = runServiceProbe("selftest:$vendorId:$deviceId", null, 30_000L, driverMode)

    private suspend fun runProbe(surface: Surface?, modeSnapshot: DriverMode): String = runServiceProbe("base", surface, 20_000L, modeSnapshot)

    private suspend fun runSurfaceProbe(surface: Surface, modeSnapshot: DriverMode = driverMode): String {
        val first = runServiceProbe("surface", surface, 25_000L, modeSnapshot)
        val nativeWindowInUse = runCatching {
            JSONObject(first).optString("reason").contains("VkResult=-1000000001")
        }.getOrDefault(false)
        if (!nativeWindowInUse || !surface.isValid) return first
        delay(200L)
        if (!surface.isValid) return first
        return runServiceProbe("surface", surface, 25_000L, modeSnapshot)
    }

    private fun runningVulkanProbePids(processSuffix: String): List<Int> = runCatching {
        val activityManager = getSystemService(ActivityManager::class.java) ?: return@runCatching emptyList()
        val expectedName = packageName + processSuffix
        activityManager.runningAppProcesses.orEmpty()
            .filter { it.uid == Process.myUid() && it.processName == expectedName }
            .map { it.pid }
            .distinct()
    }.getOrElse { error ->
        Log.w("VulkanScope", "Unable to inspect dedicated Vulkan probe process $processSuffix", error)
        emptyList()
    }

    private fun stopVulkanProbeProcess(serviceClass: Class<out android.app.Service>, processSuffix: String) {
        runCatching { stopService(Intent(this@MainActivity, serviceClass)) }
        runningVulkanProbePids(processSuffix).forEach { pid ->
            runCatching { Process.killProcess(pid) }.onFailure { error ->
                Log.w("VulkanScope", "Unable to kill stale Vulkan probe pid=$pid for $processSuffix", error)
            }
        }
    }

    private fun stopVulkanProbeProcess() {
        stopVulkanProbeProcess(VulkanProbeService::class.java, ":vulkan_probe")
    }

    private fun stopAllVulkanProbeProcesses() {
        stopVulkanProbeProcess()
        backgroundProbeServices.indices.forEach { index ->
            stopVulkanProbeProcess(backgroundProbeServices[index], backgroundProbeProcessSuffixes[index])
        }
    }

    private suspend fun ensureVulkanProbeProcessQuiescent(
        serviceClass: Class<out android.app.Service>,
        processSuffix: String,
        timeoutMs: Long = 1_500L
    ): Boolean {
        val initialPids = runningVulkanProbePids(processSuffix)
        val stopRequested = runCatching {
            withContext(Dispatchers.Main.immediate) { stopService(Intent(this@MainActivity, serviceClass)) }
        }.getOrDefault(false)
        if (initialPids.isEmpty() && !stopRequested) return true
        val deadline = System.nanoTime() + timeoutMs * 1_000_000L
        if (stopRequested) delay(20L)
        initialPids.forEach { pid -> runCatching { Process.killProcess(pid) } }
        while (true) {
            val pids = runningVulkanProbePids(processSuffix)
            if (pids.isEmpty()) return true
            pids.forEach { pid -> runCatching { Process.killProcess(pid) } }
            if (System.nanoTime() >= deadline) {
                Log.e("VulkanScope", "Previous dedicated Vulkan probe process remained alive after bounded teardown for $processSuffix: pids=$pids")
                return false
            }
            delay(10L)
        }
    }

    private suspend fun runServiceProbe(
        group: String,
        surface: Surface?,
        timeoutMs: Long,
        modeSnapshot: DriverMode
    ): String = runServiceProbeEndpoint(
        group,
        surface,
        timeoutMs,
        modeSnapshot,
        VulkanProbeService::class.java,
        ":vulkan_probe",
        probeMutex
    )

    private suspend fun runBackgroundIsolatedProbe(
        group: String,
        lane: Int,
        modeSnapshot: DriverMode,
        driverPaths: Pair<String?, String?>
    ): String {
        val safeLane = lane.coerceIn(0, BACKGROUND_PROBE_LANES - 1)
        val timeoutMs = if (group in ISOLATED_ADVANCED_GROUPS.keys) 30_000L else 12_000L
        return runServiceProbeEndpoint(
            group,
            null,
            timeoutMs,
            modeSnapshot,
            backgroundProbeServices[safeLane],
            backgroundProbeProcessSuffixes[safeLane],
            backgroundProbeMutexes[safeLane],
            driverPaths
        )
    }

    private suspend fun runServiceProbeEndpoint(
        group: String,
        surface: Surface?,
        timeoutMs: Long,
        modeSnapshot: DriverMode,
        serviceClass: Class<out android.app.Service>,
        processSuffix: String,
        endpointMutex: Mutex,
        resolvedDriverPaths: Pair<String?, String?>? = null
    ): String = endpointMutex.withLock {
        fun unavailableProbe(reason: String): String = if (group == "base") {
            JSONObject().put("status", "unavailable").put("reason", reason).put("baseReportComplete", false).put("devices", JSONArray()).toString()
        } else {
            JSONObject().put("status", "unavailable").put("group", group).put("reason", reason).put("devices", JSONArray()).toString()
        }
        val hostProbeStartNanos = System.nanoTime()
        if (driverImportInFlight) return@withLock unavailableProbe("The Vulkan probe was deferred while the private driver bundle was being updated.")
        val quiescenceStartNanos = System.nanoTime()
        if (!ensureVulkanProbeProcessQuiescent(serviceClass, processSuffix)) return@withLock unavailableProbe("The previous dedicated Vulkan probe process could not be terminated within the bounded teardown window.")
        val quiescenceMs = elapsedMillis(quiescenceStartNanos)
        val maxProbeResultBytes = 64L * 1024L * 1024L
        val resultFile = File(cacheDir, "vulkan_probe_${java.util.UUID.randomUUID()}.json")
        val crashMarkerFile = File(resultFile.absolutePath + ".crash")
        val terminalFile = File(resultFile.absolutePath + ".done")
        val timingFile = File(resultFile.absolutePath + ".timing")
        resultFile.delete()
        crashMarkerFile.delete()
        terminalFile.delete()
        timingFile.delete()
        val driverPaths = resolvedDriverPaths ?: withContext(Dispatchers.IO) {
            findTurnipIcd(modeSnapshot) to findTurnipBundle(modeSnapshot)
        }
        val intent = Intent(this@MainActivity, serviceClass)
            .putExtra(VulkanProbeService.EXTRA_QUERY_GROUP, group)
            .putExtra(VulkanProbeService.EXTRA_DRIVER_MODE, modeSnapshot.name)
            .putExtra(VulkanProbeService.EXTRA_DRIVER_ICD, driverPaths.first)
            .putExtra(VulkanProbeService.EXTRA_DRIVER_BUNDLE, driverPaths.second)
            .putExtra(VulkanProbeService.EXTRA_HOOK_LIB_DIR, applicationInfo.nativeLibraryDir)
            .putExtra(VulkanProbeService.EXTRA_RESULT_PATH, resultFile.absolutePath)
            .putExtra(VulkanProbeService.EXTRA_TERMINAL_PATH, terminalFile.absolutePath)
            .putExtra(VulkanProbeService.EXTRA_TIMING_PATH, timingFile.absolutePath)
            .putExtra(VulkanProbeService.EXTRA_TIMEOUT_MS, timeoutMs)
        if (surface != null) intent.putExtra(VulkanProbeService.EXTRA_SURFACE, surface)
        val started = runCatching { withContext(Dispatchers.Main.immediate) { startService(intent) } }.isSuccess
        if (!started) {
            resultFile.delete()
            crashMarkerFile.delete()
            terminalFile.delete()
            timingFile.delete()
            return@withLock if (group == "base") {
                "{\"status\":\"unavailable\",\"reason\":\"Unable to start the dedicated Vulkan probe process\",\"devices\":[]}"
            } else {
                "{\"status\":\"unavailable\",\"group\":${JSONObject.quote(group)},\"reason\":\"Unable to start the dedicated Vulkan query process\",\"devices\":[]}"
            }
        }
        var timingSnapshot: Map<String, Long> = emptyMap()
        val result = withContext(Dispatchers.IO) {
            var value: String? = null
            var lastObservedLength = -1L
            var lastObservedModified = -1L
            var lastObservedInode = -1L
            fun crashDetected(): Boolean = crashMarkerFile.isFile && crashMarkerFile.length() > 0L
            fun unavailable(reason: String): String = unavailableProbe(reason)
            fun terminalCandidate(candidate: String): Boolean {
                return strictProbeTerminalCandidate(candidate, group == "base")
            }
            fun readPublishedCandidate(): String? {
                if (!resultFile.isFile) return null
                val resultLength = resultFile.length()
                if (resultLength !in 1L..maxProbeResultBytes) return null
                return runCatching { readFileTextLimited(resultFile, maxProbeResultBytes.toInt()) }.getOrNull()
            }
            fun readTimingTelemetry(): Map<String, Long> {
                if (!timingFile.isFile || timingFile.length() !in 1L..16_384L) return emptyMap()
                return runCatching {
                    val json = JSONObject(readFileTextLimited(timingFile, 16_384))
                    buildMap {
                        listOf("dispatchToWorkerMs", "libraryLoadMs", "nativeCallMs", "terminalValidationMs", "servicePreTerminalMs").forEach { key ->
                            if (json.has(key) && !json.isNull(key)) put(key, json.getLong(key).coerceAtLeast(0L))
                        }
                    }
                }.getOrDefault(emptyMap())
            }
            suspend fun readServiceTerminalCandidateWithGrace(graceMs: Long = 300L): String? {
                val deadline = System.nanoTime() + graceMs * 1_000_000L
                do {
                    val candidate = readPublishedCandidate()
                    if (candidate != null && terminalCandidate(candidate)) return candidate
                    delay(20L)
                } while (System.nanoTime() < deadline)
                return readPublishedCandidate()?.takeIf(::terminalCandidate)
            }
            try {
                withTimeout(timeoutMs) {
                    while (value == null) {
                        if (terminalFile.isFile) {
                            val terminalCandidateText = readServiceTerminalCandidateWithGrace()
                            value = terminalCandidateText ?: unavailable("The dedicated Vulkan $group probe signaled completion but no valid terminal JSON became readable within the bounded handoff window.")
                            Log.i("VulkanScope", "Observed service-owned terminal $group publication; accepted only after a bounded stable-result reread.")
                            continue
                        }
                        if (crashDetected()) {
                            if (group == "base") {
                                val completedBeforeCrash = if (resultFile.isFile && resultFile.length() in 1L..maxProbeResultBytes) {
                                    runCatching { readFileTextLimited(resultFile, maxProbeResultBytes.toInt()) }.getOrNull()?.takeIf { candidate ->
                                        runCatching { JSONObject(candidate).optBoolean("baseReportComplete", false) }.getOrDefault(false)
                                    }
                                } else null
                                value = completedBeforeCrash ?: unavailable("The dedicated Vulkan base probe terminated before publishing a complete base report.")
                            } else {
                                value = unavailable("The dedicated Vulkan $group probe terminated after a native signal before publishing a valid result.")
                            }
                            stopVulkanProbeProcess(serviceClass, processSuffix)
                            continue
                        }
                        if (resultFile.isFile) {
                            val resultLength = resultFile.length()
                            val resultModified = resultFile.lastModified()
                            if (resultLength > maxProbeResultBytes) {
                                value = unavailable("The Vulkan probe result exceeded the safety size limit.")
                                stopVulkanProbeProcess(serviceClass, processSuffix)
                                continue
                            }
                            val resultInode = runCatching { android.system.Os.stat(resultFile.path).st_ino }.getOrDefault(-1L)
                            val checkpointChanged = resultLength != lastObservedLength || resultModified != lastObservedModified || resultInode != lastObservedInode
                            if (resultLength > 0L && checkpointChanged) {
                                lastObservedLength = resultLength
                                lastObservedModified = resultModified
                                lastObservedInode = resultInode
                                if (crashDetected()) continue
                                kotlinx.coroutines.delay(10L)
                            } else {
                                kotlinx.coroutines.delay(15L)
                            }
                        } else {
                            kotlinx.coroutines.delay(12L)
                        }
                    }
                }
            } catch (_: kotlinx.coroutines.TimeoutCancellationException) {
                val publishedBeforeTimeout = readPublishedCandidate()?.takeIf(::terminalCandidate)
                stopVulkanProbeProcess(serviceClass, processSuffix)
                delay(150L)
                val settledPublication = publishedBeforeTimeout ?: readPublishedCandidate()?.takeIf(::terminalCandidate)
                value = settledPublication ?: unavailable("The dedicated $group probe did not complete within the timeout.")
                if (settledPublication != null) Log.w("VulkanScope", "Recovered an atomic $group probe publication at the timeout boundary instead of discarding it.")
            } catch (cancelled: CancellationException) {
                withContext(NonCancellable) {
                    val quiescent = ensureVulkanProbeProcessQuiescent(serviceClass, processSuffix)
                    if (!quiescent) Log.e("VulkanScope", "Cancelled Vulkan $group probe did not reach a confirmed quiescent state before request cleanup.")
                }
                throw cancelled
            } finally {
                timingSnapshot = readTimingTelemetry()
                resultFile.delete()
                crashMarkerFile.delete()
                terminalFile.delete()
                timingFile.delete()
            }
            value ?: unavailable("The dedicated $group probe returned no result.")
        }
        val hostTotalMs = elapsedMillis(hostProbeStartNanos)
        withContext(Dispatchers.Main.immediate) {
            val timingEntries = linkedMapOf<String, Long>()
            timingEntries["service/$group/host_total"] = hostTotalMs
            timingEntries["service/$group/quiescence"] = quiescenceMs
            timingSnapshot["dispatchToWorkerMs"]?.let { timingEntries["service/$group/dispatch_to_worker"] = it }
            timingSnapshot["libraryLoadMs"]?.let { timingEntries["service/$group/library_load"] = it }
            timingSnapshot["nativeCallMs"]?.let { timingEntries["service/$group/native_call"] = it }
            timingSnapshot["terminalValidationMs"]?.let { timingEntries["service/$group/terminal_validation"] = it }
            timingSnapshot["servicePreTerminalMs"]?.let { timingEntries["service/$group/pre_terminal"] = it }
            queryTimingMs = queryTimingMs + timingEntries
        }
        result
    }


}

internal fun hdrTypeName(type: Int): String = when (type) {
    1 -> "Dolby Vision"
    2 -> "HDR10"
    3 -> "HLG"
    4 -> "HDR10+"
    6 -> "HLG+"
    else -> "Android HDR type $type"
}

internal fun formatHz(value: Float): String = String.format(java.util.Locale.US, "%.2f Hz", value)
internal fun formatLuminance(value: Float): String = if (!value.isFinite() || value == android.view.Display.HdrCapabilities.INVALID_LUMINANCE) "Unavailable" else String.format(java.util.Locale.US, "%.3f cd/m²", value)

internal fun hdrTypesText(display: DisplayReport): String = when {
    display.hdrTypes.isNotEmpty() -> display.hdrTypes.joinToString(", ")
    display.hdrCapabilityStatus == "available" -> "None reported"
    display.hdrCapabilityStatus == "unknown" -> "Unknown"
    else -> "Unavailable"
}

internal fun apiAtLeast(value: String, major: Int, minor: Int): Boolean {
    val parts = value.trim().split('.')
    val parsedMajor = parts.getOrNull(0)?.toIntOrNull() ?: return false
    val parsedMinor = parts.getOrNull(1)?.toIntOrNull() ?: return false
    return parsedMajor > major || (parsedMajor == major && parsedMinor >= minor)
}

internal fun apiVersionAtLeast(value: String, required: String): Boolean {
    fun parse(version: String): Triple<Int, Int, Int>? {
        val parts = version.trim().split('.')
        val major = parts.getOrNull(0)?.toIntOrNull() ?: return null
        val minor = parts.getOrNull(1)?.toIntOrNull() ?: return null
        val patch = parts.getOrNull(2)?.toIntOrNull() ?: 0
        return Triple(major, minor, patch)
    }
    val actual = parse(value) ?: return false
    val minimum = parse(required) ?: return false
    return actual.first > minimum.first ||
        actual.first == minimum.first && (actual.second > minimum.second ||
            actual.second == minimum.second && actual.third >= minimum.third)
}

internal fun surfaceColorSpaceExtensionEvidence(extensions: List<ExtensionEntry>, enumerationStatus: String): Pair<Boolean, String> {
    val present = extensions.any { it.name == "VK_EXT_swapchain_colorspace" }
    return when {
        present -> true to "available"
        enumerationStatus == "available" -> false to "not_exposed"
        else -> false to "unknown"
    }
}

internal fun markMetadataProbeUnavailable(base: VulkanReport, reason: String): VulkanReport {
    return base.copy(
        devices = base.devices.map { device ->
            device.copy(surfaceColorSpaceExtensionAvailable = false, surfaceColorSpaceExtensionStatus = "unknown", surfaceColorSpaceExtensionEnabled = false)
        },
        instanceExtensionStatus = "unavailable",
        instanceExtensionReason = reason.take(1024),
        instanceLayerStatus = "unavailable",
        instanceLayerReason = reason.take(1024)
    )
}

internal fun markSurfaceProbeState(base: VulkanReport, status: String, reason: String): VulkanReport {
    val colorEvidence = surfaceColorSpaceExtensionEvidence(base.instanceExtensions, base.instanceExtensionStatus)
    return base.copy(
    devices = base.devices.map { device ->
        device.copy(
            surfaceAvailable = false,
            surfacePresentationSupported = false,
            surfaceColorSpaceExtensionAvailable = colorEvidence.first,
            surfaceColorSpaceExtensionStatus = colorEvidence.second,
            surfaceColorSpaceExtensionEnabled = false,
            surfaceFormatQueryResult = -1,
            surfaceFormatQueryResultSecond = -1,
            surfaceFormatQuerySecondAttempted = false,
            surfaceFormatQueryAttempted = false,
            surfaceDependentWsiQueryStatus = "unknown",
            surfaceFormatQuerySafetyRejected = false,
            surfaceCapabilities = emptyList(),
            surfaceFormats = emptyList(),
            presentModes = emptyList(),
            presentationQueues = emptyList(),
            presentationQueueEvidence = emptyList(),
            surfaceQueueQuerySafetyRejected = false,
            surfaceQueryStatus = status,
            surfaceQueryReason = reason.take(1024),
            detailedProperties = replaceQueryStatus(
                device.detailedProperties,
                "Surface probe",
                when (status) {
                    "not_applicable" -> "Not applicable: ${reason.take(1024)}"
                    "unknown" -> "Unknown: ${reason.take(1024)}"
                    else -> "Unavailable: ${reason.take(1024)}"
                }
            )
        )
    }
)
}

internal fun mergeMetadataReport(base: VulkanReport, raw: String): VulkanReport {
    val root = runCatching { JSONObject(raw) }.getOrElse {
        return markMetadataProbeUnavailable(base, "The dedicated metadata probe returned invalid JSON.")
    }
    if (root.optString("status", "unavailable") != "available") {
        val reason = root.optString("reason", "The dedicated metadata probe did not complete.")
        return markMetadataProbeUnavailable(base, reason)
    }
    val registry = root.optJSONObject("registryCoverage")
    val parsedCoverage = if (registry != null) {
        val structs = mutableListOf<String>()
        val structArray = registry.optJSONArray("implementedPhysicalDeviceStructs") ?: JSONArray()
        for (i in 0 until structArray.length()) structs += structArray.optString(i)
        val groups = mutableListOf<String>()
        val groupArray = registry.optJSONArray("validatedRuntimeQueryGroups") ?: JSONArray()
        for (i in 0 until groupArray.length()) groups += groupArray.optString(i)
        RegistryCoverage(
            baseline = registry.optString("baseline", base.registryCoverage.baseline),
            mode = registry.optString("mode", base.registryCoverage.mode),
            implementedPhysicalDeviceStructCount = registry.optInt("implementedPhysicalDeviceStructCount", base.registryCoverage.implementedPhysicalDeviceStructCount),
            validatedRuntimeQueryGroupCount = registry.optInt("validatedRuntimeQueryGroupCount", base.registryCoverage.validatedRuntimeQueryGroupCount),
            runtimeRegistryTokenReferenceCount = registry.optInt("runtimeRegistryTokenReferenceCount", registry.optInt("runtimeExtensionTokenCount", base.registryCoverage.runtimeRegistryTokenReferenceCount)),
            catalogSchemaVersion = registry.optInt("catalogSchemaVersion", base.registryCoverage.catalogSchemaVersion),
            reportSchema = registry.optString("reportSchema", base.registryCoverage.reportSchema),
            headerBaseline = registry.optString("headerBaseline", base.registryCoverage.headerBaseline),
            instanceDependencyCandidateCount = registry.optInt("instanceDependencyCandidateCount", base.registryCoverage.instanceDependencyCandidateCount),
            implementedPhysicalDeviceStructs = structs,
            validatedRuntimeQueryGroups = groups
        )
    } else base.registryCoverage
    val instanceExtensions = parseExtensions(root.optJSONArray("instanceExtensions"), "Instance")
    val instanceLayers = mutableListOf<LayerEntry>()
    val layerArray = root.optJSONArray("instanceLayers") ?: JSONArray()
    for (i in 0 until layerArray.length()) {
        val layer = layerArray.optJSONObject(i) ?: continue
        val layerExtensions = parseExtensions(layer.optJSONArray("extensions"), "Instance layer")
        instanceLayers += LayerEntry(layer.optString("name"), layer.optString("description"), layer.optInt("specVersion"), layer.optInt("implementationVersion"), layerExtensions, layer.optString("extensionStatus", "unknown"), layer.optString("extensionReason", ""), layer.optBoolean("extensionsComplete", false))
    }
    val instanceExtensionStatus = root.optString("instanceExtensionStatus", "unknown")
    val instanceExtensionReason = root.optString("instanceExtensionReason", "")
    val instanceLayerStatus = root.optString("instanceLayerStatus", "unknown")
    val instanceLayerReason = root.optString("instanceLayerReason", "")
    val mergedInstanceExtensions = when {
        root.optBoolean("instanceExtensionsComplete", false) -> instanceExtensions
        instanceExtensionStatus == "incomplete" -> (base.instanceExtensions + instanceExtensions).distinctBy { it.name to it.specVersion }
        else -> base.instanceExtensions
    }
    val mergedInstanceLayers = when {
        root.optBoolean("instanceLayersComplete", false) -> instanceLayers
        instanceLayerStatus == "incomplete" -> (base.instanceLayers + instanceLayers).distinctBy { it.name to it.specVersion }
        else -> base.instanceLayers
    }
    val colorEvidence = surfaceColorSpaceExtensionEvidence(mergedInstanceExtensions, instanceExtensionStatus)
    return base.copy(
        devices = base.devices.map { device ->
            if (colorEvidence.second == "unknown" && device.surfaceColorSpaceExtensionStatus != "unknown") device
            else device.copy(surfaceColorSpaceExtensionAvailable = colorEvidence.first, surfaceColorSpaceExtensionStatus = colorEvidence.second, surfaceColorSpaceExtensionEnabled = if (colorEvidence.second == "not_exposed") false else device.surfaceColorSpaceExtensionEnabled)
        },
        instanceExtensions = mergedInstanceExtensions,
        instanceLayers = mergedInstanceLayers,
        registryCoverage = if (root.optBoolean("registryCoverageComplete", false)) parsedCoverage else base.registryCoverage,
        instanceExtensionStatus = instanceExtensionStatus,
        instanceExtensionReason = instanceExtensionReason,
        instanceLayerStatus = instanceLayerStatus,
        instanceLayerReason = instanceLayerReason
    )
}

internal fun sanitizeReportLabel(value: String): String = value.removePrefix("Validated extension coverage · ")
internal fun sanitizeReportSection(section: String): String = sanitizeReportLabel(section)

internal fun mergeQueryProperties(existing: List<PropertyEntry>, incoming: List<PropertyEntry>): List<PropertyEntry> =
    (existing + incoming).distinctBy { Triple(it.section, it.name, it.value) }

internal fun replaceQueryStatus(properties: List<PropertyEntry>, name: String, value: String): List<PropertyEntry> =
    properties.filterNot { it.section == "Vulkan Query Status" && it.name == name } +
        PropertyEntry("Vulkan Query Status", name, value)

internal fun replaceDetailedProperty(properties: List<PropertyEntry>, section: String, name: String, value: String): List<PropertyEntry> =
    properties.filterNot { it.section == section && it.name == name } + PropertyEntry(section, name, value)

internal fun vkResultText(value: Int): String = when (value) {
    0 -> "VK_SUCCESS (0)"
    1 -> "VK_NOT_READY (1)"
    2 -> "VK_TIMEOUT (2)"
    3 -> "VK_EVENT_SET (3)"
    4 -> "VK_EVENT_RESET (4)"
    5 -> "VK_INCOMPLETE (5)"
    -1 -> "VK_ERROR_OUT_OF_HOST_MEMORY (-1)"
    -2 -> "VK_ERROR_OUT_OF_DEVICE_MEMORY (-2)"
    -3 -> "VK_ERROR_INITIALIZATION_FAILED (-3)"
    -4 -> "VK_ERROR_DEVICE_LOST (-4)"
    -5 -> "VK_ERROR_MEMORY_MAP_FAILED (-5)"
    -6 -> "VK_ERROR_LAYER_NOT_PRESENT (-6)"
    -7 -> "VK_ERROR_EXTENSION_NOT_PRESENT (-7)"
    -8 -> "VK_ERROR_FEATURE_NOT_PRESENT (-8)"
    -9 -> "VK_ERROR_INCOMPATIBLE_DRIVER (-9)"
    -10 -> "VK_ERROR_TOO_MANY_OBJECTS (-10)"
    -11 -> "VK_ERROR_FORMAT_NOT_SUPPORTED (-11)"
    -12 -> "VK_ERROR_FRAGMENTED_POOL (-12)"
    -13 -> "VK_ERROR_UNKNOWN (-13)"
    -1000011001 -> "VK_ERROR_VALIDATION_FAILED (-1000011001)"
    -1000069000 -> "VK_ERROR_OUT_OF_POOL_MEMORY (-1000069000)"
    -1000072003 -> "VK_ERROR_INVALID_EXTERNAL_HANDLE (-1000072003)"
    -1000257000 -> "VK_ERROR_INVALID_OPAQUE_CAPTURE_ADDRESS (-1000257000)"
    -1000161000 -> "VK_ERROR_FRAGMENTATION (-1000161000)"
    1000297000 -> "VK_PIPELINE_COMPILE_REQUIRED (1000297000)"
    -1000174001 -> "VK_ERROR_NOT_PERMITTED (-1000174001)"
    -1000000000 -> "VK_ERROR_SURFACE_LOST_KHR (-1000000000)"
    -1000000001 -> "VK_ERROR_NATIVE_WINDOW_IN_USE_KHR (-1000000001)"
    1000001003 -> "VK_SUBOPTIMAL_KHR (1000001003)"
    -1000001004 -> "VK_ERROR_OUT_OF_DATE_KHR (-1000001004)"
    -1000003001 -> "VK_ERROR_INCOMPATIBLE_DISPLAY_KHR (-1000003001)"
    -1000012000 -> "VK_ERROR_INVALID_SHADER_NV (-1000012000)"
    -1000023000 -> "VK_ERROR_IMAGE_USAGE_NOT_SUPPORTED_KHR (-1000023000)"
    -1000023001 -> "VK_ERROR_VIDEO_PICTURE_LAYOUT_NOT_SUPPORTED_KHR (-1000023001)"
    -1000023002 -> "VK_ERROR_VIDEO_PROFILE_OPERATION_NOT_SUPPORTED_KHR (-1000023002)"
    -1000023003 -> "VK_ERROR_VIDEO_PROFILE_FORMAT_NOT_SUPPORTED_KHR (-1000023003)"
    -1000023004 -> "VK_ERROR_VIDEO_PROFILE_CODEC_NOT_SUPPORTED_KHR (-1000023004)"
    -1000023005 -> "VK_ERROR_VIDEO_STD_VERSION_NOT_SUPPORTED_KHR (-1000023005)"
    -1000158000 -> "VK_ERROR_INVALID_DRM_FORMAT_MODIFIER_PLANE_LAYOUT_EXT (-1000158000)"
    -1000208000 -> "VK_ERROR_PRESENT_TIMING_QUEUE_FULL_EXT (-1000208000)"
    -1000255000 -> "VK_ERROR_FULL_SCREEN_EXCLUSIVE_MODE_LOST_EXT (-1000255000)"
    1000268000 -> "VK_THREAD_IDLE_KHR (1000268000)"
    1000268001 -> "VK_THREAD_DONE_KHR (1000268001)"
    1000268002 -> "VK_OPERATION_DEFERRED_KHR (1000268002)"
    1000268003 -> "VK_OPERATION_NOT_DEFERRED_KHR (1000268003)"
    -1000299000 -> "VK_ERROR_INVALID_VIDEO_STD_PARAMETERS_KHR (-1000299000)"
    -1000338000 -> "VK_ERROR_COMPRESSION_EXHAUSTED_EXT (-1000338000)"
    1000482000 -> "VK_INCOMPATIBLE_SHADER_BINARY_EXT (1000482000)"
    1000483000 -> "VK_PIPELINE_BINARY_MISSING_KHR (1000483000)"
    -1000483000 -> "VK_ERROR_NOT_ENOUGH_SPACE_KHR (-1000483000)"
    else -> "UNKNOWN(raw=$value)"
}

internal fun presentationQueueEvidenceText(entry: PresentationQueueEvidence): String = when {
    entry.queryResult != null && entry.queryResult != 0 -> "Unavailable: ${vkResultText(entry.queryResult)}"
    entry.supported -> "PRESENT"
    else -> "NO PRESENT"
}

internal fun appendSurfaceEnumerationDiagnostics(surface: JSONObject?, capabilities: MutableList<Pair<String, String>>) {
    if (surface == null) return
    if (surface.has("dependentWsiQueryStatus")) capabilities += "Dependent WSI query status" to surface.optString("dependentWsiQueryStatus", "unknown").replace('_', ' ')
    if (surface.has("capabilityQueryApi")) capabilities += "Surface capability query API" to surface.optString("capabilityQueryApi")
    if (surface.optBoolean("capabilities2QueryAttempted", false)) capabilities += "Surface capabilities2 query" to vkResultText(surface.optInt("capabilities2QueryResult"))
    if (surface.has("capabilities2FallbackUsed")) capabilities += "Surface capabilities2 classic fallback" to surface.optBoolean("capabilities2FallbackUsed", false).toString()
    if (surface.has("formatQueryAttempted")) capabilities += "Surface format query attempted" to surface.optBoolean("formatQueryAttempted", false).toString()
    if (surface.optBoolean("formatQueryAttempted", false)) capabilities += "Surface format count query" to vkResultText(surface.optInt("formatQueryResult"))
    if (surface.optBoolean("formatQuerySecondAttempted", false)) capabilities += "Surface format data query" to vkResultText(surface.optInt("formatQueryResultSecond")) else if (surface.optBoolean("formatQueryAttempted", false)) capabilities += "Surface format data query" to "Not attempted"
    if (surface.has("formatQueryAttemptCount")) capabilities += "Surface format bounded attempts" to surface.optInt("formatQueryAttemptCount").toString()
    if (surface.has("formatQuerySafetyRejected")) capabilities += "Surface format safety rejection" to if (surface.optBoolean("formatQuerySafetyRejected")) "YES · local bound rejected the result" else "NO"
    if (surface.has("formatQuerySpecAnomaly")) capabilities += "Surface format specification anomaly" to surface.optBoolean("formatQuerySpecAnomaly").toString()
    if (surface.optString("formatQueryReason").isNotBlank()) capabilities += "Surface format query provenance" to surface.optString("formatQueryReason").take(1024)
    if (surface.has("surfaceFormats2Attempted")) capabilities += "Surface formats2 attempted" to surface.optBoolean("surfaceFormats2Attempted").toString()
    if (surface.has("surfaceFormats2FallbackUsed")) capabilities += "Surface formats2 classic fallback" to surface.optBoolean("surfaceFormats2FallbackUsed").toString()
    if (surface.has("formatEnumerationComplete")) capabilities += "Surface format enumeration" to if (surface.optBoolean("formatEnumerationComplete")) "Complete" else "Incomplete / unavailable"
    if (surface.optBoolean("presentModeQueryAttempted", false)) capabilities += "Present mode count query" to vkResultText(surface.optInt("presentModeCountQueryResult"))
    if (surface.optBoolean("presentModeDataQueryAttempted", false)) capabilities += "Present mode data query" to vkResultText(surface.optInt("presentModeDataQueryResult")) else if (surface.optBoolean("presentModeQueryAttempted", false)) capabilities += "Present mode data query" to "Not attempted"
    if (surface.has("presentModeQueryAttemptCount")) capabilities += "Present mode bounded attempts" to surface.optInt("presentModeQueryAttemptCount").toString()
    if (surface.has("presentModeQuerySafetyRejected")) capabilities += "Present mode safety rejection" to if (surface.optBoolean("presentModeQuerySafetyRejected")) "YES · local bound rejected the result" else "NO"
    if (surface.has("presentModeQuerySpecAnomaly")) capabilities += "Present mode specification anomaly" to surface.optBoolean("presentModeQuerySpecAnomaly").toString()
    if (surface.optString("presentModeQueryReason").isNotBlank()) capabilities += "Present mode query provenance" to surface.optString("presentModeQueryReason").take(1024)
    if (surface.has("presentModeEnumerationComplete")) capabilities += "Present mode enumeration" to if (surface.optBoolean("presentModeEnumerationComplete")) "Complete" else "Incomplete / unavailable"
}

internal fun physicalIdentityKey(vendorId: Long, deviceId: Long): Pair<Long, Long> = vendorId to deviceId

internal fun basePhysicalIdentityUnique(report: VulkanReport, device: DeviceReport): Boolean {
    val key = physicalIdentityKey(device.vendorIdRaw, device.deviceIdRaw)
    return report.devices.count { physicalIdentityKey(it.vendorIdRaw, it.deviceIdRaw) == key } == 1
}

internal fun jsonPhysicalIdentityMatches(array: JSONArray, vendorId: Long, deviceId: Long): List<JSONObject> =
    (0 until array.length()).mapNotNull { array.optJSONObject(it) }.filter {
        it.optLong("vendorId", Long.MIN_VALUE) == vendorId && it.optLong("deviceId", Long.MIN_VALUE) == deviceId
    }

internal fun mergeSurfaceProbeReport(base: VulkanReport, raw: String): VulkanReport {
    val root = runCatching { JSONObject(raw) }.getOrElse {
        return markSurfaceProbeState(base, "unavailable", "The dedicated Surface probe returned invalid JSON.")
    }
    val status = root.optString("status", "unavailable")
    val reason = root.optString("reason", "The dedicated Surface probe did not return a result.")
    if (status != "available" && status != "incomplete") return markSurfaceProbeState(base, status, reason)
    val resultDevices = root.optJSONArray("devices") ?: JSONArray()
    return base.copy(devices = base.devices.map { device ->
        val matches = jsonPhysicalIdentityMatches(resultDevices, device.vendorIdRaw, device.deviceIdRaw)
        val identityUnique = basePhysicalIdentityUnique(base, device) && matches.size == 1
        val match = matches.singleOrNull()
        if (!identityUnique || match == null) {
            val mappingReason = if (!basePhysicalIdentityUnique(base, device) || matches.size > 1)
                "Surface evidence cannot be attributed safely because vendorId/deviceId identifies more than one physical device and no stable cross-process identity is available."
            else "The Surface probe completed but did not return matching evidence for this physical device."
            markSurfaceProbeState(base.copy(devices = listOf(device)), "unavailable", mappingReason).devices.first()
        } else {
            val surface = match.optJSONObject("surface")
            val surfaceFormats = mutableListOf<SurfaceFormatEntry>()
            val formatArray = surface?.optJSONArray("formats") ?: JSONArray()
            for (i in 0 until formatArray.length()) {
                val sf = formatArray.optJSONObject(i) ?: continue
                surfaceFormats += SurfaceFormatEntry(sf.optString("format"), sf.optString("colorSpace"), sf.optString("class"), sf.optString("description"), true)
            }
            val capabilities = mutableListOf<Pair<String, String>>()
            listOf("minImageCount", "maxImageCount", "currentExtent", "minExtent", "maxExtent", "maxImageArrayLayers", "supportedTransforms", "currentTransform", "supportedCompositeAlpha", "supportedUsageFlags", "capabilityResult").forEach { key ->
                if (surface?.has(key) == true) capabilities += key to canonicalSurfaceCapabilityValue(key, surface.optString(key))
            }
            appendSurfaceEnumerationDiagnostics(surface, capabilities)
            capabilities += "Surface probe instance API" to root.optString("selectedApiVersion", "Unknown")
            capabilities += "VK_KHR_get_surface_capabilities2 path" to root.optString("surfaceCapabilities2Status", "unknown").replace('_', ' ')
            if (root.optString("surfaceCapabilities2Reason").isNotBlank()) capabilities += "Surface capabilities2 path reason" to root.optString("surfaceCapabilities2Reason").take(1024)
            if (surface?.has("surfaceFormats2Used") == true) {
                capabilities += "Surface format query API" to if (surface.optBoolean("surfaceFormats2Used")) "vkGetPhysicalDeviceSurfaceFormats2KHR" else "vkGetPhysicalDeviceSurfaceFormatsKHR"
            }
            if (surface?.has("formatCount") == true) capabilities += "Surface format returned count" to surface.optInt("formatCount").toString()
            val presentModes = mutableListOf<String>()
            val presentArray = surface?.optJSONArray("presentModes") ?: JSONArray()
            for (i in 0 until presentArray.length()) presentModes += presentArray.optString(i)
            val presentationQueues = mutableListOf<Pair<Int, Boolean>>()
            val presentationQueueEvidence = mutableListOf<PresentationQueueEvidence>()
            val queueArray = surface?.optJSONArray("queuePresentation") ?: JSONArray()
            for (i in 0 until queueArray.length()) {
                val q = queueArray.optJSONObject(i) ?: continue
                val queueFamily = q.optInt("queueFamily")
                val supported = q.optBoolean("supported")
                presentationQueues += queueFamily to supported
                presentationQueueEvidence += PresentationQueueEvidence(queueFamily, supported, if (q.has("queryResult")) q.optInt("queryResult") else null)
            }
            val surfaceQueueSafetyRejected = surface?.optBoolean("queueQuerySafetyRejected", false) ?: false
            val nestedSurfaceStatus = surface?.optString("queryStatus", "available") ?: "available"
            val nestedSurfaceReason = surface?.optString("queryReason", "") ?: ""
            val surfaceQueryStatus = if (status == "incomplete" && nestedSurfaceStatus == "available") "incomplete" else nestedSurfaceStatus
            val surfaceQueryReason = listOf(reason.takeIf { status == "incomplete" && it.isNotBlank() }, nestedSurfaceReason.takeIf { it.isNotBlank() }).filterNotNull().joinToString(" ").take(1024)
            val surfaceStatusText = when (surfaceQueryStatus) {
                "available" -> "Available"
                "incomplete" -> "Incomplete: partial Surface evidence retained. ${surfaceQueryReason.take(1024)}".trim()
                "not_applicable" -> "Not applicable: ${surfaceQueryReason.take(1024)}".trim()
                "unknown" -> "Unknown: ${surfaceQueryReason.take(1024)}".trim()
                else -> "Unavailable: ${surfaceQueryReason.take(1024)}".trim()
            }
            val surfaceDetails = replaceDetailedProperty(replaceQueryStatus(device.detailedProperties, "Surface probe", surfaceStatusText), "Vulkan Query Safety", "Surface queue-family enumeration safety rejected", surfaceQueueSafetyRejected.toString())
            device.copy(
                surfaceAvailable = surface?.optBoolean("available") == true,
                surfacePresentationSupported = surface?.optBoolean("presentationSupported") == true,
                surfaceColorSpaceExtensionAvailable = root.optBoolean("surfaceColorSpaceExtensionAvailable", false),
                surfaceColorSpaceExtensionStatus = root.optString("surfaceColorSpaceExtensionStatus", if (root.optBoolean("surfaceColorSpaceExtensionAvailable", false)) "available" else "unknown"),
                surfaceColorSpaceExtensionEnabled = root.optBoolean("surfaceColorSpaceExtensionEnabled", false),
                surfaceFormatQueryResult = surface?.optInt("formatQueryResult", -1) ?: -1,
                surfaceFormatQueryResultSecond = surface?.optInt("formatQueryResultSecond", -1) ?: -1,
                surfaceFormatQuerySecondAttempted = surface?.optBoolean("formatQuerySecondAttempted", false) ?: false,
                surfaceFormatQueryAttempted = surface?.optBoolean("formatQueryAttempted", false) ?: false,
                surfaceDependentWsiQueryStatus = surface?.optString("dependentWsiQueryStatus", "unknown") ?: "unknown",
                surfaceFormatQuerySafetyRejected = surface?.optBoolean("formatQuerySafetyRejected", false) ?: false,
                surfaceFormatEnumerationComplete = surface?.optBoolean("formatEnumerationComplete", false) ?: false,
                surfaceFormatQuerySpecAnomaly = surface?.optBoolean("formatQuerySpecAnomaly", false) ?: false,
                surfacePresentModeEnumerationComplete = surface?.optBoolean("presentModeEnumerationComplete", false) ?: false,
                surfacePresentModeQuerySpecAnomaly = surface?.optBoolean("presentModeQuerySpecAnomaly", false) ?: false,
                surfaceCapabilities = capabilities,
                surfaceFormats = surfaceFormats,
                presentModes = presentModes,
                presentationQueues = presentationQueues,
                presentationQueueEvidence = presentationQueueEvidence,
                detailedProperties = surfaceDetails,
                surfaceQueueQuerySafetyRejected = surfaceQueueSafetyRejected,
                surfaceQueryStatus = surfaceQueryStatus,
                surfaceQueryReason = surfaceQueryReason.take(1024)
            )
        }
    })
}


internal fun mergeAdvancedQueryReport(base: VulkanReport, raw: String, group: String, label: String): VulkanReport {
    val root = runCatching { JSONObject(raw) }.getOrElse {
        return base.copy(
            devices = base.devices.map { device ->
                device.copy(detailedProperties = replaceQueryStatus(device.detailedProperties, "$label query", "Unavailable: dedicated query returned invalid data."))
            },
            instanceGroupProperties = if (group == "groups") emptyList() else base.instanceGroupProperties,
            instanceGroupStatus = if (group == "groups") "unavailable" else base.instanceGroupStatus,
            instanceGroupReason = if (group == "groups") "The dedicated physical-device-group query returned invalid data." else base.instanceGroupReason,
            instanceGroupEnumerationResult = if (group == "groups") null else base.instanceGroupEnumerationResult,
            instanceGroupEnumerationComplete = if (group == "groups") false else base.instanceGroupEnumerationComplete
        )
    }
    val status = root.optString("status", "unavailable")
    val reason = root.optString("reason", "The dedicated advanced query did not return a result.")
    val resultDevices = root.optJSONArray("devices") ?: JSONArray()
    val parsed = mutableListOf<Triple<Long, Long, Pair<List<FeatureEntry>, List<PropertyEntry>>>>()
    val core14Status = mutableMapOf<Pair<Long, Long>, Pair<String, String>>()
    for (i in 0 until resultDevices.length()) {
        val item = resultDevices.optJSONObject(i) ?: continue
        val vendor = item.optLong("vendorId", -1L)
        val deviceId = item.optLong("deviceId", -1L)
        val features = mutableListOf<FeatureEntry>()
        val featureArray = item.optJSONArray("features") ?: JSONArray()
        for (j in 0 until featureArray.length()) {
            val feature = featureArray.optJSONObject(j) ?: continue
            features += FeatureEntry(sanitizeReportLabel(feature.optString("name")), feature.optBoolean("supported"))
        }
        val properties = mutableListOf<PropertyEntry>()
        val pa = item.optJSONArray("properties") ?: JSONArray()
        for (j in 0 until pa.length()) {
            val prop = pa.optJSONObject(j) ?: continue
            properties += PropertyEntry(sanitizeReportSection(prop.optString("section")), prop.optString("name"), prop.optString("value"))
        }
        parsed += Triple(vendor, deviceId, features to properties)
        if (group == "core14") {
            core14Status[vendor to deviceId] = item.optString("status", "available") to item.optString("reason", "")
        }
    }
    val instanceGroupProperties = if (group == "groups") {
        val values = mutableListOf<PropertyEntry>()
        val groupProperties = root.optJSONArray("groupProperties") ?: JSONArray()
        for (i in 0 until groupProperties.length()) {
            val prop = groupProperties.optJSONObject(i) ?: continue
            values += PropertyEntry(sanitizeReportSection(prop.optString("section")), prop.optString("name"), prop.optString("value"))
        }
        values
    } else {
        emptyList()
    }
    val instanceGroupEnumerationResult = if (group == "groups" && root.has("groupEnumerationResult") && !root.isNull("groupEnumerationResult")) root.optInt("groupEnumerationResult") else null
    val instanceGroupEnumerationComplete = group == "groups" && root.optBoolean("groupEnumerationComplete", false)
    val imageFormatQueryResultsByDevice: Map<Pair<Long, Long>, List<ImageFormatQueryResultEntry>> = if (group == "imageFormat2") {
        val valuesByDevice = mutableMapOf<Pair<Long, Long>, List<ImageFormatQueryResultEntry>>()
        for (i in 0 until resultDevices.length()) {
            val item = resultDevices.optJSONObject(i) ?: continue
            val vendor = item.optLong("vendorId", -1L)
            val deviceId = item.optLong("deviceId", -1L)
            val results = item.optJSONArray("imageFormatQueryResults") ?: JSONArray()
            valuesByDevice[vendor to deviceId] = parseImageFormatQueryResults(results)
        }
        valuesByDevice
    } else {
        emptyMap()
    }
    val formatEntriesByDevice: Map<Pair<Long, Long>, List<FormatEntry>> = if (group == "format2") {
        val valuesByDevice = mutableMapOf<Pair<Long, Long>, List<FormatEntry>>()
        for (i in 0 until resultDevices.length()) {
            val item = resultDevices.optJSONObject(i) ?: continue
            val vendor = item.optLong("vendorId", -1L)
            val deviceId = item.optLong("deviceId", -1L)
            val properties = item.optJSONArray("properties") ?: JSONArray()
            val entries = (0 until properties.length()).mapNotNull { index ->
                val prop = properties.optJSONObject(index) ?: return@mapNotNull null
                val name = prop.optString("name")
                val value = prop.optString("value")
                val legacyLinear = parseUnsignedHexLong(Regex("(?:^|, )linear=0x([0-9a-fA-F]+)").find(value)?.groupValues?.getOrNull(1)) ?: return@mapNotNull null
                val legacyOptimal = parseUnsignedHexLong(Regex("(?:^|, )optimal=0x([0-9a-fA-F]+)").find(value)?.groupValues?.getOrNull(1)) ?: return@mapNotNull null
                val legacyBuffer = parseUnsignedHexLong(Regex("(?:^|, )buffer=0x([0-9a-fA-F]+)").find(value)?.groupValues?.getOrNull(1)) ?: return@mapNotNull null
                val featureFlags2Available = Regex("featureFlags2Available=(true|false)").find(value)?.groupValues?.getOrNull(1)?.toBooleanStrictOrNull() == true
                val flags2Linear = parseUnsignedHexLong(Regex("featureFlags2 linear=0x([0-9a-fA-F]+)").find(value)?.groupValues?.getOrNull(1))
                val flags2Optimal = parseUnsignedHexLong(Regex("featureFlags2 linear=0x[0-9a-fA-F]+ optimal=0x([0-9a-fA-F]+)").find(value)?.groupValues?.getOrNull(1))
                val flags2Buffer = parseUnsignedHexLong(Regex("featureFlags2 linear=0x[0-9a-fA-F]+ optimal=0x[0-9a-fA-F]+ buffer=0x([0-9a-fA-F]+)").find(value)?.groupValues?.getOrNull(1))
                val linear = if (featureFlags2Available && flags2Linear != null) flags2Linear else legacyLinear
                val optimal = if (featureFlags2Available && flags2Optimal != null) flags2Optimal else legacyOptimal
                val buffer = if (featureFlags2Available && flags2Buffer != null) flags2Buffer else legacyBuffer
                FormatEntry(name, linear != 0L || optimal != 0L || buffer != 0L, linear, optimal, buffer)
            }
            valuesByDevice[vendor to deviceId] = entries
        }
        valuesByDevice
    } else {
        emptyMap()
    }
    val videoQueuesByDevice: List<Triple<Long, Long, Map<Int, Long>>> = if (group == "queue2") {
        val valuesByDevice = mutableListOf<Triple<Long, Long, Map<Int, Long>>>()
        for (i in 0 until resultDevices.length()) {
            val item = resultDevices.optJSONObject(i) ?: continue
            val vendor = item.optLong("vendorId", -1L)
            val deviceId = item.optLong("deviceId", -1L)
            val values = mutableMapOf<Int, Long>()
            val vq = item.optJSONArray("videoQueues") ?: JSONArray()
            for (j in 0 until vq.length()) {
                val v = vq.optJSONObject(j) ?: continue
                values[v.optInt("index")] = v.optLong("videoCodecOperations")
            }
            valuesByDevice += Triple(vendor, deviceId, values)
        }
        valuesByDevice
    } else {
        emptyList()
    }
    val resultIdentityCounts = (0 until resultDevices.length()).mapNotNull { resultDevices.optJSONObject(it) }.groupingBy { physicalIdentityKey(it.optLong("vendorId", Long.MIN_VALUE), it.optLong("deviceId", Long.MIN_VALUE)) }.eachCount()
    return base.copy(devices = base.devices.map { device ->
        val deviceId = device.deviceIdRaw
        val identityKey = physicalIdentityKey(device.vendorIdRaw, deviceId)
        val identityUnique = basePhysicalIdentityUnique(base, device) && resultIdentityCounts[identityKey] == 1
        val match = if (identityUnique) parsed.singleOrNull { it.first == device.vendorIdRaw && it.second == deviceId } else null
        val videoMatch = if (identityUnique) videoQueuesByDevice.singleOrNull { it.first == device.vendorIdRaw && it.second == deviceId } else null
        val formatMatch = if (identityUnique) formatEntriesByDevice[identityKey] else null
        val imageFormatQueryResultMatch = if (identityUnique) imageFormatQueryResultsByDevice[identityKey] else null
        val hasVideoQueueExtension = device.extensions.any { it.name == "VK_KHR_video_queue" }
        val videoQueueAbsenceKnown = device.deviceExtensionStatus == "available"
        val mergedQueues = device.queues.map { q ->
            when {
                status == "not_applicable" -> q.copy(videoCodecQueryStatus = "not_applicable", videoCodecQueryReason = reason.ifBlank { "The Queue Family Properties2 query does not apply to this device." })
                !hasVideoQueueExtension && videoQueueAbsenceKnown -> q.copy(videoCodecQueryStatus = "not_applicable", videoCodecQueryReason = "Complete device-extension enumeration did not report VK_KHR_video_queue for this device.")
                !hasVideoQueueExtension -> q.copy(videoCodecQueryStatus = "unknown", videoCodecQueryReason = "Device-extension enumeration is incomplete or unavailable, so absence of VK_KHR_video_queue cannot be established.")
                !identityUnique -> q.copy(videoCodecQueryStatus = "unavailable", videoCodecQueryReason = "Isolated queue evidence cannot be attributed safely because the physical-device identity is ambiguous across processes.")
                status != "available" && status != "incomplete" -> q.copy(videoCodecQueryStatus = "unavailable", videoCodecQueryReason = reason.ifBlank { "Queue Family Properties2 query did not complete." })
                videoMatch == null || !videoMatch.third.containsKey(q.index) -> q.copy(videoCodecQueryStatus = "unavailable", videoCodecQueryReason = "VkQueueFamilyVideoPropertiesKHR evidence was not returned for this queue family.")
                status == "incomplete" -> q.copy(videoCodecOperations = videoMatch.third.getValue(q.index), videoCodecQueryStatus = "incomplete", videoCodecQueryReason = reason.ifBlank { "Physical-device enumeration was incomplete; this queue's positive evidence was retained." })
                else -> q.copy(videoCodecOperations = videoMatch.third.getValue(q.index), videoCodecQueryStatus = "available", videoCodecQueryReason = "")
            }
        }
        val mergedFormats = if (formatMatch != null) {
            val byName = LinkedHashMap<String, FormatEntry>()
            device.formats.forEach { byName[it.name] = it }
            formatMatch.forEach { byName[it.name] = it }
            byName.values.toList()
        } else {
            device.formats
        }
        val mergedFeatures = if (match != null) device.features + match.third.first.filterNot { incoming -> device.features.any { it.name == incoming.name } } else device.features
        val mergedProperties = if (match != null) mergeQueryProperties(device.detailedProperties, match.third.second) else device.detailedProperties
        val statusProperty = if (!identityUnique && resultIdentityCounts.containsKey(identityKey)) {
            PropertyEntry("Vulkan Query Status", "$label query", "Unavailable: dedicated-process evidence cannot be attributed safely because vendorId/deviceId is ambiguous across physical devices.")
        } else if (status == "available" && match != null) {
            PropertyEntry("Vulkan Query Status", "$label query", "Available")
        } else if (status == "incomplete") {
            PropertyEntry("Vulkan Query Status", "$label query", "Incomplete: ${reason.ifBlank { "the bounded query returned partial evidence." }}")
        } else if (status == "not_applicable") {
            PropertyEntry("Vulkan Query Status", "$label query", "Not applicable: ${reason.ifBlank { "the query does not apply to this device." }}")
        } else {
            PropertyEntry("Vulkan Query Status", "$label query", "Unavailable: ${reason.ifBlank { "the dedicated query did not complete." }}")
        }
        var merged = device.copy(
            features = mergedFeatures,
            detailedProperties = replaceQueryStatus(mergedProperties, "$label query", statusProperty.value),
            imageFormatQueryResults = if (group == "imageFormat2") { if ((status == "available" || status == "incomplete") && imageFormatQueryResultMatch != null) imageFormatQueryResultMatch else emptyList() } else device.imageFormatQueryResults,
            queues = mergedQueues,
            formats = mergedFormats
        )
        if (group == "core14") {
            val coreStatus = core14Status[device.vendorIdRaw to (deviceId ?: -1L)]
            if (coreStatus != null) {
                merged = merged.copy(vulkan14Status = coreStatus.first, vulkan14Reason = coreStatus.second)
            }
        }
        merged
    }, instanceGroupProperties = if (group == "groups") {
        if (status == "available" || status == "incomplete") instanceGroupProperties else emptyList()
    } else base.instanceGroupProperties,
        instanceGroupStatus = if (group == "groups") status else base.instanceGroupStatus,
        instanceGroupReason = if (group == "groups") reason.take(1024) else base.instanceGroupReason,
        instanceGroupEnumerationResult = if (group == "groups") instanceGroupEnumerationResult else base.instanceGroupEnumerationResult,
        instanceGroupEnumerationComplete = if (group == "groups") instanceGroupEnumerationComplete else base.instanceGroupEnumerationComplete
    )
}

internal fun mergeExtensionGroupReport(base: VulkanReport, raw: String, group: String, extensionName: String): VulkanReport {
    val root = runCatching { JSONObject(raw) }.getOrElse {
        return base.copy(devices = base.devices.map { device ->
            if (device.extensions.any { it.name == extensionName }) {
                device.copy(detailedProperties = replaceQueryStatus(device.detailedProperties, "$extensionName query", "Unavailable: the dedicated $group probe returned invalid data."))
            } else device
        })
    }
    val status = root.optString("status", "unavailable")
    val reason = root.optString("reason", "The dedicated extension query did not return a result.")
    val resultDevices = root.optJSONArray("devices") ?: JSONArray()
    val parsed = mutableListOf<Triple<Long, Long, Pair<List<FeatureEntry>, List<PropertyEntry>>>>()
    for (i in 0 until resultDevices.length()) {
        val item = resultDevices.optJSONObject(i) ?: continue
        val vendor = item.optLong("vendorId", -1L)
        val deviceId = item.optLong("deviceId", -1L)
        val features = mutableListOf<FeatureEntry>()
        val featureArray = item.optJSONArray("features") ?: JSONArray()
        for (j in 0 until featureArray.length()) {
            val f = featureArray.optJSONObject(j) ?: continue
            features += FeatureEntry(sanitizeReportLabel(f.optString("name")), f.optBoolean("supported"))
        }
        val properties = mutableListOf<PropertyEntry>()
        val propertyArray = item.optJSONArray("properties") ?: JSONArray()
        for (j in 0 until propertyArray.length()) {
            val prop = propertyArray.optJSONObject(j) ?: continue
            properties += PropertyEntry(sanitizeReportSection(prop.optString("section")), prop.optString("name"), prop.optString("value"))
        }
        parsed += Triple(vendor, deviceId, features to properties)
    }
    val parsedIdentityCounts = parsed.groupingBy { physicalIdentityKey(it.first, it.second) }.eachCount()
    return base.copy(devices = base.devices.map { device ->
        if (!device.extensions.any { it.name == extensionName }) device
        else {
            val identityKey = physicalIdentityKey(device.vendorIdRaw, device.deviceIdRaw)
            val identityUnique = basePhysicalIdentityUnique(base, device) && parsedIdentityCounts[identityKey] == 1
            val match = if (identityUnique) parsed.singleOrNull { it.first == device.vendorIdRaw && it.second == device.deviceIdRaw } else null
            when {
                !identityUnique && parsedIdentityCounts.containsKey(identityKey) -> device.copy(
                    detailedProperties = replaceQueryStatus(device.detailedProperties, "$extensionName query", "Unavailable: dedicated-process extension evidence cannot be attributed safely because vendorId/deviceId is ambiguous across physical devices.")
                )
                (status == "available" || status == "incomplete") && match != null -> {
                    val mergedFeatures = device.features + match.third.first.filterNot { incoming -> device.features.any { existing -> existing.name == incoming.name } }
                    val mergedProperties = mergeQueryProperties(device.detailedProperties, match.third.second)
                    device.copy(
                        features = mergedFeatures,
                        detailedProperties = replaceQueryStatus(mergedProperties, "$extensionName query", if (status == "available") "Available" else "Incomplete: ${reason.ifBlank { "physical-device enumeration was incomplete; partial positive evidence was retained." }}")
                    )
                }
                status == "not_applicable" -> device.copy(
                    detailedProperties = replaceQueryStatus(device.detailedProperties, "$extensionName query", "Not applicable: the extension was not enumerated by the dedicated probe.")
                )
                else -> device.copy(
                    detailedProperties = replaceQueryStatus(device.detailedProperties, "$extensionName query", "Unavailable: ${reason.ifBlank { "the dedicated extension query did not complete." }}")
                )
            }
        }
    })
}

internal fun parseReport(raw: String): VulkanReport {
    val root = JSONObject(raw)
    val error = if (root.has("error") && !root.isNull("error")) root.optString("error") else null
    val nativeStatus = root.optString("status", if (error == null) "available" else "unavailable")
    val instanceExtensions = parseExtensions(root.optJSONArray("instanceExtensions"))
    val instanceLayers = mutableListOf<LayerEntry>()
    val layerArray = root.optJSONArray("instanceLayers") ?: JSONArray()
    for (i in 0 until layerArray.length()) {
        val layer = layerArray.optJSONObject(i) ?: continue
        val layerExtensions = parseExtensions(layer.optJSONArray("extensions"), "Instance layer")
        instanceLayers += LayerEntry(layer.optString("name"), layer.optString("description"), layer.optInt("specVersion"), layer.optInt("implementationVersion"), layerExtensions, layer.optString("extensionStatus", "unknown"), layer.optString("extensionReason", ""), layer.optBoolean("extensionsComplete", false))
    }
    val devices = mutableListOf<DeviceReport>()
    val deviceArray = root.optJSONArray("devices") ?: JSONArray()
    for (i in 0 until deviceArray.length()) {
        val item = deviceArray.optJSONObject(i) ?: continue
        val deviceLayers = mutableListOf<LayerEntry>()
        val deviceLayerArray = item.optJSONArray("deviceLayers") ?: JSONArray()
        for (j in 0 until deviceLayerArray.length()) {
            val layer = deviceLayerArray.optJSONObject(j) ?: continue
            val layerExtensions = parseExtensions(layer.optJSONArray("extensions"), "Device layer")
            deviceLayers += LayerEntry(layer.optString("name"), layer.optString("description"), layer.optInt("specVersion"), layer.optInt("implementationVersion"), layerExtensions, layer.optString("extensionStatus", "unknown"), layer.optString("extensionReason", ""), layer.optBoolean("extensionsComplete", false))
        }
        val extensions = parseExtensions(item.optJSONArray("deviceExtensions"))
        val features = mutableListOf<FeatureEntry>()
        val featureArray = item.optJSONArray("features") ?: JSONArray()
        for (j in 0 until featureArray.length()) {
            val feature = featureArray.optJSONObject(j) ?: continue
            features += FeatureEntry(sanitizeReportLabel(feature.optString("name")), feature.optBoolean("supported"))
        }
        val versionedFeatureArray = item.optJSONArray("versionedFeatures") ?: JSONArray()
        for (j in 0 until versionedFeatureArray.length()) {
            val feature = versionedFeatureArray.optJSONObject(j) ?: continue
            features += FeatureEntry(sanitizeReportLabel(feature.optString("name")), feature.optBoolean("supported"))
        }
        val queues = mutableListOf<QueueEntry>()
        val queueArray = item.optJSONArray("queues") ?: JSONArray()
        for (j in 0 until queueArray.length()) {
            val q = queueArray.optJSONObject(j) ?: continue
            val queueFlags = q.optLong("flags")
            val knownQueueFlags = 0x57FL
            queues += QueueEntry(q.optInt("index"), q.optInt("count"), q.optInt("timestampValidBits"), queueFlags, q.optBoolean("graphics"), q.optBoolean("compute"), q.optBoolean("transfer"), q.optBoolean("sparse"), q.optBoolean("protected"), q.optBoolean("videoDecode"), q.optBoolean("videoEncode"), q.optBoolean("opticalFlow"), q.optBoolean("dataGraph"), queueFlags and knownQueueFlags.inv(), q.optString("minImageTransferGranularity", "0 × 0 × 0"), q.optLong("videoCodecOperations"), q.optString("videoCodecQueryStatus", "unknown"), q.optString("videoCodecQueryReason", ""))
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
            formats += FormatEntry(format.optString("name"), format.optBoolean("supported"), format.optLong("linear"), format.optLong("optimal"), format.optLong("buffer"))
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
            detailedProperties += PropertyEntry(sanitizeReportSection(prop.optString("section")), prop.optString("name"), prop.optString("value"))
        }
        val imageFormatQueryResults = parseImageFormatQueryResults(item.optJSONArray("imageFormatQueryResults") ?: JSONArray())
        val queueQuerySafetyRejected = item.optBoolean("queueQuerySafetyRejected", false)
        val memoryHeapSafetyRejected = item.optBoolean("memoryHeapSafetyRejected", false)
        val memoryTypeSafetyRejected = item.optBoolean("memoryTypeSafetyRejected", false)
        detailedProperties += PropertyEntry("Vulkan Query Safety", "Queue-family enumeration safety rejected", queueQuerySafetyRejected.toString())
        detailedProperties += PropertyEntry("Vulkan Query Safety", "Memory-heap count safety rejected", memoryHeapSafetyRejected.toString())
        detailedProperties += PropertyEntry("Vulkan Query Safety", "Memory-type count safety rejected", memoryTypeSafetyRejected.toString())
        val surface = item.optJSONObject("surface")
        val surfaceAvailable = surface?.optBoolean("available") == true
        val surfacePresentationSupported = surface?.optBoolean("presentationSupported") == true
        val surfaceColorSpaceExtensionAvailable = surface?.optBoolean("colorSpaceExtensionAvailable") == true
        val surfaceColorSpaceExtensionEnabled = surface?.optBoolean("colorSpaceExtensionEnabled") == true
        val surfaceFormatQueryResult = surface?.optInt("formatQueryResult", -1) ?: -1
        val surfaceFormatQueryResultSecond = surface?.optInt("formatQueryResultSecond", -1) ?: -1
        val surfaceFormatQuerySecondAttempted = surface?.optBoolean("formatQuerySecondAttempted", false) ?: false
        val surfaceFormatQueryAttempted = surface?.optBoolean("formatQueryAttempted", false) ?: false
        val surfaceDependentWsiQueryStatus = surface?.optString("dependentWsiQueryStatus", "unknown") ?: "unknown"
        val surfaceFormatQuerySafetyRejected = surface?.optBoolean("formatQuerySafetyRejected", false) ?: false
        val surfaceQueueQuerySafetyRejected = surface?.optBoolean("queueQuerySafetyRejected", false) ?: false
        detailedProperties += PropertyEntry("Vulkan Query Safety", "Surface queue-family enumeration safety rejected", surfaceQueueQuerySafetyRejected.toString())
        if (surface != null) {
            listOf("minImageCount", "maxImageCount", "currentExtent", "minExtent", "maxExtent", "maxImageArrayLayers", "supportedTransforms", "currentTransform", "supportedCompositeAlpha", "supportedUsageFlags").forEach { key ->
                if (surface.has(key)) capabilityPairs += key to canonicalSurfaceCapabilityValue(key, surface.optString(key))
            }
            appendSurfaceEnumerationDiagnostics(surface, capabilityPairs)
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
            item.optString("driverVersionText", item.optString("driverVersion", "Unknown")),
            "0x${item.optLong("vendorId").toString(16).uppercase()}",
            item.optLong("vendorId"),
            "0x${item.optLong("deviceId").toString(16).uppercase()}",
            item.optLong("deviceId"),
            deviceTypeName(item.optInt("deviceType")),
            deviceLayers,
            extensions,
            item.optString("deviceExtensionStatus", "unknown"),
            item.optString("deviceExtensionReason", "Device-extension enumeration status was not reported by the native collector."),
            features,
            queues,
            heaps,
            memoryTypes,
            formats,
            limits,
            detailedProperties,
            imageFormatQueryResults,
            item.optString("extendedQueryStatus", "unknown"),
            item.optString("extendedQueryReason", "Extended physical-device query status was not reported by the native collector."),
            surfaceAvailable,
            surfacePresentationSupported,
            surfaceColorSpaceExtensionAvailable,
            surfaceColorSpaceExtensionEnabled,
            surfaceFormatQueryResult,
            surfaceFormatQueryResultSecond,
            surfaceFormatQuerySecondAttempted,
            surfaceFormatQuerySafetyRejected,
            capabilityPairs,
            surfaceFormats,
            presentModes,
            presentationQueues,
            vulkan14Status = item.optString("vulkan14Status", "not_applicable"),
            vulkan14Reason = item.optString("vulkan14Reason", ""),
            queueQuerySafetyRejected = queueQuerySafetyRejected,
            memoryHeapSafetyRejected = memoryHeapSafetyRejected,
            memoryTypeSafetyRejected = memoryTypeSafetyRejected,
            surfaceQueueQuerySafetyRejected = surfaceQueueQuerySafetyRejected,
            deviceLayerStatus = item.optString("deviceLayerStatus", "unknown"),
            deviceLayerReason = item.optString("deviceLayerReason", "Device-layer enumeration status was not reported by the native collector."),
            deviceLayersComplete = item.optBoolean("deviceLayersComplete", false),
            surfaceQueryStatus = surface?.optString("queryStatus", "unknown") ?: "unknown",
            surfaceQueryReason = surface?.optString("queryReason", "Base probe does not own final live-Surface evidence.") ?: "Base probe does not own final live-Surface evidence.",
            surfaceFormatQueryAttempted = surfaceFormatQueryAttempted,
            surfaceDependentWsiQueryStatus = surfaceDependentWsiQueryStatus
        )
    }
    val registry = root.optJSONObject("registryCoverage")
    val registryCoverage = if (registry != null) {
        val structs = mutableListOf<String>()
        val structArray = registry.optJSONArray("implementedPhysicalDeviceStructs") ?: JSONArray()
        for (i in 0 until structArray.length()) structs += structArray.optString(i)
        val groups = mutableListOf<String>()
        val groupArray = registry.optJSONArray("validatedRuntimeQueryGroups") ?: JSONArray()
        for (i in 0 until groupArray.length()) groups += groupArray.optString(i)
        RegistryCoverage(
            baseline = registry.optString("baseline", "Unknown"),
            mode = registry.optString("mode", "Unknown"),
            implementedPhysicalDeviceStructCount = registry.optInt("implementedPhysicalDeviceStructCount", 0),
            validatedRuntimeQueryGroupCount = registry.optInt("validatedRuntimeQueryGroupCount", 0),
            runtimeRegistryTokenReferenceCount = registry.optInt("runtimeRegistryTokenReferenceCount", registry.optInt("runtimeExtensionTokenCount", 0)),
            catalogSchemaVersion = registry.optInt("catalogSchemaVersion", 0),
            reportSchema = registry.optString("reportSchema", "Unknown"),
            headerBaseline = registry.optString("headerBaseline", "Unknown"),
            instanceDependencyCandidateCount = registry.optInt("instanceDependencyCandidateCount", 0),
            implementedPhysicalDeviceStructs = structs,
            validatedRuntimeQueryGroups = groups
        )
    } else RegistryCoverage()
    val effectiveError = when {
        error != null -> error
        nativeStatus == "unavailable" -> root.optString("reason", "Vulkan base probe reported unavailable.")
        nativeStatus == "incomplete" -> root.optString("reason", "Physical-device enumeration was incomplete; partial positive evidence was retained.")
        nativeStatus == "not_applicable" -> root.optString("reason", "Vulkan device enumeration is not applicable.")
        devices.isEmpty() -> root.optString("reason", "Vulkan instance was created but no physical device was returned.")
        else -> null
    }
    Log.i("VulkanScope", "Vulkan parsed status=$nativeStatus devices=${devices.size} error=${effectiveError ?: "none"}")
    return VulkanReport(
        loaderVersion = root.optString("loaderVersion", "Unknown"),
        instanceExtensions = instanceExtensions,
        instanceLayers = instanceLayers,
        devices = devices,
        error = effectiveError,
        registryCoverage = registryCoverage,
        instanceExtensionStatus = root.optString("instanceExtensionStatus", "unknown"),
        instanceExtensionReason = root.optString("instanceExtensionReason", ""),
        instanceLayerStatus = root.optString("instanceLayerStatus", "unknown"),
        instanceLayerReason = root.optString("instanceLayerReason", ""),
        baseReportComplete = root.optBoolean("baseReportComplete", false),
        physicalDeviceEnumerationResult = if (root.has("physicalDeviceEnumerationResult") && !root.isNull("physicalDeviceEnumerationResult")) root.optInt("physicalDeviceEnumerationResult") else null,
        physicalDeviceEnumerationComplete = root.optBoolean("physicalDeviceEnumerationComplete", false),
        physicalDeviceEnumerationSafetyRejected = root.optBoolean("physicalDeviceEnumerationSafetyRejected", false),
        physicalDeviceEnumerationReason = root.optString("physicalDeviceEnumerationReason", ""),
        instanceApiVersion = root.optString("instanceApiVersion", root.optString("requestedInstanceApiVersion", "Unknown"))
    )
}

internal fun parseExtensions(array: JSONArray?, scopeOverride: String? = null): List<ExtensionEntry> {
    if (array == null) return emptyList()
    val result = mutableListOf<ExtensionEntry>()
    for (i in 0 until array.length()) {
        val item = array.optJSONObject(i) ?: continue
        result += ExtensionEntry(item.optString("name"), scopeOverride ?: item.optString("scope"), item.optInt("specVersion"))
    }
    return result.sortedBy { it.name }
}

internal fun deviceTypeName(type: Int): String = when (type) {
    0 -> "VK_PHYSICAL_DEVICE_TYPE_OTHER"
    1 -> "VK_PHYSICAL_DEVICE_TYPE_INTEGRATED_GPU"
    2 -> "VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU"
    3 -> "VK_PHYSICAL_DEVICE_TYPE_VIRTUAL_GPU"
    4 -> "VK_PHYSICAL_DEVICE_TYPE_CPU"
    else -> "UNKNOWN(raw=$type)"
}
