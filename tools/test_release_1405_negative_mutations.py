import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_release_1405.py'

def run(candidate):
    return subprocess.run([sys.executable, str(verifier), '--root', str(candidate), '--skip-version'], text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)

def mutate(relative, old, new):
    with tempfile.TemporaryDirectory(prefix='vulkanscope-1405-mutation-') as td:
        candidate = Path(td) / 'tree'
        shutil.copytree(root, candidate)
        path = candidate / relative
        data = path.read_text(encoding='utf-8')
        if old not in data:
            raise SystemExit(f'mutation source token missing: {relative}: {old}')
        path.write_text(data.replace(old, new, 1), encoding='utf-8')
        result = run(candidate)
        if result.returncode == 0:
            raise SystemExit(f'negative mutation escaped verifier: {relative}: {old}')

mutations = [
    ('app/src/main/AndroidManifest.xml', '<uses-feature android:name="android.hardware.type.pc" android:required="false" />', '<uses-feature android:name="android.hardware.type.pc" android:required="true" />'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'PackageManager.FEATURE_PC', 'PackageManager.FEATURE_WATCH'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'val isExpandedWindow = configuration.screenWidthDp >= 600', 'val isExpandedWindow = false'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'Android PC form factor with freeform window management detected; Googlebook-compatible environment evidence only', 'Android PC form factor with freeform window management detected; Googlebook detected'),
    ('registry/registry_lock.json', '"registryRef": "1.4.364"', '"registryRef": "1.4.363"'),
    ('app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt', '    "VK_INTEL_device_info",\n', ''),
    ('app/src/main/cpp/runtime_extension_pnext_generated.inc', 'std::strcmp(selectedExtension, "VK_INTEL_device_info") == 0', 'std::strcmp(selectedExtension, "VK_INTEL_device_info_DISABLED") == 0'),
    ('app/src/main/cpp/extension_field_coverage_generated.inc', 'generatedEmitNumeric(dst, section, "deviceIpVersionRevision", value.deviceIpVersionRevision);', 'generatedEmitNumeric(dst, section, "deviceIpVersionRevisionMissing", value.deviceIpVersionRevision);'),
    ('app/build.gradle.kts', 'isMinifyEnabled = true', 'isMinifyEnabled = false'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'put("googlebookOsVersion", googlebookOsVersionEvidence())', 'put("googlebookOsVersionRemoved", googlebookOsVersionEvidence())')
]
for mutation in mutations:
    mutate(*mutation)

with tempfile.TemporaryDirectory(prefix='vulkanscope-1405-control-') as td:
    candidate = Path(td) / 'tree'
    shutil.copytree(root, candidate)
    path = candidate / 'BUILD_AUDIT.md'
    path.write_text(path.read_text(encoding='utf-8') + '\nUnrelated verifier false-positive control.\n', encoding='utf-8')
    result = run(candidate)
    if result.returncode != 0:
        print(result.stdout)
        raise SystemExit('false-positive control failed')
print(f'release_1405 negative mutations: PASS mutations={len(mutations)} falsePositiveControls=1')
