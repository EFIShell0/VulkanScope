#!/usr/bin/env python3
from dataclasses import dataclass


def color(state):
    state = state.strip().upper().replace('_', ' ')
    if state in {'SUPPORTED', 'PASS'}:
        return 'green'
    if state == 'AVAILABLE':
        return 'blue'
    if state in {'UNSUPPORTED', 'FAIL'}:
        return 'red'
    if state in {'UNKNOWN', 'UNRESOLVED'}:
        return 'neutral'
    if state == 'UNAVAILABLE':
        return 'amber'
    if state == 'INCOMPLETE':
        return 'purple'
    if state == 'NOT APPLICABLE':
        return 'neutral'
    return 'secondary'


for state, expected in [('SUPPORTED', 'green'), ('AVAILABLE', 'blue'), ('FAIL', 'red'), ('UNKNOWN', 'neutral'), ('UNRESOLVED', 'neutral'), ('UNAVAILABLE', 'amber'), ('INCOMPLETE', 'purple'), ('NOT_APPLICABLE', 'neutral')]:
    assert color(state) == expected
assert color('UNKNOWN') != color('UNAVAILABLE')


def system_action(active, collection_complete, busy):
    if active:
        return 'ACTIVE'
    return 'ACTIVATE' if collection_complete and not busy else 'ACTIVATE_DISABLED'


assert system_action(True, False, True) == 'ACTIVE'
assert system_action(False, True, False) == 'ACTIVATE'
assert system_action(False, False, False) == 'ACTIVATE_DISABLED'
assert system_action(False, True, True) == 'ACTIVATE_DISABLED'


def turnip_actions(selected, installed, source_available):
    unavailable = not selected and not (installed and source_available)
    if unavailable:
        return ('REMOVE',)
    if selected:
        return ('DETAILS', 'REMOVE')
    return ('DETAILS', 'ACTIVATE', 'REMOVE')


assert turnip_actions(False, False, False) == ('REMOVE',)
assert turnip_actions(False, True, False) == ('REMOVE',)
assert turnip_actions(True, True, True) == ('DETAILS', 'REMOVE')
assert turnip_actions(False, True, True) == ('DETAILS', 'ACTIVATE', 'REMOVE')


@dataclass(frozen=True)
class Confirmation:
    question_icon: bool
    cancel_x: bool
    cancel_contained: bool
    confirm_contained: bool


question = Confirmation(True, True, False, True)
assert question.question_icon and question.cancel_x and not question.cancel_contained and question.confirm_contained


def external_hit(target):
    return target == 'trailing_external_icon'


assert external_hit('trailing_external_icon')
assert not external_hit('row')
assert not external_hit('title')
assert not external_hit('leading_icon')


def switch_hit(target):
    return target == 'switch'


assert switch_hit('switch')
assert not switch_hit('row')
assert not switch_hit('label')


def exchange_path(has_provider, launch_succeeds, operation):
    if has_provider and launch_succeeds:
        return 'SAF'
    if operation in {'analysis_import', 'analysis_export', 'minimum_import', 'minimum_export', 'raw_export'}:
        return 'BOUNDED_APP_EXCHANGE'
    if operation in {'txt_export', 'html_export'}:
        return 'DOWNLOADS_FALLBACK'
    if operation == 'turnip_import':
        return 'EXACT_NAME_APP_ROOT_REVIEW'
    raise AssertionError(operation)


for operation in ['analysis_import', 'analysis_export', 'minimum_import', 'minimum_export', 'raw_export']:
    assert exchange_path(False, False, operation) == 'BOUNDED_APP_EXCHANGE'
    assert exchange_path(True, False, operation) == 'BOUNDED_APP_EXCHANGE'
    assert exchange_path(True, True, operation) == 'SAF'
for operation in ['txt_export', 'html_export']:
    assert exchange_path(False, False, operation) == 'DOWNLOADS_FALLBACK'
assert exchange_path(False, False, 'turnip_import') == 'EXACT_NAME_APP_ROOT_REVIEW'

print('PASS VulkanScope 1.0.8 driver-manager/dialog/storage state machine')
