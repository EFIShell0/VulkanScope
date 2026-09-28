#!/usr/bin/env python3
from dataclasses import dataclass
from datetime import datetime

PAGE_SIZE = 25

def accept_page_input(candidate: str, page_count: int) -> bool:
    if candidate == '':
        return True
    if not candidate.isdigit() or (len(candidate) > 1 and candidate.startswith('0')):
        return False
    return 1 <= int(candidate) <= page_count

def commit_page(text: str, current: int, page_count: int) -> int:
    try:
        requested = int(text)
    except Exception:
        requested = current + 1
    return max(1, min(page_count, requested)) - 1

assert PAGE_SIZE == 25
assert accept_page_input('1', 10)
assert accept_page_input('10', 10)
assert not accept_page_input('11', 10)
assert not accept_page_input('15', 10)
assert not accept_page_input('0', 10)
assert not accept_page_input('01', 10)
assert accept_page_input('', 10)
assert commit_page('10', 0, 10) == 9
assert commit_page('', 4, 10) == 4

@dataclass(frozen=True)
class Entry:
    name: str
    modified: int
    created: int

rows = [Entry('beta', 30, 10), Entry('Alpha', 10, 30), Entry('gamma', 20, 20)]
assert [x.name for x in sorted(rows, key=lambda x: x.name.lower())] == ['Alpha', 'beta', 'gamma']
assert [x.name for x in sorted(rows, key=lambda x: x.name.lower(), reverse=True)] == ['gamma', 'beta', 'Alpha']
assert [x.name for x in sorted(rows, key=lambda x: (-x.modified, x.name.lower()))] == ['beta', 'gamma', 'Alpha']
assert [x.name for x in sorted(rows, key=lambda x: (x.modified, x.name.lower()))] == ['Alpha', 'gamma', 'beta']
assert [x.name for x in sorted(rows, key=lambda x: (-x.created, x.name.lower()))] == ['Alpha', 'gamma', 'beta']
assert [x.name for x in sorted(rows, key=lambda x: (x.created, x.name.lower()))] == ['beta', 'gamma', 'Alpha']
print('PASS VulkanScope 2.1.3 pager and file-sort state-machine model')
