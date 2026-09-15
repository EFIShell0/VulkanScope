#!/usr/bin/env python3

def permission_flow(has_access, returned_access):
    if has_access:
        return 'OPEN'
    state = 'SETTINGS'
    state = 'OPEN' if returned_access else 'DENIED_3000MS'
    return state

assert permission_flow(True, False) == 'OPEN'
assert permission_flow(False, True) == 'OPEN'
assert permission_flow(False, False) == 'DENIED_3000MS'

def toggle(selected, path, limit):
    selected = set(selected)
    if path in selected:
        selected.remove(path)
        return selected, None
    if len(selected) >= limit:
        return selected, 'LIMIT'
    selected.add(path)
    return selected, None

selected = set()
for p in ['a.zip', 'b.zip', 'c.zip']:
    selected, error = toggle(selected, p, 3)
    assert error is None
assert len(selected) == 3
selected2, error = toggle(selected, 'd.zip', 3)
assert selected2 == selected and error == 'LIMIT'
selected, error = toggle(selected, 'b.zip', 3)
assert selected == {'a.zip', 'c.zip'} and error is None
selected, error = toggle(selected, 'd.zip', 3)
assert selected == {'a.zip', 'c.zip', 'd.zip'} and error is None

def parent_within(root, current):
    root = root.rstrip('/')
    if current == root:
        return root
    parent = current.rsplit('/', 1)[0] or '/'
    return parent if parent == root or parent.startswith(root + '/') else root

assert parent_within('/storage/emulated/0', '/storage/emulated/0') == '/storage/emulated/0'
assert parent_within('/storage/emulated/0', '/storage/emulated/0/Download') == '/storage/emulated/0'
assert parent_within('/storage/emulated/0', '/storage/emulated/0/Download/Sub') == '/storage/emulated/0/Download'

print('PASS VulkanScope 1.3.4 file-manager state machine')
