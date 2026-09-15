#!/usr/bin/env python3
import argparse, hashlib, json, sys
from pathlib import Path

def fail(message):
    raise AssertionError(message)

def need(text, token, message=None):
    if token not in text:
        fail(message or f"missing {token!r}")

def absent(text, token, message=None):
    if token in text:
        fail(message or f"forbidden {token!r}")

def sha(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()

def block(text, start, end):
    if start not in text:
        fail(f"cannot find block start {start!r}")
    tail = text.split(start, 1)[1]
    if end not in tail:
        fail(f"cannot find block end {end!r}")
    return tail.split(end, 1)[0]

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--root', default='.')
    parser.add_argument('--skip-version', action='store_true')
    args = parser.parse_args()
    root = Path(args.root).resolve()
    toolroot = Path(__file__).resolve().parents[1]
    mainp = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
    buildp = root / 'app/build.gradle.kts'
    rulesp = root / 'rules/PROJECT_RULES.md'
    contractp = root / 'tests/golden/1.3.13_filter_unification_quick_access_contract.json'
    if not contractp.is_file():
        contractp = toolroot / 'tests/golden/1.3.13_filter_unification_quick_access_contract.json'
    for path in (mainp, buildp, rulesp, contractp):
        if not path.is_file():
            fail(f'missing {path}')
    src = mainp.read_text()
    build = buildp.read_text()
    rules = rulesp.read_text()
    contract = json.loads(contractp.read_text())
    if not args.skip_version:
        need(build, 'versionCode = 1400')
        need(build, 'versionName = "1.4.0"')
        need(rules, '## Release 1.4.0 filter unification and Quick Access icon refinement')
    single = block(src, 'private fun ExpressiveSingleFilterSelector(', 'private fun ExpressiveFilterBar(')
    need(single, '.clickable(enabled = enabled, role = Role.Button) { expanded = !expanded }', 'single-filter arrow must toggle open and close')
    need(single, 'modifier = Modifier.fillMaxWidth(),\n                                    labelText = "Search filters"', 'single-filter search must use full width')
    need(single, 'enter = fadeIn(tween(220)) + scaleIn(tween(240), initialScale = 0.97f', 'single-filter motion missing')
    absent(single, 'painterResource(R.drawable.ic_close)', 'single-filter X action must be removed')
    absent(single, 'closeShape', 'single-filter close-button container must be removed')
    multi = block(src, 'private fun ExpressiveMultiFilterBar(', 'private fun ExpressiveToggleRow(')
    for token in (
        'var expanded by rememberSaveable { mutableStateOf(false) }',
        '.clickable(role = Role.Button) { expanded = !expanded }',
        'ExpressiveSearchField(',
        'Checkbox(',
        'AnimatedVisibility(',
        'ExpressiveScrollHints(listState',
    ):
        need(multi, token, 'new multi-filter selector contract drift: ' + token)
    absent(multi, 'ExpressiveFilterCarousel(', 'legacy multi-filter carousel restored')
    need(src, 'ExpressiveMultiFilterBar(FORMAT_USAGE_FILTERS.keys.toList(), usageFilters)', 'Format explorer must use unified multi-filter selector')
    for token in ('ExpressiveFilterCarousel(', 'private fun ExpressiveFilterChip(', 'FilterChip('):
        absent(src, token, 'legacy filter implementation remains: ' + token)
    need(src, 'title.contains("quick access", true) -> R.drawable.ic_quick_access_grid', 'Quick access section icon mapping missing')
    icon = root / contract['quick_access_icon_relative']
    if not icon.is_file():
        fail('Quick access 3x3 icon asset missing')
    if sha(icon) != contract['quick_access_icon_sha256']:
        fail('Quick access 3x3 icon asset drift')
    icon_text = icon.read_text()
    need(icon_text, 'android:viewportWidth="24"')
    if icon_text.count('a2,2 0,1 0,0,4') < 9:
        fail('Quick access icon must retain 3x3 nine-dot geometry')
    for rel, expected in contract['immutable_app_src_main_sha256'].items():
        path = root / rel
        if not path.is_file() or sha(path) != expected:
            fail('unrelated app/src/main drift: ' + rel)
    print('release_1400 verifier: PASS')

if __name__ == '__main__':
    try:
        main()
    except Exception as exc:
        print('release_1400 verifier: FAIL:', exc, file=sys.stderr)
        sys.exit(1)
