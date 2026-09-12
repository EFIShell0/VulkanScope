#!/usr/bin/env python3
import argparse
import re
from pathlib import Path

p=argparse.ArgumentParser()
p.add_argument('--root', type=Path, default=Path(__file__).resolve().parents[1])
p.add_argument('--skip-version', action='store_true')
p.add_argument('--skip-release-records', action='store_true')
a=p.parse_args()
root=a.root.resolve()
main=(root/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
gradle=(root/'app/build.gradle.kts').read_text(encoding='utf-8')
errors=[]
def need(v,m):
    if not v: errors.append(m)
vm=re.search(r'versionName\s*=\s*"(\d+)\.(\d+)\.(\d+)"', gradle)
cv=tuple(map(int,vm.groups())) if vm else (0,0,0)
release_minor=cv[2] if cv[:2]==(1,0) else (99 if cv>=(1,1,0) else 0)
if not a.skip_version:
    cm=re.search(r'versionCode\s*=\s*(\d+)', gradle)
    expected=cv[0]*1000+cv[1]*100+cv[2]
    need(vm is not None and cm is not None and cv >= (1,0,14) and int(cm.group(1)) == expected, 'retained 1.0.14+ semantic release identity missing')
need('onImport: (File) -> Unit' in main, 'fallback analysis callback is no longer File-typed')
need('data.candidates.firstOrNull { it.file.absolutePath == selectedPath }?.file?.let(onImport)' in main, 'selected fallback candidate is not converted to File before File callback')
need('data.candidates.firstOrNull { it.file.absolutePath == selectedPath }?.let(onImport)' not in main, 'compile-breaking candidate-to-File callback call remains')
need('@Composable\nprivate fun UpdateSourceIcon()' in main, 'first-install update source icon helper missing')
expected_source_icon = 'R.drawable.ic_download' if cv >= (1,2,0) else ('R.drawable.ic_zip_download' if release_minor >= 17 else 'R.drawable.ic_action_github')
need(f'painter = painterResource({expected_source_icon})' in main, 'first-install update source glyph does not match the active release contract')
need('UpdateStatus.DirectUpdatesDisabledIntro -> { UpdateSourceIcon(); Text("Direct GitHub updates are currently disabled. Obtainium can manage updates externally, or direct updates can be enabled in Settings."' in main, 'DirectUpdatesDisabledIntro does not use semantic source icon')
need('UpdateStatus.DirectUpdatesDisabledIntro -> { UpdateStatusBadge("INFO")' not in main, 'generic INFO badge restored for first-install GitHub/Obtainium message')
need('kotlinx.coroutines.delay(7_000L)' in main, 'bounded first-install intro lifetime changed')
if not a.skip_release_records:
    rules=(root/'rules/PROJECT_RULES.md').read_text(encoding='utf-8')
    need('## Release 1.0.14 fallback-import compile and first-install update-intro requirements' in rules, 'PROJECT_RULES 1.0.14 contract missing')
    need((root/'rules/1.0.14_FALLBACK_IMPORT_COMPILE_INTRO_ICON_AUDIT.md').is_file(), '1.0.14 audit missing')
if errors:
    for e in errors: print('FAIL:',e)
    raise SystemExit(1)
print('PASS VulkanScope 1.0.14 fallback import compile / first-install update-intro contract')
