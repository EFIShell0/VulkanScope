#!/usr/bin/env python3

class ScanCoordinator:
    def __init__(self):
        self.generation = 0
        self.visible = False
        self.loading = False
        self.path = ''
        self.entries = []

    def open(self, path):
        self.generation += 1
        self.visible = True
        self.loading = True
        self.path = path
        self.entries = []
        return self.generation

    def navigate(self, path):
        if not self.visible or self.loading:
            return None
        self.generation += 1
        self.loading = True
        self.path = path
        self.entries = []
        return self.generation

    def complete(self, generation, entries):
        if generation != self.generation or not self.visible:
            return False
        self.loading = False
        self.entries = list(entries)
        return True

    def fail(self, generation):
        if generation != self.generation or not self.visible:
            return False
        self.loading = False
        self.entries = []
        return True

    def close(self):
        self.generation += 1
        self.visible = False
        self.loading = False
        self.entries = []

state = ScanCoordinator()
g1 = state.open('/storage/emulated/0')
assert state.navigate('/storage/emulated/0/Download') is None
assert state.complete(g1, ['Download'])
g2 = state.navigate('/storage/emulated/0/Download')
assert g2 is not None and state.entries == [] and state.loading
state.close()
assert not state.complete(g2, ['stale.zip'])
assert state.entries == [] and not state.visible

g3 = state.open('/storage/emulated/0')
assert state.complete(g3, ['A', 'B'])
g4 = state.navigate('/storage/emulated/0/A')
assert g4 is not None and state.entries == []
assert state.fail(g4) and state.entries == [] and not state.loading

def responsive_header(font_scale, width_dp):
    return 'STACKED' if font_scale >= 1.3 or width_dp < 360 else 'ROW'

assert responsive_header(1.0, 411) == 'ROW'
assert responsive_header(1.3, 411) == 'STACKED'
assert responsive_header(1.0, 359) == 'STACKED'

def click_model(enabled):
    return ('CLICK_ACTION', 'MATERIAL_INDICATION') if enabled else ('DISABLED', 'NO_ACTION')

assert click_model(True) == ('CLICK_ACTION', 'MATERIAL_INDICATION')
assert click_model(False) == ('DISABLED', 'NO_ACTION')



def import_outcome(event):
    if event == "cancel":
        return "RETHROW_CANCELLATION"
    if event == "package_error":
        return "USER_VISIBLE_FAILURE"
    return "SUCCESS"

assert import_outcome("cancel") == "RETHROW_CANCELLATION"
assert import_outcome("package_error") == "USER_VISIBLE_FAILURE"
assert import_outcome("success") == "SUCCESS"

print('PASS VulkanScope 1.3.5 scan-lifecycle/responsive-interaction state machine')
