#!/usr/bin/env python3
import shutil,subprocess,sys,tempfile
from pathlib import Path
root=Path(__file__).resolve().parents[1]; verifier=root/'tools/verify_overview_tools_search_0803.py'
def mutate(label, rel, fn, expect=False):
  with tempfile.TemporaryDirectory(prefix='vs0803-neg-') as td:
    dst=Path(td)/'root'; shutil.copytree(root,dst,ignore=shutil.ignore_patterns('__pycache__','*.pyc'))
    p=dst/rel; s=p.read_text(); n=fn(s)
    if n==s: raise SystemExit(label+': mutation did not change source')
    p.write_text(n)
    r=subprocess.run([sys.executable,str(verifier),'--root',str(dst)],text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
    if (r.returncode==0)!=expect: raise SystemExit(label+' unexpected result\n'+r.stdout)
main=Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'); idx=Path('app/src/main/java/com/efishell/vulkanscope/VulkanSymbolIndex.kt'); gen=Path('tools/generate_encyclopedia_symbols.py')
mutate('re-embed analysis in Overview',main,lambda s:s.replace('    VulkanLazyPage(verticalSpacing = 14.dp) {','    val analysisModel = rememberAnalysisWorkspaceModel(report, device, display, driverMode)\n    VulkanLazyPage(verticalSpacing = 14.dp) {',1))
mutate('remove Encyclopedia route',main,lambda s:s.replace('        Page.Encyclopedia -> EncyclopediaPage()\n','',1))
mutate('restore double escaped generated separator',idx,lambda s:s.replace('vkAcquireDrmDisplayEXT\\tVulkan command\\t','vkAcquireDrmDisplayEXT\\\\tVulkan command\\\\t',1))
mutate('remove malformed decoder guard',idx,lambda s:s.replace('    if (first <= 0 || second <= first + 1) return null\n','',1))
mutate('remove malformed search-row guard',idx,lambda s:s.replace('            if (tab <= 0) continue\n','',1))
mutate('break generator delimiter',gen,lambda s:s.replace('esc(symbol)', 'esc(symbol + \"BROKEN\")',1))
mutate('unrelated Overview description',main,lambda s:s.replace('Search Vulkan terms, VK_* symbols, commands, types, extensions and VkResult meanings.','Search Vulkan terminology, VK_* symbols, commands, types, extensions and VkResult meanings.',1),expect=True)
print('PASS 0.80.3 negative mutations')
