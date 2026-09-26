#!/usr/bin/env python3
import importlib.util
import shutil
import sys
import tempfile
from pathlib import Path

sys.dont_write_bytecode = True
root = Path(__file__).resolve().parents[1]
spec = importlib.util.spec_from_file_location('verify_release_1415', root / 'tools/verify_release_1415.py')
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)


def replace(path: Path, old: str, new: str):
    text = path.read_text(encoding='utf-8')
    if old not in text:
        raise SystemExit(f'mutation target missing: {old[:120]}')
    path.write_text(text.replace(old, new, 1), encoding='utf-8')


def mutated(mutator):
    with tempfile.TemporaryDirectory(prefix='vulkanscope-1415-mutation-') as temp:
        target = Path(temp) / 'project'
        shutil.copytree(root, target)
        mutator(target)
        return module.verify(target)


def main():
    main_file = 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
    mutations = [
        mutated(lambda t: replace(t / main_file, '.widthIn(max = 352.dp)', '.widthIn(max = 680.dp)')),
        mutated(lambda t: replace(t / main_file, 'val bottomOffset = navigationBottom + if (landscape) 16.dp else 6.dp', 'val bottomOffset = navigationBottom + 6.dp')),
        mutated(lambda t: (t / main_file).write_text((t / main_file).read_text(encoding='utf-8').replace('delay(46)', 'delay(0)'), encoding='utf-8')),
        mutated(lambda t: replace(t / main_file, '.width(cellWidth * (rightEdge.value - leftEdge.value).coerceAtLeast(0.01f))', '.width(cellWidth)')),
        mutated(lambda t: replace(t / main_file, 'bottom = navigationOverlayClearance + 4.dp', 'bottom = 10.dp')),
        mutated(lambda t: replace(t / main_file, 'Page.Properties, Page.Profiles, Page.Encyclopedia', 'Page.Properties, Page.Encyclopedia')),
    ]
    if any(not errors for errors in mutations):
        raise SystemExit('a targeted negative mutation incorrectly passed')
    with tempfile.TemporaryDirectory(prefix='vulkanscope-1415-control-') as temp:
        target = Path(temp) / 'project'
        shutil.copytree(root, target)
        database = target / 'DATABASE_SETUP.md'
        database.write_text(database.read_text(encoding='utf-8') + '\n', encoding='utf-8')
        if module.verify(target):
            raise SystemExit('unrelated documentation mutation incorrectly failed')
    print('VulkanScope 1.4.15 negative mutations: PASS')


if __name__ == '__main__':
    main()
