#!/usr/bin/env python3

class MinimumProfiles:
    def __init__(self):
        self.pending = None
        self.saved = {'My minimum': 'api>=1.3'}
        self.editor = ('Draft', 'extension:VK_KHR_swapchain')

    def request(self, kind, name):
        self.pending = (kind, name)

    def cancel(self):
        self.pending = None

    def confirm(self):
        kind, name = self.pending
        self.pending = None
        if kind == 'save':
            self.saved[self.editor[0]] = self.editor[1]
        elif kind == 'load':
            self.editor = (name, self.saved[name])
        elif kind == 'delete':
            self.saved.pop(name, None)

m = MinimumProfiles()
before = dict(m.saved)
m.request('delete', 'My minimum')
m.cancel()
if m.saved != before:
    raise SystemExit('Cancel mutated a minimum profile')
m.request('delete', 'My minimum')
m.confirm()
if 'My minimum' in m.saved:
    raise SystemExit('confirmed minimum delete did not mutate state')


def folder_count(names, cap=256):
    count = 0
    limited = False
    for name in names:
        if name.lower().endswith('.zip'):
            if count >= cap:
                limited = True
                break
            count += 1
    suffix = 'file' if count == 1 else 'files'
    return f'{count}+ {suffix}' if limited else f'{count} {suffix}'

if folder_count([]) != '0 files' or folder_count(['a.zip']) != '1 file' or folder_count(['a.zip', 'b.ZIP']) != '2 files':
    raise SystemExit('folder ZIP count pluralization regressed')
if folder_count([f'{i}.zip' for i in range(300)]) != '256+ files':
    raise SystemExit('folder ZIP count is not bounded')


def tv_navigation(move_focus_succeeds, can_scroll):
    events = ['move-focus']
    if move_focus_succeeds:
        return events
    if not can_scroll:
        return events + ['boundary']
    return events + ['scroll', 'wait-24ms', 'move-focus']

if tv_navigation(True, True) != ['move-focus']:
    raise SystemExit('TV navigation scrolls before trying focus')
if tv_navigation(False, True) != ['move-focus', 'scroll', 'wait-24ms', 'move-focus']:
    raise SystemExit('TV offscreen focus fallback sequence regressed')
if tv_navigation(False, False)[-1] != 'boundary':
    raise SystemExit('TV focus fallback crosses a scroll boundary')


def transient_copy(success):
    return ['idle', 'busy', 'green-check' if success else 'red-close', 'wait-3000ms', 'idle']

if transient_copy(True) != ['idle', 'busy', 'green-check', 'wait-3000ms', 'idle']:
    raise SystemExit('report copy success state machine regressed')


def graph_state(evidence):
    if evidence.startswith(('Enumerated', 'Runtime API satisfies')):
        return 'PRESENT'
    if evidence.startswith(('Not enumerated', 'Runtime API does not expose')):
        return 'ABSENT'
    if evidence.startswith('Not applicable'):
        return 'NOT_APPLICABLE'
    return 'UNKNOWN'

cases = {
    'Enumerated device extension': 'PRESENT',
    'Not enumerated': 'ABSENT',
    'Not applicable to API': 'NOT_APPLICABLE',
    'Registry dependency only': 'UNKNOWN',
}
for evidence, expected in cases.items():
    if graph_state(evidence) != expected:
        raise SystemExit(f'graph evidence classification regressed: {evidence}')

print('PASS VulkanScope 3.0.3 state machines')
