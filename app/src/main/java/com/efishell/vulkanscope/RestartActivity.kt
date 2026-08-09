package com.efishell.vulkanscope

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper

/**
 * Minimal second-process hand-off used for a real process restart.
 * The visible restart confirmation is rendered by MainActivity with Material 3;
 * this helper is deliberately invisible and exists only to relaunch MainActivity
 * after the original process has been terminated.
 */
class RestartActivity : Activity() {
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setDimAmount(0f)
        window.decorView.alpha = 0f
        // Keep this process alive while MainActivity's original process is being
        // terminated. A separate process is intentional: it survives killProcess()
        // in MainActivity and can relaunch the application cleanly.
        handler.postDelayed({
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
        }, 1200L)
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
