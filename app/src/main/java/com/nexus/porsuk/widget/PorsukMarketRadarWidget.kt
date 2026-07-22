package com.nexus.porsuk.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

class PorsukMarketRadarWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                MarketRadarWidgetContent()
            }
        }
    }
}

@Composable
fun MarketRadarWidgetContent() {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFF0F172A)))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🌐 Canlı Piyasa Radarı",
                style = TextStyle(
                    color = ColorProvider(Color(0xFF2DD4BF)),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = GlanceModifier.defaultWeight())
            Text(
                text = "BIST & FX",
                style = TextStyle(
                    color = ColorProvider(Color(0xFF94A3B8)),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = GlanceModifier.height(8.dp))

        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MarketItemBox("BIST 100", "9.850", "+%1,4", isPositive = true, modifier = GlanceModifier.defaultWeight())
            Spacer(modifier = GlanceModifier.width(6.dp))
            MarketItemBox("USDTRY", "₺33,15", "+%0,1", isPositive = true, modifier = GlanceModifier.defaultWeight())
            Spacer(modifier = GlanceModifier.width(6.dp))
            MarketItemBox("EURTRY", "₺36,10", "-%0,1", isPositive = false, modifier = GlanceModifier.defaultWeight())
        }
    }
}

@Composable
fun MarketItemBox(name: String, priceStr: String, changeStr: String, isPositive: Boolean, modifier: GlanceModifier = GlanceModifier) {
    Column(
        modifier = modifier
            .background(ColorProvider(Color(0xFF1E293B)))
            .cornerRadius(8.dp)
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name,
            style = TextStyle(
                color = ColorProvider(Color(0xFF94A3B8)),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = GlanceModifier.height(2.dp))
        Text(
            text = priceStr,
            style = TextStyle(
                color = ColorProvider(Color.White),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = GlanceModifier.height(2.dp))
        Text(
            text = changeStr,
            style = TextStyle(
                color = ColorProvider(if (isPositive) Color(0xFF34D399) else Color(0xFFFB7185)),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
