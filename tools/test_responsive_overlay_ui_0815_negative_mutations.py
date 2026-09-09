#!/usr/bin/env python3
import shutil, subprocess, tempfile
from pathlib import Path
root=Path(__file__).resolve().parents[1]
verifier=root/'tools/verify_responsive_overlay_ui_0815.py'
current_gradle=(root/'app/build.gradle.kts').read_text(encoding='utf-8')
successor='versionName = "0.80.15"' not in current_gradle

def run(candidate):
    command=['python',str(verifier),'--root',str(candidate)]
    if successor: command.append('--skip-version')
    return subprocess.run(command,stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True).returncode

def mutate(path,old,new,label):
    with tempfile.TemporaryDirectory() as tmp:
        candidate=Path(tmp)/'root'; shutil.copytree(root,candidate)
        target=candidate/path; text=target.read_text(encoding='utf-8')
        if old not in text: raise SystemExit('mutation source missing: '+label)
        target.write_text(text.replace(old,new,1),encoding='utf-8')
        if run(candidate)==0: raise SystemExit('negative mutation passed unexpectedly: '+label)

main='app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
mutate(main,'end = 18.dp','end = 46.dp','restore reserved page lane')
mutate(main,'listState.isScrollInProgress','false','remove list scroll activity')
mutate(main,'delay(900)','delay(0)','remove idle visibility period')
mutate(main,'Modifier.padding(8.dp).size(24.dp)','Modifier.padding(4.dp).size(16.dp)','restore small arrows')
mutate(main,'Modifier.align(Alignment.TopCenter)','Modifier.align(Alignment.CenterEnd)','move upper hint back to side')
mutate(main,'Modifier.align(Alignment.BottomCenter)','Modifier.align(Alignment.CenterEnd)','move lower hint back to side')
mutate(main,'androidx.compose.foundation.BorderStroke(1.dp, VulkanOutlineVariant)','null','remove grouped detail outline')
mutate(main,'maxWidth < 420.dp','maxWidth < 300.dp','loosen detail stacking')
mutate(main,'BoxWithConstraints(Modifier.fillMaxWidth()) {\n            val columns = when {','Row(Modifier.horizontalScroll(rememberScrollState())) {\n            val columns = when {','restore Explore horizontal strip')
mutate(main,'maxWidth < 540.dp -> 2','maxWidth < 540.dp -> 4','restore fixed-narrow Quick access')
if not successor: mutate('app/build.gradle.kts','versionCode = 815','versionCode = 814','stale versionCode')
with tempfile.TemporaryDirectory() as tmp:
    candidate=Path(tmp)/'root'; shutil.copytree(root,candidate)
    p=candidate/'changelog.md'; p.write_text(p.read_text(encoding='utf-8')+'\nUnrelated wording false-positive control.\n',encoding='utf-8')
    if run(candidate)!=0: raise SystemExit('false-positive control failed')
print('PASS 0.80.15 negative mutations and false-positive control')
