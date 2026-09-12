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
gradle=(root/'app/build.gradle.kts').read_text(encoding='utf-8')
main=main_path.read_text(encoding='utf-8')
advanced=advanced_path.read_text(encoding='utf-8') if advanced_path.is_file() else ''
rules=(root/'rules/PROJECT_RULES.md').read_text(encoding='utf-8')

def need(condition,message):
    if not condition: errors.append(message)

def has_all(text,tokens,label):
    for token in tokens: need(token in text,f'{label} missing: {token}')

if not args.skip_version:
    version_match = re.search(r'versionName\s*=\s*"(\d+)\.(\d+)\.(\d+)"', gradle)
    code_match = re.search(r'versionCode\s*=\s*(\d+)', gradle)
    expected_code = None if version_match is None else int(version_match.group(1)) * 1000 + int(version_match.group(2)) * 100 + int(version_match.group(3))
    need(version_match is not None and code_match is not None and int(version_match.group(1)) >= 1 and int(code_match.group(1)) == expected_code, 'release identity is not a retained 1.x semantic identity')

need(advanced_path.is_file(),'AdvancedAnalysis.kt is missing')
need('// ' not in advanced and '/*' not in advanced,'AdvancedAnalysis.kt contains source comments')

has_all(main,[
    'EvidenceInspectorDialog(', 'CustomAccessibilityAction("Evidence actions")', 'detectTapGestures(', 'onLongPress = { showEvidenceActions = true }',
    '"Copy name + value"', '"Share evidence"', '"Add to watched evidence"', '"Open in Encyclopedia"',
    '"Global Vulkan report search"', 'ANALYSIS_GLOBAL_SEARCH_VISIBLE_LIMIT',
    '"System driver ↔ Turnip A/B"', '"Run guided System ↔ Turnip A/B"', 'abWorkflowStage', '"collect-system"', '"collect-turnip"',
    '"Collection diagnostics"', 'queryTimingMs', 'timingStartNanos', '"Per-query timing"',
    '"Capability requirement resolver"', 'requirementEvaluations',
    'FORMAT_USAGE_FILTERS', '"Sampled" to listOf("SAMPLED_IMAGE")', '"Storage" to listOf("STORAGE_IMAGE")', '"Video decode"', '"Video encode"',
    '"Matrix"', 'videoProfileQueueFamilies', '"Matching queue operation evidence"',
    '"Surface + Display presentation evidence"', 'presentationPaths',
    '"VulkanScopeMinimumProfile1"', 'importMinimumProfile', 'exportMinimumProfile', 'saveMinimumProfile',
    'exportRawTechnicalReport',
    '"Compare with VulkanScope Database"', 'fetchDatabaseTechnicalReport', 'addPathSegments("v1/reports/$id").addQueryParameter("compact", "1")',
    '"Local session history"', 'loadAnalysisHistoryRecords', 'saveAnalysisHistoryRecord',
    'ExpressiveEvidenceRow', 'ExpressiveFilterBar', 'ExpressiveMetric', 'ExpressiveStatus', 'ExpressiveToggleRow', 'ExpressiveDetailDialog', 'ExpressiveScrollHints'
] , '1.0.0 UI/workflow')

for pattern, label in [
    (r'^internal const val ANALYSIS_HISTORY_MAX_ITEMS = 8$', 'history item bound'),
    (r'^internal const val ANALYSIS_HISTORY_MAX_UNCOMPRESSED_BYTES = 8 \* 1024 \* 1024$', 'history uncompressed bound'),
    (r'^internal const val ANALYSIS_HISTORY_MAX_COMPRESSED_BYTES = 4 \* 1024 \* 1024$', 'history compressed bound'),
    (r'^internal const val ANALYSIS_DATABASE_COMPARE_MAX_BYTES = 2 \* 1024 \* 1024$', 'Database compare bound'),
    (r'^internal const val ANALYSIS_RAW_JSON_VISIBLE_LIMIT = 256$', 'raw JSON visible bound'),
    (r'^internal const val ANALYSIS_GLOBAL_SEARCH_VISIBLE_LIMIT = 160$', 'global search visible bound'),
    (r'^internal const val ANALYSIS_CUSTOM_PROFILE_MAX_RULES = 64$', 'custom profile rule bound'),
    (r'^internal const val ANALYSIS_CUSTOM_PROFILE_MAX_PROFILES = 32$', 'custom profile count bound'),
]:
    need(re.search(pattern, advanced, re.MULTILINE) is not None, f'1.0.0 bounded analysis helper missing exact {label}')

has_all(advanced,[
    'evidenceProvenance', 'queryDiagnostics', 'requirementTokens', 'evaluateRequirementToken', 'evaluateCustomMinimumRule',
    'flattenJson', 'genericDiff', 'presentationEvidencePaths', 'VulkanScopeAnalysisHistory1', 'GZIPOutputStream', 'GZIPInputStream',
    'Registry/reference presence never proves selected-device runtime support',
    'Not recorded by the current per-query report contract', 'COMPATIBLE EVIDENCE',
] , '1.0.0 bounded analysis helper')

need('baseUrl.host != "vulkanscope-database-api.vulkanscope.workers.dev"' in main and 'addPathSegments("v1/reports/$id").addQueryParameter("compact", "1")' in main,'Database compare is not pinned to the official Worker origin/route')
need('Regex("[a-f0-9]{64}")' in main,'Database compare report-id fail-closed validation missing')
need('readResponseTextLimited(response.body, ANALYSIS_DATABASE_COMPARE_MAX_BYTES)' in main,'Database compare 2 MiB response bound missing')
current_version_match = re.search(r'versionName\s*=\s*"(\d+)\.(\d+)\.(\d+)"', gradle)
current_version = tuple(map(int, current_version_match.groups())) if current_version_match else (0, 0, 0)
need(('"Raw structured technical Report"' if current_version >= (1, 0, 17) else '"Raw structured technicalReport"') in main, 'raw structured technical report section title missing for current release')
need('technicalReportJson(context, report, display, mode)' in main,'raw structured report is not sourced from canonical technicalReportJson')
need('No driver mutation starts without that user action' in main,'guided A/B explicit-user-authorization wording missing')
need('does not upgrade capability state' in main or 'does not upgrade' in main,'Vulkan Video association non-inference wording missing')
need('does not prove end-to-end' in advanced,'presentation-path non-guarantee wording missing')
need('ExpressiveScrollHints(listState, Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 6.dp))' in main,'release-note shared scroll primitive adoption missing')
need('ScrollBoundaryIndicators(listState, Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 6.dp))' not in main,'release notes bypass shared ExpressiveScrollHints primitive')
need('## Release 1.0.0 advanced evidence, analysis and workflow requirements' in rules,'PROJECT_RULES 1.0.0 contract missing')
need((root/'rules/1.0.0_ADVANCED_ANALYSIS_EVIDENCE_WORKFLOW_AUDIT.md').is_file(),'1.0.0 audit missing')
need((root/'tests/golden/0.80.15_regression_contract.json').is_file(),'0.80.15 predecessor regression contract missing')

if errors:
    for error in errors: print('FAIL:',error)
    raise SystemExit(1)
print('PASS VulkanScope 1.0.0 advanced evidence/analysis workflow contract')
