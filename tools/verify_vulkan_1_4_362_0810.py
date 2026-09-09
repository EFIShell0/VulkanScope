#!/usr/bin/env python3
import argparse, hashlib, json, re, xml.etree.ElementTree as ET
from pathlib import Path

ap=argparse.ArgumentParser(); ap.add_argument('--root',default='.'); ap.add_argument('--skip-version',action='store_true'); args=ap.parse_args()
root=Path(args.root).resolve(); errors=[]
def need(cond,msg):
    if not cond: errors.append(msg)
def text(rel): return (root/rel).read_text(encoding='utf-8',errors='ignore')

gradle=text('app/build.gradle.kts')
if not args.skip_version:
    need('versionCode = 810' in gradle and 'versionName = "0.80.10"' in gradle,'release identity is not 0.80.10/810')
lock=json.loads(text('registry/registry_lock.json')); reg_path=root/lock.get('bundledRegistryPath','registry/upstream/vk.xml'); data=reg_path.read_bytes(); sha=hashlib.sha256(data).hexdigest()
need(lock.get('apiBaseline')=='Vulkan 1.4.362','registry lock baseline is not Vulkan 1.4.362')
need(lock.get('registryRef')=='1.4.362','registry ref is not 1.4.362')
need(lock.get('registrySha256')=='cf31c965cf6e788697139601da0c7e02a75a9b6c7ac764e7641f5521ffd9da06'==sha,'registry SHA-256 does not match uploaded 1.4.362 vk.xml')
need(lock.get('headerCommit')=='ee2ec5fd83dafce291024683b50dc89219333076','Vulkan-Headers 1.4.362 commit is not pinned')
need(lock.get('headerTag')=='v1.4.362' and lock.get('headerVersion')==362,'Vulkan-Headers tag/version is not 1.4.362/362')
reg=ET.fromstring(data)
hdr=[]
for t in reg.findall("./types/type[@category='define']"):
    if t.findtext('name')=='VK_HEADER_VERSION' and t.get('api')=='vulkan,vulkanbase':
        m=re.search(r'VK_HEADER_VERSION\s+(\d+)',''.join(t.itertext()));
        if m: hdr.append(int(m.group(1)))
need(hdr==[362],f'vk.xml Vulkan header version is {hdr!r}, expected [362]')

types={}
for n in reg.findall('./types/type'):
    name=n.get('name') or n.findtext('name')
    if name: types[name]=n
def extends(name):
    seen=set()
    while name and name in types and name not in seen:
        seen.add(name); n=types[name]
        vals={x.strip() for x in (n.get('structextends') or '').split(',') if x.strip()}
        if vals:return vals
        name=n.get('alias')
    return set()
queryable=set(); provisional=set()
for ext in reg.findall('./extensions/extension'):
    name=ext.get('name') or ''; supported={x.strip() for x in (ext.get('supported') or '').split(',') if x.strip()}
    if 'vulkan' not in supported or ext.get('type')!='device' or (ext.get('platform') or '') not in {'','android','provisional'}: continue
    if any((tn.get('name') or '').startswith('VkPhysicalDevice') and {'VkPhysicalDeviceFeatures2','VkPhysicalDeviceProperties2'} & extends(tn.get('name') or '') for req in ext.findall('require') for tn in req.findall('type')):
        queryable.add(name)
        if ext.get('provisional','').lower()=='true': provisional.add(name)
need(len(queryable)==304,f'1.4.362 queryable provider census is {len(queryable)}, expected 304')
need(len(provisional)==5,f'provisional provider census is {len(provisional)}, expected 5')
need({'VK_KHR_pipeline_library_group_handles','VK_VALVE_buffer_device_address_allocation_alignment'} <= queryable,'1.4.362 new queryable extensions are missing from registry model')
coverage=text('app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt')
m=re.search(r'VALIDATED_PHYSICAL_DEVICE_QUERY_EXTENSIONS\s*=\s*setOf\((.*?)\n\)',coverage,re.S)
validated=set(re.findall(r'"(VK_[A-Za-z0-9_]+)"',m.group(1) if m else ''))
need(validated==queryable,f'validated extension coverage drift: validated={len(validated)} registry={len(queryable)} missing={sorted(queryable-validated)} extra={sorted(validated-queryable)}')

cmake=text('app/src/main/cpp/CMakeLists.txt'); need('ee2ec5fd83dafce291024683b50dc89219333076' in cmake,'CMake Vulkan-Headers 1.4.362 exact commit pin missing')
catalog=text('app/src/main/cpp/registry_query_catalog.h'); need('kBaseline = "Vulkan 1.4.362"' in catalog and 'kHeaderBaseline = "Vulkan 1.4.362 compile headers; validated query catalog Vulkan 1.4.362"' in catalog,'native query catalog baseline not 1.4.362')
need('kImplementedPhysicalDeviceStructCount = 110' in catalog and 'kValidatedRuntimeQueryGroupCount = 104' in catalog,'explicit runtime catalog 110/104 contract drifted')
cpp=text('app/src/main/cpp/vulkanscope.cpp'); need(cpp.count('vulkanRegistryVersion\\\":\\\"1.4.362')>=3,'native report provenance is not fully 1.4.362')
pnext=text('app/src/main/cpp/runtime_extension_pnext_parity.inc')
need('VK_KHR_pipeline_library_group_handles' in pnext and 'VK_EXT_pipeline_library_group_handles' in pnext,'KHR/EXT pipeline-library-group-handles alias coverage missing')
need('VkPhysicalDeviceBufferDeviceAddressAllocationAlignmentFeaturesVALVE' in pnext and 'VkPhysicalDeviceBufferDeviceAddressAllocationAlignmentPropertiesVALVE' in pnext,'VALVE allocation-alignment pNext coverage missing')
fields=text('app/src/main/cpp/extension_field_coverage_parity.inc')
need('bufferDeviceAddressAllocationAlignment' in fields and 'maxBufferDeviceAddressAllocationAlignment' in fields,'VALVE allocation-alignment fields are not serialized')
gen=text('tools/generate_extension_pnext_query.py'); need('resolve_alias' in gen or 'canonical_type_name' in gen,'runtime pNext generator does not resolve registry type aliases')
field_gen=text('tools/generate_extension_field_coverage.py'); need('!= 362' in field_gen and '1.4.362' in field_gen,'field serializer generator does not require header 362')
manifest=json.loads(text('registry/generated/registry_query_manifest.json')); need(manifest.get('baseline')=='Vulkan 1.4.362' and manifest.get('validatedPhysicalDeviceQueryExtensionCount')==304,'registry manifest baseline/provider count mismatch')
snapshot=json.loads(text('registry/generated/coverage_snapshot.json')); need(snapshot.get('baseline')=='Vulkan 1.4.362' and snapshot.get('validatedPhysicalDeviceQueryExtensionCount')==304,'coverage snapshot baseline/provider count mismatch')
resource=json.loads(text('registry/generated/resource_budget.json')); need(resource.get('registeredVulkanExtensionCensus')==476,'resource budget registered extension census is not 476')
extref=json.loads(text('registry/generated/extension_reference.json')); need(extref.get('baseline')=='Vulkan 1.4.362' and len(extref.get('entries',[]))==476,'extension reference is not 1.4.362 / 476 extensions')
sym=text('app/src/main/java/com/efishell/vulkanscope/VulkanSymbolIndex.kt');
for needle in ['VULKAN_COMMAND_SYMBOL_COUNT = 842','VULKAN_TOKEN_SYMBOL_COUNT = 6248','VULKAN_TYPE_SYMBOL_COUNT = 2461']:
    need(needle in sym,f'Encyclopedia symbol census missing {needle}')
main=text('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'); need('1.4.362' in main,'UI does not expose Vulkan 1.4.362 registry/header identity')
setup=text('DATABASE_SETUP.md')
if not args.skip_version:
    need('0.39.24' in setup,'Database companion is not 0.39.24')
else:
    need('Database 1.0.8' in setup,'current Database companion is not 1.0.8')
if errors:
    print('FAIL VulkanScope 0.80.10 / Vulkan 1.4.362 contract')
    for e in errors: print('-',e)
    raise SystemExit(1)
print('PASS VulkanScope 0.80.10 / Vulkan 1.4.362 contract: providers=304 stable=299 provisional=5 explicitStructs=110 queryGroups=104 symbols=842/6248/2461 extensions=476')
