package com.nexus.porsuk.feature.globalmarkets.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.ExchangeStatusInfo
import com.nexus.porsuk.domain.model.MarketRegion

/**
 * 8 Bölge Seçim Chip Barı (RegionSelectorChips)
 */
@Composable
fun RegionSelectorChips(
    selectedRegion: MarketRegion,
    onRegionSelected: (MarketRegion) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollableTabRow(
        selectedTabIndex = selectedRegion.ordinal,
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        divider = {},
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        MarketRegion.entries.forEach { region ->
            FilterChip(
                selected = region == selectedRegion,
                onClick = { onRegionSelected(region) },
                label = { Text("${region.iconEmoji} ${region.displayName}") },
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

/**
 * Borsa Açılış/Kapanış Durumu Banner Kartı (MarketStatusBanner)
 */
@Composable
fun MarketStatusBanner(
    statusInfo: ExchangeStatusInfo?,
    modifier: Modifier = Modifier
) {
    val status = statusInfo?.status
    val colorHex = status?.colorHex ?: 0xFF00C853

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(colorHex).copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = statusInfo?.exchangeName ?: "Borsa",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Saatler: ${statusInfo?.openCloseHoursText} • Yerel Saat: ${statusInfo?.localTimeText}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(colorHex).copy(alpha = 0.25f)
            ) {
                Text(
                    text = status?.displayName ?: "Açık",
                    color = Color(colorHex),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
