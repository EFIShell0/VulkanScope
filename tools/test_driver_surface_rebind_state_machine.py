#!/usr/bin/env python3
from dataclasses import dataclass


@dataclass
class DriverSurfaceState:
    host_generation: int = 0
    current_surface: str | None = None
    surface_generation: int = 0
    driver_rebind_pending: bool = False
    report_present: bool = False
    collection_active: bool = False
    full_collections: int = 0
    surface_refreshes: int = 0
    surface_refresh_pending: bool = False

    def request_driver_change(self, changes_driver: bool, force: bool = False):
        if not changes_driver and not force:
            return
        self.current_surface = None
        self.surface_generation += 1
        self.surface_refresh_pending = False
        self.driver_rebind_pending = True
        self.host_generation += 1
        self.report_present = False

    def surface_created(self, host_generation: int, surface: str):
        if host_generation != self.host_generation:
            return
        if self.current_surface == surface:
            return
        self.current_surface = surface
        self.surface_generation += 1
        self.surface_refresh_pending = True
        if self.driver_rebind_pending:
            self.driver_rebind_pending = False
            self.full_collections += 1
            self.collection_active = True
            return
        if not self.report_present and not self.collection_active:
            self.full_collections += 1
            self.collection_active = True
            return
        if self.report_present:
            if self.collection_active:
                self.surface_refresh_pending = True
            else:
                self.surface_refresh_pending = False
                self.surface_refreshes += 1

    def surface_destroyed(self, host_generation: int, surface: str):
        if host_generation != self.host_generation:
            return
        if self.current_surface != surface:
            return
        self.current_surface = None
        self.surface_generation += 1
        self.surface_refresh_pending = False

    def complete_collection(self):
        self.collection_active = False
        self.report_present = True
        self.surface_refresh_pending = False


state = DriverSurfaceState()
state.surface_created(0, 'system-A')
if state.full_collections != 1 or state.current_surface != 'system-A':
    raise SystemExit('FAIL initial Surface collection state')
state.complete_collection()
state.surface_created(0, 'system-A')
if state.full_collections != 1 or state.surface_refreshes != 0:
    raise SystemExit('FAIL duplicate Surface callback state')
state.request_driver_change(changes_driver=True)
if state.full_collections != 1 or state.current_surface is not None or not state.driver_rebind_pending or state.host_generation != 1:
    raise SystemExit('FAIL driver-change invalidation state')
state.surface_created(0, 'system-late')
if state.current_surface is not None or state.full_collections != 1:
    raise SystemExit('FAIL stale old-host create callback state')
state.surface_created(1, 'turnip-A')
if state.full_collections != 2 or state.current_surface != 'turnip-A' or state.driver_rebind_pending:
    raise SystemExit('FAIL driver-bound Surface rebind state')
state.surface_destroyed(0, 'system-A')
if state.current_surface != 'turnip-A':
    raise SystemExit('FAIL stale old-host destroy callback state')
state.complete_collection()
state.request_driver_change(changes_driver=False, force=False)
if state.host_generation != 1 or state.current_surface != 'turnip-A':
    raise SystemExit('FAIL unchanged-driver false-positive control')
state.request_driver_change(changes_driver=False, force=True)
if state.host_generation != 2 or not state.driver_rebind_pending or state.current_surface is not None:
    raise SystemExit('FAIL forced same-mode package replacement state')
state.surface_created(2, 'turnip-B')
if state.full_collections != 3 or state.current_surface != 'turnip-B':
    raise SystemExit('FAIL forced package Surface rebind collection state')
state.complete_collection()
state.surface_destroyed(2, 'turnip-B')
state.surface_created(2, 'turnip-C')
if state.full_collections != 3 or state.surface_refreshes != 1 or state.current_surface != 'turnip-C':
    raise SystemExit('FAIL ordinary Surface recreation false-positive control')
state.collection_active = True
state.surface_created(2, 'turnip-D')
if state.full_collections != 3 or state.surface_refreshes != 1 or not state.surface_refresh_pending:
    raise SystemExit('FAIL collection-time Surface deferral state')
print('PASS VulkanScope 0.41.43 driver/Surface rebind state machine')
