#!/usr/bin/env python3
import argparse, json, re, subprocess, sys
from pathlib import Path
parser=argparse.ArgumentParser(); parser.add_argument('--root'); args=parser.parse_args()
root=Path(args.root).resolve() if args.root else Path(__file__).resolve().parents[1]
ktp=root/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
lockp=root/'registry/profiles/profile_requirements_lock.json'
gradlep=root/'app/build.gradle.kts'
errors=[]
def need(cond,msg):
    if not cond: errors.append(msg)
if not ktp.is_file(): raise SystemExit('FAIL MainActivity.kt missing')
kt=ktp.read_text(encoding='utf-8')
gradle=gradlep.read_text(encoding='utf-8') if gradlep.is_file() else ''
need(('versionName = "0.41.44"' in gradle and 'versionCode = 454' in gradle) or ('versionName = "0.41.45"' in gradle and 'versionCode = 455' in gradle) or ('versionName = "0.41.46"' in gradle and 'versionCode = 456' in gradle) or ('versionName = "0.80.0"' in gradle and 'versionCode = 800' in gradle) or ('versionName = "0.80.1"' in gradle and 'versionCode = 801' in gradle) or ('versionName = "0.80.2"' in gradle and 'versionCode = 802' in gradle) or ('versionName = "0.80.3"' in gradle and 'versionCode = 803' in gradle),'0.41.44+ compatible version metadata missing')
need(lockp.is_file(),'profile source lock missing')
lock={}
if lockp.is_file():
    try: lock=json.loads(lockp.read_text(encoding='utf-8'))
    except Exception as e: errors.append(f'profile lock invalid JSON: {e}')
need(lock.get('vulkanBaseline')=='1.4.361','profile lock Vulkan baseline mismatch')
need(lock.get('sources',{}).get('roadmap',{}).get('ref')=='v1.4.361','Roadmap source is not immutable-ref pinned')
need(lock.get('sources',{}).get('android',{}).get('ref')=='v1.4.361','Android profile source is not immutable-ref pinned')
need('private data class ProfileFeatureRequirement(val structure: String, val member: String)' in kt,'profile features are not struct-qualified')
need('private enum class ProfilePropertyComparison { MINIMUM, MAXIMUM, EQUAL_BOOL, BITMASK_CONTAINS, VECTOR_MINIMUM }' in kt,'profile property comparison semantics incomplete')
need('formats: List<ProfileFormatRequirement>' in kt,'profile format requirement support missing')
need('requiredProfiles: List<String>' in kt and 'alternativeGroups: List<ProfileAlternativeGroup>' in kt,'profile inheritance/OR support missing')
need('hasUnknown || !requirements.completeCoverage -> "UNKNOWN"' in kt,'incomplete evaluator can become PASS')
need('if (complete) evidence.missingExtensions += extension else evidence.unknownExtensions += extension' in kt,'extension absence does not preserve enumeration certainty')
need('"subgroupSupportedOperations" to "0x3F"' in kt,'Android 15 subgroup operation mask is not authoritative 0x3F')
need('requiredProfiles = listOf("VP_ANDROID_vulkan_profile_2022")' in kt,'Android 15 required-profile inheritance missing')
need('requiredProfiles = listOf("VP_ANDROID_15_requirements")' in kt,'Android 16 required-profile inheritance missing')
need('requiredProfiles = listOf("VP_ANDROID_vulkan_profile_2025")' in kt,'Android 17 required-profile inheritance missing')
need('ProfileAlternativeGroup("primitivesGeneratedQuery OR pipelineStatisticsQuery"' in kt,'Android query OR group missing')
need('ProfileAlternativeGroup("multisampled-to-single-sampled OR shader stencil export"' in kt,'Android multisample/stencil OR group missing')
need('private fun roadmap2026ProfileCapability(): ProfileCapabilityRequirement = mergeProfileCapabilities(\n    roadmap2022Capability(),\n    roadmap2024BaseCapability(),\n    roadmap2026Capability()\n)' in kt,'Roadmap 2026 exact direct composition missing')
road26=kt[kt.find('private fun roadmap2026ProfileCapability'):kt.find('private fun vulkanProfileRequirements')]
need('roadmap2024PromotedVulkan14Capability()' not in road26,'Roadmap 2026 incorrectly inherits Roadmap 2024 promoted-v1.4 capability')
need('roadmapLineAlternatives("VkPhysicalDeviceVulkan14Features")' in kt,'Roadmap 2026 line-rasterization OR group missing')
need('ProfileRequirements("VP_KHR_roadmap_2022", "r.2", "1.3.204"' in kt,'Roadmap 2022 mapping missing')
need('ProfileRequirements("VP_KHR_roadmap_2024", "r.2", "1.3.276"' in kt,'Roadmap 2024 mapping missing')
need('ProfileRequirements("VP_KHR_roadmap_2026", "r.2", "1.4.328"' in kt,'Roadmap 2026 mapping missing')
for name in ['VP_LUNARG_minimum_requirements_1_4','VP_LUNARG_minimum_requirements_1_3','VP_LUNARG_minimum_requirements_1_2','VP_LUNARG_minimum_requirements_1_1','VP_LUNARG_minimum_requirements_1_0']:
    need(re.search(r'ProfileCatalogEntry\("'+re.escape(name)+r'",\s*"current",\s*"",',kt) is not None,f'{name} still presents an unpinned API-only minimum')
need('val results = remember(report, device) { vulkanProfileEvaluations(report, device) }' in kt,'Profiles UI does not use canonical profile evaluation source')
need(kt.count('vulkanProfileEvaluations(report,') >= 6,'profile evaluation is not consumed consistently across UI/report/export paths')
need('vulkanProfileRequirements(report' not in kt,'legacy profile requirement UI path remains')
need('put("name", value.name); put("revision", value.revision)' in kt,'profile catalog JSON uses stale Pair accessors')
need('appendLine("${it.name} | ${it.revision}")' in kt,'profile catalog TXT uses stale Pair accessors')
need('vulkanProfileCatalog().map { it.name to htmlEscape(it.revision) }' in kt,'profile catalog HTML uses stale Pair accessors')
if errors:
    for e in errors: print('FAIL:',e)
    raise SystemExit(1)
sm=root/'tools/test_profile_evaluator_state_machine.py'
if sm.is_file():
    r=subprocess.run([sys.executable,str(sm)],cwd=root,text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
    if r.stdout: print(r.stdout.rstrip())
    if r.returncode: raise SystemExit(r.returncode)
print('PASS VulkanScope 0.41.44 profile requirement source contract')
