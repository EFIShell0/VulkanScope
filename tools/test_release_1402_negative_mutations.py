#!/usr/bin/env python3
import shutil, subprocess, tempfile
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1]; VER=ROOT/'tools/verify_release_1402.py'; MAIN='app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'

def run(root, expected, skip=False):
    cmd=['python3',str(VER),'--root',str(root)]+(['--skip-version'] if skip else [])
    p=subprocess.run(cmd,stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True)
    if (p.returncode==0)!=expected: raise AssertionError(p.stdout)

def mutate(label,old,new):
    with tempfile.TemporaryDirectory(prefix='vs1402_') as tmp:
        tree=Path(tmp)/'root'; shutil.copytree(ROOT,tree); path=tree/MAIN; text=path.read_text()
        if old not in text: raise AssertionError(label+' anchor missing')
        path.write_text(text.replace(old,new,1)); run(tree,False)

def main():
    run(ROOT,True)
    mutate('landscape budget restored','((usableHeight.value * 0.62f).dp).coerceIn(220.dp, 440.dp)','(usableHeight - 96.dp).coerceIn(240.dp, 620.dp)')
    mutate('landscape row budget restored','size.coerceIn(1, if (landscape) 4 else 7)','size.coerceIn(1, 7)')
    mutate('pagination centering removed','Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {','Box(Modifier.fillMaxWidth()) {')
    mutate('page field widened','modifier = Modifier.width(72.dp).onFocusChanged','modifier = Modifier.width(96.dp).onFocusChanged')
    mutate('page text alignment removed','textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),','textStyle = MaterialTheme.typography.bodyMedium,')
    mutate('single scroll containment removed','Box(Modifier.fillMaxSize().nestedScroll(boundaryScrollConnection))','Box(Modifier.fillMaxSize())')
    with tempfile.TemporaryDirectory(prefix='vs1402_screen_') as tmp:
        tree=Path(tmp)/'root'; shutil.copytree(ROOT,tree); (tree/'screenshots').mkdir(); (tree/'screenshots/1.png').write_bytes(b'x'); run(tree,False)
    with tempfile.TemporaryDirectory(prefix='vs1402_doc_') as tmp:
        tree=Path(tmp)/'root'; shutil.copytree(ROOT,tree); p=tree/'rules/1.4.2_FILTER_LANDSCAPE_PAGINATION_AUDIT.md'; p.write_text(p.read_text()+'\n'); run(tree,True)
    print('release_1402 negative mutations: PASS')

if __name__=='__main__': main()
