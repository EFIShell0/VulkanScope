#!/usr/bin/env python3
import re

unavailable = {'', 'unknown', 'unknown gpu', 'unavailable', 'not available', 'not reported', 'not applicable', 'n/a'}


def meaningful(value):
    return value.strip().lower() not in unavailable


def success(report):
    if report is None:
        return False
    if report['error'] is not None or not report['base_complete'] or not report['devices']:
        return False
    for device in report['devices']:
        api = device.get('api', '').strip()
        if (
            meaningful(device.get('name', ''))
            or re.fullmatch(r'\d+\.\d+(?:\.\d+)?', api) is not None
            or device.get('vendor_raw', 0) != 0
            or device.get('device_raw', 0) != 0
            or any(device.get(key, 0) > 0 for key in ('extensions', 'features', 'queues', 'heaps', 'memory_types', 'formats', 'limits'))
        ):
            return True
    return False


fixtures = [
    (None, False),
    ({'error': 'timeout', 'base_complete': False, 'devices': []}, False),
    ({'error': None, 'base_complete': False, 'devices': [{'name': 'Adreno', 'api': '1.4', 'vendor_raw': 0x5143, 'device_raw': 0x1234, 'extensions': 100}]}, False),
    ({'error': None, 'base_complete': True, 'devices': []}, False),
    ({'error': None, 'base_complete': True, 'devices': [{'name': 'Unknown', 'api': 'Unknown', 'vendor_raw': 0, 'device_raw': 0, 'extensions': 0}]}, False),
    ({'error': None, 'base_complete': True, 'devices': [{'name': 'Unknown GPU', 'api': 'Unavailable', 'vendor_raw': 0, 'device_raw': 0, 'extensions': 0}]}, False),
    ({'error': None, 'base_complete': True, 'devices': [{'name': 'Unknown', 'api': 'Unknown', 'vendor_raw': 0, 'device_raw': 0, 'features': 1}]}, True),
    ({'error': None, 'base_complete': True, 'devices': [{'name': 'Adreno (TM) 830', 'api': '1.4.359', 'vendor_raw': 0x5143, 'device_raw': 0x4300, 'extensions': 176}]}, True),
]
for index, (report, expected) in enumerate(fixtures):
    actual = success(report)
    if actual != expected:
        raise SystemExit(f'collection outcome fixture {index} expected {expected} got {actual}')


def arrows(value, maximum):
    return value > 0, value < maximum


for value, maximum, expected in [(0, 100, (False, True)), (50, 100, (True, True)), (100, 100, (True, False)), (0, 0, (False, False))]:
    if arrows(value, maximum) != expected:
        raise SystemExit('scroll boundary state mismatch')

print('PASS 0.80.4 collection outcome and modal scroll-boundary state machine')
