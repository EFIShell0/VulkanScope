#!/usr/bin/env python3
import argparse
import hashlib
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root', type=Path, default=Path(__file__).resolve().parents[1])
args = parser.parse_args()
root = args.root.resolve()
errors = []

def need(condition, message):
    if not condition:
        errors.append(message)

def read(rel):
    path = root / rel
    need(path.is_file(), f'missing file: {rel}')
    return path.read_text(encoding='utf-8') if path.is_file() else ''

def block(text, start, end):
    a = text.find(start)
    b = text.find(end, a + len(start)) if a >= 0 else -1
    need(a >= 0 and b > a, f'missing block: {start}')
    return text[a:b] if a >= 0 and b > a else ''

def sha(rel):
    path = root / rel
    return hashlib.sha256(path.read_bytes()).hexdigest() if path.is_file() else ''

main = read('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')
gradle = read('app/build.gradle.kts')
rules = read('rules/PROJECT_RULES.md')
audit = read('rules/3.0.11_TURNIP_VALIDATED_FOLDER_COUNT_AUDIT.md')
changelog = read('changelog.md')

need('versionCode = 3011' in gradle and 'versionName = "3.0.11"' in gradle, '3.0.11 release identity missing')
need(changelog.startswith('## 3.0.11\n'), '3.0.11 changelog entry missing or not first')
need('## Release 3.0.11 Turnip File Manager validated folder-count requirements' in rules, '3.0.11 project rules missing')
need('# VulkanScope 3.0.11 Turnip Validated Folder Count Audit' in audit, '3.0.11 audit heading missing')
need('// ' not in main and '/*' not in main, 'source-code comment rule violated in MainActivity.kt')

entry = block(main, 'private data class TurnipFolderEntry(', '\n\nprivate data class TurnipDirectoryListing(')
need('val validZipFileCount: Int' in entry, 'Turnip folder model does not expose validated ZIP count')
need('val zipFileCount: Int' not in entry, 'legacy extension-only folder count field remains')

scan = block(main, 'private suspend fun scanTurnipFileManagerDirectory(', '\n\nprivate enum class SharedStorageBrowserMode')
for token in [
    'var inspectedZipFileCount = 0',
    'var validZipFileCount = 0',
    'if (!nested.name.endsWith(".zip", true)) continue',
    'if (inspectedZipFileCount >= 256)',
    'inspectedZipFileCount += 1',
    'if (inspectTurnipArchive(nested) != null) validZipFileCount += 1',
    'TurnipFolderEntry(canonical.path, validZipFileCount, zipCountLimited)',
]:
    need(token in scan, f'validated folder-count contract missing: {token}')
need('if (nestedCanonical.isFile' not in scan, 'legacy readable-.zip extension counter remains')

label = block(main, 'private fun turnipFolderCountLabel(', '\n\n@Composable\nprivate fun TurnipFileManagerFolderRow(')
need('val count = folder.validZipFileCount' in label, 'folder subtitle is not sourced from validated Turnip ZIP count')
need('folder.zipCountLimited' in label and '"${count}+ $suffix"' in label, 'bounded-count indicator regressed')

inspect = block(main, 'private suspend fun inspectTurnipArchive(', '\n\nprivate suspend fun scanTurnipFileManagerDirectory(')
for token in [
    'file.absoluteFile.path != canonicalFile.path',
    'compressedBytes <= 0L || compressedBytes > TURNIP_ARCHIVE_INPUT_MAX_BYTES',
    'if (++entryCount > 2048)',
    'if (fileBytes > 32L * 1024L * 1024L || totalBytes > 64L * 1024L * 1024L)',
    'if (metadataCount != 1)',
    'if (schemaVersion != 1)',
    '!libraryName.endsWith(".so", true)',
    '!libraryName.contains("vulkan", true)',
    'libraries.size != 1 || libraries.single().second <= 0L',
]:
    need(token in inspect, f'Turnip archive validity prerequisite regressed: {token}')

need(main.count('withContext(Dispatchers.IO) { scanTurnipFileManagerDirectory') >= 1, 'Turnip folder scan no longer executes on the established IO path')
need('ZipInputStream(BoundedDriverArchiveInputStream(input, TURNIP_ARCHIVE_INPUT_MAX_BYTES))' in main, 'authoritative import-time bounded ZIP validation regressed')
need('SystemDriverVendorBadge(reportRow.vendorId)' in main, 'Database GPU vendor badge regression reintroduced')
need('DatabaseSubmittedAt(reportRow.submittedAt)' in main, 'Database timestamp presentation regressed')
need('Dialog(' in block(main, '@Composable\nprivate fun FileManagerOptionsChooser(', '\nprivate fun fileManagerViewModeIcon('), '3.0.10 landscape-safe File Manager chooser regressed')

protected = {
    'app/src/main/AndroidManifest.xml': 'f89dab96d5c123cecb227d6a44f4deef5b8b92d2688e46fb55df9bd1c54f18a8',
    'app/src/main/cpp/vulkanscope.cpp': 'fa2d5aea65b493138aa7c0cdbfa25bd715fa6e2265daa4c5c6b70b777e62f3ff',
    'app/src/main/java/com/efishell/vulkanscope/AdvancedAnalysis.kt': '4bb7d65a1206873f0629792d3012139dcf3247157622e8200e40595104e62904',
    'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt': '6da56de2847870fbe2ad1777c951f405fafb3859b2552e3d60857686bc477544',
    'app/src/main/java/com/efishell/vulkanscope/VulkanDependencyGraph.kt': '6ca8bab0c89a28b322ccb449f8e2314f13597488d69fec6c1a263340c030d71d',
    'registry/registry_lock.json': '79c46ebb498997b31c56b3171296ff672d229c8434a31ac77c8a17f86e4f9d0a',
    'registry/generated/registry_query_manifest.json': '60136afc325fef0dfd1ea6bfaf57da6ae61096b96a241dd126ad0fdecc3f6d00',
}
for rel, expected in protected.items():
    need(sha(rel) == expected, f'protected predecessor-equivalent file drift: {rel}')

if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 3.0.11 validated Turnip folder-count verifier')
