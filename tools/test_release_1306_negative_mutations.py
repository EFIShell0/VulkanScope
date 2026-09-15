#!/usr/bin/env python3
import shutil, subprocess, tempfile
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]
VERIFY=ROOT/'tools/verify_release_1306.py'
MAIN='app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'

def run(root, expect):
    p=subprocess.run(['python3',str(VERIFY),'--root',str(root)],stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True)
    if (p.returncode==0) != expect: raise AssertionError(p.stdout)

def mutate(label, old, new, expect=False):
    with tempfile.TemporaryDirectory(prefix='vs1306_') as d:
        dst=Path(d)/'root'; shutil.copytree(ROOT,dst)
        f=dst/MAIN; s=f.read_text()
        if old not in s: raise AssertionError(f'{label}: anchor missing')
        f.write_text(s.replace(old,new,1)); run(dst,expect)


def mutate_nth(label, old, new, occurrence, expect=False):
    with tempfile.TemporaryDirectory(prefix='vs1306_') as d:
        dst=Path(d)/'root'; shutil.copytree(ROOT,dst)
        f=dst/MAIN; s=f.read_text()
        parts=s.split(old)
        if len(parts) <= occurrence: raise AssertionError(f'{label}: anchor occurrence missing')
        f.write_text(old.join(parts[:occurrence]) + new + old.join(parts[occurrence:]))
        run(dst,expect)

def main():
    run(ROOT,True)
    mutate('SAF comeback','private fun sharedStorageRoot()', 'private val OpenDocument = "forbidden"\nprivate fun sharedStorageRoot()')
    mutate('canonical confinement','isCanonicalSharedStoragePath(root, canonical)', 'true /* confinement removed */')
    mutate('symlink rejection','if (!isCanonicalSharedStoragePath(root, canonical) || file.absoluteFile.path != canonical.path) error("Selected file is outside approved shared storage")','if (!isCanonicalSharedStoragePath(root, canonical) || file.absoluteFile.path == canonical.path) error("Selected file is outside approved shared storage")')
    mutate_nth('listing bound','children.size >= 4096','children.size >= 999999',2)
    mutate('denial duration','delay(3000)','delay(30)')
    mutate('analysis schema','VulkanScopeAnalysisSnapshot1','VulkanScopeAnalysisSnapshotX')
    mutate('profile ceiling','256 * 1024','999 * 1024')
    mutate('atomic move','StandardCopyOption.ATOMIC_MOVE','StandardCopyOption.COPY_ATTRIBUTES')
    mutate('snapshot saveability','pendingExportFilename by rememberSaveable','pendingExportFilename by remember')
    mutate('complete gate','completeReportReady','completeReportReadyBROKEN')
    # False-positive control: permitted explanatory wording only.
    with tempfile.TemporaryDirectory(prefix='vs1306_ok_') as d:
        dst=Path(d)/'root'; shutil.copytree(ROOT,dst); f=dst/MAIN; s=f.read_text(); anchor='Choose a shared-storage folder'
        if anchor not in s: raise AssertionError('false-positive anchor missing')
        f.write_text(s.replace(anchor,'Select a shared-storage folder',1)); run(dst,True)
    print('release_1306 negative mutations: PASS')
if __name__=='__main__': main()
