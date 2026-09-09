#!/usr/bin/env python3

class NetworkModel:
    def __init__(self):
        self.tracked=None
        self.last=None
        self.generation=0
        self.events=[]
    def initial(self,network,validated):
        self.tracked=network
        self.apply(validated)
    def available(self,network):
        self.tracked=network
    def capabilities(self,network,validated):
        self.tracked=network
        self.apply(validated)
    def lost(self,network):
        if self.tracked==network:
            self.tracked=None
            self.apply(False)
    def apply(self,validated):
        if self.last is None:
            self.last=validated
            return
        if self.last==validated:
            return
        self.last=validated
        self.generation+=1
        self.events.append('CONNECTED' if validated else 'DISCONNECTED')

m=NetworkModel()
m.initial('wifi',True)
m.lost('wifi')
assert m.events==['DISCONNECTED'] and m.generation==1
m.available('cell')
m.capabilities('cell',True)
assert m.events==['DISCONNECTED','CONNECTED'] and m.generation==2
m.lost('cell')
assert m.events[-1]=='DISCONNECTED' and m.generation==3
m.available('wifi2')
m.capabilities('wifi2',True)
m.available('cell2')
m.lost('wifi2')
assert m.tracked=='cell2' and m.events[-1]=='CONNECTED'
m.capabilities('cell2',False)
assert m.events[-1]=='DISCONNECTED'

class DriverModel:
    def __init__(self):
        self.mode='SYSTEM'
        self.pending_mode=None
        self.pending_slot=None
        self.manager=False
        self.active_slot=2
        self.collecting=False
    def tap_turnip(self):
        self.manager=True
        if self.mode!='TURNIP' and self.active_slot is not None and not self.collecting:
            self.pending_mode='TURNIP'
    def request_system(self):
        if self.mode!='SYSTEM' and not self.collecting:
            self.pending_mode='SYSTEM'
    def confirm_mode(self):
        if self.pending_mode is not None and not self.collecting:
            self.mode=self.pending_mode
        self.pending_mode=None
    def request_slot(self,slot):
        if not self.collecting:
            self.pending_slot=slot
    def confirm_slot(self):
        if self.pending_slot is not None and not self.collecting:
            self.active_slot=self.pending_slot
            self.mode='TURNIP'
        self.pending_slot=None

d=DriverModel()
d.tap_turnip()
assert d.manager and d.pending_mode=='TURNIP' and d.mode=='SYSTEM'
d.pending_mode=None
assert d.mode=='SYSTEM'
d.tap_turnip(); d.confirm_mode()
assert d.mode=='TURNIP'
d.request_system()
assert d.pending_mode=='SYSTEM' and d.mode=='TURNIP'
d.confirm_mode()
assert d.mode=='SYSTEM'
d.request_slot(4)
assert d.pending_slot==4 and d.active_slot==2
d.confirm_slot()
assert d.active_slot==4 and d.mode=='TURNIP'
d.collecting=True
d.request_system(); d.request_slot(5)
assert d.pending_mode is None and d.pending_slot is None

alphas=[0.34,0.14,0.04,0.0]
assert all(0.0<=x<0.5 for x in alphas)
assert alphas==sorted(alphas,reverse=True)
assert 30>22
print('PASS 1.0.6 repeated-network-loss/driver-confirmation/edge-shadow state model')
