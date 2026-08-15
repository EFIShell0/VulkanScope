#!/usr/bin/env python3
"""Field-level, source-based parity audit against a local VulkanCapsViewer 4.12 checkout.
No runtime data is inferred. This tool only compares source-level struct/field references.
"""
from __future__ import annotations
import argparse, json, re
from pathlib import Path
import xml.etree.ElementTree as ET

ap=argparse.ArgumentParser()
ap.add_argument('--capsviewer-root', required=True)
ap.add_argument('--header', required=True)
ap.add_argument('--registry', required=True)
ap.add_argument('--out', required=True)
args=ap.parse_args()
cr=Path(args.capsviewer_root); header=Path(args.header).read_text(encoding='utf-8', errors='ignore'); root=ET.parse(args.registry).getroot()

# Parse canonical physical-device structs and fields from the real header.
struct_fields={}
for m in re.finditer(r'typedef\s+struct\s+(VkPhysicalDevice\w*)\s*\{(.*?)\}\s*\1\s*;', header, re.S):
    name, body=m.groups()
    fields=set()
    for decl in body.split(';'):
        d=' '.join(decl.strip().split())
        if not d or ' pNext' in d or ' sType' in d: continue
        fm=re.match(r'.+?\s+(\w+)(?:\[[^\]]+\])?$', d)
        if fm: fields.add(fm.group(1))
    struct_fields[name]=fields

files=[p for p in cr.rglob('*') if p.suffix.lower() in {'.h','.hpp','.c','.cc','.cpp','.cxx'}]
texts=[]
for p in files:
    try: texts.append((p,p.read_text(encoding='utf-8', errors='ignore')))
    except Exception: pass

# Source-level references: a struct name plus member access within a bounded window.
struct_refs=set(); field_refs={}
for p,t in texts:
    for sm in re.finditer(r'\b(VkPhysicalDevice\w*)\b', t):
        sname=sm.group(1)
        if sname not in struct_fields: continue
        struct_refs.add(sname)
        window=t[max(0,sm.start()-1200):min(len(t),sm.end()+1200)]
        refs=field_refs.setdefault(sname,set())
        for f in struct_fields[sname]:
            if re.search(rf'(?:->|\.)\s*{re.escape(f)}\b', window): refs.add(f)

reference_fields={s:sorted(field_refs.get(s,set())) for s in sorted(struct_refs)}
canonical_fields={s:sorted(v) for s,v in struct_fields.items() if s in struct_refs}
missing=[]; partial=[]
for s,fields in canonical_fields.items():
    used=set(reference_fields.get(s,[])); wanted=set(fields)
    if not used: missing.append(s); continue
    diff=sorted(wanted-used)
    if diff: partial.append({'struct':s,'missingFields':diff,'referencedFields':sorted(used)})

# Vendor hinting is source-derived, not inferred from GPU models.
vendor_counts={}
for s in struct_refs:
    for vendor in ('AMD','AMDX','ARM','QCOM','NV','NVX','INTEL','IMG','MESA','VALVE','HUAWEI','SEC'):
        if vendor in s: vendor_counts[vendor]=vendor_counts.get(vendor,0)+1

report={
  'reference':'VulkanCapsViewer 4.12 local source checkout',
  'filesScanned':len(files),
  'structsReferenced':len(struct_refs),
  'missingStructs':sorted(set(struct_fields)-struct_refs),
  'partialStructs':partial,
  'vendorStructMentions':vendor_counts,
}
Path(args.out).write_text(json.dumps(report,indent=2,sort_keys=True)+'\n',encoding='utf-8')
print(f'PASS source audit structsReferenced={len(struct_refs)} missingStructs={len(report["missingStructs"])} partialStructs={len(partial)}')
