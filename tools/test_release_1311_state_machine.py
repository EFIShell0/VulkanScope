#!/usr/bin/env python3

def actions(phase):
    if phase in ('CONNECTING','DOWNLOADING'): return {'confirm':'Cancel','dismiss':'Pause','close':False}
    if phase=='PAUSED': return {'confirm':'Cancel','dismiss':'Resume','close':False}
    if phase=='VERIFYING': return {'confirm':None,'dismiss':None,'close':False}
    if phase=='COMPLETED': return {'confirm':'Install','dismiss':'Close','close':True}
    if phase in ('CANCELED','FAILED'): return {'confirm':None,'dismiss':'Close','close':True}
    raise ValueError(phase)

def main():
    assert actions('CONNECTING')=={'confirm':'Cancel','dismiss':'Pause','close':False}
    assert actions('DOWNLOADING')=={'confirm':'Cancel','dismiss':'Pause','close':False}
    assert actions('PAUSED')=={'confirm':'Cancel','dismiss':'Resume','close':False}
    assert actions('VERIFYING')=={'confirm':None,'dismiss':None,'close':False}
    assert actions('COMPLETED')=={'confirm':'Install','dismiss':'Close','close':True}
    assert actions('CANCELED')['dismiss']=='Close' and actions('FAILED')['dismiss']=='Close'
    print('release_1311 state machine: PASS')
if __name__=='__main__': main()
