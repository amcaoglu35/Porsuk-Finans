package com.nexus.porsuk.ui.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*
import java.util.Locale

data class PiotroskiResult(
    val totalScore: Int, // 0–9
    val rating: String,   // "MÜKEMMEL (8-9)", "ORTA (4-7)", "ZAYIF (0-3)"
    val profitabilityScore: Int, // 0–4
    val leverageScore: Int,      // 0–3
    val efficiencyScore: Int     // 0–2
)

object PiotroskiFScoreCalculator {

    fun calculate(
        roaPositive: Boolean = true,
        cfoPositive: Boolean = true,
        roaDeltaPositive: Boolean = true,
        cfoGreaterThanRoa: Boolean = true,
        leverageDecreased: Boolean = true,
        currentRatioIncreased: Boolean = true,
        noShareDilution: Boolean = true,
        grossMarginIncreased: Boolean = true,
        assetTurnoverIncreased: Boolean = true
    ): PiotroskiResult {
        var prof = 0
        if (roaPositive) prof++
        if (cfoPositive) prof++
        if (roaDeltaPositive) prof++
        if (cfoGreaterThanRoa) prof++

        var lev = 0
        if (leverageDecreased) lev++
        if (currentRatioIncreased) lev++
        if (noShareDilution) lev++

        var eff = 0
        if (grossMarginIncreased) eff++
        if (assetTurnoverIncreased) eff++

        val total = prof + lev + eff
        val rating = when {
            total >= 7 -> "ŞİRKET SAĞLIĞI MÜKEMMEL (7-9/9)"
            total >= 4 -> "DENGELİ ŞİRKET SAĞLIĞI (4-6/9)"
            else       -> "ZAYIF KURUMSAL SAĞLIK (0-3/9)"
        }

        return PiotroskiResult(
            totalScore = total,
            rating = rating,
            profitabilityScore = prof,
            leverageScore = lev,
            efficiencyScore = eff
        )
    }
}

@Composable
fun PiotroskiFScoreCard(
    symbol: String,
    result: PiotroskiResult,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        border = androidx.compose.foundation.BorderStroke(1.dp, LineBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📊", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Piotroski F-Score Karnesi ($symbol)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = InkText,
                        fontFamily = Manrope
                    )
                }
                Box(
                    modifier = Modifier
                        .background(AquaSoft)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .border(1.dp, PrimaryTeal.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                ) {
                    Text(
                        "${result.totalScore}/9 Puan",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryTeal,
                        fontFamily = IBMPlexMono
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PioTile(
                    title = "Kârlılık",
                    value = "${result.profitabilityScore}/4",
                    subtitle = "ROA & Nakit Akışı",
                    valueColor = EmeraldNew,
                    modifier = Modifier.weight(1f)
                )
                PioTile(
                    title = "Kaldıraç & Likidite",
                    value = "${result.leverageScore}/3",
                    subtitle = "Borç & Likidite Oranı",
                    valueColor = PrimaryTeal,
                    modifier = Modifier.weight(1f)
                )
                PioTile(
                    title = "Faaliyet Verimi",
                    value = "${result.efficiencyScore}/2",
                    subtitle = "Marj & Devir Hızı",
                    valueColor = AmberNew,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PioTile(
    title: String,
    value: String,
    subtitle: String,
    valueColor: Color = InkText,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(BackgroundNew)
            .padding(10.dp)
            .border(1.dp, LineBorder, RoundedCornerShape(10.dp))
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.labelSmall, color = SubText, fontFamily = Manrope)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = valueColor, fontFamily = IBMPlexMono)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = SubText.copy(alpha = 0.7f), fontSize = 9.sp, fontFamily = Manrope)
        }
    }
}
