#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_encyclopedia_0802.py'
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
index_rel = Path('app/src/main/java/com/efishell/vulkanscope/VulkanSymbolIndex.kt')

def run_mutation(label, rel, mutate, expect_pass=False):
    with tempfile.TemporaryDirectory(prefix='vulkanscope-encyclopedia-0802-') as td:
        dst = Path(td) / 'root'
        shutil.copytree(root, dst, ignore=shutil.ignore_patterns('__pycache__', '*.pyc'))
        path = dst / rel
        text = path.read_text(encoding='utf-8')
        changed = mutate(text)
        if changed == text:
            raise SystemExit(f'{label}: mutation did not change source')
        path.write_text(changed, encoding='utf-8')
        result = subprocess.run([sys.executable, str(verifier), '--root', str(dst)], text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        passed = result.returncode == 0
        if passed != expect_pass:
            raise SystemExit(f'{label}: unexpected verifier result\n{result.stdout}')

run_mutation('remove VK_SUCCESS meaning', main_rel, lambda s: s.replace('Command successfully completed', 'Completed', 1))
run_mutation('remove local search', main_rel, lambda s: s.replace('ExpressiveSearchField(', 'RemovedSearchField(', 1))
run_mutation('restore Vulkan Video destination shortcut', main_rel, lambda s: s.replace('    CapabilitySectionCard("Encyclopedia") {', '    CapabilitySectionCard("Encyclopedia") {\n        ExpressiveAssistChip("Vulkan Video", R.drawable.ic_video) { navigate(Page.Video) }', 1))
run_mutation('remove result bound', main_rel, lambda s: s.replace('private const val ENCYCLOPEDIA_VISIBLE_RESULT_LIMIT = 24', 'private const val ENCYCLOPEDIA_VISIBLE_RESULT_LIMIT = 240', 1))
run_mutation('corrupt generated symbol index', index_rel, lambda s: s.replace('vkCreateInstance', 'vkCreateInstanceBROKEN', 1))
run_mutation('unrelated hero text', main_rel, lambda s: s.replace('Vulkan device unavailable', 'Vulkan device not available', 1), expect_pass=True)
print('PASS 0.80.2 Encyclopedia negative mutations')
