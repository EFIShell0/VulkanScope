#!/usr/bin/env python3
import shutil, subprocess, sys, tempfile
from pathlib import Path
root=Path(__file__).resolve().parents[1]
verifier=root/'tools/verify_profile_requirements_04144.py'
rels=[Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'),Path('app/build.gradle.kts'),Path('registry/profiles/profile_requirements_lock.json'),Path('tools/test_profile_evaluator_state_machine.py')]
def build():
    t=tempfile.TemporaryDirectory(prefix='vulkanscope-04144-negative-'); b=Path(t.name)
    for rel in rels:
        (b/rel).parent.mkdir(parents=True,exist_ok=True); shutil.copy2(root/rel,b/rel)
    return t,b
def run(b): return subprocess.run([sys.executable,str(verifier),'--root',str(b)],stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True)
def mutate(old,new,label):
    t,b=build()
    try:
        p=b/rels[0]; s=p.read_text(encoding='utf-8')
        if old not in s: raise SystemExit(f'FAIL mutation source missing: {label}')
        p.write_text(s.replace(old,new,1),encoding='utf-8')
        if run(b).returncode==0: raise SystemExit(f'FAIL mutation was not rejected: {label}')
    finally: t.cleanup()
mutate('"subgroupSupportedOperations" to "0x3F"','"subgroupSupportedOperations" to "0xBF"','android15-subgroup-mask')
mutate('hasUnknown || !requirements.completeCoverage -> "UNKNOWN"','hasUnknown -> "UNKNOWN"','complete-coverage-pass-gate')
mutate('roadmap2024BaseCapability(),\n    roadmap2026Capability()','roadmap2024BaseCapability(),\n    roadmap2024PromotedVulkan14Capability(),\n    roadmap2026Capability()','roadmap2026-overinheritance')
mutate('val results = remember(report, device) { vulkanProfileEvaluations(report, device) }','val results = remember(report, device) { vulkanProfileRequirements() }','profiles-ui-canonical-source')
mutate('ProfileCatalogEntry("VP_LUNARG_minimum_requirements_1_4", "current", "",','ProfileCatalogEntry("VP_LUNARG_minimum_requirements_1_4", "current", "1.4.0",','lunarg-api-only-claim')
t,b=build()
try:
    p=b/rels[0]; s=p.read_text(encoding='utf-8')
    marker='Search profiles…'
    if marker not in s: raise SystemExit('FAIL false-positive source missing')
    p.write_text(s.replace(marker,'Search audited profiles…',1),encoding='utf-8')
    if run(b).returncode!=0: raise SystemExit('FAIL unrelated UI text false-positive control')
finally: t.cleanup()
print('PASS VulkanScope 0.41.44 profile negative mutation gate')
