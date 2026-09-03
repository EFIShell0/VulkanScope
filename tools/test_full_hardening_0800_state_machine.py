#!/usr/bin/env python3
import json

ARCHIVE_LIMIT = 96 * 1024 * 1024

class ArchiveCounter:
    def __init__(self, limit):
        self.limit = limit
        self.total = 0

    def accept(self, amount):
        if amount < 0:
            raise ValueError('negative input')
        self.total += amount
        if self.total > self.limit:
            raise SecurityError('archive input limit')

class SecurityError(Exception):
    pass

def terminal_candidate(payload, base_group):
    try:
        value = json.loads(payload)
    except Exception:
        return False
    if not isinstance(value, dict):
        return False
    if not base_group:
        return True
    return value.get('baseReportComplete') is True or value.get('status') in {'unavailable', 'incomplete', 'not_applicable'}

def polling_materializations(events):
    reads = []
    for event in events:
        if event == 'checkpoint_changed':
            continue
        if event in {'terminal_marker', 'crash_marker', 'timeout_boundary'}:
            reads.append(event)
    return reads

counter = ArchiveCounter(ARCHIVE_LIMIT)
counter.accept(ARCHIVE_LIMIT - 1)
counter.accept(1)
if counter.total != ARCHIVE_LIMIT:
    raise SystemExit('FAIL archive limit rejected an exact-boundary input')
try:
    counter.accept(1)
except SecurityError:
    pass
else:
    raise SystemExit('FAIL archive limit accepted an over-boundary input')
if terminal_candidate('{"baseReportComplete":true,"devices":[]}', True) is not True:
    raise SystemExit('FAIL complete base publication was rejected')
if terminal_candidate('{"status":"unavailable","devices":[]}', True) is not True:
    raise SystemExit('FAIL explicit unavailable base publication was rejected')
if terminal_candidate('{"devices":[]}', True) is not False:
    raise SystemExit('FAIL incomplete base checkpoint was accepted as terminal')
if terminal_candidate('{"group":"limits","devices":[]}', False) is not True:
    raise SystemExit('FAIL complete non-base publication was rejected')
if terminal_candidate('{broken', False) is not False:
    raise SystemExit('FAIL malformed JSON was accepted')
trace = ['checkpoint_changed'] * 128 + ['terminal_marker']
if polling_materializations(trace) != ['terminal_marker']:
    raise SystemExit('FAIL normal checkpoint polling materialized intermediate publications')
trace = ['checkpoint_changed', 'checkpoint_changed', 'crash_marker']
if polling_materializations(trace) != ['crash_marker']:
    raise SystemExit('FAIL crash-boundary recovery state is incorrect')
trace = ['checkpoint_changed', 'timeout_boundary']
if polling_materializations(trace) != ['timeout_boundary']:
    raise SystemExit('FAIL timeout-boundary recovery state is incorrect')
print('PASS 0.80.0 full hardening state machine')
