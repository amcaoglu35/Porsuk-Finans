package com.nexus.porsuk.ui.fund.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.common.CurrencyFormatter
import com.nexus.porsuk.ui.common.NumberFormatter
import com.nexus.porsuk.ui.common.Sparkline
import com.nexus.porsuk.ui.fund.BasketDetailUiState
import com.nexus.porsuk.ui.theme.*

@Composable
fun SepetDegeriKarti(uiState: BasketDetailUiState, numberFormat: String = "TR") {
    var selectedRange by remember { mutableStateOf("1A") }
    val isProfit = uiState.profitLossPercent >= 0
    val heroGradient = if (isProfit)
        Brush.linearGradient(listOf(Color(0xFF0D3D35), Color(0xFF0A4A40), Color(0xFF07261F)))
    else
        Brush.linearGradient(listOf(Color(0xFF3D1515), Color(0xFF4A1A1A), Color(0xFF260707)))
    val accentColor = if (isProfit) PrimaryTeal else NegatifRed
    val accentSoft = if (isProfit) Color(0x3300C896) else Color(0x33FF4B4B)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(heroGradient)
    ) {
        // Decorative glow orb
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-40).dp)
                .background(
                    Brush.radialGradient(listOf(accentSoft, Color.Transparent)),
                    CircleShape
                )
                .blur(40.dp)
        )

        Column {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                // Label row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(accentColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "SEPET DEĞERİ",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp),
                        color = accentColor,
                        fontFamily = Manrope
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Big value
                Text(
                    CurrencyFormatter.formatWithSymbol(uiState.totalValue, CurrencyFormatter.getCurrencySymbol(uiState.market), numberFormat),
                    style = MaterialTheme.typography.headlineLarge.copy(fontFamily = IBMPlexMono, fontWeight = FontWeight.ExtraBold),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Profit/Loss badge
                val pct = NumberFormatter.formatPercentage(uiState.profitLossPercent, numberFormat)
                val amount = CurrencyFormatter.formatWithSymbol(uiState.profitLossAmount, CurrencyFormatter.getCurrencySymbol(uiState.market), numberFormat)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentSoft)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        if (isProfit) "▲" else "▼",
                        fontSize = 10.sp,
                        color = accentColor,
                        fontFamily = IBMPlexMono
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "$pct  ($amount)",
                        style = MaterialTheme.typography.labelMedium.copy(fontFamily = IBMPlexMono, fontWeight = FontWeight.Bold),
                        color = accentColor
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Stats row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    HeroStatBox(
                        label = "Maliyet",
                        value = CurrencyFormatter.formatWithSymbol(uiState.totalCost, CurrencyFormatter.getCurrencySymbol(uiState.market), numberFormat),
                        modifier = Modifier.weight(1f)
                    )
                    HeroStatBox(
                        label = if (isProfit) "Net Kâr" else "Net Zarar",
                        value = CurrencyFormatter.formatWithSymbol(uiState.profitLossAmount, CurrencyFormatter.getCurrencySymbol(uiState.market), numberFormat),
                        valueColor = accentColor,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Time Range Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x22FFFFFF))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("1H", "1A", "3A", "1Y", "Tümü").forEach { range ->
                        val isSelected = selectedRange == range
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) accentColor.copy(alpha = 0.3f) else Color.Transparent)
                                .clickable { selectedRange = range }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                range,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) accentColor else Color.White.copy(alpha = 0.5f),
                                fontFamily = Manrope
                            )
                        }
                    }
                }
            }

            // Sparkline area
            val profitLossVal = uiState.profitLossPercent
            val sparkPoints = remember(uiState.basketName, profitLossVal) {
                val list = mutableListOf<Float>()
                var current = 50f
                list.add(current)
                val step = (profitLossVal.toFloat() / 8f) * 10f
                for (i in 1..7) {
                    current += step + kotlin.random.Random.nextFloat() * 10f - 5f
                    list.add(current.coerceIn(10f, 90f))
                }
                list
            }
            Sparkline(
                values = sparkPoints,
                color = accentColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                filled = true
            )
        }
    }
}

@Composable
fun HeroStatBox(label: String, value: String, modifier: Modifier = Modifier, valueColor: Color = Color.White) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x22FFFFFF))
            .padding(12.dp)
    ) {
        Text(label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f), fontWeight = FontWeight.Bold, fontFamily = Manrope)
        Text(value, fontSize = 13.sp, color = valueColor, fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono)
    }
}

@Composable
fun StatBox(label: String, value: String, modifier: Modifier = Modifier, valueColor: Color = InkText) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(BackgroundNew)
            .padding(12.dp)
    ) {
        Text(label, fontSize = 10.sp, color = SubText, fontWeight = FontWeight.Bold, fontFamily = Manrope)
        Text(value, fontSize = 14.sp, color = valueColor, fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono)
    }
}
