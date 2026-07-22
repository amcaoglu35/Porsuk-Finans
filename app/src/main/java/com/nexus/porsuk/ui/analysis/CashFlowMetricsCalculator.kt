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

data class CashFlowAnalysisSummary(
    val operatingCashFlowMillion: Double,
    val capExMillion: Double,
    val freeCashFlowMillion: Double,
    val fcfYieldPct: Double,
    val profitQualityScore: Int,      // 0–100 (Operational cash flow / Net Profit quality)
    val qualityRating: String          // "Mükemmel", "İyi", "Zayıf"
)

object CashFlowMetricsCalculator {

    fun calculate(
        netProfitMillion: Double,
        operatingCashFlowMillion: Double,
        capExMillion: Double,
        marketCapMillion: Double
    ): CashFlowAnalysisSummary {
        val fcf = operatingCashFlowMillion - capExMillion
        val fcfYield = if (marketCapMillion > 0) (fcf / marketCapMillion) * 100.0 else 0.0
        
        val qualityRatio = if (netProfitMillion > 0) (operatingCashFlowMillion / netProfitMillion) else 1.0
        val qualityScore = (qualityRatio * 75).coerceIn(10.0, 100.0).toInt()
        val rating = when {
            qualityScore >= 75 -> "Mükemmel Nakit Kalitesi"
            qualityScore >= 50 -> "İyi Nakit Akışı"
            else               -> "Düşük Nakit Kalitesi"
        }

        return CashFlowAnalysisSummary(
            operatingCashFlowMillion = operatingCashFlowMillion,
            capExMillion = capExMillion,
            freeCashFlowMillion = fcf,
            fcfYieldPct = fcfYield,
            profitQualityScore = qualityScore,
            qualityRating = rating
        )
    }
}

@Composable
fun CashFlowMetricsCard(
    summary: CashFlowAnalysisSummary,
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("💸", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Nakit Akışı & FCF Kalite Analizi",
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
                        "FCF & Skoru",
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
                CFStatBox(
                    title = "Serbest Nakit (FCF)",
                    value = "₺${String.format(Locale.US, "%.1f", summary.freeCashFlowMillion)}M",
                    subtitle = "İşletme - CapEx",
                    valueColor = EmeraldNew,
                    modifier = Modifier.weight(1f)
                )
                CFStatBox(
                    title = "FCF Verimi",
                    value = "%${String.format(Locale.US, "%.1f", summary.fcfYieldPct)}",
                    subtitle = "Piyasa Değeri Oranı",
                    valueColor = PrimaryTeal,
                    modifier = Modifier.weight(1f)
                )
                CFStatBox(
                    title = "Kâr Kalite Skoru",
                    value = "${summary.profitQualityScore}/100",
                    subtitle = summary.qualityRating,
                    valueColor = if (summary.profitQualityScore >= 70) EmeraldNew else AmberNew,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CFStatBox(
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
