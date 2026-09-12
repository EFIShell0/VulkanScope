#!/usr/bin/env python3
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_release_1202.py'
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
gradle_rel = Path('app/build.gradle.kts')
resource_rels = [Path('app/src/main/res/drawable/ic_tablet.xml'), Path('app/src/main/res/drawable/ic_clear_filters.xml'), Path('app/src/main/res/drawable/ic_clear_all.xml'), Path('app/src/main/res/drawable-nodpi/mesa3d_logo.webp')]

def make_tree(temp):
    for rel in [main_rel, gradle_rel, *resource_rels]:
        dst = temp / rel
        dst.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(root / rel, dst)

def mutate_text(old, new, rel=main_rel, expect_pass=False):
    with tempfile.TemporaryDirectory(prefix='vs1202-neg-') as name:
        temp = Path(name); make_tree(temp)
        target = temp / rel
        text = target.read_text(encoding='utf-8')
        if old not in text: raise SystemExit(f'mutation source missing: {old}')
        target.write_text(text.replace(old, new, 1), encoding='utf-8')
        result = subprocess.run([sys.executable, str(verifier), '--root', str(temp)], text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        if (result.returncode == 0) != expect_pass:
            print(result.stdout); raise SystemExit(f'unexpected verifier result for mutation: {old}')

def remove_resource(rel):
    with tempfile.TemporaryDirectory(prefix='vs1202-neg-') as name:
        temp = Path(name); make_tree(temp); (temp / rel).unlink()
        result = subprocess.run([sys.executable, str(verifier), '--root', str(temp)], text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        if result.returncode == 0:
            print(result.stdout); raise SystemExit(f'missing resource was accepted: {rel}')

current_gradle = (root / gradle_rel).read_text(encoding='utf-8')
current_name = re.search(r'versionName\s*=\s*"([^"]+)"', current_gradle).group(0)
mutate_text(current_name, 'versionName = "1.2.1"', gradle_rel)
mutate_text('ExpressiveIconButton(R.drawable.ic_settings, "Settings", onSettings)', 'ExpressiveIconButton(R.drawable.ic_info, "Info", onSettings)')
mutate_text('ExpressiveDestinationCard(section.label, section.description, section.icon)', 'OverviewDestinationCard(section.label, section.description, Page.Info, { })')
mutate_text('ExpressiveSwitch(checked = allEnabled, onCheckedChange = { enabled -> onSelected(if (enabled) 0 else 1) })', 'ExpressiveSwitch(checked = allEnabled, onCheckedChange = null)')
mutate_text('visible = value.isNotEmpty()', 'visible = false')
mutate_text('enabled = model.canAddWatch', 'enabled = true')
mutate_text('QuestionDialogTitle("Remove watched evidence?")', 'QuestionDialogTitle("Removed")')
mutate_text('enabled = state.watched.isNotEmpty()', 'enabled = true')
version_text = (root / gradle_rel).read_text(encoding='utf-8')
version_match = re.search(r'versionName\s*=\s*"(\d+)\.(\d+)\.(\d+)"', version_text)
version_tuple = tuple(map(int, version_match.groups())) if version_match else (0, 0, 0)
mutate_text('trailingIcon = R.drawable.ic_open_external' if version_tuple >= (1, 2, 4) else 'trailingIcon = R.drawable.ic_link', 'trailingIcon = R.drawable.ic_chevron_right')
mutate_text('R.drawable.ic_download, enabled = directUpdatesEnabled', 'R.drawable.ic_receive, enabled = directUpdatesEnabled')
mutate_text('HdrCapabilitiesCarousel(display.hdrTypes)', 'Row { display.hdrTypes.forEach { HdrTypeCard(it) } }')
mutate_text('NavigationItem(Page.Display, "Display", R.drawable.ic_tablet)', 'NavigationItem(Page.Display, "Display", R.drawable.ic_display)')
mutate_text('SectionVectorBadgeIcon(R.drawable.ic_registry, "REG")', 'SectionVectorBadgeIcon(R.drawable.ic_registry, "VK")')
mutate_text('CompositeMesaActionIcon(R.drawable.ic_compare, tint)', 'CompositeActionVectorIcon(R.drawable.ic_cpu, R.drawable.ic_compare, tint)')
remove_resource(Path('app/src/main/res/drawable/ic_clear_filters.xml'))
remove_resource(Path('app/src/main/res/drawable-nodpi/mesa3d_logo.webp'))
mutate_text('Vulkan capability and device inspection utility', 'Vulkan capability/device inspection utility', expect_pass=True)
print('PASS VulkanScope 1.2.2 negative mutations and unrelated false-positive control')
