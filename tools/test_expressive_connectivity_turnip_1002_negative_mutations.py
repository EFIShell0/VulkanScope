#!/usr/bin/env python3
import shutil
import subprocess
import tempfile
from pathlib import Path

root=Path(__file__).resolve().parents[1]
verifier=root/'tools/verify_expressive_connectivity_turnip_1002.py'

def run(candidate):
    return subprocess.run(['python',str(verifier),'--root',str(candidate)],stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True).returncode

def mutate(path,old,new,label):
    with tempfile.TemporaryDirectory() as tmp:
        candidate=Path(tmp)/'root'
        shutil.copytree(root,candidate)
        target=candidate/path
        text=target.read_text(encoding='utf-8')
        if old not in text: raise SystemExit('mutation source missing: '+label)
        target.write_text(text.replace(old,new,1),encoding='utf-8')
        if run(candidate)==0: raise SystemExit('negative mutation passed unexpectedly: '+label)

main='app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
mutate('app/src/main/AndroidManifest.xml','    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />\n','', 'remove network-state permission')
mutate(main,'capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&\n        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)','capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)', 'drop validated-network requirement')
mutate(main,'var remainingVisibleMillis = 4_500L','var remainingVisibleMillis = 45_000L','unboundedly long network banner')
mutate(main,'painter = painterResource(if (connected) R.drawable.ic_network_connected else R.drawable.ic_network_disconnected)','painter = painterResource(R.drawable.ic_network_connected)','collapse network state icon distinction')
mutate(main,'val leftMovesBackward = layoutDirection == LayoutDirection.Ltr','val leftMovesBackward = true','remove RTL carousel direction')
mutate(main,'Icon(painterResource(R.drawable.ic_chevron_left), contentDescription = "Scroll filters left"','Icon(painterResource(R.drawable.ic_chevron_left), contentDescription = null','remove left-arrow accessibility label')
mutate(main,'ExpressiveFilterBar(sections, sections.indexOf(filter).coerceAtLeast(0))','Row(Modifier.horizontalScroll(rememberScrollState()))','restore Properties hidden filter strip')
mutate(main,'"Evidence rows" to evidenceResultCount.toString()','"Evidence" to evidenceResultCount.toString()','remove canonical evidence metric')
mutate(main,'put("location", source.location.take(4096))','put("location_removed", source.location.take(4096))','remove Turnip source location provenance')
mutate(main,'driverDate = text("driverDate") ?: text("date")','driverDate = "2026-01-01"','fabricate Turnip driver date')
current_gradle=(root/'app/build.gradle.kts').read_text(encoding='utf-8')
import re
match=re.search(r'versionCode\s*=\s*(\d+)',current_gradle)
if not match: raise SystemExit('mutation source missing: stale successor versionCode')
current_code=match.group(1)
mutate('app/build.gradle.kts',f'versionCode = {current_code}','versionCode = 1001','stale successor versionCode')
with tempfile.TemporaryDirectory() as tmp:
    candidate=Path(tmp)/'root'
    shutil.copytree(root,candidate)
    p=candidate/'changelog.md'
    p.write_text(p.read_text(encoding='utf-8')+'\nUnrelated 1.0.2 false-positive control wording.\n',encoding='utf-8')
    if run(candidate)!=0: raise SystemExit('false-positive control failed')
print('PASS 1.0.2 negative mutations and false-positive control')
