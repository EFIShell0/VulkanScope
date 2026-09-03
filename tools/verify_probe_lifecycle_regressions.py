#!/usr/bin/env python3
import argparse
import json
import re
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


require('dedicated probe loader-handle cleanup must be deferred to process teardown', '~VulkanApi() = default;' in cpp)
require('vkDestroyInstance must not run on the dedicated-probe result-publication critical path', 'destroyInstance = probeNoopDestroyInstance;' in cpp)
require('vkDestroySurfaceKHR must not run on the dedicated-probe result-publication critical path', 'destroySurfaceKHR = probeNoopDestroySurface;' in cpp)
require('dedicated service process termination must be confined to one atomic owner shared by hard-timeout, normal completion and cancellation', service.count('Process.killProcess(Process.myPid())') == 1 and 'terminationClaimed.compareAndSet(false, true)' in service and 'mainHandler.postDelayed(hardTimeoutWatchdog, timeoutMs + 100L)' in service and 'override fun onDestroy()' in service)
terminal_branch = re.search(r'if \(terminalFile\.isFile\) \{(.*?)\n\s*continue', main, re.S)
require('normal consumer polling must not terminate a still-active writer before the service-owned terminal marker', terminal_branch is not None and 'readServiceTerminalCandidateWithGrace' in terminal_branch.group(1) and 'stopVulkanProbeProcess()' not in terminal_branch.group(1))
require('timeout boundary must recover already-published terminal result', 'Recovered an atomic $group probe publication at the timeout boundary instead of discarding it.' in main and 'readPublishedCandidate()?.takeIf(::terminalCandidate)' in main)
require('a bounded stale-process barrier must run before each new probe', 'ensureVulkanProbeProcessQuiescent' in main and 'Previous dedicated Vulkan probe process remained alive after bounded teardown' in main)
require('background detail collection must have a finite total budget', 'private const val BACKGROUND_COLLECTION_BUDGET_MS = 60_000L' in main and 'deadlineNanos = System.nanoTime() + BACKGROUND_COLLECTION_BUDGET_MS * 1_000_000L' in main)
require('budget exhaustion must become explicit unavailable evidence rather than silent omission', 'bounded background Vulkan detail-collection budget' in main and 'publishUnavailableGroups(' in main)
require('background single-query execution must obey remaining total budget', 'withTimeout(remainingBudgetMs) { runIsolatedProbe(group, modeSnapshot) }' in main)

budget_path = root / 'registry/generated/resource_budget.json'
if budget_path.is_file():
    budget = json.loads(budget_path.read_text(encoding='utf-8'))
    require('resource budget must record background collection ceiling', budget.get('backgroundCollectionBudgetMs') == 60000)
else:
    errors.append('resource budget missing')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS probe lifecycle regressions: process-owned native teardown, service-owned post-JNI terminality, atomic one-shot exit, consumer timeout recovery, stale-process isolation, background detail collection bounded to 60000 ms')
