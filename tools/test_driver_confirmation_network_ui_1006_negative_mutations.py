#!/usr/bin/env python3
import shutil
import subprocess
import tempfile
from pathlib import Path

root=Path(__file__).resolve().parents[1]
main_rel=Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
gradle_rel=Path('app/build.gradle.kts')

def replace_once(path,old,new):
    text=path.read_text(encoding='utf-8')
    if old not in text: raise AssertionError('mutation anchor missing: '+old[:100])
    path.write_text(text.replace(old,new,1),encoding='utf-8')

def run(mutator,expect_fail=True):
    with tempfile.TemporaryDirectory(prefix='vs1006mut_') as td:
        dst=Path(td)/'root'
        shutil.copytree(root,dst,ignore=shutil.ignore_patterns('.gradle','build','.cxx'))
        mutator(dst)
        p=subprocess.run(['python3',str(dst/'tools/verify_driver_confirmation_network_ui_1006.py'),'--root',str(dst)],stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True)
        if expect_fail and p.returncode==0: raise AssertionError('mutation unexpectedly passed\n'+p.stdout)
        if not expect_fail and p.returncode!=0: raise AssertionError('false-positive control failed\n'+p.stdout)

mutations=[
    lambda d: replace_once(d/main_rel,'applyValidatedNetworkState(false)','applyValidatedNetworkState(validatedDefaultNetwork())'),
    lambda d: replace_once(d/main_rel,'applyValidatedNetworkState(hasValidatedInternetCapabilities(networkCapabilities))','applyValidatedNetworkState(validatedDefaultNetwork())'),
    lambda d: replace_once(d/main_rel,'pendingDriverModeConfirmation = mode\n            return','applyDriverModeChange(mode, false)\n            return'),
    lambda d: replace_once(d/main_rel,'pendingTurnipActivationSlot = slot','confirmManagedTurnipDriverActivation()'),
    lambda d: replace_once(d/main_rel,'CapabilitySectionCard("Turnip driver manager")','CapabilitySectionCard("Hidden Turnip manager")'),
    lambda d: replace_once(d/main_rel,'Driver changes are temporarily locked while VulkanScope is collecting a report.','Driver changes unavailable.'),
    lambda d: replace_once(d/main_rel,'contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 60.dp)','contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 0.dp)'),
    lambda d: replace_once(d/main_rel,'ExpressiveContainedIconTextButton("Close", R.drawable.ic_close)','ExpressiveTextButton("Close")'),
    lambda d: replace_once(d/main_rel,'DetailAffordance { onDetails(driver) }','ExpressiveTextButton("Details") { onDetails(driver) }'),
    lambda d: replace_once(d/main_rel,'ExpressiveContainedIconTextButton("Remove", R.drawable.ic_delete, enabled = enabled)','ExpressiveTextButton("Remove", enabled = enabled)'),
    lambda d: replace_once(d/main_rel,'This slot is active. Removing it will deactivate Turnip, switch VulkanScope to the System driver','This slot is active.'),
    lambda d: replace_once(d/gradle_rel,'versionCode = 1007','versionCode = 1005'),
]
for m in mutations: run(m,True)
run(lambda d: (d/'rules/1.0.6_DRIVER_CONFIRMATION_NETWORK_UI_AUDIT.md').write_text((d/'rules/1.0.6_DRIVER_CONFIRMATION_NETWORK_UI_AUDIT.md').read_text(encoding='utf-8')+'\nUnrelated audit wording.\n',encoding='utf-8'),False)
print(f'PASS 1.0.6 negative mutations: {len(mutations)} defects rejected + false-positive control')
