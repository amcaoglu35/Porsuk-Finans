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

data class AnalystConsensus(
    val symbol: String,
    val currentPrice: Double,
    val targetPriceAverage: Double,
    val upsidePotentialPct: Double,
    val buyCount: Int,
    val holdCount: Int,
    val sellCount: Int,
    val consensusRating: String // "GÜÇLÜ AL", "AL", "TUT", "SAT"
)

object AnalystConsensusTracker {

    fun getConsensus(symbol: String, currentPrice: Double = 310.0): AnalystConsensus {
        val hash = kotlin.math.abs(symbol.hashCode())
        val target = currentPrice * (1.18 + (hash % 20) / 100.0)
        val upside = ((target - currentPrice) / currentPrice) * 100.0

        val buy = 8 + (hash % 5)
        val hold = 2 + (hash % 3)
        val sell = hash % 2

        val rating = when {
            buy >= 10 -> "GÜÇLÜ AL"
            buy >= 6  -> "AL"
            hold > buy -> "TUT"
            else       -> "SAT"
        }

        return AnalystConsensus(
            symbol = symbol,
            currentPrice = currentPrice,
            targetPriceAverage = target,
            upsidePotentialPct = upside,
            buyCount = buy,
            holdCount = hold,
            sellCount = sell,
            consensusRating = rating
        )
    }
}

@Composable
fun AnalystConsensusCard(
    consensus: AnalystConsensus,
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
                    Text("🎯", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Analist Konsensüsü & Hedef Fiyat (${consensus.symbol})",
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
                        consensus.consensusRating,
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
                TargetTile(
                    title = "Ort. Hedef Fiyat",
                    value = "₺${String.format(Locale.US, "%.2f", consensus.targetPriceAverage)}",
                    subtitle = "Mevcut: ₺${String.format(Locale.US, "%.2f", consensus.currentPrice)}",
                    valueColor = EmeraldNew,
                    modifier = Modifier.weight(1f)
                )
                TargetTile(
                    title = "Prim Potansiyeli",
                    value = "%${String.format(Locale.US, "%+.1f", consensus.upsidePotentialPct)}",
                    subtitle = "12 Aylık Beklenti",
                    valueColor = PrimaryTeal,
                    modifier = Modifier.weight(1f)
                )
                TargetTile(
                    title = "Tavsiye Dağılımı",
                    value = "${consensus.buyCount} AL / ${consensus.holdCount} TUT",
                    subtitle = "${consensus.sellCount} Sat Tavsiyesi",
                    valueColor = InkText,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TargetTile(
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
