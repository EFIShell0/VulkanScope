#!/usr/bin/env python3
import shutil, subprocess, tempfile
from pathlib import Path
root=Path(__file__).resolve().parents[1]
verifier=root/'tools/verify_advanced_analysis_1000.py'

def run(candidate):
    return subprocess.run(['python',str(verifier),'--root',str(candidate)],stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True).returncode

def mutate(path,old,new,label):
    with tempfile.TemporaryDirectory() as tmp:
        candidate=Path(tmp)/'root'; shutil.copytree(root,candidate)
        target=candidate/path; text=target.read_text(encoding='utf-8')
        if old not in text: raise SystemExit('mutation source missing: '+label)
        target.write_text(text.replace(old,new),encoding='utf-8')
        if run(candidate)==0: raise SystemExit('negative mutation passed unexpectedly: '+label)

main='app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
advanced='app/src/main/java/com/efishell/vulkanscope/AdvancedAnalysis.kt'
mutate(main,'EvidenceInspectorDialog(','RemovedInspector(','remove provenance inspector')
mutate(main,'"Global Vulkan report search"','"Removed global search"','remove global report search')
mutate(main,'"Run guided System ↔ Turnip A/B"','"Removed A/B workflow"','remove guided A/B')
mutate(main,'timingStartNanos','removedTimingStartNanos','remove measured query timing')
mutate(main,'"Capability requirement resolver"','"Removed resolver"','remove requirement resolver')
mutate(main,'FORMAT_USAGE_FILTERS','REMOVED_USAGE_TABLE','remove Format Explorer usage filters')
mutate(main,'"Matrix"','"RemovedMatrix"','remove Vulkan Video matrix')
mutate(main,'"Surface + Display presentation evidence"','"Removed presentation"','remove presentation analysis')
mutate(main,'"VulkanScopeMinimumProfile1"','"RemovedMinimumProfile1"','remove custom minimum schema')
raw_title='"Raw structured technical Report"' if '"Raw structured technical Report"' in (root/main).read_text(encoding='utf-8') else '"Raw structured technicalReport"'
mutate(main,raw_title,'"Removed raw report"','remove raw technical report')
mutate(main,'addPathSegments("v1/reports/$id").addQueryParameter("compact", "1")','addPathSegments("unsafe/$id")','remove fixed Database route')
mutate(advanced,'ANALYSIS_HISTORY_MAX_ITEMS = 8','ANALYSIS_HISTORY_MAX_ITEMS = 800','remove history bound')
mutate(main,'"Add to watched evidence"','"Removed watch action"','remove per-field watch action')
mutate(main,'ExpressiveEvidenceRow','RemovedEvidenceRow','remove shared evidence primitive')
mutate(advanced,'Registry/reference presence never proves selected-device runtime support','Registry always proves runtime support','break registry/runtime boundary')
current_gradle=(root/'app/build.gradle.kts').read_text(encoding='utf-8')
import re
match=re.search(r'versionCode\s*=\s*(\d+)', current_gradle)
if not match: raise SystemExit('mutation source missing: stale versionCode')
current_code=match.group(1)
mutate('app/build.gradle.kts',f'versionCode = {current_code}','versionCode = 999','stale versionCode')
with tempfile.TemporaryDirectory() as tmp:
    candidate=Path(tmp)/'root'; shutil.copytree(root,candidate)
    p=candidate/'changelog.md'; p.write_text(p.read_text(encoding='utf-8')+'\nUnrelated wording false-positive control.\n',encoding='utf-8')
    if run(candidate)!=0: raise SystemExit('false-positive control failed')
print('PASS 1.0.0 negative mutations and false-positive control')
