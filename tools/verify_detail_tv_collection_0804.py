#!/usr/bin/env python3
import argparse
import re
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', default=str(Path(__file__).resolve().parents[1]))
args = parser.parse_args()
root = Path(args.root).resolve()
main = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
gradle = root / 'app/build.gradle.kts'
close_icon = root / 'app/src/main/res/drawable/ic_close.xml'
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

vm = re.search(r'versionName\s*=\s*"(\d+)\.(\d+)\.(\d+)"', gd); vc = re.search(r'versionCode\s*=\s*(\d+)', gd)
need(bool(vm and vc and tuple(map(int, vm.groups())) >= (0, 80, 4) and int(vc.group(1)) >= 804), '0.80.4+ compatible identity missing')
need('private enum class CollectionStatus { IDLE, COLLECTING, COMPLETED, FAILED }' in kt, 'FAILED collection state missing')
need('private fun collectionFinishedSuccessfully(' in kt, 'collection success classifier missing')
finish = body('finishCollectionStatusSoon')
need('CollectionStatus.COMPLETED' in finish and 'CollectionStatus.FAILED' in finish and 'collectionFinishedSuccessfully' in finish, 'collection completion does not classify success versus failure')
banner = body('CollectionStatusBanner')
need('Collecting information…' not in banner, 'collecting chip still contains Collecting information text')
need('R.drawable.ic_action_update' in banner, 'collecting icon was removed instead of retaining icon-only status')
need('CollectionStatus.FAILED' in banner and 'R.drawable.ic_close' in banner and '"Failed"' in banner, 'failure banner with X and Failed label missing')
need(close_icon.is_file(), 'failure X drawable missing')
format_page = body('FormatsPage')
ext_page = body('ExtensionsPage')
need(format_page.count('DetailAffordance') >= 1, 'format entries do not expose a Details affordance')
need(ext_page.count('DetailAffordance') >= 2, 'extension runtime/catalog entries do not expose Details affordances')
item_card = body('CapabilityItemCard')
need('CapabilityItemCard(' in format_page and 'then(tvBrowseModifier(shape))' in item_card, 'format detail entry is not TV focus/bring-into-view enabled through the shared capability card')
need(ext_page.count('CapabilityItemCard(') >= 2 and 'then(tvBrowseModifier(shape))' in item_card, 'extension detail entries are not TV focus/bring-into-view enabled through the shared capability card')
dialog = body('ScrollableDetailDialog')
need(dialog, 'shared scrollable detail dialog missing')
need('verticalScroll(scrollState)' in dialog, 'detail dialog content is not vertically scrollable')
need('ScrollBoundaryIndicators(scrollState' in dialog, 'detail dialog scroll-boundary indicators missing')
need('focusGroup()' in dialog, 'detail dialog content is not grouped for D-pad focus traversal')
need(format_page.count('ScrollableDetailDialog(') >= 1, 'format detail does not use shared scrollable dialog')
need(ext_page.count('ScrollableDetailDialog(') >= 2, 'extension details do not use shared scrollable dialog')
lazy = body('VulkanLazyPage')
need('userScrollEnabled = true' in lazy, 'shared lazy pages do not explicitly preserve touch/user scrolling')
need('focusGroup()' in lazy, 'shared lazy page lacks TV focus grouping')
tv = body('tvBrowseModifier')
need('bringIntoViewRequester' in tv and 'onFocusChanged' in tv and 'focusable()' in tv, 'Android TV browse modifier does not focus and bring focused evidence into view')
key_value = body('CapabilityKeyValue')
need('then(tvBrowseModifier(shape))' in key_value, 'modal capability evidence is not an Android TV browse target')
need('private fun ScrollBoundaryIndicators(scrollState: ScrollState' in kt, 'ScrollState boundary-indicator overload missing')
if errors:
    for error in errors:
        print('FAIL ' + error)
    raise SystemExit(1)
print('PASS 0.80.4 detail affordance, modal scrolling, TV focus and collection outcome contract')
