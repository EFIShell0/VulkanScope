#!/usr/bin/env python3
import argparse
import re
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', default=str(Path(__file__).resolve().parents[1]))
parser.add_argument('--skip-version', action='store_true')
args = parser.parse_args()
root = Path(args.root).resolve()
main = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
gradle = root / 'app/build.gradle.kts'
errors = []


def need(condition, message):
    if not condition:
        errors.append(message)


kt = main.read_text(encoding='utf-8')
gd = gradle.read_text(encoding='utf-8')
vm = re.search(r'versionName\s*=\s*"(\d+)\.(\d+)\.(\d+)"', gd)
release_version = tuple(map(int, vm.groups())) if vm else (0, 0, 0)


def body(name):
    match = re.search(r'private fun (?:[A-Za-z0-9_<>?.]+\.)?' + re.escape(name) + r'\s*\(', kt)
    if not match:
        return ''
    start = kt.find('{', match.start())
    depth = 0
    in_string = False
    escaped = False
    for index in range(start, len(kt)):
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


if not args.skip_version:
    vc = re.search(r'versionCode\s*=\s*(\d+)', gd)
    need(bool(vm and vc and release_version >= (0, 80, 6) and int(vc.group(1)) >= 806), '0.80.6+ compatible identity missing')
need('import androidx.compose.foundation.selection.selectable' in kt, 'selectable accessibility import missing')
need('import androidx.compose.foundation.selection.toggleable' in kt, 'toggleable accessibility import missing')
need('import androidx.compose.ui.semantics.LiveRegionMode' in kt, 'live-region semantics import missing')
need('import androidx.compose.ui.semantics.Role' in kt, 'semantic Role import missing')
need('import androidx.compose.ui.semantics.heading' in kt, 'heading semantics import missing')
need('import androidx.compose.ui.semantics.liveRegion' in kt, 'live-region property import missing')
need('import androidx.compose.ui.semantics.semantics' in kt, 'semantics modifier import missing')

layout = body('preferExpandedTextLayout')
need(layout, 'preferExpandedTextLayout missing')
need('configuration.fontScale >= 1.3f' in layout, 'large-font branch missing')
need('configuration.screenWidthDp < 360' in layout, 'narrow-display branch missing')

scroll = body('ScrollBoundaryIndicatorColumn') + body('ScrollBoundaryIndicatorBubble')
need(scroll.count('contentDescription = null') >= 1, 'scroll-boundary arrows remain TalkBack announcement targets')
need('contentDescription = "More content above"' not in scroll and 'contentDescription = "More content below"' not in scroll, 'scroll-boundary duplicate descriptions remain')

hero = body('VendorLogo')
need('contentDescription = null' in hero, 'decorative vendor logo still duplicates adjacent vendor text')

overview_destination = body('OverviewDestinationCard')
need('contentDescription = null' in overview_destination, 'overview chevron still adds redundant TalkBack announcement')

rail = body('CompactNavigationRail')
need('if (expandedTextLayout) 104.dp else 80.dp' in rail, 'navigation rail does not widen for large text')
need('.heightIn(min = 54.dp)' in rail, 'navigation rail item still uses clipping-prone fixed height')
need('maxLines = if (expandedTextLayout) 2 else 1' in rail, 'navigation rail label does not gain a second line for large text')
need('Modifier.fillMaxWidth().padding(horizontal = 2.dp, vertical = 4.dp)' in rail, 'navigation rail content still forces fillMaxSize instead of natural growable height')
need('fontSize = if (expandedTextLayout) 11.sp else 9.sp' in rail, 'navigation rail label size does not adapt under large text')
if release_version >= (1, 0, 15):
    animated_nav = body('AnimatedNavigationIcon')
    need('AnimatedNavigationIcon(' in rail, 'navigation rail no longer routes through the shared animated icon wrapper')
    need('contentDescription = null' in animated_nav, 'shared animated navigation icon duplicates visible labels for TalkBack')
else:
    need('contentDescription = null' in rail, 'navigation rail icon duplicates visible label for TalkBack')

quick = body('QuickAccessCard')
need('.heightIn(min = 72.dp)' in quick, 'Quick Access card still uses fixed height')
need('maxLines = 2' in quick, 'Quick Access label remains single-line at large text')
need('style = MaterialTheme.typography.labelSmall' in quick, 'Quick Access typography bypasses Material text scale')

overview = body('OverviewPage')
need('val expandedTextLayout = preferExpandedTextLayout()' in overview, 'Overview large-text layout state missing')
need(('val quickAccessColumns = if (expandedTextLayout) 2 else 4' in overview) or ('val quickAccessColumns = when {' in overview and 'expandedTextLayout || maxWidth < 300.dp -> 1' in overview and 'maxWidth < 540.dp -> 2' in overview), 'Quick Access does not reduce columns under large text')
need('if (expandedTextLayout)' in overview and 'MetricCard("Vulkan"' in overview, 'Overview metric cards do not adapt under large text')

header = body('AppHeader')
need('val expandedTextLayout = preferExpandedTextLayout()' in header, 'AppHeader large-text layout state missing')
need('if (expandedTextLayout)' in header and 'VulkanScope · ${page.title}' in header, 'AppHeader does not replace fixed logo/title geometry for large text')

key_value = body('ExpressiveEvidenceRow')
need('preferExpandedTextLayout()' in key_value, 'key/value rows do not stack for large text')
need('semantics(mergeDescendants = true)' in key_value, 'key/value rows are not merged into one TalkBack reading unit')

section = body('CapabilitySectionCard')
need('heading()' in section, 'capability section titles are not exposed as accessibility headings')

collection = body('CollectionStatusBanner')
need('liveRegion = LiveRegionMode.Polite' in collection, 'collection state changes are not exposed as a polite accessibility live region')
need('semantics(mergeDescendants = true)' in collection, 'collection status is not merged into one announcement unit')
need('contentDescription = null' in collection, 'collection icon duplicates the status announcement')
need('maxLines = 1' not in collection, 'collection status copy remains clipping-prone at large text')

update = body('UpdateStatusBanner')
need('liveRegion = LiveRegionMode.Polite' in update, 'update state changes are not exposed as an accessibility live region')

settings = body('SettingsPage')
need('.toggleable(' in settings and 'role = Role.Switch' in settings, 'Direct GitHub updates row is not a single TalkBack Switch target')
need('ExpressiveSwitch(checked = directUpdatesEnabled, onCheckedChange = null)' in settings, 'nested update Switch remains a duplicate TalkBack action')

driver = body('DriverOption')
need('.selectable(' in driver and 'role = Role.RadioButton' in driver, 'driver option is not a single semantic RadioButton target')
need('ExpressiveRadioButton(selected = selected, enabled = enabled, onClick = null)' in driver, 'nested driver RadioButton remains a duplicate action')

update_key_value = body('UpdateDialogKeyValue')
need('preferExpandedTextLayout()' in update_key_value, 'update metadata rows do not stack for large text')
need('semantics(mergeDescendants = true)' in update_key_value, 'update metadata key/value is not merged for TalkBack')

release_line = body('ReleaseNoteLine')
need('heading()' in release_line, 'release-note headings are not exposed as accessibility headings')
need('semantics(mergeDescendants = true)' in release_line, 'release-note rows are not merged into coherent TalkBack targets')

dialog = body('UpdateConfirmationDialog')
need('val expandedTextLayout = preferExpandedTextLayout()' in dialog, 'update dialog lacks large-text layout state')
need('val releaseNotesMaxHeight = if (expandedTextLayout) 220.dp else 360.dp' in dialog, 'release-note viewport does not reserve space under large text')
need('heightIn(max = releaseNotesMaxHeight)' in dialog, 'update dialog does not use adaptive release-note viewport height')

version_block = body('ExpressiveVersionBlock')
need('preferExpandedTextLayout()' in version_block, 'Info version metadata does not adapt for large text')
need('if (expandedTextLayout)' in version_block, 'Info metadata pills remain forced side-by-side under large text')

info = body('InfoPage')
need('val expandedTextLayout = preferExpandedTextLayout()' in info, 'Info page lacks large-text layout state')
need('CapabilitySectionCard("Export complete report")' in info and 'if (expandedTextLayout)' in info and 'Modifier.fillMaxWidth(), completeReportReady && !exportBusy, true' in info, 'Info export actions remain forced side-by-side under large text')

bottom_nav_section = kt[kt.find('ShortNavigationBar('):kt.find('ShortNavigationBar(', kt.find('ShortNavigationBar(')) + 2200]
if release_version >= (1, 0, 15):
    need('AnimatedNavigationIcon(' in bottom_nav_section, 'bottom navigation no longer routes through the shared animated icon wrapper')
    need('contentDescription = null' in body('AnimatedNavigationIcon'), 'bottom navigation animated icon duplicates its visible label for TalkBack')
else:
    need('contentDescription = null' in bottom_nav_section, 'bottom navigation icon duplicates its visible label for TalkBack')

if errors:
    for error in errors:
        print('FAIL ' + error)
    raise SystemExit(1)
print('PASS 0.80.6 TalkBack semantics and large-text/display-size source contract')
