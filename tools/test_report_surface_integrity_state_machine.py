#!/usr/bin/env python3
from dataclasses import dataclass


def raw_bytes(values):
    data = bytes(values)
    return f'type=raw; bytes={len(data)}; 0x{data.hex()}'


def values_equivalent(left, right):
    if left == right:
        return True
    if left and right.startswith(left + ' (0x'):
        return True
    if right and left.startswith(right + ' (0x'):
        return True
    return False


def merge_properties(entries, generated, generic):
    out = [dict(x) for x in entries]
    for field in generated:
        duplicate = False
        for existing in out:
            if existing['section'] == field['section'] and existing['name'] == field['name'] and values_equivalent(existing['value'], field['value']):
                duplicate = True
                break
            if existing['section'] == generic and existing['name'] == field['name'] and values_equivalent(existing['value'], field['value']):
                existing['section'] = field['section']
                duplicate = True
                break
        if not duplicate:
            out.append(dict(field))
    return out


@dataclass
class SurfaceState:
    current: str | None = None
    generation: int = 0
    pending: bool = False
    full_recollection: bool = False

    def ready(self, surface):
        if self.current != surface:
            self.current = surface
            self.generation += 1
            self.pending = True

    def destroyed(self, surface):
        if self.current == surface:
            self.current = None
            self.generation += 1
            self.pending = False


def bounded_surface_probe(results):
    calls = 0
    last = None
    for result in results[:2]:
        calls += 1
        last = result
        if 'VkResult=-1000000001' not in result:
            break
    return calls, last


uuid = list(range(16))
expected = 'type=raw; bytes=16; 0x000102030405060708090a0b0c0d0e0f'
if raw_bytes(uuid) != expected:
    raise SystemExit('FAIL raw UUID serialization state')
manual = [{'section': 'Extension · VK_EXT_example', 'name': 'flag', 'value': '1'}]
generated = [
    {'section': 'VkPhysicalDeviceExamplePropertiesEXT', 'name': 'flag', 'value': '1 (0x1)'},
    {'section': 'VkPhysicalDeviceAliasPropertiesKHR', 'name': 'flag', 'value': '1 (0x1)'}
]
merged = merge_properties(manual, generated, 'Extension · VK_EXT_example')
if [x['section'] for x in merged] != ['VkPhysicalDeviceExamplePropertiesEXT', 'VkPhysicalDeviceAliasPropertiesKHR']:
    raise SystemExit('FAIL property provenance state')
state = SurfaceState()
state.ready('A')
token_a = state.generation
state.ready('B')
token_b = state.generation
state.destroyed('A')
if state.current != 'B' or state.generation != token_b or not state.pending:
    raise SystemExit('FAIL stale Surface destruction state')
if token_a == token_b:
    raise SystemExit('FAIL Surface generation state')
calls, last = bounded_surface_probe(['Unavailable: VkResult=-1000000001', 'Available'])
if calls != 2 or last != 'Available':
    raise SystemExit('FAIL bounded Surface retry success state')
calls, last = bounded_surface_probe(['Unavailable: VkResult=-1000000001', 'Unavailable: VkResult=-1000000001', 'Available'])
if calls != 2 or 'VkResult=-1000000001' not in last:
    raise SystemExit('FAIL bounded Surface retry limit state')
calls, last = bounded_surface_probe(['Available'])
if calls != 1 or last != 'Available':
    raise SystemExit('FAIL Surface false-positive retry state')
print('PASS VulkanScope 0.41.41 report/surface integrity state machine')
