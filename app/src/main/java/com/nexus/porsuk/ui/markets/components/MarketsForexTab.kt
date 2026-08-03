package com.nexus.porsuk.ui.markets.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.common.CurrencyFormatter
import com.nexus.porsuk.data.local.entity.PriceSnapshot
import com.nexus.porsuk.ui.theme.*

@Composable
fun ForexTab(
    exchangeRates: Map<String, Double>,
    prices: Map<String, PriceSnapshot>
) {
    val pairs = listOf(
        Triple("USD / TRY", "Amerikan Doları", "USD"),
        Triple("EUR / TRY", "Euro", "EUR"),
        Triple("GBP / TRY", "İngiliz Sterlini", "GBP"),
        Triple("CHF / TRY", "İsviçre Frangı", "CHF"),
        Triple("JPY / TRY", "Japon Yeni (100)", "JPY"),
        Triple("CAD / TRY", "Kanada Doları", "CAD"),
        Triple("AUD / TRY", "Avustralya Doları", "AUD")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(pairs, key = { it.first }) { (pairTitle, pairName, code) ->
            val rate = exchangeRates[code]
            val snapshot = prices["${code}TRY"]
            val hasData = rate != null && rate > 0.0

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.2f)) {
                        Text(pairTitle, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = MaterialTheme.colorScheme.onSurface)
                        Text(pairName, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1.0f)) {
                        if (hasData) {
                            Text(CurrencyFormatter.formatTRY(rate!!, "TR"), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = MaterialTheme.colorScheme.onSurface)
                            val changePct = snapshot?.changePercent ?: 0.0
                            val isPos = changePct >= 0.0
                            val changeText = "${if (isPos) "^ %" else "v %"}${String.format("%.2f", changePct)}"
                            Text(changeText, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = if (isPos) PozitifGreen else NegatifRed)
                        } else {
                            Text("Veri Yok", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
