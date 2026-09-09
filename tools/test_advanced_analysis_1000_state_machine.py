#!/usr/bin/env python3
import re

MAX_HISTORY=8
MAX_RULES=64
MAX_PROFILES=32
MAX_GLOBAL=160
MAX_RAW=256
MAX_DB=2*1024*1024

def extension_state(present, authoritative):
    if present: return 'SATISFIED'
    return 'NOT SATISFIED' if authoritative else 'UNKNOWN'

def minimum_bool(actual, expected=True):
    if actual is None: return 'UNKNOWN'
    return 'PASS' if actual == expected else 'FAIL'

def minimum_limit(actual, op, expected):
    if actual is None: return 'UNKNOWN'
    okay={'>=':actual>=expected,'<=':actual<=expected,'==':actual==expected,'>':actual>expected,'<':actual<expected}[op]
    return 'PASS' if okay else 'FAIL'

def presentation(surface, display):
    if surface and display is True: return 'COMPATIBLE EVIDENCE'
    if surface and display is False: return 'SURFACE ONLY'
    if not surface and display is True: return 'DISPLAY ONLY'
    return 'NO MATCHING EVIDENCE'

def ab_start(mode, turnip_supported):
    if not turnip_supported: return 'blocked',None
    return ('collect-turnip','TURNIP') if mode=='SYSTEM' else ('collect-system','SYSTEM')

def ab_complete(stage, mode, turnip_supported=True):
    if stage=='collect-system' and mode=='SYSTEM':
        return ('collect-turnip','TURNIP') if turnip_supported else ('blocked',None)
    if stage=='collect-turnip' and mode=='TURNIP': return 'complete',None
    return stage,None

assert extension_state(True,False)=='SATISFIED'
assert extension_state(False,True)=='NOT SATISFIED'
assert extension_state(False,False)=='UNKNOWN'
assert minimum_bool(True)=='PASS'
assert minimum_bool(False)=='FAIL'
assert minimum_bool(None)=='UNKNOWN'
assert minimum_limit(16384,'>=',8192)=='PASS'
assert minimum_limit(4096,'>=',8192)=='FAIL'
assert minimum_limit(None,'>=',8192)=='UNKNOWN'
assert presentation(True,True)=='COMPATIBLE EVIDENCE'
assert presentation(True,False)=='SURFACE ONLY'
assert presentation(False,True)=='DISPLAY ONLY'
assert presentation(False,None)=='NO MATCHING EVIDENCE'
assert ab_start('SYSTEM',True)==('collect-turnip','TURNIP')
assert ab_start('TURNIP',True)==('collect-system','SYSTEM')
assert ab_start('SYSTEM',False)==('blocked',None)
assert ab_complete('collect-system','SYSTEM')==('collect-turnip','TURNIP')
assert ab_complete('collect-turnip','TURNIP')==('complete',None)
assert re.fullmatch(r'[a-f0-9]{64}','a'*64)
assert not re.fullmatch(r'[a-f0-9]{64}','A'*64)
assert not re.fullmatch(r'[a-f0-9]{64}','a'*63)
assert MAX_HISTORY==8 and MAX_RULES==64 and MAX_PROFILES==32 and MAX_GLOBAL==160 and MAX_RAW==256 and MAX_DB==2097152
assert len(list(range(400))[:MAX_GLOBAL])==160
assert len(list(range(400))[:MAX_RAW])==256
print('PASS 1.0.0 analysis/evidence state model')
