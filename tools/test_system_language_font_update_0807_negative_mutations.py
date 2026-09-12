#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_system_language_font_update_0807.py'
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
manifest_rel = Path('app/src/main/AndroidManifest.xml')

def verify(tree):
    result = subprocess.run([sys.executable, str(verifier), '--root', str(tree)], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, check=False)
    return result.returncode == 0

def mutate_file(name, rel, old, new, expect_pass=False):
    with tempfile.TemporaryDirectory(prefix='vulkanscope-0807-mutation-') as tmp:
        tree = Path(tmp) / 'root'
        shutil.copytree(root, tree, ignore=shutil.ignore_patterns('__pycache__', '*.pyc'))
        path = tree / rel
        text = path.read_text(encoding='utf-8')
        if old not in text:
            raise SystemExit(f'{name}: mutation anchor missing')
        path.write_text(text.replace(old, new, 1), encoding='utf-8')
        passed = verify(tree)
        if passed != expect_pass:
            got = 'PASS' if passed else 'FAIL'
            wanted = 'PASS' if expect_pass else 'FAIL'
            raise SystemExit(f'{name}: verifier returned {got}; expected {wanted}')

mutate_file('RTL support removal', manifest_rel, 'android:supportsRtl="true"', 'android:supportsRtl="false"')
mutate_file('content-aware direction removal', main_rel, 'bodyMedium = VulkanBaseTypography.bodyMedium.copy(textDirection = TextDirection.ContentOrLtr)', 'bodyMedium = VulkanBaseTypography.bodyMedium.copy(textDirection = TextDirection.Rtl)')
mutate_file('hard-coded font family regression', main_rel, 'fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace', 'fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif')
mutate_file('semantic update icon removal', main_rel, 'is UpdateStatus.Available -> { UpdateAvailableIcon();', 'is UpdateStatus.Available -> {')
mutate_file('update icon color regression', main_rel, 'tint = VulkanAccentSoft,\n                modifier = Modifier.size(20.dp)', 'tint = ComposeColor(0xFF5CA9FF),\n                modifier = Modifier.size(20.dp)')
mutate_file('generic Info icon restoration', main_rel, 'painter = painterResource(R.drawable.ic_update_available),', 'painter = painterResource(R.drawable.ic_info),')
mutate_file('unrelated update wording false-positive control', main_rel, 'VulkanScope ${status.update.version} available', 'VulkanScope ${status.update.version} is available', True)
print('PASS 0.80.7 system-language/font/update-info negative mutations and false-positive control')
