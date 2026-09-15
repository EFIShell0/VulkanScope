#!/usr/bin/env python3

def page_count(n, size=50): return max(1,(n+size-1)//size)
def show_search(n): return n>=5
def show_paging(filtered_n): return filtered_n>0 and page_count(filtered_n)>1
def opening_page(selected_index, n, size=50):
    if selected_index is None or selected_index<0 or selected_index>=n: return 0
    return min(selected_index//size, page_count(n,size)-1)
def page_edit(active_page, text, count):
    if text=='': return active_page, ''
    if not text.isdecimal() or (len(text)>1 and text.startswith('0')): return active_page, str(active_page+1)
    v=int(text)
    if v not in range(1,count+1): return active_page, str(active_page+1)
    return v-1,text

def main():
    assert not show_search(0) and not show_search(4) and show_search(5)
    assert not show_paging(1) and not show_paging(50) and show_paging(51)
    assert opening_page(0,186)==0 and opening_page(75,186)==1 and opening_page(185,186)==3
    p,t=page_edit(2,'',4); assert (p,t)==(2,'')
    p,t=page_edit(2,'4',4); assert (p,t)==(3,'4')
    p,t=page_edit(2,'5',4); assert p==2
    p,t=page_edit(2,'x',4); assert p==2
    print('release_1312 state machine: PASS')
if __name__=='__main__': main()
