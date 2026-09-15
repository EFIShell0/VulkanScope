#!/usr/bin/env python3
import argparse, hashlib, json, sys
from pathlib import Path

def fail(m): raise AssertionError(m)
def need(t,x,m=None):
    if x not in t: fail(m or f'missing {x!r}')
def absent(t,x,m=None):
    if x in t: fail(m or f'forbidden {x!r}')
def sha(p): return hashlib.sha256(p.read_bytes()).hexdigest()
def block(t,a,b):
    if a not in t or b not in t.split(a,1)[1]: fail(f'cannot isolate {a} -> {b}')
    return t.split(a,1)[1].split(b,1)[0]

def main():
    ap=argparse.ArgumentParser(); ap.add_argument('--root',default='.'); ap.add_argument('--skip-version',action='store_true'); args=ap.parse_args()
    root=Path(args.root).resolve(); toolroot=Path(__file__).resolve().parents[1]
    mainp=root/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'; buildp=root/'app/build.gradle.kts'; rulesp=root/'rules/PROJECT_RULES.md'
    cp=root/'tests/golden/1.3.10_compile_update_palette_contract.json'
    if not cp.is_file(): cp=toolroot/'tests/golden/1.3.10_compile_update_palette_contract.json'
    for p in (mainp,buildp,rulesp,cp):
        if not p.is_file(): fail(f'missing {p}')
    src=mainp.read_text(); build=buildp.read_text(); rules=rulesp.read_text(); c=json.loads(cp.read_text())
    if not args.skip_version:
        need(build,'versionCode = 1311'); need(build,'versionName = "1.3.11"')
        need(rules,'## Release 1.3.11 compile fix and updater palette hardening')
    need(src,'import androidx.compose.foundation.layout.fillMaxHeight','fillMaxHeight import missing')
    need(src,'Column(Modifier.fillMaxHeight().padding(12.dp)','filter popup no longer uses intended fillMaxHeight layout')
    prefs=block(src,'private fun DriverUpdatePreferencesPage(','private fun TurnipFileManagerDialog(')
    for x in [
        'Text("Direct GitHub updates", color = VulkanTextPrimary, fontWeight = FontWeight.SemiBold)',
        'color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)',
        'color = VulkanTextMuted, style = MaterialTheme.typography.bodySmall)'
    ]: need(prefs,x,'update preferences palette drift: '+x)
    status=block(src,'private fun UpdateStatusBanner(','private fun UpdateDialogKeyValue(')
    for x in ['contentColor = VulkanTextPrimary','Text("Checking for updates…", color = VulkanTextSecondary','Text("VulkanScope is up to date.", color = VulkanTextSecondary','Text("Downloading update…", color = VulkanTextSecondary']:
        need(status,x,'update status palette drift: '+x)
    consent=block(src,'private fun DirectUpdatesConsentDialog(','private fun UpdateConfirmationDialog(')
    for x in ['titleContentColor = VulkanTextPrimary','textContentColor = VulkanTextPrimary','color = VulkanTextPrimary)','color = VulkanTextSecondary, style = MaterialTheme.typography.bodySmall)']:
        need(consent,x,'consent dialog palette drift: '+x)
    confirm=block(src,'private fun UpdateConfirmationDialog(','private fun formatUpdateBytesDisplay(')
    for x in ['titleContentColor = VulkanTextPrimary','textContentColor = VulkanTextPrimary','color = VulkanSurfaceTonal, contentColor = VulkanTextPrimary','Text("Release notes", color = VulkanTextPrimary','color = VulkanSurfaceLow, contentColor = VulkanTextPrimary','color = VulkanTextMuted, style = MaterialTheme.typography.labelSmall)']:
        need(confirm,x,'confirmation dialog palette drift: '+x)
    transfer=block(src,'private fun UpdateTransferDialog(','private fun UpdateCancelConfirmationDialog(')
    for x in ['titleContentColor = VulkanTextPrimary','textContentColor = VulkanTextPrimary','color = VulkanSurfaceTonal, contentColor = VulkanTextPrimary','Text("Live log", color = VulkanTextPrimary','color = VulkanBlack, contentColor = VulkanTextPrimary']:
        need(transfer,x,'transfer dialog palette drift: '+x)
    for x in [
        'UpdateTransferPhase.CONNECTING, UpdateTransferPhase.DOWNLOADING, UpdateTransferPhase.PAUSED ->\n                    ExpressiveContainedIconTextButton("Cancel"',
        'UpdateTransferPhase.CONNECTING, UpdateTransferPhase.DOWNLOADING -> ExpressiveTextButton("Pause"',
        'UpdateTransferPhase.PAUSED -> ExpressiveTextButton("Resume"',
        'UpdateTransferPhase.COMPLETED ->\n                    ExpressivePrimaryIconTextButton("Install"',
        'UpdateTransferPhase.COMPLETED, UpdateTransferPhase.CANCELED, UpdateTransferPhase.FAILED -> ExpressiveCloseButton'
    ]: need(transfer,x,'update transfer action-state drift: '+x)
    cancel=block(src,'private fun UpdateCancelConfirmationDialog(','private fun ReleaseNotesContent(')
    need(cancel,'titleContentColor = VulkanTextPrimary'); need(cancel,'textContentColor = VulkanTextPrimary')
    # Retain security and cleanup invariants from updater core.
    for x in [
        'if (!packageSigningCertificatesMatch(installed, archive)) error("Downloaded package signing certificate does not match the installed VulkanScope build.")',
        'if (archiveVersionCode <= installedVersionCode) error("Downloaded package versionCode is not newer than the installed VulkanScope build.")',
        'if (temp.exists()) temp.delete()'
    ]: need(src,x,'updater security/cleanup invariant drift: '+x)
    # All app/src/main artifacts except MainActivity are immutable predecessor bytes.
    for rel,expected in c['immutable_app_src_main_sha256'].items():
        p=root/rel
        if not p.is_file() or sha(p)!=expected: fail('unrelated app/src/main drift: '+rel)
    print('release_1311 verifier: PASS')
if __name__=='__main__':
    try: main()
    except Exception as e: print('release_1311 verifier: FAIL:',e,file=sys.stderr); sys.exit(1)
