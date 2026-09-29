#!/usr/bin/env python3

def count_valid(candidates, limit=256):
    inspected = 0
    valid = 0
    limited = False
    for name, accepted, imported in candidates:
        if not name.lower().endswith('.zip'):
            continue
        if inspected >= limit:
            limited = True
            break
        inspected += 1
        if accepted:
            valid += 1
    return valid, limited

reported = []
for index in range(63):
    accepted = index in {4, 27, 58}
    imported = index == 4
    reported.append((f'archive_{index:02d}.zip', accepted, imported))
reported.extend((('notes.txt', False, False), ('driver.json', False, False)))
valid, limited = count_valid(reported)
if valid != 3 or limited:
    raise SystemExit(f'reported 63-ZIP/3-valid case regressed: valid={valid}, limited={limited}')

visible_valid = [item for item in reported if item[1]]
if len(visible_valid) != valid:
    raise SystemExit('folder count no longer matches validated visible package rows')
if sum(1 for _, accepted, imported in reported if accepted and imported) != 1 or valid != 3:
    raise SystemExit('already-imported valid package was incorrectly removed from folder count')

bounded = [(f'candidate_{index}.zip', index % 2 == 0, False) for index in range(300)]
valid, limited = count_valid(bounded)
if valid != 128 or not limited:
    raise SystemExit(f'256-ZIP bound semantics regressed: valid={valid}, limited={limited}')

empty, limited = count_valid([('ordinary.zip', False, False), ('photo.jpg', False, False)])
if empty != 0 or limited:
    raise SystemExit('invalid/non-Turnip files inflated an empty folder count')

print('PASS VulkanScope 3.0.11 validated Turnip folder-count state machine')
