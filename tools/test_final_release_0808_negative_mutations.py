#!/usr/bin/env python3
import shutil,subprocess,sys,tempfile
from pathlib import Path
root=Path(__file__).resolve().parents[1]
ver=root/'tools/verify_final_release_0808.py'
mainrel='app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
mutations=[
(mainrel,'if (collectionStatus == CollectionStatus.FAILED) return','if (collectionStatus == CollectionStatus.FAILED) Unit','failed-auto-hide-restored'),
('build.gradle.kts','version "9.4.0" apply false','version "9.3.2" apply false','agp-downgrade'),
(mainrel,'CapabilityKeyValue("Android Gradle Plugin", "9.4.0")','CapabilityKeyValue("Android Gradle Plugin", "9.3.2")','info-pin-drift')]
for rel,a,b,name in mutations:
    with tempfile.TemporaryDirectory(prefix='vs0808-mut-') as d:
        dst=Path(d)/'root';shutil.copytree(root,dst)
        p=dst/rel;s=p.read_text(encoding='utf-8')
        if a not in s:raise SystemExit('FAIL mutation source absent '+name)
        p.write_text(s.replace(a,b,1),encoding='utf-8')
        r=subprocess.run([sys.executable,str(ver),'--root',str(dst)],stdout=subprocess.DEVNULL,stderr=subprocess.DEVNULL)
        if r.returncode==0:raise SystemExit('FAIL mutation accepted '+name)
with tempfile.TemporaryDirectory(prefix='vs0808-fp-') as d:
    dst=Path(d)/'root';shutil.copytree(root,dst)
    p=dst/mainrel;s=p.read_text(encoding='utf-8')
    needle='Direct application and native library identities are reported from the release\'s pinned build configuration.'
    if needle in s:p.write_text(s.replace(needle,needle+' ',1),encoding='utf-8')
    r=subprocess.run([sys.executable,str(ver),'--root',str(dst)],stdout=subprocess.DEVNULL,stderr=subprocess.DEVNULL)
    if r.returncode!=0:raise SystemExit('FAIL false-positive descriptive mutation rejected')
print('PASS 0.80.8 negative mutations and false-positive control')
