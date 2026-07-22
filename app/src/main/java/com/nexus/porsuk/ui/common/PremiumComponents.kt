package com.nexus.porsuk.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.data.local.entity.CachedCompanyInfo
import com.nexus.porsuk.data.local.entity.NewsItemEntity
import com.nexus.porsuk.data.remote.MockNewsItem
import com.nexus.porsuk.data.remote.RichCompanyDetails
import com.nexus.porsuk.ui.common.NumberFormatter
import com.nexus.porsuk.ui.theme.*
import java.text.DecimalFormat

// ============================================================================
// 1. ADIM: DETAY EKRANI DÖVİZ KURU FORMATLAMA
// ============================================================================
@Composable
fun FormattedCurrencyEquivalents(
    livePrice: Double,
    market: String,
    numberFormat: String = "TR",
    usdTryRate: Double = 34.25,
    eurTryRate: Double = 37.12,
    modifier: Modifier = Modifier
) {
    val (formattedFirst, formattedSecond) = when (market.uppercase()) {
        "NASDAQ", "NYSE" -> {
            val priceInTry = livePrice * usdTryRate
            val priceInEur = priceInTry / eurTryRate
            CurrencyFormatter.formatTRY(priceInTry, numberFormat) to CurrencyFormatter.formatWithSymbol(priceInEur, "€", numberFormat)
        }
        "FRA", "EURONEXT", "ETR", "EPA", "AMS", "BME" -> {
            // Avrupa borsaları — EUR cinsinden
            val priceInTry = livePrice * eurTryRate
            val priceInUsd = priceInTry / usdTryRate
            CurrencyFormatter.formatTRY(priceInTry, numberFormat) to CurrencyFormatter.formatWithSymbol(priceInUsd, "$", numberFormat)
        }
        "LSE" -> {
            // Londra Borsası — GBP cinsinden, EUR/GBP ≈ 0.86 ile tahmin
            val gbpTryRate = eurTryRate * 1.165
            val priceInTry = livePrice * gbpTryRate
            val priceInEur = priceInTry / eurTryRate
            CurrencyFormatter.formatTRY(priceInTry, numberFormat) to CurrencyFormatter.formatWithSymbol(priceInEur, "€", numberFormat)
        }
        "SWX" -> {
            // İsviçre Borsası — CHF cinsinden, EUR/CHF ≈ 0.94 ile tahmin
            val chfTryRate = eurTryRate * 1.06
            val priceInTry = livePrice * chfTryRate
            val priceInUsd = priceInTry / usdTryRate
            CurrencyFormatter.formatTRY(priceInTry, numberFormat) to CurrencyFormatter.formatWithSymbol(priceInUsd, "$", numberFormat)
        }
        else -> {
            // BIST ve diğerleri — TRY cinsinden
            val priceInUsd = livePrice / usdTryRate
            val priceInEur = livePrice / eurTryRate
            CurrencyFormatter.formatWithSymbol(priceInUsd, "$", numberFormat) to CurrencyFormatter.formatWithSymbol(priceInEur, "€", numberFormat)
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$formattedFirst  •  $formattedSecond",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = JetBrainsMono,
            color = SubText
        )
    }
}

// ============================================================================
// 2. ADIM: ÖZEL CANLI CANVAS GRAFİĞİ (PREMIUM V2)
// ============================================================================
@Composable
fun PremiumLiveCanvasChart(
    pricePoints: List<Float>,
    modifier: Modifier = Modifier,
    isGlassStyle: Boolean = false
) {
    val lineAlpha = 0.8f
    val isPositive = if (pricePoints.size >= 2) pricePoints.last() >= pricePoints.first() else true
    val chartColor = if (isPositive) PrimaryTeal else NegatifRed

    val containerBg = if (isGlassStyle) Color.White.copy(alpha = 0.10f) else CardNew
    val containerBorder = if (isGlassStyle) Color.White.copy(alpha = 0.16f) else LineBorder

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(containerBg)
            .border(1.dp, containerBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (pricePoints.isEmpty()) {
            CircularProgressIndicator(color = PrimaryTeal, strokeWidth = 2.dp)
        } else if (pricePoints.size < 2) {
            Text("Veri bekleniyor...", color = SubText, fontSize = 12.sp)
        } else {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                
                val rawMax = pricePoints.maxOrNull() ?: 1f
                val rawMin = pricePoints.minOrNull() ?: 0f
                val buffer = (rawMax - rawMin) * 0.15f
                val maxVal = rawMax + buffer
                val minVal = rawMin - buffer
                val valueRange = if (maxVal - minVal == 0f) 1f else maxVal - minVal

                val stepX = width / (pricePoints.size - 1)
                
                // 1. Horizontal Grid Lines
                val gridLines = 3
                val lineCol = if (isGlassStyle) Color.White.copy(alpha = 0.12f) else LineBorder.copy(alpha = 0.4f)
                for (i in 0 until gridLines) {
                    val y = (height / (gridLines - 1)) * i
                    drawLine(
                        color = lineCol,
                        start = androidx.compose.ui.geometry.Offset(0f, y),
                        end = androidx.compose.ui.geometry.Offset(width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                val strokePath = Path().apply {
                    val startY = height - ((pricePoints[0] - minVal) / valueRange) * height
                    moveTo(0f, startY)
                    
                    for (i in 1 until pricePoints.size) {
                        val currentX = i * stepX
                        val currentY = height - ((pricePoints[i] - minVal) / valueRange) * height
                        
                        val prevX = (i - 1) * stepX
                        val prevY = height - ((pricePoints[i - 1] - minVal) / valueRange) * height
                        
                        cubicTo(
                            prevX + stepX / 2f, prevY,
                            prevX + stepX / 2f, currentY,
                            currentX, currentY
                        )
                    }
                }

                // Area Gradient
                val fillPath = Path().apply {
                    addPath(strokePath)
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }

                val areaBrush = if (isGlassStyle) {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF7CFFC4).copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            chartColor.copy(alpha = 0.25f),
                            chartColor.copy(alpha = 0.03f),
                            Color.Transparent
                        )
                    )
                }

                drawPath(
                    path = fillPath,
                    brush = areaBrush
                )

                // Stroke Brush & Glow
                val strokeBrush = if (isGlassStyle) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF7CFFC4),
                            Color(0xFF22B8D9),
                            Color(0xFF5CE0F5)
                        )
                    )
                } else {
                    androidx.compose.ui.graphics.SolidColor(chartColor)
                }

                // Glow effect
                if (isGlassStyle) {
                    drawPath(
                        path = strokePath,
                        brush = strokeBrush,
                        alpha = 0.15f,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawPath(
                        path = strokePath,
                        brush = strokeBrush,
                        alpha = 0.30f,
                        style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                    )
                } else {
                    drawPath(
                        path = strokePath,
                        color = chartColor.copy(alpha = 0.15f),
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Main Line
                drawPath(
                    path = strokePath,
                    brush = strokeBrush,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )

                // Indicators
                if (isGlassStyle) {
                    val maxValInPoints = pricePoints.maxOrNull() ?: 0f
                    val maxIndex = pricePoints.indexOf(maxValInPoints)
                    if (maxIndex != -1) {
                        val maxPointX = maxIndex * stepX
                        val maxPointY = height - ((maxValInPoints - minVal) / valueRange) * height
                        drawCircle(
                            color = Color(0xFF7CFFC4),
                            radius = 4.dp.toPx(),
                            center = androidx.compose.ui.geometry.Offset(maxPointX, maxPointY)
                        )
                    }

                    val lastX = width
                    val lastY = height - ((pricePoints.last() - minVal) / valueRange) * height
                    
                    drawCircle(
                        color = Color(0xFF22B8D9).copy(alpha = 0.4f),
                        radius = 10.dp.toPx(),
                        center = androidx.compose.ui.geometry.Offset(lastX, lastY)
                    )
                    drawCircle(
                        color = Color(0xFF22B8D9),
                        radius = 5.dp.toPx(),
                        center = androidx.compose.ui.geometry.Offset(lastX, lastY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 2.dp.toPx(),
                        center = androidx.compose.ui.geometry.Offset(lastX, lastY)
                    )
                } else {
                    val lastX = width
                    val lastY = height - ((pricePoints.last() - minVal) / valueRange) * height
                    
                    drawCircle(
                        color = chartColor.copy(alpha = 0.2f),
                        radius = 12.dp.toPx(),
                        center = androidx.compose.ui.geometry.Offset(lastX, lastY)
                    )
                    drawCircle(
                        color = chartColor,
                        radius = 5.dp.toPx(),
                        center = androidx.compose.ui.geometry.Offset(lastX, lastY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 2.dp.toPx(),
                        center = androidx.compose.ui.geometry.Offset(lastX, lastY)
                    )
                }

                // Max/Min Text Labels
                val labelColor = if (isGlassStyle) android.graphics.Color.parseColor("#99FFFFFF") else android.graphics.Color.parseColor("#64748B")
                val textPaint = android.graphics.Paint().apply {
                    color = labelColor
                    textSize = 28f
                    typeface = android.graphics.Typeface.create("monospace", android.graphics.Typeface.BOLD)
                    isAntiAlias = true
                }
                
                val maxFormatted = String.format(java.util.Locale.US, "%.2f", rawMax)
                val minFormatted = String.format(java.util.Locale.US, "%.2f", rawMin)
                
                drawContext.canvas.nativeCanvas.drawText("YÜKSEK: $maxFormatted", 20f, 35f, textPaint)
                drawContext.canvas.nativeCanvas.drawText("DÜŞÜK: $minFormatted", 20f, height - 15f, textPaint)
            }
        }
    }
}

// ============================================================================
// 3. ADIM: DOPDOLU 2X2 İSTATİSTİK GRID'İ
// ============================================================================
@Composable
fun FormattedDetailStatsGrid(
    details: RichCompanyDetails,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatItemCard(label = "F/K Oranı", value = details.peRatio, modifier = Modifier.weight(1f))
            StatItemCard(label = "Piyasa Değeri", value = details.marketCap, modifier = Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatItemCard(label = "52H En Yüksek", value = details.week52High, modifier = Modifier.weight(1f))
            StatItemCard(label = "52H En Düşük", value = details.week52Low, modifier = Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatItemCard(label = "Ödenmiş Sermaye (Lot)", value = details.volume, modifier = Modifier.weight(1f))
            StatItemCard(label = "Temettü Verimi", value = details.dividendYield, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun FormattedDetailStatsGrid(
    info: CachedCompanyInfo?,
    fallback: RichCompanyDetails,
    numberFormat: String = "TR",
    modifier: Modifier = Modifier
) {
    val df = DecimalFormat("#.##")
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatItemCard(
                label = "F/K Oranı", 
                value = info?.peRatio?.let { NumberFormatter.format(it, numberFormat) } ?: fallback.peRatio, 
                modifier = Modifier.weight(1f)
            )
            StatItemCard(
                label = "Piyasa Değeri", 
                value = info?.marketCap ?: fallback.marketCap, 
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatItemCard(
                label = "52H En Yüksek", 
                value = info?.week52High?.let { NumberFormatter.format(it, numberFormat) } ?: fallback.week52High, 
                modifier = Modifier.weight(1f)
            )
            StatItemCard(
                label = "52H En Düşük", 
                value = info?.week52Low?.let { NumberFormatter.format(it, numberFormat) } ?: fallback.week52Low,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatItemCard(
                label = "Ödenmiş Sermaye (Lot)", 
                value = info?.volume ?: fallback.volume, 
                modifier = Modifier.weight(1f)
            )
            StatItemCard(
                label = "Temettü Verimi", 
                value = info?.dividendYield?.let { "%${NumberFormatter.format(it, numberFormat)}" } ?: fallback.dividendYield, 
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatItemCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = label.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = SubText,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = InkText
            )
        }
    }
}

// ============================================================================
// 4. ADIM: SON HABERLER LİSTESİ TASARIMI
// ============================================================================
@Composable
fun PremiumNewsSection(
    newsList: List<MockNewsItem>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "SON HABERLER",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        newsList.forEach { newsItem ->
            NewsItemCard(newsItem.title, newsItem.source, newsItem.timeAgo)
        }
    }
}

@Composable
fun PremiumNewsSection(
    news: List<NewsItemEntity>,
    fallback: List<MockNewsItem>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "SON HABERLER",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
      
        if (news.isNotEmpty()) {
            news.forEach { item ->
                NewsItemCard(item.title, item.source, "Canlı", item.sentiment)
            }
        } else {
            fallback.forEach { item ->
                NewsItemCard(item.title, item.source, item.timeAgo)
            }
        }
    }
}

@Composable
fun NewsItemCard(title: String, source: String, time: String, sentiment: String? = null) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Habere git */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = InkText,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = source,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryTeal
                    )
                    if (!sentiment.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        val (dotColor, sentimentLabel) = when (sentiment) {
                            "POSITIVE" -> PrimaryTeal to "Olumlu"
                            "NEGATIVE" -> NegatifRed to "Olumsuz"
                            else -> SubText to "Nötr"
                        }
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = sentimentLabel,
                            fontSize = 10.sp,
                            color = dotColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = time,
                    fontSize = 11.sp,
                    color = SubText
                )
            }
        }
    }
}

// ============================================================================
// 6. ADIM: YAPAY ZEKA KARTI (PREMIUM V2)
// ============================================================================
@Composable
fun AiSummaryCard(
    aiText: String?,
    isLoading: Boolean,
    hasKey: Boolean,
    onNavigateToSettings: () -> Unit,
    onGenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (hasKey) PrimaryTeal.copy(alpha = 0.08f) else BackgroundNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (hasKey) PrimaryTeal.copy(alpha = 0.2f) else LineBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(TealSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✨", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Profesör'ün Yorumu",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = InkText
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (!hasKey) {
                Text(
                    "Yapay zeka analizlerini görebilmek için lütfen Ayarlar sayfasından bir Gemini API anahtarı ekleyin.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SubText
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onNavigateToSettings,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Ayarlar'a Git")
                }
            } else if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryTeal, modifier = Modifier.size(24.dp))
                }
            } else if (aiText != null) {
                Text(
                    aiText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkText,
                    lineHeight = 22.sp
                )
            } else {
                Text(
                    "Portföyün hakkında yapay zeka yorumu almak ister misin?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SubText
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onGenerate,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Yorum Al")
                }
            }
        }
    }
}

// ============================================================================
// 7. ADIM: "HAKKINDA" KARTININ METNİNİ BAĞLAYAN PREMIUM BILEŞEN
// ============================================================================
@Composable
fun CompanyAboutCard(
    symbol: String,
    info: CachedCompanyInfo?,
    modifier: Modifier = Modifier
) {
    val fallback = com.nexus.porsuk.data.remote.RichOfflineDataEngine.getRichDetailsFor(symbol)
    val aboutText = info?.about ?: fallback.about

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardNew)
            .border(1.dp, LineBorder, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Text(
            text = "HAKKINDA",
            color = SubText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = aboutText,
            color = InkText,
            fontSize = 13.5.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

// ============================================================================
// 8. ADIM: GELİŞMİŞ HABER DUYARLILIK ANALİZİ (ITEM 2)
// ============================================================================
@Composable
fun NewsSentimentAnalysisCard(
    sentimentText: String?,
    isLoading: Boolean,
    onAnalyze: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("📊", fontSize = 20.sp)
                    Text(
                        "HABER DUYARLILIK ANALİZİ",
                        fontSize = 11.sp,
                        fontFamily = JetBrainsMono,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryTeal,
                        letterSpacing = 1.2.sp
                    )
                }
                
                if (!isLoading && sentimentText == null) {
                    TextButton(onClick = onAnalyze) {
                        Text("Analiz Et", color = PrimaryTeal, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryTeal, modifier = Modifier.size(24.dp))
                }
            } else if (sentimentText != null) {
                Spacer(modifier = Modifier.height(16.dp))
                
                val score = remember(sentimentText) {
                    sentimentText.substringAfter("GENEL_SKOR:").substringBefore("\n").trim().toIntOrNull() ?: 5
                }
                val summary = remember(sentimentText) {
                    sentimentText.substringAfter("ÖZET:").substringBefore("HABERLER:").trim()
                }
                
                val scoreColor = when {
                    score >= 7 -> PrimaryTeal
                    score <= 4 -> NegatifRed
                    else -> Color(0xFFFFA726)
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { score / 10f },
                            modifier = Modifier.size(54.dp),
                            color = scoreColor,
                            trackColor = scoreColor.copy(alpha = 0.1f),
                            strokeWidth = 5.dp,
                            strokeCap = StrokeCap.Round
                        )
                        Text("$score", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = scoreColor, fontFamily = IBMPlexMono)
                    }
                    Text(
                        text = summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkText,
                        lineHeight = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    "Profesör tarafından haber başlıkları analiz edildi.",
                    fontSize = 10.sp,
                    color = SubText,
                    fontFamily = Manrope
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Son haberlerin hisse üzerindeki etkisini ve duyarlılık skorunu yapay zeka ile ölçün.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SubText
                )
            }
        }
    }
}
