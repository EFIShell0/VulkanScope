@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.efishell.vulkanscope

import android.content.res.Configuration
import android.view.Surface
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.key
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext



@Composable
internal fun ProfilesPage(report: VulkanReport, device: DeviceReport?) {
    var query by remember { mutableStateOf("") }
    var page by rememberSaveable { mutableIntStateOf(0) }
    val results = remember(report, device) { vulkanProfileEvaluations(report, device) }
    val filtered = results.filter { it.name.contains(query, true) }
    val pageCount = maxOf(1, (filtered.size + COLLECTION_PAGE_SIZE - 1) / COLLECTION_PAGE_SIZE)
    LaunchedEffect(query) { page = 0 }
    LaunchedEffect(filtered.size) { page = page.coerceIn(0, pageCount - 1) }
    val pageStart = page * COLLECTION_PAGE_SIZE
    val paged = filtered.drop(pageStart).take(COLLECTION_PAGE_SIZE)
    val visibleMappedChecks = filtered.sumOf { it.checkedRequirementCount }
    val visibleMetChecks = filtered.sumOf { it.metRequirementCount }
    val visibleFailedChecks = filtered.sumOf { it.failedRequirementCount }
    val visibleUnknownChecks = filtered.sumOf { it.unknownRequirementCount }
    VulkanLazyPage(verticalSpacing = 8.dp) {
        item {
            CapabilitySectionCard("Profile explorer") {
                Text("Profile requirements are checked from struct-qualified runtime evidence and audited normalized official definitions. Missing evidence remains UNKNOWN; catalog-only definitions and coverage-limited mappings never become API-only PASS claims.", color = ComposeColor(0xFFB6ACAE), style = MaterialTheme.typography.bodySmall)
                ExpressiveSearchField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), placeholderText = "Search profiles…")
                ExpressiveMetricGrid(
                    listOf(
                        "Visible" to filtered.size.toString(),
                        "Pass" to filtered.count { it.status == "PASS" }.toString(),
                        "Fail" to filtered.count { it.status == "FAIL" }.toString(),
                        "Unknown" to filtered.count { it.status == "UNKNOWN" }.toString()
                    ),
                    Modifier.padding(top = 4.dp)
                )
                Text("Mapped requirement checks across visible profiles", color = VulkanTextSecondary, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
                ProfileCheckMetricGrid(
                    total = visibleMappedChecks,
                    met = visibleMetChecks,
                    failed = visibleFailedChecks,
                    unknown = visibleUnknownChecks
                )
                Text("Each profile separates mapped checks that are met, verified unmet, or unknown. Category rows show the same split for extensions, features, properties/limits, formats and inherited-profile checks without turning missing evidence into an unsupported claim.", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)
            }
        }
        stickyCollectionPager(totalItems = filtered.size, currentPage = page, onPageChange = { page = it })
        items(paged, key = { it.name }) { result ->
            CapabilityItemCard {
                Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(result.name, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                        CapabilityStatusBadge(result.status, when (result.status) { "PASS" -> true; "FAIL" -> false; else -> null })
                    }
                    val apiText = if (result.minimumApiVersion.isBlank()) result.revision else "${result.revision} · Vulkan® ${result.minimumApiVersion} minimum"
                    Text(apiText, color = ComposeColor(0xFF9E9E9E), style = MaterialTheme.typography.labelMedium)
                    Text(profileSummary(result), style = MaterialTheme.typography.bodySmall)
                    ProfileCheckBreakdown(result)
                    if (result.missingExtensions.isNotEmpty()) CapabilityKeyValue("Verified missing extensions", result.missingExtensions.joinToString(", "))
                    if (result.unknownExtensions.isNotEmpty()) CapabilityKeyValue("Unknown extension requirements", result.unknownExtensions.joinToString(", "))
                    if (result.missingFeatures.isNotEmpty()) CapabilityKeyValue("Unsupported features", result.missingFeatures.joinToString(", "))
                    if (result.unknownFeatures.isNotEmpty()) CapabilityKeyValue("Unavailable feature queries", result.unknownFeatures.joinToString(", "))
                    if (result.failingLimits.isNotEmpty()) CapabilityKeyValue("Failing properties / limits", result.failingLimits.joinToString("; "))
                    if (result.unknownLimits.isNotEmpty()) CapabilityKeyValue("Unavailable properties / limits", result.unknownLimits.joinToString(", "))
                    if (result.failingFormats.isNotEmpty()) CapabilityKeyValue("Failing formats", result.failingFormats.joinToString(", "))
                    if (result.unknownFormats.isNotEmpty()) CapabilityKeyValue("Unavailable formats", result.unknownFormats.joinToString(", "))
                    if (result.failingRequirementGroups.isNotEmpty()) CapabilityKeyValue("Failed inherited / OR requirements", result.failingRequirementGroups.joinToString("; "))
                    if (result.unknownRequirementGroups.isNotEmpty()) CapabilityKeyValue("Unresolved inherited / OR requirements", result.unknownRequirementGroups.joinToString("; "))
                    if (result.coverageNote.isNotBlank()) CapabilityKeyValue("Evaluator coverage", result.coverageNote)
                }
            }
        }
    }
}

internal data class LibraryVersionInfo(val name: String, val version: String, val detail: String, val licenseName: String, val licenseAsset: String)

internal val VULKANSCOPE_LIBRARY_VERSIONS = listOf(
    LibraryVersionInfo("AndroidX Core KTX", "1.19.0", "Android application support", "Apache License 2.0", "licenses/apache_2_0.md"),
    LibraryVersionInfo("AndroidX Activity Compose", "1.13.0", "Compose activity integration", "Apache License 2.0", "licenses/apache_2_0.md"),
    LibraryVersionInfo("Compose UI", "1.12.0", "Compose UI runtime", "Apache License 2.0", "licenses/apache_2_0.md"),
    LibraryVersionInfo("Compose Foundation", "1.12.0", "Compose foundation components", "Apache License 2.0", "licenses/apache_2_0.md"),
    LibraryVersionInfo("Compose Animation", "1.12.0", "Compose animation primitives", "Apache License 2.0", "licenses/apache_2_0.md"),
    LibraryVersionInfo("Material 3", "1.5.0-alpha27", "Material 3 and expressive components", "Apache License 2.0", "licenses/apache_2_0.md"),
    LibraryVersionInfo("Lifecycle Runtime Compose", "2.11.0", "Lifecycle-aware Compose state", "Apache License 2.0", "licenses/apache_2_0.md"),
    LibraryVersionInfo("OkHttp", "5.5.0", "Explicit network requests", "Apache License 2.0", "licenses/apache_2_0.md"),
    LibraryVersionInfo("ZXing Core", "3.5.4", "Local QR code generation", "Apache License 2.0", "licenses/apache_2_0.md"),
    LibraryVersionInfo("Vulkan® Headers", "1.4.366", "Pinned Vulkan-Headers v1.4.366 (73814d0)", "Apache-2.0 OR MIT", "licenses/vulkan_headers.md"),
    LibraryVersionInfo("libadrenotools", "8fae8ce254dfc1344527e05301e43f37dea2df80", "arm64-v8a driver-loading integration · pinned commit", "BSD 2-Clause License", "licenses/libadrenotools_bsd_2_clause.md")
)

@Composable
internal fun LibraryLicenseDialog(library: LibraryVersionInfo, onDismiss: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val horizontalMargin = if (configuration.screenWidthDp < 360) 10.dp else 18.dp
    val verticalMargin = if (configuration.screenHeightDp < 520) 8.dp else 16.dp
    val dialogMaxHeight = maxOf(280.dp, configuration.screenHeightDp.dp - verticalMargin * 2)
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    var markdown by remember(library.licenseAsset) { mutableStateOf<String?>(null) }
    LaunchedEffect(library.licenseAsset) {
        markdown = withContext(Dispatchers.IO) {
            runCatching { context.assets.open(library.licenseAsset).bufferedReader().use { it.readText() } }
                .getOrElse { "# License unavailable\n\nThe packaged license document could not be read." }
        }
    }
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize().padding(horizontal = horizontalMargin, vertical = verticalMargin), contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier.fillMaxWidth().widthIn(max = if (isLandscape) 960.dp else 620.dp).heightIn(max = dialogMaxHeight),
                shape = MaterialTheme.shapes.extraLarge,
                color = VulkanSurfaceRaised,
                border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.46f)),
                tonalElevation = 4.dp,
                shadowElevation = 8.dp
            ) {
                Column(Modifier.fillMaxWidth().heightIn(max = dialogMaxHeight)) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Surface(shape = RoundedCornerShape(18.dp), color = VulkanAccentContainer) {
                            Icon(painterResource(R.drawable.ic_action_text), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.padding(10.dp).size(21.dp))
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("${library.name} license", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = VulkanTextPrimary)
                            Text(library.licenseName, style = MaterialTheme.typography.labelMedium, color = VulkanTextSecondary)
                        }
                    }
                    HorizontalDivider(color = VulkanOutlineVariant)
                    if (isLandscape) {
                        Row(Modifier.fillMaxWidth().weight(1f).padding(horizontal = 14.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Surface(Modifier.weight(0.35f).fillMaxHeight(), shape = MaterialTheme.shapes.large, color = VulkanSurfaceTonal, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanAccentSoft.copy(alpha = 0.25f))) {
                                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Icon(painterResource(R.drawable.ic_action_text), contentDescription = null, tint = VulkanAccentSoft, modifier = Modifier.size(28.dp))
                                    Text(library.name, color = VulkanTextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                    Text(library.licenseName, color = VulkanTextSecondary, style = MaterialTheme.typography.bodyMedium)
                                    Text("Bundled license text", color = VulkanAccentSoft, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                            Surface(Modifier.weight(0.65f).fillMaxHeight(), shape = MaterialTheme.shapes.large, color = VulkanSurfaceLow, border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant)) {
                                val text = markdown
                                if (text == null) Box(Modifier.fillMaxSize().padding(18.dp), contentAlignment = Alignment.Center) {
                                    Text("Loading license…", color = VulkanTextPrimary)
                                } else ReleaseNotesContent(text, Modifier.fillMaxSize())
                            }
                        }
                    } else {
                    Surface(
                        modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 14.dp, vertical = 12.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = ComposeColor(0xFF0D0D0D),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant)
                    ) {
                        val text = markdown
                        if (text == null) {
                            Box(Modifier.fillMaxSize().padding(18.dp), contentAlignment = Alignment.Center) {
                                Text("Loading license…", color = VulkanTextSecondary, style = MaterialTheme.typography.bodyMedium)
                            }
                        } else {
                            ReleaseNotesContent(text, Modifier.fillMaxSize())
                        }
                    }
                    }
                    HorizontalDivider(color = VulkanOutlineVariant)
                    Row(
                        Modifier.fillMaxWidth().heightIn(min = 66.dp).padding(horizontal = 14.dp, vertical = 9.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ExpressiveContainedIconTextButton("Close", R.drawable.ic_close, onClick = onDismiss)
                    }
                }
            }
        }
    }
}
