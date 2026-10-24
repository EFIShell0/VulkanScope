@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.efishell.vulkanscope

import android.graphics.Color
import android.graphics.RenderEffect as AndroidRenderEffect
import android.graphics.Shader
import android.os.Build
import android.view.Surface
import android.view.KeyEvent as AndroidKeyEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.content.Context
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.type
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.ScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



@Composable
internal fun VulkanScopeApp(
    displayReport: DisplayReport,
    report: VulkanReport?,
    loading: Boolean,
    collectionStatus: CollectionStatus,
    updateStatus: UpdateStatus,
    updateCheckInFlight: Boolean,
    networkBannerState: NetworkBannerState,
    validatedNetworkAvailable: Boolean,
    networkStateKnown: Boolean,
    updateConfirmation: AppUpdate?,
    updateTransferState: UpdateTransferState?,
    updateCancelConfirmationVisible: Boolean,
    onRequestUpdateConfirmation: (AppUpdate) -> Unit,
    onDismissUpdateConfirmation: () -> Unit,
    onConfirmUpdateDownload: (AppUpdate) -> Unit,
    onPauseUpdateDownload: () -> Unit,
    onResumeUpdateDownload: () -> Unit,
    onRequestCancelUpdateDownload: () -> Unit,
    onDismissCancelUpdateDownload: () -> Unit,
    onConfirmCancelUpdateDownload: () -> Unit,
    onInstallDownloadedUpdate: () -> Unit,
    onCloseUpdateTransfer: () -> Unit,
    onCheckForUpdates: () -> Unit,
    directUpdatesEnabled: Boolean,
    directUpdatesConsentVisible: Boolean,
    onDirectUpdatesChanged: (Boolean) -> Unit,
    openingAnimationEnabled: Boolean,
    onOpeningAnimationChanged: (Boolean) -> Unit,
    onDismissDirectUpdatesConsent: () -> Unit,
    onConfirmDirectUpdatesConsent: () -> Unit,
    surfaceReady: (Long, Surface) -> Unit,
    surfaceDestroyed: (Long, Surface) -> Unit,
    surfaceHostGeneration: Long,
    driverMode: DriverMode,
    turnipSupport: TurnipSupport,
    turnipManagerRevision: Int,
    turnipManagerBusy: Boolean,
    storagePermissionDeniedFeedback: Boolean,
    onDriverModeChanged: (DriverMode) -> Unit,
    pendingDriverModeConfirmation: DriverMode?,
    onConfirmDriverModeChange: () -> Unit,
    onDismissDriverModeConfirmation: () -> Unit,
    pendingTurnipActivationSlot: Int?,
    onConfirmTurnipActivation: () -> Unit,
    onDismissTurnipActivation: () -> Unit,
    onInstallDriverBundle: () -> Unit,
    onActivateTurnipDriver: (Int) -> Unit,
    onRemoveTurnipDriver: (Int) -> Unit,
    onPageOpened: (Page) -> Unit,
    onRequestQuery: (String) -> Unit,
    queryTimingMs: Map<String, Long>
) {
    var page by rememberSaveable(
        stateSaver = androidx.compose.runtime.saveable.Saver<Page, String>(
            save = { it.name },
            restore = { saved ->
                val restored = Page.values().find { it.name == saved } ?: Page.Overview
                if (restored == Page.Display) Page.Surface else restored
            }
        )
    ) { mutableStateOf(Page.Overview) }
    val chromeContext = androidx.compose.ui.platform.LocalContext.current.applicationContext
    val chromePreferences = remember(chromeContext) { ChromeAutoHideSettings(chromeContext) }
    var chromeInteractionEpoch by remember { mutableIntStateOf(0) }
    var chromeIdle by remember { mutableStateOf(false) }
    var settingsSection by rememberSaveable { mutableStateOf<SettingsSection?>(null) }
    LaunchedEffect(chromeInteractionEpoch, page, settingsSection, chromePreferences.enabled, chromePreferences.hideHeader, chromePreferences.hideNavigation) {
        chromeIdle = false
        if (chromePreferences.enabled && (chromePreferences.hideHeader || chromePreferences.hideNavigation)) {
            kotlinx.coroutines.delay(CHROME_INACTIVITY_MILLIS)
            chromeIdle = true
        }
    }
    val headerVisible = shouldShowAppChrome(chromeIdle, chromePreferences.enabled, chromePreferences.hideHeader)
    val navigationVisible = shouldShowAppChrome(chromeIdle, chromePreferences.enabled, chromePreferences.hideNavigation)
    var surfaceSection by rememberSaveable { mutableStateOf<SurfaceSection?>(null) }
    var encyclopediaSeed by rememberSaveable { mutableStateOf("") }
    var selectedDeviceIndex by rememberSaveable { mutableIntStateOf(0) }
    var sharedStorageBrowserOverlay by remember { mutableStateOf<SharedStorageBrowserOverlayRequest?>(null) }
    LaunchedEffect(page) { onPageOpened(page) }
    LaunchedEffect(report?.devices?.size) {
        val count = report?.devices?.size ?: 0
        selectedDeviceIndex = if (count == 0) 0 else selectedDeviceIndex.coerceIn(0, count - 1)
    }

    MaterialExpressiveTheme(
        colorScheme = darkColorScheme(
            background = VulkanBlack,
            surface = VulkanSurface,
            surfaceVariant = VulkanSurfaceTonal,
            primary = VulkanAccent,
            onPrimary = VulkanTextPrimary,
            primaryContainer = VulkanAccentContainer,
            onPrimaryContainer = VulkanTextPrimary,
            secondary = VulkanAccentSoft,
            onSecondary = VulkanTextPrimary,
            secondaryContainer = ComposeColor(0xFF2A2022),
            onSecondaryContainer = VulkanTextPrimary,
            tertiary = VulkanAccentSoft,
            onBackground = VulkanTextPrimary,
            onSurface = VulkanTextPrimary,
            outline = VulkanOutline,
            outlineVariant = VulkanOutlineVariant
        ),
        typography = VulkanTypography,
        shapes = VulkanExpressiveShapes,
        motionScheme = MotionScheme.expressive()
    ) {
        BackHandler(enabled = page != Page.Overview || settingsSection != null || surfaceSection != null) {
            when {
                page == Page.Settings && settingsSection != null -> settingsSection = null
                page == Page.Surface && surfaceSection != null -> surfaceSection = null
                else -> page = Page.Overview
            }
        }

        val density = LocalDensity.current
        val chromeSourceLayer = rememberGraphicsLayer()
        val chromeBlurredLayer = rememberGraphicsLayer()
        var pageChromeBackdrop by remember { mutableStateOf<ChromeBackdropSource?>(null) }
        val chromeBackdropReporter = remember {
            { layer: GraphicsLayer, rootOffset: Offset? ->
                if (rootOffset == null) {
                    if (pageChromeBackdrop?.layer === layer) pageChromeBackdrop = null
                } else {
                    pageChromeBackdrop = ChromeBackdropSource(layer, rootOffset)
                }
                Unit
            }
        }
        val chromeBlurRadiusPx = with(density) { 24.dp.toPx() }
        val chromeRenderEffect = remember(chromeBlurRadiusPx) {
            AndroidRenderEffect.createBlurEffect(chromeBlurRadiusPx, chromeBlurRadiusPx, Shader.TileMode.CLAMP).asComposeRenderEffect()
        }
        LaunchedEffect(chromeBlurredLayer, chromeRenderEffect) {
            chromeBlurredLayer.renderEffect = chromeRenderEffect
        }
        var transientOverlayHeightPx by remember { mutableIntStateOf(0) }
        var bottomNavigationHeightPx by remember { mutableIntStateOf(0) }
        val pinnedPagerHeights = remember { mutableStateMapOf<String, Int>() }
        val transientOverlayContentInset = with(density) { transientOverlayHeightPx.toDp() }.let { if (it > 0.dp) it + 8.dp else 0.dp }
        val pinnedPagerHeightPx = pinnedPagerHeights.values.maxOrNull() ?: 0
        val pinnedPagerContentInset = with(density) { pinnedPagerHeightPx.toDp() }.let { if (it > 0.dp) it + 4.dp else 0.dp }
        val coordinatedPinnedPagerInset by animateDpAsState(
            targetValue = pinnedPagerContentInset,
            animationSpec = tween(OverlayCoordinationMotionMillis, easing = FastOutSlowInEasing),
            label = "coordinatedPinnedPagerInset"
        )
        val systemNavigationInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        val requestedNavigationContentInset = if (navigationVisible) {
            maxOf(with(density) { bottomNavigationHeightPx.toDp() } + PrimaryNavigationContentGap, systemNavigationInset)
        } else systemNavigationInset
        val bottomNavigationContentInset by animateDpAsState(
            targetValue = requestedNavigationContentInset,
            animationSpec = tween(CHROME_MOTION_MILLIS, easing = FastOutSlowInEasing),
            label = "automaticNavigationContentInset"
        )
        val stickyPagerStateReporter = remember(pinnedPagerHeights) {
            { key: String, pinned: Boolean, heightPx: Int ->
                if (pinned && heightPx > 0) pinnedPagerHeights[key] = heightPx else pinnedPagerHeights.remove(key)
                Unit
            }
        }

        CompositionLocalProvider(
            LocalChromeAutoHideSettings provides chromePreferences,
            LocalValidatedNetwork provides validatedNetworkAvailable,
            LocalSharedStorageBrowserLauncher provides { request -> sharedStorageBrowserOverlay = request },
            LocalTransientOverlayContentInset provides transientOverlayContentInset,
            LocalPinnedPagerContentInset provides pinnedPagerContentInset,
            LocalAppHeaderVisible provides headerVisible,
            LocalStickyPagerStateReporter provides stickyPagerStateReporter,
            LocalBottomNavigationContentInset provides bottomNavigationContentInset,
            LocalChromeBackdropReporter provides chromeBackdropReporter
        ) {
            Scaffold(
                modifier = Modifier
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            var lastInteractionAt = 0L
                            while (true) {
                                val event = awaitPointerEvent(androidx.compose.ui.input.pointer.PointerEventPass.Initial)
                                if (event.changes.any { it.pressed }) {
                                    val now = android.os.SystemClock.uptimeMillis()
                                    if (now - lastInteractionAt >= 250L) {
                                        chromeInteractionEpoch++
                                        lastInteractionAt = now
                                    }
                                }
                            }
                        }
                    }
                    .onPreviewKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown) chromeInteractionEpoch++
                        false
                    },
                containerColor = ComposeColor.Black,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                topBar = {
                    AutoHideHeader(visible = sharedStorageBrowserOverlay == null && headerVisible) {
                    val headerBackdrop = pageChromeBackdrop ?: ChromeBackdropSource(chromeBlurredLayer, Offset.Zero)
                    AppHeader(
                        page,
                        onBack = {
                            when {
                                page == Page.Settings && settingsSection != null -> settingsSection = null
                                page == Page.Surface && surfaceSection != null -> surfaceSection = null
                                else -> page = Page.Overview
                            }
                        },
                        onSettings = { settingsSection = null; page = Page.Settings },
                        backdropLayer = headerBackdrop.layer,
                        backdropRootOffset = headerBackdrop.rootOffset
                    )
                    }
                }
            ) { padding ->
                val headerContentInset = maxOf(
                    padding.calculateTopPadding(),
                    WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                )
                CompositionLocalProvider(LocalAppHeaderContentInset provides headerContentInset) {
                    Box(Modifier.fillMaxSize()) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .drawWithContent {
                                chromeSourceLayer.record { this@drawWithContent.drawContent() }
                                chromeBlurredLayer.record { drawLayer(chromeSourceLayer) }
                                drawLayer(chromeSourceLayer)
                            }
                    ) {
                    androidx.compose.runtime.key(surfaceHostGeneration) {
                        SurfaceProbe(
                            modifier = Modifier.matchParentSize(),
                            hostGeneration = surfaceHostGeneration,
                            onCreated = surfaceReady,
                            onDestroyed = surfaceDestroyed
                        )
                    }
                    if (loading) {
                        LoadingView()
                    } else {
                        val current = report
                        if (current == null) {
                            EmptyState("No Vulkan® report")
                        } else {
                            AnimatedContent(
                                targetState = page,
                                transitionSpec = {
                                    val forward = pageTransitionIndex(targetState) > pageTransitionIndex(initialState)
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
                                Column(Modifier.fillMaxSize()) {
                                    if (current.devices.size > 1) {
                                        PhysicalDeviceSelector(current.devices, selectedDeviceIndex) { selectedDeviceIndex = it }
                                    }
                                    Box(Modifier.weight(1f)) {
                                        val evidenceContext = androidx.compose.ui.platform.LocalContext.current
                                        CompositionLocalProvider(
                                            LocalEvidenceActionEnvironment provides EvidenceActionEnvironment(
                                                openEncyclopedia = { term -> encyclopediaSeed = term; page = Page.Encyclopedia },
                                                addWatch = { token ->
                                                    val clean = token.trim().take(256)
                                                    if (clean.isNotBlank()) {
                                                        val prefs = evidenceContext.getSharedPreferences("analysis_tools", Context.MODE_PRIVATE)
                                                        val watched = prefs.getStringSet("watched", emptySet())?.toMutableSet() ?: mutableSetOf()
                                                        if (watched.size < ANALYSIS_MAX_WATCHED || clean in watched) {
                                                            watched += clean
                                                            prefs.edit().putStringSet("watched", watched).apply()
                                                        }
                                                    }
                                                }
                                            )
                                        ) {
                                            CompositionLocalProvider(LocalAppHeaderContentInset provides if (current.devices.size > 1) 0.dp else headerContentInset) {
                                            PageContent(
                                                page = targetPage,
                                                report = current,
                                                display = displayReport,
                                                driverMode = driverMode,
                                                turnipSupport = turnipSupport,
                                                turnipManagerRevision = turnipManagerRevision,
                                                turnipManagerBusy = turnipManagerBusy,
                                                storagePermissionDeniedFeedback = storagePermissionDeniedFeedback,
                                                selectedDeviceIndex = selectedDeviceIndex,
                                                onDriverModeChanged = onDriverModeChanged,
                                                onInstallDriverBundle = onInstallDriverBundle,
                                                onActivateTurnipDriver = onActivateTurnipDriver,
                                                onRemoveTurnipDriver = onRemoveTurnipDriver,
                                                onCheckForUpdates = onCheckForUpdates,
                                                updateCheckInFlight = updateCheckInFlight,
                                                directUpdatesEnabled = directUpdatesEnabled,
                                                onDirectUpdatesChanged = onDirectUpdatesChanged,
                                                openingAnimationEnabled = openingAnimationEnabled,
                                                onOpeningAnimationChanged = onOpeningAnimationChanged,
                                                collectionStatus = collectionStatus,
                                                onNavigate = { target ->
                                                    when (target) {
                                                        Page.Settings -> {
                                                            settingsSection = null
                                                            page = Page.Settings
                                                        }
                                                        Page.Display -> {
                                                            surfaceSection = SurfaceSection.DISPLAY
                                                            page = Page.Surface
                                                        }
                                                        Page.Surface -> {
                                                            surfaceSection = null
                                                            page = Page.Surface
                                                        }
                                                        else -> page = target
                                                    }
                                                },
                                                settingsSection = settingsSection,
                                                onSettingsSectionChanged = { settingsSection = it },
                                                surfaceSection = surfaceSection,
                                                onSurfaceSectionChanged = { surfaceSection = it },
                                                onRequestQuery = onRequestQuery,
                                                queryTimingMs = queryTimingMs,
                                                encyclopediaSeed = encyclopediaSeed
                                            )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    }
                    val navigationBackdrop = pageChromeBackdrop ?: ChromeBackdropSource(chromeBlurredLayer, Offset.Zero)
                    SystemNavigationBackdrop(
                        backdropLayer = navigationBackdrop.layer,
                        backdropRootOffset = navigationBackdrop.rootOffset,
                        modifier = Modifier.matchParentSize().zIndex(6f)
                    )
                    if (sharedStorageBrowserOverlay == null) {
                        AutoHideNavigation(
                            visible = navigationVisible,
                            modifier = Modifier.align(Alignment.BottomCenter).onSizeChanged { bottomNavigationHeightPx = it.height }
                        ) {
                        CompactBottomNavigationBar(
                            selectedPage = selectedNavigationPage(page),
                            onPageSelected = { target ->
                                if (target == Page.Surface) surfaceSection = null
                                page = target
                            },
                            backdropLayer = navigationBackdrop.layer,
                            backdropRootOffset = navigationBackdrop.rootOffset,
                            modifier = Modifier
                        )
                        }
                        TransientStatusOverlayHost(
                            collectionStatus = collectionStatus,
                            networkStateKnown = networkStateKnown,
                            networkAvailable = validatedNetworkAvailable,
                            networkBannerState = networkBannerState,
                            updateStatus = updateStatus,
                            onInstallUpdate = onRequestUpdateConfirmation,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = headerContentInset + coordinatedPinnedPagerInset)
                                .zIndex(11f)
                                .onSizeChanged { transientOverlayHeightPx = it.height }
                        )
                    }
                    sharedStorageBrowserOverlay?.let { overlay ->
                        SharedStorageBrowserDialog(
                            request = overlay.request,
                            onDismiss = {
                                val closing = sharedStorageBrowserOverlay
                                sharedStorageBrowserOverlay = null
                                closing?.onClosed?.invoke()
                            },
                            onImport = overlay.onImport,
                            onExport = overlay.onExport
                        )
                    }
                    }
                }
            }
        }
        if (directUpdatesConsentVisible) {
            DirectUpdatesConsentDialog(
                appName = "VulkanScope",
                releaseSource = "github.com/EFIShell0/VulkanScope/releases",
                onDismiss = onDismissDirectUpdatesConsent,
                onConfirm = onConfirmDirectUpdatesConsent
            )
        }
        updateConfirmation?.let { update ->
            UpdateConfirmationDialog(
                update = update,
                networkAvailable = validatedNetworkAvailable,
                onDismiss = onDismissUpdateConfirmation,
                onConfirm = { onConfirmUpdateDownload(update) }
            )
        }
        updateTransferState?.let { transfer ->
            UpdateTransferDialog(
                state = transfer,
                networkAvailable = validatedNetworkAvailable,
                onPause = onPauseUpdateDownload,
                onResume = onResumeUpdateDownload,
                onRequestCancel = onRequestCancelUpdateDownload,
                onInstall = onInstallDownloadedUpdate,
                onClose = onCloseUpdateTransfer
            )
        }
        if (updateCancelConfirmationVisible) {
            UpdateCancelConfirmationDialog(
                onResume = onDismissCancelUpdateDownload,
                onConfirmCancel = onConfirmCancelUpdateDownload
            )
        }
        pendingDriverModeConfirmation?.let { target ->
            AlertDialog(
            modifier = Modifier.border(1.dp, VulkanAccentSoft.copy(alpha = 0.46f), MaterialTheme.shapes.extraLarge),
                onDismissRequest = onDismissDriverModeConfirmation,
                title = { QuestionDialogTitle(if (target == DriverMode.SYSTEM) "Switch to System Vulkan® driver?" else "Switch to Turnip driver?") },
                text = {
                    Text(
                        if (target == DriverMode.SYSTEM)
                            "VulkanScope will deactivate the current Turnip source and start a fresh Vulkan collection with Android's system Vulkan driver."
                        else
                            "VulkanScope will deactivate the System source and start a fresh Vulkan collection with the currently active Turnip driver."
                    )
                },
                confirmButton = { ExpressiveContainedTextButton(if (target == DriverMode.SYSTEM) "Use System" else "Use Turnip", onClick = onConfirmDriverModeChange) },
                dismissButton = { ExpressiveCancelButton(onClick = onDismissDriverModeConfirmation) }
            )
        }
        pendingTurnipActivationSlot?.let { slot ->
            AlertDialog(
            modifier = Modifier.border(1.dp, VulkanAccentSoft.copy(alpha = 0.46f), MaterialTheme.shapes.extraLarge),
                onDismissRequest = onDismissTurnipActivation,
                title = { QuestionDialogTitle("Activate Turnip driver?") },
                text = { Text("Slot %02d will become VulkanScope's active Turnip driver. The current driver selection will be deactivated and a fresh Vulkan® collection will start after activation.".format(java.util.Locale.ROOT, slot)) },
                confirmButton = { ExpressiveContainedTextButton("Activate", onClick = onConfirmTurnipActivation) },
                dismissButton = { ExpressiveCancelButton(onClick = onDismissTurnipActivation) }
            )
        }
    }
}

@Composable
internal fun SurfaceProbe(
    modifier: Modifier = Modifier,
    hostGeneration: Long,
    onCreated: (Long, Surface) -> Unit,
    onDestroyed: (Long, Surface) -> Unit
) {
    AndroidView(
        modifier = modifier.alpha(0f),
        factory = { context ->
            SurfaceView(context).apply {
                setBackgroundColor(Color.BLACK)
                if (Build.VERSION.SDK_INT >= 34) {
                    setSurfaceLifecycle(SurfaceView.SURFACE_LIFECYCLE_FOLLOWS_ATTACHMENT)
                }
                holder.addCallback(object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) { onCreated(hostGeneration, holder.surface) }
                    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
                    override fun surfaceDestroyed(holder: SurfaceHolder) { onDestroyed(hostGeneration, holder.surface) }
                })
            }
        },
        update = {}
    )
}



@Composable
internal fun PhysicalDeviceSelector(devices: List<DeviceReport>, selectedIndex: Int, onSelected: (Int) -> Unit) {
    val headerContentInset = LocalAppHeaderContentInset.current
    val pageTopInset = headerContentInset + PageChromeSeparation
    Column(
        Modifier.fillMaxWidth().padding(start = 18.dp, top = pageTopInset + 6.dp, end = 18.dp, bottom = 6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text("Physical device", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
        ExpressiveFilterBar(devices.mapIndexed { index, device -> "GPU ${index + 1} · ${device.name.ifBlank { "Unknown" }.take(48)}" }, selectedIndex, onSelected = onSelected)
    }
}

internal fun Modifier.consumeDesktopSecondaryMouseInput(enabled: Boolean): Modifier {
    if (!enabled) return this
    return pointerInput(enabled) {
        awaitPointerEventScope {
            var secondarySequence = false
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                if (event.changes.none { it.type == PointerType.Mouse }) continue
                if (secondarySequence || event.buttons.isSecondaryPressed) {
                    event.changes.forEach { change -> if (!change.isConsumed) change.consume() }
                    secondarySequence = event.buttons.isSecondaryPressed
                }
            }
        }
    }
}

@Composable
internal fun rememberPointerWheelScalePx(): Float {
    val context = androidx.compose.ui.platform.LocalContext.current
    return remember(context) { android.view.ViewConfiguration.get(context).scaledVerticalScrollFactor.coerceAtLeast(1f) }
}

@Composable
internal fun Modifier.desktopVerticalPointerScroll(state: ScrollableState): Modifier =
    desktopVerticalPointerScroll(state, rememberPointerWheelScalePx())

internal fun Modifier.desktopVerticalPointerScroll(state: ScrollableState, wheelScalePx: Float): Modifier =
    this
        .pointerInput(state, wheelScalePx) {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Main)
                    if (event.type != PointerEventType.Scroll) continue
                    val change = event.changes.firstOrNull { !it.isConsumed } ?: continue
                    val axis = if (change.scrollDelta.y != 0f) change.scrollDelta.y else change.scrollDelta.x
                    if (axis == 0f) continue
                    val consumed = state.dispatchRawDelta(axis * wheelScalePx)
                    if (consumed != 0f) change.consume()
                }
            }
        }
        .pointerInput(state) {
            val slop = viewConfiguration.touchSlop
            awaitPointerEventScope {
                var activeId: androidx.compose.ui.input.pointer.PointerId? = null
                var lastY = 0f
                var accumulatedY = 0f
                var dragging = false
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Main)
                    val mouse = event.changes.firstOrNull { change ->
                        change.type == PointerType.Mouse && (activeId == null || change.id == activeId)
                    }
                    if (mouse == null) {
                        if (!event.buttons.isPrimaryPressed) {
                            activeId = null
                            dragging = false
                            accumulatedY = 0f
                        }
                        continue
                    }
                    if (!event.buttons.isPrimaryPressed || !mouse.pressed) {
                        activeId = null
                        dragging = false
                        accumulatedY = 0f
                        continue
                    }
                    if (activeId == null) {
                        activeId = mouse.id
                        lastY = mouse.position.y
                        accumulatedY = 0f
                        dragging = false
                        continue
                    }
                    val deltaY = mouse.position.y - lastY
                    lastY = mouse.position.y
                    if (deltaY == 0f) continue
                    if (!dragging) {
                        accumulatedY += deltaY
                        if (kotlin.math.abs(accumulatedY) <= slop) continue
                        dragging = true
                        val slopDirection = if (accumulatedY > 0f) slop else -slop
                        val overSlop = accumulatedY - slopDirection
                        if (overSlop != 0f) state.dispatchRawDelta(-overSlop)
                        mouse.consume()
                    } else {
                        state.dispatchRawDelta(-deltaY)
                        mouse.consume()
                    }
                }
            }
        }

@Composable
internal fun Modifier.tvRemoteLazyListNavigation(state: LazyListState): Modifier {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    return onPreviewKeyEvent { event ->
        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
        val direction = when (event.key.nativeKeyCode) {
            AndroidKeyEvent.KEYCODE_DPAD_DOWN -> FocusDirection.Down
            AndroidKeyEvent.KEYCODE_DPAD_UP -> FocusDirection.Up
            else -> null
        }
        if (direction != null) {
            if (focusManager.moveFocus(direction)) return@onPreviewKeyEvent true
            val info = state.layoutInfo
            if (info.totalItemsCount <= 0) return@onPreviewKeyEvent false
            val target = if (direction == FocusDirection.Down) {
                ((info.visibleItemsInfo.lastOrNull()?.index ?: state.firstVisibleItemIndex) + 1).coerceAtMost(info.totalItemsCount - 1)
            } else {
                ((info.visibleItemsInfo.firstOrNull()?.index ?: state.firstVisibleItemIndex) - 1).coerceAtLeast(0)
            }
            val canAdvance = if (direction == FocusDirection.Down) state.canScrollForward else state.canScrollBackward
            if (!canAdvance) return@onPreviewKeyEvent false
            scope.launch {
                state.animateScrollToItem(target)
                delay(24L)
                focusManager.moveFocus(direction)
            }
            true
        } else when (event.key.nativeKeyCode) {
            AndroidKeyEvent.KEYCODE_PAGE_DOWN -> if (!state.canScrollForward) false else {
                val target = (state.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: state.firstVisibleItemIndex).coerceAtMost((state.layoutInfo.totalItemsCount - 1).coerceAtLeast(0))
                scope.launch { state.animateScrollToItem(target) }
                true
            }
            AndroidKeyEvent.KEYCODE_PAGE_UP -> if (!state.canScrollBackward) false else {
                val target = (state.firstVisibleItemIndex - (state.layoutInfo.visibleItemsInfo.size.coerceAtLeast(1) - 1)).coerceAtLeast(0)
                scope.launch { state.animateScrollToItem(target) }
                true
            }
            else -> false
        }
    }
}

@Composable
internal fun Modifier.tvRemoteLazyGridNavigation(state: LazyGridState): Modifier {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    return onPreviewKeyEvent { event ->
        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
        val direction = when (event.key.nativeKeyCode) {
            AndroidKeyEvent.KEYCODE_DPAD_DOWN -> FocusDirection.Down
            AndroidKeyEvent.KEYCODE_DPAD_UP -> FocusDirection.Up
            else -> null
        }
        if (direction != null) {
            if (focusManager.moveFocus(direction)) return@onPreviewKeyEvent true
            val info = state.layoutInfo
            if (info.totalItemsCount <= 0) return@onPreviewKeyEvent false
            val target = if (direction == FocusDirection.Down) {
                ((info.visibleItemsInfo.maxOfOrNull { it.index } ?: state.firstVisibleItemIndex) + 1).coerceAtMost(info.totalItemsCount - 1)
            } else {
                ((info.visibleItemsInfo.minOfOrNull { it.index } ?: state.firstVisibleItemIndex) - 1).coerceAtLeast(0)
            }
            val canAdvance = if (direction == FocusDirection.Down) state.canScrollForward else state.canScrollBackward
            if (!canAdvance) return@onPreviewKeyEvent false
            scope.launch {
                state.animateScrollToItem(target)
                delay(24L)
                focusManager.moveFocus(direction)
            }
            true
        } else when (event.key.nativeKeyCode) {
            AndroidKeyEvent.KEYCODE_PAGE_DOWN -> if (!state.canScrollForward) false else {
                val target = (state.layoutInfo.visibleItemsInfo.maxOfOrNull { it.index } ?: state.firstVisibleItemIndex).coerceAtMost((state.layoutInfo.totalItemsCount - 1).coerceAtLeast(0))
                scope.launch { state.animateScrollToItem(target) }
                true
            }
            AndroidKeyEvent.KEYCODE_PAGE_UP -> if (!state.canScrollBackward) false else {
                val visibleCount = state.layoutInfo.visibleItemsInfo.size.coerceAtLeast(1)
                val target = (state.firstVisibleItemIndex - visibleCount).coerceAtLeast(0)
                scope.launch { state.animateScrollToItem(target) }
                true
            }
            else -> false
        }
    }
}

@Composable
internal fun Modifier.dpadScrollableNavigation(state: ScrollState): Modifier {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val lineStepPx = with(density) { 176.dp.roundToPx() }
    val pageStepPx = with(density) { (configuration.screenHeightDp.dp * 0.68f).roundToPx() }
    return onPreviewKeyEvent { event ->
        if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
        val direction = when (event.key.nativeKeyCode) {
            AndroidKeyEvent.KEYCODE_DPAD_DOWN -> FocusDirection.Down
            AndroidKeyEvent.KEYCODE_DPAD_UP -> FocusDirection.Up
            else -> null
        }
        if (direction != null) {
            if (focusManager.moveFocus(direction)) return@onPreviewKeyEvent true
            val delta = if (direction == FocusDirection.Down) lineStepPx else -lineStepPx
            val target = (state.value + delta).coerceIn(0, state.maxValue)
            if (target == state.value) return@onPreviewKeyEvent false
            scope.launch { state.animateScrollTo(target) }
            true
        } else when (event.key.nativeKeyCode) {
            AndroidKeyEvent.KEYCODE_PAGE_DOWN -> {
                val target = (state.value + pageStepPx).coerceAtMost(state.maxValue)
                if (target == state.value) false else {
                    scope.launch { state.animateScrollTo(target) }
                    true
                }
            }
            AndroidKeyEvent.KEYCODE_PAGE_UP -> {
                val target = (state.value - pageStepPx).coerceAtLeast(0)
                if (target == state.value) false else {
                    scope.launch { state.animateScrollTo(target) }
                    true
                }
            }
            else -> false
        }
    }
}
