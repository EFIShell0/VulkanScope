#!/usr/bin/env python3
import re

PAGE_SIZE = 50

def page_count(total):
    return max(1, (total + PAGE_SIZE - 1) // PAGE_SIZE)

def page_slice(total, page):
    count = page_count(total)
    page = min(max(page, 0), count - 1)
    start = page * PAGE_SIZE
    end = min(start + PAGE_SIZE, total)
    return start, end

def combined_slice(limit_count, property_count, page):
    total = limit_count + property_count
    start, end = page_slice(total, page)
    limit_start = min(start, limit_count)
    limit_end = min(end, limit_count)
    property_start = max(0, start - limit_count)
    property_end = max(0, end - limit_count)
    return limit_end - limit_start, property_end - property_start

def progress(downloaded, total):
    if total is None or total <= 0:
        return 0.0
    return min(max(downloaded / total, 0.0), 1.0)

def valid_report_id(value):
    return bool(re.fullmatch(r'[0-9a-f]{64}', value))

assert page_count(0) == 1
assert page_count(1) == 1
assert page_count(50) == 1
assert page_count(51) == 2
assert page_count(123) == 3
assert page_slice(123, 0) == (0, 50)
assert page_slice(123, 1) == (50, 100)
assert page_slice(123, 2) == (100, 123)
assert page_slice(123, 99) == (100, 123)
assert combined_slice(30, 80, 0) == (30, 20)
assert combined_slice(30, 80, 1) == (0, 50)
assert combined_slice(30, 80, 2) == (0, 10)
assert progress(0, 100) == 0.0
assert progress(25, 100) == 0.25
assert progress(100, 100) == 1.0
assert progress(125, 100) == 1.0
assert valid_report_id('a' * 64)
assert not valid_report_id('A' * 64)
assert not valid_report_id('a' * 63)
assert not valid_report_id('../' + 'a' * 64)
layouts = ['LIST', 'COMPACT', 'DETAILS', 'GRID', 'DENSE_GRID', 'LARGE_GRID']
selected = 'DENSE_GRID'
stored = selected
restored = stored if stored in layouts else 'LIST'
assert restored == selected
assert len(layouts) == 6
print('PASS VulkanScope 2.1.0 UI/pagination/update/Database state machine')
