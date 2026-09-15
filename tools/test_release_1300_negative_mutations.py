#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_release_1300.py'
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
gradle_rel = Path('app/build.gradle.kts')
manifest_rel = Path('app/src/main/AndroidManifest.xml')
asset_rels = [
    Path('app/src/main/assets/licenses/apache_2_0.md'),
    Path('app/src/main/assets/licenses/libadrenotools_bsd_2_clause.md'),
    Path('app/src/main/assets/licenses/vulkan_headers.md'),
]

def make_tree(temp):
    for rel in [main_rel, gradle_rel, manifest_rel, *asset_rels]:
        dst = temp / rel
        dst.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(root / rel, dst)

def mutate_text(old, new, rel=main_rel, expect_pass=False):
    with tempfile.TemporaryDirectory(prefix='vs1300-neg-') as name:
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

mutate_text('versionName = "1.3.0"', 'versionName = "1.2.5"', gradle_rel)
mutate_text(r'\bVulkan(?!Scope|®)', r'\bVulkan')
mutate_text('VULKAN_TRADEMARK_DISPLAY_REGEX.replace(text, "Vulkan®")', 'text')
mutate_text('PropertyEntry("Vulkan Query Status", name, value)', 'PropertyEntry("Vulkan® Query Status", name, value)')
mutate_text('it.section == "Vulkan Query Safety"', 'it.section == "Vulkan® Query Safety"')
mutate_text('ChevronAffordance("License", "Open ${library.name} license") { selectedLibraryLicense = library }', 'Text(library.licenseName)')
mutate_text('ReleaseNotesContent(text, Modifier.fillMaxSize())', 'Text(text)')
mutate_text('ExpressiveContainedIconTextButton("Close", R.drawable.ic_close, onClick = onDismiss)', 'ExpressiveCloseButton(onClick = onDismiss)')
mutate_text('Apache License 2.0', 'Unknown license')
mutate_text('Version 2.0, January 2004', 'Version 2.0', asset_rels[0])
mutate_text('BSD 2-Clause License', 'BSD License', asset_rels[1])
mutate_text('MIT License', 'Unknown license', asset_rels[2])
mutate_text('VulkanScope is a Vulkan® capability and device inspection utility', 'VulkanScope is a Vulkan® device inspection utility', expect_pass=True)
print('PASS VulkanScope 1.3.0 negative mutations and unrelated false-positive control')
