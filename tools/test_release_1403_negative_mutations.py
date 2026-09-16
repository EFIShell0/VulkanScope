#!/usr/bin/env python3
import shutil, subprocess, tempfile
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]; VER=ROOT/'tools/verify_release_1403.py'; MAIN='app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'; MANIFEST='app/src/main/AndroidManifest.xml'

def run(root, expected, skip=False):
    cmd=['python3',str(VER),'--root',str(root)]+(['--skip-version'] if skip else [])
    p=subprocess.run(cmd,stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True)
    if (p.returncode==0)!=expected: raise AssertionError(p.stdout)

def mutate(path,label,old,new):
    with tempfile.TemporaryDirectory(prefix='vs1403_') as tmp:
        tree=Path(tmp)/'root'; shutil.copytree(ROOT,tree); p=tree/path; text=p.read_text()
        if old not in text: raise AssertionError(label+' anchor missing')
        p.write_text(text.replace(old,new,1)); run(tree,False)

def main():
    run(ROOT,True)
    mutate(MAIN,'landscape width recapped','val dropdownWidth = if (landscape) maxWidth else maxWidth.coerceAtMost(560.dp)','val dropdownWidth = maxWidth.coerceAtMost(560.dp)')
    mutate(MAIN,'active destination saver weakened','restore = { saved -> Page.values().find { it.name == saved } ?: Page.Overview }','restore = { saved -> Page.Overview }')
    mutate(MAIN,'selected device no longer saveable','var selectedDeviceIndex by rememberSaveable { mutableIntStateOf(0) }','var selectedDeviceIndex by remember { mutableIntStateOf(0) }')
    mutate(MAIN,'Surface recreation promoted to full scan','else if (latestReport != null) {\n                            requestSurfaceRefresh(surface)','else if (latestReport != null) {\n                            requestReportCollection()')
    mutate(MANIFEST,'orientation config handling removed','orientation|screenSize|smallestScreenSize|screenLayout|keyboardHidden','screenSize|smallestScreenSize|screenLayout|keyboardHidden')
    mutate(MANIFEST,'screen size config handling removed','orientation|screenSize|smallestScreenSize|screenLayout|keyboardHidden','orientation|smallestScreenSize|screenLayout|keyboardHidden')
    with tempfile.TemporaryDirectory(prefix='vs1403_screen_') as tmp:
        tree=Path(tmp)/'root'; shutil.copytree(ROOT,tree); (tree/'screenshots').mkdir(); (tree/'screenshots/1.png').write_bytes(b'x'); run(tree,False)
    with tempfile.TemporaryDirectory(prefix='vs1403_doc_') as tmp:
        tree=Path(tmp)/'root'; shutil.copytree(ROOT,tree); p=tree/'rules/1.4.3_LANDSCAPE_WIDTH_ROTATION_STATE_AUDIT.md'; p.write_text(p.read_text()+'\n'); run(tree,True)
    print('release_1403 negative mutations: PASS')

if __name__=='__main__': main()
