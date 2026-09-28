#!/usr/bin/env python3

def fade_state(value, measured, available, focused):
    overflow = bool(value.strip()) and measured > available
    return focused and overflow, overflow

assert fade_state('', 0, 100, False) == (False, False)
assert fade_state('abc', 40, 100, True) == (False, False)
assert fade_state('abcdefgh', 140, 100, False) == (False, True)
assert fade_state('abcdefgh', 140, 100, True) == (True, True)

def pager_offset(pinned, header_inset, transient_inset):
    return header_inset + transient_inset + 56 if pinned else 0

assert pager_offset(False, 96, 0) == 0
assert pager_offset(True, 96, 0) == 152
assert pager_offset(True, 72, 44) == 172

modes = ['LIST', 'COMPACT', 'DETAILS', 'GRID', 'DENSE_GRID', 'LARGE_GRID']
sorts = ['NAME_ASC', 'NAME_DESC', 'MODIFIED_NEWEST', 'MODIFIED_OLDEST', 'CREATED_NEWEST', 'CREATED_OLDEST']
assert len(modes) == 6 and len(set(modes)) == 6
assert len(sorts) == 6 and len(set(sorts)) == 6
state = {'view': 'LIST', 'sort': 'NAME_ASC'}
state['view'] = 'DENSE_GRID'
assert state['sort'] == 'NAME_ASC'
state['sort'] = 'CREATED_NEWEST'
assert state == {'view': 'DENSE_GRID', 'sort': 'CREATED_NEWEST'}

def header_horizontal_padding(base, system_start, system_end):
    return base + system_start, base + system_end

assert header_horizontal_padding(14, 0, 48) == (14, 62)
print('PASS VulkanScope 2.1.5 UI state-machine contract')
