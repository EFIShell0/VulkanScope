#!/usr/bin/env python3
import argparse
import subprocess
import sys
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root')
args = parser.parse_args()
root = Path(args.root).resolve() if args.root else Path(__file__).resolve().parents[1]
errors = []

main_path = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
service_path = root / 'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt'
if not main_path.is_file() or not service_path.is_file():
    raise SystemExit('probe cancellation verifier inputs missing')
main = main_path.read_text(encoding='utf-8')
service = service_path.read_text(encoding='utf-8')

def require(label, condition):
    if not condition:
        errors.append(label)

service_destroy = service.split('override fun onDestroy()', 1)[1] if 'override fun onDestroy()' in service else ''
require('service onDestroy must claim the shared hard-exit owner so stopService can terminate a JNI-blocked worker',
        'terminateDedicatedProcess("Probe service teardown requested; terminating the dedicated process")' in service_destroy and
        'terminationClaimed.compareAndSet(false, true)' in service and
        service.count('Process.killProcess(Process.myPid())') == 1)
require('service onDestroy must shut down the executor before process exit',
        'worker.shutdownNow()' in service_destroy)
require('consumer quiescence must retain stopService return evidence',
        'val stopRequested' in main and 'stopService(Intent(this@MainActivity, VulkanProbeService::class.java))' in main)
require('accepted stopService must receive an ActivityManager-independent bounded settle window',
        'if (stopRequested) delay(150L)' in main)
require('runServiceProbe cancellation must execute non-cancellable teardown before deleting request files',
        'catch (cancelled: CancellationException)' in main and 'withContext(NonCancellable)' in main and 'ensureVulkanProbeProcessQuiescent()' in main)
require('cancellation must be rethrown after teardown rather than converted into capability evidence',
        'throw cancelled' in main)

state_test = root / 'tools/test_probe_cancellation_state_machine.py'
require('cancellation behavioral state-machine test must exist', state_test.is_file())
if state_test.is_file():
    result = subprocess.run([sys.executable, str(state_test)], cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    if result.returncode != 0:
        errors.append('cancellation behavioral state-machine test failed: ' + result.stdout.strip().replace('\n', ' | '))

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS probe cancellation recovery: service-destroy shared hard-exit owner, non-cancellable consumer teardown, stopService settle, cancellation rethrow, false-positive control')
