#!/usr/bin/env python3
import re

HEX64 = 'a' * 64

def resolve_watch(entries, raw):
    value = raw.strip()
    if not 2 <= len(value) <= 256:
        return None
    direct = value[:256]
    if re.fullmatch(r'(VK_[A-Z0-9_]+|Vk[A-Za-z0-9_]+|vk[A-Za-z0-9_]+)', direct, re.I):
        return direct
    for key, entry_value in entries.items():
        if value.lower() in key.lower() or value.lower() in entry_value.lower():
            return key[:256] or None
    return None

def submit_model(network=True, complete=True, devices=1, collection_error=False, serialized=True, payload_bytes=1024, http_code=201, response_id=HEX64, network_exception=False):
    if not network:
        return False, None, 'network-validation', True
    if not complete or devices == 0 or collection_error:
        return False, None, 'report-validation', True
    if not serialized:
        return False, None, 'serialization', True
    if payload_bytes > 2 * 1024 * 1024:
        return False, None, 'payload-validation', True
    if network_exception:
        return False, None, 'network-request', True
    if not 200 <= http_code < 300:
        return False, None, 'http-response', True
    if not re.fullmatch(r'[a-f0-9]{64}', response_id or ''):
        return False, None, 'response-validation', True
    return True, response_id, 'success', False

entries = {'extension/instance/VK_EXT_swapchain_colorspace': 'present · spec 5', 'Vendor ID': '0x5143'}
assert resolve_watch(entries, 'swapchain') == 'extension/instance/VK_EXT_swapchain_colorspace'
assert resolve_watch(entries, 'VK_KHR_surface') == 'VK_KHR_surface'
assert resolve_watch(entries, 'x') is None
assert submit_model() == (True, HEX64, 'success', False)
assert submit_model(response_id='abcd') == (False, None, 'response-validation', True)
assert submit_model(http_code=409) == (False, None, 'http-response', True)
assert submit_model(network=False) == (False, None, 'network-validation', True)
assert submit_model(serialized=False) == (False, None, 'serialization', True)
assert submit_model(payload_bytes=2 * 1024 * 1024 + 1) == (False, None, 'payload-validation', True)
assert submit_model(network_exception=True) == (False, None, 'network-request', True)
print('PASS VulkanScope 1.2.3 watched-resolution and Database-result state machine')
