package com.nexus.porsuk.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.nexus.porsuk.MainActivity
import com.nexus.porsuk.data.local.PorsukDatabase
import com.nexus.porsuk.ui.common.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────────────────────────────────────
// PAYLAŞILAN WIDGET RENK PALETİ — Koyu tema, uygulama ile tutarlı
// ─────────────────────────────────────────────────────────────────────────────
object WidgetTheme {
    // Koyu tema
    val Background    = ColorProvider(Color(0xFF0D1B22))  // Koyu lacivert arkaplan
    val Surface       = ColorProvider(Color(0xFF162230))  // Kart yüzeyi
    val SurfaceAlt    = ColorProvider(Color(0xFF1E2E3A))  // Alternatif yüzey
    val BorderLine    = ColorProvider(Color(0xFF243344))  // Ayırıcı çizgi
    val TextPrimary   = ColorProvider(Color(0xFFE8EFF5))  // Açık metin
    val TextMuted     = ColorProvider(Color(0xFF5E7D8F))  // Soluk gri metin
    val Aqua          = ColorProvider(Color(0xFF00B6C9))  // Marka rengi
    val AquaLight     = ColorProvider(Color(0xFF0E2D35))  // Aqua'nın koyu arka planı
    val Green         = ColorProvider(Color(0xFF00A878))  // Artış
    val GreenBg       = ColorProvider(Color(0xFF0C2920))  // Artış arka planı
    val Red           = ColorProvider(Color(0xFFEF4A5F))  // Düşüş
    val RedBg         = ColorProvider(Color(0xFF2E111A))  // Düşüş arka planı
    val Orange        = ColorProvider(Color(0xFFFF8C42))  // Uyarı
}

// ─────────────────────────────────────────────────────────────────────────────
// ANA PORTFÖY WİDGET'I — Toplam değer + kar/zarar TL + sparkline + top moverlar
// ─────────────────────────────────────────────────────────────────────────────
class PorsukWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = PorsukDatabase.getDatabase(context)
        val assetDao = database.assetDao()

        val basketItems = assetDao.getAllBasketItemsDirect()
        val companies   = assetDao.getAllCompaniesDirect()
        val watchlist   = assetDao.getWatchlistDirect()
        val companyMap  = companies.associateBy { it.symbol }

        // Döviz kurları
        val usdRate = companyMap["USDTRY"]?.currentPrice ?: 34.5
        val eurRate = companyMap["EURTRY"]?.currentPrice ?: 37.2

        // Portföy hesaplama
        var totalValueTryVal = 0.0
        var totalCostTryVal  = 0.0
        basketItems.forEach { item ->
            val company = companyMap[item.symbol]
            val market  = company?.market ?: "BIST"
            val rate    = when (market.uppercase()) {
                "NASDAQ", "NYSE"  -> usdRate
                "FRA", "EURONEXT" -> eurRate
                else              -> 1.0
            }
            val currentPrice = company?.currentPrice ?: item.buyPrice
            totalValueTryVal += item.quantity * currentPrice * rate
            totalCostTryVal  += item.quantity * item.buyPrice * rate
        }

        val profitLossVal      = totalValueTryVal - totalCostTryVal
        val portfolioChangePct = if (totalCostTryVal > 0.0)
            ((totalValueTryVal - totalCostTryVal) / totalCostTryVal) * 100.0
        else 0.0

        val totalValueStr  = CurrencyFormatter.formatWithSymbol(totalValueTryVal, "").trim()
        val profitLossStr  = CurrencyFormatter.formatWithSymbol(kotlin.math.abs(profitLossVal), "").trim()
        val changePctStr   = String.format(Locale.US, "%+.2f%%", portfolioChangePct)
        val lastUpdate     = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val isPositive     = portfolioChangePct >= 0.0
        val changeSymbol   = if (isPositive) "▲" else "▼"
        val plSymbol       = if (isPositive) "+" else "-"
        val changeColor    = if (isPositive) WidgetTheme.Green  else WidgetTheme.Red
        val changeBgColor  = if (isPositive) WidgetTheme.GreenBg else WidgetTheme.RedBg

        // Top moverlar (izleme listesinden)
        val watchlistCompanies = watchlist.mapNotNull { companyMap[it.symbol] }
        val calculatedMovers = watchlistCompanies
            .sortedByDescending { kotlin.math.abs(it.changePercent) }
            .take(3)
            .map { company ->
                val pos     = company.changePercent >= 0.0
                val chgText = String.format(Locale.US, "%+.2f%%", company.changePercent)
                MoverModel(company.symbol, chgText, pos)
            }

        val topMovers = calculatedMovers.ifEmpty {
            listOf(
                MoverModel("THYAO", "+2.40%", true),
                MoverModel("AAPL",  "-0.50%", false),
                MoverModel("TSLA",  "+1.80%", true)
            )
        }

        // Sparkline verisi (portföy geçmişinden)
        val historyEntries = assetDao.getPortfolioHistoryDirect()
        val chartHeights   = if (historyEntries.isNotEmpty()) {
            val minVal = historyEntries.minOf { it.totalValue }
            val maxVal = historyEntries.maxOf { it.totalValue }
            val range  = (maxVal - minVal).coerceAtLeast(1.0)
            val base   = historyEntries.map { entry ->
                val ratio = (entry.totalValue - minVal) / range
                (8 + (ratio * 36)).toInt()
            }
            List(16) { index -> base[index % base.size] }
        } else {
            listOf(10, 14, 18, 12, 20, 26, 22, 30, 28, 35, 32, 38, 33, 42, 40, 44)
        }

        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(WidgetTheme.Background)
                    .padding(14.dp)
                    .clickable(actionStartActivity<MainActivity>())
            ) {
                // — ÜSTÜ: Kategori Etiketi + Saat —
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    Box(
                        modifier = GlanceModifier
                            .background(WidgetTheme.AquaLight)
                            .cornerRadius(4.dp)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "PORTFÖY",
                            style = TextStyle(
                                color = WidgetTheme.Aqua,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = GlanceModifier.width(6.dp))
                    Text(
                        "USD: ₺${String.format(Locale.US, "%.2f", usdRate)} | EUR: ₺${String.format(Locale.US, "%.2f", eurRate)}",
                        style = TextStyle(color = WidgetTheme.TextMuted, fontSize = 8.sp)
                    )
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    Row(
                        verticalAlignment = Alignment.Vertical.CenterVertically,
                        modifier = GlanceModifier.clickable(androidx.glance.appwidget.action.actionRunCallback<RefreshPricesAction>())
                    ) {
                        Text(
                            "güncellendi $lastUpdate",
                            style = TextStyle(color = WidgetTheme.TextMuted, fontSize = 8.sp)
                        )
                        Spacer(modifier = GlanceModifier.width(4.dp))
                        Text(
                            "🔄",
                            style = TextStyle(color = WidgetTheme.Aqua, fontSize = 10.sp)
                        )
                    }
                }

                Spacer(modifier = GlanceModifier.height(8.dp))

                // — ORTA: Toplam Değer + Kar/Zarar —
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Vertical.CenterVertically
                ) {
                    Column(modifier = GlanceModifier.defaultWeight()) {
                        Text(
                            text = "₺ $totalValueStr",
                            style = TextStyle(
                                color = WidgetTheme.TextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = GlanceModifier.height(2.dp))
                        Row(verticalAlignment = Alignment.Vertical.CenterVertically) {
                            Box(
                                modifier = GlanceModifier
                                    .background(changeBgColor)
                                    .cornerRadius(4.dp)
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "$changeSymbol $changePctStr",
                                    style = TextStyle(
                                        color = changeColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = GlanceModifier.width(6.dp))
                            Text(
                                text = "$plSymbol₺$profitLossStr K/Z",
                                style = TextStyle(color = WidgetTheme.TextMuted, fontSize = 10.sp)
                            )
                        }
                    }
                    // Logo badge
                    Box(
                        modifier = GlanceModifier
                            .size(30.dp)
                            .background(WidgetTheme.AquaLight)
                            .cornerRadius(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "P",
                            style = TextStyle(
                                color = WidgetTheme.Aqua,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = GlanceModifier.height(10.dp))

                // — SPARKLINE GRAFİĞİ (ince, zarif) —
                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(44.dp),
                    verticalAlignment = Alignment.Vertical.Bottom
                ) {
                    chartHeights.forEach { heightVal ->
                        val barColor = if (isPositive) WidgetTheme.Green else WidgetTheme.Red
                        Box(
                            modifier = GlanceModifier
                                .width(4.dp)
                                .height(heightVal.dp)
                                .background(barColor)
                                .cornerRadius(2.dp)
                        ) {}
                        Spacer(modifier = GlanceModifier.width(4.dp))
                    }
                }

                Spacer(modifier = GlanceModifier.height(10.dp))

                // — EN ÇOK HAREKET EDENLER —
                Row(modifier = GlanceModifier.fillMaxWidth()) {
                    topMovers.forEachIndexed { index, mover ->
                        WidgetMoverChip(mover = mover, modifier = GlanceModifier.defaultWeight())
                        if (index < topMovers.size - 1) {
                            Spacer(modifier = GlanceModifier.width(5.dp))
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Mover chip bileşeni — yeniden kullanılabilir
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun WidgetMoverChip(mover: MoverModel, modifier: GlanceModifier = GlanceModifier) {
    val bgColor  = if (mover.isPositive) WidgetTheme.GreenBg else WidgetTheme.RedBg
    val txtColor = if (mover.isPositive) WidgetTheme.Green   else WidgetTheme.Red
    val arrow    = if (mover.isPositive) "▲" else "▼"

    Box(
        modifier = modifier
            .background(bgColor)
            .cornerRadius(8.dp)
            .padding(vertical = 5.dp, horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.Horizontal.CenterHorizontally) {
            Text(
                text = mover.symbol,
                style = TextStyle(
                    color = WidgetTheme.TextPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = GlanceModifier.height(2.dp))
            Text(
                text = "$arrow ${mover.change}",
                style = TextStyle(
                    color = txtColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

data class MoverModel(
    val symbol: String,
    val change: String,
    val isPositive: Boolean
)
