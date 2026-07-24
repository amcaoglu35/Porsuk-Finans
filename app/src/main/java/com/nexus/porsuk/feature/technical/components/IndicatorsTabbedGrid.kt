package com.nexus.porsuk.feature.technical.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexus.porsuk.domain.model.IndicatorCategory
import com.nexus.porsuk.domain.model.IndicatorValue

/**
 * Porsuk Technical Engine — Kategoriye Göre İndikatör Listesi Izgarası (IndicatorsTabbedGrid)
 */
@Composable
fun IndicatorsTabbedGrid(
    selectedCategory: IndicatorCategory,
    onCategorySelected: (IndicatorCategory) -> Unit,
    indicators: List<IndicatorValue>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ScrollableTabRow(
            selectedTabIndex = selectedCategory.ordinal,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            divider = {},
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            IndicatorCategory.entries.forEach { cat ->
                FilterChip(
                    selected = cat == selectedCategory,
                    onClick = { onCategorySelected(cat) },
                    label = { Text(cat.displayName) },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val filteredList = indicators.filter { it.category == selectedCategory }
        filteredList.forEach { ind ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = ind.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = ind.valueText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(ind.signal.colorHex).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = ind.signal.displayName,
                            color = Color(ind.signal.colorHex),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
