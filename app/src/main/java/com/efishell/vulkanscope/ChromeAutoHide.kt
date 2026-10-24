package com.efishell.vulkanscope

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

internal const val CHROME_INACTIVITY_MILLIS = 3000L
internal const val CHROME_MOTION_MILLIS = 320

internal fun shouldShowAppChrome(idle: Boolean, autoHideEnabled: Boolean, hideThisBar: Boolean): Boolean =
    !idle || !autoHideEnabled || !hideThisBar

internal class ChromeAutoHideSettings(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)

    var enabled by mutableStateOf(preferences.getBoolean("chrome_auto_hide_enabled", true))
        private set
    var hideHeader by mutableStateOf(preferences.getBoolean("chrome_auto_hide_header", true))
        private set
    var hideNavigation by mutableStateOf(preferences.getBoolean("chrome_auto_hide_navigation", true))
        private set

    fun updateEnabled(value: Boolean) {
        enabled = value
        preferences.edit().putBoolean("chrome_auto_hide_enabled", value).apply()
    }

    fun updateHideHeader(value: Boolean) {
        hideHeader = value
        preferences.edit().putBoolean("chrome_auto_hide_header", value).apply()
    }

    fun updateHideNavigation(value: Boolean) {
        hideNavigation = value
        preferences.edit().putBoolean("chrome_auto_hide_navigation", value).apply()
    }
}

internal val LocalChromeAutoHideSettings = staticCompositionLocalOf<ChromeAutoHideSettings?> { null }

@Composable
internal fun AutoHideHeader(visible: Boolean, content: @Composable () -> Unit) {
    Box(Modifier.fillMaxWidth()) {
        AnimatedVisibility(
            visible = visible,
            enter = expandVertically(animationSpec = tween(CHROME_MOTION_MILLIS, easing = FastOutSlowInEasing), expandFrom = Alignment.Top) + fadeIn(tween(230)),
            exit = shrinkVertically(animationSpec = tween(CHROME_MOTION_MILLIS, easing = FastOutSlowInEasing), shrinkTowards = Alignment.Top) + fadeOut(tween(220))
        ) { content() }
    }
}

@Composable
internal fun AutoHideNavigation(visible: Boolean, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInVertically(tween(CHROME_MOTION_MILLIS, easing = FastOutSlowInEasing)) { it } + fadeIn(tween(230)),
        exit = slideOutVertically(tween(CHROME_MOTION_MILLIS, easing = FastOutSlowInEasing)) { it } + fadeOut(tween(220))
    ) { content() }
}
