#!/usr/bin/env python3
import importlib.util
import shutil
import sys
import tempfile
from pathlib import Path

sys.dont_write_bytecode = True
root = Path(__file__).resolve().parents[1]
spec = importlib.util.spec_from_file_location('verify_release_1413', root / 'tools/verify_release_1413.py')
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)


def replace(path: Path, old: str, new: str):
    text = path.read_text(encoding='utf-8')
    if old not in text:
        raise SystemExit(f'mutation target missing: {old[:100]}')
    path.write_text(text.replace(old, new, 1), encoding='utf-8')


def mutated(mutator):
    with tempfile.TemporaryDirectory(prefix='vulkanscope-1413-mutation-') as temp:
        target = Path(temp) / 'project'
        shutil.copytree(root, target)
        mutator(target)
        return module.verify(target)


def main():
    main_file = 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
    mutations = [
        mutated(lambda t: replace(t / main_file, 'ShortNavigationBar(', 'NavigationBar(')),
        mutated(lambda t: replace(t / main_file, 'ShortNavigationBarItem(', 'NavigationBarItem(')),
        mutated(lambda t: replace(t / main_file, 'NavigationRailItem(', 'Box(')),
        mutated(lambda t: replace(t / main_file, 'Page.Properties, Page.Profiles, Page.Encyclopedia', 'Page.Properties, Page.Encyclopedia')),
        mutated(lambda t: replace(t / main_file, 'tint = VulkanAccentSoft,\n                    modifier = Modifier.size(20.dp)', 'tint = ComposeColor.Unspecified,\n                    modifier = Modifier.size(27.dp)')),
    ]
    if any(not errors for errors in mutations):
        raise SystemExit('a targeted negative mutation incorrectly passed')
    with tempfile.TemporaryDirectory(prefix='vulkanscope-1413-control-') as temp:
        target = Path(temp) / 'project'
        shutil.copytree(root, target)
        changelog = target / 'changelog.md'
        changelog.write_text(changelog.read_text(encoding='utf-8') + '\n', encoding='utf-8')
        if module.verify(target):
            raise SystemExit('documentation-only false-positive control failed')
    print('VulkanScope 1.4.13 negative mutations: PASS')


if __name__ == '__main__':
    main()
