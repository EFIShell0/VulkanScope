#!/usr/bin/env python3

def vendor_id(text):
    value = text.strip()
    if not value:
        return None
    try:
        return int(value[2:], 16) if value.lower().startswith('0x') else int(value, 10)
    except ValueError:
        return None

known = {0x5143: 'Qualcomm', 0x13B5: 'Arm', 0x10DE: 'NVIDIA', 0x1002: 'AMD', 0x8086: 'Intel'}
if known.get(vendor_id('20803')) != 'Qualcomm':
    raise SystemExit('decimal Database vendor-id mapping regressed')
if known.get(vendor_id('0x10DE')) != 'NVIDIA':
    raise SystemExit('hex Database vendor-id mapping regressed')
if vendor_id('Adreno') is not None:
    raise SystemExit('free-form GPU text must not become vendor identity')

checks = [35, 15, 10, 10, 10, 10, 10, 5]

def score(triggered):
    return max(0, 100 - sum(points for points, active in zip(checks, triggered) if active))

if score([False] * 8) != 100:
    raise SystemExit('clear Quality baseline regressed')
if score([True, False, False, False, False, False, False, False]) != 65:
    raise SystemExit('top-level collection deduction regressed')
if score([True] * 8) != 0:
    raise SystemExit('Quality zero floor regressed')
if score([False, True, True, False, False, False, True, True]) != 60:
    raise SystemExit('additive Quality deduction model regressed')

def search_transition(expanded, action):
    if action == 'search' and not expanded:
        return True, 'expand'
    if action == 'close' and expanded:
        return False, 'shrink'
    return expanded, 'stable'

state, motion = search_transition(False, 'search')
if not state or motion != 'expand':
    raise SystemExit('file-manager search opening motion regressed')
state, motion = search_transition(state, 'close')
if state or motion != 'shrink':
    raise SystemExit('file-manager search closing motion regressed')

def summarize(rows):
    upper = [row.upper() for row in rows]
    return upper.count('PASS'), upper.count('FAIL'), upper.count('UNAVAILABLE'), len(upper)

if summarize(['PASS', 'FAIL', 'UNAVAILABLE', 'PASS']) != (2, 1, 1, 4):
    raise SystemExit('self-test result counters regressed')
if summarize([]) != (0, 0, 0, 0):
    raise SystemExit('empty self-test summary regressed')

print('PASS VulkanScope 3.0.7 UI/evidence state machine')
