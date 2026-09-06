#!/usr/bin/env python3
import shutil
import subprocess
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_material3_expressive_ui_0813.py'

def run(candidate):
    return subprocess.run(['python', str(verifier), '--root', str(candidate)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True).returncode

def mutate_and_expect_failure(path, old, new, label):
    with tempfile.TemporaryDirectory() as tmp:
        candidate = Path(tmp) / 'root'
        shutil.copytree(root, candidate)
        target = candidate / path
        text = target.read_text(encoding='utf-8')
        if old not in text:
            raise SystemExit(f'mutation source missing: {label}')
        target.write_text(text.replace(old, new, 1), encoding='utf-8')
        if run(candidate) == 0:
            raise SystemExit(f'negative mutation passed unexpectedly: {label}')

mutate_and_expect_failure('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false))', 'AlertDialog(onDismissRequest = onDismiss,', 'complex dialog regression')
mutate_and_expect_failure('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'value.length > 34', 'value.length > 54', 'cramped key-value threshold')
mutate_and_expect_failure('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'end = 46.dp', 'end = 18.dp', 'scroll indicator overlap lane')
mutate_and_expect_failure('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'ComposeColor.Transparent else VulkanSurfaceTonal', 'VulkanSurfaceTonal else VulkanSurfaceTonal', 'boxed detail rows')
mutate_and_expect_failure('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'ExpressiveSwitch(checked = state.includeUnchanged, onCheckedChange = null)', 'Switch(checked = state.includeUnchanged, onCheckedChange = { state.includeUnchanged = it })', 'raw Analysis switch')
mutate_and_expect_failure('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'CapabilitySectionCard("Vulkan inspection")', 'Column { Text("Vulkan inspection") }', 'unstyled loading hierarchy')
mutate_and_expect_failure('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'import androidx.compose.ui.semantics.role\n', '', 'Compose role extension import')
mutate_and_expect_failure('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'modifier = Modifier.semantics { role = Role.Button }', 'modifier = Modifier', 'Details explicit Button role')
mutate_and_expect_failure('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'value.length > 32', 'value.length > 52', 'cramped update metadata threshold')
mutate_and_expect_failure('app/build.gradle.kts', 'versionCode = 813', 'versionCode = 812', 'stale versionCode')

with tempfile.TemporaryDirectory() as tmp:
    candidate = Path(tmp) / 'root'
    shutil.copytree(root, candidate)
    target = candidate / 'changelog.md'
    target.write_text(target.read_text(encoding='utf-8') + '\nPresentation wording false-positive control.\n', encoding='utf-8')
    if run(candidate) != 0:
        raise SystemExit('false-positive control failed')

print('PASS 0.80.13 Material 3 Expressive negative mutations and false-positive control')
