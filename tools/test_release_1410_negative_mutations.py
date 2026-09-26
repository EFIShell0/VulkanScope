#!/usr/bin/env python3
import importlib.util
import shutil
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
spec = importlib.util.spec_from_file_location('verify_release_1410', root / 'tools/verify_release_1410.py')
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)


def mutated(mutator):
    with tempfile.TemporaryDirectory(prefix='vulkanscope-1410-mutation-') as temp:
        target = Path(temp) / 'project'
        shutil.copytree(root, target)
        mutator(target)
        return module.verify(target)


def replace(path, old, new):
    text = path.read_text(encoding='utf-8')
    if old not in text:
        raise SystemExit('mutation target missing')
    path.write_text(text.replace(old, new, 1), encoding='utf-8')


def main():
    main_file = 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
    failures = []
    failures.append(mutated(lambda t: replace(t / main_file, 'title.equals("Opening animation", true) -> R.drawable.ic_opening_animation_toggle', 'title.equals("Opening animation", true) -> R.drawable.ic_info')))
    failures.append(mutated(lambda t: replace(t / main_file, 'painterResource(R.drawable.ic_opening_animation_toggle)', 'painterResource(R.drawable.ic_info)')))
    failures.append(mutated(lambda t: replace(t / main_file, 'Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {\n                        Text("VulkanScope opening animation"', 'Image(painter = painterResource(R.drawable.ic_opening_animation_toggle), contentDescription = null)\n                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {\n                        Text("VulkanScope opening animation"')))
    failures.append(mutated(lambda t: (t / 'app/src/main/res/drawable-nodpi/ic_opening_animation_toggle.png').unlink()))
    if any(not result for result in failures):
        raise SystemExit('a targeted negative mutation incorrectly passed')
    with tempfile.TemporaryDirectory(prefix='vulkanscope-1410-control-') as temp:
        target = Path(temp) / 'project'
        shutil.copytree(root, target)
        changelog = target / 'changelog.md'
        changelog.write_text(changelog.read_text(encoding='utf-8') + '\n', encoding='utf-8')
        if module.verify(target):
            raise SystemExit('documentation-only false-positive control failed')
    print('VulkanScope 1.4.10 negative mutations: PASS')


if __name__ == '__main__':
    main()
