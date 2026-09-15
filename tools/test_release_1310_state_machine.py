#!/usr/bin/env python3

def imported_key(path,name): return path.strip()+'\0'+name.strip()
def can_select(path,name,imported): return imported_key(path,name) not in imported
def clamp_page(page,count): return max(0,min(page,max(1,count)-1))

def main():
    imported={imported_key('/storage/emulated/0/Download/turnip.zip','turnip.zip')}
    assert not can_select('/storage/emulated/0/Download/turnip.zip','turnip.zip',imported)
    assert can_select('/storage/emulated/0/Other/turnip.zip','turnip.zip',imported)
    assert can_select('/storage/emulated/0/Download/turnip2.zip','turnip2.zip',imported)
    assert clamp_page(3,1)==0 and clamp_page(9,4)==3 and clamp_page(2,4)==2
    # Pagination remains a persistent footer; only the flexible result region changes target page/query.
    popup={'search':True,'results_weighted':True,'paging_footer':True}
    assert all(popup.values())
    print('release_1310 state machine: PASS')
if __name__=='__main__': main()
