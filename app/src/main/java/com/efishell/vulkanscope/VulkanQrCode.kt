package com.efishell.vulkanscope

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

@Composable
internal fun VulkanQrCode(text: String, modifier: Modifier = Modifier) {
    val matrix = remember(text) {
        if (text.isBlank()) null else runCatching {
            QRCodeWriter().encode(
                text,
                BarcodeFormat.QR_CODE,
                1,
                1,
                mapOf(EncodeHintType.MARGIN to 2, EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M)
            )
        }.getOrNull()
    }
    Canvas(modifier.background(Color.White)) {
        val value = matrix ?: return@Canvas
        val cellWidth = size.width / value.width
        val cellHeight = size.height / value.height
        for (y in 0 until value.height) {
            for (x in 0 until value.width) {
                if (value[x, y]) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(x * cellWidth, y * cellHeight),
                        size = Size(cellWidth, cellHeight)
                    )
                }
            }
        }
    }
}
