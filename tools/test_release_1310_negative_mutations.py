#!/usr/bin/env python3
import shutil, subprocess, tempfile
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]; VER=ROOT/'tools/verify_release_1310.py'; MAIN='app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
def run(root,ok):
    p=subprocess.run(['python3',str(VER),'--root',str(root)],stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True)
    if (p.returncode==0)!=ok: raise AssertionError(p.stdout)
def mut(label,old,new):
    with tempfile.TemporaryDirectory(prefix='vs1310_') as t:
        d=Path(t)/'root'; shutil.copytree(ROOT,d); p=d/MAIN; s=p.read_text()
        if old not in s: raise AssertionError(label+' anchor missing')
        p.write_text(s.replace(old,new,1)); run(d,False)
def main():
    run(ROOT,True)
    mut('whole selector clickable','.size(44.dp)\n                        .clip(arrowShape)\n                        .clickable(enabled = enabled, role = Role.Button) { expanded = !expanded }','.size(44.dp)')
    mut('page motion removed','targetState = page to query','targetState = 0 to ""')
    mut('flexible result region removed','Box(Modifier.weight(1f).fillMaxWidth().heightIn(min = 56.dp))','Box(Modifier.fillMaxWidth().heightIn(min = 56.dp))')
    mut('folder arrow action removed','FileManagerNavigateArrow(enabled = enabled, description = "Open folder") { onOpen() }','Icon(painterResource(R.drawable.ic_chevron_right), contentDescription = null)')
    mut('duplicate UI bypass','enabled = !state.importing && !alreadyImported','enabled = !state.importing')
    mut('duplicate selection guard removed','if (turnipImportedSourceKey(candidate.path, candidate.name) in state.importedSourceKeys) return','if (false) return')
    mut('Mesa badge removed','MesaOfficialLogoBadge(size = 38.dp)','Image(painterResource(R.drawable.mesa3d_logo), contentDescription = "Mesa")')
    mut('About disclosure removed','VulkanScope is not an official Khronos Group project.','VulkanScope')
    with tempfile.TemporaryDirectory(prefix='vs1310_ok_') as t:
        d=Path(t)/'root'; shutil.copytree(ROOT,d); p=d/'changelog.md'; p.write_text(p.read_text()+'\n'); run(d,True)
    print('release_1310 negative mutations: PASS')
if __name__=='__main__': main()
