#!/usr/bin/env python3
import shutil
import subprocess
import tempfile
from pathlib import Path

root=Path(__file__).resolve().parents[1]
verifier=root/'tools/verify_offline_turnip_manager_1004.py'
main_rel=Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
gradle_rel=Path('app/build.gradle.kts')

def run(mutator,expect_fail=True):
    with tempfile.TemporaryDirectory(prefix='vs1004mut_') as td:
        dst=Path(td)/'root'
        shutil.copytree(root,dst,ignore=shutil.ignore_patterns('.gradle','build','.cxx'))
        mutator(dst)
        p=subprocess.run(['python3',str(dst/'tools/verify_offline_turnip_manager_1004.py'),'--root',str(dst)],stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True)
        if expect_fail and p.returncode==0: raise AssertionError('mutation unexpectedly passed\n'+p.stdout)
        if not expect_fail and p.returncode!=0: raise AssertionError('false-positive control failed\n'+p.stdout)

def replace_once(path,old,new):
    s=path.read_text(encoding='utf-8')
    if old not in s: raise AssertionError('mutation anchor missing: '+old[:80])
    path.write_text(s.replace(old,new,1),encoding='utf-8')

mutations=[
    lambda d: replace_once(d/main_rel,'if (state != NetworkBannerState.HIDDEN) renderedState = state','renderedState = state'),
    lambda d: replace_once(d/main_rel,'var remainingVisibleMillis = 4_500L','var remainingVisibleMillis = 0L'),
    lambda d: replace_once(d/main_rel,'OfflineFeatureAvailabilityBanner(collectionStatus == CollectionStatus.COLLECTING)','OfflineFeatureAvailabilityBanner(false)'),
    lambda d: replace_once(d/main_rel,'Internet features are unavailable','Offline'),
    lambda d: replace_once(d/main_rel,'enabled = !submissionInFlight && completeReportReady && networkAvailable','enabled = !submissionInFlight && completeReportReady'),
    lambda d: replace_once(d/main_rel,'private const val TURNIP_MANAGER_MAX_DRIVERS = 10','private const val TURNIP_MANAGER_MAX_DRIVERS = 11'),
    lambda d: replace_once(d/main_rel,'Driver imported into slot %02d. Activate it from the Turnip driver manager.','Driver imported and activated.'),
    lambda d: replace_once(d/main_rel,'if (!tempSlotRoot.renameTo(finalSlotRoot))','if (false && !tempSlotRoot.renameTo(finalSlotRoot))'),
    lambda d: replace_once(d/main_rel,'File(filesDir, "turnip_imports")','Environment.getExternalStorageDirectory()'),
    lambda d: replace_once(d/main_rel,'.setType("*/*")','.setType("application/zip")'),
    lambda d: replace_once(d/main_rel,'"turnip_%02d.zip"','"turnip_%02d.bin"'),
    lambda d: replace_once(d/main_rel,'FlowRow(horizontalArrangement = Arrangement.spacedBy(2.dp), verticalArrangement = Arrangement.spacedBy(2.dp))','Row(horizontalArrangement = Arrangement.spacedBy(2.dp))'),
    lambda d: replace_once(d/main_rel,'Brush.horizontalGradient','Brush.verticalGradient'),
    lambda d: replace_once(d/gradle_rel,'versionCode = 1007','versionCode = 1003'),
]
for m in mutations: run(m,True)
run(lambda d: (d/'rules/1.0.4_OFFLINE_TURNIP_MANAGER_FILTER_EDGE_AUDIT.md').write_text((d/'rules/1.0.4_OFFLINE_TURNIP_MANAGER_FILTER_EDGE_AUDIT.md').read_text(encoding='utf-8')+'\nUnrelated audit wording.\n',encoding='utf-8'),False)
print(f'PASS 1.0.4 negative mutations: {len(mutations)} defects rejected + false-positive control')
