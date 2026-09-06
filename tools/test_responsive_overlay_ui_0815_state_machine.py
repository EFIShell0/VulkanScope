#!/usr/bin/env python3

def directions(position, maximum):
    return position > 0, position < maximum


def indicator_slots(position, maximum):
    up, down = directions(position, maximum)
    return ("top" if up else None, "bottom" if down else None)

def detail_stacked(width_dp,font_scale,key,value):
    return font_scale >= 1.3 or width_dp < 420 or len(key) > 22 or len(value) > 30 or '\n' in value

def explore_columns(width_dp,font_scale):
    expanded=font_scale>=1.3 or width_dp<360
    if expanded or width_dp<300: return 1
    if width_dp<620: return 2
    return 3

def quick_columns(width_dp,font_scale):
    expanded=font_scale>=1.3 or width_dp<360
    if expanded or width_dp<300: return 1
    if width_dp<540: return 2
    if width_dp<780: return 3
    return 4

def body_height(height_dp):
    return min(540,max(140,height_dp-200))

assert directions(0,100)==(False,True)
assert directions(50,100)==(True,True)
assert directions(100,100)==(True,False)
assert directions(0,0)==(False,False)
assert indicator_slots(0,100)==(None,"bottom")
assert indicator_slots(50,100)==("top","bottom")
assert indicator_slots(100,100)==("top",None)
assert indicator_slots(0,0)==(None,None)

visible=True
scrolling=False
has_direction=True
assert visible
visible=False
assert not visible
scrolling=True
if scrolling and has_direction: visible=True
assert visible
scrolling=False
visible=False
assert not visible
scrolling=True
if scrolling and has_direction: visible=True
assert visible

assert detail_stacked(340,1.0,'Scope','device')
assert detail_stacked(520,1.0,'Registry requires','Unavailable in checked-in reference asset')
assert detail_stacked(700,1.3,'Scope','device')
assert not detail_stacked(520,1.0,'Scope','device')
assert explore_columns(393,1.0)==2
assert explore_columns(800,1.0)==3
assert explore_columns(700,1.3)==1
assert quick_columns(393,1.0)==2
assert quick_columns(700,1.0)==3
assert quick_columns(900,1.0)==4
assert quick_columns(700,1.3)==1
assert body_height(400)==200
assert body_height(600)==400
assert body_height(900)==540
print('PASS 0.80.15 responsive overlay-scroll/detail-layout state machine')
