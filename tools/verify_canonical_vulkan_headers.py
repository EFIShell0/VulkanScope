#!/usr/bin/env python3
import json
import re
import sys
from pathlib import Path

root = Path(__file__).resolve().parents[1]
lock = json.loads((root / 'registry/registry_lock.json').read_text(encoding='utf-8'))
if len(sys.argv) != 2:
    print('usage: verify_canonical_vulkan_headers.py <vulkan_core.h>')
    raise SystemExit(2)
text = Path(sys.argv[1]).read_text(encoding='utf-8', errors='ignore')
match = re.search(r'#define\s+VK_HEADER_VERSION\s+([0-9]+)', text)
if not match or int(match.group(1)) != int(lock['headerVersion']):
    print(f'FAIL: expected canonical Vulkan-Headers {lock["headerTag"]}')
    raise SystemExit(1)
required = ['VkPhysicalDeviceProperties2', 'VkPhysicalDeviceFeatures2', 'VkPhysicalDeviceVulkan14Properties', 'VkPhysicalDeviceVulkan14Features']
missing = [name for name in required if ('struct ' + name) not in text and ('typedef struct ' + name) not in text]
if missing:
    print('FAIL: missing required canonical structures: ' + ', '.join(missing))
    raise SystemExit(1)
print(f'PASS canonical Vulkan-Headers {lock["headerTag"]} commit={lock["headerCommit"]}')
