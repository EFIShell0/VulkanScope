#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_release_2101.py'


def run_verifier(temp):
    return subprocess.run([sys.executable, str(verifier), '--root', str(temp)], stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)


def mutate_and_expect_failure(rel, old, new):
    with tempfile.TemporaryDirectory(prefix='vs2101-mutation-') as temp_name:
        temp = Path(temp_name) / 'root'
        shutil.copytree(root, temp)
        path = temp / rel
        value = path.read_text(encoding='utf-8')
        if old not in value:
            raise SystemExit(f'mutation source token missing: {rel}: {old}')
        path.write_text(value.replace(old, new, 1), encoding='utf-8')
        result = run_verifier(temp)
        if result.returncode == 0:
            raise SystemExit(f'verifier accepted forbidden mutation: {rel}: {old} -> {new}')


def mutate_and_expect_success(rel, old, new):
    with tempfile.TemporaryDirectory(prefix='vs2101-control-') as temp_name:
        temp = Path(temp_name) / 'root'
        shutil.copytree(root, temp)
        path = temp / rel
        value = path.read_text(encoding='utf-8')
        if old not in value:
            raise SystemExit(f'control source token missing: {rel}: {old}')
        path.write_text(value.replace(old, new, 1), encoding='utf-8')
        result = run_verifier(temp)
        if result.returncode != 0:
            raise SystemExit('verifier false-positive on unrelated non-production mutation:\n' + result.stdout)


mutations = [
    (
        'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt',
        'CollectionPager(totalItems = filtered.size, currentPage = page, onPageChange = { page = it })',
        'CollectionPager(filtered.size, page) { page = it }',
    ),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'private const val COLLECTION_PAGE_SIZE = 50', 'private const val COLLECTION_PAGE_SIZE = 100'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'DENSE_GRID, LARGE_GRID', 'LARGE_GRID'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', '.fillMaxWidth(animatedProgress)', '.fillMaxWidth()'),
    ('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt', 'uriHandler.openUri(databaseReportUrl(reportId))', 'uriHandler.openUri(OFFICIAL_DATABASE_WEB_URL)'),
]
for mutation in mutations:
    mutate_and_expect_failure(*mutation)

mutate_and_expect_success('BUILD_AUDIT.md', '# ', '#  ')
print('PASS VulkanScope 2.1.1 targeted negative mutations and false-positive control')
