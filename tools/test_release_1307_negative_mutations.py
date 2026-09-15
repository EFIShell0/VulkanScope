#!/usr/bin/env python3
import shutil, subprocess, tempfile
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]; V=ROOT/'tools/verify_release_1307.py'; MAIN='app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
def run(root,ok):
    p=subprocess.run(['python3',str(V),'--root',str(root)],stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True)
    if (p.returncode==0)!=ok: raise AssertionError(p.stdout)
def mut(label,old,new):
    with tempfile.TemporaryDirectory(prefix='vs1307_') as d:
        dst=Path(d)/'root'; shutil.copytree(ROOT,dst); f=dst/MAIN; s=f.read_text();
        if old not in s: raise AssertionError(label+' anchor missing')
        f.write_text(s.replace(old,new,1)); run(dst,False)
def main():
    run(ROOT,True)
    mut('Turnip opt-in removed','@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun TurnipFileManagerDialog(', '@Composable\nprivate fun TurnipFileManagerDialog(')
    mut('shared storage opt-in removed','@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun SharedStorageBrowserDialog(', '@Composable\nprivate fun SharedStorageBrowserDialog(')
    mut('wrong opt-in class','@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun TurnipFileManagerDialog(', '@OptIn(ExperimentalMaterial3Api::class)\n@Composable\nprivate fun TurnipFileManagerDialog(')
    # false-positive control: documentation wording may vary without affecting production contract
    with tempfile.TemporaryDirectory(prefix='vs1307_ok_') as d:
        dst=Path(d)/'root'; shutil.copytree(ROOT,dst); f=dst/'changelog.md'; f.write_text(f.read_text()+'\n')
        run(dst,True)
    print('release_1307 negative mutations: PASS')
if __name__=='__main__': main()
