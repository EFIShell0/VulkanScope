#!/usr/bin/env python3
import argparse
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', default=str(Path(__file__).resolve().parents[1]))
args = parser.parse_args()
root = Path(args.root).resolve()
cpp = (root / 'app/src/main/cpp/vulkanscope.cpp').read_text(encoding='utf-8')
errors = []
checks = {
    'maintenance5 A1B5/A8 format eligibility': 'if (value >= 1000470000 && value <= 1000470001) return apiVersionAtLeast(apiVersion, 1, 4) || has("VK_KHR_maintenance5");',
    'tensor aliasing canonical image-layout name': 'if (value == VK_IMAGE_LAYOUT_TENSOR_ALIASING_ARM) return "VK_IMAGE_LAYOUT_TENSOR_ALIASING_ARM";',
    'DCI-P3 XYZ presentation semantics': 'presentation engine interprets components as XYZ',
    'legacy Dolby Vision enum semantics': 'Legacy Vulkan Dolby Vision color-space enum · does not signal Dolby Vision metadata',
}
for label, token in checks.items():
    if token not in cpp:
        errors.append(label)
if errors:
    for item in errors:
        print(f'FAIL: missing regression contract: {item}')
    raise SystemExit(1)
print('PASS targeted Vulkan specification regressions: maintenance5 formats, tensor layout, DCI-P3 XYZ semantics, legacy Dolby Vision semantics')
