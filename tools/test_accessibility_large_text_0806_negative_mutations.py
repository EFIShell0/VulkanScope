#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_accessibility_large_text_0806.py'
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')


def verify(tree):
    result = subprocess.run([sys.executable, str(verifier), '--root', str(tree)], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, check=False)
    return result.returncode == 0


def mutate(name, old, new, expect_pass=False):
    with tempfile.TemporaryDirectory(prefix='vulkanscope-0806-mutation-') as tmp:
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


mutate('scroll arrow TalkBack noise', 'contentDescription = null,\n            tint = VulkanAccentSoft,\n            modifier = Modifier.padding(8.dp).size(24.dp)', 'contentDescription = "More content above",\n            tint = VulkanAccentSoft,\n            modifier = Modifier.padding(8.dp).size(24.dp)')
mutate('large-font threshold removal', 'return configuration.fontScale >= 1.3f || configuration.screenWidthDp < 360', 'return configuration.screenWidthDp < 360')
mutate('Quick Access fixed height regression', 'modifier = modifier.heightIn(min = 72.dp)', 'modifier = modifier.height(72.dp)')
mutate('Quick Access large-text adaptation regression', 'expandedTextLayout || maxWidth < 300.dp -> 1', 'maxWidth < 300.dp -> 1')
mutate('collection live-region removal', 'val failed = status == CollectionStatus.FAILED\n        Surface(\n            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp).semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },', 'val failed = status == CollectionStatus.FAILED\n        Surface(\n            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp).semantics(mergeDescendants = true) { },')
mutate('key-value TalkBack merge removal', 'modifier = Modifier.fillMaxWidth().then(tvBrowseModifier(shape)).semantics(mergeDescendants = true) { }', 'modifier = Modifier.fillMaxWidth().then(tvBrowseModifier(shape))')
mutate('section heading removal', '.weight(1f).semantics { heading() }', '.weight(1f)')
mutate('switch duplicate action restoration', 'ExpressiveSwitch(checked = directUpdatesEnabled, onCheckedChange = null)', 'ExpressiveSwitch(checked = directUpdatesEnabled, onCheckedChange = onDirectUpdatesChanged)')
mutate('driver radio duplicate action restoration', 'ExpressiveRadioButton(selected = selected, enabled = enabled, onClick = null)', 'ExpressiveRadioButton(selected = selected, enabled = enabled, onClick = onClick)')
mutate('Info export large-text stacking removal', 'if (expandedTextLayout) {\n                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {', 'if (false) {\n                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {')
mutate('update-dialog adaptive viewport removal', 'val releaseNotesMaxHeight = if (expandedTextLayout) 220.dp else 360.dp', 'val releaseNotesMaxHeight = 360.dp')
mutate('unrelated Hero wording false-positive control', 'Vulkan device unavailable', 'Vulkan device not available', True)
print('PASS 0.80.6 accessibility/large-text negative mutations and false-positive control')
