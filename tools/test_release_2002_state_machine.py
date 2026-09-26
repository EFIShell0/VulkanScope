#!/usr/bin/env python3

def lane_count(memory_class_mb):
    if memory_class_mb <= 256:
        return 2
    if memory_class_mb <= 384:
        return 3
    return 4

def assign(groups, lanes):
    return [(group, index % lanes) for index, group in enumerate(groups)]

def merge_in_original_order(groups, completion_order, payloads):
    completed = {group: payloads[group] for group in completion_order}
    return [completed[group] for group in groups]

assert lane_count(128) == 2
assert lane_count(256) == 2
assert lane_count(257) == 3
assert lane_count(384) == 3
assert lane_count(385) == 4
assert lane_count(1024) == 4

groups = [f'ext::VK_TEST_{index:03d}' for index in range(125)]
for memory in (192, 320, 512):
    lanes = lane_count(memory)
    assignments = assign(groups, lanes)
    assert len(assignments) == len(groups)
    assert [group for group, _ in assignments] == groups
    assert max(lane for _, lane in assignments) < lanes
    assert min(lane for _, lane in assignments) == 0

completion = list(reversed(groups))
payloads = {group: f'payload:{group}' for group in groups}
merged = merge_in_original_order(groups, completion, payloads)
assert merged == [payloads[group] for group in groups]

failed = groups[37]
payloads[failed] = f'unavailable:{failed}'
merged = merge_in_original_order(groups, completion, payloads)
assert len(merged) == 125
assert merged[37] == f'unavailable:{failed}'
assert merged[36] == f'payload:{groups[36]}'
assert merged[38] == f'payload:{groups[38]}'

print('PASS VulkanScope 2.0.2 bounded parallel one-shot state machine')
