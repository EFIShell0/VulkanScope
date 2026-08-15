package com.efishell.vulkanscope

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.view.Surface
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class VulkanProbeService : Service() {
    companion object {
        private val PROCESS_NATIVE_PROBE_LOCK = ReentrantLock(true)
        const val EXTRA_QUERY_GROUP = "query_group"
        const val EXTRA_DRIVER_MODE = "driver_mode"
        const val EXTRA_DRIVER_ICD = "driver_icd"
        const val EXTRA_DRIVER_BUNDLE = "driver_bundle"
        const val EXTRA_HOOK_LIB_DIR = "hook_lib_dir"
        const val EXTRA_SURFACE = "surface"
        const val EXTRA_RESULT_PATH = "result_path"
    }
    private val worker: ExecutorService = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "VulkanProbeWorker")
    }
    private val stopHandler = Handler(Looper.getMainLooper())
    @Volatile private var latestStartId = 0
    private val delayedStop = Runnable { stopSelfResult(latestStartId) }
    private external fun collectVulkanData(surface: Surface?, driverMode: String, driverIcdPath: String?, driverBundlePath: String?, hookLibDir: String, resultPath: String): String
    private external fun collectVulkanSurfaceData(surface: Surface?, driverMode: String, driverIcdPath: String?, driverBundlePath: String?, hookLibDir: String, resultPath: String): String
    private external fun collectVulkanQueryData(group: String, driverMode: String, driverIcdPath: String?, driverBundlePath: String?, hookLibDir: String, resultPath: String): String

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val group = intent?.getStringExtra(EXTRA_QUERY_GROUP) ?: "base"
        latestStartId = startId
        stopHandler.removeCallbacks(delayedStop)
        val mode = intent?.getStringExtra(EXTRA_DRIVER_MODE) ?: "SYSTEM"
        val icd = intent?.getStringExtra(EXTRA_DRIVER_ICD)
        val bundle = intent?.getStringExtra(EXTRA_DRIVER_BUNDLE)
        val hook = intent?.getStringExtra(EXTRA_HOOK_LIB_DIR).orEmpty()
        val resultPath = intent?.getStringExtra(EXTRA_RESULT_PATH)
        if (resultPath.isNullOrBlank()) {
            stopSelfResult(startId)
            return START_NOT_STICKY
        }
        val cacheRoot = cacheDir.canonicalFile
        val requestedResult = runCatching { File(resultPath).canonicalFile }.getOrNull()
        if (requestedResult == null || !requestedResult.path.startsWith(cacheRoot.path + File.separator)) {
            return START_NOT_STICKY
        }
        val surface = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_SURFACE, Surface::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_SURFACE) as? Surface
        }
        worker.execute {
            try {
                System.loadLibrary("vulkanscope")
                val safeSurface = surface?.takeIf { it.isValid }
                val result = PROCESS_NATIVE_PROBE_LOCK.withLock {
                    when (group) {
                        "base" -> collectVulkanData(safeSurface, mode, icd, bundle, hook, resultPath)
                        "surface" -> collectVulkanSurfaceData(safeSurface, mode, icd, bundle, hook, resultPath)
                        else -> collectVulkanQueryData(group, mode, icd, bundle, hook, resultPath)
                    }
                }
                writeResult(resultPath, result)
            } catch (t: Throwable) {
                writeResult(resultPath, if (group == "base") {
                    "{\"status\":\"unavailable\",\"reason\":${org.json.JSONObject.quote(t.message ?: "Vulkan probe failed")},\"devices\":[]}"
                } else {
                    "{\"status\":\"unavailable\",\"group\":${org.json.JSONObject.quote(group)},\"reason\":${org.json.JSONObject.quote(t.message ?: "Vulkan query failed")},\"devices\":[]}"
                })
            } finally {
                stopHandler.removeCallbacks(delayedStop)
                stopHandler.postDelayed(delayedStop, 1500L)
            }
        }
        return START_NOT_STICKY
    }

    private fun writeResult(path: String, text: String) {
        runCatching {
            val file = File(path)
            file.parentFile?.mkdirs()
            val temp = File(file.parentFile, file.name + ".tmp")
            FileOutputStream(temp, false).use { it.write(text.toByteArray(Charsets.UTF_8)) }
            try {
                Files.move(temp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
            } catch (e: java.nio.file.AtomicMoveNotSupportedException) {
                Files.move(temp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
        }.onFailure { error ->
            Log.e("VulkanProbeWork", "Unable to publish Vulkan probe result", error)
        }
    }

    override fun onDestroy() {
        stopHandler.removeCallbacks(delayedStop)
        worker.shutdownNow()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
