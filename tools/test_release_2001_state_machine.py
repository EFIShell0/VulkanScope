#!/usr/bin/env python3

MAX_GROUPS = 16
BACKGROUND_BUDGET_MS = 60_000

def validate_session(groups, timeouts):
    if not 1 <= len(groups) <= MAX_GROUPS:
        return False
    if len(groups) != len(timeouts):
        return False
    if any(not g or len(g) > 256 or "\x00" in g for g in groups):
        return False
    return all(1_000 <= t <= 60_000 for t in timeouts)

def simulate(groups, fault_indexes=()):
    results = {}
    launches = 0
    offset = 0
    faults = set(fault_indexes)
    while offset < len(groups):
        launches += 1
        local = groups[offset:offset + MAX_GROUPS]
        failed = None
        for index, group in enumerate(local):
            absolute = offset + index
            if absolute in faults:
                results[group] = "unavailable"
                failed = index
                break
            results[group] = "available"
        if failed is None:
            offset += len(local)
        else:
            offset += failed + 1
    return launches, results

groups = [f"ext::VK_TEST_{i}" for i in range(125)]
timeouts = [12_000] * 16
assert validate_session(groups[:16], timeouts)
assert not validate_session([], [])
assert not validate_session(groups[:17], [12_000] * 17)
assert not validate_session(["x", "y"], [12_000])
assert not validate_session([""], [12_000])
assert not validate_session(["a\x00b"], [12_000])
assert not validate_session(["x"], [999])
assert not validate_session(["x"], [60_001])
launches, results = simulate(groups)
assert launches == 8 and len(results) == 125 and set(results.values()) == {"available"}
launches, results = simulate(groups, {40})
assert launches == 9 and results[groups[40]] == "unavailable" and all(results[g] == "available" for i, g in enumerate(groups) if i != 40)
launches, results = simulate(groups, {10, 50, 90})
assert launches == 10 and sum(v == "unavailable" for v in results.values()) == 3 and len(results) == 125
assert BACKGROUND_BUDGET_MS == 60_000
print("PASS VulkanScope 2.0.1 bounded-session collector state-machine model")
