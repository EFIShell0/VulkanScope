#!/usr/bin/env python3
import heapq


def lane_count(low_ram, low_memory, total_mib, available_mib, processors):
    ratio = available_mib / total_mib if total_mib > 0 else 0.0
    if low_ram or low_memory:
        return 2
    if total_mib < 3072 or available_mib < 768 or ratio < 0.12 or processors < 4:
        return 2
    if total_mib < 5120 or available_mib < 1280 or ratio < 0.16 or processors < 6:
        return 3
    if total_mib < 6144 or available_mib < 1536 or ratio < 0.18 or processors < 8:
        return 4
    if total_mib < 7168 or available_mib < 2048 or ratio < 0.22:
        return 5
    return 6


def dynamic_schedule(durations, lanes):
    heap = [(0, lane) for lane in range(lanes)]
    heapq.heapify(heap)
    assignment = []
    for index, duration in enumerate(durations):
        ready, lane = heapq.heappop(heap)
        assignment.append((index, lane, ready, ready + duration))
        heapq.heappush(heap, (ready + duration, lane))
    return assignment


def tv_long_press(action, repeat_count, is_long_press, consumed):
    if action == 'down' and (is_long_press or repeat_count > 0):
        return True, True
    if action == 'up' and consumed:
        return True, False
    return False, consumed

assert lane_count(True, False, 12288, 8000, 8) == 2
assert lane_count(False, True, 12288, 8000, 8) == 2
assert lane_count(False, False, 2048, 1500, 8) == 2
assert lane_count(False, False, 4096, 1800, 8) == 3
assert lane_count(False, False, 5500, 2200, 8) == 4
assert lane_count(False, False, 6800, 2300, 8) == 5
assert lane_count(False, False, 8192, 3000, 8) == 6
assert lane_count(False, False, 12288, 7000, 8) == 6
assert lane_count(False, False, 12288, 7000, 6) == 4

durations = [135] + [70] * 134
assignment4 = dynamic_schedule(durations, 4)
assignment6 = dynamic_schedule(durations, 6)
assert max(end for _, _, _, end in assignment6) < max(end for _, _, _, end in assignment4)
assert len(assignment6) == 135
assert sorted(index for index, _, _, _ in assignment6) == list(range(135))

consumed, state = tv_long_press('down', 0, False, False)
assert not consumed and not state
consumed, state = tv_long_press('down', 1, False, state)
assert consumed and state
consumed, state = tv_long_press('down', 2, False, state)
assert consumed and state
consumed, state = tv_long_press('up', 0, False, state)
assert consumed and not state
consumed, state = tv_long_press('down', 0, True, False)
assert consumed and state

print('PASS VulkanScope 2.0.4 six-lane scheduler and TV long-press state machine')
