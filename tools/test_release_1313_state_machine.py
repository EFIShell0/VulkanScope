#!/usr/bin/env python3

def vendor_id(text):
    if text is None: return None
    t=text.strip()
    if not t: return None
    try: return int(t[2:],16) if t.lower().startswith('0x') else int(t,10)
    except ValueError: return None

def transition(expanded,event,ime=False):
    if event=='arrow': return True if not expanded else expanded
    if event=='x': return False
    if event in {'outside','select'}: return expanded
    if event=='back': return expanded
    return expanded

def main():
    assert vendor_id('0x5143')==0x5143 and vendor_id('20803')==20803 and vendor_id('bad') is None
    assert transition(False,'arrow') is True
    assert transition(True,'arrow') is True
    assert transition(True,'outside') is True
    assert transition(True,'select') is True
    assert transition(True,'back',False) is True
    assert transition(True,'back',True) is True
    assert transition(True,'x') is False
    print('release_1313 state machine: PASS')
if __name__=='__main__': main()
