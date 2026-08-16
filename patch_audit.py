from pathlib import Path

root=Path('/mnt/data/audit217')
main=root/'app/src/main/java/com/efishell/vulkanscope/MainActivity.kt'
service=root/'app/src/main/java/com/efishell/vulkanscope/VulkanProbeService.kt'
cpp=root/'app/src/main/cpp/vulkanscope.cpp'
build=root/'app/build.gradle.kts'

s=main.read_text()
s=s.replace('import android.graphics.Color\n', 'import android.app.ActivityManager\nimport android.graphics.Color\n')
s=s.replace('import android.os.Build\n', 'import android.os.Build\nimport android.os.Process\n')

old='''    private suspend fun runProbe(surface: Surface?): String = runServiceProbe("base", surface, 45_000L)\n\n    private suspend fun runSurfaceProbe(surface: Surface): String = runServiceProbe("surface", surface, 25_000L)\n\n    private suspend fun runServiceProbe(group: String, surface: Surface?, timeoutMs: Long): String {\n'''
new='''    private suspend fun runProbe(surface: Surface?): String = runServiceProbe("base", surface, 45_000L)\n\n    private suspend fun runSurfaceProbe(surface: Surface): String = runServiceProbe("surface", surface, 25_000L)\n\n    private fun stopVulkanProbeProcess() {\n        runCatching { stopService(Intent(this@MainActivity, VulkanProbeService::class.java)) }\n        runCatching {\n            val activityManager = getSystemService(ActivityManager::class.java) ?: return\n            val expectedName = "${packageName}:vulkan_probe"\n            activityManager.runningAppProcesses.orEmpty()\n                .filter { it.uid == Process.myUid() && it.processName == expectedName }\n                .forEach { process -> Process.killProcess(process.pid) }\n        }.onFailure { error ->\n            Log.w("VulkanScope", "Unable to stop the isolated Vulkan probe process", error)\n        }\n    }\n\n    private suspend fun runServiceProbe(group: String, surface: Surface?, timeoutMs: Long): String {\n        val maxProbeResultBytes = 64L * 1024L * 1024L\n'''
if old not in s: raise SystemExit('runServiceProbe header not found')
s=s.replace(old,new)

old='''                    while (value == null) {\n                        if (resultFile.isFile && resultFile.length() > 0L) {\n                            val candidate = runCatching { resultFile.readText() }.getOrElse { error ->\n'''
new='''                    while (value == null) {\n                        if (resultFile.isFile) {\n                            val resultLength = resultFile.length()\n                            if (resultLength > maxProbeResultBytes) {\n                                value = if (group == "base") {\n                                    "{\\"status\\":\\"unavailable\\",\\"reason\\":\\"The Vulkan probe result exceeded the safety size limit.\\",\\"devices\\":[]}"\n                                } else {\n                                    "{\\"status\\":\\"unavailable\\",\\"group\\":${JSONObject.quote(group)},\\"reason\\":\\"The Vulkan query result exceeded the safety size limit.\\",\\"devices\\":[]}"\n                                }\n                                stopVulkanProbeProcess()\n                                continue\n                            }\n                            if (resultLength > 0L) {\n                            val candidate = runCatching { resultFile.readText() }.getOrElse { error ->\n'''
if old not in s: raise SystemExit('poll block not found')
s=s.replace(old,new)
old='''                            } else {\n                                kotlinx.coroutines.delay(60L)\n                            }\n'''
new='''                            }\n                        } else {\n                            kotlinx.coroutines.delay(60L)\n                        }\n'''
# Replace only first matching following poll block.
pos=s.find(old, s.find('private suspend fun runServiceProbe'))
if pos==-1: raise SystemExit('else block not found')
s=s[:pos]+new+s[pos+len(old):]
old='''            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {\n                value = partialCandidate ?: if (group == "base") {\n'''
new='''            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {\n                stopVulkanProbeProcess()\n                value = partialCandidate ?: if (group == "base") {\n'''
if old not in s: raise SystemExit('timeout catch not found')
s=s.replace(old,new,1)

# status badge normalization
old='''        val lower = value.lowercase()\n'''
new='''        val lower = value.trim().lowercase().replace('_', ' ')\n'''
if old not in s: raise SystemExit('status lower not found')
s=s.replace(old,new,1)

# Merge format2 results into the Formats model as well as the detailed property list.
needle='''    val videoQueuesByDevice: List<Triple<Long, Long, Map<Int, Long>>> = if (group == "queue2") {\n'''
insert='''    val formatEntriesByDevice: Map<Pair<Long, Long>, List<FormatEntry>> = if (group == "format2") {\n        resultDevices.asSequence().mapNotNull { item ->\n            val vendor = item.optLong("vendorId", -1L)\n            val deviceId = item.optLong("deviceId", -1L)\n            val properties = item.optJSONArray("properties") ?: JSONArray()\n            val entries = (0 until properties.length()).mapNotNull { index ->\n                val prop = properties.optJSONObject(index) ?: return@mapNotNull null\n                val name = prop.optString("name")\n                val value = prop.optString("value")\n                val linear = Regex("(?:^|, )linear=0x([0-9a-fA-F]+)").find(value)?.groupValues?.getOrNull(1)?.toLongOrNull(16) ?: return@mapNotNull null\n                val optimal = Regex("(?:^|, )optimal=0x([0-9a-fA-F]+)").find(value)?.groupValues?.getOrNull(1)?.toLongOrNull(16) ?: return@mapNotNull null\n                val buffer = Regex("(?:^|, )buffer=0x([0-9a-fA-F]+)").find(value)?.groupValues?.getOrNull(1)?.toLongOrNull(16) ?: return@mapNotNull null\n                FormatEntry(name, linear != 0L || optimal != 0L || buffer != 0L, linear, optimal, buffer)\n            }\n            (vendor to deviceId) to entries\n        }.toMap()\n    } else {\n        emptyMap()\n    }\n    val videoQueuesByDevice: List<Triple<Long, Long, Map<Int, Long>>> = if (group == "queue2") {\n'''
if needle not in s: raise SystemExit('format merge insertion point not found')
s=s.replace(needle,insert,1)
old='''        val videoMatch = videoQueuesByDevice.firstOrNull { it.first == device.vendorIdRaw && it.second == deviceId }\n        val mergedQueues = if (videoMatch != null) device.queues.map { q -> q.copy(videoCodecOperations = videoMatch.third[q.index] ?: q.videoCodecOperations) } else device.queues\n'''
new='''        val videoMatch = videoQueuesByDevice.firstOrNull { it.first == device.vendorIdRaw && it.second == deviceId }\n        val formatMatch = formatEntriesByDevice[device.vendorIdRaw to (deviceId ?: -1L)]\n        val mergedQueues = if (videoMatch != null) device.queues.map { q -> q.copy(videoCodecOperations = videoMatch.third[q.index] ?: q.videoCodecOperations) } else device.queues\n        val mergedFormats = if (formatMatch != null) {\n            val byName = LinkedHashMap<String, FormatEntry>()\n            device.formats.forEach { byName[it.name] = it }\n            formatMatch.forEach { byName[it.name] = it }\n            byName.values.toList()\n        } else {\n            device.formats\n        }\n'''
if old not in s: raise SystemExit('merge format section not found')
s=s.replace(old,new,1)
old='''        var merged = device.copy(features = mergedFeatures, detailedProperties = replaceQueryStatus(mergedProperties, "$label query", statusProperty.value), queues = mergedQueues)\n'''
new='''        var merged = device.copy(features = mergedFeatures, detailedProperties = replaceQueryStatus(mergedProperties, "$label query", statusProperty.value), queues = mergedQueues, formats = mergedFormats)\n'''
if old not in s: raise SystemExit('merged copy not found')
s=s.replace(old,new,1)

main.write_text(s)

# Remove forbidden source comment.
c=cpp.read_text()
c=c.replace('    const bool hasLiveSurface = false; // Base probe is intentionally surface-independent; WSI is queried by the isolated surface probe.\n', '    const bool hasLiveSurface = false;\n')
cpp.write_text(c)

# Update current stable AndroidX versions verified from Android Developers release tables.
b=build.read_text()
b=b.replace('implementation("androidx.core:core-ktx:1.17.0")','implementation("androidx.core:core-ktx:1.17.0")')
b=b.replace('implementation("androidx.activity:activity-compose:1.11.0")','implementation("androidx.activity:activity-compose:1.13.0")')
b=b.replace('implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.3")','implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.4")')
# version bump
b=b.replace('versionCode = 134','versionCode = 135').replace('versionName = "0.21.7"','versionName = "0.21.8"')
build.write_text(b)

print('patched')
