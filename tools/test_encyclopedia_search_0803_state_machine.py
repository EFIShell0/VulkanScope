#!/usr/bin/env python3
import ast,re
from pathlib import Path
root=Path(__file__).resolve().parents[1]
text=(root/'app/src/main/java/com/efishell/vulkanscope/VulkanSymbolIndex.kt').read_text()
rows=[]
for line in text.splitlines():
    stripped=line.strip()
    if stripped.startswith('"') and stripped.endswith('",'):
        try: rows.append(ast.literal_eval(stripped[:-1]))
        except Exception: pass
if not rows: raise SystemExit('FAIL no generated rows decoded')

def decode(row):
    first=row.find('\t'); second=row.find('\t',first+1)
    if first<=0 or second<=first+1: return None
    return row[:first],row[first+1:second],row[second+1:]
valid=[decode(r) for r in rows]
if any(x is None for x in valid): raise SystemExit('FAIL generated row is not tab-delimited at runtime')
for target in ['vkCreateInstance','VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2','VkPhysicalDeviceFeatures2']:
    if not any(x[0]==target for x in valid): raise SystemExit('FAIL missing searchable symbol '+target)
for malformed in ['', 'vkCreateInstance', '\towner\tprovider', 'name\t\tprovider', 'name\\towner\\tprovider']:
    if decode(malformed) is not None: raise SystemExit('FAIL malformed row accepted: '+repr(malformed))
print(f'PASS 0.80.3 Encyclopedia search state machine rows={len(valid)} malformed=5')
