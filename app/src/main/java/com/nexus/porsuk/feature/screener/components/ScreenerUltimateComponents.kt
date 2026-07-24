package com.nexus.porsuk.feature.screener.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.ScreenerResultItem
import com.nexus.porsuk.domain.model.SmartFilterPresetCategory

/**
 * 10 Akıllı Filtre Paketi Chip Barı (SmartFilterPresetChips)
 */
@Composable
fun SmartFilterPresetChips(
    selectedPreset: SmartFilterPresetCategory,
    onPresetSelected: (SmartFilterPresetCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollableTabRow(
        selectedTabIndex = selectedPreset.ordinal,
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        divider = {},
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        SmartFilterPresetCategory.entries.forEach { preset ->
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
 * Screener Sonuç Öge Kartı (ScreenerResultRowCard - Altman Z & Piotroski Rozeti ile)
 */
@Composable
fun ScreenerResultRowCard(
    item: ScreenerResultItem,
    modifier: Modifier = Modifier
) {
    val isPositive = item.dailyChangePct >= 0
    val changeColor = if (isPositive) Color(0xFF00C853) else Color(0xFFD50000)
    val changePrefix = if (isPositive) "+" else ""

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
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
                    Text(
                        text = "${item.companyName} • P. Değeri: ${item.marketCapText}",
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

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )

            // Değerleme & Finansal Sağlık Rozetleri (F/K, ROE, Altman Z, Piotroski)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BadgeText("F/K", "${item.peRatio}")
                BadgeText("ROE", "%${item.roePct}")
                BadgeText("Altman Z", "${item.altmanZScore}")
                BadgeText("Piotroski F", "${item.piotroskiFScore}/9")
            }
        }
    }
}

@Composable
private fun BadgeText(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}
