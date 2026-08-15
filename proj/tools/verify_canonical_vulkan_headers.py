#!/usr/bin/env python3
import re, sys
from pathlib import Path

if len(sys.argv) != 2:
    print("usage: verify_canonical_vulkan_headers.py <vulkan_core.h>")
    raise SystemExit(2)
text = Path(sys.argv[1]).read_text(encoding="utf-8", errors="ignore")
m = re.search(r"#define\s+VK_HEADER_VERSION\s+([0-9]+)", text)
if not m or int(m.group(1)) != 357:
    print("FAIL: expected canonical Vulkan-Headers 1.4.357")
    raise SystemExit(1)
required = ["VkPhysicalDeviceProperties2", "VkPhysicalDeviceFeatures2", "VkPhysicalDeviceVulkan14Properties", "VkPhysicalDeviceVulkan14Features"]
missing = [x for x in required if ("struct " + x) not in text and ("typedef struct " + x) not in text]
if missing:
    print("FAIL: missing required canonical structures:", ", ".join(missing))
    raise SystemExit(1)
print("PASS: canonical Vulkan-Headers 1.4.357")
