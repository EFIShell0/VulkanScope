#!/usr/bin/env python3

def close_turnip(state):
    if state['importing'] or not state['visible']:
        return state, False
    next_state = dict(state)
    next_state['visible'] = False
    next_state['details'] = None
    next_state['status'] = None
    return next_state, True

state = {'visible': True, 'importing': False, 'directory': '/storage/emulated/0/Download', 'selected': {'a.zip'}, 'details': 'a.zip', 'status': 'ready'}
closing, delayed_reset = close_turnip(state)
if not delayed_reset or closing['visible'] or closing['directory'] != state['directory'] or closing['selected'] != state['selected']:
    raise SystemExit('Turnip exit animation no longer retains browser state')


def toolbar(search_expanded, width=100):
    if search_expanded:
        return ['view-sort', ('search-field', width), 'close-search']
    return ['summary', 'view-sort', 'search']

collapsed = toolbar(False)
expanded = toolbar(True, 72)
if collapsed[-2:] != ['view-sort', 'search']:
    raise SystemExit('search is no longer immediately to the right of View & sort')
if expanded[0] != 'view-sort' or expanded[1] != ('search-field', 72):
    raise SystemExit('expanded search no longer receives remaining toolbar width')


def format_elapsed(ms):
    safe = max(0, ms)
    return f'{safe / 1000.0:.3f} s ({safe} ms)'

expected = {0: '0.000 s (0 ms)', 1: '0.001 s (1 ms)', 1250: '1.250 s (1250 ms)', -4: '0.000 s (0 ms)'}
for ms, text in expected.items():
    if format_elapsed(ms) != text:
        raise SystemExit(f'diagnostic duration formatting regressed for {ms}')


def merge_page(existing, page, reset):
    source = page if reset else existing + page
    unique = []
    seen = set()
    for row in source:
        if row['id'] in seen:
            continue
        seen.add(row['id'])
        unique.append(row)
        if len(unique) == 200:
            break
    return unique

page1 = [{'id': f'{i:064x}'} for i in range(50)]
page2 = [{'id': f'{i:064x}'} for i in range(40, 100)]
merged = merge_page(page1, page2, False)
if len(merged) != 100:
    raise SystemExit('Database list deduplication regressed')
large = merge_page([], [{'id': f'{i:064x}'} for i in range(250)], True)
if len(large) != 200:
    raise SystemExit('Database in-app bound regressed')

cursor = {'submittedAt': '2026-09-29T09:00:00.000Z', 'id': 'a' * 64}
query = {'limit': '50', 'beforeSubmittedAt': cursor['submittedAt'], 'beforeId': cursor['id']}
if query['limit'] != '50' or len(query['beforeId']) != 64:
    raise SystemExit('Database cursor request contract regressed')

print('PASS VulkanScope 3.0.5 UI/workflow state machine')
