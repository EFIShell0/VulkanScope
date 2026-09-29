#!/usr/bin/env python3

class Cancelled(Exception):
    pass

def scan_folder(children, limit=256):
    inspected = 0
    valid = 0
    limited = False
    for kind, accepted in children:
        if kind == 'cancel':
            raise Cancelled()
        if kind == 'error':
            continue
        name = kind
        if not name.lower().endswith('.zip'):
            continue
        if inspected >= limit:
            limited = True
            break
        inspected += 1
        if accepted:
            valid += 1
    return valid, limited

items = [(f'archive_{i:02d}.zip', i in {4, 27, 58}) for i in range(63)]
items += [('notes.txt', False), ('broken-folder-error', False)]
valid, limited = scan_folder(items)
if valid != 3 or limited:
    raise SystemExit(f'63-ZIP/3-valid contract regressed: valid={valid}, limited={limited}')

bounded = [(f'candidate_{i}.zip', i % 2 == 0) for i in range(300)]
valid, limited = scan_folder(bounded)
if valid != 128 or not limited:
    raise SystemExit(f'256-candidate bound regressed: valid={valid}, limited={limited}')

valid, limited = scan_folder([('bad.zip', False), ('ordinary.txt', False), ('error', False), ('good.zip', True)])
if valid != 1 or limited:
    raise SystemExit('ordinary per-folder failures no longer skip safely')

try:
    scan_folder([('good.zip', True), ('cancel', False), ('good2.zip', True)])
except Cancelled:
    pass
else:
    raise SystemExit('cancellation was swallowed')

print('PASS VulkanScope 3.0.12 folder-scan compile/cancellation state machine')
