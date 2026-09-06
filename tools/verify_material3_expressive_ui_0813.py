#!/usr/bin/env python3
import argparse
import re
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', type=Path, default=Path(__file__).resolve().parents[1])
parser.add_argument('--skip-version', action='store_true')
args = parser.parse_args()
root = args.root.resolve()
errors = []
main_path = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
gradle_path = root / 'app/build.gradle.kts'
rules_path = root / 'rules/PROJECT_RULES.md'
main = main_path.read_text(encoding='utf-8')
gradle = gradle_path.read_text(encoding='utf-8')
rules = rules_path.read_text(encoding='utf-8')
superseded_by_0815 = 'versionName = "0.80.15"' in gradle

def require(condition, message):
    if not condition:
        errors.append(message)

def block(name, next_name=None):
    start = main.find(name)
    if start < 0:
        return ''
    if next_name is None:
        return main[start:start + 5000]
    end = main.find(next_name, start + len(name))
    return main[start:] if end < 0 else main[start:end]

if not args.skip_version:
    require('versionCode = 813' in gradle and 'versionName = "0.80.13"' in gradle, 'release identity is not 0.80.13/813')

require('androidx.compose.material3:material3:1.5.0-alpha27' in gradle, 'Material 3 pin is not 1.5.0-alpha27')
require('MaterialExpressiveTheme(' in main and 'motionScheme = MotionScheme.expressive()' in main, 'MaterialExpressiveTheme/MotionScheme.expressive is missing')
require(main.count('import androidx.compose.ui.semantics.Role\n') == 1, 'Compose Role import is missing or duplicated')
require(main.count('import androidx.compose.ui.semantics.role\n') == 1, 'Compose semantics role extension import is missing or duplicated')
for token in ['largeIncreased = RoundedCornerShape(32.dp)', 'extraLargeIncreased = RoundedCornerShape(36.dp)', 'extraExtraLarge = RoundedCornerShape(40.dp)']:
    require(token in main, f'alpha27 expressive shape-scale token missing: {token}')

page = block('private fun VulkanLazyPage(', '@Composable\nprivate fun ScrollBoundaryIndicators')
if superseded_by_0815:
    require('end = 18.dp' in page and 'ScrollBoundaryIndicators(listState, Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 10.dp))' in page, '0.80.15 superseding overlay indicator layout is missing')
else:
    require('end = 46.dp' in page, 'top-level lazy pages do not reserve an indicator lane')
    require('ScrollBoundaryIndicators(listState, Modifier.align(Alignment.CenterEnd).padding(end = 8.dp))' in page, 'top-level scroll indicator placement is stale')
require('userScrollEnabled = true' in page, 'top-level user scrolling is not explicitly preserved')
require('listState.canScrollBackward' in main and 'listState.canScrollForward' in main, 'actual-boundary scroll state semantics are missing')

indicator = block('private fun ScrollBoundaryIndicatorColumn(', '@Composable\nprivate fun PageContent')
if superseded_by_0815:
    require('Modifier.padding(8.dp).size(24.dp)' in main and 'rememberScrollIndicatorVisibility' in main and 'Modifier.align(Alignment.TopCenter)' in indicator and 'Modifier.align(Alignment.BottomCenter)' in indicator, '0.80.15 superseding activity-aware indicator geometry is missing')
else:
    require('shape = RoundedCornerShape(999.dp)' in indicator and 'Modifier.padding(horizontal = 4.dp, vertical = 5.dp)' in indicator, 'scroll indicators are not presented as a compact shared rail')
    require('modifier = Modifier.padding(4.dp).size(16.dp)' in indicator, 'scroll indicator geometry is not compact')

kv = block('private fun CapabilityKeyValue(key: String, value: String)', '@Composable\nprivate fun CapabilityStatusBadge')
require('BoxWithConstraints(Modifier.fillMaxWidth())' in kv, 'key/value layout is not width-aware')
require('value.length > 34' in kv and 'key.length > 26' in kv, 'key/value stacking threshold does not prevent narrow wrapped value columns')
require('LocalDetailKeyValuePresentation.current' in kv, 'detail-dialog key/value presentation is not context-aware')
if superseded_by_0815:
    require('androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant)' in kv and 'color = VulkanSurfaceTonal' in kv, '0.80.15 grouped detail evidence-card presentation is missing')
else:
    require('ComposeColor.Transparent else VulkanSurfaceTonal' in kv, 'detail key/value rows do not flatten inside the dialog')
    require('HorizontalDivider(Modifier.padding(horizontal = 14.dp), color = VulkanOutlineVariant)' in kv, 'detail key/value rows lack the shared divider treatment')
require('TextAlign.End' not in kv, 'key/value values remain right-aligned and vulnerable to jagged wrapping')

update_kv = block('private fun UpdateDialogKeyValue(key: String, value: String)', '@Composable\nprivate fun DirectUpdatesConsentDialog')
require('BoxWithConstraints(Modifier.fillMaxWidth())' in update_kv, 'update metadata is not width-aware')
require('value.length > 32' in update_kv and 'TextAlign.End' not in update_kv, 'update metadata retains the cramped right-aligned layout')

detail = block('private fun ScrollableDetailDialog(', '@Composable\nprivate fun FormatsPage')
require('Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false))' in detail, 'complex detail content still uses the fixed AlertDialog slot layout')
if superseded_by_0815:
    require('shape = MaterialTheme.shapes.extraLarge' in detail and 'widthIn(max = 560.dp)' in detail, '0.80.15 responsive grouped dialog geometry is missing')
    require('bodyMaxHeight = minOf(540.dp, maxOf(140.dp, (configuration.screenHeightDp - 200).dp))' in detail, '0.80.15 dialog body height adaptation is missing')
    require('padding(end = 38.dp)' not in detail and 'ScrollBoundaryIndicators(scrollState' in detail, '0.80.15 overlay detail indicator behavior is missing')
else:
    require('shape = MaterialTheme.shapes.extraExtraLarge' in detail, 'detail dialog does not use the alpha27 expressive large shape')
    require('widthIn(max = 640.dp)' in detail, 'detail dialog does not have a responsive width cap')
    require('bodyMaxHeight = minOf(560.dp, maxOf(220.dp, (configuration.screenHeightDp - 240).dp))' in detail, 'detail dialog body does not adapt to display height')
    require('padding(end = 38.dp)' in detail and 'ScrollBoundaryIndicators(scrollState' in detail, 'detail body does not reserve space for its boundary indicators')
require('CompositionLocalProvider(LocalDetailKeyValuePresentation provides true)' in detail, 'detail dialog does not activate the flat evidence-row presentation')

button = block('private fun DetailAffordance(', '@Composable\nprivate fun ScrollableDetailDialog')
require('TextButton(' in button and 'ButtonDefaults.shapes(' in button and 'pressedShape = RoundedCornerShape(24.dp)' in button, 'Details action does not use expressive button morphing')
require('role = Role.Button' in button, 'Details action lost explicit Button semantics')

analysis = block('private fun LazyListScope.analysisWorkspaceItems(', '\n\n}\n\n\n@OptIn')
require('ExpressiveSwitch(checked = state.includeUnchanged, onCheckedChange = null)' in analysis, 'Analysis still bypasses the shared expressive switch wrapper')
require(re.search(r'(?<!Expressive)Switch\s*\(', main) is not None, 'shared Switch wrapper is unexpectedly missing')
raw_switches = list(re.finditer(r'(?<!Expressive)Switch\s*\(', main))
require(len(raw_switches) == 1, 'a direct Material Switch exists outside the shared expressive wrapper')

loading = block('private fun LoadingView()', '@Composable\nprivate fun EmptyState')
require('VulkanLazyPage(' in loading and 'CapabilitySectionCard("Vulkan inspection")' in loading, 'startup loading remains outside the shared expressive hierarchy')
require('LoadingIndicator(color = VulkanAccentSoft' in loading and 'ExpressiveLinearProgressIndicator' in loading, 'startup loading lacks expressive activity/progress presentation')
require('liveRegion = LiveRegionMode.Polite' in loading, 'startup loading lost polite accessibility status semantics')

empty = block('private fun EmptyState(message: String)', 'private fun capabilitySectionIcon')
require('Surface(color = VulkanSurfaceLow, shape = MaterialTheme.shapes.large' in empty, 'empty states remain unstyled text-only placeholders')

formats = block('private fun FormatsPage(', 'private fun propertySectionRank')
extensions = block('private fun ExtensionsPage(', 'private fun imageFormatQueryGroupState')
require('CapabilityItemCard(containerColor = VulkanSurfaceRaised)' in formats, 'Format rows bypass the shared capability-card hierarchy')
require('CapabilityItemCard(containerColor = VulkanSurfaceRaised)' in extensions and 'CapabilityItemCard(containerColor = ComposeColor(0xFF211B12))' in extensions, 'Extension rows bypass the shared capability-card hierarchy')

for token in ['maxLines = 2, overflow = TextOverflow.Ellipsis', 'maxLines = 3, overflow = TextOverflow.Ellipsis']:
    require(token in block('private fun ExpressiveActionButton(', '@Composable\nprivate fun ExpressiveIdentityBlock'), f'action-card text wrapping token missing: {token}')

require('## Release 0.80.13 Material 3 Expressive full-UI coherence requirements' in rules, 'PROJECT_RULES 0.80.13 UI contract is missing')
require((root / 'rules/0.80.13_MATERIAL3_EXPRESSIVE_FULL_UI_AUDIT.md').is_file(), '0.80.13 UI audit document is missing')
require((root / 'tests/golden/0.80.12_regression_contract.json').is_file(), '0.80.12 -> 0.80.13 regression contract is missing')

if errors:
    for error in errors:
        print(f'FAIL: {error}')
    raise SystemExit(1)
print('PASS VulkanScope 0.80.13 Material 3 Expressive full-UI coherence contract')
