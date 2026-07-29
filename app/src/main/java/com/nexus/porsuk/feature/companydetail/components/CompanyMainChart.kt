package com.nexus.porsuk.feature.companydetail.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CompanyMainChart(
    data: List<Double>,
    modifier: Modifier = Modifier
) {
    val mainGreen = Color(0xFF14B88A)
    val dataPoints = remember(data) {
        if (data.isEmpty()) return@remember emptyList<Float>()
        val min = data.minOrNull() ?: 0.0
        val max = data.maxOrNull() ?: 1.0
        val range = (max - min).coerceAtLeast(0.01)
        data.map { ((it - min) / range).toFloat() }
    }

    Column(modifier = modifier.padding(vertical = 16.dp)) {
        if (dataPoints.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = mainGreen)
            }
        } else {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 16.dp)
            ) {
                val width = size.width
                val height = size.height
                val spacing = width / (dataPoints.size - 1).coerceAtLeast(1)
                
                val path = Path()
                val fillPath = Path()
                
                dataPoints.forEachIndexed { index, point ->
                    val x = index * spacing
                    val y = height - (point * height)
                    
                    if (index == 0) {
                        path.moveTo(x, y)
                        fillPath.moveTo(x, height)
                        fillPath.lineTo(x, y)
                    } else {
                        val prevX = (index - 1) * spacing
                        val prevY = height - (dataPoints[index - 1] * height)
                        path.cubicTo(
                            prevX + spacing / 2, prevY,
                            x - spacing / 2, y,
                            x, y
                        )
                        fillPath.cubicTo(
                            prevX + spacing / 2, prevY,
                            x - spacing / 2, y,
                            x, y
                        )
                    }
                }
                
                fillPath.lineTo(width, height)
                fillPath.close()
                
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(mainGreen.copy(alpha = 0.3f), Color.Transparent)
                    )
                )
                
                drawPath(
                    path = path,
                    color = mainGreen,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
        }
    }
}
