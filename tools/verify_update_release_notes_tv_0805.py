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


vm = re.search(r'versionName\s*=\s*"(\d+)\.(\d+)\.(\d+)"', gd)
vc = re.search(r'versionCode\s*=\s*(\d+)', gd)
version_tuple = tuple(map(int, vm.groups())) if vm else (0, 0, 0)
if not args.skip_version:
    need(bool(vm and vc and version_tuple >= (0, 80, 5) and int(vc.group(1)) >= 805), '0.80.5+ compatible identity missing')
need('androidx.compose.foundation:foundation:1.12.0' in gd, 'Compose Foundation 1.12.0 TV lazy-layout baseline drifted')
release = body('ReleaseNotesContent')
line = body('ReleaseNoteLine')
dialog = body('UpdateConfirmationDialog')
update_banner = body('UpdateStatusBanner')
tv = body('tvBrowseModifier')
need(release, 'ReleaseNotesContent missing')
need('rememberLazyListState()' in release, 'release notes do not own a LazyListState')
need('Box(' in release and 'LazyColumn(' in release, 'release notes are not hosted in a bounded Box plus LazyColumn')
need('state = listState' in release, 'release notes LazyColumn is not bound to its list state')
need('focusGroup()' in release, 'release notes list lacks TV focus grouping')
need('userScrollEnabled = true' in release, 'release notes touch/user scrolling is not explicitly preserved')
need('itemsIndexed(lines' in release and 'ReleaseNoteLine(raw)' in release, 'release-note rows are not emitted as indexed lazy items')
need('ExpressiveScrollHints(listState' in release, 'release notes lack shared up/down boundary indicators')
need(line, 'ReleaseNoteLine missing')
need('then(tvBrowseModifier(shape))' in line, 'release-note rows are not TV focus/bring-into-view targets')
need('RoundedCornerShape(10.dp)' in line, 'release-note focus surface shape is not design-aligned')
need('Surface(' in line and 'ComposeColor.Transparent' in line, 'release-note focus surface does not preserve the existing dark release-note presentation')
need('Modifier.padding(horizontal = 8.dp, vertical = 4.dp)' in line, 'release-note focus target lacks readable TV spacing')
need('bringIntoViewRequester' in tv and 'onFocusChanged' in tv and 'focusable()' in tv, 'shared TV browse modifier no longer provides focus-driven bring-into-view')
need(('shape = RoundedCornerShape(32.dp)' in dialog or 'shape = MaterialTheme.shapes.extraLarge' in dialog) and 'containerColor = VulkanSurfaceRaised' in dialog, 'update dialog lost Material 3 Expressive dialog geometry/color')
need(dialog.count('RoundedCornerShape(20.dp)') >= 2 or dialog.count('shape = MaterialTheme.shapes.medium') >= 2, 'update metadata/release-note inner surfaces lost matching expressive geometry')
need('heightIn(max = releaseNotesMaxHeight)' in dialog or 'ReleaseNotesContent(update.releaseNotes, Modifier.fillMaxWidth().heightIn(max = 360.dp))' in dialog, 'release-note viewport is no longer bounded to the established update-dialog height')
need('releaseNotesMaxHeight = if (expandedTextLayout) 220.dp else 360.dp' in dialog or 'heightIn(max = 360.dp)' in dialog, 'release-note viewport no longer retains the 360dp normal-scale ceiling')
if version_tuple >= (1, 0, 13):
    need('ExpressivePrimaryIconTextButton("Download update", R.drawable.ic_download_update, enabled = networkAvailable, onClick = onConfirm)' in dialog and 'ExpressiveCancelButton(onClick = onDismiss)' in dialog, 'update dialog 1.0.13+ semantic action hierarchy drifted')
    need('ChevronAffordance("Review", "Review update")' in update_banner, 'update banner 1.0.13+ Review chevron action missing')
else:
    need(('ExpressivePrimaryButton("Download APK", onConfirm)' in dialog or 'ExpressivePrimaryButton("Download APK", enabled = networkAvailable, onClick = onConfirm)' in dialog) and ('ExpressiveTextButton("Cancel", onDismiss)' in dialog or 'ExpressiveTextButton("Cancel", onClick = onDismiss)' in dialog or 'ExpressiveCancelButton(onClick = onDismiss)' in dialog), 'update dialog action hierarchy drifted')
    need('ExpressiveTextButton("Review")' in update_banner, 'update banner Review action missing')
need(('RoundedCornerShape(24.dp)' in update_banner or 'shape = MaterialTheme.shapes.large' in update_banner) and 'color = VulkanSurfaceRaised' in update_banner, 'update banner geometry/color drifted from application design language')
need('.onKeyEvent' not in release and '.onPreviewKeyEvent' not in release, 'release notes add custom D-pad interception instead of standard Compose focus scrolling')
if errors:
    for error in errors:
        print('FAIL ' + error)
    raise SystemExit(1)
print('PASS 0.80.5 update release-notes scrolling, TV focus and design-integrity contract')
