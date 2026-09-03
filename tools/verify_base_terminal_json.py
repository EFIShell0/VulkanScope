#!/usr/bin/env python3
import argparse
import subprocess
import sys
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root')
args = parser.parse_args()
root = Path(args.root).resolve() if args.root else Path(__file__).resolve().parents[1]
cpp = (root / 'app/src/main/cpp/vulkanscope.cpp').read_text(encoding='utf-8')
errors = []


def req(label: str, condition: bool) -> None:
    if not condition:
        errors.append(label)

surface = 'out << ",\\"surface\\":{\\"available\\":false,\\"queryStatus\\":\\"unknown\\",\\"queryReason\\":\\"Base probe does not own final live-Surface evidence.\\"}";'
req('final base device object must close after its surface object and before the devices array closes', surface in cpp and surface + "\n        out << '}';" in cpp)
req('base terminal builder must structurally validate JSON containers before publication', 'bool jsonContainersBalanced(const std::string& value)' in cpp and cpp.count('jsonContainersBalanced(finalResult)') >= 2)
start = cpp.find('std::string collect(jobject surfaceObject')
end = cpp.find('\nstd::string collectVulkanMetadata', start)
collect = cpp[start:end] if start >= 0 and end > start else ''
req('base one-shot probe must leave Vulkan instance destruction to dedicated process lifetime', collect != '' and 'destroyInstance(instance' not in collect)
req('base terminal root must close the devices array and root after every device object is closed', '    out << "]}";' in collect)
state = root / 'tools/test_base_terminal_json_state_machine.py'
req('base terminal JSON behavioral state-machine test must exist', state.is_file())
if state.is_file():
    result = subprocess.run([sys.executable, str(state)], cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    if result.returncode != 0:
        errors.append('base terminal JSON state machine failed: ' + result.stdout.strip().replace('\n', ' | '))
if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS base terminal JSON: device/root closure, structural pre-publication validation, process-owned base teardown, multi-device behavioral grammar')
