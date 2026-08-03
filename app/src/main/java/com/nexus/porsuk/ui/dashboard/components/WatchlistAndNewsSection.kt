package com.nexus.porsuk.ui.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.data.local.entity.PriceSnapshot
import com.nexus.porsuk.data.local.entity.WatchlistItem
import com.nexus.porsuk.ui.common.CurrencyFormatter
import com.nexus.porsuk.ui.common.Sparkline
import com.nexus.porsuk.ui.theme.*

@Composable
fun DashboardWatchlistSection(
    watchlist: List<WatchlistItem>,
    prices: Map<String, PriceSnapshot>,
    onStockClick: (String, String) -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val outlineColor = MaterialTheme.colorScheme.outline

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        border = BorderStroke(1.dp, outlineColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("⭐ İzleme Listem", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = onSurfaceColor)
            Spacer(modifier = Modifier.height(12.dp))

            if (watchlist.isEmpty()) {
                Text("Henüz izleme listene hisse eklemedin.", style = MaterialTheme.typography.bodySmall, color = onSurfaceVariant)
            } else {
                watchlist.take(5).forEach { item ->
                    val price = prices[item.symbol]?.price ?: 0.0
                    WatchlistRowItem(item = item, price = price, onClick = { onStockClick(item.symbol, "BIST") })
                    HorizontalDivider(color = outlineColor.copy(alpha = 0.4f))
                }
            }
        }
    }
}

@Composable
private fun WatchlistRowItem(
    item: WatchlistItem,
    price: Double,
    onClick: () -> Unit
) {
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    var isFav by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.symbol, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = onSurfaceColor)
            Text("BIST", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = onSurfaceVariant)
        }

        Sparkline(
            values = listOf(40f, 42f, 45f, 48f, 50f),
            color = PozitifGreen,
            modifier = Modifier
                .width(60.dp)
                .height(24.dp),
            filled = true
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text(CurrencyFormatter.formatTRY(price, "TR"), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = IBMPlexMono), color = onSurfaceColor)
            Text("^ %1,45", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 8.5.sp, fontFamily = IBMPlexMono), color = PozitifGreen)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = if (isFav) Icons.Default.Star else Icons.Outlined.StarBorder,
            contentDescription = "Favori",
            tint = if (isFav) Color(0xFFFFB800) else onSurfaceVariant,
            modifier = Modifier
                .size(16.dp)
                .clickable { isFav = !isFav }
        )
    }
}

@Composable
fun DashboardNewsSection() {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val outlineColor = MaterialTheme.colorScheme.outline

    val newsList = remember {
        listOf(
            NewsItem("BIST 100 rekor tazeledi: Bankacılık öncülüğünde yükseliş", "Piyasalar", "Yüksek Olumlu", "%89 Güven", "3 dk okuma"),
            NewsItem("Merkez Bankası faiz kararı metninde enflasyon vurgusu", "Makro", "Nötr Etki", "%92 Güven", "4 dk okuma")
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        border = BorderStroke(1.dp, outlineColor)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text("📰 Son Haberler & AI Etki Analizi", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = onSurfaceColor)
            Spacer(modifier = Modifier.height(12.dp))

            newsList.forEach { item ->
                NewsRowItem(item = item)
                HorizontalDivider(color = outlineColor.copy(alpha = 0.4f))
            }
        }
    }
}

private data class NewsItem(
    val title: String,
    val category: String,
    val impact: String,
    val aiConfidence: String,
    val readTime: String
)

@Composable
private fun NewsRowItem(item: NewsItem) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // News Thumbnail Box
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text("📰", fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontFamily = Manrope), color = onSurfaceColor, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(2.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(item.category, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontWeight = FontWeight.Bold), color = primaryColor)
                Text("•", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp), color = onSurfaceVariant)
                Text(item.impact, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontWeight = FontWeight.Bold), color = PozitifGreen)
                Text("•", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp), color = onSurfaceVariant)
                Text(item.readTime, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp), color = onSurfaceVariant)
            }
        }
    }
}
