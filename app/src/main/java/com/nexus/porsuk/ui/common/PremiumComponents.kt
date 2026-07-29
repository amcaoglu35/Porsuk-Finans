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
import com.nexus.porsuk.data.remote.RichCompanyDetails
import com.nexus.porsuk.ui.common.NumberFormatter
import com.nexus.porsuk.ui.theme.*
import java.text.DecimalFormat

data class MockNewsItem(
    val title: String,
    val source: String,
    val timeAgo: String
)

// ============================================================================
// 1. ADIM: DETAY EKRANI DÖVİZ KURU FORMATLAMA
// ============================================================================
@Composable
fun FormattedCurrencyEquivalents(
    price: Double,
    market: String,
    exchangeRates: Map<String, Double>
) {
    val usdTry = exchangeRates["USDTRY"] ?: exchangeRates["USD"] ?: 34.15
    val eurTry = exchangeRates["EURTRY"] ?: exchangeRates["EUR"] ?: 36.42
    
    val priceInUsd: Double
    val priceInEur: Double
    val priceInTry: Double
    
    when (market.uppercase()) {
        "NASDAQ", "NYSE" -> {
            priceInUsd = price
            priceInTry = price * usdTry
            priceInEur = priceInTry / eurTry
        }
        "BIST", "IST" -> {
            priceInTry = price
            priceInUsd = price / usdTry
            priceInEur = price / eurTry
        }
        else -> {
            priceInEur = price
            priceInTry = price * eurTry
            priceInUsd = priceInTry / usdTry
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(TealSoft.copy(alpha = 0.5f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CurrencyItem("₺", String.format(java.util.Locale.US, "%,.2f", priceInTry))
        VerticalDivider(modifier = Modifier.height(24.dp).width(1.dp), color = LineBorder)
        CurrencyItem("$", String.format(java.util.Locale.US, "%,.2f", priceInUsd))
        VerticalDivider(modifier = Modifier.height(24.dp).width(1.dp), color = LineBorder)
        CurrencyItem("€", String.format(java.util.Locale.US, "%,.2f", priceInEur))
    }
}

@Composable
private fun CurrencyItem(symbol: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(symbol, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = InkText, fontFamily = IBMPlexMono)
    }
}

// ============================================================================
// 2. ADIM: ANALİZ KARTLARI (LİKİDİTE, VOLATİLİTE, TREND)
// ============================================================================
@Composable
fun MetricAnalysisGrid(
    peRatio: Double?,
    dividendYield: Double?,
    marketCap: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AnalysisMetricCard(
            title = "Değerleme",
            value = if (peRatio != null) "F/K: $peRatio" else "N/A",
            status = if (peRatio != null && peRatio < 15) "İskontolu" else "Normal",
            color = if (peRatio != null && peRatio < 15) EmeraldNew else Violet,
            modifier = Modifier.weight(1f)
        )
        AnalysisMetricCard(
            title = "Temettü",
            value = if (dividendYield != null) "%$dividendYield" else "%0.0",
            status = if (dividendYield != null && dividendYield > 5) "Yüksek Verim" else "Büyüme",
            color = if (dividendYield != null && dividendYield > 5) PrimaryTeal else Gold,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun AnalysisMetricCard(
    title: String,
    value: String,
    status: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 10.sp, color = SubText, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = InkText)
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = status,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

// ============================================================================
// 3. ADIM: GELİŞMİŞ TREND VE HACİM GÖSTERGESİ (CUSTOM CANVAS)
// ============================================================================
@Composable
fun TrendMomentumIndicator(
    changePercent: Double,
    volume: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Trend & Momentum", fontSize = 11.sp, color = SubText, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (changePercent >= 0) "Yükseliş Trendi" else "Düşüş Trendi",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkText
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (changePercent >= 0) PositiveGreen else NegatifRed)
                    )
                }
                Text("Hacim: ${volume ?: "N/A"}", fontSize = 10.sp, color = SubText)
            }
            
            // Mini Trend Canvas
            Canvas(modifier = Modifier.size(width = 80.dp, height = 40.dp)) {
                val path = Path()
                path.moveTo(0f, size.height * 0.8f)
                if (changePercent >= 0) {
                    path.quadraticTo(size.width * 0.4f, size.height * 0.7f, size.width * 0.6f, size.height * 0.3f)
                    path.lineTo(size.width, 0f)
                } else {
                    path.quadraticTo(size.width * 0.4f, size.height * 0.3f, size.width * 0.6f, size.height * 0.7f)
                    path.lineTo(size.width, size.height)
                }
                
                drawPath(
                    path = path,
                    color = if (changePercent >= 0) PositiveGreen else NegatifRed,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }
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
    fallback: List<NewsItemEntity>,
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
      
        val itemsToDisplay = if (news.isNotEmpty()) news else fallback
        itemsToDisplay.forEach { item ->
            NewsItemCard(item.title, item.source, "Canlı", item.sentiment)
        }
    }
}

@Composable
fun NewsItemCard(
    title: String,
    source: String,
    time: String,
    sentiment: String? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(source, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("•", fontSize = 9.sp, color = SubText)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(time, fontSize = 9.sp, color = SubText)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = InkText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
            }
            
            if (sentiment != null) {
                SentimentDot(sentiment)
            } else {
                Icon(Icons.Default.ChevronRight, null, tint = LineBorder, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun SentimentDot(sentiment: String) {
    val color = when (sentiment.uppercase()) {
        "POSITIVE" -> EmeraldNew
        "NEGATIVE" -> NegatifRed
        else -> Gold
    }
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color)
    )
}

// ============================================================================
// 5. ADIM: ANALİTİK ÖZET (ŞİRKET KARNESİ)
// ============================================================================
@Composable
fun CompanyScorecard(
    symbol: String,
    peRatio: Double?,
    yield: Double?
) {
    val score = calculateMockScore(peRatio, yield)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryTeal)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("ORAKUL ANALİTİK SKOR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f))
                Spacer(modifier = Modifier.height(4.dp))
                Text("$symbol Karnesi", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            }
            
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { score / 100f },
                    modifier = Modifier.size(56.dp),
                    color = Color.White,
                    strokeWidth = 6.dp,
                    trackColor = Color.White.copy(alpha = 0.2f),
                )
                Text(score.toString(), fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun FormattedDetailStatsGrid(
    info: CachedCompanyInfo?,
    fallback: RichCompanyDetails?,
    formatType: String = "TR"
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AnalysisMetricCard("F/K Oranı", info?.peRatio?.toString() ?: fallback?.peRatio ?: "-", "Değerleme", PrimaryTeal, Modifier.weight(1f))
            AnalysisMetricCard("Temettü Verimi", "%${info?.dividendYield ?: fallback?.dividendYield ?: "-"}", "Verim", Gold, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AnalysisMetricCard("Piyasa Değeri", info?.marketCap ?: fallback?.marketCap ?: "-", "Büyüklük", Violet, Modifier.weight(1f))
            AnalysisMetricCard("Hacim", info?.volume ?: fallback?.volume ?: "-", "Likidite", AquaNew, Modifier.weight(1f))
        }
    }
}

@Composable
fun PremiumLiveCanvasChart(
    prices: List<Double> = emptyList(),
    pricePoints: List<Float> = emptyList(),
    color: Color = PrimaryTeal,
    isGlassStyle: Boolean = false,
    modifier: Modifier = Modifier
) {
    val data = if (prices.isNotEmpty()) prices.map { it.toFloat() } else pricePoints
    Canvas(modifier = modifier.fillMaxSize()) {
        if (data.size < 2) return@Canvas
        val max = data.maxOrNull() ?: 1f
        val min = data.minOrNull() ?: 0f
        val range = (max - min).coerceAtLeast(0.1f)
        
        val path = Path()
        data.forEachIndexed { index, price ->
            val x = (index.toFloat() / (data.size - 1)) * size.width
            val y = (1f - ((price - min) / range).toFloat()) * size.height
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, color, style = Stroke(width = 2.dp.toPx()))
    }
}

@Composable
fun CompanyAboutCard(
    symbol: String,
    info: CachedCompanyInfo?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Şirket Hakkında", fontWeight = FontWeight.Bold, color = InkText)
            Spacer(modifier = Modifier.height(8.dp))
            Text(info?.about ?: "Şirket bilgisi yükleniyor...", fontSize = 12.sp, color = SubText)
        }
    }
}

private fun calculateMockScore(pe: Double?, yield: Double?): Int {
    var base = 70
    if (pe != null && pe < 10) base += 15
    if (yield != null && yield > 4) base += 10
    return base.coerceAtMost(98)
}
