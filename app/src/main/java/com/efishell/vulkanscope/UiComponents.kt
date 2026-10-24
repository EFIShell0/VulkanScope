@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.efishell.vulkanscope

import android.content.res.Configuration
import android.os.Build
import android.view.Surface
import android.view.KeyEvent as AndroidKeyEvent
import android.content.Intent
import android.content.Context
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch




internal data class NavigationItem(val page: Page, val label: String, val icon: Int)

internal fun selectedNavigationPage(page: Page): Page = when (page) {
    Page.Display -> Page.Surface
    Page.Features, Page.Memory, Page.Queues, Page.Video, Page.Formats, Page.Properties, Page.Profiles, Page.Encyclopedia, Page.Analysis, Page.Settings, Page.Info -> Page.Overview
    else -> page
}

internal fun navigationItems(): List<NavigationItem> = listOf(
    NavigationItem(Page.Overview, "Overview", R.drawable.ic_home),
    NavigationItem(Page.Vulkan, "Vulkan", R.drawable.ic_vulkan),
    NavigationItem(Page.Surface, "Surface", R.drawable.ic_surface),
    NavigationItem(Page.Extensions, "Extensions", R.drawable.ic_extensions)
)

internal fun pageIcon(page: Page): Int = when (page) {
    Page.Overview -> R.drawable.ic_home
    Page.Vulkan -> R.drawable.ic_vulkan
    Page.Display -> R.drawable.ic_tablet
    Page.Surface -> R.drawable.ic_surface
    Page.Features -> R.drawable.ic_features
    Page.Memory -> R.drawable.ic_memory
    Page.Queues -> R.drawable.ic_queues
    Page.Video -> R.drawable.ic_video
    Page.Formats -> R.drawable.ic_formats
    Page.Properties -> R.drawable.ic_properties
    Page.Extensions -> R.drawable.ic_extensions
    Page.Profiles -> R.drawable.ic_profile
    Page.Encyclopedia -> R.drawable.ic_book
    Page.Analysis -> R.drawable.ic_analysis
    Page.Settings -> R.drawable.ic_settings
    Page.Info -> R.drawable.ic_info
}


internal fun pageTransitionIndex(page: Page): Int = when (page) {
    Page.Overview -> 0
    Page.Vulkan -> 1
    Page.Surface -> 2
    Page.Display -> 3
    Page.Extensions -> 4
    Page.Features -> 5
    Page.Memory -> 6
    Page.Queues -> 7
    Page.Video -> 8
    Page.Formats -> 9
    Page.Properties -> 10
    Page.Profiles -> 11
    Page.Encyclopedia -> 12
    Page.Analysis -> 13
    Page.Settings -> 14
    Page.Info -> 15
}

internal data class VendorInfo(val name: String, val logo: Int)

internal fun vendorInfo(vendorId: Long): VendorInfo = when (vendorId) {
    0x5143L -> VendorInfo("Qualcomm", R.drawable.gpu_vendor_qualcomm)
    0x13B5L -> VendorInfo("Arm", R.drawable.gpu_vendor_arm)
    0x1010L -> VendorInfo("Imagination Technologies", R.drawable.gpu_vendor_imagination)
    0x19E5L -> VendorInfo("Huawei", R.drawable.gpu_vendor_huawei)
    0x10DEL -> VendorInfo("NVIDIA", R.drawable.gpu_vendor_nvidia)
    0x1002L -> VendorInfo("AMD", R.drawable.gpu_vendor_amd)
    0x8086L -> VendorInfo("Intel", R.drawable.gpu_vendor_intel)
    0x144DL -> VendorInfo("Samsung", R.drawable.gpu_vendor_samsung)
    0x14E4L -> VendorInfo("Broadcom", R.drawable.gpu_vendor_broadcom)
    0x10001L -> VendorInfo("Vivante", R.drawable.gpu_vendor_vivante)
    0x10002L -> VendorInfo("VeriSilicon", R.drawable.gpu_vendor_vsi)
    else -> VendorInfo("Unknown vendor", R.drawable.gpu_vendor_unknown)
}

@Composable
internal fun VendorLogo(vendorId: Long?, modifier: Modifier) {
    val info = vendorInfo(vendorId ?: -1L)
    Card(colors = CardDefaults.cardColors(containerColor = VulkanSurfaceLow), shape = MaterialTheme.shapes.medium, modifier = modifier) {
        Image(
            painter = painterResource(info.logo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().padding(8.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
internal fun UpdateAvailableIcon() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = VulkanAccentContainer,
        modifier = Modifier.size(34.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(R.drawable.ic_update_available),
                contentDescription = null,
                tint = VulkanAccentSoft,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


@Composable
internal fun UpdateSourceIcon() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = VulkanAccentContainer,
        modifier = Modifier.size(34.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(R.drawable.ic_download),
                contentDescription = null,
                tint = VulkanAccentSoft,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
internal fun UpdateStatusBadge(label: String) {
    Surface(shape = RoundedCornerShape(999.dp), color = ComposeColor(0xFF163D24)) {
        Text(label, color = ComposeColor(0xFF55D98A), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp))
    }
}

@Composable
internal fun FloatingStatusSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth().widthIn(max = 520.dp),
        color = ComposeColor(0xD61A1A1F),
        contentColor = VulkanTextPrimary,
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ComposeColor(0x423A3438)),
        tonalElevation = 0.dp,
        shadowElevation = 8.dp
    ) {
        content()
    }
}

@Composable
internal fun TransientStatusOverlayHost(
    collectionStatus: CollectionStatus,
    networkStateKnown: Boolean,
    networkAvailable: Boolean,
    networkBannerState: NetworkBannerState,
    updateStatus: UpdateStatus,
    onInstallUpdate: (AppUpdate) -> Unit,
    modifier: Modifier = Modifier
) {
    val overlayVisible = collectionStatus != CollectionStatus.IDLE ||
        updateStatus !is UpdateStatus.Hidden ||
        networkBannerState != NetworkBannerState.HIDDEN ||
        (networkStateKnown && !networkAvailable)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = if (overlayVisible) 8.dp else 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        CollectionStatusBanner(collectionStatus)
        ConnectivityStatusHost(collectionStatus, networkStateKnown, networkAvailable, networkBannerState)
        UpdateStatusBanner(updateStatus, onInstallUpdate)
    }
}

@Composable
internal fun ConnectivityStatusHost(collectionStatus: CollectionStatus, networkStateKnown: Boolean, networkAvailable: Boolean, transitionState: NetworkBannerState) {
    var showPersistentOffline by remember { mutableStateOf(false) }
    LaunchedEffect(networkStateKnown, networkAvailable, transitionState) {
        showPersistentOffline = false
        if (networkStateKnown && !networkAvailable && transitionState == NetworkBannerState.HIDDEN) {
            delay(380L)
            showPersistentOffline = true
        }
    }
    NetworkStatusBanner(transitionState)
    AnimatedVisibility(
        visible = showPersistentOffline,
        enter = fadeIn(animationSpec = androidx.compose.animation.core.tween(240)) + expandVertically(animationSpec = androidx.compose.animation.core.tween(240)),
        exit = fadeOut(animationSpec = androidx.compose.animation.core.tween(220)) + shrinkVertically(animationSpec = androidx.compose.animation.core.tween(220))
    ) {
        OfflineFeatureAvailabilityBanner(collectionStatus == CollectionStatus.COLLECTING)
    }
}

@Composable
internal fun OfflineFeatureAvailabilityBanner(collectionInProgress: Boolean) {
    FloatingStatusSurface(
        modifier = Modifier.semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite }
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Surface(shape = RoundedCornerShape(50), color = ComposeColor(0xFF16344F), modifier = Modifier.size(30.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(painter = painterResource(R.drawable.ic_info), contentDescription = null, tint = ComposeColor(0xFF5CA9FF), modifier = Modifier.size(17.dp))
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Text("Internet features are unavailable", color = ComposeColor(0xFF9CCBFF), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                Text(if (collectionInProgress) "Vulkan® collection continues offline. Internet-dependent actions remain locked by network state, while report-dependent actions also remain locked until collection completes." else "Vulkan® inspection stays available offline. Database submission/fetching, web links and update checks remain disabled until Android reports a validated internet connection.", color = VulkanTextSecondary, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
internal fun NetworkStatusBanner(state: NetworkBannerState) {
    var renderedState by remember { mutableStateOf(NetworkBannerState.CONNECTED) }
    LaunchedEffect(state) {
        if (state != NetworkBannerState.HIDDEN) renderedState = state
    }
    AnimatedVisibility(
        visible = state != NetworkBannerState.HIDDEN,
        enter = fadeIn(animationSpec = androidx.compose.animation.core.tween(240)) + expandVertically(animationSpec = androidx.compose.animation.core.tween(240)),
        exit = fadeOut(animationSpec = androidx.compose.animation.core.tween(360)) + shrinkVertically(animationSpec = androidx.compose.animation.core.tween(360))
    ) {
        val connected = renderedState == NetworkBannerState.CONNECTED
        val stateColor = if (connected) ComposeColor(0xFF55D98A) else ComposeColor(0xFFFF7676)
        val stateContainer = if (connected) ComposeColor(0xFF163D24) else ComposeColor(0xFF431C20)
        FloatingStatusSurface(
            modifier = Modifier.semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite }
        ) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(shape = RoundedCornerShape(50), color = stateContainer, modifier = Modifier.size(30.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(if (connected) R.drawable.ic_network_connected else R.drawable.ic_network_disconnected),
                            contentDescription = null,
                            tint = stateColor,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text(if (connected) "Connected to network" else "No internet connection", color = stateColor, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    Text(if (connected) "Android reports a validated default network." else "Android does not currently report a validated default network.", color = VulkanTextSecondary, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
internal fun UpdateStatusBanner(status: UpdateStatus, onInstallUpdate: (AppUpdate) -> Unit) {
    AnimatedVisibility(
        visible = status !is UpdateStatus.Hidden,
        enter = fadeIn(animationSpec = androidx.compose.animation.core.tween(220)) + expandVertically(animationSpec = androidx.compose.animation.core.tween(220)),
        exit = fadeOut(animationSpec = androidx.compose.animation.core.tween(360)) + shrinkVertically(animationSpec = androidx.compose.animation.core.tween(360))
    ) {
        FloatingStatusSurface(
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
        ) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                when (status) {
                    UpdateStatus.Checking -> { ExpressiveLinearProgressIndicator(Modifier.width(72.dp)); Text("Checking for updates…", color = VulkanTextSecondary, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f)) }
                    UpdateStatus.UpToDate -> { UpdateStatusBadge("UP TO DATE"); Text("VulkanScope is up to date.", color = VulkanTextSecondary, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f)) }
                    UpdateStatus.DirectUpdatesDisabledIntro -> { UpdateSourceIcon(); Text("Direct GitHub updates are currently disabled. Obtainium can manage updates externally, or direct updates can be enabled in Settings.", color = VulkanTextSecondary, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f)) }
                    is UpdateStatus.Available -> { UpdateAvailableIcon(); Text("VulkanScope ${status.update.version} available", color = VulkanAccentSoft, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f)); ChevronAffordance("Review", "Review update") { onInstallUpdate(status.update) } }
                    is UpdateStatus.Downloading -> { ExpressiveLinearProgressIndicator(Modifier.width(72.dp)); Text("Downloading update…", color = VulkanTextSecondary, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f)) }
                    is UpdateStatus.Failed -> Text(status.message, color = ComposeColor(0xFFFF8A8A), style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
                    UpdateStatus.Hidden -> Unit
                }
            }
        }
    }
}

@Composable
internal fun UpdateDialogKeyValue(key: String, value: String) {
    val expandedTextLayout = preferExpandedTextLayout()
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val stacked = expandedTextLayout || maxWidth < 360.dp || key.length > 24 || value.length > 32 || value.contains("\n")
        val modifier = Modifier.fillMaxWidth().then(tvBrowseModifier(RoundedCornerShape(12.dp))).semantics(mergeDescendants = true) { }
        if (stacked) {
            Column(modifier.padding(vertical = 3.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(trademarkVulkanDisplayText(key), color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                Text(trademarkVulkanDisplayText(value.ifBlank { "Unavailable" }), color = VulkanTextPrimary, style = MaterialTheme.typography.bodySmall)
            }
        } else {
            Row(modifier.padding(vertical = 3.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                Text(trademarkVulkanDisplayText(key), color = VulkanTextMuted, modifier = Modifier.weight(0.82f), style = MaterialTheme.typography.labelSmall)
                Text(trademarkVulkanDisplayText(value.ifBlank { "Unavailable" }), modifier = Modifier.weight(1.18f), color = VulkanTextPrimary, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}


@Composable
internal fun DirectUpdatesConsentDialog(appName: String, releaseSource: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
            modifier = Modifier.border(1.dp, VulkanAccentSoft.copy(alpha = 0.46f), MaterialTheme.shapes.extraLarge),
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = VulkanSurfaceRaised,
        titleContentColor = VulkanTextPrimary,
        textContentColor = VulkanTextPrimary,
        tonalElevation = 0.dp,
        title = { QuestionDialogTitle("Enable direct GitHub updates?") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("$appName will check for updates and download APKs directly from $releaseSource.", color = VulkanTextPrimary)
                Text("If you use Obtainium, leave this disabled so Obtainium remains the single update manager. Enabling direct updates makes the app independently check the same official GitHub Releases source and may duplicate update notifications.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = { ExpressivePrimaryButton("Enable direct updates", onClick = onConfirm) },
        dismissButton = { ExpressiveCancelButton(onClick = onDismiss) }
    )
}

@Composable
internal fun UpdateConfirmationDialog(update: AppUpdate, networkAvailable: Boolean, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    val expandedTextLayout = preferExpandedTextLayout()
    val releaseNotesMaxHeight = if (expandedTextLayout) 220.dp else 360.dp
    AlertDialog(
            modifier = Modifier.fillMaxWidth().widthIn(max = 940.dp).border(1.dp, VulkanAccentSoft.copy(alpha = 0.46f), MaterialTheme.shapes.extraLarge),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = VulkanSurfaceRaised,
        titleContentColor = VulkanTextPrimary,
        textContentColor = VulkanTextPrimary,
        tonalElevation = 0.dp,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                SemanticDialogTitle("Download VulkanScope ${update.version}?", R.drawable.ic_update_available)
                Text("Review the target build and release notes before any APK download starts.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
            }
        },
        text = {
            AdaptiveDialogColumns {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(shape = MaterialTheme.shapes.medium, color = VulkanSurfaceTonal, contentColor = VulkanTextPrimary) {
                    Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        UpdateDialogKeyValue("Installed version", "${update.installedVersion} (versionCode ${update.installedVersionCode})")
                        UpdateDialogKeyValue("Available release", update.version)
                        UpdateDialogKeyValue("Installed ABI", update.installedAbi)
                        UpdateDialogKeyValue("Download ABI", update.downloadAbi)
                        UpdateDialogKeyValue("APK asset", update.assetName)
                        UpdateDialogKeyValue("Downloaded versionCode", "Verified from the APK before installation")
                    }
                }
                Text("The APK is verified for official provenance and signing certificate before Android's installer opens.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                }
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Release notes", color = VulkanTextPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Surface(shape = MaterialTheme.shapes.medium, color = VulkanSurfaceLow, contentColor = VulkanTextPrimary) {
                    ReleaseNotesContent(update.releaseNotes, Modifier.fillMaxWidth().heightIn(max = releaseNotesMaxHeight))
                }
                Text("Package identity, versionCode and versionName are validated locally before installation.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                if (!networkAvailable) Text("Download is disabled until Android reports a validated internet connection.", color = ComposeColor(0xFF9CCBFF), style = MaterialTheme.typography.labelSmall)
                }
            }
        },
        confirmButton = { ExpressivePrimaryIconTextButton("Download update", R.drawable.ic_download_update, enabled = networkAvailable, onClick = onConfirm) },
        dismissButton = { ExpressiveCancelButton(onClick = onDismiss) }
    )
}

internal fun formatUpdateBytesDisplay(bytes: Long): String {
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

internal fun formatUpdateSpeedDisplay(bytesPerSecond: Long): String =
    if (bytesPerSecond <= 0L) "—" else "${formatUpdateBytesDisplay(bytesPerSecond)}/s"

@Composable
internal fun ExpressiveDeterminateDownloadProgress(progress: Float, modifier: Modifier = Modifier) {
    val target = progress.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = target, animationSpec = tween(180, easing = FastOutSlowInEasing), label = "updateDownloadProgress")
    val shape = RoundedCornerShape(999.dp)
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(shape)
                .background(ComposeColor(0xFF2A2022))
        ) {
            if (animatedProgress > 0f) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .clip(shape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(ComposeColor(0xFF7A171B), VulkanAccentSoft, ComposeColor(0xFFE2676A))
                            )
                        )
                )
            }
        }
        Text(
            "${"%.1f".format(java.util.Locale.US, target * 100f)}%",
            color = VulkanTextSecondary,
            style = MaterialTheme.typography.labelMedium,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
internal fun UpdateTransferDialog(
    state: UpdateTransferState,
    networkAvailable: Boolean,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onRequestCancel: () -> Unit,
    onInstall: () -> Unit,
    onClose: () -> Unit
) {
    val terminal = state.phase in setOf(UpdateTransferPhase.COMPLETED, UpdateTransferPhase.CANCELED, UpdateTransferPhase.FAILED)
    val title = when (state.phase) {
        UpdateTransferPhase.CONNECTING -> "Connecting to update"
        UpdateTransferPhase.DOWNLOADING -> "Downloading VulkanScope ${state.update.version}"
        UpdateTransferPhase.PAUSED -> "Update download paused"
        UpdateTransferPhase.VERIFYING -> "Verifying update"
        UpdateTransferPhase.COMPLETED -> "Update downloaded"
        UpdateTransferPhase.CANCELED -> "Update canceled"
        UpdateTransferPhase.FAILED -> "Update download failed"
    }
    val titleIcon = when (state.phase) {
        UpdateTransferPhase.COMPLETED -> R.drawable.ic_check
        UpdateTransferPhase.CANCELED -> R.drawable.ic_close
        UpdateTransferPhase.FAILED -> R.drawable.ic_network_disconnected
        else -> R.drawable.ic_download_update
    }
    val connection = when {
        state.phase == UpdateTransferPhase.CANCELED -> "Canceled"
        state.phase == UpdateTransferPhase.FAILED -> "Error"
        state.phase == UpdateTransferPhase.COMPLETED -> "Completed"
        state.phase == UpdateTransferPhase.PAUSED -> "Paused"
        !networkAvailable && state.phase in setOf(UpdateTransferPhase.CONNECTING, UpdateTransferPhase.DOWNLOADING) -> "Connection unavailable"
        else -> state.connectionStatus
    }
    val progressTotal = state.totalBytes?.takeIf { it > 0L } ?: state.update.assetSizeBytes?.takeIf { it > 0L }
    val progressFraction = when {
        state.phase == UpdateTransferPhase.COMPLETED || state.phase == UpdateTransferPhase.VERIFYING -> 1f
        progressTotal != null -> (state.bytesDownloaded.toDouble() / progressTotal.toDouble()).coerceIn(0.0, 1.0).toFloat()
        else -> 0f
    }
    val progressText = progressTotal?.let { total ->
        val percent = progressFraction * 100f
        "${formatUpdateBytesDisplay(state.bytesDownloaded)} / ${formatUpdateBytesDisplay(total)} (${"%.1f".format(java.util.Locale.US, percent)}%)"
    } ?: formatUpdateBytesDisplay(state.bytesDownloaded)
    val logState = rememberLazyListState()
    LaunchedEffect(state.log.size) {
        if (state.log.isNotEmpty()) logState.animateScrollToItem(state.log.lastIndex)
    }
    AlertDialog(
            modifier = Modifier.border(1.dp, VulkanAccentSoft.copy(alpha = 0.46f), MaterialTheme.shapes.extraLarge),
        onDismissRequest = { if (terminal) onClose() },
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = VulkanSurfaceRaised,
        titleContentColor = VulkanTextPrimary,
        textContentColor = VulkanTextPrimary,
        tonalElevation = 0.dp,
        title = { SemanticDialogTitle(title, titleIcon) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (state.phase !in setOf(UpdateTransferPhase.CANCELED, UpdateTransferPhase.FAILED)) {
                    ExpressiveDeterminateDownloadProgress(progressFraction, Modifier.fillMaxWidth())
                }
                Surface(shape = MaterialTheme.shapes.medium, color = VulkanSurfaceTonal, contentColor = VulkanTextPrimary) {
                    Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        UpdateDialogKeyValue("Connection", connection)
                        UpdateDialogKeyValue("Download speed", formatUpdateSpeedDisplay(state.bytesPerSecond))
                        UpdateDialogKeyValue("Downloaded", progressText)
                        UpdateDialogKeyValue("APK asset", state.update.assetName)
                    }
                }
                state.errorMessage?.let {
                    Text(it, color = ComposeColor(0xFFFF8A8A), style = MaterialTheme.typography.bodySmall)
                }
                Text("Live log", color = VulkanTextPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Surface(shape = MaterialTheme.shapes.medium, color = VulkanBlack, contentColor = VulkanTextPrimary) {
                    Box(Modifier.fillMaxWidth().heightIn(min = 120.dp, max = 220.dp).padding(12.dp)) {
                        LazyColumn(
                            state = logState,
                            modifier = Modifier.fillMaxWidth().desktopVerticalPointerScroll(logState).tvRemoteLazyListNavigation(logState).focusable().focusGroup(),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            userScrollEnabled = true
                        ) {
                            itemsIndexed(state.log, key = { index, _ -> "update-log:$index" }) { _, line ->
                                Text(line, color = ComposeColor(0xFFB9D6B9), style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))
                            }
                        }
                        ExpressiveScrollHints(logState, Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 2.dp))
                    }
                }
                if (state.phase == UpdateTransferPhase.COMPLETED) {
                    Text("The verified APK remains available here until you choose Install or Close.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                }
            }
        },
        confirmButton = {
            when (state.phase) {
                UpdateTransferPhase.CONNECTING, UpdateTransferPhase.DOWNLOADING, UpdateTransferPhase.PAUSED ->
                    ExpressiveContainedIconTextButton("Cancel", R.drawable.ic_close, onClick = onRequestCancel)
                UpdateTransferPhase.COMPLETED ->
                    ExpressivePrimaryIconTextButton("Install", R.drawable.ic_download_update, onClick = onInstall)
                else -> Unit
            }
        },
        dismissButton = {
            when (state.phase) {
                UpdateTransferPhase.CONNECTING, UpdateTransferPhase.DOWNLOADING -> ExpressiveTextButton("Pause", onClick = onPause)
                UpdateTransferPhase.PAUSED -> ExpressiveTextButton("Resume", onClick = onResume)
                UpdateTransferPhase.COMPLETED, UpdateTransferPhase.CANCELED, UpdateTransferPhase.FAILED -> ExpressiveCloseButton(onClick = onClose)
                UpdateTransferPhase.VERIFYING -> Unit
            }
        }
    )
}

@Composable
internal fun UpdateCancelConfirmationDialog(onResume: () -> Unit, onConfirmCancel: () -> Unit) {
    AlertDialog(
            modifier = Modifier.border(1.dp, VulkanAccentSoft.copy(alpha = 0.46f), MaterialTheme.shapes.extraLarge),
        onDismissRequest = onResume,
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = VulkanSurfaceRaised,
        titleContentColor = VulkanTextPrimary,
        textContentColor = VulkanTextPrimary,
        tonalElevation = 0.dp,
        title = { QuestionDialogTitle("Cancel update download?") },
        text = {
            Text("The download has been paused. Canceling removes the partial APK. Resume continues the current download.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodyMedium)
        },
        confirmButton = { ExpressiveContainedIconTextButton("Cancel download", R.drawable.ic_close, onClick = onConfirmCancel) },
        dismissButton = { ExpressiveTextButton("Resume", onClick = onResume) }
    )
}

@Composable
internal fun ReleaseNotesContent(markdown: String, modifier: Modifier = Modifier) {
    val lines = remember(markdown) { markdown.lines() }
    val listState = rememberLazyListState()
    Box(modifier.padding(14.dp)) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth().desktopVerticalPointerScroll(listState).tvRemoteLazyListNavigation(listState).focusGroup(),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            userScrollEnabled = true
        ) {
            itemsIndexed(lines, key = { index, _ -> "release-note:$index" }) { _, raw ->
                ReleaseNoteLine(raw)
            }
        }
        ExpressiveScrollHints(listState, Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 6.dp))
    }
}

@Composable
internal fun ReleaseNoteLine(raw: String) {
    val line = raw.trimEnd()
    if (line.isBlank() || line.startsWith("```")) {
        Spacer(Modifier.height(if (line.isBlank()) 3.dp else 1.dp))
        return
    }
    val shape = RoundedCornerShape(10.dp)
    val isHeading = line.startsWith("# ") || line.startsWith("## ") || line.startsWith("### ")
    val text = when {
        line.startsWith("### ") -> line.removePrefix("### ")
        line.startsWith("## ") -> line.removePrefix("## ")
        line.startsWith("# ") -> line.removePrefix("# ")
        line.startsWith("- ") || line.startsWith("* ") -> "• " + line.drop(2)
        line.startsWith("> ") -> line.drop(2)
        else -> line
    }
    val style = when {
        line.startsWith("### ") -> MaterialTheme.typography.titleSmall
        line.startsWith("## ") -> MaterialTheme.typography.titleMedium
        line.startsWith("# ") -> MaterialTheme.typography.titleLarge
        else -> MaterialTheme.typography.bodySmall
    }
    val weight = when {
        line.startsWith("# ") -> FontWeight.Bold
        line.startsWith("## ") || line.startsWith("### ") -> FontWeight.SemiBold
        else -> FontWeight.Normal
    }
    val color = ComposeColor(0xFFF8F9FC)
    Surface(
        color = ComposeColor.Transparent,
        shape = shape,
        modifier = Modifier.fillMaxWidth().then(tvBrowseModifier(shape)).semantics(mergeDescendants = true) { if (isHeading) heading() }
    ) {
        Text(
            trademarkVulkanDisplayText(text),
            color = color,
            style = style,
            fontWeight = weight,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
internal fun CollectionStatusBanner(status: CollectionStatus) {
    AnimatedVisibility(
        visible = status != CollectionStatus.IDLE,
        enter = fadeIn(animationSpec = androidx.compose.animation.core.tween(260)) + expandVertically(animationSpec = androidx.compose.animation.core.tween(260)),
        exit = fadeOut(animationSpec = androidx.compose.animation.core.tween(420)) + shrinkVertically(animationSpec = androidx.compose.animation.core.tween(420))
    ) {
        val collecting = status == CollectionStatus.COLLECTING
        val failed = status == CollectionStatus.FAILED
        FloatingStatusSurface(
            modifier = Modifier.semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite }
        ) {
            Column(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (collecting) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = VulkanAccentContainer,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_action_update),
                                    contentDescription = null,
                                    tint = VulkanAccentSoft,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Text(
                            "VulkanScope is collecting Vulkan information in the background.",
                            color = ComposeColor(0xFF9E9E9E),
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        val stateColor = if (failed) ComposeColor(0xFFFF7676) else ComposeColor(0xFF55D98A)
                        val stateContainer = if (failed) ComposeColor(0xFF431C20) else ComposeColor(0xFF163D24)
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = stateContainer,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(if (failed) R.drawable.ic_close else R.drawable.ic_check),
                                    contentDescription = null,
                                    tint = stateColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text(
                            if (failed) "Failed" else "Completed",
                            color = stateColor,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            if (failed) "No complete Vulkan information was collected." else "Vulkan information updated.",
                            color = ComposeColor(0xFF9E9E9E),
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                if (collecting) ExpressiveLinearProgressIndicator(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun LoadingView() {
    VulkanLazyPage(verticalSpacing = 12.dp, modifier = Modifier.background(VulkanBlack).semantics { liveRegion = LiveRegionMode.Polite }) {
        item {
            CapabilitySectionCard("Vulkan inspection") {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Surface(shape = MaterialTheme.shapes.large, color = VulkanAccentContainer) {
                        Box(Modifier.size(58.dp), contentAlignment = Alignment.Center) {
                            LoadingIndicator(color = VulkanAccentSoft, modifier = Modifier.size(34.dp))
                        }
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text("Inspecting Vulkan®…", color = VulkanTextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("Collecting the complete Vulkan® evidence set for this session.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Surface(shape = MaterialTheme.shapes.medium, color = VulkanSurfaceTonal) {
                    Column(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Collection in progress", color = VulkanTextPrimary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                        Text("Capability pages become available after the base report reaches a validated terminal state. Missing evidence is not converted into Unsupported while collection is incomplete.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                }
                ExpressiveLinearProgressIndicator(Modifier.fillMaxWidth())
                Text("Supported, Unsupported, Unavailable, Not applicable and Unknown remain separate evidence states.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
internal fun EmptyState(message: String) {
    Surface(color = VulkanSurfaceLow, shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(shape = MaterialTheme.shapes.medium, color = VulkanSurfaceTonal) {
                Icon(painterResource(R.drawable.ic_info), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.padding(9.dp).size(20.dp))
            }
            Text(trademarkVulkanDisplayText(message), color = VulkanTextSecondary, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        }
    }
}

internal fun capabilitySectionIcon(title: String): Int = when {
    title.equals("Display", true) -> R.drawable.ic_tablet
    title.equals("Instance layers", true) -> R.drawable.ic_library
    title.equals("Device layers", true) -> R.drawable.ic_cpu
    title.equals("Developer", true) -> R.drawable.ic_code
    title.equals("Application", true) -> R.drawable.vulkanscope_scope_wordmark
    title.equals("Libraries", true) -> R.drawable.ic_library
    title.equals("Build toolchain", true) -> R.drawable.ic_build
    title.equals("Device ABI", true) -> R.drawable.ic_cpu
    title.equals("Android", true) || title.equals("Android runtime", true) || title.equals("Operating system", true) -> R.drawable.ic_android
    title.equals("Updates", true) || title.equals("Update preferences", true) -> R.drawable.ic_download
    title.equals("App bar visibility", true) -> R.drawable.ic_chrome_visibility
    title.equals("Encyclopedia", true) -> R.drawable.ic_book
    title.equals("How to read encyclopedia entries", true) -> R.drawable.ic_question
    title.equals("Reference search", true) -> R.drawable.ic_search
    title.equals("Global Vulkan report search", true) -> R.drawable.ic_search
    title.equals("Analysis workspace", true) -> R.drawable.ic_analysis
    title.equals("Collection integrity score", true) -> R.drawable.ic_shield
    title.equals("Scoring method", true) -> R.drawable.ic_evidence
    title.equals("What this score does not mean", true) -> R.drawable.ic_question
    title.equals("Dependency graph explorer", true) || title.equals("Graph legend", true) || title.equals("Capability dependency graph", true) || title.equals("Visual registry-reference graph", true) -> R.drawable.ic_graph
    title.equals("Graph overview", true) -> R.drawable.ic_quick_access_grid
    title.equals("Interactive dependency map", true) -> R.drawable.ic_compass
    title.equals("Vulkan active self-tests", true) || title.equals("Optional active tests", true) -> R.drawable.ic_test
    title.equals("Test result summary", true) || title.equals("Self-test result", true) -> R.drawable.ic_self_test
    title.equals("Result semantics", true) -> R.drawable.ic_question
    title.equals("Capability requirement resolver", true) -> R.drawable.ic_registry
    title.equals("Requirement evaluation summary", true) -> R.drawable.ic_check
    title.equals("Vulkan Profiles and custom minimums", true) -> R.drawable.ic_profile
    title.equals("Custom minimum builder", true) -> R.drawable.ic_add
    title.equals("Current custom evaluation", true) -> R.drawable.ic_check
    title.equals("Saved minimum profiles", true) -> R.drawable.ic_save
    title.equals("Local session history", true) -> R.drawable.ic_history
    title.equals("Watched evidence", true) -> R.drawable.ic_watch_add
    title.equals("A/B summary", true) || title.equals("Diff summary", true) || title.equals("Offline report compare", true) || title.equals("System driver ↔ Turnip A/B", true) -> R.drawable.ic_compare
    title.equals("Vulkan registry / query engine", true) || title.equals("Raw structured technical Report", true) -> R.drawable.ic_registry
    title.equals("Opening animation", true) -> R.drawable.ic_opening_animation_toggle
    title.equals("Export complete report", true) -> R.drawable.ic_surface
    title.equals("Compare with VulkanScope Database", true) -> R.drawable.ic_action_database
    title.equals("Database comparison summary", true) -> R.drawable.ic_compare
    title.equals("Database permalink & QR", true) -> R.drawable.ic_qr
    title.equals("VkSurfaceKHR", true) -> R.drawable.ic_surface_khr
    title.equals("Search surface formats / color spaces", true) -> R.drawable.ic_surface_search
    title.contains("Database", true) -> R.drawable.ic_action_database
    title.contains("profile", true) -> R.drawable.ic_profile
    title.contains("diagnostic", true) || title.equals("Query overview", true) || title.equals("Collection diagnostics", true) -> R.drawable.ic_evidence
    title.equals("Extension explorer", true) -> R.drawable.ic_extensions
    title.equals("Explore", true) -> R.drawable.ic_compass
    title.contains("quick access", true) -> R.drawable.ic_quick_access_grid
    title.contains("snapshot", true) || title.contains("inspection", true) -> R.drawable.ic_vulkan
    title.startsWith("Vulkan", true) -> R.drawable.ic_vulkan
    title.contains("driver", true) -> R.drawable.ic_vulkan
    title.contains("display", true) || title.contains("HDR", true) -> R.drawable.ic_display
    title.contains("surface", true) || title.contains("present", true) -> R.drawable.ic_surface
    title.contains("feature", true) || title.contains("requirement", true) || title.contains("evaluation", true) || title.contains("minimum", true) -> R.drawable.ic_features
    title.equals("Memory heaps", true) -> R.drawable.ic_memory_heap
    title.equals("Memory types", true) -> R.drawable.ic_memory_type
    title.contains("memory", true) || title.startsWith("Heap", true) -> R.drawable.ic_memory
    title.contains("queue", true) -> R.drawable.ic_queues
    title.contains("format", true) || title.contains("color space", true) -> R.drawable.ic_formats
    title.contains("extension", true) || title.contains("layer", true) -> R.drawable.ic_extensions
    title.contains("property", true) || title.contains("limit", true) || title.contains("runtime", true) -> R.drawable.ic_properties
    else -> R.drawable.ic_info
}


@Composable
internal fun preferExpandedTextLayout(): Boolean {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    return configuration.fontScale >= 1.3f || configuration.screenWidthDp < 360
}

@Composable
internal fun tvBrowseModifier(shape: Shape): Modifier {
    val requester = remember { BringIntoViewRequester() }
    var focused by remember { mutableStateOf(false) }
    LaunchedEffect(focused) { if (focused) requester.bringIntoView() }
    return Modifier
        .bringIntoViewRequester(requester)
        .onFocusChanged { state -> focused = state.isFocused }
        .focusable()
        .border(if (focused) 2.dp else 0.dp, if (focused) ComposeColor(0xFFE2676A) else ComposeColor.Transparent, shape)
}
@Composable
internal fun ExpressiveSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    labelText: String? = null,
    enabled: Boolean = true,
    placeholderText: String? = null
) {
    var focused by remember { mutableStateOf(false) }
    val containerColor = if (focused) VulkanSurfaceTonal else VulkanSurfaceLow
    val density = LocalDensity.current
    BoxWithConstraints(modifier) {
        val fontPx = with(density) { MaterialTheme.typography.bodyLarge.fontSize.toPx() }
        val availableTextPx = with(density) { (maxWidth - 112.dp).coerceAtLeast(24.dp).toPx() }
        val measuredTextPx = remember(value, fontPx) {
            android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply { textSize = fontPx }.measureText(value)
        }
        val textOverflows = value.isNotBlank() && measuredTextPx > availableTextPx
        val showLeadingFade = focused && textOverflows
        val showTrailingFade = textOverflows
        Box(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth().onFocusChanged { focused = it.isFocused },
                enabled = enabled,
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                leadingIcon = { Icon(painter = painterResource(R.drawable.ic_search), contentDescription = null, modifier = Modifier.size(20.dp)) },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = value.isNotEmpty(),
                        enter = fadeIn(tween(150)) + slideInHorizontally(tween(150)) { it / 2 },
                        exit = fadeOut(tween(120)) + slideOutHorizontally(tween(120)) { it / 2 }
                    ) {
                        IconButton(onClick = { onValueChange("") }, enabled = enabled, modifier = Modifier.size(40.dp)) {
                            Icon(painterResource(R.drawable.ic_close), contentDescription = "Clear search", modifier = Modifier.size(18.dp))
                        }
                    }
                },
                label = if (labelText == null) null else { { Text(labelText) } },
                placeholder = if (placeholderText == null) null else { { Text(placeholderText) } },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = VulkanTextPrimary,
                    unfocusedTextColor = VulkanTextPrimary,
                    focusedContainerColor = VulkanSurfaceTonal,
                    unfocusedContainerColor = VulkanSurfaceLow,
                    cursorColor = VulkanAccentSoft,
                    focusedBorderColor = VulkanAccentSoft,
                    unfocusedBorderColor = VulkanOutline,
                    focusedLeadingIconColor = VulkanAccentSoft,
                    unfocusedLeadingIconColor = VulkanTextMuted,
                    focusedTrailingIconColor = VulkanAccentSoft,
                    unfocusedTrailingIconColor = VulkanTextSecondary,
                    focusedLabelColor = VulkanAccentSoft,
                    unfocusedLabelColor = VulkanTextSecondary,
                    focusedPlaceholderColor = VulkanTextSecondary,
                    unfocusedPlaceholderColor = VulkanTextMuted
                )
            )
            AnimatedVisibility(
                visible = showLeadingFade,
                enter = fadeIn(tween(150)),
                exit = fadeOut(tween(120)),
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 44.dp)
            ) {
                Box(
                    Modifier
                        .width(24.dp)
                        .height(34.dp)
                        .background(Brush.horizontalGradient(listOf(containerColor.copy(alpha = 0.92f), containerColor.copy(alpha = 0.54f), ComposeColor.Transparent)))
                )
            }
            AnimatedVisibility(
                visible = showTrailingFade,
                enter = fadeIn(tween(150)),
                exit = fadeOut(tween(120)),
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 43.dp)
            ) {
                Box(
                    Modifier
                        .width(28.dp)
                        .height(34.dp)
                        .background(Brush.horizontalGradient(listOf(ComposeColor.Transparent, containerColor.copy(alpha = 0.48f), containerColor.copy(alpha = 0.88f))))
                )
            }
        }
    }
}
@Composable
internal fun ExpressiveSwitch(checked: Boolean, onCheckedChange: ((Boolean) -> Unit)?, enabled: Boolean = true) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        thumbContent = if (checked) {
            {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    tint = VulkanAccent,
                    modifier = Modifier.size(14.dp)
                )
            }
        } else null,
        colors = SwitchDefaults.colors(
            checkedThumbColor = VulkanTextPrimary,
            checkedTrackColor = VulkanAccent,
            uncheckedThumbColor = VulkanTextSecondary,
            uncheckedTrackColor = VulkanSurfaceTonal,
            uncheckedBorderColor = VulkanOutlineVariant
        )
    )
}

@Composable
internal fun QuestionDialogTitle(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(shape = RoundedCornerShape(18.dp), color = VulkanAccentContainer) {
            Icon(painterResource(R.drawable.ic_question), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.padding(9.dp).size(20.dp))
        }
        Text(trademarkVulkanDisplayText(text), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = VulkanTextPrimary, modifier = Modifier.weight(1f))
    }
}

@Composable
internal fun SemanticDialogTitle(text: String, icon: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(shape = RoundedCornerShape(18.dp), color = VulkanAccentContainer) {
            Icon(painterResource(icon), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.padding(9.dp).size(20.dp))
        }
        Text(trademarkVulkanDisplayText(text), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = VulkanTextPrimary, modifier = Modifier.weight(1f))
    }
}

@Composable
internal fun ExpressiveCancelButton(enabled: Boolean = true, onClick: () -> Unit) {
    TextButton(onClick = onClick, enabled = enabled, modifier = Modifier.heightIn(min = 48.dp), colors = ButtonDefaults.textButtonColors(contentColor = VulkanAccentSoft, disabledContentColor = VulkanTextMuted)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                Icon(painterResource(R.drawable.ic_close), contentDescription = null, modifier = Modifier.size(17.dp))
            }
            Text("Cancel", fontWeight = FontWeight.Normal, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
internal fun ExpressiveCloseButton(enabled: Boolean = true, onClick: () -> Unit) {
    TextButton(onClick = onClick, enabled = enabled, modifier = Modifier.heightIn(min = 48.dp), colors = ButtonDefaults.textButtonColors(contentColor = VulkanAccentSoft, disabledContentColor = VulkanTextMuted)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                Icon(painterResource(R.drawable.ic_close), contentDescription = null, modifier = Modifier.size(17.dp))
            }
            Text("Close", fontWeight = FontWeight.Normal, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ExpressivePrimaryButton(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shapes = ButtonDefaults.shapes(
            shape = RoundedCornerShape(20.dp),
            pressedShape = RoundedCornerShape(26.dp)
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = VulkanAccent,
            contentColor = VulkanTextPrimary,
            disabledContainerColor = ComposeColor(0xFF33292E),
            disabledContentColor = ComposeColor(0xFFC9BFC1)
        )
    ) {
        Text(trademarkVulkanDisplayText(label), fontWeight = FontWeight.SemiBold)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ExpressivePrimaryIconTextButton(label: String, icon: Int, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shapes = ButtonDefaults.shapes(
            shape = RoundedCornerShape(20.dp),
            pressedShape = RoundedCornerShape(26.dp)
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = VulkanAccent,
            contentColor = VulkanTextPrimary,
            disabledContainerColor = ComposeColor(0xFF33292E),
            disabledContentColor = ComposeColor(0xFFC9BFC1)
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            Icon(painterResource(icon), contentDescription = null, modifier = Modifier.size(18.dp))
            Text(label, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ExpressiveTextButton(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        shapes = ButtonDefaults.shapes(
            shape = RoundedCornerShape(18.dp),
            pressedShape = RoundedCornerShape(24.dp)
        ),
        colors = ButtonDefaults.textButtonColors(contentColor = VulkanAccentSoft, disabledContentColor = ComposeColor(0xFFC9BFC1))
    ) {
        Text(label, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
internal fun ExpressiveContainedTextButton(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        shapes = ButtonDefaults.shapes(
            shape = RoundedCornerShape(18.dp),
            pressedShape = RoundedCornerShape(24.dp)
        ),
        colors = ButtonDefaults.textButtonColors(
            containerColor = VulkanAccentContainer,
            contentColor = VulkanAccentSoft,
            disabledContainerColor = ComposeColor(0xFF33292E),
            disabledContentColor = ComposeColor(0xFFC9BFC1)
        ),
        modifier = Modifier.heightIn(min = 48.dp).border(1.dp, VulkanOutlineVariant.copy(alpha = 0.55f), RoundedCornerShape(18.dp))
    ) {
        Text(label, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
internal fun ExpressiveContainedIconTextButton(label: String, icon: Int, modifier: Modifier = Modifier, enabled: Boolean = true, fontWeight: FontWeight = FontWeight.SemiBold, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = 48.dp).border(1.dp, VulkanOutlineVariant.copy(alpha = 0.55f), RoundedCornerShape(18.dp)),
        shapes = ButtonDefaults.shapes(
            shape = RoundedCornerShape(18.dp),
            pressedShape = RoundedCornerShape(24.dp)
        ),
        colors = ButtonDefaults.textButtonColors(
            containerColor = VulkanAccentContainer,
            contentColor = VulkanAccentSoft,
            disabledContainerColor = ComposeColor(0xFF33292E),
            disabledContentColor = ComposeColor(0xFFC9BFC1)
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp), modifier = Modifier.heightIn(min = 24.dp)) {
            Icon(painterResource(icon), contentDescription = null, modifier = Modifier.size(18.dp))
            Text(trademarkVulkanDisplayText(label), fontWeight = fontWeight, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
internal fun ExpressiveLinearProgressIndicator(modifier: Modifier = Modifier) {
    LinearWavyProgressIndicator(
        modifier = modifier.height(12.dp),
        color = VulkanAccentSoft,
        trackColor = ComposeColor(0xFF2A2022)
    )
}

@Composable
internal fun SectionHeaderIcon(title: String, sectionIcon: Int) {
    when {
        title.equals("App bar visibility", true) -> {
            Box(Modifier.padding(8.dp).size(27.dp), contentAlignment = Alignment.Center) {
                Icon(painter = painterResource(R.drawable.ic_chrome_visibility), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.size(22.dp))
            }
        }
        title.equals("Opening animation", true) -> {
            Box(Modifier.padding(8.dp).size(27.dp), contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(R.drawable.ic_opening_animation_toggle),
                    contentDescription = null,
                    tint = VulkanAccentSoft,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        title.equals("Encyclopedia", true) -> SectionVectorBadgeIcon(R.drawable.ic_book, "REF")
        title.equals("Reference search", true) -> SectionVectorBadgeIcon(R.drawable.ic_book, overlayIcon = R.drawable.ic_search)
        title.equals("How to read encyclopedia entries", true) -> SectionVectorBadgeIcon(R.drawable.ic_book, overlayIcon = R.drawable.ic_question)
        title.equals("Capability requirement resolver", true) -> SectionVectorBadgeIcon(R.drawable.ic_registry, overlayIcon = R.drawable.ic_search)
        title.equals("Requirement evaluation summary", true) -> SectionVectorBadgeIcon(R.drawable.ic_registry, overlayIcon = R.drawable.ic_check)
        title.equals("Vulkan Profiles and custom minimums", true) -> SectionVectorBadgeIcon(R.drawable.ic_profile, "VP")
        title.equals("Custom minimum builder", true) -> SectionVectorBadgeIcon(R.drawable.ic_profile, overlayIcon = R.drawable.ic_add)
        title.equals("Saved minimum profiles", true) -> SectionVectorBadgeIcon(R.drawable.ic_profile, overlayIcon = R.drawable.ic_save)
        title.equals("Current custom evaluation", true) -> SectionVectorBadgeIcon(R.drawable.ic_profile, overlayIcon = R.drawable.ic_check)
        title.equals("Dependency graph explorer", true) -> SectionVectorBadgeIcon(R.drawable.ic_graph, overlayIcon = R.drawable.ic_search)
        title.equals("Graph overview", true) -> SectionVectorBadgeIcon(R.drawable.ic_graph, overlayIcon = R.drawable.ic_quick_access_grid)
        title.equals("Interactive dependency map", true) -> SectionVectorBadgeIcon(R.drawable.ic_graph, overlayIcon = R.drawable.ic_compass)
        title.equals("Collection integrity score", true) -> SectionVectorBadgeIcon(R.drawable.ic_shield, overlayIcon = R.drawable.ic_check)
        title.equals("Scoring method", true) -> SectionVectorBadgeIcon(R.drawable.ic_shield, overlayIcon = R.drawable.ic_evidence)
        title.equals("What this score does not mean", true) -> SectionVectorBadgeIcon(R.drawable.ic_shield, overlayIcon = R.drawable.ic_question)
        title.equals("Vulkan active self-tests", true) -> SectionVectorBadgeIcon(R.drawable.ic_test, "RUN")
        title.equals("Test result summary", true) -> SectionVectorBadgeIcon(R.drawable.ic_self_test, "SUM")
        title.equals("Result semantics", true) -> SectionVectorBadgeIcon(R.drawable.ic_test, overlayIcon = R.drawable.ic_question)
        title.equals("Vulkan registry / query engine", true) -> SectionVectorBadgeIcon(R.drawable.ic_registry, "REG")
        title.equals("About", true) -> AboutSectionIcon()
        title.equals("Android runtime", true) -> AndroidRuntimeSectionIcon()
        title.equals("Extension explorer", true) -> SectionVectorBadgeIcon(R.drawable.ic_extensions, overlayIcon = R.drawable.ic_search)
        title.equals("Format explorer", true) -> SectionVectorBadgeIcon(R.drawable.ic_formats, overlayIcon = R.drawable.ic_search)
        title.equals("Instance layers", true) -> SectionVectorBadgeIcon(R.drawable.ic_library, overlayIcon = R.drawable.ic_extensions)
        title.equals("Device layers", true) -> SectionVectorBadgeIcon(R.drawable.ic_cpu, overlayIcon = R.drawable.ic_library)
        sectionIcon == R.drawable.vulkanscope_scope_wordmark -> {
            Image(
                painter = painterResource(sectionIcon),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 11.dp).width(32.dp).height(18.dp)
            )
        }
        sectionIcon == R.drawable.ic_android -> {
            Image(
                painter = painterResource(sectionIcon),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 12.dp).width(27.dp).height(16.dp)
            )
        }
        title.equals("VkSurfaceKHR", true) -> {
            Box(Modifier.padding(7.dp).size(27.dp)) {
                Icon(painter = painterResource(R.drawable.ic_surface), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.align(Alignment.TopStart).size(21.dp))
                Surface(shape = RoundedCornerShape(3.dp), color = VulkanSurface, modifier = Modifier.align(Alignment.BottomEnd).border(0.7.dp, VulkanAccentSoft, RoundedCornerShape(3.dp))) {
                    Text("KHR", color = VulkanAccentSoft, fontSize = 4.1.sp, lineHeight = 4.3.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 1.dp, vertical = 0.8.dp))
                }
            }
        }
        title.equals("Search surface formats / color spaces", true) -> {
            Box(Modifier.padding(7.dp).size(27.dp)) {
                Icon(painter = painterResource(R.drawable.ic_surface), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.align(Alignment.TopStart).size(21.dp))
                Surface(shape = RoundedCornerShape(4.dp), color = VulkanSurface, modifier = Modifier.align(Alignment.BottomEnd)) {
                    Icon(painter = painterResource(R.drawable.ic_search), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.padding(1.2.dp).size(9.dp))
                }
            }
        }
        title.equals("Queue query safety", true) -> {
            Box(Modifier.padding(7.dp).size(27.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_queues),
                    contentDescription = null,
                    tint = VulkanAccentSoft,
                    modifier = Modifier.align(Alignment.TopStart).size(21.dp)
                )
                Surface(shape = RoundedCornerShape(4.dp), color = VulkanSurface, modifier = Modifier.align(Alignment.BottomEnd)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_shield),
                        contentDescription = null,
                        tint = VulkanAccentSoft,
                        modifier = Modifier.padding(1.3.dp).size(9.dp)
                    )
                }
            }
        }
        title.equals("Export complete report", true) -> {
            Box(Modifier.padding(7.dp).size(27.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_surface),
                    contentDescription = null,
                    tint = VulkanAccentSoft,
                    modifier = Modifier.align(Alignment.TopStart).size(21.dp)
                )
                Surface(shape = RoundedCornerShape(4.dp), color = VulkanSurface, modifier = Modifier.align(Alignment.BottomEnd)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_export),
                        contentDescription = null,
                        tint = VulkanAccentSoft,
                        modifier = Modifier.padding(1.3.dp).size(9.dp)
                    )
                }
            }
        }
        title.equals("Surface + Display presentation evidence", true) -> {
            Box(Modifier.padding(7.dp).size(27.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_display),
                    contentDescription = null,
                    tint = VulkanAccentSoft,
                    modifier = Modifier.align(Alignment.TopStart).size(21.dp)
                )
                Surface(shape = RoundedCornerShape(4.dp), color = VulkanSurface, modifier = Modifier.align(Alignment.BottomEnd)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_surface),
                        contentDescription = null,
                        tint = VulkanAccentSoft,
                        modifier = Modifier.padding(1.5.dp).size(9.dp)
                    )
                }
            }
        }
        title.equals("Raw structured technical Report", true) -> {
            Box(Modifier.padding(7.dp).size(27.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_registry),
                    contentDescription = null,
                    tint = VulkanAccentSoft,
                    modifier = Modifier.align(Alignment.TopStart).size(21.dp)
                )
                Surface(
                    shape = RoundedCornerShape(3.dp),
                    color = VulkanSurface,
                    modifier = Modifier.align(Alignment.BottomEnd).border(0.7.dp, VulkanAccentSoft, RoundedCornerShape(3.dp))
                ) {
                    Text(
                        "JSON",
                        color = VulkanAccentSoft,
                        fontSize = 4.2.sp,
                        lineHeight = 4.5.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 1.1.dp, vertical = 0.8.dp)
                    )
                }
            }
        }
        title.equals("Compare with VulkanScope Database", true) -> {
            Box(Modifier.padding(7.dp).size(27.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_action_database),
                    contentDescription = null,
                    tint = VulkanAccentSoft,
                    modifier = Modifier.align(Alignment.TopStart).size(21.dp)
                )
                Surface(shape = RoundedCornerShape(4.dp), color = VulkanSurface, modifier = Modifier.align(Alignment.BottomEnd)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_compare),
                        contentDescription = null,
                        tint = VulkanAccentSoft,
                        modifier = Modifier.padding(1.2.dp).size(9.dp)
                    )
                }
            }
        }
        title.equals("HDR capabilities", true) || title.equals("HDR / wide-color surface detection", true) -> {
            DisplaySectionBadgeIcon("HDR")
        }
        title.equals("Supported display modes", true) -> {
            DisplaySectionBadgeIcon("MODE")
        }
        title.equals("Display ↔ Vulkan interpretation", true) -> {
            Box(Modifier.padding(7.dp).size(27.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_display),
                    contentDescription = null,
                    tint = VulkanAccentSoft,
                    modifier = Modifier.align(Alignment.TopStart).size(22.dp)
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = VulkanSurface,
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_surface),
                        contentDescription = null,
                        tint = VulkanAccentSoft,
                        modifier = Modifier.padding(1.5.dp).size(8.dp)
                    )
                }
            }
        }
        else -> {
            Icon(
                painter = painterResource(sectionIcon),
                contentDescription = null,
                tint = VulkanAccentSoft,
                modifier = Modifier.padding(10.dp).size(21.dp)
            )
        }
    }
}

@Composable
internal fun SectionVectorBadgeIcon(primary: Int, badgeText: String? = null, overlayIcon: Int? = null) {
    Box(Modifier.padding(7.dp).size(27.dp)) {
        Icon(painterResource(primary), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.align(Alignment.TopStart).size(21.dp))
        Surface(shape = RoundedCornerShape(3.dp), color = VulkanSurface, modifier = Modifier.align(Alignment.BottomEnd).border(0.7.dp, VulkanAccentSoft, RoundedCornerShape(3.dp))) {
            if (overlayIcon != null) {
                Icon(painterResource(overlayIcon), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.padding(1.2.dp).size(9.dp))
            } else {
                Text(badgeText.orEmpty(), color = VulkanAccentSoft, fontSize = 4.0.sp, lineHeight = 4.3.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 1.dp, vertical = 0.8.dp))
            }
        }
    }
}

@Composable
internal fun AndroidRuntimeSectionIcon() {
    Box(Modifier.padding(7.dp).size(27.dp)) {
        Image(painterResource(R.drawable.ic_android), contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier.align(Alignment.TopStart).width(22.dp).height(15.dp))
        Surface(shape = RoundedCornerShape(3.dp), color = VulkanSurface, modifier = Modifier.align(Alignment.BottomEnd).border(0.7.dp, VulkanAccentSoft, RoundedCornerShape(3.dp))) {
            Text("RUN", color = VulkanAccentSoft, fontSize = 4.0.sp, lineHeight = 4.3.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 1.dp, vertical = 0.8.dp))
        }
    }
}

@Composable
internal fun AboutSectionIcon() {
    Box(Modifier.padding(6.dp).size(30.dp)) {
        Image(painterResource(R.drawable.vulkanscope_scope_wordmark), contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier.align(Alignment.CenterStart).width(27.dp).height(15.dp))
        Surface(shape = RoundedCornerShape(4.dp), color = VulkanSurface, modifier = Modifier.align(Alignment.BottomEnd)) {
            Icon(painterResource(R.drawable.ic_info), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.padding(1.2.dp).size(9.dp))
        }
    }
}

@Composable
internal fun DisplaySectionBadgeIcon(label: String) {
    Box(Modifier.padding(7.dp).size(27.dp)) {
        Icon(
            painter = painterResource(R.drawable.ic_display),
            contentDescription = null,
            tint = VulkanAccentSoft,
            modifier = Modifier.align(Alignment.TopStart).size(22.dp)
        )
        Surface(
            shape = RoundedCornerShape(3.dp),
            color = VulkanSurface,
            modifier = Modifier.align(Alignment.BottomEnd).border(0.7.dp, VulkanAccentSoft, RoundedCornerShape(3.dp))
        ) {
            Text(
                label,
                color = VulkanAccentSoft,
                fontSize = if (label == "MODE") 4.2.sp else 5.2.sp,
                lineHeight = if (label == "MODE") 4.5.sp else 5.4.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = 1.2.dp, vertical = 0.8.dp)
            )
        }
    }
}

@Composable
internal fun CapabilitySectionCard(title: String, content: @Composable () -> Unit) {
    val shape = MaterialTheme.shapes.extraLarge
    VulkanPremiumPanel(
        color = VulkanSurfaceRaised,
        shape = shape,
        emphasis = true,
        modifier = Modifier.fillMaxWidth().then(tvBrowseModifier(shape))
    ) {
        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val sectionIcon = capabilitySectionIcon(title)
                Surface(shape = RoundedCornerShape(18.dp), color = ComposeColor(0xFF351719)) {
                    SectionHeaderIcon(title, sectionIcon)
                }
                Text(
                    trademarkVulkanDisplayText(title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = ComposeColor(0xFFF7F2F3),
                    modifier = Modifier.weight(1f).semantics { heading() }
                )
            }
            HorizontalDivider(color = ComposeColor(0xFF2A2527))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { content() }
        }
    }
}

@Composable
internal fun CapabilityItemCard(
    containerColor: ComposeColor = VulkanSurfaceRaised,
    content: @Composable () -> Unit
) {
    val shape = MaterialTheme.shapes.large
    VulkanPremiumPanel(color = containerColor, shape = shape, modifier = Modifier.fillMaxWidth().then(tvBrowseModifier(shape))) {
        Column(Modifier.fillMaxWidth()) { content() }
    }
}

@Composable
internal fun rememberFilterScrollBoundaryConnection(): NestedScrollConnection = remember {
    object : NestedScrollConnection {
        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
            return if (available.y != 0f) Offset(0f, available.y) else Offset.Zero
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            return if (available.y != 0f) Velocity(0f, available.y) else Velocity.Zero
        }
    }
}

@Composable
internal fun ExpressiveSingleFilterSelector(
    labels: List<String>,
    selectedIndex: Int?,
    enabled: Boolean,
    indicatorTint: ComposeColor,
    onSelected: (Int) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var dropdownMounted by remember { mutableStateOf(false) }
    var dropdownVisible by remember { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    var page by rememberSaveable { mutableIntStateOf(0) }
    var pageField by remember { mutableStateOf(TextFieldValue("1")) }
    var pageFieldFocused by remember { mutableStateOf(false) }
    var suppressFilterPageCommit by remember { mutableStateOf(false) }
    val pageSize = COLLECTION_PAGE_SIZE
    val showSearch = labels.size >= 5
    val indexed = remember(labels, query) {
        labels.mapIndexed { index, label -> index to label }
            .filter { (_, label) -> query.isBlank() || label.contains(query, ignoreCase = true) }
    }
    val pageCount = maxOf(1, (indexed.size + pageSize - 1) / pageSize)
    val selectedLabel = selectedIndex?.takeIf { it in labels.indices }?.let(labels::get) ?: "Select filter"
    val selectorAlpha by animateFloatAsState(if (enabled) 1f else 0.52f, animationSpec = tween(180), label = "filterSelectorAlpha")
    val selectorScale by animateFloatAsState(if (enabled) 1f else 0.985f, animationSpec = tween(180), label = "filterSelectorScale")
    val arrowRotation by animateFloatAsState(if (expanded) 180f else 0f, animationSpec = tween(180), label = "filterSelectorArrow")
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    val imeVisible = WindowInsets.ime.getBottom(density) > 0

    LaunchedEffect(enabled) {
        if (!enabled) expanded = false
    }
    LaunchedEffect(showSearch) {
        if (!showSearch && query.isNotEmpty()) query = ""
    }
    LaunchedEffect(expanded, enabled) {
        if (expanded && enabled) {
            query = ""
            val openingPage = selectedIndex?.takeIf { it in labels.indices }?.div(pageSize) ?: 0
            page = openingPage.coerceIn(0, pageCount - 1)
            pageField = TextFieldValue((page + 1).toString())
            dropdownMounted = true
            dropdownVisible = false
            delay(20)
            dropdownVisible = true
        } else {
            dropdownVisible = false
            delay(190)
            dropdownMounted = false
        }
    }
    fun commitFilterPage() {
        val requested = pageField.text.toIntOrNull()?.coerceIn(1, pageCount) ?: (page + 1).coerceIn(1, pageCount)
        page = requested - 1
        pageField = TextFieldValue(requested.toString())
    }
    fun requestFilterPageChange(targetPage: Int) {
        val bounded = targetPage.coerceIn(0, pageCount - 1)
        suppressFilterPageCommit = true
        pageField = TextFieldValue((bounded + 1).toString())
        focusManager.clearFocus(force = true)
        pageFieldFocused = false
        suppressFilterPageCommit = false
        page = bounded
    }
    LaunchedEffect(query, pageCount) {
        page = page.coerceIn(0, pageCount - 1)
        if (!pageFieldFocused) pageField = TextFieldValue((page + 1).toString())
    }
    LaunchedEffect(page, pageCount, pageFieldFocused) {
        page = page.coerceIn(0, pageCount - 1)
        if (!pageFieldFocused) pageField = TextFieldValue((page + 1).toString())
    }

    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val configuration = androidx.compose.ui.platform.LocalConfiguration.current
        val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val dropdownWidth = if (landscape) maxWidth else maxWidth.coerceAtMost(560.dp)
        val screenHeight = configuration.screenHeightDp.dp
        val imeHeight = with(density) { WindowInsets.ime.getBottom(density).toDp() }
        val usableHeight = (screenHeight - imeHeight).coerceAtLeast(220.dp)
        val dropdownMaxHeight = if (landscape) {
            ((usableHeight.value * 0.62f).dp).coerceIn(220.dp, 440.dp)
        } else {
            (usableHeight - 96.dp).coerceIn(240.dp, 620.dp)
        }
        val visibleRows = indexed.drop(page * pageSize).take(pageSize).size.coerceIn(1, if (landscape) 4 else 7)
        val dropdownDesiredHeight = (
            28.dp +
                (if (showSearch) 96.dp else 0.dp) +
                (58.dp * visibleRows) +
                (if (pageCount > 1 && indexed.isNotEmpty()) 78.dp else 0.dp)
            ).coerceAtMost(dropdownMaxHeight)
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            val selectorShape = MaterialTheme.shapes.medium
            Surface(
                shape = selectorShape,
                color = VulkanSurfaceTonal,
                contentColor = VulkanTextPrimary,
                border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutline),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 58.dp)
                    .alpha(selectorAlpha)
                    .graphicsLayer(scaleX = selectorScale, scaleY = selectorScale)
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text("Filter", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                        Text(
                            trademarkVulkanDisplayText(selectedLabel),
                            color = VulkanTextPrimary,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    val arrowShape = RoundedCornerShape(15.dp)
                    Surface(
                        shape = arrowShape,
                        color = if (expanded) VulkanAccent else VulkanAccentContainer,
                        contentColor = indicatorTint,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(arrowShape)
                            .clickable(enabled = enabled, role = Role.Button) { expanded = !expanded }
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(R.drawable.ic_expand_more),
                                contentDescription = if (expanded) "Close filter menu" else "Open filter menu",
                                tint = indicatorTint,
                                modifier = Modifier.size(24.dp).graphicsLayer(rotationZ = arrowRotation)
                            )
                        }
                    }
                }
            }

            BackHandler(enabled = dropdownMounted && expanded) {
                if (imeVisible) focusManager.clearFocus(force = true)
            }

            if (dropdownMounted && enabled) {
                AnimatedVisibility(
                    visible = dropdownVisible,
                    enter = fadeIn(tween(220)) + scaleIn(tween(240), initialScale = 0.97f, transformOrigin = TransformOrigin(0.5f, 0f)) + expandVertically(tween(220), expandFrom = Alignment.Top),
                    exit = fadeOut(tween(150)) + scaleOut(tween(170), targetScale = 0.985f, transformOrigin = TransformOrigin(0.5f, 0f)) + shrinkVertically(tween(170), shrinkTowards = Alignment.Top)
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = VulkanSurfaceRaised,
                        contentColor = VulkanTextPrimary,
                        border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutline),
                        shadowElevation = 8.dp,
                        modifier = Modifier.width(dropdownWidth).height(dropdownDesiredHeight)
                    ) {
                        Column(Modifier.fillMaxHeight().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (showSearch) {
                                ExpressiveSearchField(
                                    value = query,
                                    onValueChange = { value ->
                                        query = value.take(120)
                                        page = 0
                                        pageField = TextFieldValue("1")
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    labelText = "Search filters",
                                    placeholderText = "Type a filter name…"
                                )
                            }
                            if (showSearch) {
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        if (indexed.isEmpty()) "No matching filters" else "${indexed.size} result${if (indexed.size == 1) "" else "s"} · up to $pageSize per page",
                                        color = VulkanTextSecondary,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (indexed.isNotEmpty() && pageCount > 1) {
                                        Text("Page ${page + 1} / $pageCount", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                            Box(Modifier.weight(1f).fillMaxWidth().heightIn(min = 56.dp)) {
                                AnimatedContent(
                                    targetState = page to query,
                                    transitionSpec = {
                                        val direction = if (targetState.first >= initialState.first) 1 else -1
                                        (fadeIn(tween(180)) + slideInHorizontally(tween(190)) { direction * (it / 10) }) togetherWith
                                            (fadeOut(tween(120)) + slideOutHorizontally(tween(140)) { -direction * (it / 10) })
                                    },
                                    label = "filterPageTransition"
                                ) { (targetPage, targetQuery) ->
                                    val targetIndexed = labels.mapIndexed { index, label -> index to label }
                                        .filter { (_, label) -> targetQuery.isBlank() || label.contains(targetQuery, ignoreCase = true) }
                                    val targetPageItems = targetIndexed.drop(targetPage * pageSize).take(pageSize)
                                    val selectedOffset = if (targetQuery.isBlank()) targetPageItems.indexOfFirst { it.first == selectedIndex } else -1
                                    val targetListState = rememberLazyListState(initialFirstVisibleItemIndex = selectedOffset.coerceAtLeast(0))
                                    val boundaryScrollConnection = rememberFilterScrollBoundaryConnection()
                                    Box(Modifier.fillMaxSize().nestedScroll(boundaryScrollConnection)) {
                                        LazyColumn(
                                            state = targetListState,
                                            modifier = Modifier.fillMaxSize().desktopVerticalPointerScroll(targetListState).tvRemoteLazyListNavigation(targetListState).focusGroup(),
                                            verticalArrangement = Arrangement.spacedBy(5.dp),
                                            contentPadding = PaddingValues(vertical = 2.dp)
                                        ) {
                                            items(targetPageItems, key = { it.first }) { (index, label) ->
                                                val selected = index == selectedIndex
                                                val rowShape = RoundedCornerShape(18.dp)
                                                Surface(
                                                    shape = rowShape,
                                                    color = if (selected) VulkanAccentContainer else ComposeColor.Transparent,
                                                    contentColor = if (selected) VulkanTextPrimary else VulkanTextSecondary,
                                                    modifier = Modifier.fillMaxWidth().clip(rowShape).clickable(role = Role.RadioButton) { onSelected(index) }.then(tvBrowseModifier(rowShape))
                                                ) {
                                                    Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                                        Surface(shape = RoundedCornerShape(999.dp), color = if (selected) VulkanAccentSoft else VulkanOutlineVariant, modifier = Modifier.size(10.dp)) {}
                                                        Text(
                                                            trademarkVulkanDisplayText(label),
                                                            modifier = Modifier.weight(1f),
                                                            textAlign = TextAlign.Start,
                                                            softWrap = true,
                                                            color = if (selected) VulkanTextPrimary else VulkanTextSecondary,
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                    }
                                                }
                                            }
                                            if (targetPageItems.isEmpty()) {
                                                item {
                                                    Text(
                                                        "No filter matches this search.",
                                                        color = VulkanTextMuted,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 16.dp),
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                            }
                                        }
                                        ExpressiveScrollHints(targetListState, Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 4.dp))
                                    }
                                }
                            }
                            if (indexed.isNotEmpty() && pageCount > 1) {
                                HorizontalDivider(color = VulkanOutlineVariant)
                                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        IconButton(
                                            onClick = { if (page > 0) requestFilterPageChange(page - 1) },
                                            enabled = page > 0,
                                            colors = IconButtonDefaults.iconButtonColors(
                                                containerColor = VulkanAccentContainer,
                                                contentColor = VulkanTextPrimary,
                                                disabledContainerColor = VulkanSurfaceLow,
                                                disabledContentColor = VulkanTextMuted
                                            )
                                        ) {
                                            Icon(painterResource(R.drawable.ic_chevron_left), contentDescription = "Previous filter page")
                                        }
                                        Box(Modifier.width(72.dp), contentAlignment = Alignment.Center) {
                                            OutlinedTextField(
                                                value = pageField,
                                                onValueChange = { value ->
                                                    val candidate = value.text
                                                    when {
                                                        candidate.isEmpty() -> pageField = value
                                                        candidate.all { it.isDigit() } && !(candidate.length > 1 && candidate.startsWith('0')) && (candidate.toIntOrNull() ?: 0) in 1..pageCount -> pageField = value
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth().onFocusChanged { focus ->
                                                    val wasFocused = pageFieldFocused
                                                    pageFieldFocused = focus.isFocused
                                                    if (focus.isFocused) {
                                                        pageField = pageField.copy(selection = TextRange(0, pageField.text.length))
                                                    } else if (wasFocused && !suppressFilterPageCommit) {
                                                        commitFilterPage()
                                                    }
                                                },
                                                singleLine = true,
                                                label = { Text("Page") },
                                                textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                                                keyboardActions = KeyboardActions(onDone = {
                                                    commitFilterPage()
                                                    focusManager.clearFocus(force = true)
                                                }),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = VulkanAccentSoft,
                                                    unfocusedBorderColor = VulkanOutline,
                                                    focusedTextColor = VulkanTextPrimary,
                                                    unfocusedTextColor = ComposeColor.Transparent,
                                                    cursorColor = VulkanAccentSoft
                                                )
                                            )
                                            if (!pageFieldFocused) {
                                                Box(Modifier.matchParentSize(), contentAlignment = Alignment.Center) {
                                                    AnimatedContent(
                                                        targetState = (page + 1).coerceIn(1, pageCount),
                                                        transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(130)) },
                                                        label = "filterPageNumberTransition"
                                                    ) { animatedNumber ->
                                                        Text(animatedNumber.toString(), color = VulkanTextPrimary, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.offset(y = 4.dp))
                                                    }
                                                }
                                            }
                                        }
                                        Text("/ $pageCount", color = VulkanTextSecondary, style = MaterialTheme.typography.bodyMedium)
                                        IconButton(
                                            onClick = { if (page + 1 < pageCount) requestFilterPageChange(page + 1) },
                                            enabled = page + 1 < pageCount,
                                            colors = IconButtonDefaults.iconButtonColors(
                                                containerColor = VulkanAccentContainer,
                                                contentColor = VulkanTextPrimary,
                                                disabledContainerColor = VulkanSurfaceLow,
                                                disabledContentColor = VulkanTextMuted
                                            )
                                        ) {
                                            Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = "Next filter page")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
internal fun ExpressiveFilterBar(labels: List<String>, selectedIndex: Int, arrowTint: ComposeColor = VulkanTextPrimary, onSelected: (Int) -> Unit) {
    if (labels.firstOrNull()?.equals("All", true) == true && labels.size > 1) {
        val allEnabled = selectedIndex == 0
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ExpressiveSwitch(checked = allEnabled, onCheckedChange = { enabled -> onSelected(if (enabled) 0 else 1) })
                Column(Modifier.weight(1f)) {
                    Text("All", fontWeight = FontWeight.SemiBold, color = VulkanTextPrimary)
                    Text(if (allEnabled) "All entries are shown; specific filters are locked." else "Specific filter selection is enabled.", color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall)
                }
            }
            ExpressiveSingleFilterSelector(
                labels = labels.drop(1),
                selectedIndex = if (selectedIndex <= 0) null else selectedIndex - 1,
                enabled = !allEnabled,
                indicatorTint = arrowTint,
                onSelected = { onSelected(it + 1) }
            )
        }
    } else {
        ExpressiveSingleFilterSelector(labels, selectedIndex, true, arrowTint, onSelected)
    }
}

@Composable
internal fun ExpressiveMultiFilterBar(labels: List<String>, selectedLabels: Set<String>, onToggle: (String) -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var contentMounted by remember { mutableStateOf(false) }
    var contentVisible by remember { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    val showSearch = labels.size >= 5
    val visibleLabels = remember(labels, query) {
        labels.filter { query.isBlank() || it.contains(query, ignoreCase = true) }
    }
    val summary = when (selectedLabels.size) {
        0 -> "No filters selected"
        1 -> selectedLabels.firstOrNull() ?: "1 filter selected"
        else -> "${selectedLabels.size} filters selected"
    }
    val arrowRotation by animateFloatAsState(if (expanded) 180f else 0f, animationSpec = tween(180), label = "multiFilterArrow")
    LaunchedEffect(expanded) {
        if (expanded) {
            contentMounted = true
            contentVisible = false
            delay(20)
            contentVisible = true
        } else {
            contentVisible = false
            delay(190)
            contentMounted = false
        }
    }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        val selectorShape = MaterialTheme.shapes.medium
        Surface(
            shape = selectorShape,
            color = VulkanSurfaceTonal,
            contentColor = VulkanTextPrimary,
            border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutline),
            modifier = Modifier.fillMaxWidth().heightIn(min = 58.dp)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("Filters", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                    Text(
                        trademarkVulkanDisplayText(summary),
                        color = VulkanTextPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                val arrowShape = RoundedCornerShape(15.dp)
                Surface(
                    shape = arrowShape,
                    color = if (expanded) VulkanAccent else VulkanAccentContainer,
                    contentColor = VulkanTextPrimary,
                    modifier = Modifier.size(44.dp).clip(arrowShape).clickable(role = Role.Button) { expanded = !expanded }
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(R.drawable.ic_expand_more),
                            contentDescription = if (expanded) "Close filter menu" else "Open filter menu",
                            tint = VulkanTextPrimary,
                            modifier = Modifier.size(24.dp).graphicsLayer(rotationZ = arrowRotation)
                        )
                    }
                }
            }
        }
        if (contentMounted) {
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(220)) + scaleIn(tween(240), initialScale = 0.97f, transformOrigin = TransformOrigin(0.5f, 0f)) + expandVertically(tween(220), expandFrom = Alignment.Top),
                exit = fadeOut(tween(150)) + scaleOut(tween(170), targetScale = 0.985f, transformOrigin = TransformOrigin(0.5f, 0f)) + shrinkVertically(tween(170), shrinkTowards = Alignment.Top)
            ) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = VulkanSurfaceRaised,
                    contentColor = VulkanTextPrimary,
                    border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutline),
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth().heightIn(max = 620.dp)
                ) {
                    Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (showSearch) {
                            ExpressiveSearchField(
                                value = query,
                                onValueChange = { query = it.take(120) },
                                modifier = Modifier.fillMaxWidth(),
                                labelText = "Search filters",
                                placeholderText = "Type a filter name…"
                            )
                            Text(
                                "${visibleLabels.size} result${if (visibleLabels.size == 1) "" else "s"}",
                                color = VulkanTextSecondary,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        val listState = rememberLazyListState()
                        val boundaryScrollConnection = rememberFilterScrollBoundaryConnection()
                        Box(Modifier.fillMaxWidth().heightIn(min = 56.dp, max = 420.dp).nestedScroll(boundaryScrollConnection)) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxWidth().desktopVerticalPointerScroll(listState).tvRemoteLazyListNavigation(listState).focusGroup(),
                                verticalArrangement = Arrangement.spacedBy(5.dp),
                                contentPadding = PaddingValues(vertical = 2.dp)
                            ) {
                                items(visibleLabels, key = { it }) { label ->
                                    val selected = label in selectedLabels
                                    val rowShape = RoundedCornerShape(18.dp)
                                    Surface(
                                        shape = rowShape,
                                        color = if (selected) VulkanAccentContainer else ComposeColor.Transparent,
                                        contentColor = if (selected) VulkanTextPrimary else VulkanTextSecondary,
                                        modifier = Modifier.fillMaxWidth().clip(rowShape).clickable(role = Role.Checkbox) { onToggle(label) }.then(tvBrowseModifier(rowShape))
                                    ) {
                                        Row(
                                            Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Checkbox(
                                                checked = selected,
                                                onCheckedChange = { onToggle(label) },
                                                colors = CheckboxDefaults.colors(
                                                    checkedColor = VulkanAccent,
                                                    checkmarkColor = VulkanTextPrimary,
                                                    uncheckedColor = VulkanOutline
                                                )
                                            )
                                            Text(
                                                trademarkVulkanDisplayText(label),
                                                modifier = Modifier.weight(1f),
                                                color = if (selected) VulkanTextPrimary else VulkanTextSecondary,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                                if (visibleLabels.isEmpty()) {
                                    item {
                                        Text(
                                            "No filter matches this search.",
                                            color = VulkanTextMuted,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 16.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                            ExpressiveScrollHints(listState, Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun ExpressiveToggleRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ExpressiveSwitch(checked = checked, onCheckedChange = onCheckedChange)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(trademarkVulkanDisplayText(title), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(trademarkVulkanDisplayText(subtitle), style = MaterialTheme.typography.labelSmall, color = VulkanTextSecondary)
        }
    }
}

@Composable
internal fun ExpressiveMetric(label: String, value: String, modifier: Modifier = Modifier) {
    val shape = MaterialTheme.shapes.large
    Surface(
        shape = shape,
        color = VulkanAccentContainer,
        contentColor = VulkanTextPrimary,
        border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.34f)),
        modifier = modifier.animateContentSize(animationSpec = tween(220, easing = FastOutSlowInEasing)).then(tvBrowseModifier(shape))
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 13.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(trademarkVulkanDisplayText(label), color = VulkanAccentSoft, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text(trademarkVulkanDisplayText(value), color = VulkanTextPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
internal fun ExpressiveMetricGrid(metrics: List<Pair<String, String>>, modifier: Modifier = Modifier) {
    if (metrics.isEmpty()) return
    val fontScale = LocalDensity.current.fontScale
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val columns = when {
            maxWidth < 300.dp || (fontScale >= 1.55f && maxWidth < 420.dp) -> 1
            maxWidth < 760.dp -> 2
            else -> 3
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            metrics.chunked(columns).forEach { rowMetrics ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowMetrics.forEach { (label, value) -> ExpressiveMetric(label, value, Modifier.weight(1f)) }
                    repeat(columns - rowMetrics.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
internal fun ExpressiveStatus(state: String) {
    val positive = when (state.uppercase()) {
        "PASS", "SATISFIED", "SUPPORTED", "AVAILABLE", "COMPATIBLE EVIDENCE", "NO REJECTION", "CLEAR" -> true
        "FAIL", "NOT SATISFIED", "UNSUPPORTED", "SAFETY REJECTED", "DEDUCTION" -> false
        else -> null
    }
    CapabilityStatusBadge(state, positive)
}

internal fun copyEvidenceText(context: Context, label: String, text: String) {
    (context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager)?.setPrimaryClip(android.content.ClipData.newPlainText(label, text))
}

internal fun shareEvidenceText(context: Context, text: String) {
    runCatching { context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text), "Share VulkanScope evidence")) }
}

@Composable
internal fun EvidenceInspectorDialog(key: String, value: String, onDismiss: () -> Unit, onWatch: ((String) -> Unit)? = null) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val environment = LocalEvidenceActionEnvironment.current
    val provenance = remember(key, value) { evidenceProvenance(key, value) }
    val referenceToken = remember(key, value) { evidenceTokenForReference(key, value) }
    val resolvedWatch = onWatch ?: environment?.addWatch
    ExpressiveDetailDialog("Evidence provenance", onDismiss) {
        ExpressiveEvidenceRow("Evidence key", key)
        ExpressiveEvidenceRow("Value", value.ifBlank { "Unavailable" })
        ExpressiveEvidenceRow("Evidence class", provenance.evidenceClass)
        ExpressiveEvidenceRow("Source", provenance.source)
        ExpressiveEvidenceRow("Query/API path", provenance.queryPath)
        ExpressiveEvidenceRow("Query group", provenance.queryGroup)
        ExpressiveEvidenceRow("Registry relationship", provenance.registryRelation)
        Text(trademarkVulkanDisplayText(provenance.interpretation), color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
        TransientActionButton("Copy name + value", "Copy the exact displayed evidence pair", R.drawable.ic_copy) { runCatching { copyEvidenceText(context, "VulkanScope evidence", "$key = $value") }.isSuccess }
        ExpressiveActionButton("Share evidence", "Android Sharesheet · explicit user action", R.drawable.ic_share, trailingIcon = R.drawable.ic_open_external) { shareEvidenceText(context, "$key = $value") }
        TransientActionButton("Add to watched evidence", "Local bounded watch list", R.drawable.ic_watch_add, enabled = referenceToken.isNotBlank() && resolvedWatch != null, idleTrailingIcon = R.drawable.ic_add) { runCatching { resolvedWatch?.invoke(referenceToken) }.isSuccess }
        ExpressiveActionButton("Open in Encyclopedia", "Open the closest Vulkan symbol/reference token", R.drawable.ic_book, enabled = referenceToken.isNotBlank() && environment != null) { environment?.openEncyclopedia?.invoke(referenceToken) }
    }
}

@Composable
internal fun ExpressiveEvidenceRow(key: String, value: String) {
    val expandedTextLayout = preferExpandedTextLayout()
    val detailPresentation = LocalDetailKeyValuePresentation.current
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val stacked = if (detailPresentation) {
            expandedTextLayout || maxWidth < 420.dp || key.length > 22 || value.length > 30 || value.contains("\n")
        } else {
            expandedTextLayout || maxWidth < 360.dp || key.length > 26 || value.length > 34 || value.contains("\n")
        }
        val shape: Shape = if (detailPresentation) MaterialTheme.shapes.medium else RoundedCornerShape(16.dp)
        Surface(
            color = VulkanSurfaceTonal,
            shape = shape,
            border = if (detailPresentation) androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant) else null,
            modifier = Modifier.fillMaxWidth().then(tvBrowseModifier(shape)).semantics(mergeDescendants = true) { }
        ) {
            if (stacked) {
                Column(
                    Modifier.fillMaxWidth().padding(horizontal = if (detailPresentation) 13.dp else 14.dp, vertical = if (detailPresentation) 10.dp else 11.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(trademarkVulkanDisplayText(key), color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall, fontWeight = if (detailPresentation) FontWeight.SemiBold else FontWeight.Normal)
                    Text(trademarkVulkanDisplayText(value.ifBlank { "Unavailable" }), color = ComposeColor(0xFFE7DFE1), style = if (detailPresentation) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall)
                }
            } else {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = if (detailPresentation) 13.dp else 14.dp, vertical = if (detailPresentation) 10.dp else 11.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(trademarkVulkanDisplayText(key), color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(0.88f))
                    Text(trademarkVulkanDisplayText(value.ifBlank { "Unavailable" }), color = ComposeColor(0xFFE7DFE1), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1.12f))
                }
            }
        }
    }
}

@Composable
internal fun CapabilityKeyValue(key: String, value: String) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val environment = LocalEvidenceActionEnvironment.current
    val referenceToken = remember(key, value) { evidenceTokenForReference(key, value) }
    var showEvidenceActions by remember(key, value) { mutableStateOf(false) }
    var showQuickMenu by remember(key, value) { mutableStateOf(false) }
    var tvLongPressConsumed by remember(key, value) { mutableStateOf(false) }
    var tvLongPressJob by remember(key, value) { mutableStateOf<Job?>(null) }
    val tvLongPressScope = rememberCoroutineScope()
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isTelevision = configuration.uiMode and Configuration.UI_MODE_TYPE_MASK == Configuration.UI_MODE_TYPE_TELEVISION
    val suppressDesktopQuickMenu = remember(context) {
        isChromeOsRuntime(context) || isAndroidPcFormFactor(context) || hasFreeformWindowManagement(context)
    }
    var pressed by remember(key, value) { mutableStateOf(false) }
    val pressScale by animateFloatAsState(if (pressed) 0.985f else 1f, tween(110), label = "evidenceHoldScale")
    val pressHighlightAlpha by animateFloatAsState(if (pressed) 0.58f else 0f, tween(110), label = "evidenceHoldHighlight")
    val pressBorderAlpha by animateFloatAsState(if (pressed) 0.46f else 0f, tween(140, easing = FastOutSlowInEasing), label = "evidenceHoldBorder")
    Box {
        Box(
            Modifier.fillMaxWidth()
                .graphicsLayer(scaleX = pressScale, scaleY = pressScale)
                .background(VulkanAccentContainer.copy(alpha = pressHighlightAlpha), RoundedCornerShape(14.dp))
                .border(1.dp, VulkanAccentSoft.copy(alpha = pressBorderAlpha), RoundedCornerShape(14.dp))
                .consumeDesktopSecondaryMouseInput(suppressDesktopQuickMenu)
                .onPreviewKeyEvent { event ->
                    if (!isTelevision) return@onPreviewKeyEvent false
                    val nativeKeyCode = event.key.nativeKeyCode
                    val activationKey = event.key == Key.DirectionCenter ||
                        event.key == Key.Enter ||
                        event.key == Key.NumPadEnter ||
                        nativeKeyCode == AndroidKeyEvent.KEYCODE_BUTTON_SELECT ||
                        nativeKeyCode == AndroidKeyEvent.KEYCODE_BUTTON_A
                    if (!activationKey) return@onPreviewKeyEvent false
                    when (event.type) {
                        KeyEventType.KeyDown -> {
                            if (tvLongPressConsumed) {
                                true
                            } else {
                                val existingJob = tvLongPressJob
                                if (existingJob == null || !existingJob.isActive) {
                                    tvLongPressJob = tvLongPressScope.launch {
                                        delay(550L)
                                        showEvidenceActions = true
                                        tvLongPressConsumed = true
                                    }
                                    false
                                } else {
                                    false
                                }
                            }
                        }
                        KeyEventType.KeyUp -> {
                            val consumed = tvLongPressConsumed
                            tvLongPressJob?.cancel()
                            tvLongPressJob = null
                            tvLongPressConsumed = false
                            consumed
                        }
                        else -> false
                    }
                }
                .pointerInput(key, value) {
                    detectTapGestures(
                        onPress = {
                            pressed = true
                            try {
                                tryAwaitRelease()
                            } finally {
                                pressed = false
                            }
                        },
                        onLongPress = { showEvidenceActions = true }
                    )
                }
                .pointerInput(key, value, suppressDesktopQuickMenu) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Main)
                            if (event.type == PointerEventType.Press && event.buttons.isSecondaryPressed) {
                                if (!suppressDesktopQuickMenu) showQuickMenu = true
                                event.changes.forEach { change -> if (!change.isConsumed) change.consume() }
                            }
                        }
                    }
                }
                .semantics {
                    customActions = listOf(CustomAccessibilityAction("Evidence actions") { showEvidenceActions = true; true })
                }
        ) {
            ExpressiveEvidenceRow(key, value)
        }
        if (!suppressDesktopQuickMenu) {
            DropdownMenu(expanded = showQuickMenu, onDismissRequest = { showQuickMenu = false }) {
                DropdownMenuItem(
                    text = { Text("Copy name + value") },
                    onClick = {
                        showQuickMenu = false
                        runCatching { copyEvidenceText(context, "VulkanScope evidence", "$key = $value") }
                    }
                )
                DropdownMenuItem(
                    text = { Text("Share evidence") },
                    onClick = {
                        showQuickMenu = false
                        shareEvidenceText(context, "$key = $value")
                    }
                )
                DropdownMenuItem(
                    text = { Text("Add to watched evidence") },
                    enabled = referenceToken.isNotBlank() && environment?.addWatch != null,
                    onClick = {
                        showQuickMenu = false
                        environment?.addWatch?.invoke(referenceToken)
                    }
                )
                DropdownMenuItem(
                    text = { Text("Open in Encyclopedia") },
                    enabled = referenceToken.isNotBlank() && environment?.openEncyclopedia != null,
                    onClick = {
                        showQuickMenu = false
                        environment?.openEncyclopedia?.invoke(referenceToken)
                    }
                )
                DropdownMenuItem(
                    text = { Text("More details") },
                    onClick = {
                        showQuickMenu = false
                        showEvidenceActions = true
                    }
                )
            }
        }
    }
    if (showEvidenceActions) EvidenceInspectorDialog(key, value, onDismiss = { showEvidenceActions = false })
}

internal fun evidenceStateAccent(state: String): ComposeColor = when (state.trim().uppercase().replace('_', ' ')) {
    "SUPPORTED", "PASS" -> ComposeColor(0xFF73C991)
    "AVAILABLE" -> ComposeColor(0xFF7CC4FF)
    "UNSUPPORTED", "FAIL" -> ComposeColor(0xFFFF8A8A)
    "UNAVAILABLE" -> ComposeColor(0xFFFFC857)
    "INCOMPLETE" -> ComposeColor(0xFFD3A4FF)
    "NOT APPLICABLE" -> ComposeColor(0xFFC4C4C4)
    "UNKNOWN", "UNRESOLVED" -> ComposeColor(0xFFA8A8A8)
    else -> VulkanTextSecondary
}

@Composable
internal fun CapabilityStatusBadge(label: String, positive: Boolean? = null) {
    val normalized = label.trim().uppercase().replace('_', ' ')
    val available = positive == null && (normalized == "AVAILABLE" || normalized.startsWith("AVAILABLE "))
    val unknown = positive == null && (normalized == "UNKNOWN" || normalized.startsWith("UNKNOWN ") || normalized == "UNRESOLVED")
    val unavailable = positive == null && (normalized == "UNAVAILABLE" || normalized.startsWith("UNAVAILABLE "))
    val incomplete = positive == null && normalized == "INCOMPLETE"
    val notApplicable = positive == null && (normalized == "NOT APPLICABLE" || normalized.startsWith("NOT APPLICABLE "))
    val background = when {
        positive == true -> ComposeColor(0xFF173421)
        positive == false -> ComposeColor(0xFF3A1D20)
        available -> ComposeColor(0xFF172B3A)
        unavailable -> ComposeColor(0xFF332A16)
        incomplete -> ComposeColor(0xFF30243A)
        notApplicable -> ComposeColor(0xFF272727)
        unknown -> ComposeColor(0xFF252525)
        else -> VulkanSurfaceTonal
    }
    val foreground = when {
        positive == true -> ComposeColor(0xFF73C991)
        positive == false -> ComposeColor(0xFFFF8A8A)
        available -> ComposeColor(0xFF7CC4FF)
        unavailable -> ComposeColor(0xFFFFC857)
        incomplete -> ComposeColor(0xFFD3A4FF)
        notApplicable -> ComposeColor(0xFFC4C4C4)
        unknown -> ComposeColor(0xFFA8A8A8)
        else -> VulkanTextSecondary
    }
    Surface(shape = RoundedCornerShape(999.dp), color = background) {
        Text(label, color = foreground, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp))
    }
}

@Composable
internal fun ActionButtonIconArtwork(title: String, icon: Int, tint: ComposeColor) {
    when {
        title.equals("Run guided System ↔ Turnip A/B", true) -> {
            CompositeActionVectorIcon(R.drawable.ic_cpu, R.drawable.ic_compare, tint)
            return
        }
        title.equals("Switch to System and collect", true) -> {
            CompositeAndroidActionIcon(R.drawable.ic_compare, tint)
            return
        }
        title.equals("Switch to Turnip and collect", true) -> {
            CompositeMesaActionIcon(R.drawable.ic_compare, tint)
            return
        }
    }
    val badge = when {
        title.equals("Export TXT", true) -> "TXT"
        title.equals("Export HTML", true) -> "HTML"
        title.equals("Export JSON", true) -> "JSON"
        else -> null
    }
    if (badge == null) {
        Icon(painter = painterResource(icon), contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        return
    }
    Box(Modifier.size(24.dp)) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.align(Alignment.TopStart).size(19.dp)
        )
        Surface(
            shape = RoundedCornerShape(3.dp),
            color = VulkanSurface,
            modifier = Modifier.align(Alignment.BottomEnd).border(0.7.dp, tint, RoundedCornerShape(3.dp))
        ) {
            Text(
                badge,
                color = tint,
                fontSize = if (badge == "HTML" || badge == "JSON") 3.7.sp else 4.4.sp,
                lineHeight = if (badge == "HTML" || badge == "JSON") 4.0.sp else 4.7.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(horizontal = if (badge == "HTML" || badge == "JSON") 0.8.dp else 1.0.dp, vertical = 0.7.dp)
            )
        }
    }
}

@Composable
internal fun CompositeActionVectorIcon(primary: Int, overlay: Int, tint: ComposeColor) {
    Box(Modifier.size(24.dp)) {
        Icon(painterResource(primary), contentDescription = null, tint = tint, modifier = Modifier.align(Alignment.TopStart).size(19.dp))
        Surface(shape = RoundedCornerShape(4.dp), color = VulkanSurface, modifier = Modifier.align(Alignment.BottomEnd)) {
            Icon(painterResource(overlay), contentDescription = null, tint = tint, modifier = Modifier.padding(1.2.dp).size(9.dp))
        }
    }
}

@Composable
internal fun CompositeAndroidActionIcon(overlay: Int, tint: ComposeColor) {
    Box(Modifier.size(24.dp)) {
        Image(painterResource(R.drawable.ic_android), contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier.align(Alignment.TopStart).width(20.dp).height(14.dp))
        Surface(shape = RoundedCornerShape(4.dp), color = VulkanSurface, modifier = Modifier.align(Alignment.BottomEnd)) {
            Icon(painterResource(overlay), contentDescription = null, tint = tint, modifier = Modifier.padding(1.2.dp).size(9.dp))
        }
    }
}

@Composable
internal fun CompositeMesaActionIcon(overlay: Int, tint: ComposeColor) {
    Box(Modifier.size(24.dp)) {
        Image(painterResource(R.drawable.mesa3d_logo), contentDescription = null, contentScale = ContentScale.Fit, colorFilter = ColorFilter.tint(VulkanAccentSoft), modifier = Modifier.align(Alignment.TopStart).size(20.dp))
        Surface(shape = RoundedCornerShape(4.dp), color = VulkanSurface, modifier = Modifier.align(Alignment.BottomEnd)) {
            Icon(painterResource(overlay), contentDescription = null, tint = tint, modifier = Modifier.padding(1.2.dp).size(9.dp))
        }
    }
}

@Composable
internal fun ExpressiveActionButton(
    title: String,
    subtitle: String,
    icon: Int,
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true,
    compact: Boolean = false,
    trailingIcon: Int = R.drawable.ic_chevron_right,
    trailingTint: ComposeColor? = null,
    onClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    val shape = if (compact) MaterialTheme.shapes.large else MaterialTheme.shapes.largeIncreased
    val container = if (!enabled) ComposeColor(0xFF101010) else VulkanSurfaceRaised
    val iconContainer = if (enabled) VulkanAccentContainer else ComposeColor(0xFF1C1C1C)
    val accent = if (enabled) VulkanAccentSoft else ComposeColor(0xFF989092)
    val titleColor = if (enabled) VulkanTextPrimary else ComposeColor(0xFF9C9697)
    val detailColor = if (enabled) VulkanTextSecondary else ComposeColor(0xFF898485)
    VulkanPremiumPanel(
        color = container,
        shape = shape,
        selected = focused && enabled,
        modifier = modifier
    ) {
        if (compact) {
            Column(
                Modifier.fillMaxWidth().padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(17.dp), color = iconContainer) {
                        Box(Modifier.padding(9.dp)) { ActionButtonIconArtwork(title, icon, accent) }
                    }
                    IconButton(
                        onClick = onClick,
                        enabled = enabled,
                        modifier = Modifier.size(48.dp).onFocusChanged { focused = it.isFocused },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = if (enabled) VulkanAccentContainer else ComposeColor(0xFF191919), contentColor = accent, disabledContainerColor = ComposeColor(0xFF191919), disabledContentColor = accent)
                    ) {
                        AnimatedContent(targetState = trailingIcon, label = "actionTrailingIconCompact") { currentIcon ->
                            Icon(painter = painterResource(currentIcon), contentDescription = trademarkVulkanDisplayText(title), tint = trailingTint ?: accent, modifier = Modifier.size(18.dp))
                        }
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(trademarkVulkanDisplayText(title), color = titleColor, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(trademarkVulkanDisplayText(subtitle), color = detailColor, style = MaterialTheme.typography.labelSmall)
                }
            }
        } else {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 17.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(shape = RoundedCornerShape(18.dp), color = iconContainer) {
                    Box(Modifier.padding(10.dp)) { ActionButtonIconArtwork(title, icon, accent) }
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(trademarkVulkanDisplayText(title), color = titleColor, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(trademarkVulkanDisplayText(subtitle), color = detailColor, style = MaterialTheme.typography.labelSmall)
                }
                IconButton(
                    onClick = onClick,
                    enabled = enabled,
                    modifier = Modifier.size(48.dp).onFocusChanged { focused = it.isFocused },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = if (enabled) VulkanAccentContainer else ComposeColor(0xFF191919), contentColor = accent, disabledContainerColor = ComposeColor(0xFF191919), disabledContentColor = accent)
                ) {
                    AnimatedContent(targetState = trailingIcon, label = "actionTrailingIcon") { currentIcon ->
                        Icon(painter = painterResource(currentIcon), contentDescription = trademarkVulkanDisplayText(title), tint = trailingTint ?: accent, modifier = Modifier.size(21.dp))
                    }
                }
            }
        }
    }
}

@Composable
internal fun ExpressiveExternalLinkRow(title: String, subtitle: String, icon: Int, enabled: Boolean = true, onOpen: () -> Unit) {
    VulkanPremiumPanel(color = if (enabled) VulkanSurfaceRaised else VulkanSurfaceLow, shape = MaterialTheme.shapes.largeIncreased, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 17.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Surface(shape = RoundedCornerShape(18.dp), color = if (enabled) ComposeColor(0xFF351719) else ComposeColor(0xFF181818)) {
                Icon(painterResource(icon), contentDescription = null, tint = if (enabled) VulkanAccentSoft else VulkanTextMuted, modifier = Modifier.padding(11.dp).size(22.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(trademarkVulkanDisplayText(title), color = if (enabled) VulkanTextPrimary else VulkanTextMuted, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyLarge)
                Text(trademarkVulkanDisplayText(subtitle), color = if (enabled) VulkanTextSecondary else VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
            }
            IconButton(onClick = onOpen, enabled = enabled, colors = IconButtonDefaults.iconButtonColors(containerColor = if (enabled) VulkanAccentContainer else VulkanSurfaceLow, contentColor = VulkanAccentSoft, disabledContentColor = VulkanTextMuted), modifier = Modifier.size(48.dp)) {
                Icon(painterResource(R.drawable.ic_open_external), contentDescription = "Open external link", modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
internal fun ExpressiveIdentityBlock(title: String, subtitle: String, icon: Int) {
    VulkanPremiumPanel(color = VulkanSurfaceRaised, shape = MaterialTheme.shapes.largeIncreased, modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().padding(17.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(shape = RoundedCornerShape(20.dp), color = ComposeColor(0xFF351719)) {
                Icon(painter = painterResource(icon), contentDescription = null, tint = ComposeColor(0xFFE2676A), modifier = Modifier.padding(12.dp).size(24.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(trademarkVulkanDisplayText(title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = ComposeColor(0xFFF7F2F3))
                Text(trademarkVulkanDisplayText(subtitle), style = MaterialTheme.typography.bodySmall, color = ComposeColor(0xFFB6ACAE))
            }
        }
    }
}

@Composable
internal fun ExpressiveVersionBlock(application: String, version: String, versionCode: String, packageName: String, abi: String) {
    val expandedTextLayout = preferExpandedTextLayout()
    VulkanPremiumPanel(color = VulkanSurfaceRaised, shape = MaterialTheme.shapes.extraLarge, emphasis = true, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                Surface(shape = RoundedCornerShape(19.dp), color = ComposeColor(0xFF351719)) {
                    Image(painter = painterResource(R.drawable.vulkanscope_logo_foreground), contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier.size(46.dp))
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text(application, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text("Version $version", color = ComposeColor(0xFFE98A8C), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                }
                Surface(shape = RoundedCornerShape(999.dp), color = ComposeColor(0xFF272224)) {
                    Text("#$versionCode", color = ComposeColor(0xFFC7BEC0), style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp))
                }
            }
            if (expandedTextLayout) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExpressiveInfoPill("Installed ABI", abi, Modifier.fillMaxWidth())
                    ExpressiveInfoPill("Package", packageName, Modifier.fillMaxWidth())
                }
            } else {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExpressiveInfoPill("Installed ABI", abi, Modifier.weight(1f))
                    ExpressiveInfoPill("Package", packageName, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
internal fun DatabaseDriverModePill(driverMode: String, modifier: Modifier = Modifier) {
    val isTurnip = driverMode.contains("turnip", ignoreCase = true)
    val value = driverMode.ifBlank { "Unknown" }
    VulkanPremiumPanel(color = VulkanSurfaceTonal, shape = MaterialTheme.shapes.medium, modifier = modifier) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text("Driver", color = ComposeColor(0xFF968D8F), style = MaterialTheme.typography.labelSmall)
            Text(
                trademarkVulkanDisplayText(value),
                color = if (isTurnip) VulkanAccentSoft else VulkanTextPrimary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

internal data class DatabaseSubmittedDisplay(val date: String, val time: String, val zone: String)

internal fun databaseSubmittedDisplay(context: Context, raw: String): DatabaseSubmittedDisplay? = runCatching {
    val instant = java.time.Instant.parse(raw.trim())
    val date = java.util.Date.from(instant)
    val timeZone = java.util.TimeZone.getDefault()
    val dateFormat = android.text.format.DateFormat.getMediumDateFormat(context).apply { this.timeZone = timeZone }
    val timeFormat = android.text.format.DateFormat.getTimeFormat(context).apply { this.timeZone = timeZone }
    DatabaseSubmittedDisplay(
        date = dateFormat.format(date),
        time = timeFormat.format(date),
        zone = timeZone.getDisplayName(timeZone.inDaylightTime(date), java.util.TimeZone.SHORT)
    )
}.getOrNull()

@Composable
internal fun DatabaseSubmittedAt(raw: String) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val display = remember(raw, context) { databaseSubmittedDisplay(context, raw) }
    if (display == null) {
        CapabilityKeyValue("Submitted", raw.ifBlank { "Unknown" })
    } else {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Submitted", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ExpressiveInfoPill("Date", display.date, Modifier.weight(1f))
                ExpressiveInfoPill("Time", display.time, Modifier.weight(1f))
            }
            Text("Device local time · ${display.zone}", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
internal fun ExpressiveInfoPill(label: String, value: String, modifier: Modifier = Modifier) {
    VulkanPremiumPanel(color = VulkanSurfaceTonal, shape = MaterialTheme.shapes.medium, modifier = modifier) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(trademarkVulkanDisplayText(label), color = ComposeColor(0xFF968D8F), style = MaterialTheme.typography.labelSmall)
            Text(trademarkVulkanDisplayText(value), color = ComposeColor(0xFFE7DFE1), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium, maxLines = 3, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
internal fun MetricCard(title: String, value: String, modifier: Modifier) {
    val shape = MaterialTheme.shapes.large
    VulkanPremiumPanel(
        color = VulkanAccentContainer,
        shape = shape,
        emphasis = true,
        modifier = modifier.animateContentSize(animationSpec = tween(220, easing = FastOutSlowInEasing)).then(tvBrowseModifier(shape))
    ) {
        Column(Modifier.padding(horizontal = 15.dp, vertical = 13.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(trademarkVulkanDisplayText(title), color = VulkanAccentSoft, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Text(trademarkVulkanDisplayText(value), color = VulkanTextPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, maxLines = 3, overflow = TextOverflow.Ellipsis)
        }
    }
}

internal fun formatBytes(value: Long): String {
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
