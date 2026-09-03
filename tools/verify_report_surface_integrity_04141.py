import argparse
import re
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--root')
args = parser.parse_args()
root = Path(args.root).resolve() if args.root else Path(__file__).resolve().parents[1]
cpp = (root / 'app/src/main/cpp/vulkanscope.cpp').read_text(encoding='utf-8')
kt = (root / 'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt').read_text(encoding='utf-8')
errors = []

def need(condition, message):
    if not condition:
        errors.append(message)

auto_start = cpp.find('template <typename T> void generatedEmitAuto(')
auto_end = cpp.find('#include <extension_field_coverage_generated.inc>', auto_start)
auto = cpp[auto_start:auto_end] if auto_start >= 0 and auto_end > auto_start else ''
need('std::is_same_v<std::remove_cv_t<E>, uint8_t>' in auto, 'uint8 array raw-byte branch missing')
need('reinterpret_cast<const uint8_t*>(value), sizeof(value)' in auto, 'uint8 array exact raw byte serialization missing')
merge_start = cpp.find('for (const auto& generated : api.generatedFields)')
merge_end = cpp.find('api.generatedFields.clear();', merge_start)
merge = cpp[merge_start:merge_end] if merge_start >= 0 and merge_end > merge_start else ''
need('genericPropertySection' in cpp, 'generic property section identity missing')
need('propertyValuesEquivalent' in cpp, 'property value equivalence missing')
need('existing.section == generated.section && existing.name == generated.name' in merge, 'property dedup is not section-aware')
need('existing.section == genericPropertySection && existing.name == generated.name' in merge, 'manual-to-generated property provenance upgrade missing')
need('private var surfaceGeneration = 0L' in kt, 'surface generation token missing')
need('private var surfaceRefreshPending = false' in kt, 'surface refresh pending state missing')
need('surfaceDestroyed: (Surface) -> Unit' in kt or 'surfaceDestroyed: (Long, Surface) -> Unit' in kt, 'destroyed surface identity callback missing')
need('override fun surfaceDestroyed(holder: SurfaceHolder) { onDestroyed(holder.surface) }' in kt or 'override fun surfaceDestroyed(holder: SurfaceHolder) { onDestroyed(hostGeneration, holder.surface) }' in kt, 'destroyed surface identity forwarding missing')
refresh_start = kt.find('private fun requestSurfaceRefresh(surface: Surface)')
refresh_end = kt.find('private suspend fun startBackgroundInformationCollection', refresh_start)
refresh = kt[refresh_start:refresh_end] if refresh_start >= 0 and refresh_end > refresh_start else ''
need('surfaceRefreshPending = true' in refresh, 'surface refresh does not defer while collection is active')
need('if (collectionInFlight || pendingCollectionTasks.isNotEmpty()) {\n            collectionPending = true' not in refresh, 'surface-only refresh still requests a full report recollection')
need('surfaceGeneration == surfaceToken' in refresh, 'surface refresh stale-generation gate missing')
enrich_start = kt.find('private suspend fun enrichReport(')
enrich_end = kt.find('private suspend fun runIsolatedProbe', enrich_start)
enrich = kt[enrich_start:enrich_end] if enrich_start >= 0 and enrich_end > enrich_start else ''
need('surfaceGeneration' in enrich and 'currentSurface === surface' in enrich, 'enrichment stale-surface merge gate missing')
probe_match = re.search(r'private suspend fun runSurfaceProbe\(.*?\n\s*private fun runningVulkanProbePids', kt, re.S)
probe = probe_match.group(0) if probe_match else ''
need('VkResult=-1000000001' in probe, 'native-window-in-use bounded retry detector missing')
need(probe.count('runServiceProbe("surface"') == 2, 'surface retry is not exactly one bounded retry')
need('delay(' in probe, 'surface retry delay missing')
if errors:
    for error in errors:
        print('FAIL:', error)
    raise SystemExit(1)
print('PASS VulkanScope 0.41.41 report/surface integrity contract')
