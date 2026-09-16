#!/usr/bin/env python3
import argparse, hashlib, json, sys
from pathlib import Path

def fail(message): raise AssertionError(message)
def need(text, token, message=None):
    if token not in text: fail(message or f"missing {token!r}")
def absent(text, token, message=None):
    if token in text: fail(message or f"forbidden {token!r}")
def sha(path): return hashlib.sha256(path.read_bytes()).hexdigest()
def block(text, start, end):
    if start not in text: fail(f"cannot find block start {start!r}")
    tail=text.split(start,1)[1]
    if end not in tail: fail(f"cannot find block end {end!r}")
    return tail.split(end,1)[0]

def main():
    ap=argparse.ArgumentParser(); ap.add_argument('--root',default='.'); ap.add_argument('--skip-version',action='store_true'); args=ap.parse_args()
    root=Path(args.root).resolve(); toolroot=Path(__file__).resolve().parents[1]
    mainp=root/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'; buildp=root/'app/build.gradle.kts'; rulesp=root/'rules/PROJECT_RULES.md'
    contractp=root/'tests/golden/1.4.1_filter_landscape_pagination_contract.json'
    if not contractp.is_file(): contractp=toolroot/'tests/golden/1.4.1_filter_landscape_pagination_contract.json'
    for p in (mainp,buildp,rulesp,contractp):
        if not p.is_file(): fail(f'missing {p}')
    src=mainp.read_text(); build=buildp.read_text(); rules=rulesp.read_text(); contract=json.loads(contractp.read_text())
    if not args.skip_version:
        need(build,'versionCode = 1402'); need(build,'versionName = "1.4.2"')
        need(rules,'## Release 1.4.2 landscape filter sizing and compact centered pagination')
    single=block(src,'private fun ExpressiveSingleFilterSelector(','private fun ExpressiveFilterBar(')
    for token in [
        'val configuration = androidx.compose.ui.platform.LocalConfiguration.current',
        'val usableHeight = (screenHeight - imeHeight).coerceAtLeast(220.dp)',
        'val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE',
        '((usableHeight.value * 0.62f).dp).coerceIn(220.dp, 440.dp)',
        '(usableHeight - 96.dp).coerceIn(240.dp, 620.dp)',
        'size.coerceIn(1, if (landscape) 4 else 7)',
        'Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center)',
        'modifier = Modifier.width(72.dp).onFocusChanged',
        'textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center)',
        'candidate.isEmpty() -> pageField = value',
        'requested != null && requested in 1..pageCount',
        'Box(Modifier.fillMaxSize().nestedScroll(boundaryScrollConnection))'
    ]: need(single,token,'1.4.2 single-filter contract drift: '+token)
    absent(single,'modifier = Modifier.width(96.dp).onFocusChanged','oversized 96 dp page field restored')
    absent(single,'Text("/ $pageCount", color = VulkanTextSecondary, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))','weighted page-count label would spread pagination to the edges')
    multi=block(src,'private fun ExpressiveMultiFilterBar(','private fun ExpressiveToggleRow(')
    need(multi,'.heightIn(min = 56.dp, max = 420.dp).nestedScroll(boundaryScrollConnection)','1.4.1 multi-filter scroll containment regressed')
    turn=block(src,'private fun TurnipDriverManagerTable(','private fun turnipDriverStateLabel(')
    if turn.count('ExpressiveInfoPill("Vulkan® library", info.libraryName ?: "Not available"') < 2: fail('1.4.1 Turnip library metadata regressed')
    if turn.count('ExpressiveInfoPill("Description", info.description?.takeIf { it.isNotBlank() } ?: "Not provided"') < 2: fail('1.4.1 Turnip description metadata regressed')
    if (root/'screenshots').exists(): fail('top-level screenshots directory must not be packaged from 1.4.2 onward')
    files=root/'files.txt'
    if files.is_file() and any(line.strip().startswith('screenshots/') for line in files.read_text().splitlines()): fail('screenshots entries remain in files.txt')
    for rel,expected in contract['immutable_app_src_main_sha256'].items():
        p=root/rel
        if not p.is_file() or sha(p)!=expected: fail('unrelated app/src/main drift: '+rel)
    print('release_1402 verifier: PASS')

if __name__=='__main__':
    try: main()
    except Exception as exc:
        print('release_1402 verifier: FAIL:',exc,file=sys.stderr); sys.exit(1)
