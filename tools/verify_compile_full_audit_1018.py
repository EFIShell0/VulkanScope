#!/usr/bin/env python3
import argparse
import json
import re
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', type=Path, default=Path(__file__).resolve().parents[1])
parser.add_argument('--skip-version', action='store_true')
args = parser.parse_args()
root = args.root.resolve()
errors = []

def need(condition, message):
    if not condition:
        errors.append(message)

main_path = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
service_path = root / 'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt'
gradle_path = root / 'app/build.gradle.kts'
root_gradle_path = root / 'build.gradle.kts'
manifest_path = root / 'app/src/main/AndroidManifest.xml'
cmake_path = root / 'app/src/main/cpp/CMakeLists.txt'
lock_path = root / 'registry/registry_lock.json'
database_setup_path = root / 'DATABASE_SETUP.md'
rules_path = root / 'rules/PROJECT_RULES.md'
audit_path = root / 'rules/1.0.18_COMPILE_FULL_REAUDIT.md'
quality_path = root / 'tools/quality_gate.py'
for path in [main_path, service_path, gradle_path, root_gradle_path, manifest_path, cmake_path, lock_path, database_setup_path, rules_path, quality_path]:
    need(path.is_file(), f'missing required file: {path.relative_to(root)}')
if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)

main = main_path.read_text(encoding='utf-8')
service = service_path.read_text(encoding='utf-8')
gradle = gradle_path.read_text(encoding='utf-8')
root_gradle = root_gradle_path.read_text(encoding='utf-8')
manifest = manifest_path.read_text(encoding='utf-8')
cmake = cmake_path.read_text(encoding='utf-8')
lock = json.loads(lock_path.read_text(encoding='utf-8'))
database_setup = database_setup_path.read_text(encoding='utf-8')
rules = rules_path.read_text(encoding='utf-8')
quality = quality_path.read_text(encoding='utf-8')

if not args.skip_version:
    need('versionCode = 1018' in gradle and 'versionName = "1.0.18"' in gradle, '1.0.18 release identity missing')
need('compileSdk = 37' in gradle and 'targetSdk = 37' in gradle and 'minSdk = 24' in gradle, 'Android SDK policy drifted')
need('ndkVersion = "29.0.14206865"' in gradle, 'NDK pin drifted')
need(all(abi in gradle for abi in ['"arm64-v8a"', '"armeabi-v7a"', '"x86_64"']), 'required ABI set incomplete')
need('"x86"' not in gradle, 'forbidden x86 ABI enabled')
need('isMinifyEnabled = true' in gradle and 'isShrinkResources = true' in gradle, 'release shrinking/resource optimization disabled')
need('id("com.android.application") version "9.4.0" apply false' in root_gradle, 'AGP pin drifted')
need('id("org.jetbrains.kotlin.plugin.compose") version "2.4.10" apply false' in root_gradle, 'Kotlin Compose plugin pin drifted')

signature = 'private fun ExpressiveFilterBar(labels: List<String>, selectedIndex: Int, arrowTint: ComposeColor = VulkanTextPrimary, onSelected: (Int) -> Unit)'
good_call = 'ExpressiveFilterBar(devices.mapIndexed { index, device -> "GPU ${index + 1} · ${device.name.ifBlank { "Unknown" }.take(48)}" }, selectedIndex, onSelected = onSelected)'
bad_call = 'ExpressiveFilterBar(devices.mapIndexed { index, device -> "GPU ${index + 1} · ${device.name.ifBlank { "Unknown" }.take(48)}" }, selectedIndex, onSelected)'
need(signature in main, 'ExpressiveFilterBar signature drifted')
need(good_call in main, 'PhysicalDeviceSelector callback is not bound through named onSelected')
need(bad_call not in main, 'compile-breaking positional PhysicalDeviceSelector callback remains')
need('compile-breaking positional PhysicalDeviceSelector callback remains after arrowTint parameter insertion' in (root / 'tools/verify_compile_regressions.py').read_text(encoding='utf-8'), 'shared compile-regression gate does not cover the 1.0.18 failure class')

need(manifest.count('android.permission.INTERNET') == 1, 'INTERNET permission count drifted')
need('android:usesCleartextTraffic="false"' in manifest, 'cleartext traffic protection missing')
need('android:allowBackup="false"' in manifest, 'application backup must remain disabled')
need('<service android:name=".VulkanProbeService" android:exported="false"' in manifest, 'Vulkan probe service exposure drifted')
need('<provider android:name="androidx.core.content.FileProvider"' in manifest and 'android:exported="false"' in manifest, 'FileProvider exposure drifted')
need('android.permission.MANAGE_EXTERNAL_STORAGE' not in manifest and 'android.permission.QUERY_ALL_PACKAGES' not in manifest, 'unnecessary broad Android permission introduced')

need('private const val OFFICIAL_DATABASE_API_ENDPOINT = "https://vulkanscope-database-api.vulkanscope.workers.dev"' in main, 'fixed Database HTTPS origin drifted')
need('https://api.github.com/repos/EFIShell0/VulkanScope/releases?per_page=20' in main, 'official update API route drifted')
need('Database API endpoint' not in main and 'databaseEndpoint' not in main, 'Database endpoint became user-editable')
need('.addPathSegments("v1/reports").build()' in main and '.post(payload.toRequestBody("application/json; charset=utf-8".toMediaType()))' in main, 'Database POST route drifted')
need('.addPathSegments("v1/reports/$id").addQueryParameter("compact", "1")' in main and '.get().build()' in main, 'Database compact GET route drifted')
need('payload.optJSONObject("technicalReport") ?: error("Database report does not contain technicalReport")' in main, 'Database lookup technicalReport requirement drifted')
need('payload.size > 2 * 1024 * 1024' in main and 'No data was truncated.' in main, 'Database fail-closed no-truncation ceiling drifted')
need('put("schemaVersion", 2)' in main and 'put("technicalReport", technicalReportJson(context, report, display, mode))' in main, 'Database submission schema drifted')
need('VulkanScope Database 1.0.8 is the companion Database for VulkanScope 1.0.18' in database_setup, 'Database companion documentation drifted')
need('schema 2 / technicalReport 3' in database_setup and 'normalizer 16' in database_setup, 'Database schema/normalizer documentation drifted')

need(lock.get('apiBaseline') == 'Vulkan 1.4.362', 'Vulkan API baseline drifted')
need(lock.get('registryRef') == '1.4.362', 'Vulkan registry ref drifted')
need(lock.get('publishedDate') == '2026-09-04', 'Vulkan registry publication date drifted')
need(lock.get('registrySha256') == 'cf31c965cf6e788697139601da0c7e02a75a9b6c7ac764e7641f5521ffd9da06', 'Vulkan registry hash drifted')
need(lock.get('headerCommit') == 'ee2ec5fd83dafce291024683b50dc89219333076', 'Vulkan-Headers commit drifted')
need(lock.get('headerVersion') == 362, 'Vulkan header version drifted')
need('GIT_TAG ee2ec5fd83dafce291024683b50dc89219333076' in cmake, 'CMake Vulkan-Headers pin drifted')
need('-fstack-protector-strong' in cmake and '"-Wl,-z,relro"' in cmake and '"-Wl,-z,now"' in cmake, 'native hardening flags drifted')
need('-Wall -Wextra -Werror' in cmake, 'VulkanScope native warning-as-error policy drifted')

for token in ['worker.shutdownNow()', 'mainHandler.removeCallbacksAndMessages(null)', 'surface?.release()', 'PROCESS_NATIVE_PROBE_LOCK.withLock', 'terminateDedicatedProcess(']:
    need(token in service, f'probe resource/lifecycle invariant missing: {token}')
need('Executors.newSingleThreadExecutor' in service, 'probe work is no longer single-thread serialized')
need('requestedResult.path.startsWith(cacheRoot.path + File.separator)' in service, 'probe result canonical-path confinement drifted')
need('timeoutMs !in 1_000L..60_000L' in service, 'probe timeout bound drifted')

need('## Release 1.0.18 compile-regression and full retained-audit requirements' in rules, 'PROJECT_RULES 1.0.18 contract missing')
for tool in ['verify_compile_full_audit_1018.py', 'test_compile_full_audit_1018_state_machine.py', 'test_compile_full_audit_1018_negative_mutations.py']:
    need(tool in quality, f'aggregate quality gate omits 1.0.18 tool: {tool}')
need(audit_path.is_file(), '1.0.18 audit record missing')

for forbidden in [r'\bTODO\b', r'\bFIXME\b']:
    need(re.search(forbidden, main + service) is None, f'production source contains unresolved marker matching {forbidden}')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 1.0.18 compile/full retained audit contract')
