#!/usr/bin/env python3
from dataclasses import dataclass

@dataclass(frozen=True)
class Candidate:
    file: str

def import_selected(candidates, selected):
    match=next((c for c in candidates if c.file == selected), None)
    return None if match is None else match.file

items=[Candidate('/a/VulkanScope-gpu-analysis.json'), Candidate('/b/VulkanScope-profile-minimum.json')]
assert import_selected(items, items[0].file) == items[0].file
assert import_selected(items, '/missing') is None
assert isinstance(import_selected(items, items[1].file), str)
intro={'direct_updates_enabled':False,'first_install':True,'seen':False}
show=(not intro['direct_updates_enabled']) and intro['first_install'] and not intro['seen']
assert show
assert not ((True is intro['direct_updates_enabled']) and show)
print('PASS VulkanScope 1.0.14 fallback callback / first-install intro state machine')
