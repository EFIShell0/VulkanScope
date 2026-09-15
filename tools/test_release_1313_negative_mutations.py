#!/usr/bin/env python3
import shutil, subprocess, tempfile
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]; VER=ROOT/'tools/verify_release_1313.py'; MAIN='app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
def run(root,ok,skip=False):
    cmd=['python3',str(VER),'--root',str(root)] + (['--skip-version'] if skip else [])
    p=subprocess.run(cmd,stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True)
    if (p.returncode==0)!=ok: raise AssertionError(p.stdout)
def mut(label,old,new):
    with tempfile.TemporaryDirectory(prefix='vs1313_') as t:
        d=Path(t)/'root'; shutil.copytree(ROOT,d); p=d/MAIN; s=p.read_text()
        if old not in s: raise AssertionError(label+' anchor missing')
        p.write_text(s.replace(old,new,1)); run(d,False)
def main():
    run(ROOT,True)
    mut('Mesa tint removed','colorFilter = ColorFilter.tint(VulkanAccentSoft)','colorFilter = null')
    mut('System vendor logo removed','SystemDriverVendorBadge(summary?.vendorId)','Icon(painterResource(R.drawable.ic_android), contentDescription = null)')
    mut('Turnip driver-name pill mislabeled','ExpressiveInfoPill("Driver name", info.driverName ?: "Not provided"','ExpressiveInfoPill("Driver version", info.driverVersion ?: "Not provided"')
    mut('free popup restored','Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {\n            val selectorShape','Popup(Alignment.TopStart) {\n            val selectorShape')
    mut('arrow closes menu','.clickable(enabled = enabled && !expanded, role = Role.Button) { expanded = true }','.clickable(enabled = enabled, role = Role.Button) { expanded = !expanded }')
    mut('selection closes menu','.clickable(role = Role.RadioButton) { onSelected(index) }','.clickable(role = Role.RadioButton) { onSelected(index); expanded = false }')
    mut('Back closes menu','BackHandler(enabled = dropdownMounted && expanded) {\n                if (imeVisible) focusManager.clearFocus(force = true)\n            }','BackHandler(enabled = dropdownMounted && expanded) {\n                if (imeVisible) focusManager.clearFocus(force = true) else expanded = false\n            }')
    with tempfile.TemporaryDirectory(prefix='vs1313_ok_') as t:
        d=Path(t)/'root'; shutil.copytree(ROOT,d); p=d/'changelog.md'; p.write_text(p.read_text()+'\n'); run(d,True)
    print('release_1313 negative mutations: PASS')
if __name__=='__main__': main()
