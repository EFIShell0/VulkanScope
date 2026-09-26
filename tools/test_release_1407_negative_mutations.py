import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_release_1407.py'

def run(candidate):
    return subprocess.run([sys.executable, str(verifier), '--root', str(candidate), '--skip-version'], text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)

def mutate(relative, old, new):
    with tempfile.TemporaryDirectory(prefix='vulkanscope-1407-mutation-') as td:
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
    (m, 'private fun persistOpeningAnimationPreference(enabled: Boolean)', 'private fun setOpeningAnimationEnabled(enabled: Boolean)'),
    (m, 'if (event.type != PointerEventType.Scroll) continue', 'if (event.type == PointerEventType.Scroll) continue'),
    (m, 'android.view.ViewConfiguration.get(context).scaledVerticalScrollFactor.coerceAtLeast(1f)', '1f'),
    (m, 'change.type == PointerType.Mouse', 'change.type != PointerType.Mouse'),
    (m, 'if (!event.buttons.isPrimaryPressed || !mouse.pressed)', 'if (!event.buttons.isSecondaryPressed || !mouse.pressed)'),
    (m, 'if (kotlin.math.abs(accumulatedY) <= slop) continue', 'if (false) continue'),
    (m, 'state.dispatchRawDelta(-deltaY)', 'state.dispatchRawDelta(deltaY)'),
    (m, 'if (overSlop != 0f) state.dispatchRawDelta(-overSlop)\n                        mouse.consume()', 'if (overSlop != 0f) state.dispatchRawDelta(-overSlop)\n                        Unit'),
    (m, 'Modifier.fillMaxSize().desktopVerticalPointerScroll(listState, pointerWheelScalePx).focusGroup()', 'Modifier.fillMaxSize().focusGroup()'),
    (m, 'if (consumed != 0f) change.consume()', 'if (false) change.consume()')
]
for mutation in mutations:
    mutate(*mutation)

with tempfile.TemporaryDirectory(prefix='vulkanscope-1407-control-') as td:
    candidate = Path(td) / 'tree'
    shutil.copytree(root, candidate)
    path = candidate / 'BUILD_AUDIT.md'
    path.write_text(path.read_text(encoding='utf-8') + '\nUnrelated 1.4.7 verifier false-positive control.\n', encoding='utf-8')
    result = run(candidate)
    if result.returncode != 0:
        print(result.stdout)
        raise SystemExit('false-positive control failed')
print(f'release_1407 negative mutations: PASS mutations={len(mutations)} falsePositiveControls=1')
