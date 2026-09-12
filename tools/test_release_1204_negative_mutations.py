#!/usr/bin/env python3
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_release_1204.py'
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
gradle_rel = Path('app/build.gradle.kts')

def make_tree(temp):
    for rel in [main_rel, gradle_rel]:
        dst = temp / rel
        dst.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(root / rel, dst)

def mutate_text(old, new, rel=main_rel, expect_pass=False):
    with tempfile.TemporaryDirectory(prefix='vs1204-neg-') as name:
        temp = Path(name)
        make_tree(temp)
        target = temp / rel
        text = target.read_text(encoding='utf-8')
        if old not in text:
            raise SystemExit(f'mutation source missing: {old}')
        target.write_text(text.replace(old, new, 1), encoding='utf-8')
        result = subprocess.run([sys.executable, str(verifier), '--root', str(temp)], text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        if (result.returncode == 0) != expect_pass:
            print(result.stdout)
            raise SystemExit(f'unexpected verifier result for mutation: {old}')

current_gradle = (root / gradle_rel).read_text(encoding='utf-8')
current_name = re.search(r'versionName\s*=\s*"[^"]+"', current_gradle).group(0)
mutate_text(current_name, 'versionName = "1.2.3"', gradle_rel)
mutate_text('var pendingHistoryDeleteAll by mutableStateOf(false)', 'var pendingHistoryDeleteAll by mutableStateOf(true)')
mutate_text('enabled = state.history.isNotEmpty()) { state.pendingHistoryDeleteAll = true }', 'enabled = true) { state.pendingHistoryDeleteAll = true }')
mutate_text('QuestionDialogTitle("Delete all analysis history?")', 'QuestionDialogTitle("History")')
mutate_text('analysisModel.deleteAllHistory()', 'analysisModel.state.history = emptyList()')
mutate_text('LaunchedEffect(Unit) { cardsVisible = true }', 'LaunchedEffect(Unit) { cardsVisible = false }')
mutate_text('fadeIn(tween(durationMillis = 260, delayMillis = index * 45))', 'fadeIn(tween(durationMillis = 0, delayMillis = 0))')
mutate_text('slideInHorizontally(tween(durationMillis = 260, delayMillis = index * 45)) { it / 10 }', 'slideInHorizontally(tween(durationMillis = 0)) { 0 }')
mutate_text('ExpressiveDestinationCard(section.label, section.description, section.icon)', 'CapabilityItemCard { Text(section.label) }')
mutate_text('trailingIcon = R.drawable.ic_open_external) { model.shareLink() }', 'trailingIcon = R.drawable.ic_link) { model.shareLink() }')
mutate_text('trailingIcon = R.drawable.ic_open_external) { shareEvidenceText(context, "$key = $value") }', 'trailingIcon = R.drawable.ic_link) { shareEvidenceText(context, "$key = $value") }')
mutate_text('Vulkan capability and device inspection utility', 'Vulkan capability/device inspection utility', expect_pass=True)
print('PASS VulkanScope 1.2.4 negative mutations and unrelated false-positive control')
