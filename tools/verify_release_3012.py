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
audit = read('rules/3.0.12_TURNIP_FOLDER_SCAN_COMPILE_FIX_AUDIT.md')
changelog = read('changelog.md')

need('versionCode = 3012' in gradle and 'versionName = "3.0.12"' in gradle, '3.0.12 release identity missing')
need(changelog.startswith('## 3.0.12\n'), '3.0.12 changelog entry missing or not first')
need('## Release 3.0.12 Turnip folder-scan Kotlin compile restoration requirements' in rules, '3.0.12 project rules missing')
need('# VulkanScope 3.0.12 Turnip Folder Scan Compile Fix Audit' in audit, '3.0.12 audit heading missing')
need('// ' not in main and '/*' not in main, 'source-code comment rule violated in MainActivity.kt')

scan = block(main, 'private suspend fun scanTurnipFileManagerDirectory(', '\n\nprivate enum class SharedStorageBrowserMode')
for token in [
    'val folders = ArrayList<TurnipFolderEntry>()',
    'for (child in children)',
    'coroutineContext.ensureActive()',
    'if (inspectedZipFileCount >= 256)',
    'if (inspectTurnipArchive(nested) != null) validZipFileCount += 1',
    'folders += TurnipFolderEntry(canonical.path, validZipFileCount, zipCountLimited)',
    'catch (cancelled: CancellationException)',
    'throw cancelled',
]:
    need(token in scan, f'compile/cancellation repair contract missing: {token}')
need('children.asSequence().mapNotNull' not in scan, 'non-suspend Sequence folder lambda reintroduced')
need('runCatching {' not in scan, 'non-suspend runCatching folder wrapper reintroduced')

label = block(main, 'private fun turnipFolderCountLabel(', '\n\n@Composable\nprivate fun TurnipFileManagerFolderRow(')
need('val count = folder.validZipFileCount' in label, '3.0.11 validated folder subtitle regressed')
need('folder.zipCountLimited' in label and '"${count}+ $suffix"' in label, 'bounded-count marker regressed')

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

need(main.count('withContext(Dispatchers.IO) { scanTurnipFileManagerDirectory') >= 1, 'Turnip scan no longer uses established IO path')
need('ZipInputStream(BoundedDriverArchiveInputStream(input, TURNIP_ARCHIVE_INPUT_MAX_BYTES))' in main, 'authoritative import validation regressed')

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
print('PASS VulkanScope 3.0.12 Turnip folder-scan compile verifier')
