#!/usr/bin/env python3
import shutil
import subprocess
import tempfile
from pathlib import Path

root=Path(__file__).resolve().parents[1]
verifier=root/'tools/verify_turnip_connectivity_filter_dialog_1007.py'
main_rel=Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')

def run_mutation(name,old,new,should_fail=True,target=main_rel):
    with tempfile.TemporaryDirectory() as td:
        temp=Path(td)/'tree'
        shutil.copytree(root,temp)
        path=temp/target
        text=path.read_text(encoding='utf-8')
        if old not in text:
            raise AssertionError(f'{name}: mutation anchor missing')
        path.write_text(text.replace(old,new,1),encoding='utf-8')
        result=subprocess.run(['python3',str(temp/'tools/verify_turnip_connectivity_filter_dialog_1007.py'),'--root',str(temp)],stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True)
        if should_fail and result.returncode==0:
            raise AssertionError(f'{name}: verifier accepted defect')
        if not should_fail and result.returncode!=0:
            raise AssertionError(f'{name}: false-positive control failed\n{result.stdout}')

run_mutation('restore Turnip source selector','            CapabilitySectionCard("Turnip driver manager") {','            DriverOption(DriverMode.TURNIP, false, "Legacy Turnip selector", false) {}\n            CapabilitySectionCard("Turnip driver manager") {')
run_mutation('unsupported manager exposes metrics','TurnipSupport.UNSUPPORTED -> {','TurnipSupport.UNSUPPORTED -> {\n                        ExpressiveMetricGrid(emptyList())')
run_mutation('remove source availability','    val sourceAvailable: Boolean,\n','')
run_mutation('remove persisted SAF read permission','                runCatching { contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }\n','')
run_mutation('available ignores source','driver.info.installed && driver.sourceAvailable -> "AVAILABLE"','driver.info.installed -> "AVAILABLE"')
run_mutation('remove trash action icon','ExpressiveContainedIconTextButton("Remove", R.drawable.ic_delete','ExpressiveContainedTextButton("Remove"')
run_mutation('suppress network during collection','    NetworkStatusBanner(transitionState)','    if (collectionStatus == CollectionStatus.COLLECTING) return\n    NetworkStatusBanner(transitionState)')
run_mutation('remove offline collecting text','"Vulkan collection continues offline. Internet-dependent actions remain locked by network state, while report-dependent actions also remain locked until collection completes."','"Vulkan inspection stays available offline."')
run_mutation('remove combined database state','!completeReportReady && !networkAvailable -> "Waiting for complete Vulkan collection · internet unavailable"','!completeReportReady && !networkAvailable -> "Waiting for complete Vulkan collection"')
run_mutation('remove arrow animation','val leftVisualAlpha by animateFloatAsState(if (canMoveLeft) 1f else 0.42f, tween(220), label = "filterLeftAlpha")','val leftVisualAlpha = if (canMoveLeft) 1f else 0.42f')
run_mutation('remove carousel inset','contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 60.dp)','contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 0.dp)')
run_mutation('restore hard edge drawing','Box(modifier.fillMaxWidth().heightIn(min = 48.dp)) {','Box(modifier.fillMaxWidth().drawWithContent { drawContent() }.heightIn(min = 48.dp)) {')
run_mutation('remove Close X','ExpressiveContainedIconTextButton("Close", R.drawable.ic_close)','ExpressiveContainedTextButton("Close")')
run_mutation('remove Extension info icon','title.equals("Extension explorer", true) -> R.drawable.ic_info','title.equals("Extension explorer", true) -> R.drawable.ic_extensions')
run_mutation('unrelated changelog wording','## 1.0.6','## 1.0.6 retained history',should_fail=False,target=Path('changelog.md'))
print('PASS VulkanScope 1.0.7 negative mutations and false-positive control')
