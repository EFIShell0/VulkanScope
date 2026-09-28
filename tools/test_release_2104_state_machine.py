#!/usr/bin/env python3
from pathlib import Path
root = Path(__file__).resolve().parents[1]
main = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
assert 'import androidx.compose.foundation.lazy.stickyHeader' not in main
for token in [
    'private data class VideoProfileEvidence(',
    'private data class VulkanVideoEvidence(',
    'private fun parseVulkanVideoEvidence(',
    'private fun videoEvidenceState(',
    'private fun videoPropertyLabel(',
    'stickyHeader(key = pagerKey)',
    'CapabilityKeyValue("Video codec query", queueVideoCodecQueryState(queue))',
    'CapabilityKeyValue("Video codec operations", videoCodecOperationFlags(queue.videoCodecOperations))',
]:
    assert token in main, token
print('PASS VulkanScope 2.1.4 Kotlin compile-restoration state model')
