#!/usr/bin/env python3
import shutil,subprocess,tempfile
from pathlib import Path
root=Path(__file__).resolve().parents[1]
verifier=root/'tools/verify_full_audit_1001.py'

def run(candidate):
    return subprocess.run(['python',str(verifier),'--root',str(candidate)],stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True).returncode

def mutate(path,old,new,label):
    with tempfile.TemporaryDirectory() as tmp:
        candidate=Path(tmp)/'root'; shutil.copytree(root,candidate)
        target=candidate/path; text=target.read_text(encoding='utf-8')
        if old not in text: raise SystemExit('mutation source missing: '+label)
        target.write_text(text.replace(old,new,1),encoding='utf-8')
        if run(candidate)==0: raise SystemExit('negative mutation passed unexpectedly: '+label)

main='app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'; advanced='app/src/main/java/com/efishell/vulkanscope/AdvancedAnalysis.kt'
mutate(advanced,'import java.math.BigDecimal','import java.math.MathContext','remove exact decimal implementation')
mutate(advanced,'Feature expectation must be true/supported/yes/1 or false/unsupported/no/0','Invalid feature input accepted','remove feature expectation validation')
mutate(advanced,'Feature name is ambiguous','Feature matched first','remove feature ambiguity protection')
mutate(advanced,'AnalysisHistoryRecord? = runCatching {','AnalysisHistoryRecord? {','remove history I/O fail-closed wrapper')
mutate(main,'Unknown · runtime device API evidence is unavailable or unparsable','Runtime API does not expose','restore false-negative core dependency')
mutate(main,'technicalLeavesNeeded = state.tab == 8 || state.tab == 9','technicalLeavesNeeded = true','restore eager technicalReport allocation')
mutate(main,'val resolvedWatch = onWatch ?: environment?.addWatch','val resolvedWatch = environment?.addWatch','break single resolved watch callback')
current_main=(root/main).read_text(encoding='utf-8')
if 'private fun ExpressiveFilterCarousel' in current_main:
    mutate(main,'LazyRow(','Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {','restore unmanaged horizontally hidden filters')
else:
    mutate(main,'FlowRow(Modifier.fillMaxWidth()','Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())','restore horizontally hidden filters')
timing_source = 'The base collector does not publish elapsed time for every Vulkan® query' if 'The base collector does not publish elapsed time for every Vulkan® query' in current_main else 'The base collector does not publish elapsed time for every Vulkan query'
mutate(main,timing_source,'The current collector does not publish per-query durations','restore inaccurate timing text')
current_gradle=(root/'app/build.gradle.kts').read_text(encoding='utf-8')
import re
match=re.search(r'versionCode\s*=\s*(\d+)',current_gradle)
if not match: raise SystemExit('mutation source missing: stale successor versionCode')
current_code=match.group(1)
mutate('app/build.gradle.kts',f'versionCode = {current_code}','versionCode = 1001' if current_code != '1001' else 'versionCode = 1000','stale successor versionCode')
with tempfile.TemporaryDirectory() as tmp:
    candidate=Path(tmp)/'root'; shutil.copytree(root,candidate)
    p=candidate/'changelog.md'; p.write_text(p.read_text(encoding='utf-8')+'\nUnrelated audit false-positive control.\n',encoding='utf-8')
    if run(candidate)!=0: raise SystemExit('false-positive control failed')
print('PASS 1.0.1 negative mutations and false-positive control')
