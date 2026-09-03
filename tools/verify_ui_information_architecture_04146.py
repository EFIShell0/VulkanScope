#!/usr/bin/env python3
import argparse
import re
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', default=str(Path(__file__).resolve().parents[1]))
args = parser.parse_args()
root = Path(args.root).resolve()
kt_path = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
gradle_path = root / 'app/build.gradle.kts'
kt = kt_path.read_text(encoding='utf-8')
gradle = gradle_path.read_text(encoding='utf-8')
errors = []

def require(condition, message):
    if not condition:
        errors.append(message)

def function_body(name):
    match = re.search(r'private fun (?:[A-Za-z0-9_<>?.]+\.)?' + re.escape(name) + r'\s*\(', kt)
    if not match:
        return ''
    open_brace = kt.find('{', match.start())
    if open_brace < 0:
        return ''
    depth = 0
    in_string = False
    escaped = False
    for index in range(open_brace, len(kt)):
        char = kt[index]
        if in_string:
            if escaped:
                escaped = False
            elif char == '\\':
                escaped = True
            elif char == '"':
                in_string = False
            continue
        if char == '"':
            in_string = True
        elif char == '{':
            depth += 1
        elif char == '}':
            depth -= 1
            if depth == 0:
                return kt[match.start():index + 1]
    return ''

version_match = re.search(r'versionName\s*=\s*"([0-9.]+)"', gradle)
version_tuple = tuple(int(x) for x in version_match.group(1).split('.')) if version_match else (0,)
require(version_tuple >= (0, 41, 46), '0.41.46+ compatible release identity missing')
require('Video("Vulkan Video")' in kt, 'dedicated Vulkan Video page identity missing')
if version_tuple < (0, 80, 3):
    require('Analysis("Analysis")' not in kt and 'Page.Analysis' not in kt and 'private fun AnalysisPage(' not in kt, 'standalone Analysis destination remains')
else:
    require('Analysis("Analysis workspace")' in kt and 'Analysis("Analysis")' not in kt and 'Page.Analysis -> AnalysisPage(' in kt, '0.80.3 Analysis tool destination contract missing')
require('private fun VulkanVideoPage(' in kt and 'Page.Video -> VulkanVideoPage(' in kt, 'dedicated Vulkan Video page routing missing')
require('Page.Video -> listOf("queue2", "videoCapabilities")' in kt, 'Vulkan Video page does not request existing video/queue evidence groups')
require('QuickAccessCard("Analysis"' not in kt, 'Overview Analysis quick-access button remains')
require('QuickAccessCard("Vulkan Video", Page.Video' in kt, 'Overview Vulkan Video quick access missing')
require('listOf(Page.Features, Page.Memory, Page.Queues, Page.Video, Page.Formats, Page.Properties)' in kt, 'Explore information architecture does not include Vulkan Video')

overview = function_body('OverviewPage')
require(overview, 'OverviewPage missing')
snapshot_pos = overview.find('CapabilitySectionCard("Capability snapshot")')
if version_tuple < (0, 80, 3):
    analysis_pos = overview.find('analysisWorkspaceItems(')
    require(snapshot_pos >= 0 and analysis_pos > snapshot_pos, 'Analysis workspace is not placed after Capability snapshot')
    require('rememberAnalysisWorkspaceModel(' in overview, 'Overview does not retain Analysis workspace behavior')
else:
    require(snapshot_pos >= 0 and 'destination = Page.Analysis' in overview and 'rememberAnalysisWorkspaceModel(' not in overview, '0.80.3 Overview Analysis destination/lazy ownership contract missing')
    analysis_page = function_body('AnalysisPage')
    require('rememberAnalysisWorkspaceModel(' in analysis_page and 'analysisWorkspaceItems(' in analysis_page and 'VulkanLazyPage(' in analysis_page, '0.80.3 separate Analysis page behavior missing')

require('private fun VulkanLazyPage(' in kt, 'common lazy-page wrapper missing')
require('private fun ScrollBoundaryIndicators(' in kt, 'scroll boundary indicator composable missing')
require('derivedStateOf { listState.canScrollBackward }' in kt, 'up-arrow boundary condition missing')
require('derivedStateOf { listState.canScrollForward }' in kt, 'down-arrow boundary condition missing')
require('R.drawable.ic_scroll_up' in kt and 'R.drawable.ic_scroll_down' in kt, 'scroll arrow vector assets not consumed')

page_functions = ['OverviewPage', 'VulkanPage', 'DisplayPage', 'SurfacePage', 'FeaturesPage', 'MemoryPage', 'QueuesPage', 'VulkanVideoPage', 'FormatsPage', 'PropertiesPage', 'ProfilesPage', 'InfoPage', 'SettingsPage', 'ExtensionsPage'] + (['EncyclopediaPage', 'AnalysisPage'] if version_tuple >= (0, 80, 3) else [])
for name in page_functions:
    body = function_body(name)
    require(body, f'{name} missing')
    require('VulkanLazyPage(' in body, f'{name} does not use the common boundary-aware lazy page wrapper')
    require('LazyColumn(' not in body, f'{name} retains a private top-level LazyColumn instead of the common wrapper')

require(re.search(r'private fun LazyListScope\.analysisWorkspaceItems\s*\(', kt) is not None, 'lazy Analysis workspace item emitter missing')
analysis_body = function_body('analysisWorkspaceItems')
require('items(model.diffRows' in analysis_body, 'Analysis diff collection is not retained as lazy items')
require('items(model.profileResults' in analysis_body, 'Analysis profile collection is not retained as lazy items')
require('items(model.graphEntries.drop(1)' in analysis_body, 'Analysis dependency graph collection is not retained as lazy items')
require('items(model.visibleWatched' in analysis_body, 'Analysis watch collection is not retained as lazy items')
require('CapabilitySectionCard("Analysis workspace")' in analysis_body, 'Analysis workspace presentation heading missing')

video_body = function_body('VulkanVideoPage')
for needle in ['Overview', 'Decode', 'Encode', 'Formats', 'Queues', 'exact 4:2:0 8-bit', 'sampled-profile']:
    require(needle in video_body, f'Vulkan Video UI missing required presentation token: {needle}')
require('parseVulkanVideoEvidence' in kt and 'VideoProfileEvidence' in kt, 'Vulkan Video evidence parser/model missing')
require('videoEvidenceState(' in kt, 'Vulkan Video evidence-state classifier missing')
require('if (evidence.profiles.isNotEmpty()) "Available: exact-profile Vulkan Video census evidence was collected."' in video_body, 'Vulkan Video available-path query status is not derived from retained exact-profile evidence')
require('capabilityQuery?.takeIf { it.startsWith("Unavailable", true) || it.startsWith("Unknown", true) }' in video_body, 'Vulkan Video unavailable/unknown entry-point evidence is not preserved')

library_pins = [
    ('AndroidX Core KTX', '1.19.0'),
    ('AndroidX Activity Compose', '1.13.0'),
    ('Compose UI', '1.12.0'),
    ('Compose Foundation', '1.12.0'),
    ('Compose Animation', '1.12.0'),
    ('Material 3', '1.5.0-alpha27'),
    ('Lifecycle Runtime Compose', '2.11.0'),
    ('OkHttp', '5.5.0'),
    ('ZXing Core', '3.5.4'),
    ('Vulkan-Headers', '1.4.361'),
    ('libadrenotools', '8fae8ce254dfc1344527e05301e43f37dea2df80'),
]
require('CapabilitySectionCard("Libraries")' in function_body('InfoPage'), 'Info Libraries section missing')
for name, version in library_pins:
    require(name in kt and version in kt, f'Info library identity missing or stale: {name} {version}')

build_pins = {
    'androidx.core:core-ktx:1.19.0': ('AndroidX Core KTX', '1.19.0'),
    'androidx.activity:activity-compose:1.13.0': ('AndroidX Activity Compose', '1.13.0'),
    'androidx.compose.ui:ui:1.12.0': ('Compose UI', '1.12.0'),
    'androidx.compose.foundation:foundation:1.12.0': ('Compose Foundation', '1.12.0'),
    'androidx.compose.animation:animation:1.12.0': ('Compose Animation', '1.12.0'),
    'androidx.compose.material3:material3:1.5.0-alpha27': ('Material 3', '1.5.0-alpha27'),
    'androidx.lifecycle:lifecycle-runtime-compose:2.11.0': ('Lifecycle Runtime Compose', '2.11.0'),
    'com.squareup.okhttp3:okhttp:5.5.0': ('OkHttp', '5.5.0'),
    'com.google.zxing:core:3.5.4': ('ZXing Core', '3.5.4'),
}
for coordinate in build_pins:
    require(coordinate in gradle, f'build dependency pin drifted: {coordinate}')
cmake = (root / 'app/src/main/cpp/CMakeLists.txt').read_text(encoding='utf-8')
require('GIT_TAG 31386378257ac8653ce5b32c93baec385259ebbe' in cmake, 'Vulkan-Headers commit pin drifted')
require('GIT_TAG 8fae8ce254dfc1344527e05301e43f37dea2df80' in cmake, 'libadrenotools commit pin drifted')

for resource in ['ic_scroll_up.xml', 'ic_scroll_down.xml', 'ic_video.xml']:
    require((root / 'app/src/main/res/drawable' / resource).is_file(), f'missing local vector resource: {resource}')

if errors:
    for error in errors:
        print(f'FAIL {error}')
    raise SystemExit(1)
print('PASS 0.41.46 UI information architecture')
