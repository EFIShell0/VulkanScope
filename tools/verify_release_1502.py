#!/usr/bin/env python3
import argparse
import importlib.util
import json
import re
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

sys.dont_write_bytecode = True


def load_1501(root: Path):
    spec = importlib.util.spec_from_file_location('verify_release_1501', root / 'tools/verify_release_1501.py')
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


def version_tuple(value: str) -> tuple[int, ...]:
    return tuple(int(piece) for piece in value.split('.'))


def verify(root: Path, skip_version: bool = False) -> list[str]:
    errors: list[str] = []
    previous = load_1501(root)
    errors.extend(f'1.5.1 retained contract: {value}' for value in previous.verify(root, skip_version=True))

    app_build = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
    root_build = (root / 'build.gradle.kts').read_text(encoding='utf-8')
    wrapper = (root / 'gradle/wrapper/gradle-wrapper.properties').read_text(encoding='utf-8')
    manifest_path = root / 'app/src/main/AndroidManifest.xml'
    main = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
    service = (root / 'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt').read_text(encoding='utf-8')
    lock = json.loads((root / 'registry/registry_lock.json').read_text(encoding='utf-8'))

    if not skip_version and ('versionCode = 1502' not in app_build or 'versionName = "1.5.2"' not in app_build):
        errors.append('release identity is not 1.5.2/1502')
    if 'id("com.android.application") version "9.4.1" apply false' not in root_build:
        errors.append('Android Gradle Plugin is not pinned to stable 9.4.1')
    if re.search(r'com\.android\.application.*(?:alpha|beta|rc)', root_build, re.IGNORECASE):
        errors.append('preview Android Gradle Plugin is not allowed')
    if 'id("org.jetbrains.kotlin.plugin.compose") version "2.4.10" apply false' not in root_build:
        errors.append('Kotlin Compose plugin drifted from retained 2.4.10')

    match = re.search(r'gradle-([0-9]+\.[0-9]+(?:\.[0-9]+)?)-bin\.zip', wrapper)
    if not match:
        errors.append('Gradle wrapper version could not be parsed')
    else:
        gradle_version = match.group(1)
        if version_tuple(gradle_version) < version_tuple('9.6.0'):
            errors.append(f'Gradle {gradle_version} is below AGP 9.4 minimum 9.6.0')
        if gradle_version != '9.7.1':
            errors.append(f'Gradle wrapper drifted from audited 9.7.1: {gradle_version}')

    for token in [
        'compileSdk = 37',
        'targetSdk = 37',
        'minSdk = 31',
        'ndkVersion = "29.0.14206865"',
        'abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64")',
        'isMinifyEnabled = true',
        'isShrinkResources = true',
        'implementation("androidx.core:core-ktx:1.19.0")',
        'implementation("androidx.activity:activity-compose:1.13.0")',
        'implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0")',
        'implementation("com.squareup.okhttp3:okhttp:5.5.0")',
    ]:
        if token not in app_build:
            errors.append(f'audited Android build/runtime pin drifted: {token}')

    if lock.get('apiBaseline') != 'Vulkan 1.4.364' or lock.get('registryRef') != '1.4.364' or lock.get('headerVersion') != 364:
        errors.append('locked Vulkan baseline drifted from Vulkan 1.4.364 / header 364')

    android_ns = 'http://schemas.android.com/apk/res/android'
    tree = ET.parse(manifest_path)
    root_xml = tree.getroot()
    application = root_xml.find('application')
    if application is None:
        errors.append('manifest application element missing')
    else:
        if application.get(f'{{{android_ns}}}usesCleartextTraffic') != 'false':
            errors.append('cleartext traffic protection is not explicitly false')
        if application.get(f'{{{android_ns}}}allowBackup') != 'false':
            errors.append('backup protection is not explicitly false')
        if application.get(f'{{{android_ns}}}supportsRtl') != 'true':
            errors.append('RTL support is not enabled')
        for kind in ('provider', 'service'):
            for node in application.findall(kind):
                if node.get(f'{{{android_ns}}}exported') != 'false':
                    errors.append(f'{kind} unexpectedly exported: {node.get(f"{{{android_ns}}}name")}')
    permissions = {
        node.get(f'{{{android_ns}}}name')
        for node in root_xml.findall('uses-permission')
        if node.get(f'{{{android_ns}}}name')
    }
    expected_permissions = {
        'android.permission.INTERNET',
        'android.permission.ACCESS_NETWORK_STATE',
        'android.permission.REQUEST_INSTALL_PACKAGES',
        'android.permission.MANAGE_EXTERNAL_STORAGE',
    }
    if permissions != expected_permissions:
        errors.append(f'manifest permission set drifted: {sorted(permissions)}')

    lifecycle_tokens = [
        'unregisterNetworkCallback(networkCallback)',
        'unregisterDisplayListener(displayListener)',
        'activeUpdateCheckCall?.cancel()',
        'activeUpdateDownloadCall?.cancel()',
        'turnipFileManagerScanJob?.cancel()',
        'stopVulkanProbeProcess()',
        'activityScope.cancel()',
    ]
    for token in lifecycle_tokens:
        if token not in main:
            errors.append(f'Activity lifecycle/resource cleanup missing: {token}')
    for token in ['.followRedirects(false)', '.followSslRedirects(false)', 'TURNIP_ARCHIVE_INPUT_MAX_BYTES = 96L * 1024L * 1024L', 'ANALYSIS_MAX_SNAPSHOT_BYTES = 8 * 1024 * 1024', 'safe.setReadOnly()', 'library.setReadOnly()']:
        if token not in main:
            errors.append(f'security/resource bound missing: {token}')
    for token in ['64L * 1024L * 1024L', 'worker.shutdownNow()']:
        if token not in service:
            errors.append(f'probe resource/lifecycle bound missing: {token}')

    metric_patterns = {
        'width': r'PrimaryNavigationMaxWidth\s*=\s*(\d+(?:\.\d+)?)\.dp',
        'height': r'PrimaryNavigationHeight\s*=\s*(\d+(?:\.\d+)?)\.dp',
    }
    metrics = {}
    for key, pattern in metric_patterns.items():
        found = re.search(pattern, main)
        if not found:
            errors.append(f'navigation accessibility metric missing: {key}')
        else:
            metrics[key] = float(found.group(1))
    if metrics.get('height', 0.0) < 48.0:
        errors.append('navigation tab touch height is below 48 dp')
    if metrics.get('width', 0.0) / 4.0 < 48.0:
        errors.append('navigation tab touch width is below 48 dp')
    nav_start = main.find('private fun CompactBottomNavigationBar')
    nav_end = main.find('@Composable\nprivate fun ExploreDestinationTile', nav_start)
    nav = main[nav_start:nav_end] if nav_start >= 0 and nav_end > nav_start else ''
    for token in ['role = Role.Tab', 'Text(', 'item.label', 'indication = null']:
        if token not in nav:
            errors.append(f'navigation accessibility/selection contract missing: {token}')

    if '## Release 1.5.2 AGP 9.4.1 full audit and build-chain hardening requirements' not in (root / 'rules/PROJECT_RULES.md').read_text(encoding='utf-8'):
        errors.append('1.5.2 release rules are missing')
    if not (root / 'rules/1.5.2_AGP_9_4_1_FULL_AUDIT.md').is_file():
        errors.append('1.5.2 audit document is missing')
    return errors


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument('--root', default=str(Path(__file__).resolve().parents[1]))
    parser.add_argument('--skip-version', action='store_true')
    args = parser.parse_args()
    errors = verify(Path(args.root).resolve(), args.skip_version)
    if errors:
        for error in errors:
            print('FAIL', error)
        return 1
    print('VulkanScope 1.5.2 AGP/spec/security/accessibility/lifecycle verifier: PASS')
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
