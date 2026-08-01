package com.nexus.porsuk.feature.companydetail.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.feature.companydetail.CandleStickData
import com.nexus.porsuk.feature.companydetail.ChartTimeFrame
import com.nexus.porsuk.feature.companydetail.ChartType

@Composable
fun CompanyMainChart(
    data: List<Double>,
    candles: List<CandleStickData> = emptyList(),
    chartType: ChartType = ChartType.LINE,
    selectedTimeFrame: ChartTimeFrame = ChartTimeFrame.ONE_MONTH,
    onChartTypeChange: (ChartType) -> Unit = {},
    onTimeFrameChange: (ChartTimeFrame) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val mainGreen = Color(0xFF14B88A)
    val mainRed = Color(0xFFE53935)
    val cardBg = Color(0xFFFFFFFF)

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .padding(16.dp)
    ) {
        // Top Controls: Chart Type Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Fiyat Grafiği",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (chartType == ChartType.LINE) mainGreen else Color.Transparent)
                        .clickable { onChartTypeChange(ChartType.LINE) }
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Çizgi",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (chartType == ChartType.LINE) Color.White else Color(0xFF64748B)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (chartType == ChartType.CANDLESTICK) mainGreen else Color.Transparent)
                        .clickable { onChartTypeChange(ChartType.CANDLESTICK) }
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Mum 🕯️",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (chartType == ChartType.CANDLESTICK) Color.White else Color(0xFF64748B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Chart View
        if (chartType == ChartType.LINE) {
            LineChartView(data = data, mainGreen = mainGreen)
        } else {
            CandlestickChartView(candles = candles, mainGreen = mainGreen, mainRed = mainRed)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // TimeFrame Chips Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChartTimeFrame.values().forEach { timeFrame ->
                val selected = timeFrame == selectedTimeFrame
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selected) mainGreen.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { onTimeFrameChange(timeFrame) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = timeFrame.label,
                        fontSize = 12.sp,
                        fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                        color = if (selected) mainGreen else Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

@Composable
private fun LineChartView(data: List<Double>, mainGreen: Color) {
    val dataPoints = remember(data) {
        if (data.isEmpty()) return@remember emptyList<Float>()
        val min = data.minOrNull() ?: 0.0
        val max = data.maxOrNull() ?: 1.0
        val range = (max - min).coerceAtLeast(0.01)
        data.map { ((it - min) / range).toFloat() }
    }

    if (dataPoints.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = mainGreen)
        }
    } else {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
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
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
}

@Composable
private fun CandlestickChartView(
    candles: List<CandleStickData>,
    mainGreen: Color,
    mainRed: Color
) {
    if (candles.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = mainGreen)
        }
        return
    }

    val minLow = remember(candles) { candles.minOf { it.low } }
    val maxHigh = remember(candles) { candles.maxOf { it.high } }
    val maxVolume = remember(candles) { candles.maxOf { it.volume }.coerceAtLeast(1.0) }
    val priceRange = (maxHigh - minLow).coerceAtLeast(0.01)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        val width = size.width
        val height = size.height
        val candleWidth = width / candles.size
        val bodyWidth = (candleWidth * 0.6f).coerceAtLeast(4f)

        candles.forEachIndexed { index, candle ->
            val x = index * candleWidth + candleWidth / 2
            val isBullish = candle.close >= candle.open
            val color = if (isBullish) mainGreen else mainRed

            // Draw Volume Bar at the bottom 25% of height
            val volumeHeight = ((candle.volume / maxVolume) * (height * 0.25f)).toFloat()
            drawRect(
                color = color.copy(alpha = 0.2f),
                topLeft = Offset(x - bodyWidth / 2, height - volumeHeight),
                size = Size(bodyWidth, volumeHeight)
            )

            // Convert prices to Y coordinates
            val chartAreaHeight = height * 0.7f
            val highY = chartAreaHeight - (((candle.high - minLow) / priceRange) * chartAreaHeight).toFloat()
            val lowY = chartAreaHeight - (((candle.low - minLow) / priceRange) * chartAreaHeight).toFloat()
            val openY = chartAreaHeight - (((candle.open - minLow) / priceRange) * chartAreaHeight).toFloat()
            val closeY = chartAreaHeight - (((candle.close - minLow) / priceRange) * chartAreaHeight).toFloat()

            // Draw High-Low Line (Wick)
            drawLine(
                color = color,
                start = Offset(x, highY),
                end = Offset(x, lowY),
                strokeWidth = 2.dp.toPx()
            )

            // Draw Open-Close Candle Body
            val bodyTop = minOf(openY, closeY)
            val bodyBottom = maxOf(openY, closeY)
            val candleBodyHeight = (bodyBottom - bodyTop).coerceAtLeast(3f)

            drawRect(
                color = color,
                topLeft = Offset(x - bodyWidth / 2, bodyTop),
                size = Size(bodyWidth, candleBodyHeight)
            )
        }
    }
}

