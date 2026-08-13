package com.efishell.vulkanscope

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.view.Surface
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread

class VulkanProbeService : Service() {
    companion object {
        const val EXTRA_QUERY_GROUP = "query_group"
        const val EXTRA_DRIVER_MODE = "driver_mode"
        const val EXTRA_DRIVER_ICD = "driver_icd"
        const val EXTRA_DRIVER_BUNDLE = "driver_bundle"
        const val EXTRA_HOOK_LIB_DIR = "hook_lib_dir"
        const val EXTRA_SURFACE = "surface"
        const val EXTRA_RESULT_PATH = "result_path"
    }

    private external fun collectVulkanData(surface: Surface?, driverMode: String, driverIcdPath: String?, driverBundlePath: String?, hookLibDir: String, resultPath: String): String
    private external fun collectVulkanQueryData(group: String, driverMode: String, driverIcdPath: String?, driverBundlePath: String?, hookLibDir: String, resultPath: String): String

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val group = intent?.getStringExtra(EXTRA_QUERY_GROUP) ?: "base"
        val mode = intent?.getStringExtra(EXTRA_DRIVER_MODE) ?: "SYSTEM"
        val icd = intent?.getStringExtra(EXTRA_DRIVER_ICD)
        val bundle = intent?.getStringExtra(EXTRA_DRIVER_BUNDLE)
        val hook = intent?.getStringExtra(EXTRA_HOOK_LIB_DIR).orEmpty()
        val resultPath = intent?.getStringExtra(EXTRA_RESULT_PATH)
        if (resultPath.isNullOrBlank()) {
            stopSelf(startId)
            return START_NOT_STICKY
        }
        val surface = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_SURFACE, Surface::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_SURFACE) as? Surface
        }
        thread(start = true) {
            try {
                System.loadLibrary("vulkanscope")
                val result = if (group == "base") {
                    collectVulkanData(surface, mode, icd, bundle, hook, resultPath)
                } else {
                    collectVulkanQueryData(group, mode, icd, bundle, hook, resultPath)
                }
                writeResult(resultPath, result)
            } catch (t: Throwable) {
                writeResult(resultPath, if (group == "base") {
                    "{\"status\":\"unavailable\",\"reason\":${org.json.JSONObject.quote(t.message ?: "Vulkan probe failed")},\"devices\":[]}"
                } else {
                    "{\"status\":\"unavailable\",\"group\":${org.json.JSONObject.quote(group)},\"reason\":${org.json.JSONObject.quote(t.message ?: "Vulkan query failed")},\"devices\":[]}"
                })
            } finally {
                stopSelf(startId)
            }
        }
        return START_NOT_STICKY
    }

    private fun writeResult(path: String, text: String) {
        runCatching {
            val file = File(path)
            file.parentFile?.mkdirs()
            FileOutputStream(file, false).use { it.write(text.toByteArray(Charsets.UTF_8)) }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
