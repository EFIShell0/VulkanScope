package com.efishell.vulkanscope

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Process
import android.view.Surface
import android.util.JsonReader
import android.util.JsonToken
import android.util.Log
import android.system.Os
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.InputStreamReader
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


private class ProbeBoundedOutputStream(
    private val delegate: OutputStream,
    private val maxBytes: Long
) : OutputStream() {
    private var written = 0L

    private fun reserve(count: Int) {
        if (count < 0 || written > maxBytes - count.toLong()) {
            throw IllegalStateException("Probe result exceeds the service publication safety limit")
        }
    }

    override fun write(value: Int) {
        reserve(1)
        delegate.write(value)
        written++
    }

    override fun write(buffer: ByteArray, offset: Int, length: Int) {
        if (offset < 0 || length < 0 || offset > buffer.size - length) throw IndexOutOfBoundsException()
        reserve(length)
        delegate.write(buffer, offset, length)
        written += length.toLong()
    }

    override fun flush() = delegate.flush()
    override fun close() = delegate.close()
}

open class VulkanProbeService : Service() {
    companion object {
        private val PROCESS_NATIVE_PROBE_LOCK = ReentrantLock(true)
        const val EXTRA_QUERY_GROUP = "query_group"
        const val EXTRA_DRIVER_MODE = "driver_mode"
        const val EXTRA_DRIVER_ICD = "driver_icd"
        const val EXTRA_DRIVER_BUNDLE = "driver_bundle"
        const val EXTRA_HOOK_LIB_DIR = "hook_lib_dir"
        const val EXTRA_SURFACE = "surface"
        const val EXTRA_RESULT_PATH = "result_path"
        const val EXTRA_TERMINAL_PATH = "terminal_path"
        const val EXTRA_TIMING_PATH = "timing_path"
        const val EXTRA_TIMEOUT_MS = "timeout_ms"
    }
    private val mainHandler = Handler(Looper.getMainLooper())
    private val terminationClaimed = AtomicBoolean(false)
    private val worker: ExecutorService = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "VulkanProbeWorker")
    }
    private external fun collectVulkanData(surface: Surface?, driverMode: String, driverIcdPath: String?, driverBundlePath: String?, hookLibDir: String, resultPath: String): Boolean
    private external fun collectVulkanSurfaceData(surface: Surface?, driverMode: String, driverIcdPath: String?, driverBundlePath: String?, hookLibDir: String, resultPath: String): Boolean
    private external fun collectVulkanQueryData(group: String, driverMode: String, driverIcdPath: String?, driverBundlePath: String?, hookLibDir: String, resultPath: String): Boolean

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val serviceStartNanos = System.nanoTime()
        val group = intent?.getStringExtra(EXTRA_QUERY_GROUP) ?: "base"
        val mode = intent?.getStringExtra(EXTRA_DRIVER_MODE) ?: "SYSTEM"
        val icd = intent?.getStringExtra(EXTRA_DRIVER_ICD)
        val bundle = intent?.getStringExtra(EXTRA_DRIVER_BUNDLE)
        val hook = intent?.getStringExtra(EXTRA_HOOK_LIB_DIR).orEmpty()
        val resultPath = intent?.getStringExtra(EXTRA_RESULT_PATH)
        val terminalPath = intent?.getStringExtra(EXTRA_TERMINAL_PATH)
        val timingPath = intent?.getStringExtra(EXTRA_TIMING_PATH)
        val timeoutMs = intent?.getLongExtra(EXTRA_TIMEOUT_MS, 0L) ?: 0L
        if (resultPath.isNullOrBlank() || terminalPath.isNullOrBlank() || timingPath.isNullOrBlank() || timeoutMs !in 1_000L..60_000L) {
            stopSelfResult(startId)
            return START_NOT_STICKY
        }
        val cacheRoot = cacheDir.canonicalFile
        val requestedResult = runCatching { File(resultPath).canonicalFile }.getOrNull()
        val requestedTerminal = runCatching { File(terminalPath).canonicalFile }.getOrNull()
        val requestedTiming = runCatching { File(timingPath).canonicalFile }.getOrNull()
        if (requestedResult == null || requestedTerminal == null || requestedTiming == null ||
            !requestedResult.path.startsWith(cacheRoot.path + File.separator) ||
            !requestedTerminal.path.startsWith(cacheRoot.path + File.separator) ||
            !requestedTiming.path.startsWith(cacheRoot.path + File.separator) ||
            requestedTerminal.path != requestedResult.path + ".done" ||
            requestedTiming.path != requestedResult.path + ".timing") {
            stopSelfResult(startId)
            return START_NOT_STICKY
        }
        val safeResultPath = requestedResult.path
        val safeTerminalPath = requestedTerminal.path
        val safeTimingPath = requestedTiming.path
        val hardTimeoutWatchdog = Runnable {
            terminateDedicatedProcess("Hard probe deadline reached; terminating the dedicated probe process", true)
        }
        mainHandler.postDelayed(hardTimeoutWatchdog, timeoutMs + 100L)
        val surface = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_SURFACE, Surface::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_SURFACE) as? Surface
        }
        worker.execute {
            val workerStartNanos = System.nanoTime()
            var published = false
            var libraryLoadMs: Long? = null
            var nativeCallMs: Long? = null
            try {
                val libraryLoadStartNanos = System.nanoTime()
                System.loadLibrary("vulkanscope")
                libraryLoadMs = elapsedProbeMillis(libraryLoadStartNanos)
                val safeSurface = surface?.takeIf { it.isValid }
                val nativeCallStartNanos = System.nanoTime()
                published = PROCESS_NATIVE_PROBE_LOCK.withLock {
                    when (group) {
                        "base" -> collectVulkanData(safeSurface, mode, icd, bundle, hook, safeResultPath)
                        "surface" -> collectVulkanSurfaceData(safeSurface, mode, icd, bundle, hook, safeResultPath)
                        else -> collectVulkanQueryData(group, mode, icd, bundle, hook, safeResultPath)
                    }
                }
                nativeCallMs = elapsedProbeMillis(nativeCallStartNanos)
                if (!published) throw IllegalStateException("Vulkan probe result could not be published within the safety limit")
            } catch (t: Throwable) {
                published = writeResult(safeResultPath, if (group == "base") {
                    "{\"status\":\"unavailable\",\"reason\":${org.json.JSONObject.quote(t.message ?: "Vulkan probe failed")},\"baseReportComplete\":false,\"devices\":[]}"
                } else {
                    "{\"status\":\"unavailable\",\"group\":${org.json.JSONObject.quote(group)},\"reason\":${org.json.JSONObject.quote(t.message ?: "Vulkan query failed")},\"devices\":[]}"
                })
            } finally {
                mainHandler.removeCallbacks(hardTimeoutWatchdog)
                val validationStartNanos = System.nanoTime()
                var terminalPayloadValid = published && validateTerminalResult(safeResultPath, group)
                if (published && !terminalPayloadValid) {
                    Log.e("VulkanProbeWork", "Native probe returned after publishing malformed or non-terminal JSON; replacing it with explicit unavailable evidence")
                    published = writeResult(safeResultPath, if (group == "base") {
                        "{\"status\":\"unavailable\",\"reason\":\"The native Vulkan base probe completed but produced invalid terminal JSON.\",\"baseReportComplete\":false,\"devices\":[]}"
                    } else {
                        "{\"status\":\"unavailable\",\"group\":${org.json.JSONObject.quote(group)},\"reason\":\"The native Vulkan query completed but produced invalid terminal JSON.\",\"devices\":[]}"
                    })
                    terminalPayloadValid = published && validateTerminalResult(safeResultPath, group)
                }
                val validationMs = elapsedProbeMillis(validationStartNanos)
                val timingPayload = org.json.JSONObject()
                    .put("group", group)
                    .put("dispatchToWorkerMs", ((workerStartNanos - serviceStartNanos) / 1_000_000L).coerceAtLeast(0L))
                    .put("libraryLoadMs", libraryLoadMs ?: org.json.JSONObject.NULL)
                    .put("nativeCallMs", nativeCallMs ?: org.json.JSONObject.NULL)
                    .put("terminalValidationMs", validationMs)
                    .put("servicePreTerminalMs", elapsedProbeMillis(serviceStartNanos))
                    .toString()
                val timingPublished = writeTelemetry(safeTimingPath, timingPayload)
                if (!timingPublished) Log.w("VulkanProbeWork", "Unable to publish bounded probe timing telemetry")
                val terminalPublished = terminalPayloadValid && writeResult(safeTerminalPath, "done")
                if (published && !terminalPublished) {
                    Log.e("VulkanProbeWork", "Unable to publish Vulkan probe terminal marker after JNI return and terminal validation")
                }
                surface?.release()
                terminateDedicatedProcess(
                    if (terminalPublished) {
                        "Terminal result and service-owned completion marker are durable; terminating the one-shot probe process"
                    } else {
                        "Probe worker finished without a durable completion marker; terminating the one-shot probe process"
                    }
                )
            }
        }
        return START_NOT_STICKY
    }


    private fun elapsedProbeMillis(startNanos: Long): Long = ((System.nanoTime() - startNanos) / 1_000_000L).coerceAtLeast(0L)

    private fun writeTelemetry(path: String, text: String): Boolean {
        if (text.toByteArray(Charsets.UTF_8).size > 16 * 1024) return false
        return runCatching {
            val file = File(path)
            file.parentFile?.mkdirs()
            val temp = File(file.parentFile, file.name + ".tmp")
            FileOutputStream(temp, false).use { stream ->
                OutputStreamWriter(stream, Charsets.UTF_8).use { writer ->
                    writer.write(text)
                    writer.flush()
                }
            }
            Os.rename(temp.path, file.path)
            true
        }.onFailure { error ->
            runCatching { File(path + ".tmp").delete() }
            Log.w("VulkanProbeWork", "Unable to publish probe timing telemetry", error)
        }.getOrDefault(false)
    }


    private fun validateTerminalResult(path: String, group: String): Boolean {
        val file = File(path)
        if (!file.isFile || file.length() !in 1L..(64L * 1024L * 1024L)) return false
        return runCatching {
            FileInputStream(file).use { input ->
                InputStreamReader(input, Charsets.UTF_8).use { text ->
                    JsonReader(text).use json@ { reader ->
                        reader.isLenient = false
                        if (reader.peek() != JsonToken.BEGIN_OBJECT) return@json false
                        var status: String? = null
                        var baseComplete = false
                        reader.beginObject()
                        while (reader.hasNext()) {
                            when (reader.nextName()) {
                                "status" -> status = if (reader.peek() == JsonToken.STRING) reader.nextString() else { reader.skipValue(); null }
                                "baseReportComplete" -> baseComplete = if (reader.peek() == JsonToken.BOOLEAN) reader.nextBoolean() else { reader.skipValue(); false }
                                else -> reader.skipValue()
                            }
                        }
                        reader.endObject()
                        if (reader.peek() != JsonToken.END_DOCUMENT) return@json false
                        group != "base" || baseComplete || status == "unavailable" || status == "incomplete" || status == "not_applicable"
                    }
                }
            }
        }.getOrDefault(false)
    }

    private fun writeResult(path: String, text: String): Boolean {
        return runCatching {
            val file = File(path)
            file.parentFile?.mkdirs()
            val temp = File(file.parentFile, file.name + ".tmp")
            FileOutputStream(temp, false).use { stream ->
                val bounded = ProbeBoundedOutputStream(stream, 64L * 1024L * 1024L)
                OutputStreamWriter(bounded, Charsets.UTF_8).use { writer ->
                    writer.write(text)
                    writer.flush()
                    stream.fd.sync()
                }
            }
            Os.rename(temp.path, file.path)
            true
        }.onFailure { error ->
            runCatching { File(path + ".tmp").delete() }
            Log.e("VulkanProbeWork", "Unable to publish Vulkan probe result", error)
        }.getOrDefault(false)
    }

    private fun terminateDedicatedProcess(reason: String, error: Boolean = false) {
        if (!terminationClaimed.compareAndSet(false, true)) return
        if (error) Log.e("VulkanProbeWork", reason) else Log.i("VulkanProbeWork", reason)
        Process.killProcess(Process.myPid())
    }

    override fun onDestroy() {
        worker.shutdownNow()
        mainHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
        terminateDedicatedProcess("Probe service teardown requested; terminating the dedicated process")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

class VulkanProbeServiceBg0 : VulkanProbeService()
class VulkanProbeServiceBg1 : VulkanProbeService()
class VulkanProbeServiceBg2 : VulkanProbeService()
class VulkanProbeServiceBg3 : VulkanProbeService()
class VulkanProbeServiceBg4 : VulkanProbeService()
class VulkanProbeServiceBg5 : VulkanProbeService()
