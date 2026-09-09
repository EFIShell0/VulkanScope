#!/usr/bin/env python3

def turnip_manager(support):
    if support in {'UNSUPPORTED','UNKNOWN'}:
        return {'status':'UNAVAILABLE','metrics':False,'import':False}
    return {'status':'SUPPORTED','metrics':True,'import':True}

for state in ['UNSUPPORTED','UNKNOWN']:
    result=turnip_manager(state)
    assert result=={'status':'UNAVAILABLE','metrics':False,'import':False}
assert turnip_manager('SUPPORTED')['metrics'] and turnip_manager('SUPPORTED')['import']

def slot_state(active,installed,source_available):
    if active:
        return 'ACTIVE','green'
    if installed and source_available:
        return 'AVAILABLE','blue'
    return 'UNAVAILABLE','amber'

assert slot_state(True,True,False)==('ACTIVE','green')
assert slot_state(False,True,True)==('AVAILABLE','blue')
assert slot_state(False,True,False)==('UNAVAILABLE','amber')
assert slot_state(False,False,True)==('UNAVAILABLE','amber')

class Connectivity:
    def __init__(self,collecting,known,online):
        self.collecting=collecting
        self.known=known
        self.online=online
        self.transition=None
    def change(self,online):
        if self.known and self.online!=online:
            self.transition='CONNECTED' if online else 'DISCONNECTED'
        self.known=True
        self.online=online
    def persistent(self):
        return self.known and not self.online and self.transition is None
    def finish_transition(self):
        self.transition=None

m=Connectivity(True,True,False)
assert m.persistent()
m.change(True)
assert m.transition=='CONNECTED' and m.collecting
m.finish_transition()
assert not m.persistent()
m.change(False)
assert m.transition=='DISCONNECTED' and m.collecting
m.finish_transition()
assert m.persistent()
m.collecting=False
assert m.persistent()

def database_lock(complete,online):
    if not complete and not online:
        return 'collection+network'
    if not complete:
        return 'collection'
    if not online:
        return 'network'
    return 'ready'

assert database_lock(False,False)=='collection+network'
assert database_lock(False,True)=='collection'
assert database_lock(True,False)=='network'
assert database_lock(True,True)=='ready'

def carousel_state(can_move):
    return {'arrowAlpha':1.0 if can_move else 0.42,'scale':1.0 if can_move else 0.92,'continuationAlpha':1.0 if can_move else 0.0,'durationMs':220,'edgeInsetDp':60,'maskWidthDp':82}

assert carousel_state(False)=={'arrowAlpha':0.42,'scale':0.92,'continuationAlpha':0.0,'durationMs':220,'edgeInsetDp':60,'maskWidthDp':82}
assert carousel_state(True)=={'arrowAlpha':1.0,'scale':1.0,'continuationAlpha':1.0,'durationMs':220,'edgeInsetDp':60,'maskWidthDp':82}
assert carousel_state(True)['edgeInsetDp']>48

print('PASS VulkanScope 1.0.7 Turnip/connectivity/filter/dialog state machine')
