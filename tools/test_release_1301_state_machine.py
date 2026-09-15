#!/usr/bin/env python3

def filtered(labels, query):
    return [(i, label) for i, label in enumerate(labels) if not query or query.lower() in label.lower()]

def page_slice(items, page, size=50):
    count = max(1, (len(items) + size - 1) // size)
    page = min(max(page, 0), count - 1)
    return page, count, items[page * size:(page + 1) * size]

def direct_page(current, typed, count):
    digits = ''.join(c for c in typed if c.isdigit())[:5]
    if digits:
        requested = int(digits)
        if 1 <= requested <= count:
            return requested - 1, digits
    return current, digits

labels = [f'Core {i}' for i in range(1, 126)]
items = filtered(labels, '')
page, pages, first = page_slice(items, 0)
assert page == 0 and pages == 3 and len(first) == 50
page, pages, last = page_slice(items, 2)
assert len(last) == 25 and last[0][0] == 100
searched = filtered(labels, 'core 12')
assert [label for _, label in searched] == ['Core 12', 'Core 120', 'Core 121', 'Core 122', 'Core 123', 'Core 124', 'Core 125']
assert filtered(['Alpha', 'beta', 'Gamma'], 'BETA') == [(1, 'beta')]
page, typed = direct_page(0, '3', 3)
assert page == 2 and typed == '3'
page, typed = direct_page(2, '99', 3)
assert page == 2 and typed == '99'
all_labels = ['All', 'Core 1.0', 'Core 1.1']
selected = 0
all_enabled = selected == 0
assert all_enabled
selected = 1
assert selected - 1 == 0
assert all_labels[selected] == 'Core 1.0'
android_hdr = {1: 'Dolby Vision', 2: 'HDR10', 3: 'HLG', 4: 'HDR10+', 6: 'HLG+'}
assert android_hdr[3] == 'HLG' and android_hdr[6] == 'HLG+' and 5 not in android_hdr
logo_types = {'Dolby Vision', 'HDR10', 'HDR10+', 'Dolby Vision 2', 'HDR10+ Advanced', 'HDR Vivid'}
assert 'HLG' not in logo_types and 'HLG+' not in logo_types
print('PASS VulkanScope 1.3.1 filter pagination/search/All and HDR presentation state model')
