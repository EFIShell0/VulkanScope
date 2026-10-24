@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.efishell.vulkanscope

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp



@Composable
internal fun AnalysisPage(report: VulkanReport, device: DeviceReport?, display: DisplayReport, driverMode: DriverMode, turnipSupport: TurnipSupport, collectionStatus: CollectionStatus, queryTimingMs: Map<String, Long>, onDriverModeChanged: (DriverMode) -> Unit) {
    var storageAction by remember { mutableStateOf<AnalysisStorageAction?>(null) }
    val analysisModel = rememberAnalysisWorkspaceModel(report, device, display, driverMode, turnipSupport, collectionStatus, queryTimingMs, onDriverModeChanged) { storageAction = it }
    val networkAvailable = LocalValidatedNetwork.current
    VulkanLazyPage(verticalSpacing = 12.dp) {
        analysisWorkspaceItems(analysisModel, report, device, networkAvailable)
    }
    val launchSharedStorageBrowser = LocalSharedStorageBrowserLauncher.current
    LaunchedEffect(storageAction) {
        val action = storageAction ?: return@LaunchedEffect
        val request = when (action) {
            AnalysisStorageAction.IMPORT_SNAPSHOT -> SharedStorageBrowserRequest(
                title = "Import analysis snapshot",
                description = "Choose a bounded VulkanScope analysis JSON snapshot from shared storage.",
                mode = SharedStorageBrowserMode.IMPORT,
                allowedExtensions = setOf("json"),
                maxImportBytes = ANALYSIS_MAX_SNAPSHOT_BYTES.toLong()
            )
            AnalysisStorageAction.EXPORT_SNAPSHOT -> SharedStorageBrowserRequest(
                title = "Export analysis snapshot",
                description = "Choose a shared-storage folder and file name for the validated analysis snapshot.",
                mode = SharedStorageBrowserMode.EXPORT,
                allowedExtensions = setOf("json"),
                suggestedFileName = "VulkanScope-${safeFilePart(device?.name ?: "Unknown-GPU")}-analysis.json"
            )
            AnalysisStorageAction.IMPORT_MINIMUM_PROFILE -> SharedStorageBrowserRequest(
                title = "Import minimum profile",
                description = "Choose a VulkanScope minimum-profile JSON file. Schema and rule bounds are validated before use.",
                mode = SharedStorageBrowserMode.IMPORT,
                allowedExtensions = setOf("json"),
                maxImportBytes = 256L * 1024L
            )
            AnalysisStorageAction.EXPORT_MINIMUM_PROFILE -> SharedStorageBrowserRequest(
                title = "Export minimum profile",
                description = "Choose a shared-storage folder for the bounded VulkanScope minimum-profile JSON.",
                mode = SharedStorageBrowserMode.EXPORT,
                allowedExtensions = setOf("json"),
                suggestedFileName = "VulkanScope-${safeFilePart(analysisModel.state.customProfileName.ifBlank { "profile" })}-minimum.json"
            )
            AnalysisStorageAction.EXPORT_TECHNICAL_REPORT -> SharedStorageBrowserRequest(
                title = "Export technicalReport JSON",
                description = "Choose a shared-storage folder for the exact bounded schema-v3 technicalReport JSON used by Database submission.",
                mode = SharedStorageBrowserMode.EXPORT,
                allowedExtensions = setOf("json"),
                suggestedFileName = "VulkanScope-${safeFilePart(device?.name ?: "Unknown-GPU")}-technicalReport.json"
            )
        }
        launchSharedStorageBrowser(
            SharedStorageBrowserOverlayRequest(
                request = request,
                onImport = { file ->
                    when (action) {
                        AnalysisStorageAction.IMPORT_SNAPSHOT -> analysisModel.importSnapshotFile(file)
                        AnalysisStorageAction.IMPORT_MINIMUM_PROFILE -> analysisModel.importMinimumProfileFile(file)
                        else -> Result.failure(IllegalStateException("This action does not import files"))
                    }
                },
                onExport = { file ->
                    when (action) {
                        AnalysisStorageAction.EXPORT_SNAPSHOT -> analysisModel.exportSnapshotFile(file)
                        AnalysisStorageAction.EXPORT_MINIMUM_PROFILE -> analysisModel.exportMinimumProfileFile(file)
                        AnalysisStorageAction.EXPORT_TECHNICAL_REPORT -> analysisModel.exportRawTechnicalReportFile(file)
                        else -> Result.failure(IllegalStateException("This action does not export files"))
                    }
                }
            )
        )
        storageAction = null
    }
    analysisModel.state.pendingMinimumConfirmation?.let { pending ->
        val title = when (pending.kind) {
            MinimumProfileConfirmationKind.SAVE -> "Save minimum profile?"
            MinimumProfileConfirmationKind.LOAD -> "Load minimum profile?"
            MinimumProfileConfirmationKind.DELETE -> "Delete minimum profile?"
        }
        val body = when (pending.kind) {
            MinimumProfileConfirmationKind.SAVE -> "Save ${pending.name.ifBlank { "this minimum" }} to VulkanScope private local minimum profiles? Existing entries with the same name are replaced only after you confirm."
            MinimumProfileConfirmationKind.LOAD -> "Load ${pending.name} into the custom minimum editor? Unsaved edits currently in the editor will be replaced."
            MinimumProfileConfirmationKind.DELETE -> "Delete ${pending.name} from VulkanScope private local minimum profiles? This cannot be undone."
        }
        AlertDialog(
            modifier = Modifier.border(1.dp, VulkanAccentSoft.copy(alpha = 0.46f), MaterialTheme.shapes.extraLarge),
            onDismissRequest = { analysisModel.state.pendingMinimumConfirmation = null },
            shape = MaterialTheme.shapes.extraLarge,
            containerColor = VulkanSurfaceRaised,
            titleContentColor = VulkanTextPrimary,
            textContentColor = VulkanTextPrimary,
            tonalElevation = 0.dp,
            title = { QuestionDialogTitle(title) },
            text = { Text(body, color = VulkanTextSecondary, style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                when (pending.kind) {
                    MinimumProfileConfirmationKind.SAVE -> ExpressiveContainedIconTextButton("Save", R.drawable.ic_save, fontWeight = FontWeight.Bold) {
                        analysisModel.state.pendingMinimumConfirmation = null
                        analysisModel.saveMinimumProfile()
                    }
                    MinimumProfileConfirmationKind.LOAD -> ExpressiveContainedIconTextButton("Load", R.drawable.ic_action_update, fontWeight = FontWeight.Bold) {
                        analysisModel.state.pendingMinimumConfirmation = null
                        analysisModel.loadMinimumProfile(pending.name)
                    }
                    MinimumProfileConfirmationKind.DELETE -> ExpressiveContainedIconTextButton("Delete", R.drawable.ic_delete, fontWeight = FontWeight.Bold) {
                        analysisModel.state.pendingMinimumConfirmation = null
                        analysisModel.deleteMinimumProfile(pending.name)
                    }
                }
            },
            dismissButton = { ExpressiveCancelButton { analysisModel.state.pendingMinimumConfirmation = null } }
        )
    }
    analysisModel.state.selectedEvidence?.let { selected ->
        EvidenceInspectorDialog(selected.first, selected.second, onDismiss = { analysisModel.state.selectedEvidence = null }, onWatch = analysisModel.addWatched)
    }
    analysisModel.state.pendingHistoryDelete?.let { record ->
        AlertDialog(
            modifier = Modifier.border(1.dp, VulkanAccentSoft.copy(alpha = 0.46f), MaterialTheme.shapes.extraLarge),
            onDismissRequest = { analysisModel.state.pendingHistoryDelete = null },
            title = { QuestionDialogTitle("Delete analysis history snapshot?") },
            text = { Text("This deletes the selected bounded local analysis snapshot from VulkanScope private storage. The action cannot be undone.") },
            confirmButton = {
                ExpressiveContainedIconTextButton("Yes", R.drawable.ic_delete) {
                    analysisModel.state.pendingHistoryDelete = null
                    analysisModel.deleteHistory(record)
                }
            },
            dismissButton = { ExpressiveCancelButton { analysisModel.state.pendingHistoryDelete = null } }
        )
    }
    if (analysisModel.state.pendingHistoryDeleteAll) {
        AlertDialog(
            modifier = Modifier.border(1.dp, VulkanAccentSoft.copy(alpha = 0.46f), MaterialTheme.shapes.extraLarge),
            onDismissRequest = { analysisModel.state.pendingHistoryDeleteAll = false },
            title = { QuestionDialogTitle("Delete all analysis history?") },
            text = { Text("Delete all ${analysisModel.state.history.size} retained local analysis snapshots? This does not change current Vulkan® capability evidence.") },
            confirmButton = {
                ExpressiveContainedIconTextButton("Delete all", R.drawable.ic_clear_all, fontWeight = FontWeight.Bold) {
                    analysisModel.state.pendingHistoryDeleteAll = false
                    analysisModel.deleteAllHistory()
                }
            },
            dismissButton = { ExpressiveCloseButton { analysisModel.state.pendingHistoryDeleteAll = false } }
        )
    }
    analysisModel.state.pendingWatchDelete?.let { token ->
        AlertDialog(
            modifier = Modifier.border(1.dp, VulkanAccentSoft.copy(alpha = 0.46f), MaterialTheme.shapes.extraLarge),
            onDismissRequest = { analysisModel.state.pendingWatchDelete = null },
            title = { QuestionDialogTitle("Remove watched evidence?") },
            text = { Text("Remove $token from the local watched-evidence list?") },
            confirmButton = {
                ExpressiveContainedIconTextButton("Delete", R.drawable.ic_delete, fontWeight = FontWeight.Bold) {
                    analysisModel.state.pendingWatchDelete = null
                    analysisModel.persistWatched(analysisModel.state.watched - token)
                }
            },
            dismissButton = { ExpressiveCloseButton { analysisModel.state.pendingWatchDelete = null } }
        )
    }
    if (analysisModel.state.pendingWatchDeleteAll) {
        AlertDialog(
            modifier = Modifier.border(1.dp, VulkanAccentSoft.copy(alpha = 0.46f), MaterialTheme.shapes.extraLarge),
            onDismissRequest = { analysisModel.state.pendingWatchDeleteAll = false },
            title = { QuestionDialogTitle("Delete all watched evidence?") },
            text = { Text("Delete all ${analysisModel.state.watched.size} entries from the local watched-evidence list? This does not change Vulkan® capability evidence.") },
            confirmButton = {
                ExpressiveContainedIconTextButton("Delete all", R.drawable.ic_clear_all, fontWeight = FontWeight.Bold) {
                    analysisModel.state.pendingWatchDeleteAll = false
                    analysisModel.persistWatched(emptySet())
                }
            },
            dismissButton = { ExpressiveCloseButton { analysisModel.state.pendingWatchDeleteAll = false } }
        )
    }
}
