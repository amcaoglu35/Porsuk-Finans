package com.nexus.porsuk.ui.markets.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.data.local.entity.PriceSnapshot
import com.nexus.porsuk.ui.common.CurrencyFormatter
import com.nexus.porsuk.ui.common.PercentFormatter
import com.nexus.porsuk.ui.common.Sparkline
import com.nexus.porsuk.ui.theme.*

@Composable
fun IndicesTab(
    prices: Map<String, PriceSnapshot> = emptyMap(),
    onStockClick: (String, String) -> Unit
) {
    val indexConfigs = listOf(
        IndexConfig("BIST 100", "BIST100", "🇹🇷 Türkiye", "₺14.2B Hacim", "10.456,87", 1.35),
        IndexConfig("BIST 30", "BIST30", "🇹🇷 Türkiye", "₺11.8B Hacim", "11.632,15", 1.28),
        IndexConfig("NASDAQ", "QQQ", "🇺🇸 ABD", "$42.5B Hacim", "16.832,62", 1.28),
        IndexConfig("S&P 500", "SPY", "🇺🇸 ABD", "$38.1B Hacim", "5.325,16", 0.88),
        IndexConfig("DAX 40", "DAX", "🇩🇪 Almanya", "€6.4B Hacim", "18.720,40", -0.25),
        IndexConfig("FTSE 100", "FTSE", "🇬🇧 İngiltere", "£4.2B Hacim", "8.245,10", 0.42),
        IndexConfig("Nikkei 225", "N225", "🇯🇵 Japonya", "¥2.8T Hacim", "38.650,00", 0.75),
        IndexConfig("Hang Seng", "HSI", "🇭🇰 Hong Kong", "HK$18.5B", "17.920,80", -0.65)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(indexConfigs, key = { it.name }) { item ->
            val snapshot = prices[item.symbol] ?: prices[item.name.replace(" ", "")]
            val priceStr = if (snapshot != null && snapshot.price > 0.0) CurrencyFormatter.formatTRY(snapshot.price, "TR") else item.defaultPrice
            val changeVal = snapshot?.changePercent ?: item.defaultChange
            val (changeText, isPos) = PercentFormatter.formatChangePercent(changeVal)

            val sparkValues = remember(isPos) {
                if (isPos) listOf(40f, 42f, 45f, 48f, 50f) else listOf(50f, 48f, 45f, 42f, 40f)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(20.dp))
                    .clickable { onStockClick(item.symbol, "INDEX") },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.2f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(item.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, fontFamily = Manrope), color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(item.countryFlag, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(item.volumeInfo, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1.0f)) {
                        Text(priceStr, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = MaterialTheme.colorScheme.onSurface)
                        Text(changeText, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = if (isPos) PozitifGreen else NegatifRed)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Sparkline(
                        values = sparkValues,
                        color = if (isPos) PozitifGreen else NegatifRed,
                        modifier = Modifier.width(65.dp).height(28.dp),
                        filled = true
                    )
                }
            }
        }
    }
}

private data class IndexConfig(
    val name: String,
    val symbol: String,
    val countryFlag: String,
    val volumeInfo: String,
    val defaultPrice: String,
    val defaultChange: Double
)
