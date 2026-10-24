@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.efishell.vulkanscope

import android.graphics.Color
import android.graphics.Shader
import android.content.res.Configuration
import android.view.Surface
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.border
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay



internal fun historyLabel(record: AnalysisHistoryRecord): String = java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT, java.text.DateFormat.SHORT).format(java.util.Date(record.timestampMs))

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
internal fun LazyListScope.analysisWorkspaceItems(model: AnalysisWorkspaceModel, report: VulkanReport, device: DeviceReport?, networkAvailable: Boolean) {
    val state = model.state
    fun analysisPage(total: Int): Int = state.contentPage.coerceIn(0, maxOf(0, (total + COLLECTION_PAGE_SIZE - 1) / COLLECTION_PAGE_SIZE - 1))
    fun <T> analysisSlice(source: List<T>): List<T> {
        val page = analysisPage(source.size)
        return source.drop(page * COLLECTION_PAGE_SIZE).take(COLLECTION_PAGE_SIZE)
    }
    fun analysisPager(total: Int, key: String = "analysis-primary-${state.tab}") {
        stickyCollectionPager(totalItems = total, currentPage = analysisPage(total), onPageChange = { state.contentPage = it }, pagerKey = key)
    }
    val tabs = listOf("Compare", "Search", "Drivers", "Diagnostics", "Requirements", "Minimums", "Graph", "Presentation", "Raw JSON", "Database", "History", "Watched", "Score", "Share", "Tests")
    item {
        CapabilitySectionCard("Analysis workspace") {
            Text("Analysis, history, custom minimums and optional tests stay local unless you explicitly fetch a public Database report or share a link. None of these tools mutate canonical TXT, HTML or Database capability evidence.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
            Text("Analysis JSON exchange uses VulkanScope's in-app shared-storage browser; storage access is requested only after an explicit import/export action.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
            ExpressiveFilterBar(tabs, state.tab) { state.tab = it; state.contentPage = 0 }
        }
    }
    when (state.tab) {
        0 -> {
            item { CapabilitySectionCard("Offline report compare") {
                CapabilityKeyValue("Snapshot status", state.analysisStatus)
                SharedStoragePermissionActionButton("Import analysis snapshot", "Open the in-app shared-storage browser · JSON · 8 MiB bound", R.drawable.ic_action_import) { model.importSnapshot() }
                SharedStoragePermissionActionButton("Export analysis snapshot", "Choose a shared-storage folder and JSON file name", R.drawable.ic_export) { model.exportSnapshot() }
                if (state.baseline != null) {
                    ExpressiveToggleRow("Show unchanged", "Include evidence rows whose canonical value is unchanged.", state.includeUnchanged) { state.includeUnchanged = it; state.contentPage = 0 }
                    ExpressiveFilterBar(listOf("All", "Added", "Removed", "Changed", "Regression"), listOf("All", "Added", "Removed", "Changed", "Regression").indexOf(state.diffStateFilter).coerceAtLeast(0)) { state.diffStateFilter = listOf("All", "Added", "Removed", "Changed", "Regression")[it]; state.contentPage = 0 }
                    ExpressiveSearchField(value = state.diffQuery, onValueChange = { state.diffQuery = it; state.contentPage = 0 }, modifier = Modifier.fillMaxWidth(), placeholderText = "Search evidence differences…")
                }
            } }
            if (state.baseline != null) {
                item { CapabilitySectionCard("Diff summary") {
                    ExpressiveMetric("Added", model.diffRows.count { it.state == "Added" }.toString())
                    ExpressiveMetric("Removed", model.diffRows.count { it.state.startsWith("Removed") }.toString())
                    ExpressiveMetric("Changed", model.diffRows.count { it.state.startsWith("Changed") }.toString())
                    ExpressiveMetric("Regression candidates", model.diffRows.count { it.state.contains("regression candidate") }.toString())
                    Text("Regression candidates are evidence changes only; VulkanScope does not declare a driver defect from a diff alone.", color = VulkanTextMuted, style = MaterialTheme.typography.bodySmall)
                } }
                analysisPager(model.diffRows.size)
                items(analysisSlice(model.diffRows), key = { it.key }) { row ->
                    CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        CapabilityKeyValue(row.key, row.state)
                        row.baseline?.let { CapabilityKeyValue("Baseline", it) }
                        row.current?.let { CapabilityKeyValue("Current", it) }
                        ExpressiveContainedIconTextButton("Evidence provenance", R.drawable.ic_evidence) { state.selectedEvidence = row.key to (row.current ?: row.baseline.orEmpty()) }
                    } }
                }
            }
        }
        1 -> {
            item { CapabilitySectionCard("Global Vulkan report search") {
                Text("Searches the completed current-device evidence model. Registry/reference symbols remain separate and can be opened in Encyclopedia from each result.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                ExpressiveSearchField(value = state.globalQuery, onValueChange = { state.globalQuery = it; state.contentPage = 0 }, modifier = Modifier.fillMaxWidth(), placeholderText = "Search descriptorBuffer, maxImageDimension2D, HDR10…")
                ExpressiveFilterBar(listOf("All", "extension", "feature", "property", "limit", "format", "surface", "display", "queue", "profile", "query"), listOf("All", "extension", "feature", "property", "limit", "format", "surface", "display", "queue", "profile", "query").indexOf(state.globalKindFilter).coerceAtLeast(0)) { state.globalKindFilter = listOf("All", "extension", "feature", "property", "limit", "format", "surface", "display", "queue", "profile", "query")[it] }
                CapabilityKeyValue("Visible result bound", "$ANALYSIS_GLOBAL_SEARCH_VISIBLE_LIMIT")
            } }
            if (state.globalQuery.trim().length < 2) item { EmptyState("Type at least two characters to search the complete current report") }
            analysisPager(model.globalResults.size)
            items(analysisSlice(model.globalResults), key = { it.first }) { result ->
                CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    CapabilityKeyValue(result.first, result.second)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) { DetailAffordance { state.selectedEvidence = result } }
                } }
            }
        }
        2 -> {
            item { CapabilitySectionCard("System driver ↔ Turnip A/B") {
                Text("Each completed driver session is captured into bounded local history. Switching drivers remains an explicit user action and uses VulkanScope's existing driver-change/collection path.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                CapabilityKeyValue("System snapshot", model.systemHistory?.let(::historyLabel) ?: "No completed local System session")
                CapabilityKeyValue("Turnip snapshot", model.turnipHistory?.let(::historyLabel) ?: "No completed local Turnip session")
                CapabilityKeyValue("Guided workflow", state.abWorkflowStatus)
                ExpressiveActionButton("Run guided System ↔ Turnip A/B", "One explicit workflow retains the current completed session, collects the missing System/Turnip side, then exposes the local evidence diff", R.drawable.ic_compare, enabled = model.canSwitchSystem || model.canSwitchTurnip) { model.startDriverAbWorkflow() }
                if (state.abWorkflowStage in setOf("collect-system", "collect-turnip")) ExpressiveTextButton("Cancel guided A/B") { model.cancelDriverAbWorkflow() }
                ExpressiveActionButton("Switch to System and collect", "Manual single-side collection using the existing driver selection workflow", R.drawable.ic_compare, enabled = model.canSwitchSystem) { model.switchToSystem() }
                ExpressiveActionButton("Switch to Turnip and collect", "Manual single-side collection; requires a validated installed compatible driver bundle", R.drawable.ic_compare, enabled = model.canSwitchTurnip) { model.switchToTurnip() }
                Text("The guided workflow may perform the second driver switch after the first requested collection completes because that continuation was explicitly authorized by Run guided A/B. No driver mutation starts without that user action, and no network upload is involved.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
            } }
            if (model.systemHistory != null && model.turnipHistory != null) {
                item { CapabilitySectionCard("A/B summary") {
                    ExpressiveMetric("Changed evidence", model.driverDiffRows.size.toString())
                    ExpressiveMetric("Added", model.driverDiffRows.count { it.state == "Added" }.toString())
                    ExpressiveMetric("Removed", model.driverDiffRows.count { it.state.startsWith("Removed") }.toString())
                    ExpressiveMetric("Regression candidates", model.driverDiffRows.count { it.state.contains("regression candidate") }.toString())
                    Text("The baseline is the latest System session and the current side is the latest Turnip session. Differences are evidence changes, not performance or quality rankings.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                } }
                val boundedDriverDiff = model.driverDiffRows.take(ANALYSIS_GLOBAL_SEARCH_VISIBLE_LIMIT)
                analysisPager(boundedDriverDiff.size)
                items(analysisSlice(boundedDriverDiff), key = { "ab:${it.key}" }) { row -> CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    CapabilityKeyValue(row.key, row.state)
                    row.baseline?.let { CapabilityKeyValue("System", it) }
                    row.current?.let { CapabilityKeyValue("Turnip", it) }
                } } }
            } else item { EmptyState("Collect at least one completed System session and one completed Turnip session") }
        }
        3 -> {
            val totalMs = model.timingsMs["collection/total"]
            val baseMs = model.timingsMs["collection/base_total"]
            val enrichmentMs = model.timingsMs["collection/enrichment_total"]
            val backgroundMs = model.timingsMs["collection/background_total"]
            val probeTotals = model.timingsMs.filterKeys { it.startsWith("probe_total/") }
            val queueWaits = model.timingsMs.filterKeys { it.startsWith("probe_queue_wait/") }
            val slowestProbe = probeTotals.maxByOrNull { it.value }
            val longestQueueWait = queueWaits.maxByOrNull { it.value }
            item {
                CapabilitySectionCard("Diagnostic collection") {
                    Text("Measured collection timing is grouped by orchestration, dedicated-probe work and scheduler wait. Main values use seconds for quick reading and retain the exact millisecond measurement in parentheses. Missing timing remains unavailable rather than inferred.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                    totalMs?.let {
                        Surface(shape = MaterialTheme.shapes.extraLarge, color = VulkanAccentContainer, contentColor = VulkanTextPrimary, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.44f))) {
                            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Complete collection", color = VulkanAccentSoft, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                Text(formatAnalysisElapsedTime(it), color = VulkanTextPrimary, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                                Text("App-side measured end-to-end collection interval", color = VulkanTextSecondary, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        baseMs?.let { ExpressiveInfoPill("Base", formatAnalysisElapsedTime(it)) }
                        enrichmentMs?.let { ExpressiveInfoPill("Metadata + Surface", formatAnalysisElapsedTime(it)) }
                        backgroundMs?.let { ExpressiveInfoPill("Background", formatAnalysisElapsedTime(it)) }
                        model.timingsMs["collection/background_process_lanes"]?.let { ExpressiveInfoPill("Isolated lanes", it.toString()) }
                    }
                }
            }
            item {
                CapabilitySectionCard("Probe and scheduler timing") {
                    ExpressiveMetricGrid(
                        listOf(
                            "Dedicated probes" to probeTotals.size.toString(),
                            "Diagnostic rows" to model.diagnostics.size.toString(),
                            "Safety rejections" to model.diagnostics.count { it.state == "SAFETY REJECTED" }.toString(),
                            "Incomplete" to model.diagnostics.count { it.state == "INCOMPLETE" }.toString()
                        )
                    )
                    slowestProbe?.let {
                        CapabilityKeyValue("Slowest dedicated probe", "${it.key.removePrefix("probe_total/")} · ${formatAnalysisElapsedTime(it.value)}")
                    }
                    longestQueueWait?.let {
                        CapabilityKeyValue("Longest scheduler wait", "${it.key.removePrefix("probe_queue_wait/")} · ${formatAnalysisElapsedTime(it.value)}")
                    }
                    Text("Scheduler wait is shown separately from probe work when that evidence exists, so queue delay is not presented as native-call duration.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                }
            }
            analysisPager(model.diagnostics.size)
            items(analysisSlice(model.diagnostics), key = { "diag:${it.key}" }) { row ->
                CapabilityItemCard {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ExpressiveStatus(row.state)
                            Text(row.key.substringAfterLast('/').ifBlank { row.key }, modifier = Modifier.weight(1f), color = VulkanTextPrimary, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                        }
                        CapabilityKeyValue("Evidence", row.value)
                        Surface(shape = RoundedCornerShape(14.dp), color = VulkanSurfaceLow, contentColor = VulkanTextSecondary, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.26f))) {
                            Row(Modifier.fillMaxWidth().padding(horizontal = 11.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(painterResource(R.drawable.ic_evidence), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.size(18.dp))
                                Text(row.timing, color = VulkanTextSecondary, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }
        4 -> {
            val satisfiedCount = model.requirementEvaluations.count { it.state == "SATISFIED" }
            val notSatisfiedCount = model.requirementEvaluations.count { it.state == "NOT SATISFIED" }
            val unknownCount = model.requirementEvaluations.count { it.state == "UNKNOWN" }
            val requestedToken = state.requirementInput.trim()
            item {
                CapabilitySectionCard("Capability requirement resolver") {
                    Text(
                        "Resolve one Vulkan registry token into its checked-in dependency references, then compare every referenced token with the runtime evidence collected from the selected device. Registry relationships and runtime support remain separate evidence classes.",
                        color = VulkanTextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    ExpressiveSearchField(
                        value = state.requirementInput,
                        onValueChange = { state.requirementInput = it.take(160) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholderText = "VK_EXT_descriptor_buffer"
                    )
                    ExpressiveMetricGrid(
                        listOf(
                            "SATISFIED" to satisfiedCount.toString(),
                            "NOT SATISFIED" to notSatisfiedCount.toString(),
                            "UNKNOWN" to unknownCount.toString(),
                            "CHECKED" to model.requirementEvaluations.size.toString()
                        )
                    )
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        color = VulkanSurfaceLow,
                        border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.32f)),
                        modifier = Modifier.fillMaxWidth().animateContentSize(tween(220, easing = FastOutSlowInEasing))
                    ) {
                        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                                Icon(painterResource(R.drawable.ic_registry), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.size(20.dp))
                                Text("Registry relationship", color = VulkanTextPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            }
                            if (requestedToken.isBlank()) {
                                Text("Enter a VK_* token to inspect its local registry reference and runtime evidence.", color = VulkanTextMuted, style = MaterialTheme.typography.bodySmall)
                            } else {
                                CapabilityKeyValue("Requested token", requestedToken)
                                model.requirementReference?.let { ref ->
                                    CapabilityKeyValue("Depends", ref.depends.ifBlank { "No dependency expression in checked-in reference" })
                                    CapabilityKeyValue("Requires", ref.requires.ifBlank { "No requires expression in checked-in reference" })
                                    CapabilityKeyValue("Promoted to", ref.promotedTo.ifBlank { "Not promoted in checked-in reference" })
                                } ?: Text(
                                    "No checked-in extension relationship was resolved for this input. The requested token can still be compared with available runtime evidence without inventing registry metadata.",
                                    color = VulkanTextMuted,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                    Text(
                        "SATISFIED means the requested runtime evidence is explicitly present. NOT SATISFIED requires explicit contrary evidence. UNKNOWN means the current report cannot safely decide the token.",
                        color = VulkanTextMuted,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            if (model.requirementEvaluations.isNotEmpty()) {
                item {
                    CapabilitySectionCard("Requirement evaluation summary") {
                        Text(
                            "Each row below is evaluated independently. A registry dependency expression is never collapsed into an inferred global support result.",
                            color = VulkanTextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                        CapabilityKeyValue("Evaluation scope", "${model.requirementEvaluations.size} unique token${if (model.requirementEvaluations.size == 1) "" else "s"}")
                    }
                }
            }
            analysisPager(model.requirementEvaluations.size)
            items(analysisSlice(model.requirementEvaluations), key = { "req:${it.token}" }) { result ->
                CapabilityItemCard {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(shape = RoundedCornerShape(14.dp), color = VulkanAccentContainer, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.32f))) {
                                Icon(painterResource(R.drawable.ic_registry), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.padding(9.dp).size(20.dp))
                            }
                            Text(result.token, modifier = Modifier.weight(1f), color = VulkanTextPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            ExpressiveStatus(result.state)
                        }
                        CapabilityKeyValue("Runtime evidence", result.evidence)
                        Text(
                            when (result.state) {
                                "SATISFIED" -> "Explicit report evidence satisfies this token."
                                "NOT SATISFIED" -> "Explicit report evidence contradicts this token."
                                else -> "The available report evidence is insufficient for a safe yes/no decision."
                            },
                            color = VulkanTextMuted,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
            if (requestedToken.isNotBlank() && model.requirementEvaluations.isEmpty()) item { EmptyState("No bounded requirement evidence could be produced for the requested token") }
        }
        5 -> {
            val profilePassCount = model.profileResults.count { it.status == "PASS" }
            val profileFailCount = model.profileResults.count { it.status == "FAIL" }
            val profileUnknownCount = model.profileResults.count { it.status == "UNKNOWN" }
            val customPassCount = model.customMinimumEvaluations.count { it.state == "PASS" }
            val customFailCount = model.customMinimumEvaluations.count { it.state == "FAIL" }
            val customUnknownCount = model.customMinimumEvaluations.count { it.state == "UNKNOWN" }
            item {
                CapabilitySectionCard("Saved minimums · Custom profile files") {
                    CapabilityKeyValue("Locally saved minimum profiles", state.savedProfiles.size.toString())
                    SharedStoragePermissionActionButton("Import custom profile", "JSON · 256 KiB bound", R.drawable.ic_action_import) { model.importMinimumProfile() }
                    SharedStoragePermissionActionButton("Export custom profile", "Choose a shared-storage folder and JSON file name", R.drawable.ic_export) { model.exportMinimumProfile() }
                    Text("Saved minimum profiles are listed immediately below; import and export remain explicit local actions.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                }
            }
            val topSavedMinimumProfiles = state.savedProfiles.toSortedMap().entries.toList()
            if (topSavedMinimumProfiles.isNotEmpty()) {
                item { CapabilitySectionCard("Saved minimum profiles") {
                    ExpressiveMetricGrid(listOf("SAVED" to topSavedMinimumProfiles.size.toString()))
                    topSavedMinimumProfiles.take(10).forEach { entry ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(entry.key, modifier = Modifier.weight(1f), color = VulkanTextPrimary, style = MaterialTheme.typography.bodyMedium)
                            ExpressiveContainedIconTextButton("Load", R.drawable.ic_action_update) {
                                state.pendingMinimumConfirmation = MinimumProfileConfirmation(MinimumProfileConfirmationKind.LOAD, entry.key)
                            }
                        }
                    }
                    if (topSavedMinimumProfiles.size > 10) Text("Additional saved profiles appear in the searchable list below.", color = VulkanTextMuted)
                } }
            }
            item {
                CapabilitySectionCard("Vulkan Profiles and custom minimums") {
                    Text(
                        "Inspect bundled Vulkan Profile evaluations and build a separate local minimum contract. Official profile evidence, custom rules and raw runtime evidence remain distinct and never overwrite one another.",
                        color = VulkanTextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    ExpressiveSearchField(
                        value = state.profileQuery,
                        onValueChange = { state.profileQuery = it; state.contentPage = 0 },
                        modifier = Modifier.fillMaxWidth(),
                        placeholderText = "Search bundled profiles…"
                    )
                    ExpressiveFilterBar(listOf("All", "FAIL", "UNKNOWN", "PASS"), listOf("All", "FAIL", "UNKNOWN", "PASS").indexOf(state.profileStatusFilter).coerceAtLeast(0)) {
                        state.profileStatusFilter = listOf("All", "FAIL", "UNKNOWN", "PASS")[it]
                        state.contentPage = 0
                    }
                    ExpressiveMetricGrid(
                        listOf(
                            "PASS" to profilePassCount.toString(),
                            "FAIL" to profileFailCount.toString(),
                            "UNKNOWN" to profileUnknownCount.toString(),
                            "VISIBLE" to model.profileResults.size.toString()
                        )
                    )
                    Text(
                        "PASS means every mapped requirement used by this bundled evaluation is met. FAIL requires at least one verified failed requirement. UNKNOWN preserves missing or unresolved evidence instead of guessing support.",
                        color = VulkanTextMuted,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            analysisPager(model.profileResults.size)
            items(analysisSlice(model.profileResults), key = { "profile:${it.name}" }) { result ->
                CapabilityItemCard {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(shape = RoundedCornerShape(14.dp), color = VulkanAccentContainer, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.32f))) {
                                Icon(painterResource(R.drawable.ic_profile), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.padding(9.dp).size(20.dp))
                            }
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(result.name, color = VulkanTextPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                if (result.revision.isNotBlank()) Text("Revision ${result.revision}", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                            }
                            ExpressiveStatus(result.status)
                        }
                        ExpressiveMetricGrid(
                            listOf(
                                "MAPPED" to result.checkedRequirementCount.toString(),
                                "MET" to result.metRequirementCount.toString(),
                                "FAILED" to result.failedRequirementCount.toString(),
                                "UNKNOWN" to result.unknownRequirementCount.toString()
                            )
                        )
                        CapabilityKeyValue("Profile summary", profileSummary(result))
                        result.minimumApiVersion.takeIf { it.isNotBlank() }?.let { CapabilityKeyValue("Minimum API", it) }
                        result.failingLimits.takeIf { it.isNotEmpty() }?.let { CapabilityKeyValue("Verified property / limit failures", it.joinToString("; ")) }
                        result.unknownLimits.takeIf { it.isNotEmpty() }?.let { CapabilityKeyValue("Unavailable properties / limits", it.joinToString("; ")) }
                        result.failingRequirementGroups.takeIf { it.isNotEmpty() }?.let { CapabilityKeyValue("Failed inherited / OR requirements", it.joinToString("; ")) }
                        result.unknownRequirementGroups.takeIf { it.isNotEmpty() }?.let { CapabilityKeyValue("Unresolved inherited / OR requirements", it.joinToString("; ")) }
                        result.coverageNote.takeIf { it.isNotBlank() }?.let { Text(it, color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall) }
                    }
                }
            }
            item {
                CapabilitySectionCard("Custom minimum builder") {
                    Text(
                        "Create up to $ANALYSIS_CUSTOM_PROFILE_MAX_RULES local rules. These rules are a user-defined acceptance checklist for the current report; they do not modify Vulkan capabilities or bundled Profile results.",
                        color = VulkanTextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Surface(shape = MaterialTheme.shapes.large, color = VulkanSurfaceLow, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.26f)), modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Profile identity", color = VulkanAccentSoft, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            OutlinedTextField(value = state.customProfileName, onValueChange = { state.customProfileName = it.take(128) }, modifier = Modifier.fillMaxWidth(), label = { Text("Profile name") }, singleLine = true)
                        }
                    }
                    Surface(shape = MaterialTheme.shapes.large, color = VulkanSurfaceLow, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.26f)), modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Rule contract", color = VulkanAccentSoft, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                            OutlinedTextField(value = state.customProfileRules, onValueChange = { if (it.length <= 16_384) state.customProfileRules = it }, modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp), label = { Text("One rule per line") }, supportingText = { Text("api>=1.3 · extension:VK_KHR_dynamic_rendering · feature:name=true · limit:name>=number · evidence:path=value") })
                            CapabilityKeyValue("Parsed rule count", state.customProfileRules.lineSequence().count { it.isNotBlank() }.coerceAtMost(ANALYSIS_CUSTOM_PROFILE_MAX_RULES).toString())
                            CapabilityKeyValue("Local status", state.customProfileStatus)
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ExpressiveContainedIconTextButton("Save", R.drawable.ic_save, modifier = Modifier.weight(1f)) {
                            state.pendingMinimumConfirmation = MinimumProfileConfirmation(MinimumProfileConfirmationKind.SAVE, state.customProfileName.trim())
                        }
                    }
                }
            }
            val savedProfileRows = state.savedProfiles.toSortedMap().entries.toList()
            val savedProfilePageCount = maxOf(1, (savedProfileRows.size + COLLECTION_PAGE_SIZE - 1) / COLLECTION_PAGE_SIZE)
            val savedProfilePage = state.savedProfilesPage.coerceIn(0, savedProfilePageCount - 1)
            val pagedSavedProfiles = savedProfileRows.drop(savedProfilePage * COLLECTION_PAGE_SIZE).take(COLLECTION_PAGE_SIZE)
            stickyCollectionPager(
                totalItems = savedProfileRows.size,
                currentPage = savedProfilePage,
                onPageChange = { state.savedProfilesPage = it },
                pagerKey = "analysis-saved-profiles"
            )
            items(pagedSavedProfiles, key = { "saved:${it.key}" }) { entry ->
                CapabilityItemCard {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(shape = RoundedCornerShape(14.dp), color = VulkanAccentContainer, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.32f))) {
                                Icon(painterResource(R.drawable.ic_save), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.padding(9.dp).size(20.dp))
                            }
                            Column(Modifier.weight(1f)) {
                                Text(entry.key, color = VulkanTextPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Text("${entry.value.lineSequence().count { it.isNotBlank() }} rule(s)", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ExpressiveContainedIconTextButton("Load", R.drawable.ic_action_update, modifier = Modifier.weight(1f)) {
                                state.pendingMinimumConfirmation = MinimumProfileConfirmation(MinimumProfileConfirmationKind.LOAD, entry.key)
                            }
                            ExpressiveContainedIconTextButton("Delete", R.drawable.ic_delete, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold) {
                                state.pendingMinimumConfirmation = MinimumProfileConfirmation(MinimumProfileConfirmationKind.DELETE, entry.key)
                            }
                        }
                    }
                }
            }
            item {
                CapabilitySectionCard("Current custom evaluation") {
                    ExpressiveMetricGrid(
                        listOf(
                            "PASS" to customPassCount.toString(),
                            "FAIL" to customFailCount.toString(),
                            "UNKNOWN" to customUnknownCount.toString(),
                            "TOTAL" to model.customMinimumEvaluations.size.toString()
                        )
                    )
                    Text("Each local rule is evaluated independently against the current evidence snapshot. UNKNOWN is preserved whenever the requested evidence cannot be safely resolved.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                }
            }
            analysisPager(model.customMinimumEvaluations.size, "analysis-custom-minimums")
            items(analysisSlice(model.customMinimumEvaluations), key = { "minimum:${it.rule}" }) { evaluation ->
                CapabilityItemCard {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            ExpressiveStatus(evaluation.state)
                            Text(evaluation.rule, modifier = Modifier.weight(1f), color = VulkanTextPrimary, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }
                        CapabilityKeyValue("Observed evidence", evaluation.evidence)
                    }
                }
            }
        }
        6 -> {
            item { CapabilitySectionCard("Dependency graph explorer") {
                Text("Explore the checked-in Vulkan registry relationship graph without mixing registry references with runtime support evidence. Traversal stays bounded and cycle-safe.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                ExpressiveSearchField(value = state.graphInput, onValueChange = { state.graphInput = it.trim().take(160) }, modifier = Modifier.fillMaxWidth(), placeholderText = "VK_KHR_swapchain")
                Text("Traversal depth", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                ExpressiveFilterBar((1..4).map { "Depth $it" }, state.graphDepth - 1) { state.graphDepth = it + 1 }
                CapabilityKeyValue("Root", model.graphRootToken.ifBlank { "Enter a Vulkan® extension token" })
                model.graphRootRef?.let { ref ->
                    CapabilityKeyValue("Registry depends", ref.depends.ifBlank { "No dependency expression in the checked-in reference" })
                    CapabilityKeyValue("Registry requires", ref.requires.ifBlank { "No additional requires expression" })
                    CapabilityKeyValue("Promoted to", ref.promotedTo.ifBlank { "Not promoted in the checked-in reference" })
                }
                if (state.graphInput.isNotBlank() && model.graphRootRef == null) {
                    Text("No checked-in extension relationship record matched this token. Try an exact VK_* extension name.", color = ComposeColor(0xFFFFC857), style = MaterialTheme.typography.bodySmall)
                }
            } }
            if (model.graphRootRef != null) {
                item {
                    val runtimePresent = model.visualGraphNodes.count { it.evidence.startsWith("Enumerated") || it.evidence.startsWith("Runtime API satisfies") }
                    val runtimeAbsent = model.visualGraphNodes.count { it.evidence.startsWith("Not enumerated") || it.evidence.startsWith("Runtime API does not expose") }
                    val runtimeUnknown = (model.visualGraphNodes.size - runtimePresent - runtimeAbsent).coerceAtLeast(0)
                    CapabilitySectionCard("Graph overview") {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(Modifier.weight(1f)) { ExpressiveMetric("Nodes", model.graphEntries.size.toString()) }
                            Box(Modifier.weight(1f)) { ExpressiveMetric("Direct", model.graphEntries.count { it.depth == 1 }.toString()) }
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(Modifier.weight(1f)) { ExpressiveMetric("Present", runtimePresent.toString()) }
                            Box(Modifier.weight(1f)) { ExpressiveMetric("Unknown", runtimeUnknown.toString()) }
                        }
                        if (runtimeAbsent > 0) CapabilityKeyValue("Not exposed in completed runtime evidence", runtimeAbsent.toString())
                        Text("Node color/status summarizes only the selected device's current enumeration/API evidence. It is not a conformance or performance result.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                    }
                }
                item { CapabilitySectionCard("Interactive dependency map") { VulkanDependencyGraph(model.visualGraphNodes, Modifier.fillMaxWidth()); CapabilityKeyValue("Traversal bound", "${model.graphEntries.size} registry-reference node(s)") } }
                analysisPager(model.graphEntries.size)
                items(analysisSlice(model.graphEntries), key = { "dep:${it.depth}:${it.token}" }) { entry -> CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    val runtimeEvidence = dependencyRuntimeEvidence(entry.token, report, device)
                    CapabilityKeyValue(entry.token, runtimeEvidence)
                    CapabilityKeyValue("Depth", entry.depth.toString())
                    entry.parent?.let { CapabilityKeyValue("Registry edge", "$it → ${entry.token}") }
                    if (entry.depth == 0) Text("Traversal root", color = VulkanAccentSoft, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                } } }
            }
        }
        7 -> {
            item { CapabilitySectionCard("Surface + Display presentation evidence") {
                Text("These are compatible evidence paths, not end-to-end presentation guarantees. Vulkan® Surface color spaces and Android physical-display HDR/wide-gamut evidence remain separate sources.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
            } }
            analysisPager(model.presentationPaths.size)
            items(analysisSlice(model.presentationPaths), key = { it.title }) { path -> CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                ExpressiveStatus(path.state)
                Text(path.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                CapabilityKeyValue("Vulkan® Surface", path.surfaceEvidence)
                CapabilityKeyValue("Android Display", path.displayEvidence)
                Text(path.note, color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
            } } }
        }
        8 -> {
            item { CapabilitySectionCard("Raw structured technical Report") {
                Text("Read-only tree leaves from the exact local schema-v3 technicalReport object used by Database submission. Values are searchable. Export the canonical JSON together with TXT and HTML under Database & Reports.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                ExpressiveSearchField(value = state.rawQuery, onValueChange = { state.rawQuery = it; state.contentPage = 0 }, modifier = Modifier.fillMaxWidth(), placeholderText = "Search JSON path or value…")
                CapabilityKeyValue("Visible leaf bound", "$ANALYSIS_RAW_JSON_VISIBLE_LIMIT")
            } }
            analysisPager(model.rawLeaves.size)
            items(analysisSlice(model.rawLeaves), key = { "raw:${it.path}" }) { leaf -> CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                CapabilityKeyValue(leaf.path, leaf.value)
                Text(leaf.type, color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                ExpressiveContainedIconTextButton("Evidence actions", R.drawable.ic_evidence) { state.selectedEvidence = leaf.path to leaf.value }
            } } }
        }
        9 -> {
            item {
                CapabilitySectionCard("VulkanScope Database lookup") {
                    Text("Choose a bounded public Database list or enter an exact report ID. Both paths are explicit user-initiated reads from the fixed official HTTPS endpoint and the selected technicalReport is compared locally.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                    ExpressiveFilterBar(
                        listOf("Database list", "Report ID"),
                        state.databaseLookupMode.coerceIn(0, 1)
                    ) {
                        state.databaseLookupMode = it
                        state.contentPage = 0
                    }
                    if (state.databaseLookupMode == 0) {
                        ExpressiveSearchField(
                            value = state.databaseListQuery,
                            onValueChange = { state.databaseListQuery = it.take(120); state.contentPage = 0 },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = networkAvailable,
                            placeholderText = "Filter loaded GPU, device, driver or report ID…"
                        )
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ExpressiveContainedIconTextButton(
                                if (state.databaseReports.isEmpty()) "Load reports" else "Refresh",
                                R.drawable.ic_database_fetch,
                                modifier = Modifier.weight(1f),
                                enabled = networkAvailable && !state.databaseListLoading
                            ) { model.fetchDatabaseReportList(true) }
                            ExpressiveContainedIconTextButton(
                                "Load more",
                                R.drawable.ic_download,
                                modifier = Modifier.weight(1f),
                                enabled = networkAvailable && !state.databaseListLoading && state.databaseListCursor != null
                            ) { model.fetchDatabaseReportList(false) }
                        }
                        CapabilityKeyValue("List status", state.databaseListStatus)
                        Text("Each request is capped at 50 rows and the in-app list is bounded to 200 unique reports.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                    } else {
                        ExpressiveSearchField(
                            value = state.databaseReportId,
                            onValueChange = { state.databaseReportId = it.lowercase().filter { ch -> ch in '0'..'9' || ch in 'a'..'f' }.take(64) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = networkAvailable,
                            placeholderText = "64-character report id"
                        )
                        ExpressiveActionButton(
                            "Fetch by report ID",
                            if (networkAvailable) "Load the exact public technicalReport for local comparison" else "Unavailable without a validated internet connection",
                            R.drawable.ic_database_fetch,
                            enabled = networkAvailable && !state.databaseLoading && state.databaseReportId.length == 64
                        ) { model.fetchDatabaseReport(state.databaseReportId) }
                        CapabilityKeyValue("Status", state.databaseStatus)
                    }
                    if (!networkAvailable) Text("Database lookup is locked until Android reports a validated internet connection.", color = ComposeColor(0xFFFFC857), style = MaterialTheme.typography.bodySmall)
                    if (state.databaseListLoading || state.databaseLoading) LoadingIndicator(color = VulkanAccentSoft, modifier = Modifier.size(32.dp))
                    if (state.databaseRemoteLeaves.isNotEmpty()) ExpressiveToggleRow("Show unchanged", "Include identical structured JSON leaves.", state.databaseIncludeUnchanged) { state.databaseIncludeUnchanged = it }
                }
            }
            if (state.databaseLookupMode == 0) {
                val query = state.databaseListQuery.trim()
                val visibleReports = state.databaseReports.filter { report ->
                    query.isBlank() || listOf(report.id, report.gpuName, report.manufacturer, report.model, report.driverMode, report.driverVersion, report.deviceApiVersion, report.vendorId, report.deviceId)
                        .any { it.contains(query, true) }
                }
                analysisPager(visibleReports.size)
                items(analysisSlice(visibleReports), key = { "db-list:${it.id}" }) { reportRow ->
                    CapabilityItemCard {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                SystemDriverVendorBadge(reportRow.vendorId)
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(reportRow.gpuName.ifBlank { "Unknown GPU" }, color = VulkanTextPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                    Text("${reportRow.manufacturer} ${reportRow.model}".trim(), color = VulkanTextSecondary, style = MaterialTheme.typography.labelMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                }
                            }
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                ExpressiveInfoPill("API", reportRow.deviceApiVersion)
                                DatabaseDriverModePill(reportRow.driverMode)
                                ExpressiveInfoPill("Version", reportRow.driverVersion)
                            }
                            DatabaseSubmittedAt(reportRow.submittedAt)
                            CapabilityKeyValue("Report ID", reportRow.id)
                            ExpressiveContainedIconTextButton(
                                "Compare this report",
                                R.drawable.ic_compare,
                                enabled = networkAvailable && !state.databaseLoading
                            ) {
                                state.databaseLookupMode = 1
                                state.databaseReportId = reportRow.id
                                model.fetchDatabaseReport(reportRow.id)
                            }
                        }
                    }
                }
            }
            if (state.databaseRemoteLeaves.isNotEmpty()) {
                item {
                    CapabilitySectionCard("Database comparison summary") {
                        ExpressiveMetric("Differences", model.databaseDiff.count { it.state != "Unchanged" }.toString())
                        ExpressiveMetric("Added locally", model.databaseDiff.count { it.state == "Added" }.toString())
                        ExpressiveMetric("Missing locally", model.databaseDiff.count { it.state == "Removed" }.toString())
                        Text("A difference is a report-evidence difference only; it is not a device ranking or market-share statement.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                    }
                }
                val boundedDatabaseDiff = model.databaseDiff.take(ANALYSIS_GLOBAL_SEARCH_VISIBLE_LIMIT)
                analysisPager(boundedDatabaseDiff.size)
                items(analysisSlice(boundedDatabaseDiff), key = { "db:${it.key}" }) { row ->
                    CapabilityItemCard {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            CapabilityKeyValue(row.key, row.state)
                            row.baseline?.let { CapabilityKeyValue("Database", it) }
                            row.current?.let { CapabilityKeyValue("Current", it) }
                        }
                    }
                }
            }
        }
        10 -> {
            item { CapabilitySectionCard("Local session history") {
                CapabilityKeyValue("Retention", "${state.history.size} / $ANALYSIS_HISTORY_MAX_ITEMS sessions")
                CapabilityKeyValue("Status", state.historyStatus)
                Text("Completed analysis snapshots are compressed into private app storage with strict count/size bounds. No history is uploaded automatically.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExpressiveContainedIconTextButton("Clear all", R.drawable.ic_clear_all, modifier = Modifier.weight(1f), enabled = state.history.isNotEmpty()) { state.pendingHistoryDeleteAll = true }
                }
            } }
            analysisPager(state.history.size)
            items(analysisSlice(state.history), key = { "history:${it.id}" }) { record -> CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                CapabilityKeyValue(historyLabel(record), record.driverMode)
                CapabilityKeyValue("GPU", record.deviceName)
                CapabilityKeyValue("App version", record.applicationVersion)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExpressiveContainedIconTextButton("Use as baseline", R.drawable.ic_baseline, modifier = Modifier.weight(1f)) { model.useHistoryAsBaseline(record) }
                    ExpressiveContainedIconTextButton("Delete", R.drawable.ic_delete, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold) { state.pendingHistoryDelete = record }
                }
            } } }
            if (state.history.isEmpty()) item { EmptyState("No completed local analysis session retained yet") }
        }
        11 -> {
            item { CapabilitySectionCard("Watched evidence") {
                ExpressiveSearchField(value = state.watchInput, onValueChange = { state.watchInput = it }, modifier = Modifier.fillMaxWidth(), placeholderText = "Extension, feature, limit, format or evidence token…")
                CapabilityKeyValue("Watch-list usage", "${state.watched.size} / $ANALYSIS_MAX_WATCHED")
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExpressiveContainedIconTextButton("Add to watch list", R.drawable.ic_watch_add, modifier = Modifier.weight(1f), enabled = model.canAddWatch) { model.addWatched(state.watchInput); state.watchInput = "" }
                    ExpressiveContainedIconTextButton("Clear all", R.drawable.ic_clear_all, modifier = Modifier.weight(1f), enabled = state.watched.isNotEmpty()) { state.pendingWatchDeleteAll = true }
                }
                ExpressiveSearchField(value = state.watchQuery, onValueChange = { state.watchQuery = it; state.contentPage = 0 }, modifier = Modifier.fillMaxWidth(), placeholderText = "Filter watched entries…")
                ExpressiveFilterBar(listOf("All", "Matched", "Missing"), listOf("All", "Matched", "Missing").indexOf(state.watchStateFilter).coerceAtLeast(0)) { state.watchStateFilter = listOf("All", "Matched", "Missing")[it] }
            } }
            analysisPager(model.visibleWatched.size)
            items(analysisSlice(model.visibleWatched), key = { "watch:$it" }) { token ->
                val evidence = model.watchedEvidence[token] ?: (0 to emptyList())
                CapabilityItemCard { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    CapabilityKeyValue(token, if (evidence.first == 0) "Not present in current snapshot" else "${evidence.first} matching evidence item(s)")
                    evidence.second.forEach { CapabilityKeyValue(it.first, it.second) }
                    ExpressiveContainedIconTextButton("Remove", R.drawable.ic_delete) { state.pendingWatchDelete = token }
                } }
            }
        }
        12 -> {
            val assessed = model.capabilityScore.checks.filter { it.applicable }
            val passed = assessed.count { !it.triggered }
            val failed = assessed.size - passed
            item {
                CapabilitySectionCard("Capability evidence score") {
                    Surface(shape = MaterialTheme.shapes.extraLarge, color = VulkanAccentContainer, contentColor = VulkanTextPrimary, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.44f))) {
                        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Weighted device evidence score", color = VulkanAccentSoft, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                Text(model.capabilityScore.score?.let { "$it%" } ?: "Unavailable", color = VulkanTextPrimary, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                                Text(model.capabilityScore.level, color = VulkanTextSecondary, style = MaterialTheme.typography.bodyMedium)
                            }
                            Surface(shape = CircleShape, color = ComposeColor(0xFF31181B), modifier = Modifier.size(54.dp)) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(painterResource(R.drawable.ic_analysis), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.size(26.dp))
                                }
                            }
                        }
                    }
                    ExpressiveMetricGrid(listOf("VULKAN API" to (device?.apiVersion ?: "Unknown"), "EXTENSIONS" to (device?.extensions?.map { it.name }?.distinct()?.size?.toString() ?: "Unknown"), "EVIDENCE" to "${model.capabilityScore.evidenceCoverage} / 100"))
                    Text("Five continuous, weighted components: Vulkan API version (30%), extension breadth (25%), supported feature bits (20%), collection integrity (20%) and surface presentation (5%). Missing components are excluded; low evidence coverage suppresses the score.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                }
            }
            item { CapabilitySectionCard("Score breakdown") {
                model.capabilityScore.components.forEach { component ->
                    val localPoints = component.fraction?.let { (100.0 * it).toInt().coerceIn(0, 100) }
                    CapabilityItemCard {
                        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(component.title, modifier = Modifier.weight(1f), color = VulkanTextPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Text(localPoints?.let { "$it% · ${component.weight}% weight" } ?: "N/A · ${component.weight}% weight", color = VulkanAccentSoft, style = MaterialTheme.typography.labelMedium)
                            }
                            Text(component.evidence, color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                            Text(component.methodology, color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            } }
            item { CapabilitySectionCard("Collection safety checks") {
                ExpressiveMetricGrid(listOf("PASSED" to passed.toString(), "FAILED" to failed.toString(), "ASSESSED" to assessed.size.toString()))
            } }
            analysisPager(model.capabilityScore.checks.size)
            items(analysisSlice(model.capabilityScore.checks), key = { "quality:${it.title}" }) { check ->
                CapabilityItemCard {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            ExpressiveStatus(if (!check.applicable) "NOT APPLICABLE" else if (check.triggered) "FAILED" else "PASSED")
                            Text(check.title, modifier = Modifier.weight(1f), color = VulkanTextPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        }
                        CapabilityKeyValue("Observed evidence", check.evidence)
                        if (check.applicable) CapabilityKeyValue("Score weight", check.weight.toString())
                    }
                }
            }
            if (model.capabilityScore.checks.isEmpty()) item { EmptyState("A physical-device report is needed for capability evidence scoring") }
            item {
                CapabilitySectionCard("Interpretation and limits") {
                    Text("This is an app-defined local capability/evidence index, not a benchmark or Khronos CTS result. Different hardware and extension ecosystems are not directly comparable by extension count alone. Missing and inapplicable components are never treated as failures, and no performance superiority is inferred.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        13 -> {
            item { CapabilitySectionCard("Database permalink & QR") {
                CapabilityKeyValue("Target", if (model.lastSharedReportId.isBlank()) "VulkanScope Database" else "Last submitted report · ${model.lastSharedReportId.take(12)}…")
                CapabilityKeyValue("Permalink", model.sharedReportUrl)
                ExpressiveActionButton("Share link", "Android Sharesheet · no background upload", R.drawable.ic_share, trailingIcon = R.drawable.ic_open_external) { model.shareLink() }
                TransientActionButton("Copy link", "Copy permalink to clipboard", R.drawable.ic_copy) { runCatching { model.copyLink() }.isSuccess }
            } }
            item {
                Surface(color = VulkanSurfaceRaised, shape = MaterialTheme.shapes.extraLarge, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Scan to open the current VulkanScope Database permalink", color = VulkanTextPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                        Text("The QR payload is exactly the permalink shown above. The quiet zone and high-contrast code area are kept unobstructed for reliable scanning.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                        Surface(color = ComposeColor.White, shape = RoundedCornerShape(20.dp), modifier = Modifier.padding(4.dp)) {
                            VulkanQrCode(model.sharedReportUrl, Modifier.padding(14.dp).size(220.dp))
                        }
                    }
                }
            }
        }
        else -> {
            item {
                CapabilitySectionCard("Vulkan active self-tests") {
                    Text("These tests run only when you press Run. They execute in the isolated Vulkan probe path against the selected physical device and remain separate from capability collection.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                    CapabilityKeyValue("Target GPU", device?.let { "${it.name} · ${it.vendorId}:${it.deviceId}" } ?: "Unavailable")
                    CapabilityKeyValue("Execution scope", "Selected physical device · bounded graphics/compute queue-family selection")
                    ExpressiveMetricGrid(
                        listOf(
                            "Device" to "VkDevice",
                            "Shader" to "SPIR-V™",
                            "Layout" to "Pipeline layout",
                            "Pipeline" to "Compute"
                        )
                    )
                    Text("PASS means the specific create/destroy path completed successfully. FAIL means the attempted Vulkan operation returned a failure or did not produce the required handle. UNAVAILABLE means the test could not be safely attempted because a required entry point, prerequisite or target identity was unavailable.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
                    ExpressiveActionButton("Run Vulkan self-tests", "Create a minimal VkDevice, shader module, pipeline layout and compute pipeline for the selected GPU", R.drawable.ic_self_test, enabled = !state.testRunning && model.selfTestsAvailable) { model.runSelfTests() }
                    if (state.testRunning) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            LoadingIndicator(color = VulkanAccentSoft, modifier = Modifier.size(32.dp))
                            Text("Self-tests are running in the isolated probe…", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            state.testResult?.let { result ->
                val tests = result.optJSONArray("tests")
                val testRows = if (tests == null) emptyList() else (0 until tests.length()).mapNotNull { tests.optJSONObject(it) }
                val passCount = testRows.count { it.optString("status").equals("PASS", true) }
                val failCount = testRows.count { it.optString("status").equals("FAIL", true) }
                val unavailableCount = testRows.count { it.optString("status").equals("UNAVAILABLE", true) }
                val overallRaw = result.optString("status", "unknown")
                val overallLabel = when (overallRaw) {
                    "completed" -> "Completed"
                    "completed_with_failures" -> "Completed with failures"
                    "completed_with_unavailable" -> "Completed with unavailable checks"
                    "unavailable" -> "Unavailable"
                    else -> overallRaw.replace('_', ' ').replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                }
                item {
                    CapabilitySectionCard("Test result summary") {
                        Surface(shape = MaterialTheme.shapes.extraLarge, color = VulkanSurfaceTonal, contentColor = VulkanTextPrimary, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.34f))) {
                            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                ExpressiveStatus(when { failCount > 0 -> "FAIL"; unavailableCount > 0 || overallRaw == "unavailable" -> "UNAVAILABLE"; overallRaw == "completed" -> "PASS"; else -> "UNKNOWN" })
                                Text(overallLabel, color = VulkanTextPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text("${testRows.size} reported check${if (testRows.size == 1) "" else "s"}", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        ExpressiveMetricGrid(
                            listOf(
                                "PASS" to passCount.toString(),
                                "FAIL" to failCount.toString(),
                                "UNAVAILABLE" to unavailableCount.toString(),
                                "TOTAL" to testRows.size.toString()
                            )
                        )
                        result.optString("reason").takeIf { it.isNotBlank() }?.let { CapabilityKeyValue("Why no full result was available", it) }
                    }
                }
                if (testRows.isNotEmpty()) {
                    itemsIndexed(testRows, key = { index, test -> "self-test:$index:${test.optString("name")}" }) { index, test ->
                        val status = test.optString("status", "UNKNOWN").uppercase()
                        CapabilityItemCard {
                            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Surface(shape = CircleShape, color = VulkanAccentContainer, contentColor = VulkanAccentSoft, modifier = Modifier.size(34.dp)) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text((index + 1).toString(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge) }
                                    }
                                    Text(trademarkSpirvDisplayText(test.optString("name", "Test")), modifier = Modifier.weight(1f), color = VulkanTextPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                    ExpressiveStatus(status)
                                }
                                test.optString("detail").takeIf { it.isNotBlank() }?.let { CapabilityKeyValue("Vulkan result", it) }
                                Text(
                                    when (status) {
                                        "PASS" -> "The requested Vulkan object path completed and produced the expected handle."
                                        "FAIL" -> "The operation was attempted but did not complete successfully. Capability evidence is not rewritten by this result."
                                        "UNAVAILABLE" -> "The check was not safely runnable because its required path or prerequisite was unavailable."
                                        else -> "The probe returned an unrecognized test state; capability evidence remains unchanged."
                                    },
                                    color = VulkanTextMuted,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                } else {
                    item { EmptyState(result.optString("reason").takeIf { it.isNotBlank() } ?: "No individual self-test result was returned") }
                }
                item {
                    CapabilitySectionCard("Result semantics") {
                        Text("Self-tests exercise a separate minimal Vulkan execution path. They do not override capability enumeration, prove conformance, or alter the descriptive Score tab.", color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
internal fun ChevronAffordance(label: String, contentDescription: String, onClick: () -> Unit) {
    Surface(
        color = VulkanAccentContainer,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.semantics { role = Role.Button }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(start = 12.dp, end = 2.dp, top = 2.dp, bottom = 2.dp)
        ) {
            Text(trademarkVulkanDisplayText(label), color = VulkanAccentSoft, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
            IconButton(onClick = onClick, modifier = Modifier.size(44.dp)) {
                Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = trademarkVulkanDisplayText(contentDescription), tint = VulkanAccentSoft, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun DetailAffordance(onClick: () -> Unit) = ChevronAffordance("Details", "Open details", onClick)

@Composable
internal fun ScrollableDetailDialog(title: String, onDismiss: () -> Unit, content: @Composable () -> Unit) = ExpressiveDetailDialog(title, onDismiss, content)

@Composable
internal fun ExpressiveDetailDialog(title: String, onDismiss: () -> Unit, content: @Composable () -> Unit) {
    val scrollState = rememberScrollState()
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val horizontalMargin = if (configuration.screenWidthDp < 360) 10.dp else 18.dp
    val verticalMargin = if (configuration.screenHeightDp < 520 || landscape) 8.dp else 16.dp
    val availableHeight = maxOf(280.dp, configuration.screenHeightDp.dp - verticalMargin * 2)
    val dialogHeight = if (landscape) availableHeight else minOf(720.dp, availableHeight)
    val dialogWidth = if (landscape) 980.dp else 640.dp
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize().padding(horizontal = horizontalMargin, vertical = verticalMargin), contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier.fillMaxWidth().widthIn(max = dialogWidth).height(dialogHeight),
                shape = MaterialTheme.shapes.extraLarge,
                color = VulkanSurfaceRaised,
                border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.46f)),
                tonalElevation = 4.dp,
                shadowElevation = 8.dp
            ) {
                Column(Modifier.fillMaxSize()) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Surface(shape = RoundedCornerShape(18.dp), color = VulkanAccentContainer) {
                            Icon(painterResource(capabilitySectionIcon(title)), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.padding(10.dp).size(21.dp))
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = VulkanTextPrimary)
                            Text("Detailed Vulkan® evidence", style = MaterialTheme.typography.labelMedium, color = VulkanTextSecondary)
                        }
                    }
                    HorizontalDivider(color = VulkanOutlineVariant)
                    Box(Modifier.fillMaxWidth().weight(1f).padding(horizontal = 14.dp, vertical = 12.dp)) {
                        Column(
                            Modifier.fillMaxWidth().desktopVerticalPointerScroll(scrollState).dpadScrollableNavigation(scrollState).verticalScroll(scrollState).focusable().focusGroup()
                        ) {
                            AdaptiveDialogColumns {
                                CompositionLocalProvider(LocalDetailKeyValuePresentation provides true) { content() }
                            }
                        }
                        ExpressiveScrollHints(scrollState, Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 6.dp))
                    }
                    HorizontalDivider(color = VulkanOutlineVariant)
                    Row(
                        Modifier.fillMaxWidth().heightIn(min = 66.dp).padding(horizontal = 14.dp, vertical = 9.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ExpressiveContainedIconTextButton("Close", R.drawable.ic_close) { onDismiss() }
                    }
                }
            }
        }
    }
}

internal val FORMAT_USAGE_FILTERS = linkedMapOf(
    "Sampled" to listOf("SAMPLED_IMAGE"),
    "Storage" to listOf("STORAGE_IMAGE"),
    "Color attachment" to listOf("COLOR_ATTACHMENT"),
    "Depth/stencil" to listOf("DEPTH_STENCIL_ATTACHMENT"),
    "Linear filtering" to listOf("SAMPLED_IMAGE_FILTER_LINEAR"),
    "Blit source" to listOf("BLIT_SRC"),
    "Blit destination" to listOf("BLIT_DST"),
    "Transfer source" to listOf("TRANSFER_SRC"),
    "Transfer destination" to listOf("TRANSFER_DST"),
    "Video decode" to listOf("VIDEO_DECODE"),
    "Video encode" to listOf("VIDEO_ENCODE"),
    "YCbCr / chroma" to listOf("YCBCR", "CHROMA_SAMPLES")
)

internal fun formatMatchesUsageFilters(format: FormatEntry, selected: Set<String>): Boolean {
    if (selected.isEmpty()) return true
    val decoded = listOf(formatFeatureFlags(format.linear), formatFeatureFlags(format.optimal), formatFeatureFlags(format.buffer)).joinToString(" ")
    return selected.all { label -> FORMAT_USAGE_FILTERS[label].orEmpty().any { token -> decoded.contains(token, true) } }
}
