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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.data.local.entity.PriceSnapshot
import com.nexus.porsuk.ui.theme.*

@Composable
fun EtfTab(
    prices: Map<String, PriceSnapshot>
) {
    val etfs = listOf(
        Triple("SPY", "SPDR S&P 500 ETF", "SPY"),
        Triple("QQQ", "Invesco QQQ Trust", "QQQ"),
        Triple("GLD", "SPDR Gold Shares", "GLD"),
        Triple("VOO", "Vanguard S&P 500 ETF", "VOO"),
        Triple("TLT", "iShares 20+ Year Treasury", "TLT"),
        Triple("IWM", "iShares Russell 2000 ETF", "IWM")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(etfs, key = { it.first }) { (symbol, name, ySymbol) ->
            val snapshot = prices[ySymbol]

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
                        Text(symbol, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = MaterialTheme.colorScheme.onSurface)
                        Text(name, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp), color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }

                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1.0f)) {
                        if (snapshot != null) {
                            Text("$${String.format("%.2f", snapshot.price)}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = MaterialTheme.colorScheme.onSurface)
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
