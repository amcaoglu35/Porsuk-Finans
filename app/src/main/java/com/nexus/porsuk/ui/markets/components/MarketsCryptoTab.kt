package com.nexus.porsuk.ui.markets.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.data.local.entity.PriceSnapshot
import com.nexus.porsuk.ui.theme.*

@Composable
fun CryptoTab(
    prices: Map<String, PriceSnapshot>,
    exchangeRates: Map<String, Double>
) {
    val usdRate = exchangeRates["USD"] ?: 34.5
    val cryptos = listOf(
        Triple("Bitcoin", "BTC", "BTC-USD"),
        Triple("Ethereum", "ETH", "ETH-USD"),
        Triple("BNB", "BNB", "BNB-USD"),
        Triple("Solana", "SOL", "SOL-USD"),
        Triple("XRP", "XRP", "XRP-USD"),
        Triple("Avalanche", "AVAX", "AVAX-USD"),
        Triple("Dogecoin", "DOGE", "DOGE-USD")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(cryptos, key = { it.second }) { (name, symbol, ySymbol) ->
            val snapshot = prices[ySymbol]
            val hasData = snapshot != null && snapshot.price > 0.0

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
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1.2f)) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(symbol.take(3), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 10.sp), color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = MaterialTheme.colorScheme.onSurface)
                            if (hasData) {
                                Text("₺${String.format("%.0f", snapshot!!.price * usdRate)}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1.0f)) {
                        if (hasData) {
                            Text("$${String.format("%.2f", snapshot!!.price)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = MaterialTheme.colorScheme.onSurface)
                            val isPos = snapshot.changePercent >= 0.0
                            val changeText = "${if (isPos) "^ %" else "v %"}${String.format("%.2f", snapshot.changePercent)}"
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
