#!/usr/bin/env python3
import importlib.util
import shutil
import sys
import tempfile
from pathlib import Path

sys.dont_write_bytecode = True
root = Path(__file__).resolve().parents[1]
spec = importlib.util.spec_from_file_location('verify_release_1501', root / 'tools/verify_release_1501.py')
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)


def replace(path: Path, old: str, new: str):
    value = path.read_text(encoding='utf-8')
    if old not in value:
        raise SystemExit(f'mutation target missing: {old[:120]}')
    path.write_text(value.replace(old, new, 1), encoding='utf-8')


def mutated(mutator):
    with tempfile.TemporaryDirectory(prefix='vulkanscope-1501-mutation-') as temp:
        target = Path(temp) / 'project'
        shutil.copytree(root, target)
        mutator(target)
        return module.verify(target)


def main():
    main_file = 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
    mutations = [
        mutated(lambda t: replace(t / main_file, 'PrimaryNavigationMaxWidth = 310.dp', 'PrimaryNavigationMaxWidth = 352.dp')),
        mutated(lambda t: replace(t / main_file, 'contentWindowInsets = WindowInsets(0, 0, 0, 0)', 'contentWindowInsets = ScaffoldDefaults.contentWindowInsets')),
        mutated(lambda t: replace(t / main_file, 'indication = null', 'indication = LocalIndication.current')),
        mutated(lambda t: replace(t / main_file, 'scaleY = 0.96f + indicatorAlpha * 0.04f\n                                }\n                                .clip(RoundedCornerShape(999.dp))', 'scaleY = 0.96f + indicatorAlpha * 0.04f\n                                }\n                                .clip(RoundedCornerShape(4.dp))')),
        mutated(lambda t: replace(t / main_file, 'val metRequirementCount: Int = 0', 'val acceptedRequirementCount: Int = 0')),
        mutated(lambda t: replace(t / main_file, 'put("unknownRequirementCount", profile.unknownRequirementCount)', 'put("unknownRequirementCount", profile.failedRequirementCount)')),
        mutated(lambda t: replace(t / main_file, 'evidence.recordCheck(evidence.groupChecks, groupState)', 'Unit')),
        mutated(lambda t: replace(t / main_file, 'delay(280L)', 'delay(1_200L)')),
    ]
    if any(not errors for errors in mutations):
        raise SystemExit('a targeted negative mutation incorrectly passed')
    with tempfile.TemporaryDirectory(prefix='vulkanscope-1501-control-') as temp:
        target = Path(temp) / 'project'
        shutil.copytree(root, target)
        database = target / 'DATABASE_SETUP.md'
        database.write_text(database.read_text(encoding='utf-8') + '\n', encoding='utf-8')
        if module.verify(target):
            raise SystemExit('unrelated documentation mutation incorrectly failed')
    print('VulkanScope 1.5.1 negative mutations: PASS')


if __name__ == '__main__':
    main()
