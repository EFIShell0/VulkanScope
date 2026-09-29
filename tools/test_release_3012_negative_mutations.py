#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_3012.py'
main_rel = 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
mutations = [
    ('restore extension-only counting', 'if (inspectTurnipArchive(nested) != null) validZipFileCount += 1', 'validZipFileCount += 1'),
    ('swallow cancellation', 'catch (cancelled: CancellationException) {\n            throw cancelled\n        } catch (_: Exception) {', 'catch (_: CancellationException) {\n        } catch (_: Exception) {'),
    ('remove bound', 'if (inspectedZipFileCount >= 256) {', 'if (false) {'),
    ('restore non-suspend sequence lambda marker', 'val folders = ArrayList<TurnipFolderEntry>()', 'val folders = children.asSequence().mapNotNull { child ->'),
]

for name, old, new in mutations:
    with tempfile.TemporaryDirectory(prefix='vulkanscope-3012-neg-') as temp:
        root = Path(temp) / 'tree'
        shutil.copytree(source, root)
        target = root / main_rel
        text = target.read_text(encoding='utf-8')
        if old not in text:
            raise SystemExit(f'negative mutation source token missing: {name}')
        target.write_text(text.replace(old, new, 1), encoding='utf-8')
        result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        if result.returncode == 0:
            raise SystemExit(f'negative mutation unexpectedly passed: {name}')

with tempfile.TemporaryDirectory(prefix='vulkanscope-3012-protected-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    protected = root / 'app/src/main/cpp/vulkanscope.cpp'
    protected.write_bytes(protected.read_bytes() + b'\n')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode == 0:
        raise SystemExit('protected native mutation unexpectedly passed')

with tempfile.TemporaryDirectory(prefix='vulkanscope-3012-control-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    audit = root / 'rules/3.0.12_TURNIP_FOLDER_SCAN_COMPILE_FIX_AUDIT.md'
    audit.write_text(audit.read_text(encoding='utf-8') + '\nAdditional compile evidence.\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode != 0:
        print(result.stdout)
        raise SystemExit('documentation-only false-positive control failed')

print('PASS VulkanScope 3.0.12 negative mutations and documentation false-positive control')
