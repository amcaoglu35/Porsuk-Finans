package com.nexus.porsuk.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.data.local.entity.PortfolioHistoryEntry
import com.nexus.porsuk.data.local.entity.StockHistoryEntry
import com.nexus.porsuk.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun PerformanceComparisonChart(
    portfolioHistory: List<PortfolioHistoryEntry>,
    benchmarkHistory: List<StockHistoryEntry> = emptyList(),
    modifier: Modifier = Modifier
) {
    var selectedTimeframe by remember { mutableStateOf("1H") }
    var activeIndex by remember { mutableStateOf<Int?>(null) }

    val cutoffTime = remember(selectedTimeframe) {
        val now = System.currentTimeMillis()
        when (selectedTimeframe) {
            "1H" -> now - 7L * 24 * 60 * 60 * 1000
            "1A" -> now - 30L * 24 * 60 * 60 * 1000
            else -> 0L
        }
    }

    val filteredPortfolio = remember(portfolioHistory, cutoffTime) {
        val filtered = portfolioHistory.filter { it.timestamp >= cutoffTime }
        if (filtered.size < 2) portfolioHistory.takeLast(7) else filtered
    }

    val filteredBenchmark = remember(benchmarkHistory, cutoffTime) {
        val filtered = benchmarkHistory.filter { it.timestamp >= cutoffTime }
        if (filtered.size < 2) benchmarkHistory.takeLast(7) else filtered
    }

    val myPortfolioData = remember(filteredPortfolio) {
        if (filteredPortfolio.isEmpty()) {
            listOf(0f, 1.2f, 0.8f, 2.5f, 3.1f, 2.7f, 4.8f)
        } else {
            val firstVal = filteredPortfolio.firstOrNull()?.totalValue ?: 1.0
            filteredPortfolio.map { if (firstVal > 0.0) ((it.totalValue - firstVal) / firstVal * 100.0).toFloat() else 0f }
        }
    }

    val bist100Data = remember(filteredBenchmark, myPortfolioData.size) {
        if (filteredBenchmark.isNotEmpty()) {
            val firstVal = filteredBenchmark.firstOrNull()?.price ?: 1.0
            filteredBenchmark.map { if (firstVal > 0.0) ((it.price - firstVal) / firstVal * 100.0).toFloat() else 0f }
        } else if (myPortfolioData.size > 1) {
            val baseBist = listOf(0f, -0.5f, 0.2f, 1.1f, 0.9f, 1.5f, 2.1f, 1.8f, 2.6f, 3.2f)
            List(myPortfolioData.size) { idx ->
                baseBist.getOrNull(idx) ?: (baseBist.last() + (idx - baseBist.size + 1) * 0.3f)
            }
        } else {
            listOf(0f)
        }
    }

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale("tr", "TR")) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Surface)
            .border(1.dp, BorderLine, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        // Üst Başlık ve Zaman Aralığı Seçicisi
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Performans Karşılaştırma",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                fontFamily = Manrope
            )
            
            // Zaman Aralığı Seçici Row
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceAlt)
                    .padding(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                listOf("1H" to "1H", "1A" to "1A", "Tümü" to "Tümü").forEach { (key, label) ->
                    val isSelected = selectedTimeframe == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Aqua else Color.Transparent)
                            .clickable { 
                                selectedTimeframe = key 
                                activeIndex = null
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else TextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            fontFamily = Manrope
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Aktif Noktaya Göre Dinamik Değer Değişimi
        val showIndex = activeIndex ?: (myPortfolioData.size - 1)
        val currentPortfolioVal = myPortfolioData.getOrNull(showIndex) ?: 0f
        val currentBistVal = bist100Data.getOrNull(showIndex) ?: 0f
        
        val activeDateText = remember(activeIndex, filteredPortfolio, showIndex) {
            if (activeIndex != null && filteredPortfolio.isNotEmpty()) {
                val ts = filteredPortfolio.getOrNull(showIndex)?.timestamp ?: 0L
                dateFormatter.format(Date(ts))
            } else {
                "Son Durum"
            }
        }

        // Legend + tarih ayrı satırlara ayrıldı (iç içe girme önleme)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val mySign = if (currentPortfolioVal >= 0) "+" else ""
                val bistSign = if (currentBistVal >= 0) "+" else ""
                val myPct = String.format(Locale.US, "%.2f", currentPortfolioVal)
                val bistPct = String.format(Locale.US, "%.2f", currentBistVal)

                LegendItem(
                    color = Aqua,
                    label = "Portföyüm",
                    value = "$mySign$myPct%",
                    valueColor = if (currentPortfolioVal >= 0) Aqua else NegativeRed,
                    modifier = Modifier.weight(1f)
                )
                LegendItem(
                    color = Color.Gray,
                    label = "BIST 100",
                    value = "$bistSign$bistPct%",
                    valueColor = if (currentBistVal >= 0) PositiveGreen else NegativeRed,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = activeDateText,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = if (activeIndex != null) Aqua else TextMuted,
                    fontFamily = Manrope,
                    fontSize = 10.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Grafik Çizimi ve Dokunmatik Dinamik Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(myPortfolioData.size, filteredPortfolio, filteredBenchmark) {
                        detectTapGestures(
                            onPress = { offset ->
                                if (filteredPortfolio.isNotEmpty()) {
                                    val minTime = minOf(
                                        filteredPortfolio.minOfOrNull { it.timestamp } ?: 0L,
                                        filteredBenchmark.minOfOrNull { it.timestamp } ?: 0L
                                    )
                                    val maxTime = maxOf(
                                        filteredPortfolio.maxOfOrNull { it.timestamp } ?: 0L,
                                        filteredBenchmark.maxOfOrNull { it.timestamp } ?: 0L
                                    )
                                    val timeRange = (maxTime - minTime).coerceAtLeast(1L)
                                    val chartWidth = size.width - 20f
                                    
                                    var closestIndex = 0
                                    var minDiff = Float.MAX_VALUE
                                    for (i in filteredPortfolio.indices) {
                                        val pointX = 10f + chartWidth * (filteredPortfolio[i].timestamp - minTime).toFloat() / timeRange
                                        val diff = kotlin.math.abs(pointX - offset.x)
                                        if (diff < minDiff) {
                                            minDiff = diff
                                            closestIndex = i
                                        }
                                    }
                                    activeIndex = closestIndex
                                }
                                try {
                                    awaitRelease()
                                } finally {
                                    activeIndex = null
                                }
                            }
                        )
                    }
                    .pointerInput(myPortfolioData.size, filteredPortfolio, filteredBenchmark) {
                        detectHorizontalDragGestures(
                            onDragStart = { offset ->
                                if (filteredPortfolio.isNotEmpty()) {
                                    val minTime = minOf(
                                        filteredPortfolio.minOfOrNull { it.timestamp } ?: 0L,
                                        filteredBenchmark.minOfOrNull { it.timestamp } ?: 0L
                                    )
                                    val maxTime = maxOf(
                                        filteredPortfolio.maxOfOrNull { it.timestamp } ?: 0L,
                                        filteredBenchmark.maxOfOrNull { it.timestamp } ?: 0L
                                    )
                                    val timeRange = (maxTime - minTime).coerceAtLeast(1L)
                                    val chartWidth = size.width - 20f
                                    
                                    var closestIndex = 0
                                    var minDiff = Float.MAX_VALUE
                                    for (i in filteredPortfolio.indices) {
                                        val pointX = 10f + chartWidth * (filteredPortfolio[i].timestamp - minTime).toFloat() / timeRange
                                        val diff = kotlin.math.abs(pointX - offset.x)
                                        if (diff < minDiff) {
                                            minDiff = diff
                                            closestIndex = i
                                        }
                                    }
                                    activeIndex = closestIndex
                                }
                            },
                            onDragEnd = { activeIndex = null },
                            onDragCancel = { activeIndex = null },
                            onHorizontalDrag = { change, _ ->
                                if (filteredPortfolio.isNotEmpty()) {
                                    val minTime = minOf(
                                        filteredPortfolio.minOfOrNull { it.timestamp } ?: 0L,
                                        filteredBenchmark.minOfOrNull { it.timestamp } ?: 0L
                                    )
                                    val maxTime = maxOf(
                                        filteredPortfolio.maxOfOrNull { it.timestamp } ?: 0L,
                                        filteredBenchmark.maxOfOrNull { it.timestamp } ?: 0L
                                    )
                                    val timeRange = (maxTime - minTime).coerceAtLeast(1L)
                                    val chartWidth = size.width - 20f
                                    
                                    var closestIndex = 0
                                    var minDiff = Float.MAX_VALUE
                                    for (i in filteredPortfolio.indices) {
                                        val pointX = 10f + chartWidth * (filteredPortfolio[i].timestamp - minTime).toFloat() / timeRange
                                        val diff = kotlin.math.abs(pointX - change.position.x)
                                        if (diff < minDiff) {
                                            minDiff = diff
                                            closestIndex = i
                                        }
                                    }
                                    activeIndex = closestIndex
                                }
                            }
                        )
                    }
            ) {
                val width = size.width
                val height = size.height
                val paddingLeft = 10f
                val paddingRight = 10f
                val paddingTop = 15f
                val paddingBottom = 15f

                val chartWidth = width - paddingLeft - paddingRight
                val chartHeight = height - paddingTop - paddingBottom

                // Grid yatay çizgiler
                val gridLines = 3
                for (i in 0..gridLines) {
                    val y = paddingTop + chartHeight * (i.toFloat() / gridLines)
                    drawLine(
                        color = BorderLine.copy(alpha = 0.4f),
                        start = Offset(paddingLeft, y),
                        end = Offset(width - paddingRight, y),
                        strokeWidth = 1f
                    )
                }

                val allData = myPortfolioData + bist100Data
                val actualMin = allData.minOrNull() ?: -1f
                val actualMax = allData.maxOrNull() ?: 6f
                val minVal = actualMin - 0.5f
                val maxVal = actualMax + 0.5f
                val valRange = if (maxVal - minVal == 0f) 1f else maxVal - minVal

                // Min and Max Timestamps for proper alignment
                val minTime = minOf(
                    filteredPortfolio.minOfOrNull { it.timestamp } ?: 0L,
                    filteredBenchmark.minOfOrNull { it.timestamp } ?: 0L
                )
                val maxTime = maxOf(
                    filteredPortfolio.maxOfOrNull { it.timestamp } ?: 0L,
                    filteredBenchmark.maxOfOrNull { it.timestamp } ?: 0L
                )
                val timeRange = (maxTime - minTime).coerceAtLeast(1L)

                // Bezier Eğrisi Yolları
                val myPath = Path()
                val bistPath = Path()

                // 1. Portföy Eğrisi (Cubic Bezier)
                if (filteredPortfolio.isNotEmpty() && myPortfolioData.size == filteredPortfolio.size) {
                    val firstX = paddingLeft + chartWidth * (filteredPortfolio[0].timestamp - minTime).toFloat() / timeRange
                    val startY = paddingTop + chartHeight * (1f - (myPortfolioData[0] - minVal) / valRange)
                    myPath.moveTo(firstX, startY)
                    for (i in 1 until myPortfolioData.size) {
                        val currentX = paddingLeft + chartWidth * (filteredPortfolio[i].timestamp - minTime).toFloat() / timeRange
                        val currentY = paddingTop + chartHeight * (1f - (myPortfolioData[i] - minVal) / valRange)
                        
                        val prevX = paddingLeft + chartWidth * (filteredPortfolio[i - 1].timestamp - minTime).toFloat() / timeRange
                        val prevY = paddingTop + chartHeight * (1f - (myPortfolioData[i - 1] - minVal) / valRange)
                        
                        val stepX = currentX - prevX
                        myPath.cubicTo(
                            prevX + stepX / 2f, prevY,
                            prevX + stepX / 2f, currentY,
                            currentX, currentY
                        )
                    }
                }

                // 2. BIST 100 Eğrisi (Cubic Bezier)
                if (filteredBenchmark.isNotEmpty() && bist100Data.size == filteredBenchmark.size) {
                    val firstX = paddingLeft + chartWidth * (filteredBenchmark[0].timestamp - minTime).toFloat() / timeRange
                    val startY = paddingTop + chartHeight * (1f - (bist100Data[0] - minVal) / valRange)
                    bistPath.moveTo(firstX, startY)
                    for (i in 1 until bist100Data.size) {
                        val currentX = paddingLeft + chartWidth * (filteredBenchmark[i].timestamp - minTime).toFloat() / timeRange
                        val currentY = paddingTop + chartHeight * (1f - (bist100Data[i] - minVal) / valRange)
                        
                        val prevX = paddingLeft + chartWidth * (filteredBenchmark[i - 1].timestamp - minTime).toFloat() / timeRange
                        val prevY = paddingTop + chartHeight * (1f - (bist100Data[i - 1] - minVal) / valRange)
                        
                        val stepX = currentX - prevX
                        bistPath.cubicTo(
                            prevX + stepX / 2f, prevY,
                            prevX + stepX / 2f, currentY,
                            currentX, currentY
                        )
                    }
                }

                // BIST 100 Çizimi
                drawPath(
                    path = bistPath,
                    color = Color.Gray.copy(alpha = 0.4f),
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )

                // Portföy Çizgisinin Altını Doldurma Gradient
                if (filteredPortfolio.isNotEmpty() && myPortfolioData.size == filteredPortfolio.size) {
                    val fillPath = Path().apply {
                        addPath(myPath)
                        val lastX = paddingLeft + chartWidth * (filteredPortfolio.last().timestamp - minTime).toFloat() / timeRange
                        val firstX = paddingLeft + chartWidth * (filteredPortfolio.first().timestamp - minTime).toFloat() / timeRange
                        lineTo(lastX, height - paddingBottom)
                        lineTo(firstX, height - paddingBottom)
                        close()
                    }
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Aqua.copy(alpha = 0.2f),
                                Aqua.copy(alpha = 0.02f),
                                Color.Transparent
                            )
                        )
                    )
                }

                // Portföy Çizimi
                drawPath(
                    path = myPath,
                    color = Aqua,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // İnteraktif Kılavuz Çizgisi (Crosshair) ve Nokta Vurgulama
                val currentIdx = activeIndex
                if (currentIdx != null) {
                    val activeX = paddingLeft + chartWidth * (filteredPortfolio[currentIdx].timestamp - minTime).toFloat() / timeRange
                    
                    // Dikey kılavuz çizgisi
                    drawLine(
                        color = Aqua.copy(alpha = 0.5f),
                        start = Offset(activeX, paddingTop),
                        end = Offset(activeX, height - paddingBottom),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )

                    // Portföy Aktif Noktası
                    val myNorm = (myPortfolioData[currentIdx] - minVal) / valRange
                    val myY = paddingTop + chartHeight * (1f - myNorm)
                    drawCircle(color = Aqua.copy(alpha = 0.2f), radius = 10.dp.toPx(), center = Offset(activeX, myY))
                    drawCircle(color = Aqua, radius = 5.dp.toPx(), center = Offset(activeX, myY))
                    drawCircle(color = Color.White, radius = 2.dp.toPx(), center = Offset(activeX, myY))

                    // BIST 100 Aktif Noktası (Portföy tarihindeki en yakın BIST 100 noktası)
                    if (filteredBenchmark.isNotEmpty() && bist100Data.size == filteredBenchmark.size) {
                        val targetTime = filteredPortfolio[currentIdx].timestamp
                        var closestBistIdx = 0
                        var minBistDiff = Long.MAX_VALUE
                        for (i in filteredBenchmark.indices) {
                            val diff = kotlin.math.abs(filteredBenchmark[i].timestamp - targetTime)
                            if (diff < minBistDiff) {
                                minBistDiff = diff
                                closestBistIdx = i
                            }
                        }
                        val bistX = paddingLeft + chartWidth * (filteredBenchmark[closestBistIdx].timestamp - minTime).toFloat() / timeRange
                        val bistNorm = (bist100Data[closestBistIdx] - minVal) / valRange
                        val bistY = paddingTop + chartHeight * (1f - bistNorm)
                        drawCircle(color = Color.Gray.copy(alpha = 0.2f), radius = 8.dp.toPx(), center = Offset(bistX, bistY))
                        drawCircle(color = Color.Gray, radius = 4.dp.toPx(), center = Offset(bistX, bistY))
                    }
                } else {
                    // Touch yoksa, en son noktaları göster
                    if (filteredPortfolio.isNotEmpty() && myPortfolioData.size == filteredPortfolio.size) {
                        val lastIdx = myPortfolioData.size - 1
                        val x = paddingLeft + chartWidth * (filteredPortfolio[lastIdx].timestamp - minTime).toFloat() / timeRange
                        val myNorm = (myPortfolioData[lastIdx] - minVal) / valRange
                        val myY = paddingTop + chartHeight * (1f - myNorm)
                        drawCircle(color = Aqua, radius = 5.dp.toPx(), center = Offset(x, myY))
                    }
                    if (filteredBenchmark.isNotEmpty() && bist100Data.size == filteredBenchmark.size) {
                        val lastIdx = bist100Data.size - 1
                        val x = paddingLeft + chartWidth * (filteredBenchmark[lastIdx].timestamp - minTime).toFloat() / timeRange
                        val bistNorm = (bist100Data[lastIdx] - minVal) / valRange
                        val bistY = paddingTop + chartHeight * (1f - bistNorm)
                        drawCircle(color = Color.Gray, radius = 4.dp.toPx(), center = Offset(x, bistY))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        
        // Kullanıcıya İpucu
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (activeIndex != null) "Değerleri görmek için kaydırın" else "Detaylar için grafiğe basılı tutun ve sürükleyin",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted.copy(alpha = 0.7f),
                fontFamily = Manrope,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    value: String = "",
    valueColor: Color = color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontFamily = Manrope,
            color = TextMuted,
            fontWeight = FontWeight.Medium
        )
        if (value.isNotEmpty()) {
            Text(
                text = value,
                fontSize = 11.sp,
                fontFamily = Manrope,
                color = valueColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
