package com.efishell.vulkanscope

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class RestartActivity : Activity() {
    companion object {
        const val EXTRA_PARENT_PID = "parent_pid"
    }

    private val handler = Handler(Looper.getMainLooper())
    private var parentPid: Int = -1
    private var attempts = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setDimAmount(0f)
        window.decorView.alpha = 0f
        parentPid = intent.getIntExtra(EXTRA_PARENT_PID, -1)
        waitForPreviousProcess()
    }

    private fun waitForPreviousProcess() {
        handler.postDelayed({
            attempts += 1
            if (parentPid <= 0 || !isProcessAlive(parentPid)) {
                launchMain()
                return@postDelayed
            }
            if (attempts >= 50) {
                runCatching { android.os.Process.killProcess(parentPid) }
                handler.postDelayed({ launchMain() }, 300L)
                return@postDelayed
            }
            waitForPreviousProcess()
        }, 100L)
    }

    private fun isProcessAlive(pid: Int): Boolean = runCatching {
        java.io.File("/proc/$pid").exists()
    }.getOrDefault(false)

    private fun launchMain() {
        val launch = packageManager.getLaunchIntentForPackage(packageName)
            ?: Intent(this, MainActivity::class.java)
        launch.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_NO_ANIMATION
        )
        startActivity(launch)
        finishAndRemoveTask()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
