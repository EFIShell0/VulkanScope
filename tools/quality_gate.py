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


def run(command):
    result = subprocess.run(command, cwd=root, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    if result.stdout:
        print(result.stdout.rstrip())
    if result.returncode != 0:
        raise SystemExit(result.returncode)


run([sys.executable, 'tools/verify_regression_contracts.py'])
run([sys.executable, 'tools/verify_spec_regressions.py'])
run([sys.executable, 'tools/verify_1_4_361_regressions.py'])
run([sys.executable, 'tools/verify_vulkan_1_4_362_0810.py', '--skip-version'])
run([sys.executable, 'tools/test_vulkan_1_4_362_0810_negative_mutations.py'])
run([sys.executable, 'tools/verify_compile_role_0812.py', '--skip-version'])
run([sys.executable, 'tools/verify_paddingvalues_compile_0814.py', '--skip-version'])
run([sys.executable, 'tools/test_paddingvalues_compile_0814_negative_mutations.py'])
run([sys.executable, 'tools/verify_responsive_overlay_ui_0815.py'])
run([sys.executable, 'tools/test_responsive_overlay_ui_0815_state_machine.py'])
run([sys.executable, 'tools/test_responsive_overlay_ui_0815_negative_mutations.py'])
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
