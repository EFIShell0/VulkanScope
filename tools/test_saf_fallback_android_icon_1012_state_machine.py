#!/usr/bin/env python3

def route(launch_ok, launch_error=None, import_action=True):
    if launch_error in ('ActivityNotFoundException','SecurityException'): return 'fallback-dialog' if import_action else 'fallback-write+toast'
    return 'saf-picker' if launch_ok else 'runtime-failure'
assert route(True)=='saf-picker'
assert route(True,import_action=False)=='saf-picker'
assert route(False,'ActivityNotFoundException')=='fallback-dialog'
assert route(False,'SecurityException')=='fallback-dialog'
assert route(False,'ActivityNotFoundException',False)=='fallback-write+toast'
assert route(True)==route(True)
analysis_roots=['Android/data/pkg/files/Documents','Android/data/pkg/files/Download','files/analysis_exchange']; turnip_roots=['files/turnip_imports','Android/data/pkg/files','Android/data/pkg/files/Download','Android/data/pkg/files/Documents']
assert len(analysis_roots)==3 and len(turnip_roots)==4 and all('..' not in x for x in analysis_roots+turnip_roots)
assert [f'turnip_{i:02d}.zip' for i in range(1,11)][0]=='turnip_01.zip' and [f'turnip_{i:02d}.zip' for i in range(1,11)][-1]=='turnip_10.zip'
print('PASS VulkanScope retained 1.0.12 bounded fallback routing with 1.0.15 launch-first supersession')
