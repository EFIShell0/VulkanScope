#!/usr/bin/env python3
import argparse
import re
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', type=Path, default=Path(__file__).resolve().parents[1])
parser.add_argument('--skip-version', action='store_true')
parser.add_argument('--skip-release-records', action='store_true')
args = parser.parse_args()
root = args.root.resolve()
errors = []
main_path = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
gradle_path = root / 'app/build.gradle.kts'
rules_path = root / 'rules/PROJECT_RULES.md'
audit_path = root / 'rules/1.0.10_PADDINGVALUES_IMPORT_COMPILE_AUDIT.md'
for path in [main_path, gradle_path]:
    if not path.is_file():
        errors.append(f'missing required file: {path.relative_to(root)}')
if errors:
    for error in errors:
        print(f'FAIL: {error}')
    raise SystemExit(1)
main = main_path.read_text(encoding='utf-8')
gradle = gradle_path.read_text(encoding='utf-8')

def require(condition, message):
    if not condition:
        errors.append(message)

if not args.skip_version:
    version_match = re.search(r'versionName\s*=\s*"(\d+)\.(\d+)\.(\d+)"', gradle)
    code_match = re.search(r'versionCode\s*=\s*(\d+)', gradle)
    cv = tuple(map(int, version_match.groups())) if version_match else (0, 0, 0)
    expected = cv[0] * 1000 + cv[1] * 100 + cv[2]
    require(version_match is not None and code_match is not None and cv >= (1, 0, 10) and int(code_match.group(1)) == expected, 'retained 1.0.10+ semantic release identity missing')
required_import = 'import androidx.compose.foundation.layout.PaddingValues'
require(main.count(required_import) == 1, 'androidx.compose.foundation.layout.PaddingValues import must exist exactly once')
require('contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)' in main, 'contained icon/text button contentPadding expression drifted')
block_match = re.search(r'private fun ExpressiveContainedIconTextButton\(.*?\n\}', main, re.S)
require(block_match is not None, 'ExpressiveContainedIconTextButton could not be isolated')
if block_match is not None:
    block = block_match.group(0)
    require('contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)' in block, 'PaddingValues use is not retained in ExpressiveContainedIconTextButton')
    require('modifier = modifier.heightIn(min = 48.dp)' in block, 'contained icon/text button minimum touch geometry drifted')
if not args.skip_release_records:
    require(rules_path.is_file(), 'PROJECT_RULES.md is missing')
    if rules_path.is_file():
        rules = rules_path.read_text(encoding='utf-8')
        require('## Release 1.0.10 PaddingValues import compile-regression requirements' in rules, 'PROJECT_RULES 1.0.10 compile-regression contract is missing')
    require(audit_path.is_file(), '1.0.10 compile audit is missing')
    require((root / 'tests/golden/1.0.9_regression_contract.json').is_file(), '1.0.9 immutable regression contract is missing')
if errors:
    for error in errors:
        print(f'FAIL: {error}')
    raise SystemExit(1)
print('PASS VulkanScope 1.0.10 PaddingValues import compile-regression contract')
