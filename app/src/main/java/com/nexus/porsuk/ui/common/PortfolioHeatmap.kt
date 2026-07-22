package com.nexus.porsuk.ui.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.porsuk.ui.theme.*
import java.util.Locale

data class HeatmapItem(
    val symbol: String,
    val value: Double,
    val changePercent: Double,
    val market: String = "BIST"
)

@Composable
fun PortfolioHeatmap(
    items: List<HeatmapItem>,
    modifier: Modifier = Modifier,
    onAssetClick: (String) -> Unit = {}
) {
    if (items.isEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardNew)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Isı haritası için henüz sepetinizde hisse bulunmuyor.",
                    color = SubText,
                    fontSize = 13.sp
                )
            }
        }
        return
    }

    val totalValue = remember(items) { items.sumOf { it.value }.coerceAtLeast(0.01) }
    val sortedItems = remember(items) { items.sortedByDescending { it.value }.take(6) }
    val chunkedRows = remember(sortedItems) { sortedItems.chunked(3) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardNew),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📊 Portföy Isı Haritası",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = InkText
                    )
                }
                Text(
                    text = "${items.size} Varlık",
                    fontSize = 12.sp,
                    color = SubText,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, LineBorder, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                chunkedRows.forEach { rowItems ->
                    val rowTotalValue = rowItems.sumOf { it.value }.coerceAtLeast(0.01)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        rowItems.forEach { item ->
                            val weight = (item.value / rowTotalValue).toFloat().coerceIn(0.1f, 1.0f)
                            HeatmapTile(
                                item = item,
                                totalPortfolioValue = totalValue,
                                modifier = Modifier
                                    .weight(weight)
                                    .fillMaxHeight(),
                                onClick = { onAssetClick(item.symbol) }
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendBadge(label = "> +3%", color = Color(0xFF059669))
                LegendBadge(label = "0 - 3%", color = Color(0xFF34D399))
                LegendBadge(label = "Nötr", color = Color(0xFF6B7280))
                LegendBadge(label = "0 - -3%", color = Color(0xFFF87171))
                LegendBadge(label = "< -3%", color = Color(0xFFDC2626))
            }
        }
    }
}

@Composable
private fun HeatmapTile(
    item: HeatmapItem,
    totalPortfolioValue: Double,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val pctOfTotal = (item.value / totalPortfolioValue * 100).coerceIn(0.0, 100.0)

    val tileColor = when {
        item.changePercent >= 3.0 -> Color(0xFF059669)
        item.changePercent > 0.0 -> Color(0xFF34D399)
        item.changePercent == 0.0 -> Color(0xFF4B5563)
        item.changePercent > -3.0 -> Color(0xFFF87171)
        else -> Color(0xFFDC2626)
    }

    val animatedColor by animateColorAsState(
        targetValue = tileColor,
        animationSpec = tween(durationMillis = 300),
        label = "TileBgColor"
    )

    val formattedChange = if (item.changePercent >= 0) {
        "+${String.format(Locale.US, "%.1f", item.changePercent)}%"
    } else {
        "${String.format(Locale.US, "%.1f", item.changePercent)}%"
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(animatedColor)
            .clickable { onClick() }
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = item.symbol,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = formattedChange,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.95f),
                maxLines = 1
            )
            Text(
                text = "%${String.format(Locale.US, "%.1f", pctOfTotal)}",
                fontSize = 9.sp,
                color = Color.White.copy(alpha = 0.75f),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun LegendBadge(label: String, color: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = SubText,
            fontWeight = FontWeight.Medium
        )
    }
}
