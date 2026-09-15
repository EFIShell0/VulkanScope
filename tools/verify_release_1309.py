#!/usr/bin/env python3
import argparse, hashlib, json, re, sys
from pathlib import Path


def fail(message):
    raise AssertionError(message)


def need(text, token, message=None):
    if token not in text:
        fail(message or f"missing {token!r}")


def absent(text, token, message=None):
    if token in text:
        fail(message or f"forbidden {token!r}")


def sha(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


def source_block(text: str, start: str, end: str) -> str:
    if start not in text or end not in text.split(start, 1)[1]:
        fail(f"unable to isolate source block {start!r} -> {end!r}")
    return text.split(start, 1)[1].split(end, 1)[0]


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--root', default='.')
    ap.add_argument('--skip-version', action='store_true')
    args = ap.parse_args()
    root = Path(args.root).resolve()

    main_path = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
    build_path = root / 'app/build.gradle.kts'
    rules_path = root / 'rules/PROJECT_RULES.md'
    contract_path = root / 'tests/golden/1.3.8_ui_storage_refinement_contract.json'
    if not contract_path.is_file():
        contract_path = Path(__file__).resolve().parents[1] / 'tests/golden/1.3.8_ui_storage_refinement_contract.json'
    for path in [main_path, build_path, rules_path, contract_path]:
        if not path.is_file():
            fail(f'missing required file: {path}')

    src = main_path.read_text(encoding='utf-8')
    build = build_path.read_text(encoding='utf-8')
    rules = rules_path.read_text(encoding='utf-8')
    contract = json.loads(contract_path.read_text(encoding='utf-8'))

    if not args.skip_version:
        need(build, 'versionCode = 1309', '1.3.9 versionCode missing')
        need(build, 'versionName = "1.3.9"', '1.3.9 versionName missing')
        need(rules, '## Release 1.3.9 filter popup, export/storage browser and file-manager interaction refinement')

    # Filter popup: stable transient state, selector-relative positioning, IME-first Back, bounded interactions.
    filt = source_block(src, 'private fun ExpressiveSingleFilterSelector(', 'private fun ExpressiveFilterBar(')
    for token in [
        'var expanded by rememberSaveable { mutableStateOf(false) }',
        'var query by rememberSaveable { mutableStateOf("") }',
        'PopupPositionProvider',
        'anchorBounds.left',
        'anchorBounds.bottom + popupGapPx',
        'BackHandler(enabled = popupMounted && expanded)',
        'if (imeVisible) focusManager.clearFocus(force = true) else expanded = false',
        'dismissOnBackPress = false',
        'dismissOnClickOutside = true',
        '.clip(selectorShape)',
        '.clip(rowShape).clickable(role = Role.RadioButton)',
        'ExpressiveSearchField(',
        'ExpressiveScrollHints(listState',
        'val pagingEnabled = indexed.isNotEmpty() && pageCount > 1',
        'candidate.any { !it.isDigit() }',
        'requested !in 1..pageCount',
        'KeyboardOptions(keyboardType = KeyboardType.Number)'
    ]:
        need(filt, token, 'filter popup contract drift: ' + token)
    absent(filt, 'rememberSaveable(labels', 'filter popup state became keyed to the live label list')
    absent(filt, 'dismissOnBackPress = true', 'popup window would dismiss before IME-first Back handling')

    # Complete TXT/HTML export: both actions are always full-width and not weighted into a fragile row.
    report = source_block(src, 'CapabilitySectionCard("Export complete report")', 'CapabilitySectionCard("VulkanScope Database")')
    need(report, 'SharedStoragePermissionActionButton(\n                        "Export TXT"')
    need(report, 'SharedStoragePermissionActionButton(\n                        "Export HTML"')
    if report.count('Modifier.fillMaxWidth()') < 3:
        fail('complete-report export actions are not both full-width')
    absent(report, 'Modifier.weight(1f)', 'weighted side-by-side TXT/HTML export layout returned')
    absent(report, 'SAF', 'runtime complete-report copy mentions removed legacy storage architecture')

    # Shared storage browser: current Expressive surfaces, clipped rows, immutable requested file type.
    shared = source_block(src, 'private fun SharedStorageBrowserDialog(', 'private fun SystemDriverManagerRow(')
    for token in [
        'val exportExtension = remember(request.mode, request.allowedExtensions)',
        'request.allowedExtensions.singleOrNull()?.lowercase',
        'var filenameBase by remember(request.title)',
        'Text(exportExtension?.let { ".$it" } ?: "type"',
        'The file type is fixed for this export; only the name can be changed.',
        'val fixedName = "${filenameBase.trim()}.$extension"',
        'validatedSharedStorageDestination(directory ?: return@ExpressiveContainedIconTextButton, fixedName, setOf(extension))',
        'color = VulkanSurfaceRaised',
        'contentColor = VulkanTextPrimary',
        'ExpressiveSearchField(',
        'SharedStorageFolderRow(',
        'SharedStorageFileRow(',
        'ExpressiveScrollHints(listState',
        '.clip(shape).clickable(enabled = enabled, role = Role.Button'
    ]:
        need(shared, token, 'shared-storage browser contract drift: ' + token)
    absent(shared, 'filename = request.suggestedFileName', 'export returned to a directly editable full filename')

    # Turnip manager: four icon-only modes and selected geometry = clickable geometry.
    need(src, 'private enum class TurnipFileManagerViewMode { LIST, COMPACT, GRID, DETAILS }')
    fm = source_block(src, 'private fun TurnipFileManagerDialog(', 'private fun SharedStoragePermissionActionButton(')
    for token in [
        'TurnipFileManagerViewModeButton(R.drawable.ic_view_list, "List view"',
        'TurnipFileManagerViewModeButton(R.drawable.ic_view_compact, "Compact view"',
        'TurnipFileManagerViewModeButton(R.drawable.ic_view_grid, "Grid view"',
        'TurnipFileManagerViewModeButton(R.drawable.ic_view_details, "Details view"',
        'role = Role.RadioButton',
        'LazyVerticalGrid(',
        'GridCells.Adaptive(156.dp)',
        'state.viewMode == TurnipFileManagerViewMode.DETAILS',
        '.clip(shape).clickable(enabled = enabled, role = Role.Button'
    ]:
        need(fm, token, 'Turnip file-manager contract drift: ' + token)
    package_click = '.clip(shape).clickable(enabled = enabled, role = Role.Checkbox) { onSelectedChange() }'
    if fm.count(package_click) < 2:
        fail('both list/details and grid package cards must clip the selectable card interaction')
    # View selector must be icon-only; descriptions belong in contentDescription rather than visible labels.
    mode_buttons = source_block(src, 'private fun TurnipFileManagerViewModeButtons(', 'private fun TurnipFileManagerFolderRow(')
    absent(mode_buttons, 'Text("List")', 'List text label returned to the view-mode selector')
    absent(mode_buttons, 'Text("Compact")', 'Compact text label returned to the view-mode selector')

    # New local vector resources are required and must be valid packaged Android vectors.
    for rel in contract['allowed_new_resources']:
        path = root / rel
        if not path.is_file():
            fail(f'missing view-mode vector resource: {rel}')
        xml = path.read_text(encoding='utf-8')
        need(xml, '<vector ', f'invalid vector resource: {rel}')
        need(xml, '<path ', f'vector has no path: {rel}')
        absent(xml, 'android:src=', f'network/runtime artwork reference forbidden in {rel}')
        absent(xml, 'android:uri=', f'network/runtime artwork reference forbidden in {rel}')

    # Runtime copy must not discuss removed legacy picker architecture. Historical rules are intentionally excluded.
    for match in re.finditer(r'"([^"\\]|\\.)*"', src):
        literal = match.group(0)
        if re.search(r'\bSAF\b', literal):
            fail(f'user-facing/source runtime string mentions legacy storage architecture: {literal[:120]}')

    # Everything under app/src/main other than MainActivity and the four explicitly added vectors stays predecessor-identical.
    for rel, expected in contract['immutable_app_src_main_sha256'].items():
        path = root / rel
        if not path.is_file() or sha(path) != expected:
            fail(f'unrelated production drift: {rel}')

    print('release_1309 verifier: PASS')


if __name__ == '__main__':
    try:
        main()
    except Exception as exc:
        print(f'release_1309 verifier: FAIL: {exc}', file=sys.stderr)
        sys.exit(1)
