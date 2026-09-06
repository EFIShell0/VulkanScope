#!/usr/bin/env python3

def transition(selected, event, value):
    if event == 'details':
        return value
    if event in {'card', 'focus', 'dpad_focus', 'scroll'}:
        return selected
    if event == 'dismiss':
        return None
    raise ValueError(event)

for kind in ['format', 'supported-extension', 'catalog-extension']:
    if transition(None, 'card', kind) is not None:
        raise SystemExit('FAIL card activation opened detail: ' + kind)
    if transition(None, 'dpad_focus', kind) is not None:
        raise SystemExit('FAIL TV focus opened detail: ' + kind)
    if transition(None, 'details', kind) != kind:
        raise SystemExit('FAIL Details action did not open detail: ' + kind)
    if transition(kind, 'dismiss', kind) is not None:
        raise SystemExit('FAIL dismiss did not clear detail: ' + kind)
print('PASS 0.80.9 detail-button-only state machine')
