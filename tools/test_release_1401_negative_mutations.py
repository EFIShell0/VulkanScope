#!/usr/bin/env python3
import shutil, subprocess, tempfile
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]; VER=ROOT/'tools/verify_release_1401.py'; MAIN='app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'

def run(root, expected, skip=False):
    cmd=['python3',str(VER),'--root',str(root)]+(['--skip-version'] if skip else [])
    p=subprocess.run(cmd,stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True)
    if (p.returncode==0)!=expected: raise AssertionError(p.stdout)

def mutate(label,old,new):
    with tempfile.TemporaryDirectory(prefix='vs1401_') as tmp:
        tree=Path(tmp)/'root'; shutil.copytree(ROOT,tree); path=tree/MAIN; text=path.read_text()
        if old not in text: raise AssertionError(label+' anchor missing')
        path.write_text(text.replace(old,new,1)); run(tree,False)

def main():
    run(ROOT,True)
    mutate('scroll remainder leak','if (available.y != 0f) Offset(0f, available.y) else Offset.Zero','Offset.Zero')
    mutate('fling remainder leak','if (available.y != 0f) Velocity(0f, available.y) else Velocity.Zero','Velocity.Zero')
    mutate('single boundary removed','Box(Modifier.fillMaxSize().nestedScroll(boundaryScrollConnection))','Box(Modifier.fillMaxSize())')
    mutate('multi boundary removed','.heightIn(min = 56.dp, max = 420.dp).nestedScroll(boundaryScrollConnection)','.heightIn(min = 56.dp, max = 420.dp)')
    mutate('pre-scroll interception added','object : NestedScrollConnection {','object : NestedScrollConnection {\n        override fun onPreScroll(available: Offset, source: NestedScrollSource) = available')
    mutate('wide library metadata removed','ExpressiveInfoPill("Vulkan® library", info.libraryName ?: "Not available", Modifier.weight(1f))','Text("library hidden")')
    mutate('compact description metadata removed','ExpressiveInfoPill("Description", info.description?.takeIf { it.isNotBlank() } ?: "Not provided", Modifier.fillMaxWidth())','Text("description hidden")')
    with tempfile.TemporaryDirectory(prefix='vs1401_doc_') as tmp:
        tree=Path(tmp)/'root'; shutil.copytree(ROOT,tree); p=tree/'rules/1.4.1_FILTER_BOUNDARY_TURNIP_SLOT_METADATA_AUDIT.md'; p.write_text(p.read_text()+'\n'); run(tree,True)
    print('release_1401 negative mutations: PASS')

if __name__=='__main__': main()
