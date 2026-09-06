#!/usr/bin/env python3
import argparse
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', type=Path, default=Path(__file__).resolve().parents[1])
parser.add_argument('--skip-version', action='store_true')
args = parser.parse_args()
root = args.root.resolve()
errors = []
main = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
rules = (root / 'rules/PROJECT_RULES.md').read_text(encoding='utf-8')

def require(condition, message):
    if not condition:
        errors.append(message)

if not args.skip_version:
    require(any(f'versionCode = {code}' in gradle and f'versionName = "{name}"' in gradle for name, code in [('0.80.14', 814), ('0.80.15', 815)]), 'release identity is not a retained 0.80.14+ compile-compatible identity')
require('import androidx.compose.foundation.layout.calculateBottomPadding' not in main, 'invalid calculateBottomPadding package import remains')
require('import androidx.compose.foundation.layout.calculateTopPadding' not in main, 'invalid calculateTopPadding package import remains')
require('navigationPadding.calculateTopPadding()' in main, 'PaddingValues calculateTopPadding member call was removed')
require('navigationPadding.calculateBottomPadding()' in main, 'PaddingValues calculateBottomPadding member call was removed')
require('## Release 0.80.14 PaddingValues compile-regression requirements' in rules, 'PROJECT_RULES 0.80.14 compile contract is missing')
require((root / 'rules/0.80.14_PADDINGVALUES_COMPILE_AUDIT.md').is_file(), '0.80.14 compile audit is missing')
require((root / 'tests/golden/0.80.13_regression_contract.json').is_file(), '0.80.13 regression contract is missing')
if errors:
    for error in errors:
        print(f'FAIL: {error}')
    raise SystemExit(1)
print('PASS VulkanScope 0.80.14 PaddingValues compile-regression contract')
