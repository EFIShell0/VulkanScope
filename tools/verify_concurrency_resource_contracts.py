#!/usr/bin/env python3
import json
import re
from pathlib import Path

root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
service = (root / 'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt').read_text(encoding='utf-8')
manifest = (root / 'app/src/main/AndroidManifest.xml').read_text(encoding='utf-8')
cpp = (root / 'app/src/main/cpp/vulkanscope.cpp').read_text(encoding='utf-8')
errors = []
checks = {
    'probe coroutine mutex': 'private val probeMutex = Mutex()' in main and 'runServiceProbe(group: String, surface: Surface?, timeoutMs: Long, modeSnapshot: DriverMode): String = probeMutex.withLock' in main,
    'timeout starts after serialized service start': main.find('val started = runCatching') < main.find('withTimeout(timeoutMs)', main.find('private suspend fun runServiceProbe')),
    'dedicated process service': 'android:name=".VulkanProbeService" android:exported="false" android:process=":vulkan_probe"' in manifest,
    'single worker': 'Executors.newSingleThreadExecutor' in service,
    'fair native probe lock': 'ReentrantLock(true)' in service,
    'atomic service publication': 'Os.rename(temp.path, file.path)' in service,
    'atomic native publication': 'rename(tempPath.c_str(), path)' in cpp,
    'unique result path': 'java.util.UUID.randomUUID()' in main,
    'hard timeout process stop': 'private fun stopVulkanProbeProcess()' in main and 'runningVulkanProbePids().forEach { pid ->' in main and 'Process.killProcess(pid)' in main and bool(re.search(r'catch \(_: kotlinx\.coroutines\.TimeoutCancellationException\) \{.*?stopVulkanProbeProcess\(\)', main, re.S)),
    'worker cancellation on destroy': 'worker.shutdownNow()' in service,
    'native publication cap': 'kMaxProbePublishedBytes = 64ULL * 1024ULL * 1024ULL' in cpp,
    'main publication cap': 'val maxProbeResultBytes = 64L * 1024L * 1024L' in main,
    'service error publication cap': 'ProbeBoundedOutputStream(stream, 64L * 1024L * 1024L)' in service,
    'no JNI full-result jstring duplication': 'NewStringUTF(result.c_str())' not in cpp and service.count('): Boolean') >= 3,
    'exact-size main file read': 'val size = input.channel.size()' in main and 'val bytes = ByteArray(size.toInt())' in main,
    'database no-truncation guard': 'complete report exceeds the current VulkanScope Database 2 MiB transport limit. No data was truncated.' in main,
    'analysis no-truncation guard': 'Current analysis snapshot exceeds 8 MiB and was not truncated' in main,
}
for name, passed in checks.items():
    if not passed:
        errors.append(name)
if 'isolated Vulkan probe process' in main or 'isolated probe process' in main:
    errors.append('Android process-isolation terminology overclaims isolatedProcess semantics')
registry = json.loads((root / 'registry/generated/extension_reference.json').read_text(encoding='utf-8'))
registered = len(registry.get('entries', []))
max_ext_match = re.search(r'kMaxExtensionEntries\s*=\s*(\d+)', cpp)
max_ext = int(max_ext_match.group(1)) if max_ext_match else 0
budget = {
    'schemaVersion': 1,
    'probePublicationLimitBytes': 64 * 1024 * 1024,
    'databaseTransportLimitBytes': 2 * 1024 * 1024,
    'analysisSnapshotLimitBytes': 8 * 1024 * 1024,
    'registeredVulkanExtensionCensus': registered,
    'nativeExtensionEnumerationEntryLimit': max_ext,
    'baseProbeTimeoutMs': 20000,
    'surfaceProbeTimeoutMs': 25000,
    'selfTestTimeoutMs': 30000,
    'advancedProbeTimeoutMs': 30000,
    'simpleProbeTimeoutMs': 12000,
    'backgroundCollectionBudgetMs': 60000,
    'publicationPolicy': 'fail-closed without truncation',
    'memoryEvidence': 'Native success results are published directly to the bounded checkpoint file and no longer copied into a JNI jstring; the main process reads an atomically published inode into one exact-size byte array before UTF-8 decoding.',
    'absoluteCompletenessQualification': 'The 64 MiB per-probe publication ceiling is a safety ceiling, not a proof that every theoretically possible driver output fits. Oversize evidence fails explicitly and is never promoted as a complete report.'
}
(root / 'registry/generated/resource_budget.json').write_text(json.dumps(budget, indent=2) + '\n', encoding='utf-8')
if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print(f'PASS concurrency/resource contracts: registryExtensions={registered}/{max_ext} probeLimit={budget["probePublicationLimitBytes"]} databaseLimit={budget["databaseTransportLimitBytes"]} analysisLimit={budget["analysisSnapshotLimitBytes"]}')
