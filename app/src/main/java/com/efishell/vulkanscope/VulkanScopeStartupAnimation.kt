@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.efishell.vulkanscope

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay



@Composable
internal fun VulkanScopeOpeningAnimation(startAnimation: Boolean, onFinished: () -> Unit) {
    var phase by remember { mutableIntStateOf(0) }
    val contentAlpha by animateFloatAsState(
        targetValue = when {
            phase >= 4 -> 0f
            phase >= 1 -> 1f
            else -> 0f
        },
        animationSpec = tween(durationMillis = if (phase >= 4) 260 else 360, easing = FastOutSlowInEasing),
        label = "openingContentAlpha"
    )
    val logoScale by animateFloatAsState(
        targetValue = when {
            phase >= 4 -> 1.025f
            phase >= 2 -> 1f
            phase >= 1 -> 1.012f
            else -> 0.94f
        },
        animationSpec = tween(durationMillis = if (phase >= 4) 280 else 520, easing = FastOutSlowInEasing),
        label = "openingLogoScale"
    )
    val haloAlpha by animateFloatAsState(
        targetValue = when {
            phase >= 4 -> 0f
            phase >= 3 -> 0.26f
            phase >= 2 -> 0.52f
            phase >= 1 -> 0.34f
            else -> 0f
        },
        animationSpec = tween(durationMillis = 460, easing = FastOutSlowInEasing),
        label = "openingHaloAlpha"
    )
    val haloScale by animateFloatAsState(
        targetValue = when {
            phase >= 4 -> 1.08f
            phase >= 2 -> 1f
            phase >= 1 -> 0.92f
            else -> 0.84f
        },
        animationSpec = tween(durationMillis = 620, easing = FastOutSlowInEasing),
        label = "openingHaloScale"
    )
    val lineScale by animateFloatAsState(
        targetValue = when {
            phase >= 2 -> 1f
            phase >= 1 -> 0.34f
            else -> 0f
        },
        animationSpec = tween(durationMillis = 520, easing = FastOutSlowInEasing),
        label = "openingLineScale"
    )
    val lineAlpha by animateFloatAsState(
        targetValue = when {
            phase >= 4 -> 0f
            phase >= 1 -> 1f
            else -> 0f
        },
        animationSpec = tween(durationMillis = if (phase >= 4) 220 else 300, easing = FastOutSlowInEasing),
        label = "openingLineAlpha"
    )
    val accentPulse by animateFloatAsState(
        targetValue = when {
            phase >= 4 -> 0f
            phase >= 3 -> 0.28f
            phase >= 2 -> 0.82f
            phase >= 1 -> 0.46f
            else -> 0f
        },
        animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing),
        label = "openingAccentPulse"
    )
    LaunchedEffect(startAnimation) {
        if (!startAnimation) return@LaunchedEffect
        phase = 1
        delay(520L)
        phase = 2
        delay(500L)
        phase = 3
        delay(300L)
        phase = 4
        delay(280L)
        onFinished()
    }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(ComposeColor.Black, ComposeColor(0xFF070707), ComposeColor(0xFF0A0607), ComposeColor.Black)
                )
            )
            .pointerInput(Unit) { detectTapGestures { } },
        contentAlignment = Alignment.Center
    ) {
        val landscape = maxWidth > maxHeight
        val logoMaxWidth = if (landscape) 560.dp else 360.dp
        val logoWidthFraction = if (landscape) 0.52f else 0.78f
        val haloSize = if (landscape) 620.dp else 470.dp
        val coreGlowSize = if (landscape) 390.dp else 300.dp
        val lineWidth = if (landscape) 180.dp else 142.dp
        Box(
            Modifier
                .size(haloSize)
                .graphicsLayer {
                    alpha = haloAlpha
                    scaleX = haloScale
                    scaleY = haloScale
                }
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ComposeColor(0x665B1519),
                            ComposeColor(0x33230A0C),
                            ComposeColor.Transparent
                        ),
                        radius = if (landscape) 560f else 450f
                    )
                )
        )
        Box(
            Modifier
                .size(coreGlowSize)
                .graphicsLayer {
                    alpha = accentPulse
                    scaleX = 0.9f + accentPulse * 0.12f
                    scaleY = 0.9f + accentPulse * 0.12f
                }
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            VulkanAccent.copy(alpha = 0.28f),
                            ComposeColor(0x242E0A0C),
                            ComposeColor.Transparent
                        )
                    )
                )
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(if (landscape) 18.dp else 16.dp),
            modifier = Modifier.graphicsLayer {
                alpha = contentAlpha
                scaleX = logoScale
                scaleY = logoScale
            }
        ) {
            Image(
                painter = painterResource(R.drawable.vulkanscope_logo_horizontal_aligned),
                contentDescription = "VulkanScope",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .widthIn(max = logoMaxWidth)
                    .fillMaxWidth(logoWidthFraction)
                    .heightIn(max = if (landscape) 112.dp else 96.dp)
            )
            Box(
                Modifier
                    .width(lineWidth + 20.dp)
                    .height(2.dp)
                    .graphicsLayer {
                        alpha = lineAlpha
                        scaleX = lineScale
                    }
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                ComposeColor(0xFF7A171B),
                                VulkanAccentSoft,
                                ComposeColor(0xFF7A171B)
                            )
                        )
                    )
            )
        }
    }
}
