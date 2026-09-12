#!/usr/bin/env python3
import shutil
import subprocess
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')


def replace_once(path, old, new):
    text = path.read_text(encoding='utf-8')
    if old not in text:
        raise AssertionError('mutation anchor missing: ' + old[:120])
    path.write_text(text.replace(old, new, 1), encoding='utf-8')


def run(name, mutator, expect_fail=True):
    with tempfile.TemporaryDirectory(prefix='vs1008mut_') as td:
        dst = Path(td) / 'root'
        shutil.copytree(root, dst, ignore=shutil.ignore_patterns('.gradle', 'build', '.cxx'))
        mutator(dst)
        result = subprocess.run(['python3', str(dst / 'tools/verify_driver_manager_dialog_storage_ui_1008.py'), '--root', str(dst), '--skip-version'], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        if expect_fail and result.returncode == 0:
            raise AssertionError(name + ': verifier accepted defect\n' + result.stdout)
        if not expect_fail and result.returncode != 0:
            raise AssertionError(name + ': false-positive control failed\n' + result.stdout)


mutations = [
    ('restore separate manager card', lambda d: replace_once(d / main_rel, 'CapabilitySectionCard("Driver manager")', 'CapabilitySectionCard("Turnip driver manager")')),
    ('remove System active pill', lambda d: replace_once(d / main_rel, 'if (active) TurnipStatePill("ACTIVE", true, true)', 'if (active) Text("Active")')),
    ('remove Overview bold source', lambda d: replace_once(d / main_rel, 'fontWeight = FontWeight.Bold\n                    )', 'fontWeight = FontWeight.Normal\n                    )')),
    ('make unknown amber', lambda d: replace_once(d / main_rel, '"UNKNOWN", "UNRESOLVED" -> ComposeColor(0xFFA8A8A8)', '"UNKNOWN", "UNRESOLVED" -> ComposeColor(0xFFFFC857)')),
    ('restore unavailable Details', lambda d: replace_once(d / main_rel, 'if (!unavailable) DetailAffordance { onDetails(driver) }', 'DetailAffordance { onDetails(driver) }')),
    ('remove unavailable strike', lambda d: replace_once(d / main_rel, 'textDecoration = TextDecoration.LineThrough', 'textDecoration = TextDecoration.None')),
    ('remove question icon', lambda d: replace_once(d / main_rel, 'R.drawable.ic_question', 'R.drawable.ic_info')),
    ('make Cancel bold', lambda d: replace_once(d / main_rel, 'Text("Cancel", fontWeight = FontWeight.Normal', 'Text("Cancel", fontWeight = FontWeight.Bold')),
    ('remove delete trash', lambda d: replace_once(d / main_rel, 'ExpressiveContainedIconTextButton("Delete", R.drawable.ic_delete, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)', 'ExpressiveContainedTextButton("Delete"')),
    ('uncontain baseline', lambda d: replace_once(d / main_rel, 'ExpressiveContainedIconTextButton("Use as baseline", R.drawable.ic_baseline, modifier = Modifier.weight(1f))', 'ExpressiveTextButton("Use as baseline")')),
    ('make history Delete non-bold', lambda d: replace_once(d / main_rel, 'ExpressiveContainedIconTextButton("Delete", R.drawable.ic_delete, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)', 'ExpressiveContainedIconTextButton("Delete", R.drawable.ic_delete, modifier = Modifier.weight(1f))')),
    ('make toggle row clickable', lambda d: replace_once(d / main_rel, 'private fun ExpressiveToggleRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {\n    Row(\n        Modifier.fillMaxWidth().padding(vertical = 4.dp)', 'private fun ExpressiveToggleRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {\n    Row(\n        Modifier.fillMaxWidth().toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange).padding(vertical = 4.dp)')),
    ('make external row whole-card clickable', lambda d: replace_once(d / main_rel, 'Surface(color = if (enabled) ComposeColor(0xFF1A1718)', 'Card(onClick = onOpen, enabled = enabled, colors = CardDefaults.cardColors(containerColor = if (enabled) ComposeColor(0xFF1A1718)')),
    ('remove external icon', lambda d: replace_once(d / main_rel, 'Icon(painterResource(R.drawable.ic_open_external), contentDescription = "Open external link"', 'Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = "Open external link"')),
    ('remove analysis file scan bound', lambda d: replace_once(d / main_rel, '.asSequence().take(64).forEach { file ->', '.asSequence().forEach { file ->')),
    ('broaden analysis storage', lambda d: replace_once(d / main_rel, 'roots += File(context.filesDir, "analysis_exchange")', 'roots += Environment.getExternalStorageDirectory()')),
    ('remove picker launch fallback', lambda d: replace_once(d / main_rel, 'if (!tryLaunchSystemDocumentPicker { exportLauncher.launch(filename) }) exportSnapshotFallback()', 'exportLauncher.launch(filename)')),
    ('suppress connectivity during collection', lambda d: replace_once(d / main_rel, 'OfflineFeatureAvailabilityBanner(collectionStatus == CollectionStatus.COLLECTING)', 'if (collectionStatus != CollectionStatus.COLLECTING) OfflineFeatureAvailabilityBanner(false)')),
]

for name, mutation in mutations:
    run(name, mutation, True)
run('unrelated changelog wording', lambda d: (d / 'changelog.md').write_text((d / 'changelog.md').read_text(encoding='utf-8') + '\nUnrelated historical wording.\n', encoding='utf-8'), False)
print(f'PASS VulkanScope 1.0.8 negative mutations: {len(mutations)} defects rejected + false-positive control')
