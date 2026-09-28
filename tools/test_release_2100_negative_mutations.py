#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_release_2100.py'

def mutate_and_expect_failure(rel, old, new):
    with tempfile.TemporaryDirectory(prefix='vs2100-mutation-') as temp_name:
        temp = Path(temp_name) / 'root'
        shutil.copytree(root, temp)
        path = temp / rel
        value = path.read_text(encoding='utf-8')
        if old not in value:
            raise SystemExit(f'mutation source token missing: {rel}: {old}')
        path.write_text(value.replace(old, new, 1), encoding='utf-8')
        result = subprocess.run([sys.executable, str(verifier), '--root', str(temp)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        if result.returncode == 0:
            raise SystemExit(f'verifier accepted forbidden mutation: {rel}: {old} -> {new}')

mutations = [
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'containerColor = VulkanSurfaceRaised.copy(alpha = 0.78f)', 'containerColor = VulkanSurfaceRaised'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'SoftScrollIntersectionShadows(showTopFade, showBottomFade, Modifier.fillMaxSize())', 'Box(Modifier.fillMaxSize())'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'private const val COLLECTION_PAGE_SIZE = 50', 'private const val COLLECTION_PAGE_SIZE = 100'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'DENSE_GRID, LARGE_GRID', 'LARGE_GRID'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'putString("turnip_file_manager_view_mode", mode.name)', 'putString("turnip_file_manager_view_mode", "LIST")'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', '.fillMaxWidth(animatedProgress)', '.fillMaxWidth()'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'uriHandler.openUri(databaseReportUrl(reportId))', 'uriHandler.openUri(OFFICIAL_DATABASE_WEB_URL)'),
    ('app/src/main/cpp/vulkanscope.cpp', 'VK_API_VERSION_1_0', 'VK_API_VERSION_1_1'),
]
for mutation in mutations:
    mutate_and_expect_failure(*mutation)
print('PASS VulkanScope 2.1.0 targeted negative mutations')
