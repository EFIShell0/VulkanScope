#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_release_1302.py'
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
gradle_rel = Path('app/build.gradle.kts')
manifest_rel = Path('app/src/main/AndroidManifest.xml')

def make_tree(temp):
    for rel in [main_rel, gradle_rel, manifest_rel]:
        dst = temp / rel
        dst.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(root / rel, dst)

def mutate(old, new, rel=main_rel, expect_pass=False):
    with tempfile.TemporaryDirectory(prefix='vs1302-neg-') as name:
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

mutate('versionName = "1.3.2"', 'versionName = "1.3.1"', gradle_rel)
mutate('(existing + message).takeLast(120)', '(existing + message)')
mutate('if (state.phase != UpdateTransferPhase.PAUSED) pauseUpdateDownload()', 'updateDownloadPaused = false')
mutate('log = appendBoundedUpdateLog(state.log, "Cancel confirmed. Stopping download…")\n        )\n        activeUpdateDownloadCall?.cancel()', 'log = appendBoundedUpdateLog(state.log, "Cancel confirmed. Stopping download…")\n        )\n        Unit')
mutate('updateCancelConfirmationVisible = false\n        resumeUpdateDownload()', 'updateCancelConfirmationVisible = false\n        updateDownloadPaused = true')
mutate('requestPackageInstall(apk)', 'updateTransferState = null')
mutate('onDismissRequest = { if (terminal) onClose() }', 'onDismissRequest = onClose')
mutate('ExpressiveContainedIconTextButton("Cancel", R.drawable.ic_close, onClick = onRequestCancel)', 'ExpressiveTextButton("Cancel", onClick = onRequestCancel)')
mutate('UpdateTransferPhase.PAUSED -> ExpressiveTextButton("Resume", onClick = onResume)', 'UpdateTransferPhase.PAUSED -> ExpressivePrimaryIconTextButton("Resume", R.drawable.ic_download_update, onClick = onResume)')
mutate('ExpressivePrimaryIconTextButton("Install", R.drawable.ic_download_update, onClick = onInstall)', 'ExpressiveTextButton("Install", onClick = onInstall)')
mutate('MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)', 'MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Default)')
mutate('256L * 1024L * 1024L', '512L * 1024L * 1024L')
mutate('parsedUrl.host != "github.com"', 'parsedUrl.host != "example.com"')
mutate('The verified APK remains available here until you choose Install or Close.', 'The verified APK stays available here until you choose Install or Close.', expect_pass=True)
print('PASS VulkanScope 1.3.2 negative mutations and unrelated false-positive control')
