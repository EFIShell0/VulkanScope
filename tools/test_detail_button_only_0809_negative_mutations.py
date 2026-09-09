#!/usr/bin/env python3
import shutil,subprocess,sys,tempfile
from pathlib import Path
root=Path(__file__).resolve().parents[1]
ver=root/'tools/verify_detail_button_only_0809.py'
mainrel='app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
mutations=[
(mainrel,'itemsIndexed(filtered, key = { index, format -> "format:${format.name}:$index" }) { _, format ->\n            CapabilityItemCard(containerColor = VulkanSurfaceRaised) {','itemsIndexed(filtered, key = { index, format -> "format:${format.name}:$index" }) { _, format ->\n            Card(onClick = { selected = format }, colors = CardDefaults.cardColors(containerColor = VulkanSurfaceRaised)) {','format-card-action'),
(mainrel,'DetailAffordance { selectedSupported = extension }','DetailAffordance { Unit }','supported-details-action'),
(mainrel,'DetailAffordance { selectedCatalog = name }','DetailAffordance { Unit }','catalog-details-action'),
(mainrel,'private fun DetailAffordance(onClick: () -> Unit)','private fun DetailAffordance()','explicit-details-contract')]
for rel,a,b,name in mutations:
    with tempfile.TemporaryDirectory(prefix='vs0809-mut-') as d:
        dst=Path(d)/'root';shutil.copytree(root,dst)
        p=dst/rel;s=p.read_text(encoding='utf-8')
        if a not in s:raise SystemExit('FAIL mutation source absent '+name)
        p.write_text(s.replace(a,b,1),encoding='utf-8')
        r=subprocess.run([sys.executable,str(ver),'--root',str(dst)],stdout=subprocess.DEVNULL,stderr=subprocess.DEVNULL)
        if r.returncode==0:raise SystemExit('FAIL mutation accepted '+name)
with tempfile.TemporaryDirectory(prefix='vs0809-fp-') as d:
    dst=Path(d)/'root';shutil.copytree(root,dst)
    p=dst/mainrel;s=p.read_text(encoding='utf-8')
    needle='Use Details for full decoded/raw format evidence.'
    if needle not in s:raise SystemExit('FAIL false-positive source absent')
    p.write_text(s.replace(needle,needle+' ',1),encoding='utf-8')
    r=subprocess.run([sys.executable,str(ver),'--root',str(dst)],stdout=subprocess.DEVNULL,stderr=subprocess.DEVNULL)
    if r.returncode!=0:raise SystemExit('FAIL false-positive descriptive mutation rejected')
print('PASS 0.80.9 detail-button-only negative mutations and false-positive control')
