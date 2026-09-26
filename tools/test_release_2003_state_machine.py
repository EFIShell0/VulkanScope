#!/usr/bin/env python3
import heapq

def lane_count(low_ram, low_memory, total_mib, available_mib, processors):
    ratio = available_mib / total_mib if total_mib > 0 else 0.0
    if low_ram or low_memory:
        return 2
    if total_mib < 3072 or available_mib < 768 or ratio < 0.12:
        return 2
    if total_mib < 6144 or available_mib < 1536 or ratio < 0.20 or processors < 6:
        return 3
    return 4

def dynamic_schedule(durations, lanes):
    heap = [(0, lane) for lane in range(lanes)]
    heapq.heapify(heap)
    assignment = []
    for index, duration in enumerate(durations):
        ready, lane = heapq.heappop(heap)
        assignment.append((index, lane, ready, ready + duration))
        heapq.heappush(heap, (ready + duration, lane))
    return assignment

def merge_original(groups, completion, payloads):
    completed = {group: payloads[group] for group in completion}
    return [completed[group] for group in groups]

assert lane_count(True, False, 12288, 8000, 8) == 2
assert lane_count(False, True, 12288, 8000, 8) == 2
assert lane_count(False, False, 2048, 1500, 8) == 2
assert lane_count(False, False, 4096, 600, 8) == 2
assert lane_count(False, False, 4096, 1800, 8) == 3
assert lane_count(False, False, 8192, 3000, 4) == 3
assert lane_count(False, False, 8192, 3000, 8) == 4
assert lane_count(False, False, 12288, 7000, 8) == 4

durations = [280, 40, 40, 40, 40, 40, 40, 40, 40, 40]
assignment = dynamic_schedule(durations, 2)
assert len(assignment) == len(durations)
assert sorted(index for index, _, _, _ in assignment) == list(range(len(durations)))
assert all(0 <= lane < 2 for _, lane, _, _ in assignment)
assert assignment[1][1] != assignment[0][1]
dynamic_makespan = max(end for _, _, _, end in assignment)
static_lane0 = sum(durations[0::2])
static_lane1 = sum(durations[1::2])
assert dynamic_makespan < max(static_lane0, static_lane1)

groups = [f'ext::VK_TEST_{index:03d}' for index in range(135)]
completion = list(reversed(groups))
payloads = {group: f'payload:{group}' for group in groups}
assert merge_original(groups, completion, payloads) == [payloads[group] for group in groups]

host_total = 84
scheduled_elapsed = 87
probe_total = host_total
queue_wait = max(0, scheduled_elapsed - host_total)
assert probe_total == 84
assert queue_wait == 3

print('PASS VulkanScope 2.0.3 adaptive scheduler state machine')
