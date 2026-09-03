#!/usr/bin/env python3
import json
import re
from pathlib import Path

root = Path(__file__).resolve().parents[1]
cmake = (root / "app/src/main/cpp/CMakeLists.txt").read_text(encoding="utf-8")
lock = json.loads((root / "registry/registry_lock.json").read_text(encoding="utf-8"))
errors = []
if f'GIT_TAG {lock["headerCommit"]}' not in cmake:
    errors.append("Vulkan-Headers exact commit pin is missing")
if 'file(SHA256 "${VULKANSCOPE_VULKAN_XML}"' in cmake:
    errors.append("fetched Vulkan-Headers registry is still compared to the Vulkan-Docs byte hash")
if 'file(SHA256 "${VULKANSCOPE_LOCKED_VULKAN_XML}"' not in cmake:
    errors.append("bundled canonical Vulkan-Docs registry snapshot is not SHA-locked")
if lock["registrySha256"] not in cmake:
    errors.append("bundled canonical registry SHA does not match registry_lock.json")
if f'VK_HEADER_VERSION[ \\t]+{lock["headerVersion"]}' not in cmake:
    errors.append("fetched Vulkan header semantic version gate is missing")
if f'<name>VK_HEADER_VERSION</name> {lock["headerVersion"]}' not in cmake:
    errors.append("fetched Vulkan-Headers registry semantic version gate is missing")
if 'VK_NV_private_data_base_handle' not in cmake:
    errors.append("Vulkan 1.4.361 registry sentinel gate is missing")
if 'VULKANSCOPE_PROJECT_ROOT' not in cmake or 'registry/upstream/vk.xml' not in cmake:
    errors.append("bundled canonical registry path is not anchored to the project root")
if errors:
    for error in errors:
        print(f"FAIL: {error}")
    raise SystemExit(1)
print("PASS CMake Vulkan 1.4.361 lock: canonical Vulkan-Docs snapshot byte-locked; fetched Vulkan-Headers exact-commit and semantic-version locked")
