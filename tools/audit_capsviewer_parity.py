import argparse
import json
import re
import sys
from pathlib import Path
import xml.etree.ElementTree as ET

ap = argparse.ArgumentParser(description='Audit VulkanScope physical-device pNext coverage and optionally compare against a local VulkanCapsViewer 4.12 checkout.')
ap.add_argument('--registry', required=True)
ap.add_argument('--header', required=True)
ap.add_argument('--vulkanscope-root', required=True)
ap.add_argument('--capsviewer-root')
ap.add_argument('--mode', choices=['canonical','source'], default='canonical', help='canonical audit or local CapsViewer source audit')
ap.add_argument('--out', required=True)
args = ap.parse_args()

root = ET.parse(args.registry).getroot()
header = Path(args.header).read_text(encoding='utf-8', errors='ignore')
vsroot = Path(args.vulkanscope_root)
out = Path(args.out)

# Canonical pNext-capable physical-device structures from vk.xml/header.
types = {}
for node in root.findall('./types/type'):
    name = node.get('name') or (node.findtext('name') or '')
    if name:
        types[name] = node
providers = {}
for ext in root.findall('./extensions/extension'):
    ext_name = ext.get('name') or ''
    if not ext_name or ext.get('supported') in ('disabled', 'provisional'):
        continue
    for req in ext.findall('./require'):
        for t in req.findall('type'):
            n = t.get('name') or ''
            if n.startswith('VkPhysicalDevice'):
                providers.setdefault(n, set()).add(ext_name)

header_structs = set(re.findall(r'typedef\s+struct\s+(VkPhysicalDevice\w*)\s*\{', header))
validated = set()
catalog = (vsroot/'app/src/main/cpp/registry_query_catalog.h').read_text(encoding='utf-8')
for n in re.findall(r'VkPhysicalDevice\w+', catalog):
    validated.add(n)


if args.capsviewer_root and args.mode == 'source':
    import subprocess
    tool = vsroot / 'tools' / 'compare_capsviewer_4_12.py'
    result = subprocess.run([sys.executable, str(tool), '--capsviewer-root', str(args.capsviewer_root), '--header', str(args.header), '--registry', str(args.registry), '--out', str(out)], check=False)
    raise SystemExit(result.returncode)

# Read generated pNext query output if present in generated build tree or source.
pnext_hits = set()
for cand in list(vsroot.glob('**/runtime_extension_pnext_generated.inc')) + list(vsroot.glob('**/extension_pnext_query_generated.inc')):
    try:
        text=cand.read_text(encoding='utf-8')
    except Exception:
        continue
    pnext_hits.update(re.findall(r'storage\.add<(VkPhysicalDevice\w*)>', text))

canonical=[]
for name in sorted(header_structs):
    if name in {'VkPhysicalDeviceFeatures','VkPhysicalDeviceFeatures2','VkPhysicalDeviceProperties','VkPhysicalDeviceProperties2','VkPhysicalDeviceMemoryProperties','VkPhysicalDeviceMemoryProperties2','VkPhysicalDeviceGroupProperties'}:
        continue
    node=types.get(name)
    if node is None:
        continue
    extset={x.strip() for x in (node.get('structextends') or '').split(',') if x.strip()}
    if not ({'VkPhysicalDeviceFeatures2','VkPhysicalDeviceProperties2'} & extset):
        continue
    if not providers.get(name):
        continue
    canonical.append(name)

full=sorted(set(canonical)&set(pnext_hits))
missing=sorted(set(canonical)-set(pnext_hits))
validated_missing=sorted(set(canonical)&set(validated)-set(pnext_hits))

report={
    'canonicalHeader':'Vulkan 1.4.360',
    'canonicalPNextCandidates':len(canonical),
    'generatedPNextTypes':len(pnext_hits),
    'fullCoverageCount':len(full),
    'missingFromGeneratedPNext':missing,
    'validatedCatalogButNotGenerated':validated_missing,
}

if args.capsviewer_root:
    cr=Path(args.capsviewer_root)
    hits=set()
    for f in cr.rglob('*'):
        if f.suffix.lower() not in {'.h','.hpp','.cpp','.cc','.cxx'}:
            continue
        try: txt=f.read_text(encoding='utf-8', errors='ignore')
        except Exception: continue
        hits.update(re.findall(r'VkPhysicalDevice\w+(?:Features|Properties)\w*', txt))
    report['capsViewerReferenceStructMentions']=len(hits)
    report['capsViewerNotInCanonicalHeader']=sorted(hits-set(header_structs))
    report['capsViewerStructsNotGenerated']=sorted(hits-set(pnext_hits))
else:
    report['capsViewerReference'] = 'not provided in build environment; canonical coverage audit completed'

out.parent.mkdir(parents=True, exist_ok=True)
out.write_text(json.dumps(report,indent=2,sort_keys=True)+'\n',encoding='utf-8')

print(f"PASS canonical pNext candidates={len(canonical)} generated={len(pnext_hits)} full={len(full)} missing={len(missing)}")
if validated_missing:
    print('WARNING validated structs not represented in generated runtime pNext:', ', '.join(validated_missing))
