#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_release_1301.py'
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
gradle_rel = Path('app/build.gradle.kts')
manifest_rel = Path('app/src/main/AndroidManifest.xml')
icon_rel = Path('app/src/main/res/drawable/ic_expand_more.xml')

def make_tree(temp):
    for rel in [main_rel, gradle_rel, manifest_rel, icon_rel]:
        dst = temp / rel
        dst.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(root / rel, dst)

def mutate(old, new, rel=main_rel, expect_pass=False):
    with tempfile.TemporaryDirectory(prefix='vs1301-neg-') as name:
        temp = Path(name)
        make_tree(temp)
        target = temp / rel
        text = target.read_text(encoding='utf-8')
        if old not in text:
            raise SystemExit(f'mutation source missing: {old}')
        target.write_text(text.replace(old, new, 1), encoding='utf-8')
        result = subprocess.run([sys.executable, str(verifier), '--root', str(temp)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        if (result.returncode == 0) != expect_pass:
            print(result.stdout)
            raise SystemExit(f'unexpected verifier result for mutation: {old}')

mutate('versionName = "1.3.1"', 'versionName = "1.3.0"', gradle_rel)
mutate('val pageSize = 50', 'val pageSize = 100')
mutate('label.contains(query, ignoreCase = true)', 'label.contains(query, ignoreCase = false)')
mutate('indexed.drop(page * pageSize).take(pageSize)', 'indexed.take(pageSize)')
mutate('if (requested in 1..pageCount) page = requested - 1', 'page = (requested - 1).coerceAtLeast(0)')
mutate('contentDescription = "Previous filter page"', 'contentDescription = "Filter page"')
mutate('enabled = !allEnabled', 'enabled = true')
mutate('onSelected = { onSelected(it + 1) }', 'onSelected = { onSelected(it) }')
mutate('ExpressiveSingleFilterSelector(labels, selectedIndex, true, arrowTint, onSelected)', 'ExpressiveFilterCarousel(labels, selectedIndex, arrowTint = arrowTint, isSelected = { it == selectedIndex }, onSelected = onSelected)')
mutate('HLG and HLG+ are shown as text because no official logo is defined by the authoritative standards sources used by VulkanScope.', 'HLG and HLG+ are shown as text.')
mutate('6 -> "HLG+"', '5 -> "HLG+"')
mutate('"hdr vivid" -> R.drawable.hdr_vivid\n        else -> null', '"hdr vivid" -> R.drawable.hdr_vivid\n        "hlg" -> R.drawable.hdr_hdr10\n        else -> null')
mutate('Text("Filter", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)', 'Text("Choose", color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)', expect_pass=True)
print('PASS VulkanScope 1.3.1 negative mutations and unrelated false-positive control')
