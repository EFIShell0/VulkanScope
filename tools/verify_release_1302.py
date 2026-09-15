#!/usr/bin/env python3
import argparse
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root')
parser.add_argument('--skip-version', action='store_true')
args = parser.parse_args()
root = Path(args.root).resolve() if args.root else Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
gradle = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
manifest = (root / 'app/src/main/AndroidManifest.xml').read_text(encoding='utf-8')
errors = []

def need(condition, message):
    if not condition:
        errors.append(message)

def block(start, end):
    a = main.find(start)
    b = main.find(end, a + len(start)) if a >= 0 else -1
    need(a >= 0 and b > a, f'block missing: {start}')
    return main[a:b] if a >= 0 and b > a else ''

if not args.skip_version:
    need('versionCode = 1302' in gradle and 'versionName = "1.3.2"' in gradle, '1.3.2 release identity missing')
for phase in ['CONNECTING', 'DOWNLOADING', 'PAUSED', 'VERIFYING', 'COMPLETED', 'CANCELED', 'FAILED']:
    need(phase in block('private enum class UpdateTransferPhase', 'private data class UpdateTransferState'), f'update transfer phase missing: {phase}')
need('private var updateTransferState by mutableStateOf<UpdateTransferState?>(null)' in main, 'persistent update transfer UI state missing')
need('@Volatile private var updateDownloadPaused = false' in main, 'pause lifecycle flag missing')
need('@Volatile private var updateDownloadCancelRequested = false' in main, 'cancel lifecycle flag missing')
need('(existing + message).takeLast(120)' in main, 'live update log is not bounded to 120 entries')
need('elapsed >= 400_000_000L' in main, 'progress publication is not rate limited')
need('now - lastLogAt >= 2_000_000_000L' in main, 'live log progress publication is not bounded')

start = block('    private fun startUpdateDownload(update: AppUpdate) {', '    private fun pauseUpdateDownload()')
need('updateStatus = UpdateStatus.Hidden' in start, 'legacy transient downloading banner is not suppressed when persistent transfer flow starts')
need('phase = UpdateTransferPhase.CONNECTING' in start, 'download flow does not start in CONNECTING')
need('UpdateTransferPhase.COMPLETED' in start and 'Update downloaded and ready to install.' in start, 'verified download terminal state missing')
need('requestPackageInstall(apk)' not in start, 'successful download still auto-opens Android package installer')
need('UpdateTransferPhase.CANCELED' in start and 'Download canceled by user.' in start, 'confirmed cancellation terminal state/log missing')
need('UpdateTransferPhase.FAILED' in start and 'connectionStatus = "Error"' in start, 'failed transfer terminal state missing')

pause = block('    private fun pauseUpdateDownload() {', '    private fun resumeUpdateDownload()')
need('updateDownloadPaused = true' in pause and 'phase = UpdateTransferPhase.PAUSED' in pause and 'Download paused.' in pause, 'pause transition contract missing')
resume = block('    private fun resumeUpdateDownload() {', '    private fun requestCancelUpdateDownload()')
need('updateDownloadPaused = false' in resume and 'phase = UpdateTransferPhase.DOWNLOADING' in resume and 'Download resumed.' in resume, 'resume transition contract missing')
request_cancel = block('    private fun requestCancelUpdateDownload() {', '    private fun dismissCancelUpdateDownload()')
need('if (state.phase != UpdateTransferPhase.PAUSED) pauseUpdateDownload()' in request_cancel, 'Cancel request does not pause first')
need('updateCancelConfirmationVisible = true' in request_cancel, 'Cancel question confirmation is not shown')
dismiss_cancel = block('    private fun dismissCancelUpdateDownload() {', '    private fun confirmCancelUpdateDownload()')
need('resumeUpdateDownload()' in dismiss_cancel, 'dismissing cancel confirmation does not resume')
confirm_cancel = block('    private fun confirmCancelUpdateDownload() {', '    private fun installDownloadedUpdate()')
need('updateDownloadCancelRequested = true' in confirm_cancel and 'activeUpdateDownloadCall?.cancel()' in confirm_cancel, 'confirmed cancel does not stop active HTTP call')
install = block('    private fun installDownloadedUpdate() {', '    private fun closeUpdateTransfer()')
need('if (state.phase != UpdateTransferPhase.COMPLETED) return' in install and 'requestPackageInstall(apk)' in install, 'installer is not gated behind explicit completed-state action')
close = block('    private fun closeUpdateTransfer() {', '    private fun appendBoundedUpdateLog')
need('UpdateTransferPhase.COMPLETED' in close and 'UpdateTransferPhase.CANCELED' in close and 'UpdateTransferPhase.FAILED' in close, 'Close is not restricted to terminal states')

transfer = block('private fun UpdateTransferDialog(', '@Composable\nprivate fun UpdateCancelConfirmationDialog')
for token in ['UpdateDialogKeyValue("Connection", connection)', 'UpdateDialogKeyValue("Download speed", formatUpdateSpeedDisplay(state.bytesPerSecond))', 'UpdateDialogKeyValue("Downloaded", progressText)', 'Text("Live log"', 'FontFamily.Monospace', '"Update downloaded"', '"Update canceled"']:
    need(token in transfer, f'transfer UI requirement missing: {token}')
need('UpdateTransferPhase.COMPLETED -> R.drawable.ic_check' in transfer, 'completed transfer does not use check glyph')
need('UpdateTransferPhase.CANCELED -> R.drawable.ic_close' in transfer, 'canceled transfer does not use X glyph')
need('ExpressiveContainedIconTextButton("Cancel", R.drawable.ic_close, onClick = onRequestCancel)' in transfer, 'active Cancel is not a contained X action')
need('UpdateTransferPhase.PAUSED -> ExpressiveTextButton("Resume", onClick = onResume)' in transfer, 'Resume is not uncontained/iconless')
need('ExpressivePrimaryIconTextButton("Install", R.drawable.ic_download_update, onClick = onInstall)' in transfer, 'Install is not a contained action using the requested update-download icon')
need('UpdateTransferPhase.COMPLETED, UpdateTransferPhase.CANCELED, UpdateTransferPhase.FAILED -> ExpressiveCloseButton(onClick = onClose)' in transfer, 'terminal Close-X action missing')
need('onDismissRequest = { if (terminal) onClose() }' in transfer, 'active transfer can be dismissed outside terminal states')
need('state.phase in setOf(UpdateTransferPhase.CONNECTING, UpdateTransferPhase.DOWNLOADING, UpdateTransferPhase.VERIFYING)' in transfer, 'active progress presentation missing')

cancel_dialog = block('private fun UpdateCancelConfirmationDialog(', '@Composable\nprivate fun ReleaseNotesContent')
need('QuestionDialogTitle("Cancel update download?")' in cancel_dialog, 'Cancel confirmation does not use question title')
need('ExpressiveContainedIconTextButton("Cancel download", R.drawable.ic_close, onClick = onConfirmCancel)' in cancel_dialog, 'Cancel confirmation destructive action is not contained X')
need('ExpressiveTextButton("Resume", onClick = onResume)' in cancel_dialog, 'Cancel confirmation Resume is not plain text')

network = block('    private fun fetchLatestCompatibleUpdate(): AppUpdate?', '    private fun installedVersionName()')
need('parsedUrl.scheme != "https"' in network and 'parsedUrl.host != "github.com"' in network and '!parsedUrl.encodedPath.startsWith("/EFIShell0/VulkanScope/releases/download/")' in network, 'official GitHub APK provenance validation drifted')
download = block('    private suspend fun downloadUpdateApk(update: AppUpdate): File', '    private fun packageSigningCertificatesMatch')
need(download.count('256L * 1024L * 1024L') >= 2, '256 MiB update ceiling is not enforced both before and during streaming')
for token in ['File(cacheDir, "updates")', 'output.fd.sync()', 'archive.packageName != packageName', 'packageSigningCertificatesMatch(installed, archive)', 'archiveVersionCode <= installedVersionCode', 'archiveVersion != update.version', 'if (temp.exists()) temp.delete()']:
    need(token in download, f'retained APK security/resource contract missing: {token}')
need('android.permission.REQUEST_INSTALL_PACKAGES' in manifest, 'existing explicit package-install permission disappeared')
need('MANAGE_EXTERNAL_STORAGE' not in manifest, 'unrequested broad storage permission introduced')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 1.3.2 persistent update-transfer dialog and explicit-install contract')
