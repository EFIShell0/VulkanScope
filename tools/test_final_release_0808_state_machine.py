#!/usr/bin/env python3

def terminal_state(success, elapsed, new_collection=False):
    state='COMPLETED' if success else 'FAILED'
    if new_collection:return 'COLLECTING'
    if state=='FAILED':return 'FAILED'
    return 'IDLE' if elapsed>=2.0 else state
cases=[
(False,0,False,'FAILED'),(False,2,False,'FAILED'),(False,60,False,'FAILED'),
(False,600,False,'FAILED'),(False,3,True,'COLLECTING'),
(True,0,False,'COMPLETED'),(True,1.9,False,'COMPLETED'),(True,2,False,'IDLE'),
(True,10,True,'COLLECTING')]
for c in cases:
    got=terminal_state(*c[:3])
    if got!=c[3]:raise SystemExit(f'FAIL collection state {c} got={got}')
print('PASS 0.80.8 collection terminal state machine')
