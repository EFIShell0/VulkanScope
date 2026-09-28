#!/usr/bin/env python3

PAGE_SIZE = 50


def page_count(total):
    return max(1, (total + PAGE_SIZE - 1) // PAGE_SIZE)


def clamp_page(total, page):
    return min(max(page, 0), page_count(total) - 1)


def page_slice(total, page):
    page = clamp_page(total, page)
    start = page * PAGE_SIZE
    return start, min(start + PAGE_SIZE, total)


def on_page_change(total, current, requested):
    return clamp_page(total, requested)


assert page_count(0) == 1
assert page_count(50) == 1
assert page_count(51) == 2
assert page_count(151) == 4
assert page_slice(151, 0) == (0, 50)
assert page_slice(151, 1) == (50, 100)
assert page_slice(151, 2) == (100, 150)
assert page_slice(151, 3) == (150, 151)
assert on_page_change(151, 0, 1) == 1
assert on_page_change(151, 1, 2) == 2
assert on_page_change(151, 2, 3) == 3
assert on_page_change(151, 3, 4) == 3
assert on_page_change(151, 3, -1) == 0
print('PASS VulkanScope 2.1.1 collection pager state machine')
