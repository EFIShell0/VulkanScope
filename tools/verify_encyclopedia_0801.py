#!/usr/bin/env python3
import argparse
import re
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', default=str(Path(__file__).resolve().parents[1]))
args = parser.parse_args()
root = Path(args.root).resolve()
kt = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
errors = []

def require(condition, message):
    if not condition:
        errors.append(message)

def function_body(name):
    match = re.search(r'private fun (?:[A-Za-z0-9_<>?.]+\.)?' + re.escape(name) + r'\s*\(', kt)
    if not match:
        return ''
    open_brace = kt.find('{', match.start())
    if open_brace < 0:
        return ''
    depth = 0
    in_string = False
    escaped = False
    for index in range(open_brace, len(kt)):
        char = kt[index]
        if in_string:
            if escaped:
                escaped = False
            elif char == '\\':
                escaped = True
            elif char == '"':
                in_string = False
            continue
        if char == '"':
            in_string = True
        elif char == '{':
            depth += 1
        elif char == '}':
            depth -= 1
            if depth == 0:
                return kt[match.start():index + 1]
    return ''

require(('versionName = "0.80.0"' in gradle and 'versionCode = 800' in gradle) or ('versionName = "0.80.1"' in gradle and 'versionCode = 801' in gradle), '0.80.0/0.80.1 release identity missing')
overview = function_body('OverviewPage')
require(overview, 'OverviewPage missing')
snapshot_pos = overview.find('CapabilitySectionCard("Capability snapshot")')
encyclopedia_pos = overview.find('EncyclopediaOverviewCard(')
analysis_pos = overview.find('analysisWorkspaceItems(')
require(snapshot_pos >= 0 and encyclopedia_pos > snapshot_pos and analysis_pos > encyclopedia_pos, 'Encyclopedia is not placed between Capability snapshot and Analysis workspace')
require(overview.count('EncyclopediaOverviewCard(') == 1, 'Overview must contain exactly one Encyclopedia card')
encyclopedia = function_body('EncyclopediaOverviewCard')
require(encyclopedia, 'EncyclopediaOverviewCard missing')
require('CapabilitySectionCard("Encyclopedia")' in encyclopedia, 'Encyclopedia is not presented as its own design-system card')
for state in ['Supported', 'Unsupported', 'Unavailable', 'Not applicable', 'Unknown']:
    require(f'CapabilityKeyValue("{state}"' in encyclopedia, f'Encyclopedia evidence-state definition missing: {state}')
for destination in ['Page.Extensions', 'Page.Profiles', 'Page.Video']:
    require(destination in encyclopedia, f'Encyclopedia reference navigation missing: {destination}')
require('Runtime evidence and registry reference data remain separate.' in encyclopedia, 'Encyclopedia does not preserve registry/runtime evidence separation')
require('does not mean the hardware lacks video decoding or encoding' in encyclopedia, 'Vulkan Video encyclopedia note does not prevent hardware-codec misinterpretation')
require('verticalScroll(' not in encyclopedia and 'LazyColumn(' not in encyclopedia, 'Encyclopedia introduces a nested vertical scroll container')

if errors:
    for error in errors:
        print(f'FAIL {error}')
    raise SystemExit(1)
print('PASS 0.80.1 Overview Encyclopedia contract')
