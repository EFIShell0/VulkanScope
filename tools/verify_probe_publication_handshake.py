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
errors = []


def require(name, condition):
    if not condition:
        errors.append(name)


require('probe service must expose a dedicated terminal sidecar path', 'const val EXTRA_TERMINAL_PATH = "terminal_path"' in service)
require('terminal sidecar must be canonically confined and bound to the result path', 'requestedTerminal.path != requestedResult.path + ".done"' in service)
require('probe service process exit must use one atomic owner shared by hard-timeout, normal completion and cancellation', service.count('Process.killProcess(Process.myPid())') == 1 and 'AtomicBoolean' in service and 'terminationClaimed.compareAndSet(false, true)' in service and 'val hardTimeoutWatchdog = Runnable {' in service and 'override fun onDestroy()' in service)
require('terminal marker must be service-owned and published only after JNI return plus terminal validation', 'val terminalPublished = terminalPayloadValid && writeResult(safeTerminalPath, "done")' in service and 'validateTerminalResult' in service and 'publishProbeTerminalMarker' not in cpp)
require('fallback result publication must determine terminal-marker eligibility', 'published = writeResult(safeResultPath' in service)
require('main process must create and pass the exact terminal sidecar', 'val terminalFile = File(resultFile.absolutePath + ".done")' in main and '.putExtra(VulkanProbeService.EXTRA_TERMINAL_PATH, terminalFile.absolutePath)' in main)
require('terminal sidecar must force a bounded stable result reread after service ownership', 'if (terminalFile.isFile)' in main and 'readServiceTerminalCandidateWithGrace' in main and 'Observed service-owned terminal $group publication' in main)
require('malformed or non-terminal service publication must fail closed after the bounded handoff window', 'no valid terminal JSON became readable within the bounded handoff window' in main)
require('service must replace invalid post-JNI terminal data with explicit unavailable evidence before marking done', 'produced invalid terminal JSON' in service and 'replacing it with explicit unavailable evidence' in service)
terminal_branch = re.search(r'if \(terminalFile\.isFile\) \{(.*?)\n\s*continue', main, re.S)
require('normal terminal acceptance must not synchronously kill the producer before the stable reread completes', terminal_branch is not None and 'readServiceTerminalCandidateWithGrace' in terminal_branch.group(1) and 'stopVulkanProbeProcess()' not in terminal_branch.group(1))
require('terminal sidecar must be deleted on all normal cleanup paths', 'terminalFile.delete()' in main)
require('timeout boundary must retain bounded terminal recovery', 'readPublishedCandidate()?.takeIf(::terminalCandidate)' in main)
require('base-ready checkpoint must close the root JSON object', 'baseReadySnapshot += ",\\\"surface\\\":{\\\"available\\\":false}}]}";' in cpp)
require('base-ready checkpoint log must reflect actual atomic publication success', 'const bool baseReadyPublished = publishProbeCheckpoint(checkpointPath, baseReadySnapshot);' in cpp and 'base core checkpoint publication failed before optional surface enrichment' in cpp)
require('terminal base JSON must rewrite one pre-existing completeness field instead of appending a duplicate', 'const std::string completenessMarker = "\\\"baseReportComplete\\\":false";' in cpp and 'uniqueCompletenessMarker' in cpp and 'finalResult.replace(completenessMarkerPos' in cpp)
require('terminal base JSON must not append a second baseReportComplete field', not re.search(r'finalResult\s*\+=.*baseReportComplete', cpp))
require('final checkpoint log must reflect actual publication success', 'const bool finalCheckpointPublished = publishProbeCheckpoint(checkpointPath, finalResult);' in cpp and 'base terminal checkpoint publication failed' in cpp)

state_machine = Path(__file__).resolve().parent / 'test_probe_publication_state_machine.py'
require('behavioral probe-publication state-machine test must exist', state_machine.is_file())
if state_machine.is_file():
    result = subprocess.run([sys.executable, str(state_machine)], cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    if result.returncode != 0:
        errors.append('behavioral probe-publication state-machine test failed: ' + result.stdout.strip().replace('\n', ' | '))

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS probe publication handshake: service-owned post-JNI terminal marker, atomic single-exit owner, bounded terminal reread, canonical base JSON, fail-closed validation, behavioral state machine PASS')
