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
advanced_path = root / 'app/src/main/java/com/efishell/vulkanscope/AdvancedAnalysis.kt'
main_path = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
gradle_path = root / 'app/build.gradle.kts'
rules_path = root / 'rules/PROJECT_RULES.md'
audit_path = root / 'rules/1.0.3_KOTLIN_REGEX_COMPILE_FIX_AUDIT.md'
for path in [advanced_path, main_path, gradle_path, rules_path]:
    if not path.is_file():
        errors.append(f'missing required file: {path.relative_to(root)}')
if errors:
    for error in errors:
        print(f'FAIL: {error}')
    raise SystemExit(1)
advanced = advanced_path.read_text(encoding='utf-8')
main = main_path.read_text(encoding='utf-8')
gradle = gradle_path.read_text(encoding='utf-8')
rules = rules_path.read_text(encoding='utf-8')

def require(condition, message):
    if not condition:
        errors.append(message)

if not args.skip_version:
    require(any(f'versionCode = {code}' in gradle and f'versionName = "{name}"' in gradle for name,code in [('1.0.3',1003),('1.0.4',1004),('1.0.5',1005),('1.0.6',1006),('1.0.7',1007),('1.0.8',1008),('1.0.9',1009),('1.0.10',1010),('1.0.11',1011),('1.0.12',1012),('1.0.13',1013),('1.0.14',1014),('1.0.15',1015),('1.0.16',1016),('1.0.17',1017),('1.0.18',1018)]), 'release identity is not a retained 1.0.3+ identity')
required_advanced = [
    'Regex("""^api\\s*>=\\s*([0-9]+\\.[0-9]+)$""", RegexOption.IGNORE_CASE)',
    'Regex("""^(.+?)(>=|<=|==|>|<)(-?[0-9]+(?:\\.[0-9]+)?)$""")',
    'Regex("""-?[0-9]+(?:\\.[0-9]+)?""")',
]
required_main = 'Regex("""VK_VERSION_(\\d+)_(\\d+)""")'
for token in required_advanced:
    require(token in advanced, f'compile-safe raw regex is missing from AdvancedAnalysis.kt: {token}')
require(required_main in main, 'compile-safe raw VK_VERSION dependency regex is missing from MainActivity.kt')
invalid_advanced = [
    'Regex("^api\\s*>=\\s*([0-9]+\\.[0-9]+)$", RegexOption.IGNORE_CASE)',
    'Regex("^(.+?)(>=|<=|==|>|<)(-?[0-9]+(?:\\.[0-9]+)?)$")',
    'Regex("-?[0-9]+(?:\\.[0-9]+)?")',
]
invalid_main = 'Regex("VK_VERSION_(\\d+)_(\\d+)")'
for token in invalid_advanced:
    require(token not in advanced, f'predecessor unsupported ordinary-string regex escape was restored: {token}')
require(invalid_main not in main, 'predecessor unsupported VK_VERSION ordinary-string regex escape was restored')
patterns = [
    (r'^api\s*>=\s*([0-9]+\.[0-9]+)$', [('api >= 1.4', True), ('API>=1.4', True), ('api = 1.4', False)]),
    (r'^(.+?)(>=|<=|==|>|<)(-?[0-9]+(?:\.[0-9]+)?)$', [('maxImageDimension2D>=4096', True), ('x<-1.25', True), ('x~=2', False)]),
    (r'-?[0-9]+(?:\.[0-9]+)?', [('value 18446744073709551615', True), ('value -1.25', True)]),
    (r'VK_VERSION_(\d+)_(\d+)', [('VK_VERSION_1_4', True), ('VK_VERSION_10_12', True), ('VK_VERSION_1_X', False)]),
]
for pattern, samples in patterns:
    compiled = re.compile(pattern, re.IGNORECASE if pattern.startswith('^api') else 0)
    for sample, expected in samples:
        actual = bool(compiled.fullmatch(sample) if pattern.startswith('^api') or pattern.startswith('^(.+?') or pattern.startswith('VK_VERSION_') else compiled.search(sample))
        require(actual == expected, f'regex behavior drifted for {pattern!r} sample {sample!r}')
require('## Release 1.0.3 Kotlin regular-expression compile-regression requirements' in rules, 'PROJECT_RULES 1.0.3 compile-regression contract is missing')
require(audit_path.is_file(), '1.0.3 Kotlin regex compile-fix audit is missing')
require((root / 'tests/golden/1.0.2_regression_contract.json').is_file(), '1.0.2 immutable regression contract is missing')
if errors:
    for error in errors:
        print(f'FAIL: {error}')
    raise SystemExit(1)
print('PASS VulkanScope 1.0.3 Kotlin regex compile-regression contract')
