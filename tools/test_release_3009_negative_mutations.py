#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_3009.py'
main_rel = 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
mutations = [
    (
        'restore raw Database submitted timestamp',
        'DatabaseSubmittedAt(reportRow.submittedAt)',
        'CapabilityKeyValue("Submitted", reportRow.submittedAt.ifBlank { "Unknown" })'
    ),
    (
        'remove Turnip/System Overview color distinction',
        'color = if (isTurnip) VulkanAccentSoft else VulkanTextPrimary,',
        'color = VulkanTextPrimary,'
    ),
    (
        'duplicate Quality visible icon treatment',
        'title.equals("Scoring method", true) -> SectionVectorBadgeIcon(R.drawable.ic_shield, overlayIcon = R.drawable.ic_evidence)',
        'title.equals("Scoring method", true) -> SectionVectorBadgeIcon(R.drawable.ic_shield, overlayIcon = R.drawable.ic_check)'
    ),
    (
        'restore generic info treatment in Tests',
        'title.equals("Result semantics", true) -> SectionVectorBadgeIcon(R.drawable.ic_test, overlayIcon = R.drawable.ic_question)',
        'title.equals("Result semantics", true) -> SectionVectorBadgeIcon(R.drawable.ic_info, overlayIcon = R.drawable.ic_question)'
    ),
]

for name, old, new in mutations:
    with tempfile.TemporaryDirectory(prefix='vulkanscope-3009-neg-') as temp:
        root = Path(temp) / 'tree'
        shutil.copytree(source, root)
        target = root / main_rel
        text = target.read_text(encoding='utf-8')
        if old not in text:
            raise SystemExit(f'negative mutation source token missing: {name}')
        target.write_text(text.replace(old, new, 1), encoding='utf-8')
        files = sorted(path.relative_to(root).as_posix() for path in root.rglob('*') if path.is_file())
        (root / 'files.txt').write_text('\n'.join(files) + '\n', encoding='utf-8')
        result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        if result.returncode == 0:
            raise SystemExit(f'negative mutation unexpectedly passed: {name}')

with tempfile.TemporaryDirectory(prefix='vulkanscope-3009-rule-neg-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    target = root / 'rules/PROJECT_RULES.md'
    text = target.read_text(encoding='utf-8')
    line = '- Visible icon treatments must preserve semantic identity: within the same destination, tab, dialog or simultaneously visible action group, the exact same glyph/treatment must not represent different meanings. When one base glyph is still the clearest semantic root, derive a visibly distinct meaningful variant with an internal or lower-right icon/text badge; arbitrary decorative variants that do not communicate the role are forbidden.\n'
    if line not in text:
        raise SystemExit('semantic icon rule token missing')
    target.write_text(text.replace(line, '', 1), encoding='utf-8')
    files = sorted(path.relative_to(root).as_posix() for path in root.rglob('*') if path.is_file())
    (root / 'files.txt').write_text('\n'.join(files) + '\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode == 0:
        raise SystemExit('semantic icon permanent-rule deletion unexpectedly passed')

with tempfile.TemporaryDirectory(prefix='vulkanscope-3009-protected-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    protected = root / 'app/src/main/cpp/vulkanscope.cpp'
    protected.write_bytes(protected.read_bytes() + b'\n')
    files = sorted(path.relative_to(root).as_posix() for path in root.rglob('*') if path.is_file())
    (root / 'files.txt').write_text('\n'.join(files) + '\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode == 0:
        raise SystemExit('protected native mutation unexpectedly passed regression boundary')

with tempfile.TemporaryDirectory(prefix='vulkanscope-3009-control-') as temp:
    root = Path(temp) / 'tree'
    shutil.copytree(source, root)
    audit = root / 'rules/3.0.9_DATABASE_TIME_DRIVER_ICON_AUDIT.md'
    audit.write_text(audit.read_text(encoding='utf-8') + '\nAdditional bounded documentation evidence.\n', encoding='utf-8')
    files = sorted(path.relative_to(root).as_posix() for path in root.rglob('*') if path.is_file())
    (root / 'files.txt').write_text('\n'.join(files) + '\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(root)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    if result.returncode != 0:
        print(result.stdout)
        raise SystemExit('documentation-only false-positive control failed')

print('PASS VulkanScope 3.0.9 negative mutations and documentation false-positive control')
