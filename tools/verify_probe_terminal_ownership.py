#!/usr/bin/env python3
import argparse
import re
import subprocess
import sys
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root')
args = parser.parse_args()
root = Path(args.root).resolve() if args.root else Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
service = (root / 'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt').read_text(encoding='utf-8')
cpp = (root / 'app/src/main/cpp/vulkanscope.cpp').read_text(encoding='utf-8')
errors=[]

def req(label, cond):
    if not cond: errors.append(label)

req('native code must not publish the .done terminal marker before JNI/service return', 'publishProbeTerminalMarker' not in cpp and 'terminal marker remains service-owned' in cpp)
req('base JNI wrapper must not rewrite an already durable collector final result', 'bool finalPublishedByCollector = false;' in cpp and 'finalPublishedByCollector || publishProbeCheckpoint(resultPath, result)' in cpp)
req('service must be the sole terminal-marker publisher after native collection returns', 'val terminalPublished = terminalPayloadValid && writeResult(safeTerminalPath, "done")' in service and 'after JNI return' in service)
req('service must stream-validate bounded terminal JSON before publishing done and replace invalid native output with explicit unavailable evidence', 'var terminalPayloadValid = published && validateTerminalResult(safeResultPath, group)' in service and 'terminalPayloadValid = published && validateTerminalResult(safeResultPath, group)' in service and 'JsonReader' in service and 'JsonToken.END_DOCUMENT' in service and 'replacing it with explicit unavailable evidence' in service and 'produced invalid terminal JSON' in service)
req('all service process-exit paths must share one atomic termination claim', 'AtomicBoolean' in service and 'terminationClaimed.compareAndSet(false, true)' in service and service.count('Process.killProcess(Process.myPid())') == 1)
req('normal worker completion must not call stopSelfResult before claiming process termination', 'Terminal result and service-owned completion marker are durable' in service and 'stopSelfResult(startId)' not in service.split('worker.execute {',1)[1])
checkpoint_branch = re.search(r'if \(resultLength > 0L && checkpointChanged\) \{(.*?)\n\s*\} else \{\n\s*kotlinx\.coroutines\.delay\(40L\)', main, re.S)
req('normal consumer polling must not promote a terminal-shaped checkpoint without the service marker', checkpoint_branch is not None and 'terminalCandidate(' not in checkpoint_branch.group(1) and 'value =' not in checkpoint_branch.group(1))
terminal_branch = main.split('if (terminalFile.isFile) {',1)[1].split('if (crashDetected())',1)[0] if 'if (terminalFile.isFile) {' in main else ''
req('service terminal marker must use a bounded stable-result reread before acceptance', 'readServiceTerminalCandidateWithGrace' in main and 'bounded stable-result reread' in terminal_branch)
req('terminal marker acceptance must not synchronously stop the service before the final reread completes', 'stopVulkanProbeProcess()' not in terminal_branch)
req('timeout boundary may still recover a pre-return durable terminal-shaped result', 'publishedBeforeTimeout = readPublishedCandidate()?.takeIf(::terminalCandidate)' in main)
state = root / 'tools/test_probe_terminal_ownership_state_machine.py'
req('terminal ownership behavioral state-machine test must exist', state.is_file())
if state.is_file():
    r=subprocess.run([sys.executable,str(state)],cwd=root,text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
    if r.returncode: errors.append('terminal ownership state machine failed: '+r.stdout.strip().replace('\n',' | '))
if errors:
    for e in errors: print('FAIL:',e)
    raise SystemExit(1)
print('PASS probe terminal ownership: service-owned post-JNI marker, provisional pre-marker checkpoints, single atomic process-exit owner, bounded terminal reread, timeout recovery retained')
