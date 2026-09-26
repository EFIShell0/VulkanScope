#!/usr/bin/env python3
import importlib.util
import shutil
import sys
import tempfile
from pathlib import Path

sys.dont_write_bytecode = True
root = Path(__file__).resolve().parents[1]
spec = importlib.util.spec_from_file_location('verify_release_1414', root / 'tools/verify_release_1414.py')
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)


def replace(path: Path, old: str, new: str):
    text = path.read_text(encoding='utf-8')
    if old not in text:
        raise SystemExit(f'mutation target missing: {old[:100]}')
    path.write_text(text.replace(old, new, 1), encoding='utf-8')


def mutated(mutator):
    with tempfile.TemporaryDirectory(prefix='vulkanscope-1414-mutation-') as temp:
        target = Path(temp) / 'project'
        shutil.copytree(root, target)
        mutator(target)
        return module.verify(target)


def main():
    main_file = 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
    mutations = [
        mutated(lambda t: replace(t / main_file, 'NavigationItem(Page.Extensions, "Extensions", R.drawable.ic_extensions)', '')),
        mutated(lambda t: replace(t / main_file, 'ComposeColor(0xCC17171B)', 'ComposeColor(0xFF17171B)')),
        mutated(lambda t: replace(t / main_file, 'modifier = Modifier\n                            .weight(1f)\n                            .fillMaxHeight()', 'modifier = Modifier\n                            .width(64.dp)\n                            .fillMaxHeight()')),
        mutated(lambda t: replace(t / main_file, 'modifier = Modifier.align(Alignment.BottomCenter)', 'modifier = Modifier')),
        mutated(lambda t: replace(t / main_file, 'if (useRail) 0.dp else 82.dp', '0.dp')),
        mutated(lambda t: replace(t / main_file, 'Page.Properties, Page.Profiles, Page.Encyclopedia', 'Page.Properties, Page.Encyclopedia')),
    ]
    if any(not errors for errors in mutations):
        raise SystemExit('a targeted negative mutation incorrectly passed')
    with tempfile.TemporaryDirectory(prefix='vulkanscope-1414-control-') as temp:
        target = Path(temp) / 'project'
        shutil.copytree(root, target)
        database = target / 'DATABASE_SETUP.md'
        database.write_text(database.read_text(encoding='utf-8') + '\n', encoding='utf-8')
        if module.verify(target):
            raise SystemExit('unrelated documentation mutation incorrectly failed')
    print('VulkanScope 1.4.14 negative mutations: PASS')


if __name__ == '__main__':
    main()
