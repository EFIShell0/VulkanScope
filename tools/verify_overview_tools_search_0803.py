#!/usr/bin/env python3
import argparse,re,sys
from pathlib import Path
p=argparse.ArgumentParser(); p.add_argument('--root',default=str(Path(__file__).resolve().parents[1])); a=p.parse_args(); root=Path(a.root)
main=root/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'; gradle=root/'app/build.gradle.kts'; index=root/'app/src/main/java/com/efishell/vulkanscope/VulkanSymbolIndex.kt'; gen=root/'tools/generate_encyclopedia_symbols.py'
errs=[]
def req(c,m):
    if not c: errs.append(m)
kt=main.read_text(); gd=gradle.read_text(); ix=index.read_text(); gg=gen.read_text()
def body(name):
    m=re.search(r'private fun (?:[A-Za-z0-9_<>?.]+\.)?'+re.escape(name)+r'\s*\(',kt)
    if not m:return ''
    b=kt.find('{',m.start()); d=0; ins=False; esc=False
    for i in range(b,len(kt)):
        ch=kt[i]
        if ins:
            if esc: esc=False
            elif ch=='\\': esc=True
            elif ch=='"': ins=False
            continue
        if ch=='"': ins=True
        elif ch=='{': d+=1
        elif ch=='}':
            d-=1
            if d==0:return kt[m.start():i+1]
    return ''
vm = re.search(r'versionName\s*=\s*"(\d+)\.(\d+)\.(\d+)"', gd); vc = re.search(r'versionCode\s*=\s*(\d+)', gd)
req(bool(vm and vc and tuple(map(int, vm.groups())) >= (0, 80, 3) and int(vc.group(1)) >= 803), '0.80.3+ compatible identity missing')
req('Encyclopedia("Encyclopedia")' in kt and 'Analysis("Analysis workspace")' in kt,'separate Encyclopedia/Analysis Page destinations missing')
ov=body('OverviewPage')
req(ov,'OverviewPage missing')
req('rememberAnalysisWorkspaceModel(' not in ov and 'analysisWorkspaceItems(' not in ov,'Analysis workspace is still eagerly embedded in Overview')
req('EncyclopediaOverviewCard(' not in ov and 'EncyclopediaPage(' not in ov,'Encyclopedia content is still embedded in Overview')
req('title = "Encyclopedia"' in ov and 'Page.Encyclopedia' in ov,'Overview Encyclopedia entry card missing')
req('title = "Analysis workspace"' in ov and 'Page.Analysis' in ov,'Overview Analysis entry card missing')
enc=body('EncyclopediaPage'); ana=body('AnalysisPage')
req(enc and 'VulkanLazyPage(' in enc,'Encyclopedia is not a separate lazy page')
req('ExpressiveSearchField(' in enc and 'items(entries' in enc,'Encyclopedia page search/results are not lazy list items')
req('entries.forEachIndexed' not in enc,'Encyclopedia result rows are eagerly composed in one card')
req(ana and 'rememberAnalysisWorkspaceModel(' in ana and 'analysisWorkspaceItems(' in ana and 'VulkanLazyPage(' in ana,'Analysis workspace is not a separate lazy page')
pc=body('PageContent')
req('Page.Encyclopedia -> EncyclopediaPage(' in pc,'PageContent Encyclopedia route missing')
req('Page.Analysis -> AnalysisPage(report, device, display, driverMode, turnipSupport, collectionStatus, queryTimingMs, onDriverModeChanged)' in pc,'PageContent Analysis route missing')
req('Page.Encyclopedia, Page.Analysis' in kt or ('Page.Encyclopedia' in kt and 'Page.Analysis' in kt),'Overview-selected mapping for tool pages missing')
first=next((l for l in ix.splitlines() if l.strip().startswith('"vk')), '')
req(bool(first),'generated symbol row missing')
req('\\\\t' not in first,'generated symbol row still double-escapes tab separators and can crash decoder')
req("if (first <= 0 || second <= first + 1) return null" in ix,'malformed generated row decoder is not fail-closed')
req("if (tab <= 0) continue" in ix,'search loop does not skip malformed generated rows')
req("f'    \"{esc(symbol)}\\\\t{esc(owner)}\\\\t{esc(providers)}\",'" in gg,'generator does not emit exactly one Kotlin tab escape between fields')
if errs:
    [print('FAIL '+e) for e in errs]; raise SystemExit(1)
print('PASS 0.80.3 separate Overview tools and crash-safe Encyclopedia search contract')
