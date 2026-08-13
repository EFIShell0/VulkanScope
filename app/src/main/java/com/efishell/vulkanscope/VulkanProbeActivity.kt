package com.efishell.vulkanscope

import android.app.Activity
import android.os.Bundle
import android.view.Surface
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class VulkanProbeActivity : Activity() {
    companion object {
        const val EXTRA_DRIVER_MODE = "driver_mode"
        const val EXTRA_DRIVER_ICD = "driver_icd"
        const val EXTRA_DRIVER_BUNDLE = "driver_bundle"
        const val EXTRA_HOOK_LIB_DIR = "hook_lib_dir"
        const val EXTRA_SURFACE = "surface"
        const val EXTRA_RESULT_PATH = "result_path"
    }

    private external fun collectVulkanData(surface: Surface?, driverMode: String, driverIcdPath: String?, driverBundlePath: String?, hookLibDir: String): String

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
        val surface = if (android.os.Build.VERSION.SDK_INT >= 33) intent.getParcelableExtra(EXTRA_SURFACE, Surface::class.java) else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_SURFACE) as? Surface
        }
        val mode = intent.getStringExtra(EXTRA_DRIVER_MODE) ?: "SYSTEM"
        val icd = intent.getStringExtra(EXTRA_DRIVER_ICD)
        val bundle = intent.getStringExtra(EXTRA_DRIVER_BUNDLE)
        val hook = intent.getStringExtra(EXTRA_HOOK_LIB_DIR).orEmpty()
        Thread {
            val result = runCatching { collectVulkanData(surface, mode, icd, bundle, hook) }.getOrElse { e ->
                "{\"error\":\"${escape(e.message ?: "Vulkan probe failed")}\"}"
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

    private fun escape(value: String): String = buildString {
        value.forEach {
            when (it) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(it)
            }
        }
    }
}
