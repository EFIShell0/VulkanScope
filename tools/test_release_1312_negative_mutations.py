#!/usr/bin/env python3
import shutil, subprocess, tempfile
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]; VER=ROOT/'tools/verify_release_1312.py'; MAIN='app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
def run(root,ok,skip=False):
    cmd=['python3',str(VER),'--root',str(root)] + (['--skip-version'] if skip else [])
    p=subprocess.run(cmd,stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True)
    if (p.returncode==0)!=ok: raise AssertionError(p.stdout)
def mut(label,old,new):
    with tempfile.TemporaryDirectory(prefix='vs1312_') as t:
        d=Path(t)/'root'; shutil.copytree(ROOT,d); p=d/MAIN; s=p.read_text()
        if old not in s: raise AssertionError(label+' anchor missing')
        p.write_text(s.replace(old,new,1)); run(d,False)
def main():
    run(ROOT,True)
    mut('small filter search threshold','val showSearch = labels.size >= 5','val showSearch = labels.isNotEmpty()')
    mut('staged popup reveal removed','popupMounted = true\n            popupVisible = false\n            delay(20)\n            popupVisible = true','popupMounted = true\n            popupVisible = true')
    mut('selected reopen removed','val openingPage = selectedIndex?.takeIf { it in labels.indices }?.div(pageSize) ?: 0','val openingPage = 0')
    mut('page delete removed','candidate.isEmpty() -> pageField = value','candidate.isEmpty() -> Unit')
    mut('single-page chrome restored','if (indexed.isNotEmpty() && pageCount > 1) {','if (indexed.isNotEmpty()) {')
    mut('shared folder body click restored','private fun SharedStorageFolderRow(path: String, enabled: Boolean, onOpen: () -> Unit) {\n    val shape = MaterialTheme.shapes.large\n    Surface(\n        modifier = Modifier.fillMaxWidth(),','private fun SharedStorageFolderRow(path: String, enabled: Boolean, onOpen: () -> Unit) {\n    val shape = MaterialTheme.shapes.large\n    Surface(\n        modifier = Modifier.fillMaxWidth().clickable(enabled = enabled, onClick = onOpen),')
    mut('shared file arrow removed','FileManagerNavigateArrow(enabled = enabled, description = "Select ${entry.name}", onClick = onSelect)','Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = null)')
    mut('System visual badge removed','painterResource(R.drawable.ic_android)','painterResource(R.drawable.ic_info)')
    mut('Turnip grouped evidence removed','ExpressiveInfoPill("Source ZIP"','CapabilityKeyValue("Source ZIP"')
    with tempfile.TemporaryDirectory(prefix='vs1312_ok_') as t:
        d=Path(t)/'root'; shutil.copytree(ROOT,d); p=d/'changelog.md'; p.write_text(p.read_text()+'\n'); run(d,True)
    print('release_1312 negative mutations: PASS')
if __name__=='__main__': main()
