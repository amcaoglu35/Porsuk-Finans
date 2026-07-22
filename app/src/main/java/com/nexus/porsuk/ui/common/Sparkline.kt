package com.nexus.porsuk.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun Sparkline(
    values: List<Float>,
    color: Color,
    modifier: Modifier = Modifier,
    filled: Boolean = false
) {
    Canvas(modifier = modifier) {
        if (values.size < 2) return@Canvas
        val minV = values.min()
        val maxV = values.max()
        val range = (maxV - minV).takeIf { it != 0f } ?: 1f
        val stepX = size.width / (values.size - 1)
        
        // Kavisli (Cubic Bezier) Yol Çizimi
        val path = Path().apply {
            val startY = size.height * (1f - (values[0] - minV) / range)
            moveTo(0f, startY)
            
            for (i in 1 until values.size) {
                val currentX = i * stepX
                val currentY = size.height * (1f - (values[i] - minV) / range)
                
                val prevX = (i - 1) * stepX
                val prevY = size.height * (1f - (values[i - 1] - minV) / range)
                
                // Bezier eğrisi hesaplaması
                cubicTo(
                    prevX + stepX / 2f, prevY,
                    prevX + stepX / 2f, currentY,
                    currentX, currentY
                )
            }
        }
        
        if (filled) {
            val fillPath = Path().apply {
                addPath(path)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(
                fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        color.copy(alpha = 0.15f),
                        Color.Transparent
                    )
                )
            )
        }
        
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 1.75.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}
