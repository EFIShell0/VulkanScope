#!/usr/bin/env python3
import argparse
import re
from pathlib import Path

parser=argparse.ArgumentParser()
parser.add_argument('--root',type=Path,default=Path(__file__).resolve().parents[1])
parser.add_argument('--skip-version',action='store_true')
args=parser.parse_args(); root=args.root.resolve(); errors=[]
main_path=root/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
advanced_path=root/'app/src/main/java/com/efishell/vulkanscope/AdvancedAnalysis.kt'
main=main_path.read_text(encoding='utf-8')
advanced=advanced_path.read_text(encoding='utf-8')
gradle=(root/'app/build.gradle.kts').read_text(encoding='utf-8')
root_gradle=(root/'build.gradle.kts').read_text(encoding='utf-8')
rules=(root/'rules/PROJECT_RULES.md').read_text(encoding='utf-8')

def need(value,message):
    if not value: errors.append(message)

version_match = re.search(r'versionName\s*=\s*"(\d+)\.(\d+)\.(\d+)"', gradle)
current_version = tuple(map(int, version_match.groups())) if version_match else (0, 0, 0)
if not args.skip_version:
    code_match = re.search(r'versionCode\s*=\s*(\d+)', gradle)
    expected_code = None if version_match is None else current_version[0] * 1000 + current_version[1] * 100 + current_version[2]
    need(version_match is not None and code_match is not None and current_version >= (1, 0, 1) and int(code_match.group(1)) == expected_code, 'release identity is not a retained 1.0.1+ semantic identity')
need('kBaseline = "Vulkan 1.4.362"' in (root/'app/src/main/cpp/registry_query_catalog.h').read_text(encoding='utf-8'),'Vulkan registry/query baseline is not 1.4.362')
need('id("com.android.application") version "9.4.0" apply false' in root_gradle,'AGP stable baseline is not 9.4.0')
need('implementation("androidx.compose.foundation:foundation:1.12.0")' in gradle,'Compose Foundation is not 1.12.0')
need('implementation("androidx.compose.material3:material3:1.5.0-alpha27")' in gradle,'Material 3 Expressive dependency is not 1.5.0-alpha27')
need('import java.math.BigDecimal' in advanced,'exact decimal minimum comparison is missing BigDecimal')
minimum_start=advanced.find('internal fun evaluateCustomMinimumRule')
minimum_end=advanced.find('\ninternal fun flattenJson',minimum_start)
minimum=advanced[minimum_start:minimum_end]
need(minimum_start>=0 and minimum_end>minimum_start,'custom minimum evaluator missing')
need('.toDoubleOrNull()' not in minimum,'custom minimum evaluator still uses lossy Double comparison')
for token in ['customMinimumMatches','Feature expectation must be true/supported/yes/1 or false/unsupported/no/0','Feature name is ambiguous','Limit name is ambiguous','BigDecimal(numeric)','actual.compareTo(expected)']:
    need(token in minimum or token in advanced,f'custom minimum correctness contract missing: {token}')
need('saveAnalysisHistoryRecord(context: Context' in advanced and 'AnalysisHistoryRecord? = runCatching {' in advanced and '}.getOrNull()' in advanced,'history persistence is not fail-closed around storage I/O')
need('loadAnalysisHistoryRecords(context: Context): List<AnalysisHistoryRecord> = runCatching {' in advanced and '}.getOrDefault(emptyList())' in advanced,'history load is not fail-closed')
need('deleteAnalysisHistoryRecord(context: Context, id: String): Boolean' in advanced and 'return runCatching {' in advanced,'history delete is not fail-closed')
for token in ['Unknown · runtime device API evidence is unavailable or unparsable','"available" -> "Not enumerated by completed $scope extension evidence"','"incomplete" -> "Unknown · $scope extension enumeration is incomplete"','"unavailable" -> "Unknown · $scope extension enumeration is unavailable','"not_applicable" -> "Not applicable · $scope extension enumeration does not apply"']:
    need(token in main,f'dependency evidence authority contract missing: {token}')
need('technicalLeavesNeeded = state.tab == 8 || state.tab == 9' in main,'technicalReport JSON leaves are not gated to Raw JSON/Database tabs')
need('val currentTechnical = remember' not in main,'technicalReport JSON is still eagerly materialized on every Analysis tab')
need('technicalReportJson(context, report, display, mode).toString(2)' in main,'raw JSON export is not lazily generated on explicit export')
need('val resolvedWatch = onWatch ?: environment?.addWatch' in main,'evidence watch callback is not resolved to one writer')
need('onWatch(referenceToken); environment?.addWatch?.invoke(referenceToken)' not in main,'evidence watch action still writes twice')
filter_start=main.find('private fun ExpressiveFilterBar')
filter_end=main.find('\n@Composable\nprivate fun ExpressiveToggleRow',filter_start)
filter_block=main[filter_start:filter_end]
is_1002_or_newer = current_version >= (1, 0, 2)
if is_1002_or_newer:
    need('ExpressiveFilterCarousel(' in filter_block,'shared filter bar does not route through the 1.0.2 expressive carousel')
    carousel_start=main.find('private fun ExpressiveFilterCarousel')
    carousel_end=main.find('\n@Composable\nprivate fun ExpressiveFilterBar',carousel_start)
    carousel_block=main[carousel_start:carousel_end]
    need('LazyRow(' in carousel_block and 'canScrollBackward' in carousel_block and 'canScrollForward' in carousel_block,'1.0.2 carousel does not expose bounded scrolling')
    need('Scroll filters left' in carousel_block and 'Scroll filters right' in carousel_block and 'LocalLayoutDirection' in carousel_block,'1.0.2 carousel accessibility/RTL contract missing')
    need('horizontalScroll' not in filter_block and 'horizontalScroll' not in carousel_block,'shared filter bar still hides destinations in an unmanaged horizontal strip')
else:
    need('FlowRow(' in filter_block,'shared filter bar is not responsive FlowRow')
    need('horizontalScroll' not in filter_block,'shared filter bar still hides destinations in horizontal scrolling')
need(('The base collector does not publish elapsed time for every Vulkan query' in main or 'The base collector does not publish elapsed time for every Vulkan® query' in main) and 'dedicated or on-demand probes display measured app-side elapsed time' in main,'diagnostics timing explanation is inaccurate')
need('Current session could not be persisted because local storage was unavailable or the bounded history size was exceeded' in main,'history failure UI still claims only a size-limit cause')
need('## Release 1.0.1 full security, specification, correctness and usability audit requirements' in rules,'PROJECT_RULES 1.0.1 contract missing')
need((root/'rules/1.0.1_FULL_SECURITY_SPEC_CORRECTNESS_USABILITY_AUDIT.md').is_file(),'1.0.1 audit file missing')
need((root/'tests/golden/1.0.0_regression_contract.json').is_file(),'1.0.0 predecessor regression contract missing')
if errors:
    for error in errors: print('FAIL:',error)
    raise SystemExit(1)
print('PASS VulkanScope 1.0.1 full security/spec/correctness/usability contract')
