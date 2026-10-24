from pathlib import Path
import hashlib
import json
import re
import sys
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
KOTLIN = ROOT / 'app/src/main/java/com/efishell/vulkanscope'
ERRORS = []


def check(condition, failure):
    if not condition:
        ERRORS.append(failure)


def source(name):
    return (ROOT/name).read_text(encoding='utf-8')


lock = json.loads(source('registry/registry_lock.json'))
xml = ROOT/'registry/upstream/vk.xml'
check(xml.is_file(), 'canonical registry XML is missing')
if xml.is_file():
    check(hashlib.sha256(xml.read_bytes()).hexdigest() == lock['registrySha256'], 'vk.xml hash does not match lock')
    root = ET.parse(xml).getroot()
    header = [n for n in root.findall('./types/type') if n.get('api') == 'vulkan,vulkanbase' and 'VK_HEADER_VERSION' in ''.join(n.itertext())]
    check(any(re.search(r'VK_HEADER_VERSION\s*</name>\s*366\b', ET.tostring(n,encoding='unicode')) for n in header), 'registry does not declare Vulkan header version 366')
check(lock['headerVersion'] == 366 and lock['headerTag'] == 'v1.4.366', 'header version/tag mismatch')
check(lock['apiBaseline'] == 'Vulkan 1.4.366', 'registry API baseline mismatch')
cmake=source('app/src/main/cpp/CMakeLists.txt')
check('GIT_TAG v1.4.366' in cmake, 'CMake header tag not updated')
check('VK_HEADER_VERSION[ \\t]+366' in cmake, 'CMake header semantic guard missing')
check(lock['registrySha256'] in cmake, 'CMake canonical XML hash guard missing')
manifest=json.loads(source('registry/generated/registry_query_manifest.json'))
coverage=json.loads(source('registry/generated/coverage_snapshot.json'))
check(manifest['registrySha256'] == lock['registrySha256'] and manifest['headerVersion'] == 366,'generated query manifest stale')
check(coverage['baseline']=='Vulkan 1.4.366' and coverage['registrySha256']==lock['registrySha256'], 'coverage manifest stale')
check(coverage['validatedPhysicalDeviceQueryExtensionCount'] == 305,'validated extension count unexpectedly changed')
check(coverage['registryQueryableExtensionCount'] == 307 and len(coverage['newQueryableExtensionProvidersPendingNativeGeneration'])==2,'new queryable extension coverage gap not disclosed')
ref=json.loads(source('registry/generated/extension_reference.json'))
check('1.4.366' in json.dumps(ref) and len(ref['entries'])>=470,'extension reference not regenerated')
symbol=source('app/src/main/java/com/efishell/vulkanscope/VulkanSymbolIndex.kt')
check('VulkanRegistrySymbol' in symbol and len(symbol)>200000,'symbol index not regenerated')
check('1.4.366' in source('app/src/main/cpp/registry_query_catalog.h'), 'native catalog baseline stale')
check('1.4.364' not in source('app/src/main/cpp/vulkanscope.cpp'), 'native JSON still reports old Vulkan baseline')
check('364' not in source('app/src/main/cpp/CMakeLists.txt'), 'CMake has old header check')
app=source('app/build.gradle.kts');build=source('build.gradle.kts');wrapper=source('gradle/wrapper/gradle-wrapper.properties')
for needle in ('val releaseVersionName = "4.0.2"','versionName = releaseVersionName','versionCode = 4002','targetSdk = 37','minorApiLevel = 2','isUniversalApk = true','"arm64-v8a", "x86_64"'):
    check(needle in app, f'missing Android config: {needle}')
check('armeabi-v7a' not in app,'ARMv7 is still in Android build configuration')
check('9.4.1' in build,'AGP pin mismatch')
check('2.4.21' in build,'Kotlin pin mismatch')
check('gradle-9.8.1-bin.zip' in wrapper,'Gradle wrapper pin mismatch')
check('org.jetbrains.kotlin.android' not in build and 'kotlin-android' not in build,'Kotlin Android plugin should not be used with AGP 9')
activity=source('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
check(len(activity.splitlines())<5000,'MainActivity was not split into cohesive files')
files=['MainActivity','CoreModels','AnalysisEntry','AnalysisDomain','AnalysisScreens','UiChrome','UiOverview','UiDeviceScreens','UiExtensions','UiReports','UiProfiles','UiSettings','UiEncyclopedia','UiFormatsAndProperties','UiComponents']
check(all((KOTLIN/(f+'.kt')).is_file() for f in files),'missing one or more split Kotlin modules')
reports=source('app/src/main/java/com/efishell/vulkanscope/UiReports.kt')
analysis=source('app/src/main/java/com/efishell/vulkanscope/AnalysisScreens.kt')
domain=source('app/src/main/java/com/efishell/vulkanscope/AnalysisDomain.kt')
extensions=source('app/src/main/java/com/efishell/vulkanscope/UiExtensions.kt')
components=source('app/src/main/java/com/efishell/vulkanscope/UiComponents.kt')
check(all(x in reports for x in ('"Export TXT"','"Export HTML"','"Export JSON"')), 'unified report exports missing')
check('prepareReportStorageExport("application/json")' in reports,'JSON report action not wired to export')
check('mime in setOf("text/plain", "text/html", "application/json")' in extensions,'JSON snapshot MIME guard missing')
check('Export technicalReport JSON' not in analysis,'duplicate Analysis technical report JSON export not removed')
check('"Import analysis snapshot"' in analysis,'analysis snapshot import removed')
check('"Saved minimums · Custom profile files"' in analysis,'minimums top card missing')
check(all(x in analysis for x in ('"Import custom profile"','"Export custom profile"','"Saved minimum profiles"')), 'minimums import/export or saved cards missing')
check('wholeRowClickable = true' in reports and 'Modifier.clickable(onClick = onClick)' in components,'database submit hit target not repaired')
check('if (wholeRowClickable) 24.dp else 19.dp' in components,'database arrow icon not enlarged')
check('val score = if (assessed.isEmpty()) null else ((100.0 * passed / assessed.size) + 0.5)' in domain,'assessment-percent formula changed')
check('val assessed = checks.filter { it.applicable }' in domain,'N/A excluded from scoring requirement changed')
check('"NOT APPLICABLE"' in analysis and '"ASSESSED"' in analysis,'Quality UI breakdown missing')
check('internal suspend fun createExportSnapshot' in extensions,'cross-file report export helper remains inaccessible')
readme=source('README.md')
for filename in ['ARCHITECTURE.md','BUILD.md','VULKAN_REGISTRY.md','ANALYSIS_SCORING.md','REPORT_EXPORTS.md','QA.md','RELEASE_4_0_2.md']:
    check((ROOT/'docs'/filename).is_file() and ('docs/'+filename) in readme,'README docs link broken: '+filename)
check('# VulkanScope 4.0.2 authoritative engineering policy' in source('rules/PROJECT_RULES.md'),'rules 4.0.2 policy missing')

for f in ('AdaptiveLazyLayout.kt','AdaptiveDialogLayout.kt'):
    check((KOTLIN/f).is_file(),f'adaptive layout file absent: {f}')
components = source('app/src/main/java/com/efishell/vulkanscope/UiComponents.kt')
reports = source('app/src/main/java/com/efishell/vulkanscope/UiReports.kt')
chrome = source('app/src/main/java/com/efishell/vulkanscope/UiChrome.kt')
check('content = adaptiveContent' in chrome and 'AdaptiveLazyListRecorder' in chrome,'page list is not adaptive')
check('DatabaseSubmissionStatusCard(' in reports and 'Ready to submit' in reports and 'Connection unavailable' in reports,'database status states missing')
check('ComposeColor(0xFFF8F9FC)' in components,'Markdown white text style missing')
check('Khronos Group logo are registered trademarks' in reports and 'Vulkan logo are registered trademarks' in reports,'Khronos logo attribution missing')
check(not list((ROOT/'rules').glob('[0-9]*.md')),'stale audit markdown shipped')
check('val releaseVersionName = "4.0.2"' in app, 'release version name constant absent')
check('output.outputFileName.set("vulkanscope-$releaseVersionName-$abiName.apk")' in app, 'release output naming missing')
check('onVariants(selector().withBuildType("release"))' in app, 'release output selector missing')
check('FilterConfiguration.FilterType.ABI' in app, 'ABI-specific APK naming missing')
check('?: "universal"' in app, 'universal APK name missing')
check('applicationVariants' not in app and 'BaseVariantOutputImpl' not in app, 'legacy output API used')
check('AnimatedSettingsSectionCard(cardsVisible, index, section, onSelected)' in reports, 'Settings cards must use a separate animation composable')
check('private fun AnimatedSettingsSectionCard(' in reports, 'dedicated scope-safe animation composable missing')
scoped = reports.split('internal fun SettingsSectionCards(', 1)[1].split('private fun AnimatedSettingsSectionCard(', 1)[0]
check('AnimatedVisibility(' not in scoped, 'RowScope AnimatedVisibility still called inside SettingsSectionCards')
if ERRORS:
    print('FAIL VulkanScope 4.0.2 static verification:', *ERRORS, sep='\n - ')
    sys.exit(1)
print('PASS VulkanScope 4.0.2: registry, Kotlin scoped animation and three APK naming contracts; static only, Android build NOT VERIFIED')
