#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_encyclopedia_0801.py'
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')

def run_mutation(label, mutate, expect_pass=False):
    with tempfile.TemporaryDirectory(prefix='vulkanscope-encyclopedia-') as td:
        dst = Path(td) / 'root'
        shutil.copytree(root, dst, ignore=shutil.ignore_patterns('__pycache__', '*.pyc'))
        path = dst / main_rel
        text = path.read_text(encoding='utf-8')
        changed = mutate(text)
        if changed == text:
            raise SystemExit(f'{label}: mutation did not change source')
        path.write_text(changed, encoding='utf-8')
        result = subprocess.run([sys.executable, str(verifier), '--root', str(dst)], text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        passed = result.returncode == 0
        if passed != expect_pass:
            raise SystemExit(f'{label}: unexpected verifier result\n{result.stdout}')

run_mutation('remove encyclopedia placement', lambda s: s.replace('        item { EncyclopediaOverviewCard(navigate) }\n', '', 1))
run_mutation('move encyclopedia below analysis', lambda s: s.replace('        item { EncyclopediaOverviewCard(navigate) }\n        analysisWorkspaceItems(analysisModel, report, device)', '        analysisWorkspaceItems(analysisModel, report, device)\n        item { EncyclopediaOverviewCard(navigate) }', 1))
run_mutation('remove Not applicable definition', lambda s: s.replace('        CapabilityKeyValue("Not applicable", "A required prerequisite, API scope or extension is not exposed, so that query does not apply to this runtime path.")\n', '', 1))
run_mutation('unrelated hero text', lambda s: s.replace('Vulkan device unavailable', 'Vulkan device not available', 1), expect_pass=True)
print('PASS 0.80.1 Encyclopedia negative mutations')
