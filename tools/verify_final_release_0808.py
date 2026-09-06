#!/usr/bin/env python3
import argparse,re
from pathlib import Path
p=argparse.ArgumentParser();p.add_argument('--root');p.add_argument('--skip-version',action='store_true');a=p.parse_args()
root=Path(a.root).resolve() if a.root else Path(__file__).resolve().parents[1]
errors=[]
main=(root/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
rg=(root/'build.gradle.kts').read_text(encoding='utf-8')
ag=(root/'app/build.gradle.kts').read_text(encoding='utf-8')
wrap=(root/'gradle/wrapper/gradle-wrapper.properties').read_text(encoding='utf-8')
if not a.skip_version:
    if not any(f'versionName = "{v}"' in ag and f'versionCode = {c}' in ag for v,c in [('0.80.8',808),('0.80.9',809),('0.80.10',810),('0.80.12',812),('0.80.13',813),('0.80.14',814),('0.80.15',815)]): errors.append('version identity must preserve the 0.80.8+ contract')
if 'id("com.android.application") version "9.4.0" apply false' not in rg: errors.append('AGP 9.4.0 exact pin missing')
if 'gradle-9.7.1-bin.zip' not in wrap: errors.append('Gradle 9.7.1 must be retained')
if 'CapabilityKeyValue("Android Gradle Plugin", "9.4.0")' not in main: errors.append('Info build-tool AGP identity stale')
m=re.search(r'private fun finishCollectionStatusSoon\(delayMillis: Long = 2000L\) \{(.*?)\n    \}',main,re.S)
if not m: errors.append('collection terminal status helper missing')
else:
    body=m.group(1)
    for token in ['collectionFinishedSuccessfully(latestReport)','CollectionStatus.COMPLETED','CollectionStatus.FAILED']:
        if token not in body: errors.append('collection terminal classification missing '+token)
    if 'if (collectionStatus == CollectionStatus.FAILED) return' not in body: errors.append('FAILED collection banner is not persistent')
    if body.find('if (collectionStatus == CollectionStatus.FAILED) return')>body.find('collectionCompletionJob = activityScope.launch') and 'collectionCompletionJob = activityScope.launch' in body: errors.append('FAILED persistence guard must precede auto-hide scheduling')
for path in ['rules/0.80.8_FINAL_AGP_COLLECTION_DATABASE_AUDIT.md','tools/test_final_release_0808_state_machine.py','tools/test_final_release_0808_negative_mutations.py']:
    if not (root/path).is_file(): errors.append('missing 0.80.8 release gate '+path)
if errors: raise SystemExit('FAIL 0.80.8 final release contract\n- '+'\n- '.join(errors))
print('PASS 0.80.8 final release contract: persistent Failed state + AGP 9.4.0 + Gradle 9.7.1')
