import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_release_1406.py'

def run(candidate):
    return subprocess.run([sys.executable, str(verifier), '--root', str(candidate), '--skip-version'], text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)

def mutate(relative, old, new):
    with tempfile.TemporaryDirectory(prefix='vulkanscope-1406-mutation-') as td:
        candidate = Path(td) / 'tree'
        shutil.copytree(root, candidate)
        path = candidate / relative
        data = path.read_text(encoding='utf-8')
        if old not in data:
            raise SystemExit(f'mutation source token missing: {relative}: {old}')
        path.write_text(data.replace(old, new, 1), encoding='utf-8')
        result = run(candidate)
        if result.returncode == 0:
            raise SystemExit(f'negative mutation escaped verifier: {relative}: {old}')

m = 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
mutations = [
    (m, 'NavigationBar(', 'ShortNavigationBar('),
    (m, 'alwaysShowLabel = true', 'alwaysShowLabel = false'),
    (m, '.heightIn(min = 80.dp)', '.heightIn(min = 64.dp)'),
    (m, 'prefs.getBoolean("opening_animation_enabled", true)', 'prefs.getBoolean("opening_animation_enabled", false)'),
    (m, 'private var startupGateOpen by mutableStateOf(false)', 'private var startupGateOpen by mutableStateOf(true)'),
    (m, 'private fun requestReportCollection() {\n        if (!startupGateOpen || !activityStarted) return', 'private fun requestReportCollection() {'),
    (m, 'private fun requestQueryGroup(group: String) {\n        if (!startupGateOpen) return', 'private fun requestQueryGroup(group: String) {'),
    (m, 'R.drawable.vulkanscope_logo_horizontal', 'R.drawable.ic_home'),
    (m, 'ExpressiveSwitch(checked = openingAnimationEnabled, onCheckedChange = onOpeningAnimationChanged)', 'ExpressiveSwitch(checked = openingAnimationEnabled, onCheckedChange = null)'),
    (m, 'putBoolean("opening_animation_enabled", enabled)', 'putBoolean("opening_animation_enabled_removed", enabled)'),
    (m, 'startupGateOpen = true', 'startupGateOpen = false'),
    (m, 'if (startupGateOpen || !openingAnimationEnabled) {\n            VulkanScopeApp(', 'if (true) {\n            VulkanScopeApp(')
]
for mutation in mutations:
    mutate(*mutation)

with tempfile.TemporaryDirectory(prefix='vulkanscope-1406-control-') as td:
    candidate = Path(td) / 'tree'
    shutil.copytree(root, candidate)
    path = candidate / 'BUILD_AUDIT.md'
    path.write_text(path.read_text(encoding='utf-8') + '\nUnrelated 1.4.6 verifier false-positive control.\n', encoding='utf-8')
    result = run(candidate)
    if result.returncode != 0:
        print(result.stdout)
        raise SystemExit('false-positive control failed')
print(f'release_1406 negative mutations: PASS mutations={len(mutations)} falsePositiveControls=1')
