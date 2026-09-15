#!/usr/bin/env python3
import shutil, subprocess, tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
VERIFIER = ROOT / 'tools/verify_release_1309.py'
MAIN = 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'


def run(root: Path, should_pass: bool):
    proc = subprocess.run(['python3', str(VERIFIER), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if (proc.returncode == 0) != should_pass:
        raise AssertionError(proc.stdout)


def mutate(label: str, relative: str, old: str, new: str):
    with tempfile.TemporaryDirectory(prefix='vs1309_') as temp:
        dst = Path(temp) / 'root'
        shutil.copytree(ROOT, dst)
        path = dst / relative
        text = path.read_text(encoding='utf-8')
        if old not in text:
            raise AssertionError(f'{label}: mutation anchor missing')
        path.write_text(text.replace(old, new, 1), encoding='utf-8')
        run(dst, False)


def main():
    run(ROOT, True)
    mutate('live-label state keyed again', MAIN,
           'var expanded by rememberSaveable { mutableStateOf(false) }',
           'var expanded by rememberSaveable(labels) { mutableStateOf(false) }')
    mutate('popup auto Back dismissal returned', MAIN,
           'properties = PopupProperties(focusable = true, dismissOnBackPress = false, dismissOnClickOutside = true)',
           'properties = PopupProperties(focusable = true, dismissOnBackPress = true, dismissOnClickOutside = true)')
    mutate('IME-first Back removed', MAIN,
           'if (imeVisible) focusManager.clearFocus(force = true) else expanded = false',
           'expanded = false')
    mutate('selector anchor removed', MAIN,
           'val desiredX = if (layoutDirection == LayoutDirection.Ltr) anchorBounds.left else anchorBounds.right - popupContentSize.width',
           'val desiredX = 0')
    mutate('TXT export weighted again', MAIN,
           'R.drawable.ic_action_text,\n                        Modifier.fillMaxWidth(),',
           'R.drawable.ic_action_text,\n                        Modifier.weight(1f),')
    mutate('fixed export type removed', MAIN,
           'val fixedName = "${filenameBase.trim()}.$extension"',
           'val fixedName = filenameBase.trim()')
    mutate('grid mode removed', MAIN,
           'private enum class TurnipFileManagerViewMode { LIST, COMPACT, GRID, DETAILS }',
           'private enum class TurnipFileManagerViewMode { LIST, COMPACT, DETAILS }')
    mutate('package-card clipping/click target removed', MAIN,
           '.clip(shape).clickable(enabled = enabled, role = Role.Checkbox) { onSelectedChange() }',
           '.clickable(enabled = enabled, role = Role.Checkbox) { onSelectedChange() }')
    mutate('legacy storage copy returned', MAIN,
           "Analysis JSON exchange uses VulkanScope's in-app shared-storage browser; storage access is requested only after an explicit import/export action.",
           "Analysis JSON exchange uses VulkanScope's in-app shared-storage browser. SAF stays removed; storage access is requested only after an explicit import/export action.")
    with tempfile.TemporaryDirectory(prefix='vs1309_ok_') as temp:
        dst = Path(temp) / 'root'
        shutil.copytree(ROOT, dst)
        changelog = dst / 'changelog.md'
        changelog.write_text(changelog.read_text(encoding='utf-8') + '\n', encoding='utf-8')
        run(dst, True)
    print('release_1309 negative mutations: PASS')


if __name__ == '__main__':
    main()
