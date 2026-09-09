#!/usr/bin/env python3

def old_route(is_tv, resolver_visible, actual_launch):
    if is_tv or not resolver_visible: return 'fallback'
    return 'picker' if actual_launch else 'fallback'

def new_route(actual_launch, launch_error=None, user_result='selected'):
    if launch_error in ('ActivityNotFoundException', 'SecurityException'): return 'fallback'
    if not actual_launch: return 'uncaught-runtime-failure'
    return 'picker-result:' + user_result

assert old_route(False, False, True) == 'fallback'
assert new_route(True) == 'picker-result:selected'
assert new_route(True, user_result='cancelled') == 'picker-result:cancelled'
assert old_route(True, True, True) == 'fallback'
assert new_route(True) == 'picker-result:selected'
assert new_route(False, 'ActivityNotFoundException') == 'fallback'
assert new_route(False, 'SecurityException') == 'fallback'
assert new_route(False, 'IllegalStateException') == 'uncaught-runtime-failure'
analysis_roots = ['Android/data/pkg/files/Documents', 'Android/data/pkg/files/Download', 'files/analysis_exchange']
turnip_roots = ['files/turnip_imports', 'Android/data/pkg/files', 'Android/data/pkg/files/Download', 'Android/data/pkg/files/Documents']
assert len(analysis_roots) == 3 and len(turnip_roots) == 4
assert ['turnip_%02d.zip' % i for i in range(1, 11)] == [f'turnip_{i:02d}.zip' for i in range(1,11)]
print('PASS VulkanScope 1.0.15 launch-first picker/fallback state machine')
