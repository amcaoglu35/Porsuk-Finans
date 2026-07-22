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

data class FinancialHealthFlags(
    val altmanZScore: Double,          // >2.99 Safe, 1.81-2.99 Grey, <1.81 Distress
    val altmanZone: String,            // "GÜVENLİ BÖLGE", "GRİ BÖLGE", "FİNANSAL RİSK"
    val altmanZoneColorHex: Long,
    val beneishMScore: Double,         // > -1.78 indicates Manipulation Risk
    val isManipulationRiskHigh: Boolean,
    val beneishRating: String          // "TEMİZ BİLANÇO", "MANİPÜLASYON ŞÜPHESİ"
)

object BankruptcyAndManipulationDetector {

    fun analyze(
        workingCapitalToAssets: Double = 0.25,
        retainedEarningsToAssets: Double = 0.35,
        ebitToAssets: Double = 0.18,
        marketCapToTotalLiabilities: Double = 2.1,
        salesToAssets: Double = 1.15,
        dsri: Double = 1.02,
        gmi: Double = 1.01,
        aqi: Double = 0.98,
        sgi: Double = 1.12,
        depi: Double = 1.0,
        sgai: Double = 1.0,
        lvgi: Double = 0.95,
        tata: Double = 0.03
    ): FinancialHealthFlags {
        // Altman Z-Score calculation = 1.2*X1 + 1.4*X2 + 3.3*X3 + 0.6*X4 + 0.999*X5
        val zScore = 1.2 * workingCapitalToAssets +
                1.4 * retainedEarningsToAssets +
                3.3 * ebitToAssets +
                0.6 * marketCapToTotalLiabilities +
                0.999 * salesToAssets

        val (zone, colorHex) = when {
            zScore >= 2.99 -> "GÜVENLİ BÖLGE" to 0xFF00A878
            zScore >= 1.81 -> "GRİ BÖLGE (DİKKAT)" to 0xFFFFB800
            else           -> "FİNANSAL İFLAS RİSKİ" to 0xFFEF4A5F
        }

        // Beneish M-Score calculation
        val mScore = -4.84 + 0.92 * dsri + 0.528 * gmi + 0.404 * aqi + 0.892 * sgi +
                0.115 * depi - 0.172 * sgai + 4.679 * tata - 0.327 * lvgi

        val isHighRisk = mScore > -1.78
        val beneishRating = if (isHighRisk) "MANİPÜLASYON ŞÜPHESİ (KIRMIZI BAYRAK)" else "TEMİZ MUHASEBE VE BİLANÇO"

        return FinancialHealthFlags(
            altmanZScore = zScore,
            altmanZone = zone,
            altmanZoneColorHex = colorHex,
            beneishMScore = mScore,
            isManipulationRiskHigh = isHighRisk,
            beneishRating = beneishRating
        )
    }
}

@Composable
fun BankruptcyAndManipulationCard(
    symbol: String,
    flags: FinancialHealthFlags,
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
                    Text("🛡️", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "İflas Riski & Muhasebe Manipülasyon Tespiti ($symbol)",
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
                        "Z-Score & M-Score",
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
                HealthTile(
                    title = "Altman Z-Skoru",
                    value = String.format(Locale.US, "%.2f", flags.altmanZScore),
                    subtitle = flags.altmanZone,
                    valueColor = Color(flags.altmanZoneColorHex),
                    modifier = Modifier.weight(1f)
                )
                HealthTile(
                    title = "Beneish M-Skoru",
                    value = String.format(Locale.US, "%.2f", flags.beneishMScore),
                    subtitle = flags.beneishRating,
                    valueColor = if (flags.isManipulationRiskHigh) RoseNew else EmeraldNew,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun HealthTile(
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
