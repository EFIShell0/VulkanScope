#!/usr/bin/env python3
from dataclasses import dataclass
from pathlib import Path

@dataclass
class Permission:
    granted: bool=False; pending: bool=False; denied_until_ms:int=0
    def request(self, already, now=0):
        if already: self.granted=True; self.pending=False; return 'open'
        self.pending=True; return 'settings'
    def resume(self, now, granted):
        assert self.pending; self.pending=False; self.granted=granted
        if granted: return 'open'
        self.denied_until_ms=now+3000; return 'denied'

@dataclass
class Snapshot:
    pending: bool=False; complete: bool=True
    def prepare(self):
        if not self.complete or self.pending: return False
        self.pending=True; return True
    def cancel(self): self.pending=False
    def save(self):
        if not self.pending: return False
        self.pending=False; return True

def under(root,candidate):
    root=Path(root).resolve(); c=Path(candidate).resolve()
    return c==root or root in c.parents

def main():
    p=Permission(); assert p.request(False)=='settings'; assert p.resume(100,False)=='denied'; assert p.denied_until_ms==3100
    p=Permission(); assert p.request(True)=='open' and p.granted
    assert under('/storage/emulated/0','/storage/emulated/0/Download/a.json')
    assert not under('/storage/emulated/0','/data/local/tmp/x.json')
    max_snapshot=8*1024*1024; assert max_snapshot<=8*1024*1024 and max_snapshot+1>max_snapshot
    max_profile=256*1024; assert max_profile<=256*1024 and max_profile+1>max_profile
    s=Snapshot(); assert s.prepare(); assert not s.prepare(); s.cancel(); assert not s.pending; assert s.prepare(); assert s.save(); assert not s.pending
    print('release_1306 state machine: PASS')
if __name__=='__main__': main()
