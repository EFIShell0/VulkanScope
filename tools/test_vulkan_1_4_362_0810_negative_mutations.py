#!/usr/bin/env python3
import shutil,subprocess,sys,tempfile
from pathlib import Path
root=Path(__file__).resolve().parents[1]
ver=root/'tools/verify_vulkan_1_4_362_0810.py'
mutations=[
('registry/registry_lock.json','"registryRef": "1.4.362"','"registryRef": "1.4.361"','registry-ref-drift'),
('app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt','"VK_VALVE_buffer_device_address_allocation_alignment",','', 'valve-provider-removal'),
('app/src/main/cpp/runtime_extension_pnext_parity.inc','"VK_KHR_pipeline_library_group_handles"','"BROKEN_KHR_pipeline_library_group_handles"','khr-alias-removal'),
('app/src/main/cpp/extension_field_coverage_parity.inc','            generatedEmitNumeric(dst, section, "maxBufferDeviceAddressAllocationAlignment", value.maxBufferDeviceAddressAllocationAlignment);','            generatedEmitNumeric(dst, section, "maxAllocationAlignmentBroken", value.maxAllocationAlignmentBroken);','valve-property-report-removal'),
('app/src/main/java/com/efishell/vulkanscope/VulkanSymbolIndex.kt','const val VULKAN_TOKEN_SYMBOL_COUNT = 6248','const val VULKAN_TOKEN_SYMBOL_COUNT = 6247','encyclopedia-census-drift')]
for rel,a,b,name in mutations:
    with tempfile.TemporaryDirectory(prefix='vs0810-mut-') as d:
        dst=Path(d)/'root';shutil.copytree(root,dst)
        p=dst/rel;s=p.read_text(encoding='utf-8')
        if a not in s:raise SystemExit('FAIL mutation source absent '+name)
        p.write_text(s.replace(a,b,1),encoding='utf-8')
        r=subprocess.run([sys.executable,str(ver),'--root',str(dst),'--skip-version'],stdout=subprocess.DEVNULL,stderr=subprocess.DEVNULL)
        if r.returncode==0:raise SystemExit('FAIL mutation accepted '+name)
with tempfile.TemporaryDirectory(prefix='vs0810-fp-') as d:
    dst=Path(d)/'root';shutil.copytree(root,dst)
    p=dst/'changelog.md';s=p.read_text(encoding='utf-8')
    needle='Updates the locked Vulkan registry and Vulkan-Headers baseline'
    if needle not in s:raise SystemExit('FAIL false-positive source absent')
    p.write_text(s.replace(needle,needle+' ',1),encoding='utf-8')
    r=subprocess.run([sys.executable,str(ver),'--root',str(dst),'--skip-version'],stdout=subprocess.DEVNULL,stderr=subprocess.DEVNULL)
    if r.returncode!=0:raise SystemExit('FAIL false-positive wording mutation rejected')
print('PASS 0.80.10 Vulkan 1.4.362 negative mutations and false-positive control')
