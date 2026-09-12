class Transient:
    def __init__(self):
        self.state = 0
        self.until = None
    def trigger(self, ok, now):
        if self.state != 0:
            return False
        self.state = 2 if ok else 3
        self.until = now + 3000
        return True
    def tick(self, now):
        if self.until is not None and now >= self.until:
            self.state = 0
            self.until = None

class Filters:
    def __init__(self):
        self.index = 0
    @property
    def all_enabled(self):
        return self.index == 0
    def toggle_all(self, enabled):
        self.index = 0 if enabled else 1
    def choose(self, index):
        if not self.all_enabled:
            self.index = index

x = Transient()
assert x.trigger(True, 1000)
assert x.state == 2
assert not x.trigger(False, 1500)
x.tick(3999)
assert x.state == 2
x.tick(4000)
assert x.state == 0
assert x.trigger(False, 5000)
assert x.state == 3
x.tick(8000)
assert x.state == 0
f = Filters()
assert f.all_enabled
f.choose(2)
assert f.index == 0
f.toggle_all(False)
assert f.index == 1
f.choose(2)
assert f.index == 2
f.toggle_all(True)
assert f.index == 0
print('PASS VulkanScope 1.2.0 transient/filter state machine')
