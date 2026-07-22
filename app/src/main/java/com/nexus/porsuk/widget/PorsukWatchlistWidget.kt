package com.nexus.porsuk.widget

import android.content.Context
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
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.nexus.porsuk.MainActivity
import com.nexus.porsuk.data.local.PorsukDatabase
import com.nexus.porsuk.ui.common.CurrencyFormatter
import java.util.Locale

// ─────────────────────────────────────────────────────────────────────────────
// İZLEME LİSTESİ WİDGET'I — Max 5 hisse, ok simgeli satırlar, koyu tema
// ─────────────────────────────────────────────────────────────────────────────
class PorsukWatchlistWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = PorsukDatabase.getDatabase(context)
        val assetDao = database.assetDao()

        val watchlist  = assetDao.getWatchlistDirect()
        val companies  = assetDao.getAllCompaniesDirect()
        val companyMap = companies.associateBy { it.symbol }

        // Max 5 hisse göster
        val items = watchlist.mapNotNull { companyMap[it.symbol] }.take(5)

        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(WidgetTheme.Background)
                    .padding(14.dp)
                    .clickable(actionStartActivity<MainActivity>())
            ) {
                // — BAŞLIK SATIRI —
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
                            "İZLEME",
                            style = TextStyle(
                                color = WidgetTheme.Aqua,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    Text(
                        text = "Takip Listesi",
                        style = TextStyle(
                            color = WidgetTheme.TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    Text(
                        text = "${items.size} hisse",
                        style = TextStyle(color = WidgetTheme.TextMuted, fontSize = 9.sp)
                    )
                }

                Spacer(modifier = GlanceModifier.height(10.dp))

                if (items.isEmpty()) {
                    // — BOŞ DURUM —
                    Box(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .defaultWeight()
                            .background(WidgetTheme.Surface)
                            .cornerRadius(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.Horizontal.CenterHorizontally) {
                            Text(
                                "👀",
                                style = TextStyle(fontSize = 20.sp)
                            )
                            Spacer(modifier = GlanceModifier.height(4.dp))
                            Text(
                                "Henüz takip edilen hisse yok.",
                                style = TextStyle(
                                    color = WidgetTheme.TextMuted,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                } else {
                    // — HİSSE SATIR LİSTESİ —
                    Column(
                        modifier = GlanceModifier.fillMaxWidth().defaultWeight()
                    ) {
                        items.forEachIndexed { index, company ->
                            val isPositive  = company.changePercent >= 0.0
                            val changeText  = String.format(Locale.US, "%+.2f%%", company.changePercent)
                            val arrow       = if (isPositive) "▲" else "▼"
                            val changeColor = if (isPositive) WidgetTheme.Green else WidgetTheme.Red
                            val changeBg    = if (isPositive) WidgetTheme.GreenBg else WidgetTheme.RedBg
                            val currency    = CurrencyFormatter.getCurrencySymbol(company.market)

                            Row(
                                modifier = GlanceModifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Vertical.CenterVertically
                            ) {
                                // Sembol
                                Text(
                                    text = company.symbol,
                                    style = TextStyle(
                                        color = WidgetTheme.TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = GlanceModifier.defaultWeight()
                                )

                                // Fiyat
                                Text(
                                    text = "$currency${company.currentPrice}",
                                    style = TextStyle(
                                        color = WidgetTheme.TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )

                                Spacer(modifier = GlanceModifier.width(8.dp))

                                // Değişim badge (ok simgeli)
                                Box(
                                    modifier = GlanceModifier
                                        .background(changeBg)
                                        .cornerRadius(5.dp)
                                        .padding(horizontal = 5.dp, vertical = 2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$arrow $changeText",
                                        style = TextStyle(
                                            color = changeColor,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            // Ayırıcı (son satır hariç)
                            if (index < items.size - 1) {
                                Spacer(
                                    modifier = GlanceModifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(WidgetTheme.BorderLine)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
