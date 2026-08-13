package com.efishell.vulkanscope

import android.app.Activity
import android.os.Bundle
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class Vulkan14ProbeActivity : Activity() {
    companion object {
        const val EXTRA_QUERY_GROUP = "query_group"
        const val EXTRA_DRIVER_MODE = "driver_mode"
        const val EXTRA_DRIVER_ICD = "driver_icd"
        const val EXTRA_DRIVER_BUNDLE = "driver_bundle"
        const val EXTRA_HOOK_LIB_DIR = "hook_lib_dir"
        const val EXTRA_RESULT_PATH = "result_path"
    }

    private external fun collectVulkan14Data(driverMode: String, driverIcdPath: String?, driverBundlePath: String?, hookLibDir: String): String
    private external fun collectVulkanExtensionGroupData(group: String, driverMode: String, driverIcdPath: String?, driverBundlePath: String?, hookLibDir: String): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            System.loadLibrary("vulkanscope")
        } catch (_: Throwable) {
            setResult(RESULT_CANCELED)
            finish()
            return
        }
        window.setDimAmount(0f)
        window.decorView.alpha = 0f
        val group = intent.getStringExtra(EXTRA_QUERY_GROUP) ?: "vulkan14"
        val mode = intent.getStringExtra(EXTRA_DRIVER_MODE) ?: "SYSTEM"
        val icd = intent.getStringExtra(EXTRA_DRIVER_ICD)
        val bundle = intent.getStringExtra(EXTRA_DRIVER_BUNDLE)
        val hook = intent.getStringExtra(EXTRA_HOOK_LIB_DIR).orEmpty()
        Thread {
            val result = runCatching {
                if (group == "vulkan14") collectVulkan14Data(mode, icd, bundle, hook) else collectVulkanExtensionGroupData(group, mode, icd, bundle, hook)
            }.getOrElse { e ->
                "{\"status\":\"unavailable\",\"group\":${quote(group)},\"reason\":${quote(e.message ?: "Isolated Vulkan query failed")},\"devices\":[]}"
            }
            val file = File(cacheDir, "vulkan_probe_${UUID.randomUUID()}.json")
            runOnUiThread {
                runCatching {
                    FileOutputStream(file).use { it.write(result.toByteArray(Charsets.UTF_8)) }
                    setResult(RESULT_OK, intent.putExtra(EXTRA_RESULT_PATH, file.absolutePath))
                }.onFailure { setResult(RESULT_CANCELED) }
                finish()
            }
        }.start()
    }

    private fun quote(value: String): String = org.json.JSONObject.quote(value)
}
