package com.nexus.porsuk.ui.portfolio.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.common.CurrencyFormatter
import com.nexus.porsuk.ui.theme.*

@Composable
fun PortfolioPerformanceChartCard(
    selectedTimeframe: Int,
    onTimeframeSelected: (Int) -> Unit,
    chartData: List<Double>,
    numberFormat: String
) {
    val timeframes = remember { listOf("1G", "1H", "1A", "3A", "6A", "1Y", "Tümü") }
    
    val displayData = remember(chartData) {
        if (chartData.isEmpty()) listOf(0.0, 0.0, 0.0, 0.0, 0.0) else chartData
    }

    var animState by remember { mutableStateOf(false) }
    LaunchedEffect(selectedTimeframe) {
        animState = false
        animState = true
    }

    val chartAnimProgress by animateFloatAsState(
        targetValue = if (animState) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "chart_line_draw_anim"
    )

    var touchOffset by remember { mutableStateOf<Offset?>(null) }
    var hoveredPrice by remember { mutableStateOf<String?>(null) }

    val primaryColor = MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black.copy(0.03f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📈", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Portföy Performansı",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (hoveredPrice != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = primaryColor,
                        contentColor = Color.White
                    ) {
                        Text(
                            text = hoveredPrice,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Timeframe Pills Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                timeframes.forEachIndexed { idx, label ->
                    val isSelected = selectedTimeframe == idx
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background,
                        border = BorderStroke(1.dp, if (isSelected) primaryColor else MaterialTheme.colorScheme.outline),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onTimeframeSelected(idx) }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            Text(
                                label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 10.sp
                                ),
                                color = if (isSelected) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Smooth Curve Line Chart with Touch Interactive Hover Point
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(displayData) {
                            detectTapGestures(
                                onPress = { offset ->
                                    touchOffset = offset
                                    val idx = (offset.x / size.width * (displayData.size - 1)).toInt().coerceIn(0, displayData.size - 1)
                                    val valAtPoint = displayData[idx]
                                    hoveredPrice = CurrencyFormatter.formatTRY(valAtPoint, numberFormat)
                                }
                            )
                        }
                ) {
                    val width = size.width
                    val height = size.height

                    val minVal = displayData.minOrNull() ?: 0.0
                    val maxVal = displayData.maxOrNull() ?: 1.0
                    val range = (maxVal - minVal).coerceAtLeast(1.0)

                    val points = displayData.mapIndexed { idx, value ->
                        val x = if (displayData.size > 1) idx.toFloat() / (displayData.size - 1) * width else width / 2
                        val y = height - ((value - minVal) / range * height).toFloat().coerceIn(0f, height.toFloat())
                        Offset(x, y)
                    }

                    if (points.isEmpty()) return@Canvas

                    val currentWidth = width * chartAnimProgress

                    val path = Path()
                    path.moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        val prev = points[i - 1]
                        val curr = points[i]
                        if (curr.x <= currentWidth) {
                            path.cubicTo(
                                (prev.x + curr.x) / 2, prev.y,
                                (prev.x + curr.x) / 2, curr.y,
                                curr.x, curr.y
                            )
                        }
                    }

                    val fillPath = Path()
                    fillPath.addPath(path)
                    fillPath.lineTo(currentWidth, height)
                    fillPath.lineTo(0f, height)
                    fillPath.close()

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(primaryColor.copy(alpha = 0.35f), Color.Transparent)
                        )
                    )

                    drawPath(
                        path = path,
                        color = primaryColor,
                        style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                    )

                    touchOffset?.let { offset ->
                        drawCircle(
                            color = primaryColor,
                            radius = 6.dp.toPx(),
                            center = Offset(offset.x.coerceIn(0f, width), offset.y.coerceIn(0f, height))
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 3.dp.toPx(),
                            center = Offset(offset.x.coerceIn(0f, width), offset.y.coerceIn(0f, height))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Time Labels Placeholder
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Açılış", "Kapanış").forEach { label ->
                    Text(
                        label,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp, fontFamily = IBMPlexMono),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(14.dp))

            // Min, Max, Avg Statistics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ChartSummaryMetric("Minimum", CurrencyFormatter.formatTRY(displayData.minOrNull() ?: 0.0, numberFormat), NegatifRed)
                ChartSummaryMetric("Maksimum", CurrencyFormatter.formatTRY(displayData.maxOrNull() ?: 0.0, numberFormat), PozitifGreen)
                ChartSummaryMetric("Ortalama", CurrencyFormatter.formatTRY(displayData.average(), numberFormat), primaryColor)
            }
        }
    }
}

@Composable
private fun ChartSummaryMetric(title: String, value: String, color: Color) {
    Column {
        Text(
            title,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = Manrope
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            value,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, fontFamily = IBMPlexMono),
            color = color
        )
    }
}
