import subprocess,tempfile,shutil,sys
from pathlib import Path
root=Path(__file__).resolve().parents[1]; verifier=root/'tools/verify_html_presentation_04142.py'
mutations=[('.available{background:#182f52;color:#a9c9ff}', '.available{background:#133b28;color:#74e2a6}'),('lower.contains("unavailable") || lower == "not available" -> "unavailable"','lower.contains("unavailable") -> "unavailable"'),('CapabilityKeyValue("Registry report schema", registryCoverage.reportSchema)','CapabilityKeyValue("Report schema", registryCoverage.reportSchema)')]
source=root/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
for i,(a,b) in enumerate(mutations):
    with tempfile.TemporaryDirectory() as td:
        dst=Path(td)/'root'; shutil.copytree(root,dst); p=dst/source.relative_to(root); s=p.read_text(encoding='utf-8');
        if a not in s: raise SystemExit(f'mutation source missing {i}')
        p.write_text(s.replace(a,b,1),encoding='utf-8'); r=subprocess.run([sys.executable,str(dst/'tools/verify_html_presentation_04142.py')],stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True)
        if r.returncode==0: raise SystemExit(f'negative mutation {i} was not rejected')
print('PASS VulkanScope 0.41.42 HTML presentation negative mutations')
