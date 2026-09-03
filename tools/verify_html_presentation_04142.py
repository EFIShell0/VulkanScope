import argparse
from pathlib import Path
p=argparse.ArgumentParser();p.add_argument('--root');a=p.parse_args();root=Path(a.root).resolve() if a.root else Path(__file__).resolve().parents[1]
kt=(root/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
errors=[]
def need(c,m):
    if not c: errors.append(m)
need('.yes{background:#133b28;color:#74e2a6}.available{background:#182f52;color:#a9c9ff}' in kt,'Supported and Available HTML states are not visually distinct')
need('lower.contains("unavailable") || lower == "not available" -> "unavailable"' in kt,'Not available is not routed to Unavailable')
need('lower == "not available" || lower == "not exposed" -> "no"' not in kt,'Not available is still classified as Unsupported')
need('CapabilityKeyValue("Registry report schema", registryCoverage.reportSchema)' in kt,'registry report schema UI label missing')
need('appendLine("Registry report schema=${report.registryCoverage.reportSchema}")' in kt,'registry report schema TXT label missing')
need('"Registry report schema" to htmlEscape(report.registryCoverage.reportSchema)' in kt,'registry report schema HTML label missing')
need('CapabilityKeyValue("Report schema", registryCoverage.reportSchema)' not in kt and '"Report schema" to htmlEscape(report.registryCoverage.reportSchema)' not in kt,'ambiguous registry Report schema label remains')
if errors:
    [print('FAIL:',x) for x in errors]; raise SystemExit(1)
print('PASS VulkanScope 0.41.42 HTML presentation contract')
