#!/usr/bin/env python3
import shutil, subprocess, sys, tempfile
from pathlib import Path
root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_saf_launch_first_full_audit_1015.py'
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')

def replace_once(path, old, new):
    text = path.read_text(encoding='utf-8')
    if old not in text: raise AssertionError('mutation anchor missing: ' + old[:160])
    path.write_text(text.replace(old,new,1),encoding='utf-8')

def run_case(name, mutate, fail=True):
    with tempfile.TemporaryDirectory(prefix='vs1015-mut-') as tmp:
        d=Path(tmp)/'project'; shutil.copytree(root,d); mutate(d)
        r=subprocess.run([sys.executable,str(d/'tools/verify_saf_launch_first_full_audit_1015.py'),'--root',str(d)],stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True)
        if (r.returncode != 0) != fail:
            print(r.stdout); raise AssertionError(f'{name}: expected fail={fail}, got {r.returncode != 0}')

muts=[
('restore resolver preflight', lambda d: replace_once(d/main_rel, 'private inline fun tryLaunchSystemDocumentPicker(launch: () -> Unit): Boolean {', 'private fun documentPickerAvailable() = PackageManager.MATCH_DEFAULT_ONLY != 0\nprivate inline fun tryLaunchSystemDocumentPicker(launch: () -> Unit): Boolean {')),
('restore TV guess', lambda d: replace_once(d/main_rel, 'return try {\n        launch()', 'if (isTvDevice(context)) return false\n    return try {\n        launch()')),
('Turnip bypass launch-first', lambda d: replace_once(d/main_rel, 'if (!tryLaunchSystemDocumentPicker { driverPickerLauncher.launch(arrayOf("application/zip", "application/octet-stream", "*/*")) }) {', 'if (true) {')),
('Analysis bypass launch-first', lambda d: replace_once(d/main_rel, 'if (!tryLaunchSystemDocumentPicker { importLauncher.launch(arrayOf("application/json", "text/plain")) })', 'if (true)')),
('report bypass launch-first', lambda d: replace_once(d/main_rel, 'if (!tryLaunchSystemDocumentPicker { launcher.launch(snapshot.filename) })', 'if (true)')),
('swallow broad exception', lambda d: replace_once(d/main_rel, 'catch (error: SecurityException)', 'catch (error: Throwable)')),
('false SAF unavailable wording', lambda d: replace_once(d/main_rel, 'The system document picker could not be opened.', "Android's Storage Access Framework is unavailable.")),
('broaden analysis scan', lambda d: replace_once(d/main_rel, 'roots += File(context.filesDir, "analysis_exchange")', 'roots += Environment.getExternalStorageDirectory()')),
('remove exact fallback toast', lambda d: replace_once(d/main_rel, 'Document picker fallback · technicalReport JSON saved to ${it.absolutePath}', 'technicalReport saved')),
('omit retained 1.0.14 quality gate', lambda d: replace_once(d/'tools/quality_gate.py', "run([sys.executable, 'tools/verify_fallback_import_compile_intro_1014.py'])", "print('skip 1014')")),
('omit 1.0.15 quality gate', lambda d: replace_once(d/'tools/quality_gate.py', "run([sys.executable, 'tools/verify_saf_launch_first_full_audit_1015.py'])", "print('skip 1015')")),
('stale version', lambda d: replace_once(d/'app/build.gradle.kts','versionCode = 1018','versionCode = 1014')),
]
for n,m in muts: run_case(n,m,True)
run_case('unrelated changelog wording', lambda d: (d/'changelog.md').write_text((d/'changelog.md').read_text(encoding='utf-8')+'\nUnrelated wording.\n',encoding='utf-8'), False)
print(f'PASS VulkanScope 1.0.15 negative mutations: {len(muts)} defects rejected + false-positive control')
