#!/usr/bin/env python3
from dataclasses import dataclass, replace

SYSTEM = 'SYSTEM'
TURNIP = 'TURNIP'

@dataclass(frozen=True)
class Driver:
    slot: int
    selected: bool
    installed: bool = True
    source_available: bool = True

def read_drivers(active_slot: int, active_mode: str, slots=(1, 2)):
    return [Driver(slot, active_mode == TURNIP and slot == active_slot) for slot in slots]

def present(driver: Driver, active_mode: str):
    return driver if active_mode == TURNIP else replace(driver, selected=False)

def assert_exclusive(mode, drivers):
    system_active = mode == SYSTEM
    turnip_active = [d.slot for d in drivers if present(d, mode).selected]
    assert not (system_active and turnip_active), (mode, system_active, turnip_active)
    if mode == SYSTEM:
        assert system_active and turnip_active == []
    else:
        assert not system_active and len(turnip_active) <= 1

for active_slot in (1, 2):
    system_rows = read_drivers(active_slot, SYSTEM)
    assert_exclusive(SYSTEM, system_rows)
    turnip_rows = read_drivers(active_slot, TURNIP)
    assert_exclusive(TURNIP, turnip_rows)
    assert [d.slot for d in turnip_rows if d.selected] == [active_slot]

stale_turnip_rows = read_drivers(1, TURNIP)
assert stale_turnip_rows[0].selected
assert_exclusive(SYSTEM, stale_turnip_rows)
assert not present(stale_turnip_rows[0], SYSTEM).selected

reloaded_system_rows = read_drivers(1, SYSTEM)
assert not any(d.selected for d in reloaded_system_rows)
reloaded_turnip_rows = read_drivers(1, TURNIP)
assert [d.slot for d in reloaded_turnip_rows if d.selected] == [1]

print('PASS VulkanScope 1.0.11 active-driver exclusivity state machine')
