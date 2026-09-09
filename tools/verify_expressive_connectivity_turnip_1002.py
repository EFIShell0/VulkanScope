#!/usr/bin/env python3
import argparse
import re
from pathlib import Path

parser=argparse.ArgumentParser()
parser.add_argument('--root',type=Path,default=Path(__file__).resolve().parents[1])
parser.add_argument('--skip-version',action='store_true')
args=parser.parse_args()
root=args.root.resolve()
errors=[]
main=(root/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
manifest=(root/'app/src/main/AndroidManifest.xml').read_text(encoding='utf-8')
gradle=(root/'app/build.gradle.kts').read_text(encoding='utf-8')
rules=(root/'rules/PROJECT_RULES.md').read_text(encoding='utf-8')

def need(value,message):
    if not value: errors.append(message)

if not args.skip_version:
    need(any(f'versionCode = {code}' in gradle and f'versionName = "{name}"' in gradle for name, code in [(f'1.0.{minor}', 1000 + minor) for minor in range(2, 19)]),'release identity is not a retained 1.0.2+ identity')
need('kBaseline = "Vulkan 1.4.362"' in (root/'app/src/main/cpp/registry_query_catalog.h').read_text(encoding='utf-8'),'Vulkan baseline drifted from 1.4.362')
need('implementation("androidx.compose.foundation:foundation:1.12.0")' in gradle,'Compose Foundation baseline drifted')
need('implementation("androidx.compose.material3:material3:1.5.0-alpha27")' in gradle,'Material 3 Expressive baseline drifted')
need('android.permission.ACCESS_NETWORK_STATE' in manifest,'ACCESS_NETWORK_STATE permission missing')
need(manifest.count('android.permission.ACCESS_NETWORK_STATE') == 1,'ACCESS_NETWORK_STATE permission duplicated')
for token in ['registerDefaultNetworkCallback(networkCallback)','NET_CAPABILITY_INTERNET','NET_CAPABILITY_VALIDATED','NetworkBannerState.CONNECTED','NetworkBannerState.DISCONNECTED','4_500L','Connected to network','No internet connection','R.drawable.ic_network_connected','R.drawable.ic_network_disconnected','liveRegion = LiveRegionMode.Polite']:
    need(token in main,f'validated-network presentation contract missing: {token}')
network_start=main.find('private fun registerNetworkStateCallback')
network_end=main.find('override fun onResume()',network_start)
network_block=main[network_start:network_end]
need(network_start >= 0 and network_end > network_start,'network state implementation block missing')
need('OkHttp' not in network_block and 'Request.Builder' not in network_block and 'InetAddress' not in network_block,'network status performs an application network probe')
for name in ['ic_chevron_left.xml','ic_network_connected.xml','ic_network_disconnected.xml']:
    need((root/'app/src/main/res/drawable'/name).is_file(),f'new drawable missing: {name}')
carousel_start=main.find('private fun ExpressiveFilterCarousel(')
carousel_end=main.find('\n@Composable\nprivate fun ExpressiveFilterBar',carousel_start)
carousel=main[carousel_start:carousel_end]
need(carousel_start >= 0 and carousel_end > carousel_start,'shared expressive filter carousel missing')
for token in ['LazyRow(','ic_chevron_left','ic_chevron_right','Scroll filters left','Scroll filters right','LocalLayoutDirection.current','LayoutDirection.Ltr','state.canScrollBackward','state.canScrollForward','state.animateScrollToItem(index)','focusGroup()']:
    need(token in carousel,f'filter carousel contract missing: {token}')
need(main.count('ExpressiveFilterChip(') == 2,'chip filters bypass the shared expressive carousel')
need('modifier = modifier.heightIn(min = 48.dp)' in main,'filter chip minimum touch/visual height missing')
need('Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())' not in main[main.find('private fun SupportFilterRow'):main.find('private val EMBEDDED_EXTENSION_REFERENCE_NAMES')],'support filter restored hidden horizontal strip')
for token in ['ExpressiveFilterBar(sections','ExpressiveFilterBar(extensionFilters','ExpressiveFilterBar(sources','ExpressiveFilterBar(tabs','ExpressiveMultiFilterBar(FORMAT_USAGE_FILTERS.keys.toList()']:
    need(token in main,f'filter surface is not routed through shared carousel: {token}')
for token in ['private fun ExpressiveMetricGrid','"Evidence rows" to evidenceResultCount.toString()','"Property / query" to propertyResultCount.toString()','"Safety diagnostics" to safetyEvidenceCount.toString()','"Unique names" to uniquePropertyNames.toString()','"Limits" to limitResultCount.toString()','expandedText || maxWidth < 360.dp','maxWidth < 760.dp']:
    need(token in main,f'responsive metric contract missing: {token}')
need('evidence rows · $propertyResultCount property/query rows' not in main,'flat Properties evidence count sentence remains')
for token in ['OpenableColumns.DISPLAY_NAME','OpenableColumns.SIZE','DocumentsContract.Document.COLUMN_LAST_MODIFIED','driverVersion = text("driverVersion")','driverDate = text("driverDate") ?: text("date")','packageVersion = text("packageVersion")','vendor = text("vendor")','author = text("author")','minApi = minApi','librarySizeBytes = library.length()','"Driver version"','"Driver date"','"Minimum Android API"']:
    need(token in main,f'Turnip provenance/metadata contract missing: {token}')
legacy_provenance = '.putString("turnip_source_location", source.location)' in main and 'turnip_source_name' in main
managed_provenance = 'private const val TURNIP_MANAGER_SOURCE_NAME = "source.json"' in main and 'writeTurnipSourceInfo(' in main and 'put("location", source.location.take(4096))' in main and ('"SAF / source location"' in main or '"Source location"' in main)
need(legacy_provenance or managed_provenance,'Turnip source provenance persistence/display contract missing')
need('Not provided by package' in main,'missing Turnip metadata is not explicitly represented')
need(('Source-document provenance is kept only in app-private settings and is not added to technical reports or Database submissions.' in main) or ('Source-document provenance is private app metadata and is not added to technicalReport or VulkanScope Database submissions.' in main),'Turnip provenance privacy explanation missing')
serialization_start=main.find('private fun technicalReportJson')
serialization_end=main.find('\nprivate fun reportToText',serialization_start)
serialization=main[serialization_start:serialization_end]
need(serialization_start >= 0 and serialization_end > serialization_start,'technicalReport serialization block missing')
for token in ['turnip_source_name','turnip_source_location','turnip_source_size','turnip_source_modified','turnip_imported_at']:
    need(token not in serialization,f'private Turnip provenance leaked into technicalReport serialization: {token}')
text_start=main.find('private fun reportToText')
html_start=main.find('private fun reportToHtml',text_start)
need(all(token not in main[text_start:html_start] for token in ['turnip_source_location','turnip_source_name']),'private Turnip provenance leaked into TXT export')
need('## Release 1.0.2 Material 3 Expressive filters, connectivity state, Turnip metadata and accessibility requirements' in rules,'PROJECT_RULES 1.0.2 contract missing')
need((root/'rules/1.0.2_EXPRESSIVE_CONNECTIVITY_TURNIP_ACCESSIBILITY_AUDIT.md').is_file(),'1.0.2 audit file missing')
need((root/'tests/golden/1.0.1_regression_contract.json').is_file(),'1.0.1 predecessor regression contract missing')
need('FontFamily(' not in main and not (root/'app/src/main/res/font').exists(),'bundled/custom font path violates system-font contract')
need('onKeyEvent' not in carousel and 'onPreviewKeyEvent' not in carousel,'filter carousel uses custom TV key interception')
if errors:
    for error in errors: print('FAIL:',error)
    raise SystemExit(1)
print('PASS VulkanScope 1.0.2 expressive filter/connectivity/Turnip/accessibility contract')
