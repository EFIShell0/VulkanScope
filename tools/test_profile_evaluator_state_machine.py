#!/usr/bin/env python3
from dataclasses import dataclass

@dataclass
class Evidence:
    failed: bool = False
    unknown: bool = False


def state(evidence, complete=True):
    if evidence.failed:
        return 'FAIL'
    if evidence.unknown or not complete:
        return 'UNKNOWN'
    return 'PASS'


def check_minimum(actual, required):
    return Evidence(failed=actual < required)


def check_maximum(actual, required):
    return Evidence(failed=actual > required)


def check_mask(actual, required):
    return Evidence(failed=(actual & required) != required)


def check_or(items):
    states = [state(x) for x in items]
    if 'PASS' in states:
        return Evidence()
    if all(x == 'FAIL' for x in states):
        return Evidence(failed=True)
    return Evidence(unknown=True)

assert state(Evidence(), True) == 'PASS'
assert state(Evidence(), False) == 'UNKNOWN'
assert state(Evidence(unknown=True), True) == 'UNKNOWN'
assert state(Evidence(failed=True, unknown=True), False) == 'FAIL'
assert state(check_minimum(7, 8)) == 'FAIL'
assert state(check_minimum(8, 8)) == 'PASS'
assert state(check_maximum(4097, 4096)) == 'FAIL'
assert state(check_maximum(4096, 4096)) == 'PASS'
assert state(check_mask(0x3F, 0x3F)) == 'PASS'
assert state(check_mask(0x1F, 0x3F)) == 'FAIL'
assert state(check_or([Evidence(failed=True), Evidence()])) == 'PASS'
assert state(check_or([Evidence(failed=True), Evidence(failed=True)])) == 'FAIL'
assert state(check_or([Evidence(failed=True), Evidence(unknown=True)])) == 'UNKNOWN'
assert state(Evidence(), False) != 'PASS'
print('PASS VulkanScope 0.41.44 profile evaluator state machine')
