#!/usr/bin/env python3
import shutil, subprocess, tempfile
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]; V=ROOT/'tools/verify_release_1308.py'; MAIN='app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
def run(root,ok):
    p=subprocess.run(['python3',str(V),'--root',str(root)],stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True)
    if (p.returncode==0)!=ok: raise AssertionError(p.stdout)
def mut(label,old,new):
    with tempfile.TemporaryDirectory(prefix='vs1308_') as d:
        dst=Path(d)/'root'; shutil.copytree(ROOT,dst); f=dst/MAIN; s=f.read_text()
        if old not in s: raise AssertionError(label+' anchor missing')
        f.write_text(s.replace(old,new,1)); run(dst,False)
def main():
    run(ROOT,True)
    mut('popup motion removed','scaleIn(tween(180), initialScale = 0.96f)','fadeIn(tween(180))')
    mut('strict page range removed','if (requested !in 1..pageCount) return@OutlinedTextField','if (requested < 1) return@OutlinedTextField')
    mut('numeric keyboard removed','KeyboardOptions(keyboardType = KeyboardType.Number)','KeyboardOptions.Default')
    mut('filter scroll hints removed','ExpressiveScrollHints(listState, Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 4.dp))','Spacer(Modifier.size(1.dp))')
    mut('file manager dark content color removed','shape = MaterialTheme.shapes.extraLarge,\n            color = VulkanSurfaceRaised,\n            contentColor = VulkanTextPrimary','shape = MaterialTheme.shapes.extraLarge,\n            color = VulkanSurfaceRaised')
    mut('segmented view role removed','role = Role.RadioButton','role = Role.Button')
    mut('legacy green selection returned','color = if (selected) VulkanAccentContainer else VulkanSurfaceTonal','color = if (selected) ComposeColor(0xFF163121) else VulkanSurfaceTonal')
    with tempfile.TemporaryDirectory(prefix='vs1308_ok_') as d:
        dst=Path(d)/'root'; shutil.copytree(ROOT,dst); f=dst/'changelog.md'; f.write_text(f.read_text()+'\n'); run(dst,True)
    print('release_1308 negative mutations: PASS')
if __name__=='__main__': main()
