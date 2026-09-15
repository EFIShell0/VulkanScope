#!/usr/bin/env python3
import shutil, subprocess, tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
VER = ROOT / 'tools/verify_release_1400.py'
MAIN = 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'

def run(root, expected, skip=False):
    cmd = ['python3', str(VER), '--root', str(root)] + (['--skip-version'] if skip else [])
    proc = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if (proc.returncode == 0) != expected:
        raise AssertionError(proc.stdout)

def mutate(label, old, new):
    with tempfile.TemporaryDirectory(prefix='vs1400_') as tmp:
        tree = Path(tmp) / 'root'
        shutil.copytree(ROOT, tree)
        path = tree / MAIN
        text = path.read_text()
        if old not in text:
            raise AssertionError(label + ' anchor missing')
        path.write_text(text.replace(old, new, 1))
        run(tree, False)

def main():
    run(ROOT, True)
    mutate('single arrow no longer toggles', '.clickable(enabled = enabled, role = Role.Button) { expanded = !expanded }', '.clickable(enabled = enabled && !expanded, role = Role.Button) { expanded = true }')
    mutate('Format explorer unified filter removed', 'ExpressiveMultiFilterBar(FORMAT_USAGE_FILTERS.keys.toList(), usageFilters)', 'Text("Legacy usage filter")')
    mutate('legacy FilterChip restored', 'private fun ExpressiveMultiFilterBar(', 'private fun LegacyFilterChipMarker() { FilterChip() }\n\nprivate fun ExpressiveMultiFilterBar(')
    mutate('Quick access icon mapping restored to home', 'title.contains("quick access", true) -> R.drawable.ic_quick_access_grid', 'title.contains("quick access", true) -> R.drawable.ic_home')
    with tempfile.TemporaryDirectory(prefix='vs1400_icon_') as tmp:
        tree = Path(tmp) / 'root'
        shutil.copytree(ROOT, tree)
        icon = tree / 'app/src/main/res/drawable/ic_quick_access_grid.xml'
        icon.write_text(icon.read_text().replace('M5.5,3.5', 'M6.5,3.5', 1))
        run(tree, False)
    with tempfile.TemporaryDirectory(prefix='vs1400_ok_') as tmp:
        tree = Path(tmp) / 'root'
        shutil.copytree(ROOT, tree)
        changelog = tree / 'changelog.md'
        changelog.write_text(changelog.read_text() + '\n')
        run(tree, True)
    print('release_1400 negative mutations: PASS')

if __name__ == '__main__':
    main()
