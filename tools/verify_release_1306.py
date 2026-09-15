#!/usr/bin/env python3
import argparse, hashlib, json, re, sys
from pathlib import Path

def fail(msg): raise AssertionError(msg)
def need(text, token, why=None):
    if token not in text: fail(why or f'missing {token!r}')
def absent(text, token, why=None):
    if token in text: fail(why or f'forbidden {token!r}')
def sha(p): return hashlib.sha256(p.read_bytes()).hexdigest()

def main():
    ap=argparse.ArgumentParser(); ap.add_argument('--root',default='.'); ap.add_argument('--skip-version',action='store_true'); a=ap.parse_args()
    r=Path(a.root).resolve(); src=(r/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(); analysis=(r/'app/src/main/java/com/efishell/vulkanscope/AdvancedAnalysis.kt').read_text(); manifest=(r/'app/src/main/AndroidManifest.xml').read_text(); build=(r/'app/build.gradle.kts').read_text(); rules=(r/'rules/PROJECT_RULES.md').read_text()
    if not a.skip_version:
        need(build,'versionCode = 1306'); need(build,'versionName = "1.3.6"')
    # SAF must remain absent.
    for t in ['OpenDocument','CreateDocument','ACTION_OPEN_DOCUMENT','ACTION_CREATE_DOCUMENT','takePersistableUriPermission','rememberLauncherForActivityResult']:
        absent(src,t)
    # Permission stays explicit and uses existing special-access contract.
    need(manifest,'android.permission.MANAGE_EXTERNAL_STORAGE')
    for t in ['READ_EXTERNAL_STORAGE','WRITE_EXTERNAL_STORAGE']: absent(manifest,t)
    for t in ['requestSharedStorageAccess','Environment.isExternalStorageManager()','Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION','pendingSharedStorageGranted','pendingSharedStorageDenied','delay(3000)']:
        need(src,t)
    # Browser/path/symlink/listing bounds and IO.
    for t in ['SharedStorageBrowserDialog','sharedStorageRoot()','isCanonicalSharedStoragePath','validatedSharedStorageImportFile','validatedSharedStorageDestination','withContext(Dispatchers.IO)','allowedExtensions']:
        need(src,t)
    shared_scan = src.split('private suspend fun scanSharedStorageDirectory(',1)[1].split('private fun validatedSharedStorageImportFile',1)[0]
    for t in ['children.size >= 4096','directory.absoluteFile.path != canonicalDirectory.path','isCanonicalSharedStoragePath(canonicalRoot, canonicalDirectory)','java.nio.file.Files.newDirectoryStream(canonicalDirectory.toPath())']:
        need(shared_scan,t)
    need(src,'if (!isCanonicalSharedStoragePath(root, canonical) || file.absoluteFile.path != canonical.path)')
    need(src,'if (!isCanonicalSharedStoragePath(root, target) || target.parentFile?.canonicalFile != canonicalDirectory)')
    # Atomic save / overwrite acknowledgement.
    for t in ['replaceSharedStorageFileAtomically','output.fd.sync()','StandardCopyOption.ATOMIC_MOVE','StandardCopyOption.REPLACE_EXISTING','pendingOverwrite','Overwrite existing file?']:
        need(src,t)
    # Analysis/profile/technicalReport schema and bounds.
    for t in ['VulkanScopeAnalysisSnapshot1','8 * 1024 * 1024','validateAnalysisSnapshot','VulkanScopeMinimumProfile1','256 * 1024','array.length() !in 1..ANALYSIS_CUSTOM_PROFILE_MAX_RULES','it.length > 512','name.length > 128','technicalReportJson','EXPORT_TECHNICAL_REPORT']:
        need(src,t)
    need(src,'if (obj.optString("schema") != "VulkanScopeAnalysisSnapshot1")')
    need(src,'readBoundedAnalysisBytes(input, 256 * 1024)')
    need(src,'writeSharedStorageBytes(target, bytes, 256 * 1024)')
    need(analysis,'ANALYSIS_CUSTOM_PROFILE_MAX_RULES = 64')
    # Complete TXT/HTML snapshot-at-init and private-cache lifecycle.
    for t in ['completeReportReady','createExportSnapshot','report_exports','64L * 1024L * 1024L','pendingExportFilename by rememberSaveable','pendingExportPath by rememberSaveable','pendingExportMime by rememberSaveable','cleanupStaleExportSnapshots','copySharedStorageFile','discardPendingSnapshot','prepareReportStorageExport']:
        need(src,t)
    info_page = src.split('private fun InfoPage(',1)[1].split('private fun DriverUpdatePreferencesPage(',1)[0]
    need(info_page,'val completeReportReady = isCompleteReportReady(report, collectionStatus)')
    # No intentional unavailable placeholder remains.
    for t in ['Export unavailable · SAF','storage export remains unavailable','Unavailable in 1.3.3']:
        absent(src,t)
    need(rules,'## Release 1.3.6 in-app import/export storage completion requirements')
    # Immutable predecessor production surfaces.
    g=json.loads((r/'tests/golden/1.3.5_storage_exchange_contract.json').read_text())
    for rel,expected in g['immutable_app_src_main_sha256'].items():
        p=r/rel
        if not p.is_file(): fail(f'immutable production file missing: {rel}')
        if sha(p)!=expected: fail(f'unrelated production drift: {rel}')
    print('release_1306 verifier: PASS')
if __name__=='__main__':
    try: main()
    except Exception as e:
        print(f'release_1306 verifier: FAIL: {e}',file=sys.stderr); sys.exit(1)
