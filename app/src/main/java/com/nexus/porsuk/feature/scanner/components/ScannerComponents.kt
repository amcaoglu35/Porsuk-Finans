package com.nexus.porsuk.feature.scanner.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.ScanPresetCategory
import com.nexus.porsuk.domain.model.ScanResultItem

/**
 * 11 Hazır Tarama Stratejisi Yatay Chip Barı (PresetScanChipsBar)
 */
@Composable
fun PresetScanChipsBar(
    selectedPreset: ScanPresetCategory,
    onPresetSelected: (ScanPresetCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollableTabRow(
        selectedTabIndex = selectedPreset.ordinal,
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        divider = {},
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        ScanPresetCategory.entries.forEach { preset ->
            FilterChip(
                selected = preset == selectedPreset,
                onClick = { onPresetSelected(preset) },
                label = { Text("${preset.iconEmoji} ${preset.displayName}") },
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

/**
 * Tarama Sonuç Öge Kartı (ScanResultItemRow)
 */
@Composable
fun ScanResultItemRow(
    item: ScanResultItem,
    modifier: Modifier = Modifier
) {
    val isPositive = item.dailyChangePct >= 0
    val changeColor = if (isPositive) Color(0xFF00C853) else Color(0xFFD50000)
    val changePrefix = if (isPositive) "+" else ""

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "${item.masterScore} Skor",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${item.companyName} • Hacim: ${item.volumeText}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${item.lastPrice}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$changePrefix${item.dailyChangePct}%",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = changeColor
                )
            }
        }
    }
}
