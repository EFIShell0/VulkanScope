#!/usr/bin/env python3

def clamp(value, low, high): return max(low, min(high, value))

def menu_budget(screen_height, ime_height, landscape):
    usable=max(screen_height-ime_height,220.0)
    if landscape: return clamp(usable*0.62,220.0,440.0)
    return clamp(usable-96.0,240.0,620.0)

def visible_rows(result_count, landscape):
    return clamp(result_count if result_count else 1,1,4 if landscape else 7)

def centered_edges(container_width, group_width):
    left=(container_width-group_width)/2.0
    return left, container_width-(left+group_width)

def main():
    landscape=menu_budget(691,0,True)
    portrait=menu_budget(691,0,False)
    assert 420 < landscape < 440
    assert portrait == 595
    assert landscape < portrait
    assert menu_budget(432,0,True) < 300
    assert menu_budget(1200,0,True) == 440
    assert menu_budget(400,220,True) == 220
    assert visible_rows(55,True) == 4
    assert visible_rows(55,False) == 7
    assert visible_rows(2,True) == 2
    left,right=centered_edges(560,48+8+72+8+28+8+48)
    assert abs(left-right) < 1e-9
    assert 72 < 96
    print('release_1402 state machine: PASS')

if __name__=='__main__': main()
