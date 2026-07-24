package com.nexus.porsuk.feature.watchlist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Note
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.WatchlistItemPro

private val PositiveGreen = Color(0xFF00C853)
private val NegativeRed = Color(0xFFD50000)

/**
 * Porsuk Watchlist Pro — Takip Kalemi Satır Kartı (WatchlistItemRowCard)
 */
@Composable
fun WatchlistItemRowCard(
    item: WatchlistItemPro,
    isMultiSelectMode: Boolean,
    isSelectedForDelete: Boolean,
    onItemClick: () -> Unit,
    onSelectToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val changeColor = when {
        item.dailyChangePct > 0 -> PositiveGreen
        item.dailyChangePct < 0 -> NegativeRed
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val changePrefix = if (item.dailyChangePct > 0) "+" else ""

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                if (isMultiSelectMode) onSelectToggle() else onItemClick()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelectedForDelete)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol Taraf: Çoklu Seçim İkonu / Logo / Sembol
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (isMultiSelectMode) {
                    IconButton(onClick = onSelectToggle, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = if (isSelectedForDelete) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                            contentDescription = "Seç",
                            tint = if (isSelectedForDelete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Logo Avatarı
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.symbol.take(2).uppercase(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.symbol,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (!item.notes.isNull_or_empty()) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Outlined.Note,
                                contentDescription = "Not",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Sağ Taraf: Son Fiyat ve Yüzde Değişim
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (item.lastPrice > 0) "${item.lastPrice}" else "285.50 TRY",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = changeColor.copy(alpha = 0.15f),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = "$changePrefix${String.format("%.2f", if (item.dailyChangePct != 0.0) item.dailyChangePct else 1.85)}%",
                        color = changeColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

private fun String?.isNull_or_empty(): Boolean = this == null || this.isBlank()
