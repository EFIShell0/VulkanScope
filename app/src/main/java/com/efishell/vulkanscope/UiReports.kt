@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.efishell.vulkanscope

import android.content.res.Configuration
import android.os.Build
import android.view.Surface
import android.util.Log
import android.content.Intent
import android.content.Context
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext




@Composable
internal fun InfoPage(report: VulkanReport, display: DisplayReport, mode: DriverMode, collectionStatus: CollectionStatus, onCheckForUpdates: () -> Unit, directUpdatesEnabled: Boolean, updateCheckInFlight: Boolean, showInfo: Boolean = true, showReporting: Boolean = true) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val networkAvailable = LocalValidatedNetwork.current
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()
    val installedAbi = remember { detectInstalledAbi(context) }
    val packageInfo = remember { runCatching { context.packageManager.getPackageInfo(context.packageName, 0) }.getOrNull() }
    val versionName = packageInfo?.versionName ?: "Unknown"
    val versionCode = if (packageInfo != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) packageInfo.longVersionCode.toString() else {
        @Suppress("DEPRECATION")
        packageInfo?.versionCode?.toString() ?: "Unknown"
    }
    val completeReportReady = isCompleteReportReady(report, collectionStatus)
    var submissionState by remember { mutableStateOf("Ready") }
    var submissionInFlight by remember { mutableStateOf(false) }
    var submissionSuccessId by remember { mutableStateOf<String?>(null) }
    var submissionFailureLog by remember { mutableStateOf<String?>(null) }
    var selectedLibraryLicense by remember { mutableStateOf<LibraryVersionInfo?>(null) }
    val exportStem = remember(report) { exportFileStem(report) }
    var pendingExportFilename by rememberSaveable { mutableStateOf("") }
    var pendingExportPath by rememberSaveable { mutableStateOf("") }
    var pendingExportMime by rememberSaveable { mutableStateOf("") }
    var exportPreparing by remember { mutableStateOf(false) }
    fun pendingSnapshot(): ExportSnapshot? {
        if (pendingExportFilename.isBlank() || pendingExportPath.isBlank() || pendingExportMime !in setOf("text/plain", "text/html", "application/json")) return null
        return ExportSnapshot(pendingExportFilename, pendingExportPath, pendingExportMime)
    }
    fun resetPendingSnapshot() {
        pendingExportFilename = ""
        pendingExportPath = ""
        pendingExportMime = ""
    }
    fun discardPendingSnapshot() {
        val snapshot = pendingSnapshot()
        resetPendingSnapshot()
        if (snapshot != null) scope.launch { withContext(Dispatchers.IO) { deleteExportSnapshot(context, snapshot) } }
    }
    fun prepareReportStorageExport(mime: String) {
        if (!completeReportReady || exportPreparing || pendingExportPath.isNotBlank() || mime !in setOf("text/plain", "text/html", "application/json")) return
        exportPreparing = true
        val isHtml = mime == "text/html"
        val isJson = mime == "application/json"
        val filename = "$exportStem.${if (isJson) "json" else if (isHtml) "html" else "txt"}"
        scope.launch {
            try {
                val snapshot = withContext(Dispatchers.IO) {
                    createExportSnapshot(context, filename, mime) {
                        when { isJson -> technicalReportJson(context, report, display, mode).toString(2); isHtml -> reportToHtml(context, report, display, mode); else -> reportToText(context, report, display, mode) }
                    }
                }
                pendingExportFilename = snapshot.filename
                pendingExportPath = snapshot.path
                pendingExportMime = snapshot.mime
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Throwable) {
                Log.e("VulkanScope", "Report snapshot creation failed", error)
                android.widget.Toast.makeText(context, "${if (isJson) "JSON" else if (isHtml) "HTML" else "TXT"} report could not be prepared", android.widget.Toast.LENGTH_SHORT).show()
            } finally {
                exportPreparing = false
            }
        }
    }
    val registryCoverage = report.registryCoverage
    LaunchedEffect(Unit) { withContext(Dispatchers.IO) { cleanupStaleExportSnapshots(context) } }
    submissionFailureLog?.let { log ->
        DatabaseSubmissionFailureDialog(log = log, onDismiss = { submissionFailureLog = null })
    }
    selectedLibraryLicense?.let { library ->
        LibraryLicenseDialog(library = library, onDismiss = { selectedLibraryLicense = null })
    }
    val launchSharedStorageBrowser = LocalSharedStorageBrowserLauncher.current
    val pendingStorageSnapshot = pendingSnapshot()
    LaunchedEffect(pendingStorageSnapshot?.path) {
        val snapshot = pendingStorageSnapshot ?: return@LaunchedEffect
        val mime = snapshot.mime
        val isHtml = mime == "text/html"
        val isJson = mime == "application/json"
        launchSharedStorageBrowser(
            SharedStorageBrowserOverlayRequest(
                request = SharedStorageBrowserRequest(
                    title = if (isJson) "Export JSON report" else if (isHtml) "Export HTML report" else "Export TXT report",
                    description = "Choose a shared-storage folder for the complete ${if (isJson) "structured JSON" else if (isHtml) "offline HTML" else "plain-text"} VulkanScope report.",
                    mode = SharedStorageBrowserMode.EXPORT,
                    allowedExtensions = setOf(if (isJson) "json" else if (isHtml) "html" else "txt"),
                    suggestedFileName = snapshot.filename
                ),
                onExport = { destination ->
                    try {
                        val saved = withContext(Dispatchers.IO) {
                            val target = validatedSharedStorageDestination(
                                destination.parentFile ?: error("Destination folder is unavailable"),
                                destination.name,
                                setOf(if (isJson) "json" else if (isHtml) "html" else "txt")
                            )
                            val source = validatedExportSnapshot(context, snapshot)
                            copySharedStorageFile(target, source)
                            target
                        }
                        android.widget.Toast.makeText(context, "${if (isJson) "JSON" else if (isHtml) "HTML" else "TXT"} report saved to ${saved.absolutePath}", android.widget.Toast.LENGTH_LONG).show()
                        Result.success("Saved ${saved.name}")
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    } catch (error: Throwable) {
                        Result.failure(error)
                    }
                },
                onClosed = { discardPendingSnapshot() }
            )
        )
    }
    VulkanLazyPage(verticalSpacing = 14.dp) {
        if (showInfo) item {
            CapabilitySectionCard("Developer") {
                ExpressiveIdentityBlock("Semih Boran", "EFI Shell · VulkanScope developer", R.drawable.ic_person)
                ExpressiveExternalLinkRow("Open GitHub profile", if (networkAvailable) "EFIShell0 · Projects and public profile" else "Unavailable without a validated internet connection", R.drawable.ic_action_github, enabled = networkAvailable) { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/EFIShell0"))) } }
            }
        }
        if (showInfo) item {
            CapabilitySectionCard("Application") {
                ExpressiveVersionBlock("VulkanScope", versionName, versionCode, context.packageName, installedAbi)
                ExpressiveActionButton("Check for updates", when { !directUpdatesEnabled -> "Direct GitHub updates are disabled in Settings"; !networkAvailable -> "Unavailable without a validated internet connection"; !updateCheckInFlight -> "Official EFIShell0/VulkanScope GitHub release channel"; else -> "Checking official release channel…" }, R.drawable.ic_download, enabled = directUpdatesEnabled && networkAvailable && !updateCheckInFlight, trailingIcon = R.drawable.ic_receive, onClick = onCheckForUpdates)
                ExpressiveExternalLinkRow("Open GitHub repository", if (networkAvailable) "Source, releases and project history" else "Unavailable without a validated internet connection", R.drawable.ic_action_github, enabled = networkAvailable) { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/EFIShell0/VulkanScope"))) } }
                Text(if (directUpdatesEnabled) "Direct update checks use the official VulkanScope GitHub release channel. APK download still requires explicit review and confirmation." else "Direct GitHub update checks are currently disabled. Obtainium can manage updates from the official GitHub Releases source without VulkanScope running its own update discovery.", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
            }
        }
        if (showInfo) item {
            CapabilitySectionCard("Libraries") {
                Text("Direct application and native library identities are reported from the release's pinned build configuration. Build tools are listed separately and are not presented as runtime libraries.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                VULKANSCOPE_LIBRARY_VERSIONS.forEach { library ->
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        CapabilityKeyValue(library.name, library.version)
                        Text(trademarkVulkanDisplayText(library.detail), color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                        Text(library.licenseName, color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            ChevronAffordance("License", "Open ${library.name} license") { selectedLibraryLicense = library }
                        }
                    }
                }
            }
        }
        if (showInfo) item {
            CapabilitySectionCard("Build toolchain") {
                CapabilityKeyValue("Android Gradle Plugin", "9.4.1")
                CapabilityKeyValue("Kotlin Compose plugin", "2.4.21")
                CapabilityKeyValue("Gradle wrapper", "9.8.1")
                CapabilityKeyValue("Android NDK", "29.0.14206865")
                CapabilityKeyValue("C++ language level", "C++20")
                CapabilityKeyValue("CMake minimum", "3.22.1")
            }
        }
        if (showInfo) item {
            CapabilitySectionCard("Device ABI") {
                CapabilityKeyValue("Installed ABI", installedAbi)
                CapabilityKeyValue("Supported ABIs", Build.SUPPORTED_ABIS.joinToString(", "))
                Text("Installed ABI is the native ABI used by this VulkanScope installation; supported ABIs are the ABIs reported by Android for the device.", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
            }
        }
        if (showInfo) item {
            CapabilitySectionCard("Android") {
                CapabilityKeyValue("Platform", platformRuntimeName(context))
                CapabilityKeyValue("Host OS version", hostPlatformVersionEvidence(context))
                CapabilityKeyValue("ChromeOS ARC runtime", if (isChromeOsRuntime(context)) "Detected" else "Not detected")
                CapabilityKeyValue("Android PC form factor", if (isAndroidPcFormFactor(context)) "Detected" else "Not detected")
                CapabilityKeyValue("Freeform window management", if (hasFreeformWindowManagement(context)) "Detected" else "Not detected")
                CapabilityKeyValue("Googlebook environment", googlebookEnvironmentEvidence(context))
                CapabilityKeyValue("Googlebook OS version", googlebookOsVersionEvidence())
                CapabilityKeyValue("Manufacturer", Build.MANUFACTURER)
                CapabilityKeyValue("Brand", Build.BRAND)
                CapabilityKeyValue("Model", Build.MODEL)
                CapabilityKeyValue("Android", Build.VERSION.RELEASE)
                CapabilityKeyValue("SDK", Build.VERSION.SDK_INT.toString())
                CapabilityKeyValue("Security patch", Build.VERSION.SECURITY_PATCH.ifBlank { "Unavailable" })
                CapabilityKeyValue("Codename", Build.VERSION.CODENAME)
                CapabilityKeyValue("Product", Build.PRODUCT)
                CapabilityKeyValue("Device", Build.DEVICE)
                CapabilityKeyValue("Board", Build.BOARD)
                CapabilityKeyValue("Hardware", Build.HARDWARE)
                CapabilityKeyValue("Build ID", Build.ID)
                CapabilityKeyValue("Incremental", Build.VERSION.INCREMENTAL)
                CapabilityKeyValue("Build fingerprint", Build.FINGERPRINT)
            }
        }
        if (showInfo) item {
            CapabilitySectionCard("Vulkan registry / query engine") {
                CapabilityKeyValue("Current published specification", "Vulkan® 1.4.366 · 2026-09-25")
                CapabilityKeyValue("Collection baseline", registryCoverage.baseline)
                CapabilityKeyValue("Engine", registryCoverage.mode)
                CapabilityKeyValue("Physical-device structs", registryCoverage.implementedPhysicalDeviceStructCount.toString())
                CapabilityKeyValue("Validated query groups", registryCoverage.validatedRuntimeQueryGroupCount.toString())
                CapabilityKeyValue("Runtime registry token references", registryCoverage.runtimeRegistryTokenReferenceCount.toString())
                CapabilityKeyValue("Catalog schema", registryCoverage.catalogSchemaVersion.toString())
                CapabilityKeyValue("Registry report schema", registryCoverage.reportSchema)
                CapabilityKeyValue("Header baseline", registryCoverage.headerBaseline)
                CapabilityKeyValue("Instance dependency candidates", registryCoverage.instanceDependencyCandidateCount.toString())
                Text("Registry metadata is build-time/offline. Runtime does not download or parse the Khronos registry. Unknown structures remain unavailable unless a validated native query path exists.", color = ComposeColor(0xFF8F8F8F), style = MaterialTheme.typography.bodySmall)
            }
        }
        if (showInfo) item {
            CapabilitySectionCard("About") {
                Text("VulkanScope is independent and is not affiliated with or endorsed by the Khronos® Group.", color = ComposeColor(0xFFFFC857), fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Text("VulkanScope is a Vulkan® capability and device inspection utility for Android. It reports information exposed by the active Vulkan® implementation and selected driver mode while keeping Android display/HDR evidence separate from Vulkan® capability claims.", color = ComposeColor(0xFFB0B0B0))
                HorizontalDivider(color = VulkanOutlineVariant)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Trademarks and artwork", color = VulkanTextPrimary, fontWeight = FontWeight.SemiBold)
                }
                Text("Khronos and the Khronos Group logo are registered trademarks of the Khronos Group Inc.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                Text("Vulkan and the Vulkan logo are registered trademarks of the Khronos Group Inc.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                Text("SPIR, SPIR-V and the SPIR logo are trademarks of the Khronos Group Inc.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                Text("Other vendor/device, HDR and platform logos and names remain the property of their respective owners. Display here identifies technology; it does not imply sponsorship or certification.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                ExpressiveExternalLinkRow("Official trademark notices", "Khronos® Group trademarks and logo attribution", R.drawable.ic_khronos_swirl, enabled = networkAvailable) { uriHandler.openUri("https://www.khronos.org/legal/trademarks/") }
            }
        }
        if (showReporting) item {
            CapabilitySectionCard("Export complete report") {
                Text("TXT/HTML/JSON export uses VulkanScope's in-app shared-storage browser. Storage access is requested only when you explicitly start an export. Report serialization runs off the UI thread through a private temporary snapshot and the destination write is atomic.", color = ComposeColor(0xFFB6ACAE), style = MaterialTheme.typography.bodySmall)
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SharedStoragePermissionActionButton(
                        "Export TXT",
                        when { exportPreparing -> "Preparing private report snapshot…"; completeReportReady -> "Plain-text complete report · choose shared-storage destination"; else -> "Waiting for complete Vulkan® collection" },
                        R.drawable.ic_action_text,
                        Modifier.fillMaxWidth(),
                        completeReportReady && !exportPreparing && pendingExportPath.isBlank(),
                        false
                    ) { prepareReportStorageExport("text/plain") }
                    SharedStoragePermissionActionButton(
                        "Export HTML",
                        when { exportPreparing -> "Preparing private report snapshot…"; completeReportReady -> "Styled offline complete report · choose shared-storage destination"; else -> "Waiting for complete Vulkan® collection" },
                        R.drawable.ic_action_html,
                        Modifier.fillMaxWidth(),
                        completeReportReady && !exportPreparing && pendingExportPath.isBlank(),
                        false
                    ) { prepareReportStorageExport("text/html") }
                    SharedStoragePermissionActionButton(
                        "Export JSON",
                        when { exportPreparing -> "Preparing private report snapshot…"; completeReportReady -> "Exact local technicalReport schema-v3 JSON · choose shared-storage destination"; else -> "Waiting for complete Vulkan® collection" },
                        R.drawable.ic_export,
                        Modifier.fillMaxWidth(),
                        completeReportReady && !exportPreparing && pendingExportPath.isBlank(),
                        false
                    ) { prepareReportStorageExport("application/json") }
                }
                if (!completeReportReady) Text("TXT, HTML and JSON export remain disabled until the complete Vulkan® collection pass has finished, matching the Database completeness gate.", color = ComposeColor(0xFFFFC857), style = MaterialTheme.typography.bodySmall)
            }
        }
        if (showReporting) item {
            CapabilitySectionCard("VulkanScope Database") {
                Text("Submit the complete technical VulkanScope report to the public database. Capability fields cannot be selectively omitted, and sensitive device identifiers or private paths are not included.", color = ComposeColor(0xFFB6ACAE), style = MaterialTheme.typography.bodySmall)
                TransientActionButton(if (submissionInFlight) "Submitting…" else "Submit complete report", when {
                    submissionInFlight -> "Uploading the complete technical dataset"
                    !completeReportReady && !networkAvailable -> "Waiting for complete Vulkan collection · internet unavailable"
                    !completeReportReady -> "Waiting for complete Vulkan collection"
                    !networkAvailable -> "Unavailable without a validated internet connection"
                    else -> "Structured JSON + canonical TXT report"
                }, R.drawable.ic_database_submit, enabled = !submissionInFlight && completeReportReady && networkAvailable, idleTrailingIcon = R.drawable.ic_upload) {
                    if (submissionInFlight) false else {
                        submissionInFlight = true
                        submissionSuccessId = null
                        submissionFailureLog = null
                        submissionState = "Submitting complete technical report…"
                        try {
                            val result = submitDatabaseReport(context, report, display, mode)
                            submissionState = result.summary
                            if (result.success) {
                                submissionSuccessId = result.reportId
                            } else {
                                submissionFailureLog = result.log
                            }
                            result.success
                        } catch (error: CancellationException) {
                            throw error
                        } catch (error: Throwable) {
                            submissionState = "Submission failed: ${error.message ?: error.javaClass.simpleName}"
                            submissionFailureLog = databaseSubmissionExceptionLog("unexpected", error)
                            false
                        } finally {
                            submissionInFlight = false
                        }
                    }
                }
                Text("Compatibility notice: when a newer VulkanScope release raises the Database submission floor, reports from older app versions are rejected by the server. A rejection is returned as a submission error and is shown here instead of being treated as a successful upload.", color = ComposeColor(0xFFFFC857), style = MaterialTheme.typography.bodySmall)
                DatabaseSubmissionStatusCard(
                    status = submissionState,
                    uploading = submissionInFlight,
                    hasCompleteReport = completeReportReady,
                    hasValidatedNetwork = networkAvailable,
                    succeeded = submissionSuccessId != null,
                    failed = submissionFailureLog != null
                )
                submissionSuccessId?.let { reportId ->
                    Text("Report ID", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = VulkanSurfaceLow,
                        contentColor = VulkanTextPrimary,
                        border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(start = 14.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                reportId,
                                modifier = Modifier.weight(1f),
                                color = VulkanTextPrimary,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            TransientIconButton(
                                idleIcon = R.drawable.ic_copy,
                                contentDescription = "Copy report ID"
                            ) {
                                runCatching { copyEvidenceText(context, "VulkanScope report ID", reportId) }.isSuccess
                            }
                        }
                    }
                    ExpressiveContainedIconTextButton(
                        "Open report",
                        R.drawable.ic_open_external,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = networkAvailable
                    ) { uriHandler.openUri(databaseReportUrl(reportId)) }
                }
                if (!completeReportReady && !networkAvailable) {
                    Text("Database submission is locked for two independent reasons: Vulkan® collection is incomplete and Android does not report a validated internet connection.", color = ComposeColor(0xFFFFC857), style = MaterialTheme.typography.bodySmall)
                    Text("When internet returns during collection, the network lock clears immediately; submission still waits for complete report evidence.", color = ComposeColor(0xFF9CCBFF), style = MaterialTheme.typography.bodySmall)
                } else {
                    if (!completeReportReady) Text("Wait for the complete Vulkan® collection pass to finish before submitting.", color = ComposeColor(0xFFFFC857), style = MaterialTheme.typography.bodySmall)
                    if (!networkAvailable) Text("Database upload and public report browsing are disabled until Android reports a validated internet connection.", color = ComposeColor(0xFF9CCBFF), style = MaterialTheme.typography.bodySmall)
                }
                ExpressiveExternalLinkRow("Open VulkanScope Database", if (networkAvailable) "Browse public VulkanScope hardware reports" else "Unavailable without a validated internet connection", R.drawable.ic_database_browse, enabled = networkAvailable) { uriHandler.openUri(OFFICIAL_DATABASE_WEB_URL) }
            }
        }
    }
}

internal fun detectInstalledAbi(context: Context): String {
    val nativeDir = context.applicationInfo.nativeLibraryDir.orEmpty().lowercase()
    return when {
        nativeDir.contains("arm64") -> "arm64-v8a"
        nativeDir.contains("x86_64") -> "x86_64"
        nativeDir.contains("x86") -> "x86"
        else -> Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown"
    }
}
@Composable
internal fun SettingsSectionCards(onSelected: (SettingsSection) -> Unit) {
    var cardsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { cardsVisible = true }
    SectionDestinationGrid(SettingsSection.entries.toList()) { section, index ->
        AnimatedSettingsSectionCard(cardsVisible, index, section, onSelected)
    }
}

@Composable
private fun AnimatedSettingsSectionCard(
    visible: Boolean,
    index: Int,
    section: SettingsSection,
    onSelected: (SettingsSection) -> Unit
) {
    val enterDelay = index * 45
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(durationMillis = 260, delayMillis = enterDelay)) +
            slideInHorizontally(tween(durationMillis = 260, delayMillis = enterDelay)) { it / 10 },
        exit = fadeOut(tween(durationMillis = 120))
    ) {
        ExpressiveDestinationCard(section.label, section.description, section.icon) { onSelected(section) }
    }
}

@Composable
internal fun DatabaseSubmissionFailureDialog(log: String, onDismiss: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scrollState = rememberScrollState()
    AlertDialog(
            modifier = Modifier.border(1.dp, VulkanAccentSoft.copy(alpha = 0.46f), MaterialTheme.shapes.extraLarge),
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = VulkanSurfaceRaised,
        tonalElevation = 0.dp,
        title = { DatabaseFailureDialogTitle() },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("The Database submission did not complete successfully. The complete bounded submission log is shown below.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                Surface(shape = MaterialTheme.shapes.medium, color = ComposeColor(0xFF0D0D0D), border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant)) {
                    Text(
                        log,
                        modifier = Modifier.fillMaxWidth().heightIn(max = 360.dp).desktopVerticalPointerScroll(scrollState).dpadScrollableNavigation(scrollState).verticalScroll(scrollState).padding(14.dp),
                        color = VulkanTextPrimary,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        },
        confirmButton = { ExpressiveContainedIconTextButton("Copy all", R.drawable.ic_copy) { copyEvidenceText(context, "VulkanScope Database submission log", log) } },
        dismissButton = { ExpressiveContainedIconTextButton("Close", R.drawable.ic_close, onClick = onDismiss) }
    )
}

@Composable
internal fun DatabaseFailureDialogTitle() {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(shape = RoundedCornerShape(18.dp), color = VulkanAccentContainer) {
            Box(Modifier.padding(8.dp).size(24.dp)) {
                Icon(painterResource(R.drawable.ic_database_submit), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.align(Alignment.TopStart).size(19.dp))
                Surface(shape = RoundedCornerShape(6.dp), color = VulkanSurfaceRaised, modifier = Modifier.align(Alignment.BottomEnd)) {
                    Icon(painterResource(R.drawable.ic_close), contentDescription = null, tint = ComposeColor(0xFFFF6B6B), modifier = Modifier.padding(1.dp).size(10.dp))
                }
            }
        }
        Text("Database submission failed", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = VulkanTextPrimary, modifier = Modifier.weight(1f))
    }
}

@Composable
internal fun TransientActionButton(
    title: String,
    subtitle: String,
    icon: Int,
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true,
    idleTrailingIcon: Int = icon,
    action: suspend () -> Boolean
) {
    val scope = rememberCoroutineScope()
    var state by remember { mutableStateOf(0) }
    val busy = state != 0
    val trailing = when (state) {
        2 -> R.drawable.ic_check
        3 -> R.drawable.ic_close
        else -> idleTrailingIcon
    }
    val trailingTint = when (state) {
        2 -> ComposeColor(0xFF73C991)
        3 -> ComposeColor(0xFFFF6B6B)
        else -> VulkanAccentSoft
    }
    val semanticTitle = when (state) {
        2 -> "$title · completed"
        3 -> "$title · failed"
        else -> title
    }
    val semanticSubtitle = when (state) {
        2 -> "Completed successfully"
        3 -> "The action failed"
        else -> subtitle
    }
    ExpressiveActionButton(
        semanticTitle,
        semanticSubtitle,
        icon,
        modifier,
        enabled && !busy,
        trailingIcon = trailing,
        trailingTint = trailingTint,
    ) {
        if (!enabled || busy) return@ExpressiveActionButton
        state = 1
        scope.launch {
            val success = try {
                action()
            } catch (error: CancellationException) {
                state = 0
                throw error
            } catch (_: Throwable) {
                false
            }
            state = if (success) 2 else 3
            delay(3000)
            state = 0
        }
    }
}

@Composable
internal fun TransientIconButton(
    idleIcon: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    action: suspend () -> Boolean
) {
    val scope = rememberCoroutineScope()
    var state by remember { mutableStateOf(0) }
    val busy = state != 0
    val icon = when (state) {
        2 -> R.drawable.ic_check
        3 -> R.drawable.ic_close
        else -> idleIcon
    }
    val tint = when (state) {
        2 -> ComposeColor(0xFF73C991)
        3 -> ComposeColor(0xFFFF6B6B)
        else -> VulkanAccentSoft
    }
    val container = when (state) {
        2 -> ComposeColor(0xFF173524)
        3 -> ComposeColor(0xFF3B1B1E)
        else -> VulkanAccentContainer
    }
    IconButton(
        onClick = {
            if (!enabled || busy) return@IconButton
            state = 1
            scope.launch {
                val success = try { action() }
                catch (cancelled: CancellationException) { state = 0; throw cancelled }
                catch (_: Throwable) { false }
                state = if (success) 2 else 3
                delay(3000L)
                state = 0
            }
        },
        enabled = enabled && !busy,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = container,
            contentColor = tint,
            disabledContainerColor = container,
            disabledContentColor = tint
        ),
        modifier = modifier
    ) {
        AnimatedContent(
            targetState = icon,
            transitionSpec = { (fadeIn(tween(180)) + scaleIn(tween(220), initialScale = 0.72f)) togetherWith (fadeOut(tween(120)) + scaleOut(tween(150), targetScale = 0.82f)) },
            label = "transientIcon"
        ) { targetIcon ->
            Icon(painterResource(targetIcon), contentDescription = if (state == 2) "$contentDescription completed" else contentDescription, modifier = Modifier.size(19.dp))
        }
    }
}

@Composable
internal fun DatabaseSubmissionStatusCard(
    status: String,
    uploading: Boolean,
    hasCompleteReport: Boolean,
    hasValidatedNetwork: Boolean,
    succeeded: Boolean,
    failed: Boolean
) {
    val ready = !uploading && !failed && !succeeded && hasCompleteReport && hasValidatedNetwork
    val accent = when {
        failed -> ComposeColor(0xFFFF858C)
        uploading -> ComposeColor(0xFF91BFFF)
        succeeded || ready -> ComposeColor(0xFF80DFA8)
        else -> ComposeColor(0xFFFFC77D)
    }
    val headline = when {
        uploading -> "Submitting report"
        failed -> "Submission unsuccessful"
        succeeded -> "Report submitted"
        !hasCompleteReport && !hasValidatedNetwork -> "Collection and connection required"
        !hasCompleteReport -> "Collection in progress"
        !hasValidatedNetwork -> "Connection unavailable"
        else -> "Ready to submit"
    }
    val explanation = when {
        uploading -> "The complete report is being transferred. Keep VulkanScope open until the request finishes."
        failed -> status.ifBlank { "The request did not complete. Review the submission details and try again." }
        succeeded -> "Your report was accepted. Open its report ID below to view the submitted record."
        !hasCompleteReport && !hasValidatedNetwork -> "Complete Vulkan collection and connect to a validated network before submitting."
        !hasCompleteReport -> "Wait for complete report collection. Local inspection remains available."
        !hasValidatedNetwork -> "No validated internet connection. You can still inspect and export the report locally."
        else -> "Collection is complete and a validated connection is available. Nothing is uploaded until you press Submit."
    }
    val label = when {
        uploading -> "IN PROGRESS"
        failed -> "ACTION REQUIRED"
        succeeded -> "COMPLETE"
        ready -> "AVAILABLE"
        else -> "NOT AVAILABLE"
    }
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = VulkanSurfaceRaised,
        contentColor = VulkanTextPrimary,
        border = androidx.compose.foundation.BorderStroke(1.dp, accent.copy(alpha = 0.38f)),
        modifier = Modifier.fillMaxWidth().semantics(mergeDescendants = true) { liveRegion = androidx.compose.ui.semantics.LiveRegionMode.Polite }
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = accent, modifier = Modifier.width(4.dp).heightIn(min = 122.dp)) { }
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 17.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Text(label, color = accent, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text(headline, color = VulkanTextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(explanation, color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
