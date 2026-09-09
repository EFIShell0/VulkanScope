#!/usr/bin/env python3


def chevron_activation(target):
    return target == 'trailing-icon'


def system_details(mode, current_complete, current_summary, cached_summary):
    if mode == 'SYSTEM' and current_complete and current_summary:
        return current_summary, 'current System report'
    if cached_summary:
        return cached_summary, 'last completed System report'
    return None, 'unavailable'


def database_state(validated, loading, report_id):
    return {
        'input_enabled': validated,
        'fetch_enabled': validated and not loading and len(report_id) == 64 and all(c in '0123456789abcdef' for c in report_id),
        'reason': None if validated else 'amber-offline'
    }


def fallback(kind, token='GPU'):
    roots = ['Android/data/pkg/files/Documents', 'Android/data/pkg/files/Download', 'files/analysis_exchange']
    if kind == 'analysis-import': return 'VulkanScope-*-analysis.json', roots
    if kind == 'analysis-export': return f'VulkanScope-{token}-analysis.json', roots
    if kind == 'minimum-import': return 'VulkanScope-*-minimum.json', roots
    if kind == 'minimum-export': return f'VulkanScope-{token}-minimum.json', roots
    if kind == 'technical-export': return f'VulkanScope-{token}-technicalReport.json', roots
    if kind == 'turnip-import': return 'turnip_01.zip..turnip_10.zip', ['files/turnip_imports', 'Android/data/pkg/files', 'Android/data/pkg/files/Download', 'Android/data/pkg/files/Documents']
    if kind == 'txt-export': return f'VulkanScope-{token}-report.txt', ['public Download']
    if kind == 'html-export': return f'VulkanScope-{token}-report.html', ['public Download']
    raise AssertionError(kind)


assert chevron_activation('trailing-icon')
assert not chevron_activation('body')
assert not chevron_activation('title')
summary, source = system_details('SYSTEM', True, {'driver': 'system'}, {'driver': 'old'})
assert summary == {'driver': 'system'} and source == 'current System report'
summary, source = system_details('TURNIP', True, {'driver': 'turnip'}, {'driver': 'system cached'})
assert summary == {'driver': 'system cached'} and source == 'last completed System report'
summary, source = system_details('TURNIP', True, {'driver': 'turnip'}, None)
assert summary is None and source == 'unavailable'
offline = database_state(False, False, 'a' * 64)
assert offline == {'input_enabled': False, 'fetch_enabled': False, 'reason': 'amber-offline'}
online_bad = database_state(True, False, 'a' * 63)
assert online_bad['input_enabled'] and not online_bad['fetch_enabled']
online_ok = database_state(True, False, 'a' * 64)
assert online_ok['input_enabled'] and online_ok['fetch_enabled']
for kind in ['analysis-import', 'analysis-export', 'minimum-import', 'minimum-export', 'technical-export', 'turnip-import', 'txt-export', 'html-export']:
    name, roots = fallback(kind)
    assert name and roots
assert fallback('turnip-import')[0] == 'turnip_01.zip..turnip_10.zip'
print('PASS VulkanScope 1.0.9 action/System-evidence/offline/fallback state machine')
