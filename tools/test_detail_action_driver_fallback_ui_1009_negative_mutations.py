#!/usr/bin/env python3
import shutil
import subprocess
import tempfile
import re
from pathlib import Path

root = Path(__file__).resolve().parents[1]
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
vm = re.search(r'versionName\s*=\s*"(\d+)\.(\d+)\.(\d+)"', gradle)
current_version = tuple(map(int, vm.groups())) if vm else (0, 0, 0)


def replace_once(path, old, new):
    text = path.read_text(encoding='utf-8')
    if old not in text:
        raise AssertionError('mutation anchor missing: ' + old[:140])
    path.write_text(text.replace(old, new, 1), encoding='utf-8')


def run(name, mutator, expect_fail=True):
    with tempfile.TemporaryDirectory(prefix='vs1009mut_') as td:
        dst = Path(td) / 'root'
        shutil.copytree(root, dst, ignore=shutil.ignore_patterns('.gradle', 'build', '.cxx'))
        mutator(dst)
        result = subprocess.run(['python3', str(dst / 'tools/verify_detail_action_driver_fallback_ui_1009.py'), '--root', str(dst)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        if expect_fail and result.returncode == 0:
            raise AssertionError(name + ': verifier accepted defect\n' + result.stdout)
        if not expect_fail and result.returncode != 0:
            raise AssertionError(name + ': false-positive control failed\n' + result.stdout)


mutations = [
    ('remove dialog max height', lambda d: (d / main_rel).write_text((d / main_rel).read_text(encoding='utf-8').replace('.heightIn(max = dialogMaxHeight)', ''), encoding='utf-8')),
    ('remove flexible dialog body', lambda d: replace_once(d / main_rel, '.weight(1f, fill = false)', '')),
    ('shrink icon text target', lambda d: replace_once(d / main_rel, 'private fun ExpressiveContainedIconTextButton(label: String, icon: Int, modifier: Modifier = Modifier, enabled: Boolean = true, fontWeight: FontWeight = FontWeight.SemiBold, onClick: () -> Unit) {\n    TextButton(\n        onClick = onClick,\n        enabled = enabled,\n        modifier = modifier.heightIn(min = 48.dp)', 'private fun ExpressiveContainedIconTextButton(label: String, icon: Int, modifier: Modifier = Modifier, enabled: Boolean = true, fontWeight: FontWeight = FontWeight.SemiBold, onClick: () -> Unit) {\n    TextButton(\n        onClick = onClick,\n        enabled = enabled,\n        modifier = modifier.heightIn(min = 20.dp)')),
    ('restore row-wide action card', lambda d: replace_once(d / main_rel, 'Surface(\n        color = container,', 'Card(\n        onClick = onClick,\n        colors = CardDefaults.cardColors(containerColor = container),')),
    ('restore Overview whole-card click', lambda d: replace_once(d / main_rel, 'onClick = onClick,', 'onClick = { },') if current_version >= (1, 2, 2) else replace_once(d / main_rel, 'onClick = { navigate(destination) },', 'onClick = { },')),
    ('remove System cached evidence', lambda d: replace_once(d / main_rel, 'private fun readSystemDriverSummary(', 'private fun readSystemDriverSummaryBROKEN(')),
    ('misattribute Turnip in System dialog', lambda d: replace_once(d / main_rel, 'CapabilityKeyValue("Evidence source", evidenceSource)', 'CapabilityKeyValue("Evidence source", "Turnip current report")')),
    ('uncontain provenance', lambda d: replace_once(d / main_rel, 'ExpressiveContainedIconTextButton("Evidence provenance", R.drawable.ic_evidence)', 'ExpressiveTextButton("Evidence provenance")')),
    ('uncontain watch add', lambda d: replace_once(d / main_rel, 'ExpressiveContainedIconTextButton("Add to watch list", R.drawable.ic_watch_add, modifier', 'ExpressiveTextButton("Add to watch list", modifier')),
    ('remove copy-link icon', lambda d: replace_once(d / main_rel, 'TransientActionButton("Copy link", "Copy permalink to clipboard", R.drawable.ic_copy)', 'TransientActionButton("Copy link", "Copy permalink to clipboard", R.drawable.ic_link)') if current_version >= (1, 2, 0) else replace_once(d / main_rel, 'ExpressiveContainedIconTextButton("Copy link", R.drawable.ic_link)', 'ExpressiveContainedTextButton("Copy link")')) ,
    ('remove long press feedback', lambda d: replace_once(d / main_rel, 'graphicsLayer(scaleX = pressScale, scaleY = pressScale)', 'graphicsLayer()')),
    ('enable Database input offline', lambda d: replace_once(d / main_rel, 'enabled = networkAvailable,\n                    placeholderText = "64-character report id"', 'enabled = true,\n                    placeholderText = "64-character report id"')),
    ('make offline reason blue', lambda d: replace_once(d / main_rel, 'Public Database report-id lookup is locked until Android reports a validated internet connection.", color = ComposeColor(0xFFFFC857)', 'Public Database report-id lookup is locked until Android reports a validated internet connection.", color = ComposeColor(0xFF9CCBFF)')),
    ('remove analysis filename guidance', lambda d: (d / main_rel).write_text((d / main_rel).read_text(encoding='utf-8').replace('VulkanScope-*-analysis.json', '*-analysis.json'), encoding='utf-8')),
    ('remove Turnip exact-name guidance', lambda d: (d / main_rel).write_text((d / main_rel).read_text(encoding='utf-8').replace('turnip_01.zip through turnip_10.zip', 'Turnip ZIP files'), encoding='utf-8')),
    ('broaden analysis storage', lambda d: replace_once(d / main_rel, 'roots += File(context.filesDir, "analysis_exchange")', 'roots += Environment.getExternalStorageDirectory()')),
]

for name, mutation in mutations:
    run(name, mutation, True)
run('unrelated changelog wording', lambda d: (d / 'changelog.md').write_text((d / 'changelog.md').read_text(encoding='utf-8') + '\nUnrelated retained history wording.\n', encoding='utf-8'), False)
print(f'PASS VulkanScope 1.0.9 negative mutations: {len(mutations)} defects rejected + false-positive control')
