@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.efishell.vulkanscope

import android.graphics.Color
import android.content.res.Configuration
import android.os.Build
import android.view.Surface
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.geometry.Offset
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch





@Composable
internal fun VulkanPage(report: VulkanReport, device: DeviceReport?, turnipSupport: TurnipSupport) {
    VulkanLazyPage(verticalSpacing = 14.dp) {
        item { CapabilitySectionCard("Vulkan API") {
            CapabilityKeyValue("Loader API", report.loaderVersion)
            CapabilityKeyValue("Base probe instance API", report.instanceApiVersion)
            CapabilityKeyValue("Device API", device?.apiVersion ?: "Unknown")
            CapabilityKeyValue("Driver", device?.driverVersionText ?: device?.driverVersion ?: "Unknown")
            CapabilityKeyValue("Device type", device?.deviceType ?: "Unknown")
            CapabilityKeyValue("Vendor ID", device?.vendorId ?: "Unknown")
            CapabilityKeyValue("Device ID", device?.deviceId ?: "Unknown")
            CapabilityKeyValue("Physical-device enumeration result", report.physicalDeviceEnumerationResult?.let(::vkResultText) ?: "Not returned")
            CapabilityKeyValue("Physical-device enumeration complete", report.physicalDeviceEnumerationComplete.toString())
            CapabilityKeyValue("Physical-device enumeration safety rejection", if (report.physicalDeviceEnumerationSafetyRejected) "YES · local bound rejected the result" else "NO")
            if (report.physicalDeviceEnumerationReason.isNotBlank()) CapabilityKeyValue("Physical-device enumeration provenance", report.physicalDeviceEnumerationReason)
        } }
        item { CapabilitySectionCard("Physical device groups") {
            CapabilityKeyValue("Query status", report.instanceGroupStatus)
            if (report.instanceGroupReason.isNotBlank()) CapabilityKeyValue("Reason", report.instanceGroupReason)
            CapabilityKeyValue("Enumeration result", report.instanceGroupEnumerationResult?.let(::vkResultText) ?: "Unknown")
            CapabilityKeyValue("Enumeration complete", report.instanceGroupEnumerationComplete.toString())
            if (report.instanceGroupProperties.isEmpty()) {
                Text(if (report.instanceGroupEnumerationComplete) "No physical device groups were enumerated." else "No physical-device-group property payload is currently available.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
            } else {
                report.instanceGroupProperties.forEach { property -> CapabilityKeyValue(property.name, property.value) }
            }
        } }
        item { CapabilitySectionCard("Instance layers") {
            CapabilityKeyValue("Enumeration status", report.instanceLayerStatus)
            if (report.instanceLayerReason.isNotBlank()) CapabilityKeyValue("Enumeration reason", report.instanceLayerReason)
            if (report.instanceLayers.isEmpty()) {
                when (report.instanceLayerStatus) {
                    "available" -> {
                        Text("No instance layers are exposed by the active Vulkan® implementation.")
                        Text("This is normal on many Android production/driver configurations; validation layers are optional and are not bundled by VulkanScope.", color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.bodySmall)
                    }
                    "incomplete" -> Text("Instance-layer enumeration is incomplete; an empty list is not treated as proof that no layers exist.")
                    "unavailable" -> Text("Instance-layer enumeration is unavailable.")
                    else -> Text("Instance-layer enumeration state is unknown.")
                }
            }
            report.instanceLayers.forEach { layer ->
                CapabilityItemCard(containerColor = VulkanSurfaceTonal) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(layer.name, fontWeight = FontWeight.SemiBold)
                        Text("spec ${layer.specVersion} · implementation ${layer.implementationVersion}", color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall)
                        if (layer.description.isNotBlank()) Text(layer.description, color = VulkanTextMuted, style = MaterialTheme.typography.bodySmall)
                        Text("Extension enumeration: ${layer.extensionStatus}${if (layer.extensionsComplete) " · complete" else " · incomplete"}", color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall)
                        if (layer.extensionReason.isNotBlank()) Text(layer.extensionReason, color = VulkanTextMuted, style = MaterialTheme.typography.bodySmall)
                        layer.extensions.forEach { ext -> Text("${ext.name} · spec ${ext.specVersion}", color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall) }
                    }
                }
            }
        } }
        item { CapabilitySectionCard("Device layers") {
            CapabilityKeyValue("Enumeration status", device?.deviceLayerStatus ?: "unknown")
            CapabilityKeyValue("Enumeration complete", device?.deviceLayersComplete?.toString() ?: "false")
            if (!device?.deviceLayerReason.isNullOrBlank()) CapabilityKeyValue("Enumeration reason", device?.deviceLayerReason ?: "")
            if (device?.deviceLayers.isNullOrEmpty()) {
                when (device?.deviceLayerStatus) {
                    "available" -> {
                        Text("No device layers are exposed.")
                        Text("Device layers are legacy functionality; modern Vulkan® uses instance layers.", color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.bodySmall)
                    }
                    "incomplete" -> Text("Device-layer enumeration is incomplete; an empty list is not proof that no device layers exist.")
                    "unavailable" -> Text("Device-layer enumeration is unavailable.")
                    else -> Text("Device-layer enumeration state is unknown.")
                }
            }
            device?.deviceLayers?.forEach { layer ->
                CapabilityItemCard(containerColor = VulkanSurfaceTonal) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(layer.name, fontWeight = FontWeight.SemiBold)
                        Text("spec ${layer.specVersion} · implementation ${layer.implementationVersion}", color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall)
                        if (layer.description.isNotBlank()) Text(layer.description, color = VulkanTextMuted, style = MaterialTheme.typography.bodySmall)
                        Text("Extension enumeration: ${layer.extensionStatus}${if (layer.extensionsComplete) " · complete" else " · incomplete"}", color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall)
                        if (layer.extensionReason.isNotBlank()) Text(layer.extensionReason, color = VulkanTextMuted, style = MaterialTheme.typography.bodySmall)
                        layer.extensions.forEach { ext -> Text("${ext.name} · spec ${ext.specVersion}", color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall) }
                    }
                }
            }
        } }
        item { CapabilitySectionCard("Instance extensions") {
            CapabilityKeyValue("Enumeration status", report.instanceExtensionStatus)
            if (report.instanceExtensionReason.isNotBlank()) CapabilityKeyValue("Enumeration reason", report.instanceExtensionReason)
            if (report.instanceExtensions.isEmpty()) {
                when (report.instanceExtensionStatus) {
                    "available" -> EmptyState("No instance extensions exposed")
                    "incomplete" -> EmptyState("Instance-extension enumeration incomplete")
                    "unavailable" -> EmptyState("Instance-extension enumeration unavailable")
                    else -> EmptyState("Instance-extension enumeration unknown")
                }
            }
            report.instanceExtensions.sortedBy { it.name }.forEach { ext -> CapabilityKeyValue(ext.name, "spec ${ext.specVersion}") }
        } }
        item { CapabilitySectionCard("Operating system") {
            val context = androidx.compose.ui.platform.LocalContext.current
            CapabilityKeyValue("Platform", platformRuntimeName(context))
            CapabilityKeyValue("Host OS version", hostPlatformVersionEvidence(context))
            CapabilityKeyValue("ChromeOS ARC runtime", if (isChromeOsRuntime(context)) "Detected" else "Not detected")
            CapabilityKeyValue("Android PC form factor", if (isAndroidPcFormFactor(context)) "Detected" else "Not detected")
            CapabilityKeyValue("Freeform window management", if (hasFreeformWindowManagement(context)) "Detected" else "Not detected")
            CapabilityKeyValue("Googlebook environment", googlebookEnvironmentEvidence(context))
            CapabilityKeyValue("Googlebook OS version", googlebookOsVersionEvidence())
            CapabilityKeyValue("Architecture", Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown")
            CapabilityKeyValue("Version", Build.VERSION.RELEASE)
            CapabilityKeyValue("Codename", Build.VERSION.CODENAME)
            CapabilityKeyValue("SDK", Build.VERSION.SDK_INT.toString())
            CapabilityKeyValue("Build ID", Build.ID)
            CapabilityKeyValue("Build incremental", Build.VERSION.INCREMENTAL)
            CapabilityKeyValue("Security patch", Build.VERSION.SECURITY_PATCH)
            CapabilityKeyValue("Brand", Build.BRAND)
            CapabilityKeyValue("Manufacturer", Build.MANUFACTURER)
            CapabilityKeyValue("Product", Build.PRODUCT)
            CapabilityKeyValue("Device", Build.DEVICE)
            CapabilityKeyValue("Board", Build.BOARD)
            CapabilityKeyValue("Hardware", Build.HARDWARE)
            CapabilityKeyValue("Fingerprint", Build.FINGERPRINT)
        } }
        item { CapabilitySectionCard("Android runtime") {
            CapabilityKeyValue("Architecture", Build.SUPPORTED_ABIS.joinToString(", "))
            CapabilityKeyValue("Manufacturer", Build.MANUFACTURER)
            CapabilityKeyValue("Model", Build.MODEL)
            CapabilityKeyValue("Android", Build.VERSION.RELEASE)
            CapabilityKeyValue("SDK", Build.VERSION.SDK_INT.toString())
            CapabilityKeyValue("Build ID", Build.ID)
            CapabilityKeyValue("Build incremental", Build.VERSION.INCREMENTAL)
            CapabilityKeyValue("Turnip eligibility", when (turnipSupport) {
                TurnipSupport.SUPPORTED -> "Eligible: arm64-v8a + Qualcomm Adreno Vulkan evidence detected"
                TurnipSupport.UNSUPPORTED -> "Not eligible: current platform/Vulkan® evidence does not satisfy the Turnip gate"
                TurnipSupport.UNKNOWN -> "Unknown: waiting for complete platform/Vulkan® evidence"
            })
        } }
        item { CapabilitySectionCard("Feature coverage") {
            val supported = device?.features?.count { it.supported } ?: 0
            val total = device?.features?.size ?: 0
            CapabilityKeyValue("Queried feature fields", "$supported / $total supported")
            CapabilityKeyValue("Instance extensions", when (report.instanceExtensionStatus) {
                "available" -> report.instanceExtensions.size.toString()
                "incomplete" -> "${report.instanceExtensions.size} retained · Incomplete"
                "unavailable" -> "Unavailable"
                else -> "Unknown"
            })
            CapabilityKeyValue("Instance extension query", report.instanceExtensionStatus)
            CapabilityKeyValue("Device extensions", when (device?.deviceExtensionStatus) {
                "available" -> device.extensions.size.toString()
                "incomplete" -> "${device.extensions.size} retained · Incomplete"
                "unavailable" -> "Unavailable"
                else -> "Unknown"
            })
        } }
    }
}



@Composable
internal fun HeroCard(device: DeviceReport?, report: VulkanReport, driverMode: DriverMode, onDriverDetails: () -> Unit) {
    Surface(color = VulkanSurfaceRaised, shape = MaterialTheme.shapes.extraLargeIncreased, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                VendorLogo(device?.vendorIdRaw, Modifier.size(82.dp))
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(device?.name ?: if (report.error != null) "Vulkan® unavailable" else "Vulkan® device unavailable", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(if (device != null) vendorInfo(device.vendorIdRaw).name else report.error?.take(120) ?: "Unknown vendor", color = ComposeColor(0xFFBDBDBD), maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(
                        driverMode.label,
                        color = if (driverMode == DriverMode.TURNIP) VulkanAccentSoft else VulkanTextPrimary,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (device != null) {
                CapabilityKeyValue("Vendor ID", device.vendorId)
                Text("GPU name, vendor ID and device ID are read from VkPhysicalDeviceProperties returned by the active Vulkan® implementation; the vendor label is only a presentation mapping of the numeric vendorID.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    DetailAffordance(onDriverDetails)
                }
            }
        }
    }
}



@Composable
internal fun ExpressiveDestinationCard(title: String, subtitle: String, icon: Int, onClick: () -> Unit) {
    val shape = RoundedCornerShape(24.dp)
    Surface(
        shape = shape,
        color = ComposeColor(0xFF1B171A),
        border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant.copy(alpha = 0.62f)),
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 17.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(shape = RoundedCornerShape(16.dp), color = ComposeColor(0xFF352024), border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.28f))) {
                Icon(painterResource(icon), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.padding(12.dp).size(22.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(trademarkVulkanDisplayText(title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = VulkanTextPrimary, maxLines = 2)
                Text(trademarkVulkanDisplayText(subtitle), style = MaterialTheme.typography.bodySmall, color = VulkanTextSecondary, maxLines = 3, overflow = TextOverflow.Ellipsis)
            }
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.iconButtonColors(containerColor = ComposeColor(0xFF352024), contentColor = VulkanAccentSoft)
            ) {
                Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = "Open ${trademarkVulkanDisplayText(title)}", tint = VulkanAccentSoft, modifier = Modifier.size(21.dp))
            }
        }
    }
}

@Composable
internal fun OverviewDestinationCard(title: String, subtitle: String, destination: Page, navigate: (Page) -> Unit) {
    ExpressiveDestinationCard(title, subtitle, pageIcon(destination)) { navigate(destination) }
}

@Composable
internal fun QuickAccessCard(title: String, destination: Page, navigate: (Page) -> Unit, modifier: Modifier) {
    Card(
        onClick = { navigate(destination) },
        colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFF1A1718)),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.heightIn(min = 72.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 9.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
        ) {
            if (title == "HDR & Color") {
                DisplaySectionBadgeIcon("HDR")
            } else {
                Icon(
                    painterResource(pageIcon(destination)),
                    contentDescription = null,
                    modifier = Modifier.size(19.dp),
                    tint = ComposeColor(0xFFE2676A)
                )
            }
            Text(
                title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}



@Composable
internal fun SystemNavigationBackdrop(
    backdropLayer: GraphicsLayer,
    backdropRootOffset: Offset,
    modifier: Modifier = Modifier
) {
    val navigationInsets = WindowInsets.navigationBars.asPaddingValues()
    val layoutDirection = LocalLayoutDirection.current
    val bottomInset = navigationInsets.calculateBottomPadding()
    val leftInset = navigationInsets.calculateLeftPadding(layoutDirection)
    val rightInset = navigationInsets.calculateRightPadding(layoutDirection)
    Box(modifier) {
        if (bottomInset > 0.dp) {
            var targetRootOffset by remember { mutableStateOf(Offset.Zero) }
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(bottomInset)
                    .onGloballyPositioned { targetRootOffset = it.positionInRoot() }
                    .frostedChromeBackdrop(backdropLayer, backdropRootOffset, targetRootOffset)
            )
        } else {
            if (leftInset > 0.dp) {
                var targetRootOffset by remember { mutableStateOf(Offset.Zero) }
                Box(
                    Modifier
                        .align(if (layoutDirection == LayoutDirection.Ltr) Alignment.CenterStart else Alignment.CenterEnd)
                        .fillMaxHeight()
                        .width(leftInset)
                        .onGloballyPositioned { targetRootOffset = it.positionInRoot() }
                        .frostedChromeBackdrop(backdropLayer, backdropRootOffset, targetRootOffset)
                )
            }
            if (rightInset > 0.dp) {
                var targetRootOffset by remember { mutableStateOf(Offset.Zero) }
                Box(
                    Modifier
                        .align(if (layoutDirection == LayoutDirection.Ltr) Alignment.CenterEnd else Alignment.CenterStart)
                        .fillMaxHeight()
                        .width(rightInset)
                        .onGloballyPositioned { targetRootOffset = it.positionInRoot() }
                        .frostedChromeBackdrop(backdropLayer, backdropRootOffset, targetRootOffset)
                )
            }
        }
    }
}

@Composable
internal fun CompactBottomNavigationBar(
    selectedPage: Page,
    onPageSelected: (Page) -> Unit,
    backdropLayer: GraphicsLayer,
    backdropRootOffset: Offset,
    modifier: Modifier = Modifier
) {
    val items = navigationItems()
    val navigationBarInsets = WindowInsets.navigationBars.asPaddingValues()
    val layoutDirection = LocalLayoutDirection.current
    val bottomSystemInset = navigationBarInsets.calculateBottomPadding()
    val startSystemInset = navigationBarInsets.calculateLeftPadding(layoutDirection)
    val endSystemInset = navigationBarInsets.calculateRightPadding(layoutDirection)
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val compactMaxWidth = if (landscape) 404.dp else PrimaryNavigationMaxWidth
    val compactHeight = if (landscape) 52.dp else PrimaryNavigationHeight
    val compactIndicatorHeight = if (landscape) 40.dp else PrimaryNavigationIndicatorHeight
    val compactIndicatorInset = if (landscape) 6.dp else PrimaryNavigationIndicatorHorizontalInset
    val compactIconSize = if (landscape) 18.dp else 20.dp
    val compactLabelFontSize = if (landscape) 9.sp else 9.sp
    val compactLabelLineHeight = if (landscape) 9.sp else 10.sp
    val compactItemTopPadding = if (landscape) 2.dp else 4.dp
    val compactItemBottomPadding = if (landscape) 2.dp else 3.dp
    val compactLabelSpacing = if (landscape) 1.dp else 2.dp
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp + startSystemInset, end = 16.dp + endSystemInset, bottom = PrimaryNavigationBottomGap + bottomSystemInset),
        contentAlignment = Alignment.BottomCenter
    ) {
        val targetWidth = maxWidth.coerceAtMost(compactMaxWidth)
        val navigationShape = RoundedCornerShape(999.dp)
        var navigationRootOffset by remember { mutableStateOf(Offset.Zero) }
        Surface(
            color = ComposeColor.Transparent,
            contentColor = VulkanTextPrimary,
            shape = navigationShape,
            border = androidx.compose.foundation.BorderStroke(1.dp, VulkanGlassBorder),
            shadowElevation = 10.dp,
            modifier = Modifier
                .width(targetWidth)
                .height(compactHeight)
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .clip(navigationShape)
                    .onGloballyPositioned { navigationRootOffset = it.positionInRoot() }
                    .frostedChromeBackdrop(backdropLayer, backdropRootOffset, navigationRootOffset)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                items.forEach { item ->
                    val selected = item.page == selectedPage
                    val interactionSource = remember(item.page) { MutableInteractionSource() }
                    val indicatorAlpha by animateFloatAsState(
                        targetValue = if (selected) 1f else 0f,
                        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
                        label = "navigationIndicatorAlpha"
                    )
                    val indicatorScale by animateFloatAsState(
                        targetValue = if (selected) 1f else 0.86f,
                        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
                        label = "navigationIndicatorScale"
                    )
                    val iconScale by animateFloatAsState(
                        targetValue = if (selected) 1f else 0.94f,
                        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
                        label = "navigationIconScale"
                    )
                    val iconColor by animateColorAsState(
                        targetValue = if (selected) VulkanAccentSoft else ComposeColor(0xFFE3DEE0),
                        animationSpec = tween(durationMillis = 120),
                        label = "navigationIconTint"
                    )
                    val textColor by animateColorAsState(
                        targetValue = if (selected) VulkanAccentSoft else ComposeColor(0xFFE9E3E6),
                        animationSpec = tween(durationMillis = 120),
                        label = "navigationTextTint"
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .selectable(
                                selected = selected,
                                interactionSource = interactionSource,
                                indication = null,
                                role = Role.Tab,
                                onClick = { onPageSelected(item.page) }
                            )
                            .then(tvBrowseModifier(RoundedCornerShape(999.dp))),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = compactIndicatorInset)
                                .height(compactIndicatorHeight)
                                .graphicsLayer {
                                    alpha = indicatorAlpha
                                    scaleX = indicatorScale
                                    scaleY = 0.96f + indicatorAlpha * 0.04f
                                }
                                .clip(RoundedCornerShape(999.dp))
                                .background(VulkanAccentContainer.copy(alpha = 0.82f))
                        )
                        Column(
                            modifier = Modifier.padding(top = compactItemTopPadding, bottom = compactItemBottomPadding),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = null,
                                tint = iconColor,
                                modifier = Modifier
                                    .size(compactIconSize)
                                    .graphicsLayer {
                                        scaleX = iconScale
                                        scaleY = iconScale
                                    }
                            )
                            Spacer(Modifier.height(compactLabelSpacing))
                            Text(
                                trademarkVulkanDisplayText(item.label),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                fontSize = compactLabelFontSize,
                                lineHeight = compactLabelLineHeight,
                                color = textColor,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
internal fun ExploreDestinationTile(page: Page, onNavigate: (Page) -> Unit, modifier: Modifier = Modifier) {
    val shape = MaterialTheme.shapes.medium
    Card(
        onClick = { onNavigate(page) },
        colors = CardDefaults.cardColors(containerColor = VulkanSurfaceTonal),
        shape = shape,
        modifier = modifier.heightIn(min = 52.dp).then(tvBrowseModifier(shape))
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 11.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Surface(shape = MaterialTheme.shapes.small, color = VulkanAccentContainer) {
                Icon(painterResource(pageIcon(page)), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.padding(7.dp).size(18.dp))
            }
            Text(trademarkVulkanDisplayText(page.title), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = VulkanTextPrimary, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
internal fun ExploreCard(onNavigate: (Page) -> Unit) {
    val expandedTextLayout = preferExpandedTextLayout()
    val pages = listOf(Page.Features, Page.Memory, Page.Queues, Page.Video, Page.Formats, Page.Properties)
    CapabilitySectionCard("Explore") {
        Text("Detailed Vulkan® inspection areas", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val columns = when {
                expandedTextLayout || maxWidth < 300.dp -> 1
                maxWidth < 620.dp -> 2
                else -> 3
            }
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                pages.chunked(columns).forEach { rowPages ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        rowPages.forEach { page -> ExploreDestinationTile(page, onNavigate, Modifier.weight(1f)) }
                        repeat(columns - rowPages.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}


@Composable
internal fun HeaderActionButton(
    icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = 50.dp
) {
    val background = if (enabled) ComposeColor(0xFF35191D) else VulkanSurfaceLow
    val content = if (enabled) VulkanAccentSoft else VulkanTextMuted
    Surface(
        shape = CircleShape,
        color = background,
        contentColor = content,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (enabled) VulkanAccentSoft.copy(alpha = 0.34f) else VulkanOutlineVariant),
        tonalElevation = 2.dp,
        shadowElevation = 5.dp,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(painterResource(icon), contentDescription = contentDescription, tint = content, modifier = Modifier.size(if (size < 46.dp) 20.dp else 23.dp))
        }
    }
}

@Composable
internal fun AnimatedHeaderActionButton(
    visible: Boolean,
    icon: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fromEnd: Boolean = false,
    buttonSize: Dp = 50.dp,
    slotSize: Dp = 50.dp
) {
    Box(modifier.size(slotSize), contentAlignment = Alignment.Center) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(190)) + slideInHorizontally(tween(240, easing = FastOutSlowInEasing)) { width -> if (fromEnd) width / 2 else -width / 2 } + scaleIn(animationSpec = tween(190), initialScale = 0.82f),
            exit = fadeOut(tween(150)) + slideOutHorizontally(tween(210, easing = FastOutSlowInEasing)) { width -> if (fromEnd) width / 2 else -width / 2 } + scaleOut(animationSpec = tween(150), targetScale = 0.82f)
        ) {
            HeaderActionButton(icon = icon, contentDescription = contentDescription, onClick = onClick, enabled = enabled, size = buttonSize)
        }
    }
}

@Composable
internal fun HeaderTitleBadge(title: String, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            trademarkVulkanDisplayText(title),
            style = MaterialTheme.typography.titleSmall,
            color = ComposeColor(0xFFF5F0F1),
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Box(Modifier.width(28.dp).height(2.dp).clip(RoundedCornerShape(99.dp)).background(VulkanAccentSoft.copy(alpha = 0.88f)))
    }
}

internal fun Modifier.frostedChromeBackdrop(backdropLayer: GraphicsLayer, backdropRootOffset: Offset, targetRootOffset: Offset): Modifier = drawWithContent {
    clipRect {
        withTransform({ translate(backdropRootOffset.x - targetRootOffset.x, backdropRootOffset.y - targetRootOffset.y) }) {
            drawLayer(backdropLayer)
        }
        drawRect(VulkanGlassTint)
    }
    drawContent()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppHeader(page: Page, onBack: () -> Unit, onSettings: () -> Unit, backdropLayer: GraphicsLayer, backdropRootOffset: Offset) {
    val showBack = page != Page.Overview
    val showSettings = page != Page.Settings
    val navigationPadding = WindowInsets.navigationBars.asPaddingValues()
    val layoutDirection = LocalLayoutDirection.current
    val navigationStartInset = navigationPadding.calculateLeftPadding(layoutDirection)
    val navigationEndInset = navigationPadding.calculateRightPadding(layoutDirection)
    var headerRootOffset by remember { mutableStateOf(Offset.Zero) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { headerRootOffset = it.positionInRoot() }
            .frostedChromeBackdrop(backdropLayer, backdropRootOffset, headerRootOffset)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(
                    start = 14.dp + navigationStartInset,
                    end = 14.dp + navigationEndInset,
                    top = 8.dp,
                    bottom = 9.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AnimatedHeaderActionButton(
                visible = showBack,
                icon = R.drawable.ic_back,
                contentDescription = "Back",
                onClick = onBack,
                buttonSize = 36.dp,
                slotSize = 50.dp
            )
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.vulkanscope_logo_horizontal_aligned),
                    contentDescription = "VulkanScope",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(126.dp).height(30.dp)
                )
                Box(Modifier.width(1.dp).height(25.dp).background(ComposeColor.White.copy(alpha = 0.13f)))
                HeaderTitleBadge(title = page.title, modifier = Modifier.weight(1f))
            }
            AnimatedHeaderActionButton(
                visible = showSettings,
                icon = R.drawable.ic_settings,
                contentDescription = "Settings",
                onClick = onSettings,
                fromEnd = true,
                buttonSize = 50.dp,
                slotSize = 50.dp
            )
        }
    }
}

@Composable
internal fun DisplayPage(display: DisplayReport, device: DeviceReport?) {
    VulkanLazyPage(verticalSpacing = 14.dp) {
        item { CapabilitySectionCard("Display") {
            CapabilityKeyValue("Physical mode", display.resolution)
            CapabilityKeyValue("Current refresh", display.refreshRate)
            CapabilityKeyValue("Wide color gamut", when (display.wideGamut) { true -> "SUPPORTED"; false -> "UNSUPPORTED"; null -> "UNAVAILABLE" })
            CapabilityKeyValue("Preferred wide-gamut color space", display.preferredWideGamut)
        } }
        item { CapabilitySectionCard("HDR capabilities") {
            if (display.hdrTypes.isEmpty()) {
                CapabilityKeyValue("HDR types", when (display.hdrCapabilityStatus) {
                    "available" -> "None reported"
                    "unknown" -> "Unknown"
                    else -> "Unavailable"
                })
            } else {
                Text(
                    "The official logos below represent HDR types detected by Android on this device. HLG and HLG+ are shown as text because no official logo is defined by the authoritative standards sources used by VulkanScope.",
                    color = VulkanTextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
                HdrCapabilitiesCarousel(display.hdrTypes)
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp), color = ComposeColor(0xFF303030))
            CapabilityKeyValue("Minimum luminance", display.minLuminance)
            CapabilityKeyValue("Maximum luminance", display.maxLuminance)
            CapabilityKeyValue("Maximum average luminance", display.averageLuminance)
        } }
        item { CapabilitySectionCard("Supported display modes") {
            if (display.modes.isEmpty()) EmptyState("Display mode list unavailable")
            display.modes.forEachIndexed { index, mode -> CapabilityKeyValue("Mode ${index + 1}", mode) }
        } }
        item { CapabilitySectionCard("Display ↔ Vulkan interpretation") {
            CapabilityKeyValue("Android wide gamut", when (display.wideGamut) { true -> "Supported"; false -> "Unsupported"; null -> "Unavailable" })
            CapabilityKeyValue("Vulkan® surface query", device?.surfaceQueryStatus?.uppercase() ?: "UNKNOWN")
            if (!device?.surfaceQueryReason.isNullOrBlank()) CapabilityKeyValue("Surface query reason", device?.surfaceQueryReason ?: "")
            CapabilityKeyValue("Vulkan® surface data", when (device?.surfaceQueryStatus) {
                "available" -> "Available from VkSurfaceKHR"
                "incomplete" -> "Incomplete: partial Surface evidence retained"
                "not_applicable" -> "Not applicable"
                "unavailable" -> "Unavailable"
                else -> "Unknown"
            })
            Text("A Vulkan® color-space capability is not treated as a measurement of the panel's physical gamut.", color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.bodySmall)
        } }
    }
}


@Composable
internal fun HdrCapabilitiesCarousel(types: List<String>) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val canMoveLeft = scrollState.value > 0
    val canMoveRight = scrollState.value < scrollState.maxValue
    Box(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().horizontalScroll(scrollState), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            types.forEach { HdrTypeCard(it) }
        }
        AnimatedVisibility(visible = canMoveLeft, modifier = Modifier.align(Alignment.CenterStart), enter = fadeIn(), exit = fadeOut()) {
            IconButton(
                onClick = { scope.launch { scrollState.animateScrollTo((scrollState.value - 360).coerceAtLeast(0)) } },
                colors = IconButtonDefaults.iconButtonColors(containerColor = VulkanAccentContainer, contentColor = VulkanTextPrimary),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(painterResource(R.drawable.ic_chevron_left), contentDescription = "Scroll HDR capabilities left", modifier = Modifier.size(23.dp))
            }
        }
        AnimatedVisibility(visible = canMoveRight, modifier = Modifier.align(Alignment.CenterEnd), enter = fadeIn(), exit = fadeOut()) {
            IconButton(
                onClick = { scope.launch { scrollState.animateScrollTo((scrollState.value + 360).coerceAtMost(scrollState.maxValue)) } },
                colors = IconButtonDefaults.iconButtonColors(containerColor = VulkanAccentContainer, contentColor = VulkanTextPrimary),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = "Scroll HDR capabilities right", modifier = Modifier.size(23.dp))
            }
        }
    }
}

@Composable
internal fun HdrTypeCard(type: String) {
    val normalized = type.trim().lowercase(java.util.Locale.ROOT)
    val logo = when (normalized) {
        "dolby vision" -> R.drawable.hdr_dolby_vision
        "dolby vision 2" -> R.drawable.hdr_dolby_vision_2
        "hdr10" -> R.drawable.hdr_hdr10
        "hdr10+" -> R.drawable.hdr_hdr10_plus
        "hdr10+ advanced" -> R.drawable.hdr_hdr10_plus_advanced
        "hdr vivid" -> R.drawable.hdr_vivid
        else -> null
    }
    val whiteCard = normalized == "hdr10"
    val shape = RoundedCornerShape(18.dp)
    Surface(
        shape = shape,
        color = if (whiteCard) ComposeColor.White else ComposeColor(0xFF111111),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (whiteCard) ComposeColor(0xFFE0E0E0) else ComposeColor(0xFF2B2B2B)),
        modifier = Modifier.then(tvBrowseModifier(shape))
    ) {
        if (logo != null) {
            Image(
                painter = painterResource(logo),
                contentDescription = type,
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(154.dp).height(62.dp).padding(horizontal = 13.dp, vertical = 10.dp)
            )
        } else {
            Text(
                type,
                color = if (whiteCard) ComposeColor.Black else ComposeColor.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
internal fun SurfaceFormatsPage(device: DeviceReport?) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf(SupportFilter.ALL) }
    var page by rememberSaveable { mutableIntStateOf(0) }
    val entries = remember(device) {
        val formats = device?.surfaceFormats ?: emptyList()
        val completeEnumeration = device?.surfaceFormatEnumerationComplete == true && !device.surfaceFormatQuerySpecAnomaly
        if (completeEnumeration) buildSurfaceCatalog(formats) else formats
    }
    val filtered = remember(query, filter, entries) {
        entries.filter { entry ->
            val statusOk = when (filter) { SupportFilter.ALL -> true; SupportFilter.SUPPORTED -> entry.supported; SupportFilter.UNSUPPORTED -> !entry.supported }
            statusOk && (query.isBlank() || entry.format.contains(query, true) || entry.colorSpace.contains(query, true) || entry.classification.contains(query, true) || entry.description.contains(query, true))
        }
    }
    val pageCount = maxOf(1, (filtered.size + COLLECTION_PAGE_SIZE - 1) / COLLECTION_PAGE_SIZE)
    LaunchedEffect(query, filter) { page = 0 }
    LaunchedEffect(filtered.size) { page = page.coerceIn(0, pageCount - 1) }
    val pageStart = page * COLLECTION_PAGE_SIZE
    val paged = filtered.drop(pageStart).take(COLLECTION_PAGE_SIZE)
    VulkanLazyPage(verticalSpacing = 12.dp) {
        item { CapabilitySectionCard("VkSurfaceKHR") {
            CapabilityKeyValue("Status", device?.surfaceQueryStatus?.replace('_', ' ')?.uppercase() ?: "UNKNOWN")
            if (!device?.surfaceQueryReason.isNullOrBlank()) CapabilityKeyValue("Reason", device?.surfaceQueryReason ?: "")
            CapabilityKeyValue("Presentation support", when {
                device == null -> "Unavailable"
                device.surfacePresentationSupported -> "Supported"
                device.surfaceQueryStatus == "available" -> "Not supported by this Vulkan device"
                device.surfaceQueryStatus == "not_applicable" -> "Not applicable"
                device.surfaceQueryStatus == "incomplete" -> "Unknown: Surface query is incomplete"
                device.surfaceQueryStatus == "unknown" -> "Unknown"
                else -> "Unavailable"
            })
            CapabilityKeyValue("Surface queue-family safety rejection", when (device?.surfaceQueueQuerySafetyRejected) { true -> "YES · result discarded"; false -> "NO"; null -> "Unavailable" })
            CapabilityKeyValue("Dependent WSI queries", when (device?.surfaceDependentWsiQueryStatus) {
                "available" -> "Available"
                "incomplete" -> "Incomplete"
                "not_applicable" -> "Not applicable: no queue family supports presentation to this Surface"
                else -> "Unknown: presentation support was not proven"
            })
            CapabilityKeyValue("Surface format query", when {
                device == null -> "Unavailable"
                device.surfaceFormatQueryAttempted -> vkResultText(device.surfaceFormatQueryResult)
                device.surfaceDependentWsiQueryStatus == "not_applicable" -> "Not applicable"
                else -> "Unknown: query not attempted because Surface support was not proven"
            })
            CapabilityKeyValue("Second format query", when {
                device == null -> "Unavailable"
                device.surfaceFormatQuerySafetyRejected -> "Rejected: safety bound"
                device.surfaceFormatQuerySecondAttempted -> vkResultText(device.surfaceFormatQueryResultSecond)
                device.surfaceDependentWsiQueryStatus == "not_applicable" -> "Not applicable"
                device.surfaceFormatQueryAttempted -> "Not attempted"
                else -> "Unknown: query not attempted because Surface support was not proven"
            })
            CapabilityKeyValue("Returned format pairs", when {
                device == null -> "Unavailable"
                device.surfaceFormatQueryAttempted -> device.surfaceFormats.size.toString()
                device.surfaceDependentWsiQueryStatus == "not_applicable" -> "Not applicable"
                else -> "Unknown"
            })
            CapabilityKeyValue("Surface-format enumeration complete", when {
                device == null -> "Unavailable"
                device.surfaceFormatEnumerationComplete && !device.surfaceFormatQuerySpecAnomaly -> "YES"
                device.surfaceFormatQuerySpecAnomaly -> "NO · specification anomaly"
                else -> "NO"
            })
            CapabilityKeyValue("Present-mode enumeration complete", when {
                device == null -> "Unavailable"
                device.surfacePresentModeEnumerationComplete && !device.surfacePresentModeQuerySpecAnomaly -> "YES"
                device.surfacePresentModeQuerySpecAnomaly -> "NO · specification anomaly"
                else -> "NO"
            })
            device?.surfaceCapabilities?.forEach { CapabilityKeyValue(it.first, it.second) }
        } }
        item { CapabilitySectionCard("Search surface formats / color spaces") {
            ExpressiveSearchField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), placeholderText = "Search BT.709, BT.2020, P3, HDR10, format…")
            Spacer(Modifier.height(6.dp))
            SupportFilterRow(filter) { filter = it }
            Text("${filtered.size} entries", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.labelMedium)
        } }
        item { CapabilitySectionCard("HDR / wide-color surface detection") {
            CapabilityKeyValue("VK_EXT_swapchain_colorspace", when {
                device?.surfaceColorSpaceExtensionEnabled == true -> "Enabled"
                device?.surfaceColorSpaceExtensionStatus == "available" -> "Available but not enabled"
                device?.surfaceColorSpaceExtensionStatus == "not_exposed" -> "Not exposed"
                else -> "Unknown: instance-extension enumeration is incomplete or unavailable"
            })
            val hdrPairs = device?.surfaceFormats?.count { it.classification == "HDR10 / PQ" || it.classification == "HDR10 / HLG" || it.classification == "Dolby Vision" } ?: 0
            val wideColorPairs = device?.surfaceFormats?.count { it.classification == "Display-P3" || it.classification == "Display-P3 / Linear" || it.classification == "BT.2020" || it.classification == "scRGB" || it.classification == "scRGB / Linear" } ?: 0
            CapabilityKeyValue("HDR color-space pairs", when { device?.surfacePresentationSupported == true -> hdrPairs.toString(); device?.surfaceDependentWsiQueryStatus == "not_applicable" -> "Not applicable"; else -> "Unknown" })
            CapabilityKeyValue("Wide-color pairs", when { device?.surfacePresentationSupported == true -> wideColorPairs.toString(); device?.surfaceDependentWsiQueryStatus == "not_applicable" -> "Not applicable"; else -> "Unknown" })
            Text(if (device?.surfaceFormatEnumerationComplete == true && !device.surfaceFormatQuerySpecAnomaly) "Supported entries are returned directly by the completed surface-format enumeration. Catalog entries absent from that complete result are shown as not supported for this surface." else "Surface-format enumeration was not confirmed complete, so VulkanScope shows only returned entries and does not infer unsupported format/color-space pairs from missing results.", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
        } }
        item { CapabilitySectionCard("Format + color-space pairs") {
            if (filtered.isEmpty()) Text("No matching entries") else Text("${filtered.size} matching format/color-space pairs", color = VulkanTextMuted, style = MaterialTheme.typography.bodySmall)
        } }
        stickyCollectionPager(totalItems = filtered.size, currentPage = page, onPageChange = { page = it })
        items(paged, key = { "${it.format}|${it.colorSpace}|${it.supported}" }) { format ->
            VulkanEvidenceCard(
                title = format.format,
                category = "Surface format and color space",
                status = if (format.supported) "SUPPORTED" else "NOT SUPPORTED",
                accent = if (format.supported) ComposeColor(0xFF80DFA8) else ComposeColor(0xFFFFC77D),
                icon = R.drawable.ic_surface,
                description = format.description,
                metadata = listOf(format.colorSpace, format.classification)
            )
        }
    }
}

@Composable
internal fun SurfaceSectionCards(onSelected: (SurfaceSection) -> Unit) {
    var cardsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { cardsVisible = true }
    VulkanLazyPage(verticalSpacing = 10.dp) {
        item(key = "surface-destinations-fullwidth") {
            SectionDestinationGrid(SurfaceSection.entries.toList()) { section, index ->
                AnimatedSurfaceSectionCard(cardsVisible, index, section, onSelected)
            }
        }
    }
}

@Composable
private fun AnimatedSurfaceSectionCard(
    visible: Boolean,
    index: Int,
    section: SurfaceSection,
    onSelected: (SurfaceSection) -> Unit
) {
    val delayMillis = index * 45
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(durationMillis = 260, delayMillis = delayMillis)) +
            slideInHorizontally(tween(durationMillis = 260, delayMillis = delayMillis)) { it / 10 },
        exit = fadeOut(tween(durationMillis = 120))
    ) {
        ExpressiveDestinationCard(section.label, section.description, section.icon) { onSelected(section) }
    }
}

@Composable
internal fun SurfacePresentationPage(device: DeviceReport?) {
    VulkanLazyPage(verticalSpacing = 12.dp) {
        item {
            CapabilitySectionCard("Presentation overview") {
                CapabilityKeyValue("Surface query status", device?.surfaceQueryStatus?.replace('_', ' ')?.uppercase() ?: "UNKNOWN")
                CapabilityKeyValue("Presentation support", when {
                    device == null -> "Unavailable"
                    device.surfacePresentationSupported -> "Supported"
                    device.surfaceQueryStatus == "available" -> "Not supported by this Vulkan device"
                    device.surfaceQueryStatus == "not_applicable" -> "Not applicable"
                    device.surfaceQueryStatus == "incomplete" -> "Unknown: Surface query is incomplete"
                    device.surfaceQueryStatus == "unknown" -> "Unknown"
                    else -> "Unavailable"
                })
                CapabilityKeyValue("Present-mode enumeration complete", when {
                    device == null -> "Unavailable"
                    device.surfacePresentModeEnumerationComplete && !device.surfacePresentModeQuerySpecAnomaly -> "YES"
                    device.surfacePresentModeQuerySpecAnomaly -> "NO · specification anomaly"
                    else -> "NO"
                })
            }
        }
        item { CapabilitySectionCard("Present modes") {
            if (device?.presentModes.isNullOrEmpty()) EmptyState(when (device?.surfaceDependentWsiQueryStatus) { "not_applicable" -> "Present-mode query not applicable because this physical device cannot present to the Surface"; "unknown" -> "Present-mode query not attempted because Surface support was not proven"; else -> "Present mode data unavailable" })
            device?.presentModes?.forEachIndexed { index, mode -> CapabilityKeyValue("Mode ${index + 1}", mode) }
        } }
        item { CapabilitySectionCard("Presentation support") {
            if (device?.presentationQueueEvidence.isNullOrEmpty() && device?.presentationQueues.isNullOrEmpty()) EmptyState("Presentation queue data unavailable")
            if (!device?.presentationQueueEvidence.isNullOrEmpty()) {
                device?.presentationQueueEvidence?.forEach { CapabilityKeyValue("Queue family ${it.queueFamily}", presentationQueueEvidenceText(it)) }
            } else {
                device?.presentationQueues?.forEach { CapabilityKeyValue("Queue family ${it.first}", if (it.second) "PRESENT" else "NO PRESENT") }
            }
        } }
    }
}

@Composable
internal fun SurfaceDestinationPage(
    display: DisplayReport,
    device: DeviceReport?,
    selectedSection: SurfaceSection?,
    onSectionSelected: (SurfaceSection?) -> Unit
) {
    AnimatedContent(
        targetState = selectedSection,
        transitionSpec = {
            if (targetState != null) {
                slideInHorizontally(tween(240)) { it / 7 } + fadeIn(tween(220)) togetherWith
                    slideOutHorizontally(tween(180)) { -it / 9 } + fadeOut(tween(160))
            } else {
                slideInHorizontally(tween(240)) { -it / 7 } + fadeIn(tween(220)) togetherWith
                    slideOutHorizontally(tween(180)) { it / 9 } + fadeOut(tween(160))
            }
        },
        label = "surfaceSectionTransition"
    ) { section ->
        when (section) {
            null -> SurfaceSectionCards { onSectionSelected(it) }
            SurfaceSection.DISPLAY -> DisplayPage(display, device)
            SurfaceSection.SURFACE_FORMATS -> SurfaceFormatsPage(device)
            SurfaceSection.PRESENTATION -> SurfacePresentationPage(device)
        }
    }
}

internal fun buildSurfaceCatalog(supported: List<SurfaceFormatEntry>): List<SurfaceFormatEntry> {
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
internal fun FeaturesPage(device: DeviceReport?) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf(SupportFilter.ALL) }
    var sourceFilter by remember { mutableStateOf("All") }
    var page by rememberSaveable { mutableIntStateOf(0) }
    val all = device?.features ?: emptyList()
    val sources = remember(all) { listOf("All") + all.map { featureSource(it.name) }.distinct() }
    val filtered = remember(all, query, filter, sourceFilter) {
        all.filter { feature ->
            val supportMatches = when (filter) {
                SupportFilter.ALL -> true
                SupportFilter.SUPPORTED -> feature.supported
                SupportFilter.UNSUPPORTED -> !feature.supported
            }
            val sourceMatches = sourceFilter == "All" || featureSource(feature.name) == sourceFilter
            val textMatches = query.isBlank() || feature.name.contains(query, ignoreCase = true)
            supportMatches && sourceMatches && textMatches
        }
    }
    val pageCount = maxOf(1, (filtered.size + COLLECTION_PAGE_SIZE - 1) / COLLECTION_PAGE_SIZE)
    LaunchedEffect(query, filter, sourceFilter) { page = 0 }
    LaunchedEffect(filtered.size) { page = page.coerceIn(0, pageCount - 1) }
    val pageStart = page * COLLECTION_PAGE_SIZE
    val paged = filtered.drop(pageStart).take(COLLECTION_PAGE_SIZE)
    VulkanLazyPage(verticalSpacing = 8.dp) {
        item {
            CapabilitySectionCard("Feature explorer") {
                Text("Runtime feature support. Core 1.0, promoted core versions and extension-provided feature blocks remain distinguishable.", style = MaterialTheme.typography.bodySmall, color = ComposeColor(0xFFB6ACAE))
                ExpressiveSearchField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), labelText = "Search features")
                SupportFilterRow(filter) { filter = it }
                ExpressiveFilterBar(sources, sources.indexOf(sourceFilter).coerceAtLeast(0), arrowTint = VulkanAccentSoft) { sourceFilter = sources[it] }
                ExpressiveMetricGrid(
                    listOf(
                        "Matching features" to filtered.size.toString(),
                        "Showing" to if (filtered.isEmpty()) "0" else "${pageStart + 1}-${pageStart + paged.size}"
                    ),
                    Modifier.padding(top = 4.dp)
                )
            }
        }
        stickyCollectionPager(totalItems = filtered.size, currentPage = page, onPageChange = { page = it })
        itemsIndexed(paged, key = { index, feature -> "feature:${feature.name}:${pageStart + index}" }) { _, feature ->
            VulkanEvidenceCard(
                title = featureNameOnly(feature.name),
                category = "Runtime feature bit",
                status = if (feature.supported) "SUPPORTED" else "NOT SUPPORTED",
                accent = if (feature.supported) ComposeColor(0xFF80DFA8) else ComposeColor(0xFFFFC77D),
                icon = R.drawable.ic_features,
                description = if (feature.supported) "The selected device reported this feature bit true." else "The queried feature bit was reported false; this is not an API-level error.",
                metadata = listOf(featureSource(feature.name))
            )
        }
        if (filtered.isEmpty()) item { EmptyState("No matching features") }
    }
}

@Composable
internal fun MemoryPage(device: DeviceReport?) {
    VulkanLazyPage(verticalSpacing = 14.dp) {
        item { CapabilitySectionCard("Memory query safety") {
            CapabilityKeyValue("Heap-count safety rejection", when (device?.memoryHeapSafetyRejected) { true -> "YES · bounded data only"; false -> "NO"; null -> "Unavailable" })
            CapabilityKeyValue("Type-count safety rejection", when (device?.memoryTypeSafetyRejected) { true -> "YES · bounded data only"; false -> "NO"; null -> "Unavailable" })
        } }
        item { CapabilitySectionCard("Memory heaps") {
            if (device?.heaps.isNullOrEmpty()) EmptyState("Memory heap data unavailable")
            device?.heaps?.forEach { heap ->
                CapabilityItemCard(containerColor = VulkanSurfaceTonal) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text("Heap ${heap.index}", fontWeight = FontWeight.SemiBold)
                        CapabilityKeyValue("Size", formatBytes(heap.size))
                        CapabilityKeyValue("Flags", memoryHeapFlags(heap.flags))
                    }
                }
            }
        } }
        item { CapabilitySectionCard("Memory types") {
            if (device?.memoryTypes.isNullOrEmpty()) EmptyState("Memory type data unavailable")
            device?.memoryTypes?.forEach { type ->
                CapabilityItemCard(containerColor = VulkanSurfaceTonal) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text("Type ${type.index}", fontWeight = FontWeight.SemiBold)
                        CapabilityKeyValue("Heap", type.heap.toString())
                        CapabilityKeyValue("Properties", memoryTypeFlags(type.flags))
                    }
                }
            }
        } }
    }
}

@Composable
internal fun QueuesPage(device: DeviceReport?) {
    var page by rememberSaveable { mutableIntStateOf(0) }
    val queues = remember(device) { device?.queues ?: emptyList() }
    val pageCount = maxOf(1, (queues.size + COLLECTION_PAGE_SIZE - 1) / COLLECTION_PAGE_SIZE)
    LaunchedEffect(queues.size) { page = page.coerceIn(0, pageCount - 1) }
    val pageStart = page * COLLECTION_PAGE_SIZE
    val pagedQueues = queues.drop(pageStart).take(COLLECTION_PAGE_SIZE)
    VulkanLazyPage(verticalSpacing = 10.dp) {
        item { CapabilitySectionCard("Queue query safety") {
            CapabilityKeyValue("Enumeration safety rejection", when (device?.queueQuerySafetyRejected) { true -> "YES · result discarded"; false -> "NO"; null -> "Unavailable" })
            CapabilityKeyValue("Queue families", queues.size.toString())
        } }
        stickyCollectionPager(totalItems = queues.size, currentPage = page, onPageChange = { page = it }, pagerKey = "queues-pager")
        items(pagedQueues, key = { it.index }) { queue ->
            CapabilitySectionCard("Queue family ${queue.index}") {
                CapabilityKeyValue("Queue count", queue.count.toString())
                CapabilityKeyValue("Timestamp valid bits", queue.timestampBits.toString())
                CapabilityKeyValue("Capabilities", queueCapabilityFlags(queue.flags))
                CapabilityKeyValue("Graphics", if (queue.graphics) "YES" else "NO")
                CapabilityKeyValue("Compute", if (queue.compute) "YES" else "NO")
                CapabilityKeyValue("Transfer", if (queue.transfer) "YES" else "NO")
                CapabilityKeyValue("Sparse binding", if (queue.sparse) "YES" else "NO")
                CapabilityKeyValue("Protected", if (queue.protected) "YES" else "NO")
                CapabilityKeyValue("Video decode", if (queue.videoDecode) "YES" else "NO")
                CapabilityKeyValue("Video encode", if (queue.videoEncode) "YES" else "NO")
                CapabilityKeyValue("Optical flow", if (queue.opticalFlow) "YES" else "NO")
                CapabilityKeyValue("Data graph", if (queue.dataGraph) "YES" else "NO")
                CapabilityKeyValue("Min image transfer granularity", queue.granularity)
                if (queue.unknownFlags != 0L) CapabilityKeyValue("Unknown queue flag bits", "0x${queue.unknownFlags.toString(16).uppercase()}")
                CapabilityKeyValue("Video codec query", queueVideoCodecQueryState(queue))
                if (queueVideoCodecEvidenceRetained(queue)) CapabilityKeyValue("Video codec operations", videoCodecOperationFlags(queue.videoCodecOperations))
            }
        }
        if (queues.isEmpty()) item { EmptyState("Queue family data unavailable") }
    }
}

internal data class VideoProfileEvidence(
    val operation: String,
    val direction: String,
    val codec: String,
    val variant: String,
    val status: String,
    val properties: List<Pair<String, String>>
)

internal data class VulkanVideoEvidence(
    val general: List<PropertyEntry>,
    val profiles: List<VideoProfileEvidence>,
    val formats: List<PropertyEntry>
)

internal fun parseVulkanVideoEvidence(device: DeviceReport?): VulkanVideoEvidence {
    val rows = device?.detailedProperties.orEmpty().filter { it.section == "Vulkan Video" }
    val operationOrder = listOf("H.264 decode", "H.265 decode", "VP9 decode", "AV1 decode", "H.264 encode", "H.265 encode", "AV1 encode")
    val grouped = linkedMapOf<Pair<String, String>, MutableList<Pair<String, String>>>()
    val formats = mutableListOf<PropertyEntry>()
    val general = mutableListOf<PropertyEntry>()
    rows.forEach { entry ->
        when {
            entry.name.startsWith("Video formats · ") -> formats += entry
            entry.name.startsWith("Video · ") -> {
                val body = entry.name.removePrefix("Video · ")
                val operation = operationOrder.firstOrNull { body.startsWith("$it · ") }
                if (operation == null) {
                    general += entry
                } else {
                    val tail = body.removePrefix("$operation · ")
                    val bracketEnd = tail.lastIndexOf(']')
                    val variant = if (bracketEnd >= 0) tail.substring(0, bracketEnd + 1).trim() else tail.trim()
                    val property = if (bracketEnd >= 0) tail.substring(bracketEnd + 1).trim().ifBlank { "status" } else "status"
                    grouped.getOrPut(operation to variant) { mutableListOf() } += property to entry.value
                }
            }
            else -> general += entry
        }
    }
    val profiles = grouped.map { (identity, values) ->
        val operation = identity.first
        val direction = if (operation.endsWith("decode")) "Decode" else "Encode"
        val codec = operation.removeSuffix(" decode").removeSuffix(" encode")
        val status = values.lastOrNull { it.first == "status" }?.second ?: "Unknown: exact profile status was not reported."
        VideoProfileEvidence(operation, direction, codec, identity.second, status, values.filterNot { it.first == "status" })
    }.sortedWith(compareBy<VideoProfileEvidence> { operationOrder.indexOf(it.operation).let { index -> if (index < 0) Int.MAX_VALUE else index } }.thenBy { it.variant })
    return VulkanVideoEvidence(general, profiles, formats)
}

internal fun videoEvidenceState(value: String): String = when {
    value.startsWith("Supported", true) -> "SUPPORTED"
    value.startsWith("Unsupported", true) -> "UNSUPPORTED"
    value.startsWith("Available", true) -> "AVAILABLE"
    value.startsWith("Unavailable", true) -> "UNAVAILABLE"
    value.startsWith("Not applicable", true) -> "NOT APPLICABLE"
    value.startsWith("Incomplete", true) || value.startsWith("Partial", true) -> "INCOMPLETE"
    value.startsWith("Unknown", true) -> "UNKNOWN"
    else -> "UNKNOWN"
}

internal fun videoPropertyLabel(name: String): String = when (name) {
    "maxLevel", "maxLevelIdc" -> "Maximum level"
    "codedExtent" -> "Coded extent"
    "DPB" -> "Decoded picture buffer"
    "bitstreamAlignment" -> "Bitstream alignment"
    "stdHeader" -> "StdVideo header"
    "fieldOffsetGranularity" -> "Field offset granularity"
    "capabilityFlags" -> "Capability flags"
    "pictureAccessGranularity" -> "Picture access granularity"
    "encodeFlags" -> "Encode flags"
    "rateControlModes" -> "Rate-control modes"
    "maxRateControlLayers" -> "Maximum rate-control layers"
    "maxBitrate" -> "Maximum bitrate"
    "maxQualityLevels" -> "Maximum quality levels"
    "encodeInputPictureGranularity" -> "Encode input granularity"
    "supportedEncodeFeedbackFlags" -> "Encode feedback flags"
    "codecFlags" -> "Codec flags"
    "maxSliceCount" -> "Maximum slice count"
    "maxSliceSegmentCount" -> "Maximum slice-segment count"
    "maxTiles" -> "Maximum tiles"
    "ctbSizes" -> "CTB sizes"
    "transformBlockSizes" -> "Transform block sizes"
    "maxPPictureL0ReferenceCount" -> "Maximum P-picture L0 references"
    "maxBPictureL0ReferenceCount" -> "Maximum B-picture L0 references"
    "maxL1ReferenceCount" -> "Maximum L1 references"
    "maxTemporalLayerCount" -> "Maximum temporal layers"
    "maxSubLayerCount" -> "Maximum sub-layers"
    "expectDyadicTemporalLayerPattern" -> "Expects dyadic temporal-layer pattern"
    "expectDyadicTemporalSubLayerPattern" -> "Expects dyadic temporal sub-layer pattern"
    "qpRange" -> "QP range"
    "prefersGopRemainingFrames" -> "Prefers GOP remaining frames"
    "requiresGopRemainingFrames" -> "Requires GOP remaining frames"
    "stdSyntaxFlags" -> "StdVideo syntax flags"
    "codedPictureAlignment" -> "Coded picture alignment"
    "minTileSize" -> "Minimum tile size"
    "maxTileSize" -> "Maximum tile size"
    "superblockSizes" -> "Superblock sizes"
    "maxSingleReferenceCount" -> "Maximum single references"
    "singleReferenceNameMask" -> "Single-reference name mask"
    "maxUnidirectionalCompoundReferenceCount" -> "Maximum unidirectional compound references"
    "maxUnidirectionalCompoundGroup1ReferenceCount" -> "Maximum unidirectional compound group 1 references"
    "unidirectionalCompoundReferenceNameMask" -> "Unidirectional compound reference-name mask"
    "maxBidirectionalCompoundReferenceCount" -> "Maximum bidirectional compound references"
    "maxBidirectionalCompoundGroup1ReferenceCount" -> "Maximum bidirectional compound group 1 references"
    "maxBidirectionalCompoundGroup2ReferenceCount" -> "Maximum bidirectional compound group 2 references"
    "bidirectionalCompoundReferenceNameMask" -> "Bidirectional compound reference-name mask"
    "maxSpatialLayerCount" -> "Maximum spatial layers"
    "maxOperatingPoints" -> "Maximum operating points"
    "qIndexRange" -> "Q-index range"
    else -> name
}

@Composable
internal fun VideoEvidenceStateBadge(state: String) {
    val background = when (state) {
        "SUPPORTED" -> ComposeColor(0xFF173421)
        "UNSUPPORTED" -> ComposeColor(0xFF3A1D20)
        "AVAILABLE" -> ComposeColor(0xFF172B3A)
        "UNAVAILABLE" -> ComposeColor(0xFF332A16)
        "NOT APPLICABLE" -> ComposeColor(0xFF272727)
        "INCOMPLETE" -> ComposeColor(0xFF30243A)
        else -> ComposeColor(0xFF252525)
    }
    val foreground = when (state) {
        "SUPPORTED" -> ComposeColor(0xFF73C991)
        "UNSUPPORTED" -> ComposeColor(0xFFFF8A8A)
        "AVAILABLE" -> ComposeColor(0xFF7CC4FF)
        "UNAVAILABLE" -> ComposeColor(0xFFFFC857)
        "NOT APPLICABLE" -> ComposeColor(0xFFC4C4C4)
        "INCOMPLETE" -> ComposeColor(0xFFD3A4FF)
        else -> ComposeColor(0xFFA8A8A8)
    }
    Surface(shape = RoundedCornerShape(999.dp), color = background) {
        Text(state, color = foreground, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
    }
}

@Composable
internal fun VideoProfileEvidenceCard(profile: VideoProfileEvidence) {
    CapabilityItemCard {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("${profile.codec} ${profile.direction.lowercase()}", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                    Text(profile.variant, color = VulkanTextPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                }
                VideoEvidenceStateBadge(videoEvidenceState(profile.status))
            }
            Text(profile.status, color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
            profile.properties.forEach { (name, value) -> CapabilityKeyValue(videoPropertyLabel(name), value) }
        }
    }
}

internal fun videoOperationNeedle(profile: VideoProfileEvidence): String = when {
    profile.direction == "Decode" && profile.codec == "H.264" -> "DECODE_H264"
    profile.direction == "Decode" && profile.codec == "H.265" -> "DECODE_H265"
    profile.direction == "Decode" && profile.codec == "VP9" -> "DECODE_VP9"
    profile.direction == "Decode" && profile.codec == "AV1" -> "DECODE_AV1"
    profile.direction == "Encode" && profile.codec == "H.264" -> "ENCODE_H264"
    profile.direction == "Encode" && profile.codec == "H.265" -> "ENCODE_H265"
    profile.direction == "Encode" && profile.codec == "AV1" -> "ENCODE_AV1"
    else -> ""
}

internal fun videoProfileQueueFamilies(profile: VideoProfileEvidence, queues: List<QueueEntry>): List<Int> {
    val needle = videoOperationNeedle(profile)
    if (needle.isBlank()) return emptyList()
    return queues.filter { queue -> queueVideoCodecEvidenceRetained(queue) && videoCodecOperationFlags(queue.videoCodecOperations).contains(needle, true) }.map { it.index }
}

@Composable
internal fun VulkanVideoPage(device: DeviceReport?) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var page by rememberSaveable { mutableIntStateOf(0) }
    val evidence = remember(device) { parseVulkanVideoEvidence(device) }
    val tabs = listOf("Overview", "Matrix", "Decode", "Encode", "Formats", "Queues")
    val registry = evidence.general.lastOrNull { it.name == "videoRegistry" }?.value
    val recipe = evidence.general.lastOrNull { it.name == "queryRecipe" }?.value
    val capabilityQuery = evidence.general.lastOrNull { it.name == "Video capability query" }?.value
    val formatQuery = evidence.general.lastOrNull { it.name == "Video format query" }?.value
    val decodeProfiles = remember(evidence.profiles) { evidence.profiles.filter { it.direction == "Decode" } }
    val encodeProfiles = remember(evidence.profiles) { evidence.profiles.filter { it.direction == "Encode" } }
    val queryStatus = evidence.general.lastOrNull { it.name == "queryStatus" }?.value
        ?: capabilityQuery?.takeIf { it.startsWith("Unavailable", true) || it.startsWith("Unknown", true) }
        ?: if (evidence.profiles.isNotEmpty()) "Available: exact-profile Vulkan Video census evidence was collected." else "Unknown: Vulkan Video query status was not reported."
    val activeTotal = when (tab) {
        1 -> evidence.profiles.size
        2 -> decodeProfiles.size
        3 -> encodeProfiles.size
        4 -> evidence.formats.size
        5 -> device?.queues?.size ?: 0
        else -> 0
    }
    val activePageCount = maxOf(1, (activeTotal + COLLECTION_PAGE_SIZE - 1) / COLLECTION_PAGE_SIZE)
    LaunchedEffect(tab) { page = 0 }
    LaunchedEffect(activeTotal) { page = page.coerceIn(0, activePageCount - 1) }
    VulkanLazyPage(verticalSpacing = 10.dp) {
        item {
            CapabilitySectionCard("Vulkan Video") {
                Text("Exact-profile Vulkan® Video evidence collected by VulkanScope. Capability results below apply only to the queried exact 4:2:0 8-bit luma/chroma profile combinations and never imply codec-wide support.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                ExpressiveFilterBar(tabs, tab) { tab = it; page = 0 }
            }
        }
        if (tab == 0) {
            item {
                CapabilitySectionCard("Query overview") {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Capability query", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        VideoEvidenceStateBadge(videoEvidenceState(queryStatus))
                    }
                    CapabilityKeyValue("Query status", queryStatus)
                    CapabilityKeyValue("Exact general profile", "VK_VIDEO_CHROMA_SUBSAMPLING_420_BIT_KHR · 8-bit luma · 8-bit chroma")
                    CapabilityKeyValue("Exact profile combinations", evidence.profiles.size.toString())
                    CapabilityKeyValue("Decode combinations", decodeProfiles.size.toString())
                    CapabilityKeyValue("Encode combinations", encodeProfiles.size.toString())
                    registry?.let { CapabilityKeyValue("Registry identity", it) }
                    capabilityQuery?.let { CapabilityKeyValue("Capability entry point", it) }
                    recipe?.let { CapabilityKeyValue("Query recipe", it) }
                }
            }
            item {
                CapabilitySectionCard("Codec summary") {
                    listOf("H.264", "H.265", "VP9", "AV1").forEach { codec ->
                        val codecRows = evidence.profiles.filter { it.codec == codec }
                        val supported = codecRows.count { videoEvidenceState(it.status) == "SUPPORTED" }
                        val unsupported = codecRows.count { videoEvidenceState(it.status) == "UNSUPPORTED" }
                        val unresolved = codecRows.size - supported - unsupported
                        CapabilityKeyValue(codec, "${codecRows.size} exact combinations · $supported supported · $unsupported unsupported · $unresolved other evidence states")
                    }
                }
            }
            item {
                CapabilitySectionCard("Format scope") {
                    CapabilityKeyValue("Format query", formatQuery ?: "Unknown: sampled-profile format query status was not reported.")
                    Text("Format rows are intentionally sampled-profile evidence. They are not a complete profile/chroma/bit-depth Cartesian census and are kept separate from the exact-profile capability results.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                }
            }
        } else if (tab == 1) {
            item { CapabilitySectionCard("Exact-profile matrix") {
                Text("Each row keeps exact-profile capability state, matching queue codec-operation evidence and bounded sampled-format evidence separate. A queue/format association does not upgrade the exact-profile capability state.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
            } }
            stickyCollectionPager(totalItems = evidence.profiles.size, currentPage = page, onPageChange = { page = it }, pagerKey = "video-matrix-pager")
            items(evidence.profiles.drop(page * COLLECTION_PAGE_SIZE).take(COLLECTION_PAGE_SIZE), key = { "matrix:${it.operation}:${it.variant}" }) { profile ->
                val queueFamilies = videoProfileQueueFamilies(profile, device?.queues.orEmpty())
                val sampledFormats = evidence.formats.filter { it.name.contains(profile.operation, true) || it.name.contains(profile.codec, true) }
                CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("${profile.codec} ${profile.direction.lowercase()}", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                            Text(profile.variant, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall)
                        }
                        VideoEvidenceStateBadge(videoEvidenceState(profile.status))
                    }
                    CapabilityKeyValue("Exact profile status", profile.status)
                    CapabilityKeyValue("Matching queue operation evidence", if (queueFamilies.isEmpty()) "No matching retained queue-operation token" else queueFamilies.joinToString { "Queue $it" })
                    CapabilityKeyValue("Bounded sampled-format rows", sampledFormats.size.toString())
                    profile.properties.firstOrNull { it.first == "codedExtent" }?.let { CapabilityKeyValue("Coded extent", it.second) }
                    profile.properties.firstOrNull { it.first == "DPB" }?.let { CapabilityKeyValue("Decoded picture buffer", it.second) }
                    profile.properties.firstOrNull { it.first == "rateControlModes" }?.let { CapabilityKeyValue("Rate-control modes", it.second) }
                } }
            }
            if (evidence.profiles.isEmpty()) item { EmptyState("No exact-profile Vulkan® Video evidence reported") }
        } else if (tab == 2) {
            item { CapabilitySectionCard("Decode profiles") { Text("H.264, H.265, VP9 and AV1 exact decode-profile combinations from the locked registry-driven census.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall) } }
            stickyCollectionPager(totalItems = decodeProfiles.size, currentPage = page, onPageChange = { page = it }, pagerKey = "video-decode-pager")
            items(decodeProfiles.drop(page * COLLECTION_PAGE_SIZE).take(COLLECTION_PAGE_SIZE), key = { "decode:${it.operation}:${it.variant}" }) { VideoProfileEvidenceCard(it) }
            if (decodeProfiles.isEmpty()) item { EmptyState("No decode-profile evidence reported") }
        } else if (tab == 3) {
            item { CapabilitySectionCard("Encode profiles") { Text("H.264, H.265 and AV1 exact encode-profile combinations from the locked registry-driven census.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall) } }
            stickyCollectionPager(totalItems = encodeProfiles.size, currentPage = page, onPageChange = { page = it }, pagerKey = "video-encode-pager")
            items(encodeProfiles.drop(page * COLLECTION_PAGE_SIZE).take(COLLECTION_PAGE_SIZE), key = { "encode:${it.operation}:${it.variant}" }) { VideoProfileEvidenceCard(it) }
            if (encodeProfiles.isEmpty()) item { EmptyState("No encode-profile evidence reported") }
        } else if (tab == 4) {
            item {
                CapabilitySectionCard("Sampled-profile formats") {
                    CapabilityKeyValue("Format query", formatQuery ?: "Unknown: sampled-profile format query status was not reported.")
                    Text("These rows preserve the existing bounded sampled-profile format recipes. A listed format is evidence only for the exact sampled profile and usage token shown in that row.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                }
            }
            stickyCollectionPager(totalItems = evidence.formats.size, currentPage = page, onPageChange = { page = it }, pagerKey = "video-formats-pager")
            items(evidence.formats.drop(page * COLLECTION_PAGE_SIZE).take(COLLECTION_PAGE_SIZE), key = { it.name }) { entry ->
                CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(entry.name.removePrefix("Video formats · "), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                    Text(entry.value, color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                } }
            }
            if (evidence.formats.isEmpty()) item { EmptyState("No sampled-profile Vulkan® Video format evidence reported") }
        } else {
            item {
                CapabilitySectionCard("Queue support") {
                    Text("Queue-family video flags and codec-operation masks are independent runtime evidence. A false queue flag remains an exact queried value and is not reclassified as Unsupported unless an owning support query establishes that state.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                }
            }
            val videoQueues = device?.queues.orEmpty()
            stickyCollectionPager(totalItems = videoQueues.size, currentPage = page, onPageChange = { page = it }, pagerKey = "video-queues-pager")
            items(videoQueues.drop(page * COLLECTION_PAGE_SIZE).take(COLLECTION_PAGE_SIZE), key = { it.index }) { queue ->
                CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Queue family ${queue.index}", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                        VideoEvidenceStateBadge(when (queue.videoCodecQueryStatus) {
                            "available" -> "AVAILABLE"
                            "incomplete" -> "INCOMPLETE"
                            "not_applicable" -> "NOT APPLICABLE"
                            "unavailable" -> "UNAVAILABLE"
                            else -> "UNKNOWN"
                        })
                    }
                    CapabilityKeyValue("Video decode flag", if (queue.videoDecode) "YES" else "NO")
                    CapabilityKeyValue("Video encode flag", if (queue.videoEncode) "YES" else "NO")
                    CapabilityKeyValue("Codec query", queueVideoCodecQueryState(queue))
                    if (queueVideoCodecEvidenceRetained(queue)) CapabilityKeyValue("Codec operations", videoCodecOperationFlags(queue.videoCodecOperations))
                } }
            }
            if (device?.queues.isNullOrEmpty()) item { EmptyState("Queue family data unavailable") }
        }
    }
}



internal const val ANALYSIS_MAX_SNAPSHOT_BYTES = 8 * 1024 * 1024
