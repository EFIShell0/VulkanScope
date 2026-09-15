#!/usr/bin/env python3
from enum import Enum, auto

class Phase(Enum):
    CONNECTING = auto()
    DOWNLOADING = auto()
    PAUSED = auto()
    VERIFYING = auto()
    COMPLETED = auto()
    CANCELED = auto()
    FAILED = auto()

terminal = {Phase.COMPLETED, Phase.CANCELED, Phase.FAILED}
active = {Phase.CONNECTING, Phase.DOWNLOADING, Phase.PAUSED, Phase.VERIFYING}
phase = Phase.DOWNLOADING
paused = False
cancel_question = False
installer_opened = False
log = []

def append(message):
    global log
    log = (log + [message])[-120:]

def pause():
    global phase, paused
    assert phase in {Phase.CONNECTING, Phase.DOWNLOADING}
    paused = True
    phase = Phase.PAUSED
    append('Download paused.')

def resume():
    global phase, paused
    assert phase is Phase.PAUSED
    paused = False
    phase = Phase.DOWNLOADING
    append('Download resumed.')

def request_cancel():
    global cancel_question
    if phase is not Phase.PAUSED:
        pause()
    cancel_question = True

def dismiss_cancel():
    global cancel_question
    cancel_question = False
    resume()

def confirm_cancel():
    global phase, cancel_question
    assert phase is Phase.PAUSED and cancel_question
    cancel_question = False
    phase = Phase.CANCELED
    append('Download canceled by user.')

request_cancel()
assert phase is Phase.PAUSED and paused and cancel_question
dismiss_cancel()
assert phase is Phase.DOWNLOADING and not paused and not cancel_question
request_cancel()
confirm_cancel()
assert phase is Phase.CANCELED and phase in terminal and log[-1] == 'Download canceled by user.'
assert all(p not in terminal for p in active)
for i in range(180):
    append(f'line-{i}')
assert len(log) == 120 and log[0] == 'line-60' and log[-1] == 'line-179'
phase = Phase.VERIFYING
assert not installer_opened
phase = Phase.COMPLETED
assert not installer_opened
installer_opened = True
assert installer_opened and phase is Phase.COMPLETED
print('PASS VulkanScope 1.3.2 pause/resume/cancel/terminal/log state model')
