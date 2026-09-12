#!/usr/bin/env python3
import argparse
import re
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root')
parser.add_argument('--skip-version', action='store_true')
args = parser.parse_args()
root = Path(args.root).resolve() if args.root else Path(__file__).resolve().parents[1]
main_path = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
gradle_path = root / 'app/build.gradle.kts'
manifest_path = root / 'app/src/main/AndroidManifest.xml'
main = main_path.read_text(encoding='utf-8')
gradle = gradle_path.read_text(encoding='utf-8')
manifest = manifest_path.read_text(encoding='utf-8')
version_match = re.search(r'versionName\s*=\s*"(\d+)\.(\d+)\.(\d+)"', gradle)
current_version = tuple(map(int, version_match.groups())) if version_match else (0, 0, 0)
code_match = re.search(r'versionCode\s*=\s*(\d+)', gradle)
errors = []

def need(condition, message):
    if not condition:
        errors.append(message)

def between(start, end):
    a = main.find(start)
    b = main.find(end, a + 1) if a >= 0 else -1
    return main[a:b] if a >= 0 and b > a else ''

if not args.skip_version:
    expected_code = current_version[0] * 1000 + current_version[1] * 100 + current_version[2]
    need(version_match is not None and code_match is not None and current_version >= (1, 2, 3) and int(code_match.group(1)) == expected_code, 'retained 1.2.3+ release identity missing')
need(re.search(r'\bminSdk\s*=\s*31\b', gradle) is not None, 'Android 12 API 31 minimum is missing')
need(re.search(r'\btargetSdk\s*=\s*37\b', gradle) is not None, 'targetSdk 37 drifted')
need('android.permission.WRITE_EXTERNAL_STORAGE' not in manifest, 'legacy WRITE_EXTERNAL_STORAGE permission remains')
need('ActivityResultContracts.RequestPermission()' not in between('private fun InfoPage(', '@Composable\nprivate fun SettingsSectionCards'), 'legacy pre-Android-10 report-export permission launcher remains')
need('Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && androidx.core.content.ContextCompat.checkSelfPermission' not in main, 'legacy pre-Android-10 report-export permission branch remains')

resolver = between('fun resolveWatchInput(raw: String): String?', 'val canAddWatch')
need(resolver != '', 'typed watched-evidence resolver missing')
need('currentEntries.entries.firstOrNull { entry ->' in resolver, 'watched-evidence resolver does not iterate Map.entries')
need('currentEntries.firstOrNull' not in main, 'compile-breaking firstOrNull call remains on Map receiver')
need('return evidenceTokenForReference(matchedEntry.key, matchedEntry.value)' in resolver, 'watched-evidence resolver lost typed key/value resolution')

need('private data class DatabaseSubmissionResult(val success: Boolean, val reportId: String?, val summary: String, val log: String)' in main, 'typed Database submission result missing')
need('private fun boundedDatabaseSubmissionLog(value: String): String' in main and 'val limit = 96 * 1024' in main, 'bounded Database diagnostic log missing')
need('readResponseTextLimited(response.body, 64 * 1024)' in main, 'bounded Database response-read ceiling drifted')
submit = between('private suspend fun submitDatabaseReport(', '\nprivate fun safeFilePart')
need('if (!id.matches(Regex("[a-f0-9]{64}"))) {' in submit, 'successful HTTP response is not gated on exact lowercase 64-hex report id')
need('DatabaseSubmissionResult(true, id, "Report submitted successfully."' in main, 'successful submission does not carry the exact report id')
need('putString("last_report_id", id)' in main, 'successful exact report id is not persisted for existing permalink flow')
need('submissionSuccessId = result.reportId\n' in main, 'successful report id is not published to UI state')
need('Text("Report ID"' in main, 'successful report-id label missing')
success_id_ui = between('submissionSuccessId?.let { reportId ->', '                }\n')
need('Text(reportId, color = VulkanTextPrimary' in success_id_ui and 'fontWeight = FontWeight.Bold' in success_id_ui and 'fontFamily = FontFamily.Monospace' in success_id_ui, 'successful report id is not full bold primary monospace text')
need('.take(12)' not in between('submissionSuccessId?.let { reportId ->', '}',), 'successful report id is still truncated in its result presentation')

failure_dialog = between('private fun DatabaseSubmissionFailureDialog(', '@Composable\nprivate fun DatabaseFailureDialogTitle')
need(failure_dialog != '', 'Database submission failure dialog missing')
need('fontFamily = FontFamily.Monospace' in failure_dialog, 'failure log is not code-style monospace text')
need('verticalScroll(scrollState)' in failure_dialog and 'heightIn(max = 360.dp)' in failure_dialog, 'failure log lacks bounded scrollable presentation')
need('ExpressiveContainedIconTextButton("Copy all", R.drawable.ic_copy)' in failure_dialog, 'contained Copy all action with copy icon missing')
need('ExpressiveContainedIconTextButton("Close", R.drawable.ic_close' in failure_dialog, 'contained Close action with X icon missing')
need('copyEvidenceText(context, "VulkanScope Database submission log", log)' in failure_dialog, 'Copy all does not copy the complete displayed log')

title = between('private fun DatabaseFailureDialogTitle()', '@Composable\nprivate fun TransientActionButton')
need('R.drawable.ic_database_submit' in title and 'R.drawable.ic_close' in title and 'Alignment.BottomEnd' in title, 'Database+lower-right-X failure artwork missing')
need('tint = ComposeColor(0xFFFF6B6B)' in title, 'Database failure X is not red')

for phase in ['network-validation', 'report-validation', 'endpoint-validation', 'serialization', 'payload-validation', 'http-response', 'response-validation', 'network-request']:
    need(f'"{phase}"' in submit, f'Database failure phase missing: {phase}')
need('databaseSubmissionFailure(' in submit, 'typed failure helper is not used by submission paths')
need('return@withContext "Submission' not in submit, 'string-only Database failure result remains')
need('!submissionState.contains(' not in main, 'Database success is still inferred from message text')
need('submissionFailureLog = result.log' in main, 'typed failed submission does not open diagnostic state')
need('submissionFailureLog?.let { log ->' in main and 'DatabaseSubmissionFailureDialog(log = log' in main, 'failed submission does not open the diagnostic dialog')
need('payload.toString(' not in submit and 'String(payload' not in submit, 'submitted technical report payload is copied into failure diagnostics')
need('payloadBytes=${payload.size}' in submit, 'bounded diagnostics do not retain payload-size evidence')

transient = between('private fun TransientActionButton(', '@Composable\nprivate fun SettingsPage')
need('catch (error: CancellationException)' in transient and 'throw error' in transient, 'transient action wrapper swallows coroutine cancellation')

need('OFFICIAL_DATABASE_API_ENDPOINT = "https://vulkanscope-database-api.vulkanscope.workers.dev"' in main, 'fixed Database endpoint drifted')
need('if (payload.size > 2 * 1024 * 1024)' in submit, 'Database 2 MiB fail-closed payload ceiling drifted')
need('android:usesCleartextTraffic="false"' in manifest, 'cleartext traffic prohibition drifted')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 1.2.3 compile/Database/Android-12 contract')
