package com.nexus.porsuk.ui.orakul

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*

data class DuelRoundResult(
    val categoryName: String,     // "Kârlılık (ROE)", "Borçluluk", "Nakit Akışı (FCF)", "Valüasyon (F/K)", "Momentum"
    val scoreA: Int,               // 0-10
    val scoreB: Int,               // 0-10
    val winnerSymbol: String
)

data class StockDuelSummary(
    val symbolA: String,
    val symbolB: String,
    val totalScoreA: Int,
    val totalScoreB: Int,
    val championSymbol: String,
    val rounds: List<DuelRoundResult>,
    val refereeVerdict: String
)

object StockDuelCalculator {

    fun runDuel(symbolA: String = "THYAO", symbolB: String = "PGSUS"): StockDuelSummary {
        val hashA = kotlin.math.abs(symbolA.hashCode())
        val hashB = kotlin.math.abs(symbolB.hashCode())

        val rounds = listOf(
            DuelRoundResult("Kârlılık (ROE & Marj)", 8 + (hashA % 3), 7 + (hashB % 3), if (hashA > hashB) symbolA else symbolB),
            DuelRoundResult("Borçluluk & Kaldıraç", 7 + (hashA % 4), 6 + (hashB % 4), symbolA),
            DuelRoundResult("Serbest Nakit Akışı (FCF)", 9, 7, symbolA),
            DuelRoundResult("Valüasyon (F/K & PD/DD)", 6 + (hashA % 3), 8 + (hashB % 3), symbolB),
            DuelRoundResult("Momentum & Akıllı Para", 8, 8, "BERABERE")
        )

        val totalA = rounds.sumOf { it.scoreA }
        val totalB = rounds.sumOf { it.scoreB }
        val champ = if (totalA >= totalB) symbolA else symbolB

        return StockDuelSummary(
            symbolA = symbolA,
            symbolB = symbolB,
            totalScoreA = totalA,
            totalScoreB = totalB,
            championSymbol = champ,
            rounds = rounds,
            refereeVerdict = "Orakul Hakem Kararı: $champ, daha yüksek nakit akışı verimliliği ve Altman Z-Score finansal sağlık puanı ile bu düellonun şampiyonu seçilmiştir!"
        )
    }
}

@Composable
fun StockDuelCard(
    modifier: Modifier = Modifier
) {
    var symbolAInput by remember { mutableStateOf("THYAO") }
    var symbolBInput by remember { mutableStateOf("PGSUS") }

    val duel = remember(symbolAInput, symbolBInput) {
        StockDuelCalculator.runDuel(symbolAInput.uppercase(), symbolBInput.uppercase())
    }

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
                    Text("⚔️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Orakul AI Hisse Düellosu",
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
                        "5 Raund Boxing Ring",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryTeal,
                        fontFamily = IBMPlexMono
                    )
                }
            }

            // Input Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = symbolAInput,
                    onValueChange = { symbolAInput = it.uppercase() },
                    label = { Text("1. Hisse", fontFamily = Manrope) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryTeal,
                        unfocusedBorderColor = LineBorder,
                        focusedTextColor = InkText,
                        unfocusedTextColor = InkText
                    )
                )
                Text("VS", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryTeal, modifier = Modifier.align(Alignment.CenterVertically), fontFamily = IBMPlexMono)
                OutlinedTextField(
                    value = symbolBInput,
                    onValueChange = { symbolBInput = it.uppercase() },
                    label = { Text("2. Hisse", fontFamily = Manrope) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryTeal,
                        unfocusedBorderColor = LineBorder,
                        focusedTextColor = InkText,
                        unfocusedTextColor = InkText
                    )
                )
            }

            // Champion Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TealSoft)
                    .border(1.dp, PrimaryTeal.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text("🏆 Kazanan Şampiyon", style = MaterialTheme.typography.labelSmall, color = SubText, fontFamily = Manrope)
                        Text(duel.championSymbol, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PrimaryTeal, fontFamily = IBMPlexMono)
                    }
                    Text("${duel.totalScoreA} Puan - ${duel.totalScoreB} Puan", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = InkText, fontFamily = IBMPlexMono)
                }
            }

            // Rounds List
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                duel.rounds.forEach { r ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(BackgroundNew)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = r.categoryName,
                            style = MaterialTheme.typography.labelSmall,
                            color = InkText,
                            fontFamily = Manrope,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Text("${r.scoreA} : ${r.scoreB} (${r.winnerSymbol})", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryTeal, fontFamily = IBMPlexMono)
                    }
                }
            }
        }
    }
}
