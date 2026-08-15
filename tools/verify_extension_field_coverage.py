import json
import re
import sys
import argparse
from pathlib import Path

root = Path(__file__).resolve().parents[1]
ap = argparse.ArgumentParser()
ap.add_argument('--header', required=True)
args = ap.parse_args()
header_path = Path(args.header)
header = header_path.read_text()
beta_path = header_path.with_name('vulkan_beta.h')
if beta_path.exists():
    header += '\n' + beta_path.read_text()
catalog = (root / 'app/src/main/cpp/registry_query_catalog.h').read_text()
generated = (root / 'app/src/main/cpp/extension_field_coverage_generated.inc').read_text()
structs = re.findall(r'typedef\s+struct\s+(VkPhysicalDevice\w*)\s*\{(.*?)\}\s*\1\s*;', header, re.S)
macros = dict(re.findall(r'#define\s+(VK_STRUCTURE_TYPE_[A-Z0-9_]+)\s+([-0-9]+)', header))

def to_macro(name):
    s=name[2:].replace('Vulkan11','Vulkan_1_1').replace('Vulkan12','Vulkan_1_2').replace('Vulkan13','Vulkan_1_3').replace('Vulkan14','Vulkan_1_4')
    s=re.sub(r'([a-z0-9])([A-Z])',r'\1_\2',s)
    s=re.sub(r'([A-Za-z])([0-9])',r'\1_\2',s)
    s=re.sub(r'([0-9])([A-Za-z])',r'\1_\2',s)
    return 'VK_STRUCTURE_TYPE_'+s.upper()


dups={}
for name,body in structs:
    if name in {'VkPhysicalDeviceMemoryProperties','VkPhysicalDeviceFeatures','VkPhysicalDeviceProperties2','VkPhysicalDeviceFeatures2','VkPhysicalDeviceSparseProperties','VkPhysicalDeviceLimits'}: continue
    if 'pNext' not in body: continue
    macro=to_macro(name)
    if name=='VkPhysicalDeviceTextureCompressionASTC3DFeaturesEXT': macro='VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_TEXTURE_COMPRESSION_ASTC_3D_FEATURES_EXT'
    if macro in macros: dups.setdefault(macros[macro],[]).append(name)
serializable=[name for name,body in structs if 'pNext' in body and name not in {'VkPhysicalDeviceProperties2','VkPhysicalDeviceFeatures2','VkPhysicalDeviceMemoryProperties','VkPhysicalDeviceFeatures','VkPhysicalDeviceSparseProperties','VkPhysicalDeviceLimits'}]
serializable=[n for n in serializable if len(dups.get(macros.get(to_macro(n)),[]))<=1 or not macros.get(to_macro(n))]
case_count=generated.count('case VK_STRUCTURE_TYPE_')
implemented=int(re.search(r'kImplementedPhysicalDeviceStructCount\s*=\s*(\d+)',catalog).group(1))
if case_count != len(serializable):
    print(f'FAIL serializer coverage: generated={case_count} expected={len(serializable)}')
    sys.exit(1)
if implemented < case_count:
    print(f'FAIL serializer count exceeds implemented catalog: {case_count}>{implemented}')
    sys.exit(1)
print(f'PASS generated pNext serializers={case_count}; implemented structs={implemented}; duplicate-sType structs excluded safely')
