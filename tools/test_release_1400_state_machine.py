#!/usr/bin/env python3

def single_filter(expanded, event):
    if event == 'arrow':
        return not expanded
    if event in {'select', 'outside'}:
        return expanded
    return expanded

def multi_filter(expanded, event, selected):
    if event == 'arrow':
        return (not expanded), selected
    if event.startswith('toggle:'):
        label = event.split(':', 1)[1]
        updated = set(selected)
        if label in updated:
            updated.remove(label)
        else:
            updated.add(label)
        return expanded, updated
    return expanded, selected

def main():
    assert single_filter(False, 'arrow') is True
    assert single_filter(True, 'arrow') is False
    assert single_filter(True, 'select') is True
    assert single_filter(True, 'outside') is True
    expanded, selected = multi_filter(False, 'arrow', set())
    assert expanded is True and not selected
    expanded, selected = multi_filter(expanded, 'toggle:Sampled', selected)
    assert expanded is True and selected == {'Sampled'}
    expanded, selected = multi_filter(expanded, 'toggle:Storage', selected)
    assert expanded is True and selected == {'Sampled', 'Storage'}
    expanded, selected = multi_filter(expanded, 'arrow', selected)
    assert expanded is False and selected == {'Sampled', 'Storage'}
    print('release_1400 state machine: PASS')

if __name__ == '__main__':
    main()
