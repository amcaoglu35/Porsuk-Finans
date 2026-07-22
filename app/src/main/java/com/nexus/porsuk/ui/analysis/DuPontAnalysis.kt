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

data class DuPontBreakdown(
    val netProfitMarginPct: Double,  // Net Kâr / Satışlar (%)
    val assetTurnover: Double,        // Satışlar / Toplam Varlıklar (x)
    val financialLeverage: Double,    // Toplam Varlıklar / Özkaynaklar (x)
    val calculatedRoePct: Double      // DuPont ROE = Margin × Turnover × Leverage (%)
)

object DuPontAnalysis {

    fun calculate(
        netProfit: Double,
        revenue: Double,
        totalAssets: Double,
        equity: Double
    ): DuPontBreakdown {
        val margin = if (revenue > 0) (netProfit / revenue) * 100.0 else 0.0
        val turnover = if (totalAssets > 0) revenue / totalAssets else 0.0
        val leverage = if (equity > 0) totalAssets / equity else 0.0
        val roe = (margin / 100.0) * turnover * leverage * 100.0

        return DuPontBreakdown(
            netProfitMarginPct = margin,
            assetTurnover = turnover,
            financialLeverage = leverage,
            calculatedRoePct = roe
        )
    }
}

@Composable
fun DuPontAnalysisCard(
    symbol: String,
    duPont: DuPontBreakdown,
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
                    Text("🔍", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "DuPont ROE Ayrıştırması ($symbol)",
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
                        "DuPont Model",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryTeal,
                        fontFamily = IBMPlexMono
                    )
                }
            }

            // Formula representation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DuPontFactor(
                    title = "Net Kâr Marjı",
                    value = "%${String.format(Locale.US, "%.1f", duPont.netProfitMarginPct)}",
                    modifier = Modifier.weight(1f)
                )
                Text("×", fontWeight = FontWeight.Bold, color = SubText, modifier = Modifier.padding(horizontal = 4.dp))
                DuPontFactor(
                    title = "Varlık Devir Hızı",
                    value = "${String.format(Locale.US, "%.2f", duPont.assetTurnover)}x",
                    modifier = Modifier.weight(1f)
                )
                Text("×", fontWeight = FontWeight.Bold, color = SubText, modifier = Modifier.padding(horizontal = 4.dp))
                DuPontFactor(
                    title = "Finansal Kaldıraç",
                    value = "${String.format(Locale.US, "%.2f", duPont.financialLeverage)}x",
                    modifier = Modifier.weight(1f)
                )
            }

            // Final calculated ROE result banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundNew)
                    .padding(12.dp)
                    .border(1.dp, LineBorder, RoundedCornerShape(10.dp))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "DuPont Özkaynak Kârlılığı (ROE):",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = InkText,
                        fontFamily = Manrope
                    )
                    Text(
                        "%${String.format(Locale.US, "%.1f", duPont.calculatedRoePct)}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldNew,
                        fontFamily = IBMPlexMono
                    )
                }
            }
        }
    }
}

@Composable
private fun DuPontFactor(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(BackgroundNew)
            .padding(8.dp)
            .border(1.dp, LineBorder, RoundedCornerShape(8.dp))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = SubText, fontSize = 10.sp, fontFamily = Manrope)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = PrimaryTeal, fontFamily = IBMPlexMono)
        }
    }
}
