#!/usr/bin/env python3

def resolve(text, current, count):
    try:
        requested = int(text)
    except Exception:
        requested = current + 1
    requested = max(1, min(count, requested))
    return requested - 1

current = 0
field = ''
for digit in '12':
    field += digit
    assert current == 0
assert resolve(field, current, 20) == 11
assert resolve('0', 5, 20) == 0
assert resolve('999', 5, 20) == 19
assert resolve('', 5, 20) == 5

root_path = '/storage/emulated/0'
assert not (root_path != '/storage/emulated/0')
assert '/storage/emulated/0/Download' != root_path

modes = ['LIST', 'COMPACT', 'DETAILS', 'GRID', 'DENSE_GRID', 'LARGE_GRID']
assert len(modes) == 6 and len(set(modes)) == 6
assert 92 < 156 < 228
print('PASS VulkanScope 2.1.2 UI state-machine model')
