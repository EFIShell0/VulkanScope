#!/usr/bin/env python3
import argparse
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', default='.')
parser.add_argument('--skip-version', action='store_true')
args = parser.parse_args()
root = Path(args.root).resolve()
errors = []

def require(condition, message):
    if not condition:
        errors.append(message)

def text(relative):
    return (root / relative).read_text(encoding='utf-8')

main = text('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
gradle = text('app/build.gradle.kts')
setup = text('DATABASE_SETUP.md')
rules = text('rules/PROJECT_RULES.md')

if not args.skip_version:
    require('versionCode = 812' in gradle and 'versionName = "0.80.12"' in gradle, 'release identity is not 0.80.12/812')
require(main.count('import androidx.compose.ui.semantics.Role\n') == 1, 'Compose Role import is missing or duplicated')
require(main.count('import androidx.compose.ui.semantics.role\n') == 1, 'SemanticsPropertyReceiver.role extension import is missing or duplicated')
require('Modifier.semantics { role = Role.Button }' in main, 'explicit Details/button semantics role assignment is missing')

if args.skip_version:
    require('private fun ExpressiveSwitch' in main and 'Switch(' in main, 'Material Switch control semantics regressed')
    require('role = Role.Switch' not in main, 'successor restored a second row-level Switch semantic target')
else:
    require('role = Role.Switch' in main, 'Switch role semantics regressed')
require('role = Role.RadioButton' in main, 'RadioButton role semantics regressed')
require('implementation("androidx.compose.ui:ui:1.12.0")' in gradle, 'validated Compose UI 1.12.0 pin drifted')
require('id("org.jetbrains.kotlin.android")' not in text('build.gradle.kts'), 'deprecated Kotlin Android plugin unexpectedly restored')
if not args.skip_version:
    require('VulkanScope 0.80.12 uses the fixed official VulkanScope Database Worker root:' in setup, 'DATABASE_SETUP current application identity is stale')
    require('VulkanScope Database 0.39.24 is the companion Database for VulkanScope 0.80.12' in setup, 'DATABASE_SETUP companion wording is stale')
require('## Release 0.80.12 Compose role compile-regression requirements' in rules, 'PROJECT_RULES 0.80.12 compile-regression contract is missing')
require((root / 'rules/0.80.12_COMPOSE_ROLE_COMPILE_FIX_AUDIT.md').is_file(), '0.80.12 audit document is missing')
require((root / 'tests/golden/0.80.10_regression_contract.json').is_file(), '0.80.10 -> 0.80.12 regression contract is missing')

if errors:
    print('FAIL VulkanScope 0.80.12 Compose role compile-regression contract')
    for error in errors:
        print('-', error)
    raise SystemExit(1)
print('PASS VulkanScope 0.80.12 Compose role compile-regression contract')
