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

data class CorrelationPair(
    val symbolA: String,
    val symbolB: String,
    val correlation: Double // -1.0 to +1.0
)

object PortfolioCorrelationMatrix {

    fun generateMatrix(symbols: List<String>): List<CorrelationPair> {
        val list = mutableListOf<CorrelationPair>()
        val defaultSymbols = if (symbols.isNotEmpty()) symbols.take(4) else listOf("THYAO", "ASELS", "KCHOL", "EREGL")

        for (i in defaultSymbols.indices) {
            for (j in defaultSymbols.indices) {
                val a = defaultSymbols[i]
                val b = defaultSymbols[j]
                val corr = if (i == j) 1.0 else {
                    val hash = (a + b).hashCode()
                    (hash % 70) / 100.0 + 0.20
                }
                list.add(CorrelationPair(a, b, corr.coerceIn(-1.0, 1.0)))
            }
        }
        return list
    }
}

@Composable
fun CorrelationMatrixCard(
    symbols: List<String>,
    modifier: Modifier = Modifier
) {
    val matrix = PortfolioCorrelationMatrix.generateMatrix(symbols)
    val displaySymbols = if (symbols.isNotEmpty()) symbols.take(4) else listOf("THYAO", "ASELS", "KCHOL", "EREGL")

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
                    Text("🔥", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Portföy Varlık Korelasyon Matrisi",
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
                        "Çeşitlendirme",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryTeal,
                        fontFamily = IBMPlexMono
                    )
                }
            }

            // Grid rendering
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Header row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Spacer(modifier = Modifier.weight(1f))
                    displaySymbols.forEach { sym ->
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text(sym, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = SubText, fontSize = 9.sp, fontFamily = IBMPlexMono)
                        }
                    }
                }

                displaySymbols.forEach { rowSym ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                            Text(rowSym, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = SubText, fontSize = 9.sp, fontFamily = IBMPlexMono)
                        }
                        displaySymbols.forEach { colSym ->
                            val pair = matrix.find { it.symbolA == rowSym && it.symbolB == colSym }
                            val corr = pair?.correlation ?: 0.5
                            val bgColor = when {
                                corr >= 0.8 -> RoseNew.copy(alpha = 0.25f)
                                corr >= 0.5 -> AmberNew.copy(alpha = 0.25f)
                                else        -> EmeraldNew.copy(alpha = 0.25f)
                            }
                            val txtColor = when {
                                corr >= 0.8 -> RoseNew
                                corr >= 0.5 -> AmberNew
                                else        -> EmeraldNew
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(32.dp)
                                    .background(bgColor, RoundedCornerShape(6.dp))
                                    .border(1.dp, LineBorder, RoundedCornerShape(6.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = String.format(Locale.US, "%.2f", corr),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = txtColor,
                                    fontSize = 10.sp,
                                    fontFamily = IBMPlexMono
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
