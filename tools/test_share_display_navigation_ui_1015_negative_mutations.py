#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')

def replace_once(path, old, new):
    text = path.read_text(encoding='utf-8')
    if old not in text: raise AssertionError('mutation anchor missing: ' + old[:160])
    path.write_text(text.replace(old, new, 1), encoding='utf-8')

def run_case(name, mutate, fail=True):
    with tempfile.TemporaryDirectory(prefix='vs1015-ui-mut-') as tmp:
        d = Path(tmp) / 'project'
        shutil.copytree(root, d)
        mutate(d)
        r = subprocess.run([sys.executable, str(d/'tools/verify_share_display_navigation_ui_1015.py'), '--root', str(d)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        if (r.returncode != 0) != fail:
            print(r.stdout)
            raise AssertionError(f'{name}: expected fail={fail}, got {r.returncode != 0}')

muts = [
    ('Share link reuses chain icon', lambda d: replace_once(d/main_rel, 'R.drawable.ic_share) { model.shareLink() }', 'R.drawable.ic_link) { model.shareLink() }')),
    ('Database QR reuses link icon', lambda d: replace_once(d/main_rel, 'title.equals("Database permalink & QR", true) -> R.drawable.ic_qr', 'title.equals("Database permalink & QR", true) -> R.drawable.ic_link')),
    ('HDR badge removed', lambda d: replace_once(d/main_rel, 'title.equals("HDR capabilities", true) || title.equals("HDR / wide-color surface detection", true) -> {\n            DisplaySectionBadgeIcon("HDR")', 'title.equals("HDR capabilities", true) || title.equals("HDR / wide-color surface detection", true) -> {\n            DisplaySectionBadgeIcon("MODE")')),
    ('MODE badge removed', lambda d: replace_once(d/main_rel, 'title.equals("Supported display modes", true) -> {\n            DisplaySectionBadgeIcon("MODE")', 'title.equals("Supported display modes", true) -> {\n            DisplaySectionBadgeIcon("HDR")')),
    ('Surface overlay removed', lambda d: replace_once(d/main_rel, 'title.equals("Display ↔ Vulkan interpretation", true) -> {\n            Box(Modifier.padding(7.dp).size(27.dp)) {', 'title.equals("Display ↔ Vulkan interpretation disabled", true) -> {\n            Box(Modifier.padding(7.dp).size(27.dp)) {')),
    ('bottom navigation animation removed', lambda d: replace_once(d/main_rel, 'icon = { AnimatedNavigationIcon(item.page, item.icon, animationTrigger, 24.dp) }', 'icon = { Icon(painterResource(item.icon), contentDescription = null) }')),
    ('rail click trigger removed', lambda d: replace_once(d/main_rel, 'animationTrigger += 1\n                        onPageSelected(item.page)', 'onPageSelected(item.page)')),
    ('Vulkan motion made generic', lambda d: replace_once(d/main_rel, 'rotationZ = -11f * wave', 'rotationZ = 0f')),
    ('Display motion removed', lambda d: replace_once(d/main_rel, 'scaleY = 1f - 0.10f * wave', 'scaleY = 1f')),
    ('unbounded animation introduced', lambda d: replace_once(d/main_rel, 'val motion = remember(page) { androidx.compose.animation.core.Animatable(0f) }', 'val motion = remember(page) { androidx.compose.animation.core.Animatable(0f) }\n    val loop = rememberInfiniteTransition()')),
    ('QR finder pattern removed', lambda d: replace_once(d/'app/src/main/res/drawable/ic_qr.xml', 'M14,4H20V10H14Z', 'M14,4H14V4H14Z')),
    ('stale version', lambda d: replace_once(d/'app/build.gradle.kts', 'versionCode = 1018', 'versionCode = 1014')),
]
for name, mutation in muts:
    run_case(name, mutation, True)
run_case('unrelated changelog wording', lambda d: (d/'changelog.md').write_text((d/'changelog.md').read_text(encoding='utf-8') + '\nUnrelated wording.\n', encoding='utf-8'), False)
print(f'PASS VulkanScope 1.0.15 UI negative mutations: {len(muts)} defects rejected + false-positive control')
