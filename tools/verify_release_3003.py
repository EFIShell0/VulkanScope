#!/usr/bin/env python3
import argparse
import hashlib
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', type=Path, default=Path(__file__).resolve().parents[1])
args = parser.parse_args()
root = args.root.resolve()
errors = []

def need(condition, message):
    if not condition:
        errors.append(message)

def read(rel):
    path = root / rel
    need(path.is_file(), f'missing file: {rel}')
    return path.read_text(encoding='utf-8') if path.is_file() else ''

def block(text, start, end):
    a = text.find(start)
    b = text.find(end, a + len(start)) if a >= 0 else -1
    need(a >= 0 and b > a, f'missing block: {start}')
    return text[a:b] if a >= 0 and b > a else ''

main = read('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
graph = read('app/src/main/java/com/efishell/vulkanscope/VulkanDependencyGraph.kt')
gradle = read('app/build.gradle.kts')
rules = read('rules/PROJECT_RULES.md')
audit = read('rules/3.0.3_ANALYSIS_FILE_MANAGER_TV_AUDIT.md')
changelog = read('changelog.md')

declared_files = [line.strip() for line in read('files.txt').splitlines() if line.strip()]
actual_files = sorted(path.relative_to(root).as_posix() for path in root.rglob('*') if path.is_file())
need(declared_files == actual_files, 'files.txt strict census mismatch')

need('versionCode = 3003' in gradle and 'versionName = "3.0.3"' in gradle, '3.0.3 release identity missing')
need(changelog.startswith('## 3.0.3\n'), '3.0.3 changelog entry missing or not first')
need('## Release 3.0.3 Analysis minimum confirmations, graph usability, full-screen Turnip file-manager, TV navigation and copy-feedback requirements' in rules, '3.0.3 project rules missing')
need('# VulkanScope 3.0.3 Analysis, File Manager and TV Audit' in audit, '3.0.3 audit heading missing')

minimum = block(main, '        5 -> {', '        6 -> {')
need('ExpressiveContainedIconTextButton("Load", R.drawable.ic_action_update' in minimum, 'minimum Load contained update-action control missing')
need('ExpressiveContainedIconTextButton("Delete", R.drawable.ic_delete' in minimum, 'minimum Delete contained trash control missing')
need('MinimumProfileConfirmation(MinimumProfileConfirmationKind.SAVE' in minimum, 'minimum Save does not enter confirmation state')
need('MinimumProfileConfirmation(MinimumProfileConfirmationKind.LOAD' in minimum, 'minimum Load does not enter confirmation state')
need('MinimumProfileConfirmation(MinimumProfileConfirmationKind.DELETE' in minimum, 'minimum Delete does not enter confirmation state')
confirm = block(main, 'analysisModel.state.pendingMinimumConfirmation?.let { pending ->', 'analysisModel.state.selectedEvidence?.let { selected ->')
need('title = { QuestionDialogTitle(title) }' in confirm, 'minimum confirmations do not use question title treatment')
need('ExpressiveContainedIconTextButton("Save", R.drawable.ic_save' in confirm, 'minimum Save confirm action missing')
need('ExpressiveContainedIconTextButton("Load", R.drawable.ic_action_update' in confirm, 'minimum Load confirm action missing')
need('ExpressiveContainedIconTextButton("Delete", R.drawable.ic_delete' in confirm, 'minimum Delete confirm action missing')
need('dismissButton = { ExpressiveCancelButton' in confirm, 'minimum confirmation Cancel action missing')

need('private enum class VulkanGraphEvidenceState { PRESENT, ABSENT, UNKNOWN, NOT_APPLICABLE }' in graph, 'graph evidence-state separation missing')
need('val shown = nodes.take(24)' in graph, 'graph visual node bound missing')
need('.horizontalScroll(horizontalState)' in graph and '.verticalScroll(verticalState)' in graph, 'graph two-axis bounded viewport missing')
need('Swipe horizontally for dependency depth and vertically' in graph, 'graph navigation guidance missing')
need('Visual graph is capped at ${shown.size} nodes' in graph, 'graph cap disclosure missing')
need('Dependency graph explorer' in main and 'Graph overview' in main and 'Interactive dependency map' in main, 'Analysis graph redesign sections missing')
need('not a conformance or performance result' in main, 'graph support/performance caution missing')

manager = block(main, 'private data class FileManagerBreadcrumb(', '@Composable\nprivate fun TurnipFileManagerSelectionSummary')
need('private fun fileManagerBreadcrumbs(' in manager and 'FileManagerBreadcrumb("Storage", rootText)' in manager, 'file-manager breadcrumbs missing')
need('Text(">"' in manager, 'breadcrumb separator missing')
need('private fun ExpandableFileManagerSearch(' in manager and 'HeaderActionButton(' in manager and 'R.drawable.ic_search' in manager, 'animated round file-manager search action missing')
need('Box(Modifier.fillMaxSize().zIndex(40f))' in manager, 'Turnip file manager is not full-screen overlay')
need('.padding(start = navigationStartInset, end = navigationEndInset, bottom = navigationBottomInset)' in manager, 'Turnip file manager does not preserve system navigation inset')
need('FileManagerBreadcrumbBar(' in manager and 'ExpandableFileManagerSearch(' in manager, 'Turnip full-screen manager does not use breadcrumbs/search')
need('private data class TurnipFolderEntry(' in main and 'zipFileCount: Int' in main and 'zipCountLimited: Boolean' in main, 'bounded folder ZIP count model missing')
need('private fun turnipFolderCountLabel(' in main and '"${count}+ $suffix"' in main, 'folder file-count presentation missing')
need('zipFileCount >= 256' in main, 'folder ZIP count cap missing')
options = block(main, 'private fun FileManagerOptionsChooser(', 'private fun fileManagerViewModeIcon(')
need('onDismissRequest = { }' in options, 'view/sort chooser can close outside explicit X')
need('contentDescription = "Close view and sort menu"' in options and 'onClick = { expanded = false }' in options, 'view/sort explicit X close action missing')

need('private fun Modifier.tvRemoteLazyListNavigation(state: LazyListState): Modifier' in main, 'TV lazy-list navigation fallback missing')
need('private fun Modifier.tvRemoteLazyGridNavigation(state: LazyGridState): Modifier' in main, 'TV lazy-grid navigation fallback missing')
need(main.count('if (focusManager.moveFocus(direction)) return@onPreviewKeyEvent true') >= 3, 'TV focus-first navigation ordering missing')
need(main.count('delay(24L)') >= 3, 'TV scroll-then-focus retry missing')
need('AndroidKeyEvent.KEYCODE_PAGE_DOWN' in main and 'AndroidKeyEvent.KEYCODE_PAGE_UP' in main, 'TV Page Up/Page Down handling missing')
need('.focusProperties { canFocus = !isTelevision }' in main, 'TV redundant chevron focus suppression missing')
need(main.count('.tvRemoteLazyGridNavigation(gridState).focusGroup()') >= 2, 'file-manager grids do not use TV navigation fallback')
need(main.count('.tvRemoteLazyListNavigation(listState).focusGroup()') >= 2, 'file-manager lists do not use TV navigation fallback')

copy_block = block(main, 'private fun TransientIconButton(', '@Composable\nprivate fun SettingsPage(')
need('2 -> R.drawable.ic_check' in copy_block and 'ComposeColor(0xFF73C991)' in copy_block, 'copy success green-check state missing')
need('delay(3000L)' in copy_block, 'copy success duration is not 3000 ms')
need('TransientIconButton(' in main and 'contentDescription = "Copy report ID"' in main, 'report-ID transient copy control missing')

need('@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun SharedStorageBrowserListing(' in main, '3.0.2 expressive compile opt-in regressed')
need('PixelCopy' not in main and 'toImageBitmap(' not in main, 'forbidden framebuffer/bitmap capture path present')
need('// ' not in main and '/*' not in main, 'source-code comment rule violated in MainActivity.kt')
need('// ' not in graph and '/*' not in graph, 'source-code comment rule violated in VulkanDependencyGraph.kt')

protected = {
    'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt': '6da56de2847870fbe2ad1777c951f405fafb3859b2552e3d60857686bc477544',
    'app/src/main/AndroidManifest.xml': 'f89dab96d5c123cecb227d6a44f4deef5b8b92d2688e46fb55df9bd1c54f18a8',
    'app/src/main/cpp/vulkanscope.cpp': 'fa2d5aea65b493138aa7c0cdbfa25bd715fa6e2265daa4c5c6b70b777e62f3ff',
    'registry/registry_lock.json': '79c46ebb498997b31c56b3171296ff672d229c8434a31ac77c8a17f86e4f9d0a',
    'registry/generated/registry_query_manifest.json': '60136afc325fef0dfd1ea6bfaf57da6ae61096b96a241dd126ad0fdecc3f6d00',
}
for rel, expected in protected.items():
    path = root / rel
    need(path.is_file(), f'missing protected file: {rel}')
    if path.is_file():
        need(hashlib.sha256(path.read_bytes()).hexdigest() == expected, f'protected file drift: {rel}')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 3.0.3 Analysis/file-manager/TV verifier')
