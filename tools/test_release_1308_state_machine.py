#!/usr/bin/env python3

def apply_page(current, raw, count):
    if count <= 1: return current
    if not raw or not raw.isdigit(): return current
    n=int(raw)
    return n-1 if 1 <= n <= count else current

def main():
    assert apply_page(0,'2',4)==1
    assert apply_page(1,'x',4)==1
    assert apply_page(1,'0',4)==1
    assert apply_page(1,'5',4)==1
    assert apply_page(0,'1',1)==0
    labels=['Core 1.0','Core 1.1','VK_EXT_memory_budget','VK_KHR_surface']
    q='memory'
    hits=[x for x in labels if q.lower() in x.lower()]
    assert hits==['VK_EXT_memory_budget']
    page_size=50
    assert (len(list(range(101)))+page_size-1)//page_size==3
    print('release_1308 state machine: PASS')
if __name__=='__main__': main()
