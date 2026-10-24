@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.efishell.vulkanscope

import android.content.res.Configuration
import android.view.Surface
import android.content.Context
import java.io.File
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.ui.focus.focusProperties
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.zIndex
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



@Composable
internal fun SettingsPage(
    report: VulkanReport,
    display: DisplayReport,
    mode: DriverMode,
    turnipSupport: TurnipSupport,
    turnipManagerRevision: Int,
    turnipManagerBusy: Boolean,
    storagePermissionDeniedFeedback: Boolean,
    onModeChanged: (DriverMode) -> Unit,
    onInstallDriverBundle: () -> Unit,
    onActivateTurnipDriver: (Int) -> Unit,
    onRemoveTurnipDriver: (Int) -> Unit,
    collectionStatus: CollectionStatus,
    directUpdatesEnabled: Boolean,
    onDirectUpdatesChanged: (Boolean) -> Unit,
    openingAnimationEnabled: Boolean,
    onOpeningAnimationChanged: (Boolean) -> Unit,
    onCheckForUpdates: () -> Unit,
    updateCheckInFlight: Boolean,
    selectedSection: SettingsSection?,
    onSectionSelected: (SettingsSection?) -> Unit
) {
    AnimatedContent(
        targetState = selectedSection,
        modifier = Modifier.fillMaxSize(),
        transitionSpec = {
            when {
                initialState == null && targetState != null ->
                    slideInHorizontally(tween(durationMillis = 280)) { it / 8 } + fadeIn(tween(durationMillis = 220)) togetherWith
                        slideOutHorizontally(tween(durationMillis = 180)) { -it / 12 } + fadeOut(tween(durationMillis = 150))
                initialState != null && targetState == null ->
                    slideInHorizontally(tween(durationMillis = 240)) { -it / 10 } + fadeIn(tween(durationMillis = 200)) togetherWith
                        slideOutHorizontally(tween(durationMillis = 200)) { it / 10 } + fadeOut(tween(durationMillis = 150))
                else ->
                    fadeIn(tween(durationMillis = 200)) togetherWith fadeOut(tween(durationMillis = 150))
            }
        },
        label = "settingsSectionTransition"
    ) { targetSection ->
        when (targetSection) {
            null -> VulkanLazyPage(verticalSpacing = 10.dp) {
                item(key = "settings-destinations-fullwidth") { SettingsSectionCards { onSectionSelected(it) } }
            }
            SettingsSection.INFO -> InfoPage(
                report,
                display,
                mode,
                collectionStatus,
                onCheckForUpdates,
                directUpdatesEnabled,
                updateCheckInFlight,
                showReporting = false
            )
            SettingsSection.REPORTS -> InfoPage(
                report,
                display,
                mode,
                collectionStatus,
                onCheckForUpdates,
                directUpdatesEnabled,
                updateCheckInFlight,
                showInfo = false
            )
            SettingsSection.DRIVER_UPDATES -> DriverUpdatePreferencesPage(
                report = report,
                mode = mode,
                turnipSupport = turnipSupport,
                turnipManagerRevision = turnipManagerRevision,
                turnipManagerBusy = turnipManagerBusy,
                storagePermissionDeniedFeedback = storagePermissionDeniedFeedback,
                onModeChanged = onModeChanged,
                onInstallDriverBundle = onInstallDriverBundle,
                onActivateTurnipDriver = onActivateTurnipDriver,
                onRemoveTurnipDriver = onRemoveTurnipDriver,
                collectionStatus = collectionStatus,
                directUpdatesEnabled = directUpdatesEnabled,
                onDirectUpdatesChanged = onDirectUpdatesChanged,
                openingAnimationEnabled = openingAnimationEnabled,
                onOpeningAnimationChanged = onOpeningAnimationChanged
            )
        }
    }
}

@Composable
internal fun DriverUpdatePreferencesPage(
    report: VulkanReport,
    mode: DriverMode,
    turnipSupport: TurnipSupport,
    turnipManagerRevision: Int,
    turnipManagerBusy: Boolean,
    storagePermissionDeniedFeedback: Boolean,
    onModeChanged: (DriverMode) -> Unit,
    onInstallDriverBundle: () -> Unit,
    onActivateTurnipDriver: (Int) -> Unit,
    onRemoveTurnipDriver: (Int) -> Unit,
    collectionStatus: CollectionStatus,
    directUpdatesEnabled: Boolean,
    onDirectUpdatesChanged: (Boolean) -> Unit,
    openingAnimationEnabled: Boolean,
    onOpeningAnimationChanged: (Boolean) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val completeReportReady = isCompleteReportReady(report, collectionStatus)
    val settingsPrefs = remember(context) { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }
    var drivers by remember { mutableStateOf<List<ManagedTurnipDriver>>(emptyList()) }
    var managerLoading by remember { mutableStateOf(false) }
    var detailsDriver by remember { mutableStateOf<ManagedTurnipDriver?>(null) }
    var removeDriver by remember { mutableStateOf<ManagedTurnipDriver?>(null) }
    var cachedSystemSummary by remember { mutableStateOf(readSystemDriverSummary(settingsPrefs)) }
    var detailsSystemDriver by remember { mutableStateOf(false) }
    val currentSystemSummary = remember(report, mode, completeReportReady) {
        if (mode == DriverMode.SYSTEM && completeReportReady) systemDriverSummaryFromReport(report) else null
    }
    val displayedSystemSummary = currentSystemSummary ?: cachedSystemSummary

    LaunchedEffect(currentSystemSummary) {
        currentSystemSummary?.let { summary ->
            cachedSystemSummary = summary
            withContext(Dispatchers.IO) { persistSystemDriverSummary(settingsPrefs, summary) }
        }
    }

    LaunchedEffect(turnipManagerRevision, turnipSupport, turnipManagerBusy, mode) {
        if (turnipSupport == TurnipSupport.SUPPORTED && !turnipManagerBusy) {
            managerLoading = true
            drivers = withContext(Dispatchers.IO) {
                readManagedTurnipDrivers(context, context.getSharedPreferences("settings", Context.MODE_PRIVATE), mode)
            }
            managerLoading = false
        } else if (turnipSupport != TurnipSupport.SUPPORTED) {
            drivers = emptyList()
            managerLoading = false
        }
    }

    VulkanLazyPage(verticalSpacing = 14.dp) {
        item {
            CapabilitySectionCard("Update preferences") {
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Direct GitHub updates", color = VulkanTextPrimary, fontWeight = FontWeight.SemiBold)
                        Text(if (directUpdatesEnabled) "Enabled · update checks use the official VulkanScope GitHub Releases channel" else "Disabled · recommended when Obtainium manages updates", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                    ExpressiveSwitch(checked = directUpdatesEnabled, onCheckedChange = onDirectUpdatesChanged)
                }
                Text("Direct GitHub updates are enabled by default so new installations receive update checks. When disabled, VulkanScope performs no startup update check and will not download update APKs. Obtainium can track the universal APK from the official GitHub Releases channel without enabling the built-in updater.", color = VulkanTextMuted, style = MaterialTheme.typography.bodySmall)
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                CapabilitySectionCard("Opening animation") {
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("VulkanScope opening animation", color = VulkanTextPrimary, fontWeight = FontWeight.SemiBold)
                            Text(if (openingAnimationEnabled) "Enabled · VulkanScope logo animation plays before startup collection" else "Disabled · app content can start immediately", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                        }
                        ExpressiveSwitch(checked = openingAnimationEnabled, onCheckedChange = onOpeningAnimationChanged)
                    }
                    Text("Applies on the next cold launch. When enabled, Vulkan collection, display inspection, network observation and the automatic update check stay deferred until the opening animation completes.", color = VulkanTextMuted, style = MaterialTheme.typography.bodySmall)
                }
                ChromeVisibilityPreferencesCard()
            }
        }
        item {
            CapabilitySectionCard("Driver manager") {
                Text("System Vulkan® driver is the default source. Turnip packages remain separately managed and become active only after explicit activation and confirmation.", color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.bodySmall)
                SystemDriverManagerRow(
                    active = mode == DriverMode.SYSTEM,
                    enabled = completeReportReady && !turnipManagerBusy,
                    summary = displayedSystemSummary,
                    onDetails = { detailsSystemDriver = true },
                    onActivate = { onModeChanged(DriverMode.SYSTEM) }
                )
                if (collectionStatus == CollectionStatus.COLLECTING) {
                    Text("Driver changes are temporarily locked while VulkanScope is collecting a report. Switching Vulkan® drivers mid-collection would mix evidence from different driver sessions.", color = ComposeColor(0xFFFFC857), style = MaterialTheme.typography.bodySmall)
                }
                HorizontalDivider(color = VulkanOutlineVariant)
                when (turnipSupport) {
                    TurnipSupport.UNSUPPORTED -> {
                        Text("UNAVAILABLE", color = ComposeColor(0xFFFFC857), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        Text("Turnip requires an arm64-v8a device with a detected Qualcomm Adreno Vulkan® implementation. Driver slots and import controls are hidden because this device does not satisfy that eligibility gate.", color = ComposeColor(0xFFFFC857), style = MaterialTheme.typography.bodySmall)
                    }
                    TurnipSupport.UNKNOWN -> {
                        Text("UNAVAILABLE", color = ComposeColor(0xFFFFC857), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        Text("Turnip availability cannot be confirmed until VulkanScope has authoritative platform and Vulkan® device evidence.", color = ComposeColor(0xFFFFC857), style = MaterialTheme.typography.bodySmall)
                    }
                    TurnipSupport.SUPPORTED -> {
                        val occupied = drivers.size
                        ExpressiveMetricGrid(
                            listOf(
                                "Installed" to occupied.toString(),
                                "Maximum" to TURNIP_MANAGER_MAX_DRIVERS.toString(),
                                "Available slots" to (TURNIP_MANAGER_MAX_DRIVERS - occupied).coerceAtLeast(0).toString()
                            )
                        )
                        Text("Turnip ZIP import uses VulkanScope's in-app file manager. It shows folders and pre-validated Turnip ZIP packages only; imported packages are copied into VulkanScope private storage and revalidated before installation.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                        AnimatedContent(targetState = storagePermissionDeniedFeedback, label = "storagePermissionFeedback") { denied ->
                            ExpressiveActionButton(
                                title = if (denied) "Permission denied" else "Import driver ZIP",
                                subtitle = if (denied) "Storage access was not granted" else "Open VulkanScope file manager · ${(TURNIP_MANAGER_MAX_DRIVERS - occupied).coerceAtLeast(0)} slots remaining",
                                icon = if (denied) R.drawable.ic_close else R.drawable.ic_zip_download,
                                enabled = completeReportReady && !managerLoading && !turnipManagerBusy && occupied < TURNIP_MANAGER_MAX_DRIVERS,
                                onClick = onInstallDriverBundle
                            )
                        }
                        if (managerLoading) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                ExpressiveLinearProgressIndicator(Modifier.width(72.dp))
                                Text("Reading private driver slots…", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                            }
                        } else if (drivers.isEmpty()) {
                            EmptyState("No Turnip drivers imported")
                        } else {
                            TurnipDriverManagerTable(
                                drivers = drivers,
                                activeMode = mode,
                                enabled = completeReportReady && !turnipManagerBusy,
                                onDetails = { detailsDriver = it },
                                onActivate = onActivateTurnipDriver,
                                onRemove = { removeDriver = it }
                            )
                        }
                        if (!completeReportReady) Text("Driver import, activation and removal unlock after the complete Vulkan® collection pass finishes.", color = ComposeColor(0xFFFFC857), style = MaterialTheme.typography.bodySmall)
                        Text("Turnip packages must use the validated AdrenoTools schema (meta.json + metadata-declared Vulkan® .so). Up to 10 private driver packages can be retained.", color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

    if (detailsSystemDriver && displayedSystemSummary != null) {
        SystemDriverDetailsDialog(
            summary = displayedSystemSummary,
            evidenceSource = if (currentSystemSummary != null) "Current completed System report" else "Last completed System report retained in private UI metadata",
            active = mode == DriverMode.SYSTEM,
            onDismiss = { detailsSystemDriver = false }
        )
    }
    detailsDriver?.let { driver ->
        TurnipDriverDetailsDialog(driver = driver, onDismiss = { detailsDriver = null })
    }
    removeDriver?.let { driver ->
        AlertDialog(
            modifier = Modifier.border(1.dp, VulkanAccentSoft.copy(alpha = 0.46f), MaterialTheme.shapes.extraLarge),
            onDismissRequest = { if (!turnipManagerBusy) removeDriver = null },
            title = { QuestionDialogTitle("Delete Turnip driver?") },
            text = { Text((if (driver.selected) "This slot is active. Removing it will deactivate Turnip, switch VulkanScope to the System driver, and delete the private package. " else "") + "Slot %02d · %s will be deleted from VulkanScope private storage. This cannot be undone.".format(java.util.Locale.ROOT, driver.slot, driver.info.zipName ?: driver.info.driverName ?: "Imported driver")) },
            confirmButton = {
                ExpressiveContainedIconTextButton("Delete", R.drawable.ic_delete, enabled = !turnipManagerBusy, fontWeight = FontWeight.Bold) {
                    removeDriver = null
                    onRemoveTurnipDriver(driver.slot)
                }
            },
            dismissButton = { ExpressiveCancelButton(enabled = !turnipManagerBusy) { removeDriver = null } }
        )
    }

}


internal data class FileManagerBreadcrumb(val label: String, val path: String)

internal fun fileManagerBreadcrumbs(rootPath: String, directoryPath: String): List<FileManagerBreadcrumb> {
    val root = File(rootPath)
    val current = File(directoryPath)
    val rootText = root.path.trimEnd(File.separatorChar)
    val currentText = current.path.trimEnd(File.separatorChar)
    if (rootText.isBlank() || currentText.isBlank()) return emptyList()
    val relative = if (currentText == rootText) "" else currentText.removePrefix(rootText).trim(File.separatorChar)
    val crumbs = mutableListOf(FileManagerBreadcrumb("Storage", rootText))
    if (relative.isNotBlank()) {
        var path = rootText
        relative.split(File.separatorChar).filter { it.isNotBlank() }.forEach { segment ->
            path += File.separator + segment
            crumbs += FileManagerBreadcrumb(segment, path)
        }
    }
    return crumbs
}

@Composable
internal fun FileManagerBreadcrumbBar(
    rootPath: String,
    directoryPath: String,
    enabled: Boolean,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val crumbs = remember(rootPath, directoryPath) { fileManagerBreadcrumbs(rootPath, directoryPath) }
    val scrollState = rememberScrollState()
    Row(
        modifier = modifier.horizontalScroll(scrollState),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        crumbs.forEachIndexed { index, crumb ->
            if (index > 0) Text(">", color = VulkanTextMuted, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            val active = index == crumbs.lastIndex
            val shape = RoundedCornerShape(12.dp)
            val crumbContainer by animateColorAsState(if (active) VulkanAccentContainer else VulkanSurfaceLow, tween(210), label = "breadcrumbContainer")
            val crumbContent by animateColorAsState(if (active) VulkanAccentSoft else VulkanTextSecondary, tween(210), label = "breadcrumbContent")
            val crumbBorder by animateColorAsState(if (active) VulkanAccentSoft.copy(alpha = 0.46f) else VulkanOutlineVariant, tween(210), label = "breadcrumbBorder")
            Surface(
                shape = shape,
                color = crumbContainer,
                contentColor = crumbContent,
                border = androidx.compose.foundation.BorderStroke(1.dp, crumbBorder),
                modifier = Modifier
                    .animateContentSize(animationSpec = tween(220, easing = FastOutSlowInEasing))
                    .clip(shape)
                    .clickable(enabled = enabled && !active, role = Role.Button) { onNavigate(crumb.path) }
                    .then(tvBrowseModifier(shape))
            ) {
                Text(
                    crumb.label,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                    color = crumbContent,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun TurnipFileManagerDialog(
    state: TurnipFileManagerState,
    onDismiss: () -> Unit,
    onNavigate: (String) -> Unit,
    onUp: () -> Unit,
    onToggleSelection: (String) -> Unit,
    onViewMode: (FileManagerViewMode) -> Unit,
    onSortMode: (FileManagerSortMode) -> Unit,
    onDetails: (TurnipArchiveCandidate) -> Unit,
    onDismissDetails: () -> Unit,
    onImport: () -> Unit
) {
    var search by remember(state.directoryPath) { mutableStateOf("") }
    var searchExpanded by rememberSaveable(state.directoryPath) { mutableStateOf(false) }
    val searchFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    LaunchedEffect(searchExpanded, state.importing) {
        if (searchExpanded && !state.importing) runCatching { searchFocusRequester.requestFocus() }
    }
    val filteredFolders = remember(state.folders, search) {
        if (search.isBlank()) state.folders else state.folders.filter { File(it.path).name.contains(search, true) }
    }
    val filteredCandidates = remember(state.candidates, search) {
        if (search.isBlank()) state.candidates else state.candidates.filter {
            it.name.contains(search, true) || it.driverName?.contains(search, true) == true || it.driverVersion?.contains(search, true) == true
        }
    }
    val atRoot = state.directoryPath == state.rootPath
    val expandedTextLayout = preferExpandedTextLayout()
    val context = androidx.compose.ui.platform.LocalContext.current
    val suppressDesktopSecondaryInput = remember(context) { isChromeOsRuntime(context) || isAndroidPcFormFactor(context) }
    val navigationPadding = WindowInsets.navigationBars.asPaddingValues()
    val layoutDirection = LocalLayoutDirection.current
    val navigationStartInset = navigationPadding.calculateLeftPadding(layoutDirection)
    val navigationEndInset = navigationPadding.calculateRightPadding(layoutDirection)
    val navigationBottomInset = navigationPadding.calculateBottomPadding()
    BackHandler(enabled = !state.importing) { onDismiss() }
    Box(Modifier.fillMaxSize().zIndex(40f)) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = navigationStartInset, end = navigationEndInset, bottom = navigationBottomInset)
                .consumeDesktopSecondaryMouseInput(suppressDesktopSecondaryInput),
            shape = RoundedCornerShape(0.dp),
            color = VulkanBlack,
            contentColor = VulkanTextPrimary,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AnimatedHeaderActionButton(
                        visible = !atRoot,
                        icon = R.drawable.ic_back,
                        contentDescription = "Parent folder",
                        onClick = onUp,
                        enabled = !state.loading && !state.importing,
                        buttonSize = 42.dp,
                        slotSize = 50.dp
                    )
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Turnip file manager", color = VulkanTextPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        FileManagerBreadcrumbBar(
                            rootPath = state.rootPath,
                            directoryPath = state.directoryPath,
                            enabled = !state.loading && !state.importing,
                            onNavigate = onNavigate,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    HeaderActionButton(icon = R.drawable.ic_close, contentDescription = "Close", onClick = onDismiss, enabled = !state.importing)
                }

                Surface(shape = MaterialTheme.shapes.extraLarge, color = VulkanSurfaceTonal, contentColor = VulkanTextPrimary, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.34f))) {
                    Row(
                        Modifier.fillMaxWidth().animateContentSize(animationSpec = tween(240, easing = FastOutSlowInEasing)).padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AnimatedContent(
                            targetState = searchExpanded,
                            transitionSpec = {
                                (fadeIn(tween(180)) + expandHorizontally(tween(240, easing = FastOutSlowInEasing), expandFrom = Alignment.End)) togetherWith
                                    (fadeOut(tween(130)) + shrinkHorizontally(tween(210, easing = FastOutSlowInEasing), shrinkTowards = Alignment.End))
                            },
                            label = "turnipFileManagerSearchResize",
                            modifier = Modifier.weight(1f).animateContentSize(animationSpec = tween(240, easing = FastOutSlowInEasing))
                        ) { expanded ->
                            if (expanded) {
                                ExpressiveSearchField(
                                    value = search,
                                    onValueChange = { search = it.take(120) },
                                    modifier = Modifier.fillMaxWidth().focusRequester(searchFocusRequester),
                                    enabled = !state.importing,
                                    placeholderText = "Folders and validated Turnip packages"
                                )
                            } else {
                                TurnipFileManagerSelectionSummary(state)
                            }
                        }
                        FileManagerOptionsChooser(state.viewMode, state.sortMode, onViewMode, onSortMode, enabled = !state.importing, modifier = Modifier.width(50.dp))
                        AnimatedContent(
                            targetState = searchExpanded,
                            transitionSpec = { fadeIn(tween(160)) + scaleIn(tween(190), initialScale = 0.82f) togetherWith fadeOut(tween(120)) + scaleOut(tween(150), targetScale = 0.82f) },
                            label = "turnipFileManagerSearchAction"
                        ) { expanded ->
                            HeaderActionButton(
                                icon = if (expanded) R.drawable.ic_close else R.drawable.ic_search,
                                contentDescription = if (expanded) "Close search" else "Search files and folders",
                                onClick = {
                                    if (expanded) {
                                        search = ""
                                        focusManager.clearFocus(force = true)
                                        searchExpanded = false
                                    } else searchExpanded = true
                                },
                                enabled = !state.importing,
                                size = if (expanded) 46.dp else 48.dp
                            )
                        }
                    }
                }

                state.status?.let { message ->
                    val warning = message.contains("failed", true) || message.contains("unavailable", true) || message.contains("limit", true)
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        color = if (warning) ComposeColor(0xFF2A2115) else VulkanSurfaceLow,
                        contentColor = if (warning) ComposeColor(0xFFFFC857) else VulkanTextSecondary,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (warning) ComposeColor(0xFF55401E) else VulkanOutlineVariant)
                    ) {
                        Text(message, modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp), color = if (warning) ComposeColor(0xFFFFC857) else VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                }

                AnimatedContent(
                    targetState = state.directoryPath,
                    transitionSpec = {
                        (fadeIn(tween(190)) + slideInHorizontally(tween(230, easing = FastOutSlowInEasing)) { it / 8 }) togetherWith
                            (fadeOut(tween(150)) + slideOutHorizontally(tween(200, easing = FastOutSlowInEasing)) { -it / 8 })
                    },
                    label = "turnipDirectoryTransition",
                    modifier = Modifier.weight(1f).fillMaxWidth().animateContentSize(animationSpec = tween(220, easing = FastOutSlowInEasing))
                ) { _ ->
                    Box(Modifier.fillMaxSize()) {
                    if (state.loading) {
                        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            LoadingIndicator(color = VulkanAccentSoft)
                            Text("Scanning folders and validating Turnip ZIPs…", color = VulkanTextSecondary, textAlign = TextAlign.Center)
                        }                    } else if (state.viewMode in setOf(FileManagerViewMode.GRID, FileManagerViewMode.DENSE_GRID, FileManagerViewMode.LARGE_GRID)) {
                        val gridState = rememberLazyGridState()
                        val denseGrid = state.viewMode == FileManagerViewMode.DENSE_GRID
                        val largeGrid = state.viewMode == FileManagerViewMode.LARGE_GRID
                        val showTopFade by remember(gridState) { derivedStateOf { gridState.canScrollBackward } }
                        val showBottomFade by remember(gridState) { derivedStateOf { gridState.canScrollForward } }
                        val minimumCellWidth = when (state.viewMode) {
                            FileManagerViewMode.DENSE_GRID -> 92.dp
                            FileManagerViewMode.LARGE_GRID -> 228.dp
                            else -> 156.dp
                        }
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minimumCellWidth),
                            state = gridState,
                            modifier = Modifier.fillMaxSize().desktopVerticalPointerScroll(gridState).tvRemoteLazyGridNavigation(gridState).focusGroup(),
                            horizontalArrangement = Arrangement.spacedBy(if (denseGrid) 6.dp else 8.dp),
                            verticalArrangement = Arrangement.spacedBy(if (denseGrid) 6.dp else if (largeGrid) 10.dp else 8.dp),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            gridItems(filteredFolders, key = { "folder:${it.path}" }) { folder ->
                                TurnipFileManagerFolderGridCard(folder = folder, enabled = !state.importing, dense = denseGrid, large = largeGrid) { onNavigate(folder.path) }
                            }
                            gridItems(filteredCandidates, key = { "zip:${it.path}" }) { candidate ->
                                val alreadyImported = turnipImportedSourceKey(candidate.path, candidate.name) in state.importedSourceKeys
                                TurnipFileManagerCandidateGridCard(
                                    candidate = candidate,
                                    selected = !alreadyImported && candidate.path in state.selectedPaths,
                                    alreadyImported = alreadyImported,
                                    enabled = !state.importing && !alreadyImported,
                                    dense = denseGrid,
                                    large = largeGrid,
                                    onSelectedChange = { onToggleSelection(candidate.path) },
                                    onDetails = { onDetails(candidate) }
                                )
                            }
                            if (filteredFolders.isEmpty() && filteredCandidates.isEmpty()) {
                                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                                    EmptyState(if (search.isBlank()) "No folders or validated Turnip ZIPs in this location" else "No matching folders or validated Turnip ZIPs")
                                }
                            }
                        }
                        SoftScrollIntersectionShadows(showTopFade, showBottomFade, Modifier.fillMaxSize(), edgeColor = VulkanBlack)
                        ExpressiveScrollHints(gridState, Modifier.fillMaxSize().padding(horizontal = 6.dp, vertical = 6.dp))
                    } else {
                        val listState = rememberLazyListState()
                        val compact = state.viewMode == FileManagerViewMode.COMPACT
                        val details = state.viewMode == FileManagerViewMode.DETAILS
                        val showTopFade by remember(listState) { derivedStateOf { listState.canScrollBackward } }
                        val showBottomFade by remember(listState) { derivedStateOf { listState.canScrollForward } }
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize().desktopVerticalPointerScroll(listState).tvRemoteLazyListNavigation(listState).focusGroup(),
                            verticalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 9.dp),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            items(filteredFolders, key = { "folder:${it.path}" }) { folder ->
                                TurnipFileManagerFolderRow(folder = folder, compact = compact, details = details, enabled = !state.importing) { onNavigate(folder.path) }
                            }
                            items(filteredCandidates, key = { "zip:${it.path}" }) { candidate ->
                                val alreadyImported = turnipImportedSourceKey(candidate.path, candidate.name) in state.importedSourceKeys
                                TurnipFileManagerCandidateRow(
                                    candidate = candidate,
                                    selected = !alreadyImported && candidate.path in state.selectedPaths,
                                    compact = compact,
                                    details = details,
                                    alreadyImported = alreadyImported,
                                    enabled = !state.importing && !alreadyImported,
                                    onSelectedChange = { onToggleSelection(candidate.path) },
                                    onDetails = { onDetails(candidate) }
                                )
                            }
                            if (filteredFolders.isEmpty() && filteredCandidates.isEmpty()) {
                                item { EmptyState(if (search.isBlank()) "No folders or validated Turnip ZIPs in this location" else "No matching folders or validated Turnip ZIPs") }
                            }
                        }
                        SoftScrollIntersectionShadows(showTopFade, showBottomFade, Modifier.fillMaxSize(), edgeColor = VulkanBlack)
                        ExpressiveScrollHints(listState, Modifier.fillMaxSize().padding(horizontal = 6.dp, vertical = 6.dp))
                    }
                    }
                }

                Surface(shape = MaterialTheme.shapes.extraLarge, color = VulkanSurfaceLow, contentColor = VulkanTextPrimary, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant)) {
                    if (expandedTextLayout) {
                        Column(Modifier.fillMaxWidth().padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Other files are hidden. ZIP validation is repeated during import.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                                if (state.importing) Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    LoadingIndicator(color = VulkanAccentSoft, modifier = Modifier.size(28.dp)); Text("Importing…", color = VulkanTextSecondary, fontWeight = FontWeight.SemiBold)
                                } else ExpressiveContainedIconTextButton("Import ${state.selectedPaths.size}", R.drawable.ic_zip_download, enabled = state.selectedPaths.isNotEmpty() && state.selectedPaths.size <= state.maxSelectable, fontWeight = FontWeight.Bold, onClick = onImport)
                            }
                        }
                    } else {
                        Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Other files are hidden. ZIP validation is repeated during import.", modifier = Modifier.weight(1f), color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                            if (state.importing) Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                LoadingIndicator(color = VulkanAccentSoft, modifier = Modifier.size(28.dp)); Text("Importing…", color = VulkanTextSecondary, fontWeight = FontWeight.SemiBold)
                            } else ExpressiveContainedIconTextButton("Import ${state.selectedPaths.size}", R.drawable.ic_zip_download, enabled = state.selectedPaths.isNotEmpty() && state.selectedPaths.size <= state.maxSelectable, fontWeight = FontWeight.Bold, onClick = onImport)
                        }
                    }
                }
            }
        }
    }
    state.details?.let { TurnipArchiveCandidateDetailsDialog(candidate = it, onDismiss = onDismissDetails) }
}

@Composable
internal fun TurnipFileManagerSelectionSummary(state: TurnipFileManagerState) {
    val selectedCount = state.selectedPaths.size
    val remainingCount = (state.maxSelectable - selectedCount).coerceAtLeast(0)
    Column(Modifier.animateContentSize(animationSpec = tween(220, easing = FastOutSlowInEasing)), verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            Surface(shape = RoundedCornerShape(999.dp), color = VulkanAccentContainer, contentColor = VulkanAccentSoft) {
                AnimatedContent(
                    targetState = selectedCount,
                    transitionSpec = { fadeIn(tween(150)) + slideInVertically(tween(180)) { it / 2 } togetherWith fadeOut(tween(120)) + slideOutVertically(tween(150)) { -it / 2 } },
                    label = "turnipSelectedCount"
                ) { count ->
                    Text(count.toString(), modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                }
            }
            Text("selected", color = VulkanTextPrimary, fontWeight = FontWeight.Bold)
        }
        AnimatedContent(
            targetState = remainingCount,
            transitionSpec = { fadeIn(tween(150)) + slideInVertically(tween(180)) { it / 3 } togetherWith fadeOut(tween(120)) + slideOutVertically(tween(150)) { -it / 3 } },
            label = "turnipRemainingCount"
        ) { remaining ->
            Text("$remaining / ${state.maxSelectable} remaining", color = VulkanTextSecondary, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
internal fun FileManagerOptionsChooser(
    viewMode: FileManagerViewMode,
    sortMode: FileManagerSortMode,
    onViewMode: (FileManagerViewMode) -> Unit,
    onSortMode: (FileManagerSortMode) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier, contentAlignment = Alignment.CenterEnd) {
        Box(Modifier.size(50.dp), contentAlignment = Alignment.Center) {
            HeaderActionButton(
                icon = fileManagerViewModeIcon(viewMode),
                contentDescription = "View and sort files and folders",
                onClick = { if (enabled) expanded = true },
                modifier = Modifier.size(48.dp),
                enabled = enabled,
                size = 48.dp
            )
            Surface(
                shape = CircleShape,
                color = VulkanAccentContainer,
                contentColor = VulkanAccentSoft,
                border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.38f)),
                modifier = Modifier.align(Alignment.BottomEnd).size(20.dp)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(painterResource(fileManagerSortModeIcon(sortMode)), contentDescription = null, modifier = Modifier.size(12.dp))
                }
            }
        }
    }
    if (expanded) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                val menuScrollState = rememberScrollState()
                val menuMaxHeight = (maxHeight - 24.dp).coerceAtLeast(220.dp)
                Surface(
                    color = VulkanSurfaceRaised,
                    contentColor = VulkanTextPrimary,
                    tonalElevation = 8.dp,
                    shadowElevation = 14.dp,
                    shape = RoundedCornerShape(22.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.30f)),
                    modifier = Modifier
                        .widthIn(min = 292.dp, max = 360.dp)
                        .heightIn(max = menuMaxHeight)
                        .focusGroup()
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .desktopVerticalPointerScroll(menuScrollState)
                            .dpadScrollableNavigation(menuScrollState)
                            .verticalScroll(menuScrollState)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            HeaderActionButton(
                                icon = R.drawable.ic_close,
                                contentDescription = "Close view and sort menu",
                                onClick = { expanded = false },
                                enabled = true,
                                size = 36.dp
                            )
                            Text(fileManagerViewModeLabel(viewMode), color = VulkanAccentSoft, style = MaterialTheme.typography.labelMedium)
                        }
                        HorizontalDivider(color = VulkanOutlineVariant)
                        FileManagerViewMode.entries.chunked(3).forEach { rowModes ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                rowModes.forEach { mode ->
                                    val selected = mode == viewMode
                                    val tileShape = RoundedCornerShape(16.dp)
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(tileShape)
                                            .background(if (selected) VulkanAccentContainer else ComposeColor.Transparent)
                                            .clickable(enabled = enabled, role = Role.Button) { onViewMode(mode) }
                                            .padding(horizontal = 6.dp, vertical = 9.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            painterResource(fileManagerViewModeIcon(mode)),
                                            contentDescription = fileManagerViewModeLabel(mode),
                                            tint = if (selected) VulkanAccentSoft else VulkanTextPrimary,
                                            modifier = Modifier.size(27.dp)
                                        )
                                        Text(
                                            fileManagerViewModeLabel(mode),
                                            color = if (selected) VulkanTextPrimary else VulkanTextSecondary,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                            style = MaterialTheme.typography.labelSmall,
                                            textAlign = TextAlign.Center,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Box(Modifier.width(34.dp).height(3.dp).clip(RoundedCornerShape(99.dp)).background(if (selected) VulkanAccentSoft else ComposeColor.Transparent))
                                    }
                                }
                                repeat(3 - rowModes.size) { Spacer(Modifier.weight(1f)) }
                            }
                        }
                        HorizontalDivider(color = VulkanOutlineVariant)
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Sort", color = VulkanTextPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text(fileManagerSortLabel(sortMode), color = VulkanAccentSoft, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        FileManagerSortMode.entries.forEach { candidate ->
                            val selected = candidate == sortMode
                            val rowShape = RoundedCornerShape(14.dp)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(rowShape)
                                    .background(if (selected) VulkanAccentContainer.copy(alpha = 0.72f) else ComposeColor.Transparent)
                                    .clickable(enabled = enabled, role = Role.Button) { onSortMode(candidate) }
                                    .padding(horizontal = 10.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    painterResource(fileManagerSortModeIcon(candidate)),
                                    contentDescription = fileManagerSortLabel(candidate),
                                    tint = if (selected) VulkanAccentSoft else VulkanTextSecondary,
                                    modifier = Modifier.size(19.dp)
                                )
                                Text(fileManagerSortLabel(candidate), color = if (selected) VulkanAccentSoft else VulkanTextPrimary, modifier = Modifier.weight(1f), fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
                                if (selected) {
                                    Icon(painterResource(R.drawable.ic_check), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.size(17.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

internal fun fileManagerViewModeIcon(mode: FileManagerViewMode): Int = when (mode) {
    FileManagerViewMode.LIST -> R.drawable.ic_view_list
    FileManagerViewMode.COMPACT -> R.drawable.ic_view_compact
    FileManagerViewMode.DETAILS -> R.drawable.ic_view_details
    FileManagerViewMode.GRID -> R.drawable.ic_view_grid
    FileManagerViewMode.DENSE_GRID -> R.drawable.ic_view_grid_dense
    FileManagerViewMode.LARGE_GRID -> R.drawable.ic_view_tiles_large
}

internal fun fileManagerViewModeLabel(mode: FileManagerViewMode): String = when (mode) {
    FileManagerViewMode.LIST -> "List"
    FileManagerViewMode.COMPACT -> "Compact list"
    FileManagerViewMode.DETAILS -> "Detailed list"
    FileManagerViewMode.GRID -> "Grid"
    FileManagerViewMode.DENSE_GRID -> "Dense grid"
    FileManagerViewMode.LARGE_GRID -> "Large tiles"
}

internal fun fileManagerSortModeIcon(mode: FileManagerSortMode): Int = when (mode) {
    FileManagerSortMode.NAME_ASC -> R.drawable.ic_sort_name_asc
    FileManagerSortMode.NAME_DESC -> R.drawable.ic_sort_name_desc
    FileManagerSortMode.MODIFIED_NEWEST -> R.drawable.ic_sort_modified_newest
    FileManagerSortMode.MODIFIED_OLDEST -> R.drawable.ic_sort_modified_oldest
    FileManagerSortMode.CREATED_NEWEST -> R.drawable.ic_sort_created_newest
    FileManagerSortMode.CREATED_OLDEST -> R.drawable.ic_sort_created_oldest
}

@Composable
internal fun MesaOfficialLogoBadge(size: Dp, muted: Boolean = false, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = VulkanAccentContainer,
        contentColor = VulkanAccentSoft,
        border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.34f))
    ) {
        Image(
            painter = painterResource(R.drawable.mesa3d_logo),
            contentDescription = "Mesa",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(VulkanAccentSoft),
            modifier = Modifier.padding(6.dp).size(size).alpha(if (muted) 0.42f else 1f)
        )
    }
}

@Composable
internal fun FileManagerNavigateArrow(enabled: Boolean, description: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    val configuration = LocalConfiguration.current
    val isTelevision = configuration.uiMode and Configuration.UI_MODE_TYPE_MASK == Configuration.UI_MODE_TYPE_TELEVISION
    Surface(
        shape = shape,
        color = if (enabled) ComposeColor(0xFF31181B) else VulkanSurfaceLow,
        contentColor = if (enabled) VulkanAccentSoft else VulkanTextMuted,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (enabled) VulkanAccentSoft.copy(alpha = 0.24f) else VulkanOutlineVariant),
        modifier = Modifier.size(46.dp).focusProperties { canFocus = !isTelevision }.clip(shape).clickable(enabled = enabled, role = Role.Button, onClick = onClick)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = description, modifier = Modifier.size(21.dp))
        }
    }
}

internal fun turnipFolderCountLabel(folder: TurnipFolderEntry): String {
    val count = folder.validZipFileCount
    val suffix = if (count == 1) "file" else "files"
    return if (folder.zipCountLimited) "${count}+ $suffix" else "$count $suffix"
}

@Composable
internal fun TurnipFileManagerFolderRow(folder: TurnipFolderEntry, compact: Boolean, details: Boolean, enabled: Boolean, onOpen: () -> Unit) {
    val path = folder.path
    val shape = MaterialTheme.shapes.large
    Surface(shape = shape, color = VulkanSurfaceTonal, contentColor = VulkanTextPrimary, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.38f)), tonalElevation = if (compact) 0.dp else 1.dp, modifier = Modifier.animateContentSize(animationSpec = tween(220, easing = FastOutSlowInEasing)).fillMaxWidth().clip(shape).clickable(enabled = enabled, role = Role.Button, onClick = onOpen).then(tvBrowseModifier(shape))) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = if (compact) 8.dp else 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(shape = RoundedCornerShape(14.dp), color = ComposeColor(0xFF2A2418), contentColor = ComposeColor(0xFFFFC857)) {
                Icon(painterResource(R.drawable.ic_folder), contentDescription = null, modifier = Modifier.padding(if (compact) 7.dp else 8.dp).size(if (compact) 22.dp else 25.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(File(path).name.ifBlank { path }, color = VulkanTextPrimary, fontWeight = FontWeight.SemiBold, maxLines = if (compact) 1 else 2, overflow = TextOverflow.Ellipsis)
                if (!compact) Text(if (details) "${turnipFolderCountLabel(folder)} · $path" else turnipFolderCountLabel(folder), color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall, maxLines = if (details) 2 else 1, overflow = TextOverflow.Ellipsis)
            }
            FileManagerNavigateArrow(enabled = enabled, description = "Open folder") { onOpen() }
        }
    }
}

@Composable
internal fun TurnipFileManagerCandidateRow(candidate: TurnipArchiveCandidate, selected: Boolean, compact: Boolean, details: Boolean, alreadyImported: Boolean, enabled: Boolean, onSelectedChange: () -> Unit, onDetails: () -> Unit) {
    val shape = MaterialTheme.shapes.large
    val rowAlpha = if (alreadyImported) 0.56f else 1f
    Surface(
        modifier = Modifier.animateContentSize(animationSpec = tween(220, easing = FastOutSlowInEasing)).fillMaxWidth().alpha(rowAlpha).clip(shape).clickable(enabled = enabled, role = Role.Checkbox) { onSelectedChange() }.then(tvBrowseModifier(shape)),
        shape = shape,
        color = if (alreadyImported) VulkanSurfaceLow else if (selected) VulkanAccentContainer else VulkanSurfaceTonal,
        contentColor = if (alreadyImported) VulkanTextMuted else VulkanTextPrimary,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (selected && !alreadyImported) VulkanAccentSoft.copy(alpha = 0.58f) else VulkanAccentSoft.copy(alpha = 0.38f)),
        tonalElevation = if (compact) 0.dp else 1.dp
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = if (compact) 7.dp else 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Checkbox(checked = selected, onCheckedChange = { onSelectedChange() }, enabled = enabled, colors = CheckboxDefaults.colors(checkedColor = VulkanAccent, checkmarkColor = VulkanTextPrimary, uncheckedColor = VulkanOutline, disabledCheckedColor = VulkanTextMuted, disabledUncheckedColor = VulkanOutlineVariant))
            MesaOfficialLogoBadge(size = if (compact) 30.dp else 36.dp, muted = alreadyImported)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(candidate.name, color = if (alreadyImported) VulkanTextMuted else ComposeColor(0xFF72DE91), fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(candidate.driverName ?: candidate.libraryName, color = if (alreadyImported) VulkanTextMuted else VulkanTextPrimary, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (alreadyImported) Text("Already imported · same file name and location", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                if (!compact) Text("${candidate.driverVersion ?: candidate.packageVersion ?: "Version not exposed"} · ${formatBytes(candidate.sizeBytes)} · ${formatTimestampOrUnavailable(candidate.modifiedAtMillis.takeIf { it > 0L })}", color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall, maxLines = 3, overflow = TextOverflow.Ellipsis)
                if (details) Text("Schema ${candidate.schemaVersion} · ${candidate.vendor ?: "Vendor not exposed"} · ${candidate.libraryName} (${formatBytes(candidate.librarySizeBytes)})", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall, maxLines = 3, overflow = TextOverflow.Ellipsis)
            }
            IconButton(onClick = onDetails, enabled = enabled, colors = IconButtonDefaults.iconButtonColors(containerColor = if (selected && !alreadyImported) ComposeColor(0xFF4A2023) else VulkanSurfaceLow, contentColor = VulkanAccentSoft, disabledContainerColor = VulkanSurfaceLow, disabledContentColor = VulkanTextMuted)) {
                Icon(painterResource(R.drawable.ic_info), contentDescription = "Turnip package info")
            }
        }
    }
}

@Composable
internal fun TurnipFileManagerFolderGridCard(folder: TurnipFolderEntry, enabled: Boolean, dense: Boolean, large: Boolean, onOpen: () -> Unit) {
    val path = folder.path
    val shape = MaterialTheme.shapes.large
    if (large) {
        Surface(shape = shape, color = VulkanSurfaceTonal, contentColor = VulkanTextPrimary, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.38f)), modifier = Modifier.animateContentSize(animationSpec = tween(220, easing = FastOutSlowInEasing)).fillMaxWidth().clip(shape).clickable(enabled = enabled, role = Role.Button, onClick = onOpen).then(tvBrowseModifier(shape))) {
            Row(
                Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(shape = RoundedCornerShape(18.dp), color = ComposeColor(0xFF2A2418), contentColor = ComposeColor(0xFFFFC857)) {
                    Icon(painterResource(R.drawable.ic_folder), contentDescription = null, modifier = Modifier.padding(10.dp).size(38.dp))
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(File(path).name.ifBlank { path }, color = VulkanTextPrimary, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(turnipFolderCountLabel(folder), color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                }
                FileManagerNavigateArrow(enabled = enabled, description = "Open folder") { onOpen() }
            }
        }
        return
    }
    val padding = if (dense) 6.dp else 12.dp
    val iconSize = if (dense) 18.dp else 30.dp
    val titleStyle = if (dense) MaterialTheme.typography.labelMedium else MaterialTheme.typography.labelLarge
    Surface(shape = shape, color = VulkanSurfaceTonal, contentColor = VulkanTextPrimary, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.38f)), modifier = Modifier.animateContentSize(animationSpec = tween(220, easing = FastOutSlowInEasing)).fillMaxWidth().clip(shape).clickable(enabled = enabled, role = Role.Button, onClick = onOpen).then(tvBrowseModifier(shape))) {
        Column(
            Modifier.fillMaxWidth().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(if (dense) 5.dp else 8.dp)
        ) {
            Surface(shape = RoundedCornerShape(if (dense) 12.dp else 18.dp), color = ComposeColor(0xFF2A2418), contentColor = ComposeColor(0xFFFFC857)) {
                Icon(painterResource(R.drawable.ic_folder), contentDescription = null, modifier = Modifier.padding(if (dense) 6.dp else 10.dp).size(iconSize))
            }
            Text(File(path).name.ifBlank { path }, color = VulkanTextPrimary, fontWeight = FontWeight.SemiBold, style = titleStyle, textAlign = TextAlign.Center, maxLines = if (dense) 1 else 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxWidth())
            Text(turnipFolderCountLabel(folder), color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                FileManagerNavigateArrow(enabled = enabled, description = "Open folder") { onOpen() }
            }
        }
    }
}

@Composable
internal fun TurnipFileManagerCandidateGridCard(candidate: TurnipArchiveCandidate, selected: Boolean, alreadyImported: Boolean, enabled: Boolean, dense: Boolean, large: Boolean, onSelectedChange: () -> Unit, onDetails: () -> Unit) {
    val shape = MaterialTheme.shapes.large
    val surfaceModifier = Modifier.fillMaxWidth().alpha(if (alreadyImported) 0.56f else 1f).clip(shape).clickable(enabled = enabled, role = Role.Checkbox) { onSelectedChange() }.then(tvBrowseModifier(shape))
    if (large) {
        Surface(modifier = surfaceModifier, shape = shape, color = if (alreadyImported) VulkanSurfaceLow else if (selected) VulkanAccentContainer else VulkanSurfaceTonal, contentColor = if (alreadyImported) VulkanTextMuted else VulkanTextPrimary, border = androidx.compose.foundation.BorderStroke(1.dp, if (selected && !alreadyImported) VulkanAccentSoft.copy(alpha = 0.58f) else VulkanAccentSoft.copy(alpha = 0.38f))) {
            Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MesaOfficialLogoBadge(size = 44.dp, muted = alreadyImported)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(candidate.name, color = if (alreadyImported) VulkanTextMuted else ComposeColor(0xFF72DE91), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(candidate.driverVersion ?: candidate.packageVersion ?: "Version not exposed", color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(candidate.driverName ?: candidate.libraryName, color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    if (alreadyImported) Text("Already imported", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Checkbox(checked = selected, onCheckedChange = { onSelectedChange() }, enabled = enabled, colors = CheckboxDefaults.colors(checkedColor = VulkanAccent, checkmarkColor = VulkanTextPrimary, uncheckedColor = VulkanOutline, disabledUncheckedColor = VulkanOutlineVariant))
                    IconButton(onClick = onDetails, enabled = enabled, colors = IconButtonDefaults.iconButtonColors(containerColor = VulkanSurfaceLow, contentColor = VulkanAccentSoft, disabledContainerColor = VulkanSurfaceLow, disabledContentColor = VulkanTextMuted), modifier = Modifier.size(42.dp)) {
                        Icon(painterResource(R.drawable.ic_info), contentDescription = "Turnip package info", modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
        return
    }
    val padding = if (dense) 6.dp else 10.dp
    val titleStyle = if (dense) MaterialTheme.typography.labelMedium else MaterialTheme.typography.labelLarge
    Surface(modifier = surfaceModifier, shape = shape, color = if (alreadyImported) VulkanSurfaceLow else if (selected) VulkanAccentContainer else VulkanSurfaceTonal, contentColor = if (alreadyImported) VulkanTextMuted else VulkanTextPrimary, border = androidx.compose.foundation.BorderStroke(1.dp, if (selected && !alreadyImported) VulkanAccentSoft.copy(alpha = 0.58f) else VulkanAccentSoft.copy(alpha = 0.38f))) {
        Column(Modifier.fillMaxWidth().padding(padding), verticalArrangement = Arrangement.spacedBy(if (dense) 5.dp else 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Checkbox(checked = selected, onCheckedChange = { onSelectedChange() }, enabled = enabled, colors = CheckboxDefaults.colors(checkedColor = VulkanAccent, checkmarkColor = VulkanTextPrimary, uncheckedColor = VulkanOutline, disabledUncheckedColor = VulkanOutlineVariant))
                IconButton(onClick = onDetails, enabled = enabled, colors = IconButtonDefaults.iconButtonColors(containerColor = VulkanSurfaceLow, contentColor = VulkanAccentSoft, disabledContainerColor = VulkanSurfaceLow, disabledContentColor = VulkanTextMuted), modifier = Modifier.size(if (dense) 36.dp else 42.dp)) { Icon(painterResource(R.drawable.ic_info), contentDescription = "Turnip package info", modifier = Modifier.size(if (dense) 17.dp else 20.dp)) }
            }
            MesaOfficialLogoBadge(size = if (dense) 24.dp else 38.dp, muted = alreadyImported)
            Text(candidate.name, color = if (alreadyImported) VulkanTextMuted else ComposeColor(0xFF72DE91), fontWeight = FontWeight.Bold, style = titleStyle, textAlign = TextAlign.Center, maxLines = if (dense) 1 else 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxWidth())
            if (alreadyImported) Text("Already imported", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            if (!dense) Text(candidate.driverVersion ?: candidate.packageVersion ?: "Version not exposed", color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxWidth())
        }
    }
}


@Composable
internal fun TurnipArchiveCandidateDetailsDialog(candidate: TurnipArchiveCandidate, onDismiss: () -> Unit) {
    AlertDialog(
            modifier = Modifier.border(1.dp, VulkanAccentSoft.copy(alpha = 0.46f), MaterialTheme.shapes.extraLarge),
        onDismissRequest = onDismiss,
        containerColor = VulkanSurfaceRaised,
        titleContentColor = VulkanTextPrimary,
        textContentColor = VulkanTextSecondary,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MesaOfficialLogoBadge(size = 38.dp)
                Column(Modifier.weight(1f)) {
                    Text("Turnip package", color = VulkanTextPrimary, fontWeight = FontWeight.Bold)
                    Text(candidate.name, color = ComposeColor(0xFF72DE91), style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
        },
        text = {
            Box(Modifier.fillMaxWidth().heightIn(max = 480.dp)) {
                val detailsScroll = rememberScrollState()
                Column(Modifier.fillMaxWidth().desktopVerticalPointerScroll(detailsScroll).dpadScrollableNavigation(detailsScroll).verticalScroll(detailsScroll).focusable().focusGroup().padding(end = 2.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CapabilityKeyValue("Validation", "Passed pre-import package checks")
                    CapabilityKeyValue("Schema", candidate.schemaVersion.toString())
                    CapabilityKeyValue("Driver name", candidate.driverName ?: "Not exposed")
                    CapabilityKeyValue("Vulkan version", candidate.driverVersion ?: "Not exposed")
                    CapabilityKeyValue("Driver date", candidate.driverDate ?: "Not exposed")
                    CapabilityKeyValue("Package version", candidate.packageVersion ?: "Not exposed")
                    CapabilityKeyValue("Vendor", candidate.vendor ?: "Not exposed")
                    CapabilityKeyValue("Author", candidate.author ?: "Not exposed")
                    CapabilityKeyValue("Minimum API", candidate.minApi?.toString() ?: "Not exposed")
                    CapabilityKeyValue("Vulkan library", candidate.libraryName)
                    CapabilityKeyValue("Library size", formatBytes(candidate.librarySizeBytes))
                    CapabilityKeyValue("ZIP size", formatBytes(candidate.sizeBytes))
                    CapabilityKeyValue("Modified", formatTimestampOrUnavailable(candidate.modifiedAtMillis.takeIf { it > 0L }))
                    candidate.description?.let { CapabilityKeyValue("Description", it) }
                    CapabilityKeyValue("Path", candidate.path)
                }
                ExpressiveScrollHints(detailsScroll, Modifier.fillMaxSize().padding(horizontal = 2.dp, vertical = 2.dp))
            }
        },
        confirmButton = { ExpressiveContainedIconTextButton("Close", R.drawable.ic_close, onClick = onDismiss) }
    )
}

@Composable
internal fun SharedStoragePermissionActionButton(
    title: String,
    subtitle: String,
    icon: Int,
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true,
    compact: Boolean = false,
    onGranted: () -> Unit
) {
    val requestAccess = LocalSharedStorageAccessRequest.current
    val scope = rememberCoroutineScope()
    var denied by remember { mutableStateOf(false) }
    var feedbackGeneration by remember { mutableIntStateOf(0) }
    AnimatedContent(targetState = denied, label = "sharedStoragePermissionFeedback") { permissionDenied ->
        ExpressiveActionButton(
            title = if (permissionDenied) "Permission denied" else title,
            subtitle = if (permissionDenied) "Shared-storage access was not granted" else subtitle,
            icon = if (permissionDenied) R.drawable.ic_close else icon,
            modifier = modifier,
            enabled = enabled,
            compact = compact,
            trailingIcon = if (permissionDenied) R.drawable.ic_close else R.drawable.ic_chevron_right,
            trailingTint = if (permissionDenied) ComposeColor(0xFFFF6B6B) else null
        ) {
            if (!enabled) return@ExpressiveActionButton
            requestAccess(
                {
                    denied = false
                    onGranted()
                },
                {
                    feedbackGeneration += 1
                    val generation = feedbackGeneration
                    denied = true
                    scope.launch {
                        delay(3_000L)
                        if (feedbackGeneration == generation) denied = false
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun SharedStorageBrowserDialog(
    request: SharedStorageBrowserRequest,
    onDismiss: () -> Unit,
    onImport: suspend (File) -> Result<String> = { Result.failure(IllegalStateException("Import is unavailable")) },
    onExport: suspend (File) -> Result<String> = { Result.failure(IllegalStateException("Export is unavailable")) }
) {
    val root = remember { runCatching { sharedStorageRoot() }.getOrNull() }
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    val suppressDesktopSecondaryInput = remember(context) { isChromeOsRuntime(context) || isAndroidPcFormFactor(context) }
    val browserPrefs = remember(context) { context.getSharedPreferences("file_manager_ui", Context.MODE_PRIVATE) }
    var directoryPath by remember(request.title) { mutableStateOf(root?.path.orEmpty()) }
    var listing by remember(request.title) { mutableStateOf(SharedStorageDirectoryListing(emptyList(), emptyList(), false)) }
    var loading by remember(request.title) { mutableStateOf(root != null) }
    var busy by remember(request.title) { mutableStateOf(false) }
    var status by remember(request.title) { mutableStateOf<String?>(if (root == null) "Shared storage is unavailable" else null) }
    var search by remember(request.title, directoryPath) { mutableStateOf("") }
    var viewMode by remember {
        mutableStateOf(runCatching {
            FileManagerViewMode.valueOf(browserPrefs.getString("shared_storage_file_manager_view_mode", FileManagerViewMode.LIST.name) ?: FileManagerViewMode.LIST.name)
        }.getOrDefault(FileManagerViewMode.LIST))
    }
    var sortMode by remember {
        mutableStateOf(runCatching {
            FileManagerSortMode.valueOf(browserPrefs.getString("shared_storage_file_manager_sort_mode", FileManagerSortMode.NAME_ASC.name) ?: FileManagerSortMode.NAME_ASC.name)
        }.getOrDefault(FileManagerSortMode.NAME_ASC))
    }
    val exportExtension = remember(request.mode, request.allowedExtensions) {
        if (request.mode == SharedStorageBrowserMode.EXPORT) request.allowedExtensions.singleOrNull()?.lowercase(java.util.Locale.ROOT) else null
    }
    val suggestedBase = remember(request.title, exportExtension) {
        val suggested = request.suggestedFileName.take(180)
        val suffix = exportExtension?.let { ".$it" }.orEmpty()
        if (suffix.isNotEmpty() && suggested.endsWith(suffix, ignoreCase = true)) suggested.dropLast(suffix.length) else suggested
    }
    var filenameBase by remember(request.title) { mutableStateOf(suggestedBase.take(160)) }
    var pendingOverwrite by remember(request.title) { mutableStateOf<File?>(null) }
    val directory = remember(directoryPath) { directoryPath.takeIf { it.isNotBlank() }?.let(::File) }
    val atRoot = root != null && directoryPath == root.path
    val filteredFolders = remember(listing.folders, search) { if (search.isBlank()) listing.folders else listing.folders.filter { File(it).name.contains(search, true) } }
    val filteredFiles = remember(listing.files, search) { if (search.isBlank()) listing.files else listing.files.filter { it.name.contains(search, true) } }
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    val imeVisible = WindowInsets.ime.getBottom(density) > 0
    val navigationPadding = WindowInsets.navigationBars.asPaddingValues()
    val layoutDirection = LocalLayoutDirection.current
    val navigationStartInset = navigationPadding.calculateLeftPadding(layoutDirection)
    val navigationEndInset = navigationPadding.calculateRightPadding(layoutDirection)
    val navigationBottomInset = navigationPadding.calculateBottomPadding()
    var screenVisible by remember(request.title) { mutableStateOf(false) }
    var closing by remember(request.title) { mutableStateOf(false) }
    var searchExpanded by rememberSaveable(request.title) { mutableStateOf(false) }
    val searchFocusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { screenVisible = true }
    LaunchedEffect(searchExpanded, busy) {
        if (searchExpanded && !busy) runCatching { searchFocusRequester.requestFocus() }
    }
    fun requestClose() {
        if (busy || closing) return
        closing = true
        focusManager.clearFocus(force = true)
        screenVisible = false
        scope.launch {
            delay(220L)
            onDismiss()
        }
    }

    LaunchedEffect(directoryPath, request.mode, request.allowedExtensions, sortMode) {
        val scanRoot = root ?: return@LaunchedEffect
        val target = directory ?: return@LaunchedEffect
        loading = true
        status = null
        listing = SharedStorageDirectoryListing(emptyList(), emptyList(), false)
        try {
            val scanned = withContext(Dispatchers.IO) { scanSharedStorageDirectory(scanRoot, target, request.allowedExtensions, request.mode == SharedStorageBrowserMode.IMPORT, sortMode) }
            listing = scanned
            if (scanned.entryLimitReached) status = "This folder reached the bounded 4096-entry scan limit."
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Throwable) {
            status = error.message ?: "Unable to read this folder"
        } finally {
            loading = false
        }
    }

    fun navigateUp() {
        val scanRoot = root ?: return
        val current = directory ?: return
        if (current.path == scanRoot.path || loading || busy) return
        val parent = runCatching { current.parentFile?.canonicalFile }.getOrNull() ?: return
        if (isCanonicalSharedStoragePath(scanRoot, parent)) directoryPath = parent.path
    }

    fun runExport(destination: File) {
        if (busy) return
        busy = true
        status = "Saving…"
        scope.launch {
            val result = try { onExport(destination) }
            catch (cancelled: CancellationException) { busy = false; throw cancelled }
            catch (error: Throwable) { Result.failure(error) }
            busy = false
            result.onSuccess { status = it; requestClose() }.onFailure { status = it.message ?: "Export failed" }
        }
    }

    fun runImport(entry: SharedStorageFileEntry) {
        if (busy) return
        busy = true
        status = "Validating ${entry.name}…"
        scope.launch {
            val result = try { onImport(File(entry.path)) }
            catch (cancelled: CancellationException) { busy = false; throw cancelled }
            catch (error: Throwable) { Result.failure(error) }
            busy = false
            result.onSuccess { status = it; requestClose() }.onFailure { status = it.message ?: "Import failed" }
        }
    }

    BackHandler(enabled = !busy) {
        if (imeVisible) focusManager.clearFocus(force = true)
        else if (!atRoot) navigateUp()
        else requestClose()
    }

    val headerContent: @Composable () -> Unit = {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AnimatedHeaderActionButton(
                visible = !atRoot,
                icon = R.drawable.ic_back,
                contentDescription = "Parent folder",
                onClick = ::navigateUp,
                enabled = !loading && !busy,
                buttonSize = 42.dp,
                slotSize = 50.dp
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(request.title, color = VulkanTextPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                if (root != null) {
                    FileManagerBreadcrumbBar(
                        rootPath = root.path,
                        directoryPath = directoryPath,
                        enabled = !loading && !busy,
                        onNavigate = { directoryPath = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text("Shared storage unavailable", color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall)
                }
            }
            HeaderActionButton(icon = R.drawable.ic_close, contentDescription = "Close", onClick = ::requestClose, enabled = !busy)
        }
    }

    val descriptionContent: @Composable () -> Unit = {
        Text(request.description, modifier = Modifier.fillMaxWidth(), color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
    }

    val searchContent: @Composable () -> Unit = {
        Row(Modifier.fillMaxWidth().animateContentSize(animationSpec = tween(240, easing = FastOutSlowInEasing)), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AnimatedContent(
                targetState = searchExpanded,
                transitionSpec = {
                    (fadeIn(tween(180)) + expandHorizontally(tween(240, easing = FastOutSlowInEasing), expandFrom = Alignment.End)) togetherWith
                        (fadeOut(tween(130)) + shrinkHorizontally(tween(210, easing = FastOutSlowInEasing), shrinkTowards = Alignment.End))
                },
                label = "sharedFileManagerSearchResize",
                modifier = Modifier.weight(1f).animateContentSize(animationSpec = tween(240, easing = FastOutSlowInEasing))
            ) { expanded ->
                if (expanded) {
                    ExpressiveSearchField(
                        value = search,
                        onValueChange = { search = it.take(120) },
                        modifier = Modifier.fillMaxWidth().focusRequester(searchFocusRequester),
                        enabled = !busy,
                        placeholderText = if (request.mode == SharedStorageBrowserMode.IMPORT) "Folders and supported files" else "Folders"
                    )
                } else {
                    val visibleFileCount = if (request.mode == SharedStorageBrowserMode.IMPORT) filteredFiles.size else 0
                    Text(
                        if (request.mode == SharedStorageBrowserMode.IMPORT) "${filteredFolders.size} folders · $visibleFileCount files" else "${filteredFolders.size} folders",
                        modifier = Modifier.fillMaxWidth(),
                        color = VulkanTextSecondary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            FileManagerOptionsChooser(
                viewMode = viewMode,
                sortMode = sortMode,
                onViewMode = { mode ->
                    viewMode = mode
                    browserPrefs.edit().putString("shared_storage_file_manager_view_mode", mode.name).apply()
                },
                onSortMode = { mode ->
                    sortMode = mode
                    browserPrefs.edit().putString("shared_storage_file_manager_sort_mode", mode.name).apply()
                },
                enabled = !busy,
                modifier = Modifier.width(50.dp)
            )
            AnimatedContent(
                targetState = searchExpanded,
                transitionSpec = { fadeIn(tween(160)) + scaleIn(tween(190), initialScale = 0.82f) togetherWith fadeOut(tween(120)) + scaleOut(tween(150), targetScale = 0.82f) },
                label = "sharedFileManagerSearchAction"
            ) { expanded ->
                HeaderActionButton(
                    icon = if (expanded) R.drawable.ic_close else R.drawable.ic_search,
                    contentDescription = if (expanded) "Close search" else "Search files and folders",
                    onClick = {
                        if (expanded) {
                            search = ""
                            focusManager.clearFocus(force = true)
                            searchExpanded = false
                        } else searchExpanded = true
                    },
                    enabled = !busy,
                    size = if (expanded) 46.dp else 48.dp
                )
            }
        }
    }

    val exportNameContent: @Composable () -> Unit = {
        if (request.mode == SharedStorageBrowserMode.EXPORT) {
            Surface(shape = MaterialTheme.shapes.large, color = VulkanSurfaceTonal, contentColor = VulkanTextPrimary, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant)) {
                Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("File name", color = VulkanTextSecondary, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = filenameBase,
                            onValueChange = { value -> filenameBase = value.take(160).filterNot { it == '/' || it == '\\' || it.code < 0x20 } },
                            modifier = Modifier.weight(1f),
                            enabled = !busy,
                            singleLine = true,
                            label = { Text("Name") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VulkanAccentSoft, unfocusedBorderColor = VulkanOutline, focusedTextColor = VulkanTextPrimary, unfocusedTextColor = VulkanTextPrimary, cursorColor = VulkanAccentSoft)
                        )
                        Surface(shape = RoundedCornerShape(16.dp), color = VulkanAccentContainer, contentColor = VulkanAccentSoft, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.35f))) {
                            Text(exportExtension?.let { ".$it" } ?: "type", modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp), color = VulkanAccentSoft, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                    Text("The file type is fixed for this export; only the name can be changed.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }

    val statusContent: @Composable () -> Unit = {
        status?.let { message ->
            val warning = message.contains("failed", true) || message.contains("invalid", true) || message.contains("unavailable", true) || message.contains("limit", true)
            Surface(shape = MaterialTheme.shapes.medium, color = if (warning) ComposeColor(0xFF2A2115) else VulkanSurfaceTonal, contentColor = if (warning) ComposeColor(0xFFFFC857) else VulkanTextSecondary, border = androidx.compose.foundation.BorderStroke(1.dp, if (warning) ComposeColor(0xFF55401E) else VulkanOutlineVariant)) {
                Text(message, modifier = Modifier.fillMaxWidth().padding(10.dp), color = if (warning) ComposeColor(0xFFFFC857) else VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
            }
        }
    }

    val footerContent: @Composable () -> Unit = {
        Surface(shape = MaterialTheme.shapes.extraLarge, color = VulkanSurfaceLow, contentColor = VulkanTextPrimary, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant)) {
            Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    if (request.mode == SharedStorageBrowserMode.IMPORT) "Only ${request.allowedExtensions.joinToString { ".$it" }} files are shown. Selection is revalidated before import." else "Choose a folder and name. Saving uses an atomic temporary write and asks before replacing an existing file.",
                    modifier = Modifier.weight(1f), color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall
                )
                if (busy) LoadingIndicator(color = VulkanAccentSoft, modifier = Modifier.size(30.dp))
                else if (request.mode == SharedStorageBrowserMode.EXPORT) {
                    ExpressiveContainedIconTextButton("Save", R.drawable.ic_save, enabled = root != null && directory != null && filenameBase.trim().isNotBlank() && exportExtension != null) {
                        val extension = exportExtension ?: return@ExpressiveContainedIconTextButton
                        val fixedName = "${filenameBase.trim()}.$extension"
                        val target = try { validatedSharedStorageDestination(directory ?: return@ExpressiveContainedIconTextButton, fixedName, setOf(extension)) }
                        catch (error: Throwable) { status = error.message ?: "Invalid destination"; return@ExpressiveContainedIconTextButton }
                        if (target.exists()) pendingOverwrite = target else runExport(target)
                    }
                }
            }
        }
    }

    Box(Modifier.fillMaxSize().zIndex(50f)) {
        AnimatedVisibility(
            visible = screenVisible,
            enter = fadeIn(tween(180)) + slideInVertically(tween(220, easing = FastOutSlowInEasing)) { it / 10 },
            exit = fadeOut(tween(160)) + slideOutVertically(tween(210, easing = FastOutSlowInEasing)) { it / 12 }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = navigationStartInset, end = navigationEndInset, bottom = navigationBottomInset)
                    .consumeDesktopSecondaryMouseInput(suppressDesktopSecondaryInput),
                shape = RoundedCornerShape(0.dp),
                color = VulkanBlack,
                contentColor = VulkanTextPrimary,
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                BoxWithConstraints(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp)) {
                    val landscapeLayout = maxWidth > maxHeight && maxWidth >= 700.dp
                    if (landscapeLayout) {
                        val controlsScrollState = rememberScrollState()
                        Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            Column(
                                Modifier
                                    .weight(0.4f)
                                    .fillMaxHeight()
                                    .desktopVerticalPointerScroll(controlsScrollState)
                                    .dpadScrollableNavigation(controlsScrollState)
                                    .verticalScroll(controlsScrollState)
                                    .focusable()
                                    .focusGroup(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                headerContent()
                                descriptionContent()
                                searchContent()
                                exportNameContent()
                                statusContent()
                                footerContent()
                            }
                            Box(Modifier.weight(0.6f).fillMaxHeight()) {
                                SharedStorageBrowserListing(
                                    loading = loading,
                                    viewMode = viewMode,
                                    filteredFolders = filteredFolders,
                                    filteredFiles = filteredFiles,
                                    mode = request.mode,
                                    busy = busy,
                                    navigationKey = directoryPath,
                                    onNavigate = { directoryPath = it },
                                    onImport = ::runImport,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    } else {
                        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            headerContent()
                            descriptionContent()
                            searchContent()
                            exportNameContent()
                            statusContent()
                            SharedStorageBrowserListing(
                                loading = loading,
                                viewMode = viewMode,
                                filteredFolders = filteredFolders,
                                filteredFiles = filteredFiles,
                                mode = request.mode,
                                busy = busy,
                                navigationKey = directoryPath,
                                onNavigate = { directoryPath = it },
                                onImport = ::runImport,
                                modifier = Modifier.weight(1f).fillMaxWidth()
                            )
                            footerContent()
                        }
                    }
                }
            }
        }
    }

    pendingOverwrite?.let { target ->
        AlertDialog(
            modifier = Modifier.border(1.dp, VulkanAccentSoft.copy(alpha = 0.46f), MaterialTheme.shapes.extraLarge),
            onDismissRequest = { if (!busy) pendingOverwrite = null },
            containerColor = VulkanSurfaceRaised,
            titleContentColor = VulkanTextPrimary,
            textContentColor = VulkanTextSecondary,
            title = { QuestionDialogTitle("Overwrite existing file?") },
            text = { Text("${target.name} already exists in this folder. Replace it with the new VulkanScope export?") },
            confirmButton = { ExpressiveContainedIconTextButton("Replace", R.drawable.ic_save, enabled = !busy) { pendingOverwrite = null; runExport(target) } },
            dismissButton = { ExpressiveCancelButton(enabled = !busy) { pendingOverwrite = null } }
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun SharedStorageBrowserListing(
    loading: Boolean,
    viewMode: FileManagerViewMode,
    filteredFolders: List<String>,
    filteredFiles: List<SharedStorageFileEntry>,
    mode: SharedStorageBrowserMode,
    busy: Boolean,
    navigationKey: String,
    onNavigate: (String) -> Unit,
    onImport: (SharedStorageFileEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = navigationKey,
        transitionSpec = {
            (fadeIn(tween(190)) + slideInHorizontally(tween(230, easing = FastOutSlowInEasing)) { it / 8 }) togetherWith
                (fadeOut(tween(150)) + slideOutHorizontally(tween(200, easing = FastOutSlowInEasing)) { -it / 8 })
        },
        label = "sharedStorageDirectoryTransition",
        modifier = modifier.animateContentSize(animationSpec = tween(220, easing = FastOutSlowInEasing))
    ) { _ ->
        Box(Modifier.fillMaxSize()) {
        if (loading) {
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LoadingIndicator(color = VulkanAccentSoft)
                Text("Reading shared storage…", color = VulkanTextSecondary)
            }
        } else if (viewMode in setOf(FileManagerViewMode.GRID, FileManagerViewMode.DENSE_GRID, FileManagerViewMode.LARGE_GRID)) {
            val gridState = rememberLazyGridState()
            val denseGrid = viewMode == FileManagerViewMode.DENSE_GRID
            val largeGrid = viewMode == FileManagerViewMode.LARGE_GRID
            val showTopFade by remember(gridState) { derivedStateOf { gridState.canScrollBackward } }
            val showBottomFade by remember(gridState) { derivedStateOf { gridState.canScrollForward } }
            val minimumCellWidth = when (viewMode) {
                FileManagerViewMode.DENSE_GRID -> 92.dp
                FileManagerViewMode.LARGE_GRID -> 228.dp
                else -> 156.dp
            }
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minimumCellWidth),
                state = gridState,
                modifier = Modifier.fillMaxSize().desktopVerticalPointerScroll(gridState).tvRemoteLazyGridNavigation(gridState).focusGroup(),
                horizontalArrangement = Arrangement.spacedBy(if (denseGrid) 6.dp else 8.dp),
                verticalArrangement = Arrangement.spacedBy(if (denseGrid) 6.dp else if (largeGrid) 10.dp else 8.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                gridItems(filteredFolders, key = { "shared-folder:$it" }) { path ->
                    SharedStorageFolderGridCard(path = path, enabled = !busy, dense = denseGrid, large = largeGrid) { onNavigate(path) }
                }
                if (mode == SharedStorageBrowserMode.IMPORT) {
                    gridItems(filteredFiles, key = { "shared-file:${it.path}" }) { entry ->
                        SharedStorageFileGridCard(entry = entry, enabled = !busy, dense = denseGrid, large = largeGrid) { onImport(entry) }
                    }
                }
                if (filteredFolders.isEmpty() && (mode == SharedStorageBrowserMode.EXPORT || filteredFiles.isEmpty())) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) { EmptyState("No matching folders or files in this location") }
                }
            }
            SoftScrollIntersectionShadows(showTopFade, showBottomFade, Modifier.fillMaxSize(), edgeColor = VulkanBlack)
            ExpressiveScrollHints(gridState, Modifier.fillMaxSize().padding(horizontal = 6.dp, vertical = 6.dp))
        } else {
            val listState = rememberLazyListState()
            val compact = viewMode == FileManagerViewMode.COMPACT
            val details = viewMode == FileManagerViewMode.DETAILS
            val showTopFade by remember(listState) { derivedStateOf { listState.canScrollBackward } }
            val showBottomFade by remember(listState) { derivedStateOf { listState.canScrollForward } }
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().desktopVerticalPointerScroll(listState).tvRemoteLazyListNavigation(listState).focusGroup(),
                verticalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 8.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(filteredFolders, key = { "shared-folder:$it" }) { path ->
                    SharedStorageFolderRow(path = path, compact = compact, details = details, enabled = !busy) { onNavigate(path) }
                }
                if (mode == SharedStorageBrowserMode.IMPORT) {
                    items(filteredFiles, key = { "shared-file:${it.path}" }) { entry ->
                        SharedStorageFileRow(entry = entry, compact = compact, details = details, enabled = !busy) { onImport(entry) }
                    }
                }
                if (filteredFolders.isEmpty() && (mode == SharedStorageBrowserMode.EXPORT || filteredFiles.isEmpty())) item { EmptyState("No matching folders or files in this location") }
            }
            SoftScrollIntersectionShadows(showTopFade, showBottomFade, Modifier.fillMaxSize(), edgeColor = VulkanBlack)
            ExpressiveScrollHints(listState, Modifier.fillMaxSize().padding(horizontal = 6.dp, vertical = 6.dp))
        }
        }
    }
}

@Composable
internal fun SharedStorageFolderRow(path: String, compact: Boolean, details: Boolean, enabled: Boolean, onOpen: () -> Unit) {
    val shape = MaterialTheme.shapes.large
    Surface(
        modifier = Modifier.animateContentSize(animationSpec = tween(220, easing = FastOutSlowInEasing)).fillMaxWidth().clip(shape).clickable(enabled = enabled, role = Role.Button, onClick = onOpen).then(tvBrowseModifier(shape)),
        shape = shape,
        color = VulkanSurfaceTonal,
        contentColor = VulkanTextPrimary,
        border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.38f)),
        tonalElevation = if (compact) 0.dp else 1.dp
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = if (compact) 8.dp else 11.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(shape = RoundedCornerShape(14.dp), color = ComposeColor(0xFF2A2418), contentColor = ComposeColor(0xFFFFC857)) {
                Icon(painterResource(R.drawable.ic_folder), contentDescription = null, modifier = Modifier.padding(if (compact) 7.dp else 8.dp).size(if (compact) 22.dp else 24.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(File(path).name.ifBlank { path }, color = VulkanTextPrimary, fontWeight = FontWeight.SemiBold, maxLines = if (compact) 1 else 2, overflow = TextOverflow.Ellipsis)
                if (!compact) Text(if (details) path else "Folder", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall, maxLines = if (details) 2 else 1, overflow = TextOverflow.Ellipsis)
            }
            FileManagerNavigateArrow(enabled = enabled, description = "Open folder", onClick = onOpen)
        }
    }
}

internal fun sharedStorageFileIcon(entry: SharedStorageFileEntry): Int = when (entry.name.substringAfterLast('.', "").lowercase(java.util.Locale.ROOT)) {
    "html", "htm" -> R.drawable.ic_action_html
    "txt" -> R.drawable.ic_action_text
    else -> R.drawable.ic_export
}

@Composable
internal fun SharedStorageFileRow(entry: SharedStorageFileEntry, compact: Boolean, details: Boolean, enabled: Boolean, onSelect: () -> Unit) {
    val shape = MaterialTheme.shapes.large
    Surface(
        modifier = Modifier.animateContentSize(animationSpec = tween(220, easing = FastOutSlowInEasing)).fillMaxWidth().clip(shape).clickable(enabled = enabled, role = Role.Button, onClick = onSelect).then(tvBrowseModifier(shape)),
        shape = shape,
        color = VulkanSurfaceTonal,
        contentColor = VulkanTextPrimary,
        border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.38f)),
        tonalElevation = if (compact) 0.dp else 1.dp
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = if (compact) 8.dp else 11.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(shape = RoundedCornerShape(14.dp), color = VulkanAccentContainer, contentColor = VulkanAccentSoft) {
                Icon(painterResource(sharedStorageFileIcon(entry)), contentDescription = null, modifier = Modifier.padding(if (compact) 7.dp else 8.dp).size(if (compact) 22.dp else 24.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(entry.name, color = VulkanTextPrimary, fontWeight = FontWeight.SemiBold, maxLines = if (compact) 1 else 2, overflow = TextOverflow.Ellipsis)
                if (!compact) Text("${formatBytes(entry.sizeBytes)} · ${formatTimestampOrUnavailable(entry.modifiedAtMillis.takeIf { it > 0L })}", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (details) Text("Created ${formatTimestampOrUnavailable(entry.createdAtMillis.takeIf { it > 0L })} · ${entry.path}", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall, maxLines = 3, overflow = TextOverflow.Ellipsis)
            }
            FileManagerNavigateArrow(enabled = enabled, description = "Select ${entry.name}", onClick = onSelect)
        }
    }
}

@Composable
internal fun SharedStorageFolderGridCard(path: String, enabled: Boolean, dense: Boolean, large: Boolean, onOpen: () -> Unit) {
    val shape = MaterialTheme.shapes.large
    if (large) {
        Surface(shape = shape, color = VulkanSurfaceTonal, contentColor = VulkanTextPrimary, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.38f)), modifier = Modifier.animateContentSize(animationSpec = tween(220, easing = FastOutSlowInEasing)).fillMaxWidth().clip(shape).clickable(enabled = enabled, role = Role.Button, onClick = onOpen).then(tvBrowseModifier(shape))) {
            Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(shape = RoundedCornerShape(18.dp), color = ComposeColor(0xFF2A2418), contentColor = ComposeColor(0xFFFFC857)) {
                    Icon(painterResource(R.drawable.ic_folder), contentDescription = null, modifier = Modifier.padding(10.dp).size(38.dp))
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(File(path).name.ifBlank { path }, color = VulkanTextPrimary, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text("Folder", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                }
                FileManagerNavigateArrow(enabled = enabled, description = "Open folder", onClick = onOpen)
            }
        }
    } else {
        Surface(shape = shape, color = VulkanSurfaceTonal, contentColor = VulkanTextPrimary, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.38f)), modifier = Modifier.animateContentSize(animationSpec = tween(220, easing = FastOutSlowInEasing)).fillMaxWidth().clip(shape).clickable(enabled = enabled, role = Role.Button, onClick = onOpen).then(tvBrowseModifier(shape))) {
            Column(Modifier.fillMaxWidth().padding(if (dense) 8.dp else 11.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(if (dense) 5.dp else 8.dp)) {
                Surface(shape = RoundedCornerShape(if (dense) 12.dp else 16.dp), color = ComposeColor(0xFF2A2418), contentColor = ComposeColor(0xFFFFC857)) {
                    Icon(painterResource(R.drawable.ic_folder), contentDescription = null, modifier = Modifier.padding(if (dense) 7.dp else 9.dp).size(if (dense) 25.dp else 32.dp))
                }
                Text(File(path).name.ifBlank { path }, color = VulkanTextPrimary, fontWeight = FontWeight.SemiBold, style = if (dense) MaterialTheme.typography.labelMedium else MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxWidth())
                FileManagerNavigateArrow(enabled = enabled, description = "Open folder", onClick = onOpen)
            }
        }
    }
}

@Composable
internal fun SharedStorageFileGridCard(entry: SharedStorageFileEntry, enabled: Boolean, dense: Boolean, large: Boolean, onSelect: () -> Unit) {
    val shape = MaterialTheme.shapes.large
    if (large) {
        Surface(shape = shape, color = VulkanSurfaceTonal, contentColor = VulkanTextPrimary, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.38f)), modifier = Modifier.animateContentSize(animationSpec = tween(220, easing = FastOutSlowInEasing)).fillMaxWidth().clip(shape).clickable(enabled = enabled, role = Role.Button, onClick = onSelect).then(tvBrowseModifier(shape))) {
            Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(shape = RoundedCornerShape(18.dp), color = VulkanAccentContainer, contentColor = VulkanAccentSoft) {
                    Icon(painterResource(sharedStorageFileIcon(entry)), contentDescription = null, modifier = Modifier.padding(10.dp).size(36.dp))
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(entry.name, color = VulkanTextPrimary, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text("${formatBytes(entry.sizeBytes)} · ${formatTimestampOrUnavailable(entry.modifiedAtMillis.takeIf { it > 0L })}", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                FileManagerNavigateArrow(enabled = enabled, description = "Select ${entry.name}", onClick = onSelect)
            }
        }
    } else {
        Surface(shape = shape, color = VulkanSurfaceTonal, contentColor = VulkanTextPrimary, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.38f)), modifier = Modifier.animateContentSize(animationSpec = tween(220, easing = FastOutSlowInEasing)).fillMaxWidth().clip(shape).clickable(enabled = enabled, role = Role.Button, onClick = onSelect).then(tvBrowseModifier(shape))) {
            Column(Modifier.fillMaxWidth().padding(if (dense) 8.dp else 11.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(if (dense) 5.dp else 8.dp)) {
                Surface(shape = RoundedCornerShape(if (dense) 12.dp else 16.dp), color = VulkanAccentContainer, contentColor = VulkanAccentSoft) {
                    Icon(painterResource(sharedStorageFileIcon(entry)), contentDescription = null, modifier = Modifier.padding(if (dense) 7.dp else 9.dp).size(if (dense) 25.dp else 32.dp))
                }
                Text(entry.name, color = VulkanTextPrimary, fontWeight = FontWeight.SemiBold, style = if (dense) MaterialTheme.typography.labelMedium else MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.fillMaxWidth())
                if (!dense) Text(formatBytes(entry.sizeBytes), color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                FileManagerNavigateArrow(enabled = enabled, description = "Select ${entry.name}", onClick = onSelect)
            }
        }
    }
}


internal fun vendorIdFromDisplay(value: String?): Long? {
    val text = value?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    return if (text.startsWith("0x", ignoreCase = true)) text.substring(2).toLongOrNull(16) else text.toLongOrNull()
}

@Composable
internal fun SystemDriverVendorBadge(vendorId: String?) {
    val info = vendorInfo(vendorIdFromDisplay(vendorId) ?: -1L)
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = VulkanAccentContainer,
        contentColor = VulkanAccentSoft,
        border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.28f)),
        modifier = Modifier.size(50.dp)
    ) {
        Image(
            painter = painterResource(info.logo),
            contentDescription = "${info.name} GPU vendor",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize().padding(7.dp)
        )
    }
}

@Composable
internal fun SystemDriverManagerRow(
    active: Boolean,
    enabled: Boolean,
    summary: SystemDriverSummary?,
    onDetails: () -> Unit,
    onActivate: () -> Unit
) {
    val shape = MaterialTheme.shapes.extraLarge
    Surface(
        modifier = Modifier.fillMaxWidth().then(tvBrowseModifier(shape)),
        shape = shape,
        color = if (active) ComposeColor(0xFF21191A) else VulkanSurfaceRaised,
        contentColor = VulkanTextPrimary,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (active) VulkanAccentSoft.copy(alpha = 0.46f) else VulkanOutlineVariant),
        tonalElevation = if (active) 2.dp else 1.dp
    ) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SystemDriverVendorBadge(summary?.vendorId)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("System Vulkan® driver", color = VulkanTextPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text("Android platform Vulkan® source", color = VulkanTextSecondary, style = MaterialTheme.typography.labelMedium)
                }
                if (active) TurnipStatePill("ACTIVE", true, true)
                else ExpressiveContainedTextButton("Activate", enabled = enabled, onClick = onActivate)
            }
            if (summary != null) {
                ExpressiveInfoPill("GPU", summary.deviceName, Modifier.fillMaxWidth())
                ExpressiveInfoPill("Driver", summary.driverName ?: summary.driverInfo ?: summary.driverVersion, Modifier.fillMaxWidth())
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExpressiveInfoPill("Version", summary.driverVersion, Modifier.weight(1f))
                    ExpressiveInfoPill("Source", "System", Modifier.weight(1f))
                }
                HorizontalDivider(color = VulkanOutlineVariant)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    DetailAffordance(onDetails)
                }
            } else {
                Surface(shape = MaterialTheme.shapes.medium, color = VulkanSurfaceLow, contentColor = VulkanTextSecondary) {
                    Text(
                        "Detailed System driver evidence becomes available after a completed System-driver collection. Turnip evidence is never reused as System evidence.",
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        color = VulkanTextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
internal fun SystemDriverDetailsDialog(summary: SystemDriverSummary, evidenceSource: String, active: Boolean, onDismiss: () -> Unit) {
    ScrollableDetailDialog(title = "System Vulkan driver", onDismiss = onDismiss) {
        CapabilityKeyValue("State", if (active) "Active" else "Inactive · retained System evidence")
        CapabilityKeyValue("Evidence source", evidenceSource)
        CapabilityKeyValue("Captured", formatTimestampOrUnavailable(summary.capturedAtMillis))
        CapabilityKeyValue("GPU", summary.deviceName)
        CapabilityKeyValue("Vulkan® API", summary.apiVersion)
        CapabilityKeyValue("Driver version", summary.driverVersion)
        CapabilityKeyValue("Driver name", summary.driverName ?: "Not exposed by the completed System report")
        CapabilityKeyValue("Driver info", summary.driverInfo ?: "Not exposed by the completed System report")
        CapabilityKeyValue("Driver ID", summary.driverId ?: "Not exposed by the completed System report")
        CapabilityKeyValue("Conformance version", summary.conformanceVersion ?: "Not exposed by the completed System report")
        CapabilityKeyValue("Loader version", summary.loaderVersion)
        CapabilityKeyValue("Instance API version", summary.instanceApiVersion)
        CapabilityKeyValue("Vendor ID", summary.vendorId)
        CapabilityKeyValue("Device ID", summary.deviceId)
        CapabilityKeyValue("Device type", summary.deviceType)
        Text("The retained System summary is private UI metadata only and is not added to technicalReport, TXT/HTML exports or VulkanScope Database submissions.", color = VulkanTextMuted, style = MaterialTheme.typography.bodySmall)
    }
}
@Composable
internal fun TurnipDriverManagerTable(
    drivers: List<ManagedTurnipDriver>,
    activeMode: DriverMode,
    enabled: Boolean,
    onDetails: (ManagedTurnipDriver) -> Unit,
    onActivate: (Int) -> Unit,
    onRemove: (ManagedTurnipDriver) -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val wide = maxWidth >= 620.dp && !preferExpandedTextLayout()
        val useTwoColumnPills = maxWidth >= 390.dp && !preferExpandedTextLayout()
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            drivers.forEach { rawDriver ->
                val driver = if (activeMode == DriverMode.TURNIP) rawDriver else rawDriver.copy(selected = false)
                val info = driver.info
                val unavailable = turnipDriverStateLabel(driver) == "UNAVAILABLE"
                val cardShape = MaterialTheme.shapes.extraLarge
                Surface(
                    modifier = Modifier.fillMaxWidth().alpha(if (unavailable) 0.68f else 1f).then(tvBrowseModifier(cardShape)),
                    shape = cardShape,
                    color = if (driver.selected) ComposeColor(0xFF21191A) else VulkanSurfaceRaised,
                    contentColor = VulkanTextPrimary,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (driver.selected) VulkanAccentSoft.copy(alpha = 0.46f) else VulkanOutlineVariant),
                    tonalElevation = if (driver.selected) 2.dp else 1.dp
                ) {
                    if (wide) {
                        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            MesaOfficialLogoBadge(size = 30.dp, muted = unavailable)
                            Surface(shape = RoundedCornerShape(14.dp), color = if (driver.selected) VulkanAccentContainer else VulkanSurfaceLow, contentColor = if (driver.selected) VulkanAccentSoft else VulkanTextSecondary) {
                                Text("%02d".format(java.util.Locale.ROOT, driver.slot), modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                            }
                            Column(Modifier.weight(1.25f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(info.driverName ?: if (info.installed) "Turnip package" else "Invalid package", color = VulkanTextPrimary, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Text(info.driverVersion ?: "Version not provided", color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall)
                            }
                            Column(Modifier.weight(1.35f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(info.zipName ?: "Source name unavailable", color = VulkanTextPrimary, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Text(info.zipSizeBytes?.let { android.text.format.Formatter.formatShortFileSize(androidx.compose.ui.platform.LocalContext.current, it) } ?: "Size unavailable", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                            }
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp), horizontalAlignment = Alignment.End) {
                                TurnipStatePill(turnipDriverStateLabel(driver), driver.selected, driver.sourceAvailable && info.installed)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    if (!unavailable) DetailAffordance { onDetails(driver) }
                                    if (!unavailable && !driver.selected) ExpressiveContainedTextButton("Activate", enabled = enabled && info.installed) { onActivate(driver.slot) }
                                    ExpressiveContainedIconTextButton("Remove", R.drawable.ic_delete, enabled = enabled) { onRemove(driver) }
                                }
                            }
                            }
                            HorizontalDivider(color = VulkanOutlineVariant)
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ExpressiveInfoPill("Vulkan® library", info.libraryName ?: "Not available", Modifier.weight(1f))
                                ExpressiveInfoPill("Description", info.description?.takeIf { it.isNotBlank() } ?: "Not provided", Modifier.weight(1f))
                            }
                        }
                    } else {
                        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                MesaOfficialLogoBadge(size = 34.dp, muted = unavailable)
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        info.driverName ?: if (info.installed) "Turnip package" else "Invalid package",
                                        color = VulkanTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        textDecoration = if (unavailable) TextDecoration.LineThrough else TextDecoration.None
                                    )
                                    Text(
                                        "Slot %02d · %s".format(java.util.Locale.ROOT, driver.slot, info.driverVersion ?: "version not provided"),
                                        color = VulkanTextSecondary,
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                TurnipStatePill(turnipDriverStateLabel(driver), driver.selected, driver.sourceAvailable && info.installed)
                            }
                            ExpressiveInfoPill("Source ZIP", info.zipName ?: "Source name unavailable", Modifier.fillMaxWidth())
                            ExpressiveInfoPill("Vulkan® library", info.libraryName ?: "Not available", Modifier.fillMaxWidth())
                            ExpressiveInfoPill("Description", info.description?.takeIf { it.isNotBlank() } ?: "Not provided", Modifier.fillMaxWidth())
                            if (useTwoColumnPills) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    ExpressiveInfoPill("Driver name", info.driverName ?: "Not provided", Modifier.weight(1f))
                                    ExpressiveInfoPill("Package", info.packageVersion ?: "Not provided", Modifier.weight(1f))
                                }
                            } else {
                                ExpressiveInfoPill("Driver name", info.driverName ?: "Not provided", Modifier.fillMaxWidth())
                                ExpressiveInfoPill("Package", info.packageVersion ?: "Not provided", Modifier.fillMaxWidth())
                            }
                            HorizontalDivider(color = VulkanOutlineVariant)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (!unavailable) DetailAffordance { onDetails(driver) }
                                if (!unavailable && !driver.selected) ExpressiveContainedTextButton("Activate", enabled = enabled && info.installed) { onActivate(driver.slot) }
                                ExpressiveContainedIconTextButton("Remove", R.drawable.ic_delete, enabled = enabled) { onRemove(driver) }
                            }
                        }
                    }
                }
            }
        }
    }
}

internal fun turnipDriverStateLabel(driver: ManagedTurnipDriver): String = when {
    driver.selected -> "ACTIVE"
    driver.info.installed && driver.sourceAvailable -> "AVAILABLE"
    else -> "UNAVAILABLE"
}
@Composable
internal fun TurnipStatePill(label: String, active: Boolean, valid: Boolean) {
    val container = when {
        active -> ComposeColor(0xFF173421)
        valid -> ComposeColor(0xFF16344F)
        else -> ComposeColor(0xFF332A16)
    }
    val content = when {
        active -> ComposeColor(0xFF73C991)
        valid -> ComposeColor(0xFF9CCBFF)
        else -> ComposeColor(0xFFFFC857)
    }
    Surface(shape = RoundedCornerShape(999.dp), color = container) {
        Text(label, color = content, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
    }
}

@Composable
internal fun TurnipDriverDetailsDialog(driver: ManagedTurnipDriver, onDismiss: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val info = driver.info
    ScrollableDetailDialog(title = "Turnip slot %02d".format(java.util.Locale.ROOT, driver.slot), onDismiss = onDismiss) {
        CapabilityKeyValue("State", turnipDriverStateLabel(driver).lowercase().replaceFirstChar { it.uppercase() })
        CapabilityKeyValue("Source availability", if (driver.sourceAvailable) "Available" else "Unavailable")
        Text("Source ZIP", color = VulkanTextPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        CapabilityKeyValue("ZIP name", info.zipName ?: "Not available")
        CapabilityKeyValue("Source location", info.zipLocation ?: "Not available")
        CapabilityKeyValue("ZIP size", info.zipSizeBytes?.let { android.text.format.Formatter.formatShortFileSize(context, it) } ?: "Not available")
        CapabilityKeyValue("ZIP modified", formatTimestampOrUnavailable(info.zipModifiedAtMillis))
        CapabilityKeyValue("Imported", formatTimestampOrUnavailable(info.importedAtMillis))
        Text("Driver package", color = VulkanTextPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        CapabilityKeyValue("Driver name", info.driverName ?: "Not provided by package")
        CapabilityKeyValue("Vulkan version", info.driverVersion ?: "Not provided by package")
        CapabilityKeyValue("Driver date", info.driverDate ?: "Not provided by package")
        CapabilityKeyValue("Package version", info.packageVersion ?: "Not provided by package")
        CapabilityKeyValue("Vendor", info.vendor ?: "Not provided by package")
        CapabilityKeyValue("Author", info.author ?: "Not provided by package")
        CapabilityKeyValue("Minimum Android API", info.minApi?.toString() ?: "Not provided by package")
        CapabilityKeyValue("Metadata schema", info.schemaVersion?.toString() ?: "Not available")
        CapabilityKeyValue("Vulkan® library", info.libraryName ?: "Not available")
        CapabilityKeyValue("Library size", info.librarySizeBytes?.let { android.text.format.Formatter.formatShortFileSize(context, it) } ?: "Not available")
        info.description?.let { CapabilityKeyValue("Description", it) }
        Text("Source-document provenance is private app metadata and is not added to technicalReport or VulkanScope Database submissions.", color = VulkanTextMuted, style = MaterialTheme.typography.bodySmall)
    }
}
