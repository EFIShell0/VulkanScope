#!/usr/bin/env python3
import argparse,re
from pathlib import Path
p=argparse.ArgumentParser();p.add_argument('--root');p.add_argument('--skip-version',action='store_true');a=p.parse_args()
root=Path(a.root).resolve() if a.root else Path(__file__).resolve().parents[1]
main=(root/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
ag=(root/'app/build.gradle.kts').read_text(encoding='utf-8')
errors=[]
if not a.skip_version:
    m_name=re.search(r'versionName\s*=\s*"(\d+)\.(\d+)\.(\d+)"',ag)
    m_code=re.search(r'versionCode\s*=\s*(\d+)',ag)
    if not m_name or tuple(map(int,m_name.groups())) < (0,80,9): errors.append('versionName must be 0.80.9 or newer')
    if not m_code or int(m_code.group(1)) < 809: errors.append('versionCode must be 809 or newer')
if 'private fun DetailAffordance(onClick: () -> Unit)' not in main: errors.append('Details affordance is not an explicit button action')
for bad,label in [
    ('Card(onClick = { selected = format }','format card still opens detail'),
    ('Card(onClick = { selectedSupported = extension }','supported extension card still opens detail'),
    ('Card(onClick = { selectedCatalog = name }','catalog extension card still opens detail')]:
    if bad in main: errors.append(label)
for token,label in [
    ('DetailAffordance { selected = format }','format Details button action missing'),
    ('DetailAffordance { selectedSupported = extension }','supported extension Details button action missing'),
    ('DetailAffordance { selectedCatalog = name }','catalog extension Details button action missing'),
    ('Use Details for full decoded/raw format evidence.','format helper does not describe button-only interaction')]:
    if token not in main: errors.append(label)
if errors: raise SystemExit('FAIL 0.80.9 detail-button-only contract\n- '+'\n- '.join(errors))
print('PASS 0.80.9 detail-button-only contract')
