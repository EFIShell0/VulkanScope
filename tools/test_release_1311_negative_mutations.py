#!/usr/bin/env python3
import shutil, subprocess, tempfile
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]; VER=ROOT/'tools/verify_release_1311.py'; MAIN='app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
def run(root,ok):
    p=subprocess.run(['python3',str(VER),'--root',str(root)],stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True)
    if (p.returncode==0)!=ok: raise AssertionError(p.stdout)
def mut(label,old,new):
    with tempfile.TemporaryDirectory(prefix='vs1311_') as t:
        d=Path(t)/'root'; shutil.copytree(ROOT,d); p=d/MAIN; s=p.read_text()
        if old not in s: raise AssertionError(label+' anchor missing')
        p.write_text(s.replace(old,new,1)); run(d,False)

def mut_block(label,start,end,old,new):
    with tempfile.TemporaryDirectory(prefix='vs1311_') as t:
        d=Path(t)/'root'; shutil.copytree(ROOT,d); p=d/MAIN; s=p.read_text()
        if start not in s or end not in s.split(start,1)[1]: raise AssertionError(label+' block missing')
        before,rest=s.split(start,1); body,after=rest.split(end,1)
        if old not in body: raise AssertionError(label+' anchor missing')
        p.write_text(before+start+body.replace(old,new,1)+end+after); run(d,False)
def main():
    run(ROOT,True)
    mut('fillMaxHeight import removed','import androidx.compose.foundation.layout.fillMaxHeight\n','')
    mut('status content color removed','contentColor = VulkanTextPrimary,\n            shape = MaterialTheme.shapes.large','shape = MaterialTheme.shapes.large')
    mut('consent text palette removed','Text("$appName will check for updates and download APKs directly from $releaseSource.", color = VulkanTextPrimary)','Text("$appName will check for updates and download APKs directly from $releaseSource.")')
    mut_block('confirmation nested surface palette removed','private fun UpdateConfirmationDialog(','private fun formatUpdateBytesDisplay(','color = VulkanSurfaceTonal, contentColor = VulkanTextPrimary','color = VulkanSurfaceTonal')
    mut_block('transfer live-log title palette removed','private fun UpdateTransferDialog(','private fun UpdateCancelConfirmationDialog(','Text("Live log", color = VulkanTextPrimary','Text("Live log", color = ComposeColor.Black')
    mut('signature check removed','if (!packageSigningCertificatesMatch(installed, archive)) error("Downloaded package signing certificate does not match the installed VulkanScope build.")','if (false) error("Downloaded package signing certificate does not match the installed VulkanScope build.")')
    mut('partial cleanup removed','if (temp.exists()) temp.delete()','if (false && temp.exists()) temp.delete()')
    with tempfile.TemporaryDirectory(prefix='vs1311_ok_') as t:
        d=Path(t)/'root'; shutil.copytree(ROOT,d); p=d/'changelog.md'; p.write_text(p.read_text()+'\n'); run(d,True)
    print('release_1311 negative mutations: PASS')
if __name__=='__main__': main()
