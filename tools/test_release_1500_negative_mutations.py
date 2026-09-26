#!/usr/bin/env python3
import importlib.util
import shutil
import sys
import tempfile
from pathlib import Path

sys.dont_write_bytecode = True
root = Path(__file__).resolve().parents[1]
spec = importlib.util.spec_from_file_location('verify_release_1500', root / 'tools/verify_release_1500.py')
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)


def replace(path: Path, old: str, new: str):
    text = path.read_text(encoding='utf-8')
    if old not in text:
        raise SystemExit(f'mutation target missing: {old[:120]}')
    path.write_text(text.replace(old, new, 1), encoding='utf-8')


def mutated(mutator):
    with tempfile.TemporaryDirectory(prefix='vulkanscope-1500-mutation-') as temp:
        target = Path(temp) / 'project'
        shutil.copytree(root, target)
        mutator(target)
        return module.verify(target)


def main():
    main_file = 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
    mutations = [
        mutated(lambda t: replace(t / main_file, 'PrimaryNavigationMaxWidth = 326.dp', 'PrimaryNavigationMaxWidth = 352.dp')),
        mutated(lambda t: replace(t / main_file, 'PrimaryNavigationHeight = 54.dp', 'PrimaryNavigationHeight = 62.dp')),
        mutated(lambda t: replace(t / main_file, 'indicatorStretch.animateTo(1.24f', 'indicatorStretch.animateTo(1.90f')),
        mutated(lambda t: replace(t / main_file, 'bottom = PrimaryNavigationBottomGap', 'bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + PrimaryNavigationBottomGap')),
        mutated(lambda t: replace(t / main_file, 'PrimaryNavigationBottomGap + PrimaryNavigationHeight + PrimaryNavigationContentGap', 'navigationPadding.calculateBottomPadding() + PrimaryNavigationBottomGap + PrimaryNavigationHeight + PrimaryNavigationContentGap')),
        mutated(lambda t: replace(t / main_file, 'widthIn(max = 520.dp)', 'widthIn(max = 680.dp)')),
        mutated(lambda t: replace(t / main_file, 'delay(900L)', 'delay(90_000L)')),
        mutated(lambda t: replace(t / main_file, 'armOpeningSequenceWatchdog()', 'Unit')),
    ]
    if any(not errors for errors in mutations):
        raise SystemExit('a targeted negative mutation incorrectly passed')
    with tempfile.TemporaryDirectory(prefix='vulkanscope-1500-control-') as temp:
        target = Path(temp) / 'project'
        shutil.copytree(root, target)
        database = target / 'DATABASE_SETUP.md'
        database.write_text(database.read_text(encoding='utf-8') + '\n', encoding='utf-8')
        if module.verify(target):
            raise SystemExit('unrelated documentation mutation incorrectly failed')
    print('VulkanScope 1.5.0 negative mutations: PASS')


if __name__ == '__main__':
    main()
