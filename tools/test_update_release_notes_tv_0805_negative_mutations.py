#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_update_release_notes_tv_0805.py'
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')


def verify(tree):
    return subprocess.run([sys.executable, str(verifier), '--root', str(tree), '--skip-version'], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, check=False).returncode == 0


def mutate(name, old, new, expect_pass=False):
    with tempfile.TemporaryDirectory(prefix='vulkanscope-0805-mutation-') as tmp:
        tree = Path(tmp) / 'root'
        shutil.copytree(root, tree, ignore=shutil.ignore_patterns('__pycache__', '*.pyc'))
        path = tree / main_rel
        text = path.read_text(encoding='utf-8')
        if old not in text:
            raise SystemExit(f'{name}: mutation anchor missing')
        path.write_text(text.replace(old, new, 1), encoding='utf-8')
        passed = verify(tree)
        if passed != expect_pass:
            got = 'PASS' if passed else 'FAIL'
            wanted = 'PASS' if expect_pass else 'FAIL'
            raise SystemExit(f'{name}: verifier returned {got}; expected {wanted}')


mutate('release-note boundary indicators removal', 'ScrollBoundaryIndicators(listState, Modifier.align(Alignment.CenterEnd).padding(end = 2.dp))', 'Spacer(Modifier.size(1.dp))')
mutate('release-note TV focus removal', 'color = ComposeColor.Transparent,\n        shape = shape,\n        modifier = Modifier.fillMaxWidth().then(tvBrowseModifier(shape))', 'color = ComposeColor.Transparent,\n        shape = shape,\n        modifier = Modifier.fillMaxWidth()')
mutate('release-note focus group removal', 'modifier = Modifier.fillMaxWidth().focusGroup().padding(end = 28.dp),', 'modifier = Modifier.fillMaxWidth().padding(end = 28.dp),')
mutate('release-note user scroll removal', 'verticalArrangement = Arrangement.spacedBy(5.dp),\n            userScrollEnabled = true', 'verticalArrangement = Arrangement.spacedBy(5.dp),\n            userScrollEnabled = false')
mutate('release-note viewport bound removal', 'ReleaseNotesContent(update.releaseNotes, Modifier.fillMaxWidth().heightIn(max = releaseNotesMaxHeight))', 'ReleaseNotesContent(update.releaseNotes, Modifier.fillMaxWidth())')
mutate('update dialog design geometry drift', 'Text("Release notes", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)\n                Surface(shape = MaterialTheme.shapes.medium, color = ComposeColor(0xFF0D0D0D))', 'Text("Release notes", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)\n                Surface(shape = RoundedCornerShape(8.dp), color = ComposeColor(0xFF0D0D0D))')
mutate('unrelated update description false-positive control', 'Review the target build and release notes before any APK download starts.', 'Review release information before downloading the APK.', True)
print('PASS 0.80.5 update release-notes negative mutations and false-positive control')
