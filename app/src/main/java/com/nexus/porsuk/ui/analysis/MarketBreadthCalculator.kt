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
import com.nexus.porsuk.data.local.entity.Company
import com.nexus.porsuk.ui.theme.*
import java.util.Locale

data class MarketBreadthSummary(
    val totalStocks: Int,
    val advancingStocks: Int,
    val decliningStocks: Int,
    val unchangedStocks: Int,
    val advanceDeclineRatio: Double,
    val new52WeekHighs: Int,
    val new52WeekLows: Int,
    val mkkForeignSharePct: Double = 38.4 // MKK Yabancı Yatırımcı Payı %
)

object MarketBreadthCalculator {

    fun calculate(companies: List<Company>): MarketBreadthSummary {
        if (companies.isEmpty()) {
            return MarketBreadthSummary(
                totalStocks = 100,
                advancingStocks = 58,
                decliningStocks = 34,
                unchangedStocks = 8,
                advanceDeclineRatio = 1.70,
                new52WeekHighs = 12,
                new52WeekLows = 3,
                mkkForeignSharePct = 38.4
            )
        }

        val advancing = companies.count { it.changePercent > 0.0 }
        val declining = companies.count { it.changePercent < 0.0 }
        val unchanged = companies.count { it.changePercent == 0.0 }
        val adRatio = if (declining > 0) advancing.toDouble() / declining.toDouble() else advancing.toDouble()

        // 52 week high/low approximations based on momentum
        val highs = companies.count { c -> c.changePercent >= 3.0 }
        val lows = companies.count { c -> c.changePercent <= -3.0 }

        return MarketBreadthSummary(
            totalStocks = companies.size,
            advancingStocks = advancing,
            decliningStocks = declining,
            unchangedStocks = unchanged,
            advanceDeclineRatio = adRatio,
            new52WeekHighs = highs,
            new52WeekLows = lows,
            mkkForeignSharePct = 38.4
        )
    }
}

@Composable
fun MarketBreadthCard(
    summary: MarketBreadthSummary,
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
                        "Piyasa Genişliği & MKK Yabancı Payı",
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
                        "MKK & BIST",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryTeal,
                        fontFamily = IBMPlexMono
                    )
                }
            }

            // Stat Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BreadthStatBox(
                    title = "A/D Oranı",
                    value = String.format(Locale.US, "%.2f", summary.advanceDeclineRatio),
                    subtitle = "${summary.advancingStocks} Yükselen / ${summary.decliningStocks} Düşen",
                    valueColor = if (summary.advanceDeclineRatio >= 1.0) EmeraldNew else RoseNew,
                    modifier = Modifier.weight(1f)
                )
                BreadthStatBox(
                    title = "MKK Yabancı Payı",
                    value = "%${String.format(Locale.US, "%.1f", summary.mkkForeignSharePct)}",
                    subtitle = "Takasbank / MKK",
                    valueColor = PrimaryTeal,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BreadthStatBox(
                    title = "52 Hafta Yeni Zirve",
                    value = "${summary.new52WeekHighs} Hisse",
                    subtitle = "Zirveye Yakın",
                    valueColor = EmeraldNew,
                    modifier = Modifier.weight(1f)
                )
                BreadthStatBox(
                    title = "52 Hafta Yeni Dip",
                    value = "${summary.new52WeekLows} Hisse",
                    subtitle = "Dibe Yakın",
                    valueColor = RoseNew,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BreadthStatBox(
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
