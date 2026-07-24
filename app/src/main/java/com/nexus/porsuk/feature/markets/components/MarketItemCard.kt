package com.nexus.porsuk.feature.markets.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.MarketQuote

private val PositiveGreen = Color(0xFF00C853)
private val NegativeRed = Color(0xFFD50000)

/**
 * Porsuk Markets Module — Piyasa Öğe Kartı (MarketItemCard)
 *
 * Liste (LazyColumn) ve Grid (LazyVerticalGrid) görünümlerinin ikisini de destekler.
 */
@Composable
fun MarketItemCard(
    quote: MarketQuote,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onCardClick: () -> Unit,
    isGridView: Boolean = false,
    modifier: Modifier = Modifier
) {
    val changeColor = when {
        quote.dailyChangePct > 0 -> PositiveGreen
        quote.dailyChangePct < 0 -> NegativeRed
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val changePrefix = if (quote.dailyChangePct > 0) "+" else ""

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        if (isGridView) {
            // Grid Görünümü
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = quote.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    IconButton(
                        onClick = onFavoriteToggle,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Favori",
                            tint = if (isFavorite) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = quote.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${quote.lastPrice} ${quote.currency}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = changeColor.copy(alpha = 0.15f),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "$changePrefix${String.format("%.2f", quote.dailyChangePct)}%",
                        color = changeColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        } else {
            // Liste Görünümü
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sol Bölüm: Favori Yıldızı, Piyasa Durum Noktası, Sembol ve Adı
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onFavoriteToggle,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Favori",
                            tint = if (isFavorite) Color(0xFFFFB300) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Piyasa Açık/Kapalı Göstergesi (Yeşil/Gri Nokta)
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(PositiveGreen)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = quote.symbol,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = quote.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Sağ Bölüm: Son Fiyat, Günlük Yüzde Değişim Kutusu, Hacim
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${quote.lastPrice} ${quote.currency}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = changeColor.copy(alpha = 0.15f),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = "$changePrefix${String.format("%.2f", quote.dailyChangePct)}%",
                            color = changeColor,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    if (quote.volume > 0) {
                        Text(
                            text = "Hacim: ${formatVolume(quote.volume)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatVolume(volume: Long): String {
    return when {
        volume >= 1_000_000_000 -> String.format("%.1fB", volume / 1_000_000_000.0)
        volume >= 1_000_000 -> String.format("%.1fM", volume / 1_000_000.0)
        volume >= 1_000 -> String.format("%.1fK", volume / 1_000.0)
        else -> volume.toString()
    }
}
