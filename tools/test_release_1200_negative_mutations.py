import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_release_1200.py'
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
native_rel = Path('app/src/main/cpp/vulkanscope.cpp')
gradle_rel = Path('app/build.gradle.kts')

def run_case(old, new, rel, expect_pass):
    with tempfile.TemporaryDirectory(prefix='vs1200-neg-') as name:
        temp = Path(name)
        for item in [main_rel, native_rel, gradle_rel]:
            target = temp / item
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(root / item, target)
        target = temp / rel
        text = target.read_text(encoding='utf-8')
        if old not in text:
            raise SystemExit(f'mutation source missing: {old}')
        target.write_text(text.replace(old, new, 1), encoding='utf-8')
        result = subprocess.run([sys.executable, str(verifier), '--root', str(temp)], text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
        if (result.returncode == 0) != expect_pass:
            print(result.stdout)
            raise SystemExit(f'unexpected verifier result for mutation {old}')

run_case('versionName = "1.2.0"', 'versionName = "1.2.1"', gradle_rel, False)
run_case('delay(3000)', 'delay(1200)', main_rel, False)
run_case('enabled = !allEnabled', 'enabled = true', main_rel, False)
run_case('Text("KHR"', 'Text("EXT"', main_rel, False)
run_case('Vulkan capability and device inspection utility', 'Vulkan device and capability inspection utility', main_rel, True)
print('PASS VulkanScope 1.2.0 negative mutations and unrelated false-positive control')
