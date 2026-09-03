#!/usr/bin/env python3
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

root = Path(__file__).resolve().parents[1]
verifier = root / 'tools/verify_ui_information_architecture_04146.py'
main_rel = Path('app/src/main/java/com/efishell/vulkanscope/MainActivity.kt')

def verify(tree):
    result = subprocess.run([sys.executable, str(verifier), '--root', str(tree)], text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    return result.returncode, result.stdout

def mutate(name, transform, should_fail=True):
    with tempfile.TemporaryDirectory(prefix=f'vs-ui-{name}-') as temp:
        tree = Path(temp) / 'tree'
        shutil.copytree(root, tree, ignore=shutil.ignore_patterns('.gradle', 'build', '__pycache__'))
        path = tree / main_rel
        text = path.read_text(encoding='utf-8')
        updated = transform(text)
        if updated == text:
            raise SystemExit(f'FAIL mutation did not change source: {name}')
        path.write_text(updated, encoding='utf-8')
        code, output = verify(tree)
        if should_fail and code == 0:
            raise SystemExit(f'FAIL verifier accepted targeted mutation: {name}\n{output}')
        if not should_fail and code != 0:
            raise SystemExit(f'FAIL false-positive control rejected: {name}\n{output}')

mutate('analysis-route', lambda s: s.replace('Video("Vulkan Video")', 'Video("Vulkan Video"), Analysis("Analysis")', 1))
mutate('analysis-before-snapshot', lambda s: s.replace('Page.Analysis -> AnalysisPage(report, device, display, driverMode)', 'Page.Analysis -> EmptyState("Analysis unavailable")', 1))
mutate('missing-page-wrapper', lambda s: s.replace('private fun VulkanPage(report: VulkanReport, device: DeviceReport?, turnipSupport: TurnipSupport) {\n    VulkanLazyPage(', 'private fun VulkanPage(report: VulkanReport, device: DeviceReport?, turnipSupport: TurnipSupport) {\n    LazyColumn(', 1))
mutate('reversed-up-boundary', lambda s: s.replace('derivedStateOf { listState.canScrollBackward }', 'derivedStateOf { listState.canScrollForward }', 1))
mutate('missing-video-route', lambda s: s.replace('Page.Video -> VulkanVideoPage(device)', 'Page.Video -> EmptyState("Video")', 1))
mutate('video-available-path-unknown', lambda s: s.replace('if (evidence.profiles.isNotEmpty()) "Available: exact-profile Vulkan Video census evidence was collected."', 'if (evidence.profiles.isNotEmpty()) "Unknown: exact-profile Vulkan Video census evidence was collected."', 1))
mutate('library-version-drift', lambda s: s.replace('LibraryVersionInfo("OkHttp", "5.5.0"', 'LibraryVersionInfo("OkHttp", "5.4.0"', 1))
mutate('unrelated-text', lambda s: s.replace('Detailed Vulkan inspection areas', 'Detailed Vulkan inspection areas and tools', 1), should_fail=False)
print('PASS 0.41.46 UI information architecture negative mutations')
