#!/usr/bin/env python3
import argparse
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

main_timeout_catch = main.split('catch (_: kotlinx.coroutines.TimeoutCancellationException)', 1)[1].split('catch (cancelled: CancellationException)', 1)[0] if 'catch (_: kotlinx.coroutines.TimeoutCancellationException)' in main and 'catch (cancelled: CancellationException)' in main else ''


def require(name, condition):
    if not condition:
        errors.append(name)


require('native base collector may publish a final checkpoint before JNI return but must leave terminal-marker ownership to the service',
        'terminal marker remains service-owned' in cpp and 'publishProbeTerminalMarker' not in cpp and
        'finalPublishedByCollector || publishProbeCheckpoint(resultPath, result)' in cpp)
require('all Vulkan instance teardown in the dedicated probe process must be process-owned',
        '~VulkanApi() = default;' in cpp and 'destroyInstance = probeNoopDestroyInstance;' in cpp and 'destroySurfaceKHR = probeNoopDestroySurface;' in cpp)
require('service must own a hard timeout watchdog independent of ActivityManager PID discovery',
        'const val EXTRA_TIMEOUT_MS = "timeout_ms"' in service and
        'val hardTimeoutWatchdog = Runnable {' in service and
        'mainHandler.postDelayed(hardTimeoutWatchdog, timeoutMs + 100L)' in service and
        'Hard probe deadline reached; terminating the dedicated probe process' in service and
        'terminateDedicatedProcess("Hard probe deadline reached; terminating the dedicated probe process", true)' in service)
require('successful service termination must happen only after post-JNI service-owned terminal publication through the atomic exit owner',
        'val terminalPublished = terminalPayloadValid && writeResult(safeTerminalPath, "done")' in service and
        'Terminal result and service-owned completion marker are durable' in service and
        'terminationClaimed.compareAndSet(false, true)' in service)
require('consumer must pass its exact bounded timeout to the dedicated process watchdog and settle past the watchdog on timeout',
        '.putExtra(VulkanProbeService.EXTRA_TIMEOUT_MS, timeoutMs)' in main and
        'delay(150L)' in main_timeout_catch and
        'val settledPublication = publishedBeforeTimeout ?: readPublishedCandidate()?.takeIf(::terminalCandidate)' in main_timeout_catch)
require('consumer must enforce a bounded stale-process barrier before every new service probe',
        'private suspend fun ensureVulkanProbeProcessQuiescent(timeoutMs: Long = 1_500L): Boolean' in main and
        'if (!ensureVulkanProbeProcessQuiescent()) return@withLock unavailableProbe' in main and
        main.find('if (!ensureVulkanProbeProcessQuiescent())') < main.find('startService(intent)', main.find('if (!ensureVulkanProbeProcessQuiescent())')))
require('base timeout must be bounded to 20 seconds', 'runServiceProbe("base", surface, 20_000L, modeSnapshot)' in main)
require('terminal unavailable/timeout base results must not be retried automatically',
        'val retryablePartial = attempt == 0 &&' in main and 'root.optString("status", "unavailable") == "incomplete"' in main and 'parsed.error?.contains("timeout", ignoreCase = true) != true' in main)
require('canonical synthetic base unavailable must include baseReportComplete=false',
        '.put("reason", reason).put("baseReportComplete", false).put("devices", JSONArray())' in main)

test_path = root / 'tools/test_probe_timeout_state_machine.py'
require('timeout/handoff behavioral state-machine test must exist', test_path.is_file())
if test_path.is_file():
    result = subprocess.run([sys.executable, str(test_path)], cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    if result.returncode != 0:
        errors.append('timeout/handoff behavioral state-machine test failed: ' + result.stdout.strip().replace('\n', ' | '))

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS probe timeout recovery: pre-return final-checkpoint recovery, service-owned post-JNI terminal marker, process-owned teardown, atomic one-shot termination, independent hard-deadline watchdog, stale-process barrier, 20s base ceiling, non-retry of deterministic timeout')
