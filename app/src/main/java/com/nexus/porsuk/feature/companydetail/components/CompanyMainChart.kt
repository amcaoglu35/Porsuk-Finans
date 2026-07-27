package com.nexus.porsuk.feature.companydetail.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CompanyMainChart(
    modifier: Modifier = Modifier
) {
    val mainGreen = Color(0xFF14B88A)
    val dataPoints = listOf(0.2f, 0.25f, 0.22f, 0.4f, 0.35f, 0.6f, 0.55f, 0.8f, 0.75f, 0.95f, 0.85f, 1.0f)

    Column(modifier = modifier.padding(vertical = 16.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp)
        ) {
            val width = size.width
            val height = size.height
            val spacing = width / (dataPoints.size - 1)
            
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
                    // Use cubicTo for "fluid" look
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
            
            // Draw gradient area
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(mainGreen.copy(alpha = 0.3f), Color.Transparent)
                )
            )
            
            // Draw line
            drawPath(
                path = path,
                color = mainGreen,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Volume & RSI labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Hacim: 45.2M",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "RSI (14): 62.4",
                style = MaterialTheme.typography.labelSmall,
                color = mainGreen,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
