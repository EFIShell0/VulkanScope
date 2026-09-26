#!/usr/bin/env python3
import argparse
import json
import subprocess
import sys
import tempfile
import urllib.request
from pathlib import Path

root = Path(__file__).resolve().parents[1]
parser = argparse.ArgumentParser()
parser.add_argument('--registry')
parser.add_argument('--header')
parser.add_argument('--strict-upstream', action='store_true')
parser.add_argument('--fetch-locked-upstream', action='store_true')
args = parser.parse_args()
lock = json.loads((root / 'registry/registry_lock.json').read_text(encoding='utf-8'))
gradle_text = (root / 'app/build.gradle.kts').read_text(encoding='utf-8')
release_2006_surface_scroll = 'versionCode = 2006' in gradle_text and 'versionName = "2.0.6"' in gradle_text
release_2005_tv_key_compile_fix = 'versionCode = 2005' in gradle_text and 'versionName = \"2.0.5\"' in gradle_text
release_2004_six_lane_tv = 'versionCode = 2004' in gradle_text and 'versionName = \"2.0.4\"' in gradle_text
release_2003_adaptive_scheduler = 'versionCode = 2003' in gradle_text and 'versionName = \"2.0.3\"' in gradle_text
release_2002_parallel_oneshot = 'versionCode = 2002' in gradle_text and 'versionName = \"2.0.2\"' in gradle_text
release_2000_timing_instrumentation = 'versionCode = 2000' in gradle_text and 'versionName = \"2.0.0\"' in gradle_text
release_1502_agp_full_audit = 'versionCode = 1502' in gradle_text and 'versionName = \"1.5.2\"' in gradle_text
release_1501_profile_navigation_opening = 'versionCode = 1501' in gradle_text and 'versionName = \"1.5.1\"' in gradle_text
release_1303_storage_removal = 'versionCode = 1303' in gradle_text and 'versionName = \"1.3.3\"' in gradle_text
release_1304_file_manager = 'versionCode = 1304' in gradle_text and 'versionName = \"1.3.4\"' in gradle_text
release_1305_full_audit = 'versionCode = 1305' in gradle_text and 'versionName = \"1.3.5\"' in gradle_text
release_1306_storage_exchange = 'versionCode = 1306' in gradle_text and 'versionName = \"1.3.6\"' in gradle_text
release_1405_googlebook_vulkan = 'versionCode = 1405' in gradle_text and 'versionName = \"1.4.5\"' in gradle_text
release_1406_launch_navigation = 'versionCode = 1406' in gradle_text and 'versionName = \"1.4.6\"' in gradle_text
release_1407_compile_mouse_input = 'versionCode = 1407' in gradle_text and 'versionName = \"1.4.7\"' in gradle_text
release_1412_compile_navigation_profiles = 'versionCode = 1412' in gradle_text and 'versionName = \"1.4.12\"' in gradle_text
release_1500_compact_navigation_watchdog = 'versionCode = 1500' in gradle_text and 'versionName = \"1.5.0\"' in gradle_text
release_1416_surface_status_navigation = 'versionCode = 1416' in gradle_text and 'versionName = \"1.4.16\"' in gradle_text
release_1415_unified_navigation = 'versionCode = 1415' in gradle_text and 'versionName = \"1.4.15\"' in gradle_text
release_1414_floating_navigation = 'versionCode = 1414' in gradle_text and 'versionName = \"1.4.14\"' in gradle_text
release_1413_researched_navigation = 'versionCode = 1413' in gradle_text and 'versionName = \"1.4.13\"' in gradle_text


def run(command):
    result = subprocess.run(command, cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    if result.stdout:
        print(result.stdout.rstrip())
    if result.returncode != 0:
        raise SystemExit(result.returncode)


def run_legacy_storage_gate(command):
    if not release_1303_storage_removal and not release_1304_file_manager:
        run(command)



def run_2006_quality_gate():
    commands = [
        [sys.executable, 'tools/verify_release_2006.py'],
        [sys.executable, 'tools/test_release_2006_state_machine.py'],
        [sys.executable, 'tools/test_release_2006_negative_mutations.py'],
        [sys.executable, 'tools/verify_cmake_registry_lock.py'],
        [sys.executable, 'tools/verify_registry_snapshot.py'],
        [sys.executable, 'tools/verify_compile_regressions.py'],
        [sys.executable, 'tools/verify_spec_regressions.py'],
        [sys.executable, 'tools/verify_probe_publication_handshake.py'],
        [sys.executable, 'tools/verify_probe_terminal_ownership.py'],
        [sys.executable, 'tools/verify_probe_lifecycle_regressions.py'],
        [sys.executable, 'tools/verify_probe_timeout_recovery.py'],
        [sys.executable, 'tools/verify_probe_cancellation_recovery.py'],
        [sys.executable, 'tools/verify_report_semantics_04140.py'],
        [sys.executable, 'tools/verify_report_surface_integrity_04141.py'],
        [sys.executable, 'tools/verify_profile_requirements_04144.py'],
        [sys.executable, 'tools/test_profile_evaluator_state_machine.py'],
        [sys.executable, 'tools/test_profile_requirements_negative_mutations.py'],
        [sys.executable, 'tools/verify_video_registry_04145.py'],
        [sys.executable, 'tools/test_video_profile_census_state_machine.py'],
        [sys.executable, 'tools/verify_full_hardening_0800.py'],
        [sys.executable, 'tools/test_full_hardening_0800_state_machine.py'],
        [sys.executable, 'tools/verify_concurrency_resource_contracts.py'],
        [sys.executable, 'tools/verify_package_reproducibility.py'],
    ]
    for command in commands:
        run(command)
    registry_path = Path(args.registry).resolve() if args.registry else (root / lock['bundledRegistryPath']).resolve()
    header_path = Path(args.header).resolve() if args.header else None
    with tempfile.TemporaryDirectory(prefix='vulkanscope-manifest-') as temp_name:
        generated = Path(temp_name) / 'registry_query_manifest.json'
        command = [
            sys.executable, 'tools/generate_vk_registry.py',
            '--registry', str(registry_path),
            '--catalog', 'app/src/main/cpp/registry_query_catalog.h',
            '--coverage', 'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt',
            '--lock', 'registry/registry_lock.json',
            '--out', str(generated),
            '--require-complete-extension-coverage'
        ]
        if header_path:
            command += ['--header', str(header_path)]
        run(command)
        expected = json.loads((root / 'registry/generated/registry_query_manifest.json').read_text(encoding='utf-8'))
        actual = json.loads(generated.read_text(encoding='utf-8'))
        if actual != expected:
            raise SystemExit('regenerated locked registry manifest differs from checked-in manifest')
    if args.strict_upstream and not header_path:
        raise SystemExit('strict upstream header gate requires --header')
    if header_path:
        run([sys.executable, 'tools/verify_canonical_vulkan_headers.py', str(header_path)])
        run([sys.executable, 'tools/verify_registry_catalog.py', 'registry/generated/registry_query_manifest.json', str(header_path), 'app/src/main/cpp/registry_query_catalog.h'])
        run([sys.executable, 'tools/verify_extension_field_coverage.py', '--registry', str(registry_path), '--header', str(header_path)])
        run([sys.executable, 'tools/verify_upstream_registry.py', '--registry', str(registry_path), '--header', str(header_path)])
        print('PASS strict locked-header verification')
    else:
        print('INFO strict canonical Vulkan header byte-level verification NOT EXECUTED; registry-level verification used the bundled SHA-256-locked vk.xml')
    for path in sorted((root / 'tools').glob('*.py')):
        compile(path.read_text(encoding='utf-8'), str(path), 'exec')
    for path in sorted((root / 'registry/generated').glob('*.json')) + [root / 'registry/registry_lock.json', root / 'registry/video_registry_lock.json'] + sorted((root / 'tests/golden').glob('*_regression_contract.json')):
        json.loads(path.read_text(encoding='utf-8'))
    print('VulkanScope quality gate 2.0.6: PASS')






def run_2005_quality_gate():
    commands = [
        [sys.executable, 'tools/verify_release_2005.py'],
        [sys.executable, 'tools/test_release_2005_state_machine.py'],
        [sys.executable, 'tools/test_release_2005_negative_mutations.py'],
        [sys.executable, 'tools/verify_cmake_registry_lock.py'],
        [sys.executable, 'tools/verify_registry_snapshot.py'],
        [sys.executable, 'tools/verify_compile_regressions.py'],
        [sys.executable, 'tools/verify_spec_regressions.py'],
        [sys.executable, 'tools/verify_probe_publication_handshake.py'],
        [sys.executable, 'tools/verify_probe_terminal_ownership.py'],
        [sys.executable, 'tools/verify_probe_lifecycle_regressions.py'],
        [sys.executable, 'tools/verify_probe_timeout_recovery.py'],
        [sys.executable, 'tools/verify_probe_cancellation_recovery.py'],
        [sys.executable, 'tools/verify_report_semantics_04140.py'],
        [sys.executable, 'tools/verify_report_surface_integrity_04141.py'],
        [sys.executable, 'tools/verify_profile_requirements_04144.py'],
        [sys.executable, 'tools/test_profile_evaluator_state_machine.py'],
        [sys.executable, 'tools/test_profile_requirements_negative_mutations.py'],
        [sys.executable, 'tools/verify_video_registry_04145.py'],
        [sys.executable, 'tools/test_video_profile_census_state_machine.py'],
        [sys.executable, 'tools/verify_full_hardening_0800.py'],
        [sys.executable, 'tools/test_full_hardening_0800_state_machine.py'],
        [sys.executable, 'tools/verify_concurrency_resource_contracts.py'],
        [sys.executable, 'tools/verify_package_reproducibility.py'],
    ]
    for command in commands:
        run(command)
    registry_path = Path(args.registry).resolve() if args.registry else (root / lock['bundledRegistryPath']).resolve()
    header_path = Path(args.header).resolve() if args.header else None
    with tempfile.TemporaryDirectory(prefix='vulkanscope-manifest-') as temp_name:
        generated = Path(temp_name) / 'registry_query_manifest.json'
        command = [
            sys.executable, 'tools/generate_vk_registry.py',
            '--registry', str(registry_path),
            '--catalog', 'app/src/main/cpp/registry_query_catalog.h',
            '--coverage', 'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt',
            '--lock', 'registry/registry_lock.json',
            '--out', str(generated),
            '--require-complete-extension-coverage'
        ]
        if header_path:
            command += ['--header', str(header_path)]
        run(command)
        expected = json.loads((root / 'registry/generated/registry_query_manifest.json').read_text(encoding='utf-8'))
        actual = json.loads(generated.read_text(encoding='utf-8'))
        if actual != expected:
            raise SystemExit('regenerated locked registry manifest differs from checked-in manifest')
    if args.strict_upstream and not header_path:
        raise SystemExit('strict upstream header gate requires --header')
    if header_path:
        run([sys.executable, 'tools/verify_canonical_vulkan_headers.py', str(header_path)])
        run([sys.executable, 'tools/verify_registry_catalog.py', 'registry/generated/registry_query_manifest.json', str(header_path), 'app/src/main/cpp/registry_query_catalog.h'])
        run([sys.executable, 'tools/verify_extension_field_coverage.py', '--registry', str(registry_path), '--header', str(header_path)])
        run([sys.executable, 'tools/verify_upstream_registry.py', '--registry', str(registry_path), '--header', str(header_path)])
        print('PASS strict locked-header verification')
    else:
        print('INFO strict canonical Vulkan header byte-level verification NOT EXECUTED; registry-level verification used the bundled SHA-256-locked vk.xml')
    for path in sorted((root / 'tools').glob('*.py')):
        compile(path.read_text(encoding='utf-8'), str(path), 'exec')
    for path in sorted((root / 'registry/generated').glob('*.json')) + [root / 'registry/registry_lock.json', root / 'registry/video_registry_lock.json'] + sorted((root / 'tests/golden').glob('*_regression_contract.json')):
        json.loads(path.read_text(encoding='utf-8'))
    print('VulkanScope quality gate 2.0.5: PASS')




def run_2004_quality_gate():
    commands = [
        [sys.executable, 'tools/verify_release_2004.py'],
        [sys.executable, 'tools/test_release_2004_state_machine.py'],
        [sys.executable, 'tools/test_release_2004_negative_mutations.py'],
        [sys.executable, 'tools/verify_cmake_registry_lock.py'],
        [sys.executable, 'tools/verify_registry_snapshot.py'],
        [sys.executable, 'tools/verify_compile_regressions.py'],
        [sys.executable, 'tools/verify_spec_regressions.py'],
        [sys.executable, 'tools/verify_probe_publication_handshake.py'],
        [sys.executable, 'tools/verify_probe_terminal_ownership.py'],
        [sys.executable, 'tools/verify_probe_lifecycle_regressions.py'],
        [sys.executable, 'tools/verify_probe_timeout_recovery.py'],
        [sys.executable, 'tools/verify_probe_cancellation_recovery.py'],
        [sys.executable, 'tools/verify_report_semantics_04140.py'],
        [sys.executable, 'tools/verify_report_surface_integrity_04141.py'],
        [sys.executable, 'tools/verify_profile_requirements_04144.py'],
        [sys.executable, 'tools/test_profile_evaluator_state_machine.py'],
        [sys.executable, 'tools/test_profile_requirements_negative_mutations.py'],
        [sys.executable, 'tools/verify_video_registry_04145.py'],
        [sys.executable, 'tools/test_video_profile_census_state_machine.py'],
        [sys.executable, 'tools/verify_full_hardening_0800.py'],
        [sys.executable, 'tools/test_full_hardening_0800_state_machine.py'],
        [sys.executable, 'tools/verify_concurrency_resource_contracts.py'],
        [sys.executable, 'tools/verify_package_reproducibility.py'],
    ]
    for command in commands:
        run(command)
    registry_path = Path(args.registry).resolve() if args.registry else (root / lock['bundledRegistryPath']).resolve()
    header_path = Path(args.header).resolve() if args.header else None
    with tempfile.TemporaryDirectory(prefix='vulkanscope-manifest-') as temp_name:
        generated = Path(temp_name) / 'registry_query_manifest.json'
        command = [
            sys.executable, 'tools/generate_vk_registry.py',
            '--registry', str(registry_path),
            '--catalog', 'app/src/main/cpp/registry_query_catalog.h',
            '--coverage', 'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt',
            '--lock', 'registry/registry_lock.json',
            '--out', str(generated),
            '--require-complete-extension-coverage'
        ]
        if header_path:
            command += ['--header', str(header_path)]
        run(command)
        expected = json.loads((root / 'registry/generated/registry_query_manifest.json').read_text(encoding='utf-8'))
        actual = json.loads(generated.read_text(encoding='utf-8'))
        if actual != expected:
            raise SystemExit('regenerated locked registry manifest differs from checked-in manifest')
    if args.strict_upstream and not header_path:
        raise SystemExit('strict upstream header gate requires --header')
    if header_path:
        run([sys.executable, 'tools/verify_canonical_vulkan_headers.py', str(header_path)])
        run([sys.executable, 'tools/verify_registry_catalog.py', 'registry/generated/registry_query_manifest.json', str(header_path), 'app/src/main/cpp/registry_query_catalog.h'])
        run([sys.executable, 'tools/verify_extension_field_coverage.py', '--registry', str(registry_path), '--header', str(header_path)])
        run([sys.executable, 'tools/verify_upstream_registry.py', '--registry', str(registry_path), '--header', str(header_path)])
        print('PASS strict locked-header verification')
    else:
        print('INFO strict canonical Vulkan header byte-level verification NOT EXECUTED; registry-level verification used the bundled SHA-256-locked vk.xml')
    for path in sorted((root / 'tools').glob('*.py')):
        compile(path.read_text(encoding='utf-8'), str(path), 'exec')
    for path in sorted((root / 'registry/generated').glob('*.json')) + [root / 'registry/registry_lock.json', root / 'registry/video_registry_lock.json'] + sorted((root / 'tests/golden').glob('*_regression_contract.json')):
        json.loads(path.read_text(encoding='utf-8'))
    print('VulkanScope quality gate 2.0.4: PASS')




def run_2003_quality_gate():
    commands = [
        [sys.executable, 'tools/verify_release_2003.py'],
        [sys.executable, 'tools/test_release_2003_state_machine.py'],
        [sys.executable, 'tools/test_release_2003_negative_mutations.py'],
        [sys.executable, 'tools/verify_cmake_registry_lock.py'],
        [sys.executable, 'tools/verify_registry_snapshot.py'],
        [sys.executable, 'tools/verify_compile_regressions.py'],
        [sys.executable, 'tools/verify_spec_regressions.py'],
        [sys.executable, 'tools/verify_probe_publication_handshake.py'],
        [sys.executable, 'tools/verify_probe_terminal_ownership.py'],
        [sys.executable, 'tools/verify_probe_lifecycle_regressions.py'],
        [sys.executable, 'tools/verify_probe_timeout_recovery.py'],
        [sys.executable, 'tools/verify_probe_cancellation_recovery.py'],
        [sys.executable, 'tools/verify_report_semantics_04140.py'],
        [sys.executable, 'tools/verify_report_surface_integrity_04141.py'],
        [sys.executable, 'tools/verify_profile_requirements_04144.py'],
        [sys.executable, 'tools/test_profile_evaluator_state_machine.py'],
        [sys.executable, 'tools/test_profile_requirements_negative_mutations.py'],
        [sys.executable, 'tools/verify_video_registry_04145.py'],
        [sys.executable, 'tools/test_video_profile_census_state_machine.py'],
        [sys.executable, 'tools/verify_full_hardening_0800.py'],
        [sys.executable, 'tools/test_full_hardening_0800_state_machine.py'],
        [sys.executable, 'tools/verify_concurrency_resource_contracts.py'],
        [sys.executable, 'tools/verify_package_reproducibility.py'],
    ]
    for command in commands:
        run(command)
    registry_path = Path(args.registry).resolve() if args.registry else (root / lock['bundledRegistryPath']).resolve()
    header_path = Path(args.header).resolve() if args.header else None
    with tempfile.TemporaryDirectory(prefix='vulkanscope-manifest-') as temp_name:
        generated = Path(temp_name) / 'registry_query_manifest.json'
        command = [
            sys.executable, 'tools/generate_vk_registry.py',
            '--registry', str(registry_path),
            '--catalog', 'app/src/main/cpp/registry_query_catalog.h',
            '--coverage', 'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt',
            '--lock', 'registry/registry_lock.json',
            '--out', str(generated),
            '--require-complete-extension-coverage'
        ]
        if header_path:
            command += ['--header', str(header_path)]
        run(command)
        expected = json.loads((root / 'registry/generated/registry_query_manifest.json').read_text(encoding='utf-8'))
        actual = json.loads(generated.read_text(encoding='utf-8'))
        if actual != expected:
            raise SystemExit('regenerated locked registry manifest differs from checked-in manifest')
    if args.strict_upstream and not header_path:
        raise SystemExit('strict upstream header gate requires --header')
    if header_path:
        run([sys.executable, 'tools/verify_canonical_vulkan_headers.py', str(header_path)])
        run([sys.executable, 'tools/verify_registry_catalog.py', 'registry/generated/registry_query_manifest.json', str(header_path), 'app/src/main/cpp/registry_query_catalog.h'])
        run([sys.executable, 'tools/verify_extension_field_coverage.py', '--registry', str(registry_path), '--header', str(header_path)])
        run([sys.executable, 'tools/verify_upstream_registry.py', '--registry', str(registry_path), '--header', str(header_path)])
        print('PASS strict locked-header verification')
    else:
        print('INFO strict canonical Vulkan header byte-level verification NOT EXECUTED; registry-level verification used the bundled SHA-256-locked vk.xml')
    for path in sorted((root / 'tools').glob('*.py')):
        compile(path.read_text(encoding='utf-8'), str(path), 'exec')
    for path in sorted((root / 'registry/generated').glob('*.json')) + [root / 'registry/registry_lock.json', root / 'registry/video_registry_lock.json'] + sorted((root / 'tests/golden').glob('*_regression_contract.json')):
        json.loads(path.read_text(encoding='utf-8'))
    print('VulkanScope quality gate 2.0.3: PASS')



def run_2002_quality_gate():
    commands = [
        [sys.executable, 'tools/verify_release_2002.py'],
        [sys.executable, 'tools/test_release_2002_state_machine.py'],
        [sys.executable, 'tools/test_release_2002_negative_mutations.py'],
        [sys.executable, 'tools/verify_cmake_registry_lock.py'],
        [sys.executable, 'tools/verify_registry_snapshot.py'],
        [sys.executable, 'tools/verify_compile_regressions.py'],
        [sys.executable, 'tools/verify_spec_regressions.py'],
        [sys.executable, 'tools/verify_probe_publication_handshake.py'],
        [sys.executable, 'tools/verify_probe_terminal_ownership.py'],
        [sys.executable, 'tools/verify_probe_lifecycle_regressions.py'],
        [sys.executable, 'tools/verify_probe_timeout_recovery.py'],
        [sys.executable, 'tools/verify_probe_cancellation_recovery.py'],
        [sys.executable, 'tools/verify_report_semantics_04140.py'],
        [sys.executable, 'tools/verify_report_surface_integrity_04141.py'],
        [sys.executable, 'tools/verify_profile_requirements_04144.py'],
        [sys.executable, 'tools/test_profile_evaluator_state_machine.py'],
        [sys.executable, 'tools/test_profile_requirements_negative_mutations.py'],
        [sys.executable, 'tools/verify_video_registry_04145.py'],
        [sys.executable, 'tools/test_video_profile_census_state_machine.py'],
        [sys.executable, 'tools/verify_full_hardening_0800.py'],
        [sys.executable, 'tools/test_full_hardening_0800_state_machine.py'],
        [sys.executable, 'tools/verify_concurrency_resource_contracts.py'],
        [sys.executable, 'tools/verify_package_reproducibility.py'],
    ]
    for command in commands:
        run(command)
    registry_path = Path(args.registry).resolve() if args.registry else (root / lock['bundledRegistryPath']).resolve()
    header_path = Path(args.header).resolve() if args.header else None
    with tempfile.TemporaryDirectory(prefix='vulkanscope-manifest-') as temp_name:
        generated = Path(temp_name) / 'registry_query_manifest.json'
        command = [
            sys.executable, 'tools/generate_vk_registry.py',
            '--registry', str(registry_path),
            '--catalog', 'app/src/main/cpp/registry_query_catalog.h',
            '--coverage', 'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt',
            '--lock', 'registry/registry_lock.json',
            '--out', str(generated),
            '--require-complete-extension-coverage'
        ]
        if header_path:
            command += ['--header', str(header_path)]
        run(command)
        expected = json.loads((root / 'registry/generated/registry_query_manifest.json').read_text(encoding='utf-8'))
        actual = json.loads(generated.read_text(encoding='utf-8'))
        if actual != expected:
            raise SystemExit('regenerated locked registry manifest differs from checked-in manifest')
    if args.strict_upstream and not header_path:
        raise SystemExit('strict upstream header gate requires --header')
    if header_path:
        run([sys.executable, 'tools/verify_canonical_vulkan_headers.py', str(header_path)])
        run([sys.executable, 'tools/verify_registry_catalog.py', 'registry/generated/registry_query_manifest.json', str(header_path), 'app/src/main/cpp/registry_query_catalog.h'])
        run([sys.executable, 'tools/verify_extension_field_coverage.py', '--registry', str(registry_path), '--header', str(header_path)])
        run([sys.executable, 'tools/verify_upstream_registry.py', '--registry', str(registry_path), '--header', str(header_path)])
        print('PASS strict locked-header verification')
    else:
        print('INFO strict canonical Vulkan header byte-level verification NOT EXECUTED; registry-level verification used the bundled SHA-256-locked vk.xml')
    for path in sorted((root / 'tools').glob('*.py')):
        compile(path.read_text(encoding='utf-8'), str(path), 'exec')
    for path in sorted((root / 'registry/generated').glob('*.json')) + [root / 'registry/registry_lock.json', root / 'registry/video_registry_lock.json'] + sorted((root / 'tests/golden').glob('*_regression_contract.json')):
        json.loads(path.read_text(encoding='utf-8'))
    print('VulkanScope quality gate 2.0.2: PASS')


def run_2000_quality_gate():
    commands = [
        [sys.executable, 'tools/verify_release_2000.py'],
        [sys.executable, 'tools/test_release_2000_state_machine.py'],
        [sys.executable, 'tools/test_release_2000_negative_mutations.py'],
        [sys.executable, 'tools/verify_cmake_registry_lock.py'],
        [sys.executable, 'tools/verify_registry_snapshot.py'],
        [sys.executable, 'tools/verify_compile_regressions.py'],
        [sys.executable, 'tools/verify_spec_regressions.py'],
        [sys.executable, 'tools/verify_probe_publication_handshake.py'],
        [sys.executable, 'tools/verify_probe_terminal_ownership.py'],
        [sys.executable, 'tools/verify_probe_lifecycle_regressions.py'],
        [sys.executable, 'tools/verify_probe_timeout_recovery.py'],
        [sys.executable, 'tools/verify_probe_cancellation_recovery.py'],
        [sys.executable, 'tools/verify_report_semantics_04140.py'],
        [sys.executable, 'tools/verify_report_surface_integrity_04141.py'],
        [sys.executable, 'tools/verify_profile_requirements_04144.py'],
        [sys.executable, 'tools/test_profile_evaluator_state_machine.py'],
        [sys.executable, 'tools/test_profile_requirements_negative_mutations.py'],
        [sys.executable, 'tools/verify_video_registry_04145.py'],
        [sys.executable, 'tools/test_video_profile_census_state_machine.py'],
        [sys.executable, 'tools/verify_full_hardening_0800.py'],
        [sys.executable, 'tools/test_full_hardening_0800_state_machine.py'],
        [sys.executable, 'tools/verify_concurrency_resource_contracts.py'],
        [sys.executable, 'tools/verify_package_reproducibility.py'],
    ]
    for command in commands:
        run(command)
    registry_path = Path(args.registry).resolve() if args.registry else (root / lock['bundledRegistryPath']).resolve()
    header_path = Path(args.header).resolve() if args.header else None
    with tempfile.TemporaryDirectory(prefix='vulkanscope-manifest-') as temp_name:
        generated = Path(temp_name) / 'registry_query_manifest.json'
        command = [
            sys.executable, 'tools/generate_vk_registry.py',
            '--registry', str(registry_path),
            '--catalog', 'app/src/main/cpp/registry_query_catalog.h',
            '--coverage', 'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt',
            '--lock', 'registry/registry_lock.json',
            '--out', str(generated),
            '--require-complete-extension-coverage'
        ]
        if header_path:
            command += ['--header', str(header_path)]
        run(command)
        expected = json.loads((root / 'registry/generated/registry_query_manifest.json').read_text(encoding='utf-8'))
        actual = json.loads(generated.read_text(encoding='utf-8'))
        if actual != expected:
            raise SystemExit('regenerated locked registry manifest differs from checked-in manifest')
    if args.strict_upstream and not header_path:
        raise SystemExit('strict upstream header gate requires --header')
    if header_path:
        run([sys.executable, 'tools/verify_canonical_vulkan_headers.py', str(header_path)])
        run([sys.executable, 'tools/verify_registry_catalog.py', 'registry/generated/registry_query_manifest.json', str(header_path), 'app/src/main/cpp/registry_query_catalog.h'])
        run([sys.executable, 'tools/verify_extension_field_coverage.py', '--registry', str(registry_path), '--header', str(header_path)])
        run([sys.executable, 'tools/verify_upstream_registry.py', '--registry', str(registry_path), '--header', str(header_path)])
        print('PASS strict locked-header verification')
    else:
        print('INFO strict canonical Vulkan header byte-level verification NOT EXECUTED; registry-level verification used the bundled SHA-256-locked vk.xml')
    for path in sorted((root / 'tools').glob('*.py')):
        compile(path.read_text(encoding='utf-8'), str(path), 'exec')
    for path in sorted((root / 'registry/generated').glob('*.json')) + [root / 'registry/registry_lock.json', root / 'registry/video_registry_lock.json'] + sorted((root / 'tests/golden').glob('*_regression_contract.json')):
        json.loads(path.read_text(encoding='utf-8'))
    print('VulkanScope quality gate 2.0.0: PASS')


def run_1502_quality_gate():
    commands = [
        [sys.executable, 'tools/verify_release_1502.py'],
        [sys.executable, 'tools/test_release_1502_state_machine.py'],
        [sys.executable, 'tools/test_release_1502_negative_mutations.py'],
                        [sys.executable, 'tools/verify_cmake_registry_lock.py'],
        [sys.executable, 'tools/verify_registry_snapshot.py'],
        [sys.executable, 'tools/verify_compile_regressions.py'],
        [sys.executable, 'tools/verify_spec_regressions.py'],
        [sys.executable, 'tools/verify_probe_lifecycle_regressions.py'],
        [sys.executable, 'tools/verify_probe_timeout_recovery.py'],
        [sys.executable, 'tools/verify_probe_cancellation_recovery.py'],
        [sys.executable, 'tools/verify_report_semantics_04140.py'],
        [sys.executable, 'tools/verify_report_surface_integrity_04141.py'],
        [sys.executable, 'tools/verify_profile_requirements_04144.py'],
        [sys.executable, 'tools/test_profile_evaluator_state_machine.py'],
        [sys.executable, 'tools/test_profile_requirements_negative_mutations.py'],
        [sys.executable, 'tools/verify_video_registry_04145.py'],
        [sys.executable, 'tools/test_video_profile_census_state_machine.py'],
        [sys.executable, 'tools/verify_full_hardening_0800.py'],
        [sys.executable, 'tools/test_full_hardening_0800_state_machine.py'],
        [sys.executable, 'tools/verify_concurrency_resource_contracts.py'],
        [sys.executable, 'tools/verify_package_reproducibility.py'],
    ]
    for command in commands:
        run(command)
    registry_path = Path(args.registry).resolve() if args.registry else (root / lock['bundledRegistryPath']).resolve()
    header_path = Path(args.header).resolve() if args.header else None
    with tempfile.TemporaryDirectory(prefix='vulkanscope-manifest-') as temp_name:
        generated = Path(temp_name) / 'registry_query_manifest.json'
        command = [
            sys.executable, 'tools/generate_vk_registry.py',
            '--registry', str(registry_path),
            '--catalog', 'app/src/main/cpp/registry_query_catalog.h',
            '--coverage', 'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt',
            '--lock', 'registry/registry_lock.json',
            '--out', str(generated),
            '--require-complete-extension-coverage'
        ]
        if header_path:
            command += ['--header', str(header_path)]
        run(command)
        expected = json.loads((root / 'registry/generated/registry_query_manifest.json').read_text(encoding='utf-8'))
        actual = json.loads(generated.read_text(encoding='utf-8'))
        if actual != expected:
            raise SystemExit('regenerated locked registry manifest differs from checked-in manifest')
    if args.strict_upstream and not header_path:
        raise SystemExit('strict upstream header gate requires --header')
    if header_path:
        run([sys.executable, 'tools/verify_canonical_vulkan_headers.py', str(header_path)])
        run([sys.executable, 'tools/verify_registry_catalog.py', 'registry/generated/registry_query_manifest.json', str(header_path), 'app/src/main/cpp/registry_query_catalog.h'])
        run([sys.executable, 'tools/verify_extension_field_coverage.py', '--registry', str(registry_path), '--header', str(header_path)])
        run([sys.executable, 'tools/verify_upstream_registry.py', '--registry', str(registry_path), '--header', str(header_path)])
        print('PASS strict locked-header verification')
    else:
        print('INFO strict canonical Vulkan header byte-level verification NOT EXECUTED; registry-level verification used the bundled SHA-256-locked vk.xml')
    for path in sorted((root / 'tools').glob('*.py')):
        compile(path.read_text(encoding='utf-8'), str(path), 'exec')
    for path in sorted((root / 'registry/generated').glob('*.json')) + [root / 'registry/registry_lock.json', root / 'registry/video_registry_lock.json'] + sorted((root / 'tests/golden').glob('*_regression_contract.json')):
        json.loads(path.read_text(encoding='utf-8'))
    print('VulkanScope quality gate 1.5.2: PASS')


def run_1501_quality_gate():
    commands = [
        [sys.executable, 'tools/verify_release_1501.py'],
        [sys.executable, 'tools/test_release_1501_state_machine.py'],
        [sys.executable, 'tools/test_release_1501_negative_mutations.py'],
        [sys.executable, 'tools/verify_cmake_registry_lock.py'],
        [sys.executable, 'tools/verify_registry_snapshot.py'],
        [sys.executable, 'tools/verify_compile_regressions.py'],
        [sys.executable, 'tools/verify_spec_regressions.py'],
        [sys.executable, 'tools/verify_probe_lifecycle_regressions.py'],
        [sys.executable, 'tools/verify_probe_timeout_recovery.py'],
        [sys.executable, 'tools/verify_probe_cancellation_recovery.py'],
        [sys.executable, 'tools/verify_report_semantics_04140.py'],
        [sys.executable, 'tools/verify_report_surface_integrity_04141.py'],
        [sys.executable, 'tools/verify_profile_requirements_04144.py'],
        [sys.executable, 'tools/test_profile_evaluator_state_machine.py'],
        [sys.executable, 'tools/test_profile_requirements_negative_mutations.py'],
        [sys.executable, 'tools/verify_video_registry_04145.py'],
        [sys.executable, 'tools/test_video_profile_census_state_machine.py'],
        [sys.executable, 'tools/verify_full_hardening_0800.py'],
        [sys.executable, 'tools/test_full_hardening_0800_state_machine.py'],
        [sys.executable, 'tools/verify_concurrency_resource_contracts.py'],
        [sys.executable, 'tools/verify_package_reproducibility.py'],
    ]
    for command in commands:
        run(command)
    registry_path = Path(args.registry).resolve() if args.registry else (root / lock['bundledRegistryPath']).resolve()
    header_path = Path(args.header).resolve() if args.header else None
    with tempfile.TemporaryDirectory(prefix='vulkanscope-manifest-') as temp_name:
        generated = Path(temp_name) / 'registry_query_manifest.json'
        command = [
            sys.executable, 'tools/generate_vk_registry.py',
            '--registry', str(registry_path),
            '--catalog', 'app/src/main/cpp/registry_query_catalog.h',
            '--coverage', 'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt',
            '--lock', 'registry/registry_lock.json',
            '--out', str(generated),
            '--require-complete-extension-coverage'
        ]
        if header_path:
            command += ['--header', str(header_path)]
        run(command)
        expected = json.loads((root / 'registry/generated/registry_query_manifest.json').read_text(encoding='utf-8'))
        actual = json.loads(generated.read_text(encoding='utf-8'))
        if actual != expected:
            raise SystemExit('regenerated locked registry manifest differs from checked-in manifest')
    if args.strict_upstream and not header_path:
        raise SystemExit('strict upstream header gate requires --header')
    if header_path:
        run([sys.executable, 'tools/verify_canonical_vulkan_headers.py', str(header_path)])
        run([sys.executable, 'tools/verify_registry_catalog.py', 'registry/generated/registry_query_manifest.json', str(header_path), 'app/src/main/cpp/registry_query_catalog.h'])
        run([sys.executable, 'tools/verify_extension_field_coverage.py', '--registry', str(registry_path), '--header', str(header_path)])
        run([sys.executable, 'tools/verify_upstream_registry.py', '--registry', str(registry_path), '--header', str(header_path)])
        print('PASS strict locked-header verification')
    else:
        print('INFO strict canonical Vulkan header byte-level verification NOT EXECUTED; registry-level verification used the bundled SHA-256-locked vk.xml')
    for path in sorted((root / 'tools').glob('*.py')):
        compile(path.read_text(encoding='utf-8'), str(path), 'exec')
    for path in sorted((root / 'registry/generated').glob('*.json')) + [root / 'registry/registry_lock.json', root / 'registry/video_registry_lock.json'] + sorted((root / 'tests/golden').glob('*_regression_contract.json')):
        json.loads(path.read_text(encoding='utf-8'))
    print('VulkanScope quality gate 1.5.1: PASS')


def run_1500_quality_gate():
    commands = [
        [sys.executable, 'tools/verify_release_1500.py'],
        [sys.executable, 'tools/test_release_1500_state_machine.py'],
        [sys.executable, 'tools/test_release_1500_negative_mutations.py'],
        [sys.executable, 'tools/verify_release_1500_regression.py'],
        [sys.executable, 'tools/verify_cmake_registry_lock.py'],
        [sys.executable, 'tools/verify_registry_snapshot.py'],
        [sys.executable, 'tools/verify_compile_regressions.py'],
        [sys.executable, 'tools/verify_spec_regressions.py'],
        [sys.executable, 'tools/verify_probe_lifecycle_regressions.py'],
        [sys.executable, 'tools/verify_probe_timeout_recovery.py'],
        [sys.executable, 'tools/verify_probe_cancellation_recovery.py'],
        [sys.executable, 'tools/verify_report_semantics_04140.py'],
        [sys.executable, 'tools/verify_report_surface_integrity_04141.py'],
        [sys.executable, 'tools/verify_profile_requirements_04144.py'],
        [sys.executable, 'tools/test_profile_evaluator_state_machine.py'],
        [sys.executable, 'tools/test_profile_requirements_negative_mutations.py'],
        [sys.executable, 'tools/verify_video_registry_04145.py'],
        [sys.executable, 'tools/test_video_profile_census_state_machine.py'],
        [sys.executable, 'tools/verify_full_hardening_0800.py'],
        [sys.executable, 'tools/test_full_hardening_0800_state_machine.py'],
        [sys.executable, 'tools/verify_concurrency_resource_contracts.py'],
        [sys.executable, 'tools/verify_package_reproducibility.py'],
    ]
    for command in commands:
        run(command)
    registry_path = Path(args.registry).resolve() if args.registry else (root / lock['bundledRegistryPath']).resolve()
    header_path = Path(args.header).resolve() if args.header else None
    with tempfile.TemporaryDirectory(prefix='vulkanscope-manifest-') as temp_name:
        generated = Path(temp_name) / 'registry_query_manifest.json'
        command = [
            sys.executable, 'tools/generate_vk_registry.py',
            '--registry', str(registry_path),
            '--catalog', 'app/src/main/cpp/registry_query_catalog.h',
            '--coverage', 'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt',
            '--lock', 'registry/registry_lock.json',
            '--out', str(generated),
            '--require-complete-extension-coverage'
        ]
        if header_path:
            command += ['--header', str(header_path)]
        run(command)
        expected = json.loads((root / 'registry/generated/registry_query_manifest.json').read_text(encoding='utf-8'))
        actual = json.loads(generated.read_text(encoding='utf-8'))
        if actual != expected:
            raise SystemExit('regenerated locked registry manifest differs from checked-in manifest')
    if args.strict_upstream and not header_path:
        raise SystemExit('strict upstream header gate requires --header')
    if header_path:
        run([sys.executable, 'tools/verify_canonical_vulkan_headers.py', str(header_path)])
        run([sys.executable, 'tools/verify_registry_catalog.py', 'registry/generated/registry_query_manifest.json', str(header_path), 'app/src/main/cpp/registry_query_catalog.h'])
        run([sys.executable, 'tools/verify_extension_field_coverage.py', '--registry', str(registry_path), '--header', str(header_path)])
        run([sys.executable, 'tools/verify_upstream_registry.py', '--registry', str(registry_path), '--header', str(header_path)])
        print('PASS strict locked-header verification')
    else:
        print('INFO strict canonical Vulkan header byte-level verification NOT EXECUTED; registry-level verification used the bundled SHA-256-locked vk.xml')
    for path in sorted((root / 'tools').glob('*.py')):
        compile(path.read_text(encoding='utf-8'), str(path), 'exec')
    for path in sorted((root / 'registry/generated').glob('*.json')) + [root / 'registry/registry_lock.json', root / 'registry/video_registry_lock.json'] + sorted((root / 'tests/golden').glob('*_regression_contract.json')):
        json.loads(path.read_text(encoding='utf-8'))
    print('VulkanScope quality gate 1.5.0: PASS')



def run_1416_quality_gate():
    commands = [
        [sys.executable, 'tools/verify_release_1416.py'],
        [sys.executable, 'tools/test_release_1416_state_machine.py'],
        [sys.executable, 'tools/test_release_1416_negative_mutations.py'],
        [sys.executable, 'tools/verify_release_1416_regression.py'],
        [sys.executable, 'tools/verify_cmake_registry_lock.py'],
        [sys.executable, 'tools/verify_registry_snapshot.py'],
        [sys.executable, 'tools/verify_compile_regressions.py'],
        [sys.executable, 'tools/verify_spec_regressions.py'],
        [sys.executable, 'tools/verify_probe_lifecycle_regressions.py'],
        [sys.executable, 'tools/verify_probe_timeout_recovery.py'],
        [sys.executable, 'tools/verify_probe_cancellation_recovery.py'],
        [sys.executable, 'tools/verify_report_semantics_04140.py'],
        [sys.executable, 'tools/verify_report_surface_integrity_04141.py'],
        [sys.executable, 'tools/verify_profile_requirements_04144.py'],
        [sys.executable, 'tools/test_profile_evaluator_state_machine.py'],
        [sys.executable, 'tools/test_profile_requirements_negative_mutations.py'],
        [sys.executable, 'tools/verify_video_registry_04145.py'],
        [sys.executable, 'tools/test_video_profile_census_state_machine.py'],
        [sys.executable, 'tools/verify_full_hardening_0800.py'],
        [sys.executable, 'tools/test_full_hardening_0800_state_machine.py'],
        [sys.executable, 'tools/verify_concurrency_resource_contracts.py'],
        [sys.executable, 'tools/verify_package_reproducibility.py'],
    ]
    for command in commands:
        run(command)
    registry_path = Path(args.registry).resolve() if args.registry else (root / lock['bundledRegistryPath']).resolve()
    header_path = Path(args.header).resolve() if args.header else None
    with tempfile.TemporaryDirectory(prefix='vulkanscope-manifest-') as temp_name:
        generated = Path(temp_name) / 'registry_query_manifest.json'
        command = [
            sys.executable, 'tools/generate_vk_registry.py',
            '--registry', str(registry_path),
            '--catalog', 'app/src/main/cpp/registry_query_catalog.h',
            '--coverage', 'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt',
            '--lock', 'registry/registry_lock.json',
            '--out', str(generated),
            '--require-complete-extension-coverage'
        ]
        if header_path:
            command += ['--header', str(header_path)]
        run(command)
        expected = json.loads((root / 'registry/generated/registry_query_manifest.json').read_text(encoding='utf-8'))
        actual = json.loads(generated.read_text(encoding='utf-8'))
        if actual != expected:
            raise SystemExit('regenerated locked registry manifest differs from checked-in manifest')
    if args.strict_upstream and not header_path:
        raise SystemExit('strict upstream header gate requires --header')
    if header_path:
        run([sys.executable, 'tools/verify_canonical_vulkan_headers.py', str(header_path)])
        run([sys.executable, 'tools/verify_registry_catalog.py', 'registry/generated/registry_query_manifest.json', str(header_path), 'app/src/main/cpp/registry_query_catalog.h'])
        run([sys.executable, 'tools/verify_extension_field_coverage.py', '--registry', str(registry_path), '--header', str(header_path)])
        run([sys.executable, 'tools/verify_upstream_registry.py', '--registry', str(registry_path), '--header', str(header_path)])
        print('PASS strict locked-header verification')
    else:
        print('INFO strict canonical Vulkan header byte-level verification NOT EXECUTED; registry-level verification used the bundled SHA-256-locked vk.xml')
    for path in sorted((root / 'tools').glob('*.py')):
        compile(path.read_text(encoding='utf-8'), str(path), 'exec')
    for path in sorted((root / 'registry/generated').glob('*.json')) + [root / 'registry/registry_lock.json', root / 'registry/video_registry_lock.json'] + sorted((root / 'tests/golden').glob('*_regression_contract.json')):
        json.loads(path.read_text(encoding='utf-8'))
    print('VulkanScope quality gate 1.4.16: PASS')


def run_1415_quality_gate():
    commands = [
        [sys.executable, 'tools/verify_release_1415.py'],
        [sys.executable, 'tools/test_release_1415_state_machine.py'],
        [sys.executable, 'tools/test_release_1415_negative_mutations.py'],
        [sys.executable, 'tools/verify_release_1415_regression.py'],
        [sys.executable, 'tools/verify_cmake_registry_lock.py'],
        [sys.executable, 'tools/verify_registry_snapshot.py'],
        [sys.executable, 'tools/verify_compile_regressions.py'],
        [sys.executable, 'tools/verify_spec_regressions.py'],
        [sys.executable, 'tools/verify_probe_lifecycle_regressions.py'],
        [sys.executable, 'tools/verify_probe_timeout_recovery.py'],
        [sys.executable, 'tools/verify_probe_cancellation_recovery.py'],
        [sys.executable, 'tools/verify_report_semantics_04140.py'],
        [sys.executable, 'tools/verify_report_surface_integrity_04141.py'],
        [sys.executable, 'tools/verify_profile_requirements_04144.py'],
        [sys.executable, 'tools/test_profile_evaluator_state_machine.py'],
        [sys.executable, 'tools/test_profile_requirements_negative_mutations.py'],
        [sys.executable, 'tools/verify_video_registry_04145.py'],
        [sys.executable, 'tools/test_video_profile_census_state_machine.py'],
        [sys.executable, 'tools/verify_full_hardening_0800.py'],
        [sys.executable, 'tools/test_full_hardening_0800_state_machine.py'],
        [sys.executable, 'tools/verify_concurrency_resource_contracts.py'],
        [sys.executable, 'tools/verify_package_reproducibility.py'],
    ]
    for command in commands:
        run(command)
    registry_path = Path(args.registry).resolve() if args.registry else (root / lock['bundledRegistryPath']).resolve()
    header_path = Path(args.header).resolve() if args.header else None
    with tempfile.TemporaryDirectory(prefix='vulkanscope-manifest-') as temp_name:
        generated = Path(temp_name) / 'registry_query_manifest.json'
        command = [
            sys.executable, 'tools/generate_vk_registry.py',
            '--registry', str(registry_path),
            '--catalog', 'app/src/main/cpp/registry_query_catalog.h',
            '--coverage', 'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt',
            '--lock', 'registry/registry_lock.json',
            '--out', str(generated),
            '--require-complete-extension-coverage'
        ]
        if header_path:
            command += ['--header', str(header_path)]
        run(command)
        expected = json.loads((root / 'registry/generated/registry_query_manifest.json').read_text(encoding='utf-8'))
        actual = json.loads(generated.read_text(encoding='utf-8'))
        if actual != expected:
            raise SystemExit('regenerated locked registry manifest differs from checked-in manifest')
    if args.strict_upstream and not header_path:
        raise SystemExit('strict upstream header gate requires --header')
    if header_path:
        run([sys.executable, 'tools/verify_canonical_vulkan_headers.py', str(header_path)])
        run([sys.executable, 'tools/verify_registry_catalog.py', 'registry/generated/registry_query_manifest.json', str(header_path), 'app/src/main/cpp/registry_query_catalog.h'])
        run([sys.executable, 'tools/verify_extension_field_coverage.py', '--registry', str(registry_path), '--header', str(header_path)])
        run([sys.executable, 'tools/verify_upstream_registry.py', '--registry', str(registry_path), '--header', str(header_path)])
        print('PASS strict locked-header verification')
    else:
        print('INFO strict canonical Vulkan header byte-level verification NOT EXECUTED; registry-level verification used the bundled SHA-256-locked vk.xml')
    for path in sorted((root / 'tools').glob('*.py')):
        compile(path.read_text(encoding='utf-8'), str(path), 'exec')
    for path in sorted((root / 'registry/generated').glob('*.json')) + [root / 'registry/registry_lock.json', root / 'registry/video_registry_lock.json'] + sorted((root / 'tests/golden').glob('*_regression_contract.json')):
        json.loads(path.read_text(encoding='utf-8'))
    print('VulkanScope quality gate 1.4.15: PASS')


def run_1414_quality_gate():
    commands = [
        [sys.executable, 'tools/verify_release_1414.py'],
        [sys.executable, 'tools/test_release_1414_state_machine.py'],
        [sys.executable, 'tools/test_release_1414_negative_mutations.py'],
        [sys.executable, 'tools/verify_release_1414_regression.py'],
        [sys.executable, 'tools/verify_cmake_registry_lock.py'],
        [sys.executable, 'tools/verify_registry_snapshot.py'],
        [sys.executable, 'tools/verify_compile_regressions.py'],
        [sys.executable, 'tools/verify_spec_regressions.py'],
        [sys.executable, 'tools/verify_probe_lifecycle_regressions.py'],
        [sys.executable, 'tools/verify_probe_timeout_recovery.py'],
        [sys.executable, 'tools/verify_probe_cancellation_recovery.py'],
        [sys.executable, 'tools/verify_report_semantics_04140.py'],
        [sys.executable, 'tools/verify_report_surface_integrity_04141.py'],
        [sys.executable, 'tools/verify_profile_requirements_04144.py'],
        [sys.executable, 'tools/test_profile_evaluator_state_machine.py'],
        [sys.executable, 'tools/test_profile_requirements_negative_mutations.py'],
        [sys.executable, 'tools/verify_video_registry_04145.py'],
        [sys.executable, 'tools/test_video_profile_census_state_machine.py'],
        [sys.executable, 'tools/verify_full_hardening_0800.py'],
        [sys.executable, 'tools/test_full_hardening_0800_state_machine.py'],
        [sys.executable, 'tools/verify_concurrency_resource_contracts.py'],
        [sys.executable, 'tools/verify_package_reproducibility.py'],
    ]
    for command in commands:
        run(command)
    registry_path = Path(args.registry).resolve() if args.registry else (root / lock['bundledRegistryPath']).resolve()
    header_path = Path(args.header).resolve() if args.header else None
    with tempfile.TemporaryDirectory(prefix='vulkanscope-manifest-') as temp_name:
        generated = Path(temp_name) / 'registry_query_manifest.json'
        command = [
            sys.executable, 'tools/generate_vk_registry.py',
            '--registry', str(registry_path),
            '--catalog', 'app/src/main/cpp/registry_query_catalog.h',
            '--coverage', 'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt',
            '--lock', 'registry/registry_lock.json',
            '--out', str(generated),
            '--require-complete-extension-coverage'
        ]
        if header_path:
            command += ['--header', str(header_path)]
        run(command)
        expected = json.loads((root / 'registry/generated/registry_query_manifest.json').read_text(encoding='utf-8'))
        actual = json.loads(generated.read_text(encoding='utf-8'))
        if actual != expected:
            raise SystemExit('regenerated locked registry manifest differs from checked-in manifest')
    if args.strict_upstream and not header_path:
        raise SystemExit('strict upstream header gate requires --header')
    if header_path:
        run([sys.executable, 'tools/verify_canonical_vulkan_headers.py', str(header_path)])
        run([sys.executable, 'tools/verify_registry_catalog.py', 'registry/generated/registry_query_manifest.json', str(header_path), 'app/src/main/cpp/registry_query_catalog.h'])
        run([sys.executable, 'tools/verify_extension_field_coverage.py', '--registry', str(registry_path), '--header', str(header_path)])
        run([sys.executable, 'tools/verify_upstream_registry.py', '--registry', str(registry_path), '--header', str(header_path)])
        print('PASS strict locked-header verification')
    else:
        print('INFO strict canonical Vulkan header byte-level verification NOT EXECUTED; registry-level verification used the bundled SHA-256-locked vk.xml')
    for path in sorted((root / 'tools').glob('*.py')):
        compile(path.read_text(encoding='utf-8'), str(path), 'exec')
    for path in sorted((root / 'registry/generated').glob('*.json')) + [root / 'registry/registry_lock.json', root / 'registry/video_registry_lock.json'] + sorted((root / 'tests/golden').glob('*_regression_contract.json')):
        json.loads(path.read_text(encoding='utf-8'))
    print('VulkanScope quality gate 1.4.14: PASS')


def run_1413_quality_gate():
    commands = [
        [sys.executable, 'tools/verify_release_1413.py'],
        [sys.executable, 'tools/test_release_1413_state_machine.py'],
        [sys.executable, 'tools/test_release_1413_negative_mutations.py'],
        [sys.executable, 'tools/verify_release_1413_regression.py'],
        [sys.executable, 'tools/verify_cmake_registry_lock.py'],
        [sys.executable, 'tools/verify_registry_snapshot.py'],
        [sys.executable, 'tools/verify_compile_regressions.py'],
        [sys.executable, 'tools/verify_spec_regressions.py'],
        [sys.executable, 'tools/verify_probe_lifecycle_regressions.py'],
        [sys.executable, 'tools/verify_probe_timeout_recovery.py'],
        [sys.executable, 'tools/verify_probe_cancellation_recovery.py'],
        [sys.executable, 'tools/verify_report_semantics_04140.py'],
        [sys.executable, 'tools/verify_report_surface_integrity_04141.py'],
        [sys.executable, 'tools/verify_profile_requirements_04144.py'],
        [sys.executable, 'tools/test_profile_evaluator_state_machine.py'],
        [sys.executable, 'tools/test_profile_requirements_negative_mutations.py'],
        [sys.executable, 'tools/verify_video_registry_04145.py'],
        [sys.executable, 'tools/test_video_profile_census_state_machine.py'],
        [sys.executable, 'tools/verify_full_hardening_0800.py'],
        [sys.executable, 'tools/test_full_hardening_0800_state_machine.py'],
        [sys.executable, 'tools/verify_concurrency_resource_contracts.py'],
        [sys.executable, 'tools/verify_package_reproducibility.py'],
    ]
    for command in commands:
        run(command)
    registry_path = Path(args.registry).resolve() if args.registry else (root / lock['bundledRegistryPath']).resolve()
    header_path = Path(args.header).resolve() if args.header else None
    with tempfile.TemporaryDirectory(prefix='vulkanscope-manifest-') as temp_name:
        generated = Path(temp_name) / 'registry_query_manifest.json'
        command = [
            sys.executable, 'tools/generate_vk_registry.py',
            '--registry', str(registry_path),
            '--catalog', 'app/src/main/cpp/registry_query_catalog.h',
            '--coverage', 'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt',
            '--lock', 'registry/registry_lock.json',
            '--out', str(generated),
            '--require-complete-extension-coverage'
        ]
        if header_path:
            command += ['--header', str(header_path)]
        run(command)
        expected = json.loads((root / 'registry/generated/registry_query_manifest.json').read_text(encoding='utf-8'))
        actual = json.loads(generated.read_text(encoding='utf-8'))
        if actual != expected:
            raise SystemExit('regenerated locked registry manifest differs from checked-in manifest')
    if args.strict_upstream and not header_path:
        raise SystemExit('strict upstream header gate requires --header')
    if header_path:
        run([sys.executable, 'tools/verify_canonical_vulkan_headers.py', str(header_path)])
        run([sys.executable, 'tools/verify_registry_catalog.py', 'registry/generated/registry_query_manifest.json', str(header_path), 'app/src/main/cpp/registry_query_catalog.h'])
        run([sys.executable, 'tools/verify_extension_field_coverage.py', '--registry', str(registry_path), '--header', str(header_path)])
        run([sys.executable, 'tools/verify_upstream_registry.py', '--registry', str(registry_path), '--header', str(header_path)])
        print('PASS strict locked-header verification')
    else:
        print('INFO strict canonical Vulkan header byte-level verification NOT EXECUTED; registry-level verification used the bundled SHA-256-locked vk.xml')
    for path in sorted((root / 'tools').glob('*.py')):
        compile(path.read_text(encoding='utf-8'), str(path), 'exec')
    for path in sorted((root / 'registry/generated').glob('*.json')) + [root / 'registry/registry_lock.json', root / 'registry/video_registry_lock.json'] + sorted((root / 'tests/golden').glob('*_regression_contract.json')):
        json.loads(path.read_text(encoding='utf-8'))
    print('VulkanScope quality gate 1.4.13: PASS')


def run_1412_quality_gate():
    commands = [
        [sys.executable, 'tools/verify_release_1412.py'],
        [sys.executable, 'tools/test_release_1412_state_machine.py'],
        [sys.executable, 'tools/test_release_1412_negative_mutations.py'],
        [sys.executable, 'tools/verify_release_1412_regression.py'],
        [sys.executable, 'tools/verify_cmake_registry_lock.py'],
        [sys.executable, 'tools/verify_registry_snapshot.py'],
        [sys.executable, 'tools/verify_compile_regressions.py'],
        [sys.executable, 'tools/verify_spec_regressions.py'],
        [sys.executable, 'tools/verify_probe_lifecycle_regressions.py'],
        [sys.executable, 'tools/verify_probe_timeout_recovery.py'],
        [sys.executable, 'tools/verify_probe_cancellation_recovery.py'],
        [sys.executable, 'tools/verify_report_semantics_04140.py'],
        [sys.executable, 'tools/verify_report_surface_integrity_04141.py'],
        [sys.executable, 'tools/verify_profile_requirements_04144.py'],
        [sys.executable, 'tools/test_profile_evaluator_state_machine.py'],
        [sys.executable, 'tools/test_profile_requirements_negative_mutations.py'],
        [sys.executable, 'tools/verify_video_registry_04145.py'],
        [sys.executable, 'tools/test_video_profile_census_state_machine.py'],
        [sys.executable, 'tools/verify_full_hardening_0800.py'],
        [sys.executable, 'tools/test_full_hardening_0800_state_machine.py'],
        [sys.executable, 'tools/verify_concurrency_resource_contracts.py'],
        [sys.executable, 'tools/verify_package_reproducibility.py'],
    ]
    for command in commands:
        run(command)
    registry_path = Path(args.registry).resolve() if args.registry else (root / lock['bundledRegistryPath']).resolve()
    header_path = Path(args.header).resolve() if args.header else None
    with tempfile.TemporaryDirectory(prefix='vulkanscope-manifest-') as temp_name:
        generated = Path(temp_name) / 'registry_query_manifest.json'
        command = [
            sys.executable, 'tools/generate_vk_registry.py',
            '--registry', str(registry_path),
            '--catalog', 'app/src/main/cpp/registry_query_catalog.h',
            '--coverage', 'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt',
            '--lock', 'registry/registry_lock.json',
            '--out', str(generated),
            '--require-complete-extension-coverage'
        ]
        if header_path:
            command += ['--header', str(header_path)]
        run(command)
        expected = json.loads((root / 'registry/generated/registry_query_manifest.json').read_text(encoding='utf-8'))
        actual = json.loads(generated.read_text(encoding='utf-8'))
        if actual != expected:
            raise SystemExit('regenerated locked registry manifest differs from checked-in manifest')
    if args.strict_upstream and not header_path:
        raise SystemExit('strict upstream header gate requires --header')
    if header_path:
        run([sys.executable, 'tools/verify_canonical_vulkan_headers.py', str(header_path)])
        run([sys.executable, 'tools/verify_registry_catalog.py', 'registry/generated/registry_query_manifest.json', str(header_path), 'app/src/main/cpp/registry_query_catalog.h'])
        run([sys.executable, 'tools/verify_extension_field_coverage.py', '--registry', str(registry_path), '--header', str(header_path)])
        run([sys.executable, 'tools/verify_upstream_registry.py', '--registry', str(registry_path), '--header', str(header_path)])
        print('PASS strict locked-header verification')
    else:
        print('INFO strict canonical Vulkan header byte-level verification NOT EXECUTED; registry-level verification used the bundled SHA-256-locked vk.xml')
    for path in sorted((root / 'tools').glob('*.py')):
        compile(path.read_text(encoding='utf-8'), str(path), 'exec')
    for path in sorted((root / 'registry/generated').glob('*.json')) + [root / 'registry/registry_lock.json', root / 'registry/video_registry_lock.json'] + sorted((root / 'tests/golden').glob('*_regression_contract.json')):
        json.loads(path.read_text(encoding='utf-8'))
    print('VulkanScope quality gate 1.4.12: PASS')


def run_1407_quality_gate():
    commands = [
        [sys.executable, 'tools/verify_release_1407.py'],
        [sys.executable, 'tools/test_release_1407_state_machine.py'],
        [sys.executable, 'tools/test_release_1407_negative_mutations.py'],
        [sys.executable, 'tools/verify_cmake_registry_lock.py'],
        [sys.executable, 'tools/verify_registry_snapshot.py'],
        [sys.executable, 'tools/verify_compile_regressions.py'],
        [sys.executable, 'tools/verify_spec_regressions.py'],
        [sys.executable, 'tools/verify_probe_lifecycle_regressions.py'],
        [sys.executable, 'tools/verify_probe_timeout_recovery.py'],
        [sys.executable, 'tools/verify_probe_cancellation_recovery.py'],
        [sys.executable, 'tools/verify_report_semantics_04140.py'],
        [sys.executable, 'tools/verify_report_surface_integrity_04141.py'],
        [sys.executable, 'tools/verify_profile_requirements_04144.py'],
        [sys.executable, 'tools/verify_video_registry_04145.py'],
        [sys.executable, 'tools/test_video_profile_census_state_machine.py'],
        [sys.executable, 'tools/verify_full_hardening_0800.py'],
        [sys.executable, 'tools/test_full_hardening_0800_state_machine.py'],
        [sys.executable, 'tools/verify_concurrency_resource_contracts.py'],
        [sys.executable, 'tools/verify_package_reproducibility.py'],
    ]
    for command in commands:
        run(command)
    registry_path = Path(args.registry).resolve() if args.registry else (root / lock['bundledRegistryPath']).resolve()
    header_path = Path(args.header).resolve() if args.header else None
    with tempfile.TemporaryDirectory(prefix='vulkanscope-manifest-') as temp_name:
        generated = Path(temp_name) / 'registry_query_manifest.json'
        command = [
            sys.executable, 'tools/generate_vk_registry.py',
            '--registry', str(registry_path),
            '--catalog', 'app/src/main/cpp/registry_query_catalog.h',
            '--coverage', 'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt',
            '--lock', 'registry/registry_lock.json',
            '--out', str(generated),
            '--require-complete-extension-coverage'
        ]
        if header_path:
            command += ['--header', str(header_path)]
        run(command)
        expected = json.loads((root / 'registry/generated/registry_query_manifest.json').read_text(encoding='utf-8'))
        actual = json.loads(generated.read_text(encoding='utf-8'))
        if actual != expected:
            raise SystemExit('regenerated locked registry manifest differs from checked-in manifest')
    if args.strict_upstream and not header_path:
        raise SystemExit('strict upstream header gate requires --header')
    if header_path:
        run([sys.executable, 'tools/verify_canonical_vulkan_headers.py', str(header_path)])
        run([sys.executable, 'tools/verify_registry_catalog.py', 'registry/generated/registry_query_manifest.json', str(header_path), 'app/src/main/cpp/registry_query_catalog.h'])
        run([sys.executable, 'tools/verify_extension_field_coverage.py', '--registry', str(registry_path), '--header', str(header_path)])
        run([sys.executable, 'tools/verify_upstream_registry.py', '--registry', str(registry_path), '--header', str(header_path)])
        print('PASS strict locked-header verification')
    else:
        print('INFO strict canonical Vulkan header byte-level verification NOT EXECUTED; registry-level verification used the bundled SHA-256-locked vk.xml')
    for path in sorted((root / 'tools').glob('*.py')):
        compile(path.read_text(encoding='utf-8'), str(path), 'exec')
    for path in sorted((root / 'registry/generated').glob('*.json')) + [root / 'registry/registry_lock.json', root / 'registry/video_registry_lock.json'] + sorted((root / 'tests/golden').glob('*_regression_contract.json')) + [root / 'tests/golden/1.4.4_googlebook_vulkan_1.4.364_contract.json', root / 'tests/golden/1.4.5_launch_navigation_contract.json', root / 'tests/golden/1.4.6_compile_mouse_input_contract.json']:
        json.loads(path.read_text(encoding='utf-8'))
    print('VulkanScope quality gate 1.4.7: PASS')

def run_1406_quality_gate():
    commands = [
        [sys.executable, 'tools/verify_release_1406.py'],
        [sys.executable, 'tools/test_release_1406_state_machine.py'],
        [sys.executable, 'tools/test_release_1406_negative_mutations.py'],
        [sys.executable, 'tools/verify_cmake_registry_lock.py'],
        [sys.executable, 'tools/verify_registry_snapshot.py'],
        [sys.executable, 'tools/verify_compile_regressions.py'],
        [sys.executable, 'tools/verify_spec_regressions.py'],
        [sys.executable, 'tools/verify_probe_lifecycle_regressions.py'],
        [sys.executable, 'tools/verify_probe_timeout_recovery.py'],
        [sys.executable, 'tools/verify_probe_cancellation_recovery.py'],
        [sys.executable, 'tools/verify_report_semantics_04140.py'],
        [sys.executable, 'tools/verify_report_surface_integrity_04141.py'],
        [sys.executable, 'tools/verify_profile_requirements_04144.py'],
        [sys.executable, 'tools/verify_video_registry_04145.py'],
        [sys.executable, 'tools/test_video_profile_census_state_machine.py'],
        [sys.executable, 'tools/verify_full_hardening_0800.py'],
        [sys.executable, 'tools/test_full_hardening_0800_state_machine.py'],
        [sys.executable, 'tools/verify_concurrency_resource_contracts.py'],
        [sys.executable, 'tools/verify_package_reproducibility.py'],
    ]
    for command in commands:
        run(command)
    registry_path = Path(args.registry).resolve() if args.registry else (root / lock['bundledRegistryPath']).resolve()
    header_path = Path(args.header).resolve() if args.header else None
    with tempfile.TemporaryDirectory(prefix='vulkanscope-manifest-') as temp_name:
        generated = Path(temp_name) / 'registry_query_manifest.json'
        command = [
            sys.executable, 'tools/generate_vk_registry.py',
            '--registry', str(registry_path),
            '--catalog', 'app/src/main/cpp/registry_query_catalog.h',
            '--coverage', 'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt',
            '--lock', 'registry/registry_lock.json',
            '--out', str(generated),
            '--require-complete-extension-coverage'
        ]
        if header_path:
            command += ['--header', str(header_path)]
        run(command)
        expected = json.loads((root / 'registry/generated/registry_query_manifest.json').read_text(encoding='utf-8'))
        actual = json.loads(generated.read_text(encoding='utf-8'))
        if actual != expected:
            raise SystemExit('regenerated locked registry manifest differs from checked-in manifest')
    if args.strict_upstream and not header_path:
        raise SystemExit('strict upstream header gate requires --header')
    if header_path:
        run([sys.executable, 'tools/verify_canonical_vulkan_headers.py', str(header_path)])
        run([sys.executable, 'tools/verify_registry_catalog.py', 'registry/generated/registry_query_manifest.json', str(header_path), 'app/src/main/cpp/registry_query_catalog.h'])
        run([sys.executable, 'tools/verify_extension_field_coverage.py', '--registry', str(registry_path), '--header', str(header_path)])
        run([sys.executable, 'tools/verify_upstream_registry.py', '--registry', str(registry_path), '--header', str(header_path)])
        print('PASS strict locked-header verification')
    else:
        print('INFO strict canonical Vulkan header byte-level verification NOT EXECUTED; registry-level verification used the bundled SHA-256-locked vk.xml')
    for path in sorted((root / 'tools').glob('*.py')):
        compile(path.read_text(encoding='utf-8'), str(path), 'exec')
    for path in sorted((root / 'registry/generated').glob('*.json')) + [root / 'registry/registry_lock.json', root / 'registry/video_registry_lock.json'] + sorted((root / 'tests/golden').glob('*_regression_contract.json')) + [root / 'tests/golden/1.4.4_googlebook_vulkan_1.4.364_contract.json', root / 'tests/golden/1.4.5_launch_navigation_contract.json']:
        json.loads(path.read_text(encoding='utf-8'))
    print('VulkanScope quality gate 1.4.6: PASS')

def run_1405_quality_gate():
    commands = [
        [sys.executable, 'tools/verify_release_1405.py'],
        [sys.executable, 'tools/test_release_1405_state_machine.py'],
        [sys.executable, 'tools/test_release_1405_negative_mutations.py'],
        [sys.executable, 'tools/verify_cmake_registry_lock.py'],
        [sys.executable, 'tools/verify_registry_snapshot.py'],
        [sys.executable, 'tools/verify_compile_regressions.py'],
        [sys.executable, 'tools/verify_spec_regressions.py'],
        [sys.executable, 'tools/verify_probe_lifecycle_regressions.py'],
        [sys.executable, 'tools/verify_probe_timeout_recovery.py'],
        [sys.executable, 'tools/verify_probe_cancellation_recovery.py'],
        [sys.executable, 'tools/verify_report_semantics_04140.py'],
        [sys.executable, 'tools/verify_report_surface_integrity_04141.py'],
        [sys.executable, 'tools/verify_profile_requirements_04144.py'],
        [sys.executable, 'tools/verify_video_registry_04145.py'],
        [sys.executable, 'tools/test_video_profile_census_state_machine.py'],
        [sys.executable, 'tools/verify_full_hardening_0800.py'],
        [sys.executable, 'tools/test_full_hardening_0800_state_machine.py'],
        [sys.executable, 'tools/verify_concurrency_resource_contracts.py'],
        [sys.executable, 'tools/verify_package_reproducibility.py'],
    ]
    for command in commands:
        run(command)
    registry_path = Path(args.registry).resolve() if args.registry else (root / lock['bundledRegistryPath']).resolve()
    header_path = Path(args.header).resolve() if args.header else None
    with tempfile.TemporaryDirectory(prefix='vulkanscope-manifest-') as temp_name:
        generated = Path(temp_name) / 'registry_query_manifest.json'
        command = [
            sys.executable, 'tools/generate_vk_registry.py',
            '--registry', str(registry_path),
            '--catalog', 'app/src/main/cpp/registry_query_catalog.h',
            '--coverage', 'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt',
            '--lock', 'registry/registry_lock.json',
            '--out', str(generated),
            '--require-complete-extension-coverage'
        ]
        if header_path:
            command += ['--header', str(header_path)]
        run(command)
        expected = json.loads((root / 'registry/generated/registry_query_manifest.json').read_text(encoding='utf-8'))
        actual = json.loads(generated.read_text(encoding='utf-8'))
        if actual != expected:
            raise SystemExit('regenerated locked registry manifest differs from checked-in manifest')
    if args.strict_upstream and not header_path:
        raise SystemExit('strict upstream header gate requires --header')
    if header_path:
        run([sys.executable, 'tools/verify_canonical_vulkan_headers.py', str(header_path)])
        run([sys.executable, 'tools/verify_registry_catalog.py', 'registry/generated/registry_query_manifest.json', str(header_path), 'app/src/main/cpp/registry_query_catalog.h'])
        run([sys.executable, 'tools/verify_extension_field_coverage.py', '--registry', str(registry_path), '--header', str(header_path)])
        run([sys.executable, 'tools/verify_upstream_registry.py', '--registry', str(registry_path), '--header', str(header_path)])
        print('PASS strict locked-header verification')
    else:
        print('INFO strict canonical Vulkan header byte-level verification NOT EXECUTED; registry-level verification used the bundled SHA-256-locked vk.xml')
    for path in sorted((root / 'tools').glob('*.py')):
        compile(path.read_text(encoding='utf-8'), str(path), 'exec')
    for path in sorted((root / 'registry/generated').glob('*.json')) + [root / 'registry/registry_lock.json', root / 'registry/video_registry_lock.json'] + sorted((root / 'tests/golden').glob('*_regression_contract.json')) + [root / 'tests/golden/1.4.4_googlebook_vulkan_1.4.364_contract.json']:
        json.loads(path.read_text(encoding='utf-8'))
    print('VulkanScope quality gate 1.4.5: PASS')

def run_1306_quality_gate():
    # 1.3.6 is intentionally storage-only. Do not replay superseded SAF/history gates.
    commands = [
        [sys.executable, 'tools/verify_release_1306.py'],
        [sys.executable, 'tools/test_release_1306_state_machine.py'],
        [sys.executable, 'tools/verify_compile_regressions.py'],
        [sys.executable, 'tools/verify_package_reproducibility.py'],
    ]
    for command in commands:
        run(command)
    print('VulkanScope quality gate 1.3.6: PASS')



def run_1305_quality_gate():
    commands = [
        [sys.executable, 'tools/verify_regression_contracts.py'],
        [sys.executable, 'tools/verify_spec_regressions.py'],
        [sys.executable, 'tools/verify_vulkan_1_4_362_0810.py', '--skip-version'],
        [sys.executable, 'tools/test_vulkan_1_4_362_0810_negative_mutations.py'],
        [sys.executable, 'tools/verify_cmake_registry_lock.py'],
        [sys.executable, 'tools/verify_compile_regressions.py'],
        [sys.executable, 'tools/verify_responsive_overlay_ui_0815.py', '--skip-version'],
        [sys.executable, 'tools/verify_probe_lifecycle_regressions.py'],
        [sys.executable, 'tools/verify_probe_timeout_recovery.py'],
        [sys.executable, 'tools/verify_probe_cancellation_recovery.py'],
        [sys.executable, 'tools/verify_report_semantics_04140.py'],
        [sys.executable, 'tools/verify_report_surface_integrity_04141.py'],
        [sys.executable, 'tools/verify_profile_requirements_04144.py'],
        [sys.executable, 'tools/verify_video_registry_04145.py'],
        [sys.executable, 'tools/test_video_profile_census_state_machine.py'],
        [sys.executable, 'tools/verify_full_hardening_0800.py'],
        [sys.executable, 'tools/verify_release_1305.py'],
        [sys.executable, 'tools/test_release_1305_state_machine.py'],
        [sys.executable, 'tools/verify_registry_snapshot.py'],
        [sys.executable, 'tools/verify_package_reproducibility.py'],
    ]
    for command in commands:
        run(command)
    registry_path = Path(args.registry).resolve() if args.registry else (root / lock['bundledRegistryPath']).resolve()
    header_path = Path(args.header).resolve() if args.header else None
    owned_temp = None
    if args.fetch_locked_upstream:
        owned_temp = tempfile.TemporaryDirectory(prefix='vulkanscope-upstream-')
        temp = Path(owned_temp.name)
        registry_path = temp / 'vk.xml'
        header_path = temp / 'vulkan_core.h'
        registry_url = f'https://raw.githubusercontent.com/{lock["registryRepository"]}/{lock["registryRef"]}/{lock["registryPath"]}'
        header_url = f'https://raw.githubusercontent.com/{lock["headerRepository"]}/{lock["headerCommit"]}/{lock["headerPath"]}'
        try:
            urllib.request.urlretrieve(registry_url, registry_path)
            urllib.request.urlretrieve(header_url, header_path)
        except Exception as exc:
            raise SystemExit(f'locked upstream fetch failed: {exc}')
    if not registry_path.is_file():
        raise SystemExit('locked registry input is missing')
    with tempfile.TemporaryDirectory(prefix='vulkanscope-manifest-') as temp_name:
        generated = Path(temp_name) / 'registry_query_manifest.json'
        command = [
            sys.executable, 'tools/generate_vk_registry.py',
            '--registry', str(registry_path),
            '--catalog', 'app/src/main/cpp/registry_query_catalog.h',
            '--coverage', 'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt',
            '--lock', 'registry/registry_lock.json',
            '--out', str(generated),
            '--require-complete-extension-coverage'
        ]
        if header_path:
            command += ['--header', str(header_path)]
        run(command)
        expected = json.loads((root / 'registry/generated/registry_query_manifest.json').read_text(encoding='utf-8'))
        actual = json.loads(generated.read_text(encoding='utf-8'))
        if actual != expected:
            raise SystemExit('regenerated locked registry manifest differs from checked-in manifest')
    print('PASS locked registry regeneration')
    if args.strict_upstream and not header_path:
        raise SystemExit('strict upstream header gate requires --header or --fetch-locked-upstream')
    if header_path:
        run([sys.executable, 'tools/verify_canonical_vulkan_headers.py', str(header_path)])
        run([sys.executable, 'tools/verify_registry_catalog.py', 'registry/generated/registry_query_manifest.json', str(header_path), 'app/src/main/cpp/registry_query_catalog.h'])
        run([sys.executable, 'tools/verify_extension_field_coverage.py', '--registry', str(registry_path), '--header', str(header_path)])
        run([sys.executable, 'tools/verify_upstream_registry.py', '--registry', str(registry_path), '--header', str(header_path)])
        print('PASS strict locked-header verification')
    else:
        print('INFO canonical Vulkan header byte-level verification requires the locked 1.4.362 header; registry-level strict verification completed from the bundled vk.xml')
    for path in sorted((root / 'tools').glob('*.py')):
        compile(path.read_text(encoding='utf-8'), str(path), 'exec')
    for path in sorted((root / 'registry/generated').glob('*.json')) + [root / 'registry/registry_lock.json'] + sorted((root / 'tests/golden').glob('*_regression_contract.json')):
        json.loads(path.read_text(encoding='utf-8'))
    print('VulkanScope quality gate 1.3.5: PASS')


if release_2006_surface_scroll:
    run_2006_quality_gate()
    raise SystemExit(0)
if release_2005_tv_key_compile_fix:
    run_2005_quality_gate()
    raise SystemExit(0)
if release_2004_six_lane_tv:
    run_2004_quality_gate()
    raise SystemExit(0)
if release_2003_adaptive_scheduler:
    run_2003_quality_gate()
    raise SystemExit(0)
if release_2002_parallel_oneshot:
    run_2002_quality_gate()
    raise SystemExit(0)
if release_2000_timing_instrumentation:
    run_2000_quality_gate()
    raise SystemExit(0)

if release_1502_agp_full_audit:
    run_1502_quality_gate()
    raise SystemExit(0)

if release_1501_profile_navigation_opening:
    run_1501_quality_gate()
    raise SystemExit(0)

if release_1500_compact_navigation_watchdog:
    run_1500_quality_gate()
    raise SystemExit(0)
if release_1416_surface_status_navigation:
    run_1416_quality_gate()
    raise SystemExit(0)
if release_1415_unified_navigation:
    run_1415_quality_gate()
    raise SystemExit(0)

if release_1414_floating_navigation:
    run_1414_quality_gate()
    raise SystemExit(0)

if release_1413_researched_navigation:
    run_1413_quality_gate()
    raise SystemExit(0)

if release_1412_compile_navigation_profiles:
    run_1412_quality_gate()
    raise SystemExit(0)

if release_1407_compile_mouse_input:
    run_1407_quality_gate()
    raise SystemExit(0)

if release_1406_launch_navigation:
    run_1406_quality_gate()
    raise SystemExit(0)

if release_1405_googlebook_vulkan:
    run_1405_quality_gate()
    raise SystemExit(0)


if release_1306_storage_exchange:
    run_1306_quality_gate()
    raise SystemExit(0)


if release_1305_full_audit:
    run_1305_quality_gate()
    raise SystemExit(0)


run([sys.executable, 'tools/verify_regression_contracts.py'])
run([sys.executable, 'tools/verify_spec_regressions.py'])
run([sys.executable, 'tools/verify_1_4_361_regressions.py'])
run([sys.executable, 'tools/verify_vulkan_1_4_362_0810.py', '--skip-version'])
run([sys.executable, 'tools/test_vulkan_1_4_362_0810_negative_mutations.py'])
run([sys.executable, 'tools/verify_compile_role_0812.py', '--skip-version'])
run([sys.executable, 'tools/verify_paddingvalues_compile_0814.py', '--skip-version'])
run([sys.executable, 'tools/verify_responsive_overlay_ui_0815.py', '--skip-version'])
run([sys.executable, 'tools/test_responsive_overlay_ui_0815_state_machine.py'])
run([sys.executable, 'tools/verify_advanced_analysis_1000.py', '--skip-version'])
run([sys.executable, 'tools/test_advanced_analysis_1000_state_machine.py'])
run([sys.executable, 'tools/test_advanced_analysis_1000_negative_mutations.py'])
run([sys.executable, 'tools/verify_full_audit_1001.py'])
run([sys.executable, 'tools/test_full_audit_1001_state_machine.py'])
run([sys.executable, 'tools/test_full_audit_1001_negative_mutations.py'])
run([sys.executable, 'tools/verify_expressive_connectivity_turnip_1002.py'])
run([sys.executable, 'tools/test_expressive_connectivity_turnip_1002_state_machine.py'])
run([sys.executable, 'tools/test_expressive_connectivity_turnip_1002_negative_mutations.py'])
run([sys.executable, 'tools/verify_kotlin_regex_compile_1003.py'])
run([sys.executable, 'tools/test_kotlin_regex_compile_1003_negative_mutations.py'])
run([sys.executable, 'tools/test_offline_turnip_manager_1004_state_machine.py'])
run([sys.executable, 'tools/verify_kotlin_default_parameter_compile_1005.py'])
run([sys.executable, 'tools/test_kotlin_default_parameter_compile_1005_negative_mutations.py'])
run([sys.executable, 'tools/test_driver_confirmation_network_ui_1006_state_machine.py'])
run([sys.executable, 'tools/test_turnip_connectivity_filter_dialog_1007_state_machine.py'])
run_legacy_storage_gate([sys.executable, 'tools/verify_driver_manager_dialog_storage_ui_1008.py', '--skip-version'])
run_legacy_storage_gate([sys.executable, 'tools/test_driver_manager_dialog_storage_ui_1008_state_machine.py'])
run_legacy_storage_gate([sys.executable, 'tools/test_driver_manager_dialog_storage_ui_1008_negative_mutations.py'])
run_legacy_storage_gate([sys.executable, 'tools/verify_detail_action_driver_fallback_ui_1009.py'])
run_legacy_storage_gate([sys.executable, 'tools/test_detail_action_driver_fallback_ui_1009_state_machine.py'])
run_legacy_storage_gate([sys.executable, 'tools/test_detail_action_driver_fallback_ui_1009_negative_mutations.py'])
run([sys.executable, 'tools/verify_paddingvalues_import_compile_1010.py'])
run([sys.executable, 'tools/test_paddingvalues_import_compile_1010_negative_mutations.py'])
run([sys.executable, 'tools/verify_icon_active_state_ui_1011.py'])
run([sys.executable, 'tools/test_icon_active_state_ui_1011_state_machine.py'])
run([sys.executable, 'tools/test_icon_active_state_ui_1011_negative_mutations.py'])
run_legacy_storage_gate([sys.executable, 'tools/verify_saf_fallback_android_icon_1012.py'])
run_legacy_storage_gate([sys.executable, 'tools/test_saf_fallback_android_icon_1012_state_machine.py'])
run_legacy_storage_gate([sys.executable, 'tools/test_saf_fallback_android_icon_1012_negative_mutations.py'])
run([sys.executable, 'tools/verify_update_semantic_icons_1013.py'])
run([sys.executable, 'tools/test_update_semantic_icons_1013_state_machine.py'])
run([sys.executable, 'tools/test_update_semantic_icons_1013_negative_mutations.py'])
run_legacy_storage_gate([sys.executable, 'tools/verify_fallback_import_compile_intro_1014.py'])
run_legacy_storage_gate([sys.executable, 'tools/test_fallback_import_compile_intro_1014_state_machine.py'])
run_legacy_storage_gate([sys.executable, 'tools/test_fallback_import_compile_intro_1014_negative_mutations.py'])
run_legacy_storage_gate([sys.executable, 'tools/verify_saf_launch_first_full_audit_1015.py'])
run_legacy_storage_gate([sys.executable, 'tools/test_saf_launch_first_full_audit_1015_state_machine.py'])
run_legacy_storage_gate([sys.executable, 'tools/test_saf_launch_first_full_audit_1015_negative_mutations.py'])
run([sys.executable, 'tools/verify_share_display_navigation_ui_1015.py'])
run([sys.executable, 'tools/test_share_display_navigation_ui_1015_state_machine.py'])
run([sys.executable, 'tools/test_share_display_navigation_ui_1015_negative_mutations.py'])
run([sys.executable, 'tools/verify_release_1202.py', '--skip-version'])
run([sys.executable, 'tools/test_release_1202_state_machine.py'])
run([sys.executable, 'tools/verify_release_1203.py'])
run([sys.executable, 'tools/test_release_1203_state_machine.py'])
run([sys.executable, 'tools/test_release_1203_negative_mutations.py'])
run([sys.executable, 'tools/verify_release_1204.py', '--skip-version'])
run([sys.executable, 'tools/test_release_1204_state_machine.py'])
run([sys.executable, 'tools/test_release_1204_negative_mutations.py'])
run([sys.executable, 'tools/verify_release_1205.py'])
run([sys.executable, 'tools/test_release_1205_state_machine.py'])
run([sys.executable, 'tools/test_release_1205_negative_mutations.py'])
run([sys.executable, 'tools/verify_release_1300.py'])
run([sys.executable, 'tools/test_release_1300_state_machine.py'])
run([sys.executable, 'tools/test_release_1300_negative_mutations.py'])
if release_1303_storage_removal:
    run([sys.executable, 'tools/verify_release_1303.py'])
    run([sys.executable, 'tools/test_release_1303_state_machine.py'])
    run([sys.executable, 'tools/test_release_1303_negative_mutations.py'])
if release_1304_file_manager:
    run([sys.executable, 'tools/verify_release_1304.py'])
    run([sys.executable, 'tools/test_release_1304_state_machine.py'])
    run([sys.executable, 'tools/test_release_1304_negative_mutations.py'])
run([sys.executable, 'tools/verify_material3_expressive_ui_0813.py', '--skip-version'])
run([sys.executable, 'tools/test_material3_expressive_ui_0813_state_machine.py'])
run([sys.executable, 'tools/verify_cmake_registry_lock.py'])
run([sys.executable, 'tools/verify_compile_regressions.py'])
run([sys.executable, 'tools/test_probe_publication_state_machine.py'])
run([sys.executable, 'tools/verify_probe_publication_handshake.py'])
run([sys.executable, 'tools/test_probe_terminal_ownership_state_machine.py'])
run([sys.executable, 'tools/verify_probe_terminal_ownership.py'])
run([sys.executable, 'tools/test_base_terminal_json_state_machine.py'])
run([sys.executable, 'tools/verify_base_terminal_json.py'])
run([sys.executable, 'tools/test_report_semantics_state_machine.py'])
run([sys.executable, 'tools/verify_report_semantics_04140.py'])
run([sys.executable, 'tools/test_report_surface_integrity_state_machine.py'])
run([sys.executable, 'tools/verify_report_surface_integrity_04141.py'])
run([sys.executable, 'tools/test_report_surface_integrity_negative_mutations.py'])
run([sys.executable, 'tools/verify_html_presentation_04142.py'])
run([sys.executable, 'tools/test_html_presentation_negative_mutations.py'])
run([sys.executable, 'tools/test_driver_surface_rebind_state_machine.py'])
run([sys.executable, 'tools/verify_driver_surface_rebind_04143.py'])
run([sys.executable, 'tools/test_driver_surface_rebind_negative_mutations.py'])
run([sys.executable, 'tools/test_profile_evaluator_state_machine.py'])
run([sys.executable, 'tools/verify_profile_requirements_04144.py'])
run([sys.executable, 'tools/test_profile_requirements_negative_mutations.py'])
run([sys.executable, 'tools/verify_video_registry_04145.py'])
run([sys.executable, 'tools/test_video_profile_census_state_machine.py'])
run([sys.executable, 'tools/test_video_registry_negative_mutations.py'])
run([sys.executable, 'tools/test_scroll_boundary_indicators_state_machine.py'])
run([sys.executable, 'tools/verify_ui_information_architecture_04146.py'])
run([sys.executable, 'tools/test_ui_information_architecture_negative_mutations.py'])
run([sys.executable, 'tools/verify_full_hardening_0800.py'])
run([sys.executable, 'tools/test_full_hardening_0800_state_machine.py'])
run([sys.executable, 'tools/test_full_hardening_0800_negative_mutations.py'])
run([sys.executable, 'tools/verify_encyclopedia_0802.py'])
run([sys.executable, 'tools/test_encyclopedia_0802_negative_mutations.py'])
run([sys.executable, 'tools/verify_overview_tools_search_0803.py'])
run([sys.executable, 'tools/test_encyclopedia_search_0803_state_machine.py'])
run([sys.executable, 'tools/test_overview_tools_search_0803_negative_mutations.py'])
run([sys.executable, 'tools/verify_detail_tv_collection_0804.py'])
run([sys.executable, 'tools/test_detail_tv_collection_0804_state_machine.py'])
run([sys.executable, 'tools/test_detail_tv_collection_0804_negative_mutations.py'])
run([sys.executable, 'tools/verify_update_release_notes_tv_0805.py'])
run([sys.executable, 'tools/test_update_release_notes_tv_0805_state_machine.py'])
run([sys.executable, 'tools/test_update_release_notes_tv_0805_negative_mutations.py'])
run([sys.executable, 'tools/verify_accessibility_large_text_0806.py'])
run([sys.executable, 'tools/test_accessibility_large_text_0806_state_machine.py'])
run([sys.executable, 'tools/test_accessibility_large_text_0806_negative_mutations.py'])
run([sys.executable, 'tools/verify_system_language_font_update_0807.py'])
run([sys.executable, 'tools/test_system_language_font_update_0807_state_machine.py'])
run([sys.executable, 'tools/test_system_language_font_update_0807_negative_mutations.py'])
run([sys.executable, 'tools/verify_final_release_0808.py'])
run([sys.executable, 'tools/test_final_release_0808_state_machine.py'])
run([sys.executable, 'tools/test_final_release_0808_negative_mutations.py'])
run([sys.executable, 'tools/verify_detail_button_only_0809.py'])
run([sys.executable, 'tools/test_detail_button_only_0809_state_machine.py'])
run([sys.executable, 'tools/test_detail_button_only_0809_negative_mutations.py'])
run([sys.executable, 'tools/test_probe_timeout_state_machine.py'])
run([sys.executable, 'tools/verify_probe_timeout_recovery.py'])
run([sys.executable, 'tools/test_probe_cancellation_state_machine.py'])
run([sys.executable, 'tools/verify_probe_cancellation_recovery.py'])
run([sys.executable, 'tools/verify_probe_lifecycle_regressions.py'])
run([sys.executable, 'tools/verify_registry_snapshot.py'])
run([sys.executable, 'tools/verify_concurrency_resource_contracts.py'])
run([sys.executable, 'tools/verify_release.py', '--skip-regression-contracts', '--skip-nested-verifiers'])
run([sys.executable, 'tools/verify_package_reproducibility.py'])

registry_path = Path(args.registry).resolve() if args.registry else (root / lock['bundledRegistryPath']).resolve()
header_path = Path(args.header).resolve() if args.header else None
owned_temp = None
if args.fetch_locked_upstream:
    owned_temp = tempfile.TemporaryDirectory(prefix='vulkanscope-upstream-')
    temp = Path(owned_temp.name)
    registry_path = temp / 'vk.xml'
    header_path = temp / 'vulkan_core.h'
    registry_url = f'https://raw.githubusercontent.com/{lock["registryRepository"]}/{lock["registryRef"]}/{lock["registryPath"]}'
    header_url = f'https://raw.githubusercontent.com/{lock["headerRepository"]}/{lock["headerCommit"]}/{lock["headerPath"]}'
    try:
        urllib.request.urlretrieve(registry_url, registry_path)
        urllib.request.urlretrieve(header_url, header_path)
    except Exception as exc:
        raise SystemExit(f'locked upstream fetch failed: {exc}')

if not registry_path.is_file():
    raise SystemExit('locked registry input is missing')
with tempfile.TemporaryDirectory(prefix='vulkanscope-manifest-') as temp_name:
    generated = Path(temp_name) / 'registry_query_manifest.json'
    command = [
        sys.executable, 'tools/generate_vk_registry.py',
        '--registry', str(registry_path),
        '--catalog', 'app/src/main/cpp/registry_query_catalog.h',
        '--coverage', 'app/src/main/java/com/efishell/vulkanscope/ValidatedExtensionCoverage.kt',
        '--lock', 'registry/registry_lock.json',
        '--out', str(generated),
        '--require-complete-extension-coverage'
    ]
    if header_path:
        command += ['--header', str(header_path)]
    run(command)
    expected = json.loads((root / 'registry/generated/registry_query_manifest.json').read_text(encoding='utf-8'))
    actual = json.loads(generated.read_text(encoding='utf-8'))
    if actual != expected:
        raise SystemExit('regenerated locked registry manifest differs from checked-in manifest')
print('PASS locked registry regeneration')

if args.strict_upstream and not header_path:
    raise SystemExit('strict upstream header gate requires --header or --fetch-locked-upstream')
if header_path:
    run([sys.executable, 'tools/verify_canonical_vulkan_headers.py', str(header_path)])
    run([sys.executable, 'tools/verify_registry_catalog.py', 'registry/generated/registry_query_manifest.json', str(header_path), 'app/src/main/cpp/registry_query_catalog.h'])
    run([sys.executable, 'tools/verify_extension_field_coverage.py', '--registry', str(registry_path), '--header', str(header_path)])
    run([sys.executable, 'tools/verify_upstream_registry.py', '--registry', str(registry_path), '--header', str(header_path)])
    print('PASS strict locked-header verification')
else:
    print('INFO canonical Vulkan header byte-level verification requires the locked 1.4.362 header; registry-level strict verification completed from the bundled vk.xml')

for path in sorted((root / 'tools').glob('*.py')):
    compile(path.read_text(encoding='utf-8'), str(path), 'exec')
for path in sorted((root / 'registry/generated').glob('*.json')) + [root / 'registry/registry_lock.json'] + sorted((root / 'tests/golden').glob('*_regression_contract.json')):
    json.loads(path.read_text(encoding='utf-8'))
print('VulkanScope quality gate: PASS')
