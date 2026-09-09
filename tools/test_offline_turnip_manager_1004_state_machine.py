#!/usr/bin/env python3

MAX=10

def transition(previous,current,generation):
    if previous is None:
        return current,generation,'HIDDEN',False
    if previous == current:
        return previous,generation,None,False
    generation += 1
    return current,generation,'CONNECTED' if current else 'DISCONNECTED',True

def tick_banner(state,remaining,collecting,step=100):
    if state == 'HIDDEN' or remaining <= 0: return state,remaining
    if collecting: return state,remaining
    remaining=max(0,remaining-step)
    return ('HIDDEN' if remaining==0 else state),remaining

def persistent_offline(known,network,collecting,transition_state):
    return known and not network and not collecting and transition_state=='HIDDEN'

def import_slot(occupied):
    for i in range(1,MAX+1):
        if i not in occupied: return i
    return None

def fallback_names(): return [f'turnip_{i:02d}.zip' for i in range(1,MAX+1)]

def edge_fades(can_left,can_right,layout):
    return (can_left,can_right) if layout in ('LTR','RTL') else (False,False)

prev=None; gen=0
prev,gen,state,shown=transition(prev,False,gen)
assert prev is False and state=='HIDDEN' and shown is False
assert persistent_offline(True,False,False,'HIDDEN')
assert not persistent_offline(True,False,True,'HIDDEN')
prev,gen,state,shown=transition(prev,True,gen)
assert state=='CONNECTED' and shown and gen==1
remaining=4500
for _ in range(50): state,remaining=tick_banner(state,remaining,True)
assert state=='CONNECTED' and remaining==4500
for _ in range(44): state,remaining=tick_banner(state,remaining,False)
assert state=='CONNECTED' and remaining==100
state,remaining=tick_banner(state,remaining,False)
assert state=='HIDDEN' and remaining==0
prev,gen,state,shown=transition(prev,False,gen)
assert state=='DISCONNECTED' and gen==2
assert transition(False,False,gen)[2] is None
assert import_slot(set())==1
assert import_slot({1,2,4})==3
assert import_slot(set(range(1,11))) is None
assert fallback_names()[0]=='turnip_01.zip' and fallback_names()[-1]=='turnip_10.zip' and len(fallback_names())==10
assert edge_fades(False,True,'LTR')==(False,True)
assert edge_fades(True,False,'RTL')==(True,False)
print('PASS 1.0.4 network/banner/Turnip-slot/fallback/filter-edge state model')
