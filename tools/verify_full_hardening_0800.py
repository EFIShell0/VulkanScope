#!/usr/bin/env python3
import argparse
import json
import re
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root')
args = parser.parse_args()
root = Path(args.root).resolve() if args.root else Path(__file__).resolve().parents[1]
errors = []
main_path = root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
service_path = root / 'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt'
manifest_path = root / 'app/src/main/AndroidManifest.xml'
cmake_path = root / 'app/src/main/cpp/CMakeLists.txt'
build_path = root / 'app/build.gradle.kts'
root_build_path = root / 'build.gradle.kts'
registry_lock_path = root / 'registry/registry_lock.json'
for path in [main_path, service_path, manifest_path, cmake_path, build_path, root_build_path, registry_lock_path]:
    if not path.is_file():
        errors.append(f'missing audit input: {path.relative_to(root)}')
if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
main = main_path.read_text(encoding='utf-8')
service = service_path.read_text(encoding='utf-8')
manifest = manifest_path.read_text(encoding='utf-8')
cmake = cmake_path.read_text(encoding='utf-8')
build = build_path.read_text(encoding='utf-8')
root_build = root_build_path.read_text(encoding='utf-8')
lock = json.loads(registry_lock_path.read_text(encoding='utf-8'))
for needle in [
    'private const val TURNIP_ARCHIVE_INPUT_MAX_BYTES = 96L * 1024L * 1024L',
    'private class BoundedDriverArchiveInputStream',
    'Driver bundle archive exceeds the compressed-input safety limit',
    'ZipInputStream(BoundedDriverArchiveInputStream(input, TURNIP_ARCHIVE_INPUT_MAX_BYTES)).use { zip ->'
]:
    if needle not in main:
        errors.append(f'compressed Turnip input bound missing: {needle}')
if 'ZipInputStream(input).use { zip ->' in main:
    errors.append('Turnip import still feeds an unbounded selected input directly to ZipInputStream')
for needle in [
    'import android.util.JsonReader',
    'import android.util.JsonToken',
    'import java.io.StringReader',
    'private fun strictProbeTerminalCandidate(candidate: String, baseGroup: Boolean): Boolean',
    'return strictProbeTerminalCandidate(candidate, group == "base")'
]:
    if needle not in main:
        errors.append(f'streaming probe terminal validation missing: {needle}')
if re.search(r'fun terminalCandidate\(candidate: String\): Boolean \{\s*val parsed = runCatching \{ JSONObject\(candidate\)', main):
    errors.append('probe terminal validation still materializes a JSONObject tree')
checkpoint_match = re.search(r'if \(resultLength > 0L && checkpointChanged\) \{(.*?)\n\s*\} else \{\n\s*kotlinx\.coroutines\.delay\(40L\)', main, re.S)
if not checkpoint_match:
    errors.append('checkpoint-change polling block could not be located')
else:
    checkpoint_block = checkpoint_match.group(1)
    if 'readFileTextLimited' in checkpoint_block or 'JSONObject(' in checkpoint_block:
        errors.append('normal changed-checkpoint polling still materializes/parses the full publication')
    if 'if (crashDetected()) continue' not in checkpoint_block:
        errors.append('changed-checkpoint polling must immediately re-enter crash handling after metadata observation')
for needle in [
    'val maxProbeResultBytes = 64L * 1024L * 1024L',
    'val maxTotalBytes = 64L * 1024L * 1024L',
    'val maxFileBytes = 32L * 1024L * 1024L',
    'val maxEntries = 2048',
    'private val databaseHttpClient = ipv6PreferredHttpClient.newBuilder()',
    '    .followRedirects(false)',
    '    .followSslRedirects(false)',
    'OFFICIAL_DATABASE_API_ENDPOINT = "https://vulkanscope-database-api.vulkanscope.workers.dev"',
    '.url("https://api.github.com/repos/EFIShell0/VulkanScope/releases?per_page=20")'
]:
    if needle not in main:
        errors.append(f'retained resource/security invariant missing: {needle}')
for needle in [
    'android:allowBackup="false"',
    'android:usesCleartextTraffic="false"',
    'android:name="androidx.core.content.FileProvider"',
    'android:exported="false"',
    'android:name=".VulkanProbeService"'
]:
    if needle not in manifest:
        errors.append(f'Android security invariant missing: {needle}')
if manifest.count('android:exported="true"') != 1:
    errors.append('only the launcher activity may remain exported')
for needle in ['-fstack-protector-strong', '-Wl,-z,relro', '-Wl,-z,now']:
    if needle not in cmake:
        errors.append(f'native hardening flag missing: {needle}')
for needle in [
    'compileSdk = 37',
    'targetSdk = 37',
    'ndkVersion = "29.0.14206865"',
    'implementation("androidx.core:core-ktx:1.19.0")',
    'implementation("androidx.activity:activity-compose:1.13.0")',
    'implementation("androidx.compose.ui:ui:1.12.0")',
    'implementation("androidx.compose.material3:material3:1.5.0-alpha27")',
    'implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0")',
    'implementation("com.squareup.okhttp3:okhttp:5.5.0")',
    'implementation("com.google.zxing:core:3.5.4")'
]:
    if needle not in build:
        errors.append(f'validated Android/runtime pin drift: {needle}')
for needle in [
    'id("com.android.application") version "9.3.2" apply false',
    'id("org.jetbrains.kotlin.plugin.compose") version "2.4.10" apply false'
]:
    if needle not in root_build:
        errors.append(f'validated build-tool pin drift: {needle}')
if lock.get('apiBaseline') != 'Vulkan 1.4.361' or lock.get('registryRef') != '1.4.361' or lock.get('headerVersion') != 361:
    errors.append('Vulkan 1.4.361/header 361 release lock drifted')
if 'JsonReader' not in service or 'reader.isLenient = false' not in service:
    errors.append('probe service strict streaming JSON validation regressed')
if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS 0.80.0 full hardening source contract')
