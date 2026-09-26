#!/usr/bin/env python3
import importlib.util
import shutil
import sys
import tempfile
from pathlib import Path

sys.dont_write_bytecode = True
root = Path(__file__).resolve().parents[1]
spec = importlib.util.spec_from_file_location('verify_release_1502', root / 'tools/verify_release_1502.py')
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)


def replace(path: Path, old: str, new: str):
    value = path.read_text(encoding='utf-8')
    if old not in value:
        raise SystemExit(f'mutation target missing: {old[:120]}')
    path.write_text(value.replace(old, new, 1), encoding='utf-8')


def mutated(mutator):
    with tempfile.TemporaryDirectory(prefix='vulkanscope-1502-mutation-') as temp:
        target = Path(temp) / 'project'
        shutil.copytree(root, target)
        mutator(target)
        return module.verify(target)


def main():
    main_file = 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
    mutations = [
        mutated(lambda t: replace(t / 'build.gradle.kts', 'version "9.4.1"', 'version "9.4.0"')),
        mutated(lambda t: replace(t / 'build.gradle.kts', 'version "9.4.1"', 'version "9.5.0-alpha01"')),
        mutated(lambda t: replace(t / 'gradle/wrapper/gradle-wrapper.properties', 'gradle-9.7.1-bin.zip', 'gradle-9.5.0-bin.zip')),
        mutated(lambda t: replace(t / 'app/build.gradle.kts', 'compileSdk = 37', 'compileSdk = 38')),
        mutated(lambda t: replace(t / 'app/src/main/AndroidManifest.xml', 'android:usesCleartextTraffic="false"', 'android:usesCleartextTraffic="true"')),
        mutated(lambda t: replace(t / main_file, 'activityScope.cancel()', 'Unit')),
        mutated(lambda t: replace(t / main_file, '.followRedirects(false)', '.followRedirects(true)')),
        mutated(lambda t: replace(t / main_file, 'PrimaryNavigationHeight = 54.dp', 'PrimaryNavigationHeight = 44.dp')),
        mutated(lambda t: replace(t / main_file, 'role = Role.Tab', 'role = null')),
        mutated(lambda t: replace(t / 'registry/registry_lock.json', '"headerVersion": 364', '"headerVersion": 363')),
    ]
    if any(not errors for errors in mutations):
        raise SystemExit('a targeted 1.5.2 negative mutation incorrectly passed')
    with tempfile.TemporaryDirectory(prefix='vulkanscope-1502-control-') as temp:
        target = Path(temp) / 'project'
        shutil.copytree(root, target)
        database = target / 'DATABASE_SETUP.md'
        database.write_text(database.read_text(encoding='utf-8') + '\n', encoding='utf-8')
        if module.verify(target):
            raise SystemExit('unrelated documentation mutation incorrectly failed')
    print('VulkanScope 1.5.2 negative mutations: PASS')


if __name__ == '__main__':
    main()
