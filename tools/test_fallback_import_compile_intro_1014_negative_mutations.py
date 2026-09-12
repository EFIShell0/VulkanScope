#!/usr/bin/env python3
import shutil, subprocess, tempfile
from pathlib import Path
root=Path(__file__).resolve().parents[1]
ver=root/'tools/verify_fallback_import_compile_intro_1014.py'
gradle=(root/'app/build.gradle.kts').read_text(encoding='utf-8')
re=__import__('re')
vm=re.search(r'versionName\s*=\s*"(\d+)\.(\d+)\.(\d+)"', gradle)
cv=tuple(map(int,vm.groups())) if vm else (0,0,0)
release_minor=cv[2] if cv[:2]==(1,0) else (99 if cv >= (1,1,0) else 0)
source_icon='R.drawable.ic_download' if cv >= (1,2,0) else ('R.drawable.ic_zip_download' if release_minor >= 17 else 'R.drawable.ic_action_github')

def repl(path, old, new):
    s=path.read_text(encoding='utf-8')
    if old not in s: raise RuntimeError('mutation target missing: '+old[:80])
    path.write_text(s.replace(old,new,1),encoding='utf-8')

def run(d):
    return subprocess.run(['python3',str(d/'tools/verify_fallback_import_compile_intro_1014.py'),'--root',str(d)],capture_output=True,text=True).returncode
mutations=[
 ('candidate passed directly', lambda d: repl(d/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt','?.file?.let(onImport)','?.let(onImport)')),
 ('file extraction removed', lambda d: repl(d/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt','it.file.absolutePath == selectedPath }?.file?.let(onImport)','it.file.absolutePath == selectedPath }?.let(onImport)')),
 ('generic info restored', lambda d: repl(d/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt','UpdateStatus.DirectUpdatesDisabledIntro -> { UpdateSourceIcon();','UpdateStatus.DirectUpdatesDisabledIntro -> { UpdateStatusBadge("INFO");')),
 ('update source glyph lost', lambda d: repl(d/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt',f'painter = painterResource({source_icon})','painter = painterResource(R.drawable.ic_info)')),
 ('stale version', lambda d: repl(d/'app/build.gradle.kts', re.search(r'versionCode\s*=\s*\d+', (d/'app/build.gradle.kts').read_text(encoding='utf-8')).group(0), 'versionCode = 1013')),
 ('intro lifetime drift', lambda d: repl(d/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt','kotlinx.coroutines.delay(7_000L)','kotlinx.coroutines.delay(70_000L)')),
]
for name,fn in mutations:
    with tempfile.TemporaryDirectory() as td:
        d=Path(td)/'r'; shutil.copytree(root,d); fn(d)
        if run(d)==0: raise SystemExit('FAIL mutation accepted: '+name)
with tempfile.TemporaryDirectory() as td:
    d=Path(td)/'r'; shutil.copytree(root,d)
    p=d/'changelog.md'; p.write_text(p.read_text(encoding='utf-8')+'\nUnrelated audit wording control.\n',encoding='utf-8')
    if run(d)!=0: raise SystemExit('FAIL unrelated false-positive control rejected')
print(f'PASS VulkanScope 1.0.14 negative mutations: {len(mutations)} defects rejected + false-positive control')
