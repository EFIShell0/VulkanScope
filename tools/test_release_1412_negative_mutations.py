#!/usr/bin/env python3
import importlib.util
import sys
sys.dont_write_bytecode = True
import shutil
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
spec = importlib.util.spec_from_file_location('verify_release_1412', root / 'tools/verify_release_1412.py')
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)


def mutated(mutator):
    with tempfile.TemporaryDirectory(prefix='vulkanscope-1412-mutation-') as temp:
        target = Path(temp) / 'project'
        shutil.copytree(root, target)
        mutator(target)
        return module.verify(target)


def replace(path: Path, old: str, new: str):
    text = path.read_text(encoding='utf-8')
    if old not in text:
        raise SystemExit(f'mutation target missing: {old[:80]}')
    path.write_text(text.replace(old, new, 1), encoding='utf-8')


def main():
    main_file = 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
    mutations = []
    mutations.append(mutated(lambda t: replace(t / main_file, 'private fun profileHtmlDetails(profile: ProfileResult): String = buildString {\n    append(htmlEscape', 'private fun profileHtmlDetails(profile: ProfileResult): String = buildString {\n    append(statusBadge(profile.status))\n    append(htmlEscape')))
    mutations.append(mutated(lambda t: replace(t / main_file, 'Page.Properties, Page.Profiles, Page.Encyclopedia', 'Page.Properties, Page.Encyclopedia')))
    mutations.append(mutated(lambda t: replace(t / main_file, 'color = if (selected) VulkanAccentContainer else ComposeColor.Transparent', 'color = if (selected) ComposeColor(0xFF1E1516) else ComposeColor.Transparent')))
    mutations.append(mutated(lambda t: replace(t / main_file, 'tint = VulkanAccentSoft,\n                    modifier = Modifier.size(20.dp)', 'tint = ComposeColor.Unspecified,\n                    modifier = Modifier.size(27.dp)')))
    mutations.append(mutated(lambda t: replace(t / main_file, 'put("profileEvaluation", JSONArray().apply { vulkanProfileEvaluations(report, d).forEach { profile -> put(profileEvaluationJson(profile)) } })', 'put("profileEvaluation", JSONArray().apply { vulkanProfileEvaluations(report, d).forEach { profile -> put(JSONObject().put("name", profile.name).put("status", profile.status)) } })')))
    mutations.append(mutated(lambda t: replace(t / main_file, 'profileDetailPairs(p).forEach { (label, value) -> appendLine("  - $label: $value") }', 'Unit')))
    if any(not errors for errors in mutations):
        raise SystemExit('a targeted negative mutation incorrectly passed')
    with tempfile.TemporaryDirectory(prefix='vulkanscope-1412-control-') as temp:
        target = Path(temp) / 'project'
        shutil.copytree(root, target)
        changelog = target / 'changelog.md'
        changelog.write_text(changelog.read_text(encoding='utf-8') + '\n', encoding='utf-8')
        if module.verify(target):
            raise SystemExit('documentation-only false-positive control failed')
    print('VulkanScope 1.4.12 negative mutations: PASS')


if __name__ == '__main__':
    main()
