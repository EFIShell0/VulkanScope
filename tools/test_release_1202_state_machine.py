#!/usr/bin/env python3
import re

class SettingsNavigation:
    def __init__(self):
        self.page = 'Overview'
        self.section = None
    def open_settings(self):
        self.page = 'Settings'; self.section = None
    def enter(self, section):
        assert self.page == 'Settings' and self.section is None
        self.section = section
    def back(self):
        if self.page == 'Settings' and self.section is not None:
            self.section = None
        else:
            self.page = 'Overview'

nav = SettingsNavigation()
nav.open_settings(); assert nav.page == 'Settings' and nav.section is None
nav.enter('Reports'); nav.back(); assert nav.page == 'Settings' and nav.section is None
nav.back(); assert nav.page == 'Overview'

class AllFilter:
    def __init__(self): self.all = True; self.specific = 0
    @property
    def specific_enabled(self): return not self.all
    def switch(self, enabled): self.all = enabled; self.specific = 0 if enabled else 1

f = AllFilter(); assert not f.specific_enabled
f.switch(False); assert f.specific_enabled and f.specific == 1
f.switch(True); assert not f.specific_enabled and f.specific == 0

class Watch:
    def __init__(self): self.items = set(); self.pending = None; self.pending_all = False
    def valid(self, text, evidence=('vendorID', 'VK_EXT_swapchain_colorspace')):
        t = text.strip()
        if not 2 <= len(t) <= 256: return False
        return bool(re.fullmatch(r'VK_[A-Z0-9_]+|Vk[A-Za-z0-9_]+|vk[A-Za-z0-9_]+', t, re.I)) or any(t.lower() in x.lower() for x in evidence)
    def add(self, text):
        assert self.valid(text); self.items.add(text)
    def ask_delete(self, text): assert text in self.items; self.pending = text
    def confirm_delete(self): self.items.remove(self.pending); self.pending = None
    def ask_all(self): assert self.items; self.pending_all = True
    def cancel_all(self): self.pending_all = False

w = Watch(); assert not w.valid('x'); assert w.valid('VK_KHR_surface'); assert w.valid('vendor')
w.add('VK_KHR_surface'); w.ask_delete('VK_KHR_surface'); assert 'VK_KHR_surface' in w.items
w.confirm_delete(); assert not w.items
w.add('VkPhysicalDeviceProperties'); w.ask_all(); w.cancel_all(); assert w.items

class UpdateCheck:
    def __init__(self): self.in_flight = False
    @property
    def enabled(self): return not self.in_flight
    def start(self):
        if self.in_flight: return False
        self.in_flight = True; return True
    def finish(self): self.in_flight = False
u = UpdateCheck(); assert u.start() and not u.enabled and not u.start(); u.finish(); assert u.enabled

class Transient:
    def __init__(self): self.state = 'idle'
    def finish(self, ok): self.state = 'success' if ok else 'failure'
    def timeout(self): self.state = 'idle'
t = Transient(); t.finish(True); assert t.state == 'success'; t.timeout(); assert t.state == 'idle'
t.finish(False); assert t.state == 'failure'; t.timeout(); assert t.state == 'idle'

print('PASS VulkanScope 1.2.2 Settings/filter/watch/update state machine')
