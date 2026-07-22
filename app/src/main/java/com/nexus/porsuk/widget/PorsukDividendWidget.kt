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

class PorsukDividendWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                DividendWidgetContent()
            }
        }
    }
}

@Composable
fun DividendWidgetContent() {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFF062C24)))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "💰 Temettü & Pasif Gelir",
                style = TextStyle(
                    color = ColorProvider(Color(0xFF2DD4BF)),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = GlanceModifier.defaultWeight())
            Text(
                text = "14 GÜN KALDI",
                style = TextStyle(
                    color = ColorProvider(Color(0xFF34D399)),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = GlanceModifier.height(8.dp))

        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Tahmini Yıllık Gelir",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFA7F3D0)),
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = "₺18.450,00",
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = GlanceModifier.defaultWeight())

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Sıradaki Ödeme",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFA7F3D0)),
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = "TUPRS (%6.8)",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF34D399)),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
