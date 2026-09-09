#!/usr/bin/env python3
import argparse
import re
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', type=Path, default=Path(__file__).resolve().parents[1])
parser.add_argument('--skip-version', action='store_true')
args = parser.parse_args()
root = args.root.resolve()
errors = []
main_path = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
gradle_path = root / 'app/build.gradle.kts'
rules_path = root / 'rules/PROJECT_RULES.md'
audit_path = root / 'rules/1.0.5_KOTLIN_DEFAULT_PARAMETER_COMPILE_FIX_AUDIT.md'
for path in [main_path, gradle_path, rules_path]:
    if not path.is_file():
        errors.append(f'missing required file: {path.relative_to(root)}')
if errors:
    for error in errors:
        print(f'FAIL: {error}')
    raise SystemExit(1)
main = main_path.read_text(encoding='utf-8')
gradle = gradle_path.read_text(encoding='utf-8')
rules = rules_path.read_text(encoding='utf-8')

def require(condition, message):
    if not condition:
        errors.append(message)

if not args.skip_version:
    require(any(f'versionCode = {code}' in gradle and f'versionName = "{name}"' in gradle for name, code in [('1.0.5',1005),('1.0.6',1006),('1.0.7',1007),('1.0.8',1008),('1.0.9',1009),('1.0.10',1010),('1.0.11',1011),('1.0.12',1012),('1.0.13',1013),('1.0.14',1014),('1.0.15',1015),('1.0.16',1016),('1.0.17',1017),('1.0.18',1018)]), 'release identity is not a retained 1.0.5+ identity')
signature = re.search(r'private fun ExpressiveTextButton\(label: String, enabled: Boolean = true, onClick: \(\) -> Unit\)', main)
require(signature is not None, 'ExpressiveTextButton default-parameter signature drifted')
if any(f'versionName = "{name}"' in gradle for name in ['1.0.8', '1.0.9', '1.0.10', '1.0.11', '1.0.12', '1.0.13', '1.0.14', '1.0.15', '1.0.16', '1.0.17', '1.0.18']):
    require(re.search(r'private fun ExpressiveCancelButton\(enabled: Boolean = true, onClick: \(\) -> Unit\)', main) is not None, 'ExpressiveCancelButton default-parameter signature drifted')
    good = 'ExpressiveCancelButton(onClick = onDismiss)'
    bad = 'ExpressiveCancelButton(onDismiss)'
else:
    good = 'ExpressiveTextButton("Cancel", onClick = onDismiss)'
    bad = 'ExpressiveTextButton("Cancel", onDismiss)'
require(main.count(good) == 2, 'both update-dialog Cancel actions must pass onDismiss through the named onClick argument')
require(bad not in main, 'compile-breaking positional onDismiss call remains')
for fn in ['DirectUpdatesConsentDialog', 'UpdateConfirmationDialog']:
    match = re.search(rf'private fun {fn}\(.*?\n\}}', main, re.S)
    require(match is not None, f'{fn} could not be isolated')
    if match is not None:
        require(good in match.group(0), f'{fn} does not use the compile-safe named Cancel callback')
require('## Release 1.0.5 Kotlin default-parameter call-site compile-regression requirements' in rules, 'PROJECT_RULES 1.0.5 compile-regression contract is missing')
require(audit_path.is_file(), '1.0.5 compile-fix audit is missing')
require((root / 'tests/golden/1.0.4_regression_contract.json').is_file(), '1.0.4 immutable regression contract is missing')
if errors:
    for error in errors:
        print(f'FAIL: {error}')
    raise SystemExit(1)
print('PASS VulkanScope 1.0.5 Kotlin default-parameter compile-regression contract')
