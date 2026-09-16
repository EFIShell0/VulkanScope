#!/usr/bin/env python3

def contain_post_scroll(available_x, available_y):
    return (0.0, available_y) if available_y != 0.0 else (0.0, 0.0)

def contain_post_fling(available_x, available_y):
    return (0.0, available_y) if available_y != 0.0 else (0.0, 0.0)

def turnip_slot_metadata(library_name, description):
    library=(library_name or '').strip() or 'Not available'
    desc=(description or '').strip() or 'Not provided'
    return library, desc

def main():
    assert contain_post_scroll(7.0, 0.0)==(0.0,0.0)
    assert contain_post_scroll(0.0, 18.0)==(0.0,18.0)
    assert contain_post_scroll(0.0,-23.0)==(0.0,-23.0)
    assert contain_post_fling(9.0,0.0)==(0.0,0.0)
    assert contain_post_fling(0.0,1200.0)==(0.0,1200.0)
    assert contain_post_fling(0.0,-950.0)==(0.0,-950.0)
    assert turnip_slot_metadata('libvulkan_freedreno.so','Mesa Turnip')==('libvulkan_freedreno.so','Mesa Turnip')
    assert turnip_slot_metadata(None,None)==('Not available','Not provided')
    assert turnip_slot_metadata('','   ')==('Not available','Not provided')
    print('release_1401 state machine: PASS')

if __name__=='__main__': main()
