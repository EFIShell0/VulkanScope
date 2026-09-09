#!/usr/bin/env python3
import argparse, re, xml.etree.ElementTree as ET
from pathlib import Path
parser=argparse.ArgumentParser(); parser.add_argument('--root',type=Path,default=Path(__file__).resolve().parents[1]); parser.add_argument('--skip-version',action='store_true'); parser.add_argument('--skip-release-records',action='store_true'); args=parser.parse_args()
root=args.root.resolve(); errors=[]
mainp=root/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'; gradlep=root/'app/build.gradle.kts'; manifestp=root/'app/src/main/AndroidManifest.xml'
for p in [mainp,gradlep,manifestp]:
    if not p.is_file(): errors.append(f'missing required file: {p.relative_to(root)}')
if errors: [print('FAIL:',e) for e in errors]; raise SystemExit(1)
main=mainp.read_text(encoding='utf-8'); gradle=gradlep.read_text(encoding='utf-8'); manifest=manifestp.read_text(encoding='utf-8')
def need(c,m):
    if not c: errors.append(m)
def block(a,b):
    x=main.find(a); y=main.find(b,x+len(a)) if x>=0 else -1; need(x>=0 and y>x,f'block missing: {a}'); return main[x:y] if x>=0 and y>x else ''
vm=re.search(r'versionName\s*=\s*"1\.0\.(\d+)"',gradle); cm=re.search(r'versionCode\s*=\s*(\d+)',gradle); minor=int(vm.group(1)) if vm else -1
if not args.skip_version: need(vm is not None and cm is not None and minor>=12 and int(cm.group(1))>=1012,'retained 1.0.12+ release identity missing')
need('kBaseline = "Vulkan 1.4.362"' in (root/'app/src/main/cpp/registry_query_catalog.h').read_text(encoding='utf-8'),'Vulkan baseline drifted')
need('android.permission.MANAGE_EXTERNAL_STORAGE' not in manifest and 'android.permission.MANAGE_EXTERNAL_STORAGE' not in main,'all-files access is forbidden')
if minor>=15:
    need('private inline fun tryLaunchSystemDocumentPicker(' in main,'1.0.15 superseding launch-first helper missing')
    need('private fun documentPickerAvailable(' not in main and 'PackageManager.MATCH_DEFAULT_ONLY' not in main,'superseded resolver preflight returned')
    need('tryLaunchSystemDocumentPicker { driverPickerLauncher.launch(' in main,'Turnip launch-first routing missing')
    need('tryLaunchSystemDocumentPicker { importLauncher.launch(' in main and 'tryLaunchSystemDocumentPicker { minimumImportLauncher.launch(' in main,'Analysis launch-first imports missing')
    need('tryLaunchSystemDocumentPicker { launcher.launch(snapshot.filename) }' in main,'complete report launch-first export missing')
else:
    picker=block('private fun documentPickerAvailable(','private fun analysisDocumentProviderAvailable')
    for t in ['PackageManager.MATCH_DEFAULT_ONLY','resolveActivity','return resolved?.activityInfo != null']: need(t in picker,f'legacy 1.0.12 picker-preflight contract missing: {t}')
helpers=block('private enum class FallbackAnalysisImportKind','private fun validateAnalysisSnapshot')
for t in ['FallbackAnalysisImportKind { SNAPSHOT, MINIMUM }','private fun scanAnalysisExchangeFiles(','getExternalFilesDirs(Environment.DIRECTORY_DOCUMENTS)','getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS)','File(context.filesDir, "analysis_exchange")','.take(64)','canonical.parentFile != root','canonical.name.startsWith("VulkanScope-")','canonical.name.endsWith(suffix, true)']: need(t in helpers,f'bounded Analysis fallback helper missing: {t}')
for bad in ['Environment.getExternalStorageDirectory()','File("/storage/emulated/0")']: need(bad not in helpers,f'broad Analysis fallback scan introduced: {bad}')
model=block('private fun rememberAnalysisWorkspaceModel(','private fun historyLabel')
for t in ['state.fallbackImportDialogData = data','importFallbackFile(kind: FallbackAnalysisImportKind, file: File)','writeAnalysisExchangeFile(context, "VulkanScope-${safeFilePart(device?.name ?: "Unknown-GPU")}-analysis.json"','writeAnalysisExchangeFile(context, "VulkanScope-${safeFilePart(name)}-minimum.json"','writeAnalysisExchangeFile(context, "VulkanScope-${safeFilePart(device?.name ?: "Unknown-GPU")}-technicalReport.json"']: need(t in model,f'Analysis fallback behavior missing: {t}')
need(model.count('android.widget.Toast.makeText(context, result.fold(')>=3,'all three Analysis fallback exports must report with a toast')
for t in ['analysis snapshot saved to ${it.absolutePath}','minimum profile saved to ${it.absolutePath}','technicalReport JSON saved to ${it.absolutePath}']: need(t in model,f'fallback export toast lacks exact path: {t}')
analysis_page=block('private fun AnalysisPage(','@Composable\nprivate fun VulkanPage'); need('FallbackAnalysisImportDialog(' in analysis_page,'Analysis fallback import dialog not rendered'); need('analysisModel.importFallbackFile(data.kind, file)' in analysis_page,'bounded selected file routing missing')
dialog=block('private fun FallbackAnalysisImportDialog(','@Composable\nprivate fun DriverOption')
for t in ['R.drawable.ic_action_import','color = VulkanTextPrimary','VulkanScope-*-analysis.json','VulkanScope-*-minimum.json','Scanned app-specific locations','Role.RadioButton','ExpressiveContainedIconTextButton("Import selected", R.drawable.ic_action_import']: need(t in dialog,f'Analysis fallback dialog requirement missing: {t}')
turnip=block('private fun FallbackTurnipImportDialog(','@Composable\nprivate fun FallbackAnalysisImportDialog')
need('R.drawable.ic_action_import' in turnip,'Fallback Turnip semantic import icon missing'); need('Text("Fallback Turnip import", color = VulkanTextPrimary' in turnip,'Fallback Turnip title color unreadable'); need('turnip_01.zip through turnip_10.zip' in turnip,'Turnip bounded fallback filename guidance missing')
analysis_ui=block('private fun LazyListScope.analysisWorkspaceItems(','@OptIn(ExperimentalMaterial3ExpressiveApi::class)\n@Composable\nprivate fun DetailAffordance')
for t in ['fallback selection dialog for bounded VulkanScope-*-analysis.json','fallback selection dialog for bounded VulkanScope-*-minimum.json','shows the exact saved path in a toast','VulkanScope-<GPU>-technicalReport.json']: need(t in analysis_ui,f'user-facing fallback guidance missing: {t}')
android=root/'app/src/main/res/drawable/ic_android.xml'; update=root/'app/src/main/res/drawable/ic_download_update.xml'
for p in [android,update]:
    need(p.is_file(),f'missing drawable: {p.name}')
    if p.is_file():
        try: ET.parse(p)
        except Exception as e: errors.append(f'invalid drawable XML {p.name}: {e}')
if android.is_file():
    x=android.read_text(encoding='utf-8'); need('android:viewportWidth="152"' in x and 'android:viewportHeight="89"' in x,'Android icon supplied-artwork viewport drifted'); need('M151.025,85.224' in x and 'M115.225,67.663' in x,'Android icon path data drifted'); need(('android:fillColor="#E2676A"' in x and 'android:fillColor="#351719"' in x) or ('android:fillColor="#34A853"' in x and 'android:fillColor="#202124"' in x),'Android icon palette invalid')
if update.is_file():
    x=update.read_text(encoding='utf-8'); need('M12,3v10' in x and 'M5,17h14' in x,'update/download glyph semantics drifted'); need('a8.5,8.5' not in x,'unwanted update outer circle returned')
if minor>=15:
    section=block('private fun SectionHeaderIcon(','@Composable\nprivate fun DisplaySectionBadgeIcon')
    need('sectionIcon == R.drawable.ic_android ->' in section,'Android supplied-artwork rendering missing')
    need('Image(' in section and 'contentScale = ContentScale.Fit' in section,'Android artwork flattened into generic tint')
else:
    section=block('private fun CapabilitySectionCard(','@Composable\nprivate fun CapabilityItemCard')
    need('if (sectionIcon == R.drawable.ic_android)' in section,'Android supplied-artwork rendering missing')
    need('Image(' in section and 'contentScale = ContentScale.Fit' in section,'Android artwork flattened into generic tint')
if not args.skip_release_records:
    rp=root/'rules/PROJECT_RULES.md'; need(rp.is_file() and '## Release 1.0.12 SAF fallback detection, fallback-dialog and supplied-icon requirements' in rp.read_text(encoding='utf-8'),'PROJECT_RULES 1.0.12 contract missing'); need((root/'rules/1.0.12_SAF_FALLBACK_ANDROID_ICON_AUDIT.md').is_file(),'1.0.12 audit missing'); need((root/'tests/golden/1.0.11_regression_contract.json').is_file(),'1.0.11 regression contract missing')
if errors: [print('FAIL:',e) for e in errors]; raise SystemExit(1)
print('PASS VulkanScope retained 1.0.12 bounded fallback / Android icon contract with 1.0.15 launch-first supersession')
