#!/usr/bin/env python3
import shutil, subprocess, sys, tempfile
from pathlib import Path
root=Path(__file__).resolve().parents[1]; main_rel=Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
def rep(p,a,b):
    s=p.read_text(encoding='utf-8')
    if a not in s: raise AssertionError('mutation anchor missing: '+a[:140])
    p.write_text(s.replace(a,b,1),encoding='utf-8')
def case(name,fn,fail=True):
    with tempfile.TemporaryDirectory(prefix='vs1012-ret-mut-') as t:
        d=Path(t)/'p'; shutil.copytree(root,d); fn(d); r=subprocess.run([sys.executable,str(d/'tools/verify_saf_fallback_android_icon_1012.py'),'--root',str(d)],stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True)
        if (r.returncode!=0)!=fail: print(r.stdout); raise AssertionError(f'{name}: expected fail={fail}')
m=[
('remove launch-first Turnip',lambda d: rep(d/main_rel,'tryLaunchSystemDocumentPicker { driverPickerLauncher.launch(','tryLaunchSystemDocumentPickerDisabled { driverPickerLauncher.launch(')),
('remove analysis fallback dialog',lambda d: rep(d/main_rel,'private fun FallbackAnalysisImportDialog(','private fun MissingFallbackAnalysisImportDialog(')),
('broaden analysis scan',lambda d: rep(d/main_rel,'roots += File(context.filesDir, "analysis_exchange")','roots += Environment.getExternalStorageDirectory()')),
('remove exact export path',lambda d: rep(d/main_rel,'technicalReport JSON saved to ${it.absolutePath}','technicalReport saved')),
('unreadable Turnip title',lambda d: rep(d/main_rel,'Text("Fallback Turnip import", color = VulkanTextPrimary','Text("Fallback Turnip import", color = ComposeColor.Black')),
('remove Turnip semantic icon',lambda d: rep(d/main_rel,'Icon(painterResource(R.drawable.ic_action_import), contentDescription = null, tint = VulkanAccentSoft','Icon(painterResource(R.drawable.ic_info), contentDescription = null, tint = VulkanAccentSoft')),
('damage Android geometry',lambda d: rep(d/'app/src/main/res/drawable/ic_android.xml','M151.025,85.224','M150.025,85.224')),
('restore update outer circle',lambda d: rep(d/'app/src/main/res/drawable/ic_download_update.xml','M12,3v10','M12,3.5a8.5,8.5 0,1 0,7.8 5.1M12,3v10')),
('flatten Android artwork',lambda d: rep(d/main_rel,'sectionIcon == R.drawable.ic_android ->','false ->')),
('stale version',lambda d: rep(d/'app/build.gradle.kts','versionCode = 1018','versionCode = 1011')),
]
for n,f in m: case(n,f,True)
case('unrelated changelog',lambda d:(d/'changelog.md').write_text((d/'changelog.md').read_text(encoding='utf-8')+'\nUnrelated wording.\n',encoding='utf-8'),False)
print(f'PASS VulkanScope retained 1.0.12 negative mutations: {len(m)} defects rejected + false-positive control')
