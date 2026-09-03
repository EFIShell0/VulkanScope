#!/usr/bin/env python3
import argparse
import subprocess
import sys
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root')
args = parser.parse_args()
root = Path(args.root).resolve() if args.root else Path(__file__).resolve().parents[1]
kt_path = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
if not kt_path.is_file():
    raise SystemExit('FAIL MainActivity.kt missing')
kt = kt_path.read_text(encoding='utf-8')
errors = []

def need(condition, message):
    if not condition:
        errors.append(message)

need('private var surfaceHostGeneration by mutableStateOf(0L)' in kt, 'driver-bound Surface host generation state missing')
need('private var driverSurfaceRebindPending = false' in kt, 'driver Surface rebind pending state missing')
need('surfaceReady: (Long, Surface) -> Unit' in kt, 'Surface ready callback is not host-generation bound')
need('surfaceDestroyed: (Long, Surface) -> Unit' in kt, 'Surface destroyed callback is not host-generation bound')
need('if (hostGeneration != surfaceHostGeneration)' in kt, 'stale Surface host callback rejection missing')
need('onCreated(hostGeneration, holder.surface)' in kt, 'Surface ready callback does not forward host generation')
need('onDestroyed(hostGeneration, holder.surface)' in kt, 'Surface destroyed callback does not forward host generation')
need('androidx.compose.runtime.key(surfaceHostGeneration)' in kt, 'SurfaceView host is not recreated on driver generation change')
need('private fun prepareDriverSurfaceRebind()' in kt, 'driver Surface rebind preparation helper missing')
prepare_start = kt.find('private fun prepareDriverSurfaceRebind()')
prepare_end = kt.find('private fun applyDriverModeChange', prepare_start)
prepare = kt[prepare_start:prepare_end] if prepare_start >= 0 and prepare_end > prepare_start else ''
need('currentSurface = null' in prepare, 'driver change does not invalidate old Surface identity')
need('surfaceGeneration += 1L' in prepare, 'driver change does not invalidate old Surface token')
need('surfaceRefreshPending = false' in prepare, 'driver change leaves stale Surface refresh pending')
need('driverSurfaceRebindPending = true' in prepare, 'driver change does not wait for replacement Surface')
need('surfaceHostGeneration += 1L' in prepare, 'driver change does not recreate Surface host')
apply_start = kt.find('private fun applyDriverModeChange(')
apply_end = kt.find('private fun findInstalledTurnipLibrary', apply_start)
apply = kt[apply_start:apply_end] if apply_start >= 0 and apply_end > apply_start else ''
need('prepareDriverSurfaceRebind()' in apply, 'driver mode change does not prepare Surface rebind')
need('requestReportCollection()' not in apply, 'driver change still starts full collection before replacement Surface is ready')
ready_start = kt.find('surfaceReady = { hostGeneration, surface ->')
ready_end = kt.find('surfaceDestroyed = { hostGeneration, surface ->', ready_start)
ready = kt[ready_start:ready_end] if ready_start >= 0 and ready_end > ready_start else ''
need('driverSurfaceRebindPending' in ready and 'requestReportCollection()' in ready, 'replacement Surface does not trigger deferred driver collection')
need('hostGeneration != surfaceHostGeneration' in ready, 'Surface ready path accepts stale host generation')
destroy_start = kt.find('surfaceDestroyed = { hostGeneration, surface ->')
destroy_end = kt.find('driverMode = driverMode', destroy_start)
destroy = kt[destroy_start:destroy_end] if destroy_start >= 0 and destroy_end > destroy_start else ''
need('hostGeneration != surfaceHostGeneration' in destroy, 'Surface destroy path accepts stale host generation')
if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
state_machine = root / 'tools/test_driver_surface_rebind_state_machine.py'
if state_machine.is_file():
    result = subprocess.run([sys.executable, str(state_machine)], cwd=root, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.stdout:
        print(result.stdout.rstrip())
    if result.returncode != 0:
        raise SystemExit(result.returncode)
print('PASS VulkanScope 0.41.43 driver/Surface rebind source contract')
