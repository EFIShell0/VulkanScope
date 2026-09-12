#!/usr/bin/env python3
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_release_1203.py'
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
gradle_rel = Path('app/build.gradle.kts')
manifest_rel = Path('app/src/main/AndroidManifest.xml')

def make_tree(temp):
    for rel in [main_rel, gradle_rel, manifest_rel]:
        dst = temp / rel
        dst.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(root / rel, dst)

def mutate_text(old, new, rel=main_rel, expect_pass=False):
    with tempfile.TemporaryDirectory(prefix='vs1203-neg-') as name:
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

current_gradle = (root / gradle_rel).read_text(encoding='utf-8')
current_name = re.search(r'versionName\s*=\s*"([^"]+)"', current_gradle).group(0)
mutate_text(current_name, 'versionName = "1.2.2"', gradle_rel)
mutate_text('minSdk = 31', 'minSdk = 24', gradle_rel)
mutate_text('<uses-permission android:name="android.permission.INTERNET" />', '<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="28" />\n    <uses-permission android:name="android.permission.INTERNET" />', manifest_rel)
mutate_text('currentEntries.entries.firstOrNull { entry ->', 'currentEntries.firstOrNull { entry ->')
mutate_text('DatabaseSubmissionResult(true, id, "Report submitted successfully."', 'DatabaseSubmissionResult(true, null, "Report submitted successfully."')
mutate_text('if (!id.matches(Regex("[a-f0-9]{64}"))) {\n                    databaseSubmissionFailure', 'if (false) {\n                    databaseSubmissionFailure')
mutate_text('submissionSuccessId = result.reportId', 'submissionSuccessId = result.reportId?.take(12)')
mutate_text('Text(reportId, color = VulkanTextPrimary, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)', 'Text(reportId, color = VulkanTextPrimary, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Default)')
mutate_text('style = MaterialTheme.typography.bodySmall,\n                        fontFamily = FontFamily.Monospace', 'style = MaterialTheme.typography.bodySmall,\n                        fontFamily = FontFamily.Default')
mutate_text('ExpressiveContainedIconTextButton("Copy all", R.drawable.ic_copy)', 'ExpressiveContainedIconTextButton("Copy all", R.drawable.ic_chevron_right)')
mutate_text('ExpressiveContainedIconTextButton("Close", R.drawable.ic_close, onClick = onDismiss)', 'ExpressiveCloseButton(onClick = onDismiss)')
mutate_text('submissionFailureLog = result.log', 'submissionFailureLog = null')
mutate_text('val limit = 96 * 1024', 'val limit = Int.MAX_VALUE')
mutate_text('catch (error: CancellationException) {\n                state = 0\n                throw error', 'catch (_: CancellationException) {\n                state = 3\n                false')
mutate_text('Vulkan capability and device inspection utility', 'Vulkan capability/device inspection utility', expect_pass=True)
print('PASS VulkanScope 1.2.3 negative mutations and unrelated false-positive control')
