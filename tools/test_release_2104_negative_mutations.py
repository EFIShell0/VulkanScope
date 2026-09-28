#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path
source = Path(__file__).resolve().parents[1]
verifier = source / 'tools/verify_release_2104.py'
mutations = [
    ('sticky_import', 'import androidx.compose.foundation.lazy.LazyListScope\n', 'import androidx.compose.foundation.lazy.LazyListScope\nimport androidx.compose.foundation.lazy.stickyHeader\n'),
    ('video_model', 'private data class VideoProfileEvidence(', 'private data class VideoProfileEvidenceRemoved('),
    ('video_parser', 'private fun parseVulkanVideoEvidence(', 'private fun parseVulkanVideoEvidenceRemoved('),
    ('video_state', 'private fun videoEvidenceState(', 'private fun videoEvidenceStateRemoved('),
    ('video_label', 'private fun videoPropertyLabel(', 'private fun videoPropertyLabelRemoved('),
    ('queue_video_query', 'CapabilityKeyValue("Video codec query", queueVideoCodecQueryState(queue))', 'CapabilityKeyValue("Queue index", queue.index.toString())'),
]
with tempfile.TemporaryDirectory(prefix='vs2104-neg-') as td:
    base = Path(td) / 'base'
    shutil.copytree(source, base, ignore=shutil.ignore_patterns('.gradle', 'build', '.cxx', '*.zip', '*.apk', '__pycache__'))
    for name, old, new in mutations:
        tree = Path(td) / name
        shutil.copytree(base, tree)
        main = tree / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
        data = main.read_text(encoding='utf-8')
        if old not in data:
            raise SystemExit(f'mutation anchor missing: {name}')
        main.write_text(data.replace(old, new, 1), encoding='utf-8')
        result = subprocess.run([sys.executable, str(verifier), '--root', str(tree)], capture_output=True, text=True)
        if result.returncode == 0:
            raise SystemExit(f'negative mutation escaped verifier: {name}')
    doc = Path(td) / 'doc_only'
    shutil.copytree(base, doc)
    audit = doc / 'BUILD_AUDIT.md'
    audit.write_text(audit.read_text(encoding='utf-8') + '\n2.1.4 false-positive control.\n', encoding='utf-8')
    result = subprocess.run([sys.executable, str(verifier), '--root', str(doc)], capture_output=True, text=True)
    if result.returncode != 0:
        raise SystemExit('documentation-only mutation incorrectly rejected\n' + result.stdout + result.stderr)
print('PASS VulkanScope 2.1.4 negative mutations and false-positive guard')
