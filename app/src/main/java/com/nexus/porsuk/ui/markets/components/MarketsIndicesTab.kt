package com.nexus.porsuk.ui.markets.components

import androidx.compose.foundation.BorderStroke
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
import com.nexus.porsuk.ui.common.Sparkline
import com.nexus.porsuk.ui.theme.*

@Composable
fun IndicesTab(onStockClick: (String, String) -> Unit) {
    val indices = remember {
        listOf(
            IndexItem("BIST 100", "🇹🇷 Türkiye", "10.456,87", "^ %1,35", "₺14.2B Hacim", true, listOf(40f, 42f, 45f, 48f, 50f)),
            IndexItem("BIST 30", "🇹🇷 Türkiye", "11.632,15", "^ %1,28", "₺11.8B Hacim", true, listOf(42f, 43f, 46f, 49f, 52f)),
            IndexItem("NASDAQ", "🇺🇸 ABD", "16.832,62", "^ %1,28", "$42.5B Hacim", true, listOf(60f, 62f, 65f, 68f, 70f)),
            IndexItem("S&P 500", "🇺🇸 ABD", "5.325,16", "^ %0,88", "$38.1B Hacim", true, listOf(50f, 52f, 51f, 55f, 58f)),
            IndexItem("DAX 40", "🇩🇪 Almanya", "18.720,40", "v %-0,25", "€6.4B Hacim", false, listOf(188f, 187f, 187.2f)),
            IndexItem("FTSE 100", "🇬🇧 İngiltere", "8.245,10", "^ %0,42", "£4.2B Hacim", true, listOf(81f, 82f, 82.45f)),
            IndexItem("Nikkei 225", "🇯🇵 Japonya", "38.650,00", "^ %0,75", "¥2.8T Hacim", true, listOf(380f, 383f, 386.5f)),
            IndexItem("Hang Seng", "🇭🇰 Hong Kong", "17.920,80", "v %-0,65", "HK$18.5B", false, listOf(181f, 180f, 179.2f))
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(indices, key = { it.name }) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(20.dp)),
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
                        Text(item.volume, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1.0f)) {
                        Text(item.price, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = MaterialTheme.colorScheme.onSurface)
                        Text(item.changePct, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontFamily = IBMPlexMono), color = if (item.isPos) PozitifGreen else NegatifRed)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Sparkline(
                        values = item.sparkValues,
                        color = if (item.isPos) PozitifGreen else NegatifRed,
                        modifier = Modifier.width(65.dp).height(28.dp),
                        filled = true
                    )
                }
            }
        }
    }
}

private data class IndexItem(val name: String, val countryFlag: String, val price: String, val changePct: String, val volume: String, val isPos: Boolean, val sparkValues: List<Float>)
