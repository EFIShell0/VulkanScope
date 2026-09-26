#!/usr/bin/env python3
import json

MAX_TIMING_BYTES = 16 * 1024

def elapsed_ms(start_ns, end_ns):
    return max(0, (end_ns - start_ns) // 1_000_000)

def parse_telemetry(text):
    raw = text.encode('utf-8')
    if not raw or len(raw) > MAX_TIMING_BYTES:
        return {}
    obj = json.loads(text)
    keys = ('dispatchToWorkerMs', 'libraryLoadMs', 'nativeCallMs', 'terminalValidationMs', 'servicePreTerminalMs')
    result = {}
    for key in keys:
        value = obj.get(key)
        if isinstance(value, int) and value >= 0:
            result[key] = value
    return result

def timing_path_valid(cache_root, result_path, timing_path):
    prefix = cache_root.rstrip('/') + '/'
    return result_path.startswith(prefix) and timing_path.startswith(prefix) and timing_path == result_path + '.timing'

assert elapsed_ms(10, 9) == 0
assert elapsed_ms(0, 3_999_999) == 3
payload = json.dumps({'group':'base','dispatchToWorkerMs':4,'libraryLoadMs':2,'nativeCallMs':113,'terminalValidationMs':1,'servicePreTerminalMs':121})
parsed = parse_telemetry(payload)
assert parsed['nativeCallMs'] == 113 and parsed['servicePreTerminalMs'] == 121
assert parse_telemetry(json.dumps({'nativeCallMs':-1})) == {}
assert parse_telemetry('x' * (MAX_TIMING_BYTES + 1)) == {}
assert timing_path_valid('/data/user/0/app/cache', '/data/user/0/app/cache/vulkan_probe_a.json', '/data/user/0/app/cache/vulkan_probe_a.json.timing')
assert not timing_path_valid('/data/user/0/app/cache', '/data/user/0/app/cache/vulkan_probe_a.json', '/sdcard/timing.json')
assert not timing_path_valid('/data/user/0/app/cache', '/data/user/0/app/cache/vulkan_probe_a.json', '/data/user/0/app/cache/other.timing')
print('PASS VulkanScope 2.0.0 timing state-machine model')
