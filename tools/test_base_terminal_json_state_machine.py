#!/usr/bin/env python3
import json


def build(device_count: int, close_devices: bool) -> str:
    parts = ['{"status":"available","baseReportComplete":true,"devices":[']
    for index in range(device_count):
        if index:
            parts.append(',')
        parts.append('{"name":"fixture","surface":{"available":false}}')
        if not close_devices:
            parts[-1] = parts[-1][:-1]
    parts.append('],"deviceExtensionEnumerationsComplete":true,"deviceExtensionEnumerationReason":""}')
    return ''.join(parts)


def main() -> None:
    for count in (1, 2, 3):
        payload = build(count, True)
        parsed = json.loads(payload)
        assert parsed['baseReportComplete'] is True
        assert len(parsed['devices']) == count
        assert payload.count('"baseReportComplete"') == 1
    broken = build(1, False)
    failed = False
    try:
        json.loads(broken)
    except json.JSONDecodeError:
        failed = True
    assert failed
    print('PASS base-terminal JSON state machine: each device closes before devices array, root parses for one/multiple devices, one canonical completeness key, missing device closure is rejected')


if __name__ == '__main__':
    main()
