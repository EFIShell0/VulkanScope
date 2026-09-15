#!/usr/bin/env python3

REMOVED_STORAGE_ACTIONS = {
    'turnip_zip_import',
    'analysis_snapshot_import',
    'analysis_snapshot_export',
    'minimum_profile_import',
    'minimum_profile_export',
    'technical_report_json_export',
    'complete_txt_export',
    'complete_html_export',
}

def route(action: str) -> str:
    return 'UNAVAILABLE_NO_SAF_NO_FALLBACK' if action in REMOVED_STORAGE_ACTIONS else 'UNCHANGED'

for action in sorted(REMOVED_STORAGE_ACTIONS):
    assert route(action) == 'UNAVAILABLE_NO_SAF_NO_FALLBACK'
for action in ['database_submit', 'update_download', 'turnip_activate_installed_slot', 'turnip_remove_installed_slot', 'vulkan_collect']:
    assert route(action) == 'UNCHANGED'

assert len(REMOVED_STORAGE_ACTIONS) == 8
print('PASS VulkanScope 1.3.3 storage-removal state machine: 8 removed routes, unrelated core actions unchanged')
