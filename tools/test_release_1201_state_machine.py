class UpdateCheck:
    def __init__(self):
        self.in_flight = False
        self.result = None

    @property
    def enabled(self):
        return not self.in_flight

    def start(self):
        if self.in_flight:
            return False
        self.in_flight = True
        self.result = None
        return True

    def complete(self, result):
        assert self.in_flight
        self.result = result
        self.in_flight = False

    def cancel(self):
        assert self.in_flight
        self.in_flight = False

check = UpdateCheck()
assert check.enabled
assert check.start()
assert not check.enabled
assert not check.start()
check.complete('up-to-date')
assert check.enabled and check.result == 'up-to-date'
assert check.start()
assert not check.enabled
check.cancel()
assert check.enabled
print('PASS VulkanScope 1.2.1 update-check state machine')
