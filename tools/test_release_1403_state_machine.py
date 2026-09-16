#!/usr/bin/env python3

def dropdown_width(container_width, landscape):
    return container_width if landscape else min(container_width, 560.0)

def orientation_transition(config_handled, report_present, current_page, surface_recreated):
    activity_recreated=not config_handled
    full_collection=activity_recreated or not report_present
    surface_refresh=bool(config_handled and report_present and surface_recreated)
    retained_page=current_page if config_handled else 'saved-state'
    return activity_recreated, full_collection, surface_refresh, retained_page

def main():
    assert dropdown_width(1180.0, True)==1180.0
    assert dropdown_width(560.0, True)==560.0
    assert dropdown_width(1180.0, False)==560.0
    assert dropdown_width(420.0, False)==420.0
    recreated,full,refresh,page=orientation_transition(True,True,'Properties',False)
    assert not recreated and not full and not refresh and page=='Properties'
    recreated,full,refresh,page=orientation_transition(True,True,'Formats',True)
    assert not recreated and not full and refresh and page=='Formats'
    recreated,full,refresh,page=orientation_transition(False,True,'Extensions',True)
    assert recreated and full and not refresh and page=='saved-state'
    print('release_1403 state machine: PASS')

if __name__=='__main__': main()
